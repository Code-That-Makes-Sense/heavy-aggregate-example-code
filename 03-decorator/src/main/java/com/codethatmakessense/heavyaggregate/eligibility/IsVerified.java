package com.codethatmakessense.heavyaggregate.eligibility;

import com.codethatmakessense.heavyaggregate.Author;
import com.codethatmakessense.heavyaggregate.Decision;

final class IsVerified implements Spec {

    private final Author author;

    IsVerified(Author author) {
        this.author = author;
    }

    public Decision check() {
        if (!author.verified()) {
            return Decision.no("author must be verified");
        }
        return Decision.ok();
    }
}
