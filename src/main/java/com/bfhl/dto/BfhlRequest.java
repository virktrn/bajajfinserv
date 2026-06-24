package com.bfhl.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * Request DTO – wraps the incoming data array.
 */
@Data
public class BfhlRequest {

    @NotNull(message = "data field must not be null")
    @JsonProperty("data")
    private List<String> data;
}
