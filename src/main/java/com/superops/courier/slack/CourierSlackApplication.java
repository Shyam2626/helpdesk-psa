package com.superops.courier.slack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CourierSlackApplication {

	public static void main(String[] args) {
		SpringApplication.run(CourierSlackApplication.class, args);
	}

}
