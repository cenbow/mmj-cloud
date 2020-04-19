package com.qianmi.open.api.tool.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加密解密工具类
 */
public class AesUtils {

    private static final String ALGORITHM = "AES/CBC/NoPadding";
    private static final String METHOD = "AES";
    private static final String UTF_8 = "UTF-8";

    /**
     * AES加密
     * @param data 明文
     * @param key 密钥
     * @return 密文
     * @throws Exception
     */
    public static String encrypt(String data, String key) throws Exception {
        // 参数校验
        RequestCheckUtils.checkNotEmpty(data, "data");
        RequestCheckUtils.checkNotEmpty(key, "key");
        RequestCheckUtils.checkLength(key, 16, "key");

        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            int blockSize = cipher.getBlockSize();
            byte[] dataBytes = data.getBytes(UTF_8);
            int plaintextLength = dataBytes.length;
            if (plaintextLength % blockSize != 0) {
                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
            }
            byte[] plaintext = new byte[plaintextLength];
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(UTF_8), METHOD);
            IvParameterSpec ivspec = new IvParameterSpec(key.getBytes(UTF_8));
            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            byte[] encrypted = cipher.doFinal(plaintext);
            return new BASE64Encoder().encode(encrypted);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * AES解密
     * @param data 密文
     * @param key 密钥
     * @return 明文
     * @throws Exception
     */
    public static String decrypt(String data, String key) throws Exception {
        // 参数校验
        RequestCheckUtils.checkNotEmpty(data, "data");
        RequestCheckUtils.checkNotEmpty(key, "key");
        RequestCheckUtils.checkLength(key, 16, "key");

        try {
            byte[] dataBytes = new BASE64Decoder().decodeBuffer(data);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(UTF_8), METHOD);
            IvParameterSpec ivspec = new IvParameterSpec(key.getBytes(UTF_8));
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
            byte[] result = cipher.doFinal(dataBytes);
            return new String(result, UTF_8).trim();
        } catch (Exception e) {
            return null;
        }
    }

}
