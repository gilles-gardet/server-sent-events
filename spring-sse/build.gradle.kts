import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.4"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
    kotlin("plugin.jpa") version "1.7.22"
}

group = "com.ggardet"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
//    implementation("ch.qos.logback:logback-classic:1.2.9")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.11")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-security")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    doFirst {
        val activeProfile = System.getProperty("spring.profiles.active")
        if (activeProfile == "reactive") {
            System.setProperty("spring.main.web-application-type", "servlet")
        }
        if (project.hasProperty("springProfile")) {
            val springProfile = project.property("springProfile") as String
            systemProperties["spring.profiles.active"] = springProfile
            if (springProfile == "reactive") {
                systemProperties["spring.main.web-application-type"] = "reactive"
            }
        } else {
            systemProperties["spring.profiles.active"] = "reactive"
            systemProperties["spring.main.web-application-type"] = "reactive"
        }
    }
}
