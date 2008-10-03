<div id="main">
<h1>Result #${result.uuid}</h1>

<jsp:useBean id="describedPaths" type="java.util.SortedMap"
	scope="request" /> 
<jsp:useBean id="toc" type="java.util.SortedMap"
	scope="request" />

<p>
<a href="addResultToCollection.web?resultUuid=${result.uuid}&collectionId=staging">Add to staging collection</a><br/>
<a href="addResultToCollection.web?resultUuid=${result.uuid}&collectionId=official">Add to official collection</a>
</p>

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
		<c:forEach items="${result.elements[resultPartEntry.key].tags}" var="tagEntry">
			<tr>
				<td style="font-style: italic;font-size: 80%">${tagEntry.key}</td>
				<td style="font-size: 80%">${tagEntry.value}</td>
			</tr>
		</c:forEach>
	</table>
	<table>
	
		<c:forEach items="${resultPartEntry.value.parts}" var="part">
			<tr>
				<td class="${part.status == 0 ? 'passed' : (part.status == 1 ? 'failed': 'error')}" style="vertical-align:top">
				${part.message}
				<c:if test="${part.status == 2}">
					<pre>${part.exceptionMessage}</pre>
				</c:if>
				</td>
				<c:if test="${part.testRunUuid!=null}">
					<td style="vertical-align:top">
					<a href="testRunView.web?uuid=${part.testRunUuid}">test run</a>
					</td>
				</c:if>
			</tr>
		</c:forEach>
	</table>

</c:forEach></div>
