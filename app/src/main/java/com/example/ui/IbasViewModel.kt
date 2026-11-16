package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.EmployeeProfile
import com.example.data.IbasRepository
import com.example.data.ProfilePreferencesRepository
import com.example.data.SavedApplicationEntity
import com.example.data.SavedPayBillEntity
import com.example.domain.ApplicationFormInput
import com.example.domain.ApplicationTemplates
import com.example.domain.ArrearCalculatorEngine
import com.example.domain.ArrearInput
import com.example.domain.ArrearResult
import com.example.domain.BillType
import com.example.domain.GpfAnnualResult
import com.example.domain.GpfCalculatorEngine
import com.example.domain.GpfInput
import com.example.domain.LeaveApplicationType
import com.example.domain.PayBillCalculator
import com.example.domain.PayBillInput
import com.example.domain.PayBillResult
import com.example.domain.PayScale2015
import com.example.domain.PensionCalculatorEngine
import com.example.domain.PensionInput
import com.example.domain.PensionResult
import com.example.util.FormatUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class IbasViewModel(application: Application) : AndroidViewModel(application) {
    private val prefsRepo = ProfilePreferencesRepository(application)
    private val db = AppDatabase.getInstance(application)
    private val ibasRepo = IbasRepository(db.ibasDao())

    val profile: StateFlow<EmployeeProfile> = prefsRepo.profileFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), EmployeeProfile())

    val savedPayBills: StateFlow<List<SavedPayBillEntity>> = ibasRepo.savedPayBills
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedApplications: StateFlow<List<SavedApplicationEntity>> = ibasRepo.savedApplications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Pay Bill State
    private val _payBillInput = MutableStateFlow(PayBillInput())
    val payBillInput: StateFlow<PayBillInput> = _payBillInput.asStateFlow()

    private val _payBillResult = MutableStateFlow(PayBillCalculator.calculate(PayBillInput()))
    val payBillResult: StateFlow<PayBillResult> = _payBillResult.asStateFlow()

    private val _selectedBillMonth = MutableStateFlow("November 2026")
    val selectedBillMonth: StateFlow<String> = _selectedBillMonth.asStateFlow()

    // GPF State
    private val _gpfInput = MutableStateFlow(GpfInput())
    val gpfInput: StateFlow<GpfInput> = _gpfInput.asStateFlow()

    private val _gpfResult = MutableStateFlow(GpfCalculatorEngine.calculateFiscalYear(GpfInput()))
    val gpfResult: StateFlow<GpfAnnualResult> = _gpfResult.asStateFlow()

    // Pension State
    private val _pensionInput = MutableStateFlow(PensionInput())
    val pensionInput: StateFlow<PensionInput> = _pensionInput.asStateFlow()

    private val _pensionResult = MutableStateFlow(PensionCalculatorEngine.calculate(PensionInput()))
    val pensionResult: StateFlow<PensionResult> = _pensionResult.asStateFlow()

    // Arrear State
    private val _arrearInput = MutableStateFlow(ArrearInput())
    val arrearInput: StateFlow<ArrearInput> = _arrearInput.asStateFlow()

    private val _arrearResult = MutableStateFlow(ArrearCalculatorEngine.calculate(ArrearInput()))
    val arrearResult: StateFlow<ArrearResult> = _arrearResult.asStateFlow()

    // Application Form State
    private val _appFormInput = MutableStateFlow(ApplicationFormInput())
    val appFormInput: StateFlow<ApplicationFormInput> = _appFormInput.asStateFlow()

    init {
        viewModelScope.launch {
            val initialProfile = prefsRepo.profileFlow.first()
            syncCalculatorsWithProfile(initialProfile)
        }
    }

    private fun syncCalculatorsWithProfile(p: EmployeeProfile) {
        val gradeData = PayScale2015.getGrade(p.grade)
        val oldBasic = gradeData.steps.getOrElse(p.stepIndex) { gradeData.minBasic }
        val new2026Basic = PayScale2015.getPayableBasicForStep(p.grade, p.stepIndex, p.payScalePhase)
        updatePayBillInput(
            _payBillInput.value.copy(
                grade = p.grade,
                stepIndex = p.stepIndex,
                phase = p.payScalePhase,
                basicPay = p.basicPay,
                tier = p.houseRentTier,
                gpfPercent = p.gpfPercent
            )
        )
        updateGpfInput(
            _gpfInput.value.copy(
                openingBalance = p.gpfOpeningBalance,
                monthlySubscription = (p.basicPay * (p.gpfPercent / 100.0))
            )
        )
        updatePensionInput(
            _pensionInput.value.copy(
                lastBasicPay = gradeData.steps2026.getOrElse(p.stepIndex) { gradeData.minBasic2026 },
                birthDateIso = p.birthDateIso
            )
        )
        updateArrearInput(
            _arrearInput.value.copy(
                drawnBasic = oldBasic,
                dueBasic = new2026Basic,
                tier = p.houseRentTier,
                gpfPercent = p.gpfPercent
            )
        )
        _appFormInput.update { form ->
            form.copy(
                applicantName = p.fullName,
                designation = p.designation,
                officeName = p.officeName,
                recipientAuthority = p.authorityTitle,
                ibasId = p.ibasId,
                gpfAccountNo = p.gpfAccountNo,
                mobileNo = p.mobileNo
            )
        }
    }

    fun toggleLanguage() {
        viewModelScope.launch {
            prefsRepo.toggleLanguage(!profile.value.isBengali)
        }
    }

    fun saveProfile(newProfile: EmployeeProfile) {
        viewModelScope.launch {
            prefsRepo.saveProfile(newProfile)
            syncCalculatorsWithProfile(newProfile)
        }
    }

    // Pay Bill Actions
    fun updateBillMonth(month: String) {
        _selectedBillMonth.value = month
    }

    fun updatePayBillInput(newInput: PayBillInput) {
        _payBillInput.value = newInput
        _payBillResult.value = PayBillCalculator.calculate(newInput)
    }

    fun buildPayBillTextSummary(isBengali: Boolean): String {
        val p = profile.value
        val inp = _payBillInput.value
        val res = _payBillResult.value
        val month = _selectedBillMonth.value
        val typeLabel = if (isBengali) inp.billType.titleBn else inp.billType.titleEn

        return buildString {
            appendLine("=========================================")
            appendLine(if (isBengali) "গণপ্রজাতন্ত্রী বাংলাদেশ সরকার - বেতন বিল কপি" else "GOVT. OF BANGLADESH - MANUAL PAY BILL COPY")
            appendLine("=========================================")
            appendLine("${if (isBengali) "বিলের ধরন" else "Bill Type"}: $typeLabel")
            appendLine("${if (isBengali) "মাস" else "Month"}: $month")
            if (p.fullName.isNotBlank()) appendLine("${if (isBengali) "নাম" else "Name"}: ${p.fullName}")
            if (p.designation.isNotBlank()) appendLine("${if (isBengali) "পদবি" else "Designation"}: ${p.designation}")
            if (p.officeName.isNotBlank()) appendLine("${if (isBengali) "দপ্তর" else "Office"}: ${p.officeName}")
            if (p.ibasId.isNotBlank()) appendLine("iBAS++ ID: ${p.ibasId}")
            appendLine("${if (isBengali) "গ্রেড" else "Grade"}: ${inp.grade} (${PayScale2015.getGrade(inp.grade).scaleText})")
            appendLine("-----------------------------------------")
            appendLine(if (isBengali) "[ভাতাদি / EARNINGS]" else "[EARNINGS / ALLOWANCES]")
            appendLine("Basic Pay / মূল বেতন         : ${FormatUtils.taka(res.basicPay)}")
            if (res.houseRentAllowance > 0) appendLine("House Rent / বাড়ি ভাড়া      : ${FormatUtils.taka(res.houseRentAllowance)}")
            if (res.medicalAllowance > 0) appendLine("Medical / চিকিৎসা ভাতা       : ${FormatUtils.taka(res.medicalAllowance)}")
            if (res.educationAllowance > 0) appendLine("Education / শিক্ষা সহায়ক    : ${FormatUtils.taka(res.educationAllowance)}")
            if (res.tiffinAllowance > 0) appendLine("Tiffin / টিফিন ভাতা          : ${FormatUtils.taka(res.tiffinAllowance)}")
            if (res.washingAllowance > 0) appendLine("Washing / ধোলাই ভাতা         : ${FormatUtils.taka(res.washingAllowance)}")
            if (res.specialBenefit > 0) appendLine("Special Benefit / বিশেষ সুবিধা: ${FormatUtils.taka(res.specialBenefit)}")
            if (res.chargeAllowance > 0) appendLine("Charge Allowance / দায়িত্ব    : ${FormatUtils.taka(res.chargeAllowance)}")
            if (res.conveyanceAllowance > 0) appendLine("Conveyance / যাতায়াত        : ${FormatUtils.taka(res.conveyanceAllowance)}")
            if (res.otherAllowance > 0) appendLine("Other Allowances / অন্যান্য   : ${FormatUtils.taka(res.otherAllowance)}")
            appendLine("Gross Total / মোট বেতন-ভাতা  : ${FormatUtils.taka(res.grossPay)}")
            appendLine("-----------------------------------------")
            appendLine(if (isBengali) "[কর্তনসমূহ / DEDUCTIONS]" else "[DEDUCTIONS]")
            if (res.gpfSubscription > 0) appendLine("GPF Subscription (${inp.gpfPercent}%)     : ${FormatUtils.taka(res.gpfSubscription)}")
            if (res.gpfLoanInstallment > 0) appendLine("GPF Loan / অগ্রিম কিস্তি     : ${FormatUtils.taka(res.gpfLoanInstallment)}")
            if (res.benevolentFund > 0) appendLine("Benevolent / কল্যাণ তহবিল    : ${FormatUtils.taka(res.benevolentFund)}")
            if (res.groupInsurance > 0) appendLine("Group Insurance / যৌথ বীমা   : ${FormatUtils.taka(res.groupInsurance)}")
            if (res.govtQuarterDeduction > 0) appendLine("Govt Quarter Rent            : ${FormatUtils.taka(res.govtQuarterDeduction)}")
            if (res.incomeTax > 0) appendLine("Income Tax (TDS) / আয়কর     : ${FormatUtils.taka(res.incomeTax)}")
            if (res.otherDeduction > 0) appendLine("Other Deduction / অন্যান্য   : ${FormatUtils.taka(res.otherDeduction)}")
            appendLine("Total Deduction / মোট কর্তন  : ${FormatUtils.taka(res.totalDeduction)}")
            appendLine("=========================================")
            appendLine("NET PAYABLE / নিট প্রাপ্য     : ${FormatUtils.taka(res.netPayable)}")
            appendLine("In Words: ${PayBillCalculator.amountInWordsEn(res.netPayable.toLong())}")
            appendLine("=========================================")
        }
    }

    fun saveCurrentPayBill(isBengali: Boolean) {
        viewModelScope.launch {
            val inp = _payBillInput.value
            val res = _payBillResult.value
            val summary = buildPayBillTextSummary(isBengali)
            ibasRepo.savePayBill(
                SavedPayBillEntity(
                    monthLabel = _selectedBillMonth.value,
                    billTypeLabel = if (isBengali) inp.billType.titleBn else inp.billType.titleEn,
                    grade = inp.grade,
                    basicPay = res.basicPay,
                    grossPay = res.grossPay,
                    totalDeduction = res.totalDeduction,
                    netPayable = res.netPayable,
                    billTextSummary = summary
                )
            )
        }
    }

    fun deletePayBill(id: Int) {
        viewModelScope.launch { ibasRepo.deletePayBill(id) }
    }

    // GPF Actions
    fun updateGpfInput(newInput: GpfInput) {
        _gpfInput.value = newInput
        _gpfResult.value = GpfCalculatorEngine.calculateFiscalYear(newInput)
    }

    // Pension Actions
    fun updatePensionInput(newInput: PensionInput) {
        _pensionInput.value = newInput
        _pensionResult.value = PensionCalculatorEngine.calculate(newInput)
    }

    // Arrear Actions
    fun updateArrearInput(newInput: ArrearInput) {
        _arrearInput.value = newInput
        _arrearResult.value = ArrearCalculatorEngine.calculate(newInput)
    }

    // Application Actions
    fun updateAppFormInput(newInput: ApplicationFormInput) {
        _appFormInput.value = newInput
    }

    fun selectApplicationType(type: LeaveApplicationType) {
        _appFormInput.update { it.copy(type = type) }
    }

    fun saveCurrentApplication(isBengali: Boolean) {
        viewModelScope.launch {
            val form = _appFormInput.value
            val letter = ApplicationTemplates.generateLetter(form, isBengali)
            val days = ApplicationTemplates.calculateDaysInclusive(form.startDateIso, form.endDateIso)
            val title = if (isBengali) form.type.titleBn else form.type.titleEn
            ibasRepo.saveApplication(
                SavedApplicationEntity(
                    appTypeCode = form.type.code,
                    title = title,
                    dateRangeLabel = "${form.startDateIso} → ${form.endDateIso}",
                    daysCount = days,
                    letterBody = letter
                )
            )
        }
    }

    fun deleteApplication(id: Int) {
        viewModelScope.launch { ibasRepo.deleteApplication(id) }
    }
}
