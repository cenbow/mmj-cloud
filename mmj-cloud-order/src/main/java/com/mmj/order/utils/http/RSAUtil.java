package com.mmj.order.utils.http;


import com.mmj.order.utils.constant.CommonConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAUtil {

	private static Logger logger = LoggerFactory.getLogger(RSAUtil.class);

	/**
	 * 签名私钥
	 */
	public static final String PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJ8WBqs9+kTsZyhMd2HlmhTitNKC2QOLlsRT7xSzBsY6hMdfsuxpTIyUne++q0PbB755CAINAMOXqUS7YZfqLuxKMI1lL2Q1ePygXo8AcendqNDcBl6DVVS0N1OmX3hrox3XNCAq6N/Y6FSmoAR9eKshlPWrmtUMrT/pM5qpW65nAgMBAAECgYAlRofxf+gwViQlsSUX7zCXTDeKS9aFmiONhQ00EG656+RIwwetlU62cew/zLFciOnbm4hg0qMnMVYcILvRWLH9lDCvBfUFviUGh8cH5GwxsYKdVC0FMnLBreL2nqZqMyz3IgUYq5on2YQOeTDvw24We6Ir+cnG/eC3Og6v1YQrgQJBAOhkA55Ak71HYbyd93iasCHDZ0oMUBQr8h8CEg99U1gO1YiTVxUdWVlyYK0NKQlB6B1B4qozNAqNT7d5tu13jbECQQCvP4O7Qyy1GMmOGp7xERJtvMElgmb/3eWKILWGqXMA6+73qxOgUTxq22Euuw1CxSq/oQjNsgkKiCWg7FZ+aIuXAkAlZFpUr01WSlMGl2BHTpSCij9nYb3M0RlfNeUUCvVLGO/wzsxXQGatBsNZdOGidLFVa9F900lPXVdzTSlK4k6xAkEAo/47t7FU1OMtOd2PfucK58YKuJ8e47Eya9/P/wPyqpoeNYXSB+P9NcG1X5WLA0CekFSReGtyKnjdPYnsJr6OYQJAehECZP7DQ3XdM4SVEOT/aXzIaxAQMamI6UEsN835r+bN1zm+VVxRoDUcOLrWK3TjFI7JOt/CNf9KOzhpfLD++Q==";
	
	/**
	 * 签名公钥：由客户端保存，用于数据加密
	 */
	public static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCfFgarPfpE7GcoTHdh5ZoU4rTSgtkDi5bEU+8UswbGOoTHX7LsaUyMlJ3vvqtD2we+eQgCDQDDl6lEu2GX6i7sSjCNZS9kNXj8oF6PAHHp3ajQ3AZeg1VUtDdTpl94a6Md1zQgKujf2OhUpqAEfXirIZT1q5rVDK0/6TOaqVuuZwIDAQAB";
	
	public static final String APP_KEY = "MMJ";
	
	private static String RSA = "RSA";

	private static String API = "/api";

	private static KeyPair generateRSAKeyPair() {
		try {
			KeyPairGenerator generator = KeyPairGenerator.getInstance(RSA);
			int keyLength = 1024; //512~2048
			generator.initialize(keyLength);
			return generator.genKeyPair();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 用公钥加密 <br>
	 * 每次加密的字节数，不能超过密钥的长度值除以 8 再减去 11，所以采取分段加密的方式规避
	 * @param param
	 *            需要加密的数据
	 * @return 加密后的String型数据
	 */
	public static String encryptData(String param) {
		try {
			PublicKey publicKey = getPublicKey(PUBLIC_KEY);
			byte[] data = param.getBytes("UTF-8");
			Cipher cipher = Cipher.getInstance(RSA);
			// 编码前设定编码方式及密钥
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);

			RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;
			// 模长
			int keyLen = rsaPublicKey.getModulus().bitLength() / 8;
			int maxEncryptBlock = keyLen - 11;

			// 如果明文长度大于模长-11则要分组加密
			int inputLen = data.length;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int offSet = 0;
			byte[] temp;
			int i = 0;
			// 对数据分段加密
			while (inputLen - offSet > 0) {
				if (inputLen - offSet > maxEncryptBlock) {
					temp = cipher.doFinal(data, offSet, maxEncryptBlock);
				} else {
					temp = cipher.doFinal(data, offSet, inputLen - offSet);
				}
				out.write(temp, 0, temp.length);
				i++;
				offSet = i * maxEncryptBlock;
			}
			byte[] encryptedData = out.toByteArray();
			out.close();
			// 传入编码数据并返回编码结果
			return Base64Utils.encodeToString(encryptedData);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 用私钥解密
	 * 
	 * @param param
	 *            经过encryptedData()加密后再进行base64加密返回的String数据
	 * @return
	 */
	public static String decryptData(String param) {
		try {
			PrivateKey privateKey = getPrivateKey(PRIVATE_KEY);
			byte[] encryptedData = Base64Utils.decodeFromString(param);
			Cipher cipher = Cipher.getInstance(RSA);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);

			RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) privateKey;
			// 模长
			int keyLen = rsaPrivateKey.getModulus().bitLength() / 8;
			int maxDecryptBlock = keyLen;// 不用减11

			// 如果密文长度大于模长则要分组解密
			int inputLen = encryptedData.length;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int offSet = 0;
			byte[] temp;
			int i = 0;
			// 对数据分段解密
			while (inputLen - offSet > 0) {
				if (inputLen - offSet > maxDecryptBlock) {
					temp = cipher.doFinal(encryptedData, offSet,
							maxDecryptBlock);
				} else {
					temp = cipher.doFinal(encryptedData, offSet, inputLen
							- offSet);
				}
				out.write(temp, 0, temp.length);
				i++;
				offSet = i * maxDecryptBlock;
			}
			byte[] decryptedData = out.toByteArray();
			out.close();

			return new String(decryptedData, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/** 
     * 通过公钥byte[](publicKey.getEncoded())将公钥还原，适用于RSA算法 
     * 
     * @param keyBytes 
     * @return 
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeySpecException 
     */  
	private static PublicKey getPublicKey(String publicKeyStr) throws NoSuchAlgorithmException,  
            InvalidKeySpecException  
    { 
    	byte[] keyBytes = Base64Utils.decode(publicKeyStr.getBytes());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);  
        PublicKey publicKey = keyFactory.generatePublic(keySpec);  
        return publicKey;  
    }  
  
    /** 
     * 通过私钥byte[]将公钥还原，适用于RSA算法 
     * 
     * @param keyBytes 
     * @return 
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeySpecException 
     */  
	private static PrivateKey getPrivateKey(String privateKeyStr) throws NoSuchAlgorithmException,  
            InvalidKeySpecException  
    {
    	byte[] keyBytes = Base64Utils.decode(privateKeyStr.getBytes());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);  
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);  
        return privateKey;  
    }
	
	public static String getSignParamValue(String url) {
		StringBuilder sb = new StringBuilder();
		sb.append(APP_KEY);
		sb.append(CommonConstant.Symbol.UNDERLINE);
		sb.append(System.currentTimeMillis());
		sb.append(CommonConstant.Symbol.UNDERLINE);

		java.net.URL jnr;
		try {
			jnr = new java.net.URL(url);
			String servletPath = jnr.getPath();
			if(!servletPath.startsWith(API)) {
				if(servletPath.startsWith(CommonConstant.Symbol.SPRIT)) {
					servletPath = servletPath.substring(servletPath.indexOf(CommonConstant.Symbol.SPRIT) + 1);
				}
				servletPath = servletPath.substring(servletPath.indexOf(CommonConstant.Symbol.SPRIT));
			}
			sb.append(servletPath);
		} catch (MalformedURLException e) {
			logger.error("-->getSignParamValue-->解析URL出错：", e);
			sb.append(url);
		}

		return encryptData(sb.toString());
	}

	public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException {
		
		// 客户端通过公钥对设定的字段串（令牌标识（MMJ_TOKEN_）+时间戳）进行加密得到签名参数sign=xxxxxxxxxxxxx
		// 服务端判断是否有带sign参数，没有则返回错误提示，
		// 如果有则通过私钥（私钥不传播）对签名参数进行解密，得到解密后的字符串，判断认证令牌标识是否正确，不正确则返回错误提示
		// 再判断传来的时间戳，如果时间大于1分钟，则签名无效，返回错误提示
		
//		KeyPair keyPair = generateRSAKeyPair();
//		byte[] publicKeyByte = Base64Utils.encode(keyPair.getPublic().getEncoded());
//		byte[] privateKeyByte = Base64Utils.encode(keyPair.getPrivate().getEncoded());
//		String publicKeyStr = new String(publicKeyByte);
//		String privateKeyStr = new String(privateKeyByte); 
////		String publicKeyStr = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJGbfbPzZ92pE2NdQfdRcwnzsc4jAfQaA5C/aHGhlSCo+3hB9o4slL5ET8vusiMiGVAM5UqkLJC9QW620wmB+L8CAwEAAQ==";
////		String privateKeyStr = "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAkZt9s/Nn3akTY11B91FzCfOxziMB9BoDkL9ocaGVIKj7eEH2jiyUvkRPy+6yIyIZUAzlSqQskL1BbrbTCYH4vwIDAQABAkEAgpDqvEWEmsh1AUHr2CkqPf9PLmhH5Sq6jb6FmndpMRnacZtelTcTk1z7bBArzvQntpkqeXOUr9C1nzrIo9YW8QIhANQpjo+CTm8Fd1hvMfxaUyRbk6J1vXvef4Y2h1LymziXAiEAr7F5SStJO2aUXgUbA2yWjwsGs6Za5BEpJF+sMCM+XhkCIQCaDLqo1TLiRhPMMKEY3PT1t1DRa7B0GrB54WoM/n9abQIgNYGwNVdocx23e7bEVlOguqmZ/eZg8epEBF7ausJRASkCH0WHysOa/VXigKbmy+strJ2qbKnNBYWqokIq5maBS9I=";
//		System.out.println("公钥：" + new String(publicKeyStr));
//		System.out.println("私钥：" + new String(privateKeyStr));
		
		String url = "https://api.polynome.cn/weixin/msg/sendMsg?userid=18180228740284416";
		String sign = getSignParamValue(url);
		System.out.println("加密后的数据："+sign);
		String result = decryptData(sign);
		System.out.println("解密后的数据："+result);
		
//		String timestampStr = result.replace("MMJ_APP_", "");
//		System.out.println("时间戳:"+timestampStr);
//		
//		long currentTimestamp = System.currentTimeMillis();
//		long interval = (currentTimestamp - Long.valueOf(timestampStr))/1000;
//		System.out.println(interval);
//		if(interval > 120 || interval < 0) {
//			System.out.println("时间不符");
//		}
		
	}

}
