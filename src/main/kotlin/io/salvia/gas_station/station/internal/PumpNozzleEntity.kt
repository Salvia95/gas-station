package io.salvia.gas_station.station.internal

import io.salvia.gas_station.shared.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

/**
 * 주유기 노즐 엔티티 (station 모듈 내부 전용)
 */
@Entity
@Table(
    name = "pump_nozzles",
    indexes = [
        Index(name = "idx_nozzles_pump_number", columnList = "pump_id, nozzle_number"),
        Index(name = "idx_nozzles_fuel_type", columnList = "fuel_type")
    ],
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_pump_nozzle_number",
            columnNames = ["pump_id", "nozzle_number"]
        )
    ]
)
internal class PumpNozzleEntity(
    nozzleNumber: Int,
    fuelType: FuelType
) : BaseEntity() {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pump_id", nullable = false)
    var pump: PumpEntity? = null

    @Column(name = "nozzle_number", nullable = false)
    @field:Min(value = 1, message = "노즐 번호는 1 이상이어야 합니다.")
    var nozzleNumber: Int = nozzleNumber
        protected set

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type", nullable = false, length = 20)
    @field:NotNull(message = "유종은 필수 입력값 입니다.")
    var fuelType: FuelType = fuelType
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "connected_tank_id", nullable = false)
    @field:NotNull(message = "연결된 탱크는 필수 입력값 입니다.")
    var connectedTank: FuelTankEntity? = null
        protected set

    fun connectTank(tank: FuelTankEntity) {
        require(tank.fuelType == this.fuelType) {
            "노즐의 유종(${this.fuelType.displayName})과 " +
                "탱크의 유종(${tank.fuelType.displayName})이 일치하지 않습니다."
        }
        this.connectedTank = tank
    }

    fun disconnectTank() {
        this.connectedTank = null
    }

    fun canDispense(): Boolean {
        val tank = connectedTank ?: return false
        return tank.status.isOperational() && tank.currentAmount > tank.safetyStock
    }

    protected constructor() : this(0, FuelType.GASOLINE_REGULAR)
}
