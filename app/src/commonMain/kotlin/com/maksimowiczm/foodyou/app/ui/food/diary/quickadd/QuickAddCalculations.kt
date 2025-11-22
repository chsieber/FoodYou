package com.maksimowiczm.foodyou.app.ui.food.diary.quickadd

import com.maksimowiczm.foodyou.common.domain.food.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.common.domain.food.NutrientsHelper
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import kotlin.math.roundToInt

internal data class QuickAddValues(
    val name: String,
    val energy: Double,
    val proteins: Double?,
    val carbohydrates: Double?,
    val fats: Double?,
)

internal fun calculateQuickAddEnergy(
    proteins: Double?,
    carbohydrates: Double?,
    fats: Double?,
): Double? {
    if (proteins == null && carbohydrates == null && fats == null) {
        return null
    }

    val proteinsValue = proteins ?: 0.0
    val carbohydratesValue = carbohydrates ?: 0.0
    val fatsValue = fats ?: 0.0

    val energy =
        NutrientsHelper.calculateEnergy(
            proteins = proteinsValue,
            carbohydrates = carbohydratesValue,
            fats = fatsValue,
        )

    return energy.roundToInt().toDouble()
}

internal fun QuickAddValues.toNutritionFacts(): NutritionFacts =
    NutritionFacts(
        energy = energy.toNutrientValue(),
        proteins = proteins.toNutrientValue(),
        carbohydrates = carbohydrates.toNutrientValue(),
        fats = fats.toNutrientValue(),
    )
