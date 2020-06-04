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
import pe.gob.sunat.sp.asistencia.bean.BeanTurnoTrabajo;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;

/**
 * <p>
 * Title: T1270DAO
 * </p>
 * <p>
 * Description: Clase encargada de administrar las consultas a la tabla
 * T1270TurnoPerson
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

public class T1270DAO extends DAOAccesoBD {

	private static final Logger log = Logger.getLogger(T1270DAO.class);

	public T1270DAO() {
	}

	/**
	 * Metodo encargado de buscar los turnos de trabajo filtrados por un
	 * criterio con un valor determinado. Join con las tablas T45Turno,
	 * T02Perdp, T99Codigos y T12UOrga.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param criterio
	 *            String. Criterio de filtro de los registros.
	 * @param valor
	 *            String. Valor de filtro para el criterio dado.
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanTurnoTrabajo.
	 * @throws SQLException
	 */
	public ArrayList joinWithT45T02T99T12(String dbpool, String criterio,
			String valor) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList turnos = null;

		try {

			strSQL.append("select 	t.cod_pers,t.u_organ,c.t99abrev, ")
					.append( "        p.t02cod_cate, p.t02ap_pate,p.t02ap_mate,p.t02nombres, ")
					.append( "        t.turno,t.est_id,u.t12des_corta,t2.des_turno, ")
					.append( "        t.fini, t.ffin, t2.h_inicio, t2.h_fin , t.fcreacion , t.cuser_crea , t.fmod , t.cuser_mod ")
					.append( "from    t1270turnoperson t, ").append( "        t45turno t2, ")
					.append( "	   t02perdp p, " ).append( "        t99codigos c, ")
					.append( "        t12uorga u " ).append( "where   (c.t99cod_tab = ?) and ")
					.append( "        (t.est_id = ?) and ")
					.append( "        (t2.est_id = ?) ");

			//busqueda por registro de trabajador
			if (criterio.equals("0")) {
				strSQL .append( " and t.cod_pers like '" ).append( valor.trim().toUpperCase()
						).append( "%' ");
			}
			//busqueda por UO
			if (criterio.equals("1")) {
				strSQL .append( " and t.u_organ like '" ).append( valor.trim().toUpperCase()
						).append( "%' ");
			}
			//busqueda por categoria
			if (criterio.equals("2")) {
				strSQL .append( " and p.t02cod_cate like '")
						.append( valor.trim().toUpperCase() ).append( "%' ");
			}

			//Joins
			strSQL .append( " and (p.t02cod_cate = c.t99codigo) and "
					+ " (t.u_organ = u.t12cod_uorga) and "
					+ " (t.cod_pers = p.t02cod_pers) and "
					+ " (t.turno = t2.cod_turno)");

			strSQL .append( " order by 12,13,8,5 ");
			log.debug("Const:"+strSQL);
			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, Constantes.CODTAB_CATEGORIA);
			pre.setString(2, Constantes.ACTIVO);
			pre.setString(3, Constantes.ACTIVO);
			rs = pre.executeQuery();
			turnos = new ArrayList();

			BeanTurnoTrabajo turno = null;
			while (rs.next()) {

				turno = new BeanTurnoTrabajo();

				turno.setCodPers(rs.getString("cod_pers"));
				turno.setCodUOrg(rs.getString("u_organ"));
				turno.setDescUOrg(rs.getString("t12des_corta").trim());
				turno.setCodCategoria(rs.getString("t02cod_cate"));
				turno.setDescCategoria(rs.getString("t99abrev").trim());
				turno.setTrabajador(rs.getString("t02ap_pate").trim() + " "
						+ rs.getString("t02ap_mate").trim() + ", "
						+ rs.getString("t02nombres").trim());
				turno.setFechaIni(rs.getTimestamp("fini"));
				turno.setFechaFin(rs.getTimestamp("ffin"));
				turno.setHoraIni(rs.getString("h_inicio"));
				turno.setHoraFin(rs.getString("h_fin"));
				turno.setTurno(rs.getString("turno"));
				turno.setDescTurno(rs.getString("des_turno").trim());
				
				//Inicio agregado x ICR 06/12/2010
				turno.setFechaCreacion(rs.getTimestamp("fcreacion"));
				turno.setCodUsuCreacion(rs.getString("cuser_crea"));
				turno.setFechaModificacion(rs.getTimestamp("fmod"));
				turno.setCodUsuModificacion(rs.getString("cuser_mod"));
				//Fin agregado x ICR 06/12/2010

				turnos.add(turno);
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
		return turnos;
	}

	/**
	 * Metodo encargado de buscar turnos de un determinado trabajador. Join con
	 * la tabla T45Turno.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            String. Numero de registro del trabajador.
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanTurnoTrabajo.
	 * @throws SQLException
	 */
	public ArrayList joinWithT45(String dbpool, String codPers)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList turnos = null;

		try {

			strSQL.append("select 	tp.u_organ, tp.turno, tp.fini, ")
					.append( "        tp.ffin, t.h_inicio, t.h_fin , tp.fcreacion , tp.cuser_crea , tp.fmod , tp.cuser_mod ")
					.append( "from 	t1270turnoperson tp, " ).append( "        t45turno t ")
					.append( "where   tp.cod_pers = ? and ")
					.append( "        tp.est_id = ? and " ).append( "		tp.turno = t.cod_turno");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, Constantes.ACTIVO);
			rs = pre.executeQuery();
			turnos = new ArrayList();

			BeanTurnoTrabajo turno = null;
			while (rs.next()) {

				turno = new BeanTurnoTrabajo();

				turno.setCodUOrg(rs.getString("u_organ"));
				turno.setFechaIni(rs.getTimestamp("fini"));
				turno.setFechaFin(rs.getTimestamp("ffin"));
				turno.setHoraIni(rs.getString("h_inicio"));
				turno.setHoraFin(rs.getString("h_fin"));
				turno.setTurno(rs.getString("turno"));
				
				//Inicio agregado x ICR 06/12/2010
				turno.setFechaCreacion(rs.getTimestamp("fcreacion"));
				turno.setCodUsuCreacion(rs.getString("cuser_crea"));
				turno.setFechaModificacion(rs.getTimestamp("fmod"));
				turno.setCodUsuModificacion(rs.getString("cuser_mod"));
				//Fin agregado x ICR 06/12/2010

				turnos.add(turno);
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
		return turnos;
	}

	/**
	 * Metodo encargado de buscar turnos de un determinado trabajador filtrados
	 * por un rango de fechas. Join con la tabla T45Turno.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            String. Numero de registro del trabajador.
	 * @param fIni
	 *            Timestamp. Fecha inferior del rango de busqueda.
	 * @param fFin
	 *            Timestamp. Fecha superior del rango de busqueda.
	 * @param orden
	 *            int. Indica el campo por el cual se desea ordenar el
	 *            resultado.
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanTurnoTrabajo.
	 * @throws SQLException
	 */
	public ArrayList joinWithT45ByCodFiniFfin(String dbpool, String codPers,
			Timestamp fIni, Timestamp fFin, int orden) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList turnos = null;

		try {

			strSQL.append("select 	DATE(tp.fini), tp.cod_pers, tp.u_organ, tp.turno, ")
					.append( "        tp.fini, tp.ffin, t.h_inicio, t.h_fin, tp.sonom_id, t.oper_id , tp.fcreacion , tp.cuser_crea , tp.fmod , tp.cuser_mod,tp.observ ")
					.append( "from 	t1270turnoperson tp, ")
					.append( "        t45turno t ")
					.append( "where   tp.cod_pers = ? and ")
					.append( "        tp.est_id = ? and ")
					.append( "	    tp.turno = t.cod_turno ");

			if (orden == 1) {
				strSQL .append( " order by 1");
			}

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, Constantes.ACTIVO);
			rs = pre.executeQuery();
			turnos = new ArrayList();

			BeanTurnoTrabajo turno = null;
			while (rs.next()) {

				Timestamp f1 = rs.getTimestamp("fini");
				Timestamp f2 = rs.getTimestamp("ffin");

				if (fIni.equals(f1) || fIni.equals(f2) || fFin.equals(f1)
						|| fFin.equals(f2)
						|| (fFin.after(f1) && fFin.before(f2))
						|| (fIni.after(f1) && fIni.before(f2))
						|| (fIni.after(f1) && fFin.before(f2))
						|| (fIni.before(f1) && fFin.after(f2))) {

					turno = new BeanTurnoTrabajo();
					turno.setCodPers(rs.getString("cod_pers"));
					turno.setCodUOrg(rs.getString("u_organ"));
					turno.setFechaIni(f1);
					turno.setFechaFin(f2);
					turno.setHoraIni(rs.getString("h_inicio"));
					turno.setHoraFin(rs.getString("h_fin"));
					turno.setTurno(rs.getString("turno"));
					String oper = rs.getString("oper_id");
					turno.setOperativo(oper != null ? oper
							.equals(Constantes.ACTIVO) : false);
							
					//Inicio agregado x ICR 06/12/2010
					turno.setFechaCreacion(rs.getTimestamp("fcreacion"));
					turno.setCodUsuCreacion(rs.getString("cuser_crea"));
					turno.setFechaModificacion(rs.getTimestamp("fmod"));
					turno.setCodUsuModificacion(rs.getString("cuser_mod"));
					//Fin agregado x ICR 06/12/2010
					turno.setSustento(rs.getString("observ"));

					turnos.add(turno);
				}
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
		return turnos;
	}

	/**
	 * Metodo encargado de buscar el turno de un determinado trabajador para una
	 * fecha especifica. Join con la tabla T45Turno.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            String. Numero de registro del trabajador.
	 * @param fecha
	 *            String. Fecha para la cual se busca el turno del trabajador.
	 * @return Bean de la clase BeanTurnoTrabajo.
	 * @throws SQLException
	 */
	public BeanTurnoTrabajo joinWithT45ByCodFecha(String dbpool,
			String codPers, String fecha) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;

		String fechaCompara = Utiles.toYYYYMMDD(fecha);
		BeanTurnoTrabajo turno = null;

		try {

			strSQL.append("select 	tp.u_organ, tp.turno, tp.fini, tp.sonom_id, ")
					.append( "        tp.ffin, t.h_inicio, t.h_fin, t.tolera_turno, t.hlimit, t.oper_id, ")
					.append( "        t.h_inirefr, t.h_finref, t.min_refr, t.scontrol_id, t.dias_int , tp.fcreacion , tp.cuser_crea , tp.fmod , tp.cuser_mod, t.des_turno ")
					.append( "from    t1270turnoperson tp, " ).append( "        t45turno t ")
					.append( "where   tp.cod_pers = ? and ")
					.append( "        tp.turno > '' ")
					.append( "  and   tp.fini <= '" ).append( fechaCompara)
					.append( "' and " ).append( "        tp.ffin >= '")
					.append( fechaCompara ).append( "' and " )
					.append( "	    tp.turno = t.cod_turno and ")
					.append( "        tp.est_id = ? ");

			con = getConnection(dbpool);
			//con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, Constantes.ACTIVO);
			rs = pre.executeQuery();
			log.debug("Query : "+ strSQL.toString());
			log.debug("Query1 : "+ codPers);
			if (rs.next()) {

				turno = new BeanTurnoTrabajo();

				turno.setDescTurno(rs.getString("des_turno"));
				turno.setCodUOrg(rs.getString("u_organ"));
				turno.setFechaIni(rs.getTimestamp("fini"));
				turno.setFechaFin(rs.getTimestamp("ffin"));
				turno.setHoraIni(rs.getString("h_inicio"));
				turno.setHoraFin(rs.getString("h_fin"));
				turno.setTurno(rs.getString("turno"));
				turno.setTolera(rs.getInt("tolera_turno"));
				turno.setHoraLimite(rs.getString("hlimit"));
				turno.setHoraIniRefrigerio(rs.getString("h_inirefr"));
				turno.setHoraFinRefrigerio(rs.getString("h_finref"));
				turno.setMinutosRefrigerio(rs.getInt("min_refr"));
				turno.setDuracion(rs.getInt("dias_int"));
				//turno.setHorasCompensa(Integer.parseInt(rs.getString("sonom_id")!=null?rs.getString("sonom_id"):"0"));

				//Inicio agregado x ICR 06/12/2010
				turno.setFechaCreacion(rs.getTimestamp("fcreacion"));
				turno.setCodUsuCreacion(rs.getString("cuser_crea"));
				turno.setFechaModificacion(rs.getTimestamp("fmod"));
				turno.setCodUsuModificacion(rs.getString("cuser_mod"));
				//Fin agregado x ICR 06/12/2010
				
				String oper = rs.getString("oper_id");
				turno.setOperativo(oper != null ? oper
						.equals(Constantes.ACTIVO) : false);

				String control = rs.getString("scontrol_id");
				turno.setControla(control != null ? control
						.equals(Constantes.ACTIVO) : true);

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
		return turno;
	}
	

	/**
	 * Metodo encargado de buscar el turno de un determinado trabajador para una
	 * fecha especifica. Join con la tabla T45Turno.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            String. Numero de registro del trabajador.
	 * @param fecha
	 *            String. Fecha para la cual se busca el turno del trabajador.
	 * @return Arreglo de la clase BeanTurnoTrabajo.
	 * @throws SQLException
	 */
	public ArrayList joinWithT45ByCodFechaArray(String dbpool,
			String codPers, String fecha) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList turnos = null;
		String fechaCompara = Utiles.toYYYYMMDD(fecha);
		

		try {

			strSQL.append("select 	tp.u_organ, tp.turno, tp.fini, tp.sonom_id, ")
					.append( "        tp.ffin, t.h_inicio, t.h_fin, t.tolera_turno, t.hlimit, t.oper_id, ")
					.append( "        t.h_inirefr, t.h_finref, t.min_refr, t.scontrol_id, t.dias_int , tp.fcreacion , tp.cuser_crea , tp.fmod , tp.cuser_mod, t.des_turno ")
					.append( "from    t1270turnoperson tp, " ).append( "        t45turno t ")
					.append( "where   tp.cod_pers = ? and ")
					.append( "        tp.turno > '' ")
					.append( "  and   tp.fini <= '" ).append( fechaCompara)
					.append( "' and " ).append( "        tp.ffin >= '")
					.append( fechaCompara ).append( "' and " )
					.append( "	    tp.turno = t.cod_turno and ")
					.append( "        tp.est_id = ? ");

			con = getConnection(dbpool);
			//con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, Constantes.ACTIVO);
			rs = pre.executeQuery();
			log.debug("Query : "+ strSQL.toString());
			log.debug("Query1 : "+ codPers);
			turnos = new ArrayList();
			BeanTurnoTrabajo turno = null;
			while (rs.next()) {

				turno = new BeanTurnoTrabajo();

				turno.setDescTurno(rs.getString("des_turno"));
				turno.setCodUOrg(rs.getString("u_organ"));
				turno.setFechaIni(rs.getTimestamp("fini"));
				turno.setFechaFin(rs.getTimestamp("ffin"));
				turno.setHoraIni(rs.getString("h_inicio"));
				turno.setHoraFin(rs.getString("h_fin"));
				turno.setTurno(rs.getString("turno"));
				turno.setTolera(rs.getInt("tolera_turno"));
				turno.setHoraLimite(rs.getString("hlimit"));
				turno.setHoraIniRefrigerio(rs.getString("h_inirefr"));
				turno.setHoraFinRefrigerio(rs.getString("h_finref"));
				turno.setMinutosRefrigerio(rs.getInt("min_refr"));
				turno.setDuracion(rs.getInt("dias_int"));
				//turno.setHorasCompensa(Integer.parseInt(rs.getString("sonom_id")!=null?rs.getString("sonom_id"):"0"));

				//Inicio agregado x ICR 06/12/2010
				turno.setFechaCreacion(rs.getTimestamp("fcreacion"));
				turno.setCodUsuCreacion(rs.getString("cuser_crea"));
				turno.setFechaModificacion(rs.getTimestamp("fmod"));
				turno.setCodUsuModificacion(rs.getString("cuser_mod"));
				//Fin agregado x ICR 06/12/2010
				
				String oper = rs.getString("oper_id");
				turno.setOperativo(oper != null ? oper
						.equals(Constantes.ACTIVO) : false);

				String control = rs.getString("scontrol_id");
				turno.setControla(control != null ? control
						.equals(Constantes.ACTIVO) : true);
				
				turnos.add(turno);
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
		return turnos;
	}

	/**
	 * Metodo encargado de buscar los turnos de trabajo filtrados por un
	 * criterio con un valor determinado, ademas de por un rango de fechas y un
	 * codigo de turno especifico Join con las tablas T45Turno, T02Perdp,
	 * T99Codigos, T12UOrga.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param fechaIni
	 *            String. Fecha inferior del rango de busqueda.
	 * @param fechaFin
	 *            String. Fecha superior del rango de busqueda.
	 * @param codTurno
	 *            String. Codigo del turno
	 * @param criterio
	 *            String. Criterio de filtro de los registros.
	 * @param valor
	 *            String. Valor de filtro para el criterio dado.
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanTurnoTrabajo.
	 * @throws SQLException
	 */
	public ArrayList joinWithT45T02T99T12(String dbpool, String fechaIni,
			String fechaFin, String codTurno, String criterio, String valor,
			HashMap seguridad) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList turnos = null;

		try {

			strSQL.append("select  t.cod_pers,t.u_organ,c.t99abrev, ")			
					.append( "       substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) cod_cate, p.t02ap_pate,p.t02ap_mate,p.t02nombres, ")
					.append( "        t.turno,t.est_id,u.t12des_corta,t2.des_turno, ")
					.append( "        t.fini, t.ffin, t2.h_inicio, t2.h_fin , t.fcreacion , t.cuser_crea , t.fmod , t.cuser_mod,t.observ ")
					.append( "from    t1270turnoperson t, " ).append( "        t45turno t2, ")
					.append( "	t02perdp p, " ).append( "        t99codigos c, ")
					.append( "        t12uorga u " ).append( "where 	(c.t99cod_tab = ?) and ")
					.append( "        (t.est_id = ?) ");
			// EBV 13/03/2009 Se mostraran todos los turnos programados independientemente de si esta activo o no.
				//	.append( "        (t2.est_id = ?) ");
			//	EBV 13/03/2009 ---
			//Si se ha indicado un turno en particular
			if (!codTurno.trim().equals("")) {
				strSQL .append( " and turno = '" ).append( codTurno.trim().toUpperCase())
						.append( "'");
			}

			String fIni = Utiles.toYYYYMMDD(fechaIni);
			String fFin = Utiles.toYYYYMMDD(fechaFin);

			if (!fechaIni.equals("") && !fechaFin.equals("")) {

				strSQL .append( " and ( ");
				strSQL .append( " ( t.fini = DATE('" ).append( fIni ).append( "') ) or ");
				strSQL .append( " ( t.fini = DATE('" ).append( fFin ).append( "') ) or ");
				strSQL .append( " ( t.ffin = DATE('" ).append( fIni ).append( "') ) or ");
				strSQL .append( " ( t.ffin = DATE('" ).append( fFin ).append( "') ) or ");
				strSQL .append( " ( (DATE('" ).append( fFin ).append( "') > t.fini) and ( DATE('").append( fFin ).append( "') < t.ffin ) ) or ");
				strSQL .append( " ( (DATE('" ).append( fIni ).append( "') > t.fini) and ( DATE('").append( fIni ).append( "') < t.ffin ) ) or ");
				strSQL .append( " ( (DATE('" ).append( fIni ).append( "') > t.fini) and ( DATE('").append( fFin ).append( "') < t.ffin ) ) or ");
				strSQL .append( " ( (DATE('" ).append( fIni ).append( "') < t.fini) and ( DATE('").append( fFin ).append( "') > t.ffin ) ) ");
				strSQL .append( " ) ");

			} else {
				strSQL .append( " and (1 = 2) ");
			}

			//busqueda por registro de trabajador
			if (criterio.equals("0")) {
				strSQL .append( " and t.cod_pers like '" ).append( valor.trim().toUpperCase())
						.append( "%' ");
			}
			//busqueda por UO
			if (criterio.equals("1")) {
				//strSQL .append( " and t.u_organ like '" ).append( valor.trim().toUpperCase()) ICAPUNAY-PAS20145E230000034 ajustes a consulta de "turnos administrativos" y "autorizar papeletas"
				strSQL .append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '" ).append( valor.trim().toUpperCase()) //ICAPUNAY-PAS20145E230000034 ajustes a consulta de "turnos administrativos" y "autorizar papeletas"
										.append( "%' ");
			}
			//busqueda por categoria
			if (criterio.equals("2")) {
				strSQL .append( " and substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) like '")
						.append( valor.trim().toUpperCase() ).append( "%' ");
			}
			boolean rolOper=false;
			//criterios de visibilidad
			if (seguridad != null) {

				HashMap roles = (HashMap) seguridad.get("roles");
				String uoSeg = (String) seguridad.get("uoSeg");
				String uoAO = (String) seguridad.get("uoAO");//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

				if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null) {rolOper=true;
					strSQL .append( " and ((substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '")
							.append( uoSeg ).append( "') ");
					
					strSQL .append( " or (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) in ")
					.append( "  (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '")
					.append( uoAO ).append( "'))) ");
				}
			}

			//Joins
			strSQL .append( " and (substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) = c.t99codigo) and ")
					.append( "     (t.u_organ = u.t12cod_uorga) and ") 
					.append( "     (t.cod_pers = p.t02cod_pers) and ")
					.append( "     (t.turno = t2.cod_turno)");

			strSQL .append( " order by 12,13,8,5 ");
			log.debug("ROL:"+rolOper+"-Consulta:"+strSQL);
			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, Constantes.CODTAB_CATEGORIA);
			pre.setString(2, Constantes.ACTIVO);
			// EBV 13/03/2009 Se mostraran todos los turnos programados independientemente de si esta activo o no.
		//	pre.setString(3, Constantes.ACTIVO);
//			 EBV 13/03/2009 --------
			rs = pre.executeQuery();

			turnos = new ArrayList();
			BeanTurnoTrabajo turno = null;
			while (rs.next()) {

				turno = new BeanTurnoTrabajo();

				turno.setCodPers(rs.getString("cod_pers"));
				turno.setCodUOrg(rs.getString("u_organ"));
				turno.setDescUOrg(rs.getString("t12des_corta") != null ? rs
						.getString("t12des_corta").trim() : "");
				turno.setCodCategoria(rs.getString("cod_cate"));
				turno.setDescCategoria(rs.getString("t99abrev") != null ? rs
						.getString("t99abrev").trim() : "");
				turno.setTrabajador(rs.getString("t02ap_pate").trim().concat(" ")
						.concat( rs.getString("t02ap_mate").trim() ).concat( ", ")
						.concat( rs.getString("t02nombres").trim()));
				turno.setFechaIni(rs.getTimestamp("fini"));
				turno.setFechaFin(rs.getTimestamp("ffin"));
				turno.setSustento(rs.getString("observ"));
				turno.setHoraIni(rs.getString("h_inicio"));
				turno.setHoraFin(rs.getString("h_fin"));
				turno.setTurno(rs.getString("turno"));
				turno.setDescTurno(rs.getString("des_turno") != null ? rs
						.getString("des_turno").trim() : "");

				//Inicio agregado x ICR 06/12/2010
				turno.setFechaCreacion(rs.getTimestamp("fcreacion"));
				turno.setCodUsuCreacion(rs.getString("cuser_crea"));
				turno.setFechaModificacion(rs.getTimestamp("fmod"));
				turno.setCodUsuModificacion(rs.getString("cuser_mod"));
				//Fin agregado x ICR 06/12/2010
				
				turnos.add(turno);
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
		return turnos;
	}

	/**
	 * Metodo encargado de obtener el total de horas a compensar en un dia
	 * 
	 * @param dbpool
	 * @param codPers
	 * @param fecha
	 * @return @throws
	 *         SQLException
	 */
	public int findHorasCompensa(String dbpool, String codPers, String fecha)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		int horas = 0;
		
		try {

			strSQL.append("select  qhoras " ).append( "from    t1608compensa ")
					.append( "where   cod_personal = ? ")
					.append( "        and fcompensa = ? ")
					.append( "        and ind_estado_id = ? ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setDate(2, new BeanFechaHora(fecha).getSQLDate());
			pre.setString(3, Constantes.ACTIVO);
			rs = pre.executeQuery();

			if (rs.next()) {
				//se convierten la cantidad de horas a minutos
				horas = rs.getInt("qhoras")*60;
		
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
		return horas;
	}
	
	
	//ICAPUNAY 05/10/2012 AJUSTES AOM 06A4T11 LABOR EXCEPCIONAL	
	/**
	 * Metodo encargado de buscar el turno controlado operativo o administrativo de duracion de 1 dia de un determinado trabajador para una
	 * fecha especifica. Join con la tabla T45Turno.
	 * @param dbpool 
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            String. Numero de registro del trabajador.
	 * @param fecha
	 *            String. Fecha para la cual se busca el turno del trabajador.
	 * @return Bean de la clase BeanTurnoTrabajo.
	 * @throws SQLException
	 */
	public BeanTurnoTrabajo joinWithT45ByCodPersFecha_Controlado_OperAdm1dia(String dbpool,
			String codPers, String fecha) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;

		String fechaCompara = Utiles.toYYYYMMDD(fecha);
		BeanTurnoTrabajo turno = null;

		try {
			strSQL.append("select 	tp.u_organ, tp.turno, tp.fini, tp.sonom_id, ")
					.append( "        tp.ffin, t.h_inicio, t.h_fin, t.tolera_turno, t.hlimit, t.oper_id, ")
					.append( "        t.h_inirefr, t.h_finref, t.min_refr, t.scontrol_id, t.dias_int , tp.fcreacion , tp.cuser_crea , tp.fmod , tp.cuser_mod, t.des_turno ")
					.append( "from    t1270turnoperson tp, " ).append( "        t45turno t ")
					.append( "where   tp.cod_pers = ? and ")
					.append( "        tp.turno > '' ")
					.append( "  and   tp.fini <= '" ).append( fechaCompara)
					.append( "' and " ).append( "        tp.ffin >= '")
					.append( fechaCompara ).append( "' and " )
					.append( "	    tp.turno = t.cod_turno and ")
					.append( "        tp.est_id = ? ")
					.append( " and t.scontrol_id='1' and t.oper_id in ('0','1') and t.h_fin>t.h_inicio ");					

			con = getConnection(dbpool);		
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, Constantes.ACTIVO);			
			rs = pre.executeQuery();
			log.debug("joinWithT45ByCodPersFecha_Controlado_OperAdm1dia(strSQL): "+ strSQL.toString());
			log.debug("registro: "+ codPers);
			if (rs.next()) {
				turno = new BeanTurnoTrabajo();
				turno.setDescTurno(rs.getString("des_turno"));
				turno.setCodUOrg(rs.getString("u_organ"));
				turno.setFechaIni(rs.getTimestamp("fini"));
				turno.setFechaFin(rs.getTimestamp("ffin"));
				turno.setHoraIni(rs.getString("h_inicio"));
				turno.setHoraFin(rs.getString("h_fin"));
				turno.setTurno(rs.getString("turno"));
				turno.setTolera(rs.getInt("tolera_turno"));
				turno.setHoraLimite(rs.getString("hlimit"));
				turno.setHoraIniRefrigerio(rs.getString("h_inirefr"));
				turno.setHoraFinRefrigerio(rs.getString("h_finref"));
				turno.setMinutosRefrigerio(rs.getInt("min_refr"));
				turno.setDuracion(rs.getInt("dias_int"));
				turno.setFechaCreacion(rs.getTimestamp("fcreacion"));
				turno.setCodUsuCreacion(rs.getString("cuser_crea"));
				turno.setFechaModificacion(rs.getTimestamp("fmod"));
				turno.setCodUsuModificacion(rs.getString("cuser_mod"));				
				String oper = rs.getString("oper_id");
				if (oper != null){
					turno.setOperativo(oper.equals(Constantes.ACTIVO)?true:false);
				}else{//==null
					/*if (turno.getHoraFin().compareTo(turno.getHoraIni())>0){//de 1 dia de duracion adm u ope
						//no se si es adm u ope?????????
					}else{//2 dias
						turno.setOperativo(true);
					}*/
				}
				//turno.setOperativo(oper != null ? oper.equals(Constantes.ACTIVO) : false);
				String control = rs.getString("scontrol_id");
				if (control!= null){
					turno.setControla(control.equals(Constantes.ACTIVO)?true:false);
				}
				//turno.setControla(control != null ? control.equals(Constantes.ACTIVO) : true);
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
		return turno;
	}
	
	/**
	 * Metodo encargado de buscar el turno controlado operativo de duracion de 2 dias de un determinado trabajador para una
	 * fecha especifica. Join con la tabla T45Turno.
	 * @param dbpool 
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            String. Numero de registro del trabajador.
	 * @param fecha
	 *            String. Fecha para la cual se busca el turno del trabajador.
	 * @return Bean de la clase BeanTurnoTrabajo.
	 * @throws SQLException
	 */
	public BeanTurnoTrabajo joinWithT45ByCodPersFecha_Controlado_Oper2dias(String dbpool,
			String codPers, String fecha) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;

		String fechaCompara = Utiles.toYYYYMMDD(fecha);
		BeanTurnoTrabajo turno = null;

		try {
			strSQL.append("select 	tp.u_organ, tp.turno, tp.fini, tp.sonom_id, ")
					.append( "        tp.ffin, t.h_inicio, t.h_fin, t.tolera_turno, t.hlimit, t.oper_id, ")
					.append( "        t.h_inirefr, t.h_finref, t.min_refr, t.scontrol_id, t.dias_int , tp.fcreacion , tp.cuser_crea , tp.fmod , tp.cuser_mod, t.des_turno ")
					.append( "from    t1270turnoperson tp, " ).append( "        t45turno t ")
					.append( "where   tp.cod_pers = ? and ")
					.append( "        tp.turno > '' ")
					.append( "  and   tp.fini = '" ).append( fechaCompara)
					.append( "' and " )
					.append( "	    tp.turno = t.cod_turno and ")
					.append( "        tp.est_id = ? ")
					.append( " and t.scontrol_id='1' and t.oper_id='1' and t.h_fin<=t.h_inicio ");

			con = getConnection(dbpool);		
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, Constantes.ACTIVO);			
			rs = pre.executeQuery();
			log.debug("joinWithT45ByCodPersFecha_Controlado_Oper2dias(strSQL): "+ strSQL.toString());
			log.debug("registro: "+ codPers);
			if (rs.next()) {
				turno = new BeanTurnoTrabajo();
				turno.setDescTurno(rs.getString("des_turno"));
				turno.setCodUOrg(rs.getString("u_organ"));
				turno.setFechaIni(rs.getTimestamp("fini"));
				turno.setFechaFin(rs.getTimestamp("ffin"));
				turno.setHoraIni(rs.getString("h_inicio"));
				turno.setHoraFin(rs.getString("h_fin"));
				turno.setTurno(rs.getString("turno"));
				turno.setTolera(rs.getInt("tolera_turno"));
				turno.setHoraLimite(rs.getString("hlimit"));
				turno.setHoraIniRefrigerio(rs.getString("h_inirefr"));
				turno.setHoraFinRefrigerio(rs.getString("h_finref"));
				turno.setMinutosRefrigerio(rs.getInt("min_refr"));
				turno.setDuracion(rs.getInt("dias_int"));
				turno.setFechaCreacion(rs.getTimestamp("fcreacion"));
				turno.setCodUsuCreacion(rs.getString("cuser_crea"));
				turno.setFechaModificacion(rs.getTimestamp("fmod"));
				turno.setCodUsuModificacion(rs.getString("cuser_mod"));				
				String oper = rs.getString("oper_id");
				if (oper != null){
					turno.setOperativo(oper.equals(Constantes.ACTIVO)?true:false);
				}else{//==null
					/*if (turno.getHoraFin().compareTo(turno.getHoraIni())>0){//de 1 dia de duracion adm u ope
						//no se si es adm u ope?????????
					}else{//2 dias
						turno.setOperativo(true);
					}*/
				}
				//turno.setOperativo(oper != null ? oper.equals(Constantes.ACTIVO) : false);
				String control = rs.getString("scontrol_id");
				if (control!= null){
					turno.setControla(control.equals(Constantes.ACTIVO)?true:false);
				}
				//turno.setControla(control != null ? control.equals(Constantes.ACTIVO) : true);
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
		return turno;
	}
	//FIN ICAPUNAY 05/10/2012 AJUSTES AOM 06A4T11 LABOR EXCEPCIONAL
	
	//ICAPUNAY-PAS20144EB20000144-Optimizacion Labor Excepcional-Memo 31-2013-4F3100
	/**
	 * Metodo encargado de buscar el turno no controlado operativo o administrativo de duracion de 1 dia de un determinado trabajador para una
	 * fecha especifica. Join con la tabla T45Turno.
	 * @param dbpool 
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            String. Numero de registro del trabajador.
	 * @param fecha
	 *            String. Fecha para la cual se busca el turno del trabajador.
	 * @return Bean de la clase BeanTurnoTrabajo.
	 * @throws SQLException
	 */
	public BeanTurnoTrabajo joinWithT45ByCodPersFecha_NoControlado_OperAdm1dia(String dbpool,
			String codPers, String fecha) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;

		String fechaCompara = Utiles.toYYYYMMDD(fecha);
		BeanTurnoTrabajo turno = null;

		try {
			strSQL.append("select 	tp.u_organ, tp.turno, tp.fini, tp.sonom_id, ")
					.append( "        tp.ffin, t.h_inicio, t.h_fin, t.tolera_turno, t.hlimit, t.oper_id, ")
					.append( "        t.h_inirefr, t.h_finref, t.min_refr, t.scontrol_id, t.dias_int , tp.fcreacion , tp.cuser_crea , tp.fmod , tp.cuser_mod, t.des_turno ")
					.append( "from    t1270turnoperson tp, " ).append( "        t45turno t ")
					.append( "where   tp.cod_pers = ? and ")
					.append( "        tp.turno > '' ")
					.append( "  and   tp.fini <= '" ).append( fechaCompara)
					.append( "' and " ).append( "        tp.ffin >= '")
					.append( fechaCompara ).append( "' and " )
					.append( "	    tp.turno = t.cod_turno and ")
					.append( "        tp.est_id = ? ")
					.append( " and t.scontrol_id='0' and t.oper_id in ('0','1') and t.h_fin>t.h_inicio ");					

			con = getConnection(dbpool);		
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, Constantes.ACTIVO);			
			rs = pre.executeQuery();
			log.debug("joinWithT45ByCodPersFecha_NoControlado_OperAdm1dia(strSQL): "+ strSQL.toString());
			log.debug("registro: "+ codPers);
			if (rs.next()) {
				turno = new BeanTurnoTrabajo();
				turno.setDescTurno(rs.getString("des_turno"));
				turno.setCodUOrg(rs.getString("u_organ"));
				turno.setFechaIni(rs.getTimestamp("fini"));
				turno.setFechaFin(rs.getTimestamp("ffin"));
				turno.setHoraIni(rs.getString("h_inicio"));
				turno.setHoraFin(rs.getString("h_fin"));
				turno.setTurno(rs.getString("turno"));
				turno.setTolera(rs.getInt("tolera_turno"));
				turno.setHoraLimite(rs.getString("hlimit"));
				turno.setHoraIniRefrigerio(rs.getString("h_inirefr"));
				turno.setHoraFinRefrigerio(rs.getString("h_finref"));
				turno.setMinutosRefrigerio(rs.getInt("min_refr"));
				turno.setDuracion(rs.getInt("dias_int"));
				turno.setFechaCreacion(rs.getTimestamp("fcreacion"));
				turno.setCodUsuCreacion(rs.getString("cuser_crea"));
				turno.setFechaModificacion(rs.getTimestamp("fmod"));
				turno.setCodUsuModificacion(rs.getString("cuser_mod"));				
				String oper = rs.getString("oper_id");
				if (oper != null){
					turno.setOperativo(oper.equals(Constantes.ACTIVO)?true:false);
				}else{//==null
					/*if (turno.getHoraFin().compareTo(turno.getHoraIni())>0){//de 1 dia de duracion adm u ope
						//no se si es adm u ope?????????
					}else{//2 dias
						turno.setOperativo(true);
					}*/
				}
				//turno.setOperativo(oper != null ? oper.equals(Constantes.ACTIVO) : false);
				String control = rs.getString("scontrol_id");
				if (control!= null){
					turno.setControla(control.equals(Constantes.ACTIVO)?true:false);
				}
				//turno.setControla(control != null ? control.equals(Constantes.ACTIVO) : true);
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
		return turno;
	}
	
	/**
	 * Metodo encargado de buscar el turno no controlado operativo de duracion de 2 dias de un determinado trabajador para una
	 * fecha especifica. Join con la tabla T45Turno.
	 * @param dbpool 
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            String. Numero de registro del trabajador.
	 * @param fecha
	 *            String. Fecha para la cual se busca el turno del trabajador.
	 * @return Bean de la clase BeanTurnoTrabajo.
	 * @throws SQLException
	 */
	public BeanTurnoTrabajo joinWithT45ByCodPersFecha_NoControlado_Oper2dias(String dbpool,
			String codPers, String fecha) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;

		String fechaCompara = Utiles.toYYYYMMDD(fecha);
		BeanTurnoTrabajo turno = null;

		try {
			strSQL.append("select 	tp.u_organ, tp.turno, tp.fini, tp.sonom_id, ")
					.append( "        tp.ffin, t.h_inicio, t.h_fin, t.tolera_turno, t.hlimit, t.oper_id, ")
					.append( "        t.h_inirefr, t.h_finref, t.min_refr, t.scontrol_id, t.dias_int , tp.fcreacion , tp.cuser_crea , tp.fmod , tp.cuser_mod, t.des_turno ")
					.append( "from    t1270turnoperson tp, " ).append( "        t45turno t ")
					.append( "where   tp.cod_pers = ? and ")
					.append( "        tp.turno > '' ")
					.append( "  and   tp.fini = '" ).append( fechaCompara)
					.append( "' and " )
					.append( "	    tp.turno = t.cod_turno and ")
					.append( "        tp.est_id = ? ")
					.append( " and t.scontrol_id='0' and t.oper_id='1' and t.h_fin<=t.h_inicio ");

			con = getConnection(dbpool);		
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, Constantes.ACTIVO);			
			rs = pre.executeQuery();
			log.debug("joinWithT45ByCodPersFecha_NoControlado_Oper2dias(strSQL): "+ strSQL.toString());
			log.debug("registro: "+ codPers);
			if (rs.next()) {
				turno = new BeanTurnoTrabajo();
				turno.setDescTurno(rs.getString("des_turno"));
				turno.setCodUOrg(rs.getString("u_organ"));
				turno.setFechaIni(rs.getTimestamp("fini"));
				turno.setFechaFin(rs.getTimestamp("ffin"));
				turno.setHoraIni(rs.getString("h_inicio"));
				turno.setHoraFin(rs.getString("h_fin"));
				turno.setTurno(rs.getString("turno"));
				turno.setTolera(rs.getInt("tolera_turno"));
				turno.setHoraLimite(rs.getString("hlimit"));
				turno.setHoraIniRefrigerio(rs.getString("h_inirefr"));
				turno.setHoraFinRefrigerio(rs.getString("h_finref"));
				turno.setMinutosRefrigerio(rs.getInt("min_refr"));
				turno.setDuracion(rs.getInt("dias_int"));
				turno.setFechaCreacion(rs.getTimestamp("fcreacion"));
				turno.setCodUsuCreacion(rs.getString("cuser_crea"));
				turno.setFechaModificacion(rs.getTimestamp("fmod"));
				turno.setCodUsuModificacion(rs.getString("cuser_mod"));				
				String oper = rs.getString("oper_id");
				if (oper != null){
					turno.setOperativo(oper.equals(Constantes.ACTIVO)?true:false);
				}else{//==null
					/*if (turno.getHoraFin().compareTo(turno.getHoraIni())>0){//de 1 dia de duracion adm u ope
						//no se si es adm u ope?????????
					}else{//2 dias
						turno.setOperativo(true);
					}*/
				}
				//turno.setOperativo(oper != null ? oper.equals(Constantes.ACTIVO) : false);
				String control = rs.getString("scontrol_id");
				if (control!= null){
					turno.setControla(control.equals(Constantes.ACTIVO)?true:false);
				}
				//turno.setControla(control != null ? control.equals(Constantes.ACTIVO) : true);
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
		return turno;
	}
	//FIN ICAPUNAY-PAS20144EB20000144-Optimizacion Labor Excepcional-Memo 31-2013-4F3100
	
	//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
	/**
	 * Metodo encargado de buscar el turno NO controlado (operativo/administrativo) activo/inactivo de duracion de 2 dias de un determinado trabajador para una
	 * fecha especifica. Join con la tabla T45Turno.
	 * @param dbpool 
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            String. Numero de registro del trabajador.
	 * @param fecha
	 *            String. Fecha para la cual se busca el turno del trabajador.
	 * @return Bean de la clase BeanTurnoTrabajo.
	 * @throws SQLException
	 */
	public BeanTurnoTrabajo joinWithT45ByCodPersFecha_NoControlado_ActInact_OperAdm2dias(String dbpool,
			String codPers, String fecha) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;

		String fechaCompara = Utiles.toYYYYMMDD(fecha);
		BeanTurnoTrabajo turno = null;

		try {
			strSQL.append("select 	tp.u_organ, tp.turno, tp.fini, tp.sonom_id, ")
					.append( "        tp.ffin, t.h_inicio, t.h_fin, t.tolera_turno, t.hlimit, t.oper_id, ")
					.append( "        t.h_inirefr, t.h_finref, t.min_refr, t.scontrol_id, t.dias_int , tp.fcreacion , tp.cuser_crea , tp.fmod , tp.cuser_mod, t.des_turno ")
					.append( "from    t1270turnoperson tp, " ).append( "        t45turno t ")
					.append( "where   tp.cod_pers = ? and ")
					.append( "        tp.turno > '' ")
					.append( "  and   tp.fini = '" ).append( fechaCompara)
					.append( "' and " )
					.append( "	    tp.turno = t.cod_turno and tp.turno in (select t99codigo from t99codigos where t99cod_tab=? and t99tip_desc=? and t99estado=?) and ")
					.append( "        tp.est_id in (?,?) ")
					.append( " and t.scontrol_id='0' and t.oper_id in ('0','1') and ( (t.h_fin<=t.h_inicio) or cast(to_date(t.h_fin,'%H:%M:%S') - to_date(t.h_inicio,'%H:%M:%S') as interval minute(9) to minute)='1') ");
			//cast(to_date(t.h_fin,'%H:%M:%S') - to_date(t.h_inicio,'%H:%M:%S') as interval minute(9) to minute)='1' para obtener turno CA1 (00:00:00-00:01:00)

			con = getConnection(dbpool);		
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, Constantes.CODTAB_VISUALIZAR_TURNOS_NOCONTROLADOS);
			pre.setString(3, Constantes.T01DETALLE);
			pre.setString(4, Constantes.ACTIVO);
			pre.setString(5, Constantes.ACTIVO);
			pre.setString(6, Constantes.INACTIVO);	
			rs = pre.executeQuery();
			log.debug("joinWithT45ByCodPersFecha_NoControlado_ActInact_OperAdm2dias(strSQL): "+ strSQL.toString());
			log.debug("registro: "+ codPers);
			if (rs.next()) {
				turno = new BeanTurnoTrabajo();
				turno.setDescTurno(rs.getString("des_turno"));
				turno.setCodUOrg(rs.getString("u_organ"));
				turno.setFechaIni(rs.getTimestamp("fini"));
				turno.setFechaFin(rs.getTimestamp("ffin"));
				turno.setHoraIni(rs.getString("h_inicio"));
				turno.setHoraFin(rs.getString("h_fin"));
				turno.setTurno(rs.getString("turno"));
				turno.setTolera(rs.getInt("tolera_turno"));
				turno.setHoraLimite(rs.getString("hlimit"));
				turno.setHoraIniRefrigerio(rs.getString("h_inirefr"));
				turno.setHoraFinRefrigerio(rs.getString("h_finref"));
				turno.setMinutosRefrigerio(rs.getInt("min_refr"));
				turno.setDuracion(rs.getInt("dias_int"));
				turno.setFechaCreacion(rs.getTimestamp("fcreacion"));
				turno.setCodUsuCreacion(rs.getString("cuser_crea"));
				turno.setFechaModificacion(rs.getTimestamp("fmod"));
				turno.setCodUsuModificacion(rs.getString("cuser_mod"));				
				String oper = rs.getString("oper_id");
				if (oper != null){
					turno.setOperativo(oper.equals(Constantes.ACTIVO)?true:false);
				}
				String control = rs.getString("scontrol_id");
				if (control!= null){
					turno.setControla(control.equals(Constantes.ACTIVO)?true:false);
				}				
			} 
		}

		catch (Exception e) {
			log.error("**** SQL ERROR joinWithT45ByCodPersFecha_NoControlado_ActInact_OperAdm2dias ()**** "+ e.toString());
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
		return turno;
	}
	
	/**
	 * Metodo encargado de buscar el turno NO controlado (operativo/administrativo) activo/inactivo de duracion de 1 dia de un determinado trabajador para una
	 * fecha especifica. Join con la tabla T45Turno.
	 * @param dbpool 
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            String. Numero de registro del trabajador.
	 * @param fecha
	 *            String. Fecha para la cual se busca el turno del trabajador.
	 * @return Bean de la clase BeanTurnoTrabajo.
	 * @throws SQLException
	 */
	public BeanTurnoTrabajo joinWithT45ByCodPersFecha_NoControlado_ActInact_OperAdm1dia(String dbpool,
			String codPers, String fecha) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;

		String fechaCompara = Utiles.toYYYYMMDD(fecha);
		BeanTurnoTrabajo turno = null;

		try {
			strSQL.append("select 	tp.u_organ, tp.turno, tp.fini, tp.sonom_id, ")
					.append( "        tp.ffin, t.h_inicio, t.h_fin, t.tolera_turno, t.hlimit, t.oper_id, ")
					.append( "        t.h_inirefr, t.h_finref, t.min_refr, t.scontrol_id, t.dias_int , tp.fcreacion , tp.cuser_crea , tp.fmod , tp.cuser_mod, t.des_turno ")
					.append( "from    t1270turnoperson tp, " ).append( "        t45turno t ")
					.append( "where   tp.cod_pers = ? and ")
					.append( "        tp.turno > '' ")
					.append( "  and   tp.fini <= '" ).append( fechaCompara)
					.append( "' and " ).append( "        tp.ffin >= '")
					.append( fechaCompara ).append( "' and " )
					.append( "	    tp.turno = t.cod_turno and tp.turno in (select t99codigo from t99codigos where t99cod_tab=? and t99tip_desc=? and t99estado=?) and ")
					.append( "        tp.est_id in (?,?) ")
					.append( " and t.scontrol_id='0' and t.oper_id in ('0','1') and t.h_fin>t.h_inicio ");					

			con = getConnection(dbpool);		
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, Constantes.CODTAB_VISUALIZAR_TURNOS_NOCONTROLADOS);
			pre.setString(3, Constantes.T01DETALLE);
			pre.setString(4, Constantes.ACTIVO);
			pre.setString(5, Constantes.ACTIVO);
			pre.setString(6, Constantes.INACTIVO);			
			rs = pre.executeQuery();
			log.debug("joinWithT45ByCodPersFecha_NoControlado_ActInact_OperAdm1dia(strSQL): "+ strSQL.toString());
			log.debug("registro: "+ codPers);
			if (rs.next()) {
				turno = new BeanTurnoTrabajo();
				turno.setDescTurno(rs.getString("des_turno"));
				turno.setCodUOrg(rs.getString("u_organ"));
				turno.setFechaIni(rs.getTimestamp("fini"));
				turno.setFechaFin(rs.getTimestamp("ffin"));
				turno.setHoraIni(rs.getString("h_inicio"));
				turno.setHoraFin(rs.getString("h_fin"));
				turno.setTurno(rs.getString("turno"));
				turno.setTolera(rs.getInt("tolera_turno"));
				turno.setHoraLimite(rs.getString("hlimit"));
				turno.setHoraIniRefrigerio(rs.getString("h_inirefr"));
				turno.setHoraFinRefrigerio(rs.getString("h_finref"));
				turno.setMinutosRefrigerio(rs.getInt("min_refr"));
				turno.setDuracion(rs.getInt("dias_int"));
				turno.setFechaCreacion(rs.getTimestamp("fcreacion"));
				turno.setCodUsuCreacion(rs.getString("cuser_crea"));
				turno.setFechaModificacion(rs.getTimestamp("fmod"));
				turno.setCodUsuModificacion(rs.getString("cuser_mod"));				
				String oper = rs.getString("oper_id");
				if (oper != null){
					turno.setOperativo(oper.equals(Constantes.ACTIVO)?true:false);
				}
				String control = rs.getString("scontrol_id");
				if (control!= null){
					turno.setControla(control.equals(Constantes.ACTIVO)?true:false);
				}			
			} 
		}

		catch (Exception e) {
			log.error("**** SQL ERROR joinWithT45ByCodPersFecha_NoControlado_ActInact_OperAdm1dia() **** "+ e.toString());
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
		return turno;
	}
	//FIN ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados

}