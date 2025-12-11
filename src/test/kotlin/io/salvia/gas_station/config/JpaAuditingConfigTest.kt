package io.salvia.gas_station.config

import org.assertj.core.api.Assertions.assertThat
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.AuditorAware
import kotlin.test.Test

@SpringBootTest
class JpaAuditingConfigTest {

    private val config = JpaAuditingConfig()

    @Test
    fun `AuditorAware가 정상적으로 생성된다`() {
        val auditorAware = config.auditorAware()
        assertThat(auditorAware).isNotNull
    }

    @Test
    fun `현재 Auditor는 system을 반환한다`() {
        val auditorAware = config.auditorAware()
        val currentAuditor = auditorAware.currentAuditor

        assertThat(currentAuditor).isPresent
        assertThat(currentAuditor.get()).isEqualTo("System")
    }
}