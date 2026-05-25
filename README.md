# When the Aggregate Gets Heavy - Example Code

[![Tests](https://github.com/Code-That-Makes-Sense/heavy-aggregate-example-code/actions/workflows/tests.yml/badge.svg?branch=main)](https://github.com/Code-That-Makes-Sense/heavy-aggregate-example-code/actions/workflows/tests.yml)
[![Mutation](https://github.com/Code-That-Makes-Sense/heavy-aggregate-example-code/actions/workflows/mutation.yml/badge.svg?branch=main)](https://github.com/Code-That-Makes-Sense/heavy-aggregate-example-code/actions/workflows/mutation.yml)
![JDK 25](https://img.shields.io/badge/JDK-25-blue)
![Gradle 9.5.1](https://img.shields.io/badge/Gradle-9.5.1-02303A)
![Mutation kill rate](<https://img.shields.io/badge/mutation%20kill%20rate-%E2%89%A580%25%20(enforced)-success>)

Companion code for the _Code That Makes Sense_ post
**[When the Aggregate Gets Heavy: Splitting Behavior Without Anemia](https://codethatmakessense.substack.com/p/when-the-aggregate-gets-heavy)**.

A blog/CMS `Post` aggregate grew too big to hold all its behavior cleanly. This
repo refactors it in four phases. Each step extracts one concern with a design
pattern, without sliding into the anemic-domain trap, and one shared test suite
proves the behavior never changed.

## The five phases

Each module is a full snapshot of the codebase at one stage. Read them in order,
and diff phase N against phase N+1 to see exactly one pattern appear.

| Phase | Module              | What it extracts                                            | Pattern                 |
| ----- | ------------------- | ----------------------------------------------------------- | ----------------------- |
| 0     | `00-start`          | nothing yet: the god-class `Post`, every concern inline     | (the "before")          |
| 1     | `01-specification`  | `canBeFeatured()` policy + the `PostQueries` filter example | Specification           |
| 2     | `02-state-strategy` | `transitionTo` + `canEdit` lifecycle, `excerpt()` algorithm | State + Strategy        |
| 3     | `03-decorator`      | `render()` transformation chain                             | Decorator               |
| 4     | `04-cor`            | `validateForPublish()` ordered guards                       | Chain of Responsibility |

The public `Post` API is identical in all five modules, only the internals
change. That is what lets a single test suite run against every phase.

## How behavior preservation is proven

Every module compiles the same tests from `shared-tests/` against its own
implementation, and all five share one approved golden-master transcript. So a
single command runs the identical suite against all five phases:

```bash
./gradlew test
```

Green means every refactoring preserved behavior. To run one phase on its own:

```bash
./gradlew :04-cor:test
```

## Mutation testing

Tests can pass without actually catching bugs. PIT (`./gradlew pitest`) inserts
faults into the production code and checks whether the suite notices. A minimum
80% kill rate per module is enforced; on this codebase every phase scores
98-100%.

```bash
./gradlew pitest
```

## Hard code-style rules

Two constructs are banned everywhere in this codebase:

- **No ternary operator** (`? :`).
- **No `switch` / `switch` expression.**

State and Strategy use polymorphism. Predicates use early-return `if` / `else`.
Enum-to-instance lookups use immutable `Map` registries. Verified with a final
grep at build time.

## Requirements

JDK 25. Gradle provisions it through the toolchain and the wrapper pins Gradle
9.5.1, so there is nothing else to install: `./gradlew` fetches what it needs.

## Layout

```text
# the five phase snapshots
00-start/
01-specification/
02-state-strategy/
03-decorator/
04-cor/

# unit tests + golden master, shared by all phases
shared-tests/
```

## About

Part of [Code That Makes Sense](https://codethatmakessense.substack.com),
engineering judgment for the AI era.
