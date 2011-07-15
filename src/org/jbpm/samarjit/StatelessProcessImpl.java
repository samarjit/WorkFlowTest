package org.jbpm.samarjit;

import org.drools.definition.process.Node;
import org.drools.definition.process.NodeContainer;
import org.jbpm.process.core.context.exception.ExceptionScope;
import org.jbpm.process.core.context.swimlane.SwimlaneContext;
import org.jbpm.process.core.context.variable.VariableScope;

import org.jbpm.workflow.core.impl.NodeContainerImpl;
import org.jbpm.workflow.core.impl.WorkflowProcessImpl;
import org.jbpm.workflow.core.node.StartNode;

public class StatelessProcessImpl extends WorkflowProcessImpl {
	private static final long serialVersionUID = 510l;
	
	public static final String RULEFLOW_TYPE = "RuleFlow";
	 
	public StatelessProcessImpl() {
        setType(RULEFLOW_TYPE);
        // TODO create contexts on request ?
        VariableScope variableScope = new VariableScope();
        addContext(variableScope);
        setDefaultContext(variableScope);
        SwimlaneContext swimLaneContext = new SwimlaneContext();
        addContext(swimLaneContext);
        setDefaultContext(swimLaneContext);
        ExceptionScope exceptionScope = new ExceptionScope();
        addContext(exceptionScope);
        setDefaultContext(exceptionScope);
    }
    
    public VariableScope getVariableScope() {
        return (VariableScope) getDefaultContext(VariableScope.VARIABLE_SCOPE);
    }
    
    public SwimlaneContext getSwimlaneContext() {
        return (SwimlaneContext) getDefaultContext(SwimlaneContext.SWIMLANE_SCOPE);
    }

    public ExceptionScope getExceptionScope() {
        return (ExceptionScope) getDefaultContext(ExceptionScope.EXCEPTION_SCOPE);
    }

    protected NodeContainer createNodeContainer() {
        return new WorkflowProcessNodeContainer();
    }
    
    public StartNode getStart() {
        Node[] nodes = getNodes();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] instanceof StartNode) {
                return (StartNode) nodes[i];
            }
        }
        return null;
    }

    private class WorkflowProcessNodeContainer extends NodeContainerImpl {
        
        private static final long serialVersionUID = 510l;

        protected void validateAddNode(Node node) {
            super.validateAddNode(node);
            if ((node instanceof StartNode) && (getStart() != null)) {
                throw new IllegalArgumentException(
                    "A RuleFlowProcess cannot have more than one start node!");
            }
        }
        
    }
}
