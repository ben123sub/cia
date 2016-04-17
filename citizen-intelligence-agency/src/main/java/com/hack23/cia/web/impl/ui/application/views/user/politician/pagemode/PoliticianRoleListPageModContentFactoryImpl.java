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
package com.hack23.cia.web.impl.ui.application.views.user.politician.pagemode;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

import com.hack23.cia.model.external.riksdagen.person.impl.AssignmentData;
import com.hack23.cia.model.external.riksdagen.person.impl.PersonData;
import com.hack23.cia.model.internal.application.data.politician.impl.ViewRiksdagenPolitician;
import com.hack23.cia.service.api.DataContainer;
import com.hack23.cia.web.impl.ui.application.views.common.labelfactory.LabelFactory;
import com.hack23.cia.web.impl.ui.application.views.common.sizing.ContentRatio;
import com.hack23.cia.web.impl.ui.application.views.common.viewnames.PoliticianPageMode;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * The Class RoleListPageModContentFactoryImpl.
 */
@Component
public final class PoliticianRoleListPageModContentFactoryImpl extends AbstractPoliticianPageModContentFactoryImpl {

	/**
	 * Instantiates a new role list page mod content factory impl.
	 */
	public PoliticianRoleListPageModContentFactoryImpl() {
		super();
	}

	@Override
	public boolean matches(final String page, final String parameters) {
		return NAME.equals(page) && parameters.contains(PoliticianPageMode.ROLELIST.toString());
	}

	@Secured({ "ROLE_ANONYMOUS", "ROLE_USER", "ROLE_ADMIN" })
	@Override
	public Layout createContent(final String parameters, final MenuBar menuBar, final Panel panel) {
		final VerticalLayout panelContent = createPanelContent();

		final String pageId = getPageId(parameters);

		final DataContainer<PersonData, String> dataContainer = getApplicationManager()
				.getDataContainer(PersonData.class);

		final PersonData personData = dataContainer.load(pageId);
		if (personData != null) {

			final DataContainer<ViewRiksdagenPolitician, String> politicianDataContainer = getApplicationManager()
					.getDataContainer(ViewRiksdagenPolitician.class);

			final ViewRiksdagenPolitician viewRiksdagenPolitician = politicianDataContainer.load(personData.getId());

			getMenuItemFactory().createPoliticianMenuBar(menuBar, pageId);

			final Label createHeader2Label = LabelFactory.createHeader2Label(PoliticianPageMode.ROLELIST.toString());
			panelContent.addComponent(createHeader2Label);
			panelContent.setExpandRatio(createHeader2Label,ContentRatio.SMALL);

			final List<AssignmentData> assignmentList = personData.getPersonAssignmentData()
					.getAssignmentList();

			createRoleList(panelContent, assignmentList);

			pageCompleted(parameters, panel, pageId, viewRiksdagenPolitician);

		}
		return panelContent;

	}

	/**
	 * Creates the role list.
	 *
	 * @param roleSummaryLayoutTabsheet
	 *            the role summary layout tabsheet
	 * @param assignmentList
	 *            the assignment list
	 */
	private void createRoleList(final VerticalLayout roleSummaryLayoutTabsheet, final List<AssignmentData> assignmentList) {

		final Comparator<AssignmentData> compare = (o1, o2) -> o1.getFromDate().compareTo(o2.getFromDate());

		Collections.sort(assignmentList, compare);

		final Grid createBasicBeanItemGrid = getGridFactory()
				.createBasicBeanItemGrid(new BeanItemContainer<>(AssignmentData.class, assignmentList), "Assignments",
						new String[] { "roleCode", "assignmentType", "status", "detail", "orgCode", "fromDate",
								"toDate" },
						new String[] { "hjid", "intressentId", "orderNumber", "orgCode" }, null, null, null);
		roleSummaryLayoutTabsheet.addComponent(createBasicBeanItemGrid);

		roleSummaryLayoutTabsheet.setExpandRatio(createBasicBeanItemGrid, ContentRatio.GRID);

	}


}