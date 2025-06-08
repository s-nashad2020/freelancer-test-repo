package com.respiroc.companylookup.service

import com.respiroc.companylookup.model.CompanyInfo
import com.respiroc.companylookup.model.CompanySearchResult

/**
 * Service interface for company lookup operations
 */
interface CompanyLookupService {
    
    /**
     * Search for companies based on a query and country code
     * 
     * @param query The search query (company name, registration number, etc.)
     * @param countryCode The ISO country code to search in
     * @return CompanySearchResult containing matching companies
     */
    fun search(query: String, countryCode: String): CompanySearchResult
    
    /**
     * Get detailed information about a specific company
     * 
     * @param companyId The unique identifier of the company
     * @param countryCode The ISO country code where the company is registered
     * @return CompanyInfo containing detailed company information
     */
    fun getInfo(companyId: String, countryCode: String): CompanyInfo
}