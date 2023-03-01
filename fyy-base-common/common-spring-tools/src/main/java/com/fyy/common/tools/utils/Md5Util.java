package com.fyy.common.tools.utils;

import com.fyy.common.tools.exception.RenException;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author fuyouyi
 * @since 2020/10/19
 */
public class Md5Util {

    private static final String CHARSET = "UTF-8";

    /**
     * MD5加密
     *
     * @param str 内容
     */
    public static String MD5(String str) {
        try {
            return MD5(str, CHARSET);
        } catch (Exception e) {
            throw new RenException("MD5加密失败", e);
        }
    }

    /**
     * MD5加密
     *
     * @param str     内容
     * @param charset 编码方式
     * @throws Exception
     */
    public static String MD5(String str, String charset) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(str.getBytes(charset));
        byte[] result = md.digest();
        StringBuffer sb = new StringBuffer(32);
        for (int i = 0; i < result.length; i++) {
            int val = result[i] & 0xff;
            if (val <= 0xf) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(val));
        }
        return sb.toString().toLowerCase();
    }
}
