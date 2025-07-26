package com.respiroc.employees.application

import com.respiroc.employees.application.payload.CreateEmployeePayload
import com.respiroc.employees.application.payload.UpdateEmployeePayload
import com.respiroc.employees.domain.model.Employee
import com.respiroc.employees.domain.repository.EmployeeRepository
import com.respiroc.util.domain.address.Address
import com.respiroc.util.exception.ResourceNotFoundException
import com.respiroc.util.exception.ResourceAlreadyExistsException
import jakarta.persistence.EntityManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class EmployeeService(
    private val employeeRepository: EmployeeRepository,
    private val entityManager: EntityManager
) {

    fun createEmployee(tenantId: Long, command: CreateEmployeePayload): Employee {
        // Validate unique constraints
        validateUniqueConstraints(tenantId, command)
        
        val address = getOrCreateAddress(command)
        val employee = Employee()
        employee.tenantId = tenantId
        employee.name = command.name
        employee.employeeNumber = command.employeeNumber
        employee.email = command.email
        employee.personalPhone = command.personalPhone
        employee.workPhone = command.workPhone
        employee.dateOfBirth = command.dateOfBirth
        employee.address = address
        
        return employeeRepository.save(employee)
    }

    fun updateEmployee(tenantId: Long, employeeId: Long, command: UpdateEmployeePayload): Employee {
        val employee = findEmployeeByIdAndTenant(tenantId, employeeId)
        
        // Validate unique constraints for updates (excluding current employee)
        validateUniqueConstraintsForUpdate(tenantId, employeeId, command)
        
        // Update fields if provided
        command.name?.let { employee.name = it }
        command.employeeNumber?.let { employee.employeeNumber = it }
        command.email?.let { employee.email = it }
        command.personalPhone?.let { employee.personalPhone = it }
        command.workPhone?.let { employee.workPhone = it }
        command.dateOfBirth?.let { employee.dateOfBirth = it }
        
        // Update address if address fields are provided
        if (hasAddressFields(command)) {
            employee.address = getOrCreateAddress(command)
        }
        
        return employeeRepository.save(employee)
    }

    fun getEmployee(tenantId: Long, employeeId: Long): Employee {
        return findEmployeeByIdAndTenant(tenantId, employeeId)
    }

    fun getEmployees(tenantId: Long, pageable: Pageable): Page<Employee> {
        return employeeRepository.findByTenantId(tenantId, pageable)
    }

    fun searchEmployees(tenantId: Long, searchTerm: String, pageable: Pageable): Page<Employee> {
        return if (searchTerm.isBlank()) {
            employeeRepository.findByTenantId(tenantId, pageable)
        } else {
            employeeRepository.findByTenantIdAndSearchTerm(tenantId, searchTerm, pageable)
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

    private fun validateUniqueConstraints(tenantId: Long, command: CreateEmployeePayload) {
        command.email?.let { email ->
            if (employeeRepository.existsByTenantIdAndEmail(tenantId, email)) {
                throw ResourceAlreadyExistsException("Employee with email '$email' already exists")
            }
        }
        
        command.employeeNumber?.let { employeeNumber ->
            if (employeeRepository.existsByTenantIdAndEmployeeNumber(tenantId, employeeNumber)) {
                throw ResourceAlreadyExistsException("Employee with number '$employeeNumber' already exists")
            }
        }
    }

    private fun validateUniqueConstraintsForUpdate(tenantId: Long, employeeId: Long, command: UpdateEmployeePayload) {
        command.email?.let { email ->
            // Check if email exists for another employee (not the current one)
            val existingEmployeeWithEmail = employeeRepository.findByTenantIdAndEmail(tenantId, email)
            if (existingEmployeeWithEmail != null && existingEmployeeWithEmail.id != employeeId) {
                throw ResourceAlreadyExistsException("Employee with email '$email' already exists")
            }
        }
        
        command.employeeNumber?.let { employeeNumber ->
            // Check if employee number exists for another employee (not the current one)
            val existingEmployeeWithNumber = employeeRepository.findByTenantIdAndEmployeeNumber(tenantId, employeeNumber)
            if (existingEmployeeWithNumber != null && existingEmployeeWithNumber.id != employeeId) {
                throw ResourceAlreadyExistsException("Employee with number '$employeeNumber' already exists")
            }
        }
    }

    private fun getOrCreateAddress(command: CreateEmployeePayload): Address? {
        if (!isValidAddress(command)) return null
        val address = Address(
            city = command.city ?: "",
            addressPart1 = command.addressPart1 ?: "",
            postalCode = command.postalCode,
            countryIsoCode = command.addressCountryCode ?: "NO",
            addressPart2 = command.addressPart2,
            administrativeDivisionCode = command.administrativeDivisionCode
        )
        return Address.upsertAddress(entityManager, address)
    }

    private fun getOrCreateAddress(command: UpdateEmployeePayload): Address? {
        if (!isValidAddress(command)) return null
        val address = Address(
            city = command.city ?: "",
            addressPart1 = command.addressPart1 ?: "",
            postalCode = command.postalCode,
            countryIsoCode = command.addressCountryCode ?: "NO",
            addressPart2 = command.addressPart2,
            administrativeDivisionCode = command.administrativeDivisionCode
        )
        return Address.upsertAddress(entityManager, address)
    }

    private fun isValidAddress(command: CreateEmployeePayload): Boolean {
        return command.addressPart1 != null || command.city != null || command.postalCode != null ||
               command.addressPart2 != null || command.administrativeDivisionCode != null
    }

    private fun isValidAddress(command: UpdateEmployeePayload): Boolean {
        return command.addressPart1 != null || command.city != null || command.postalCode != null ||
               command.addressPart2 != null || command.administrativeDivisionCode != null
    }

    private fun hasAddressFields(command: UpdateEmployeePayload): Boolean {
        return command.addressPart1 != null || command.city != null || command.addressCountryCode != null ||
               command.addressPart2 != null || command.postalCode != null || command.administrativeDivisionCode != null
    }
} 