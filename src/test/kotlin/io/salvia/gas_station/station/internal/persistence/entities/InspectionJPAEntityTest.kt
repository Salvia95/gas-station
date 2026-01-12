package io.salvia.gas_station.station.internal.persistence.entities

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate

@DisplayName("InspectionJPAEntity 단위 테스트")
class InspectionJPAEntityTest {

    private lateinit var inspection: InspectionEntity

    @BeforeEach
    fun setUp() {
        inspection = InspectionEntity(
            inspectionType = InspectionType.REGULAR,
            inspectionTarget = InspectionTarget.STATION,
            scheduledDate = LocalDate.of(2024, 3, 15),
            inspector = "홍길동"
        )
    }

    @Nested
    @DisplayName("초기 상태 테스트")
    inner class InitialStateTest {

        @Test
        @DisplayName("생성 시 기본 상태는 SCHEDULED이다")
        fun initialStatusIsScheduled() {
            assertEquals(InspectionStatus.SCHEDULED, inspection.status)
        }

        @Test
        @DisplayName("생성 시 점검 실시일은 null이다")
        fun initialInspectionDateIsNull() {
            assertNull(inspection.inspectionDate)
        }

        @Test
        @DisplayName("생성 시 대상 ID는 null이다")
        fun initialTargetIdIsNull() {
            assertNull(inspection.targetId)
        }

        @Test
        @DisplayName("점검 유형, 대상, 예정일, 점검자가 올바르게 설정된다")
        fun initialPropertiesAreSet() {
            assertEquals(InspectionType.REGULAR, inspection.inspectionType)
            assertEquals(InspectionTarget.STATION, inspection.inspectionTarget)
            assertEquals(LocalDate.of(2024, 3, 15), inspection.scheduledDate)
            assertEquals("홍길동", inspection.inspector)
        }
    }

    @Nested
    @DisplayName("InspectionType 열거형 테스트")
    inner class InspectionTypeTest {

        @Test
        @DisplayName("REGULAR는 정기 점검이다")
        fun regularIsRegular() {
            assertTrue(InspectionType.REGULAR.isRegular())
        }

        @Test
        @DisplayName("LEGAL은 정기 점검이다")
        fun legalIsRegular() {
            assertTrue(InspectionType.LEGAL.isRegular())
        }

        @Test
        @DisplayName("EMERGENCY는 정기 점검이 아니다")
        fun emergencyIsNotRegular() {
            assertFalse(InspectionType.EMERGENCY.isRegular())
        }

        @Test
        @DisplayName("SELF는 정기 점검이 아니다")
        fun selfIsNotRegular() {
            assertFalse(InspectionType.SELF.isRegular())
        }

        @Test
        @DisplayName("SPECIAL은 정기 점검이 아니다")
        fun specialIsNotRegular() {
            assertFalse(InspectionType.SPECIAL.isRegular())
        }

        @Test
        @DisplayName("REGULAR는 문서화가 필요하지 않다")
        fun regularDoesNotRequireDocumentation() {
            assertFalse(InspectionType.REGULAR.requiredDocumentation)
        }

        @Test
        @DisplayName("LEGAL은 문서화가 필요하다")
        fun legalRequiresDocumentation() {
            assertTrue(InspectionType.LEGAL.requiredDocumentation)
        }

        @Test
        @DisplayName("EMERGENCY는 문서화가 필요하다")
        fun emergencyRequiresDocumentation() {
            assertTrue(InspectionType.EMERGENCY.requiredDocumentation)
        }

        @Test
        @DisplayName("SELF는 문서화가 필요하지 않다")
        fun selfDoesNotRequireDocumentation() {
            assertFalse(InspectionType.SELF.requiredDocumentation)
        }

        @Test
        @DisplayName("SPECIAL은 문서화가 필요하다")
        fun specialRequiresDocumentation() {
            assertTrue(InspectionType.SPECIAL.requiredDocumentation)
        }
    }

    @Nested
    @DisplayName("InspectionTarget 열거형 테스트")
    inner class InspectionTargetTest {

        @Test
        @DisplayName("모든 점검 대상이 올바른 설명을 가진다")
        fun allTargetsHaveDescription() {
            assertEquals("주유소 전체", InspectionTarget.STATION.description)
            assertEquals("연료탱크", InspectionTarget.FUEL_TANK.description)
            assertEquals("주유기", InspectionTarget.PUMP.description)
            assertEquals("소방 설비", InspectionTarget.FIRE_SAFETY.description)
            assertEquals("전기 설비", InspectionTarget.ELECTRICAL.description)
            assertEquals("기타", InspectionTarget.OTHER.description)
        }
    }

    @Nested
    @DisplayName("InspectionStatus 열거형 테스트")
    inner class InspectionStatusTest {

        @Test
        @DisplayName("SCHEDULED는 완료되지 않은 상태이다")
        fun scheduledIsNotCompleted() {
            assertFalse(InspectionStatus.SCHEDULED.isCompleted())
        }

        @Test
        @DisplayName("IN_PROGRESS는 완료되지 않은 상태이다")
        fun inProgressIsNotCompleted() {
            assertFalse(InspectionStatus.IN_PROGRESS.isCompleted())
        }

        @Test
        @DisplayName("COMPLETED_NORMAL은 완료 상태이다")
        fun completedNormalIsCompleted() {
            assertTrue(InspectionStatus.COMPLETED_NORMAL.isCompleted())
        }

        @Test
        @DisplayName("COMPLETED_WARNING은 완료 상태이다")
        fun completedWarningIsCompleted() {
            assertTrue(InspectionStatus.COMPLETED_WARNING.isCompleted())
        }

        @Test
        @DisplayName("COMPLETED_ACTION_REQUIRED는 완료 상태이다")
        fun completedActionRequiredIsCompleted() {
            assertTrue(InspectionStatus.COMPLETED_ACTION_REQUIRED.isCompleted())
        }

        @Test
        @DisplayName("CANCELED는 완료되지 않은 상태이다")
        fun canceledIsNotCompleted() {
            assertFalse(InspectionStatus.CANCELED.isCompleted())
        }

        @Test
        @DisplayName("COMPLETED_NORMAL은 조치가 필요하지 않다")
        fun completedNormalDoesNotNeedAction() {
            assertFalse(InspectionStatus.COMPLETED_NORMAL.needAction())
        }

        @Test
        @DisplayName("COMPLETED_WARNING은 조치가 필요하다")
        fun completedWarningNeedsAction() {
            assertTrue(InspectionStatus.COMPLETED_WARNING.needAction())
        }

        @Test
        @DisplayName("COMPLETED_ACTION_REQUIRED는 조치가 필요하다")
        fun completedActionRequiredNeedsAction() {
            assertTrue(InspectionStatus.COMPLETED_ACTION_REQUIRED.needAction())
        }

        @Test
        @DisplayName("SCHEDULED는 조치가 필요하지 않다")
        fun scheduledDoesNotNeedAction() {
            assertFalse(InspectionStatus.SCHEDULED.needAction())
        }

        @Test
        @DisplayName("IN_PROGRESS는 조치가 필요하지 않다")
        fun inProgressDoesNotNeedAction() {
            assertFalse(InspectionStatus.IN_PROGRESS.needAction())
        }

        @Test
        @DisplayName("CANCELED는 조치가 필요하지 않다")
        fun canceledDoesNotNeedAction() {
            assertFalse(InspectionStatus.CANCELED.needAction())
        }
    }

    @Nested
    @DisplayName("다양한 점검 유형 테스트")
    inner class VariousInspectionTypesTest {

        @Test
        @DisplayName("법정 점검을 생성할 수 있다")
        fun createLegalInspection() {
            // given & when
            val legalInspection = InspectionEntity(
                inspectionType = InspectionType.LEGAL,
                inspectionTarget = InspectionTarget.FUEL_TANK,
                scheduledDate = LocalDate.of(2024, 6, 1),
                inspector = "김철수"
            )

            // then
            assertEquals(InspectionType.LEGAL, legalInspection.inspectionType)
            assertTrue(legalInspection.inspectionType.isRegular())
            assertTrue(legalInspection.inspectionType.requiredDocumentation)
        }

        @Test
        @DisplayName("긴급 점검을 생성할 수 있다")
        fun createEmergencyInspection() {
            // given & when
            val emergencyInspection = InspectionEntity(
                inspectionType = InspectionType.EMERGENCY,
                inspectionTarget = InspectionTarget.FIRE_SAFETY,
                scheduledDate = LocalDate.now(),
                inspector = "이영희"
            )

            // then
            assertEquals(InspectionType.EMERGENCY, emergencyInspection.inspectionType)
            assertFalse(emergencyInspection.inspectionType.isRegular())
            assertTrue(emergencyInspection.inspectionType.requiredDocumentation)
        }

        @Test
        @DisplayName("자체 점검을 생성할 수 있다")
        fun createSelfInspection() {
            // given & when
            val selfInspection = InspectionEntity(
                inspectionType = InspectionType.SELF,
                inspectionTarget = InspectionTarget.PUMP,
                scheduledDate = LocalDate.now().plusDays(7),
                inspector = "박민수"
            )

            // then
            assertEquals(InspectionType.SELF, selfInspection.inspectionType)
            assertFalse(selfInspection.inspectionType.isRegular())
            assertFalse(selfInspection.inspectionType.requiredDocumentation)
        }
    }

    @Nested
    @DisplayName("다양한 점검 대상 테스트")
    inner class VariousInspectionTargetsTest {

        @Test
        @DisplayName("연료탱크 점검을 생성할 수 있다")
        fun createFuelTankInspection() {
            // given & when
            val tankInspection = InspectionEntity(
                inspectionType = InspectionType.REGULAR,
                inspectionTarget = InspectionTarget.FUEL_TANK,
                scheduledDate = LocalDate.of(2024, 4, 1),
                inspector = "탱크 전문가"
            )

            // then
            assertEquals(InspectionTarget.FUEL_TANK, tankInspection.inspectionTarget)
        }

        @Test
        @DisplayName("주유기 점검을 생성할 수 있다")
        fun createPumpInspection() {
            // given & when
            val pumpInspection = InspectionEntity(
                inspectionType = InspectionType.REGULAR,
                inspectionTarget = InspectionTarget.PUMP,
                scheduledDate = LocalDate.of(2024, 5, 1),
                inspector = "펌프 전문가"
            )

            // then
            assertEquals(InspectionTarget.PUMP, pumpInspection.inspectionTarget)
        }

        @Test
        @DisplayName("소방 설비 점검을 생성할 수 있다")
        fun createFireSafetyInspection() {
            // given & when
            val fireSafetyInspection = InspectionEntity(
                inspectionType = InspectionType.LEGAL,
                inspectionTarget = InspectionTarget.FIRE_SAFETY,
                scheduledDate = LocalDate.of(2024, 6, 1),
                inspector = "소방 점검관"
            )

            // then
            assertEquals(InspectionTarget.FIRE_SAFETY, fireSafetyInspection.inspectionTarget)
        }

        @Test
        @DisplayName("전기 설비 점검을 생성할 수 있다")
        fun createElectricalInspection() {
            // given & when
            val electricalInspection = InspectionEntity(
                inspectionType = InspectionType.SPECIAL,
                inspectionTarget = InspectionTarget.ELECTRICAL,
                scheduledDate = LocalDate.of(2024, 7, 1),
                inspector = "전기 기사"
            )

            // then
            assertEquals(InspectionTarget.ELECTRICAL, electricalInspection.inspectionTarget)
        }
    }

    @Nested
    @DisplayName("복합 시나리오 테스트")
    inner class ComplexScenarioTest {

        @Test
        @DisplayName("법정 점검이 완료되어 조치가 필요한 경우")
        fun legalInspectionCompletedWithActionRequired() {
            // given
            val legalInspection = InspectionEntity(
                inspectionType = InspectionType.LEGAL,
                inspectionTarget = InspectionTarget.FUEL_TANK,
                scheduledDate = LocalDate.of(2024, 3, 1),
                inspector = "법정 점검관"
            )

            // when: 상태를 COMPLETED_ACTION_REQUIRED로 변경 (실제로는 setter가 protected라 변경 불가)
            // 여기서는 초기 상태만 테스트
            // then
            assertTrue(legalInspection.inspectionType.requiredDocumentation)
            assertTrue(legalInspection.inspectionType.isRegular())

            // 완료 상태일 때 조치 필요 여부 확인
            assertTrue(InspectionStatus.COMPLETED_ACTION_REQUIRED.isCompleted())
            assertTrue(InspectionStatus.COMPLETED_ACTION_REQUIRED.needAction())
        }

        @Test
        @DisplayName("긴급 점검이 정상 완료된 경우")
        fun emergencyInspectionCompletedNormally() {
            // given
            val emergencyInspection = InspectionEntity(
                inspectionType = InspectionType.EMERGENCY,
                inspectionTarget = InspectionTarget.FIRE_SAFETY,
                scheduledDate = LocalDate.now(),
                inspector = "긴급 점검관"
            )

            // then
            assertFalse(emergencyInspection.inspectionType.isRegular())
            assertTrue(emergencyInspection.inspectionType.requiredDocumentation)

            // 정상 완료 시
            assertTrue(InspectionStatus.COMPLETED_NORMAL.isCompleted())
            assertFalse(InspectionStatus.COMPLETED_NORMAL.needAction())
        }

        @Test
        @DisplayName("주유소 전체 정기 점검이 예정된 경우")
        fun stationWideRegularInspectionScheduled() {
            // given
            val stationInspection = InspectionEntity(
                inspectionType = InspectionType.REGULAR,
                inspectionTarget = InspectionTarget.STATION,
                scheduledDate = LocalDate.now().plusMonths(1),
                inspector = "종합 점검관"
            )

            // then
            assertEquals(InspectionType.REGULAR, stationInspection.inspectionType)
            assertEquals(InspectionTarget.STATION, stationInspection.inspectionTarget)
            assertEquals(InspectionStatus.SCHEDULED, stationInspection.status)
            assertTrue(stationInspection.inspectionType.isRegular())
            assertFalse(stationInspection.inspectionType.requiredDocumentation)
            assertFalse(stationInspection.status.isCompleted())
            assertFalse(stationInspection.status.needAction())
        }

        @Test
        @DisplayName("점검 상태 전체 흐름 검증")
        fun inspectionStatusFlow() {
            // SCHEDULED -> IN_PROGRESS -> COMPLETED
            assertFalse(InspectionStatus.SCHEDULED.isCompleted())
            assertFalse(InspectionStatus.IN_PROGRESS.isCompleted())
            assertTrue(InspectionStatus.COMPLETED_NORMAL.isCompleted())
            assertTrue(InspectionStatus.COMPLETED_WARNING.isCompleted())
            assertTrue(InspectionStatus.COMPLETED_ACTION_REQUIRED.isCompleted())

            // CANCELED는 별도 흐름
            assertFalse(InspectionStatus.CANCELED.isCompleted())
        }

        @Test
        @DisplayName("특정 장비에 대한 점검을 생성할 수 있다")
        fun createInspectionForSpecificEquipment() {
            // given: targetId를 설정할 수 있는 점검 (실제로는 setter가 protected)
            val pumpInspection = InspectionEntity(
                inspectionType = InspectionType.REGULAR,
                inspectionTarget = InspectionTarget.PUMP,
                scheduledDate = LocalDate.of(2024, 4, 1),
                inspector = "주유기 점검관"
            )

            // then
            assertEquals(InspectionTarget.PUMP, pumpInspection.inspectionTarget)
            assertNull(pumpInspection.targetId) // 초기값은 null
        }
    }

    @Nested
    @DisplayName("점검 유형별 문서화 요구사항 테스트")
    inner class DocumentationRequirementsTest {

        @Test
        @DisplayName("문서화가 필요한 점검 유형들")
        fun inspectionTypesRequiringDocumentation() {
            val requiringDocumentation = listOf(
                InspectionType.LEGAL,
                InspectionType.EMERGENCY,
                InspectionType.SPECIAL
            )

            requiringDocumentation.forEach { type ->
                assertTrue(type.requiredDocumentation, "${type.description}은 문서화가 필요해야 함")
            }
        }

        @Test
        @DisplayName("문서화가 필요하지 않은 점검 유형들")
        fun inspectionTypesNotRequiringDocumentation() {
            val notRequiringDocumentation = listOf(
                InspectionType.REGULAR,
                InspectionType.SELF
            )

            notRequiringDocumentation.forEach { type ->
                assertFalse(type.requiredDocumentation, "${type.description}은 문서화가 필요하지 않아야 함")
            }
        }
    }
}
