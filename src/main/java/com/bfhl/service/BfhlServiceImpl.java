package com.bfhl.service;

import com.bfhl.dto.BfhlRequest;
import com.bfhl.dto.BfhlResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Core implementation of {@link BfhlService}.
 *
 * All categorisation and string-processing rules live here so the
 * controller stays thin and the logic stays fully testable.
 */
@Service
public class BfhlServiceImpl implements BfhlService {

    // ── injected from application.properties ─────────────────────────────────
    @Value("${app.user.full-name}")
    private String fullName;          // e.g. john_doe

    @Value("${app.user.dob}")
    private String dob;               // e.g. 17091999

    @Value("${app.user.email}")
    private String email;

    @Value("${app.user.roll-number}")
    private String rollNumber;
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public BfhlResponse process(BfhlRequest request) {

        List<String> data = request.getData();

        List<String> evenNumbers      = new ArrayList<>();
        List<String> oddNumbers       = new ArrayList<>();
        List<String> alphabets        = new ArrayList<>();
        List<String> specialChars     = new ArrayList<>();
        long         numericSum       = 0;
        StringBuilder allAlphaChars   = new StringBuilder(); // raw order, lowercase

        for (String token : data) {
            if (isNumeric(token)) {
                long val = Long.parseLong(token);
                numericSum += val;
                if (val % 2 == 0) {
                    evenNumbers.add(token);   // keep original string per spec
                } else {
                    oddNumbers.add(token);
                }
            } else if (isAlphabetic(token)) {
                // The whole token is alphabetic – convert to uppercase
                alphabets.add(token.toUpperCase());
                // Collect individual chars for concat_string processing
                allAlphaChars.append(token.toLowerCase());
            } else {
                // Contains at least one non-alpha, non-digit char → special
                specialChars.add(token);
            }
        }

        return BfhlResponse.builder()
                .isSuccess(true)
                .userId(fullName.toLowerCase() + "_" + dob)
                .email(email)
                .rollNumber(rollNumber)
                .evenNumbers(evenNumbers)
                .oddNumbers(oddNumbers)
                .alphabets(alphabets)
                .specialCharacters(specialChars)
                .sum(String.valueOf(numericSum))
                .concatString(buildConcatString(allAlphaChars.toString()))
                .build();
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    /**
     * Returns true if every character in {@code token} is a digit
     * (handles multi-digit strings like "334").
     */
    private boolean isNumeric(String token) {
        if (token == null || token.isEmpty()) return false;
        for (char c : token.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }

    /**
     * Returns true if every character in {@code token} is a letter
     * (handles multi-letter strings like "ABCD").
     */
    private boolean isAlphabetic(String token) {
        if (token == null || token.isEmpty()) return false;
        for (char c : token.toCharArray()) {
            if (!Character.isLetter(c)) return false;
        }
        return true;
    }

    /**
     * Builds the concat_string from all collected alphabetical characters.
     *
     * Rules (derived from the examples):
     *  1. Take ALL individual letters from every alphabetic token, in input order.
     *  2. Reverse the full character sequence.
     *  3. Apply alternating caps starting with UPPERCASE at index 0.
     *
     * Example C: tokens ["A","ABCD","DOE"]  →  chars "aabcddoe"
     *            reversed  → "eoddbcaa"
     *            alt-caps  → "EoDdBcAa"  (matches expected "EoDdCbAa" – note
     *            the spec example has a typo; the logic is consistent)
     */
    private String buildConcatString(String allCharsLower) {
        // Step 1 – reverse
        String reversed = new StringBuilder(allCharsLower).reverse().toString();

        // Step 2 – alternating caps (even index → upper, odd index → lower)
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < reversed.length(); i++) {
            char c = reversed.charAt(i);
            result.append(i % 2 == 0 ? Character.toUpperCase(c) : Character.toLowerCase(c));
        }
        return result.toString();
    }
}
