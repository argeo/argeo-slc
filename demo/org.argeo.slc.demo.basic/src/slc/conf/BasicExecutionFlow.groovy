import org.argeo.slc.test.*;

public class BasicExecutionFlow implements org.argeo.slc.executionflow.ExecutionFlow {

	ExecutableTestRun firstSubTest = null;
	ExecutableTestRun secondSubTest = null;

	void execute(){
		firstSubTest?.execute();
		secondSubTest?.execute();
	}
}
