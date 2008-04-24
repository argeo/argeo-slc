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
<table>
	<tr>
		<td>UUID</td>
		<td>TYPE</td>
		<td>BEGIN</td>
	</tr>
	<c:forEach items="${slcExecutionSteps}" var="slcExecutionStep">
		<tr>
			<td>${slcExecutionStep.uuid}</td>
			<td>${slcExecutionStep.type}</td>
			<td>${slcExecutionStep.begin}</td>
		</tr>
		<c:forEach items="${slcExecutionStep.logLines}" var="slcExecutionStepLog">
		<tr>
			<td colspan="3">- <em>${slcExecutionStepLog}</em></td>
		</tr>
		</c:forEach>
		<tr>
		</tr>
	</c:forEach>

</table>
</div>

<%@include file="footer.txt"%>