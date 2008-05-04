<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@include file="header.txt"%>
<jsp:include page="common.jsp" />

<div id="main">
<h1>SLC Execution #${slcExecution.uuid}</h1>

<h2>Details</h2>
<table>
	<tr>
		<td>Host</td>
		<td>${slcExecution.host}</td>
	</tr>
	<tr>
		<td>User</td>
		<td>${slcExecution.user}</td>
	</tr>
	<tr>
		<td>Status</td>
		<td>${slcExecution.status}</td>
	</tr>
	<tr>
		<td>Type</td>
		<td>${slcExecution.type}</td>
	</tr>
	<c:choose>
		<c:when test="${slcExecution.type == 'org.argeo.slc.ant'}">
			<tr>
				<td>Script</td>
				<td>${slcExecution.attributes['ant.file']}</td>
			</tr>
		</c:when>
	</c:choose>
</table>

<h2>Execution Steps</h2>
<c:forEach items="${slcExecutionSteps}" var="slcExecutionStep">
	<a name="step_${slcExecutionStep.uuid}"></a>
	<h3 class="executionStep">${slcExecutionStep.begin} -
	${slcExecutionStep.uuid} (${slcExecutionStep.type})</h3>
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