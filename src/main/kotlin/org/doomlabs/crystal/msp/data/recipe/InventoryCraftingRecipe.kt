package org.doomlabs.crystal.msp.data.recipe

import kotlinx.serialization.Serializable

@Serializable
data class InventoryCraftingRecipe(
    val pattern: Array<String>,
    val result: Array<String>,
    val ingredients: Map<Char, CraftingIngredient>,
    val mirror: MirrorSetting = MirrorSetting(false, arrayOf()),
    val rotation: Boolean = false,
    val achievements: Map<String, Array<String>>? = null
) {
    @Serializable
    data class MirrorSetting(val enable: Boolean, val axis: Array<Char>) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as MirrorSetting

            if (enable != other.enable) return false
            if (!axis.contentEquals(other.axis)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = enable.hashCode()
            result = 31 * result + axis.contentHashCode()
            return result
        }
    }

    @Serializable
    data class CraftingIngredient(val id: String)

    val dimension: Pair<Int, Int>
        get() = pattern.maxOf { it.length } to pattern.size

    val inputCount = pattern.sumOf { it.count { key -> key in ingredients } }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InventoryCraftingRecipe

        if (!pattern.contentEquals(other.pattern)) return false
        if (!result.contentEquals(other.result)) return false
        if (rotation != other.rotation) return false
        if (mirror != other.mirror) return false
        if (ingredients != other.ingredients) return false

        return true
    }

    override fun hashCode(): Int {
        var result1 = pattern.contentHashCode()
        result1 = 31 * result1 + result.contentHashCode()
        result1 = 31 * result1 + rotation.hashCode()
        result1 = 31 * result1 + mirror.hashCode()
        result1 = 31 * result1 + ingredients.hashCode()
        return result1
    }
}
