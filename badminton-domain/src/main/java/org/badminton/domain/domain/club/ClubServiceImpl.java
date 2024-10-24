package org.badminton.domain.domain.club;

import org.badminton.domain.domain.club.command.ClubCreateCommand;
import org.badminton.domain.domain.club.command.ClubUpdateCommand;
import org.badminton.domain.domain.club.info.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClubServiceImpl implements ClubService {

	private final ClubReader clubReader;
	private final ClubStore clubStore;

	@Override
	public Page<ClubCardInfo> readAllClubs(Pageable pageable) {
		Page<Club> clubsPage = clubReader.readAllClubs(pageable);
		var response = ClubPage.builder().club(clubsPage).build();
		return response.clubToPageCardInfo();
	}

	@Override
	public Page<ClubCardInfo> searchClubs(String keyword, Pageable pageable) {
		var search = ClubSearch.searchResult(keyword, pageable, clubReader);
		var club = search.getFindClubs();
		var response = ClubPage.builder().club(club).build();
		return response.clubToPageCardInfo();
	}

	@Override
	public ClubSummaryInfo readClub(Long clubId) {
		var club = clubReader.readClub(clubId);
		return ClubSummaryInfo.toClubSummaryInfo(club);
	}

	@Override
	public ClubCreateInfo createClub(ClubCreateCommand clubCreateCommand) {
		Club club = new Club(clubCreateCommand.clubName(),
			clubCreateCommand.clubDescription(),
			clubCreateCommand.clubImage());
		var createResponse = clubStore.store(club);
		return ClubCreateInfo.toClubCreateInfo(createResponse);
	}

	@Override
	public ClubUpdateInfo updateClub(ClubUpdateCommand clubUpdateCommand, Long clubId) {
		var club = clubReader.readClub(clubId);
		club.updateClub(clubUpdateCommand);
		var updated = clubStore.store(club);
		return ClubUpdateInfo.toClubUpdateInfo(updated);
	}

	@Override
	public ClubDeleteInfo deleteClub(Long clubId) {
		var club = clubReader.readClub(clubId);
		club.doWithdrawal();
		var deleted = clubStore.store(club);
		return ClubDeleteInfo.clubDeleteInfo(deleted);
	}
}
