package com.bfhl.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Returned when the request fails validation or an unexpected error occurs.
 */
@Data
@AllArgsConstructor
public class ErrorResponse {

    @JsonProperty("is_success")
    private boolean isSuccess;

    @JsonProperty("error")
    private String error;
}
