package com.projetos.financial_planner.services.exceptions;

@SuppressWarnings("serial")
public class EmailException extends RuntimeException {

    public EmailException(String msg) {
        super(msg);
    }
}