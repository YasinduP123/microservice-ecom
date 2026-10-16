package edu.yasidu.orderService.client.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InventoryFeignClientConfig {

    @Bean
    public Logger.Level invFeignLoggerLevel(){
        return Logger.Level.BASIC;
    }

}
