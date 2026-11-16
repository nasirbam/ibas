package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.Summarize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.domain.PensionInput
import com.example.domain.PensionResult
import com.example.ui.components.BadgeChip
import com.example.ui.components.ExportActionRow
import com.example.ui.components.KeyValueRow
import com.example.ui.components.NumberInputField
import com.example.ui.components.SectionCard
import com.example.ui.components.SummaryGradientCard
import com.example.util.DocumentExporter
import com.example.util.FormatUtils

@Composable
fun PensionScreen(
    isBengali: Boolean,
    input: PensionInput,
    result: PensionResult,
    onInputChange: (PensionInput) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val pensionSummary = remember(input, result) {
        buildString {
            appendLine("=== PENSION & GRATUITY STATEMENT (BD GOVT) ===")
            appendLine("Last Basic Pay: ${FormatUtils.taka(input.lastBasicPay)}")
            appendLine("Qualifying Service: ${input.qualifyingYears} Years (${result.pensionPercent}%)")
            appendLine("Gross Monthly Pension: ${FormatUtils.taka(result.grossMonthlyPension)}")
            appendLine("Surrendered 50% Pension: ${FormatUtils.taka(result.surrenderedHalfPension)}")
            appendLine("Gratuity Rate: ${result.gratuityRatePerTaka} Tk / 1 Tk")
            appendLine("Total Gratuity Lump Sum: ${FormatUtils.taka(result.totalGratuityLumpSum)}")
            appendLine("----------------------------------------------")
            appendLine("Net Monthly Pension Base: ${FormatUtils.taka(result.netMonthlyPensionBase)}")
            appendLine("Medical Allowance: ${FormatUtils.taka(result.medicalAllowance)}")
            appendLine("Total Monthly Payable: ${FormatUtils.taka(result.totalMonthlyPensionPayable)}")
            appendLine("Festival Bonus (Each): ${FormatUtils.taka(result.festivalBonusEach)}")
            appendLine("Boishakhi Allowance (20%): ${FormatUtils.taka(result.boishakhiAllowance)}")
            appendLine("PRL Start Date: ${result.prlStartDateFormatted}")
            appendLine("Final Retirement Date: ${result.finalRetirementDateFormatted}")
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SummaryGradientCard(
                title = if (isBengali) "এককালীন আনুতোষিক (Gratuity)" else "Total Lump-Sum Gratuity",
                primaryValue = FormatUtils.taka(result.totalGratuityLumpSum),
                subtitle = if (isBengali) {
                    "পেনশন হার: ${result.pensionPercent}%  •  প্রতি টাকায়: ৳ ${result.gratuityRatePerTaka}"
                } else {
                    "Pension Rate: ${result.pensionPercent}%  •  Factor: ৳ ${result.gratuityRatePerTaka} / Tk"
                },
                secondaryLeftLabel = if (isBengali) "মাসিক নিট পেনশন (ভাতাসহ)" else "Monthly Payable Pension",
                secondaryLeftValue = FormatUtils.taka(result.totalMonthlyPensionPayable),
                secondaryRightLabel = if (isBengali) "মোট পেনশন (১০০%)" else "Gross Pension (100%)",
                secondaryRightValue = FormatUtils.taka(result.grossMonthlyPension)
            )
        }

        item {
            SectionCard(
                title = if (isBengali) "চাকরিকাল ও শেষ মূল বেতন" else "Service Length & Last Basic Pay",
                icon = Icons.Outlined.AccountBalance,
                trailing = {
                    BadgeChip(text = "${result.pensionPercent}%")
                }
            ) {
                NumberInputField(
                    value = input.lastBasicPay.toString(),
                    onValueChange = { txt ->
                        onInputChange(input.copy(lastBasicPay = txt.toIntOrNull() ?: 0))
                    },
                    label = if (isBengali) "সর্বশেষ মূল বেতন (Last Basic)" else "Last Basic Pay",
                    testTag = "pension_basic_input"
                )

                Spacer(Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isBengali) "মোট চাকরিকাল: ${input.qualifyingYears} বছর" else "Qualifying Service: ${input.qualifyingYears} years",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = if (isBengali) "হার: ৳ ${result.gratuityRatePerTaka}" else "Rate: ৳ ${result.gratuityRatePerTaka}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Slider(
                    value = input.qualifyingYears.toFloat(),
                    onValueChange = { v -> onInputChange(input.copy(qualifyingYears = v.toInt())) },
                    valueRange = 5f..25f,
                    steps = 19
                )

                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isBengali) "বয়স ৬৫+ (বর্ধিত চিকিৎসা ভাতা)" else "Age 65+ (Higher Medical)",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = if (isBengali) "৬৫ বছরের নিচে ৳ ১,৫০০, উপরে ৳ ২,৫০০" else "৳ 1,500 under 65, ৳ 2,500 at 65+",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = input.ageAtRetirement >= 65,
                        onCheckedChange = { over65 ->
                            onInputChange(input.copy(ageAtRetirement = if (over65) 65 else 59))
                        }
                    )
                }
            }
        }

        // PRL & Retirement Date Calculator
        item {
            SectionCard(
                title = if (isBengali) "পিআরএল (PRL) ও অবসরের তারিখ হিসাব" else "PRL & Final Retirement Dates",
                icon = Icons.Outlined.CalendarMonth
            ) {
                OutlinedTextField(
                    value = input.birthDateIso,
                    onValueChange = { onInputChange(input.copy(birthDateIso = it)) },
                    label = { Text(if (isBengali) "জন্ম তারিখ (YYYY-MM-DD)" else "Date of Birth (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBengali) "মুক্তিযোদ্ধা কোটা (অবসরের বয়স ৬০ বছর)" else "Freedom Fighter (Age 60 Retirement)",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = input.isFreedomFighter,
                        onCheckedChange = { onInputChange(input.copy(isFreedomFighter = it)) }
                    )
                }
                Spacer(Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(Modifier.height(6.dp))
                KeyValueRow(
                    label = if (isBengali) "পিআরএল (PRL) শুরুর তারিখ" else "PRL Start Date",
                    value = result.prlStartDateFormatted,
                    isHighlighted = true
                )
                KeyValueRow(
                    label = if (isBengali) "চূড়ান্ত অবসরের তারিখ" else "Final Retirement Date",
                    value = result.finalRetirementDateFormatted,
                    isHighlighted = true
                )
            }
        }

        // Detailed Breakdown
        item {
            SectionCard(
                title = if (isBengali) "বিস্তারিত পেনশন বিবরণী" else "Detailed Pension Breakdown",
                icon = Icons.Outlined.Summarize
            ) {
                KeyValueRow(
                    if (isBengali) "মোট পেনশন (${result.pensionPercent}%)" else "Gross Pension (${result.pensionPercent}%)",
                    FormatUtils.taka(result.grossMonthlyPension)
                )
                KeyValueRow(
                    if (isBengali) "বাধ্যতামূলক সমর্পণ (৫০%)" else "Mandatory Surrender (50%)",
                    FormatUtils.taka(result.surrenderedHalfPension)
                )
                KeyValueRow(
                    if (isBengali) "অবশিষ্ট নিট মাসিক পেনশন" else "Remaining Net Pension Base",
                    FormatUtils.taka(result.netMonthlyPensionBase)
                )
                KeyValueRow(
                    if (isBengali) "মাসিক চিকিৎসা ভাতা" else "Medical Allowance",
                    FormatUtils.taka(result.medicalAllowance)
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                KeyValueRow(
                    if (isBengali) "সর্বমোট মাসিক প্রাপ্য পেনশন" else "Total Monthly Pension Payable",
                    FormatUtils.taka(result.totalMonthlyPensionPayable),
                    isHighlighted = true
                )
                KeyValueRow(
                    if (isBengali) "এককালীন আনুতোষিক (${result.gratuityRatePerTaka} গুণ)" else "Gratuity (${result.gratuityRatePerTaka}x)",
                    FormatUtils.taka(result.totalGratuityLumpSum),
                    isHighlighted = true
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                KeyValueRow(
                    if (isBengali) "উৎসব ভাতা (প্রতি ঈদে ১০০%)" else "Festival Bonus (Each)",
                    FormatUtils.taka(result.festivalBonusEach)
                )
                KeyValueRow(
                    if (isBengali) "বাংলা নববর্ষ ভাতা (২০%)" else "Boishakhi Allowance (20%)",
                    FormatUtils.taka(result.boishakhiAllowance)
                )
            }
        }

        item {
            SectionCard(
                title = if (isBengali) "পেনশন শিট ডাউনলোড ও শেয়ার" else "Export Pension Statement",
                icon = Icons.Outlined.EventAvailable
            ) {
                ExportActionRow(
                    isBengali = isBengali,
                    onCopy = {
                        DocumentExporter.copyToClipboard(context, "Pension Summary", pensionSummary)
                        Toast.makeText(
                            context,
                            if (isBengali) "পেনশন হিসাব কপি হয়েছে" else "Pension statement copied",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onSharePdf = {
                        DocumentExporter.sharePdf(
                            context = context,
                            fileName = "Pension_Gratuity_Estimate",
                            headerTitle = "iBAS Sahayak - Pension & Gratuity Estimate",
                            subHeader = "Qualifying Service: ${input.qualifyingYears} Years | Last Basic: ${FormatUtils.taka(input.lastBasicPay)}",
                            bodyText = pensionSummary
                        )
                    },
                    onPrint = {
                        DocumentExporter.printPdf(
                            context = context,
                            jobName = "Pension_Gratuity_Estimate",
                            headerTitle = "iBAS Sahayak - Pension & Gratuity Estimate",
                            subHeader = "Qualifying Service: ${input.qualifyingYears} Years",
                            bodyText = pensionSummary
                        )
                    }
                )
            }
        }
    }
}
