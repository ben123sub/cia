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
package com.hack23.cia.service.impl;

import org.junit.Test;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.stopwords.StopwordsHandler;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

/**
 * The Class WordCountTest.
 */
public final class WordCountTest {

	/**
	 * Word count test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void wordCountTest() throws Exception {

		// http://weka.wikispaces.com/Use+WEKA+in+your+Java+code#Classification-Building

		final String html = "Det måste bli lättare att bygga. Plan- och byggreglerna behöver förenklas och processen förkortas, för att därigenom möjliggöra en ökad takt på byggandet i ett Sverige där bostadsbristen är ett allvarligt problem. Samplaneringen mellan infrastruktur och bostäder behöver förtydligas. Folkpartiet har under Alliansregeringen medverkat till en rad lagändringar för att förenkla plan- och byggprocessen, och även den nu aktuella propositionen är resultatet av ett lagstiftningsarbete som påbörjades under Alliansregeringen. Vi välkomnar att detta arbete nu leder till konkret lagstiftning. Däremot är vi kritiska till att den nuvarande regeringen inte tillräckligt grundligt har vägt in de olika aspekter som behöver uppmärksammas i lagstiftningsarbetet.";

		final Attribute input = new Attribute("html", (FastVector<String>) null);

		final FastVector inputVec = new FastVector();
		inputVec.addElement(input);

		final Instances htmlInst = new Instances("html", inputVec, 1);

		htmlInst.add(new DenseInstance(1));
		htmlInst.instance(0).setValue(0, html);


		final StringToWordVector filter = new StringToWordVector();
		final StopwordsHandler StopwordsHandler = new StopwordsHandler() {

			@Override
			public boolean isStopword(final String word) {

				return word.length() <3;
			}
		};

		final NGramTokenizer tokenizer = new NGramTokenizer();
		tokenizer.setNGramMinSize(1);
		tokenizer.setNGramMaxSize(1);
		tokenizer.setDelimiters(" \r\n\t.,;:'\"()?!'");

		filter.setTokenizer(tokenizer);

		filter.setStopwordsHandler(StopwordsHandler);
		filter.setLowerCaseTokens(true);
		// filter.setUseStoplist(true);
		filter.setOutputWordCounts(true);
		filter.setWordsToKeep(10);

		filter.setInputFormat(htmlInst);
		final Instances dataFiltered = Filter.useFilter(htmlInst, filter);

		final Instance last = dataFiltered.lastInstance();

		final int numAttributes = last.numAttributes();

		for (int i = 0; i < numAttributes; i++) {
			if (Integer.parseInt(last.toString(i)) > 1) {
				System.out.println(last.attribute(i).name() + ":"
						+ last.toString(i));
			}
		}
	}

}
