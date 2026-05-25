package com.codethatmakessense.heavyaggregate.lifecycle;

import com.codethatmakessense.heavyaggregate.Status;

public interface PostState {
    Status code();
    boolean canEdit();
    PostState transitionTo(Status target);
}
