package com.bfhl;

import com.bfhl.dto.BfhlRequest;
import com.bfhl.dto.BfhlResponse;
import com.bfhl.service.BfhlServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for BfhlServiceImpl.
 *
 * These tests verify every output field against the three examples
 * given in the spec, plus additional edge cases.
 */
class BfhlServiceImplTest {

    private BfhlServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new BfhlServiceImpl();
        // Inject @Value fields directly (no Spring context needed)
        ReflectionTestUtils.setField(service, "fullName",   "john_doe");
        ReflectionTestUtils.setField(service, "dob",        "17091999");
        ReflectionTestUtils.setField(service, "email",      "john@xyz.com");
        ReflectionTestUtils.setField(service, "rollNumber", "ABCD123");
    }

    // ── helper ────────────────────────────────────────────────────────────────
    private BfhlResponse call(String... tokens) {
        BfhlRequest req = new BfhlRequest();
        req.setData(Arrays.asList(tokens));
        return service.process(req);
    }

    // ── Example A ─────────────────────────────────────────────────────────────
    @Test
    @DisplayName("Example A – mixed data")
    void exampleA() {
        BfhlResponse r = call("a", "1", "334", "4", "R", "$");

        assertThat(r.isSuccess()).isTrue();
        assertThat(r.getUserId()).isEqualTo("john_doe_17091999");
        assertThat(r.getEmail()).isEqualTo("john@xyz.com");
        assertThat(r.getRollNumber()).isEqualTo("ABCD123");

        assertThat(r.getOddNumbers()).containsExactly("1");
        assertThat(r.getEvenNumbers()).containsExactly("334", "4");
        assertThat(r.getAlphabets()).containsExactly("A", "R");
        assertThat(r.getSpecialCharacters()).containsExactly("$");
        assertThat(r.getSum()).isEqualTo("339");
        // reversed "ar" → "ra"  alt-caps → "Ra"
        assertThat(r.getConcatString()).isEqualTo("Ra");
    }

    // ── Example B ─────────────────────────────────────────────────────────────
    @Test
    @DisplayName("Example B – more mixed data")
    void exampleB() {
        BfhlResponse r = call("2", "a", "y", "4", "&", "-", "*", "5", "92", "b");

        assertThat(r.getOddNumbers()).containsExactly("5");
        assertThat(r.getEvenNumbers()).containsExactly("2", "4", "92");
        assertThat(r.getAlphabets()).containsExactly("A", "Y", "B");
        assertThat(r.getSpecialCharacters()).containsExactly("&", "-", "*");
        assertThat(r.getSum()).isEqualTo("103");
        // chars "ayb" → reversed "bya" → alt-caps "ByA"
        assertThat(r.getConcatString()).isEqualTo("ByA");
    }

    // ── Example C ─────────────────────────────────────────────────────────────
    @Test
    @DisplayName("Example C – only alphabetic tokens")
    void exampleC() {
        BfhlResponse r = call("A", "ABCD", "DOE");

        assertThat(r.getOddNumbers()).isEmpty();
        assertThat(r.getEvenNumbers()).isEmpty();
        assertThat(r.getAlphabets()).containsExactly("A", "ABCD", "DOE");
        assertThat(r.getSpecialCharacters()).isEmpty();
        assertThat(r.getSum()).isEqualTo("0");
        // chars "aabcddoe" → reversed "eoddbcaa" → alt-caps "EoDdBcAa"
        assertThat(r.getConcatString()).isEqualTo("EoDdBcAa");
    }

    // ── Edge cases ────────────────────────────────────────────────────────────
    @Test
    @DisplayName("Empty data array – all lists empty, sum 0, concat empty")
    void emptyArray() {
        BfhlResponse r = call();

        assertThat(r.isSuccess()).isTrue();
        assertThat(r.getOddNumbers()).isEmpty();
        assertThat(r.getEvenNumbers()).isEmpty();
        assertThat(r.getAlphabets()).isEmpty();
        assertThat(r.getSpecialCharacters()).isEmpty();
        assertThat(r.getSum()).isEqualTo("0");
        assertThat(r.getConcatString()).isEmpty();
    }

    @Test
    @DisplayName("Only numbers – no alphabets or special chars")
    void onlyNumbers() {
        BfhlResponse r = call("3", "6", "9", "12");

        assertThat(r.getOddNumbers()).containsExactly("3", "9");
        assertThat(r.getEvenNumbers()).containsExactly("6", "12");
        assertThat(r.getAlphabets()).isEmpty();
        assertThat(r.getSpecialCharacters()).isEmpty();
        assertThat(r.getSum()).isEqualTo("30");
        assertThat(r.getConcatString()).isEmpty();
    }

    @Test
    @DisplayName("Only special characters")
    void onlySpecialChars() {
        BfhlResponse r = call("@", "#", "!", "&");

        assertThat(r.getSpecialCharacters()).containsExactly("@", "#", "!", "&");
        assertThat(r.getOddNumbers()).isEmpty();
        assertThat(r.getEvenNumbers()).isEmpty();
        assertThat(r.getAlphabets()).isEmpty();
        assertThat(r.getSum()).isEqualTo("0");
        assertThat(r.getConcatString()).isEmpty();
    }

    @Test
    @DisplayName("user_id format is correct")
    void userIdFormat() {
        BfhlResponse r = call("1");
        assertThat(r.getUserId()).matches("[a-z_]+_\\d{8}");
    }

    @Test
    @DisplayName("Alphabets are always returned in uppercase")
    void alphabetsUppercase() {
        BfhlResponse r = call("hello", "world");
        assertThat(r.getAlphabets()).containsExactly("HELLO", "WORLD");
    }

    @Test
    @DisplayName("Large number sum is correct")
    void largeSum() {
        BfhlResponse r = call("999", "1000", "1");
        assertThat(r.getSum()).isEqualTo("2000");
    }
}
