/// Post lifecycle states implementing the **State** design pattern.
///
/// [PostState] is an interface with no default methods; every concrete state
/// must implement `code()`, `canEdit()`, and `transitionTo(Status)` explicitly.
/// This forces every new state to declare its full behavior - no "forgot to
/// override" risk that a base-class default would hide.
///
/// [PostStates] is an immutable enum-to-instance registry. `Post.transitionTo`
/// delegates to the current state, which returns the new state instance (or
/// rejects with `IllegalStateException`).
package com.codethatmakessense.heavyaggregate.lifecycle;
