package org.argeo.slc.web.mvc.result;

import java.util.SortedSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.core.test.tree.TreeTestResultCollection;
import org.argeo.slc.dao.test.tree.TreeTestResultCollectionDao;
import org.argeo.slc.msg.ReferenceList;
import org.argeo.slc.web.mvc.AbstractServiceController;
import org.springframework.web.servlet.ModelAndView;

public class ResultCollectionReferencesController extends
		AbstractServiceController {
	private final TreeTestResultCollectionDao testResultCollectionDao;

	public ResultCollectionReferencesController(
			TreeTestResultCollectionDao testResultCollectionDao) {
		this.testResultCollectionDao = testResultCollectionDao;
	}

	protected void handleServiceRequest(HttpServletRequest request,
			HttpServletResponse response, ModelAndView modelAndView)
			throws Exception {
		SortedSet<TreeTestResultCollection> results = testResultCollectionDao
				.listCollections();

		ReferenceList referenceList = new ReferenceList();
		for (TreeTestResultCollection collection : results) {
			referenceList.getReferences().add(collection.getId());
		}

		modelAndView.addObject("referenceList", referenceList);
	}
}
