package com.onlineclassroom.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Represents error details to be returned in the response when an error occurs.
 * It provides relevant details such as the timestamp of
 *  * the error, a brief error message, and additional details about the error.
 *
 * @author Md Moinul Islam Sourav
 * @since 2025-02-02
 */
@AllArgsConstructor
@Getter
@Setter
public class ErrorDetails {
    private Date timestamp;
    private String message;
    private String details;
}
