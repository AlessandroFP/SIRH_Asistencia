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
 * Clase       : T4562DAO 
 * Descripcion : Clase encargada de administrar los datos de la tabla T4562NotificaDir
 * Proyecto    : ASISTENCIA - AOM ALERTA DE SOLICITUDES Y MOVIMIENTOS
 * Autor       : JVILLACORTA
 * Fecha       : 24-MARZO-2011 
 * 
 * */

public class T4562DAO extends DAOAbstract{
	
	private DataSource datasource;
	
	Propiedades constantes = new Propiedades(getClass(), "/constantes.properties");	
	
	private final StringBuffer INSERTAR_NOTIFICACION_BY_SOLICITUD = new 	StringBuffer("INSERT INTO t4562NotificaDir "
		      ).append("(num_notificacion, cod_anno, num_solicitud, cod_uuoo, cod_pers, num_seguim, tipo_solicitud, fec_notificacion, cod_pers_notif) "
		      ).append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ");
	
	//JVILLACORTA - 03/11/2011 - MODIFICA ALERTA DE SOLICITUDES
	private final StringBuffer FIND_ALL_NOTIFICACIONES = new  StringBuffer("select FIRST 1 * "
	).append("from t4562NotificaDir ");		
	
	private final StringBuffer FIND_ULTIMA_NOTIFICACION = new  StringBuffer("select first 1 * from t4562NotificaDir "	
	).append("order by num_notificacion desc ");
	//FIN - JVILLACORTA - 03/11/2011 - MODIFICA ALERTA DE SOLICITUDES
	
	/**
	* 
	* Este constructor del DAO dicierne como crear el datasource
	* dependiendo del tipo de objeto que recibe. Esto nos ayuda a
	* mejorar la invocacion del dao.
	* 
	* @param datasource Object
	*/
	public T4562DAO(Object datasource) {
		if (datasource instanceof DataSource)
		  this.datasource = (DataSource)datasource;
		else if (datasource instanceof String)
		  this.datasource = getDataSource((String)datasource);
		else
		  throw new DAOException(this, "Datasource no valido");
	}
	
	/** JVILLACORTA - 03/11/2011 - MODIFICA ALERTA DE SOLICITUDES
	 * Metodo que se encarga de registrar la notificacion de correo por aprobacion de solicitudes enviada a un jefe o directivo
	 * @param params Map (num_notificacion, cod_anno, num_solicitud, cod_uuoo, cod_pers, num_seguim, tipo_solicitud, fec_envio_notific, cod_pers_notif)  
	 * @return boolean	 
	 * @throws DAOException
	 */	
	public boolean insertNotificacionBySolicitudes(Map params) throws DAOException {
		
		int insertado = 0;
		if (log.isDebugEnabled()) log.debug("T4562DAO - insertNotificacionBySolicitudes - params " + params);
		if (log.isDebugEnabled()) log.debug("num_notificacion: " + params.get("num_notificacion"));
		if (log.isDebugEnabled()) log.debug("cod_anno: " + params.get("cod_anno"));
		if (log.isDebugEnabled()) log.debug("num_solicitud: " + params.get("num_solicitud"));
		if (log.isDebugEnabled()) log.debug("cod_uuoo: " + params.get("cod_uuoo"));
		if (log.isDebugEnabled()) log.debug("cod_pers: " + params.get("cod_pers"));
		if (log.isDebugEnabled()) log.debug("num_seguim: " + params.get("num_seguim"));
		if (log.isDebugEnabled()) log.debug("tipo_solicitud: " + params.get("tipo_solicitud"));
		if (log.isDebugEnabled()) log.debug("fec_notificacion: " + params.get("fec_notificacion"));
		if (log.isDebugEnabled()) log.debug("cod_pers_notif: " + params.get("cod_pers_notif"));			
		
		try{
			insertado = executeUpdate(datasource, INSERTAR_NOTIFICACION_BY_SOLICITUD.toString(), new Object[]{params.get("num_notificacion"), 
				params.get("cod_anno"), params.get("num_solicitud") , params.get("cod_uuoo"), params.get("cod_pers"), params.get("num_seguim"),
				params.get("tipo_solicitud"), params.get("fec_notificacion") , params.get("cod_pers_notif")});
		} catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [INSERTAR_NOTIFICACION_BY_SOLICITUD]");
		}
		
		return (insertado > 0);
	} //FIN - JVILLACORTA - 03/11/2011 - MODIFICA ALERTA DE SOLICITUDES
	
	/** JVILLACORTA - 03/11/2011 - MODIFICA ALERTA DE SOLICITUDES
	  * Metodo que obtiene todas las notificaciones enviadas por alerta de solicitudes pendientes a directivos	
	  * @return List
	  * @throws DAOException
	  */
	public List findAllNotificacionesEnviadas() throws DAOException {
		
		List notificaciones = null;
		if (log.isDebugEnabled()) log.debug("T4562DAO - findAllNotificacionesEnviadas");
		try{
			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			notificaciones = executeQuery(datasource, FIND_ALL_NOTIFICACIONES.toString());
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [FIND_ALL_NOTIFICACIONES]");
		}	
		
		return notificaciones;
	} //FIN - JVILLACORTA - 03/11/2011 - MODIFICA ALERTA DE SOLICITUDES

	
	/** JVILLACORTA - 03/11/2011 - MODIFICA ALERTA DE SOLICITUDES
	  * Metodo que devuelve la ultima notificacion registrada	
	  * @return Map
	  * @throws DAOException
	  */
	public Map findUltimaNotificacionRegistrada() throws DAOException {
		
		
		Map notificacion= null;
		if (log.isDebugEnabled()) log.debug("T4562DAO - findUltimaNotificacionRegistrada");
		try{
			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);			
			notificacion = executeQueryUniqueResult(datasource, FIND_ULTIMA_NOTIFICACION.toString());
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [FIND_ULTIMA_NOTIFICACION]");
		}	
		
		return notificacion;
	} //FIN - JVILLACORTA - 03/11/2011 - MODIFICA ALERTA DE SOLICITUDES
		
}
