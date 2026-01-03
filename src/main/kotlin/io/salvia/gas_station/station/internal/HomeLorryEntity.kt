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
 * 홈로리 (이동식 탱크) 엔티티 (station 모듈 내부 전용)
 */
@Entity
@Table(
    name = "home_lorry",
    indexes = [
        Index(name = "idx_home_lorry_station_number", columnList = "station_id, home_lorry_number"),
        Index(name = "idx_home_lorry_fuel_type", columnList = "fuel_type"),
        Index(name = "idx_home_lorry_status", columnList = "status")
    ],
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_station_home_lorry_number",
            columnNames = ["station_id", "home_lorry_number"]
        )
    ]
)
internal class HomeLorryEntity(
    homeLorryNumber: String,
    fuelType: FuelType,
    capacity: BigDecimal
) : BaseEntity(), Statusable, Inspectable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    var station: StationEntity? = null

    @Column(name = "home_lorry_number", nullable = false, length = 20)
    @field:NotBlank(message = "홈로리 번호는 필수 입력값 입니다.")
    @field:Size(max = 20, message = "홈로리 번호는 20자 이하여야 합니다.")
    var homeLorryNumber: String = homeLorryNumber
        protected set

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type", nullable = false, length = 30)
    @field:NotNull(message = "유종은 필수 입력값 입니다.")
    var fuelType: FuelType = fuelType
        protected set

    @Column(name = "capacity", nullable = false, precision = 12, scale = 2)
    @field:NotNull(message = "홈로리 용량은 필수 입력값 입니다.")
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

    @Column(name = "last_inspection_date")
    override var lastInspectionDate: LocalDate? = null

    @Column(name = "next_inspection_date")
    override var nextInspectionDate: LocalDate? = null

    protected constructor() : this("", FuelType.GASOLINE_REGULAR, BigDecimal.ZERO)
}
