package com.yasas;

import java.util.Collections;

//import org.apache.tomcat.util.scan.StandardJarScanner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//import net.guides.springboot2.springboot2webappjsp.Springboot2WebappJspApplication;

@SpringBootApplication
//@ComponentScan(basePackages = {"com.yasas.services"})
@EnableJpaRepositories("com.yasas.repository")
public class YasasSpringCrudApplication {

	public static void main(String[] args) {
		//SpringApplication.run(YasasSpringCrudApplication.class, args);
		SpringApplication app = new SpringApplication(YasasSpringCrudApplication.class);
		app.setDefaultProperties(Collections.singletonMap("server.servlet.context-path", "/"));
		app.run(args);
              //App runs third commit
              // Fourth commit is here 
	      // Branch feature feature1 commit is here
	      // Branch feature2 commit is here
	      // Branch feature3 commit is here
              // Branch feature3 First Commit is here 
	}

}
