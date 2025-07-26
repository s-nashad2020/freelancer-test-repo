# Employees Module

This module provides comprehensive employee management functionality for the Respiroc application.

## Features

- **CRUD Operations**: Create, Read, Update, and Delete employees
- **Search Functionality**: Search employees by name, email, or employee number
- **Address Management**: Support for employee addresses with automatic address deduplication
- **Validation**: Comprehensive validation for all employee fields
- **Multi-tenant Support**: All operations are scoped to specific tenants

## Database Schema

The employees table includes the following fields:

- `id`: Primary key
- `tenant_id`: Foreign key to tenants table
- `employee_number`: Optional employee number (unique per tenant)
- `name`: Employee name (required)
- `email`: Email address (unique per tenant)
- `personal_phone`: Personal phone number
- `work_phone`: Work phone number
- `address_id`: Foreign key to addresses table
- `date_of_birth`: Date of birth
- `created_at`: Creation timestamp
- `updated_at`: Last update timestamp

## Usage

### Creating an Employee

```kotlin
val createPayload = CreateEmployeePayload(
    name = "John Doe",
    email = "john.doe@example.com",
    employeeNumber = "EMP001",
    personalPhone = "+1234567890",
    workPhone = "+0987654321",
    dateOfBirth = LocalDate.of(1990, 1, 1),
    addressPart1 = "123 Main St",
    city = "New York",
    postalCode = "10001",
    addressCountryCode = "US"
)

val employee = employeeService.createEmployee(tenantId, createPayload)
```

### Updating an Employee

```kotlin
val updatePayload = UpdateEmployeePayload(
    name = "John Smith",
    email = "john.smith@example.com"
)

val updatedEmployee = employeeService.updateEmployee(tenantId, employeeId, updatePayload)
```

### Searching Employees

```kotlin
val pageable = PageRequest.of(0, 20, Sort.by("name"))
val employees = employeeService.searchEmployees(tenantId, "John", pageable)
```

### Deleting an Employee

```kotlin
employeeService.deleteEmployee(tenantId, employeeId)
```

## Validation Rules

- **Name**: Required, maximum 255 characters
- **Email**: Optional, must be valid email format, maximum 255 characters, unique per tenant
- **Employee Number**: Optional, maximum 50 characters, unique per tenant
- **Phone Numbers**: Optional, maximum 50 characters each
- **Date of Birth**: Optional, valid date
- **Address Fields**: All address fields are optional, but if provided, addressPart1, city, and addressCountryCode are required

## Business Rules

1. **Unique Constraints**: Email and employee number must be unique within a tenant
2. **Address Deduplication**: Addresses are automatically deduplicated using the existing address system
3. **Tenant Isolation**: All operations are scoped to the specific tenant
4. **Soft Validation**: The system validates data but allows partial information (only name is required)

## Dependencies

- `util` module: For address management and common utilities
- Spring Boot Data JPA: For database operations
- Spring Boot Validation: For input validation
