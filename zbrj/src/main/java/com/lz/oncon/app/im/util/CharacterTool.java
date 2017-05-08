package com.lz.oncon.app.im.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class CharacterTool {
	 /**
	  * 递归
	  * @author wyh
	  * @param strJaggedArray
	  * @return
	  */
	    public static String[] Exchange(String[][] strJaggedArray){
	        String[][] temp = DoExchange(strJaggedArray);
	        return temp[0];       
	    }
	   
	    /**
	     * 递归
	     * @author wyh
	     * @param strJaggedArray
	     * @return
	     */
	    private static String[][] DoExchange(String[][] strJaggedArray){
	        int len = strJaggedArray.length;
	        if(len >= 2){           
	            int len1 = strJaggedArray[0].length;
	            int len2 = strJaggedArray[1].length;
	            int newlen = len1*len2;
	            String[] temp = new String[newlen];
	            int Index = 0;
	            for(int i=0;i<len1;i++){
	                for(int j=0;j<len2;j++){
	                    temp[Index] = strJaggedArray[0][i] + strJaggedArray[1][j];
	                    Index ++;
	                }
	            }
	            String[][] newArray = new String[len-1][];
	            for(int i=2;i<len;i++){
	                newArray[i-1] = strJaggedArray[i];                           
	            }
	            newArray[0] = temp;
	            return DoExchange(newArray);
	        }else{
	         return strJaggedArray;   
	        }
	    }

	    /**
	     * 将字符串转换成ASCII码
	     * 
	     * @param cnStr
	     * @return
	     */
	    public static String getCnASCII(String cnStr) {
	    	StringBuffer strBuf = new StringBuffer();
	    	// 将字符串转换成字节序列
	    	byte[] bGBK = cnStr.getBytes();
	    	for (int i = 0; i < bGBK.length; i++) {
	    		// 将每个字符转换成ASCII码
	    		strBuf.append(Integer.toHexString(bGBK[i] & 0xff));
	    	}
	    	return strBuf.toString();
	    }
	    
	    /**
  		 * 将汉字转换为全拼
  		 * 
  		 * @param src
  		 * @return
  		 */
  		public static String getPinYin(String src) {
  			char[] t1 = null;
  			t1 = src.toCharArray();
  			String[] t2 = new String[t1.length];
  			// 设置汉字拼音输出的格式
  			HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
  			t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
  			t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
  			t3.setVCharType(HanyuPinyinVCharType.WITH_V);
  			String t4 = "";
  			int t0 = t1.length;
  			try {
  				for (int i = 0; i < t0; i++) {
  					// 判断能否为汉字字符
  					if (Character.toString(t1[i]).matches("[\\u4E00-\\u9FA5]+")) {
  						t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);// 将汉字的几种全拼都存到t2数组中
  						t4 += t2[0];// 取出该汉字全拼的第一种读音并连接到字符串t4后
  					} else {
  						// 如果不是汉字字符，间接取出字符并连接到字符串t4后
  						t4 += Character.toString(t1[i]);
  					}
  				}
  			} catch (BadHanyuPinyinOutputFormatCombination e) {
  				e.printStackTrace();
  			}
  			return t4.toLowerCase();
  		}
}