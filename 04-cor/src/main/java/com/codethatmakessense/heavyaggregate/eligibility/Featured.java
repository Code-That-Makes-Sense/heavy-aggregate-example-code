package com.codethatmakessense.heavyaggregate.eligibility;

import com.codethatmakessense.heavyaggregate.Author;
import com.codethatmakessense.heavyaggregate.Decision;
import com.codethatmakessense.heavyaggregate.ModerationStatus;
import com.codethatmakessense.heavyaggregate.Status;
import com.codethatmakessense.heavyaggregate.Tag;

import java.util.List;

public final class Featured implements Spec {

    private final String title;
    private final Author author;
    private final List<Tag> tags;
    private final ModerationStatus moderationStatus;
    private final Status status;

    public Featured(String title, Author author, List<Tag> tags,
                    ModerationStatus moderationStatus, Status status) {
        this.title = title;
        this.author = author;
        this.tags = tags;
        this.moderationStatus = moderationStatus;
        this.status = status;
    }

    public Decision check() {
        return Spec.all(
            new HasContent(title, "title"),
            new IsVerified(author),
            new AtLeastN(tags, 3, "tags"),
            new HasClearedModeration(moderationStatus),
            new IsPublished(status)
        ).check();
    }
}
