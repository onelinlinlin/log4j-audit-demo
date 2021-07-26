package com.lzp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@MapperScan(basePackages = "com.lzp.mapper")
public class bankApplication {

    public static void main(String[] args) {
        /*
         * 启动类的入口 默认整合到Tomcat容器端口8080
         * */
        SpringApplication.run(bankApplication.class, args);
    }


}
