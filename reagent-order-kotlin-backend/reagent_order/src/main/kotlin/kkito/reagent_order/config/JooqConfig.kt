package kkito.reagent_order.config

import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.jooq.impl.DefaultConfiguration
import org.jooq.tools.LoggerListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy
import javax.sql.DataSource

@Configuration
open class JooqConfig(private val dataSource: DataSource) {
    @Bean
    open fun dslContext(): DSLContext {
        val configuration = DefaultConfiguration()
        configuration.set(TransactionAwareDataSourceProxy(dataSource)) // データソースを設定
        configuration.set(SQLDialect.POSTGRES) // SQLDialectを設定

        // LoggerListenerを設定
        configuration.set(LoggerListener())

        // Configurationを使用してDSLContextを生成
        return DSL.using(configuration)
    }
}