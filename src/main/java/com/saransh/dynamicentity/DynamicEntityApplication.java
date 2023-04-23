package com.saransh.dynamicentity;

import com.saransh.dynamicentity.daoCreator.ConfigParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DynamicEntityApplication implements CommandLineRunner {

	@Autowired
	ConfigParser parser;

	public static void main(String[] args) {
		SpringApplication.run(DynamicEntityApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		parser.involkingMethod();
	}
}
