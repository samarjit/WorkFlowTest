package org.jbpm.samarjit;

import org.drools.definition.process.Node;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.NodeInstanceContainer;

public class StatelessNodeInstanceFactory implements INodeInstanceFactory{
	
	public final Class<? extends NodeInstance> cls;
    
    public StatelessNodeInstanceFactory(Class<? extends NodeInstance> cls){
        this.cls = cls;
    }
    
	public StatelessNodeInstance getNodeInstance(Node node, StatelessProcessInstance processInstance, NodeInstanceContainer nodeInstanceContainer) {
		try {
            StatelessNodeInstanceImpl nodeInstance = (StatelessNodeInstanceImpl) this.cls.newInstance();
            nodeInstance.setNodeId(node.getId());
            nodeInstance.setNodeInstanceContainer(nodeInstanceContainer);
            nodeInstance.setProcessInstance(processInstance);
            return nodeInstance;
        } catch (Exception e) {
        	e.printStackTrace();
            throw new RuntimeException("Unable to instantiate node: '"
                + this.cls.getName() + "':" + e.getMessage());
        }
	}

}
