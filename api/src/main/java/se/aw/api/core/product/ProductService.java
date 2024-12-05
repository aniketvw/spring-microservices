package se.aw.api.core.product;


import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

public interface ProductService {

    @GetMapping(
            value="/product/{productId}",
            produces = "application/json")
    Mono<Product> getProduct(@PathVariable int productId);


    Mono<Product> createProduct( Product body);


    void deleteProduct(@PathVariable int productId);



}
