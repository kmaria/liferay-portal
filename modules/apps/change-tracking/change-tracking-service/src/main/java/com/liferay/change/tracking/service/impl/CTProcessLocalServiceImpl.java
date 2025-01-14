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

package com.liferay.change.tracking.service.impl;

import com.liferay.change.tracking.internal.background.task.CTPublishBackgroundTaskExecutor;
import com.liferay.change.tracking.model.CTProcess;
import com.liferay.change.tracking.service.base.CTProcessLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;

import java.io.Serializable;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Daniel Kocsis
 */
@Component(
	property = "model.class.name=com.liferay.change.tracking.model.CTProcess",
	service = AopService.class
)
public class CTProcessLocalServiceImpl extends CTProcessLocalServiceBaseImpl {

	@Override
	public CTProcess addCTProcess(
			long userId, long ctCollectionId, boolean ignoreCollision,
			ServiceContext serviceContext)
		throws PortalException {

		_validate(ctCollectionId);

		long ctProcessId = counterLocalService.increment();

		CTProcess ctProcess = ctProcessPersistence.create(ctProcessId);

		User user = userLocalService.getUser(userId);

		ctProcess.setCompanyId(user.getCompanyId());
		ctProcess.setUserId(user.getUserId());

		ctProcess.setCreateDate(serviceContext.getCreateDate(new Date()));
		ctProcess.setCtCollectionId(ctCollectionId);

		Company company = companyLocalService.getCompany(user.getCompanyId());

		Map<String, Serializable> taskContextMap = new HashMap<>();

		taskContextMap.put("ctCollectionId", ctCollectionId);
		taskContextMap.put("ctProcessId", ctProcessId);
		taskContextMap.put("ignoreCollision", ignoreCollision);

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.addBackgroundTask(
				user.getUserId(), company.getGroupId(),
				String.valueOf(ctCollectionId), null,
				CTPublishBackgroundTaskExecutor.class, taskContextMap,
				serviceContext);

		ctProcess.setBackgroundTaskId(backgroundTask.getBackgroundTaskId());

		return ctProcessPersistence.update(ctProcess);
	}

	@Override
	public CTProcess deleteCTProcess(CTProcess ctProcess) {
		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.fetchBackgroundTask(
				ctProcess.getBackgroundTaskId());

		if (backgroundTask != null) {
			try {
				_backgroundTaskLocalService.deleteBackgroundTask(
					backgroundTask);
			}
			catch (PortalException pe) {
				_log.error(pe, pe);
			}
		}

		return ctProcessPersistence.remove(ctProcess);
	}

	@Override
	public CTProcess fetchLatestCTProcess(long companyId) {
		return ctProcessPersistence.fetchByCompanyId_First(
			companyId,
			OrderByComparatorFactoryUtil.create(
				"CTProcess", "createDate", false));
	}

	@Override
	public List<CTProcess> getCTProcesses(long ctCollectionId) {
		return ctProcessPersistence.findByCollectionId(ctCollectionId);
	}

	@Override
	public List<CTProcess> getCTProcesses(
		long companyId, long userId, String keywords, int status, int start,
		int end, OrderByComparator<CTProcess> orderByComparator) {

		return ctProcessFinder.findByC_U_N_D_S(
			companyId, userId, keywords, status, start, end, orderByComparator);
	}

	@Override
	public List<CTProcess> getCTProcesses(
		long companyId, long userId, String keywords,
		QueryDefinition<?> queryDefinition) {

		return ctProcessFinder.findByC_U_N_D_S(
			companyId, userId, keywords, queryDefinition.getStatus(),
			queryDefinition.getStart(), queryDefinition.getEnd(),
			queryDefinition.getOrderByComparator());
	}

	private void _validate(long ctCollectionId) throws PortalException {
		ctCollectionPersistence.findByPrimaryKey(ctCollectionId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CTProcessLocalServiceImpl.class);

	@Reference
	private BackgroundTaskLocalService _backgroundTaskLocalService;

}