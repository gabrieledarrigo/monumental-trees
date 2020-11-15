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
import org.hamcrest.Matchers.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

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
                .andExpect(jsonPath("$.content", notNullValue()))
                .andExpect(jsonPath("$.content[0].id", equalTo(1)))
    }

    @Test
    fun shouldReturnAMonumentalTreeByItsUniqueId() {
        mockMvc.perform(get("/monumental-trees/1"))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", equalTo(1)))
    }

    @Test
    fun shouldReturn404IfAMonumentalTreeCannotBeFound() {
        mockMvc.perform(get("/monumental-trees/12345")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound)
    }

    @Test
    fun shouldCreateANewMonumentalTree() {
        mockMvc.perform(post("/monumental-trees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                """
                    {
                    	"status": "ISCRITTO_IN_ELENCO",
                    	"pointId": "077/A235/CH/13",
                    	"typology": "ALBERO_SINGOLO",
                    	"region": "Abruzzo",
                    	"province": "Chieti",
                    	"locality": "Altino",
                    	"place": "LE MACCHIE ARTICCIARO",
                    	"latitude": 42.08723,
                    	"longitude": 14.34305,
                    	"altitude": 215.0,
                    	"genre": "Juniperus",
                    	"scientificName": "Juniperus oxycedrus L.",
                    	"commonName": "Ginepro coccolone",
                    	"context": "EXTRA_URBANO",
                    	"ageCriteria": true,
                    	"height": 12.0,
                    	"circumference": 200.0
                    }
                """.trimIndent()))
                .andExpect(status().isCreated)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.pointId", equalTo("077/A235/CH/13")))
    }

    @Test
    fun shouldReturn400IfAMonumentalTreeHasInvalidValues() {
        mockMvc.perform(post("/monumental-trees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        """
                    {
                    	"status": "ISCRITTO_IN_ELENCO",
                    	"pointId": "${"Z".repeat(513)}",
                    	"typology": "ALBERO_SINGOLO",
                    	"region": "Abruzzo",
                    	"province": "Chieti",
                    	"locality": "Altino",
                    	"place": "LE MACCHIE ARTICCIARO",
                    	"latitude": 42.08723,
                    	"longitude": 14.34305,
                    	"altitude": 215.0,
                    	"genre": "Juniperus",
                    	"scientificName": "Juniperus oxycedrus L.",
                    	"commonName": "Ginepro coccolone",
                    	"context": "EXTRA_URBANO",
                    	"ageCriteria": true,
                    	"height": 12.0,
                    	"circumference": 200.0
                    }
                """.trimIndent()))
                .andExpect(status().isBadRequest)
    }
}