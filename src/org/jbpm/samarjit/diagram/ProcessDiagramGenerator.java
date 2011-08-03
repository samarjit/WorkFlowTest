package org.jbpm.samarjit.diagram;

/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.definition.process.Connection;
import org.drools.definition.process.Node;
import org.drools.definition.process.WorkflowProcess;
import org.jbpm.bpmn2.xml.BPMNSemanticModule;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.Join;
import org.jbpm.workflow.core.node.Split;

/**
 * Class to generate an image based the diagram interchange information in a
 * BPMN 2.0 process.
 * 
 * @author Joram Barrez
 */
public class ProcessDiagramGenerator {
  
	public static class TransitionImpl {
		Connection connection;
		
		public TransitionImpl(Connection connection) {
			this.connection = connection;
		}

		private static final long serialVersionUID = 1L;

		public List<Integer> getWaypoints() {
			List<Integer> waypoints = new ArrayList<Integer>();
			String bendpoints = (String) connection.getMetaData().get("bendpoints");
        	Integer x = (Integer) connection.getFrom().getMetaData().get("x");
        	if (x == null) {
        		x = 0;
        	}
        	
        	
        	Integer y = (Integer) connection.getFrom().getMetaData().get("y");
        	if (y == null) {
        		y = 0;
        	}
        	Integer width = (Integer) connection.getFrom().getMetaData().get("width");
        	if (width == null) {
        		width = 40;
        	}
        	Integer height = (Integer) connection.getFrom().getMetaData().get("height");
        	if (height == null) {
        		height = 40;
        	}
        	
        	x = x + width/2;
        	y = y + height/2;
        	waypoints.add(x);
        	waypoints.add(y);
        	
        	if (bendpoints != null) {
            	bendpoints = bendpoints.substring(1, bendpoints.length() - 1);
            	String[] points = bendpoints.split(";");
            	for (String point: points) {
            		String[] coords = point.split(",");
            		if (coords.length == 2) {
            			waypoints.add(Integer.parseInt(coords[0]));
            			waypoints.add(Integer.parseInt(coords[1]));
            		}
            	}
            }
        	
        	x = (Integer) connection.getTo().getMetaData().get("x");
        	if (x == null) {
        		x = 0;
        	}
        	
        	y = (Integer) connection.getTo().getMetaData().get("y");
        	if (y == null) {
        		y = 0;
        	}
        	
        	width = (Integer) connection.getTo().getMetaData().get("width");
        	if (width == null) {
        		width = 40;
        	}
        	height = (Integer) connection.getTo().getMetaData().get("height");
        	if (height == null) {
        		height = 40;
        	}
        	x = x + width/2;
        	y = y + height/2;
        	
        	waypoints.add(x);
        	waypoints.add(y);
        	
			return waypoints;
		}

	}

   public static class ActivityImpl {
	private NodeImpl node;   
	public ActivityImpl(Node activity) {
		node = (NodeImpl) activity;
	}
	public String getProperty(String string) {
		if(string.equals("name")){
			return node.getName();
		}
		
		else if(string.equals("type")){
			BPMNSemanticModule semanticModule = new BPMNSemanticModule();
			String nodeName="";
			if(semanticModule.getXMLNodeNameByNodeClass(node.getClass()) == null){
				System.out.println(node.getClass()+" "+semanticModule.getHandlerByClass(node.getClass())+ " "+node.getName());
				if( node instanceof Split){
					Split split = (Split) node;
					switch (split.getType()) {
					case Split.TYPE_AND: nodeName = "parallelGateway";break;
					case Split.TYPE_XOR: nodeName = "exclusiveGateway";break;
					case Split.TYPE_OR:  nodeName = "inclusiveGateway";break;
					case Split.TYPE_XAND:nodeName = "eventBasedGateway";break;
					default:             nodeName = "complexGateway";
					}
				}else if(node instanceof Join){
					Join join = (Join) node;
					switch (join.getType()) {
						case Join.TYPE_AND: nodeName = "parallelGateway";break;
						case Join.TYPE_XOR: nodeName = "exclusiveGateway";break;
						default:            nodeName = "complexGateway";
					}
				}else if(node instanceof EndNode){
					nodeName = "endEvent";
				}else if(node instanceof ActionNode){
					nodeName = "scriptTask";
				}
				
			}else{
				nodeName = semanticModule.getXMLNodeNameByNodeClass(node.getClass());
				System.out.println(semanticModule.getXMLNodeNameByNodeClass(node.getClass()));
			}
			return nodeName;
		}
		else if(string.equals("multiInstance")){
			return null;
		}
		else{
			throw new DiagramException("Property not defined in ActivitiImpl:"+string);
		}
	}
	public int getX() {
		return (Integer) node.getMetaData().get("x");
	}

	public int getY() {
		return (Integer) node.getMetaData().get("y");
	}
	public int getWidth() {
		return (Integer)node.getMetaData().get("width");
	}
	
	public int getHeight() {
		return (Integer) node.getMetaData().get("height");
	}
	public long getId() {
		return node.getId();
	}
	public List<Connection> getDefaultOutgoingConnections() {
		return node.getDefaultOutgoingConnections();
	}
	
  }

protected static final Map<String, ActivityDrawInstruction> activityDrawInstructions = new HashMap<String, ActivityDrawInstruction>();
  
  // The instructions on how to draw a certain construct is 
  // created statically and stored in a map for performance.
  static {
    // start event
    activityDrawInstructions.put("startEvent", new ActivityDrawInstruction() {
      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl) {
        processDiagramCreator.drawNoneStartEvent(activityImpl.getX(), activityImpl.getY(), 
                activityImpl.getWidth(), activityImpl.getHeight());
      }
    });

    // start timer event
    activityDrawInstructions.put("startTimerEvent", new ActivityDrawInstruction() {
      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl) {
        processDiagramCreator.drawTimerStartEvent(activityImpl.getX(), activityImpl.getY(),
                activityImpl.getWidth(), activityImpl.getHeight());
      }
    });

    // end event
    activityDrawInstructions.put("endEvent", new ActivityDrawInstruction() {
      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl) {
        processDiagramCreator.drawNoneEndEvent(activityImpl.getX(), activityImpl.getY(), 
                activityImpl.getWidth(), activityImpl.getHeight());
      }
    });
    
    // error end event
    activityDrawInstructions.put("errorEndEvent", new ActivityDrawInstruction() {
      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl) {
        processDiagramCreator.drawErrorEndEvent(activityImpl.getX(), activityImpl.getY(), 
                activityImpl.getWidth(), activityImpl.getHeight());
      }
    });
    
    
    // task
    activityDrawInstructions.put("task", new ActivityDrawInstruction() {
      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl) {
        processDiagramCreator.drawTask((String) activityImpl.getProperty("name"), activityImpl.getX(),
                activityImpl.getY(), activityImpl.getWidth(), activityImpl.getHeight());
      }
    });
    
    // user task
    activityDrawInstructions.put("userTask", new ActivityDrawInstruction() {
      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl) {
        processDiagramCreator.drawUserTask((String) activityImpl.getProperty("name"), activityImpl.getX(),
                activityImpl.getY(), activityImpl.getWidth(), activityImpl.getHeight());
      }
    });
    
    // script task
    activityDrawInstructions.put("scriptTask", new ActivityDrawInstruction() {
      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl) {
        processDiagramCreator.drawScriptTask((String) activityImpl.getProperty("name"), activityImpl.getX(),
                activityImpl.getY(), activityImpl.getWidth(), activityImpl.getHeight());
      }
    });
    
    // service task
    activityDrawInstructions.put("serviceTask", new ActivityDrawInstruction() {
      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl) {
        processDiagramCreator.drawServiceTask((String) activityImpl.getProperty("name"), activityImpl.getX(),
                activityImpl.getY(), activityImpl.getWidth(), activityImpl.getHeight());
      }
    });
    
    // receive task
    activityDrawInstructions.put("receiveTask", new ActivityDrawInstruction() {
      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl) {
        processDiagramCreator.drawReceiveTask((String) activityImpl.getProperty("name"), activityImpl.getX(),
                activityImpl.getY(), activityImpl.getWidth(), activityImpl.getHeight());
      }
    });
    
    // send task
    activityDrawInstructions.put("sendTask", new ActivityDrawInstruction() {
      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl) {
        processDiagramCreator.drawSendTask((String) activityImpl.getProperty("name"), activityImpl.getX(),
                activityImpl.getY(), activityImpl.getWidth(), activityImpl.getHeight());
      }
    });
    
    // manual task
    activityDrawInstructions.put("manualTask", new ActivityDrawInstruction() {
      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl) {
        processDiagramCreator.drawManualTask((String) activityImpl.getProperty("name"), activityImpl.getX(),
                activityImpl.getY(), activityImpl.getWidth(), activityImpl.getHeight());
      }
    });
    
    // exclusive gateway
    activityDrawInstructions.put("exclusiveGateway", new ActivityDrawInstruction() {
      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl) {
        processDiagramCreator.drawExclusiveGateway(activityImpl.getX(), activityImpl.getY(), 
                activityImpl.getWidth(), activityImpl.getHeight());
      }
    });
    
    // parallel gateway
    activityDrawInstructions.put("parallelGateway", new ActivityDrawInstruction() {
      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl) {
        processDiagramCreator.drawParallelGateway(activityImpl.getX(), activityImpl.getY(), 
                activityImpl.getWidth(), activityImpl.getHeight());
      }
    });
    
    // Boundary timer
    activityDrawInstructions.put("boundaryTimer", new ActivityDrawInstruction() {
      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl) {
        processDiagramCreator.drawCatchingTimerEvent(activityImpl.getX(), activityImpl.getY(), 
                activityImpl.getWidth(), activityImpl.getHeight());
      }
    });
    
    // Boundary catch error
    activityDrawInstructions.put("boundaryError", new ActivityDrawInstruction() {
      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl) {
        processDiagramCreator.drawCatchingErroEvent(activityImpl.getX(), activityImpl.getY(), 
                activityImpl.getWidth(), activityImpl.getHeight());
      }
    });

    // timer catch event
    activityDrawInstructions.put("intermediateTimer", new ActivityDrawInstruction() {
      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl) {
        processDiagramCreator.drawCatchingTimerEvent(activityImpl.getX(), activityImpl.getY(),
                activityImpl.getWidth(), activityImpl.getHeight());
      }
    });
    
    // subprocess
    activityDrawInstructions.put("subProcess", new ActivityDrawInstruction() {
      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl) {
        Boolean isExpanded = true;//(Boolean) activityImpl.getProperty(BpmnParse.PROPERTYNAME_ISEXPANDED); //sub processes expanded
        if (isExpanded != null && isExpanded == false) {
          processDiagramCreator.drawCollapsedSubProcess((String) activityImpl.getProperty("name"), 
                  activityImpl.getX(), activityImpl.getY(), activityImpl.getWidth(), activityImpl.getHeight());
        } else {
          processDiagramCreator.drawExpandedSubProcess((String) activityImpl.getProperty("name"), 
                  activityImpl.getX(), activityImpl.getY(), activityImpl.getWidth(), activityImpl.getHeight());
        }
      }
    });
    
    // call activity
    activityDrawInstructions.put("callActivity", new ActivityDrawInstruction() {
      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl) {
        processDiagramCreator.drawCollapsedCallActivity((String) activityImpl.getProperty("name"), 
                  activityImpl.getX(), activityImpl.getY(), activityImpl.getWidth(), activityImpl.getHeight());
      }
    });
    
  }
  
  /**
   *  Generates a PNG diagram image of the given process definition,
   *  using the diagram interchange information of the process.
   */
  public static InputStream generatePngDiagram(WorkflowProcess processDefinition) {
    return generateDiagram(processDefinition, "png", Collections.<String>emptyList());
  }
  
  /**
   *  Generates a JPG diagram image of the given process definition,
   *  using the diagram interchange information of the process.
   */
  public static InputStream generateJpgDiagram(WorkflowProcess processDefinition) {
    return generateDiagram(processDefinition, "jpg", Collections.<String>emptyList());
  }

  protected static ProcessDiagramCanvas generateDiagram(WorkflowProcess processDefinition, List<String> highLightedActivities) {
	ProcessDiagramCanvas processDiagramCanvas = initProcessDiagramCanvas(processDefinition);
    for (Node activity : processDefinition.getNodes()) {
     drawActivity(processDiagramCanvas, new ActivityImpl(activity), highLightedActivities);

    }
    return processDiagramCanvas;
  }
    
  public static InputStream generateDiagram(WorkflowProcess processDefinition, String imageType, List<String> highLightedActivities) {
    return generateDiagram(processDefinition, highLightedActivities).generateImage(imageType);
  }
  
  
  
  private static class Point {
	
	private int x;
	private int y;

	public Point(int x,int y){	this.x = x;
		this.y = y;
	}
  }

	//start_point ---------------> end_point
  private static class  Lineseg {
	  private Point start_point;
	private Point end_point;
	public Lineseg(Point start_point,Point end_point){
		this.start_point = start_point;
		this.end_point = end_point;
	  }
		public float slope(){
			if((end_point.x-start_point.x) == 0)return 999;
			else return((float)(end_point.y - start_point.y))/(end_point.x-start_point.x); 
		}
	}

  private static class Rectangle{
	private int x;
	private int y;
	private int width;
	private int height;

	public Rectangle(int x, int y, int width, int height){
		this.x = x;
		this.y = y;
		this.width =  (width);
		this.height =  (height);
	}
  }

  private static Point findIntersection(Lineseg line /*Lineseg*/,Rectangle box /*Rectangle*/){ /*return Point */
		int outer_point;
		/*if(line.start_point.x < box.x || line.start_point.x > (box.x + box.width)){
			outer_point = line.start_point;
		}else{
			outer_point = line.end_point;
		}assume end_point is always inner_point
		*/
		/*handle yaxis parallel*/
		if(line.slope() == 999){
			/*top*/
			if(line.start_point.y < box.y){
				return new Point(line.start_point.x,box.y);
			}
			/*bottom*/
			if(line.start_point.y > (box.y+box.height)){
				return new Point(line.start_point.x, box.y+box.height);
			}
		}else{
			/*top y=10 box.y*/
			if(line.start_point.y < box.y){
				 int colx = (int) ((box.y - line.start_point.y)/ line.slope()+line.start_point.x);
				 if(colx > box.x &&  colx < (box.x+box.width)){
					 return new Point(colx, box.y);
				 }
			}
			/*right x=15 box.x+box.width*/
			if(line.start_point.x > (box.x+box.width)){
				 int coly =  (int) (line.slope()*(box.x+box.width - line.start_point.x)+line.start_point.y);
				 if(coly > box.y &&  coly < (box.y+box.height)){
					 return new Point(box.x+box.width,coly);
				 }
			}
			/*bottom y=20 box.y+box.height*/
			if(line.start_point.y > (box.y+box.height)){
				 int colx = (int) ((box.y+box.height - line.start_point.y)/ line.slope()+line.start_point.x);
				 if(colx > box.x &&  colx < (box.x+box.width)){
					 return new Point(colx, box.y+box.height);
				 }
			}
			/*left x=10 box.x*/
			if(line.start_point.x < box.x){
				 int coly =  (int) (line.slope()*(box.x - line.start_point.x)+line.start_point.y);
				 if(coly > box.y &&  coly < (box.y+box.height)){
					 return new Point(box.x,coly);
				 }
			}
		}
			
		return new Point(0,0);
	}
	
  protected static void drawActivity(ProcessDiagramCanvas processDiagramCanvas, ActivityImpl activity, List<String> highLightedActivities) {
    String type = (String) activity.getProperty("type");
    ActivityDrawInstruction drawInstruction = activityDrawInstructions.get(type);
    if (drawInstruction != null) {
      
      drawInstruction.draw(processDiagramCanvas, activity);
      
      // Gather info on the multi instance marker 
      boolean multiInstanceSequential = false, multiInstanceParallel = false, collapsed = false;
      String multiInstance = (String) activity.getProperty("multiInstance");
      if (multiInstance != null) {
        if ("sequential".equals(multiInstance)) {
          multiInstanceSequential = true;
        } else {
          multiInstanceParallel = true;
        }
      }
      
      // Gather info on the collapsed marker
      Boolean expanded = true;//(Boolean) activity.getProperty(BpmnParse.PROPERTYNAME_ISEXPANDED);
      if (expanded != null) {
        collapsed = !expanded;
      }
      
      // Actually draw the markers
      processDiagramCanvas.drawActivityMarkers(activity.getX(), activity.getY(), activity.getWidth(), 
              activity.getHeight(), multiInstanceSequential, multiInstanceParallel, collapsed);
      
      // Draw highlighted activities
      if (highLightedActivities.contains(activity.getId())) {
          drawHighLight(processDiagramCanvas, activity);
      }
      
    }
    
    // Outgoing transitions of activity
    for (Connection sequenceFlow : activity.getDefaultOutgoingConnections()) {
    	 
      List<Integer> waypoints = new TransitionImpl( sequenceFlow).getWaypoints();
//      for (int i=2; i < waypoints.size(); i+=2) { // waypoints.size() minimally 4: x1, y1, x2, y2 
//        boolean drawConditionalIndicator = (i == 2) /*samarjit*/ && false
////          && sequenceFlow.getProperty(BpmnParse.PROPERTYNAME_CONDITION) != null //samarjit let there be all arrows
////          && !((String) activity.getProperty("type")).toLowerCase().contains("gateway")
//        ;
//        if (i < waypoints.size() - 2) {
//          processDiagramCanvas.drawSequenceflowWithoutArrow(waypoints.get(i-2), waypoints.get(i-1), 
//                waypoints.get(i), waypoints.get(i+1), drawConditionalIndicator);
//        } else {
//          processDiagramCanvas.drawSequenceflow(waypoints.get(i-2), waypoints.get(i-1), 
//                  waypoints.get(i), waypoints.get(i+1), drawConditionalIndicator);
//        }
//      }
      
      int size = waypoints.size() -1;
      Point end_point = new Point(waypoints.get(size-1), waypoints.get(size));
      Point start_point = new Point(waypoints.get(size-3),waypoints.get(size-2));
	  Lineseg lastEdge = new Lineseg(start_point, end_point);
	   
	  Integer x = (Integer) sequenceFlow.getTo().getMetaData().get("x");
        	if (x == null) {
        		x = 0;
        	}
        	
      Integer y = (Integer) sequenceFlow.getTo().getMetaData().get("y");
        	if (y == null) {
        		y = 0;
        	}
        	
      Integer	width = (Integer) sequenceFlow.getTo().getMetaData().get("width");
        	if (width == null) {
        		width = 40;
        	}
      Integer	height = (Integer) sequenceFlow.getTo().getMetaData().get("height");
        	if (height == null) {
        		height = 40;
        	}
	  Rectangle rectangle = new Rectangle(x, y, width, height);
		Point intersect_point = findIntersection(lastEdge,rectangle);
		
	  
		
		//To find the origin
	      Point end_point1 = new Point(waypoints.get(0), waypoints.get(1));
	      Point start_point1 = new Point(waypoints.get(2),waypoints.get(3));
		  Lineseg lastEdge1 = new Lineseg(start_point1, end_point1);
		   
		  Integer x1 = (Integer) sequenceFlow.getFrom().getMetaData().get("x");
	        	if (x1 == null) {
	        		x1 = 0;
	        	}
	        	
	      Integer y1 = (Integer) sequenceFlow.getFrom().getMetaData().get("y");
	        	if (y1 == null) {
	        		y1 = 0;
	        	}
	        	
	      Integer	width1 = (Integer) sequenceFlow.getFrom().getMetaData().get("width");
	        	if (width1 == null) {
	        		width1 = 40;
	        	}
	      Integer	height1 = (Integer) sequenceFlow.getFrom().getMetaData().get("height");
	        	if (height1 == null) {
	        		height1 = 40;
	        	}
		  Rectangle rectangle1 = new Rectangle(x1, y1, width1, height1);
			Point intersect_point1 = findIntersection(lastEdge1,rectangle1);

			if (size == 3) {
				processDiagramCanvas.drawSequenceflow(intersect_point1.x, intersect_point1.y, intersect_point.x, intersect_point.y, false);
			} else {
				processDiagramCanvas.drawSequenceflowWithoutArrow(intersect_point1.x, intersect_point1.y, start_point1.x, start_point1.y, false);
				for (int i = 4; i < size - 3; i += 4) {
					processDiagramCanvas.drawSequenceflowWithoutArrow(waypoints.get(i), waypoints.get(i + 1), waypoints.get(i + 2), waypoints.get(i + 3), false);
				}
				processDiagramCanvas.drawSequenceflow(start_point.x, start_point.y, intersect_point.x, intersect_point.y, false);
			}
    }
    
    // Nested activities (boundary events)
//    System.out.println("We will deal with nested activity later");
//    for (ActivityImpl nestedActivity : activity.getActivities()) {
//      drawActivity(processDiagramCanvas, nestedActivity, highLightedActivities);
//    }
  }

  private static void drawHighLight(ProcessDiagramCanvas processDiagramCanvas, ActivityImpl activity) {
      processDiagramCanvas.drawHighLight(activity.getX(), activity.getY(), activity.getWidth(), activity.getHeight());

  }

    protected static ProcessDiagramCanvas initProcessDiagramCanvas(WorkflowProcess processDefinition) {
    int minX = Integer.MAX_VALUE;
    int maxX = 0;
    int minY = Integer.MAX_VALUE;
    int maxY = 0;
    
    for (Node activity : processDefinition.getNodes()) {
    	Integer nx = (Integer) activity.getMetaData().get("x");
        Integer ny = (Integer) activity.getMetaData().get("y");
        Integer nwidth = (Integer) activity.getMetaData().get("width");
        Integer nheight = (Integer) activity.getMetaData().get("height");
        
      // width
      if (nx + nwidth > maxX) {
        maxX = nx + nwidth;
      }
      if (nx < minX) {
        minX = nx;
      }
      // height
      if (ny + nheight > maxY) {
        maxY = ny + nheight;
      }
      if (ny < minY) {
        minY = ny;
      }
      List<Connection> connections = new ArrayList<Connection>();
      for (Node node: processDefinition.getNodes()) {
          for (List<Connection> connectionList: node.getIncomingConnections().values()) {
              connections.addAll(connectionList);
          }
      }
      
      for (Connection connection : connections) {
    	  List<Integer> waypoints = new TransitionImpl( connection).getWaypoints(); 
    	  for (int i=0; i < waypoints.size(); i+=2) {
              // width
              if (waypoints.get(i) > maxX) {
                maxX = waypoints.get(i);
              }
              if (waypoints.get(i) < minX) {
                minX = waypoints.get(i);
              }
              // height
              if (waypoints.get(i+1) > maxY) {
                maxY = waypoints.get(i+1);
              }
              if (waypoints.get(i+1) < minY) {
                minY = waypoints.get(i+1);
              }
            }
      }
    }
    return new ProcessDiagramCanvas(maxX + 10, maxY + 10, minX, minY);
  }
  
  protected interface ActivityDrawInstruction {
    
    void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl);
    
  }
  
}
