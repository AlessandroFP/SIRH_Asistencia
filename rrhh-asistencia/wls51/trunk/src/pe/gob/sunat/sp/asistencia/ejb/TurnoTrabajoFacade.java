package pe.gob.sunat.sp.asistencia.ejb;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pe.gob.sunat.batch.dao.QueueDAO;
import pe.gob.sunat.framework.core.bean.MensajeBean;//WERR-PAS20155E230300132
import pe.gob.sunat.framework.core.ejb.FacadeException;//WERR-PAS20155E230300132
import pe.gob.sunat.framework.core.pattern.DynaBean;
import pe.gob.sunat.framework.core.pattern.ServiceLocator;
import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.rrhh.asistencia.dao.T1608DAO;
import pe.gob.sunat.rrhh.dao.T12DAO;//ICAPUNAY
import pe.gob.sunat.rrhh.formativa.ejb.delegate.AsisForException;//WERR-PAS20155E230300132
import pe.gob.sunat.sol.BeanFechaHora;
import pe.gob.sunat.sol.BeanMensaje;
import pe.gob.sunat.sol.IncompleteConversationalState;
import pe.gob.sunat.sp.asistencia.bean.BeanTurnoTrabajo;
import pe.gob.sunat.sp.asistencia.dao.T1270DAO;
import pe.gob.sunat.sp.asistencia.dao.T1273DAO;
import pe.gob.sunat.sp.asistencia.dao.T1282DAO;
import pe.gob.sunat.sp.asistencia.ejb.delegate.AsistenciaException;//WERR-PAS20155E230300132
import pe.gob.sunat.sp.dao.T01DAO;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;

/**
 * 
 * @ejb.bean name="TurnoTrabajoFacadeEJB"
 *           description="TurnoTrabajoFacade"
 *           jndi-name="ejb/rrhh/facade/sp/asistencia/TurnoTrabajoFacadeEJB"
 *           type="Stateless" view-type="remote"
 * 
 * @ejb.home remote-class="pe.gob.sunat.sp.asistencia.ejb.TurnoTrabajoFacadeHome"
 *           extends="javax.ejb.EJBHome"
 * 
 * @ejb.interface remote-class="pe.gob.sunat.sp.asistencia.ejb.TurnoTrabajoFacadeRemote"
 *                extends="javax.ejb.EJBObject"
 * 
 * @ejb.transaction-type type="Container"
 * 
 * @ejb.resource-ref res-ref-name="jdbc/dgsp" res-type="javax.sql.DataSource" res-auth="Container"
 * @weblogic.resource-description res-ref-name="jdbc/dgsp" jndi-name="jdbc/dgsp"
 * 
 * @ejb.resource-ref res-ref-name="jdbc/dcsp" res-type="javax.sql.DataSource" res-auth="Container"
 * @weblogic.resource-description res-ref-name="jdbc/dcsp" jndi-name="jdbc/dcsp"
 * 
 * @jboss.container-configuration name="Standard Stateless SessionBean"
 * @jboss.version="3.2" 
 * @weblogic.enable-call-by-reference True
 * @weblogic.cache max-beans-in-cache="100"
 * @weblogic.pool max-beans-in-free-pool="100" initial-beans-in-free-pool="0"
 * @weblogic.clustering home-is-clusterable="True" stateless-bean-is-clusterable="True"
 * @weblogic.transaction-descriptor trans-timeout-seconds = "300"
 * @version 1.0
 */
public class TurnoTrabajoFacade implements SessionBean {

	private final Log log = LogFactory.getLog(getClass());
	private SessionContext sessionContext;

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
	public ArrayList buscarTurnosTrabajo(String dbpool, String criterio,
			String valor) throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		ArrayList turnos = null;
		try {
			T1270DAO dao = new T1270DAO();
			turnos = dao.joinWithT45T02T99T12(dbpool, criterio, valor);
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return turnos;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 * @param params
	 * @param lista
	 * @return
	 * @throws IncompleteConversationalState
	 * @throws RemoteException
	 */
	public ArrayList eliminarTurnosTrabajo(String[] params, ArrayList lista)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {
			/*JRR - 30/03/2008
			ProcesoFacadeHome procesoHome = (ProcesoFacadeHome) ServiceLocator.getInstance().getRemoteHome(ProcesoFacadeHome.JNDI_NAME,
					ProcesoFacadeHome.class);
			ProcesoFacadeRemote proceso = procesoHome.create();
			*/
			
//			T1270CMPHome cmpHome = (T1270CMPHome)ServiceLocator.getInstance().getLocalHome(T1270CMPHome.JNDI_NAME);
			pe.gob.sunat.rrhh.asistencia.dao.T1270DAO t1270dao = new pe.gob.sunat.rrhh.asistencia.dao.T1270DAO(ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dgsp"));
			Map mturno = new HashMap();
			Map mdatos = new HashMap();

			if (params != null) {
			    BeanTurnoTrabajo turno = null;
//			    T1270CMPLocal cmpLocal = null;
				for (int i = params.length - 1; i >= 0; i--) {

					turno = (BeanTurnoTrabajo) lista.get(Integer.parseInt(params[i]));
					
					mdatos.put("cod_pers", turno.getCodPers());
					mdatos.put("turno", turno.getTurno());
					mdatos.put("fini", turno.getFechaIni());
					mturno = t1270dao.findTurnoPersonaByPK(mdatos);
					
					if (mturno!= null) t1270dao.eliminarTurnoPersona(mdatos);
					
/*					cmpLocal = cmpHome.findByPrimaryKey(
							new T1270CMPPK(
									turno.getCodPers(), 
									turno.getTurno(), 
									turno.getFechaIni()));
					//cmpLocal.setEstId(Constantes.INACTIVO);
					cmpLocal.remove(); */
					
					/*JRR - 30/03/2008
					proceso.generarRegAsistenciaPorCambioTurnoTrab(mdatos);
					proceso.registrarNovedadPorCambioTurnoTrab(mdatos);
					*/
					
					lista.remove(turno);
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
	public String modificarTurnoTrabajo(Map mapa)
			throws IncompleteConversationalState, RemoteException {

	//Se cambia el tipo de transaccion de Required a NotSupported por error la encontrar turno en ProcesoFacade. 
	//Linea 2097: turno = daoTurno.joinWithT45ByCodFecha(dbpool,codPers, fechaEval);
	/*ASANCHEZZ 20100617
	public String modificarTurnoTrabajo(String dbpool, String codPers, String codUO, 
			String turno, Timestamp fechaIni, String fechaFin, String estado,
			String usuario, boolean vCruce)
			throws IncompleteConversationalState, RemoteException {
	*///FIN
		
		BeanMensaje beanM = new BeanMensaje();
		String res = Constantes.OK;

		//ASANCHEZZ 20100617
		String dbpool = (String)mapa.get("dbpool");
		String codPers = (String)mapa.get("codPers");
		//String codUO = (String)mapa.get("codUO");
		String turno = (String)mapa.get("turno");
		Timestamp fechaIni = Timestamp.valueOf(mapa.get("fechaIni").toString());
		String fechaFin = (String)mapa.get("fechaFin");
		String obsSustento=(String)mapa.get("obsSustento");
		String estado = (String)mapa.get("estado");
		String usuario = (String)mapa.get("usuario");
		boolean vCruce = false;
		if(mapa.get("vCruce").equals("1")) vCruce = true;
		String codiPersProceso = (String)mapa.get("codiPersProceso");
		//FIN
		
		try {
			
			ProcesoFacadeHome procesoHome = (ProcesoFacadeHome) ServiceLocator
					.getInstance().getRemoteHome(ProcesoFacadeHome.JNDI_NAME,
							ProcesoFacadeHome.class);

			ProcesoFacadeRemote proceso = procesoHome.create();

			T1270DAO dao = new T1270DAO();
			//primero obtenemos los turnos asignados al personal
			ArrayList turnos = dao.joinWithT45(dbpool, codPers);

//			T1270CMPHome cmpHome = (T1270CMPHome) ServiceLocator.getInstance().getLocalHome(T1270CMPHome.JNDI_NAME);
			pe.gob.sunat.rrhh.asistencia.dao.T1270DAO t1270dao = new pe.gob.sunat.rrhh.asistencia.dao.T1270DAO(ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dgsp"));
			FechaBean fecAct = new FechaBean();
			Map mturno = new HashMap();
			Map mdatos = new HashMap();
			mdatos.put("cod_pers", codPers);
			mdatos.put("turno", turno);
			mdatos.put("fini", fechaIni);
			//ASANCHEZZ 20100617
			mdatos.put("codiPersProceso", codiPersProceso);
			//FIN
			mdatos.put("obsSustento",obsSustento);
			mdatos.put("modificar","sustento");
			//verificamos cruces
			java.util.Date f2 = Utiles.stringToTimestamp(fechaFin);
			java.sql.Timestamp fFin = new Timestamp(f2.getTime());

			if (vCruce) {
				boolean hayCruce = proceso.verificaCruceTurnosTrabajador(
						turnos, turno, fechaIni, fFin, true);
				//si no hay cruces entonces registramos
				if (!hayCruce) {

					mturno = t1270dao.findTurnoPersonaByPK(mdatos);

					if (mturno!= null) {
						mdatos.put("u_organ", mturno.get("u_organ"));
						mdatos.put("sonom_id", mturno.get("sonom_id"));
						mdatos.put("est_id", estado);
						mdatos.put("ffin", fFin);
						mdatos.put("cuser_mod", usuario);
						mdatos.put("fmod", fecAct.getTimestamp());
						t1270dao.actualizarTurnoPersona(mdatos);

						//JRR
						proceso.generarRegAsistenciaPorCambioTurnoTrab(mdatos);
						proceso.registrarNovedadPorCambioTurnoTrab(mdatos);
					}
					
/*					T1270CMPLocal cmpLocal = cmpHome
							.findByPrimaryKey(new T1270CMPPK(
									codPers,
									turno, 
									fechaIni));

					cmpLocal.setFfin(fFin);
					cmpLocal.setEstId(estado);
					cmpLocal.setFmod(new Timestamp(System.currentTimeMillis()));
					cmpLocal.setCuserMod(usuario);   */

				}
				//en caso contrario
				else {
					res = "El turno asignado presenta cruce.";
				}
			} else {

				mturno = t1270dao.findTurnoPersonaByPK(mdatos);

				if (mturno!= null) {
					mdatos.put("u_organ", mturno.get("u_organ"));
					mdatos.put("sonom_id", mturno.get("sonom_id"));
					mdatos.put("est_id", estado);
					mdatos.put("ffin", fFin);
					mdatos.put("cuser_mod", usuario);
					mdatos.put("fmod", fecAct.getTimestamp());
					t1270dao.actualizarTurnoPersona(mdatos);
					
					//JRR
					proceso.generarRegAsistenciaPorCambioTurnoTrab(mdatos);
					proceso.registrarNovedadPorCambioTurnoTrab(mdatos);
				}
				
/*				T1270CMPLocal cmpLocal = cmpHome
						.findByPrimaryKey(new T1270CMPPK(
								codPers,
								turno, 
								fechaIni));

				cmpLocal.setFfin(fFin);
				cmpLocal.setEstId(estado);
				cmpLocal.setFmod(new Timestamp(System.currentTimeMillis()));
				cmpLocal.setCuserMod(usuario);   */

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
	 * @ejb.transaction type="NotSupported"
	 */
	public BeanTurnoTrabajo buscarTrabTurno(String dbpool, String codPers,
			String fecha) throws IncompleteConversationalState, RemoteException {

		BeanTurnoTrabajo turno = null;
		BeanMensaje beanM = new BeanMensaje();
		try {

			T1270DAO dao = new T1270DAO();
			turno = dao.joinWithT45ByCodFecha(dbpool, codPers, fecha);

		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return turno;

	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void procesarOperativo(String dbpool, ArrayList trabajadores,
			ArrayList detalle, String fechaIni, String fechaFin,
			boolean bSabado, boolean bDomingo, boolean bFeriado,
			String codPersUsuario, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {

			String listaTrab = "";

			if (trabajadores != null) {
			    HashMap t = null;
				for (int i = 0; i < trabajadores.size(); i++) {

					t = (HashMap) trabajadores.get(i);
					listaTrab += ((String) t.get("t02cod_pers")).trim() + "_" + ((String) t.get("t02cod_uorg")).trim();
					if (i < trabajadores.size() - 1) {
						listaTrab += "&";
					}
				}
			}

			HashMap params = new HashMap();
			params.put("dbpool", dbpool);
			params.put("trabajadores", listaTrab);
			params.put("detalle", detalle);
			params.put("fechaIni", fechaIni);
			params.put("fechaFin", fechaFin);
			params.put("bSabado", "" + bSabado);
			params.put("bDomingo", "" + bDomingo);
			params.put("bFeriado", "" + bFeriado);
			params.put("codPers", codPersUsuario);
			params.put("observacion", "Proceso de planificacion operativa del "
					+ fechaIni + " al " + fechaFin);

			QueueDAO qd = new QueueDAO();
			qd.encolaProceso(ProcesoFacadeHome.JNDI_NAME,
					"procesarOperativoMasivo", params, usuario);

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
	public ArrayList buscarTurnosTrabajo(String dbpool, String fechaIni,
			String fechaFin, String codTurno, String criterio, String valor,
			HashMap seguridad) throws IncompleteConversationalState,
			RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		ArrayList turnos = null;
		try {
			T1270DAO dao = new T1270DAO();
			turnos = dao.joinWithT45T02T99T12(dbpool, fechaIni, fechaFin,
					codTurno, criterio, valor, seguridad);
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return turnos;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void registrarTurnoTrabajo(HashMap mapa, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {
			QueueDAO qd = new QueueDAO();
			qd.encolaProceso(ProcesoFacadeHome.JNDI_NAME,
					"registrarTurnoTrabajo", mapa, usuario);
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
	public void registrarTurnoCompensa(HashMap datos)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		String codPers = null;//WERR-PAS20155E230300132
		try {
			
/*			T1608CMPHome cmpHome = (T1608CMPHome) ServiceLocator.getInstance().getLocalHome(T1608CMPHome.JNDI_NAME);			
			T1608CMPLocal cmpLocal = null; 
*/				
			String dbpool = (String)datos.get("dbpool");
			//String codPers = (String)datos.get("codPers");
			codPers = (String)datos.get("codPers");//WERR-PAS20155E230300132
			String diasComp = (String)datos.get("diasComp");
			String fechaLic = (String)datos.get("fechaLic");
			//String fechaIni = Utiles.dameFechaSiguiente((String)datos.get("fechaGen"),1);
			String fechaIniLic = (String)datos.get("fechaIniLic");//ICR 07/01/2013 Recuperacion desde 07/01/2013 a solicitudes de feriados del 24/12/2012 o 31/12/2012 en seguimiento
			String fechaFinLic = (String)datos.get("fechaGen");//ICR 07/01/2013 Recuperacion desde 07/01/2013 a solicitudes de feriados del 24/12/2012 o 31/12/2012 en seguimiento
			
			//JVV-24/04/2012-MEMO 00085-2012-4F3100
			//String fechaIni = Utiles.dameFechaSiguiente((String)datos.get("fechaGen"),1);			
			String fechaIni = "";
			String diaSemana = Utiles.dameDiaSemana((String)datos.get("fechaGen"));
			if (log.isDebugEnabled()) log.debug("diaSemana:"+diaSemana);
			if (diaSemana.equals("Domingo")){
				fechaIni = Utiles.dameFechaSiguiente((String)datos.get("fechaGen"),0);
			} else if (diaSemana.equals("Lunes")){
				//ICR 07/01/2013 Recuperacion desde 07/01/2013 a solicitudes de feriados del 24/12/2012 o 31/12/2012 en seguimiento
				//fechaIni = Utiles.dameFechaSiguiente((String)datos.get("fechaGen"),7);
				if (fechaFinLic.equals("24/12/2012") || fechaFinLic.equals("31/12/2012")){//dias de licencia igual al 24/12/2012 o 31/12/2012					
					log.debug("fecha fin ES 24/12/2012 o 31/12/2012");
					if (fechaIniLic.equals(fechaFinLic)){
						log.debug("fecha fin ES igual a fecha inicio (24/12/2012 o 31/12/2012)");						
						fechaIni = "07/01/2013";
					}else{
						log.debug("entro3");
						throw new Exception("No se puede aprobar ya que feriado compensable es s�lo para 1 d�a ya sea 24/12/2012 o 31/12/2012.");
					}
				}else{
					log.debug("fecha fin NO ES ni 24/12/2012 ni 31/12/2012");
					fechaIni = Utiles.dameFechaSiguiente((String)datos.get("fechaGen"),7);//otros dias de licencia diferente al 24/12/2012 o 31/12/2012
				}
				//ICR 07/01/2013 Recuperacion desde 07/01/2013 a solicitudes de feriados del 24/12/2012 o 31/12/2012 en seguimiento
			} else if (diaSemana.equals("Martes")){
				fechaIni = Utiles.dameFechaSiguiente((String)datos.get("fechaGen"),6);
			} else if (diaSemana.equals("Miercoles")){
				fechaIni = Utiles.dameFechaSiguiente((String)datos.get("fechaGen"),5);
			} else if (diaSemana.equals("Jueves")){
				fechaIni = Utiles.dameFechaSiguiente((String)datos.get("fechaGen"),4);
			} else if (diaSemana.equals("Viernes")){
				fechaIni = Utiles.dameFechaSiguiente((String)datos.get("fechaGen"),3);
			} else if (diaSemana.equals("Sabado")){
				fechaIni = Utiles.dameFechaSiguiente((String)datos.get("fechaGen"),0);
			}
			//FIN-JVV-24/04/2012-MEMO 00085-2012-4F3100
						
			String fechaCom = (String)datos.get("fechaCompensa");
			
			//JRR - 20/05/2009
			String anno = datos.get("anno")!=null?datos.get("anno").toString():null;
			String num_refer = datos.get("num_refer")!=null?datos.get("num_refer").toString():null;
			
			if (Utiles.obtenerDiasDiferencia((String)datos.get("fechaGen"),(String)datos.get("fechaLic"))>0){				
				fechaIni = Utiles.dameFechaSiguiente((String)datos.get("fechaLic"),1);
			}			
			String usuario = (String)datos.get("usuario");
			
			T1282DAO daoVac = new T1282DAO();
			T1273DAO daoLic = new T1273DAO();
			T01DAO paramDAO = new T01DAO();
			T1270DAO tpDAO = new T1270DAO();
			T1608DAO daoCom = new T1608DAO(dbpool);
			
			BeanTurnoTrabajo turno = tpDAO.joinWithT45ByCodFecha(dbpool,codPers,fechaLic);
						
			int numDias = 8;
			int numHoras = 1;			
			if (turno!=null){				
				//si la cantidad de dias esta relacionada a la fecha de la licencia
				if (diasComp==null || diasComp.equals("")){
					if (!turno.isOperativo()){					
						if (Utiles.isWeekEnd(fechaLic)) {
							throw new Exception("No se le puede asignar permiso porque el "+fechaLic+" es fin de semana.");
						}
						if (paramDAO.findByFechaFeriado(dbpool,fechaLic)) {
							throw new Exception("No se le puede asignar permiso porque el "+fechaLic+" es un día feriado.");
						}					
					}	
				}
				//calculamos el numero de horas que se va a compensar
				if (diasComp!=null) numDias = Integer.parseInt(diasComp);
				else numDias = (int)Utiles.obtenerHorasDifDia(turno.getHoraIni(),turno.getHoraFin());	
				numHoras = Integer.parseInt((String)datos.get("horComp"));
			}
			
			boolean asignarDia = false;
			boolean bFin = false;
			
			String diaSgte = fechaIni;
			if (fechaCom != null) {
				diaSgte = fechaCom;
			}
			String fechaFinTurno = "";
			
			//obtenemos el turno de la fecha de evaluacion
			if (log.isDebugEnabled()) log.debug("llegue al turno1");
			turno = tpDAO.joinWithT45ByCodFecha(dbpool,codPers,diaSgte);
			if (log.isDebugEnabled()) log.debug("pase al turno1");
			if (turno==null){
				//throw new Exception("Usuario No Tiene Turno disponible para Compensar.");
				int ctdDias = 4;
				while (turno==null && ctdDias > 0){
					diaSgte = Utiles.dameFechaSiguiente(diaSgte,1);
					turno = tpDAO.joinWithT45ByCodFecha(dbpool,codPers, diaSgte);
					ctdDias = ctdDias - 1;
					if (log.isDebugEnabled()) log.debug("ctddias "+ ctdDias);
				}
				if (turno==null){
			    	//throw new Exception("Usuario No Tiene Turno disponible para Compensar.");
			    	throw new Exception("El trabajador " + codPers + " no posee turno entre las fechas que va a compensar.");
				}						
			}			
			if (log.isDebugEnabled()) log.debug("llegue al turno2");

			//JRR - 20/05/2009
			Map mapa = new HashMap();
			mapa.put("cod_personal", codPers);
			
			while (numDias > 0 && turno != null) {

				fechaFinTurno = Utiles.timeToFecha(turno.getFechaFin());

				bFin = Utiles.obtenerDiasDiferencia(diaSgte, fechaFinTurno) < 0;
				
				while (numDias > 0 && !bFin) {
					
					mapa.put("fcompensa", diaSgte);
					
					if (log.isDebugEnabled()) log.debug("llegue al turno3");
					asignarDia = true;
					if (daoVac.findByCodPersFIniFFin(dbpool, codPers, diaSgte, "")) {
						asignarDia = false;
					} 
					else if (daoLic.findByCodPersFIniFFin(dbpool, codPers, diaSgte, "", "")) {
						asignarDia = false;
					} 
					//else if (daoCom.findByCodPersFIniFFin(dbpool, codPers, diaSgte, "", "")) {
					//JRR - 20/05/2009
					else if (daoCom.findCompensacionPersonaByPK(mapa)!=null && daoCom.findCompensacionPersonaByPK(mapa).get("cod_personal")!=null) {	
						asignarDia = false;
					} 
					else if (!turno.isOperativo() && Utiles.isWeekEnd(diaSgte)) {
						asignarDia = false;
					} 
					else if (!turno.isOperativo() && paramDAO.findByFechaFeriado(dbpool,diaSgte)) {
						asignarDia = false;
					}
					if (log.isDebugEnabled()) log.debug("llegue al turno4");
					if (asignarDia){ 
						numDias--;

						//JRR - Segun se ve en el codigo nunca se va a dar el caso que haya que actualizar (por asignarDia)
						/*if (daoCom.findCompensacionPersonaByPK(mapa)!=null && daoCom.findCompensacionPersonaByPK(mapa).get("cod_personal")!=null) {
							actualizarCompensacionPersona();
						} else { */
						mapa.put("qhoras",new Integer(numHoras));						
						mapa.put("fcreacion",new Timestamp(System.currentTimeMillis()));
						mapa.put("cuser_crea",usuario);
						mapa.put("ind_estado_id", Constantes.ACTIVO);
						mapa.put("anno", anno);
						mapa.put("num_refer", num_refer);
						daoCom.registrarCompensacionPersona(mapa);
						//}
						
/*						try{
							cmpLocal = cmpHome.findByPrimaryKey(
									new T1608CMPPK(
									codPers,
									new BeanFechaHora(diaSgte).getSQLDate()));

							cmpLocal.setQHoras(new Integer(cmpLocal.getQHoras().intValue()+numHoras));
							cmpLocal.setFGraba(new Timestamp(System.currentTimeMillis()));
							cmpLocal.setCUser(usuario);
						}
						catch(Exception e){
							
							HashMap mapa = new HashMap();
							mapa.put("cod_pers",codPers);
							mapa.put("fcompensa",new BeanFechaHora(diaSgte).getSQLDate());
							mapa.put("qhoras",new Integer(numHoras));						
							mapa.put("fgraba",new Timestamp(System.currentTimeMillis()));
							mapa.put("cuser",usuario);
							
							cmpLocal = cmpHome.create(mapa);							
						}
*/						
					}
					diaSgte = Utiles.dameFechaSiguiente(diaSgte, 1);
					bFin = Utiles.obtenerDiasDiferencia(diaSgte,Utiles.timeToFecha(turno.getFechaFin())) < 0;
					
				}
				
				if (numDias > 0) {
					turno = tpDAO.joinWithT45ByCodFecha(dbpool,codPers, diaSgte);						
					if (turno==null){
						//throw new Exception("Usuario No Tiene Turno disponible para Compensar.");
						int ctdDias1 = 4;
						while (turno==null && ctdDias1 > 0){
							diaSgte = Utiles.dameFechaSiguiente(diaSgte,1);
							turno = tpDAO.joinWithT45ByCodFecha(dbpool,codPers, diaSgte);
							ctdDias1 = ctdDias1 - 1;
							if (log.isDebugEnabled()) log.debug("ctddias1 "+ ctdDias1);
						}
						if (turno==null){ 
							//throw new Exception("Usuario "+ codPers +" No Tiene Turno disponible para Compensar.");
							beanM.setMensajeerror("El Trabajador "+ codPers +" no posee turno entre las fechas que va a compensar.");//WERR-PAS20155E230300132
							beanM.setMensajesol("Por favor intente nuevamente.");//WERR-PAS20155E230300132
							throw new IncompleteConversationalState(beanM);//WERR-PAS20155E230300132			
						}

					}
				} 						
			}
			
		} /*catch (Exception e) {
			//log.error(e,e);
			//beanM.setMensajeerror(e.getMessage());
			//log.error(e);
			beanM.setMensajeerror("El trabajador "+ codPers +" no posee turno entre las fechas que va a compensar.");//WERR-PAS20155E230300132
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		
			}*/
		catch (Exception e) { 
			log.error(e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		
	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public int calcularHorasCompensa(String dbpool, String codPers, String fechaIni, String fechaFin)
			throws IncompleteConversationalState, RemoteException {

		int numHoras = 0;
		BeanMensaje beanM = new BeanMensaje();
		try {

			T01DAO paramDAO = new T01DAO();
			T1270DAO dao = new T1270DAO();
			
			String fIni = Utiles.toYYYYMMDD(fechaIni);
			String fFin = Utiles.toYYYYMMDD(fechaFin);
			String fAct = fIni;
			String fReal = fechaIni;
			
			boolean contar = true;
			BeanTurnoTrabajo turno = null;
			
			while (fAct.compareTo(fFin) <= 0) {
					
				contar = true;
				turno = dao.joinWithT45ByCodFecha(dbpool,codPers,fReal);
				
				//log.debug("Fecha : "+fReal);
				//log.debug("Turno : "+turno.toString());
				
				//si es operativo
				if (turno!=null){
					if (!turno.isOperativo()){
						//si es fin de semana o feriado
						if (Utiles.isWeekEnd(fReal) || paramDAO.findByFechaFeriado(dbpool,fReal)) {
							contar = false;
						}	
					} 
					if (contar) numHoras += Utiles.obtenerHorasDifDia(turno.getHoraIni(), turno.getHoraFin())-1;
				}
				
				fReal = Utiles.dameFechaSiguiente(fReal, 1);
				fAct = Utiles.toYYYYMMDD(fReal);
			}
			
			//log.debug("Horas x Compensar : "+numHoras);
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		return numHoras;

	}	
	
	//ICR 25/09/2012 PAS20124E550000721 AJUSTES A 8 HORAS DE RECUPERACION PARA DL 1057(CAS) Y DL 276-728 POR CADA DIA DE FERIADO COMPENSABLE
	/** AJUSTES A 8 HORAS DE RECUPERACION PARA DL 1057(CAS) Y DL 276-728 POR CADA DIA DE FERIADO COMPENSABLE
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public int calcularHorasCompensaModif(String dbpool, String codPers, String fechaIni, String fechaFin)
			throws IncompleteConversationalState, RemoteException {

		int numHoras = 0;
		BeanMensaje beanM = new BeanMensaje();
		try {

			T01DAO paramDAO = new T01DAO();
			T1270DAO dao = new T1270DAO();			
			String fIni = Utiles.toYYYYMMDD(fechaIni);
			String fFin = Utiles.toYYYYMMDD(fechaFin);
			String fAct = fIni;
			String fReal = fechaIni;
			
			boolean contar = true;
			BeanTurnoTrabajo turno = null;
			
			while (fAct.compareTo(fFin) <= 0) {					
				contar = true;
				turno = dao.joinWithT45ByCodFecha(dbpool,codPers,fReal);						
				//si es operativo
				if (turno!=null){
					if (!turno.isOperativo()){
						//si es fin de semana o feriado
						if (Utiles.isWeekEnd(fReal) || paramDAO.findByFechaFeriado(dbpool,fReal)) {
							contar = false;
						}	
					} 
					if (contar) numHoras += 8; //8 horas de recuperacion por cada dia para DL 1057(CAS) Y DL 276-728;					
				}				
				fReal = Utiles.dameFechaSiguiente(fReal, 1);
				fAct = Utiles.toYYYYMMDD(fReal);
			}			
			log.debug("Horas x Compensar : "+numHoras);
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		return numHoras;

	}
	//FIN ICR 25/09/2012 PAS20124E550000721 AJUSTES A 8 HORAS DE RECUPERACION PARA DL 1057(CAS) Y DL 276-728 POR CADA DIA DE FERIADO COMPENSABLE
	
	//ICAPUNAY 15/12/2010 AOM URGENTE: 44U3T10 Gestión de turnos y calificacion de procesos
	/* ICAPUNAY 27/04/2011- REQUERIDO X RUBEN LAZARTE 
	/**
	 * Metodo encargado de listar las Unidades Organizaciones (UUOO) Activas
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param criterio String
	 * @param valor	String
	 * @param orden	String
	 * @return	ArrayList
	 * @throws IncompleteConversationalState
	 * @throws RemoteException
	 */
	
	/* ICAPUNAY 27/04/2011- REQUERIDO X RUBEN LAZARTE 
	public List buscarUUOOActivos(String criterio,
			String valor, String orden) throws IncompleteConversationalState,
			RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		List listaUnidades = null;
		try {
			
			T12DAO t12dao = new T12DAO(ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dcsp"));
			
			Map prms = new HashMap();
			prms.put("criterio", criterio);
			prms.put("valor", valor);
			prms.put("orden", orden);
			
			//OBTENIENDO LISTADO DE UNIDADES ORGANIZACIONALES ACTIVAS
			listaUnidades = t12dao.findByCodDesc(prms);

		} catch (Exception e) {
			log.error(e.getMessage());
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return listaUnidades;
	}
	*/
	
	
	/**
	 * Metodo encargado de la Planificacion de Turnos Masivos
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"		
	 * @param dbean	DynaBean
	 * @throws IncompleteConversationalState
	 * @throws RemoteException
	 */
	public void planificarTurnosMasivos(DynaBean dbean)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		String usuario =  dbean.getString("usuario");
		String mes =  dbean.getString("cmbMes");
		String anio =  dbean.getString("cmbAnio");
		dbean.put("observacion", "Validacion de turnos del "+ mes + "/" + anio);	
		
		try {

			if (log.isDebugEnabled()) log.debug("Ingreso a planificarTurnosMasivos-TurnoTrabajoFacade");
			if (log.isDebugEnabled()) log.debug("Va a ingresar a encolaProceso-QueueDAO");
			
			
			
			QueueDAO qd = new QueueDAO();
			qd.encolaProceso(ProcesoFacadeHome.JNDI_NAME,
			"planificarTurnosMasivos", (HashMap)dbean, usuario);
			
			if (log.isDebugEnabled()) log.debug("Salió de encolaProceso-QueueDAO");

		} catch (Exception e) {
			log.error(e.getMessage());
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

	}
	//FIN ICAPUNAY 15/12/2010 AOM URGENTE: 44U3T10 Gestión de turnos y calificacion de procesos
	
}