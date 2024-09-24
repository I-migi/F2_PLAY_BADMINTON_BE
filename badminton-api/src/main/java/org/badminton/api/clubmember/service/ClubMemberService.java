package org.badminton.api.clubmember.service;

import org.badminton.api.common.error.ErrorCode;
import org.badminton.api.common.exception.club.ClubNotExistException;
import org.badminton.api.common.exception.clubmember.ClubMemberExistInClubException;
import org.badminton.api.common.exception.member.MemberNotExistException;
import org.badminton.domain.club.entity.ClubEntity;
import org.badminton.domain.club.repository.ClubRepository;
import org.badminton.domain.clubmember.entity.ClubMemberEntity;
import org.badminton.domain.clubmember.entity.ClubMemberRole;
import org.badminton.domain.clubmember.repository.ClubMemberRepository;
import org.badminton.domain.member.entity.MemberEntity;
import org.badminton.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClubMemberService {

	private final ClubMemberRepository clubMemberRepository;
	private final ClubRepository clubRepository;
	private final MemberRepository memberRepository;

	public ClubMemberEntity joinClub(Long memberId, Long clubId) {
		ClubEntity clubEntity = clubRepository.findByClubIdAndIsClubDeletedFalse(clubId)
			.orElseThrow(() -> new ClubNotExistException(ErrorCode.RESOURCE_NOT_EXIST, clubId));

		MemberEntity memberEntity = memberRepository.findByMemberId(memberId)
			.orElseThrow(() -> new MemberNotExistException(ErrorCode.RESOURCE_NOT_EXIST, String.valueOf(memberId)));

		if (clubMemberRepository.existsByMember_MemberId(memberId)) {
			throw new ClubMemberExistInClubException(ErrorCode.MEMBER_ALREADY_EXIST_IN_CLUB, memberId);
		}

		ClubMemberEntity clubMemberEntity = new ClubMemberEntity(clubEntity, memberEntity, ClubMemberRole.ROLE_USER);

		clubMemberRepository.save(clubMemberEntity);
		return clubMemberEntity;

	}

}
