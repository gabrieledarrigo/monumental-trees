package darrigo.gabriele.monumental.trees.controller

import darrigo.gabriele.monumental.trees.WithPostgreSQL
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
internal class MonumentalTreeControllerTest: WithPostgreSQL() {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun shouldReturnAPaginatedListOfMonumentalTrees() {
        mockMvc.perform(get("/monumental-trees"))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    }
}