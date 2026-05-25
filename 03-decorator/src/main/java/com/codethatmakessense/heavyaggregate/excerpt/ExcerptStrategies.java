package com.codethatmakessense.heavyaggregate.excerpt;

import com.codethatmakessense.heavyaggregate.ExcerptKind;

import java.util.Map;

public final class ExcerptStrategies {

    private static final Map<ExcerptKind, ExcerptStrategy> REGISTRY = Map.of(
        ExcerptKind.FIRST_PARAGRAPH, new FirstParagraph(),
        ExcerptKind.LEAD_IN, new LeadIn(),
        ExcerptKind.SMART_TRUNCATE, new SmartTruncate()
    );

    private ExcerptStrategies() {
    }

    public static ExcerptStrategy forKind(ExcerptKind kind) {
        ExcerptStrategy strategy = REGISTRY.get(kind);
        if (strategy == null) {
            throw new IllegalStateException("no strategy registered for " + kind);
        }
        return strategy;
    }
}
