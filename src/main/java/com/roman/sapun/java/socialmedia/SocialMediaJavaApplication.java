package com.roman.sapun.java.socialmedia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SocialMediaJavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialMediaJavaApplication.class, args);
	}

}
