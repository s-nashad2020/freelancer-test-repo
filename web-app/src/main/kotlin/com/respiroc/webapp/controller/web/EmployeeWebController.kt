package com.respiroc.webapp.controller.web

import com.respiroc.employees.application.EmployeeService
import com.respiroc.employees.application.payload.CreateEmployeePayload
import com.respiroc.employees.application.payload.UpdateEmployeePayload
import com.respiroc.webapp.controller.BaseController
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/employees")
class EmployeeWebController(
    private val employeeService: EmployeeService
) : BaseController() {

    @GetMapping
    fun overview(
        @RequestParam(required = false) search: String?,
        model: Model
    ): String {
        addCommonAttributesForCurrentTenant(model, "Employee overview")
        model.addAttribute("pageTitle", "Employee overview")
        
        val tenantId = tenantId()
        val employees = if (search.isNullOrBlank()) {
            employeeService.getEmployees(tenantId)
        } else {
            employeeService.searchEmployees(tenantId, search)
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
} 