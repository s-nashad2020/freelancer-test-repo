package com.respiroc.employees.domain.repository

import com.respiroc.employees.domain.model.Employee
import com.respiroc.util.repository.CustomJpaRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface EmployeeRepository : CustomJpaRepository<Employee, Long> {
    
    fun findByTenantId(tenantId: Long, pageable: Pageable): Page<Employee>
    
    fun findByTenantIdAndNameContainingIgnoreCase(tenantId: Long, name: String, pageable: Pageable): Page<Employee>
    
    fun findByTenantIdAndEmailContainingIgnoreCase(tenantId: Long, email: String, pageable: Pageable): Page<Employee>
    
    fun findByTenantIdAndEmployeeNumberContainingIgnoreCase(tenantId: Long, employeeNumber: String, pageable: Pageable): Page<Employee>
    
    @Query("SELECT e FROM Employee e WHERE e.tenantId = :tenantId AND (LOWER(e.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(e.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(e.employeeNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    fun findByTenantIdAndSearchTerm(@Param("tenantId") tenantId: Long, @Param("searchTerm") searchTerm: String, pageable: Pageable): Page<Employee>
    
    fun findByTenantIdAndId(tenantId: Long, id: Long): Employee?
    
    fun existsByTenantIdAndEmail(tenantId: Long, email: String): Boolean
    
    fun existsByTenantIdAndEmployeeNumber(tenantId: Long, employeeNumber: String): Boolean
    
    fun findByTenantIdAndEmail(tenantId: Long, email: String): Employee?
    
    fun findByTenantIdAndEmployeeNumber(tenantId: Long, employeeNumber: String): Employee?
} 