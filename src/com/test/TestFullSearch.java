package com.test;

import org.junit.Test;

import com.client.IFtsClient;

public class TestFullSearch {
	
	@Test
	public void testSearch(){
		
		IFtsClient client = new IFtsClient();
		try {
			String key = client.connect("liugang", "123456");
			client.Search("V_DOCUDOMENT", key, "");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

}
