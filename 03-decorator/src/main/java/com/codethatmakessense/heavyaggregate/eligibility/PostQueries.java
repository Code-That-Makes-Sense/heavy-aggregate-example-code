package com.codethatmakessense.heavyaggregate.eligibility;

import com.codethatmakessense.heavyaggregate.Post;
import com.codethatmakessense.heavyaggregate.Status;
import com.codethatmakessense.heavyaggregate.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class PostQueries {

    private PostQueries() {
    }

    public static Predicate<Post> published() {
        return p -> p.status() == Status.PUBLISHED;
    }

    public static Predicate<Post> featured() {
        return p -> p.canBeFeatured().allowed();
    }

    public static Predicate<Post> byAuthor(String authorName) {
        return p -> p.author().name().equals(authorName);
    }

    public static Predicate<Post> taggedWith(String tagName) {
        return p -> {
            for (Tag t : p.tags()) {
                if (t.name().equals(tagName)) {
                    return true;
                }
            }
            return false;
        };
    }

    public static List<Post> filter(List<Post> posts, Predicate<Post> predicate) {
        List<Post> out = new ArrayList<>();
        for (Post p : posts) {
            if (predicate.test(p)) {
                out.add(p);
            }
        }
        return out;
    }
}
