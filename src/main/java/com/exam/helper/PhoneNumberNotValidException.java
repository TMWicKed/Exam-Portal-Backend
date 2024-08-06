package com.exam.helper;

public class PhoneNumberNotValidException extends RuntimeException {
    public PhoneNumberNotValidException() {
        super("User with this Username is already there in DB !! try with another one");
    }

    public PhoneNumberNotValidException(String msg)
    {
        super(msg);
    }
}
