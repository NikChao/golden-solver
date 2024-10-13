plugins {
    id("java")
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "org.goldenpath.solver"
version = "1.0-SNAPSHOT"

javafx {
    version = "22.0.1"
    modules = listOf("javafx.controls")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.10.0")

    implementation("org.openjfx:javafx-controls:22.0.1")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("Main") // Set the main class for the application
}
