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
import pe.gob.sunat.sp.dao.T02DAO;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;

/**
 * <p>
 * Title: T1456DAO
 * </p>
 * <p>
 * Description: Clase encargada de administrar las consultas a la tabla
 * T1456Vacacion_Gen
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

public class T1456DAO extends DAOAccesoBD {

	private static final Logger log = Logger.getLogger(T1456DAO.class);

	public T1456DAO() {
	}

	/**
	 * Metodo encargado de buscar el registro activo de la fecha de reingreso o
	 * ingreso de un trabajador especifico. Join con la tabla T02Perdp.
	 * @throws SQLException
	 */
	public HashMap joinWithT02ByCodPersEstId(String dbpool, String codPers)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		HashMap registro = new HashMap();

		try {
			T02DAO dao = new T02DAO();

			strSQL.append("select 	v.cod_pers, v.fecha, p.t02nombres, p.t02ap_pate, p.t02ap_mate "
					).append( " from t1456vacacion_gen v, "
					).append( "      t02perdp p "
					).append( " where  v.est_id = ? and"
					).append( "        v.cod_pers = ? and p.t02cod_pers = v.cod_pers ");
			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, Constantes.ACTIVO);
			pre.setString(2, codPers.trim().toUpperCase());
			rs = pre.executeQuery();
			if (rs.next()) {
				registro.put("cod_pers", rs.getString("cod_pers"));
				registro.put("fecha", Utiles.timeToFecha(rs
						.getTimestamp("fecha")));
				registro.put("trabajador", rs.getString("t02ap_pate").trim()
						.concat(" " ).concat( rs.getString("t02ap_mate").trim() ).concat( ", ").concat( rs.getString("t02nombres").trim()));
			} else {
				HashMap hm = dao.joinWithT12T99ByCodPers(dbpool, codPers, null);
				if (hm.get("t02cod_pers")!=null){
				  registro.put("cod_pers", hm.get("t02cod_pers"));
				  registro.put("fecha", hm.get("t02f_ingsun"));
				  registro.put("trabajador", ((String)hm.get("t02ap_pate")).concat( " ").concat((String) hm.get("t02ap_mate") ).concat( ", " ).concat((String) hm.get("t02nombres")));
				}else{
				  registro.put("cod_pers", "NO EXISTE");
				  registro.put("fecha", "");
				  registro.put("trabajador", "NO EXISTE");
				}
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
		
		return registro;
	}

	/**
	 * M�todo encargado de marcar como inactivos los registros activos de
	 * reincorporaci�n de un trabajador espec�fico.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param mapa
	 *            HashMap. Conteniendo los par�metros de la actualizaci�n
	 *            ("codPers" y "usuario").
	 * @throws SQLException
	 */
	public void desactivarByCodPers(HashMap mapa) throws SQLException {

		String codPers = (String) mapa.get("codPers");
		String usuario = (String) mapa.get("usuario");
		String dbpool = (String) mapa.get("dbpool");

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;

		try {

			strSQL.append("update t1456vacacion_gen set est_id = ?, fmod = ?, cuser_mod = ? "
					).append( " where est_id = ? and cod_pers = ?");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, Constantes.INACTIVO);
			pre.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			pre.setString(3, usuario);
			pre.setString(4, Constantes.ACTIVO);
			pre.setString(5, codPers.trim());
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
	 * Metodo que se encarga de la busqueda de los datos de los trabajadores
	 * cuya fecha de ingreso o reingreso a la Sunat sea menor a la indicada por
	 * el parametro respectivo, y que no temgan generado aun un registro en la
	 * tabla T1281Vacaciones_c para el ano indicado por el parametro
	 * correspondiente.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param criterio
	 *            String. Criterio de filtro de los registros.
	 * @param valor
	 *            String. Valor de filtro para el criterio dado.
	 * @param anno
	 *            String. Ano para el cual no debe haber registro en la tabla
	 *            T1282Vacaciones_c.
	 * @param fechaActual
	 *            Fecha maxima permitida de ingreso o reingreso.
	 * @return ArrayList conteniendo los registros cargados en HashMaps.
	 * @throws SQLException
	 */
	public ArrayList findByFIngFRepuesto(String dbpool, String criterio,
			String valor, String anno, String fechaActual, HashMap seguridad)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList personal = null;

		try {

			String fIni = Utiles.toYYYYMMDD(fechaActual);

			strSQL.append("select 	p.t02cod_pers, substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) cod_uorga, p.t02f_ingsun, p.t02cod_ante, "
					).append( " (select g.fecha from t1456vacacion_gen g "
					).append( "  where g.cod_pers = p.t02cod_pers and est_id = ? ) frepuesto "
					).append( "from 	t02perdp p "
					).append( "where 	(p.t02f_ingsun <= ? "
					).append( "         or ( select fecha "
					).append( "              from t1456vacacion_gen "
					).append( "              where cod_pers = p.t02cod_pers and est_id = ? ) <= DATE('"
					).append( fIni ).append( "'))" ).append( "        and p.t02cod_stat = ? ");

			if (criterio.equals("0")) {
				strSQL.append(" and p.t02cod_pers = '" ).append( valor.trim().toUpperCase()
						).append( "' ");
			}
			if (criterio.equals("1")) {
				strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = '"
						).append( valor.trim().toUpperCase() ).append( "' ");
			}

			strSQL.append("  and p.t02cod_pers not in ( "
					).append( "                select cod_pers  "
					).append( "                from t1281vacaciones_c  "
					).append( "                where cod_pers > '0' and anno = '" ).append( anno
					).append( "' and dias > 0) ");

			//criterios de visibilidad
//			if (seguridad != null) {
			if (seguridad != null && !seguridad.isEmpty()) {				

				String codigo = (String) seguridad.get("codPers");

				strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) in ");
				strSQL.append(" (select u_organ from t1485seg_uorga "
						).append( " where cod_pers = '" ).append( codigo.toUpperCase()
						).append( "' and operacion = '" ).append( Constantes.PROCESO_VACACIONES
						).append( "') ");
			}

			strSQL.append(" order by p.t02cod_pers");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, Constantes.ACTIVO);
			pre.setTimestamp(2, Utiles.stringToTimestamp(fechaActual+ " 00:00:00"));
			pre.setString(3, Constantes.ACTIVO);
			pre.setString(4, Constantes.ACTIVO);
			rs = pre.executeQuery();
			personal = new ArrayList();

			while (rs.next()) {

				HashMap a = new HashMap();

				a.put("t02cod_pers", rs.getString("t02cod_pers"));
				a.put("t02cod_uorg", rs.getString("cod_uorga"));
				a.put("f_ingsun", rs.getString("t02f_ingsun"));
				a.put("t02cod_ante", rs.getString("t02cod_ante"));
				a.put("f_repuesto", rs.getString("frepuesto"));
				a.put("fecha", a.get("f_repuesto") != null ? (String) a
						.get("f_repuesto") : (String) a.get("f_ingsun"));

				personal.add(a);

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
		return personal;
	}

	/**
	 * Metodo que se encarga de la busqueda de los datos de los trabajadores
	 * cuya fecha de ingreso o reingreso a la Sunat sea menor a la indicada por
	 * el parametro respectivo.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param valor
	 *            String. Valor de filtro para el criterio dado.
	 * @param fechaActual
	 *            Fecha maxima permitida de ingreso o reingreso.
	 * @return ArrayList conteniendo los registros cargados en HashMaps.
	 * @throws SQLException
	 */
	public String findByFecha(String dbpool, 
			String valor, String fechaActual)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		String personal = null;

		try {

			String fIni = Utiles.toYYYYMMDD(fechaActual);

			strSQL.append("select p.t02f_ingsun, "
					).append( " (select g.fecha from t1456vacacion_gen g "
					).append( "  where g.cod_pers = p.t02cod_pers and est_id = ? ) frepuesto "
					).append( "from 	t02perdp p "
					).append( "where 	(p.t02f_ingsun <= ? "
					).append( "         or ( select fecha "
					).append( "              from t1456vacacion_gen "
					).append( "              where cod_pers = p.t02cod_pers and est_id = ? ) <= DATE('"
					).append( fIni ).append( "'))" ).append( "        and p.t02cod_stat = ? ");

				strSQL.append(" and p.t02cod_pers = '" ).append( valor.trim().toUpperCase()
						).append( "' ");


			

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, Constantes.ACTIVO);
			pre.setTimestamp(2, Utiles.stringToTimestamp(fechaActual+ " 00:00:00"));
			pre.setString(3, Constantes.ACTIVO);
			pre.setString(4, Constantes.ACTIVO);
			rs = pre.executeQuery();
			
			while (rs.next()) {

				HashMap a = new HashMap();

				a.put("f_ingsun", Utiles.dateToString(rs.getDate("t02f_ingsun")));
				a.put("f_repuingsun", Utiles.dateToString(rs.getDate("frepuesto")));
				a.put("f_repuesto", rs.getString("frepuesto"));
				a.put("fecha", a.get("f_repuesto") != null ? (String) a
						.get("f_repuingsun") : (String) a.get("f_ingsun"));

				personal = (String)a.get("fecha");

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
		return personal;
	}
	
	//ICAPUNAY 25/06/2012 AOM 06A4T11 LABOR EXCEPCIONAL Y COMPENSACIONES-PAS20124E550000064
	/**
	 * Metodo encargado de buscar el registro activo de la fecha de reingreso o
	 * ingreso de un trabajador especifico. Join con la tabla T02Perdp.
	 * @throws SQLException
	 */
	public HashMap findFechaReingreso(String dbpool, String codPers)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		HashMap registro = new HashMap();

		try {
			
			strSQL.append("select 	v.cod_pers, v.fecha, p.t02nombres, p.t02ap_pate, p.t02ap_mate "
					).append( " from t1456vacacion_gen v, "
					).append( "      t02perdp p "
					).append( " where  v.est_id = ? and"
					).append( "        v.cod_pers = ? and p.t02cod_pers = v.cod_pers ");
			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, Constantes.ACTIVO);
			pre.setString(2, codPers.trim().toUpperCase());
			rs = pre.executeQuery();
			if (rs.next()) {
				registro.put("cod_pers", rs.getString("cod_pers"));
				registro.put("fecha", Utiles.timeToFecha(rs
						.getTimestamp("fecha")));
				registro.put("trabajador", rs.getString("t02ap_pate").trim()
						.concat(" " ).concat( rs.getString("t02ap_mate").trim() ).concat( ", ").concat( rs.getString("t02nombres").trim()));
			} else {				
				registro.put("cod_pers", "NO EXISTE");
				registro.put("fecha", "");
				registro.put("trabajador", "NO EXISTE");			
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
		
		return registro;
	}
	//FIN ICAPUNAY 25/06/2012 AOM 06A4T11 LABOR EXCEPCIONAL Y COMPENSACIONES-PAS20124E550000064
}