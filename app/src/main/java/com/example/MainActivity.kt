package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.IbasViewModel
import com.example.ui.screens.ApplicationsScreen
import com.example.ui.screens.ArrearScreen
import com.example.ui.screens.CalculatorsHubScreen
import com.example.ui.screens.GpfScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PayBillScreen
import com.example.ui.screens.PayScaleScreen
import com.example.ui.screens.PensionScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.IbasSahayakTheme
import com.example.util.DocumentExporter

private data class BottomNavTab(
    val route: String,
    val labelBn: String,
    val labelEn: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

private val mainTabs = listOf(
    BottomNavTab("home", "হোম", "Home", Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavTab("paybill", "বেতন বিল", "Pay Bill", Icons.Filled.ReceiptLong, Icons.Outlined.ReceiptLong),
    BottomNavTab("calculators", "হিসাব", "Calculators", Icons.Filled.Calculate, Icons.Outlined.Calculate),
    BottomNavTab("applications", "আবেদন", "Letters", Icons.Filled.EditNote, Icons.Outlined.EditNote)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IbasSahayakTheme {
                IbasSahayakApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IbasSahayakApp(viewModel: IbasViewModel = viewModel()) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val currentRoute = currentDestination?.route ?: "home"

    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val savedBills by viewModel.savedPayBills.collectAsStateWithLifecycle()
    val savedApps by viewModel.savedApplications.collectAsStateWithLifecycle()

    val payBillInput by viewModel.payBillInput.collectAsStateWithLifecycle()
    val payBillResult by viewModel.payBillResult.collectAsStateWithLifecycle()
    val selectedBillMonth by viewModel.selectedBillMonth.collectAsStateWithLifecycle()

    val gpfInput by viewModel.gpfInput.collectAsStateWithLifecycle()
    val gpfResult by viewModel.gpfResult.collectAsStateWithLifecycle()

    val pensionInput by viewModel.pensionInput.collectAsStateWithLifecycle()
    val pensionResult by viewModel.pensionResult.collectAsStateWithLifecycle()

    val arrearInput by viewModel.arrearInput.collectAsStateWithLifecycle()
    val arrearResult by viewModel.arrearResult.collectAsStateWithLifecycle()

    val appFormInput by viewModel.appFormInput.collectAsStateWithLifecycle()

    val isBn = profile.isBengali
    var previewDocumentTitle by remember { mutableStateOf<String?>(null) }
    var previewDocumentBody by remember { mutableStateOf<String?>(null) }

    val topLevelRoutes = setOf("home", "paybill", "calculators", "applications")
    val isTopLevel = currentRoute in topLevelRoutes

    val topBarTitle = when (currentRoute) {
        "home" -> if (isBn) "iBAS সহায়ক" else "iBAS Sahayak"
        "paybill" -> if (isBn) "বেতন বিল কপি" else "Pay Bill Copy"
        "calculators" -> if (isBn) "হিসাব ও ক্যালকুলেটর" else "Calculators"
        "applications" -> if (isBn) "আবেদনপত্র জেনারেটর" else "Official Letters"
        "gpf" -> if (isBn) "জিপিএফ ক্যালকুলেটর" else "GPF Calculator"
        "pension" -> if (isBn) "পেনশন ও গ্র্যাচুইটি" else "Pension & PRL"
        "arrear" -> if (isBn) "বকেয়া বেতন হিসাব" else "Arrear Calculator"
        "payscale" -> if (isBn) "বেতন স্কেল ২০১৫" else "Pay Scale 2015"
        "profile" -> if (isBn) "কর্মকর্তা প্রোফাইল" else "Employee Profile"
        else -> "iBAS Sahayak"
    }

    fun navigateToTab(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavRail = maxWidth >= 600.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = topBarTitle,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        if (!isTopLevel) {
                            IconButton(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                    contentDescription = if (isBn) "পেছনে যান" else "Back"
                                )
                            }
                        }
                    },
                    actions = {
                        FilledTonalButton(
                            onClick = { viewModel.toggleLanguage() },
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .testTag("topbar_language_toggle")
                        ) {
                            Icon(
                                Icons.Outlined.Translate,
                                contentDescription = if (isBn) "ভাষা পরিবর্তন" else "Switch Language",
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            Text(if (isBn) "EN" else "বাংলা")
                        }
                        if (currentRoute != "profile") {
                            IconButton(
                                onClick = { navController.navigate("profile") },
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("topbar_profile_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Badge,
                                    contentDescription = if (isBn) "প্রোফাইল" else "Profile"
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                if (!useNavRail) {
                    NavigationBar {
                        mainTabs.forEach { tab ->
                            val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                            NavigationBarItem(
                                selected = selected,
                                onClick = { navigateToTab(tab.route) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = if (isBn) tab.labelBn else tab.labelEn
                                    )
                                },
                                label = {
                                    Text(
                                        text = if (isBn) tab.labelBn else tab.labelEn,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                modifier = Modifier.testTag("nav_tab_${tab.route}")
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (useNavRail) {
                    NavigationRail(modifier = Modifier.fillMaxHeight()) {
                        mainTabs.forEach { tab ->
                            val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                            NavigationRailItem(
                                selected = selected,
                                onClick = { navigateToTab(tab.route) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = if (isBn) tab.labelBn else tab.labelEn
                                    )
                                },
                                label = { Text(if (isBn) tab.labelBn else tab.labelEn) }
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier
                            .fillMaxSize()
                            .widthIn(max = 720.dp)
                    ) {
                        composable("home") {
                            HomeScreen(
                                profile = profile,
                                payBillPreview = payBillResult,
                                savedBills = savedBills,
                                savedApps = savedApps,
                                onNavigate = { route ->
                                    if (route in topLevelRoutes) navigateToTab(route)
                                    else navController.navigate(route)
                                },
                                onDeleteBill = { viewModel.deletePayBill(it) },
                                onDeleteApp = { viewModel.deleteApplication(it) },
                                onOpenPayBillText = { text ->
                                    previewDocumentTitle = if (isBn) "সংরক্ষিত বেতন বিল কপি" else "Saved Pay Bill Copy"
                                    previewDocumentBody = text
                                },
                                onOpenAppText = { text ->
                                    previewDocumentTitle = if (isBn) "সংরক্ষিত আবেদনপত্র" else "Saved Application"
                                    previewDocumentBody = text
                                }
                            )
                        }

                        composable("paybill") {
                            PayBillScreen(
                                isBengali = isBn,
                                selectedMonth = selectedBillMonth,
                                input = payBillInput,
                                result = payBillResult,
                                onMonthChange = viewModel::updateBillMonth,
                                onInputChange = viewModel::updatePayBillInput,
                                buildSummaryText = { viewModel.buildPayBillTextSummary(isBn) },
                                onSaveBill = { viewModel.saveCurrentPayBill(isBn) }
                            )
                        }

                        composable("calculators") {
                            CalculatorsHubScreen(
                                isBengali = isBn,
                                initialTab = 0,
                                gpfInput = gpfInput,
                                gpfResult = gpfResult,
                                onGpfInputChange = viewModel::updateGpfInput,
                                pensionInput = pensionInput,
                                pensionResult = pensionResult,
                                onPensionInputChange = viewModel::updatePensionInput,
                                arrearInput = arrearInput,
                                arrearResult = arrearResult,
                                onArrearInputChange = viewModel::updateArrearInput
                            )
                        }

                        composable("applications") {
                            ApplicationsScreen(
                                isBengali = isBn,
                                form = appFormInput,
                                onFormChange = viewModel::updateAppFormInput,
                                onSelectType = viewModel::selectApplicationType,
                                onSaveApplication = { viewModel.saveCurrentApplication(isBn) }
                            )
                        }

                        composable("gpf") {
                            GpfScreen(
                                isBengali = isBn,
                                input = gpfInput,
                                result = gpfResult,
                                onInputChange = viewModel::updateGpfInput
                            )
                        }

                        composable("pension") {
                            PensionScreen(
                                isBengali = isBn,
                                input = pensionInput,
                                result = pensionResult,
                                onInputChange = viewModel::updatePensionInput
                            )
                        }

                        composable("arrear") {
                            ArrearScreen(
                                isBengali = isBn,
                                input = arrearInput,
                                result = arrearResult,
                                onInputChange = viewModel::updateArrearInput
                            )
                        }

                        composable("payscale") {
                            PayScaleScreen(isBengali = isBn)
                        }

                        composable("profile") {
                            ProfileScreen(
                                profile = profile,
                                onSaveProfile = viewModel::saveProfile
                            )
                        }
                    }
                }
            }
        }
    }

    // Saved Document Preview Dialog
    if (previewDocumentBody != null && previewDocumentTitle != null) {
        val docBody = previewDocumentBody ?: ""
        val docTitle = previewDocumentTitle ?: ""
        AlertDialog(
            onDismissRequest = {
                previewDocumentBody = null
                previewDocumentTitle = null
            },
            title = { Text(docTitle) },
            text = {
                Text(
                    text = docBody,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        DocumentExporter.sharePdf(
                            context = context,
                            fileName = "Saved_Document",
                            headerTitle = docTitle,
                            subHeader = "iBAS Sahayak",
                            bodyText = docBody
                        )
                    }
                ) {
                    Text(if (isBn) "PDF শেয়ার" else "Share PDF")
                }
            },
            dismissButton = {
                Row {
                    TextButton(
                        onClick = {
                            DocumentExporter.copyToClipboard(context, docTitle, docBody)
                            Toast.makeText(
                                context,
                                if (isBn) "কপি করা হয়েছে" else "Copied",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    ) {
                        Text(if (isBn) "কপি" else "Copy")
                    }
                    Spacer(Modifier.width(4.dp))
                    TextButton(
                        onClick = {
                            previewDocumentBody = null
                            previewDocumentTitle = null
                        }
                    ) {
                        Text(if (isBn) "বন্ধ করুন" else "Close")
                    }
                }
            }
        )
    }
}
