package org.jbpm.samarjit.mynodeinst;

import org.drools.definition.process.Node;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.NodeInstanceContainer;
import org.jbpm.samarjit.INodeInstanceFactory;
import org.jbpm.samarjit.StatelessNodeInstance;
import org.jbpm.samarjit.StatelessNodeInstanceImpl;
import org.jbpm.samarjit.StatelessProcessInstance;

public class StatelessReuseNodeInstanceFactory implements INodeInstanceFactory{
    public final Class<? extends NodeInstance> cls;
    
    public StatelessReuseNodeInstanceFactory(Class<? extends NodeInstance> cls){
        this.cls = cls;
    }

	public  StatelessNodeInstance getNodeInstance(Node node, StatelessProcessInstance processInstance, NodeInstanceContainer nodeInstanceContainer) {    	
		StatelessNodeInstance result = (StatelessNodeInstance) ((org.jbpm.workflow.instance.NodeInstanceContainer)
    		nodeInstanceContainer).getFirstNodeInstance( node.getId() );
        if (result != null) {
            return result;
        }
        try {
        	StatelessNodeInstanceImpl nodeInstance = (StatelessNodeInstanceImpl) cls.newInstance();
            nodeInstance.setNodeId(node.getId());
            nodeInstance.setNodeInstanceContainer(nodeInstanceContainer);
            nodeInstance.setProcessInstance(processInstance);
            return nodeInstance;
        } catch (Exception e) {
            throw new RuntimeException("Unable to instantiate node '"
                + this.cls.getName() + "': " + e.getMessage());
        }
	}
}
