package com.kubepay.webflux.movie;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovieService {
	
	Flux<Movie> list();

    Flux<Movie> findByRating(String rating);

    Mono<Movie> update(String id, MovieRequest movieRequest);

    Mono<Movie> create(Mono<MovieRequest> movieRequest);

    Mono<Movie> read(String id);

    Mono<Movie> delete(String id);

}
