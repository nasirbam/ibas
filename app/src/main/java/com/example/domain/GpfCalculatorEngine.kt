package com.example.domain

import kotlin.math.roundToLong

enum class GpfInterestMode(val titleBn: String, val titleEn: String) {
    BD_GOVT_SLAB("সরকারি স্ল্যাব (১৩%/১২%/১১%)", "Govt Slab (13% / 12% / 11%)"),
    CUSTOM_FLAT("কাস্টম সরল হার (%)", "Custom Flat Rate (%)")
}

data class GpfInput(
    val openingBalance: Double = 450000.0,
    val monthlySubscription: Double = 3000.0,
    val monthlyLoanRecovery: Double = 0.0,
    val withdrawalAmount: Double = 0.0,
    val withdrawalMonthIndex: Int = 5, // 0=July .. 11=June
    val interestMode: GpfInterestMode = GpfInterestMode.BD_GOVT_SLAB,
    val customRatePercent: Double = 13.0
)

data class GpfMonthlyRow(
    val monthIndex: Int, // 0 = July .. 11 = June
    val monthNameBn: String,
    val monthNameEn: String,
    val subscription: Double,
    val recovery: Double,
    val withdrawal: Double,
    val progressiveBalance: Double,
    val monthlyInterestEarned: Double
)

data class GpfAnnualResult(
    val rows: List<GpfMonthlyRow>,
    val totalSubscription: Double,
    val totalRecovery: Double,
    val totalWithdrawal: Double,
    val effectiveRatePercent: Double,
    val totalAnnualInterest: Double,
    val closingBalance: Double,
    val maxEligibleAdvance80Percent: Double
)

object GpfCalculatorEngine {
    private val fiscalMonthsBn = listOf(
        "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর",
        "জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন"
    )
    private val fiscalMonthsEn = listOf(
        "July", "August", "September", "October", "November", "December",
        "January", "February", "March", "April", "May", "June"
    )

    /**
     * Bangladesh Finance Division GPF Interest Slab Rules:
     * Up to 15,00,000 -> 13%
     * 15,00,001 to 30,00,000 -> 12%
     * Above 30,00,000 -> 11%
     */
    fun calculateSlabAnnualInterest(productSum: Double, closingBeforeInterest: Double): Pair<Double, Double> {
        val avgMonthlyBalance = productSum / 12.0
        return when {
            closingBeforeInterest <= 1_500_000.0 -> {
                13.0 to (avgMonthlyBalance * 0.13)
            }
            closingBeforeInterest <= 3_000_000.0 -> {
                // Slab calculation: 13% on first 15L, 12% on remainder
                val tier1 = 1_500_000.0 * 0.13
                val tier2 = (avgMonthlyBalance - 1_500_000.0).coerceAtLeast(0.0) * 0.12
                val effective = if (avgMonthlyBalance > 0) ((tier1 + tier2) / avgMonthlyBalance) * 100.0 else 12.5
                effective to (tier1 + tier2)
            }
            else -> {
                val tier1 = 1_500_000.0 * 0.13
                val tier2 = 1_500_000.0 * 0.12
                val tier3 = (avgMonthlyBalance - 3_000_000.0).coerceAtLeast(0.0) * 0.11
                val effective = if (avgMonthlyBalance > 0) ((tier1 + tier2 + tier3) / avgMonthlyBalance) * 100.0 else 11.5
                effective to (tier1 + tier2 + tier3)
            }
        }
    }

    fun calculateFiscalYear(input: GpfInput): GpfAnnualResult {
        var runningBalance = input.openingBalance
        var productSum = 0.0
        var totalSub = 0.0
        var totalRec = 0.0
        var totalWith = 0.0
        val provisionalRows = mutableListOf<GpfMonthlyRow>()

        // Standard BD Govt GPF rule: monthly deposits earn interest from the following month,
        // or progressive balance sum / 12. Using standard iBAS++ monthly product method:
        // Balance before current month's deposit vs after; standard iBAS computes on monthly closing
        // excluding current month subscription (i.e. 11/12/10... months), which equals progressive sum.
        for (i in 0 until 12) {
            val withdrawal = if (i == input.withdrawalMonthIndex) input.withdrawalAmount else 0.0
            runningBalance = (runningBalance + input.monthlySubscription + input.monthlyLoanRecovery - withdrawal)
                .coerceAtLeast(0.0)
            totalSub += input.monthlySubscription
            totalRec += input.monthlyLoanRecovery
            totalWith += withdrawal

            // Opening balance earns 12 months; July deposit earns 11 months, etc.
            val interestBearingBalance = (runningBalance - input.monthlySubscription - input.monthlyLoanRecovery)
                .coerceAtLeast(0.0)
            productSum += interestBearingBalance

            provisionalRows.add(
                GpfMonthlyRow(
                    monthIndex = i,
                    monthNameBn = fiscalMonthsBn[i],
                    monthNameEn = fiscalMonthsEn[i],
                    subscription = input.monthlySubscription,
                    recovery = input.monthlyLoanRecovery,
                    withdrawal = withdrawal,
                    progressiveBalance = runningBalance,
                    monthlyInterestEarned = 0.0
                )
            )
        }

        val (effectiveRate, annualInterestRaw) = if (input.interestMode == GpfInterestMode.BD_GOVT_SLAB) {
            calculateSlabAnnualInterest(productSum, runningBalance)
        } else {
            val rate = input.customRatePercent
            rate to ((productSum / 12.0) * (rate / 100.0))
        }

        val annualInterest = annualInterestRaw.roundToLong().toDouble()
        val finalRows = provisionalRows.map { row ->
            val monthShare = if (productSum > 0) {
                val base = (row.progressiveBalance - row.subscription - row.recovery).coerceAtLeast(0.0)
                (base / productSum) * annualInterest
            } else 0.0
            row.copy(monthlyInterestEarned = monthShare)
        }

        val closing = (runningBalance + annualInterest).roundToLong().toDouble()
        return GpfAnnualResult(
            rows = finalRows,
            totalSubscription = totalSub,
            totalRecovery = totalRec,
            totalWithdrawal = totalWith,
            effectiveRatePercent = effectiveRate,
            totalAnnualInterest = annualInterest,
            closingBalance = closing,
            maxEligibleAdvance80Percent = (closing * 0.80).roundToLong().toDouble()
        )
    }

    fun calculateAdvanceEmi(advanceAmount: Double, installments: Int): Double {
        if (installments <= 0 || advanceAmount <= 0) return 0.0
        return (advanceAmount / installments).roundToLong().toDouble()
    }
}
