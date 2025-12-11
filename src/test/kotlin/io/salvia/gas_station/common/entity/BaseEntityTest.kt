package io.salvia.gas_station.common.entity

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BaseEntityTest {

    @Test
    fun isNewShouldReturnTrueWhenNewEntity() {
        // Given
        val entity = TestEntity()

        // When/Then
        assertThat(entity.isNew).isTrue()
    }

    @Test
    fun isNewShouldReturnFalseWhenEntityWithId() {
        // Given
        val entity = TestEntity().apply {
            id = 1L
        }

        // When/Then
        assertThat(entity.isNew).isFalse()
    }

    @Test
    fun entitiesWithSameIdEquals() {
        // Given
        val entity1 = TestEntity().apply { id = 1L }
        val entity2 = TestEntity().apply { id = 1L }

        // When/Then
        assertThat(entity1).isEqualTo(entity2)
        assertThat(entity1.hashCode()).isEqualTo(entity2.hashCode())
    }

    @Test
    fun entitiesWithDifferentIdNotEquals() {
        // Given
        val entity1 = TestEntity().apply { id = 1L }
        val entity2 = TestEntity().apply { id = 2L }

        // When/Then
        assertThat(entity1).isNotEqualTo(entity2)
        assertThat(entity1.hashCode()).isNotEqualTo(entity2.hashCode())
    }

}

private class TestEntity: BaseEntity()