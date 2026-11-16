package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.TableRows
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.ArrearInput
import com.example.domain.ArrearResult
import com.example.domain.HouseRentTier
import com.example.ui.components.ExportActionRow
import com.example.ui.components.KeyValueRow
import com.example.ui.components.NumberInputField
import com.example.ui.components.SectionCard
import com.example.ui.components.SummaryGradientCard
import com.example.ui.theme.MonoFontFamily
import com.example.util.DocumentExporter
import com.example.util.FormatUtils

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ArrearScreen(
    isBengali: Boolean,
    input: ArrearInput,
    result: ArrearResult,
    onInputChange: (ArrearInput) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val arrearReportText = remember(input, result) {
        buildString {
            appendLine("=== ARREAR SALARY STATEMENT (DUE vs DRAWN) ===")
            appendLine("Period: ${input.startDateIso} to ${input.endDateIso}")
            appendLine("Drawn Basic: ${FormatUtils.taka(input.drawnBasic)} | Due Basic: ${FormatUtils.taka(input.dueBasic)}")
            appendLine("Total Drawn: ${FormatUtils.taka(result.totalDrawn)}")
            appendLine("Total Due: ${FormatUtils.taka(result.totalDue)}")
            if (result.bonusArrear > 0) appendLine("Festival Bonus Arrear: ${FormatUtils.taka(result.bonusArrear)}")
            appendLine("Total Gross Arrear: ${FormatUtils.taka(result.totalGrossArrear)}")
            appendLine("GPF Deduction (${input.gpfPercent}%): ${FormatUtils.taka(result.totalGpfDeduction)}")
            appendLine("NET ARREAR PAYABLE: ${FormatUtils.taka(result.netArrearPayable)}")
            appendLine("----------------------------------------------")
            result.rows.forEach { row ->
                appendLine("${row.monthLabel.padEnd(10)} (${row.daysCount}/${row.totalDaysInMonth}d) | Diff: ${FormatUtils.taka(row.grossDiff)} | Net: ${FormatUtils.taka(row.netDiff)}")
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
                title = if (isBengali) "নিট প্রাপ্য বকেয়া বেতন" else "Net Arrear Payable",
                primaryValue = FormatUtils.taka(result.netArrearPayable),
                subtitle = if (isBengali) {
                    "সময়কাল: ${input.startDateIso} হতে ${input.endDateIso} (${result.rows.size} মাস)"
                } else {
                    "Period: ${input.startDateIso} to ${input.endDateIso} (${result.rows.size} months)"
                },
                secondaryLeftLabel = if (isBengali) "মোট বকেয়া (Gross)" else "Gross Arrear",
                secondaryLeftValue = FormatUtils.taka(result.totalGrossArrear),
                secondaryRightLabel = if (isBengali) "জিপিএফ কর্তন" else "GPF Deduction",
                secondaryRightValue = FormatUtils.taka(result.totalGpfDeduction)
            )
        }

        item {
            SectionCard(
                title = if (isBengali) "বকেয়া সময়কাল ও মূল বেতন" else "Arrear Period & Basic Pay Comparison",
                icon = Icons.Outlined.DateRange
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = input.startDateIso,
                        onValueChange = { onInputChange(input.copy(startDateIso = it)) },
                        label = { Text(if (isBengali) "শুরুর তারিখ" else "Start Date") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = input.endDateIso,
                        onValueChange = { onInputChange(input.copy(endDateIso = it)) },
                        label = { Text(if (isBengali) "শেষ তারিখ" else "End Date") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    NumberInputField(
                        value = input.drawnBasic.toString(),
                        onValueChange = { txt ->
                            onInputChange(input.copy(drawnBasic = txt.toIntOrNull() ?: 0))
                        },
                        label = if (isBengali) "উত্তোলিত মূল বেতন (Drawn)" else "Drawn Basic",
                        modifier = Modifier.weight(1f),
                        testTag = "arrear_drawn_input"
                    )
                    NumberInputField(
                        value = input.dueBasic.toString(),
                        onValueChange = { txt ->
                            onInputChange(input.copy(dueBasic = txt.toIntOrNull() ?: 0))
                        },
                        label = if (isBengali) "প্রাপ্য মূল বেতন (Due)" else "Due Basic",
                        modifier = Modifier.weight(1f),
                        testTag = "arrear_due_input"
                    )
                }

                Spacer(Modifier.height(12.dp))
                Text(
                    text = if (isBengali) "বাড়ি ভাড়া এলাকা" else "House Rent Area",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(6.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    HouseRentTier.entries.forEach { tier ->
                        FilterChip(
                            selected = input.tier == tier,
                            onClick = { onInputChange(input.copy(tier = tier)) },
                            label = { Text(if (isBengali) tier.shortBn else tier.shortEn) }
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBengali) "বাড়ি ভাড়ার পার্থক্য অন্তর্ভুক্ত করুন" else "Include House Rent Difference",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Switch(
                        checked = input.includeHouseRentDiff,
                        onCheckedChange = { onInputChange(input.copy(includeHouseRentDiff = it)) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBengali) "বকেয়া থেকে জিপিএফ কর্তন (${input.gpfPercent}%)" else "Deduct GPF (${input.gpfPercent}%)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Switch(
                        checked = input.deductGpfFromArrear,
                        onCheckedChange = { onInputChange(input.copy(deductGpfFromArrear = it)) }
                    )
                }

                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBengali) "বকেয়া উৎসব ভাতা (ঈদ বোনাস)" else "Festival Bonus Arrears",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(0, 1, 2).forEach { count ->
                            FilterChip(
                                selected = input.bonusMonthsCount == count,
                                onClick = { onInputChange(input.copy(bonusMonthsCount = count)) },
                                label = { Text("$count") }
                            )
                        }
                    }
                }
            }
        }

        item {
            SectionCard(
                title = if (isBengali) "সারাংশ (Due vs Drawn)" else "Summary (Due vs Drawn)",
                icon = Icons.Outlined.Calculate
            ) {
                KeyValueRow(if (isBengali) "মোট প্রাপ্য ছিল (Due)" else "Total Due", FormatUtils.taka(result.totalDue))
                KeyValueRow(if (isBengali) "মোট উত্তোলিত (Drawn)" else "Total Drawn", FormatUtils.taka(result.totalDrawn))
                if (result.bonusArrear > 0) {
                    KeyValueRow(if (isBengali) "উৎসব ভাতা বকেয়া" else "Bonus Arrear", FormatUtils.taka(result.bonusArrear))
                }
                KeyValueRow(if (isBengali) "মোট বকেয়া (Gross)" else "Gross Arrear", FormatUtils.taka(result.totalGrossArrear), isHighlighted = true)
                KeyValueRow(if (isBengali) "জিপিএফ কর্তন" else "GPF Deduction", FormatUtils.taka(result.totalGpfDeduction), isDeduction = true)
                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                KeyValueRow(if (isBengali) "নিট প্রাপ্য বকেয়া" else "Net Arrear Payable", FormatUtils.taka(result.netArrearPayable), isHighlighted = true)
            }
        }

        if (result.rows.isNotEmpty()) {
            item {
                SectionCard(
                    title = if (isBengali) "মাসভিত্তিক বকেয়া বিবরণী" else "Month-by-Month Breakdown",
                    icon = Icons.Outlined.TableRows
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)) {
                        Text(if (isBengali) "মাস" else "Month", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f))
                        Text(if (isBengali) "উত্তোলিত" else "Drawn", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Text(if (isBengali) "প্রাপ্য" else "Due", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Text(if (isBengali) "নিট বকেয়া" else "Net Diff", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    }
                    HorizontalDivider()
                    result.rows.forEach { row ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                            Text(
                                text = "${row.monthLabel}\n(${row.daysCount}/${row.totalDaysInMonth}d)",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1.2f)
                            )
                            Text(FormatUtils.taka(row.drawnTotal), fontFamily = MonoFontFamily, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                            Text(FormatUtils.taka(row.dueTotal), fontFamily = MonoFontFamily, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                            Text(
                                FormatUtils.taka(row.netDiff),
                                fontFamily = MonoFontFamily,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    }
                }
            }
        }

        item {
            SectionCard(
                title = if (isBengali) "বকেয়া বিল কপি শেয়ার / PDF" else "Export Arrear Statement",
                icon = Icons.Outlined.ReceiptLong
            ) {
                ExportActionRow(
                    isBengali = isBengali,
                    onCopy = {
                        DocumentExporter.copyToClipboard(context, "Arrear Statement", arrearReportText)
                        Toast.makeText(
                            context,
                            if (isBengali) "বকেয়া বিবরণী কপি হয়েছে" else "Arrear statement copied",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onSharePdf = {
                        DocumentExporter.sharePdf(
                            context = context,
                            fileName = "Arrear_Bill_${input.startDateIso}",
                            headerTitle = "iBAS Sahayak - Arrear Salary Bill Statement",
                            subHeader = "Period: ${input.startDateIso} to ${input.endDateIso}",
                            bodyText = arrearReportText
                        )
                    },
                    onPrint = {
                        DocumentExporter.printPdf(
                            context = context,
                            jobName = "Arrear_Bill",
                            headerTitle = "iBAS Sahayak - Arrear Salary Bill Statement",
                            subHeader = "Period: ${input.startDateIso} to ${input.endDateIso}",
                            bodyText = arrearReportText
                        )
                    }
                )
            }
        }
    }
}
