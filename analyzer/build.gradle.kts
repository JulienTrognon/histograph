
plugins {
    `java-library`
}

dependencies {
    implementation(project(":config"))
    implementation(project(":util"))
    implementation(project(":api"))
    implementation("org.json:json:20210307")

    testImplementation("junit:junit:4.+")
}


