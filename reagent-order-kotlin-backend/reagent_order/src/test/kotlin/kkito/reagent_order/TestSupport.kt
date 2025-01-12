package kkito.reagent_order

import com.generate.jooq.Tables.APP_USER
import com.generate.jooq.Tables.ORDER_DETAIL
import com.generate.jooq.Tables.ORDER_SET
import com.generate.jooq.Tables.USER_ORDER
import kkito.reagent_order.config.SecurityConfig
import org.assertj.db.type.Changes
import org.assertj.db.type.Table
import org.jooq.DSLContext
import org.json.JSONObject
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.ResultActions
import javax.sql.DataSource

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig::class)
open class TestSupport {
    @Autowired
    lateinit var dslContext: DSLContext

    @Autowired
    lateinit var dataSource: DataSource

    @BeforeEach
    open fun setUp() {
        dslContext.deleteFrom(APP_USER).execute()
        dslContext.deleteFrom(ORDER_SET).execute()
        dslContext.deleteFrom(ORDER_DETAIL).execute()
        dslContext.deleteFrom(USER_ORDER).execute()
    }

    fun createChanges(tableNames: List<String>): Changes {
        return Changes(
            *tableNames.map {
                Table(dataSource, it)
            }.toTypedArray()
        )
    }

    fun createResponseBodyJson(resultAction: ResultActions): JSONObject {
        return JSONObject(resultAction.andReturn().response.contentAsByteArray.toString(Charsets.UTF_8))
    }
}
