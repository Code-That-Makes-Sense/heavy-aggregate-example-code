package com.codethatmakessense.heavyaggregate.eligibility;

import com.codethatmakessense.heavyaggregate.Decision;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HasContent spec")
class HasContentTest {

    @Test
    @DisplayName("passes when value is non-blank")
    void passesWhenValueIsNonBlank() {
        Decision d = new HasContent("anything", "field").check();
        assertThat(d.allowed()).isTrue();
        assertThat(d.reason()).isEmpty();
    }

    @ParameterizedTest(name = "fails when value is {0}")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   ", "\t", "\n"})
    @DisplayName("fails when value is null, empty, or whitespace")
    void failsWhenValueIsBlank(String value) {
        Decision d = new HasContent(value, "title").check();
        assertThat(d.allowed()).isFalse();
        assertThat(d.reason()).isEqualTo("title must not be blank");
    }

    @Test
    @DisplayName("uses the supplied field name in the failure message")
    void usesTheSuppliedFieldNameInTheFailureMessage() {
        Decision d = new HasContent("", "body").check();
        assertThat(d.reason()).isEqualTo("body must not be blank");
    }
}
