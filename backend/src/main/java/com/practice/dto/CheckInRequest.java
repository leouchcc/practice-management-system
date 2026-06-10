package com.practice.dto;

import lombok.Data;

@Data
public class CheckInRequest {
    private String location;
    private String address;
}
