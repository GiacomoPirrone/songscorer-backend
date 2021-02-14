package com.songscorer.songscorer.exceptions;

public class SymphonyzeException extends RuntimeException {

    // For displaying given message on encounter with exception
    public SymphonyzeException(String exMessage) {
        super(exMessage);
    }

    // For displaying given message and the exception type encountered
    public SymphonyzeException(String exMessage, Exception exception) {
        super(exMessage, exception);
    }
}