package com.codethatmakessense.heavyaggregate.lifecycle;

import com.codethatmakessense.heavyaggregate.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Scheduled state")
class ScheduledStateTest {

    private final PostState scheduled = new ScheduledState();

    @Test
    @DisplayName("has code SCHEDULED")
    void hasCodeScheduled() {
        assertThat(scheduled.code()).isEqualTo(Status.SCHEDULED);
    }

    @Test
    @DisplayName("forbids editing while the post is on the schedule")
    void forbidsEditingWhileThePostIsOnTheSchedule() {
        assertThat(scheduled.canEdit()).isFalse();
    }

    @Test
    @DisplayName("transitions back to DRAFT when the schedule is canceled")
    void transitionsBackToDraftWhenTheScheduleIsCanceled() {
        PostState next = scheduled.transitionTo(Status.DRAFT);
        assertThat(next.code()).isEqualTo(Status.DRAFT);
    }

    @Test
    @DisplayName("transitions to PUBLISHED when the scheduled time arrives")
    void transitionsToPublishedWhenTheScheduledTimeArrives() {
        PostState next = scheduled.transitionTo(Status.PUBLISHED);
        assertThat(next.code()).isEqualTo(Status.PUBLISHED);
    }

    @ParameterizedTest(name = "rejects transition to {0}")
    @EnumSource(value = Status.class, names = {"IN_REVIEW", "SCHEDULED", "ARCHIVED"})
    @DisplayName("rejects every transition except DRAFT and PUBLISHED")
    void rejectsEveryTransitionExceptDraftAndPublished(Status target) {
        assertThatThrownBy(() -> scheduled.transitionTo(target))
            .isInstanceOf(IllegalStateException.class);
    }
}
