package com.shu.json.schema.examples.exception;

public class JsonSchemaLoadingFailedException extends RuntimeException {

    private static final long serialVersionUID = 8457056685229663273L;

	public JsonSchemaLoadingFailedException(String message) {
        super(message);
    }

    public JsonSchemaLoadingFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
