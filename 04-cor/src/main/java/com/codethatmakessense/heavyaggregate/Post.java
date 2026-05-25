package com.codethatmakessense.heavyaggregate;

import com.codethatmakessense.heavyaggregate.eligibility.Featured;
import com.codethatmakessense.heavyaggregate.excerpt.ExcerptStrategies;
import com.codethatmakessense.heavyaggregate.lifecycle.PostState;
import com.codethatmakessense.heavyaggregate.lifecycle.PostStates;
import com.codethatmakessense.heavyaggregate.publish.PublishCheck;
import com.codethatmakessense.heavyaggregate.publish.PublishGuards;
import com.codethatmakessense.heavyaggregate.render.RenderChain;

import java.util.List;

public final class Post {

    private final String id;
    private final String title;
    private final String body;
    private final Author author;
    private final List<Tag> tags;
    private final List<Reference> references;
    private final String seoDescription;
    private final ModerationStatus moderationStatus;

    private PostState state;

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
        this.state = PostStates.forStatus(Status.DRAFT);
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
        return state.code();
    }

    public Decision canBeFeatured() {
        return new Featured(title, author, tags, moderationStatus, state.code()).check();
    }

    public Decision validateForPublish() {
        return PublishGuards.runChain(
            new PublishCheck(title, body, references, seoDescription, moderationStatus)
        );
    }

    public void transitionTo(Status target) {
        state = state.transitionTo(target);
    }

    public boolean canEdit() {
        return state.canEdit();
    }

    public String excerpt(ExcerptKind kind) {
        return ExcerptStrategies.forKind(kind).extract(body, title);
    }

    public String render(RenderProfile profile) {
        return RenderChain.chainFor(profile).apply(body);
    }
}
