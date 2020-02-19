package org.feosd.admin;

import org.feosd.auth.client.AuthConfiguration;
import org.feosd.base.service.spring.BaseConfiguration;
import org.feosd.base.service.spring.ServiceValidConfiguration;
import org.feosd.base.web.api.CorsConfguration;
import org.feosd.common.dict.web.aspect.DictAspect;
import org.feosd.common.syslog.web.aspect.SysLogAspect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({BaseConfiguration.class, CorsConfguration.class, AuthConfiguration.class, ServiceValidConfiguration.class})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public SysLogAspect sysLogAspect(){
        return new SysLogAspect();
    }

    @Bean
    public DictAspect dictAspect(){
        return new DictAspect();
    }
}
