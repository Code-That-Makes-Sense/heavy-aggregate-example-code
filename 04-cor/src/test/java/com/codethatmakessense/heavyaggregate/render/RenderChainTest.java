package com.codethatmakessense.heavyaggregate.render;

import com.codethatmakessense.heavyaggregate.RenderProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Render chain")
class RenderChainTest {

    @ParameterizedTest(name = "includeToC={0}, highlightSyntax={1} produces {2}")
    @CsvSource(delimiter = '|', value = {
        "true  | true  | [[[[[X + MarkdownToHtml] + SyntaxHighlight] + ExpandEmbeds] + InjectToC] + SanitizeXss]",
        "true  | false | [[[[X + MarkdownToHtml] + ExpandEmbeds] + InjectToC] + SanitizeXss]",
        "false | true  | [[[[X + MarkdownToHtml] + SyntaxHighlight] + ExpandEmbeds] + SanitizeXss]",
        "false | false | [[[X + MarkdownToHtml] + ExpandEmbeds] + SanitizeXss]"
    })
    @DisplayName("builds the chain that matches the supplied profile")
    void buildsTheChainThatMatchesTheSuppliedProfile(boolean includeToC, boolean highlightSyntax, String expected) {
        String result = RenderChain.chainFor(new RenderProfile(includeToC, highlightSyntax)).apply("X");
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("returns a reusable chain that does not retain prior input")
    void returnsAReusableChainThatDoesNotRetainPriorInput() {
        RenderLayer chain = RenderChain.chainFor(new RenderProfile(true, true));
        String first = chain.apply("A");
        String second = chain.apply("B");
        assertThat(first).contains("A");
        assertThat(second).contains("B");
        assertThat(first).isNotEqualTo(second);
    }
}
