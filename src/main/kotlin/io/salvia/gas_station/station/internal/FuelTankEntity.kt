package io.salvia.gas_station.station.internal

import io.salvia.gas_station.shared.BaseEntity
import io.salvia.gas_station.shared.Inspectable
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
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.LocalDate

/**
 * 유류 탱크 엔티티 (station 모듈 내부 전용)
 */
@Entity
@Table(
    name = "fuel_tanks",
    indexes = [
        Index(name = "idx_station_tank_number", columnList = "station_id, tank_number"),
        Index(name = "idx_fuel_type", columnList = "fuel_type")
    ],
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_station_tank_number",
            columnNames = ["station_id", "tank_number"]
        )
    ]
)
internal class FuelTankEntity(
    tankNumber: String,
    fuelType: FuelType,
    capacity: BigDecimal
) : BaseEntity(), Statusable, Inspectable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    var station: StationEntity? = null

    @Column(name = "tank_number", nullable = false, length = 20)
    @field:NotBlank(message = "탱크 번호는 필수 입력값 입니다.")
    @field:Size(max = 20, message = "탱크 번호는 20자 이하여야 합니다.")
    var tankNumber: String = tankNumber
        protected set

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type", nullable = false, length = 30)
    @field:NotNull(message = "유종은 필수 입력값 입니다.")
    var fuelType: FuelType = fuelType
        protected set

    @Column(name = "capacity", nullable = false, precision = 12, scale = 2)
    @field:NotNull(message = "탱크 용량은 필수 입력값 입니다.")
    @field:Min(value = 0, message = "용량은 0 이상이어야 합니다.")
    var capacity: BigDecimal = capacity
        protected set

    @Column(name = "current_amount", nullable = false, precision = 12, scale = 2)
    var currentAmount: BigDecimal = BigDecimal.ZERO
        protected set

    @Column(name = "safety_stock", nullable = false, precision = 12, scale = 2)
    var safetyStock: BigDecimal = BigDecimal.ZERO
        protected set

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    override var status: EquipmentStatus = EquipmentStatus.ACTIVE

    @Column(name = "installation_date")
    var installationDate: LocalDate? = null
        protected set

    @Column(name = "last_inspection_date")
    override var lastInspectionDate: LocalDate? = null

    @Column(name = "next_inspection_date")
    override var nextInspectionDate: LocalDate? = null

    @Column(name = "notes", length = 500)
    var notes: String? = null
        protected set

    fun fill(amount: BigDecimal): BigDecimal {
        require(amount > BigDecimal.ZERO) { "충전량은 0보다 커야 합니다." }
        require(status == EquipmentStatus.ACTIVE) { "탱크가 정상 상태가 아닙니다." }

        val availableSpace = capacity - currentAmount
        val actualAmount = amount.min(availableSpace)

        currentAmount += actualAmount

        return actualAmount
    }

    fun consume(amount: BigDecimal) {
        require(amount > BigDecimal.ZERO) { "소비량은 0보다 커야 합니다." }
        require(currentAmount >= amount) {
            "[재고 부족] 재고: ${currentAmount}L, 요청: ${amount}L"
        }

        currentAmount -= amount
    }

    fun updateSafetyStock(amount: BigDecimal) {
        require(amount >= BigDecimal.ZERO) { "안전 재고량은 0 이상이어야 합니다." }
        require(amount <= capacity) { "안전 재고는 탱크 용량 이하여야 합니다." }

        this.safetyStock = amount
    }

    fun isBelowSafetyStock(): Boolean {
        return currentAmount < safetyStock
    }

    fun getStockRate(): BigDecimal {
        if (capacity == BigDecimal.ZERO) return BigDecimal.ZERO

        return (currentAmount / capacity * BigDecimal(100))
            .setScale(2, java.math.RoundingMode.HALF_UP)
    }

    fun setInstallationInfo(installationDate: LocalDate) {
        this.installationDate = installationDate
    }

    fun updateNotes(notes: String) {
        this.notes = notes
    }

    protected constructor() : this("", FuelType.GASOLINE_REGULAR, BigDecimal.ZERO)
}
