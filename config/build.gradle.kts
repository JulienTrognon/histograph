
plugins {
    `java-library`
}

dependencies {
    implementation(project(":util"))
    testImplementation("junit:junit:4.+")
    implementation("org.yaml:snakeyaml:1.29")
    implementation("commons-validator:commons-validator:1.7")
}


