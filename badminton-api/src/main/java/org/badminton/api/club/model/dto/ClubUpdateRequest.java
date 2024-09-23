package org.badminton.api.club.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClubUpdateRequest {

	@NotBlank(message = "동호회 이름은 필수 입력 항목입니다.")
	@Size(min = 2, max = 20)
	private String clubName;

	@NotBlank
	@Size(max = 1000, message = "동호회 소개란에는 최대 1000자까지 입력할 수 있습니다.")
	private String clubDescription;

	private String clubImage;

}
