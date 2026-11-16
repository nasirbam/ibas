package com.example.domain

import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

data class ArrearInput(
    val startDateIso: String = "2026-07-01",
    val endDateIso: String = "2026-10-31",
    val drawnBasic: Int = 22000,
    val dueBasic: Int = 24260,
    val tier: HouseRentTier = HouseRentTier.OTHER_DISTRICT,
    val includeHouseRentDiff: Boolean = true,
    val gpfPercent: Int = 10,
    val deductGpfFromArrear: Boolean = true,
    val bonusMonthsCount: Int = 0 // 0, 1, or 2 Eid bonuses fallen in period
)

data class ArrearMonthRow(
    val monthLabel: String,
    val daysCount: Int,
    val totalDaysInMonth: Int,
    val drawnTotal: Int,
    val dueTotal: Int,
    val grossDiff: Int,
    val gpfDiff: Int,
    val netDiff: Int
)

data class ArrearResult(
    val rows: List<ArrearMonthRow>,
    val totalDrawn: Int,
    val totalDue: Int,
    val totalGrossArrear: Int,
    val bonusArrear: Int,
    val totalGpfDeduction: Int,
    val netArrearPayable: Int
)

object ArrearCalculatorEngine {
    fun calculate(input: ArrearInput): ArrearResult {
        val start = runCatching { LocalDate.parse(input.startDateIso.trim()) }
            .getOrElse { LocalDate.of(2026, 7, 1) }
        val end = runCatching { LocalDate.parse(input.endDateIso.trim()) }
            .getOrElse { LocalDate.of(2026, 10, 31) }

        if (end.isBefore(start)) {
            return ArrearResult(emptyList(), 0, 0, 0, 0, 0, 0)
        }

        val rows = mutableListOf<ArrearMonthRow>()
        val fmt = DateTimeFormatter.ofPattern("MMM yyyy")
        var ym = YearMonth.from(start)
        val endYm = YearMonth.from(end)

        var totalDrawn = 0
        var totalDue = 0
        var totalGrossDiff = 0
        var totalGpfDiff = 0

        while (!ym.isAfter(endYm)) {
            val daysInMonth = ym.lengthOfMonth()
            val firstDay = if (ym == YearMonth.from(start)) start.dayOfMonth else 1
            val lastDay = if (ym == endYm) end.dayOfMonth else daysInMonth
            val activeDays = (lastDay - firstDay + 1).coerceAtLeast(0)
            val fraction = activeDays.toDouble() / daysInMonth.toDouble()

            val drawnHra = if (input.includeHouseRentDiff) PayScale2015.calculateHouseRent(input.drawnBasic, input.tier) else 0
            val dueHra = if (input.includeHouseRentDiff) PayScale2015.calculateHouseRent(input.dueBasic, input.tier) else 0

            val monthDrawn = ((input.drawnBasic + drawnHra) * fraction).roundToInt()
            val monthDue = ((input.dueBasic + dueHra) * fraction).roundToInt()
            val grossDiff = (monthDue - monthDrawn).coerceAtLeast(0)

            val basicDiff = ((input.dueBasic - input.drawnBasic) * fraction).coerceAtLeast(0.0)
            val gpfDiff = if (input.deductGpfFromArrear) {
                (basicDiff * (input.gpfPercent / 100.0)).roundToInt()
            } else 0
            val netDiff = (grossDiff - gpfDiff).coerceAtLeast(0)

            totalDrawn += monthDrawn
            totalDue += monthDue
            totalGrossDiff += grossDiff
            totalGpfDiff += gpfDiff

            rows.add(
                ArrearMonthRow(
                    monthLabel = ym.format(fmt),
                    daysCount = activeDays,
                    totalDaysInMonth = daysInMonth,
                    drawnTotal = monthDrawn,
                    dueTotal = monthDue,
                    grossDiff = grossDiff,
                    gpfDiff = gpfDiff,
                    netDiff = netDiff
                )
            )
            ym = ym.plusMonths(1)
        }

        val bonusArrear = ((input.dueBasic - input.drawnBasic).coerceAtLeast(0)) * input.bonusMonthsCount.coerceIn(0, 2)
        val totalGrossWithBonus = totalGrossDiff + bonusArrear
        val netPayable = (totalGrossWithBonus - totalGpfDiff).coerceAtLeast(0)

        return ArrearResult(
            rows = rows,
            totalDrawn = totalDrawn,
            totalDue = totalDue,
            totalGrossArrear = totalGrossWithBonus,
            bonusArrear = bonusArrear,
            totalGpfDeduction = totalGpfDiff,
            netArrearPayable = netPayable
        )
    }
}
