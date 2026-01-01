package io.salvia.gas_station.station.internal

import io.salvia.gas_station.shared.BaseEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Index
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

/**
 * 주유소 엔티티 (station 모듈 내부 전용)
 */
@Entity
@Table(
    name = "gas_stations",
    indexes = [
        Index(name = "idx_city", columnList = "city"),
        Index(name = "idx_status", columnList = "status")
    ]
)
internal class StationEntity(
    name: String,
    code: String,
    address: String,
    city: String,
    phoneNumber: String
) : BaseEntity() {

    @Column(name = "name", nullable = false, length = 100)
    @field:NotBlank(message = "주유소 이름은 필수 입력값 입니다.")
    @field:Size(min = 2, max = 100, message = "주유소 이름은 2-100자여야 합니다.")
    var name: String = name
        protected set

    @Column(name = "code", nullable = false, unique = true, length = 50)
    @field:NotBlank(message = "주유소 코드는 필수 입력값 입니다.")
    @field:Pattern(
        regexp = "^STN-\\d{4}-\\d{3}$",
        message = "주유소 코드 형식이 올바르지 않습니다. (예: STN-2024-001)"
    )
    var code: String = code
        protected set

    @Column(name = "address", nullable = false, length = 255)
    @field:NotBlank(message = "주소는 필수입니다")
    @field:Size(max = 255, message = "주소는 255자 이하여야 합니다")
    var address: String = address
        protected set

    @Column(name = "city", nullable = false, length = 50)
    @field:NotBlank(message = "도시는 필수 입력값 입니다.")
    @field:Size(max = 50, message = "도시명은 50자 이하여야 합니다.")
    var city: String = city
        protected set

    @Column(name = "phone_number", nullable = false, length = 20)
    @field:NotBlank(message = "전화번호는 필수입니다")
    @field:Pattern(
        regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$",
        message = "전화번호 형식이 올바르지 않습니다 (예: 02-1234-5678)"
    )
    var phoneNumber: String = phoneNumber
        protected set

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: StationStatus = StationStatus.ACTIVE
        protected set

    @OneToOne(
        mappedBy = "station",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    var operationInfo: StationOperationInfoEntity? = null
        protected set

    @OneToMany(
        mappedBy = "station",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    private val _tanks: MutableList<FuelTankEntity> = mutableListOf()

    val tanks: List<FuelTankEntity>
        get() = _tanks.toList()

    @OneToMany(
        mappedBy = "station",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    private val _homeLorrys: MutableList<HomeLorryEntity> = mutableListOf()

    val homeLorrys: List<HomeLorryEntity>
        get() = _homeLorrys.toList()

    @OneToMany(
        mappedBy = "station",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    private val _pumps: MutableList<PumpEntity> = mutableListOf()

    val pumps: List<PumpEntity>
        get() = _pumps.toList()

    @OneToMany(
        mappedBy = "station",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    private val _inspections: MutableList<InspectionEntity> = mutableListOf()

    val inspections: List<InspectionEntity>
        get() = _inspections.toList()

    fun updateBasicInfo(
        name: String? = null,
        address: String? = null,
        city: String? = null,
        phoneNumber: String? = null
    ) {
        name?.let { this.name = it }
        address?.let { this.address = it }
        city?.let { this.city = it }
        phoneNumber?.let { this.phoneNumber = it }
    }

    fun assignOperationInfo(operationInfo: StationOperationInfoEntity) {
        this.operationInfo = operationInfo
        operationInfo.station = this
    }

    fun addTank(tank: FuelTankEntity) {
        _tanks.add(tank)
        tank.station = this
    }

    fun removeTank(tank: FuelTankEntity) {
        _tanks.remove(tank)
        tank.station = null
    }

    fun addHomeLorry(homeLorry: HomeLorryEntity) {
        _homeLorrys.add(homeLorry)
        homeLorry.station = this
    }

    fun removeHomeLorry(homeLorry: HomeLorryEntity) {
        _homeLorrys.remove(homeLorry)
        homeLorry.station = null
    }

    fun addPump(pump: PumpEntity) {
        _pumps.add(pump)
        pump.station = this
    }

    fun removePump(pump: PumpEntity) {
        _pumps.remove(pump)
        pump.station = null
    }

    fun addInspection(inspection: InspectionEntity) {
        _inspections.add(inspection)
        inspection.station = this
    }

    fun activate() {
        this.status = StationStatus.ACTIVE
    }

    fun suspend() {
        this.status = StationStatus.SUSPENDED
    }

    fun close() {
        this.status = StationStatus.CLOSED
    }

    fun isOperating(): Boolean {
        return status == StationStatus.ACTIVE
    }

    protected constructor() : this("", "", "", "", "")
}
