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

<liferay-util:buffer var="stagingConfigurationPortlet">
	<liferay-portlet:runtime portletName="<%= StagingConfigurationPortletKeys.STAGING_CONFIGURATION %>" />
</liferay-util:buffer>

<%
String taglibOnClick = renderResponse.getNamespace() + "openStagingConfigurationView()";
%>

<liferay-ui:icon
	message="staging-configuration"
	onClick="<%= taglibOnClick %>"
	url="javascript:;"
/>

<aui:script>
	function <portlet:namespace />openStagingConfigurationView() {
		Liferay.Util.Window.getWindow(
			{
				dialog: {
					bodyContent: '<%= stagingConfigurationPortlet %>',
					centered: true,
					modal: true
					toolbars: {
						footer: [
							{
								label: Liferay.Language.get('ok'),
								on: {
									click: function(event) {
										event.domEvent.preventDefault();
									}
								},
								primary: true
							},
							{
								label: Liferay.Language.get('cancel'),
								on: {
									click: function(event) {
										event.domEvent.preventDefault();
									}
								}
							}
						]
					}
				},
				title: Liferay.Language.get('staging-configuration')
			}
		);
	}
</aui:script>