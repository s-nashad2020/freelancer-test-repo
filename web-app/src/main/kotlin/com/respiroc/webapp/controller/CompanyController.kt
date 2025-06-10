package com.respiroc.webapp.controller

import com.respiroc.companylookup.model.CompanyInfo
import com.respiroc.companylookup.model.CompanySearchResult
import com.respiroc.companylookup.service.CompanyLookupService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/companies")
class CompanyController(private val companyLookupService: CompanyLookupService) {

    @GetMapping("/search")
    fun searchCompanies(
        @RequestParam query: String,
        @RequestParam countryCode: String
    ): CompanySearchResult {
        return companyLookupService.search(query, countryCode)
    }

    @GetMapping("/{companyId}")
    fun getCompanyInfo(
        @PathVariable companyId: String,
        @RequestParam countryCode: String
    ): CompanyInfo {
        return companyLookupService.getInfo(companyId, countryCode)
    }
}