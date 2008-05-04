<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@include file="header.txt"%>
<jsp:include page="common.jsp" />

<div id="main">
<h1>Result #${result.uuid}</h1>

<jsp:useBean id="describedPaths" type="java.util.SortedMap"
	scope="request" /> <jsp:useBean id="toc" type="java.util.SortedMap"
	scope="request" />

<table>
	<c:forEach items="${toc}" var="tocEntry">
		<jsp:useBean id="tocEntry" type="java.util.Map.Entry" />
		<tr>
			<td style="padding-left: ${tocEntry.key.depth}0px"
				class="${tocEntry.value}">${tocEntry.key.name }</td>
		</tr>
	</c:forEach>
</table>

<c:forEach items="${result.resultParts}" var="resultPartEntry">
	<jsp:useBean id="resultPartEntry" type="java.util.Map.Entry" />

	<h2><%=describedPaths.get(resultPartEntry.getKey())%></h2>
	<table>
		<c:forEach items="${resultPartEntry.value.parts}" var="part">
			<tr>
				<td class="${part.status == 0 ? 'passed' : 'failed'}">
				${part.message}</td>
				<c:if test="${part.testRunUuid!=null}">
					<td>
					<a href="testRunView.web?uuid=${part.testRunUuid}">related
					test run</a>
					</td>
				</c:if>
			</tr>
		</c:forEach>
	</table>

</c:forEach></div>

<%@include file="footer.txt"%>