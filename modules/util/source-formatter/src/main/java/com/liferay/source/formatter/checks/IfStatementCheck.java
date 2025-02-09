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

package com.liferay.source.formatter.checks;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public abstract class IfStatementCheck extends BaseFileCheck {

	protected void checkIfClauseParentheses(
		String ifClause, String fileName, int lineNumber,
		boolean checkMissingParentheses) {

		ifClause = stripQuotes(ifClause);

		if (ifClause.matches(
				"[^()]*\\((\\(?\\w+ instanceof \\w+\\)?( \\|\\| )?)+" +
					"\\)[^()]*") &&
			!ifClause.matches("[^()]*\\([^()]*\\)[^()]*")) {

			addMessage(fileName, "Redundant parentheses", lineNumber);

			return;
		}

		if (ifClause.contains(StringPool.DOUBLE_SLASH) ||
			ifClause.contains("/*") || ifClause.contains("*/")) {

			return;
		}

		if (checkMissingParentheses) {
			_checkMissingParentheses(ifClause, fileName, lineNumber);
		}

		if (_hasRedundantParentheses(ifClause, "||", "&&") ||
			_hasRedundantParentheses(ifClause, "&&", "||")) {

			addMessage(fileName, "Redundant parentheses", lineNumber);

			return;
		}

		int x = ifClause.indexOf(StringPool.OPEN_PARENTHESIS);

		while (true) {
			x = ifClause.indexOf(StringPool.OPEN_PARENTHESIS, x + 1);

			if (x == -1) {
				return;
			}

			char previousChar = ifClause.charAt(x - 1);

			if ((previousChar != CharPool.EXCLAMATION) &&
				(previousChar != CharPool.OPEN_PARENTHESIS) &&
				(previousChar != CharPool.SPACE)) {

				continue;
			}

			if (previousChar == CharPool.SPACE) {
				String s = StringUtil.trim(ifClause.substring(0, x - 1));

				if (Character.isLetterOrDigit(s.charAt(s.length() - 1))) {
					continue;
				}
			}

			int y = x;

			while (true) {
				y = ifClause.indexOf(StringPool.CLOSE_PARENTHESIS, y + 1);

				String s = ifClause.substring(x + 1, y);

				if (getLevel(s) != 0) {
					continue;
				}

				char nextChar = ifClause.charAt(y + 1);

				if ((previousChar == CharPool.OPEN_PARENTHESIS) &&
					(nextChar == CharPool.CLOSE_PARENTHESIS)) {

					addMessage(fileName, "Redundant parentheses", lineNumber);

					return;
				}

				if (((nextChar == CharPool.CLOSE_PARENTHESIS) ||
					 (nextChar == CharPool.SPACE)) &&
					_hasRedundantParentheses(s)) {

					addMessage(fileName, "Redundant parentheses", lineNumber);

					return;
				}

				break;
			}
		}
	}

	private void _checkMissingParentheses(
		String ifClause, String fileName, int lineNumber) {

		int x = -1;

		while (true) {
			int y = ifClause.indexOf("||", x + 1);
			int z = ifClause.indexOf("&&", x + 1);

			if ((y == -1) || (z == -1)) {
				break;
			}

			String s = ifClause.substring(Math.min(y, z), Math.max(y, z));

			if (getLevel(s) == 0) {
				addMessage(fileName, "Missing parentheses", lineNumber);

				return;
			}

			x = Math.min(y, z);
		}

		outerLoop:
		while (true) {
			Matcher matcher = _methodCallPattern.matcher(ifClause);

			if (!matcher.find()) {
				break;
			}

			x = matcher.start() + 1;

			while (true) {
				x = ifClause.indexOf(StringPool.CLOSE_PARENTHESIS, x + 1);

				if (x == -1) {
					break outerLoop;
				}

				String s = ifClause.substring(matcher.start() + 1, x + 1);

				if (getLevel(s) == 0) {
					ifClause = StringUtil.replaceFirst(
						ifClause, s, StringPool.BLANK, matcher.start());

					break;
				}
			}
		}

		int previousParenthesisPos = -1;

		for (int i = 0; i < ifClause.length(); i++) {
			char c = ifClause.charAt(i);

			if ((c != CharPool.OPEN_PARENTHESIS) &&
				(c != CharPool.CLOSE_PARENTHESIS)) {

				continue;
			}

			if (previousParenthesisPos != -1) {
				String s = ifClause.substring(previousParenthesisPos + 1, i);

				if (_hasMissingParentheses(s)) {
					addMessage(fileName, "Missing parentheses", lineNumber);

					return;
				}
			}

			previousParenthesisPos = i;
		}
	}

	private boolean _hasMissingParentheses(String s) {
		if (Validator.isNull(s)) {
			return false;
		}

		boolean containsAndOperator = s.contains("&&");
		boolean containsOrOperator = s.contains("||");

		if (containsAndOperator && containsOrOperator) {
			return true;
		}

		boolean containsCompareOperator = false;

		if (s.contains(" == ") || s.contains(" != ") || s.contains(" < ") ||
			s.contains(" > ") || s.contains(" =< ") || s.contains(" => ") ||
			s.contains(" <= ") || s.contains(" >= ")) {

			containsCompareOperator = true;
		}

		boolean containsMathOperator = false;

		if (s.contains(" = ") || s.contains(" - ") || s.contains(" + ") ||
			s.contains(" & ") || s.contains(" % ") || s.contains(" * ") ||
			s.contains(" / ")) {

			containsMathOperator = true;
		}

		if (containsCompareOperator &&
			(containsAndOperator || containsOrOperator ||
			 (containsMathOperator && !s.contains(StringPool.OPEN_BRACKET)))) {

			return true;
		}

		if (s.contains(" ? ") &&
			(containsAndOperator || containsCompareOperator ||
			 containsOrOperator)) {

			return true;
		}

		return false;
	}

	private boolean _hasRedundantParentheses(String s) {
		//if (s.matches("\\w+ instanceof \\w+")) {
		//	return true;
		//}

		int x = -1;

		while (true) {
			x = s.indexOf(StringPool.SPACE, x + 1);

			if (x == -1) {
				break;
			}

			if (getLevel(s.substring(0, x)) == 0) {
				return false;
			}
		}

		return true;
	}

	private boolean _hasRedundantParentheses(
		String s, String operator1, String operator2) {

		while (true) {
			int x = s.indexOf("!(");

			if (x == -1) {
				break;
			}

			int y = x;

			while (true) {
				y = s.indexOf(")", y + 1);

				String linePart = s.substring(x, y + 1);

				if (getLevel(linePart) == 0) {
					break;
				}
			}

			s = StringUtil.replaceFirst(s, ")", StringPool.BLANK, y);
			s = StringUtil.replaceFirst(s, "!(", StringPool.BLANK, x);
		}

		String[] parts = StringUtil.split(s, operator1);

		if (parts.length < 3) {
			return false;
		}

		for (int i = 1; i < (parts.length - 1); i++) {
			String part = parts[i];

			if (part.contains(operator2)) {
				continue;
			}

			if (Math.abs(getLevel(part)) == 1) {
				return true;
			}
		}

		return false;
	}

	private static final Pattern _methodCallPattern = Pattern.compile("\\w\\(");

}