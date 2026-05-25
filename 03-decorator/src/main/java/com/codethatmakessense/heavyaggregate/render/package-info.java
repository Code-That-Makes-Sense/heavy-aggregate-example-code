/// Pluggable render-pipeline layers implementing the **Decorator** design
/// pattern in its classical wrapping form.
///
/// [RenderLayer] exposes `apply(String input)`. Concrete layers wrap an inner
/// `RenderLayer` and produce a transformed string; composition is via
/// constructor wrapping.
///
/// [RenderChain] assembles the chain based on a supplied
/// [com.codethatmakessense.heavyaggregate.RenderProfile]. Some layers
/// (`SyntaxHighlight`, `InjectToC`) are conditional on the profile flags;
/// others always run. Adding a layer means adding a class plus one builder
/// line, never editing `Post` - the OCP demo.
package com.codethatmakessense.heavyaggregate.render;
