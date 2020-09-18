import org.gradle.api.tasks.wrapper.Wrapper.DistributionType.ALL

//==============================================================================
// This software is developed by Stellar Science Ltd Co and the U.S. Government.
// Copyright (C) 2020 Stellar Science; U.S. Government has Unlimited Rights.
// Warning: May contain EXPORT CONTROLLED, FOUO, ITAR, or sensitive information.
//==============================================================================

plugins {
    base
    `maven-publish`
    `java-library`
}

group = "xom"
version = "1.3.5-STELLAR"

buildDir = file("gradle_build")

tasks.named<Wrapper>("wrapper") {
    distributionType = ALL
    gradleVersion = "6.6.1"
}

ant.importBuild("build.xml") { antTaskName ->
    when (antTaskName) {
        "javadoc" -> "antJavadoc"
        "jar" -> "antJar"
        "init" -> "antInit"
        "test" -> "antTest"
        "clean" -> "antClean"
        "help" -> "antHelp"
        else -> antTaskName
    }
}

tasks.named(BasePlugin.CLEAN_TASK_NAME) {
    dependsOn("antClean")
}

tasks.named(JavaPlugin.JAR_TASK_NAME) {
    dependsOn("antJar")
}

publishing {
    repositories {
        for (mavenRepositoryIndex in 0..10) {
            if (!project.hasProperty("maven.repository.url.$mavenRepositoryIndex")) {
                continue
            }
            logger.info("Going to publish to maven.repository.url.$mavenRepositoryIndex(" + project.findProperty("maven.repository.url.$mavenRepositoryIndex") + ")")
            maven {
                name = "maven.repository.url.$mavenRepositoryIndex"
                url = uri(project.findProperty("maven.repository.url.$mavenRepositoryIndex") as String)
                credentials {
                    username = project.findProperty("maven.repository.username.$mavenRepositoryIndex") as String
                    password = project.findProperty("maven.repository.password.$mavenRepositoryIndex") as String
                }
            }
        }
    }
    publications {
        register<MavenPublication>("xom") {
            groupId = project.group.toString()
            artifactId = "xom"
            version = project.version.toString()

            artifact(file("build/xom-1.3.5.jar")) {
                this.classifier = null
                this.extension = "jar"
            }

            pom {
                licenses {
                    license {
                        this.name.set("GNU LESSER GENERAL PUBLIC LICENSE Version 2.1")
                        this.url.set("https://www.gnu.org/licenses/old-licenses/lgpl-2.1.en.html")
                    }
                }
            }
        }
    }
}
