package com.erywim.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChatMemory implements Serializable {
    private Long id;
    private Long userId;
    private String message;
    private String role;

    public ChatMemory() {
    }

    public ChatMemory(Long userId, String message,String role) {
        this.userId = userId;
        this.message = message;
        this.role = role;
    }
}
