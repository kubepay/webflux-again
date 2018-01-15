package com.kubepay.webflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories
public class WebfluxAgainApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebfluxAgainApplication.class, args);
	}
}
//application contains
//#servicestore.jks
//#servicestore.p12
//#servicestore.pem
//#servicestore.pfx
