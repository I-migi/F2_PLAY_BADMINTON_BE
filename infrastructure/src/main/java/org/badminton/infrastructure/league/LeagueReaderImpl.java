package org.badminton.infrastructure.league;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import org.badminton.domain.common.enums.MatchGenerationType;
import org.badminton.domain.common.exception.league.LeagueNotExistException;
import org.badminton.domain.domain.league.LeagueReader;
import org.badminton.domain.domain.league.entity.League;
import org.badminton.domain.domain.league.enums.AllowedLeagueStatus;
import org.badminton.domain.domain.league.enums.LeagueStatus;
import org.badminton.domain.domain.league.enums.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class LeagueReaderImpl implements LeagueReader {
	private final LeagueRepository leagueRepository;

	@Override
	public League readLeague(String clubToken, Long leagueId) {
		return leagueRepository.findByClubClubTokenAndLeagueId(clubToken, leagueId).orElseThrow(
			() -> new LeagueNotExistException(clubToken, leagueId));
	}

	@Override
	public League readLeagueById(Long leagueId) {
		return leagueRepository.findById(leagueId)
			.orElseThrow(() -> new LeagueNotExistException(leagueId));
	}

	@Override
	public List<League> readLeagueByMonth(String clubToken, LocalDateTime startOfMonth, LocalDateTime endOfMonth) {
		return leagueRepository.findAllByClubClubTokenAndLeagueAtBetween(clubToken, startOfMonth, endOfMonth);
	}

	@Override
	public List<League> readLeagueByDate(String clubToken, LocalDateTime startOfMonth, LocalDateTime endOfMonth) {
		return leagueRepository.findAllByClubClubTokenAndLeagueAtBetween(clubToken, startOfMonth, endOfMonth);
	}

	@Override
	public Integer getCountByClubId(Long clubId) {
		return leagueRepository.countByClubClubIdAndLeagueStatus(clubId, LeagueStatus.RECRUITING_COMPLETED);
	}

	@Override
	public MatchGenerationType getMatchGenerationTypeByLeagueId(Long leagueId) {
		return leagueRepository.getMatchGenerationTypeByLeagueId(leagueId).orElseThrow(
			() -> new LeagueNotExistException(leagueId)
		);
	}

	// TODO: 지역 필터링 기능 구현
	@Override
	public Page<League> readOngoingAndUpcomingLeagueByDate(AllowedLeagueStatus leagueStatus, Region region,
		LocalDate date, Pageable pageable) {
		LocalDateTime startOfDay = date.atStartOfDay();
		LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

		if (leagueStatus.getStatus() == LeagueStatus.PLAYING || leagueStatus.getStatus() == LeagueStatus.RECRUITING) {
			return leagueRepository.findAllByLeagueAtBetweenAndLeagueStatus(startOfDay, endOfDay,
				leagueStatus.getStatus(), pageable);
		}
		return leagueRepository.findAllByLeagueAtBetweenAndLeagueStatusNotIn(startOfDay, endOfDay, Arrays.asList(
			LeagueStatus.CANCELED, LeagueStatus.FINISHED), pageable);
	}
}
