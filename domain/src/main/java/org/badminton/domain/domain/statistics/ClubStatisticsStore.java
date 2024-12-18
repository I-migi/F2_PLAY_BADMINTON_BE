package org.badminton.domain.domain.statistics;

public interface ClubStatisticsStore {
	void store(ClubStatistics clubStatistics);

	void increaseClubVisitCount(String clubToken);
}
