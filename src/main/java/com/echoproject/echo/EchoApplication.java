
package com.echoproject.echo;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication(scanBasePackages = "com.echoproject")
public class EchoApplication {

	public static void main(String[] args) {
        FirebaseInitializer.initialize();
        SpringApplication.run(EchoApplication.class, args);
	}
}
