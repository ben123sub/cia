/*
 * Copyright 2014 James Pether Sörling
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *	$Id$
 *  $HeadURL$
*/
package com.hack23.cia.web.impl.ui.application.views.user.goverment.pagemode;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import com.hack23.cia.model.internal.application.data.ministry.impl.ViewRiksdagenMinistry;
import com.hack23.cia.model.internal.application.data.ministry.impl.ViewRiksdagenMinistry_;
import com.hack23.cia.model.internal.application.system.impl.ApplicationEventGroup;
import com.hack23.cia.service.api.DataContainer;
import com.hack23.cia.web.impl.ui.application.action.ViewAction;
import com.hack23.cia.web.impl.ui.application.views.common.viewnames.PageMode;
import com.hack23.cia.web.impl.ui.application.views.common.viewnames.UserViews;
import com.hack23.cia.web.impl.ui.application.views.pageclicklistener.PageItemPropertyClickListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.Layout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * The Class MinistryRankingDataGridPageModContentFactoryImpl.
 */
@Service
public final class MinistryRankingDataGridPageModContentFactoryImpl
		extends AbstractMinistryRankingPageModContentFactoryImpl {

	/** The Constant NAME. */
	public static final String NAME = UserViews.MINISTRY_RANKING_VIEW_NAME;

	/** The Constant DATAGRID. */
	private static final String DATAGRID = "Datagrid:";

	/**
	 * Instantiates a new ministry ranking data grid page mod content factory
	 * impl.
	 */
	public MinistryRankingDataGridPageModContentFactoryImpl() {
		super();
	}

	@Override
	public boolean matches(final String page, final String parameters) {
		return NAME.equals(page)
				&& (!StringUtils.isEmpty(parameters) && parameters.contains(PageMode.DATAGRID.toString()));
	}

	@Secured({ "ROLE_ANONYMOUS", "ROLE_USER", "ROLE_ADMIN" })
	@Override
	public Layout createContent(final String parameters, final MenuBar menuBar, final Panel panel) {
		final VerticalLayout panelContent = createPanelContent();

		getMenuItemFactory().createMinistryRankingMenuBar(menuBar);

		final String pageId = getPageId(parameters);

		panelContent.addComponent(createTable());

		panel.setCaption(DATAGRID + parameters);

		getPageActionEventHelper().createPageEvent(ViewAction.VISIT_MINISTRY_RANKING_VIEW, ApplicationEventGroup.USER,
				NAME, parameters, pageId);

		return panelContent;

	}

	/**
	 * Creates the table.
	 *
	 * @return the component
	 */
	private Component createTable() {
		final DataContainer<ViewRiksdagenMinistry, String> dataContainer = getApplicationManager()
				.getDataContainer(ViewRiksdagenMinistry.class);

		final BeanItemContainer<ViewRiksdagenMinistry> dataSource = new BeanItemContainer<>(ViewRiksdagenMinistry.class,
				dataContainer.getAllOrderBy(ViewRiksdagenMinistry_.currentMemberSize));

		return getGridFactory().createBasicBeanItemGrid(dataSource, "Ministries",
				new String[] { "nameId", "totalDaysServed", "currentMemberSize", "totalAssignments",
						"firstAssignmentDate", "lastAssignmentDate", "active" },
				null, "nameId", new PageItemPropertyClickListener(UserViews.MINISTRY_VIEW_NAME, "nameId"), null);

	}

}