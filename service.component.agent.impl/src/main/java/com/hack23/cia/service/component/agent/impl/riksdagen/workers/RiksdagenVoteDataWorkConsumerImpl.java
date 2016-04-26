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
package com.hack23.cia.service.component.agent.impl.riksdagen.workers;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hack23.cia.service.external.riksdagen.api.RiksdagenApi;

/**
 * The Class RiksdagenVoteDataWorkConsumerImpl.
 */
@Service("riksdagenVoteDataWorkConsumerImpl")
@Transactional
public final class RiksdagenVoteDataWorkConsumerImpl implements
MessageListener {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(RiksdagenVoteDataWorkConsumerImpl.class);

	/** The import service. */
	@Autowired
	private RiksdagenUpdateService updateService;

	/** The riksdagen api. */
	@Autowired
	private RiksdagenApi riksdagenApi;

	/**
	 * Instantiates a new riksdagen vote data work consumer impl.
	 */
	public RiksdagenVoteDataWorkConsumerImpl() {
		super();
	}

	@Override
	public void onMessage(final Message message) {
		final String ballotId;
		try {
			ballotId = (String) ((ObjectMessage) message).getObject();
			try {
				updateService.updateVoteDataData(riksdagenApi
						.getBallot(ballotId));
			} catch (final Exception e2) {
				LOGGER.warn("Eror loading riksdagen voteData:" + ballotId + " errorMessage:" ,e2);
			}
		} catch (final JMSException e) {
			LOGGER.warn("No Valid input",e);
		}
	}
}