package io.salvia.gas_station.station.internal

import io.salvia.gas_station.station.Station
import io.salvia.gas_station.station.StationService
import io.salvia.gas_station.station.internal.persistence.StationRepository
import io.salvia.gas_station.station.internal.persistence.entities.StationJPAEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class StationServiceImpl(
    private val stationRepository: StationRepository
): StationService {

    override fun getStations(): List<Station>? {
        return stationRepository.findAll().map { it.toStation() }
    }

    override fun getStationById(id: String): Station? {
        TODO("Not yet implemented")
    }

    override fun getStationByCode(code: String): Station? {
        return stationRepository.findByCode(code)
    }

    override fun getStationsByCity(city: String): List<Station>? {
        return stationRepository.findAllByCity(city)
    }

    override fun isStationOperating(code: String): Boolean {
        return stationRepository.existsByCode(code)
    }
}

data class CreateStationCommand(
    val name: String,
    val address: String,
    val ownerName: String,
    val businessNumber: String,
    val phoneNumber: String?
)

// 확장 함수: StationJPAEntity를 Station으로 변환
private fun StationJPAEntity.toStation(): Station {
    return Station(
        id = this.id ?: 0L,
        name = this.name,
        businessNumber = this.code, // code를 businessNumber로 매핑 (임시)
        address = this.address,
        city = this.city,
        phoneNumber = this.phoneNumber,
        status = this.status.name,
        isOperating = this.isOperating()
    )
}