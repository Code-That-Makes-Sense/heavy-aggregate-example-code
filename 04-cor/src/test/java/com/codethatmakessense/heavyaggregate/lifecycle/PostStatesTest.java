package com.codethatmakessense.heavyaggregate.lifecycle;

import com.codethatmakessense.heavyaggregate.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PostStates registry")
class PostStatesTest {

    @ParameterizedTest(name = "returns the matching state for {0}")
    @EnumSource(Status.class)
    @DisplayName("returns the state whose code matches the looked-up status")
    void returnsTheStateWhoseCodeMatchesTheLookedUpStatus(Status status) {
        assertThat(PostStates.forStatus(status).code()).isEqualTo(status);
    }

    @Test
    @DisplayName("returns the same singleton instance for repeated lookups")
    void returnsTheSameSingletonInstanceForRepeatedLookups() {
        assertThat(PostStates.forStatus(Status.DRAFT))
            .isSameAs(PostStates.forStatus(Status.DRAFT));
    }
}
