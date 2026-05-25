package com.codethatmakessense.heavyaggregate.lifecycle;

import com.codethatmakessense.heavyaggregate.Status;

final class DraftState implements PostState {

    public Status code() {
        return Status.DRAFT;
    }

    public boolean canEdit() {
        return true;
    }

    public PostState transitionTo(Status target) {
        if (target == Status.IN_REVIEW) {
            return PostStates.forStatus(Status.IN_REVIEW);
        }
        throw new IllegalStateException("cannot transition from DRAFT to " + target);
    }
}
