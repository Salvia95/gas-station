package io.salvia.gas_station.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.util.Optional

/**
 * JPA Auditing Configuration
 * Automatically input user information into @CreatedBy, @LastModifiedBy
 */

@Configuration
@EnableJpaAuditing
class JpaAuditingConfig {

    @Bean
    fun auditorAware(): AuditorAware<String> {
        return AuditorAware {
            //Todo Change to real user Information after sync Spring Security
            // val authentication = SecurityContextHolder.getContext().authentication
            // Optional.of(authentication?.name ?: "system")

            Optional.of("System")
        }
    }
}