package org.argeo.slc.xml.test.tree;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.core.io.Resource;
import org.springframework.oxm.Marshaller;
import org.springframework.xml.transform.StringResult;
import org.w3c.dom.Document;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.SlcException;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.test.TestResultListener;
import org.argeo.slc.test.TestResultPart;

/** Build a report based on a tree test result using an XSLT stylesheet. */
public class XsltReportGenerator implements TestResultListener<TreeTestResult> {
	private final static Log log = LogFactory.getLog(XsltReportGenerator.class);

	private DocumentBuilder documentBuilder = null;

	private Resource xsltStyleSheet;

	private Templates templates;

	private Marshaller marshaller;

	private String outputDir;
	private String outputFileExtension = "html";

	private Boolean logXml = false;

	private Map<String, String> xsltParameters = new HashMap<String, String>();

	public void init() {
		if (templates != null)
			return;

		if (xsltStyleSheet == null)
			throw new SlcException("XSLT style sheet not specified.");

		InputStream in = null;
		try {
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			in = xsltStyleSheet.getInputStream();
			StreamSource xsltSource = new StreamSource(in);
			templates = transformerFactory.newTemplates(xsltSource);
		} catch (Exception e) {
			throw new SlcException("Could not initialize templates", e);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	public void resultPartAdded(TreeTestResult testResult,
			TestResultPart testResultPart) {

	}

	public void close(TreeTestResult testResult) {
		if (templates == null)
			throw new SlcException("XSLT template not initialized");

		File file = getFile(testResult);
		OutputStream out = null;

		try {
			Transformer transformer = templates.newTransformer();
			for (String paramKey : xsltParameters.keySet()) {
				transformer
						.setParameter(paramKey, xsltParameters.get(paramKey));
				if (log.isTraceEnabled())
					log.trace("Set XSLT parameter " + paramKey + " to "
							+ xsltParameters.get(paramKey));
			}

			if (documentBuilder == null)
				documentBuilder = DocumentBuilderFactory.newInstance()
						.newDocumentBuilder();

			Document document = documentBuilder.newDocument();
			DOMResult marshallResult = new DOMResult(document);
			marshaller.marshal(testResult, marshallResult);

			if (logXml) {
				Transformer identityTransformer = TransformerFactory
						.newInstance().newTransformer();
				StringResult xmlResult = new StringResult();
				identityTransformer.transform(new DOMSource(marshallResult
						.getNode()), xmlResult);
				log.info("Marshalled XML:\n" + xmlResult);
			}

			DOMSource transfoSource = new DOMSource(marshallResult.getNode());

			if (outputDir != null) {
				File dir = new File(outputDir);
				dir.mkdirs();
				out = new FileOutputStream(file);
				StreamResult outputResult = new StreamResult(out);
				transformer.transform(transfoSource, outputResult);
			} else {
				// print on console if no output dir
				StringResult result = new StringResult();
				transformer.transform(transfoSource, result);
				log.info("Generated report:\n" + result);
			}
		} catch (Exception e) {
			throw new SlcException(
					"Could not transform test result to " + file, e);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	public Resource getXsltStyleSheet() {
		return xsltStyleSheet;
	}

	public void setXsltStyleSheet(Resource xsltStyleSheet) {
		this.xsltStyleSheet = xsltStyleSheet;
	}

	public void setTemplates(Templates templates) {
		this.templates = templates;
	}

	public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}

	public void setOutputFileExtension(String outputFileExtension) {
		this.outputFileExtension = outputFileExtension;
	}

	protected File getFile(TreeTestResult result) {
		Long time = System.currentTimeMillis();
		return new File(outputDir + File.separator + time + "-"
				+ result.getUuid() + "." + outputFileExtension);
	}

	public void setLogXml(Boolean logXml) {
		this.logXml = logXml;
	}

	public void setXsltParameters(Map<String, String> xsltParameters) {
		this.xsltParameters = xsltParameters;
	}

}
