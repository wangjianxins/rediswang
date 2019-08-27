package com.wang.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
		System.out.println("  .   ____          _            __ _ _\n" +
				" /\\\\ / ___'_ __ _ _(_)_ __  __ _ \\ \\ \\ \\\n" +
				"( ( )\\___ | '_ | '_| | '_ \\/ _` | \\ \\ \\ \\\n" +
				" \\\\/  ___)| |_)| | | | | || (_| |  ) ) ) )\n" +
				"  '  |____| .__|_| |_|_| |_\\__, | / / / /\n" +
				" =========|_|==============|___/=/_/_/_/");
	}

}
