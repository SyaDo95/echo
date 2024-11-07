
package com.echoproject.echo;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class EchoApplication {

	public static void main(String[] args) {
		try {
			FirebaseInitializer.initialize();
		} catch (IOException e) {
			e.printStackTrace();
		}
		SpringApplication.run(EchoApplication.class, args);
	}
}
