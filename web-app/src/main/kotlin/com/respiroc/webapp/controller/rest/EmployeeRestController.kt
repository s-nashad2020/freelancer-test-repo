package com.respiroc.webapp.controller.rest

import com.respiroc.employees.application.EmployeeService
import com.respiroc.employees.application.payload.CreateEmployeePayload
import com.respiroc.employees.application.payload.UpdateEmployeePayload
import com.respiroc.employees.domain.model.Employee
import com.respiroc.util.context.ContextAwareApi

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/employees")
class EmployeeRestController(
    private val employeeService: EmployeeService
) : ContextAwareApi {

    @GetMapping
    fun getEmployees(
        @RequestParam(required = false) search: String?
    ): ResponseEntity<List<Employee>> {
        val tenantId = tenantId()
        
        val employees = if (search.isNullOrBlank()) {
            employeeService.getEmployees(tenantId)
        } else {
            employeeService.searchEmployees(tenantId, search)
        }
        
        return ResponseEntity.ok(employees)
    }

    @GetMapping("/{id}")
    fun getEmployee(@PathVariable id: Long): ResponseEntity<Employee> {
        val tenantId = tenantId()
        val employee = employeeService.getEmployee(tenantId, id)
        return ResponseEntity.ok(employee)
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
} 