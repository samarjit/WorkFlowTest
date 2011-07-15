package org.jbpm.samarjit;

import java.util.HashMap;
import java.util.Map;

import org.drools.definition.process.Node;
import org.jbpm.samarjit.mynodeinst.StatelessActionNodeInstance;
import org.jbpm.samarjit.mynodeinst.StatelessEndNodeInstance;
import org.jbpm.samarjit.mynodeinst.StatelessHumanTaskNodeInstance;
import org.jbpm.samarjit.mynodeinst.StatelessJoinInstance;
import org.jbpm.samarjit.mynodeinst.StatelessReuseNodeInstanceFactory;
import org.jbpm.samarjit.mynodeinst.StatelessSplitInstance;
import org.jbpm.samarjit.mynodeinst.StatelessStartNodeInstance;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.CompositeContextNode;
import org.jbpm.workflow.core.node.CompositeNode;
import org.jbpm.workflow.core.node.DynamicNode;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.EventNode;
import org.jbpm.workflow.core.node.FaultNode;
import org.jbpm.workflow.core.node.ForEachNode;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.jbpm.workflow.core.node.Join;
import org.jbpm.workflow.core.node.MilestoneNode;
import org.jbpm.workflow.core.node.RuleSetNode;
import org.jbpm.workflow.core.node.Split;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.core.node.StateNode;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.jbpm.workflow.core.node.TimerNode;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.jbpm.workflow.instance.node.CompositeContextNodeInstance;
import org.jbpm.workflow.instance.node.CompositeNodeInstance;
import org.jbpm.workflow.instance.node.DynamicNodeInstance;
import org.jbpm.workflow.instance.node.EventNodeInstance;
import org.jbpm.workflow.instance.node.FaultNodeInstance;
import org.jbpm.workflow.instance.node.ForEachNodeInstance;
import org.jbpm.workflow.instance.node.MilestoneNodeInstance;
import org.jbpm.workflow.instance.node.RuleSetNodeInstance;
import org.jbpm.workflow.instance.node.StateNodeInstance;
import org.jbpm.workflow.instance.node.SubProcessNodeInstance;
import org.jbpm.workflow.instance.node.TimerNodeInstance;
import org.jbpm.workflow.instance.node.WorkItemNodeInstance;

public class StatelessNodeInstanceFactoryRegistry {
	   public static final StatelessNodeInstanceFactoryRegistry INSTANCE = new StatelessNodeInstanceFactoryRegistry();

	    private Map<Class< ? extends Node>, INodeInstanceFactory> registry;

	    private StatelessNodeInstanceFactoryRegistry() {
	        this.registry = new HashMap<Class< ? extends Node>, INodeInstanceFactory>();
	        System.out.println("StatelessNodeInstanceFactoryRegistry.class Samarjit Registering different NodeInstances");
	        // hard wired nodes:
	        register( RuleSetNode.class,
	                  new StatelessNodeInstanceFactory( RuleSetNodeInstance.class ) );
	        register( Split.class,
	                  new StatelessNodeInstanceFactory( StatelessSplitInstance.class ) );
	        register( Join.class,
	                  new StatelessReuseNodeInstanceFactory( StatelessJoinInstance.class ) );
	        register( StartNode.class,
	                  new StatelessNodeInstanceFactory( StatelessStartNodeInstance.class ) );
	        register( EndNode.class,
	                  new StatelessNodeInstanceFactory( StatelessEndNodeInstance.class ) );
	        register( MilestoneNode.class,
	                  new StatelessNodeInstanceFactory( MilestoneNodeInstance.class ) );
	        register( SubProcessNode.class,
	                  new StatelessNodeInstanceFactory( SubProcessNodeInstance.class ) );
	        register( ActionNode.class,
	                  new StatelessNodeInstanceFactory( StatelessActionNodeInstance.class ) );
	        register( WorkItemNode.class,
	                  new StatelessNodeInstanceFactory( WorkItemNodeInstance.class ) );
	        register( TimerNode.class,
	                  new StatelessNodeInstanceFactory( TimerNodeInstance.class ) );
	        register( FaultNode.class,
	                  new StatelessNodeInstanceFactory( FaultNodeInstance.class ) );
	        register( CompositeNode.class,
	                  new StatelessNodeInstanceFactory( CompositeNodeInstance.class ) );
	        register( CompositeContextNode.class,
	                  new StatelessNodeInstanceFactory( CompositeContextNodeInstance.class ) );
	        register( HumanTaskNode.class,
	                  new StatelessNodeInstanceFactory( StatelessHumanTaskNodeInstance.class ) );
	        register( ForEachNode.class,
	                  new StatelessNodeInstanceFactory( ForEachNodeInstance.class ) );
	        register( EventNode.class,
	                  new StatelessNodeInstanceFactory( EventNodeInstance.class ) );
	        register( StateNode.class,
	                  new StatelessNodeInstanceFactory( StateNodeInstance.class ) );
	        register( DynamicNode.class,
	                  new StatelessNodeInstanceFactory( DynamicNodeInstance.class ) );
	    }

	    public void register(Class< ? extends Node> cls,
	                         INodeInstanceFactory factory) {
	        this.registry.put( cls,
	                           factory );
	    }

	    public INodeInstanceFactory getProcessNodeInstanceFactory(Node node) {
	    	Class<?> clazz = node.getClass();
	        while (clazz != null) {
	        	INodeInstanceFactory result = this.registry.get( clazz );
	        	if (result != null) {
	        		return result;
	        	}
	        	clazz = clazz.getSuperclass();
	        }
	        return null;
	    }
}
