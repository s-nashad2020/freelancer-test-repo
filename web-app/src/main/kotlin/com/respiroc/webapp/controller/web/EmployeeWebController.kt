package com.respiroc.webapp.controller.web

import com.respiroc.employees.application.EmployeeService
import com.respiroc.employees.application.payload.CreateEmployeePayload
import com.respiroc.employees.application.payload.UpdateEmployeePayload
import com.respiroc.webapp.controller.BaseController
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.time.format.DateTimeFormatter

@Controller
@RequestMapping("/employees")
class EmployeeWebController(
    private val employeeService: EmployeeService
) : BaseController() {

    @GetMapping
    fun overview(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "name") sortBy: String,
        @RequestParam(defaultValue = "asc") sortDir: String,
        @RequestParam(required = false) search: String?,
        model: Model
    ): String {
        addCommonAttributesForCurrentTenant(model, "Employee overview")
        model.addAttribute("pageTitle", "Employee overview")
        
        val tenantId = tenantId()
        val sort = Sort.by(if (sortDir == "desc") Sort.Direction.DESC else Sort.Direction.ASC, sortBy)
        val pageable = PageRequest.of(page, size, sort)
        
        val employees = if (search.isNullOrBlank()) {
            employeeService.getEmployees(tenantId, pageable)
        } else {
            employeeService.searchEmployees(tenantId, search, pageable)
        }
        
        model.addAttribute("employees", employees)
        return "employees/overview"
    }

    @GetMapping("/create")
    fun createForm(model: Model): String {
        addCommonAttributesForCurrentTenant(model, "Create Employee")
        model.addAttribute("pageTitle", "Create Employee")
        return "employees/form"
    }

    @GetMapping("/{id}/edit")
    fun editForm(@PathVariable id: Long, model: Model): String {
        addCommonAttributesForCurrentTenant(model, "Edit Employee")
        model.addAttribute("pageTitle", "Edit Employee")
        
        val tenantId = tenantId()
        val employee = employeeService.getEmployee(tenantId, id)
        model.addAttribute("employee", employee)
        
        return "employees/form"
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
    fun createEmployee(@ModelAttribute payload: CreateEmployeePayload, model: Model): String {
        try {
            val tenantId = tenantId()
            employeeService.createEmployee(tenantId, payload)
            return "redirect:/employees"
        } catch (e: Exception) {
            addCommonAttributesForCurrentTenant(model, "Create Employee")
            model.addAttribute("pageTitle", "Create Employee")
            model.addAttribute("error", e.message)
            return "employees/form"
        }
    }

    @PostMapping("/{id}")
    fun updateEmployee(@PathVariable id: Long, @ModelAttribute payload: UpdateEmployeePayload, model: Model): String {
        try {
            val tenantId = tenantId()
            employeeService.updateEmployee(tenantId, id, payload)
            return "redirect:/employees"
        } catch (e: Exception) {
            addCommonAttributesForCurrentTenant(model, "Edit Employee")
            model.addAttribute("pageTitle", "Edit Employee")
            model.addAttribute("error", e.message)
            
            val tenantId = tenantId()
            val employee = employeeService.getEmployee(tenantId, id)
            model.addAttribute("employee", employee)
            
            return "employees/form"
        }
    }

    private fun generateCsvContent(employees: List<com.respiroc.employees.domain.model.Employee>): String {
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