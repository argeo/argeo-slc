<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@include file="header.txt"%>
<jsp:include page="common.jsp" />

<div id="main">
<h1>TestRun #${testRunDescriptor.testRunUuid}</h1>

<c:if test="${testRunDescriptor.slcExecutionUuid !=null}">
	Related	SLC Execution: <a
		href="slcExecutionView.web?uuid=${testRunDescriptor.slcExecutionUuid}">${testRunDescriptor.slcExecutionUuid}</a>
	<br />
	<c:if test="${testRunDescriptor.slcExecutionStepUuid !=null}">
	Related SLC Execution Step:	<a
			href="slcExecutionView.web?uuid=${testRunDescriptor.slcExecutionUuid}#step_${testRunDescriptor.slcExecutionStepUuid}">${testRunDescriptor.slcExecutionStepUuid}</a>
		<br />
	</c:if>
</c:if> <c:if test="${testRunDescriptor.testResultUuid !=null}">
	Related	Test Result: <a
		href="resultView.web?uuid=${testRunDescriptor.testResultUuid}">${testRunDescriptor.testResultUuid}</a>
	<br />
</c:if> 

<c:if test="${testRunDescriptor.deployedSytemId !=null}">
	Related Deployed System: ${testRunDescriptor.deployedSytemId}
	<br />
</c:if>

 <%@include file="footer.txt"%>