package com.codethatmakessense.heavyaggregate.eligibility;

import com.codethatmakessense.heavyaggregate.Decision;
import com.codethatmakessense.heavyaggregate.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("IsPublished spec")
class IsPublishedTest {

    @Test
    @DisplayName("passes when the post is in the PUBLISHED state")
    void passesWhenThePostIsInThePublishedState() {
        Decision d = new IsPublished(Status.PUBLISHED).check();
        assertThat(d.allowed()).isTrue();
    }

    @ParameterizedTest(name = "fails when the post is in the {0} state")
    @EnumSource(value = Status.class, names = {"DRAFT", "IN_REVIEW", "SCHEDULED", "ARCHIVED"})
    @DisplayName("fails when the post is in any non-published state")
    void failsWhenThePostIsInAnyNonPublishedState(Status status) {
        Decision d = new IsPublished(status).check();
        assertThat(d.allowed()).isFalse();
        assertThat(d.reason()).contains("published");
    }
}
