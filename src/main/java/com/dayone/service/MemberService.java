package com.dayone.service;

import com.dayone.exception.impl.AlreadyExistUserException;
import com.dayone.exception.impl.NoMemberException;
import com.dayone.exception.impl.PasswordMismatchException;
import com.dayone.model.Auth;
import com.dayone.persist.MemberRepository;
import com.dayone.persist.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("couldn't find user -> " + username));
    }

    public MemberEntity register(Auth.SignUp member) {
        // 회원가입을 위한 메서드
        boolean exists = this.memberRepository.existsByUsername(member.getUsername());
        if(exists) {
            log.warn("이미 존재하는 아이디입니다. -> " + member.getUsername());
            throw new AlreadyExistUserException();
        }

        member.setPassword(this.passwordEncoder.encode(member.getPassword()));
        var result = this.memberRepository.save(member.toEntity());

        return result;
    }

    public MemberEntity authenticate(Auth.SighIn member) {
        // 검증을 위한 메서드
        var user = this.memberRepository.findByUsername(member.getUsername())
                .orElseThrow(()-> new NoMemberException());

        if(!this.passwordEncoder.matches(member.getPassword(), user.getPassword())) {
            throw new PasswordMismatchException();
        }

        return user;
    }

}
