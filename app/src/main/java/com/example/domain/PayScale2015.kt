package com.example.domain

import kotlin.math.roundToInt

enum class PayScalePhase(
    val titleBn: String,
    val titleEn: String,
    val shortBn: String,
    val shortEn: String
) {
    PHASE_1_2026(
        "ফেজ-১: জুলাই–ডিসেম্বর ২০২৬ (৪০%/৫০%)",
        "Phase 1: Jul–Dec 2026 (40% / 50%)",
        "২০২৬ ফেজ-১",
        "2026 Phase 1"
    ),
    PHASE_2_2027(
        "ফেজ-২: জানুয়ারি–জুন ২০২৭ (৭০%/৭৫%)",
        "Phase 2: Jan–Jun 2027 (70% / 75%)",
        "২০২৭ ফেজ-২",
        "2027 Phase 2"
    ),
    FULL_2026(
        "পূর্ণ ২০২৬ পে-স্কেল (১০০%)",
        "Full 2026 Pay Scale (100%)",
        "পূর্ণ ২০২৬ (১০০%)",
        "Full 2026 (100%)"
    ),
    OLD_2015(
        "পূর্বের ২০১৫ পে-স্কেল",
        "Previous 2015 Pay Scale",
        "২০১৫ স্কেল",
        "2015 Scale"
    )
}

enum class HouseRentTier(
    val titleBn: String,
    val titleEn: String,
    val shortBn: String,
    val shortEn: String
) {
    DHAKA_METRO(
        "ঢাকা মেট্রোপলিটন এলাকা",
        "Dhaka Metropolitan Area",
        "ঢাকা মেট্রো",
        "Dhaka Metro"
    ),
    DIVISIONAL_CITY(
        "চট্টগ্রাম/খুলনা/রাজশাহী/সিলেট/বরিশাল/রংপুর/ময়মনসিংহ/নারায়ণগঞ্জ/গাজীপুর",
        "Divisional Cities, Narayanganj & Gazipur",
        "বিভাগীয়/বড় শহর",
        "Major Cities"
    ),
    OTHER_DISTRICT(
        "অন্যান্য জেলা ও উপজেলা এলাকা",
        "Other Districts & Upazilas",
        "অন্যান্য এলাকা",
        "Other Areas"
    )
}

enum class BillType(val titleBn: String, val titleEn: String) {
    MONTHLY_SALARY("মাসিক বেতন বিল", "Monthly Salary Bill"),
    EID_BONUS("উৎসব ভাতা (ঈদ/পূজা)", "Festival Bonus Bill"),
    BOISHAKHI("বাংলা নববর্ষ ভাতা (২০%)", "Boishakhi Allowance (20%)"),
    RRL_ALLOWANCE("শ্রান্তি ও বিনোদন ভাতা", "Recreation Allowance (RRL)")
}

data class PayGrade(
    val grade: Int,
    val scaleText2026: String,
    val scaleText: String, // 2015 scale text
    val minBasic2026: Int,
    val maxBasic2026: Int,
    val minBasic: Int, // 2015 min basic
    val maxBasic: Int, // 2015 max basic
    val incrementPercent: Double,
    val steps2026: List<Int>,
    val steps: List<Int> // 2015 steps
)

object PayScale2015 {
    // Official Bangladesh National Pay Scale 2026 (S.R.O. No. 347-Law/2026) & 2015 Reference
    val grades: List<PayGrade> = listOf(
        PayGrade(1, "১,৫৬,০০০ (নির্ধারিত)", "৭৮,০০০ (নির্ধারিত)", 156000, 156000, 78000, 78000, 0.0, listOf(156000), listOf(78000)),
        PayGrade(2, "১,৩২,০০০ - ১,৫৩,০০০", "৬৬,০০০ - ৭৬,৪৯০", 132000, 153000, 66000, 76490, 3.75, listOf(132000, 136950, 142090, 147420, 153000), listOf(66000, 68480, 71050, 73720, 76490)),
        PayGrade(3, "১,১৩,০০০ - ১,৪৮,৮০০", "৫৬,৫০০ - ৭৪,৪০০", 113000, 148800, 56500, 74400, 4.0, generateSteps(113000, 148800, 0.04), listOf(56500, 58760, 61120, 63570, 66120, 68770, 71530, 74400)),
        PayGrade(4, "১,০০,০০০ - ১,৪২,৪০০", "৫০,০০০ - ৭১,২০০", 100000, 142400, 50000, 71200, 4.0, generateSteps(100000, 142400, 0.04), listOf(50000, 52000, 54080, 56250, 58500, 60840, 63280, 65820, 68460, 71200)),
        PayGrade(5, "৮৬,০০০ - ১,৩৯,৭০০", "৪৩,০০০ - ৬৯,৮৫০", 86000, 139700, 43000, 69850, 4.5, generateSteps(86000, 139700, 0.045), listOf(43000, 44940, 46970, 49090, 51300, 53610, 56030, 58560, 61200, 63960, 66840, 69850)),
        PayGrade(6, "৭১,০০০ - ১,৩৪,০০০", "৩৫,৫০০ - ৬৭,০১০", 71000, 134000, 35500, 67010, 4.5, generateSteps(71000, 134000, 0.045), listOf(35500, 37100, 38770, 40520, 42350, 44260, 46260, 48350, 50530, 52810, 55190, 57680, 60280, 63000, 65840, 67010)),
        PayGrade(7, "৫৮,০০০ - ১,২৬,৮০০", "২৯,০০০ - ৬৩,৪১০", 58000, 126800, 29000, 63410, 5.0, generateSteps(58000, 126800, 0.05), generateSteps(29000, 63410, 0.05)),
        PayGrade(8, "৪৬,০০০ - ১,১১,০০০", "২৩,০০০ - ৫৫,৪৭০", 46000, 111000, 23000, 55470, 5.0, generateSteps(46000, 111000, 0.05), generateSteps(23000, 55470, 0.05)),
        PayGrade(9, "৪৪,০০০ - ১,০৬,২০০", "২২,০০০ - ৫৩,০৬০", 44000, 106200, 22000, 53060, 5.0, generateSteps(44000, 106200, 0.05), generateSteps(22000, 53060, 0.05)),
        PayGrade(10, "৩২,০০০ - ৭৭,৩০০", "১৬,০০০ - ৩৮,৬৪০", 32000, 77300, 16000, 38640, 5.0, generateSteps(32000, 77300, 0.05), generateSteps(16000, 38640, 0.05)),
        PayGrade(11, "২৫,০০০ - ৬০,৫০০", "১২,৫০০ - ৩০,২৩০", 25000, 60500, 12500, 30230, 5.0, generateSteps(25000, 60500, 0.05), generateSteps(12500, 30230, 0.05)),
        PayGrade(12, "২৪,৫০০ - ৫৯,২০০", "১১,৩০০ - ২৭,৩০০", 24500, 59200, 11300, 27300, 5.0, generateSteps(24500, 59200, 0.05), generateSteps(11300, 27300, 0.05)),
        PayGrade(13, "২৪,০০০ - ৫৮,০০০", "১১,০০০ - ২৬,৫৯০", 24000, 58000, 11000, 26590, 5.0, generateSteps(24000, 58000, 0.05), generateSteps(11000, 26590, 0.05)),
        PayGrade(14, "২৩,৫০০ - ৫৬,৮০০", "১০,২০০ - ২৪,৬৮০", 23500, 56800, 10200, 24680, 5.0, generateSteps(23500, 56800, 0.05), generateSteps(10200, 24680, 0.05)),
        PayGrade(15, "২২,৮০০ - ৫৫,২০০", "৯,৭০০ - ২৩,৪৯০", 22800, 55200, 9700, 23490, 5.0, generateSteps(22800, 55200, 0.05), generateSteps(9700, 23490, 0.05)),
        PayGrade(16, "২১,৯০০ - ৫২,৯০০", "৯,৩০০ - ২২,৪৯০", 21900, 52900, 9300, 22490, 5.0, generateSteps(21900, 52900, 0.05), generateSteps(9300, 22490, 0.05)),
        PayGrade(17, "২১,৪০০ - ৫১,৯০০", "৯,০০০ - ২১,৮০০", 21400, 51900, 9000, 21800, 5.0, generateSteps(21400, 51900, 0.05), generateSteps(9000, 21800, 0.05)),
        PayGrade(18, "২১,০০০ - ৫০,৯০০", "৮,৮০০ - ২১,৩১০", 21000, 50900, 8800, 21310, 5.0, generateSteps(21000, 50900, 0.05), generateSteps(8800, 21310, 0.05)),
        PayGrade(19, "২০,৫০০ - ৪৯,৬০০", "৮,৫০০ - ২০,৫৭০", 20500, 49600, 8500, 20570, 5.0, generateSteps(20500, 49600, 0.05), generateSteps(8500, 20570, 0.05)),
        PayGrade(20, "২০,০০০ - ৪৮,৪০০", "৮,২৫০ - ২০,০১০", 20000, 48400, 8250, 20010, 5.0, generateSteps(20000, 48400, 0.05), generateSteps(8250, 20010, 0.05))
    )

    private fun generateSteps(start: Int, max: Int, rate: Double): List<Int> {
        val list = mutableListOf(start)
        var current = start
        for (i in 1..20) {
            val rawNext = current * (1.0 + rate)
            val next = (((rawNext + 9.0) / 10.0).toInt() * 10).coerceAtMost(max)
            if (next <= current) break
            list.add(next)
            current = next
            if (current >= max) break
        }
        return list
    }

    fun getGrade(gradeNumber: Int): PayGrade =
        grades.find { it.grade == gradeNumber } ?: grades[8]

    /**
     * Computes payable basic pay automatically based on Grade, Step index (0-based), and 2026 Implementation Phase.
     */
    fun getPayableBasicForStep(gradeNumber: Int, stepIndex: Int, phase: PayScalePhase): Int {
        val g = getGrade(gradeNumber)
        val oldBasic = g.steps.getOrElse(stepIndex) { g.steps.last() }
        val newBasic = g.steps2026.getOrElse(stepIndex) { g.steps2026.last() }
        return computePhasedBasic(gradeNumber, oldBasic, newBasic, phase)
    }

    fun computePhasedBasic(gradeNumber: Int, oldBasic2015: Int, newBasic2026: Int, phase: PayScalePhase): Int {
        val diff = (newBasic2026 - oldBasic2015).coerceAtLeast(0)
        return when (phase) {
            PayScalePhase.OLD_2015 -> oldBasic2015
            PayScalePhase.PHASE_1_2026 -> {
                val ratio = if (gradeNumber <= 9) 0.40 else 0.50
                oldBasic2015 + (diff * ratio).roundToInt()
            }
            PayScalePhase.PHASE_2_2027 -> {
                val ratio = if (gradeNumber <= 9) 0.70 else 0.75
                oldBasic2015 + (diff * ratio).roundToInt()
            }
            PayScalePhase.FULL_2026 -> newBasic2026
        }
    }

    fun calculateHouseRent(basicPay: Int, tier: HouseRentTier): Int {
        if (basicPay <= 0) return 0
        return when {
            basicPay <= 9700 -> when (tier) {
                HouseRentTier.DHAKA_METRO -> maxOf((basicPay * 0.65).roundToInt(), 5600)
                HouseRentTier.DIVISIONAL_CITY -> maxOf((basicPay * 0.55).roundToInt(), 5000)
                HouseRentTier.OTHER_DISTRICT -> maxOf((basicPay * 0.50).roundToInt(), 4500)
            }
            basicPay <= 16000 -> when (tier) {
                HouseRentTier.DHAKA_METRO -> maxOf((basicPay * 0.60).roundToInt(), 6400)
                HouseRentTier.DIVISIONAL_CITY -> maxOf((basicPay * 0.50).roundToInt(), 5400)
                HouseRentTier.OTHER_DISTRICT -> maxOf((basicPay * 0.45).roundToInt(), 4800)
            }
            basicPay <= 35500 -> when (tier) {
                HouseRentTier.DHAKA_METRO -> maxOf((basicPay * 0.55).roundToInt(), 9600)
                HouseRentTier.DIVISIONAL_CITY -> maxOf((basicPay * 0.45).roundToInt(), 8000)
                HouseRentTier.OTHER_DISTRICT -> maxOf((basicPay * 0.40).roundToInt(), 7200)
            }
            else -> when (tier) {
                HouseRentTier.DHAKA_METRO -> maxOf((basicPay * 0.50).roundToInt(), 19500)
                HouseRentTier.DIVISIONAL_CITY -> maxOf((basicPay * 0.40).roundToInt(), 16000)
                HouseRentTier.OTHER_DISTRICT -> maxOf((basicPay * 0.35).roundToInt(), 14200)
            }
        }
    }

    /**
     * Under 2026 Pay Order: Medical allowance is 3,000 Tk (up to age 50) and 4,000 Tk (age 50+).
     * Under 2015 / interim: 1,500 Tk.
     */
    fun calculateMedicalAllowance(phase: PayScalePhase, isAge50Plus: Boolean): Int {
        return if (phase == PayScalePhase.OLD_2015) {
            1500
        } else {
            if (isAge50Plus) 4000 else 3000
        }
    }

    fun calculateSpecialBenefit(basicPay: Int, grade: Int): Int {
        if (basicPay <= 0) return 0
        val fivePercent = (basicPay * 0.05).roundToInt()
        val floor = if (grade >= 10) 1500 else 1000
        return maxOf(fivePercent, floor)
    }

    fun calculateBenevolentFund(basicPay: Int): Int =
        minOf((basicPay * 0.01).roundToInt(), 150)

    fun calculateGroupInsurance(grade: Int): Int =
        if (grade <= 9) 100 else 40
}

data class PayBillInput(
    val grade: Int = 9,
    val stepIndex: Int = 0,
    val phase: PayScalePhase = PayScalePhase.PHASE_1_2026,
    val basicPay: Int = PayScale2015.getPayableBasicForStep(9, 0, PayScalePhase.PHASE_1_2026),
    val tier: HouseRentTier = HouseRentTier.OTHER_DISTRICT,
    val billType: BillType = BillType.MONTHLY_SALARY,
    val childrenCount: Int = 1,
    val isAge50Plus: Boolean = false,
    val includeSpecialBenefit: Boolean = false, // Absorbed into 2026 Pay Scale; toggleable for 2015
    val chargeAllowance: Int = 0,
    val conveyanceAllowance: Int = 0,
    val otherAllowance: Int = 0,
    val gpfPercent: Int = 10,
    val gpfLoanInstallment: Int = 0,
    val livesInGovtQuarter: Boolean = false,
    val incomeTax: Int = 0,
    val otherDeduction: Int = 0
)

data class PayBillResult(
    val basicPay: Int,
    val houseRentAllowance: Int,
    val medicalAllowance: Int,
    val educationAllowance: Int,
    val tiffinAllowance: Int,
    val washingAllowance: Int,
    val specialBenefit: Int,
    val chargeAllowance: Int,
    val conveyanceAllowance: Int,
    val otherAllowance: Int,
    val grossPay: Int,
    val gpfSubscription: Int,
    val gpfLoanInstallment: Int,
    val benevolentFund: Int,
    val groupInsurance: Int,
    val govtQuarterDeduction: Int,
    val incomeTax: Int,
    val otherDeduction: Int,
    val totalDeduction: Int,
    val netPayable: Int
)

object PayBillCalculator {
    fun calculate(input: PayBillInput): PayBillResult {
        return when (input.billType) {
            BillType.EID_BONUS, BillType.RRL_ALLOWANCE -> {
                val amt = input.basicPay
                PayBillResult(
                    basicPay = amt, houseRentAllowance = 0, medicalAllowance = 0,
                    educationAllowance = 0, tiffinAllowance = 0, washingAllowance = 0,
                    specialBenefit = 0, chargeAllowance = 0, conveyanceAllowance = 0,
                    otherAllowance = 0, grossPay = amt, gpfSubscription = 0,
                    gpfLoanInstallment = 0, benevolentFund = 0, groupInsurance = 0,
                    govtQuarterDeduction = 0, incomeTax = 0, otherDeduction = 0,
                    totalDeduction = 0, netPayable = amt
                )
            }
            BillType.BOISHAKHI -> {
                val boishakhi = (input.basicPay * 0.20).roundToInt()
                PayBillResult(
                    basicPay = boishakhi, houseRentAllowance = 0, medicalAllowance = 0,
                    educationAllowance = 0, tiffinAllowance = 0, washingAllowance = 0,
                    specialBenefit = 0, chargeAllowance = 0, conveyanceAllowance = 0,
                    otherAllowance = 0, grossPay = boishakhi, gpfSubscription = 0,
                    gpfLoanInstallment = 0, benevolentFund = 0, groupInsurance = 0,
                    govtQuarterDeduction = 0, incomeTax = 0, otherDeduction = 0,
                    totalDeduction = 0, netPayable = boishakhi
                )
            }
            BillType.MONTHLY_SALARY -> {
                // Under 2026 interim transition, HRA references June 30, 2026 basic or current basic
                val g = PayScale2015.getGrade(input.grade)
                val hraBase = if (input.phase == PayScalePhase.PHASE_1_2026 || input.phase == PayScalePhase.PHASE_2_2027) {
                    g.steps.getOrElse(input.stepIndex) { input.basicPay }
                } else {
                    input.basicPay
                }
                val hra = if (input.livesInGovtQuarter) 0 else PayScale2015.calculateHouseRent(hraBase, input.tier)
                val medical = PayScale2015.calculateMedicalAllowance(input.phase, input.isAge50Plus)
                val education = input.childrenCount.coerceIn(0, 2) * 500
                val tiffin = if (input.grade >= 11) 200 else 0
                val washing = if (input.grade >= 17) 100 else 0
                val specialBenefit = if (input.includeSpecialBenefit) {
                    PayScale2015.calculateSpecialBenefit(input.basicPay, input.grade)
                } else 0

                val gross = input.basicPay + hra + medical + education + tiffin + washing +
                    specialBenefit + input.chargeAllowance + input.conveyanceAllowance + input.otherAllowance

                val gpfSub = (input.basicPay * (input.gpfPercent / 100.0)).roundToInt()
                val benevolent = PayScale2015.calculateBenevolentFund(input.basicPay)
                val groupIns = PayScale2015.calculateGroupInsurance(input.grade)
                val quarterDed = if (input.livesInGovtQuarter) (input.basicPay * 0.075).roundToInt() else 0

                val totalDed = gpfSub + input.gpfLoanInstallment + benevolent + groupIns +
                    quarterDed + input.incomeTax + input.otherDeduction
                val net = (gross - totalDed).coerceAtLeast(0)

                PayBillResult(
                    basicPay = input.basicPay,
                    houseRentAllowance = hra,
                    medicalAllowance = medical,
                    educationAllowance = education,
                    tiffinAllowance = tiffin,
                    washingAllowance = washing,
                    specialBenefit = specialBenefit,
                    chargeAllowance = input.chargeAllowance,
                    conveyanceAllowance = input.conveyanceAllowance,
                    otherAllowance = input.otherAllowance,
                    grossPay = gross,
                    gpfSubscription = gpfSub,
                    gpfLoanInstallment = input.gpfLoanInstallment,
                    benevolentFund = benevolent,
                    groupInsurance = groupIns,
                    govtQuarterDeduction = quarterDed,
                    incomeTax = input.incomeTax,
                    otherDeduction = input.otherDeduction,
                    totalDeduction = totalDed,
                    netPayable = net
                )
            }
        }
    }

    fun amountInWordsEn(amount: Long): String {
        if (amount == 0L) return "Zero Taka Only"
        val units = arrayOf(
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
            "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
            "Seventeen", "Eighteen", "Nineteen"
        )
        val tens = arrayOf("", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety")

        fun twoDigits(n: Int): String = when {
            n < 20 -> units[n]
            else -> tens[n / 10] + if (n % 10 != 0) " ${units[n % 10]}" else ""
        }

        val sb = StringBuilder()
        var rem = amount
        val crore = (rem / 10_000_000).toInt()
        rem %= 10_000_000
        val lakh = (rem / 100_000).toInt()
        rem %= 100_000
        val thousand = (rem / 1_000).toInt()
        rem %= 1_000
        val hundred = (rem / 100).toInt()
        val last = (rem % 100).toInt()

        if (crore > 0) sb.append("${twoDigits(crore)} Crore ")
        if (lakh > 0) sb.append("${twoDigits(lakh)} Lakh ")
        if (thousand > 0) sb.append("${twoDigits(thousand)} Thousand ")
        if (hundred > 0) sb.append("${units[hundred]} Hundred ")
        if (last > 0) sb.append("${twoDigits(last)} ")

        return "${sb.toString().trim()} Taka Only"
    }
}
