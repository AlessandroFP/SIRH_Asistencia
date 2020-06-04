package pe.gob.sunat.sp.asistencia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import pe.gob.sunat.sol.dao.DAOAccesoBD;
import pe.gob.sunat.sp.asistencia.bean.BeanResumen;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;

/**
 * <p>
 * Title: T1272DAO
 * </p>
 * <p>
 * Description: Clase encargada de administrar las consultas a la tabla
 * T1272Asistencia_r
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

public class T1272DAO extends DAOAccesoBD {

	private static final Logger log = Logger.getLogger(T1272DAO.class);

	public T1272DAO() {
	}

	/**
	 * Metodo que se encarga de actualizar el estado de los registros del
	 * resumen de asistencia de un trabajador o unidad organizacional (segun
	 * indique el tipo parametro) para un periodo especifico.
	 * @throws SQLException
	 */
	public boolean updateByCodPersUOPeriodo(String dbpool, String codPers,
			String uo, String periodo, int tipo, HashMap seguridad)
			throws SQLException {

		StringBuffer strUpd = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		boolean result = false;

		try {

			strUpd.append("update t1272asistencia_r set est_id = ? where periodo = ? ");

			//por persona
			if (tipo == 0) {
				strUpd.append(" and cod_pers = '" + codPers ).append( "'");
			}

			//por uo
			if (tipo == 1) {
				strUpd.append(" and u_organ = '" ).append( uo ).append( "'");
			}

			//criterios de visibilidad
			if (seguridad != null) {

				String codigo = (String) seguridad.get("codPers");

				strUpd.append(" and u_organ in ");
				strUpd.append(" (select u_organ from t1485seg_uorga "
						).append( " where cod_pers = '" ).append( codigo
						).append( "' and operacion = '" ).append( Constantes.PROCESO_CIERRE
						).append( "') ");
			}

			con = getConnection(dbpool);
			pre = con.prepareStatement(strUpd.toString());
			pre.setString(1, Constantes.ACTIVO);
			pre.setString(2, periodo);
			int res = pre.executeUpdate();

			result = (res > 0);

 

		}

		catch (Exception e) {
			log.error("**** SQL ERROR ****", e);
			throw new SQLException(e.toString());
		} finally {
			try {
				pre.close();
			} catch (Exception e) {

			}
			try {
				con.close();
			} catch (Exception e) {

			}
		}

		return result;
	}

	/**
	 * Metodo que se encarga de eliminar los registros del resumen de asistencia
	 * de un trabajador para un periodo determinado.
	 * @throws SQLException
	 */
	public void deleteByCodPersPeriodo(String dbpool, String codPers,
			String periodo) throws SQLException {

		StringBuffer strUpd = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;

		try {

			strUpd.append("delete from t1272asistencia_r where cod_pers = ? and periodo = ? ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strUpd.toString());
			pre.setString(1, codPers);
			pre.setString(2, periodo);
			pre.executeUpdate();
 

		} catch (Exception e) {
			log.error("**** SQL ERROR ****", e);
			throw new SQLException(e.toString());
		} finally {
			try {
				pre.close();
			} catch (Exception e) {

			}
			try {
				con.close();
			} catch (Exception e) {

			}
		}

	}

	/**
	 * Metodo que se encarga de insertar un registro de resumen de asistencia.
	 * @throws SQLException
	 */
	public void insertByCodPersPeriodoMov(String dbpool, String codPers,
			String periodo, String uo, String mov, float total, String usuario)
			throws SQLException {

		StringBuffer strUpd = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;

		try {

			strUpd.append("insert into t1272asistencia_r(cod_pers,periodo,u_organ,mov,total,est_id,fcreacion,cuser_crea) "
					).append( "values(?,?,?,?,?,?,?,?)");
			
			con = getConnection(dbpool);
			pre = con.prepareStatement(strUpd.toString());
			pre.setString(1, codPers);
			pre.setString(2, periodo);
			pre.setString(3, uo);
			pre.setString(4, mov);
			pre.setFloat(5, total);
			pre.setString(6, Constantes.ACTIVO);
			pre.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
			pre.setString(8, usuario);
			pre.executeUpdate();
 

		}

		catch (Exception e) {
			log.error("**** SQL ERROR ****", e);
			throw new SQLException(e.toString());
		} finally {
			try {
				pre.close();
			} catch (Exception e) {

			}
			try {
				con.close();
			} catch (Exception e) {

			}
		}
	}

	/**
	 * Metodo que se encarga de buscar los registros del resumen de asistencia
	 * de un periodo filtrados por un criterio con un valor determinado.
	 * @throws SQLException
	 */
	public ArrayList joinWithT1279T02T12ByPeriodoCriterioValor(String dbpool,
			String periodo, String criterio, String valor) throws SQLException {
		
		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList detalle = null;

		try {
			strSQL.append("select a.cod_pers, p.t02nombres, p.t02ap_pate,"
					).append( "       p.t02ap_mate, uo.t12des_corta, a.periodo,"
					).append( "       a.mov, t.descrip, t.medida, a.total"
					).append( " from t1272asistencia_r a," ).append( "      t1279tipo_mov t,"
					).append( "      t02perdp p," ).append( "      t12uorga uo"
					).append( " where t.califica = ? and a.periodo = ? ");

			if (!valor.trim().equals("")) {
				if (criterio.trim().equals("0")) {
					strSQL.append(" and a.cod_pers like '").append( valor.trim().toUpperCase() ).append( "%'");
				}
				if (criterio.trim().equals("1")) {
					strSQL.append(" and a.u_organ like '").append( valor.trim().toUpperCase() ).append( "%'");
				}
			}

			//Joins
			strSQL.append(" and t.mov = a.mov and ").append( "     p.t02cod_pers = a.cod_pers and").append( "     uo.t12cod_uorga = a.u_organ ");
			strSQL.append(" order by p.t02ap_pate, p.t02ap_mate ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, "S");
			pre.setString(2, periodo);
			rs = pre.executeQuery();
			detalle = new ArrayList();
			BeanResumen det = null;

			while (rs.next()) {

				det = new BeanResumen();

				det.setCodPers(rs.getString("cod_pers"));
				det.setPeriodo(rs.getString("periodo"));
				det.setMov(rs.getString("mov"));
				det.setTrabajador(rs.getString("t02ap_pate").trim().concat( " "
						).concat( rs.getString("t02ap_mate").trim() ).concat( ", "
						).concat( rs.getString("t02nombres").trim()));
				det.setUndOrg(rs.getString("t12des_corta"));
				det.setDescMovimiento(rs.getString("descrip").trim());
				det.setTotal(rs.getInt("total"));
				det.setMedida(rs.getString("medida"));

				detalle.add(det);
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
		return detalle;
	}

	/**
	 * Metodo que devuelve los datos de un registro de resumen de asistencia
	 * especifico.
	 * @throws SQLException
	 */
	public BeanResumen findByPk(String dbpool, String codPers,
			String periodo, String mov) throws SQLException {
		
		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		BeanResumen det = null;

		try {
			strSQL.append("select cod_pers, periodo, mov, u_organ, total").append( " from t1272asistencia_r" ).append( " where cod_pers = ? and "
					).append( "       periodo = ? and " ).append( "       mov = ?");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, periodo);
			pre.setString(3, mov);
			rs = pre.executeQuery();

			while (rs.next()) {

				det = new BeanResumen();

				det.setCodPers(rs.getString("cod_pers"));
				det.setPeriodo(rs.getString("periodo"));
				det.setMov(rs.getString("mov"));
				det.setUndOrg(rs.getString("u_organ"));
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

	/**
	 * Metodo que se encarga de devolver el numero de dias laborados por un
	 * trabajador en un rango de fechas determinado.
	 * @throws SQLException
	 */
	public int findDiasLaborablesByFiniFin(String dbpool, String codPers,
			String fechaIni, String fechaFin) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		int dias = 0;

		try {

			String fIni = Utiles.toYYYYMMDD(fechaIni);
			String fFin = Utiles.toYYYYMMDD(fechaFin);

			strSQL.append("select 	count(distinct a.fecha) dias "
					).append( "from    t1454asistencia_d a, "
					//).append( "        t1276periodo p, "
					).append( "        t1279tipo_mov m "
					//).append( "where   a.cod_pers = ? and "
							//).append( "        DATE(p.finicio[7,10]||''/''||p.finicio[4,5]||''/''||p.finicio[1,2]) <= DATE('"
							//).append( fIni
							//).append( "') and "
							//).append( "        DATE(p.ffin[7,10]||''/''||p.ffin[4,5]||''/''||p.ffin[1,2]) >= DATE('"
							//).append( fFin
							//).append( "') and "
					
					).append( "where   a.cod_pers = ? and "
					).append( "        a.fecha >= '"
					).append( fIni
					).append( "' and a.fecha <= '"
					).append( fFin
					).append( "' and "
					).append( "        (not (a.mov = ? or "
					).append( "         (m.tipo_id = ? and m.califica = ?))) and "
					//).append( "         (m.tipo_id = ? and m.mov = ? and m.califica = ?))) and "
					//).append( "        a.periodo = p.periodo and "
					).append( "        a.mov = m.mov ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, Constantes.MOV_INASISTENCIA);
			pre.setString(3, Constantes.TIPO_MOV_LICENCIA);
			//pre.setString(4, Constantes.LICENCIA_ENFERMEDAD);
			pre.setString(4, Constantes.DESCUENTO);

			rs = pre.executeQuery();

			if (rs.next()) {
				dias = rs.getInt("dias");
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
		return dias;
	}

	/**
	 * Metodo que se encarga de devolver la cantidad de dÃ­as que un trabajador
	 * ha estado de licencia por enfermedad
	 * @throws SQLException
	 */
	public int findDiasEnfermedadByFiniFin(String dbpool, String codPers,
			String fechaIni, String fechaFin) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		int dias = 0;

		try {

		
			String fIni = Utiles.toYYYYMMDD(fechaIni);
			String fFin = Utiles.toYYYYMMDD(fechaFin);

			strSQL.append("select 	count(distinct a.fecha) dias "
					).append( "from    t1454asistencia_d a, "
					//).append( "        t1276periodo p, "
					).append( "        t1279tipo_mov m "
					//).append( "where   a.cod_pers = ? and "
							//).append( "        DATE(p.finicio[7,10]||''/''||p.finicio[4,5]||''/''||p.finicio[1,2]) <= DATE('"
							//).append( fIni
							//).append( "') and "
							//).append( "        DATE(p.ffin[7,10]||''/''||p.ffin[4,5]||''/''||p.ffin[1,2]) >= DATE('"
							//).append( fFin ).append( "') and "
							
					).append( "where   a.cod_pers = ? and "
					).append( "        a.fecha >= '"
					).append( fIni
					).append( "' and a.fecha <= '"
					).append( fFin 
					).append( "' and "
					).append( "        m.tipo_id = ? and m.mov = ? and "
					//).append( "        a.periodo = p.periodo and "
					).append( "        a.mov = m.mov ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, Constantes.TIPO_MOV_LICENCIA);
			pre.setString(3, Constantes.LICENCIA_ENFERMEDAD);
			
			rs = pre.executeQuery();

			if (rs.next()) {
				dias = rs.getInt("dias");
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
		return dias;
	}

}