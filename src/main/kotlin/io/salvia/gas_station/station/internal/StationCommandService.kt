package io.salvia.gas_station.station.internal

import io.salvia.gas_station.station.StationId
import io.salvia.gas_station.station.internal.domain.StationStatus

/**
 * 주유소 Command 처리 서비스
 * Write 작업을 담당
 */
internal interface StationCommandService {

    /**
     * 주유소 생성
     * @param command 주유소 생성 커맨드
     * @return 생성된 주유소 ID
     */
    fun createStation(command: CreateStationCommand): StationId

    /**
     * 주유소 기본 정보 수정
     * @param stationId 주유소 ID
     * @param command 수정 커맨드
     */
    fun updateStation(stationId: StationId, command: UpdateStationCommand)

    /**
     * 주유소 상태 변경
     * @param stationId 주유소 ID
     * @param status 변경할 상태
     */
    fun changeStationStatus(stationId: StationId, status: StationStatus)

    /**
     * 주유소 삭제
     * @param stationId 주유소 ID
     */
    fun deleteStation(stationId: StationId)

    /**
     * 주유소 운영 정보 설정
     * @param stationId 주유소 ID
     * @param command 운영 정보 설정 커맨드
     */
    fun setOperationInfo(stationId: StationId, command: SetOperationInfoCommand)

    /**
     * 유류 탱크 추가
     * @param stationId 주유소 ID
     * @param command 탱크 추가 커맨드
     * @return 생성된 탱크 ID
     */
    fun addFuelTank(stationId: StationId, command: AddFuelTankCommand): Long

    /**
     * 주유기 추가
     * @param stationId 주유소 ID
     * @param command 주유기 추가 커맨드
     * @return 생성된 주유기 ID
     */
    fun addPump(stationId: StationId, command: AddPumpCommand): Long

    /**
     * 주유기 노즐 추가
     * @param stationId 주유소 ID
     * @param pumpId 주유기 ID
     * @param command 노즐 추가 커맨드
     * @return 생성된 노즐 ID
     */
    fun addNozzle(stationId: StationId, pumpId: Long, command: AddNozzleCommand): Long

    /**
     * 홈로리 추가
     * @param stationId 주유소 ID
     * @param command 홈로리 추가 커맨드
     * @return 생성된 홈로리 ID
     */
    fun addHomeLorry(stationId: StationId, command: AddHomeLorryCommand): Long
}
