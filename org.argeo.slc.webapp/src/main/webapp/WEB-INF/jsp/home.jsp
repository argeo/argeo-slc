<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@include file="header.txt"%>
<jsp:include page="common.jsp" />

<div id="main">
<h1>SLC Home</h1>

<h2>Web</h2>
<a href="resultList.web">Results</a><br/>
<a href="slcExecutionList.web">SLC Executions</a>

<h2>Web Services</h2>
<a href="slcService/slcDefinition.wsdl">WSDL Definition</a>

</div>

<%@include file="footer.txt"%>