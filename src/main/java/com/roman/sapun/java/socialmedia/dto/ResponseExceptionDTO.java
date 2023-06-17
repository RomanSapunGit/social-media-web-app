package com.roman.sapun.java.socialmedia.dto;


import java.sql.Timestamp;


public record ResponseExceptionDTO(String exception, Timestamp timestamp, String message) {
}
