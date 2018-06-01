package sql.mybatis;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;


import java.util.List;

public abstract class BaseDAO {

    public List findForList(final String sqlMapId, final Object param) {
        SqlSession session = null;
        List result = null;
        try {
            SqlSessionFactory f = FactoryManager.getInstance();
            session = f.openSession();
            if (param == null) {
                result = session.selectList(sqlMapId);
            } else {
                result = session.selectList(sqlMapId, param);
            }
            // session.commit();
        } catch (Exception e) {
           throw new DatabaseException("SQL执行失败"+sqlMapId,e);
        } finally {
            if (session != null)
                session.close();
        }
        return result;
    }

    /**
     * 插入一个实体
     *
     * @param sqlMapId  mybatis ӳ��id
     * @param object  实体
     * @return
     */
    public int insert(final String sqlMapId, final Object object) {
        SqlSession session = null;
        try {
            SqlSessionFactory f = FactoryManager.getInstance();
            session = f.openSession();
            int result = session.insert(sqlMapId, object);
            session.commit();
            return result;
        } catch (Exception e) {
            throw new DatabaseException("SQL执行失败"+sqlMapId,e);
        } finally {
            if (session != null)
                session.close();
        }

    }

    /**
     * 查询一个实体
     *
     * @param sqlMapId  mybatis
     * @param param
     * @return
     */
    public Object findForObject(final String sqlMapId, final Object param){
        SqlSession session = null;
        try {
            SqlSessionFactory f = FactoryManager.getInstance();
            session = f.openSession();
            if (param != null) {
                return session.selectOne(sqlMapId, param);
            } else {
                return session.selectOne(sqlMapId);
            }
        } catch (Exception e) {
            throw new DatabaseException("SQL执行失败"+sqlMapId,e);
        } finally {
            if (session != null)
                session.close();
        }
    }

    /**
     * 更新一个实体
     * @param sqlMapId
     * @param param
     * @return
     */
    public int update(final String sqlMapId, final Object param){
        SqlSession session = null;
        try {
            SqlSessionFactory factory = FactoryManager.getInstance();
            session = factory.openSession();
            int result = session.update(sqlMapId, param);
            session.commit();
            return result;
        } catch (Exception e) {
            throw new DatabaseException("SQL执行失败"+sqlMapId,e);
        } finally {
            if (session != null)
                session.close();
        }
    }

    public int delete(final String sqlMapId, final Object param){
        SqlSession session = null;
        try {
            SqlSessionFactory f = FactoryManager.getInstance();
            session = f.openSession();
            int result = session.delete(sqlMapId, param);
            session.commit();
            return result;
        } catch (Exception e) {
            throw new DatabaseException("SQL执行失败"+sqlMapId,e);
        } finally {
            if (session != null)
                session.close();
        }
    }

    /**
     * TC infodba数据库 查询一个列表
     * @param sqlMapId
     * @param param
     * @return
     */
    public List findForListToTC(final String sqlMapId, final Object param) {
        SqlSession session = null;
        List result = null;
        try {
            SqlSessionFactory f = SqlSessionFactoryManager.getInstance();
            session = f.openSession();
            if (param == null) {
                result = session.selectList(sqlMapId);
            } else {
                result = session.selectList(sqlMapId, param);
            }
            // session.commit();
        } catch (Exception e) {
            throw new DatabaseException("SQL执行失败"+sqlMapId,e);
        } finally {
            if (session != null)
                session.close();
        }
        return result;
    }
}
