package com.jwyoon.oauth.test;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.Test;

public class JayyptTest {

	@Test
	public void contextLoads() {
	}

	@Test 
	public void jasypt() { 
		String url = "jdbc:oracle:thin:@(description= (retry_count=20)(retry_delay=3)(address=(protocol=tcps)(port=1522)(host=adb.ca-toronto-1.oraclecloud.com))(connect_data=(service_name=gcaa070203a63c7_jakedbdb_high.adb.oraclecloud.com))(security=(ssl_server_cert_dn=\"CN=adb.ca-toronto-1.oraclecloud.com, OU=Oracle BMCS TORONTO, O=Oracle Corporation, L=Redwood City, ST=California, C=US\")))";
				
		System.out.println(jasyptEncoding(url));
		
	  
	  //System.out.println(jasyptEncoding(username));
	  //System.out.println(jasyptEncoding(password)); }
	}
	public String jasyptEncoding(String value) {

        String key = "jwyoon0717secret_key";
	
		StandardPBEStringEncryptor pbeEnc = new StandardPBEStringEncryptor();
		pbeEnc.setAlgorithm("PBEWithMD5AndDES"); //
		pbeEnc.setPassword(key); 
		return pbeEnc.encrypt(value);
	}

}
