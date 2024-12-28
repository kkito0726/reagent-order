package kkito.reagent_order

import com.generate.jooq.Tables.APP_USER
import org.assertj.db.type.Changes
import org.assertj.db.type.Table
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.sql.DataSource

@SpringBootTest
open class TestSupport {
    @Autowired
    lateinit var dslContext: DSLContext

    @Autowired
    lateinit var dataSource: DataSource

    @BeforeEach
    open fun setUp() {
        dslContext.deleteFrom(APP_USER).execute()
    }

    fun createChanges(tableNames: List<String>): Changes {
        return Changes(
            *tableNames.map {
                Table(dataSource, it)
            }.toTypedArray()
        )
    }

}
