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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
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
import com.example.data.EmployeeProfile
import com.example.domain.HouseRentTier
import com.example.domain.PayScale2015
import com.example.ui.components.NumberInputField
import com.example.ui.components.SectionCard
import com.example.util.FormatUtils

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    profile: EmployeeProfile,
    onSaveProfile: (EmployeeProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var draft by remember(profile) { mutableStateOf(profile) }
    var gradeExpanded by remember { mutableStateOf(false) }
    val isBn = draft.isBengali

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionCard(
                title = if (isBn) "ভাষা ও প্রদর্শন সেটিংস" else "Language & Display",
                icon = Icons.Outlined.Language
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBn) "বাংলা ভাষা ব্যবহার করুন (Bengali UI)" else "Use Bengali Language (বাংলা)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Switch(
                        checked = draft.isBengali,
                        onCheckedChange = { checked ->
                            draft = draft.copy(isBengali = checked)
                            onSaveProfile(draft)
                        },
                        modifier = Modifier.testTag("profile_language_switch")
                    )
                }
            }
        }

        item {
            SectionCard(
                title = if (isBn) "কর্মকর্তা/কর্মচারীর ব্যক্তিগত তথ্য" else "Employee Identity & Office",
                icon = Icons.Outlined.Badge
            ) {
                OutlinedTextField(
                    value = draft.fullName,
                    onValueChange = { draft = draft.copy(fullName = it) },
                    label = { Text(if (isBn) "পূর্ণ নাম" else "Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = draft.designation,
                    onValueChange = { draft = draft.copy(designation = it) },
                    label = { Text(if (isBn) "পদবি" else "Designation") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = draft.officeName,
                    onValueChange = { draft = draft.copy(officeName = it) },
                    label = { Text(if (isBn) "দপ্তর / প্রতিষ্ঠানের নাম" else "Office / Department Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = draft.authorityTitle,
                    onValueChange = { draft = draft.copy(authorityTitle = it) },
                    label = { Text(if (isBn) "নিয়ন্ত্রণকারী কর্মকর্তা (আবেদন প্রাপক)" else "Controlling Authority (Default Recipient)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = draft.ibasId,
                        onValueChange = { draft = draft.copy(ibasId = it) },
                        label = { Text("iBAS++ / PIN") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = draft.gpfAccountNo,
                        onValueChange = { draft = draft.copy(gpfAccountNo = it) },
                        label = { Text(if (isBn) "জিপিএফ হিসাব নং" else "GPF A/C No") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = draft.birthDateIso,
                        onValueChange = { draft = draft.copy(birthDateIso = it) },
                        label = { Text(if (isBn) "জন্ম তারিখ (YYYY-MM-DD)" else "DOB (YYYY-MM-DD)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = draft.mobileNo,
                        onValueChange = { draft = draft.copy(mobileNo = it) },
                        label = { Text(if (isBn) "মোবাইল নম্বর" else "Mobile No") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            SectionCard(
                title = if (isBn) "বেতন গ্রেড ও জিপিএফ ডিফল্ট" else "Pay Grade & GPF Defaults",
                icon = Icons.Outlined.WorkOutline
            ) {
                Text(
                    text = if (isBn) "পে-স্কেল ২০২৬ পর্যায়" else "Pay Scale 2026 Phase",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    com.example.domain.PayScalePhase.entries.forEach { phase ->
                        FilterChip(
                            selected = draft.payScalePhase == phase,
                            onClick = {
                                val autoBasic = PayScale2015.getPayableBasicForStep(draft.grade, draft.stepIndex, phase)
                                draft = draft.copy(payScalePhase = phase, basicPay = autoBasic)
                            },
                            label = { Text(if (isBn) phase.shortBn else phase.shortEn) }
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ExposedDropdownMenuBox(
                        expanded = gradeExpanded,
                        onExpandedChange = { gradeExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = "${if (isBn) "গ্রেড" else "Grade"} ${draft.grade}",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(if (isBn) "গ্রেড" else "Grade") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = gradeExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        )
                        ExposedDropdownMenu(
                            expanded = gradeExpanded,
                            onDismissRequest = { gradeExpanded = false }
                        ) {
                            PayScale2015.grades.forEach { g ->
                                DropdownMenuItem(
                                    text = { Text("Grade ${g.grade} (২০২৬: ${FormatUtils.numberFormatted(g.minBasic2026)})") },
                                    onClick = {
                                        val autoBasic = PayScale2015.getPayableBasicForStep(g.grade, 0, draft.payScalePhase)
                                        draft = draft.copy(grade = g.grade, stepIndex = 0, basicPay = autoBasic)
                                        gradeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    NumberInputField(
                        value = draft.basicPay.toString(),
                        onValueChange = { draft = draft.copy(basicPay = it.toIntOrNull() ?: 0) },
                        label = if (isBn) "মূল বেতন (২০২৬)" else "Basic Pay (2026)",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    NumberInputField(
                        value = draft.gpfPercent.toString(),
                        onValueChange = { draft = draft.copy(gpfPercent = (it.toIntOrNull() ?: 10).coerceIn(0, 30)) },
                        label = if (isBn) "জিপিএফ চাঁদা (%)" else "GPF %",
                        prefix = null,
                        suffix = "%",
                        modifier = Modifier.weight(1f)
                    )
                    NumberInputField(
                        value = draft.gpfOpeningBalance.toLong().toString(),
                        onValueChange = { draft = draft.copy(gpfOpeningBalance = it.toDoubleOrNull() ?: 0.0) },
                        label = if (isBn) "বর্তমান জিপিএফ স্থিতি" else "GPF Opening Balance",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(12.dp))
                Text(
                    text = if (isBn) "কর্মস্থল এলাকা (বাড়ি ভাড়া)" else "Posting Location (HRA)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(6.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    HouseRentTier.entries.forEach { tier ->
                        FilterChip(
                            selected = draft.houseRentTier == tier,
                            onClick = { draft = draft.copy(houseRentTier = tier) },
                            label = { Text(if (isBn) tier.shortBn else tier.shortEn) }
                        )
                    }
                }
            }
        }

        item {
            Button(
                onClick = {
                    onSaveProfile(draft)
                    Toast.makeText(
                        context,
                        if (isBn) "প্রোফাইল সংরক্ষণ ও সকল ক্যালকুলেটরে যুক্ত হয়েছে" else "Profile saved & synced across all tools",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .minimumInteractiveComponentSize()
                    .testTag("save_profile_button")
            ) {
                Icon(Icons.Outlined.CheckCircle, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (isBn) "প্রোফাইল সংরক্ষণ করুন" else "Save Profile & Sync Tools")
            }
        }
    }
}
