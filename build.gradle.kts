plugins {
    id("org.springframework.boot") version "2.6.6"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("java")
    id("com.diffplug.spotless") version "6.4.2"
    id("org.openapi.generator") version "5.4.0"

}
apply(plugin = "org.openapi.generator")
group = "com.video.service"
version = "1.0-SNAPSHOT"
configurations {
    all {
        resolutionStrategy {
            eachDependency {
                if (requested.group.startsWith("org.hibernate") and requested.name.startsWith("hibernate-core")) {
                    useVersion("5.6.5.Final")
                }
            }
        }
        //exclude("org.springframework.boot:spring-boot-starter-logging")
        exclude("ch.qos.logback")
    }
}
repositories {
    exclusiveContent {
        forRepository {
            maven {
                url = uri("https://www.dcm4che.org/maven2")
            }
        }
        filter {
            includeGroup("xuggle")
        }
    }
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
        mavenCentral()
}
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-rest")
    implementation("org.projectlombok:lombok:1.18.20")
    implementation("org.openapitools:openapi-generator-gradle-plugin:5.4.0")
    implementation("xuggle:xuggle-xuggler:5.4")
    annotationProcessor("org.projectlombok:lombok:1.18.20")
    runtimeOnly("com.h2database:h2")
    implementation("commons-io:commons-io:2.11.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("io.springfox:springfox-boot-starter:3.0.0"){
        exclude("org.springframework.plugin:spring-plugin-core")
    }
   implementation("io.springfox:springfox-swagger-ui:3.0.0")
    testImplementation("org.dbunit:dbunit:2.5.4")
    testImplementation("nl.jqno.equalsverifier:equalsverifier:3.4.1")
    testImplementation("com.github.springtestdbunit:spring-test-dbunit:1.3.0")


}
spotless {
    java {
        removeUnusedImports()
        googleJavaFormat("1.9")
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
