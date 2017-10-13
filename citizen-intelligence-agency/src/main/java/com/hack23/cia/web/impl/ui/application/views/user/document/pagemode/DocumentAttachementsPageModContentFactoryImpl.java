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
package com.hack23.cia.web.impl.ui.application.views.user.document.pagemode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.taskdefs.optional.ejb.WeblogicTOPLinkDeploymentTool;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

import com.hack23.cia.model.external.riksdagen.dokumentlista.impl.DocumentElement;
import com.hack23.cia.model.external.riksdagen.dokumentstatus.impl.DocumentAttachment;
import com.hack23.cia.model.external.riksdagen.dokumentstatus.impl.DocumentData;
import com.hack23.cia.model.external.riksdagen.dokumentstatus.impl.DocumentData_;
import com.hack23.cia.model.external.riksdagen.dokumentstatus.impl.DocumentStatusContainer;
import com.hack23.cia.model.external.riksdagen.dokumentstatus.impl.DocumentStatusContainer_;
import com.hack23.cia.model.external.riksdagen.utskottsforslag.impl.CommitteeProposalComponentData;
import com.hack23.cia.model.internal.application.system.impl.ApplicationEventGroup;
import com.hack23.cia.service.api.DataContainer;
import com.hack23.cia.web.impl.ui.application.action.ViewAction;
import com.hack23.cia.web.impl.ui.application.views.common.labelfactory.LabelFactory;
import com.hack23.cia.web.impl.ui.application.views.common.pagelinks.impl.ExternalAttachmentDownloadLink;
import com.hack23.cia.web.impl.ui.application.views.common.sizing.ContentRatio;
import com.hack23.cia.web.impl.ui.application.views.common.viewnames.DocumentPageMode;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Layout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.whitestein.vaadin.widgets.wtpdfviewer.WTPdfViewer;


/**
 * The Class DocumentAttachementsPageModContentFactoryImpl.
 */
@Component
public final class DocumentAttachementsPageModContentFactoryImpl extends AbstractDocumentPageModContentFactoryImpl {

	/** The Constant DOCUMENT_ATTACHMENTS. */
	private static final String DOCUMENT_ATTACHMENTS = "Document Attachments";

	/**
	 * Instantiates a new document attachements page mod content factory impl.
	 */
	public DocumentAttachementsPageModContentFactoryImpl() {
		super();
	}

	@Override
	public boolean matches(final String page, final String parameters) {
		return NAME.equals(page) && !StringUtils.isEmpty(parameters)
				&& parameters.contains(DocumentPageMode.DOCUMENTATTACHMENTS.toString());
	}

	@Secured({ "ROLE_ANONYMOUS", "ROLE_USER", "ROLE_ADMIN" })
	@Override
	public Layout createContent(final String parameters, final MenuBar menuBar, final Panel panel) {
		final VerticalLayout panelContent = createPanelContent();

		final String pageId = getPageId(parameters);

		final DataContainer<DocumentElement, String> documentElementDataContainer = getApplicationManager()
				.getDataContainer(DocumentElement.class);

		final DataContainer<DocumentStatusContainer, String> documentStatusContainerDataContainer = getApplicationManager()
				.getDataContainer(DocumentStatusContainer.class);

		getApplicationManager().getDataContainer(CommitteeProposalComponentData.class);

		final DocumentElement documentElement = documentElementDataContainer.load(pageId);

		if (documentElement != null) {

			getDocumentMenuItemFactory().createDocumentMenuBar(menuBar, pageId);

			final DocumentStatusContainer documentStatusContainer = documentStatusContainerDataContainer
					.findByQueryProperty(DocumentStatusContainer.class, DocumentStatusContainer_.document,
							DocumentData.class, DocumentData_.id, pageId);

			LabelFactory.createHeader2Label(panelContent,DOCUMENT_ATTACHMENTS);


			if (documentStatusContainer != null && documentStatusContainer.getDocumentAttachmentContainer() != null
					&& documentStatusContainer.getDocumentAttachmentContainer().getDocumentAttachmentList() != null) {

				getGridFactory().createBasicBeanItemGrid(
						panelContent, DocumentAttachment.class, documentStatusContainer.getDocumentAttachmentContainer().getDocumentAttachmentList(),
						"Document attachements", new String[] { "fileName", "fileSize", "fileType", "fileUrl" }, new String[] { "hjid" },
						null, null, null);

				final List<DocumentAttachment> documentAttachmentList = documentStatusContainer.getDocumentAttachmentContainer().getDocumentAttachmentList();

				for (final DocumentAttachment documentAttachment : documentAttachmentList) {
					
					if ("pdf".equalsIgnoreCase(documentAttachment.getFileType())) {
						WTPdfViewer wtPdfViewer = new WTPdfViewer();
						wtPdfViewer.setSizeFull();

						System.out.println("ADDED PDF");
						final StreamResource.StreamSource source = new StreamResource.StreamSource() {

							/** The Constant serialVersionUID. */
							private static final long serialVersionUID = 1L;

							@Override
							public InputStream getStream() {

								try {
									return new URL(documentAttachment.getFileUrl()).openStream();
								} catch (final IOException e) {
									return new ByteArrayInputStream(new byte[0]);
								}
							}
						};

						
						wtPdfViewer.setResource(new StreamResource(source, documentAttachment.getFileName()));
						
						panelContent.addComponent(wtPdfViewer);						
						panelContent.setExpandRatio(wtPdfViewer,ContentRatio.LARGE);
					} else {
						final VerticalLayout verticalLayout = new VerticalLayout();
						panelContent.addComponent(verticalLayout);
						panelContent.setExpandRatio(verticalLayout,ContentRatio.SMALL);
						final ExternalAttachmentDownloadLink link = new ExternalAttachmentDownloadLink(documentAttachment.getFileName(), documentAttachment.getFileType(), documentAttachment.getFileUrl());
						verticalLayout.addComponent(link);
					}
				}
			}

			panel.setContent(panelContent);
			getPageActionEventHelper().createPageEvent(ViewAction.VISIT_DOCUMENT_VIEW, ApplicationEventGroup.USER, NAME,
					parameters, pageId);
		}

		return panelContent;

	}

}
