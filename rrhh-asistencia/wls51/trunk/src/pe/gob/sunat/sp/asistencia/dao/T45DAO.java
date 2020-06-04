package pe.gob.sunat.sp.asistencia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import pe.gob.sunat.sol.dao.DAOAccesoBD;
import pe.gob.sunat.sp.asistencia.bean.BeanTurno;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;

/**
 * <p>
 * Title: T45DAO
 * </p>
 * <p>
 * Description: Clase encargada de administrar las consultas a la tabla T45Turno
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

public class T45DAO extends DAOAccesoBD {

	private static final Logger log = Logger.getLogger(T45DAO.class);

	public T45DAO() {
	}

	/**
	 * Metodo encargado de listar los datos de los turnos segun el estado
	 * deseado.
	 * @throws SQLException
	 */
	public ArrayList findByEstId(String dbpool, String estado)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList turnos = null;

		try {

			strSQL.append("select cod_turno, des_turno, f_inicio, h_inicio, f_ffin, h_fin,"
					).append( "       est_id, dias_int, tolera_turno, hlimit, oper_id, h_inirefr,"
					).append( "       h_finref, min_refr, scontrol_id "
					).append( " from  t45turno" ).append( " where est_id = ? order by 1 ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, estado);
			rs = pre.executeQuery();
			turnos = new ArrayList();
			BeanTurno turno = null;

			while (rs.next()) {

				turno = new BeanTurno();

				turno.setCodTurno(rs.getString("cod_turno"));
				turno.setDesTurno(rs.getString("des_turno").trim());
				turno.setFechaIni(rs.getString("f_inicio"));
				turno.setHoraIni(rs.getString("h_inicio"));
				turno.setFechaFin(rs.getString("f_ffin"));
				turno.setHoraFin(rs.getString("h_fin"));
				turno.setEstId(rs.getString("est_id"));
				turno.setDiasInt(rs.getString("dias_int"));
				turno.setTolera(rs.getString("tolera_turno"));
				turno.setHoraLimite(rs.getString("hlimit"));
				turno.setOperId(rs.getString("oper_id"));
				turno.setControlId(rs.getString("scontrol_id"));
				turno.setRefrIni(rs.getString("h_inirefr"));
				turno.setRefrFin(rs.getString("h_finref"));
				turno.setRefrMin(rs.getString("min_refr"));

				turnos.add(turno);
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
		return turnos;
	}

	/**
	 * M�todo encargado de buscar los turnos filtrados por un criterio con un
	 * valor determinado.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param criterio
	 *            String. Criterio de filtro de los registros.
	 * @param valor
	 *            String. Valor de filtro para el criterio dado.
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanTurno.
	 * @throws SQLException
	 */
	public ArrayList findByCritVal(String dbpool, String criterio, String valor)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList turnos = null;
		String fecCompara = "";

		try {
			strSQL.append("select cod_turno, des_turno, f_inicio, h_inicio, f_ffin, h_fin,"
					).append( "       est_id, dias_int, tolera_turno, hlimit, oper_id, h_inirefr,"
					).append( "       h_finref, min_refr, scontrol_id  "
					).append( " from  t45turno" ).append( " where est_id = ? ");

			boolean agrega = false;

			//busqueda por c�digo de reloj
			if (criterio.equals("0")) {
				strSQL.append("  and cod_turno like '" ).append( valor.trim().toUpperCase()
						).append( "%' ");
				agrega = true;
			}

			if (criterio.equals("2")) {
				strSQL.append("  and upper(des_turno) like '%"
						).append( valor.trim().toUpperCase() ).append( "%' ");
				agrega = true;
			}

			if (criterio.equals("3")) {
				strSQL.append("  and oper_id = '" ).append( valor.trim() ).append( "' ");
				agrega = true;
			}

			if (criterio.equals("4") || criterio.equals("5")) {
				fecCompara = Utiles.toYYYYMMDD(valor);
			}

			//Búsqueda exacta por código
			if (criterio.equals("6")) {
				strSQL.append("  and upper(cod_turno) = '"
						).append( valor.trim().toUpperCase() ).append( "'");
				agrega = true;
			}

			strSQL.append("  order by 1");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, Constantes.ACTIVO);
			rs = pre.executeQuery();
			turnos = new ArrayList();

			String fechaInicio = "";
			//String fechaIni = "";
			String fechaFinal = "";
			BeanTurno turno = null;

			while (rs.next()) {
				agrega = true;
				if (criterio.equals("4")) {
					fechaInicio = Utiles.toYYYYMMDD(rs.getString("f_inicio"));

					agrega = (fecCompara.equals("") || (fecCompara
							.compareTo(fechaInicio) <= 0));
				}

				if (criterio.equals("5")) {
					fechaFinal = Utiles.toYYYYMMDD(rs.getString("f_ffin"));

					agrega = (fecCompara.equals("") || (fecCompara
							.compareTo(fechaFinal) >= 0));
				}

				if (agrega) {
					turno = new BeanTurno();

					turno.setCodTurno(rs.getString("cod_turno"));
					turno.setDesTurno(rs.getString("des_turno").trim());
					turno.setFechaIni(rs.getString("f_inicio"));
					turno.setHoraIni(rs.getString("h_inicio"));
					turno.setFechaFin(rs.getString("f_ffin"));
					turno.setHoraFin(rs.getString("h_fin"));
					turno.setEstId(rs.getString("est_id"));
					turno.setDiasInt(rs.getString("dias_int"));
					turno.setTolera(rs.getString("tolera_turno"));
					turno.setHoraLimite(rs.getString("hlimit"));
					turno.setOperId(rs.getString("oper_id"));
					turno.setRefrIni(rs.getString("h_inirefr"));
					turno.setRefrFin(rs.getString("h_finref"));
					turno.setRefrMin(rs.getString("min_refr"));
					turno.setControlId(rs.getString("scontrol_id"));

					turnos.add(turno);
				}
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
		return turnos;
	}

}