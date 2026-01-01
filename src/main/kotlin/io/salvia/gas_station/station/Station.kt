package io.salvia.gas_station.station

/**
 * 주유소 도메인 모델 (Public API)
 * 다른 모듈에서 주유소 정보를 참조할 때 사용
 */
data class Station(
    val id: Long,
    val name: String,
    val code: String,
    val address: String,
    val city: String,
    val phoneNumber: String,
    val status: String,
    val isOperating: Boolean
)

/**
 * 주유소 ID Value Object
 */
@JvmInline
value class StationId(val value: Long) {
    init {
        require(value > 0) { "Station ID must be positive" }
    }
}
