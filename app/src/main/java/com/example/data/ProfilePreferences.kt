package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.domain.HouseRentTier
import com.example.domain.PayScale2015
import com.example.domain.PayScalePhase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ibas_profile_prefs")

data class EmployeeProfile(
    val fullName: String = "",
    val designation: String = "",
    val officeName: String = "",
    val authorityTitle: String = "উপজেলা নির্বাহী অফিসার / দপ্তর প্রধান",
    val ibasId: String = "",
    val gpfAccountNo: String = "",
    val mobileNo: String = "",
    val grade: Int = 9,
    val stepIndex: Int = 0,
    val payScalePhase: PayScalePhase = PayScalePhase.PHASE_1_2026,
    val basicPay: Int = PayScale2015.getPayableBasicForStep(9, 0, PayScalePhase.PHASE_1_2026),
    val houseRentTier: HouseRentTier = HouseRentTier.OTHER_DISTRICT,
    val gpfPercent: Int = 10,
    val gpfOpeningBalance: Double = 350000.0,
    val birthDateIso: String = "1988-05-15",
    val joiningDateIso: String = "2018-01-01",
    val isBengali: Boolean = true
)

class ProfilePreferencesRepository(private val context: Context) {
    private object Keys {
        val FULL_NAME = stringPreferencesKey("full_name")
        val DESIGNATION = stringPreferencesKey("designation")
        val OFFICE_NAME = stringPreferencesKey("office_name")
        val AUTHORITY_TITLE = stringPreferencesKey("authority_title")
        val IBAS_ID = stringPreferencesKey("ibas_id")
        val GPF_ACCOUNT_NO = stringPreferencesKey("gpf_account_no")
        val MOBILE_NO = stringPreferencesKey("mobile_no")
        val GRADE = intPreferencesKey("grade")
        val STEP_INDEX = intPreferencesKey("step_index")
        val PAY_PHASE = stringPreferencesKey("pay_phase")
        val BASIC_PAY = intPreferencesKey("basic_pay")
        val HRA_TIER = stringPreferencesKey("hra_tier")
        val GPF_PERCENT = intPreferencesKey("gpf_percent")
        val GPF_BALANCE = doublePreferencesKey("gpf_balance")
        val BIRTH_DATE = stringPreferencesKey("birth_date")
        val JOINING_DATE = stringPreferencesKey("joining_date")
        val IS_BENGALI = booleanPreferencesKey("is_bengali")
    }

    val profileFlow: Flow<EmployeeProfile> = context.dataStore.data.map { prefs ->
        val tierName = prefs[Keys.HRA_TIER] ?: HouseRentTier.OTHER_DISTRICT.name
        val tier = runCatching { HouseRentTier.valueOf(tierName) }
            .getOrDefault(HouseRentTier.OTHER_DISTRICT)
        val phaseName = prefs[Keys.PAY_PHASE] ?: PayScalePhase.PHASE_1_2026.name
        val phase = runCatching { PayScalePhase.valueOf(phaseName) }
            .getOrDefault(PayScalePhase.PHASE_1_2026)
        val grade = prefs[Keys.GRADE] ?: 9
        val stepIndex = prefs[Keys.STEP_INDEX] ?: 0
        val defaultBasic = PayScale2015.getPayableBasicForStep(grade, stepIndex, phase)

        EmployeeProfile(
            fullName = prefs[Keys.FULL_NAME] ?: "",
            designation = prefs[Keys.DESIGNATION] ?: "",
            officeName = prefs[Keys.OFFICE_NAME] ?: "",
            authorityTitle = prefs[Keys.AUTHORITY_TITLE] ?: "উপজেলা নির্বাহী অফিসার / দপ্তর প্রধান",
            ibasId = prefs[Keys.IBAS_ID] ?: "",
            gpfAccountNo = prefs[Keys.GPF_ACCOUNT_NO] ?: "",
            mobileNo = prefs[Keys.MOBILE_NO] ?: "",
            grade = grade,
            stepIndex = stepIndex,
            payScalePhase = phase,
            basicPay = prefs[Keys.BASIC_PAY] ?: defaultBasic,
            houseRentTier = tier,
            gpfPercent = prefs[Keys.GPF_PERCENT] ?: 10,
            gpfOpeningBalance = prefs[Keys.GPF_BALANCE] ?: 350000.0,
            birthDateIso = prefs[Keys.BIRTH_DATE] ?: "1988-05-15",
            joiningDateIso = prefs[Keys.JOINING_DATE] ?: "2018-01-01",
            isBengali = prefs[Keys.IS_BENGALI] ?: true
        )
    }

    suspend fun saveProfile(profile: EmployeeProfile) {
        context.dataStore.edit { prefs ->
            prefs[Keys.FULL_NAME] = profile.fullName
            prefs[Keys.DESIGNATION] = profile.designation
            prefs[Keys.OFFICE_NAME] = profile.officeName
            prefs[Keys.AUTHORITY_TITLE] = profile.authorityTitle
            prefs[Keys.IBAS_ID] = profile.ibasId
            prefs[Keys.GPF_ACCOUNT_NO] = profile.gpfAccountNo
            prefs[Keys.MOBILE_NO] = profile.mobileNo
            prefs[Keys.GRADE] = profile.grade
            prefs[Keys.STEP_INDEX] = profile.stepIndex
            prefs[Keys.PAY_PHASE] = profile.payScalePhase.name
            prefs[Keys.BASIC_PAY] = profile.basicPay
            prefs[Keys.HRA_TIER] = profile.houseRentTier.name
            prefs[Keys.GPF_PERCENT] = profile.gpfPercent
            prefs[Keys.GPF_BALANCE] = profile.gpfOpeningBalance
            prefs[Keys.BIRTH_DATE] = profile.birthDateIso
            prefs[Keys.JOINING_DATE] = profile.joiningDateIso
            prefs[Keys.IS_BENGALI] = profile.isBengali
        }
    }

    suspend fun toggleLanguage(isBengali: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.IS_BENGALI] = isBengali
        }
    }
}
