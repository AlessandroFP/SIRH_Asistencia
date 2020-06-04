package pe.gob.sunat.sp.asistencia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.sol.BeanFechaHora;
import pe.gob.sunat.sol.dao.DAOAccesoBD;
import pe.gob.sunat.sp.asistencia.bean.BeanProceso;
import pe.gob.sunat.sp.dao.T02DAO;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;

/**
 * <p>
 * Title: T1277DAO
 * </p>
 * <p>
 * Description: Clase encargada de administrar las consultas a la tabla
 * T1277Solicitud
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

public class T1277DAO extends DAOAccesoBD {

	private static final Logger log = Logger.getLogger(T1277DAO.class);

	private static final String SELECT_SOLICITUD = "SELECT anno,numero,u_organ,cod_pers,asunto,ffinicio,cargo,licencia FROM   t1277solicitud WHERE anno = ? and  numero =?"  ;
	
	//AGONZALESF -PAS20171U230200001 - solicitud de reintegro     Solicitudes para ver documentos aprobados en reintegro	
	//AGONZALES - PAS20191U230200011 -solicitud de reintegro
	private final StringBuffer  FIND_DOC_APROB_SOLICITUD =new StringBuffer(" ") 
	.append(" select m.descrip as tipo, ")
	.append(" date(l.ffinicio) as fechaInicio,")
	.append(" date(l.ffin) as fechaFin, ")
	.append(" l.observ as motivo ")
	.append(" from t1277solicitud l,t1279tipo_mov m ")
	.append(" where l.licencia=m.mov ")
	.append(" and l.licencia in (SELECT  t99descrip from t99codigos   WHERE t99cod_tab='R07' and t99tip_desc ='D' and t99tipo='SOLIC' and t99estado ='1'    )")	
	.append(" and l.cod_pers=? ")
	.append(" AND ") 
	.append(" ( ")
	.append("  	l.ffinicio between ? and ?")
	.append("   or l.ffin  between ? and ?")
	.append(" 	or  ? between l.ffinicio and l.ffin")
	.append(" 	or  ? between l.ffinicio and l.ffin")
	.append(" )");
	
	 
	private static final StringBuffer SELECT_OMISION_ANULACION =  new StringBuffer(" ")
	.append(" select s.ffinicio from t1277solicitud s, t1455sol_seg ss")
	.append(" where (s.licencia='10' or s.licencia = '60' ) and s.anno=ss.anno")
	.append(" 			 and s.numero=ss.numero")
	.append(" 			 and s.seguim_act=ss.num_seguim")
	.append(" 			 and ss.accion_id='2' and ss.estado_id='2'")
	.append(" 			 and s.cod_pers = ?");
	 
	public T1277DAO() {
	}

	/**
	 * Metodo encargado de listar los datos de las solicitudes con estado
	 * "Pendiente" generadas por un trabajador, filtradas por un tipo y un
	 * criterio con un valor determinado. Join con las tablas T02Perdp,
	 * T1455Sol_Seg y T1279Tipo_Mov.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            String. Numero de registro del trabajador.
	 * @param tipo
	 *            String. Codigo del tipo de solicitud.
	 * @param criterio
	 *            String. Criterio de filtro de los registros.
	 * @param valor
	 *            String. Valor de filtro para el criterio dado.
	 * @return ArrayList conteniendo los registros cargados en HashMaps.
	 * @throws SQLException
	 */
	public ArrayList findSolicitudesIniciadas(String dbpool, String codPers,
			String tipo, String criterio, String valor, String usuario)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;

		try {
			//prac-asanchez: se necesita el campo dias
			strSQL.append("select   s.cod_pers, m.descrip, s.anno, s.numero, s.u_organ, "
					).append( "        s.fecha fechaSol, s.asunto, s.dias, ss.fecha_recep ultDeriv, "
					).append( "        ss.estado_id, ss.cuser_dest "
					).append( "from    t1277solicitud s, "
					).append( "        t1455sol_seg ss, "
					).append( "        t1279tipo_mov m "
					).append( "where   (s.cod_pers = ? or s.cuser_crea = ? ) and "
					).append( "        (ss.estado_id = ? or ss.estado_id = ?) and "
					).append( "        s.anno = ss.anno and "
					).append( "        s.numero = ss.numero and "
					).append( "        s.u_organ > '000000' and "
					).append( "        s.cod_pers = ss.cod_pers and "
					).append( "        s.licencia = m.mov and "		
					).append( "        s.seguim_act = ss.num_seguim ");

			if (!tipo.equals("-1")) {
				strSQL.append(" and s.licencia = '" ).append( tipo.trim().toUpperCase()
						).append( "' ");
			}
			if (criterio.equals("0")) {
				strSQL.append(" and s.anno || '-' || s.numero like '%"
						).append( valor.trim().toUpperCase() ).append( "%' ");
			}
			if (criterio.equals("1")) {
				strSQL.append(" and upper(s.asunto) like '%"
						).append( valor.trim().toUpperCase() ).append( "%' ");
			}
			if (criterio.equals("2")) {
				String f = Utiles.toYYYYMMDD(valor.trim());
				strSQL.append(" and DATE(s.fecha) <= DATE('" ).append( f ).append( "') ");
			}
			if (criterio.equals("3")) {
				String f = Utiles.toYYYYMMDD(valor.trim());
				strSQL.append(" and DATE(ss.fecha_recep) <= DATE('" ).append( f ).append( "') ");
			}
			if (criterio.equals("4")) {
				strSQL.append(" and ss.cuser_dest like '"
						).append( valor.trim().toUpperCase() ).append( "%' ");
			}
			strSQL.append(" order by fechaSol desc ");

			log.debug(strSQL.toString());
			
			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, usuario);
			pre.setString(3, Constantes.ESTADO_SEGUIMIENTO);
			pre.setString(4, Constantes.ESTADO_CONCLUIDA);
			rs = pre.executeQuery();

			T02DAO dao = new T02DAO();
			lista = new ArrayList();
			HashMap solicitud = null;
			String responsable = "";
			while (rs.next()) {

				solicitud = new HashMap();

				solicitud.put("codPers", rs.getString("cod_pers"));
				solicitud.put("descrip", rs.getString("descrip").trim());
				solicitud.put("anno", rs.getString("anno"));
				solicitud.put("numero", rs.getString("numero"));
				solicitud.put("uorgan", rs.getString("u_organ"));

				String numSol = (String) solicitud.get("anno") + "-" + (String) solicitud.get("numero");

				solicitud.put("numSol", numSol);
				solicitud.put("fechaSol", rs.getTimestamp("fechaSol"));
				solicitud.put("asunto", rs.getString("asunto").trim());
				solicitud.put("ultDeriv", rs.getTimestamp("ultDeriv"));
				solicitud.put("estId", rs.getString("estado_id"));

				responsable = rs.getString("cuser_dest");
				if (responsable != null && !responsable.equals("")) {
					solicitud.put("codResp", responsable);
					solicitud.put("responsable", dao
							.findNombreCompletoByCodPers(dbpool, responsable
									.trim()));
				}

				String estado = "Seguimiento";
				if (((String) solicitud.get("estId")).trim().equals(
						Constantes.ESTADO_CONCLUIDA)) {
					estado = "Concluida";
				}
				solicitud.put("estado", estado);

				lista.add(solicitud);
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
		return lista;
	}

	/**
	 * 
	 * @param dbpool
	 * @param tipo
	 * @param criterio
	 * @param valor
	 * @param seguridad
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList findConsultaGeneral(HashMap datos, HashMap seguridad)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;

		try {

			String dbpool = (String) datos.get("dbpool");
			String tipo = (String) datos.get("tipo");
			String criterio = (String) datos.get("criterio");
			String valor = (String) datos.get("valor");
			String criterio2 = (String) datos.get("criterio2");
			String valor2 = (String) datos.get("valor2");
			String estado_id = (String) datos.get("estado_id");
			//prac-asanchez
			String valorFechaFin = (String) datos.get("valorFechaFin");

			strSQL.append("select   s.cod_pers, m.descrip, s.anno, s.numero, s.u_organ, "
					).append( "        s.fecha fechaSol, s.asunto, s.dias, ss.fecha_recep ultDeriv, "
					).append( "        ss.accion_id, ss.estado_id, ss.cuser_dest "
					).append( "from    t1277solicitud s, "
					).append( "        t1455sol_seg ss, "
					).append( "        t1279tipo_mov m "
					).append( "where   s.cod_pers = ss.cod_pers and "
					).append( "        s.anno = ss.anno and "
					).append( "        s.numero = ss.numero and "
					).append( "        s.seguim_act = ss.num_seguim and "
					).append( "        s.licencia = m.mov ");

			if (criterio2.equals("1")) {
				strSQL.append(" and s.u_organ like '" ).append( valor2.trim().toUpperCase()
						).append( "%' ");
			}
			if (criterio2.equals("0")) {
				strSQL.append(" and s.cod_pers like '"
						).append( valor2.trim().toUpperCase() ).append( "%' ");
			}
			if (!tipo.equals("-1")) {
				strSQL.append(" and s.licencia = '" ).append( tipo.trim().toUpperCase()
						).append( "' ");
			}
			if (criterio.equals("0")) {
				strSQL.append(" and s.anno || '-' || s.numero like '%"
						).append( valor.trim().toUpperCase() ).append( "%' ");
			}
			
			if (criterio.equals("1")) {
				strSQL.append(" and upper(s.asunto) like '%"
						).append( valor.trim().toUpperCase() ).append( "%' ");
			}
			if (criterio.equals("2")) {
				String f = Utiles.toYYYYMMDD(valor.trim());
				strSQL.append(" and DATE(s.fecha) <= DATE('" ).append( f ).append( "') ");
			}
			if (criterio.equals("3")) {
				String f = Utiles.toYYYYMMDD(valor.trim());
				strSQL.append(" and DATE(ss.fecha_recep) <= DATE('" ).append( f ).append( "') ");
			}
			if (criterio.equals("4")) {
				strSQL.append(" and ss.cuser_dest like '"
						).append( valor.trim().toUpperCase() ).append( "%' ");
			}
			
			//prac-asanchez
			if (criterio.equals("5")) {
				String f = Utiles.toYYYYMMDD(valor.trim());
				String fFin = Utiles.toYYYYMMDD(valorFechaFin.trim());
				strSQL.append(" and DATE(s.fecha) >= DATE('" ).append( f ).append( "') ")
				.append(" and DATE(s.fecha) <= DATE('" ).append( fFin ).append( "') ");
			}
						
					
			if (!estado_id.equals("-1")) {
				strSQL.append(" and ss.estado_id = '").append(estado_id).append( "' ");
			}			

			if (seguridad != null) {
				HashMap roles = (HashMap) seguridad.get("roles");
				String uoSeg = (String) seguridad.get("uoSeg");
				String uoAO = (String) seguridad.get("uoAO");//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

				if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
					strSQL.append(" and 1=1 ");
				} else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null) {
					strSQL.append(" and ((s.u_organ like '" ).append( uoSeg ).append( "') ");
					strSQL.append(" or (s.u_organ in  (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '" )
					.append( uoAO ).append( "'))) ");
					
				}else if (//roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null ||
						roles.get(Constantes.ROL_JEFE) != null) {
					strSQL.append(" and s.u_organ like '" ).append( uoSeg ).append( "' ");
				} else {
					strSQL.append(" and 1=2 ");
				}
			}

			strSQL.append(" order by fechaSol desc ");
			
			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			
			log.debug("findConsultaGeneral(strSQL): " + strSQL);
			
			rs = pre.executeQuery();

			T02DAO dao = new T02DAO();

			lista = new ArrayList();
			HashMap solicitud = null;
			String emisor = "";
			String responsable = "";
			while (rs.next()) {

				solicitud = new HashMap();

				solicitud.put("codPers", rs.getString("cod_pers"));
				solicitud.put("descrip", rs.getString("descrip").trim());
				solicitud.put("anno", rs.getString("anno"));
				solicitud.put("numero", rs.getString("numero"));
				solicitud.put("uorgan", rs.getString("u_organ"));
				String numSol = (String) solicitud.get("anno") + "-" + (String) solicitud.get("numero");
				solicitud.put("numSol", numSol);
				solicitud.put("fechaSol", rs.getTimestamp("fechaSol"));
				solicitud.put("asunto", rs.getString("asunto").trim());
				solicitud.put("ultDeriv", rs.getTimestamp("ultDeriv"));
				solicitud.put("dias", rs.getString("dias"));
				solicitud.put("estId", rs.getString("estado_id"));
				solicitud.put("accion_id", rs.getString("accion_id"));

				emisor = rs.getString("cod_pers");
				responsable = rs.getString("cuser_dest");
				if (emisor != null && !emisor.equals("")) {
					solicitud.put("emisor", dao.findNombreCompletoByCodPers(
							dbpool, emisor.trim()));
				}
				if (responsable != null && !responsable.equals("")) {
					solicitud.put("codResp", responsable);
					solicitud.put("responsable", dao
							.findNombreCompletoByCodPers(dbpool, responsable
									.trim()));
				}

				String estado = "Seguimiento";
				if (((String) solicitud.get("estId")).trim().equals(
						Constantes.ESTADO_CONCLUIDA)) {
					estado = "Concluida";
				}
				solicitud.put("estado", estado);
				
				//prac-asanchez
				String accion = "";
				//solo pintara el valor de accion, si el estado es CONCLUIDA
				if(estado.equals("Concluida")){
					if (((String) solicitud.get("accion_id")).trim().equals(Constantes.ACCION_INICIAR)) {
						accion = "Iniciada";
					}
					if (((String) solicitud.get("accion_id")).trim().equals(Constantes.ACCION_APROBAR)) {
						accion = "Aprobada";
					}
					if (((String) solicitud.get("accion_id")).trim().equals(Constantes.ACCION_RECHAZAR)) {
						accion = "Rechazada";
					}
					if (((String) solicitud.get("accion_id")).trim().equals(Constantes.ACCION_DERIVAR_RRHH)) {
						accion = "Derivada a RRHH";
					}
				}

				
				solicitud.put("accion", accion);
				
				lista.add(solicitud);
			}
			log.debug("lista: " + lista);
 
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
	 * Mï¿½todo encargado de listar los datos de las solicitudes con estado
	 * "Concluida" generadas por un trabajador, filtradas por un tipo y un
	 * criterio con un valor determinado. Join con las tablas T02Perdp,
	 * T1455Sol_Seg y T1279Tipo_Mov.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            String. Nï¿½mero de registro del trabajador.
	 * @param tipo
	 *            String. Cï¿½digo del tipo de solicitud.
	 * @param criterio
	 *            String. Criterio de filtro de los registros.
	 * @param valor
	 *            String. Valor de filtro para el criterio dado.
	 * @return ArrayList conteniendo los registros cargados en HashMaps.
	 * @throws SQLException
	 */
	public ArrayList findSolicitudesConcluidas(String dbpool, String codPers,
			String tipo, String criterio, String valor, String usuario)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;

		try {

			strSQL.append("select 	s.cod_pers, m.descrip, s.anno, s.numero, s.u_organ, "
					).append( "        s.fecha fechaSol, s.asunto, ss.fecha_recep ultDeriv, "
					).append( "        p1.t02ap_pate || p1.t02ap_mate || ', ' || p1.t02nombres emisor, "
					).append( "        p.t02ap_pate || p.t02ap_mate || ', ' || p.t02nombres responsable, "
					).append( "        ss.estado_id "
					).append( "from    t02perdp p, "
					).append( "        t02perdp p1, "
					).append( "        t1277solicitud s, "
					).append( "        t1455sol_seg ss, "
					).append( "        t1279tipo_mov m "
					).append( "where   (s.cod_pers = ? or "
					).append( "        s.cuser_crea = ?) and "
					).append( "        ss.estado_id = ? and "
					).append( "        s.cod_pers = ss.cod_pers and "
					).append( "        s.anno = ss.anno and "
					).append( "        s.numero = ss.numero and "
					).append( "        s.seguim_act = ss.num_seguim and "
					).append( "        ss.cuser_orig = p.t02cod_pers and "
					).append( "        s.cod_pers = p1.t02cod_pers and "
					).append( "        s.licencia = m.mov ");

			if (!tipo.equals("-1")) {
				strSQL.append(" and s.licencia = '" ).append( tipo.trim().toUpperCase()
						).append( "' ");
			}

			if (criterio.equals("0")) {
				strSQL.append(" and s.anno || '-' || s.numero like '%"
						).append( valor.trim().toUpperCase() ).append( "%' ");
			}
			if (criterio.equals("1")) {
				strSQL.append(" and upper(s.asunto) like '%"
						).append( valor.trim().toUpperCase() ).append( "%' ");
			}
			if (criterio.equals("2")) {
				String f = Utiles.toYYYYMMDD(valor.trim());
				strSQL.append(" and DATE(s.fecha) <= DATE('" ).append( f ).append( "') ");
			}
			if (criterio.equals("3")) {
				String f = Utiles.toYYYYMMDD(valor.trim());
				strSQL.append(" and DATE(ss.fecha_recep) <= DATE('" ).append( f ).append( "') ");
			}
			if (criterio.equals("4")) {
				strSQL.append(" and p.t02ap_pate || ' ' || p.t02ap_mate || ', ' || p.t02nombres like '%"
						).append( valor.trim().toUpperCase() ).append( "%' ");
			}
			strSQL.append(" order by ss.fecha_recep desc ");

			log.debug(strSQL.toString());
			
			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, usuario);
			pre.setString(3, Constantes.ESTADO_CONCLUIDA);
			rs = pre.executeQuery();

			lista = new ArrayList();
			HashMap solicitud = null;
			while (rs.next()) {

				solicitud = new HashMap();

				solicitud.put("codPers", rs.getString("cod_pers"));
				solicitud.put("descrip", rs.getString("descrip").trim());
				solicitud.put("anno", rs.getString("anno"));
				solicitud.put("numero", rs.getString("numero"));
				solicitud.put("uorgan", rs.getString("u_organ"));

				String numSol = (String) solicitud.get("anno") + "-"
						+ (String) solicitud.get("numero");

				solicitud.put("numSol", numSol);
				solicitud.put("fechaSol", rs.getTimestamp("fechaSol"));
				solicitud.put("asunto", rs.getString("asunto").trim());
				solicitud.put("ultDeriv", rs.getTimestamp("ultDeriv"));
				solicitud.put("emisor", rs.getString("emisor").trim());
				solicitud
						.put("responsable", rs.getString("responsable").trim());
				solicitud.put("estId", rs.getString("estado_id"));

				String estado = "Seguimiento";
				if (((String) solicitud.get("estId")).trim().equals(
						Constantes.ESTADO_CONCLUIDA)) {
					estado = "Concluida";
				}
				solicitud.put("estado", estado);

				lista.add(solicitud);
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
	 * Metodo encargado de listar los datos de las solicitudes con estado
	 * "Pendiente" destinadas a un trabajador, filtradas por un tipo y un
	 * criterio con un valor determinado. Join con las tablas T02Perdp,
	 * T1455Sol_Seg y T1279Tipo_Mov.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            String. Numero de registro del trabajador.
	 * @param tipo
	 *            String. Codigo del tipo de solicitud.
	 * @param criterio
	 *            String. Criterio de filtro de los registros.
	 * @param valor
	 *            String. Valor de filtro para el criterio dado.
	 * @return ArrayList conteniendo los registros cargados en HashMaps.
	 * @throws SQLException
	 */
	public ArrayList findSolicitudesRecibidas(String dbpool, String codPers,
			String tipo, String criterio, String valor) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;

		try {

			strSQL.append("select   s.cod_pers, m.descrip, s.anno, "
					).append( "        s.numero, s.u_organ u_organ, "
					).append( "        s.fecha fechaSol, "
					).append( "        s.asunto,	ss.fecha_recep ultDeriv, "
					//).append( "        ss.estado_id, ss.cuser_orig, ss.cuser_dest, ss.u_organ uo_orig " //ICAPUNAY 05/10/2012 AJUSTES AOM 06A4T11 LABOR EXCEPCIONAL
					//).append( "        ss.estado_id, ss.cuser_orig, ss.cuser_dest, ss.u_organ uo_orig, s.est_id " //ICAPUNAY 05/10/2012 AJUSTES AOM 06A4T11 LABOR EXCEPCIONAL //ICR - 28/11/2012 PAS20124E550000064 labor excepcional
					).append( "        ss.estado_id, ss.cuser_orig, ss.cuser_dest, ss.u_organ uo_orig, s.est_id, p.t02cod_rel, " //ICR - 28/11/2012 PAS20124E550000064 labor excepcional		
					//).append( "from    t1277solicitud s, "//ICR - 28/11/2012 PAS20124E550000064 labor excepcional
				    ).append( " s.licencia " //RMONTES 14/06/2017 Solicitudes de reintegro
					).append( "from    t1277solicitud s,t02perdp p, "//ICR - 28/11/2012 PAS20124E550000064 labor excepcional		
					).append( "        t1455sol_seg ss, " 
					).append( "        t1279tipo_mov m "
					).append( "where   ss.cuser_dest = ? and "
					).append( "        ss.estado_id != ? and "
					).append( "        s.cod_pers = ss.cod_pers and "
					).append( "        s.anno = ss.anno and "
					).append( "        s.numero = ss.numero and "
					).append( "        s.seguim_act = ss.num_seguim and "
					).append( "        s.cod_pers = p.t02cod_pers and "//ICR - 28/11/2012 PAS20124E550000064 labor excepcional
					).append( "        s.licencia = m.mov ");

			if (!tipo.equals("-1")) {
				strSQL.append(" and s.licencia = '" ).append( tipo.trim().toUpperCase()
						).append( "' ");
			}

			if (criterio.equals("0")) {
				strSQL.append(" and s.anno || '-' || s.numero like '%"
						).append( valor.trim().toUpperCase() ).append( "%' ");
			}
			if (criterio.equals("1")) {
				strSQL.append(" and upper(s.asunto) like '%"
						).append( valor.trim().toUpperCase() ).append( "%' ");
			}
			if (criterio.equals("2")) {
				String f = Utiles.toYYYYMMDD(valor.trim());
				strSQL.append(" and DATE(s.fecha) <= DATE('" ).append( f ).append( "') ");
			}
			if (criterio.equals("3")) {
				strSQL.append(" and s.cod_pers like '" ).append( valor.trim().toUpperCase()
						).append( "%' ");
			}
			if (criterio.equals("4")) {
				String f = Utiles.toYYYYMMDD(valor.trim());
				strSQL.append(" and DATE(ss.fecha_recep) <= DATE('" ).append( f ).append( "') ");
			}
			if (criterio.equals("5")) {
				strSQL.append(" and ss.cuser_orig like '"
						).append( valor.trim().toUpperCase() ).append( "%' ");
			}
			strSQL.append(" order by ss.fecha_recep desc ");

			log.debug(strSQL.toString());
			
			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, Constantes.ESTADO_CONCLUIDA);
			rs = pre.executeQuery();

			T02DAO dao = new T02DAO();
			lista = new ArrayList();
			HashMap solicitud = null;
			String emisor = "";
			String responsable = "";
			while (rs.next()) {

				solicitud = new HashMap();

				solicitud.put("codPers", rs.getString("cod_pers"));
				solicitud.put("descrip", rs.getString("descrip").trim());
				solicitud.put("anno", rs.getString("anno"));
				solicitud.put("numero", rs.getString("numero"));
				solicitud.put("uorgan", rs.getString("u_organ"));
				solicitud.put("uoOrig", rs.getString("uo_orig"));

				String numSol = (String) solicitud.get("anno") + "-"
						+ (String) solicitud.get("numero");

				solicitud.put("numSol", numSol);
				solicitud.put("fechaSol", rs.getTimestamp("fechaSol"));
				solicitud.put("asunto", rs.getString("asunto").trim());
				solicitud.put("ultDeriv", rs.getTimestamp("ultDeriv"));
				solicitud.put("estId", rs.getString("estado_id"));
				solicitud.put("estadoSol", rs.getString("est_id"));//ICAPUNAY 05/10/2012 AJUSTES AOM 06A4T11 LABOR EXCEPCIONAL
				solicitud.put("regimenCol", rs.getString("t02cod_rel"));//ICR - 28/11/2012 PAS20124E550000064 labor excepcional
				solicitud.put("licencia", rs.getString("licencia"));//RMONTES 14/06/2017 Solicitudes de reintegro

				emisor = rs.getString("cod_pers");
				responsable = rs.getString("cuser_orig");
				if (emisor != null && !emisor.equals(""))
					solicitud.put("emisor", dao.findNombreCompletoByCodPers(
							dbpool, emisor.trim()));
				if (responsable != null && !responsable.equals("")) {
					solicitud.put("codResp", responsable);
					solicitud.put("responsable", dao
							.findNombreCompletoByCodPers(dbpool, responsable
									.trim()));
				}

				log.debug("solicitud recibida : "+solicitud);
				
				lista.add(solicitud);
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
	 * Mï¿½todo encargado de buscar los datos de una solicitud especï¿½fica. Join
	 * con las tablas T02Perdp, T1455Sol_Seg y T1279Tipo_Mov.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            String. Nï¿½mero de registro del trabajador.
	 * @param anno
	 *            String. Aï¿½o de la solicitud.
	 * @param numero
	 *            String. Nï¿½mero de la solicitud.
	 * @return HashMap conteniendo los datos de la solicitud.
	 * @throws SQLException
	 */
	public HashMap findSolicitud(String dbpool, String codPers, String anno,
			String numero) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		HashMap solicitud = new HashMap();

		try {

			strSQL.append("select 	 s.cod_pers, m.descrip, s.anno, s.numero, s.u_organ u_organ, "
					).append( "        s.ffinicio, s.ffin, s.observ, s.licencia, m.srrhh_id, s.cuser_crea, "
					).append( "        ss.cuser_orig, ss.cuser_dest, s.anno_vac, s.cod_crea, s.dias, "
					).append( "        s.fecha fechaSol, s.asunto, ss.fecha_recep ultDeriv, s.seguim_act, ss.u_organ uo_orig, "
					).append( "        p.t02ap_pate || p.t02ap_mate || ', ' || p.t02nombres responsable, "
					).append( "        ss.estado_id, m.oblig_id "
					).append( "from    t02perdp p, "
					).append( "        t1277solicitud s, "
					).append( "        t1455sol_seg ss, "
					).append( "        t1279tipo_mov m "
					).append( "where   s.cod_pers = ? and "
					).append( "        s.anno = ? and s.numero = ? and "
					).append( "        s.cod_pers = ss.cod_pers and "
					).append( "        s.anno = ss.anno and "
					).append( "        s.numero = ss.numero and "
					).append( "        s.seguim_act = ss.num_seguim and "
					).append( "        ss.cuser_dest = p.t02cod_pers and "
					).append( "        s.licencia = m.mov ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, anno);
			pre.setString(3, numero);
			rs = pre.executeQuery();

			if (rs.next()) {

				solicitud.put("rrhh", rs.getString("srrhh_id") != null ? rs
						.getString("srrhh_id").trim() : Constantes.INACTIVO);
				solicitud.put("tipo", rs.getString("licencia"));
				solicitud.put("descrip", rs.getString("descrip").trim());
				solicitud.put("codPers", rs.getString("cod_pers"));
				solicitud.put("anno", rs.getString("anno"));
				solicitud.put("numero", rs.getString("numero"));
				solicitud.put("uorgan", rs.getString("u_organ").trim());
				solicitud.put("uoOrig", rs.getString("uo_orig").trim());
				
				String numSol = (String) solicitud.get("anno") + "-"
						+ (String) solicitud.get("numero");

				solicitud.put("numSol", numSol);
				solicitud.put("fechaSol", rs.getTimestamp("fechaSol"));
				solicitud.put("userOrig", rs.getString("cuser_orig"));
				solicitud.put("userDest", rs.getString("cuser_dest"));
				solicitud.put("asunto", rs.getString("asunto").trim());
				solicitud.put("seguim_act", rs.getString("seguim_act"));
				solicitud.put("ultDeriv", rs.getTimestamp("ultDeriv"));
				solicitud.put("responsable", rs.getString("responsable").trim());
				solicitud.put("estId", rs.getString("estado_id"));

				String estado = "Seguimiento";
				if (((String) solicitud.get("estId")).trim().equals(
						Constantes.ESTADO_CONCLUIDA)) {
					estado = "Concluida";
				}
				solicitud.put("estado", estado);
				solicitud.put("ffinicio", rs.getTimestamp("ffinicio"));
				solicitud.put("ffin", rs.getTimestamp("ffin"));
				solicitud.put("annoVac", rs.getString("anno_vac"));
				solicitud.put("txtObs", rs.getString("observ").trim());
				solicitud.put("codCreador",
						rs.getString("cod_crea") != null ? rs
								.getString("cod_crea") : rs
								.getString("cod_pers"));
				solicitud.put("flujo_oblig",
						rs.getString("oblig_id") != null ? rs.getString(
								"oblig_id").trim() : Constantes.ACTIVO);
				solicitud.put("dias", rs.getString("dias"));

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
		return solicitud;
	}

	/**
	 * Mï¿½todo encargado de buscar los registros de seguimiento de una solicitud
	 * especï¿½fica. Join con las tablas T02Perdp, T1455Sol_Seg y T1279Tipo_Mov.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            String. Nï¿½mero de registro del trabajador.
	 * @param anno
	 *            String. Aï¿½o de la solicitud.
	 * @param numero
	 *            String. Nï¿½mero de la solicitud.
	 * @return ArrayList conteniendo los registros cargados en HashMaps.
	 * @throws SQLException
	 */
	public ArrayList findSeguimientos(String dbpool, String codPers,
			String anno, String numero) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;

		try {

			strSQL.append("select   ss.estado_id, ss.fecha_recep, ss.fecha_deriv, ss.accion_id, ss.tobserv, ss.fcreacion, "
					).append( "        ss.cuser_orig, ss.cuser_dest "
					).append( "from    t1277solicitud s, "
					).append( "        t1455sol_seg ss "
					).append( "where   s.anno = ? and "
					).append( "        s.numero = ? and s.cod_pers = ? and "							
					).append( "        s.cod_pers = ss.cod_pers and "
					).append( "        s.anno = ss.anno and "
					).append( "        s.numero = ss.numero "
					).append( "order by ss.fecha_recep desc");

			log.debug(strSQL.toString());
			
			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());			
			pre.setString(1, anno);
			pre.setString(2, numero);
			pre.setString(3, codPers);
			rs = pre.executeQuery();

			T02DAO dao = new T02DAO();
			lista = new ArrayList();
			HashMap solicitud = null;
			String origen = "";
			String destino = "";
			while (rs.next()) {

				solicitud = new HashMap();

				solicitud.put("estId", rs.getString("estado_id"));
				String estado = "Seguimiento";
				if (((String) solicitud.get("estId")).trim().equals(
						Constantes.ESTADO_CONCLUIDA)) {
					estado = "Concluida";
				}
				solicitud.put("estado", estado);

				solicitud.put("accionId", rs.getString("accion_id"));
				String accion = "Iniciar";
				if (((String) solicitud.get("accionId")).trim().equals(
						Constantes.ACCION_RECHAZAR)) {
					accion = "Rechazar";
				}
				if (((String) solicitud.get("accionId")).trim().equals(
						Constantes.ACCION_DERIVAR_RRHH)) {
					accion = "Derivar";
				}
				if (((String) solicitud.get("accionId")).trim().equals(
						Constantes.ACCION_APROBAR)) {
					if (((String) solicitud.get("estId")).trim().equals(
							Constantes.ESTADO_CONCLUIDA)) {
						accion = "Aprobar";
					} else {
						accion = "Derivar";
					}
				}
				solicitud.put("accion", accion);

				solicitud.put("fecha", rs.getTimestamp("fecha_recep"));
				solicitud.put("fechaDeriv", rs.getTimestamp("fecha_deriv"));
				solicitud.put("txtObs", rs.getString("tobserv").trim());

				origen = rs.getString("cuser_orig");
				destino = rs.getString("cuser_dest");

				solicitud.put("userOrig", origen);
				solicitud.put("userDest", destino);

				if (origen != null && !origen.equals("")) {
					solicitud.put("remitente", dao.findNombreCompletoByCodPers(
							dbpool, origen.trim()));
				}
				if (destino != null && !destino.equals("")) {
					solicitud.put("destinatario",
							dao.findNombreCompletoByCodPers(dbpool, destino
									.trim()));
				}

				lista.add(solicitud);
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
	 * Mï¿½todo encargado de buscar los datos del trabajador que tiene la
	 * responsabilidad de aprobar una solicitud de un determinado tipo para una
	 * unidad organizacional especï¿½fica.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param solicitud
	 *            HashMap que contiene el tipo("tipo") y el cï¿½digo de la unidad
	 *            organizacional("uorgan").
	 * @return HashMap con los datos del trabajador aprobador.
	 * @throws SQLException
	 */
	public HashMap findAprobador(String dbpool, HashMap solicitud)
			throws SQLException {

		HashMap aprobador = new HashMap();
		try {

			String tipo = ((String) solicitud.get("tipo")).trim();
			String uo = ((String) solicitud.get("uorgan")).trim();
			String userAprob = findAprobador(dbpool, tipo, uo, Constantes.ACCION_APROBAR);

			HashMap flujoRRHH = new HashMap();
			if (((String) solicitud.get("rrhh")).trim().equals(Constantes.ACTIVO)) {
				flujoRRHH = findFlujoRRHH(dbpool, tipo, uo);
			}

			aprobador.put("rrhh", ((String) solicitud.get("rrhh")).trim());
			aprobador.put("tipo", tipo);
			aprobador.put("uo", uo);
			aprobador.put("aprobador", userAprob);
			//aprobador.put("aprobadorRRHH", userRRHH);
			aprobador.put("flujoRRHH", flujoRRHH);

		} catch (Exception e) {

		}
		return aprobador;
	}

	/**
	 * Mï¿½todo encargado de buscar el cï¿½digo del trabajador que tiene la
	 * responsabilidad de aprobar una solicitud de un determinado tipo para una
	 * unidad organizacional y acciï¿½n especï¿½ficas.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param tipo
	 *            String. Tipo de solicitud.
	 * @param uo
	 *            String. Cï¿½digo de la unidad organizacional
	 * @param accion
	 *            String. Acciï¿½n a validar.
	 * @return String. Cï¿½digo del trabajador aprobador.
	 * @throws SQLException
	 */
	public String findAprobador(String dbpool, String tipo, String uo,
			String accion) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		String aprobador = "";

		try {

			strSQL.append("select cod_personal_ori from t1480sol_flujo "
					).append( "where mov = ? and accion_id = ? and u_organ = ? and "
							).append( " ( cinstancia_aprob = ? or cinstancia_aprob = ? )");
		
			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, tipo);
			pre.setString(2, accion);
			pre.setString(3, uo);
			pre.setString(4, Constantes.ESTACION_INICIAL);
			pre.setString(5, Constantes.ESTACION_UNICA);
			rs = pre.executeQuery();

			if (rs.next()) {
				aprobador = rs.getString("cod_personal_ori");
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
		return aprobador;
	}

	/**
	 * Metodo que devuelve un mapa con el flujo de aprobacion por parte de RRHH
	 * 
	 * @param dbpool
	 * @param tipo
	 * @param uo
	 * @return @throws
	 *         SQLException
	 */
	public HashMap findFlujoRRHH(String dbpool, String tipo, String uo)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		HashMap aprobadores = new HashMap();
		HashMap a = null;
		HashMap b = null;
		try {

			strSQL.append("select cod_personal_ori, cod_personal_des, accion_id, cinstancia_aprob from t1480sol_flujo "
					).append( "where mov = ? and u_organ = ? ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, tipo);
			pre.setString(2, uo);
			rs = pre.executeQuery();

			while (rs.next()) {

				a = (HashMap) aprobadores.get(rs.getString("cod_personal_ori"));
				b = new HashMap();
				if (a != null) {
					b.put("destino", rs.getString("cod_personal_des"));
					b.put("estado", rs.getString("cinstancia_aprob"));
					a.put(rs.getString("accion_id"), b);
				} else {
					a = new HashMap();
					b.put("destino", rs.getString("cod_personal_des"));
					b.put("estado", rs.getString("cinstancia_aprob"));
					a.put(rs.getString("accion_id"), b);
				}
				aprobadores.put(rs.getString("cod_personal_ori"), a);
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
		return aprobadores;
	}

	
	/**
	 * Metodo que inserta registro de Solicitud
	 * @throws SQLException
	 */
	public boolean insertRegistroSolicitud(String dbpool, String annoActual, Integer numero, String codUO, 
			String userOrig, String asunto, String codCate, String tipo, String fechaIni, 
			String fechaFin, int numDias, String annoVac, String ACTIVO, String txtObs, String codCreador,
			String usuario)
			throws SQLException {

		String strUpd = "";
		PreparedStatement pre = null;
		Connection con = null;
		boolean result = false;

		try {

			strUpd = "Insert into t1277solicitud (" +
			"anno,numero,u_organ,cod_pers,asunto,fecha,cargo,licencia,ffinicio,ffin,dias,anno_vac,"+
			"est_id,seguim_act,observ,cod_crea,fcreacion,cuser_crea,fmod,cuser_mod) " + 
            "Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			con = getConnection(dbpool);
			pre = con.prepareStatement(strUpd);
			
			pre.setString(1, annoActual);
			pre.setInt(2, numero.intValue());
			pre.setString(3, codUO);
			pre.setString(4, userOrig);
			pre.setString(5, asunto);
			pre.setTimestamp(6, new java.sql.Timestamp(System.currentTimeMillis()));
			pre.setString(7, codCate);
			pre.setString(8, tipo);
			pre.setTimestamp(9,Utiles.stringToTimestamp(fechaIni + " 00:00:00"));
			pre.setTimestamp(10,Utiles.stringToTimestamp(fechaFin + " 00:00:00"));
			pre.setInt(11, numDias);
			pre.setString(12, annoVac);
			pre.setString(13,ACTIVO );
			pre.setString(14,"0" );
			pre.setString(15,txtObs );
			pre.setString(16, codCreador);
			pre.setTimestamp(17, new java.sql.Timestamp(System.currentTimeMillis()));
			pre.setString(18, usuario);
			pre.setString(19, null);
			pre.setString(20, null);
			
			int res = pre.executeUpdate();

			result = true;

 
            
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
	 * Metodo que actualiza Solicitud
	 * @throws SQLException
	 */
	public boolean updateRegistroSolicitud(String dbpool, String numSeg, String annoActual, Integer numero, String codUO, 
			String userOrig, String licencia, String fechaIni)throws SQLException {

		String strUpd = "";
		PreparedStatement pre = null;
		Connection con = null;
		boolean result = false;

		try {

			strUpd = "Update t1277solicitud Set seguim_act = ? "+ 
            " Where anno = ? And numero = ? And u_organ = ? And cod_pers = ? And licencia = ? And ffinicio = ?";

			con = getConnection(dbpool);
			pre = con.prepareStatement(strUpd);
			
			pre.setString(1, numSeg);
			pre.setString(2, annoActual);
			pre.setInt(3, numero.intValue());
			pre.setString(4, codUO);
			pre.setString(5, userOrig);
			pre.setString(6, licencia);
			pre.setTimestamp(7, Utiles.stringToTimestamp(fechaIni + " 00:00:00"));
			
			
			int res = pre.executeUpdate();

			result = true;
 
            
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
	 * Metodo que actualiza Fechas de la solicitud Solicitud: jquispecoi: 03/2014
	 * @throws SQLException
	 */
	public boolean updateRegistroSolicitudFechas(String dbpool, String ffinicio, String ffin, String cuser_mod, 
			String anno, int numero, String cod_pers, String licencia)throws SQLException {

		String strUpd = "";
		PreparedStatement pre = null;
		Connection con = null;
		boolean result = false;

		try {
			strUpd = "update t1277solicitud set ffinicio=?, ffin=?, fmod=?, cuser_mod=? "+
				     "where anno=? and numero=? and cod_pers=? and licencia=? ";
			con = getConnection(dbpool);
			pre = con.prepareStatement(strUpd);
			
			pre.setTimestamp(1, Utiles.stringToTimestamp(ffinicio + " 00:00:00"));
			pre.setTimestamp(2, Utiles.stringToTimestamp(ffin + " 00:00:00"));
			pre.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
			pre.setString(4, cuser_mod);
			pre.setString(5, anno);
			pre.setInt(6, numero);
			pre.setString(7, cod_pers);
			pre.setString(8, licencia);
			
			int res = pre.executeUpdate();
			result = true;
		}

		catch (Exception e) {
			log.error("**** SQL ERROR **** "+ e.toString());
			throw new SQLException(e.toString());
		} finally {
			try {
				pre.close();
			} catch (Exception e) {}
			try {
				con.close();
			} catch (Exception e) {}
		}
		return result;
	}
	
	
	//ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta 
	/**
	 * 
	 * @param dbpool
	 * @param tipo
	 * @param criterio
	 * @param valor
	 * @param seguridad
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList findConsultaGeneralPorRegimen(HashMap datos, HashMap seguridad)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;

		try {

			String dbpool = (String) datos.get("dbpool");
			String tipo = (String) datos.get("tipo");
			String criterio = (String) datos.get("criterio");
			String valor = (String) datos.get("valor");
			String criterio2 = (String) datos.get("criterio2");
			String valor2 = (String) datos.get("valor2");
			String estado_id = (String) datos.get("estado_id");
			String regimen = (String) datos.get("regimen"); //filtrar por regimen 
			
			String valorFechaFin = (String) datos.get("valorFechaFin");

			strSQL.append("select   s.cod_pers, m.descrip, s.anno, s.numero, s.u_organ, "
					).append( "        s.fecha fechaSol, s.asunto, s.dias, ss.fecha_recep ultDeriv, "
					).append( "        ss.accion_id, ss.estado_id, ss.cuser_dest "
					).append( "from    t1277solicitud s, "
					).append( "        t1455sol_seg ss, "
					).append( "        t1279tipo_mov m, t02perdp p "   //se unio con t02perdp
					).append( "where   s.cod_pers = ss.cod_pers and "
					).append( "   	   s.cod_pers = p.t02cod_pers and " //relacion de tablas s con p
					).append( "        s.anno = ss.anno and "
					).append( "        s.numero = ss.numero and "
					).append( "        s.seguim_act = ss.num_seguim and "
					).append( "        s.licencia = m.mov ");
				
			if (criterio2.equals("1")) {
				strSQL.append(" and s.u_organ like '" ).append( valor2.trim().toUpperCase()
						).append( "%' ");
			}
			if (criterio2.equals("0")) {
				strSQL.append(" and s.cod_pers like '"
						).append( valor2.trim().toUpperCase() ).append( "%' ");
			}
			if (!tipo.equals("-1")) {
				strSQL.append(" and s.licencia = '" ).append( tipo.trim().toUpperCase()
						).append( "' ");
			}
			//regimen
			if (regimen.equals("0")) { //DL 276-728
				strSQL.append(" and p.t02cod_rel not in ('09','10') ");
			}
			if (regimen.equals("1")) { //DL 1057 (cas)
				strSQL.append(" and p.t02cod_rel = '09' ");
			}
			if (regimen.equals("2")) { //formativas
				strSQL.append(" and p.t02cod_rel = '10' ");
			}
			//fin regimen
			if (criterio.equals("0")) {
				strSQL.append(" and s.anno || '-' || s.numero like '%"
						).append( valor.trim().toUpperCase() ).append( "%' ");
			}
			
			if (criterio.equals("1")) {
				strSQL.append(" and upper(s.asunto) like '%"
						).append( valor.trim().toUpperCase() ).append( "%' ");
			}
			if (criterio.equals("2")) {
				String f = Utiles.toYYYYMMDD(valor.trim());
				strSQL.append(" and DATE(s.fecha) <= DATE('" ).append( f ).append( "') ");
			}
			if (criterio.equals("3")) {
				String f = Utiles.toYYYYMMDD(valor.trim());
				strSQL.append(" and DATE(ss.fecha_recep) <= DATE('" ).append( f ).append( "') ");
			}
			if (criterio.equals("4")) {
				strSQL.append(" and ss.cuser_dest like '"
						).append( valor.trim().toUpperCase() ).append( "%' ");
			}			
			if (criterio.equals("5")) {
				String f = Utiles.toYYYYMMDD(valor.trim());
				String fFin = Utiles.toYYYYMMDD(valorFechaFin.trim());
				strSQL.append(" and DATE(s.fecha) >= DATE('" ).append( f ).append( "') ")
				.append(" and DATE(s.fecha) <= DATE('" ).append( fFin ).append( "') ");
			}
						
					
			if (!estado_id.equals("-1")) {
				strSQL.append(" and ss.estado_id = '").append(estado_id).append( "' ");
			}			

			if (seguridad != null) {
				HashMap roles = (HashMap) seguridad.get("roles");
				String uoSeg = (String) seguridad.get("uoSeg");
				String uoAO = (String) seguridad.get("uoAO");

				if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
					strSQL.append(" and 1=1 ");
				} else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null) {
					strSQL.append(" and ((s.u_organ like '" ).append( uoSeg ).append( "') ");
					strSQL.append(" or (s.u_organ in  (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '" )
					.append( uoAO ).append( "'))) ");
					
				}else if (//roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null ||
						roles.get(Constantes.ROL_JEFE) != null) {
					strSQL.append(" and s.u_organ like '" ).append( uoSeg ).append( "' ");
				} else {
					strSQL.append(" and 1=2 ");
				}
			}

			strSQL.append(" order by fechaSol desc ");
			
			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			
			log.debug("findConsultaGeneralPorRegimen(strSQL): " + strSQL);
			
			rs = pre.executeQuery();

			T02DAO dao = new T02DAO();

			lista = new ArrayList();
			HashMap solicitud = null;
			String emisor = "";
			String responsable = "";
			while (rs.next()) {

				solicitud = new HashMap();

				solicitud.put("codPers", rs.getString("cod_pers"));
				solicitud.put("descrip", rs.getString("descrip").trim());
				solicitud.put("anno", rs.getString("anno"));
				solicitud.put("numero", rs.getString("numero"));
				solicitud.put("uorgan", rs.getString("u_organ"));
				String numSol = (String) solicitud.get("anno") + "-" + (String) solicitud.get("numero");
				solicitud.put("numSol", numSol);
				solicitud.put("fechaSol", rs.getTimestamp("fechaSol"));
				solicitud.put("asunto", rs.getString("asunto").trim());
				solicitud.put("ultDeriv", rs.getTimestamp("ultDeriv"));
				solicitud.put("dias", rs.getString("dias"));
				solicitud.put("estId", rs.getString("estado_id"));
				solicitud.put("accion_id", rs.getString("accion_id"));

				emisor = rs.getString("cod_pers");
				responsable = rs.getString("cuser_dest");
				if (emisor != null && !emisor.equals("")) {
					solicitud.put("emisor", dao.findNombreCompletoByCodPers(
							dbpool, emisor.trim()));
				}
				if (responsable != null && !responsable.equals("")) {
					solicitud.put("codResp", responsable);
					solicitud.put("responsable", dao
							.findNombreCompletoByCodPers(dbpool, responsable
									.trim()));
				}

				String estado = "Seguimiento";
				if (((String) solicitud.get("estId")).trim().equals(Constantes.ESTADO_CONCLUIDA)){
					estado = "Concluida";
				}
				solicitud.put("estado", estado);
				
				String accion = "";
				//solo pintara el valor de accion, si el estado es CONCLUIDA
				if(estado.equals("Concluida")){
					if (((String) solicitud.get("accion_id")).trim().equals(Constantes.ACCION_INICIAR)) {
						accion = "Iniciada";
					}
					if (((String) solicitud.get("accion_id")).trim().equals(Constantes.ACCION_APROBAR)) {
						accion = "Aprobada";
					}
					if (((String) solicitud.get("accion_id")).trim().equals(Constantes.ACCION_RECHAZAR)) {
						accion = "Rechazada";
					}
					if (((String) solicitud.get("accion_id")).trim().equals(Constantes.ACCION_DERIVAR_RRHH)) {
						accion = "Derivada a RRHH";
					}
				}				
				solicitud.put("accion", accion);
				
				lista.add(solicitud);
			}
			log.debug("findConsultaGeneralPorRegimen(lista): " + lista);
 
		} catch (Exception e) {
			log.error("**** SQL ERROR findConsultaGeneralPorRegimen()**** "+ e.toString());
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
	//FIN ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta 
	
	//AGONZALESF -PAS20171U230200001 - solicitud de reintegro   
	 /**
	  * Obtiene solicitud por anio y numero
	  * @param dbpool :pool a base
	  * @param anio : año de solicitud
	  * @param numero : numero de solicitud
	  * @return Mapa con datos de la solicitud
	  * @throws SQLException
	  */
	public Map findSolicitudByAnioByNro(DataSource dbpool,String anio, Integer numero) throws SQLException {
	 
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null; 
		HashMap solicitud = null;
		try {  
			con = getConnection(dbpool);
			pre = con.prepareStatement(SELECT_SOLICITUD); 
			log.debug("findSolicitudByAnioByNro(strSQL): " + SELECT_SOLICITUD); 
			pre.setString(1, anio);
			pre.setInt(2, numero.intValue());
			
			rs = pre.executeQuery();  
			
			String emisor = "";
			String responsable = "";
			while (rs.next()) { 
				solicitud = new HashMap(); 
				solicitud.put("cod_pers", rs.getString("cod_pers"));
				solicitud.put("licencia", rs.getString("licencia"));
				solicitud.put("anno", rs.getString("anno"));
				solicitud.put("numero", rs.getString("numero"));
				solicitud.put("u_organ", rs.getString("u_organ"));  
				solicitud.put("ffinicio", rs.getDate("ffinicio"));  
			}
			log.debug("findSolicitudByAnioByNro(solicitud): " + solicitud);
 
		} catch (Exception e) {
			log.error("**** SQL ERROR findSolicitudByAnioByNro()**** "+ e.toString());
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
		return solicitud;
	}

	//AGONZALESF -PAS20171U230200001 - solicitud de reintegro  
	 /**
	  * Funcion para obtener solicitudes(documentos aprobados) para solicitudes reintegro
	  * @param ds : Datasource de base de datos
	  * @param filtro para la busqueda
	  * @return lista de solicitudes que cumplen los criterios de busqueda
	  * @throws SQLException
	  */
	public List findSolicitudesByRangoByEmpleado(DataSource ds, Map filtro) throws SQLException {
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList detalles = new ArrayList();

		try {
			log.debug("Ingreso findSolicitudesByRangoByEmpleado ->" + filtro);
			con = getConnection(ds);
			pre = con.prepareStatement(FIND_DOC_APROB_SOLICITUD.toString());
			log.debug("sql  " + FIND_DOC_APROB_SOLICITUD.toString());

			java.sql.Date ffinicio = new BeanFechaHora(filtro.get("ffinicio").toString()).getSQLDate();
			java.sql.Date ffin =new BeanFechaHora(filtro.get("ffin").toString()).getSQLDate();
			
			pre.setString(1, (String) filtro.get("cod_pers"));
			pre.setDate(2, ffinicio);			
			pre.setDate(3, ffin );
			pre.setDate(4, ffinicio);
			pre.setDate(5, ffin);
			pre.setDate(6, ffinicio);
			pre.setDate(7, ffin);
			rs = pre.executeQuery();
			detalles = new ArrayList();
			while (rs.next()) {

				HashMap det = new HashMap();
				det.put("tipo", rs.getString("tipo"));
				det.put("fechaInicio", new BeanFechaHora(rs.getDate("fechaInicio")).getFormatDate("dd/MM/yyyy"));
				det.put("fechaFin", new BeanFechaHora(rs.getDate("fechaFin")).getFormatDate("dd/MM/yyyy"));
				det.put("motivo", rs.getString("motivo"));
				detalles.add(det);
			}
			log.debug("findSolicitudesByRangoByEmpleado ->" + detalles);

		} catch (Exception e) {
			log.error("**** SQL ERROR findDocAprobadoSolicitudes()**** " + e.toString());
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

		return detalles;
	}
		 
	// PAS20181U230200067 - solicitud de reintegro , planillas adicionales, agregar omisiones	
	/**
	 * Consulta para obtener fechas donde existe solicitudes de anulacion o omision  aprobadas
	 * @param ds
	 * @param filtro
	 * @return
	 */
	public List findFechasSolicitudesOmisionAnulacionMarcas(DataSource ds, Map filtro) throws SQLException {
		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		List detalle = new ArrayList();
		String fec = "";
		try {

			strSQL.append(SELECT_OMISION_ANULACION.toString())
			.append(" AND TO_CHAR(s.ffinicio , '%Y') =?")
			.append(" AND TO_CHAR(s.ffinicio , '%m') =?"); 
			con = getConnection(ds);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, (String) filtro.get("cod_pers"));
			pre.setString(2, (String) filtro.get("anioConsulta"));
			pre.setString(3, (String) filtro.get("mesConsulta"));
			log.debug(strSQL);
			log.debug(filtro);
			rs = pre.executeQuery();
			while (rs.next()) {
				Date date = rs.getDate("ffinicio");
				detalle.add(date);
			}
			log.debug(detalle);
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
		 
		// PAS20181U230200067 - solicitud de reintegro , planillas adicionales, agregar omisiones	
		/**
		 * Obtener fechas con omisiones o anulaciones aprobadas para sustentar reintegro en un determinado rango de fecha 
		 * @param ds
		 * @param filtro
		 * @return
		 */
		public List obtenerOmisionesAnulacionesSustento (DataSource ds, Map filtro) throws SQLException {
			StringBuffer strSQL = new StringBuffer("");
			PreparedStatement pre = null;
			Connection con = null;
			ResultSet rs = null;
			List detalle = new ArrayList();
			try {

				strSQL.append(SELECT_OMISION_ANULACION.toString())
				.append(" AND s.ffinicio  BETWEEN TO_DATE(? , '%d/%m/%Y') AND TO_DATE(?,'%d/%m/%Y')") ;
				con = getConnection(ds);
				pre = con.prepareStatement(strSQL.toString());
				
				log.debug(strSQL);
				log.debug(filtro);
				pre.setString(1, (String) filtro.get("cod_pers"));
				pre.setString(2, (String) filtro.get("ffinicioLic"));
				pre.setString(3, (String) filtro.get("ffinLic"));
				
				rs = pre.executeQuery();
				while (rs.next()) {
					Date date = rs.getDate("ffinicio");
					detalle.add(date);
				}
				log.debug(detalle);
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
	
}