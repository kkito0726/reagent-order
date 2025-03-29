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
	url = System.getenv("JDBC_URL") ?: "jdbc:postgresql://localhost:5432/main"
	user = System.getenv("DB_USER") ?: "sa"
	password = System.getenv("DB_PASSWORD") ?: "pass1234"
	locations = arrayOf(System.getenv("FLYWAY_LOCATIONS") ?: "filesystem:src/main/resources/db/migration")
}


