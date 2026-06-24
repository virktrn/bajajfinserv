package com.bfhl.service;

import com.bfhl.dto.BfhlRequest;
import com.bfhl.dto.BfhlResponse;

/**
 * Contract for the core BFHL business logic.
 * Having an interface here keeps the controller decoupled from the implementation
 * and makes unit-testing with mocks straightforward.
 */
public interface BfhlService {

    /**
     * Processes the incoming data array and returns the categorised response.
     *
     * @param request validated request DTO containing the data array
     * @return fully populated {@link BfhlResponse}
     */
    BfhlResponse process(BfhlRequest request);
}
