<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/xml"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<dataSet>
<c:forEach items="${results}" var="result">
	<data>
		<param name="testName">${result.uuid}</param>
		<param name="uuid">${result.uuid}</param>
<c:choose>
	<c:when test="${result.closeDate != null}">
		<param name="date">${result.closeDate}</param>
	</c:when>
	<c:otherwise>
		<param name="date">NOT CLOSED</param>
	</c:otherwise>
</c:choose>
		<report type="applet">org.argeo.slc.web.Applet</report>
		<report type="download" commandid="xsl">XSL</report>
		<report type="download" commandid="xml">XML</report>
		<report type="download" commandid="xls">Excel</report>
		<report type="download" commandid="pdf">Portable Document Format</report>
	</data>
</c:forEach>
</dataSet>