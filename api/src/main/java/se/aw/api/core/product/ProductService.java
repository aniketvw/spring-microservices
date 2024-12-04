package se.aw.api.core.product;


import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

public interface ProductService {

    @GetMapping(
            value="/product/{productId}",
            produces = "application/json")
    Mono getProduct(@PathVariable int productId);


    Product createProduct(@RequestBody Product body);


    void deleteProduct(@PathVariable int productId);



}
