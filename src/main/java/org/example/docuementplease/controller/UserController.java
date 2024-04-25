package org.example.docuementplease.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.example.docuementplease.domain.Documents;
import org.example.docuementplease.domain.User;
import org.example.docuementplease.service.DocumentService;
import org.example.docuementplease.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class UserController {
    private final UserService userService;
    private final DocumentService documentService;

    public UserController(UserService userService, DocumentService documentService) {
        this.userService = userService;
        this.documentService = documentService;
    }

    @Operation(summary = "모든 사용자 정보 조회", description = "등록된 모든 사용자의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 사용자 정보를 조회함",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = User.class))
                    )
            ),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 접근"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/user_total_info")
    public ResponseEntity<?> getMemberInfo() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "회원가입 API", description = "새로운 회원을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 회원가입 완료", content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 접근"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PutMapping("/join")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        User registered = userService.registerNewUserAccount(user);
        return ResponseEntity.ok(registered);
    }

    @Operation(summary = "회원탈퇴 API", description = "회원을 탈퇴하는 API 입니다.")
    @DeleteMapping("/delete_user")
    public ResponseEntity<?> deleteUser(
            @RequestParam(value = "user_name") String userName) {
        userService.deleteUser(userName);
        Optional<User> user = userService.findUserbyUsername(userName);
        if (user.isPresent()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok("delete success");
        }
    }

    @Operation(summary = "유저의 유료 티켓 수 변경 API", description = "유저의 소모할 무료 티켓을 입력하고, 티켓 수를 줄이는 API 입니다.")
    @PostMapping("/updateUserTickets")
    public ResponseEntity<?> updateUserTickets(
            @RequestParam(value = "user_name") String user_name,
            @RequestParam(value = "usedTicketCount") int usedTicketCount) {
        Optional<User> user = userService.findUserbyUsername(user_name);
        if(user.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            int tickets = user.get().getTickets() - usedTicketCount;
            user.get().setTickets(tickets);
            userService.userSave(user.get());
            return ResponseEntity.ok("남은 티켓 개수는 " + tickets + "개 입니다.");
        }
    }

    @Operation(summary = "유저의 무료 티켓 수 변경 API", description = "유저의 소모할 무료 티켓을 입력하고, 티켓 수를 줄이는 API 입니다.")
    @PostMapping("/updateDailyUserTickets")
    public ResponseEntity<?> updateDailyUserTickets(
            @RequestParam(value = "user_name") String user_name,
            @RequestParam(value = "usedDailyTicketCount") int usedDailyTicketCount) {
        Optional<User> user = userService.findUserbyUsername(user_name);
        if(user.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            int tickets = user.get().getDaily_tickets() - usedDailyTicketCount;
            user.get().setDaily_tickets(tickets);
            userService.userSave(user.get());
            return ResponseEntity.ok("남은 무료 티켓 개수는 " + tickets + "개 입니다.");
        }
    }

    @Operation(summary = "아이디 중복확인 API", description = "회원가입 할때, 아이디 중복검사 하는 API 입니다.")
    @GetMapping("/duplicate_confirmation")
    public ResponseEntity<?> duplicateConfirmation(@RequestParam(value = "user_name") String user_name) {
        if (userService.hasUserID(user_name)) {
            throw new DataIntegrityViolationException("사용자 이름이 이미 존재합니다.");
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "문서 저장 API", description = "문서 저장 API 입니다.")
    @PostMapping("/save_document")
    public ResponseEntity<?> saveDocument(
            @RequestParam(value = "user_name") String user_name,
            @RequestParam(value = "document_name") String document_name,
            @RequestParam(value = "type") String type,
            @RequestParam(value = "content") String content,
            @RequestParam(value = "target") String target,
            @RequestParam(value = "amount") int amount,
            @RequestParam(value = "text") String text
    ) {
        Documents document = new Documents();
        document.setName(document_name);
        document.setTarget(target);
        document.setType(type);
        document.setAmount(amount);
        document.setContent(content);
        document.setText(text);

        Optional<User> user = userService.findUserbyUsername(user_name);
        if(user.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            user.get().getDocuments().add(document);
            document.getUsers().add(user.get());
            documentService.documentSave(document);
            userService.userSave(user.get());
        }

        return ResponseEntity.ok().build();
    }


}
