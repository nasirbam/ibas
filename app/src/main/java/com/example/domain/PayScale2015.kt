package com.example.domain

import kotlin.math.roundToInt

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
    val scaleText: String,
    val minBasic: Int,
    val maxBasic: Int,
    val incrementPercent: Double,
    val steps: List<Int>
)

object PayScale2015 {
    // Official National Pay Scale 2015 (Bangladesh Gazette)
    val grades: List<PayGrade> = listOf(
        PayGrade(1, "৭৮,০০০ (নির্ধারিত)", 78000, 78000, 0.0, listOf(78000)),
        PayGrade(2, "৬৬,০০০ - ৭৬,৪৯০", 66000, 76490, 3.75, listOf(66000, 68480, 71050, 73720, 76490)),
        PayGrade(3, "৫৬,৫০০ - ৭৪,৪০০", 56500, 74400, 4.0, listOf(56500, 58760, 61120, 63570, 66120, 68770, 71530, 74400)),
        PayGrade(4, "৫০,০০০ - ৭১,২০০", 50000, 71200, 4.0, listOf(50000, 52000, 54080, 56250, 58500, 60840, 63280, 65820, 68460, 71200)),
        PayGrade(5, "৪৩,০০০ - ৬৯,৮৫০", 43000, 69850, 4.5, listOf(43000, 44940, 46970, 49090, 51300, 53610, 56030, 58560, 61200, 63960, 66840, 69850)),
        PayGrade(6, "৩৫,৫০০ - ৬৭,০১০", 35500, 67010, 4.5, listOf(35500, 37100, 38770, 40520, 42350, 44260, 46260, 48350, 50530, 52810, 55190, 57680, 60280, 63000, 65840, 67010)),
        PayGrade(7, "২৯,০০০ - ৬৩,৪১০", 29000, 63410, 5.0, generateSteps(29000, 63410, 0.05)),
        PayGrade(8, "২৩,০০০ - ৫৫,৪৭০", 23000, 55470, 5.0, generateSteps(23000, 55470, 0.05)),
        PayGrade(9, "২২,০০০ - ৫৩,০৬০", 22000, 53060, 5.0, generateSteps(22000, 53060, 0.05)),
        PayGrade(10, "১৬,০০০ - ৩৮,৬৪০", 16000, 38640, 5.0, generateSteps(16000, 38640, 0.05)),
        PayGrade(11, "১২,৫০০ - ৩০,২৩০", 12500, 30230, 5.0, generateSteps(12500, 30230, 0.05)),
        PayGrade(12, "১১,৩০০ - ২৭,৩০০", 11300, 27300, 5.0, generateSteps(11300, 27300, 0.05)),
        PayGrade(13, "১১,০০০ - ২৬,৫৯০", 11000, 26590, 5.0, generateSteps(11000, 26590, 0.05)),
        PayGrade(14, "১০,২০০ - ২৪,৬৮০", 10200, 24680, 5.0, generateSteps(10200, 24680, 0.05)),
        PayGrade(15, "৯,৭০০ - ২৩,৪৯০", 9700, 23490, 5.0, generateSteps(9700, 23490, 0.05)),
        PayGrade(16, "৯,৩০০ - ২২,৪৯০", 9300, 22490, 5.0, generateSteps(9300, 22490, 0.05)),
        PayGrade(17, "৯,০০০ - ২১,৮০০", 9000, 21800, 5.0, generateSteps(9000, 21800, 0.05)),
        PayGrade(18, "৮,৮০০ - ২১,৩১০", 8800, 21310, 5.0, generateSteps(8800, 21310, 0.05)),
        PayGrade(19, "৮,৫০০ - ২০,৫৭০", 8500, 20570, 5.0, generateSteps(8500, 20570, 0.05)),
        PayGrade(20, "৮,২৫০ - ২০,০১০", 8250, 20010, 5.0, generateSteps(8250, 20010, 0.05))
    )

    private fun generateSteps(start: Int, max: Int, rate: Double): List<Int> {
        val list = mutableListOf(start)
        var current = start
        for (i in 1..20) {
            // Round up to nearest 10 taka as per Pay Scale 2015 rules
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
        grades.find { it.grade == gradeNumber } ?: grades[8] // Default Grade 9

    /**
     * Official Finance Division House Rent Allowance (বাড়ি ভাড়া ভাতা) slab rules
     */
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
     * Special Benefit (বিশেষ সুবিধা) - 5% of Basic Pay, minimum 1000 Tk (or 1500 Tk for Grade 10-20)
     */
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
    val basicPay: Int = 22000,
    val tier: HouseRentTier = HouseRentTier.OTHER_DISTRICT,
    val billType: BillType = BillType.MONTHLY_SALARY,
    val childrenCount: Int = 1, // Max 2 children @ 500 = 1000
    val includeSpecialBenefit: Boolean = true,
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
            BillType.EID_BONUS -> {
                val bonus = input.basicPay
                PayBillResult(
                    basicPay = bonus,
                    houseRentAllowance = 0,
                    medicalAllowance = 0,
                    educationAllowance = 0,
                    tiffinAllowance = 0,
                    washingAllowance = 0,
                    specialBenefit = 0,
                    chargeAllowance = 0,
                    conveyanceAllowance = 0,
                    otherAllowance = 0,
                    grossPay = bonus,
                    gpfSubscription = 0,
                    gpfLoanInstallment = 0,
                    benevolentFund = 0,
                    groupInsurance = 0,
                    govtQuarterDeduction = 0,
                    incomeTax = 0,
                    otherDeduction = 0,
                    totalDeduction = 0,
                    netPayable = bonus
                )
            }
            BillType.BOISHAKHI -> {
                val boishakhi = (input.basicPay * 0.20).roundToInt()
                PayBillResult(
                    basicPay = boishakhi,
                    houseRentAllowance = 0,
                    medicalAllowance = 0,
                    educationAllowance = 0,
                    tiffinAllowance = 0,
                    washingAllowance = 0,
                    specialBenefit = 0,
                    chargeAllowance = 0,
                    conveyanceAllowance = 0,
                    otherAllowance = 0,
                    grossPay = boishakhi,
                    gpfSubscription = 0,
                    gpfLoanInstallment = 0,
                    benevolentFund = 0,
                    groupInsurance = 0,
                    govtQuarterDeduction = 0,
                    incomeTax = 0,
                    otherDeduction = 0,
                    totalDeduction = 0,
                    netPayable = boishakhi
                )
            }
            BillType.RRL_ALLOWANCE -> {
                val rrl = input.basicPay
                PayBillResult(
                    basicPay = rrl,
                    houseRentAllowance = 0,
                    medicalAllowance = 0,
                    educationAllowance = 0,
                    tiffinAllowance = 0,
                    washingAllowance = 0,
                    specialBenefit = 0,
                    chargeAllowance = 0,
                    conveyanceAllowance = 0,
                    otherAllowance = 0,
                    grossPay = rrl,
                    gpfSubscription = 0,
                    gpfLoanInstallment = 0,
                    benevolentFund = 0,
                    groupInsurance = 0,
                    govtQuarterDeduction = 0,
                    incomeTax = 0,
                    otherDeduction = 0,
                    totalDeduction = 0,
                    netPayable = rrl
                )
            }
            BillType.MONTHLY_SALARY -> {
                val hra = if (input.livesInGovtQuarter) 0 else PayScale2015.calculateHouseRent(input.basicPay, input.tier)
                val medical = 1500
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
