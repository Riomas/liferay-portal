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

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.sharepoint.soap.connector.SharepointConnection;
import com.liferay.sharepoint.soap.connector.SharepointException;
import com.liferay.sharepoint.soap.connector.SharepointObject;
import com.liferay.sharepoint.soap.connector.SharepointResultException;
import com.liferay.sharepoint.soap.connector.internal.util.RemoteExceptionUtil;

import com.microsoft.schemas.sharepoint.soap.CopyErrorCode;
import com.microsoft.schemas.sharepoint.soap.CopyResult;
import com.microsoft.schemas.sharepoint.soap.holders.CopyResultCollectionHolder;

import java.net.URL;

import java.rmi.RemoteException;

import java.util.List;

import org.apache.axis.holders.UnsignedIntHolder;

/**
 * @author Iván Zaera
 */
public class CopySharepointObjectOperation extends BaseOperation {

	@Override
	public void afterPropertiesSet() {
		_addFolderOperation = getOperation(AddFolderOperation.class);
		_getSharepointObjectByPathOperation = getOperation(
			GetSharepointObjectByPathOperation.class);
		_getSharepointObjectsByFolderOperation = getOperation(
			GetSharepointObjectsByFolderOperation.class);
	}

	public void execute(String path, String newPath)
		throws SharepointException {

		SharepointObject sharepointObject =
			_getSharepointObjectByPathOperation.execute(path);

		if (sharepointObject == null) {
			throw new SharepointException(
				"Unable to find Sharepoint object at " + path);
		}

		if (sharepointObject.isFile()) {
			copyFile(path, newPath);
		}
		else {
			copyFolder(path, newPath);
		}
	}

	protected void copyFile(String path, String newPath)
		throws SharepointException {

		URL pathURL = toURL(path);
		URL newPathURL = toURL(newPath);

		CopyResultCollectionHolder copyResultCollectionHolder =
			new CopyResultCollectionHolder();

		try {
			copySoap.copyIntoItemsLocal(
				pathURL.toString(), new String[] {newPathURL.toString()},
				new UnsignedIntHolder(), copyResultCollectionHolder);
		}
		catch (RemoteException remoteException) {
			RemoteExceptionUtil.handleRemoteException(remoteException);
		}

		CopyResult copyResult = copyResultCollectionHolder.value[0];

		CopyErrorCode copyErrorCode = copyResult.getErrorCode();

		if (copyErrorCode != CopyErrorCode.Success) {
			throw new SharepointResultException(
				copyErrorCode.toString(), copyResult.getErrorMessage());
		}
	}

	protected void copyFolder(String path, String newPath)
		throws SharepointException {

		createFolder(newPath);

		List<SharepointObject> sharepointObjects =
			_getSharepointObjectsByFolderOperation.execute(
				path, SharepointConnection.ObjectTypeFilter.ALL);

		for (SharepointObject sharepointObject : sharepointObjects) {
			String sharepointObjectPath = pathHelper.buildPath(
				path, sharepointObject.getName());

			String newSharepointObjectPath = pathHelper.buildPath(
				newPath, sharepointObject.getName());

			if (sharepointObject.isFile()) {
				copyFile(sharepointObjectPath, newSharepointObjectPath);
			}
			else {
				copyFolder(sharepointObjectPath, newSharepointObjectPath);
			}
		}
	}

	protected void createFolder(String folderPath) {
		try {
			String parentFolderPath = pathHelper.getParentFolderPath(
				folderPath);

			String folderName = pathHelper.getName(folderPath);

			_addFolderOperation.execute(parentFolderPath, folderName);
		}
		catch (SharepointException sharepointException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to create folder at " + folderPath,
					sharepointException);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CopySharepointObjectOperation.class);

	private AddFolderOperation _addFolderOperation;
	private GetSharepointObjectByPathOperation
		_getSharepointObjectByPathOperation;
	private GetSharepointObjectsByFolderOperation
		_getSharepointObjectsByFolderOperation;

}