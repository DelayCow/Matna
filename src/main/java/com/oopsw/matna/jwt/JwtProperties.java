package com.oopsw.matna.jwt;

public interface JwtProperties {
    String SECRET = "matna";
    int EXPIRATION_TIME = 1000 * 60 * 5;
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";
}