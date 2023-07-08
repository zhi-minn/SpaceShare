package com.example.spaceshare.utils

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber

object PhoneNumberValidator {
    fun isValidCanadianPhoneNumber(phoneNumber: String): Boolean {
        val phoneNumberUtil = PhoneNumberUtil.getInstance()
        val numberProto: Phonenumber.PhoneNumber
        try {
            numberProto = phoneNumberUtil.parse(phoneNumber, "CA")
        } catch (e: Exception) {
            return false
        }
        return phoneNumberUtil.isValidNumberForRegion(numberProto, "CA")
    }
}