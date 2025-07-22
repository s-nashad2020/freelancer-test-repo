package com.respiroc.attachment.domain.repository

import com.respiroc.attachment.domain.model.Attachment
import com.respiroc.util.repository.CustomJpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AttachmentRepository : CustomJpaRepository<Attachment, Long>
