import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.shadow)
    alias(libs.plugins.maven.publish)
}

val archivesBaseName = "rmscmod"
val modVersion = "1.0.0"
val shadowOnly: Configuration by configurations.creating

version = modVersion
group = "top.voemp.rmscmod"

repositories {
    // Add repositories to retrieve artifacts from in here.
    // You should only use this when depending on other mods because
    // Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
    // See https://docs.gradle.org/current/userguide/declaring_repositories.html
    // for more information about repositories.
}

fabricApi {
    configureDataGeneration {
        client = true
    }
}

dependencies {
    // 要更改版本，请参阅 libs.versions.toml 文件
    minecraft(libs.minecraft)
    mappings("net.fabricmc:yarn:${libs.versions.yarn.get()}:v2")
    modImplementation(libs.fabric.loader)
    // Fabric API
    // 从技术上讲，这是可选的，但无论如何您都可能需要它
    modImplementation(libs.fabric.api)
    modImplementation(libs.fabric.kotlin)

    // 串口通信
    implementation(libs.jSerialComm)
    shadowOnly(libs.jSerialComm)
}

tasks.shadowJar {
    configurations = listOf(shadowOnly)
    archiveClassifier = "all"
}

tasks.remapJar {
    dependsOn(tasks.shadowJar)
    inputFile.set(tasks.shadowJar.flatMap { it.archiveFile })
}

tasks.processResources {
    val props = ("version" to modVersion)

    filesMatching("fabric.mod.json") {
        expand(props)
    }
}

tasks.withType<JavaCompile> {
    options.release.set(21)
}

tasks.withType<KotlinCompile> {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
}

java {
    // Fabric 将自动将 sourcesJar 附加到 RemapSourcesJar 任务和存在的 build 任务上
    // 如果删除此行，则不会生成来源
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${archivesBaseName}" }
    }
}

// 配置 Maven 发布
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = archivesBaseName
            from(components["java"])
        }
    }

    // 有关如何设置发布的信息，请参见https://docs.gradle.org/current/userguide/publishing_maven.html
    repositories {
        // 添加存储库以发布到此处
        // 注意：此块与顶级中的块没有相同的功能
        // 这里的存储库将用于发布您的工件，而不是用于检索依赖关系
    }
}