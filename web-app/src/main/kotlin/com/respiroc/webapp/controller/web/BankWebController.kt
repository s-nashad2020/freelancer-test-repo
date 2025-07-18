package com.respiroc.webapp.controller.web

import com.respiroc.bank.application.BankAccountService
import com.respiroc.webapp.controller.BaseController
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping(value = ["/bank"])
class BankWebController(private val bankAccountService: BankAccountService) : BaseController() {

    @GetMapping("/account")
    fun getBankAccounts(model: Model): String {
        addCommonAttributesForCurrentTenant(model, "Bank Accounts")
        return "bank/bank-account"
    }

    @GetMapping("/account/new")
    fun getBankAccountForm(model: Model): String {
        addCommonAttributesForCurrentTenant(model, "New Bank Account")
        return "bank/bank-account-from"
    }
}