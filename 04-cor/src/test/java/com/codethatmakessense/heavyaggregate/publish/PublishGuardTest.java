package com.codethatmakessense.heavyaggregate.publish;

import com.codethatmakessense.heavyaggregate.Decision;
import com.codethatmakessense.heavyaggregate.ModerationStatus;
import com.codethatmakessense.heavyaggregate.Reference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Publish guards")
class PublishGuardTest {

    private static PublishCheck okContext() {
        return new PublishCheck(
            "title",
            "a body that is intentionally long enough to satisfy the body min length guard",
            List.of(new Reference("https://example.com", "Example")),
            "an SEO description",
            ModerationStatus.CLEARED
        );
    }

    @Nested
    @DisplayName("TitlePresent")
    class TitlePresentTests {

        @Test
        @DisplayName("passes when the title is non-blank")
        void passesWhenTheTitleIsNonBlank() {
            assertThat(new TitlePresent().check(okContext()).allowed()).isTrue();
        }

        @ParameterizedTest(name = "fails when the title is {0}")
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t"})
        @DisplayName("fails when the title is null, empty, or whitespace")
        void failsWhenTheTitleIsBlank(String title) {
            PublishCheck c = new PublishCheck(title, "b", List.of(), "s", ModerationStatus.CLEARED);
            Decision d = new TitlePresent().check(c);
            assertThat(d.allowed()).isFalse();
            assertThat(d.reason()).isEqualTo("title must not be blank");
        }
    }

    @Nested
    @DisplayName("BodyMinLength")
    class BodyMinLengthTests {

        @Test
        @DisplayName("passes when the body meets the configured threshold")
        void passesWhenTheBodyMeetsTheConfiguredThreshold() {
            assertThat(new BodyMinLength(50).check(okContext()).allowed()).isTrue();
        }

        @Test
        @DisplayName("fails when the body is shorter than the threshold")
        void failsWhenTheBodyIsShorterThanTheThreshold() {
            PublishCheck c = new PublishCheck("t", "short", List.of(), "s", ModerationStatus.CLEARED);
            Decision d = new BodyMinLength(50).check(c);
            assertThat(d.allowed()).isFalse();
            assertThat(d.reason()).isEqualTo("body must be at least 50 characters");
        }

        @Test
        @DisplayName("fails when the body is null")
        void failsWhenTheBodyIsNull() {
            PublishCheck c = new PublishCheck("t", null, List.of(), "s", ModerationStatus.CLEARED);
            assertThat(new BodyMinLength(50).check(c).allowed()).isFalse();
        }

        @Test
        @DisplayName("uses the constructor-supplied threshold")
        void usesTheConstructorSuppliedThreshold() {
            PublishCheck c = new PublishCheck("t", "abc", List.of(), "s", ModerationStatus.CLEARED);
            assertThat(new BodyMinLength(3).check(c).allowed()).isTrue();
            assertThat(new BodyMinLength(4).check(c).allowed()).isFalse();
        }
    }

    @Nested
    @DisplayName("NoBrokenReferences")
    class NoBrokenReferencesTests {

        @Test
        @DisplayName("passes when every reference is valid")
        void passesWhenEveryReferenceIsValid() {
            PublishCheck c = new PublishCheck(
                "t", "b",
                List.of(new Reference("https://a.example", "A"), new Reference("https://b.example", "B")),
                "s", ModerationStatus.CLEARED
            );
            assertThat(new NoBrokenReferences().check(c).allowed()).isTrue();
        }

        @Test
        @DisplayName("passes when no references are supplied")
        void passesWhenNoReferencesAreSupplied() {
            PublishCheck c = new PublishCheck("t", "b", List.of(), "s", ModerationStatus.CLEARED);
            assertThat(new NoBrokenReferences().check(c).allowed()).isTrue();
        }

        @Test
        @DisplayName("fails and reports the first broken reference url")
        void failsAndReportsTheFirstBrokenReferenceUrl() {
            PublishCheck c = new PublishCheck(
                "t", "b",
                List.of(new Reference("https://ok.example", "OK"), new Reference("http://dead.example", "Dead")),
                "s", ModerationStatus.CLEARED
            );
            Decision d = new NoBrokenReferences().check(c);
            assertThat(d.allowed()).isFalse();
            assertThat(d.reason()).contains("http://dead.example");
        }
    }

    @Nested
    @DisplayName("SeoPresent")
    class SeoPresentTests {

        @Test
        @DisplayName("passes when the SEO description is non-blank")
        void passesWhenTheSeoDescriptionIsNonBlank() {
            assertThat(new SeoPresent().check(okContext()).allowed()).isTrue();
        }

        @ParameterizedTest(name = "fails when the SEO description is {0}")
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "\t"})
        @DisplayName("fails when the SEO description is null, empty, or whitespace")
        void failsWhenTheSeoDescriptionIsBlank(String description) {
            PublishCheck c = new PublishCheck("t", "b", List.of(), description, ModerationStatus.CLEARED);
            Decision d = new SeoPresent().check(c);
            assertThat(d.allowed()).isFalse();
            assertThat(d.reason()).isEqualTo("SEO description must be present");
        }
    }

    @Nested
    @DisplayName("ModerationCleared")
    class ModerationClearedTests {

        @Test
        @DisplayName("passes when moderation status is CLEARED")
        void passesWhenModerationStatusIsCleared() {
            assertThat(new ModerationCleared().check(okContext()).allowed()).isTrue();
        }

        @ParameterizedTest(name = "fails when moderation status is {0}")
        @EnumSource(value = ModerationStatus.class, names = {"PENDING", "FLAGGED"})
        @DisplayName("fails when moderation status is not CLEARED")
        void failsWhenModerationStatusIsNotCleared(ModerationStatus status) {
            PublishCheck c = new PublishCheck("t", "b", List.of(), "s", status);
            Decision d = new ModerationCleared().check(c);
            assertThat(d.allowed()).isFalse();
            assertThat(d.reason()).contains(status.name());
        }
    }
}
