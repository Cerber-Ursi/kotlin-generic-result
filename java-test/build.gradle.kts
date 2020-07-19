plugins {
    java
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":impl"))
}

application {
    mainClassName = "ru.cerbe.result.test.java.App"
}
