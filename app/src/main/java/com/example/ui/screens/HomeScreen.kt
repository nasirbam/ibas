package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.TableChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.EmployeeProfile
import com.example.data.SavedApplicationEntity
import com.example.data.SavedPayBillEntity
import com.example.domain.PayBillResult
import com.example.ui.components.BadgeChip
import com.example.ui.components.SectionCard
import com.example.ui.theme.MonoFontFamily
import com.example.util.FormatUtils

data class HomeToolItem(
    val route: String,
    val titleBn: String,
    val titleEn: String,
    val subtitleBn: String,
    val subtitleEn: String,
    val icon: ImageVector,
    val testTag: String
)

@Composable
fun HomeScreen(
    profile: EmployeeProfile,
    payBillPreview: PayBillResult,
    savedBills: List<SavedPayBillEntity>,
    savedApps: List<SavedApplicationEntity>,
    onNavigate: (String) -> Unit,
    onDeleteBill: (Int) -> Unit,
    onDeleteApp: (Int) -> Unit,
    onOpenPayBillText: (String) -> Unit,
    onOpenAppText: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isBn = profile.isBengali

    val tools = listOf(
        HomeToolItem(
            route = "paybill",
            titleBn = "বেতন বিল কপি",
            titleEn = "Pay Bill Copy",
            subtitleBn = "মাসিক ও উৎসব বিল তৈরি ও PDF",
            subtitleEn = "Monthly & bonus bill + PDF",
            icon = Icons.Outlined.ReceiptLong,
            testTag = "tool_paybill"
        ),
        HomeToolItem(
            route = "gpf",
            titleBn = "জিপিএফ হিসাব",
            titleEn = "GPF Calculator",
            subtitleBn = "বার্ষিক সুদ (১৩%) ও অগ্রিম কিস্তি",
            subtitleEn = "Annual interest & advance EMI",
            icon = Icons.Outlined.Savings,
            testTag = "tool_gpf"
        ),
        HomeToolItem(
            route = "pension",
            titleBn = "পেনশন ও গ্র্যাচুইটি",
            titleEn = "Pension & PRL",
            subtitleBn = "আনুতোষিক, মাসিক পেনশন ও PRL",
            subtitleEn = "Gratuity, monthly & PRL date",
            icon = Icons.Outlined.AccountBalance,
            testTag = "tool_pension"
        ),
        HomeToolItem(
            route = "arrear",
            titleBn = "বকেয়া বেতন",
            titleEn = "Arrear Calculator",
            subtitleBn = "তারিখ ভিত্তিক বেতন ও ভাতা পার্থক্য",
            subtitleEn = "Date-range due vs drawn diff",
            icon = Icons.Outlined.Calculate,
            testTag = "tool_arrear"
        ),
        HomeToolItem(
            route = "applications",
            titleBn = "ছুটি ও যোগদান আবেদন",
            titleEn = "Leave & Joining",
            subtitleBn = "RRL, CL, EL, ML ও যোগদানপত্র",
            subtitleEn = "RRL, CL, EL, ML & Joining letter",
            icon = Icons.Outlined.EditNote,
            testTag = "tool_applications"
        ),
        HomeToolItem(
            route = "payscale",
            titleBn = "বেতন স্কেল ২০১৫",
            titleEn = "Pay Scale 2015",
            subtitleBn = "১-২০ গ্রেডের ধাপ ও বাড়ি ভাড়া",
            subtitleEn = "Grade 1–20 steps & HRA slabs",
            icon = Icons.Outlined.TableChart,
            testTag = "tool_payscale"
        )
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Civic Hero Banner Card
        item {
            Card(
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_office_banner),
                        contentDescription = if (isBn) "সরকারি দপ্তর ব্যানার" else "Civic office banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(190.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(190.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF072B1E).copy(alpha = 0.35f),
                                        Color(0xFF072B1E).copy(alpha = 0.88f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(190.dp)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BadgeChip(
                                text = if (isBn) "iBAS++ ম্যানুয়াল সহায়ক" else "iBAS++ Manual Companion",
                                containerColor = Color(0xFFFFDEA6),
                                contentColor = Color(0xFF2B1B00)
                            )
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.18f),
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .clip(CircleShape)
                                    .clickable { onNavigate("profile") }
                                    .testTag("hero_profile_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Outlined.Badge,
                                        contentDescription = if (isBn) "প্রোফাইল" else "Profile",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = if (isBn) "প্রোফাইল" else "Profile",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Column {
                            Text(
                                text = profile.fullName.ifBlank {
                                    if (isBn) "সরকারি কর্মকর্তা / কর্মচারী" else "Bangladesh Govt. Service Holder"
                                },
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = buildString {
                                    if (profile.designation.isNotBlank()) append(profile.designation)
                                    else append(if (isBn) "আপনার পদবি ও অফিস সেট করুন" else "Tap Profile to set your designation")
                                    append("  •  ")
                                    append(if (isBn) "গ্রেড ${profile.grade}" else "Grade ${profile.grade}")
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = if (isBn) "আনুমানিক নিট মাসিক বেতন" else "Est. Monthly Net Pay",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White.copy(alpha = 0.78f)
                                    )
                                    Text(
                                        text = FormatUtils.taka(payBillPreview.netPayable),
                                        fontFamily = MonoFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = Color(0xFFFFDEA6)
                                    )
                                }
                                FilledTonalButton(
                                    onClick = { onNavigate("paybill") },
                                    modifier = Modifier.testTag("hero_quick_paybill")
                                ) {
                                    Text(if (isBn) "বিল দেখুন" else "Open Bill")
                                    Spacer(Modifier.width(4.dp))
                                    Icon(
                                        Icons.AutoMirrored.Outlined.ArrowForward,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section Header for Tools
        item {
            Text(
                text = if (isBn) "সকল ম্যানুয়াল সেবা ও ক্যালকুলেটর" else "Manual iBAS++ Tools & Calculators",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // 2-Column Grid of Tools
        items(tools.chunked(2)) { rowTools ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowTools.forEach { tool ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(MaterialTheme.shapes.medium)
                            .clickable { onNavigate(tool.route) }
                            .testTag(tool.testTag),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = tool.icon,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = if (isBn) tool.titleBn else tool.titleEn,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = if (isBn) tool.subtitleBn else tool.subtitleEn,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // Saved History Section
        item {
            SectionCard(
                title = if (isBn) "সংরক্ষিত বিল ও আবেদনপত্র" else "Saved Bills & Applications",
                icon = Icons.Outlined.History
            ) {
                if (savedBills.isEmpty() && savedApps.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Outlined.AccountBalanceWallet,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = if (isBn) {
                                "এখনো কোনো বেতন বিল বা আবেদনপত্র সংরক্ষণ করা হয়নি।"
                            } else {
                                "No saved pay bills or applications yet."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    savedBills.take(4).forEach { bill ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onOpenPayBillText(bill.billTextSummary) }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Outlined.ReceiptLong,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${bill.billTypeLabel} (${bill.monthLabel})",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${if (isBn) "নিট প্রাপ্য" else "Net"}: ${FormatUtils.taka(bill.netPayable)} • Grade ${bill.grade}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(
                                onClick = { onDeleteBill(bill.id) },
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Icon(
                                    Icons.Outlined.DeleteOutline,
                                    contentDescription = if (isBn) "মুছুন" else "Delete",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    savedApps.take(4).forEach { app ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onOpenAppText(app.letterBody) }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Outlined.Description,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = app.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${app.dateRangeLabel} (${app.daysCount} ${if (isBn) "দিন" else "days"})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(
                                onClick = { onDeleteApp(app.id) },
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Icon(
                                    Icons.Outlined.DeleteOutline,
                                    contentDescription = if (isBn) "মুছুন" else "Delete",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
