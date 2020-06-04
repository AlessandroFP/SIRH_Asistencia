package pe.gob.sunat.rrhh.asistencia.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import javax.sql.DataSource;

import pe.gob.sunat.framework.core.bean.MensajeBean;
import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.Propiedades;
import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.sol.BeanFechaHora;
import pe.gob.sunat.sp.asistencia.bean.BeanReporte;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;

/** 
 * 
 * Clase       : ReporteDAO 
 * Descripcion : Clase encargada de administrar las consultas para la realizacion
 * de reportes
 * Proyecto    : ASISTENCIA 
 * @author     : 
 * Fecha       : 30-SEP-2008 
 * 
 * */

public class ReporteDAO extends DAOAbstract {
	
	private DataSource dataSource = null;
	Propiedades constantes = new Propiedades(getClass(),"/constantes.properties");
	
	/*
	public static final String CRITERIO_COD = "0";
	public static final String CRITERIO_UUOO = "1";
	public static final String CRITERIO_DEP = "4";
	*/
	private final StringBuffer FIND_PERSONAL_BY_CRITERIO_VALOR_INACT =  
		new StringBuffer("select")
		.append( " p.t02cod_pers, p.t02ap_pate, p.t02ap_mate, p.t02nombres, ")
		.append( " p.t02cod_uorgl,p.t02cod_uorg, ")
		.append( " p.t02f_ingsun,uo.t12des_uorga,uo.t12des_corta,param.t99descrip ")
		.append( " from    t02perdp p, ")
		.append( " t12uorga uo, ")
		.append( " t99codigos param ");
	
	private final StringBuffer FIND_PERSONAL_BY_CRITERIO_VALOR_ACT =  
		new StringBuffer("select")
		.append( " p.t02cod_rel, p.t02cod_pers, p.t02ap_pate, p.t02ap_mate, p.t02nombres, ")
		.append( " substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) cod_uorg, ")				
		.append( " substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) cod_cate, ")
		.append( " p.t02f_ingsun, uo.t12des_uorga, t12des_corta, param.t99descrip ")				
		.append( " from    t02perdp p, ")
		.append( " t12uorga uo, ")
		.append( " t99codigos param ");	
	
	//ICAPUNAY - PAS20171U230300074 - Mejoras vacaciones
	private final StringBuffer FIND_PERSONAL_BY_CRITERIO_VALOR_ESTADO =  
		new StringBuffer("select")
	    .append( " p.t02cod_rel, (CASE WHEN p.t02cod_rel=? THEN 'DL. 1057' ELSE CASE WHEN p.t02cod_rel=? THEN 'Modalidad formativa' ELSE 'DL. 276-728' END END) as regimen,  ")
		.append( " p.t02cod_pers as registro, p.t02f_ingsun as fechaingreso, p.t02f_cese as fechacese, p.t02ap_pate, p.t02ap_mate, p.t02nombres, ")
		.append( " (trim(p.t02ap_pate)||' '||trim(p.t02ap_mate)||' '||trim(p.t02nombres)) as apenom, ")
		.append( " substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) as coduo, ")				
		.append( " substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) cod_cate, ")
		.append( " uo.t12des_uorga, uo.t12des_corta, param.t99descrip ")				
		.append( " from t02perdp p, ")
		.append( " t12uorga uo, ")
		.append( " t99codigos param ");	
	//FIN ICAPUNAY
	
	//JVILLACORTA - 19/04/2011 - ALERTA DE SOLICITUDES
	private final StringBuffer FIND_NOTIFICA_DIRECTIVO_BY_CRITERIO_VALOR =  
		new StringBuffer(" select distinct n.cod_pers_notif, p.t02ap_pate, p.t02ap_mate, p.t02nombres, param.t99descrip, uo.t12cod_uorga, uo.t12des_corta ")
	    .append(" from t4562NotificaDir n, t02perdp p, t99codigos param, t12uorga uo ")
	    .append(" where n.cod_pers_notif = p.t02cod_pers ")
	    .append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = uo.t12cod_uorga ")
	    .append(" and substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) = param.t99codigo ");
	
	private final StringBuffer FIND_NOTIFICA_TRABAJADOR_BY_CRITERIO_VALOR =  
		new StringBuffer(" select distinct n.cod_pers, p.t02ap_pate, p.t02ap_mate, p.t02nombres, param.t99descrip, uo.t12cod_uorga, uo.t12des_corta ")
	    .append(" from t4563NotificaTra n, t02perdp p, t99codigos param, t12uorga uo ")
	    .append(" where n.cod_pers = p.t02cod_pers ")
	    .append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = uo.t12cod_uorga ")
	    .append(" and substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) = param.t99codigo ");
	//FIN - JVILLACORTA - 19/04/2011 - ALERTA DE SOLICITUDES
	
	
	private final StringBuffer FIND_VAVACIONES_GOZADAS =  
		new StringBuffer("select  v.anno_vac,v.ffinicio,")
		.append(" v.ffin, v.dias, v.observ, m.descrip, v.licencia, v.u_organ ")
		.append( "from	t1282vacaciones_d v, t1279tipo_mov m ")
		.append( "where m.mov = v.licencia and	v.cod_pers = ? and  v.est_id != '0' and")
		.append( " (v.licencia in ( ?, ?, ?)  ");
	
	
	private final StringBuffer FIND_COD_PERS_SALDO_ANNO =  
		new StringBuffer(" ")
		.append("select cod_pers, anno, dias, saldo")
		.append( " from t1281vacaciones_c where cod_pers = ? ");
	
	/*private final StringBuffer FIND_NOTIFICACIONES_DIREC =  
		new StringBuffer(" select cod_pers_notif, 'Sem1' as semana, count(cod_pers_notif) as cantidad from t4562NotificaDir ")
		.append(" where cod_pers_notif = ? ")
		.append(" and fec_notificacion >= ? and fec_notificacion < ? group by cod_pers_notif ")
		.append(" union all ")
		.append(" select cod_pers_notif, 'Sem2' as semana, count(cod_pers_notif) as cantidad from t4562NotificaDir ")
		.append(" where cod_pers_notif = ? ")
		.append(" and fec_notificacion >= ? and fec_notificacion < ? group by cod_pers_notif ")
		.append(" union all ")
		.append(" select cod_pers_notif, 'Sem3' as semana, count(cod_pers_notif) as cantidad from t4562NotificaDir ")
		.append(" where cod_pers_notif = ? ")
		.append(" and fec_notificacion >= ? and fec_notificacion < ? group by cod_pers_notif ")
		.append(" union all ")
		.append(" select cod_pers_notif, 'Sem4' as semana, count(cod_pers_notif) as cantidad from t4562NotificaDir  ")
		.append(" where cod_pers_notif = ? ")
		.append(" and fec_notificacion >= ? and fec_notificacion < ? group by cod_pers_notif ");
	*/
	/*private final StringBuffer FIND_NOTIFICACIONES_TRAB =  
		new StringBuffer(" select cod_pers, 'Sem1' as semana, count(cod_pers) as cantidad from t4563NotificaTra ")
		.append(" where cod_pers = ? ")
		.append(" and fec_notificacion >= ? and fec_notificacion < ? group by cod_pers ")
		.append(" union all ")
		.append(" select cod_pers, 'Sem2' as semana, count(cod_pers) as cantidad from t4563NotificaTra ")
		.append(" where cod_pers = ? ")
		.append(" and fec_notificacion >= ? and fec_notificacion < ? group by cod_pers ")
		.append(" union all ")
		.append(" select cod_pers, 'Sem3' as semana, count(cod_pers) as cantidad from t4563NotificaTra ")
		.append(" where cod_pers = ? ")
		.append(" and fec_notificacion >= ? and fec_notificacion < ? group by cod_pers ")
		.append(" union all ")
		.append(" select cod_pers, 'Sem4' as semana, count(cod_pers) as cantidad from t4563NotificaTra  ")
		.append(" where cod_pers = ? ")
		.append(" and fec_notificacion >= ? and fec_notificacion < ? group by cod_pers ");
	*/
	
	//JVILLACORTA - 19/04/2011 - ALERTA DE SOLICITUDES
	private final StringBuffer FIND_DETALLE_NOTIFICA_DIREC =  
		//new StringBuffer(" select n.num_notificacion, n.fec_notificacion, n.cod_pers, p.t02nombres, p.t02ap_pate, t.descrip ") //ICAPUNAY 11/08/2011 visualizar anno, numero y fecha solicitud y ape_mate solicitante
		new StringBuffer(" select n.num_notificacion,s.anno,s.numero,s.fecha,n.cod_pers,p.t02nombres,p.t02ap_pate,p.t02ap_mate,t.descrip ") //ICAPUNAY 11/08/2011 visualizar anno, numero y fecha solicitud y ape_mate solicitante
		//.append(" from t4562NotificaDir n, t1279Tipo_mov t, t02perdp p ") //ICAPUNAY 11/08/2011 visualizar anno, numero y fecha solicitud y ape_mate solicitante
		.append(" from t4562NotificaDir n, t1279Tipo_mov t, t02perdp p, t1277solicitud s ") //ICAPUNAY 11/08/2011 visualizar anno, numero y fecha solicitud y ape_mate solicitante
		.append(" where n.cod_pers_notif = ? ")
		.append(" and n.cod_anno=s.anno and n.num_solicitud=s.numero ") //ICAPUNAY 11/08/2011 visualizar anno, numero y fecha solicitud y ape_mate solicitante
		.append(" and n.tipo_solicitud = t.mov ")
		.append(" and n.cod_pers = p.t02cod_pers ")
		.append(" and n.fec_notificacion >= ? ")		
		.append(" and n.fec_notificacion <= ? ");
	//FIN - JVILLACORTA - 19/04/2011 - ALERTA DE SOLICITUDES
	
	//JVILLACORTA - 29/11/2011 - ALERTA DE SOLICITUDES
	private final StringBuffer FIND_DETALLE_NOTIFICA_TRAB =		
		new StringBuffer(" select n.num_notificacion, n.fec_ingreso, n.fec_notificacion, t.descrip ")		
		.append(" from t4563NotificaTra n, t1279Tipo_mov t, t02perdp p ") 
		.append(" where n.cod_pers = ? ")
		.append(" and n.cod_mov = t.mov ") 
		.append(" and n.cod_pers = p.t02cod_pers ")
		.append(" and n.fec_notificacion >= ? ")		
		.append(" and n.fec_notificacion <= ? ")
	    .append(" order by n.fec_ingreso ");
	//FIN - JVILLACORTA - 29/11/2011 - ALERTA DE SOLICITUDES


	 //ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
	  private static final StringBuffer findUOsByJefe = new StringBuffer("SELECT * ")
	  .append("FROM t12uorga where substr(trim(nvl(t12cod_encar,'')||nvl(t12cod_jefat,'')),1,4)=? ")
	  .append("and t12ind_estad=? "); 
	  //ICAPUNAY - PAS20165E230300132
	  
	  //ICAPUNAY - PAS20171U230300074 - Mejoras vacaciones
	  private final StringBuffer FIND_NOTIFICA_DIRECTIVOS_VACACIONES =  
			new StringBuffer("SELECT nvd.cod_uorg_notif as coduo,u.t12des_corta as desuo,nvd.des_notif, ")
		    .append("nvd.cod_pers_jefe as regdirec,(trim(p.t02ap_pate)||' '||trim(p.t02ap_mate)||' '||trim(p.t02nombres)) as apenomdirec, ")
		    .append("DATE(nvd.fec_envio_notif) as fecnotif,nvd.num_trabaj ")
		    .append("FROM t9036notifvacdir nvd,t12uorga u, t02perdp p ")
		    //.append("where substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6)=u.t12cod_uorga ")
		    .append("where nvd.cod_uorg_notif=u.t12cod_uorga ")
	  		.append("and nvd.cod_pers_jefe=p.t02cod_pers ")
	  		.append("and nvd.ind_tip_notif=? ") //1= colaboradores con programacion, 2= colaboradores sin programacion
	  		.append("and DATE(nvd.fec_envio_notif) between ? and ? ");
	  //FIN ICAPUNAY - PAS20171U230300074 - Mejoras vacaciones
	  
					
	/**
	 *@param datasource Object
	 *
	 * */
	public ReporteDAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.dataSource = (DataSource)datasource;
		else if (datasource instanceof String)
			this.dataSource = getDataSource((String)datasource);
		else
			throw new DAOException(this, "Datasource no valido");
	}
	
	
	
	/**
	 * Metodo encargado de buscar empleados de acuerdo a criterio
	 * de busqueda y de visibilidad por rol de usuario
	 * @param Map datos
	 * @return List 
	 * @throws DAOException
	 */
	
	public List joinWithT02T12T99findPersonalByCriterioValorInact(Map datos)
	throws DAOException {
		
	String criterio =	(datos.get("criterio") != null) ? (String)datos.get("criterio"): "";
	String valor =		(datos.get("valor") != null) ? (String)datos.get("valor"): "";
	Map seguridad =		(datos.get("seguridad") != null) ? (HashMap)datos.get("seguridad"): new HashMap();
	StringBuffer strSQLFinal = new StringBuffer("");
	StringBuffer strSQL = new StringBuffer("");
	List lista = null;
	
	//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
	List objsJefe = new ArrayList();
	List resUOsJefe= new ArrayList();
	HashMap uoMap = new HashMap();
    String codUoJef = "";
	String codPersUsuario = seguridad!=null && !seguridad.isEmpty()? ((String) seguridad.get("codPers")).trim():"";
	//ICAPUNAY - PAS20165E230300132 
	
	if (constantes.leePropiedad("CRITERIO_COD").equals(criterio)) {
	  strSQL.append( "where   p.t02cod_stat in (?,?) ")
	  .append( " and p.t02cod_pers = '" )
		.append( valor.trim().toUpperCase()).append( "'");
	} else {
		strSQL.append( "where   p.t02cod_stat = ? ");
	}
					
	if (constantes.leePropiedad("CRITERIO_UUOO").equals(criterio)) {
	  if (log.isDebugEnabled())log.debug("entro criterio uno-->");
		strSQL.append( " and '").append(valor.trim().toUpperCase() )
		//.append("' = p.t02cod_uorg ");ICAPUNAY-PAS20144EB20000075-ajustes visibilidad reporte vacaciones y reporte vacaciones efectuadas
		.append("' = substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) ");//ICAPUNAY-PAS20144EB20000075-ajustes visibilidad reporte vacaciones y reporte vacaciones efectuadas
	}
	if (constantes.leePropiedad("CRITERIO_DEP").equals(criterio)) {
	  if(log.isDebugEnabled())log.debug("entro criterio cuatro-->");
		String intendencia = "--'";
		if (valor.trim().length()==6 && "0000".equals(valor.trim().substring(2,6))){
		  intendencia=valor.trim().substring(0,2).concat("%'");
		}
		//strSQL.append( " and p.t02cod_uorg like '").append(intendencia);ICAPUNAY-PAS20144EB20000075-ajustes visibilidad reporte vacaciones y reporte vacaciones efectuadas
		strSQL.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(intendencia);//ICAPUNAY-PAS20144EB20000075-ajustes visibilidad reporte vacaciones y reporte vacaciones efectuadas
	}
					
	strSQL .append( " and param.t99cod_tab = ? ")
	.append( " and param.t99tip_desc = ? ")
	//.append( " and p.t02cod_uorg = uo.t12cod_uorga ") ICAPUNAY-PAS20144EB20000075-ajustes visibilidad reporte vacaciones y reporte vacaciones efectuadas
	.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = uo.t12cod_uorga ") //ICAPUNAY-PAS20144EB20000075-ajustes visibilidad reporte vacaciones y reporte vacaciones efectuadas
	.append( " and p.t02cod_cate = param.t99codigo ");
					
	//criterios de visibilidad
	if (seguridad != null && !seguridad.isEmpty()) {
		//String roles = (String) seguridad.get("roles"); ICAPUNAY-PAS20144EB20000075-ajustes visibilidad reporte vacaciones y reporte vacaciones efectuadas
		HashMap roles = (HashMap) seguridad.get("roles"); //ICAPUNAY-PAS20144EB20000075-ajustes visibilidad reporte vacaciones y reporte vacaciones efectuadas
		String perfil_usuario = seguridad.get("perfil_usuario")!=null?seguridad.get("perfil_usuario").toString():"";//ICAPUNAY-PAS20144EB20000075-ajustes visibilidad reporte vacaciones y reporte vacaciones efectuadas
		String uoSeg = (String) seguridad.get("uoSeg");
		String uuoo = (String) seguridad.get("uuoo");
		String uoAO = (String) seguridad.get("uoAO");//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
        if (log.isDebugEnabled()) log.debug("roles: "+roles); //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
        if (log.isDebugEnabled()) log.debug("uoAO: "+uoAO); //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
        //ICAPUNAY-PAS20144EB20000075-ajustes visibilidad reporte vacaciones y reporte vacaciones efectuadas
        if  ("usuario_AnalisCentral".equals(perfil_usuario) || roles.get(Constantes.ROL_ANALISTA_CENTRAL)!=null){
        	strSQL.append(" and 1=1 ");
        }
        //ICAPUNAY-PAS20144EB20000075-ajustes visibilidad reporte vacaciones y reporte vacaciones efectuadas
        else if ("usuario_Jefe".equals(perfil_usuario) || roles.get(Constantes.ROL_JEFE)!=null) {
        	//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
        	//si es jefe, debe visualizar informacion de colaboradores de todas las unidades donde es encargado o jefe (incluyendo subunidades)
			objsJefe.add(codPersUsuario.toUpperCase());
			objsJefe.add("1");    		  
			resUOsJefe=executeQuery(dataSource, findUOsByJefe.toString(), objsJefe.toArray());
			if(log.isDebugEnabled()) log.debug("resUOsJefe:"+ resUOsJefe);
			if (resUOsJefe.size()>0){    			   		      
				for (int i = 0;i<resUOsJefe.size();i++){ //0,1,2 (3 unidades)
					uoMap=(HashMap)resUOsJefe.get(i);
					codUoJef= uoMap.get("t12cod_uorga").toString().trim();
					if (i==0){//0 (primer registro)
						strSQL = strSQL.append(" and (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(findUuooJefe(codUoJef)).append("%' "); 
					}else{
						strSQL = strSQL.append(" or substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(findUuooJefe(codUoJef)).append("%' "); 
					}    		    	  
					if (i==resUOsJefe.size()-1){//2 (ultimo registro)
						strSQL = strSQL.append(") ");
					}
				}
			}
			//strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append( uuoo.trim().toUpperCase()).append("' ");//ICAPUNAY-MEMO 32-4F3100-2013
			//ICAPUNAY - PAS20165E230300132
		} else if ("usuario_AnalisOperativo".equals(perfil_usuario) || roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) {			
			strSQL.append(" and ((substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append( uoSeg.trim().substring(0,2).toUpperCase()).append("%') ");//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			strSQL.append(" or (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) in (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '").append(uoAO).append("'))) ");//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
		}
	}
	strSQL.append(" order by t02cod_pers ");
	Object o[];
	if (constantes.leePropiedad("CRITERIO_COD").equals(criterio)) {
  	o = new Object[]{constantes.leePropiedad("PROGRAMACION_ACEPTADA"), 
				constantes.leePropiedad("PROGRAMACION_ELIMINADA"),
				constantes.leePropiedad("CODTAB_CATEGORIA"), 
				constantes.leePropiedad("T99DETALLE")};
	} else {
		o = new Object[]{constantes.leePropiedad("PROGRAMACION_ACEPTADA"),
		    constantes.leePropiedad("CODTAB_CATEGORIA"),
				constantes.leePropiedad("T99DETALLE")};
	}
	strSQLFinal.append(FIND_PERSONAL_BY_CRITERIO_VALOR_INACT.toString()).append( strSQL.toString());
	if(log.isDebugEnabled())log.debug("strSQLFinalll:" + strSQLFinal);
	lista = executeQuery(dataSource, strSQLFinal.toString(), o);
	return lista;
	}
	
	/**
	 * Metodo encargado de listar la vacaciones Gozadas
	 * @param Map datos, boolean incluyePend,boolean incluyeProg
	 * @return List
	 * @throws DAOException
	 */
	
	public List findVacacionesGozadas(Map datos) throws DAOException {
		boolean incluyePend=false;
		boolean incluyeProg=false;
		if ("true".equals(datos.get("incluyePend").toString())){
			incluyePend=true;
		}
    if ("true".equals(datos.get("incluyeProg").toString())){
    	incluyeProg=true;
		}
		String fechaIni = (datos.get("fechaIni") != null) ? (String)datos.get("fechaIni"): "";
		String fechaFin = (datos.get("fechaFin") != null) ? (String)datos.get("fechaFin"): "";
		String codPers  =  (datos.get("t02cod_pers") != null) ? (String)datos.get("t02cod_pers"): "";
		int numDias =  		(datos.get("numDias") != null) ? Integer.parseInt((String)datos.get("numDias")): 0;
		StringBuffer strSQLFinal = new StringBuffer("");
		StringBuffer strSQL = new StringBuffer("");
		List lista = null;
						
		if (incluyeProg) {
			strSQL.append( " or ( (v.licencia = '")
		  .append( constantes.leePropiedad("VACACION_PROGRAMADA"))
			.append( "' or v.licencia = '")
			.append( constantes.leePropiedad("REPROGRAMACION_VACACION")).append( "') ")
			.append( "      and v.est_id = '" )
			.append( constantes.leePropiedad("PROGRAMACION_ACEPTADA"))
			.append( "') ) ");
		} else {
			strSQL.append(" ) ");
		}
						
		if (!"".equals(fechaIni)) {
		  FechaBean fb_fechaIni = new FechaBean(fechaIni);
			String fecCompara1 = fb_fechaIni.getFormatDate("yyyy/MM/dd");
			strSQL.append(" and (v.ffinicio >= DATE('")
			.append( fecCompara1 ).append( "')");
		}
		if (!"".equals(fechaFin)) {
		  FechaBean fb_fechaFin = new FechaBean(fechaFin);
			String fecCompara2 = fb_fechaFin.getFormatDate("yyyy/MM/dd");
			strSQL.append(" and v.ffin <= DATE('" ).append( fecCompara2)
			.append( "'))");
		}
						
		if (numDias >= 0) {
		  strSQL.append(" and v.dias >= ").append(""+numDias);
		}
						
		if (!incluyePend) {
			strSQL.append(" and v.ffin <= DATE('")
			.append( new FechaBean().getFormatDate("yyyy/MM/dd") ).append( "')");
			strSQL.append(" order by v.anno_vac, v.ffinicio, v.licencia");
		} else {
			strSQL.append(" order by v.ffinicio, v.licencia");
		}
		strSQLFinal.append(FIND_VAVACIONES_GOZADAS.toString()).append( strSQL.toString());
		lista = executeQuery(dataSource, strSQLFinal.toString(), 
						new Object[]{codPers, constantes.leePropiedad("VACACION"),
						constantes.leePropiedad("VACACION_ESPECIAL"),
						constantes.leePropiedad("VACACION_VENTA")});
			
		return lista;
	}
	
	
	/**
	 * Metodo encargado de buscar saldos
	 * @param Map datos, boolean saldoFavor
	 * @return List
	 * @throws DAOException
	 */
	
	public List findAnhosPendByCodPersSaldoAnno(Map datos)
	throws DAOException {
		boolean saldoFavor=false;
		if ("true".equals(datos.get("saldoFavor").toString())){
			saldoFavor=true;
		}
		String codPers = (datos.get("t02cod_pers") != null) ? (String)datos.get("t02cod_pers"): "";
		String anhoIni = (datos.get("anhoIni") != null) ? (String)datos.get("anhoIni"): "";
		String anhoFin = (datos.get("anhoFin") != null) ? (String)datos.get("anhoFin"): "";
		StringBuffer strSQLFinal = new StringBuffer("");
		StringBuffer strSQL = new StringBuffer("");
		List lista = null;
		if (saldoFavor) {
			strSQL .append( " and saldo > 0");
		}
		if (!anhoIni.trim().equals("")) {
			strSQL .append( " and (anno >= " ).append( anhoIni.trim());
			if (!anhoFin.trim().equals("")) {
				strSQL .append( " and anno <= " ).append( anhoFin.trim() ).append( ")");
			} else {
				strSQL .append( ")");
			}
		} else {
			if (!anhoFin.trim().equals("")) {
				strSQL .append( " and anno <= " ).append( anhoFin.trim());
			}
		}
		strSQL .append( " order by anno");
		strSQLFinal.append(FIND_COD_PERS_SALDO_ANNO.toString()).append( strSQL.toString());
		lista = executeQuery(dataSource, strSQLFinal.toString(), 
      			new Object[]{codPers});
		return lista;
					
	}
	
	/**
	 * Metodo encargado de buscar empleados activos de acuerdo a criterio
	 * de busqueda, regimen y de visibilidad por rol de usuario
	 * @param Map datos
	 * @return List 
	 * @throws DAOException
	 */
	//JVV - ini
	public List joinWithT02T12T99findPersonalByCriterioValorAct(Map datos)
	throws DAOException {
				
		String regimen =	(datos.get("regimen") != null) ? (String)datos.get("regimen"): "";
		String criterio =	(datos.get("criterio") != null) ? (String)datos.get("criterio"): "";
		String valor =	(datos.get("valor") != null) ? (String)datos.get("valor"): "";		
		Map seguridad =		(datos.get("seguridad") != null) ? (HashMap)datos.get("seguridad"): new HashMap();		
		
		if(log.isDebugEnabled())log.debug("regimen:" + regimen);
		if(log.isDebugEnabled())log.debug("criterio:" + criterio);
		if(log.isDebugEnabled())log.debug("valor:" + valor);
		if(log.isDebugEnabled())log.debug("seguridad:" + seguridad);
		
		StringBuffer strSQLFinal = new StringBuffer("");
		StringBuffer strSQL = new StringBuffer("");
		List lista = null;	
		
		//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
		List objsJefe = new ArrayList();
		List resUOsJefe= new ArrayList();
		HashMap uoMap = new HashMap();
	    String codUoJef = "";
		String codPersUsuario = seguridad!=null && !seguridad.isEmpty()? ((String) seguridad.get("codPers")).trim():"";
		//ICAPUNAY - PAS20165E230300132 
		
		//strSQL .append( "where   p.t02cod_stat = ? ");
		
		if (constantes.leePropiedad("CRITERIO_COD").equals(criterio)) {			
			if (log.isDebugEnabled())log.debug("entro criterio COD-->");
			if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
				strSQL .append( "where   p.t02cod_stat in (?,?) ")
				.append( "and p.t02cod_pers = '").append( valor.trim().toUpperCase()).append( "'")
				.append( "and   p.t02cod_rel not in (?,?) ") //not in (09,10)
				.append( " and param.t99cod_tab = ? ")
				.append( " and param.t99tip_desc = ? ");
			} else if (regimen.equals("09")){
				//strSQL .append( "where   p.t02cod_stat in (?) ") //ICAPUNAY 01/08/2011 a solicitud de Carmen Lama (ver INACTIVOS tambiÃ©n)	
				strSQL .append( "where   p.t02cod_stat in (?,?) ") //ICAPUNAY 01/08/2011 a solicitud de Carmen Lama (ver INACTIVOS tambiÃ©n)	
				.append( "and p.t02cod_pers = '").append( valor.trim().toUpperCase()).append( "'")				
				.append( "and   p.t02cod_rel = ? ") //= '09'
				.append( " and param.t99cod_tab = ? ")
				.append( " and param.t99tip_desc = ? ");
			  }
			else if (regimen.equals("10")){
				//strSQL .append( "where   p.t02cod_stat in (?) ") //ICAPUNAY 01/08/2011 a solicitud de Carmen Lama (ver INACTIVOS tambiÃ©n)
				strSQL .append( "where   p.t02cod_stat in (?,?) ") //ICAPUNAY 01/08/2011 a solicitud de Carmen Lama (ver INACTIVOS tambiÃ©n)	
				.append( "and p.t02cod_pers = '").append( valor.trim().toUpperCase()).append( "'")				
				.append( "and   p.t02cod_rel = ? ") //= '10'
				.append( " and param.t99cod_tab = ? ")
				.append( " and param.t99tip_desc = ? ");
			}
		} 	
				
		if (constantes.leePropiedad("CRITERIO_UUOO").equals(criterio)) {				  
			if (log.isDebugEnabled())log.debug("entro criterio UUOO-->");
			if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
				strSQL .append( "where   p.t02cod_stat = ? ")			
				.append( "and   p.t02cod_rel not in (?,?) ") //not in (09)
				.append( " and param.t99cod_tab = ? ")
				.append( " and param.t99tip_desc = ? ");
			} else if (regimen.equals("09")){
				strSQL .append( "where   p.t02cod_stat = ? ")			
				.append( "and   p.t02cod_rel = ? ") //= '09'
				.append( " and param.t99cod_tab = ? ")
				.append( " and param.t99tip_desc = ? ");
			} else if (regimen.equals("10")){
				strSQL .append( "where   p.t02cod_stat in (?) ")				
				.append( "and   p.t02cod_rel = ? ") //= '10'
				.append( " and param.t99cod_tab = ? ")
				.append( " and param.t99tip_desc = ? ");
			}				
			strSQL.append( "and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = '")			  
			.append( valor.trim().toUpperCase()).append( "'");
		}
		
		if (constantes.leePropiedad("CRITERIO_INTD").equals(criterio)) {			
			String intendencia = valor.substring(0,2);  
			if (log.isDebugEnabled())log.debug("codigo intendencia:" + intendencia);			
			if (log.isDebugEnabled())log.debug("entro criterio INTD-->");
			if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
				strSQL .append( " where   p.t02cod_stat = ? ")				
				.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) LIKE '")
				.append(intendencia.trim().toUpperCase()).append("%'")
				.append( " and   p.t02cod_rel not in (?,?) ") //not in (09,10)
				.append( " and param.t99cod_tab = ? ")
				.append( " and param.t99tip_desc = ? ");
			} else if (constantes.leePropiedad("CODREL_REG1057").equals(regimen)){
				strSQL .append( "where   p.t02cod_stat = ? ")				
				.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) LIKE '")
				.append(intendencia.trim().toUpperCase()).append("%'")				
				.append( " and p.t02cod_rel = ? ") //= '09'
				.append( " and param.t99cod_tab = ? ")
				.append( " and param.t99tip_desc = ? ");
			} else if (constantes.leePropiedad("CODREL_MOD_FORMATIVA").equals(regimen)){
				strSQL .append( "where   p.t02cod_stat = ? ")				
				.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) LIKE '")
				.append(intendencia.trim().toUpperCase()).append("%'")				
				.append( " and p.t02cod_rel = ? ") //= '10'
				.append( " and param.t99cod_tab = ? ")
				.append( " and param.t99tip_desc = ? ");
			}
		}
		
		if (constantes.leePropiedad("CRITERIO_DEP").equals(criterio)) {
			  if(log.isDebugEnabled())log.debug("entro criterio DEP-->");
			  /*String intendencia = "--'";
			  if (valor.trim().length()==6 && "0000".equals(valor.trim().substring(2,6))){
				  intendencia=valor.trim().substring(0,2).concat("%'");
			  }*/
			  //strSQL.append( " and p.t02cod_uorg like '").append(intendencia);
			  if(log.isDebugEnabled())log.debug("entro criterio DEP-->");
			  if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
				    strSQL .append( "where   p.t02cod_stat = ? ")				  
				    //.append( " and p.t02cod_uorg like '")
				    //.append(intendencia)
				  	.append( "and   p.t02cod_rel not in (?,?) ") //not in (09,10)
					.append( " and param.t99cod_tab = ? ")
					.append( " and param.t99tip_desc = ? ");
				} else if (constantes.leePropiedad("CODREL_REG1057").equals(regimen)){
					strSQL .append( "where   p.t02cod_stat = ? ")				
				    //.append( " and p.t02cod_uorg like '")
				    //.append(intendencia)
					.append( "and   p.t02cod_rel = ? ") //= '09'
					.append( " and param.t99cod_tab = ? ")
					.append( " and param.t99tip_desc = ? ");
				} else if (constantes.leePropiedad("CODREL_MOD_FORMATIVA").equals(regimen)){
					strSQL .append( "where   p.t02cod_stat = ? ")					
					.append( "and   p.t02cod_rel = ? ") //= '10'
					.append( " and param.t99cod_tab = ? ")
					.append( " and param.t99tip_desc = ? ");
				}
		}
		
		//strSQL.append( " and param.t99cod_tab = ? ")
		//.append( " and param.t99tip_desc = ? ")
		strSQL.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = uo.t12cod_uorga ")
		.append( " and substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) = param.t99codigo ");
		
		//criterios de visibilidad
		if (seguridad != null && !seguridad.isEmpty()) {
			Map roles = (HashMap) seguridad.get("roles");
	  	    //if(log.isDebugEnabled())log.debug("roles:" + roles);
			String uoSeg = (String) seguridad.get("uoSeg");
			//if(log.isDebugEnabled())log.debug("uoSeg:" + uoSeg);
			String uuoo = (String) seguridad.get("uuoo");
			String uoAO = (String) seguridad.get("uoAO");//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
	        if (log.isDebugEnabled()) log.debug("roles: "+roles); //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
	        if (log.isDebugEnabled()) log.debug("uoAO: "+uoAO); //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
			if (roles.get(constantes.leePropiedad("ROL_ANALISTA_CENTRAL")) != null) {
				strSQL.append(" and 1=1 ");
			} 
			//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			else if (roles.get(constantes.leePropiedad("ROL_ANALISTA_OPERATIVO")) != null) {
				strSQL.append(" and ((substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "') ");
				strSQL.append(" or (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) in (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '").append(uoAO).append( "'))) ");
			}
			//else if (roles.get(constantes.leePropiedad("ROL_ANALISTA_OPERATIVO")) != null || //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
			else if (
			//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
					roles.get(constantes.leePropiedad("ROL_SECRETARIA")) != null
					|| roles.get(constantes.leePropiedad("ROL_JEFE")) != null) {
				//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
				if (roles.get(constantes.leePropiedad("ROL_JEFE")) != null) {
					
					//si es jefe, debe visualizar informacion de colaboradores de todas las unidades donde es encargado o jefe (incluyendo subunidades)
					objsJefe.add(codPersUsuario.toUpperCase());
					objsJefe.add("1");    		  
					resUOsJefe=executeQuery(dataSource, findUOsByJefe.toString(), objsJefe.toArray());
					if(log.isDebugEnabled()) log.debug("resUOsJefe:"+ resUOsJefe);
					if (resUOsJefe.size()>0){    			   		      
						for (int i = 0;i<resUOsJefe.size();i++){ //0,1,2 (3 unidades)
							uoMap=(HashMap)resUOsJefe.get(i);
							codUoJef= uoMap.get("t12cod_uorga").toString().trim();
							if (i==0){//0 (primer registro)
								strSQL = strSQL.append(" and (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(findUuooJefe(codUoJef)).append("%' "); 
							}else{
								strSQL = strSQL.append(" or substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(findUuooJefe(codUoJef)).append("%' "); 
							}    		    	  
							if (i==resUOsJefe.size()-1){//2 (ultimo registro)
								strSQL = strSQL.append(") ");
							}
						}
					}
				}
				else { //secretaria
					strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "' ");
				}				
				//strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "' ");
				//FIN ICAPUNAY - PAS20165E230300132
			} else {
				strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "' ") ;
			}
			//if(log.isDebugEnabled())log.debug("uuoo:" + uuoo);
			/*if ((roles!=null) && (roles.get(constantes.leePropiedad("ROL_JEFE")))!=null) {
		  	strSQL.append(" and p.t02cod_uorg = '").append( uuoo.trim().toUpperCase()).append("' ");
			} else if ((roles!=null) && (roles.get(constantes.leePropiedad("ROL_ANALISTA_OPERATIVO")))!=null) {
				strSQL.append(" and p.t02cod_uorg like '")
				.append( uoSeg.trim().substring(0,2).toUpperCase()).append("%' ");
			}*/
		}
		strSQL.append(" order by t02cod_pers ");
		
		Object o[] = null;		

		if (constantes.leePropiedad("CRITERIO_COD").equals(criterio)) {
			if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
				o = new Object[]{constantes.leePropiedad("ACTIVO"), 
						constantes.leePropiedad("INACTIVO"),
						constantes.leePropiedad("CODREL_REG1057"),
						constantes.leePropiedad("CODREL_MOD_FORMATIVA"), //JVILLACORTA 20/05/2011 - AOM:MODALIDADES FORMATIVAS
						constantes.leePropiedad("CODTAB_CATEGORIA"),
						constantes.leePropiedad("T99DETALLE")};
			} else if (regimen.equals("09")){
				o = new Object[]{constantes.leePropiedad("ACTIVO"),
						constantes.leePropiedad("INACTIVO"),//ICAPUNAY 01/08/2011 a solicitud de Carmen Lama (ver INACTIVOS tambiÃ©n)
						constantes.leePropiedad("CODREL_REG1057"),
						constantes.leePropiedad("CODTAB_CATEGORIA"),
						constantes.leePropiedad("T99DETALLE")};
			} else if (regimen.equals("10")){
				o = new Object[]{constantes.leePropiedad("ACTIVO"),
						constantes.leePropiedad("INACTIVO"),//ICAPUNAY 01/08/2011 a solicitud de Carmen Lama (ver INACTIVOS tambiÃ©n)
						constantes.leePropiedad("CODREL_MOD_FORMATIVA"),
						constantes.leePropiedad("CODTAB_CATEGORIA"),
						constantes.leePropiedad("T99DETALLE")};
			}
			
		}
		
		if (constantes.leePropiedad("CRITERIO_UUOO").equals(criterio)) {
			if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
				o = new Object[]{constantes.leePropiedad("ACTIVO"),						
						constantes.leePropiedad("CODREL_REG1057"),
						constantes.leePropiedad("CODREL_MOD_FORMATIVA"), //JVILLACORTA 20/05/2011 - AOM:MODALIDADES FORMATIVAS
						constantes.leePropiedad("CODTAB_CATEGORIA"),
						constantes.leePropiedad("T99DETALLE")};
			} else if (regimen.equals("09")){
				o = new Object[]{constantes.leePropiedad("ACTIVO"),						
						constantes.leePropiedad("CODREL_REG1057"),
						constantes.leePropiedad("CODTAB_CATEGORIA"),
						constantes.leePropiedad("T99DETALLE")};
			} else if (regimen.equals("10")){
				o = new Object[]{constantes.leePropiedad("ACTIVO"),						
						constantes.leePropiedad("CODREL_MOD_FORMATIVA"),
						constantes.leePropiedad("CODTAB_CATEGORIA"),
						constantes.leePropiedad("T99DETALLE")};
			}
			
		}
		
		if (constantes.leePropiedad("CRITERIO_INTD").equals(criterio)) {
			if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
				o = new Object[]{constantes.leePropiedad("ACTIVO"),						
						constantes.leePropiedad("CODREL_REG1057"),
						constantes.leePropiedad("CODREL_MOD_FORMATIVA"), //JVILLACORTA 20/05/2011 - AOM:MODALIDADES FORMATIVAS
						constantes.leePropiedad("CODTAB_CATEGORIA"),
						constantes.leePropiedad("T99DETALLE")};
			} else if (regimen.equals("09")){
				o = new Object[]{constantes.leePropiedad("ACTIVO"),						
						constantes.leePropiedad("CODREL_REG1057"),
						constantes.leePropiedad("CODTAB_CATEGORIA"),
						constantes.leePropiedad("T99DETALLE")};
			} else if (regimen.equals("10")){
				o = new Object[]{constantes.leePropiedad("ACTIVO"),						
						constantes.leePropiedad("CODREL_MOD_FORMATIVA"),
						constantes.leePropiedad("CODTAB_CATEGORIA"),
						constantes.leePropiedad("T99DETALLE")};
			}
			
		}
		
		if (constantes.leePropiedad("CRITERIO_DEP").equals(criterio)) {
			if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
				o = new Object[]{constantes.leePropiedad("ACTIVO"),						
						constantes.leePropiedad("CODREL_REG1057"),
						constantes.leePropiedad("CODREL_MOD_FORMATIVA"), //JVILLACORTA 20/05/2011 - AOM:MODALIDADES FORMATIVAS
						constantes.leePropiedad("CODTAB_CATEGORIA"),
						constantes.leePropiedad("T99DETALLE")};
			} else if (regimen.equals("09")){
				o = new Object[]{constantes.leePropiedad("ACTIVO"),						
						constantes.leePropiedad("CODREL_REG1057"),
						constantes.leePropiedad("CODTAB_CATEGORIA"),
						constantes.leePropiedad("T99DETALLE")};
			} else if (regimen.equals("10")){
				o = new Object[]{constantes.leePropiedad("ACTIVO"),						
						constantes.leePropiedad("CODREL_MOD_FORMATIVA"),
						constantes.leePropiedad("CODTAB_CATEGORIA"),
						constantes.leePropiedad("T99DETALLE")};
			}
			
		}					
		strSQLFinal.append(FIND_PERSONAL_BY_CRITERIO_VALOR_ACT.toString()).append( strSQL.toString());
		if(log.isDebugEnabled())log.debug("strSQLFinal:" + strSQLFinal);
		lista = executeQuery(dataSource, strSQLFinal.toString(), o);
		
		return lista;			
	}		

	/**JVILLACORTA - 19/04/2011 - ALERTA DE SOLICITUDES
	 * Metodo encargado de buscar notificaciones de solicitud a directivos 
	 * de acuerdo a criterio de busqueda
	 * @param Map datos
	 * @return List 
	 * @throws DAOException
	 */	
	public List findNotificaDirectivosByCriterioValor(Map datos)
	throws DAOException {
				
		String criterio =	(datos.get("criterio") != null) ? (String)datos.get("criterio"): "";
		String valor =	(datos.get("valor") != null) ? (String)datos.get("valor"): "";		
		String regimen =	(datos.get("regimen") != null) ? (String)datos.get("regimen"): "";
			
		if(log.isDebugEnabled())log.debug("datos en findNotificaDirectivosByCriterioValor:" + datos);		
		
		StringBuffer strSQLFinal = new StringBuffer("");
		StringBuffer strSQL = new StringBuffer("");
		List lista = null;				
		
		if (constantes.leePropiedad("CRITERIO_COD").equals(criterio)) {			
			if (log.isDebugEnabled())log.debug("entro criterio COD-->");
			if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
				strSQL.append("and p.t02cod_rel not in (?,?)")
				.append( " and param.t99cod_tab = ? ")
				.append( " and param.t99tip_desc = ? ")
				.append( "and n.cod_pers_notif = '").append( valor.trim().toUpperCase()).append( "'");
			} else if (regimen.equals("09")){					
				strSQL.append( "and   p.t02cod_rel = ? ")
				.append( " and param.t99cod_tab = ? ")
				.append( " and param.t99tip_desc = ? ")
				.append( "and n.cod_pers_notif = '").append( valor.trim().toUpperCase()).append( "'");				
			} else if (regimen.equals("10")){
				strSQL.append( "and   p.t02cod_rel = ? ")
				.append( " and param.t99cod_tab = ? ")
				.append( " and param.t99tip_desc = ? ")
				.append( "and n.cod_pers_notif = '").append( valor.trim().toUpperCase()).append( "'");				
			}			
		} 	
				
		if (constantes.leePropiedad("CRITERIO_UUOO").equals(criterio)) {				  
			if (log.isDebugEnabled())log.debug("entro criterio UUOO-->");
			if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
				strSQL.append("and p.t02cod_rel not in (?,?)")
				.append( " and param.t99cod_tab = ? ")
				.append( " and param.t99tip_desc = ? ");
			} else if (regimen.equals("09")){
				strSQL.append( "and   p.t02cod_rel = ? ")
				.append( " and param.t99cod_tab = ? ")
				.append( " and param.t99tip_desc = ? ");
			} else if (regimen.equals("10")){
				strSQL.append( "and   p.t02cod_rel = ? ")
				.append( " and param.t99cod_tab = ? ")
				.append( " and param.t99tip_desc = ? ");
			}
			strSQL.append( "and uo.t12cod_uorga = '").append( valor.trim().toUpperCase()).append( "'");
		}
		
		if (constantes.leePropiedad("CRITERIO_INTD").equals(criterio)) {			
			String intendencia = valor.substring(0,2);  
			if (log.isDebugEnabled())log.debug("codigo intendencia:" + intendencia);			
			if (log.isDebugEnabled())log.debug("entro criterio INTD-->");
			if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
				strSQL.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) LIKE '")
				.append(intendencia.trim().toUpperCase()).append("%'")
				.append( " and   p.t02cod_rel not in (?,?) ") //not in (09,10)
				.append( " and param.t99cod_tab = ? ")
				.append( " and param.t99tip_desc = ? ");
			} else if (constantes.leePropiedad("CODREL_REG1057").equals(regimen)){
				strSQL.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) LIKE '")
				.append(intendencia.trim().toUpperCase()).append("%'")				
				.append( " and p.t02cod_rel = ? ") //= '09'
				.append( " and param.t99cod_tab = ? ")
				.append( " and param.t99tip_desc = ? ");
			} else if (constantes.leePropiedad("CODREL_MOD_FORMATIVA").equals(regimen)){
				strSQL.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) LIKE '")
				.append(intendencia.trim().toUpperCase()).append("%'")				
				.append( " and p.t02cod_rel = ? ") //= '10'
				.append( " and param.t99cod_tab = ? ")
				.append( " and param.t99tip_desc = ? ");
			}
		}
		
		if (constantes.leePropiedad("CRITERIO_DEP").equals(criterio)) {			  			  
			  if(log.isDebugEnabled())log.debug("entro criterio DEP-->");
			  if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
				    strSQL.append( "and   p.t02cod_rel not in (?,?) ") //not in (09,10)
					.append( " and param.t99cod_tab = ? ")
					.append( " and param.t99tip_desc = ? ");
				} else if (constantes.leePropiedad("CODREL_REG1057").equals(regimen)){
					strSQL.append( "and   p.t02cod_rel = ? ") //= '09'
					.append( " and param.t99cod_tab = ? ")
					.append( " and param.t99tip_desc = ? ");
				} else if (constantes.leePropiedad("CODREL_MOD_FORMATIVA").equals(regimen)){
					strSQL.append( "and   p.t02cod_rel = ? ") //= '10'
					.append( " and param.t99cod_tab = ? ")
					.append( " and param.t99tip_desc = ? ");
				}
		}
		
		/*strSQL.append( " and param.t99cod_tab = ? ")
		.append( " and param.t99tip_desc = ? ");*/				
		strSQL.append(" order by uo.t12cod_uorga asc, n.cod_pers_notif asc ");
		
		Object o[] = null;			
				/*o = new Object[]{constantes.leePropiedad("CODTAB_CATEGORIA"),
						constantes.leePropiedad("T99DETALLE")};*/
				if (constantes.leePropiedad("CRITERIO_COD").equals(criterio)) {
					if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
						o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
								constantes.leePropiedad("CODREL_MOD_FORMATIVA"), 
								constantes.leePropiedad("CODTAB_CATEGORIA"),
								constantes.leePropiedad("T99DETALLE")};
					} else if (regimen.equals("09")){
						o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
								constantes.leePropiedad("CODTAB_CATEGORIA"),
								constantes.leePropiedad("T99DETALLE")};
					} else if (regimen.equals("10")){
						o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),
								constantes.leePropiedad("CODTAB_CATEGORIA"),
								constantes.leePropiedad("T99DETALLE")};
					}					
				}
				
				if (constantes.leePropiedad("CRITERIO_UUOO").equals(criterio)) {
					if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
						o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
								constantes.leePropiedad("CODREL_MOD_FORMATIVA"), 
								constantes.leePropiedad("CODTAB_CATEGORIA"),
								constantes.leePropiedad("T99DETALLE")};
					} else if (regimen.equals("09")){
						o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
								constantes.leePropiedad("CODTAB_CATEGORIA"),
								constantes.leePropiedad("T99DETALLE")};
					} else if (regimen.equals("10")){
						o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),
								constantes.leePropiedad("CODTAB_CATEGORIA"),
								constantes.leePropiedad("T99DETALLE")};
					}					
				}
				
				if (constantes.leePropiedad("CRITERIO_INTD").equals(criterio)) {
					if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
						o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
								constantes.leePropiedad("CODREL_MOD_FORMATIVA"), 
								constantes.leePropiedad("CODTAB_CATEGORIA"),
								constantes.leePropiedad("T99DETALLE")};
					} else if (regimen.equals("09")){
						o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
								constantes.leePropiedad("CODTAB_CATEGORIA"),
								constantes.leePropiedad("T99DETALLE")};
					} else if (regimen.equals("10")){
						o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),
								constantes.leePropiedad("CODTAB_CATEGORIA"),
								constantes.leePropiedad("T99DETALLE")};
					}					
				}
				
				if (constantes.leePropiedad("CRITERIO_DEP").equals(criterio)) {
					if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
						o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
								constantes.leePropiedad("CODREL_MOD_FORMATIVA"), 
								constantes.leePropiedad("CODTAB_CATEGORIA"),
								constantes.leePropiedad("T99DETALLE")};
					} else if (regimen.equals("09")){
						o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
								constantes.leePropiedad("CODTAB_CATEGORIA"),
								constantes.leePropiedad("T99DETALLE")};
					} else if (regimen.equals("10")){
						o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),
								constantes.leePropiedad("CODTAB_CATEGORIA"),
								constantes.leePropiedad("T99DETALLE")};
					}					
				}				
			
		strSQLFinal.append(FIND_NOTIFICA_DIRECTIVO_BY_CRITERIO_VALOR.toString()).append( strSQL.toString());
		if(log.isDebugEnabled())log.debug("strSQLFinal:" + strSQLFinal);
		lista = executeQuery(dataSource, strSQLFinal.toString(), o);
		
		return lista;			
	}//FIN - JVILLACORTA - 19/04/2011 - ALERTA DE SOLICITUDES
	
	/**JVILLACORTA - 18/04/2011 - ALERTA DE SOLICITUDES
	 * Metodo encargado de buscar notificaciones de movimientos de asistencia a trabajadores 
	 * de acuerdo a criterio de busqueda
	 * @param Map datos
	 * @return List 
	 * @throws DAOException
	 */	
	public List findNotificaTrabajadoresByCriterioValor(Map datos)
	throws DAOException {
				
		String criterio =	(datos.get("criterio") != null) ? (String)datos.get("criterio"): "";
		String valor =	(datos.get("valor") != null) ? (String)datos.get("valor"): "";
		String regimen =	(datos.get("regimen") != null) ? (String)datos.get("regimen"): "";
			
		if(log.isDebugEnabled())log.debug("datos en findNotificaTrabajadoresByCriterioValor:" + datos);		
		
		StringBuffer strSQLFinal = new StringBuffer("");
		StringBuffer strSQL = new StringBuffer("");
		List lista = null;				
				
		if (constantes.leePropiedad("CRITERIO_COD").equals(criterio)) {			
			if (log.isDebugEnabled())log.debug("entro criterio COD-->");
			if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
				strSQL.append("and p.t02cod_rel not in (?,?)")
				.append( " and param.t99cod_tab = ? ")
				.append( " and param.t99tip_desc = ? ")
				.append( "and n.cod_pers = '").append( valor.trim().toUpperCase()).append( "'");
			} else if (regimen.equals("09")){
				strSQL.append( "and   p.t02cod_rel = ? ")
				.append( " and param.t99cod_tab = ? ")
				.append( " and param.t99tip_desc = ? ")
				.append( "and n.cod_pers = '").append( valor.trim().toUpperCase()).append( "'");
			} else if (regimen.equals("10")){
				strSQL.append( "and   p.t02cod_rel = ? ")
				.append( " and param.t99cod_tab = ? ")
				.append( " and param.t99tip_desc = ? ")
				.append( "and n.cod_pers = '").append( valor.trim().toUpperCase()).append( "'");
			}
		} 	
			
		if (constantes.leePropiedad("CRITERIO_UUOO").equals(criterio)) {				  
			if (log.isDebugEnabled())log.debug("entro criterio UUOO-->");
			if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
				strSQL.append("and p.t02cod_rel not in (?,?)")
				.append( " and param.t99cod_tab = ? ")
				.append( " and param.t99tip_desc = ? ");
			} else if (regimen.equals("09")){
				strSQL.append( "and   p.t02cod_rel = ? ")
				.append( " and param.t99cod_tab = ? ")
				.append( " and param.t99tip_desc = ? ");
			} else if (regimen.equals("10")){
				strSQL.append( "and   p.t02cod_rel = ? ")
				.append( " and param.t99cod_tab = ? ")
				.append( " and param.t99tip_desc = ? ");
			}
			//strSQL.append( "and p.t02cod_uorg = '").append( valor.trim().toUpperCase()).append( "'");
			strSQL.append( "and uo.t12cod_uorga = '").append( valor.trim().toUpperCase()).append( "'");
		}
		
		if (constantes.leePropiedad("CRITERIO_INTD").equals(criterio)) {			
			String intendencia = valor.substring(0,2);  
			if (log.isDebugEnabled())log.debug("codigo intendencia:" + intendencia);			
			if (log.isDebugEnabled())log.debug("entro criterio INTD-->");
			if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
				strSQL.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) LIKE '")
				.append(intendencia.trim().toUpperCase()).append("%'")
				.append( " and   p.t02cod_rel not in (?,?) ") //not in (09,10)
				.append( " and param.t99cod_tab = ? ")
				.append( " and param.t99tip_desc = ? ");
			} else if (constantes.leePropiedad("CODREL_REG1057").equals(regimen)){
				strSQL.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) LIKE '")
				.append(intendencia.trim().toUpperCase()).append("%'")				
				.append( " and p.t02cod_rel = ? ") //= '09'
				.append( " and param.t99cod_tab = ? ")
				.append( " and param.t99tip_desc = ? ");
			} else if (constantes.leePropiedad("CODREL_MOD_FORMATIVA").equals(regimen)){
				strSQL.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) LIKE '")
				.append(intendencia.trim().toUpperCase()).append("%'")				
				.append( " and p.t02cod_rel = ? ") //= '10'
				.append( " and param.t99cod_tab = ? ")
				.append( " and param.t99tip_desc = ? ");
			}
		}

		if (constantes.leePropiedad("CRITERIO_DEP").equals(criterio)) {			  			  
			  if(log.isDebugEnabled())log.debug("entro criterio DEP-->");
			  if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
				    strSQL.append( "and   p.t02cod_rel not in (?,?) ") //not in (09,10)
					.append( " and param.t99cod_tab = ? ")
					.append( " and param.t99tip_desc = ? ");
				} else if (constantes.leePropiedad("CODREL_REG1057").equals(regimen)){
					strSQL.append( "and   p.t02cod_rel = ? ") //= '09'
					.append( " and param.t99cod_tab = ? ")
					.append( " and param.t99tip_desc = ? ");
				} else if (constantes.leePropiedad("CODREL_MOD_FORMATIVA").equals(regimen)){
					strSQL.append( "and   p.t02cod_rel = ? ") //= '10'
					.append( " and param.t99cod_tab = ? ")
					.append( " and param.t99tip_desc = ? ");
				}
		}
		
		/*strSQL.append( " and param.t99cod_tab = ? ")
		.append( " and param.t99tip_desc = ? ");*/					
		strSQL.append(" order by uo.t12cod_uorga asc,n.cod_pers asc ");
		
		Object o[] = null;			
		/*o = new Object[]{constantes.leePropiedad("CODTAB_CATEGORIA"),
						constantes.leePropiedad("T99DETALLE")};*/			

		if (constantes.leePropiedad("CRITERIO_COD").equals(criterio)) {
			if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
				o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
						constantes.leePropiedad("CODREL_MOD_FORMATIVA"), 
						constantes.leePropiedad("CODTAB_CATEGORIA"),
						constantes.leePropiedad("T99DETALLE")};
			} else if (regimen.equals("09")){
				o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
						constantes.leePropiedad("CODTAB_CATEGORIA"),
						constantes.leePropiedad("T99DETALLE")};
			} else if (regimen.equals("10")){
				o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),
						constantes.leePropiedad("CODTAB_CATEGORIA"),
						constantes.leePropiedad("T99DETALLE")};
			}					
		}
		
		if (constantes.leePropiedad("CRITERIO_UUOO").equals(criterio)) {
			if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
				o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
						constantes.leePropiedad("CODREL_MOD_FORMATIVA"), 
						constantes.leePropiedad("CODTAB_CATEGORIA"),
						constantes.leePropiedad("T99DETALLE")};
			} else if (regimen.equals("09")){
				o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
						constantes.leePropiedad("CODTAB_CATEGORIA"),
						constantes.leePropiedad("T99DETALLE")};
			} else if (regimen.equals("10")){
				o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),
						constantes.leePropiedad("CODTAB_CATEGORIA"),
						constantes.leePropiedad("T99DETALLE")};
			}					
		}
		
		if (constantes.leePropiedad("CRITERIO_INTD").equals(criterio)) {
			if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
				o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
						constantes.leePropiedad("CODREL_MOD_FORMATIVA"), 
						constantes.leePropiedad("CODTAB_CATEGORIA"),
						constantes.leePropiedad("T99DETALLE")};
			} else if (regimen.equals("09")){
				o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
						constantes.leePropiedad("CODTAB_CATEGORIA"),
						constantes.leePropiedad("T99DETALLE")};
			} else if (regimen.equals("10")){
				o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),
						constantes.leePropiedad("CODTAB_CATEGORIA"),
						constantes.leePropiedad("T99DETALLE")};
			}					
		}
		
		if (constantes.leePropiedad("CRITERIO_DEP").equals(criterio)) {
			if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
				o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
						constantes.leePropiedad("CODREL_MOD_FORMATIVA"), 
						constantes.leePropiedad("CODTAB_CATEGORIA"),
						constantes.leePropiedad("T99DETALLE")};
			} else if (regimen.equals("09")){
				o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
						constantes.leePropiedad("CODTAB_CATEGORIA"),
						constantes.leePropiedad("T99DETALLE")};
			} else if (regimen.equals("10")){
				o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),
						constantes.leePropiedad("CODTAB_CATEGORIA"),
						constantes.leePropiedad("T99DETALLE")};
			}					
		}	
		
		strSQLFinal.append(FIND_NOTIFICA_TRABAJADOR_BY_CRITERIO_VALOR.toString()).append( strSQL.toString());
		if(log.isDebugEnabled())log.debug("strSQLFinal:" + strSQLFinal);
		lista = executeQuery(dataSource, strSQLFinal.toString(), o);
		
		return lista;			
	}//FIN - JVILLACORTA - 19/04/2011 - ALERTA DE SOLICITUDES
	
	/**JVILLACORTA - 20/04/2011 - ALERTA DE SOLICITUDES
	 * Metodo encargado de buscar el detalle de notificaciones a directivos 
	 * de acuerdo a criterio de busqueda
	 * @param Map datos
	 * @return List 
	 * @throws DAOException
	 */	
	public List findDetalleNotificaDirec(Map datos)
	throws DAOException {
		List lista = null;
		try {		
			if (log.isDebugEnabled()) log.debug("codNotif: " + datos.get("codNotif"));
			if (log.isDebugEnabled()) log.debug("fIni: " + datos.get("fIni"));	
			if (log.isDebugEnabled()) log.debug("fFin: " + datos.get("fFin"));
			
			Date fechIni = new Date();
			Date fechFin = new Date();
			fechIni = new FechaBean((String)datos.get("fIni")).getSQLDate();
			fechFin = new FechaBean((String)datos.get("fFin")).getSQLDate();
						
			lista = executeQuery(dataSource, FIND_DETALLE_NOTIFICA_DIREC.toString(),				 
					new Object[]{ datos.get("codNotif"), fechIni, fechFin});
				
		} catch (Exception e) {
			log.error("*** SQL Error ****",e);
			throw new DAOException(this, "Error en la consulta. ReporteDAO - [findDetalleNotificaDirec]");
		} finally {	
		}
		return lista;			
	} //FIN - JVILLACORTA - 19/04/2011 - ALERTA DE SOLICITUDES
	
	/**JVILLACORTA - 19/04/2011 - ALERTA DE SOLICITUDES
	 * Metodo encargado de listar las notificaciones realizadas a directivos 
	 * en las 4 semanas posteriores a la fecha de inicio
	 * @param Map datos
	 * @return List 
	 * @throws DAOException
	 */	
	public List findNotificacionesDirec(Map hm, String codPers)
	throws DAOException {		
		
		StringBuffer strSQL = new StringBuffer("");
		List res = null;
		try {
			if (log.isDebugEnabled()) log.debug("codPers: " + codPers);
			if (log.isDebugEnabled()) log.debug("fechaIni: " + hm.get("fechaIni"));			
			if (log.isDebugEnabled()) log.debug("fchSem1: " + hm.get("fchSem1"));
			if (log.isDebugEnabled()) log.debug("fchSem2: " + hm.get("fchSem2"));
			if (log.isDebugEnabled()) log.debug("fchSem3: " + hm.get("fchSem3"));
			if (log.isDebugEnabled()) log.debug("fchSem4: " + hm.get("fchSem4"));
			if (log.isDebugEnabled()) log.debug("fchSem5: " + hm.get("fechaFin"));
			if (log.isDebugEnabled()) log.debug("semana: " + hm.get("semana"));
			
			Date fIni = new Date();
			Date fSem1 = new Date();
			Date fSem2 = new Date();
			Date fSem3 = new Date();
			Date fSem4 = new Date();
			Date fSem5 = new Date();
			fIni = new FechaBean((String)hm.get("fechaIni")).getSQLDate();
			if (log.isDebugEnabled()) log.debug("fIni: " + fIni);				
			fSem1 = new FechaBean((String)hm.get("fchSem1")).getSQLDate();
			if (log.isDebugEnabled()) log.debug("fSem1: " + fSem1);	
			fSem2 = new FechaBean((String)hm.get("fchSem2")).getSQLDate();
			if (log.isDebugEnabled()) log.debug("fSem2: " + fSem2);	
			fSem3 = new FechaBean((String)hm.get("fchSem3")).getSQLDate();
			if (log.isDebugEnabled()) log.debug("fSem3: " + fSem3);	
			fSem4 = new FechaBean((String)hm.get("fchSem4")).getSQLDate();
			if (log.isDebugEnabled()) log.debug("fSem4: " + fSem4);
			fSem5 = new FechaBean((String)hm.get("fechaFin")).getSQLDate();
			if (log.isDebugEnabled()) log.debug("fSem5: " + fSem5);
			
			String semana = (hm.get("semana") != null) ? (String)hm.get("semana"): "";
			
			if (semana.equals("1")){
				//strSQL .append(" select cod_pers_notif, 'Sem1' as semana, count(cod_pers_notif) as cantidad from t4562NotificaDir ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				strSQL .append(" select cod_pers_notif, 'Sem1' as semana, count(distinct num_notificacion) as cantidad from t4562NotificaDir ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				.append(" where cod_pers_notif = ? ") 
				.append(" and fec_notificacion >= ? and fec_notificacion < ? group by cod_pers_notif ");				
			} else if (semana.equals("2")){
				//strSQL .append(" select cod_pers_notif, 'Sem1' as semana, count(cod_pers_notif) as cantidad from t4562NotificaDir ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				strSQL .append(" select cod_pers_notif, 'Sem1' as semana, count(distinct num_notificacion) as cantidad from t4562NotificaDir ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				.append(" where cod_pers_notif = ? ") 
				.append(" and fec_notificacion >= ? and fec_notificacion < ? group by cod_pers_notif ")
				.append(" union all ")
				//.append(" select cod_pers_notif, 'Sem2' as semana, count(cod_pers_notif) as cantidad from t4562NotificaDir ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				.append(" select cod_pers_notif, 'Sem2' as semana, count(distinct num_notificacion) as cantidad from t4562NotificaDir ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				.append(" where cod_pers_notif = ? ")
				.append(" and fec_notificacion >= ? and fec_notificacion < ? group by cod_pers_notif ");
			} else if (semana.equals("3")){
				//strSQL .append(" select cod_pers_notif, 'Sem1' as semana, count(cod_pers_notif) as cantidad from t4562NotificaDir ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				strSQL .append(" select cod_pers_notif, 'Sem1' as semana, count(distinct num_notificacion) as cantidad from t4562NotificaDir ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				.append(" where cod_pers_notif = ? ") 
				.append(" and fec_notificacion >= ? and fec_notificacion < ? group by cod_pers_notif ")
				.append(" union all ")
				//.append(" select cod_pers_notif, 'Sem2' as semana, count(cod_pers_notif) as cantidad from t4562NotificaDir ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				.append(" select cod_pers_notif, 'Sem2' as semana, count(distinct num_notificacion) as cantidad from t4562NotificaDir ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				.append(" where cod_pers_notif = ? ")
				.append(" and fec_notificacion >= ? and fec_notificacion < ? group by cod_pers_notif ")
				.append(" union all ")
				//.append(" select cod_pers_notif, 'Sem3' as semana, count(cod_pers_notif) as cantidad from t4562NotificaDir ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				 .append(" select cod_pers_notif, 'Sem3' as semana, count(distinct num_notificacion) as cantidad from t4562NotificaDir ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				.append(" where cod_pers_notif = ? ")
				.append(" and fec_notificacion >= ? and fec_notificacion < ? group by cod_pers_notif ");
			} else if (semana.equals("4")){
				//strSQL .append(" select cod_pers_notif, 'Sem1' as semana, count(cod_pers_notif) as cantidad from t4562NotificaDir ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				strSQL .append(" select cod_pers_notif, 'Sem1' as semana, count(distinct num_notificacion) as cantidad from t4562NotificaDir ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				.append(" where cod_pers_notif = ? ") 
				.append(" and fec_notificacion >= ? and fec_notificacion < ? group by cod_pers_notif ")
				.append(" union all ")
				//.append(" select cod_pers_notif, 'Sem2' as semana, count(cod_pers_notif) as cantidad from t4562NotificaDir ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				.append(" select cod_pers_notif, 'Sem2' as semana, count(distinct num_notificacion) as cantidad from t4562NotificaDir ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				.append(" where cod_pers_notif = ? ")
				.append(" and fec_notificacion >= ? and fec_notificacion < ? group by cod_pers_notif ")
				.append(" union all ")
				//.append(" select cod_pers_notif, 'Sem3' as semana, count(cod_pers_notif) as cantidad from t4562NotificaDir ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				.append(" select cod_pers_notif, 'Sem3' as semana, count(distinct num_notificacion) as cantidad from t4562NotificaDir ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				.append(" where cod_pers_notif = ? ")
				.append(" and fec_notificacion >= ? and fec_notificacion < ? group by cod_pers_notif ")
				.append(" union all ")
				//.append(" select cod_pers_notif, 'Sem4' as semana, count(cod_pers_notif) as cantidad from t4562NotificaDir  ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				.append(" select cod_pers_notif, 'Sem4' as semana, count(distinct num_notificacion) as cantidad from t4562NotificaDir ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				.append(" where cod_pers_notif = ? ")
				.append(" and fec_notificacion >= ? and fec_notificacion < ? group by cod_pers_notif ");
			} else if (semana.equals("5")){
				strSQL .append(" select cod_pers_notif, 'Sem1' as semana, count(distinct num_notificacion) as cantidad from t4562NotificaDir ") 
				.append(" where cod_pers_notif = ? ") 
				.append(" and fec_notificacion >= ? and fec_notificacion < ? group by cod_pers_notif ")
				.append(" union all ")				
				.append(" select cod_pers_notif, 'Sem2' as semana, count(distinct num_notificacion) as cantidad from t4562NotificaDir ") 
				.append(" where cod_pers_notif = ? ")
				.append(" and fec_notificacion >= ? and fec_notificacion < ? group by cod_pers_notif ")
				.append(" union all ")				
				.append(" select cod_pers_notif, 'Sem3' as semana, count(distinct num_notificacion) as cantidad from t4562NotificaDir ") 
				.append(" where cod_pers_notif = ? ")
				.append(" and fec_notificacion >= ? and fec_notificacion < ? group by cod_pers_notif ")
				.append(" union all ")				
				.append(" select cod_pers_notif, 'Sem4' as semana, count(distinct num_notificacion) as cantidad from t4562NotificaDir ") 
				.append(" where cod_pers_notif = ? ")
				.append(" and fec_notificacion >= ? and fec_notificacion < ? group by cod_pers_notif ")
				.append(" union all ")				
				.append(" select cod_pers_notif, 'Sem5' as semana, count(distinct num_notificacion) as cantidad from t4562NotificaDir ") 
				.append(" where cod_pers_notif = ? ")
				.append(" and fec_notificacion >= ? and fec_notificacion <= ? group by cod_pers_notif ");
			}
			
			Object o[] = null;
			
			if (semana.equals("1")){
				o = new Object[]{codPers, fIni, fSem1};				
			} else if (semana.equals("2")){
				o = new Object[]{codPers, fIni, fSem1,
					      		 codPers, fSem1, fSem2};
			} else if (semana.equals("3")){
				o = new Object[]{codPers, fIni, fSem1,
							     codPers, fSem1, fSem2,
							     codPers, fSem2, fSem3};
			} else if (semana.equals("4")){
				o = new Object[]{codPers, fIni, fSem1,
					      codPers, fSem1, fSem2,
					      codPers, fSem2, fSem3,
					      codPers, fSem3, fSem4};
			} else if (semana.equals("5")){
				o = new Object[]{codPers, fIni, fSem1,
					      codPers, fSem1, fSem2,
					      codPers, fSem2, fSem3,
					      codPers, fSem3, fSem4,
					      codPers, fSem4, fSem5};
			}
			
			if(log.isDebugEnabled())log.debug("strSQL:" + strSQL);
			res = executeQuery(dataSource, strSQL.toString(), o);
			/*res = executeQuery(dataSource, FIND_NOTIFICACIONES_DIREC.toString(),
					new Object[]{ codPers, fIni, fSem1,
							      codPers, fSem1, fSem2,
							      codPers, fSem2, fSem3,
							      codPers, fSem3, fSem4});*/				
			
		} catch (Exception e) {
			log.error("*** SQL Error ****",e);
			throw new DAOException(this, "Error en la consulta. ReporteDAO - [findNotificacionesDirec]");
		} finally {	
		}
		return res;				
	}//FIN - JVILLACORTA - 19/04/2011 - ALERTA DE SOLICITUDES
	
	/**JVILLACORTA - 18/04/2011 - ALERTA DE SOLICITUDES
	 * Metodo encargado de listar las notificaciones realizadas a trabajadores 
	 * en las 4 semanas posteriores a la fecha de inicio
	 * @param Map datos
	 * @return List 
	 * @throws DAOException
	 */	
	public List findNotificacionesTrab(Map hm, String codPers)
	throws DAOException {
		
		StringBuffer strSQL = new StringBuffer("");
		List res = null;
		try {
			if (log.isDebugEnabled()) log.debug("codPers: " + codPers);
			if (log.isDebugEnabled()) log.debug("fechaIni: " + hm.get("fechaIni"));			
			if (log.isDebugEnabled()) log.debug("fchSem1: " + hm.get("fchSem1"));
			if (log.isDebugEnabled()) log.debug("fchSem2: " + hm.get("fchSem2"));
			if (log.isDebugEnabled()) log.debug("fchSem3: " + hm.get("fchSem3"));
			if (log.isDebugEnabled()) log.debug("fchSem4: " + hm.get("fchSem4"));
			if (log.isDebugEnabled()) log.debug("fchSem5: " + hm.get("fechaFin"));
			if (log.isDebugEnabled()) log.debug("semana: " + hm.get("semana"));
			
			Date fIni = new Date();
			Date fSem1 = new Date();
			Date fSem2 = new Date();
			Date fSem3 = new Date();
			Date fSem4 = new Date();
			Date fSem5 = new Date();
			fIni = new FechaBean((String)hm.get("fechaIni")).getSQLDate();
			if (log.isDebugEnabled()) log.debug("fIni: " + fIni);
			fSem1 = new FechaBean((String)hm.get("fchSem1")).getSQLDate();	
			if (log.isDebugEnabled()) log.debug("fSem1: " + fSem1);
			fSem2 = new FechaBean((String)hm.get("fchSem2")).getSQLDate();
			if (log.isDebugEnabled()) log.debug("fSem2: " + fSem2);
			fSem3 = new FechaBean((String)hm.get("fchSem3")).getSQLDate();
			if (log.isDebugEnabled()) log.debug("fSem3: " + fSem3);
			fSem4 = new FechaBean((String)hm.get("fchSem4")).getSQLDate();
			if (log.isDebugEnabled()) log.debug("fSem4: " + fSem4);
			fSem5 = new FechaBean((String)hm.get("fechaFin")).getSQLDate();
			if (log.isDebugEnabled()) log.debug("fSem5: " + fSem5);
			
            String semana = (hm.get("semana") != null) ? (String)hm.get("semana"): "";
			
			if (semana.equals("1")){
				//strSQL .append(" select cod_pers, 'Sem1' as semana, count(cod_pers) as cantidad from t4563NotificaTra ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				strSQL .append(" select cod_pers, 'Sem1' as semana, count(distinct num_notificacion) as cantidad from t4563NotificaTra ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				.append(" where cod_pers = ? ") 
				.append(" and fec_notificacion >= ? and fec_notificacion < ? group by cod_pers ");				
			} else if (semana.equals("2")){
				//strSQL .append(" select cod_pers, 'Sem1' as semana, count(cod_pers) as cantidad from t4563NotificaTra ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				strSQL .append(" select cod_pers, 'Sem1' as semana, count(distinct num_notificacion) as cantidad from t4563NotificaTra ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				.append(" where cod_pers = ? ") 
				.append(" and fec_notificacion >= ? and fec_notificacion < ? group by cod_pers ")
				.append(" union all ")
				//.append(" select cod_pers, 'Sem2' as semana, count(cod_pers) as cantidad from t4563NotificaTra ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				.append(" select cod_pers, 'Sem2' as semana, count(distinct num_notificacion) as cantidad from t4563NotificaTra ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				.append(" where cod_pers = ? ")
				.append(" and fec_notificacion >= ? and fec_notificacion < ? group by cod_pers ");
			} else if (semana.equals("3")){
				//strSQL .append(" select cod_pers, 'Sem1' as semana, count(cod_pers) as cantidad from t4563NotificaTra ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				strSQL .append(" select cod_pers, 'Sem1' as semana, count(distinct num_notificacion) as cantidad from t4563NotificaTra ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				.append(" where cod_pers = ? ") 
				.append(" and fec_notificacion >= ? and fec_notificacion < ? group by cod_pers ")
				.append(" union all ")
				//.append(" select cod_pers, 'Sem2' as semana, count(cod_pers) as cantidad from t4563NotificaTra ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				.append(" select cod_pers, 'Sem2' as semana, count(distinct num_notificacion) as cantidad from t4563NotificaTra ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				.append(" where cod_pers = ? ")
				.append(" and fec_notificacion >= ? and fec_notificacion < ? group by cod_pers ")
				.append(" union all ")
				//.append(" select cod_pers, 'Sem3' as semana, count(cod_pers) as cantidad from t4563NotificaTra ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				.append(" select cod_pers, 'Sem3' as semana, count(distinct num_notificacion) as cantidad from t4563NotificaTra ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				.append(" where cod_pers = ? ")
				.append(" and fec_notificacion >= ? and fec_notificacion < ? group by cod_pers ");
			} else if (semana.equals("4")){
				//strSQL .append(" select cod_pers, 'Sem1' as semana, count(cod_pers) as cantidad from t4563NotificaTra ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				strSQL .append(" select cod_pers, 'Sem1' as semana, count(distinct num_notificacion) as cantidad from t4563NotificaTra ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				.append(" where cod_pers = ? ") 
				.append(" and fec_notificacion >= ? and fec_notificacion < ? group by cod_pers ")
				.append(" union all ")
				//.append(" select cod_pers, 'Sem2' as semana, count(cod_pers) as cantidad from t4563NotificaTra ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				.append(" select cod_pers, 'Sem2' as semana, count(distinct num_notificacion) as cantidad from t4563NotificaTra ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				.append(" where cod_pers = ? ")
				.append(" and fec_notificacion >= ? and fec_notificacion < ? group by cod_pers ")
				.append(" union all ")
				//.append(" select cod_pers, 'Sem3' as semana, count(cod_pers) as cantidad from t4563NotificaTra ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				.append(" select cod_pers, 'Sem3' as semana, count(distinct num_notificacion) as cantidad from t4563NotificaTra ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				.append(" where cod_pers = ? ")
				.append(" and fec_notificacion >= ? and fec_notificacion < ? group by cod_pers ")
				.append(" union all ")
				//.append(" select cod_pers, 'Sem4' as semana, count(cod_pers) as cantidad from t4563NotificaTra ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				.append(" select cod_pers, 'Sem4' as semana, count(distinct num_notificacion) as cantidad from t4563NotificaTra ") //ICAPUNAY 10/08/2011 CONTABILIZAR NUMERO REAL NOTIFICACIONES
				.append(" where cod_pers = ? ")
				.append(" and fec_notificacion >= ? and fec_notificacion < ? group by cod_pers ");
			} else if (semana.equals("5")){				
				strSQL .append(" select cod_pers, 'Sem1' as semana, count(distinct num_notificacion) as cantidad from t4563NotificaTra ") 
				.append(" where cod_pers = ? ") 
				.append(" and fec_notificacion >= ? and fec_notificacion < ? group by cod_pers ")
				.append(" union all ")				
				.append(" select cod_pers, 'Sem2' as semana, count(distinct num_notificacion) as cantidad from t4563NotificaTra ") 
				.append(" where cod_pers = ? ")
				.append(" and fec_notificacion >= ? and fec_notificacion < ? group by cod_pers ")
				.append(" union all ")				
				.append(" select cod_pers, 'Sem3' as semana, count(distinct num_notificacion) as cantidad from t4563NotificaTra ") 
				.append(" where cod_pers = ? ")
				.append(" and fec_notificacion >= ? and fec_notificacion < ? group by cod_pers ")
				.append(" union all ")				
				.append(" select cod_pers, 'Sem4' as semana, count(distinct num_notificacion) as cantidad from t4563NotificaTra ") 
				.append(" where cod_pers = ? ")
				.append(" and fec_notificacion >= ? and fec_notificacion < ? group by cod_pers ")
				.append(" union all ")				
				.append(" select cod_pers, 'Sem5' as semana, count(distinct num_notificacion) as cantidad from t4563NotificaTra ") 
				.append(" where cod_pers = ? ")
				.append(" and fec_notificacion >= ? and fec_notificacion <= ? group by cod_pers ");				
			}
			
			Object o[] = null;
			
			if (semana.equals("1")){
				o = new Object[]{codPers, fIni, fSem1};				
			} else if (semana.equals("2")){
				o = new Object[]{codPers, fIni, fSem1,
					      		 codPers, fSem1, fSem2};
			} else if (semana.equals("3")){
				o = new Object[]{codPers, fIni, fSem1,
							     codPers, fSem1, fSem2,
							     codPers, fSem2, fSem3};
			} else if (semana.equals("4")){
				o = new Object[]{codPers, fIni, fSem1,
					      codPers, fSem1, fSem2,
					      codPers, fSem2, fSem3,
					      codPers, fSem3, fSem4};
			} else if (semana.equals("5")){
				o = new Object[]{codPers, fIni, fSem1,
					      codPers, fSem1, fSem2,
					      codPers, fSem2, fSem3,
					      codPers, fSem3, fSem4,
					      codPers, fSem4, fSem5};
			}
			
			if(log.isDebugEnabled())log.debug("strSQL:" + strSQL);
			res = executeQuery(dataSource, strSQL.toString(), o);
			/*res = executeQuery(dataSource, FIND_NOTIFICACIONES_TRAB.toString(),
					new Object[]{ codPers, fIni, fSem1,
							      codPers, fSem1, fSem2,
							      codPers, fSem2, fSem3,
							      codPers, fSem3, fSem4});*/				
			
		} catch (Exception e) {
			log.error("*** SQL Error ****",e);
			throw new DAOException(this, "Error en la consulta. ReporteDAO - [findNotificacionesTrab]");
		} finally {	
		}
		return res;				
	}//FIN - JVILLACORTA - 19/04/2011 - ALERTA DE SOLICITUDES
	
	/**JVILLACORTA - 29/11/2011 - ALERTA DE SOLICITUDES
	 * Metodo encargado de buscar el detalle de notificaciones a directivos 
	 * de acuerdo a criterio de busqueda
	 * @param Map datos
	 * @return List 
	 * @throws DAOException
	 */	
	public List findDetalleNotificaTrab(Map datos)
	throws DAOException {
		List lista = null;
		try {		
			if (log.isDebugEnabled()) log.debug("codNotif: " + datos.get("codNotif"));
			if (log.isDebugEnabled()) log.debug("fIni: " + datos.get("fIni"));	
			if (log.isDebugEnabled()) log.debug("fFin: " + datos.get("fFin"));
			
			Date fechIni = new Date();
			Date fechFin = new Date();
			fechIni = new FechaBean((String)datos.get("fIni")).getSQLDate();
			fechFin = new FechaBean((String)datos.get("fFin")).getSQLDate();
						
			lista = executeQuery(dataSource, FIND_DETALLE_NOTIFICA_TRAB.toString(),				 
					new Object[]{ datos.get("codNotif"), fechIni, fechFin});
				
		} catch (Exception e) {
			log.error("*** SQL Error ****",e);
			throw new DAOException(this, "Error en la consulta. ReporteDAO - [findDetalleNotificaDirec]");
		} finally {	
		}
		return lista;			
	} //FIN - JVILLACORTA - 29/11/2011 - ALERTA DE SOLICITUDES
	
	
	//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
	/**
	   * Metodo encargado de obtener el codigo de unidad organica restando los ceros empezando desde el final hasta el caracter que no sea cero		 
	   * @param String unidad
	   * @return String uoJefe
	   * @throws SQLException
	   */
	  public String findUuooJefe(String unidad) throws DAOException {
    
	    String uoJefe = "";	
	    try {

	    	log.debug("findUuooJefe-unidadd: "+unidad); 
			uoJefe= unidad!=null? unidad.trim(): "";
        	if (!"".equals(uoJefe)){
        		log.debug("entro if");
        		int nroCar = uoJefe.length();
            	log.debug("nroCar: "+nroCar);
            	char v= '9';  //solo 1 caracter almacena              	
            	for (int p = nroCar-1; p >= 0; p--) {
            		log.debug("entro for");
            		log.debug("uoJefe: "+uoJefe);
            		log.debug("v: "+v);
            		log.debug("p: "+p);
            		v= uoJefe.charAt(p);
            		if (p!=0){
            			log.debug("entro p!=0");
                		if ('0'==v){
                			uoJefe=uoJefe.substring(0, p);
                			log.debug("uoJefe2: "+uoJefe);
                		}else{                			
                			break;
                		} 
            		}	
            	}            		
        	}
        	log.debug("findUuooJefe-uoJefee: "+uoJefe);
	    }
	    catch (Exception e) {
	    	log.error("*** SQL Error ****",e);
			MensajeBean msg = new MensajeBean();
			msg.setMensajeerror("Ha ocurrido un error inesperado: ".concat( e.getMessage() ));
			msg.setMensajesol("Por favor, intente nuevamente realizar la operacion, de continuar con el problema comuniquese con el webmaster");
	    }
	    finally {
	    }
	    log.debug("findUuooJefe-uoJefee(final): "+uoJefe);
		return uoJefe; 
	  }	  
	  //ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
	  
	  
	  //ICAPUNAY - PAS20171U230300074 - Mejoras vacaciones
	  /**
		 * Metodo encargado de buscar directivos notificados que no/si programaron vacaciones de trabajadores 
		 * de acuerdo a criterio de busqueda
		 * @param Map datos
		 * @return List 
		 * @throws DAOException
		 */	
		public ArrayList findNotificaDirectivosVacaciones(Map datos)
		throws DAOException {
					
			String criterio =	(datos.get("criterio") != null) ? (String)datos.get("criterio"): "";
			String valor =	(datos.get("valor") != null) ? (String)datos.get("valor"): "";		
			Map seguridad =	(datos.get("seguridad") != null) ? (HashMap)datos.get("seguridad"):new HashMap();
			String fechaNotific =	(datos.get("fechaNotific") != null) ? (String)datos.get("fechaNotific"): "";
			String fechaNotificFin =	(datos.get("fechaNotificFin") != null) ? (String)datos.get("fechaNotificFin"): "";
			String tipoNotif = (datos.get("tipoNotif") != null) ? (String)datos.get("tipoNotif"): "";
			String unidadAnterior =	(datos.get("unidadAnterior") != null) ? (String)datos.get("unidadAnterior"): "";
				
			if(log.isDebugEnabled())log.debug("findNotificaDirectivosVacaciones (datos): " + datos);			
					
			List objsJefe = new ArrayList();
			List resUOsJefe= new ArrayList();
			HashMap uoMap = new HashMap();
		    String codUoJef = "";
		    String codUoAnter = "";
			String codPersUsuario = seguridad!=null && !seguridad.isEmpty()? ((String) seguridad.get("codPers")).trim():"";
			
			StringBuffer strSQLFinal = new StringBuffer("");
			StringBuffer strSQL = new StringBuffer("");
			ArrayList lista = null;				
			
			if (constantes.leePropiedad("CRITERIO_COD").equals(criterio)) {	//registro			
				strSQL.append( "and nvd.cod_pers_jefe='").append(valor).append("' ");									
			}
			/*if (constantes.leePropiedad("CRITERIO_UUOO").equals(criterio) || constantes.leePropiedad("CRITERIO_INTD").equals(criterio)) { //uuoo o intendencia			
				strSQL.append( "and (u.t12cod_uorga like '").append(findUuooJefe(valor)).append("%' or u.t12cod_uorga like '").append(findUuooJefe(unidadAnterior)).append("%') ");				
			}*/	
			//ICAPUNAY - PAS20181U230300008 - Mejoras vacaciones
			if (constantes.leePropiedad("CRITERIO_UUOO").equals(criterio)) { //uuoo			
				strSQL.append( "and (u.t12cod_uorga= '").append(valor).append("' or u.t12cod_uorga= '").append(unidadAnterior).append("') ");				
			}
			if (constantes.leePropiedad("CRITERIO_INTD").equals(criterio)) { //intendencia				
				if (!unidadAnterior.equals("")){
					strSQL.append( "and (u.t12cod_uorga like '").append(findUuooJefe(valor)).append("%' or u.t12cod_uorga like '").append(findUuooJefe(unidadAnterior)).append("%') ");
				}else{
					strSQL.append( "and (u.t12cod_uorga like '").append(findUuooJefe(valor)).append("%') ");
				}								
			}
			//FIN
						
			//criterios de visibilidad
			if (seguridad != null && !seguridad.isEmpty()) {
				Map roles = (HashMap) seguridad.get("roles");		  	    
				String uoSeg = (String) seguridad.get("uoSeg");				
				String uoAO = (String) seguridad.get("uoAO");
		        
				if (roles.get(constantes.leePropiedad("ROL_ANALISTA_CENTRAL")) != null) {
					strSQL.append(" and 1=1 ");
				}				 
				else if (roles.get(constantes.leePropiedad("ROL_ANALISTA_OPERATIVO")) != null) {
					strSQL.append(" and ( (u.t12cod_uorga like '").append(uoSeg).append( "') ");
					strSQL.append(" or (u.t12cod_uorga in (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '").append(uoAO).append( "')) ) ");
				}
			
				else if (roles.get(constantes.leePropiedad("ROL_SECRETARIA")) != null
						|| roles.get(constantes.leePropiedad("ROL_JEFE")) != null) {					
					if (roles.get(constantes.leePropiedad("ROL_JEFE")) != null) {
						
						//si es jefe, debe visualizar informacion de colaboradores de todas las unidades donde es encargado o jefe (incluyendo subunidades)
						objsJefe.add(codPersUsuario.toUpperCase());
						objsJefe.add("1");    		  
						resUOsJefe=executeQuery(dataSource, findUOsByJefe.toString(), objsJefe.toArray());
						if(log.isDebugEnabled()) log.debug("resUOsJefe:"+ resUOsJefe);
						if (resUOsJefe.size()>0){    			   		      
							for (int i = 0;i<resUOsJefe.size();i++){ //0,1,2 (3 unidades)
								uoMap=(HashMap)resUOsJefe.get(i);
								codUoJef= uoMap.get("t12cod_uorga")!=null?uoMap.get("t12cod_uorga").toString().trim():"";
								codUoAnter= uoMap.get("t12cod_anter")!=null?uoMap.get("t12cod_anter").toString().trim():"";
								if (i==0){//0 (primer registro)
									strSQL = strSQL.append(" and (u.t12cod_uorga like '").append(findUuooJefe(codUoJef)).append("%' or u.t12cod_uorga like '").append(findUuooJefe(codUoAnter)).append("%' "); 
								}else{
									strSQL = strSQL.append(" or u.t12cod_uorga like '").append(findUuooJefe(codUoJef)).append("%' or u.t12cod_uorga like '").append(findUuooJefe(codUoAnter)).append("%' "); 
								}    		    	  
								if (i==resUOsJefe.size()-1){//2 (ultimo registro)
									strSQL = strSQL.append(") ");
								}
							}
						}
					}
					else { //secretaria
						strSQL.append(" and u.t12cod_uorga like '").append(uoSeg).append( "' ");
					}					
				} else {
					strSQL.append(" and u.t12cod_uorga like '").append(uoSeg).append( "' ") ;
				}
				
			}
			strSQL.append("order by coduo asc, regdirec asc, fecnotif asc ");
			
			Object o[] = null;				
								
						o = new Object[]{tipoNotif, //1= colaboradores con programacion, 2= colaboradores sin programacion
								new FechaBean(fechaNotific).getSQLDate(),
								new FechaBean(fechaNotificFin).getSQLDate()};									
				
			strSQLFinal.append(FIND_NOTIFICA_DIRECTIVOS_VACACIONES.toString()).append( strSQL.toString());
			if(log.isDebugEnabled())log.debug("strSQLFinal:" + strSQLFinal);
			lista = (ArrayList)executeQuery(dataSource, strSQLFinal.toString(), o);
			
			return lista;			
		}
		
		/**
		 * Metodo encargado de buscar empleados activos o inactivos de acuerdo a criterio
		 * de busqueda, regimen y de visibilidad por rol de usuario
		 * @param Map datos
		 * @return List 
		 * @throws DAOException
		 */		
		public List joinWithT02T12T99findPersonalByCriterioValorEstado(Map datos)
		throws DAOException {
			
			if(log.isDebugEnabled()) log.debug("ingreso joinWithT02T12T99findPersonalByCriterioValorEstado");					
			String regimen =	(datos.get("regimen") != null) ? (String)datos.get("regimen"): "";
			String criterio =	(datos.get("criterio") != null) ? (String)datos.get("criterio"): "";
			String valor =	(datos.get("valor") != null) ? ((String)datos.get("valor")).trim(): "";	
			String estado =	(datos.get("estado") != null) ? (String)datos.get("estado"): "";//1 o 0
			Map seguridad =		(datos.get("seguridad") != null) ? (HashMap)datos.get("seguridad"): new HashMap();		
			
			if(log.isDebugEnabled())log.debug("regimen:" + regimen);
			if(log.isDebugEnabled())log.debug("criterio:" + criterio);
			if(log.isDebugEnabled())log.debug("valor:" + valor);
			if(log.isDebugEnabled())log.debug("estado:" + estado);
			if(log.isDebugEnabled())log.debug("seguridad:" + seguridad);
			
			StringBuffer strSQLFinal = new StringBuffer("");
			StringBuffer strSQL = new StringBuffer("");
			List lista = null;	
			
			
			List objsJefe = new ArrayList();
			List resUOsJefe= new ArrayList();
			HashMap uoMap = new HashMap();
		    String codUoJef = "";
			String codPersUsuario = seguridad!=null && !seguridad.isEmpty()? ((String) seguridad.get("codPers")).trim():"";
			
			if (constantes.leePropiedad("CRITERIO_COD").equals(criterio)) {//registro			
				if (log.isDebugEnabled())log.debug("entro criterio COD-->");
				if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {//01
					strSQL .append( "where   p.t02cod_stat in (?) ")
					.append( "and p.t02cod_pers = '").append( valor.trim().toUpperCase()).append( "' ")
					.append( "and   p.t02cod_rel not in (?,?) ") //not in (09,10)
					.append( " and param.t99cod_tab = ? ")
					.append( " and param.t99tip_desc = ? ");
				} else if (constantes.leePropiedad("CODREL_REG1057").equals(regimen)){//09						
					strSQL .append( "where   p.t02cod_stat in (?) ") 	
					.append( "and p.t02cod_pers = '").append( valor.trim().toUpperCase()).append( "' ")				
					.append( "and   p.t02cod_rel = ? ") //= '09'
					.append( " and param.t99cod_tab = ? ")
					.append( " and param.t99tip_desc = ? ");
				  }
				else if (constantes.leePropiedad("CODREL_MOD_FORMATIVA").equals(regimen)){//10					
					strSQL .append( "where   p.t02cod_stat in (?) ") 	
					.append( "and p.t02cod_pers = '").append( valor.trim().toUpperCase()).append( "' ")				
					.append( "and   p.t02cod_rel = ? ") //= '10'
					.append( " and param.t99cod_tab = ? ")
					.append( " and param.t99tip_desc = ? ");
				}
			} 	
					
			if (constantes.leePropiedad("CRITERIO_UUOO").equals(criterio)) {//uuoo				  
				if (log.isDebugEnabled())log.debug("entro criterio UUOO-->");
				if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
					strSQL .append( "where  p.t02cod_stat in (?) ")			
					.append( "and   p.t02cod_rel not in (?,?) ") //not in (09,10)
					.append( " and param.t99cod_tab = ? ")
					.append( " and param.t99tip_desc = ? ");
				} else if (constantes.leePropiedad("CODREL_REG1057").equals(regimen)){
					strSQL .append( "where  p.t02cod_stat in (?) ")			
					.append( "and   p.t02cod_rel = ? ") //= '09'
					.append( " and param.t99cod_tab = ? ")
					.append( " and param.t99tip_desc = ? ");
				} else if (constantes.leePropiedad("CODREL_MOD_FORMATIVA").equals(regimen)){
					strSQL .append( "where  p.t02cod_stat in (?) ")				
					.append( "and   p.t02cod_rel = ? ") //= '10'
					.append( " and param.t99cod_tab = ? ")
					.append( " and param.t99tip_desc = ? ");
				}				
				strSQL.append( "and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6)= '")			  
				.append(valor.trim().toUpperCase()).append( "' ");
			}
			
			if (constantes.leePropiedad("CRITERIO_INTD").equals(criterio)) {//intendencia			
				String intendencia = valor.substring(0,2).trim();  
				if (log.isDebugEnabled())log.debug("codigo intendencia:" + intendencia);			
				if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
					strSQL .append( " where  p.t02cod_stat in (?) ")					
					.append( " and   p.t02cod_rel not in (?,?) ") //not in (09,10)
					.append( " and param.t99cod_tab = ? ")
					.append( " and param.t99tip_desc = ? ");
				} else if (constantes.leePropiedad("CODREL_REG1057").equals(regimen)){
					strSQL .append( "where  p.t02cod_stat in (?) ")								
					.append( " and p.t02cod_rel = ? ") //= '09'
					.append( " and param.t99cod_tab = ? ")
					.append( " and param.t99tip_desc = ? ");
				} else if (constantes.leePropiedad("CODREL_MOD_FORMATIVA").equals(regimen)){
					strSQL .append( "where  p.t02cod_stat in (?) ")								
					.append( " and p.t02cod_rel = ? ") //= '10'
					.append( " and param.t99cod_tab = ? ")
					.append( " and param.t99tip_desc = ? ");
				}
				strSQL.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) LIKE '")
				.append(intendencia.trim().toUpperCase()).append("%' ");
			}
			
			if (constantes.leePropiedad("CRITERIO_DEP").equals(criterio)) {//institucional			  
				  if(log.isDebugEnabled())log.debug("entro criterio DEP-->");
				  if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
					    strSQL .append( "where  p.t02cod_stat in (?) ")				   
					  	.append( "and   p.t02cod_rel not in (?,?) ") //not in (09,10)
						.append( " and param.t99cod_tab = ? ")
						.append( " and param.t99tip_desc = ? ");
					} else if (constantes.leePropiedad("CODREL_REG1057").equals(regimen)){
						strSQL .append( "where  p.t02cod_stat in (?) ")					   
						.append( "and   p.t02cod_rel = ? ") //= '09'
						.append( " and param.t99cod_tab = ? ")
						.append( " and param.t99tip_desc = ? ");
					} else if (constantes.leePropiedad("CODREL_MOD_FORMATIVA").equals(regimen)){
						strSQL .append( "where  p.t02cod_stat in (?) ")					
						.append( "and   p.t02cod_rel = ? ") //= '10'
						.append( " and param.t99cod_tab = ? ")
						.append( " and param.t99tip_desc = ? ");
					}
			}			
			strSQL.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = uo.t12cod_uorga ")
			.append( " and substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) = param.t99codigo ");
			
			//criterios de visibilidad
			if (seguridad != null && !seguridad.isEmpty()) {
				Map roles = (HashMap) seguridad.get("roles");		  	    
				String uoSeg = (String) seguridad.get("uoSeg");			
				String uoAO = ((String) seguridad.get("uoAO")).trim();
		        if (log.isDebugEnabled()) log.debug("roles: "+roles); 
		        if (log.isDebugEnabled()) log.debug("uoAO: "+uoAO); 
				if (roles.get(constantes.leePropiedad("ROL_ANALISTA_CENTRAL")) != null) {
					strSQL.append(" and 1=1 ");
				} 				
				else if (roles.get(constantes.leePropiedad("ROL_ANALISTA_OPERATIVO")) != null) {
					strSQL.append(" and ((substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "') ");
					strSQL.append(" or (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) in (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '").append(uoAO).append( "'))) ");
				}				
				else if (roles.get(constantes.leePropiedad("ROL_SECRETARIA")) != null
						|| roles.get(constantes.leePropiedad("ROL_JEFE")) != null) {
					
					if (roles.get(constantes.leePropiedad("ROL_JEFE")) != null) {
						
						//si es jefe, debe visualizar informacion de colaboradores de todas las unidades donde es encargado o jefe (incluyendo subunidades)
						objsJefe.add(codPersUsuario.toUpperCase());
						objsJefe.add("1");    		  
						resUOsJefe=executeQuery(dataSource, findUOsByJefe.toString(), objsJefe.toArray());
						if(log.isDebugEnabled()) log.debug("resUOsJefe:"+ resUOsJefe);
						if (resUOsJefe.size()>0){    			   		      
							for (int i = 0;i<resUOsJefe.size();i++){ //0,1,2 (3 unidades)
								uoMap=(HashMap)resUOsJefe.get(i);
								codUoJef= uoMap.get("t12cod_uorga").toString().trim();
								if (i==0){//0 (primer registro)
									strSQL = strSQL.append(" and (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(findUuooJefe(codUoJef)).append("%' "); 
								}else{
									strSQL = strSQL.append(" or substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(findUuooJefe(codUoJef)).append("%' "); 
								}    		    	  
								if (i==resUOsJefe.size()-1){//2 (ultimo registro)
									strSQL = strSQL.append(") ");
								}
							}
						}
					}
					else { //secretaria
						strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "' ");
					}				
					
				} else {
					strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "' ") ;
				}
				
			}
			strSQL.append(" order by coduo asc, t02cod_pers asc ");
			
			Object o[] = null;		

			if (constantes.leePropiedad("CRITERIO_COD").equals(criterio)) {
				if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
					o = new Object[]{
							constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),
							estado,						
							constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"), 
							constantes.leePropiedad("CODTAB_CATEGORIA"),
							constantes.leePropiedad("T99DETALLE")};
				} else if (constantes.leePropiedad("CODREL_REG1057").equals(regimen)){
					o = new Object[]{
							constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),
							estado,
							constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODTAB_CATEGORIA"),
							constantes.leePropiedad("T99DETALLE")};
				} else if (constantes.leePropiedad("CODREL_MOD_FORMATIVA").equals(regimen)){
					o = new Object[]{
							constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),
							estado,
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),
							constantes.leePropiedad("CODTAB_CATEGORIA"),
							constantes.leePropiedad("T99DETALLE")};
				}
				
			}
			
			if (constantes.leePropiedad("CRITERIO_UUOO").equals(criterio)) {
				if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
					o = new Object[]{
							constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),
							estado,						
							constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),
							constantes.leePropiedad("CODTAB_CATEGORIA"),
							constantes.leePropiedad("T99DETALLE")};
				} else if (constantes.leePropiedad("CODREL_REG1057").equals(regimen)){
					o = new Object[]{
							constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),
							estado,						
							constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODTAB_CATEGORIA"),
							constantes.leePropiedad("T99DETALLE")};
				} else if (constantes.leePropiedad("CODREL_MOD_FORMATIVA").equals(regimen)){
					o = new Object[]{
							constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),
							estado,						
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),
							constantes.leePropiedad("CODTAB_CATEGORIA"),
							constantes.leePropiedad("T99DETALLE")};
				}
				
			}
			
			if (constantes.leePropiedad("CRITERIO_INTD").equals(criterio)) {
				if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
					o = new Object[]{
							constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),
							estado,						
							constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"), 
							constantes.leePropiedad("CODTAB_CATEGORIA"),
							constantes.leePropiedad("T99DETALLE")};
				} else if (constantes.leePropiedad("CODREL_REG1057").equals(regimen)){
					o = new Object[]{
							constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),
							estado,						
							constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODTAB_CATEGORIA"),
							constantes.leePropiedad("T99DETALLE")};
				} else if (constantes.leePropiedad("CODREL_MOD_FORMATIVA").equals(regimen)){
					o = new Object[]{
							constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),
							estado,						
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),
							constantes.leePropiedad("CODTAB_CATEGORIA"),
							constantes.leePropiedad("T99DETALLE")};
				}
				
			}
			
			if (constantes.leePropiedad("CRITERIO_DEP").equals(criterio)) {
				if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
					o = new Object[]{
							constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),
							estado,						
							constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"), 
							constantes.leePropiedad("CODTAB_CATEGORIA"),
							constantes.leePropiedad("T99DETALLE")};
				} else if (constantes.leePropiedad("CODREL_REG1057").equals(regimen)){
					o = new Object[]{
							constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),
							estado,						
							constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODTAB_CATEGORIA"),
							constantes.leePropiedad("T99DETALLE")};
				} else if (constantes.leePropiedad("CODREL_MOD_FORMATIVA").equals(regimen)){
					o = new Object[]{
							constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),
							estado,					
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),
							constantes.leePropiedad("CODTAB_CATEGORIA"),
							constantes.leePropiedad("T99DETALLE")};
				}
				
			}					
			strSQLFinal.append(FIND_PERSONAL_BY_CRITERIO_VALOR_ESTADO.toString()).append( strSQL.toString());
			if(log.isDebugEnabled())log.debug("strSQLFinal:" + strSQLFinal);
			lista = executeQuery(dataSource, strSQLFinal.toString(), o);
			
			return lista;			
		}		
	//FIN ICAPUNAY - PAS20171U230300074 - Mejoras vacaciones	
}