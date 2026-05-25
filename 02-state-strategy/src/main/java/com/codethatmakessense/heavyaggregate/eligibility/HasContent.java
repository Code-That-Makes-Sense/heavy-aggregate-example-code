package com.codethatmakessense.heavyaggregate.eligibility;

import com.codethatmakessense.heavyaggregate.Decision;

public final class HasContent implements Spec {

    private final String value;
    private final String fieldName;

    public HasContent(String value, String fieldName) {
        this.value = value;
        this.fieldName = fieldName;
    }

    public Decision check() {
        if (value == null || value.isBlank()) {
            return Decision.no(fieldName + " must not be blank");
        }
        return Decision.ok();
    }
}
