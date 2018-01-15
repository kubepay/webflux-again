package com.kubepay.webflux.movie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MovieServiceImpl implements MovieService {

	@Autowired
	private MovieRepository movieRepository;

	@Override
	public Flux<Movie> list() {
		return movieRepository.findAll();
	}

	@Override
	public Flux<Movie> findByRating(final String rating) {
		return movieRepository.findByRating(rating);
	}

	@Override
	public Mono<Movie> update(String id, MovieRequest movieRequest) {

		movieRepository.findById(id).map(existingMovie -> {
			if (movieRequest.getDescription() != null) {
				existingMovie.setDescription(movieRequest.getDescription());
			}
			if (movieRequest.getRating() != null) {
				existingMovie.setRating(movieRequest.getRating());
			}
			if (movieRequest.getTitle() != null) {
				existingMovie.setTitle(movieRequest.getTitle());
			}
			return existingMovie;
		}).subscribe(movieRepository::save);
		return null;
	}

	@Override
	public Mono<Movie> create(Mono<MovieRequest> movieRequest) {

		movieRequest.map(newMovie -> {

			Movie movie = new Movie();

			if (newMovie.getDescription() != null) {
				movie.setDescription(newMovie.getDescription());
			}
			if (newMovie.getRating() != null) {
				movie.setRating(newMovie.getRating());
			}
			if (newMovie.getTitle() != null) {
				movie.setTitle(newMovie.getTitle());
			}

			return movie;

		}).subscribe(movieRepository::save);
		return null;
	}

	@Override
	public Mono<Movie> read(String id) {
		return movieRepository.findById(id);
	}

	@Override
	public Mono<Movie> delete(String id) {
		Mono<Movie> movie = movieRepository.findById(id);
		movieRepository.deleteById(id);
		return movie;

//		return movieRepository.findById(id).flatMap(oldValue -> movieRepository.delete(id).then(Mono.just(oldValue)))
//				.singleOrEmpty();
//
//		return movieRepository.findById(id).untilOther(movieRepository.deleteById(id));
	}
}
