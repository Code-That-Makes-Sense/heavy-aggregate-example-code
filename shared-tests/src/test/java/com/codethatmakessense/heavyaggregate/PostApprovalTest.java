package com.codethatmakessense.heavyaggregate;

import org.approvaltests.Approvals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Post golden master transcript")
public class PostApprovalTest {

    @Test
    @DisplayName("matches the approved transcript across every behavior of the public API")
    void matchesApprovedTranscript() {
        StringBuilder t = new StringBuilder();

        section(t, "Section 1: Construct + observe");
        Post draft = TestPosts.valid();
        line(t, "post.id()", draft.id());
        line(t, "post.title()", draft.title());
        line(t, "post.author().name()", draft.author().name());
        line(t, "post.author().verified()", Boolean.toString(draft.author().verified()));
        line(t, "post.tags().size()", Integer.toString(draft.tags().size()));
        line(t, "post.status()", draft.status().toString());
        line(t, "post.canEdit()", Boolean.toString(draft.canEdit()));
        t.append("\n");

        section(t, "Section 2: canBeFeatured");
        labeled(t, "Happy path (published)",
            format(TestPosts.atPublished().canBeFeatured()));
        labeled(t, "Still a draft",
            format(TestPosts.valid().canBeFeatured()));
        labeled(t, "Missing title",
            format(TestPosts.builder().title("").build().canBeFeatured()));
        labeled(t, "Unverified author",
            format(TestPosts.builder().author(new Author("Ada", false)).build().canBeFeatured()));
        labeled(t, "Only 2 tags",
            format(TestPosts.builder().tags(java.util.List.of(new Tag("a"), new Tag("b"))).build().canBeFeatured()));
        labeled(t, "Moderation flagged",
            format(TestPosts.builder().moderationStatus(ModerationStatus.FLAGGED).build().canBeFeatured()));
        t.append("\n");

        section(t, "Section 3: validateForPublish");
        labeled(t, "All clear", format(TestPosts.valid().validateForPublish()));
        labeled(t, "Title missing", format(TestPosts.builder().title("").build().validateForPublish()));
        labeled(t, "Body too short", format(TestPosts.builder().body("too short").build().validateForPublish()));
        labeled(t, "Broken reference",
            format(TestPosts.builder()
                .references(java.util.List.of(new Reference("http://dead.example", "Dead Example")))
                .build()
                .validateForPublish()));
        labeled(t, "Missing SEO",
            format(TestPosts.builder().seoDescription("").build().validateForPublish()));
        labeled(t, "Moderation flagged",
            format(TestPosts.builder().moderationStatus(ModerationStatus.FLAGGED).build().validateForPublish()));
        labeled(t, "Moderation pending",
            format(TestPosts.builder().moderationStatus(ModerationStatus.PENDING).build().validateForPublish()));
        t.append("\n");

        section(t, "Section 4: State transitions");
        Post p = TestPosts.valid();
        labeled(t, "Start", state(p));
        p.transitionTo(Status.IN_REVIEW);
        labeled(t, "After DRAFT->IN_REVIEW", state(p));
        p.transitionTo(Status.SCHEDULED);
        labeled(t, "After IN_REVIEW->SCHEDULED", state(p));
        p.transitionTo(Status.PUBLISHED);
        labeled(t, "After SCHEDULED->PUBLISHED", state(p));
        p.transitionTo(Status.ARCHIVED);
        labeled(t, "After PUBLISHED->ARCHIVED", state(p));
        t.append("\n");

        t.append("Rejection path:\n");
        Post rejected = TestPosts.valid();
        rejected.transitionTo(Status.IN_REVIEW);
        rejected.transitionTo(Status.DRAFT);
        labeled(t, "After IN_REVIEW->DRAFT", state(rejected));
        t.append("\n");

        t.append("Cancel schedule:\n");
        Post canceled = TestPosts.valid();
        canceled.transitionTo(Status.IN_REVIEW);
        canceled.transitionTo(Status.SCHEDULED);
        canceled.transitionTo(Status.DRAFT);
        labeled(t, "After SCHEDULED->DRAFT", state(canceled));
        t.append("\n");

        t.append("Invalid transitions (each throws):\n");
        labeled(t, "DRAFT->PUBLISHED", throwsMessage(TestPosts.valid(), Status.PUBLISHED));
        labeled(t, "DRAFT->ARCHIVED", throwsMessage(TestPosts.valid(), Status.ARCHIVED));
        labeled(t, "PUBLISHED->DRAFT", throwsMessage(TestPosts.atPublished(), Status.DRAFT));
        labeled(t, "ARCHIVED->PUBLISHED", throwsMessage(TestPosts.atArchived(), Status.PUBLISHED));
        t.append("\n");

        section(t, "Section 5: Excerpt strategies");
        labeled(t, "FIRST_PARAGRAPH",
            TestPosts.valid().excerpt(ExcerptKind.FIRST_PARAGRAPH));
        labeled(t, "LEAD_IN",
            TestPosts.valid().excerpt(ExcerptKind.LEAD_IN));
        labeled(t, "SMART_TRUNCATE",
            TestPosts.valid().excerpt(ExcerptKind.SMART_TRUNCATE));
        t.append("\n");

        section(t, "Section 6: Render - conditional decorators");
        labeled(t, "ToC + Syntax (full)",
            TestPosts.valid().render(new RenderProfile(true, true)));
        labeled(t, "ToC only",
            TestPosts.valid().render(new RenderProfile(true, false)));
        labeled(t, "Syntax only",
            TestPosts.valid().render(new RenderProfile(false, true)));
        labeled(t, "Neither (minimal)",
            TestPosts.valid().render(new RenderProfile(false, false)));

        Approvals.verify(t.toString());
    }

    private static void section(StringBuilder t, String name) {
        t.append("=== ").append(name).append(" ===\n");
    }

    private static void line(StringBuilder t, String key, String value) {
        t.append(key).append(" = ").append(value).append("\n");
    }

    private static void labeled(StringBuilder t, String label, String value) {
        t.append(pad(label, 28)).append(": ").append(value).append("\n");
    }

    private static String pad(String s, int width) {
        StringBuilder b = new StringBuilder(s);
        while (b.length() < width) {
            b.append(' ');
        }
        return b.toString();
    }

    private static String format(Decision d) {
        return "ok=" + d.allowed() + ", reason=" + d.reason();
    }

    private static String state(Post p) {
        return "status=" + p.status() + ", canEdit=" + p.canEdit();
    }

    private static String throwsMessage(Post p, Status target) {
        try {
            p.transitionTo(target);
            return "no exception thrown";
        } catch (IllegalStateException e) {
            return e.getMessage();
        }
    }
}
