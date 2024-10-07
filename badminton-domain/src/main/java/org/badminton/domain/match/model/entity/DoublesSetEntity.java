package org.badminton.domain.match.model.entity;

import static org.badminton.domain.common.consts.Constants.*;

import org.badminton.domain.common.BaseTimeEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "doubles_set")
@NoArgsConstructor
public class DoublesSetEntity extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long doublesSetId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "doublesMatchId")
	private DoublesMatchEntity doublesMatch;

	private int setIndex;
	private int team1Score;
	private int team2Score;

	public DoublesSetEntity(DoublesMatchEntity doublesMatch, int setIndex) {
		this.doublesMatch = doublesMatch;
		this.setIndex = setIndex;
		this.team1Score = INITIAL_SET_SCORE;
		this.team2Score = INITIAL_SET_SCORE;
	}

	public void saveSetScore(int team1Score, int team2Score) {
		this.team1Score = team1Score;
		this.team2Score = team2Score;
	}
}
