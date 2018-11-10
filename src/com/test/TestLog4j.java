package com.test;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public   class  TestLog4j  {
    public   static   void  main(String[] args)  {
       //PropertyConfigurator.configure( "F:/tomcat6.0/webapps/fts/WEB-INF/classes/log4j.properties" );
      // Logger logger  =  Logger.getLogger(TestLog4j. class );
      // logger.info( " info " );
    	
    	StringBuffer resultxml =  new StringBuffer();
		resultxml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		
		System.out.println(resultxml.toString());
       
       
   } 
} 
