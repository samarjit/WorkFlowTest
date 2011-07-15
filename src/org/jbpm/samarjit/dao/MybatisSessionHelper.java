package org.jbpm.samarjit.dao;

import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

/**
 * This is
 * Usage 
 * SqlSession session = <ThisClass>.openSession();<br/>
 * MyMapper specificMapper = session.getMapper(Clazz MyMapper.class); <br/>
 * specificMapper.crudMethods(...);<br/>
 * session.close();<br/>
 * 
 * @author Samarjit
 *
 */
public class MybatisSessionHelper {
	private static SqlSessionFactory sqlSessionFactory = null;
	
	 
	
	private MybatisSessionHelper() {
		String resource = "org/jbpm/samarjit/dao/configuration.xml";
		Reader reader = null;

		try {
			reader = Resources.getResourceAsReader(resource);
			sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader
//					 MybatisSessionHelper.class.getResourceAsStream("/org/jbpm/samarjit/dao/configuration.xml")
					);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static MybatisSessionHelper eINSTANCE = new MybatisSessionHelper();

	public void addMapper(Class<?> mapperClass){
		sqlSessionFactory.getConfiguration().addMapper(  mapperClass);
	}
	
	public SqlSession openSession(){
		return sqlSessionFactory.openSession();
	}
	
}
