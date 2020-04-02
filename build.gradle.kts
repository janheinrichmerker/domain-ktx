plugins {
    kotlin("jvm") version "1.3.71"
    id("de.undercouch.download") version "4.0.2"
}

group = "dev.reimer"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("de.malkusch.whois-server-list:public-suffix-list:2.2.0")
}

tasks {
    val download = register<de.undercouch.gradle.tasks.download.Download>("downloadPublicSuffixList") {
        src("https://publicsuffix.org/list/effective_tld_names.dat")
        dest(sourceSets.main.get().output.resourcesDir!!.resolve("domain").resolve("effective_tld_names.dat"))
        onlyIfModified(true)
    }

    compileKotlin {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-XXLanguage:+InlineClasses")
        }
        dependsOn(download)
    }

    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-XXLanguage:+InlineClasses")
        }
        dependsOn(download)
    }
}
