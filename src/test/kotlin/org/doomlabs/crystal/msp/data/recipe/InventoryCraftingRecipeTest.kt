package org.doomlabs.crystal.msp.data.recipe

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class InventoryCraftingRecipeTest {
    @Test
    fun testDeserialization() {
        val infWater = """
            {
              "pattern": [ "FEF" ],
              "result": [ "FFF" ],
              "rotation": true,
              "mirror": {
                "enable": false,
                "axis": []
              },
              "ingredients": {
                "F": { "id": "minecraft:water_bucket" },
                "E": { "id": "minecraft:bucket" }
              }
            }
        """.trimIndent()
        val data = Json.decodeFromString<InventoryCraftingRecipe>(infWater)
        assertEquals(3 to 1, data.dimension)
        assertEquals(3, data.inputCount)
        assertEquals("FEF", data.pattern[0])
        assertEquals("FFF", data.result[0])
        assertFalse(data.mirror.enable)
        assertEquals(0, data.mirror.axis.size)
        assertEquals(2, data.ingredients.size)
    }

    @Test
    fun testSerialization() {
        val data = InventoryCraftingRecipe(
            arrayOf("S"),
            arrayOf("C"),
            mapOf(
                'S' to InventoryCraftingRecipe.CraftingIngredient("minecraft:stick"),
                'C' to InventoryCraftingRecipe.CraftingIngredient("minecraft:crafting_table")
            ),
            InventoryCraftingRecipe.MirrorSetting(true, arrayOf('x')),
        )

        val localJson = Json { prettyPrint = true }
        assertEquals("""
            {
                "pattern": [
                    "S"
                ],
                "result": [
                    "C"
                ],
                "ingredients": {
                    "S": {
                        "id": "minecraft:stick"
                    },
                    "C": {
                        "id": "minecraft:crafting_table"
                    }
                },
                "mirror": {
                    "enable": true,
                    "axis": [
                        "x"
                    ]
                }
            }
        """.trimIndent(), localJson.encodeToString<InventoryCraftingRecipe>(data))
    }
}