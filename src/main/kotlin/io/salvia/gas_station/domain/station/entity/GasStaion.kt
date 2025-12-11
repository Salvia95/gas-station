package io.salvia.gas_station.domain.station.entity

import io.salvia.gas_station.common.entity.BaseEntity
import io.salvia.gas_station.domain.station.enums.StationStatus
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
import org.hibernate.annotations.Cascade

/**
 * GasStation Entity
 *
 */
@Entity
@Table(name = "gas_stations",
    indexes = [
        Index(name = "idx_city", columnList = "city"),
        Index(name = "idx_status", columnList = "status")
    ])
class GasStaion(
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

    /**
     * Internel Code for Management
     * Ex) "STN-2024-001"
     */
    @Column(name = "code", nullable = false, unique = true, length = 50)
    @field:NotBlank(message = "주유소 코드는 필수 입력값 입니다.")
    @field:Pattern(
        regexp = "^STN-\\d{4}-\\d{3}$",
        message = "주유소 코드 형식이 올바르지 않습니다. (예: STN-2024-001)"
    )
    var code: String = code
        protected set

    /**
     * 상세 주소
     */
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
    var operationInfo: StationOperationInfo? = null
        protected set

    @OneToMany(
        mappedBy = "station",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    private val _tanks: MutableList<FuelTank> = mutableListOf()

    val tanks: List<FuelTank>
        get() = _tanks.toList()

    @OneToMany(
        mappedBy = "station",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    private val _homelorrys: MutableList<HomeLorry> = mutableListOf()

    val homeLorrys: List<HomeLorry>
        get() = _homelorrys.toList()

    @OneToMany(
        mappedBy = "station",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    private val _pumps: MutableList<Pump> = mutableListOf()

    val pumps: List<Pump>
        get() = _pumps.toList()

    private val _inspections: MutableList<Inspection> = mutableListOf()

    val inspection: List<Inspection>
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

    fun setOperationInfo(operationInfo: StationOperationInfo) {
        this.operationInfo = operationInfo
        operationInfo.station = this
    }

    fun addTank(tank: FuelTank) {
        _tanks.add(tank)
        tank.station = this
    }

    fun removeTank(tank: FuelTank) {
        _tanks.remove(tank)
        tank.station = null
    }

    fun addHomeLorry(homeLorry: HomeLorry) {
        _homelorrys.add(homeLorry)
        homeLorry.station = this
    }

    fun removeHomeLorry(homeLorry: HomeLorry) {
        _homelorrys.remove(homeLorry)
        homeLorry.station = null
    }

    fun addPump(pump: Pump) {
        _pumps.add(pump)
        pump.station = this
    }

    fun removePump(pump: Pump) {
        _pumps.remove(pump)
        pump.station = null
    }

    fun addInspection(inspection: Inspection) {
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