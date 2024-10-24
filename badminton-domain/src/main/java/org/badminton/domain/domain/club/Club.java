package org.badminton.domain.domain.club;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.badminton.domain.common.BaseTimeEntity;
import org.badminton.domain.domain.club.command.ClubUpdateCommand;
import org.badminton.domain.domain.club.info.ClubCreateInfo;
import org.badminton.domain.domain.clubmember.entity.ClubMember;
import org.badminton.domain.domain.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static org.badminton.domain.domain.club.info.ClubSummaryInfo.getMemberTierLongMap;

@Entity
@Getter
@Table(name = "club")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Club extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long clubId;

	private String clubName;

	@Column(columnDefinition = "text")
	private String clubDescription;

	private String clubImage;

	private boolean isClubDeleted;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "club")
	private List<ClubMember> clubMembers = new ArrayList<>();

	public Club(String clubName, String clubDescription, String clubImage) {
		super();
		this.clubName = clubName;
		this.clubDescription = clubDescription;
		this.clubImage = clubImage;
		isClubDeleted = false;
	}

	public Club(ClubCreateInfo clubCreateInfo) {
		this.clubId = clubCreateInfo.clubId();
		this.clubDescription = clubCreateInfo.clubDescription();
		this.clubName = clubCreateInfo.clubName();
		this.clubImage = clubCreateInfo.clubImage();
		this.isClubDeleted = false;
	}

	public void updateClub(ClubUpdateCommand clubUpdateCommand) {
		this.clubName = clubUpdateCommand.clubName();
		this.clubDescription = clubUpdateCommand.clubDescription();
		this.clubImage = clubUpdateCommand.clubImage();
	}

	public Map<Member.MemberTier, Long> getClubMemberCountByTier() {
		return getMemberTierLongMap(clubMembers);
	}

	public void doWithdrawal() {
		this.isClubDeleted = true;
	}
}
