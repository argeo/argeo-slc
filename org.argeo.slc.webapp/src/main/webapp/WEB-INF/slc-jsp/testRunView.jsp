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
