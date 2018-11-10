package com.test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import sun.org.mozilla.javascript.internal.ast.ThrowStatement;

public class Test {
	
	
	
	public void test()throws Exception{
		
		try {
			
			if(1==1){
				throw new Exception("hello");
			}
			
			
		} catch (Exception e) {
			System.out.println("coming");
		}
		
	}
	
	public static void main(String[] args) {
		BigDecimal big =new BigDecimal(2.03f);
		BigDecimal big1 =new BigDecimal(1.21f);
		//System.out.println(big1-big.);
		//System.out.println(2.03-1.21);
		
		
		System.out.println(Math.ceil(1.6));
		System.out.println(Math.floor(1.6));
		Test test = new Test();
		
		try {
			test.test();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long l = 45;
		Long l2 = new Long(l);
		String sl = l2.toString();
		System.out.println(sl);
			
		List<String> list = new ArrayList<String>();
		
		list.add("one");
		list.add("two");
		
		System.out.println(list.toString());
		
		System.out.println(new Date().getTime());//1474943528510

	}
	
	
	
	

}
