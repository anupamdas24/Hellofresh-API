package com.hellofresh.utilities;

import java.io.FileInputStream;
import java.util.Properties;

public class Getconfig {
	
	public static String get_properties(String key) {
	Properties p = new Properties();
	String FilePath = System.getProperty("user.dir")+"/src/main/resources/config.properties";
	FileInputStream fi=null;
	try {
		fi = new FileInputStream(FilePath);
		p.load(fi);
	} catch (Exception e) {
		e.printStackTrace();
	}
	return (String)p.getProperty(key);
}
}
