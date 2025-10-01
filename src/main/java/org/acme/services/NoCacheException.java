package org.acme.services;

public class NoCacheException extends RuntimeException {
    final static long serialVersionUID = 1L;

    public NoCacheException() {
        super();
    }
}
