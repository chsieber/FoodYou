package com.maksimowiczm.foodyou.app.ui.food.diary.quickadd

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.ui.common.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.app.ui.food.diary.component.ChipsDatePicker
import com.maksimowiczm.foodyou.app.ui.food.diary.component.ChipsDatePickerState
import com.maksimowiczm.foodyou.app.ui.food.diary.component.ChipsMealPicker
import com.maksimowiczm.foodyou.app.ui.food.diary.component.ChipsMealPickerState
import com.maksimowiczm.foodyou.common.compose.extension.add
import foodyou.app.generated.resources.*
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun QuickAddScreen(
    onBack: () -> Unit,
    onSave: () -> Unit,
    dateState: ChipsDatePickerState,
    mealState: ChipsMealPickerState,
    modifier: Modifier = Modifier,
    formState: QuickAddFormState = rememberQuickAddFormState(),
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val focusRequester = remember { FocusRequester() }
    val canSave = formState.isValid && mealState.selectedMeal != null

    LaunchedEffect(Unit) {
        delay(100)
        runCatching { focusRequester.requestFocus() }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.headline_quick_add)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                actions = {
                    FilledIconButton(
                        onClick = {
                            if (canSave) {
                                onSave()
                            }
                        },
                        enabled = canSave,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Save,
                            contentDescription = stringResource(Res.string.action_save),
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier.fillMaxSize()
                    .imePadding()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues.add(horizontal = 16.dp, vertical = 8.dp),
        ) {
            item {
                Text(
                    text = stringResource(Res.string.description_quick_add),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(Modifier.height(16.dp))
                ChipsDatePicker(state = dateState)
                Spacer(Modifier.height(16.dp))
                ChipsMealPicker(state = mealState)
                Spacer(Modifier.height(16.dp))
                QuickAddForm(
                    state = formState,
                    modifier = Modifier.focusRequester(focusRequester),
                )
            }
        }
    }
}
