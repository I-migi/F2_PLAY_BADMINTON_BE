package org.badminton.domain.domain.match.info;

import org.badminton.domain.domain.league.entity.LeagueParticipant;
import org.badminton.domain.domain.league.vo.Team;
import org.badminton.domain.domain.member.entity.Member;

public record MatchTeamInfo(
	String participant1MemberToken,
	String participant1Name,
	String participant1Image,
	String participant2MemberToken,
	String participant2Name,
	String participant2Image,
	int winSetCounts,
	Member.MemberTier participant1Tier,
	Member.MemberTier participant2Tier
) {

	public static MatchTeamInfo fromTeam(Team team, int winSetCounts) {
		if (team == null) {
			return null;
		}
		return new MatchTeamInfo(
			getParticipantMemberToken(team.getLeagueParticipant1()),
			getParticipantName(team.getLeagueParticipant1()),
			getParticipantImage(team.getLeagueParticipant1()),
			getParticipantMemberToken(team.getLeagueParticipant2()),
			getParticipantName(team.getLeagueParticipant2()),
			getParticipantImage(team.getLeagueParticipant2()),
			winSetCounts,
			getParticipantMemberTier(team.getLeagueParticipant1()),
			getParticipantMemberTier(team.getLeagueParticipant2())

		);
	}

	private static Member.MemberTier getParticipantMemberTier(LeagueParticipant participant) {
		return (participant != null && participant.getMember() != null)
			? participant.getMember().getTier()
			: null;
	}

	private static String getParticipantMemberToken(LeagueParticipant participant) {
		return (participant != null && participant.getMember() != null)
			? participant.getMember().getMemberToken()
			: null;
	}

	private static String getParticipantName(LeagueParticipant participant) {
		return (participant != null && participant.getMember() != null)
			? participant.getMember().getName()
			: null;
	}

	private static String getParticipantImage(LeagueParticipant participant) {
		return (participant != null && participant.getMember() != null)
			? participant.getMember().getProfileImage()
			: null;
	}
}
