package com.wic.edu.kg;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.wic.edu.kg.mapper")
public class WicEduKgApplication {

    public static void main(String[] args) {
        SpringApplication.run(WicEduKgApplication.class, args);
    }

}
