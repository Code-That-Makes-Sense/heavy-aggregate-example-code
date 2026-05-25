package com.codethatmakessense.heavyaggregate.lifecycle;

import com.codethatmakessense.heavyaggregate.Status;

final class InReviewState implements PostState {

    public Status code() {
        return Status.IN_REVIEW;
    }

    public boolean canEdit() {
        return true;
    }

    public PostState transitionTo(Status target) {
        if (target == Status.DRAFT) {
            return PostStates.forStatus(Status.DRAFT);
        }
        if (target == Status.SCHEDULED) {
            return PostStates.forStatus(Status.SCHEDULED);
        }
        throw new IllegalStateException("cannot transition from IN_REVIEW to " + target);
    }
}
