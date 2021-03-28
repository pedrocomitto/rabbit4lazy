import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("java-library")
	id("maven")
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.4.31"
	kotlin("plugin.spring") version "1.4.31"
}

java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.boot:spring-boot-dependencies:2.4.4")
	}
}

dependencies {
	api("org.springframework.boot:spring-boot-starter-amqp")
	api("com.fasterxml.jackson.module:jackson-module-kotlin")

	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.amqp:spring-rabbit-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

val deployerJars by configurations.creating

dependencies {
	deployerJars("org.apache.maven.wagon:wagon-ssh:2.2")
}

tasks.named<Upload>("uploadArchives") {
	repositories.withGroovyBuilder {
		"mavenDeployer" {
			setProperty("configuration", deployerJars)
			"repository"("url" to "") {
				"authentication"("userName" to "", "password" to "")
			}
		}
	}
}