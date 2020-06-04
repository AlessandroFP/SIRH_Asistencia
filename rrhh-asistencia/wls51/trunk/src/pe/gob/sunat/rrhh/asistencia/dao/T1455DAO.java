package pe.gob.sunat.rrhh.asistencia.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.Propiedades;
import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.utils.Utiles;

/** 
 * 
 * Clase       : T1455DAO 
 * Descripcion : clase encargada de administrar los datos de la tabla t1274licen_med 
 * Proyecto    : ASISTENCIA 
 * Autor       : PRAC-JCALLO
 * Fecha       : 12-MAR-2007 
 * 
 * */

public class T1455DAO extends DAOAbstract {
	
	private DataSource dataSource = null;
	
	Propiedades constantes = new Propiedades(getClass(), "/constantes.properties");
	
	private final StringBuffer findAllColumnsByKey = new StringBuffer("SELECT anno, numero, u_organ, cod_pers, num_seguim, fecha_recep, fecha_deriv, nvl(accion_id,'') as  accion_id, ")
       .append(" nvl(estado_id,'') as estado_id, nvl(cuser_orig,'') as cuser_orig, nvl(cuser_dest,'') as cuser_dest, nvl(tobserv,'') as tobserv, ")
       .append(" fcreacion, nvl(cuser_crea,'') as cuser_crea, fmod, nvl(cuser_mod,'') as cuser_mod ")
       .append(" FROM t1455sol_seg WHERE anno = ? and numero = ? and u_organ = ? and cod_pers = ? and num_seguim = ?");
	
	private final StringBuffer insertarSeguimientoSol =  new StringBuffer("INSERT INTO t1455sol_seg ( anno, numero, u_organ, cod_pers, num_seguim, fecha_recep, fecha_deriv, accion_id, ")
       .append(" estado_id, cuser_orig, cuser_dest, tobserv, fcreacion, cuser_crea) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
	
	private final StringBuffer findSigSeg = new StringBuffer("SELECT max(num_seguim)+1 total from t1455sol_seg where cod_pers = ? and anno = ? and numero = ?");
	
	private final StringBuffer ExisteOrigenIgualDestino = new StringBuffer("select  first 1* from t1455sol_seg where cuser_orig=cuser_dest and cuser_orig=? and estado_id=? and anno=? and numero=? order by num_seguim desc"); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
	
	//JRR
	private final StringBuffer findRespSolicitudesPendientesByffinicio = new StringBuffer("SELECT unique ss.cuser_dest ")
		.append(" FROM t1277solicitud s, t1455sol_seg ss ")
		.append(" WHERE s.cod_pers = ss.cod_pers and s.anno = ss.anno and s.numero = ss.numero and s.seguim_act = ss.num_seguim ")
		.append(" and s.ffinicio >= ? and s.ffinicio <= ? and ss.estado_id = ? ")
		//JRR - 17/09/2009
		.append(" and ss.cuser_dest is not null ");
		//.append(" and s.anno = ? and s.ffinicio >= ? and s.ffinicio <= ? and ss.estado_id = ? ");
	
	//JRR
	private final StringBuffer findTrabSolicitudesPendientesByffinicioAndResp = new StringBuffer("SELECT s.numero, s.cod_pers, ss.fecha_recep as fmod")
		.append(" FROM t1277solicitud s, t1455sol_seg ss ")
		.append(" WHERE s.cod_pers = ss.cod_pers and s.anno = ss.anno and s.numero = ss.numero and s.seguim_act = ss.num_seguim ")		
		.append(" and s.ffinicio >= ? and s.ffinicio <= ? and ss.cuser_dest = ? and ss.estado_id = ? ");    
		//.append(" and s.anno = ? and s.ffinicio >= ? and s.ffinicio <= ? and ss.cuser_dest = ? and ss.estado_id = ? ");    
	
	//JVILLACORTA - 03/11/2011 - MODIFICA ALERTA DE SOLICITUDES
	private final StringBuffer findRespSolicitudesPendientes = new StringBuffer("SELECT unique ss.cuser_dest ")
	.append(" FROM t1277solicitud s, t1455sol_seg ss ")
	.append(" WHERE s.cod_pers = ss.cod_pers and s.anno = ss.anno and s.numero = ss.numero and s.seguim_act = ss.num_seguim ")
	.append(" and ss.estado_id = ? ")		
	.append(" and ss.cuser_dest is not null ");
	
	//DTARAZONA - CONSULTA DE DIRECTIVOS QUE TIENEN SOLICITUDES DE VACACIONES PENDIENTES DE APROBAR - 2018-01-22
		private final StringBuffer BUSCAR_DIRECTIVOS_SOLICITUD_VACACIONES= new StringBuffer("SELECT unique ss.cuser_dest, p.t02ap_pate,p.t02ap_mate,p.t02nombres")
		.append(" FROM t1277solicitud s, t1455sol_seg ss, t02perdp p ")
		.append(" WHERE s.cod_pers = ss.cod_pers AND ss.cuser_dest=p.t02cod_pers AND s.anno = ss.anno AND s.numero = ss.numero AND s.seguim_act = ss.num_seguim") 
		.append(" AND ss.estado_id = ? AND ss.cuser_dest IS NOT NULL AND s.licencia=?");
		
		private final StringBuffer BUSCAR_SOLICITUDES_PENDIENTES=new StringBuffer("SELECT s.anno, s.numero, ss.u_organ, s.cod_pers, p.t02ap_pate,p.t02ap_mate,p.t02nombres, ss.num_seguim, t.mov, t.descrip,")
		.append("ss.cuser_dest, to_char(s.fecha,'%d/%m/%Y') as fecha1,to_char(s.ffinicio,'%d/%m/%Y') as fec_inicio, to_char(s.ffin,'%d/%m/%Y') as fec_fin,s.fecha")
		.append(" FROM t1277solicitud s, t1455sol_seg ss, t1279Tipo_mov t, t02perdp p ")
		.append(" WHERE s.cod_pers = ss.cod_pers AND s.cod_pers=p.t02cod_pers AND s.anno = ss.anno AND s.numero = ss.numero AND s.seguim_act = ss.num_seguim AND ss.estado_id = ?")
		.append(" AND ss.cuser_dest = ? AND s.licencia=t.mov AND s.licencia=? ORDER BY fecha, s.numero");
				
		//FIN DTARAZONA 
		
	//private final StringBuffer findTrabSolicitudesPendientes = new StringBuffer(" SELECT s.anno, s.numero, ss.u_organ, s.cod_pers, ss.num_seguim, t.mov, t.descrip, ss.cuser_dest, s.fecha ") //ICAPUNAY 08/08 DEBER SER ss.u_organ
	private final StringBuffer findTrabSolicitudesPendientes = new StringBuffer(" SELECT s.anno, s.numero, ss.u_organ, s.cod_pers, ss.num_seguim, t.mov, t.descrip, ss.cuser_dest, to_char(s.fecha,'%d/%m/%Y') as fecha ") 
	.append(" FROM t1277solicitud s, t1455sol_seg ss, t1279Tipo_mov t ")
	.append(" WHERE s.cod_pers = ss.cod_pers and s.anno = ss.anno and s.numero = ss.numero and s.seguim_act = ss.num_seguim ")
	.append(" and ss.cuser_dest = ? and ss.estado_id = ? and s.licencia=t.mov and s.fecha < ? ") //JVILLACORTA - 03/11/2011 - MODIFICA ALERTA DE SOLICITUDES		
    .append(" order by fecha, s.numero ");
	//.append(" order by s.fecha, s.numero "); 
	//FIN - JVILLACORTA - 03/11/2011 - MODIFICA ALERTA DE SOLICITUDES
		
	private final String findSolicitudesByCodpersAndLicencia = 	//*****jquispecoi solicitudes no rechazadas 
		"SELECT "+ 
		"    s.anno, "+ 
		"    s.numero,  "+ 
		"    ss.accion_id, "+ 
		"    ss.estado_id "+
		"FROM  "+
		"    t1277solicitud s, "+ 
		"    t1455sol_seg ss  "+
		"WHERE  "+
		"    s.cod_pers = ss.cod_pers "+ 
		"    and s.anno = ss.anno  "+
		"    and s.numero = ss.numero "+ 
		"    and s.seguim_act = ss.num_seguim "+ 
		"    and s.licencia = ?  "+
		"    and ss.accion_id != 3  "+
		"    and s.cod_pers = ? ";
	
	
	/**
	 *@param datasource Object
	 *
	 * */
	public T1455DAO(Object datasource) {
		if (datasource instanceof DataSource)
	      this.dataSource = (DataSource)datasource;
	    else if (datasource instanceof String)
	      this.dataSource = getDataSource((String)datasource);
	    else
	      throw new DAOException(this, "Datasource no valido");
	}
	
	/**
	  * Metodo que obtiene los datos a partir de la llave 
	  * @param datos (String anno, Short numero, String u_organ, String cod_pers,String num_seguim)
	  * @return
	  * @throws DAOException
	  */
	public Map findAllColumnsByKey(Map datos) throws DAOException {
		
		Map soli = executeQueryUniqueResult(dataSource, findAllColumnsByKey.toString(), new Object[]{ 
					datos.get("anno"), datos.get("numero"),
					datos.get("u_organ"), datos.get("cod_pers"),
					datos.get("num_seguim") } );
		
		return soli;
	}
	
	/**
	 * Metodo encargado de insertar un registro en la tabla t1274licen_med
	 * @param params ( anno, numero, u_organ, cod_pers, num_seguim, fecha_recep, fecha_deriv, accion_id, estado_id, cuser_orig, cuser_dest, tobserv, fcreacion, cuser_crea)
	 * @throws DAOException
	 */
	public boolean insertarSeguimientoSol(Map params) throws DAOException {
		//anno, numero, u_organ, cod_pers, num_seguim, fecha_recep, fecha_deriv, accion_id, ")
	    //estado_id, cuser_orig, cuser_dest, tobserv, fcreacion, cuser_crea
		int modificado = executeUpdate(dataSource, insertarSeguimientoSol.toString(), new Object[]{params.get("anno"), 
			params.get("numero"), params.get("u_organ") , params.get("cod_pers"), params.get("num_seguim"),params.get("fecha_recep"),
			params.get("fecha_deriv"), params.get("accion_id") , params.get("estado_id"), params.get("cuser_orig"), params.get("cuser_dest"), 
			params.get("tobserv"), params.get("fcreacion"), params.get("cuser_crea")});
		log.debug("modificado "+modificado);
		return (modificado>0);
	}
	
	/**
	 * Metodo encargado de actualizar solo las campos especificados de un registro
	 * @param params contiene (Map columns, String anno, Short numero, String u_organ, String cod_pers, String num_seguim)
	 * @throws DAOException
	 */
	public boolean updateCustomColumns(Map params) throws DAOException {
		
		StringBuffer strSQL = new StringBuffer("UPDATE t1455sol_seg ");
		List listaVal = new ArrayList();
		Map columns = (params.get("columns") != null) ? (HashMap)params.get("columns"): new HashMap();
		
		if(columns != null && !columns.isEmpty()) {
			Iterator it = columns.entrySet().iterator();
			boolean first = true;//para ver si es el primer campo de la sentencia SQL
			while (it.hasNext()) {
				Map.Entry e = (Map.Entry)it.next();
				if (first) {
					strSQL.append(" set "+e.getKey()+"= ? ");
					listaVal.add(e.getValue());
					first = false;
				} else {
					strSQL.append(", "+e.getKey()+"= ? ");
					listaVal.add(e.getValue());
				}				
			}
		}
		
		//JRR - 17/10/2008
		if (params.get("cuser_dest")!=null) {
			strSQL.append("  Where anno = ? and numero = ? and cod_pers = ? and cuser_dest = ? ");
			if(log.isDebugEnabled()) log.debug("SQL 1: "+strSQL.toString());
			listaVal.add(params.get("anno"));
			listaVal.add(params.get("numero"));
			listaVal.add(params.get("cod_pers"));
			listaVal.add(params.get("cuser_dest"));
			
		}
		//
		else {
			strSQL.append("  WHERE anno = ? and numero = ? and u_organ = ? and cod_pers = ? and num_seguim = ?");
			if(log.isDebugEnabled()) log.debug("SQL 2: "+strSQL.toString()); 
			//los datos de la llave primaria
			listaVal.add(params.get("anno"));
			listaVal.add(params.get("numero"));
			listaVal.add(params.get("u_organ"));
			listaVal.add(params.get("cod_pers"));
			listaVal.add(params.get("num_seguim"));
		}
		
		int modificado = executeUpdate(dataSource, strSQL.toString(), listaVal.toArray());
		
		return (modificado>0);
	}
	
	/**
	 * Metodo encargado de buscar el codigo del siguiente seguimiento de una
	 * solicitud.
	 * @param params(String anno, Short numero,	String codPers)
	 * @throws DAOException
	 */
	public String findSigSeg(Map params) throws DAOException {

		Map msegui = executeQueryUniqueResult(dataSource, findSigSeg.toString(), new Object[]{params.get("cod_pers"),
			params.get("anno"), params.get("numero")});
		
		String numSiguiente = ""; 
		
		if(msegui != null && !msegui.isEmpty()){
			numSiguiente = ( msegui.get("total")!=null ) ? msegui.get("total").toString():"0"; 
		}else{
			numSiguiente = "0";
		}
			
		return numSiguiente;
	}

	//ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
	/**
	 * Metodo encargado de determinar si un usuario existe en un seguimiento de la solicitud como origen y destino 
	 * solicitud.
	 * @param params(String cuser_orig, String estado_id, String anno, Short numero)
	 * @throws DAOException
	 */
	public boolean ExisteOrigenIgualDestino(Map params) throws DAOException {

		boolean existe=false;
		Map msegui = executeQueryUniqueResult(dataSource, ExisteOrigenIgualDestino.toString(), new Object[]{params.get("cuser_orig"),params.get("estado_id"),
			params.get("anno"), params.get("numero")});
		
		if(msegui != null && !msegui.isEmpty()){
			existe=true;
		}			
		return existe;
	}
	//ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
	
	//JRR
	/**
	 * Metodo encargado de buscar los responsables que tienen solicitudes pendientes 
	 * @param params Map(String periodo)
	 * @throws DAOException
	 */
	public List findRespSolicitudesPendientesByffinicio(Map params) throws DAOException {
		
		List resultado = new ArrayList();
//		String cod_anho = params.get("periodo").toString().substring(0,4);
//		String cod_mes = params.get("periodo").toString().substring(4,6);
		String fechaIni = params.get("finicio").toString().trim(); //"01/" + cod_mes + "/" + cod_anho;
		String fechaFin = params.get("ffin").toString().trim();
		Timestamp tsfechaIni = Utiles.stringToTimestamp(fechaIni + " 00:00:00");
		Timestamp tsfechaFin = Utiles.stringToTimestamp(fechaFin + " 00:00:00");
		
		resultado = executeQuery(dataSource, findRespSolicitudesPendientesByffinicio.toString(),
				new Object[]{tsfechaIni, tsfechaFin, constantes.leePropiedad("ESTADO_SEGUIMIENTO")});
		
		return resultado;
	}
		
	/**
	 * Metodo encargado de buscar las solicitudes pendientes por responsable 
	 * @param params Map(String periodo, String cuser_dest)
	 * @throws DAOException
	 */
	public List findTrabSolicitudesPendientesByffinicioAndResp(Map params, Map resp) throws DAOException {
		
		List resultado = new ArrayList();	
//		String cod_anho = params.get("periodo").toString().substring(0,4);
//		String cod_mes = params.get("periodo").toString().substring(4,6);
		String fechaIni = params.get("finicio").toString().trim(); //"01/" + cod_mes + "/" + cod_anho;
		String fechaFin = params.get("ffin").toString().trim();
		Timestamp tsfechaIni = Utiles.stringToTimestamp(fechaIni + " 00:00:00");
		Timestamp tsfechaFin = Utiles.stringToTimestamp(fechaFin + " 00:00:00");
	
		resultado = executeQuery(dataSource, findTrabSolicitudesPendientesByffinicioAndResp.toString(),
				new Object[]{tsfechaIni, tsfechaFin, resp.get("cuser_dest").toString().trim(),
							constantes.leePropiedad("ESTADO_SEGUIMIENTO")});				
		
		return resultado;
	}
	
	//JVILLACORTA - 03/11/2011 - MODIFICA ALERTA DE SOLICITUDES
	/**
	 * Metodo encargado de buscar los jefes y directivos que tienen solicitudes pendientes 
	 * @throws DAOException
	 */
	public List findRespSolicitudesPendientes() throws DAOException {
		
		List resultado = new ArrayList();		
		
		resultado = executeQuery(dataSource, findRespSolicitudesPendientes.toString(),
				new Object[]{constantes.leePropiedad("ESTADO_SEGUIMIENTO")});
		
		return resultado;
	} //FIN - JVILLACORTA - 03/11/2011 - MODIFICA ALERTA DE SOLICITUDES
	
	//JVILLACORTA - 03/11/2011 - MODIFICA ALERTA DE SOLICITUDES	
	/**
	 * Metodo encargado de buscar las solicitudes pendientes por jefe o directivo(responsable) 
	 * @param params Map(String periodo, String cuser_dest)
	 * @throws DAOException
	 */
	public List findTrabSolicitudesPendientes(Map resp) throws DAOException {
		
		List resultado = new ArrayList();
		
		if (log.isDebugEnabled()) log.debug("hoy: " + resp.get("hoy"));
		Date fhoy = new Date();
		fhoy = new FechaBean((String)resp.get("hoy")).getSQLDate();
		if (log.isDebugEnabled()) log.debug("fhoy: " + fhoy);
	
		resultado = executeQuery(dataSource, findTrabSolicitudesPendientes.toString(),
				new Object[]{resp.get("cuser_dest").toString().trim(),
							constantes.leePropiedad("ESTADO_SEGUIMIENTO"), fhoy});				
		
		return resultado;
	} //FIN - JVILLACORTA - 03/11/2011 - MODIFICA ALERTA DE SOLICITUDES	
	
	
	//DTARAZONA - 23/01/2018
	
		public List buscarDirectivosSolicitudVacacionesPendientes() throws DAOException{
			List directivos=new ArrayList();
			try{
				if(log.isDebugEnabled())  log.debug("T1277DAO buscarDirectivosSolicitudVacaciones");
				directivos=executeQuery(dataSource, BUSCAR_DIRECTIVOS_SOLICITUD_VACACIONES.toString(),new Object[]{constantes.leePropiedad("ESTADO_SEGUIMIENTO"),constantes.leePropiedad("VACACION")});
			}catch(Exception e){
				log.error("*** Error en buscarDirectivosSolicitudVacaciones: ",e);
			}
			return directivos;
		}
		
		public List buscarSolicitudesPendientesAprobar(Map params) throws DAOException{
			List solicitudesPendientes=new ArrayList();
			try{
				if(log.isDebugEnabled())  log.debug("T1277DAO buscarSolicitPA: "+params.get("cod_dir"));
				solicitudesPendientes=executeQuery(dataSource, BUSCAR_SOLICITUDES_PENDIENTES.toString(),new Object[]{constantes.leePropiedad("ESTADO_SEGUIMIENTO"),params.get("cod_dir"),constantes.leePropiedad("VACACION")});
			}catch(Exception e){
				log.error("*** Error en buscarSolicitudesPendientesAprobar: ",e);
			}
			
			return solicitudesPendientes;
		}
		//FIN DTARAZONA
	
	/**
	 * Buscar solicitudes no rechazadas, de un determinado tipo
	 * pertenecientes a un colaborador, devuelve una lista   de
	 * solicitudes. 
	 * @param params Par�metros, licencia y c�digo de empleado
	 * @return Lista de solicitudes 
	 * @throws DAOException
	 * @author jquispecoi
	 */
	public List findSolicitudesByCodpersAndLicencia(Map params) throws DAOException {
		if(log.isDebugEnabled()) log.debug("method: findSolicitudesByCodpersAndLicencia");
		List resultado = new ArrayList();	
	
		resultado = executeQuery(dataSource, findSolicitudesByCodpersAndLicencia,
				new Object[]{params.get("licencia"), params.get("codPers")});				
		
		return resultado;
	}
	
}
