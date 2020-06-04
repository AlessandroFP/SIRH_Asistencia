package pe.gob.sunat.sa.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import pe.gob.sunat.utils.Utilidades;
import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.sol.*;
import pe.gob.sunat.sol.dao.DAOAccesoBD;

/**
 * @author WDELGADO
 *
 * DAO de la tabla T01param
 */
public class T01DAO extends DAOAbstract{
	
	private DataSource datasource;
	
	public T01DAO() {
	    super();
	}
	
	/**
	 * @param datasource Object
	*/
	public T01DAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.datasource = (DataSource)datasource;
	    else if (datasource instanceof String)
	    	this.datasource = getDataSource((String)datasource);
	    else
	    	throw new DAOException(this, "Datasource no valido");
	}
	
/**
 * Método findByClave
 * @param String dbpool
 * @param String num
 * @param String arg
 * @return HashMap hm
 * @throws IncompleteConversationalState
 */	
public HashMap findByClave(String dbpool, String num, String arg) throws IncompleteConversationalState {
    PreparedStatement pstmt = null;
    Connection conn = null;
    ResultSet rs = null;
    HashMap hm = new HashMap();
    try {
      String selectStatement = null;
      selectStatement = "SELECT t01_numero, t01_argumento, t01des_larga, t01des_corta FROM t01param WHERE t01_numero = ? AND t01_tipo = 'D' AND t01_argumento = ? ";
      
      // Conectar a la BD
      conn = getConnection(datasource);
      // Buscar en la BD
      pstmt = conn.prepareStatement(selectStatement);
      pstmt.setString(1, num);
      pstmt.setString(2, arg);
      rs = pstmt.executeQuery();
      if (rs.next()) {
        hm.put("t01_numero", rs.getString("t01_numero"));
        hm.put("t01_argumento", rs.getString("t01_argumento"));
        hm.put("t01des_larga", rs.getString("t01des_larga"));
        hm.put("t01des_corta", rs.getString("t01des_corta"));
      }
			else 
			{
        hm.put("t01_numero", "-");
        hm.put("t01_argumento", "-");
        hm.put("t01des_larga", "Sin descripción");
        hm.put("t01des_corta", "S/descrip.");
			}
			rs.close();
    }
    catch (Exception e) { throw new IncompleteConversationalState(e.getMessage()); }
		finally 
		{
			try {pstmt.close();}
			catch (Exception e) {}

			try	{conn.close();}
			catch (Exception e) {}
		}
		return hm;
	}

	/**
	 * Método findByParam
	 * @param String dbpool
	 * @param String num
	 * @param String tip
	 * @param String arg
	 * @return ArrayList aLRpta
	 * @throws IncompleteConversationalState
	 */
	//public ArrayList findByParam(String dbpool, String num, String tip, String arg)
	public ArrayList findByParam(String num, String tip, String arg)
	throws IncompleteConversationalState
	{
		Utilidades common = new Utilidades();
		String sWhere = "";
		boolean hasParam = false;
		String sOrder = "";
		String sSQL="";
		
		String pnumero="";
		String ptipo="";
		String pargumento="";

		// Build WHERE statement
		
		pnumero = num.trim() ;
		ptipo = tip.trim();
		pargumento = arg.trim();

		//-- Check número
		if (pnumero != null && ! pnumero.equals("")) 
		{
			if (! sWhere.equals("")) sWhere += " and ";
			hasParam = true;
			sWhere += "t01_numero = '" + common.replace(pnumero, "'", "''") + "'";
		}
		
		//-- Check tipo
		if (ptipo != null && ! ptipo.equals("")) 
		{

			if (! sWhere.equals("")) sWhere += " and ";
			hasParam = true;
			sWhere += "t01_tipo = '" + common.replace(ptipo, "'", "''") + "'";
		}

		//-- Check argumento
		if (pargumento != null && ! pargumento.equals("")) 
		{

			if (! sWhere.equals("")) sWhere += " and ";
			hasParam = true;
			sWhere += "t01_argumento = '" + common.replace(pargumento, "'", "''") + "'";
		}
		
		
		// Build full SQL statement
		if (hasParam) { sWhere = " WHERE (" + sWhere + ")"; }
		sOrder = " ";

		sSQL = "SELECT * from t01param ";
		sSQL = sSQL + sWhere + sOrder;

		ArrayList aLRpta = new ArrayList();

		try { 
			aLRpta = (ArrayList)executeQuery(datasource, sSQL.toString());
			if ( aLRpta.size() < 1) {
				java.util.Hashtable rsHash = new java.util.Hashtable();
				rsHash.put("t01des_corta", "0");
				aLRpta.add(rsHash);
			}			
		} catch (Exception e) {
			log.error("*** SQL Error ****",e);
			StringBuffer msg = new StringBuffer().append("Error en la consulta. T01DA0 - [findByParam] : ")
	 									 .append(e.toString());
			throw new DAOException(this, msg.toString());
		} finally {
    	}
		return aLRpta;
	}

	/**
	 * Método findByClaveDS
	 * @param String dbpool
	 * @param String num
	 * @param String arg
	 * @return HashMap hm
	 * @throws IncompleteConversationalState
	 */	
	public HashMap findByClaveDS(DataSource dbpool, String num, String arg) throws IncompleteConversationalState {
	    PreparedStatement pstmt = null;
	    Connection conn = null;
	    ResultSet rs = null;
	    HashMap hm = new HashMap();
	    try {
	      String selectStatement = null;
	      selectStatement = "SELECT t01_numero, t01_argumento, t01des_larga, t01des_corta FROM t01param WHERE t01_numero = ? AND t01_tipo = 'D' AND t01_argumento = ? ";
	      
	      // Conectar a la BD
	      conn = getConnection(dbpool);
	      // Buscar en la BD
	      pstmt = conn.prepareStatement(selectStatement);
	      pstmt.setString(1, num);
	      pstmt.setString(2, arg);
	      rs = pstmt.executeQuery();
	      if (rs.next()) {
	        hm.put("t01_numero", rs.getString("t01_numero"));
	        hm.put("t01_argumento", rs.getString("t01_argumento"));
	        hm.put("t01des_larga", rs.getString("t01des_larga"));
	        hm.put("t01des_corta", rs.getString("t01des_corta"));
	      }
				else 
				{
	        hm.put("t01_numero", "-");
	        hm.put("t01_argumento", "-");
	        hm.put("t01des_larga", "Sin descripción");
	        hm.put("t01des_corta", "S/descrip.");
				}
				rs.close();
	    }
	    catch (Exception e) { throw new IncompleteConversationalState(e.getMessage()); }
			finally 
			{
				try {pstmt.close();}
				catch (Exception e) {}

				try	{conn.close();}
				catch (Exception e) {}
			}
			return hm;
		}
	
}