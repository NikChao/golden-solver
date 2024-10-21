plugins {
    id("java")
    application
    id("org.openjfx.javafxplugin") version "0.1.0"

    id("org.beryx.jlink") version "3.0.1"
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
    implementation("io.github.mkpaz:atlantafx-base:2.0.0")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("org.goldenpath.solver.Main")
    mainModule.set("org.goldenpath.solver")
}

jlink{
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
    launcher {
        name = "golden solver"
    }

    jpackage {
        if (org.gradle.internal.os.OperatingSystem.current().isWindows) {
            installerOptions.addAll(listOf("--win-per-user-install", "--win-dir-chooser", "--win-menu", "--win-shortcut"))
            imageOptions.addAll(listOf("--win-console"))
        }
        if (org.gradle.internal.os.OperatingSystem.current().isMacOsX) {
            installerOptions.addAll(listOf("--mac-package-name", "GoldenSolver", "--mac-sign", "--mac-bundle-identifier", "org.goldenpath.solver"))
            imageOptions.addAll(listOf(
                "--mac-package-identifier", "org.goldenpath.solver",
                "--mac-package-signing-prefix", "Developer ID Application",
                "--icon", "/Users/nikolaichaourov/Documents/solver/src/main/resources/icon.icns",
                "--type", "app-image"
            ))
        }
    }
}
