package com.artha.domain

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import kotlin.math.pow

data class AmortizationRow(
    val monthIndex: Int,
    val openingBalanceMinor: Long,
    val emiMinor: Long,
    val principalComponentMinor: Long,
    val interestComponentMinor: Long,
    val closingBalanceMinor: Long
)

data class PrepaymentSimulationResult(
    val originalTotalInterestMinor: Long,
    val revisedTotalInterestMinor: Long,
    val interestSavedMinor: Long,
    val originalTenureMonths: Int,
    val revisedTenureMonths: Int,
    val monthsSaved: Int
)

class EmiCalculator {

    companion object {
        private val INSTANCE = EmiCalculator()

        fun calculateMonthlyEmi(
            principalMinor: Long,
            annualInterestRatePercent: Double,
            tenureMonths: Int
        ): Long = INSTANCE.calculateMonthlyEmi(principalMinor, annualInterestRatePercent, tenureMonths)
    }

    private val mc = MathContext(16, RoundingMode.HALF_EVEN)

    /**
     * Calculate monthly EMI for reducing-balance loan using exact mathematical formula:
     * EMI = [P * r * (1+r)^n] / [(1+r)^n - 1]
     * where:
     * - P = principal in minor units
     * - r = monthly interest rate (annualRatePercent / 12 / 100)
     * - n = tenure in months
     */
    fun calculateMonthlyEmi(
        principalMinor: Long,
        annualInterestRatePercent: Double,
        tenureMonths: Int
    ): Long {
        if (principalMinor <= 0L || tenureMonths <= 0) return 0L
        if (annualInterestRatePercent <= 0.0) {
            // Zero interest loan: EMI is straight principal / tenure
            return principalMinor / tenureMonths
        }

        val monthlyRate = annualInterestRatePercent / (12.0 * 100.0)
        val factor = (1.0 + monthlyRate).pow(tenureMonths.toDouble())
        val emi = (principalMinor.toDouble() * monthlyRate * factor) / (factor - 1.0)

        return BigDecimal.valueOf(emi)
            .setScale(0, RoundingMode.HALF_EVEN)
            .longValueExact()
    }

    /**
     * Generate month-by-month amortization schedule.
     */
    fun generateAmortizationSchedule(
        principalMinor: Long,
        annualInterestRatePercent: Double,
        tenureMonths: Int
    ): List<AmortizationRow> {
        if (principalMinor <= 0L || tenureMonths <= 0) return emptyList()

        val emiMinor = calculateMonthlyEmi(principalMinor, annualInterestRatePercent, tenureMonths)
        val monthlyRate = annualInterestRatePercent / (12.0 * 100.0)

        val schedule = ArrayList<AmortizationRow>(tenureMonths)
        var currentBalance = principalMinor

        for (m in 1..tenureMonths) {
            val interestPartDouble = currentBalance.toDouble() * monthlyRate
            val interestPart = BigDecimal.valueOf(interestPartDouble)
                .setScale(0, RoundingMode.HALF_EVEN)
                .longValueExact()

            val principalPart = if (m == tenureMonths) {
                // Final month settles remaining balance exactly
                currentBalance
            } else {
                (emiMinor - interestPart).coerceAtLeast(0L).coerceAtMost(currentBalance)
            }

            val closingBalance = (currentBalance - principalPart).coerceAtLeast(0L)

            schedule.add(
                AmortizationRow(
                    monthIndex = m,
                    openingBalanceMinor = currentBalance,
                    emiMinor = principalPart + interestPart,
                    principalComponentMinor = principalPart,
                    interestComponentMinor = interestPart,
                    closingBalanceMinor = closingBalance
                )
            )

            currentBalance = closingBalance
            if (currentBalance == 0L) break
        }

        return schedule
    }

    /**
     * Prepayment Simulation:
     * Simulates making an extra recurring payment every month or a one-time prepayment.
     * Computes exact interest saved and tenure reduction.
     */
    fun simulatePrepayment(
        principalMinor: Long,
        annualInterestRatePercent: Double,
        tenureMonths: Int,
        extraMonthlyPaymentMinor: Long = 0L,
        lumpSumPrepaymentMinor: Long = 0L,
        lumpSumMonthIndex: Int = 1
    ): PrepaymentSimulationResult {
        val baseSchedule = generateAmortizationSchedule(principalMinor, annualInterestRatePercent, tenureMonths)
        val originalTotalInterest = baseSchedule.sumOf { it.interestComponentMinor }

        val monthlyRate = annualInterestRatePercent / (12.0 * 100.0)
        val regularEmi = calculateMonthlyEmi(principalMinor, annualInterestRatePercent, tenureMonths)

        var balance = principalMinor
        var revisedInterest = 0L
        var revisedMonths = 0

        while (balance > 0L && revisedMonths < (tenureMonths * 2)) {
            revisedMonths++

            // Apply lumpsum if applicable
            if (revisedMonths == lumpSumMonthIndex && lumpSumPrepaymentMinor > 0L) {
                balance = (balance - lumpSumPrepaymentMinor).coerceAtLeast(0L)
                if (balance == 0L) break
            }

            val interest = BigDecimal.valueOf(balance.toDouble() * monthlyRate)
                .setScale(0, RoundingMode.HALF_EVEN)
                .longValueExact()
            revisedInterest += interest

            val totalPaidThisMonth = regularEmi + extraMonthlyPaymentMinor
            val principalPaid = (totalPaidThisMonth - interest).coerceAtLeast(0L)

            if (principalPaid >= balance) {
                balance = 0L
                break
            } else {
                balance -= principalPaid
            }
        }

        val interestSaved = (originalTotalInterest - revisedInterest).coerceAtLeast(0L)
        val monthsSaved = (tenureMonths - revisedMonths).coerceAtLeast(0)

        return PrepaymentSimulationResult(
            originalTotalInterestMinor = originalTotalInterest,
            revisedTotalInterestMinor = revisedInterest,
            interestSavedMinor = interestSaved,
            originalTenureMonths = tenureMonths,
            revisedTenureMonths = revisedMonths,
            monthsSaved = monthsSaved
        )
    }
}
