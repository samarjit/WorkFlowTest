package org.jbpm.samarjit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.drools.runtime.process.EventListener;
import org.drools.runtime.process.ProcessInstance;
import org.jbpm.process.instance.event.SignalManager;

//DefaultSignalManager
public class StatelessSignalManager implements SignalManager{
	private Map<String, List<EventListener>> processEventListeners;
	
	public void signalEvent(String type, Object event) {
		executeQueuedActions();
	}

	
	private void executeQueuedActions() {
		System.out.println("StatelessSignalManager { executeQueuedActions()} not implemented!");
	}


	public void signalEvent(long processInstanceId, String type, Object event) {
		ProcessInstance processInstance = StatelessRuntime.eINSTANCE.getProcessInstanceManager().getProcessInstance(1);
		if (processInstance != null) {
			//kruntime.queueWorkingMemoryAction(new SignalProcessInstanceAction(processInstanceId, type, event));
			//kruntime.
			executeQueuedActions();
		}
	}
	public void internalSignalEvent(String type, Object event) {
		if (processEventListeners != null) {
			List<EventListener> eventListeners = processEventListeners.get(type);
			if (eventListeners != null) {
				for (EventListener eventListener: eventListeners) {
					eventListener.signalEvent(type, event);
				}
			}
		}
	}
	
	public void addEventListener(String type, EventListener eventListener) {
		if (processEventListeners == null) {
			processEventListeners = new HashMap<String, List<EventListener>>();
		}
		List<EventListener> eventListeners = processEventListeners.get(type);
		if (eventListeners == null) {
			eventListeners = new CopyOnWriteArrayList<EventListener>();
			processEventListeners.put(type, eventListeners);
		}
		eventListeners.add(eventListener);
	}

	
	public void removeEventListener(String type, EventListener eventListener) {
		if (processEventListeners != null) {
			List<EventListener> eventListeners = processEventListeners.get(type);
			if (eventListeners != null) {
				eventListeners.remove(eventListener);
			}
		}
	}

}
