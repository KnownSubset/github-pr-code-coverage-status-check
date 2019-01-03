package io.jenkins.plugins.gcr.parsers;

public class ParserException extends Exception {

    public ParserException(String message, Throwable ex) {
        super(message, ex);
    }

}
