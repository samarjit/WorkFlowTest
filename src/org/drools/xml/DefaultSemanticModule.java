/*    */ package org.drools.xml;
/*    */ 
/*    */ import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
/*    */ 
/*    */ public class DefaultSemanticModule
/*    */   implements SemanticModule
/*    */ {
/*    */   public String uri;
/*    */   public Map<String, Handler> handlers;
/*    */   public Map<Class<?>, Handler> handlersByClass;
/*    */ 
/*    */   public DefaultSemanticModule(String uri)
/*    */   {
/* 28 */     this.uri = uri;
/* 29 */     this.handlers = new HashMap();
/* 30 */     this.handlersByClass = new HashMap();
/*    */   }
/*    */ 
/*    */   public String getUri() {
/* 34 */     return this.uri;
/*    */   }
/*    */ 
/*    */   public void addHandler(String name, Handler handler) {
/* 38 */     this.handlers.put(name, handler);
/* 39 */     if ((handler != null) && (handler.generateNodeFor() != null))
/* 40 */       this.handlersByClass.put(handler.generateNodeFor(), handler);
/*    */   }
/*    */ 
/*    */   public Handler getHandler(String name)
/*    */   {
/* 45 */     return ((Handler)this.handlers.get(name));
/*    */   }
/*    */ 
/*    */   public Handler getHandlerByClass(Class<?> clazz) {
/* 49 */     while (clazz != null) {
/* 50 */       Handler handler = (Handler)this.handlersByClass.get(clazz);
/* 51 */       if (handler != null) {
/* 52 */         return handler;
/*    */       }
/* 54 */       clazz = clazz.getSuperclass();
/*    */     }
/* 56 */     return null;
/*    */   }
		   public String getXMLNodeNameByNodeClass(Class<?> clazz){
			   Handler handler = null;
			   while(clazz != null) {
				handler = (Handler)this.handlersByClass.get(clazz);
			      if (handler != null) {
			        break;
			      }
			      clazz = clazz.getSuperclass();
			    }
			   for (Entry<String, Handler> entry : handlers.entrySet()) {
				   if(entry.getValue() == handler){
					   return entry.getKey();
				   }
			   	}
			return null;
		   }
/*    */ }

/* Location:           C:\Users\Samarjit\Downloads\maven-3.0-bin\maven-3.0\mvnrepository\org\drools\drools-core\5.3.0-SNAPSHOT\drools-core-5.3.0-SNAPSHOT.jar
 * Qualified Name:     org.drools.xml.DefaultSemanticModule
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.5.3
 */