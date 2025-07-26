package com.respiroc.employees.application.payload

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class UpdateEmployeePayload(
    @field:Size(max = 255, message = "Name must not exceed 255 characters")
    val name: String? = null,
    
    @field:Size(max = 50, message = "Employee number must not exceed 50 characters")
    val employeeNumber: String? = null,
    
    @field:Email(message = "Email must be a valid email address")
    @field:Size(max = 255, message = "Email must not exceed 255 characters")
    val email: String? = null,
    
    @field:Size(max = 50, message = "Personal phone must not exceed 50 characters")
    val personalPhone: String? = null,
    
    @field:Size(max = 50, message = "Work phone must not exceed 50 characters")
    val workPhone: String? = null,
    
    val dateOfBirth: LocalDate? = null,
    
    // Address fields
    @field:Size(max = 75, message = "Address part 1 must not exceed 75 characters")
    val addressPart1: String? = null,
    
    @field:Size(max = 75, message = "Address part 2 must not exceed 75 characters")
    val addressPart2: String? = null,
    
    @field:Size(max = 40, message = "City must not exceed 40 characters")
    val city: String? = null,
    
    @field:Size(max = 20, message = "Postal code must not exceed 20 characters")
    val postalCode: String? = null,
    
    @field:Size(max = 2, message = "Country code must be 2 characters")
    val addressCountryCode: String? = null,
    
    @field:Size(max = 10, message = "Administrative division code must not exceed 10 characters")
    val administrativeDivisionCode: String? = null
) 