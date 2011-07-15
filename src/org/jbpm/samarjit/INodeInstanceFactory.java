package org.jbpm.samarjit;

import org.drools.definition.process.Node;
import org.drools.runtime.process.NodeInstanceContainer;

public interface INodeInstanceFactory {
	public StatelessNodeInstance getNodeInstance(Node node, StatelessProcessInstance processInstance, NodeInstanceContainer nodeInstanceContainer) ;
}
