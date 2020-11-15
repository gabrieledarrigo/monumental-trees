package darrigo.gabriele.monumental.trees

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
internal abstract class WithPostgreSQL {
    companion object {
        private const val USERNAME = "username"

        private const val PASSWORD = "password"

        private const val PORT = 5432

        @Container
        val container = PostgreSQLContainer<Nothing>(DockerImageName.parse("postgres")).apply {
            withDatabaseName("monumental_trees_test")
            withUrlParam("stringtype", "unspecified")
            withUsername(USERNAME)
            withPassword(PASSWORD)
            withExposedPorts(PORT)
        }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", container::getJdbcUrl)
            registry.add("spring.datasource.password", container::getPassword)
            registry.add("spring.datasource.username", container::getUsername)
        }
    }
}
