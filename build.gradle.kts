group = "ru.cerbe"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks {
    wrapper {
        gradleVersion = "6.4"
        distributionType = Wrapper.DistributionType.ALL
    }
}
