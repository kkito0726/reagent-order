plugins {
    id("nu.studer.jooq") version "8.1" // jOOQ Gradleプラグイン
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    isEnabled = false
}

dependencies {
    implementation("org.jooq:jooq:3.17.7")
    jooqGenerator("org.postgresql:postgresql:42.7.4")
}

jooq {
    version.set("3.17.7")
    configurations {
        create("main") { // コード生成の設定
            generateSchemaSourceOnCompilation.set(false)
            jooqConfiguration.apply {
                jdbc = org.jooq.meta.jaxb.Jdbc().apply {
                    driver = "org.postgresql.Driver"
                    url = "jdbc:postgresql://localhost:5432/main"
                    user = "sa"
                    password = "pass1234"
                }
                generator = org.jooq.meta.jaxb.Generator().apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    database = org.jooq.meta.jaxb.Database().apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                    }
                    target = org.jooq.meta.jaxb.Target().apply {
                        packageName = "com.generate.jooq" // 生成されたコードのパッケージ
                        directory = "src/main/kotlin" // 出力ディレクトリ
                    }
                }
            }
        }
    }
}

tasks.register<Jar>("buildJar") {
    from(sourceSets.main.get().output)
    archiveBaseName.set("jooq")
    archiveVersion.set("0.0.1-SNAPSHOT")
    archiveClassifier.set("plain") // JARにplainの接尾辞を付ける
}
