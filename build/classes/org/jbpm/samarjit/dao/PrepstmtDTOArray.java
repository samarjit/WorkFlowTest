package org.jbpm.samarjit.dao; 
  
 import java.sql.Timestamp;
import java.util.ArrayList; 
import java.util.Date;
  
 public class PrepstmtDTOArray    { 
 private ArrayList<PrepstmtDTO> ardto =null; 
  
 public ArrayList<PrepstmtDTO> getArdto() { 
         return ardto; 
 } 
 public PrepstmtDTOArray(){ 
         ardto = new ArrayList<PrepstmtDTO>(); 
 } 
 public void add( PrepstmtDTO.DataType dt,String data){ 
         ardto.add(new PrepstmtDTO(dt,data));     
 }
 public void add( PrepstmtDTO.DataType dt,Date data){ 
     ardto.add(new PrepstmtDTO(dt,data.toString()));     
 }
 public void add( PrepstmtDTO.DataType dt,Timestamp data){ 
     ardto.add(new PrepstmtDTO(dt,data.toString()));     
 }
 public String   toString(String SQL){ 
         String retval = "PerpstmtDTOArray:"; 
         //String[] sqlar = (SQL+" ").split("\\?"); 
         String[] sqlar = SQL.split("\\?"); 
         int i =0 ; 
         for ( i =0 ;i< ardto.size();i++) { 
                 PrepstmtDTO itr =  ardto.get(i); 
                 retval = retval + sqlar[i] +" '"+itr.getData() +"'|"+itr.getTypeString(); 
         } 
         if(i+1 == sqlar.length) 
         retval = retval + sqlar[i]; 
         return retval; 
 } 
  
  
 } 
 