/// Composable post-eligibility predicates implementing the **Specification**
/// design pattern.
///
/// The package-private `Spec` interface is the value-keyed specification: each
/// implementation receives the value it checks at construction (never the
/// `Post` aggregate), keeping helpers free from anemic getters back to the
/// aggregate.
///
/// [Featured] composes the primitive specs (`HasContent`, `IsVerified`,
/// `AtLeastN`) into a named domain policy. `Post.canBeFeatured()` delegates
/// here.
///
/// [PostQueries] exposes `Predicate<Post>` factories (`published`, `featured`,
/// `byAuthor`, `taggedWith`) for query-side composition over post collections.
package com.codethatmakessense.heavyaggregate.eligibility;
