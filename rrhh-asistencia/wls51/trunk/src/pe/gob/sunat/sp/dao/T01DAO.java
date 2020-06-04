package pe.gob.sunat.sp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.sql.DataSource;

import pe.gob.sunat.sol.BeanFechaHora;
import pe.gob.sunat.sol.IncompleteConversationalState;
import pe.gob.sunat.sol.dao.DAOAccesoBD;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utilidades;

/**
 * <p>
 * Description: Clase para administrar la t01param de sp
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: Sunat
 * </p>
 * 
 * @author cgarratt
 * @version 1.0
 */

public class T01DAO extends DAOAccesoBD {

	public T01DAO() {
	}

	/**
	 * Metodo encargado de verificar si una fecha es dia feriado.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param Fecha
	 *            String. fecha evaluada por la funcion.
	 * @return Valor booleano donde "verdadero" indica que el dia evaluado es
	 *         feriado.
	 * @throws SQLException
	 */
	public boolean findByFechaFeriado(String dbpool, String fecha)
			throws SQLException {

		String strSQL = "";
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		boolean feriado = false;

		BeanFechaHora bfh = new BeanFechaHora(fecha);
		String fecCompara = bfh.getFormatDate("yyyy/MM/dd");

		try {

			strSQL = " select t01_argumento from t01param "
					+ " where (DATE(t01_argumento) = DATE('" + fecCompara
					+ "')) " + " and t01_numero = ?" + " and t01_tipo = ? ";

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL);
			pre.setString(1, Constantes.T01CODTAB_FERIADO);
			pre.setString(2, Constantes.T01DETALLE);

			rs = pre.executeQuery();
			if (rs.next())
				feriado = true;

			if (rs != null) {
				rs.close();
			}

			if (pre != null) {
				pre.close();
			}

			if (con != null) {
				con.close();
			}
		} catch (Exception e) {
			throw new SQLException(e.toString());
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pre.close();
			} catch (Exception e) {
			}
			try {
				con.close();
			} catch (Exception e) {
			}
		}
		return feriado;
	}

	/**
	 * Metodo findByClaveDS
	 * 
	 * @param String
	 *            dbpool
	 * @param String
	 *            num
	 * @param String
	 *            arg
	 * @return HashMap hm
	 * @throws IncompleteConversationalState
	 */
	public HashMap findByClaveDS(DataSource dbpool, String num, String arg)
			throws IncompleteConversationalState {
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
			} else {
				hm.put("t01_numero", "-");
				hm.put("t01_argumento", "-");
				hm.put("t01des_larga", "Sin descripcion");
				hm.put("t01des_corta", "S/descrip.");
			}
			rs.close();
		} catch (Exception e) {
			throw new IncompleteConversationalState(e.getMessage());
		} finally {
			try {
				pstmt.close();
			} catch (Exception e) {
			}

			try {
				conn.close();
			} catch (Exception e) {
			}
		}
		return hm;
	}
	
	/**
	 * Metodo findByClaveDS
	 * 
	 * @param String
	 *            dbpool
	 * @param String
	 *            num
	 * @param String
	 *            arg
	 * @return HashMap hm
	 * @throws IncompleteConversationalState
	 */
	public HashMap findByClave(String dbpool, String num, String arg)
			throws IncompleteConversationalState {
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
			} else {
				hm.put("t01_numero", "-");
				hm.put("t01_argumento", "-");
				hm.put("t01des_larga", "Sin descripcion");
				hm.put("t01des_corta", "S/descrip.");
			}
			rs.close();
		} catch (Exception e) {
			throw new IncompleteConversationalState(e.getMessage());
		} finally {
			try {
				pstmt.close();
			} catch (Exception e) {
			}

			try {
				conn.close();
			} catch (Exception e) {
			}
		}
		return hm;
	}	

	/**
	 * 
	 * @param datos
	 * @return
	 * @throws SQLException
	 */
	public HashMap findByCodParam(HashMap datos)
			throws SQLException {

		Utilidades common = new Utilidades();
		String strSQL = "";
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		HashMap detalle = new HashMap();

		try {

			strSQL = "SELECT t01_numero, t01_argumento, t01des_larga, t01des_corta FROM t01param  "
					+ " WHERE t01_numero = ? AND t01_tipo = ? ";

			con = getConnection((String)datos.get("dbpool"));
			pre = con.prepareStatement(strSQL);
			pre.setString(1, (String)datos.get("codParam"));
			pre.setString(2, Constantes.T01DETALLE);
			rs = pre.executeQuery();

			while (rs.next()) {
				detalle.put(rs.getString("t01_argumento").trim(),
						rs.getString("t01des_larga")!=null?rs.getString("t01des_larga").trim():"");
			}

			if (rs != null) {
				rs.close();
			}
			if (pre != null) {
				pre.close();
			}
			if (con != null) {
				con.close();
			}
		}

		catch (Exception e) {
			throw new SQLException(e.toString());
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pre.close();
			} catch (Exception e) {
			}
			try {
				con.close();
			} catch (Exception e) {
			}
		}
		return detalle;
	}
	
	/**
	 * 
	 * @param datos
	 * @return
	 * @throws SQLException
	 */
	public ArrayList findDetalleByCodParam(HashMap datos)
			throws SQLException {

		Utilidades common = new Utilidades();
		String strSQL = "";
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList categorias = new ArrayList();

		try {

			strSQL = "select * from t01param "
					+ "where t01_numero = ? and t01_tipo = ? ";

			con = getConnection((String)datos.get("dbpool"));
			pre = con.prepareStatement(strSQL);
			pre.setString(1, (String)datos.get("codParam"));
			pre.setString(2, Constantes.T01DETALLE);			
			rs = pre.executeQuery();

			String[] aFields = common.getFieldsName(rs);

			while (rs.next()) {
				HashMap rsHash = new HashMap();
				common.getRecordToMap(rs, rsHash, aFields);
				categorias.add(rsHash);
			}

			if (rs != null) {
				rs.close();
			}
			if (pre != null) {
				pre.close();
			}
			if (con != null) {
				con.close();
			}
		}

		catch (Exception e) {
			throw new SQLException(e.toString());
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pre.close();
			} catch (Exception e) {
			}
			try {
				con.close();
			} catch (Exception e) {
			}
		}
		return categorias;
	}	

}