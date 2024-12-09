package se.aw.microservices.core.product.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.aw.api.core.product.Product;
import se.aw.api.core.product.ProductService;
import se.aw.api.event.Event;
import se.aw.util.exceptions.EventProcessingException;

import java.util.function.Consumer;

@Configuration
public class MessageProcessorConfig {
    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessorConfig.class);

    private final ProductService productService;

    @Autowired
    public MessageProcessorConfig(ProductService productService){

        this.productService=productService;

    }

    @Bean
    public Consumer<Event<Integer, Product>> messageProcessor(){

        return event->{
            switch(event.getEventType()){
                case CREATE:
                    Product product=event.getData();
                    productService.createProduct(product).block();
                    break;
                case DELETE:
                    int productId= event.getKey();
                    productService.deleteProduct(productId);
                    break;
                default:
                    String errorMessage="Incorrect event type:"+
                            event.getEventType()+", expected CREATE or DELETE event";
                    throw new EventProcessingException(errorMessage);

            }
            LOG.info("Message processing done!");

        };

    }

}
