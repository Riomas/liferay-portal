/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.vulcan.dto.converter;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.util.LocaleThreadLocal;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.core.UriInfo;

/**
 * @author Rubén Pulido
 * @author Víctor Galán
 */
public interface DTOConverterContext {

	public default Map<String, Map> getActions() {
		return new HashMap<>();
	}

	public default DTOConverterRegistry getDTOConverterRegistry() {
		return null;
	}

	public default Object getId() {
		return null;
	}

	public default Locale getLocale() {
		return LocaleThreadLocal.getDefaultLocale();
	}

	public default Optional<UriInfo> getUriInfoOptional() {
		return Optional.empty();
	}

	public default User getUser() {
		return (User)PermissionThreadLocal.getPermissionChecker();
	}

	public default long getUserId() {
		User user = getUser();

		return user.getUserId();
	}

	public default boolean isAcceptAllLanguages() {
		return true;
	}

}