package com.lb.common.util;

/**
 * 加密使用
 * <p>Title: </p>
 * <p>Description: </p>
 */
import java.security.*;
import javax.crypto.*;

import android.text.TextUtils;

public class Encoding
{
  private static String Algorithm="DES"; //定义 加密算法,可用 DES,DESede,Blowfish

  private static String keyText="D6A3B6ABEACDB1E0";//密钥

  /**
   * 算法提供商
   */
  static{
    Security.addProvider(new com.sun.crypto.provider.SunJCE());
  }

  /**
   * 构造方法
   */
  public Encoding() {
  }

  /**
   * 加密，内部使用
   * @since 2002-09-20
   * @param input
   * @param key
   * @return
   * @throws Exception
   */
  private static byte[] encode(byte[] input,byte[] key) throws Exception{
    SecretKey deskey = new javax.crypto.spec.SecretKeySpec(key,Algorithm);
    Cipher c1 = Cipher.getInstance(Algorithm);
    c1.init(Cipher.ENCRYPT_MODE,deskey);
    byte[] cipherByte=c1.doFinal(input);
    return cipherByte;

  }

  //解密
  private static byte[] decode(byte[] input,byte[] key) throws Exception{
    SecretKey deskey = new javax.crypto.spec.SecretKeySpec(key,Algorithm);
    Cipher c1 = Cipher.getInstance(Algorithm);
    c1.init(Cipher.DECRYPT_MODE,deskey);
    byte[] clearByte=c1.doFinal(input);
    return clearByte;
  }

  //md5()信息摘要, 不可逆
  private static String md5(byte[] input) throws Exception{
    java.security.MessageDigest alg=java.security.MessageDigest.getInstance("MD5"); //or "SHA-1"
    alg.update(input);
    byte[] digest = alg.digest();
    return byte2string(digest);
  }

  /**
   * 字节码转换成自定义字符串
   * 内部调试使用
   * @param b
   * @return
   */
  public static String byte2string(byte[] b)
  {
    String hs="";
    String stmp="";
    for (int n=0;n<b.length;n++){
      stmp=(java.lang.Integer.toHexString(b[n] & 0XFF));
      if (stmp.length()==1)
        hs=hs+"0"+stmp;
      else hs=hs+stmp;
    }
    return hs.toUpperCase();
  }

  private static byte[] string2byte(String hs)
  {
    byte[] b=new byte[hs.length()/2];
    for(int i=0;i<hs.length();i=i+2)
    {
      String sub=hs.substring(i,i+2);
      byte bb= Integer.valueOf(sub,16).byteValue();
      b[i/2]=bb;
    }
    return b;
  }

  /**
   * MD5加密的接口，不可逆
   * @since 2002-09-20
   * @param text
   * @param newDebug
   * @return
   */
  public static String encodeCmd(String text)
  {
    String encodedText=new String();
    try
    {
      encodedText=md5(text.getBytes());
    }
    catch(Exception e)
    {
    }
    return encodedText;
  }

  /**
   * 加密（可逆）
   * @param inputText 原始文本
   * @param newDebug  是否观看加密过程
   * @return
   */
  public static String encodingCanDecode(String inputText)
  {
    byte[] key;
    String encodedText=null;
    key=string2byte(keyText);
    try
    {
      encodedText=byte2string(encode(inputText.getBytes(),key));
    }
    catch(Exception e)
    {
    }
    return encodedText;
  }

  /**
   * 解密
   * @param encodedText 加密的文本
   * @param newDebug  是否观看解密过程
   * @return
   */
  public static String decoding(String encodedText)
  {
	  if(TextUtils.isEmpty(encodedText)){
		  return "";
	  }
    byte[] key;
    String decodedText=null;
    key=string2byte(keyText);
    try
    {
    	byte[] b=string2byte(encodedText);
      byte[] bb=decode(b,key);
      decodedText=new String(bb);
    }
    catch(Exception e)
    {
    }
    return decodedText;
  }

  public static void main(String arg[])
  {
    System.out.println("dbpassword : "+encodingCanDecode("daj039jcec983c2!!ew!fe13"));
  }

}