import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm

plugins {
    kotlin("jvm") version "2.3.0"

    kotlin("plugin.serialization") version "2.3.0"

    // Apply ktlint plugin for code linting
    id("org.jlleitschuh.gradle.ktlint") version "14.0.1"

    id("org.jetbrains.dokka") version "2.2.0"

    id("com.vanniktech.maven.publish") version "0.36.0"

    // Apply SonarQube plugin for code quality analysis
    id("org.sonarqube") version "7.2.3.7755"
    // Apply Kover plugin for code coverage analysis
    id("org.jetbrains.kotlinx.kover") version "0.9.7"
}

group = "com.github.xmlet"
version = "1.1.0"

repositories {
    mavenCentral()
}

dependencies {
    // HtmlFlow Kotlin dependency
    api("com.github.xmlet:htmlflow-kotlin:5.0.4")

    // Jakarta web service annotations API for using annotation @Path
    implementation("jakarta.ws.rs:jakarta.ws.rs-api:4.0.0")
    // Kotlin reflection library
    implementation(kotlin("reflect"))
    // Kotlinx serialization JSON
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()
    configure(
        KotlinJvm(
            javadocJar = JavadocJar.Dokka(tasks.dokkaGeneratePublicationHtml),
        ),
    )
    coordinates(groupId = project.group.toString(), artifactId = project.name, version = project.version.toString())
    pom {
        name.set("HtmlFlow-Datastar")
        description.set("Type-Safe Hypermedia-First DSL for Reactive Backend-Driven Web Applications")
        inceptionYear.set("2026")
        url.set("https://github.com/xmlet/HtmlFlow-Datastar")
        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
            }
        }
        developers {
            developer {
                id.set("fmcarvalho")
                name.set("Miguel Gamboa de Carvalho")
                url.set("https://github.com/fmcarvalho")
            }
            developer {
                id.set("PauloCarvalho13")
                name.set("Paulo Carvalho")
                url.set("https://github.com/PauloCarvalho13")
            }
            developer {
                id.set("RicardoGomes07")
                name.set("Ricardo Gomes")
                url.set("https://github.com/RicardoGomes07")
            }
            developer {
                id.set("LeonelCorreia")
                name.set("Leonel Correia")
                url.set("https://github.com/LeonelCorreia")
            }
        }
        scm {
            url.set("https://github.com/xmlet/HtmlFlow-Datastar")
            connection.set("scm:git:https://github.com/xmlet/HtmlFlow-Datastar.git")
            developerConnection.set("scm:git:https://github.com/xmlet/HtmlFlow-Datastar.git")
        }
    }
}

sonar {
    properties {
        property("sonar.projectKey", "xmlet_HtmlFlow-Datastar")
        property("sonar.organization", "fmcarvalho-xmlet")
        property("sonar.coverage.jacoco.xmlReportPaths", "$projectDir/build/reports/kover/report.xml")
        property("sonar.junit.reportPaths", "$projectDir/build/test-results/test")
    }
}

kover.reports {
    verify {
        rule {
            bound {
                minValue.set(80)
                maxValue.set(100)
            }
        }
    }
}

tasks.named("sonar") {
    dependsOn(tasks.named("koverXmlReport"))
}
