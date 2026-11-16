package com.example.domain

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

enum class LeaveApplicationType(
    val code: String,
    val titleBn: String,
    val titleEn: String,
    val subtitleBn: String,
    val subtitleEn: String
) {
    RRL(
        "RRL",
        "শ্রান্তি ও বিনোদন ছুটি (RRL)",
        "Rest & Recreation Leave (RRL)",
        "১৫ দিনের ছুটি ও ১ মাসের মূল বেতন সমপরিমাণ ভাতা",
        "15-day leave & 1 month basic pay allowance"
    ),
    CL(
        "CL",
        "নৈমিত্তিক ছুটি (CL)",
        "Casual Leave (CL)",
        "জরুরি পারিবারিক বা ব্যক্তিগত প্রয়োজনে স্বল্পকালীন ছুটি",
        "Short leave for urgent personal/family reasons"
    ),
    EL(
        "EL",
        "অর্জিত ছুটি (EL)",
        "Earned Leave (EL)",
        "গড় বেতনে বা অর্ধ-গড় বেতনে অর্জিত ছুটি ভোগ",
        "Earned leave on full or half average pay"
    ),
    ML(
        "ML",
        "চিকিৎসা ছুটি (ML)",
        "Medical Leave (ML)",
        "অসুস্থতা ও চিকিৎসকের পরামর্শ মোতাবেক ছুটি",
        "Medical leave with physician certificate"
    ),
    JOINING(
        "JOINING",
        "ছুটি শেষে যোগদানপত্র",
        "Joining Report After Leave",
        "ছুটি ভোগ শেষে কর্মস্থলে পুনরায় যোগদানের আবেদন",
        "Report to resume duty after sanctioned leave"
    ),
    GPF_ADVANCE(
        "GPF_ADVANCE",
        "জিপিএফ অগ্রিমের আবেদন",
        "GPF Advance Application",
        "সাধারণ ভবিষ্য তহবিল হতে ফেরতযোগ্য/অফেরতযোগ্য অগ্রিম",
        "Refundable or non-refundable GPF advance"
    ),
    PRL(
        "PRL",
        "পিআরএল (PRL) ছুটি আবেদন",
        "Post-Retirement Leave (PRL)",
        "চাকরি শেষে ১ বছরের অবসর উত্তর ছুটির আবেদন",
        "1-year Post-Retirement Leave request"
    )
}

data class ApplicationFormInput(
    val type: LeaveApplicationType = LeaveApplicationType.RRL,
    val applicantName: String = "",
    val designation: String = "",
    val officeName: String = "",
    val ibasId: String = "",
    val gpfAccountNo: String = "",
    val recipientAuthority: String = "উপজেলা নির্বাহী অফিসার / দপ্তর প্রধান",
    val recipientOffice: String = "",
    val throughAuthority: String = "",
    val applicationDate: String = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
    val startDateIso: String = LocalDate.now().plusDays(2).toString(),
    val endDateIso: String = LocalDate.now().plusDays(16).toString(),
    val joiningDateIso: String = LocalDate.now().plusDays(17).toString(),
    val leaveMemoRef: String = "",
    val reason: String = "",
    val leaveAddress: String = "",
    val mobileNo: String = "",
    val requestedAmount: String = "৫০,০০০"
)

object ApplicationTemplates {

    fun calculateDaysInclusive(startIso: String, endIso: String): Int {
        return try {
            val s = LocalDate.parse(startIso.trim())
            val e = LocalDate.parse(endIso.trim())
            val diff = ChronoUnit.DAYS.between(s, e) + 1
            diff.coerceAtLeast(1).toInt()
        } catch (_: Exception) {
            1
        }
    }

    private fun formatPrettyDate(iso: String): String {
        return try {
            LocalDate.parse(iso.trim()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        } catch (_: Exception) {
            iso
        }
    }

    fun generateLetter(input: ApplicationFormInput, isBengali: Boolean): String {
        val name = input.applicantName.ifBlank { if (isBengali) "[আপনার নাম]" else "[Your Name]" }
        val desig = input.designation.ifBlank { if (isBengali) "[পদবি]" else "[Designation]" }
        val office = input.officeName.ifBlank { if (isBengali) "[দপ্তরের নাম]" else "[Office Name]" }
        val auth = input.recipientAuthority.ifBlank { if (isBengali) "দপ্তর প্রধান" else "Head of Office" }
        val authOffice = input.recipientOffice.ifBlank { office }
        val startPretty = formatPrettyDate(input.startDateIso)
        val endPretty = formatPrettyDate(input.endDateIso)
        val joinPretty = formatPrettyDate(input.joiningDateIso)
        val days = calculateDaysInclusive(input.startDateIso, input.endDateIso)

        return if (isBengali) {
            generateBengaliLetter(input, name, desig, office, auth, authOffice, startPretty, endPretty, joinPretty, days)
        } else {
            generateEnglishLetter(input, name, desig, office, auth, authOffice, startPretty, endPretty, joinPretty, days)
        }
    }

    private fun generateBengaliLetter(
        input: ApplicationFormInput,
        name: String,
        desig: String,
        office: String,
        auth: String,
        authOffice: String,
        startPretty: String,
        endPretty: String,
        joinPretty: String,
        days: Int
    ): String {
        val throughLine = if (input.throughAuthority.isNotBlank()) {
            "\nমাধ্যম: ${input.throughAuthority}\n"
        } else "\n"

        val idLine = if (input.ibasId.isNotBlank()) "আইডি/পিন: ${input.ibasId}\n" else ""
        val addrLine = if (input.leaveAddress.isNotBlank()) "ছুটিকালীন ঠিকানা: ${input.leaveAddress}\n" else ""
        val phoneLine = if (input.mobileNo.isNotBlank()) "মোবাইল: ${input.mobileNo}\n" else ""

        val (subject, body) = when (input.type) {
            LeaveApplicationType.RRL -> {
                "বিষয়: শ্রান্তি ও বিনোদন ছুটি এবং ভাতা মঞ্জুরের আবেদন।" to
                    """যথাযথ সম্মান পূর্বক নিবেদন এই যে, আমি নিম্নস্বাক্ষরকারী $office-এ "$desig" পদে কর্মরত আছি। সরকারি বিধি মোতাবেক ৩ (তিন) বছর চাকরিকাল পূর্ণ হওয়ায় আমার শ্রান্তি ও বিনোদন ছুটি এবং ভাতা প্রাপ্য হয়েছে। এমতাবস্থায় আগামী $startPretty খ্রি. তারিখ হতে $endPretty খ্রি. তারিখ পর্যন্ত মোট $days (পনেরো) দিন শ্রান্তি ও বিনোদন ছুটি এবং ০১ (এক) মাসের মূল বেতনের সমপরিমাণ শ্রান্তি ও বিনোদন ভাতা মঞ্জুরের জন্য বিনীত অনুরোধ জানাচ্ছি।${if (input.reason.isNotBlank()) "\n\nবিশেষ দ্রষ্টব্য: ${input.reason}" else ""}

অতএব, মহোদয় সমীপে বিনীত প্রার্থনা, অনুগ্রহপূর্বক আমাকে উক্ত $days দিনের শ্রান্তি ও বিনোদন ছুটি ও ভাতা মঞ্জুর করে বাধিত করবেন।"""
            }
            LeaveApplicationType.CL -> {
                val reasonText = input.reason.ifBlank { "অনিবার্য পারিবারিক/ব্যক্তিগত কারণে" }
                "বিষয়: নৈমিত্তিক ছুটি (CL) মঞ্জুরের আবেদন।" to
                    """যথাযথ সম্মান পূর্বক নিবেদন এই যে, আমি $office-এ "$desig" পদে কর্মরত আছি। $reasonText আগামী $startPretty খ্রি. তারিখ হতে $endPretty খ্রি. তারিখ পর্যন্ত মোট $days দিন কর্মস্থলে উপস্থিত থাকা আমার পক্ষে সম্ভব নয়।

অতএব, মহোদয় সমীপে বিনীত প্রার্থনা, আমাকে উক্ত $days দিনের নৈমিত্তিক ছুটি (CL) এবং কর্মস্থল ত্যাগের অনুমতি প্রদান করে বাধিত করবেন।"""
            }
            LeaveApplicationType.EL -> {
                val reasonText = input.reason.ifBlank { "ব্যক্তিগত ও পারিবারিক প্রয়োজনে" }
                "বিষয়: গড় বেতনে অর্জিত ছুটি (EL) মঞ্জুরের আবেদন।" to
                    """যথাযথ সম্মান পূর্বক নিবেদন এই যে, আমি $office-এ "$desig" পদে কর্মরত আছি। $reasonText আগামী $startPretty খ্রি. তারিখ হতে $endPretty খ্রি. তারিখ পর্যন্ত মোট $days দিন গড় বেতনে অর্জিত ছুটি (Earned Leave) ভোগ করা আমার একান্ত প্রয়োজন।

অতএব, মহোদয় সমীপে বিনীত প্রার্থনা, আমার পাওনা অর্জিত ছুটি থেকে উক্ত $days দিনের অর্জিত ছুটি মঞ্জুর করে বাধিত করবেন।"""
            }
            LeaveApplicationType.ML -> {
                val reasonText = input.reason.ifBlank { "শারীরিক অসুস্থতাজনিত কারণে বিশেষজ্ঞ চিকিৎসকের পরামর্শ অনুযায়ী বিশ্রাম ও চিকিৎসার জন্য" }
                "বিষয়: চিকিৎসা ছুটি (Medical Leave) মঞ্জুরের আবেদন।" to
                    """যথাযথ সম্মান পূর্বক নিবেদন এই যে, আমি $office-এ "$desig" পদে কর্মরত আছি। $reasonText আগামী $startPretty খ্রি. তারিখ হতে $endPretty খ্রি. তারিখ পর্যন্ত মোট $days দিন আমার চিকিৎসা ছুটির প্রয়োজন। আবেদনপত্রের সাথে চিকিৎসকের প্রদত্ত মেডিকেল সার্টিফিকেট সংযুক্ত করা হলো।

অতএব, মহোদয় সমীপে বিনীত প্রার্থনা, অনুগ্রহপূর্বক আমাকে উক্ত $days দিনের চিকিৎসা ছুটি মঞ্জুর করে বাধিত করবেন।"""
            }
            LeaveApplicationType.JOINING -> {
                val memoText = input.leaveMemoRef.ifBlank { "[পূর্বের ছুটি মঞ্জুরিপত্রের স্মারক ও তারিখ]" }
                val reasonText = input.reason.ifBlank { "মঞ্জুরকৃত ছুটি" }
                "বিষয়: ছুটি ভোগ শেষে কর্মস্থলে যোগদানপত্র।" to
                    """যথাযথ সম্মান পূর্বক নিবেদন এই যে, আপনার দপ্তরের স্মারক নং- $memoText মোতাবেক অনুমোদিত $startPretty খ্রি. হতে $endPretty খ্রি. তারিখ পর্যন্ত মোট $days দিনের $reasonText ভোগ শেষে অদ্য $joinPretty খ্রি. তারিখ পূর্বাহ্নে আমি আমার স্বীয় কর্মস্থল ($office)-এ পুনরায় যোগদান করলাম।

অতএব, মহোদয় সমীপে বিনীত প্রার্থনা, আমার যোগদানপত্রটি সদয় গ্রহণ করে বাধিত করবেন।"""
            }
            LeaveApplicationType.GPF_ADVANCE -> {
                val gpfNo = input.gpfAccountNo.ifBlank { "[জিপিএফ হিসাব নম্বর]" }
                val reasonText = input.reason.ifBlank { "পারিবারিক গৃহ নির্মাণ / চিকিৎসা ব্যয় নির্বাহের জন্য" }
                "বিষয়: সাধারণ ভবিষ্য তহবিল (GPF) হতে ${input.requestedAmount} টাকা অগ্রিম উত্তোলনের আবেদন।" to
                    """যথাযথ সম্মান পূর্বক নিবেদন এই যে, আমি $office-এ "$desig" পদে কর্মরত আছি। আমার জিপিএফ হিসাব নম্বর: $gpfNo। বর্তমানে $reasonText আমার অর্থের একান্ত প্রয়োজন দেখা দিয়েছে।

অতএব, মহোদয় সমীপে বিনীত প্রার্থনা, বিধি মোতাবেক আমার জিপিএফ তহবিল হতে ${input.requestedAmount}/- টাকা অগ্রিম উত্তোলনের মঞ্জুরি প্রদান করে বাধিত করবেন।"""
            }
            LeaveApplicationType.PRL -> {
                "বিষয়: অবসর উত্তর ছুটি (PRL) মঞ্জুরের আবেদন।" to
                    """যথাযথ সম্মান পূর্বক নিবেদন এই যে, আমি $office-এ "$desig" পদে কর্মরত আছি। সরকারি চাকরি আইন অনুযায়ী আগামী $startPretty খ্রি. তারিখে আমার ৫৯ বছর বয়স পূর্ণ হওয়ায় $startPretty খ্রি. হতে $endPretty খ্রি. তারিখ পর্যন্ত ০১ (এক) বছর মেয়াদী অবসর উত্তর ছুটি (PRL) প্রাপ্য হয়েছে।

অতএব, মহোদয় সমীপে বিনীত প্রার্থনা, আমাকে উক্ত মেয়াদের পিআরএল (PRL) ছুটি ও সংশ্লিষ্ট অবসর সুবিধা মঞ্জুর করে বাধিত করবেন।"""
            }
        }

        return """তারিখ: ${input.applicationDate} খ্রি.

বরাবর,
$auth
$authOffice
$throughLine
$subject

জনাব,
$body

বিনীত নিবেদক,

___________________
$name
$desig
$office
$idLine$addrLine$phoneLine""".trim()
    }

    private fun generateEnglishLetter(
        input: ApplicationFormInput,
        name: String,
        desig: String,
        office: String,
        auth: String,
        authOffice: String,
        startPretty: String,
        endPretty: String,
        joinPretty: String,
        days: Int
    ): String {
        val throughLine = if (input.throughAuthority.isNotBlank()) {
            "\nThrough: ${input.throughAuthority}\n"
        } else "\n"

        val idLine = if (input.ibasId.isNotBlank()) "iBAS++ ID: ${input.ibasId}\n" else ""
        val addrLine = if (input.leaveAddress.isNotBlank()) "Address during leave: ${input.leaveAddress}\n" else ""
        val phoneLine = if (input.mobileNo.isNotBlank()) "Mobile: ${input.mobileNo}\n" else ""

        val (subject, body) = when (input.type) {
            LeaveApplicationType.RRL -> "Subject: Prayer for Rest & Recreation Leave (RRL) and Recreation Allowance." to
                "With due respect, I am serving as $desig at $office. Having completed 3 years of continuous service as per government rules, I am eligible for Rest & Recreation Leave. I kindly request you to grant me $days days of RRL from $startPretty to $endPretty along with one month's basic pay as Recreation Allowance."
            LeaveApplicationType.CL -> "Subject: Application for Casual Leave (CL) for $days day(s)." to
                "With due respect, I am serving as $desig at $office. Due to ${input.reason.ifBlank { "urgent personal reasons" }}, I will be unable to attend office from $startPretty to $endPretty ($days days). Kindly grant me Casual Leave and permission to leave the station."
            LeaveApplicationType.EL -> "Subject: Application for Earned Leave (EL) on Average Pay." to
                "With due respect, I am serving as $desig at $office. Due to ${input.reason.ifBlank { "personal/family reasons" }}, I kindly request you to grant me $days days of Earned Leave from $startPretty to $endPretty from my accumulated leave credit."
            LeaveApplicationType.ML -> "Subject: Application for Medical Leave." to
                "With due respect, I am serving as $desig at $office. Due to ${input.reason.ifBlank { "illness and as advised by the attending physician" }}, I require $days days of Medical Leave from $startPretty to $endPretty. The medical certificate is attached herewith."
            LeaveApplicationType.JOINING -> "Subject: Joining Report after availing sanctioned leave." to
                "With due respect, pursuant to your office memo no. ${input.leaveMemoRef.ifBlank { "[Memo No. & Date]" }}, after availing $days days of leave from $startPretty to $endPretty, I hereby resume my duties as $desig at $office on the forenoon of $joinPretty. Kindly accept my joining report."
            LeaveApplicationType.GPF_ADVANCE -> "Subject: Application for GPF Advance of BDT ${input.requestedAmount}." to
                "With due respect, I am serving as $desig at $office (GPF A/C: ${input.gpfAccountNo.ifBlank { "[GPF No]" }}). For ${input.reason.ifBlank { "urgent family/medical/housing expenses" }}, kindly sanction a GPF advance of BDT ${input.requestedAmount} as per existing rules."
            LeaveApplicationType.PRL -> "Subject: Application for Post-Retirement Leave (PRL)." to
                "With due respect, I am serving as $desig at $office. Upon completion of 59 years of age, kindly grant me 1 year Post-Retirement Leave (PRL) from $startPretty to $endPretty along with admissible retirement benefits."
        }

        return """Date: ${input.applicationDate}

To,
$auth
$authOffice
$throughLine
$subject

Sir/Madam,
$body

Yours faithfully,

___________________
$name
$desig
$office
$idLine$addrLine$phoneLine""".trim()
    }
}
