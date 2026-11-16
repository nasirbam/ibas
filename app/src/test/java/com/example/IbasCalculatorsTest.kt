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
import com.example.domain.PensionCalculatorEngine
import com.example.domain.PensionInput
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class IbasCalculatorsTest {

    @Test
    fun testPayBillCalculationGrade9() {
        val input = PayBillInput(
            grade = 9,
            basicPay = 22000,
            tier = HouseRentTier.OTHER_DISTRICT,
            billType = BillType.MONTHLY_SALARY,
            childrenCount = 1,
            includeSpecialBenefit = true,
            gpfPercent = 10
        )
        val result = PayBillCalculator.calculate(input)
        // HRA: 40% of 22000 = 8800 (min 7200)
        assertEquals(8800, result.houseRentAllowance)
        // Medical: 1500, Edu: 500, Special Benefit 5%: 1100
        assertEquals(1500, result.medicalAllowance)
        assertEquals(500, result.educationAllowance)
        assertEquals(1100, result.specialBenefit)
        assertEquals(22000 + 8800 + 1500 + 500 + 1100, result.grossPay)
        // GPF 10%: 2200, Benevolent 1% max 150: 150, Group Ins Grade 9: 100
        assertEquals(2200, result.gpfSubscription)
        assertEquals(150, result.benevolentFund)
        assertEquals(100, result.groupInsurance)
        assertEquals(2450, result.totalDeduction)
        assertEquals(result.grossPay - result.totalDeduction, result.netPayable)
    }

    @Test
    fun testPayScaleGradesCountAndBounds() {
        assertEquals(20, PayScale2015.grades.size)
        assertEquals(78000, PayScale2015.getGrade(1).minBasic)
        assertEquals(8250, PayScale2015.getGrade(20).minBasic)
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
                lastBasicPay = 40000,
                qualifyingYears = 25,
                ageAtRetirement = 59
            )
        )
        assertEquals(90, result.pensionPercent)
        assertEquals(36000, result.grossMonthlyPension)
        assertEquals(18000, result.surrenderedHalfPension)
        assertEquals(230, result.gratuityRatePerTaka)
        assertEquals(18000L * 230L, result.totalGratuityLumpSum)
        assertEquals(18000 + 1500, result.totalMonthlyPensionPayable)
    }

    @Test
    fun testArrearCalculatorRange() {
        val result = ArrearCalculatorEngine.calculate(
            ArrearInput(
                startDateIso = "2026-07-01",
                endDateIso = "2026-09-30",
                drawnBasic = 22000,
                dueBasic = 23100,
                tier = HouseRentTier.OTHER_DISTRICT,
                includeHouseRentDiff = true,
                gpfPercent = 10,
                deductGpfFromArrear = true
            )
        )
        assertEquals(3, result.rows.size)
        assertTrue(result.netArrearPayable > 0)
    }

    @Test
    fun testLeaveApplicationGeneration() {
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
        assertTrue(letterBn.contains("মোঃ নাসির উদ্দিন"))
    }
}
