package com.example.demo;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AccessTemporarilyRestrictedException extends ResponseStatusException {

    public AccessTemporarilyRestrictedException() {
        super(HttpStatus.FORBIDDEN, "Access temporarily restricted");
    }

}
