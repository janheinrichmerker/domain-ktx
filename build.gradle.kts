plugins {
    kotlin("jvm") version "2.1.10"
    id("de.undercouch.download") version "5.6.0"
    `maven-publish`
    id("org.jetbrains.dokka") version "0.9.17"
    id("com.palantir.git-version") version "3.1.0"
}

val gitVersion: groovy.lang.Closure<String> by extra
group = "dev.reimer"
version = gitVersion()

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("de.malkusch.whois-server-list:public-suffix-list:2.2.0")
}

lateinit var javadocJar: TaskProvider<Jar>
lateinit var sourcesJar: TaskProvider<Jar>

tasks {
    val download = register<de.undercouch.gradle.tasks.download.Download>("downloadPublicSuffixList") {
        src("https://publicsuffix.org/list/effective_tld_names.dat")
        dest(sourceSets.main.get().output.resourcesDir!!.resolve("domain").resolve("effective_tld_names.dat"))
        onlyIfModified(true)
    }

    compileKotlin {
        dependsOn(download)
    }

    compileTestKotlin {
        dependsOn(download)
    }

    // Include project license in generated JARs.
    withType<Jar> {
        from(project.projectDir) {
            include("LICENSE")
            into("META-INF")
        }
    }

    // Generate Kotlin/Java documentation from sources.
    dokka {
        outputFormat = "html"
    }

    // JAR containing Kotlin/Java documentation.
    javadocJar = register<Jar>("javadocJar") {
        group = JavaBasePlugin.DOCUMENTATION_GROUP
        dependsOn(dokka)
        from(dokka)
        archiveClassifier.set("javadoc")
    }

    // JAR containing all source files.
    sourcesJar = register<Jar>("sourcesJar") {
        from(sourceSets.main.get().allSource)
        archiveClassifier.set("sources")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifact(sourcesJar.get())
            artifact(javadocJar.get())
        }
    }
}
