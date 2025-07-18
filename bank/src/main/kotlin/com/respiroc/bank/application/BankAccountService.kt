package com.respiroc.bank.application

import com.respiroc.bank.domain.repository.BankAccountRepository
import org.springframework.stereotype.Service

@Service
class BankAccountService(private val bankAccountRepository: BankAccountRepository) {
}