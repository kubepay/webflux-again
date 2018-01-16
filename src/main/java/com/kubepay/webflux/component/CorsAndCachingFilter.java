package com.kubepay.webflux.component;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class CorsAndCachingFilter implements WebFilter {
	
	
	public static final int DEFAULT_DAYS_TO_LIVE = 1461; // 4 years
    public static final long DEFAULT_SECONDS_TO_LIVE = TimeUnit.DAYS.toMillis(DEFAULT_DAYS_TO_LIVE);
    // We consider the last modified date is the start up time of the server
    public final static Long LAST_MODIFIED = System.currentTimeMillis();
    private long cacheTimeToLive = DEFAULT_SECONDS_TO_LIVE;
	

	@Override
	public Mono<Void> filter(final ServerWebExchange serverWebExchange, final WebFilterChain webFilterChain) {

		//Caching
		serverWebExchange.getResponse().getHeaders().add("Pragma", "cache");
		serverWebExchange.getResponse().getHeaders().add("Cache-Control", "max-age=" + cacheTimeToLive + ", public");
		serverWebExchange.getResponse().getHeaders().add("Access-Control-Allow-Origin", "*");
		serverWebExchange.getResponse().getHeaders().add("Last-Modified", LAST_MODIFIED.toString());
		
		//CORS
		serverWebExchange.getResponse().getHeaders().add("Access-Control-Allow-Methods",
				"GET, PUT, POST, DELETE, OPTIONS");
		serverWebExchange.getResponse().getHeaders().add("Access-Control-Allow-Headers",
				"DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,"
						+ "If-Modified-Since,Cache-Control,Content-Type,Content-Range,Range");
		
		if (serverWebExchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
			serverWebExchange.getResponse().getHeaders().add("Access-Control-Max-Age", "1728000");
			serverWebExchange.getResponse().setStatusCode(HttpStatus.NO_CONTENT);
			return Mono.empty();
		} else {
			serverWebExchange.getResponse().getHeaders().add("Access-Control-Expose-Headers",
					"DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,"
							+ "If-Modified-Since,Cache-Control,Content-Type,Content-Range,Range");
			return webFilterChain.filter(serverWebExchange);
		}
	}

}
