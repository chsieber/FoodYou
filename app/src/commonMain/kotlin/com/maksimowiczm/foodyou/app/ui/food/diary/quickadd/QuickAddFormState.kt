package com.maksimowiczm.foodyou.app.ui.food.diary.quickadd

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.maksimowiczm.foodyou.app.ui.common.form.FormField
import com.maksimowiczm.foodyou.app.ui.common.form.nullableDoubleParser
import com.maksimowiczm.foodyou.app.ui.common.form.rememberFormField
import com.maksimowiczm.foodyou.app.ui.common.form.stringParser
import com.maksimowiczm.foodyou.common.compose.utility.formatClipZeros
import foodyou.app.generated.resources.*
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import org.jetbrains.compose.resources.stringResource

internal enum class QuickAddFormFieldError {
    Required,
    InvalidNumber,
    NegativeNumber;

    @Composable
    fun stringResource(): String =
        when (this) {
            Required -> stringResource(Res.string.warning_enter_calories_or_macros)
            InvalidNumber -> stringResource(Res.string.error_invalid_number)
            NegativeNumber -> stringResource(Res.string.error_value_cannot_be_negative)
        }
}

@Composable
internal fun rememberQuickAddFormState(
    name: String = "",
    proteins: Double? = null,
    carbohydrates: Double? = null,
    fats: Double? = null,
    energy: Double? = null,
): QuickAddFormState {
    val name =
        rememberFormField<String, QuickAddFormFieldError>(
            initialValue = name,
            parser = stringParser(),
            validator = { null },
            textFieldState = rememberTextFieldState(name),
        )

    val proteinsForm =
        rememberFormField(
            initialValue = proteins,
            parser = nullableDoubleParser(onNotANumber = { QuickAddFormFieldError.InvalidNumber }),
            validator = {
                if (it != null && it < 0) QuickAddFormFieldError.NegativeNumber else null
            },
            textFieldState = rememberTextFieldState(proteins?.formatClipZeros() ?: ""),
        )

    val carbohydratesForm =
        rememberFormField(
            initialValue = carbohydrates,
            parser = nullableDoubleParser(onNotANumber = { QuickAddFormFieldError.InvalidNumber }),
            validator = {
                if (it != null && it < 0) QuickAddFormFieldError.NegativeNumber else null
            },
            textFieldState = rememberTextFieldState(carbohydrates?.formatClipZeros() ?: ""),
        )

    val fatsForm =
        rememberFormField(
            initialValue = fats,
            parser = nullableDoubleParser(onNotANumber = { QuickAddFormFieldError.InvalidNumber }),
            validator = {
                if (it != null && it < 0) QuickAddFormFieldError.NegativeNumber else null
            },
            textFieldState = rememberTextFieldState(fats?.formatClipZeros() ?: ""),
        )

    val energyForm =
        rememberFormField(
            initialValue = energy,
            parser = nullableDoubleParser(onNotANumber = { QuickAddFormFieldError.InvalidNumber }),
            validator = {
                if (it != null && it < 0) QuickAddFormFieldError.NegativeNumber else null
            },
            textFieldState = rememberTextFieldState(energy?.formatClipZeros() ?: ""),
        )

    val autoCalculateEnergyState =
        rememberSaveable(proteins, carbohydrates, fats, energy) {
            val calculatedEnergy = calculateQuickAddEnergy(proteins, carbohydrates, fats)
            val initialState = energy == null || calculatedEnergy == energy

            mutableStateOf(initialState)
        }

    var isProgrammaticallyUpdatingEnergy by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(autoCalculateEnergyState, proteinsForm, carbohydratesForm, fatsForm) {
        snapshotFlow {
                if (!autoCalculateEnergyState.value) {
                    return@snapshotFlow null
                }

                calculateQuickAddEnergy(
                        proteins = proteinsForm.value,
                        carbohydrates = carbohydratesForm.value,
                        fats = fatsForm.value,
                    )
                    ?.formatClipZeros()
            }
            .filterNotNull()
            .collectLatest {
                isProgrammaticallyUpdatingEnergy = true
                energyForm.textFieldState.setTextAndPlaceCursorAtEnd(it)
                isProgrammaticallyUpdatingEnergy = false
            }
    }

    LaunchedEffect(energyForm) {
        snapshotFlow { energyForm.textFieldState.text }
            .drop(1)
            .collectLatest {
                if (!isProgrammaticallyUpdatingEnergy && autoCalculateEnergyState.value) {
                    autoCalculateEnergyState.value = false
                }
            }
    }

    return remember(
        name,
        proteinsForm,
        carbohydratesForm,
        fatsForm,
        energyForm,
        autoCalculateEnergyState,
    ) {
        QuickAddFormState(
            name = name,
            proteins = proteinsForm,
            carbohydrates = carbohydratesForm,
            fats = fatsForm,
            energy = energyForm,
            autoCalculateEnergyState = autoCalculateEnergyState,
        )
    }
}

@Stable
internal class QuickAddFormState(
    val name: FormField<String, QuickAddFormFieldError>,
    val proteins: FormField<Double?, QuickAddFormFieldError>,
    val carbohydrates: FormField<Double?, QuickAddFormFieldError>,
    val fats: FormField<Double?, QuickAddFormFieldError>,
    val energy: FormField<Double?, QuickAddFormFieldError>,
    autoCalculateEnergyState: MutableState<Boolean>,
) {
    var autoCalculateEnergy by autoCalculateEnergyState

    val hasAnyNutrients by derivedStateOf {
        energy.value != null ||
            proteins.value != null ||
            carbohydrates.value != null ||
            fats.value != null
    }

    val isValid by derivedStateOf {
        proteins.error == null &&
            carbohydrates.error == null &&
            fats.error == null &&
            energy.error == null &&
            hasAnyNutrients
    }
}
