<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@include file="WEB-INF/jsp/header.txt"%>
<jsp:include page="WEB-INF/jsp/common.jsp" />

<%
response.sendRedirect("slcWeb/home");
%>

<%@include file="WEB-INF/jsp/footer.txt"%>