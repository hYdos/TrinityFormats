plugins {
    id("java")
}

group = "me.hydos"
version = "1.0-SNAPSHOT"
val lwjglVersion = "3.3.2-SNAPSHOT"
val lwjglNatives = "natives-windows"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    implementation("com.google.flatbuffers:flatbuffers-java:23.3.3")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.joml", "joml", "1.10.5")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    implementation("de.javagl", "jgltf-model", "2.0.3")

    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-assimp")
    runtimeOnly("org.lwjgl", "lwjgl", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-assimp", classifier = lwjglNatives)
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}