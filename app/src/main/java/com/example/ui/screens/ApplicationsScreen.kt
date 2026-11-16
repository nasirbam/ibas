package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Preview
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.ApplicationFormInput
import com.example.domain.ApplicationTemplates
import com.example.domain.LeaveApplicationType
import com.example.ui.components.BadgeChip
import com.example.ui.components.ExportActionRow
import com.example.ui.components.SectionCard
import com.example.util.DocumentExporter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ApplicationsScreen(
    isBengali: Boolean,
    form: ApplicationFormInput,
    onFormChange: (ApplicationFormInput) -> Unit,
    onSelectType: (LeaveApplicationType) -> Unit,
    onSaveApplication: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val daysCount = ApplicationTemplates.calculateDaysInclusive(form.startDateIso, form.endDateIso)
    val generatedLetter = remember(form, isBengali) {
        ApplicationTemplates.generateLetter(form, isBengali)
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Application Type Selector
        item {
            SectionCard(
                title = if (isBengali) "আবেদনপত্রের ধরন নির্বাচন করুন" else "Select Official Application Type",
                icon = Icons.Outlined.Description
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    LeaveApplicationType.entries.forEach { appType ->
                        FilterChip(
                            selected = form.type == appType,
                            onClick = { onSelectType(appType) },
                            label = { Text(if (isBengali) appType.titleBn else appType.titleEn) },
                            modifier = Modifier.testTag("app_type_${appType.code}")
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = if (isBengali) form.type.subtitleBn else form.type.subtitleEn,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Form Fields
        item {
            SectionCard(
                title = if (isBengali) "আবেদনকারীর ও ছুটির তথ্য" else "Applicant & Leave Details",
                icon = Icons.Outlined.EditNote,
                trailing = {
                    BadgeChip(text = "$daysCount ${if (isBengali) "দিন" else "days"}")
                }
            ) {
                OutlinedTextField(
                    value = form.recipientAuthority,
                    onValueChange = { onFormChange(form.copy(recipientAuthority = it)) },
                    label = { Text(if (isBengali) "প্রাপক (বরাবর)" else "Recipient Authority (To)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = form.throughAuthority,
                    onValueChange = { onFormChange(form.copy(throughAuthority = it)) },
                    label = { Text(if (isBengali) "মাধ্যম (ঐচ্ছিক, যেমন: প্রধান শিক্ষক / কর্মকর্তা)" else "Through (Optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = form.applicantName,
                        onValueChange = { onFormChange(form.copy(applicantName = it)) },
                        label = { Text(if (isBengali) "আবেদনকারীর নাম" else "Applicant Name") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = form.designation,
                        onValueChange = { onFormChange(form.copy(designation = it)) },
                        label = { Text(if (isBengali) "পদবি" else "Designation") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = form.officeName,
                    onValueChange = { onFormChange(form.copy(officeName = it)) },
                    label = { Text(if (isBengali) "কর্মস্থল / দপ্তরের নাম" else "Office / Department Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (form.type != LeaveApplicationType.GPF_ADVANCE) {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = form.startDateIso,
                            onValueChange = { onFormChange(form.copy(startDateIso = it)) },
                            label = { Text(if (isBengali) "শুরু (YYYY-MM-DD)" else "From (YYYY-MM-DD)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = form.endDateIso,
                            onValueChange = { onFormChange(form.copy(endDateIso = it)) },
                            label = { Text(if (isBengali) "শেষ (YYYY-MM-DD)" else "To (YYYY-MM-DD)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (form.type == LeaveApplicationType.JOINING) {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = form.joiningDateIso,
                            onValueChange = { onFormChange(form.copy(joiningDateIso = it)) },
                            label = { Text(if (isBengali) "যোগদানের তারিখ" else "Joining Date") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = form.leaveMemoRef,
                            onValueChange = { onFormChange(form.copy(leaveMemoRef = it)) },
                            label = { Text(if (isBengali) "মঞ্জুরি স্মারক ও তারিখ" else "Sanction Memo No") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (form.type == LeaveApplicationType.GPF_ADVANCE) {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = form.requestedAmount,
                            onValueChange = { onFormChange(form.copy(requestedAmount = it)) },
                            label = { Text(if (isBengali) "অগ্রিম টাকার পরিমাণ" else "Requested Amount") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = form.gpfAccountNo,
                            onValueChange = { onFormChange(form.copy(gpfAccountNo = it)) },
                            label = { Text(if (isBengali) "জিপিএফ হিসাব নম্বর" else "GPF Account No") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = form.reason,
                    onValueChange = { onFormChange(form.copy(reason = it)) },
                    label = {
                        Text(
                            when (form.type) {
                                LeaveApplicationType.JOINING -> if (isBengali) "ছুটির ধরন (যেমন: শ্রান্তি বিনোদন/অর্জিত ছুটি)" else "Type of Leave Availed"
                                else -> if (isBengali) "কারণ / বিবরণ" else "Reason / Remarks"
                            }
                        )
                    },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = form.leaveAddress,
                        onValueChange = { onFormChange(form.copy(leaveAddress = it)) },
                        label = { Text(if (isBengali) "ছুটিকালীন ঠিকানা (ঐচ্ছিক)" else "Leave Address") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = form.mobileNo,
                        onValueChange = { onFormChange(form.copy(mobileNo = it)) },
                        label = { Text(if (isBengali) "মোবাইল নম্বর" else "Mobile No") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Official Paper Preview Card
        item {
            SectionCard(
                title = if (isBengali) "সরকারি আবেদনপত্র প্রিভিউ (A4 ফরম্যাট)" else "Official Application Preview",
                icon = Icons.Outlined.Preview
            ) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = Color(0xFFFFFEFA),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFFEFA))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = generatedLetter,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                lineHeight = 22.sp,
                                color = Color(0xFF1A1F1C)
                            )
                        )
                    }
                }
                Spacer(Modifier.height(14.dp))
                ExportActionRow(
                    isBengali = isBengali,
                    onCopy = {
                        DocumentExporter.copyToClipboard(context, form.type.titleEn, generatedLetter)
                        Toast.makeText(
                            context,
                            if (isBengali) "আবেদনপত্র কপি করা হয়েছে" else "Application copied to clipboard",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onCopySutonnyMj = {
                        DocumentExporter.copySutonnyMjToClipboard(context, form.type.titleEn, generatedLetter)
                        Toast.makeText(
                            context,
                            "SutonnyMJ (Bijoy) ফরম্যাটে কপি হয়েছে! MS Word-এ পেস্ট করে SutonnyMJ ফন্ট দিন।",
                            Toast.LENGTH_LONG
                        ).show()
                    },
                    onSharePdf = {
                        DocumentExporter.sharePdf(
                            context = context,
                            fileName = "Application_${form.type.code}",
                            headerTitle = if (isBengali) form.type.titleBn else form.type.titleEn,
                            subHeader = form.officeName.ifBlank { "Official Application" },
                            bodyText = generatedLetter
                        )
                    },
                    onPrint = {
                        DocumentExporter.printPdf(
                            context = context,
                            jobName = "Application_${form.type.code}",
                            headerTitle = if (isBengali) form.type.titleBn else form.type.titleEn,
                            subHeader = form.officeName.ifBlank { "Official Application" },
                            bodyText = generatedLetter
                        )
                    },
                    onSave = {
                        onSaveApplication()
                        Toast.makeText(
                            context,
                            if (isBengali) "আবেদনপত্র সংরক্ষণ করা হয়েছে" else "Application saved to history",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        }
    }
}
