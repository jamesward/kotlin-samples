import com.google.protobuf.gradle.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val coroutinesVersion = "1.3.6"
val grpcVersion = "1.26.0"
val grpcKotlinVersion = "0.1.1"
val protobufVersion = "3.11.1"

plugins {
    application
    kotlin("jvm") version "1.3.70"
    id("com.google.protobuf") version "0.8.12"
}

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutinesVersion}")

    implementation("com.google.protobuf:protobuf-java:${protobufVersion}")
    implementation("com.google.protobuf:protobuf-java-util:${protobufVersion}")
    implementation("io.grpc:grpc-netty-shaded:${grpcVersion}")
    implementation("io.grpc:grpc-protobuf:${grpcVersion}")
    implementation("io.grpc:grpc-stub:${grpcVersion}")
    implementation("io.grpc:grpc-kotlin-stub:${grpcKotlinVersion}")

    compileOnly("javax.annotation:javax.annotation-api:1.2")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${protobufVersion}"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${grpcVersion}"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:${grpcKotlinVersion}"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClassName = "io.grpc.examples.helloworld.HelloWorldServerKt"
}

tasks.register<JavaExec>("HelloWorldClient") {
    main = "io.grpc.examples.helloworld.HelloWorldClientKt"
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register<CreateStartScripts>("otherStartScripts") {
    mainClassName = "io.grpc.examples.helloworld.HelloWorldClientKt"
    applicationName = "HelloWorldClientKt"
    outputDir = file("${project.buildDir}/scripts")
    classpath = files(System.getProperty("app.home") + "/lib/*")
}

tasks.named("startScripts") {
    dependsOn(tasks.named("otherStartScripts"))
}

tasks.register<DefaultTask>("stage") {
    dependsOn("installDist")
}
