package com.codethatmakessense.heavyaggregate.lifecycle;

import com.codethatmakessense.heavyaggregate.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Draft state")
class DraftStateTest {

    private final PostState draft = new DraftState();

    @Test
    @DisplayName("has code DRAFT")
    void hasCodeDraft() {
        assertThat(draft.code()).isEqualTo(Status.DRAFT);
    }

    @Test
    @DisplayName("allows editing")
    void allowsEditing() {
        assertThat(draft.canEdit()).isTrue();
    }

    @Test
    @DisplayName("transitions to IN_REVIEW when submitted for review")
    void transitionsToInReviewWhenSubmittedForReview() {
        PostState next = draft.transitionTo(Status.IN_REVIEW);
        assertThat(next.code()).isEqualTo(Status.IN_REVIEW);
    }

    @ParameterizedTest(name = "rejects transition to {0}")
    @EnumSource(value = Status.class, names = {"DRAFT", "SCHEDULED", "PUBLISHED", "ARCHIVED"})
    @DisplayName("rejects every transition except IN_REVIEW")
    void rejectsEveryTransitionExceptInReview(Status target) {
        assertThatThrownBy(() -> draft.transitionTo(target))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("DRAFT")
            .hasMessageContaining(target.name());
    }
}
