package com.codethatmakessense.heavyaggregate.eligibility;

import com.codethatmakessense.heavyaggregate.Author;
import com.codethatmakessense.heavyaggregate.Decision;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("IsVerified spec")
class IsVerifiedTest {

    @Test
    @DisplayName("passes when the author is verified")
    void passesWhenTheAuthorIsVerified() {
        Decision d = new IsVerified(new Author("Ada", true)).check();
        assertThat(d.allowed()).isTrue();
        assertThat(d.reason()).isEmpty();
    }

    @Test
    @DisplayName("fails when the author is not verified")
    void failsWhenTheAuthorIsNotVerified() {
        Decision d = new IsVerified(new Author("Ada", false)).check();
        assertThat(d.allowed()).isFalse();
        assertThat(d.reason()).isEqualTo("author must be verified");
    }
}
