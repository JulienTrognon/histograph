
plugins {
    `java-library`
}


repositories {
    mavenCentral()
}

dependencies {
    testImplementation("junit:junit:4.+")
    implementation(project(":config"))
    implementation(project(":util"))
    implementation("org.json:json:20210307")

}


