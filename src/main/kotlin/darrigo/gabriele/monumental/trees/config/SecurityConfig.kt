package darrigo.gabriele.monumental.trees.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtDecoders
import org.springframework.security.oauth2.jwt.JwtValidators
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder

@EnableWebSecurity
class SecurityConfig : WebSecurityConfigurerAdapter() {
    @Value("\${auth0.audience}")
    private lateinit var audience: String

    @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private lateinit var issuer: String

    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
            .mvcMatchers("/actuator/*").permitAll()
            .mvcMatchers("/api/v1/*").authenticated()
            .and().cors()
            .and().oauth2ResourceServer().jwt()
    }

    @Bean
    fun jwtDecoder(): JwtDecoder {
        val decoder = JwtDecoders.fromIssuerLocation(issuer) as NimbusJwtDecoder
        val withIssuer = JwtValidators.createDefaultWithIssuer(issuer)
        val validator = DelegatingOAuth2TokenValidator<Jwt>(withIssuer)

        decoder.setJwtValidator(validator)

        return decoder
    }
}
