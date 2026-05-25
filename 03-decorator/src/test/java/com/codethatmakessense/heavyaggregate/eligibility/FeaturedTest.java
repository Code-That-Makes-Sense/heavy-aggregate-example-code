package com.codethatmakessense.heavyaggregate.eligibility;

import com.codethatmakessense.heavyaggregate.Author;
import com.codethatmakessense.heavyaggregate.Decision;
import com.codethatmakessense.heavyaggregate.ModerationStatus;
import com.codethatmakessense.heavyaggregate.Status;
import com.codethatmakessense.heavyaggregate.Tag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Featured policy")
class FeaturedTest {

    private static final Author VERIFIED = new Author("Ada", true);
    private static final List<Tag> THREE_TAGS = List.of(new Tag("a"), new Tag("b"), new Tag("c"));

    private static Featured featuredFor(String title, Author author, List<Tag> tags,
                                        ModerationStatus moderation, Status status) {
        return new Featured(title, author, tags, moderation, status);
    }

    @Test
    @DisplayName("passes when every condition is met")
    void passesWhenEveryConditionIsMet() {
        Decision d = featuredFor("title", VERIFIED, THREE_TAGS, ModerationStatus.CLEARED, Status.PUBLISHED).check();
        assertThat(d.allowed()).isTrue();
    }

    @Test
    @DisplayName("fails when title is blank")
    void failsWhenTitleIsBlank() {
        Decision d = featuredFor("", VERIFIED, THREE_TAGS, ModerationStatus.CLEARED, Status.PUBLISHED).check();
        assertThat(d.allowed()).isFalse();
        assertThat(d.reason()).contains("title");
    }

    @Test
    @DisplayName("fails when author is not verified")
    void failsWhenAuthorIsNotVerified() {
        Decision d = featuredFor("title", new Author("Ada", false), THREE_TAGS,
            ModerationStatus.CLEARED, Status.PUBLISHED).check();
        assertThat(d.allowed()).isFalse();
        assertThat(d.reason()).contains("verified");
    }

    @Test
    @DisplayName("fails when fewer than three tags are supplied")
    void failsWhenFewerThanThreeTagsAreSupplied() {
        Decision d = featuredFor("title", VERIFIED, List.of(new Tag("only-one")),
            ModerationStatus.CLEARED, Status.PUBLISHED).check();
        assertThat(d.allowed()).isFalse();
        assertThat(d.reason()).contains("at least 3 tags required");
    }

    @Test
    @DisplayName("fails when moderation is not cleared")
    void failsWhenModerationIsNotCleared() {
        Decision d = featuredFor("title", VERIFIED, THREE_TAGS,
            ModerationStatus.FLAGGED, Status.PUBLISHED).check();
        assertThat(d.allowed()).isFalse();
        assertThat(d.reason()).contains("moderation");
        assertThat(d.reason()).contains("FLAGGED");
    }

    @Test
    @DisplayName("fails when the post is not in PUBLISHED state")
    void failsWhenThePostIsNotInPublishedState() {
        Decision d = featuredFor("title", VERIFIED, THREE_TAGS,
            ModerationStatus.CLEARED, Status.DRAFT).check();
        assertThat(d.allowed()).isFalse();
        assertThat(d.reason()).contains("published");
    }

    @Test
    @DisplayName("reports the title failure before any other failure when multiple checks fail")
    void reportsTitleFailureBeforeAnyOtherFailureWhenMultipleChecksFail() {
        Decision d = featuredFor("", new Author("Ada", false), List.of(),
            ModerationStatus.FLAGGED, Status.DRAFT).check();
        assertThat(d.reason()).contains("title");
    }
}
