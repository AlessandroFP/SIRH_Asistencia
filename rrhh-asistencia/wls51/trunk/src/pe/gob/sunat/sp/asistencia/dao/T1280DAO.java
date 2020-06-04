package pe.gob.sunat.sp.asistencia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import pe.gob.sunat.sol.dao.DAOAccesoBD;
import pe.gob.sunat.sp.asistencia.bean.BeanTipoReloj;
import pe.gob.sunat.utils.Constantes;

/**
 * <p>
 * Title: T1280DAO
 * </p>
 * <p>
 * Description: Clase encargada de administrar las consultas a la tabla
 * T1280Tipo_Reloj
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

public class T1280DAO extends DAOAccesoBD {

	private static final Logger log = Logger.getLogger(T1280DAO.class);

	public T1280DAO() {
	}

	/**
	 * Metodo encargado de listar los datos de los relojes cuyo estado
	 * corresponda al parametro recibido.
	 * @throws SQLException
	 */
	public ArrayList findByEstId(String dbpool, String estado)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList relojes = null;

		try {

			strSQL.append("select reloj, descrip, sede from t1280tipo_reloj "
					).append( "where est_id = ? order by 1 ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, estado);
			rs = pre.executeQuery();
			relojes = new ArrayList();
			BeanTipoReloj reloj = null;

			while (rs.next()) {

				reloj = new BeanTipoReloj();

				reloj.setReloj(rs.getString("reloj"));
				reloj.setDescrip(rs.getString("descrip").trim());
				reloj.setSede(rs.getString("sede"));

				relojes.add(reloj);
			}
 
		}

		catch (Exception e) {
			log.error("**** SQL ERROR ****", e);
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
		return relojes;
	}

	/**
	 * M�todo encargado de listar los datos de los relojes filtrados por un
	 * criterio con un valor determinado.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param criterio
	 *            String. Criterio de filtro de los registros.
	 * @param valor
	 *            String. Valor de filtro para el criterio dado.
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanTipoReloj.
	 * @throws SQLException
	 */
	public ArrayList findByCritVal(String dbpool, String criterio, String valor)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList relojes = null;

		try {
			strSQL.append("select reloj, descrip, sede from t1280tipo_reloj "
					).append( " where est_id = ? ");

			//busqueda por c�digo de reloj
			if (criterio.equals("0")) {
				strSQL.append(" and reloj like '" ).append( valor.trim().toUpperCase()
						).append( "%' ");
			}

			//busqueda por descripci�n
			if (criterio.equals("1")) {
				strSQL.append(" and upper(descrip) like '%"
						).append( valor.trim().toUpperCase() ).append( "%' ");
			}

			//busqueda por sede
			if (criterio.equals("2")) {
				strSQL.append(" and upper(sede) like '%"
						).append( valor.trim().toUpperCase() ).append( "%' ");
			}

			strSQL.append(" order by 1 ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, Constantes.ACTIVO);
			rs = pre.executeQuery();
			relojes = new ArrayList();
			BeanTipoReloj reloj = null;
			while (rs.next()) {
				reloj = new BeanTipoReloj();

				reloj.setReloj(rs.getString("reloj"));
				reloj.setDescrip(rs.getString("descrip").trim());
				reloj.setSede(rs.getString("sede"));

				relojes.add(reloj);
			}

 
		} catch (Exception e) {
			log.error("**** SQL ERROR ****", e);
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
		return relojes;
	}

}