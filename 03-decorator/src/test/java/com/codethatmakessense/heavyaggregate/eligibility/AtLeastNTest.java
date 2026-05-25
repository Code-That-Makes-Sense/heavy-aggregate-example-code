package com.codethatmakessense.heavyaggregate.eligibility;

import com.codethatmakessense.heavyaggregate.Decision;
import com.codethatmakessense.heavyaggregate.Tag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AtLeastN spec")
class AtLeastNTest {

    @ParameterizedTest(name = "passes when size {0} meets minimum {1}")
    @CsvSource({
        "3, 3",
        "4, 3",
        "5, 0",
        "0, 0"
    })
    @DisplayName("passes when item count meets the minimum")
    void passesWhenItemCountMeetsTheMinimum(int itemCount, int minimum) {
        List<Object> items = Stream.generate(() -> (Object) "x").limit(itemCount).toList();
        assertThat(new AtLeastN(items, minimum, "tags").check().allowed()).isTrue();
    }

    @ParameterizedTest(name = "fails when size {0} is below minimum {1}")
    @CsvSource({
        "2, 3",
        "0, 3",
        "0, 1"
    })
    @DisplayName("fails when item count is below the minimum")
    void failsWhenItemCountIsBelowTheMinimum(int itemCount, int minimum) {
        List<Object> items = Stream.generate(() -> (Object) "x").limit(itemCount).toList();
        Decision d = new AtLeastN(items, minimum, "tags").check();
        assertThat(d.allowed()).isFalse();
        assertThat(d.reason()).isEqualTo("at least " + minimum + " tags required");
    }

    @Test
    @DisplayName("uses the supplied item name in the failure message")
    void usesTheSuppliedItemNameInTheFailureMessage() {
        Decision d = new AtLeastN(List.of(), 2, "categories").check();
        assertThat(d.reason()).isEqualTo("at least 2 categories required");
    }

    @Test
    @DisplayName("accepts any element type via wildcard")
    void acceptsAnyElementTypeViaWildcard() {
        List<Tag> tags = List.of(new Tag("intro"), new Tag("welcome"), new Tag("guide"));
        Decision d = new AtLeastN(tags, 3, "tags").check();
        assertThat(d.allowed()).isTrue();
    }
}
