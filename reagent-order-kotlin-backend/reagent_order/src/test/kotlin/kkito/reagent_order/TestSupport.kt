package kkito.reagent_order;


import com.generate.jooq.Tables.APP_USER
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
open class TestSupport(
    @Autowired val dslContext: DSLContext
) {
    @BeforeEach
    open fun setUp() {
        dslContext.deleteFrom(APP_USER).execute()
    }

}
