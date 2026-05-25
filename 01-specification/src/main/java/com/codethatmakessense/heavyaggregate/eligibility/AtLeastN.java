package com.codethatmakessense.heavyaggregate.eligibility;

import com.codethatmakessense.heavyaggregate.Decision;

import java.util.List;

final class AtLeastN implements Spec {

    private final List<?> items;
    private final int minimum;
    private final String itemName;

    AtLeastN(List<?> items, int minimum, String itemName) {
        this.items = items;
        this.minimum = minimum;
        this.itemName = itemName;
    }

    public Decision check() {
        if (items.size() < minimum) {
            return Decision.no("at least " + minimum + " " + itemName + " required");
        }
        return Decision.ok();
    }
}
