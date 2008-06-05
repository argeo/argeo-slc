<div id="banner"><a href="home.web"><img
	src="images/SpartaBanner.gif" /></a></div>

<div id="navigation">

<table border="0" cellspacing="0">
	<tr>
		<td class="nav1"><a href="home.web">Home</a></td>
	</tr>
	<tr>
		<td class="nav1">Overview</td>
	</tr>
	<tr>
		<td class="nav2"><a href="resultCollectionList.web">Collections</a></td>
	</tr>
	<tr>
		<td class="nav2"><a href="resultList.web">All Results</a></td>
	</tr>
	<tr>
		<td class="nav2"><a href="slcExecutionList.web">SLC
		Executions</a></td>
	</tr>
	<tr>
		<td class="nav1"><a href="resultCollectionList.web">Collections</a></td>
	</tr>
	<c:forEach items="${resultCollections}" var="resultCollection">
		<tr>
			<td class="nav2"><a
				href="resultCollectionView.web?id=${resultCollection.id}">${resultCollection.id}</a></td>
		</tr>
	</c:forEach>
	<tr>
		<td class="nav1">Technical</td>
	</tr>
	<tr>
		<td class="nav2"><a href="slcService/slcDefinition.wsdl">WSDL
		Definition</a></td>
	</tr>
</table>
</div>
