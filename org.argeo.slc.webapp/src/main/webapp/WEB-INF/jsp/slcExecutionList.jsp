<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@include file="header.txt"%>
<jsp:include page="common.jsp" />

<div id="main">
<h1>SLC Execution List</h1>

<table>
	<tr>
		<td></td>
		<td>UUID</td>
		<td>STATUS</td>
		<td>HOST</td>
		<td>TYPE</td>		
	</tr>
	<c:forEach items="${slcExecutions}" var="slcExecution">
		<tr>
			<td><a href="slcExecutionView.web?uuid=${slcExecution.uuid}">View details</a></td>
			<td>${slcExecution.uuid}</td>
			<td>${slcExecution.status}</td>
			<td>${slcExecution.host}</td>
			<td>${slcExecution.type}</td>
		</tr>
	</c:forEach>
</table>

</div>

<%@include file="footer.txt"%>