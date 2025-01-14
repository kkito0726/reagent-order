package kkito.reagent_order

import com.generate.jooq.Tables.APP_USER
import com.generate.jooq.Tables.ORDER_DETAIL
import com.generate.jooq.Tables.ORDER_SET
import com.generate.jooq.Tables.USER_ORDER
import kkito.reagent_order.config.SecurityConfig
import org.assertj.db.type.Changes
import org.assertj.db.type.Table
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.json.JSONArray
import org.json.JSONObject
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.ResultActions
import java.sql.Connection
import java.sql.DriverManager
import javax.sql.DataSource

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig::class)
open class TestSupport {
    @Autowired
    lateinit var dslContext: DSLContext

    @Autowired
    lateinit var dataSource: DataSource

    companion object {
        fun createDSLContext(): DSLContext {
            val url = "jdbc:postgresql://localhost:5432/main"
            val user = "sa"
            val password = "pass1234"

            // JDBC接続を作成
            val connection: Connection = DriverManager.getConnection(url, user, password)

            // DSLContextを生成
            return DSL.using(connection)
        }

        fun deleteAll() {
            val dslContext = createDSLContext()
            dslContext.deleteFrom(APP_USER).execute()
            dslContext.deleteFrom(ORDER_SET).execute()
            dslContext.deleteFrom(ORDER_DETAIL).execute()
            dslContext.deleteFrom(USER_ORDER).execute()
        }
    }

    @BeforeEach
    open fun setUp() {
        deleteAll()
    }

    fun createChanges(tableNames: List<String>): Changes {
        return Changes(
            *tableNames.map {
                Table(dataSource, it)
            }.toTypedArray()
        )
    }

    fun createResponseBodyJson(resultActions: ResultActions): JSONObject {
        return JSONObject(resultActions.andReturn().response.contentAsByteArray.toString(Charsets.UTF_8))
    }

    fun createResponseJsonArray(resultActions: ResultActions): JSONArray {
        return JSONArray((resultActions.andReturn().response.contentAsByteArray.toString(Charsets.UTF_8)))
    }
}
