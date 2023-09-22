package com.codenames.backend.controller.dto;

public class JoinRequest {
    
    private Integer sessionId;
    private String pseudo;

    public JoinRequest() {
        
    }

    public JoinRequest(Integer sessionId, String pseudo) {
        this.sessionId = sessionId;
        this.pseudo = pseudo;
    }

    public Integer getSessionId() {
        return this.sessionId;
    }
    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public String getPseudo() {
        return this.pseudo;
    }
    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }
}
