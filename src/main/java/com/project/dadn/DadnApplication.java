package com.project.dadn;

import com.project.dadn.components.rabbitmq.RabbitMQProducer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class DadnApplication {

//	public static void main(String[] args) {
//		SpringApplication.run(DadnApplication.class, args);
//
//		RabbitMQProducer messageProducer = applicationContext
//				.getBean(RabbitMQProducer.class);
//		messageProducer.sendMessage("Hello Techmaster");
//	}

	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication
				.run(DadnApplication.class);
		RabbitMQProducer messageProducer = applicationContext
				.getBean(RabbitMQProducer.class);
		messageProducer.sendMessage("Hello Viet Le");
	}

//	private CriteriaBuilderFactory cbf;
//	private EntityManager em;
//	public DadnApplication(BlazeConfig config) {
//		this.cbf = config.getCbf();
//		this.em = config.getEm();
//	}

}
