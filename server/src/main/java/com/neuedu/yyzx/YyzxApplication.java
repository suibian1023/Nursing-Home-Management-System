package com.neuedu.yyzx;

import org.mindrot.jbcrypt.BCrypt;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.neuedu.yyzx.mapper")
public class YyzxApplication {
    public static void main(String[] args) {
        // >>> 启动时在控制台输出 admin123 的 BCrypt 密文，便于手动更新数据库 password 字段 <<<
        System.out.println("========== BCrypt(guanjia123) = " + BCrypt.hashpw("guanjia123", BCrypt.gensalt(12)) + " ==========");
        SpringApplication.run(YyzxApplication.class, args);
    }
}
