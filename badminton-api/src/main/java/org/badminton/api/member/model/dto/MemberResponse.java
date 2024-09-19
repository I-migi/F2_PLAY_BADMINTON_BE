package org.badminton.api.member.model.dto;

import org.badminton.domain.member.entity.MemberEntity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 요청 DTO")
public record MemberResponse(

	@Schema(description = "회원 역할", example = "AUTHORIZATION_USER")
	String authorization,

	@Schema(description = "회원 이름", example = "이선우")
	String name,

	@Schema(description = "oAuth 로그인 이메일", example = "qosle@naver.com")
	String email,

	@Schema(description = "oAuth 제공 ID", example = "1070449979547641023123")
	String providerId
) {

	public static MemberResponse memberEntityToResponse(MemberEntity memberEntity) {
		return new MemberResponse(memberEntity.getAuthorization(), memberEntity.getName(), memberEntity.getEmail(),
			memberEntity.getProviderId());
	}
}


