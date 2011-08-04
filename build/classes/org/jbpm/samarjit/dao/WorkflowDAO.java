package org.jbpm.samarjit.dao;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.rowset.CachedRowSet;

import org.apache.ibatis.session.SqlSession;
import org.jbpm.samarjit.StatelessNodeInstance;
import org.jbpm.samarjit.dao.PrepstmtDTO.DataType;
import org.jbpm.samarjit.dto.ActHiTaskinst;
import org.jbpm.samarjit.dto.ActRuExecution;
import org.jbpm.samarjit.dto.ActRuTask;
import org.jbpm.samarjit.mynodeinst.MockStatelessNodeInstance;
import org.jbpm.workflow.instance.NodeInstance;

public class WorkflowDAO {
	public static void log(String s){
		System.out.println("WorkflowDAO:"+s);
	}
	
	public static ArrayList<String> getcurrentTask(String execution_id) {
		DBConnector db = new DBConnector();
		ArrayList<String> currentlist = new ArrayList<String>();
		try {
			String qryNextId = "select task_def_key_ from ACT_RU_TASK where execution_id_ = '"+execution_id+"';";
			CachedRowSet crs = db.executeQuery(qryNextId);
			while (crs.next()) {
				String id = crs.getString("task_def_key_");
				currentlist.add(id);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return currentlist;
	}
	
	public static ArrayList<String> getCompletedTask(String execution_id) {
		DBConnector db = new DBConnector();
		ArrayList<String> currentlist = new ArrayList<String>();
		try {
			String qryNextId = "select task_def_key_ from ACT_HI_TASKINST  where execution_id_ = '"+execution_id+"';";
			CachedRowSet crs = db.executeQuery(qryNextId);
			while (crs.next()) {
				String id = crs.getString("task_def_key_");
				currentlist.add(id);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return currentlist;
	}
	
	public static int getNextId(){
		DBConnector db = new DBConnector();
		int value=-1;
		try {
			String qryNextId = "select value_  from ACT_GE_PROPERTY where name_='next.dbid'";
			CachedRowSet crs = db.executeQuery(qryNextId);
			if(crs.next()){
				value = crs.getInt("VALUE_");
			}
			value++;
			String qryUpdNextId = "update ACT_GE_PROPERTY set value_= "+value +" where name_='next.dbid'";
			log(qryUpdNextId);
			db.executeUpdate(qryUpdNextId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return value;
	}
	public static void createProcessInstance(NodeInstance nodeInstance) {
		DBConnector db = new DBConnector();
		try {
//			int id = getNextId();
			String qryCreateProcInst = "insert into ACT_RU_EXECUTION (ID_,REV_,PROC_INST_ID_,PARENT_ID_,PROC_DEF_ID_ ,IS_ACTIVE_ ) values("
				+"'"+nodeInstance.getProcessInstance().getId()+"',0,'"+nodeInstance.getProcessInstance().getId()+"',null,'"+nodeInstance.getProcessInstance().getProcessId()+"'" +
						", "+nodeInstance.getProcessInstance().getState()+")";
			log(qryCreateProcInst);
			db.executeUpdate(qryCreateProcInst);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public static void completeProcessInstance(long Id) {
		DBConnector db = new DBConnector();
		try {
//			int id = getNextId();
			String qrySelProcCompleted = "select ID_,PROC_INST_ID_, BUSINESS_KEY_, PROC_DEF_ID_, ACT_ID_, START_TIME_ from ACT_RU_EXECUTION where PROC_INST_ID_='"+Id+"'";
			log(qrySelProcCompleted);
			CachedRowSet crs  = db.executeQuery(qrySelProcCompleted);
			while(crs.next()){
				String id = crs.getString("ID_");
				String processInstanceId = crs.getString("PROC_INST_ID_");
				String businessKey = crs.getString("BUSINESS_KEY_");
				String procDecId = crs.getString("PROC_DEF_ID_");
				String startActId = crs.getString("ACT_ID_");
				Timestamp startTime = crs.getTimestamp("START_TIME_");
			String sqlProcInsertHistNode = "insert into ACT_HI_PROCINST (ID_,PROC_INST_ID_, BUSINESS_KEY_, PROC_DEF_ID_, START_ACT_ID_, START_TIME_, END_TIME_, DURATION_)" +
				"values (?,?,?,?,?,?,?,?)";
				PrepstmtDTOArray prepStmtAr1 = new PrepstmtDTOArray();
				prepStmtAr1.add(DataType.STRING,id);
				prepStmtAr1.add(DataType.STRING,processInstanceId);
				prepStmtAr1.add(DataType.STRING,businessKey);
				prepStmtAr1.add(DataType.STRING,procDecId);
				prepStmtAr1.add(DataType.STRING,startActId);
			if(startTime == null)startTime= new Timestamp(new Date().getTime());
				prepStmtAr1.add(DataType.TIMESTAMP,startTime);
				prepStmtAr1.add(DataType.TIMESTAMP,new Timestamp(new Date().getTime()));
			Long duration = new Date().getTime() - startTime.getTime();
				prepStmtAr1.add(DataType.LONG,duration.toString());
				
				log(prepStmtAr1.toString(sqlProcInsertHistNode));
				db.executePreparedUpdate(sqlProcInsertHistNode, prepStmtAr1);
			}
			String qryCompleteProcInst = "delete from ACT_RU_EXECUTION where PROC_INST_ID_='"+Id+"'";
			log(qryCompleteProcInst);
			db.executeUpdate(qryCompleteProcInst);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public static void createNodeInstance(StatelessNodeInstance nodeInstance) {
		DBConnector db = new DBConnector();
		try {
//			int id = getNextId();
			String qryExistingNodeInst = "select ID_ from ACT_RU_TASK where ID_='"+nodeInstance.getId()+"'";
			log(qryExistingNodeInst);
			CachedRowSet crs = db.executeQuery(qryExistingNodeInst);
			if(crs.next()){
				return; //Do not create a node that already exisits .. like for Reused Join Nodes
			}
			
			String qryCreateNodeInst = "insert into ACT_RU_TASK (ID_,REV_,EXECUTION_ID_,PROC_INST_ID_,PROC_DEF_ID_,NAME_," +
					"PARENT_TASK_ID_,DESCRIPTION_,TASK_DEF_KEY_,OWNER_,ASSIGNEE_,DELEGATION_,PRIORITY_,CREATE_TIME_,DUE_DATE_, CLASS_NAME_" + 
					") values\n("
				+"'"+nodeInstance.getId()+"',0,'"+nodeInstance.getProcessInstance().getId()+"','"+nodeInstance.getProcessInstance().getId()+"','"+nodeInstance.getProcessInstance().getProcessId()+"','"+nodeInstance.getNodeName()+"'"
				+",null,'"+nodeInstance+"','"+nodeInstance.getNodeId()+"',null,null,null,null,'"+new Timestamp(new Date().getTime())+"',null, '"+nodeInstance.getClass().getCanonicalName()+"')";
			System.err.println("Starting node instande "+nodeInstance);
			log(qryCreateNodeInst);
			db.executeUpdate(qryCreateNodeInst);
			
//			SqlSession sqlSession = MybatisSessionHelper.eINSTANCE.openSession();
//			DBActivitiMapper dbActivitiMapper = sqlSession.getMapper(DBActivitiMapper.class);
//			String queryUserTask = "insert into ACT_RU_IDENTITYLINK  (ID_ ,REV_ ,GROUP_ID_ ,TYPE_ ,USER_ID_ ,TASK_ID_ ,SCREEN_URL_ )" +
//					"values ( (select NVL(max(ID_)+1,0) from ACT_RU_IDENTITYLINK),?,?,?,?,?,?)" ; 
//			PrepstmtDTOArray prepStmtAr = new PrepstmtDTOArray();
//			if(nodeInstance instanceof StatelessWorkItemNodeInstance){
//				StatelessWorkItemNodeInstance workItemNodeInst = (StatelessWorkItemNodeInstance)nodeInstance;
//				WorkItemNode workItemNode = (WorkItemNode) workItemNodeInst.getNode();
//				prepStmtAr.add(DataType.INT, "0"); //rev
//				prepStmtAr.add(DataType.STRING, "TODOSYSTEM"); //group_id_
//				prepStmtAr.add(DataType.STRING, "NA"); //type_
//				prepStmtAr.add(DataType.STRING, ""); //user_id_
//				prepStmtAr.add(DataType.LONG, Long.toString(workItemNodeInst.getId())); //task_id_ instance
//				prepStmtAr.add(DataType.STRING, "TODO"); //task_id_
//				//loop later based on user
//				db.executePreparedUpdate(queryUserTask, prepStmtAr);
//			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public static void completeNodeInstance(StatelessNodeInstance nodeInstance) {
		DBConnector db = new DBConnector();
		try {
//			int id = getNextId();
//			System.err.println("Completing node instande "+nodeInstance);//if(1==1)return;
			String qryCompleteNodeInst = "select ID_,EXECUTION_ID_,PROC_INST_ID_,PROC_DEF_ID_,NAME_,PARENT_TASK_ID_,DESCRIPTION_,TASK_DEF_KEY_ ,ASSIGNEE_,DUE_DATE_,PRIORITY_,OWNER_,CREATE_TIME_   from ACT_RU_TASK where ID_='"+nodeInstance.getId()+"'";
			log(qryCompleteNodeInst);
			CachedRowSet crs = db.executeQuery(qryCompleteNodeInst);
			while(crs.next()){
				String id = crs.getString("ID_");
				String executionId = crs.getString("EXECUTION_ID_");
				String processInstanceId = crs.getString("PROC_INST_ID_");
				String procDecId = crs.getString("PROC_DEF_ID_");
				String name = crs.getString("NAME_");
				String taskId = crs.getString("PARENT_TASK_ID_");
				String description = crs.getString("DESCRIPTION_");
				String taskDefKey = crs.getString("TASK_DEF_KEY_");
				String assignee = crs.getString("ASSIGNEE_");
				String dueDate = crs.getString("DUE_DATE_");
				Integer priority = crs.getInt("PRIORITY_");
				String owner = crs.getString("OWNER_");
				Timestamp createTime = crs.getTimestamp("CREATE_TIME_");
				
				String sqlInsertHistNode = "insert into ACT_HI_TASKINST (ID_,EXECUTION_ID_,PROC_INST_ID_,PROC_DEF_ID_,NAME_,PARENT_TASK_ID_,DESCRIPTION_,TASK_DEF_KEY_ ,ASSIGNEE_,DUE_DATE_,PRIORITY_,OWNER_,START_TIME_, END_TIME_, DURATION_)" +
						"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				PrepstmtDTOArray prepStmtAr = new PrepstmtDTOArray();
				prepStmtAr.add(DataType.STRING,id);
				prepStmtAr.add(DataType.STRING,executionId);
				prepStmtAr.add(DataType.STRING,processInstanceId);
				prepStmtAr.add(DataType.STRING,procDecId);
				prepStmtAr.add(DataType.STRING,name);
				prepStmtAr.add(DataType.STRING,taskId);
				prepStmtAr.add(DataType.STRING,description);
				prepStmtAr.add(DataType.STRING,taskDefKey);
				prepStmtAr.add(DataType.STRING,assignee);
				prepStmtAr.add(DataType.STRING,dueDate);
				if(priority == null)priority=0;
				prepStmtAr.add(DataType.INT,priority.toString());
				prepStmtAr.add(DataType.STRING,owner);
				if(createTime == null)createTime= new Timestamp(new Date().getTime());
				prepStmtAr.add(DataType.TIMESTAMP,createTime);
				prepStmtAr.add(DataType.TIMESTAMP,new Timestamp(new Date().getTime()));
				Long duration = new Date().getTime() - createTime.getTime();
				prepStmtAr.add(DataType.LONG,duration.toString());
				
				log(prepStmtAr.toString(sqlInsertHistNode));
				db.executePreparedUpdate(sqlInsertHistNode, prepStmtAr);
				
//				String qryDeleteIdentityLink= "delete from ACT_RU_IDENTITYLINK where TASK_ID_ = '"+nodeInstance.getId()+"'";
//				db.executeUpdate(qryDeleteIdentityLink);
				
				String qryDeleteCompletedNodeInst= "delete from ACT_RU_TASK where ID_='"+nodeInstance.getId()+"'";
				db.executeUpdate(qryDeleteCompletedNodeInst);
				
			}
//			if(saved)System.out.println("The completing instane has been saved.."+nodeInstance);
//			else System.out.println("The completing instane has failed to save.."+nodeInstance);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public static List<ActRuExecution> selectRunningWorkflows() {
		SqlSession sqlSession = MybatisSessionHelper.eINSTANCE.openSession();
		DBActivitiMapper dbActivitiMapper = sqlSession.getMapper(DBActivitiMapper.class);
		List<ActRuExecution> selectRunningWorkflows = dbActivitiMapper.selectRunningWorkflows();
		sqlSession.close();
		return selectRunningWorkflows;
	}
	
	
	public static ArrayList<MockStatelessNodeInstance> getCompletedInstances(
			ArrayList<Long> idList, long  processId) {
		DBConnector db = new DBConnector();
		ArrayList<MockStatelessNodeInstance> mockList = null;
		String qryHist = "select ID_, TASK_DEF_KEY_, NAME_ from ACT_HI_TASKINST where proc_inst_id_ = ?";
		PrepstmtDTOArray prepStmtAr = new PrepstmtDTOArray();
		prepStmtAr.add(DataType.STRING,Long.toString(processId));
		if(idList.size() >0)qryHist +=" AND TASK_DEF_KEY_ in (";
		for (Long nodeId : idList) {
			prepStmtAr.add(DataType.STRING,nodeId.toString());
			qryHist += "?,";
		}
		if(idList.size() >0){
			qryHist  = qryHist.substring(0,qryHist.length() - 1);
			qryHist +=")";
		}
		log(prepStmtAr.toString(qryHist));
		CachedRowSet crs = null;
		try{
			crs = db.executePreparedQuery(qryHist, prepStmtAr);
			mockList = new ArrayList<MockStatelessNodeInstance>();
			while(crs.next()){
				String idStr = crs.getString("ID_");
				String taskdef = crs.getString("TASK_DEF_KEY_");
				String nodeName = crs.getString("NAME_");
				MockStatelessNodeInstance mockNode = new MockStatelessNodeInstance();
				mockNode.setId(Long.parseLong(idStr));
				mockNode.setNodeId(Long.parseLong(taskdef));
				mockNode.setNodeName(nodeName);
				mockList.add(mockNode);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(crs != null){
				try {
					crs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return mockList;
	}

	public static List<MockStatelessNodeInstance> getHistory(int instanceid) {
		SqlSession sqlSession = MybatisSessionHelper.eINSTANCE.openSession();
		DBActivitiMapper dbActivitiMapper = sqlSession.getMapper(DBActivitiMapper.class);
		List<MockStatelessNodeInstance> historyTasks = dbActivitiMapper.selectMockHiTasks(String.valueOf(instanceid));
		List<MockStatelessNodeInstance> runningTasks = dbActivitiMapper.selectMockRunTasks(String.valueOf(instanceid));
		
		 
		historyTasks.addAll(runningTasks );
		
		sqlSession.close();
		
		return historyTasks;
	}

	public static int deployMaker(String wflId, String filename, String filedesc) {
		SqlSession sqlSession = MybatisSessionHelper.eINSTANCE.openSession();
		DBActivitiMapper dbActivitiMapper = sqlSession.getMapper(DBActivitiMapper.class);
		Map deployedWfl = dbActivitiMapper.getDeployedWfl(wflId);
		int rev = -1;
		if(deployedWfl != null){
		String revstr = String.valueOf(deployedWfl.get("REV_"));
			if(revstr != null && !"".equals(revstr)){
				rev = Integer.parseInt(revstr);
			}
		}
		String maker = "system";
		if(rev ==  -1 ){
			rev = 0 ;
			System.out.println(String.format("%s %s %s %s %s", rev, wflId,filename, filedesc, maker));
			dbActivitiMapper.deployMakerInsert(String.valueOf(rev), wflId,filename, filedesc, maker );
		}else{
			rev++;
			dbActivitiMapper.deployMakerUpdate(String.valueOf(rev), wflId,filename, filedesc, maker);
		}
		 
		sqlSession.commit();
		sqlSession.close();
		return rev;
	}

	public static void saveImagePos(String processid, String imgposrelX, String imgposrelY) {
		SqlSession sqlSession = MybatisSessionHelper.eINSTANCE.openSession();
		DBActivitiMapper dbActivitiMapper = sqlSession.getMapper(DBActivitiMapper.class);
		dbActivitiMapper.saveImagePos(  processid,   imgposrelX,   imgposrelY);
		sqlSession.commit();
		sqlSession.close();
	}

	public static List<String> getImage(String processid) {
		SqlSession sqlSession = MybatisSessionHelper.eINSTANCE.openSession();
		DBActivitiMapper dbActivitiMapper = sqlSession.getMapper(DBActivitiMapper.class);
		Map<String, String> imgDtl = dbActivitiMapper.getDeployedWfl(processid);
		List<String> imgDtllst = new ArrayList<String>();
		imgDtllst.add(	imgDtl.get("FILE_NAME_"));
		imgDtllst.add(	imgDtl.get("REL_X_"));
		imgDtllst.add(	imgDtl.get("REL_Y_"));
		sqlSession.close();
		return imgDtllst;
	}

}
