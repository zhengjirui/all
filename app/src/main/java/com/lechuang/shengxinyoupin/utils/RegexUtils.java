package com.lechuang.shengxinyoupin.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @author: LGH
 * @since: 2017/12/23
 * @describe:
 */

public class RegexUtils {

    /**
     * 描述：是否是邮箱.
     *
     * @param str 指定的字符串
     * @return 是否是邮箱:是为true，否则false
     */
    public static Boolean isEmail(String str) {
        Boolean isEmail = false;
        String expr = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})$";

        if (str.matches(expr)) {
            isEmail = true;
        }
        return isEmail;
    }

    /**
     * 判断是否是手机号
     *
     * @param phone
     * @return
     */
    public static boolean checkPhone(String phone) {
        return Utils.isTelNumber(phone);
//        Pattern pattern = Pattern
//                .compile("^((13[0-9])|(15[^4])|(18[0-9])|(17[0-8])|(19[0-9])|(147,145))\\d{8}$");
//        Matcher matcher = pattern.matcher(phone);
//
//        if (matcher.matches()) {
//            return true;
//        }
//        return false;
    }

    /**
     * 只允许字母、数字和汉字
     *
     * @param str
     * @return
     * @throws PatternSyntaxException
     */
    public static String inputFilter(String str) throws PatternSyntaxException {
        // 只允许字母、数字和汉字
        String regEx = "[^a-zA-Z0-9\u4E00-\u9FA5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }
}
