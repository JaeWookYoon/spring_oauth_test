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
        config.setPassword(key); // ?ïî?ò∏?ôî?ï† ?ïå ?Ç¨?ö©?ïò?äî ?Ç§
        config.setAlgorithm("PBEWithMD5AndDES"); // ?ïî?ò∏?ôî ?ïåÍ≥†Î¶¨Ï¶?
        config.setKeyObtentionIterations("1000"); // Î∞òÎ≥µ?ï† ?ï¥?ã± ?öå?àò
        config.setPoolSize("1"); // ?ù∏?ä§?Ñ¥?ä§ pool
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator"); // salt ?Éù?Ñ± ?Å¥?ûò?ä§
        config.setStringOutputType("base64"); //?ù∏ÏΩîÎî© Î∞©Ïãù
        encryptor.setConfig(config);
        return encryptor;
    }
}
