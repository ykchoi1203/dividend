package com.dayone.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";    // 인증 타입을 나타내기 위해 사용. -> Bearer xxxx.yyyy.zzzz

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
       String token = this.resolveTokenFromRequest(request);

       if(StringUtils.hasText(token) && this.tokenProvider.validateToken(token)) {
           // 토큰 유효성 검증
           Authentication authentication = this.tokenProvider.getAuthentication(token);
           SecurityContextHolder.getContext().setAuthentication(authentication);

           log.info(String.format("[%s] -> %s", this.tokenProvider.getUsername(token), request.getRequestURI()));
       }

       // 필터가 연속적으로 실행시키기 위해 filterChain 사용
       filterChain.doFilter(request, response);
    }

    private String resolveTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(TOKEN_HEADER);

        if(!ObjectUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)) {
            return token.substring(TOKEN_PREFIX.length());
        }

        return null;
    }

}
