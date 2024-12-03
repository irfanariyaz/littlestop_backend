package com.shopr.catalog.controller;

import com.shopr.catalog.model.User;
import com.shopr.catalog.request.LoginRequest;
import com.shopr.catalog.response.ApiResponse;
import com.shopr.catalog.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("${api.prefix}/admin")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000/")
public class UserController {
    private final UserService userService;

    @PostMapping("login")
    public ResponseEntity<ApiResponse> login( @RequestBody LoginRequest request){
        log.info("Login request: {}", request);
        try {
           User user = userService.login(request);
            return ResponseEntity.ok(new ApiResponse("Login Success!", user.getEmail()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }

    }

}
