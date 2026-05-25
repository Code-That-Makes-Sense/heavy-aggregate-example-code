package com.codethatmakessense.heavyaggregate;

import java.util.List;

public final class TestPosts {

    private TestPosts() {
    }

    public static Post valid() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Post atInReview() {
        Post p = valid();
        p.transitionTo(Status.IN_REVIEW);
        return p;
    }

    public static Post atScheduled() {
        Post p = atInReview();
        p.transitionTo(Status.SCHEDULED);
        return p;
    }

    public static Post atPublished() {
        Post p = atScheduled();
        p.transitionTo(Status.PUBLISHED);
        return p;
    }

    public static Post atArchived() {
        Post p = atPublished();
        p.transitionTo(Status.ARCHIVED);
        return p;
    }

    public static final class Builder {
        private String id = "p-1";
        private String title = "Hello world";
        private String body =
            "This is the body of a post that is intentionally long enough to satisfy publish length requirements.";
        private Author author = new Author("Ada", true);
        private List<Tag> tags = List.of(new Tag("intro"), new Tag("welcome"), new Tag("guide"));
        private List<Reference> references = List.of(new Reference("https://example.com", "Example"));
        private String seoDescription = "An introductory welcome post.";
        private ModerationStatus moderationStatus = ModerationStatus.CLEARED;

        public Builder id(String v) { this.id = v; return this; }
        public Builder title(String v) { this.title = v; return this; }
        public Builder body(String v) { this.body = v; return this; }
        public Builder author(Author v) { this.author = v; return this; }
        public Builder tags(List<Tag> v) { this.tags = v; return this; }
        public Builder references(List<Reference> v) { this.references = v; return this; }
        public Builder seoDescription(String v) { this.seoDescription = v; return this; }
        public Builder moderationStatus(ModerationStatus v) { this.moderationStatus = v; return this; }

        public Post build() {
            return new Post(id, title, body, author, tags, references, seoDescription, moderationStatus);
        }
    }
}
