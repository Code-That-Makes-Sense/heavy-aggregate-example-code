package com.codethatmakessense.heavyaggregate;

import com.codethatmakessense.heavyaggregate.eligibility.Featured;

import java.util.List;

public final class Post {

    private static final int MIN_BODY_LENGTH = 50;

    private final String id;
    private final String title;
    private final String body;
    private final Author author;
    private final List<Tag> tags;
    private final List<Reference> references;
    private final String seoDescription;
    private final ModerationStatus moderationStatus;

    private Status status;

    public Post(String id, String title, String body, Author author,
                List<Tag> tags, List<Reference> references,
                String seoDescription, ModerationStatus moderationStatus) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.author = author;
        this.tags = List.copyOf(tags);
        this.references = List.copyOf(references);
        this.seoDescription = seoDescription;
        this.moderationStatus = moderationStatus;
        this.status = Status.DRAFT;
    }

    public String id() {
        return id;
    }

    public String title() {
        return title;
    }

    public Author author() {
        return author;
    }

    public List<Tag> tags() {
        return tags;
    }

    public Status status() {
        return status;
    }

    public Decision canBeFeatured() {
        return new Featured(title, author, tags, moderationStatus, status).check();
    }

    public Decision validateForPublish() {
        if (title == null || title.isBlank()) {
            return Decision.no("title must not be blank");
        }
        if (body == null || body.length() < MIN_BODY_LENGTH) {
            return Decision.no("body must be at least " + MIN_BODY_LENGTH + " characters");
        }
        for (Reference reference : references) {
            if (!reference.valid()) {
                return Decision.no("reference is broken: " + reference.url());
            }
        }
        if (seoDescription == null || seoDescription.isBlank()) {
            return Decision.no("SEO description must be present");
        }
        if (moderationStatus != ModerationStatus.CLEARED) {
            return Decision.no("moderation status is not cleared: " + moderationStatus);
        }
        return Decision.ok();
    }

    public void transitionTo(Status target) {
        if (status == Status.DRAFT && target == Status.IN_REVIEW) {
            status = target;
            return;
        }
        if (status == Status.IN_REVIEW && target == Status.DRAFT) {
            status = target;
            return;
        }
        if (status == Status.IN_REVIEW && target == Status.SCHEDULED) {
            status = target;
            return;
        }
        if (status == Status.SCHEDULED && target == Status.DRAFT) {
            status = target;
            return;
        }
        if (status == Status.SCHEDULED && target == Status.PUBLISHED) {
            status = target;
            return;
        }
        if (status == Status.PUBLISHED && target == Status.ARCHIVED) {
            status = target;
            return;
        }
        throw new IllegalStateException("cannot transition from " + status + " to " + target);
    }

    public boolean canEdit() {
        return status == Status.DRAFT || status == Status.IN_REVIEW;
    }

    public String excerpt(ExcerptKind kind) {
        if (kind == ExcerptKind.FIRST_PARAGRAPH) {
            return "[FirstParagraph excerpt of: " + title + " (body length " + body.length() + ")]";
        }
        if (kind == ExcerptKind.LEAD_IN) {
            return "[LeadIn excerpt of: " + title + " (body length " + body.length() + ")]";
        }
        if (kind == ExcerptKind.SMART_TRUNCATE) {
            return "[SmartTruncate excerpt of: " + title + " (body length " + body.length() + ")]";
        }
        throw new IllegalStateException("unknown excerpt kind: " + kind);
    }

    public String render(RenderProfile profile) {
        String s = body;
        s = "[" + s + " + MarkdownToHtml]";
        if (profile.highlightSyntax()) {
            s = "[" + s + " + SyntaxHighlight]";
        }
        s = "[" + s + " + ExpandEmbeds]";
        if (profile.includeToC()) {
            s = "[" + s + " + InjectToC]";
        }
        s = "[" + s + " + SanitizeXss]";
        return s;
    }
}
