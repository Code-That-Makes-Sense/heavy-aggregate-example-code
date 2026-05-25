package com.codethatmakessense.heavyaggregate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Post aggregate contract")
public class PostContractTest {

    @Nested
    @DisplayName("Decision factory")
    class DecisionFactoryTests {

        @Test
        @DisplayName("`ok` returns an allowed decision with an empty reason")
        void okReturnsAnAllowedDecisionWithAnEmptyReason() {
            Decision d = Decision.ok();
            assertThat(d.allowed()).isTrue();
            assertThat(d.reason()).isEmpty();
        }

        @Test
        @DisplayName("`no` returns a disallowed decision carrying the supplied reason")
        void noReturnsADisallowedDecisionCarryingTheSuppliedReason() {
            Decision d = Decision.no("because");
            assertThat(d.allowed()).isFalse();
            assertThat(d.reason()).isEqualTo("because");
        }
    }

    @Nested
    @DisplayName("construction")
    class ConstructionTests {

        @Test
        @DisplayName("exposes id and title from the constructor")
        void exposesIdAndTitleFromTheConstructor() {
            Post p = TestPosts.valid();
            assertThat(p.id()).isEqualTo("p-1");
            assertThat(p.title()).isEqualTo("Hello world");
        }

        @Test
        @DisplayName("exposes the supplied author record")
        void exposesTheSuppliedAuthorRecord() {
            Post p = TestPosts.valid();
            assertThat(p.author().name()).isEqualTo("Ada");
            assertThat(p.author().verified()).isTrue();
        }

        @Test
        @DisplayName("exposes the supplied tag collection")
        void exposesTheSuppliedTagCollection() {
            assertThat(TestPosts.valid().tags()).hasSize(3);
        }

        @Test
        @DisplayName("starts in the DRAFT state")
        void startsInTheDraftState() {
            assertThat(TestPosts.valid().status()).isEqualTo(Status.DRAFT);
        }

        @Test
        @DisplayName("defensively copies the tag list at construction")
        void defensivelyCopiesTheTagListAtConstruction() {
            List<Tag> mutable = new ArrayList<>();
            mutable.add(new Tag("x"));
            Post p = TestPosts.builder().tags(mutable).build();
            mutable.add(new Tag("y"));
            assertThat(p.tags()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("canBeFeatured")
    class CanBeFeaturedTests {

        @Test
        @DisplayName("passes when the post is published and meets every featured criterion")
        void passesWhenThePostIsPublishedAndMeetsEveryFeaturedCriterion() {
            assertThat(TestPosts.atPublished().canBeFeatured().allowed()).isTrue();
        }

        @Test
        @DisplayName("fails when the post is still a draft")
        void failsWhenThePostIsStillADraft() {
            Decision d = TestPosts.valid().canBeFeatured();
            assertThat(d.allowed()).isFalse();
            assertThat(d.reason()).contains("published");
        }

        @Test
        @DisplayName("fails when the title is blank")
        void failsWhenTheTitleIsBlank() {
            Decision d = TestPosts.builder().title("").build().canBeFeatured();
            assertThat(d.allowed()).isFalse();
            assertThat(d.reason()).contains("title");
        }

        @Test
        @DisplayName("fails when the author is not verified")
        void failsWhenTheAuthorIsNotVerified() {
            Decision d = TestPosts.builder().author(new Author("Ada", false)).build().canBeFeatured();
            assertThat(d.allowed()).isFalse();
            assertThat(d.reason()).contains("verified");
        }

        @ParameterizedTest(name = "fails when there are {0} tags")
        @MethodSource("com.codethatmakessense.heavyaggregate.PostContractTest#insufficientTagSamples")
        @DisplayName("fails when fewer than three tags are supplied")
        void failsWhenFewerThanThreeTagsAreSupplied(int sizeLabel, List<Tag> tags) {
            Decision d = TestPosts.builder().tags(tags).build().canBeFeatured();
            assertThat(d.allowed()).isFalse();
            assertThat(d.reason()).contains("at least 3");
        }

        @ParameterizedTest(name = "fails when moderation is {0}")
        @EnumSource(value = ModerationStatus.class, names = {"PENDING", "FLAGGED"})
        @DisplayName("fails when moderation is not CLEARED")
        void failsWhenModerationIsNotCleared(ModerationStatus moderation) {
            Decision d = TestPosts.builder().moderationStatus(moderation).build().canBeFeatured();
            assertThat(d.allowed()).isFalse();
            assertThat(d.reason()).contains("moderation");
        }
    }

    @Nested
    @DisplayName("validateForPublish")
    class ValidateForPublishTests {

        @Test
        @DisplayName("passes when every guard accepts the post")
        void passesWhenEveryGuardAcceptsThePost() {
            assertThat(TestPosts.valid().validateForPublish().allowed()).isTrue();
        }

        @Test
        @DisplayName("fails when the title is blank")
        void failsWhenTheTitleIsBlank() {
            Decision d = TestPosts.builder().title("").build().validateForPublish();
            assertThat(d.allowed()).isFalse();
            assertThat(d.reason()).contains("title");
        }

        @Test
        @DisplayName("fails when the body is shorter than the threshold")
        void failsWhenTheBodyIsShorterThanTheThreshold() {
            Decision d = TestPosts.builder().body("too short").build().validateForPublish();
            assertThat(d.allowed()).isFalse();
            assertThat(d.reason()).contains("characters");
        }

        @Test
        @DisplayName("fails and identifies a broken reference")
        void failsAndIdentifiesABrokenReference() {
            Decision d = TestPosts.builder()
                .references(List.of(new Reference("http://dead.example", "Dead Example")))
                .build()
                .validateForPublish();
            assertThat(d.allowed()).isFalse();
            assertThat(d.reason()).contains("broken");
            assertThat(d.reason()).contains("dead.example");
        }

        @Test
        @DisplayName("fails when the SEO description is missing")
        void failsWhenTheSeoDescriptionIsMissing() {
            Decision d = TestPosts.builder().seoDescription("").build().validateForPublish();
            assertThat(d.allowed()).isFalse();
            assertThat(d.reason()).contains("SEO");
        }

        @ParameterizedTest(name = "fails when moderation status is {0}")
        @EnumSource(value = ModerationStatus.class, names = {"PENDING", "FLAGGED"})
        @DisplayName("fails when moderation status is not CLEARED")
        void failsWhenModerationStatusIsNotCleared(ModerationStatus status) {
            Decision d = TestPosts.builder().moderationStatus(status).build().validateForPublish();
            assertThat(d.allowed()).isFalse();
            assertThat(d.reason()).contains("moderation");
            assertThat(d.reason()).contains(status.name());
        }
    }

    @Nested
    @DisplayName("transitionTo")
    class TransitionTests {

        @ParameterizedTest(name = "{0} -> {1} succeeds")
        @MethodSource("com.codethatmakessense.heavyaggregate.PostContractTest#validTransitions")
        @DisplayName("accepts every transition the state machine declares valid")
        void acceptsEveryTransitionTheStateMachineDeclaresValid(Status from, Status to) {
            Post p = postIn(from);
            p.transitionTo(to);
            assertThat(p.status()).isEqualTo(to);
        }

        @ParameterizedTest(name = "{0} -> {1} throws")
        @MethodSource("com.codethatmakessense.heavyaggregate.PostContractTest#invalidTransitionsSamples")
        @DisplayName("throws IllegalStateException on every invalid transition")
        void throwsIllegalStateExceptionOnEveryInvalidTransition(Status from, Status to) {
            Post p = postIn(from);
            assertThatThrownBy(() -> p.transitionTo(to))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(from.name())
                .hasMessageContaining(to.name());
        }
    }

    @Nested
    @DisplayName("canEdit")
    class CanEditTests {

        @ParameterizedTest(name = "returns {1} in {0}")
        @CsvSource({
            "DRAFT,     true",
            "IN_REVIEW, true",
            "SCHEDULED, false",
            "PUBLISHED, false",
            "ARCHIVED,  false"
        })
        @DisplayName("returns true only while the post is editable in its lifecycle")
        void returnsTrueOnlyWhileThePostIsEditableInItsLifecycle(Status status, boolean expected) {
            assertThat(postIn(status).canEdit()).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("excerpt")
    class ExcerptTests {

        @ParameterizedTest(name = "for {0} produces marker including title and body length")
        @CsvSource({
            "FIRST_PARAGRAPH, FirstParagraph",
            "LEAD_IN,         LeadIn",
            "SMART_TRUNCATE,  SmartTruncate"
        })
        @DisplayName("produces a deterministic marker keyed by ExcerptKind, title, and body length")
        void producesADeterministicMarkerKeyedByExcerptKindTitleAndBodyLength(ExcerptKind kind, String prefix) {
            String body = "deterministic body content";
            Post p = TestPosts.builder().body(body).build();
            assertThat(p.excerpt(kind)).isEqualTo(
                "[" + prefix + " excerpt of: " + p.title() + " (body length " + body.length() + ")]"
            );
        }
    }

    @Nested
    @DisplayName("render")
    class RenderTests {

        @ParameterizedTest(name = "includeToC={0}, highlightSyntax={1}: ToC present={2}, Syntax present={3}")
        @CsvSource({
            "true,  true,  true,  true",
            "true,  false, true,  false",
            "false, true,  false, true",
            "false, false, false, false"
        })
        @DisplayName("includes only the layers requested by the supplied profile")
        void includesOnlyTheLayersRequestedBySuppliedProfile(
            boolean includeToC, boolean highlightSyntax, boolean tocPresent, boolean syntaxPresent
        ) {
            Post p = TestPosts.valid();
            String rendered = p.render(new RenderProfile(includeToC, highlightSyntax));
            assertThat(rendered).contains("MarkdownToHtml");
            assertThat(rendered).contains("ExpandEmbeds");
            assertThat(rendered).contains("SanitizeXss");
            if (tocPresent) {
                assertThat(rendered).contains("InjectToC");
            } else {
                assertThat(rendered).doesNotContain("InjectToC");
            }
            if (syntaxPresent) {
                assertThat(rendered).contains("SyntaxHighlight");
            } else {
                assertThat(rendered).doesNotContain("SyntaxHighlight");
            }
        }
    }

    // ----- @MethodSource providers (package-private so @Nested classes can resolve them) -----

    static Stream<Arguments> insufficientTagSamples() {
        return Stream.of(
            Arguments.of(0, List.of()),
            Arguments.of(1, List.of(new Tag("only-one"))),
            Arguments.of(2, List.of(new Tag("a"), new Tag("b")))
        );
    }

    static Stream<Arguments> validTransitions() {
        return Stream.of(
            Arguments.of(Status.DRAFT, Status.IN_REVIEW),
            Arguments.of(Status.IN_REVIEW, Status.SCHEDULED),
            Arguments.of(Status.IN_REVIEW, Status.DRAFT),
            Arguments.of(Status.SCHEDULED, Status.PUBLISHED),
            Arguments.of(Status.SCHEDULED, Status.DRAFT),
            Arguments.of(Status.PUBLISHED, Status.ARCHIVED)
        );
    }

    static Stream<Arguments> invalidTransitionsSamples() {
        return Stream.of(
            Arguments.of(Status.DRAFT, Status.SCHEDULED),
            Arguments.of(Status.DRAFT, Status.PUBLISHED),
            Arguments.of(Status.DRAFT, Status.ARCHIVED),
            Arguments.of(Status.IN_REVIEW, Status.PUBLISHED),
            Arguments.of(Status.IN_REVIEW, Status.ARCHIVED),
            Arguments.of(Status.SCHEDULED, Status.IN_REVIEW),
            Arguments.of(Status.SCHEDULED, Status.ARCHIVED),
            Arguments.of(Status.PUBLISHED, Status.DRAFT),
            Arguments.of(Status.PUBLISHED, Status.IN_REVIEW),
            Arguments.of(Status.PUBLISHED, Status.SCHEDULED),
            Arguments.of(Status.ARCHIVED, Status.DRAFT),
            Arguments.of(Status.ARCHIVED, Status.PUBLISHED)
        );
    }

    private static Post postIn(Status status) {
        if (status == Status.DRAFT) {
            return TestPosts.valid();
        }
        if (status == Status.IN_REVIEW) {
            return TestPosts.atInReview();
        }
        if (status == Status.SCHEDULED) {
            return TestPosts.atScheduled();
        }
        if (status == Status.PUBLISHED) {
            return TestPosts.atPublished();
        }
        if (status == Status.ARCHIVED) {
            return TestPosts.atArchived();
        }
        throw new IllegalStateException("unknown status: " + status);
    }
}
