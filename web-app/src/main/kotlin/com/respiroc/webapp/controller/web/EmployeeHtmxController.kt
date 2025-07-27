package com.respiroc.webapp.controller.web

import com.respiroc.employees.application.EmployeeService
import com.respiroc.webapp.controller.BaseController
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/htmx/employees")
class EmployeeHtmxController(
    private val employeeService: EmployeeService
) : BaseController() {

    @GetMapping("/search")
    fun searchEmployees(
        @RequestParam(required = false) search: String?,
        model: Model
    ): String {
        val tenantId = tenantId()

        val employees = if (search.isNullOrBlank()) {
            employeeService.getEmployees(tenantId)
        } else {
            employeeService.searchEmployees(tenantId, search)
        }

        model.addAttribute("employees", employees)
        return "fragments/employee-table :: table"
    }
} 