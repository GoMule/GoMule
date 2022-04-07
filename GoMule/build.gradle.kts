import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
}

group = "org.example"
version = "1.0-SNAPSHOT"


repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.guava:guava:31.0.1-jre")
    implementation("com.formdev:flatlaf:1.6")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of("8"))
    }
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes(mapOf("Main-Class" to "gomule.GoMule"))
    }
    from({
        configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }
    })
}

tasks.register<Copy>("copyJarToDistribution") {
    from(tasks.jar.get())
    into("build/tmp/distribution/GoMule")
    rename { "GoMule.jar" }
}

tasks.register<Copy>("copyResourcesToDistribution") {
    from(".") {
        include(
            "d2111/**",
            "dupelists/**",
            "resources/**",
            "COPYING.txt",
            "LICENSE.txt",
            "standard.css",
            "standard.dat",
        )
    }
    into("build/tmp/distribution/GoMule/")
}

tasks.register("createDistribution") {
    delete("build/tmp/distribution")
    mkdir("build/tmp/distribution/GoMule")
}

tasks.register<Zip>("distribution") {
    from("build/tmp/distribution/")
    include("GoMule/**")
    archiveFileName.set("GoMuleR4.4.13_Resurrected_1.0.zip")
}

tasks.named("copyJarToDistribution") {
    dependsOn("test", "jar")
}

tasks.named("distribution") {
    dependsOn("createDistribution", "copyJarToDistribution", "copyResourcesToDistribution")
}

tasks.named("copyJarToDistribution") {
    dependsOn("createDistribution")
}

tasks.named("copyResourcesToDistribution") {
    dependsOn("createDistribution")
}