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
package com.hack23.cia.web.impl.ui.application.views.user.committee.pagemode;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import org.tltv.gantt.Gantt;
import org.tltv.gantt.client.shared.Resolution;
import org.tltv.gantt.client.shared.Step;
import org.tltv.gantt.client.shared.SubStep;

import com.hack23.cia.model.internal.application.data.committee.impl.ViewRiksdagenCommittee;
import com.hack23.cia.model.internal.application.data.committee.impl.ViewRiksdagenCommitteeRoleMember;
import com.hack23.cia.model.internal.application.data.committee.impl.ViewRiksdagenCommitteeRoleMember_;
import com.hack23.cia.model.internal.application.system.impl.ApplicationEventGroup;
import com.hack23.cia.service.api.DataContainer;
import com.hack23.cia.web.impl.ui.application.action.ViewAction;
import com.hack23.cia.web.impl.ui.application.views.common.labelfactory.LabelFactory;
import com.hack23.cia.web.impl.ui.application.views.common.sizing.ContentRatio;
import com.hack23.cia.web.impl.ui.application.views.common.viewnames.CommitteePageMode;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Layout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * The Class CommitteeRoleGhantPageModContentFactoryImpl.
 */
@Component
public final class CommitteeRoleGhantPageModContentFactoryImpl extends AbstractCommitteePageModContentFactoryImpl {

	/** The Constant COMMITTEE. */
	private static final String COMMITTEE = "Committee:";

	/** The Constant ROLE_GHANT_NOT_IMPLEMENTED. */
	private static final String ROLE_GHANT = "Role chart";

	/**
	 * Instantiates a new committee role ghant page mod content factory impl.
	 */
	public CommitteeRoleGhantPageModContentFactoryImpl() {
		super();
	}

	@Override
	public boolean matches(final String page, final String parameters) {
		return NAME.equals(page)
				&& (!StringUtils.isEmpty(parameters) && parameters.contains(CommitteePageMode.ROLEGHANT.toString()));
	}

	@Secured({ "ROLE_ANONYMOUS", "ROLE_USER", "ROLE_ADMIN" })
	@Override
	public Layout createContent(final String parameters, final MenuBar menuBar, final Panel panel) {
		final VerticalLayout panelContent = createPanelContent();

		final String pageId = getPageId(parameters);

		final DataContainer<ViewRiksdagenCommittee, String> dataContainer = getApplicationManager()
				.getDataContainer(ViewRiksdagenCommittee.class);

		final ViewRiksdagenCommittee viewRiksdagenCommittee = dataContainer.load(pageId);

		if (viewRiksdagenCommittee != null) {

			getCommitteeMenuItemFactory().createCommitteeeMenuBar(menuBar, pageId);

			LabelFactory.createHeader2Label(panelContent,ROLE_GHANT);


			final DataContainer<ViewRiksdagenCommitteeRoleMember, String> committeeRoleMemberDataContainer = getApplicationManager()
					.getDataContainer(ViewRiksdagenCommitteeRoleMember.class);

			final List<ViewRiksdagenCommitteeRoleMember> allMembers = committeeRoleMemberDataContainer.getAllBy(ViewRiksdagenCommitteeRoleMember_.detail,
							viewRiksdagenCommittee.getEmbeddedId().getDetail());

			createRoleGhant(panelContent, allMembers);


			panel.setCaption(COMMITTEE + viewRiksdagenCommittee.getEmbeddedId().getDetail());
			getPageActionEventHelper().createPageEvent(ViewAction.VISIT_COMMITTEE_VIEW, ApplicationEventGroup.USER,
					NAME, parameters, pageId);
		}
		return panelContent;

	}


	/**
	 * Creates the role ghant.
	 *
	 * @param roleSummaryLayoutTabsheet
	 *            the role summary layout tabsheet
	 * @param assignmentList
	 *            the assignment list
	 */
	private static void createRoleGhant(final VerticalLayout roleSummaryLayoutTabsheet,
			final List<ViewRiksdagenCommitteeRoleMember> assignmentList) {

		final Comparator<ViewRiksdagenCommitteeRoleMember> compare = (o1, o2) -> o1.getFromDate().compareTo(o2.getFromDate());

		Collections.sort(assignmentList, compare);

		final Gantt createGantt = createGantt(assignmentList);
		roleSummaryLayoutTabsheet.addComponent(createGantt);
		roleSummaryLayoutTabsheet.setExpandRatio(createGantt, ContentRatio.GRID);

	}

	/**
	 * Creates the gantt.
	 *
	 * @param assignmentList
	 *            the assignment list
	 * @return the gantt
	 */
	private static Gantt createGantt(final List<ViewRiksdagenCommitteeRoleMember> assignmentList) {

		final Function<ViewRiksdagenCommitteeRoleMember, String> role = new RoleMapping();

		final Map<String, List<ViewRiksdagenCommitteeRoleMember>> assignmentListMap = assignmentList.stream()
				.collect(Collectors.groupingBy(role, TreeMap::new, Collectors.toList()));

		final Gantt gantt = new Gantt();
		gantt.setSizeFull();
		gantt.setWidth(100, Unit.PERCENTAGE);
		gantt.setHeight(100, Unit.PERCENTAGE);
		gantt.setResizableSteps(false);
		gantt.setMovableSteps(false);
		gantt.setResolution(Resolution.Week);

		if (!assignmentList.isEmpty()) {

			gantt.setStartDate(assignmentList.get(0).getFromDate());
			gantt.setEndDate(assignmentList.get(assignmentList.size() - 1).getToDate());

			for (final Entry<String, List<ViewRiksdagenCommitteeRoleMember>> entry : entriesSortedByValues(assignmentListMap)) {

				final String stepName = entry.getKey();

				final Step step = new Step();
				step.setDescription(stepName);

				final List<ViewRiksdagenCommitteeRoleMember> assignments = entry.getValue();

				final Comparator<ViewRiksdagenCommitteeRoleMember> compare = (o1, o2) -> o1.getFromDate().compareTo(o2.getFromDate());

				Collections.sort(assignments, compare);

				addViewRiksdagenCommitteeRoleMemberToStep(stepName, step, assignments);

				gantt.addStep(step);
			}
		}

		return gantt;
	}

	/**
	 * Entries sorted by values.
	 *
	 * @param map
	 *            the map
	 * @return the sorted set
	 */
	private static SortedSet<Map.Entry<String, List<ViewRiksdagenCommitteeRoleMember>>> entriesSortedByValues(
			final Map<String, List<ViewRiksdagenCommitteeRoleMember>> map) {
		final Comparator<? super Entry<String, List<ViewRiksdagenCommitteeRoleMember>>> compare = (o1, o2) -> {

			final Comparator<ViewRiksdagenCommitteeRoleMember> compare1 = (o11, o21) -> {
				final int compareDate = o11.getFromDate().compareTo(o21.getFromDate());
				if (compareDate == 0) {
					final int compareType = o11.getRoleCode().compareTo(o21.getRoleCode());
					if (compareType == 0) {
						return o11.getDetail().compareTo(o21.getDetail());
					} else {
						return compareType;
					}
				}

				return compareDate;
			};

			Collections.sort(o1.getValue(), compare1);
			Collections.sort(o2.getValue(), compare1);

			return compare1.compare(o1.getValue().get(0), o2.getValue().get(0));
		};

		final SortedSet<Map.Entry<String, List<ViewRiksdagenCommitteeRoleMember>>> sortedEntries = new TreeSet<>(compare);
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}


	/**
	 * Adds the assignment data to step.
	 *
	 * @param stepName
	 *            the step name
	 * @param step
	 *            the step
	 * @param assignments
	 *            the assignments
	 */
	private static void addViewRiksdagenCommitteeRoleMemberToStep(final String stepName, final Step step,
			final List<ViewRiksdagenCommitteeRoleMember> assignments) {

		for (final ViewRiksdagenCommitteeRoleMember assignmentData : assignments) {

			String subStepName = "";

			if (assignmentData.getRoleCode() != null) {
				final StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(assignmentData.getFirstName());
				stringBuilder.append(" ");
				stringBuilder.append(assignmentData.getLastName());
				stringBuilder.append(" (");
				stringBuilder.append(assignmentData.getParty());
				stringBuilder.append(")");
				subStepName = stringBuilder.toString();
			}

			final SubStep sameRoleSubStep = new SubStep(stepName + '.' + subStepName);

			sameRoleSubStep.setBackgroundColor("A8D999");

			if (assignmentData.getRoleCode().toLowerCase().contains("suppleant")) {
				sameRoleSubStep.setBackgroundColor("ded858");
			} else if (assignmentData.getRoleCode().toLowerCase().contains("ordförande")) {
				sameRoleSubStep.setBackgroundColor("3271c8");
			} else {
				sameRoleSubStep.setBackgroundColor("0eab76");
			}


			sameRoleSubStep.setStartDate(assignmentData.getFromDate().getTime());
			sameRoleSubStep.setEndDate(assignmentData.getToDate().getTime());

			step.addSubStep(sameRoleSubStep);
		}
	}

	/**
	 * The Class RoleMapping.
	 */
	private static final class RoleMapping implements Function<ViewRiksdagenCommitteeRoleMember, String> {

		@Override
		public String apply(final ViewRiksdagenCommitteeRoleMember t) {
			return t.getRoleCode();
		}
	}


}
