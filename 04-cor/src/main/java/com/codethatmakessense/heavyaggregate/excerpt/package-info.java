/// Pluggable excerpt-extraction algorithms implementing the **Strategy**
/// design pattern.
///
/// [ExcerptStrategy] exposes a single `extract(body, title)` operation.
/// Concrete strategies (`FirstParagraph`, `LeadIn`, `SmartTruncate`) are
/// interchangeable behind the interface; each receives the values it
/// transforms rather than the `Post` aggregate.
///
/// [ExcerptStrategies] is an immutable enum-to-instance registry keyed by
/// [com.codethatmakessense.heavyaggregate.ExcerptKind].
package com.codethatmakessense.heavyaggregate.excerpt;
