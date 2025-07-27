package com.respiroc.employees.application

import com.respiroc.employees.application.payload.CreateEmployeePayload
import com.respiroc.employees.application.payload.UpdateEmployeePayload
import com.respiroc.employees.domain.model.Employee
import com.respiroc.employees.domain.repository.EmployeeRepository
import com.respiroc.util.domain.address.Address
import com.respiroc.util.exception.ResourceNotFoundException
import com.respiroc.util.exception.ResourceAlreadyExistsException
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class EmployeeService(
    private val employeeRepository: EmployeeRepository,
    private val entityManager: EntityManager
) {

    fun createEmployee(tenantId: Long, employeePayload: CreateEmployeePayload): Employee {
        // Validate unique constraints
        validateUniqueConstraints(tenantId, employeePayload)
        
        val address = getOrCreateAddress(employeePayload)
        val employee = Employee()
        employee.tenantId = tenantId
        employee.name = employeePayload.name
        employee.employeeNumber = employeePayload.employeeNumber
        employee.email = employeePayload.email
        employee.personalPhone = employeePayload.personalPhone
        employee.workPhone = employeePayload.workPhone
        employee.dateOfBirth = employeePayload.dateOfBirth
        employee.address = address
        
        return employeeRepository.save(employee)
    }

    fun updateEmployee(tenantId: Long, employeeId: Long, employeePayload: UpdateEmployeePayload): Employee {
        val employee = findEmployeeByIdAndTenant(tenantId, employeeId)
        
        // Validate unique constraints for updates (excluding current employee)
        validateUniqueConstraintsForUpdate(tenantId, employeeId, employeePayload)
        
        // Update fields if provided
        employeePayload.name?.let { employee.name = it }
        employeePayload.employeeNumber?.let { employee.employeeNumber = it }
        employeePayload.email?.let { employee.email = it }
        employeePayload.personalPhone?.let { employee.personalPhone = it }
        employeePayload.workPhone?.let { employee.workPhone = it }
        employeePayload.dateOfBirth?.let { employee.dateOfBirth = it }
        
        // Update address if address fields are provided
        if (hasAddressFields(employeePayload)) {
            employee.address = getOrCreateAddress(employeePayload)
        }
        
        return employeeRepository.save(employee)
    }

    fun getEmployee(tenantId: Long, employeeId: Long): Employee {
        return findEmployeeByIdAndTenant(tenantId, employeeId)
    }

    fun getEmployees(tenantId: Long): List<Employee> {
        return employeeRepository.findByTenantId(tenantId)
    }

    fun searchEmployees(tenantId: Long, searchTerm: String): List<Employee> {
        return if (searchTerm.isBlank()) {
            employeeRepository.findByTenantId(tenantId)
        } else {
            employeeRepository.findByTenantIdAndSearchTerm(tenantId, searchTerm)
        }
    }

    fun deleteEmployee(tenantId: Long, employeeId: Long) {
        val employee = findEmployeeByIdAndTenant(tenantId, employeeId)
        employeeRepository.delete(employee)
    }

    private fun findEmployeeByIdAndTenant(tenantId: Long, employeeId: Long): Employee {
        return employeeRepository.findByTenantIdAndId(tenantId, employeeId)
            ?: throw ResourceNotFoundException("Employee not found with id: $employeeId")
    }

    private fun validateUniqueConstraints(tenantId: Long, employeePayload: CreateEmployeePayload) {
        employeePayload.email?.let { email ->
            if (employeeRepository.existsByTenantIdAndEmail(tenantId, email)) {
                throw ResourceAlreadyExistsException("Employee with email '$email' already exists")
            }
        }
        
        employeePayload.employeeNumber?.let { employeeNumber ->
            if (employeeRepository.existsByTenantIdAndEmployeeNumber(tenantId, employeeNumber)) {
                throw ResourceAlreadyExistsException("Employee with number '$employeeNumber' already exists")
            }
        }
    }

    private fun validateUniqueConstraintsForUpdate(tenantId: Long, employeeId: Long, employeePayload: UpdateEmployeePayload) {
        employeePayload.email?.let { email ->
            // Check if email exists for another employee (not the current one)
            val existingEmployeeWithEmail = employeeRepository.findByTenantIdAndEmail(tenantId, email)
            if (existingEmployeeWithEmail != null && existingEmployeeWithEmail.id != employeeId) {
                throw ResourceAlreadyExistsException("Employee with email '$email' already exists")
            }
        }
        
        employeePayload.employeeNumber?.let { employeeNumber ->
            // Check if employee number exists for another employee (not the current one)
            val existingEmployeeWithNumber = employeeRepository.findByTenantIdAndEmployeeNumber(tenantId, employeeNumber)
            if (existingEmployeeWithNumber != null && existingEmployeeWithNumber.id != employeeId) {
                throw ResourceAlreadyExistsException("Employee with number '$employeeNumber' already exists")
            }
        }
    }

    private fun getOrCreateAddress(employeePayload: CreateEmployeePayload): Address? {
        if (!isValidAddress(employeePayload)) return null
        val address = Address(
            city = employeePayload.city ?: "",
            addressPart1 = employeePayload.addressPart1 ?: "",
            postalCode = employeePayload.postalCode,
            countryIsoCode = employeePayload.addressCountryCode ?: "NO",
            addressPart2 = employeePayload.addressPart2,
            administrativeDivisionCode = employeePayload.administrativeDivisionCode
        )
        return Address.upsertAddress(entityManager, address)
    }

    private fun getOrCreateAddress(employeePayload: UpdateEmployeePayload): Address? {
        if (!isValidAddress(employeePayload)) return null
        val address = Address(
            city = employeePayload.city ?: "",
            addressPart1 = employeePayload.addressPart1 ?: "",
            postalCode = employeePayload.postalCode,
            countryIsoCode = employeePayload.addressCountryCode ?: "NO",
            addressPart2 = employeePayload.addressPart2,
            administrativeDivisionCode = employeePayload.administrativeDivisionCode
        )
        return Address.upsertAddress(entityManager, address)
    }

    private fun isValidAddress(employeePayload: CreateEmployeePayload): Boolean {
        return employeePayload.addressPart1 != null || employeePayload.city != null || employeePayload.postalCode != null ||
               employeePayload.addressPart2 != null || employeePayload.administrativeDivisionCode != null
    }

    private fun isValidAddress(employeePayload: UpdateEmployeePayload): Boolean {
        return employeePayload.addressPart1 != null || employeePayload.city != null || employeePayload.postalCode != null ||
               employeePayload.addressPart2 != null || employeePayload.administrativeDivisionCode != null
    }

    private fun hasAddressFields(employeePayload: UpdateEmployeePayload): Boolean {
        return employeePayload.addressPart1 != null || employeePayload.city != null || employeePayload.addressCountryCode != null ||
               employeePayload.addressPart2 != null || employeePayload.postalCode != null || employeePayload.administrativeDivisionCode != null
    }
} 