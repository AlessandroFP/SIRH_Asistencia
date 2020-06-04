package pe.gob.sunat.rrhh.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeMap;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.bean.ParamBean;
import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.cache.interfaz.Param;

/**
 *  
 * Clase que accede a la tabla t01param, basada en la estructura definida en
 * la base de datos SP.
 * 
 * @author JVALDEZ
 * @since dao-1.0
 */
public class T01DAO extends DAOAbstract implements Param{
  
  /**
   * 
   * @param v String[]
   * @param pool String
   *            
   * @return TreeMap
   */
  public TreeMap mbuscar(String[] v, String pool) {
    return mbuscar(v, getDataSource(pool));
  }
  
  /**
   * 
   * @param v String[]
   * @param ds DataSource
   * @return TreeMap
   */
  public TreeMap mbuscar(String[] v, DataSource ds) {
    TreeMap r = new TreeMap();
    Connection con = null;
    try {
      con = getConnection(ds);
      ResultSet rs = null;
      StringBuffer sb_cond = new StringBuffer("('");
      for (int i = 0; i < v.length; i++) {
        sb_cond.append(v[i].trim()).append("'");
        if ((i + 1) < v.length) {
          sb_cond.append(",'");
        }
      }
      sb_cond.append(")");
      sb_cond
      .insert(0,
      "SELECT t01_argumento, t01des_larga, t01des_corta FROM t01param WHERE t01_numero IN ");
      sb_cond.append(" AND t01_tipo = 'D' ORDER BY 1");
      
      PreparedStatement pstmt = con.prepareStatement(sb_cond.toString());
      rs = pstmt.executeQuery();
      while (rs.next()) {
        ParamBean param = new ParamBean(rs.getString(1).trim(), 
            rs.getString(2).trim(),
            rs.getString(3)==null?"":rs.getString(3).trim(), "");
        r.put(param.getCodigo().trim(), param);
      }
      log.debug("Elementos cargados..:" + r.size());
      rs.close();
    } catch (SQLException e) {
      throw new DAOException(this, e.getMessage()
          + e.getLocalizedMessage() + e.getErrorCode());
    } catch (Exception e) {
      throw new DAOException(this, e.getMessage()
          + e.getLocalizedMessage());
    } finally {
      close(con);
    }
    return r;
  }
  
  /**
   * 
   * @param qry String
   * @param pool String
   * @return TreeMap
   */
  public TreeMap mbuscar(String qry, String pool) {
    return mbuscar(qry, getDataSource(pool));
  }
  
  /**
   * 
   * @param qry String
   * @param ds DataSource
   * @return TreeMap
   */
  public TreeMap mbuscar(String qry, DataSource ds) {
    TreeMap r = new TreeMap();
    Connection con = null;
    try {
      con = getConnection(ds);
      PreparedStatement pstmt = con.prepareStatement(qry);
      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        ParamBean param = new ParamBean(rs.getString(1).trim(), rs.getString(2), rs.getString(3), "");
        r.put(param.getCodigo().trim(), param);
      }
      log.debug("Elementos cargados..:" + r.size());
      rs.close();
    } catch (SQLException e) {
      throw new DAOException(this, e);
    } catch (Exception e) {
      throw new DAOException(this, e);
    } finally {
      close(con);
    }
    return r;
  }
  
  /**
   * 
   * @param v String[]
   * @param pool String
   * @param valor String
   * @return ParamBean
   */
  public ParamBean buscar(String[] v, String pool, String valor) {
    return buscar(v, getDataSource(pool), valor);
  }
  
  /**
   * 
   * @param v String[]
   * @param ds DataSource
   * @param valor String
   * @return ParamBean
   */
  public ParamBean buscar(String[] v, DataSource ds, String valor) {
    ParamBean r = new ParamBean();
    Connection con = null;
    try {
      con = getConnection(ds);
      ResultSet rs = null;
      StringBuffer sb_cond = new StringBuffer("('");
      for (int i = 0; i < v.length; i++) {
        sb_cond.append(v[i].trim()).append("'");
        if ((i + 1) < v.length) {
          sb_cond.append(",'");
        }
      }
      sb_cond.append(")");
      
      sb_cond
      .insert(0,
      "SELECT t01_argumento, t01des_larga, t01des_corta FROM t01param WHERE t01_numero IN ");
      sb_cond.append(" AND t01_tipo = 'D' ORDER BY 1");
      PreparedStatement pstmt = con.prepareStatement(sb_cond.toString());
      pstmt.setString(1, valor);
      rs = pstmt.executeQuery();
      if (rs.next()) {
        r = new ParamBean(rs.getString(1).trim(), 
            rs.getString(2).trim(),
            rs.getString(3)==null?"":rs.getString(3).trim(), "" );				
      }
      rs.close();
    } catch (SQLException e) {
      throw new DAOException(this, e);
    } catch (Exception e) {
      throw new DAOException(this, e);
    } finally {
      close(con);
    }
    return r;
  }
  
}
