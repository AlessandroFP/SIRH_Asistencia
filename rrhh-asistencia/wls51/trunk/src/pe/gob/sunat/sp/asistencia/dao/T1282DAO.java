	package pe.gob.sunat.sp.asistencia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import pe.gob.sunat.sol.dao.DAOAccesoBD;
import pe.gob.sunat.sp.asistencia.bean.BeanVacacion;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;

/**
 * <p>
 * Title: T1282DAO
 * </p>
 * <p>
 * Description: Clase encargada de administrar las consultas a la tabla
 * T1282Vacacion_d
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

public class T1282DAO extends DAOAccesoBD {

	private static final Logger log = Logger.getLogger(T1282DAO.class);

	public T1282DAO() {
	}

	/**
	 * Metodo encargado de verificar si un trabajador tiene un registro de
	 * vacacion efectiva o especial cuyas fechas sean abarcadas por los
	 * parametros indicados.
	 * @throws SQLException
	 */
	public boolean findByCodPersFIniFFin(String dbpool, String codPers,
			String fecha1, String fecha2) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		boolean tiene = false;

		try {
			String fecCompara1 = Utiles.toYYYYMMDD(fecha1);
			String fecCompara2 = "";
			if (fecha2.trim() != "") {
				fecCompara2 = Utiles.toYYYYMMDD(fecha2);
			}

			strSQL.append("select cod_pers from t1282Vacaciones_d "
					).append( " where cod_pers = ? and (licencia = ? or licencia = ?) ");

			if (!fecha2.equals("")) {

				if (!fecha1.equals(fecha2)) {
					strSQL.append(" and ((ffinicio <= DATE('" ).append( fecCompara1
							).append( "') and ffin >= DATE('" ).append( fecCompara2 ).append( "')) ");
					strSQL.append(" or (ffinicio >= DATE('" ).append( fecCompara1
							).append( "') and ffin <= DATE('" ).append( fecCompara2 ).append( "')) ");
					strSQL.append(" or (ffinicio <= DATE('" ).append( fecCompara1
							).append( "') and ffin <= DATE('" ).append( fecCompara2
							).append( "') and ffin >= DATE('" ).append( fecCompara1 ).append( "') ) ");
					strSQL.append(" or (ffinicio >= DATE('" ).append( fecCompara1
							).append( "') and ffin >= DATE('" ).append( fecCompara2
							).append( "') and ffinicio <= DATE('" ).append( fecCompara2
							).append( "'))) ");
				} else {
					
					strSQL.append(" and (ffinicio <= DATE('" ).append( fecCompara1
							).append( "'))  and (ffin >= DATE('" ).append( fecCompara2 ).append( "')) ");
				}

			} else {
				
				strSQL.append(" and (ffinicio <=DATE('" ).append( fecCompara1
						).append( "'))  and (ffin >= DATE('" ).append( fecCompara1 ).append( "')) ");
			}

			con = getConnection(dbpool);
			
			//con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, Constantes.VACACION);
			pre.setString(3, Constantes.VACACION_ESPECIAL);

			
			rs = pre.executeQuery();
			if (rs.next())
				tiene = true;

 
			
			
		} catch (Exception e) {
			log.error("**** SQL ERROR **** "+ e.toString());
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
		return tiene;
	}

	/**
	 * M�todo encargado de verificar si un trabajador tiene un registro de
	 * vacacio�n efectiva o especial cuyas fechas sean abarcadas por los
	 * par�metros indicados.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            String. N�mero de registro del trabajador.
	 * @param fecha1
	 *            String. Fecha inferior del rango de b�squeda.
	 * @param fecha2
	 *            String. Fecha superior del rango de b�squeda.
	 * @return Booelan. Devuelve "verdadero" si existe por lo menos un registro
	 *         que cumpla con las condiciones.
	 * @throws SQLException
	 */
	public boolean findByCodPersFIniFFin(String dbpool, String codPers,
			String fecha1, String fecha2, boolean prog, String fechaIni)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		boolean tiene = false;

		try {
			
			
			
			
			String fecCompara1 = Utiles.toYYYYMMDD(fecha1);
			String fecCompara2 = "";
			String fecIni = "";
			if (fecha2.trim() != "") {
				fecCompara2 = Utiles.toYYYYMMDD(fecha2);
			}
			if (fechaIni.trim() != "") {
				fecIni = Utiles.toYYYYMMDD(fechaIni);
			}

			strSQL.append("select cod_pers from t1282Vacaciones_d "
					).append( " where cod_pers = ? and (licencia = ? or licencia = ? ");

			if (prog) {
				strSQL.append(" or ( (licencia = ? and (est_id = ? or est_id = ?)) ");
				strSQL.append(" 	  or (licencia = ? and est_id = ?) ) ");
			}

			strSQL.append(" ) ");

			if (!fecha2.equals("")) {

				if (!fecha1.equals(fecha2)) {
					strSQL.append(" and ((ffinicio <= DATE('" ).append( fecCompara1
							).append( "') and ffin >= DATE('" ).append( fecCompara2 ).append( "')) ");
					strSQL.append(" or (ffinicio >= DATE('" ).append( fecCompara1
							).append( "') and ffin <= DATE('" ).append( fecCompara2 ).append( "')) ");
					strSQL.append(" or (ffinicio <= DATE('" ).append( fecCompara1
							).append( "') and ffin <= DATE('" ).append( fecCompara2
							).append( "') and ffin >= DATE('" ).append( fecCompara1 ).append( "') ) ");
					strSQL.append(" or (ffinicio >= DATE('" ).append( fecCompara1
							).append( "') and ffin >= DATE('" ).append( fecCompara2
							).append( "') and ffinicio <= DATE('" ).append( fecCompara2
							).append( "'))) ");
				} else {
					strSQL.append(" and (ffinicio <= DATE('" ).append( fecCompara1
							).append( "'))  and (ffin >= DATE('" ).append( fecCompara2 ).append( "')) ");
				}

			} else {
				strSQL.append(" and (ffinicio <=DATE('" ).append( fecCompara1
						).append( "'))  and (ffin >= DATE('" ).append( fecCompara1 ).append( "')) ");
			}

			if (!fechaIni.equals("")) {
				strSQL.append(" and ffinicio != DATE('" ).append( fecIni ).append( "') ");
			}

			
			
			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, Constantes.VACACION);
			pre.setString(3, Constantes.VACACION_ESPECIAL);
			if (prog) {
				pre.setString(4, Constantes.VACACION_PROGRAMADA);
				pre.setString(5, Constantes.PROG_ACEPTADA);
				pre.setString(6, Constantes.PROG_PROGRAMADA);
				pre.setString(7, Constantes.REPROGRAMACION_VACACION);
				pre.setString(8, Constantes.PROG_ACEPTADA);
			}

			rs = pre.executeQuery();
			if (rs.next())
				tiene = true;

 

		} catch (Exception e) {

			e.printStackTrace();
			log.error("**** SQL ERROR **** "+ e.toString());
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
		return tiene;
	}

	/**
	 * M�todo encargado de buscar los datos de los detalles de vacaciones de un
	 * trabajador, filtrados por a�o, estado y licencia.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            String. N�mero de registro del trabajador.
	 * @param anho
	 *            String. A�o de b�squeda.
	 * @param estado
	 *            String. Estado de los registros deseados.
	 * @param licencia
	 *            String. Tipo de vacaci�n buscada.
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanVacacion.
	 * @throws SQLException
	 */
	public ArrayList findByCodPersAnnoVacEstIdLicencia(String dbpool,
			String codPers, String anho, String estado, String licencia)
			throws SQLException {
		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList vacaciones = null;

		try {

			strSQL.append(" select cod_pers, ffinicio, ffin, anno_vac, dias, observ,"
					).append( "        periodo, licencia, est_id, anno "
					).append( " from   t1282vacaciones_d "
					).append( " where  cod_pers = ? and est_id = ?");

			if (!anho.equals("")) {
				strSQL.append(" and anno_vac = '" ).append( anho.trim() ).append( "'");
			}

			if (!licencia.equals("")) {
				strSQL.append(" and licencia = '" ).append( licencia.trim() ).append( "'");
			}

			strSQL.append(" order by ffinicio");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers.trim());
			pre.setString(2, estado);

			rs = pre.executeQuery();
			vacaciones = new ArrayList();
			BeanVacacion vac = null;

			while (rs.next()) {

				vac = new BeanVacacion();

				vac.setCodPers(rs.getString("cod_pers"));
				vac.setFechaInicio(rs.getTimestamp("ffinicio"));
				vac.setFechaFin(rs.getTimestamp("ffin"));
				vac.setAnnoVac(rs.getString("anno_vac"));
				vac.setDias(rs.getInt("dias"));
				vac.setObservacion(rs.getString("observ").trim());
				vac.setPeriodo(rs.getString("periodo"));
				vac.setLicencia(rs.getString("licencia"));
				//vac.setNumero(rs.getString("numero"));
				vac.setEstado(rs.getString("est_id"));

				vacaciones.add(vac);
			}

 
		}

		catch (Exception e) {
			log.error("**** SQL ERROR **** "+ e.toString());
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
		return vacaciones;
	}

	/**
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codPers
	 * @param anho
	 * @param estado
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList findVacacionesPorFirmar(String dbpool, String codPers,
			String anho, String estado) throws SQLException {
		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList vacaciones = null;

		try {

			strSQL.append("select * from t1282vacaciones_d where cod_pers = ? and est_id = ?");

			if (!anho.equals("")) {
				strSQL.append(" and anno = '" ).append( anho ).append( "'");
			}

			strSQL.append(" and (licencia = ? or licencia = ?) order by ffinicio");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers.trim());
			pre.setString(2, estado);
			pre.setString(3, Constantes.VACACION);
			pre.setString(4, Constantes.VACACION_ESPECIAL);

			rs = pre.executeQuery();
			vacaciones = new ArrayList();
			BeanVacacion vac = null;

			while (rs.next()) {

				vac = new BeanVacacion();

				vac.setCodPers(rs.getString("cod_pers"));
				vac.setFechaInicio(rs.getTimestamp("ffinicio"));
				vac.setFechaFin(rs.getTimestamp("ffin"));
				vac.setAnnoVac(rs.getString("anno_vac"));
				vac.setDias(rs.getInt("dias"));
				vac.setObservacion(rs.getString("observ").trim());
				vac.setPeriodo(rs.getString("periodo"));
				vac.setLicencia(rs.getString("licencia"));
				//vac.setNumero(rs.getString("numero"));
				vac.setEstado(rs.getString("est_id"));

				vacaciones.add(vac);
			}

 
		}

		catch (Exception e) {
			log.error("**** SQL ERROR **** "+ e.toString());
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
		return vacaciones;
	}

	/**
	 * M�todo encargado de buscar los datos de los detalles de vacaciones de un
	 * trabajador, filtrados por a�o y estado.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            String. N�mero de registro del trabajador.
	 * @param anho
	 *            String. A�o de b�squeda.
	 * @param estado
	 *            String. Estado de los registros deseados.
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanVacacion.
	 * @throws SQLException
	 */
	public ArrayList findByCodPersAnnoEstId(String dbpool, String codPers,
			String anho, String estado) throws SQLException {
		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList vacaciones = null;
		int i = 1;

		try {

			strSQL.append("select cod_pers, periodo, ffinicio, anno_vac, ffin, "
					).append( " licencia, dias, u_organ, observ, fcreacion, cuser_crea, est_id"
					).append( " from t1282vacaciones_d where cod_pers = ?");

			if (!anho.trim().equals("")) {
				strSQL.append(" and year(ffinicio) = ? ");
				i++;
			}

			if (!estado.trim().equals("")) {
				strSQL.append(" and est_id = ? ");
				i++;
			}

			strSQL.append(" order by ffinicio");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);

			if (!anho.trim().equals("")) {
				pre.setString(2, anho);
			}
			if (!estado.trim().equals("")) {
				pre.setString(i, estado);
			}

			rs = pre.executeQuery();
			vacaciones = new ArrayList();
			BeanVacacion vac = null;

			while (rs.next()) {

				vac = new BeanVacacion();

				//vac.setNumero(rs.getString("numero"));
				vac.setCodPers(rs.getString("cod_pers"));
				vac.setPeriodo(rs.getString("periodo"));
				vac.setFechaInicio(rs.getTimestamp("ffinicio"));
				vac.setAnnoVac(rs.getString("anno_vac"));
				vac.setFechaFin(rs.getTimestamp("ffin"));
				vac.setLicencia(rs.getString("licencia"));
				vac.setDias(rs.getInt("dias"));
				vac.setCodUO(rs.getString("u_organ"));
				vac.setObservacion(rs.getString("observ").trim());
				vac.setFCrea(rs.getTimestamp("fcreacion"));
				vac.setUsrCrea(rs.getString("cuser_crea"));
				vac.setEstado(rs.getString("est_id"));

				vacaciones.add(vac);
			}

 
		}

		catch (Exception e) {
			log.error("**** SQL ERROR **** "+ e.toString());
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
		return vacaciones;
	}

	/**
	 * Metodo encargado de buscar los registros de detalle de vacaciones
	 * filtrados por estado, registro del trabajador, tipo de vacacion y ano.
	 * Join con la tabla T1279Tipo_Mov.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            String. Numero de registro del trabajador.
	 * @param estado
	 *            String. Estado de los registros deseados.
	 * @param licencia
	 *            String. Tipo de vacacion deseada.
	 * @param anho
	 *            String. Ano de busqueda.
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanVacacion.
	 * @throws SQLException
	 */
	public ArrayList joinWithT99ByCodPersEstIdLicenciaAnhoFechaIni(
			String dbpool, String codPers, String estado, String licencia,
			String anho, HashMap seguridad) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList vacaciones = null;

		try {

			strSQL.append("select  v.cod_pers, v.periodo, v.ffinicio, v.ffin, v.anno_vac,"
					).append( "       v.dias, v.u_organ, v.observ, v.periodo, v.licencia, v.fcreacion,"
					).append( "       v.cuser_crea, tm.descrip as tipo, v.est_id, "
					).append( "       v.anno_ref, v.area_ref, v.numero_ref, v.cuser_mod "
					).append( " from  t1282vacaciones_d v, "
					).append( "       t1279tipo_mov tm, "
					).append( "       t02perdp p"
					).append( " where tm.tipo_id = '"
					).append( Constantes.TIPO_MOV_VACACIONES
					).append( "' " ).append( " and v.cod_pers = ? ");

			//criterios de visibilidad
			if (seguridad != null) {
				HashMap roles = (HashMap) seguridad.get("roles");
				String uoSeg = (String) seguridad.get("uoSeg");
				String uoAO = (String) seguridad.get("uoAO");//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

				if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
					strSQL.append(" and 1=1 ");
				} else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null) {
					strSQL.append(" and ((substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '"
							).append( uoSeg ).append( "') ");
					
					strSQL.append("   or (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) in ")
					.append(" (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip = '"							
					).append( uoAO ).append( "'))) ");
					
				}else if (//roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null || 
						roles.get(Constantes.ROL_JEFE) != null) {
					strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '"
							).append( uoSeg ).append( "' ");
				}else {
					strSQL.append(" and 1=2 ");
				}
			}

			if (!estado.trim().equals("")) {
				strSQL.append(" and v.est_id = ?");
			}

			if (!licencia.equals("")) {
				if (licencia.trim().equals(Constantes.VACACION_PROGRAMADA)) {

					strSQL.append(" and ((v.licencia='"
							).append( Constantes.VACACION_PROGRAMADA ).append( "' ");
					strSQL.append("      and v.est_id != '"
							).append( Constantes.PROG_RECHAZADA ).append( "' ");
					strSQL.append("		 and v.est_id != '"
							).append( Constantes.PROG_EFECTUADA ).append( "' ) ");
					strSQL.append(" or (v.licencia='"
							).append( Constantes.REPROGRAMACION_VACACION ).append( "' ");
					strSQL.append("      and v.est_id = '"
							).append( Constantes.PROG_ACEPTADA ).append( "')) ");
				} else {
					strSQL.append(" and v.licencia='" ).append( licencia.trim() ).append( "' ");
				}

			} else {
				strSQL.append(" and (v.licencia='" ).append( Constantes.VACACION
						).append( "' or v.licencia='" ).append( Constantes.VACACION_ESPECIAL
						).append( "') ");
			}

			if (!anho.trim().equals("")) {
				//if (licencia.trim().equals(Constantes.VACACION_PROGRAMADA)
				//		|| licencia.trim().equals(Constantes.VACACION_VENTA)) {
					strSQL.append(" and v.anno_vac = '" ).append( anho ).append( "' ");
				//} else {
				//	strSQL.append(" and year(v.ffinicio) = " ).append( anho);
				//}
			}

			strSQL.append(" and tm.mov = v.licencia "
					).append( " and v.cod_pers = p.t02cod_pers ");

			strSQL.append(" order by v.anno_vac, v.ffinicio ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers.trim().toUpperCase());
			if (!estado.trim().equals("")) {
				pre.setString(2, estado);
			}

			rs = pre.executeQuery();
			vacaciones = new ArrayList();
			BeanVacacion vac = null;

			while (rs.next()) {

				vac = new BeanVacacion();

				vac.setCodPers(rs.getString("cod_pers"));
				vac.setPeriodo(rs.getString("periodo"));
				vac.setFechaInicio(rs.getTimestamp("ffinicio"));
				vac.setFechaFin(rs.getTimestamp("ffin"));
				vac.setAnnoVac(rs.getString("anno_vac"));
				vac.setDias(rs.getInt("dias"));
				vac.setCodUO(rs.getString("u_organ"));
				vac.setObservacion(rs.getString("observ").trim());
				vac.setPeriodo(rs.getString("periodo"));
				vac.setLicencia(rs.getString("licencia"));
				vac.setTipo(rs.getString("tipo"));
				vac.setFCrea(rs.getTimestamp("fcreacion"));
				vac.setUsrCrea(rs.getString("cuser_crea"));
				vac.setEstado(rs.getString("est_id"));
				vac.setAnnoRef(rs.getString("anno_ref"));
				vac.setAreaRef(rs.getString("area_ref"));
				vac.setNumeroRef(rs.getString("numero_ref"));
				vac.setUsrMod(rs.getString("cuser_mod"));

				vacaciones.add(vac);
			}

 
		}

		catch (Exception e) {
			log.error("**** SQL ERROR **** "+ e.toString());
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
		return vacaciones;
	}

	/**
	 * Metodo encargado de obtener los dias de los detalles de vacaciones
	 * filtrados por registro de trabajador, ano y tipo de vacacion
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            Numero de registro del trabajador.
	 * @param anho
	 *            Ano de busqueda.
	 * @param licencia
	 *            Tipo de vacacion deseada.
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanVacacion.
	 * @throws SQLException
	 */
	public ArrayList findByCodPersAnnoVacLicencia(String dbpool,
			String codPers, String anho, String licencia, String fechaIni)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList vacaciones = null;

		try {

			String fecIni = "";
			if (fechaIni.trim() != "") {
				fecIni = Utiles.toYYYYMMDD(fechaIni);
			}

			strSQL.append("select dias from t1282vacaciones_d where cod_pers = ? "
					).append( " and anno_vac = ? and licencia = ? ");

			if (!fechaIni.equals("")) {
				strSQL.append(" and ffinicio != DATE('" ).append( fecIni ).append( "') ");
			}

			strSQL.append(" order by dias");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers.trim());
			pre.setString(2, anho.trim());
			pre.setString(3, licencia.trim());

			rs = pre.executeQuery();
			vacaciones = new ArrayList();

			while (rs.next()) {
				vacaciones.add(rs.getString("dias"));
			}
 
		}

		catch (Exception e) {
			log.error("**** SQL ERROR **** "+ e.toString());
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
		return vacaciones;
	}

	/**
	 * M�todo encargado de buscar la lista de vacaciones efectivas y especiales
	 * ya gozadas hasta la fecha indicada.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param mapa
	 *            HashMap que contiene los par�metros de b�squeda ("fechaFin",
	 *            "codPers" y "dias").
	 * @return ArrayList conteniendo los registros cargados en HashMaps.
	 * @throws SQLException
	 */
	public ArrayList findByCodPersFechaFinDiasGoz(HashMap mapa)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;

		String dbpool = (String) mapa.get("dbpool");

		String fechaFin = mapa.get("fechaFin") != null ? mapa.get("fechaFin")
				.toString() : "";
		String codPers = mapa.get("codPers") != null ? mapa.get("codPers")
				.toString() : "";
		int dias = mapa.get("dias") != null ? Integer.parseInt(mapa.get("dias")
				.toString()) : 0;
		//Orden: 1 Ascendente y 0 descendente
		String orden = mapa.get("orden") != null ? mapa.get("orden").toString()
				: Constantes.ACTIVO;

		try {

			strSQL.append("select  anno_vac, ffinicio, ffin, dias "
					).append( "from	t1282vacaciones_d "
					).append( "where 	cod_pers = ? and (licencia = ? or licencia = ?)");

			if (!fechaFin.equals("")) {
				strSQL.append(" and ffin <= DATE('" ).append( Utiles.toYYYYMMDD(fechaFin)
						).append( "')");
			}

			if (dias > 0) {
				strSQL.append(" and dias >= " ).append( dias);
			}

			strSQL.append(" order by ffinicio");

			if (orden.trim().equals(Constantes.INACTIVO)) {
				strSQL.append(" desc");
			}

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, Constantes.VACACION);
			pre.setString(3, Constantes.VACACION_ESPECIAL);
			rs = pre.executeQuery();

			lista = new ArrayList();
			HashMap hVac = null;

			while (rs.next()) {

				hVac = new HashMap();

				hVac.put("anno_vac", rs.getString("anno_vac") != null ? rs
						.getString("anno_vac") : "");
				hVac.put("ffinicio", rs.getString("ffinicio") != null ? Utiles
						.timeToFecha(rs.getTimestamp("ffinicio")) : "");
				hVac.put("ffin", rs.getString("ffin") != null ? Utiles
						.timeToFecha(rs.getTimestamp("ffin")) : "");
				hVac.put("dias", rs.getString("dias") != null ? rs
						.getString("dias") : "");

				lista.add(hVac);
			}

 
		} catch (Exception e) {
			log.error("**** SQL ERROR **** "+ e.toString());
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
		return lista;
	}

	/**
	 * M�todo encargado de buscar la lista de vacaciones efectivas y especiales
	 * por gozar a la fecha indicada.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param mapa
	 *            HashMap que contiene los par�metros de b�squeda ("fechaFin",
	 *            "codPers" y "dias").
	 * @return ArrayList conteniendo los registros cargados en HashMaps.
	 * @throws SQLException
	 */
	public ArrayList findByCodPersFechaFinDiasPend(HashMap mapa)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;

		String fechaFin = mapa.get("fechaFin") != null ? mapa.get("fechaFin")
				.toString() : "";
		String codPers = mapa.get("codPers") != null ? mapa.get("codPers")
				.toString() : "";
		int dias = mapa.get("dias") != null ? Integer.parseInt(mapa.get("dias")
				.toString()) : 0;
		//Orden: 1 Ascendente y 0 descendente
		String orden = mapa.get("orden") != null ? mapa.get("orden").toString()
				: Constantes.ACTIVO;
		String dbpool = (String) mapa.get("dbpool");

		try {

			strSQL.append("select  anno_vac, ffinicio, ffin, dias "
					).append( "from	t1282vacaciones_d "
					).append( "where 	cod_pers = ? and (licencia = ? or licencia = ?)");

			if (!fechaFin.equals("")) {
				strSQL.append(" and ffin > DATE('" ).append( Utiles.toYYYYMMDD(fechaFin)
						).append( "')");
			}

			if (dias > 0) {
				strSQL.append(" and dias >= " ).append( dias);
			}

			strSQL.append(" order by ffinicio");

			if (orden.trim().equals(Constantes.INACTIVO)) {
				strSQL.append(" desc");
			}

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, Constantes.VACACION);
			pre.setString(3, Constantes.VACACION_ESPECIAL);
			rs = pre.executeQuery();

			lista = new ArrayList();
			HashMap hVac = null;

			while (rs.next()) {

				hVac = new HashMap();

				hVac.put("anno_vac", rs.getString("anno_vac") != null ? rs
						.getString("anno_vac") : "");
				hVac.put("ffinicio", rs.getString("ffinicio") != null ? Utiles
						.timeToFecha(rs.getTimestamp("ffinicio")) : "");
				hVac.put("ffin", rs.getString("ffin") != null ? Utiles
						.timeToFecha(rs.getTimestamp("ffin")) : "");
				hVac.put("dias", rs.getString("dias") != null ? rs
						.getString("dias") : "");

				lista.add(hVac);
			}

 
		} catch (Exception e) {
			log.error("**** SQL ERROR **** "+ e.toString());
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
		return lista;
	}

	public int findByCodPersLicenciaAnhoVac(String dbpool, String codPers,
			String licencia, String anho) throws SQLException {
		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		int acumulado = 0;

		try {

			strSQL.append("select sum(dias) as acumulado"
					).append( " from  t1282vacaciones_d " ).append( " where cod_pers = ? and "
					).append( "       anno_vac = '" ).append( anho.trim() ).append( "' ");

			
			if (licencia.equals("46")){
				strSQL.append(" and licencia = '").append( licencia ).append( "' ");	
			}else{
			   if (licencia.equals(Constantes.VACACION_PROGRAMADA)) {
  				 strSQL.append(" and ((licencia = '"
						).append( Constantes.VACACION_PROGRAMADA ).append( "' ");
				 strSQL.append(" 		and (est_id != '" ).append( Constantes.PROG_EFECTUADA
						).append( "' and est_id != '" ).append( Constantes.PROG_RECHAZADA
						).append( "')) ");
				 strSQL.append(" or (licencia = '"
						).append( Constantes.REPROGRAMACION_VACACION ).append( "' ");
				 strSQL.append(" 	and est_id = '" ).append( Constantes.PROG_ACEPTADA
						).append( "')) ");

			   } else {
				strSQL.append(" and licencia = '" ).append( licencia.trim() ).append( "' ");
				strSQL.append(" and (est_id != '" ).append( Constantes.PROG_EFECTUADA
						).append( "' and est_id != '" ).append( Constantes.PROG_RECHAZADA
						).append( "') ");
			   }
			}

			
			
			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers.trim().toUpperCase());
			rs = pre.executeQuery();

			if (rs.next()) {
				acumulado = rs.getInt("acumulado");
			}

 
		}

		catch (Exception e) {
			log.error("**** SQL ERROR **** "+ e.toString());
			throw new SQLException(e.toString());
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
			}
			try {
				if (pre != null)
					pre.close();
			} catch (Exception e) {
			}
			try {
				if (con != null)
					con.close();
			} catch (Exception e) {
			}
		}
		return acumulado;
	}

	/**
	 * M�todo encargado de buscar la lista de vacaciones efectivas y especiales
	 * por gozar a la fecha indicada.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param mapa
	 *            HashMap que contiene los par�metros de b�squeda ("fechaFin",
	 *            "codPers" y "dias").
	 * @return ArrayList conteniendo los registros cargados en HashMaps.
	 * @throws SQLException
	 */
	public ArrayList findPendientes(HashMap mapa) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;

		String codPers = mapa.get("codPers") != null ? (String) mapa
				.get("codPers") : "";
		String orden = mapa.get("orden") != null ? (String) mapa.get("orden")
				: "1";
		String licencia = mapa.get("licencia") != null ? (String) mapa
				.get("licencia") : "";
		String dbpool = (String) mapa.get("dbpool");
		String fechaFin = Utiles.obtenerFechaActual();

		try {

			strSQL.append("select vd.cod_pers, vd.periodo , vd.anno_vac, "
					).append( "       vd.ffinicio, vd.ffin, vd.dias, tp.descrip, vd.observ "
					).append( "from   t1282vacaciones_d vd, "
					).append( "       t1279tipo_mov tp " ).append( "where  vd.cod_pers = ? ");

			if (licencia.trim().equals("")) {
				strSQL.append(" and (vd.licencia = '" ).append( Constantes.VACACION
						).append( "' or vd.licencia = '" ).append( Constantes.VACACION_ESPECIAL
						).append( "' )");
			} else {

				if (licencia.trim().equals(Constantes.VACACION_PROGRAMADA)) {
					strSQL.append(" and (vd.licencia = '"
							).append( Constantes.VACACION_PROGRAMADA ).append( "' ");
					strSQL.append(" or vd.licencia = '"
							).append( Constantes.REPROGRAMACION_VACACION ).append( "' ) ");
					strSQL.append(" and vd.est_id = '" ).append( Constantes.PROG_ACEPTADA
							).append( "' ");
				} else if (licencia.trim().equals(
						Constantes.REPROGRAMACION_VACACION)) {
					strSQL.append(" and vd.licencia = '"
							).append( Constantes.VACACION_PROGRAMADA ).append( "' ");
					strSQL.append(" and vd.est_id = '" ).append( Constantes.PROG_ACEPTADA
							).append( "' ");
				} else {
					strSQL.append(" and vd.licencia = '" ).append( licencia.trim() ).append( "' )");
				}
			}

			if (!fechaFin.equals("")) {
				strSQL.append(" and vd.ffin > DATE('" ).append( Utiles.toYYYYMMDD(fechaFin)
						).append( "')");
			}

			strSQL.append(" and tp.mov = vd.licencia order by vd.ffinicio ");

			if (orden.trim().equals("0")) {
				strSQL.append(" desc");
			}

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);

			rs = pre.executeQuery();

			lista = new ArrayList();
			BeanVacacion bVac = null;

			while (rs.next()) {

				bVac = new BeanVacacion();

				bVac.setCodPers(rs.getString("cod_pers"));
				bVac.setPeriodo(rs.getString("periodo"));
				bVac.setAnnoVac(rs.getString("anno_vac"));
				bVac.setFechaInicio(rs.getTimestamp("ffinicio"));
				bVac.setFechaFin(rs.getTimestamp("ffin"));
				bVac.setDias(rs.getInt("dias"));
				bVac.setTipo(rs.getString("descrip").trim());
				bVac.setTipo(rs.getString("observ"));

				lista.add(bVac);
			}

 
		} catch (Exception e) {

			e.printStackTrace();
			log.error("**** SQL ERROR **** "+ e.toString());
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
		return lista;
	}

	/**
	 * M�todo encargado de buscar la lista de vacaciones registradas de un
	 * trabajador o de los trabajadores de una unidad organizacional para un a�o
	 * y tipo de licencias espec�ficos.
	 * 
	 * @param mapa
	 *            HashMap que contiene los par�metros de b�squeda ("dbpool",
	 *            "criterio", "valor", "anho", "licencia" y "codUsr").
	 * @return ArrayList conteniendo los registros cargados en HashMaps.
	 * @throws SQLException
	 */
	public ArrayList joinWithT02ByCritValAnhoLicencia(HashMap mapa)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;

		String valor = mapa.get("valor") != null ? (String) mapa.get("valor")
				: "";
		String codUO = mapa.get("codUO") != null ? (String) mapa.get("codUO")
				: "";
		String fechaIni = mapa.get("fechaIni") != null ? (String) mapa
				.get("fechaIni") : "";
		String fechaFin = mapa.get("fechaFin") != null ? (String) mapa
				.get("fechaFin") : "";
		String licencia = mapa.get("licencia") != null ? (String) mapa
				.get("licencia") : "";
		String dbpool = (String) mapa.get("dbpool");
		String codUsr = (String) mapa.get("codUsr");
		HashMap seguridad = (HashMap) mapa.get("seguridad");

		try {

			String fecCompara1 = Utiles.toYYYYMMDD(fechaIni);
			String fecCompara2 = "";
			if (fechaFin.trim() != "") {
				fecCompara2 = Utiles.toYYYYMMDD(fechaFin);
			}

			strSQL.append("select  v.cod_pers, v.periodo, v.licencia, "
					).append( "        v.ffinicio, v.ffin, v.dias, v.est_id, v.observ, "
					).append( "        p.t02ap_pate, p.t02ap_mate, p.t02nombres "
					).append( "from	t1282vacaciones_d v, t02perdp p "
					).append( "where v.licencia = ? ");

			if (!valor.trim().equals("")) {
				strSQL.append(" and p.t02cod_pers = '" ).append( valor.trim().toUpperCase()
						).append( "'");
			}
			strSQL.append(" and v.cod_pers != ? ");

			if (!fechaFin.equals("")) {

				if (!fechaIni.equals(fechaFin)) {

					strSQL.append(" and ((v.ffinicio <= DATE('" ).append( fecCompara1
							).append( "') and v.ffin >= DATE('" ).append( fecCompara2 ).append( "')) ");
					strSQL.append(" or (v.ffinicio >= DATE('" ).append( fecCompara1
							).append( "') and v.ffin <= DATE('" ).append( fecCompara2 ).append( "')) ");
					strSQL.append(" or (v.ffinicio <= DATE('" ).append( fecCompara1
							).append( "') and v.ffin <= DATE('" ).append( fecCompara2
							).append( "') and v.ffin >= DATE('" ).append( fecCompara1 ).append( "') ) ");
					strSQL.append(" or (v.ffinicio >= DATE('" ).append( fecCompara1
							).append( "') and v.ffin >= DATE('" ).append( fecCompara2
							).append( "') and v.ffinicio <= DATE('" ).append( fecCompara2
							).append( "'))) ");
				} else {
					strSQL.append(" and (v.ffinicio <= DATE('" ).append( fecCompara1
							).append( "'))  and (v.ffin >= DATE('" ).append( fecCompara2
							).append( "')) ");
				}

			} else {
				if (!fechaIni.equals("")) {
					strSQL.append(" and (v.ffinicio <=DATE('" ).append( fecCompara1
							).append( "'))  and (v.ffin >= DATE('" ).append( fecCompara1
							).append( "')) ");
				}
			}

			//criterios de visibilidad
			if (seguridad != null) {

				HashMap roles = (HashMap) seguridad.get("roles");

				if (roles.get(Constantes.ROL_JEFE) != null) {
					strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = '"
							).append( codUO.trim().toUpperCase() ).append( "' ");
				} else {
					strSQL.append(" and 1 = 2 )");
				}
			}

			strSQL.append(" and  p.t02cod_pers = v.cod_pers "
					).append( " order by v.ffinicio, p.t02ap_pate, p.t02ap_mate, p.t02nombres ");
						
			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, licencia);
			pre.setString(2, codUsr);
			
			
			rs = pre.executeQuery();

			lista = new ArrayList();
			HashMap hVac = null;
			while (rs.next()) {

				hVac = new HashMap();

				/*
				 * hVac.put("numero", rs.getString("numero") != null ? rs
				 * .getString("numero") : "");
				 */
				hVac.put("cod_pers", rs.getString("cod_pers") != null ? rs
						.getString("cod_pers") : "");
				hVac.put("periodo", rs.getString("periodo") != null ? rs
						.getString("periodo") : "");
				hVac.put("licencia", rs.getString("licencia") != null ? rs
						.getString("licencia") : "");
				hVac.put("ffinicio", rs.getString("ffinicio") != null ? Utiles
						.timeToFecha(rs.getTimestamp("ffinicio")) : "");
				hVac.put("ffin", rs.getString("ffin") != null ? Utiles
						.timeToFecha(rs.getTimestamp("ffin")) : "");
				hVac.put("dias", rs.getString("dias") != null ? rs
						.getString("dias") : "");
				hVac.put("observ", rs.getString("observ") != null ? rs
						.getString("observ") : "");
				hVac.put("est_id", rs.getString("est_id") != null ? rs
						.getString("est_id") : "");
				hVac.put("trabajador",((rs.getString("t02ap_pate") != null ? rs
										.getString("t02ap_pate").trim() : "")).concat( " "
										).concat( (rs.getString("t02ap_mate") != null ? rs
												.getString("t02ap_mate").trim()
												: "") ).concat( ", " ).concat( (rs
										.getString("t02nombres") != null ? rs
										.getString("t02nombres").trim() : "")));

				lista.add(hVac);
			}

 
		} catch (Exception e) {
			log.error("**** SQL ERROR **** "+ e.toString());
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
		return lista;
	}

	/**
	 * Metodo encargado de realizar la busqueda del numero de registro del
	 * personal que se no encuentre de vacaciones en la fecha indicada.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param fecha
	 *            String. Fecha para la cual se busca los registros de
	 *            trabajadores.
	 * @return ArrayList conteniendo los numeros de registro de los trabajadores
	 *         con vacacion.
	 * @throws SQLException
	 */

	public ArrayList joinWithT02ByFecha(String dbpool, String fecha,
			HashMap seguridad) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList personal = null;

		try {

			fecha = Utiles.toYYYYMMDD(fecha);

			strSQL.append("select 	t02cod_pers from t02perdp "
					).append( "where  	t02cod_stat = ? and  "
					).append( "        t02cod_pers not in "
					).append( "        ( "
					).append( "                select 	cod_pers from t1282Vacaciones_d  "
					).append( "                where 	(licencia = ? or licencia = ?) and  "
					).append( "                (ffinicio <= DATE('" ).append( fecha
					).append( "')) and  " ).append( "                (ffin >= DATE('" ).append( fecha
					).append( "')) " ).append( "        )  ");

			//criterios de visibilidad
			if (seguridad != null) {

				HashMap roles = (HashMap) seguridad.get("roles");
				String uoSeg = (String) seguridad.get("uoSeg");
				String uoAO = (String) seguridad.get("uoAO");//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO


				if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
					strSQL.append(" and 1=1 ");
				} else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null) {
					strSQL.append(" and ((substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) like '"
							).append( uoSeg ).append( "') ");
					
					strSQL.append(" or (substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) in ")
					.append(" (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= ' ")
					.append( uoAO ).append( "'))) ");
					
					
				} else {
					strSQL.append(" and 1=2 ");
				}
			}

			strSQL.append("order	by t02cod_pers ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, Constantes.ACTIVO);
			pre.setString(2, Constantes.VACACION);
			pre.setString(3, Constantes.VACACION_ESPECIAL);
			rs = pre.executeQuery();
			personal = new ArrayList();

			while (rs.next()) {
				personal.add(rs.getString("t02cod_pers"));
			}

 
		}

		catch (Exception e) {
			log.error("**** SQL ERROR **** "+ e.toString());
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

}