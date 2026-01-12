package io.salvia.gas_station.station.internal.persistence

import io.salvia.gas_station.station.Station
import io.salvia.gas_station.station.internal.persistence.entities.StationJPAEntity
import org.springframework.data.jpa.repository.JpaRepository

interface StationRepository : JpaRepository<StationJPAEntity, Long> {

    fun findByCode(code: String): Station?
    fun findAllByCity(city: String): List<Station>?
    fun existsByCode(code: String): Boolean
}