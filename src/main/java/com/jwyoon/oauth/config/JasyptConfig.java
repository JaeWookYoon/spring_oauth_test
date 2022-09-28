package com.jwyoon.oauth.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JasyptConfig {

	@Bean(name = "jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {

        String key = "jwyoon0717secret_key";
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(key); // ?��?��?��?�� ?�� ?��?��?��?�� ?��
        config.setAlgorithm("PBEWithMD5AndDES"); // ?��?��?�� ?��고리�?
        config.setKeyObtentionIterations("1000"); // 반복?�� ?��?�� ?��?��
        config.setPoolSize("1"); // ?��?��?��?�� pool
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator"); // salt ?��?�� ?��?��?��
        config.setStringOutputType("base64"); //?��코딩 방식
        encryptor.setConfig(config);
        return encryptor;
    }
}
