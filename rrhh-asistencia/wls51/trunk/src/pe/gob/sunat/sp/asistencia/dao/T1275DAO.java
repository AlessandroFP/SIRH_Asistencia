package pe.gob.sunat.sp.asistencia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import pe.gob.sunat.sol.BeanFechaHora;
import pe.gob.sunat.sol.dao.DAOAccesoBD;
import pe.gob.sunat.sp.asistencia.bean.BeanMarcacion;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;

/**
 * Title: T1275DAO
 * Description: Clase encargada de administrar las consultas a la tabla
 * T1275Marcacion
 * Copyright: Copyright (c) 2004
 * Company: Sunat
 * @author cgarratt
 * @version 1.0
 */

public class T1275DAO extends DAOAccesoBD {

	private static final Logger log = Logger.getLogger(T1275DAO.class);

	public T1275DAO() {
	}

	/**
	 * Metodo encargado de buscar las registrs de marcaciones impares para un
	 * rango de fechas, filtradas por un criterio con un valor determinado. Join
	 * con la tabla T02Perdp
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param fechaIni
	 *            String. Fecha inferior del rango de busqueda.
	 * @param fechaFin
	 *            String. Fecha superior del rango de busqueda.
	 * @param criterio
	 *            String. Criterio de filtro de los registros.
	 * @param valor
	 *            String. Valor de filtro para el criterio dado.
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanMarcacion.
	 * @throws SQLException
	 */
	public ArrayList findImpares(String dbpool, String fechaIni,
			String fechaFin, String criterio, String valor, HashMap seguridad)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList marcaciones = null;

		try {

			strSQL.append(" select p.t02cod_pers, p.t02ap_pate, "
					).append( "p.t02ap_mate, p.t02nombres, "
					).append( "m.fecha, count(*) as num_marcas "
					).append( "from t1275marcacion m, t02perdp p "
					).append( "where m.sdel = ? ");

			if (!fechaIni.equals("")) {
				strSQL.append(" and m.fecha >= DATE('").append( Utiles.toYYYYMMDD(fechaIni) ).append( "') ");
			}
			if (!fechaFin.equals("")) {
				strSQL.append(" and m.fecha <= DATE('").append( Utiles.toYYYYMMDD(fechaFin) ).append( "') ");
			}

			//busqueda por registro de trabajador
			if (criterio.equals("0")) {
				strSQL.append(" and p.t02cod_pers like '"
						).append( valor.trim().toUpperCase() ).append( "%' ");
			}

			//busqueda por unidad organizacional
			if (criterio.equals("1")) {
				strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '"
						).append( valor.trim().toUpperCase() ).append( "%' ");
			}

			//busqueda por categoria
			if (criterio.equals("2")) {
				strSQL.append(" and substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,6) like '"
						).append( valor.trim().toUpperCase() ).append( "%' ");
			}

			//criterios de visibilidad
			if (seguridad != null) {

				HashMap roles = (HashMap) seguridad.get("roles");
				String uoSeg = (String) seguridad.get("uoSeg");
				String uoAO = (String) seguridad.get("uoAO");//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO


				if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
					strSQL.append(" and 1=1 ");
				} else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null) {
					strSQL.append(" and ((substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '"
							).append( uoSeg.toUpperCase() ).append( "') ");
					
					strSQL.append(" or (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) in ")
							.append(" (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '")
					.append( uoAO.toUpperCase() ).append( "'))) ");
					
					
				} else if (//roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null ||
						 roles.get(Constantes.ROL_JEFE) != null) {
					strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '"
							).append( uoSeg.toUpperCase() ).append( "' ");
				} else {
					strSQL.append(" and 1=2 ");
				}
			}

			strSQL.append(" and m.cod_pers = p.t02cod_pers ");
			strSQL.append(" group by 1,2,3,4,5 ");
			strSQL.append(" order by 1 ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, Constantes.ACTIVO);

			log.debug(strSQL);
			log.debug("Inicio : " + new Timestamp(System.currentTimeMillis()));
			rs = pre.executeQuery();
			log.debug("Fin : " + new Timestamp(System.currentTimeMillis()));

			marcaciones = new ArrayList();

			BeanMarcacion marcacion = null;
			while (rs.next()) {

				int numMarcas = rs.getInt("num_marcas");
				//solo ingresan las marcaciones impares
				if (numMarcas % 2 == 1) {

					marcacion = new BeanMarcacion();

					marcacion.setCodPers(rs.getString("t02cod_pers"));
					marcacion.setAPaterno(rs.getString("t02ap_pate").trim());
					marcacion.setAMaterno(rs.getString("t02ap_mate").trim());
					marcacion.setNombres(rs.getString("t02nombres").trim());
					marcacion.setFecha(new BeanFechaHora(rs.getDate("fecha")).getFormatDate("dd/MM/yyyy"));
					marcacion.setNumMarcas(numMarcas);

					marcaciones.add(marcacion);
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
		return marcaciones;
	}

	/**
	 * M�todo que se encarga de buscar las marcas de reloj de una persona para
	 * una fecha espec�fica. Join con la tabla T1280Tipo_Reloj.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            N�mero de registro del trabajador.
	 * @param fecha
	 *            Fecha de b�squeda.
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanMarcacion.
	 * @throws SQLException
	 */

	public ArrayList joinWithT1280(String dbpool, String codPers, String fecha)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList marcaciones = null;

		try {

			strSQL.append("select  m.cod_pers, r.reloj, r.sede, r.descrip, m.fecha, m.hora "
					).append( "from 	t1275marcacion m,"
					).append( "        t1280tipo_reloj r "
					).append( "where   m.cod_pers = ? and  "
					).append( "        m.fecha = ? and m.sdel = ? and r.reloj = m.reloj");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setDate(2, new BeanFechaHora(fecha).getSQLDate());
			pre.setString(3, Constantes.ACTIVO);
			rs = pre.executeQuery();
			marcaciones = new ArrayList();
			BeanMarcacion marcacion = null;

			while (rs.next()) {

				marcacion = new BeanMarcacion();

				marcacion.setCodPers(rs.getString("cod_pers"));
				marcacion.setDescReloj(rs.getString("descrip"));
				marcacion.setReloj(rs.getString("reloj"));
				marcacion.setFecha(new BeanFechaHora(rs.getDate("fecha")).getFormatDate("dd/MM/yyyy"));
				marcacion.setSede(rs.getString("sede"));
				marcacion.setHora(rs.getString("hora"));

				marcaciones.add(marcacion);
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
		return marcaciones;
	}

	/**
	 * Metodo que se encarga de buscar las marcas de reloj de una persona para
	 * una fecha especifica. Join con la tabla T1280Tipo_Reloj.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            Numero de registro del trabajador.
	 * @param fecha
	 *            Fecha de busqueda.
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanMarcacion.
	 * @throws SQLException
	 */

	public ArrayList joinWithT1280T1597(String dbpool, String codPers,
			String fechaIni, String fechaFin, HashMap seguridad)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList marcaciones = null;

		try {

			strSQL.append(" select m.cod_pers, m.fecha, m.hora, r.reloj, r.sede, r.descrip "
					).append( " from   t1275marcacion m,"
					).append( "        t1280tipo_reloj r, "
					).append( "        t1597pase p "
					).append( " where  m.cod_pers = ?  ");

			if (!fechaIni.equals("")) {
				strSQL.append(" and m.fecha >= DATE('").append( Utiles.toYYYYMMDD(fechaIni) ).append( "') ");
			}
			if (!fechaFin.equals("")) {
				strSQL.append(" and m.fecha <= DATE('").append( Utiles.toYYYYMMDD(fechaFin) ).append( "') ");
			}

			strSQL.append(" 	   and m.sdel = ? and m.cod_pers = p.cod_pase").
				   append( "        and m.reloj = r.reloj ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers.trim().toUpperCase());
			pre.setString(2, Constantes.ACTIVO);

			rs = pre.executeQuery();

			marcaciones = new ArrayList();
			HashMap marcacion = null;

			while (rs.next()) {

				marcacion = new HashMap();

				marcacion.put("cod_pers", rs.getString("cod_pers"));
				marcacion.put("desc_reloj", rs.getString("descrip"));
				marcacion.put("reloj", rs.getString("reloj"));
				marcacion.put("fecha", new BeanFechaHora(rs.getDate("fecha")).getFormatDate("dd/MM/yyyy"));
				marcacion.put("sede", rs.getString("sede"));
				marcacion.put("hora", rs.getString("hora"));

				marcaciones.add(marcacion);
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
		return marcaciones;
	}

	/**
	 * Metodo que se encarga de buscar las marcas de reloj de una persona para
	 * una fecha especifica. Join con la tabla T1280Tipo_Reloj.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            Numero de registro del trabajador.
	 * @param fecha
	 *            Fecha de busqueda.
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanMarcacion.
	 * @throws SQLException
	 */

	public ArrayList joinWithT1280(String dbpool, String codPers, String fecha,
			HashMap seguridad) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList marcaciones = null;

		try {
			
			strSQL.append(" select m.cod_pers, m.fecha, m.hora, m.fcreacion, m.cuser_crea, m.fmod, m.cuser_mod" //jquispe
					).append( ", r.reloj, r.sede, r.descrip "
					).append( " from   t1275marcacion m,"
					).append( "        t1280tipo_reloj r "
					).append( " where  m.cod_pers = ? and  "
					).append( "        m.fecha = ? and "
					).append( "        m.sdel = ? and "
					).append( "        m.reloj = r.reloj ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers.trim().toUpperCase());
			pre.setDate(2, new BeanFechaHora(fecha.trim()).getSQLDate());
			pre.setString(3, Constantes.ACTIVO);
			rs = pre.executeQuery();

			marcaciones = new ArrayList();
			HashMap marcacion = null;

			while (rs.next()) {

				marcacion = new HashMap();

				marcacion.put("cod_pers", rs.getString("cod_pers"));
				marcacion.put("desc_reloj", rs.getString("descrip"));
				marcacion.put("reloj", rs.getString("reloj"));
				marcacion.put("fecha", new BeanFechaHora(rs.getDate("fecha")).getFormatDate("dd/MM/yyyy"));
				marcacion.put("sede", rs.getString("sede"));
				marcacion.put("hora", rs.getString("hora"));
				marcacion.put("fcreacion", rs.getString("fcreacion"));		//jquispe:ini
				marcacion.put("cuser_crea", rs.getString("cuser_crea"));
				marcacion.put("fmod", rs.getString("fmod"));
				marcacion.put("cuser_mod", rs.getString("cuser_mod"));		//jquispe:fin

				marcaciones.add(marcacion);
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
		return marcaciones;
	}

	/**
	 * Metodo encargado de la carga de pases provisionales
	 * 
	 * @param dbpool
	 * @param fecha
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList findPases(String dbpool, String fecha) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList pases = null;

		try {

			strSQL.append(" select cod_pase, sestado_pase, finivig, ffinvig, subica_pase "
					).append( " from   t1597pase "
					).append( " where  DATE(?) between finivig and ffinvig ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setDate(1, new BeanFechaHora(fecha).getSQLDate());
			rs = pre.executeQuery();
			pases = new ArrayList();
			HashMap pase = null;

			while (rs.next()) {

				pase = new HashMap();

				pase.put("cod_pase", rs.getString("cod_pase"));
				pase.put("ind_estado_id", rs.getString("sestado_pase"));
				pase.put("fecha_ini_vig", rs.getTimestamp("finivig"));
				pase.put("fecha_fin_vig", rs.getTimestamp("ffinvig"));

				pases.add(pase);
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
		return pases;
	}

	/**
	 * M�todo que se encarga de buscar las marcas de reloj de una persona para
	 * una fecha espec�fica.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            N�mero de registro del trabajador.
	 * @param FechaEval
	 *            Fecha de b�squeda.
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanMarcacion.
	 * @throws SQLException
	 */
	public ArrayList findByCodPersFecha(String dbpool, String codPers,
			String FechaEval) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList marcaciones = null;

		String fecI = Utiles.toYYYYMMDD(FechaEval);
		String fecF = Utiles
				.toYYYYMMDD(Utiles.dameFechaSiguiente(FechaEval, 1));

		try {

			strSQL.append("select cod_pers, fecha, hora, reloj "
					).append( " from t1275marcacion  "
					).append( " where cod_pers = ? and "
					).append( " fecha >= DATE('").append( fecI).append( "') and "
					).append( " fecha < DATE('").append( fecF ).append( "') and sdel = ? order by hora");

			log.debug(strSQL.toString());
			
			con = getConnection(dbpool);
			//con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, Constantes.ACTIVO);
			rs = pre.executeQuery();
			marcaciones = new ArrayList();
			BeanMarcacion marcacion = null;

			while (rs.next()) {

				marcacion = new BeanMarcacion();

				marcacion.setCodPers(rs.getString("cod_pers"));
				marcacion.setFecha(new BeanFechaHora(rs.getDate("fecha")).getFormatDate("dd/MM/yyyy"));				
				marcacion.setHora(rs.getString("hora"));
				marcacion.setReloj(rs.getString("reloj"));

				marcaciones.add(marcacion);

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
		return marcaciones;

	}
//WERR-PAS20155E230300139
	public BeanMarcacion findByCodPersFechaHoraAnula(String dbpool, String codPers,
			String fecha, String hora) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		BeanMarcacion marcacion = null;

		try {

			strSQL.append("select reloj "
					).append( " from t1275marcacion  "
					).append( " where cod_pers = ? and "
					).append( " fecha = DATE('").append( Utiles.toYYYYMMDD(fecha) ).append( "') and hora = '"
					).append( hora.trim() ).append( "' and sdel = ? ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, Constantes.ACTIVO);
			rs = pre.executeQuery();

			while (rs.next()) {
				marcacion = new BeanMarcacion();
				marcacion.setReloj(rs.getString("reloj"));
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
		return marcacion;

	}
//END WERR-PAS20155E230300139
	/**
	 * M�todo que se encarga de buscar las marcas de reloj de una persona para
	 * una fecha espec�fica.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            N�mero de registro del trabajador.
	 * @param fecha
	 *            Fecha de b�squeda.
	 * @param hora
	 *            Hora de b�squeda.
	 * @return Bean de la clase BeanMarcacion.
	 * @throws SQLException
	 */
	public BeanMarcacion findByCodPersFechaHora(String dbpool, String codPers,
			String fecha, String hora) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		BeanMarcacion marcacion = null;

		try {

			strSQL.append("select reloj "
					).append( " from t1275marcacion  "
					).append( " where cod_pers = ? and "
					).append( " fecha >= DATE('").append( Utiles.toYYYYMMDD(fecha) ).append( "') and hora = '"
					).append( hora.trim() ).append( "' and sdel = ? ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, Constantes.ACTIVO);
			rs = pre.executeQuery();

			while (rs.next()) {
				marcacion = new BeanMarcacion();
				marcacion.setReloj(rs.getString("reloj"));
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
		return marcacion;

	}

	/**
	 * M�todo que se encarga de insertar una marcacion
	 * 
	 * @param dbpool
	 * @param codPers
	 * @param fecha
	 * @param hora
	 * @param reloj
	 * @param usuario
	 * @throws SQLException
	 */
	public void insertByCodPersFechaReloj(String dbpool, String codPers,
			String fecha, String hora, String reloj, String usuario)
			throws SQLException {

		String strUpd = "";
		PreparedStatement pre = null;
		Connection con = null;

		try {

			strUpd = "insert into t1275marcacion(cod_pers, fecha, hora, reloj, sdel, fcreacion, cuser_crea ) values(?,?,?,?,?,?,?)";
			
			con = getConnection(dbpool);
			pre = con.prepareStatement(strUpd);
			pre.setString(1, codPers);
			pre.setDate(2, new BeanFechaHora(fecha).getSQLDate());
			pre.setString(3, hora);
			pre.setString(4, reloj);
			pre.setString(5, Constantes.ACTIVO);
			pre.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
			pre.setString(7, usuario);
			pre.executeUpdate();
 

		} catch (Exception e) {
			//log.error("**** SQL ERROR ****", e);
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
	 * Metodo encargado de la carga de practicantes
	 * 
	 * @param dbpool
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList findPracticantes(String dbpool) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;

		try {

			strSQL.append(" select cod_practica " ).append( " from   t1596practicante  "
					).append( " where  sestado_activo = ? ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, Constantes.ACTIVO);
			rs = pre.executeQuery();
			lista = new ArrayList();
			HashMap pradsa = null;

			while (rs.next()) {

				pradsa = new HashMap();

				pradsa.put("cod_practica", rs.getString("cod_practica"));

				lista.add(pradsa);
			}

 
		}

		catch (Exception e) {
			log.error("**** SQL ERROR ****", e);
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
		return lista;
	}

	/**
	 * Metodo que se encarga de verificar si una marcacion existe
	 * 
	 * @param dbpool
	 * @param codPers
	 * @param fecha
	 * @param hora
	 * @return @throws
	 *         SQLException
	 */
	public boolean findMarca(String dbpool, String codPers, String fecha,
			String hora) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		boolean existe = false;
		

		try {

			strSQL.append("  select * "
					).append( " from t1275marcacion  "
					).append( " where cod_pers = ? and "
					).append( " fecha >= DATE('"
					).append( Utiles.toYYYYMMDD(fecha) ).append( "') and " ).append( " hora = '"
					).append( hora.trim() ).append( "' and sdel = ? ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, Constantes.ACTIVO);
			rs = pre.executeQuery();

			if (rs.next()) {
				existe = true;
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
		return existe;
	}

	/**
	 * Metodo que se encarga de buscar los datos del personal que ha realizado
	 * marcaciones desde la fecha de inicio hasta el termino de la fecha final.
	 * Join con la tabla T1275Marcacion.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param fechaIni
	 *            String. Fecha inferior del rango de busqueda.
	 * @param fechaFin
	 *            String. Fecha superior del rango de busqueda.
	 * @return ArrayList conteniendo los registros cargados en HashMaps.
	 * @throws SQLException
	 */
	public ArrayList joinWithT02Nested(String dbpool, String fechaIni,
			String fechaFin, String criterio, String valor) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList listaPersonas = null;

//		String fecI = Utiles.toYYYYMMDD(fechaIni);
//		String fecF = Utiles.toYYYYMMDD(Utiles.dameFechaSiguiente(fechaFin, 1));

		try {

			

			if (criterio.equals("0")) {
				strSQL.append(" Select t02cod_pers, substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) cod_uorga "
				).append( " from t02perdp "
				);
				strSQL.append(" where t02cod_pers = '" ).append( valor.trim().toUpperCase() ).append( "' ");
			}

			if (criterio.equals("1")) {
				/*strSQL.append( " where t02cod_pers in (select distinct cod_pers "
				).append( "                       from t1275marcacion  "
				).append( "                       where   fecha >= DATE('").append(fecI).append( "') and "
				).append( "                       fecha < DATE('").append(fecF).append( "') and sdel = ? ) ");
				*/
				//strSQL.append(" and substr(trim(both ' ' from (nvl (admsp.t02perdp.t02cod_uorgl ,'')||NVL(admsp.t02perdp.t02cod_uorg ,''))),1,6)='"
				/*strSQL.append(" where substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) = '"
						).append( valor.trim().toUpperCase() ).append( "' ");*/
				strSQL.append(" Select t02cod_pers, substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) cod_uorga "
				).append( " from t02perdp "
				).append(" where  t02cod_uorgl is null and t02cod_uorg='").append( valor.trim().toUpperCase() ).append( "' ");
				strSQL.append(" union select t02cod_pers, substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) cod_uorga ")
				 .append(" from t02perdp where t02cod_uorgl='").append( valor.trim().toUpperCase() ).append( "' ");
				//strSQL.append(" and (t02cod_uorgl='").append( valor.trim().toUpperCase() ).append( "' or t02cod_uorg = ' ").append( valor.trim().toUpperCase() ).append( "' ");//2Q0301' or a.t02cod_uorg = '2Q0301' )")
			}

			//JRR
			if (criterio.equals("2")) {
				String intendencia = valor.substring(0,2);
				
				strSQL.append(" Select t02cod_pers, substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) cod_uorga "
				).append( " from t02perdp "
				).append(" where  t02cod_uorgl is null and t02cod_uorg LIKE '").append( intendencia.trim().toUpperCase() ).append( "%' ");
				strSQL.append(" union select t02cod_pers, substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) cod_uorga ")
				 .append(" from t02perdp where t02cod_uorgl LIKE '").append( intendencia.trim().toUpperCase() ).append( "%' ");
			}
			//
			
			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			//pre.setString(1, Constantes.ACTIVO);
			rs = pre.executeQuery();
			listaPersonas = new ArrayList();

			while (rs.next()) {

				HashMap a = new HashMap();

				a.put("t02cod_pers", rs.getString("t02cod_pers"));
				a.put("t02cod_uorg", rs.getString("cod_uorga"));

				listaPersonas.add(a);
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
		return listaPersonas;

	}
	
	//ICAPUNAY 05/10/2012 AJUSTES AOM 06A4T11 LABOR EXCEPCIONAL
	/**
	 * Metodo que se encarga de buscar las marcas de reloj de una persona para una fecha especifica y segun rangos de inicio y fin de las horas de marca.
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            Numero de registro del trabajador.
	 * @param FechaEval
	 *            Fecha de busqueda.
	 * @param HoraIni
	 *            Filtro para la hora de la marcacion de inicio.
	 * @param HoraFin
	 *            Filtro para la hora de la marcacion de salida.
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanMarcacion.
	 * @throws SQLException
	 */
	public ArrayList findByCodPersFechaRangosHoras(String dbpool, String codPers,
			String FechaEval,String HoraIni, String HoraFin) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList marcaciones = null;

		String fecI = Utiles.toYYYYMMDD(FechaEval);
		String fecF = Utiles.toYYYYMMDD(Utiles.dameFechaSiguiente(FechaEval, 1));

		try {
			strSQL.append("select cod_pers, fecha, hora, reloj "
					).append( " from t1275marcacion  "
					).append( " where cod_pers = ? and "
					).append( " hora >= '").append(HoraIni.trim()).append("' and "
					).append( " hora <= '").append(HoraFin.trim()).append("' and "				
					).append( " fecha >= DATE('").append( fecI).append( "') and "
					).append( " fecha < DATE('").append( fecF ).append( "') and sdel = ? order by hora");

			log.debug("findByCodPersFechaRangosHoras(strSQL): "+strSQL.toString());
			
			con = getConnection(dbpool);		
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, Constantes.ACTIVO);
			rs = pre.executeQuery();
			marcaciones = new ArrayList();
			BeanMarcacion marcacion = null;

			while (rs.next()) {
				marcacion = new BeanMarcacion();
				marcacion.setCodPers(rs.getString("cod_pers"));
				marcacion.setFecha(new BeanFechaHora(rs.getDate("fecha")).getFormatDate("dd/MM/yyyy"));				
				marcacion.setHora(rs.getString("hora"));
				marcacion.setReloj(rs.getString("reloj"));
				marcaciones.add(marcacion);
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
		return marcaciones;
	}	
	//FIN ICAPUNAY 05/10/2012 AJUSTES AOM 06A4T11 LABOR EXCEPCIONAL
}