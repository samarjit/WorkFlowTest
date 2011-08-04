package org.jbpm.samarjit.mynodeinst;

import org.drools.runtime.process.NodeInstance;
import org.jbpm.samarjit.StatelessNodeInstanceImpl;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.instance.impl.NodeInstanceImpl;


public class StatelessStartNodeInstance extends StatelessNodeInstanceImpl {

    private static final long serialVersionUID = 510l;

    public void internalTrigger(final NodeInstance from, String type) {
        if (type != null) {
            throw new IllegalArgumentException(
                "A StartNode does not accept incoming connections!");
        }
        if (from != null) {
            throw new IllegalArgumentException(
                "A StartNode can only be triggered by the process itself!");
        }
        triggerCompleted();
    }
    
    public StartNode getStartNode() {
        return (StartNode) getNode();
    }

    public void triggerCompleted() {
        triggerCompleted(org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE, true);
    }
    
}
