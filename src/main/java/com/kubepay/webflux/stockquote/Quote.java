package com.kubepay.webflux.stockquote;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Instant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter@Setter
@NoArgsConstructor
public class Quote {
	
	private static final MathContext MATH_CONTEXT = new MathContext(2);

	private String ticker;

	private BigDecimal price;

	private Instant instant;

	public Quote(final String ticker, final BigDecimal price) {
		this.ticker = ticker;
		this.price = price;
	}

	public Quote(final String ticker, final Double price) {
		this(ticker, new BigDecimal(price, MATH_CONTEXT));
	}

}
