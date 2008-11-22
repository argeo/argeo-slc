package org.argeo.slc.web.mvc.result;

import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.tree.PartSubList;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.dao.test.tree.TreeTestResultDao;
import org.argeo.slc.structure.StructureElement;
import org.argeo.slc.test.TestResultPart;
import org.argeo.slc.test.TestStatus;

public class ResultViewController extends ParameterizableViewController {
	public final static String MODELKEY_RESULT = "result";

	private final TreeTestResultDao testResultDao;

	public ResultViewController(TreeTestResultDao testResultDao) {
		this.testResultDao = testResultDao;
	}

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String uuid = request.getParameter("uuid");
		TreeTestResult result = testResultDao.getTestResult(uuid);

		SortedMap<TreeSPath, String> toc = generateToc(result);

		SortedMap<TreeSPath, String> describedPaths = new TreeMap<TreeSPath, String>();
		for (TreeSPath path : toc.keySet()) {
			describedPaths.put(path, describedPath(path, result));
		}

		SortedMap<TreeSPath, String> anchors = new TreeMap<TreeSPath, String>();
		for (TreeSPath path : toc.keySet()) {
			anchors.put(path, anchor(path));
		}

		ModelAndView modelAndView = new ModelAndView();

		modelAndView.addObject(MODELKEY_RESULT, result);
		modelAndView.addObject("toc", toc);
		modelAndView.addObject("describedPaths", describedPaths);
		modelAndView.addObject("anchors", anchors);
		modelAndView.setViewName(getViewName());
		return modelAndView;
	}

	private SortedMap<TreeSPath, String> generateToc(TreeTestResult result) {
		SortedMap<TreeSPath, String> toc = new TreeMap<TreeSPath, String>();
		for (TreeSPath path : result.getResultParts().keySet()) {
			PartSubList subList = (PartSubList) result.getResultParts().get(
					path);
			boolean isFailed = false;
			for (TestResultPart part : subList.getParts()) {
				if (!part.getStatus().equals(TestStatus.PASSED)) {
					isFailed = true;
					break;
				}
			}
			fillToc(toc, path, isFailed);
		}
		return toc;
	}

	private void fillToc(SortedMap<TreeSPath, String> toc, TreeSPath path,
			boolean isFailed) {
		if (isFailed) {
			toc.put(path, "failed");
		} else {
			if (!toc.containsKey(path)) {
				toc.put(path, "passed");
			}
		}

		if (path.getParent() != null) {
			fillToc(toc, path.getParent(), isFailed);
		}
	}

	private static String anchor(TreeSPath path) {
		return path.getAsUniqueString().replace(path.getSeparator(), '_');
	}

	private static String describedPath(TreeSPath path, TreeTestResult ttr) {
		StringBuffer buf = new StringBuffer("");
		// TODO :optimize with hierarchy
		describedPath(path, buf, ttr);
		return buf.toString();
	}

	private static void describedPath(TreeSPath path, StringBuffer buf,
			TreeTestResult ttr) {
		if (path.getParent() != null) {
			describedPath(path.getParent(), buf, ttr);
		}
		String description = path.getName();
		StructureElement element = ttr.getElements().get(path);
		if (element != null) {
			description = element.getLabel();
		}
		buf.append('/').append(description);
	}

}
