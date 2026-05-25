plugins {
    id("info.solidsoft.pitest") version "1.19.0" apply false
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "info.solidsoft.pitest")

    repositories {
        mavenCentral()
    }

    the<JavaPluginExtension>().toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }

    dependencies {
        "testImplementation"("org.junit.jupiter:junit-jupiter:5.11.4")
        "testImplementation"("org.assertj:assertj-core:3.26.3")
        "testImplementation"("com.approvaltests:approvaltests:30.1.1")
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher:1.11.4")
    }

    // Wire the shared suite into every phase module's test compilation.
    the<SourceSetContainer>().getByName("test").java.srcDir(
        rootProject.layout.projectDirectory.dir("shared-tests/src/test/java")
    )

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
        // ApprovalTests resolves the shared test source (and its approved files)
        // relative to here, so all phases share one set of approved files.
        workingDir = rootProject.projectDir
        testLogging {
            events("passed", "failed", "skipped")
            showStandardStreams = false
        }
    }

    extensions.configure<info.solidsoft.gradle.pitest.PitestPluginExtension>("pitest") {
        junit5PluginVersion.set("1.2.1")
        targetClasses.set(listOf("com.codethatmakessense.heavyaggregate.*"))
        targetTests.set(listOf("com.codethatmakessense.heavyaggregate.*"))
        // PostApprovalTest is a holistic golden-master regression check whose
        // ApprovalTests file lookup needs workingDir = rootProject.projectDir.
        // PIT spawns its own JVM with a different cwd, so the approval test
        // cannot resolve its .approved.txt under PIT. Mutation-killing is the
        // contract test's job; exclude the approval test from the mutation run.
        excludedTestClasses.set(listOf("com.codethatmakessense.heavyaggregate.PostApprovalTest"))
        mutationThreshold.set(80)
        timestampedReports.set(false)
        outputFormats.set(setOf("HTML", "XML"))
    }
}
