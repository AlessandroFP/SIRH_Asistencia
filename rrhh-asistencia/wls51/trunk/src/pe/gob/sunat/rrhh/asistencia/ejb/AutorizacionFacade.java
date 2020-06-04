package pe.gob.sunat.rrhh.asistencia.ejb;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import pe.gob.sunat.framework.core.bean.MensajeBean;
import pe.gob.sunat.framework.core.ejb.FacadeException;
import pe.gob.sunat.framework.core.ejb.StatelessAbstract;
import pe.gob.sunat.framework.core.pattern.ServiceLocator;
import pe.gob.sunat.framework.util.Propiedades;
import pe.gob.sunat.framework.util.common.T3Service;
import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.rrhh.asistencia.dao.T1273DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T1282DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T1962DAO;
import pe.gob.sunat.rrhh.dao.T02DAO;

  
/**
 * <p> Title: Scheduler de Autorizaciones</p>
 * <p> Proyecto : rrhh-autorizaciones</p>
 * <p> Clase : AutorizacionFacade</p>
 * <p> Fecha : 25 Junio 2008 </p>
 * <p> Description : Clase EJB tipo Facade que encapsula la funcionalidad del scheduler </p>
 * <p> Copyright : Copyright (c) 2000-2008 </p>
 * <p> Company : COMSA</p>
 * 
 * @author MIGUEL JURADO (COMSA)
 * @version 1.0
 * 
 * @ejb.bean name="AutorizacionFacadeEJB" description="AutorizacionFacade"
 *           jndi-name="ejb/facade/rrhh/asistencia/AutorizacionFacadeEJB"
 *           type="Stateless" view-type="remote"
 * @ejb.home remote-class="pe.gob.sunat.rrhh.asistencia.ejb.AutorizacionFacadeHome"
 *           extends="javax.ejb.EJBHome"
 * @ejb.interface remote-class="pe.gob.sunat.rrhh.asistencia.ejb.AutorizacionFacadeRemote"
 *                extends="javax.ejb.EJBObject"
 * @ejb.transaction-type type="Container"
 * @ejb.resource-ref description="DS a la base personal"
 *                   res-ref-name="jdbc/dcsp" res-type="java.sql.DataSource"
 *                   res-auth="Container"
 * @weblogic.resource-description jndi-name="jdbc/dcsp" res-ref-name="jdbc/dcsp"
 * 
 * @ejb.resource-ref description="DS a la base Personas"
 *                   res-ref-name="jdbc/dgsp" res-type="java.sql.DataSource"
 *                   res-auth="Container" 
 * @weblogic.resource-description jndi-name="jdbc/dgsp" res-ref-name="jdbc/dgsp"
 * 
 * @weblogic.enable-call-by-reference True
 * @weblogic.clustering home-is-clusterable="True" stateless-bean-is-clusterable="True"
 * @weblogic.pool initial-beans-in-free-pool="5" max-beans-in-free-pool="10"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 */

public class AutorizacionFacade extends StatelessAbstract {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Propiedades constantes = new Propiedades(getClass(), "/constantes.properties");
	
	
	/**
	 * Metodo encargado de procesar las autorizacion
	 * 
	 * @throws FacadeException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */

	public void actualizarAutorizaciones() throws FacadeException {

		Propiedades constantes = new Propiedades(getClass(),"/constantes.properties");
		DataSource dbpool_sp = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dcsp");
		if (log.isDebugEnabled()){
		  log.debug("entro al RelojSunat");
		}
		String formatoHora = "HH:mm";
		FechaBean horaHoy = new FechaBean(new FechaBean().getFormatDate(formatoHora), formatoHora);
		String flagCtrl = "-";
		try {
			flagCtrl = T3Service.getInstance("fileT3").getT3FileProperty("autorizacion.t3", "flagCtrl");
		} catch (Exception e) {
			return;
		}
		if (flagCtrl != null) {
			flagCtrl = flagCtrl.trim();
			if (!flagCtrl.equals("1")) {
				return;
			}
		} else {
			return;
		}
		
		try {
			
			//JRR - 14/03/2011
			if (log.isDebugEnabled()){
				  log.debug("Hora actual: " + horaHoy.getHora24());
			}
			//
			
			if (((horaHoy.getTimestamp().getTime() >= (new FechaBean(constantes.leePropiedad("FECH_INI_RNG1"), formatoHora).getTimestamp().getTime())) 
				&& (horaHoy.getTimestamp().getTime() <= (new FechaBean(constantes.leePropiedad("FECH_FIN_RNG1"), formatoHora).getTimestamp().getTime())))
				|| ((horaHoy.getTimestamp().getTime()>= (new FechaBean(constantes.leePropiedad("FECH_INI_RNG2"),formatoHora).getTimestamp().getTime())) 
				&& (horaHoy.getTimestamp().getTime() <= (new FechaBean(constantes.leePropiedad("FECH_FIN_RNG2"),formatoHora).getTimestamp().getTime())))
				|| ((horaHoy.getTimestamp().getTime() >=(new FechaBean(constantes.leePropiedad("FECH_INI_RNG3"),formatoHora).getTimestamp().getTime())) 
				&& (horaHoy.getTimestamp().getTime() <=	(new FechaBean(constantes.leePropiedad("FECH_FIN_RNG3"),formatoHora).getTimestamp().getTime())))) {

				T02DAO t02dao = new T02DAO(dbpool_sp);
				T1282DAO vacacion = new T1282DAO(dbpool_sp);
				T1273DAO licencia = new T1273DAO(dbpool_sp);
				
				FechaBean hoy = new FechaBean();
				FechaBean intervaloFin = new FechaBean();
				intervaloFin.getCalendar().add(Calendar.DATE, -3);
				FechaBean intervaloIni = new FechaBean();
				intervaloIni.getCalendar().add(Calendar.DATE, -1);
				//cesados

				// Bloquear X Vacaciones
				Map vacaciones = new HashMap();
				Map codigo = null;
				vacaciones.put("fechaProceso", hoy.getTimestamp());
				vacaciones.put("licencia", constantes.leePropiedad("VACACION"));
				if (log.isDebugEnabled()){
				  log.debug("mapa Bloquear X Vacaciones es " + vacaciones);
				}
				List codigosVacaciones = vacacion.obtenerVacaciones(vacaciones);
				if (log.isDebugEnabled()){
				  log.debug("la lista de codigosVacaciones tiene " + codigosVacaciones.size());
				}
				for (int i = 0; i < codigosVacaciones.size(); i++) {
					
					codigo = (HashMap) codigosVacaciones.get(i);
					codigo.put("cod_usumodif","Scheduler");
					codigo.put("autorizacion",constantes.leePropiedad("BLOQUEAR_X_VACACIONES"));
					if (log.isDebugEnabled()){
					  log.debug("efectuarAutorizaciones de Bloquear Vacaciones tiene " + codigo);
					}
					this.efectuarAutorizaciones(codigo);
					
				}

				// Bloquear X Suspension
				Map suspension = new HashMap();
				suspension.put("fechaProceso", hoy.getTimestamp());
				suspension.put("licencia", constantes.leePropiedad("SUSPENCION"));
				if (log.isDebugEnabled()){
				  log.debug("mapa Bloquear X Suspension es " + suspension);
				}
				List codigosSuspension = licencia.obtenerCodigosBloqueado(suspension);
				if (log.isDebugEnabled()){
				  log.debug("la lista de codigosSuspension tiene " + codigosSuspension.size());
				}
				for (int i = 0; i < codigosSuspension.size(); i++) {
					codigo = (HashMap) codigosSuspension.get(i);
					codigo.put("cod_usumodif","Scheduler");
					codigo.put("autorizacion",constantes.leePropiedad("BLOQUEAR_X_SUSPENCION"));
					if (log.isDebugEnabled()){
					  log.debug("efectuarAutorizaciones de Bloquear X Suspension tiene " + codigo);
					}
					this.efectuarAutorizaciones(codigo);
				}

				// desbloquea fin Suspension
				Map finSuspension = new HashMap();
				finSuspension.put("fechaIni", intervaloIni.getTimestamp());
				finSuspension.put("fechaFin", intervaloFin.getTimestamp());
				finSuspension.put("licencia", constantes.leePropiedad("SUSPENCION"));
				if (log.isDebugEnabled()){
				  log.debug("mapa desbloquea fin Suspension es " + finSuspension);
				}
				List codigosFinSuspension = licencia.obtenerCodigosDesbloquea(finSuspension);
				if (log.isDebugEnabled()){
				  log.debug("la lista de codigosFinSuspension tiene " + codigosFinSuspension.size());
				}
				for (int i = 0; i < codigosFinSuspension.size(); i++) {
					codigo = (HashMap) codigosFinSuspension.get(i);
					codigo.put("cod_usumodif","Scheduler");
					codigo.put("autorizacion",constantes.leePropiedad("DESBLOQUEAR_X_FIN_SUSPENCION"));
					if (log.isDebugEnabled()){
					  log.debug("efectuarAutorizaciones de desbloquea fin Suspension tiene " + codigo);
					}
					this.efectuarAutorizaciones(codigo);
				}

				// Bloquear Cesados
				Map cesados = new HashMap();
				cesados.put("fechaIni", intervaloIni.getTimestamp());
				cesados.put("fechaFin", intervaloFin.getTimestamp());
				if (log.isDebugEnabled()){
				  log.debug("mapa Bloquear Cesados es " + cesados);
				}
				List codigosCesados = t02dao.obtenerCesados(cesados);
				if (log.isDebugEnabled()){
				  log.debug("la lista de codigosCesados tiene " + codigosCesados.size());
				}
				for (int i = 0; i < codigosCesados.size(); i++) {
					codigo = (HashMap) codigosCesados.get(i);
					codigo.put("cod_usumodif","Scheduler");
					codigo.put("autorizacion",constantes.leePropiedad("BLOQUEAR_X_CESADOS"));
					if (log.isDebugEnabled()){
					  log.debug("efectuarAutorizaciones de Bloquear Cesados tiene " + codigo);
					}
					this.efectuarAutorizaciones(codigo);
				}

				// desbloquear Fin Vacaciones
				Map finVacaciones = new HashMap();
				finVacaciones.put("fechaIni", intervaloIni.getTimestamp());
				finVacaciones.put("fechaFin", intervaloFin.getTimestamp());
				finVacaciones.put("licencia", constantes.leePropiedad("VACACION"));
				if (log.isDebugEnabled()){
				  log.debug("mapa desbloquear Fin Vacaciones es " + finVacaciones);
				}
				List codigosFinVacaciones = vacacion.obtenerfinVacaciones(finVacaciones);
				if (log.isDebugEnabled()){
				  log.debug("la lista de codigosFinVacaciones tiene " + codigosFinVacaciones.size());
				}
				for (int i = 0; i < codigosFinVacaciones.size(); i++) {
					codigo = (HashMap) codigosFinVacaciones.get(i);
					codigo.put("cod_usumodif","Scheduler");
					codigo.put("autorizacion",constantes.leePropiedad("DESBLOQUEAR_X_FIN_VACACIONES"));
					if (log.isDebugEnabled()){
					  log.debug("efectuarAutorizaciones de desbloquear Fin Vacaciones tiene " + codigo);
					}
					this.efectuarAutorizaciones(codigo);
				}

				// bloquear Exoneraciones
				Map exonerados = new HashMap();
				exonerados.put("fechaProceso", hoy.getTimestamp());
				exonerados.put("licencia", constantes.leePropiedad("EXONERACION"));
				if (log.isDebugEnabled()){
				  log.debug("mapa bloquear Exoneraciones es " + exonerados);
				}
				List codigosExonerados = licencia.obtenerCodigosBloqueado(exonerados);
				if (log.isDebugEnabled()){
				  log.debug("la lista de codigosExonerados tiene " + codigosExonerados.size());
				}
				for (int i = 0; i < codigosExonerados.size(); i++) {
					codigo = (HashMap) codigosExonerados.get(i);
					codigo.put("cod_usumodif","Scheduler");
					codigo.put("autorizacion",constantes.leePropiedad("BLOQUEAR_X_EXONERACIONES"));
					if (log.isDebugEnabled()){
					  log.debug("efectuarAutorizaciones de bloquear Exoneraciones tiene " + codigo);
					}
					this.efectuarAutorizaciones(codigo);
				}

				// desbloquear Fin Exoneraciones -
				Map finExonerados = new HashMap();
				finExonerados.put("fechaIni", intervaloIni.getTimestamp());
				finExonerados.put("fechaFin", intervaloFin.getTimestamp());
				finExonerados.put("licencia", constantes.leePropiedad("EXONERACION"));
				if (log.isDebugEnabled()){
				  log.debug("mapa desbloquear Fin Exoneraciones es " + finExonerados);
				}
				List codigosFinExonerados = licencia.obtenerCodigosDesbloquea(finExonerados);
				if (log.isDebugEnabled()){
				  log.debug("la lista de codigosFinExonerados tiene " + codigosFinExonerados.size());
				}
				for (int i = 0; i < codigosFinExonerados.size(); i++) {
					codigo = (HashMap) codigosFinExonerados.get(i);
					codigo.put("cod_usumodif","Scheduler");
					codigo.put("autorizacion",constantes.leePropiedad("DESBLOQUEAR_X_FIN_EXONERACIONES"));
					if (log.isDebugEnabled()){
					  log.debug("efectuarAutorizaciones de desbloquear Fin Exoneraciones tiene " + codigo);
					}
					this.efectuarAutorizaciones(codigo);
				}

				// desbloquear nuevos
				Map nuevos = new HashMap();
				nuevos.put("cod_usumodif","Scheduler");
				nuevos.put("fechaIni", intervaloIni.getTimestamp());
				nuevos.put("fechaFin", intervaloFin.getTimestamp());
				nuevos.put("autorizacion",constantes.leePropiedad("DESBLOQUEAR_X_NUEVOS"));
				if (log.isDebugEnabled()){
				  log.debug("efectuarAutorizaciones de desbloquear nuevos tiene " + nuevos);
				}
				this.efectuarAutorizaciones(nuevos);
				
				
				/* ICAPUNAY - AOM 45U3T10: ALERTA DE VACACIONES PARA TRABAJADORES (BLOQUEO DE LECTURA DE FOTOCHECK) - 07/04/2011 */
				
				// Bloquear X Vacaciones Programadas
				Map vacacionesProgramadas = new HashMap();				
				vacacionesProgramadas.put("estadoTrabajador", constantes.leePropiedad("ACTIVO"));
				vacacionesProgramadas.put("licencia", constantes.leePropiedad("VACACION_PROGRAMADA"));
				vacacionesProgramadas.put("estadoLicencia", constantes.leePropiedad("PROGRAMACION_ACEPTADA"));
				vacacionesProgramadas.put("fechaProceso", hoy.getTimestamp());
				if (log.isDebugEnabled()){
				  log.debug("El mapa Bloquear X Vacaciones Programadas es: " + vacacionesProgramadas);
				}
				List codigosVacacionesProgramadas = vacacion.obtenerVacacionesProgramadas(vacacionesProgramadas);//FALTA CREAR METODO
				if (log.isDebugEnabled()){
				  log.debug("La lista de codigosVacacionesProgramadas tiene: " + codigosVacacionesProgramadas.size()+" codigos");
				}
				for (int i = 0; i < codigosVacacionesProgramadas.size(); i++) {
					
					codigo = (HashMap) codigosVacacionesProgramadas.get(i);
					codigo.put("cod_usumodif","Scheduler");
					codigo.put("autorizacion",constantes.leePropiedad("BLOQUEAR_X_VACACIONES_PROGRAMADAS"));
					if (log.isDebugEnabled()){
					  log.debug("efectuarAutorizaciones de Bloquear Vacaciones Programadas tiene el: " + codigo);
					}
					this.efectuarAutorizaciones(codigo);
					
				}
				
				/* FIN ICAPUNAY - AOM 45U3T10: ALERTA DE VACACIONES PARA TRABAJADORES (BLOQUEO DE LECTURA DE FOTOCHECK) - 07/04/2011 */
				
			}
		} catch (Exception e) {
			log.error(e, e);
			MensajeBean msgErr = new MensajeBean();
			msgErr.setError(true);
			msgErr.setMensajesol("En este momento no podemos atenderlo, por favor intente en unos momentos.");
			msgErr.setMensajeerror(e.toString());
			throw new FacadeException(this, msgErr);
		} finally {

		}
	}
	
	//////////////
	/**
	 * Metodo encargado de actualizar registros de la tablas T1962
	 * 
	 * @throws FacadeException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */

	public void efectuarAutorizaciones(Map datos) throws FacadeException {
		DataSource dbpool_gsp = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dgsp");
		T1962DAO autorizaciones = new T1962DAO(dbpool_gsp);
		try{
			
			if( constantes.leePropiedad("BLOQUEAR_X_VACACIONES").equals((String)datos.get("autorizacion"))){
				autorizaciones.bloquearXVacaciones(datos);
			} else if( constantes.leePropiedad("BLOQUEAR_X_SUSPENCION").equals((String)datos.get("autorizacion"))){
				autorizaciones.bloquearXSuspensiones(datos);
			} else if( constantes.leePropiedad("BLOQUEAR_X_CESADOS").equals((String)datos.get("autorizacion"))){
				autorizaciones.bloquearCesados(datos);
			} else if( constantes.leePropiedad("BLOQUEAR_X_EXONERACIONES").equals((String)datos.get("autorizacion"))){
				autorizaciones.bloquearExoneraciones(datos);
			} else if( constantes.leePropiedad("DESBLOQUEAR_X_FIN_SUSPENCION").equals((String)datos.get("autorizacion"))){
				autorizaciones.desbloquearFinSuspensiones(datos);
			} else if( constantes.leePropiedad("DESBLOQUEAR_X_FIN_VACACIONES").equals((String)datos.get("autorizacion"))){
				autorizaciones.desbloquearFinVacaciones(datos);
			} else if( constantes.leePropiedad("DESBLOQUEAR_X_FIN_EXONERACIONES").equals((String)datos.get("autorizacion"))){
				autorizaciones.desbloquearFinExoneraciones(datos);
			} else if( constantes.leePropiedad("DESBLOQUEAR_X_NUEVOS").equals((String)datos.get("autorizacion"))){
				autorizaciones.desbloquearNuevos(datos);
			
			/* ICAPUNAY - AOM 45U3T10: ALERTA DE VACACIONES PARA TRABAJADORES (BLOQUEO DE LECTURA DE FOTOCHECK) - 07/04/2011 */
			} else if( constantes.leePropiedad("BLOQUEAR_X_VACACIONES_PROGRAMADAS").equals((String)datos.get("autorizacion"))){
				autorizaciones.bloquearXVacacionesProgramadas(datos);//CREAR UN NUEVO METODO
			} 	
			/* FIN ICAPUNAY - AOM 45U3T10: ALERTA DE VACACIONES PARA TRABAJADORES (BLOQUEO DE LECTURA DE FOTOCHECK) - 07/04/2011 */

	} catch (Exception e) {
		log.error(e, e);
		MensajeBean msgErr = new MensajeBean();
		msgErr.setError(true);
		msgErr.setMensajesol("En este momento no podemos atenderlo, por favor intente en unos momentos.");
		msgErr.setMensajeerror(e.toString());
		throw new FacadeException(this, msgErr);
	} finally {

	}
	}
	///////
	
	
}