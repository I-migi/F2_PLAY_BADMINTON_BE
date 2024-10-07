package org.badminton.api.config.security;

import java.io.Serializable;

import org.badminton.api.member.oauth2.dto.CustomOAuth2Member;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class ClubPermissionEvaluator implements PermissionEvaluator {

	@Override
	public boolean hasPermission(Authentication auth, Object targetDomainObject, Object permission) {
		if ((auth == null) || !(auth.getPrincipal() instanceof CustomOAuth2Member)) {
			return false;
		}
		CustomOAuth2Member member = (CustomOAuth2Member)auth.getPrincipal();
		Long clubId = (Long)targetDomainObject;
		String requiredRole = (String)permission;

		String userRole = member.getClubRole(clubId);
		return userRole != null && userRole.equals(requiredRole);
	}

	@Override
	public boolean hasPermission(Authentication auth, Serializable targetId, String targetType, Object permission) {
		return false;
	}

	public boolean hasClubRole(Authentication auth, Long clubId, String... roles) {
		if (!(auth.getPrincipal() instanceof CustomOAuth2Member)) {
			return false;
		}

		return auth.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.anyMatch(authority -> {
				for (String role : roles) {
					String expectedAuthority = String.format("%s:%s", clubId,
						(role.startsWith("ROLE_") ? role : "ROLE_" + role));
					if (authority.equals(expectedAuthority)) {
						return true;
					}
				}
				return false;
			});
	}
}