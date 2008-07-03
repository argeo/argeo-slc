<div id="main">
<h1>Results</h1>

<table cellspacing="0">
	<thead>
		<tr>
			<th>Id</th>
			<th>Status</th>
		</tr>
	</thead>
	<c:forEach items="${results}" var="result">
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
			<td style="padding-right: 5px"><a href="resultView.web?uuid=${result.uuid}">view</a></td>
			<td><a href="resultView.xslt?uuid=${result.uuid}">xsl</a></td>
			<td><a href="resultViewXml.xslt?uuid=${result.uuid}">xml</a></td>
		</tr>
	</c:forEach>
</table>

</div>
