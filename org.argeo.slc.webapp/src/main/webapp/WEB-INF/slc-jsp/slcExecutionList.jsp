<div id="main">
<h1>SLC Execution List</h1>

<table>
	<thead>
		<tr>
			<td>UUID</td>
			<td>STATUS</td>
			<td>HOST</td>
			<td>TYPE</td>
		</tr>
	</thead>
	<c:forEach items="${slcExecutions}" var="slcExecution">
		<tr>
			<td>${slcExecution.uuid}</td>
			<td>${slcExecution.status}</td>
			<td>${slcExecution.host}</td>
			<td>${slcExecution.type}</td>
			<td><a href="slcExecutionView.web?uuid=${slcExecution.uuid}">view</a></td>
		</tr>
	</c:forEach>
</table>

</div>
