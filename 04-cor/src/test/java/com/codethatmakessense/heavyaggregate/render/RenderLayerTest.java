package com.codethatmakessense.heavyaggregate.render;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Render layer")
class RenderLayerTest {

    private static final RenderLayer BASE = new PassThrough();

    private static Stream<Arguments> layersAndMarkers() {
        return Stream.of(
            Arguments.of("MarkdownToHtml", (Function<RenderLayer, RenderLayer>) MarkdownToHtml::new, "[X + MarkdownToHtml]"),
            Arguments.of("SyntaxHighlight", (Function<RenderLayer, RenderLayer>) SyntaxHighlight::new, "[X + SyntaxHighlight]"),
            Arguments.of("ExpandEmbeds", (Function<RenderLayer, RenderLayer>) ExpandEmbeds::new, "[X + ExpandEmbeds]"),
            Arguments.of("InjectToC", (Function<RenderLayer, RenderLayer>) InjectToC::new, "[X + InjectToC]"),
            Arguments.of("SanitizeXss", (Function<RenderLayer, RenderLayer>) SanitizeXss::new, "[X + SanitizeXss]")
        );
    }

    @Test
    @DisplayName("PassThrough returns the input unchanged")
    void passThroughReturnsTheInputUnchanged() {
        assertThat(new PassThrough().apply("X")).isEqualTo("X");
    }

    @ParameterizedTest(name = "{0} wraps the input with its marker")
    @MethodSource("layersAndMarkers")
    @DisplayName("each concrete layer wraps the inner output with its marker")
    void eachConcreteLayerWrapsTheInnerOutputWithItsMarker(
        String name, Function<RenderLayer, RenderLayer> factory, String expected
    ) {
        assertThat(factory.apply(BASE).apply("X")).isEqualTo(expected);
    }

    @Test
    @DisplayName("composes layers so the innermost wrap runs first")
    void composesLayersSoTheInnermostWrapRunsFirst() {
        RenderLayer chain = new SyntaxHighlight(new MarkdownToHtml(BASE));
        assertThat(chain.apply("X")).isEqualTo("[[X + MarkdownToHtml] + SyntaxHighlight]");
    }
}
