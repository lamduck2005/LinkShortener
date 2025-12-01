package com.lamduck2005.linkshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class LinkShortenerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LinkShortenerApplication.class, args);
    }

}
