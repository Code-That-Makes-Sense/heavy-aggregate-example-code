package com.codethatmakessense.heavyaggregate.excerpt;

import com.codethatmakessense.heavyaggregate.ExcerptKind;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Excerpt strategies")
class ExcerptStrategiesTest {

    private static Stream<Arguments> strategiesAndMarkers() {
        return Stream.of(
            Arguments.of(new FirstParagraph(), "[FirstParagraph excerpt of: My title (body length 4)]"),
            Arguments.of(new LeadIn(), "[LeadIn excerpt of: My title (body length 4)]"),
            Arguments.of(new SmartTruncate(), "[SmartTruncate excerpt of: My title (body length 4)]")
        );
    }

    @ParameterizedTest(name = "{0} produces \"{1}\"")
    @MethodSource("strategiesAndMarkers")
    @DisplayName("returns a deterministic marker keyed by strategy and title")
    void returnsADeterministicMarkerKeyedByStrategyAndTitle(ExcerptStrategy strategy, String expected) {
        assertThat(strategy.extract("body", "My title")).isEqualTo(expected);
    }

    @ParameterizedTest(name = "lookup for {0} returns a usable strategy")
    @EnumSource(ExcerptKind.class)
    @DisplayName("returns a strategy for every ExcerptKind via the registry")
    void returnsAStrategyForEveryExcerptKindViaTheRegistry(ExcerptKind kind) {
        assertThat(ExcerptStrategies.forKind(kind)).isNotNull();
    }

    @Test
    @DisplayName("returns the same singleton instance for repeated lookups")
    void returnsTheSameSingletonInstanceForRepeatedLookups() {
        assertThat(ExcerptStrategies.forKind(ExcerptKind.FIRST_PARAGRAPH))
            .isSameAs(ExcerptStrategies.forKind(ExcerptKind.FIRST_PARAGRAPH));
    }
}
