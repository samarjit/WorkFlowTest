package org.jbpm.samarjit.diagram;

public class DiagramException extends RuntimeException {
	String message;
	
	 
	
	
	  private static final long serialVersionUID = 1L;

	  public DiagramException(String message, Throwable cause) {
	    super(message, cause);
	    this.message = message +"\n"+ cause.getLocalizedMessage();
	  }

	  public DiagramException(String message) {
	    super(message);
	    this.message = message;
	  }
	  
	  public String toString(){
		  return message;
	  }
}
