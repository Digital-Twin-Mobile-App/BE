package com.project.dadn;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.project.dadn.components.rabbitmq.RabbitMQProducer;
import com.project.dadn.configurations.BlazeConfig;
import jakarta.persistence.EntityManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DadnApplication {

	public static void main(String[] args) {
		SpringApplication.run(DadnApplication.class, args);

		RabbitMQProducer messageProducer = applicationContext
				.getBean(RabbitMQProducer.class);
		messageProducer.sendMessage("Hello Techmaster");
	}

//	private CriteriaBuilderFactory cbf;
//	private EntityManager em;
//	public DadnApplication(BlazeConfig config) {
//		this.cbf = config.getCbf();
//		this.em = config.getEm();
//	}

}
