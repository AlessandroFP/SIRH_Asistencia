package pe.gob.sunat.sp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import pe.gob.sunat.sol.dao.DAOAccesoBD;

/**
 * 
 * Clase : CorreoDAO Proyecto : Asistencia Descripcion : Fecha : 12/09/2005
 * 
 * @author CGARRATT
 */
public class CorreoDAO extends DAOAccesoBD {

	public CorreoDAO() {
	}

	/**
	 * Metodo encargado de obtener el correo del trabajador
	 * 
	 * @param dbpool
	 * @param codPers
	 * @return
	 * @throws SQLException
	 */
	public String findCorreoByCodPers(String dbpool, String codPers)
			throws SQLException {

		String strSQL = "";
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		String correo = "";

		try {

			strSQL = "select smtp from correos where cod_pers = ?";

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL);
			pre.setString(1, codPers);
			rs = pre.executeQuery();

			if (rs.next()) {
				correo = rs.getString("smtp") != null ? rs.getString("smtp")
						.trim() : "";
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
		return correo;
	}

	/**
	 * Metodo encargado de obtener el correo del trabajador
	 * 
	 * @param dbpool
	 * @param codPers
	 * @return
	 * @throws SQLException
	 */
	public String findCorreoByCodPersDS(DataSource dbpool, String codPers)
			throws SQLException {

		String strSQL = "";
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		String correo = "";

		try {

			strSQL = "select smtp from correos where cod_pers = ?";

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL);
			pre.setString(1, codPers);
			rs = pre.executeQuery();

			if (rs.next()) {
				correo = rs.getString("smtp") != null ? rs.getString("smtp")
						.trim() : "";
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
		return correo;
	}

}