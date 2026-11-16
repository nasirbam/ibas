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
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.TableRows
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.GpfAnnualResult
import com.example.domain.GpfCalculatorEngine
import com.example.domain.GpfInput
import com.example.domain.GpfInterestMode
import com.example.ui.components.ExportActionRow
import com.example.ui.components.KeyValueRow
import com.example.ui.components.NumberInputField
import com.example.ui.components.SectionCard
import com.example.ui.components.SummaryGradientCard
import com.example.ui.theme.MonoFontFamily
import com.example.util.DocumentExporter
import com.example.util.FormatUtils
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GpfScreen(
    isBengali: Boolean,
    input: GpfInput,
    result: GpfAnnualResult,
    onInputChange: (GpfInput) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var withdrawalMonthExpanded by remember { mutableStateOf(false) }
    var advanceAmountText by remember { mutableStateOf("100000") }
    var advanceInstallments by remember { mutableIntStateOf(24) }

    val advanceEmi = GpfCalculatorEngine.calculateAdvanceEmi(
        advanceAmount = advanceAmountText.toDoubleOrNull() ?: 0.0,
        installments = advanceInstallments
    )

    val gpfReportText = remember(input, result, isBengali) {
        buildString {
            appendLine("=== GPF ANNUAL STATEMENT (JULY - JUNE) ===")
            appendLine("Opening Balance (1 July): ${FormatUtils.taka(input.openingBalance)}")
            appendLine("Total Subscription (12m): ${FormatUtils.taka(result.totalSubscription)}")
            if (result.totalRecovery > 0) appendLine("Total Loan Recovery: ${FormatUtils.taka(result.totalRecovery)}")
            if (result.totalWithdrawal > 0) appendLine("Withdrawal: ${FormatUtils.taka(result.totalWithdrawal)}")
            appendLine("Effective Interest Rate: ${String.format(Locale.US, "%.2f", result.effectiveRatePercent)}%")
            appendLine("Annual Interest Earned: ${FormatUtils.taka(result.totalAnnualInterest)}")
            appendLine("Closing Balance (30 June): ${FormatUtils.taka(result.closingBalance)}")
            appendLine("Max Eligible Advance (80%): ${FormatUtils.taka(result.maxEligibleAdvance80Percent)}")
            appendLine("------------------------------------------")
            result.rows.forEach { r ->
                appendLine("${r.monthNameEn.padEnd(10)} | Bal: ${FormatUtils.taka(r.progressiveBalance)}")
            }
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SummaryGradientCard(
                title = if (isBengali) "৩০শে জুন সমাপনী জিপিএফ স্থিতি" else "Closing GPF Balance (June 30)",
                primaryValue = FormatUtils.taka(result.closingBalance),
                subtitle = if (isBengali) {
                    "কার্যকর মুনাফার হার: ${String.format(Locale.US, "%.2f", result.effectiveRatePercent)}%  •  সর্বোচ্চ ঋণ যোগ্যতা (৮০%): ${FormatUtils.taka(result.maxEligibleAdvance80Percent)}"
                } else {
                    "Effective Rate: ${String.format(Locale.US, "%.2f", result.effectiveRatePercent)}%  •  Max 80% Advance: ${FormatUtils.taka(result.maxEligibleAdvance80Percent)}"
                },
                secondaryLeftLabel = if (isBengali) "বার্ষিক মুনাফা (Interest)" else "Annual Interest",
                secondaryLeftValue = FormatUtils.taka(result.totalAnnualInterest),
                secondaryRightLabel = if (isBengali) "মোট জমা (১২ মাস)" else "12-Month Deposit",
                secondaryRightValue = FormatUtils.taka(result.totalSubscription + result.totalRecovery)
            )
        }

        item {
            SectionCard(
                title = if (isBengali) "জিপিএফ হিসাব ইনপুট (জুলাই - জুন)" else "GPF Fiscal Year Inputs (Jul - Jun)",
                icon = Icons.Outlined.Savings
            ) {
                NumberInputField(
                    value = input.openingBalance.toLong().toString(),
                    onValueChange = { txt ->
                        onInputChange(input.copy(openingBalance = txt.toDoubleOrNull() ?: 0.0))
                    },
                    label = if (isBengali) "প্রারম্ভিক জের (১ জুলাই)" else "Opening Balance (July 1)",
                    testTag = "gpf_opening_input"
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    NumberInputField(
                        value = input.monthlySubscription.toLong().toString(),
                        onValueChange = { txt ->
                            onInputChange(input.copy(monthlySubscription = txt.toDoubleOrNull() ?: 0.0))
                        },
                        label = if (isBengali) "মাসিক চাঁদা" else "Monthly Subscription",
                        modifier = Modifier.weight(1f),
                        testTag = "gpf_sub_input"
                    )
                    NumberInputField(
                        value = input.monthlyLoanRecovery.toLong().toString(),
                        onValueChange = { txt ->
                            onInputChange(input.copy(monthlyLoanRecovery = txt.toDoubleOrNull() ?: 0.0))
                        },
                        label = if (isBengali) "ঋণ কিস্তি ফেরত" else "Loan Recovery/mo",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    NumberInputField(
                        value = input.withdrawalAmount.toLong().toString(),
                        onValueChange = { txt ->
                            onInputChange(input.copy(withdrawalAmount = txt.toDoubleOrNull() ?: 0.0))
                        },
                        label = if (isBengali) "বছরের মাঝামাঝি উত্তোলন" else "Mid-Year Withdrawal",
                        modifier = Modifier.weight(1f)
                    )
                    ExposedDropdownMenuBox(
                        expanded = withdrawalMonthExpanded,
                        onExpandedChange = { withdrawalMonthExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        val selectedRow = result.rows.getOrNull(input.withdrawalMonthIndex)
                        OutlinedTextField(
                            value = selectedRow?.let { if (isBengali) it.monthNameBn else it.monthNameEn } ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(if (isBengali) "উত্তোলনের মাস" else "Withdrawal Month") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = withdrawalMonthExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        )
                        ExposedDropdownMenu(
                            expanded = withdrawalMonthExpanded,
                            onDismissRequest = { withdrawalMonthExpanded = false }
                        ) {
                            result.rows.forEach { row ->
                                DropdownMenuItem(
                                    text = { Text(if (isBengali) row.monthNameBn else row.monthNameEn) },
                                    onClick = {
                                        onInputChange(input.copy(withdrawalMonthIndex = row.monthIndex))
                                        withdrawalMonthExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                Text(
                    text = if (isBengali) "মুনাফার হার পদ্ধতি" else "Interest Calculation Rule",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    GpfInterestMode.entries.forEach { mode ->
                        FilterChip(
                            selected = input.interestMode == mode,
                            onClick = { onInputChange(input.copy(interestMode = mode)) },
                            label = { Text(if (isBengali) mode.titleBn else mode.titleEn) }
                        )
                    }
                }

                if (input.interestMode == GpfInterestMode.CUSTOM_FLAT) {
                    Spacer(Modifier.height(10.dp))
                    NumberInputField(
                        value = input.customRatePercent.toString(),
                        onValueChange = { txt ->
                            onInputChange(input.copy(customRatePercent = txt.toDoubleOrNull() ?: 13.0))
                        },
                        label = if (isBengali) "মুনাফার হার (%)" else "Interest Rate (%)",
                        prefix = null,
                        suffix = "%"
                    )
                }
            }
        }

        // GPF Advance EMI Calculator
        item {
            SectionCard(
                title = if (isBengali) "জিপিএফ অগ্রিম কিস্তি (EMI) হিসাব" else "GPF Advance & Installment Calculator",
                icon = Icons.Outlined.AccountBalance
            ) {
                NumberInputField(
                    value = advanceAmountText,
                    onValueChange = { advanceAmountText = it },
                    label = if (isBengali) "প্রস্তাবিত অগ্রিমের পরিমাণ" else "Requested Advance Amount"
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = if (isBengali) "পরিশোধের কিস্তি সংখ্যা" else "Repayment Installments",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(6.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(12, 24, 36, 48).forEach { inst ->
                        FilterChip(
                            selected = advanceInstallments == inst,
                            onClick = { advanceInstallments = inst },
                            label = { Text("$inst ${if (isBengali) "মাস" else "mo"}") }
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                KeyValueRow(
                    label = if (isBengali) "মাসিক কিস্তির পরিমাণ" else "Monthly Installment",
                    value = FormatUtils.taka(advanceEmi),
                    isHighlighted = true
                )
            }
        }

        // 12-Month Ledger Table
        item {
            SectionCard(
                title = if (isBengali) "মাসভিত্তিক জিপিএফ খতিয়ান (জুলাই–জুন)" else "Monthly GPF Ledger (Jul–Jun)",
                icon = Icons.Outlined.TableRows
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)) {
                    Text(
                        if (isBengali) "মাস" else "Month",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1.1f)
                    )
                    Text(
                        if (isBengali) "জমা" else "Deposit",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        if (isBengali) "ক্রমবর্ধমান স্থিতি" else "Balance",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1.2f)
                    )
                }
                HorizontalDivider()
                result.rows.forEach { row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isBengali) row.monthNameBn else row.monthNameEn,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1.1f)
                        )
                        Text(
                            text = FormatUtils.taka(row.subscription + row.recovery - row.withdrawal),
                            fontFamily = MonoFontFamily,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = FormatUtils.taka(row.progressiveBalance),
                            fontFamily = MonoFontFamily,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1.2f)
                        )
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                }
            }
        }

        item {
            SectionCard(
                title = if (isBengali) "হিসাব বিবরণী শেয়ার / PDF" else "Export GPF Statement",
                icon = Icons.Outlined.Calculate
            ) {
                ExportActionRow(
                    isBengali = isBengali,
                    onCopy = {
                        DocumentExporter.copyToClipboard(context, "GPF Statement", gpfReportText)
                        Toast.makeText(
                            context,
                            if (isBengali) "জিপিএফ হিসাব কপি হয়েছে" else "GPF Statement copied",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onSharePdf = {
                        DocumentExporter.sharePdf(
                            context = context,
                            fileName = "GPF_Statement",
                            headerTitle = "iBAS Sahayak - GPF Annual Statement",
                            subHeader = "Bangladesh Govt Fiscal Year (July - June)",
                            bodyText = gpfReportText
                        )
                    },
                    onPrint = {
                        DocumentExporter.printPdf(
                            context = context,
                            jobName = "GPF_Statement",
                            headerTitle = "iBAS Sahayak - GPF Annual Statement",
                            subHeader = "Bangladesh Govt Fiscal Year (July - June)",
                            bodyText = gpfReportText
                        )
                    }
                )
            }
        }
    }
}
