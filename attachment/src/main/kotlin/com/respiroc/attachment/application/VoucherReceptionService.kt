package com.respiroc.attachment.application

import com.respiroc.attachment.domain.model.Attachment
import com.respiroc.attachment.domain.model.VoucherReceptionDocument
import com.respiroc.attachment.domain.repository.AttachmentRepository
import com.respiroc.attachment.domain.repository.VoucherReceptionDocumentRepository
import com.respiroc.tenant.domain.model.Tenant
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class VoucherReceptionService(
    private val voucherDocumentRepository: VoucherReceptionDocumentRepository,
    private val attachmentRepository: AttachmentRepository,
    private val attachmentService: AttachmentService
) {

    fun saveDocument(
        fileData: ByteArray,
        filename: String,
        mimeType: String,
        senderEmail: String,
        tenant: Tenant
    ): VoucherReceptionDocument {

        val (pdfBytes, pdfName, pdfMime) =
            attachmentService.convertToPdf(fileData, filename, mimeType)

        val attachment = Attachment().apply {
            this.fileData = pdfBytes
            this.filename = pdfName
            this.mimetype = pdfMime
        }
        val savedAttachment = attachmentRepository.save(attachment)

        val document = VoucherReceptionDocument().apply {
            this.attachment = savedAttachment
            this.senderEmail = senderEmail
            this.tenant = tenant
        }
        return voucherDocumentRepository.save(document)
    }
}