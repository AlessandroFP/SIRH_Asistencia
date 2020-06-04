package pe.gob.sunat.rrhh.asistencia.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.Propiedades;

/** 
 * 
 * Clase       : T4502DAO 
 * Descripcion : Clase encargada de administrar los datos de la tabla t4502NotificaVac
 * Proyecto    : ASISTENCIA 
 * Autor       : ICAPUNAY
 * Fecha       : 22-FEBRERO-2011 
 * 
 * */

public class T4502DAO extends DAOAbstract{
	
	private DataSource datasource;
	
	Propiedades constantes = new Propiedades(getClass(), "/constantes.properties");
	
	
	private final StringBuffer INSERTAR_NOTIFICACION_BY_VACACION = new 	StringBuffer("INSERT INTO t4502NotificaVac "
			).append("(num_notificacion, cod_pers, num_periodo, cod_licencia, fec_ini_vacacion, ind_envio_notific, fec_envio_notific, cod_usucreac, fec_creac) "
			).append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ");

	
	private final StringBuffer FIND_NOTIFICACIONES_ENVIADAS_BY_FECHA_ENVIO = new  StringBuffer("select uo.t12cod_uorga as cod_uorg,n.cod_pers as cod_pers, "
			).append("(trim(p.t02ap_pate)|| ' ' || trim(p.t02ap_mate)|| ' ' || trim(p.t02nombres)) as nombre, "	
			).append("DATE(n.fec_ini_vacacion) as fec_ini_vacacion "
			).append("from t4502NotificaVac n, t02perdp p, t12uorga uo where n.cod_pers=p.t02cod_pers "	
			).append("and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = uo.t12cod_uorga "
			).append("and n.ind_envio_notific=? and DATE(n.fec_envio_notific)=? "			
			).append("order by cod_uorg asc, n.cod_pers asc ");		
	
	private final StringBuffer FIND_ALL_NOTIFICACIONES = new  StringBuffer("select first 1 * "
	).append("from t4502NotificaVac ");	
		
	private final StringBuffer FIND_NOTIFICACION_ENVIADA_NOREGISTRADA = new  StringBuffer("select *  from t4502NotificaVac "
	).append("where cod_pers=? and DATE(fec_ini_vacacion)=? "	
	).append("and ind_envio_notific=? and ind_regis_solicit=? ");
	
	private final StringBuffer FIND_ULTIMA_NOTIFICACION = new  StringBuffer("select first 1 * from t4502NotificaVac "	
	).append("order by num_notificacion desc ");

	private final StringBuffer UPDATE_REGISTRO_SOLICITUD = new 	StringBuffer("UPDATE t4502NotificaVac "
			).append("SET ind_regis_solicit = ?, "
			).append("    cod_usumodif = ?, "
			).append("    fec_modif = ? "			
			).append("WHERE num_notificacion = ? ");
	
	
	private final StringBuffer UPDATE_APROBACION_SOLICITUD = new  StringBuffer("UPDATE t4502NotificaVac "
			).append("SET ind_aprob_solicit = ?, "
			).append("    cod_usumodif = ?, "
			).append("    fec_modif = ?, "			
			).append("WHERE num_notificacion = ? ");
			
	private final StringBuffer UPDATE_IND_LECTURA_PERS = new  StringBuffer("UPDATE t4502NotificaVac "
			).append("SET ind_lectu_pers = ?, "
			).append("    cod_usumodif = ?, "
			).append("    fec_modif = ?, "			
			).append("WHERE num_notificacion = ? ");	

	private final StringBuffer UPDATE_IND_LECTURA_JEFE = new  StringBuffer("UPDATE t4502NotificaVac "
			).append("SET ind_lectu_jefe = ?, "
			).append("    cod_usumodif = ?, "
			).append("    fec_modif = ?, "			
			).append("WHERE num_notificacion = ? ");				
			
	private final StringBuffer DELETE_NOTIFICACION_BY_NUM_NOTIF = new  StringBuffer("DELETE FROM t4502NotificaVac "	
			).append("WHERE num_notificacion = ? ");
			

	
	/**
	* 
	* Este constructor del DAO dicierne como crear el datasource
	* dependiendo del tipo de objeto que recibe. Esto nos ayuda a
	* mejorar la invocacion del dao.
	* 
	* @param datasource Object
	*/
	public T4502DAO(Object datasource) {
		if (datasource instanceof DataSource)
		  this.datasource = (DataSource)datasource;
		else if (datasource instanceof String)
		  this.datasource = getDataSource((String)datasource);
		else
		  throw new DAOException(this, "Datasource no valido");
	}
	
	/**
	 * Metodo que se encarga de registrar la notificacion de correo por vacaciones enviada a un trabajador
	 * @param params Map (num_notificacion, cod_pers, num_periodo, cod_licencia, fec_ini_vacacion, ind_envio_notific, fec_envio_notific, cod_usucreac, fec_creac)  
	 * @return boolean	 
	 * @throws DAOException
	 */	
	public boolean insertNotificacionByVacaciones(Map params) throws DAOException {
	
		int insertado=0;
		if (log.isDebugEnabled()) log.debug("T4502DAO - insertNotificacionByVacaciones - params " + params);		
		try{
			insertado = executeUpdate(datasource, INSERTAR_NOTIFICACION_BY_VACACION.toString(), new Object[]{params.get("num_notificacion"), 
				params.get("cod_pers"), params.get("num_periodo") , params.get("cod_licencia"), params.get("fec_ini_vacacion"),constantes.leePropiedad("ACTIVO"),
				params.get("fec_envio_notific"), params.get("cod_usucreac") , params.get("fec_creac")});
		} catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [INSERTAR_NOTIFICACION_BY_VACACION]");
		}
		
		return (insertado>0);
	}
	
	/**
	  * Metodo que obtiene las notificaciones de vacaciones enviadas a trabajadores según fecha de envio
	  * @param fechaEnvio Date
	  * @return List
	  * @throws DAOException
	  */
	public List findNotificacionesEnviadasByFechaEnvio(Date fechaEnvio) throws DAOException {
		
		List notificaciones= null;
		if (log.isDebugEnabled()) log.debug("T4502DAO - findNotificacionesEnviadasByFechaEnvio - fechaEnvio " + fechaEnvio);
		try{
			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			notificaciones = executeQuery(datasource, FIND_NOTIFICACIONES_ENVIADAS_BY_FECHA_ENVIO.toString(), new Object[]{constantes.leePropiedad("ACTIVO"),fechaEnvio} );
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [FIND_NOTIFICACIONES_ENVIADAS_BY_FECHA_ENVIO]");
		}	
		
		return notificaciones;
	}
	
	/**
	  * Metodo que obtiene todas las notificaciones enviadas	
	  * @return List
	  * @throws DAOException
	  */
	public List findAllNotificacionesEnviadas() throws DAOException {
		
		List notificaciones= null;
		if (log.isDebugEnabled()) log.debug("T4502DAO - findAllNotificacionesEnviadas");
		try{
			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			notificaciones = executeQuery(datasource, FIND_ALL_NOTIFICACIONES.toString());
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [FIND_ALL_NOTIFICACIONES]");
		}	
		
		return notificaciones;
	}
	
	/**
	  * Metodo que devuelve una notificacion no registrada por el trabajador según una fecha de inicio de vacacion
	  * @param mapa Map	 
	  * @return Map
	  * @throws DAOException
	  */
	public Map findNotificacionEnviadaPeroNoRegistrada(Map mapa) throws DAOException {
		
		
		List listaVal = new ArrayList();
		listaVal.add(mapa.get("userOrig"));
		listaVal.add(mapa.get("fec_ini_vacacion"));
		listaVal.add(constantes.leePropiedad("ACTIVO"));
		listaVal.add(constantes.leePropiedad("INACTIVO"));
		
		Map notificacion= null;
		List notificaciones= null;
		if (log.isDebugEnabled()) log.debug("T4502DAO - findNotificacionEnviadaPeroNoRegistrada - mapa " + mapa);
		try{
			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);			
			notificaciones = executeQuery(datasource, FIND_NOTIFICACION_ENVIADA_NOREGISTRADA.toString(), listaVal.toArray() );
			if(notificaciones!=null && !notificaciones.isEmpty()){
				notificacion=(Map)notificaciones.get(0);
			}
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [FIND_NOTIFICACION_ENVIADA_NOREGISTRADA]");
		}	
		
		return notificacion;
	}
	
	/**
	  * Metodo que devuelve la ultima notificacion registrada	
	  * @return Map
	  * @throws DAOException
	  */
	public Map findUltimaNotificacionRegistrada() throws DAOException {
		
		
		Map notificacion= null;
		if (log.isDebugEnabled()) log.debug("T4502DAO - findUltimaNotificacionRegistrada");
		try{
			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);			
			notificacion = executeQueryUniqueResult(datasource, FIND_ULTIMA_NOTIFICACION.toString());
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [FIND_ULTIMA_NOTIFICACION]");
		}	
		
		return notificacion;
	}
	
	/**
	 * Metodo que se encarga de actualizar el indicador de registro de solicitud por el trabajador
	 * @param params Map (cod_usumodif, fec_modif, num_notificacion)
	 * @return boolean
	 * @throws DAOException
	 */
	
	public boolean updateRegistroSolicitudByPK(Map params) throws DAOException {
		
		List listaVal = new ArrayList();
		listaVal.add(constantes.leePropiedad("ACTIVO"));
		listaVal.add(params.get("cod_usumodif"));
		listaVal.add(params.get("fec_modif"));
		listaVal.add(params.get("num_notificacion"));
		int actualizaRegSol=0;
		
		if (log.isDebugEnabled()) log.debug("T4502DAO - updateRegistroSolicitudByPK - params " + params);
		try{
		
			actualizaRegSol=executeUpdate(datasource,UPDATE_REGISTRO_SOLICITUD.toString(), listaVal.toArray());
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [UPDATE_REGISTRO_SOLICITUD]");
		}		
		
		return (actualizaRegSol>0);	
		
	}
	
	/**
	 * Metodo que se encarga de actualizar el indicador de aprobacion de solicitud por el jefe
	 * @param params Map (cod_usumodif, fec_modif, num_notificacion)
	 * @return boolean
	 * @throws DAOException
	 */
	
	public boolean updateAprobacionSolicitudByPK(Map params) throws DAOException {
		
		List listaVal = new ArrayList();
		listaVal.add(constantes.leePropiedad("ACTIVO"));
		listaVal.add(params.get("cod_usumodif"));
		listaVal.add(params.get("fec_modif"));
		listaVal.add(params.get("num_notificacion"));
		int actualizaAprobSol=0;
		
		if (log.isDebugEnabled()) log.debug("T4502DAO - updateAprobacionSolicitudByPK - params " + params);
		try{
		
			actualizaAprobSol=executeUpdate(datasource,UPDATE_APROBACION_SOLICITUD.toString(), listaVal.toArray());
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [UPDATE_APROBACION_SOLICITUD]");
		}		
		
		return (actualizaAprobSol>0);	
		
	}
	
	/**
	 * Metodo que se encarga de actualizar el indicador de lectura de la notificacion por el trabajador
	 * @param params Map (cod_usumodif, fec_modif, num_notificacion)
	 * @return boolean
	 * @throws DAOException
	 */
	
	public boolean updateLecturaNotificacionByTrabajador(Map params) throws DAOException {
		
		List listaVal = new ArrayList();
		listaVal.add(constantes.leePropiedad("ACTIVO"));
		listaVal.add(params.get("cod_usumodif"));
		listaVal.add(params.get("fec_modif"));
		listaVal.add(params.get("num_notificacion"));
		int actualizaLectByTrab=0;
		
		if (log.isDebugEnabled()) log.debug("T4502DAO - updateLecturaNotificacionByTrabajador - params " + params);
		try{
		
			actualizaLectByTrab=executeUpdate(datasource,UPDATE_IND_LECTURA_PERS.toString(), listaVal.toArray());
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [UPDATE_IND_LECTURA_PERS]");
		}		
		
		return (actualizaLectByTrab>0);	
		
	}
	
	/**
	 * Metodo que se encarga de actualizar el indicador de lectura de la notificacion por el jefe
	 * @param params Map (cod_usumodif, fec_modif, num_notificacion)
	 * @return boolean
	 * @throws DAOException
	 */
	
	public boolean updateLecturaNotificacionByJefe(Map params) throws DAOException {
		
		List listaVal = new ArrayList();
		listaVal.add(constantes.leePropiedad("ACTIVO"));
		listaVal.add(params.get("cod_usumodif"));
		listaVal.add(params.get("fec_modif"));
		listaVal.add(params.get("num_notificacion"));		
		int actualizaLectByJefe=0;
		
		if (log.isDebugEnabled()) log.debug("T4502DAO - updateLecturaNotificacionByJefe - params " + params);
		try{
		
			actualizaLectByJefe=executeUpdate(datasource,UPDATE_IND_LECTURA_JEFE.toString(), listaVal.toArray());
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [UPDATE_IND_LECTURA_JEFE]");
		}		
		
		return (actualizaLectByJefe>0);	
		
	}
	
	/**
	 * Metodo encargado de eliminar(fisico) una notificación a partir de la llave num_notificacion
	 * @param numNoti String
	 * @return boolean
	 * @throws DAOException
	 */
	public boolean deleteNotificacionByPK(String numNoti) throws DAOException {
		
		int eliminado=0;
		if (log.isDebugEnabled()) log.debug("T4502DAO - deleteNotificacionByPK - numNoti " + numNoti);
		try{
					
			eliminado = executeUpdate(datasource, DELETE_NOTIFICACION_BY_NUM_NOTIF.toString(), new Object[]{numNoti});
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [DELETE_NOTIFICACION_BY_NUM_NOTIF]");
		}		
		
		return (eliminado>0);
	}
	
	
}
