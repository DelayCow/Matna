package com.oopsw.matna.jwt;

public interface JwtProperties {
    int EXPIRES_IN = 1000 * 60 * 60 ; //1h
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";
    int TOKEN_REFRESH_THRESHOLD = 1000 * 60 * 15; //15m
}
