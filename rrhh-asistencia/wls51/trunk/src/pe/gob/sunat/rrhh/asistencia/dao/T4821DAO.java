package pe.gob.sunat.rrhh.asistencia.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.Propiedades;
import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.utils.Utiles;

import java.util.Date;

import pe.gob.sunat.rrhh.dao.T02DAO;
import pe.gob.sunat.utils.Constantes;

import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.sql.Connection;


/**
 * <p>
 * Title: T4821DAO
 * </p>
 * <p>
 * Description: Clase encargada de administrar las consultas a la tabla
 * T4821solicituddet
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: Sunat
 * </p>
 * 
 * @author mtomaylla
 * @version 1.0
 */

public class T4821DAO extends DAOAbstract {

	private DataSource dataSource = null;

	Propiedades constantes = new Propiedades(getClass(),
			"/constantes.properties");

	private final StringBuffer INSERT_REGISTRO = new StringBuffer(
	"Insert into t4821solicituddet (")
	.append("ann_sol,num_sol,cod_uo_sol,cod_pers_sol,cod_mov_sol,fec_registro_sol,num_detalle,fec_permiso,hor_ini_permiso,hor_fin_permiso,obs_sustento_obs,cod_user_crea,")
	.append("fec_creacion,cod_user_mod,fec_modifica) ").append("Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

	/*
	private final StringBuffer VERIFICA_REGISTRO = new StringBuffer(
	"select ann_sol from t4821solicituddet, t1455sol_seg ")
	.append(
	"where ann_sol = anno and num_sol = numero and cod_pers_sol = cod_pers and fec_permiso = ? ")
	.append("and ( ? >= hor_ini_permiso and ? <= hor_fin_permiso) ")
	.append("and ( ? >= hor_ini_permiso and ? <= hor_fin_permiso) ")
	.append(
	" and cod_pers_sol = ? and cod_mov_sol = ?  and accion_id = ? and estado_id = ? ");*/
	
	//ICAPUNAY 21/06/2012 NO DEJA REGISTRAR UN RANGO NUEVO QUE INTERSECTE CON RANGO EN SEGUIMIENTO, APROBADOS O RECHAZADOS
	/*
	private final StringBuffer VERIFICA_REGISTRO = new StringBuffer(
	"select fec_permiso,hor_ini_permiso,hor_fin_permiso,numero from t4821solicituddet, t1455sol_seg ")
	.append(
	"where ann_sol = anno and num_sol = numero and cod_pers_sol = cod_pers and fec_permiso = ? ")
	.append("and (( ? < hor_ini_permiso or ? < hor_fin_permiso) ")
	.append("and ( ? > hor_ini_permiso or ? > hor_fin_permiso)) ")
	.append(
	" and cod_pers_sol = ? and cod_mov_sol = ?  and accion_id = ? and estado_id = ? ");*/
	//FIN ICAPUNAY 21/06/2012 NO DEJA REGISTRAR UN RANGO NUEVO QUE INTERSECTE CON RANGO EN SEGUIMIENTO, APROBADOS O RECHAZADOS
	
	//ICAPUNAY 21/06/2012 NO DEJA REGISTRAR UN RANGO NUEVO QUE INTERSECTE CON RANGO EN SEGUIMIENTO, APROBADOS O RECHAZADOS	
	private final StringBuffer VERIFICA_REGISTRO = new StringBuffer(
	"select sd.fec_permiso,sd.hor_ini_permiso,sd.hor_fin_permiso,s.numero from t4821solicituddet sd, t1277solicitud s,t1455sol_seg ss ")
	.append("where sd.ann_sol = s.anno and sd.num_sol = s.numero and sd.cod_pers_sol = s.cod_pers and sd.fec_permiso = ? ")
	.append("and (( ? <= sd.hor_ini_permiso and ? <= sd.hor_ini_permiso) ")
	.append("or ( ? >= sd.hor_fin_permiso and ? >= sd.hor_fin_permiso)) ")
	.append("and s.anno = ss.anno and s.numero = ss.numero and s.cod_pers = ss.cod_pers ")
	.append("and sd.cod_pers_sol = ? and sd.cod_mov_sol = ?  and ss.accion_id = ? and ss.estado_id = ? and s.seguim_act = ? ");
	
	private final StringBuffer VERIFICA_PERMISO_RECHAZADO = new StringBuffer(
	"select a.fec_aut,a.hor_ini_aut,a.hor_fin_aut from t4818autorizaexc a ")
	.append("where a.cod_pers =? and a.fec_aut=? and a.ind_aut='0' ")
	.append("and (( ? < a.hor_ini_aut or ? < a.hor_fin_aut) ")
	.append("and ( ? > a.hor_ini_aut or ? > a.hor_fin_aut)) ");
	
	private final StringBuffer VERIFICA_PERMISO_AUTORIZADO = new StringBuffer(
	"select a.fec_aut,a.hor_ini_aut,a.hor_fin_aut from t4818autorizaexc a ")
	.append("where a.cod_pers =? and a.fec_aut=? and a.ind_aut='1' ")
	.append("and (( ? < a.hor_ini_aut or ? < a.hor_fin_aut) ")
	.append("and ( ? > a.hor_ini_aut or ? > a.hor_fin_aut)) ");
	
	private final StringBuffer VERIFICA_REGISTRO_ENSEGUIMIENTO = new StringBuffer(
	//"select sd.fec_permiso,sd.hor_ini_permiso,sd.hor_fin_permiso,s.numero from t4821solicituddet sd, t1277solicitud s,t1455sol_seg ss ")//add ICR 19/12 add anno
	"select sd.fec_permiso,sd.hor_ini_permiso,sd.hor_fin_permiso,s.numero,s.anno from t4821solicituddet sd, t1277solicitud s,t1455sol_seg ss ")//add ICR 19/12 add anno		
	.append("where sd.ann_sol = s.anno and sd.num_sol = s.numero and sd.cod_pers_sol = s.cod_pers and sd.fec_permiso = ? ")
	.append("and (( ? < sd.hor_ini_permiso or ? < sd.hor_fin_permiso) ")
	.append("and ( ? > sd.hor_ini_permiso or ? > sd.hor_fin_permiso)) ")
	.append("and s.anno = ss.anno and s.numero = ss.numero and s.cod_pers = ss.cod_pers ")
	.append("and sd.cod_pers_sol = ? and sd.cod_mov_sol = ?  and ss.accion_id = ? and ss.estado_id = ? and s.seguim_act = ? ");
	
	/*
	private final StringBuffer VERIFICA_PERMISO_ENSEGUIMIENTO_APROBADO = new StringBuffer(
	"select sd.fec_permiso,sd.hor_ini_permiso,sd.hor_fin_permiso,s.numero from t4821solicituddet sd, t1277solicitud s,t1455sol_seg ss ")
	.append("where sd.ann_sol = s.anno and sd.num_sol = s.numero and sd.cod_pers_sol = s.cod_pers and sd.fec_permiso = ? ")
	.append("and s.anno = ss.anno and s.numero = ss.numero and s.cod_pers = ss.cod_pers ")
	.append("and sd.cod_pers_sol = ? and sd.cod_mov_sol = ?  and ss.accion_id = ? and ss.estado_id = ? and s.seguim_act = ? ");*/
	
	private final StringBuffer FIND_FECHA_PERMISO = new StringBuffer("select distinct fec_permiso from t4821solicituddet "	
	).append("where cod_pers_sol=? and fec_permiso=? ");
	//FIN ICAPUNAY 21/06/2012 NO DEJA REGISTRAR UN RANGO NUEVO QUE INTERSECTE CON RANGO EN SEGUIMIENTO, APROBADOS O RECHAZADOS

	private final StringBuffer LISTA_SOLICITUDES = new StringBuffer(
	//"select fec_permiso, hor_ini_permiso, hor_fin_permiso, obs_sustento_obs from t4821solicituddet, t1455sol_seg ")//ICR - 03/12/2012 PAS20124E550000064 labor excepcional
	"select distinct fec_permiso, hor_ini_permiso, hor_fin_permiso, obs_sustento_obs from t4821solicituddet, t1455sol_seg ")//ICR - 03/12/2012 PAS20124E550000064 labor excepcional		
	.append("where ann_sol = anno and num_sol = numero and cod_uo_sol = u_organ and cod_pers_sol = cod_pers  and estado_id = '1' ")
	.append("and ann_sol = ? and num_sol = ? and cod_uo_sol = ? and cod_pers_sol = ? and cod_mov_sol = ?  ");
	
	private final StringBuffer LISTA_SOLICITUDES_ADMINISTRACION = new StringBuffer(		//jquispecoi 03/2014
			"select distinct det.fec_permiso, det.hor_ini_permiso, det.hor_fin_permiso, det.obs_sustento_obs,  "+
			"    aut.ind_aut, comp.hor_ini_comp, comp.hor_fin_comp, comp.cnt_min_comp_acu, comp.cnt_min_comp_sal, comp.ind_comp "+ 
			"from t4821solicituddet det "+
			"inner join t1455sol_seg seg on  "+
			"    det.ann_sol = seg.anno and  "+
			"    det.num_sol = seg.numero and  "+
			"    det.cod_uo_sol = seg.u_organ and  "+
			"    det.cod_pers_sol = seg.cod_pers and  "+
			"    seg.estado_id = '1' and  "+
			"    det.ann_sol = ? and  "+
			"    det.num_sol = ? and  "+
			"    det.cod_uo_sol = ? and  "+
			"    det.cod_pers_sol = ? and  "+
			"    det.cod_mov_sol = ? "+
			"left join t4818autorizaexc aut on  "+
			"    det.cod_pers_sol=aut.cod_pers and  "+
			"    det.fec_permiso = aut.fec_aut and  "+
			"    det.hor_ini_permiso=aut.hor_ini_aut "+
			"left join t4819compensacion comp on  "+
			"    det.cod_pers_sol=comp.cod_pers and  "+
			"    det.fec_permiso = comp.fec_perm and  "+
			"    det.hor_ini_permiso=comp.hor_ini_aut ");	

	private final StringBuffer LISTA_DETALLE = new StringBuffer(
	"select ann_sol,num_sol,cod_uo_sol,cod_pers_sol,cod_mov_sol,fec_registro_sol,num_detalle,fec_permiso,hor_ini_permiso,hor_fin_permiso,obs_sustento_obs,cod_user_crea from t4821solicituddet ")
	.append(
			"where ann_sol = ? and num_sol = ? and cod_uo_sol = ? and cod_pers_sol = ? and cod_mov_sol = ? and num_detalle = ? ");
	
	//INICIO ICAPUNAY
	private final StringBuffer INSERTAR_DETALLE = new StringBuffer("insert into t4821solicituddet "
	).append("(ann_sol,num_sol,cod_uo_sol,cod_pers_sol,cod_mov_sol,fec_registro_sol,num_detalle,"
	).append("fec_permiso,hor_ini_permiso,hor_fin_permiso,obs_sustento_obs,cod_user_crea,fec_creacion) "
	).append("values (?,?,?,?,?,?,?,?,?,?,?,?,?) ");
	
	//ICAPUNAY 01/06/2012 INTERSECCION DE RANGOS NO AUTORIZADOS EN SOLICITUDES EN SEGUIMIENTO	
	private final StringBuffer FIND_INTERVALOS_INTERSECTADOS = new StringBuffer("select sd.* from t4821solicituddet sd, t1277solicitud s,t1455sol_seg ss " //add ICR 19/12 seguimiento 0 (colab) y 1 (superv)	
	).append("where sd.ann_sol = s.anno and sd.num_sol = s.numero and sd.cod_pers_sol = s.cod_pers " //add ICR 19/12 seguimiento 0 (colab) y 1 (superv)	
	).append("and s.anno = ss.anno and s.numero = ss.numero and s.cod_pers = ss.cod_pers and ((ss.accion_id <> ? and ss.estado_id <> ?) or (ss.accion_id <> ? and ss.estado_id <> ?))  " //add ICR 20/12 excluye aprobadas y rechazadas (solo obtiene en seguimiento)		
	).append("and  sd.cod_pers_sol = ? and sd.cod_mov_sol = ? and sd.fec_permiso = ? "	
	).append("and ((? < sd.hor_ini_permiso or ? < sd.hor_fin_permiso) " // (decia <= - dice <)ICAPUNAY-PAS20144EB20000144-Optimizacion Labor Excepcional-Memo 31-2013-4F3100
	).append("and (? > sd.hor_ini_permiso or ? > sd.hor_fin_permiso)) " // (decia >= - dice >)ICAPUNAY-PAS20144EB20000144-Optimizacion Labor Excepcional-Memo 31-2013-4F3100		
	).append("order by sd.hor_ini_permiso asc ");
	
	//FIN ICAPUNAY
	
	
	//NVILLAR - LABOR EXCEPCIONAL
	private final StringBuffer FIND_CONSULTA_LAB_EXCEPCIONAL = new StringBuffer(
	" SELECT DISTINCT r.anno AS anio, r.numero AS numero, r.cod_pers AS registro, r.asunto, date(r.fecha) AS fecha, t.accion_id AS estado, r.licencia AS licencia")		
	.append(" FROM t1277Solicitud r, t4821SolicitudDet s, t1455sol_seg t ")
	.append(" WHERE r.anno = s.ann_sol ")
	.append(" AND r.numero =s.num_sol ")
	.append(" AND r.u_organ =s.cod_uo_sol ")
	.append(" AND r.cod_pers =s.cod_pers_sol ")
	.append(" AND r.licencia = s.cod_mov_sol ")
	//.append(" AND r.ffinicio = s.fec_registro_sol ")
	.append(" AND r.anno = t.anno ")
	.append(" AND r.numero = t.numero ")
	.append(" AND r.cod_pers = t.cod_pers ")
	.append(" AND r.seguim_act = t.num_seguim ");
	//FIN 
	
	
	//NVILLAR - LABOR EXCEPCIONAL
	private final StringBuffer FIND_CONSULTA_LAB_EXCEPCIONAL2 =	new StringBuffer(
	" SELECT DISTINCT substr(trim(nvl(v.t02cod_uorgl,'')||nvl(v.t02cod_uorg,'')),1,6) AS unidad, r.cod_pers AS codigo, r.anno AS anio, r.numero AS numero, r.cod_pers AS registro, r.asunto, date(r.fecha) AS fecha, t.accion_id AS estado, r.licencia AS licencia")		
	.append(" FROM t1277Solicitud r, t4821SolicitudDet s, t1455sol_seg t, t02perdp v ")
	.append(" WHERE r.anno = s.ann_sol ")
	.append(" AND r.numero =s.num_sol ")
	.append(" AND r.u_organ =s.cod_uo_sol ")
	.append(" AND r.cod_pers =s.cod_pers_sol ")
	.append(" AND r.licencia = s.cod_mov_sol ")
	//.append(" AND r.ffinicio = s.fec_registro_sol ") 
	.append(" AND r.anno = t.anno ")
	.append(" AND r.numero = t.numero ")
	.append(" AND r.cod_pers = t.cod_pers ")
	.append(" AND r.cod_pers = v.t02cod_pers ")
	.append(" AND s.cod_pers_sol = v.t02cod_pers ")
	.append(" AND t.cod_pers = v.t02cod_pers ")
	.append(" AND r.seguim_act = t.num_seguim ");
	//FIN 

	//NVILLAR  LABOR EXCEPCIONAL
	private final StringBuffer FIND_DETALLE_CONSULTA_LAB_EXCEPCIONAL =	new StringBuffer(
	" SELECT fec_permiso AS fec_permiso, hor_ini_permiso[1,5] AS hora_inicio, hor_fin_permiso[1,5] AS hora_fin, obs_sustento_obs AS sustento")		
	.append(" FROM t4821SolicitudDet ") 
	.append(" WHERE ann_sol = ? ")
	.append(" AND num_sol = ? ")
	.append(" AND cod_mov_sol = ? ");
	//.append(" AND date(fec_registro_sol) = ? ");
	//FIN 
	
	
	//NVILLAR - LABOR EXCEPCIONAL
	private final StringBuffer FIND_DETALLE_CONSULTA_LAB_EXCEPCIONAL2 =		
		new StringBuffer(" SELECT fec_permiso AS fec_permiso, hor_ini_permiso[1,5] AS hora_inicio, hor_fin_permiso[1,5] AS hora_fin, obs_sustento_obs AS sustento")		
	.append(" FROM t4821SolicitudDet ") 
	.append(" WHERE ann_sol = ? ")
	.append(" AND num_sol = ? ")
	.append(" AND cod_pers_sol = ? ")
	.append(" AND cod_mov_sol = ? ");
	//.append(" AND date(fec_registro_sol) = ? ");
	//FIN 
	
	/* MTM 24/05/2012 */
	private final StringBuffer FIND_FECHA_PERMANENCIA = new StringBuffer("select a.fec_permiso, a.num_sol, a.num_detalle from t4821SolicitudDet a, T4820SOLDETXCOMP b "
	).append("where a.cod_pers_sol = b.cod_pers_sol and a.ann_sol = b.ann_sol and a.num_sol = b.num_sol ")
    .append("and a.cod_uo_sol = b.cod_uo_sol and a.cod_mov_sol = b.cod_mov_sol and a.num_detalle = b.num_detalle ")
    .append("and a.fec_registro_sol = b.fec_registro_sol and a.cod_pers_sol = ? and a.fec_permiso = ? ");

	/*EBV 11/06/2012*/
	private final StringBuffer VERIFICA_REGISTRO_COMPENSACION = new StringBuffer(
	//"select sd.* from t4821solicituddet sd, t1277solicitud s ")//add ICR seguimiento x registro de colab o aprob de supervisor
	"select sd.* from t4821solicituddet sd, t1277solicitud s,t1455sol_seg ss ")//add ICR seguimiento x registro de colab o aprob de supervisor
	.append("where sd.ann_sol = s.anno and sd.num_sol = s.numero and sd.cod_pers_sol = s.cod_pers and sd.fec_permiso = ? ")//icr se comenta uuoo porque puede variar //add ICR seguimiento x registro de colab o aprob de supervisor
	.append("and s.anno = ss.anno and s.numero = ss.numero and s.cod_pers = ss.cod_pers ")//add ICR seguimiento x registro de colab o aprob de supervisor
	.append("and sd.cod_pers_sol = ? and sd.cod_mov_sol = ? and ss.accion_id = ? and ss.estado_id = ? and s.seguim_act = ? ");//icr se comenta uuoo porque puede variar //add ICR seguimiento x registro de colab o aprob de supervisor
	//.append("where ann_sol = anno and num_sol = numero and cod_uo_sol = u_organ and cod_pers_sol = cod_pers and seguim_act = '0' and fec_permiso = ? ")//icr se comenta uuoo porque puede variar
	//.append("and  cod_uo_sol = ? and cod_pers_sol = ? and cod_mov_sol = ? ");//icr se comenta uuoo porque puede variar
	
	
	//ICAPUNAY 05/10/2012 AJUSTES AOM 06A4T11 LABOR EXCEPCIONAL
	private final StringBuffer FIND_FECHAS_PERMISO = new StringBuffer("select distinct fec_permiso from t4821solicituddet "	
	).append("where cod_pers_sol=? and ann_sol=? and num_sol=? order by fec_permiso asc ");
	//FIN ICAPUNAY 05/10/2012 AJUSTES AOM 06A4T11 LABOR EXCEPCIONAL
	
	public T4821DAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.dataSource = (DataSource) datasource;
		else if (datasource instanceof String)
			this.dataSource = getDataSource((String) datasource);
		else
			throw new DAOException(this, "Datasource no valido");
	}

	/**
	 * MTM Metodo que inserta registro de Solicitud Labor Excepcional
	 * 
	 * @throws SQLException
	 */
	public boolean insertRegistroSolicitudDetLabor(String dbpool,
			String annoActual, Integer numero, String codUO, String userOrig,
			String codMov, int numdetalle, String fecha, String horaini,
			String horafin, String sustento, String codCreador, String usuario)
			throws SQLException {

		Integer d = new Integer(numdetalle);
		int modificado = 0;
		if (log.isDebugEnabled()) log.debug("T4821DAO - insertRegistroSolicitudDetLabor - annoActual: " + annoActual + " - numero: " + numero);	
		if (log.isDebugEnabled()) log.debug("T4821DAO - insertRegistroSolicitudDetLabor - codUO: " + codUO + " - userOrig: " + userOrig);	
		if (log.isDebugEnabled()) log.debug("T4821DAO - insertRegistroSolicitudDetLabor - codMov: " + codMov + " - numdetalle: " + numdetalle);
		if (log.isDebugEnabled()) log.debug("T4821DAO - insertRegistroSolicitudDetLabor - fecha: " + fecha + " - horaini: " + horaini);
		if (log.isDebugEnabled()) log.debug("T4821DAO - insertRegistroSolicitudDetLabor - horafin: " + horafin + " - codCreador: " + codCreador);
		try{

		modificado = executeUpdate(dataSource, INSERT_REGISTRO.toString(),
				new Object[] { annoActual, numero, codUO, userOrig, codMov,
						new java.sql.Timestamp(System.currentTimeMillis()), d,
						Utiles.stringToTimestamp(fecha + " 00:00:00"), horaini,
						horafin, sustento, codCreador,
						new java.sql.Timestamp(System.currentTimeMillis()),
						usuario, null });
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta T4821DAO. [INSERT_REGISTRO]");
		}
		return (modificado > 0);
	}

	public List verificaRegistrosRechazadosAutorizaLabor(String dbpool,String userOrig,String fecha,			
			String horaini, String horafin) throws SQLException {
		List listaRechazado = null;
		log.debug("entra verificaRegistrosRechazadosAutorizaLabor");
		listaRechazado = executeQuery(dataSource,VERIFICA_PERMISO_RECHAZADO.toString(), new Object[] {userOrig,				
			new FechaBean(fecha).getSQLDate(), horaini,horaini, horafin, horafin}); 	
		log.debug("sale verificaRegistrosRechazadosAutorizaLabor");
		log.debug("listaRechazado: "+ listaRechazado);
		return listaRechazado;
	}
	
	//ICAPUNAY 21/06/2012 NO DEJA REGISTRAR UN RANGO NUEVO QUE INTERSECTE CON RANGO EN SEGUIMIENTO, APROBADOS O RECHAZADOS
	
	public List verificaRegistrosAutorizadosAutorizaLabor(String dbpool,String userOrig,String fecha,			
			String horaini, String horafin) throws SQLException {
		List listaAutorizado = null;
		log.debug("entra verificaRegistrosAutorizadosAutorizaLabor");
		listaAutorizado = executeQuery(dataSource,VERIFICA_PERMISO_AUTORIZADO.toString(), new Object[] {userOrig,				
			new FechaBean(fecha).getSQLDate(), horaini,horaini, horafin, horafin}); 	
		log.debug("sale verificaRegistrosAutorizadosAutorizaLabor");
		log.debug("listaAutorizado: "+ listaAutorizado);
		return listaAutorizado;
	}
	
	public List verificaRegistroSolicitudDetLabor(String dbpool,String userOrig, String codMov, String fecha,
			//String horaini, String horafin,String accion, String estado) throws SQLException {//ICAPUNAY 21/06/2012 NO DEJA REGISTRAR UN RANGO NUEVO QUE INTERSECTE CON RANGO EN SEGUIMIENTO, APROBADOS O RECHAZADOS
			String horaini, String horafin,String accion, String estado,String seguimiento) throws SQLException {//ICAPUNAY 21/06/2012 NO DEJA REGISTRAR UN RANGO NUEVO QUE INTERSECTE CON RANGO EN SEGUIMIENTO, APROBADOS O RECHAZADOS
		List listaAprobada = null;
		log.debug("entra verificaRegistroSolicitudDetLabor");
		listaAprobada = executeQuery(dataSource, VERIFICA_REGISTRO.toString(), new Object[] {
				//new FechaBean(fecha).getSQLDate(), horaini,horaini, horafin, horafin, userOrig, codMov, accion, estado }); //ICAPUNAY 21/06/2012 NO DEJA REGISTRAR UN RANGO NUEVO QUE INTERSECTE CON RANGO EN SEGUIMIENTO, APROBADOS O RECHAZADOS
			new FechaBean(fecha).getSQLDate(), horaini,horafin, horaini, horafin, userOrig, codMov, accion, estado,seguimiento }); //ICAPUNAY 21/06/2012 NO DEJA REGISTRAR UN RANGO NUEVO QUE INTERSECTE CON RANGO EN SEGUIMIENTO, APROBADOS O RECHAZADOS	
		log.debug("sale verificaRegistroSolicitudDetLabor");
		log.debug("listaAprobada: "+ listaAprobada);
		return listaAprobada;
	}
	
	//revisar
	public List verificaRegistroSeguimientoSolicitudDetLabor(String dbpool,String userOrig, String codMov, String fecha,			
			String horaini, String horafin,String accion, String estado,String seguimiento) throws SQLException {
		List listaSeguimiento = null;
		log.debug("entra verificaRegistroSeguimientoSolicitudDetLabor");
		listaSeguimiento = executeQuery(dataSource, VERIFICA_REGISTRO_ENSEGUIMIENTO.toString(), new Object[] {				
			new FechaBean(fecha).getSQLDate(), horaini,horaini, horafin, horafin, userOrig, codMov, accion, estado,seguimiento });	
		log.debug("sale verificaRegistroSeguimientoSolicitudDetLabor");
		log.debug("listaSeguimiento: "+ listaSeguimiento);
		return listaSeguimiento;
	}
	
	/*
	/**
	  * Metodo que busca si existe una fecha de permiso con un estado para un colaborador	
	  * @param userOrig String
	  * @param fecha String	
	  * @return listaEstado Lista
	  * @throws DAOException
	  */	
	/*public List verificaEstadoFechaPermiso(String dbpool,
			String codUO, String userOrig, String codMov, String fecha,String accion, String estado,String seguimiento) throws SQLException {
		List listaEstado = null;
		log.debug("entra verificaEstadoFechaPermiso");
		listaEstado = executeQuery(dataSource, VERIFICA_PERMISO_ENSEGUIMIENTO_APROBADO.toString(), new Object[] {			
			new FechaBean(fecha).getSQLDate(),userOrig, codMov, accion, estado,seguimiento });	
		log.debug("sale verificaEstadoFechaPermiso");
		log.debug("listaEstado: "+ listaEstado);
		return listaEstado;
	}*/
	
	
	/**
	  * Metodo que busca si existe una fecha de permiso para un colaborador	
	  * @param codPers String
	  * @param fechaPermiso String	
	  * @return mfechaPermiso Map
	  * @throws DAOException
	  */
	public Map findfechaPermiso(String codPers, String fechaPermiso) throws DAOException {
		
		
		Map mfechaPermiso = null;
		if (log.isDebugEnabled()) log.debug("T4818DAO - findfechaPermiso - codPers: " + codPers + " - fechaPermiso: " + fechaPermiso);		
		try{
			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);			
			mfechaPermiso = executeQueryUniqueResult(dataSource, FIND_FECHA_PERMISO.toString(), new Object[]{codPers,
				new FechaBean(fechaPermiso).getSQLDate()});
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [FIND_AUTORIZACION]");
		}	
		return mfechaPermiso;
	}
	//ICAPUNAY 21/06/2012 NO DEJA REGISTRAR UN RANGO NUEVO QUE INTERSECTE CON RANGO EN SEGUIMIENTO, APROBADOS O RECHAZADOS

	public List listaSolicitudes(String dbpool, String anno, String numero,
			String coduo, String codPers, String tipo) throws SQLException {

		List lista = null;
		if (log.isDebugEnabled()) log.debug("T4821DAO - listaSolicitudes - anno: " + anno + " - coduo: " + coduo);
		if (log.isDebugEnabled()) log.debug("T4821DAO - listaSolicitudes - codPers: " + codPers + " - tipo: " + tipo);
		try{
			lista = executeQuery(dataSource, LISTA_SOLICITUDES.toString(), new Object[] {
					anno, numero, coduo, codPers, tipo });

		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta T4821DAO. [LISTA_SOLICITUDES]");
		}	
		return lista;
	}
	
	public List listaSolicitudesAdministracion(String dbpool, String anno, String numero, //jquispecoi 03/2014
			String coduo, String codPers, String tipo) throws SQLException {

		List lista = null;
		try{
			lista = executeQuery(dataSource, LISTA_SOLICITUDES_ADMINISTRACION.toString(), new Object[] {
					anno, numero, coduo, codPers, tipo });

		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta T4821DAO. [LISTA_SOLICITUDES]");
		}	
		return lista;
	}
	
	public Map listaDetalle(String anno, String numero,
			String coduo, String codPers, String tipo, int num_detalle) throws SQLException {

		Map detalle = new HashMap();

		detalle = executeQueryUniqueResult(dataSource, LISTA_DETALLE.toString(), new Object[] {
				anno, numero, coduo, codPers, tipo, new Integer(num_detalle) });

		return detalle;

	}
	
	//INICIO ICAPUNAY
	/**
	 * Metodo que se encarga de registrar un intervalo de compensacion autorizado, no autorizado o rechazado
	 * @param params Map (cod_pers,fec_perm,hor_ini_perm,hor_fin_perm,cod_uorga,hor_ini_aut,cod_jefe_aut,hor_ini_comp,hor_fin_comp,cnt_min_comp_acu,ind_comp,cnt_min_comp_sal,cod_user_crea,fec_creacion)  
	 * @return insertado boolean	 
	 * @throws DAOException
	 */	
	public boolean insertDetalleSolicitud(Map params) throws DAOException {
	
		int insertado=0;
		if (log.isDebugEnabled()) log.debug("T4821DAO - insertDetalleSolicitud - params " + params);		
		
		try{
			insertado = executeUpdate(dataSource,INSERTAR_DETALLE.toString(), new Object[]{params.get("ann_sol"),params.get("num_sol"),params.get("cod_uo_sol"),
				params.get("cod_pers_sol"),params.get("cod_mov_sol"),params.get("fec_registro_sol"),params.get("num_detalle"),params.get("fec_permiso"),
				params.get("hor_ini_permiso"),params.get("hor_fin_permiso"),params.get("obs_sustento_obs"),params.get("cod_user_crea"),params.get("fec_creacion")});
		} catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [INSERTAR_DETALLE]");
		}		
		return (insertado>0);
	}	
	
	/**
	  * Metodo que devuelve intervalos intersectados de solicitudes en seguimiento para un intervalo de permanencia excepcional (124) a insertar para un colaborador y fecha	
	  * @param codPers String
	  * @param codMov String	 
	  * @param mapaNoAut Map
	  * @return intervaloIntersectado List
	  * @throws DAOException
	  */
	public List findIntervalosIntersectados(String codMov,Map mapaNoAut) throws DAOException {
		
		List intervaloIntersectado = null;
		String codPers=mapaNoAut.get("cod_pers").toString();
		if (log.isDebugEnabled()) log.debug("T4821DAO - findIntervaloIntersectado - codPers: " + codPers);
		String horaIniComp=mapaNoAut.get("hor_ini_comp").toString();
		if (log.isDebugEnabled()) log.debug("T4821DAO - findIntervaloIntersectado - horaIniComp: " + horaIniComp);
		String horaFinComp=mapaNoAut.get("hor_fin_comp").toString();
		if (log.isDebugEnabled()) log.debug("T4821DAO - findIntervaloIntersectado - horaFinComp: " + horaFinComp);
		Date fechaPermiso = (Date)mapaNoAut.get("fec_perm");
		if (log.isDebugEnabled()) log.debug("T4821DAO - findIntervaloIntersectado - fechaPermiso: " + fechaPermiso);
		
		if (log.isDebugEnabled()) log.debug("T4821DAO - findIntervaloIntersectado - mapaNoAut: " + mapaNoAut);	
		
		try{			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);			
			//intervaloIntersectado = executeQuery(dataSource,FIND_INTERVALOS_INTERSECTADOS.toString(), new Object[]{codPers,codMov,fechaPermiso,horaIniComp,horaIniComp,horaFinComp,horaFinComp}); //add ICR 19/12 seguimiento 0 (colab) y 1 (superv)
			intervaloIntersectado = executeQuery(dataSource,FIND_INTERVALOS_INTERSECTADOS.toString(), new Object[]{"2","2","3","2",codPers,codMov,fechaPermiso,horaIniComp,horaIniComp,horaFinComp,horaFinComp}); //add ICR 20/12 excluye aprobadas {2,2} y rechazadas {3,2} (solo obtiene en seguimiento)
			
			/*//no funciona
			 intervaloIntersectado = executeQuery(dataSource,FIND_INTERVALOS_INTERSECTADOS.toString(), new Object[]{"1","1","0",codPers,codMov,fechaPermiso,horaIniComp,horaIniComp,horaFinComp,horaFinComp}); //add ICR 19/12 seguimiento 0 (colab)
			 if (intervaloIntersectado!=null && intervaloIntersectado.size()>0){
				if (log.isDebugEnabled()) log.debug("Intervalo a intersectar: "+horaIniComp+"-"+horaFinComp+" intersectado en otras intervalos de solicitudes de labor en seguimiento (registrado x colab)");
			}else{
				intervaloIntersectado = executeQuery(dataSource,FIND_INTERVALOS_INTERSECTADOS.toString(), new Object[]{"2","1","1",codPers,codMov,fechaPermiso,horaIniComp,horaIniComp,horaFinComp,horaFinComp}); //add ICR 19/12 seguimiento 1 (superv)
				if (intervaloIntersectado!=null && intervaloIntersectado.size()>0){
					if (log.isDebugEnabled()) log.debug("Intervalo a intersectar: "+horaIniComp+"-"+horaFinComp+" intersectado en otras intervalos de solicitudes de labor en seguimiento (registrado x superv)");
				}
			}*/
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta T4821DAO. [FIND_INTERVALOS_INTERSECTADOS]. Intervalo a intersectar intersectado en otras intervalos de solicitudes de labor en seguimiento.");
		}	
		return intervaloIntersectado;
	}
	
	
	//FIN ICAPUNAY
	
	/**NVILLAR - 14/03/2012 - LABOR EXCEPCIONAL
	 * Metodo encargado de buscar el detalle de notificaciones a directivos 
	 * de acuerdo a criterio de busqueda
	 * @param Map datos
	 * @return List 
	 * @throws DAOException
	 */	
	public List findDetalleSolicitudLE(Map params) throws DAOException {
		StringBuffer sSQL = new StringBuffer(FIND_CONSULTA_LAB_EXCEPCIONAL.toString()); 
		List lista = null;
		try {		
			String criterio = (params.get("criterio") != null ) ? params.get("criterio").toString():""; // 0=LaborExcepcional, 1=Compensaciones, 2=Todos
			String estado = (params.get("estado") != null ) ? params.get("estado").toString():""; // 0=Todos, 1=Iniciada, 2=Aprobada, 3=Rechazada
			String fechaIni = (params.get("fechaIni") != null ) ? params.get("fechaIni").toString():""; //Fecha inicio
			String fechaFin = (params.get("fechaFin") != null ) ? params.get("fechaFin").toString():""; //Fecha fin
			String anio = (params.get("anio") != null ) ? params.get("anio").toString():"";
			String numero = (params.get("numero") != null ) ? params.get("numero").toString():"";
			String codPers = (params.get("codPers") != null ) ? params.get("codPers").toString():"";

			if (log.isDebugEnabled()) log.debug("criterio: " + params.get("criterio"));
			if (log.isDebugEnabled()) log.debug("estado: " + params.get("estado"));
			if (log.isDebugEnabled()) log.debug("fechaIni: " + params.get("fechaIni"));	
			if (log.isDebugEnabled()) log.debug("fechaFin: " + params.get("fechaFin"));
			if (log.isDebugEnabled()) log.debug("anio: " + params.get("anio"));	
			if (log.isDebugEnabled()) log.debug("numero: " + params.get("numero"));
			
			if (log.isDebugEnabled()) log.debug("fechaIni: " + fechaIni);
			if (log.isDebugEnabled()) log.debug("fechaFin: " + fechaFin);
			Date fechIni = new Date();
			Date fechFin = new Date();
			fechIni = new FechaBean(fechaIni).getSQLDate();
			fechFin = new FechaBean(fechaFin).getSQLDate();
			if (log.isDebugEnabled()) log.debug("fechIni: " + fechIni);
			if (log.isDebugEnabled()) log.debug("fechFin: " + fechFin);

			//rango de fechas de la consulta
			sSQL.append(" AND date(s.fec_permiso) between '").append( fechIni).append( "' and '").append( fechFin).append("' ");

			//criterios de busqueda
			//tipo de solicitud (obligatorio)
			if (criterio.equals("0")){ //0=LaborExcepcional				
				sSQL.append(" AND r.licencia = '").append(constantes.leePropiedad("LABOR_EXCEPCIONAL")).append("' ");
			} else if(criterio.equals("1")){//1=Compensaciones			
				sSQL.append(" AND r.licencia = '").append(constantes.leePropiedad("COMPENSACION")).append("' ");
			}else if(criterio.equals("2")){//2=Todos
				sSQL.append(" AND r.licencia in ('" ).append(constantes.leePropiedad("LABOR_EXCEPCIONAL")).append("','").append(constantes.leePropiedad("COMPENSACION")).append("') ");
			}		

			//estado de solicitud (opcional)
			if (estado.equals("0")) { //0=Todos				
				//sSQL.append(" AND t.accion_id = '").append(constantes.leePropiedad("ACCION_INICIAR")).append("' ");
			}else if(estado.equals("1")){//1=Iniciada
				sSQL.append(" AND t.accion_id = '").append(constantes.leePropiedad("ACCION_INICIAR")).append("' ");
			}else if(estado.equals("2")){//2=Aprobada
				sSQL.append(" AND t.accion_id = '").append(constantes.leePropiedad("ACCION_APROBAR")).append("' ");
			}else if(estado.equals("3")){//3=Rechazada
				sSQL.append(" AND t.accion_id = '").append(constantes.leePropiedad("ACCION_RECHAZAR")).append("' ");
			}

			//numero de solicitud (opcional)
			if (anio.trim()!=""){ //0=LaborExcepcional				
				sSQL.append(" AND r.anno = '").append(anio).append("' AND r.numero = '").append(numero).append("' ");
			}
            
			//consulta solo del colaborador solicitante
			sSQL.append(" AND r.cod_pers = '").append(codPers.trim().toUpperCase()).append("' ");
			//sSQL.append(" AND r.cod_pers = '1548' "); // pruebas desarrollo
			
			sSQL.append(" ORDER BY 1,2 ");

			setIsolationLevel(T02DAO.TX_READ_UNCOMMITTED);
			lista = (ArrayList) executeQuery(dataSource, sSQL.toString());
			setIsolationLevel(-1);

		} catch (Exception e) {
			log.error("*** SQL Error ****",e);
			throw new DAOException(this, "Error en la consulta. ReporteDAO - [findDetalleNotificaDirec]");
		} finally {	
		}
		return lista;			
	} //FIN - NVILLAR - 14/03/2012 - LABOR EXCEPCIONAL

    
	/**NVILLAR - 02/05/2012 - LABOR EXCEPCIONAL
	 * Metodo encargado de buscar el detalle de notificaciones a directivos 
	 * de acuerdo a criterio de busqueda
	 * @param Map datos
	 * @return List 
	 * @throws DAOException
	 */	
	public List findDetalleSolicitudLE2(Map params) throws DAOException {

		StringBuffer sSQL = new StringBuffer(FIND_CONSULTA_LAB_EXCEPCIONAL2.toString()); 
		List lista = null;
		try {		
			String criterio = (params.get("criterio") != null ) ? params.get("criterio").toString():""; // 0=LaborExcepcional, 1=Compensaciones, 2=Todos
			String valor = (params.get("valor") != null ) ? params.get("valor").toString():""; 
			String fechaIni = (params.get("fechaIni") != null ) ? params.get("fechaIni").toString():""; //Fecha inicio
			String fechaFin = (params.get("fechaFin") != null ) ? params.get("fechaFin").toString():""; //Fecha fin
			String regimen = (params.get("regimen") != null ) ? params.get("regimen").toString():"";
			String solicitud1 = (params.get("solicitud1") != null ) ? params.get("solicitud1").toString():"";
			String solicitud2 = (params.get("solicitud2") != null ) ? params.get("solicitud2").toString():"";
			String solicitud3 = (params.get("solicitud3") != null ) ? params.get("solicitud3").toString():"";
			Map seguridad = (HashMap)params.get("seguridad");
			if (log.isDebugEnabled()) log.debug("criterio: " + params.get("criterio"));
			if (log.isDebugEnabled()) log.debug("estado: " + params.get("estado"));
			if (log.isDebugEnabled()) log.debug("fechaIni: " + params.get("fechaIni"));	
			if (log.isDebugEnabled()) log.debug("fechaFin: " + params.get("fechaFin"));
			if (log.isDebugEnabled()) log.debug("anio: " + params.get("anio"));	
			if (log.isDebugEnabled()) log.debug("numero: " + params.get("numero"));
			if (log.isDebugEnabled()) log.debug("fechaIni: " + fechaIni);
			if (log.isDebugEnabled()) log.debug("fechaFin: " + fechaFin);
			if (log.isDebugEnabled()) log.debug("solicitud1: " + solicitud1);
			if (log.isDebugEnabled()) log.debug("solicitud2: " + solicitud2);
			if (log.isDebugEnabled()) log.debug("solicitud3: " + solicitud3);
			Date fechIni = new Date();
			Date fechFin = new Date();
			fechIni = new FechaBean(fechaIni).getSQLDate();
			fechFin = new FechaBean(fechaFin).getSQLDate();
			if (log.isDebugEnabled()) log.debug("fechIni: " + fechIni);
			if (log.isDebugEnabled()) log.debug("fechFin: " + fechFin);

			//tipo de Regimen/Modalidad
            if (regimen.equals("0")){ //Regimen 276 - 728
                sSQL.append("and v.t02cod_rel not in('09','10') ");                
            }else if (regimen.equals("1")){ //Regimen 1057
                sSQL.append("and v.t02cod_rel = '09' ");
            }else if (regimen.equals("2")){ //Modalidad Formativa
                sSQL.append("and v.t02cod_rel = '10' ");
            }
			
			//tipo de solicitud
            if (solicitud3.equals("0"))
            {
                  if (solicitud1.equals("1") && solicitud2.equals("0")){
                       sSQL.append(" and r.licencia = '124' and  r.est_id = '1' ");}

                  if (solicitud1.equals("0") && solicitud2.equals("1")){
                       sSQL.append(" and r.licencia = '124' and  r.est_id = '2' ");}

                  if (solicitud1.equals("1") && solicitud2.equals("1")){
                       sSQL.append(" and r.licencia = '124' ");}
                  
                  if (solicitud1.equals("0") && solicitud2.equals("0")){
                       sSQL.append(" and r.licencia in ( '124', '125') ");}

            } else {

                  if (solicitud1.equals("1") && solicitud2.equals("0")){
                       sSQL.append(" and (r.licencia = '125' or (r.licencia = '124' and r.est_id = '1') ) ");}
                  
                  if (solicitud1.equals("0") && solicitud2.equals("1")){
                  
                       sSQL.append(" and (r.licencia = '125' or (r.licencia = '124' and r.est_id = '2') ) ");}

                  if (solicitud1.equals("1") && solicitud2.equals("1")){
                       sSQL.append(" and r.licencia in ( '124', '125') ");}
                  
                  if (solicitud1.equals("0") && solicitud2.equals("0")){
                       sSQL.append(" and r.licencia in ( '125') ");}
            }
            if (log.isDebugEnabled()) log.debug("sSQL: " + sSQL);

			
			//rango de fechas de la consulta
			sSQL.append(" AND date(s.fec_permiso) between '").append( fechIni).append( "' and '").append( fechFin).append("' ");
			
			//criterios de consulta
			if(criterio.trim().equals("0")){ // la opcion escogida es REGISTRO
				sSQL.append(" AND r.cod_pers = '").append(valor.trim()).append("' ");
			}
			
			if(criterio.trim().equals("1")){ // la opcion escogida es UND. ORGANIZACIONAL
				sSQL.append(" AND substr(trim(nvl(v.t02cod_uorgl,'')||nvl(v.t02cod_uorg,'')),1,6) = '").append(valor.trim()).append("' ");
			}
			if(criterio.trim().equals("5")){ // la opcion escogida es INTENDENCIA
				//criterio = "Intendencia";
				String intendencia = valor.toString().trim().substring(0,2); // 2A de 2A0000
				
				sSQL.append(" and substr(trim(nvl(v.t02cod_uorgl,'')||nvl(v.t02cod_uorg,'')),1,6) LIKE '")
				.append( intendencia.trim().toUpperCase() ).append( "%'");				
			}
			
			if(criterio.trim().equals("4")){ // la opcion escogida es INSTITUCIONAL
				//criterio = "Institucional";
			}
	
			//criterios de visibilidad
			if (seguridad != null && seguridad.size()>0) {
				Map roles = (HashMap) seguridad.get("roles");
				String uoSeg = (String) seguridad.get("uoSeg");
				String codPerso = (String) seguridad.get("codPers");
				String uoAO = (String) seguridad.get("uoAO");//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

				
				log.debug("findDetalleSolicitudLE_Seguridad / roles: " + roles);
				log.debug("findDetalleSolicitudLE_Seguridad / uoSeg: " + uoSeg);
				log.debug("findDetalleSolicitudLE_Seguridad / codSeg: " + codPerso);

				if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {//Visualiza a todos
					sSQL.append(" AND 1=1 ");					
					log.debug(" AND 1=1 ");

				} else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null //Visualiza solo a los de la UO a la cual pertenece el usuario logueado
						|| roles.get(Constantes.ROL_SECRETARIA) != null
						|| roles.get(Constantes.ROL_JEFE) != null) {

					//para que rol_jefe pueda visualizar los colaboradores de su area
					if (log.isDebugEnabled()) log.debug("uoSeg: "+uoSeg);
					if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null){
						uoSeg = uoSeg.substring(0,2).concat("%");
						if (log.isDebugEnabled()) log.debug("uoSeg final de ROL_ANALISTA_OPERATIVO: "+uoSeg);
					}
					if (roles.get(Constantes.ROL_JEFE)!=null){
						if (log.isDebugEnabled()) log.debug("uoSeg inicial de ROL_JEFE: "+uoSeg);
						if (log.isDebugEnabled()) log.debug("uoSeg.length(): "+uoSeg.length());

						for(int p = uoSeg.length()-1; p == 0; p--){
							String letra = uoSeg.substring(p,p+1).trim();
							if(!letra.equals("0")){
								uoSeg = uoSeg.substring(0,p+1).concat("%");
								p = 0;
							}
						}						
						if (log.isDebugEnabled()) log.debug("uoSeg final de ROL_JEFE: "+uoSeg);
					}
					log.debug(" AND SUBSTR(TRIM(NVL(T02COD_UORGL,'')||NVL(T02COD_UORG,'')),1,6) LIKE '"+uoSeg+"'");
					//para que rol_jefe pueda visualizar los colaboradores de su area

					if(roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null){
						sSQL.append(" AND ((SUBSTR(TRIM(NVL(T02COD_UORGL,'')||NVL(T02COD_UORG,'')),1,6) LIKE '").append(uoSeg).append( "') ");
						
						sSQL.append(" OR (SUBSTR(TRIM(NVL(T02COD_UORGL,'')||NVL(T02COD_UORG,'')),1,6) IN ")
						.append("  (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '")
						.append(uoAO).
						append( "'))) ");
						
					}else{
					sSQL.append(" AND SUBSTR(TRIM(NVL(T02COD_UORGL,'')||NVL(T02COD_UORG,'')),1,6) LIKE '").append(uoSeg).append( "' ");
					}

				} else { //No visualiza ninguno
					sSQL.append(" AND 1=2 ");					
					log.debug(" AND 1=2 ");
				}
			}
			sSQL.append(" ORDER BY 1,2,3,4 ");
			setIsolationLevel(T02DAO.TX_READ_UNCOMMITTED);
			lista = (ArrayList) executeQuery(dataSource, sSQL.toString());
			setIsolationLevel(-1);
		} catch (Exception e) {
			log.error("*** SQL Error ****",e);
			throw new DAOException(this, "Error en la consulta. ReporteDAO - [findDetalleNotificaDirec]");
		} finally {	
		}
		return lista;			
	} //FIN - NVILLAR - 02/05/2012 - LABOR EXCEPCIONAL
	

	/**NVILLAR - 19/03/2012 - LABOR EXCEPCIONAL
	 * Metodo encargado de buscar el detalle de notificaciones a directivos 
	 * de acuerdo a criterio de busqueda
	 * @param Map datos
	 * @return List 
	 * @throws DAOException
	 */	
	public List buscaDetalleConsultaSolicitudesLE(Map datos) throws DAOException {
		StringBuffer sSQL = new StringBuffer(FIND_DETALLE_CONSULTA_LAB_EXCEPCIONAL.toString()); 
		List lista = null;
		try {		
			if (log.isDebugEnabled()) log.debug("det_anio: " + datos.get("anio"));
			if (log.isDebugEnabled()) log.debug("det_numero: " + datos.get("numero"));
			if (log.isDebugEnabled()) log.debug("licencia: " + datos.get("licencia"));
			if (log.isDebugEnabled()) log.debug("fecha_desc: " + datos.get("fecha_desc"));// 01/01/2012

			String anio = (datos.get("anio") != null ) ? datos.get("anio").toString():""; //Ejm. 2012
			String numero = (datos.get("numero") != null ) ? datos.get("numero").toString():""; //Ejm. 320
			String licencia = (datos.get("licencia") != null ) ? datos.get("licencia").toString():""; //licencia. 1=Iniciada, 2=Aprobada, 3=Rechazada
			String fecha_desc = (datos.get("fecha_desc") != null ) ? datos.get("fecha_desc").toString():"";

			Map seguridad = (HashMap)datos.get("seguridad");

			//if (log.isDebugEnabled()) log.debug("fecha_desc: " + fecha_desc);
			Date fechDes = new Date();
			fechDes = new FechaBean(fecha_desc).getSQLDate();
			if (log.isDebugEnabled()) log.debug("fechDes: " + fechDes);

			//criterios de visibilidad
			if (seguridad != null && seguridad.size()>0) {
				Map roles = (HashMap) seguridad.get("roles");
				String uoSeg = (String) seguridad.get("uoSeg");
				String codPers = (String) seguridad.get("codPers");
				log.debug("findDetalleSolicitudLE_Seguridad / roles: " + roles);
				log.debug("findDetalleSolicitudLE_Seguridad / uoSeg: " + uoSeg);
				log.debug("findDetalleSolicitudLE_Seguridad / codSeg: " + codPers);
				if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {//Visualiza a todos
					//sSQL.append(" AND t.cod_pers='").append(codPers).append("' ");					
					//log.debug(" AND 1=1 ");
				} else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null //Visualiza solo a los de la UO a la cual pertenece el usuario logueado
						|| roles.get(Constantes.ROL_SECRETARIA) != null
						|| roles.get(Constantes.ROL_JEFE) != null) {

					//ICAPUNAY 28/11/2011 para que rol_jefe pueda visualizar los colaboradores de su area
					if (log.isDebugEnabled()) log.debug("uoSeg: "+uoSeg);
					if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null){
						uoSeg = uoSeg.substring(0,2).concat("%");
						if (log.isDebugEnabled()) log.debug("uoSeg final de ROL_ANALISTA_OPERATIVO: "+uoSeg);
					}
					if (roles.get(Constantes.ROL_JEFE)!=null){
						if (log.isDebugEnabled()) log.debug("uoSeg inicial de ROL_JEFE: "+uoSeg);
						if (log.isDebugEnabled()) log.debug("uoSeg.length(): "+uoSeg.length());

						for(int p = uoSeg.length()-1; p == 0; p--){
							/*letra = uoSeg.substring(p,p+1).trim();
							if(!letra.equals("0")){
								uoSeg = uoSeg.substring(0,p+1).concat("%");
								p = 0;
							}*/
						}						
						if (log.isDebugEnabled()) log.debug("uoSeg final de ROL_JEFE: "+uoSeg);
					}
					log.debug(" AND SUBSTR(TRIM(NVL(T02COD_UORGL,'')||NVL(T02COD_UORG,'')),1,6) LIKE '"+uoSeg+"'");
					//ICAPUNAY 28/11/2011 para que rol_jefe pueda visualizar los colaboradores de su area

					//sSQL.append(" AND SUBSTR(TRIM(NVL(T02COD_UORGL,'')||NVL(T02COD_UORG,'')),1,6) LIKE '").append(uoSeg).append( "' ");

				} else { //No visualiza ninguno
					//sSQL.append(" AND 1=2 ");					
					log.debug(" AND 1=2 ");
				}
			}
			sSQL.append(" ORDER BY 1,2");
			setIsolationLevel(T02DAO.TX_READ_UNCOMMITTED);
			//lista = (ArrayList) executeQuery(dataSource, sSQL.toString(), new Object[]{ anio, numero, licencia, fechDes}); 
			lista = (ArrayList) executeQuery(dataSource, sSQL.toString(), new Object[]{ anio, numero, licencia}); 
			setIsolationLevel(-1);
		} catch (Exception e) {
			log.error("*** SQL Error ****",e);
			throw new DAOException(this, "Error en la consulta. T4821DAO - [buscaDetalleConsultaSolicitudesLE]");
		} finally {	
		}
		return lista;			
	} //FIN - NVILLAR - 19/03/2012 - LABOR EXCEPCIONAL
	
	
	
	/**NVILLAR - 02/05/2012 - LABOR EXCEPCIONAL
	 * Metodo encargado de buscar el detalle de notificaciones a directivos 
	 * de acuerdo a criterio de busqueda
	 * @param Map datos
	 * @return List 
	 * @throws DAOException
	 */	
	public List buscaDetalleConsultaSolicitudesLE2(Map datos) throws DAOException {

		StringBuffer sSQL = new StringBuffer(FIND_DETALLE_CONSULTA_LAB_EXCEPCIONAL2.toString()); 
		List lista = null;
		try {		
			if (log.isDebugEnabled()) log.debug("DAO_anio: " + datos.get("anio"));
			if (log.isDebugEnabled()) log.debug("DAO_numero: " + datos.get("numero"));
			if (log.isDebugEnabled()) log.debug("DAO_registro: " + datos.get("registro"));
			if (log.isDebugEnabled()) log.debug("DAO_licencia: " + datos.get("licencia"));
			if (log.isDebugEnabled()) log.debug("DAO_fecha: " + datos.get("fecha"));// 01/01/2012

			String anio = (datos.get("anio") != null ) ? datos.get("anio").toString():""; //Ejm. 2012
			String numero = (datos.get("numero") != null ) ? datos.get("numero").toString():""; //Ejm. 320
			String registro = (datos.get("registro") != null ) ? datos.get("registro").toString():""; //Ejm. 1548
			String licencia = (datos.get("licencia") != null ) ? datos.get("licencia").toString():""; //licencia. 1=Iniciada, 2=Aprobada, 3=Rechazada
			String fecha = (datos.get("fecha") != null ) ? datos.get("fecha").toString():"";

			Map seguridad = (HashMap)datos.get("seguridad");

			//if (log.isDebugEnabled()) log.debug("fecha_desc: " + fecha_desc);
			Date fechDes = new Date();
			fechDes = new FechaBean(fecha).getSQLDate();
			if (log.isDebugEnabled()) log.debug("fechDes: " + fechDes);

			//criterios de visibilidad
			if (seguridad != null && seguridad.size()>0) {
				Map roles = (HashMap) seguridad.get("roles");
				String uoSeg = (String) seguridad.get("uoSeg");
				String codPerso = (String) seguridad.get("codPers");
				String uoAO = (String) seguridad.get("uoAO");
				log.debug("findDetalleSolicitudLE_Seguridad / roles: " + roles);
				log.debug("findDetalleSolicitudLE_Seguridad / uoSeg: " + uoSeg);
				log.debug("findDetalleSolicitudLE_Seguridad / codSeg: " + codPerso);

				if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {//Visualiza a todos
					sSQL.append(" AND 1=1 ");					
					log.debug(" AND 1=1 ");

				} else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null //Visualiza solo a los de la UO a la cual pertenece el usuario logueado
						|| roles.get(Constantes.ROL_SECRETARIA) != null
						|| roles.get(Constantes.ROL_JEFE) != null) {

					//para que rol_jefe pueda visualizar los colaboradores de su area
					if (log.isDebugEnabled()) log.debug("uoSeg: "+uoSeg);
					if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null){
						uoSeg = uoSeg.substring(0,2).concat("%");
						if (log.isDebugEnabled()) log.debug("uoSeg final de ROL_ANALISTA_OPERATIVO: "+uoSeg);
					}
					if (roles.get(Constantes.ROL_JEFE)!=null){
						if (log.isDebugEnabled()) log.debug("uoSeg inicial de ROL_JEFE: "+uoSeg);
						if (log.isDebugEnabled()) log.debug("uoSeg.length(): "+uoSeg.length());

						for(int p = uoSeg.length()-1; p == 0; p--){
							String letra = uoSeg.substring(p,p+1).trim();
							if(!letra.equals("0")){
								uoSeg = uoSeg.substring(0,p+1).concat("%");
								p = 0;
							}
						}						
						if (log.isDebugEnabled()) log.debug("uoSeg final de ROL_JEFE: "+uoSeg);
					}
					log.debug(" AND SUBSTR(TRIM(NVL(T02COD_UORGL,'')||NVL(T02COD_UORG,'')),1,6) LIKE '"+uoSeg+"'");
					//para que rol_jefe pueda visualizar los colaboradores de su area

					if(roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null){
						sSQL.append(" AND ((SUBSTR(TRIM(NVL(T02COD_UORGL,'')||NVL(T02COD_UORG,'')),1,6) LIKE '").append(uoSeg).append( "') ");
						
						sSQL.append(" OR (SUBSTR(TRIM(NVL(T02COD_UORGL,'')||NVL(T02COD_UORG,'')),1,6) IN ")
						.append("  (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '")
						.append(uoAO).
						append( "'))) ");
						
					}else{
					sSQL.append(" AND SUBSTR(TRIM(NVL(T02COD_UORGL,'')||NVL(T02COD_UORG,'')),1,6) LIKE '").append(uoSeg).append( "' ");
					}
					

				} else { //No visualiza ninguno
					sSQL.append(" AND 1=2 ");					
					log.debug(" AND 1=2 ");
				}
			}
			sSQL.append(" ORDER BY 1,2");
			setIsolationLevel(T02DAO.TX_READ_UNCOMMITTED);
			//lista = (ArrayList) executeQuery(dataSource, sSQL.toString(),new Object[]{ anio, numero, registro, licencia, fechDes});
			lista = (ArrayList) executeQuery(dataSource, sSQL.toString(),new Object[]{ anio, numero, registro, licencia}); 
			setIsolationLevel(-1);
		} catch (Exception e) {
			log.error("*** SQL Error ****",e);
			throw new DAOException(this, "Error en la consulta. T4821DAO - [buscaDetalleConsultaSolicitudesLE]");
		} finally {	
		}
		return lista;			
	} //FIN - NVILLAR - 02/05/2012 - LABOR EXCEPCIONAL

	/** MTM - LABOR EXCEPCIONAL
	 * Metodo verifica registro de compensacion para una determinada fecha
	 * @throws SQLException
	 */
	public boolean verificaRegistroCompensacion(String dbpool,
			String userOrig, String fecha)
			throws SQLException {
		List anno = null;
		String cod_pers = null;
			log.debug("anno1");
			anno = executeQuery(dataSource, FIND_FECHA_PERMANENCIA.toString(), new Object[] {
				userOrig, new FechaBean(fecha).getSQLDate()});
			log.debug("anno2");
			log.debug(anno.size()+"");
			log.debug(anno+"");
			for(int i=0;i <anno.size();i++){
				log.debug(anno.get(i).toString());
				cod_pers = anno.get(i).toString();
				if (cod_pers != null){
					return true;
				}
			}
			return false;
	}
	
		public boolean verificaRegistroSolicitudCompensacion(String dbpool,
			//String codUO, String userOrig, String codMov, String fecha) throws SQLException {			
			String userOrig, String codMov, String fecha) throws SQLException {	
			List listaCompensa = new ArrayList();			
			if (log.isDebugEnabled()) log.debug("T4821DAO - verificaRegistroSolicitudCompensacion - userOrig: " + userOrig);
			if (log.isDebugEnabled()) log.debug("T4821DAO - verificaRegistroSolicitudCompensacion - codMov: " + codMov);
			if (log.isDebugEnabled()) log.debug("T4821DAO - verificaRegistroSolicitudCompensacion - fecha: " + fecha);
			if (log.isDebugEnabled()) log.debug("listaCompensa: "+listaCompensa);
			Date fechaEval = new FechaBean(fecha).getSQLDate();
			if (log.isDebugEnabled()) log.debug("fechaEval: "+fechaEval);
			try{	
				//anno = executeQuery(dataSource, VERIFICA_REGISTRO_COMPENSACION.toString(), new Object[] {fechaEval,codUO, userOrig, codMov });
				listaCompensa = executeQuery(dataSource, VERIFICA_REGISTRO_COMPENSACION.toString(), new Object[] {fechaEval,userOrig, codMov,"1","1","0"});//icr se comenta uuoo porque puede variar //add ICR seguimiento x registro de colab o aprob de supervisor
							
			}catch (Exception e) {
				log.error("*** SQL Error ****", e);
				throw new DAOException(this, "Error en consulta T4821DAO. [verificaRegistroSolicitudCompensacion]");
			}		
			//if (anno.size() == 0) {
			if (listaCompensa!=null && listaCompensa.size()>0){
				if (log.isDebugEnabled()) log.debug("listaCompensa1: "+listaCompensa);
				if (log.isDebugEnabled()) log.debug("SI existe solicitud de compensacion en seguimiento(registrada x colab)");
				return true;
			} else {
				listaCompensa = executeQuery(dataSource, VERIFICA_REGISTRO_COMPENSACION.toString(), new Object[] {fechaEval,userOrig, codMov,"2","1","1"});//icr se comenta uuoo porque puede variar //add ICR seguimiento x registro de colab o aprob de supervisor
				if (log.isDebugEnabled()) log.debug("listaCompensa2: "+listaCompensa);
				if (listaCompensa!=null && listaCompensa.size()>0){					
					if (log.isDebugEnabled()) log.debug("SI existe solicitud de compensacion en seguimiento(aprob x superv)");
					return true;
				}else{
					return false;
				}				
			}
		}
		
		//ICAPUNAY 05/10/2012 AJUSTES AOM 06A4T11 LABOR EXCEPCIONAL
		public List findFechasPermiso(String codPers,String anno,String numero) throws SQLException {
				List lista = null;
				if (log.isDebugEnabled()) log.debug("T4821DAO - findFechasPermiso - codPers: " + codPers);
				if (log.isDebugEnabled()) log.debug("T4821DAO - findFechasPermiso - anno: " + anno);
				if (log.isDebugEnabled()) log.debug("T4821DAO - findFechasPermiso - numero: " + numero);
			try{	
				
				lista = executeQuery(dataSource, FIND_FECHAS_PERMISO.toString(), new Object[] {codPers,anno,numero});

			}catch (Exception e) {
				log.error("*** SQL Error ****", e);
				throw new DAOException(this, "Error en consulta T4821DAO. [FIND_FECHAS_PERMISO]");
			}	
			return lista;
		}
		//FIN ICAPUNAY 05/10/2012 AJUSTES AOM 06A4T11 LABOR EXCEPCIONAL
		//dtarazona findSolVacRepByNum
		public List findSolVacRepByNum(String pool,String codPers,String numero,String anno) throws SQLException {
				List lista = null;
				if (log.isDebugEnabled()) log.debug("T4821DAO - findFechasPermiso - codPers: " + codPers);
				if (log.isDebugEnabled()) log.debug("T4821DAO - findFechasPermiso - anno: " + pool);
				if (log.isDebugEnabled()) log.debug("T4821DAO - findFechasPermiso - numero: " + numero);
			try{	
				if(log.isDebugEnabled()) log.debug("Anno De solicitud:"+anno);
				String sql="select d.ann_sol,d.hor_ini_permiso,d.obs_sustento_obs,to_char(d.fec_permiso,'%d/%m/%Y') as fec_permiso from t4821solicituddet d where num_sol=? and cod_pers_sol=? and cod_mov_sol=?";
				lista = executeQuery(dataSource, sql.toString(), new Object[] {numero,codPers,anno});
	
			}catch (Exception e) {
				log.error("*** SQL Error ****", e);
				throw new DAOException(this, "Error en consulta T4821DAO. [FIND_FECHAS_PERMISO]");
			}	
			return lista;
		}
		//fin dtarazona
		/**
		 * Metodo que actualiza Fechas de la solicitud Solicitud: jquispecoi: 03/2014
		 * @throws SQLException
		 */
		public boolean updateRegistroSolicitudDetFecha(String fec_permiso_final, String cod_user_mod, 
				String anno, int numero, String cod_pers, String fec_permiso)throws SQLException {

			String strUpd = "";
			PreparedStatement pre = null;
			Connection con = null;
			boolean result = false;

			try {
				strUpd = "update t4821solicituddet set fec_permiso=?, cod_user_mod=?, fec_modifica=? "+
					     "where ann_sol=? and num_sol=? and cod_pers_sol=? and fec_permiso=? ";
				con = getConnection(dataSource);
				pre = con.prepareStatement(strUpd);
				
				pre.setDate(1, new FechaBean(fec_permiso_final).getSQLDate());
				pre.setString(2, cod_user_mod);
				pre.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
				pre.setString(4, anno);
				pre.setInt(5, numero);
				pre.setString(6, cod_pers);
				pre.setDate(7, new FechaBean(fec_permiso).getSQLDate());
				
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

}
