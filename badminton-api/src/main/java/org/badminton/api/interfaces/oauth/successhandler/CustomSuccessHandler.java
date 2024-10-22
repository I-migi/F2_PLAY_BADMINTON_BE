package org.badminton.api.interfaces.oauth.successhandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.badminton.api.common.exception.member.MemberNotExistException;
import org.badminton.api.interfaces.oauth.dto.CustomOAuth2Member;
import org.badminton.api.interfaces.oauth.jwt.JwtUtil;
import org.badminton.domain.domain.member.entity.MemberEntity;
import org.badminton.domain.infrastructures.member.MemberRepository;
import org.badminton.infra.clubmember.ClubMemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final ClubMemberRepository clubMemberRepository;

    @Value("${custom.server.front}")
    private String frontUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        log.info("CustomSuccessHandler onAuthenticationSuccess");

        CustomOAuth2Member customUserDetails = (CustomOAuth2Member) authentication.getPrincipal(); // 인증된 사용자 객체 가져오기

        Long memberId = customUserDetails.getMemberId();
        MemberEntity memberEntity = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new MemberNotExistException(memberId));

        memberEntity.updateLastConnection();

        String registrationId = customUserDetails.getRegistrationId();

        String oAuthAccessToken = customUserDetails.getOAuthAccessToken();

        List<String> roles = customUserDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        log.info("roles: {}", roles);

        String accessToken = jwtUtil.createAccessToken(String.valueOf(memberId), roles, registrationId,
                oAuthAccessToken);

        log.info("Extracted from JWT - registrationId: {}, oAuthAccessToken: {}",
                jwtUtil.getRegistrationId(accessToken), jwtUtil.getOAuthToken(accessToken));

        String refreshToken = jwtUtil.createRefreshToken(String.valueOf(memberId), roles, registrationId);

        memberEntity.updateRefreshToken(refreshToken);
        memberRepository.save(memberEntity);

        clearSession(request, response);

        jwtUtil.setAccessTokenHeader(response, accessToken);
        jwtUtil.setAccessTokenCookie(response, accessToken);
        jwtUtil.setRefreshTokenCookie(response, refreshToken);

        response.sendRedirect(frontUrl);

    }

    private void clearSession(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate();
        ResponseCookie jSessionIdCookie = ResponseCookie.from("JSESSIONID", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", jSessionIdCookie.toString());
    }
}


