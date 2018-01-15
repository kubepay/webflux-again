package com.kubepay.webflux.movie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class MovieRestController {

    @Autowired
    private MovieService movieService;

    @GetMapping(value = "/movies")
    public Flux<ResponseEntity<Movie>> list() {

        return movieService.list().map(m -> new ResponseEntity<>(m, HttpStatus.OK));
    }

    @GetMapping(value = "/moviesByRating")
    public Flux<ResponseEntity<Movie>> findByRating(
            @RequestParam(value = "rating", required = false) final String rating) {

        return movieService.findByRating(rating)
                .map(m -> new ResponseEntity<>(m, HttpStatus.OK));

    }

    @GetMapping("/movies/{movieId}")
    public Mono<ResponseEntity<Movie>> read(
            @PathVariable("movieId") final String movieId) {

        return movieService.read(movieId)
                .map(m -> new ResponseEntity<>(m, HttpStatus.OK));
    }

    @DeleteMapping("/movies/{movieId}")
    public Mono<ResponseEntity<Movie>> delete(
            @PathVariable("movieId") final String movieId) {

        return movieService.delete(movieId)
                .map(m -> new ResponseEntity<>(m, HttpStatus.OK));
    }

    @PutMapping("/movies/{movieId}")
    public Mono<ResponseEntity<Movie>> update(
            @PathVariable("movieId") final String movieId,
            @RequestBody final MovieRequest movieRequest) {

        return movieService.update(movieId, movieRequest)
                .map(m -> new ResponseEntity<>(m, HttpStatus.OK));

    }

    @PostMapping("/movies")
    public Mono<ResponseEntity<Movie>> create(
            @RequestBody final Mono<MovieRequest> movieRequest) {

        return movieService.create(movieRequest)
                .map(m -> new ResponseEntity<>(m, HttpStatus.OK));

    }

}
