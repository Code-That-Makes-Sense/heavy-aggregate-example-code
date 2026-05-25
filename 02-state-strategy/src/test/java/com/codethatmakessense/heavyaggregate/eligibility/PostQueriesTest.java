package com.codethatmakessense.heavyaggregate.eligibility;

import com.codethatmakessense.heavyaggregate.Author;
import com.codethatmakessense.heavyaggregate.Post;
import com.codethatmakessense.heavyaggregate.Status;
import com.codethatmakessense.heavyaggregate.Tag;
import com.codethatmakessense.heavyaggregate.TestPosts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PostQueries")
class PostQueriesTest {

    @Test
    @DisplayName("`published` matches posts in the PUBLISHED state")
    void publishedMatchesPostsInThePublishedState() {
        Post draft = TestPosts.valid();
        Post published = TestPosts.atPublished();
        assertThat(PostQueries.published().test(draft)).isFalse();
        assertThat(PostQueries.published().test(published)).isTrue();
    }

    @ParameterizedTest(name = "`published` rejects posts in {0}")
    @EnumSource(value = Status.class, names = {"DRAFT", "IN_REVIEW", "SCHEDULED", "ARCHIVED"})
    @DisplayName("`published` rejects posts in any non-published state")
    void publishedRejectsPostsInAnyNonPublishedState(Status status) {
        Post p = transitionedTo(status);
        assertThat(PostQueries.published().test(p)).isFalse();
    }

    @Test
    @DisplayName("`featured` matches a published post that meets the featured policy")
    void featuredMatchesAPublishedPostThatMeetsTheFeaturedPolicy() {
        Post good = TestPosts.atPublished();
        Post draft = TestPosts.valid();
        assertThat(PostQueries.featured().test(good)).isTrue();
        assertThat(PostQueries.featured().test(draft)).isFalse();
    }

    @Test
    @DisplayName("`byAuthor` matches the exact author name")
    void byAuthorMatchesTheExactAuthorName() {
        Post p = TestPosts.builder().author(new Author("Ada", true)).build();
        assertThat(PostQueries.byAuthor("Ada").test(p)).isTrue();
        assertThat(PostQueries.byAuthor("Grace").test(p)).isFalse();
    }

    @Test
    @DisplayName("`taggedWith` matches when the post carries the named tag")
    void taggedWithMatchesWhenThePostCarriesTheNamedTag() {
        Post p = TestPosts.builder()
            .tags(List.of(new Tag("intro"), new Tag("welcome"), new Tag("guide"))).build();
        assertThat(PostQueries.taggedWith("intro").test(p)).isTrue();
        assertThat(PostQueries.taggedWith("welcome").test(p)).isTrue();
        assertThat(PostQueries.taggedWith("missing").test(p)).isFalse();
    }

    @Test
    @DisplayName("`filter` returns only posts that match the predicate")
    void filterReturnsOnlyPostsThatMatchThePredicate() {
        Post a = TestPosts.builder().id("a").author(new Author("Ada", true)).build();
        Post g = TestPosts.builder().id("g").author(new Author("Grace", true)).build();
        List<Post> result = PostQueries.filter(List.of(a, g), PostQueries.byAuthor("Ada"));
        assertThat(result).extracting(Post::id).containsExactly("a");
    }

    @Test
    @DisplayName("`filter` returns an empty list when no post matches")
    void filterReturnsAnEmptyListWhenNoPostMatches() {
        Post p = TestPosts.builder().author(new Author("Ada", true)).build();
        List<Post> result = PostQueries.filter(List.of(p), PostQueries.byAuthor("Grace"));
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("composes `published` and `byAuthor` to find published posts by a given author")
    void composesPublishedAndByAuthorToFindPublishedPostsByAGivenAuthor() {
        Post draftAda = TestPosts.builder().id("draftAda").author(new Author("Ada", true)).build();
        Post publishedAda = TestPosts.atPublished();
        Predicate<Post> filter = PostQueries.published().and(PostQueries.byAuthor("Ada"));
        List<Post> result = PostQueries.filter(List.of(draftAda, publishedAda), filter);
        assertThat(result).extracting(Post::id).containsExactly("p-1");
    }

    @Test
    @DisplayName("composes `featured` and `taggedWith` so either match is enough")
    void composesFeaturedAndTaggedWithSoEitherMatchIsEnough() {
        Post featuredPublished = TestPosts.atPublished();
        Post draftWithFeatureTag = TestPosts.builder()
            .id("tag")
            .tags(List.of(new Tag("feature")))
            .build();
        Post unmatched = TestPosts.builder()
            .id("nei")
            .tags(List.of(new Tag("other")))
            .build();
        Predicate<Post> filter = PostQueries.featured().or(PostQueries.taggedWith("feature"));
        List<Post> result = PostQueries.filter(
            List.of(featuredPublished, draftWithFeatureTag, unmatched), filter);
        assertThat(result).extracting(Post::id).containsExactlyInAnyOrder("p-1", "tag");
    }

    private static Post transitionedTo(Status target) {
        if (target == Status.DRAFT) {
            return TestPosts.valid();
        }
        if (target == Status.IN_REVIEW) {
            return TestPosts.atInReview();
        }
        if (target == Status.SCHEDULED) {
            return TestPosts.atScheduled();
        }
        if (target == Status.PUBLISHED) {
            return TestPosts.atPublished();
        }
        if (target == Status.ARCHIVED) {
            return TestPosts.atArchived();
        }
        throw new IllegalStateException("unknown status: " + target);
    }
}
