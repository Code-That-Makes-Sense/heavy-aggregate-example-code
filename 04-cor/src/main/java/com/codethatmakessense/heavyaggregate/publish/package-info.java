/// Ordered publish-time validation guards implementing the **Chain of
/// Responsibility** design pattern in its list-based (pipeline) form.
///
/// Validators are homogeneous: each guard decides only "does my specific
/// check pass". The runner shorts on first failure. Classical linked-list
/// CoR would pay off when handlers differ in what they do (some route, some
/// transform, some short-circuit on their own conditions); for homogeneous
/// validators a list of guards plus a runner is the honest shape.
///
/// [PublishCheck] is the context record carrying everything the guards need.
/// [PublishGuards] runs the fixed-order chain.
package com.codethatmakessense.heavyaggregate.publish;
