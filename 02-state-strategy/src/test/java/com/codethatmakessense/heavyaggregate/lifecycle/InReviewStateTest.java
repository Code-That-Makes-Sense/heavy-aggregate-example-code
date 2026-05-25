package com.codethatmakessense.heavyaggregate.lifecycle;

import com.codethatmakessense.heavyaggregate.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("In-review state")
class InReviewStateTest {

    private final PostState inReview = new InReviewState();

    @Test
    @DisplayName("has code IN_REVIEW")
    void hasCodeInReview() {
        assertThat(inReview.code()).isEqualTo(Status.IN_REVIEW);
    }

    @Test
    @DisplayName("allows editing while a reviewer holds the post")
    void allowsEditingWhileAReviewerHoldsThePost() {
        assertThat(inReview.canEdit()).isTrue();
    }

    @Test
    @DisplayName("transitions back to DRAFT when the reviewer rejects")
    void transitionsBackToDraftWhenTheReviewerRejects() {
        PostState next = inReview.transitionTo(Status.DRAFT);
        assertThat(next.code()).isEqualTo(Status.DRAFT);
    }

    @Test
    @DisplayName("transitions to SCHEDULED when the reviewer approves")
    void transitionsToScheduledWhenTheReviewerApproves() {
        PostState next = inReview.transitionTo(Status.SCHEDULED);
        assertThat(next.code()).isEqualTo(Status.SCHEDULED);
    }

    @ParameterizedTest(name = "rejects transition to {0}")
    @EnumSource(value = Status.class, names = {"IN_REVIEW", "PUBLISHED", "ARCHIVED"})
    @DisplayName("rejects every transition except DRAFT and SCHEDULED")
    void rejectsEveryTransitionExceptDraftAndScheduled(Status target) {
        assertThatThrownBy(() -> inReview.transitionTo(target))
            .isInstanceOf(IllegalStateException.class);
    }
}
