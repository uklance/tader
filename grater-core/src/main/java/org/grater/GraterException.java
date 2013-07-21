package org.grater;

public class GraterException extends RuntimeException {
	public GraterException() {
		super();
	}

	public GraterException(String message, Throwable cause) {
		super(message, cause);
	}

	public GraterException(String message) {
		super(message);
	}

	public GraterException(Throwable cause) {
		super(cause);
	}
}
