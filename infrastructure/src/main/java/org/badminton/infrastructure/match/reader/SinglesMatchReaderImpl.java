package org.badminton.infrastructure.match.reader;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.badminton.domain.common.enums.MatchStatus;
import org.badminton.domain.common.enums.MatchType;
import org.badminton.domain.common.exception.match.BracketNotExistException;
import org.badminton.domain.common.exception.match.MatchNotExistException;
import org.badminton.domain.domain.league.entity.LeagueParticipant;
import org.badminton.domain.domain.match.entity.SinglesMatch;
import org.badminton.domain.domain.match.reader.SinglesMatchReader;
import org.badminton.domain.domain.match.reader.SinglesMatchRepositoryCustom;
import org.badminton.infrastructure.match.repository.SinglesMatchRepository;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SinglesMatchReaderImpl implements SinglesMatchReader {
	private final SinglesMatchRepository singlesMatchRepository;
	private final SinglesMatchRepositoryCustom singlesMatchRepositoryCustom;

	@Override
	public List<SinglesMatch> getSinglesBracket(Long leagueId) {
		List<SinglesMatch> bracketInLeague = singlesMatchRepository.findAllSinglesMatch(leagueId);
		if (bracketInLeague.isEmpty()) {
			throw new BracketNotExistException(leagueId);
		}
		return bracketInLeague;
	}

	@Override
	public boolean checkIfBracketEmpty(Long leagueId) {
		List<SinglesMatch> bracketInLeague = singlesMatchRepository.findAllByLeague_LeagueId(leagueId);
		return bracketInLeague.isEmpty();
	}

	@Override
	public SinglesMatch getSinglesMatch(Long matchId) {
		return singlesMatchRepository.findById(matchId)
			.orElseThrow(() -> new MatchNotExistException(matchId, MatchType.SINGLES));
	}

	@Override
	public SinglesMatch getSinglesMatch(Long leagueId, Long matchId) {
		return singlesMatchRepository.findByIdAndLeagueLeagueId(matchId, leagueId)
			.orElseThrow(() -> new MatchNotExistException(matchId, MatchType.SINGLES));
	}

	@Override
	public List<SinglesMatch> getSinglesMatchesByClubMember(Long clubMemberId) {
		return singlesMatchRepositoryCustom.findAllCompletedByClubMemberId(clubMemberId);
	}

	@Override
	public List<SinglesMatch> findMatchesByLeagueAndRound(Long leagueId, int currentRoundNumber) {
		return singlesMatchRepository.findAllByLeague_LeagueIdAndRoundNumber(leagueId, currentRoundNumber);
	}

	@Override
	public int findMaxRoundByLeague(Long leagueId) {
		return singlesMatchRepository.findTopRoundNumberByLeagueId(leagueId);
	}

	@Override
	public SinglesMatch findFirstMatchByLeagueId(Long leagueId) {
		return singlesMatchRepository.findFirstByLeagueLeagueIdOrderByIdAsc(leagueId);
	}

	@Override
	public boolean allMatchesFinishedForLeague(Long leagueId) {
		return singlesMatchRepository.allMatchesFinishedForLeague(leagueId);
	}

	@Override
	public boolean allMatchesNotStartedForLeague(Long leagueId) {
		List<MatchStatus> statuses = Arrays.asList(MatchStatus.NOT_STARTED, MatchStatus.BYE);
		return singlesMatchRepository.allMatchesNotStartedForLeague(leagueId, statuses);
	}

	@Override
	public boolean allRoundMatchesDone(Long leagueId, int roundNumber) {
		List<MatchStatus> statuses = List.of(MatchStatus.FINISHED, MatchStatus.BYE);
		return singlesMatchRepository.areAllMatchesFinishedOrBye(leagueId, roundNumber, statuses);
	}

	@Override
	public Optional<SinglesMatch> findMatchByLeagueParticipant(LeagueParticipant leagueParticipant) {
		return singlesMatchRepository.findByLeagueParticipant(leagueParticipant);
	}

}
