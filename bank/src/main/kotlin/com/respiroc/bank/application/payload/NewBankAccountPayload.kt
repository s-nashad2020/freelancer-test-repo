package com.respiroc.bank.application.payload

data class NewBankAccountPayload(val countryCode: String, val accountNumber: String, val bankCode: String)
