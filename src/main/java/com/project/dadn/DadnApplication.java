package com.project.dadn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class DadnApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();

        System.setProperty("SPRING_DATASOURCE_URL", dotenv.get("SPRING_DATASOURCE_URL"));
        System.setProperty("SPRING_DATASOURCE_USERNAME", dotenv.get("SPRING_DATASOURCE_USERNAME"));
        System.setProperty("SPRING_DATASOURCE_PASSWORD", dotenv.get("SPRING_DATASOURCE_PASSWORD"));

        SpringApplication.run(DadnApplication.class, args);
    }

//	public static void main(String[] args) {
//		ApplicationContext applicationContext = SpringApplication
//				.run(DadnApplication.class);
////		RabbitMQProducer messageProducer = applicationContext
////				.getBean(RabbitMQProducer.class);
////		messageProducer.sendMessage("Hello Viet Le");
}

//	private CriteriaBuilderFactory cbf;
//	private EntityManager em;
//	public DadnApplication(BlazeConfig config) {
//		this.cbf = config.getCbf();
//		this.em = config.getEm();
//	}


