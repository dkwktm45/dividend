package com.example.dividend.service;

import com.example.dividend.exception.impl.AlreadExistUserException;
import com.example.dividend.exception.impl.InvalidPasswordException;
import com.example.dividend.exception.impl.NotFoundUserException;
import com.example.dividend.exception.impl.NotFoundUserIdException;
import com.example.dividend.model.Auth;
import com.example.dividend.persist.MemberRepository;
import com.example.dividend.persist.entity.MemberEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return memberRepository.findByUsername(username)
        .orElseThrow(NotFoundUserException::new);
  }

  public MemberEntity register(Auth.SignUp member) {
    boolean exists = memberRepository.existsByUsername(member.getUsername());
    if (exists) {
      throw new AlreadExistUserException();
    }
    member.setPassword(this.passwordEncoder.encode(member.getPassword()));
    return memberRepository.save(member.toEntity());
  }
  public MemberEntity authenticate(Auth.SignIn memeber) {
    var user = memberRepository.findByUsername(memeber.getUsername())
        .orElseThrow(NotFoundUserIdException::new);

    if (!this.passwordEncoder.matches(memeber.getPassword(),
        user.getPassword())) {
      throw new InvalidPasswordException();
    }

    return user;
  }
}
