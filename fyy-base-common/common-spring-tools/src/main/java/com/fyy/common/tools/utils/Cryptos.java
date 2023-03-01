package com.fyy.common.tools.utils;

import com.alibaba.fastjson2.JSON;
import com.fyy.common.tools.exception.RenException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * AES加密解密
 */
@Slf4j
public class Cryptos {
    /**
     * 自定义密钥
     */
    private String strDefaultKey = "1564789456100125";
    private byte[] key = strDefaultKey.getBytes();

    private static final String AES = "AES";
    private static final String AES_CBC = "AES/CBC/PKCS5Padding";
    private static final String HMACSHA1 = "HmacSHA1";

    private static final int DEFAULT_HMACSHA1_KEYSIZE = 160; //RFC2401
    private static final int DEFAULT_AES_KEYSIZE = 128;
    private static final int DEFAULT_IVSIZE = 16;

    private static SecureRandom random = new SecureRandom();

    private Cryptos() {
    }

    /**
     * 设置密钥
     */
    private Cryptos(String strDefaultKey) {
        this.strDefaultKey = strDefaultKey;
        this.key = strDefaultKey.getBytes();
    }

    public static Cryptos getInstance() {
        return CryptosSingle.getInstance();
    }

    /**
     * 枚举单例
     */
    public enum CryptosSingle {
        INSTANCE;

        private final Cryptos cryptos;

        CryptosSingle() {
            cryptos = new Cryptos();
        }

        public static Cryptos getInstance() {
            return CryptosSingle.INSTANCE.cryptos;
        }
    }

    //-- HMAC-SHA1 function --//

    /**
     * 使用HMAC-SHA1进行消息签名, 返回字节数组,长度为20字节.
     *
     * @param input 原始输入字符数组
     * @param key   HMAC-SHA1密钥
     */
    public static byte[] hmacSha1(byte[] input, byte[] key) {
        try {
            SecretKey secretKey = new SecretKeySpec(key, HMACSHA1);
            Mac mac = Mac.getInstance(HMACSHA1);
            mac.init(secretKey);
            return mac.doFinal(input);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            throw new RenException("使用HMAC-SHA1进行消息签名出错：", e);
        }
    }

    /**
     * 校验HMAC-SHA1签名是否正确.
     *
     * @param expected 已存在的签名
     * @param input    原始输入字符串
     * @param key      密钥
     */
    public static boolean isMacValid(byte[] expected, byte[] input, byte[] key) {
        byte[] actual = hmacSha1(input, key);
        return Arrays.equals(expected, actual);
    }

    /**
     * 生成HMAC-SHA1密钥,返回字节数组,长度为160位(20字节).
     * HMAC-SHA1算法对密钥无特殊要求, RFC2401建议最少长度为160位(20字节).
     */
    public static byte[] generateHmacSha1Key() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(HMACSHA1);
            keyGenerator.init(DEFAULT_HMACSHA1_KEYSIZE);
            SecretKey secretKey = keyGenerator.generateKey();
            return secretKey.getEncoded();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            throw new RenException("生成HMAC-SHA1密钥出错：", e);
        }
    }

    //-- AES funciton --//

    /**
     * 使用AES加密原始字符串.
     *
     * @param input 原始输入字符数组
     */
    public byte[] aesEncrypt(byte[] input) {
        if (key.length != 16) {
            log.info("Key长度不是16位");
        }

        return aes(input, key, Cipher.ENCRYPT_MODE);
    }

    /**
     * 使用AES加密原始字符串.
     *
     * @param input 原始输入字符串
     */
    public String aesEncrypt(String input) {
        try {
            byte[] encryptResult = aesEncrypt(input.getBytes());
            return Encodes.encodeHex(encryptResult);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RenException("生成HMAC-SHA1密钥出错：", e);
        }
    }

    /**
     * 使用AES解密字符串, 返回原始字符串.
     *
     * @param input Hex编码的加密字符串
     * @throws UnsupportedEncodingException
     */
    public String aesDecrypt(byte[] input) throws UnsupportedEncodingException {
        if (key.length != 16) {
            log.info("Key长度不是16位");
        }
        byte[] decryptResult = aes(input, key, Cipher.DECRYPT_MODE);
        return new String(decryptResult, StandardCharsets.UTF_8);
    }

    /**
     * 使用AES解密字符串, 返回原始字符串.
     *
     * @param input Hex编码的加密字符串
     * @throws UnsupportedEncodingException
     */
    public String aesDecrypt(String input) {
        try {
            return aesDecrypt(Encodes.decodeHex(input));
        } catch (Exception e) {
            throw new RenException("解密出错：", e);
        }
    }


    /**
     * 【常用】 AES对称加密
     *
     * @param originMsg 明文信息
     * @param keyStr    密钥
     */
    public static String aesEncrypt(String originMsg, String keyStr) {
        try {
            byte[] raw = keyStr.getBytes(StandardCharsets.US_ASCII);
            SecretKeySpec keySpec = new SecretKeySpec(raw, AES);
            Cipher cipher = Cipher.getInstance(AES_CBC);
            int blockSize = cipher.getBlockSize();
            IvParameterSpec iv = new IvParameterSpec(keyStr.substring(0, blockSize).getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
            byte[] original = cipher.doFinal(originMsg.getBytes());
            return Encodes.encodeHex(original); // 16进制加密
        } catch (Exception e) {
            throw new RenException("加密失败：", e);
        }
    }

    /**
     * 【常用】 AES对称加密
     *
     * @param encodeMsg 密文信息
     * @param keyStr    密钥
     */
    public static String aesDecrypt(String encodeMsg, String keyStr) {
        try {
            byte[] raw = keyStr.getBytes(StandardCharsets.US_ASCII);
            SecretKeySpec keySpec = new SecretKeySpec(raw, AES);
            Cipher cipher = Cipher.getInstance(AES_CBC);
            int blockSize = cipher.getBlockSize();
            IvParameterSpec iv = new IvParameterSpec(keyStr.substring(0, blockSize).getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
            byte[] encrypted1 = Encodes.decodeHex(encodeMsg); // 解密
            byte[] deOriginal = cipher.doFinal(encrypted1);
            return new String(deOriginal, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RenException("解密失败：", e);
        }
    }


    /**
     * 使用AES加密原始字符串
     *
     * @param input 原始输入字符数组
     * @param key   符合AES要求的密钥
     * @param iv    初始向量
     */
    public static byte[] aesEncrypt(byte[] input, byte[] key, byte[] iv) {
        return aes(input, key, iv, Cipher.ENCRYPT_MODE);
    }

    /**
     * 使用AES解密字符串, 返回原始字符串.
     *
     * @param input Hex编码的加密字符串
     * @param key   符合AES要求的密钥
     * @param iv    初始向量
     * @throws UnsupportedEncodingException
     */
    public static String aesDecrypt(byte[] input, byte[] key, byte[] iv) throws UnsupportedEncodingException {
        byte[] decryptResult = aes(input, key, iv, Cipher.DECRYPT_MODE);
        return new String(decryptResult, StandardCharsets.UTF_8);
    }

    /**
     * 使用AES加密或解密无编码的原始字节数组, 返回无编码的字节数组结果.
     *
     * @param input 原始字节数组
     * @param key   符合AES要求的密钥
     * @param mode  Cipher.ENCRYPT_MODE 或 Cipher.DECRYPT_MODE
     */
    private static byte[] aes(byte[] input, byte[] key, int mode) {
        try {
            SecretKey secretKey = new SecretKeySpec(key, AES);
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(mode, secretKey);
            return cipher.doFinal(input);
        } catch (GeneralSecurityException e) {
            throw new RenException("使用AES加密或解密无编码的原始字节数组出错：", e);
        }
    }

    /**
     * 使用AES加密或解密无编码的原始字节数组, 返回无编码的字节数组结果.
     *
     * @param input 原始字节数组
     * @param key   符合AES要求的密钥
     * @param iv    初始向量
     * @param mode  Cipher.ENCRYPT_MODE 或 Cipher.DECRYPT_MODE
     */
    private static byte[] aes(byte[] input, byte[] key, byte[] iv, int mode) {
        try {
            SecretKey secretKey = new SecretKeySpec(key, AES);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance(AES_CBC);
            cipher.init(mode, secretKey, ivSpec);
            return cipher.doFinal(input);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            throw new RenException("使用AES加密或解密无编码的原始字节数组：", e);
        }
    }

    /**
     * 生成AES密钥,返回字节数组, 默认长度为128位(16字节).
     */
    public static byte[] generateAesKey() {
        return generateAesKey(DEFAULT_AES_KEYSIZE);
    }

    /**
     * 生成AES密钥,可选长度为128,192,256位.
     */
    public static byte[] generateAesKey(int keysize) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES);
            keyGenerator.init(keysize);
            SecretKey secretKey = keyGenerator.generateKey();
            return secretKey.getEncoded();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            throw new RenException("生成AES密钥出错：", e);
        }
    }

    /**
     * 生成随机向量,默认大小为cipher.getBlockSize(), 16字节.
     */
    public static byte[] generateIV() {
        byte[] bytes = new byte[DEFAULT_IVSIZE];
        random.nextBytes(bytes);
        return bytes;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    /**
     * 抖店专用解密
     */
    public static String doudianAesDecrypt(String msgSecret, String appSecret) {
        try {
            String Key = appSecret.replaceAll("-", "");
            byte[] raw = Key.getBytes(StandardCharsets.US_ASCII);
            SecretKeySpec keySpec = new SecretKeySpec(raw, AES);
            Cipher cipher = Cipher.getInstance(AES_CBC);
            int blockSize = cipher.getBlockSize();
            IvParameterSpec iv = new IvParameterSpec(Key.substring(0, blockSize).getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
            byte[] encrypted1 = Base64.getDecoder().decode(msgSecret);//先用base64解密
            byte[] original = cipher.doFinal(encrypted1);
            return new String(original, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RenException("抖店付费消息解密失败，请注意CSRF: msg = " + msgSecret);
        }

    }

    @Data
    public static class DoudianPayMessage {

        /**
         * 消息类型，1代表支付成功通知消息，2代表授权相关消息
         */
        private Integer msg_type;

        private String msg;
    }


    public static void main(String[] args) throws Exception {
        String msg = "{\"company_code\":\"1297062040007114754\",\"userId\":\"1295619787862573057\"}";
        String a = Cryptos.getInstance().aesEncrypt(msg);
        String b = Cryptos.getInstance().aesDecrypt(a);
        System.out.println(msg + "\n" + a + "\n" + b);

        String scMsg = "avE+wkIQrAuCCe3zIic8uwKSqiuseHYx8yklYPuTwqoJ7ATQvGFHkqYKvNvOp/lFsxHkcgUSV4CUxMDj/1bsayKQUW/WfJorIt//HAV5753UtMkr+kARXoocHKjN70qm";
        String appSec = "a5c5a2dc-31ca-4a7a-b743-e862d96082bd";
        DoudianPayMessage doudianPayMessage = JSON.parseObject(doudianAesDecrypt(scMsg, appSec), DoudianPayMessage.class);
        if (doudianPayMessage.getMsg_type() == 1) {
            // 支付成功
            System.out.println(JSON.toJSON(doudianPayMessage));
        } else if (doudianPayMessage.getMsg_type() == 2) {
            // 关闭授权
            System.out.println(JSON.toJSON(doudianPayMessage));
        }

//        String originMsg = "15871710952";
//        String appSec = "a5c5a2dc31ca4a7ab743e862d96082bd";
//
//
//        // 加密过程
//        byte[] raw = appSec.getBytes(StandardCharsets.US_ASCII);
//        SecretKeySpec keySpec = new SecretKeySpec(raw, AES);
//        Cipher cipher = Cipher.getInstance(AES_CBC);
//        int blockSize = cipher.getBlockSize();
//        IvParameterSpec iv = new IvParameterSpec(appSec.substring(0, blockSize).getBytes());
//        cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
//        byte[] original = cipher.doFinal(originMsg.getBytes());
//        String encodeHex = Encodes.encodeHex(original);//先加密
//        System.out.println(encodeHex);
//
//
//        cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
//        byte[] encrypted1 = Encodes.decodeHex(encodeHex); // 解密
//        byte[] deOriginal = cipher.doFinal(encrypted1);
//        System.out.println(new String(deOriginal, StandardCharsets.UTF_8));
    }
}
