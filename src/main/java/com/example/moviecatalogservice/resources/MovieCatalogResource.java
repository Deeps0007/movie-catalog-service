package com.example.moviecatalogservice.resources;

import com.example.moviecatalogservice.models.CatalogItem;
import com.example.moviecatalogservice.models.Movie;
import com.example.moviecatalogservice.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalogs")
public class MovieCatalogResource {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalogs(@PathVariable("userId") String userId) {

        //get all the rating list by call the rating-data-service
        UserRating ratings = restTemplate.getForObject("http://rating-info-service/ratings/user/" + userId, UserRating.class);
        return ratings.getUserRating().stream().map(rating -> {
            //for Each movie Id , call movie Info service and get the details
            Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);

            // Put them all together
            return new CatalogItem(movie.getName(), "Description", rating.getRating());
        })
                .collect(Collectors.toList());
    }
}
/*   return Collections.singletonList(
                new CatalogItem("Suryavanshi", "Action", 4)
        );*/

/*Movie movie= webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8082/movies/" + rating.getMovieId() )
                    .retrieve()
                    .bodyToMono(Movie.class)
                    .block();
            */
