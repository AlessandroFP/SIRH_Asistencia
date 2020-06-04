package pe.gob.sunat.utils.bd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import pe.gob.sunat.sol.dao.DAOAccesoBD;

/**
 * 
 * Clase       : OracleSequenceDAO 
 * Proyecto    : Asistencia 
 * Descripcion : 
 * Autor       : CGARRATT
 * Fecha       : 10-mar-2005 11:15:19
 */
public class OracleSequenceDAO extends DAOAccesoBD 
{
	/**
	 * Metodo encargado de obtener el valor de un sequence en ORACLE 
	 * @param ds
	 * @param sequence_id
	 * @return
	 * @throws SQLException
	 */
	public String getSequenceDS(DataSource ds, String sequence_id) 
		throws SQLException {
		
		Connection con = null;
		PreparedStatement pre = null;
		ResultSet rs = null;
		String sequence = "";

		try {

			StringBuffer sql = new StringBuffer();
			sql.append(" select ").append(sequence_id).append(".nextval secuencia from dual");

			con = getConnection(ds);
			pre = con.prepareStatement(sql.toString());
			rs = pre.executeQuery();

			if (rs.next()) sequence = rs.getString("secuencia");
			
			if (rs != null) rs.close();
			if (pre != null) pre.close();
			if (con != null) con.close();
			
		}
		catch (Exception e) {
			throw new SQLException("No se pudo obtener el secuenciador : " + sequence_id+".\n"+ e.toString());
		} finally {
			try { rs.close(); } catch (Exception e) {}
			try { pre.close(); } catch (Exception e) {}
			try { con.close(); } catch (Exception e) {}
		}
		return sequence;
	}
	
	/**
	 * Metodo encargado de obtener el valor de un sequence en ORACLE
	 * @param dbpool
	 * @param sequence_id
	 * @return
	 * @throws SQLException
	 */
	public String getSequence(String dbpool, String sequence_id) 
		throws SQLException {
		
		Connection con = null;
		PreparedStatement pre = null;
		ResultSet rs = null;
		String sequence = "";

		try {

			StringBuffer sql = new StringBuffer();
			sql.append(" select ").append(sequence_id).append(".nextval secuencia from dual");

			con = getConnection(dbpool);
			pre = con.prepareStatement(sql.toString());
			rs = pre.executeQuery();

			if (rs.next()) sequence = rs.getString("secuencia");
			
			if (rs != null) rs.close();
			if (pre != null) pre.close();
			if (con != null) con.close();
			
		}
		catch (Exception e) {
			throw new SQLException("No se pudo obtener el secuenciador : " + sequence_id+".\n"+ e.toString());
		} finally {
			try { rs.close(); } catch (Exception e) {}
			try { pre.close(); } catch (Exception e) {}
			try { con.close(); } catch (Exception e) {}
		}
		return sequence;
	}	
	
	/**
	 * Metodo encargado de obtener el valor de un sequence en ORACLE 
	 * @param con
	 * @param sequence_id
	 * @return
	 * @throws SQLException
	 */
	public String getSequenceCon(Connection con, String sequence_id) 
		throws SQLException {
		
		PreparedStatement pre = null;
		ResultSet rs = null;
		String sequence = "";

		try {

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT ").append(sequence_id).append(".nextval secuencia FROM dual");

			pre = con.prepareStatement(sql.toString());
			rs = pre.executeQuery();

			if (rs.next()) sequence = rs.getString("secuencia");
			
			if (rs != null) rs.close();
			if (pre != null) pre.close();
			
		}
		catch (Exception e) {
			throw new SQLException("No se pudo obtener el secuenciador : " + sequence_id+".\n"+ e.toString());
		} finally {
			try { rs.close(); } catch (Exception e) {}
			try { pre.close(); } catch (Exception e) {}
		}
		return sequence;
	}
	
}
