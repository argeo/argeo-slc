<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@include file="header.txt"%>
<jsp:include page="common.jsp" />

<div id="main">
<h1>SLC Execution Details</h1>
<h2>Uuid = ${slcExecution.uuid}</h2>
<br>
<h2>Execution Steps</h2>
<c:forEach items="${slcExecutionSteps}" var="slcExecutionStep">
	<a name="step_${slcExecutionStep.uuid}"></a>
	<h3 class="executionStep">${slcExecutionStep.begin} - ${slcExecutionStep.uuid}
	(${slcExecutionStep.type})</h3>
<table>
		<c:forEach items="${slcExecutionStep.logLines}"
			var="slcExecutionStepLogLine">
			<tr>
				<td colspan="3">${slcExecutionStepLogLine}</td>
			</tr>
		</c:forEach>
	</table>
</c:forEach></div>

<%@include file="footer.txt"%>