package pe.gob.sunat.sa.dao; 

import java.util.HashMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.sol.*;
import pe.gob.sunat.sol.dao.DAOAccesoBD;


/**
 * @author WDELGADO
 *
 * DAO de la tabla t33gcpat 
 * 
 */
public class T33DAO extends DAOAbstract {
	
	private DataSource datasource;
	
	public T33DAO() {
	    super();
	}
	
	/**
	 * @param datasource Object
	*/
	public T33DAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.datasource = (DataSource)datasource;
	    else if (datasource instanceof String)
	    	this.datasource = getDataSource((String)datasource);
	    else
	    	throw new DAOException(this, "Datasource no valido");
	}

  	/**
  	 * Método findByCodPatrim 
  	 * @param String dbpool_sa
  	 * @param String t33cod_patrim
  	 * @return HashMap hMap 
  	 * @throws IncompleteConversationalState
  	 */
	public HashMap findByCodPatrim(String dbpool_sa, String t33cod_patrim) throws
		IncompleteConversationalState 
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		String selectStatement = null;
		HashMap hMap = new HashMap(3);

		selectStatement = "SELECT * FROM t33gcpat WHERE t33cod_patrim = ? ";

		try 
		{
			//conn = getConnection(dbpool_sa);
			conn = getConnection(datasource);
			conn.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			pstmt = conn.prepareStatement(selectStatement);
			pstmt.setString(1, t33cod_patrim);
			pstmt.setMaxRows(1);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next())
			{
				hMap.put("t33cod_patrim", rs.getString("t33cod_patrim"));
				hMap.put("t33des_grupo", rs.getString("t33des_grupo"));
				hMap.put("t33des_clase", rs.getString("t33des_clase"));
				hMap.put("t33des_clasin", rs.getString("t33des_clasin"));
			} 
			else 
			{
				hMap.put("t33cod_patrim", "-");
				hMap.put("t33des_grupo", "S/descripción");
				hMap.put("t33des_clase", "S/descripción");
				hMap.put("t33des_clasin", "S/descripción");
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
		return hMap;
	}

 }