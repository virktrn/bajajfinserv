package com.bfhl.controller;

import com.bfhl.dto.BfhlRequest;
import com.bfhl.dto.BfhlResponse;
import com.bfhl.service.BfhlService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Single-route controller exposing POST /bfhl.
 *
 * All business logic is delegated to {@link BfhlService}; this class
 * is intentionally thin – it handles only HTTP concerns.
 */
@RestController
@RequestMapping("/bfhl")
@CrossOrigin(origins = "*")   // allows calls from any origin (handy for testing)
public class BfhlController {

    private final BfhlService bfhlService;

    public BfhlController(BfhlService bfhlService) {
        this.bfhlService = bfhlService;
    }


    @PostMapping
    public ResponseEntity<BfhlResponse> process(@Valid @RequestBody BfhlRequest request) {
        BfhlResponse response = bfhlService.process(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }

}
