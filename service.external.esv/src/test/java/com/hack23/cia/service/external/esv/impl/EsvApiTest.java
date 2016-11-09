/*
 * Copyright 2010 James Pether Sörling
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
package com.hack23.cia.service.external.esv.impl;

import java.util.List;
import java.util.Map;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.hack23.cia.service.external.esv.api.EsvApi;
import com.hack23.cia.service.external.esv.api.GovernmentBodyAnnualSummary;

/**
 * The Class EsvApiTest.
 */
public final class EsvApiTest extends AbstractEsvFunctionalIntegrationTest {

	/** The esv api. */
	@Autowired
	private EsvApi esvApi;

	/** The i. */
	@Rule
	public ContiPerfRule i = new ContiPerfRule();

	/**
	 * Gets the data defence ministry success test.
	 *
	 * @return the data defence ministry success test
	 */
	@Test
	@PerfTest(threads = 1, duration = 3000, warmUp = 1500)
	@Required(max = 1000, average = 800, percentile95 = 900, throughput = 2)
	public void getDataDefenceMinistrySuccessTest() {
		final Map<Integer, List<GovernmentBodyAnnualSummary>> governmentBodyAnnualSummaryData = esvApi
				.getDataPerMinistry("Försvarsdepartementet");
		assertNotNull(governmentBodyAnnualSummaryData);
		assertEquals(18, governmentBodyAnnualSummaryData.size());
		for (final List<GovernmentBodyAnnualSummary> list : governmentBodyAnnualSummaryData.values()) {
			for (final GovernmentBodyAnnualSummary governmentBodyAnnualSummary : list) {
				assertNotNull(governmentBodyAnnualSummary);
			}
		}
	}

	/**
	 * Gets the data finance ministry 1900 failure test.
	 *
	 * @return the data finance ministry 1900 failure test
	 */
	@Test
	@PerfTest(threads = 1, duration = 3000, warmUp = 1500)
	@Required(max = 1000, average = 800, percentile95 = 900, throughput = 2)
	public void getDataFinanceMinistry1900FailureTest() {
		final List<GovernmentBodyAnnualSummary> list = esvApi.getDataPerMinistryAndYear("Finansdepartementet", 1900);
		assertNotNull(list);
		assertEquals(0, list.size());
	}

	/**
	 * Gets the data finance ministry 1999 success test.
	 *
	 * @return the data finance ministry 1999 success test
	 */
	@Test
	@PerfTest(threads = 1, duration = 3000, warmUp = 1500)
	@Required(max = 1000, average = 800, percentile95 = 900, throughput = 2)
	public void getDataFinanceMinistry1999SuccessTest() {
		final List<GovernmentBodyAnnualSummary> list = esvApi.getDataPerMinistryAndYear("Finansdepartementet", 1999);
		assertNotNull(list);
		assertEquals(42, list.size());
		for (final GovernmentBodyAnnualSummary governmentBodyAnnualSummary : list) {
			assertNotNull(governmentBodyAnnualSummary);
		}
	}

	/**
	 * Gets the data finance ministry 2016 success test.
	 *
	 * @return the data finance ministry 2016 success test
	 */
	@Test
	@PerfTest(threads = 1, duration = 3000, warmUp = 1500)
	@Required(max = 1000, average = 800, percentile95 = 900, throughput = 2)
	public void getDataFinanceMinistry2016SuccessTest() {
		final List<GovernmentBodyAnnualSummary> list = esvApi.getDataPerMinistryAndYear("Finansdepartementet", 2016);
		assertNotNull(list);
		assertEquals(45, list.size());
		for (final GovernmentBodyAnnualSummary governmentBodyAnnualSummary : list) {
			assertNotNull(governmentBodyAnnualSummary);
		}
	}

	/**
	 * Gets the data finance ministry success test.
	 *
	 * @return the data finance ministry success test
	 */
	@Test
	@PerfTest(threads = 1, duration = 3000, warmUp = 1500)
	@Required(max = 1000, average = 800, percentile95 = 900, throughput = 2)
	public void getDataFinanceMinistrySuccessTest() {
		final Map<Integer, List<GovernmentBodyAnnualSummary>> governmentBodyAnnualSummaryData = esvApi
				.getDataPerMinistry("Finansdepartementet");
		assertNotNull(governmentBodyAnnualSummaryData);
		assertEquals(18, governmentBodyAnnualSummaryData.size());
		for (final List<GovernmentBodyAnnualSummary> list : governmentBodyAnnualSummaryData.values()) {
			for (final GovernmentBodyAnnualSummary governmentBodyAnnualSummary : list) {
				assertNotNull(governmentBodyAnnualSummary);
			}
		}
	}

	/**
	 * Gets the data foreign ministry success test.
	 *
	 * @return the data foreign ministry success test
	 */
	@Test
	@PerfTest(threads = 1, duration = 3000, warmUp = 1500)
	@Required(max = 1000, average = 800, percentile95 = 900, throughput = 2)
	public void getDataForeignMinistrySuccessTest() {
		final Map<Integer, List<GovernmentBodyAnnualSummary>> governmentBodyAnnualSummaryData = esvApi
				.getDataPerMinistry("Utrikesdepartementet");
		assertNotNull(governmentBodyAnnualSummaryData);
		assertEquals(18, governmentBodyAnnualSummaryData.size());
		for (final List<GovernmentBodyAnnualSummary> list : governmentBodyAnnualSummaryData.values()) {
			for (final GovernmentBodyAnnualSummary governmentBodyAnnualSummary : list) {
				assertNotNull(governmentBodyAnnualSummary);
			}
		}
	}

	/**
	 * Gets the data per government body success test.
	 *
	 * @return the data per government body success test
	 */
	@Test
	@PerfTest(threads = 1, duration = 3000, warmUp = 1500)
	@Required(max = 1000, average = 800, percentile95 = 900, throughput = 2)
	public void getDataPerGovernmentBodySuccessTest() {
		final Map<Integer, GovernmentBodyAnnualSummary> governmentBodyAnnualSummaryData = esvApi
				.getDataPerGovernmentBody("Exportkreditnämnden");
		assertNotNull(governmentBodyAnnualSummaryData);
		assertEquals(18, governmentBodyAnnualSummaryData.size());
		for (final GovernmentBodyAnnualSummary governmentBodyAnnualSummary : governmentBodyAnnualSummaryData.values()) {
			assertNotNull(governmentBodyAnnualSummary);
		}
	}

	/**
	 * Gets the data success test.
	 *
	 * @return the data success test
	 */
	@Test
	@PerfTest(threads = 1, duration = 3000, warmUp = 1500)
	@Required(max = 1000, average = 800, percentile95 = 900, throughput = 2)
	public void getDataSuccessTest() {
		final Map<Integer, List<GovernmentBodyAnnualSummary>> governmentBodyAnnualSummaryData = esvApi.getData();
		assertNotNull(governmentBodyAnnualSummaryData);
		assertEquals(18, governmentBodyAnnualSummaryData.size());
		for (final List<GovernmentBodyAnnualSummary> list : governmentBodyAnnualSummaryData.values()) {
			assertTrue(list.size() > 200);
			for (final GovernmentBodyAnnualSummary governmentBodyAnnualSummary : list) {
				assertNotNull(governmentBodyAnnualSummary);
			}
		}
	}

	/**
	 * Gets the government body names success test.
	 *
	 * @return the government body names success test
	 */
	@Test
	@PerfTest(threads = 2, duration = 3000, warmUp = 1500)
	@Required(max = 1000, average = 20, percentile95 = 50, throughput = 50)
	public void getGovernmentBodyNamesSuccessTest() {
		final List<String> list = esvApi.getGovernmentBodyNames();
		assertNotNull(list);
		assertEquals(452, list.size());
	}

	/**
	 * Gets the ministry names success test.
	 *
	 * @return the ministry names success test
	 */
	@Test
	@PerfTest(threads = 2, duration = 3000, warmUp = 1500)
	@Required(max = 1000, average = 20, percentile95 = 50, throughput = 50)
	public void getMinistryNamesSuccessTest() {
		final List<String> list = esvApi.getMinistryNames();
		assertNotNull(list);
		assertEquals(17, list.size());
	}

	
	/**
	 * Gets the government body names foreign ministry success test.
	 *
	 * @return the government body names foreign ministry success test
	 */
	@Test
	@PerfTest(threads = 2, duration = 3000, warmUp = 1500)
	@Required(max = 1000, average = 20, percentile95 = 50, throughput = 50)
	public void getGovernmentBodyNamesForeignMinistrySuccessTest() {
		final List<String> list = esvApi.getGovernmentBodyNames("Utrikesdepartementet");
		assertNotNull(list);
		assertEquals(21, list.size());
	}

}
