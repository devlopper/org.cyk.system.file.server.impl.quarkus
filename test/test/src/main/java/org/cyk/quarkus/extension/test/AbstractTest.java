package org.cyk.quarkus.extension.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractTest {

	@BeforeEach
	public void listenBeforeEach() {
		__listenBeforeEach__();
	}
	
	protected void __listenBeforeEach__() {
		
	}
	
	@AfterEach
	public void listenAfterEach() {
		__listenAfterEach__();
	}
	
	protected void __listenAfterEach__() {
		
	}
}