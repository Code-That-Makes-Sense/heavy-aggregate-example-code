package com.codethatmakessense.heavyaggregate.publish;

import com.codethatmakessense.heavyaggregate.Decision;
import com.codethatmakessense.heavyaggregate.ModerationStatus;
import com.codethatmakessense.heavyaggregate.Reference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Publish guards chain")
class PublishGuardsTest {

    private static final String LONG_BODY =
        "a body that is intentionally long enough to satisfy the body min length guard";
    private static final Reference HEALTHY = new Reference("https://example.com", "Example");
    private static final Reference BROKEN = new Reference("http://dead.example", "Dead");

    private static PublishCheck allClear() {
        return new PublishCheck("title", LONG_BODY, List.of(HEALTHY), "an SEO description", ModerationStatus.CLEARED);
    }

    private static Stream<Arguments> firstFailureScenarios() {
        return Stream.of(
            Arguments.of("title",
                new PublishCheck("", "short", List.of(), "", ModerationStatus.PENDING)),
            Arguments.of("body",
                new PublishCheck("title", "short", List.of(), "", ModerationStatus.PENDING)),
            Arguments.of("broken",
                new PublishCheck("title", LONG_BODY, List.of(BROKEN), "", ModerationStatus.PENDING)),
            Arguments.of("SEO",
                new PublishCheck("title", LONG_BODY, List.of(), "", ModerationStatus.PENDING)),
            Arguments.of("moderation",
                new PublishCheck("title", LONG_BODY, List.of(), "SEO desc", ModerationStatus.FLAGGED))
        );
    }

    @Test
    @DisplayName("passes when every guard accepts the context")
    void passesWhenEveryGuardAcceptsTheContext() {
        assertThat(PublishGuards.checkAll(allClear()).allowed()).isTrue();
    }

    @ParameterizedTest(name = "fails on the first failing guard - reason contains \"{0}\"")
    @MethodSource("firstFailureScenarios")
    @DisplayName("short-circuits on the first failing guard in chain order")
    void shortCircuitsOnTheFirstFailingGuardInChainOrder(String expectedReasonFragment, PublishCheck context) {
        Decision d = PublishGuards.checkAll(context);
        assertThat(d.allowed()).isFalse();
        assertThat(d.reason()).contains(expectedReasonFragment);
    }
}
