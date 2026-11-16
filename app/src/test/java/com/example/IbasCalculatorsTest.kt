package com.example

import com.example.domain.ApplicationFormInput
import com.example.domain.ApplicationTemplates
import com.example.domain.ArrearCalculatorEngine
import com.example.domain.ArrearInput
import com.example.domain.BillType
import com.example.domain.GpfCalculatorEngine
import com.example.domain.GpfInput
import com.example.domain.HouseRentTier
import com.example.domain.LeaveApplicationType
import com.example.domain.PayBillCalculator
import com.example.domain.PayBillInput
import com.example.domain.PayScale2015
import com.example.domain.PayScalePhase
import com.example.domain.PensionCalculatorEngine
import com.example.domain.PensionInput
import com.example.util.BijoyConverter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class IbasCalculatorsTest {

    @Test
    fun testPayScale2026PhasedBasicPay() {
        // Grade 9: 2015 = 22,000, 2026 = 44,000, diff = 22,000
        // Phase 1 (40% for Grade 1-9) = 22,000 + 8,800 = 30,800
        assertEquals(30800, PayScale2015.getPayableBasicForStep(9, 0, PayScalePhase.PHASE_1_2026))
        // Phase 2 (70% for Grade 1-9) = 22,000 + 15,400 = 37,400
        assertEquals(37400, PayScale2015.getPayableBasicForStep(9, 0, PayScalePhase.PHASE_2_2027))
        // Full 2026 = 44,000
        assertEquals(44000, PayScale2015.getPayableBasicForStep(9, 0, PayScalePhase.FULL_2026))

        // Grade 20: 2015 = 8,250, 2026 = 20,000, diff = 11,750
        // Phase 1 (50% for Grade 10-20) = 8,250 + 5,875 = 14,125
        assertEquals(14125, PayScale2015.getPayableBasicForStep(20, 0, PayScalePhase.PHASE_1_2026))
    }

    @Test
    fun testPayBillCalculationGrade9_2026Phase1() {
        val input = PayBillInput(
            grade = 9,
            stepIndex = 0,
            phase = PayScalePhase.PHASE_1_2026,
            basicPay = 30800,
            tier = HouseRentTier.OTHER_DISTRICT,
            billType = BillType.MONTHLY_SALARY,
            childrenCount = 1,
            isAge50Plus = false,
            includeSpecialBenefit = false,
            gpfPercent = 10
        )
        val result = PayBillCalculator.calculate(input)
        // Interim HRA uses June 30 2026 basic (22,000 * 40% = 8,800)
        assertEquals(8800, result.houseRentAllowance)
        // 2026 Medical allowance under 50 = 3,000
        assertEquals(3000, result.medicalAllowance)
        assertEquals(500, result.educationAllowance)
        assertEquals(30800 + 8800 + 3000 + 500, result.grossPay)
        // GPF 10% of 30,800 = 3,080; Benevolent max 150; Group Ins 100
        assertEquals(3080, result.gpfSubscription)
        assertEquals(150, result.benevolentFund)
        assertEquals(100, result.groupInsurance)
        assertEquals(3330, result.totalDeduction)
        assertEquals(result.grossPay - result.totalDeduction, result.netPayable)
    }

    @Test
    fun testPayScaleGradesCountAndBounds() {
        assertEquals(20, PayScale2015.grades.size)
        assertEquals(156000, PayScale2015.getGrade(1).minBasic2026)
        assertEquals(20000, PayScale2015.getGrade(20).minBasic2026)
    }

    @Test
    fun testGpfFiscalYearInterest() {
        val result = GpfCalculatorEngine.calculateFiscalYear(
            GpfInput(
                openingBalance = 100000.0,
                monthlySubscription = 2000.0,
                monthlyLoanRecovery = 0.0,
                withdrawalAmount = 0.0
            )
        )
        assertEquals(12, result.rows.size)
        assertEquals(24000.0, result.totalSubscription, 0.01)
        assertTrue(result.totalAnnualInterest > 13000.0)
        assertTrue(result.closingBalance > 137000.0)
    }

    @Test
    fun testPension25YearsGratuity() {
        val result = PensionCalculatorEngine.calculate(
            PensionInput(
                lastBasicPay = 44000,
                qualifyingYears = 25,
                ageAtRetirement = 59
            )
        )
        assertEquals(90, result.pensionPercent)
        assertEquals(39600, result.grossMonthlyPension)
        assertEquals(19800, result.surrenderedHalfPension)
        assertEquals(230, result.gratuityRatePerTaka)
        assertEquals(19800L * 230L, result.totalGratuityLumpSum)
    }

    @Test
    fun testArrearCalculatorRange() {
        val result = ArrearCalculatorEngine.calculate(
            ArrearInput(
                startDateIso = "2026-07-01",
                endDateIso = "2026-10-31",
                drawnBasic = 22000,
                dueBasic = 30800,
                tier = HouseRentTier.OTHER_DISTRICT,
                includeHouseRentDiff = false,
                gpfPercent = 10,
                deductGpfFromArrear = true
            )
        )
        assertEquals(4, result.rows.size)
        assertTrue(result.netArrearPayable > 0)
    }

    @Test
    fun testLeaveApplicationAndBijoySutonnyMjConversion() {
        val letterBn = ApplicationTemplates.generateLetter(
            ApplicationFormInput(
                type = LeaveApplicationType.RRL,
                applicantName = "মোঃ নাসির উদ্দিন",
                designation = "সহকারী প্রোগ্রামার",
                officeName = "উপজেলা আইসিটি অফিস"
            ),
            isBengali = true
        )
        assertTrue(letterBn.contains("শ্রান্তি ও বিনোদন ছুটি"))
        val bijoy = BijoyConverter.toSutonnyMJ("বাংলাদেশ")
        assertTrue(bijoy.isNotBlank())
        assertTrue(bijoy.contains("evsjv"))
    }
}
