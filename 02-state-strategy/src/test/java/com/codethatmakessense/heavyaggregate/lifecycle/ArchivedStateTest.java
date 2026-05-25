package com.codethatmakessense.heavyaggregate.lifecycle;

import com.codethatmakessense.heavyaggregate.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Archived state")
class ArchivedStateTest {

    private final PostState archived = new ArchivedState();

    @Test
    @DisplayName("has code ARCHIVED")
    void hasCodeArchived() {
        assertThat(archived.code()).isEqualTo(Status.ARCHIVED);
    }

    @Test
    @DisplayName("forbids editing the archived post")
    void forbidsEditingTheArchivedPost() {
        assertThat(archived.canEdit()).isFalse();
    }

    @ParameterizedTest(name = "rejects transition to {0}")
    @EnumSource(Status.class)
    @DisplayName("rejects every outbound transition because the state is terminal")
    void rejectsEveryOutboundTransitionBecauseTheStateIsTerminal(Status target) {
        assertThatThrownBy(() -> archived.transitionTo(target))
            .isInstanceOf(IllegalStateException.class);
    }
}
