package com.codethatmakessense.heavyaggregate.lifecycle;

import com.codethatmakessense.heavyaggregate.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Published state")
class PublishedStateTest {

    private final PostState published = new PublishedState();

    @Test
    @DisplayName("has code PUBLISHED")
    void hasCodePublished() {
        assertThat(published.code()).isEqualTo(Status.PUBLISHED);
    }

    @Test
    @DisplayName("forbids editing once the post is live")
    void forbidsEditingOnceThePostIsLive() {
        assertThat(published.canEdit()).isFalse();
    }

    @Test
    @DisplayName("transitions to ARCHIVED when the post is taken down")
    void transitionsToArchivedWhenThePostIsTakenDown() {
        PostState next = published.transitionTo(Status.ARCHIVED);
        assertThat(next.code()).isEqualTo(Status.ARCHIVED);
    }

    @ParameterizedTest(name = "rejects transition to {0}")
    @EnumSource(value = Status.class, names = {"DRAFT", "IN_REVIEW", "SCHEDULED", "PUBLISHED"})
    @DisplayName("rejects every transition except ARCHIVED")
    void rejectsEveryTransitionExceptArchived(Status target) {
        assertThatThrownBy(() -> published.transitionTo(target))
            .isInstanceOf(IllegalStateException.class);
    }
}
