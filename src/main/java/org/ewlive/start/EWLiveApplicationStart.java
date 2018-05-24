package org.ewlive.start;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("org.ewlive")
public class EWLiveApplicationStart {
    public static void main(String[] args) {
        SpringApplication.run(EWLiveApplicationStart.class, args);
    }
}
