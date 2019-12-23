package fhq.demo.vo;


import lombok.Getter;
import lombok.Setter;

/**
 * @author fhq
 * @date 2019/12/20 10:50
 */
@Setter
@Getter
public class CodeMsg {
    public static final CodeMsg ERROR_USER_NOT_FOUND = new CodeMsg(404, "没有找到该用户：%s");
    public static final CodeMsg ERROR_EMAIL_NOT_ACTIVE = new CodeMsg(403, "该邮箱没有激活");
    public static final CodeMsg ERROR_EMPTY_TOKEN = new CodeMsg(400, "token为空");
    public static final CodeMsg ERROR_INVALID_TOKEN = new CodeMsg(400, "无效token：%s");
    public static final CodeMsg ERROR_INVALID_EMAIL = new CodeMsg(400, "无效有效：%s");
    public static final CodeMsg ERROR_FAIL_TO_ACTIVATE_ACCOUNT = new CodeMsg(400, "激活账户失败：%s");
    public static final CodeMsg ERROR_FAIL_TO_REGISTER = new CodeMsg(400, "注册账户失败：%s");
    public static final CodeMsg ERROR_USER_PASSWORD_NOT_MATCH = new CodeMsg(403, "用户名和密码不匹配");
    public static final CodeMsg ERROR_FAIL_TO_SEND_ACTIVE_EMAIL = new CodeMsg(400, "发送激活邮件失败");

    private CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private int code;
    private String msg;

    public static final CodeMsg SUCCESS = new CodeMsg(200, "操作成功");

    public CodeMsg fillArgs(String args) {
        String message = String.format(this.getMsg(), args);
        return new CodeMsg(getCode(), message);
    }
}
