package com.lotty.wishlysystemapi.payload.response;

public class JwtResponse {

    private String accessToken;
    private String tokenType = "Bearer";

    public JwtResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    // геттеры и сеттеры
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}