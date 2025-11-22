package com.maksimowiczm.foodyou.app.ui.food.diary.quickadd

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.ui.food.diary.component.rememberChipsDatePickerState
import com.maksimowiczm.foodyou.app.ui.food.diary.component.rememberChipsMealPickerState
import com.maksimowiczm.foodyou.common.compose.extension.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.common.extension.minus
import com.maksimowiczm.foodyou.common.extension.plus
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import kotlinx.datetime.LocalDate
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.time.Duration.Companion.days
import org.jetbrains.compose.resources.stringResource

@Composable
fun CreateQuickAddScreen(
    onBack: () -> Unit,
    onSave: () -> Unit,
    mealId: Long,
    date: LocalDate,
    modifier: Modifier = Modifier,
) {
    val viewModel: CreateQuickAddViewModel = koinViewModel { parametersOf(date, mealId) }
    val defaultQuickAddName = stringResource(Res.string.headline_quick_add)

    val latestOnSave by rememberUpdatedState(onSave)
    LaunchedCollectWithLifecycle(viewModel.uiEvents) {
        when (it) {
            QuickAddUiEvent.Saved -> latestOnSave()
        }
    }

    val meals = viewModel.meals.collectAsStateWithLifecycle().value
    val today by viewModel.today.collectAsStateWithLifecycle()

    if (meals == null || meals.isEmpty()) {
        // TODO loading state
        return
    }

    val selectedMealName =
        remember(meals, mealId) {
                meals.firstOrNull { it.id == mealId } ?: meals.firstOrNull()
            }
            ?.name

    val formState = rememberQuickAddFormState()
    val dateState =
        rememberChipsDatePickerState(
            today = today,
            initialDates =
                listOf(today.minus(1.days), today, today.plus(1.days), date)
                    .distinct()
                    .sorted(),
            selectedDate = date,
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

            viewModel.addEntry(
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
