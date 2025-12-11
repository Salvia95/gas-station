package io.salvia.gas_station.domain.station.entity

import io.salvia.gas_station.common.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import java.time.LocalTime

@Entity
@Table(name = "station_operation_info")
class StationOperationInfo (
    stationType: StationType,
    is24Hours: Boolean,
    openingTime: LocalTime?,
    closingTime: LocalTime?
) : BaseEntity() {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "station_id",
        nullable = false,
        unique = true
    )
    var station: GasStaion? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "station_type", nullable = false, length = 20)
    @field:NotNull(message = "주유소 형태는 필수 입력값 입니다.")
    var stationType: StationType = stationType
        protected set

    @Column(name = "is_24_hours", nullable = false)
    var is24Hours: Boolean = is24Hours
        protected set

    @Column(name = "opening_time")
    var openingTime: LocalTime? = openingTime
        protected set

    @Column(name = "closing_time")
    var closingTime: LocalTime? = closingTime
        protected set

    @Column(name = "has_car_wash", nullable = false)
    var hasCarWash: Boolean = false
        protected set

    @Enumerated(EnumType.STRING)
    @Column(name = "car_wash_type", length = 20)
    var carWashType: CarWashType? = null
        protected set

    @Column(name = "has_convenience_store", nullable = false)
    var hasConvenienceStore: Boolean = false
        protected set

    @Column(name = "has_repair_shop", nullable = false)
    var hasRepairShop: Boolean = false
        protected set

    @Column(name = "has_rest_area", nullable = false)
    var hasRestArea: Boolean = false
        protected set

    @Column(name = "notes", length = 1000)
    var notes: String? = null
        protected set

    fun changeStationType(newType: StationType) {
        this.stationType = newType
    }

    fun setOperatingHours(
        is24Hours: Boolean,
        openingTime: LocalTime? = null,
        closingTime: LocalTime? = null
    ) {
        this.is24Hours = is24Hours

        if(is24Hours) {
            this.openingTime = null
            this.closingTime = null
        } else {
            require(openingTime != null && closingTime != null) {
                "24시간 운영이 아닌 경우 영업 시간을 지정해야 합니다."
            }

            this.openingTime = openingTime
            this.closingTime = closingTime
        }
    }

    fun setCarWash(hasCarWash: Boolean, carWashType: CarWashType? = null) {
        this.hasCarWash = hasCarWash

        if (hasCarWash) {
            require(carWashType != null) { "세차기가 있을 경우 세차기 유형을 지정해야 합니다." }
            this.carWashType = carWashType
        } else {
            this.carWashType = null
        }
    }

    fun setFacilities(
        hasConvenienceStore: Boolean = this.hasConvenienceStore,
        hasRepairShop: Boolean = this.hasRepairShop,
        hasRestArea: Boolean = this.hasRestArea
    ) {
        this.hasConvenienceStore = hasConvenienceStore
        this.hasRepairShop = hasRepairShop
        this.hasRestArea = hasRestArea
    }

    fun updateNotes(notes: String) {
        this.notes = notes
    }

    fun isCurrentlyOpen(): Boolean {
        if (is24Hours) return true

        val now = LocalTime.now()
        val opening = openingTime ?: return false
        val closing = closingTime ?: return false

        return if (closing > opening) {
            // 정상 케이스: 09:00 ~ 22:00
            now in opening..closing
        } else {
            // 자정 넘어가는 케이스: 22:00 ~ 02:00
            now >= opening || now <= closing
        }
    }

    // JPA를 위한 no-arg constructor
    protected constructor() : this(
        StationType.FULL_SERVICE,
        false,
        null,
        null
    )
}


enum class StationType(val description: String, val laborCostMultiplier: Double) {
    SELF_SERVICE("셀프", 0.0),
    FULL_SERVICE("유인", 1.0),
    HYBRID("혼합", 0.5)
}

enum class CarWashType(val description: String) {
    AUTOMATIC("자동 세차"),
    MANUAL("수동 세차"),
    HYBRID("혼합형")
}