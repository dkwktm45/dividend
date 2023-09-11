package com.example.dividend.web;

import com.example.dividend.model.Auth;
import com.example.dividend.persist.entity.MemberEntity;
import com.example.dividend.security.TokenProvider;
import com.example.dividend.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
  private final MemberService memberService;
  private final TokenProvider tokenProvider;
  private static final Logger logger =
      LoggerFactory.getLogger(AuthController.class);
  @PostMapping("/signup")
  public ResponseEntity<?> signup(@RequestBody Auth.SignUp request) {
    logger.info("[post] /auth/signup 요청 데이터 : {}",request.toString());
    MemberEntity result = memberService.register(request);
    return ResponseEntity.ok(result);
  }
  @PostMapping("/signin")
  public ResponseEntity<?> signin(@RequestBody Auth.SignIn request) {
    logger.info("[post] /auth/signin 요청 데이터 : {}",request.toString());
    var member = memberService.authenticate(request);
    var token = tokenProvider.generateToken(member.getUsername(),
        member.getRoles());

    return ResponseEntity.ok(token);
  }
}
