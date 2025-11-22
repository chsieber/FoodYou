package com.maksimowiczm.foodyou.app.ui.home.meals.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.ui.common.component.FoodErrorListItem
import com.maksimowiczm.foodyou.app.ui.common.component.FoodListItem
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalEnergyFormatter
import com.maksimowiczm.foodyou.app.ui.common.utility.stringResourceWithWeight
import com.maksimowiczm.foodyou.common.compose.utility.formatClipZeros
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun MealFoodListItem(
    entry: MealEntryModel,
    color: Color,
    contentColor: Color,
    shape: Shape,
    modifier: Modifier = Modifier,
) {
    when (entry) {
        is FoodMealEntryModel ->
            MealFoodListItem(
                entry = entry,
                color = color,
                contentColor = contentColor,
                shape = shape,
                modifier = modifier,
            )

        is ManualMealEntryModel ->
            MealFoodListItem(
                entry = entry,
                color = color,
                contentColor = contentColor,
                shape = shape,
                modifier = modifier,
            )
    }
}

@Composable
internal fun MealFoodListItem(
    entry: FoodMealEntryModel,
    color: Color,
    contentColor: Color,
    shape: Shape,
    modifier: Modifier = Modifier,
) {
    val g = stringResource(Res.string.unit_gram_short)

    val proteinsString = entry.proteins?.let { it.formatClipZeros("%.1f") + " $g" }

    val carbohydratesString = entry.carbohydrates?.let { it.formatClipZeros("%.1f") + " $g" }

    val fatsString = entry.fats?.let { it.formatClipZeros("%.1f") + " $g" }

    val caloriesString = entry.energy?.let { LocalEnergyFormatter.current.formatEnergy(it) }

    val measurementString =
        entry.measurement.stringResourceWithWeight(
            totalWeight = entry.totalWeight,
            servingWeight = entry.servingWeight,
            isLiquid = entry.isLiquid,
        )

    if (measurementString == null) {
        FoodErrorListItem(
            headline = entry.name,
            errorMessage = stringResource(Res.string.error_measurement_error),
            modifier = modifier,
        )
    } else if (
        proteinsString == null ||
            carbohydratesString == null ||
            fatsString == null ||
            caloriesString == null
    ) {
        FoodErrorListItem(
            headline = entry.name,
            errorMessage = stringResource(Res.string.error_food_is_missing_required_fields),
            modifier = modifier,
        )
    } else {
        FoodListItem(
            name = { Text(entry.name) },
            proteins = { Text(text = proteinsString, style = MaterialTheme.typography.bodySmall) },
            carbohydrates = {
                Text(text = carbohydratesString, style = MaterialTheme.typography.bodySmall)
            },
            fats = { Text(text = fatsString, style = MaterialTheme.typography.bodySmall) },
            calories = { Text(text = caloriesString, style = MaterialTheme.typography.bodySmall) },
            measurement = {
                Text(text = measurementString, style = MaterialTheme.typography.bodySmall)
            },
            isRecipe = entry.isRecipe,
            modifier = modifier,
            containerColor = color,
            contentColor = contentColor,
            shape = shape,
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
        )
    }
}

@Composable
internal fun MealFoodListItem(
    entry: ManualMealEntryModel,
    color: Color,
    contentColor: Color,
    shape: Shape,
    modifier: Modifier = Modifier,
) {
    val g = stringResource(Res.string.unit_gram_short)

    val dash = stringResource(Res.string.em_dash)

    val proteinsString = entry.proteins?.let { it.formatClipZeros("%.1f") + " $g" } ?: dash
    val carbohydratesString = entry.carbohydrates?.let { it.formatClipZeros("%.1f") + " $g" } ?: dash
    val fatsString = entry.fats?.let { it.formatClipZeros("%.1f") + " $g" } ?: dash
    val caloriesString =
        entry.energy?.let { LocalEnergyFormatter.current.formatEnergy(it) } ?: dash
    val name = entry.name.ifBlank { stringResource(Res.string.headline_quick_add) }

    FoodListItem(
        name = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(name)
                Icon(
                    imageVector = Icons.Outlined.Bolt,
                    contentDescription = stringResource(Res.string.headline_quick_add),
                    modifier = Modifier.size(18.dp),
                )
            }
        },
        proteins = { Text(text = proteinsString, style = MaterialTheme.typography.bodySmall) },
        carbohydrates = { Text(text = carbohydratesString, style = MaterialTheme.typography.bodySmall) },
        fats = { Text(text = fatsString, style = MaterialTheme.typography.bodySmall) },
        calories = { Text(text = caloriesString, style = MaterialTheme.typography.bodySmall) },
        measurement = {
            Text(
                text = stringResource(Res.string.headline_quick_add),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
        },
        isRecipe = false,
        modifier = modifier,
        containerColor = color,
        contentColor = contentColor,
        shape = shape,
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
    )
}
