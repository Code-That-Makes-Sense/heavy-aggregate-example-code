package com.codethatmakessense.heavyaggregate.eligibility;

import com.codethatmakessense.heavyaggregate.Decision;
import com.codethatmakessense.heavyaggregate.ModerationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HasClearedModeration spec")
class HasClearedModerationTest {

    @Test
    @DisplayName("passes when moderation status is CLEARED")
    void passesWhenModerationStatusIsCleared() {
        Decision d = new HasClearedModeration(ModerationStatus.CLEARED).check();
        assertThat(d.allowed()).isTrue();
    }

    @ParameterizedTest(name = "fails when moderation status is {0}")
    @EnumSource(value = ModerationStatus.class, names = {"PENDING", "FLAGGED"})
    @DisplayName("fails when moderation status is anything other than CLEARED")
    void failsWhenModerationStatusIsAnythingOtherThanCleared(ModerationStatus status) {
        Decision d = new HasClearedModeration(status).check();
        assertThat(d.allowed()).isFalse();
        assertThat(d.reason()).contains("moderation status is not cleared");
        assertThat(d.reason()).contains(status.name());
    }
}
