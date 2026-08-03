package com.Bhawesh.expense_tracker.exception;

public class StatementParsingException extends RuntimeException {
    public StatementParsingException(String message) {
        super(message);
    }

    public StatementParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
