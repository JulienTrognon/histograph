plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

version = "dev"
group = "up"

dependencies {
    implementation(project(":analyzer"))
    implementation(project(":cli"))
    implementation(project(":config"))
    implementation(project(":api"))
    implementation(project(":webgen"))
    testImplementation("junit:junit:4.+")
}

allprojects {
    repositories {
        mavenCentral()
    }

    plugins.apply("java")

    java.sourceCompatibility = JavaVersion.VERSION_1_10

    tasks {
        compileJava {
            options.encoding = "UTF-8"
        }

        compileTestJava {
            options.encoding = "UTF-8"
        }
    }
}

application.mainClass.set("up.visulog.cli.CLILauncher")
application.mainClassName = ("up.visulog.cli.CLILauncher")

tasks {
    shadowJar {
        archiveClassifier.set("")
    }
}

