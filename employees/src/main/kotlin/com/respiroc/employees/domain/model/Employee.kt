package com.respiroc.employees.domain.model

import com.respiroc.util.domain.address.Address
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(name = "employees")
@EntityListeners(AuditingEntityListener::class)
class Employee : Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long = -1

    @Column(name = "tenant_id", nullable = false)
    var tenantId: Long = -1

    @Size(max = 50)
    @Column(name = "employee_number", nullable = true, length = 50)
    var employeeNumber: String? = null

    @Size(max = 255)
    @Column(name = "name", nullable = false)
    lateinit var name: String

    @Email
    @Size(max = 255)
    @Column(name = "email", nullable = true)
    var email: String? = null

    @Size(max = 50)
    @Column(name = "personal_phone", nullable = true, length = 50)
    var personalPhone: String? = null

    @Size(max = 50)
    @Column(name = "work_phone", nullable = true, length = 50)
    var workPhone: String? = null

    @Column(name = "address_id", nullable = true, updatable = false, insertable = false)
    var addressId: Long? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "address_id", nullable = true)
    var address: Address? = null

    @Column(name = "date_of_birth", nullable = true)
    var dateOfBirth: LocalDate? = null

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    lateinit var createdAt: Instant

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    lateinit var updatedAt: Instant
} 