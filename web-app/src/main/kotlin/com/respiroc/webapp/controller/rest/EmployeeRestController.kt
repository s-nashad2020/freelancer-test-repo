package com.respiroc.webapp.controller.rest

import com.respiroc.employees.application.EmployeeService
import com.respiroc.employees.application.payload.CreateEmployeePayload
import com.respiroc.employees.application.payload.UpdateEmployeePayload
import com.respiroc.employees.domain.model.Employee
import com.respiroc.util.context.ContextAwareApi

import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/api/employees")
class EmployeeRestController(
    private val employeeService: EmployeeService
) : ContextAwareApi {

    @GetMapping
    fun getEmployees(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "name") sortBy: String,
        @RequestParam(defaultValue = "asc") sortDir: String,
        @RequestParam(required = false) search: String?
    ): ResponseEntity<Page<Employee>> {
        val tenantId = tenantId()
        val sort = Sort.by(if (sortDir == "desc") Sort.Direction.DESC else Sort.Direction.ASC, sortBy)
        val pageable = PageRequest.of(page, size, sort)
        
        val employees = if (search.isNullOrBlank()) {
            employeeService.getEmployees(tenantId, pageable)
        } else {
            employeeService.searchEmployees(tenantId, search, pageable)
        }
        
        return ResponseEntity.ok(employees)
    }

    @GetMapping("/{id}")
    fun getEmployee(@PathVariable id: Long): ResponseEntity<Employee> {
        val tenantId = tenantId()
        val employee = employeeService.getEmployee(tenantId, id)
        return ResponseEntity.ok(employee)
    }

    @GetMapping("/export")
    fun exportEmployees(
        @RequestParam(required = false) search: String?,
        @RequestParam(defaultValue = "name") sortBy: String,
        @RequestParam(defaultValue = "asc") sortDir: String
    ): ResponseEntity<String> {
        val tenantId = tenantId()
        val sort = Sort.by(if (sortDir == "desc") Sort.Direction.DESC else Sort.Direction.ASC, sortBy)
        val pageable = PageRequest.of(0, 10000, sort) // Export all employees
        
        val employees = if (search.isNullOrBlank()) {
            employeeService.getEmployees(tenantId, pageable)
        } else {
            employeeService.searchEmployees(tenantId, search, pageable)
        }
        
        val csvContent = generateCsvContent(employees.content)
        
        val headers = HttpHeaders()
        headers.contentType = MediaType.parseMediaType("text/csv")
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"employees_export.csv\"")
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(csvContent)
    }

    @PostMapping
    fun createEmployee(@Valid @RequestBody payload: CreateEmployeePayload): ResponseEntity<Employee> {
        val tenantId = tenantId()
        val employee = employeeService.createEmployee(tenantId, payload)
        return ResponseEntity.status(HttpStatus.CREATED).body(employee)
    }

    @PutMapping("/{id}")
    fun updateEmployee(
        @PathVariable id: Long,
        @Valid @RequestBody payload: UpdateEmployeePayload
    ): ResponseEntity<Employee> {
        val tenantId = tenantId()
        val employee = employeeService.updateEmployee(tenantId, id, payload)
        return ResponseEntity.ok(employee)
    }

    @DeleteMapping("/{id}")
    fun deleteEmployee(@PathVariable id: Long): ResponseEntity<Unit> {
        val tenantId = tenantId()
        employeeService.deleteEmployee(tenantId, id)
        return ResponseEntity.ok().build()
    }

    private fun generateCsvContent(employees: List<Employee>): String {
        val csvBuilder = StringBuilder()
        
        // CSV Header
        csvBuilder.append("Name,Employee Number,Email,Personal Phone,Work Phone,Address,City,Postal Code,Country,Date of Birth\n")
        
        // CSV Data
        employees.forEach { employee ->
            val name = escapeCsvField(employee.name)
            val employeeNumber = escapeCsvField(employee.employeeNumber ?: "")
            val email = escapeCsvField(employee.email ?: "")
            val personalPhone = escapeCsvField(employee.personalPhone ?: "")
            val workPhone = escapeCsvField(employee.workPhone ?: "")
            val address = escapeCsvField(employee.address?.addressPart1 ?: "")
            val city = escapeCsvField(employee.address?.city ?: "")
            val postalCode = escapeCsvField(employee.address?.postalCode ?: "")
            val country = escapeCsvField(employee.address?.countryIsoCode ?: "")
            val dateOfBirth = employee.dateOfBirth?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: ""
            
            csvBuilder.append("$name,$employeeNumber,$email,$personalPhone,$workPhone,$address,$city,$postalCode,$country,$dateOfBirth\n")
        }
        
        return csvBuilder.toString()
    }

    private fun escapeCsvField(field: String): String {
        return if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            "\"${field.replace("\"", "\"\"")}\""
        } else {
            field
        }
    }
} 