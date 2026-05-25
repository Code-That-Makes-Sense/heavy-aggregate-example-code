package com.codethatmakessense.heavyaggregate.eligibility;

import com.codethatmakessense.heavyaggregate.Decision;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Spec combinators")
class SpecCombinatorsTest {

    private static final Spec PASS = Decision::ok;
    private static final Spec FAIL_A = () -> Decision.no("a");
    private static final Spec FAIL_B = () -> Decision.no("b");

    @Nested
    @DisplayName("Spec.all")
    class All {

        @Test
        @DisplayName("passes when every spec passes")
        void passesWhenEverySpecPasses() {
            assertThat(Spec.all(PASS, PASS, PASS).check().allowed()).isTrue();
        }

        @Test
        @DisplayName("returns the first failure")
        void returnsTheFirstFailure() {
            Decision d = Spec.all(PASS, FAIL_A, FAIL_B).check();
            assertThat(d.allowed()).isFalse();
            assertThat(d.reason()).isEqualTo("a");
        }

        @Test
        @DisplayName("passes when called with no specs")
        void passesWhenCalledWithNoSpecs() {
            assertThat(Spec.all().check().allowed()).isTrue();
        }
    }

    @Nested
    @DisplayName("Spec.any")
    class Any {

        @Test
        @DisplayName("passes when at least one spec passes")
        void passesWhenAtLeastOneSpecPasses() {
            assertThat(Spec.any(FAIL_A, PASS, FAIL_B).check().allowed()).isTrue();
        }

        @Test
        @DisplayName("fails with the last failure when every spec fails")
        void failsWithTheLastFailureWhenEverySpecFails() {
            Decision d = Spec.any(FAIL_A, FAIL_B).check();
            assertThat(d.allowed()).isFalse();
            assertThat(d.reason()).isEqualTo("b");
        }

        @Test
        @DisplayName("fails when called with no specs")
        void failsWhenCalledWithNoSpecs() {
            assertThat(Spec.any().check().allowed()).isFalse();
        }
    }

    @Nested
    @DisplayName("Spec.not")
    class Not {

        @Test
        @DisplayName("inverts a passing spec into a failure")
        void invertsAPassingSpecIntoAFailure() {
            assertThat(PASS.not().check().allowed()).isFalse();
        }

        @Test
        @DisplayName("inverts a failing spec into a pass")
        void invertsAFailingSpecIntoAPass() {
            assertThat(FAIL_A.not().check().allowed()).isTrue();
        }
    }

    @Nested
    @DisplayName("Spec.and")
    class And {

        @Test
        @DisplayName("passes when both specs pass")
        void passesWhenBothSpecsPass() {
            assertThat(PASS.and(PASS).check().allowed()).isTrue();
        }

        @Test
        @DisplayName("fails when either spec fails")
        void failsWhenEitherSpecFails() {
            assertThat(PASS.and(FAIL_A).check().allowed()).isFalse();
        }
    }

    @Nested
    @DisplayName("Spec.or")
    class Or {

        @Test
        @DisplayName("passes when at least one spec passes")
        void passesWhenAtLeastOneSpecPasses() {
            assertThat(FAIL_A.or(PASS).check().allowed()).isTrue();
        }

        @Test
        @DisplayName("fails when both specs fail")
        void failsWhenBothSpecsFail() {
            assertThat(FAIL_A.or(FAIL_B).check().allowed()).isFalse();
        }
    }
}
