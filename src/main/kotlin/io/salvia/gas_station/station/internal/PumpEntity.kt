package io.salvia.gas_station.station.internal

import io.salvia.gas_station.shared.BaseEntity
import io.salvia.gas_station.shared.Inspectable
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDate

/**
 * 주유기 엔티티 (station 모듈 내부 전용)
 */
@Entity
@Table(
    name = "pumps",
    indexes = [
        Index(name = "idx_pumps_station_number", columnList = "station_id, pump_number"),
        Index(name = "idx_pumps_status", columnList = "status")
    ],
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_station_pump_number",
            columnNames = ["station_id", "pump_number"]
        )
    ]
)
internal class PumpEntity(
    pumpNumber: String,
    nozzleCount: Int
) : BaseEntity(), Inspectable, Statusable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    var station: StationEntity? = null

    @Column(name = "pump_number", nullable = false, length = 20)
    @field:NotBlank(message = "주유기 번호는 필수 입력값 입니다.")
    @field:Size(max = 20, message = "주유기 번호는 20자 이하여야 합니다.")
    var pumpNumber: String = pumpNumber
        protected set

    @Column(name = "nozzle_count", nullable = false)
    @field:Min(value = 1, message = "노즐 개수는 1개 이상이어야 합니다.")
    var nozzleCount: Int = nozzleCount
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

    @OneToMany(
        mappedBy = "pump",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    private val _nozzles: MutableList<PumpNozzleEntity> = mutableListOf()

    val nozzles: List<PumpNozzleEntity>
        get() = _nozzles.toList()

    fun addNozzle(nozzle: PumpNozzleEntity) {
        require(_nozzles.size < nozzleCount) {
            "노즐 개수는 ${nozzleCount}개를 초과할 수 없습니다."
        }
        require(_nozzles.none { it.fuelType == nozzle.fuelType }) {
            "이미 ${nozzle.fuelType.displayName} 노즐이 존재합니다."
        }

        _nozzles.add(nozzle)
        nozzle.pump = this
    }

    fun removeNozzle(nozzle: PumpNozzleEntity) {
        _nozzles.remove(nozzle)
        nozzle.pump = null
    }

    protected constructor() : this("", 0)
}
