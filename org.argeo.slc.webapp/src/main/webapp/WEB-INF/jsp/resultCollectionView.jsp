<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@include file="header.txt"%>
<jsp:include page="common.jsp" />

<div id="main">
<h1>Result Collection ${resultCollection.id}</h1>

<table cellspacing="0">
	<thead>
		<tr>
			<th>Id</th>
			<th>Status</th>
		</tr>
	</thead>
	<c:forEach items="${resultCollection.results}" var="result">
		<tr>
			<td>${result.uuid}</td>
			<c:choose>
				<c:when test="${result.closeDate != null}">
					<td>${result.closeDate}</td>
				</c:when>
				<c:otherwise>
					<td>NOT CLOSED</td>
				</c:otherwise>
			</c:choose>
			<td><a href="resultView.web?uuid=${result.uuid}">view</a></td>
			<td><a
				href="removeResultFromCollection.web?resultUuid=${result.uuid}&collectionId=${resultCollection.id}">remove</a></td>
		</tr>
	</c:forEach>
</table>

</div>

<%@include file="footer.txt"%>