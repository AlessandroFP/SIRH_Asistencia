package pe.gob.sunat.sp.asistencia.ejb;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pe.gob.sunat.batch.dao.QueueDAO;
import pe.gob.sunat.framework.core.bean.MensajeBean;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.core.ejb.FacadeException;
import pe.gob.sunat.framework.core.ejb.StatelessAbstract;
import pe.gob.sunat.framework.core.pattern.ServiceLocator;
import pe.gob.sunat.framework.messaging.appmessaging.AppMessaging;
import pe.gob.sunat.framework.util.dao.SequenceDAO;
import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.framework.util.mail.Correo;
import pe.gob.sunat.rrhh.asistencia.dao.T1271DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T130DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T132DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T1933DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T3701DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T4635DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T4636DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T4819DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T4820DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T8167DAO;
import pe.gob.sunat.rrhh.dao.T1595DAO;//WERR-PAS20155E230300132
import pe.gob.sunat.sol.BeanFechaHora;
import pe.gob.sunat.sol.BeanMensaje;
import pe.gob.sunat.sol.IncompleteConversationalState;
import pe.gob.sunat.sol.seguridad.acceso.portal.bean.BeanUsuario;
import pe.gob.sunat.sp.asistencia.bean.BeanDevolucion;
import pe.gob.sunat.sp.asistencia.bean.BeanHoraExtra;
import pe.gob.sunat.sp.asistencia.bean.BeanMarcacion;
import pe.gob.sunat.sp.asistencia.bean.BeanPeriodo;
import pe.gob.sunat.sp.asistencia.bean.BeanResumen;
import pe.gob.sunat.sp.asistencia.bean.BeanTurnoTrabajo;
import pe.gob.sunat.sp.asistencia.dao.T1270DAO;
import pe.gob.sunat.sp.asistencia.dao.T1272DAO;
import pe.gob.sunat.sp.asistencia.dao.T1275DAO;
import pe.gob.sunat.sp.asistencia.dao.T1276DAO;
import pe.gob.sunat.sp.asistencia.dao.T1279DAO;
import pe.gob.sunat.sp.asistencia.dao.T1280DAO;
import pe.gob.sunat.sp.asistencia.dao.T1444DAO;
import pe.gob.sunat.sp.asistencia.dao.T1480DAO;
import pe.gob.sunat.sp.asistencia.dao.T9395DAO;
import pe.gob.sunat.sp.bean.BeanT12;
import pe.gob.sunat.sp.dao.CorreoDAO;
import pe.gob.sunat.sp.dao.T01DAO;
import pe.gob.sunat.sp.dao.T02DAO;
import pe.gob.sunat.sp.dao.T12DAO;
import pe.gob.sunat.sp.dao.T5864DAO;
import pe.gob.sunat.sp.dao.T99DAO;
import pe.gob.sunat.tecnologia.menu.bean.MenuCliente;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;

/**
 * 
 * @ejb.bean name="AsistenciaFacadeEJB"
 *           description="AsistenciaFacade"
 *           jndi-name="ejb/rrhh/facade/sp/asistencia/AsistenciaFacadeEJB"
 *           type="Stateless" view-type="remote"
 * 
 * @ejb.home remote-class="pe.gob.sunat.sp.asistencia.ejb.AsistenciaFacadeHome"
 *           extends="javax.ejb.EJBHome"
 * 
 * @ejb.interface remote-class="pe.gob.sunat.sp.asistencia.ejb.AsistenciaFacadeRemote"
 *                extends="javax.ejb.EJBObject"
 * 
 * @ejb.transaction-type type="Container"
 * 
 * @ejb.resource-ref res-ref-name="jdbc/dgsp" res-type="javax.sql.DataSource" res-auth="Container"
 * @weblogic.resource-description res-ref-name="jdbc/dgsp" jndi-name="jdbc/dgsp"
 * 
 * @ejb.resource-ref description="DS a la base de Secuencias" res-ref-name="jdbc/dcbdseq" res-type="java.sql.DataSource" res-auth="Container" * 
 * @weblogic.resource-description jndi-name="jdbc/dcbdseq" res-ref-name="jdbc/dcbdseq"
 * 
 * @jboss.container-configuration name="Standard Stateless SessionBean"
 * @jboss.version="3.2" 
 * @weblogic.enable-call-by-reference True
 * 
 * @weblogic.clustering home-is-clusterable="True" stateless-bean-is-clusterable="True"
 * @weblogic.transaction-descriptor trans-timeout-seconds = "300"
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.cache max-beans-in-cache="300"
 * 
 * @version 1.0
 */
public class AsistenciaFacade extends StatelessAbstract{

	private final Log log = LogFactory.getLog(getClass());
	private final Log log_papeleta = LogFactory.getLog("SIRH_papeleta");	
	//JRR - 07/05/2010
	ServiceLocator sl = ServiceLocator.getInstance();

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList buscarMarcacionesImpares(String dbpool, String fechaIni,
			String fechaFin, String criterio, String valor, HashMap seguridad)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		ArrayList marcaciones = null;
		try {

			T1275DAO dao = new T1275DAO();
			marcaciones = dao.findImpares(dbpool, fechaIni, fechaFin, criterio,
					valor, seguridad);
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return marcaciones;
	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList buscarMarcacionesPase(String dbpool, String fechaIni, String fechaFin, 
			String codPers, HashMap seguridad)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		ArrayList marcaciones = null;
		try {

			T1275DAO dao = new T1275DAO();
			marcaciones = dao.joinWithT1280T1597(dbpool, codPers, fechaIni, fechaFin, seguridad);
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return marcaciones;
	}	
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList buscarMarcaciones(String dbpool, String fecha,
			String codPers, HashMap seguridad)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		ArrayList marcaciones = null;
		//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
		T1270DAO turnoDAO = new T1270DAO();	
		T1276DAO periodoDAO = new T1276DAO();	
		T02DAO persDAO = new T02DAO();	
		BeanTurnoTrabajo turno1 = null;
		BeanTurnoTrabajo turno2 = null;
		BeanTurnoTrabajo turno3 = null;
		boolean visualizar = true;
		String s_fechaAnt = "";
		HashMap hmPers =null;
		String cod_rel="-";
		String periodo = "";
		Map params2 = new HashMap();
		boolean bPeriodoCerrado = false;
		//FIN 
		
		try {

			T1275DAO dao = new T1275DAO();
			//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
			hmPers = persDAO.findByCodPers(dbpool, codPers.trim().toUpperCase());
			cod_rel=hmPers!=null?hmPers.get("t02cod_rel").toString():"-";						
			params2.put("dbpool", dbpool);			
			//FIN
			
			//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados			
			log.debug("fecha: "+fecha);
			visualizar = true;
			turno1 = null;
			turno2 = null;
			turno3 = null;
			
			if(!cod_rel.equals("-")) {
				if(cod_rel.equals("09")) {//regimen 1057 (CAS)
					periodo = periodoDAO.findByFechaCAS(dbpool, fecha);	
					log.debug("periodo CAS: "+periodo);
					params2.put("periodo", periodo);
					bPeriodoCerrado = periodoDAO.isPeriodoCerradoCAS(params2);
					log.debug("bPeriodoCerrado CAS: "+bPeriodoCerrado);
				} else if(cod_rel.equals("10")) {//Modalidades Formativas 
					periodo = periodoDAO.findByFechaModFormativas(dbpool, fecha);	
					log.debug("periodo formativas: "+periodo);
					params2.put("periodo", periodo);
					bPeriodoCerrado = periodoDAO.isPeriodoCerradoModFormativa(params2);
					log.debug("bPeriodoCerrado Formativa: "+bPeriodoCerrado);												
				} else { //regimen 276-728
					periodo = periodoDAO.findByFecha(dbpool, fecha);
					log.debug("periodo 276-728: "+periodo);
					params2.put("periodo",periodo);
					bPeriodoCerrado = periodoDAO.isPeriodoCerrado(params2);
					log.debug("bPeriodoCerrado 276-728: "+bPeriodoCerrado);	
				}
			}
		
			turno1 = turnoDAO.joinWithT45ByCodPersFecha_NoControlado_ActInact_OperAdm1dia(dbpool,codPers.trim(),fecha);		    
			if (turno1!=null && bPeriodoCerrado){
				visualizar = false;	//no se debe visualizar informacion de s_fecha
				log.debug("NO visualizar informacion de fecha: "+fecha);
			}else{
				turno2 = turnoDAO.joinWithT45ByCodPersFecha_NoControlado_ActInact_OperAdm2dias(dbpool,codPers.trim(),fecha);
				if (turno2!=null && bPeriodoCerrado){
					visualizar = false;	//no se debe visualizar informacion de s_fecha
					log.debug("NO visualizar informacion1 de fecha: "+fecha);
				}else{
					s_fechaAnt = Utiles.dameFechaAnterior(fecha, 1);
					log.debug("s_fechaAnt: "+s_fechaAnt);
					turno3 = turnoDAO.joinWithT45ByCodPersFecha_NoControlado_ActInact_OperAdm2dias(dbpool,codPers.trim(),s_fechaAnt);
					if (turno3!=null && bPeriodoCerrado){
						visualizar = false; //no se debe visualizar informacion de s_fecha
						log.debug("NO visualizar informacion2 de fecha: "+fecha);
					}						
				}							    
			}
							
			if (visualizar==true){
				log.debug("SI visualizar informacion de fecha: "+fecha);					
			//FIN ICAPUNAY
				
				marcaciones = dao.joinWithT1280(dbpool, codPers, fecha, seguridad);
				
			}//add esta linea //ICAPUNAY - PAS20165E230300132 - MSNAAF071		
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return marcaciones;
	}	

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList listarMarcacionesImpares(String dbpool, String codPers,
			String fecha) throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		ArrayList marcaciones = null;
		try {

			T1275DAO dao = new T1275DAO();
			marcaciones = dao.joinWithT1280(dbpool, codPers, fecha);
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		return marcaciones;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="RequiresNew"
	 */
	public void registrarMarcacionImpar(String dbpool, String codPers,
			String fecha, String hora, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {

			MantenimientoFacadeHome facadeHome = (MantenimientoFacadeHome) sl.getRemoteHome(
							MantenimientoFacadeHome.JNDI_NAME,
							MantenimientoFacadeHome.class);
			MantenimientoFacadeRemote mantenimiento = facadeHome.create();

			T1270DAO turnoDAO = new T1270DAO();
			String periodo = mantenimiento.periodoCerradoAFecha(dbpool, fecha);

			if (!periodo.equals("")) {
				throw new IncompleteConversationalState("El periodo " + periodo
						+ " se encuentra cerrado.");
			} else {

				BeanTurnoTrabajo turno = turnoDAO.joinWithT45ByCodFecha(dbpool,
						codPers, fecha);

				if (turno != null) {

					float dif1 = Utiles.obtenerHorasDiferencia(turno.getHoraIni(), hora);
					float dif2 = Utiles.obtenerHorasDiferencia(hora, turno.getHoraFin());

					if (dif1 < 0) {
						throw new IncompleteConversationalState(
								"La hora ingresada es anterior a la hora inicio ("+turno.getHoraIni()+") de su turno de trabajo.");
					}else if (dif2 < 0) {
						throw new IncompleteConversationalState(
								"La hora ingresada es posterior a la hora fin ("+turno.getHoraFin()+") de su turno de trabajo.");
					} else {

						T1275CMPHome cmpHome = (T1275CMPHome) sl.getLocalHome(T1275CMPHome.JNDI_NAME);

						//insertamos el registro
						T1275CMPLocal cmpLocal = cmpHome.create(
								codPers, new BeanFechaHora(fecha).getSQLDate(), hora, Constantes.RELOJ_MANUAL);
						
						cmpLocal.setCuserCrea(usuario);	
						cmpLocal.setFcreacion(new Timestamp(System.currentTimeMillis()));
						cmpLocal.setEstado(Constantes.ACTIVO);
					}
				} else {
					throw new IncompleteConversationalState("Ud. no posee un turno asignado para "+fecha+".");
				}
			}

		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

	}

	
	/**
	 * Metodo encargado de invocar al proceso de Asistencia
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param mapa
	 * @return
	 * @throws RemoteException
	 */
	public String procesarAsistencia(Map mProceso) throws IncompleteConversationalState,
			RemoteException {
	/*public String procesarAsistencia(String dbpool, String periodo,
			String criterio, String valor, String codPers, String usuario,
			HashMap seguridad, String indPap) throws IncompleteConversationalState,
			RemoteException {*/

		BeanMensaje beanM = new BeanMensaje();
		String res = Constantes.OK;
		try {
			//JROJAS4 - 25/03/2010
			String dbpool = mProceso.get("dbpool").toString();
			String regimen = (mProceso.get("regimen")!=null ? mProceso.get("regimen").toString().trim():"");
			String periodo = mProceso.get("periodo").toString();
			String criterio = mProceso.get("criterio").toString();
			String valor = mProceso.get("valor").toString();
			Map seguridad = (HashMap)mProceso.get("seguridad");
			String codPers = mProceso.get("codigo").toString();
			String indPap = mProceso.get("indPap").toString();
			String usuario = mProceso.get("usuario").toString();

			MantenimientoFacadeHome facadeHome = (MantenimientoFacadeHome) sl.getRemoteHome(
							MantenimientoFacadeHome.JNDI_NAME,
							MantenimientoFacadeHome.class);
			MantenimientoFacadeRemote mantenimiento = facadeHome.create();
			
			boolean cerrado = false;
			Map prms = new HashMap();
			HashMap params = new HashMap();
			
			if (regimen.equals("2")) { //Para el reg.CAS si viene de proceso manual
				cerrado = mantenimiento.periodoCerradoCAS(mProceso);
				
			} else if (regimen.equals("1")){ //Para los Contratados/Nombrados
				cerrado = mantenimiento.periodoCerradoAFecha(dbpool,
						periodo, Utiles.obtenerFechaActual());
				
			} else if (regimen.equals("3")){ //JRR - 27/04/2011 - Para Modalidad Formativa
				cerrado = mantenimiento.periodoCerradoModFormativa(mProceso);
			}
			
			//JRR - 27/04/2011 - Para validar regimen
			params.put("regimen", regimen);
			prms.put("regimen", regimen);
			//
			
			//JRR - Solo me interesa enviar mensaje de periodo cerrado en los procesos manuales, en los automaticos se valida en ProcesoFacade
			if (cerrado) {
				throw new IncompleteConversationalState("El periodo " + periodo
						+ " se encuentra cerrado.");
			} else {
				
				//JRR - Si vamos a procesar, entonces importa si son CAS o NO CAS para enviar sus fechas de Inicio y Fin de proceso 
				T1271DAO asisDAO = new T1271DAO(dbpool);
				prms.put("criterio", criterio);
				prms.put("valor", valor);
				prms.put("seguridad", seguridad);	
				List lista = new ArrayList();

				//Encolamos el proceso
				QueueDAO qd = new QueueDAO();
				int total = 0;
				
				if (log.isDebugEnabled()) log.debug("PROCESAR ASISTENCIA - findPersonalAsistencia MAP PRMS:"+prms);
				lista = asisDAO.findPersonalAsistencia(prms);

				total = ( lista != null ) ? lista.size() : 0;
				if (log.isDebugEnabled()) log.debug("total: "+total);

				params.put("dbpool", dbpool);
				params.put("periodo", periodo);
				params.put("criterio", criterio);
				params.put("valor", valor);
				params.put("codPers", codPers);
				params.put("seguridad", seguridad);
				params.put("indPap", indPap);
				
				//ASANCHEZZ 20100514 - PARA QUE SE ACTUALICE EL ESTADO DEL PROCESO Y GENERE EL ZIP CORRESPONDIENTE
				params.put("codPersT1481", mProceso.get("codPersT1481")!=null ? mProceso.get("codPersT1481").toString().trim():"");			
				//FIN

				if (log.isDebugEnabled()) log.debug("PARAMS: " + params);

				int numPartes = 8;
				int limiteInferior = 0;
				int limiteSuperior = 0;
				int grupo = (total / numPartes);
				if (total < numPartes) {
					numPartes = 1;
				}

				for (int i = 0; i < numPartes; i++) {

					limiteSuperior += grupo;
					if (i == numPartes - 1) {
						limiteSuperior = total;
					}

					log.info("Enviando grupo " + (i + 1) + " ("
							+ limiteInferior + " - " + limiteSuperior + ")");

					//seteamos los limites a procesar
					params.put("limiteInferior", "" + limiteInferior);
					params.put("limiteSuperior", "" + limiteSuperior);

					if (criterio.equals("2")) {
						params.put("observacion",
								"Proceso de asistencia del periodo " + periodo
								+ ". Grupo " + (i + 1));
					} else {
						params.put("observacion",
								"Proceso de asistencia del periodo " + periodo
								+ ". Criterio : " +criterio.trim()+ "-" + valor.toUpperCase().trim() +  " .Grupo " + (i + 1));
					}

					
					qd.encolaProceso(ProcesoFacadeHome.JNDI_NAME,"procesarAsistencia", params, usuario);
					//PARA PRUEBAS LOCALES
					/*ProcesoFacadeHome facade2Home = (ProcesoFacadeHome) sl.getRemoteHome(ProcesoFacadeHome.JNDI_NAME,
  							ProcesoFacadeHome.class);

					ProcesoFacadeRemote remote = facade2Home.create();	
					remote.procesarAsistencia(params, usuario);*/
					//PARA PRUEBAS LOCALES
					limiteInferior = limiteSuperior + 1;
				}

			}	

		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return res;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public ArrayList actualizarPapeletas(String dbpool, String[] params,
			ArrayList lista, String usuario, String fecha, String supervisor, String[] observs)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		ArrayList mensajes = new ArrayList();
		try {
			
			MantenimientoFacadeHome facadeHome = (MantenimientoFacadeHome) sl.getRemoteHome(
							MantenimientoFacadeHome.JNDI_NAME,
							MantenimientoFacadeHome.class);
			MantenimientoFacadeRemote mantenimiento = facadeHome.create();
			String periodo = mantenimiento.periodoCerradoAFecha(dbpool, fecha);

			if (!periodo.equals("")) {
				throw new IncompleteConversationalState("El periodo " + periodo + " se encuentra cerrado.");
			} else {

				HashMap mAsis = null;
				//prac-jcallo
				T1271DAO dao = new T1271DAO(dbpool);				
				float minle = 0;
				T1270DAO daoTurno = new T1270DAO();
				BeanTurnoTrabajo turno = null;
				
				if (lista != null && lista.size() > 0) {
					mAsis = (HashMap) lista.get(0);
					log.debug("lista.size : "+lista.size());
					//PRAC-JCALLO
					Map prms = new HashMap();
					prms.put("cod_pers", mAsis.get("cod_pers").toString());
					prms.put("fecha", Utiles.dateToString((Date)mAsis.get("fing")));					
					log.debug("metodo actualizarPapeletas...  prms :"+prms);
					if (dao.findByCodPersFFinNull(prms)) {
						throw new IncompleteConversationalState(
								"No se puede registrar una papeleta en un dia con marcaciones impares,"
										+ " primero debe ser registrada la marcacion faltante.");
					}
				}

				boolean registrar = true;
				for (int i = 0; i < lista.size(); i++) {
					
					registrar = true;
					mAsis = (HashMap) lista.get(i);
					String fActual = Utiles.obtenerFechaActual();

					if (params[i] != null) {
						if (params[i].trim().equals("-1")) {
							if (mAsis.get("mov")!= null &&
								(mAsis.get("estado_id").toString().equals(Constantes.PAPELETA_ACEPTADA) ||	
								mAsis.get("estado_id").toString().equals(Constantes.PAPELETA_PROCESADA) ||
								mAsis.get("estado_id").toString().equals(Constantes.PAPELETA_REGISTRADA))
								) {
								mensajes.add("Marcacion con hora de Inicio "
												+ Utiles.dateToString((Date)mAsis.get("fing"))
												+ ". No esta permitido eliminar papeletas.");

							}
						} else {
							if (mAsis.get("mov") == null
								|| !(params[i].trim().equals(mAsis.get("mov").toString().trim()))) {
								
								//solo se valida la cantidad de dias si la fecha actual
								//es distinta a la fecha de la marcacion
								if (!fActual.equals(Utiles.dateToString((Date)mAsis.get("fing")))) {

									int valida = validaRango(dbpool, params[i]
											.trim(), Utiles.dateToString((Date)mAsis.get("fing")), fActual);

									if (valida != 0) {
										mensajes
												.add("Para la marcacion con hora de Inicio "
														+ mAsis.get("hing").toString()
														+ ". Esta fuera del rango de fechas para el registro del tipo de papeleta indicada.");
										registrar = false;
									}
								}
							}

							if (registrar) {
								T9395DAO seguimPapeleta=new T9395DAO(sl.getDataSource(dbpool));
								
								T99DAO codigoDAO=new T99DAO();
								String fechaMinPap = codigoDAO.findParamByCodTabCodigo(dbpool, "510", "30");
								int limMinutos=Integer.parseInt(codigoDAO.findParamByCodTabCodigo(dbpool, "510", "50"));
								log.debug("fechaMinima: "+fechaMinPap+"minMinimo:"+limMinutos);
								
								String hPap="";
								if (mAsis.get("mov") == null
									|| (mAsis.get("mov") != null
									&& 
									( ( mAsis.get("mov").toString().trim().equals(Constantes.ENTRADA_NORMAL)
										|| mAsis.get("mov").toString().trim().equals(Constantes.MOVIMIENTO_NORMAL) 
										|| mAsis.get("mov").toString().trim().equals(Constantes.SALIDA_NORMAL)
									  ) 
									  || mAsis.get("estado_id").toString().trim().equals(Constantes.PRE_CALIFICACION_ASIS)
									  || mAsis.get("estado_id").toString().trim().equals(Constantes.PAPELETA_REGISTRADA)
									  || mAsis.get("estado_id").toString().trim().equals(Constantes.PAPELETA_RECHAZADA)
									) 
									&& !mAsis.get("mov").toString().trim().equals(params[i].trim()))) {
									log.debug("IF case:");
									turno = daoTurno.joinWithT45ByCodFecha(dbpool, mAsis.get("cod_pers").toString(), Utiles.dateToString((Date)mAsis.get("fing")));
										if (params[i].trim().equals(Constantes.MOV_PAPELETA_COMPENSACION)){
											float maxMinRefrigerio=0;											
												
												String exRef="NO";													
												if( mAsis.get("estado_id").toString().trim().equals(Constantes.PAPELETA_REGISTRADA)
														|| mAsis.get("estado_id").toString().trim().equals(Constantes.PAPELETA_RECHAZADA)){													
													log.debug("No es un movimiento normal -> Caso IF");
													HashMap existeSegPapeleta=(HashMap)seguimPapeleta.findByPK(mAsis.get("cod_pers").toString(), mAsis.get("periodo").toString(),  mAsis.get("fing").toString(), mAsis.get("hing").toString());
													if(existeSegPapeleta!=null && existeSegPapeleta.get("cod_personal")!=null){
														if(existeSegPapeleta.get("cod_mov_ini").toString().trim().equals(Constantes.MOV_TARDANZA_EFECTIVA)||
																existeSegPapeleta.get("cod_mov_ini").toString().trim().equals(Constantes.MOV_INASISTENCIA)||
																existeSegPapeleta.get("cod_mov_ini").toString().trim().equals(Constantes.MOV_TARDANZA_CON_DESCUENTO)||
																existeSegPapeleta.get("cod_mov_ini").toString().trim().equals(Constantes.MOV_TARDANZA_EFECTIVA)||
																existeSegPapeleta.get("cod_mov_ini").toString().trim().equals(Constantes.MOV_TARDANZA_SIN_DESCUENTO)){
															log.debug("Mov inasistencia.:");
															hPap=mAsis.get("hing").toString().trim();
															mAsis.put("hsal", mAsis.get("hing").toString());
															mAsis.put("hing", turno.getHoraIni());
														}else if ((existeSegPapeleta.get("cod_mov_ini").toString().trim().equals(Constantes.MOV_EXCESO_REFRIGERIO))||
																(existeSegPapeleta.get("cod_mov_ini").toString().trim().equals(Constantes.MOV_SALIDA_NO_AUTORIZADA))) {
															if((existeSegPapeleta.get("cod_mov_ini").toString().trim().equals(Constantes.MOV_EXCESO_REFRIGERIO))){																
																maxMinRefrigerio=turno.getMinutosRefrigerio();
																exRef="SI";
																log.debug("maxMinRefrigerio: "+maxMinRefrigerio);														
															}else if((existeSegPapeleta.get("cod_mov_ini").toString().trim().equals(Constantes.MOV_SALIDA_NO_AUTORIZADA))){
																if (mAsis.get("hsal").toString().trim().equals("")){
																	mAsis.put("hsal", turno.getHoraFin());
																}
															}
															hPap=mAsis.get("hing").toString().trim();
														}else{
															log.debug("Mov otros.:");
															hPap=mAsis.get("hing").toString().trim();
														}
													}
												}else{
													log.debug("Es un movimiento normal -> Caso IF");
													if ((mAsis.get("mov").toString().trim().equals(Constantes.MOV_TARDANZA_EFECTIVA)) ||
															(mAsis.get("mov").toString().trim().equals(Constantes.MOV_INASISTENCIA)) ||
															(mAsis.get("mov").toString().trim().equals(Constantes.MOV_TARDANZA_CON_DESCUENTO))||
															(mAsis.get("mov").toString().trim().equals(Constantes.MOV_TARDANZA_SIN_DESCUENTO)))
														{		
															log.debug("Mov inasistencia.:");
															hPap=mAsis.get("hing").toString().trim();
															mAsis.put("hsal", mAsis.get("hing").toString());
															mAsis.put("hing", turno.getHoraIni());
														}else if ((mAsis.get("mov").toString().trim().equals(Constantes.MOV_EXCESO_REFRIGERIO))||
																(mAsis.get("mov").toString().trim().equals(Constantes.MOV_SALIDA_NO_AUTORIZADA))) {
															
															log.debug("ingreso a refrigerioo");//BORRAR 25/06
														
															if((mAsis.get("mov").toString().trim().equals(Constantes.MOV_EXCESO_REFRIGERIO))){
																/*T99DAO codigoDAO=new T99DAO();
																String climaLab = codigoDAO.findParamByCodTabCodigo(dbpool,Constantes.CODTAB_PARAMETROS_ASISTENCIA,Constantes.MINUTOS_MAXIMO_CLIMALABORAL);
																int minMaxClima = climaLab != null ? Integer.parseInt(climaLab) : 0;
																log.debug("minMaxClima: "+minMaxClima);*/																
																maxMinRefrigerio=turno.getMinutosRefrigerio();
																log.debug("maxMinRefrigerio: "+maxMinRefrigerio);														
															}else if((mAsis.get("mov").toString().trim().equals(Constantes.MOV_SALIDA_NO_AUTORIZADA))){
																if (mAsis.get("hsal").toString().trim().equals("")){
																	mAsis.put("hsal", turno.getHoraFin());
																}
															}
															hPap=mAsis.get("hing").toString().trim();
														}else{
															log.debug("Mov otros.:");
															hPap=mAsis.get("hing").toString().trim();
														}
												}
											T4819DAO daoBolsa = new T4819DAO(sl.getDataSource(dbpool));
											Integer saldo; 
											HashMap datos = new HashMap();
											datos.put("cod_pers", mAsis.get("cod_pers").toString());

											pe.gob.sunat.sp.dao.T02DAO empleadoDAO = new pe.gob.sunat.sp.dao.T02DAO();		//ICR 08/05/2015-PAS20155E230000154-labor antigua  (autorizacion y compensacion) y labor nueva (sol. compensacion) mayor o igual fecha ingreso a sunat
											Map mapaEmpleado = new HashMap();
											String regimen="";
											Date fecha_ingreso;	
											mapaEmpleado = empleadoDAO.findEmpleado(dbpool,mAsis.get("cod_pers").toString());	
											fecha_ingreso = (mapaEmpleado!=null && !mapaEmpleado.isEmpty())?((Date)mapaEmpleado.get("t02f_ingsun")):null;	
											datos.put("fecha_ingreso", fecha_ingreso);
											
											if(log.isDebugEnabled()) log.debug("Masis:"+mapaEmpleado);
											saldo = daoBolsa.findSaldoLaborAutorizadas(datos);
											if(log.isDebugEnabled()) log.debug("Saldo:"+saldo.floatValue()+", FechaIng: "+turno.getFechaIni());
											
											minle = Utiles.obtenerMinutosDiferencia(mAsis.get("hing").toString(), mAsis.get("hsal").toString());
											
											if((mAsis.get("mov").toString().trim().equals(Constantes.MOV_EXCESO_REFRIGERIO))||(exRef.equals("SI"))){
												regimen=(mapaEmpleado!=null && !mapaEmpleado.isEmpty())?mapaEmpleado.get("t02cod_rel").toString():"";
												log.debug("Es regimen:"+regimen);
												if(regimen.equals("09") || regimen.equals("10"))
												minle=minle-maxMinRefrigerio;
											}
												
											if(log.isDebugEnabled()) log.debug("MinAcumulados: "+minle);
											if (saldo.floatValue() < minle){
												//Si no tiene Saldo no se registra
												mensajes.add("No es posible registrar la compensación por horas por no contar con saldo suficiente (Registro: "+ mAsis.get("cod_pers").toString()+", Fecha y hora: "+ mAsis.get("fing").toString()+" "+ mAsis.get("hing").toString()+").");
												registrar = false;
											}											
											if(minle<limMinutos){
												mensajes.add("El número de minutos solicitados para la compensación por horas debe ser mayor o igual a "+limMinutos+" (Registro: "+ mAsis.get("cod_pers").toString()+", Fecha y hora: "+ mAsis.get("fing").toString()+" "+ mAsis.get("hing").toString()+").");
												registrar = false;
											}
											
											String[] fechaFormat=mAsis.get("fing").toString().split("-");
											String nuevaFecha=""+fechaFormat[2]+"/"+fechaFormat[1]+"/"+fechaFormat[0];
											log.debug("Nueva fecha:"+nuevaFecha);
											log.debug("Utiles.obtenerDiasDiferencia(fechaMinPap.toString(),nuevaFecha):"+Utiles.obtenerDiasDiferencia(fechaMinPap.toString(),nuevaFecha));
											//
											if(Utiles.obtenerDiasDiferencia(fechaMinPap.toString(),nuevaFecha)<0){
												mensajes.add("La fecha de la marcación no puede ser menor al "+fechaMinPap.toString());
												registrar = false;
											}
										}else{
											log.debug("Otro tipo de papeleta.");
										}
										if (registrar) {
											log.debug("Entrando a registrar 1er caso.");
											
											HashMap existeSegPapeleta=null;
											if (params[i].trim().equals(Constantes.MOV_PAPELETA_COMPENSACION)){
												existeSegPapeleta=(HashMap)seguimPapeleta.findByPK(mAsis.get("cod_pers").toString(), mAsis.get("periodo").toString(),  mAsis.get("fing").toString(), hPap);
											}
											else{
												existeSegPapeleta=(HashMap)seguimPapeleta.findByPK(mAsis.get("cod_pers").toString(), mAsis.get("periodo").toString(),  mAsis.get("fing").toString(), mAsis.get("hing").toString());
												hPap=mAsis.get("hing").toString();
											}												
											HashMap datosPap=new HashMap();
											log.debug("Existe1:"+existeSegPapeleta);
											if(existeSegPapeleta!=null && existeSegPapeleta.get("cod_personal")!=null){
												log.debug("SI encontre en T9395");
												registrarPapeleta(dbpool, mAsis.get("cod_pers").toString(), 
														mAsis.get("periodo").toString(),
														mAsis.get("u_organ").toString(),
														Utiles.dateToString((Date)mAsis.get("fing")),
														hPap, 
														params[i].trim(), usuario, supervisor, observs[i]);
												
												datosPap.put("hing", hPap);
											}else
											{		
												log.debug("MASIS:"+mAsis);log.debug("No encontre en T9395");												
												if( mAsis.get("mov").toString().trim().equals(Constantes.MOV_INASISTENCIA)||
														mAsis.get("mov").toString().trim().equals(Constantes.MOV_TARDANZA_CON_DESCUENTO)||
														mAsis.get("mov").toString().trim().equals(Constantes.MOV_TARDANZA_SIN_DESCUENTO))
												{			
													if (params[i].trim().equals(Constantes.MOV_PAPELETA_COMPENSACION)){
														registrarPapeleta(dbpool, mAsis.get("cod_pers").toString(), 
																mAsis.get("periodo").toString(),
																mAsis.get("u_organ").toString(),
																Utiles.dateToString((Date)mAsis.get("fing")),
																mAsis.get("hsal").toString(), 
																params[i].trim(), usuario, supervisor, observs[i]);
														log.debug("mAsis.get(salida):"+mAsis.get("hsal")+"Entrada="+mAsis.get("hing"));
														datosPap.put("hing", mAsis.get("hsal").toString());
													}else{
														registrarPapeleta(dbpool, mAsis.get("cod_pers").toString(), 
																mAsis.get("periodo").toString(),
																mAsis.get("u_organ").toString(),
																Utiles.dateToString((Date)mAsis.get("fing")),
																mAsis.get("hing").toString(), 
																params[i].trim(), usuario, supervisor, observs[i]);
														log.debug("mAsis.get(salida):"+mAsis.get("hsal")+"Entrada="+mAsis.get("hing"));
														datosPap.put("hing", mAsis.get("hing").toString());
													}
													
												}else{
													if(mAsis.get("hsal").toString().trim().equals("")){
														log.debug("Turno:"+turno.getHoraFin());
														mAsis.put("hsal", turno.getHoraFin());
													}
													registrarPapeleta(dbpool, mAsis.get("cod_pers").toString(), 
															mAsis.get("periodo").toString(),
															mAsis.get("u_organ").toString(),
															Utiles.dateToString((Date)mAsis.get("fing")),
															mAsis.get("hing").toString(), 
															params[i].trim(), usuario, supervisor, observs[i]);
													
													datosPap.put("hing", mAsis.get("hing").toString());	
												}												
											}										
											log_papeleta.info("Registrar|".
													concat(mAsis.get("cod_pers").toString()).
													concat("|").
													concat(Utiles.dateToString((Date)mAsis.get("fing"))).
													concat("|").
													concat(mAsis.get("hing").toString()).
													concat("|").
													concat(mAsis.get("mov").toString()).
													concat("|").
													concat(params[i])
											);
											HashMap existeSegPapeleta1=(HashMap)seguimPapeleta.findByPK(mAsis.get("cod_pers").toString(), mAsis.get("periodo").toString(),  mAsis.get("fing").toString(), datosPap.get("hing").toString());
											log.debug("Existe2:"+existeSegPapeleta1);											
											FechaBean fecAct = new FechaBean();

											DataSource dsSecuence= sl.getDataSource("java:/comp/env/jdbc/dcbdseq");
											int Secuencia = 0;
											Secuencia = new SequenceDAO().getSequence(dsSecuence,"SET9395");
											
											datosPap.put("secuencia",new Integer(Secuencia));
											datosPap.put("cod_pers", mAsis.get("cod_pers").toString());
											datosPap.put("periodo", mAsis.get("periodo").toString());											
											datosPap.put("fing", mAsis.get("fing").toString());
											datosPap.put("mov", mAsis.get("mov").toString());
											datosPap.put("cod_mov_pap", params[i].trim());
											datosPap.put("estado_id", mAsis.get("estado_id").toString());
											datosPap.put("ind_del", "0");
											if(existeSegPapeleta1!=null && existeSegPapeleta1.get("cod_personal")!=null){												
												datosPap.put("cod_usumodif", mAsis.get("cod_pers").toString());
												datosPap.put("fec_modif", fecAct.getTimestamp());											
												seguimPapeleta.updateMovFin(datosPap);
											}else
											{	
												datosPap.put("cod_usuregis", mAsis.get("cod_pers").toString());
												datosPap.put("fec_regis", fecAct.getTimestamp());											
												seguimPapeleta.insertSeguimPapeleta(datosPap);
											}
										}
										
								} else {
									log.debug("Else:");
									float maxMinRefrigerio=0;
									log.debug("params[i].trim().equals(mAsis.get(mov).toString().trim()):"+params[i].trim().equals(mAsis.get("mov").toString().trim())+"--"+observs[i].trim().equals(mAsis.get("obs_papeleta").toString().trim()));
									if ( (!params[i].trim().equals(mAsis.get("mov").toString().trim())) || (!observs[i].trim().equals(mAsis.get("obs_papeleta").toString().trim())) ) {
										if (!((mAsis.get("mov").toString().trim().equals(Constantes.ENTRADA_NORMAL)
											  || mAsis.get("mov").toString().trim().equals(Constantes.MOVIMIENTO_NORMAL) 
											  || mAsis.get("mov").toString().trim().equals(Constantes.SALIDA_NORMAL)
											) 
											|| mAsis.get("estado_id").toString().trim().equals(Constantes.PRE_CALIFICACION_ASIS)
											|| mAsis.get("estado_id").toString().trim().equals(Constantes.PAPELETA_REGISTRADA)
											|| mAsis.get("estado_id").toString().trim().equals(Constantes.PAPELETA_RECHAZADA)	
										)) {											
											throw new IncompleteConversationalState("No es posible modificar una calificacion.");
										}
										String exRef="NO";
										//Papeleta Compensacion - Ver Saldos
										if (params[i].trim().equals(Constantes.MOV_PAPELETA_COMPENSACION) || (mAsis.get("mov").toString().trim().equals(Constantes.MOV_PAPELETA_COMPENSACION))){
												turno = daoTurno.joinWithT45ByCodFecha(dbpool, mAsis.get("cod_pers").toString(),
														Utiles.dateToString((Date)mAsis.get("fing")));
												if(mAsis.get("mov").toString().trim().equals(Constantes.MOVIMIENTO_NORMAL)){
												if ((mAsis.get("mov").toString().trim().equals(Constantes.MOV_TARDANZA_EFECTIVA)) ||
														(mAsis.get("mov").toString().equals(Constantes.MOV_INASISTENCIA))||
														(mAsis.get("mov").toString().trim().equals(Constantes.MOV_TARDANZA_SIN_DESCUENTO)) ||
														(mAsis.get("mov").toString().trim().equals(Constantes.MOV_TARDANZA_CON_DESCUENTO)))
													{
														log.debug("Mov inasistencia.:");
														mAsis.put("hsal", mAsis.get("hing").toString());
														mAsis.put("hing", turno.getHoraIni());
													}else if ((mAsis.get("mov").toString().trim().equals(Constantes.MOV_EXCESO_REFRIGERIO))||
															(mAsis.get("mov").toString().trim().equals(Constantes.MOV_SALIDA_NO_AUTORIZADA))||
															(mAsis.get("mov").toString().trim().equals(Constantes.MOV_PAPELETA_COMPENSACION))) {
														log.debug("ingreso a refrigerioo");//BORRAR 25/06
													
														if((mAsis.get("mov").toString().trim().equals(Constantes.MOV_EXCESO_REFRIGERIO))){	
															exRef="SI";
															maxMinRefrigerio=turno.getMinutosRefrigerio();
															log.debug("maxMinRefrigerio: "+maxMinRefrigerio);														
														}else if((mAsis.get("mov").toString().trim().equals(Constantes.MOV_SALIDA_NO_AUTORIZADA))){
															if (mAsis.get("hsal").toString().trim().equals("")){
																mAsis.put("hsal", turno.getHoraFin());
															}
														}
													}else{
														log.debug("Mov otros.:");														
													}
												}else{
													log.debug("No es un movimiento normal -> Caso ELSE");
													HashMap existeSegPapeleta=(HashMap)seguimPapeleta.findByPK(mAsis.get("cod_pers").toString(), mAsis.get("periodo").toString(),  mAsis.get("fing").toString(), mAsis.get("hing").toString());
													if(existeSegPapeleta!=null && existeSegPapeleta.get("cod_personal")!=null){
														if(existeSegPapeleta.get("cod_mov_ini").toString().trim().equals(Constantes.MOV_TARDANZA_EFECTIVA)||
																existeSegPapeleta.get("cod_mov_ini").toString().trim().equals(Constantes.MOV_INASISTENCIA)||
																existeSegPapeleta.get("cod_mov_ini").toString().trim().equals(Constantes.MOV_TARDANZA_CON_DESCUENTO)||
																existeSegPapeleta.get("cod_mov_ini").toString().trim().equals(Constantes.MOV_TARDANZA_EFECTIVA)||
																existeSegPapeleta.get("cod_mov_ini").toString().trim().equals(Constantes.MOV_TARDANZA_SIN_DESCUENTO)){
															log.debug("Mov inasistencia.:");
															mAsis.put("hsal", mAsis.get("hing").toString());
															mAsis.put("hing", turno.getHoraIni());
														}else if ((existeSegPapeleta.get("cod_mov_ini").toString().trim().equals(Constantes.MOV_EXCESO_REFRIGERIO))||
																(existeSegPapeleta.get("cod_mov_ini").toString().trim().equals(Constantes.MOV_SALIDA_NO_AUTORIZADA))) {
															if((existeSegPapeleta.get("cod_mov_ini").toString().trim().equals(Constantes.MOV_EXCESO_REFRIGERIO))){																
																maxMinRefrigerio=turno.getMinutosRefrigerio();
																log.debug("maxMinRefrigerio: "+maxMinRefrigerio);														
															}else if((existeSegPapeleta.get("cod_mov_ini").toString().trim().equals(Constantes.MOV_SALIDA_NO_AUTORIZADA))){
																if (mAsis.get("hsal").toString().trim().equals("")){
																	mAsis.put("hsal", turno.getHoraFin());
																}
															}
														}else{
															log.debug("Mov otros.:");
															
														}
													}
												}
											minle = Utiles.obtenerMinutosDiferencia(mAsis.get("hing").toString(), mAsis.get("hsal").toString());
											T4819DAO daoBolsa = new T4819DAO(sl.getDataSource(dbpool));
											Integer saldo; 
											HashMap datos = new HashMap();
											datos.put("cod_pers", mAsis.get("cod_pers").toString());

											pe.gob.sunat.sp.dao.T02DAO empleadoDAO = new pe.gob.sunat.sp.dao.T02DAO();		//ICR 08/05/2015-PAS20155E230000154-labor antigua  (autorizacion y compensacion) y labor nueva (sol. compensacion) mayor o igual fecha ingreso a sunat
											Map mapaEmpleado = new HashMap();
											Date fecha_ingreso;	String regimen="";
											mapaEmpleado = empleadoDAO.findEmpleado(dbpool,mAsis.get("cod_pers").toString());	
											fecha_ingreso = (mapaEmpleado!=null && !mapaEmpleado.isEmpty())?((Date)mapaEmpleado.get("t02f_ingsun")):null;	
											datos.put("fecha_ingreso", fecha_ingreso);
											
											if((mAsis.get("mov").toString().trim().equals(Constantes.MOV_EXCESO_REFRIGERIO))||exRef.equals("SI")){
												regimen=(mapaEmpleado!=null && !mapaEmpleado.isEmpty())?mapaEmpleado.get("t02cod_rel").toString():"";
												if(regimen.equals("09")||regimen.equals("10"))
												minle=minle-maxMinRefrigerio;
											}
											
											if(log.isDebugEnabled()) log.debug("Masis:"+mAsis);
											saldo = daoBolsa.findSaldoLaborAutorizadas(datos);
											if(log.isDebugEnabled()) log.debug("Saldo:"+saldo.floatValue()+", FechaIng: "+turno.getFechaIni()+" ,MinAcumulados:"+minle);
											
											if (saldo.floatValue() < minle){
												//Si no tiene Saldo no se registra
												mensajes.add("No es posible registrar la compensación por horas por no contar con saldo suficiente (Registro: "+ mAsis.get("cod_pers").toString()+", Fecha y hora: "+ mAsis.get("fing").toString()+" "+ mAsis.get("hing").toString()+").");
												registrar = false;
											}											
											if(minle<limMinutos){
												mensajes.add("El número de minutos solicitados para la compensación por horas debe ser mayor o igual a "+limMinutos+" (Registro: "+ mAsis.get("cod_pers").toString()+", Fecha y hora: "+ mAsis.get("fing").toString()+" "+ mAsis.get("hing").toString()+").");
												registrar = false;
											}
											//CONTROLAR QUE LA FECHA DE MARCACION SEA POSTERIOR A 13/12/2012
											String[] fechaFormat=mAsis.get("fing").toString().split("-");
											String nuevaFecha=""+fechaFormat[2]+"/"+fechaFormat[1]+"/"+fechaFormat[0];
											log.debug("Nueva fecha:"+nuevaFecha);
											log.debug("Utiles.obtenerDiasDiferencia(fechaMinPap.toString(),nuevaFecha):"+Utiles.obtenerDiasDiferencia(fechaMinPap.toString(),nuevaFecha));
											//
											if(Utiles.obtenerDiasDiferencia(fechaMinPap.toString(),nuevaFecha)<0){
												mensajes.add("La fecha de la marcación no puede ser menor al "+fechaMinPap.toString());
												registrar = false;
											}
											
										}
										//modificarPapeleta
										if (registrar) {
											log.debug("Entrando a registrar 2do caso.");
											HashMap existeSegPapeleta=(HashMap)seguimPapeleta.findByPK(mAsis.get("cod_pers").toString(), mAsis.get("periodo").toString(),  mAsis.get("fing").toString(), mAsis.get("hing").toString());
											HashMap datosPap=new HashMap();
											log.debug("Existe1:"+existeSegPapeleta);
											if(existeSegPapeleta!=null && existeSegPapeleta.get("cod_personal")!=null)
											{
												modificarPapeleta(dbpool, mAsis.get("cod_pers").toString(), mAsis.get("periodo").toString(),
														mAsis.get("u_organ").toString(), Utiles.dateToString((Date)mAsis.get("fing")), 
														mAsis.get("hing").toString(), params[i].trim(), usuario, supervisor, observs[i]);
											
												log_papeleta.info("Modificar|".
														concat(mAsis.get("cod_pers").toString()).
														concat("|").
														concat(Utiles.dateToString((Date)mAsis.get("fing"))).
														concat("|").
														concat(mAsis.get("hing").toString()).
														concat("|").
														concat(mAsis.get("mov").toString()).
														concat("|").
														concat(params[i]));
													datosPap.put("hing", mAsis.get("hing").toString());												
											}else{
												registrarPapeleta(dbpool, mAsis.get("cod_pers").toString(), 
														mAsis.get("periodo").toString(),
														mAsis.get("u_organ").toString(),
														Utiles.dateToString((Date)mAsis.get("fing")),
														mAsis.get("hsal").toString(), 
														params[i].trim(), usuario, supervisor, observs[i]);
												log_papeleta.info("Modificar|".
														concat(mAsis.get("cod_pers").toString()).
														concat("|").
														concat(Utiles.dateToString((Date)mAsis.get("fing"))).
														concat("|").
														concat(mAsis.get("hing").toString()).
														concat("|").
														concat(mAsis.get("mov").toString()).
														concat("|").
														concat(params[i]));
												datosPap.put("hing", mAsis.get("hsal").toString());
											}
											HashMap existeSegPapeleta1=(HashMap)seguimPapeleta.findByPK(mAsis.get("cod_pers").toString(), mAsis.get("periodo").toString(),  mAsis.get("fing").toString(), datosPap.get("hing").toString());
											log.debug("Existe2:"+existeSegPapeleta1);	
											FechaBean fecAct = new FechaBean();
											DataSource dsSecuence= sl.getDataSource("java:/comp/env/jdbc/dcbdseq");
											int Secuencia = 0;
											Secuencia = new SequenceDAO().getSequence(dsSecuence,"SET9395");
											
											datosPap.put("secuencia",new Integer(Secuencia));
											datosPap.put("cod_pers", mAsis.get("cod_pers").toString());
											datosPap.put("periodo", mAsis.get("periodo").toString());
											datosPap.put("fing", mAsis.get("fing").toString());
											
											datosPap.put("mov", mAsis.get("mov").toString());
											datosPap.put("cod_mov_pap", params[i].trim());
											datosPap.put("estado_id", mAsis.get("estado_id").toString());
											datosPap.put("ind_del", "0");
											if(existeSegPapeleta1!=null && existeSegPapeleta1.get("cod_personal")!=null){												
												datosPap.put("cod_usumodif", mAsis.get("cod_pers").toString());
												datosPap.put("fec_modif", fecAct.getTimestamp());											
												seguimPapeleta.updateMovFin(datosPap);
											}else{												
												datosPap.put("cod_usuregis", mAsis.get("cod_pers").toString());
												datosPap.put("fec_regis", fecAct.getTimestamp());											
												seguimPapeleta.insertSeguimPapeleta(datosPap);
											}											
										}
									}
								}
							}
						}
					}
				}
			}

			if (mensajes.isEmpty()) {
				mensajes = null;
			}
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		return mensajes;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void registrarPapeleta(String dbpool, String codPers,
			String periodo, String uOrg, String fIng, String hIng,
			String codMov, String usuario, String supervisor, String observs)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {
			//PRAC-JCALLO
			//T1271CMPHome cmpHome = (T1271CMPHome) ServiceLocator.getInstance().getLocalHome(T1271CMPHome.JNDI_NAME);
			HashMap jefe = this.buscarTrabajadorJefe(dbpool,codPers,null);
			log.debug("jefe:"+jefe);
			T1271DAO asisdao = new T1271DAO(dbpool);
			
			Map params = new HashMap();
			//(cod_pers, periodo, uorgan, mov, fing, hing, autor_id, jefe_autor, estado_id, fcreacion, cuser_crea, obs_papeleta)
			params.put("cod_pers",codPers);
			params.put("periodo",periodo);
			//params.put("uorgan",uOrg);			
			params.put("fing",new FechaBean(fIng).getSQLDate());
			params.put("hing",hIng);
			
			params.put("mov",codMov);
			params.put("autor_id","0");
			//params.put("jefe_autor",codPers);
			params.put("estado_id",Constantes.PAPELETA_REGISTRADA);
			params.put("fmod",new FechaBean().getTimestamp());
			params.put("cuser_mod",usuario);
			params.put("obs_papeleta",observs);
			
			/*T1271CMPLocal cmpLocal = cmpHome
					.findByPrimaryKey(new T1271CMPPK(codPers,
							periodo, new BeanFechaHora(fIng).getSQLDate(), hIng));*/

			/*cmpLocal.setMov(codMov);
			cmpLocal.setObsPapeleta(observs);	
			cmpLocal.setEstadoId(Constantes.PAPELETA_REGISTRADA);
			cmpLocal.setAutorId("0");*/
			if (supervisor.equals("-")){
				T12DAO uoDAO = new T12DAO();
				HashMap pp = new HashMap();
				pp.put("dbpool",dbpool);
				pp.put("codOpcion",Constantes.DELEGA_SOLICITUDES);
				pp.put("codUO",(String)jefe.get("t12cod_uorg_jefe"));
				HashMap delegado = uoDAO.findDelegado(pp);
				if (delegado!=null && !delegado.isEmpty()){
					jefe.put("t12cod_jefe",delegado.get("t02cod_pers"));
				}
				//cmpLocal.setJefeAutor((String)jefe.get("t12cod_jefe")); //EBV 16/03/2006 Se incluye al Jefe
				params.put("jefe_autor",(String)jefe.get("t12cod_jefe"));
			}
			else
			{
				//cmpLocal.setJefeAutor(supervisor); //EBV 21/04/2006 Se incluye al supervisor
				params.put("jefe_autor",supervisor);
			}
			//cmpLocal.setFmod(new Timestamp(System.currentTimeMillis()));
			//cmpLocal.setCuserMod(usuario);
			log.debug("metodo registrarPapeleta ... params:"+params);
			//actualizando datos del registro en la tabla 1271asistencia
			asisdao.updateRegistroPapeleta(params);
			
			QueueDAO qDAO = new QueueDAO();
			T02DAO personalDAO = new T02DAO();
			CorreoDAO correoDAO = new CorreoDAO();
			
			String nombre = personalDAO.findNombreCompletoByCodPers(dbpool, codPers);
			String mensaje = "Usted tiene una nueva papeleta por autorizar del trabajador "+codPers+" - "+nombre+" para el dia "+ fIng + ".";
			
			ResourceBundle bundle = ResourceBundle.getBundle("pe.gob.sunat.sp.asistencia.sirh");
			String servidorIP = bundle.getString("servidorIP");
			String programa = bundle.getString("programa6");

			//String url = new MenuCliente().generaInvocacionLibre(programa);
			String url = new MenuCliente().generaInvocacionURL(programa,null,true,MenuCliente.INTRANET,"/ol-ti-iaasistencia/asisS02Alias");
			//String strURL = "http://"+servidorIP+"/cl-at-iamenu"+url;
			String strURL = "http://"+servidorIP+url;
			log.debug("URL "+url);
			log.debug("strURL "+strURL);
			
			String texto = Utiles.textoCorreoProceso(dbpool, (String)jefe.get("t12cod_jefe_desc"), mensaje, strURL);
			
			//enviamos el mail al trabajador
			HashMap datos = new HashMap();
			datos.put("subject","Papeleta Electronica");
			datos.put("message",texto);
			datos.put("from", correoDAO.findCorreoByCodPers(dbpool, codPers));
			if (supervisor.equals("-")){
				datos.put("to", correoDAO.findCorreoByCodPers(dbpool,(String)jefe.get("t12cod_jefe")));
			}
			else
			{
				nombre = personalDAO.findNombreCompletoByCodPers(dbpool, supervisor);
				texto = Utiles.textoCorreoProceso(dbpool, nombre, mensaje, strURL);
				datos.put("message",texto);
				datos.put("to", correoDAO.findCorreoByCodPers(dbpool,supervisor));
			}
			
			
			qDAO.sendMail(datos);	
			
		} 
		catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void modificarPapeleta(String dbpool, String codPers, String periodo, String uOrg,
			String fIng, String hIng, String codMov, String usuario, String supervisor, String observs)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {
			//PRAC-JCALLO
			/*T1271CMPHome cmpHome = (T1271CMPHome) ServiceLocator.getInstance().getLocalHome(T1271CMPHome.JNDI_NAME);

			T1271CMPLocal cmpLocal = cmpHome
					.findByPrimaryKey(new T1271CMPPK(codPers,
							periodo, new BeanFechaHora(fIng).getSQLDate(), hIng));*/
			T1271DAO asisdao = new T1271DAO(dbpool);
			Map params = new HashMap();
			
			params.put("cod_pers", codPers);
			params.put("periodo", periodo);
			params.put("u_organ", uOrg);
			params.put("fIng", fIng);
			params.put("hIng", hIng);
			log.debug("metodo modificarpapeleta ... params:"+params);
			Map papeleta = asisdao.findByKeyAsistencia(params);

			if ((papeleta.get("autor_id") == null || (papeleta.get("autor_id").toString().equals("0"))) && (papeleta.get("estado_id").toString().equals(Constantes.PAPELETA_REGISTRADA))) {
				papeleta.put("mov", codMov);//cmpLocal.setMov(codMov);
				papeleta.put("obs_papeleta", observs);//cmpLocal.setObsPapeleta(observs);
				papeleta.put("fmod",new FechaBean().getTimestamp());//cmpLocal.setFmod(new Timestamp(System.currentTimeMillis()));
				papeleta.put("cuser_mod",usuario);//cmpLocal.setCuserMod(usuario);
				//if (supervisor == null){
				log.debug("Supervisor "+ supervisor);
				if (supervisor !="-"){
					//cmpLocal.setJefeAutor(supervisor); //EBV 21/04/2006 Se incluye al supervisor
					params.put("jefe_autor", supervisor);
				}
				//actualizando registro asistencia (modificar papeleta)
				log.debug("papeleta : "+papeleta);
				asisdao.updateRegistroPapeleta(papeleta);
			} else {
				throw new IncompleteConversationalState(
						"La papeleta ya ha sido aceptada por lo que no puede ser modificada.");
			}
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void eliminarPapeleta(String dbpool, String codPers,
			String periodo, String uOrg, String fIng, String hIng,
			String usuario, String nvoMov)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {
			//PRAC-JCALLO
			/*T1271CMPHome cmpHome = (T1271CMPHome) ServiceLocator.getInstance().getLocalHome(T1271CMPHome.JNDI_NAME);

			T1271CMPLocal cmpLocal = cmpHome
					.findByPrimaryKey(new T1271CMPPK(codPers,
							periodo, new BeanFechaHora(fIng).getSQLDate(), hIng));*/
			
			T1271DAO asisdao = new T1271DAO(dbpool);
			Map params = new HashMap();
			params.put("cod_pers", codPers);
			params.put("periodo", periodo);
			params.put("u_organ", uOrg);
			params.put("fIng", fIng);
			params.put("hIng", hIng);
			
			params.put("mov", nvoMov);//cmpLocal.setMov(nvoMov);
			params.put("estado_id", Constantes.PRE_CALIFICACION_ASIS);//cmpLocal.setEstadoId(Constantes.PRE_CALIFICACION_ASIS);
			params.put("autor_id", "0");//cmpLocal.setAutorId("0");
			params.put("jefe_autor", "");//cmpLocal.setJefeAutor(null);
			//este campo fecha_autor se pune en NULO DESDE LA misma sentecia SQL (... fecha_autor = null)
			//params.put("fecha_autor", "");//cmpLocal.setFechaAutor(null);//duda como pasarle nulo//
			params.put("fmod", new FechaBean().getTimestamp());//cmpLocal.setFmod(new Timestamp(System.currentTimeMillis()));
			params.put("cuser_mod",usuario);//cmpLocal.setCuserMod(usuario);
			params.put("obs_papeleta","");//cmpLocal.setObsPapeleta("");
			log.debug("metodo eliminar papeleta... params"+params);
			asisdao.updateEliminarPapeleta(params);

		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public ArrayList calificarPapeletas(String dbpool, String[] params,
			ArrayList lista, String codJefe, String usuario)
			throws IncompleteConversationalState, RemoteException {
		
		BeanMensaje beanM = new BeanMensaje();
		ArrayList mensajes = new ArrayList();
		try {
			//PRAC-JCALLO
			//BeanAsistencia beanAsis = null;
			HashMap mAsis = null;

			MantenimientoFacadeHome facadeMantHome = (MantenimientoFacadeHome) sl.getRemoteHome(
							MantenimientoFacadeHome.JNDI_NAME,
							MantenimientoFacadeHome.class);
			MantenimientoFacadeRemote facadeMantRemote = facadeMantHome.create();

			String periodoCerrado = "";
			boolean bPeriodoCerrado = false;
			
			T1270DAO daoTurno = new T1270DAO();
			BeanTurnoTrabajo turno = null;
			float minle = 0;

			//prac-jcallo
			for (int i = 0; i < lista.size(); i++) {
				mAsis = (HashMap) lista.get(i);
				if (params[i] != null) {
					if (mAsis.get("estado_id") == null || !mAsis.get("estado_id").toString().trim().equals(params[i])) {
						bPeriodoCerrado = facadeMantRemote.periodoCerradoUOFecha(dbpool, Utiles.dateToString((Date)mAsis.get("fing")), Utiles.obtenerFechaActual(), mAsis.get("u_organ").toString() );
						if (bPeriodoCerrado){
							periodoCerrado = facadeMantRemote.periodoCerradoAFecha(dbpool, Utiles.dateToString((Date)mAsis.get("fing")));	
							mensajes.add("La papeleta del trabajador con registro "
									+ mAsis.get("cod_pers").toString()
									+ " del "
									+ Utiles.dateToString((Date)mAsis.get("fing"))
									+ " - "
									+ mAsis.get("hing").toString()
									+ " no puede ser "
									+ (params[i].trim().equals(Constantes.PAPELETA_ACEPTADA) ? "aceptada": "rechazada")
									+ " porque el periodo "
									+ periodoCerrado
									+ " se encuentra cerrado.");
						}
						else{
							
							
							try {
								//Papeleta Compensacion - Ver Saldos
								if ( (mAsis.get("mov").toString().trim().equals(Constantes.MOV_PAPELETA_COMPENSACION)) && (params[i].trim().equals(Constantes.PAPELETA_ACEPTADA))){
									//DTARAZONA - BUSCAMOS EL MOVIMIENTO INICIAL DE LA PAPELETA
									T9395DAO seguimPapeleta=new T9395DAO(sl.getDataSource(dbpool));
									HashMap existeSegPapeleta=(HashMap)seguimPapeleta.findByPK(mAsis.get("cod_pers").toString(), mAsis.get("periodo").toString(),  mAsis.get("fing").toString(), mAsis.get("hing").toString());
									if(log.isDebugEnabled()) log.debug("Encontró?:"+existeSegPapeleta);
									String movOriginal="";
									boolean procesar=true;
									float maxMinRefrigerio=0;
									if(existeSegPapeleta!=null){
										turno = daoTurno.joinWithT45ByCodFecha(dbpool, mAsis.get("cod_pers").toString(), Utiles.dateToString((Date)mAsis.get("fing")));
										movOriginal=existeSegPapeleta.get("cod_mov_ini").toString().trim();
										if(log.isDebugEnabled()) log.debug("Movimiento Original:"+movOriginal);
										
										pe.gob.sunat.sp.dao.T02DAO empleadoDAO = new pe.gob.sunat.sp.dao.T02DAO();		//ICR 08/05/2015-PAS20155E230000154-labor antigua  (autorizacion y compensacion) y labor nueva (sol. compensacion) mayor o igual fecha ingreso a sunat
										Map mapaEmpleado = new HashMap();
										String regimen="";boolean cruceRecuparacion=false;
										Date fecha_ingreso;	
										mapaEmpleado = empleadoDAO.findEmpleado(dbpool,mAsis.get("cod_pers").toString());	
										fecha_ingreso = (mapaEmpleado!=null && !mapaEmpleado.isEmpty())?((Date)mapaEmpleado.get("t02f_ingsun")):null;
										regimen=(mapaEmpleado!=null && !mapaEmpleado.isEmpty())?mapaEmpleado.get("t02cod_rel").toString():"";
										
										maxMinRefrigerio=turno.getMinutosRefrigerio();
										log.debug("maxMinRefrigerio: "+maxMinRefrigerio);
										
										if(movOriginal.equals(Constantes.MOV_TARDANZA_CON_DESCUENTO) || movOriginal.equals(Constantes.MOV_INASISTENCIA) || 
										   movOriginal.equals(Constantes.MOV_SALIDA_NO_AUTORIZADA) || movOriginal.equals(Constantes.MOV_EXCESO_REFRIGERIO))
										{//MOVIMIENTOS DESCONTABLES
											if(movOriginal.equals(Constantes.MOV_SALIDA_NO_AUTORIZADA) || movOriginal.equals(Constantes.MOV_EXCESO_REFRIGERIO)){
												if(movOriginal.equals(Constantes.MOV_EXCESO_REFRIGERIO)){
													
													if(Utiles.obtenerMinutosDiferencia(turno.getHoraFin(), mAsis.get("hsal").toString())>0){//MARCACION FINAL ES MAYOR A LA HORA FIN DEL TURNO
														minle = Utiles.obtenerMinutosDiferencia(mAsis.get("hing").toString(), turno.getHoraFin());
														cruceRecuparacion=true;//VERIFICAR RECUPERACIÓN
													}
													else{
														minle = Utiles.obtenerMinutosDiferencia(mAsis.get("hing").toString(), mAsis.get("hsal").toString());
													}
													
													//ANALIZAR SI TIENE MAS MOVIMIENTOS DE REFRIGERIO Y/O EXCESO DE REFRIGERIO EN EL DÍA
													T1271DAO t1271dao=new T1271DAO(dbpool);
													HashMap param=new HashMap();
													param.put("cod_pers", mAsis.get("cod_pers").toString());
													param.put("fecha", Utiles.dateToString((Date)mAsis.get("fing")));
													List movimientos=t1271dao.findByCodPersFecha(param);
													int minOtrosRefrig=0;
													boolean hayOtrosER=false,hayPapCompensaRef=false;
													int indicePap=0,indiceExRef=0,indiceCompER=0;
													for(int a=0;a<movimientos.size();a++){
														HashMap movimiento=(HashMap)movimientos.get(a);
														if(log.isDebugEnabled()) log.debug("movimiento "+a+": "+movimiento.get("hing").toString());
														if(movimiento.get("hing").toString().trim().equals(mAsis.get("hing").toString().trim()))
															indicePap=a;
														
														if(movimiento.get("mov").toString().trim().equals(Constantes.MOV_REFRIGERIO))
															minOtrosRefrig+=Utiles.obtenerMinutosDiferencia(movimiento.get("hing").toString(), movimiento.get("hsal").toString());
														if(movimiento.get("mov").toString().trim().equals(Constantes.MOV_EXCESO_REFRIGERIO) && !movimiento.get("hing").toString().trim().equals(mAsis.get("hing").toString().trim())  && !hayOtrosER){
															hayOtrosER=true;
															indiceExRef=a;
														}
														
														String movOrig="";
														if(movimiento.get("mov").toString().trim().equals(Constantes.MOV_PAPELETA_COMPENSACION) && movimiento.get("estado_id").toString().trim().equals("2")){ //VERIFICAR SI HAY PAPELETA DE COMP. 
															HashMap mapMovOrig=(HashMap)seguimPapeleta.findByPK(mAsis.get("cod_pers").toString(), movimiento.get("periodo").toString(),  movimiento.get("fing").toString(), movimiento.get("hing").toString());
															if(mapMovOrig!=null)
																movOrig=mapMovOrig.get("cod_mov_ini").toString().trim();
															if(movOrig.equals(Constantes.MOV_EXCESO_REFRIGERIO)){
																hayPapCompensaRef=true;
																indiceCompER=a;
															}
														}
													}
													if(log.isDebugEnabled()) log.debug("Otros refrigerios:"+minOtrosRefrig+", Hay Pap:"+hayPapCompensaRef+"- Pos ER - Pap:"+indiceExRef+"-"+indicePap);
													
													if(regimen.equals("09")||regimen.equals("10")){
														if((!hayPapCompensaRef || indiceCompER>indicePap) && (!hayOtrosER || indiceExRef>=indicePap))
															minle=minOtrosRefrig+minle-maxMinRefrigerio;
													}
												}else{
													if(mAsis.get("hsal").toString().trim().equals("")){//NO TIENE MARCACION FINAL
														if(Utiles.obtenerMinutosDiferencia(mAsis.get("hing").toString(), turno.getHoraIni())>0 || Utiles.obtenerMinutosDiferencia(turno.getHoraFin(), mAsis.get("hing").toString())>0){ //SI HORA INICIAL ES MENOOR A LA INICIO DEL TURNO/HORA INICIAL ES MAYOR A LA HORA FIN DEL TURNO
															minle = Utiles.obtenerMinutosDiferencia(turno.getHoraIni(), turno.getHoraFin());
														}else{
															minle = Utiles.obtenerMinutosDiferencia(mAsis.get("hing").toString(), turno.getHoraFin());
														}
														cruceRecuparacion=true;
													}else{//TIENE MARCACION FINAL
														if(Utiles.obtenerMinutosDiferencia(turno.getHoraFin(), mAsis.get("hsal").toString())>0){//MARCACION FINAL ES MAYOR A LA HORA FIN DEL TURNO
															if(Utiles.obtenerMinutosDiferencia(mAsis.get("hing").toString(), turno.getHoraIni())>0){ //SI HORA INICIAL ES MENOOR A LA inicio DEL TURNO
																minle = Utiles.obtenerMinutosDiferencia(turno.getHoraIni(), turno.getHoraFin());
															}else{
																if(Utiles.obtenerMinutosDiferencia(turno.getHoraFin(),mAsis.get("hing").toString())>0)
																	mensajes.add("No es posible procesar su papeleta porque las marcaciones están fuera de su horario de trabajo.");
																else{
																	minle = Utiles.obtenerMinutosDiferencia(mAsis.get("hing").toString(), turno.getHoraFin());
																}
															}
															cruceRecuparacion=true;//VERIFICAR RECUPERACIÓN
														}else{
															if(Utiles.obtenerMinutosDiferencia(mAsis.get("hing").toString(), turno.getHoraIni())>0){ //SI HORA INICIAL ES MENOOR A LA HORA INI DEL TURNO
																if(Utiles.obtenerMinutosDiferencia(mAsis.get("hsal").toString(),turno.getHoraIni())>0)
																	mensajes.add("No es posible procesar su papeleta porque las marcaciones están fuera de su horario de trabajo.");
																else{
																	minle = Utiles.obtenerMinutosDiferencia(turno.getHoraIni(), mAsis.get("hsal").toString());
																}
															}else{
																minle = Utiles.obtenerMinutosDiferencia(mAsis.get("hing").toString(), mAsis.get("hsal").toString());
															}
														}
													}
												}
											}else{
												if(Utiles.obtenerMinutosDiferencia(turno.getHoraFin(), mAsis.get("hing").toString())>0){ //SI HORA INICIAL ES MAYOR A LA HORA FIN DEL TURNO
													minle = Utiles.obtenerMinutosDiferencia(turno.getHoraIni(), turno.getHoraFin());
												}else{
													minle = Utiles.obtenerMinutosDiferencia(turno.getHoraIni(), mAsis.get("hing").toString());
												}
											}
										}else{//MOVIMIENTOS NO DESCONTABLES
											if(mAsis.get("hsal").toString().trim().equals(""))//NO TIENE MARCACION FINAL
												mensajes.add("No es posible procesar su papeleta porque solo tiene una marcación.");
											else //TIENE MARCACIÓN FINAL
											minle = Utiles.obtenerMinutosDiferencia(mAsis.get("hing").toString(), mAsis.get("hsal").toString());
										}
										//GET SALDO
										T4819DAO daoBolsa = new T4819DAO(sl.getDataSource(dbpool));
										HashMap datos = new HashMap();
										datos.put("cod_pers", mAsis.get("cod_pers").toString());
										
										//MINUTOS MINIMO PARA COMPENSAR
										T99DAO codigoDAO=new T99DAO();
										int limMinutos=Integer.parseInt(codigoDAO.findParamByCodTabCodigo(dbpool, "510", "50"));
										log.debug("minMinimo:"+limMinutos);
										
										datos.put("fecha_ingreso", fecha_ingreso);
										
										Integer saldo = daoBolsa.findSaldoLaborAutorizadas(datos);
										
										if(cruceRecuparacion){//VALIDAR QUE TENGA DIAS DE RECUPERACIÓN POR FERIADO COMPENSABLE
											T1270DAO dao = new T1270DAO();	
											int horasCompensa = dao.findHorasCompensa(dbpool,mAsis.get("cod_pers").toString(),Utiles.dateToString((Date)mAsis.get("fing"))); 
											if (log.isDebugEnabled()) log.debug("horasCompensa-CAS:"+horasCompensa+"- FechaEvaluar:"+Utiles.dateToString((Date)mAsis.get("fing")));
											minle+=horasCompensa;
										}
										if(log.isDebugEnabled()) log.debug("Minutos a procesar:"+minle);
										
										//VALIDAR QUE MINUTOS SOLICITADOS SEAN MENOR A SALDO Y MAYOR A 180
										if(minle>saldo.floatValue()){
											mensajes.add("No es posible registrar la compensación por horas por no contar con saldo suficiente (Registro: "+ mAsis.get("cod_pers").toString()+", Fecha y hora: "+ mAsis.get("fing").toString()+" "+ mAsis.get("hing").toString()+").");
											procesar=false;
										}
										if(minle<limMinutos){
											mensajes.add("El número de minutos solicitados para la compensación por horas debe ser mayor o igual a "+limMinutos+" (Registro: "+ mAsis.get("cod_pers").toString()+", Fecha y hora: "+ mAsis.get("fing").toString()+" "+ mAsis.get("hing").toString()+").");
											procesar=false;
										}
										
										//CUMPLE TODAS LAS REGLAS PARA APROBAR
										if(procesar){
											alternarVoBoPapeleta(dbpool, mAsis.get("cod_pers").toString(), mAsis.get("periodo").toString(),
													mAsis.get("u_organ").toString(), Utiles.dateToString((Date)mAsis.get("fing")), mAsis.get("hing").toString(),
															params[i], codJefe, usuario, mAsis.get("mov").toString(),minle);
											
										}
									}else
									{
										mensajes.add("La papeleta "+mAsis.get("mov").toString().trim()+" de fecha: "+mAsis.get("fing").toString().trim()+" y hora: "+mAsis.get("hing").toString().trim()+" no se pudo aprobar porque no es posible encontrar su movimiento inicial.");
									}
									//FIN DTARAZONA									
								} else {
									/*alternarVoBoPapeleta(dbpool, beanAsis
											.getCodPers(), beanAsis.getPeriodo(),
											beanAsis.getUOrgan(), beanAsis
													.getFIng(), beanAsis.getHIng(),
													params[i], codJefe, usuario, beanAsis.getMov(),0);*/
									alternarVoBoPapeleta(dbpool, mAsis.get("cod_pers").toString(), mAsis.get("periodo").toString(),
											mAsis.get("u_organ").toString(), Utiles.dateToString((Date)mAsis.get("fing")), mAsis.get("hing").toString(),
													params[i], codJefe, usuario, mAsis.get("mov").toString(),0);
								}
							} catch (Exception ex) {
								mensajes.add(ex.getMessage());
							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return mensajes;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void alternarVoBoPapeleta(String dbpool, String codPers,
			String periodo, String uOrg, String fIng, String hIng,
			String visto, String codJefe, String usuario, String mov, float minle)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {
			//UPDATE...APROBAR O NO
			T02DAO personalDAO = new T02DAO();
			CorreoDAO correoDAO = new CorreoDAO();

			String mensaje = "";
			//LICENCIA POR DIA DE COMPENSACION
			if (mov.trim().equalsIgnoreCase(Constantes.MOV_PAPELETA_COMPENSACION)) {
				//Calculamos la cantidad de minutos a compensar
				
				//DTARAZONA 
				T4819DAO t4819DAO = new T4819DAO(dbpool);//VERIFICAR CONEXION
				T4820DAO t4820DAO = new T4820DAO(dbpool);
				Map datos1 = new HashMap();
				FechaBean fecAct = new FechaBean();
				pe.gob.sunat.sp.dao.T02DAO empleadoDAO = new pe.gob.sunat.sp.dao.T02DAO();		//ICR 08/05/2015-PAS20155E230000154-labor antigua  (autorizacion y compensacion) y labor nueva (sol. compensacion) mayor o igual fecha ingreso a sunat
				Map mapaEmpleado = new HashMap();
				Date fecha_ingreso;	
				mapaEmpleado = empleadoDAO.findEmpleado(dbpool,codPers);	
				fecha_ingreso = (mapaEmpleado!=null && !mapaEmpleado.isEmpty())?((Date)mapaEmpleado.get("t02f_ingsun")):null;	
				datos1.put("fecha_ingreso", fecha_ingreso);										//ICR 08/05/2015 - fecha de ingreso no puede ser null
								
				datos1.put("cod_pers", codPers);
				
				List fechas = t4819DAO.findHorasLaborAutorizadasConSaldo(datos1);
				if(log.isDebugEnabled()) log.debug("Datos:"+datos1);
				if(log.isDebugEnabled()) log.debug("Fechas de la bolsa:"+fechas);
				int tempnum = 0;
				Map detalle = new HashMap();
				if (fechas != null && fechas.size() >0){
					Map he = new HashMap();
					int minutos = 0;
					int i=0;
					
					detalle = new HashMap();
					detalle.put("ann_sol",periodo);
					detalle.put("num_sol","0");
					detalle.put("cod_uo_sol",uOrg);
					detalle.put("cod_pers_sol",codPers);
					detalle.put("cod_mov_sol",mov);
					detalle.put("fec_registro_sol",Utiles.stringToTimestamp(fIng+" "+hIng));
					detalle.put("num_detalle",new Integer(0));
					log.debug("Utiles.stringToTimestamp(fIng+hIng):"+Utiles.stringToTimestamp(fIng+" "+hIng));
					fechas = t4819DAO.findHorasLaborAutorizadasConSaldo(datos1);						
					//minutos = 480;
					minutos = (int)minle;
					while (minutos>0){
						if (fechas != null && fechas.size() >0){
							he = (HashMap) fechas.get(i);
						}								
						if(he!=null && !he.isEmpty()){//adicionado 29/05/2012
							if (minutos >= Integer.parseInt(he.get("cnt_min_comp_sal").toString())){								
								//Actualizar a cero ese dia y obtener nueva fecha
								tempnum = 0;										
								he.put("tempnum", new Integer(tempnum));
								he.put("usuario", usuario);
								he.put("indicador", "3");
								he.put("min_usado", he.get("cnt_min_comp_sal").toString());
								t4819DAO.updateLabor(he);
								if (detalle != null && !detalle.isEmpty()){
									t4820DAO.insertaDetalle(detalle,he);
								}									
								minutos = minutos - Integer.parseInt(he.get("cnt_min_comp_sal").toString());
								i = i + 1;
							} else {
									//Actualizar dias y a cero el dia									
									tempnum =  Integer.parseInt(he.get("cnt_min_comp_sal").toString()) - minutos;	
									he.put("tempnum", new Integer(tempnum));
									he.put("usuario", usuario);
									he.put("indicador", "1");
									he.put("min_usado", String.valueOf(minutos));
									t4819DAO.updateLabor(he);
									if (detalle != null && !detalle.isEmpty()){
										t4820DAO.insertaDetalle(detalle,he);
									}										
									minutos = 0;
								}
						}//adicionado 29/05/2012								
					}					
				}				
				//FIN DTARAZONA
			}
			//PRAC-JCALLO
			/*T1271CMPHome cmpHome = (T1271CMPHome) ServiceLocator.getInstance().getLocalHome(T1271CMPHome.JNDI_NAME);

			T1271CMPLocal cmpLocal = cmpHome
					.findByPrimaryKey(new T1271CMPPK(codPers,
							periodo, new BeanFechaHora(fIng).getSQLDate(), hIng));*/
			//JRR - 30/01/2009
			T3701DAO t3701dao = new T3701DAO(ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dgsp"));
			Map mnovedad = new HashMap();
			Map map_aux = null;
			//
			
			T1271DAO asisdao = new T1271DAO(dbpool);
			HashMap params = new HashMap();
			params.put("cod_pers", codPers);
			params.put("periodo", periodo);
			params.put("u_organ", uOrg);
			params.put("fIng", fIng);
			params.put("hIng", hIng);
			log.debug("metodo alternarVoBoPapeleta .. params:"+params);
			Map papeleta = asisdao.findByKeyAsistencia(params); 

			String mensaje1  = papeleta.get("obs_papeleta").toString();//cmpLocal.getObsPapeleta();
			mensaje1 = mensaje1.trim() + " " + mensaje;
			papeleta.put("obs_papeleta", mensaje1);//cmpLocal.setObsPapeleta(mensaje1);
			papeleta.put("estado_id", visto);//cmpLocal.setEstadoId(visto);
			FechaBean fecAct = new FechaBean();
			papeleta.put("fecha_autor", fecAct.getTimestamp());//cmpLocal.setFechaAutor(new Timestamp(System.currentTimeMillis()));
			papeleta.put("jefe_autor", codJefe);//cmpLocal.setJefeAutor(codJefe); //EBV 16/03/2006 Iba el usuario...
			//EBV 22/08/2007 el rechazo 
			if (visto.equals(Constantes.PAPELETA_RECHAZADA)) {
				papeleta.put("mov", Constantes.MOV_PAPELETA_NO_AUTORIZADA);//cmpLocal.setMov(Constantes.MOV_PAPELETA_NO_AUTORIZADA);
				papeleta.put("estado_id", Constantes.PRE_CALIFICACION_ASIS); 				//jquispecoi 05/2014
				papeleta.put("obs_papeleta", "*PAPELETA RECHAZADA*\n" + mensaje1);			//
			}
			papeleta.put("fmod",  fecAct.getTimestamp());//cmpLocal.setFmod(new Timestamp(System.currentTimeMillis()));
			papeleta.put("cuser_mod",usuario);//cmpLocal.setCuserMod(usuario);
			
			//actualizando 11271asistencia
			log.debug("params : "+params+"      papeleta:"+papeleta);
			asisdao.updateAsistenciaFechaAutor(papeleta);
			
			if (visto.equals(Constantes.PAPELETA_RECHAZADA)){ 		//jquispecoi 05/2014 - Llamamos a Generaciï¿½n de Asistencia
				T99DAO codigoDAO = new T99DAO();
				String fechaIniNSA = codigoDAO.findParamByCodTabCodigo(dbpool,Constantes.CODTAB_PARAMETROS_ASISTENCIA,Constantes.FECHA_INICIO_SIRH_NSA);
				HashMap fechas = new HashMap();
				fechas.put("FechaNSA",fechaIniNSA);
				
				DataSource dcsp = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dcsp");
				pe.gob.sunat.rrhh.dao.T02DAO t02dao = new pe.gob.sunat.rrhh.dao.T02DAO(dcsp);
				HashMap beanPersona = (HashMap)t02dao.findByRegistro((String)papeleta.get("cod_pers"));
				beanPersona.put("t02cod_pers", papeleta.get("cod_pers"));
				beanPersona.put("t02cod_uorg", beanPersona.get("cod_uorg"));
				
				HashMap mapa=new HashMap();
				mapa.put("dbpool", "jdbc/dgsp");
				mapa.put("fechaEval",papeleta.get("fing_desc"));
				mapa.put("periodo", papeleta.get("periodo"));
				
				Map superMapa = new HashMap();
				superMapa.put("mapa", mapa);
				superMapa.put("beanPersona", beanPersona);
				superMapa.put("fechas", fechas);
				superMapa.put("usuario", usuario);
				
				String t02cod_rel=(String)beanPersona.get("t02cod_rel");
				
				ProcesoFacade pf=new ProcesoFacade();
				
				log.debug("superMapa--"+superMapa);
				if(t02cod_rel.equals("09")) {
					pf.generarAsistenciaTrabajadorCAS(superMapa);
				} else if(t02cod_rel.equals("10")) {
					pf.generarAsistenciaTrabajadorModFormativa(superMapa);
				} else {
					pf.generarAsistenciaTrabajador(mapa,beanPersona,fechas,usuario);
				}
			}												//jquispecoi fin
			
			String origen = codJefe;
			String destino = codPers;

			String estado = "";
			if (visto.equals(Constantes.PAPELETA_ACEPTADA)) {
				estado = "aceptada";
				
				//JRR - 30/01/2009
				mnovedad.put("cod_pers", codPers);
				mnovedad.put("ind_proceso", "1");
				mnovedad.put("cod_usucrea", usuario);
				mnovedad.put("fec_creacion", fecAct.getTimestamp());
				mnovedad.put("fec_refer", new FechaBean(fIng).getSQLDate());

				map_aux = t3701dao.findNovedadByTrabFec(mnovedad); 
				if(map_aux!=null){
					mnovedad.put("ind_proc_cerrado", "2");
					t3701dao.actualizarNovedadCerrada(mnovedad);
				}
				else
					t3701dao.registrarNovedad(mnovedad);
				//
				
			}
			if (visto.equals(Constantes.PAPELETA_RECHAZADA)) {
				estado = "rechazada";
			}

			if (!estado.equals("")) {				
				
				log_papeleta.info("Autorizar|Jefe:".
						concat(codJefe).
						concat("|Registro:").
						concat(codPers).
						concat("|").
						concat(fIng).
						concat("|").
						concat(hIng).
						concat("|").
						concat(estado)
				);
				
				String mensaje2 = "Su papeleta del dia " + fIng + " ha sido "+ estado + ".";

				/*ResourceBundle bundle = ResourceBundle.getBundle("pe.gob.sunat.sp.asistencia.sirh");
				String servidorIP = bundle.getString("servidorIP");
				String strURL = "http://"+servidorIP+"/asistencia/asisS13Alias";
				String paramsURL = "accion=cargarPapeletas&codPers="+codPers+"&fecha="+fIng;
				String programa = bundle.getString("programa3");
				Cifrador cifrador = new Cifrador();
				String url = cifrador.encriptaURL(codPers, programa, strURL, paramsURL);*/

				String nombre = personalDAO.findNombreCompletoByCodPers(dbpool,	destino);
				String texto = Utiles.textoCorreoProceso(dbpool, nombre, mensaje2,"");

				//enviamos el mail al trabajador
				/*HashMap*/ Map datos = new HashMap();
				datos.put("subject", "Papeletas Electronicas");
				datos.put("message", texto);
				datos.put("from", correoDAO.findCorreoByCodPers(dbpool,	origen));
				datos.put("to", correoDAO.findCorreoByCodPers(dbpool, destino));

//				QueueDAO queue = new QueueDAO();
//				queue.enviaCorreo(datos);
				
				//JRR
				Correo correo = new Correo(datos.get("message").toString());
	    		correo.setAsunto((String)datos.get("subject"));
	    		correo.setRemitente((String)datos.get("from"), "AsistenciaSIRH");    		
	    		correo.agregarDestinatario((String)datos.get("to"));    		
	    		correo.enviarHtml();
				//
				
			}
			else{
				log_papeleta.info("Autorizar|Jefe:".
						concat(codJefe).
						concat("|Registro:").
						concat(codPers).
						concat("|").
						concat(fIng).
						concat("|").
						concat(hIng).
						concat("|registrada")
				);
			}

		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

	}
    
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public ArrayList calificarAsistencias(String dbpool, String[] params,
			ArrayList lista, String usuario, String fecha)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		ArrayList mensajes = new ArrayList();
		try {

			ProcesoFacadeHome facadeHome = (ProcesoFacadeHome) sl.getRemoteHome(ProcesoFacadeHome.JNDI_NAME,
					ProcesoFacadeHome.class);
			ProcesoFacadeRemote facadeRemote = facadeHome.create();
			
			MantenimientoFacadeHome mantHome = (MantenimientoFacadeHome) sl.getRemoteHome(
							MantenimientoFacadeHome.JNDI_NAME,
							MantenimientoFacadeHome.class);
			MantenimientoFacadeRemote mantenimiento = mantHome.create();
			log.debug("fechaqqqq"+fecha);
			String periodo = mantenimiento.periodoCerradoAFecha(dbpool, fecha);

			if (!periodo.equals("")) {
				throw new IncompleteConversationalState("El periodo " + periodo
						+ " se encuentra cerrado.");
			} else {

				HashMap mAsis = null;
				//PRAC-JCALLO de aqui para abajo todo lo de beanAsistencia por hasmap
				T1271DAO dao = new T1271DAO(dbpool);
				String nvoMov = "";

				if (lista != null && lista.size() > 0) {
					mAsis = (HashMap) lista.get(0);
					Map prms = new HashMap();
					prms.put("cod_pers", mAsis.get("cod_pers").toString());
					prms.put("fecha", Utiles.dateToString((Date)mAsis.get("fing")));
					log.debug("metodo calificarAsistencias... prms: "+prms);
					if (dao.findByCodPersFFinNull(prms)) {
						throw new IncompleteConversationalState(
								"No se puede registrar una calificacion en un dia con marcaciones impares,"
										+ " primero debe ser registrada la marcacion faltante.");
					}
				}

				boolean tieneRefrigerio = false;
				String msg = "";
				String tipoMov = "";
				String reloj1 = "";
				String reloj2 = "";
				T1275DAO marcaDAO = new T1275DAO();
				BeanMarcacion bMarca = null;
				int errores = -1;
				T1270DAO daoTurno = new T1270DAO();
				BeanTurnoTrabajo turno = null;
				int numEntr = 0;
				int numSali = 0;
				int numRefr = 0;
				T1279DAO daoTipo = new T1279DAO();

				if (lista != null && lista.size() > 0) {
					mAsis = (HashMap) lista.get(0);
					turno = daoTurno.joinWithT45ByCodFecha(dbpool, mAsis.get("cod_pers").toString(), Utiles.dateToString((Date)mAsis.get("fing")));
					if (turno  == null) {
						throw new IncompleteConversationalState(
								"El trabajador no tiene Turno asignado. Favor Comunicarse con el Analista.");
					}
					for (int i = 0; i < lista.size(); i++) {
						mAsis = (HashMap) lista.get(i);

						//Validamos que solo tenga una calificacion de entrada.
						if (params[i] != null
								&& daoTipo.findByFlags(dbpool, params[i], "0",
										false)) {
							numEntr++;
						}
						if (numEntr > 1) {
							throw new IncompleteConversationalState(
									"El dia solo puede tener una calificacion de entrada.");
						}

						//Validamos que solo tenga una calificaciÃ¯Â¿Â½n de
						// refrigerio.
						if (params[i] != null
								&& daoTipo.findByFlags(dbpool, params[i], "1",
										false)) {
							numRefr++;
						}
						if (numRefr > 1) {
							throw new IncompleteConversationalState(
									"El dia solo puede tener una calificacion de refrigerio.");
						}

						//Validamos que solo tenga una calificaciÃ¯Â¿Â½n de salida.
						if (params[i] != null
								&& daoTipo.findByFlags(dbpool, params[i], "2",
										false)) {
							numSali++;
						}
						if (numSali > 1) {
							throw new IncompleteConversationalState(
									"El dia solo puede tener una calificacion de salida.");
						}

					}
				}

				for (int i = 0; i < lista.size(); i++) {
					mAsis = (HashMap) lista.get(i);
					msg = "";
					tipoMov = "";
					reloj1 = "";
					reloj2 = "";

					if (params[i] != null) {
						if (i == 0) {
							tipoMov = "0";
							nvoMov = Constantes.ENTRADA_NORMAL;
						} else if (i == (lista.size() - 1)) {
							tipoMov = "2";
							nvoMov = Constantes.SALIDA_NORMAL;
						} else {
							tipoMov = "3";
							nvoMov = Constantes.MOV_EN_OBSERVACION;
						}

						if (params[i].equals("-1")) {
							/*if (mAsis.get("mov") != null) {
								//Obtenemos el codigo del movimiento pre
								// calificado
								if (turno.getHoraIni().trim().equals("")) {
									nvoMov = Constantes.MOV_EN_OBSERVACION;
								} else {
									

									if (tipoMov.equals("0")) {
										nvoMov = facadeRemote
												.preCalificarEntrada(Utiles.dateToString((Date)mAsis.get("fing"))
														, mAsis.get("hing").toString(), turno
														.getHoraIni(), turno
														.getTolera(), turno
														.getHoraLimite(),
														Constantes.MINUTO);
									} else if (tipoMov.equals("2")) {
										nvoMov = facadeRemote
												.preCalificarSalida(
														dbpool,
														mAsis.get("cod_pers").toString(),
														Utiles.dateToString((Date)mAsis.get("fing")), 
														mAsis.get("hsal").toString(), 
														turno.getHoraFin(),
														Constantes.MINUTO);
									} else if (tipoMov.equals("3")) {
										//Obtenemos el primer reloj
										bMarca = marcaDAO
												.findByCodPersFechaHora(dbpool,
														beanAsis.getCodPers(),
														beanAsis.getFIng(),
														beanAsis.getHIng());
										reloj1 = bMarca.getReloj();

										//Obtenemos el segundo reloj
										bMarca = marcaDAO
												.findByCodPersFechaHora(dbpool,
														beanAsis.getCodPers(),
														beanAsis.getFIng(),
														beanAsis.getHSal());
										reloj2 = bMarca.getReloj();
										nvoMov = facadeRemote
												.preCalificarMovimiento(
														reloj1,
														reloj2,
														beanAsis.getFIng(),
														beanAsis.getHIng(),
														beanAsis.getHSal(),
														turno.getHoraIniRefrigerio(),
														turno.getHoraFinRefrigerio(),
														turno.getMinutosRefrigerio(),
														Constantes.MINUTO,
														tieneRefrigerio,
														turno.isOperativo());
										//EBV 14/08/2007
										//Cuando no pone movimiento se deja el actual
										nvoMov = mAsis.get("mov").toString().trim();
										//EBV 14/08/2007
									}
								
								}

								//eliminarPapeleta
								//EBV 25/02/2008
								eliminarPapeleta(dbpool, beanAsis.getCodPers(),
										beanAsis.getPeriodo(), beanAsis
												.getUOrgan(), beanAsis
												.getFIng(), beanAsis.getHIng(),
										usuario, nvoMov);
										
							}*/
						} else {
							if ((mAsis.get("mov") == null)
                                                            || (mAsis.get("mov") != null &&
                                                            		mAsis.get("estado_id").toString().trim().equals(Constantes.PRE_CALIFICACION_ASIS)
									/*
									 * (beanAsis.getMov().trim().
									 * equals(Constantes. ENTRADA_NORMAL) ||
									 * beanAsis.getMov().trim().
									 * equals(Constantes. MOVIMIENTO_NORMAL) ||
									 * beanAsis.getMov().trim().
									 * equals(Constantes. SALIDA_NORMAL))
									 */&& !mAsis.get("mov").toString().trim().equals(params[i].trim()))) {

								if (params[i]
										.equals(Constantes.MOV_OTRO_EDIFICIO)) {
									//Obtenemos el primer reloj
									bMarca = marcaDAO.findByCodPersFechaHora(
											dbpool, mAsis.get("cod_pers").toString(),
											Utiles.dateToString((Date)mAsis.get("fing")), mAsis.get("hing").toString());
									reloj1 = bMarca.getReloj();

									//Obtenemos el segundo reloj
									bMarca = marcaDAO.findByCodPersFechaHora(
											dbpool, mAsis.get("cod_pers").toString(),
											Utiles.dateToString((Date)mAsis.get("fing")), mAsis.get("hsal").toString());
									reloj2 = bMarca.getReloj();
								}

								msg = validarMovimiento(dbpool, mAsis.get("cod_pers").toString(),
										Utiles.dateToString((Date)mAsis.get("fing")),
										mAsis.get("hing").toString(), mAsis.get("hsal").toString(),
										tipoMov, params[i].trim(),
										tieneRefrigerio, reloj1, reloj2, turno);

								if (msg.trim().equals("")) {
									//registrarCalificacion
									registrarCalificacion(dbpool, mAsis.get("cod_pers").toString(), mAsis.get("periodo").toString(),
											mAsis.get("u_organ").toString(), Utiles.dateToString((Date)mAsis.get("fing")), mAsis.get("hing").toString(),
											params[i].trim(), usuario);
									
									if (mAsis.get("mov") != null && daoTipo.findByFlags(dbpool,params[i].trim(),"2", true)) {
										tieneRefrigerio = true;
									}
								}

							} else {
								if (!params[i].trim().equals(
										mAsis.get("mov").toString().trim())) {
									if (!(mAsis.get("estado_id").toString().trim().equals(Constantes.PRE_CALIFICACION_ASIS) || 

											//mAsis.get("estado_id").toString().trim().equals(Constantes.PAPELETA_REGISTRADA) ||
											
											//EBV - 09072008 - Papeletas 
											mAsis.get("estado_id").toString().trim().equals(Constantes.PAPELETA_PROCESADA) ||
											
											mAsis.get("estado_id").toString().trim().equals(Constantes.ASISTENCIA_CALIFICADA)
											/*
											 * beanAsis.getMov().trim().equals(Constantes.
											 * ENTRADA_NORMAL) ||
											 * beanAsis.getMov().trim().equals(Constantes.
											 * MOVIMIENTO_NORMAL) ||
											 * beanAsis.getMov().trim().equals(Constantes.SALIDA_NORMAL)
											 */)) {
										throw new IncompleteConversationalState(
												"No es posible modificar una papeleta.");
									}

									if (params[i]
											.equals(Constantes.MOV_OTRO_EDIFICIO)) {
										//Obtenemos el primer reloj
										bMarca = marcaDAO
												.findByCodPersFechaHora(dbpool,
														mAsis.get("cod_pers").toString(),
														Utiles.dateToString((Date)mAsis.get("fing")),
														mAsis.get("hing").toString());
										reloj1 = bMarca.getReloj();

										//Obtenemos el segundo reloj
										bMarca = marcaDAO
												.findByCodPersFechaHora(dbpool,
														mAsis.get("cod_pers").toString(),
														Utiles.dateToString((Date)mAsis.get("fing")),
														mAsis.get("hsal").toString());
										reloj2 = bMarca.getReloj();
									}

									msg = validarMovimiento(dbpool, mAsis.get("cod_pers").toString(),
											Utiles.dateToString((Date)mAsis.get("fing")),
											mAsis.get("hing").toString(), mAsis.get("hsal").toString(), tipoMov,
											params[i].trim(), tieneRefrigerio,
											reloj1, reloj2, turno);

									if (msg.trim().equals("")) {
										//modificarCalificaciÃ¯Â¿Â½n
										modificarCalificacion(dbpool, mAsis.get("cod_pers").toString(),
												mAsis.get("periodo").toString(), mAsis.get("u_organ").toString(),
												Utiles.dateToString((Date)mAsis.get("fing")), mAsis.get("hing").toString(),
												params[i].trim(), usuario);
										if (mAsis.get("mov") != null
												&& daoTipo.findByFlags(dbpool,
														params[i].trim(), "2",
														true)) {
											tieneRefrigerio = true;
										}
									}
								}
							}

							if (!msg.trim().equals("")) {
								errores++;
								msg = "Error para el registro "
										+ mAsis.get("cod_pers").toString() + " del "
										+ Utiles.dateToString((Date)mAsis.get("fing")) + " - "
										+ mAsis.get("hing").toString() + ". " + msg;
								mensajes.add(errores, msg);
							}

						}
					}
				}
			}
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		return mensajes;
	}

	//ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public ArrayList calificarAsistenciasCasPlanilla(String dbpool, String[] params,
			ArrayList lista, String usuario, String fecha, String regimenModalidad)
			throws IncompleteConversationalState, RemoteException {

		log.debug("calificarAsistenciasCasPlanilla");
		log.debug("params: "+params);
		log.debug("lista: "+lista);
		BeanMensaje beanM = new BeanMensaje();
		ArrayList mensajes = new ArrayList();
		try {

			ProcesoFacadeHome facadeHome = (ProcesoFacadeHome) sl.getRemoteHome(ProcesoFacadeHome.JNDI_NAME,
					ProcesoFacadeHome.class);
			ProcesoFacadeRemote facadeRemote = facadeHome.create();
			
			MantenimientoFacadeHome mantHome = (MantenimientoFacadeHome) sl.getRemoteHome(
							MantenimientoFacadeHome.JNDI_NAME,
							MantenimientoFacadeHome.class);
			MantenimientoFacadeRemote mantenimiento = mantHome.create();
			//ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII String periodo = mantenimiento.periodoCerradoAFecha(dbpool, fecha);
			String periodo = mantenimiento.periodoCerradoAFechaPorRegimen(dbpool, fecha,regimenModalidad); //ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII
			
			if (!periodo.equals("")) {
				throw new IncompleteConversationalState("El periodo " + periodo
						+ " se encuentra cerrado.");
			} else {
				
				HashMap mAsis = null;
				//PRAC-JCALLO de aqui para abajo todo lo de beanAsistencia por hasmap
				T1271DAO dao = new T1271DAO(dbpool);
				String nvoMov = "";

				if (lista != null && lista.size() > 0) {
					mAsis = (HashMap) lista.get(0);
					Map prms = new HashMap();
					prms.put("cod_pers", mAsis.get("cod_pers").toString());
					prms.put("fecha", Utiles.dateToString((Date)mAsis.get("fing")));
					log.debug("metodo calificarAsistencias... prms: "+prms);
					if (dao.findByCodPersFFinNull(prms)) {
						throw new IncompleteConversationalState(
								"No se puede registrar una calificacion en un dia con marcaciones impares,"
										+ " primero debe ser registrada la marcacion faltante.");
					}
					
					//ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
					T8167DAO climaDao = new T8167DAO(dbpool); 
					String fechaClima = "";					
					Map mapClima = climaDao.findAutorizaEnMesByRegByFecha(mAsis.get("cod_pers").toString().trim(), fecha);
					log.debug("fecha: "+fecha);
					log.debug("mapClima cas-planilla: "+mapClima);
					if (mapClima!=null && !mapClima.isEmpty()) {
						fechaClima = mapClima.get("fec_aut_desc").toString();
						log.debug("fechaClima: "+fechaClima);
						//ICAPUNAY - PAS20171U230300040 - Refrigerio Clima Laboral
						if(!fecha.trim().equals(fechaClima)){							
							for (int i = 0; i < lista.size(); i++) {
								log.debug("params[i]: "+params[i]);
								mAsis = (HashMap) lista.get(i);
								log.debug("mAsis.get(mov): "+mAsis.get("mov"));
								if (params[i].trim().equals(Constantes.MOV_REFRI_CLIMALABORAL)){
								//if (!mAsis.get("mov").toString().trim().equals(params[i].trim()) && params[i].equals(Constantes.MOV_REFRI_CLIMALABORAL)){									
									log.debug("params[i]=136");
									throw new IncompleteConversationalState("El colaborador ya tiene registrada una calificación de Clima laboral en la fecha " + fechaClima+ ". Sólo puede registrar una actividad de clima laboral por mes para cada colaborador.");
								}								
							}							
						}
						//throw new IncompleteConversationalState("El colaborador ya tiene registrada una calificación de Clima laboral en la fecha " + fechaClima+ ". Sólo puede registrar una actividad de clima laboral por mes para cada colaborador.");
						//FIN ICAPUNAY - PAS20171U230300040 - Refrigerio Clima Laboral
					}
					//FIN ICAPUNAY
				}

				boolean tieneRefrigerio = false;
				String msg = "";
				String tipoMov = "";
				String reloj1 = "";
				String reloj2 = "";
				T1275DAO marcaDAO = new T1275DAO();
				BeanMarcacion bMarca = null;
				int errores = -1;
				T1270DAO daoTurno = new T1270DAO();
				BeanTurnoTrabajo turno = null;
				int numEntr = 0;
				int numSali = 0;
				int numRefr = 0;
				T1279DAO daoTipo = new T1279DAO();

				if (lista != null && lista.size() > 0) {
					mAsis = (HashMap) lista.get(0);
					turno = daoTurno.joinWithT45ByCodFecha(dbpool, mAsis.get("cod_pers").toString(), Utiles.dateToString((Date)mAsis.get("fing")));
					if (turno  == null) {
						throw new IncompleteConversationalState(
								"El trabajador no tiene Turno asignado. Favor Comunicarse con el Analista.");
					}
					for (int i = 0; i < lista.size(); i++) {
						mAsis = (HashMap) lista.get(i);

						//Validamos que solo tenga una calificacion de entrada.
						if (params[i] != null
								&& daoTipo.findByFlags(dbpool, params[i], "0",
										false)) {
							numEntr++;
						}
						if (numEntr > 1) {
							throw new IncompleteConversationalState(
									"El dia solo puede tener una calificacion de entrada.");
						}

						//Validamos que solo tenga una calificaciÃ¯Â¿Â½n de
						// refrigerio.
						if (params[i] != null
								&& daoTipo.findByFlags(dbpool, params[i], "1",
										false)) {
							numRefr++;
						}
						if (numRefr > 1) {
							throw new IncompleteConversationalState(
									"El dia solo puede tener una calificacion de refrigerio.");
						}

						//Validamos que solo tenga una calificaciÃ¯Â¿Â½n de salida.
						if (params[i] != null
								&& daoTipo.findByFlags(dbpool, params[i], "2",
										false)) {
							numSali++;
						}
						if (numSali > 1) {
							throw new IncompleteConversationalState(
									"El dia solo puede tener una calificacion de salida.");
						}

					}
				}

				for (int i = 0; i < lista.size(); i++) {
					mAsis = (HashMap) lista.get(i);
					log.debug("mAsis: "+mAsis);
					msg = "";
					tipoMov = "";
					reloj1 = "";
					reloj2 = "";

					if (params[i] != null) {
						if (i == 0) {
							tipoMov = "0";
							nvoMov = Constantes.ENTRADA_NORMAL;
						} else if (i == (lista.size() - 1)) {
							tipoMov = "2";
							nvoMov = Constantes.SALIDA_NORMAL;
						} else {
							tipoMov = "3";
							nvoMov = Constantes.MOV_EN_OBSERVACION;
						}

						if (params[i].equals("-1")) {
							/*if (mAsis.get("mov") != null) {
								//Obtenemos el codigo del movimiento pre
								// calificado
								if (turno.getHoraIni().trim().equals("")) {
									nvoMov = Constantes.MOV_EN_OBSERVACION;
								} else {
									

									if (tipoMov.equals("0")) {
										nvoMov = facadeRemote
												.preCalificarEntrada(Utiles.dateToString((Date)mAsis.get("fing"))
														, mAsis.get("hing").toString(), turno
														.getHoraIni(), turno
														.getTolera(), turno
														.getHoraLimite(),
														Constantes.MINUTO);
									} else if (tipoMov.equals("2")) {
										nvoMov = facadeRemote
												.preCalificarSalida(
														dbpool,
														mAsis.get("cod_pers").toString(),
														Utiles.dateToString((Date)mAsis.get("fing")), 
														mAsis.get("hsal").toString(), 
														turno.getHoraFin(),
														Constantes.MINUTO);
									} else if (tipoMov.equals("3")) {
										//Obtenemos el primer reloj
										bMarca = marcaDAO
												.findByCodPersFechaHora(dbpool,
														beanAsis.getCodPers(),
														beanAsis.getFIng(),
														beanAsis.getHIng());
										reloj1 = bMarca.getReloj();

										//Obtenemos el segundo reloj
										bMarca = marcaDAO
												.findByCodPersFechaHora(dbpool,
														beanAsis.getCodPers(),
														beanAsis.getFIng(),
														beanAsis.getHSal());
										reloj2 = bMarca.getReloj();
										nvoMov = facadeRemote
												.preCalificarMovimiento(
														reloj1,
														reloj2,
														beanAsis.getFIng(),
														beanAsis.getHIng(),
														beanAsis.getHSal(),
														turno.getHoraIniRefrigerio(),
														turno.getHoraFinRefrigerio(),
														turno.getMinutosRefrigerio(),
														Constantes.MINUTO,
														tieneRefrigerio,
														turno.isOperativo());
										//EBV 14/08/2007
										//Cuando no pone movimiento se deja el actual
										nvoMov = mAsis.get("mov").toString().trim();
										//EBV 14/08/2007
									}
								
								}

								//eliminarPapeleta
								//EBV 25/02/2008
								eliminarPapeleta(dbpool, beanAsis.getCodPers(),
										beanAsis.getPeriodo(), beanAsis
												.getUOrgan(), beanAsis
												.getFIng(), beanAsis.getHIng(),
										usuario, nvoMov);
										
							}*/
						} else {
							if ((mAsis.get("mov") == null)
                                                            || (mAsis.get("mov") != null &&
                                                            		mAsis.get("estado_id").toString().trim().equals(Constantes.PRE_CALIFICACION_ASIS)
									/*
									 * (beanAsis.getMov().trim().
									 * equals(Constantes. ENTRADA_NORMAL) ||
									 * beanAsis.getMov().trim().
									 * equals(Constantes. MOVIMIENTO_NORMAL) ||
									 * beanAsis.getMov().trim().
									 * equals(Constantes. SALIDA_NORMAL))
									 */&& !mAsis.get("mov").toString().trim().equals(params[i].trim()))) {

								if (params[i]
										.equals(Constantes.MOV_OTRO_EDIFICIO)) {
									//Obtenemos el primer reloj
									bMarca = marcaDAO.findByCodPersFechaHora(
											dbpool, mAsis.get("cod_pers").toString(),
											Utiles.dateToString((Date)mAsis.get("fing")), mAsis.get("hing").toString());
									reloj1 = bMarca.getReloj();

									//Obtenemos el segundo reloj
									bMarca = marcaDAO.findByCodPersFechaHora(
											dbpool, mAsis.get("cod_pers").toString(),
											Utiles.dateToString((Date)mAsis.get("fing")), mAsis.get("hsal").toString());
									reloj2 = bMarca.getReloj();
								}

								msg = validarMovimiento(dbpool, mAsis.get("cod_pers").toString(),
										Utiles.dateToString((Date)mAsis.get("fing")),
										mAsis.get("hing").toString(), mAsis.get("hsal").toString(),
										tipoMov, params[i].trim(),
										tieneRefrigerio, reloj1, reloj2, turno);

								if (msg.trim().equals("")) {
									//registrarCalificacion
									registrarCalificacion(dbpool, mAsis.get("cod_pers").toString(), mAsis.get("periodo").toString(),
											mAsis.get("u_organ").toString(), Utiles.dateToString((Date)mAsis.get("fing")), mAsis.get("hing").toString(),
											params[i].trim(), usuario);
									
									if (mAsis.get("mov") != null && daoTipo.findByFlags(dbpool,params[i].trim(),"2", true)) {
										tieneRefrigerio = true;
									}
								}

							} else {
								if (!params[i].trim().equals(
										mAsis.get("mov").toString().trim())) {
									if (!(mAsis.get("estado_id").toString().trim().equals(Constantes.PRE_CALIFICACION_ASIS) || 

											//mAsis.get("estado_id").toString().trim().equals(Constantes.PAPELETA_REGISTRADA) ||
											
											//EBV - 09072008 - Papeletas 
											mAsis.get("estado_id").toString().trim().equals(Constantes.PAPELETA_PROCESADA) ||
											
											mAsis.get("estado_id").toString().trim().equals(Constantes.ASISTENCIA_CALIFICADA)
											/*
											 * beanAsis.getMov().trim().equals(Constantes.
											 * ENTRADA_NORMAL) ||
											 * beanAsis.getMov().trim().equals(Constantes.
											 * MOVIMIENTO_NORMAL) ||
											 * beanAsis.getMov().trim().equals(Constantes.SALIDA_NORMAL)
											 */)) {
										throw new IncompleteConversationalState(
												"No es posible modificar una papeleta.");
									}

									if (params[i]
											.equals(Constantes.MOV_OTRO_EDIFICIO)) {
										//Obtenemos el primer reloj
										bMarca = marcaDAO
												.findByCodPersFechaHora(dbpool,
														mAsis.get("cod_pers").toString(),
														Utiles.dateToString((Date)mAsis.get("fing")),
														mAsis.get("hing").toString());
										reloj1 = bMarca.getReloj();

										//Obtenemos el segundo reloj
										bMarca = marcaDAO
												.findByCodPersFechaHora(dbpool,
														mAsis.get("cod_pers").toString(),
														Utiles.dateToString((Date)mAsis.get("fing")),
														mAsis.get("hsal").toString());
										reloj2 = bMarca.getReloj();
									}

									msg = validarMovimiento(dbpool, mAsis.get("cod_pers").toString(),
											Utiles.dateToString((Date)mAsis.get("fing")),
											mAsis.get("hing").toString(), mAsis.get("hsal").toString(), tipoMov,
											params[i].trim(), tieneRefrigerio,
											reloj1, reloj2, turno);

									if (msg.trim().equals("")) {
										//modificarCalificaciÃ¯Â¿Â½n
										modificarCalificacion(dbpool, mAsis.get("cod_pers").toString(),
												mAsis.get("periodo").toString(), mAsis.get("u_organ").toString(),
												Utiles.dateToString((Date)mAsis.get("fing")), mAsis.get("hing").toString(),
												params[i].trim(), usuario);
										if (mAsis.get("mov") != null
												&& daoTipo.findByFlags(dbpool,
														params[i].trim(), "2",
														true)) {
											tieneRefrigerio = true;
										}
									}
								}
							}

							if (!msg.trim().equals("")) {
								errores++;
								msg = "Error para el registro "
										+ mAsis.get("cod_pers").toString() + " del "
										+ Utiles.dateToString((Date)mAsis.get("fing")) + " - "
										+ mAsis.get("hing").toString() + ". " + msg;
								mensajes.add(errores, msg);
							}

						}
					}
				}
			}
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		return mensajes;
	}
	//FIN ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII

	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void registrarCalificacion(String dbpool ,String codPers, String periodo,
			String uOrg, String fIng, String hIng, String codMov, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {
			//PRAC-JCALLO 
			/*T1271CMPHome cmpHome = (T1271CMPHome) ServiceLocator.getInstance().getLocalHome(T1271CMPHome.JNDI_NAME);

			T1271CMPLocal cmpLocal = cmpHome
					.findByPrimaryKey(new T1271CMPPK(codPers,
							periodo, new BeanFechaHora(fIng).getSQLDate(), hIng));*/

			T1271DAO asisdao = new T1271DAO(dbpool);
			//JRR - 30/01/2009
			T3701DAO t3701dao = new T3701DAO(ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dgsp"));
			Map mnovedad = new HashMap();
			Map map_aux = null;
			
			/*PRAC-JCALLO TODO ESTE CODIGO SE REEMPLAZO POR OTRO METODO DEACTUALIZACION DE LA TABLA 1271
			 * Map params = new HashMap();
			params.put("cod_pers", codPers);
			params.put("periodo", periodo);
			params.put("u_organ", uOrg);
			params.put("fIng", fIng);
			params.put("hIng", hIng);
			log.debug("metodo registrarCalificacion...params: "+params);
			Map papeleta = asisdao.findByKeyAsistencia(params); 

			papeleta.put("mov", codMov);//cmpLocal.setMov(codMov);
			papeleta.put("autor_id", "0");//cmpLocal.setAutorId("0");
			papeleta.put("estado_id", Constantes.ASISTENCIA_CALIFICADA);//cmpLocal.setEstadoId(Constantes.ASISTENCIA_CALIFICADA);
			papeleta.put("fMod", new FechaBean().getTimestamp());//cmpLocal.setFmod(new Timestamp(System.currentTimeMillis()));
			papeleta.put("cuser_mod", usuario);//cmpLocal.setCuserMod(usuario);
			//falta verificar si solo actualiza los campos especificados
			log.debug("papeleta:"+papeleta);
			asisdao.updateRegistroAsistencia(papeleta); */
			
			//PRAC-JCALLO codigo que reemplazo la parte de arriba
			Map datos = new HashMap();
			//campos a actualizar
			Map columns = new HashMap();
			columns.put("mov",codMov);
			columns.put("autor_id","0");
			columns.put("estado_id",Constantes.ASISTENCIA_CALIFICADA);
			columns.put("fMod", new FechaBean().getTimestamp());//cmpLocal.setFmod(new Timestamp(System.currentTimeMillis()));
			columns.put("cuser_mod",usuario);			
			datos.put("columns", columns);
			//datos de la llave primaria
			datos.put("cod_pers", codPers);
			datos.put("periodo", periodo);
			datos.put("fing", new FechaBean(fIng).getSQLDate());
			datos.put("hing", hIng);
			log.debug("metodo registrarCalificacion... datos :  "+datos);
			asisdao.updateCustomColumns(datos);
			
			//JRR - 30/01/2009
			mnovedad.put("cod_pers", codPers);
			mnovedad.put("ind_proceso", "1");
			mnovedad.put("cod_usucrea", usuario);
			mnovedad.put("fec_creacion", new FechaBean().getTimestamp());
			mnovedad.put("fec_refer", new FechaBean(fIng).getSQLDate());

			map_aux = t3701dao.findNovedadByTrabFec(mnovedad); 
			if(map_aux!=null){
				mnovedad.put("ind_proc_cerrado", "2");
				t3701dao.actualizarNovedadCerrada(mnovedad);
			}
			else
				t3701dao.registrarNovedad(mnovedad);
			//
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void modificarCalificacion(String dbpool,String codPers, String periodo,
			String uOrg, String fIng, String hIng, String codMov, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {
			//PRAC-JCALLO
			/*T1271CMPHome cmpHome = (T1271CMPHome) ServiceLocator.getInstance().getLocalHome(T1271CMPHome.JNDI_NAME);

			T1271CMPLocal cmpLocal = cmpHome
					.findByPrimaryKey(new T1271CMPPK(codPers,
							periodo, new BeanFechaHora(fIng).getSQLDate(), hIng));*/
			//JRR - 30/01/2009
			T3701DAO t3701dao = new T3701DAO(ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dgsp"));
			Map mnovedad = new HashMap();
			Map map_aux = null;
			
			T1271DAO asisdao = new T1271DAO(dbpool); 
			Map params = new HashMap();
			params.put("cod_pers", codPers);
			params.put("periodo", periodo);
			params.put("u_organ", uOrg);
			params.put("fIng", fIng);
			params.put("hIng", hIng);
			log.debug("metodo modificarCalificacion ... params:"+params);
			Map papeleta = asisdao.findByKeyAsistencia(params); 

			if (
				papeleta.get("estado_id").toString().equals(Constantes.PRE_CALIFICACION_ASIS) ||
				//EBV - 10072008 - Papeletas 
				papeleta.get("estado_id").toString().equals(Constantes.PAPELETA_PROCESADA) ||
				
				//papeleta.get("estado_id").toString().equals(Constantes.PAPELETA_REGISTRADA) ||
				
				papeleta.get("estado_id").toString().equals(Constantes.ASISTENCIA_CALIFICADA) || 
				papeleta.get("estado_id").toString().equals(Constantes.PAPELETA_RECHAZADA)) {
				
				//cmpLocal.setMov(codMov);
				//cmpLocal.setEstadoId(Constantes.ASISTENCIA_CALIFICADA);
				//cmpLocal.setFmod(new Timestamp(System.currentTimeMillis()));
				//cmpLocal.setCuserMod(usuario);
								
				papeleta.put("mov", codMov);
				papeleta.put("estado_id", Constantes.ASISTENCIA_CALIFICADA);
				//papeleta.put("fMod", new FechaBean().getTimestamp());//ICAPUNAY FORMATIVAS - NO ACTUALIZA FECHA DE MODIFICACION
				papeleta.put("fmod", new FechaBean().getTimestamp());//ICAPUNAY FORMATIVAS - SI ACTUALIZA FECHA DE MODIFICACION
				papeleta.put("cuser_mod", usuario);
				
				//actualizando ..FALTA verificar si realmente modifica solo los campos especificados
				log.debug("papeleta : "+papeleta);
				asisdao.updateRegistroPapeleta(papeleta);
				
				//JRR - 30/01/2009
				mnovedad.put("cod_pers", codPers);
				mnovedad.put("ind_proceso", "1");
				mnovedad.put("cod_usucrea", usuario);
				mnovedad.put("fec_creacion", new FechaBean().getTimestamp());
				mnovedad.put("fec_refer", new FechaBean(fIng).getSQLDate());

				map_aux = t3701dao.findNovedadByTrabFec(mnovedad); 
				if(map_aux!=null){
					mnovedad.put("ind_proc_cerrado", "2");
					t3701dao.actualizarNovedadCerrada(mnovedad);
				}
				else
					t3701dao.registrarNovedad(mnovedad);
				//
				
			} else {
				throw new IncompleteConversationalState(
						"Imposible modificar el registro porque es una Papeleta. Intente modificarlo desde la ventana correspondiente.");
			}
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

	}

	/**
	 * Grabacion del Registro de Devolucion
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void grabarDevolucion(Map devol)
	/*public void grabarDevolucion(String dbpool, String codPers, String periodo,
			String mov, Integer total, String observacion, String usuario)*/
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		Integer total = new Integer(devol.get("total").toString().trim());
		try {

			MantenimientoFacadeHome facadeHome = (MantenimientoFacadeHome) sl.getRemoteHome(
							MantenimientoFacadeHome.JNDI_NAME,
							MantenimientoFacadeHome.class);
			MantenimientoFacadeRemote mantenimiento = facadeHome.create();
			boolean cerrado = mantenimiento.periodoCerradoAFecha(devol.get("dbpool").toString().trim(),
					devol.get("periodo").toString().trim(), Utiles.obtenerFechaActual());

			if (!cerrado) {
				T1276DAO dao = new T1276DAO();
				BeanPeriodo bean = dao.findByCodigo(devol.get("dbpool").toString().trim(), devol.get("periodo").toString().trim());
				
				throw new IncompleteConversationalState("El periodo " + devol.get("periodo").toString().trim()
						+ " aun no ha sido cerrado. Fecha de cierre : "+bean.getFechaCie()+".");
			} else {

				T1272DAO asisDAO = new T1272DAO();
				BeanResumen bAsis = asisDAO.findByPk(devol.get("dbpool").toString().trim(), devol.get("codPers").toString().trim(),
						devol.get("periodo").toString().trim(), devol.get("mov").toString().trim());

				if (bAsis == null) {
					throw new IncompleteConversationalState(
							"No se ha encontrado el registro de asistencia al cual hace referencia la devolucion, la accion ha sido cancelada.");
				}

				if (bAsis.getTotal() < total.intValue()) {
					throw new IncompleteConversationalState(
							"No puede registrar una devolucion por una cantidad mayor a la registrada en el record de asistencia.");
				}

				//T1276DAO periodoDAO = new T1276DAO();
				//EBV 09/12/2008
				//El periodo viene desde el Servlet
				/*String periodoReg = periodoDAO.joinWithT1278(devol.get("dbpool").toString().trim(), bAsis
						.getUndOrg(), Utiles.obtenerFechaActual());*/
					String periodoReg = devol.get("periodoAsis").toString().trim();

				T1444CMPHome cmpHome = (T1444CMPHome)sl.getLocalHome(T1444CMPHome.JNDI_NAME);

				try {
					T1444CMPLocal cmpLocal = cmpHome
							.findByPrimaryKey(new T1444CMPPK(devol.get("codPers").toString().trim(),
									devol.get("periodo").toString().trim(), devol.get("mov").toString().trim()));

					cmpLocal.setTotal(total);
					cmpLocal.setPeriodReg(periodoReg);
					cmpLocal.setObserv(devol.get("observacion").toString());
					cmpLocal.setFmod(new java.sql.Timestamp(System
							.currentTimeMillis()));
					cmpLocal.setCuserMod(devol.get("usuario").toString().trim());
				} catch (Exception e) {
					//insertamos el registro
					cmpHome.create(devol.get("codPers").toString().trim(),
							devol.get("periodo").toString().trim(), devol.get("mov").toString().trim(), total, periodoReg, devol.get("observacion").toString().trim(),
							new Timestamp(System.currentTimeMillis()), devol.get("usuario").toString().trim());
				}
			}
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public List buscarPapeletasSubordinados(String dbpool, String codPers,
			String fechaIni, String fechaFin, HashMap seguridad)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		List papeletas = null;
		try {
			//PRAC-JCALLO
			T1271DAO daoAsis = new T1271DAO(dbpool);
			Map params = new HashMap();
			params.put("cod_pers", codPers);
			params.put("fechaIni", fechaIni);
			params.put("fechaFin", fechaFin);
			params.put("seguridad", seguridad);
			log.debug("metodo buscarPapeletasSubordinados ... params:"+params);
			papeletas = daoAsis.findPapeletasSubordinados(params);
			log.debug("papeletas.size:"+papeletas.size());
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return papeletas;
	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public List buscarPapeletasGeneradas(String dbpool, String codPers, HashMap seguridad)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		List papeletas = null;
		try {
			//PRAC-JCALLO
			T1271DAO daoAsis = new T1271DAO(dbpool);
			Map params = new HashMap();
			
			params.put("cod_pers", codPers);
			params.put("seguridad", seguridad);
			log.debug("metodo buscarPapeletasGeneradas... params:"+params);
			papeletas = daoAsis.findPapeletas(params);
			log.debug("papeletas.size : "+papeletas.size());
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return papeletas;
	}	

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public List buscarAsistencias(String dbpool, String codPers,
			String fecha, HashMap seguridad)
			throws IncompleteConversationalState, RemoteException {

		List lista = null;
		BeanMensaje beanM = new BeanMensaje();
		//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
		T1270DAO turnoDAO = new T1270DAO();	
		T1276DAO periodoDAO = new T1276DAO();	
		T02DAO persDAO = new T02DAO();	
		BeanTurnoTrabajo turno1 = null;
		BeanTurnoTrabajo turno2 = null;
		BeanTurnoTrabajo turno3 = null;
		boolean visualizar = true;
		String s_fechaAnt = "";
		HashMap hmPers =null;
		String cod_rel="-";
		String periodo = "";
		Map params2 = new HashMap();
		boolean bPeriodoCerrado = false;
		//FIN 
		
		try {
			//PRAC-JCALLO
			T1271DAO dao = new T1271DAO(dbpool);
			//VERIRIFICAR EL USO DE BEANS... DE LA LISTA DE RESULTADOS
			
			//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
			hmPers = persDAO.findByCodPers(dbpool, codPers.trim().toUpperCase());
			cod_rel=hmPers!=null?hmPers.get("t02cod_rel").toString():"-";						
			params2.put("dbpool", dbpool);			
			//FIN
			
			//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados			
			log.debug("fecha: "+fecha);
			visualizar = true;
			turno1 = null;
			turno2 = null;
			turno3 = null;
			
			if(!cod_rel.equals("-")) {
				if(cod_rel.equals("09")) {//regimen 1057 (CAS)
					periodo = periodoDAO.findByFechaCAS(dbpool, fecha);	
					log.debug("periodo CAS: "+periodo);
					params2.put("periodo", periodo);
					bPeriodoCerrado = periodoDAO.isPeriodoCerradoCAS(params2);
					log.debug("bPeriodoCerrado CAS: "+bPeriodoCerrado);
				} else if(cod_rel.equals("10")) {//Modalidades Formativas 
					periodo = periodoDAO.findByFechaModFormativas(dbpool, fecha);	
					log.debug("periodo formativas: "+periodo);
					params2.put("periodo", periodo);
					bPeriodoCerrado = periodoDAO.isPeriodoCerradoModFormativa(params2);
					log.debug("bPeriodoCerrado Formativa: "+bPeriodoCerrado);												
				} else { //regimen 276-728
					periodo = periodoDAO.findByFecha(dbpool, fecha);
					log.debug("periodo 276-728: "+periodo);
					params2.put("periodo",periodo);
					bPeriodoCerrado = periodoDAO.isPeriodoCerrado(params2);
					log.debug("bPeriodoCerrado 276-728: "+bPeriodoCerrado);	
				}
			}
		
			turno1 = turnoDAO.joinWithT45ByCodPersFecha_NoControlado_ActInact_OperAdm1dia(dbpool,codPers.trim(),fecha);		    
			if (turno1!=null && bPeriodoCerrado){
				visualizar = false;	//no se debe visualizar informacion de s_fecha
				log.debug("NO visualizar informacion de fecha: "+fecha);
			}else{
				turno2 = turnoDAO.joinWithT45ByCodPersFecha_NoControlado_ActInact_OperAdm2dias(dbpool,codPers.trim(),fecha);
				if (turno2!=null && bPeriodoCerrado){
					visualizar = false;	//no se debe visualizar informacion de s_fecha
					log.debug("NO visualizar informacion1 de fecha: "+fecha);
				}else{
					s_fechaAnt = Utiles.dameFechaAnterior(fecha, 1);
					log.debug("s_fechaAnt: "+s_fechaAnt);
					turno3 = turnoDAO.joinWithT45ByCodPersFecha_NoControlado_ActInact_OperAdm2dias(dbpool,codPers.trim(),s_fechaAnt);
					if (turno3!=null && bPeriodoCerrado){
						visualizar = false; //no se debe visualizar informacion de s_fecha
						log.debug("NO visualizar informacion2 de fecha: "+fecha);
					}						
				}							    
			}
							
			if (visualizar==true){
				log.debug("SI visualizar informacion de fecha: "+fecha);					
			//FIN ICAPUNAY	
				
				Map params = new HashMap();
				params.put("cod_pers", codPers);
				params.put("fecha", fecha);
				params.put("seguridad", seguridad);
				log.debug("metodo buscarAsistencias... params:"+params);
				lista = dao.findByCodPersFecha(params);
				log.debug("lista.size : "+lista.size());
			}//add esta linea //ICAPUNAY - PAS20165E230300132 - MSNAAF071	
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return lista;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList buscarAsistenciasPeriodo(String dbpool, String periodo,
			String criterio, String valor)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		ArrayList lista = null;
		try {

			T1272DAO dao = new T1272DAO();
			lista = dao.joinWithT1279T02T12ByPeriodoCriterioValor(dbpool,
					periodo, criterio, valor);
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return lista;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public BeanDevolucion buscarDevoluciones(String dbpool, String codPers,
			String periodo, String mov) throws IncompleteConversationalState,
			RemoteException {

		BeanDevolucion bean = null;
		try {
			T1444DAO dao = new T1444DAO();
			bean = dao.findByPk(dbpool, codPers, periodo, mov);
		} catch (Exception e) {
			log.error(e,e);
			throw new IncompleteConversationalState(e.getMessage());
		}

		return bean;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public HashMap buscarTrabajador(String dbpool, String codPers,
			HashMap seguridad) throws IncompleteConversationalState,
			RemoteException {

		HashMap t = null;
		BeanMensaje beanM = new BeanMensaje();
		try {
			T02DAO dao = new T02DAO();
			t = dao.joinWithT12T99ByCodPers(dbpool, codPers, seguridad);
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return t;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public HashMap buscarTrabajadorJefe(String dbpool, String codPers, HashMap seguridad) throws IncompleteConversationalState,
			RemoteException {

		HashMap t = null;
		BeanMensaje beanM = new BeanMensaje();
		try {
			
			if(log.isDebugEnabled()) log.debug("Llego a buscarTrabajadorJefe");
			
			T02DAO dao = new T02DAO();
			T12DAO daoUO = new T12DAO();

			//obtenemos los datos del trabajador
			t = dao.joinWithT12T99ByCodPers(dbpool,codPers,seguridad);
			
			if(log.isDebugEnabled()) log.debug("Empleado t : "+t);
			
			HashMap datos = new HashMap();
			datos.put("dbpool",dbpool);
			datos.put("codOpcion",Constantes.DELEGA_SOLICITUDES);
			datos.put("codUO",(String)t.get("t02cod_uorg"));
			
			//ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
			HashMap hmUniOrg = daoUO.findByCodUorga(dbpool,t.get("t02cod_uorg")!=null?((String)t.get("t02cod_uorg")).trim():"");
			String t12cod_repor= (hmUniOrg!=null && !hmUniOrg.isEmpty())?!hmUniOrg.get("t12cod_repor").equals("-")?hmUniOrg.get("t12cod_repor").toString().trim():"":"";
			HashMap hmUniOrg2 = daoUO.findByCodUorga(dbpool,t12cod_repor);
			if(log.isDebugEnabled()) log.debug("buscarTrabajadorJefe-hmUniOrg2 ICR: "+hmUniOrg2);
			String t12cod_repor_desc= (hmUniOrg2!=null && !hmUniOrg2.isEmpty())?!hmUniOrg2.get("t12des_uorga").equals("Sin descripcion")?hmUniOrg2.get("t12des_uorga").toString().trim():"":"";
			HashMap aux1 = new HashMap();
			aux1.put("dbpool",dbpool);
			aux1.put("codUO",t12cod_repor);
			aux1.put("codOpcion",Constantes.DELEGA_SOLICITUDES);				
			if(log.isDebugEnabled()) log.debug("buscarTrabajadorJefe-Buscando delegado uuoo superior ICR ("+t12cod_repor+"):"+aux1);
			HashMap delegadoUOSup = daoUO.findDelegado(aux1);
			if(log.isDebugEnabled()) log.debug("buscarTrabajadorJefe-delegadoUOSup ICR: "+delegadoUOSup);
			String delegUOSup = (delegadoUOSup!=null && !delegadoUOSup.isEmpty())?((String)delegadoUOSup.get("t02cod_pers")).trim():"";	
			if(log.isDebugEnabled()) log.debug("buscarTrabajadorJefe-delegUOSup ICR: "+delegUOSup);
			//ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
			
			//primero verificamos si la unidad organizacional posee un delegado
			if(log.isDebugEnabled()) log.debug("Buscando delegado : "+datos);
			HashMap delegado = daoUO.findDelegado(datos);
			if(log.isDebugEnabled()) log.debug("Delegado : "+delegado);
			
			if (delegado!=null && !delegado.isEmpty()){
				if(log.isDebugEnabled()) log.debug("si hay delegado");//LOG
				if (t.get("t02cod_pers")!=null && !((String)t.get("t02cod_pers")).equals("")) {
					if(log.isDebugEnabled()) log.debug("si existe solicitante");//LOG
					
					if (!((String)t.get("t12cod_jefe")).equals(codPers.trim().toUpperCase()) &&
						!((String)delegado.get("t02cod_pers")).trim().equals(codPers.trim().toUpperCase())) {
						
						if(log.isDebugEnabled()) log.debug("solicitante NO es jefe o delegado");//LOG
						
						//aÃ±adimos los datos del delegado
						t.put("t12cod_jefe", (String) delegado.get("t02cod_pers"));//1548
				        t.put("t12cod_jefe_desc", //Enzo
				                (String) delegado.get("t02ap_pate") + " " +
				                (String) delegado.get("t02ap_mate") + ", " +
				                (String) delegado.get("t02nombres")
				                );
				        t.put("t12cod_cate_desc", (String) delegado.get("t02desc_cate"));
				        t.put("t12cod_uorg_desc", (String) delegado.get("t12des_uorga")); //5E2302
				        t.put("t12cod_uorg_jefe", (String) delegado.get("t02cod_uorg")); //5E2302
				        t.put("esDelegado","SI");
				        t.put("jefedelego",(String) delegado.get("t12cod_jefe")); //1267
				        //t02cod_uorg=5E4200
				        //t02cod_pers=2153
				        if(log.isDebugEnabled()) log.debug("t(solicitante no es jefe o delegado): "+t);//LOG
						
			        }
			        else{
			        	if(log.isDebugEnabled()) log.debug("solicitante es jefe o delegado");//LOG
			        	
			        	//ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
			        	if (delegadoUOSup!=null && !delegadoUOSup.isEmpty()){
			        		if(log.isDebugEnabled()) log.debug("delegadoUOSupp(solicitante es jefe o delegado): "+delegadoUOSup);
				        	//aÃ±adimos los datos del delegado de uo superior de uo solicitante
							t.put("t12cod_jefe", (String) delegadoUOSup.get("t02cod_pers"));
					        t.put("t12cod_jefe_desc", (String) delegadoUOSup.get("t02ap_pate") + " " + (String) delegadoUOSup.get("t02ap_mate") + ", " + (String) delegadoUOSup.get("t02nombres"));
					        t.put("t12cod_cate_desc", (String) delegadoUOSup.get("t02desc_cate"));					        
					        t.put("t12cod_uorg_desc", (String) delegadoUOSup.get("t12des_uorga")); 
					        t.put("t12cod_uorg_jefe", (String) delegadoUOSup.get("t02cod_uorg")); 
			        	}
			        	else{			        	
				        	HashMap jefe = daoUO.findJefeByTrabajador(dbpool,
	                                ( (String) t.get("t02cod_uorg")).trim(),
	                                ( (String) delegado.get("cod_jefe")).trim());
				        	if(log.isDebugEnabled()) log.debug("jefee(solicitante es jefe o delegado): "+jefe);				        	
				        	//t.put("t12cod_jefe", jefe.get("t12cod_encar") != " " ? (String) jefe.get("t12cod_encar") : (String) jefe.get("t12cod_jefat")); //ICAPUNAY - PAS20165E230300116 - delegacion sol  17/05/16
				        	t.put("t12cod_jefe", jefe.get("t12cod_encar") != null?(!jefe.get("t12cod_encar").toString().trim().equals("")?jefe.get("t12cod_encar").toString().trim():jefe.get("t12cod_jefat").toString().trim()): jefe.get("t12cod_jefat").toString().trim()); //ICAPUNAY - PAS20165E230300116 - delegacion sol  17/05/16
							t.put("t12cod_jefe_desc", jefe.get("t12cod_encar_desc") != " " ? (String) jefe.get("t12cod_encar_desc") : (String) jefe.get("t12cod_jefat_desc"));
							t.put("t12cod_cate_desc", (String) jefe.get("t12cod_cate_desc"));														
							t.put("t12cod_uorg_desc", t12cod_repor_desc);//debe ser la uo donde es jefe y no a donde pertenece //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
							t.put("t12cod_uorg_jefe", t12cod_repor);//debe ser la desc uo donde es jefe y no a donde pertenece //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
			        	}
			        	//ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
			        	
			        	/* //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
			        	HashMap jefe = daoUO.findJefeByTrabajador(dbpool,
                                ( (String) t.get("t02cod_uorg")).trim(),
                                ( (String) delegado.get("cod_jefe")).trim());
			        	if(log.isDebugEnabled()) log.debug("jefe(solicitante es jefe o delegado): "+jefe);//LOG
			        	
			        	t.put("t12cod_jefe", jefe.get("t12cod_encar") != " " ? (String) jefe.get("t12cod_encar") :
						      (String) jefe.get("t12cod_jefat"));
						t.put("t12cod_jefe_desc", jefe.get("t12cod_encar_desc") != " " ? (String) jefe.get("t12cod_encar_desc") :
						      (String) jefe.get("t12cod_jefat_desc"));
						t.put("t12cod_cate_desc", (String) jefe.get("t12cod_cate_desc"));*/ //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
						
						/*
			        	t.put("t12cod_jefe",
						      (jefe.get("t12cod_jefat") != null && jefe.get("t12cod_jefat").toString().trim() !="" )?
						      (String) jefe.get("t12cod_jefat") :
						      (String) jefe.get("t12cod_encar"));
						t.put("t12cod_jefe_desc", (jefe.get("t12cod_jefat") != null && jefe.get("t12cod_jefat").toString().trim() !="" )? (String) jefe.get("t12cod_jefat_desc"): (String) jefe.get("t12cod_encar_desc"));
						*/		
			        	
			        	//t.put("t12cod_uorg_desc", (String) jefe.get("t12cod_uorg_desc"));//ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp						
						//t.put("t12cod_uorg_jefe", (String) jefe.get("t12cod_uorg_jefe")); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
						t.put("esDelegado","SI");	
						t.put("jefedelego",(String) delegado.get("t12cod_jefe"));
						if(log.isDebugEnabled()) log.debug("t(solicitante es jefe o delegado): "+t);//LOG
						
						//JRR - 21/10/2008 - Para ver si la gerencia del jefe de division tiene delegado
/*						HashMap hm = new HashMap();
						hm.put("dbpool",dbpool);
						hm.put("codOpcion",Constantes.DELEGA_SOLICITUDES);
						hm.put("codUO",(String)t.get("t12cod_uorg_jefe"));
						
						if(log.isDebugEnabled()) log.debug("Buscando delegado de nivel mas alto: "+hm);
						Map delegado2 = daoUO.findDelegado(hm);
						if(log.isDebugEnabled()) log.debug("Delegado2 : "+delegado2);
						if (delegado2!=null && !delegado2.isEmpty()){
							//aÃ±adimos los datos del delegado
							t.put("t12cod_jefe", (String) delegado.get("t02cod_pers"));
					        t.put("t12cod_jefe_desc",
					                (String) delegado2.get("t02ap_pate") + " " +
					                (String) delegado2.get("t02ap_mate") + ", " +
					                (String) delegado2.get("t02nombres")
					                );
					        t.put("t12cod_cate_desc", (String) delegado2.get("t02desc_cate"));
					        t.put("t12cod_uorg_desc", (String) delegado2.get("t12des_uorga"));
					        t.put("t12cod_uorg_jefe", (String) delegado2.get("t02cod_uorg"));
					        t.put("esDelegado","SI");
					        t.put("jefedelego",(String) delegado2.get("t12cod_jefe"));
						}*/
						//						
			        }			       
				}
				if(log.isDebugEnabled()) log.debug("Fin Si hay delegado");//LOG
			}
			else{
				if(log.isDebugEnabled()) log.debug("No hay delegado");//LOG
				
				if (t.get("t02cod_pers")!=null && !((String)t.get("t02cod_pers")).equals("")) {
					if(log.isDebugEnabled()) log.debug("si existe solicitante");//LOG //entro (solic es jefe 1548)
					
					HashMap mapa = new HashMap();
					mapa.put("dbpool",dbpool);
					mapa.put("codUO",(String)t.get("t02cod_uorg"));
					//JRR - 19/01/2009
					mapa.put("cod_pers",(String)t.get("t02cod_pers"));
					
					
					HashMap superior = buscarSuperiorSolicitud(mapa);
					if(log.isDebugEnabled()) {
						log.debug("superior : "+superior);
						log.debug("superior(CodPers) : "+codPers);
						log.debug("superior(t.get(t12cod_jefe)) : "+((String) t.get("t12cod_jefe")));
						log.debug("superior(superior.get(codJefe)) : "+((String)superior.get("codJefe")));
						log.debug("sup(t.get(t12cod_jefe).equals(codPers)): "+((String) t.get("t12cod_jefe")).equals(codPers));
					}
					
					if (((String) t.get("t12cod_jefe")).equals(codPers) || 
						(superior!=null && ((String)superior.get("codJefe")).equals(codPers))) {
						if(log.isDebugEnabled()) log.debug("solicitante es jefe(superior)");//LOG //entro (solic es jefe 1548)
						
						HashMap jefe = new HashMap();
						
						//JRR - 12/09/2008 - Para que reconozca al delegado cuando el solicitante es jefe
						if (superior!=null && 
							superior.get("codJefe")!=null &&	//21/01/2009
							( ((String)superior.get("codJefe")).equals(codPers) || 
								(superior.get("existeDelegado").toString().equals("1")) )
							){ //ok probe cuando sol. es jefe: no {no hay delegado uo inferior} si {si hay delegado uo superior y jefe uo sup. pertenece a la uo sup.}
							if(log.isDebugEnabled()) log.debug("solicitante es jefe(superior) o si existe delegado" );//LOG							
							if(log.isDebugEnabled()) log.debug("entro a 1");
							
							//JRR - 10/10/2008 - Para enviar seguridad nula y no valide que el delegado pertenezca a la unidad del solicitante
							jefe = dao.joinWithT12T99ByCodPers( dbpool, (String) superior.get("codJefe"),null);
							//jefe = dao.joinWithT12T99ByCodPers( dbpool, (String) superior.get("codJefe"),seguridad);
				        	
							if(log.isDebugEnabled()) log.debug("jefe 1 : "+jefe);
							
				        	t.put("t12cod_jefe", (String) jefe.get("t02cod_pers"));
				        	t.put("t12cod_jefe_desc",
				                (String) jefe.get("t02cod_pers") + " - " +
				                (String) jefe.get("t02ap_pate") + " " +
				                (String) jefe.get("t02ap_mate") + ", " +
				                (String) jefe.get("t02nombres")
				                );
				        	
				        	t.put("t12cod_cate_desc", (String) jefe.get("t02desc_cate"));
				        	t.put("t12cod_uorg_desc", (String) jefe.get("t12des_uorga"));
				        	t.put("t12cod_uorg_jefe", (String) superior.get("codUOJefe"));
				        	t.put("esDelegado","SI");		
				        	t.put("jefedelego",(String) jefe.get("t12cod_jefe"));
				        	if(log.isDebugEnabled()) log.debug("t(solicitante es jefe(superior) o si existe delegado): "+t );//LOG	
						}
						else{
							if(log.isDebugEnabled()) log.debug("solicitante NO es jefe(superior) o NO existe delegado" );//LOG	//entro (solic es jefe 1548)

							if(log.isDebugEnabled()) log.debug("entro a 2");
							
							//ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp  ---falta arreglar
				        	if (delegadoUOSup!=null && !delegadoUOSup.isEmpty()){
				        		if(log.isDebugEnabled()) log.debug("delegadoUOSupp(solicitante NO es jefe(superior) o NO existe delegado): "+delegadoUOSup);
					        	//aÃ±adimos los datos del delegado de uo superior de uo solicitante
								t.put("t12cod_jefe", (String) delegadoUOSup.get("t02cod_pers"));
						        t.put("t12cod_jefe_desc", (String) delegadoUOSup.get("t02ap_pate") + " " + (String) delegadoUOSup.get("t02ap_mate") + ", " + (String) delegadoUOSup.get("t02nombres"));
						        t.put("t12cod_cate_desc", (String) delegadoUOSup.get("t02desc_cate"));					        
						        t.put("t12cod_uorg_desc", (String) delegadoUOSup.get("t12des_uorga")); 
						        t.put("t12cod_uorg_jefe", (String) delegadoUOSup.get("t02cod_uorg")); 
				        	}
				        	else{			        	
				        		jefe = daoUO.findJefeByTrabajador(dbpool,( (String) t.get("t02cod_uorg")).trim(), ( (String) t.get("t02cod_pers")).trim());
					        	if(log.isDebugEnabled()) log.debug("jefee(solicitante NO es jefe(superior) o NO existe delegado): "+jefe);		
					        	
					        	//t.put("t12cod_jefe", jefe.get("t12cod_encar") != " " ? (String) jefe.get("t12cod_encar") :(String) jefe.get("t12cod_jefat")); //ICAPUNAY - PAS20165E230300116 - delegacion sol  17/05/16
					        	t.put("t12cod_jefe", jefe.get("t12cod_encar") != null?(!jefe.get("t12cod_encar").toString().trim().equals("")?jefe.get("t12cod_encar").toString().trim():jefe.get("t12cod_jefat").toString().trim()): jefe.get("t12cod_jefat").toString().trim()); //ICAPUNAY - PAS20165E230300116 - delegacion sol  17/05/16
								t.put("t12cod_jefe_desc", jefe.get("t12cod_encar_desc") != " " ? (String) jefe.get("t12cod_encar_desc") : (String) jefe.get("t12cod_jefat_desc"));
							  	t.put("t12cod_cate_desc", (String) jefe.get("t12cod_cate_desc"));
							  	t.put("t12cod_uorg_desc", t12cod_repor_desc);//debe ser la uo donde es jefe y no a donde pertenece //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
								t.put("t12cod_uorg_jefe", t12cod_repor);//debe ser la desc uo donde es jefe y no a donde pertenece //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp								
				        	}
				        	//ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
							
							/* ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
							jefe = daoUO.findJefeByTrabajador(dbpool,
                                    ( (String) t.get("t02cod_uorg")).trim(),
                                    ( (String) t.get("t02cod_pers")).trim());
                            */ //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp							
							
						  	/*t.put("t12cod_jefe", jefe.get("t12cod_jefat") != " " ? (String) jefe.get("t12cod_jefat") :
						      (String) jefe.get("t12cod_encar"));
						  	t.put("t12cod_jefe_desc", jefe.get("t12cod_jefat_desc") != " " ? (String) jefe.get("t12cod_jefat_desc") :
						      (String) jefe.get("t12cod_encar_desc"));
						      */
				        	
						  	/* /* ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
				        	t.put("t12cod_jefe", jefe.get("t12cod_encar") != " " ? (String) jefe.get("t12cod_encar") :
							      (String) jefe.get("t12cod_jefat"));
							t.put("t12cod_jefe_desc", jefe.get("t12cod_encar_desc") != " " ? (String) jefe.get("t12cod_encar_desc") :
							      (String) jefe.get("t12cod_jefat_desc"));
						  	t.put("t12cod_cate_desc", (String) jefe.get("t12cod_cate_desc"));
						  	t.put("t12cod_uorg_desc", (String) jefe.get("t12cod_uorg_desc"));
						  	t.put("t12cod_uorg_jefe", (String) jefe.get("t12cod_uorg_jefe"));
						  	*/ ///* ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
						  	t.put("esDelegado","NO");
						  	if(log.isDebugEnabled()) log.debug("t(solicitante NO es jefe(superior) o NO existe delegado): "+t );//LOG	
							
						}
			        }
			        else {
			        	
			        	if(log.isDebugEnabled()) log.debug("solicitante NO es jefe");//LOG
			        	
			        	//Para enviar seguridad nula y no valide que el jefe pertenezca a la unidad del solicitante
			        	//HashMap jefe = dao.joinWithT12T99ByCodPers( dbpool, (String) t.get("t12cod_jefe"),seguridad); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
			        	HashMap jefe = dao.joinWithT12T99ByCodPers( dbpool, (String) t.get("t12cod_jefe"),null); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
			        	if(log.isDebugEnabled()) log.debug("jefe(solicitante NO es jefe): "+jefe );//LOG
			        	
			        	t.put("t12cod_jefe", (String) jefe.get("t02cod_pers"));
			        	t.put("t12cod_jefe_desc",
			                (String) jefe.get("t02cod_pers") + " - " +
			                (String) jefe.get("t02ap_pate") + " " +
			                (String) jefe.get("t02ap_mate") + ", " +
			                (String) jefe.get("t02nombres")
			                );
			        	
			        	t.put("t12cod_cate_desc", (String) jefe.get("t02desc_cate"));
			        	//t.put("t12cod_uorg_desc", (String) jefe.get("t12des_uorga")); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
			        	t.put("t12cod_uorg_desc", (String) t.get("t12des_uorga")); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
			        	t.put("t12cod_uorg_jefe", (String) jefe.get("t02cod_uorg"));
			        	t.put("esDelegado","NO");
			        	if(log.isDebugEnabled()) log.debug("t(solicitante NO es jefe): "+t );//LOG
			        }
			      }	
				if(log.isDebugEnabled()) log.debug("Fin No hay delegado");//LOG
			}
			
			if(log.isDebugEnabled()) log.debug("TrabajadorJefe Final : "+t);
			if (t.get("t12cod_jefe")!=null){
				if (t.get("t12cod_jefe").toString().trim().equals(t.get("t02cod_pers").toString().trim())){
					
					if ("SI".equals(t.get("esDelegado"))){
						HashMap datos1 = new HashMap();
						datos1.put("dbpool",dbpool);
						datos1.put("codOpcion",Constantes.DELEGA_SOLICITUDES);
						datos1.put("codUO",(String)t.get("t12cod_uorg_jefe"));
						
						//primero verificamos si la unidad organizacional posee un delegado
						if(log.isDebugEnabled()) log.debug("Buscando delegado : "+datos1);
						Map delegado1 = daoUO.findDelegado(datos1);
						if(log.isDebugEnabled()) log.debug("Delegado : "+delegado1);
						//JRR - 27/09/2010 - Se agregÃ³ validaciÃ³n de nulos
                        if (delegado1!=null && delegado1.get("cod_jefe")!=null){
                        	Map jefe22 = daoUO.findJefeByTrabajador(dbpool,(String)t.get("t12cod_uorg_jefe"),delegado1.get("cod_jefe").toString());
                        	if(log.isDebugEnabled()) log.debug("JEFE22 : "+jefe22);
                        	t.put("t02cod_uorg", t.get("t12cod_uorg_jefe"));
                        	t.put("t12cod_jefe", jefe22.get("t12cod_jefe_final"));
                        	t.put("t12cod_cate_desc", jefe22.get("t12cod_cate_desc"));
                        	t.put("t12cod_uorg_jefe", jefe22.get("t12cod_uorg_jefe"));
                        	t.put("t12cod_uorg_desc", jefe22.get("t12cod_uorg_desc"));
                        	t.put("t12cod_jefe_desc", jefe22.get("t12cod_jefat_desc"));
                        	if(log.isDebugEnabled()) log.debug("T - JEFE22 : "+t);
                        }
                        	
					}
					
				}
			}
				
			
		} catch (Exception e) {
			log.error(e,e);			
			beanM.setMensajeerror(e.toString());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		
		return t;
	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public HashMap buscarSuperiorSolicitud(HashMap mapa) throws IncompleteConversationalState,
			RemoteException {

		HashMap superior = new HashMap();
		BeanMensaje beanM = new BeanMensaje();
		try {
			
			if(log.isDebugEnabled()) log.debug("Llego a buscarSuperiorSolicitud "+mapa);
			
			String dbpool = (String)mapa.get("dbpool");
			String codUO = (String)mapa.get("codUO");
			
			//T02DAO dao = new T02DAO();
			T12DAO daoUO = new T12DAO();

			//obtenemos los datos del trabajador
/*			19/01/2009
			String jefe1 = daoUO.findJefeByUO(dbpool,codUO);
			log.debug("jefe1 : "+jefe1);
*/			
			String solicitante = mapa.get("cod_pers").toString().trim();
			HashMap empJefe1 = daoUO.findJefeByTrabajador(dbpool,codUO.trim(),solicitante);
//			HashMap empJefe1 = daoUO.findJefeByTrabajador(dbpool,codUO.trim(),jefe1);
			if(log.isDebugEnabled()) log.debug("empJefe1 : "+empJefe1);
			
			HashMap datos = new HashMap();
			datos.put("dbpool",dbpool);
			datos.put("codOpcion",Constantes.DELEGA_SOLICITUDES);
			datos.put("codUO",(String)empJefe1.get("t12cod_uorg_jefe"));
			
			//primero verificamos si la unidad organizacional posee un delegado
			if(log.isDebugEnabled()) log.debug("Buscando delegado : "+datos);
			HashMap delegado = daoUO.findDelegado(datos);
			if(log.isDebugEnabled()) log.debug("Delegado : "+delegado);
			
			if (delegado!=null && !delegado.isEmpty()){
				if(log.isDebugEnabled()) log.debug("Si existe delegado");
				superior.put("codJefe",(String)delegado.get("t02cod_pers"));
				superior.put("codUOJefe",(String)empJefe1.get("t12cod_uorg_jefe"));
				//JRR - 12/09/2008
				superior.put("existeDelegado", "1");
			}
			else{
				if(log.isDebugEnabled()) log.debug("NO existe delegado");
				if(log.isDebugEnabled()) log.debug("empJefe1.get(t12cod_jefat): "+empJefe1.get("t12cod_jefat"));//ICR 04/12/2012 solucion a error reportado por vcorrales 
				if(log.isDebugEnabled()) log.debug("empJefe1.get(t12cod_encar): "+empJefe1.get("t12cod_encar"));//ICR 04/12/2012 solucion a error reportado por vcorrales 
				//superior.put("codJefe",empJefe1.get("t12cod_jefat") != null ?(String) empJefe1.get("t12cod_jefat") :(String) empJefe1.get("t12cod_encar"));//ICR 04/12/2012 solucion a error reportado por vcorrales 
				superior.put("codJefe",empJefe1.get("t12cod_jefat")!= null?(!empJefe1.get("t12cod_jefat").toString().trim().equals("")?(String) empJefe1.get("t12cod_jefat"):(String) empJefe1.get("t12cod_encar")):(String) empJefe1.get("t12cod_encar"));//ICR 04/12/2012 solucion a error reportado por vcorrales
				if(log.isDebugEnabled()) log.debug("superior.get(codJefe): "+superior.get("codJefe"));//ICR 04/12/2012 solucion a error reportado por vcorrales
				superior.put("codUOJefe",(String)empJefe1.get("t12cod_uorg_jefe"));
				//JRR - 12/09/2008				
				superior.put("existeDelegado", "0");
			}
			
			if(log.isDebugEnabled()) log.debug("superiorSolicitud : "+superior);
			
		} catch (Exception e) {
			log.error(e,e);			
			beanM.setMensajeerror(e.toString());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		
		return superior;
	}	

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public HashMap buscarJefe(String dbpool, String uorg, String codPers)
			throws IncompleteConversationalState, RemoteException {

		HashMap t = null;
		BeanMensaje beanM = new BeanMensaje();
		try {
			T12DAO dao = new T12DAO();
			t = dao.findByJefe(dbpool, uorg.trim(), codPers.trim());
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return t;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public BeanT12 buscarUObyCodigo(String dbpool, String codigo)
			throws IncompleteConversationalState, RemoteException {

		BeanT12 uo = null;
		BeanMensaje beanM = new BeanMensaje();
		try {
			T12DAO dao = new T12DAO();
			uo = dao.findByCodigo(dbpool, codigo);
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return uo;

	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList buscarUOrgan(String dbpool, String criterio, String valor,
			HashMap seguridad) throws IncompleteConversationalState,
			RemoteException {

		ArrayList uo = null;
		BeanMensaje beanM = new BeanMensaje();
		try {

			//ICAPUNAY-MEMO 32-4F3100-2013
            HashMap roles = (HashMap) seguridad.get("roles");            
            log.debug("buscarUOrgan-roles: "+roles);
            if (roles.get(Constantes.ROL_JEFE) != null) {
            	ReporteFacadeHome spReporteFacadeHome = (ReporteFacadeHome) sl.getRemoteHome(
                                ReporteFacadeHome.JNDI_NAME,      ReporteFacadeHome.class);
            	ReporteFacadeRemote spReporteFacadeRemote = spReporteFacadeHome.create(); 
            	log.debug("buscarUOrgan-seguridad antes: "+seguridad);                
                String uoAO = (String) seguridad.get("uoAO");
                seguridad.put("uoSeg", spReporteFacadeRemote.findUuooJefe(uoAO)+"%");//4EB2%
                log.debug("buscarUOrgan-seguridad final: "+seguridad);
            }                   
            //ICAPUNAY-MEMO 32-4F3100-2013

			
			T12DAO dao = new T12DAO();
			uo = dao.findByCodDesc(dbpool, criterio, valor, seguridad);
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return uo;

	}
	 
//JRR - 21/06/2010 - FUSION PROGRAMACION	
  /**
   * Metodo usado para buscar intendencias
   * @param datos Map
   * @return List 
   * @throws FacadeException
   * @ejb.interface-method view-type="remote"
   * @ejb.transaction type="NotSupported"
   */
	public List  buscarIntendencias(Map params) throws  FacadeException {

		List intendencias = null;
		
		try {
			if(log.isDebugEnabled()) log.debug("en elFacade");
			DataSource dbpool_sp = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dcsp");
			if(log.isDebugEnabled()) log.debug("en el f1");
			pe.gob.sunat.rrhh.dao.T12DAO dao = new pe.gob.sunat.rrhh.dao.T12DAO(dbpool_sp);
			if(log.isDebugEnabled()) log.debug("en el f2");
			intendencias = dao.findIntByCodDesc(params);
			if(log.isDebugEnabled()) log.debug("en el 3");
		 } catch (DAOException e) {
	      log.error(e, e);
	     	MensajeBean beanM = new MensajeBean();
	      beanM.setMensajeerror(e.getMessage());
	      beanM.setMensajesol("Por favor intente nuevamente, posible error en la BD");
	      throw new FacadeException(this, beanM);
	    } catch (Exception e) {
	      log.error(e, e);
	     	MensajeBean beanM = new MensajeBean();
	      beanM.setMensajeerror(e.getMessage());
	      beanM.setMensajesol("Por favor intente nuevamente.");
	      throw new FacadeException(this, beanM);
	    }
		return intendencias;

	}
//
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList buscarUOPeriodo(String dbpool, String periodo,
			String criterio, String valor)
			throws IncompleteConversationalState, RemoteException {

		ArrayList uo = null;
		BeanMensaje beanM = new BeanMensaje();
		try {

			T12DAO dao = new T12DAO();
			uo = dao.joinWithT1278(dbpool, periodo, criterio, valor);

		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return uo;

	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList buscarUOSinPeriodo(String dbpool, String periodo)
			throws IncompleteConversationalState, RemoteException {

		ArrayList uo = null;
		BeanMensaje beanM = new BeanMensaje();
		try {
			
			T12DAO dao = new T12DAO();
			uo = dao.joinWithT1278SinPeriodo(dbpool, periodo, Constantes.ESTADO_ACTIVO_DEPENDENCIAS);

		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return uo;

	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList buscarPersonal(String dbpool, String criterio,
			String valor, HashMap seguridad)
			throws IncompleteConversationalState, RemoteException {

		ArrayList personal = null;
		BeanMensaje beanM = new BeanMensaje();
		try {
			
			//ICAPUNAY-MEMO 32-4F3100-2013
            HashMap roles = (HashMap) seguridad.get("roles");            
            log.debug("buscarPersonal-roles: "+roles);
            if (roles.get(Constantes.ROL_JEFE) != null) {
            	ReporteFacadeHome spReporteFacadeHome = (ReporteFacadeHome) sl.getRemoteHome(
                                ReporteFacadeHome.JNDI_NAME,      ReporteFacadeHome.class);
            	ReporteFacadeRemote spReporteFacadeRemote = spReporteFacadeHome.create(); 
            	log.debug("buscarPersonal-seguridad antes: "+seguridad);                
                String uoAO = (String) seguridad.get("uoAO");
                seguridad.put("uoSeg", spReporteFacadeRemote.findUuooJefe(uoAO)+"%");//4EB2%
                log.debug("buscarPersonal-seguridad final: "+seguridad);
            }                   
            //ICAPUNAY-MEMO 32-4F3100-2013

			T02DAO dao = new T02DAO();
			personal = dao.joinWithT12T99(dbpool, criterio, valor, seguridad);
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return personal;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList buscarT99Codigo(String dbpool, String criterio,
			String valor, String codTab) throws IncompleteConversationalState,
			RemoteException {

		ArrayList codigos = null;
		BeanMensaje beanM = new BeanMensaje();
		try {

			T99DAO dao = new T99DAO();
			codigos = dao.findByCodTab(dbpool, criterio, valor, codTab);
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return codigos;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public String obtenerFechaSgtesDiasHabiles(String dbpool,
			String fechaReferencia, int numDias)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {
			int diasAgregados = 0;
			T01DAO paramDAO = new T01DAO();
			String fechaSgte = fechaReferencia;

			while (diasAgregados < numDias) {
				//JRR - 07/04/2009
				//fechaSgte = Utiles.dameFechaSiguiente(fechaSgte, 1);

				if (!paramDAO.findByFechaFeriado(dbpool, fechaSgte)
						&& !Utiles.isWeekEnd(fechaSgte)) {
					diasAgregados++;
				}
				fechaSgte = Utiles.dameFechaSiguiente(fechaSgte, 1);
			}
			//PRAC-ASANCHEZ 09/07/2009 - si fechaSgte es dia NO habil
			
			
			if(paramDAO.findByFechaFeriado(dbpool, fechaSgte) || Utiles.isWeekEnd(fechaSgte)){
				boolean esDiaHabil = false;
				while(esDiaHabil == false){
					fechaSgte = Utiles.dameFechaSiguiente(fechaSgte, 1);
					if(!paramDAO.findByFechaFeriado(dbpool, fechaSgte) && !Utiles.isWeekEnd(fechaSgte)){
						esDiaHabil = true;
					}
				}
			}
			//
			return fechaSgte;
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

	}
	
	
	//PRAC-ASANCHEZ 24/06/2009
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 *
	public String fechaFinXDiasHabiles(String dbpool,
			String fechaReferencia, int numDias)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {
			T01DAO paramDAO = new T01DAO();

			String fechaSgte = fechaReferencia;
			if(numDias == 1){
				boolean diaSgteEsDiaHabil = false;
				while(diaSgteEsDiaHabil == false){
					fechaSgte = Utiles.dameFechaSiguiente(fechaSgte, 1); //siempre me devuelve el dia sgte habil
					if(!paramDAO.findByFechaFeriado(dbpool, fechaSgte) && !Utiles.isWeekEnd(fechaSgte)){
						diaSgteEsDiaHabil = true;
					}
				}
			}else{
				if(!paramDAO.findByFechaFeriado(dbpool, fechaSgte) && !Utiles.isWeekEnd(fechaSgte)){
					for(int i=0 ;i<numDias-1; i++){
						fechaSgte = fechaFinXDiasHabiles(dbpool, fechaSgte, 1);
					}
				}
				if(paramDAO.findByFechaFeriado(dbpool, fechaSgte) || Utiles.isWeekEnd(fechaSgte)){
					fechaSgte = fechaFinXDiasHabiles(dbpool, fechaSgte, 1);
					fechaSgte = fechaFinXDiasHabiles(dbpool, fechaSgte, numDias);
				}
			}
			return fechaSgte;
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

	}
	//
	*/
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public int obtenerDiasHabilesDiferencia(String dbpool, String fecha1,
			String fecha2) throws IncompleteConversationalState,
			RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		int numDias = 0;

		try {

			T01DAO paramDAO = new T01DAO();

			//para la verificacion de dias faltantes
			String fIni = Utiles.toYYYYMMDD(fecha1);
			String fFin = Utiles.toYYYYMMDD(fecha2);
			String fAct = fIni;
			String fReal = fecha1;

			//mientras no haya terminado el periodo en proceso
			while (fAct.compareTo(fFin) <= 0) {
				if (!paramDAO.findByFechaFeriado(dbpool, fReal) && !Utiles.isWeekEnd(fReal)) {
					numDias++;
				}
				fReal = Utiles.dameFechaSiguiente(fReal, 1);
				fAct = Utiles.toYYYYMMDD(fReal);
			}
			numDias--;
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return numDias;
	}
	
	//dtarazona
	/** Obtiene el parametro fecha de implentacion del reporte
	 * @throws Exception 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported" 
	 */
	public String findParamByCodTabCodigo( HashMap datos)
			throws FacadeException {
		log.debug("Entrando a findParamByCodTabCodigo.");
		MensajeBean beanM = new MensajeBean();
		//DataSource ds = sl.getDataSource("java:comp/env/jdbc/dcsp");
		T99DAO t99dao = new T99DAO();
        //pe.gob.sunat.rrhh.dao.T99DAO t99dao = new pe.gob.sunat.rrhh.dao.T99DAO(ds);
        
		String estado="";
		try {					
			estado=(t99dao.findByCodParam(datos.get("dbpool").toString(), datos.get("t99cod_tab").toString(), datos.get("t99codigo").toString())).get("t99descrip").toString();
			log.debug("EstadoFac:"+estado);
		} catch (DAOException e) {
	  		log.error(e, e);
	  		beanM.setMensajeerror(e.getMessage());
	  		beanM.setMensajesol("Por favor intente nuevamente, posible error en la BD");
	  		throw new FacadeException(this, beanM);
	  	} catch (Exception e) {
	  		log.error(e, e);
	  		beanM.setMensajeerror(e.getMessage());
	  		beanM.setMensajesol("Por favor intente nuevamente.");
	  		throw new FacadeException(this, beanM);
	  	}
			
		return estado;
	}
	//dtarazona

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public int validaDiasAntesDespues(String dbpool, String mov,
			String fechaRegistro, String fechaInicio, String fechaFin)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		int valida = 0;
		try {

			T1279DAO movDAO = new T1279DAO();

			int numDiasAntes = movDAO.findNumDiasByTipo(dbpool, mov, "1");
			int numDiasDespues = movDAO.findNumDiasByTipo(dbpool, mov, "2");
			log.debug(" ... numDiasAntes="+numDiasAntes+" , numDiasDespues="+numDiasDespues);
			int difAntes = Utiles.obtenerDiasDiferencia(fechaInicio,fechaRegistro);
			int difDespues = Utiles.obtenerDiasDiferencia(fechaRegistro,fechaFin);
			log.debug(" ... difAntes="+difAntes+" , difDespues="+difDespues);
			//fechaInicio <= fechaRegistro <= fechaFin
			if (difAntes >= 0 && difDespues >= 0 && numDiasAntes > 0 && numDiasDespues > 0) {
				valida = -1;
			} else {

				//despues de la fecha de registro
				if (difAntes < 0) {
					if (numDiasAntes > 0) {
						log.debug("...this.obtenerDiasHabilesDiferencia(dbpool,fechaRegistro, fechaInicio) : "+dbpool+",  "+fechaRegistro+",  "+fechaInicio);
						int diasHabilesAntes = this.obtenerDiasHabilesDiferencia(dbpool,fechaRegistro, fechaInicio);
						log.debug("... diasHabilesAntes: "+diasHabilesAntes);
						if (diasHabilesAntes < 0
								|| diasHabilesAntes < numDiasAntes) {
							valida = -1;
						}
					}
				}

				//antes de la fecha de registro
				if (difDespues < 0) {
					if (numDiasDespues > 0) {
						log.debug("...this.obtenerDiasHabilesDiferencia(dbpool,fechaFin, fechaRegistro) : "+dbpool+",  "+fechaFin+",  "+fechaRegistro);
						int diasHabilesDespues = this.obtenerDiasHabilesDiferencia(dbpool, fechaFin,fechaRegistro);
						log.debug(".. diasHabilesDespues="+diasHabilesDespues);
						if (diasHabilesDespues < 0
								|| diasHabilesDespues > numDiasDespues) {
							valida = 1;
						}
					}
				}
			}
						
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		return valida;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public String validarMovimiento(String dbpool, String codPers,
			String fecha, String hIni, String hFin, String tipo, String nvoMov,
			boolean tieneRef, String reloj1, String reloj2,
			BeanTurnoTrabajo turno) throws IncompleteConversationalState,
			RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		String mensaje = "";
		try {
			float cantidad = 0;

			//Primero verificamos que el tipo movimiento sea coherente
			T1279DAO dao = new T1279DAO();
			T1270DAO turnoDAO = new T1270DAO();
			
			if (!dao.findByFlags(dbpool, nvoMov, tipo, true)) {
				mensaje = "Tipo de calificacion no es permitida para una marcacion de ";
				mensaje += tipo.trim().equals("0") ? "entrada" : (tipo.trim()
						.equals("2") ? "salida" : "movimiento");
				mensaje += ".";
			}

			if (mensaje.trim().equals("")) {
				if (nvoMov.trim().equals(Constantes.MOV_OTRO_EDIFICIO)) {
					if (reloj1.trim().equals(reloj2.trim())) {
						mensaje = "No puede ser un movimiento hacia otro edificio ya que los relojes son iguales.";
					}
				}

				if (mensaje.trim().equals("")) {
					if (tipo.equals("3")) {
						//Validamos refrigerio
						if (dao.findByFlags(dbpool, nvoMov, "1", false)) {
							if (tieneRef) {
								mensaje = "El dia ya posee una calificacion de refrigerio.";
							}

							if (mensaje.trim().equals("")) {
								float antes = Utiles
										.calculaAcumulado(
												Constantes.MINUTO,
												Utiles.stringToTimestamp(fecha
														+ " " + hIni),
												Utiles
														.stringToTimestamp(fecha
																+ " "
																+ turno
																		.getHoraIniRefrigerio()));

								if (antes > 0) {
									mensaje = "El movimiento no puede tener una calificacion de refrigerio.";
								} else {
									float durante = Utiles.calculaAcumulado(
											Constantes.MINUTO, Utiles
													.stringToTimestamp(fecha
															+ " " + hIni),
											Utiles.stringToTimestamp(fecha
													+ " " + hFin));

									float despues = Utiles
											.calculaAcumulado(
													Constantes.MINUTO,
													Utiles
															.stringToTimestamp(fecha
																	+ " "
																	+ turno
																			.getHoraFinRefrigerio()),
													Utiles
															.stringToTimestamp(fecha
																	+ " "
																	+ hIni));

									if (despues < 0) {
										if (durante <= turno
												.getMinutosRefrigerio()) {
											if (nvoMov
													.trim()
													.equals(
															Constantes.MOV_EXCESO_REFRIGERIO)) {
												mensaje = "No puede ser una calificacion de exceso de refrigerio.";
											}
										} else {
											if (nvoMov.trim().equals(
													Constantes.MOV_REFRIGERIO)) {
												mensaje = "La unica calificacion de refrigerio aceptada es Exceso en refrigerio.";
											}
										}
									} else {
										if (nvoMov.trim().equals(
												Constantes.MOV_REFRIGERIO)
												|| nvoMov
														.trim()
														.equals(
																Constantes.MOV_EXCESO_REFRIGERIO)) {
											mensaje = "El movimiento no puede tener una calificacion de refrigerio.";
										}

									}

								}
							}
						}

					} else {

						if (turno != null
								&& !turno.getTurno().trim().equals("")) {
							if (tipo.equals("0")) {
								//Validamos los movimientos de entrada
								cantidad = Utiles.calculaAcumulado(
										Constantes.MINUTO, Utiles
												.stringToTimestamp(fecha + " "
														+ turno.getHoraIni()),
										Utiles.stringToTimestamp(fecha + " "
												+ hIni));

								if (cantidad == 0) {
									if (nvoMov.trim().equals(
											Constantes.MOV_ENTRADA_ANTICIPADA)
											|| nvoMov
													.trim()
													.equals(
															Constantes.MOV_TARDANZA_CON_DESCUENTO)
											|| nvoMov
													.trim()
													.equals(
															Constantes.MOV_TARDANZA_SIN_DESCUENTO)) {
										mensaje = "Calificacion de entrada no permitida.";
									}
								} else if (cantidad < 0) {
									if (nvoMov.trim().equals(
											Constantes.MOV_TARDANZA_CON_DESCUENTO)
											|| nvoMov
													.trim()
													.equals(
															Constantes.MOV_TARDANZA_SIN_DESCUENTO)) {
										mensaje = "La calificacion no puede ser una tardanza.";
									}
								} else {
									if (cantidad > turno.getTolera()) {
										cantidad = Utiles
												.calculaAcumulado(
														Constantes.MINUTO,
														Utiles
																.stringToTimestamp(fecha
																		+ " "
																		+ turno
																				.getHoraLimite()),
														Utiles
																.stringToTimestamp(fecha
																		+ " "
																		+ hIni));

										if (cantidad <= 0) {

											if (nvoMov
													.trim()
													.equals(
															Constantes.MOV_ENTRADA_ANTICIPADA)
													|| nvoMov
															.trim()
															.equals(
																	Constantes.ENTRADA_NORMAL)
													|| nvoMov
															.trim()
															.equals(
																	Constantes.MOV_TARDANZA_SIN_DESCUENTO)) {
												mensaje = "La unica calificacion de entrada permitida es la Tardanza con descuento.";
											}
										} else {
											if (nvoMov
													.trim()
													.equals(
															Constantes.MOV_ENTRADA_ANTICIPADA)
													|| nvoMov
															.trim()
															.equals(
																	Constantes.ENTRADA_NORMAL)
													|| nvoMov
															.trim()
															.equals(
																	Constantes.MOV_TARDANZA_SIN_DESCUENTO)
													|| nvoMov
															.trim()
															.equals(
																	Constantes.MOV_TARDANZA_CON_DESCUENTO)) {

												mensaje = "Ninguna calificacion de entrada es aplicable.";
											}
										}
									} else {
										if (nvoMov
												.trim()
												.equals(
														Constantes.MOV_ENTRADA_ANTICIPADA)
												|| nvoMov
														.trim()
														.equals(
																Constantes.ENTRADA_NORMAL)
												|| nvoMov
														.trim()
														.equals(
																Constantes.MOV_TARDANZA_CON_DESCUENTO)) {
											mensaje = "La unica calificacion de entrada aplicable es Tardanza sin descuento.";
										}
									}
								}

							} else if (tipo.equals("2")) {
								//Validamos los movimientos de salida
								cantidad = Utiles.calculaAcumulado(
										Constantes.MINUTO, Utiles
												.stringToTimestamp(fecha + " "
														+ turno.getHoraFin()),
										Utiles.stringToTimestamp(fecha + " "
												+ hIni));

								//se agregan las horas de compensacion
								cantidad -= turnoDAO.findHorasCompensa(dbpool,codPers,fecha);
								
								//salida anticipada
								if (cantidad > 0) {
									if (nvoMov
											.trim()
											.equals(
													Constantes.MOV_SALIDA_NO_AUTORIZADA)) {
										mensaje = "El movimiento no puede ser una salida no autorizada.";
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		return mensaje;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public String obtenerUOVisibilidad(String dbpool, String codUO)
			throws IncompleteConversationalState, RemoteException {

		String uo = "";
		BeanMensaje beanM = new BeanMensaje();
		try {
			T12DAO dao = new T12DAO();
			uo = dao.findUOByVisibilidad(dbpool, codUO);
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		return uo;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public int validaRango(String dbpool, String mov, String fechaBase,
			String fechaEvaluada) throws IncompleteConversationalState,
			RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		int valida = 0;
		try {

			T1279DAO movDAO = new T1279DAO();

			int numDiasAntes = 0;
			int numDiasDespues = 0;

			int difDias = Utiles.obtenerDiasDiferencia(fechaBase, fechaEvaluada);

			//fechaEvaluada > fechaBase
			if (difDias > 0) {
				numDiasDespues = movDAO.findNumDiasByTipo(dbpool, mov, "2");
				int diasHabDif = this.obtenerDiasHabilesDiferencia(dbpool,
						fechaBase, fechaEvaluada);
				
				if (numDiasDespues!=0 && numDiasDespues < diasHabDif) {
					valida = 1; //Dias despues del rango
				}
			} 
			else if (difDias < 0) {
				numDiasAntes = movDAO.findNumDiasByTipo(dbpool, mov, "1");
				int diasHabDif = this.obtenerDiasHabilesDiferencia(dbpool,
						fechaEvaluada, fechaBase);
				
				if (numDiasAntes!=0 && numDiasAntes < diasHabDif) {
					valida = -1; //Dias antes del rango
				}
			}

		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
	
		return valida;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void procesarMarcaciones(HashMap mapa, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {

			log.debug("Llego a procesarMarcaciones");
			
			QueueDAO qd = new QueueDAO();
			String path = (String)mapa.get("ruta");
			log.debug("mapa : "+mapa);
			
			if (path.toUpperCase().endsWith(".DBF")){
				qd.encolaProceso(ProcesoFacadeHome.JNDI_NAME,
						"procesarMarcacionesDBF", mapa, usuario);	
			}
			else{
				qd.encolaProceso(ProcesoFacadeHome.JNDI_NAME,
						"procesarMarcaciones", mapa, usuario);
			}
			

		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void generarRegistros(HashMap mapa, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {
			log.debug("INICIO DE METODO AsistenciaFacade - generarRegistros");
			QueueDAO qd = new QueueDAO();
			
			//eliminamos las marcaciones impares
			ProcesoFacadeHome facadeHome = (ProcesoFacadeHome) sl.getRemoteHome(ProcesoFacadeHome.JNDI_NAME,
	          							ProcesoFacadeHome.class);
			
			ProcesoFacadeRemote remote = facadeHome.create();	
			
			//ASANCHEZZ 20100412
			if(log.isDebugEnabled())log.debug("AsistenciaFacade - generarRegistros - mapa: " + mapa);
			/*
			remote.eliminarMarcacionesImpares((String)mapa.get("dbpool"), 
					(String)mapa.get("fechaIni"), 
					(String)mapa.get("fechaFin"),
					(String)mapa.get("criterio"), 
					(String)mapa.get("valor"));
			*/
			remote.eliminarMarcacionesImpares(mapa);

			/*
			T1275DAO daoMarca = new T1275DAO();
			ArrayList lista = daoMarca.joinWithT02Nested(
						(String)mapa.get("dbpool"), 
						(String)mapa.get("fechaIni"), 
						(String)mapa.get("fechaFin"),
						(String)mapa.get("criterio"), 
						(String)mapa.get("valor"));
			*/
			
			DataSource dgsp = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dgsp");
			pe.gob.sunat.rrhh.dao.T02DAO t02dao = new pe.gob.sunat.rrhh.dao.T02DAO(dgsp);
			if(log.isDebugEnabled())log.debug("AsistenciaFacade - generarRegistros - mapa: " + mapa);
			//List lista = t02dao.buscarPersonal(mapa); //ICAPUNAY 13/06/2012 PASE 2012-381 AJUSTES A CALIFICACION DE ASISTENCIA
			List lista = t02dao.buscarPersonal_Calificacion(mapa); //ICAPUNAY 13/06/2012 PASE 2012-381 AJUSTES A CALIFICACION DE ASISTENCIA
			
			log.debug("lista busca personal.size-total :"+lista.size());			
			log.debug("lista personal buscado:"+lista);
			//FIN

			int total = lista != null ? lista.size() : 0;

			int numPartes = 32; //ICAPUNAY 13/06/2012 PASE 2012-381 AJUSTES A CALIFICACION DE ASISTENCIA
			//int numPartes = 128;  // JVV-07/05/2012 GENERACION ASISTENCIA INSTITUCIONAL
			log.debug("numPartes:"+numPartes);
			int limiteInferior = 0;
			int limiteSuperior = 0;
			int grupo = (total / numPartes);
			if (total < numPartes) {
				numPartes = 1;
			}

			for (int i = 0; i < numPartes; i++) {

				limiteSuperior += grupo;
				if (i == numPartes - 1) {
					limiteSuperior = total;
				}

				log.info("Enviando grupo " + (i + 1) + " ("+ limiteInferior + " - " + limiteSuperior + ")");
				log.debug("Enviando grupo " + (i + 1) + " ("+ limiteInferior + " - " + limiteSuperior + ")");

				//seteamos los limites a procesar
				mapa.put("limiteInferior", "" + limiteInferior);
				mapa.put("limiteSuperior", "" + limiteSuperior);
				mapa.put("observacion", "Registro de asistencias (" + (String)mapa.get("fechaIni") + "-"+
						//(String)mapa.get("fechaFin")+"). Grupo " + (i + 1));
				(String)mapa.get("fechaFin")+"). Criterio : "+  mapa.get("criterio").toString().trim() + " - " + mapa.get("valor").toString().toUpperCase().trim() + ". Grupo " + (i + 1));						

				qd.encolaProceso(ProcesoFacadeHome.JNDI_NAME,"generarRegistroAsistencia", mapa, usuario);				
				//remote.generarRegistroAsistencia(mapa, usuario);//PARA PRUEBAS LOCALES
				limiteInferior = limiteSuperior + 1;
			}								

		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void procesarCalificacion(HashMap mapa, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {

			MantenimientoFacadeHome facadeHome = (MantenimientoFacadeHome) sl.getRemoteHome(
							MantenimientoFacadeHome.JNDI_NAME,
							MantenimientoFacadeHome.class);
			MantenimientoFacadeRemote mantenimiento = facadeHome.create();

			String dbpool = (String) mapa.get("dbpool");
			String fecha = (String) mapa.get("fecha");

			String periodo = mantenimiento.periodoCerradoAFecha(dbpool, fecha);

			if (!periodo.equals("")) {
				throw new IncompleteConversationalState("El perÃƒÂ­odo " + periodo
						+ " se encuentra cerrado.");
			} else {

				QueueDAO qd = new QueueDAO();
				qd.encolaProceso(ProcesoFacadeHome.JNDI_NAME,
						"procesarCalificacion", mapa, usuario);
			}

		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void generarDataReloj(HashMap mapa, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {

			QueueDAO qd = new QueueDAO();
			qd.encolaProceso(ProcesoFacadeHome.JNDI_NAME,
					"generarDataReloj", mapa, usuario);

		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void cerrarAsistencia(HashMap mapa, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {

			MantenimientoFacadeHome facadeHome = (MantenimientoFacadeHome) sl.getRemoteHome(
							MantenimientoFacadeHome.JNDI_NAME,
							MantenimientoFacadeHome.class);
			MantenimientoFacadeRemote mantenimiento = facadeHome.create();
			
			String dbpool = (String) mapa.get("dbpool");
			String periodo = (String) mapa.get("periodo");

//			T72DAO t72dao = new T72DAO(dbpool);
			
			boolean cerrado = mantenimiento.periodoCerradoAFecha(dbpool,
					periodo, Utiles.obtenerFechaActual());

//			boolean enviadoPlanillas = false;
			
			if (cerrado) {
				throw new IncompleteConversationalState("El perÃ­odo " + periodo
						+ " se encuentra cerrado.");
			} else {
/*				//JRR
				enviadoPlanillas = t72dao.verificarEnvioPlanillas(periodo);

				if (enviadoPlanillas) {
					throw new IncompleteConversationalState("El perÃ­odo " + periodo
							+ " ya fue enviado a Planillas.");
				} else { */
					QueueDAO qd = new QueueDAO();
					qd.encolaProceso(ProcesoFacadeHome.JNDI_NAME, "cerrarAsistencia", mapa, usuario);
//				}
			}
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void procesarPases(HashMap datos)
		throws IncompleteConversationalState, RemoteException {
		
		try {
			
			String codPers = (String)datos.get("codPers");
			ArrayList lista = (ArrayList)datos.get("lista");
			
			T1275CMPHome home = (T1275CMPHome)sl.getLocalHome(T1275CMPHome.JNDI_NAME);
			T1275CMPLocal cmpLocal = null;
			
			HashMap pase = null;
			
			if (lista!=null && lista.size()>0){				
				for (int i = 0; i < lista.size(); i++) {
				
					pase = (HashMap)lista.get(i);
					
					try{						
						cmpLocal = home.findByPrimaryKey(
								new T1275CMPPK(
									(String)pase.get("cod_pers"),
									new BeanFechaHora((String)pase.get("fecha")).getSQLDate(),
									(String)pase.get("hora"),
									(String)pase.get("reloj")
								));
						
						cmpLocal.remove();
						
					}
					catch(Exception e){
						throw new Exception("La marcacion de las "+(String)pase.get("hora")+" ya ha sido calificada. Imposible modificar.");
					}		
					
					try{						
						cmpLocal = home.create(codPers,
								new BeanFechaHora((String)pase.get("fecha")).getSQLDate(),
								(String)pase.get("hora"),
								(String)pase.get("reloj")
								);
												
						cmpLocal.setCuserCrea((String)datos.get("usuario"));	
						cmpLocal.setFcreacion(new Timestamp(System.currentTimeMillis()));
						cmpLocal.setEstado(Constantes.ACTIVO);
						cmpLocal.setCodPase((String)pase.get("cod_pers"));						
						
					}
					catch(Exception e){
						throw new Exception("Error al modificar la marcacion de las "+(String)pase.get("hora")+".");
					}														
				}				
			}
			
		} catch (Exception e) {
			log.error(e,e);
			BeanMensaje beanM = new BeanMensaje();
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public boolean esJefeEncargadoDelegado(HashMap datos) {

		boolean esJefe = false;
		try {
			
			T12DAO daoUO = new T12DAO();
			
			BeanT12 uo = daoUO.findByCodigo(
					(String)datos.get("dbpool"), 
					(String)datos.get("codUO"));
			
			String codPers = (String)datos.get("codPers");
			String codJefat = uo.getT12cod_jefat();
			String codEncar = uo.getT12cod_encar();

			log.debug("esJefeEncargadoDelegado : ");
			log.debug("codPers : "+codPers);
			log.debug("codJefat : "+codJefat);
			log.debug("codEncar : "+codEncar);
			
			//verificamos si es jefe o encargado
			if ( (codJefat != null && codJefat.trim().equals(codPers.trim())) || 
				 (codEncar != null && codEncar.trim().equals(codPers.trim()))){
				return true;
			}
			else{
				//obtenemos el delegado de la unidad para dicha opcion
				HashMap delegado = daoUO.findDelegado(datos); 
				if (delegado!=null && !delegado.isEmpty()){
					if (((String)delegado.get("t02cod_pers")).equals(codPers.trim())) return true;
					else return false;
				}
				else return false;	
			}
		} catch (Exception e) {
			esJefe = false;
		}
		return esJefe;
	}
	
	//ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public boolean esJefeEncargadoDelegadoSolicitudes(HashMap datos) {
		log.debug("esJefeEncargadoDelegadoSolicitudes(datos): "+datos);	
		boolean habilitado = false;
		try {
			
			T12DAO daoUO = new T12DAO();
			ArrayList uuoosDelegadas = new ArrayList();
			ArrayList uuoos = new ArrayList();
			
			log.debug("esJefeEncargadoDelegadoSolicitudes(llego)");	
			uuoos = daoUO.findJefeEncargadoInUUOOs(datos);
			log.debug("esJefeEncargadoDelegadoSolicitudes(uuoos): "+uuoos);	
			
			//verificamos si el usuario es jefe o encargado de alguna unidad
			if (uuoos!=null && !uuoos.isEmpty()){
				log.debug("esJefeEncargadoDelegadoSolicitudes(entro1) ");	
				habilitado=true;
			}
			else{
				log.debug("esJefeEncargadoDelegadoSolicitudes(entro2) ");	
				//verificamos si el usuario es delegado de alguna unidad
				uuoosDelegadas = daoUO.findDelegadoInUUOOs(datos); 
				if (uuoosDelegadas!=null && !uuoosDelegadas.isEmpty()){
					log.debug("esJefeEncargadoDelegadoSolicitudes(uuoosDelegadas): "+uuoosDelegadas);	
					habilitado=true;				
				}
				else habilitado=false;
			}
		} catch (Exception e) {
			habilitado = false;
		}
		return habilitado;
	}
	//ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
	
	/** WERR-PAS20155E230300132
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public boolean esDelegadoEncargadoPapeleta(HashMap datos) {
		boolean esDelegado = false;
		try {
			T12DAO daoUO = new T12DAO();
			BeanT12 uo = daoUO.findByCodigo(
					(String)datos.get("dbpool"), 
					(String)datos.get("codUO"));	
			String codPers = (String)datos.get("codPers");
			String codJefat = uo.getT12cod_jefat();
			String codEncar = uo.getT12cod_encar();
			log.debug("esDelegadoEncargadoPapeleta : ");
				ServiceLocator sl = ServiceLocator.getInstance();
				//verificacion de si es delegado, a partir de su registro y opcion
				HashMap datosPapeleta = new HashMap();
				T1595DAO daot1595 = new T1595DAO(sl.getDataSource("java:comp/env/jdbc/dgsp"));
				datosPapeleta.put("nroRegistro", codPers);
				datosPapeleta.put("opcion", datos.get("codOpcion"));
				if(daot1595.isDelegado2(datosPapeleta))return true;
		} catch (Exception e) {
			esDelegado = false;
		}
		return esDelegado;
	}		
	//end WERR-PAS20155E230300132
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void registrarManual(HashMap datos)
		throws IncompleteConversationalState, RemoteException {
		
		try {
			
			T1270DAO turnoDAO = new T1270DAO();
			BeanTurnoTrabajo turno = turnoDAO.joinWithT45ByCodFecha(
					(String)datos.get("dbpool"), 
					(String)datos.get("codPersManual"), 
					(String)datos.get("fechaManual"));
			
			if (turno!=null){
				
				T1275CMPHome home = (T1275CMPHome)sl.getLocalHome(T1275CMPHome.JNDI_NAME);
				T1275CMPLocal cmpLocal = null;
				
				try{						
					cmpLocal = home.create(
							(String)datos.get("codPersManual"),
							new BeanFechaHora((String)datos.get("fechaManual")).getSQLDate(),
							(String)datos.get("txtMarca"),
							Constantes.RELOJ_MANUAL
							);
											
					cmpLocal.setCuserCrea((String)datos.get("usuario"));	
					cmpLocal.setFcreacion(new Timestamp(System.currentTimeMillis()));
					cmpLocal.setEstado(Constantes.ACTIVO);												
				}
				catch(Exception e){
					throw new Exception("Error al registrar la marcacion de las "+(String)datos.get("txtMarca")+".");
				}				
			}
			else{
				throw new IncompleteConversationalState("El trabajador no posee un turno asignado para el "+(String)datos.get("fechaManual")+".");
			}
			
		} catch (Exception e) {
			log.error(e,e);
			BeanMensaje beanM = new BeanMensaje();
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
	}	
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public boolean esSupervisor(String dbpool, String codPers, String mov, String codUO)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		boolean esSupervisor = false;
		try {

			T1933DAO solicitudDAO = new T1933DAO();
			esSupervisor = solicitudDAO.esSupervisor(dbpool, codPers, mov, codUO);
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return esSupervisor;
	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public ArrayList cargarRelojes(String dbpool, String estado)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        ArrayList relojes = null;
        try {

            T1280DAO dao = new T1280DAO();
            relojes = dao.findByEstId(dbpool, estado);
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }
        return relojes;
    }
    
  //ICAPUNAY - FORMATIVAS
    /**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public ArrayList calificarAsistenciasFormativas(String dbpool, String[] params,
			//ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII ArrayList lista, String usuario, String fecha)
			ArrayList lista, String usuario, String fecha, String regimenModalidad) //ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		ArrayList mensajes = new ArrayList();
		try {

			/*
			ProcesoFacadeHome facadeHome = (ProcesoFacadeHome) sl.getRemoteHome(ProcesoFacadeHome.JNDI_NAME,
					ProcesoFacadeHome.class);
			ProcesoFacadeRemote facadeRemote = facadeHome.create();*/
			
			MantenimientoFacadeHome mantHome = (MantenimientoFacadeHome) sl.getRemoteHome(
							MantenimientoFacadeHome.JNDI_NAME,
							MantenimientoFacadeHome.class);
			MantenimientoFacadeRemote mantenimiento = mantHome.create();
			//ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII String periodo = mantenimiento.periodoCerradoAFecha(dbpool, fecha);
			String periodo = mantenimiento.periodoCerradoAFechaPorRegimen(dbpool, fecha,regimenModalidad); //ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII

			if (!periodo.equals("")) {
				throw new IncompleteConversationalState("El periodo " + periodo
						+ " se encuentra cerrado.");
			} else { // periodo no esta cerrado

				HashMap mAsis = null;
				
				T1271DAO dao = new T1271DAO(dbpool);
				//String nvoMov = "";

				if (lista != null && lista.size() > 0) {
					mAsis = (HashMap) lista.get(0);
					Map prms = new HashMap();
					prms.put("cod_pers", mAsis.get("cod_pers").toString());
					prms.put("fecha", Utiles.dateToString((Date)mAsis.get("fing")));
					log.debug("metodo calificarAsistenciasFormativas... prms: "+prms);
					if (dao.findByCodPersFFinNull(prms)) {
						throw new IncompleteConversationalState(
								"No se puede registrar una calificacion en un dia con marcaciones impares,"
										+ " primero debe ser registrada la marcacion faltante.");
					}
					
					//ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
					T8167DAO climaDao = new T8167DAO(dbpool); 
					String fechaClima = "";					
					Map mapClima = climaDao.findAutorizaEnMesByRegByFecha(mAsis.get("cod_pers").toString().trim(), fecha);
					log.debug("fecha: "+fecha);
					log.debug("mapClima formativas: "+mapClima);
					if (mapClima!=null && !mapClima.isEmpty()) {
						fechaClima = mapClima.get("fec_aut_desc").toString();
						log.debug("fechaClima: "+fechaClima);
						//ICAPUNAY - PAS20171U230300040 - Refrigerio Clima Laboral
						if(!fecha.trim().equals(fechaClima)){							
							for (int i = 0; i < lista.size(); i++) {
								log.debug("params[i]: "+params[i]);
								mAsis = (HashMap) lista.get(i);
								log.debug("mAsis.get(mov): "+mAsis.get("mov"));
								if (params[i].trim().equals(Constantes.MOV_REFRI_CLIMALABORAL)){
								//if (!mAsis.get("mov").toString().trim().equals(params[i].trim()) && params[i].equals(Constantes.MOV_REFRI_CLIMALABORAL)){									
									log.debug("params[i]=136");
									throw new IncompleteConversationalState("El colaborador ya tiene registrada una calificación de Clima laboral en la fecha " + fechaClima+ ". Sólo puede registrar una actividad de clima laboral por mes para cada colaborador.");
								}								
							}							
						}
						//throw new IncompleteConversationalState("El colaborador ya tiene registrada una calificación de Clima laboral en la fecha " + fechaClima+ ". Sólo puede registrar una actividad de clima laboral por mes para cada colaborador.");
						//FIN ICAPUNAY - PAS20171U230300040 - Refrigerio Clima Laboral
					}
					//FIN ICAPUNAY
				}

				//boolean tieneRefrigerio = false;
				String msg = "";
				//String tipoMov = "";
				//String reloj1 = "";
				//String reloj2 = "";
				//T1275DAO marcaDAO = new T1275DAO();
				//BeanMarcacion bMarca = null;
				int errores = -1;
				T1270DAO daoTurno = new T1270DAO();
				BeanTurnoTrabajo turno = null;
				//int numEntr = 0;
				//int numSali = 0;
				//int numRefr = 0;
				//T1279DAO daoTipo = new T1279DAO();

				if (lista != null && lista.size() > 0) {
					mAsis = (HashMap) lista.get(0);
					turno = daoTurno.joinWithT45ByCodFecha(dbpool, mAsis.get("cod_pers").toString(), Utiles.dateToString((Date)mAsis.get("fing")));
					if (turno  == null) {
						throw new IncompleteConversationalState(
								"El Colaborador no tiene turno asignado. Favor comunicarse con el analista.");
					}

				}
				
				int nroNoSelec=0;
				for (int i = 0; i < lista.size(); i++) {
					if (params[i] != null) {
						if (params[i].equals("-1")){
							nroNoSelec=nroNoSelec+1;
						}				
						
					}					
				}	
				if(nroNoSelec==lista.size()){
					throw new IncompleteConversationalState("Seleccione por lo menos una observaciÃ³n para un tipo de marcaciÃ³n a calificar.");
				}

				for (int i = 0; i < lista.size(); i++) {
					mAsis = (HashMap) lista.get(i);
					msg = "";
					//tipoMov = "";
					//reloj1 = "";
					//reloj2 = "";

					if (params[i] != null) {
						
						/*
						if (i == 0) {
							tipoMov = "0";
							nvoMov = Constantes.ENTRADA_NORMAL;
						} else if (i == (lista.size() - 1)) {
							tipoMov = "2";
							nvoMov = Constantes.SALIDA_NORMAL;
						} else {
							tipoMov = "3";
							nvoMov = Constantes.MOV_EN_OBSERVACION;
						}
						*/

						if (!params[i].equals("-1")){//movimiento seleccionado es permiso subvencionado o no subvencionado

								if (!params[i].trim().equals(mAsis.get("mov").toString().trim())) {
									if (!(mAsis.get("estado_id").toString().trim().equals(Constantes.PRE_CALIFICACION_ASIS) || mAsis.get("estado_id").toString().trim().equals(Constantes.PAPELETA_PROCESADA) ||//Papeletas 
											mAsis.get("estado_id").toString().trim().equals(Constantes.ASISTENCIA_CALIFICADA)
											)) {
												throw new IncompleteConversationalState("No es posible modificar una papeleta.");//Ã‚Â¿Porque se da este mensaje?
									}
									
									//validarMovimiento(reglas) - para formativas debe ser diferente
									/*
									msg = validarMovimiento(dbpool, mAsis.get("cod_pers").toString(),
											Utiles.dateToString((Date)mAsis.get("fing")),
											mAsis.get("hing").toString(), mAsis.get("hsal").toString(), tipoMov,
											params[i].trim(), tieneRefrigerio,
											reloj1, reloj2, turno);
									*/	
									if(mAsis.get("mov").toString().trim().equals(Constantes.ENTRADA_NORMAL) ||mAsis.get("mov").toString().trim().equals(Constantes.SALIDA_NORMAL) ){
										msg = "Tipo de calificacion no es permitida para una marcacion de "+mAsis.get("descrip").toString().trim()+".";										
									}

									if (msg.trim().equals("")) {
										//modificarCalificacion
										modificarCalificacion(dbpool, mAsis.get("cod_pers").toString(),
												mAsis.get("periodo").toString(), mAsis.get("u_organ").toString(),
												Utiles.dateToString((Date)mAsis.get("fing")), mAsis.get("hing").toString(),
												params[i].trim(), usuario);
										/*
										if (mAsis.get("mov") != null && daoTipo.findByFlags(dbpool,params[i].trim(),"2",true)) {
											tieneRefrigerio = true;
										}*/
									}
								}
							

							if (!msg.trim().equals("")) {
								errores++;
								msg = "Error para el registro "
										+ mAsis.get("cod_pers").toString() + " del "
										+ Utiles.dateToString((Date)mAsis.get("fing")) + " - "
										+ mAsis.get("hing").toString() + ". " + msg;
								mensajes.add(errores, msg);
							}

						}
					}
				}
			}
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		return mensajes;
	}
	//FIN ICAPUNAY - FORMATIVAS
	
	//ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public ArrayList buscarUnidades(String dbpool, String registro)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        ArrayList unidades = null;
        try {

            pe.gob.sunat.rrhh.dao.T12DAO dao = new pe.gob.sunat.rrhh.dao.T12DAO(dbpool);
            unidades = dao.findUUOOsByJefeDelegado(registro);
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }
        return unidades;
    }
    
    /**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public ArrayList buscarUnidadesNoDependientes(String dbpool, String registro, String nivel1, String nivel2)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        ArrayList unidades = null;
        try {

            pe.gob.sunat.rrhh.dao.T12DAO dao = new pe.gob.sunat.rrhh.dao.T12DAO(dbpool);
            unidades = dao.findUUOOsFromNivelByJefeDelegado(registro,nivel1,nivel2);
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }
        return unidades;
    }
    
    /**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public ArrayList buscarSubUnidades(String dbpool, String unidadSinCeros, String unidad, String nivel)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        ArrayList subunidades = null;
        try {

            pe.gob.sunat.rrhh.dao.T12DAO dao = new pe.gob.sunat.rrhh.dao.T12DAO(dbpool);
            subunidades = dao.findUUOOsByUO(unidadSinCeros,unidad,nivel);
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }
        return subunidades;
    }
    
    /**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public List findColaboradoresSinClimaLicenciaVacacionesByUOByFecha(String dbpool, Map datos)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        List colaboradores = null;
        try {

            pe.gob.sunat.rrhh.asistencia.dao.T8167DAO dao = new pe.gob.sunat.rrhh.asistencia.dao.T8167DAO(dbpool);
            colaboradores = dao.findColaboradoresSinClimaLicenciaVacacionesByUOByFecha(datos);
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }
        return colaboradores;
    }
    
    /**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void registrarPermisoRefrigerio(String dbpool,Map datos)
			throws IncompleteConversationalState, RemoteException {

		DataSource dsSecuence= sl.getDataSource("java:/comp/env/jdbc/dcbdseq");
		BeanMensaje beanM = new BeanMensaje();		
		List listaParticipantes = new ArrayList();	
		boolean inserto=false;	
		int Secuencia = 0;
		
		try {						
			    
	        T8167DAO t8167dao = new T8167DAO(dbpool);       
	        String colaboradoresConcatenados = (String) datos.get("colaboradores");	        
	   
			if(colaboradoresConcatenados!=null && !colaboradoresConcatenados.trim().equals("")){
				StringTokenizer st = new StringTokenizer(colaboradoresConcatenados, "_");			
				while (st.hasMoreTokens()) {
					listaParticipantes.add(st.nextToken().trim());						
				}	
			}					
			
			if(listaParticipantes!=null && !listaParticipantes.isEmpty()){				               	    		  
				for (int i = 0; i < listaParticipantes.size(); i++) {
					Secuencia = new SequenceDAO().getSequence(dsSecuence,"SET8167");
					log.debug("Secuencia: "+Secuencia);
					datos.put("cod_pers", ((String)listaParticipantes.get(i)).trim());
					//datos.put("num_seqcli", Secuencia);//FALTA
					datos.put("num_seqcli", String.valueOf(Secuencia));
					inserto=t8167dao.insertRegistroAutorizaCli(datos);
					if (inserto==true){
						log.debug("SI se inserto la autorizacion refrigerio para el colaborador: "+ datos.get("cod_pers") +" y fecha: "+datos.get("fec_aut"));
					}else{
						log.debug("NO se inserto la autorizacion refrigerio para el colaborador: "+ datos.get("cod_pers") +" y fecha: "+datos.get("fec_aut"));
					}
				}			
			}			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

	}
    //ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
	
	//ICAPUNAY - PAS20171U230300040 - Refrigerio Clima Laboral
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public ArrayList buscarUnidadesByNivelByReporte(String dbpool,String nivel,String registro)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        ArrayList unidades = null;
        try {

            pe.gob.sunat.rrhh.dao.T12DAO dao = new pe.gob.sunat.rrhh.dao.T12DAO(dbpool);
            unidades = dao.findUUOOsFromNivelByReporta(nivel,registro);
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }
        return unidades;
    }
    //FIN
	
    
    //PAS20181U230200023-Licencia por enfermedad
    /**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public ArrayList buscarT5864Parametro(String criterio, String valor ,String codTab)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        ArrayList diagnosticos = null;
    	DataSource dcsp = ServiceLocator.getInstance().getDataSource("jdbc/dcsp");
    	Map filtro = new HashMap();
        try {
        	T5864DAO dao = new  T5864DAO(dcsp);
        	filtro.put("cod_tab", codTab);        	
        	filtro.put("cod_tip_desc", "D");
        	filtro.put("cod_estado", "A");
        	
        	if (criterio.equals("codigo")) {
        		filtro.put("des_corta", "%"+valor +"%");	 
        	}
        	
        	if (criterio.equals("descripcion")) {
        		filtro.put("des_larga_con_acento", "*"+Utiles.setacento(valor)+"*" );	 
        	}
            diagnosticos = (ArrayList) dao.findByFiltro(filtro) ;
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }
        return diagnosticos;
    }
    
    /**
   	 * @ejb.interface-method view-type="remote"
   	 * @ejb.transaction type="NotSupported"
   	 */
       public ArrayList buscarT5864ParametroPorFiltro(Map filtro)
               throws IncompleteConversationalState, RemoteException {

           BeanMensaje beanM = new BeanMensaje();
           ArrayList diagnosticos = null;
       	DataSource dcsp = ServiceLocator.getInstance().getDataSource("jdbc/dcsp");
        
           try {
           	T5864DAO dao = new  T5864DAO(dcsp); 
               diagnosticos = (ArrayList) dao.findByFiltro(filtro) ;
           } catch (Exception e) {
           	log.error(e,e);
               beanM.setMensajeerror(e.getMessage());
               beanM.setMensajesol("Por favor intente nuevamente.");
               throw new IncompleteConversationalState(beanM);
           }
           return diagnosticos;
       }
	
}
