/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.argeo.slc.web.mvc.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.attachment.AttachmentsStorage;
import org.argeo.slc.core.attachment.SimpleAttachment;
import org.argeo.slc.core.test.tree.ResultAttributes;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.core.test.tree.TreeTestResultCollection;
import org.argeo.slc.dao.test.tree.TreeTestResultCollectionDao;
import org.argeo.slc.dao.test.tree.TreeTestResultDao;
import org.argeo.slc.msg.ExecutionAnswer;
import org.argeo.slc.msg.ObjectList;
import org.argeo.slc.msg.ReferenceList;
import org.argeo.slc.services.TestManagerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ResultController {

	// private final static Log log =
	// LogFactory.getLog(ServiceController.class);

	// Constants
	public final static String KEY_ANSWER = "__answer";
	protected final String FORCE_DOWNLOAD = "Content-Type: application/force-download";

	// IoC
	private TreeTestResultDao treeTestResultDao;
	private TreeTestResultCollectionDao treeTestResultCollectionDao;
	private TestManagerService testManagerService;
	private AttachmentsStorage attachmentsStorage;

	// Business Methods
	@RequestMapping("/getResult.service")
	protected TreeTestResult getResult(
			@RequestParam(value = "uuid", required = false) String uuid) {

		TreeTestResult result = treeTestResultDao.getTestResult(uuid);
		if (result == null)
			throw new SlcException("No result found for uuid " + uuid);
		return result;
	}

	@RequestMapping("/addResultToCollection.service")
	protected ExecutionAnswer addResultToCollection(
			@RequestParam String collectionId, @RequestParam String resultUuid) {
		testManagerService.addResultToCollection(collectionId, resultUuid);
		return ExecutionAnswer.ok("Execution completed properly");
	}

	@RequestMapping("/removeResultFromCollection.service")
	protected ExecutionAnswer removeResultFromCollection(
			HttpServletRequest request) {
		String collectionId = request.getParameter("collectionId");
		String[] resultUuids = request.getParameterValues("resultUuid");
		String[] attrNames = request.getParameterValues("attrName");
		String[] attrPatterns = request.getParameterValues("attrPattern");

		// Checks
		if (collectionId == null)
			throw new SlcException("A collection id must be specified");
		if (attrNames != null
				&& (attrPatterns == null || attrNames.length != attrPatterns.length))
			throw new SlcException(
					"There must be as many attrName as attrPatterns");

		// Remove specified results
		if (resultUuids != null)
			for (String resultUuid : resultUuids)
				testManagerService.removeResultFromCollection(collectionId,
						resultUuid);

		if (attrNames != null) {
			TreeTestResultCollection sourceCollection = treeTestResultCollectionDao
					.getTestResultCollection(collectionId);

			int index = 0;
			for (String attrName : attrNames) {
				String attrPattern = attrPatterns[index];// safe: checked above

				List<TreeTestResult> results = new ArrayList<TreeTestResult>(
						sourceCollection.getResults());
				for (TreeTestResult treeTestResult : results) {
					if (PatternMatchUtils.simpleMatch(attrPattern,
							treeTestResult.getAttributes().get(attrName))) {
						testManagerService.removeResultFromCollection(
								collectionId, treeTestResult.getUuid());
					}
				}
				index++;
			}
		} else {
			if (resultUuids == null) {// no specs
				// remove all
				// TODO: optimize
				TreeTestResultCollection sourceCollection = treeTestResultCollectionDao
						.getTestResultCollection(collectionId);
				List<TreeTestResult> results = new ArrayList<TreeTestResult>(
						sourceCollection.getResults());
				for (TreeTestResult treeTestResult : results) {
					testManagerService.removeResultFromCollection(collectionId,
							treeTestResult.getUuid());
				}

			}
		}
		return ExecutionAnswer.ok("Execution completed properly");
	}

	@RequestMapping("/listCollectionRefs.service")
	protected ReferenceList listCollectionRefs(HttpServletRequest request,
			HttpServletResponse response) {

		SortedSet<TreeTestResultCollection> results = treeTestResultCollectionDao
				.listCollections();

		ReferenceList referenceList = new ReferenceList();
		for (TreeTestResultCollection collection : results) {
			referenceList.getReferences().add(collection.getId());
		}
		return referenceList;
	}

	@RequestMapping("/listResultAttributes.service")
	protected ObjectList listResultAttributes(@RequestParam String id,
			Model model) {

		List<ResultAttributes> resultAttributes = treeTestResultCollectionDao
				.listResultAttributes(id);
		return new ObjectList(resultAttributes);
	}

	@RequestMapping("/listResults.service")
	@SuppressWarnings(value = { "unchecked" })
	protected ObjectList listResults(
			@RequestParam(value = "collectionId", required = false) String collectionId,
			HttpServletRequest request) {
		Map<String, String[]> parameterMap = request.getParameterMap();
		Map<String, String> attributes = new HashMap<String, String>();
		for (String parameter : parameterMap.keySet()) {
			if (parameter.startsWith("attr.")) {
				String key = parameter.substring("attr.".length());
				attributes.put(key, parameterMap.get(parameter)[0]);
			}
		}

		List<TreeTestResult> resultAttributes = treeTestResultCollectionDao
				.listResults(collectionId, attributes);
		return new ObjectList(resultAttributes);
	}

	@RequestMapping("/copyCollectionToCollection.service")
	protected ExecutionAnswer copyCollectionToCollection(
			@RequestParam String sourceCollectionId,
			@RequestParam String targetCollectionId, HttpServletRequest request) {

		String[] attrNames = request.getParameterValues("attrName");
		String[] attrPatterns = request.getParameterValues("attrPattern");

		// Checks
		if (sourceCollectionId == null || targetCollectionId == null)
			throw new SlcException(
					"Source and target collection ids must be specified");
		if (attrNames != null
				&& (attrPatterns == null || attrNames.length != attrPatterns.length))
			throw new SlcException(
					"There must be as many attrName as attrPatterns");

		TreeTestResultCollection sourceCollection = treeTestResultCollectionDao
				.getTestResultCollection(sourceCollectionId);
		if (attrNames != null) {
			int index = 0;
			for (String attrName : attrNames) {
				String attrPattern = attrPatterns[index];// safe: checked above

				for (TreeTestResult treeTestResult : sourceCollection
						.getResults()) {
					if (PatternMatchUtils.simpleMatch(attrPattern,
							treeTestResult.getAttributes().get(attrName))) {
						testManagerService.addResultToCollection(
								targetCollectionId, treeTestResult.getUuid());
					}
				}
				index++;
			}
		} else {
			// remove all
			// TODO: optimize
			for (TreeTestResult treeTestResult : sourceCollection.getResults()) {
				testManagerService.addResultToCollection(targetCollectionId,
						treeTestResult.getUuid());
			}
		}
		return ExecutionAnswer.ok("Execution completed properly");
	}

	@RequestMapping("/getAttachment.service")
	protected void getAttachment(@RequestParam String uuid,
			@RequestParam String contentType, @RequestParam String name,
			HttpServletResponse response) throws Exception {
		if (contentType == null || "".equals(contentType.trim())) {
			if (name != null) {
				contentType = FORCE_DOWNLOAD;
				String ext = FilenameUtils.getExtension(name);
				// cf. http://en.wikipedia.org/wikServicei/Internet_media_type
				if ("csv".equals(ext))
					contentType = "text/csv";
				else if ("pdf".equals(ext))
					contentType = "application/pdf";
				else if ("zip".equals(ext))
					contentType = "application/zip";
				else if ("html".equals(ext))
					contentType = "application/html";
				else if ("txt".equals(ext))
					contentType = "text/plain";
				else if ("doc".equals(ext) || "docx".equals(ext))
					contentType = "application/msword";
				else if ("xls".equals(ext) || "xlsx".equals(ext))
					contentType = "application/vnd.ms-excel";
				else if ("xml".equals(ext))
					contentType = "text/xml";
			}
		}

		if (name != null) {
			contentType = contentType + ";name=\"" + name + "\"";
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ name + "\"");
		}
		response.setHeader("Expires", "0");
		response.setHeader("Cache-Control", "no-cache, must-revalidate");
		response.setHeader("Pragma", "no-cache");

		SimpleAttachment resourceDescriptor = new SimpleAttachment();
		resourceDescriptor.setUuid(uuid);
		resourceDescriptor.setContentType(contentType);

		response.setContentType(contentType);
		ServletOutputStream outputStream = response.getOutputStream();
		attachmentsStorage.retrieveAttachment(resourceDescriptor, outputStream);
	}

	// IoC

	public void setTreeTestResultDao(TreeTestResultDao treeTestResultDao) {
		this.treeTestResultDao = treeTestResultDao;
	}

	public void setTestManagerService(TestManagerService testManagerService) {
		this.testManagerService = testManagerService;
	}

	public void setTreeTestResultCollectionDao(
			TreeTestResultCollectionDao treeTestResultCollectionDao) {
		this.treeTestResultCollectionDao = treeTestResultCollectionDao;
	}

	public void setAttachmentsStorage(AttachmentsStorage attachmentsStorage) {
		this.attachmentsStorage = attachmentsStorage;
	}
}
