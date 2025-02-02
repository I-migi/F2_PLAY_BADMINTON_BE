package org.badminton.domain.domain.league;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.badminton.domain.common.enums.MatchGenerationType;
import org.badminton.domain.domain.league.entity.League;
import org.badminton.domain.domain.league.enums.AllowedLeagueStatus;
import org.badminton.domain.domain.league.enums.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LeagueReader {
	League readLeague(String clubToken, Long leagueId);

	League readLeagueNotCanceled(Long leagueId);

	League readLeagueById(Long leagueId);

	List<League> readLeagueByMonth(String clubToken, LocalDateTime startOfMonth, LocalDateTime endOfMonth);

	List<League> readLeagueByDate(String clubToken, LocalDateTime startOfMonth, LocalDateTime endOfMonth);

	Page<League> readLeagueStatusIsNotAllAndRegionIsNotAll(
		AllowedLeagueStatus leagueStatus,
		Region region,
		LocalDate date,
		Pageable pageable
	);

	Page<League> readLeagueStatusIsAllAndRegionIsNotAll(
		Region region,
		LocalDate date,
		Pageable pageable
	);

	Page<League> readLeagueStatusIsNotAllAndRegionIsAll(
		AllowedLeagueStatus leagueStatus,
		LocalDate date,
		Pageable pageable
	);

	Page<League> readLeagueStatusIsAllAndRegionIsAll(
		LocalDate date,
		Pageable pageable
	);

	Integer getCountByClubId(Long clubId);

	MatchGenerationType getMatchGenerationTypeByLeagueId(Long leagueId);

	void checkLeagueExistIn3Hours(String memberToken, LocalDateTime leagueAt);

	Page<League> readLeagueByPageable(Pageable pageable);
}
