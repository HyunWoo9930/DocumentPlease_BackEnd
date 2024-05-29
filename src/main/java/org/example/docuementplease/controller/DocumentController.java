package org.example.docuementplease.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.example.docuementplease.domain.DocumentInputResponse;
import org.example.docuementplease.domain.SharedDocuments;
import org.example.docuementplease.service.DocumentService;
import org.example.docuementplease.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
public class DocumentController {

    private final DocumentService documentService;
    private final UserService userService;

    public DocumentController(DocumentService documentService, UserService userService) {
        this.documentService = documentService;
        this.userService = userService;
    }

    @PutMapping("/update_is_shared")
    public ResponseEntity<?> updateIsShared(
            @RequestParam(value = "user_name") String user_name,
            @RequestParam(value = "document_name") String document_name
    ) {
        try {
            Long userId = userService.getUserId(user_name);
            documentService.updateIsShared(userId, document_name);
            return ResponseEntity.ok("공유 성공했습니다.");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "공유 문서 반환 API", description = "공유가 허용된 문서를 반환해주는 API")
    @GetMapping("/shared_documents")
    public ResponseEntity<?> getShareDocuments() {
        try {
            List<SharedDocuments> sharedDocuments = documentService.sharedDocuments();
            return ResponseEntity.ok(sharedDocuments);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "좋아요 1 증가 API", description = "좋아요 1 증가하는 API")
    @PutMapping("/add_like_count")
    public ResponseEntity<?> updateLikeCount(
            @RequestParam(value = "doc_name") String doc_name,
            @RequestParam(value = "user_name") String user_name
    ) {
        try {
            int count = documentService.updateLikeCount(doc_name, user_name);
            return ResponseEntity.ok(count);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "문서의 좋아요 수 반환 API", description = "문서의 좋아요 수 반환하는 API")
    @GetMapping("/get_like_count")
    public ResponseEntity<?> getLikeCount(
            @RequestParam(value = "doc_name") String doc_name,
            @RequestParam(value = "user_name") String user_name
    ) {
        try {
            int count = documentService.getLikeCount(doc_name, user_name);
            return ResponseEntity.ok(count);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/get_document_input")
    public ResponseEntity<?> getDocumentInput(
            @RequestParam(value = "doc_input") Long doc_input
    ) {
        try {
            DocumentInputResponse documentInput = documentService.getDocumentInput(doc_input);
            return ResponseEntity.ok(documentInput);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/update_document_name")
    public ResponseEntity<?> updateDocumentName(
            @RequestParam(value = "doc_name") String doc_name,
            @RequestParam(value = "user_name") String user_name,
            @RequestParam(value = "new_doc_name") String new_doc_name
    ) {
        try {
            documentService.updateDocumentName(doc_name, user_name, new_doc_name);
            return ResponseEntity.ok("수정이 완료되었습니다.");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/get_total_likes")
    public ResponseEntity<?> getTotalLikes(
            @RequestParam("user_name") String user_name
    ) {
        try {
            AtomicInteger totalLikes = documentService.getTotalLikes(user_name);
            return ResponseEntity.ok(totalLikes);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "환불 문서 내용 저장 API", description = "환불 문서 내용 저장 API 입니다.")
    @PostMapping("/save_refunded_doc")
    public ResponseEntity<?> saveDocOutput(
            @RequestParam(value = "send_content") String send_content,
            @RequestParam(value = "content") String content,
            @RequestParam(value = "user_name") String user_name
    ) {
        try {
            documentService.saveRefundedDoc(content, send_content, user_name);
            return ResponseEntity.ok().body("성공적으로 저장하였습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "환불 문서들 반환 API")
    @GetMapping("/get_refunded_doc")
    public ResponseEntity<?> getRefundedDoc() {
        return ResponseEntity.ok(documentService.getRefundedDocument());
    }


}
