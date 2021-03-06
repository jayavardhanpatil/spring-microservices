package io.spring.moviecatalogservice.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import io.spring.moviecatalogservice.models.CatalogItem;
import io.spring.moviecatalogservice.models.Movie;
import io.spring.moviecatalogservice.models.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MovieInfo {

    @Autowired
    private RestTemplate restTemplate;


    @HystrixCommand(fallbackMethod = "getFallBackCatalogItem",
                    commandProperties = {
                        @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000"),
                            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
                        @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
                            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "3000")
                    })
    public CatalogItem getCatalogItem(Rating rating) {
        Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);
        return new CatalogItem(movie.getName(), movie.getOverView(), rating.getRating());
    }

    public CatalogItem getFallBackCatalogItem(Rating rating) {
        return new CatalogItem("No Movie", "Movie details did not find", rating.getRating());
    }

}
