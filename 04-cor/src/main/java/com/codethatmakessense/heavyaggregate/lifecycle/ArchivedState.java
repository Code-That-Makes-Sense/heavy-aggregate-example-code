package com.codethatmakessense.heavyaggregate.lifecycle;

import com.codethatmakessense.heavyaggregate.Status;

final class ArchivedState implements PostState {

    public Status code() {
        return Status.ARCHIVED;
    }

    public boolean canEdit() {
        return false;
    }

    public PostState transitionTo(Status target) {
        throw new IllegalStateException("cannot transition from ARCHIVED to " + target);
    }
}
