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
package com.hack23.cia.web.impl.ui.application.views.common.chartfactory;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import org.dussan.vaadin.dcharts.DCharts;
import org.dussan.vaadin.dcharts.base.elements.XYseries;
import org.dussan.vaadin.dcharts.data.DataSeries;
import org.dussan.vaadin.dcharts.options.Cursor;
import org.dussan.vaadin.dcharts.options.Options;
import org.dussan.vaadin.dcharts.options.Series;
import org.dussan.vaadin.dcharts.options.SeriesDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hack23.cia.model.internal.application.data.committee.impl.ViewRiksdagenVoteDataBallotPoliticianSummaryDaily;

/**
 * The Class ChartDataManagerImpl.
 */
@Service
public final class PoliticianDataManagerImpl implements PoliticianChartDataManager {

	/** The Constant ABSENT. */
	private static final String ABSENT = "Absent";

	/** The Constant PARTY_REBEL. */
	private static final String PARTY_REBEL = "Party Rebel";

	/** The Constant WON. */
	private static final String WON = "Won";

	/** The Constant NUMBER_BALLOTS. */
	private static final String NUMBER_BALLOTS = "Number ballots";

	/** The Constant DD_MMM_YYYY. */
	private static final String DD_MMM_YYYY = "dd-MMM-yyyy";

	/** The data chart manager. */
	@Autowired
	private GenericChartDataManager<ViewRiksdagenVoteDataBallotPoliticianSummaryDaily> dataChartManager;


	/**
	 * Instantiates a new politician data manager impl.
	 */
	public PoliticianDataManagerImpl() {
		super();
	}



	@Override
	public DCharts createPersonLineChart(final String personId) {

		final List<ViewRiksdagenVoteDataBallotPoliticianSummaryDaily> list = dataChartManager.findByValue(personId);

		final Series series = new Series().addSeries(new XYseries().setLabel(WON))
				.addSeries(new XYseries().setLabel(PARTY_REBEL)).addSeries(new XYseries().setLabel(ABSENT))
				.addSeries(new XYseries().setLabel(NUMBER_BALLOTS));

		final DataSeries dataSeries = new DataSeries().newSeries();

		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DD_MMM_YYYY, Locale.ENGLISH);

		if (list != null) {

			for (final ViewRiksdagenVoteDataBallotPoliticianSummaryDaily viewRiksdagenVoteDataBallotPoliticianSummaryDaily : list) {
				if (viewRiksdagenVoteDataBallotPoliticianSummaryDaily != null) {
					dataSeries.add(
							simpleDateFormat.format(
									viewRiksdagenVoteDataBallotPoliticianSummaryDaily.getEmbeddedId().getVoteDate()),
							viewRiksdagenVoteDataBallotPoliticianSummaryDaily.getWonPercentage());
				}
			}

			dataSeries.newSeries();

			for (final ViewRiksdagenVoteDataBallotPoliticianSummaryDaily viewRiksdagenVoteDataBallotPoliticianSummaryDaily : list) {
				if (viewRiksdagenVoteDataBallotPoliticianSummaryDaily != null) {
					dataSeries.add(
							simpleDateFormat.format(
									viewRiksdagenVoteDataBallotPoliticianSummaryDaily.getEmbeddedId().getVoteDate()),
							viewRiksdagenVoteDataBallotPoliticianSummaryDaily.getRebelPercentage());
				}
			}

			dataSeries.newSeries();
			for (final ViewRiksdagenVoteDataBallotPoliticianSummaryDaily viewRiksdagenVoteDataBallotPoliticianSummaryDaily : list) {
				if (viewRiksdagenVoteDataBallotPoliticianSummaryDaily != null) {
					dataSeries.add(
							simpleDateFormat.format(
									viewRiksdagenVoteDataBallotPoliticianSummaryDaily.getEmbeddedId().getVoteDate()),
							viewRiksdagenVoteDataBallotPoliticianSummaryDaily.getPoliticianPercentageAbsent());
				}
			}

			dataSeries.newSeries();
			for (final ViewRiksdagenVoteDataBallotPoliticianSummaryDaily viewRiksdagenVoteDataBallotPoliticianSummaryDaily : list) {
				if (viewRiksdagenVoteDataBallotPoliticianSummaryDaily != null) {
					dataSeries.add(
							simpleDateFormat.format(
									viewRiksdagenVoteDataBallotPoliticianSummaryDaily.getEmbeddedId().getVoteDate()),
							viewRiksdagenVoteDataBallotPoliticianSummaryDaily.getNumberBallots());
				}
			}

		}

		final Cursor cursor = new Cursor().setShow(true);

		final Options options = new Options().addOption(new SeriesDefaults()).addOption(ChartOptionsImpl.INSTANCE.createAxesXYDateFloat())
				.addOption(ChartOptionsImpl.INSTANCE.createHighLighterNorth()).addOption(cursor).addOption(series)
				.addOption(ChartOptionsImpl.INSTANCE.createLegendOutside());

		return new DCharts().setDataSeries(dataSeries).setOptions(options).show();
	}


}