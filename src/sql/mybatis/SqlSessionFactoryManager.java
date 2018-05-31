package sql.mybatis;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

public class SqlSessionFactoryManager {
	static Reader reader = null;
	public static SqlSessionFactory factory = null;
	/** 配置文件所在地 */
	public static String resource = "mybatis-tc-config.xml";
	static {
		try {
			reader = Resources.getResourceAsReader(resource);
			SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
			factory = builder.build(reader);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static SqlSessionFactory getInstance() {
		if (factory == null) {
			synchronized (reader) {
				if (factory == null)
					try {
						reader = Resources.getResourceAsReader(resource);
						SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
						factory = builder.build(reader);
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}
		return factory;
	}
}
