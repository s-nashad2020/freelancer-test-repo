package com.respiroc.employees.application

import com.respiroc.employees.application.payload.CreateEmployeePayload
import com.respiroc.employees.domain.model.Employee
import com.respiroc.employees.domain.repository.EmployeeRepository
import com.respiroc.util.domain.address.Address
import com.respiroc.util.exception.ResourceAlreadyExistsException
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.LocalDate

class EmployeeServiceTest {

    private val employeeRepository: EmployeeRepository = mock()
    private val entityManager: EntityManager = mock()
    private val employeeService = EmployeeService(employeeRepository, entityManager)

    @Test
    fun `should create employee successfully`() {
        // Given
        val tenantId = 1L
        val createPayload = CreateEmployeePayload(
            name = "John Doe",
            email = "john.doe@example.com",
            employeeNumber = "EMP001",
            personalPhone = "+1234567890",
            workPhone = "+0987654321",
            dateOfBirth = LocalDate.of(1990, 1, 1)
        )
        
        val address = Address(
            countryIsoCode = "US",
            administrativeDivisionCode = null,
            city = "New York",
            postalCode = "10001",
            addressPart1 = "123 Main St",
            addressPart2 = null
        )
        
        whenever(employeeRepository.existsByTenantIdAndEmail(tenantId, createPayload.email!!)).thenReturn(false)
        whenever(employeeRepository.existsByTenantIdAndEmployeeNumber(tenantId, createPayload.employeeNumber!!)).thenReturn(false)
        whenever(employeeRepository.save(any())).thenAnswer { it.getArgument(0) }
        
        // Mock EntityManager for address creation
        val mockQuery = mock<jakarta.persistence.Query>()
        whenever(entityManager.createNativeQuery(any<String>(), eq(Address::class.java))).thenReturn(mockQuery)
        whenever(mockQuery.setParameter(any<String>(), any<Any>())).thenReturn(mockQuery)
        whenever(mockQuery.singleResult).thenReturn(address)
        
        // When
        val result = employeeService.createEmployee(tenantId, createPayload)
        
        // Then
        assert(result.name == createPayload.name)
        assert(result.email == createPayload.email)
        assert(result.employeeNumber == createPayload.employeeNumber)
        verify(employeeRepository).save(any())
    }

    @Test
    fun `should throw exception when email already exists`() {
        // Given
        val tenantId = 1L
        val createPayload = CreateEmployeePayload(
            name = "John Doe",
            email = "john.doe@example.com"
        )
        
        whenever(employeeRepository.existsByTenantIdAndEmail(tenantId, createPayload.email!!)).thenReturn(true)
        
        // When & Then
        assertThrows<ResourceAlreadyExistsException> {
            employeeService.createEmployee(tenantId, createPayload)
        }
    }

    @Test
    fun `should throw exception when employee number already exists`() {
        // Given
        val tenantId = 1L
        val createPayload = CreateEmployeePayload(
            name = "John Doe",
            employeeNumber = "EMP001"
        )
        
        whenever(employeeRepository.existsByTenantIdAndEmployeeNumber(tenantId, createPayload.employeeNumber!!)).thenReturn(true)
        
        // When & Then
        assertThrows<ResourceAlreadyExistsException> {
            employeeService.createEmployee(tenantId, createPayload)
        }
    }
} 