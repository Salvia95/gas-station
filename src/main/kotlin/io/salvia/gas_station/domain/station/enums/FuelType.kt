package io.salvia.gas_station.domain.station.enums

enum class FuelType(
    val displayName: String
) {
    GASOLINE_REGULAR("휘발유"),
    GASOLINE_PREMIUM("고급휘발유"),
    DISEL("경유"),
    KEROSENE("등유"),
    LPG("LPG");

    fun isGasoline(): Boolean {
        return this in listOf(GASOLINE_REGULAR, GASOLINE_PREMIUM)
    }
}