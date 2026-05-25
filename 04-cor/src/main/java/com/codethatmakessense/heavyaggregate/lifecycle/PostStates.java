package com.codethatmakessense.heavyaggregate.lifecycle;

import com.codethatmakessense.heavyaggregate.Status;

import java.util.Map;

public final class PostStates {

    private static final Map<Status, PostState> REGISTRY = Map.of(
        Status.DRAFT, new DraftState(),
        Status.IN_REVIEW, new InReviewState(),
        Status.SCHEDULED, new ScheduledState(),
        Status.PUBLISHED, new PublishedState(),
        Status.ARCHIVED, new ArchivedState()
    );

    private PostStates() {
    }

    public static PostState forStatus(Status status) {
        PostState state = REGISTRY.get(status);
        if (state == null) {
            throw new IllegalStateException("no state registered for " + status);
        }
        return state;
    }
}
