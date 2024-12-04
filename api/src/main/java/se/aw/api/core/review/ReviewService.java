package se.aw.api.core.review;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ReviewService {

    @GetMapping(
            value    = "/review",
            produces = "application/json")
    Flux<Review> getReviews(@RequestParam(value = "productId", required = true) int productId);


    Review createReview(@RequestBody Review body);


    void deleteReviews(@RequestParam(value = "productId", required = true)  int productId);


}
