package org.badminton.infrastructure.match.service;

import org.badminton.domain.domain.league.LeagueReader;
import org.badminton.domain.domain.league.entity.League;
import org.badminton.domain.domain.match.info.SetInfo;
import org.badminton.domain.domain.match.reader.DoublesMatchReader;
import org.badminton.domain.domain.match.reader.SinglesMatchReader;
import org.badminton.domain.domain.match.service.AbstractMatchRetrieveService;
import org.badminton.domain.domain.match.service.MatchStrategy;
import org.badminton.domain.domain.match.store.DoublesMatchStore;
import org.badminton.domain.domain.match.store.SinglesMatchStore;
import org.badminton.infrastructure.match.strategy.TournamentDoublesMatchStrategy;
import org.badminton.infrastructure.match.strategy.TournamentSinglesMatchStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TournamentMatchRetrieveServiceImpl extends AbstractMatchRetrieveService {

	private final LeagueReader leagueReader;
	private final SinglesMatchReader singlesMatchReader;
	private final DoublesMatchReader doublesMatchReader;
	private final SinglesMatchStore singlesMatchStore;
	private final DoublesMatchStore doublesMatchStore;
	private final TournamentSinglesEndSetHandler tournamentSinglesEndSetHandler;
	private final TournamentSinglesBracketCreator tournamentSinglesBracketCreator;
	private final TournamentDoublesEndSetHandler tournamentDoublesEndSetHandler;
	private final TournamentDoublesBracketCreator tournamentDoublesBracketCreator;

	@Override
	@Transactional
	public MatchStrategy makeSinglesOrDoublesMatchStrategy(Long leagueId) {
		League league = findLeague(leagueId);
		return switch (league.getMatchType()) {
			case SINGLES -> new TournamentSinglesMatchStrategy(singlesMatchReader, singlesMatchStore,
				tournamentSinglesBracketCreator, tournamentSinglesEndSetHandler);
			case DOUBLES -> new TournamentDoublesMatchStrategy(doublesMatchReader, doublesMatchStore,
				tournamentDoublesEndSetHandler, tournamentDoublesBracketCreator);
		};
	}

	@Override
	@Transactional
	public SetInfo.Main retrieveSet(MatchStrategy matchStrategy, Long matchId, int setNumber) {
		return matchStrategy.retrieveSet(matchId, setNumber);
	}

	private League findLeague(Long leagueId) {
		return leagueReader.readLeagueById(leagueId);
	}

}
