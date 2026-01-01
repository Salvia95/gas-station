package io.salvia.gas_station.station

/**
 * 주유소 서비스 인터페이스 (Public API)
 * 다른 모듈에서 주유소 모듈의 기능을 사용할 때 이 인터페이스를 통해 접근
 */
interface StationService {
    /**
     * 주유소 정보 조회
     */
    fun getStation(stationId: StationId): Station?

    /**
     * 주유소 코드로 조회
     */
    fun getStationByCode(code: String): Station?

    /**
     * 주유소 운영 여부 확인
     */
    fun isStationOperating(stationId: StationId): Boolean

    /**
     * 도시별 주유소 목록 조회
     */
    fun getStationsByCity(city: String): List<Station>
}
