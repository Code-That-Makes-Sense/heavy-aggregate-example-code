package com.codethatmakessense.heavyaggregate.lifecycle;

import com.codethatmakessense.heavyaggregate.Status;

final class PublishedState implements PostState {

    public Status code() {
        return Status.PUBLISHED;
    }

    public boolean canEdit() {
        return false;
    }

    public PostState transitionTo(Status target) {
        if (target == Status.ARCHIVED) {
            return PostStates.forStatus(Status.ARCHIVED);
        }
        throw new IllegalStateException("cannot transition from PUBLISHED to " + target);
    }
}
