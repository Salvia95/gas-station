package io.salvia.gas_station.station.internal

import io.salvia.gas_station.station.Station
import io.salvia.gas_station.station.StationService
import io.salvia.gas_station.station.internal.persistence.StationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class StationServiceImpl(
    private val stationRepository: StationRepository
): StationService {

    override fun getStations(): List<Station>? {
        return stationRepository.findAll().toList()
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