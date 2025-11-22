package com.maksimowiczm.foodyou.app.ui.food.diary.quickadd

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.ui.food.diary.component.rememberChipsDatePickerState
import com.maksimowiczm.foodyou.app.ui.food.diary.component.rememberChipsMealPickerState
import com.maksimowiczm.foodyou.common.compose.extension.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.common.extension.minus
import com.maksimowiczm.foodyou.common.extension.plus
import com.maksimowiczm.foodyou.fooddiary.domain.entity.ManualDiaryEntryId
import foodyou.app.generated.resources.*
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import foodyou.app.generated.resources.Res
import kotlin.time.Duration.Companion.days
import org.jetbrains.compose.resources.stringResource

@Composable
fun UpdateQuickAddScreen(
    onBack: () -> Unit,
    onSave: () -> Unit,
    id: Long,
    modifier: Modifier = Modifier,
) {
    val viewModel: UpdateQuickAddViewModel = koinViewModel { parametersOf(ManualDiaryEntryId(id)) }
    val defaultQuickAddName = stringResource(Res.string.headline_quick_add)

    val latestOnSave by rememberUpdatedState(onSave)
    LaunchedCollectWithLifecycle(viewModel.uiEvents) {
        when (it) {
            QuickAddUiEvent.Saved -> latestOnSave()
        }
    }

    val meals = viewModel.meals.collectAsStateWithLifecycle().value
    val today by viewModel.today.collectAsStateWithLifecycle()
    val entry = viewModel.entry.collectAsStateWithLifecycle().value

    if (entry == null || meals == null || meals.isEmpty()) {
        // TODO loading state
        return
    }

    val formState =
        rememberQuickAddFormState(
            name = entry.name,
            energy = entry.nutritionFacts.energy.value,
            proteins = entry.nutritionFacts.proteins.value,
            carbohydrates = entry.nutritionFacts.carbohydrates.value,
            fats = entry.nutritionFacts.fats.value,
        )

    val selectedMealName =
        remember(meals, entry.mealId) {
                meals.firstOrNull { it.id == entry.mealId } ?: meals.firstOrNull()
            }
            ?.name

    val dateState =
        rememberChipsDatePickerState(
            today = today,
            initialDates =
                listOf(today.minus(1.days), today, today.plus(1.days), entry.date)
                    .distinct()
                    .sorted(),
            selectedDate = entry.date,
        )
    val mealState =
        rememberChipsMealPickerState(meals = meals.map { it.name }, selectedMeal = selectedMealName)

    QuickAddScreen(
        onBack = onBack,
        onSave = {
            val selectedMealId =
                mealState.selectedMeal?.let { mealName ->
                    meals.firstOrNull { it.name == mealName }?.id
                }
                    ?: return@QuickAddScreen

            val energy =
                formState.energy.value
                    ?: calculateQuickAddEnergy(
                        proteins = formState.proteins.value,
                        carbohydrates = formState.carbohydrates.value,
                        fats = formState.fats.value,
                    )
                    ?: return@QuickAddScreen

            val values =
                QuickAddValues(
                    name =
                        formState.name.value.ifBlank {
                            defaultQuickAddName
                        },
                    energy = energy,
                    proteins = formState.proteins.value,
                    carbohydrates = formState.carbohydrates.value,
                    fats = formState.fats.value,
                )

            viewModel.updateEntry(
                values = values,
                mealId = selectedMealId,
                date = dateState.selectedDate,
            )
        },
        modifier = modifier,
        formState = formState,
        dateState = dateState,
        mealState = mealState,
    )
}
