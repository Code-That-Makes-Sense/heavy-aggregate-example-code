package com.codethatmakessense.heavyaggregate.publish;

import com.codethatmakessense.heavyaggregate.ModerationStatus;
import com.codethatmakessense.heavyaggregate.Reference;

import java.util.List;

public record PublishCheck(
    String title,
    String body,
    List<Reference> references,
    String seoDescription,
    ModerationStatus moderationStatus
) {
}
