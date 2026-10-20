val ktorVersion = "3.0.0" // หรือเวอร์ชันล่าสุดที่เสถียร
val kotlinVersion = "2.0.0"
val logbackVersion = "1.4.14"
val koinVersion = "4.0.2" // ใช้เวอร์ชันล่าสุดที่รองรับ Ktor 3

val exposedVersion = "0.53.0" // เวอร์ชันเสถียรสำหรับ Kotlin 2.x
val sqliteVersion = "3.46.0.0"
val flywayVersion = "13.10.0"

plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
    id("io.ktor.plugin") version "3.0.0"
}

group = "org.deesoft"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor Server Core & Netty Engine
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")

    // Content Negotiation สำหรับแปลง JSON (Serialization)
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")

    // Koin Core & Ktor Integration
    implementation("io.insert-koin:koin-ktor:${koinVersion}")
    implementation("io.insert-koin:koin-core:${koinVersion}")
    implementation("io.insert-koin:koin-logger-slf4j:${koinVersion}")

    // JetBrains Exposed (ORM)
        implementation("org.jetbrains.exposed:exposed-core:${exposedVersion}")
        implementation("org.jetbrains.exposed:exposed-dao:${exposedVersion}")
        implementation("org.jetbrains.exposed:exposed-jdbc:${exposedVersion}")
        implementation("org.jetbrains.exposed:exposed-java-time:${exposedVersion}") // รองรับคอลัมน์ประเภท Date/Time
    // SQLite JDBC Driver
        implementation("org.xerial:sqlite-jdbc:${sqliteVersion}")
    // Flyway สำหรับจัดการ Database Migration
        implementation("org.flywaydb:flyway-core:${flywayVersion}")
        implementation("org.flywaydb:flyway-database-nc-sqlite:${flywayVersion}") // ตัวเชื่อม Flyway กับ SQLite

    //StatusPages
    implementation("io.ktor:ktor-server-status-pages-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")

    // Logging
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    // Testing
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktorVersion")
    testImplementation(kotlin("test"))

    // Ktor Test Host สำหรับทดสอบ Routing
    testImplementation("io.ktor:ktor-server-test-host-jvm")
    testImplementation("io.ktor:ktor-client-content-negotiation")

    // JUnit 5 สำหรับรัน Test
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // MockK สำหรับทำ Mock ใน Kotlin
    testImplementation("io.mockk:mockk:1.13.11")

    // Ktor Server CallLogging (สำหรับบันทึก HTTP Request/Response อัตโนมัติ)
    implementation("io.ktor:ktor-server-call-logging-jvm")
    // Logback และ Logstash Encoder สำหรับทำ JSON Logging
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")

    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

}

tasks.test {
    useJUnitPlatform() // เปิดใช้งาน JUnit 5 Platform
}