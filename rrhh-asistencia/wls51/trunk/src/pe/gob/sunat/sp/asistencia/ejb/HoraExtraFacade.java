package pe.gob.sunat.sp.asistencia.ejb;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pe.gob.sunat.batch.dao.QueueDAO;
import pe.gob.sunat.framework.core.pattern.ServiceLocator;
import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.rrhh.asistencia.dao.T1273DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T1274DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T1282DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T4819DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T4820DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T4821DAO;
import pe.gob.sunat.rrhh.dao.T12DAO;
import pe.gob.sunat.sol.BeanMensaje;
import pe.gob.sunat.sol.IncompleteConversationalState;
import pe.gob.sunat.sp.asistencia.bean.BeanHoraExtra;
import pe.gob.sunat.sp.asistencia.bean.BeanTurnoTrabajo;
import pe.gob.sunat.sp.asistencia.dao.T1270DAO;
//import pe.gob.sunat.sp.asistencia.dao.T1273DAO;
import pe.gob.sunat.sp.asistencia.dao.T1276DAO;
//import pe.gob.sunat.sp.asistencia.dao.T1282DAO;
import pe.gob.sunat.sp.asistencia.dao.T1275DAO;
import pe.gob.sunat.sp.asistencia.dao.T1279DAO;
import pe.gob.sunat.sp.asistencia.dao.T130DAO;
import pe.gob.sunat.sp.asistencia.dao.T132DAO;
import pe.gob.sunat.sp.asistencia.ejb.delegate.HoraExtraDelegate;
import pe.gob.sunat.sp.dao.CorreoDAO;
import pe.gob.sunat.sp.dao.T01DAO;
import pe.gob.sunat.sp.dao.T02DAO;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;
import pe.gob.sunat.utils.bd.OracleSequenceDAO;

/**
 * 
 * @ejb.bean name="HoraExtraFacadeEJB"
 *           description="HoraExtraFacade"
 *           jndi-name="ejb/rrhh/facade/sp/asistencia/HoraExtraFacadeEJB"
 *           type="Stateless" view-type="remote"
 * 
 * @ejb.home remote-class="pe.gob.sunat.sp.asistencia.ejb.HoraExtraFacadeHome"
 *           extends="javax.ejb.EJBHome"
 * 
 * @ejb.interface remote-class="pe.gob.sunat.sp.asistencia.ejb.HoraExtraFacadeRemote"
 *                extends="javax.ejb.EJBObject"
 *                
 * @ejb.resource-ref res-ref-name="jdbc/dcsp" res-type="javax.sql.DataSource" res-auth="Container"
 * @weblogic.resource-description res-ref-name="jdbc/dcsp" jndi-name="jdbc/dcsp"
 * 
 * @ejb.resource-ref res-ref-name="jdbc/dgsp" res-type="javax.sql.DataSource" res-auth="Container"
 * @weblogic.resource-description res-ref-name="jdbc/dgsp" jndi-name="jdbc/dgsp"
 *
 * @ejb.resource-ref res-ref-name="pool_oracle" res-type="javax.sql.DataSource" res-auth="Container"
 * @jboss.resource-ref res-ref-name="pool_oracle" jndi-name="java:/pool_scad"
 * @weblogic.resource-description res-ref-name="pool_oracle" jndi-name="jdbc/dcscad"
 * 
 * @ejb.transaction-type type="Container"
 * 
 * @jboss.container-configuration name="Standard Stateless SessionBean"
 * @jboss.version="3.2" 
 * @weblogic.enable-call-by-reference True
 * @weblogic.clustering home-is-clusterable="True" stateless-bean-is-clusterable="True"
 * @weblogic.transaction-descriptor trans-timeout-seconds = "300"
 * @weblogic.cache max-beans-in-cache="100"
 * @weblogic.pool max-beans-in-free-pool="100" initial-beans-in-free-pool="0"
 * 
 * @version 1.0
 */
public class HoraExtraFacade implements SessionBean {
	
	private final Log log = LogFactory.getLog(getClass());
	private SessionContext sessionContext;
	ServiceLocator sl = ServiceLocator.getInstance(); //ICAPUNAY 06/05/2012 AOM 06A4T11 LABOR EXCEPCIONAL Y COMPENSACIONES-PAS20124E550000064

	public void ejbCreate() {		
	}

	public void ejbRemove() {
	}

	public void ejbActivate() {
	}

	public void ejbPassivate() {
	}

	public void setSessionContext(SessionContext sessionContext) {
		this.sessionContext = sessionContext;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList cargarHorasExtras(String dbpool, String codJefe)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		ArrayList horasExtras = null;
		try {

			T130DAO dao = new T130DAO();
			horasExtras = dao.joinWithT02(dbpool, "", "", codJefe, null);
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return horasExtras;

	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public String modificarHoraExtra(BeanHoraExtra he, String fechaAut,
			String horaIni, String horaFin, String horaSalida, String obs,
			String usuario) throws IncompleteConversationalState,
			RemoteException {

		String res = Constantes.OK;
		BeanMensaje beanM = new BeanMensaje();
		try {

			T130CMPHome cmpHome = (T130CMPHome) ServiceLocator.getInstance().getLocalHome(T130CMPHome.JNDI_NAME);

			T130CMPLocal cmpLocal = cmpHome
					.findByPrimaryKey(new T130CMPPK(he.getCodPers(),
							he.getFechaAutorizacion(), he.getHoraIni()));

			cmpLocal.remove();

			T130CMPLocal cmpLocal2 = cmpHome.create(he.getCodPers(),
					fechaAut, horaIni, he.getCodUO(), horaFin, horaSalida.equals("")?horaFin:horaSalida, obs,
					he.getCodJefe(), Constantes.INACTIVO, new Timestamp(System
							.currentTimeMillis()), usuario);

			cmpLocal2.setFmod(new Timestamp(System.currentTimeMillis()));
			cmpLocal2.setCuserMod(usuario);

		} catch (Exception e) {
			log.error(e,e);
			res = e.getMessage();
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
	public ArrayList eliminarHorasExtras(String[] params, ArrayList lista)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {

			T130CMPHome cmpHome = (T130CMPHome) ServiceLocator.getInstance().getLocalHome(T130CMPHome.JNDI_NAME);
			T130CMPLocal cmpLocal = null;

			if (params != null) {
				for (int i = params.length - 1; i >= 0; i--) {

					BeanHoraExtra he = (BeanHoraExtra) lista.get(Integer
							.parseInt(params[i]));

					cmpLocal = cmpHome.findByPrimaryKey(new T130CMPPK(he
									.getCodPers(), he.getFechaAutorizacion(),
									he.getHoraIni()));

					cmpLocal.remove();
					lista.remove(he);
				}
			}
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
	public ArrayList buscarHorasExtras(String dbpool, String criterio,
			String valor, String codJefe, HashMap seguridad)
			throws IncompleteConversationalState, RemoteException {

		ArrayList horas = null;
		BeanMensaje beanM = new BeanMensaje();
		try {

			T130DAO dao = new T130DAO();
			horas = dao.joinWithT02(dbpool, criterio, valor, codJefe, seguridad);
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return horas;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList buscarAcumulados(String dbpool, String criterio,
			String valor, String codUO, HashMap seguridad)
			throws IncompleteConversationalState, RemoteException {

		ArrayList acum = null;
		BeanMensaje beanM = new BeanMensaje();
		try {

			T132DAO dao = new T132DAO();
			acum = dao.joinWithT02T99(dbpool, criterio, valor, codUO, seguridad);
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return acum;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public String registraCompensacionHE(String dbpool, String codPers,
			String codUO, String fecha, String usuario)
			throws IncompleteConversationalState, RemoteException {

		String res = Constantes.OK;
		BeanMensaje beanM = new BeanMensaje();
		try {

			DataSource dsOracle = ServiceLocator.getInstance().getDataSource("java:comp/env/pool_oracle");
			
			T1270DAO turnoDAO = new T1270DAO();
			T1282DAO vacacionDAO = new T1282DAO(dbpool);

			BeanTurnoTrabajo turno = turnoDAO.joinWithT45ByCodFecha(dbpool,
					codPers, fecha);

			if (turno != null) {

				QueueDAO queueDAO = new QueueDAO();
				T02DAO personalDAO = new T02DAO();
				CorreoDAO correoDAO = new CorreoDAO();
				T1273DAO licenciaDAO = new T1273DAO(dbpool);
				//prac-jcallo
				Map prms = new HashMap();
				prms.put("cod_pers", codPers);
				prms.put("fechaIni", fecha);
				prms.put("fechaFin", fecha);		
				log.debug("metodo registraCompensacionHE ... prms:"+prms);
				boolean bVacacion = vacacionDAO.findByCodPersFIniFFin(prms);//findByCodPersFIniFFin(dbpool,codPers, fecha, fecha);
				if (!bVacacion) {
					//prac-jcallo
					Map params = new HashMap();
					params.put("cod_pers", codPers);
					params.put("fecha1", fecha);
					params.put("fecha2", fecha);
					params.put("numero", "");
					log.debug("... params : "+params);
					//***/
					boolean tieneLicencia = licenciaDAO.findByCodPersFIniFFin(params);
							//dbpool, codPers, fecha, fecha, "");
					
					if (!tieneLicencia) {

						T132CMPHome acumHome = (T132CMPHome) ServiceLocator.getInstance().getLocalHome(T132CMPHome.JNDI_NAME);

						float dif = Utiles.obtenerHorasDiferencia(
								turno.getHoraIni(),
								turno.getHoraFin());
						
						T132CMPLocal acumRemo = acumHome
								.findByPrimaryKey(new T132CMPPK(codPers));
						
						float actual = acumRemo.getTAcum().floatValue();
						float nuevo = new java.math.BigDecimal(actual - dif).floatValue();

						if (nuevo>=0){
						
							acumRemo.setTAcum(new java.math.BigDecimal(nuevo));
							acumRemo.setFmod(new java.sql.Timestamp(System
									.currentTimeMillis()));
							acumRemo.setCuserMod(usuario);

							//T1273CMPHome cmpHome = (T1273CMPHome) ServiceLocator.getInstance().getLocalHome(T1273CMPHome.JNDI_NAME);
							//T1273DAO licenciadao = new T1273DAO(dbpool);
							
							//licenciaDAO.

							OracleSequenceDAO seqDAO = new OracleSequenceDAO();
							String numero = seqDAO.getSequenceDS(dsOracle,Constantes.SEQ_LICENCIA);

							T1276DAO dao = new T1276DAO();
							String periodoActual = (dao.findPeriodoActual(dbpool))
									.getPeriodo();

							java.sql.Timestamp fLicI = Utiles
									.stringToTimestamp(fecha + " 00:00:00");
							java.sql.Timestamp fLicF = Utiles
									.stringToTimestamp(fecha + " 00:00:00");

							/*cmpHome.create(
									periodoActual, Utiles.obtenerAnhoActual(),
									new java.lang.Short(numero), codUO,
									fLicI, fLicF, codPers, 1,
									Constantes.LICENCIA_COMPENSACION, "", "0", "",
									"COMPENSACION DE LABOR EXCEPCIONAL",
									new java.sql.Timestamp(System
											.currentTimeMillis()), usuario);*/
							
							//registrar compensacioon laboral excepcional
							//prac-jcallo
							FechaBean fec = new FechaBean(fecha);
							Map dts = new HashMap();
							dts.put("periodo", periodoActual);
							dts.put("anno", Utiles.obtenerAnhoActual());
							dts.put("numero", new java.lang.Short(numero));
							dts.put("u_organ", codUO);
							dts.put("ffinicio", fec.getTimestamp());
							dts.put("ffin",  fec.getTimestamp());
							dts.put("cod_pers",  codPers);
							dts.put("qdias",  Float.valueOf("1"));
							dts.put("licencia",  Constantes.LICENCIA_COMPENSACION);
							dts.put("anno_ref",  "");
							dts.put("numero_ref",  "0");
							dts.put("area_ref",  "");
							dts.put("observ",  "COMPENSACION DE LABOR EXCEPCIONAL");
							dts.put("fcreacion",  new FechaBean().getTimestamp());
							dts.put("cuser_crea",  usuario);
							//insercion de registro de licencia
							log.debug("... dts : "+dts);
							licenciaDAO.registrarLicenciaGeneral(dts);

							//obtenemos los datos del jefe
							HashMap trab = personalDAO.findByCodPersWithJefe(
									dbpool, codPers, null);

							String mensaje = "Se ha registrado un dia de compensacion para el "+ fecha + ".";
							String nombre = personalDAO.findNombreCompletoByCodPers(dbpool, codPers);
							String texto = Utiles.textoCorreoProceso(dbpool,nombre, mensaje,"");

							//enviamos el mail al trabajador
							HashMap datos = new HashMap();
							datos.put("subject", "Licencia de Compensacion");
							datos.put("message", texto);
							datos.put("from", correoDAO.findCorreoByCodPers(dbpool, (String) trab.get("t12cod_jefe")));
							datos.put("to", correoDAO.findCorreoByCodPers(dbpool,codPers));
							queueDAO.enviaCorreo(datos);							
						}
						else {
							throw new IncompleteConversationalState(
									"El trabajador no posee una cantidad sufieciente de horas de labor excepcional.");
						}

					} else {
						throw new IncompleteConversationalState(
								"El trabajador se encuentra de licencia el "
										+ fecha + ".");
					}
				} else {
					throw new IncompleteConversationalState(
							"El trabajador se encuentra de vacaciones el "
									+ fecha + ".");
				}
			} else {
				throw new IncompleteConversationalState(
						"El trabajador no posee un turno de trabajo asignado para el "
								+ fecha + ".");
			}
		} catch (Exception e) {
			log.error(e,e);
			res = e.getMessage();
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		return res;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public String registraAutorizacionHE(String dbpool, HashMap params,
			ArrayList trabajadores, String usuario)
			throws IncompleteConversationalState, RemoteException {

		String res = Constantes.OK;
		BeanMensaje beanM = new BeanMensaje();
		try {

			String listaTrab = "";
			HashMap t = null;
			String codPers = ""; 
			String codUO = "";
			if (trabajadores != null) {
				for (int i = 0; i < trabajadores.size(); i++) {

					t = (HashMap) trabajadores.get(i);
					codPers = (String) t.get("t02cod_pers");
					codUO = (String) t.get("t02cod_uorg");
					listaTrab += codPers.trim() + "_" + codUO.trim();
					if (i < trabajadores.size() - 1) {
						listaTrab += "&";
					}
				}
			}

			params.put("trabajadores", listaTrab);

			QueueDAO qd = new QueueDAO();
			qd.encolaProceso(ProcesoFacadeHome.JNDI_NAME,
					"registrarAutorizacionHE", params, usuario);

		} catch (Exception e) {
			log.error(e,e);
			res = e.getMessage();
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return res;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList cargarAutorizaciones(String dbpool, String codPers)
			throws IncompleteConversationalState, RemoteException {

		ArrayList autorizaciones = null;
		BeanMensaje beanM = new BeanMensaje();
		try {
			T130DAO dao = new T130DAO();
			autorizaciones = dao.findByCodPersEstado(dbpool, codPers,
					Constantes.ACTIVO);
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return autorizaciones;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public BeanHoraExtra verificaSalidaHE(String dbpool, String codPersonal)
			throws IncompleteConversationalState, RemoteException {

		BeanHoraExtra salida = null;
		BeanMensaje beanM = new BeanMensaje();
		try {

			T130DAO dao = new T130DAO();
			salida = dao.joinWithT02T02(dbpool, codPersonal);
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return salida;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void acumularHE(HashMap mapa, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {
			QueueDAO qd = new QueueDAO();
			Map seguridad = new HashMap();
/*********/		
			//ICAPUNAY 20/03/2013 - Memo 00023_2013_4F3100			
			//T130DAO daoMarca = new T130DAO();
			/*ArrayList lista = daoMarca.findAutorizadosByCriterioValor(
						(String)mapa.get("dbpool"), 
						(String)mapa.get("criterio"), 
						(String)mapa.get("valor"),
						(HashMap)seguridad);*/
			pe.gob.sunat.rrhh.asistencia.dao.T130DAO dao = new pe.gob.sunat.rrhh.asistencia.dao.T130DAO();
			ArrayList lista = dao.findAutorizadosByCriterioValor((String)mapa.get("dbpool"), (String)mapa.get("criterio"),
					(String)mapa.get("valor"),(HashMap)seguridad); 					
			log.debug("lista nueva : "+ lista);
			//ICAPUNAY 20/03/2013 - Memo 00023_2013_4F3100

			int total = lista != null ? lista.size() : 0;
			log.debug("total : "+ total);

			//int numPartes = 2; //ICR-MEMO 18/03/2013 para que al procesar 3 meses no demore tanto por institucional
			int numPartes = 32; //ICR-MEMO 18/03/2013 para que al procesar 3 meses no demore tanto por institucional
			int limiteInferior = 0;
			int limiteSuperior = 0;
			int grupo = (total / numPartes);
			if (total < numPartes) {
				numPartes = 1;
			}

			log.debug("grupo : "+ grupo);
			log.debug("numPartes : "+ numPartes);
			for (int i = 0; i < numPartes; i++) {
				log.debug("i : "+ i);
				log.debug("limiteSuperior1 : "+ limiteSuperior);
				limiteSuperior += grupo;
				log.debug("limiteSuperior2 : "+ limiteSuperior);
				if (i == numPartes - 1) {
					log.debug("entro (i): "+i);
					log.debug("limiteSuperior3 : "+ limiteSuperior);
					limiteSuperior = total;
					log.debug("limiteSuperior4 : "+ limiteSuperior);
				}

				log.debug("limiteInferior : "+ limiteInferior);
				log.debug("limiteSuperior : "+ limiteSuperior);
				log.info("Enviando grupo " + (i + 1) + " ("+ limiteInferior + " - " + limiteSuperior + ")");

				//seteamos los limites a procesar
				mapa.put("limiteInferior", "" + limiteInferior);
				mapa.put("limiteSuperior", "" + limiteSuperior);
				mapa.put("observacion", "Registro de horas extras. Grupo " + (i + 1));

				qd.encolaProceso(ProcesoFacadeHome.JNDI_NAME, "acumularHE",	mapa, usuario);

				log.debug("limiteSuperior f : "+ limiteSuperior);
				log.debug("limiteInferior f : "+ limiteInferior);
				limiteInferior = limiteSuperior + 1;
				log.debug("limiteInferior ff : "+ limiteInferior);
			}								
/***********/			

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
	public void registrarSalidaHE(String dbpool, String codPersonal,
			String fecha, String horaIni, String horaSalida, String fechaEfect)
			throws IncompleteConversationalState, RemoteException {

		boolean registrar = false;
		BeanMensaje beanM = new BeanMensaje();
		try {

			T01DAO paramDAO = new T01DAO();
			T1270DAO turnoDAO = new T1270DAO();
			
			String fActual = Utiles.obtenerFechaActual();
			BeanTurnoTrabajo turno = turnoDAO.joinWithT45ByCodFecha(dbpool, codPersonal, fActual);

			if (turno != null) {

				if (turno.isOperativo()){
					
					if (Utiles.obtenerHorasDiferencia(turno.getHoraFin(),horaSalida) >= 0) {
						registrar = true;
					} else {
						throw new IncompleteConversationalState(
								"Aun no se ha cumplido la hora fin de su turno de trabajo.");						
					}
					
					if (Utiles.obtenerHorasDiferencia(horaIni,horaSalida) >= 0) {
						registrar = true;
					} else {
						throw new IncompleteConversationalState(
							"Aun no se ha cumplido la hora inicio de su autorizacion.");							
					}
				}
				else{										
					
					boolean diaEspecial = Utiles.isDiaSemana(fecha, Constantes.SABADO)
							|| Utiles.isDiaSemana(fecha, Constantes.DOMINGO)
							|| paramDAO.findByFechaFeriado(dbpool, fecha);						
					
					if (!diaEspecial){							
						if (Utiles.obtenerHorasDiferencia(turno.getHoraFin(),horaSalida) >= 0) {
							registrar = true;
						} else {
							throw new IncompleteConversationalState(
								"Aun no se ha cumplido la hora fin de su turno de trabajo.");							
						}
						
						if (Utiles.obtenerHorasDiferencia(horaIni,horaSalida) >= 0) {
							registrar = true;
						} else {
							throw new IncompleteConversationalState(
								"Aun no se ha cumplido la hora inicio de su autorizacion.");							
						}
					}
					else{
						registrar = true;
					}												
				}
				
			} else {
				registrar = true;				
			}

			if (registrar){
			
				T130CMPHome cmpHome = (T130CMPHome) ServiceLocator.getInstance().getLocalHome(T130CMPHome.JNDI_NAME);

				T130CMPLocal cmpLocal = cmpHome
						.findByPrimaryKey(new T130CMPPK(codPersonal,
								fecha, horaIni));

				cmpLocal.setHSalid(horaSalida);
				cmpLocal.setFEfect(fechaEfect);
				
			}									

		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

	}
	
	//ICAPUNAY 31/01/2013 AOM 06A4T11 LABOR EXCEPCIONAL Y COMPENSACIONES (por derivacion mal solicitudes por proceso con hilos)
	/**
	 * Metodo encargado de procesar la acumulación de horas de labor excepcional por interfaz segun criterio
	 * @param mapa HashMap 
	 * @param usuario String 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void procesarLaborExcepcional_interfaz(HashMap mapa, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {
			log.debug("INICIO METODO HoraExtraFacade-procesarLaborExcepcional_interfaz - mapa: "+mapa);
			String criterio = (String) mapa.get("criterio");
						
			//ICR
			mapa.put("observacion", "Proceso General Acumulacion Horas Extras(" + (String)mapa.get("fechaIni") + "-"+			
					(String)mapa.get("fechaFin")+").Criterio>"+  criterio);
			if (log.isDebugEnabled()) log.debug("Va a ingresar a encolaProceso(procesarLaborExcepcional_interfaz)-QueueDAO");			
			QueueDAO qd = new QueueDAO();
			qd.encolaProceso(ProcesoFacadeHome.JNDI_NAME,
			"procesarLaborExcepcional_interfaz", (HashMap)mapa, usuario);
			
			if (log.isDebugEnabled()) log.debug("Salio de encolaProceso(procesarLaborExcepcional_interfaz)-QueueDAO");
			//ICR		   						
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
	}
	//FIN ICAPUNAY 31/01/2013 AOM 06A4T11 LABOR EXCEPCIONAL Y COMPENSACIONES (por derivacion mal solicitudes por proceso con hilos)
	
	//ICAPUNAY 31/01/2013 AOM 06A4T11 LABOR EXCEPCIONAL Y COMPENSACIONES (por derivacion mal solicitudes por proceso con hilos)
	/*/**
	 * Metodo encargado de procesar la acumulación de horas de labor excepcional por interfaz segun criterio
	 * @param mapa HashMap 
	 * @param usuario String 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	/*public void procesarLaborExcepcional_interfaz(HashMap mapa, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {
			log.debug("INICIO METODO HoraExtraFacade-procesarLaborExcepcional_interfaz - mapa: "+mapa);
			String dbpool = (String) mapa.get("dbpool");
			String regimen = (String) mapa.get("regimen");
			String criterio = (String) mapa.get("criterio");
			String valor = (String) mapa.get("valor");
			String fechaIni = (String) mapa.get("fechaIni");
			String fechaFin = (String) mapa.get("fechaFin");
			String codPers = (String) mapa.get("codPers");			
			HashMap seguridad = (HashMap) mapa.get("seguridad");
						
			HoraExtraFacadeHome spHoraExtraFacadeHome = (HoraExtraFacadeHome) sl.getRemoteHome(
					HoraExtraFacadeHome.JNDI_NAME,	HoraExtraFacadeHome.class);
			HoraExtraFacadeRemote spHoraExtraFacadeRemote;
			
		   	T12DAO t12dao = new T12DAO("jdbc/dcsp");
			Map mProceso = new HashMap();
			mProceso.put("dbpool", dbpool);	
			mProceso.put("regimen", regimen);			
			mProceso.put("fechaIni", fechaIni);
			mProceso.put("fechaFin", fechaFin);
			mProceso.put("codPers", codPers);
			mProceso.put("usuario", usuario);
			mProceso.put("seguridad", seguridad);
			
			if (criterio.equals("0") || criterio.equals("1") ){//0=registro,1=uuoo
				if(log.isDebugEnabled()) log.debug("entro a registro u uuoo");
				mProceso.put("criterio", criterio);
				mProceso.put("valor", valor.trim().toUpperCase());
				spHoraExtraFacadeRemote = spHoraExtraFacadeHome.create();
				if(log.isDebugEnabled()) log.debug("Llamando a procesarLaborExcepcional-HoraExtraFacade(registro u uuoo): "+mapa);				
				spHoraExtraFacadeRemote.procesarLaborExcepcional((HashMap)mProceso,usuario);
				
			}else{//4=inten y 3=institu
				if(log.isDebugEnabled()) log.debug("entro a intend e institu");
				Map prms=new HashMap();	
				Map mUniOrg = new HashMap();//mapa de unidad organizacional
				prms.put("orden", "1");				
				if (criterio.equals("4")){
					prms.put("criterio", "0");//por t12cod_uorga
					prms.put("valor", valor.substring(0,2));//4E
				}else{//3=institu
					prms.put("valor", "");
					prms.put("criterio", "");
				}
				if(log.isDebugEnabled()) log.debug("prms(intend e institu): "+prms);
				List listaUnidades=  t12dao.findByCodDesc(prms);
				if(log.isDebugEnabled()) log.debug("listaUnidades(intend e institu): "+listaUnidades);
				if (listaUnidades!=null && listaUnidades.size()>0){
					for (int i = 0; i < listaUnidades.size(); i++) {
						mUniOrg = (Map)listaUnidades.get(i);						
						valor = mUniOrg.get("t12cod_uorga").toString();//CODIGO DE UNIDAD ORGANIZACIONAL A PROCESAR	
						mProceso.put("criterio", "1");//por unidad organizacional
						mProceso.put("valor", valor.trim().toUpperCase());
						
						spHoraExtraFacadeRemote = spHoraExtraFacadeHome.create();
						if(log.isDebugEnabled()) log.debug("Llamando a procesarLaborExcepcional-HoraExtraFacade(intend e institu): "+mapa);						
					    if(log.isDebugEnabled()) log.debug("**** PROCESANDO LABOR EXCEPCIONAL PARA LA UNIDAD : "+valor+" ****");	
						spHoraExtraFacadeRemote.procesarLaborExcepcional((HashMap)mProceso,usuario);
					}
				}				
			}						
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
	}*/
	//FIN ICAPUNAY 31/01/2013 AOM 06A4T11 LABOR EXCEPCIONAL Y COMPENSACIONES (por derivacion mal solicitudes por proceso con hilos)
	
	//ICAPUNAY 09/04/2012 AOM 06A4T11 LABOR EXCEPCIONAL Y COMPENSACIONES-PAS20124E550000064
	/**
	 * Metodo encargado de procesar la acumulación de horas de labor excepcional por grupo de colaboradores
	 * @param mapa HashMap 
	 * @param usuario String 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void procesarLaborExcepcional(HashMap mapa, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {
			log.debug("INICIO METODO HoraExtraFacade-procesarLaborExcepcional - mapa: "+mapa);
			QueueDAO qd = new QueueDAO();			
		
			DataSource dcsp = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dcsp");
			pe.gob.sunat.rrhh.dao.T02DAO t02dao = new pe.gob.sunat.rrhh.dao.T02DAO(dcsp);			
			List lista = t02dao.buscarPersonal_labor(mapa);//todos los colaboradores que cumplan los criterios
		
			int total = lista != null ? lista.size() : 0;
			String criterio = mapa.get("criterio").toString().trim();//0=registro,1=uuoo,4=inten y 3=institu

			//int numPartes = 16;//ICAPUNAY 20/06
			int numPartes = 4;//ICAPUNAY 31/01/2013
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

				//seteamos los limites a procesar
				mapa.put("limiteInferior", "" + limiteInferior);
				mapa.put("limiteSuperior", "" + limiteSuperior);
				mapa.put("observacion", "Proceso acumulacion horas extras(" + (String)mapa.get("fechaIni") + "-"+			
				(String)mapa.get("fechaFin")+").Criterio>"+  criterio+ ".Grupo" + (i + 1));
				log.debug("ANTES ProcesoFacade-procesarLaborExcepcional - mapa: "+mapa);
				log.debug("ANTES ProcesoFacade-procesarLaborExcepcional - usuario: "+usuario);

				qd.encolaProceso(ProcesoFacadeHome.JNDI_NAME,"procesarLaborExcepcional", mapa, usuario);

				limiteInferior = limiteSuperior + 1;
			}								

		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
	}
	//FIN ICAPUNAY 09/04/2012 AOM 06A4T11 LABOR EXCEPCIONAL Y COMPENSACIONES-PAS20124E550000064
	
	
	//ICAPUNAY 06/05/2012 AOM 06A4T11 LABOR EXCEPCIONAL Y COMPENSACIONES-PAS20124E550000064
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public ArrayList procesarCompensacionTemporal(HashMap params) throws IncompleteConversationalState,RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		ArrayList mensajes = new ArrayList();
		if (log.isDebugEnabled()) log.debug("HoraExtraFacade-metodo:procesarCompensacionTemporal");
		try {
			String dbpool = (String)params.get("dbpool");
			DataSource dsOracle = sl.getDataSource("java:comp/env/pool_oracle");
			T1273DAO t1273dao = new T1273DAO(dbpool);
			T1276DAO periodoDAO = new T1276DAO();			
						
			String cod_pers = (String)params.get("cod_pers");
			String uuoo_pers = (String)params.get("uuoo_pers");
			String fechaIniComp = (String)params.get("fechaIniComp");
			String fechaFinComp = (String)params.get("fechaFinComp");
			String observacion = (String)params.get("observacion");
			String usuario = (String)params.get("usuario");
			//String cod_usuario = (String)params.get("codPers");
			//HashMap seguridad = (HashMap)params.get("seguridad");			

			mensajes = validarCompensacionTemporal(params);

			if (mensajes.isEmpty()) {//no hay errores de validacion
				
				int numDias = new FechaBean().calculaNumDiasHabiles(new FechaBean(fechaIniComp), new FechaBean(fechaFinComp))+1;
				if (log.isDebugEnabled()) log.debug("numDias: "+numDias);
			
				T4819DAO t4819DAO = new T4819DAO(sl.getDataSource("java:comp/env/jdbc/dgsp"));			
				Map datos1 = new HashMap();
				datos1.put("cod_pers", cod_pers);
				List fechas = t4819DAO.findHorasLaborAutorizadasConSaldo(datos1);
				if (log.isDebugEnabled()) log.debug("fechas1: "+fechas);
				int tempnum = 0;
				if (fechas != null && fechas.size() >0){
					if (log.isDebugEnabled()) log.debug("fechas1.size(): "+fechas.size());
					Map he = new HashMap();
					int minutos = 0;
					int i=0;
					if (log.isDebugEnabled()) log.debug("i1: "+i);
					for (int j=1;j<=numDias;j++){
						i=0;
						if (log.isDebugEnabled()) log.debug("i: "+i);
						if (log.isDebugEnabled()) log.debug("j: "+j);
						fechas = t4819DAO.findHorasLaborAutorizadasConSaldo(datos1);
						if (log.isDebugEnabled()) log.debug("fechas2: "+fechas);
						if (log.isDebugEnabled()) log.debug("fechas2.size(): "+fechas.size());
						minutos = 480;//x dia habil de compensacion
						while (minutos>0){
							if (log.isDebugEnabled()) log.debug("entro a while");//ICAPUNAY 09/02/2012-POR PRUEBAS DESARROLLO
							if (log.isDebugEnabled()) log.debug("i2: "+i);
							he = (HashMap) fechas.get(i);
							if(he!=null && !he.isEmpty()){
								if (log.isDebugEnabled()) log.debug("fec_perm2: "+he.get("fec_perm_desc"));
								if (log.isDebugEnabled()) log.debug("he: "+he);//ICAPUNAY 09/02/2012-POR PRUEBAS DESARROLLO
								if (log.isDebugEnabled()) log.debug("minutos1: "+minutos);//ICAPUNAY 09/02/2012-POR PRUEBAS DESARROLLO
								if (log.isDebugEnabled()) log.debug("cnt_min_comp_sal: "+he.get("cnt_min_comp_sal"));//ICAPUNAY 09/02/2012-POR PRUEBAS DESARROLLO
								if (minutos >= Integer.parseInt(he.get("cnt_min_comp_sal").toString())){								
									//Actualizar a cero ese dia y obtiene nueva fecha
									tempnum = 0;								
									if (log.isDebugEnabled()) log.debug("fec_perm: "+he.get("fec_perm_desc"));
									if (log.isDebugEnabled()) log.debug("hor_ini_comp: "+he.get("hor_ini_comp"));
									if (log.isDebugEnabled()) log.debug("tempnum: "+tempnum);								
									he.put("tempnum", new Integer(tempnum));
									he.put("usuario", usuario);
									he.put("indicador", "3");
									he.put("min_usado", he.get("cnt_min_comp_sal").toString());
									t4819DAO.updateLabor(he);	
									if (log.isDebugEnabled()) log.debug("minutos2: "+minutos);
									minutos = minutos - Integer.parseInt(he.get("cnt_min_comp_sal").toString());
									if (log.isDebugEnabled()) log.debug("minutosF: "+minutos);
									if (log.isDebugEnabled()) log.debug("i3: "+i);
									i = i + 1;
									if (log.isDebugEnabled()) log.debug("i4: "+i);
								} else {
									if (log.isDebugEnabled()) log.debug("i5: "+i);
									//Actualizar dias y a cero el dia	
									if (log.isDebugEnabled()) log.debug("fec_perm: "+he.get("fec_perm_desc"));
									if (log.isDebugEnabled()) log.debug("hor_ini_comp: "+he.get("hor_ini_comp"));								
									if (log.isDebugEnabled()) log.debug("cnt_min_comp_sal: "+Integer.parseInt(he.get("cnt_min_comp_sal").toString()));
									if (log.isDebugEnabled()) log.debug("minutos3: "+minutos);
									tempnum =  Integer.parseInt(he.get("cnt_min_comp_sal").toString()) - minutos;								
									if (log.isDebugEnabled()) log.debug("tempnum: "+tempnum);							
									he.put("tempnum", new Integer(tempnum));
									he.put("usuario", usuario);
									he.put("indicador", "1");
									he.put("min_usado", String.valueOf(minutos));
									t4819DAO.updateLabor(he);									
									minutos = 0;
									if (log.isDebugEnabled()) log.debug("minutos4: "+minutos);
								}							
							}							
							if (log.isDebugEnabled()) log.debug("fin while");
						}//fin while
					}//fin for					
						
				}//fin fechas										
					
				OracleSequenceDAO seqDAO = new OracleSequenceDAO();
				String annoLicencia = Utiles.obtenerAnhoActual();
				String numeroLic = seqDAO.getSequenceDS(dsOracle,Constantes.SEQ_LICENCIA);
				String periodoActual = (periodoDAO.findPeriodoActual(dbpool)).getPeriodo();
				Timestamp fLicIni = Utiles.stringToTimestamp(fechaIniComp + " 00:00:00");
				Timestamp fLicFin = Utiles.stringToTimestamp(fechaFinComp + " 00:00:00");
				
				Map dts = new HashMap();
				dts.put("periodo",periodoActual);
				dts.put("anno",annoLicencia);
				dts.put("numero",new Integer(numeroLic));
				dts.put("u_organ",uuoo_pers);
				dts.put("ffinicio",fLicIni);
				dts.put("ffin",fLicFin);
				dts.put("cod_pers",cod_pers);
				dts.put("qdias",new Integer(numDias));
				dts.put("licencia",Constantes.MOV_SOLICITUD_COMPENSACION);
				dts.put("anno_ref",null);			
				dts.put("numero_ref",null);
				dts.put("area_ref",null);
				dts.put("observ", observacion);
				dts.put("fcreacion",new FechaBean().getTimestamp());
				dts.put("cuser_crea",usuario);
				log.debug("t1273dao.registrarLicenciaGeneral(dts)... dts: "+dts);
				t1273dao.registrarLicenciaGeneral(dts);				

			}//fin no hay errores de validacion
			
		} catch (Exception e) {
			log.error(e);
			mensajes.add(e.toString());
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		if (log.isDebugEnabled()) log.debug("mensajes: "+mensajes);
		return mensajes;
	}
	
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList validarCompensacionTemporal(HashMap params) throws IncompleteConversationalState,RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		ArrayList mensajes = new ArrayList();
		if (log.isDebugEnabled()) log.debug("HoraExtraFacade-metodo:validarCompensacionTemporal");
		try {

			String dbpool = (String)params.get("dbpool");			
			T1273DAO licenciaDAO = new T1273DAO(dbpool);
			T1282DAO vacacionDAO = new T1282DAO(dbpool);
			T4819DAO t4819DAO = new T4819DAO(sl.getDataSource("java:comp/env/jdbc/dcsp"));
			T1270DAO turnoDAO = new T1270DAO();
			T01DAO paramDAO = new T01DAO();//ICAPUNAY 11/06 PASE 2012-64 LABOR EXCEPCIONAL
									
			String cod_pers = (String)params.get("cod_pers");		
			//String uuoo_pers = (String)params.get("uuoo_pers");
			String fechaIniComp = (String)params.get("fechaIniComp");
			String fechaFinComp = (String)params.get("fechaFinComp");
			//String observacion = (String)params.get("observacion");
			//String usuario = (String)params.get("usuario");
			//String cod_usuario = (String)params.get("codPers");
			//HashMap seguridad = (HashMap)params.get("seguridad");
						
			//ICAPUNAY 11/06 PASE 2012-64 LABOR EXCEPCIONAL
			boolean esDiaHabil = true;			
			if ((paramDAO.findByFechaFeriado(dbpool,fechaIniComp) || Utiles.isWeekEnd(fechaIniComp)) || (paramDAO.findByFechaFeriado(dbpool,fechaFinComp) || Utiles.isWeekEnd(fechaFinComp))) {
				esDiaHabil = false;
				mensajes.add("Fecha de inicio y fin de las compensaciones deben ser días hábiles.");
			}
			//ICAPUNAY 11/06 PASE 2012-64 LABOR EXCEPCIONAL
			
			if(esDiaHabil){//ICAPUNAY 11/06 PASE 2012-64 LABOR EXCEPCIONAL
				int NumdiasHabil = new FechaBean().calculaNumDiasHabiles(new FechaBean(fechaIniComp), new FechaBean(fechaFinComp))+1;
				if (log.isDebugEnabled()) log.debug("NumdiasHabil: "+NumdiasHabil);
				
				if (NumdiasHabil>0){//al menos hay un dia habil en el rango de fechas a compensar					
					Map datos1 = new HashMap();
					datos1.put("cod_pers", cod_pers);
					
					Map prm = new HashMap();
					prm.put("cod_pers", cod_pers.trim());			
					prm.put("fechaIni", fechaIniComp);//dd/MM/yyyy			
					prm.put("fechaFin", fechaFinComp);
					log.debug("vacacionDAO.findByCodPersFIniFFin(prm)... prm:"+prm);			
					boolean bVacacion = vacacionDAO.findByCodPersFIniFFin(prm);			
					if (bVacacion) {
						mensajes.add("El trabajador se encuentra de vacaciones durante esas fechas.");
					}
					
					//ICAPUNAY 11/06/2012 VALIDA VACACIONES PROGRAMADAS
	    			boolean bVacacion1 = vacacionDAO.findByCodPersFIniFFin49(prm);    			
	    			if (bVacacion1) {
	    				mensajes.add("El trabajador tiene vacaciones programadas durante esas fechas."); 			
	    			}
	    			//FIN ICAPUNAY
					
					Map prms = new HashMap();
					prms.put("cod_pers", cod_pers.trim());
					prms.put("fecha1", fechaIniComp);//dd/MM/yyyy
					prms.put("fecha2", fechaFinComp);
					prms.put("numero", "");
					log.debug("licenciaDAO.findByCodPersFIniFFin(prms)... prms:"+prms);
					boolean bLicencia = licenciaDAO.findByCodPersFIniFFin(prms); //(dbpool,codPers, fechaIni, fechaFin, "");					
					if (bLicencia) {
						mensajes.add("El trabajador posee una licencia aprobada durante esas fechas.");
					}				
					
					Integer saldo = t4819DAO.findSaldoLaborAutorizadas(datos1);					
					int saldoMinAct = saldo.intValue();
					if (log.isDebugEnabled()) log.debug("saldoMinAct: " + saldoMinAct);
					
					/*ICAPUNAY 20/06
					//Calculamos la cantidad de minutos a compensar para el numero de dias habiles
					int totMinutos = 480 * NumdiasHabil;
					int totHoras = totMinutos/60;								
					if (saldoMinAct < totMinutos ){					
						mensajes.add("Usted no posee la cantidad de horas suficientes para compensar "+NumdiasHabil+" días habiles. Requiere "+totHoras+" horas.");
					}*///FIN ICAPUNAY 20/06	
					
					int TotMinutosHabil = 0;
					int horasHabil = 0;
					int TotHorasHabil = 0;
					//if (bVacacion==false && bLicencia==false && saldoMinAct >= totMinutos ){//ICAPUNAY 20/06
					if (bVacacion==false && bLicencia==false){
						FechaBean fechaEval= new FechaBean();
						String codTurno="";
						String descTurno="";
						for (int i=1;i<=NumdiasHabil;i++){
							if (log.isDebugEnabled()) log.debug("fechaIniComp: "+ fechaIniComp);
							fechaEval = new FechaBean().calculaFecFinHabiles(new FechaBean(fechaIniComp), i-1);
							if (log.isDebugEnabled()) log.debug("fechaEval.getFormatDate(dd/MM/yyyy): "+ fechaEval.getFormatDate("dd/MM/yyyy"));						
							BeanTurnoTrabajo turno = turnoDAO.joinWithT45ByCodFecha(dbpool,cod_pers,fechaEval.getFormatDate("dd/MM/yyyy"));	
							if (turno!=null){
								codTurno = turno.getTurno().trim();
						    	descTurno = turno.getDescTurno();
						    	if ((turno.isOperativo()) || (!turno.isControla()) ){//turno es operativo o turno es administrativo no es controlado
						    		mensajes.add("No se puede compensar fecha hábil: "+fechaEval.getFormatDate("dd/MM/yyyy")+" porque tiene turno operativo o no controlado: "+ codTurno+"-"+descTurno);
						    	}else{
						    		horasHabil = Math.round(Utiles.obtenerHorasDifDia(turno.getHoraIni(),turno.getHoraFin()));//horas enteras del turno de la fecha habil
						    		TotMinutosHabil = TotMinutosHabil + (60 * horasHabil);//minutos del turno de la fecha habil
						    	}
							}else{
								mensajes.add("No se puede compensar fecha hábil: "+fechaEval.getFormatDate("dd/MM/yyyy")+" porque no tiene turno asignado");
							}						
						}//fin for NumdiasHabil
						
						TotHorasHabil = TotMinutosHabil/60;								
						if (saldoMinAct < TotHorasHabil ){					
							mensajes.add("Usted no posee la cantidad de horas suficientes para compensar "+NumdiasHabil+" días habiles. Requiere "+TotHorasHabil+" horas.");
						}
					}				
				}//fin NumdiasHabil>0
			}//FIN ICAPUNAY 11/06 PASE 2012-64 LABOR EXCEPCIONAL							
			
		} catch (Exception e) {
			log.error(e);
			mensajes.add(e.toString());
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		if (log.isDebugEnabled()) log.debug("mensajes: "+mensajes);
		return mensajes;
	}
	//FIN ICAPUNAY 06/05/2012 AOM 06A4T11 LABOR EXCEPCIONAL Y COMPENSACIONES-PAS20124E550000064

}