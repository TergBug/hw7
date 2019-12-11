package org.mycode.exceptions;

public class InvalidRepoFileException extends Exception {
    public InvalidRepoFileException() {
        super();
    }
    public InvalidRepoFileException(String message) {
        super(message);
    }
}
