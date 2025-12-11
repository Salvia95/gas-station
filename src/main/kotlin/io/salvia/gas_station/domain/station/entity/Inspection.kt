package io.salvia.gas_station.domain.station.entity

import io.salvia.gas_station.common.entity.BaseEntity
import io.salvia.gas_station.domain.station.enums.InspectionStatus
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
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDate

@Entity
@Table(
    name = "inspections",
    indexes = [
        Index(name = "idx_station_date", columnList = "station_id, inspection_date"),
        Index(name = "idx_status", columnList = "status"),
        Index(name = "idx_next_date", columnList = "next_inspection_date")
    ]
)
class Inspection(
    inspectionType: InspectionType,
    inspectionTarget: InspectionTarget,
    scheduledDate: LocalDate,
    inspector: String
) : BaseEntity() {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    var station: GasStaion? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "inspection_type", nullable = false, length = 20)
    @field:NotNull(message = "점검 유형은 필수 입력값 입니다.")
    var inspectionType: InspectionType = inspectionType
        protected set

    @Enumerated(EnumType.STRING)
    @Column(name = "inspection_target", nullable = false, length = 20)
    @field:NotNull(message = "점검 대상은 필수 입력값 입니다.")
    var inspectionTarget: InspectionTarget = inspectionTarget
        protected set

    @Column(name = "target_id")
    var targetId: Long? = null
        protected set

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    var status: InspectionStatus = InspectionStatus.SCHEDULED
        protected set

    @Column(name = "scheduled_date", nullable = false)
    @field:NotNull(message = "점검 예정일은 필수 입력값 입니다.")
    var scheduledDate: LocalDate = scheduledDate
        protected set

    @Column(name = "inspection_date")
    var inspectionDate: LocalDate? = null
        protected set

    @Column(name = "inspector", nullable = false, length = 100)
    @field:NotBlank(message = "점검자는 필수 입력값 입니다.")
    @field:Size(max = 100, message = "점검자명은 100자 이하여야 합니다.")
    var inspector: String = inspector
        protected set


}

@Entity
@Table(
    name = "inspection_agencies",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_business_registration_number",
            columnNames = ["business_registration_number"]
        )
    ]
)
class InspectionAgency(
    agencyName: String,
    businessRegistrationNumber: String,
    contact: String
) : BaseEntity() {

    @Column(name = "agency_name", nullable = false, length = 30)
    @field:NotBlank(message = "점검 기관명은 필수 입력값 입니다.")
    @field:Size(max = 30, message = "점검 기관명은 30자 이하여야 합니다.")
    var agencyName: String = agencyName
        protected set

    @Column(name = "business_registration_number", nullable = false, length = 20)

    var businessRegistrationNumber: String = businessRegistrationNumber
        protected set
}

enum class InspectionType(
    val description: String,
    val requiredDocumentation: Boolean
) {
    REGULAR("정기점검", false),
    LEGAL("법정점검", true),
    EMERGENCY("긴급점검", true),
    SELF("자체점검", false),
    SPECIAL("특별점검", true);

    fun isRegular(): Boolean {
        return this in listOf(REGULAR, LEGAL)
    }
}

enum class InspectionTarget(val description: String) {
    STATION("주유소 전체"),
    FUEL_TANK("연료탱크"),
    PUMP("주유기"),
    FIRE_SAFETY("소방 설비"),
    ELECTRICAL("전기 설비"),
    OTHER("기타");
}