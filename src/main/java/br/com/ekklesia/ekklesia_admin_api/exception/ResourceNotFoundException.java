package br.com.ekklesia.ekklesia_admin_api.exception;

public class ResourceNotFoundException extends RuntimeException {

    public  ResourceNotFoundException(String message) {
        super(message);
    }
}
