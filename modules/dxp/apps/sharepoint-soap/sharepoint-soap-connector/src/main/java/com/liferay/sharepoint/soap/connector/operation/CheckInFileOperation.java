/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.sharepoint.soap.connector.operation;

import com.liferay.sharepoint.soap.connector.SharepointConnection;
import com.liferay.sharepoint.soap.connector.SharepointException;
import com.liferay.sharepoint.soap.connector.internal.util.RemoteExceptionUtil;

import java.net.URL;

import java.rmi.RemoteException;

/**
 * @author Iván Zaera
 */
public class CheckInFileOperation extends BaseOperation {

	public boolean execute(
			String filePath, String comment,
			SharepointConnection.CheckInType checkInType)
		throws SharepointException {

		try {
			URL filePathURL = toURL(filePath);

			String protocolValue = String.valueOf(
				checkInType.getProtocolValue());

			return listsSoap.checkInFile(
				filePathURL.toString(), comment, protocolValue);
		}
		catch (RemoteException remoteException) {
			RemoteExceptionUtil.handleRemoteException(remoteException);

			throw new IllegalStateException();
		}
	}

}