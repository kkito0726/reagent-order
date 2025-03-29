# DBコンテナのみ立ち上げ
docker compose up db -d

# マイグレーションとjooq生成 (buildコンテナ内ではDBコンテナにアクセスできないのでローカルで行う)
gradle :migration:flywayMigrate --no-daemon
gradle :jooq:generateJooq --no-daemon

docker compose up -d --build