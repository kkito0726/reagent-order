plugins {
	id("org.flywaydb.flyway") version "9.2.3" // 最新バージョンに更新してください
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
	isEnabled = false
}

dependencies {
	implementation("org.flywaydb:flyway-core:9.2.3") // 最新バージョンを使用
}

flyway {
	url = "jdbc:postgresql://localhost:5432/main"
	user = "sa"
	password = "pass1234"
	locations = arrayOf("filesystem:src/main/resources/db/migration")
}


