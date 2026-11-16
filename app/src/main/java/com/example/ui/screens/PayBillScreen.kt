package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.domain.BillType
import com.example.domain.HouseRentTier
import com.example.domain.PayBillCalculator
import com.example.domain.PayBillInput
import com.example.domain.PayBillResult
import com.example.domain.PayScale2015
import com.example.domain.PayScalePhase
import com.example.ui.components.ExportActionRow
import com.example.ui.components.KeyValueRow
import com.example.ui.components.NumberInputField
import com.example.ui.components.SectionCard
import com.example.ui.components.SummaryGradientCard
import com.example.util.DocumentExporter
import com.example.util.FormatUtils

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PayBillScreen(
    isBengali: Boolean,
    selectedMonth: String,
    input: PayBillInput,
    result: PayBillResult,
    onMonthChange: (String) -> Unit,
    onInputChange: (PayBillInput) -> Unit,
    buildSummaryText: () -> String,
    onSaveBill: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var gradeMenuExpanded by remember { mutableStateOf(false) }
    var stepMenuExpanded by remember { mutableStateOf(false) }
    var monthMenuExpanded by remember { mutableStateOf(false) }

    val months = listOf(
        "July 2026", "August 2026", "September 2026", "October 2026",
        "November 2026", "December 2026", "January 2027", "February 2027",
        "March 2027", "April 2027", "May 2027", "June 2027"
    )
    val currentGrade = PayScale2015.getGrade(input.grade)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SummaryGradientCard(
                title = if (isBengali) "নিট প্রাপ্য বেতন (${selectedMonth} • ${input.phase.shortBn})" else "Net Payable (${selectedMonth} • ${input.phase.shortEn})",
                primaryValue = FormatUtils.taka(result.netPayable),
                subtitle = PayBillCalculator.amountInWordsEn(result.netPayable.toLong()),
                secondaryLeftLabel = if (isBengali) "মোট বেতন ও ভাতা (Gross)" else "Gross Pay",
                secondaryLeftValue = FormatUtils.taka(result.grossPay),
                secondaryRightLabel = if (isBengali) "মোট কর্তন (Deduction)" else "Total Deduction",
                secondaryRightValue = FormatUtils.taka(result.totalDeduction)
            )
        }

        item {
            SectionCard(
                title = if (isBengali) "পে-স্কেল ২০২৬ ধাপ ও বিল সেটআপ" else "Pay Scale 2026 Phase & Bill Setup",
                icon = Icons.Outlined.Tune
            ) {
                Text(
                    text = if (isBengali) "পে-স্কেল ২০২৬ বাস্তবায়ন পর্যায় (স্বয়ংক্রিয় হিসাব)" else "Pay Scale 2026 Implementation Phase",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PayScalePhase.entries.forEach { phase ->
                        FilterChip(
                            selected = input.phase == phase,
                            onClick = {
                                val autoBasic = PayScale2015.getPayableBasicForStep(input.grade, input.stepIndex, phase)
                                onInputChange(
                                    input.copy(
                                        phase = phase,
                                        basicPay = autoBasic,
                                        includeSpecialBenefit = phase == PayScalePhase.OLD_2015
                                    )
                                )
                            },
                            label = { Text(if (isBengali) phase.shortBn else phase.shortEn) },
                            modifier = Modifier.testTag("pay_phase_${phase.name}")
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))
                Text(
                    text = if (isBengali) "বিলের ধরন" else "Bill Type",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    BillType.entries.forEach { bType ->
                        FilterChip(
                            selected = input.billType == bType,
                            onClick = { onInputChange(input.copy(billType = bType)) },
                            label = { Text(if (isBengali) bType.titleBn else bType.titleEn) },
                            modifier = Modifier.testTag("bill_type_${bType.name}")
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))
                ExposedDropdownMenuBox(
                    expanded = monthMenuExpanded,
                    onExpandedChange = { monthMenuExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedMonth,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(if (isBengali) "বেতনের মাস" else "Salary Month") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = monthMenuExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = monthMenuExpanded,
                        onDismissRequest = { monthMenuExpanded = false }
                    ) {
                        months.forEach { m ->
                            DropdownMenuItem(
                                text = { Text(m) },
                                onClick = {
                                    onMonthChange(m)
                                    monthMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ExposedDropdownMenuBox(
                        expanded = gradeMenuExpanded,
                        onExpandedChange = { gradeMenuExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = "${if (isBengali) "গ্রেড" else "Grade"} ${input.grade}",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(if (isBengali) "গ্রেড (১-২০)" else "Grade (1-20)") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = gradeMenuExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        )
                        ExposedDropdownMenu(
                            expanded = gradeMenuExpanded,
                            onDismissRequest = { gradeMenuExpanded = false }
                        ) {
                            PayScale2015.grades.forEach { g ->
                                DropdownMenuItem(
                                    text = { Text("Grade ${g.grade} (২০২৬: ${FormatUtils.numberFormatted(g.minBasic2026)})") },
                                    onClick = {
                                        val autoBasic = PayScale2015.getPayableBasicForStep(g.grade, 0, input.phase)
                                        onInputChange(input.copy(grade = g.grade, stepIndex = 0, basicPay = autoBasic))
                                        gradeMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    ExposedDropdownMenuBox(
                        expanded = stepMenuExpanded,
                        onExpandedChange = { stepMenuExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = "${if (isBengali) "ধাপ" else "Step"} ${input.stepIndex + 1} (${FormatUtils.numberFormatted(input.basicPay)})",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(if (isBengali) "বেতন ধাপ" else "Pay Step") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stepMenuExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        )
                        ExposedDropdownMenu(
                            expanded = stepMenuExpanded,
                            onDismissRequest = { stepMenuExpanded = false }
                        ) {
                            currentGrade.steps2026.forEachIndexed { idx, step2026 ->
                                val payable = PayScale2015.getPayableBasicForStep(input.grade, idx, input.phase)
                                DropdownMenuItem(
                                    text = { Text("ধাপ ${idx + 1}: ৳ ${FormatUtils.numberFormatted(payable)} (পূর্ণ: ${FormatUtils.numberFormatted(step2026)})") },
                                    onClick = {
                                        onInputChange(input.copy(stepIndex = idx, basicPay = payable))
                                        stepMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))
                NumberInputField(
                    value = input.basicPay.toString(),
                    onValueChange = { txt ->
                        onInputChange(input.copy(basicPay = txt.toIntOrNull() ?: 0))
                    },
                    label = if (isBengali) "প্রাপ্য মূল বেতন (স্বয়ংক্রিয়/সম্পাদনযোগ্য)" else "Payable Basic Pay",
                    testTag = "paybill_basic_input"
                )

                if (input.billType == BillType.MONTHLY_SALARY) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = if (isBengali) "কর্মস্থল (বাড়ি ভাড়া এলাকা)" else "Posting Area (House Rent)",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        HouseRentTier.entries.forEach { tier ->
                            FilterChip(
                                selected = input.tier == tier,
                                onClick = { onInputChange(input.copy(tier = tier)) },
                                label = { Text(if (isBengali) tier.shortBn else tier.shortEn) }
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        NumberInputField(
                            value = input.gpfPercent.toString(),
                            onValueChange = { txt ->
                                val pct = (txt.toIntOrNull() ?: 0).coerceIn(0, 30)
                                onInputChange(input.copy(gpfPercent = pct))
                            },
                            label = if (isBengali) "জিপিএফ %" else "GPF %",
                            prefix = null,
                            suffix = "%",
                            modifier = Modifier.weight(1f)
                        )
                        NumberInputField(
                            value = input.gpfLoanInstallment.toString(),
                            onValueChange = { txt ->
                                onInputChange(input.copy(gpfLoanInstallment = txt.toIntOrNull() ?: 0))
                            },
                            label = if (isBengali) "জিপিএফ ঋণ কিস্তি" else "GPF Loan EMI",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        NumberInputField(
                            value = input.incomeTax.toString(),
                            onValueChange = { txt ->
                                onInputChange(input.copy(incomeTax = txt.toIntOrNull() ?: 0))
                            },
                            label = if (isBengali) "আয়কর (TDS)" else "Income Tax",
                            modifier = Modifier.weight(1f)
                        )
                        NumberInputField(
                            value = input.chargeAllowance.toString(),
                            onValueChange = { txt ->
                                onInputChange(input.copy(chargeAllowance = txt.toIntOrNull() ?: 0))
                            },
                            label = if (isBengali) "দায়িত্ব/অন্যান্য ভাতা" else "Charge/Other",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBengali) "শিক্ষা সহায়ক ভাতা (সন্তান)" else "Education Allowance",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(0, 1, 2).forEach { count ->
                                FilterChip(
                                    selected = input.childrenCount == count,
                                    onClick = { onInputChange(input.copy(childrenCount = count)) },
                                    label = { Text("$count") }
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBengali) "বয়স ৫০+ (চিকিৎসা ৳৪,০০০)" else "Age 50+ (Medical ৳4,000)",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Switch(
                            checked = input.isAge50Plus,
                            onCheckedChange = { onInputChange(input.copy(isAge50Plus = it)) }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBengali) "সরকারি বাসা বরাদ্দ (কোয়ার্টার)" else "Govt. Quarter Occupied",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Switch(
                            checked = input.livesInGovtQuarter,
                            onCheckedChange = { onInputChange(input.copy(livesInGovtQuarter = it)) }
                        )
                    }
                }
            }
        }

        item {
            SectionCard(
                title = if (isBengali) "বেতন ও ভাতাদি (Earnings)" else "Salary & Allowances",
                icon = Icons.Outlined.AccountBalanceWallet
            ) {
                KeyValueRow(if (isBengali) "মূল বেতন (Basic Pay)" else "Basic Pay", FormatUtils.taka(result.basicPay))
                if (result.houseRentAllowance > 0) {
                    KeyValueRow(if (isBengali) "বাড়ি ভাড়া ভাতা" else "House Rent Allowance", FormatUtils.taka(result.houseRentAllowance))
                }
                if (result.medicalAllowance > 0) {
                    KeyValueRow(if (isBengali) "চিকিৎসা ভাতা (২০২৬)" else "Medical Allowance (2026)", FormatUtils.taka(result.medicalAllowance))
                }
                if (result.educationAllowance > 0) {
                    KeyValueRow(if (isBengali) "শিক্ষা সহায়ক ভাতা" else "Education Allowance", FormatUtils.taka(result.educationAllowance))
                }
                if (result.tiffinAllowance > 0) {
                    KeyValueRow(if (isBengali) "টিফিন ভাতা" else "Tiffin Allowance", FormatUtils.taka(result.tiffinAllowance))
                }
                if (result.washingAllowance > 0) {
                    KeyValueRow(if (isBengali) "ধোলাই ভাতা" else "Washing Allowance", FormatUtils.taka(result.washingAllowance))
                }
                if (result.specialBenefit > 0) {
                    KeyValueRow(if (isBengali) "বিশেষ সুবিধা" else "Special Benefit", FormatUtils.taka(result.specialBenefit))
                }
                if (result.chargeAllowance > 0) {
                    KeyValueRow(if (isBengali) "অতিরিক্ত দায়িত্ব/অন্যান্য" else "Charge/Other Allowance", FormatUtils.taka(result.chargeAllowance))
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                KeyValueRow(
                    label = if (isBengali) "মোট প্রাপ্য (Gross Pay)" else "Gross Pay",
                    value = FormatUtils.taka(result.grossPay),
                    isHighlighted = true
                )
            }
        }

        if (result.totalDeduction > 0) {
            item {
                SectionCard(
                    title = if (isBengali) "কর্তনসমূহ (Deductions)" else "Deductions",
                    icon = Icons.Outlined.RemoveCircleOutline
                ) {
                    if (result.gpfSubscription > 0) {
                        KeyValueRow(
                            if (isBengali) "জিপিএফ চাঁদা (${input.gpfPercent}%)" else "GPF Subscription (${input.gpfPercent}%)",
                            FormatUtils.taka(result.gpfSubscription),
                            isDeduction = true
                        )
                    }
                    if (result.gpfLoanInstallment > 0) {
                        KeyValueRow(if (isBengali) "জিপিএফ অগ্রিম কিস্তি" else "GPF Loan Recovery", FormatUtils.taka(result.gpfLoanInstallment), isDeduction = true)
                    }
                    if (result.benevolentFund > 0) {
                        KeyValueRow(if (isBengali) "কর্মচারী কল্যাণ তহবিল" else "Benevolent Fund", FormatUtils.taka(result.benevolentFund), isDeduction = true)
                    }
                    if (result.groupInsurance > 0) {
                        KeyValueRow(if (isBengali) "যৌথ বীমা" else "Group Insurance", FormatUtils.taka(result.groupInsurance), isDeduction = true)
                    }
                    if (result.govtQuarterDeduction > 0) {
                        KeyValueRow(if (isBengali) "সরকারি বাসা ভাড়া কর্তন (৭.৫%)" else "Govt Quarter Rent (7.5%)", FormatUtils.taka(result.govtQuarterDeduction), isDeduction = true)
                    }
                    if (result.incomeTax > 0) {
                        KeyValueRow(if (isBengali) "আয়কর (TDS)" else "Income Tax (TDS)", FormatUtils.taka(result.incomeTax), isDeduction = true)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                    KeyValueRow(
                        label = if (isBengali) "মোট কর্তন" else "Total Deduction",
                        value = FormatUtils.taka(result.totalDeduction),
                        isHighlighted = true,
                        isDeduction = true
                    )
                }
            }
        }

        item {
            SectionCard(
                title = if (isBengali) "বেতন বিল কপি প্রিন্ট, SutonnyMJ কপি ও PDF" else "Print, SutonnyMJ Copy & PDF",
                icon = Icons.Outlined.ReceiptLong
            ) {
                ExportActionRow(
                    isBengali = isBengali,
                    onCopy = {
                        DocumentExporter.copyToClipboard(context, "Pay Bill Copy", buildSummaryText())
                        Toast.makeText(
                            context,
                            if (isBengali) "বেতন বিল কপি ক্লিপবোর্ডে কপি হয়েছে" else "Pay Bill copied",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onCopySutonnyMj = {
                        DocumentExporter.copySutonnyMjToClipboard(context, "Pay Bill Copy", buildSummaryText())
                        Toast.makeText(
                            context,
                            "SutonnyMJ (Bijoy) ফরম্যাটে কপি হয়েছে! MS Word-এ পেস্ট করে SutonnyMJ ফন্ট দিন।",
                            Toast.LENGTH_LONG
                        ).show()
                    },
                    onSharePdf = {
                        DocumentExporter.sharePdf(
                            context = context,
                            fileName = "PayBill_${selectedMonth}",
                            headerTitle = "iBAS Sahayak - Pay Bill Copy (Pay Scale 2026)",
                            subHeader = "Month: $selectedMonth | Grade ${input.grade} | ${input.phase.shortEn}",
                            bodyText = buildSummaryText()
                        )
                    },
                    onPrint = {
                        DocumentExporter.printPdf(
                            context = context,
                            jobName = "PayBill_${selectedMonth}",
                            headerTitle = "iBAS Sahayak - Pay Bill Copy (Pay Scale 2026)",
                            subHeader = "Month: $selectedMonth | Grade ${input.grade}",
                            bodyText = buildSummaryText()
                        )
                    },
                    onSave = {
                        onSaveBill()
                        Toast.makeText(
                            context,
                            if (isBengali) "হিস্টোরিতে সংরক্ষণ করা হয়েছে" else "Saved to history",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        }
    }
}
