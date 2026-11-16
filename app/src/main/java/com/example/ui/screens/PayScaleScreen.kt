package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.PayScale2015
import com.example.domain.PayScalePhase
import com.example.ui.components.BadgeChip
import com.example.ui.theme.MonoFontFamily
import com.example.util.FormatUtils

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PayScaleScreen(
    isBengali: Boolean,
    modifier: Modifier = Modifier
) {
    var expandedGrade by remember { mutableIntStateOf(9) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = if (isBengali) "জাতীয় বেতন স্কেল ২০২৬ (গ্রেড ১ - ২০)" else "National Pay Scale 2026 (Grade 1–20)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = if (isBengali) {
                    "২০২৬ গেজেট (S.R.O. 347-Law/2026) ও ২০১৫ স্কেলের তুলনামূলক ধাপ দেখতে গ্রেডে ট্যাপ করুন"
                } else {
                    "Tap any grade to compare 2026 Gazette steps, phased payouts, and 2015 scale"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
        }

        items(PayScale2015.grades, key = { it.grade }) { grade ->
            val isExpanded = expandedGrade == grade.grade
            val p1Min = PayScale2015.getPayableBasicForStep(grade.grade, 0, PayScalePhase.PHASE_1_2026)
            val p2Min = PayScale2015.getPayableBasicForStep(grade.grade, 0, PayScalePhase.PHASE_2_2027)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { expandedGrade = if (isExpanded) -1 else grade.grade },
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                ),
                border = BorderStroke(
                    1.dp,
                    if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            BadgeChip(text = "${if (isBengali) "গ্রেড" else "Grade"} ${grade.grade}")
                            Column {
                                Text(
                                    text = "২০২৬: ৳ ${FormatUtils.numberFormatted(grade.minBasic2026)} - ${FormatUtils.numberFormatted(grade.maxBasic2026)}",
                                    fontFamily = MonoFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "২০১৫: ৳ ${FormatUtils.numberFormatted(grade.minBasic)} - ${FormatUtils.numberFormatted(grade.maxBasic)}",
                                    fontFamily = MonoFontFamily,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Icon(
                            imageVector = if (isExpanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                            contentDescription = null
                        )
                    }

                    AnimatedVisibility(visible = isExpanded) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            HorizontalDivider()
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = if (isBengali) {
                                    "ফেজ-১ (জুলাই–ডিসে. ২০২৬) প্রারম্ভিক: ${FormatUtils.taka(p1Min)}  |  ফেজ-২ (জানু–জুন ২০২৭): ${FormatUtils.taka(p2Min)}"
                                } else {
                                    "Phase 1 (Jul–Dec 2026) Start: ${FormatUtils.taka(p1Min)}  |  Phase 2 (Jan–Jun 2027): ${FormatUtils.taka(p2Min)}"
                                },
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = if (isBengali) "২০২৬ পূর্ণ বেতন ধাপসমূহ (${grade.incrementPercent}% ইনক্রিমেন্ট):" else "2026 Full Pay Steps (${grade.incrementPercent}% increment):",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(6.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                grade.steps2026.forEachIndexed { idx, step ->
                                    Surface(
                                        shape = MaterialTheme.shapes.extraSmall,
                                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
                                    ) {
                                        Text(
                                            text = "${idx + 1}: ${FormatUtils.numberFormatted(step)}",
                                            fontFamily = MonoFontFamily,
                                            style = MaterialTheme.typography.labelSmall,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
