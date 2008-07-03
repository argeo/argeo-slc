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
			<td><a
				href="resultCollectionViewXml.xslt?id=${resultCollection.id}">xml</a></td>
		</tr>
	</c:forEach>
</table>

</div>
