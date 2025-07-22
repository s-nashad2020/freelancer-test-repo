package com.respiroc.webapp.controller.web

import com.respiroc.attachment.application.VoucherReceptionService
import com.respiroc.attachment.domain.repository.VoucherReceptionDocumentRepository
import com.respiroc.webapp.controller.BaseController
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.server.ResponseStatusException
import java.util.*


@Controller
@RequestMapping("/voucher-reception")
class VoucherReceptionWebController(
    private val voucherReceptionDocumentRepository: VoucherReceptionDocumentRepository,
    private val voucherReceptionService: VoucherReceptionService
) : BaseController() {

    @GetMapping(value = ["", "/"])
    fun overview(model: Model): String {
        val currentUser = springUser()
        val documents = voucherReceptionDocumentRepository.findAll()

        val pdfDataMap = documents.associate { doc ->
            doc.id to Base64.getEncoder().encodeToString(doc.attachment.fileData)
        }

        addCommonAttributesForCurrentTenant(model, "Voucher Reception")
        model.addAttribute("documents", documents)
        model.addAttribute("pdfDataMap", pdfDataMap)
        model.addAttribute("tenantSlug", currentUser.ctx.currentTenant?.tenantSlug)
        return "voucher-reception/overview"
    }

    @GetMapping("/document/{id}/pdf")
    fun getDocumentData(@PathVariable id: Long): ResponseEntity<String> {
        val document = voucherReceptionDocumentRepository.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found") }

        val base64Data = Base64.getEncoder().encodeToString(document.attachment.fileData)
        val dataUrl = "data:application/pdf;base64,$base64Data"
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML)
            .body("""<embed id="pdf-embed" type="application/pdf" src="$dataUrl" style="width: 100%; height: 100%; border: none;"/>""");
    }
}