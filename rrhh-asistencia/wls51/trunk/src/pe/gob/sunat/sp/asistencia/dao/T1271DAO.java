package pe.gob.sunat.sp.asistencia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import pe.gob.sunat.sol.BeanFechaHora;
import pe.gob.sunat.sol.dao.DAOAccesoBD;
import pe.gob.sunat.sp.asistencia.bean.BeanAsistencia;
import pe.gob.sunat.sp.asistencia.bean.BeanProceso;
import pe.gob.sunat.sp.dao.T02DAO;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;

/**
 * <p>
 * Title: T1271DAO
 * </p>
 * <p>
 * Description: Clase encargada de administrar las consultas a la tabla
 * T1271Asistencia
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

public class T1271DAO extends DAOAccesoBD {

	private static final Logger log = Logger.getLogger(T1271DAO.class);

	public T1271DAO() {
	}

	/**
	 * Meotodo que se encarga de buscar los registros de asistencia de un
	 * determinado trabajador, para una fecha y periodos especificos.
	 * 
	 * @throws SQLException
	 */
	public ArrayList findByCodPersFechaPeriodo(String dbpool, String codPers,
			String fecha, String periodo) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList detalle = null;

		try {

			strSQL.append("select mov, fing, fsal, hing, hsal, "
					).append( " estado_id, periodo " ).append( " from  t1271asistencia "
					).append( " where cod_pers = ? and fing = ? ");


			con = getConnection(dbpool);
			//con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setDate(2, new BeanFechaHora(fecha).getSQLDate());
			rs = pre.executeQuery();
			detalle = new ArrayList();
			BeanProceso det = null;

			while (rs.next()) {

				det = new BeanProceso();

				det.setMovimiento(rs.getString("mov"));
				det.setFecha(new BeanFechaHora(rs.getDate("fing")).getFormatDate("dd/MM/yyyy"));
				det.setFechaIni(new BeanFechaHora(rs.getDate("fing")).getFormatDate("dd/MM/yyyy"));
				
				det.setFechaFin(new BeanFechaHora(rs.getDate("fsal")).getFormatDate("dd/MM/yyyy"));
				det.setHoraIni(rs.getString("hing"));
				det.setHoraFin(rs.getString("hsal"));
				det.setEstado(rs.getString("estado_id"));
				det.setPeriodo(rs.getString("periodo"));

				detalle.add(det);
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
		return detalle;
	}

	/**
	 * Metodo que se encarga de buscar los codigos de los trabajadores y sus
	 * respectivas areas organizacionales a partir del registro de asistencia de
	 * una determinada fecha.
	 * 
	 * @throws SQLException
	 */
	public ArrayList findPersonalByFecha(String dbpool, String fecha,
			HashMap seguridad) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList detalle = null;

		try {

			strSQL.append("select distinct cod_pers, u_organ "
					).append( "from t1271asistencia " ).append( "where fing = ? ");

			//criterios de visibilidad
			if (seguridad != null) {

				String codPers = (String) seguridad.get("codPers");

				strSQL.append(" and u_organ in ");
				strSQL.append(" (select u_organ from t1485seg_uorga "
						).append( " where cod_pers = '" ).append( codPers
						).append( "' and operacion = '"
						).append( Constantes.PROCESO_CALIFICACION ).append( "') ");
			}

			strSQL.append(" order by cod_pers");

			con = getConnection(dbpool);
			con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			pre = con.prepareStatement(strSQL.toString());
			pre.setDate(1, new BeanFechaHora(fecha).getSQLDate());
			rs = pre.executeQuery();
			detalle = new ArrayList();

			HashMap det = null;
			while (rs.next()) {

				det = new HashMap();

				det.put("codigo", rs.getString("cod_pers"));
				det.put("uo", rs.getString("u_organ"));

				detalle.add(det);
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
		return detalle;
	}

	/**
	 * Metodo que se encarga de buscar los codigos de los trabajadores y sus
	 * respectivas areas organizacionales a partir del registro de asistencia de
	 * un periodo y filtrados por un criterio con un valor determinados.
	 * 
	 * @throws SQLException
	 */
	public ArrayList findPersonalAsistencia(String dbpool, String criterio,
			String valor, String periodo, HashMap seguridad)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList detalle = null;

		try {

			strSQL.append("select t02cod_pers, substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) cod_uorga "
					).append( " from t02perdp " ).append( " where t02cod_stat = ? ");

			if (criterio.equals("0")) {
				strSQL.append(" and t02cod_pers = '" ).append( valor.trim().toUpperCase()
						).append( "'");
			}

			if (criterio.equals("1")) {
				strSQL.append(" and substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) = '"
						).append( valor.trim().toUpperCase() ).append( "'");
			}

			//criterios de visibilidad
			if (seguridad != null) {
				String codPers = (String) seguridad.get("codPers");
				strSQL.append(" and substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) in ");
				strSQL.append(" (select u_organ from t1485seg_uorga "
						).append( " where cod_pers = '" ).append( codPers
						).append( "' and operacion = '" ).append( Constantes.PROCESO_ASISTENCIA
						).append( "') ");
			}

			strSQL.append(" order by t02cod_pers");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, Constantes.ACTIVO);
			rs = pre.executeQuery();
			detalle = new ArrayList();
			HashMap det = null;

			while (rs.next()) {

				det = new HashMap();

				det.put("codigo", rs.getString("t02cod_pers"));
				det.put("uo", rs.getString("cod_uorga"));

				detalle.add(det);
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
		return detalle;
	}

	/**
	 * Metodo que se encarga de buscar los registros de asistencia de un
	 * trabajador en un periodo determinado.
	 * @throws SQLException
	 */
	public ArrayList findByCodPersPeriodo(String dbpool, String codPers,
			String periodo) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList detalle = null;

		try {

			strSQL.append("select 	   t.cod_pers, t.mov, t.fing, t.hing, t.hsal,  "
					).append( "        t.autor_id, t.estado_id "
					).append( "from 	t1271asistencia t "
					).append( "where 	t.cod_pers = ? and t.periodo = ? "
					).append( "order 	by t.fing ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, periodo);
			rs = pre.executeQuery();
			detalle = new ArrayList();
			BeanProceso det = null;

			while (rs.next()) {

				det = new BeanProceso();

				det.setCodigo(rs.getString("cod_pers"));
				det.setMovimiento(rs.getString("mov"));
				det.setFecha(new BeanFechaHora(rs.getDate("fing")).getFormatDate("dd/MM/yyyy"));
				det.setHoraIni(rs.getString("hing"));
				det.setHoraFin(rs.getString("hsal"));
				det.setAutorId(rs.getString("autor_id"));
				det.setEstado(rs.getString("estado_id"));

				detalle.add(det);
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
		return detalle;
	}

	/**
	 * Metodo que realiza la modifica el codigo de movimiento de los registros
	 * de asistencia de un trabajador, para un periodo, fecha y hora
	 * determinados.
	 * @throws SQLException
	 */
	public boolean updateByCodPersFIniHIni(String dbpool, String codPers,
			String periodo, String fechaIni, String horaIni, String mov)
			throws SQLException {

		StringBuffer strUpd = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		boolean result = false;

		try {

			strUpd.append("update t1271asistencia " ).append( "set mov = ? "
					).append( "where cod_pers = ? and periodo = ? "
					).append( "      and fing = ? and hing = ? ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strUpd.toString());
			pre.setString(1, mov);
			pre.setString(2, codPers);
			pre.setString(3, periodo);
			pre.setDate(4, new BeanFechaHora(fechaIni).getSQLDate());
			pre.setString(5, horaIni);
			int res = pre.executeUpdate();

			result = (res > 0);
 

		}

		catch (Exception e) {
			log.error("**** SQL ERROR **** "+ e.toString());
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
	 * Metodo que se encarga de modificar el codigo de movimiento y el estado de
	 * los registros de asistencia de un trabajador, para un periodo, fecha y
	 * hora determinados.
	 * @throws SQLException
	 */
	public boolean updatePapeletaByFecha(String dbpool, String codPers,
			String periodo, String fechaIni, String horaIni, String mov,
			String estado) throws SQLException {

		StringBuffer strUpd = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		boolean result = false;

		try {

			strUpd.append("update t1271asistencia " ).append( "set mov = ?, "
					).append( "    estado_id = ? "
					).append( "where cod_pers = ? and periodo = ? "
					).append( "      and fing = ? and hing = ? ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strUpd.toString());
			pre.setString(1, mov);
			pre.setString(2, estado);
			pre.setString(3, codPers);
			pre.setString(4, periodo);
			pre.setDate(5, new BeanFechaHora(fechaIni).getSQLDate());
			pre.setString(6, horaIni);
			int res = pre.executeUpdate();

			result = (res > 0);

 

		}

		catch (Exception e) {
			log.error("**** SQL ERROR **** "+ e.toString());
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
	 * Metodo que se encarga de eliminar los registros de asistencia de un
	 * trabajador para un periodo y fecha determinados.
	 * @throws SQLException
	 */
	public void deleteByCodPersPeriodo(String dbpool, String codPers,
			String periodo, String fechaIni) throws SQLException {

		StringBuffer strUpd = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;

		try {

			strUpd.append("delete from t1271asistencia ").append( "where cod_pers = ? and periodo = ? and fing = ? ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strUpd.toString());
			pre.setString(1, codPers);
			pre.setString(2, periodo);
			pre.setDate(3, new BeanFechaHora(fechaIni).getSQLDate());
			pre.executeUpdate();
 

		} catch (Exception e) {
			log.error("**** SQL ERROR **** "+ e.toString());
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
	 * Metodo que se encarga de buscar los registros de asistencia de un
	 * trabajador y fechas determinados.
	 * @throws SQLException
	 */
	public ArrayList findByCodPersFecha(String dbpool, String codPers,
			String fecha, HashMap seguridad) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList detalle = null;

		try {

			strSQL.append("select a.cod_pers, a.periodo, a.u_organ, a.mov, a.fing, a.hing, "
					).append( "      a.fsal, a.hsal, a.autor_id, a.jefe_autor, a.fecha_autor, a.estado_id, m.descrip, a.obs_papeleta "
					).append( " from  t1271asistencia a, t1279tipo_mov m "
					).append( " where a.cod_pers = ? and a.fing = ? and a.mov = m.mov ");

			//criterios de visibilidad
			if (seguridad != null) {
				HashMap roles = (HashMap) seguridad.get("roles");
				String uoSeg = (String) seguridad.get("uoSeg");
				String uoAO = (String) seguridad.get("uoAO");//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

				if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
					strSQL.append(" and 1=1 ");
				} else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null) {
					strSQL.append(" and ((a.u_organ like '" ).append( uoSeg ).append( "') ");
					
					strSQL.append(" or (a.u_organ in (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '" ).append( uoAO ).append( "'))) ");
				}  else if (//roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null ||
						roles.get(Constantes.ROL_JEFE) != null) {
					strSQL.append(" and a.u_organ like '" ).append( uoSeg ).append( "' ");
				} else {
					strSQL.append(" and 1=2 ");
				}
			}
			//else{
			//	strSQL.append(" and a.mov in (?,?,?,?,?,?) ");				
			//}

			strSQL.append(" order by a.hing");

			log.debug(strSQL.toString());
			
			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setDate(2, new BeanFechaHora(fecha).getSQLDate());
			
			//if (seguridad == null) {
				//se agrego esta linea para la validacion del registro de papeletas 04/01/2006
			//pre.setString(3,Constantes.MOV_TARDANZA_DESCUENTO);
			//	pre.setString(4,Constantes.MOV_TARDANZA_SIN_DESCUENTO);
			//	pre.setString(5,Constantes.MOV_SALIDA_NO_AUTORIZADA);
			//	pre.setString(6,Constantes.MOV_OMISION_MARCA);
			//  pre.setString(7,Constantes.MOV_INASISTENCIA);
			//	pre.setString(8,Constantes.MOV_PAPELETA_NO_AUTORIZADA);
			//}
			
			rs = pre.executeQuery();
			detalle = new ArrayList();
			BeanAsistencia asis = null;

			while (rs.next()) {
				asis = new BeanAsistencia();
				asis.setCodPers(rs.getString("cod_pers"));
				asis.setPeriodo(rs.getString("periodo"));
				asis.setUOrgan(rs.getString("u_organ"));
				asis.setMov(rs.getString("mov"));
				asis.setFIng(new BeanFechaHora(rs.getDate("fing")).getFormatDate("dd/MM/yyyy"));
				if (rs.getDate("fsal")==null) {
					asis.setFSal("");
				}else{
					asis.setFSal(new BeanFechaHora(rs.getDate("fsal")).getFormatDate("dd/MM/yyyy"));
				}
				asis.setHIng(rs.getString("hing"));
				asis.setHSal(rs.getString("hsal"));
				asis.setAutorId(rs.getString("autor_id"));
				asis.setJefeAutor(rs.getString("jefe_autor"));
				asis.setFechaAutor(rs.getTimestamp("fecha_autor"));
				asis.setEstadoId(rs.getString("estado_id"));
				asis.setDescMov(rs.getString("descrip"));
				asis.setDescObserv(rs.getString("obs_papeleta"));
				detalle.add(asis);
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
		return detalle;
	}

	/**
	 * Metodo que se encarga de buscar los registros de asistencia que se
	 * encuentren dentro de un determinado rango de fechas.
	 * @throws SQLException
	 */
	public ArrayList findByFIngFFinNull(String dbpool, String fechaIni,
			String fechaFin, String criterio, String valor) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList detalle = null;

		try {

			strSQL.append("select cod_pers, u_organ, fing "
					).append( " from t1271asistencia "
					).append( " where (fsal IS NULL and hsal IS NULL) and"
					).append( " fing >= DATE('").append( Utiles.toYYYYMMDD(fechaIni)).append( "') and"
					).append( " fing <= DATE('").append( Utiles.toYYYYMMDD(fechaFin)).append( "') ");

			if (criterio.equals("0")) {
				strSQL.append(" and cod_pers = '" ).append( valor.trim().toUpperCase()).append( "' ");
			}
			if (criterio.equals("1")) {
				strSQL.append(" and u_organ = '" ).append( valor.trim().toUpperCase()).append( "' ");
			}

			//JRR	
			if (criterio.equals("2")) {
				String intendencia = valor.substring(0,2);
				strSQL.append(" and u_organ like '" ).append( intendencia.trim().toUpperCase()).append( "%' ");
			}
			//
			
			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			rs = pre.executeQuery();
			detalle = new ArrayList();
			BeanAsistencia asis = null;

			while (rs.next()) {

				asis = new BeanAsistencia();
				asis.setCodPers(rs.getString("cod_pers"));
				asis.setFIng(new BeanFechaHora(rs.getDate("fing")).getFormatDate("dd/MM/yyyy"));

				detalle.add(asis);
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
		return detalle;
	}

	/**
	 * Meotodo que se encarga de eliminar los registros asistencia de un
	 * trabajador para una fecha especifica.
	 * @throws SQLException
	 */
	public void deleteByCodPersFecha(String dbpool, String codPers, String fecha)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;

		try {

			strSQL.append("delete from t1271asistencia"
					).append( " where cod_pers = ? and fing = ?");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setDate(2, new BeanFechaHora(fecha).getSQLDate());
			pre.executeUpdate();

 
		}

		catch (Exception e) {
			log.error("**** SQL ERROR **** "+ e.toString());
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
	 * Metodo que se encarga de determinar si existen registros de asistencia
	 * para un trabajador en una fecha determinada.
	 * @throws SQLException
	 */
	public boolean findByCodPersFFinNull(String dbpool, String codPers,
			String fecha) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		boolean res = false;

		try {

			strSQL.append("select cod_pers"
					).append( " from t1271asistencia"
					).append( " where  cod_pers = ? and "
					).append( " fing = DATE('").append( Utiles.toYYYYMMDD(fecha) ).append( "') and "
					).append( " (fsal IS NULL and hsal IS NULL) ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			rs = pre.executeQuery();

			if (rs.next()) {
				res = true;
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
		return res;
	}

	/**
	 * Metodo que se encarga de buscar los datos del registro de asistencia y de
	 * personal filtrados por registro de personal, unidad organizacional y
	 * rango fechas de asistencia. Join con la tabla T02Perdp
	 * @throws SQLException
	 */
	public ArrayList joinWithT02findByCodPersUOrgFecha(String dbpool,
			String codPers, String codUOrg, String fechaIni, String fechaFin,
			HashMap seguridad) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList detalle = null;

		String fIni = null;
		String fFin = null;

		try {

			T02DAO pDAO = new T02DAO();

			strSQL.append("select 	  a.cod_pers, a.periodo, a.u_organ, a.mov,"
					).append( "       a.fing, a.hing, a.fsal, a.hsal, a.autor_id,"
					).append( "       a.jefe_autor, a.fecha_autor, a.estado_id, a.obs_papeleta "
					).append( " from  t1271asistencia a "
					).append( " where a.cod_pers != ? and "	
					).append( "       a.periodo > '000000' and " 
					).append( "       fing >= DATE(?) and "
					).append( "       a.hing >= '00:00:00' and "
					).append( "       fsal <= DATE(?) and "
					).append( "       estado_id IS NOT NULL ");

			//criterios de visibilidad
			if (seguridad != null) {

				HashMap roles = (HashMap) seguridad.get("roles");
				String uoSeg = (String) seguridad.get("uoSeg");
				String uoAO = (String) seguridad.get("uoAO");//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

				if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
					strSQL.append(" and 1=1 ");
				} else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null) {
					strSQL.append(" and (a.u_organ like '" ).append( uoSeg ).append( "' ");
					
					strSQL.append(" or (a.u_organ in (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '" ).append( uoAO ).append( "'))) ");
				} else if (//roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null ||
						 roles.get(Constantes.ROL_JEFE) != null) {
					strSQL.append(" and a.u_organ like '" ).append( uoSeg ).append( "' ");
				} else {
					strSQL.append(" and 1=2 ");
				}
			}

			strSQL.append(" order by fing, hing");

			if (fechaIni.trim().equals("")) {
				fIni = Utiles.toYYYYMMDD(Utiles.obtenerFechaActual());
			} else {
				fIni = Utiles.toYYYYMMDD(fechaIni);
			}

			if (fechaFin.trim().equals("")) {
				fFin = fIni;
			} else {
				fFin = Utiles.toYYYYMMDD(fechaFin);
			}

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers.toUpperCase());
			pre.setString(2, fIni);
			pre.setString(3, fFin);
			rs = pre.executeQuery();
			detalle = new ArrayList();
			BeanAsistencia asis = null;

			while (rs.next()) {

				asis = new BeanAsistencia();

				asis.setCodPers(rs.getString("cod_pers"));
				asis.setTrabajador(pDAO.findNombreCompletoByCodPers(dbpool,
						asis.getCodPers()));
				asis.setPeriodo(rs.getString("periodo"));
				asis.setUOrgan(rs.getString("u_organ"));
				asis.setMov(rs.getString("mov"));
				asis.setFIng(new BeanFechaHora(rs.getDate("fing")).getFormatDate("dd/MM/yyyy"));
				asis.setFSal(new BeanFechaHora(rs.getDate("fsal")).getFormatDate("dd/MM/yyyy"));
				asis.setHIng(rs.getString("hing"));
				asis.setHSal(rs.getString("hsal"));
				asis.setAutorId(rs.getString("autor_id"));
				asis.setJefeAutor(rs.getString("jefe_autor"));
				asis.setFechaAutor(rs.getTimestamp("fecha_autor"));
				asis.setEstadoId(rs.getString("estado_id"));
				asis.setDescObserv(rs.getString("obs_papeleta"));

				detalle.add(asis);
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
		return detalle;
	}
	
	/**
	 * Metodo que se encarga de buscar los datos del registro de asistencia y de
	 * personal filtrados por registro de personal, unidad organizacional y
	 * rango fechas de asistencia. Join con la tabla T02Perdp
	 * @throws SQLException
	 */
	public ArrayList findPapeletas(String dbpool, String codPers, HashMap seguridad) 
		throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList detalle = null;

		try {

			T02DAO pDAO = new T02DAO();

			strSQL.append("select 	  a.cod_pers, a.periodo, a.u_organ, a.mov,"
					).append( "       a.fing, a.hing, a.fsal, a.hsal, a.autor_id,"
					).append( "       a.jefe_autor, a.fecha_autor, a.estado_id, a.obs_papeleta "
					).append( " from  t1271asistencia a "
					).append( " where a.u_organ like ? and "							
//					).append( " where a.u_organ = ? and "
					).append( "       a.periodo > '000000' and "
					).append( "       a.estado_id = ? and "
					).append( "       a.cod_pers != ? and "
					).append( "       a.hing >= '00:00:00' "
					).append( " and   a.jefe_autor = ? "
//					).append( "       estado_id IS NOT NULL "
					).append( " order by a.fing, a.cod_pers");
					//).append( " order by fing, hing");
			

			log.debug("strSQL : "+strSQL.toString());			
			String uoSeg = (String) seguridad.get("uoSeg");
			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			//pre.setString(1, (String)seguridad.get("codUO"));
			pre.setString(1, uoSeg);
			pre.setString(2, Constantes.PAPELETA_REGISTRADA);
			pre.setString(3, codPers.toUpperCase());
			pre.setString(4, codPers.toUpperCase());
			rs = pre.executeQuery();
			detalle = new ArrayList();
			BeanAsistencia asis = null;

			while (rs.next()) {

				asis = new BeanAsistencia();

				asis.setCodPers(rs.getString("cod_pers"));
				asis.setTrabajador(pDAO.findNombreCompletoByCodPers(dbpool, asis.getCodPers()));
				asis.setPeriodo(rs.getString("periodo"));
				asis.setUOrgan(rs.getString("u_organ"));
				asis.setMov(rs.getString("mov"));
				asis.setFIng(new BeanFechaHora(rs.getDate("fing")).getFormatDate("dd/MM/yyyy"));
				asis.setFSal(new BeanFechaHora(rs.getDate("fsal")).getFormatDate("dd/MM/yyyy"));
				asis.setHIng(rs.getString("hing"));
				asis.setHSal(rs.getString("hsal"));
				asis.setAutorId(rs.getString("autor_id"));
				asis.setJefeAutor(rs.getString("jefe_autor"));
				asis.setFechaAutor(rs.getTimestamp("fecha_autor"));
				asis.setEstadoId(rs.getString("estado_id"));
				asis.setDescObserv(rs.getString("obs_papeleta"));
				
				detalle.add(asis);
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
		return detalle;
	}	

	/**
	 * Metodo que se encarga de buscar los datos del registro de asistencia y de
	 * personal filtrados por registro de personal, unidad organizacional y
	 * rango fechas de asistencia. Join con la tabla T02Perdp
	 * @throws SQLException
	 */
	public ArrayList findPapeletasSubordinados(String dbpool, String codPers, String fechaIni, String fechaFin,
			HashMap seguridad) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList detalle = null;

		String fIni = null;
		String fFin = null;

		try {

			T02DAO pDAO = new T02DAO();

			strSQL.append("select 	  a.cod_pers, a.periodo, a.u_organ, a.mov,"
					).append( "       a.fing, a.hing, a.fsal, a.hsal, a.autor_id,"
					).append( "       a.jefe_autor, a.fecha_autor, a.estado_id, a.obs_papeleta "
					).append( " from  t1271asistencia a "
					).append( " where a.cod_pers != ? and "	
					).append( "       a.periodo > '000000' and " 
					).append( "       fing >= DATE(?) and "
					).append( "       a.hing >= '00:00:00' and "
					).append( "       fsal <= DATE(?) and "
					).append( "       estado_id IS NOT NULL ");

			//criterios de visibilidad
			if (seguridad != null) {

				HashMap roles = (HashMap) seguridad.get("roles");
//				String uoSeg = (String) seguridad.get("uoSeg");
				String cmbEstado = (String) seguridad.get("cmbEstado");
				String cmbCriterio = (String) seguridad.get("cmbCriterio");
				String txtValor = (String) seguridad.get("txtValor");

				if (roles.get(Constantes.ROL_JEFE) != null) {
					
					//strSQL.append(" and a.u_organ like '" ).append( uoSeg ).append( "' ");
					strSQL.append(" and a.jefe_autor = '" ).append(codPers.toUpperCase()).append( "' ");
					
					if (!cmbEstado.equals("-1")){
						strSQL.append(" and a.estado_id = '" ).append( cmbEstado ).append( "' ");
					}					
					if (cmbCriterio.equals("0") && !txtValor.trim().equals("")){
						strSQL.append(" and a.cod_pers like '" ).append( txtValor.toUpperCase().trim() ).append( "%' ");
					}
					else if (cmbCriterio.equals("1") && !txtValor.trim().equals("")){
						strSQL.append(" and a.u_organ like '" ).append( txtValor.toUpperCase().trim() ).append( "%' ");
					}
				} else {
					strSQL.append(" and 1=2 ");
				}
			}
			strSQL.append(" order by fing, hing");

			if (fechaIni.trim().equals("")) {
				fIni = Utiles.toYYYYMMDD(Utiles.obtenerFechaActual());
			} else {
				fIni = Utiles.toYYYYMMDD(fechaIni);
			}

			if (fechaFin.trim().equals("")) {
				fFin = fIni;
			} else {
				fFin = Utiles.toYYYYMMDD(fechaFin);
			}
			
			log.debug("strSQL : "+strSQL);

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers.toUpperCase());
			pre.setString(2, fIni);
			pre.setString(3, fFin);
			rs = pre.executeQuery();
			detalle = new ArrayList();
			BeanAsistencia asis = null;

			while (rs.next()) {

				asis = new BeanAsistencia();

				asis.setCodPers(rs.getString("cod_pers"));
				asis.setTrabajador(pDAO.findNombreCompletoByCodPers(dbpool, asis.getCodPers()));
				asis.setPeriodo(rs.getString("periodo"));
				asis.setUOrgan(rs.getString("u_organ"));
				asis.setMov(rs.getString("mov"));
				asis.setFIng(new BeanFechaHora(rs.getDate("fing")).getFormatDate("dd/MM/yyyy"));
				asis.setFSal(new BeanFechaHora(rs.getDate("fsal")).getFormatDate("dd/MM/yyyy"));
				asis.setHIng(rs.getString("hing"));
				asis.setHSal(rs.getString("hsal"));
				asis.setAutorId(rs.getString("autor_id"));
				asis.setJefeAutor(rs.getString("jefe_autor"));
				asis.setFechaAutor(rs.getTimestamp("fecha_autor"));
				asis.setEstadoId(rs.getString("estado_id"));
				asis.setDescObserv(rs.getString("obs_papeleta"));

				detalle.add(asis);
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
		return detalle;
	}	
	
	/**
	 * 
	 * @param dbpool
	 * @param codPers
	 * @param fecha
	 * @param hora
	 * @return @throws
	 *         SQLException
	 */
	public boolean findMarcacionCalificada(String dbpool, String codPers,
			String fecha, String hora) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		boolean res = false;

		try {

			strSQL.append("select cod_pers "
					).append( " from t1271asistencia"
					).append( " where cod_pers = ? and "					
					).append( " ((fing = DATE('").append( Utiles.toYYYYMMDD(fecha)).append( "') and hing = ? ) or "
					).append( "  (fsal = DATE('").append( Utiles.toYYYYMMDD(fecha)).append( "') and hsal = ? )) and "
					).append( " estado_id != ? ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);			
			pre.setString(2, hora);
			pre.setString(3, hora);
			pre.setString(4, Constantes.PRE_CALIFICACION_ASIS);
			rs = pre.executeQuery();

			if (rs.next()) {
				res = true;
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
		return res;
	}
	
	
	/**
	 * Metodo que inserta registro de Asistencia
	 * @throws SQLException
	 */
	public boolean insertRegistroAsistencia(String dbpool, 
			String codPers,String periodo,String codUO,String fechaMarcacion,String horaMarcacion,
			String fechaFinMov,String horaFinMov,String calificacion,String tMov,
			String relojIni,String relojFin,String usuario)
			throws SQLException {

		String strUpd = "";
		PreparedStatement pre = null;
		Connection con = null;
		boolean result = false;

		try {
			


			strUpd = "Insert into t1271asistencia (" + 
			"cod_pers, periodo, u_organ, mov, fing, "+
			"hing, fsal, hsal, reloj_ing,"+
			"reloj_sal, autor_id, jefe_autor, fecha_autor," +
            "estado_id,fcreacion,cuser_crea,fmod,cuser_mod) " + 
            "Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			con = getConnection(dbpool);
			pre = con.prepareStatement(strUpd);
			pre.setString(1, codPers );
			pre.setString(2, periodo);
			pre.setString(3, codUO);
			pre.setString(4, tMov);
			pre.setDate(5, new BeanFechaHora(fechaMarcacion).getSQLDate());
			pre.setString(6,horaMarcacion);
			pre.setDate(7, new BeanFechaHora(fechaFinMov).getSQLDate());
			pre.setString(8, horaFinMov);
			pre.setString(9, relojIni);
			pre.setString(10, relojFin);
			pre.setString(11, "");
			pre.setString(12, "");
			pre.setString(13,null );
			pre.setString(14,calificacion );
			pre.setTimestamp(15, new java.sql.Timestamp(System.currentTimeMillis()));
			pre.setString(16, usuario);
			pre.setString(17, null);
			pre.setString(18, null);
			int res = pre.executeUpdate();

			result = (res > 0);
			
 

		}

		catch (Exception e) {
			//log.error("**** SQL ERROR **** Se ha encontrado una doble marcación para REGISTRO:" + codPers + ", FECHA:"+ fechaMarcacion + " "+ e.toString());
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
	 * Metodo que Actualiza registro de Asistencia
	 * @throws SQLException
	 */
	public boolean updateRegistroAsistencia(String dbpool,String tMov,String fechaFinMov,
			String horaFinMov,String usuario,String codPers,String periodo,
			String fechaMarcacion,String horaMarcacion,String calificacion)
			throws SQLException {

		String strUpd = "";
		PreparedStatement pre = null;
		Connection con = null;
		boolean result = false;

		try {


			
			strUpd = "Update t1271asistencia Set mov = ?, fsal = ?, hsal = ?, fmod = ?, cuser_mod = ? " + 
			"Where cod_pers = ? And periodo = ? And fing = ? And hing = ? And estado_id = ?";
			
			con = getConnection(dbpool);
			pre = con.prepareStatement(strUpd);
			pre.setString(1, tMov);
			pre.setDate(2, new BeanFechaHora(fechaFinMov).getSQLDate());
			pre.setString(3, horaFinMov);
			pre.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
			pre.setString(5, usuario);
			pre.setString(6, codPers );
			pre.setString(7, periodo);
			pre.setDate(8, new BeanFechaHora(fechaMarcacion).getSQLDate());
			pre.setString(9,horaMarcacion);
			pre.setString(10,calificacion );
			int res = pre.executeUpdate();
			
			result = (res > 0);
 		}

		catch (Exception e) {
			log.error("**** SQL ERROR **** "+ e.toString());
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
	
	
	
	
	
	
	
	
	
	
	
	
	
}