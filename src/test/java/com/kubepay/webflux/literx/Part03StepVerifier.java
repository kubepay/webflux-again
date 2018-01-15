package com.kubepay.webflux.literx;

import java.util.function.Supplier;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

/**
* Learn how to use StepVerifier to test Mono, Flux or any other kind of Reactive Streams Publisher.
*
* @author Sebastien Deleuze
* @see <a href="http://projectreactor.io/docs/test/release/api/reactor/test/StepVerifier.html">StepVerifier Javadoc</a>
*/
public class Part03StepVerifier {

//========================================================================================

	// TODO Use StepVerifier to check that the flux parameter emits "foo" and "bar" elements then completes successfully.
	void expectFooBarComplete(Flux<String> flux) {
		
		  Flux<String> source = Flux.just("foo", "bar"); 

		  StepVerifier.create(source)
		    .expectNext("foo") 
		    .expectNext("bar")
		    .verifyComplete();
	}

//========================================================================================

	// TODO Use StepVerifier to check that the flux parameter emits "foo" and "bar" elements then a RuntimeException error.
	void expectFooBarError(Flux<String> flux) {
		fail();
	}

//========================================================================================

	// TODO Use StepVerifier to check that the flux parameter emits a User with "swhite"username
	// and another one with "jpinkman" then completes successfully.
	void expectSkylerJesseComplete(Flux<User> flux) {
		fail();
	}

//========================================================================================

	// TODO Expect 10 elements then complete and notice how long the test takes.
	void expect10Elements(Flux<Long> flux) {
		fail();
	}

//========================================================================================

	// TODO Expect 3600 elements at intervals of 1 second, and verify quicker than 3600s
	// by manipulating virtual time thanks to StepVerifier#withVirtualTime, notice how long the test takes
	void expect3600Elements(Supplier<Flux<Long>> supplier) {
		fail();
	}

	private void fail() {
		throw new AssertionError("workshop not implemented");
	}
	
	static class User {}

}