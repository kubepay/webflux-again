package com.kubepay.webflux.trading;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Mono;

public interface TradingUserRepository extends ReactiveMongoRepository<TradingUser, String> {

	Mono<TradingUser> findByUserName(String userName);

}
