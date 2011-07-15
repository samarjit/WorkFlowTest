package org.jbpm.samarjit;

import java.util.Iterator;

import org.drools.event.ProcessNodeTriggeredEventImpl;
import org.drools.event.process.ProcessEventListener;
import org.drools.event.process.ProcessNodeTriggeredEvent;
import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.process.NodeInstance;

/**
 * @author Samarjit
 * NOT IMPLEMENTED YET
 */
@Deprecated
public class StatelessEventEngine {
	 public void fireBeforeNodeTriggered(NodeInstance nodeInstance, KnowledgeRuntime kruntime) {
		 /*  82 */     Iterator<?> iter = getEventListenersIterator();
		 /*     */ 
		 /*  84 */     if (iter.hasNext()) {
		 /*  85 */       ProcessNodeTriggeredEvent event = new ProcessNodeTriggeredEventImpl(nodeInstance, kruntime);
		 /*     */       do
		 /*     */       {
		 /*  88 */         ((ProcessEventListener)iter.next()).beforeNodeTriggered(event); }
		 /*  89 */       while (iter.hasNext());
		 /*     */     }
		 /*     */   }

	private Iterator<?> getEventListenersIterator() {
		// TODO Auto-generated method stub
		return null;
	}
}
