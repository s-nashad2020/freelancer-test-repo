package com.respiroc.attachment.domain.repository

import com.respiroc.attachment.domain.model.VoucherReceptionDocument
import com.respiroc.util.repository.CustomJpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VoucherReceptionDocumentRepository : CustomJpaRepository<VoucherReceptionDocument, Long>