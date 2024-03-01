package com.roman.sapun.java.socialmedia;

import com.roman.sapun.java.socialmedia.util.scalar.ByteScalar;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.Bean;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SocialMediaJavaApplication {
	private final ByteScalar byteScalar;
	public SocialMediaJavaApplication(ByteScalar byteScalar) {
		this.byteScalar = byteScalar;
	}

	public static void main(String[] args) {
		SpringApplication.run(SocialMediaJavaApplication.class, args);
	}
	@Bean
	public RuntimeWiringConfigurer runtimeWiringConfigurer() {
		return wiringBuilder -> wiringBuilder.scalar(byteScalar.BYTE);
	}
}
