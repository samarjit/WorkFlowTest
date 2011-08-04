package org.jbpm.samarjit.mynodeinst;

import java.util.HashMap;
import java.util.Map;

import org.drools.runtime.process.NodeInstance;
import org.drools.spi.ProcessContext;
import org.jbpm.process.instance.impl.Action;
import org.jbpm.samarjit.StatelessNodeInstanceImpl;
import org.jbpm.samarjit.StatelessProcessContext;
import org.jbpm.samarjit.StatelessProcessInstance;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.node.ActionNode;
import org.mvel2.MVEL;

public class StatelessActionNodeInstance extends StatelessNodeInstanceImpl {

    private static final long serialVersionUID = 510l;

    protected ActionNode getActionNode() {
        return (ActionNode) getNode();
    }

    public void internalTrigger(final NodeInstance from, String type) {
        if (!org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "An ActionNode only accepts default incoming connections!");
        }
		Action action = (Action) getActionNode().getAction().getMetaData("Action");
		try {
			StatelessProcessContext context = new StatelessProcessContext();
			context.setNodeInstance(this);
			if(action != null){ //drools way if process builder is used
				action.execute(context);		    
			}else{
				DroolsConsequenceAction dca = (DroolsConsequenceAction) getActionNode().getAction();
				if(dca.getDialect().equals("mvel")){
					StatelessProcessInstance pInst = this.getProcessInstance();
					 Map<String, Object> variables = pInst.getVariableMap();
					 if(variables == null)variables = new HashMap<String, Object>();
					 variables.putAll(this.getVariableMap());
					System.out.println("StatelessActionNodeInstance: dca.getConsequence():"+dca.getConsequence()+" variables:"+variables); 
					MVEL.eval(dca.getConsequence(), variables);
					System.out.println("StatelessActionNodeInstance After dca.getConsequence() ");
				}
			}
		} catch (Exception e) {e.printStackTrace();
		    throw new RuntimeException("unable to execute Action", e);
		}
    	triggerCompleted();
    }

    public void triggerCompleted() {
        triggerCompleted(org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE, true);
    }
    
}
