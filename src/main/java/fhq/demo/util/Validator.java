package fhq.demo.util;

import com.alibaba.druid.util.StringUtils;

import java.util.regex.Pattern;

/**
 * @author fhq
 * @date 2019/12/22 17:00
 */
public class Validator {
    /**
     * 验证邮箱正则对象
     */
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$");


    /**
     * @param email 待验证的字符串
     * @return 是否符合邮箱格式
     */
    public static boolean isValidEmail(String email) {
        if (StringUtils.isEmpty(email)) {
            return false;
        }

        return EMAIL_PATTERN.matcher(email).matches();
    }
}
