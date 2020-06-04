package pe.gob.sunat.rrhh.asistencia.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.Propiedades;
import pe.gob.sunat.framework.util.date.FechaBean;

/** 
 * 
 * Clase       : T4563DAO 
 * Descripcion : Clase encargada de administrar los datos de la tabla T4563NotificaTra
 * Proyecto    : ASISTENCIA - AOM ALERTA DE SOLICITUDES Y MOVIMIENTOS
 * Autor       : JVILLACORTA
 * Fecha       : 29-MARZO-2011 
 * 
 * */

public class T4563DAO extends DAOAbstract{
	
	private DataSource datasource;
	
	Propiedades constantes = new Propiedades(getClass(), "/constantes.properties");	

	//JVILLACORTA - 18/11/2011 - MODIFICA ALERTA DE SOLICITUDES
	private final StringBuffer INSERTAR_NOTIFICACION_BY_MOVIMIENTO = new 	StringBuffer("INSERT INTO t4563NotificaTra "
    ).append("(num_notificacion, cod_pers, num_periodo, cod_mov, fec_ingreso, fec_notificacion) "
    ).append("VALUES (?, ?, ?, ?, ?, ?) ");
	//FIN - JVILLACORTA - 18/11/2011 - MODIFICA ALERTA DE SOLICITUDES
		
	//JVILLACORTA - 14/11/2011 - MODIFICA ALERTA DE SOLICITUDES
	private final StringBuffer FIND_ALL_NOTIFICACIONES = new  StringBuffer("select * "
	).append("from t4563NotificaTra ");	
	//FIN - JVILLACORTA - 14/11/2011 - MODIFICA ALERTA DE SOLICITUDES
	
	private final StringBuffer FIND_ULTIMA_NOTIFICACION = new  StringBuffer("select first 1 * from t4563NotificaTra "	
	).append("order by num_notificacion desc ");
	
	/**
	* 
	* Este constructor del DAO dicierne como crear el datasource
	* dependiendo del tipo de objeto que recibe. Esto nos ayuda a
	* mejorar la invocacion del dao.
	* 
	* @param datasource Object
	*/
	public T4563DAO(Object datasource) {
		if (datasource instanceof DataSource)
		  this.datasource = (DataSource)datasource;
		else if (datasource instanceof String)
		  this.datasource = getDataSource((String)datasource);
		else
		  throw new DAOException(this, "Datasource no valido");
	}
	
	/**JVILLACORTA - 18/11/2011 - MODIFICA ALERTA DE SOLICITUDES
	 * Metodo que se encarga de registrar la notificacion de correo por movimientos de asistencia enviadas a trabajadores
	 * @param params Map (num_notificacion, cod_pers, periodo, mov, fing, hing, fec_envio_notific)  
	 * @return boolean	 
	 * @throws DAOException
	 */	
	public boolean insertNotificacionByMovAsistencia(Map params) throws DAOException {
		
		int insertado = 0;
		if (log.isDebugEnabled()) log.debug("T4563DAO - insertNotificacionByMovAsistencia - params " + params);
		if (log.isDebugEnabled()) log.debug("num_notificacion: " + params.get("num_notificacion"));
		if (log.isDebugEnabled()) log.debug("cod_pers: " + params.get("cod_pers"));		
		if (log.isDebugEnabled()) log.debug("num_periodo: " + params.get("num_periodo"));
		if (log.isDebugEnabled()) log.debug("cod_mov: " + params.get("cod_mov"));
		if (log.isDebugEnabled()) log.debug("fec_ingreso: " + params.get("fec_ingreso"));
		//if (log.isDebugEnabled()) log.debug("hor_ingreso: " + params.get("hor_ingreso"));
		if (log.isDebugEnabled()) log.debug("fec_notificacion: " + params.get("fec_notificacion"));
		
		Date fhoy = new Date();
		fhoy = new FechaBean((String)params.get("fec_notificacion")).getSQLDate();
		if (log.isDebugEnabled()) log.debug("fhoy: " + fhoy);
		
		try{
			insertado = executeUpdate(datasource, INSERTAR_NOTIFICACION_BY_MOVIMIENTO.toString(), new Object[]{params.get("num_notificacion"), 
				params.get("cod_pers"), params.get("num_periodo") , params.get("cod_mov"), params.get("fec_ingreso"),
				fhoy});
		} catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [INSERTAR_NOTIFICACION_BY_MOVIMIENTO]");
		}
		
		return (insertado > 0);
	}//FIN - JVILLACORTA - 18/11/2011 - MODIFICA ALERTA DE SOLICITUDES
	
	/** JVILLACORTA - 14/11/2011 - MODIFICA ALERTA DE SOLICITUDES
	  * Metodo que obtiene todas las notificaciones enviadas por alerta de solicitudes pendientes a directivos	
	  * @return List
	  * @throws DAOException
	  */
	public List findAllNotificacionesEnviadas() throws DAOException {
		
		List notificaciones = null;
		if (log.isDebugEnabled()) log.debug("T4563DAO - findAllNotificacionesEnviadas");
		try{
			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			notificaciones = executeQuery(datasource, FIND_ALL_NOTIFICACIONES.toString());
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [FIND_ALL_NOTIFICACIONES]");
		}			
		return notificaciones;
	} //FIN - JVILLACORTA - 14/11/2011 - MODIFICA ALERTA DE SOLICITUDES

	/** JVILLACORTA - 14/11/2011 - MODIFICA ALERTA DE SOLICITUDES
	  * Metodo que devuelve la ultima notificacion registrada	
	  * @return Map
	  * @throws DAOException
	  */
	public Map findUltimaNotificacionRegistrada() throws DAOException {		
		
		Map notificacion= null;
		if (log.isDebugEnabled()) log.debug("T4563DAO - findUltimaNotificacionRegistrada");
		try{
			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);			
			notificacion = executeQueryUniqueResult(datasource, FIND_ULTIMA_NOTIFICACION.toString());
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [FIND_ULTIMA_NOTIFICACION]");
		}			
		return notificacion;
	} //FIN - JVILLACORTA - 14/11/2011 - MODIFICA ALERTA DE SOLICITUDES
	
}
