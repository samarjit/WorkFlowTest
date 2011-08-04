package org.jbpm.samarjit.dao; 

  
 public class PrepstmtDTO { 
 //public static String STRING="STRING"; 
 //public static String INT="INT"; 
 //public static String DATE="DATE"; 
 //public static String DOUBLE="DOUBLE"; 
 //public static String FLOAT="FLOAT"; 
          
 public PrepstmtDTO(DataType type,String data){ 
         this.type = type; 
         this.data = data; 
 } 
 public DataType type; 
 public String getData() { 
         return data; 
 } 
 public void setData(String data) { 
         this.data = data; 
 } 
  
 public static enum DataType { 
         STRING,INT,DATEDDMMYYYY,FLOAT,DOUBLE, TIMESTAMP, DATE_NS, LONG 
 } 
 public DataType getType() { 
         return type; 
 } 
 public String getTypeString() { 
	 if(type == DataType.TIMESTAMP)
		 return "TIMESTAMP"; 
	 if(type == DataType.DATE_NS)
		 return "DATE_NS"; 
     if(type == DataType.STRING)
    	 return "STRING"; 
     if(type == DataType.INT)
    	 return "INT"; 
     if(type == DataType.DATEDDMMYYYY)
    	 return "DATEDDMMYYYY"; 
     if(type == DataType.FLOAT)
    	 return "FLOAT";
     if(type == DataType.DOUBLE)
    	 return "DOUBLE"; 
     if(type == DataType.LONG)
    	 return "LONG"; 
     else
    	 return "INVALID_TYPE";
 }
 public static DataType getDataTypeFrmStr(String type) throws Exception{
	 if(type.equals("STRING") )
    	 return DataType.STRING; 
	 if(type.equals("INT") )
    	 return DataType.INT; 
	 if(type.equals("DATEDDMMYYYY") )
    	 return DataType.DATEDDMMYYYY; 
	 if(type.equals("DATE_NS") )
		 return DataType.DATE_NS; 
	 if(type.equals("TIMESTAMP") )
		 return DataType.TIMESTAMP; 
	 if(type.equals("FLOAT") )
    	 return DataType.FLOAT;
	 if(type.equals("DOUBLE") )
    	 return DataType.DOUBLE; 
	 if(type.equals("LONG") )
		 return DataType.LONG; 
     else
    	 throw new Exception("DataType undefined");
 }
 
 public void setType(DataType type) { 
         this.type = type; 
 } 
          
 public String data; 
  
 } 