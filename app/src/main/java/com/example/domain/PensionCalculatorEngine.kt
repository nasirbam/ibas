package com.example.domain

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

data class PensionInput(
    val lastBasicPay: Int = 45000,
    val qualifyingYears: Int = 25,
    val ageAtRetirement: Int = 59,
    val birthDateIso: String = "1972-07-15",
    val isFreedomFighter: Boolean = false
)

data class PensionResult(
    val pensionPercent: Int,
    val grossMonthlyPension: Int,
    val surrenderedHalfPension: Int,
    val gratuityRatePerTaka: Int,
    val totalGratuityLumpSum: Long,
    val netMonthlyPensionBase: Int,
    val medicalAllowance: Int,
    val totalMonthlyPensionPayable: Int,
    val festivalBonusEach: Int,
    val boishakhiAllowance: Int,
    val prlStartDateFormatted: String,
    val finalRetirementDateFormatted: String
)

object PensionCalculatorEngine {
    // Official BD Govt Pension Percentage Table (Finance Division SRO 2015)
    fun getPensionPercentage(years: Int): Int = when (years.coerceIn(0, 35)) {
        in 0..4 -> 0
        5 -> 21
        6 -> 24
        7 -> 27
        8 -> 30
        9 -> 33
        10 -> 36
        11 -> 39
        12 -> 43
        13 -> 47
        14 -> 51
        15 -> 54
        16 -> 57
        17 -> 60
        18 -> 63
        19 -> 66
        20 -> 70
        21 -> 74
        22 -> 78
        23 -> 82
        24 -> 86
        else -> 90 // 25+ years
    }

    // Official Gratuity Rate per 1 Taka surrendered (BD Govt Rules)
    fun getGratuityRate(years: Int): Int = when {
        years < 5 -> 0
        years in 5..9 -> 265
        years in 10..14 -> 260
        years in 15..19 -> 245
        else -> 230 // 20+ years
    }

    fun calculate(input: PensionInput): PensionResult {
        val percent = getPensionPercentage(input.qualifyingYears)
        val grossPension = (input.lastBasicPay * (percent / 100.0)).roundToInt()
        val surrendered = (grossPension * 0.50).roundToInt()
        val remainingBase = grossPension - surrendered
        val gratuityRate = getGratuityRate(input.qualifyingYears)
        val gratuityLumpSum = surrendered.toLong() * gratuityRate.toLong()

        val medical = if (input.ageAtRetirement >= 65) 2500 else 1500
        val totalMonthly = remainingBase + medical
        val festivalBonus = remainingBase // 100% of Net Pension twice a year
        val boishakhi = (remainingBase * 0.20).roundToInt()

        val (prlDate, retDate) = computePrlDates(input.birthDateIso, input.isFreedomFighter)

        return PensionResult(
            pensionPercent = percent,
            grossMonthlyPension = grossPension,
            surrenderedHalfPension = surrendered,
            gratuityRatePerTaka = gratuityRate,
            totalGratuityLumpSum = gratuityLumpSum,
            netMonthlyPensionBase = remainingBase,
            medicalAllowance = medical,
            totalMonthlyPensionPayable = totalMonthly,
            festivalBonusEach = festivalBonus,
            boishakhiAllowance = boishakhi,
            prlStartDateFormatted = prlDate,
            finalRetirementDateFormatted = retDate
        )
    }

    private fun computePrlDates(birthDateIso: String, isFreedomFighter: Boolean): Pair<String, String> {
        return try {
            val dob = LocalDate.parse(birthDateIso.trim())
            val retireAge = if (isFreedomFighter) 60L else 59L
            // PRL starts the day after completing age 59 (or 60 for FF)
            val prlStart = dob.plusYears(retireAge)
            val finalRetirement = prlStart.plusYears(1).minusDays(1)
            val fmt = DateTimeFormatter.ofPattern("dd MMM yyyy")
            prlStart.format(fmt) to finalRetirement.format(fmt)
        } catch (_: Exception) {
            "—" to "—"
        }
    }
}
