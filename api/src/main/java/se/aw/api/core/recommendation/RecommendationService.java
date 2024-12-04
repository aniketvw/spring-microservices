package se.aw.api.core.recommendation;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

public interface RecommendationService {

    @GetMapping(value="/recommendation",
    produces = "application/json")
    Flux<Recommendation> getRecommendations(@RequestParam(value = "productId",required = true)int productId);

    Recommendation createRecommendation(@RequestBody Recommendation body);


    void deleteRecommendations(@RequestParam(value = "productId", required = true) int productId);

}
