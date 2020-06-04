package pe.gob.sunat.sp.telefono.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import pe.gob.sunat.sol.IncompleteConversationalState;
import pe.gob.sunat.sol.dao.DAOAccesoBD;

/**
 * 
 * 
 * <p>
 * Title: DAO de Telefono
 * </p>
 * <p>
 * Description: Maneja las consultas de la aplicacion de mantenimiento de
 * directorio telefonico
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: Sunat
 * </p>
 * 
 * @author CGARRATT
 * @version 1.0
 */
public class TelefonoDAO extends DAOAccesoBD {

	public TelefonoDAO() {
	}

	/**
	 * Metodo encargado de obtener los roles
	 * 
	 * @param mapa
	 * @return
	 */
	public ArrayList findTelefonos(HashMap datos) {

		Connection con = null;
		ArrayList lista = new ArrayList();
		String strSQL = "";
		try {

			con = getConnection((String) datos.get("dbpool"));

			strSQL = "select t.*, c.t99descrip as tipo from	telefonos t, t99codigos c "
					+ "where t.cod_pers = ? "
					+ " and c.t99cod_tab = ? "
					+ " and c.t99tip_desc = ? "
					+ " and t.tip_linea = c.t99codigo " + "order by tip_linea";

			PreparedStatement pre = con.prepareStatement(strSQL);
			pre.setString(1, (String) datos.get("cod_pers"));
			pre.setString(2, "901");
			pre.setString(3, "D");
			ResultSet rs = pre.executeQuery();

			while (rs.next()) {
				HashMap telefono = new HashMap();
				telefono.put("cod_pers", rs.getString("cod_pers"));
				telefono.put("tip_linea", rs.getString("tip_linea"));
				telefono.put("numero", rs.getString("numero"));
				telefono.put("cod_uorgan", rs.getString("cod_uorgan"));
				telefono.put("cod_local", rs.getString("cod_local"));
				telefono.put("piso", rs.getString("piso"));
				telefono.put("tipo", rs.getString("tipo"));
				lista.add(telefono);
			}
			pre.close();
			rs.close();

		} catch (SQLException e) {
			throw new IncompleteConversationalState(e.getErrorCode() + ":"
					+ e.getMessage() + ":" + e.getLocalizedMessage());
		} catch (Exception e) {
			throw new IncompleteConversationalState(e.getMessage() + ":"
					+ e.getLocalizedMessage());
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return lista;
	}
	
	/**
	 * Metodo encargado de obtener los roles
	 * 
	 * @param mapa
	 * @return
	 */
	public ArrayList findTelefonosByTipo(String dbpool, String cod_pers, String tipo) {

		Connection con = null;
		ArrayList lista = null;
		String strSQL = "";
		try {

			con = getConnection(dbpool);

			strSQL = "select numero from telefonos "
					+ "where cod_pers = '"+cod_pers+"' "
					+ " 	 and tip_linea = '"+tipo+"'"; 					

			PreparedStatement pre = con.prepareStatement(strSQL);
			ResultSet rs = pre.executeQuery();
			
			String telefono = "";
			lista = new ArrayList();
			while (rs.next()) {
				telefono = rs.getString("numero");
				lista.add(telefono);
			}
			pre.close();
			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new IncompleteConversationalState(e.getErrorCode() + ":"
					+ e.getMessage() + ":" + e.getLocalizedMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new IncompleteConversationalState(e.getMessage() + ":"
					+ e.getLocalizedMessage());
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return lista;
	}	

	public ArrayList findTipos(HashMap datos)
			throws IncompleteConversationalState {

		Connection con = null;
		PreparedStatement pstmt = null;
		String selectStatement = null;
		ArrayList lista = new ArrayList();
		selectStatement = " SELECT * FROM t99codigos WHERE t99cod_tab = ? and t99tip_desc = ? ";
		try {

			con = getConnection((String) datos.get("dbpool"));

			pstmt = con.prepareStatement(selectStatement);
			pstmt.setString(1, (String) datos.get("cod_tab"));
			pstmt.setString(2, "D");

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {

				HashMap hMap = new HashMap();

				hMap.put("valor", rs.getString("t99codigo").trim());
				hMap.put("abrev", rs.getString("t99descrip").trim());

				lista.add(hMap);
			}
			pstmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IncompleteConversationalState(e.getErrorCode() + ":"
					+ e.getMessage() + ":" + e.getLocalizedMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new IncompleteConversationalState(e.getMessage() + ":"
					+ e.getLocalizedMessage());
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return lista;
	}
}