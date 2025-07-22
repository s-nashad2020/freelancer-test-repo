package com.respiroc.webapp.controller.request

import jakarta.validation.constraints.NotBlank

data class NewBankAccountRequest(
    @field:NotBlank(message = "Account number is required")
    val accountNumber: String = "",

    @field:NotBlank(message = "Bank code is required")
    val bankCode: String = "",

    @field:NotBlank(message = "Country is required")
    val countryCode: String = ""
)
