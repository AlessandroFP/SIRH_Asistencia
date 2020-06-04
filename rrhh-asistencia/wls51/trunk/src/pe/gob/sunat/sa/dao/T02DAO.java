package pe.gob.sunat.sa.dao;

import java.util.HashMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.sol.*;
import pe.gob.sunat.sol.dao.DAOAccesoBD;


/**
 * @author WDELGADO
 *
 * DAO de la Tabla T02cbien
 */
public class T02DAO extends DAOAbstract {
private DataSource datasource;
	
	public T02DAO() {
	    super();
	}
	
	/**
	 * @param datasource Object
	*/
	public T02DAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.datasource = (DataSource)datasource;
	    else if (datasource instanceof String)
	    	this.datasource = getDataSource((String)datasource);
	    else
	    	throw new DAOException(this, "Datasource no valido");
	}

  	/**
  	 * Método findByt02cod_bien
  	 * @param String dbpool_sa
  	 * @param String t02cod_bien
  	 * @return HashMap hMap
  	 * @throws SQLException
  	 */
	public HashMap findByt02cod_bien(String dbpool_sa, String t02cod_bien) throws
		SQLException 
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		String selectStatement = null;
		HashMap hMap = new HashMap(20);

		selectStatement = "SELECT * FROM t02cbien WHERE t02cod_bien = ? ";

		try 
		{
			//conn = getConnection(dbpool_sa);
			conn = getConnection(datasource);
			conn.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			pstmt = conn.prepareStatement(selectStatement);
			pstmt.setString(1, t02cod_bien);
			pstmt.setMaxRows(1);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next())
			{
				hMap.put("t02cod_bien", rs.getString("t02cod_bien"));
				hMap.put("t02des_bien", rs.getString("t02des_bien"));
				hMap.put("t02ind_vigenc", rs.getString("t02ind_vigenc"));
				hMap.put("t02presentaci", rs.getString("t02presentaci"));			
				hMap.put("t02med_compra", rs.getString("t02med_compra"));			
				hMap.put("t02med_transf", rs.getString("t02med_transf"));			
				hMap.put("t02f_ulticomp", rs.getString("t02f_ulticomp"));			
				hMap.put("t02f_ultiaten", rs.getString("t02f_ultiaten"));			
				hMap.put("t02f_ulticomp", rs.getString("t02f_ulticomp"));	
				hMap.put("t02c_stock01", rs.getString("t02c_stock01"));	
				hMap.put("t02c_stock02", rs.getString("t02c_stock02"));	
				hMap.put("t02i_cosprom1", rs.getString("t02i_cosprom1"));	
				hMap.put("t02i_cosprom2", rs.getString("t02i_cosprom2"));	
				hMap.put("t02cod_partid", rs.getString("t02cod_partid"));	
				hMap.put("t02anho", rs.getString("t02anho"));	
				hMap.put("t02cod_ctaalm", rs.getString("t02cod_ctaalm"));	
				hMap.put("t02cod_ctatra", rs.getString("t02cod_ctatra"));	
				hMap.put("t02cod_ctagas", rs.getString("t02cod_ctagas"));	
				hMap.put("t02cod_ctapas", rs.getString("t02cod_ctapas"));	
				hMap.put("t02cod_ctaimp", rs.getString("t02cod_ctaimp"));	
			} 
			else 
			{
				hMap = null;
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