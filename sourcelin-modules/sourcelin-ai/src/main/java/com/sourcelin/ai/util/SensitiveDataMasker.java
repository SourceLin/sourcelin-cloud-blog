package com.sourcelin.ai.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 敏感数据脱敏工具
 */
public class SensitiveDataMasker {

    // 手机号正则
    private static final Pattern PHONE_PATTERN = Pattern.compile("(1[3-9]\\d)\\d{4}(\\d{4})");

    // 身份证号正则
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("(\\d{6})\\d{8}(\\d{3}[0-9Xx])");

    // 邮箱正则
    private static final Pattern EMAIL_PATTERN = Pattern.compile("([\\w.-]+)@([\\w.-]+\\.[a-zA-Z]{2,})");

    // 银行卡号正则
    private static final Pattern BANK_CARD_PATTERN = Pattern.compile("(\\d{4})\\d+(\\d{4})");

    // API Key正则
    private static final Pattern API_KEY_PATTERN = Pattern.compile("(sk-[a-zA-Z0-9]{4})[a-zA-Z0-9]+([a-zA-Z0-9]{4})");

    // 中文姓名正则
    private static final Pattern CHINESE_NAME_PATTERN = Pattern.compile("([\\u4e00-\\u9fa5])[\\u4e00-\\u9fa5]+");

    /**
     * 脱敏手机号
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return phone;
        }
        Matcher matcher = PHONE_PATTERN.matcher(phone);
        return matcher.replaceAll("$1****$2");
    }

    /**
     * 脱敏身份证号
     */
    public static String maskIdCard(String idCard) {
        if (idCard == null || idCard.isEmpty()) {
            return idCard;
        }
        Matcher matcher = ID_CARD_PATTERN.matcher(idCard);
        return matcher.replaceAll("$1********$2");
    }

    /**
     * 脱敏邮箱
     */
    public static String maskEmail(String email) {
        if (email == null || email.isEmpty()) {
            return email;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String username = matcher.group(1);
            String domain = matcher.group(2);
            String maskedUsername = username.length() > 2 ?
                username.substring(0, 2) + "***" : username.substring(0, 1) + "***";
            matcher.appendReplacement(sb, maskedUsername + "@" + domain);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 脱敏银行卡号
     */
    public static String maskBankCard(String bankCard) {
        if (bankCard == null || bankCard.isEmpty()) {
            return bankCard;
        }
        Matcher matcher = BANK_CARD_PATTERN.matcher(bankCard);
        return matcher.replaceAll("$1****$2");
    }

    /**
     * 脱敏API Key
     */
    public static String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) {
            return apiKey;
        }
        Matcher matcher = API_KEY_PATTERN.matcher(apiKey);
        if (matcher.find()) {
            return matcher.replaceAll("$1****$2");
        }
        // 如果不是标准格式，脱敏中间部分
        if (apiKey.length() > 8) {
            return apiKey.substring(0, 4) + "****" + apiKey.substring(apiKey.length() - 4);
        }
        return "****";
    }

    /**
     * 脱敏中文姓名
     */
    public static String maskChineseName(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        Matcher matcher = CHINESE_NAME_PATTERN.matcher(name);
        return matcher.replaceAll("$1**");
    }

    /**
     * 通用文本脱敏（移除可能的敏感信息）
     */
    public static String maskText(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        String result = text;
        result = maskPhone(result);
        result = maskIdCard(result);
        result = maskEmail(result);
        result = maskBankCard(result);
        result = maskApiKey(result);

        return result;
    }

    /**
     * 截断过长的文本
     */
    public static String truncate(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
    }
}