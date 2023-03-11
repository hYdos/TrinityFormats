plugins {
    id("java")
    id("org.openjfx.javafxplugin") version "0.0.13"
}

group = "me.hydos"
version = "1.0-SNAPSHOT"
val lwjglVersion = "3.3.2-SNAPSHOT"
val lwjglNatives = "natives-windows"

javafx {
    version = "19"
    modules("javafx.controls", "javafx.fxml")
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    implementation("de.javagl", "jgltf-model", "2.0.3")
    implementation("com.google.flatbuffers:flatbuffers-java:23.3.3")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.joml", "joml", "1.10.5")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")

    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-assimp")
    implementation("org.lwjgl", "lwjgl-shaderc")
    runtimeOnly("org.lwjgl", "lwjgl", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-assimp", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-shaderc", classifier = lwjglNatives)
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}