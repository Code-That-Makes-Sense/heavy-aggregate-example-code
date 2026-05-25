package com.codethatmakessense.heavyaggregate.lifecycle;

import com.codethatmakessense.heavyaggregate.Status;

final class ScheduledState implements PostState {

    public Status code() {
        return Status.SCHEDULED;
    }

    public boolean canEdit() {
        return false;
    }

    public PostState transitionTo(Status target) {
        if (target == Status.DRAFT) {
            return PostStates.forStatus(Status.DRAFT);
        }
        if (target == Status.PUBLISHED) {
            return PostStates.forStatus(Status.PUBLISHED);
        }
        throw new IllegalStateException("cannot transition from SCHEDULED to " + target);
    }
}
