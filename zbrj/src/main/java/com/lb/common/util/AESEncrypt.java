package com.lb.common.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;


public class AESEncrypt {

	private static String ALG = "AES";
	private static String CE = "UTF-8";
	
	private static byte[] encrypt(String content, String password, String iv) {  
        try {             
        	SecretKeySpec key = new SecretKeySpec(password.getBytes(CE), ALG);
        	Cipher cipher = Cipher.getInstance(ALG);// 创建密码器 
        	byte[] byteContent = content.getBytes(CE);  
        	cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化   
        	byte[] result = cipher.doFinal(byteContent);  
        	return result; // 加密   
        } catch (NoSuchAlgorithmException e) {  
        	e.printStackTrace();  
        } catch (NoSuchPaddingException e) {
        	e.printStackTrace();  
        } catch (InvalidKeyException e) {  
        	e.printStackTrace();  
        } catch (UnsupportedEncodingException e) {  
        	e.printStackTrace();  
        } catch (IllegalBlockSizeException e) {  
        	e.printStackTrace();  
        } catch (BadPaddingException e) {  
        	e.printStackTrace();  
        }
        return null;  
	}
	
	private static byte[] decrypt(byte[] content, String password, String iv) {  
        try {  
        	SecretKeySpec key = new SecretKeySpec(password.getBytes(CE), ALG);
        	Cipher cipher = Cipher.getInstance(ALG);// 创建密码器   
        	cipher.init(Cipher.DECRYPT_MODE, key);// 初始化   
        	byte[] result = cipher.doFinal(content);  
        	return result; // 加密   
        } catch (NoSuchAlgorithmException e) {  
        	e.printStackTrace();  
        } catch (NoSuchPaddingException e) {  
        	e.printStackTrace();  
        } catch (InvalidKeyException e) {  
        	e.printStackTrace();  
        } catch (IllegalBlockSizeException e) {  
        	e.printStackTrace();  
        } catch (BadPaddingException e) {  
        	e.printStackTrace();  
        } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}  
        return null;  
	}  
	
	private static String parseByte2HexStr(byte buf[]) {  
        StringBuffer sb = new StringBuffer();  
        for (int i = 0; i < buf.length; i++) {  
        	String hex = Integer.toHexString(buf[i] & 0xFF);  
        	if (hex.length() == 1) {  
        		hex = '0' + hex;  
        	}  
        	sb.append(hex.toUpperCase());  
        }  
        return sb.toString();  
	}
	
	private static byte[] parseHexStr2Byte(String hexStr) {  
        if (hexStr.length() < 1)  
        	return null;  
        byte[] result = new byte[hexStr.length()/2];  
        for (int i = 0;i< hexStr.length()/2; i++) {  
        	int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);  
        	int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);  
        	result[i] = (byte) (high * 16 + low);  
        }  
        return result;
	}
	
	public static String encrypt(String content, String password){
		try{
			return parseByte2HexStr(encrypt(StringUtils.repNull(content), StringUtils.repNull(password), ""));	
		}catch(Exception e){
			return "";
		}
		
	}
	
	public static String decrypt(String content, String password){
		try{
			return new String(decrypt(parseHexStr2Byte(content), password, ""));
		}catch(Exception e){
			return "";
		}
	}
	
	//先base64解码,然后用AES解密
	public static String decrypt_Base64(String content, String password){
		try{
			return new String(decrypt(Base64.decode(content), password, ""));
		}catch(Exception e){
			return "";
		}
	}
	
	public static void main(String[] args){
//		String content = "10000testne" + System.currentTimeMillis();  
		String content = "18600205830,陈洋洋";  
		
		String password = "lur8apa4zu484pvj";
		//加密   
		System.out.println("加密前：" + content);
		String encryptResultStr = encrypt(content, password);  
		System.out.println("加密后：" + encryptResultStr);  
		//解密  
		System.out.println("解密后：" + decrypt(encryptResultStr,password));
	}
}