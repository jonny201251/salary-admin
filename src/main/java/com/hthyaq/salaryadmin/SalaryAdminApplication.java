package com.hthyaq.salaryadmin;

import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@MapperScan("com.hthyaq.salaryadmin.mapper")
public class SalaryAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalaryAdminApplication.class, args);
    }
}
