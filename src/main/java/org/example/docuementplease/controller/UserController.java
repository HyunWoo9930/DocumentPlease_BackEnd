package org.example.docuementplease.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.example.docuementplease.domain.User;
import org.example.docuementplease.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
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
    @GetMapping("/user_total_info")
    public ResponseEntity<?> getMemberInfo() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    //TODO : 중복 로그인 방지
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

    @Operation(summary = "유저의 티켓 수 변경 API", description = "유저의 소모할 티켓을 입력하고, 티켓 수를 줄이는 API 입니다.")
    @PostMapping("/updateUserTickets")
    public ResponseEntity<?> updateUserTickets(
            @RequestParam(value = "user_name") String userName,
            @RequestParam(value = "usedTicketCount") int usedTicketCount) {
        // 현재 유저의 티켓에서 - usedTicketCount 해야함.
        System.out.println("userName = " + userName);
        System.out.println("ticketCount = " + usedTicketCount);
        return ResponseEntity.ok().build();
    }


}
