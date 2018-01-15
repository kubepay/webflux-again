package com.kubepay.webflux;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumingThat;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class WebfluxAgainApplicationTests {
	
	@Test
	void lambdaExpressions() {
		
		
	    assertTrue(Stream.of(1, 2, 3)
	      .mapToInt(i -> i)
	      .sum() > 5, () -> "Sum should be greater than 5");
	}
	
	@Test
	 void groupAssertions() {
	     int[] numbers = {0, 1, 2, 3, 4};
	     assertAll("numbers",
	         () -> assertEquals(numbers[0], 1),
	         () -> assertEquals(numbers[3], 3),
	         () -> assertEquals(numbers[4], 1)
	     );
	 }
	
	@Test
	void trueAssumption() {
	    assumeTrue(5 > 1);
	    assertEquals(5 + 2, 7);
	}
	 
	@Test
	void falseAssumption() {
	    assumeFalse(5 < 1);
	    assertEquals(5 + 2, 7);
	}
	 
	@Test
	void assumptionThat() {
	    String someString = "Just a string";
	    assumingThat(
	        someString.equals("Just a string"),
	        () -> assertEquals(2 + 2, 4)
	    );
	}
	
	@Test
	void shouldThrowException() {
	    Throwable exception = assertThrows(UnsupportedOperationException.class, () -> {
	      throw new UnsupportedOperationException("Not supported");
	    });
	    assertEquals(exception.getMessage(), "Not supported");
	}
	
	@Test
	@Disabled
	void disabledTest() {
	    assertTrue(false);
	}

	@Tag("Test case")
	class TaggedTest {
	 
	    @Test
	    @Tag("Method")
	    void testMethod() {
	        assertEquals(2+2, 4);
	    }
	}

}
