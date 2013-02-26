/*

 * Copyright (C) 2007-2012 Argeo GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.argeo.slc.jcr.execution;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.ArgeoException;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.core.execution.ProcessThread;
import org.argeo.slc.execution.ExecutionModulesManager;
import org.argeo.slc.execution.ExecutionProcess;
import org.argeo.slc.execution.RealizedFlow;
import org.argeo.slc.jcr.SlcNames;

/** Where the actual execution takes place */
public class JcrProcessThread extends ProcessThread implements SlcNames {

	public JcrProcessThread(ThreadGroup processesThreadGroup,
			ExecutionModulesManager executionModulesManager,
			JcrExecutionProcess process) {
		super(processesThreadGroup, executionModulesManager, process);
	}

	@Override
	protected void process() throws InterruptedException {
		Session session = null;
		try {
			session = getJcrExecutionProcess().getRepository().login();

			List<RealizedFlow> realizedFlows = getProcess().getRealizedFlows();
			for (RealizedFlow realizedFlow : realizedFlows) {
				Node realizedFlowNode = session
						.getNode(((JcrRealizedFlow) realizedFlow).getPath());

				// set status on realized flow
				realizedFlowNode.setProperty(SLC_STATUS,
						ExecutionProcess.RUNNING);
				realizedFlowNode.getSession().save();
				try {
					//
					// EXECUTE THE FLOW
					//
					execute(realizedFlow, true);

					// set status on realized flow
					realizedFlowNode.setProperty(SLC_STATUS,
							ExecutionProcess.COMPLETED);
					realizedFlowNode.getSession().save();
				} catch (RepositoryException e) {
					throw e;
				} catch (InterruptedException e) {
					// set status on realized flow
					realizedFlowNode.setProperty(SLC_STATUS,
							ExecutionProcess.KILLED);
					realizedFlowNode.getSession().save();
					throw e;
				} catch (RuntimeException e) {
					// set status on realized flow
					realizedFlowNode.setProperty(SLC_STATUS,
							ExecutionProcess.ERROR);
					realizedFlowNode.getSession().save();
					throw e;
				}
			}
		} catch (RepositoryException e) {
			throw new ArgeoException("Cannot process "
					+ getJcrExecutionProcess().getNodePath(), e);
		} finally {
			JcrUtils.logoutQuietly(session);
		}
	}

	//
	// /** CONFIGURE THE REALIZED FLOWS */
	// PROTECTED VOID EXECUTE(NODE REALIZEDFLOWNODE) THROWS REPOSITORYEXCEPTION,
	// INTERRUPTEDEXCEPTION {
	// IF (REALIZEDFLOWNODE.HASNODE(SLC_ADDRESS)) {
	// STRING FLOWPATH = REALIZEDFLOWNODE.GETNODE(SLC_ADDRESS)
	// .GETPROPERTY(PROPERTY.JCR_PATH).GETSTRING();
	// // TODO: CONVERT TO LOCAL PATH IF REMOTE
	//
	// NODE FLOWNODE = REALIZEDFLOWNODE.GETSESSION().GETNODE(FLOWPATH);
	// STRING FLOWNAME = FLOWNODE.GETPROPERTY(SLC_NAME).GETSTRING();
	//
	// NODE EXECUTIONMODULENODE = FLOWNODE.GETSESSION().GETNODE(
	// SLCJCRUTILS.MODULEPATH(FLOWPATH));
	// STRING EXECUTIONMODULENAME = EXECUTIONMODULENODE.GETPROPERTY(
	// SLC_NAME).GETSTRING();
	// STRING EXECUTIONMODULEVERSION = EXECUTIONMODULENODE.GETPROPERTY(
	// SLC_VERSION).GETSTRING();
	//
	// REALIZEDFLOW REALIZEDFLOW = NEW REALIZEDFLOW();
	// REALIZEDFLOW.SETMODULENAME(EXECUTIONMODULENAME);
	// REALIZEDFLOW.SETMODULEVERSION(EXECUTIONMODULEVERSION);
	//
	// // RETRIEVE EXECUTION SPEC
	// DEFAULTEXECUTIONSPEC EXECUTIONSPEC = NEW DEFAULTEXECUTIONSPEC();
	// MAP<STRING, EXECUTIONSPECATTRIBUTE> ATTRS =
	// READEXECUTIONSPECATTRIBUTES(REALIZEDFLOWNODE);
	// EXECUTIONSPEC.SETATTRIBUTES(ATTRS);
	//
	// // SET EXECUTION SPEC NAME
	// IF (FLOWNODE.HASPROPERTY(SLCNAMES.SLC_SPEC)) {
	// NODE EXECUTIONSPECNODE = FLOWNODE.GETPROPERTY(SLC_SPEC)
	// .GETNODE();
	// EXECUTIONSPEC.SETBEANNAME(EXECUTIONSPECNODE.GETPROPERTY(
	// SLC_NAME).GETSTRING());
	// }
	//
	// // EXPLICITLY RETRIEVE VALUES
	// MAP<STRING, OBJECT> VALUES = NEW HASHMAP<STRING, OBJECT>();
	// FOR (STRING ATTRNAME : ATTRS.KEYSET()) {
	// EXECUTIONSPECATTRIBUTE ATTR = ATTRS.GET(ATTRNAME);
	// OBJECT VALUE = ATTR.GETVALUE();
	// VALUES.PUT(ATTRNAME, VALUE);
	// }
	//
	// EXECUTIONFLOWDESCRIPTOR EFD = NEW EXECUTIONFLOWDESCRIPTOR(FLOWNAME,
	// VALUES, EXECUTIONSPEC);
	// REALIZEDFLOW.SETFLOWDESCRIPTOR(EFD);
	//
	// //
	// // EXECUTE THE FLOW
	// //
	// EXECUTE(REALIZEDFLOW, TRUE);
	// //
	// }
	// }
	//
	// PROTECTED MAP<STRING, EXECUTIONSPECATTRIBUTE>
	// READEXECUTIONSPECATTRIBUTES(
	// NODE NODE) {
	// TRY {
	// MAP<STRING, EXECUTIONSPECATTRIBUTE> ATTRS = NEW HASHMAP<STRING,
	// EXECUTIONSPECATTRIBUTE>();
	// FOR (NODEITERATOR NIT = NODE.GETNODES(); NIT.HASNEXT();) {
	// NODE SPECATTRNODE = NIT.NEXTNODE();
	// IF (SPECATTRNODE
	// .ISNODETYPE(SLCTYPES.SLC_PRIMITIVE_SPEC_ATTRIBUTE)) {
	// STRING TYPE = SPECATTRNODE.GETPROPERTY(SLC_TYPE)
	// .GETSTRING();
	// OBJECT VALUE = NULL;
	// IF (SPECATTRNODE.HASPROPERTY(SLC_VALUE)) {
	// STRING VALUESTR = SPECATTRNODE.GETPROPERTY(SLC_VALUE)
	// .GETSTRING();
	// VALUE = PRIMITIVEUTILS.CONVERT(TYPE, VALUESTR);
	// }
	// PRIMITIVESPECATTRIBUTE SPECATTR = NEW PRIMITIVESPECATTRIBUTE(
	// TYPE, VALUE);
	// ATTRS.PUT(SPECATTRNODE.GETNAME(), SPECATTR);
	// } ELSE IF (SPECATTRNODE
	// .ISNODETYPE(SLCTYPES.SLC_REF_SPEC_ATTRIBUTE)) {
	// IF (!SPECATTRNODE.HASPROPERTY(SLC_VALUE)) {
	// CONTINUE;
	// }
	// INTEGER VALUE = (INT) SPECATTRNODE.GETPROPERTY(SLC_VALUE)
	// .GETLONG();
	// REFSPECATTRIBUTE SPECATTR = NEW REFSPECATTRIBUTE();
	// NODEITERATOR CHILDREN = SPECATTRNODE.GETNODES();
	// INT INDEX = 0;
	// STRING ID = NULL;
	// WHILE (CHILDREN.HASNEXT()) {
	// NODE CHILD = CHILDREN.NEXTNODE();
	// IF (INDEX == VALUE)
	// ID = CHILD.GETNAME();
	// INDEX++;
	// }
	// SPECATTR.SETVALUE(ID);
	// ATTRS.PUT(SPECATTRNODE.GETNAME(), SPECATTR);
	// }
	// // THROW NEW SLCEXCEPTION("UNSUPPORTED SPEC ATTRIBUTE "
	// // + SPECATTRNODE);
	// }
	// RETURN ATTRS;
	// } CATCH (REPOSITORYEXCEPTION E) {
	// THROW NEW SLCEXCEPTION("CANNOT READ SPEC ATTRIBUTES FROM " + NODE,
	// E);
	// }
	// }

	protected JcrExecutionProcess getJcrExecutionProcess() {
		return (JcrExecutionProcess) getProcess();
	}
}
