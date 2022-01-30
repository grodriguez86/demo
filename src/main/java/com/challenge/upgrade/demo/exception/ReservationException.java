package com.challenge.upgrade.demo.exception;

public class ReservationException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 5629901113497598092L;

    public ReservationException(String message, Throwable cause){
        super(message, cause);
    }

    public ReservationException(String message){
        super(message);
    }

}