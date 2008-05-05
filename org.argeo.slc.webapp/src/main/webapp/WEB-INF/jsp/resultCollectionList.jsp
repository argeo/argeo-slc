<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@include file="header.txt"%>
<jsp:include page="common.jsp" />

<div id="main">
<h1>Result Collections</h1>

<table cellspacing="0">
	<thead>
		<tr>
			<th>Id</th>
		</tr>
	</thead>
	<c:forEach items="${resultCollections}" var="resultCollection">
		<tr>
			<td>${resultCollection.id}</td>
			<td><a
				href="resultCollectionView.web?id=${resultCollection.id}">view</a></td>
		</tr>
	</c:forEach>
</table>

</div>

<%@include file="footer.txt"%>