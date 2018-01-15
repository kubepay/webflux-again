package com.kubepay.webflux.stockquote;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import static java.time.Duration.ofMillis;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_STREAM_JSON;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class QuoteHandler {
	
	private final Flux<Quote> quoteStream;

	@Autowired
	public QuoteHandler(QuoteGenerator quoteGenerator){
		this.quoteStream=quoteGenerator.fetchQuoteStream(ofMillis(200)).share();
	}
	

	public Mono<ServerResponse> hello(ServerRequest serverRequest) {

		return ok().contentType(TEXT_PLAIN).body(fromObject("Hello Spring!"));
	}

	public Mono<ServerResponse> echo(ServerRequest serverRequest) {

		return ok().contentType(TEXT_PLAIN).body(serverRequest.bodyToMono(String.class), String.class);
	}
	
	
	public Mono<ServerResponse> streamQuotes(ServerRequest serverRequest) {

		return ok().contentType(APPLICATION_STREAM_JSON).body(this.quoteStream, Quote.class);
	}
	
	public Mono<ServerResponse> fetchQuotes(ServerRequest serverRequest) {
		int size = Integer.parseInt(serverRequest.queryParam("size").orElse("10"));
		return ok().contentType(APPLICATION_JSON).body(this.quoteStream.take(size), Quote.class);
	}

}
