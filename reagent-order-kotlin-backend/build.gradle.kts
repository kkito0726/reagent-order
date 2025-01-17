plugins {
	id("java")
	id("org.jetbrains.kotlin.jvm") version "1.9.25"
	id("org.jetbrains.kotlin.plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.3.5"
	id("io.spring.dependency-management") version "1.1.6"
	id("org.jlleitschuh.gradle.ktlint") version "10.1.0"
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
	isEnabled = false
}

tasks.named<Jar>("jar") {
	isEnabled = false
}

allprojects {
	repositories {
		mavenCentral()
	}
}

subprojects {
	apply(plugin = "org.springframework.boot")
	apply(plugin = "io.spring.dependency-management")
	apply(plugin = "java")
	apply(plugin = "org.jetbrains.kotlin.jvm")

	group = "kkito"
	version = "0.0.1-SNAPSHOT"

	dependencies {
		implementation("org.springframework.boot:spring-boot-starter-security")
		implementation("org.springframework.boot:spring-boot-starter-web")
		implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
		implementation("org.springframework.boot:spring-boot-starter-jooq")
		implementation("org.jetbrains.kotlin:kotlin-reflect")
		implementation("org.postgresql:postgresql:42.7.4")
		testImplementation("org.springframework.boot:spring-boot-starter-test")
		testImplementation("org.springframework.security:spring-security-test")
		testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
		testRuntimeOnly("org.junit.platform:junit-platform-launcher")
		testImplementation("org.assertj:assertj-db:2.0.2")
		implementation("org.jooq:jooq:3.17.7")
		implementation("io.jsonwebtoken:jjwt-api:0.11.5")
		implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
		implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")
	}
}

project(":reagent_order") {
	dependencies {
		implementation(project(":jooq"))
	}

	tasks.named<Test>("test") {
		useJUnitPlatform()
	}

	tasks.named("bootRun") {
		dependsOn(":jooq:build")
	}

	springBoot {
		mainClass.set("kkito.reagent_order.ReagentOrderApplicationKt") // メインクラスを指定
	}
}

kotlin {
	compilerOptions {
		freeCompilerArgs.add("-Xjsr305=strict")
	}
}

tasks.named<Test>("test") {
	useJUnitPlatform()
}
