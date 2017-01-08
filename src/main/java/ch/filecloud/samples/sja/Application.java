package ch.filecloud.samples.sja;

import ch.filecloud.samples.sja.aop.JpaLifecycleAspect;
import ch.filecloud.samples.sja.aop.ReFormatAspect;
import ch.filecloud.samples.sja.event.PostPersistEvent;
import ch.filecloud.samples.sja.jpa.Customer;
import ch.filecloud.samples.sja.jpa.CustomerRepository;
import org.aspectj.lang.Aspects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

/**
 * Created by domi on 10/27/14.
 */
@Configuration
@EnableAutoConfiguration
@EnableSpringConfigured
public class Application {

	private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

	@Bean
	public ReFormatAspect reFormatAspect() {
		return Aspects.aspectOf(ReFormatAspect.class);
	}

	@Bean
	public JpaLifecycleAspect jpaLifecycleAspect() {
		return Aspects.aspectOf(JpaLifecycleAspect.class);
	}

	@TransactionalEventListener(fallbackExecution = true)
	public void handlePostPersistEvent(PostPersistEvent event) {
		Object source = event.getSource();
		if (source instanceof Customer) {
			Customer customer = (Customer) source;
			LOGGER.info("[PostPersistEvent] detected for {}", customer);
		} else {
			LOGGER.info("[PostPersistEvent] detected");
		}
	}

	public static void main(String[] args) {

		ConfigurableApplicationContext context = SpringApplication.run(Application.class);
		CustomerRepository repository = context.getBean(CustomerRepository.class);

		// save a couple of customers
		repository.save(new Customer("Jack", "Bauer"));
		repository.save(new Customer("Chloe", "O'Brian"));
		repository.save(new Customer("Kim", "Bauer"));
		repository.save(new Customer("David", "Palmer"));
		repository.save(new Customer("Michelle", "Dessler"));

		// fetch all customers
		Iterable<Customer> customers = repository.findAll();
		LOGGER.info("Customers found with findAll():");
		LOGGER.info("-------------------------------");
		for (Customer customer : customers) {
			LOGGER.info(customer.toString());
		}

		// fetch an individual customer by ID
		Customer customer = repository.findOne(1L);
		LOGGER.info("Customer found with findOne(1L):");
		LOGGER.info("--------------------------------");
		LOGGER.info(customer.toString());

		// fetch customers by last name
		List<Customer> bauers = repository.findByLastName("Bauer");
		LOGGER.info("Customer found with findByLastName('Bauer'):");
		LOGGER.info("--------------------------------------------");
		for (Customer bauer : bauers) {
			LOGGER.info(bauer.toString());
		}

		context.close();
	}
}