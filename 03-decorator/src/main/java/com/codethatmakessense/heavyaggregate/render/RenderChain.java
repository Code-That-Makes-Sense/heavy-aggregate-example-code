package com.codethatmakessense.heavyaggregate.render;

import com.codethatmakessense.heavyaggregate.RenderProfile;

public final class RenderChain {

    private RenderChain() {
    }

    public static RenderLayer chainFor(RenderProfile profile) {
        RenderLayer chain = new PassThrough();
        chain = new MarkdownToHtml(chain);
        if (profile.highlightSyntax()) {
            chain = new SyntaxHighlight(chain);
        }
        chain = new ExpandEmbeds(chain);
        if (profile.includeToC()) {
            chain = new InjectToC(chain);
        }
        chain = new SanitizeXss(chain);
        return chain;
    }
}
