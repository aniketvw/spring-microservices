package se.aw.microservices.core.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import se.aw.microservices.core.product.persistence.ProductEntity;

@SpringBootApplication
@ComponentScan("se.aw")

public class ProductServiceApplication {

	private static final Logger LOG= LoggerFactory.getLogger(ProductServiceApplication.class);


	public static void main(String[] args) {

		ConfigurableApplicationContext ctx = SpringApplication.run(ProductServiceApplication.class, args);

		String mongoDbHost= ctx.getEnvironment().getProperty("spring.data.mongodb.host");
		String mongodDbPort = ctx.getEnvironment().getProperty("spring.data.mongodb.port");

		LOG.info("Connected to MongoDb: " + mongoDbHost + ":" + mongodDbPort);


	}

	@Autowired
	MongoOperations mongoTemplate;

	@EventListener(ContextRefreshedEvent.class)
	public void initIndicesAfterStartup(){
		//Retrieve metadata about MongoDB entities,metadata is used to resolve what indexes are there for productEntity
		MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext=
				mongoTemplate.getConverter().getMappingContext();

		//Get the required Indexes, fields annotated with @Indexed
		IndexResolver resolver= new MongoPersistentEntityIndexResolver(mappingContext);

		//Get access to Indexed operations
		IndexOperations indexOps= mongoTemplate.indexOps(ProductEntity.class);

		//Verify if indexes are created...Resolver took from entity, indexOps takes index from mongoDb.
		//If index is not created it will be created
		resolver.resolveIndexFor(ProductEntity.class).forEach(indexOps::ensureIndex);

	}
}
