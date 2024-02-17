
plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":config"))
    implementation(project(":analyzer"))
    implementation(project(":util"))
    implementation("org.json:json:20210307")
    testImplementation("junit:junit:4.+")
}


