package org.example.docuementplease.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.example.docuementplease.domain.DocumentInputResponse;
import org.example.docuementplease.domain.DocumentOutputResponse;
import org.example.docuementplease.domain.PaymentHistoryResponse;
import org.example.docuementplease.domain.User;
import org.example.docuementplease.exceptionHandler.DocumentSaveException;
import org.example.docuementplease.service.UserService;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.SimpleTimeZone;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
    @CrossOrigin(origins = "*")
    @GetMapping("/user_total_info")
    public ResponseEntity<?> getMemberInfo() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "로그인 API", description = "로그인 시도시, 실제 DB에 있는지 확인후 반환해주는 API 입니다.")
    @GetMapping("/login")
    @CrossOrigin(origins = "*")
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
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        User registered = userService.registerNewUserAccount(user);
        return ResponseEntity.ok(registered);
    }

    @Operation(summary = "회원탈퇴 API", description = "회원을 탈퇴하는 API 입니다.")
    @DeleteMapping("/delete_user")
    @CrossOrigin(origins = "*")
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
    @PutMapping("/update_user_paid_tickets")
    @CrossOrigin(origins = "*")
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
    @PutMapping("/update_user_daily_tickets")
    @CrossOrigin(origins = "*")
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
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> duplicateConfirmation(@RequestParam(value = "user_name") String user_name) {
        if (userService.hasUserID(user_name)) {
            throw new DataIntegrityViolationException("사용자 이름이 이미 존재합니다.");
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "문서 input 저장 API", description = "문서 input 저장 API 입니다.")
    @PostMapping("/save_doc_input")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> saveDocInput(
            @RequestParam(value = "user_name") String user_name,
            @RequestParam(value = "type") String type,
            @RequestParam(value = "target") String target,
            @RequestParam(value = "amount") int amount,
            @RequestParam(value = "text") String text
    ) {
        try {
            Long id = userService.saveDocInput(user_name, type, target, text, amount);
            return ResponseEntity.ok().body("성공적으로 저장하였습니다. doc_id는 " + id + " 입니다.");
        } catch (DocumentSaveException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "문서 이름, 내용 저장 API", description = "문서 이름, 내용 저장 API 입니다.")
    @PostMapping("/save_doc_output")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> saveDocOutput(
            @RequestParam(value = "doc_id") int doc_id,
            @RequestParam(value = "document_name") String document_name,
            @RequestParam(value = "content") String content,
            @RequestParam(value = "user_name") String user_name
    ) {
        try {
            userService.saveDocOutput((long) doc_id, document_name, content, user_name);
            return ResponseEntity.ok().body("성공적으로 저장하였습니다.");
        } catch (DocumentSaveException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }



    @Operation(summary = "비밀번호 변경 API", description = "비밀번호 변경하는 API입니다. 만약 전과 똑같다면, 같은 비밀번호로 만들 수 없다고 반환해줍니다.")
    @PutMapping("/change_password")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> changePassword(
            @RequestParam(value = "new_password") String new_password,
            @RequestParam(value = "user_name") String user_name
    ) {
        try {
            userService.changePassword(user_name, new_password);
            return ResponseEntity.status(HttpStatus.OK).body("정상적으로 변경하였습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @Operation(summary = "유저의 무료 티켓 수 변경 API", description = "유저의 소모할 무료 티켓을 입력하고, 티켓 수를 줄이는 API 입니다.")
    @PutMapping("/update_free_user_tickets")
    @CrossOrigin(origins = "*")
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
    @CrossOrigin(origins = "*")
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
    @CrossOrigin(origins = "*")
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
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> DailyFreeTickets(
            @RequestParam(value = "user_name") String user_name) {
        try {
            int tickets = userService.returnDailyFreeAsk(user_name);
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "input 카테고리별 문서 반환 API", description = "input 문서를 카테고리별로 반환해주는 API 입니다.")
    @GetMapping("/category_doc_input")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> categoryDocInput(
            @RequestParam(value = "user_name") String user_name,
            @RequestParam(value = "type") String type) {
        try {
            List<DocumentInputResponse> documents = userService.returnDocForInput(user_name, type);
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @Operation(summary = "output 카테고리별 문서 반환 API", description = "output 문서를 카테고리별로 반환해주는 API 입니다.")
    @GetMapping("/category_doc_output")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> categoryDocOutput(
            @RequestParam(value = "user_name") String user_name,
            @RequestParam(value = "type") String type) {
        try {
            List<DocumentOutputResponse> documents = userService.returnDocForOutput(user_name, type);
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @Operation(summary = "아이디 찾기 API", description = "사용자의 아이디를 반환해주는 API 입니다.")
    @GetMapping("/find_id")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> findId(
            @RequestParam(value = "user_email") String email) {
        try {
            String username = userService.findId(email);
            return ResponseEntity.ok(username);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "문서 삭제 API", description = "원하는 문서를 삭제해주는 API입니다.")
    @DeleteMapping("/delete_document")
    public ResponseEntity<?> deleteDocument(
            @RequestParam(value = "user_name") String user_name,
            @RequestParam(value = "doc_name") String name) {
        try {
            userService.deleteDoc(user_name, name);
            return ResponseEntity.ok("delete success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "프로필 업로드 API", description = "프로필 업로드 API 입니다.")
    @PostMapping(value = "/upload_profile", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadProfile(
            @Parameter(name = "file", description = "업로드 사진 데이터")
            @RequestParam(value = "file") MultipartFile file,
            @RequestParam(value = "user_name") String user_name
    ) {
        try {
            userService.saveProfileImage(file, user_name);
            return ResponseEntity.ok("저장 완료");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "프로필 업로드 API", description = "프로필 업로드 API 입니다.")
    @PutMapping(value = "/update_profile", consumes = "multipart/form-data")
    public ResponseEntity<?> updateProfile(
            @Parameter(name = "file", description = "업로드 사진 데이터")
            @RequestParam(value = "file") MultipartFile file,
            @RequestParam(value = "user_name") String user_name
    ) {
        try {
            userService.updateProfileImage(file, user_name);
            return ResponseEntity.ok("수정 완료");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "프로필 삭제 API", description = "프로필 삭제 API 입니다.")
    @DeleteMapping(value = "/delete_profile")
    public ResponseEntity<?> deleteProfile(
            @RequestParam(value = "user_name") String user_name
    ) {
        try {
            userService.deleteProfileImage(user_name);
            return ResponseEntity.ok("삭제가 완료되었습니다.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "프로필 반환 API", description = "프로필 반환 API 입니다.")
    @GetMapping("/get_profile")
    public ResponseEntity<?> getProfile(
            @RequestParam(value = "user_name") String user_name
    ) {
        try {
            Resource file = userService.loadProfileImage(user_name);
            String contentType = Files.probeContentType(Paths.get(file.getURI()));
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                    .body(file);
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/update_count")
    public ResponseEntity<?> updateDocumentCreateCount(
            @RequestParam(value = "user_name") String user_name
    ) {
        try {
            int count = userService.updateDocumentCreateCount(user_name);
            return ResponseEntity.ok(count);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @Operation(summary = "결제내역 저장 API", description = "결제 내역을 저장해주는 API 입니다.")
    @PostMapping("/save_payment")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> savePayment(
            @RequestParam(value = "user_name") String username,
            @RequestParam(value = "tickets") int tickets,
            @RequestParam(value = "price") int price) {
        try {
            Long id = userService.savePayment(username, tickets, price);
            return ResponseEntity.ok().body("성공적으로 저장하였습니다. Payment_id는 " + id + " 입니다.");
        } catch (DocumentSaveException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/document_create_count")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> getDocumentCount(
            @RequestParam(value = "user_name") String user_name
    ) {
        return ResponseEntity.ok(userService.getDocumentCreateCount(user_name));
    }

    @Operation(summary = "사용자 결제 내역 반환 API", description = "사용자의 결제 내역을 반환해주는 API 입니다.")
    @GetMapping("/return_payment")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> returnPayment(
            @RequestParam(value = "user_name") String username) {
        try {
            List<PaymentHistoryResponse> paymentHistory = userService.returnUserPayment(username);
            return ResponseEntity.ok(paymentHistory);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @Operation(summary = "무료 티켓 증가 API", description = "사용자의 무료 티켓 수를 증가시키는 API 입니다.")
    @PutMapping("/plus_tickets")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> plusTickets(
            @RequestParam(value = "username") String username) {
        try {
            int tickets = userService.plusFreeTickets(username);
            return ResponseEntity.ok("남은 무료 티켓 수는 " + tickets + "개 입니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @Operation(summary = "유저 닉네임 변경 API", description = "유저 닉네임을 변경하는 API 입니다.")
    @PutMapping("/change_nick_name")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> changeNickName(
            @RequestParam(value = "nick_name") String nick_name,
            @RequestParam(value = "user_name") String user_name
    ) {
        try {
            userService.changeNickName(user_name, nick_name);
            return ResponseEntity.status(HttpStatus.OK).body("정상적으로 변경하였습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "유저 닉네임 반환 API", description = "유저의 닉네임을 반환해주는 API 입니다.")
    @GetMapping("/get_nickname")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> GetNickname(
            @RequestParam(value = "nickname") String nickname) {
        try {
            String nick_name = userService.returnGetNickname(nickname);
            return ResponseEntity.ok(nick_name);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "이메일 중복확인 API", description = "회원가입 할때, 이메일 중복검사 하는 API 입니다.")
    @GetMapping("/duplicate_email")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> duplicateEmail(@RequestParam(value = "email") String email) {
        if (userService.hasEmail(email)) {
            throw new DataIntegrityViolationException("이메일이 이미 존재합니다.");
        }
        return ResponseEntity.ok().build();
    }

}

