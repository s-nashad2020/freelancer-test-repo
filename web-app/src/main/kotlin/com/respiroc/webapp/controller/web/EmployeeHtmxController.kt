package com.respiroc.webapp.controller.web

import com.respiroc.employees.application.EmployeeService
import com.respiroc.webapp.controller.BaseController
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
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
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "name") sortBy: String,
        @RequestParam(defaultValue = "asc") sortDir: String,
        @RequestParam(required = false) search: String?,
        model: Model
    ): String {
        val tenantId = tenantId()
        val sort = Sort.by(if (sortDir == "desc") Sort.Direction.DESC else Sort.Direction.ASC, sortBy)
        val pageable = PageRequest.of(page, size, sort)

        val employees = if (search.isNullOrBlank()) {
            employeeService.getEmployees(tenantId, pageable)
        } else {
            employeeService.searchEmployees(tenantId, search, pageable)
        }

        model.addAttribute("employees", employees)
        return "fragments/employee-table :: table"
    }
} 