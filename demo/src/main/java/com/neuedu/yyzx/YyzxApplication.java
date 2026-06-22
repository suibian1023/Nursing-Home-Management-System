package com.neuedu.yyzx;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.neuedu.yyzx.mapper")
public class YyzxApplication {
    public static void main(String[] args) {
        SpringApplication.run(YyzxApplication.class, args);
    }
}
