package jp.co.sss.java_ec_program;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"jp.co.sss.java_ec_program"})
public class JavaEcProgramApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaEcProgramApplication.class, args);
	}

}
