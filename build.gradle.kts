import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("java-library")
	`maven-publish`
	maven
	signing
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.4.31"
	kotlin("plugin.spring") version "1.4.31"
}

buildscript {
	dependencies {
		classpath("com.bmuschko:gradle-nexus-plugin:2.3.1")
	}
}

group = "com.github.pedrocomitto"
version = "0.0.1"

base {
	archivesBaseName = "rabbit4lazy"
}

signing {
//	useGpgCmd()
	sign(configurations.archives.get())
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

//tasks.withType<Test> {
//	useJUnitPlatform()
//}


tasks {
	val sourcesJar by creating(Jar::class) {
		archiveClassifier.set("sources")
		from(sourceSets.main.get().allSource)
	}

	val javadocJar by creating(Jar::class) {
		dependsOn.add(javadoc)
		archiveClassifier.set("javadoc")
		from(javadoc)
	}

	artifacts {
		archives(sourcesJar)
		archives(javadocJar)
		archives(jar)
	}
}

val deployerJars by configurations.creating

dependencies {
	deployerJars("org.apache.maven.wagon:wagon-ssh:2.2")
}


tasks.named<Upload>("uploadArchives") {
	val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
	val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
	val ossrhUsername = project.ext.properties["ossrhUsername"].toString()
	val ossrhPassword = project.ext.properties["ossrhPassword"].toString()


	repositories {
		withConvention(MavenRepositoryHandlerConvention::class) {
			mavenDeployer {

				beforeDeployment {
					signing.signPom(this)
				}
				withGroovyBuilder {
//					setProperty("configuration", deployerJars)
					"repository"("url" to releasesRepoUrl) {
						"authentication"("userName" to ossrhUsername, "password" to ossrhPassword)
					}
					"snapshotRepository"("url" to snapshotsRepoUrl) {
						"authentication"("userName" to ossrhUsername, "password" to ossrhPassword)
					}
				}

				pom.project {
					withGroovyBuilder {
						"name"("rabbit4lazy")
						"packaging"("jar")
						"description"("A simple RabbitMQ library for lazy developers")
						"artifactId"(project.name)
						"version"("$version")
						"url"("https://github.com/pedrocomitto/rabbit4lazy")

						"scm" {
							"connection"("https://github.com/pedrocomitto/rabbit4lazy")
							"developerConnection"("https://github.com/pedrocomitto/rabbit4lazy")
							"url"("https://github.com/pedrocomitto/rabbit4lazy")
						}

						"licenses" {
							"license" {
								"name"("The Apache License, Version 2.0")
								"url"("http://www.apache.org/licenses/LICENSE-2.0.txt")
							}
						}

						"developers" {
							"developer" {
								"id"("pedrocomitto")
								"name"("Pedro Comitto")
								"email"("pedrocomitto@gmail.com")
							}
						}
					}
				}
			}
		}
	}
}
