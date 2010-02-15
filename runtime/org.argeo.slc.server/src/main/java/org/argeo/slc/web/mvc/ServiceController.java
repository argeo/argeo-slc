package org.argeo.slc.web.mvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.Vector;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.attachment.AttachmentsStorage;
import org.argeo.slc.core.attachment.SimpleAttachment;
import org.argeo.slc.core.test.tree.ResultAttributes;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.core.test.tree.TreeTestResultCollection;
import org.argeo.slc.dao.runtime.SlcAgentDescriptorDao;
import org.argeo.slc.dao.test.tree.TreeTestResultCollectionDao;
import org.argeo.slc.dao.test.tree.TreeTestResultDao;
import org.argeo.slc.deploy.DynamicRuntime;
import org.argeo.slc.msg.ExecutionAnswer;
import org.argeo.slc.msg.ObjectList;
import org.argeo.slc.msg.ReferenceList;
import org.argeo.slc.runtime.SlcAgentDescriptor;
import org.argeo.slc.services.TestManagerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ServiceController {

	private final static Log log = LogFactory.getLog(ServiceController.class);

	// Constants
	public final static String KEY_ANSWER = "__answer";
	protected final String FORCE_DOWNLOAD = "Content-Type: application/force-download";

	// IoC
	// FIXME : why must this be final ??
	private final TreeTestResultDao treeTestResultDao;
	private final TestManagerService testManagerService;
	private final TreeTestResultCollectionDao testResultCollectionDao;
	private final SlcAgentDescriptorDao slcAgentDescriptorDao;
	private AttachmentsStorage attachmentsStorage;
	private DynamicRuntime<?> dynamicRuntime;

	public ServiceController(TreeTestResultDao treeTestResultDao,
			TreeTestResultCollectionDao testResultCollectionDao,
			TestManagerService testManagerService,
			SlcAgentDescriptorDao slcAgentDescriptorDao) {
		if (log.isDebugEnabled())
			log.debug("In SlcServiceController Constructor");

		this.testManagerService = testManagerService;
		this.treeTestResultDao = treeTestResultDao;
		this.testResultCollectionDao = testResultCollectionDao;
		this.slcAgentDescriptorDao = slcAgentDescriptorDao;
	}

	// Business Methods

	@RequestMapping("/isServerReady.service")
	protected String isServerReady(Model model) {
		if (log.isDebugEnabled())
			log.debug("SlcServiceController :: isServerReady ");
		// Does nothing for now, it will return an OK answer.
		model.addAttribute(KEY_ANSWER, ExecutionAnswer
				.ok("Execution completed properly"));
		return KEY_ANSWER;
	}

	@RequestMapping("/shutdownRuntime.service")
	protected String shutdownRuntime(Model model) {
		if (log.isDebugEnabled())
			log.debug("SlcServiceController :: shutdownRuntime");

		new Thread() {
			public void run() {
				// wait in order to let call return
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// silent
				}
				dynamicRuntime.shutdown();
			}
		}.start();
		ExecutionAnswer answer = ExecutionAnswer.ok("Server shutting down...");
		model.addAttribute(answer);
		return KEY_ANSWER;
	}

	@RequestMapping("/getResult.service")
	protected String getResult(
			@RequestParam(value = "uuid", required = false) String uuid,
			Model model) {
		if (log.isDebugEnabled())
			log.debug("In SlcServiceController In GetResultMethod");

		TreeTestResult result = treeTestResultDao.getTestResult(uuid);
		if (result == null)
			throw new SlcException("No result found for uuid " + uuid);
		model.addAttribute("result", result);
		return "result";
	}

	@RequestMapping("/addResultToCollection.service")
	protected String addResultToCollection(@RequestParam String collectionId,
			@RequestParam String resultUuid, Model model) {
		if (log.isDebugEnabled())
			log.debug("In SlcServiceController :: addResultToCollection");

		testManagerService.addResultToCollection(collectionId, resultUuid);
		model.addAttribute(KEY_ANSWER, ExecutionAnswer
				.ok("Execution completed properly"));
		return KEY_ANSWER;
	}

	@RequestMapping("/removeResultFromCollection.service")
	protected String removeResultFromCollection(HttpServletRequest request,
			Model model) {
		if (log.isDebugEnabled())
			log
					.debug("In SlcServiceController :: removeResultFromCollection.service");

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
			TreeTestResultCollection sourceCollection = testResultCollectionDao
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
				TreeTestResultCollection sourceCollection = testResultCollectionDao
						.getTestResultCollection(collectionId);
				List<TreeTestResult> results = new ArrayList<TreeTestResult>(
						sourceCollection.getResults());
				for (TreeTestResult treeTestResult : results) {
					testManagerService.removeResultFromCollection(collectionId,
							treeTestResult.getUuid());
				}

			}
		}
		model.addAttribute(KEY_ANSWER, ExecutionAnswer
				.ok("Execution completed properly"));
		return KEY_ANSWER;
	}

	@RequestMapping("/listCollectionRefs.service")
	protected String listCollectionRefs(HttpServletRequest request,
			HttpServletResponse response, Model model) {

		if (log.isDebugEnabled())
			log.debug("In SlcServiceController :: listCollectionRefs.service");

		SortedSet<TreeTestResultCollection> results = testResultCollectionDao
				.listCollections();

		ReferenceList referenceList = new ReferenceList();
		for (TreeTestResultCollection collection : results) {
			referenceList.getReferences().add(collection.getId());
		}

		model.addAttribute("referenceList", referenceList);
		return "referenceList";
	}

	@RequestMapping("/listResultAttributes.service")
	protected String listResultAttributes(@RequestParam String id, Model model) {

		if (log.isDebugEnabled())
			log
					.debug("In SlcServiceController :: listResultAttributes.service");

		List<ResultAttributes> resultAttributes = testResultCollectionDao
				.listResultAttributes(id);

		model.addAttribute("resultAttributesList", new ObjectList(
				resultAttributes));
		return "resultAttributesList";
	}

	@RequestMapping("/listResults.service")
	@SuppressWarnings(value = { "unchecked" })
	protected String listResults(@RequestParam (value = "collectionId", required = false)String collectionId,
			HttpServletRequest request, Model model) {

		if (log.isDebugEnabled())
			log.debug("In SlcServiceController :: listResults.service");

		Map<String, String[]> parameterMap = request.getParameterMap();

		Map<String, String> attributes = new HashMap<String, String>();
		for (String parameter : parameterMap.keySet()) {
			if (parameter.startsWith("attr.")) {
				String key = parameter.substring("attr.".length());
				attributes.put(key, parameterMap.get(parameter)[0]);
			}
		}

		List<TreeTestResult> resultAttributes = testResultCollectionDao
				.listResults(collectionId, attributes);

		model.addAttribute("resultList", new ObjectList(resultAttributes));
		return "resultList";
	}

	@RequestMapping("/copyCollectionToCollection.service")
	protected String copyCollectionToCollection(
			@RequestParam String sourceCollectionId,
			@RequestParam String targetCollectionId,
			HttpServletRequest request, Model model) {

		if (log.isDebugEnabled())
			log
					.debug("In SlcServiceController :: copyCollectionToCollection.service");

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

		TreeTestResultCollection sourceCollection = testResultCollectionDao
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
		model.addAttribute(KEY_ANSWER, ExecutionAnswer
				.ok("Execution completed properly"));
		return KEY_ANSWER;
	}

	@RequestMapping("/listAgents.service")
	protected String listAgents(Model model) {
		if (log.isDebugEnabled())
			log.debug("In SlcServiceController :: listAgents.service");

		List<SlcAgentDescriptor> list = slcAgentDescriptorDao
				.listSlcAgentDescriptors();
		model.addAttribute("list", new ObjectList(list));
		return "list";
	}

	@RequestMapping("/cleanAgents.service")
	protected String cleanAgents(Model model) {

		if (log.isDebugEnabled())
			log.debug("In SlcServiceController :: cleanAgents.service");

		List<SlcAgentDescriptor> list = slcAgentDescriptorDao
				.listSlcAgentDescriptors();
		for (SlcAgentDescriptor t : new Vector<SlcAgentDescriptor>(list)) {
			slcAgentDescriptorDao.delete(t);
		}
		model.addAttribute(KEY_ANSWER, ExecutionAnswer
				.ok("Execution completed properly"));
		return KEY_ANSWER;
	}

	@RequestMapping("/getAttachment.service")
	protected String getAttachment(@RequestParam String uuid,
			@RequestParam String contentType, @RequestParam String name,
			HttpServletResponse response, Model model) throws Exception {
		if (log.isDebugEnabled())
			log.debug("In SlcServiceController :: getAttachment");

		if (contentType == null || "".equals(contentType.trim())) {
			if (name != null) {
				contentType = FORCE_DOWNLOAD;
				String ext = FilenameUtils.getExtension(name);
				// cf. http://en.wikipedia.org/wiki/Internet_media_type
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
		return null;
	}

	// IoC

	public void setDynamicRuntime(DynamicRuntime<?> dynamicRuntime) {
		this.dynamicRuntime = dynamicRuntime;
	}

	public void setAttachmentsStorage(AttachmentsStorage attachmentsStorage) {
		this.attachmentsStorage = attachmentsStorage;
	}
}
