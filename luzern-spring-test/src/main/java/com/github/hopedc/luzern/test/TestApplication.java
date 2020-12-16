package com.github.hopedc.luzern.test;


import com.github.hopedc.luzern.boot.EnableLuzern;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author hopedc
 * @date 2017-03-09 15:46
 */
@EnableLuzern
@SpringBootApplication
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
