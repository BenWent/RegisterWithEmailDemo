package fhq.demo.controller;

import fhq.demo.bean.User;
import fhq.demo.exception.UserException;
import fhq.demo.redis.RedisService;
import fhq.demo.service.UserService;
import fhq.demo.util.JwtUtil;
import fhq.demo.util.Validator;
import fhq.demo.vo.CodeMsg;
import fhq.demo.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.validation.Valid;

/**
 * @author fhq
 * @date 2019/12/20 10:49
 */
@RestController
public class UserController {
    private UserService userService;
    private JavaMailSender mailSender;
    private RedisService redisService;

    @Value("${spring.mail.username}")
    private String senderEmailAddress;

    @Autowired
    public UserController(UserService userService, JavaMailSender mailSender, RedisService redisService) {
        this.userService = userService;
        this.mailSender = mailSender;
        this.redisService = redisService;
    }

    @PostMapping("/login")
    public Result login(@Valid User user) {
        User userInDb = userService.findUserByEmail(user.getEmail());

        if (userInDb == null) {
            return Result.error(CodeMsg.ERROR_USER_NOT_FOUND.fillArgs(user.getEmail()));
        }
        if (!userInDb.isEmailActive()) {
            return Result.error(CodeMsg.ERROR_EMAIL_NOT_ACTIVE);
        }

        if (!userInDb.getPassword().equals(user.getPassword())) {
            return Result.error(CodeMsg.ERROR_USER_PASSWORD_NOT_MATCH);
        }

        return Result.success("login");
    }

    @PostMapping("/register")
    public Result register(@Valid User user) {
        try {
            userService.addUser(user);
        } catch (UserException e) {
            return Result.error(CodeMsg.ERROR_FAIL_TO_REGISTER.fillArgs(e.getMessage()));
        }

        // 发送激活邮件
        String activateEmailToken = JwtUtil.generateToken(user);
        // 将 user信息 存入redis缓存
        redisService.set(activateEmailToken, user);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setTo(user.getEmail());
            helper.setSubject("XX系统激活邮件");
            helper.setFrom(senderEmailAddress);
            helper.setText(email(activateEmailToken), true);
        } catch (MessagingException e) {
            return Result.error(CodeMsg.ERROR_FAIL_TO_SEND_ACTIVE_EMAIL);
        }

        mailSender.send(message);

        return Result.success("register");
    }

    @GetMapping("/activate/{token}")
    public Result activeEmail(@PathVariable("token") String token) {

        if (StringUtils.isEmpty(token)) {
            return Result.error(CodeMsg.ERROR_EMPTY_TOKEN);
        }

        String email = JwtUtil.getEmailFromToken(token);
        if (StringUtils.isEmpty(email)) {
            return Result.error(CodeMsg.ERROR_INVALID_TOKEN.fillArgs(token));
        } else if (!Validator.isValidEmail(email)) {
            return Result.error(CodeMsg.ERROR_INVALID_EMAIL.fillArgs(email));
        }

        // token 过期了 —— 这里其实可以根据jwt本身来验证token是否过期了，引入redis的原因在于可以从redis冲获得User对象，
        // 而不需要查询mysql数据库
        User userInCache = redisService.get(token, User.class);
        if (userInCache == null) {
            return Result.error(CodeMsg.ERROR_INVALID_TOKEN.fillArgs("token is expired"));
        }

        try {
            userService.activateEmail(userInCache);
        } catch (UserException e) {
            return Result.error(CodeMsg.ERROR_FAIL_TO_ACTIVATE_ACCOUNT.fillArgs(e.getMessage()));
        }

        return Result.success("activate");
    }

    /**
     * @param token 此次注册生成的token
     * @return 发送给注册用户邮箱的内容
     */
    private String email(String token) {
        if (StringUtils.isEmpty(token)) {
            throw new RuntimeException("token is null");
        }

        return String.format("<a href=\"http://localhost:8080/activate/%s\">点击激活邮箱</a>", token);
    }
}
