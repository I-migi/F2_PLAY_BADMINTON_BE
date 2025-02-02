package org.badminton.infrastructure.match.repository;

import static org.badminton.domain.domain.match.vo.LeagueMatchSetRedisKey.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.badminton.domain.common.enums.MatchType;
import org.badminton.domain.domain.match.info.LeagueSetsScoreInProgressInfo;
import org.badminton.domain.domain.match.vo.LeagueMatchSetRedisKey;
import org.badminton.domain.domain.match.vo.MatchRedisKey;
import org.badminton.domain.domain.match.vo.Score;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class SetRepository {

	private final RedisTemplate<String, Object> redisTemplate2;
	private final ObjectMapper objectMapper;

	@Autowired
	private StringRedisTemplate redisTemplate;

	private HashOperations<String, String, String> hashOps;

	@PostConstruct
	private void init() {
		hashOps = redisTemplate.opsForHash();
	}

	public void setMatchSetScore(MatchType matchType, Long matchId, int setNumber, Score score) {
		hashOps.put(matchType.getDescription() + matchId, String.valueOf(setNumber),
			score.toString());
	}

	public Optional<Score> getMatchSetScore(MatchType matchType, Long matchId, int setNumber) {
		String score = hashOps.get(matchType.getDescription() + matchId, String.valueOf(setNumber));
		if (score == null) {
			return Score.emptyScore();
		}
		return Optional.of(new Score(score));
	}

	public Map<MatchRedisKey, Score> getAllScores() {
		Set<String> keys = redisTemplate.keys("*게임*");

		Map<MatchRedisKey, Score> scores = new HashMap<>();
		if (keys == null) {
			return null;
		}
		for (String key : keys) {
			log.info("*****key: {}", key);
			Map<String, String> setNumberAndScore = hashOps.entries(key);
			setNumberAndScore.forEach((field, value) -> {
				log.info("*****field: {}, value: {}", field, value);
				scores.put(new MatchRedisKey(key, field), new Score(value));
			});
		}
		return scores;
	}

	public void deleteScore(MatchRedisKey matchRedisKey) {
		hashOps.delete(matchRedisKey.getKey(), matchRedisKey.getField());
	}

	public void saveInProgressSet(LeagueMatchSetRedisKey key,
		LeagueSetsScoreInProgressInfo leagueSetsScoreInProgressInfo) {
		redisTemplate2.opsForValue()
			.set(key.getKey(), leagueSetsScoreInProgressInfo, 1,
				TimeUnit.HOURS);
	}

	public List<LeagueSetsScoreInProgressInfo> getInProgressSet(Long leagueId) {
		Set<String> keys = redisTemplate2.keys(IN_PROGRESS_MATCH_PREFIX + LEAGUE_ID_PREFIX + leagueId + "*");
		if (keys == null || keys.isEmpty()) {
			return Collections.emptyList();
		}
		List<Object> allLeagueMatches = new ArrayList<>();
		for (String key : keys) {
			Object value = redisTemplate2.opsForValue().get(key);
			if (value != null) {
				allLeagueMatches.add(value);
			}
		}
		if (allLeagueMatches.isEmpty()) {
			return Collections.emptyList();
		}
		return objectMapper.convertValue(allLeagueMatches, new TypeReference<List<LeagueSetsScoreInProgressInfo>>() {
		});
	}

	public void evictLeagueMatchSet(LeagueMatchSetRedisKey leagueMatchSetRedisKey) {
		redisTemplate2.delete(leagueMatchSetRedisKey.getKey());
	}
}
