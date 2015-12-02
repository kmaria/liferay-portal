<%@ page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %>

<%@ page import="javax.portlet.PortletMode" %>

<%--
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
--%>

<%@ include file="/init.jsp" %>

<liferay-portlet:renderURL portletMode="<%= PortletMode.VIEW.toString() %>" portletName="<%= StagingConfigurationPortletKeys.STAGING_CONFIGURATION %>" var="stagingConfigurationPortletURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
	<portlet:param name="mvcRenderCommandName" value="staging" />
</liferay-portlet:renderURL>

<%
System.out.println("stagingConfigurationPortletURL: " + stagingConfigurationPortletURL);
%>
<liferay-ui:icon
	message="staging-configuration"
	method="get"
	url="<%= stagingConfigurationPortletURL %>"
	useDialog="<%= true %>"
/>