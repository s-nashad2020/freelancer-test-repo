package com.respiroc.webapp.controller.rest

import com.respiroc.attachment.application.VoucherReceptionService
import com.respiroc.tenant.application.TenantService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import java.util.Base64

@RestController
class VoucherReceptionRestController(
    private val voucherReceptionService: VoucherReceptionService,
    private val tenantService: TenantService
) {

    private val logger = LoggerFactory.getLogger(VoucherReceptionRestController::class.java)

    data class EmailDocumentRequest(
        val filename: String,
        val mimeType: String,
        val fileData: String, // base64 encoded
        val senderEmail: String
    )

    // Cloudflare worker in index.js calls this
    @PostMapping("/api/voucher-reception")
    fun receiveDocumentFromEmail(
        @RequestHeader("X-Tenant-Slug") tenantSlug: String,
        @RequestBody request: EmailDocumentRequest
    ): ResponseEntity<Map<String, Any>> {
        logger.info("Receiving Voucher Receipt for $tenantSlug")
        val tenant = tenantService.findTenantBySlug(tenantSlug)
            ?: return ResponseEntity.badRequest().body(mapOf("error" to "Company not found"))

        val fileData = try {
            Base64.getDecoder().decode(request.fileData)
        } catch (_: Exception) {
            return ResponseEntity.badRequest().body(mapOf("error" to "Invalid base64 data"))
        }

        val saved = voucherReceptionService.saveDocument(
            fileData = fileData,
            filename = request.filename,
            mimeType = request.mimeType,
            senderEmail = request.senderEmail,
            tenant = tenant
        )

        return ResponseEntity.ok(
            mapOf(
                "id" to (saved.id ?: 0),
                "filename" to saved.attachment.filename,
                "status" to "received"
            )
        )
    }
}