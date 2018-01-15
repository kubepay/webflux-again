package com.kubepay.webflux.trading;

import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import java.time.Duration;
import reactor.core.publisher.Mono;

public class EchoWebSocketHandler implements WebSocketHandler {

	@Override
	public Mono<Void> handle(WebSocketSession session) {
		return session.send(session.receive().delayElements(Duration.ofSeconds(1)).log());
	}

}
