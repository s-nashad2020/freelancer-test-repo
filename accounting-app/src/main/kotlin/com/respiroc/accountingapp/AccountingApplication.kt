package com.respiroc.accountingapp

import com.respiroc.companylookup.config.CompanyLookupConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(CompanyLookupConfig::class)
class AccountingApplication

fun main(args: Array<String>) {
    runApplication<AccountingApplication>(*args)
}