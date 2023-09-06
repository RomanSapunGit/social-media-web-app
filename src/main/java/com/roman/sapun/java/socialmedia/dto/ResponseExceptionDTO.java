package com.roman.sapun.java.socialmedia.dto;


import java.sql.Timestamp;


public record ResponseExceptionDTO(String causedBy, Timestamp timestamp, String message) {
}
