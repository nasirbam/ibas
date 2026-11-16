package com.example.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.domain.ArrearInput
import com.example.domain.ArrearResult
import com.example.domain.GpfAnnualResult
import com.example.domain.GpfInput
import com.example.domain.PensionInput
import com.example.domain.PensionResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorsHubScreen(
    isBengali: Boolean,
    initialTab: Int = 0,
    gpfInput: GpfInput,
    gpfResult: GpfAnnualResult,
    onGpfInputChange: (GpfInput) -> Unit,
    pensionInput: PensionInput,
    pensionResult: PensionResult,
    onPensionInputChange: (PensionInput) -> Unit,
    arrearInput: ArrearInput,
    arrearResult: ArrearResult,
    onArrearInputChange: (ArrearInput) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by rememberSaveable(initialTab) { mutableIntStateOf(initialTab) }

    val tabs = if (isBengali) {
        listOf("জিপিএফ", "পেনশন", "বকেয়া", "স্কেল")
    } else {
        listOf("GPF", "Pension", "Arrear", "Scale")
    }

    Column(modifier = modifier.fillMaxSize()) {
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            tabs.forEachIndexed { index, label ->
                SegmentedButton(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = tabs.size),
                    label = {
                        Text(
                            text = label,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> GpfScreen(
                isBengali = isBengali,
                input = gpfInput,
                result = gpfResult,
                onInputChange = onGpfInputChange,
                modifier = Modifier.weight(1f)
            )
            1 -> PensionScreen(
                isBengali = isBengali,
                input = pensionInput,
                result = pensionResult,
                onInputChange = onPensionInputChange,
                modifier = Modifier.weight(1f)
            )
            2 -> ArrearScreen(
                isBengali = isBengali,
                input = arrearInput,
                result = arrearResult,
                onInputChange = onArrearInputChange,
                modifier = Modifier.weight(1f)
            )
            else -> PayScaleScreen(
                isBengali = isBengali,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
