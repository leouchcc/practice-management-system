package com.practice.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String realName;
    private String studentId;
    private String phone;
    private String email;
    private String role;
}
