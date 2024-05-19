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
import org.example.docuementplease.exceptionHandler.DocumentSaveException;
import org.example.docuementplease.service.DocumentService;
import org.example.docuementplease.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
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

    @Operation(summary = "로그인 API", description = "로그인 시도시, 실제 DB에 있는지 확인후 반환해주는 API 입니다.")
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestParam(value = "id") String user_name,
            @RequestParam(value = "password") String password
    ) {
        if (userService.login(user_name, password)) {
            return ResponseEntity.ok("로그인 성공");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("아이디나 비밀번호를 잘못 입력하셨습니다.");
    }

    @Operation(summary = "회원가입 API", description = "새로운 회원을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 회원가입 완료", content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 접근"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/join")
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

    @Operation(summary = "유저의 유료 티켓 수 변경 API", description = "유저의 소모할 유료 티켓을 입력하고, 티켓 수를 줄이는 API 입니다.")
    @PostMapping("/update_user_paid_tickets")
    public ResponseEntity<?> updateUserTickets(
            @RequestParam(value = "user_name") String user_name,
            @RequestParam(value = "usedPaidTicketCount") int usedTicketCount) {
        try {
            int tickets = userService.deductPaidTickets(user_name, usedTicketCount);
            return ResponseEntity.ok("남은 티켓 수는 " + tickets + "개 입니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "유저의 당일 무료 티켓 수 변경 API", description = "유저의 소모할 당일 무료 티켓을 입력하고, 티켓 수를 줄이는 API 입니다.")
    @PostMapping("/update_user_daily_tickets")
    public ResponseEntity<?> updateDailyUserTickets(
            @RequestParam(value = "user_name") String user_name,
            @RequestParam(value = "usedDailyTicketCount") int usedDailyTicketCount) {
        try {
            int tickets = userService.deductDailyTickets(user_name, usedDailyTicketCount);
            return ResponseEntity.ok("남은 티켓 수는 " + tickets + "개 입니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
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
        try {
            userService.saveDocument(user_name, document_name, type, content, target, text, amount);
            return ResponseEntity.ok().body("성공적으로 저장하였습니다.");
        } catch (DocumentSaveException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "유저의 무료 티켓 수 변경 API", description = "유저의 소모할 무료 티켓을 입력하고, 티켓 수를 줄이는 API 입니다.")
    @PostMapping("/update_free_user_tickets")
    public ResponseEntity<?> updateFreeUserTickets(
            @RequestParam(value = "user_name") String user_name,
            @RequestParam(value = "usedFreeTicketCount") int usedFreeTicketCount) {
        try {
            int tickets = userService.deductFreeTickets(user_name, usedFreeTicketCount);
            return ResponseEntity.ok("남은 티켓 수는 " + tickets + "개 입니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "유저의 무료 티켓 수 반환 API", description = "유저가 소유한 무료 티켓 수를 반환해주는 API 입니다.")
    @GetMapping("/free_user_tickets")
    public ResponseEntity<?> FreeUserTickets(
            @RequestParam(value = "user_name") String user_name) {
        try {
            int tickets = userService.returnFreeTickets(user_name);
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "유저의 유료 티켓 수 반환 API", description = "유저가 소유한 유료 티켓 수를 반환해주는 API 입니다.")
    @GetMapping("/user_paid_tickets")
    public ResponseEntity<?> UserpaidTickets(
            @RequestParam(value = "user_name") String user_name) {
        try {
            int tickets = userService.returnPaidTickets(user_name);
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "당일 무료 질문 횟수 반환 API", description = "유저에게 남은 당일 무료 질문 횟수를 반환해주는 API 입니다.")
    @GetMapping("/today_free_ask")
    public ResponseEntity<?> TodayFreeAsk(
            @RequestParam(value = "user_name") String user_name) {
        try {
            int tickets = userService.returnFreeAsk(user_name);
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "질문 클릭시 모든 항목 반환 API", description = "유저가 저장한 질문을 클릭할 시 상세 내용을 반환해주는 API 입니다.")
    @GetMapping("/get_document")
    public ResponseEntity<?> getDocument(@RequestParam(value = "user_name") String user_name) {
        try {
            List<Documents> documents = userService.returndoc(user_name);
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
