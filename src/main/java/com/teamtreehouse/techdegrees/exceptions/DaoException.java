package com.teamtreehouse.techdegrees.exceptions;

public class DaoException extends RuntimeException {
    private final Exception exception;

    public DaoException(Exception original, String message) {
        super(message);
        this.exception = original;
    }
}
