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

package com.liferay.bulk.rest.client.resource.v1_0;

import com.liferay.bulk.rest.client.dto.v1_0.TaxonomyVocabulary;
import com.liferay.bulk.rest.client.http.HttpInvoker;
import com.liferay.bulk.rest.client.pagination.Page;
import com.liferay.bulk.rest.client.problem.Problem;
import com.liferay.bulk.rest.client.serdes.v1_0.TaxonomyVocabularySerDes;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Generated;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public interface TaxonomyVocabularyResource {

	public static Builder builder() {
		return new Builder();
	}

	public Page<TaxonomyVocabulary> postSiteTaxonomyVocabulariesCommonPage(
			Long siteId,
			com.liferay.bulk.rest.client.dto.v1_0.DocumentBulkSelection
				documentBulkSelection)
		throws Exception;

	public HttpInvoker.HttpResponse
			postSiteTaxonomyVocabulariesCommonPageHttpResponse(
				Long siteId,
				com.liferay.bulk.rest.client.dto.v1_0.DocumentBulkSelection
					documentBulkSelection)
		throws Exception;

	public static class Builder {

		public Builder authentication(String login, String password) {
			_login = login;
			_password = password;

			return this;
		}

		public TaxonomyVocabularyResource build() {
			return new TaxonomyVocabularyResourceImpl(this);
		}

		public Builder endpoint(String host, int port, String scheme) {
			_host = host;
			_port = port;
			_scheme = scheme;

			return this;
		}

		public Builder header(String key, String value) {
			_headers.put(key, value);

			return this;
		}

		public Builder locale(Locale locale) {
			_locale = locale;

			return this;
		}

		public Builder parameter(String key, String value) {
			_parameters.put(key, value);

			return this;
		}

		private Builder() {
		}

		private Map<String, String> _headers = new LinkedHashMap<>();
		private String _host = "localhost";
		private Locale _locale;
		private String _login = "test@liferay.com";
		private String _password = "test";
		private Map<String, String> _parameters = new LinkedHashMap<>();
		private int _port = 8080;
		private String _scheme = "http";

	}

	public static class TaxonomyVocabularyResourceImpl
		implements TaxonomyVocabularyResource {

		public Page<TaxonomyVocabulary> postSiteTaxonomyVocabulariesCommonPage(
				Long siteId,
				com.liferay.bulk.rest.client.dto.v1_0.DocumentBulkSelection
					documentBulkSelection)
			throws Exception {

			HttpInvoker.HttpResponse httpResponse =
				postSiteTaxonomyVocabulariesCommonPageHttpResponse(
					siteId, documentBulkSelection);

			String content = httpResponse.getContent();

			_logger.fine("HTTP response content: " + content);

			_logger.fine("HTTP response message: " + httpResponse.getMessage());
			_logger.fine(
				"HTTP response status code: " + httpResponse.getStatusCode());

			try {
				return Page.of(content, TaxonomyVocabularySerDes::toDTO);
			}
			catch (Exception e) {
				_logger.log(
					Level.WARNING,
					"Unable to process HTTP response: " + content, e);

				throw new Problem.ProblemException(Problem.toDTO(content));
			}
		}

		public HttpInvoker.HttpResponse
				postSiteTaxonomyVocabulariesCommonPageHttpResponse(
					Long siteId,
					com.liferay.bulk.rest.client.dto.v1_0.DocumentBulkSelection
						documentBulkSelection)
			throws Exception {

			HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

			httpInvoker.body(
				documentBulkSelection.toString(), "application/json");

			if (_builder._locale != null) {
				httpInvoker.header(
					"Accept-Language", _builder._locale.toLanguageTag());
			}

			for (Map.Entry<String, String> entry :
					_builder._headers.entrySet()) {

				httpInvoker.header(entry.getKey(), entry.getValue());
			}

			for (Map.Entry<String, String> entry :
					_builder._parameters.entrySet()) {

				httpInvoker.parameter(entry.getKey(), entry.getValue());
			}

			httpInvoker.httpMethod(HttpInvoker.HttpMethod.POST);

			httpInvoker.path(
				_builder._scheme + "://" + _builder._host + ":" +
					_builder._port +
						"/o/bulk/v1.0/sites/{siteId}/taxonomy-vocabularies/common",
				siteId);

			httpInvoker.userNameAndPassword(
				_builder._login + ":" + _builder._password);

			return httpInvoker.invoke();
		}

		private TaxonomyVocabularyResourceImpl(Builder builder) {
			_builder = builder;
		}

		private static final Logger _logger = Logger.getLogger(
			TaxonomyVocabularyResource.class.getName());

		private Builder _builder;

	}

}