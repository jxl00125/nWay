package com.test;




/**
 * CreateIndex
 * @author LEE.SIU.WAH
 * @email lixiaohua7@163.com
 * @date 2016-3-6 上午9:49:55
 * @version 1.0
 */
public class CreateIndex {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{

		String bb = "1啊asa是fdsf啊234";  
		System.out.println(bSubstring(bb, 1000)); 
	}

	private static void readFile()throws Exception {
		/*IFtsClient client = new IFtsClient();
		String key = client.connect("liugang", "123456");
		String str = client.createGlobalindex("v_docudoment", key);
		//String str = client.Incrementalindex("v_docudoment", key);
		System.out.println("str="+str);*/
	}
	
	
	public static String bSubstring(String s, int length) throws Exception {  
		  
	    byte[] bytes = s.getBytes("Unicode");  
	    int n = 0; // 表示当前的字节数  
	    int i = 2; // 要截取的字节数，从第3个字节开始  
	    for (; i < bytes.length && n < length; i++) {  
	        // 奇数位置，如3、5、7等，为UCS2编码中两个字节的第二个字节  
	        if (i % 2 == 1) {  
	            n++; // 在UCS2第二个字节时n加1  
	        } else {  
	            // 当UCS2编码的第一个字节不等于0时，该UCS2字符为汉字，一个汉字算两个字节  
	            if (bytes[i] != 0) {  
	                n++;  
	            }  
	        }  
	  
	    }  
	    // 如果i为奇数时，处理成偶数  
	    /* 
	     * if (i % 2 == 1){ // 该UCS2字符是汉字时，去掉这个截一半的汉字 if (bytes[i - 1] != 0) i = 
	     * i - 1; // 该UCS2字符是字母或数字，则保留该字符 else i = i + 1; } 
	     */  
	    // 将截一半的汉字去掉，不加入到结果字符串  
	    if (i % 2 == 1) {  
	        i = i - 1;  
	    }  
	    return new String(bytes, 0, i, "Unicode"); 
	}  
}
