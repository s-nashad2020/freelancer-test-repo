package com.respiroc.employees.domain.repository

import com.respiroc.employees.domain.model.Employee
import com.respiroc.util.repository.CustomJpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface EmployeeRepository : CustomJpaRepository<Employee, Long> {

    fun findByTenantId(tenantId: Long): List<Employee>

    @Query("""
        SELECT e FROM Employee e 
        WHERE e.tenantId = :tenantId 
        AND (
            LOWER(e.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) 
            OR LOWER(e.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
            OR LOWER(e.employeeNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        )
        ORDER BY e.name ASC
    """)
    fun findByTenantIdAndSearchTerm(@Param("tenantId") tenantId: Long, @Param("searchTerm") searchTerm: String): List<Employee>

    fun findByTenantIdAndId(tenantId: Long, id: Long): Employee?

    fun findByTenantIdAndEmail(tenantId: Long, email: String): Employee?

    fun findByTenantIdAndEmployeeNumber(tenantId: Long, employeeNumber: String): Employee?

    fun existsByTenantIdAndEmail(tenantId: Long, email: String): Boolean

    fun existsByTenantIdAndEmployeeNumber(tenantId: Long, employeeNumber: String): Boolean
} 