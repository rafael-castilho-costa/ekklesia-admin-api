package br.com.ekklesia.ekklesia_admin_api.exception;

public class BusinessException extends  RuntimeException{
    public BusinessException(String message)
    {
        super(message);
    }
}
