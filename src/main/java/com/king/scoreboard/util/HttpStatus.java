package com.king.scoreboard.util;

public enum HttpStatus {

    OK(200), BAD_REQUEST(400);

    private int code;

    HttpStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    @Override
    public String toString() {
        return "HttpCode{" + name() + " " + code + '}';
    }
}
