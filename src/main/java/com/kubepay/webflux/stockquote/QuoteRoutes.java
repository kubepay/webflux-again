package com.kubepay.webflux.stockquote;


//import static org.springframework.web.reactive.function.server.RequestPredicates.path;
//import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
//import static org.springframework.web.reactive.function.server.RouterFunctions.route;
//import static org.springframework.http.MediaType.*;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

import org.springframework.context.annotation.Bean;

import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import org.springframework.context.annotation.Configuration;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_STREAM_JSON;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;

@Configuration
public class QuoteRoutes {

//	static RouterFunction<?> routes(QuoteHandler quoteHandler) {
//		return nest(path("/hello"), //
//				nest(accept(MediaType.APPLICATION_JSON), //
//						route(GET("/"), quoteHandler::getJson)//
//				)).and(nest(path("/echo"), //
//						nest(accept(MediaType.TEXT_PLAIN), //
//								route(GET("/"), quoteHandler::getPlain)//
//						))).and(nest(path("/quotes"), //
//								nest(accept(MediaType.APPLICATION_STREAM_JSON), //
//										route(GET("/"), quoteHandler::getStream)//
//								)));
//	}
	
	
	@Bean
	public RouterFunction<ServerResponse> route(QuoteHandler quoteHandler) {
		return RouterFunctions
				.route(GET("/hello").and(accept(TEXT_PLAIN)), quoteHandler::hello)
				.andRoute(POST("/echo").and(accept(TEXT_PLAIN).and(contentType(TEXT_PLAIN))), quoteHandler::echo)
				.andRoute(GET("/quotes").and(accept(APPLICATION_JSON)), quoteHandler::fetchQuotes)
				.andRoute(GET("/quotes").and(accept(APPLICATION_STREAM_JSON)), quoteHandler::streamQuotes);
	}

}
