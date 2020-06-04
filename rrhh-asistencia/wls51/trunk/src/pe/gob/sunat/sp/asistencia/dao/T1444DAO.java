package pe.gob.sunat.sp.asistencia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import pe.gob.sunat.sol.dao.DAOAccesoBD;
import pe.gob.sunat.sp.asistencia.bean.BeanDevolucion;

/**
 * <p>
 * Title: T1444DAO
 * </p>
 * <p>
 * Description: Clase encargada de administrar las consultas a la tabla
 * T1444Devol
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

public class T1444DAO extends DAOAccesoBD {

	private static final Logger log = Logger.getLogger(T1444DAO.class);

	public T1444DAO() {

	}

	/**
	 * Metodo encargado de buscar los datos del registro de devolucion que
	 * cumpla con los criterios indicados por los parametros.
	 * @throws SQLException
	 */
	public BeanDevolucion findByPk(String dbpool, String codPers,
			String periodo, String mov) throws SQLException {
		
		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		BeanDevolucion det = new BeanDevolucion();

		try {
			strSQL.append("select cod_pers, periodo, mov, period_reg, observ, total"
					).append( " from t1444devol"
					).append( " where cod_pers = ? and periodo = ? and mov = ?");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, periodo);
			pre.setString(3, mov);
			rs = pre.executeQuery();

			det.setCodPers(codPers);
			det.setPeriodo(periodo);
			det.setMov(mov);

			while (rs.next()) {
				det.setObserv(rs.getString("observ").trim());
				det.setPeriodoReg(rs.getString("period_reg"));
				det.setTotal(rs.getInt("total"));
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
		return det;
	}

}