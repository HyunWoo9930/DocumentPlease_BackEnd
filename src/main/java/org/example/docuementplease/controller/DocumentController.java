package org.example.docuementplease.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.example.docuementplease.domain.SharedDocuments;
import org.example.docuementplease.service.DocumentService;
import org.example.docuementplease.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;
import java.util.List;
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
    public ResponseEntity<?> shareDocuments(
            @RequestParam(value = "accept") Boolean accept)
    {
        try {
            List<SharedDocuments> sharedDocuments = documentService.sharedDocuments(accept);
            return ResponseEntity.ok(sharedDocuments);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
