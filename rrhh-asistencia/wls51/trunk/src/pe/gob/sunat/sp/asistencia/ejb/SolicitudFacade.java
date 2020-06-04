package pe.gob.sunat.sp.asistencia.ejb;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.print.attribute.standard.Finishings;
import javax.servlet.RequestDispatcher;//WERR-PAS20155E230300132
import javax.sql.DataSource;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pe.gob.sunat.batch.dao.QueueDAO;
import pe.gob.sunat.framework.core.bean.MensajeBean;
import pe.gob.sunat.framework.core.bean.ParamBean;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.core.ejb.FacadeException;
import pe.gob.sunat.framework.core.ejb.StatelessAbstract;
import pe.gob.sunat.framework.core.pattern.ServiceLocator;
import pe.gob.sunat.framework.core.pattern.ServiceLocatorException;
import pe.gob.sunat.framework.util.Propiedades;
import pe.gob.sunat.framework.util.cache.dao.ParamDAO;
import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.framework.util.mail.Correo;
import pe.gob.sunat.framework.util.mail.CorreoException;
import pe.gob.sunat.framework.util.mail.Direccion;
import pe.gob.sunat.rrhh.asistencia.dao.T1271DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T1273DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T1274DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T1282DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T130DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T132DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T1455DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T1608DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T1933DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T4818DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T4819DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T4820DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T4821DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T3701DAO;
import pe.gob.sunat.rrhh.planilla.dao.ConsolAsistDiaDAO;//PAS20171U230200001 - solicitud de reintegro  
import pe.gob.sunat.rrhh.planilla.dao.ConsolAsistMensDAO;//PAS20171U230200001 - solicitud de reintegro  
import pe.gob.sunat.rrhh.planilla.dao.HistMetasEmpleaDAO; //PAS20171U230200001 - solicitud de reintegro  
import pe.gob.sunat.rrhh.planilla.dao.LicenciasDAO;//PAS20171U230200001 - solicitud de reintegro  
import pe.gob.sunat.rrhh.planilla.dao.MaestroPersonalDAO;//PAS20171U230200001 - solicitud de reintegro  
import pe.gob.sunat.rrhh.planilla.dao.MetasEmpleadoDAO;//PAS20171U230200001 - solicitud de reintegro  
import pe.gob.sunat.rrhh.planilla.dao.PlaniHistoricasDAO;//PAS20171U230200001 - solicitud de reintegro  
import pe.gob.sunat.rrhh.planilla.dao.T01ParametroDAO;//PAS20171U230200001 - solicitud de reintegro  
import pe.gob.sunat.rrhh.planilla.dao.T5737DAO;//PAS20171U230200001 - solicitud de reintegro  
import pe.gob.sunat.rrhh.planilla.dao.T5738DAO;//PAS20171U230200001 - solicitud de reintegro  
import pe.gob.sunat.rrhh.planilla.dao.TPeriodosDAO;//PAS20171U230200001 - solicitud de reintegro  
import pe.gob.sunat.rrhh.planilla.dao.TipoSalidasDAO;//PAS20171U230200001 - solicitud de reintegro  
import pe.gob.sunat.rrhh.programacion.ejb.ProgramacionFacadeHome;
import pe.gob.sunat.rrhh.programacion.ejb.ProgramacionFacadeRemote;
import pe.gob.sunat.sol.BeanFechaHora;
import pe.gob.sunat.sol.BeanMensaje;
import pe.gob.sunat.sol.IncompleteConversationalState;
import pe.gob.sunat.sp.asistencia.bean.BeanHoraExtra;
import pe.gob.sunat.sp.asistencia.bean.BeanMarcacion;
import pe.gob.sunat.sp.asistencia.bean.BeanTurnoTrabajo;
import pe.gob.sunat.sp.asistencia.bean.BeanSolReinConcepto;//PAS20171U230200001 - solicitud de reintegro  
import pe.gob.sunat.sp.asistencia.bean.BeanSolReinDetalle;//PAS20171U230200001 - solicitud de reintegro  
import pe.gob.sunat.sp.asistencia.bean.BeanSolReintegro;//PAS20171U230200001 - solicitud de reintegro  
import pe.gob.sunat.sp.asistencia.dao.T1270DAO;
import pe.gob.sunat.sp.asistencia.dao.T1275DAO;
import pe.gob.sunat.sp.asistencia.dao.T1276DAO;
import pe.gob.sunat.sp.asistencia.dao.T1277DAO;
import pe.gob.sunat.sp.asistencia.dao.T1279DAO;
import pe.gob.sunat.sp.asistencia.dao.T1281DAO;
import pe.gob.sunat.sp.asistencia.dao.T1454DAO;//PAS20171U230200001 - solicitud de reintegro  
import pe.gob.sunat.sp.asistencia.dao.T1480DAO;
import pe.gob.sunat.sp.asistencia.dao.T8151DAO;
import pe.gob.sunat.sp.asistencia.dao.T8153DAO;//PAS20171U230200001 - solicitud de reintegro  
import pe.gob.sunat.sp.asistencia.dao.T8154DAO;//PAS20171U230200001 - solicitud de reintegro  
import pe.gob.sunat.sp.asistencia.dao.T8155DAO;//PAS20171U230200001 - solicitud de reintegro  
import pe.gob.sunat.sp.asistencia.dao.T9388DAO;
import pe.gob.sunat.sp.asistencia.ejb.delegate.SolicitudDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.SolicitudException;//WERR-PAS20155E230300132
import pe.gob.sunat.sp.bean.BeanT99;
import pe.gob.sunat.sp.dao.CorreoDAO;
import pe.gob.sunat.sp.dao.T02DAO;
import pe.gob.sunat.sp.dao.T12DAO;
import pe.gob.sunat.sp.dao.T5864DAO;
import pe.gob.sunat.sp.dao.T99DAO;
import pe.gob.sunat.tecnologia.menu.bean.MenuCliente;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;
import pe.gob.sunat.utils.bd.OracleSequenceDAO;
import pe.gob.sunat.sp.dao.T01DAO;
import pe.gob.sunat.framework.util.date.FechaBean;

















//ICAPUNAY - TRABAJADORES CON VACIONES PROGRAMADAS ACEPTADAS O ACTIVAS - 16/03/2011 */	
import java.util.Calendar;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import pe.gob.sunat.rrhh.asistencia.dao.T4502DAO;
// FIN ICAPUNAY - TRABAJADORES CON VACIONES PROGRAMADAS ACEPTADAS O ACTIVAS - 16/03/2011 */	

/**
 * 
 * @ejb.bean name="SolicitudFacadeEJB"
 *           description="SolicitudFacade"
 *           jndi-name="ejb/rrhh/facade/sp/asistencia/SolicitudFacadeEJB"
 *           type="Stateless" view-type="remote"
 * 
 * @ejb.home remote-class="pe.gob.sunat.sp.asistencia.ejb.SolicitudFacadeHome"
 *           extends="javax.ejb.EJBHome"
 * 
 * @ejb.interface remote-class="pe.gob.sunat.sp.asistencia.ejb.SolicitudFacadeRemote"
 *                extends="javax.ejb.EJBObject"
 * 
 * @ejb.resource-ref res-ref-name="jdbc/dcsp" res-type="javax.sql.DataSource" res-auth="Container"
 * @weblogic.resource-description res-ref-name="jdbc/dcsp" jndi-name="jdbc/dcsp"
 * 
 * @ejb.resource-ref res-ref-name="jdbc/dgsp" res-type="javax.sql.DataSource" res-auth="Container"
 * @weblogic.resource-description res-ref-name="jdbc/dgsp" jndi-name="jdbc/dgsp"
 * 
 * @ejb.resource-ref res-ref-name="jdbc/dcsig" res-type="javax.sql.DataSource" res-auth="Container"
 * @weblogic.resource-description res-ref-name="jdbc/dcsig" jndi-name="jdbc/dcsig"
 * 
 * @ejb.resource-ref res-ref-name="jdbc/dgsig" res-type="javax.sql.DataSource" res-auth="Container"
 * @weblogic.resource-description res-ref-name="jdbc/dgsig" jndi-name="jdbc/dgsig"
 * 
 * @ejb.env-entry name="dcsp" type="java.lang.String" value="jdbc/dcsp"
 * @ejb.env-entry name="dgsp" type="java.lang.String" value="jdbc/dgsp"            
 * @ejb.env-entry name="dcsig" type="java.lang.String" value="jdbc/dcsig"
 * @ejb.env-entry name="dgsig" type="java.lang.String" value="jdbc/dgsig"  
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
 * @weblogic.cache max-beans-in-cache="300"
 * @weblogic.pool max-beans-in-free-pool="300" initial-beans-in-free-pool="10"
 * 
 * @version 1.0
 */
public class SolicitudFacade extends StatelessAbstract {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Log log = LogFactory.getLog(getClass());
	
	ServiceLocator sl = ServiceLocator.getInstance(); 
	
	Propiedades constantes = new Propiedades(getClass(), "/constantes.properties");


	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList verificarSolicitud(String dbpool, HashMap mapa,
			String tipo, String usuario) throws IncompleteConversationalState,
			RemoteException {
		if (log.isDebugEnabled()) log.debug("ingreso - verificarSolicitud");
		if (log.isDebugEnabled()) log.debug("mapa: "+mapa);
		if (log.isDebugEnabled()) log.debug("tipo: "+tipo);
		if (log.isDebugEnabled()) log.debug("usuario: "+usuario);
		BeanMensaje beanM = new BeanMensaje();
		ArrayList mensajes = new ArrayList();
		try {
			T1279DAO movDAO = new T1279DAO();
			T12DAO t12dao = new T12DAO();
			T1282DAO VacacionDAO = new T1282DAO(dbpool);
			DataSource ds = sl.getDataSource("java:comp/env/jdbc/dcsp"); //agregado wrr
			HashMap tipoMov = movDAO.findByMov(dbpool, tipo);
			mapa.put("dbpool", dbpool);
			log.debug("MAPA VERIFICAR SOLICITUD : " + mapa);
			T1933DAO supDAO = new T1933DAO();
			boolean supervisor = supDAO.esSupervisor(dbpool, (String)mapa.get("userDest"), (String) mapa.get("tipo"),(String)mapa.get("codUO"));
			HashMap pp = new HashMap();
			pp.put("dbpool", dbpool);
			//EBV 24/02/2009  Para todo debe tomarse la UUOO del solicitante.
			pp.put("codUO",(String)mapa.get("userOrigUO"));
			//pp.put("codUO",(String)mapa.get("codUO"));
			pp.put("mov",(String)mapa.get("tipo"));
			//pp.put("cod_pers_ori",(String)mapa.get("userOrig"));
			pp.put("cod_pers_ori",(String)mapa.get("userDest"));
			if (log.isDebugEnabled()) log.debug("pp: " + pp);
			
			//ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp 05/05/2016
			HashMap pp2 = new HashMap();
			pp2.put("dbpool", dbpool);	
			pp2.put("codUO",(String)mapa.get("userOrigUO"));		
			pp2.put("mov",(String)mapa.get("tipo"));
			pp2.put("instancia1",Constantes.ESTACION_INICIAL);
			pp2.put("instancia2",Constantes.ESTACION_UNICA);
			if (log.isDebugEnabled()) log.debug("pp2: " + pp2);
			//FIN ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp 05/05/2016
			
			//ICAPUNAY - PAS20165E230300116 - delegacion sol  17/05/16
			String userOrig = mapa.get("userOrig")!=null?mapa.get("userOrig").toString().trim():"";//solicitante
			String userOrigUO = mapa.get("userOrigUO")!=null?mapa.get("userOrigUO").toString().trim():""; //uo solicitante
			if (log.isDebugEnabled()) log.debug("userOrig: " + userOrig);
			if (log.isDebugEnabled()) log.debug("userOrigUO: " + userOrigUO);
			
			//obteniendo el jefe de la uo solicitante 
			String jefeUO = t12dao.findJefeByUO(dbpool,userOrigUO);
			if (log.isDebugEnabled()) log.debug("jefeUO: " + jefeUO);
			
			//obteniendo uo superior de uo solicitante
			HashMap hmUniOrg = t12dao.findByCodUorga(dbpool,userOrigUO);
			String t12cod_repor= (hmUniOrg!=null && !hmUniOrg.isEmpty())?!hmUniOrg.get("t12cod_repor").equals("-")?hmUniOrg.get("t12cod_repor").toString().trim():"":"";
			if (log.isDebugEnabled()) log.debug("t12cod_repor: " + t12cod_repor);
			//FIN ICAPUNAY
			
			//JQC 19/02/2014 Si el solicitante es jefe no seguir flujo de supervisiones
			T12DAO uoDAO2 = new T12DAO();
			HashMap hj = uoDAO2.findByJefatura(dbpool, (String)mapa.get("userOrigUO"));
			boolean esJefe=((String) hj.get("t12cod_jefat")).equals((String)mapa.get("userOrig")) || ((String) hj.get("t12cod_encar")).equals((String)mapa.get("userOrig"));
			
			if (supervisor){
				String jefeSup = (String)mapa.get("jefeSup");
				if (jefeSup != null && !esJefe){ 	//JQC 19/02/2014
					pp.put("cod_pers_ori",jefeSup);
				}
				
			} else {
				
				//JROJAS4 - 13/02/2009
				Map mapa_receptorSolic = new HashMap();
				String receptorSolic = "";
				String jefeEncargadoFlujo = "";				
				
				//NIVEL 1 - USUARIO QUE SOLICITA, SU UO Y SU JEFE
				//String userOrig = mapa.get("userOrig").toString().trim();  //ICAPUNAY - PAS20165E230300116 - delegacion sol  17/05/16
				//String userOrigUO = mapa.get("userOrigUO").toString().trim();  //ICAPUNAY - PAS20165E230300116 - delegacion sol  17/05/16
				if (log.isDebugEnabled()) log.debug("userOrig: " + userOrig);
				if (log.isDebugEnabled()) log.debug("userOrigUO: " + userOrigUO);
				mapa_receptorSolic = t12dao.findJefeByTrabajador(dbpool, userOrigUO, userOrig);
				if (log.isDebugEnabled()) log.debug("INICIO - mapa_receptorSolic: " + mapa_receptorSolic);
				
				
				//JRR - 01/03/2011 - Para validar que exista un jefe o encargado de la unidad sÃ­ o sÃ­
				if (mapa_receptorSolic.get("t12cod_jefe_final")!=null && !mapa_receptorSolic.get("t12cod_jefe_final").toString().trim().equals("")) {
					
					if (log.isDebugEnabled()) log.debug("verificarSolicitud - El solicitante tiene Jefe asignado");

					String jefe_userOrig = mapa_receptorSolic.get("t12cod_jefe_final").toString().trim();
					String jefe_UO_userOrig = mapa_receptorSolic.get("t12cod_uorg_jefe").toString().trim();
					if (log.isDebugEnabled()) log.debug("jefe_userOrig: " + jefe_userOrig);//LOG
					if (log.isDebugEnabled()) log.debug("jefe_UO_userOrig: " + jefe_UO_userOrig);//LOG
					if (!(userOrig.trim().equals(jefe_userOrig.trim()))){
						jefe_UO_userOrig = userOrigUO;
						if (log.isDebugEnabled()) log.debug("jefe_UO_userOrig(jefe<>solicitante): " + jefe_UO_userOrig);//LOG

					}
					//BUSCO DELEGACION EN LA UNIDAD DEL JEFE DEL SOLICITANTE
					Map map_aux = new HashMap();
					map_aux.put("dbpool", dbpool);
					map_aux.put("codUO", jefe_UO_userOrig);
					map_aux.put("codOpcion", Constantes.DELEGA_SOLICITUDES);
					if (log.isDebugEnabled()) log.debug("mapa_aux: " + map_aux);
					Map mapa_delegado = t12dao.findDelegado((HashMap)map_aux);
					if (log.isDebugEnabled()) log.debug("mapa_delegado: " + mapa_delegado);

					//SI EXISTE DELEGACION
					if (mapa_delegado!=null) {
						String delegado = (mapa_delegado.get("t02cod_pers")!=null?mapa_delegado.get("t02cod_pers").toString().trim():"");
						String jefe_delegado = (mapa_delegado.get("cod_jefe")!=null?mapa_delegado.get("cod_jefe").toString().trim():"");

						if (!delegado.equals("") && delegado.equals(userOrig)) {
							if (log.isDebugEnabled()) log.debug("DELEGADO=userOriginal " + delegado+"-"+userOrig);
							//ES EL DELEGADO
							mapa_receptorSolic = t12dao.findJefeByTrabajador(dbpool, userOrigUO, jefe_delegado);
							if (log.isDebugEnabled()) log.debug("A mapa_receptorSolic: " + mapa_receptorSolic);
							receptorSolic = mapa_receptorSolic.get("t12cod_jefe_final").toString().trim();
							jefeEncargadoFlujo = receptorSolic;
							pp2.put("codUO",t12cod_repor);//ICAPUNAY - PAS20165E230300128 - registro solic. Delegado (delegado pertenece a uo donde es delegado)  07/06/16

							//PARA VER SI EXISTE DOBLE DELEGACION
							String UO_jefeEncargado1 = mapa_receptorSolic.get("t12cod_uorg_jefe").toString().trim();
							map_aux.put("codUO", UO_jefeEncargado1);
							mapa_delegado = t12dao.findDelegado((HashMap)map_aux);
							if (log.isDebugEnabled()) log.debug("DOBLE DELEGACION - mapa_delegado : " + mapa_delegado);
							if (mapa_delegado!=null) receptorSolic = (mapa_delegado.get("t02cod_pers")!=null?mapa_delegado.get("t02cod_pers").toString().trim():"");
							//

						} else {
							log.debug("entro aca");
							//ES OTRO
							receptorSolic = delegado;
							jefeEncargadoFlujo = jefe_userOrig;
						}

					} 		

					else {
						log.debug("entro aca2");
						//SI NO EXISTE DELEGACION SIEMPRE IRA AL JEFE
						receptorSolic = jefe_userOrig;
						jefeEncargadoFlujo = receptorSolic;
					}

					if (log.isDebugEnabled()) {
						log.debug("receptorSolic: " + receptorSolic);
						log.debug("jefeEncargadoFlujo: " + jefeEncargadoFlujo);
					}

					mapa.put("receptorSolic",receptorSolic); //ICAPUNAY - PAS20165E230300128 - registro solic. Delegado (delegado pertenece a uo donde es delegado)  07/06/16
					pp.put("cod_pers_ori", jefeEncargadoFlujo);
					pp.put("codUO", map_aux.get("codUO"));
					if (log.isDebugEnabled()) log.debug("pp1(codUO): " + pp);


/*				String jefeDel = (String)mapa.get("jefedelego");
				String jefenodel = (String)mapa.get("userOrig");
				if (jefeDel != null){
					//EBV 03/02/2009 Si el Jefe es el mismo que delego tomar el Destino
					if (jefeDel.trim().equals(jefenodel.trim())){
					}
					else{
						pp.put("cod_pers_ori",jefeDel);	
					}
					log.debug("MAPA VERIFICAR SOLICITUD : " + mapa);
				}
 */				


				} else {
					mensajes.add("La solicitud no puede registrarse porque el solicitante no tiene Jefe asignado. Favor coordine con su Analista.");
					if (log.isInfoEnabled()) log.info("La solicitud no puede registrarse porque el solicitante "+ userOrig +" no tiene Jefe asignado.");
				}
			//FIN - 01/03/2011	
				
			}
			
			pe.gob.sunat.rrhh.dao.T99DAO t99dao = new pe.gob.sunat.rrhh.dao.T99DAO(sl.getDataSource("java:comp/env/jdbc/dcsp"));// PAS20165E230300184 werr
			ParamBean prmAprobUnicoRRHH = t99dao.buscar(new String []{"522"}, ds, tipo);// PAS20165E230300184 werr
			if(prmAprobUnicoRRHH.getCodigo() != null){ // PAS20165E230300184 werr
				pp.put("cod_pers_ori","");
			}
			
			T1480DAO flujoDAO = new T1480DAO();
			//EBV 03/03/2009 UUOOFIN es la UUOO a registrar
			mapa.put("UUOOFIN",pp.get("codUO").toString());
			log.debug("MAPA VERIFICAR APROBADORES : " + pp);
			ArrayList datospp = flujoDAO.findAprobadores(pp);
			log.debug("MAPA ENCONTRAR APROBADORES : "+ datospp);
			pp.put("codOpcion",Constantes.DELEGA_SOLICITUDES);

			HashMap delegado = t12dao.findDelegado(pp);
			log.debug("delegado:"+delegado);
			if (((String) mapa.get("tipo")).trim().equals(Constantes.LICENCIA_LABOR_EXCEPCIONAL )){
				datospp.add(mapa);	
			}
			if ((datospp.size() == 0)   || (datospp.size() == 0 && delegado == null )){
				pp.put("codUO",(String)mapa.get("userOrigUO"));
				log.debug("MAPA VERIFICAR APROBADORES1 : " + pp);
				datospp = flujoDAO.findAprobadores(pp);
				log.debug("MAPA ENCONTRAR APROBADORES1 : "+ datospp);
				delegado = t12dao.findDelegado(pp);
				log.debug("delegado1 :"+delegado);
				//EBV 03/03/2009 UUOOFIN es la UUOO a registrar
				mapa.put("UUOOFIN",pp.get("codUO").toString());

			}

			
			if (jefeUO.equals(userOrig)){//si solicitante es jefe de uo solicitante, debe buscar aprobadores en uo superior	 //ICAPUNAY - PAS20165E230300116 - delegacion sol  17/05/16
				if (log.isDebugEnabled()) log.debug("jefeUO=userOrig " + jefeUO+"-"+userOrig);
				if ((datospp.size() == 0)   || (datospp.size() == 0 && delegado == null )){
					//pp.put("codUO",(String)mapa.get("codUO"));  //ICAPUNAY - PAS20165E230300116 - delegacion sol  17/05/16
					pp.put("codUO",t12cod_repor);  //ICAPUNAY - PAS20165E230300116 - delegacion sol  17/05/16
					log.debug("MAPA VERIFICAR APROBADORES 2 : " + pp);
					datospp = flujoDAO.findAprobadores(pp);
					log.debug("MAPA ENCONTRAR APROBADORES 2 : "+ datospp);
					delegado = t12dao.findDelegado(pp);
					log.debug("delegado1 :"+delegado);
					//EBV 03/03/2009 UUOOFIN es la UUOO a registrar
					mapa.put("UUOOFIN",pp.get("codUO").toString());
				}
				pp2.put("codUO",t12cod_repor);
				if (log.isDebugEnabled()) log.debug("pp22(codUO): " + pp2);
			}//add linea 17/05/16			
						
			if (log.isDebugEnabled()) log.debug("pp2 finalizado: " + pp2);
			ArrayList aprobadorespp2 = flujoDAO.findAprobadoresByInstancias(pp2);
			log.debug("aprobadorespp2: "+aprobadorespp2);
			HashMap aprobador = (aprobadorespp2!=null && aprobadorespp2.size()>0)? (HashMap)aprobadorespp2.get(0):null;
			log.debug("aprobador: "+aprobador);
			String origen = (aprobador!=null && !aprobador.isEmpty())?aprobador.get("cod_pers_ori").toString():""; //aprobador de uo solicitante
			log.debug("origen: "+origen);
			String cod_pers_orii= pp.get("cod_pers_ori")!=null?pp.get("cod_pers_ori").toString().trim():""; //jefeEncargadoFlujo de uo solicitante
			log.debug("cod_pers_orii: "+cod_pers_orii);
			if (!origen.equals("") && !cod_pers_orii.equals("") && !cod_pers_orii.equals(origen) ){ //si jefeEncargadoFlujo<> aprobador de uo solicitante, emitir mensaje
				log.debug("entro if");
				mensajes.add("La solicitud no puede registrarse porque el Jefe "+ cod_pers_orii +" de la unidad "+ pp2.get("codUO").toString() + " no esta como aprobador para esta solicitud. Favor coordine con su Analista.");
				log.debug("La solicitud no puede registrarse porque el Jefe "+cod_pers_orii +" de la unidad "+pp2.get("codUO")!=null?pp2.get("codUO").toString():"" + " no esta como aprobador " + origen+" para esta solicitud");
			} else {
					log.debug("entro else");
			//FIN ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp  05/05/2016

					if ((datospp.size() == 0)   || (datospp.size() == 0 && delegado == null )){
						mensajes.add("La solicitud no puede ser atendida por error en la configuracion. Favor coordine con su Analista.");
					}
					else{
			            //MTM si es una solicitud de labor excepcional
						if(tipo.equals(Constantes.MOV_LABOR_EXCEPCIONAL)){
							mensajes = this.validaLabor(mapa, tipoMov);
						}
							
					//si es una solicitud de omision de marca
					if (((String) tipoMov.get("mov")).equals(Constantes.MOV_OMISION_MARCA) ||
						((String) tipoMov.get("mov")).equals(Constantes.MOV_ANULACION_MARCA)) {
						mensajes = this.validaMarcaciones(mapa, tipoMov);
					}
					
					//si es una solicitud de licencia
					if (((String) tipoMov.get("tipo_id")).equals(Constantes.TIPO_MOV_LICENCIA)) {
						if (log.isDebugEnabled()) log.debug("antes de-validaLicencia");
						mensajes = this.validaLicencia(mapa, tipoMov, true);
						if (log.isDebugEnabled()) log.debug("despues de-validaLicencia");
					}
		
					//EBV 11/06/2012
					/*if (mensajes.isEmpty()) {
						if(tipo.equals(Constantes.MOV_SOLICITUD_COMPENSACION)){
							if (log.isDebugEnabled()) log.debug("antes de-validaSolicitudCompensacion");
							mensajes = this.validaSolicitudCompensacion(mapa, tipoMov);
							if (log.isDebugEnabled()) log.debug("despues de-validaSolicitudCompensacion");
						}
					}*/
					
					//si es una solicitud de vacaciones
					if (((String) tipoMov.get("tipo_id")).equals(Constantes.TIPO_MOV_VACACIONES)) {
					
						if (tipo.equals(Constantes.VACACION_VENTA)) {
							
							mensajes = this.validaVentaVacaciones(dbpool, (String) mapa
									.get("userOrig"), (String) mapa.get("annoVac"),(String) mapa.get("diasVac"),
									usuario);
							
							BeanFechaHora bfh = new BeanFechaHora();
							String fechaActual = bfh.getFormatDate("dd/MM/yyyy");
							String fechaInicio = bfh.getFormatDate("01/MM/yyyy");
							
							AsistenciaFacadeHome facadeHome = (AsistenciaFacadeHome) ServiceLocator
											.getInstance().getRemoteHome(AsistenciaFacadeHome.JNDI_NAME,
											AsistenciaFacadeHome.class);
							AsistenciaFacadeRemote facadeRemote = facadeHome.create();	
							int numDias = facadeRemote.obtenerDiasHabilesDiferencia(dbpool,fechaInicio,fechaActual) + 1;
							
		
							T99DAO codigoDAO = new T99DAO();
												
							String diasVenta = codigoDAO.findParamByCodTabCodigo(dbpool,
									Constantes.CODTAB_PARAMETROS_ASISTENCIA,
									Constantes.DIAS_HABILES_MES_VENTA_VACACIONES);
		
							
							
							if (numDias > Integer.parseInt(diasVenta)) {
								 //FRD - 11/05/2009
						         mensajes.add("La Solicitud Sólo Puede Ser Generada Dentro De Los 5 Últimos Días Útiles Del Mes.");
						         //mensajes.add("La solicitud s&oacute;lo puede ser generada dentro de los ".concat(diasVenta).concat(" primeros d&iacute;as &uacute;tiles del mes."));
					        }					
							
		/*					mapa.put("fechaIni", Utiles.obtenerFechaActual());
							mapa.put("fechaFin", Utiles.obtenerFechaActual());  */
		
							/************** COMSA - 09/12/2008 *************/
		  					if(mapa.get("fechaIni")==null ||mapa.get("fechaIni").toString().trim().equals("")){
		  						mapa.put("fechaIni", new FechaBean().getFormatDate("dd/MM/yyyy").toString());
		  						mapa.put("fechaFin", new FechaBean().getFormatDate("dd/MM/yyyy").toString());	
		  					}else if(mapa.get("numDias")!=null){	
		  						mapa.put("fechaFin", Utiles.dameFechaSiguiente((String) mapa.get("fechaIni"),
		  								Integer.parseInt((String) mapa.get("numDias")) - 1));
		  					}
		  					/***********************************************/
						}
		
						if (tipo.equals(Constantes.VACACION)) {
							
							mapa.put("fechaFin", Utiles
									.dameFechaSiguiente((String) mapa.get("fechaIni"),
											Integer.parseInt((String) mapa
													.get("numDias")) - 1));
		
							mensajes = this.validaVacacionesEfectivasAdelanto(dbpool,
									(String) mapa.get("userOrig"), (String) mapa
											.get("fechaIni"), (String) mapa
											.get("fechaFin"), Integer
											.parseInt((String) mapa.get("numDias")),(String) mapa.get("annoVac"),(String)mapa.get("permitirAdelanto"));
						}
		
						/*************************/
						//JRR NUEVA SOLICITUD - 15/04/2009
						if (tipo.trim().equals(Constantes.VACACION_INDEMNIZADA)){
		
							if (log.isDebugEnabled()) log.debug("LLEGA AQUI 1 -  mapa: " + mapa);
		
							String fechaIni = (String) mapa.get("fechaIni");
							String fechaFin = (String) mapa.get("fechaFin");
							int numDias = Utiles.obtenerDiasDiferencia(fechaIni, fechaFin) + 1;
							
							List prog = VacacionDAO.findProgVacVencidas(mapa);
		
							if (prog!= null && prog.size()>0)
								mensajes.add("Ya existe una Solicitud ingresada para esta Vacacion Vencida. Por favor verifique.");
							else {
								mensajes = this.validaVacacionesEfectivas(dbpool,
											(String) mapa.get("userOrig"), (String) mapa
													.get("fechaIni"), (String) mapa
													.get("fechaFin"), numDias,(String) mapa.get("annoVac"));
							}
						}
						/***********************/
						
						if (tipo.equals(Constantes.VACACION_SUSPENDIDA)) {
							mapa.put("txtObsSeg", (String) mapa.get("asunto"));
						}
		
						if (tipo.equals(Constantes.VACACION_PROGRAMADA)) {
							mensajes = this.validaVacacionesEfectivas(dbpool,
									(String) mapa.get("userOrig"), (String) mapa
											.get("fechaIni"), (String) mapa
											.get("fechaFin"), Integer
											.parseInt((String) mapa.get("numDias")),(String) mapa.get("annoVac"));
							mapa.put("txtObsSeg", (String) mapa.get("asunto"));
						}
						
						if (tipo.equals(Constantes.REPROGRAMACION_VACACION)) {
							
							mensajes = this.validaReprogramacion(mapa);
							mapa.put("txtObsSeg", (String) mapa.get("asunto"));
							
							if (mensajes.isEmpty()){	
								
								mensajes = this.actualizaSolicitudReprogramacion(dbpool, mapa,
										Constantes.REPROGRAMACION_VACACION,
										Constantes.PROG_PROGRAMADA);
								
							}					
						}
						
						if (tipo.equals(Constantes.VACACION_POSTERGADA)) {
							log.debug("Anio al reg:"+(String) mapa.get("annoVac"));
							mensajes = this.validaPostergacion(mapa);
						}				
					}
					//PAS20171U230200001 - solicitud de reintegro    
					log.debug("movimiento--->" + ((String) tipoMov.get("mov")));
					if (((String) tipoMov.get("mov")).equals(Constantes.MOV_REINTEGRO_POR_DESCUENTO_ASISTENCIA)) {
						log.debug("Ingresa a validacion de reintegro de descuento por asistencia");
						mensajes.clear();
						mensajes = this.validaReintegroPorDsctoAsistencia(dbpool, mapa);
					}
					if (((String) tipoMov.get("mov")).equals(Constantes.MOV_REINTEGRO_POR_DESCUENTO_SUBSIDIO)) {
						log.debug("Ingresa a validacion de reintegro de descuento por subsidio");
						mensajes.clear();
						mensajes = this.validaReintegroPorDsctoLicencia (dbpool,mapa);
					}
					//fin-PAS20171U230200001 - solicitud de reintegro  
					}
				} //solo esta llave se agrego - ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp  05/05/2016	

			if (mensajes.isEmpty()) {
				mapa.put("mapdel",delegado);
				log.debug("MAPA a Registrar "+mapa);
				mensajes = this.registraSolicitud(mapa, tipo, usuario);
			}

		} catch (Exception e) {
			log.error(e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return mensajes;
	}

	/**WERR-PAS20155E230300132
	 * @throws SQLException 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
  public boolean inicioLicenciaHabil(String dbpool, String fechaAlta, String fechaNac){
		T01DAO paramDAO = new T01DAO();
		String fechaAltaSgte = null;
		boolean esDiaHabil = false;
		boolean esInicioHabil = false;	
		log.debug("fechaAlta:"+fechaAlta);
		log.debug("fechaNac:"+fechaNac);
	    int k = 1;
		//true = 2
		//false = 1
	try {

			while(esDiaHabil == false){
				log.debug("k:"+k);
				log.debug("entro whileeeeF");
				//fechaAltaSgte = Utiles.dameFechaSiguiente(fechaAlta, 1);
				fechaAltaSgte = Utiles.dameFechaSiguiente(fechaAlta, k );
				log.debug("fechaAltaSgteeeeeeF:"+fechaAltaSgte);
				//if(!paramDAO.findByFechaFeriado(dbpool, fechaAltaSgte) && !Utiles.isWeekEnd(fechaAltaSgte)){
				if(paramDAO.findByFechaFeriado(dbpool, fechaAltaSgte) || Utiles.isWeekEnd(fechaAltaSgte)){
					esDiaHabil = false;	
					k=k+1;
					log.debug("k2:"+k);
					//esDiaHabil = true;
				}
				//nuevo add
			    else{
					log.debug("fechaAltaSgte es dia habil");
					esDiaHabil = true;
				}
				//nuevo add
			}
			if (esDiaHabil){
				log.debug("entro");
				if (fechaNac.trim().equals(fechaAltaSgte.trim())){
					log.debug("inicio igual fecha alta sgte habil");
					esInicioHabil=true;
				}
			}	
			log.debug("esInicioHabil:"+esInicioHabil);
			
	} catch (SQLException e) {
		e.printStackTrace();
	}
	return esInicioHabil;
  }
//end WERR-PAS20155E230300132

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList validaLicencia(HashMap mapa, HashMap tipoMov,
			boolean validaRango) throws IncompleteConversationalState,
			RemoteException {

		ArrayList mensajes = new ArrayList();
		try {
			log.debug("metodo validaLicencia.... mapa:"+mapa+"     , tipoMov:"+tipoMov+",         validaRango"+validaRango);
			AsistenciaFacadeHome facadeHome = (AsistenciaFacadeHome) ServiceLocator
					.getInstance().getRemoteHome(AsistenciaFacadeHome.JNDI_NAME,
					AsistenciaFacadeHome.class);
			AsistenciaFacadeRemote facadeRemote = facadeHome.create();			
			
			String tipo = (String)tipoMov.get("mov");
			String qvalida = (String)tipoMov.get("qvalida");
			String dbpool = (String) mapa.get("dbpool");
			String codPers = (String) mapa.get("userOrig");
			String fechaIni = (String) mapa.get("fechaIni");
			String fechaFin = (String) mapa.get("fechaFin");
			String fechaNac = (String) mapa.get("fechaNac");
			String regimenCol = (String) mapa.get("regimenCol");//ICR - 28/11/2012 PAS20124E550000064 labor excepcional
			if (log.isDebugEnabled()) log.debug("validaLicencia(regimenCol): "+regimenCol);//ICR - 28/11/2012 PAS20124E550000064 labor excepcional
			//ICAPUNAY 09/02/2012-POR PRUEBAS DESARROLLO
			if (log.isDebugEnabled()) log.debug("tipo: "+tipo);
			if (log.isDebugEnabled()) log.debug("qvalida: "+qvalida);
			if (log.isDebugEnabled()) log.debug("codPers: "+codPers);
			if (log.isDebugEnabled()) log.debug("fechaIni: "+fechaIni);
			if (log.isDebugEnabled()) log.debug("fechaFin: "+fechaFin);
			if (log.isDebugEnabled()) log.debug("fechaNac: "+fechaNac);	
			//ICAPUNAY 09/02/2012-POR PRUEBAS DESARROLLO

			//PRAC-ASANCHEZ 09/07/2009
			Map mov = new HashMap();
			String ind;
			//
			
			T99DAO codigoDAO = new T99DAO();
			//prac-jcallo
			log.debug("DBPOOL :"+dbpool);
			T1273DAO licenciaDAO = new T1273DAO(dbpool);
			T1282DAO vacacionDAO = new T1282DAO(dbpool);
			T1279DAO movDAO = new T1279DAO();
			T01DAO paramDAO = new T01DAO();//ICAPUNAY 07/06 PASE 2012-64 LABOR EXCEPCIONAL
			T4821DAO t4821DAO = new T4821DAO(sl.getDataSource("java:comp/env/jdbc/dgsp"));//ICAPUNAY 07/06 PASE 2012-64 LABOR EXCEPCIONAL
			
			//JRR - 08/04/2009
			pe.gob.sunat.rrhh.dao.T99DAO t99dao = new pe.gob.sunat.rrhh.dao.T99DAO(sl.getDataSource("java:comp/env/jdbc/dcsp"));
			
			if (validaRango && !tipo.equals(Constantes.LICENCIA_NACIMIENTO)) {

				int numDiasAntes = movDAO.findNumDiasByTipo(dbpool, tipo, "1");
				int numDiasDespues = movDAO.findNumDiasByTipo(dbpool, tipo, "2");				
				//log.debug("numDiasAntes="+numDiasAntes+"      ,numDiasDespues="+numDiasDespues);
				//log.debug("parametros enviados"+dbpool+", "+tipo+", "+Utiles.obtenerFechaActual()+", "+fechaIni+", "+fechaFin);
				int valida = facadeRemote.validaDiasAntesDespues(dbpool, tipo,
						Utiles.obtenerFechaActual(), fechaIni, fechaFin);
				//log.debug("... valida="+valida);
				if (valida < 0) {
					mensajes.add("La cantidad m&iacute;nima de d&iacute;as de anticipaci&oacute;n para registrar la solicitud es "
									+ numDiasAntes + ".");
				}
				if (valida > 0) {
					mensajes.add("La cantidad m&iacute;nima de d&iacute;as posteriores al fin de licencia para el registro de la solicitud es de "
									+ numDiasDespues + ".");
				}
			}
			
			if (tipo.equals(Constantes.LICENCIA_NACIMIENTO)) {
				
				//PRAC-ASANCHEZ 09/07/2009
				mov = movDAO.findByMov(dbpool, Constantes.LICENCIA_NACIMIENTO);
				ind = (String)mov.get("ind_dias"); //si ind es 1 solo se cuentan dias habiles, si es 0: dias por igual
				//
				
				if (fechaNac==null) fechaNac = fechaIni;
				
				//PRAC-ASANCHEZ 24/06/2009
				/*if(((String)mapa.get("indPreguntaSiNo")).equals(Constantes.ACTIVO)){
					fechaNac = facadeRemote.fechaFinXDiasHabiles(dbpool, fechaNac, 1);
				}*/
				//
				
				T1270DAO tpDAO = new T1270DAO();
//				T01DAO paramDAO = new T01DAO();
				BeanTurnoTrabajo turno = tpDAO.joinWithT45ByCodFecha(dbpool,codPers,fechaNac);
				
				//JRR
				int diasLicNac = 0;
				String fechaFinHabiles = "";
				Map mapaT99 = new HashMap();
				Map datos = new HashMap();
				datos.put("t99cod_tab", constantes.leePropiedad("CODTAB_DIAS_LICENCIA"));
				datos.put("t99tip_desc", constantes.leePropiedad("T99DETALLE"));
				datos.put("t99estado", constantes.leePropiedad("ACTIVO"));
				datos.put("t99codigo", tipo);
				
				//JRR PEGADO ACA - 18/11/2009
				mapaT99 = t99dao.findParamByCodTabCodigo(datos);
				if (mapaT99 != null) 
				diasLicNac = Integer.parseInt(mapaT99.get("t99descrip").toString().trim()) - 1;						

				if(ind.equals(Constantes.ACTIVO)){
					if(tipo.equals(Constantes.LICENCIA_NACIMIENTO))
						fechaFinHabiles = Utiles.dameFechaSiguiente(fechaNac, diasLicNac);
					else
						fechaFinHabiles = facadeRemote.obtenerFechaSgtesDiasHabiles(dbpool, fechaNac, diasLicNac);
					if (!fechaFinHabiles.equals("")) {
						mapa.put("fechaIni", fechaNac);
						fechaFin = fechaFinHabiles;
						mapa.put("fechaFin", fechaFinHabiles);
					}
				}else{
					fechaFin = Utiles.dameFechaSiguiente(fechaNac, diasLicNac);
					mapa.put("fechaIni", fechaNac);
					mapa.put("fechaFin", fechaFin);
				}
			
			}
			
			if (tipo.equals(Constantes.LICENCIA_MATRIMONIO)) {
				//verificamos que no posea saldo vacacional
				T1281DAO dao = new T1281DAO();
				//log.debug("dbpool:"+dbpool+" ... codPers:"+codPers);
				int saldo = dao.findByCodPers(dbpool,codPers.trim());
				if (saldo>0) {
					
					//ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta vacaciones (SC)
					if(regimenCol.equals(Constantes.CODREL_REG1057)){
						mensajes.add("El trabajador posee saldo vacacional."); 
					}else{
						mensajes.add("El trabajador posee "+saldo+" d&iacute;as de saldo vacacional."); 
					}
					//FIN ICAPUNAY 
					//mensajes.add("El trabajador posee "+saldo+" d&iacute;as de saldo vacacional."); ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta vacaciones (SC)
				}

				//0. Si es trabajador CAS
				
				if(mensajes.isEmpty())
				if(regimenCol.equals(Constantes.CODREL_REG1057)){
					log.debug("regimenCol.equals(Constantes.CODREL_REG1057)");
					
					//1. El solicitante no debe tener licencia de matrimonio aprobada
					T1455DAO seguimientoDAO = new T1455DAO(dbpool);
					HashMap paramsSeguimiento = new HashMap();
					paramsSeguimiento.put("codPers", codPers);
					paramsSeguimiento.put("licencia", tipo);
					List seguimientos = seguimientoDAO.findSolicitudesByCodpersAndLicencia(paramsSeguimiento);
					log.debug("paramsSeguimiento: "+paramsSeguimiento);
					log.debug("seguimientos: "+seguimientos);
					HashMap seguimiento;					
					if(seguimientos!=null && seguimientos.size()>0){
						 seguimiento = (HashMap)seguimientos.get(0);
						 String accion = ((String)seguimiento.get("accion_id")).trim();
						 String estado = ((String)seguimiento.get("estado_id")).trim();
						 String anno = (String)seguimiento.get("anno");
						 Integer numero = (Integer)seguimiento.get("numero");
						 if(accion.equals(Constantes.ACCION_APROBAR) && estado.equals(Constantes.ESTADO_CONCLUIDA))
							 mensajes.add("El trabajador ya cuenta una solicitud de licencia de matrimonio aprobada Nro. "+anno+"-"+numero+".");
						 
						//2. El solicitante no debe tener licencia de matrimonio en seguimiento
						 else if(estado.equals(Constantes.ESTADO_SEGUIMIENTO))
							 mensajes.add("El trabajador cuenta con una solicitud de licencia de matrimonio en seguimiento Nro. "+anno+"-"+numero+".");
					}
					
					//1.1. Se busca la licencia en la tabla de vacaciones.
					if(mensajes.isEmpty()){
						HashMap paramsVacacionMatrimonio = new HashMap();
						paramsVacacionMatrimonio.put("licencia", Constantes.VACACION);
						paramsVacacionMatrimonio.put("cod_pers", codPers);
						paramsVacacionMatrimonio.put("est_id", Constantes.ACTIVO);
						paramsVacacionMatrimonio.put("ind_matri", Constantes.ACTIVO);
						List lstVacacionesMatrimonio = vacacionDAO.findVacacionesProgramadasByParams(paramsVacacionMatrimonio);
						log.debug("lstVacacionesMatrimonio: "+lstVacacionesMatrimonio);
						if(lstVacacionesMatrimonio!=null && lstVacacionesMatrimonio.size()>0){
							HashMap vacacionMatrimonio = (HashMap)lstVacacionesMatrimonio.get(0);
							String strFfinicio = (String)vacacionMatrimonio.get("ffinicio_desc");
							mensajes.add("El trabajador ya cuenta una licencia de matrimonio registrada que inicia el " + strFfinicio.substring(0, 10) + ".");
						}
					}
										
					//6. (7)El solicitante debe tener antigï¿½edad mayor a 3 meses de su fecha de ingreso de no tener a la fecha ningï¿½n aï¿½o vacacional generado [sino ver el punto EX071 de excepciones].
					if(mensajes.isEmpty()){
						//6.1. Obtener fecha de ingreso
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
						T02DAO empleadoDAO = new T02DAO();	
						Map mapaEmpleado = empleadoDAO.findEmpleado("java:comp/env/jdbc/dgsp",codPers);	
						Date dteFechaIngreso = (mapaEmpleado!=null && !mapaEmpleado.isEmpty())?((Date)mapaEmpleado.get("t02f_ingsun")):null;
						String strFechaIngreso = sdf.format(dteFechaIngreso);
						
						//6.2. Obtener aï¿½o de trabajo
						String strFechaActual = Utiles.obtenerFechaActual();
						String strAnioActual = Utiles.dameAnho(strFechaActual);
						String strFechaCalculo = strFechaIngreso.substring(0, 6) + strAnioActual;
						String anioSaldoVacVig;
						int difDias = Utiles.obtenerDiasDiferencia(strFechaCalculo, strFechaActual);
						if (difDias >= 0) 
							anioSaldoVacVig = strAnioActual;					
						else 
							anioSaldoVacVig = (Integer.parseInt(strAnioActual) - 1) + ""; 
						
						//6.3 Restamos tres meses a la fecha actual
						//Date dteFechaActual = sdf.parse(strFechaActual);
						Date dteFechaIngresoTrunco = sdf.parse(strFechaIngreso.substring(0, 6)+anioSaldoVacVig);
						
						
						Date dteFechaInicio = sdf.parse(fechaIni);
						Calendar calFechaInicio = Calendar.getInstance();
						calFechaInicio.setTime(dteFechaInicio);
						calFechaInicio.add(Calendar.MONTH, -3);
						Date dteFechaInicioDisminuido = calFechaInicio.getTime();
						log.debug("dteFechaInicio: "+dteFechaInicio);
						log.debug("dteFechaInicioDisminuido: "+dteFechaInicioDisminuido);
						log.debug("dteFechaIngresoTrunco (validaLicencia-SolicitudFacade): "+dteFechaIngresoTrunco);
						
						String ddmmFecIngTrunco = new SimpleDateFormat("dd/MM/yyyy").format(dteFechaIngresoTrunco).toString().substring(0,6);//ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta vacaciones (SC)
						String ddmmFecIniDismin = new SimpleDateFormat("dd/MM/yyyy").format(dteFechaInicioDisminuido).toString().substring(0,6);//ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta vacaciones (SC)
						
						//if(dteFechaIngresoTrunco.after(dteFechaInicioDisminuido))	//ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta vacaciones (SC)
						if(dteFechaIngresoTrunco.after(dteFechaInicioDisminuido) || ddmmFecIngTrunco.equals(ddmmFecIniDismin))	//ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta vacaciones (SC)
							mensajes.add("Debe tener una antiguedad mayor a 3 meses de su fecha de ingreso o generación de saldo vacacional para esta licencia.");
						
						//7. La fecha de inicio de la solicitud debe ser menor a la prï¿½xima fecha de generaciï¿½n (dï¿½a y mes de fecha de ingreso) de saldo vacacional.
						if(mensajes.isEmpty()){
							int anioSaldoVacProx = Integer.parseInt(anioSaldoVacVig)+1;
							Date dteProxFechaGeneracion = sdf.parse(strFechaIngreso.substring(0, 6)+anioSaldoVacProx);
							Date dteFfin = Utiles.stringToDate(fechaFin);
							if(!dteFfin.before(dteProxFechaGeneracion))
								mensajes.add("La fecha fin de la solicitud debe ser menor a la próxima fecha de generación de saldo vacacional (día y mes correspondientes a la fecha de ingreso).");
						}
					}
				}					
			}		
			
			//ICR - 28/11/2012 PAS20124E550000064 labor excepcional-MTM
			if (tipo.trim().equals(Constantes.MOV_SOLICITUD_COMPENSACION)) {
				if (log.isDebugEnabled()) log.debug("entro a LE de validaLicencia");
				if (log.isDebugEnabled()) log.debug("fechaIni: "+fechaIni);
				if (log.isDebugEnabled()) log.debug("fechaFin: "+fechaFin);
				if (log.isDebugEnabled()) log.debug("regimenCol: "+regimenCol);
				
				boolean esDiaHabil = true;
				boolean esNoHabil = false;
				T1270DAO tpDAO = new T1270DAO();
				int diferencia = 0;
				diferencia = Utiles.obtenerDiasDiferencia(fechaIni, fechaFin)+1;
				int descuento = 0;
				int descuentoTotal = 0; //add ICR 21/12/2012 sumando total de descuentos para adm (feriados y fin sema) en rango de fechas (fechaIni y fechaFin)
				int diferencia2 = 0;
				int diferencia3 = 0;
				float horas = 0;
				int	dias = 0;
				String fechaEval="";//add icr
								
				if (esDiaHabil){
					//buscar si en el rango de fecha existe un turno no asignado o si tiene un turno operativo
					for (int i = 0; i < diferencia; i++) {
						log.debug("inicio for de diferencia");
						log.debug("i :" + i);
						log.debug("diferencia :" + diferencia);
						log.debug("fechaIni :" + fechaIni);
						String fechaSig = Utiles.dameFechaSiguiente(fechaIni, i);
						String fechaMostrar = fechaSig;
						log.debug("fechaSig :" + fechaSig);
						log.debug("fechaMostrar :" + fechaMostrar);
						BeanTurnoTrabajo turno1 = tpDAO.joinWithT45ByCodPersFecha_Controlado_OperAdm1dia(dbpool, codPers, fechaSig);
						if (turno1 != null){
							log.debug("turno de 1 dia");
							Date fechaDate = new FechaBean(turno1.getFechaFin()).getSQLDate();											
							log.debug("finTurno :" + new FechaBean(fechaDate.toString(),"yyyy-MM-dd").getFormatDate("dd/MM/yyyy").toString());							
							diferencia2 = Utiles.obtenerDiasDiferencia(fechaSig,new FechaBean(fechaDate.toString(),"yyyy-MM-dd").getFormatDate("dd/MM/yyyy").toString())+1;
							log.debug("diferencia2 :" + diferencia2);
							diferencia3 = diferencia - i;
							log.debug("diferencia3 :" + diferencia3);							
							if(diferencia2 > 0){
								log.debug("entrada1(diferencia2 > 0)");
								if (diferencia3 > diferencia2){
									log.debug("validaLicencia entro(diferencia3>diferencia2)");
									descuento = 0;
									for ( int j = i;j<i+diferencia2;j++){										
										log.debug("j: "+j);
										log.debug("i: "+i);									
										fechaEval = Utiles.dameFechaSiguiente(fechaIni,j);//fechaSig debe ser fechaIni										
										log.debug("fechaEval :" + fechaEval);
										if (fechaEval!=null && !fechaEval.equals("")){										
											log.debug("entro a validar extremos fechaIni o fechaFin(fechaEval): "+fechaEval);
											if(!turno1.isOperativo()){//adm
												log.debug("turno es adm");
												if ((paramDAO.findByFechaFeriado(dbpool,fechaEval) || Utiles.isWeekEnd(fechaEval))) {
													log.debug("dia es feriado o fin de semana(fechaEval): "+fechaEval);
													esNoHabil = true;
													descuento = descuento + 1;	
													log.debug("descuento :" + descuento);
												}
											}										
											if (!esNoHabil){
												if(t4821DAO.verificaRegistroSolicitudCompensacion(dbpool, 
														//(String) mapa.get("UUOOFIN"), 
														(String) mapa.get("userOrig"),
														Constantes.MOV_SOLICITUD_COMPENSACION,
														//fechaEval) == false){
														fechaEval) == true){
													mensajes.add("Ya existe solicitud(es) de compensacion en seguimiento para la fecha: "+ fechaEval + " en el rango de fechas.");
													log.debug("Ya existe solicitud(es) de compensacion en seguimiento para la fecha: "+ fechaEval + " en el rango de fechas.");
												}else{
													log.debug("no existe solicitud de compensacion en seguimiento para fecha: " + fechaEval);
												}
											}												
										}else{
											log.debug("no existe fechaEval: " + fechaEval);
										}										
									}
									log.debug("i aca :" + i);
									log.debug("diferencia2 aca :" + diferencia2);
									i=i+(diferencia2-1);
									log.debug("i final aca :" + i);									
									dias = dias + (diferencia2-descuento);
									log.debug("entrada1.0(dias) :" + dias);	
									descuentoTotal = descuentoTotal + descuento; //add ICR 21/12/2012 sumando total de descuentos para adm (feriados y fin sema) en rango de fechas (fechaIni y fechaFin)
									log.debug("descuentoTotal1: " + descuentoTotal); //add ICR 21/12/2012 sumando total de descuentos
									if (!regimenCol.equals("")){							//jquispe: ini(26/07/2013)
										if (Math.abs(Utiles.obtenerHorasDiferencia(turno1.getHoraIni(), turno1.getHoraFin()))<=8){//<=8 horas --- queda 8 horas																	
												horas =  horas + Math.abs(Utiles.obtenerHorasDiferencia(turno1.getHoraIni(), turno1.getHoraFin()))*(diferencia2-descuento);
												if (log.isDebugEnabled()) log.debug("horas1(cas_noCas <= 8): " + horas);																											
										}else{																					
												horas =  horas + Math.abs(Utiles.obtenerHorasDiferencia(turno1.getHoraIni(), turno1.getHoraFin())-1)*(diferencia2-descuento);
												if (log.isDebugEnabled()) log.debug("horas1(cas_noCas > 8): " + horas);
										}								
									}														//jquispe: fin							
								}else{
									log.debug("validaLicencia entro2(diferencia3<=diferencia2)");
									descuento=0;
									for ( int j = i;j<i+diferencia3;j++){
										log.debug("j: "+j);
										log.debug("i: "+i);
										fechaEval = Utiles.dameFechaSiguiente(fechaIni,j);//fechaSig debe ser fechaIni									
										log.debug("fechaEval :" + fechaEval);
										if (fechaEval!=null && !fechaEval.equals("")){									
											log.debug("entro2 a validar extremos fechaIni o fechaFin(fechaEval): "+fechaEval);
											if(!turno1.isOperativo()){//adm
												log.debug("turno es adm");
												if ((paramDAO.findByFechaFeriado(dbpool,fechaEval) || Utiles.isWeekEnd(fechaEval))) {
													log.debug("dia2 es feriado o fin de semana(fechaEval): "+fechaEval);
													esNoHabil = true;
													descuento = descuento + 1;
													log.debug("descuento :" + descuento);
												}
											}								
											if (!esNoHabil){
												if(t4821DAO.verificaRegistroSolicitudCompensacion(dbpool, 
														//(String) mapa.get("UUOOFIN"), 
														(String) mapa.get("userOrig"),
														Constantes.MOV_SOLICITUD_COMPENSACION,
														//fechaEval) == false){
														fechaEval) == true){
													mensajes.add("Ya existe solicitud de compensacion en seguimiento para la fecha: "+ fechaEval + " en el rango de fechas.");
													log.debug("Ya existe2 solicitud(es) de compensacion en seguimiento para la fecha: "+ fechaEval + " en el rango de fechas.");
												}else{
													log.debug("no existe2 solicitud de compensacion en seguimiento para fecha: " + fechaEval);
												}
											}											
										}else{
											log.debug("no existe fechaEval2:" + fechaEval);
										}											
									}
									log.debug("i aca2 :" + i);
									log.debug("diferencia3 aca :" + diferencia3);									
									i=i+(diferencia3-1);
									log.debug("i final aca3 :" + i);								
									dias = dias + (diferencia3-descuento);
									log.debug("entrada1.1(dias) :" + dias);
									descuentoTotal = descuentoTotal + descuento; //add ICR 21/12/2012 sumando total de descuentos para adm (feriados y fin sema) en rango de fechas (fechaIni y fechaFin)
									log.debug("descuentoTotal2: " + descuentoTotal); //add ICR 21/12/2012 sumando total de descuentos
									if (!regimenCol.equals("")){							//jquispe: ini(26/07/2013)
										if (Math.abs(Utiles.obtenerHorasDiferencia(turno1.getHoraIni(), turno1.getHoraFin()))<=8){//<=8 horas --- queda 8 horas																
												horas =  horas + Math.abs(Utiles.obtenerHorasDiferencia(turno1.getHoraIni(), turno1.getHoraFin()))*(diferencia3-descuento);
												if (log.isDebugEnabled()) log.debug("horas2(cas_noCas <= 8): " + horas);																											
										}else{																					
												horas =  horas + Math.abs(Utiles.obtenerHorasDiferencia(turno1.getHoraIni(), turno1.getHoraFin())-1)*(diferencia3-descuento);
												if (log.isDebugEnabled()) log.debug("horas2(cas_noCas > 8): " + horas);
										}								
									}														//jquispe: fin					
								}
							}else{
								log.debug("entrada2(diferencia2 <= 0)");
								descuento=0;
								log.debug("fechaSig :" + fechaSig);	
								if (fechaSig!=null && !fechaSig.equals("")){								
									log.debug("entro3 a validar extremos fechaIni o fechaFin(fechaSig): "+fechaSig);
									if(!turno1.isOperativo()){//adm
										log.debug("turno es adm");
										if ((paramDAO.findByFechaFeriado(dbpool,fechaSig) || Utiles.isWeekEnd(fechaSig))) {
											log.debug("dia3 es feriado o fin de semana(fechaSig): "+fechaSig);
											esNoHabil = true;
											descuento = 1;
											log.debug("descuento :" + descuento);
											descuentoTotal = descuentoTotal + descuento; //add ICR 21/12/2012 sumando total de descuentos para adm (feriados y fin sema) en rango de fechas (fechaIni y fechaFin)
											log.debug("descuentoTotal3: " + descuentoTotal); //add ICR 21/12/2012 sumando total de descuentos
										}
									}								
									if (!esNoHabil){
										if(t4821DAO.verificaRegistroSolicitudCompensacion(dbpool, 
												//(String) mapa.get("UUOOFIN"), 
												(String) mapa.get("userOrig"),
												Constantes.MOV_SOLICITUD_COMPENSACION,
												//fechaSig) == false){
												fechaSig) == true){
											mensajes.add("Ya existe solicitud de compensacion en seguimiento para la fecha: "+ fechaSig + " en el rango de fechas.");
											log.debug("Ya existe3 solicitud(es) de compensacion en seguimiento para la fecha: "+ fechaSig + " en el rango de fechas.");
										}else{
											log.debug("no existe3 solicitud de compensacion en seguimiento para fecha: " + fechaSig);
										}
									}								
								}else{
									log.debug("no existe fechaSig:" + fechaSig);
								}														
								if (!regimenCol.equals("")){
									if (descuento==0){								//jquispe: ini(26/07/2013)
										if (Math.abs(Utiles.obtenerHorasDiferencia(turno1.getHoraIni(), turno1.getHoraFin()))<=8){//<=8 horas --- queda 8 horas																	
												horas =  horas + Math.abs(Utiles.obtenerHorasDiferencia(turno1.getHoraIni(), turno1.getHoraFin()));
												if (log.isDebugEnabled()) log.debug("horas3(cas_noCas <= 8): " + horas);																												
										}else{																					
												horas =  horas + Math.abs(Utiles.obtenerHorasDiferencia(turno1.getHoraIni(), turno1.getHoraFin())-1);
												if (log.isDebugEnabled()) log.debug("horas3(cas_noCas > 8): " + horas);
										}											//jquispe: fin
										dias = dias + 1;
									}																	
								}							
								log.debug("entrada2(dias) :" + dias);																
							}
						}else{
							BeanTurnoTrabajo turno2 = tpDAO.joinWithT45ByCodPersFecha_Controlado_Oper2dias(dbpool, codPers, fechaSig);
							if (turno2 != null){
								log.debug("turno de 2 dias");
								log.debug("fechaSig :" + fechaSig);	
								if (fechaSig!=null && !fechaSig.equals("")){								
									if(t4821DAO.verificaRegistroSolicitudCompensacion(dbpool, 
											//(String) mapa.get("UUOOFIN"), 
											(String) mapa.get("userOrig"),
											Constantes.MOV_SOLICITUD_COMPENSACION,
											//fechaSig) == false){
											fechaSig) == true){
										mensajes.add("Ya existe solicitud de compensacion en seguimiento para la fecha: "+ fechaSig + " en el rango de fechas.");
										log.debug("Ya existe4 solicitud(es) de compensacion en seguimiento para la fecha: "+ fechaSig + " en el rango de fechas.");
									}else{
										log.debug("no existe4 solicitud de compensacion en seguimiento para fecha: " + fechaSig);
									}	
								}else{
									log.debug("no existe fechaSig2:" + fechaSig);
								}		
								if (!regimenCol.equals("")){					//jquispe: ini(26/07/2013)									
									if (Math.abs(Utiles.obtenerHorasDiferencia(turno2.getHoraIni(), turno2.getHoraFin()))<=8){//<=8 horas --- queda 8 horas																
											//horas =  horas + Math.abs(Utiles.obtenerHorasDiferencia(turno2.getHoraIni(), turno2.getHoraFin())); ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
										    horas =  horas + Math.abs(Utiles.obtenerHorasDiferencia(turno2.getHoraIni(), "24:00:00")); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
										    horas =  horas + Math.abs(Utiles.obtenerHorasDiferencia("00:00:00", turno2.getHoraFin())); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
											if (log.isDebugEnabled()) log.debug("horas4(cas_noCas <= 8): " + horas);																											
									}else{																					
											//horas =  horas + Math.abs(Utiles.obtenerHorasDiferencia(turno2.getHoraIni(), turno2.getHoraFin())-1); ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
											horas =  horas + Math.abs(Utiles.obtenerHorasDiferencia(turno2.getHoraIni(), "24:00:00")); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
											horas =  horas + Math.abs(Utiles.obtenerHorasDiferencia("00:00:00", turno2.getHoraFin())); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
											horas =  horas - 1;
											if (log.isDebugEnabled()) log.debug("horas4(cas_noCas > 8): " + horas);
									}											//jquispe: fin
									dias = dias + 1;																								
								}							
								log.debug("dias :" + dias);														
							}else{
								String fechaAntes = Utiles.dameFechaAnterior(fechaSig, 1);
								BeanTurnoTrabajo turno3 = tpDAO.joinWithT45ByCodPersFecha_Controlado_Oper2dias(dbpool, codPers, fechaAntes);
								//add icr
								if (turno3!=null){
									log.debug("turno de 2 dias(dia anterior): "+fechaAntes);
									log.debug("entro turno3");
									if (fechaAntes.compareTo(fechaIni)>=0 && fechaAntes.compareTo(fechaFin)<=0){
										log.debug("no suma dias ni horas");									
									}else{
										log.debug("fechaAntes :" + fechaAntes);	
										if (fechaAntes!=null && !fechaAntes.equals("")){
											if(t4821DAO.verificaRegistroSolicitudCompensacion(dbpool, 
													//(String) mapa.get("UUOOFIN"), 
													(String) mapa.get("userOrig"),
													Constantes.MOV_SOLICITUD_COMPENSACION,
													//fechaAntes) == false){
													fechaAntes) == true){
												mensajes.add("Ya existe solicitud de compensacion en seguimiento para la fecha: "+ fechaSig + " en el rango de fechas.");
												log.debug("Ya existe5 solicitud(es) de compensacion en seguimiento para la fecha: "+ fechaSig + " en el rango de fechas.");
											}else{
												log.debug("no existe5 solicitud de compensacion en seguimiento para fecha: " + fechaSig);
											}
										}else{
											log.debug("no existe5 fechaAntes:" + fechaAntes);
										}
										if (!regimenCol.equals("")){					//jquispe: ini(26/07/2013)									
											if (Math.abs(Utiles.obtenerHorasDiferencia(turno3.getHoraIni(), turno3.getHoraFin()))<=8){//<=8 horas --- queda 8 horas																	
													//horas =  horas + Math.abs(Utiles.obtenerHorasDiferencia(turno3.getHoraIni(), turno3.getHoraFin())); ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
													horas =  horas + Math.abs(Utiles.obtenerHorasDiferencia(turno3.getHoraIni(), "24:00:00")); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
												    horas =  horas + Math.abs(Utiles.obtenerHorasDiferencia("00:00:00", turno3.getHoraFin())); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
													if (log.isDebugEnabled()) log.debug("horas5(cas_noCas <= 8): " + horas);																												
											}else{																					
													//horas =  horas + Math.abs(Utiles.obtenerHorasDiferencia(turno3.getHoraIni(), turno3.getHoraFin())-1); ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
													horas =  horas + Math.abs(Utiles.obtenerHorasDiferencia(turno3.getHoraIni(), "24:00:00")); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
													horas =  horas + Math.abs(Utiles.obtenerHorasDiferencia("00:00:00", turno3.getHoraFin())); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
													horas =  horas - 1;
													if (log.isDebugEnabled()) log.debug("horas5(cas_noCas > 8): " + horas);
											}											//jquispe: fin
											dias = dias + 1;																								
										}									
										log.debug("dias :" + dias);																					
										log.debug("si suma dias y horas");	
									}								
								}
								//fin add
								else{
									log.debug("NO HAY TURNO PARA FECHA: "+fechaMostrar);
									mensajes.add("Fecha: "+fechaMostrar+ " no tiene turno o tiene turno R67 o no controlado - Modifique rango a compensar.");								
								}
							}
						}		
					}
					if (log.isDebugEnabled()) log.debug("termino for(esDiaHabil): "+esDiaHabil);
					//add ICR 21/12/2012 sumando total de descuentos
					if (log.isDebugEnabled()) log.debug("descuentoTotal FINAL: " + descuentoTotal); 
					if (log.isDebugEnabled()) log.debug("diferencia (fechaIni y fechaFin) FINAL: " + diferencia); 
					//if (descuentoTotal==diferencia){//add ICR 27/12/2012
					if (dias<=0){//add ICR 27/12/2012
						//if (log.isDebugEnabled()) log.debug("descuentoTotal==diferencia: "+descuentoTotal); //add ICR 27/12/2012
						if (log.isDebugEnabled()) log.debug("dias<=0: "+dias);//add ICR 27/12/2012
						mensajes.add("El rango de fechas entre el "+fechaIni+ " y " + fechaFin + " no tiene días hábiles - Modifique rango a compensar.");
						if (log.isDebugEnabled()) log.debug("El rango de fechas entre el "+fechaIni+ " y " + fechaFin + " no tiene días hábiles - Modifique rango a compensar."); 
					}
					//add ICR 21/12/2012 sumando total de descuentos
					
					//ICAPUNAY 07/06 PASE 2012-64 LABOR EXCEPCIONAL-MTM				
					if (esDiaHabil){					
						if (log.isDebugEnabled()) log.debug("entro a esDiaHabil: "+esDiaHabil);
						if (log.isDebugEnabled()) log.debug("validaLicencia(dias): "+dias);
						if (log.isDebugEnabled()) log.debug("validaLicencia(horas): "+horas);
						
						FechaBean dia = new FechaBean();
						String fechafinComp = dia.getFormatDate("dd/MM/yyyy");
						T4819DAO t4819DAO = new T4819DAO(sl.getDataSource("java:comp/env/jdbc/dcsp"));
				
						String fechainiComp = codigoDAO.findParamByCodTabCodigo(dbpool,Constantes.CODTAB_PARAMETROS_ASISTENCIA,Constantes.FECHA_INICIO_COMPENSACION);
						if (log.isDebugEnabled()) log.debug("fechainiComp: " +fechainiComp + " - fechafinComp: " +fechafinComp);
						
						//ICAPUNAY-PAS20155E230000052-ajuste comparacion fecha inicio con parametro fecha inicio compensacion para registro y aprobacion sol. compensacion 125
						Date feciniD= new FechaBean(fechaIni).getSQLDate();
						Date fecinicompD= new FechaBean(fechainiComp).getSQLDate();
			
						//if (fechaIni.compareTo(fechainiComp)>=0){
						if (feciniD.compareTo(fecinicompD)>=0){
						//FIN ICAPUNAY-PAS20155E230000052-ajuste comparacion fecha inicio con parametro fecha inicio compensacion para registro y aprobacion sol. compensacion 125
							
							pe.gob.sunat.sp.dao.T02DAO empleadoDAO = new pe.gob.sunat.sp.dao.T02DAO();		//ICR 08/05/2015-PAS20155E230000154-labor antigua  (autorizacion y compensacion) y labor nueva (sol. compensacion) mayor o igual fecha ingreso a sunat
							Map mapaEmpleado = new HashMap();
							Date fecha_ingreso;	
							mapaEmpleado = empleadoDAO.findEmpleado("java:comp/env/jdbc/dcsp",(String) mapa.get("userOrig"));	
							fecha_ingreso = (mapaEmpleado!=null && !mapaEmpleado.isEmpty())?((Date)mapaEmpleado.get("t02f_ingsun")):null;	
							mapa.put("fecha_ingreso", fecha_ingreso);
							
							if (log.isDebugEnabled()) log.debug("fechaIni: "+fechaIni+" mayor que fechainiComp: "+fechainiComp);
							mapa.put("cod_pers",(String) mapa.get("userOrig"));
							Integer saldo = t4819DAO.findSaldoLaborAutorizadas(mapa);
							if (log.isDebugEnabled()) log.debug("saldo: " + saldo);							
							//Calculamos la cantidad de minutos a compensar			
							dias = (int)horas * 60;
							if (log.isDebugEnabled()) log.debug("dias: "+dias);									
							int saldored = saldo.intValue();
							if (log.isDebugEnabled()) log.debug("saldored: "+saldored);	
							if (saldored < dias ){					
								mensajes.add("Usted no posee la cantidad de horas suficientes para compensar.");
							}
						}else{
							if (log.isDebugEnabled()) log.debug("fechaIni: "+fechaIni+" menor que fechainiComp: "+fechainiComp);
							mensajes.add("Fecha de inicio: "+fechaIni+" debe ser mayor que la fecha: "+fechainiComp);
						}											
					}//fin esDiaHabil //ICAPUNAY 07/06 PASE 2012-64 LABOR EXCEPCIONAL-MTM
				}								
			}//fin solicitud 125 de compensacion
			
			//PRAC-JCALLO
			Map prm = new HashMap();
			prm.put("cod_pers", codPers.trim());
			
			//24072008
			if ((fechaNac!=null) && (!fechaNac.trim().equals("")))
				prm.put("fechaIni", fechaNac.trim());
			else
				prm.put("fechaIni", fechaIni.trim());
			
			prm.put("fechaFin", fechaFin.trim());
			log.debug("vacacionDAO.findByCodPersFIniFFin(prm)... prm:"+prm);
			//PRAC-JCALLO
			//boolean bVacacion = vacacionDAO.findByCodPersFIniFFin(dbpool,codPers.trim(), fechaIni.trim(), fechaFin.trim());
			boolean bVacacion = vacacionDAO.findByCodPersFIniFFin(prm);
			
			if (bVacacion) {
				mensajes.add("El trabajador se encuentra de vacaciones durante esas fechas.");
			}
			
			//EBV 11/06/2012
			boolean bVacacion1 = vacacionDAO.findByCodPersFIniFFin49(prm);
			
			if (bVacacion1 && !(tipo.equals(Constantes.LICENCIA_MATRIMONIO)&& regimenCol.equals(Constantes.CODREL_REG1057))) { //jquispe licencia matrimonio
				mensajes.add("El trabajador tiene vacaciones programadas durante esas fechas.");
			}
			//prac-jcallo
			Map prms = new HashMap();
			prms.put("cod_pers", codPers);
			
			//24072008
			if ((fechaNac!=null) && (!fechaNac.trim().equals("")))
				prms.put("fecha1", fechaNac.trim());
			else
				prms.put("fecha1", fechaIni.trim());

			prms.put("fecha2", fechaFin);
			prms.put("numero", "");
			log.debug("licenciaDAO.findByCodPersFIniFFin(prms)... prms:"+prms);
			boolean bLicencia = licenciaDAO.findByCodPersFIniFFin(prms); //(dbpool,codPers, fechaIni, fechaFin, "");
			
			if (bLicencia) {
				mensajes.add("El trabajador posee una licencia durante esas fechas.");
			}
			
			ArrayList params = codigoDAO.findByCodTab(dbpool, "0", tipo, Constantes.CODTAB_DIAS_LICENCIA);

			if (!tipo.equals(Constantes.LICENCIA_NACIMIENTO)) {
				if (params != null && params.size() > 0) {

					int numDias = 0;
					//si la validacion es por dias habiles
					if (((String)tipoMov.get("ind_dias")).equals("1")) {
						numDias = facadeRemote.obtenerDiasHabilesDiferencia(dbpool,fechaIni,fechaFin) + 1;
					}
					else{
						numDias = Utiles.obtenerDiasDiferencia(fechaIni, fechaFin) + 1;
					}
					log.debug("Dias Habiles ? "+tipoMov.get("ind_dias"));
					log.debug("NumDias Diferencia : "+numDias);

					// verificamos q el numero de dias coincida con algunos de los parametros de dia
					boolean existe = false;
					for (int i = 0; i < params.size() && !existe; i++) {
						BeanT99 p = (BeanT99) params.get(i);
						int dias = 0;
						try {
							dias = Integer.parseInt(p.getT99descrip().trim());
						} catch (Exception e) {
						}
						existe = dias == numDias;
					}
					if (!existe) {
						mensajes.add("El rango de fechas ingresado no corresponde a los par&aacute;metros requeridos por la licencia.");
					}
				}//WERR-PAS20155E230300132
			}//WERR-PAS20155E230300132	
			//WERR-PAS20155E230300132
			if (tipo.trim().equals(Constantes.LICENCIA_SINGOCEHABER_HASTA3DIAS)) {
				log.debug("llego a la validacion por 3 dias habiles");
				int diferenciaDias = 0;
				diferenciaDias = Utiles.obtenerDiasDiferencia(fechaIni, fechaFin)+1;
					if(diferenciaDias>3){
						mensajes.add("La solicitud tiene "+ diferenciaDias +" días calendarios solicitados, favor modificar la fecha fin ya que solo puede registrar máximo 3 días.");
					}
			}
			// end WERR-PAS20155E230300132
			
			//verificamos los dias de validacion (qvalida)
			if (Integer.parseInt(qvalida)>0){
				mapa.put("tipoMov",tipo);
				int histoLic = licenciaDAO.findCantidadByTipo(mapa);
				if (Integer.parseInt(qvalida)<=histoLic) {
					mensajes.add("El trabajador posee "+histoLic+" licencia(s) registradas. " +
							"El m&aacute;ximo permitido es "+qvalida+".");
				}	
			}												
			
		} catch (Exception e) {
			log.error(e);
			log.error("Exception en validaLicencia: " + e.getMessage(), e);

			mensajes.add(e.toString());
		}
		if (log.isDebugEnabled()) log.debug("mensajes finales: "+mensajes);//ICAPUNAY 09/02/2012-POR PRUEBAS DESARROLLO
		return mensajes;
	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList validaMarcaciones(HashMap mapa, HashMap tipoMov) 
		throws IncompleteConversationalState,
			RemoteException {

		ArrayList mensajes = new ArrayList();
		try {

			String dbpool = (String) mapa.get("dbpool");
			String codPers = (String) mapa.get("userOrig");			
			String fechaMarca = (String) mapa.get("fechaMarca");
			String txtHora = (String) mapa.get("txtHora");
			String tipo = (String) tipoMov.get("mov");			

			T1275DAO dao = new T1275DAO();
			//PRAC-JCALLO
			T1271DAO aDAO = new T1271DAO(dbpool);	

			
			//JRR - 27/05/2009 -Propuesta de AutomatizaciÃ³n NÂ° 001-2009-2F3100
			ArrayList lista = dao.findByCodPersFecha(dbpool,codPers,fechaMarca);
			if (lista==null || lista.size()==0){
				mensajes.add("El trabajador no posee ninguna marcaciÃ³n para el "+fechaMarca+".");
			}
/*						
			else if (lista!=null && lista.size()>0 && (lista.size()%2==0)){
				mensajes.add("El trabajador posee un numero par ("+lista.size()+") de marcaciones para el "+fechaMarca+".");
			}
*/			
			
			if (tipo.trim().equals(Constantes.MOV_ANULACION_MARCA)){
				
				if (!dao.findMarca(dbpool,codPers,fechaMarca,txtHora)){
					mensajes.add("La marcaciÃ³n de las "+txtHora+" no ha sido registrada.");			
				}	
				//prac-jcallo
				Map params = new HashMap();
				params.put("cod_pers", codPers);
				params.put("fecha", fechaMarca);
				params.put("hora", txtHora);
				log.debug("aDAO.findMarcacionCalificada(params)... params:"+params);
				//if (aDAO.findMarcacionCalificada(dbpool,codPers,fechaMarca,txtHora){
				if (aDAO.findMarcacionCalificada(params)){
					mensajes.add("La marcaciÃ³n de las "+txtHora+" ya ha sido calificada.");			
				}
				
			}
			else{
				
				T1270DAO turnoDAO = new T1270DAO();
//				T1608DAO t1608DAO = new T1608DAO(dbpool);
				BeanTurnoTrabajo turno = turnoDAO.joinWithT45ByCodFecha(dbpool,codPers, fechaMarca);
				//JRR - 20/05/2009
/*				Map params = new HashMap();
				params.put("cod_personal", codPers);
				params.put("fcompensa", fechaMarca);
*/				
				if (turno != null) {
/*					
					//JRR - 27/05/2009 -Propuesta de AutomatizaciÃ³n NÂ° 001-2009-2F3100
					T99DAO codigoDAO = new T99DAO();
					ArrayList fechaLevanta = codigoDAO.findByCodTab(dbpool,
							"1", fechaMarca, Constantes.CODTAB_FECHA_LEVANTA);
							
					
					//EBV Incluye validacion debido al Requerimiento de AutomatizaciÃ³n
					//NÂ° 00027 - 2007 - 2F3100
					//03/09/2007
					log.debug("FechaLevanta "+ fechaLevanta);
					if (fechaLevanta==null || fechaLevanta.size()==0) {
						
					float dif1 = Utiles.obtenerHorasDiferencia(turno.getHoraIni(), txtHora+":00");
					float dif2 = Utiles.obtenerHorasDiferencia(txtHora+":00", turno.getHoraFin());
//					en caso el turno sea onomastico se debe trabajar 1 hora mas
					//dif2+=t1608DAO.findHorasCompensa(dbpool,codPers,fechaMarca);
					//JRR - 20/05/2009
					String qhoras = t1608DAO.findHorasCompensa(params).get("qhoras").toString().trim(); 
					dif2+=Integer.parseInt(qhoras);

					if (dif1 < 0) {
						mensajes.add("La hora ingresada es anterior a la hora inicio ("+turno.getHoraIni()+") de su turno de trabajo.");
					}else if (dif2 < 0) {
						mensajes.add("La hora ingresada es posterior a la hora fin ("+turno.getHoraFin()+") de su turno de trabajo.");
					}
					
					}
*/					
				} else{
					mensajes.add("Ud. no posee un turno asignado para el "+fechaMarca+".");
				}
				
				if (dao.findMarca(dbpool,codPers,fechaMarca,txtHora)){
					mensajes.add("La marcacion de las "+txtHora+" ya ha sido registrada.");			
				}	
			}
			
		} catch (Exception e) {
			log.error(e);			
			mensajes.add(e.toString());
		}
		return mensajes;
	}	
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public ArrayList registrarMarcacion(HashMap mapa, HashMap tipoMov) 
		throws IncompleteConversationalState,
			RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		ArrayList mensajes = new ArrayList();
		try {

			String codPers = (String) mapa.get("userOrig");			
			String fechaMarca = (String) mapa.get("fechaMarca");
			String txtHora = (String) mapa.get("txtHora");
			String usuario = (String) mapa.get("usuario");

			mensajes = validaMarcaciones(mapa, tipoMov);
			if (mensajes.isEmpty()) {

				T1275CMPHome cmpHome = (T1275CMPHome) sl.getLocalHome(T1275CMPHome.JNDI_NAME);
				
				try{
					T1275CMPLocal cmpLocal = cmpHome.create(
							codPers,
							new BeanFechaHora(fechaMarca).getSQLDate(),
							txtHora+":00",
							Constantes.RELOJ_MANUAL
						); 
						
					cmpLocal.setCuserCrea(usuario);	
					cmpLocal.setFcreacion(new Timestamp(System.currentTimeMillis()));
					cmpLocal.setEstado(Constantes.ACTIVO);
				}
				catch(Exception e){
					mensajes.add("Error al insertar nueva marcacÃ­on de las "+txtHora+" horas.");
				}
			}
			
		} catch (Exception e) {
			log.error(e);
			mensajes.add(e.toString());
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
	public ArrayList anularMarcacion(HashMap mapa, HashMap tipoMov) 
		throws IncompleteConversationalState,
			RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		ArrayList mensajes = new ArrayList();
		try {

			String dbpool = (String) mapa.get("dbpool");
			String codPers = (String) mapa.get("userOrig");			
			String fechaMarca = (String) mapa.get("fechaMarca");
			String txtHora = (String) mapa.get("txtHora");
			String usuario = (String) mapa.get("usuario");

			mensajes = validaMarcaciones(mapa, tipoMov);
			if (mensajes.isEmpty()) {

				T1275CMPHome cmpHome = (T1275CMPHome) sl.getLocalHome(T1275CMPHome.JNDI_NAME);
				
				try{
					
					T1275DAO dao = new T1275DAO();
					//BeanMarcacion marca = dao.findByCodPersFechaHora(dbpool,codPers,fechaMarca,txtHora+":00");
					//BeanMarcacion marca = dao.findByCodPersFechaHora(dbpool,codPers,fechaMarca,txtHora);
					BeanMarcacion marca = dao.findByCodPersFechaHoraAnula(dbpool,codPers,fechaMarca,txtHora); //WERR-PAS20155E230300132
					T1275CMPLocal cmpLocal = cmpHome.findByPrimaryKey(
							new T1275CMPPK(
							codPers,
							new BeanFechaHora(fechaMarca).getSQLDate(),
							txtHora,
							marca.getReloj()
						)); 
						
					cmpLocal.setEstado(Constantes.INACTIVO);										
					cmpLocal.setFmod(new Timestamp(System.currentTimeMillis()));
					cmpLocal.setCuserMod(usuario);
					
					//Se anula la calificacion asociada a la marcacion anulada
					DataSource ds = sl.getDataSource(dbpool);
					//pe.gob.sunat.rrhh.asistencia.dao.T1271DAO t1271dao = new  pe.gob.sunat.rrhh.asistencia.dao.T1271DAO(ds);
					T1271DAO t1271dao = new  T1271DAO(ds);
					t1271dao.deleteAsistenciaAnulacion(codPers,fechaMarca,txtHora);
				}
				catch(Exception e){
					mensajes.add("Error al anular marcacÃ­on de las "+txtHora+" horas.");
				}
			}
			
		} catch (Exception e) {
			log.error(e);
			mensajes.add(e.toString());
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		return mensajes;
	}	

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList validaVentaVacaciones(String dbpool, String codPers,
			String anno, String dias, String tipo) throws IncompleteConversationalState,
			RemoteException {

		ArrayList mensajes = new ArrayList();
		try {

			T1281DAO vacDAO = new T1281DAO();
			
			//boolean poseeVenta = vacDAO.findVentaVacacional(dbpool, codPers, anno);
			//if (poseeVenta) {
			//	mensajes.add("El trabajador ya ha efectuado una venta vacacional para el a&ntilde;o "+ anno +".");
			//}
			T99DAO dao = new T99DAO();
            HashMap HMmaximoVenta= dao.diasVentaVacByTipo(dbpool);			
			int maxVenta=0;			
			if(HMmaximoVenta!=null && HMmaximoVenta.get("t99descrip")!=null)
			maxVenta=Integer.parseInt(HMmaximoVenta.get("t99descrip").toString());
			
			boolean poseeSaldo = vacDAO.findSaldoVacacional(dbpool, codPers, anno, dias);
			
			if (!poseeSaldo) {
				mensajes.add("El trabajador no posee saldo vacacional suficiente para el a&ntilde;o "+ anno +". (Considerar tambi&eacute;n que M&aacute;ximo son "+maxVenta+" dias de venta por a&ntilde;o)");
			}
			/**********************************/
			else {
				int intdias = Integer.parseInt(dias);
//				log.debug("codPers: " + codPers);
//				log.debug("intdias: " + dias);
//				log.debug("anno: " + anno);
				
				mensajes = this.verificarSolicitudesPendientesVacaciones(mensajes, dbpool,
						codPers, intdias, anno);
			}
			/**********************************/

		} catch (Exception e) {
			log.error(e);
			mensajes.add(e.toString());
		}
		return mensajes;
	}


/* COMENTADO EL METODO registrarVentaVacaciones DADO QUE NO LO UTILIZA COMSA- 07/06/2010 */	
	/* *
	 * Metodo que se encarga de validar el saldo en la Aprobacion de las Solicitudes de Venta de Vacaciones
	 * @param hm (String dbpool, String codPers, String anno, String dias)
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */	
/*	public List validaVentaVacacionesAprobacion(Map hm) throws 
			RemoteException {

		List mensajes = new ArrayList();
		try {
			T1281DAO vacDAO = new T1281DAO();
			
			if(log.isDebugEnabled()) log.debug("Dentro de validaVentaVacacionesAprobacion HM: " + hm);
			String dbpool = hm.get("dbpool").toString().trim();
			String codPers = hm.get("codPers").toString().trim();
			String anno = hm.get("anno").toString().trim();
			String dias = hm.get("diasVenta").toString().trim();
			
			boolean poseeSaldo = vacDAO.findSaldoVacacional(dbpool,	codPers, anno, dias);
			
			if(log.isDebugEnabled()) log.debug("Posee saldo: " + poseeSaldo);
			
			if (!poseeSaldo) {
				mensajes.add("El trabajador no posee saldo vacacional suficiente para el a&ntilde;o "+ hm.get("anno").toString() +". (Considerar tambi&eacute;n que M&aacute;ximo son 15 dias de venta por a&ntilde;o)");
			}

		} catch (Exception e) {
			log.error(e);
			mensajes.add(e.toString());
		}
		//if(log.isDebugEnabled()) log.debug("validaVentaVacacionesAprobacion devuelve:" + mensajes + "|");
		return mensajes;
	}
*/	
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="RequiresNew"
	 */
	public ArrayList registraSolicitud(HashMap mapa, String tipo,
			String usuario) throws IncompleteConversationalState,
			RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		ArrayList mensajes = new ArrayList();
		try {
			log.debug("INGRESO A registraSolicitud(mapa): "+mapa);
			String[] diaXCompensar = new String[1];//ICR PAS20124E550000064 Labor Excepcional y Compensaciones
			DataSource dsOracle = sl.getDataSource("java:comp/env/pool_oracle");
			DataSource ds = sl.getDataSource("java:comp/env/jdbc/dcsp"); //agregado wrr
			
			String dbpool = (String) mapa.get("dbpool");
			
			T02DAO personalDAO = new T02DAO();
			T1277DAO soliDAO = new T1277DAO();
			T1455DAO seguiDAO = new T1455DAO(dbpool);
			T1279DAO movDAO = new T1279DAO();
			CorreoDAO correoDAO = new CorreoDAO();	

			//MTM
			T4821DAO t4821dao = new T4821DAO(dbpool);
			T1282DAO t1282dao = new T1282DAO(sl.getDataSource("java:comp/env/jdbc/dcsp"));
			T1270DAO tpDAO = new T1270DAO();//add icr 18/11
			T01DAO paramDAO = new T01DAO();//add icr 18/11

			//PRAC-ASANCHEZ 09/06/2009
			AsistenciaFacadeHome asisHome = (AsistenciaFacadeHome) ServiceLocator.getInstance().getRemoteHome(
					AsistenciaFacadeHome.JNDI_NAME,AsistenciaFacadeHome.class);
			AsistenciaFacadeRemote asisRemote = asisHome.create();
			Map mov = new HashMap();
			String ind;
			//
			
			//T1277CMPHome cmpHome = (T1277CMPHome) initCtx.lookup(T1277CMPHome.JNDI_NAME);
			//T1455CMPHome segHome = (T1455CMPHome) initCtx.lookup(T1455CMPHome.JNDI_NAME);

			//registramos la solicitud
			String annoActual = Utiles.obtenerAnhoActual();
			
			OracleSequenceDAO seqDAO = new OracleSequenceDAO();
			String numero = seqDAO.getSequenceDS(dsOracle,Constantes.SEQ_SOLICITUD);

			String fechaIni = (String) mapa.get("fechaIni");
			String fechaFin = (String) mapa.get("fechaFin");
			String receptorSolic = mapa.get("receptorSolic")!=null?(String) mapa.get("receptorSolic"):""; //ICAPUNAY - PAS20165E230300128 - registro solic. Delegado (delegado pertenece a uo donde es delegado)  07/06/16

/*          JRR - 07/04/2009 - Ya lo envio desde validaLicencia			
			if (tipo.equals(Constantes.LICENCIA_NACIMIENTO)){
				log.debug("registraSolicitud - mapa: " + mapa);
				fechaIni = (String) mapa.get("fechaNac");
				fechaFin = (String) mapa.get("fechaNac");
			}
*/			
			if (tipo.equals(Constantes.MOV_OMISION_MARCA) || tipo.equals(Constantes.MOV_ANULACION_MARCA)){
				fechaIni = (String) mapa.get("fechaMarca");
				fechaFin = (String) mapa.get("fechaMarca");
			}
			
			//MTM
	        int numDias;
			if (tipo.equals(Constantes.MOV_LABOR_EXCEPCIONAL) || tipo.equals(Constantes.MOV_REINTEGRO_POR_DESCUENTO_ASISTENCIA)|| tipo.equals(Constantes.MOV_REINTEGRO_POR_DESCUENTO_SUBSIDIO)) { //PAS20171U230200001 - solicitud de reintegro  
	        	numDias = 0;	
			} else {
				if(tipo.equals(Constantes.VACACION_POSTERGADA)) //dtara
				{
					numDias=0; if(log.isDebugEnabled())log.debug("Paso1: " + tipo);
				}else{
					numDias = Utiles.obtenerDiasDiferencia(fechaIni, fechaFin) + 1;
				}
	        }
			//int numDias = Utiles.obtenerDiasDiferencia(fechaIni, fechaFin) + 1;
			
			//PRAC-ASANCHEZ 10/07/2009
			mov = movDAO.findByMov(dbpool,tipo);
			ind = (String)mov.get("ind_dias"); //si ind es 1 solo se cuentan dias habiles, si es 0: dias por igual
			if(//(tipo.trim().equals(Constantes.LICENCIA_NACIMIENTO)|| //JRR
				((tipo.trim().equals(Constantes.LICENCIA_MATRIMONIO) && !Constantes.CODREL_REG1057.equals(mapa.get("regimenCol")))||
				tipo.trim().equals(Constantes.FALLECIMIENTO_FAMILIAR)) && ind.equals(Constantes.ACTIVO)){
				numDias = asisRemote.obtenerDiasHabilesDiferencia(dbpool, fechaIni, fechaFin) + 1;
				if(log.isDebugEnabled())log.debug("fechaIni: " + fechaIni);
				if(log.isDebugEnabled())log.debug("fechaFin: " + fechaFin);
				if(log.isDebugEnabled())log.debug("numDias: " + numDias);
			}
			//
			

			//JRR - 20/11/2009
			pe.gob.sunat.rrhh.dao.T99DAO t99dao = new pe.gob.sunat.rrhh.dao.T99DAO(sl.getDataSource("java:comp/env/jdbc/dcsp"));
			Map mapaT99 = new HashMap();
			Map datosAux = new HashMap();
			datosAux.put("t99cod_tab", constantes.leePropiedad("CODTAB_DIAS_LICENCIA"));
			datosAux.put("t99tip_desc", constantes.leePropiedad("T99DETALLE"));
			datosAux.put("t99estado", constantes.leePropiedad("ACTIVO"));
			datosAux.put("t99codigo", tipo);			
			if (tipo.equals(Constantes.LICENCIA_NACIMIENTO)){
				mapaT99 = t99dao.findParamByCodTabCodigo(datosAux);
				if (mapaT99 != null) 
					numDias = Integer.parseInt(mapaT99.get("t99descrip").toString().trim());
			}	
			//			

				
			if (tipo.equals(Constantes.VACACION) ||
					tipo.equals(Constantes.VACACION_VENTA) ){
				numDias = Integer.parseInt((String) mapa.get("numDias"));
				
				/******** JRR 05/12/2008 **********/
				mapa.put("est_id", constantes.leePropiedad("PROGRAMACION_EN_SOLICITUD"));
				mapa.put("comentario","");
				/**********************************/
				
			}
			if (tipo.equals(Constantes.REPROGRAMACION_VACACION) ||
				tipo.equals(Constantes.VACACION_POSTERGADA) ){
								
				//fechaIni = (String) mapa.get("nvaFechaIni");
				//fechaFin = (String) mapa.get("nvaFechaFin");
				//numDias = Integer.parseInt((String) mapa.get("numDias"));
				fechaIni = "14/03/2018";
				fechaFin = "20/03/2018";
				numDias = 7;
			}
			
			//EBV - 13/03/2006 Podrias Cambiar la UO a la UO de RRHH y luego cambiar usuario destino y haber que hace.
			
			//Probando Inicio
			HashMap pp = new HashMap();
			ParamBean prmAprobUnicoRRHH = t99dao.buscar(new String []{"522"}, ds, tipo);// PAS20165E230300184 werr
			if(prmAprobUnicoRRHH.getCodigo() != null ){ // PAS20165E230300184 werr
				pp = personalDAO.findEmpleadoLicEnf(dbpool, (String) mapa.get("userOrig"));
				log.debug("MAPA PROBANDO PP " + pp);
				T1480DAO flujoDAO = new T1480DAO();
				pp.put("codUO",(String)pp.get("t02cod_uorg"));
				pp.put("instancia","4");
				pp.put("dbpool",dbpool);
				pp.put("mov",tipo);
				log.debug("MAPA PROBANDO PP2 " + pp);
				ArrayList datospp = flujoDAO.findAprobadores(pp);
				HashMap datoshash = (HashMap) datospp.get(0);
				log.debug("MAPA PROBANDO datoshash " + datoshash);
				mapa.put("codUO",(String)pp.get("codUO"));
				mapa.put("userDest", datoshash.get("cod_pers_ori"));
				log.debug("MAPA PROBANDO " + mapa);
			}
			// Fin Probando
			

			if (log.isDebugEnabled()) {
				log.debug("annoActual: " + annoActual);
				log.debug("numero: " + numero);
				log.debug("tipo: " + tipo);
				log.debug("fechaIni: " + fechaIni);
				log.debug("fechaFin: " + fechaFin);
				log.debug("numDias: " + numDias);
				log.debug("usuario: " + usuario);
				log.debug("MAPA insertRegistroSolicitud: " + mapa);
			}
									

			//JRR - 10/11/2009 - Lic Nac Hijo
			boolean solicitud = false;

			if(tipo.trim().equals(Constantes.LICENCIA_NACIMIENTO)) {
				
				FechaBean fb = new FechaBean();
				FechaBean fb1 = new FechaBean(fechaIni);
				FechaBean fb2 = new FechaBean(fechaFin);
				FechaBean fb3 = new FechaBean(mapa.get("fNacimiento")!=null?mapa.get("fNacimiento").toString():"");
				FechaBean fb4 = new FechaBean(mapa.get("fAlta")!=null?mapa.get("fAlta").toString():"");
				
				Map mapAux = new HashMap();
				mapAux.put("anno", annoActual);
				mapAux.put("numero", new Integer(numero));
				mapAux.put("u_organ", mapa.get("UUOOFIN"));
				mapAux.put("cod_pers", mapa.get("userOrig"));
				mapAux.put("asunto", mapa.get("asunto"));
				mapAux.put("fecha", fb.getTimestamp());
				mapAux.put("cargo", mapa.get("codCate"));
				mapAux.put("licencia", tipo);
				mapAux.put("ffinicio", fb1.getTimestamp());
				mapAux.put("ffin", fb2.getTimestamp());
				
				mapAux.put("fecNac", fb3.getSQLDate());
				mapAux.put("fecAlta", fb4.getSQLDate());
				
				mapAux.put("dias", String.valueOf(numDias));
				mapAux.put("anno_vac", mapa.get("annoVac") != null ? (String) mapa.get("annoVac") : "");
				mapAux.put("est_id", Constantes.ACTIVO);
				mapAux.put("seguim_act", "0");
				mapAux.put("observ", mapa.get("txtObs"));
				mapAux.put("cod_crea", mapa.get("codCreador"));
				mapAux.put("fcreacion", fb.getTimestamp());
				mapAux.put("cuser_crea", usuario);
				
				pe.gob.sunat.rrhh.asistencia.dao.T1277DAO t1277dao = new pe.gob.sunat.rrhh.asistencia.dao.T1277DAO(dbpool);
				solicitud = t1277dao.insertarSolicitud(mapAux);
				
			} 
			//MTM-ICR
			else {
				log.debug("inserta a t1277");
				if(!tipo.equals(Constantes.MOV_LABOR_EXCEPCIONAL)){					
					//add icr 18/11
					if (tipo.equals(Constantes.MOV_SOLICITUD_COMPENSACION)){
						log.debug("insertando en t1277 por compensacion 125");						
						//int descuento = 0;
						int diferencia = Utiles.obtenerDiasDiferencia(fechaIni, fechaFin)+1;
						int diferencia2 = 0;
						int diferencia3 = 0;
						boolean compensar = true;
						numDias = 0;
						String fechaEval="";//adicionado
						diaXCompensar = new String[diferencia+1];//adicionado
						//buscar si en el rango de fecha existe un turno no asignado o si tiene un turno operativo
						for (int i = 0; i < diferencia; i++) {
							log.debug("diferencia: " + diferencia);
							log.debug("i: " + i);
							String fechaSig = Utiles.dameFechaSiguiente(fechaIni, i);	
							log.debug("fechaSig: " + fechaSig);
							BeanTurnoTrabajo turno1 = tpDAO.joinWithT45ByCodPersFecha_Controlado_OperAdm1dia(dbpool,(String) mapa.get("userOrig"), fechaSig);
							if (turno1 != null){
								log.debug("turno de 1 dia");
								Date fechaDate = new FechaBean(turno1.getFechaFin()).getSQLDate();								
								log.debug("finTurno: " + fechaDate.toString());								
								diferencia2 = Utiles.obtenerDiasDiferencia(fechaSig,new FechaBean(fechaDate.toString(),"yyyy-MM-dd").getFormatDate("dd/MM/yyyy").toString())+1;
								log.debug("diferencia2: " + diferencia2);
								diferencia3 = diferencia - i;
								log.debug("diferencia3: " + diferencia3);								
								if(diferencia2 > 0){
									log.debug("entrada1(diferencia2 > 0)");
									if (diferencia3 > diferencia2){
										log.debug("validaLicencia entro(diferencia3>diferencia2)");										
										for ( int j = i;j<i+diferencia2;j++){
											compensar = true;
											log.debug("compensar: " + compensar);
											log.debug("j: "+j);
											log.debug("i: "+i);									
											fechaEval = Utiles.dameFechaSiguiente(fechaIni,j);									
											log.debug("fechaEval :" + fechaEval);
											if (fechaEval!=null && !fechaEval.equals("")){											
												if(!turno1.isOperativo()){//adm
													log.debug("turno es adm");
													if ((paramDAO.findByFechaFeriado(dbpool,fechaEval) || Utiles.isWeekEnd(fechaEval))) {
														log.debug("dia es feriado o fin de semana(fechaEval): "+fechaEval);													
														compensar = false;													
													}
												}																						
											}else{
												log.debug("no existe fechaEval: " + fechaEval);
											}
											if(compensar){
												numDias = numDias + 1;												
												diaXCompensar[numDias] =  fechaEval;
												log.debug("numDias: " + numDias);	
												log.debug("diaXCompensar["+numDias+"]: " + fechaEval);
											}
										}
										i=i+(diferencia2-1);										
										log.debug("diferencia2 aca :" + diferencia2);
										log.debug("i final aca : " + i);									
										log.debug("numDias final aca: " + numDias);										
									}else{
										log.debug("validaLicencia entro(diferencia3<=diferencia2)");									
										//add 10/12
										for ( int j = i;j<i+diferencia3;j++){
											compensar = true;
											log.debug("compensar: " + compensar);
											log.debug("j: "+j);
											log.debug("i: "+i);									
											fechaEval = Utiles.dameFechaSiguiente(fechaIni,j);									
											log.debug("fechaEval :" + fechaEval);
											if (fechaEval!=null && !fechaEval.equals("")){											
												if(!turno1.isOperativo()){//adm
													log.debug("turno es adm");
													if ((paramDAO.findByFechaFeriado(dbpool,fechaEval) || Utiles.isWeekEnd(fechaEval))) {
														log.debug("dia2 es feriado o fin de semana(fechaEval): "+fechaEval);													
														compensar = false;													
													}
												}																						
											}else{
												log.debug("no existe fechaEval: " + fechaEval);
											}
											if(compensar){
												numDias = numDias + 1;											
												diaXCompensar[numDias] =  fechaEval;
												log.debug("numDias: " + numDias);	
												log.debug("diaXCompensar2["+numDias+"]: " + fechaEval);
											}
										}
										//fin add 10/12
										i=i+(diferencia3-1);					
										log.debug("diferencia3 aca :" + diferencia3);
										log.debug("i final2 aca : " + i);										
										log.debug("numDias final2 aca: " + numDias);	
									}
								}else{
									log.debug("entrada2(diferencia2 <= 0)");									
									compensar = true;
									log.debug("fechaSig :" + fechaSig);	
									if (fechaSig!=null && !fechaSig.equals("")){								
										log.debug("entro3 a validar extremos fechaIni o fechaFin(fechaSig): "+fechaSig);
										if(!turno1.isOperativo()){//adm
											log.debug("turno es adm");
											if ((paramDAO.findByFechaFeriado(dbpool,fechaSig) || Utiles.isWeekEnd(fechaSig))) {
												log.debug("dia3 es feriado o fin de semana(fechaSig): "+fechaSig);											
												compensar = false;											
											}
										}																	
									}else{
										log.debug("no existe fechaSig:" + fechaSig);
									}	
									if (compensar){
										numDias = numDias + 1;										
										diaXCompensar[numDias] =  fechaSig;
										log.debug("numDias final3 aca: " + numDias);
										log.debug("diaXCompensar3["+numDias+"]: " + fechaSig);
									}																	
								}
							}else{
								log.debug("ENTRO ESTE BLOQUE");
								BeanTurnoTrabajo turno2 = tpDAO.joinWithT45ByCodPersFecha_Controlado_Oper2dias(dbpool, (String) mapa.get("userOrig"), fechaSig);
								if (turno2 != null){
									log.debug("turno de 2 dias");									
									log.debug("fechaSig :" + fechaSig);	
									numDias = numDias + 1;								
									diaXCompensar[numDias] =  fechaSig;
									log.debug("numDias final4 aca: " + numDias);
									log.debug("diaXCompensar4["+numDias+"]: " + fechaSig);
								}else{
									String fechaAntes = Utiles.dameFechaAnterior(fechaSig, 1);
									BeanTurnoTrabajo turno3 = tpDAO.joinWithT45ByCodPersFecha_Controlado_Oper2dias(dbpool, (String) mapa.get("userOrig"), fechaAntes);									
									if (turno3!=null){
										log.debug("turno de 2 dias(dia anterior): "+fechaAntes);
										if (fechaAntes.compareTo(fechaIni)>=0 && fechaAntes.compareTo(fechaFin)<=0){
											log.debug("NO SUMA DIAS NI HORAS");									
										}else{	
											log.debug("SI SUMA DIAS Y HORAS");
											log.debug("fechaAntes :" + fechaAntes);	
											numDias = numDias + 1;																											
											diaXCompensar[numDias] =  fechaAntes;
											log.debug("numDias final5 aca: " + numDias);
											log.debug("diaXCompensar5["+numDias+"]: " + fechaAntes);
										}								
									}												
								}
							}		
						}
						//fin for						
					}
					log.debug("fin insertando en t1277 por compensacion 125(numDias): "+numDias);
					//fin add compensacion
					
					log.debug("numDias antes de insertar t1277: "+numDias);
					solicitud = soliDAO.insertRegistroSolicitud(dbpool,
							annoActual, 
							new Integer(numero),						
							(String) mapa.get("UUOOFIN"),
							(String) mapa.get("userOrig"),
							(String) mapa.get("asunto"),
							(String) mapa.get("codCate"), 
							tipo, 
							fechaIni,
							fechaFin,
							numDias,
							mapa.get("annoVac") != null ? (String) mapa.get("annoVac") : "", 
							Constantes.ACTIVO,
							(String) mapa.get("txtObs"), 
							(String) mapa.get("codCreador"), 
							usuario	);
					log.debug("insertï¿½ solicitud?: "+solicitud);
					
					//add icr 07/12
					if (solicitud) {
						try{
							if (tipo.equals(Constantes.MOV_SOLICITUD_COMPENSACION) ){
								if (log.isDebugEnabled()) log.debug("insertando en t4821solicituddet por compensacion 125");				
								T4821DAO t4821DAO = new T4821DAO(sl.getDataSource("java:comp/env/jdbc/dgsp"));								
								log.debug("antes de insertar t4821(numDias): "+numDias);						
								String fechaEval="";
								boolean insertoT4821=false;								
								for (int m=1;m<=numDias;m++){								
									fechaEval = diaXCompensar[m];								
									if (log.isDebugEnabled()) log.debug("m: "+ m);
									if (log.isDebugEnabled()) log.debug("fechaEval: "+ fechaEval);					
									insertoT4821=t4821DAO.insertRegistroSolicitudDetLabor(dbpool, annoActual, new Integer(numero),
											mapa.get("UUOOFIN").toString(), mapa.get("userOrig").toString(), Constantes.MOV_SOLICITUD_COMPENSACION,										
											m,fechaEval, null, null, mapa.get("txtObs").toString(), usuario, null);
									log.debug("insertoT4821: "+insertoT4821+ " para fecha: "+fechaEval);
								}
								log.debug("fin insertar t4821");	
								//fin icr								
							}else if(tipo.equals(Constantes.VACACION_POSTERGADA)){
								if(log.isDebugEnabled()) log.debug("Insertando detalle de reprogramación en t4821solicituddet");
								T4821DAO t4821DAO = new T4821DAO(sl.getDataSource("java:comp/env/jdbc/dgsp"));								
								log.debug("antes de insertar t4821(numDias): "+numDias);						
								String fechaEval="";
								boolean insertoT4821=false;
								
								String reprogramadas=(String)mapa.get("reprogramadas");
								if(log.isDebugEnabled()) log.debug("Reprogramaciones1:"+reprogramadas);
								String programadas=(String)mapa.get("programadasSel");
								if(log.isDebugEnabled()) log.debug("Programaciones1:"+programadas);
								
								String[] prog1=reprogramadas.split("&");
								log.debug("NumRegistrosRep:"+reprogramadas.length());
								for (int m=1;m<=prog1.length;m++){
									String[] datosRep=prog1[m-1].split("-");				
									insertoT4821=t4821DAO.insertRegistroSolicitudDetLabor(dbpool,datosRep[0] , new Integer(numero),
											mapa.get("UUOOFIN").toString(), mapa.get("userOrig").toString(), Constantes.VACACION_POSTERGADA,										
											m,datosRep[2], datosRep[3], null, datosRep[1], usuario, null);
									log.debug("insertoT4821: "+insertoT4821+ " para fecha: "+fechaEval);
								}
								log.debug("fin insertar t4821");	
							}
						} catch (Exception e) {
							log.error(e);
							beanM.setMensajeerror(e.getMessage());
							beanM.setMensajesol("No se pudo insertar las fechas de compensacion en la t4821DAO.");
							throw new IncompleteConversationalState(beanM);
						}
					}
					//fin add icr 07/12
					
				}else{//labor excepcional					

					solicitud = soliDAO.insertRegistroSolicitud(dbpool,
							annoActual, 
							new Integer(numero),							
							(String) mapa.get("UUOOFIN"),
							(String) mapa.get("userOrig"),
							(String) mapa.get("asunto"),
							(String) mapa.get("codCate"), 
							tipo,
							new FechaBean().getFormatDate("dd/MM/yyyy"),
							new FechaBean().getFormatDate("dd/MM/yyyy"),
							numDias,
							mapa.get("annoVac") != null ? (String) mapa.get("annoVac") : "", 
							Constantes.ACTIVO,
							(String) mapa.get("txtObs"), 
							(String) mapa.get("codCreador"), 
							usuario	);
				}			
			}
			//PAS20171U230200001 - solicitud de reintegro   Insertar conceptos y detalles de reintegro asistencia
			if (solicitud) {
				try {

					if (tipo.equals(Constantes.MOV_REINTEGRO_POR_DESCUENTO_ASISTENCIA)) {
						log.debug("Ingresar los conceptos y detalles a tablas t8153solconcepto t8154soldetalle t8155solreintegro -- asistencia ");
						mensajes = registrarSolReintegro( annoActual,  new Integer(numero), mapa) ;
						if (!mensajes.isEmpty()) {
							beanM.setMensajeerror("Error al insertar conceptos asistencia");
							beanM.setMensajesol("No se pudo insertar reintegro de asistencia");
							throw new IncompleteConversationalState(beanM);
						}
					}
				} catch (Exception e) {
					log.error(e);
					beanM.setMensajeerror(e.getMessage());
					beanM.setMensajesol("No se pudo insertar los conceptos de reintegro asistencia");
					throw new IncompleteConversationalState(beanM);
				}
			}
			//Insertar conceptos y detalles de reintegro subsidio
			if (solicitud) {
				try {

					if (tipo.equals(Constantes.MOV_REINTEGRO_POR_DESCUENTO_SUBSIDIO)) {
						log.debug("Ingresar los conceptos y detalles a tablas t8153solconcepto t8154soldetalle t8155solreintegro -- subsidios ");
						mensajes = registrarSolReintegro( annoActual,  new Integer(numero), mapa) ;
						if (!mensajes.isEmpty()) {
							beanM.setMensajeerror("Error al insertar conceptos subsidio");
							beanM.setMensajesol("No se pudo insertar reintegro de subsidio");
							throw new IncompleteConversationalState(beanM);
						}
					}
				} catch (Exception e) {
					log.error(e);
					beanM.setMensajeerror(e.getMessage());
					beanM.setMensajesol("No se pudo insertar los conceptos de reintegro subsidio");
					throw new IncompleteConversationalState(beanM);
				}
			}
			//fin-PAS20171U230200001 - solicitud de reintegro    
			//MTM
			if (solicitud) {
				try{
					if(tipo.equals(Constantes.MOV_LABOR_EXCEPCIONAL)){
						
						ArrayList lista = (ArrayList) mapa.get("lista");
						log.debug(lista);
						String codMov = (String)mov.get("mov");
						log.debug(codMov);
						for (int i=0; i<lista.size();i++){
							int j = i+1;
							HashMap u = (HashMap)lista.get(i);
							t4821dao.insertRegistroSolicitudDetLabor(dbpool, 
									annoActual,
									new Integer(numero), 
									(String) mapa.get("UUOOFIN"),
									(String) mapa.get("userOrig"),
									codMov,
									j,
									(String) u.get("fechaMarca2"),
									(String) u.get("txtHoraIni")+":00",
									(String) u.get("txtHoraFin")+":00",
									(String) u.get("txtObs2"),
									(String) mapa.get("codCreador"), 	
									usuario);
						}	
					}
				} catch (Exception e) {
					log.error(e);
					beanM.setMensajeerror(e.getMessage());
					beanM.setMensajesol("No se pudo insertar los rangos de horas excepcionales en la t4821DAO.");
					throw new IncompleteConversationalState(beanM);
				}
			}
			//Insertar conceptos y detalles de reintegro subsidio
			if (solicitud) {
				try {
					if (tipo.equals(Constantes.LICENCIA_ENFERMEDAD)) {
						log.debug("Ingresar   detalles a tablas   txxxxsolmedica   ");
						mensajes = registrarSolMedica( annoActual,  new Integer(numero), mapa) ;
						if (!mensajes.isEmpty()) {
							beanM.setMensajeerror("Error al insertar en solicitud medica");
							beanM.setMensajesol("No se pudo insertar datos  en solicitud medica");
							throw new IncompleteConversationalState(beanM);
						}
					}
				} catch (Exception e) {
					log.error(e);
					beanM.setMensajeerror(e.getMessage());
					beanM.setMensajesol("No se pudo insertar los conceptos de reintegro subsidio");
					throw new IncompleteConversationalState(beanM);
				}
			}
			
			/******* JRR 05/12/2008 *******/
			if (solicitud) {
				try{
					if (tipo.equals(Constantes.VACACION) || tipo.equals(Constantes.VACACION_VENTA) ){
						Map param1 = new HashMap();
						if(mapa.get("nvaFechaIni")!=null && !(mapa.get("nvaFechaIni").toString()).trim().equals("")){
							param1.put("ffinicio", new FechaBean((String) mapa.get("nvaFechaIni")).getTimestamp());
						}		
						param1.put("cod_pers", (String) mapa.get("userOrig"));
						param1.put("licencia", Constantes.VACACION_PROGRAMADA);
						param1.put("anno_vac", mapa.get("annoVac") != null ? (String) mapa.get("annoVac") : "");
						param1.put("estado_id", Constantes.PROG_ACEPTADA);
						param1.put("fmod", new FechaBean().getTimestamp());
						param1.put("cuser_mod", usuario);
						param1.put("estado_new", constantes.leePropiedad("PROGRAMACION_EN_SOLICITUD"));
						
						//Sirve para actualizar la programacion solo en el caso de aprobacion de Venta
						param1.put("numero_ref", new Integer(numero));
						param1.put("anno_ref", "");
						param1.put("area_ref", "");
						
						t1282dao.actualizaProgramacion(param1);
					}else if(tipo.equals(Constantes.VACACION_POSTERGADA)){						
						String programadas=(String)mapa.get("programadasSel");
						if(log.isDebugEnabled()) log.debug("Programaciones1:"+programadas);
						
						String[] prog1=programadas.split("&");
						log.debug("NumRegistrosRep:"+programadas.length());
						for (int m=1;m<=prog1.length;m++){							
							Map param1 = new HashMap();							
							String[] datosRep=prog1[m-1].split("-");
						
							if(!(datosRep[2]).trim().equals("")){
								param1.put("ffinicio", new FechaBean(datosRep[2]).getTimestamp());
							}
							param1.put("cod_pers", (String) mapa.get("userOrig"));
							param1.put("licencia", Constantes.VACACION_PROGRAMADA);
							param1.put("anno_vac", mapa.get("annoVac") != null ? (String) mapa.get("annoVac") : "");
							param1.put("estado_id", Constantes.PROG_ACEPTADA);
							param1.put("fmod", new FechaBean().getTimestamp());
							param1.put("cuser_mod", usuario);
							param1.put("estado_new", constantes.leePropiedad("PROGRAMACION_EN_SOLICITUD"));
							
							//Sirve para actualizar la programacion solo en el caso de aprobacion de Venta
							param1.put("numero_ref", new Integer(numero));
							param1.put("anno_ref", "");
							param1.put("area_ref", "");
							param1.put("dias",new Integer(datosRep[1]));
							t1282dao.actualizaProgramacion(param1);
						}
					}
				} catch (Exception e) {
					log.error(e);
					log.error("Ha ocurrido un error en registraSolicitud "+e.getMessage(), e);
					beanM.setMensajeerror(e.getMessage());
					beanM.setMensajesol("No se pudo actualizar la programaciÃ³n.");
					throw new IncompleteConversationalState(beanM);
				}
			}
			/******************************/
			
			// ICAPUNAY - TRABAJADORES CON VACIONES PROGRAMADAS ACEPTADAS O ACTIVAS - 16/03/2011 */	(Si solicitud fue registrada y trabajador fue notificado-->se actualiza notificacion{tabla: t4502NotificaVaca, campo: ind_regis_solicit})*******/			
			if (solicitud) {
				if (log.isDebugEnabled()) log.debug("actualizar notificaciÃ³n x registro: "+"inicio");
				try{
					if (tipo.equals(Constantes.VACACION) || tipo.equals(Constantes.VACACION_VENTA) ){						
						FechaBean fb_fInicioVac1 = new FechaBean((String) mapa.get("fechaIni"),"dd/MM/yyyy");						
						String st_fInicioVac = fb_fInicioVac1.getFormatDate("yyyy-MM-dd");						
						FechaBean fb_fInicioVac2 = new FechaBean(st_fInicioVac, "yyyy-MM-dd");
						
						//FechaBean factual = new FechaBean();						
						/* ICAPUNAY/DTARAZONA - PAS20181U230300016 - Mejoras vacaciones2
						long diferencia = FechaBean.getDiferencia(fb_fInicioVac2.getCalendar(),factual.getCalendar(),Calendar.DATE);
						if (log.isDebugEnabled()) log.debug("actualizar notificaciÃ³n x registro: "+"diferencia= "+String.valueOf(diferencia));
						long valor = Long.valueOf(constantes.leePropiedad("DIAS_PREVIOS_INICIO_VACACIONES")).longValue();						
						if((diferencia < valor) || (diferencia == valor)){*/								
							String num_notificacion=null;
							Map mapaDatos = new HashMap();
							Timestamp fechaModif = new Timestamp(System.currentTimeMillis());							
							mapaDatos.put("userOrig",(String) mapa.get("userOrig"));
							mapaDatos.put("fec_ini_vacacion",fb_fInicioVac2.getSQLDate());
							if (log.isDebugEnabled()) log.debug("actualizar notificaciÃ³n x registro: "+"mapaDatos Inicial= "+mapaDatos);
							T4502DAO t4502dao = new T4502DAO(sl.getDataSource("java:comp/env/jdbc/dgsp"));							
							boolean actualizoNotificacion=false;							
							Map notificacionEnviadaNoRegistrada=t4502dao.findNotificacionEnviadaPeroNoRegistrada(mapaDatos);
							if (notificacionEnviadaNoRegistrada!=null && !notificacionEnviadaNoRegistrada.isEmpty()){								
								num_notificacion=(String)notificacionEnviadaNoRegistrada.get("num_notificacion");
								mapaDatos.put("cod_usumodif",(String)mapaDatos.get("userOrig"));
								mapaDatos.put("fec_modif",fechaModif);
								mapaDatos.put("num_notificacion",num_notificacion);								
								actualizoNotificacion=t4502dao.updateRegistroSolicitudByPK(mapaDatos);
								if (actualizoNotificacion==true){
									if (log.isDebugEnabled()) log.debug("registrarSupervisor - Se actualizÃ³ campo de registro de solicitud de notificaciÃ³n");
								}else{
									if (log.isDebugEnabled()) log.debug("registrarSupervisor - No se actualizÃ³ notificaciÃ³n");
								}
							}						
						//}	ICAPUNAY/DTARAZONA - PAS20181U230300016 - Mejoras vacaciones2					
					}
				} catch (Exception e) {
					log.error(e.getMessage());
					beanM.setMensajeerror(e.getMessage());
					beanM.setMensajesol("No se pudo actualizar la notificaciÃ³n de vacaciones.");
					throw new IncompleteConversationalState(beanM);
				}
			}
			// ICAPUNAY - TRABAJADORES CON VACIONES PROGRAMADAS ACEPTADAS O ACTIVAS - 16/03/2011 */
						
			/******* JRR NUEVA SOLICITUD *******/
			if (solicitud) {
				try{
					if (tipo.equals(Constantes.VACACION_INDEMNIZADA) ){
						
						//15/04/2009
						Map param1 = new HashMap();
						param1.put("cod_pers", (String) mapa.get("userOrig"));
						param1.put("periodo", (String) mapa.get("periodo"));
						param1.put("licencia", Constantes.VACACION_PROGRAMADA);
						param1.put("ffinicio", new FechaBean(fechaIni).getTimestamp());
						param1.put("anno_vac", mapa.get("annoVac") != null ? (String) mapa.get("annoVac") : "");
						param1.put("ffin", new FechaBean(fechaFin).getTimestamp());
						param1.put("ffin", new FechaBean(fechaFin).getTimestamp());
						param1.put("dias", String.valueOf(numDias));
						param1.put("u_organ", (String) mapa.get("UUOOFIN"));
						param1.put("anno_ref", "");
						param1.put("area_ref", "");
						param1.put("numero_ref", new Integer(numero));
						param1.put("observ", "ART 23 DL 713");
						param1.put("est_id", constantes.leePropiedad("PROGRAMACION_EN_SOLICITUD"));
						param1.put("fcreacion", new FechaBean().getTimestamp());
						param1.put("cuser_crea", usuario);
						
						if (log.isDebugEnabled()) log.debug(" PRUEBA NUEVA SOLICITUD REGISTRO PROGRAMACION - param1: "+ param1);
						t1282dao.insertarVacacionesDetalle(param1);
					}
				} catch (Exception e) {
					log.error(e);
					beanM.setMensajeerror(e.getMessage());
					beanM.setMensajesol("No se pudo actualizar la programaciÃ³n.");
					throw new IncompleteConversationalState(beanM);
				}
			}
			/******************************/
			
			if (solicitud != false) {			
				Map prms = new HashMap();
				prms.put("cod_pers", mapa.get("userOrig"));
				prms.put("anno", annoActual);
				prms.put("numero", new Integer(numero));
				//String numSeguimiento = seguiDAO.findSigSeg(dbpool, annoActual, new Short(numero), (String) mapa.get("userOrig"));
				log.debug("seguiDAO.findSigSeg(prms)... prms:"+prms);
				String numSeguimiento = seguiDAO.findSigSeg(prms);
				log.debug("... numSeguimiento:"+numSeguimiento);
				//registramos el primer seguimiento
				//T1455CMPLocal seguimiento = segHome.create(solicitud.getAnno(),
				//			solicitud.getNumero(), 
				//			solicitud.getUOrgan(), 
				//			solicitud.getCodPers(), 
				//			numSeguimiento,
				//			new java.sql.Timestamp(System.currentTimeMillis()),
				//			Constantes.ACCION_INICIAR,
				//			Constantes.ESTADO_SEGUIMIENTO, 
				//			(String) mapa.get("userOrig"),
				//			(String) mapa.get("userDest"), 
				//			(String) mapa.get("txtObsSeg"), 
				//			new java.sql.Timestamp(System.currentTimeMillis()), 
				//			usuario);

			    Map mapdel = (HashMap) mapa.get("mapdel");

			    if(log.isDebugEnabled())  log.debug("mapdel"+mapdel);
				if (mapdel!=null && !mapdel.isEmpty()){
					//String deleg = (String)delegado.get("t02cod_pers");
					//String userOrig = (String)pp.get("cod_pers_ori");
					//if (userOrig.trim().equalsIgnoreCase(deleg.trim())){
					if(log.isDebugEnabled())  log.debug("mapdel"+mapdel);
					String jefeDel = (String)mapa.get("jefedelego");
					if (jefeDel != null){
						if(log.isDebugEnabled())  log.debug("jefedelego!=null");
						//No hacer nada
					      
						if (jefeDel.trim().equals(mapdel.get("t12cod_jefe").toString().trim())){
							//No hacer nada
					    } else {
					        if(log.isDebugEnabled())  log.debug("cambia jefeee");
					        if(log.isDebugEnabled())  log.debug("receptorSolic: "+receptorSolic);
					        //mapa.put("userDest", (String)mapdel.get("t12cod_jefe"));//ICAPUNAY - PAS20165E230300128 - registro solic. Delegado (delegado pertenece a uo donde es delegado)  07/06/16
					        mapa.put("userDest", receptorSolic);//ICAPUNAY - PAS20165E230300128 - registro solic. Delegado (delegado pertenece a uo donde es delegado)  07/06/16
					        if(log.isDebugEnabled())  log.debug("userDest: "+receptorSolic);
					    }
						
					}else{
						if(log.isDebugEnabled())  log.debug("jefedelego==null");
						if (tipo.equals(Constantes.LICENCIA_ENFERMEDAD)){
							//No hacer nada
						} else
						{
							if(log.isDebugEnabled())  log.debug("donde estoy??");
							if ((mapa.get("userDest").toString().trim()).equals(mapdel.get("t02cod_pers").toString().trim())){
								if(log.isDebugEnabled())  log.debug("en no hacer nada");
//								No hacer nada
							} else
							{
								if(log.isDebugEnabled())  log.debug("cambia jefee2");
								mapa.put("userDest", (String)mapdel.get("t12cod_jefe"));
								if(log.isDebugEnabled())  log.debug("userDest2: "+(String)mapdel.get("t12cod_jefe")); //ICAPUNAY - PAS20165E230300128 - registro solic. Delegado (delegado pertenece a uo donde es delegado)  07/06/16
							}
						}

					}
						
					//}
				}
				//PRAC-JCALLO
				//anno, numero, u_organ, cod_pers, num_seguim, fecha_recep, fecha_deriv, accion_id, ")
			    //estado_id, cuser_orig, cuser_dest, tobserv, fcreacion, cuser_crea
				/*seguiDAO.insertRegistroSeguimiento(dbpool,
						annoActual,
						new Short(numero), 
						(String) mapa.get("codUO"), 
						(String) mapa.get("userOrig"), 
						numSeguimiento,
						Constantes.ACCION_INICIAR,
						Constantes.ESTADO_SEGUIMIENTO, 
						(String) mapa.get("userOrig"),
						(String) mapa.get("userDest"), 
						(String) mapa.get("txtObsSeg"), 
						usuario	);*/
				//prac-jcallo
				Map params = new HashMap();
				params.put("anno", annoActual);
				params.put("numero", new Integer(numero));
//				EBV 03/03/2009 Debe grabarse la UUOO del Solicitante
				//params.put("u_organ", mapa.get("codUO"));				
				params.put("u_organ", mapa.get("UUOOFIN"));
				params.put("cod_pers", mapa.get("userOrig"));
				params.put("num_seguim", numSeguimiento);
				params.put("fecha_recep", new FechaBean().getTimestamp());//duda en este con valor null
				params.put("fecha_deriv", null);//duda en este con valor null
				params.put("accion_id",Constantes.ACCION_INICIAR);
				params.put("estado_id", Constantes.ESTADO_SEGUIMIENTO);
				params.put("cuser_orig", mapa.get("userOrig"));
				params.put("cuser_dest", mapa.get("userDest"));
				params.put("tobserv", mapa.get("txtObsSeg"));
				params.put("fcreacion", new FechaBean().getTimestamp());
				params.put("cuser_crea", usuario);
				log.debug("seguiDAO.insertarSeguimientoSol(params)... params:"+params);
				seguiDAO.insertarSeguimientoSol(params);
				
				//actualizamos el seguimiento actual de la solicitud
				//solicitud.setSeguimAct(seguimiento.getNumSeguim());
				//solicitud.setSeguimAct(numSeguimiento);
				
				/*soliDAO.updateRegistroSolicitud(dbpool,
						annoActual, numSeguimiento,
						new Short(numero),
						(String) mapa.get("codUO"), 
						(String) mapa.get("userOrig"),
						(String) mapa.get("asunto"),
						fechaIni);*/				
				
				HashMap tipoMov = movDAO.findByMov(dbpool, tipo);

				String origen = (String) mapa.get("userOrig");
				String destino = (String) mapa.get("userDest");
				String tipoSol = (String) tipoMov.get("descrip");

				/*ResourceBundle bundle = ResourceBundle.getBundle("pe.gob.sunat.sp.asistencia.sirh");
				String servidorIP = bundle.getString("servidorIP");
				String strURL = "http://"+servidorIP+"/asistencia/asisS13Alias";
				String paramsURL = "accion=cargarRecibidas&codPers=" + destino;
				String programa = bundle.getString("programa1");
				Cifrador cifrador = new Cifrador();				
				String url = cifrador.encriptaURL(destino, programa, strURL,paramsURL);*/

				ResourceBundle bundle = ResourceBundle.getBundle("pe.gob.sunat.sp.asistencia.sirh");
				String servidorIP = bundle.getString("servidorIP");

				String mensaje = "Ud. ha recibido una nueva solicitud electr&oacute;nica de <strong>"
						+ tipoSol + "</strong>.";
				//String strURL = "http://"+servidorIP+"/cl-at-iamenu";
				//String paramsURL = "accion=cargarRecibidas&codPers=" + destino;
				String programa = bundle.getString("programa1");

				//Cifrador cifrador = new Cifrador();				
				//String url = cifrador.encriptaURL(destino, programa, strURL,paramsURL);

				//String url = new MenuCliente().generaInvocacionLibre(programa);
				String url = new MenuCliente().generaInvocacionURL(programa,null,true,MenuCliente.INTRANET,"/ol-ti-iaasistencia/asisS11Alias");
				//String strURL = "http://"+servidorIP+"/cl-at-iamenu"+url;
				String strURL = "http://"+servidorIP+url;
				log.debug("URL "+url);
				log.debug("strURL "+strURL);
				String nombre = personalDAO.findNombreCompletoByCodPers(dbpool, destino);
				String texto = Utiles.textoCorreoProceso(dbpool, nombre, mensaje, strURL);

				//String mensaje = "Ud. ha recibido una nueva solicitud electr&oacute;nica de <strong>"+ tipoSol + "</strong>.";
				//String nombre = personalDAO.findNombreCompletoByCodPers(dbpool, destino);
				//String texto = Utiles.textoCorreoProceso(dbpool, nombre, mensaje);

				//enviamos el mail al trabajador
				//ICAPUNAY 05/10/2012 AJUSTES AOM 06A4T11 LABOR EXCEPCIONAL
				/*HashMap datos = new HashMap();
				datos.put("subject", "Solicitud de Asistencia");
				datos.put("message", texto);
				datos.put("from", correoDAO.findCorreoByCodPers(dbpool,
						origen));
				datos.put("to", correoDAO
						.findCorreoByCodPers(dbpool, destino));
				QueueDAO queue = new QueueDAO();
				queue.enviaCorreo(datos);*/
				
				if (tipo.equals(Constantes.MOV_LABOR_EXCEPCIONAL) || tipo.equals(Constantes.MOV_SOLICITUD_COMPENSACION) ){//124  o 125
					String nombreColab = personalDAO.findNombreCompletoByCodPers(dbpool, origen);
					nombreColab = origen +" - "+nombreColab;
					nombre = destino +" - "+nombre;
					String mensajeToJefe = "Ud. ha recibido una nueva <strong>PAPELETA DE "+ tipoSol + " N° "+annoActual+"-"+numero+"</strong> por el(la):";
					String mensajeColab = "";
					if(tipo.equals(Constantes.MOV_SOLICITUD_COMPENSACION)){
						mensajeColab = "del " + fechaIni + " al " +fechaFin ;
					}
					if(tipo.equals(Constantes.MOV_LABOR_EXCEPCIONAL)){
						List fechas = t4821dao.findFechasPermiso(origen,annoActual,numero);
						if (fechas!=null && fechas.size()>0){
							mensajeColab = "para el "; 
							for (int m=0; m<fechas.size();m++){
								HashMap fecha = (HashMap)fechas.get(m);
								if (fechas.size()==1){
									mensajeColab = mensajeColab + fecha.get("fec_permiso_desc") ;
								}else{
									if (m==fechas.size()-1){//ultimo va "y"
										mensajeColab = mensajeColab + fecha.get("fec_permiso_desc") ;
									}else{
										if (m==fechas.size()-2){
											mensajeColab = mensajeColab + fecha.get("fec_permiso_desc")+" y " ;
										}else{
											mensajeColab = mensajeColab + fecha.get("fec_permiso_desc")+" , " ;
										}										
									}
								}								
							}
						}
					}
					log.debug("mensajeColab: "+mensajeColab);
					String textoCorreoJefe = Utiles.textoCorreoaJefes(dbpool, nombre, mensajeToJefe, strURL,nombreColab,mensajeColab);					
					
					HashMap datos = new HashMap();
					//datos.put("subject", "Solicitud de Asistencia");
					datos.put("subject", "Papeleta de Asistencia");
					datos.put("message", textoCorreoJefe);
					datos.put("from", correoDAO.findCorreoByCodPers(dbpool,	origen));
					datos.put("to", correoDAO.findCorreoByCodPers(dbpool, destino));
					QueueDAO queue = new QueueDAO();
					queue.enviaCorreo(datos);

				}else{//otros tipos solicitudes
					
					HashMap datos = new HashMap();
					datos.put("subject", "Solicitud de Asistencia");
					datos.put("message", texto);
					datos.put("from", correoDAO.findCorreoByCodPers(dbpool,origen));
					datos.put("to", correoDAO.findCorreoByCodPers(dbpool, destino));
					QueueDAO queue = new QueueDAO();
					try {
					queue.enviaCorreo(datos);
					} catch (Exception e) {
						log.error("Error en envio de correo",e);
					}
				
					
					if (tipo.equals(Constantes.LICENCIA_ENFERMEDAD)){
						mensaje = "Ud. ha recibido una copia informativa de la solicitud electr&oacute;nica de <strong>"+ tipoSol + "</strong>";
						mensaje = mensaje.concat(" <strong> del ").concat(fechaIni).concat( " al ").concat(fechaFin).concat(" </strong>.");
						mensaje = mensaje.concat(" Generada por ").concat((String)pp.get("t02nombres"));
						mensaje = mensaje.concat(" ").concat((String)pp.get("t02ap_pate"));
						mensaje = mensaje.concat(" ").concat((String)pp.get("t02ap_mate"));
						destino = (String) pp.get("t12cod_jefe");
						log.debug("AL JEFE : "+ destino);
						nombre = personalDAO.findNombreCompletoByCodPers(dbpool, destino);
						texto = Utiles.textoCorreoProceso(dbpool, nombre, mensaje,"");

						//enviamos el mail al trabajador
						datos = new HashMap();
						datos.put("subject", "Solicitud de Asistencia - Copia Informativa");
						datos.put("message", texto);
						datos.put("from", correoDAO.findCorreoByCodPers(dbpool,origen));
						datos.put("to", correoDAO.findCorreoByCodPers(dbpool, destino));
						queue = new QueueDAO();
						queue.enviaCorreo(datos);
					}
				}
				//FIN ICAPUNAY 05/10/2012 AJUSTES AOM 06A4T11 LABOR EXCEPCIONAL
			}

		} catch (Exception e) {
			log.error(e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		
		return mensajes;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList procesarSolicitud(String dbpool, HashMap solicitud,
			HashMap mapa, HashMap aprobador, String usuario)
			throws IncompleteConversationalState, RemoteException {
		
		BeanMensaje beanM = new BeanMensaje();
		ArrayList mensajes = new ArrayList();
		T4818DAO t4818dao = new T4818DAO(dbpool);
		T3701DAO t3701dao = new T3701DAO(dbpool);
		T1282DAO t1282dao = new T1282DAO(sl.getDataSource("java:comp/env/jdbc/dcsp"));
		
		try {
			DataSource dsOracle = sl.getDataSource("java:comp/env/pool_oracle");
			OracleSequenceDAO seqDAO = new OracleSequenceDAO();
			String numero = seqDAO.getSequenceDS(dsOracle,Constantes.SEQ_SOLICITUD);
			
			String accion = (String) mapa.get("accion");			
			String tipo = (String) solicitud.get("tipo");

			if (accion.equals(Constantes.ACCION_APROBAR)) {
				if (log.isDebugEnabled())
					log.debug("mapa en accion aprobar: "+mapa);
				mensajes = verificarAprobarSolicitud(dbpool, solicitud, mapa, aprobador, usuario);
			}
			else if (accion.equals(Constantes.ACCION_RECHAZAR)) {

				/* JRR - CODIGO COMSA PROGRAMACION - 21/05/2010 */
				Map datos= new HashMap();
				datos.put("paramSolicitud",solicitud);
				datos.put("paramMapa",mapa);
				datos.put("paramUsuario",usuario);
				mensajes = (ArrayList)rechazarSolicitud(datos);
				/*      */
				//log.debug("metodo procesarSolicitud solicitud : "+solicitud);
				//mensajes = rechazarSolicitud(dbpool, solicitud, mapa, usuario);
				
				if (mensajes.isEmpty()){
					if (tipo.trim().equals(Constantes.REPROGRAMACION_VACACION)){
						mensajes = this.actualizaSolicitudReprogramacion(dbpool ,solicitud,
								Constantes.REPROGRAMACION_VACACION,
								Constantes.PROG_RECHAZADA);
					}
					if(tipo.trim().equals(Constantes.VACACION_POSTERGADA)){
						ArrayList vacSolProg=(ArrayList)solicitud.get("d1");
						if(log.isDebugEnabled()) log.debug("vacSolProg:"+vacSolProg);						
						
						for(int i=0;i<vacSolProg.size();i++)
						{	
							HashMap vacSolReprog1=(HashMap)vacSolProg.get(i);	
							Map param1 = new HashMap();		
							
							param1.put("ffinicio", new FechaBean(vacSolReprog1.get("ffinicio").toString()).getTimestamp());
							
							param1.put("cod_pers", (String) solicitud.get("codPers"));
							param1.put("licencia", Constantes.VACACION_PROGRAMADA);
							param1.put("anno_vac", vacSolReprog1.get("anno_vac") != null ? (String) vacSolReprog1.get("anno_vac") : "");
							param1.put("estado_id", constantes.leePropiedad("PROGRAMACION_EN_SOLICITUD"));
							param1.put("fmod", new FechaBean().getTimestamp());
							param1.put("cuser_mod", usuario);
							param1.put("estado_new", constantes.leePropiedad("PROG_ACEPTADA"));
							if(log.isDebugEnabled()) log.debug("UsuarioMOd:"+usuario);
							//Sirve para actualizar la programacion solo en el caso de aprobacion de Venta
							param1.put("numero_ref", new Integer(solicitud.get("numero").toString()));
							param1.put("anno_ref", "");
							param1.put("area_ref", "");
							param1.put("dias", new Integer(vacSolReprog1.get("dias").toString()));
							
							boolean actEstado=t1282dao.actualizaProgramacion(param1);
							if(log.isDebugEnabled()) log.debug("Actualizado:"+param1+": Estado: "+actEstado);
						}
					}
					/*MTM si es una solicitud de labor excepcional*/
					if (tipo.trim().equals(Constantes.MOV_LABOR_EXCEPCIONAL)){
						log.debug("labor excepcional rechazo");
						log.debug("mensaje de salida: " +mensajes);
						List lista = listaSolicitud(dbpool, (String) solicitud.get("anno"), (String) solicitud.get("numero"),
								(String) solicitud.get("uoOrig"),
								(String) solicitud.get("codPers"),
								(String) solicitud.get("tipo"));

						String cod_pers = solicitud.get("codPers").toString();
						String cod_jefe_aut = solicitud.get("userDest").toString();
						String ind_aut = "0";
						log.debug("solicitud: " +solicitud);
						for (int i=0; i<lista.size();i++){
							Map prms = new HashMap();
							prms.put("cod_pers", cod_pers);
							prms.put("cod_jefe_aut", cod_jefe_aut);
							prms.put("ind_aut", ind_aut);
							prms.put("cod_user_crea", usuario);
							prms.put("fec_creacion", new Timestamp(System.currentTimeMillis()));
							HashMap u = (HashMap)lista.get(i);
							String fecha = (String) u.get("fec_permiso_desc");
							SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy"); 
							Date fechaDate = null;
							fechaDate = formato.parse(fecha); 
							prms.put("fec_aut", fechaDate);
							prms.put("hor_ini_aut", (String) u.get("hor_ini_permiso")+":00");
							prms.put("hor_fin_aut", (String) u.get("hor_fin_permiso")+":00");
							prms.put("obs_sustento", (String) u.get("obs_sustento_obs"));
							log.debug("parametro antes de insertar t4818: " + prms);
							
							if(t4818dao.verificaRegistroAutorizaExcPK(dbpool, cod_pers, fecha,(String) u.get("hor_ini_permiso"))){
								t4818dao.deleteRegistroAutorizaExc(dbpool, prms);
								t4818dao.insertRegistroAutorizaExc(dbpool, prms);							
							}else{
								t4818dao.insertRegistroAutorizaExc(dbpool, prms);
		                    }							
							log.debug("NOVEDAD: "+fechaDate+"-metodo=procesarSolicitud-accion=Rechazar");
							Map datos2 = new HashMap();
							//String ind_proceso = "4";//ICAPUNAY 27/06/2012 SOLO INSERTA (SI NO EXISTE PARA FECHA SINO ACTUALIZA) CON INDICADOR 0 EN VEZ DE 4
							String ind_proceso = "0";//ICAPUNAY 27/06/2012 SOLO INSERTA (SI NO EXISTE PARA FECHA SINO ACTUALIZA) CON INDICADOR 0 EN VEZ DE 4
							datos2.put("cod_pers", cod_pers);
							datos2.put("fec_refer", fechaDate);
							datos2.put("ind_proceso", ind_proceso);
							datos2.put("cod_usucrea", usuario);
							datos2.put("fec_creacion", new Timestamp(System.currentTimeMillis()));
							log.debug("parametro por rechazo antes de insertar t3701: " + datos2);							
							//ICAPUNAY 27/06/2012 SOLO INSERTA (SI NO EXISTE PARA FECHA SINO ACTUALIZA) CON INDICADOR 0 EN VEZ DE 4
							/*
							Map datos3 = new HashMap();
							datos3 = t3701dao.findNovedadByPK(datos2);							
							if (datos3 == null){
								t3701dao.registrarNovedad(datos2);
							}*/
							Map datos3 = t3701dao.findNovedadByTrabFec(datos2);
							log.debug("datos3: "+datos3);							
							if (datos3!=null && !datos3.isEmpty()){//si existe novedad para fecha con un indicador
								String indicadorActual = datos3.get("ind_proceso").toString().trim();
								if (!indicadorActual.equals("0")){
									datos2.put("nuevo",ind_proceso);
									datos2.put("usuario",usuario);
									datos2.put("proceso",indicadorActual);
									t3701dao.actualizar(datos2);//actualizo indicador a 0
								}								
							}else{//no existe ninguna novedad para fecha
								t3701dao.registrarNovedad(datos2);//registra novedad con indicador 0
							}
							log.debug("FIN NOVEDAD: "+fechaDate+"-metodo=procesarSolicitud-accion=Rechazar");
							//FIN ICAPUNAY 27/06/2012 SOLO INSERTA (SI NO EXISTE PARA FECHA SINO ACTUALIZA) CON INDICADOR 0 EN VEZ DE 4
						}			
					}
				
			}
			else if (accion.equals(Constantes.ACCION_DERIVAR_RRHH)) {
				mensajes = derivarRRHHSolicitud(dbpool, solicitud, mapa, aprobador, usuario);
			}
			}
		} catch (Exception e) {
			log.error(e);	
			String error="";//WERR-PAS20155E230300132
			if(e!=null){//WERR-PAS20155E230300132
				if(e.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")){//WERR-PAS20155E230300132
					BeanMensaje beanMst=new BeanMensaje();//WERR-PAS20155E230300132
						int indexError;//WERR-PAS20155E230300132	
						indexError=e.toString().indexOf("no posee turno entre las fechas que va a compensar");//WERR-PAS20155E230300132
						if(indexError!=-1){ //WERR-PAS20155E230300132
							error = e.toString().substring(184);//WERR-PAS20155E230300132
							beanMst.setMensajeerror(error);//WERR-PAS20155E230300132
							beanMst.setMensajesol("Por favor intente nuevamente.");//WERR-PAS20155E230300132
						}//WERR-PAS20155E230300132
					throw new IncompleteConversationalState(beanMst);//WERR-PAS20155E230300132
				}//WERR-PAS20155E230300132
				else{
					error=e.toString();
					beanM.setMensajeerror(error);//WERR-PAS20155E230300132
					beanM.setMensajesol("Por favor intente nuevamente.");
					throw new IncompleteConversationalState(beanM);
				}
			}//WERR-PAS20155E230300132		
			throw new IncompleteConversationalState(beanM);
		}
		return mensajes;
	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public ArrayList procesarSolicitudAdministracion(String dbpool, HashMap solicitud,
			HashMap mapa, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		ArrayList mensajes = new ArrayList();
		T4818DAO t4818dao = new T4818DAO(dbpool);//124
		T4819DAO t4819dao = new T4819DAO(dbpool);
		T3701DAO t3701dao = new T3701DAO(dbpool);
		T1273DAO t1273dao = new T1273DAO(dbpool);
		T1279DAO t1279dao = new T1279DAO();//125
		T1277DAO t1277dao = new T1277DAO();
		T4821DAO t4821dao = new T4821DAO(dbpool);
		
		ArrayList listaMensajes = mapa.get("listaMensajes") == null ? new ArrayList() :(ArrayList)mapa.get("listaMensajes");
		
		try {
			String accion_adm = (String) mapa.get("accion_adm");			
			String tipo = (String) solicitud.get("tipo");
			boolean bResult = false;			

			if (accion_adm.equals("MM")) {  			//modificar
				
				if (tipo.trim().equals(Constantes.MOV_SOLICITUD_COMPENSACION)){
					log.debug("MOV_SOLICITUD_COMPENSACION mapa: "+mapa);
					log.debug("MOV_SOLICITUD_COMPENSACION solicitud: "+solicitud);
					String cod_pers = solicitud.get("codPers").toString();
					String ffinicio = mapa.get("ffinicio").toString();
					String ffin = mapa.get("ffin").toString();
					
					HashMap tipoMov = t1279dao.findByMov(dbpool, tipo);
					HashMap params = new HashMap();
					params.put("userOrig", cod_pers);
					params.put("fechaIni", ffinicio);
					params.put("fechaFin", ffin);
					params.put("fechaFin", ffin);
					params.put("regimenCol", "--");
					params.put("dias_ini", new Integer(0));
					params.put("fechas", new ArrayList());
					params.put("dbpool", dbpool);					
					
					HashMap licencia= (HashMap)solicitud.get("licencia");		

					if(licencia == null){
						mensajes.add("No se puede encontrar la licencia asociada a la solicitud, no se realizará ninguna acción. Verifique los datos.");
					}else{
						params.put("licencia_numero", licencia.get("numero"));
						mensajes = validaLicenciaCompJefe(params,tipoMov,false); 		
						if (log.isDebugEnabled()) log.debug("MOV_SOLICITUD_COMPENSACION num_dias: "+(Integer)params.get("dias_ini"));
						if (log.isDebugEnabled()) log.debug("MOV_SOLICITUD_COMPENSACION fechas: "+(ArrayList)params.get("fechas"));
						
						log.debug("if..."+licencia);
						if(((Integer)params.get("dias_ini")).intValue() != ((Double)licencia.get("qdias")).intValue()){
							mensajes.add("El número de días válidos ingresados no corresponde al número de días de la solicitud inicial.<br />\n"+
										"Verifique los feriados y días de descanso existentes en el rango ingresado."
									);						
						}
					}
					
					if(mensajes.isEmpty()){
						int numero = Integer.parseInt((String)solicitud.get("numero"));
						String anno = (String)solicitud.get("anno");
						log.debug("anno y numero :"+anno+"--"+numero);
						
						//ACTUALIZAR LA SOLICITUD
						bResult = t1277dao.updateRegistroSolicitudFechas(dbpool, ffinicio, ffin, usuario, anno, numero, cod_pers, tipo);
						
						if(bResult){
							HashMap hmMensaje = new HashMap();
							hmMensaje.put("mensaje", "Solicitud "+anno+"--"+numero+" actualizada correctamente.");
							hmMensaje.put("tipo", "success");//info, warning, error, success
							
							listaMensajes.add(hmMensaje);
						}
						
						//ACTUALIZAR DETALLE DE LA SOLICITUD
						List detalles = new ArrayList();
						detalles = t4821dao.findFechasPermiso(cod_pers, anno, (String)solicitud.get("numero"));
						log.debug("detalles--"+detalles);
						if(detalles != null){
							ArrayList fechas_nuevas=(ArrayList)params.get("fechas");
							log.debug("fechas_nuevas--"+fechas_nuevas);
							for(int i=0; i<detalles.size(); i++){
								String fecha = (String)((HashMap)detalles.get(i)).get("fec_permiso_desc");
								String fecha_nueva = (String)fechas_nuevas.get(i);
								log.debug("fechasssss--"+fecha+"--"+fecha_nueva);
								t4821dao.updateRegistroSolicitudDetFecha(fecha_nueva, usuario, anno, numero, cod_pers, fecha);
							}	
						}
		
						//ACTUALIZAR LA LICENCIA
						bResult = t1273dao.updateRegistroLicenciaFechas(dbpool, ffinicio, ffin, usuario, anno, numero, cod_pers, tipo);
						
						if(bResult){
							HashMap hmMensaje = new HashMap();
							hmMensaje.put("mensaje", "Licencia asociada a la solicitud "+anno+"--"+numero+" actualizada correctamente.");
							hmMensaje.put("tipo", "success");//info, warning, error, success
							
							listaMensajes.add(hmMensaje);
						}
					}
					
					
				}
				
				
			}
			else if (accion_adm.equals("AA")) {			//anular

				//No puede estar aca generico si no se debe ejecutar si es que no hay mensajes de error al intentar anular un tipo de solicitud
				//mensajes = modificarSeguimientoAdministracion(dbpool, (HashMap)solicitud, usuario); //actualizar ultimo seguimiento (todos) //ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta vacaciones (SC)
				
				if (mensajes.isEmpty()){
					if (tipo.trim().equals(Constantes.MOV_LABOR_EXCEPCIONAL)){
						List lista=(List) solicitud.get("lista");

						String cod_pers = solicitud.get("codPers").toString();
						String ind_aut = "0";
						String ind_comp = "4";
						log.debug("solicitud: " +solicitud);
						for (int i=0; i<lista.size();i++){
							Map prms = new HashMap();
							HashMap u = (HashMap)lista.get(i);
							String fecha = (String) u.get("fec_permiso_desc");
							SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy"); 
							Date fechaDate = null;
							fechaDate = formato.parse(fecha); 
							String sHoraIniPerm = ((String) u.get("hor_ini_permiso")).trim().length()<8 ? ((String) u.get("hor_ini_permiso")).trim()+":00" :(String) u.get("hor_ini_permiso"); 
							
							prms.put("cod_pers", cod_pers);
							prms.put("ind_aut", ind_aut);
							prms.put("cod_user_mod", usuario);
							prms.put("fec_aut", fecha);
							prms.put("hor_ini_aut", sHoraIniPerm);
							//log.info("Inicio de ANULACION de Solicitud 124");
							if(t4818dao.verificaRegistroAutorizaExcPK(dbpool, cod_pers, fecha,sHoraIniPerm)) {
								//log.info("JMARAVI: Verificaciï¿½n de existencia de autorizacion T4818 con params::: "+prms);
								boolean result1 = t4818dao.updateRegistroAutorizaExcRechazar(prms);
								//log.info("JMARAVI: Resultado de Actualizar T4818"+result1);								
							}
							
							prms.put("ind_comp", ind_comp);
							prms.put("fec_perm", fecha);
							
							if(t4819dao.findPermanenciaExtraByFecHor(cod_pers, fecha,sHoraIniPerm)){
								//log.info("JMARAVI: Verificaciï¿½n de existencia de autorizacion T4819 con params::: "+cod_pers+":::"+ fecha+":::"+ u.get("hor_ini_permiso"));
								boolean result2 = t4819dao.updateRegistroLaborRechazar(prms);
								//log.info("JMARAVI: Resultado de Actualizar T4819"+result2);
							}
							
							log.debug("***REUSED-CODE***NOVEDAD: "+fechaDate+"-metodo=procesarSolicitud-accion=Rechazar");
							Map datos2 = new HashMap();
		 					//String ind_proceso = "4";//ICAPUNAY 27/06/2012 SOLO INSERTA (SI NO EXISTE PARA FECHA SINO ACTUALIZA) CON INDICADOR 0 EN VEZ DE 4
							String ind_proceso = "0";//ICAPUNAY 27/06/2012 SOLO INSERTA (SI NO EXISTE PARA FECHA SINO ACTUALIZA) CON INDICADOR 0 EN VEZ DE 4
							datos2.put("cod_pers", cod_pers);
							datos2.put("fec_refer", fechaDate);
							datos2.put("ind_proceso", ind_proceso);
							datos2.put("cod_usucrea", usuario);
							datos2.put("fec_creacion", new Timestamp(System.currentTimeMillis()));
							log.debug("parametro por rechazo antes de insertar t3701: " + datos2);							
							
							Map datos3 = t3701dao.findNovedadByTrabFec(datos2);
							log.debug("datos3: "+datos3);							
							if (datos3!=null && !datos3.isEmpty()){//si existe novedad para fecha con un indicador
								String indicadorActual = datos3.get("ind_proceso").toString().trim();
								if (!indicadorActual.equals("0")){
									datos2.put("nuevo",ind_proceso);
									datos2.put("usuario",usuario);
									datos2.put("proceso",indicadorActual);
									t3701dao.actualizar(datos2);//actualizo indicador a 0
								}								
							}else{//no existe ninguna novedad para fecha
								t3701dao.registrarNovedad(datos2);//registra novedad con indicador 0
							}
							log.debug("***REUSED-CODE-FIN***FIN NOVEDAD: "+fechaDate+"-metodo=procesarSolicitud-accion=Rechazar");
						}	
						
						//Adicionamos el mensaje de resultado del procesamiento.						
						bResult = true;
						if(bResult){
							HashMap hmMensaje = new HashMap();
							hmMensaje.put("mensaje", "La labor excepcional asociada a la solicitud "+solicitud.get("numero")+" fue anulada correctamente.");
							hmMensaje.put("tipo", "success");//info, warning, error, success
							
							listaMensajes.add(hmMensaje);
						}
						
						mensajes = modificarSeguimientoAdministracion(dbpool, (HashMap)solicitud, usuario); //actualizar ultimo seguimiento (todos) //ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta vacaciones (SC)
					}
					else if (tipo.trim().equals(Constantes.LICENCIA_ENFERMEDAD)){//21
						T1274DAO t1274dao = new T1274DAO(dbpool);
						String cod_pers = solicitud.get("codPers").toString();
						
						Map prms = new HashMap();
						
						prms.put("anno_ref", (String)solicitud.get("anno"));
						prms.put("numero_ref", (String)solicitud.get("numero"));						
						prms.put("cod_pers", cod_pers);
						prms.put("u_organ", (String)solicitud.get("uorgan"));
						prms.put("licencia",(String)solicitud.get("tipo"));						
						t1273dao.deleteByReference(prms);
						
						prms.clear();						
						prms.put("anno", (String)solicitud.get("anno"));
						prms.put("ffinicio", solicitud.get("ffinicio"));						
						prms.put("cod_pers", cod_pers);
						prms.put("licencia",(String)solicitud.get("tipo"));						
						bResult = t1274dao.deleteByReference(prms);
						log.debug("Anular LICENCIA_ENFERMEDAD(21): " + prms);
						
						
						bResult = true;//No hubo errores
						if(bResult){
							HashMap hmMensaje = new HashMap();
							hmMensaje.put("mensaje", "La licencia asociada a la solicitud "+solicitud.get("numero")+" fue anulada correctamente.");
							hmMensaje.put("tipo", "success");//info, warning, error, success
							
							listaMensajes.add(hmMensaje);
						}
						
						mensajes = modificarSeguimientoAdministracion(dbpool, (HashMap)solicitud, usuario); //actualizar ultimo seguimiento (todos) //ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta vacaciones (SC)
						
					}
					else if (tipo.trim().equals(Constantes.FERIADO_COMPENSABLE)){//38
						T1608DAO t1608dao = new T1608DAO(dbpool);
						String cod_pers = solicitud.get("codPers").toString();
						
						Map prms = new HashMap();
						
						prms.put("anno_ref", (String)solicitud.get("anno"));
						prms.put("numero_ref", (String)solicitud.get("numero"));						
						prms.put("cod_pers", cod_pers);
						prms.put("u_organ", (String)solicitud.get("uorgan"));
						prms.put("licencia",(String)solicitud.get("tipo"));						
						t1273dao.deleteByReference(prms);						
						
						prms.clear();
						prms.put("cod_personal", cod_pers);
						prms.put("anno", (String)solicitud.get("anno"));
						prms.put("num_refer", (String)solicitud.get("numero"));						
						bResult = t1608dao.eliminarCompensacionPersona(prms);
						
						log.debug("Anular FERIADO_COMPENSABLE(38): " + prms);
						
						bResult = true;//No hubo errores
						if(bResult){
							HashMap hmMensaje = new HashMap();
							hmMensaje.put("mensaje", "La licencia asociada a la solicitud "+solicitud.get("numero")+" fue anulada correctamente.");
							hmMensaje.put("tipo", "success");//info, warning, error, success
							
							listaMensajes.add(hmMensaje);
						}
						
						mensajes = modificarSeguimientoAdministracion(dbpool, (HashMap)solicitud, usuario); //actualizar ultimo seguimiento (todos) //ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta vacaciones (SC)
					}
					//ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta 
					else if (tipo.trim().equals(Constantes.LICENCIA_MATRIMONIO)){//23
						String cod_pers = solicitud.get("codPers").toString();
						
						//A. VALIDACIONES
						//1.1. Obtener fecha de ingreso
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
						T02DAO empleadoDAO = new T02DAO();	
						Map mapaEmpleado = empleadoDAO.findEmpleado(dbpool,cod_pers);	
						Date dteFechaIngreso = (mapaEmpleado!=null && !mapaEmpleado.isEmpty())?((Date)mapaEmpleado.get("t02f_ingsun")):null;
						String strFechaIngreso = sdf.format(dteFechaIngreso);
						
						//1.2. Obtener aï¿½o de trabajo
						String strFechaActual = Utiles.obtenerFechaActual();
						String strAnioActual = Utiles.dameAnho(strFechaActual);
						String strFechaCalculo = strFechaIngreso.substring(0, 6) + strAnioActual;
						String anioSaldoVacVig;
						int difDias = Utiles.obtenerDiasDiferencia(strFechaCalculo, strFechaActual);
						if (difDias >= 0) 
							anioSaldoVacVig = strAnioActual;						
						else 
							anioSaldoVacVig = (Integer.parseInt(strAnioActual) - 1) + ""; 
							log.debug("anioSaldoVacVig(Anulacion Sol-Admin Sol): "+anioSaldoVacVig); //ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta vacaciones (SC)
						int anioSaldoVacProx = Integer.parseInt(anioSaldoVacVig)+1;
						
						//1.3 Sumamos 5 dias a la fecha actual
						Date dteFechaActual = sdf.parse(strFechaActual);
						Date dteProxFechaGeneracion = sdf.parse(strFechaIngreso.substring(0, 6)+anioSaldoVacProx);
						
						//ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta vacaciones (SC)
						if (strFechaIngreso!=null && !strFechaIngreso.trim().equals("")){
							log.debug("fecha ingreso tiene valor");
							if (strFechaActual.substring(0, 6).trim().equals(strFechaIngreso.substring(0, 6).trim())){
								log.debug("fecha actual igual a fecha generacion (solo dias y mes)-(Anulacion Sol-Admin Sol)");
								dteProxFechaGeneracion = sdf.parse(strFechaIngreso.substring(0, 6)+anioSaldoVacVig);
							}	
						}																	
						//FIN ICAPUNAY
						
						Calendar calFechaActual = Calendar.getInstance();
						calFechaActual.setTime(dteFechaActual);
						calFechaActual.add(Calendar.DAY_OF_MONTH, 5);
						Date dteFechaActualAumentado = calFechaActual.getTime();
						log.debug("dteFechaActualAumentado: "+dteFechaActualAumentado);
						log.debug("dteProxFechaGeneracion: "+dteProxFechaGeneracion);
						
						if(dteFechaActualAumentado.after(dteProxFechaGeneracion)){
							HashMap hmMensaje = new HashMap();																
							hmMensaje.put("mensaje", "Solo es posible anular hasta 5 días antes de su fecha de generación (día y mes de fecha de ingreso) de saldo vacacional para la solicitud "+(String)solicitud.get("anno")+"-"+solicitud.get("numero")+" del registro "+cod_pers+".");
							hmMensaje.put("tipo", "error");
							listaMensajes.add(hmMensaje);
							
						}else{
							//B. TRANSACCIONES
							T1282DAO t1282dao = new T1282DAO(dbpool);
							pe.gob.sunat.rrhh.asistencia.dao.T1281DAO t1281dao = new pe.gob.sunat.rrhh.asistencia.dao.T1281DAO(dbpool);							
							
							Map vacacionEfec = (HashMap) solicitud.get("vacacionEfec");
						
							Map prms = new HashMap();
							Map cabVacacion = null;
							prms.put("cod_pers", cod_pers);
							//1.1 ACTUALIZAR SALDO (SUMAR 7 DIAS) DE Aï¿½O VACACIONAL DE LICENCIA MATRIMONIO APROB.						
							if (vacacionEfec!=null){
								prms.put("anno", vacacionEfec.get("anno_vac").toString().trim());							
								cabVacacion = t1281dao.findAllColumnsByKey(prms); //ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta 
								if(log.isDebugEnabled()) log.debug("cabVacacion CAS:"+ cabVacacion);
								
								if (cabVacacion!=null){
									
									int saldo = ((Integer)cabVacacion.get("saldo")).intValue();
		                            if (saldo==-7) {      
		                                t1281dao.deleteByPK(prms); //borrar registro generado                                   
		                            }else{
		                            	Map columns= new HashMap();
		    							columns.put("saldo", new Integer(Integer.parseInt(cabVacacion.get("saldo")+"") + 7  )); //se le suma 7 dias
		    							columns.put("cuser_mod",usuario);
		    				  		    columns.put("fmod",new Timestamp(System.currentTimeMillis()));
		    				  		    if (log.isDebugEnabled()) log.debug("columns: " + columns);
		    				  		    prms.put("columns", columns);
		    				  		    log.debug("Actualizar1(saldo) LICENCIA_MATRIMONIO(23): " + prms);
		    				  		    t1281dao.updateCustomColumns(prms);	
		                            }										
								}
							}					
							//

							//1.2 ELIMINAR VACACION PROGRAMADA Y EFECTUADA POR LICENCIA MATRIMONIO APROB
							prms.put("anno_ref", (String)solicitud.get("anno"));
							prms.put("numero_ref", (String)solicitud.get("numero"));
							prms.put("licencia",Constantes.VACACION_PROGRAMADA);//49
							prms.put("est_id1","2");						
							prms.put("licencia2",Constantes.VACACION);//07
							prms.put("est_id2",Constantes.ACTIVO);
							t1282dao.deleteVacacionesByLicByNroRefSolByMatrimonio(prms);
							log.debug("Anular1(del 07 y 49) LICENCIA_MATRIMONIO(23): " + prms);
							//							
							
							//1.3 ACTIVAR VACACIONES PROGRAMADAS INACTIVAS POR LICENCIA MATRIMONIO APROB						
								//0. verificar no existan nuevas vacaciones programadas no relacionadas a licencia matrimonio
								prms.put("licencia",Constantes.VACACION_PROGRAMADA);//49
								prms.put("estado",Constantes.ACTIVO);//1
								log.debug("nvacprog(prms): " + prms);
								List nvacprog = t1282dao.findVacacionesNoGeneradasPorLicenciaMatrimonio(prms);
								log.debug("nvacprog1: " + nvacprog);
								
								//1. verificar no existan nuevas vacaciones en solicitud no relacionadas a licencia matrimonio
								prms.put("licencia",Constantes.VACACION_PROGRAMADA);//49
								prms.put("estado","5");//vacacion programada en solicitud=5
								log.debug("nvacsol(prms): " + prms);
								List nvacsol = t1282dao.findVacacionesNoGeneradasPorLicenciaMatrimonio(prms);
								log.debug("nvacsol1: " + nvacsol);
								
								//2. verificar no existan nuevas vacaciones gozadas no relacionadas a licencia matrimonio
								prms.put("licencia",Constantes.VACACION);//07
								prms.put("estado",Constantes.ACTIVO);//1
								log.debug("nvacgoz(prms): " + prms);
								List nvacgoz = t1282dao.findVacacionesNoGeneradasPorLicenciaMatrimonio(prms);
								log.debug("nvacgoz1: " + nvacgoz);
								
								//3. se activa las vacac. programadas desactivas por la licencia matrimonio aprobada
								if ((nvacprog==null || nvacprog.isEmpty()) && (nvacsol==null || nvacsol.isEmpty()) && (nvacgoz==null || nvacgoz.isEmpty())){
									log.debug("entro todos null");
									prms.put("licencia",Constantes.VACACION_PROGRAMADA);//49
									prms.put("cuser_mod", usuario);
									prms.put("fmod",new Timestamp(System.currentTimeMillis()));
									prms.put("estado1", "1");
									prms.put("estado2", "0");
									bResult = t1282dao.updateEstadoTipo49ByNroRefSolByMatrimonio(prms);						
									log.debug("Actualizar2(activar 49) LICENCIA_MATRIMONIO(23): " + prms);
								}else{
									//3.1 Desactivamos flag de matrimonio
									prms.put("licencia",Constantes.VACACION_PROGRAMADA);//49
									prms.put("cuser_mod", usuario);
									prms.put("fmod",new Timestamp(System.currentTimeMillis()));
									prms.put("ind_matri", null);
									prms.put("est_id", "0");
									t1282dao.updateIndmatriTipo49ByNroRefSolByMatrimonio(prms);	
									
									HashMap hmMensaje3 = new HashMap();
									hmMensaje3.put("mensaje", "No se activaron las vacaciones programadas desactivadas por la solicitud "+(String)solicitud.get("anno")+"-"+solicitud.get("numero")+", ya que existen nuevas vacaciones programadas o gozadas.");
									hmMensaje3.put("tipo", "warning");//info, warning, error, success													
									listaMensajes.add(hmMensaje3);
								}
							//
							HashMap hmMensaje0 = new HashMap();
							hmMensaje0.put("mensaje", "La solicitud "+(String)solicitud.get("anno")+"-"+solicitud.get("numero")+" ha sido anulada correctamente.");
							hmMensaje0.put("tipo", "success");//info, warning, error, success													
							listaMensajes.add(hmMensaje0);
								
							HashMap hmMensaje1 = new HashMap();
							hmMensaje1.put("mensaje", "Los 7 dias de vacaciones efectuadas asociadas a la solicitud "+(String)solicitud.get("anno")+"-"+solicitud.get("numero")+" fueron anuladas correctamente.");
							hmMensaje1.put("tipo", "success");//info, warning, error, success													
							listaMensajes.add(hmMensaje1);
							//bResult = true;//No hubo errores
							if(bResult){							
								HashMap hmMensaje2 = new HashMap();
								hmMensaje2.put("mensaje", "Las vacaciones programadas desactivadas por la solicitud "+(String)solicitud.get("anno")+"-"+solicitud.get("numero")+" fueron activadas nuevamente con exito.");
								hmMensaje2.put("tipo", "success");//info, warning, error, success													
								listaMensajes.add(hmMensaje2);
							}
						}
						
						//ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta vacaciones (SC)
						if (!listaMensajes.isEmpty()){
							boolean swerror=false;
							log.debug("entro matri: listaMensajes no es vacio");
							log.debug("listaMensajes:"+listaMensajes);
							for (int m=0; m<listaMensajes.size();m++){
								HashMap sms = (HashMap)listaMensajes.get(m);
								String tipoerror = (String) sms.get("tipo");
								log.debug("tipoerror:"+tipoerror);
								if (tipoerror.trim().equals("error")){
									log.debug("encontro error");
									swerror=true;
									break;
								}
							}
							log.debug("swerror:"+swerror);
							if (swerror==false){								
								mensajes = modificarSeguimientoAdministracion(dbpool, (HashMap)solicitud, usuario);
								log.debug("actualizo seguimiento matri");
							}							 
						}						 
						//FIN ICAPUNAY
					}
					//FIN ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta 
				}
			}

		} catch (Exception e) {
			HashMap hmMensaje = new HashMap();
			hmMensaje.put("mensaje", "No se pudo actualizar la solicitud/licencia, verifique los datos y vuelva a intentarlo.");
			hmMensaje.put("tipo", "error");//info, warning, error, success
			
			listaMensajes.add(hmMensaje);
			
			log.error(e);
			beanM.setMensajeerror(e.toString());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		mapa.put("listaMensajes", listaMensajes);
		
		return mensajes;
	}

/*	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
/*	
	public ArrayList rechazarSolicitud(String dbpool, HashMap solicitud,
			HashMap mapa, String usuario) throws IncompleteConversationalState,
			RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		ArrayList mensajes = new ArrayList();
		try {
			//log.debug("metodo rechazar mapa : "+mapa+"");
			T02DAO personalDAO = new T02DAO();
			CorreoDAO correoDAO = new CorreoDAO();
			T1279DAO movDAO = new T1279DAO();

			String tipo = solicitud.get("tipo") != null ? ((String) solicitud
					.get("tipo")).trim() : "";
			HashMap tipoMov = movDAO.findByMov(dbpool, tipo);

			mapa.put("estado", Constantes.ESTADO_CONCLUIDA);
			mapa.put("userDest", (String) solicitud.get("codPers"));
			mensajes = registrarSeguimiento(dbpool, solicitud, mapa, usuario);
			
			
			Map param1 = new HashMap();
			param1.put("cod_pers", (String) solicitud.get("codPers"));
			param1.put("licencia", Constantes.VACACION_PROGRAMADA);
			param1.put("ffinicio", solicitud.get("ffinicio"));
			param1.put("anno_vac", (String) solicitud.get("annoVac"));
			param1.put("estado_id", constantes.leePropiedad("PROGRAMACION_EN_SOLICITUD"));
			param1.put("fmod", new FechaBean().getTimestamp());
			param1.put("cuser_mod", usuario);
			param1.put("estado_new", Constantes.PROG_ACEPTADA);
			
			T1282DAO VacacionDAO = new T1282DAO(dbpool);
			VacacionDAO.actualizaProgramacion(param1);
			
			//log.debug("metodo rechazarSolicitud, despues de registrarSeguiminto solicitud : "+solicitud+"");
			if (mensajes.isEmpty()) {

				String origen = (String) mapa.get("userOrig");
				String destino = (String) mapa.get("userDest");
				String tipoSol = (String) tipoMov.get("descrip");
				//prac-asanchez
				String fechainicio = Utiles.timeToFecha((java.sql.Timestamp)solicitud.get("ffinicio"));
				String fechafin = Utiles.timeToFecha((java.sql.Timestamp)solicitud.get("ffin"));
				//
				String observacion = (mapa.get("txtObs") != null ) ? (String)mapa.get("txtObs"):"";
				int fincad = observacion.indexOf("&");
				observacion = (fincad>0) ? observacion.substring(0,fincad):"";
				//falta comprobar si solo envia observacion o algun valor concatenado mas
				
				//prac-asanchez
				String mensaje = "Su solicitud electr&oacute;nica <strong>"+(String) solicitud.get("anno")
					+"-"+(String) solicitud.get("numero")+" "+tipoSol+ "</strong>, con fecha de inicio: " +
					fechainicio + " y fecha fin: " + fechafin + ", ha sido Rechazada.<br><br>"
					+((observacion.length()>0) ? "<strong>Obs: </strong>"+observacion: "");
				//
//EBV 30/03/2006 
				/*ResourceBundle bundle = ResourceBundle.getBundle("pe.gob.sunat.sp.asistencia.sirh");
				String servidorIP = bundle.getString("servidorIP");
				String strURL = "http://"+servidorIP+"/ol-ti-iaasistencia/asisS13Alias";
				String paramsURL = "accion=verSolicitud&codPers=" + destino
					+ "&anno=" + (String) solicitud.get("anno")
					+ "&numero=" + (String) solicitud.get("numero");
				String programa = bundle.getString("programa2");
				Cifrador cifrador = new Cifrador();
				String url = cifrador.encriptaURL(destino, programa, strURL, paramsURL);*/
				
/*				
				String nombre = personalDAO.findNombreCompletoByCodPers(dbpool, destino);
				String texto = Utiles.textoCorreoProceso(dbpool, nombre, mensaje,"");

				//enviamos el mail al trabajador
				HashMap datos = new HashMap();
				datos.put("subject", "Resultado de Solicitud");
				datos.put("message", texto);
				datos.put("from", correoDAO.findCorreoByCodPers(dbpool,
						origen));
				datos.put("to", correoDAO
						.findCorreoByCodPers(dbpool, destino));

				QueueDAO queue = new QueueDAO();
				queue.enviaCorreo(datos);
			}

		} catch (Exception e) {
			log.error(e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return mensajes;
	}
*/

/* JRR - 21/05/2010 - PROGRAMACION COMSA */
	/**
	 * Metodo encargado de rechazar las solicitudes 
	 * @param parametros
	 * @return List
	 * @throws FacadeException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public List rechazarSolicitud(Map parametros)  throws FacadeException {

		MensajeBean beanM = new MensajeBean();
		List mensajes = new ArrayList();
		//String dbpool="jdbc/dgsp";//--->pool de conexion
		String dbpool = ServiceLocator.getInstance().getString("java:comp/env/dcsp");
		String dbpool_g = ServiceLocator.getInstance().getString("java:comp/env/dgsp");

		try {
			
			Map solicitud= (HashMap)parametros.get("paramSolicitud");
			Map mapa= (HashMap)parametros.get("paramMapa");
			String usuario=parametros.get("paramUsuario")!=null? (String)parametros.get("paramUsuario"):"";
			
			T02DAO personalDAO = new T02DAO();
			CorreoDAO correoDAO = new CorreoDAO();
			T1279DAO movDAO = new T1279DAO();
			T1282DAO t1282dao=new T1282DAO(dbpool_g);
			T4821DAO t4821dao = new T4821DAO(dbpool);//ICAPUNAY 05/10/2012 AJUSTES AOM 06A4T11 LABOR EXCEPCIONAL

			String tipo = solicitud.get("tipo") != null ? ((String) solicitud
					.get("tipo")).trim() : "";
			Map tipoMov = movDAO.findByMov(dbpool, tipo);

			mapa.put("estado", Constantes.ESTADO_CONCLUIDA);
			mapa.put("userDest", (String) solicitud.get("codPers"));
			
			mensajes = registrarSeguimiento(dbpool, (HashMap)solicitud, (HashMap)mapa, usuario);

			if (mensajes.isEmpty()) {
				
				//EBV 19/02/2009 SOlo debe verificar la fecha de la solicitud
				//if(solicitud.get("periodo")!=null && solicitud.get("ffinicio")!=null){
				if(log.isDebugEnabled()) log.debug("	SOLICITUD ANTES DE VALIDAR "+solicitud);
				if( solicitud.get("ffinicio")!=null && Constantes.TIPO_MOV_VACACIONES.equals((String) tipoMov.get("tipo_id"))){  //jquispecoi 03/2014
					solicitud.put("cod_pers",solicitud.get("codPers"));
					solicitud.put("licencia",Constantes.VACACION_PROGRAMADA);
					Map columns = new HashMap();
					columns.put("est_id",constantes.leePropiedad("PROGRAMACION_ACEPTADA"));// cuando se rechaza se coloca en su estado original 
		  		columns.put("cuser_mod",usuario);
		  		columns.put("fmod",new FechaBean().getTimestamp());
		  		solicitud.put("columns",columns);
		  		
		  		t1282dao.updateCustomColumnsSinPeriodo(solicitud);
					
				}			
				
				solicitud.put("estid",Constantes.ACTIVO);
				solicitud.put("annoVac",solicitud.get("annoVac"));
				
				String origen = (String) mapa.get("userOrig");
				String destino = (String) mapa.get("userDest");
				String tipoSol = (String) tipoMov.get("descrip");
				//prac-asanchez
				String fechainicio = Utiles.timeToFecha((java.sql.Timestamp)solicitud.get("ffinicio"));
				String fechafin = Utiles.timeToFecha((java.sql.Timestamp)solicitud.get("ffin"));
				//				
				String observacion = (mapa.get("txtObs") != null ) ? (String)mapa.get("txtObs"):"";
				log.debug("rechazo(observacion1): "+observacion);
				int fincad = observacion.indexOf("&");
				observacion = (fincad>0) ? observacion.substring(0,fincad):"";				
				log.debug("rechazo(observacion2): "+observacion);
				//falta comprobar si solo envia observacion o algun valor concatenado mas
				//prac-asanchez
				String mensaje = "Su solicitud electr&oacute;nica <strong>"+(String) solicitud.get("anno")
				+"-"+(String) solicitud.get("numero")+" "+tipoSol+ "</strong>";			
				if (!Constantes.MOV_LABOR_EXCEPCIONAL.equals(tipo)){//otros tipos solicitudes
					//add 23/11/2012 cambios glosas
					if (Constantes.MOV_SOLICITUD_COMPENSACION.equals(tipo)){//compensacion 125
						mensaje = "Su papeleta electr&oacute;nica <strong>"+"N° "+(String) solicitud.get("anno")
						+"-"+(String) solicitud.get("numero")+" "+ tipoSol + "</strong>";
						log.debug("rechazo(mensaje1): "+mensaje);
					}
					//add 23/11/2012 cambios glosas
					mensaje = mensaje + ", con fecha de inicio: " + fechainicio + " y fecha fin: " + fechafin; 
					mensaje = mensaje + ", ha sido <strong>Rechazada.</strong><br><br>" +((observacion.length()>0) ? "<strong>Obs: </strong>"+observacion: "");
					log.debug("rechazo otras solicit(mensaje): "+mensaje);
				}
				//ICAPUNAY 05/10/2012 AJUSTES AOM 06A4T11 LABOR EXCEPCIONAL
				else{//labor excepcional (124)
					String textoFechas = "";
					String tipoLabor = solicitud.get("estadoSol")!=null?solicitud.get("estadoSol").toString().trim():"";
					if(log.isDebugEnabled()) log.debug("rechazo(tipoLabor): "+ tipoLabor);
					if (tipoLabor.equals("1")){//generada x colaborador	
						log.debug("rechazo(generada x colaborador)");
						List fechas = t4821dao.findFechasPermiso(destino,(String) solicitud.get("anno"),(String) solicitud.get("numero"));
						if (fechas!=null && fechas.size()>0){
							textoFechas = "por el ";
							for (int m=0; m<fechas.size();m++){
								HashMap fecha = (HashMap)fechas.get(m);
								if (fechas.size()==1){
									textoFechas = textoFechas + fecha.get("fec_permiso_desc") ;
								}else{
									if (m==fechas.size()-1){//ultimo va "y"
										textoFechas = textoFechas + fecha.get("fec_permiso_desc") ;
									}else{
										if (m==fechas.size()-2){
											textoFechas = textoFechas + fecha.get("fec_permiso_desc")+" y " ;
										}else{
											textoFechas = textoFechas + fecha.get("fec_permiso_desc")+" , " ;
										}										
									}
								}								
							}
						}							
						//mensaje = "Se le comunica que usted registra marcaciones fuera del ingreso y salida "+textoFechas+" que no constituye(n) labor excepcional, por lo que la solicitud <strong>"+"N° "+(String) solicitud.get("anno")
						//+"-"+(String) solicitud.get("numero")+ " " +tipoSol + "</strong> ha sido Rechazada.<br><br>" + ((observacion.length()>0) ? "<strong>Obs: </strong>"+observacion: "");
						mensaje = "Se le comunica que usted registra marcaciones fuera del ingreso y salida "+textoFechas+" que no constituye(n) labor excepcional, por lo que la <strong> PAPELETA DE "+tipoSol+" N° "+(String) solicitud.get("anno")
						+"-"+(String) solicitud.get("numero")+ "</strong> ha sido <strong>Rechazada.</strong><br><br>" + ((observacion.length()>0) ? "<strong>Obs: </strong>"+observacion: "");
					}
					if (tipoLabor.equals("2")){//autogenerada x sistema
						log.debug("rechazo(autogenerada x sistema)");
						textoFechas = "por el "+ fechainicio;
						//mensaje = "Se le comunica que usted registra marcaciones fuera del ingreso y salida "+textoFechas+" que no constituye(n) labor excepcional, por lo que la solicitud <strong>"+"N°"+(String) solicitud.get("anno")
						//+"-"+(String) solicitud.get("numero")+ " AUTOGENERADA DE " +tipoSol + "</strong> ha sido Rechazada.<br><br>" + ((observacion.length()>0) ? "<strong>Obs: </strong>"+observacion: "");
						mensaje = "Se le comunica que usted registra marcaciones fuera del ingreso y salida "+textoFechas+" que no constituye(n) labor excepcional, por lo que la <strong> PAPELETA AUTOGENERADA DE "+tipoSol+" N° "+(String) solicitud.get("anno")
						+"-"+(String) solicitud.get("numero")+ "</strong> ha sido <strong>Rechazada.</strong><br><br>" + ((observacion.length()>0) ? "<strong>Obs: </strong>"+observacion: "");
					}
					log.debug("rechazo(textoFechas): "+textoFechas);					
				}
				log.debug("rechazo(mensaje final): "+mensaje);
				//mensaje = mensaje + ", ha sido Rechazada.<br><br>" +((observacion.length()>0) ? "<strong>Obs: </strong>"+observacion: "");
				//FIN ICAPUNAY 05/10/2012 AJUSTES AOM 06A4T11 LABOR EXCEPCIONAL
				//
				//EBV 30/03/2006 
				String nombre = personalDAO.findNombreCompletoByCodPers(dbpool, destino);
				//ICAPUNAY 05/10/2012 AJUSTES AOM 06A4T11 LABOR EXCEPCIONAL
				if (Constantes.MOV_LABOR_EXCEPCIONAL.equals(tipo) || Constantes.MOV_SOLICITUD_COMPENSACION.equals(tipo) ){//124 o 125					
					nombre = solicitud.get("codPers").toString().trim()+" - "+nombre;
				}
				//ICAPUNAY 05/10/2012 AJUSTES AOM 06A4T11 LABOR EXCEPCIONAL
				String texto = "";//add 23/11/2012 cambios glosas					
				//String texto = Utiles.textoCorreoProceso(dbpool, nombre, mensaje,"");//add 23/11/2012 cambios glosas

				//enviamos el mail al trabajador
				Map datos = new HashMap();
				//ICAPUNAY 05/10/2012 AJUSTES AOM 06A4T11 LABOR EXCEPCIONAL
				if (tipo.equals(Constantes.MOV_LABOR_EXCEPCIONAL) || tipo.equals(Constantes.MOV_SOLICITUD_COMPENSACION) ){//124  o 125
					datos.put("subject", "Resultado de Papeleta");					
					if (tipo.equals(Constantes.MOV_LABOR_EXCEPCIONAL)){
						texto = Utiles.textoCorreoProcesoLaborExcepcional(dbpool, nombre, mensaje,"","rechazada");//add 23/11/2012 cambios glosas
					}
					if (tipo.equals(Constantes.MOV_SOLICITUD_COMPENSACION)){
						texto = Utiles.textoCorreoProceso(dbpool, nombre, mensaje,"");//add 23/11/2012 cambios glosas
					}
				}else{//otros tipos solicitudes
					datos.put("subject", "Resultado de Solicitud");
					texto = Utiles.textoCorreoProceso(dbpool, nombre, mensaje,"");//add 23/11/2012 cambios glosas
				}
				
				//PAS20171U230200001 - solicitud de reintegro   , Enviar notificaciones
				if (Constantes.MOV_REINTEGRO_POR_DESCUENTO_ASISTENCIA.equals(tipo)|| Constantes.MOV_REINTEGRO_POR_DESCUENTO_SUBSIDIO.equals(tipo)) {					
					enviarNotificacionRechazoReintegro( solicitud, mapa,  observacion );  
					 
				}else{//otros tipos de solicitud 
				//datos.put("subject", "Resultado de Solicitud");
				//ICAPUNAY 05/10/2012 AJUSTES AOM 06A4T11 LABOR EXCEPCIONAL
				datos.put("message", texto);
				datos.put("from", correoDAO.findCorreoByCodPers(dbpool,origen));
				datos.put("to", correoDAO.findCorreoByCodPers(dbpool, destino));
				QueueDAO queue = new QueueDAO();
				queue.enviaCorreo((HashMap)datos);
				} //PAS20171U230200001 - solicitud de reintegro  
				 
			}

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

		return mensajes;
	}
	
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public ArrayList derivarRRHHSolicitud(String dbpool, HashMap solicitud,
			HashMap mapa, HashMap aprobador, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		ArrayList mensajes = new ArrayList();
		try {

			//registramos el seguimiento
			String rrhh = (String) solicitud.get("rrhh");
			String userAprob = (String) aprobador.get("aprobador");
			
			T12DAO uoDAO = new T12DAO();
			String userAprobRRHH = "";
			HashMap flujo = (HashMap) aprobador.get("flujoRRHH");
			if (flujo!=null && !flujo.isEmpty()){
				
				HashMap aux = new HashMap();
				aux.put("dbpool",dbpool);
				aux.put("codUO",(String)aprobador.get("UOAprobadorOrigen"));
				aux.put("codOpcion",Constantes.DELEGA_SOLICITUDES);
				HashMap delegado = uoDAO.findDelegado(aux);
				
				if (delegado!=null && !delegado.isEmpty()){
					String deleg = (String)delegado.get("t02cod_pers");
					if (userAprob.trim().equalsIgnoreCase(deleg.trim())){
						userAprob = (String)delegado.get("cod_jefe");
					}
				}
				
				HashMap acciones = (HashMap)flujo.get(userAprob);
				if (acciones!=null && !acciones.isEmpty()){
					HashMap datos = (HashMap)acciones.get(Constantes.ACCION_APROBAR);
					userAprobRRHH = (String) datos.get("destino");	
				}				
			}

			if (userAprob != null && !userAprob.equals("") ) {
				if (rrhh.equals(Constantes.ACTIVO)) {
					if (userAprobRRHH != null && !userAprobRRHH.trim().equals("")) {
						mapa.put("estado", Constantes.ESTADO_SEGUIMIENTO);
						mapa.put("userDest", userAprobRRHH);
						mensajes = registrarSeguimiento(dbpool, solicitud,mapa, usuario);
					} else {
						mensajes.add("No se ha registrado usuario de RR.HH. para derivar este tipo de solicitud.");
					}
				} else {
					mensajes.add("Esta solicitud no corresponde ser derivada a RR.HH.");
				}
			} else {
				mensajes.add("No se ha registrado usuario para aprobar este tipo de solicitudes.");
			}

		} catch (Exception e) {
			log.error(e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return mensajes;
	}

	/* *
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
/*	public ArrayList verificarAprobarSolicitud(String dbpool,
			HashMap solicitud, HashMap mapa, HashMap aprobador, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		ArrayList mensajes = new ArrayList();
		try {
			
			if(log.isDebugEnabled()) {
				log.debug("Llego a verificarAprobarSolicitud ");			
				log.debug("solicitud : "+solicitud);
				log.debug("mapa : "+mapa);
				log.debug("aprobador : "+aprobador);
			}
			
			//	EBV 12/02/2007
			HashMap pp = new HashMap();
			pp.put("dbpool", dbpool);
			pp.put("codUO",(String)solicitud.get("uorgan"));
			pp.put("mov",(String)solicitud.get("tipo"));
			//pp.put("cod_pers_ori",(String)mapa.get("userOrig"));
			pp.put("cod_pers_ori",(String)solicitud.get("userDest"));

			
			T1933DAO supDAO = new T1933DAO();
			boolean supervisor = supDAO.esSupervisor(dbpool, (String)solicitud.get("userDest"), (String) solicitud.get("tipo"),(String)solicitud.get("uorgan"));
			if(log.isDebugEnabled()) log.debug("ES SUPERVISOR : "+ supervisor);
			T1480DAO flujoDAO = new T1480DAO();
			if (supervisor){
				HashMap aux = new HashMap();
				T12DAO uoDAO = new T12DAO();
				aux.put("dbpool",dbpool);
				aux.put("codUO",(String)aprobador.get("uo"));
				aux.put("codOpcion",Constantes.DELEGA_SOLICITUDES);
				
				log.debug("Buscando delegado Flujo RRHH "+aux);
				HashMap delegado = uoDAO.findDelegado(aux);
				log.debug("Delegado : "+delegado);
				if (delegado!=null && !delegado.isEmpty()){
					//String deleg = (String)delegado.get("t02cod_pers");
					//String userOrig = (String)pp.get("cod_pers_ori");
					//if (userOrig.trim().equalsIgnoreCase(deleg.trim())){
						pp.put("cod_pers_ori", (String)delegado.get("cod_jefe"));
					//}
				}
				ArrayList datospp1 = flujoDAO.findAprobadores(pp);
				String jefeSup = (String)mapa.get("codJefe");
				if (jefeSup != null && datospp1.size()==0){
					pp.put("cod_pers_ori",jefeSup);
				}
			} else {
				HashMap aux = new HashMap();
				T12DAO uoDAO = new T12DAO();
				aux.put("dbpool",dbpool);
				aux.put("codUO",(String)aprobador.get("uo"));
				aux.put("codOpcion",Constantes.DELEGA_SOLICITUDES);
				
				log.debug("Buscando delegado Flujo RRHH "+aux);
				HashMap delegado = uoDAO.findDelegado(aux);
				log.debug("Delegado : "+delegado);
				
				if (delegado!=null && !delegado.isEmpty()){
					String deleg = (String)delegado.get("t02cod_pers");
					String userOrig = (String)pp.get("cod_pers_ori");
					if (userOrig.trim().equalsIgnoreCase(deleg.trim())){
						pp.put("cod_pers_ori", (String)delegado.get("cod_jefe"));
					}
				}
				String jefeDel = (String)mapa.get("jefedelego");
				if (jefeDel != null){
					pp.put("cod_pers_ori",jefeDel);
				}
			}
			log.debug("pp : "+ pp);

			
			ArrayList datospp = flujoDAO.findAprobadores(pp);
			log.debug("MAPA ENCONTRAR APROBADORES : "+ datospp);
			
			if (datospp.size() == 0)   {
				mensajes.add("La solicitud no puede ser atendida por error en la configuracion. Favor coordine con su Analista.");
			}
			else{
				
			
			//EBV 12/02/2007
			
			T12DAO uoDAO = new T12DAO();
			
			String userOrig = (String) mapa.get("userOrig");
			String userAprobador = (String) aprobador.get("aprobador");
			String userAprobadorOriginal = (String) aprobador.get("aprobadorOriginal");
						
			String userAprobRRHH = "";
			String estacion = "";			
			HashMap flujoRRHH = (HashMap) aprobador.get("flujoRRHH");
			
			log.debug("FlujoRRHH : "+flujoRRHH);
			
			if (flujoRRHH!=null && !flujoRRHH.isEmpty()){
				
				HashMap aux = new HashMap();
				aux.put("dbpool",dbpool);
				aux.put("codUO",(String)aprobador.get("uo"));
				aux.put("codOpcion",Constantes.DELEGA_SOLICITUDES);
				
				log.debug("Buscando delegado Flujo RRHH "+aux);
				HashMap delegado = uoDAO.findDelegado(aux);
				log.debug("Delegado : "+delegado);
				
				if (delegado!=null && !delegado.isEmpty()){
					String deleg = (String)delegado.get("t02cod_pers");
					if (userOrig.trim().equalsIgnoreCase(deleg.trim())){
						userOrig = (String)delegado.get("cod_jefe");
					}
				}
				
				HashMap acciones = (HashMap)flujoRRHH.get(userOrig);
				if (acciones!=null && !acciones.isEmpty()){
					HashMap datos = (HashMap)acciones.get(Constantes.ACCION_APROBAR);
					estacion = (String) datos.get("estado");
					userAprobRRHH = (String) datos.get("destino");	
				}					
			}
			
			String superior = (String) mapa.get("codJefe");
			String rrhh = (String) solicitud.get("rrhh");

			log.debug("superior codJefe : "+superior);

			//EBV 17/11/2006 para el caso que sea Instancia UNICA
			if (estacion.equals(Constantes.ESTACION_UNICA)){
				log.debug("ENTRE A CAMBIAR ESTACION UNICA POR ESTACION FINAL");
				estacion = Constantes.ESTACION_FINAL;
			}

			//para el caso que el aprobador final de la jerarquia tenga delegado
			if (estacion.equals(Constantes.ESTACION_FINAL)){
				superior = superior.equals(userAprobadorOriginal)?userAprobador:superior;
			}

			
			log.debug("userOrig : "+userOrig);
			log.debug("userAprobador : "+userAprobador);
			log.debug("userAprobadorOriginal : "+userAprobadorOriginal);
			log.debug("userAprobRRHH : "+userAprobRRHH);
			log.debug("superior final : "+superior);
			log.debug("estacion : "+ estacion);
			
			if (userAprobador != null && !userAprobador.trim().equals("")) {
				//si la solicitud debe ser aprobada por RRHH
				if (rrhh.equals(Constantes.ACTIVO)) {
					//si existe un usuario aprobador en RRHH
					if (estacion.equals(Constantes.ESTACION_FINAL) || 
						(userAprobRRHH != null && !userAprobRRHH.trim().equals(""))) {
						
						log.debug("Estacion Final o userAprobRRHH!=null");
						
						//si es la estacion final
						if (estacion.equals(Constantes.ESTACION_FINAL)){							
							mensajes = aprobarSolicitud(dbpool, solicitud, mapa, aprobador, usuario);
						} 
						//si el responsable actual es el usuario aprobador de la unidad
						else if (userAprobRRHH!=null && !userAprobRRHH.trim().equals("")) {
							
							mapa.put("estado", Constantes.ESTADO_SEGUIMIENTO);
							mapa.put("userDest", userAprobRRHH);
							mensajes = registrarSeguimiento(dbpool, solicitud, mapa, usuario);
						}
						//si el responsable actual es un usuario de la UUOO origen						
						else {
							mapa.put("estado", Constantes.ESTADO_SEGUIMIENTO);
							if (superior != null && !superior.trim().equals("")) {
								mapa.put("userDest", superior);
								mensajes = registrarSeguimiento(dbpool,solicitud, mapa, usuario);
							} else {
								mensajes.add("No Existe Jefe o encargado a quien dirigir la solicitud.");
							}
							
						}

					} else {
						
						log.debug("entro al !responsable.equalsIgnoreCase(userAprobador) ");
						
						if (!userOrig.equalsIgnoreCase(userAprobador)) {
							//si el responsable actual es un usuario de la UUOO origen
							mapa.put("estado", Constantes.ESTADO_SEGUIMIENTO);
							if (superior != null && !superior.trim().equals("")) {
								mapa.put("userDest", superior);
								mensajes = registrarSeguimiento(dbpool,solicitud, mapa, usuario);
							} else {
								mensajes.add("No Existe Jefe o encargado a quien dirigir la solicitud.");
							}
							
							
						}
						else{
							mensajes.add("No se ha registrado usuario de RR.HH. para derivar este tipo de solicitud.");
						}
					}
				}
				//la solicitud no debe ser aprobada por RRHH
				else {
					String tipo = (String) solicitud.get("tipo");
					if (tipo.trim().equals(Constantes.LICENCIA_LABOR_EXCEPCIONAL)){
						userAprobador = userOrig;
					}
					//si el responsable es el usuario aprobador
					if (userOrig.equalsIgnoreCase(userAprobador)) {
						//aprobamos la solicitud
						mensajes = aprobarSolicitud(dbpool, solicitud, mapa, aprobador, usuario);
					} else {
						//derivamos la solicitud al superior
						mapa.put("estado", Constantes.ESTADO_SEGUIMIENTO);
						if (superior != null && !superior.trim().equals("")) {
							mapa.put("userDest", superior);
							mensajes = registrarSeguimiento(dbpool, solicitud, mapa, usuario);
						} else {
							mensajes.add("No Existe Jefe o encargado a quien dirigir la solicitud.");
						}
						
					}
				}
			} else {
				String tipo = (String) solicitud.get("tipo");
				if (tipo.trim().equals(Constantes.LICENCIA_LABOR_EXCEPCIONAL)){
					mensajes = aprobarSolicitud(dbpool, solicitud, mapa, aprobador, usuario);
				}
				else
				{
					mensajes.add("No se ha registrado usuario para aprobar este tipo de solicitudes.");
				}
			}
			}
			
		} catch (Exception e) {
			log.error(e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return mensajes;
	}
*/


/* METODO MODIFICADO POR COMSA - 07/06/2010 */
/**
 * Metodo encargado de verificar la AprobaciÃ³n de solicitud
 * @param dbpool, solicitud, mapa, aprobador, usuario
 * @return ArrayList
 * @throws RemoteException
 *   
 * @ejb.interface-method view-type="remote"
 * @ejb.transaction type="NotSupported"
 */
public ArrayList verificarAprobarSolicitud(String dbpool,
		HashMap solicitud, HashMap mapa, HashMap aprobador, String usuario)
		throws IncompleteConversationalState, RemoteException {

	BeanMensaje beanM = new BeanMensaje();
	ArrayList mensajes = new ArrayList();
	try {
		
		if(log.isDebugEnabled()) {
			log.debug("Llego a verificarAprobarSolicitud ");			
			log.debug("solicitud : "+solicitud);
			log.debug("mapa : "+mapa);
			log.debug("aprobador : "+aprobador);
		}
		
		//ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
		T1455DAO segDAO = new T1455DAO(dbpool);//05/05/2016
		T12DAO uoDAO = new T12DAO();
		T1480DAO flujoDAO = new T1480DAO();
		String userOrig = "";	
		
		//buscar delegado en uo superior a uo solicitante
		HashMap hmUniOrg = uoDAO.findByCodUorga(dbpool,aprobador.get("uo")!=null?(aprobador.get("uo")).toString().trim():"");
		String t12cod_repor= (hmUniOrg!=null && !hmUniOrg.isEmpty())?!hmUniOrg.get("t12cod_repor").equals("-")?hmUniOrg.get("t12cod_repor").toString().trim():"":"";
		HashMap aux1 = new HashMap();
		aux1.put("dbpool",dbpool);
		aux1.put("codUO",t12cod_repor);
		aux1.put("codOpcion",Constantes.DELEGA_SOLICITUDES);				
		if(log.isDebugEnabled()) log.debug("Buscando delegado uuoo superior ICR ("+t12cod_repor+"):"+aux1);
		HashMap delegadoUOSup = uoDAO.findDelegado(aux1);
		if(log.isDebugEnabled()) log.debug("delegadoUOSup ICR: "+delegadoUOSup);
		String delegUOSup = (delegadoUOSup!=null && !delegadoUOSup.isEmpty())?((String)delegadoUOSup.get("t02cod_pers")).trim():"";	
		if(log.isDebugEnabled()) log.debug("delegUOSup ICR: "+delegUOSup);
		
		//buscar nro instancias para el tipo solicitud de la uo solicitante
		HashMap qq = new HashMap();
		qq.put("dbpool", dbpool);
		qq.put("u_organ",((String)solicitud.get("uorgan")).trim());
		qq.put("mov",((String)solicitud.get("tipo")).trim());
		log.debug("qq (ICR) : "+qq);		
		ArrayList instancias = flujoDAO.findInstanciasBySolByUO(qq);
		log.debug("instancias (ICRR) : "+instancias);
		int nroInstancias = (instancias!=null && !instancias.isEmpty())?instancias.size():0;
		log.debug("nroInstancias (ICRR) : "+nroInstancias);
		
		
		//parametros para buscar si userOrig existe en un seguimiento de la solicitud como origen y destino 05/05/2016
		Map prms = new HashMap();
		prms.put("estado_id", constantes.leePropiedad("ESTADO_SEGUIMIENTO"));
		prms.put("anno", solicitud.get("anno").toString().trim());
		prms.put("numero", new Integer(((String) solicitud.get("numero")).trim()));
		boolean isOrigenDestino=false; //para determinar si delegado existe en un seguimiento de la solicitud como origen y destino
		
		//obteniendo el jefe de la uo solicitante 05/05/2016
		String jefeUO = uoDAO.findJefeByUO(dbpool,aprobador.get("uo")!=null?(aprobador.get("uo")).toString().trim():"");
		
		//obteniendo el jefe de la uo superior de solicitante 05/05/2016
		String jefeUOsup = uoDAO.findJefeByUO(dbpool,t12cod_repor);
		
		String tipo = (String) solicitud.get("tipo"); //05/05/2016
		String uuooColab= aprobador.get("uo")!=null?(aprobador.get("uo")).toString().trim():""; //05/05/2016
		//ICAPUNAY - PAS20165E230300005
		
		//	EBV 12/02/2007
		HashMap pp = new HashMap();
		pp.put("dbpool", dbpool);
		pp.put("codUO",(String)solicitud.get("uorgan"));
		pp.put("mov",(String)solicitud.get("tipo"));
		//pp.put("cod_pers_ori",(String)mapa.get("userOrig"));
		pp.put("cod_pers_ori",(String)solicitud.get("userDest"));
		log.debug("pp (ICR) : "+pp);
		
		T1933DAO supDAO = new T1933DAO();
		boolean supervisor = supDAO.esSupervisor(dbpool, (String)solicitud.get("userDest"), (String) solicitud.get("tipo"),(String)solicitud.get("uorgan"));
		if(log.isDebugEnabled()) log.debug("ES SUPERVISOR : "+ supervisor);
		
		if (supervisor){
			log.debug("SI HAY supervisor: "+supervisor);
			HashMap aux = new HashMap();
			//T12DAO uoDAO = new T12DAO();
			aux.put("dbpool",dbpool);
			aux.put("codUO",(String)aprobador.get("uo"));
			aux.put("codOpcion",Constantes.DELEGA_SOLICITUDES);			
			log.debug("Buscando delegado Flujo RRHH (supervisor=true) "+aux);
			
			HashMap delegado = uoDAO.findDelegado(aux);
			if(log.isDebugEnabled()) {
			log.debug("Delegado : "+delegado);
			}
			if (delegado!=null && !delegado.isEmpty()){
				log.debug("entro");
				//String deleg = (String)delegado.get("t02cod_pers");
				//String userOrig = (String)pp.get("cod_pers_ori");
				//if (userOrig.trim().equalsIgnoreCase(deleg.trim())){
					pp.put("cod_pers_ori", (String)delegado.get("cod_jefe"));
				//}
			}
			ArrayList datospp1 = flujoDAO.findAprobadores(pp);
			log.debug("datospp1 : "+datospp1);
			String jefeSup = (String)mapa.get("codJefe");
			log.debug("jefeSup : "+jefeSup);
			if (jefeSup != null && datospp1.size()==0){
				pp.put("cod_pers_ori",jefeSup);
			}
			if(log.isDebugEnabled()) log.debug("pp (cod_pers_ori) : "+ pp);
		} else {
			log.debug("NO HAY supervisor: "+supervisor);
			HashMap aux = new HashMap();
			//T12DAO uoDAO = new T12DAO();
			aux.put("dbpool",dbpool);
			aux.put("codUO",(String)aprobador.get("uo"));
			aux.put("codOpcion",Constantes.DELEGA_SOLICITUDES);
			
			if(log.isDebugEnabled()) log.debug("Buscando delegado Flujo RRHH (supervisor=false) "+aux);
			HashMap delegado = uoDAO.findDelegado(aux);
			if(log.isDebugEnabled()) log.debug("Delegado : "+delegado);
			
			//ICAPUNAY - PAS20165E230300005 05/05/2016
			userOrig = (String)pp.get("cod_pers_ori");
			prms.put("cuser_orig", userOrig.trim());
			isOrigenDestino = segDAO.ExisteOrigenIgualDestino(prms);
			if(log.isDebugEnabled()) log.debug("isOrigenDestino1 : "+isOrigenDestino);
			//FIN ICAPUNAY
			
			//if (delegado!=null && !delegado.isEmpty()){ //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
			if (delegado!=null && !delegado.isEmpty() && isOrigenDestino==false){ //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp 05/05/2016	
				log.debug("SI Hay delegado primer nivel1");
				String deleg = (String)delegado.get("t02cod_pers");
				//String userOrig = (String)pp.get("cod_pers_ori"); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp				
				log.debug("userOrig: "+userOrig);
				log.debug("deleg: "+deleg);
				if (userOrig.trim().equalsIgnoreCase(deleg.trim())){			
					log.debug("userOrig=deleg (userOrig es delegado 1er nivel)");
					log.debug("delegado.get(cod_jefe): "+(String)delegado.get("cod_jefe"));
					pp.put("cod_pers_ori", (String)delegado.get("cod_jefe"));
				}
				//ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
				else {
					log.debug("userOrig<>deleg");
					if (!delegUOSup.equals("")){
						log.debug("SI Hay delegado segundo nivel1");
						if (userOrig.trim().equalsIgnoreCase(delegUOSup.trim())){
							log.debug("userOrig=delegUOSup (userOrig es delegado 2do nivel)");
							log.debug("delegadoUOSup.get(cod_jefe): "+(String)delegadoUOSup.get("cod_jefe"));
							pp.put("cod_pers_ori", (String)delegadoUOSup.get("cod_jefe"));
							log.debug("pp.get(cod_pers_ori): "+(String)pp.get("cod_pers_ori")); //debe ser el aprobador de nivel superior																
						}
					}				
				}
				//ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
			}
			//ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
			else {
				log.debug("NO Hay delegado primer nivel1");			
				if (!delegUOSup.equals("")){
					log.debug("SI Hay delegado segundo nivel1");					 
					//if (userOrig.trim().equalsIgnoreCase(delegUOSup.trim())){ //05/05/2016
					if (userOrig.trim().equalsIgnoreCase(delegUOSup.trim()) && (!jefeUO.equals("") && !jefeUO.equals(delegUOSup.trim())) ){ //05/05/2016
						log.debug("userOrig===delegUOSup (userOrig es delegado 2do nivel)");
						log.debug("delegadoUOSup.get(cod_jefe):: "+(String)delegadoUOSup.get("cod_jefe"));
						pp.put("cod_pers_ori", (String)delegadoUOSup.get("cod_jefe"));
						log.debug("pp.get(cod_pers_ori):: "+(String)pp.get("cod_pers_ori")); //debe ser el aprobador de nivel superior																
					}
				}				
			}
			//ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
			String jefeDel = (String)mapa.get("jefedelego");
			if(log.isDebugEnabled()) log.debug("jefeDel : "+jefeDel);
			if (jefeDel != null){
				log.debug("jefeDel!=null");
				pp.put("cod_pers_ori",jefeDel);
			}
			log.debug("pp.get(cod_pers_ori): "+(String)pp.get("cod_pers_ori"));
		}
		if(log.isDebugEnabled()) log.debug("pp : "+ pp);

		
		ArrayList datospp = flujoDAO.findAprobadores(pp);
		if(log.isDebugEnabled()) log.debug("MAPA ENCONTRAR APROBADORES : "+ datospp);
		
		if (datospp.size() == 0)   {
			mensajes.add("La solicitud no puede ser atendida por error en la configuracion. Favor coordine con su Analista.");
		}
		else{
					
		//EBV 12/02/2007
		//T12DAO uoDAO = new T12DAO();
		//String userOrig = (String) mapa.get("userOrig"); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
		userOrig = (String) mapa.get("userOrig"); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp		
		String userAprobador = (String) aprobador.get("aprobador");
		String userAprobadorOriginal = (String) aprobador.get("aprobadorOriginal");
		String userAprobRRHH = "";
		String estacion = "";			
		HashMap flujoRRHH = (HashMap) aprobador.get("flujoRRHH");
		
		if(log.isDebugEnabled()) log.debug("pp.get(cod_pers_ori) ICR: "+(String)pp.get("cod_pers_ori"));
		if(log.isDebugEnabled()) log.debug("userOrig1 (mapa.get(userOrig)) : "+userOrig);
		if(log.isDebugEnabled()) log.debug("userAprobador1 (aprobador.get(aprobador)) : "+userAprobador);
		if(log.isDebugEnabled()) log.debug("userAprobadorOriginal1 (aprobador.get(aprobadorOriginal)): "+userAprobadorOriginal);
		if(log.isDebugEnabled()) log.debug("userAprobRRHH1 : "+userAprobRRHH);
		if(log.isDebugEnabled()) log.debug("estacion : "+estacion);
		if(log.isDebugEnabled()) log.debug("FlujoRRHH1 : "+flujoRRHH);
		
		//ICAPUNAY - PAS20165E230300005 05/05/2016	
		prms.put("cuser_orig", userOrig.trim());
		isOrigenDestino = segDAO.ExisteOrigenIgualDestino(prms);
		if(log.isDebugEnabled()) log.debug("isOrigenDestino2 : "+isOrigenDestino);
		//FIN ICAPUNAY
		
		if (flujoRRHH!=null && !flujoRRHH.isEmpty()){
			
			HashMap aux = new HashMap();
			aux.put("dbpool",dbpool);
			aux.put("codUO",(String)aprobador.get("uo"));
			aux.put("codOpcion",Constantes.DELEGA_SOLICITUDES);
			
			if(log.isDebugEnabled()) log.debug("Buscando delegado Flujo RRHH22 "+aux);
			HashMap delegado = uoDAO.findDelegado(aux);
			if(log.isDebugEnabled()) log.debug("Delegado : "+delegado);
			
			//if (delegado!=null && !delegado.isEmpty()){ ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
			if (delegado!=null && !delegado.isEmpty() && isOrigenDestino==false){ //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp 05/05/2016
				log.debug("SI Hay delegado primer nivel2");
				String deleg = (String)delegado.get("t02cod_pers");
				log.debug("deleg2: "+deleg);
				if (userOrig.trim().equalsIgnoreCase(deleg.trim())){//usuario es el delegado
					log.debug("userOrig2=deleg (userOrig es delegado 1errrr nivel)");
					log.debug("delegado.get(cod_jefe)2: "+(String)delegado.get("cod_jefe"));
					userOrig = (String)delegado.get("cod_jefe");
					log.debug("userOrig: "+userOrig);
				}
				//ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
				else {
					log.debug("userOrig<>deleg");
					if (!delegUOSup.equals("")){
						log.debug("SI Hay delegado segundo nivel2");
						if (userOrig.trim().equalsIgnoreCase(delegUOSup.trim())){
							log.debug("userOrig=delegUOSup (userOrig es delegado 2dooo nivel)");
							log.debug("delegadoUOSup.get(cod_jefe)2: "+(String)delegadoUOSup.get("cod_jefe"));
							userOrig = (String)delegadoUOSup.get("cod_jefe");
							log.debug("userOrigg: "+userOrig);																
						}
					}				
				}
				//ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
			}
			//ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
			else {
				log.debug("NO Hay delegado primer nivel2");
				if (!delegUOSup.equals("")){
					log.debug("SI Hay delegado segundo nivel2");
					//if (userOrig.trim().equalsIgnoreCase(delegUOSup.trim())){ //05/05/2016
					if (userOrig.trim().equalsIgnoreCase(delegUOSup.trim()) && (!jefeUO.equals("") && !jefeUO.equals(delegUOSup.trim())) ){ //05/05/2016
						log.debug("userOrig==delegUOSup (userOrig es delegado 2doooooo nivel)");
						log.debug("delegadoUOSup.get(cod_jefe))))): "+(String)delegadoUOSup.get("cod_jefe"));
						userOrig = (String)delegadoUOSup.get("cod_jefe");
						log.debug("userOriggg: "+userOrig);																
					}
				}				
			}
			//ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
			
			if(log.isDebugEnabled()) log.debug("userOrig final : "+userOrig);
			HashMap acciones = (HashMap)flujoRRHH.get(userOrig);
			log.debug("flujoRRHH.get("+userOrig+"):"+acciones);
			if (acciones!=null && !acciones.isEmpty()){
				HashMap datos = (HashMap)acciones.get(Constantes.ACCION_APROBAR);
				estacion = ((String) datos.get("estado")).trim();
				log.debug("estacion: "+estacion);
				
				//ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
				//userAprobRRHH = (String) datos.get("destino"); 
				if (!delegUOSup.equals("")){
					log.debug("Hay delegUOSup");
					//userAprobRRHH = delegUOSup; //destino (uuoo superior colaborador) es delegado si hay delegado en uuoo superior colaborador //120416 modifique
					//estacion=1 y es sol. 06, 103 o 46 (con 3 flujos)
					if (estacion.equals(Constantes.ESTACION_INICIAL) && (tipo.trim().equals(Constantes.LICENCIA_SINGOCEHABER_HASTA3DIAS) || (tipo.trim().equals(Constantes.VACACION_VENTA) && nroInstancias==3) || tipo.trim().equals(Constantes.COMISIONSERV_CITACJUDICIAL))){  //120416 nuevo
						log.debug("estacion=1 y es sol. 06, 103 o 46 (con 3 flujos)");
						log.debug("userAprobRRHH es delegUOSup");
						userAprobRRHH = delegUOSup; //destino (uuoo superior colaborador) es delegado si hay delegado en uuoo superior colaborador
					}
					//120416 nuevo
					else{
						log.debug("estacion<>1 y NO es sol. 06, 103 o 46 (con 3 flujos)");
						log.debug("userAprobRRHH es destino (rrhh)");
						userAprobRRHH = (String) datos.get("destino"); //destino (uuoo superior colaborador) es rrhh si no es estacion=1 y no es sol. 06, 103 o 46 (con 3 flujos)	
					}
					//fin 120416 nuevo														
				}else{
					//05/05/2016
					log.debug("NO Hay delegUOSup");
					String destino = datos.get("destino")!=null?((String) datos.get("destino")).trim():""; //aprobador de uo solicitante
					if (estacion.equals(Constantes.ESTACION_INICIAL) && (tipo.trim().equals(Constantes.LICENCIA_SINGOCEHABER_HASTA3DIAS) || (tipo.trim().equals(Constantes.VACACION_VENTA) && nroInstancias==3) || tipo.trim().equals(Constantes.COMISIONSERV_CITACJUDICIAL))){  //120416 nuevo
						log.debug("estacion=1 y es sol. 06, 103 o 46 (con 3 flujos)");
						if (!destino.equals("") && !jefeUOsup.equals("") && !jefeUOsup.equals(destino)){ //si aprobador (destino) de uo sup <> jefe de uo sup, emitir mensaje
							log.debug("entro iff");
							mensajes.add("La solicitud no puede aprobarse porque el aprobador "+ destino +" de la unidad "+ uuooColab + " no esta como jefe en la unidad " + t12cod_repor + ". Favor coordine con su Analista.");
							log.debug("La solicitud no puede aprobarse porque el aprobador "+ destino +" de la unidad "+ uuooColab + " no esta como jefe en la unidad " + t12cod_repor);	
						}else{
							log.debug("entro elsee");
							userAprobRRHH = (String) datos.get("destino"); //destino (uuoo superior colaborador) es jefe si este es aprobador en uuoo inferior
						}						
					}
					else {
					//fin 05/05/2016
						log.debug("userAprobRRHH es destino (jefe o rrhh)");
						userAprobRRHH = (String) datos.get("destino"); //destino (uuoo superior colaborador) es jefe o rrhh si no hay delegado en uuoo superior colaborador
					}//add linea 05/05/2016
				}
				log.debug("userAprobRRHH: "+userAprobRRHH);					
				//ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
			}					
		}
		
		String superior = (String) mapa.get("codJefe");
		String rrhh = (String) solicitud.get("rrhh");

		if(log.isDebugEnabled()) log.debug("superior codJefe : "+superior);

		//EBV 17/11/2006 para el caso que sea Instancia UNICA
		if (estacion.equals(Constantes.ESTACION_UNICA)){
			log.debug("estacion unica");
			estacion = Constantes.ESTACION_FINAL;
		}

		//para el caso que el aprobador final de la jerarquia tenga delegado
		if (estacion.equals(Constantes.ESTACION_FINAL)){
			log.debug("estacion final");
			superior = superior.equals(userAprobadorOriginal)?userAprobador:superior;
			log.debug("superior: "+superior);
		}

		if(log.isDebugEnabled()) {
		log.debug("rrhh : "+rrhh);
		log.debug("userOrig : "+userOrig);
		log.debug("userAprobador : "+userAprobador);
		log.debug("userAprobadorOriginal : "+userAprobadorOriginal);
		log.debug("userAprobRRHH : "+userAprobRRHH);
		log.debug("superior final : "+superior);
		log.debug("estacion : "+ estacion);
		}
		
		if (mensajes == null || mensajes.isEmpty()) { //add linea   ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp   05/05/2016
			if (userAprobador != null && !userAprobador.trim().equals("")) {
				//si la solicitud debe ser aprobada por RRHH
				if(log.isDebugEnabled()) log.debug("//si la solicitud debe ser aprobada por RRHH");
				if (rrhh.equals(Constantes.ACTIVO)) {
					//si existe un usuario aprobador en RRHH
					if(log.isDebugEnabled()) log.debug("//si existe un usuario aprobador en RRHH");
					if (estacion.equals(Constantes.ESTACION_FINAL) || 
						(userAprobRRHH != null && !userAprobRRHH.trim().equals(""))) {
						if(log.isDebugEnabled()) log.debug("//si es la estacion final - userAprobRRHH != null ");
						//si es la estacion final
						if (estacion.equals(Constantes.ESTACION_FINAL)){	
							if(log.isDebugEnabled()) log.debug("//si es la estacion final "); //INGRESO BLOQUE
							Map datos= new HashMap();
							datos.put("paramSolicitud",solicitud);
							datos.put("paramMapa",mapa);
							datos.put("paramUsuario",usuario);
							datos.put("paramAprobador",aprobador);
							
							//PAS20171U230200001 - solicitud de reintegro validar solicitudes de reintegro
							if (Constantes.MOV_REINTEGRO_POR_DESCUENTO_ASISTENCIA.equals(tipo.trim()) || Constantes.MOV_REINTEGRO_POR_DESCUENTO_SUBSIDIO.equals(tipo.trim())){
								
								//PAS20171U230200033 - solicitud de reintegro agregar carga de detalles desde la base de datos
								solicitud = this.cargarDetallesPreProceso(solicitud);
								datos.put("paramSolicitud", solicitud); 
								mensajes = (ArrayList)validarDataSolicitud(datos);	
							} 
							if(mensajes == null || mensajes.isEmpty()){
							mensajes = (ArrayList)aprobarSolicitud(datos);
						} 
						} 
						//si el responsable actual es el usuario aprobador de la unidad
						else if (userAprobRRHH!=null && !userAprobRRHH.trim().equals("")) {
							if(log.isDebugEnabled()) log.debug("//si el responsable actual es el usuario aprobador de la unidad"); //INGRESO BLOQUE
							mapa.put("estado", Constantes.ESTADO_SEGUIMIENTO);
							mapa.put("userDest", userAprobRRHH);
							//PAS20171U230200001 - solicitud de reintegro   : envio de conceptos excluidos por jefe
							if (Constantes.MOV_REINTEGRO_POR_DESCUENTO_ASISTENCIA.equals(tipo.trim()) || Constantes.MOV_REINTEGRO_POR_DESCUENTO_SUBSIDIO.equals(tipo.trim())) {
								enviarMensajeConceptosExcluidos(solicitud, mapa);
							}
							//fin PAS20171U230200001 - solicitud de reintegro  
							mensajes = registrarSeguimiento(dbpool, solicitud, mapa, usuario);
						}
						//si el responsable actual es un usuario de la UUOO origen						
						else {
							if(log.isDebugEnabled()) log.debug("//si el responsable actual es un usuario de la UUOO origen");
							mapa.put("estado", Constantes.ESTADO_SEGUIMIENTO);
							if (superior != null && !superior.trim().equals("")) {
								if(log.isDebugEnabled()) log.debug("superior != null &&");
								mapa.put("userDest", superior);
								mensajes = registrarSeguimiento(dbpool,solicitud, mapa, usuario);
							} else {
								if(log.isDebugEnabled()) log.debug("No Existe Jefe o encargado a quien dirigir la solicitud.");
								mensajes.add("No Existe Jefe o encargado a quien dirigir la solicitud.");
							}
						}
					} else {
						if(log.isDebugEnabled()) log.debug("ELSE de (estacion.equals(Constantes.ESTACION_FINAL) || (userAprobRRHH != null && !userAprobRRHH.trim().equals('')))");
						if (!userOrig.equalsIgnoreCase(userAprobador)) {
							//si el responsable actual es un usuario de la UUOO origen
							if(log.isDebugEnabled()) log.debug("//si el responsable actual es un usuario de la UUOO origen");
							mapa.put("estado", Constantes.ESTADO_SEGUIMIENTO);
							if (superior != null && !superior.trim().equals("")) {
								if(log.isDebugEnabled()) log.debug("superior != null && !superior.trim().equals('')");
								mapa.put("userDest", superior);
								mensajes = registrarSeguimiento(dbpool,solicitud, mapa, usuario);
							} else {
								if(log.isDebugEnabled()) log.debug("No Existe Jefe o encargado a quien dirigir la solicitud.");
								mensajes.add("No Existe Jefe o encargado a quien dirigir la solicitud.");
							}
						}
						else{
							if(log.isDebugEnabled()) log.debug("No se ha registrado usuario de RR.HH. para derivar este tipo de solicitud.");
							mensajes.add("No se ha registrado usuario de RR.HH. para derivar este tipo de solicitud.");
						}
					}
				}
				//la solicitud no debe ser aprobada por RRHH
				else {
					if(log.isDebugEnabled()) log.debug("//la solicitud no debe ser aprobada por RRHH");
					//String tipo = (String) solicitud.get("tipo"); ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp   05/05/2016
					if (tipo.trim().equals(Constantes.LICENCIA_LABOR_EXCEPCIONAL)){
						if(log.isDebugEnabled()) log.debug("if (tipo.trim().equals(Constantes.LICENCIA_LABOR_EXCEPCIONAL)){");
						userAprobador = userOrig;
					}
					//si el responsable es el usuario aprobador
					if(log.isDebugEnabled()) log.debug("//si el responsable es el usuario aprobador");
					if (userOrig.equalsIgnoreCase(userAprobador)) {
						if(log.isDebugEnabled()) log.debug("//aprobamos la solicitud");
						//aprobamos la solicitud
						Map datos= new HashMap();
						datos.put("paramSolicitud",solicitud);
						datos.put("paramMapa",mapa);
						datos.put("paramUsuario",usuario);
						datos.put("paramAprobador",aprobador);
						mensajes = (ArrayList)aprobarSolicitud(datos);
						
					} else {
						//derivamos la solicitud al superior
						if(log.isDebugEnabled()) log.debug("//derivamos la solicitud al superior");
						mapa.put("estado", Constantes.ESTADO_SEGUIMIENTO);
						if (superior != null && !superior.trim().equals("")) {
							if(log.isDebugEnabled()) log.debug("(superior != null && !superior.trim().equals(''))");
							mapa.put("userDest", superior);
							mensajes = registrarSeguimiento(dbpool, solicitud, mapa, usuario);
						} else {
							if(log.isDebugEnabled()) log.debug("No Existe Jefe o encargado a quien dirigir la solicitud.");
							mensajes.add("No Existe Jefe o encargado a quien dirigir la solicitud.");
						}
					}
				}
			} else {
				if(log.isDebugEnabled()) log.debug("ELSE (userAprobador != null && !userAprobador.trim().equals('')) ");
				//String tipo = (String) solicitud.get("tipo"); ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp   05/05/2016
				if (tipo.trim().equals(Constantes.LICENCIA_LABOR_EXCEPCIONAL)){
					if(log.isDebugEnabled()) log.debug("(tipo.trim().equals(Constantes.LICENCIA_LABOR_EXCEPCIONAL))");
					Map datos= new HashMap();
					datos.put("paramSolicitud",solicitud);
					datos.put("paramMapa",mapa);
					datos.put("paramUsuario",usuario);
					datos.put("paramAprobador",aprobador);
					mensajes = (ArrayList)aprobarSolicitud(datos);
				}
				else
				{
					if(log.isDebugEnabled()) log.debug("No se ha registrado usuario para aprobar este tipo de solicitudes.");
					mensajes.add("No se ha registrado usuario para aprobar este tipo de solicitudes.");
				}
			}
		 } //add linea   ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp   05/05/2016	
	  }		
	} catch (Exception e) { 
		log.error(e);		
		beanM.setMensajeerror(e.toString());//WERR-PAS20155E230300132
		beanM.setMensajesol("Por favor intente nuevamente.");
		throw new IncompleteConversationalState(beanM);
	}
	return mensajes;
}
/*       */


	/* *
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */ 
	/*public ArrayList aprobarSolicitud(String dbpool, HashMap solicitud,
			HashMap mapa, HashMap aprobador, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		ArrayList mensajes = new ArrayList();
		try {

			String tipo = solicitud.get("tipo") != null ? ((String) solicitud.get("tipo")).trim() : "";

			T02DAO personalDAO = new T02DAO();
			CorreoDAO correoDAO = new CorreoDAO();
			T1279DAO movDAO = new T1279DAO();
			HashMap tipoMov = movDAO.findByMov(dbpool, tipo);
			StringTokenizer st = null;

			String numVac = "";
			String periodo = "";
			String fechaVac = "";
			String fechaFinVac = "";

			//si es una solicitud de omision de marca
			if (((String) tipoMov.get("mov")).equals(Constantes.MOV_OMISION_MARCA)) {
				mapa.put("dbpool",dbpool);
				mapa.put("txtHora",(String)solicitud.get("txtHora"));
				mapa.put("fechaMarca",Utiles.timeToFecha((Timestamp) solicitud.get("ffinicio")));
				mapa.put("codUO",(String) solicitud.get("uorgan"));
				mapa.put("userOrig",(String) solicitud.get("codPers"));
				mapa.put("usuario",usuario);
				mensajes = registrarMarcacion(mapa, tipoMov);
			}
			
			//si es una solicitud de anulacion de marca
			if (((String) tipoMov.get("mov")).equals(Constantes.MOV_ANULACION_MARCA)) {
				mapa.put("dbpool",dbpool);
				mapa.put("txtHora",(String)solicitud.get("txtHora"));
				mapa.put("fechaMarca",Utiles.timeToFecha((Timestamp) solicitud.get("ffinicio")));
				mapa.put("codUO",(String) solicitud.get("uorgan"));
				mapa.put("userOrig",(String) solicitud.get("codPers"));
				mapa.put("usuario",usuario);
				mensajes = anularMarcacion(mapa, tipoMov);
			}

			//si es una solicitud de licencia
			if (((String) tipoMov.get("tipo_id")).equals(Constantes.TIPO_MOV_LICENCIA)) {
				mensajes = registrarLicencia(dbpool, solicitud, usuario);
			}

			//si es una solicitud de vacaciones
			if (((String) tipoMov.get("tipo_id")).equals(Constantes.TIPO_MOV_VACACIONES)) {
				
				if (tipo.equals(Constantes.VACACION_VENTA)) {
					String txtObs = (String) mapa.get("txtObs");
					String diasVenta = txtObs.substring(txtObs.indexOf("&diasVenta=")+11,txtObs.length());
					if(log.isDebugEnabled()) log.debug("LLAMO A registrarVentaVacaciones");
					mensajes = registrarVentaVacaciones(dbpool,
							(String) solicitud.get("codPers"),
							(String) solicitud.get("annoVac"),  //annoVac
							diasVenta,
							usuario,
							(String) solicitud.get("annoVac"), //annoVac
							(String) solicitud.get("uorgan"),
							(String) solicitud.get("numero"),
							(Timestamp) solicitud.get("ffinicio"));
					mapa.put("fechaIni", Utiles.obtenerFechaActual());
					mapa.put("fechaFin", Utiles.obtenerFechaActual());
				}

				if (tipo.equals(Constantes.VACACION)) {

					mensajes = registrarVacacionEfectiva(dbpool,
							(String) solicitud.get("codPers"),
							(Timestamp) solicitud.get("ffinicio"),
							(Timestamp) solicitud.get("ffin"),
							(String) solicitud.get("dias"), (String) solicitud
									.get("txtObs"), usuario, (String) solicitud
									.get("anno"), (String) solicitud
									.get("uorgan"), (String) solicitud
									.get("numero"), (String) solicitud.get("annoVac"));
				}

				/******* JRR NUEVA SOLICITUD *******/
			/*	if (tipo.equals(Constantes.VACACION_INDEMNIZADA) ){
					
					//Inserto como Venta
					T1282DAO VacacionDAO = new T1282DAO(dbpool);
					T1276DAO periodoDAO = new T1276DAO();
					
					String cod_pers = solicitud.get("codPers").toString();
					String annoVac = solicitud.get("annoVac").toString();
					String periodoAct = periodoDAO.findPeriodoActual(dbpool).getPeriodo();
					String dias = solicitud.get("dias").toString();
					String uorgan = solicitud.get("uorgan").toString();
					String numeroRef = solicitud.get("numero").toString();
					String anno = solicitud.get("anno").toString();
					FechaBean dia = new FechaBean();
					
					String fDiaSgte = Utiles.timeToFecha((Timestamp) solicitud.get("ffinicio"));
					fDiaSgte = Utiles.dameFechaSiguiente(fDiaSgte, 1);

				    Map prms = new HashMap();
				    prms.put("cod_pers", cod_pers);
				    prms.put("periodo", periodoAct);
				    prms.put("licencia", Constantes.VACACION_INDEMNIZADA);
				    prms.put("ffinicio", Utiles.stringToTimestamp(fDiaSgte + " 00:00:00"));
				    //prms.put("ffinicio", dia.getTimestamp());
				    prms.put("anno_vac", annoVac);
				    prms.put("ffin", Utiles.stringToTimestamp(fDiaSgte + " 00:00:00"));
				    //prms.put("ffin", dia.getTimestamp());
				    prms.put("dias", new Integer(dias));
				    prms.put("anno", "");
				    prms.put("u_organ", uorgan);
				    prms.put("anno_ref", annoVac);
				    prms.put("area_ref", uorgan);
				    prms.put("numero_ref", numeroRef!=null?new Integer(numeroRef):new Integer(0));
				    prms.put("observ", "Imdemnizacion por Vacacion");
				    prms.put("est_id", Constantes.ACTIVO);
				    prms.put("fcreacion", dia.getTimestamp());
				    prms.put("cuser_crea", usuario);
				    if (log.isDebugEnabled()) log.debug("1 - VacacionDAO.insertarVacacionesDetalle(prms)... prms:"+prms);
				    VacacionDAO.insertarVacacionesDetalle(prms);
					
					//Inserto como Goce
				    prms.put("ffinicio", (Timestamp) solicitud.get("ffinicio"));
				    prms.put("ffin", (Timestamp) solicitud.get("ffin"));
					prms.put("anno_ref", anno);
					prms.put("observ", "Vacaciones efectivas atrasadas");
					if (log.isDebugEnabled()) log.debug("2 - VacacionDAO.insertarVacacionesDetalle(prms)... prms:"+prms);
					VacacionDAO.insertarVacacionesDetalle(prms);
				    
					//Actualizo Programacion
				    Map param1 = new HashMap();
				    param1.put("cod_pers", cod_pers);
				    param1.put("licencia", Constantes.VACACION_PROGRAMADA);
				    //param1.put("ffinicio", "");//No buscara por este campo, no importa su valor
				    param1.put("anno_vac", annoVac);
				    param1.put("estado_id", constantes.leePropiedad("PROGRAMACION_EN_SOLICITUD"));
				    param1.put("fmod", dia.getTimestamp());
				    param1.put("cuser_mod", usuario);
				    param1.put("estado_new", constantes.leePropiedad("PROGRAMACION_VENDIDA"));
				    param1.put("apruebaSolVenta", "1");
				    param1.put("anno_ref", annoVac);
				    param1.put("area_ref", uorgan);
				    param1.put("numero_ref", (numeroRef!=null && !numeroRef.trim().equals("0"))?new Integer(numeroRef):null);
				    if (log.isDebugEnabled()) log.debug("actualizaProgramacion - param1: " + param1);
				    VacacionDAO.actualizaProgramacion(param1);				    
				    
				    //Actualizo saldo
					pe.gob.sunat.rrhh.asistencia.dao.T1281DAO t1281dao = new pe.gob.sunat.rrhh.asistencia.dao.T1281DAO(sl.getDataSource("java:comp/env/jdbc/dgsp"));
					Map mdatos = new HashMap();
					//columnas a actualizar
					Map columns = new HashMap();
					columns.put("saldo", "0");
					columns.put("fmod", dia.getTimestamp());
					columns.put("cuser_mod", usuario);
					mdatos.put("columns", columns);
					//datos de la llave primaria
					mdatos.put("cod_pers", cod_pers);
					mdatos.put("anno", annoVac);
					log.debug("t1281dao.updateCustomColumns(mdatos)... mdatos: "+mdatos);
					t1281dao.updateCustomColumns(mdatos);
					
				}
				/***********************************/
				
			/*	if (tipo.equals(Constantes.VACACION_SUSPENDIDA)) {
					st = new StringTokenizer((String) solicitud.get("txtObs"),
							"&");

					periodo = st.nextToken();
					numVac = st.nextToken();

					mensajes = suspenderVacacion(dbpool, numVac,
							(String) solicitud.get("codPers"), periodo,tipo ,
							(Timestamp) solicitud.get("ffinicio"),
							(String) solicitud.get("anno"), (String) solicitud
									.get("uorgan"), (String) solicitud
									.get("numero"), usuario);

				}

				if (tipo.equals(Constantes.VACACION_PROGRAMADA)) {
					
					st = new StringTokenizer((String) solicitud.get("txtObs"),"&");

					periodo = st.nextToken();
					numVac = st.nextToken();

					mensajes = convertirAVacacionEfectiva(dbpool, numVac,
							(String) solicitud.get("codPers"), periodo,
							(Timestamp) solicitud.get("ffinicio"),
							(String) solicitud.get("anno"), (String) solicitud
									.get("uorgan"), (String) solicitud
									.get("numero"), usuario);

				}
				
				if (tipo.equals(Constantes.REPROGRAMACION_VACACION)) {
					
					st = new StringTokenizer((String) solicitud.get("txtObs"),"&");

					periodo = st.nextToken();
					numVac = st.nextToken();
					fechaVac = st.nextToken();
					fechaFinVac = st.nextToken();
					
					solicitud.put("dbpool",dbpool);
					solicitud.put("periodo",periodo);
					solicitud.put("numVac",numVac);
					solicitud.put("fechaVac",fechaVac);
					solicitud.put("fechaFinVac",fechaFinVac);

					mensajes = reprogramarVacacion(solicitud);
				}				
				
				if (tipo.equals(Constantes.VACACION_POSTERGADA)) {
					
					st = new StringTokenizer((String) solicitud.get("txtObs"),"&");

					periodo = st.nextToken();
					fechaVac = st.nextToken();
					fechaFinVac = st.nextToken();
					
					solicitud.put("dbpool",dbpool);
					solicitud.put("periodo",periodo);
					solicitud.put("licencia",tipo);
					solicitud.put("fechaIni",fechaVac);
					solicitud.put("fechaFin",fechaFinVac);

					mensajes = postergarVacacion(solicitud);
				}				

			}

			if (mensajes.isEmpty()) {
				
				//enviamos la solicitud al solicitante
				mapa.put("estado", Constantes.ESTADO_CONCLUIDA);
				mapa.put("userDest", (String) solicitud.get("codPers"));
				mensajes = registrarSeguimiento(dbpool, solicitud, mapa,
						usuario);

				if (mensajes.isEmpty()) {

					String origen = (String) mapa.get("userOrig");
					String destino = (String) mapa.get("userDest");
					String tipoSol = (String) tipoMov.get("descrip");
					String observacion = (mapa.get("txtObs") != null ) ? (String)mapa.get("txtObs"):"";
					//prac-asanchez
					String fechainicio = Utiles.timeToFecha((java.sql.Timestamp)solicitud.get("ffinicio"));
					String fechafin = Utiles.timeToFecha((java.sql.Timestamp)solicitud.get("ffin"));
					//
					int fincad = observacion.indexOf("&");
					observacion = (fincad>0) ? observacion.substring(0,fincad):"";						
					//el campo txtObs, falta verificar si solo es la observacion o esta concatenado con mas datos
					String mensaje = "Su solicitud electr&oacute;nica <strong>"+(String) solicitud.get("anno")
						+"-"+(String) solicitud.get("numero")+" "+ tipoSol + "</strong> ha sido Aprobada.<br><br>"
						//prac-asanchez
						+"Fecha de Inicio: " + fechainicio + "<br>"
						+"Fecha Fin: " + fechafin + "<br><br>"	
						//
						+((observacion.length()>0) ? "<strong>Obs: </strong>"+observacion: "");
					/*ResourceBundle bundle = ResourceBundle.getBundle("pe.gob.sunat.sp.asistencia.sirh");
					String servidorIP = bundle.getString("servidorIP");			
					String strURL = "http://"+servidorIP+"/asistencia/asisS13Alias";
					String paramsURL = "accion=verSolicitud&codPers=" + destino
						+ "&anno=" + (String) solicitud.get("anno")
						+ "&numero=" + (String) solicitud.get("numero");
					String programa = bundle.getString("programa2");
					Cifrador cifrador = new Cifrador();
					String url = cifrador.encriptaURL(destino, programa,strURL, paramsURL);*/
					
		/*			String nombre = personalDAO.findNombreCompletoByCodPers(dbpool, destino);
					String texto = Utiles.textoCorreoProceso(dbpool, nombre, mensaje,"");

					//enviamos el mail al trabajador
					HashMap datos = new HashMap();
					datos.put("subject", "Resultado de Solicitud");
					datos.put("message", texto);
					datos.put("from", correoDAO.findCorreoByCodPers(dbpool,
							origen));
					datos.put("to", correoDAO.findCorreoByCodPers(dbpool,
							destino));

					QueueDAO queue = new QueueDAO();
					queue.enviaCorreo(datos);

				}
			}
			
		} catch (Exception e) {
			log.error(e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return mensajes;
	}
*/
	
/* JRR - FUSION PROGRAMACION - 04/06/2010 */
	/**
	 * Metodo encargado de aprobar la solicitud
	 * @param parametros
	 * @return List
	 * @throws FacadeException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public List aprobarSolicitud(Map parametros)
			 throws FacadeException {

		MensajeBean beanM = new MensajeBean();
		//String dbpool="jdbc/dgsp";//--->pool de conexion
		String dbpool = ServiceLocator.getInstance().getString("java:comp/env/dcsp");
		String dbpoolSig = ServiceLocator.getInstance().getString("java:comp/env/dgsig");
		List mensajes = new ArrayList();
		try {
			Map solicitud =(HashMap)parametros.get("paramSolicitud");
			Map mapa=(HashMap)parametros.get("paramMapa");
			String userOrig = (mapa!=null && !mapa.isEmpty())?((String) mapa.get("userOrig")).trim():""; //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
			String usuario =parametros.get("paramUsuario")!=null?(String)parametros.get("paramUsuario"):"";
			//Map aprobador=(HashMap)parametros.get("paramAprobador");
			String tipo = solicitud.get("tipo") != null ? ((String) solicitud.get("tipo")).trim() : "";
			T02DAO personalDAO = new T02DAO();
			CorreoDAO correoDAO = new CorreoDAO();
			T1279DAO movDAO = new T1279DAO();
			Map tipoMov = movDAO.findByMov(dbpool, tipo);
			T4818DAO t4818dao = new T4818DAO(dbpool);
			T3701DAO t3701dao = new T3701DAO(dbpool);
			T4821DAO t4821dao = new T4821DAO(dbpool);//ICAPUNAY 05/10/2012 AJUSTES AOM 06A4T11 LABOR EXCEPCIONAL
			T1273DAO licenciaDAO = new T1273DAO(dbpool);
			T1282DAO vacacionDAO = new T1282DAO(dbpool);
			StringTokenizer st = null;

			String numVac = "";
			String periodo = "";
			String fechaVac = "";
			String fechaFinVac = "";

			//si es una solicitud de omision de marca
			if (Constantes.MOV_OMISION_MARCA.equals((String) tipoMov.get("mov"))) {
				mapa.put("dbpool",dbpool);
				mapa.put("txtHora",(String)solicitud.get("txtHora"));
				mapa.put("fechaMarca",Utiles.timeToFecha((Timestamp) solicitud.get("ffinicio")));
				mapa.put("codUO",(String) solicitud.get("uorgan"));
				mapa.put("userOrig",(String) solicitud.get("codPers"));//se requiere para registrarMarcacion
				mapa.put("usuario",usuario);
				mensajes = registrarMarcacion((HashMap)mapa, (HashMap)tipoMov);
				mapa.put("userOrig",userOrig);//ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
			}
			
			//si es una solicitud de anulacion de marca
			if (Constantes.MOV_ANULACION_MARCA.equals((String) tipoMov.get("mov"))) {
				mapa.put("dbpool",dbpool);
				mapa.put("txtHora",(String)solicitud.get("txtHora"));
				mapa.put("fechaMarca",Utiles.timeToFecha((Timestamp) solicitud.get("ffinicio")));
				mapa.put("codUO",(String) solicitud.get("uorgan"));
				mapa.put("userOrig",(String) solicitud.get("codPers")); //se requiere para registrarMarcacion
				mapa.put("usuario",usuario);
				mensajes = anularMarcacion((HashMap)mapa, (HashMap)tipoMov);
				mapa.put("userOrig",userOrig);//ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
			}

			//MTM si es una solicitud de labor excepcional
			if (Constantes.MOV_LABOR_EXCEPCIONAL.equals((String) tipoMov.get("mov"))){
				log.debug("labor excepcional");
				log.debug("mensaje de salida: " +mensajes);
				List lista = listaSolicitud(dbpool, (String) solicitud.get("anno"), (String) solicitud.get("numero"),
						(String) solicitud.get("uoOrig"),
						(String) solicitud.get("codPers"),
						(String) solicitud.get("tipo"));

				String cod_pers = solicitud.get("codPers").toString();
				String cod_jefe_aut = solicitud.get("userDest").toString();
				String ind_aut = "1";
				log.debug("solicitud: " +solicitud);
				boolean sw=false;
				for (int i=0; i<lista.size();i++){
					Map prms = new HashMap();
					prms.put("cod_pers", cod_pers);
					prms.put("cod_jefe_aut", cod_jefe_aut);
					prms.put("ind_aut", ind_aut);
					prms.put("cod_user_crea", usuario);
					prms.put("fec_creacion", new Timestamp(System.currentTimeMillis()));
					HashMap u = (HashMap)lista.get(i);
					log.debug("lista: " +u);
					String fecha = (String) u.get("fec_permiso_desc");
					SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy"); 
					Date fechaDate = null;
					fechaDate = formato.parse(fecha); 
					prms.put("fec_aut", fechaDate);
					prms.put("hor_ini_aut", (String) u.get("hor_ini_permiso"));
					prms.put("hor_fin_aut", (String) u.get("hor_fin_permiso"));
					prms.put("obs_sustento", (String) u.get("obs_sustento_obs"));
					log.debug("parametro antes de insertar t4818: " + prms);
					
					Map prm = new HashMap();
					prm.put("cod_pers", cod_pers);
					prm.put("fechaIni", fecha);
					prm.put("fechaFin", fecha);
					boolean bVacacion = vacacionDAO.findByCodPersFIniFFin(prm);
					if (bVacacion) {
						mensajes.add("El trabajador se encuentra de vacaciones durante esas fechas.");					
					}else {
						boolean bVacacion1 = vacacionDAO.findByCodPersFIniFFin49(prm);					
						if (bVacacion1) {
							mensajes.add("El trabajador tiene vacaciones programadas durante esas fechas.");						
						}else{
							Map prms2 = new HashMap();
							prms2.put("cod_pers", cod_pers);
							prms2.put("fecha1", fecha);
							prms2.put("fecha2", fecha);
							prms2.put("numero", "");
							boolean bLicencia = licenciaDAO.findByCodPersFIniFFin(prms2); 		
							if (bLicencia) {
								mensajes.add("El trabajador posee una licencia durante esas fechas.");								
							}else{
								//dtarazona
					            HashMap param=new HashMap();
					            param.put("t99cod_tab", "510");
					            param.put("t99codigo", "47");
					            param.put("dbpool", dbpool);
								String estado=findEstadoByCodTabCodigo(param);
					            log.debug("Estado del flag:"+estado);
					            if(estado.equals("1")){
					            	log.debug("Entrando a cruce:"+estado);
					            	HashMap datos = new HashMap();
									datos.put("cod_pers", cod_pers);
									HashMap planillaActiva =(HashMap)findPlanillaActiva(datos);
									
									String anno=planillaActiva.get("ano_peri_tpe").toString().trim();
									int per=Integer.parseInt(planillaActiva.get("nume_peri_tpe").toString());
									String fechaMin="";
									if(per==1){
										fechaMin="01/12/"+(Integer.parseInt(anno)-1);
									}else{
										if(per-1<10)
										fechaMin="01/0"+(per-1)+"/"+anno;
										else
										fechaMin="01/"+(per-1)+"/"+anno;
									}
									log.debug("Fecha de labor:"+fechaMin+"-Dif:"+Utiles.obtenerDiasDiferencia(fecha, fechaMin));
									
									if(Utiles.obtenerDiasDiferencia(fecha, fechaMin)>0){
										mensajes.add("No puede generar labor excepcional con fecha anterior a la del inicio de la planilla vigente.");
									}else{								
										if(t4818dao.verificaRegistroAutorizaExcPK(dbpool, cod_pers, fecha,(String) u.get("hor_ini_permiso"))){
											mensajes.add("El permiso de fecha "+fecha+ " hora de inicio "+ (String) u.get("hor_ini_permiso")+":00"+" y hora de fin "+(String) u.get("hor_fin_permiso")+":00" + " se encuentra autorizado. No se puede aprobar la solicitud");						
										}else{
											sw=true;
										}								
									}
					            }else{
					            	log.debug("Sin cruce:"+estado);
					            	if(t4818dao.verificaRegistroAutorizaExcPK(dbpool, cod_pers, fecha,(String) u.get("hor_ini_permiso"))){
										mensajes.add("El permiso de fecha "+fecha+ " hora de inicio "+ (String) u.get("hor_ini_permiso")+":00"+" y hora de fin "+(String) u.get("hor_fin_permiso")+":00" + " se encuentra autorizado. No se puede aprobar la solicitud");						
									}else{
										sw=true;
									}
					            }
							}
						}						
					}					
									
				}				
				if(sw){					
					for (int i=0; i<lista.size();i++){
						Map prms = new HashMap();
						prms.put("cod_pers", cod_pers);
						prms.put("cod_jefe_aut", cod_jefe_aut);
						prms.put("ind_aut", ind_aut);
						prms.put("cod_user_crea", usuario);
						prms.put("fec_creacion", new Timestamp(System.currentTimeMillis()));
						HashMap u = (HashMap)lista.get(i);
						log.debug("lista: " +u);
						String fecha = (String) u.get("fec_permiso_desc");
						SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy"); 
						Date fechaDate = null;
						fechaDate = formato.parse(fecha); 
						prms.put("fec_aut", fechaDate);
						prms.put("hor_ini_aut", (String) u.get("hor_ini_permiso")+":00");
						prms.put("hor_fin_aut", (String) u.get("hor_fin_permiso")+":00");
						prms.put("obs_sustento", (String) u.get("obs_sustento_obs"));
						log.debug("parametro antes de insertar t4818: " + prms);						
						t4818dao.insertRegistroAutorizaExc(dbpool, prms);
						
						log.debug("NOVEDAD: "+fechaDate+"-metodo=aprobarSolicitud-accion=Aprobar");
						Map datos2 = new HashMap();
						//String ind_proceso = "4";//ICAPUNAY 27/06/2012 SOLO INSERTA (SI NO EXISTE PARA FECHA SINO ACTUALIZA) CON INDICADOR 0 EN VEZ DE 4
						String ind_proceso = "0";//ICAPUNAY 27/06/2012 SOLO INSERTA (SI NO EXISTE PARA FECHA SINO ACTUALIZA) CON INDICADOR 0 EN VEZ DE 4
						datos2.put("cod_pers", cod_pers);
						datos2.put("fec_refer", fechaDate);
						datos2.put("ind_proceso", ind_proceso);
						datos2.put("cod_usucrea", usuario);
						datos2.put("fec_creacion", new Timestamp(System.currentTimeMillis()));
						log.debug("parametro antes de insertar t3701: " + datos2);
						//ICAPUNAY 27/06/2012 SOLO INSERTA (SI NO EXISTE PARA FECHA SINO ACTUALIZA) CON INDICADOR 0 EN VEZ DE 4
						/*
						Map datos3 = new HashMap();
						datos3 = t3701dao.findNovedadByPK(datos2);
						log.debug("datos3: "+datos3);
						if (datos3 == null){
							t3701dao.registrarNovedad(datos2);
						}*/	
						Map datos3 = t3701dao.findNovedadByTrabFec(datos2);
						log.debug("datos3: "+datos3);							
						if (datos3!=null && !datos3.isEmpty()){//si existe novedad para fecha con un indicador
							String indicadorActual = datos3.get("ind_proceso").toString().trim();
							if (!indicadorActual.equals("0")){
								datos2.put("nuevo",ind_proceso);
								datos2.put("usuario",usuario);
								datos2.put("proceso",indicadorActual);
								t3701dao.actualizar(datos2);//actualizo indicador a 0
							}								
						}else{//no existe ninguna novedad para fecha
							t3701dao.registrarNovedad(datos2);//registra novedad con indicador 0
						}
						log.debug("FIN NOVEDAD: "+fechaDate+"-metodo=aprobarSolicitud-accion=Aprobar");
						//FIN ICAPUNAY 27/06/2012 SOLO INSERTA (SI NO EXISTE PARA FECHA SINO ACTUALIZA) CON INDICADOR 0 EN VEZ DE 4
						
					}					
				}
				
			}
			
			if(log.isDebugEnabled()) log.debug("parametros: "+parametros);
			String regimenCol = (String)solicitud.get("regimenCol");

			if (Constantes.TIPO_MOV_LICENCIA.equals((String) tipoMov.get("tipo_id"))  && 
				!(tipo.equals(Constantes.LICENCIA_MATRIMONIO) && regimenCol.equals(Constantes.CODREL_REG1057))) { //jquispe licencia matrimonio
				mensajes = registrarLicencia(dbpool, (HashMap)solicitud, usuario);
			}

			if (Constantes.TIPO_MOV_LICENCIA.equals((String) tipoMov.get("tipo_id")) && 
				(tipo.equals(Constantes.LICENCIA_MATRIMONIO) && regimenCol.equals(Constantes.CODREL_REG1057))) { //jquispe licencia matrimonio
				
				if(log.isDebugEnabled()) log.debug("LICENCIA_MATRIMONIO + CAS");
				String cod_pers = (String)solicitud.get("codPers");
				T1276DAO periodoDAO = new T1276DAO();
				
				//1. Obtiene periodo
				String periodoActual = periodoDAO.findPeriodoActual(dbpool).getPeriodo();
				solicitud.put("periodo",periodoActual);
				
				//2. Validaciones Bï¿½sicas
				Date ffinicio = (Date)solicitud.get("ffinicio");
				Date ffin = (Date)solicitud.get("ffin");
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				String strFfinicio = sdf.format(ffinicio);
				String strFfin = sdf.format(ffin); 
				
				Map prmVacacion = new HashMap();
				prmVacacion.put("cod_pers", cod_pers);
				prmVacacion.put("fechaIni", strFfinicio);
				prmVacacion.put("fechaFin", strFfin);
				boolean bVacacion = vacacionDAO.findByCodPersFIniFFin(prmVacacion);
				if (bVacacion) {
					mensajes.add("El trabajador se encuentra de vacaciones durante esas fechas.");					
				}else {
					Map prmLicencia = new HashMap();
					prmLicencia.put("cod_pers", cod_pers);
					prmLicencia.put("fecha1", strFfinicio);
					prmLicencia.put("fecha2", strFfin);
					prmLicencia.put("numero", "");
					boolean bLicencia = licenciaDAO.findByCodPersFIniFFin(prmLicencia); 		
					if (bLicencia) {
						mensajes.add("El trabajador posee una licencia durante esas fechas.");								
					}				
				}
				
				//1.1. Se busca la licencia en la tabla de vacaciones.
				if(mensajes.isEmpty()){
					HashMap paramsVacacionMatrimonio = new HashMap();
					paramsVacacionMatrimonio.put("licencia", Constantes.VACACION);
					paramsVacacionMatrimonio.put("cod_pers", cod_pers);
					paramsVacacionMatrimonio.put("est_id", Constantes.ACTIVO);
					paramsVacacionMatrimonio.put("ind_matri", Constantes.ACTIVO);
					List lstVacacionesMatrimonio = vacacionDAO.findVacacionesProgramadasByParams(paramsVacacionMatrimonio);
					log.debug("lstVacacionesMatrimonio: "+lstVacacionesMatrimonio);
					if(lstVacacionesMatrimonio!=null && lstVacacionesMatrimonio.size()>0){
						HashMap vacacionMatrimonio = (HashMap)lstVacacionesMatrimonio.get(0);
						String strFfinicio_ = (String)vacacionMatrimonio.get("ffinicio_desc");
						mensajes.add("El trabajador ya cuenta una licencia de matrimonio registrada que inicia el " + strFfinicio_.substring(0, 10) + ".");
					}
				}
				
				//3. Antiguedad tres meses
				if(mensajes.isEmpty()){ //CODIGO REPETIDO OPTIMIZAR
					//6.1. Obtener fecha de ingreso 
					pe.gob.sunat.sp.dao.T02DAO empleadoDAO = new pe.gob.sunat.sp.dao.T02DAO();	
					Map mapaEmpleado = empleadoDAO.findEmpleado("java:comp/env/jdbc/dgsp",cod_pers);	
					Date dteFechaIngreso = (mapaEmpleado!=null && !mapaEmpleado.isEmpty())?((Date)mapaEmpleado.get("t02f_ingsun")):null;
					String strFechaIngreso = sdf.format(dteFechaIngreso);
					
					//6.2. Obtener aï¿½o de trabajo
					String strFechaActual = Utiles.obtenerFechaActual();
					String strAnioActual = Utiles.dameAnho(strFechaActual);
					String strFechaCalculo = strFechaIngreso.substring(0, 6) + strAnioActual;
					String anioSaldoVacVig;
					int difDias = Utiles.obtenerDiasDiferencia(strFechaCalculo, strFechaActual);
					if (difDias >= 0) 
						anioSaldoVacVig = strAnioActual;					
					else 
						anioSaldoVacVig = (Integer.parseInt(strAnioActual) - 1) + ""; 
					solicitud.put("annoVac", (Integer.parseInt(anioSaldoVacVig)+1)+"");//utilizado vacacionFacade:convertirEfectivaMatrimonio
					
					//6.3 Restamos tres meses a la fecha actual
					//Date dteFechaActual = sdf.parse(strFechaActual);
					Date dteFechaIngresoTrunco = sdf.parse(strFechaIngreso.substring(0, 6)+anioSaldoVacVig);
												
					Date dteFechaInicio = sdf.parse(strFfinicio);//add faltaba
					Calendar calFechaActual = Calendar.getInstance();
					calFechaActual.setTime(dteFechaInicio);
					calFechaActual.add(Calendar.MONTH, -3);
					Date dteFechaInicioDisminuido = calFechaActual.getTime();
					log.debug("dteFechaInicio: "+dteFechaInicio);
					log.debug("dteFechaInicioDisminuido: "+dteFechaInicioDisminuido);
					log.debug("dteFechaIngresoTrunco (aprobarSolicitud-SolicitudFacade): "+dteFechaIngresoTrunco);
					
					String ddmmFecIngTrunco = new SimpleDateFormat("dd/MM/yyyy").format(dteFechaIngresoTrunco).toString().substring(0,6);//ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta vacaciones (SC)
					String ddmmFecIniDismin = new SimpleDateFormat("dd/MM/yyyy").format(dteFechaInicioDisminuido).toString().substring(0,6);//ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta vacaciones (SC)
					
					//if(dteFechaIngresoTrunco.after(dteFechaInicioDisminuido))	//ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta vacaciones (SC)
					if(dteFechaIngresoTrunco.after(dteFechaInicioDisminuido) || ddmmFecIngTrunco.equals(ddmmFecIniDismin))	//ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta vacaciones (SC)
						mensajes.add("Debe tener una antiguedad mayor a 3 meses de su fecha de ingreso o generación de saldo vacacional para esta licencia.");
				}
				
				//4. No tener saldo vacacional 
				if(mensajes.isEmpty()){
					T1281DAO vacacionesCabDAO = new T1281DAO();
					int saldo = vacacionesCabDAO.findByCodPers(dbpool,cod_pers);
					if (saldo>0)
						mensajes.add("El trabajador posee "+saldo+" d&iacute;as de saldo vacacional.");
				}

				log.debug("mensajes: "+mensajes);
				//5. Actualizaciones correspondientes
				if(mensajes.isEmpty())
					mensajes = convertirAVacacionEfectivaMatrimonio(dbpool,(HashMap)solicitud,usuario);				
			}
			
			
			//si es una solicitud de vacaciones
			if (Constantes.TIPO_MOV_VACACIONES.equals((String) tipoMov.get("tipo_id"))) {
				if (Constantes.VACACION_VENTA.equals(tipo)) {
					
					HashMap paramVac = new HashMap();
					paramVac.put("cod_pers", (String) solicitud.get("codPers"));
					paramVac.put("annoVac", (String) solicitud.get("annoVac"));
					log.debug("Mapa:"+paramVac);
					T1282DAO t1282dao = new T1282DAO(dbpool);
					int totalFrac = t1282dao.findTotalFraccionadoPorAnio(paramVac);
					log.debug("total fracioon:"+totalFrac);
					
					if(mapa.get("periodo")==null ){
						String txtObs = (String) mapa.get("txtObs");
						
						String diasVenta = txtObs.substring(txtObs.indexOf("&diasVenta=")+11,txtObs.length());
						//CODIGO PARA VALIDAR SI TIENE SALDO Y PUEDE VENDER VACACIONES SIN PASAR LIMITE DE 
						//15 DIAS
						int diasInt = Integer.parseInt((String) solicitud.get("dias"));
						HashMap hm = new HashMap();
						hm.put("cod_pers",(String) solicitud.get("codPers"));
						hm.put("anno",(String) solicitud.get("annoVac"));
						T1282DAO t1282 = new T1282DAO(dbpool);
						int diasTotVta = t1282.findVentasTotXanio(hm);
						
						DataSource ds = sl.getDataSource("java:comp/env/jdbc/dcsp");
			            pe.gob.sunat.rrhh.dao.T99DAO t99dao = new pe.gob.sunat.rrhh.dao.T99DAO(ds);
			    		Map filtro = new HashMap();
			    		filtro.put("t99cod_tab", "510");
			    		filtro.put("t99tip_desc", "D");
			    		filtro.put("t99estado", "1");
			    		filtro.put("t99codigo", "45");
			    		Map mapaParam = t99dao.findParamByCodTabCodigo(filtro);
			    		String paramMaxVenta = (String) mapaParam.get("t99descrip");
						log.debug("Coooodigoooo:"+paramMaxVenta);
						int maxVenta=Integer.parseInt(paramMaxVenta);
						
						if (diasTotVta>maxVenta){
						   mensajes.add("No se puede vender mas de "+maxVenta+" dias por Año (" +  (String) solicitud.get("annoVac") + ") o No hay Suficiente Saldo para Vender. Verifique Por Favor.");	
						}else{
						   int diasTotVtaYsalida = t1282.findByCodPersAnnoVacEfecComp(hm);
						   int diasDisponibles = 30 - diasTotVtaYsalida;
						   //int diasDisponibles = 30 - diasTotVtaYsalida-totalFrac;
						   log.debug("Datos importantes:"+maxVenta +"-"+ diasTotVtaYsalida+"-"+totalFrac+"-"+diasTotVta);
						   if (diasTotVta + diasInt +totalFrac> maxVenta){
							   mensajes.add("No se puede vender mas de "+maxVenta+" dias por Año (" +  (String) solicitud.get("annoVac") + ") o No hay Suficiente Saldo para Vender. Verifique Por Favor.");	   
						   }else{
							   if (diasInt<= diasDisponibles){
								   if(log.isDebugEnabled()) log.debug("OK Hay saldo para vender");	   
							   }else{
								  mensajes.add("No se puede vender mas de "+maxVenta+" dias por Año (" +  (String) solicitud.get("annoVac") + ") o No hay Suficiente Saldo para Vender. Verifique Por Favor.");
							   }   
						   }						  
						}
						

						if (mensajes.isEmpty()) {
						  VacacionFacadeHome facadeHome = (VacacionFacadeHome) ServiceLocator.getInstance().getRemoteHome(
						    VacacionFacadeHome.JNDI_NAME,VacacionFacadeHome.class);
							VacacionFacadeRemote facadeRemote = facadeHome.create();
							//String res = facadeRemote.venderVacaciones(dbpool, codPers,	anno, diasVenta,usuario, annoRef, areaRef, numeroRef/*, fIni*/);
							String res = facadeRemote.venderVacaciones(
									dbpool, 
									(String) solicitud.get("codPers"),
									(String) solicitud.get("annoVac"), 
									diasVenta,
									usuario, 
									(String) solicitud.get("annoVac"), 
									(String) solicitud.get("uorgan"), 
									(String) solicitud.get("numero"),""/*, fIni*/);
							if (!res.equals(Constantes.OK)) {
								mensajes.add(res);
							}
						}
						
						//mensajes = registrarVentaVacaciones(dbpool,
						//		(String) solicitud.get("codPers"),
						//		(String) solicitud.get("annoVac"),  
						//		diasVenta,
						//		usuario,
						//		(String) solicitud.get("annoVac"), 
						//		(String) solicitud.get("uorgan"),
						//		(String) solicitud.get("numero"),
						//		(Timestamp) solicitud.get("ffinicio"));
						//mapa.put("fechaIni", new FechaBean().getTimestamp());
						//mapa.put("fechaFin", new FechaBean().getTimestamp());
					}		
					else{
						
						//mensajes = validaVentaVacaciones(dbpool, (String) solicitud.get("codPers"), (String) solicitud.get("annoVac"), (String) solicitud.get("dias"),usuario);
						//log.debug("=====================mensajes = validaVentaVacaciones");
						//log.debug(mensajes);
						//dbpool, (String) solicitud.get("codPers"), (String) solicitud.get("annoVac"), 
						//(String) solicitud.get("dias"),usuario
						
						//CODIGO PARA VALIDAR SI TIENE SALDO Y PUEDE VENDER VACACIONES SIN PASAR LIMITE DE 
						//15 DIAS						
						int diasInt = Integer.parseInt((String) solicitud.get("dias"));
						HashMap hm = new HashMap();
						hm.put("cod_pers",(String) solicitud.get("codPers"));
						hm.put("anno",(String) solicitud.get("annoVac"));
						T1282DAO t1282 = new T1282DAO(dbpool);
						int diasTotVta = t1282.findVentasTotXanio(hm);						
						log.debug("diasTot:"+diasTotVta+"-" + diasInt);
						DataSource ds = sl.getDataSource("java:comp/env/jdbc/dcsp");
						pe.gob.sunat.rrhh.dao.T99DAO t99dao = new pe.gob.sunat.rrhh.dao.T99DAO(ds);
			    		Map filtro = new HashMap();
			    		filtro.put("t99cod_tab", "510");
			    		filtro.put("t99tip_desc", "D");
			    		filtro.put("t99estado", "1");
			    		filtro.put("t99codigo", "45");
			    		Map mapaParam = t99dao.findParamByCodTabCodigo(filtro);
			    		String paramMaxVenta = (String) mapaParam.get("t99descrip");
						log.debug("Coooodigoooo:"+paramMaxVenta);
						int maxVenta=Integer.parseInt(paramMaxVenta);
						
						if (diasTotVta>maxVenta){
						   mensajes.add("No se puede vender mas de "+maxVenta+" dias por Año (" +  (String) solicitud.get("annoVac") + ") o No hay Suficiente Saldo para Vender. Verifique Por Favor.");	
						}else{
						   int diasTotVtaYsalida = t1282.findByCodPersAnnoVacEfecComp(hm);
						   int diasDisponibles = 30 - diasTotVtaYsalida;
						   //int diasDisponibles = maxVenta - diasTotVtaYsalida-totalFrac;
						   log.debug("Datos importantes1:"+maxVenta +"-"+ diasTotVtaYsalida+"-"+totalFrac+"-"+diasTotVta);
						   if (diasTotVta + diasInt +totalFrac> maxVenta){
							   mensajes.add("No se puede vender mas de "+maxVenta+" dias por Año (" +  (String) solicitud.get("annoVac") + ") o No hay Suficiente Saldo para Vender. Verifique Por Favor.");	   
						   }else{
							   if (diasInt <= diasDisponibles){
								   if(log.isDebugEnabled()) log.debug("OK Hay saldo para vender");	   
							   }else{
								  mensajes.add("No se puede vender mas de "+maxVenta+" dias por Año (" +  (String) solicitud.get("annoVac") + ") o No hay Suficiente Saldo para Vender. Verifique Por Favor.");
							   }   
						   }
						}
						
						if (mensajes.isEmpty()) {
						  Map param=solicitud;
						  param.put("usuario",usuario);
						  VacacionFacadeHome facadeHome = 
							  (VacacionFacadeHome) ServiceLocator.getInstance().getRemoteHome(
								VacacionFacadeHome.JNDI_NAME,
								VacacionFacadeHome.class);
					      VacacionFacadeRemote facadeRemote = facadeHome.create();
					      String res = facadeRemote.venderVacaciones(param);
						  if (!res.equals(Constantes.OK)) {
							mensajes.add(res);
						  }
					    }
					}
				}
					
				if (Constantes.VACACION.equals(tipo)) {
					
					if(  mapa.get("periodo")==null ){
						
						Map params= new HashMap();
						params.put("cod_pers",solicitud.get("codPers"));
						params.put("licencia",constantes.leePropiedad("VACACION_PROGRAMADA"));
						params.put("est_id",constantes.leePropiedad("PROGRAMACION_EN_SOLICITUD"));
						params.put("anno_vac",solicitud.get("annoVac"));
						params.put("numero_ref",solicitud.get("numero"));
						T1282DAO t1282dao = new T1282DAO(dbpool);
						Map periodoSolicitud= t1282dao.findByCodPersNumeroRefLicenciaEstid(params);
						if (periodoSolicitud!=null){
							solicitud.put("periodo",(String)periodoSolicitud.get("periodo"));
						} else{
							solicitud.put("periodo",null);
						}
					}
					log.debug("esAdelantoAp:"+(String)solicitud.get("txtObs"));
					String esAdelanto=(String)solicitud.get("txtObs");
					if(solicitud.get("txtObs").equals("1")){
						mensajes = convertirAVacacionEfectivaAdelanto(dbpool,"",
								(String) solicitud.get("codPers"),
								(String) solicitud.get("periodo"),
								(Timestamp) solicitud.get("ffinicio"),
								(String) solicitud.get("anno"),
								(String) solicitud.get("uorgan"),
								(String) solicitud.get("numero"),esAdelanto,
								usuario);
					}else{
						mensajes = convertirAVacacionEfectiva(dbpool,"",
								(String) solicitud.get("codPers"),
								(String) solicitud.get("periodo"),
								(Timestamp) solicitud.get("ffinicio"),
								(String) solicitud.get("anno"),
								(String) solicitud.get("uorgan"),
								(String) solicitud.get("numero"),
								usuario);
					}					
				}


				/******* JRR NUEVA SOLICITUD *******/
				if (tipo.equals(Constantes.VACACION_INDEMNIZADA) ){
					
					//Inserto como Venta
					T1282DAO VacacionDAO = new T1282DAO(dbpool);
					T1276DAO periodoDAO = new T1276DAO();
					
					String cod_pers = solicitud.get("codPers").toString();
					String annoVac = solicitud.get("annoVac").toString();
					String periodoAct = periodoDAO.findPeriodoActual(dbpool).getPeriodo();
					String dias = solicitud.get("dias").toString();
					String uorgan = solicitud.get("uorgan").toString();
					String numeroRef = solicitud.get("numero").toString();
					String anno = solicitud.get("anno").toString();
					FechaBean dia = new FechaBean();
					
					String fDiaSgte = Utiles.timeToFecha((Timestamp) solicitud.get("ffinicio"));
					fDiaSgte = Utiles.dameFechaSiguiente(fDiaSgte, 1);

				    Map prms = new HashMap();
				    prms.put("cod_pers", cod_pers);
				    prms.put("periodo", periodoAct);
				    prms.put("licencia", Constantes.VACACION_INDEMNIZADA);
				    prms.put("ffinicio", Utiles.stringToTimestamp(fDiaSgte + " 00:00:00"));
				    //prms.put("ffinicio", dia.getTimestamp());
				    prms.put("anno_vac", annoVac);
				    prms.put("ffin", Utiles.stringToTimestamp(fDiaSgte + " 00:00:00"));
				    //prms.put("ffin", dia.getTimestamp());
				    prms.put("dias", new Integer(dias));
				    prms.put("anno", "");
				    prms.put("u_organ", uorgan);
				    prms.put("anno_ref", annoVac);
				    prms.put("area_ref", uorgan);
				    //JR cambio 28/05/2009
				    //prms.put("numero_ref", numeroRef!=null?new Integer(numeroRef):new Integer(0));
				    prms.put("numero_ref", (numeroRef!=null&&!numeroRef.trim().equals("0"))?new Integer(numeroRef):null);
				    prms.put("observ", "Imdemnizacion por Vacacion");
				    prms.put("est_id", Constantes.ACTIVO);
				    prms.put("fcreacion", dia.getTimestamp());
				    prms.put("cuser_crea", usuario);
				    if (log.isDebugEnabled()) log.debug("1 - VacacionDAO.insertarVacacionesDetalle(prms)... prms:"+prms);
				    VacacionDAO.insertarVacacionesDetalle(prms);
					
					//Inserto como Goce
				    prms.put("ffinicio", (Timestamp) solicitud.get("ffinicio"));
				    prms.put("ffin", (Timestamp) solicitud.get("ffin"));
					prms.put("anno_ref", anno);
					prms.put("observ", "Vacaciones efectivas atrasadas");
					if (log.isDebugEnabled()) log.debug("2 - VacacionDAO.insertarVacacionesDetalle(prms)... prms:"+prms);
					VacacionDAO.insertarVacacionesDetalle(prms);
				    
					//Actualizo Programacion
				    Map param1 = new HashMap();
				    param1.put("cod_pers", cod_pers);
				    param1.put("licencia", Constantes.VACACION_PROGRAMADA);
				    //param1.put("ffinicio", "");//No buscara por este campo, no importa su valor
				    param1.put("anno_vac", annoVac);
				    param1.put("estado_id", constantes.leePropiedad("PROGRAMACION_EN_SOLICITUD"));
				    param1.put("fmod", dia.getTimestamp());
				    param1.put("cuser_mod", usuario);
				    param1.put("estado_new", constantes.leePropiedad("PROGRAMACION_VENDIDA"));
				    param1.put("apruebaSolVenta", "1");
				    param1.put("anno_ref", annoVac);
				    param1.put("area_ref", uorgan);
				    //JR 28/05/2009
				    //param1.put("numero_ref", numeroRef!=null?new Integer(numeroRef):new Integer(0));
				    param1.put("numero_ref",  (numeroRef!=null&&!numeroRef.trim().equals("0"))?new Integer(numeroRef):null);
				    
				    if (log.isDebugEnabled()) log.debug("actualizaProgramacion - param1: " + param1);
				    VacacionDAO.actualizaProgramacion(param1);				    
				    
				    //Actualizo saldo
					pe.gob.sunat.rrhh.asistencia.dao.T1281DAO t1281dao = new pe.gob.sunat.rrhh.asistencia.dao.T1281DAO(ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dgsp"));
					Map mdatos = new HashMap();
					//columnas a actualizar
					Map columns = new HashMap();
					columns.put("saldo", "0");
					columns.put("fmod", dia.getTimestamp());
					columns.put("cuser_mod", usuario);
					mdatos.put("columns", columns);
					//datos de la llave primaria
					mdatos.put("cod_pers", cod_pers);
					mdatos.put("anno", annoVac);
					if(log.isDebugEnabled()) log.debug("t1281dao.updateCustomColumns(mdatos)... mdatos: "+mdatos);
					t1281dao.updateCustomColumns(mdatos);
					
				}
				/***********************************/


				if (Constantes.VACACION_SUSPENDIDA.equals(tipo)) {
					st = new StringTokenizer((String) solicitud.get("txtObs"),
							"&");

					periodo = st.nextToken();
					numVac = st.nextToken();

					mensajes = suspenderVacacion(dbpool, numVac,
							(String) solicitud.get("codPers"), periodo,tipo ,
							(Timestamp) solicitud.get("ffinicio"),
							(String) solicitud.get("anno"), (String) solicitud
									.get("uorgan"), (String) solicitud
									.get("numero"), usuario);

				}

				if (Constantes.VACACION_PROGRAMADA.equals(tipo)) {
					
					st = new StringTokenizer((String) solicitud.get("txtObs"),"&");

					periodo = st.nextToken();
					numVac = st.nextToken();

					mensajes = convertirAVacacionEfectiva(dbpool, numVac,
							(String) solicitud.get("codPers"), periodo,
							(Timestamp) solicitud.get("ffinicio"),
							(String) solicitud.get("anno"), (String) solicitud
									.get("uorgan"), (String) solicitud
									.get("numero"), usuario);

				}
				
				if (Constantes.REPROGRAMACION_VACACION.equals(tipo)) {
					
					st = new StringTokenizer((String) solicitud.get("txtObs"),"&");

					periodo = st.nextToken();
					numVac = st.nextToken();
					fechaVac = st.nextToken();
					fechaFinVac = st.nextToken();
					
					solicitud.put("dbpool",dbpool);
					solicitud.put("periodo",periodo);
					solicitud.put("numVac",numVac);
					solicitud.put("fechaVac",fechaVac);
					solicitud.put("fechaFinVac",fechaFinVac);

					mensajes = reprogramarVacacion((HashMap)solicitud);
				}				
				
				if (Constantes.VACACION_POSTERGADA.equals(tipo)) {
					if(log.isDebugEnabled()) log.debug("Entrando a Aprobar como postergada.");
					st = new StringTokenizer((String) solicitud.get("txtObs"),"&");

					periodo = st.nextToken();
					fechaVac = st.nextToken();
					fechaFinVac = "14/03/2018";//st.nextToken();
					if(log.isDebugEnabled()) log.debug("vacPost1");
					solicitud.put("dbpool",dbpool);
					solicitud.put("periodo",periodo);
					solicitud.put("licencia",tipo);
					solicitud.put("fechaIni",fechaVac);
					solicitud.put("fechaFin",fechaFinVac);
					solicitud.put("cuser_mod",usuario);
					if(log.isDebugEnabled()) log.debug("vacPost1");
					if(log.isDebugEnabled()) log.debug("Dato1 f:"+solicitud.get("d1"));
					if(log.isDebugEnabled()) log.debug("Dato2 f:"+solicitud.get("d2"));
					
					solicitud.put("aprobar", "1");
					mensajes = postergarVacacion((HashMap)solicitud);
				}				

			}

			//PAS20171U230200001 - solicitud de reintegro    solicitud de reintegro
			 
			if (Constantes.MOV_REINTEGRO_POR_DESCUENTO_ASISTENCIA.equals((String) tipoMov.get("mov"))) {
				HashMap reintegro = (HashMap) solicitud.get("solRein");
				reintegro.put("cod_usumodif", parametros.get("paramUsuario"));
				mensajes = this.procesarDetalleConcSolReintegro(reintegro);
				log.debug("mapa de solicitud ->" + solicitud);
				HashMap filtro = new HashMap();
				filtro.put("num_seqrein", reintegro.get("num_seqrein"));
				filtro.put("codPers", solicitud.get("codPers"));
				mensajes = aprobarRRHHSolReintegroAsistencia(filtro,reintegro );
			}
			if (Constantes.MOV_REINTEGRO_POR_DESCUENTO_SUBSIDIO.equals((String) tipoMov.get("mov"))) {
				HashMap reintegro = (HashMap) solicitud.get("solRein");
				reintegro.put("cod_usumodif", parametros.get("paramUsuario"));
				mensajes = this.procesarDetalleConcSolReintegro(reintegro);
				log.debug("mapa de solicitud ->" + solicitud);
				HashMap filtro = new HashMap();
				filtro.put("num_seqrein", reintegro.get("num_seqrein"));
				filtro.put("codPers", solicitud.get("codPers"));
				mensajes = aprobarRRHHSolReintegroSubsidio(filtro,reintegro);
			}
			 
			if (mensajes.isEmpty()) {
				
				
				//enviamos la solicitud al solicitante
				mapa.put("estado", Constantes.ESTADO_CONCLUIDA);
				mapa.put("userDest", (String) solicitud.get("codPers"));
				mensajes = registrarSeguimiento(dbpool, (HashMap)solicitud, (HashMap)mapa,
						usuario);

				if (mensajes.isEmpty()) {

					String origen = (String) mapa.get("userOrig");
					String destino = (String) mapa.get("userDest");
					String tipoSol = (String) tipoMov.get("descrip");
					String observacion = (mapa.get("txtObs") != null ) ? (String)mapa.get("txtObs"):"";
					log.debug("aprobacion(observacion1): "+observacion);
					//prac-asanchez 29/08/2009
					String fechainicio = Utiles.timeToFecha((java.sql.Timestamp)solicitud.get("ffinicio"));
					String fechafin = Utiles.timeToFecha((java.sql.Timestamp)solicitud.get("ffin"));
					//					
					int fincad = observacion.indexOf("&");
					observacion = (fincad>0) ? observacion.substring(0,fincad):"";					
					log.debug("aprobacion(observacion2): "+observacion);
					//el campo txtObs, falta verificar si solo es la observacion o esta concatenado con mas datos
					String mensaje = "Su solicitud electr&oacute;nica <strong>"+"N° "+(String) solicitud.get("anno")
					+"-"+(String) solicitud.get("numero")+" "+ tipoSol + "</strong> ha sido Aprobada.<br><br>";
					//prac-asanchez 29/05/2009
					if (!Constantes.MOV_LABOR_EXCEPCIONAL.equals(tipo)){//otros tipos solicitudes
						//add 23/11/2012 cambios glosas
						if (Constantes.MOV_SOLICITUD_COMPENSACION.equals(tipo)){//compensacion 125
							mensaje = "Su papeleta electr&oacute;nica <strong>"+"N° "+(String) solicitud.get("anno")
							+"-"+(String) solicitud.get("numero")+" "+ tipoSol + "</strong> ha sido <strong>Aprobada.</strong><br><br>";
						}
						//add 23/11/2012 cambios glosas
						if(!Constantes.VACACION_POSTERGADA.equals(tipo)){
							mensaje = mensaje + "Fecha de Inicio: " + fechainicio + "<br>"
									+"Fecha Fin: " + fechafin + "<br><br>";
						}						
					}
					//ICAPUNAY 05/10/2012 AJUSTES AOM 06A4T11 LABOR EXCEPCIONAL
					else{//labor excepcional
						String textoFechas = "";
						String tipoLabor = solicitud.get("estadoSol")!=null?solicitud.get("estadoSol").toString().trim():"";
						if(log.isDebugEnabled()) log.debug("tipoLabor Final: "+ tipoLabor);
						if (tipoLabor.equals("1")){//generada x colaborador	
							log.debug("generada x colaborador");
							List fechas = t4821dao.findFechasPermiso(destino,(String) solicitud.get("anno"),(String) solicitud.get("numero"));
							if (fechas!=null && fechas.size()>0){
								textoFechas = "del ";
								for (int m=0; m<fechas.size();m++){
									HashMap fecha = (HashMap)fechas.get(m);
									if (fechas.size()==1){
										textoFechas = textoFechas + fecha.get("fec_permiso_desc") ;
									}else{
										if (m==fechas.size()-1){//ultimo va "y"
											textoFechas = textoFechas + fecha.get("fec_permiso_desc") ;
										}else{
											if (m==fechas.size()-2){
												textoFechas = textoFechas + fecha.get("fec_permiso_desc")+" y " ;
											}else{
												textoFechas = textoFechas + fecha.get("fec_permiso_desc")+" , " ;
											}										
										}
									}								
								}
							}							
							//mensaje = "Se le comunica que la solicitud <strong>"+"Nï¿½ "+(String) solicitud.get("anno")
							//+"-"+(String) solicitud.get("numero")+ " " +tipoSol + "</strong> " + textoFechas + " ha sido Aprobada.<br><br>";
							mensaje = "Se le comunica que la <strong> PAPELETA DE "+tipoSol+" N° "+(String) solicitud.get("anno")
							+"-"+(String) solicitud.get("numero")+ " </strong> " + textoFechas + " ha sido <strong>Aprobada.</strong><br><br>";
						}
						if (tipoLabor.equals("2")){//autogenerada x sistema
							log.debug("autogenerada x sistema");
							textoFechas = "del "+ fechainicio;
							//mensaje = "Se le comunica que la solicitud <strong>"+"Nï¿½ "+(String) solicitud.get("anno")
							//+"-"+(String) solicitud.get("numero")+" AUTOGENERADA DE "+ tipoSol + "</strong> " + textoFechas + " ha sido Aprobada.<br><br>";
							mensaje = "Se le comunica que la <strong> PAPELETA AUTOGENERADA DE "+ tipoSol+" N° "+(String) solicitud.get("anno")
							+"-"+(String) solicitud.get("numero")+ " </strong> " + textoFechas + " ha sido <strong>Aprobada.</strong><br><br>";
						}
						log.debug("textoFechas: "+textoFechas);
					}
					log.debug("aprobacion(mensaje prefinal): "+mensaje);
					//FIN ICAPUNAY 05/10/2012 AJUSTES AOM 06A4T11 LABOR EXCEPCIONAL
					mensaje = mensaje +((observacion.length()>0) ? "<strong>Obs: </strong>"+observacion: "");
					log.debug("aprobacion(mensaje final): "+mensaje);
					
					String nombre = personalDAO.findNombreCompletoByCodPers(dbpool, destino);
					
					//ICAPUNAY 05/10/2012 AJUSTES AOM 06A4T11 LABOR EXCEPCIONAL
					if (Constantes.MOV_LABOR_EXCEPCIONAL.equals(tipo) || Constantes.MOV_SOLICITUD_COMPENSACION.equals(tipo) ){//124 o 125						
						nombre = solicitud.get("codPers").toString().trim()+" - "+nombre;
					}
					//FIN ICAPUNAY 05/10/2012 AJUSTES AOM 06A4T11 LABOR EXCEPCIONAL					
					String texto = "";//add 23/11/2012 cambios glosas					
					//String texto = Utiles.textoCorreoProceso(dbpool, nombre, mensaje,"");//add 23/11/2012 cambios glosas

					//enviamos el mail al trabajador
					Map datos = new HashMap();
					//ICAPUNAY 05/10/2012 AJUSTES AOM 06A4T11 LABOR EXCEPCIONAL
					if (tipo.equals(Constantes.MOV_LABOR_EXCEPCIONAL) || tipo.equals(Constantes.MOV_SOLICITUD_COMPENSACION) ){//124  o 125
						datos.put("subject", "Resultado de Papeleta");
						if (tipo.equals(Constantes.MOV_LABOR_EXCEPCIONAL)){
							texto = Utiles.textoCorreoProcesoLaborExcepcional(dbpool, nombre, mensaje,"","aprobada");//add 23/11/2012 cambios glosas
						}
						if (tipo.equals(Constantes.MOV_SOLICITUD_COMPENSACION)){
							texto = Utiles.textoCorreoProceso(dbpool, nombre, mensaje,"");//add 23/11/2012 cambios glosas
						}						
					}else{//otros tipos solicitudes
						datos.put("subject", "Resultado de Solicitud");
						texto = Utiles.textoCorreoProceso(dbpool, nombre, mensaje,"");//add 23/11/2012 cambios glosas
					}
					
					if (Constantes.MOV_REINTEGRO_POR_DESCUENTO_ASISTENCIA.equals((String) tipoMov.get("mov"))||Constantes.MOV_REINTEGRO_POR_DESCUENTO_SUBSIDIO.equals((String) tipoMov.get("mov"))) {
						enviarMensajeNotificacionAprobacion( solicitud , mapa);
						return mensajes; 
					}
					
					//datos.put("subject", "Resultado de Solicitud");
					log.debug("aprobacion(texto final): "+mensaje);
					//ICAPUNAY 05/10/2012 AJUSTES AOM 06A4T11 LABOR EXCEPCIONAL
					datos.put("message", texto);
					datos.put("from", correoDAO.findCorreoByCodPers(dbpool,origen));
					datos.put("to", correoDAO.findCorreoByCodPers(dbpool,destino));
					QueueDAO queue = new QueueDAO();
					queue.enviaCorreo((HashMap)datos);
				}
			}			
		} 
		 catch (DAOException e) {
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
		return mensajes;
	}
/*      */	
	
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public ArrayList registrarSeguimiento(String dbpool, HashMap solicitud,
			HashMap mapa, String usuario) throws IncompleteConversationalState,
			RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		ArrayList mensajes = new ArrayList();
		try {
			
			log.debug("Entro a registrarSeguimiento : "+mapa);
			
			T1455DAO segDAO = new T1455DAO(dbpool);
			//PRAC-JCALLO
			//T1277CMPHome solHome = (T1277CMPHome) ServiceLocator.getInstance().getLocalHome(T1277CMPHome.JNDI_NAME);
			//T1455CMPHome segHome = (T1455CMPHome) ServiceLocator.getInstance().getLocalHome(T1455CMPHome.JNDI_NAME);
			Map prms = new HashMap();
			prms.put("cod_pers", solicitud.get("codPers"));
			prms.put("anno", solicitud.get("anno"));
			prms.put("numero", new Integer((String) solicitud.get("numero")));
			
			/*String numSeguimiento = segDAO.findSigSeg(dbpool,
					(String) solicitud.get("anno"), 
					new Short((String) solicitud.get("numero")),
					(String) solicitud.get("codPers"));*/
			//prac-jcallo
			log.debug("segDAO.findSigSeg(prms) ... prms:"+prms);
			String numSeguimiento = segDAO.findSigSeg(prms);
			log.debug("... numSeguimiento:"+numSeguimiento);
			//actualizamos la fecha de derivacion del ultimo seguimiento
			//prac-jcallo
			/*T1455CMPLocal ultimoSeg = segHome.findByPrimaryKey(
					new T1455CMPPK(
							(String) solicitud.get("anno"), 
							new Short((String) solicitud.get("numero")),
							(String) solicitud.get("uoOrig"), 
							(String) solicitud.get("codPers"), 
							(String) solicitud.get("seguim_act")));*/
			//PRAC-JCALLO
			//(String anno, Short numero, String u_organ, String cod_pers,String num_seguim)
			String seguim_act= String.valueOf((new Integer(numSeguimiento).intValue() - 1));
			Map dts = new HashMap();
			dts.put("anno", solicitud.get("anno"));
			dts.put("numero", new Integer((String) solicitud.get("numero")));
			dts.put("u_organ", solicitud.get("uoOrig"));
			dts.put("cod_pers", solicitud.get("codPers"));
			dts.put("num_seguim", seguim_act);
			log.debug("segDAO.findAllColumnsByKey(dts) ... dts:"+dts);
			Map ultimoSeg = segDAO.findAllColumnsByKey(dts);

			if (ultimoSeg != null && !ultimoSeg.isEmpty()) {

				java.sql.Timestamp fDeriv = new java.sql.Timestamp(System.currentTimeMillis());

				//actualizamos la fecha de derivacion
				//PRAC-JCALLO
				//ultimoSeg.setFechaDeriv(fDeriv);
				Map colums = new HashMap();
				colums.put("fmod", new FechaBean().getTimestamp());
				colums.put("fecha_deriv", new FechaBean().getTimestamp());
				ultimoSeg.put("columns", colums);
				log.debug("segDAO.updateCustomColumns(ultimoSeg)... ultimoSeg:"+ultimoSeg);
				segDAO.updateCustomColumns(ultimoSeg);
				

				String estado = (String) mapa.get("estado");
		
				//registramos el seguimiento
				String txtObs = (String) mapa.get("txtObs");
				if (txtObs.indexOf("&diasVenta=") > 0 ){
					txtObs = txtObs.substring(0,txtObs.indexOf("&diasVenta="));
				}
				
				//PRAC-JCALLO
				/*T1455CMPLocal seguimiento = segHome.create(
						(String) solicitud.get("anno"), 
						new Short((String) solicitud.get("numero")), 
						(String) mapa.get("codUOSeg"),
						(String) solicitud.get("codPers"), 
						numSeguimiento,
						fDeriv, 
						(String) mapa.get("accion"), 
						estado,
						(String) mapa.get("userOrig"), 
						(String) mapa.get("userDest"), 
						txtObs , //antes era (String) mapa.get("txtObs")
						new java.sql.Timestamp(System.currentTimeMillis()),
						usuario);*/
				//prac-jcallo
				//( anno, numero, u_organ, cod_pers, num_seguim, fecha_recep,				
				//fecha_deriv, accion_id, estado_id, cuser_orig, cuser_dest, tobserv, fcreacion, cuser_crea)
				Map dtos = new HashMap();
				dtos.put("anno", solicitud.get("anno"));
				dtos.put("numero", new Integer((String) solicitud.get("numero")));
				dtos.put("u_organ", mapa.get("codUOSeg"));
				dtos.put("cod_pers", solicitud.get("codPers"));
				dtos.put("num_seguim", numSeguimiento);
				dtos.put("fecha_recep", fDeriv);//duda sobre este campo
				dtos.put("fecha_deriv", null);
				dtos.put("accion_id", (String) mapa.get("accion"));
				dtos.put("estado_id", estado);
				dtos.put("cuser_orig", mapa.get("userOrig"));
				dtos.put("cuser_dest", mapa.get("userDest"));
				dtos.put("tobserv", txtObs);
				dtos.put("fcreacion", new FechaBean().getTimestamp());
				dtos.put("cuser_crea", usuario);
				
				//actualizamos el ultimo seguimiento de la solicitud
				//prac-jcallo
				//if (seguimiento != null) {
				if (segDAO.insertarSeguimientoSol(dtos)) {
					
					//PRAC-JCALLO
					/*T1277CMPLocal sol = solHome
							.findByPrimaryKey(new T1277CMPPK((String) solicitud
									.get("anno"), new Short((String) solicitud
									.get("numero")), (String) solicitud
									.get("uorgan"), (String) solicitud
									.get("codPers"), (String) solicitud
									.get("tipo"),
									(java.sql.Timestamp) solicitud
											.get("ffinicio")));*/
					//prac-jcallo
					pe.gob.sunat.rrhh.asistencia.dao.T1277DAO t1277dao = new pe.gob.sunat.rrhh.asistencia.dao.T1277DAO(dbpool);
					//Map columns, String anno, Short numero, String u_organ, String cod_pers, String licencia, Timestamp ffinicio
					Map params = new HashMap();
					
					Map columns = new HashMap();
					columns.put("seguim_act", numSeguimiento);
					params.put("columns", columns);
					//datos de la llave primaria
					params.put("anno", solicitud.get("anno"));
					params.put("numero", new Integer(solicitud.get("numero").toString()) );
					params.put("u_organ", solicitud.get("uorgan"));
					params.put("cod_pers", solicitud.get("codPers"));
					params.put("licencia", solicitud.get("tipo"));
					params.put("ffinicio", (java.sql.Timestamp) solicitud.get("ffinicio"));

					//actualizamos el ultimo seguimiento
					//sol.setSeguimAct(numSeguimiento);
					//prac-jcallo
					log.debug("t1277dao.updateCustomColumns(params)... params:"+params);
					t1277dao.updateCustomColumns(params);
					//t1277dao.updateRegistroSolicitud(dbpool, numSeguimiento, solicitud.get("anno").toString().trim(), new Integer(solicitud.get("numero").toString()), solicitud.get("uorgan").toString().trim(), solicitud.get("codPers").toString().trim(), solicitud.get("tipo").toString().trim(), solicitud.get("ffinicio").toString().trim());
					//t1274dao.updateCustomColumns(params);
				}

				//enviamos el correo
				if (estado.equals(Constantes.ESTADO_SEGUIMIENTO)) {

					T02DAO personalDAO = new T02DAO();
					T1279DAO movDAO = new T1279DAO();
					CorreoDAO correoDAO = new CorreoDAO();
					
					String tipo = solicitud.get("tipo") != null ? ((String) solicitud
							.get("tipo")).trim()
							: "";
					HashMap tipoMov = movDAO.findByMov(dbpool, tipo);

					String origen = (String) mapa.get("userOrig");
					String destino = (String) mapa.get("userDest");
					String tipoSol = (String) tipoMov.get("descrip");

					String mensaje = "Ud. ha recibido una nueva solicitud electr&oacute;nica de <strong>"
							+ tipoSol + "</strong>.";
					
					ResourceBundle bundle = ResourceBundle.getBundle("pe.gob.sunat.sp.asistencia.sirh");
					String servidorIP = bundle.getString("servidorIP");
					String programa = bundle.getString("programa1");

					//Cifrador cifrador = new Cifrador();				
					//String url = cifrador.encriptaURL(destino, programa, strURL,paramsURL);

					//String url = new MenuCliente().generaInvocacionURL(programa);
					String url = new MenuCliente().generaInvocacionURL(programa,null,true,MenuCliente.INTRANET,"/ol-ti-iaasistencia/asisS11Alias");
					
					//String strURL = "http://"+servidorIP+"/cl-at-iamenu"+url;
					String strURL = "http://"+servidorIP+url;
					log.debug("URL "+url);
					log.debug("strURL "+strURL);
					
					String nombre = personalDAO.findNombreCompletoByCodPers(dbpool, destino);
					String texto = Utiles.textoCorreoProceso(dbpool, nombre, mensaje,strURL);

					try {//AGONZALESF -PAS20171U230200001 - solicitud de reintegro coloque trycatch
					//enviamos el mail al trabajador
					HashMap datos = new HashMap();
					datos.put("subject", "Envio de Solicitud");
					datos.put("message", texto);
					datos.put("from", correoDAO.findCorreoByCodPers(dbpool,origen));
					datos.put("to", correoDAO.findCorreoByCodPers(dbpool,destino));

					QueueDAO queue = new QueueDAO();
					queue.enviaCorreo(datos);
						
					} catch (Exception e) {
						log.error("Error en envio de correo");
					}
					
				}
			}

		} catch (Exception e) {
			log.error(e);
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
	public ArrayList modificarSeguimientoAdministracion(String dbpool, HashMap solicitud, //jquispecoi 03/2014
			String usuario) throws IncompleteConversationalState,
			RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		ArrayList mensajes = new ArrayList();
		try {
			log.debug("Entro a modificarSeguimientoAdministracion : ");
			
			T1455DAO segDAO = new T1455DAO(dbpool);

			Map prms = new HashMap();
			prms.put("cod_pers", solicitud.get("codPers"));
			prms.put("anno", solicitud.get("anno"));
			prms.put("numero", new Integer((String) solicitud.get("numero")));
			
			log.debug("segDAO.findSigSeg(prms) ... prms:"+prms);
			String numSeguimiento = segDAO.findSigSeg(prms);			//nro seg

			String seguim_act= String.valueOf((new Integer(numSeguimiento).intValue() - 1));
			Map dts = new HashMap();
			dts.put("anno", solicitud.get("anno"));
			dts.put("numero", new Integer((String) solicitud.get("numero")));
			dts.put("u_organ", solicitud.get("uoOrig"));
			dts.put("cod_pers", solicitud.get("codPers"));
			dts.put("num_seguim", seguim_act);
			log.debug("segDAO.findAllColumnsByKey(dts) ... dts:"+dts);
			Map ultimoSeg = segDAO.findAllColumnsByKey(dts);			//ultimo seg

			if (ultimoSeg != null && !ultimoSeg.isEmpty()) {

				//actualizamos la fecha de modificacion
				Map colums = new HashMap();
				colums.put("fmod", new FechaBean().getTimestamp());
				colums.put("cuser_mod", usuario);
				colums.put("accion_id", Constantes.ACCION_RECHAZAR);  //forzamos rechazo
				ultimoSeg.put("columns", colums);
				log.debug("segDAO.updateCustomColumns(ultimoSeg)... ultimoSeg:"+ultimoSeg);
				if(((String)ultimoSeg.get("accion_id")).trim().equals(Constantes.ACCION_APROBAR) && 
				   ((String)ultimoSeg.get("estado_id")).trim().equals(Constantes.ESTADO_CONCLUIDA)) 
				segDAO.updateCustomColumns(ultimoSeg);
			
			}

		} catch (Exception e) {
			log.error(e);
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
	public ArrayList registrarLicencia(String dbpool, HashMap solicitud,
			String usuario) throws IncompleteConversationalState,
			RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		ArrayList mensajes = new ArrayList();
		if (log.isDebugEnabled()) log.debug("SolicitudFacade-metodo:registrarLicencia");//ICAPUNAY 09/02/2012-POR PRUEBAS DESARROLLO
		try {

			DataSource dsOracle = sl.getDataSource("java:comp/env/pool_oracle");
			
			T1276DAO periodoDAO = new T1276DAO();
			T1279DAO movDAO = new T1279DAO();
			T01DAO paramDAO = new T01DAO();//add icr 18/11
			//EBV 10/04/2012 - Inicio 
			//T130DAO t130DAO = new T130DAO(sl.getDataSource("java:comp/env/jdbc/dcsp"));
			//EBV 10/04/2012 - Fin
			
			//T1273CMPHome cmpHome = (T1273CMPHome) sl.getLocalHome(T1273CMPHome.JNDI_NAME);

			//PRAC-ASANCHEZ 10/06/2009
			AsistenciaFacadeHome asisHome = (AsistenciaFacadeHome) ServiceLocator.getInstance().getRemoteHome(
					AsistenciaFacadeHome.JNDI_NAME,AsistenciaFacadeHome.class);
			AsistenciaFacadeRemote asisRemote = asisHome.create();
			//Map mov = new HashMap();
			String ind;
			//
			int dias = 0;//ICAPUNAY 07/06 PASE 2012-64 LABOR EXCEPCIONAL
			String regimenCol = (String) solicitud.get("regimenCol");//ICR - 28/11/2012 PAS20124E550000064 labor excepcional
			if (log.isDebugEnabled()) log.debug("registrarLicencia(regimenCol): "+regimenCol);//ICR - 28/11/2012 PAS20124E550000064 labor excepcional
						
			String codPers = (String) solicitud.get("codPers");
			String tipo = (String) solicitud.get("tipo");
			String fechaIni = Utiles.timeToFecha((java.sql.Timestamp) solicitud
					.get("ffinicio"));
			String fechaFin = Utiles.timeToFecha((java.sql.Timestamp) solicitud
					.get("ffin"));
			
			HashMap tipoMov = movDAO.findByMov(dbpool, tipo);
			
			HashMap params = new HashMap();
			params.put("userOrig", codPers);
			params.put("fechaIni", fechaIni);
			params.put("fechaFin", fechaFin);
			params.put("dbpool", dbpool);
			params.put("regimenCol", regimenCol);//ICR - 28/11/2012 PAS20124E550000064 labor excepcional
			params.put("dias_ini", new Integer(-1));	//jquispecoi 03/2014 

			if (log.isDebugEnabled()) log.debug("antes de entrar a metodo:validaLicencia");//ICAPUNAY 09/02/2012-POR PRUEBAS DESARROLLO
			if (log.isDebugEnabled()) log.debug("params: "+params);//ICAPUNAY 09/02/2012-POR PRUEBAS DESARROLLO
			if (log.isDebugEnabled()) log.debug("tipoMov: "+tipoMov);//ICAPUNAY 09/02/2012-POR PRUEBAS DESARROLLO
			//add ICR 20/12 Ajustes Sol. Compen(125) de Pase Labor Excepcional y Compensaciones
			if (tipo.trim().equalsIgnoreCase(Constantes.MOV_SOLICITUD_COMPENSACION)){
				if (log.isDebugEnabled()) log.debug("va entrar metodo:validaLicenciaCompJefe");
				mensajes = validaLicenciaCompJefe(params,tipoMov,false); 			
			}else{
				if (log.isDebugEnabled()) log.debug("va entrar metodo:validaLicencia");
				mensajes = validaLicencia(params,tipoMov,false);
			}
			//mensajes = validaLicencia(params,tipoMov,false);
			//add ICR 20/12 Ajustes Sol. Compen(125) de Pase Labor Excepcional y Compensaciones

			if (mensajes.isEmpty()) {

				String periodoActual = (periodoDAO.findPeriodoActual(dbpool)).getPeriodo();
				java.sql.Timestamp fLicI = (java.sql.Timestamp) solicitud.get("ffinicio");
				java.sql.Timestamp fLicF = (java.sql.Timestamp) solicitud.get("ffin");

				//LICENCIA NACIMIENTO DE HIJO
				/*
				if (tipo.trim().equalsIgnoreCase(Constantes.LICENCIA_NACIMIENTO)) {

					TurnoTrabajoFacadeHome facadeHome = (TurnoTrabajoFacadeHome) ServiceLocator
					.getInstance().getRemoteHome(TurnoTrabajoFacadeHome.JNDI_NAME,
							TurnoTrabajoFacadeHome.class);
					TurnoTrabajoFacadeRemote turno = facadeHome.create();
					
					HashMap datos = new HashMap();
					datos.put("dbpool",dbpool);
					datos.put("codPers",(String) solicitud.get("codPers"));
					datos.put("codUO",(String) solicitud.get("uorgan"));
					datos.put("fechaLic",Utiles.timeToFecha(fLicI));
					datos.put("fechaGen",Utiles.obtenerFechaActual());
					datos.put("horComp","1");
					datos.put("usuario",usuario);
					
					//registramos los turnos de compensacion
					turno.registrarTurnoCompensa(datos);
				}				
*/				
				if (log.isDebugEnabled()) log.debug("ANTES DE LICENCIAS COMPENSABLES - solicitud: "+ solicitud);
				if (log.isDebugEnabled()) log.debug("ANTES DE LICENCIAS COMPENSABLES - TIPO: "+ tipo);
				
				//LICENCIAS COMPENSABLES
				if (tipo.trim().equalsIgnoreCase(Constantes.PERMISO_CAPACITACION) ||
					tipo.trim().equalsIgnoreCase(Constantes.LICENCIA_TITULACION) ||
					//ICAPUNAY 03/02/2012 - PAS20122A550000118 Solicitud de Feriado Compensable - aprobado por EBENAVID
					//tipo.trim().equalsIgnoreCase(Constantes.LICENCIA_ELECCIONES)) {
					tipo.trim().equalsIgnoreCase(Constantes.LICENCIA_ELECCIONES) ||
					tipo.trim().equalsIgnoreCase(Constantes.FERIADO_COMPENSABLE)) {
					//FIN ICAPUNAY 03/02/2012 - PAS20122A550000118 Solicitud de Feriado Compensable - aprobado por EBENAVID

					TurnoTrabajoFacadeHome facadeHome = (TurnoTrabajoFacadeHome) ServiceLocator
					.getInstance().getRemoteHome(TurnoTrabajoFacadeHome.JNDI_NAME,
							TurnoTrabajoFacadeHome.class);
					TurnoTrabajoFacadeRemote turno = facadeHome.create();
					
					//ICR 25/09/2012 PAS20124E550000721 AJUSTES A 8 HORAS DE RECUPERACION PARA DL 1057(CAS) Y DL 276-728 POR CADA DIA DE FERIADO COMPENSABLE
					/*int diasComp = turno.calcularHorasCompensa(dbpool,
							(String) solicitud.get("codPers"),
							Utiles.timeToFecha(fLicI), 
							Utiles.timeToFecha(fLicF));*/
					int diasComp = turno.calcularHorasCompensaModif(dbpool,
							(String) solicitud.get("codPers"),
							Utiles.timeToFecha(fLicI), 
							Utiles.timeToFecha(fLicF));
					log.debug("horas de compensacion total(diasComp): "+diasComp);
					//FIN ICR 25/09/2012 PAS20124E550000721 AJUSTES A 8 HORAS DE RECUPERACION PARA DL 1057(CAS) Y DL 276-728 POR CADA DIA DE FERIADO COMPENSABLE
					
					HashMap datos = new HashMap();
					datos.put("dbpool",dbpool);
					datos.put("codPers",(String) solicitud.get("codPers"));
					datos.put("codUO",(String) solicitud.get("uorgan"));
					datos.put("fechaLic",Utiles.timeToFecha(fLicF));
					//datos.put("fechaGen",Utiles.obtenerFechaActual());
					datos.put("fechaGen", Utiles.timeToFecha(fLicF));//JVV-19/03/2012-SAU20124F310000037
					datos.put("diasComp",""+diasComp);
					datos.put("horComp","1");
					datos.put("usuario",usuario);
					datos.put("fechaIniLic",Utiles.timeToFecha(fLicI));//ICR 07/01/2013 Recuperacion desde 07/01/2013 a solicitudes de feriados del 24/12/2012 o 31/12/2012 en seguimiento
					log.debug("mapa datos: "+datos);//ICR 07/01/2013 Recuperacion desde 07/01/2013 a solicitudes de feriados del 24/12/2012 o 31/12/2012 en seguimiento
					
					//JRR - 20/05/2009
					datos.put("anno", (solicitud.get("anno")!=null?solicitud.get("anno"):null));
					datos.put("num_refer", (solicitud.get("numero")!=null?solicitud.get("numero"):null));
					
					//registramos los turnos de compensacion
					turno.registrarTurnoCompensa(datos);
				}			
				
				//registramos la licencia
				int numDias = Utiles.obtenerDiasDiferencia(fechaIni, fechaFin) + 1;

				//PRAC-ASANCHEZ 10/06/2009
				ind = (String)tipoMov.get("ind_dias"); //si ind es 1 solo se cuentan dias habiles, si es 0: dias por igual
				log.debug("MENSAJE DE ANDRES A VER SIENTRA AL IF");
				log.debug("tipo");
				log.debug(tipo);
				log.debug("ind");
				log.debug(ind);
				
				log.debug("MENSAJE DE ANDRES A VER SIENTRA AL IF");
				if(tipo.trim().equals(Constantes.LICENCIA_NACIMIENTO) && ind.equals(Constantes.ACTIVO)){
					numDias = asisRemote.obtenerDiasHabilesDiferencia(dbpool, fechaIni, fechaFin) + 1;
					//numDias=Utiles.obtenerDiasDiferencia(fechaIni, fechaFin);
					if(log.isDebugEnabled())log.debug("fechaIni: " + fechaIni);
					if(log.isDebugEnabled())log.debug("fechaFin: " + fechaFin);
					if(log.isDebugEnabled())log.debug("numDias LicNac: " + numDias);
				}
				if(tipo.trim().equals(Constantes.LICENCIA_MATRIMONIO) && ind.equals(Constantes.ACTIVO)){
					numDias = asisRemote.obtenerDiasHabilesDiferencia(dbpool, fechaIni, fechaFin) + 1;	
					if(log.isDebugEnabled())log.debug("fechaIni: " + fechaIni);
					if(log.isDebugEnabled())log.debug("fechaFin: " + fechaFin);
					if(log.isDebugEnabled())log.debug("numDias: " + numDias);
				}
				
				if(tipo.trim().equals(Constantes.FALLECIMIENTO_FAMILIAR) && ind.equals(Constantes.ACTIVO)){
					numDias = asisRemote.obtenerDiasHabilesDiferencia(dbpool, fechaIni, fechaFin) + 1;
					if(log.isDebugEnabled())log.debug("fechaIni: " + fechaIni);
					if(log.isDebugEnabled())log.debug("fechaFin: " + fechaFin);
					if(log.isDebugEnabled())log.debug("numDias: " + numDias);
				}
				//				
				
				String annoLicencia = Utiles.obtenerAnhoActual();
				if (tipo.trim().equalsIgnoreCase(Constantes.LICENCIA_ENFERMEDAD)) {
					annoLicencia = Utiles.dameAnho(fechaIni);
				}

				OracleSequenceDAO seqDAO = new OracleSequenceDAO();
				String numero = seqDAO.getSequenceDS(dsOracle,Constantes.SEQ_LICENCIA);

				//LICENCIA POR OMPENSACION
				//EBV-ICR-MTM PAS20124E550000064 labor excepcional			
				if (tipo.trim().equalsIgnoreCase(Constantes.MOV_SOLICITUD_COMPENSACION)) {
					if (log.isDebugEnabled()) log.debug("entro a LE registrarLicencia");
					if (log.isDebugEnabled()) log.debug("fechaIni: "+fechaIni);
					if (log.isDebugEnabled()) log.debug("fechaFin: "+fechaFin);
					if (log.isDebugEnabled()) log.debug("regimenCol: "+regimenCol);
					//int descuento = 0;
					int diferencia = Utiles.obtenerDiasDiferencia(fechaIni, fechaFin)+1;
					int diferencia2 = 0;
					int diferencia3 = 0;
					boolean compensar = true;
					T1270DAO tpDAO = new T1270DAO();					
					float[] horaXDia = new float[diferencia+1];
					dias = 0;
					String fechaEval="";//add icr
					
					for (int i = 0; i < diferencia; i++) {
						log.debug("inicio for de diferencia");
						log.debug("i :" + i);
						log.debug("diferencia :" + diferencia);
						log.debug("fechaIni :" + fechaIni);
						String fechaSig = Utiles.dameFechaSiguiente(fechaIni, i);
						log.debug("fechaSig :" + fechaSig);			
						BeanTurnoTrabajo turno1 = tpDAO.joinWithT45ByCodPersFecha_Controlado_OperAdm1dia(dbpool, codPers, fechaSig);
						if (turno1 != null){
							log.debug("turno de 1 dia");
							Date fechaDate = new FechaBean(turno1.getFechaFin()).getSQLDate();
							log.debug("finTurno :" + fechaDate);
							diferencia2 = Utiles.obtenerDiasDiferencia(fechaSig, new FechaBean(fechaDate.toString(),"yyyy-MM-dd").getFormatDate("dd/MM/yyyy").toString())+1;							
							log.debug("diferencia2 :" + diferencia2);
							diferencia3 = diferencia - i;
							log.debug("diferencia3 :" + diferencia3);
							if(diferencia2 > 0){
								log.debug("entrada1(diferencia2 > 0)");
								if (diferencia3 > diferencia2){
									log.debug("registrarLicencia entro(diferencia3>diferencia2)");									
									for ( int j = i;j<i+diferencia2;j++){
										compensar = true;
										log.debug("j: "+j);
										log.debug("i: "+i);	
										fechaEval = Utiles.dameFechaSiguiente(fechaIni,j);//fechaSig debe ser fechaIni										
										log.debug("fechaEval :" + fechaEval);
										if (fechaEval!=null && !fechaEval.equals("")){										
											log.debug("entro a validar extremos fechaIni o fechaFin(fechaEval): "+fechaEval);
											if(!turno1.isOperativo()){//adm
												log.debug("turno es adm");
												if ((paramDAO.findByFechaFeriado(dbpool,fechaEval) || Utiles.isWeekEnd(fechaEval))) {
													log.debug("dia es feriado o fin de semana(fechaEval): "+fechaEval);												
													compensar = false;													
												}
											}																					
										}else{
											log.debug("no existe fechaEval: " + fechaEval);
										}
										if(compensar){
											log.debug("SI compensa fecha: " + fechaEval);
											dias = dias + 1;
											log.debug("dias :" + dias);
											if (!regimenCol.equals("")){					//jquispe: ini(26/07/2013)											
												if (Math.abs(Utiles.obtenerHorasDiferencia(turno1.getHoraIni(), turno1.getHoraFin()))<=8){//<=8 horas --- queda 8 horas																									
													horaXDia[dias] =  Math.abs(Utiles.obtenerHorasDiferencia(turno1.getHoraIni(), turno1.getHoraFin()));
													log.debug("horaXDia1["+dias+"](cas_noCas <=8): " + horaXDia[dias]);																						
												}else{													
													horaXDia[dias] =  Math.abs(Utiles.obtenerHorasDiferencia(turno1.getHoraIni(), turno1.getHoraFin())-1);
													log.debug("horaXDia1["+dias+"](cas_noCas>8): " + horaXDia[dias]);
												}								
											}												//jquispe: fin	
										}																			
									}
									i=i+(diferencia2-1);									
									log.debug("diferencia2 aca :" + diferencia2);
									log.debug("i final aca : " + i);							
									log.debug("dias final aca: " + dias);																														
								}else{
									log.debug("registrarLicencia entro(diferencia3<=diferencia2)");									
									for ( int j = i;j<i+diferencia3;j++){
										compensar = true;
										log.debug("j: "+j);
										log.debug("i: "+i);
										fechaEval = Utiles.dameFechaSiguiente(fechaIni,j);//fechaSig debe ser fechaIni										
										log.debug("fechaEval :" + fechaEval);
										if (fechaEval!=null && !fechaEval.equals("")){										
											log.debug("entro a validar extremos fechaIni o fechaFin(fechaEval): "+fechaEval);
											if(!turno1.isOperativo()){//adm
												log.debug("turno es adm");
												if ((paramDAO.findByFechaFeriado(dbpool,fechaEval) || Utiles.isWeekEnd(fechaEval))) {
													log.debug("dia es feriado o fin de semana(fechaEval): "+fechaEval);												
													compensar = false;													
												}
											}																					
										}else{
											log.debug("no existe fechaEval: " + fechaEval);
										}
										if(compensar){
											log.debug("SI compensa fecha: " + fechaEval);
											dias = dias + 1;
											log.debug("dias :" + dias);
											if (!regimenCol.equals("")){					//jquispe: ini(26/07/2013)											
												if (Math.abs(Utiles.obtenerHorasDiferencia(turno1.getHoraIni(), turno1.getHoraFin()))<=8){//<=8 horas --- queda 8 horas																										
													horaXDia[dias] =  Math.abs(Utiles.obtenerHorasDiferencia(turno1.getHoraIni(), turno1.getHoraFin()));
													log.debug("horaXDia2["+dias+"](cas_noCas <=8): " + horaXDia[dias]);																						
												}else{													
													horaXDia[dias] =  Math.abs(Utiles.obtenerHorasDiferencia(turno1.getHoraIni(), turno1.getHoraFin())-1);
													log.debug("horaXDia2["+dias+"](cas_noCas>8): " + horaXDia[dias]);
												}								
											}												//jquispe: fin		
										}																
									}									
									i=i+(diferencia3-1);									
									log.debug("diferencia3 aca :" + diferencia3);
									log.debug("i final aca : " + i);								
									log.debug("dias final aca: " + dias);																
								}
							}else{
								log.debug("entrada2(diferencia2 <= 0)");
								compensar = true;
								log.debug("fechaSig :" + fechaSig);	
								if (fechaSig!=null && !fechaSig.equals("")){								
									log.debug("entro3 a validar extremos fechaIni o fechaFin(fechaSig): "+fechaSig);
									if(!turno1.isOperativo()){//adm
										log.debug("turno es adm");
										if ((paramDAO.findByFechaFeriado(dbpool,fechaSig) || Utiles.isWeekEnd(fechaSig))) {
											log.debug("dia3 es feriado o fin de semana(fechaSig): "+fechaSig);											
											compensar = false;										
										}
									}																	
								}else{
									log.debug("no existe fechaSig:" + fechaSig);
								}	
								if (compensar){
									dias = dias + 1;								
									log.debug("dias final3 aca: " + dias);
									if (!regimenCol.equals("")){					//jquispe: ini(26/07/2013)											
										if (Math.abs(Utiles.obtenerHorasDiferencia(turno1.getHoraIni(), turno1.getHoraFin()))<=8){//<=8 horas --- queda 8 horas																										
											horaXDia[dias] =  Math.abs(Utiles.obtenerHorasDiferencia(turno1.getHoraIni(), turno1.getHoraFin()));
											log.debug("horaXDia3["+dias+"](cas_noCas <=8): " + horaXDia[dias]);																						
										}else{													
											horaXDia[dias] =  Math.abs(Utiles.obtenerHorasDiferencia(turno1.getHoraIni(), turno1.getHoraFin())-1);
											log.debug("horaXDia3["+dias+"](cas_noCas>8): " + horaXDia[dias]);
										}								
									}												//jquispe: fin		
								}															
							}
						}else{
							log.debug("registrarLicencia-ENTRO ESTE BLOQUE");
							BeanTurnoTrabajo turno2 = tpDAO.joinWithT45ByCodPersFecha_Controlado_Oper2dias(dbpool, codPers, fechaSig);
							if (turno2 != null){
								log.debug("turno de 2 dias");									
								log.debug("fechaSig :" + fechaSig);	
								dias = dias + 1;
								log.debug("dias final4 aca: " + dias);
								if (!regimenCol.equals("")){					//jquispe: ini(26/07/2013)											
									if (Math.abs(Utiles.obtenerHorasDiferencia(turno2.getHoraIni(), turno2.getHoraFin()))<=8){//<=8 horas --- queda 8 horas																										
										//horaXDia[dias] =  Math.abs(Utiles.obtenerHorasDiferencia(turno2.getHoraIni(), turno2.getHoraFin())); ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
										horaXDia[dias] = Math.abs(Utiles.obtenerHorasDiferencia(turno2.getHoraIni(), "24:00:00")) + Math.abs(Utiles.obtenerHorasDiferencia("00:00:00", turno2.getHoraFin())); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
									   	log.debug("horaXDia4["+dias+"](cas_noCas <=8): " + horaXDia[dias]);																						
									}else{													
										//horaXDia[dias] =  Math.abs(Utiles.obtenerHorasDiferencia(turno2.getHoraIni(), turno2.getHoraFin())-1); ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
										horaXDia[dias] = (Math.abs(Utiles.obtenerHorasDiferencia(turno2.getHoraIni(), "24:00:00")) + Math.abs(Utiles.obtenerHorasDiferencia("00:00:00", turno2.getHoraFin())))-1; //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
										log.debug("horaXDia4["+dias+"](cas_noCas>8): " + horaXDia[dias]);
									}								
								}												//jquispe: fin									
							}
							//add icr
							else{
								String fechaAntes = Utiles.dameFechaAnterior(fechaSig, 1);
								BeanTurnoTrabajo turno3 = tpDAO.joinWithT45ByCodPersFecha_Controlado_Oper2dias(dbpool, codPers, fechaAntes);							
								if (turno3!=null){
									log.debug("turno de 2 dias(dia anterior): "+fechaAntes);
									if (fechaAntes.compareTo(fechaIni)>=0 && fechaAntes.compareTo(fechaFin)<=0){
										log.debug("NO SUMA DIAS NI HORAS");										
									}else{	
										log.debug("SI SUMA DIAS Y HORAS");
										log.debug("fechaAntes :" + fechaAntes);
										dias = dias + 1;
										log.debug("dias final5 aca: " + dias);
										if (!regimenCol.equals("")){					//jquispe: ini(26/07/2013)											
											if (Math.abs(Utiles.obtenerHorasDiferencia(turno3.getHoraIni(), turno3.getHoraFin()))<=8){//<=8 horas --- queda 8 horas																										
												//horaXDia[dias] =  Math.abs(Utiles.obtenerHorasDiferencia(turno3.getHoraIni(), turno3.getHoraFin())); ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
												horaXDia[dias] = Math.abs(Utiles.obtenerHorasDiferencia(turno3.getHoraIni(), "24:00:00")) + Math.abs(Utiles.obtenerHorasDiferencia("00:00:00", turno3.getHoraFin())); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
												log.debug("horaXDia5["+dias+"](cas_noCas <=8): " + horaXDia[dias]);																						
											}else{													
												//horaXDia[dias] =  Math.abs(Utiles.obtenerHorasDiferencia(turno3.getHoraIni(), turno3.getHoraFin())-1); ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
												horaXDia[dias] = (Math.abs(Utiles.obtenerHorasDiferencia(turno3.getHoraIni(), "24:00:00")) + Math.abs(Utiles.obtenerHorasDiferencia("00:00:00", turno3.getHoraFin())))-1; //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
												log.debug("horaXDia5["+dias+"](cas_noCas>8): " + horaXDia[dias]);
											}								
										}												//jquispe: fin										
									}								
								}							
							}
							//fin add
						}						
					}								
					if (log.isDebugEnabled()) log.debug("registrarLicencia(dias) final: "+dias);		
					T4819DAO t4819DAO = new T4819DAO(sl.getDataSource("java:comp/env/jdbc/dgsp"));
					T4820DAO t4820DAO = new T4820DAO(sl.getDataSource("java:comp/env/jdbc/dgsp"));
					T4821DAO t4821DAO = new T4821DAO(sl.getDataSource("java:comp/env/jdbc/dgsp"));
					Map datos1 = new HashMap();
					
					pe.gob.sunat.sp.dao.T02DAO empleadoDAO = new pe.gob.sunat.sp.dao.T02DAO();		//ICR 08/05/2015-PAS20155E230000154-labor antigua  (autorizacion y compensacion) y labor nueva (sol. compensacion) mayor o igual fecha ingreso a sunat
					Map mapaEmpleado = new HashMap();
					Date fecha_ingreso;	
					mapaEmpleado = empleadoDAO.findEmpleado("java:comp/env/jdbc/dgsp",codPers);	
					fecha_ingreso = (mapaEmpleado!=null && !mapaEmpleado.isEmpty())?((Date)mapaEmpleado.get("t02f_ingsun")):null;	
					datos1.put("fecha_ingreso", fecha_ingreso);										//ICR 08/05/2015 - fecha de ingreso no puede ser null
					
					
					datos1.put("cod_pers", codPers);
					List fechas = t4819DAO.findHorasLaborAutorizadasConSaldo(datos1);
					Map detalle = new HashMap();
					if (log.isDebugEnabled()) log.debug("fechas: "+fechas);
					int tempnum = 0;
					if (fechas != null && fechas.size() >0){
						Map he = new HashMap();
						int minutos = 0;
						int i=0;
						for (int j=1;j<=dias;j++){
							i=0;
							//Calculamos la cantidad de minutos a compensar
							detalle = t4821DAO.listaDetalle(solicitud.get("anno").toString(), solicitud.get("numero").toString(), (String) solicitud.get("uorgan"), (String) solicitud.get("codPers"), (String) solicitud.get("tipo"), j);							
							fechas = t4819DAO.findHorasLaborAutorizadasConSaldo(datos1);						
							//minutos = 480;
							minutos = (int)horaXDia[j] * 60;
							while (minutos>0){
								if (log.isDebugEnabled()) log.debug("entro a while");
								if (fechas != null && fechas.size() >0){
									if (log.isDebugEnabled()) log.debug("fechas2: "+fechas);
									he = (HashMap) fechas.get(i);
								}								
								if(he!=null && !he.isEmpty()){//adicionado 29/05/2012
									if (log.isDebugEnabled()) log.debug("he: "+he);
									if (log.isDebugEnabled()) log.debug("minutos: "+minutos);
									if (log.isDebugEnabled()) log.debug("he.getAcumulado(): "+he.get("cnt_min_comp_sal"));
									if (minutos >= Integer.parseInt(he.get("cnt_min_comp_sal").toString())){								
										//Actualizar a cero ese dia y obtener nueva fecha
										tempnum = 0;										
										if (log.isDebugEnabled()) log.debug("he.getFechaAutorizacion(): "+he.get("fec_perm_desc"));
										if (log.isDebugEnabled()) log.debug("he.getHoraIni(): "+he.get("hor_ini_perm"));
										if (log.isDebugEnabled()) log.debug("tempnum: "+tempnum);
										if (log.isDebugEnabled()) log.debug("usuario: "+usuario);
										he.put("tempnum", new Integer(tempnum));
										he.put("usuario", usuario);
										he.put("indicador", "3");
										he.put("min_usado", he.get("cnt_min_comp_sal").toString());
										t4819DAO.updateLabor(he);
										if (detalle != null && !detalle.isEmpty()){
											if (log.isDebugEnabled()) log.debug("a insertar en t4820(detalle): "+detalle);
											if (log.isDebugEnabled()) log.debug("a insertar en t4820(he): "+he);
											t4820DAO.insertaDetalle(detalle,he);
										}									
										if (log.isDebugEnabled()) log.debug("minutos"+minutos);
										minutos = minutos - Integer.parseInt(he.get("cnt_min_comp_sal").toString());
										if (log.isDebugEnabled()) log.debug("minutos: "+minutos);
										i = i + 1;
										if (log.isDebugEnabled()) log.debug("i: "+i);
									} else {
											//Actualizar dias y a cero el dia									
											if (log.isDebugEnabled()) log.debug("he.getAcumulado(): "+Integer.parseInt(he.get("cnt_min_comp_sal").toString()));
											if (log.isDebugEnabled()) log.debug("dias: "+minutos);
											tempnum =  Integer.parseInt(he.get("cnt_min_comp_sal").toString()) - minutos;	
											if (log.isDebugEnabled()) log.debug("minutos: "+minutos);
											if (log.isDebugEnabled()) log.debug("he.getFechaAutorizacion(): "+he.get("fec_perm_desc"));
											if (log.isDebugEnabled()) log.debug("he.getHoraIni(): "+he.get("hor_ini_perm"));
											if (log.isDebugEnabled()) log.debug("tempnum: "+tempnum);
											if (log.isDebugEnabled()) log.debug("usuario: "+usuario);
											he.put("tempnum", new Integer(tempnum));
											he.put("usuario", usuario);
											he.put("indicador", "1");
											he.put("min_usado", String.valueOf(minutos));
											t4819DAO.updateLabor(he);
											if (detalle != null && !detalle.isEmpty()){
												if (log.isDebugEnabled()) log.debug("a insertar en t4820(detalle2): "+detalle);
												if (log.isDebugEnabled()) log.debug("a insertar en t4820(he2): "+he);
												t4820DAO.insertaDetalle(detalle,he);
											}										
											minutos = 0;
											if (log.isDebugEnabled()) log.debug("minutos: "+minutos);
										}
									if (log.isDebugEnabled()) log.debug("he.getFechaAutorizacion(): "+he.get("fec_perm_desc"));
								}//adicionado 29/05/2012								
							}
						}
						if (log.isDebugEnabled()) log.debug("ACABE SOLICITUD COMPENSACION");					
					}
				}//FIN LICENCIA POR DIA DE COMPENSACION

				//prac-jcallo
				/*cmpHome.create(periodoActual,
						annoLicencia, new java.lang.Short(numero),
						(String) solicitud.get("uorgan"), fLicI, fLicF,
						codPers, numDias, tipo, (String) solicitud.get("anno"),
						(String) solicitud.get("numero"), (String) solicitud
								.get("uorgan"), (String) solicitud
								.get("txtObs"), new java.sql.Timestamp(System
								.currentTimeMillis()), usuario);*/
				//prac-jcallo
				T1273DAO t1273dao = new T1273DAO(dbpool);
				
				Map dts = new HashMap();
				dts.put("periodo", periodoActual);
				dts.put("anno", annoLicencia);
				dts.put("numero", new Integer(numero));
				dts.put("u_organ", (String) solicitud.get("uorgan"));
				dts.put("ffinicio", fLicI);
				dts.put("ffin", fLicF);
				dts.put("cod_pers", codPers);
				//ICAPUNAY 07/06 PASE 2012-64 LABOR EXCEPCIONAL
				if (tipo.trim().equalsIgnoreCase(Constantes.MOV_SOLICITUD_COMPENSACION)) {
					dts.put("qdias", new Integer(dias));
				}else{
					dts.put("qdias", new Integer(numDias));
				}
				//FIN ICAPUNAY 07/06 PASE 2012-64 LABOR EXCEPCIONAL
				dts.put("licencia", tipo);
				dts.put("anno_ref", (String) solicitud.get("anno"));
				//JR 28/05/2009
				//dts.put("numero_ref", (String) solicitud.get("numero"));
				String numeroRef = (String) solicitud.get("numero");
				dts.put("numero_ref", (numeroRef!=null&&!numeroRef.trim().equals("0"))?numeroRef:null);
				dts.put("area_ref", (String) solicitud.get("uorgan"));
				dts.put("observ", (String) solicitud.get("txtObs"));
				dts.put("fcreacion", new FechaBean().getTimestamp());
				dts.put("cuser_crea", usuario);//revisar todaviaaa
				log.debug("t1273dao.registrarLicenciaGeneral(dts)... dts:"+dts);
				//ICAPUNAY 07/06 PASE 2012-64 LABOR EXCEPCIONAL
				if (tipo.trim().equalsIgnoreCase(Constantes.MOV_SOLICITUD_COMPENSACION)) {
					if(dias>0){
						t1273dao.registrarLicenciaGeneral(dts);
					}					
				}else{
					t1273dao.registrarLicenciaGeneral(dts);
				}
				//FIN ICAPUNAY 07/06 PASE 2012-64 LABOR EXCEPCIONAL				
				
				//LICENCIA POR ENFERMEDAD
				if (tipo.trim().equalsIgnoreCase(Constantes.LICENCIA_ENFERMEDAD)) {
					
					//prac-jcallo
					
					/*T1274CMPHome medicaHome = (T1274CMPHome) ServiceLocator.getInstance().getLocalHome(
									T1274CMPHome.JNDI_NAME);

					medicaHome.create(
							codPers, tipo, periodoActual, annoLicencia,
							new java.lang.Short(numero), fLicI, fLicF,
							"", "", null, "", new Timestamp(System
									.currentTimeMillis()), usuario);*/
					
					//prac-jcallo
					T1274DAO t1274dao = new T1274DAO(dbpool);
					//cod_pers, licencia, periodo, anno, numero, ffinicio, ffin, nvl(c_certific,'') as c_certific, nvl(cod_cmp,'') as cod_cmp, fecha, nvl(enfermedad,'') as enfermedad, fcreacion, cuser_crea
					Map dtoLicMe = new HashMap(); 
					dtoLicMe.put("cod_pers", codPers);
					dtoLicMe.put("licencia", tipo);
					dtoLicMe.put("periodo", periodoActual);
					dtoLicMe.put("anno", annoLicencia);
					dtoLicMe.put("numero", new Integer(numero));
					dtoLicMe.put("ffinicio", fLicI);
					dtoLicMe.put("ffin", fLicF);
					dtoLicMe.put("c_certific", "");
					dtoLicMe.put("cod_cmp", "");
					dtoLicMe.put("fecha", null);//duda si hay errores o no sobre este campo
					dtoLicMe.put("enfermedad", "");
					dtoLicMe.put("fcreacion", new FechaBean().getTimestamp());
					dtoLicMe.put("cuser_crea", usuario);
					dtoLicMe.put("cod_cie10", " ");
					dtoLicMe.put("num_archivo",  0+""); 
					
					//
					DataSource dcsp = ServiceLocator.getInstance().getDataSource("jdbc/dcsp");
					T9388DAO solicitudLicMedicaDAO = new T9388DAO(dcsp);
					Map solicitudLicMed = solicitudLicMedicaDAO.findSolicitudLicMedica(codPers, annoLicencia,  numeroRef );  
					if(solicitudLicMed!=null){						
						if(solicitudLicMed.containsKey("cod_cie10")){
							String codDiagnostico = (String)solicitudLicMed.get("cod_cie10");
							dtoLicMe.put("cod_cie10", codDiagnostico);	
						}  
						
						if(solicitudLicMed.containsKey("num_archivo")){
							Integer numArchivo = (Integer)solicitudLicMed.get("num_archivo"); 
							dtoLicMe.put("num_archivo",  numArchivo+"");
						} 
						if(solicitudLicMed.containsKey("cod_cmp")){
							String cmp = (String)solicitudLicMed.get("cod_cmp"); 
							dtoLicMe.put("cod_cmp",  cmp );
						} 
					} 
					//insertando registro
					log.debug("t1274dao.insertarLicenciaMed(dtoLicMe)... dtoLicMe:"+dtoLicMe);
					t1274dao.insertarLicenciaMed(dtoLicMe);
				}
			}			
		}catch (Exception e) {
			log.error(e);
			if(e.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")){
				beanM=((IncompleteConversationalState)e).getBeanMensaje();
				throw new IncompleteConversationalState(beanM);
			}else{
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
			}
		}
		return mensajes;
	}


/* COMENTADO PUES EL METODO APROBAR SOLICITUD DE COMSA YA NO LO UTILIZA - 07/06/2010 */
	/* *
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
/*	public ArrayList registrarVentaVacaciones(String dbpool, String codPers,
			String anno, String diasVenta, String usuario, String annoRef, String areaRef,
			String numeroRef, Timestamp fIni) throws IncompleteConversationalState,
			RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		ArrayList mensajes = new ArrayList();
		try {

		 //Ahora el numero de dias llega de BD viaja por Mapa en diasVenta
			
			//mensajes = validaVentaVacaciones(dbpool, codPers, anno, usuario);
			//mensajes = validaVentaVacaciones(dbpool, codPers, anno, diasVenta,usuario);
			//JRR - 18/09/2008 - SE CAMBIO PARA QUE LLAME A OTRO METODO AL APROBAR
			Map hm = new HashMap();
			hm.put("dbpool", dbpool);
			hm.put("codPers", codPers);
			hm.put("anno", anno);
			hm.put("diasVenta", diasVenta);
			hm.put("usuario", usuario);
			
			mensajes = (ArrayList)validaVentaVacacionesAprobacion(hm);
			
			if(log.isDebugEnabled()) log.debug("MENSAJES: " + mensajes);
				
			if (mensajes.isEmpty()) {

				VacacionFacadeHome facadeHome = (VacacionFacadeHome) ServiceLocator
						.getInstance().getRemoteHome(
								VacacionFacadeHome.JNDI_NAME,
								VacacionFacadeHome.class);
				VacacionFacadeRemote facadeRemote = facadeHome.create();

				String res = facadeRemote.venderVacaciones(dbpool, codPers,
						anno, diasVenta,usuario, annoRef, areaRef, numeroRef/*, fIni*/
/*				);

				if (!res.equals(Constantes.OK)) {
					mensajes.add(res);
				}
			}
			
		} catch (Exception e) {
			log.error(e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return mensajes;
	}
*/


	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList cargarSolicitudesConcluidas(String dbpool, String codPers,
			String tipo, String criterio, String valor, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		ArrayList lista = null;
		try {

			T1277DAO solicitudDAO = new T1277DAO();
			lista = solicitudDAO.findSolicitudesConcluidas(dbpool, codPers,
					tipo, criterio, valor, usuario);
			
		} catch (Exception e) {
			log.error(e);
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
	public ArrayList cargarSolicitudesIniciadas(String dbpool, String codPers,
			String tipo, String criterio, String valor, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		ArrayList lista = null;
		try {

			T1277DAO solicitudDAO = new T1277DAO();
			lista = solicitudDAO.findSolicitudesIniciadas(dbpool, codPers,
					tipo, criterio, valor, usuario);
			
		} catch (Exception e) {
			log.error(e);
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
	public ArrayList cargarConsultaGeneral(HashMap datos, HashMap seguridad)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		ArrayList lista = null;
		try {			
			//ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta
			T1277DAO solicitudDAO = new T1277DAO();
			String tipo = (String) datos.get("tipo");
			Boolean adminSolicitudes = (Boolean) datos.get("adminSolicitudes");
			if (adminSolicitudes!=null && adminSolicitudes.booleanValue() && tipo.equals("23")){
				lista = solicitudDAO.findConsultaGeneralPorRegimen(datos, seguridad);
			}
			else{//otros tipos (-1,21,38,124,125				
				lista = solicitudDAO.findConsultaGeneral(datos, seguridad);
			}
			/*T1277DAO solicitudDAO = new T1277DAO();
			lista = solicitudDAO.findConsultaGeneral(datos, seguridad);*/
			//ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta 
			
		} catch (Exception e) {
			log.error(e);
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
	public ArrayList cargarSolicitudesRecibidas(String dbpool, String codPers,
			String tipo, String criterio, String valor)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		ArrayList lista = null;
		try {

			T1277DAO solicitudDAO = new T1277DAO();
			lista = solicitudDAO.findSolicitudesRecibidas(dbpool, codPers,
					tipo, criterio, valor);
			
		} catch (Exception e) {
			log.error(e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return lista;
	}
	//dtarazona cargarVacSolicitadasRep
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList cargarVacProgramadas(String dbpool, String codPers,
			String anno)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		ArrayList lista = null;
		try {

			//T1277DAO solicitudDAO = new T1277DAO();
			//lista = solicitudDAO.findSolicitudesRecibidas(dbpool, codPers,anno);
			T1282DAO solicitudDAO = new T1282DAO(dbpool);
			lista =(ArrayList) solicitudDAO.findVacProgByEst(dbpool, codPers,anno);
		} catch (Exception e) {
			log.error(e);
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
	public ArrayList cargarVacSolicitadasRep(String dbpool, String codPers,String numero,
			String anno)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		ArrayList lista = null;
		try {

			//T1277DAO solicitudDAO = new T1277DAO();
			//lista = solicitudDAO.findSolicitudesRecibidas(dbpool, codPers,anno);
			T4821DAO solicitudDAO = new T4821DAO(dbpool);
			lista =(ArrayList) solicitudDAO.findSolVacRepByNum(dbpool, codPers,numero,anno);
		} catch (Exception e) {
			log.error(e);
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
	public HashMap cargarSolicitud(String dbpool, String codPers, String anno,
			String numero) throws IncompleteConversationalState,
			RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		HashMap solicitud = new HashMap();
		try {

			T1277DAO sd = new T1277DAO();
			solicitud = sd.findSolicitud(dbpool, codPers, anno, numero);
			
		} catch (Exception e) {
			log.error(e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return solicitud;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public HashMap obtenerAprobador(String dbpool, HashMap solicitud)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		HashMap aprobador = new HashMap();
		HashMap empleado = new HashMap();
		try {

			T1277DAO sd = new T1277DAO();
			T12DAO daoUO = new T12DAO();
			T02DAO perDAO = new T02DAO();
			
			log.debug("Buscando Aprobador con datos "+solicitud);
			aprobador = sd.findAprobador(dbpool, solicitud);
			log.debug("Aprobador : "+aprobador);
			empleado = perDAO.joinWithT12T99ByCodPers(dbpool,(String)aprobador.get("aprobador"),null);
			log.debug("Empleado : "+empleado);
			//como es aprobador inicial o unico la uuoo debe ser la uuoo de la solicitud y no la uuoo a donde pertenece el aprobador que puede ser diferente
			empleado.put("t02cod_uorg",(String)aprobador.get("uo"));  //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp			
			log.debug("Empleado t02cod_uorg (modificado ICR): "+empleado); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp

			//guardamos el aprobador original en caso tenga delegado
			aprobador.put("aprobadorOriginal", (String)aprobador.get("aprobador"));
			
			if (empleado!=null && empleado.size() > 0){
				
				aprobador.put("UOAprobadorOrigen",(String)empleado.get("t02cod_uorg")); 
				
				HashMap datos = new HashMap();
				datos.put("dbpool",dbpool);
				datos.put("codUO",(String)aprobador.get("UOAprobadorOrigen"));
				datos.put("codOpcion",Constantes.DELEGA_SOLICITUDES);
				
				log.debug("Buscando Delegado con datos "+datos);
				HashMap delegado = daoUO.findDelegado(datos);
				log.debug("Delegado : "+delegado);
				if (delegado!=null && !delegado.isEmpty()){
					if (((String)aprobador.get("aprobador")).equals((String) delegado.get("cod_jefe"))){
						aprobador.put("aprobador", (String) delegado.get("t02cod_pers"));	
					}
				}	
								
			}
			else{
				throw new Exception("El registro del aprobador se encuentra inactivo.");
			}
			
			log.debug("Aprobador Final : "+aprobador);
			
		} catch (Exception e) {
			log.error(e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return aprobador;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList cargarSeguimientos(String dbpool, String codPers,
			String anno, String numero) throws IncompleteConversationalState,
			RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		ArrayList lista = null;
		try {

			T1277DAO solicitudDAO = new T1277DAO();
			lista = solicitudDAO
					.findSeguimientos(dbpool, codPers, anno, numero);
			
		} catch (Exception e) {
			log.error(e);
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
	public ArrayList validaVacacionesEfectivas(String dbpool, String codPers,
			String fechaIni, String fechaFin, int dias, String anno)
			throws IncompleteConversationalState, RemoteException {

		ArrayList mensajes = new ArrayList();
		try {

			T1282DAO VacacionDAO = new T1282DAO(dbpool);
			T1273DAO LicenciaDAO = new T1273DAO(dbpool);

			if (dias <= 0) {
				mensajes
						.add("Imposible registrar la solicitud para una vacaci&oacute;n con"
								+ " una cantidad de d&iacute;as menor o igual a cero.");
			}
			//PRAC-JCALLO
			Map prm = new HashMap();
			prm.put("cod_pers", codPers);
			prm.put("fechaIni", fechaIni);
			prm.put("fechaFin", fechaFin);
			//boolean cruce = VacacionDAO.findByCodPersFIniFFin(dbpool, codPers, fechaIni, fechaFin);
			if (log.isDebugEnabled())  log.debug("VacacionDAO.findByCodPersFIniFFin(prm)... prm:"+prm);
			boolean cruce = VacacionDAO.findByCodPersFIniFFin(prm);
			if (cruce) {
				mensajes.add("El trabajador se encuentra de vacaciones durante esas fechas.");
			}
			//EBV 28/11/2008
			Map prm1 = new HashMap();
			prm1.put("cod_pers", codPers);
			prm1.put("licencia", Constantes.VACACION_PROGRAMADA);
			prm1.put("anno_vac", anno);
			prm1.put("est_id", Constantes.ACTIVO);
			List existeprog = VacacionDAO.findByCodPersAnnoVacEstIdLicencia(prm1);
			
			if (log.isDebugEnabled()) log.debug("existeprog "+existeprog.size());
			if (existeprog.size() >0)
			{
				prm1.put("dias", String.valueOf(dias));
				prm1.put("ffinicio", fechaIni);
				if (log.isDebugEnabled()) log.debug("existeprog1 "+prm1);
				List existeprog1 = VacacionDAO.findByCodPersAnnoVacEstIdLicencia(prm1);
				if (log.isDebugEnabled()) log.debug("existeprog2 "+existeprog1.size());
				if (existeprog1.size() ==0){
					mensajes.add("La Solicitud de Vacaciones No Coincide con la Programacion Realizada. Favor verifique.");	
				}
			} else
			{
				if (log.isDebugEnabled())  log.debug("VacacionDAO.findByCodPersCruceProg(prm)... prm:"+prm);
				boolean cruce1 = VacacionDAO.findByCodPersCruceProg(prm);
				if (cruce1) {
					mensajes.add("La Solicitud que intenta ingresar presenta cruce fechas con una programaciÃ³n de vacaciones ya existente. Por favor verifique.");
				}	
			}
				
			//EBV 28/11/2008
			//prac-jcallo
			Map prms = new HashMap();
			prms.put("cod_pers", codPers);
			prms.put("fecha1", fechaIni);
			prms.put("fecha2", fechaFin);
			prms.put("numero", "");
			if (log.isDebugEnabled()) log.debug("LicenciaDAO.findByCodPersFIniFFin(prms)... prms:"+prms);
			boolean tieneLicencia = LicenciaDAO.findByCodPersFIniFFin(prms); //(dbpool,codPers, fechaIni, fechaFin, "");
			
			if (tieneLicencia) {
				mensajes.add("El trabajador posee una licencia durante esas fechas.");
			}
			
			T1281DAO vacDAO = new T1281DAO();
			//int saldo = vacDAO.findPrimerSaldoByCodPers(dbpool, codPers);
			boolean poseeSaldo = vacDAO.findSaldoVacacional(dbpool, codPers, anno, String.valueOf(dias).trim());
			if (log.isDebugEnabled()) log.debug("Posee Saldo : "+poseeSaldo);

			//if (saldo < dias) {
			if (!poseeSaldo){
				//String mensaje = "El trabajador no posee saldo suficiente ("+dias+" d&iacute;as) para registrar la solicitud de vacaci&oacute;n."
				String mensaje = "El trabajador no posee saldo suficiente para registrar la solicitud de vacaci&oacute;n de "+dias+" d&iacute;as."				
					.concat("<br>Se debe gestionar mediante una Vacaci&oacute;n Especial.");
				mensajes.add(mensaje);
			}
			/********************************/
			else {	
//				log.debug("Dpool: "+dbpool);
//				log.debug("CodPers: "+codPers);
//				log.debug("Dias: " +dias);
//				log.debug("AÃƒÂ±o: "+anno);
//				log.debug("Constante Vacacion: "+Constantes.VACACION);
			
				mensajes = this.verificarSolicitudesPendientesVacaciones(mensajes, dbpool, codPers, dias, anno);		
			}	
			/********************************/
			

		} catch (Exception e) {
			log.error(e);
			mensajes.add(e.toString());
		}
		return mensajes;
	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList validaVacacionesEfectivasAdelanto(String dbpool, String codPers,
			String fechaIni, String fechaFin, int dias, String anno,String esAdelanto)
			throws IncompleteConversationalState, RemoteException {

		ArrayList mensajes = new ArrayList();
		try {

			T1282DAO VacacionDAO = new T1282DAO(dbpool);
			T1273DAO LicenciaDAO = new T1273DAO(dbpool);

			if (dias <= 0) {
				mensajes
						.add("Imposible registrar la solicitud para una vacaci&oacute;n con"
								+ " una cantidad de d&iacute;as menor o igual a cero.");
			}
			//PRAC-JCALLO
			Map prm = new HashMap();
			prm.put("cod_pers", codPers);
			prm.put("fechaIni", fechaIni);
			prm.put("fechaFin", fechaFin);
			//boolean cruce = VacacionDAO.findByCodPersFIniFFin(dbpool, codPers, fechaIni, fechaFin);
			if (log.isDebugEnabled())  log.debug("VacacionDAO.findByCodPersFIniFFin(prm)... prm:"+prm);
			boolean cruce = VacacionDAO.findByCodPersFIniFFin(prm);
			if (cruce) {
				mensajes.add("El trabajador se encuentra de vacaciones durante esas fechas.");
			}
			//EBV 28/11/2008
			Map prm1 = new HashMap();
			prm1.put("cod_pers", codPers);
			prm1.put("licencia", Constantes.VACACION_PROGRAMADA);
			prm1.put("anno_vac", anno);
			prm1.put("est_id", Constantes.ACTIVO);
			List existeprog = VacacionDAO.findByCodPersAnnoVacEstIdLicencia(prm1);
			
			if (log.isDebugEnabled()) log.debug("existeprog "+existeprog.size());
			if (existeprog.size() >0)
			{
				prm1.put("dias", String.valueOf(dias));
				prm1.put("ffinicio", fechaIni);
				if (log.isDebugEnabled()) log.debug("existeprog1 "+prm1);
				List existeprog1 = VacacionDAO.findByCodPersAnnoVacEstIdLicencia(prm1);
				if (log.isDebugEnabled()) log.debug("existeprog2 "+existeprog1.size());
				if (existeprog1.size() ==0){
					mensajes.add("La Solicitud de Vacaciones No Coincide con la Programacion Realizada. Favor verifique.");	
				}
			} else
			{
				if (log.isDebugEnabled())  log.debug("VacacionDAO.findByCodPersCruceProg(prm)... prm:"+prm);
				boolean cruce1 = VacacionDAO.findByCodPersCruceProg(prm);
				if (cruce1) {
					mensajes.add("La Solicitud que intenta ingresar presenta cruce fechas con una programaciÃ³n de vacaciones ya existente. Por favor verifique.");
				}	
			}
				
			//EBV 28/11/2008
			//prac-jcallo
			Map prms = new HashMap();
			prms.put("cod_pers", codPers);
			prms.put("fecha1", fechaIni);
			prms.put("fecha2", fechaFin);
			prms.put("numero", "");
			if (log.isDebugEnabled()) log.debug("LicenciaDAO.findByCodPersFIniFFin(prms)... prms:"+prms);
			boolean tieneLicencia = LicenciaDAO.findByCodPersFIniFFin(prms); //(dbpool,codPers, fechaIni, fechaFin, "");
			
			if (tieneLicencia) {
				mensajes.add("El trabajador posee una licencia durante esas fechas.");
			}
			
			T1281DAO vacDAO = new T1281DAO();
			T99DAO codigoDAO = new T99DAO();
			if(esAdelanto.equals("0")){
				boolean poseeSaldo = vacDAO.findSaldoVacacional(dbpool, codPers, anno, String.valueOf(dias).trim());
				if (!poseeSaldo){
					String mensaje = "El trabajador no posee saldo suficiente para registrar la solicitud de vacaci&oacute;n de "+dias+" d&iacute;as."				
						.concat("<br>Se debe gestionar mediante una Vacaci&oacute;n Especial.");
					mensajes.add(mensaje);
				}
				else {	
					mensajes = this.verificarSolicitudesPendientesVacaciones(mensajes, dbpool, codPers, dias, anno);		
				}
			}else{
				//Validar que no exceda la cantidad de vacaciones
				String diasVaca = codigoDAO.findParamByCodTabCodigo(dbpool,
						Constantes.CODTAB_PARAMETROS_ASISTENCIA,
						Constantes.DIAS_VACACIONES);
				int numDiasVaca = diasVaca != null ? Integer.parseInt(diasVaca) : 0;
				
				pe.gob.sunat.rrhh.asistencia.dao.T1281DAO cabvacDAO = new pe.gob.sunat.rrhh.asistencia.dao.T1281DAO(dbpool);
				
				Map auxTrab = new HashMap();
				auxTrab.put("cod_pers", codPers);
				auxTrab.put("anno", anno);
				
				Map cabVacacion = cabvacDAO.findAllColumnsByKey(auxTrab); 
				if (cabVacacion!=null) {
					if(-1*(Integer.parseInt(cabVacacion.get("saldo")+"") + ((-1)*dias))>numDiasVaca){
						String mensaje = "Ha excedido la cantidad de días permitidos para registrar la vacación.";				
						mensajes.add(mensaje);
					}
				}
				log.debug("dias:"+dias+"-"+numDiasVaca);
			}
		} catch (Exception e) {
			log.error(e);
			mensajes.add(e.toString());
		}
		return mensajes;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList validaReprogramacion(HashMap datos)
			throws IncompleteConversationalState, RemoteException {

		ArrayList mensajes = new ArrayList();
		try {
			//prac-jcallo
			String dbpool = (String)datos.get("dbpool");
			
			T1282DAO VacacionDAO = new T1282DAO(dbpool);
			T1273DAO LicenciaDAO = new T1273DAO(dbpool);
			T99DAO codigoDAO = new T99DAO();
			T1279DAO movDAO = new T1279DAO();

			
			String codPers = (String)datos.get("userOrig");
			String nvaFechaIni = (String)datos.get("nvaFechaIni");
			String fechaIni = (String)datos.get("fechaIni");
			String fechaFin = (String)datos.get("fechaFin");
			String numDias = (String)datos.get("numDias");
			
			String nvaFechaFin = Utiles.dameFechaSiguiente(nvaFechaIni, Integer.parseInt(numDias)-1);
			datos.put("nvaFechaFin", nvaFechaFin);
			
			if (nvaFechaIni==null || nvaFechaIni.equals("")) {
				mensajes.add("Nueva fecha inicio no es v&aacute;lida.");
			}
			//PRAC-JCALLO
			Map prm = new HashMap();
			prm.put("cod_pers", codPers);
			prm.put("fecha1", nvaFechaIni);
			prm.put("fecha2", nvaFechaFin);
			prm.put("prog", "si");
			prm.put("fechaIni", fechaIni);			
			//boolean cruce = VacacionDAO.findByCodPersFIniFFin(dbpool, codPers, nvaFechaIni, nvaFechaFin, true, fechaIni);
			log.debug("VacacionDAO.findByCodPersFIniFFin(prm)... prm:"+prm);
			boolean cruce = VacacionDAO.findByCodPersFIniFFinProg(prm);
						
			if (cruce) {
				mensajes.add("La vacaci&oacute;n presenta cruces con alguna vacaci&oacute;n programada o efectiva.");
			}
			//prac-jcallo
			Map prms = new HashMap();
			prms.put("cod_pers", codPers);
			prms.put("fecha1", nvaFechaIni);
			prms.put("fecha2", nvaFechaFin);
			prms.put("numero", "");
			log.debug("LicenciaDAO.findByCodPersFIniFFin(prms)... prms:"+prms);
			boolean tieneLicencia = LicenciaDAO.findByCodPersFIniFFin(prms); //(dbpool,codPers, nvaFechaIni, nvaFechaFin, "");

			if (tieneLicencia) {
				mensajes.add("La vacaci&oacute;n presenta cruces con alguna licencia programada.");
			}

			T1281DAO vacDAO = new T1281DAO();
			int saldo = vacDAO.findByCodPers(dbpool, codPers);

			String saldoReprog = codigoDAO.findParamByCodTabCodigo(dbpool,
					Constantes.CODTAB_PARAMETROS_ASISTENCIA,
					Constantes.SALDO_MINIMO_REPROGRAMACION);
			int numSaldoReprog = saldoReprog != null ? Integer.parseInt(saldoReprog) : 0;
			
			if (saldo > numSaldoReprog) {
				mensajes.add("Su saldo acumulado excede el m&iacute;nimo aceptado para la reprogramaci&oacute;n <b>("+numSaldoReprog+" d&iacute;as)</b>. ");
			}
			
			int numDiasAntes = movDAO.findNumDiasByTipo(dbpool, Constantes.REPROGRAMACION_VACACION, "1");			
			int numDiasDespues = movDAO.findNumDiasByTipo(dbpool, Constantes.REPROGRAMACION_VACACION, "2");

			AsistenciaFacadeHome facadeHome = (AsistenciaFacadeHome) ServiceLocator
					.getInstance().getRemoteHome(
							AsistenciaFacadeHome.JNDI_NAME,
							AsistenciaFacadeHome.class);
			AsistenciaFacadeRemote facadeRemote = facadeHome.create();
		
			int valida = facadeRemote.validaDiasAntesDespues(dbpool, Constantes.REPROGRAMACION_VACACION,
					Utiles.obtenerFechaActual(), fechaIni, fechaFin);			
			
			if (valida < 0) {
				mensajes.add("La cantidad m&iacute;nima de d&iacute;as de anticipaci&oacute;n " +
						"para registrar la solicitud es "+ numDiasAntes + ".");
			}
			if (valida > 0) {
				mensajes.add("La cantidad m&iacute;nima de d&iacute;as posteriores al fin de licencia " +
						"para el registro de la solicitud es de "+ numDiasDespues + ".");
			}			

		} catch (Exception e) {
			log.error(e);
			mensajes.add(e.toString());
		}
		return mensajes;
	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList validaPostergacion(HashMap datos)
			throws IncompleteConversationalState, RemoteException {
		if(log.isDebugEnabled()) log.debug("Entrando a validarPostergación():"+datos.toString());
		ArrayList mensajes = new ArrayList();
		try {
			//prac-jcallo
			String dbpool = (String)datos.get("dbpool");
			
			T1281DAO vacDAO = new T1281DAO();
			T1282DAO VacacionDAO = new T1282DAO(dbpool);
			T1273DAO LicenciaDAO = new T1273DAO(dbpool);
			T1279DAO movDAO = new T1279DAO();

			VacacionFacadeHome vacHome = (VacacionFacadeHome) sl.
					getRemoteHome( VacacionFacadeHome.JNDI_NAME,
								VacacionFacadeHome.class);
			VacacionFacadeRemote vacRemote = vacHome.create();
			
			AsistenciaFacadeHome asisHome = (AsistenciaFacadeHome) sl.
					getRemoteHome( AsistenciaFacadeHome.JNDI_NAME,
								AsistenciaFacadeHome.class);
			AsistenciaFacadeRemote asisRemote = asisHome.create();
			
			String codPers = datos.get("aprobar")!=null?(String)datos.get("codPers"):(String)datos.get("userOrig");
			//String nvaFechaIni = (String)datos.get("nvaFechaIni");
			//String fechaIni = (String)datos.get("fechaIni");
			//String fechaFin = (String)datos.get("fechaFin");
			String numDias = (String)datos.get("numDias");
			String annoVac = (String)datos.get("annoVac");
			HashMap hmPermitidos = Utiles.diasVacPermitidos();
			
			log.debug("datos : "+datos);
			
			log.debug("numDias: "+numDias);
			
			//DTARAZONA 3ER ENTREGABLE
			String reprogramadas=(String)datos.get("reprogramadas");
			if(log.isDebugEnabled()) log.debug("Reprogramaciones:"+reprogramadas);
			String programadas=(String)datos.get("programadasSel");
			if(log.isDebugEnabled()) log.debug("Programaciones:"+programadas);
			
			String[] prog1=reprogramadas.split("&");
			
			for(int i=0;i<prog1.length;i++){
				String[] p1=prog1[i].split("-");
				
				String nvaFechaIni = p1[2];
				log.debug("nuevaFechaIni : "+nvaFechaIni);
				String nvaFechaFin = Utiles.dameFechaSiguiente(p1[2], Integer.parseInt(p1[1])-1);
				log.debug("nuevaFechaFin : "+nvaFechaFin);
				
				int numDiasAntiguos = Utiles.obtenerDiasDiferencia(nvaFechaIni,nvaFechaFin)+1;
				log.debug("numDiasAntiguos : "+numDiasAntiguos);
				//FIN DTARAZONA
				numDias=p1[1];
				annoVac=p1[0];
				//int numDiasAntiguos = Utiles.obtenerDiasDiferencia(fechaIni,fechaFin)+1;
				//log.debug("numDiasAntiguos : "+numDiasAntiguos);
				
				//String nvaFechaFin = Utiles.dameFechaSiguiente(nvaFechaIni, Integer.parseInt(numDias)-1);
				//datos.put("nvaFechaFin", nvaFechaFin);
				
				//log.debug("nvaFechaFin : "+nvaFechaFin);
				
				if (nvaFechaIni==null || nvaFechaIni.equals("")) {
					mensajes.add("Nueva fecha inicio no es v&aacute;lida.");
				}

				/*int difAntes = Utiles.obtenerDiasDiferencia(nvaFechaIni,Utiles.obtenerFechaActual());
				log.debug("DifAntes 1 : "+difAntes);
				if (difAntes >= 0) {
					mensajes.add("La fecha actual es posterior a la fecha de inicio de la vacaci&oacute;n ("+nvaFechaIni+").");
				}*/
				
				//difAntes = Utiles.obtenerDiasDiferencia(fechaIni,nvaFechaIni);
				//log.debug("DifAntes 2 : "+difAntes);
				//if (difAntes <= 0) {
					//mensajes.add("La nueva fecha de inicio de la vacaci&oacute;n debe ser posterior al "+echaIni+".");
				//}
				//PRAC-JCALLO
				Map prm = new HashMap();
				prm.put("cod_pers", codPers);
				prm.put("fecha1", nvaFechaIni);
				prm.put("fecha2", nvaFechaFin);
				prm.put("diasVac", numDias);
				prm.put("prog", "si");//
				prm.put("annoVac", annoVac);
				prm.put("aprobar", datos.get("aprobar")!=null?(String)datos.get("aprobar"):"0");
				
				//prm.put("fechaIni", nvaFechaIni);
				log.debug("VacacionDAO.findByCodPersFIniFFinAdelanto(prm)... prm:"+prm);
				//boolean cruce = VacacionDAO.findByCodPersFIniFFin(dbpool, codPers,nvaFechaIni, nvaFechaFin, false, fechaIni);
				boolean cruce = VacacionDAO.findByCodPersFIniFFinProgAdelanto(prm);
				log.debug("Resultado cruce:"+cruce);
				if (cruce) {
					mensajes.add("La vacación presenta cruces con alguna vacación programada, programada en solicitud o efectiva.");
				}
				
				//prac-jcallo
				Map prms = new HashMap();
				prms.put("cod_pers", codPers);
				prms.put("fecha1", nvaFechaIni);
				prms.put("fecha2", nvaFechaFin);
				prms.put("numero", "");
				log.debug("LicenciaDAO.findByCodPersFIniFFin(prms)... prms:"+prms);
				boolean tieneLicencia = LicenciaDAO.findByCodPersFIniFFin(prms); //(dbpool,codPers, nvaFechaIni, nvaFechaFin, "");

				if (tieneLicencia) {
					mensajes.add("La vacaci&oacute;n presenta cruces con alguna licencia programada.");
				}

				/*int numDiasAntes = movDAO.findNumDiasByTipo(dbpool, Constantes.VACACION_POSTERGADA, "1");			
				difAntes = asisRemote.obtenerDiasHabilesDiferencia(dbpool, Utiles.obtenerFechaActual(),nvaFechaIni);
				log.debug("DifAntes 3 : "+difAntes);
				
				if (numDiasAntes > 0 && difAntes > 0 && difAntes < numDiasAntes) {
					mensajes.add("La cantidad m&iacute;nima de d&iacute;as de anticipaci&oacute;n " +
							"para registrar la solicitud es "+ numDiasAntes + ".");
				}
				if (!vacRemote.esCantidadAceptada(dbpool, codPers, annoVac, hmPermitidos, Integer.parseInt(p1[1]), nvaFechaIni)){
					mensajes.add("La cantidad de dias seleccionada no es valida.");				
				}*/
			}
			
		} catch (Exception e) {
			log.error(e);
			mensajes.add(e.toString());
		}
		return mensajes;
	}	

	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public ArrayList registrarVacacionEfectiva(String dbpool, String codPers,
			Timestamp fechaIni, Timestamp fechaFin, String dias,
			String observacion, String usuario, String annoRef, String areaRef,
			String numeroRef, String anno) throws IncompleteConversationalState,
			RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		ArrayList mensajes = new ArrayList();
		try {

			VacacionFacadeHome facadeHome = (VacacionFacadeHome) ServiceLocator
					.getInstance().getRemoteHome(VacacionFacadeHome.JNDI_NAME,
							VacacionFacadeHome.class);
			VacacionFacadeRemote facadeRemote = facadeHome.create();

			HashMap hmPermit = Utiles.diasVacPermitidos();

			if (anno!=null || anno.length()>0){
				facadeRemote.registrarNvaVacacion(dbpool, codPers,
						Constantes.VACACION, fechaIni, new Integer(dias), anno, fechaFin,
						observacion, Constantes.ACTIVO, usuario, annoRef, areaRef,
						numeroRef, hmPermit, null, false);
			} else {
				facadeRemote.registrarNvaVacacion(dbpool, codPers,
					Constantes.VACACION, fechaIni, new Integer(dias), "NADA", fechaFin,
					observacion, Constantes.ACTIVO, usuario, annoRef, areaRef,
					numeroRef, hmPermit, null, false);
			}

		} catch (Exception e) {
			log.error(e);
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
	public ArrayList suspenderVacacion(String dbpool, String numero,
			String codPers, String periodo, String licencia ,Timestamp fechaIni, String annoRef,
			String areaRef, String numeroRef, String usuario)
			throws IncompleteConversationalState, RemoteException {

		ArrayList mensajes = new ArrayList();
		BeanMensaje beanM = new BeanMensaje();
		try {
			VacacionFacadeHome facadeHome = (VacacionFacadeHome) ServiceLocator
					.getInstance().getRemoteHome(VacacionFacadeHome.JNDI_NAME,
							VacacionFacadeHome.class);
			VacacionFacadeRemote facadeRemote = facadeHome.create();

			facadeRemote.suspenderVacacion(dbpool, numero, codPers, periodo, licencia , 
					fechaIni, annoRef, areaRef, numeroRef, usuario);

		} catch (Exception e) {
			log.error(e);
			mensajes.add(e.toString());
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
	public List convertirAVacacionEfectiva(String dbpool, String numero,
			String codPers, String periodo, Timestamp fechaIni, String annoRef,
			String areaRef, String numeroRef, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		List mensajes = new ArrayList();
		try {

			VacacionFacadeHome facadeHome = (VacacionFacadeHome) ServiceLocator
					.getInstance().getRemoteHome(VacacionFacadeHome.JNDI_NAME,
							VacacionFacadeHome.class);
			VacacionFacadeRemote facadeRemote = facadeHome.create();

			mensajes = facadeRemote.convertirEfectiva(dbpool, numero, codPers,
					periodo, fechaIni, annoRef, areaRef, numeroRef, usuario,
					null);

		} catch (Exception e) {			
			log.error(e);
			mensajes.add(e.toString());
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
	public List convertirAVacacionEfectivaAdelanto(String dbpool, String numero,
			String codPers, String periodo, Timestamp fechaIni, String annoRef,
			String areaRef, String numeroRef,String esAdelanto, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		List mensajes = new ArrayList();
		try {

			VacacionFacadeHome facadeHome = (VacacionFacadeHome) ServiceLocator
					.getInstance().getRemoteHome(VacacionFacadeHome.JNDI_NAME,
							VacacionFacadeHome.class);
			VacacionFacadeRemote facadeRemote = facadeHome.create();
			
			mensajes = facadeRemote.convertirEfectivaAdelanto(dbpool, numero, codPers,
					periodo, fechaIni, annoRef, areaRef, numeroRef, usuario,esAdelanto,
					null);

		} catch (Exception e) {			
			log.error(e);
			mensajes.add(e.toString());
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
	public List convertirAVacacionEfectivaMatrimonio(String dbpool, HashMap solicitud, String usuario)
			throws IncompleteConversationalState, RemoteException {
		BeanMensaje beanM = new BeanMensaje();
		List mensajes = new ArrayList();
		try {

			VacacionFacadeHome facadeHome = (VacacionFacadeHome) ServiceLocator
					.getInstance().getRemoteHome(VacacionFacadeHome.JNDI_NAME,
							VacacionFacadeHome.class);
			VacacionFacadeRemote facadeRemote = facadeHome.create();
			mensajes = facadeRemote.convertirEfectivaMatrimonio(dbpool, solicitud, usuario);

		} catch (Exception e) {			
			log.error(e);
			mensajes.add(e.toString());
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
	public ArrayList reprogramarVacacion(HashMap mapa)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		ArrayList mensajes = new ArrayList();
		try {

			VacacionFacadeHome facadeHome = (VacacionFacadeHome) ServiceLocator
					.getInstance().getRemoteHome(VacacionFacadeHome.JNDI_NAME,
							VacacionFacadeHome.class);
			VacacionFacadeRemote facadeRemote = facadeHome.create();

			mensajes = facadeRemote.reprogramarVacacion(mapa); 

		} catch (Exception e) {
			log.error(e);
			mensajes.add(e.toString());
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
	public ArrayList postergarVacacion(HashMap mapa)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		ArrayList mensajes = new ArrayList();
		try {

			VacacionFacadeHome facadeHome = (VacacionFacadeHome) ServiceLocator
					.getInstance().getRemoteHome(VacacionFacadeHome.JNDI_NAME,
							VacacionFacadeHome.class);
			VacacionFacadeRemote facadeRemote = facadeHome.create();

			mensajes = facadeRemote.postergarVacacion(mapa); 

		} catch (Exception e) {
			log.error(e);
			mensajes.add(e.toString());
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
	public ArrayList actualizaSolicitudReprogramacion(String dbpool, HashMap datos, String tipo, String estado)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		ArrayList mensajes = new ArrayList();
		try {

			String codPers = (String)datos.get("userOrig");
			String txtObs = (String)datos.get("txtObs");
			
			StringTokenizer st = new StringTokenizer(txtObs,"&");

			String periodo = st.nextToken();
			String fechaVac = st.nextToken();
			
			//prac-jcallo
			/*T1282CMPHome detalleHome = (T1282CMPHome) ServiceLocator.getInstance().getLocalHome(T1282CMPHome.JNDI_NAME);			
			T1282CMPLocal detalleLocal = detalleHome.findByPrimaryKey(
					new T1282CMPPK(
					codPers,
					periodo, Utiles.stringToTimestamp(fechaVac+" 00:00:00")));*/
			//PRAC-JCALLO
			T1282DAO t1282dao = new T1282DAO(dbpool);
			Map prms = new HashMap();
			//los campos a actualizar
			Map columns = new HashMap();
			columns.put("licencia", tipo);
			columns.put("est_id", estado);
			
			prms.put("columns", columns);
			//los campos de la llave primaria
			prms.put("cod_pers", codPers);
			prms.put("periodo", periodo);
			prms.put("ffinicio", new FechaBean(fechaVac).getTimestamp());

			//COMENTADO debido Ã¡ cambio de PK tabla t1282
			//log.debug("t1282dao.updateCustomColumns(prms)... prms:"+prms);
			//t1282dao.updateCustomColumns(prms);
			
			/*detalleLocal.setLicencia(tipo);
			detalleLocal.setEstId(estado);*/
			

		} catch (Exception e) {
			mensajes.add(e.toString());
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		return mensajes;
	}	
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList buscarSupervisores(String dbpool, String codPers, String mov, String uorg)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		ArrayList lista = null;
		try {

			T1933DAO solicitudDAO = new T1933DAO();
			log.debug("Buscar Supervisores codPers : "+ codPers);
			log.debug("movimiento :" + mov);
			log.debug("uorg :  " + uorg);
			lista = solicitudDAO.findSupervisores(dbpool, codPers, mov, uorg);
			
		} catch (Exception e) {
			log.error(e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return lista;
	}	
	

	/***********************/
	
	/**
	 * Metodo que se encarga de verificar las Solicitudes Pendientes de Vacaciones que tenga un trabajador
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList verificarSolicitudesPendientesVacaciones(ArrayList mensajes,
			String dbpool, String codPers, int dias, String anno) throws IncompleteConversationalState,
			RemoteException {

//		log.debug("----- verificarSolicitudesPendientesVacaciones -----");
		
		try {
			pe.gob.sunat.rrhh.asistencia.dao.T1277DAO solicitudDAO = new pe.gob.sunat.rrhh.asistencia.dao.T1277DAO(dbpool);
			T1281DAO vacDAO = new T1281DAO();
			
			//int saldo = vacDAO.findPrimerSaldoByCodPers(dbpool, codPers);
			int saldo = vacDAO.findSaldoByAnho(dbpool, codPers, anno);
			
			if (saldo >= dias) {
				Map params = new HashMap();
				params.put("dbpool", dbpool);
				params.put("cod_pers", codPers);
				params.put("anno", anno);			
				//log.debug("params: " + params);
				
				int totalPendiente = solicitudDAO.getTotalDiasPendVac(params);
				int disponible;
				//log.debug("totalPendiente: " + totalPendiente);				
				
				disponible = saldo - totalPendiente;
				//log.debug("disponible: " + disponible);				

				if (dias > disponible) {
					//log.debug("El trabajador Posee solicitud(es) pendiente(s)");
					String mensaje = "El trabajador posee un "
						    + "saldo vacacional ("+saldo+" dias) "						
							+ "de los cuales "
						    + totalPendiente
							+ " d&iacute;as se encuentran comprometidos en solicitud(es) pendiente(s), "
							+ "por lo que resulta "
							+ "insuficiente para poder registrar una solicitud adicional de "
							+ dias
							+ " d&iacute;as."
							.concat("<br>Se deber&aacute; gestionar mediante una Vacaci&oacute;n Especial.");
					mensajes.add(mensaje);
				}
			} 
			//Ya se valida antes de la llamada a verificarSolicitudesPendientesVacaciones, pero por seguridad a futuro
			else {
				//log.debug("El trabajador No Posee solicitud(es) pendiente(s)");
				String mensaje =
					    "El trabajador no posee saldo suficiente para registrar la solicitud de vacaci&oacute;n de "+dias+" d&iacute;as."
						.concat("<br>Se debe gestionar mediante una Vacaci&oacute;n Especial.");
				mensajes.add(mensaje);
			}			
		} catch (Exception e) {
			log.error(e);
			mensajes.add(e.toString());
		}

		return mensajes;
	}	
	
	
	/* FUSION PROGRAMACION */
	
	/**
	   * Mtodo encargado de obtener la programacion
	   * @param params Map
	   * @return List 
	   * @throws FacadeException
	   * @ejb.interface-method view-type="remote"
	   * @ejb.transaction type="NotSupported"
	   */
	  public Map traerVacFisyProg(Map dBean) throws FacadeException {
	   	Map mapaResult = null;
	  	MensajeBean beanM = new MensajeBean();
	   	List listaProgramacion=null;
	   	List listaVacFisAprob=null;
	   	Map mapaProg = new HashMap();
	    try {
	    	DataSource dbpool_sp = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dcsp");
	    	pe.gob.sunat.rrhh.dao.T02DAO t02DAO = new pe.gob.sunat.rrhh.dao.T02DAO(dbpool_sp);
	    	Map solicitante = t02DAO.joinWithT12T01Deta(dBean.get("txtregistro").toString());
	    	if (solicitante!=null){
	    		mapaResult = new HashMap();
	    		String fecha_ingreso=solicitante.get("t02f_ingsun_desc").toString();
	    		FechaBean fechabeanIngreso = new FechaBean(fecha_ingreso);
	    		FechaBean fechabeanHoy = new FechaBean();
	    		String fecha_gen= fechabeanIngreso.getDia() + "/" + fechabeanIngreso.getMes() + "/" + fechabeanHoy.getAnho();
	    		FechaBean fechabeanGen = new FechaBean(fecha_gen);
	    		int periodo_Act=0;
	      	if (fechabeanHoy.getCalendar().before(fechabeanGen.getCalendar())){
	    			periodo_Act = Integer.parseInt(fechabeanHoy.getAnho())-1;
	    		} else {
	    			periodo_Act = Integer.parseInt(fechabeanHoy.getAnho());
	    		}
	      	String fecha_ini_vac= fechabeanIngreso.getDia().concat("/").concat(fechabeanIngreso.getMes()).concat("/").concat(String.valueOf(periodo_Act));
	      	String fecha_gen_sig = fechabeanIngreso.getDia().concat("/").concat(fechabeanIngreso.getMes()).concat("/").concat(String.valueOf(periodo_Act+1));
	      	mapaResult.put("fecha_gen_sig",fecha_gen_sig);
	      	mapaResult.put("fecha_inicio_vac",fecha_ini_vac);
	      	pe.gob.sunat.rrhh.asistencia.dao.T1281DAO t1281dao = new pe.gob.sunat.rrhh.asistencia.dao.T1281DAO(dbpool_sp);
	      	T1282DAO t1282dao = new T1282DAO(dbpool_sp);
	      	mapaProg.put("cod_pers",dBean.get("txtregistro").toString());
	      	mapaProg.put("anno_vac",dBean.get("cod_periodo").toString());
	      	mapaProg.put("licencia",constantes.leePropiedad("VACACION_PROGRAMADA"));
	      	listaProgramacion = t1282dao.obtenerProgramadas(mapaProg);
	      	dBean.put("est_id",constantes.leePropiedad("ACTIVO"));
	      	dBean.put("licencia",constantes.leePropiedad("VACACION"));
	      	listaVacFisAprob = t1282dao.traerVacFis(dBean);
	      	String saldo = t1281dao.traerSaldo(dBean);
	      	mapaResult.put("saldo",saldo);
	      	mapaResult.put("registro",solicitante.get("t02cod_pers").toString());
	      	mapaResult.put("uorga",solicitante.get("t02cod_uorg").toString());
	      	mapaResult.put("listaProgramacion",listaProgramacion);
	      	mapaResult.put("listaVacFisAprob",listaVacFisAprob);
	    		
	    	} 
	      
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
	    return mapaResult;
	  }
	  
	  /**
	   * Mtodo encargado de obtener la programacion
	   * @param params Map
	   * @return List 
	   * @throws FacadeException
	   * @ejb.interface-method view-type="remote"
	   * @ejb.transaction type="NotSupported"
	   */
	  public Map agregarReProgramacion(Map params) throws FacadeException {
	   	Map mapaResult = null;
	   	MensajeBean beanM = new MensajeBean();
	  	try {
	  		String[] arrEliminadas = null;
	  		String[] arrEliminadasFis = null;
	  		List listaEliminadas = new ArrayList();
	  		List listaEliminadasFis = new ArrayList();
	  		Map mapaCheck=new HashMap();
	      if(params.get("chkEliminarVacFis")!=null){
	        if (params.get("chkEliminarVacFis").getClass() != String.class){
	        	arrEliminadasFis = (String[])params.get("chkEliminarVacFis");
	        	for (int j=0;j<arrEliminadasFis.length;j++){
	        		listaEliminadasFis.add(arrEliminadasFis[j]);
	        	}
	        } else{
	        	listaEliminadasFis.add((String)params.get("chkEliminarVacFis"));
	        } 
	      }
	      if(params.get("chkEliminar")!=null){
	        if (params.get("chkEliminar").getClass() != String.class){
	        	arrEliminadas = (String[])params.get("chkEliminar");
	        	for (int j=0;j<arrEliminadas.length;j++){
	        		listaEliminadas.add(arrEliminadas[j]);
	        	}
	        } else{
	        	listaEliminadas.add((String)params.get("chkEliminar"));
	        } 
	      }
	      mapaCheck.put("chkEliminarReq",listaEliminadas);
	      mapaCheck.put("chkEliminarVacFisReq",listaEliminadasFis);
	  		ProgramacionFacadeHome cpFachome = (ProgramacionFacadeHome) ServiceLocator.
	  		getInstance().getRemoteHome(ProgramacionFacadeHome.JNDI_NAME,ProgramacionFacadeHome.class);
	  		ProgramacionFacadeRemote programacion = cpFachome.create();
	  		mapaResult = programacion.agregarProgramacion(params);
	  		if (mapaResult!=null){
	  			mapaResult.put("mapaCheck",mapaCheck);
	  		}
	  		
	      
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
	    return mapaResult;
	  }
	  
	  /**
	   * Mtodo encargado de grabar las vacaciones programadas y la anulacion de las vacaciones fisicas
	   * @param datos Map
	   * @return boolean 
	   * @throws FacadeException
	   * @ejb.interface-method view-type="remote"
	   * @ejb.transaction type="RequiresNew"
	   */
	  public void grabarAnulVacFis(Map datos) throws FacadeException {
	   	MensajeBean beanM = new MensajeBean();
	   	boolean existenVacFis = false;
	    try {
	    	DataSource dbpool_sp = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dgsp");
	    	pe.gob.sunat.rrhh.asistencia.dao.T1277DAO t1277dao = new pe.gob.sunat.rrhh.asistencia.dao.T1277DAO(dbpool_sp);
	    	
	    	T1282DAO t1282dao = new T1282DAO(dbpool_sp);
	    	
	    	List listaVacFisAprob = (ArrayList)datos.get("listaVacFisAprob");
	    	List listaProgramacion = (ArrayList)datos.get("listaProgramacion");
	    	
	    	if (log.isDebugEnabled()) {
	    		log.debug("***********************listaVacFisAprob");
	    		log.debug(listaVacFisAprob);

	    		log.debug("************************listaProgramacion");
	    		log.debug(listaProgramacion);
	    	}
	    	
	    	int diasAnulados=0;
	    	String[] listaEliminadas = null;
	    	String[] arrobserv = (String[])datos.get("arrobserv");
	    	Map mapaEliminado=null;
	    	Map columns = new HashMap();
	    	
	    	Map prog =  new HashMap();
	    	int iprog= 0;
	    	
	    	int diasAnul=0;
	      if(datos.get("chkEliminarVacFis")!=null){
	      	listaEliminadas = (String[])datos.get("chkEliminarVacFis");
	        for (int i=0;i<listaEliminadas.length;i++) {
	        	mapaEliminado = ((HashMap)listaVacFisAprob.get(Integer.parseInt(listaEliminadas[i])));
		    		diasAnulados=diasAnulados + Integer.parseInt(mapaEliminado.get("dias").toString());
		    		columns.put("est_id",constantes.leePropiedad("PROGRAMACION_ELIMINADA"));
		    		columns.put("fmod",new FechaBean().getTimestamp());
		    		columns.put("cuser_mod",datos.get("cod_usumodif").toString());  
		    		columns.put("observ",arrobserv[i]);
		    		mapaEliminado.put("columns",columns);
		    		mapaEliminado.put("cod_pers",mapaEliminado.get("t02cod_pers").toString());
		    		mapaEliminado.put("licencia",constantes.leePropiedad("VACACION"));
		    		
		    		if (log.isDebugEnabled()) {
		    			log.debug("mapaEliminado================================");
		    			log.debug(mapaEliminado);
		    		}
		    		
		    		t1277dao.updateCustomColumns(mapaEliminado);
		    		
		    		//Para eliminar Vacacioens Efectivas que no Tienen ProgramaciÃ³n	
		    		prog.put("cod_pers", mapaEliminado.get("t02cod_pers").toString());
		    		prog.put("licencia", constantes.leePropiedad("VACACION_PROGRAMADA"));
		    		prog.put("ffinicio", mapaEliminado.get("ffinicio"));
		    		iprog = t1282dao.selectByPrimaryKeySinPeriodo(prog);
		    		if (iprog==0) {
		    			prog.remove("licencia");
		    			prog.put("licencia", constantes.leePropiedad("VACACION"));
		    			t1282dao.deleteByPrimaryKeySinPeriodo(prog);
		    		}
		    		//****
		    		listaVacFisAprob.remove(mapaEliminado);
		    		diasAnul = diasAnul + Integer.parseInt(mapaEliminado.get("dias").toString());
		    		existenVacFis = true;
	        }
	        datos.put("saldoAnul",String.valueOf(diasAnul));	
	      }
	      
	      datos.put("diasAnulados",String.valueOf(diasAnulados));
	    	ProgramacionFacadeHome cpFachome = (ProgramacionFacadeHome) ServiceLocator.
	  		getInstance().getRemoteHome(ProgramacionFacadeHome.JNDI_NAME,ProgramacionFacadeHome.class);
	  		ProgramacionFacadeRemote programacion = cpFachome.create();
	  		programacion.grabarReProgVacaciones(datos);
	      
	    	if (existenVacFis) {
	    		pe.gob.sunat.rrhh.dao.CorreoDAO correoDao = new pe.gob.sunat.rrhh.dao.CorreoDAO(dbpool_sp);
	    		String correosmtp = correoDao.findCorreoByRegistro(datos.get("registro").toString());
	    		
	    		if (correosmtp!=null && !"".equals(correosmtp)){
	    			
	    			try{//ICAPUNAY 24/07/2012 ELIMINAR ERROR: NULL POR PROBLEMAS CON SERVIDOR DE CORREO O CORREO ERRONEO
	    			
				  		String msg = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2//EN\"><html><body><p><strong>Se registr&oacute; su Anulaci&oacute;n de Vacaciones</strong>";
				  	 	msg = msg + "</body></html>";	    	 
				  		Correo correo = new Correo(correosmtp,msg);
				  		correo.enviarHtml();
				  		
				  	//ICAPUNAY 24/07/2012 ELIMINAR ERROR: NULL POR PROBLEMAS CON SERVIDOR DE CORREO O CORREO ERRONEO
	    			} catch (CorreoException ce) {
				 		if (log.isDebugEnabled()) log.debug("Error en CorreoException: "+ce.toString());									 	
					 	if (log.isDebugEnabled()) log.debug("No se pudo enviar correo al trabajador con registro: " + datos.get("registro").toString());									 										 	
	    			}	
	    			//FIN ICAPUNAY 24/07/2012 ELIMINAR ERROR: NULL POR PROBLEMAS CON SERVIDOR DE CORREO O CORREO ERRONEO
	    		}
	    	}
	    	
	    } catch (DAOException e) {
	      log.error(e, e);
	      beanM.setMensajeerror(e.getMessage());
	      beanM.setMensajesol("Por favor intente nuevamente, posible error en la BD");
	      //throw new FacadeException(this, beanM);
	      throw new FacadeException(this, e.toString());
	    } catch (Exception e) {
	      log.error(e, e);
	      beanM.setMensajeerror(e.getMessage());
	      beanM.setMensajesol("Por favor intente nuevamente.");
	      //throw new FacadeException(this, beanM);
	      throw new FacadeException(this, e.toString());
	    }
	    
	  }  

	  /**
	   * Metodo encargado de procesar las solicitudes  
	   * @param param Map
	   * @return List
	   * @throws IncompleteConversationalState
	   * @throws RemoteException
	   * @ejb.interface-method view-type="remote"
		 * @ejb.transaction type="NotSupported"
	   */
	 public List procesarSolicitudes(Map param) throws  FacadeException {
	  	
	  	MensajeBean beanM = new MensajeBean();
	  	List mensajes=new ArrayList();
	  	
	  	try {
			log.debug("Entrando procesarSolicitudes()");
	  		List listado = (ArrayList)param.get("listaRecibidas");
	  		String acciones[]=(String[])param.get("acciones");
	  		String ids[]=(String[])param.get("ids");
	  		String tipo=(String)param.get("cmbTipo");
	  		String codReg=(String)param.get("codReg");
	  		String usuario =(String)param.get("usuario");
	  		String txtObs=(String)param.get("txtObs");
	  		String dbpool = ServiceLocator.getInstance().getString("java:comp/env/dcsp");
	  		String dbpool_g = ServiceLocator.getInstance().getString("java:comp/env/dgsp");
	  		DataSource dbpool_sp = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dcsp");
	  		int j=0;
	  		Map solicitud=null;
	  		int inicio=new Integer(ids[0]).intValue();
	  		int fin=new Integer(ids[ids.length-1]).intValue();
	  		
	  		Map superior = new HashMap();
	  		Map seguridad= null;
	  		AsistenciaFacadeHome  facadeHome = (AsistenciaFacadeHome) ServiceLocator.
	  		getInstance().getRemoteHome(AsistenciaFacadeHome.JNDI_NAME,
	  				AsistenciaFacadeHome.class);
	  		AsistenciaFacadeRemote asistencia = facadeHome.create();
	  		
	  		Map aux = new HashMap();
	  		
	  		List mensaje=new ArrayList();
	  		String accion="";
	  		Map aprobador=null;
	  		Map mapa=null;
	  		for(int i=inicio;i<=fin;i++){
	  			
	  			if(constantes.leePropiedad("ACCION_APROBAR").equals(acciones[j].trim())||
	  					constantes.leePropiedad("ACCION_RECHAZAR").equals(acciones[j].trim())){
	  				
	  				solicitud=(HashMap)listado.get(i);
	  				//ICAPUNAY 05/10/2012 AJUSTES AOM 06A4T11 LABOR EXCEPCIONAL
	  				String estadoSol = solicitud.get("estadoSol")!=null?solicitud.get("estadoSol").toString().trim():"";
	  				log.debug("procesarSolicitudes(solicitud recibida): "+solicitud);
	  				log.debug("procesarSolicitudes(estadoSol): "+estadoSol);
	  				//ICAPUNAY 05/10/2012 AJUSTES AOM 06A4T11 LABOR EXCEPCIONAL
	  				//ICR - 28/11/2012 PAS20124E550000064 labor excepcional
	  				String regimenCol = solicitud.get("regimenCol")!=null?solicitud.get("regimenCol").toString().trim():"";
	  				log.debug("procesarSolicitudes(regimenCol): "+regimenCol);
	  				//FIN ICR - 28/11/2012 PAS20124E550000064 labor excepcional
	  				//obtenemos el usuario responsable de la aprobacion de la solicitud
	  				
	  				solicitud = cargarSolicitud(dbpool, (String) solicitud.get("codPers"), 
	  						(String) solicitud.get("anno"),
	  						(String) solicitud.get("numero"));
	  				log.debug("procesarSolicitudes(nueva solicitud): "+solicitud);
	  				
	  				if (((String) solicitud.get("tipo")).trim().equals(constantes.leePropiedad("MOV_OMISION_MARCA"))
	  						|| ((String) solicitud.get("tipo")).trim().equals(constantes.leePropiedad("MOV_ANULACION_MARCA"))) {
	  						 String txtObs1 = (String) solicitud.get("txtObs");
	  						StringTokenizer st = new StringTokenizer(txtObs1, "&hora=");
	  						solicitud.put("txtObs", st.nextToken());
	  						solicitud.put("txtHora", st.nextToken());
	  					}
	  				
	  				if (((String) solicitud.get("tipo")).trim().equals(constantes.leePropiedad("VACACION_VENTA"))){
	  					
	  					// comentado frd , txtObs = (String) solicitud.get("txtObs");
	  					String txtObsDeSol = (String) solicitud.get("txtObs");
	  					
	  					//solicitud.put("txtObs", txtObs.substring(0,txtObs.indexOf("&diasVenta=")));
	  					solicitud.put("txtObs", txtObs);
	  					solicitud.put("diasVenta",txtObsDeSol.substring(txtObsDeSol.indexOf("&diasVenta=")+11,txtObsDeSol.length()));
						
	  				}if (((String) solicitud.get("tipo")).trim().equals(Constantes.VACACION_POSTERGADA)){
	  					if(log.isDebugEnabled()){
	  						log.debug("ENtrando al tipo Vacacion Rep. para traer datos.");
	  						log.debug("CodPers:"+solicitud.get("codPers").toString());
	  						log.debug("NUmero."+solicitud.get("numero").toString());
	  						log.debug("dbpool."+dbpool);
	  					}	  					
	  					ArrayList vacProgEst = cargarVacProgramadas(dbpool,solicitud.get("codPers").toString(),
								solicitud.get("numero").toString());						
						if (log.isDebugEnabled())
							log.debug("vacPro:" + vacProgEst);
						ArrayList vacSolRep = cargarVacSolicitadasRep(dbpool,
								solicitud.get("codPers").toString(),
								solicitud.get("numero").toString(),
								"54");
						
						if (log.isDebugEnabled())
							log.debug("vacSolRep:" + vacSolRep);
						solicitud.put("d1", vacProgEst);
	  					solicitud.put("d2", vacSolRep);
	  				}

	  				if (((String) solicitud.get("tipo")).trim().equals(constantes.leePropiedad("VACACION_VENTA"))
	  						|| ((String) solicitud.get("tipo")).trim().equals(constantes.leePropiedad("VACACION"))){
	  					T1282DAO t1282dao = new T1282DAO(dbpool_sp);
	  					Map params= new HashMap();
	  					params.put("cod_pers",solicitud.get("codPers"));
	  					params.put("licencia",constantes.leePropiedad("VACACION_PROGRAMADA"));
	  					params.put("est_id",constantes.leePropiedad("PROGRAMACION_EN_SOLICITUD"));
	  					params.put("anno_vac",solicitud.get("annoVac"));
	  					params.put("numero_ref",solicitud.get("numero"));
	  					Map periodoSolicitud=	t1282dao.findByCodPersNumeroRefLicenciaEstid(params);
	  					if (periodoSolicitud!=null){
	  				 	  solicitud.put("periodo",(String)periodoSolicitud.get("periodo"));
	  					} else{
	  						solicitud.put("periodo",null);
	  					}
	  				}
	  				
	  				
	  				aprobador = this.obtenerAprobador(dbpool, (HashMap)solicitud);
	  				
	  				aux.put("dbpool",dbpool);
	  				//aux.put("codPers",codReg); -> JRR 26/05/2010
	  				aux.put("cod_pers",codReg);
	  				aux.put("codUO",(String)solicitud.get("uoOrig"));
	  				
	  				if (constantes.leePropiedad("VACACION").equals(solicitud.get("tipo").toString().trim()))
	  				{
	  					if (log.isDebugEnabled()) log.debug("Solicitud es tipo 07");//PAS20134E550000011 ICR 04/01/2013 Validacion para aprobacion solicitudes
	  					superior = asistencia.buscarTrabajadorJefe(dbpool,codReg,(HashMap)seguridad);
	  					//PAS20134E550000011 ICR 04/01/2013 Validacion para aprobacion solicitudes
	  					if (log.isDebugEnabled()) log.debug("buscarTrabajadorJefe(superior): " + superior);
	  					//superior.put("codJefe",(String)superior.get("t12cod_jefe"));
	  					//superior.put("codUOJefe",(String)superior.get("t12cod_uorg_jefe"));
	  					superior.put("codJefe",superior.get("t12cod_jefe")!=null?(String)superior.get("t12cod_jefe"):(String)superior.get("t02cod_pers"));
	  					superior.put("codUOJefe",superior.get("t12cod_uorg_jefe")!=null?(String)superior.get("t12cod_uorg_jefe"):(String)superior.get("t02cod_uorg"));
	  					if (log.isDebugEnabled()) log.debug("buscarTrabajadorJefe(superior final): " + superior);
	  					//PAS20134E550000011 ICR 04/01/2013 Validacion para aprobacion solicitudes
	  				} else{
	  					if (log.isDebugEnabled()) log.debug("Solicitud no es tipo 07");//PAS20134E550000011 ICR 04/01/2013 Validacion para aprobacion solicitudes
	  					superior = asistencia.buscarSuperiorSolicitud((HashMap)aux);
	  					//PAS20134E550000011 ICR 04/01/2013 Validacion para aprobacion solicitudes
	  					if (log.isDebugEnabled()) log.debug("buscarSuperiorSolicitud(superior): " + superior);
	  					superior.put("codJefe",superior.get("codJefe")!=null?(String)superior.get("codJefe"):(String)aux.get("cod_pers"));
	  					superior.put("codUOJefe",superior.get("codUOJefe")!=null?(String)superior.get("codUOJefe"):(String)aux.get("codUO"));
	  					if (log.isDebugEnabled()) log.debug("buscarSuperiorSolicitud(superior final): " + superior);
	  					//PAS20134E550000011 ICR 04/01/2013 Validacion para aprobacion solicitudes
	  				}
	  				mapa = new HashMap();
	  				mapa.put("accion", acciones[j]);
	  				mapa.put("txtObs", (String)param.get("txtObs"));
	  				mapa.put("userOrig", codReg);//usuario logueado
	  				mapa.put("codJefe", (String) superior.get("codJefe"));
	  				//mapa.put("codUOSeg", (String) superior.get("codUOJefe")); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
	  			    mapa.put("codUOSeg", (String)solicitud.get("uoOrig")); //uuoo de seguimiento debe ser uuoo de solicitud //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
	  				mapa.put("txtObs", txtObs);
	  				mapa.put("periodo",  solicitud.get("periodo"));
	  				mapa.put("MASIVO",constantes.leePropiedad("PROCESAR_SOLICITUD_MASIVO"));	
	  				if (log.isDebugEnabled()) log.debug("procesarSolicitudes(mapa:codUOSeg ICR): " + mapa); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
	  				
	  				solicitud.put("estadoSol",estadoSol);//ICAPUNAY 05/10/2012 AJUSTES AOM 06A4T11 LABOR EXCEPCIONAL
	  				solicitud.put("regimenCol",regimenCol);//ICR - 28/11/2012 PAS20124E550000064 labor excepcional
	  				if(log.isDebugEnabled()) log.debug("SOLICITUD : "+ solicitud);
	  				
	  				mensaje = procesarSolicitud(dbpool_g, (HashMap)solicitud, (HashMap)mapa,(HashMap)aprobador,usuario);	  				
	  				
	  				if(!mensaje.isEmpty()){
	  					if(constantes.leePropiedad("ACCION_APROBAR").equals(acciones[j].trim())){
	  						accion=constantes.leePropiedad("APROBAR_SOLICITUD");
	  					}else{
	  						accion=constantes.leePropiedad("DESAPROBAR_SOLICITUD");
	  					}
	  					
	  					mensajes.add( "Solicitud:"+(String)solicitud.get("numSol")+" Tipo:"+(String)solicitud.get("descrip") +
	  							" Solicitante:"+(String)solicitud.get("codPers")+",  Accion:" +accion);
	  					
	  					for(int k=0;k<mensaje.size();k++){
	  						
	  						mensajes.add(mensaje.get(k).toString());
	  					}
	  				}
	  				
	  				
	  			}
	  			j++;
	  		}
	  		
	  	} catch (DAOException e) {
	  		log.error(e, e);
	  		beanM.setMensajeerror(e.getMessage());
	  		beanM.setMensajesol("Por favor intente nuevamente, posible error en la BD");
	  		throw new FacadeException(this, beanM);
	  	} catch (Exception e) {
	  		log.error(e, e);
	  		if(e.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")){
	  			BeanMensaje beanMst=new BeanMensaje();
	  			beanMst=((IncompleteConversationalState)e).getBeanMensaje();
				throw new IncompleteConversationalState(beanMst);
			}
	  		beanM.setMensajeerror(e.getMessage());
	  		beanM.setMensajesol("Por favor intente nuevamente.");
	  		throw new FacadeException(this, beanM);
	  	}  	
	  	return mensajes;
	  }
	
	/*             */
		/** Obtiene la labor autorizada
		 * @ejb.interface-method view-type="remote"
		 * @ejb.transaction type="NotSupported"
		 */
		public List findHorasLaborAutorizadasConSaldo( HashMap datos)
				throws IncompleteConversationalState, RemoteException {

			BeanMensaje beanM = new BeanMensaje();
			List marcaciones = null;
			try {
				pe.gob.sunat.sp.dao.T02DAO empleadoDAO = new pe.gob.sunat.sp.dao.T02DAO();		//ICR 08/05/2015-PAS20155E230000154-labor antigua  (autorizacion y compensacion) y labor nueva (sol. compensacion) mayor o igual fecha ingreso a sunat
				Map mapaEmpleado = new HashMap();
				Date fecha_ingreso;	
				mapaEmpleado = empleadoDAO.findEmpleado((String)datos.get("dbpool"),(String)datos.get("cod_pers"));	
				fecha_ingreso = (mapaEmpleado!=null && !mapaEmpleado.isEmpty())?((Date)mapaEmpleado.get("t02f_ingsun")):null;	
				datos.put("fecha_ingreso", fecha_ingreso);										//ICR 08/05/2015 - fecha de ingreso no puede ser null
				
				T4819DAO dao = new T4819DAO(sl.getDataSource("java:comp/env/jdbc/dcsp"));
				marcaciones = dao.findHorasLaborAutorizadasConSaldo(datos);
				
			} catch (Exception e) {
				log.error(e,e);
				beanM.setMensajeerror(e.getMessage());
				beanM.setMensajesol("Por favor intente nuevamente.");
				throw new IncompleteConversationalState(beanM);
			}

			return marcaciones;
		}	

		/*MTM*/
		/**
		 * @ejb.interface-method view-type="remote"
		 * @ejb.transaction type="NotSupported"
		 */
		public ArrayList validaLabor(HashMap mapa, HashMap tipoMov) 
			throws IncompleteConversationalState,
				RemoteException {

			log.debug("entro validaLabor");
			ArrayList mensajes = new ArrayList();
			ArrayList lista = (ArrayList) mapa.get("lista");
			String dbpool = (String) mapa.get("dbpool");
			T4821DAO t4821dao = new T4821DAO(dbpool);
			T4818DAO t4818dao = new T4818DAO(dbpool);
			T1282DAO VacacionDAO = new T1282DAO(dbpool);
			T1273DAO LicenciaDAO = new T1273DAO(dbpool);
			//T4819DAO t4819dao = new T4819DAO(dbpool);
			T01DAO paramDAO = new T01DAO();
			T1270DAO turnoDAO = new T1270DAO();
			
			int flag = 0;
			pe.gob.sunat.rrhh.asistencia.dao.T1270DAO t1270dao= new pe.gob.sunat.rrhh.asistencia.dao.T1270DAO(dbpool);
			List verificaLista = new ArrayList();//ICAPUNAY 21/06/2012 NO DEJA REGISTRAR UN RANGO NUEVO QUE INTERSECTE CON RANGO EN SEGUIMIENTO, APROBADOS O RECHAZADOS
			
			try {		
				if(lista.size()==0){
					mensajes.add("No existe registros a generar Labor excepcional.");
				}
				for(int i=0;i<lista.size();i++){
					log.debug("lista: "+lista);
					HashMap u = (HashMap)lista.get(i);
					log.debug("u: "+u);
					flag = 0;
					verificaLista = null;//ICAPUNAY 21/06/2012 NO DEJA REGISTRAR UN RANGO NUEVO QUE INTERSECTE CON RANGO EN SEGUIMIENTO, APROBADOS O RECHAZADOS
					verificaLista = new ArrayList();
															
					//Map mapaPermiso = t4821dao.findfechaPermiso((String)mapa.get("userOrig"),(String)u.get("fechaMarca2"));//ICAPUNAY 21/06/2012 NO DEJA REGISTRAR UN RANGO NUEVO QUE INTERSECTE CON RANGO EN SEGUIMIENTO, APROBADOS O RECHAZADOS
					//if (mapaPermiso!=null && !mapaPermiso.isEmpty()){//ICAPUNAY 21/06/2012 NO DEJA REGISTRAR UN RANGO NUEVO QUE INTERSECTE CON RANGO EN SEGUIMIENTO, APROBADOS O RECHAZADOS
						
					//List estadoRechazado = t4821dao.verificaEstadoFechaPermiso(dbpool,(String) mapa.get("UUOOFIN"),(String) mapa.get("userOrig"),(String) tipoMov.get("mov"),(String) u.get("fechaMarca2"),"3","2","1");
					//log.debug("estadoRechazado: "+estadoRechazado);
					//if(estadoRechazado!=null && estadoRechazado.size()>0){							
					verificaLista = t4821dao.verificaRegistrosRechazadosAutorizaLabor(dbpool,									 
							(String) mapa.get("userOrig"),									
							(String) u.get("fechaMarca2"),
							(String) u.get("txtHoraIni")+":00",
							(String) u.get("txtHoraFin")+":00");							
					log.debug("verificaLista: "+verificaLista);							
					if(verificaLista!=null && verificaLista.size()>0){
						log.debug("llego aca1");
						flag = 1;														
					}
					//}//fin estadoRechazado
					
					//List estadoAprobado = t4821dao.verificaEstadoFechaPermiso(dbpool,(String) mapa.get("UUOOFIN"),(String) mapa.get("userOrig"),(String) tipoMov.get("mov"),(String) u.get("fechaMarca2"),"2","2","1");
					//log.debug("estadoAprobado: "+estadoAprobado);
					//if(estadoAprobado!=null && estadoAprobado.size()>0){						
					List verificaListaAux = new ArrayList();
					verificaListaAux = t4821dao.verificaRegistrosAutorizadosAutorizaLabor(dbpool,									 
							(String) mapa.get("userOrig"),									
							(String) u.get("fechaMarca2"),
							(String) u.get("txtHoraIni")+":00",
							(String) u.get("txtHoraFin")+":00");
					log.debug("verificaListaAux: "+verificaListaAux);
					if(verificaListaAux.size()>0){
						log.debug("llego aca3");
						mensajes.add("Ya se encuentran solicitudes aprobadas para la fecha: "+ u.get("fechaMarca2") + " en el rango de horas.");
						break;
					}
					//}//fin estadoAprobado
						
					List verificaListaAux2 = new ArrayList();
					verificaListaAux2 = t4821dao.verificaRegistroSeguimientoSolicitudDetLabor(dbpool,									 
							(String) mapa.get("userOrig"),
							(String) tipoMov.get("mov"),
							(String) u.get("fechaMarca2"),
							(String) u.get("txtHoraIni")+":00",
							(String) u.get("txtHoraFin")+":00","1","1","0");//1=iniciado 1=en seguimiento y 0=seguimiento despues de crear colaborador
					log.debug("verificaListaAux2(en seguimiento x colab): "+verificaListaAux2);
					if(verificaListaAux2.size()>0){
						log.debug("llego aca4 (en seguimiento x colab)");
						mensajes.add("Ya se encuentra(n) solicitud(es) en seguimiento para la fecha: "+ u.get("fechaMarca2") + " en el rango de horas.");
						break;
					}
					//add ICR 19/12/2012 seguimiento por supervisiones
					else{
						verificaListaAux2 = t4821dao.verificaRegistroSeguimientoSolicitudDetLabor(dbpool,									 
								(String) mapa.get("userOrig"),
								(String) tipoMov.get("mov"),
								(String) u.get("fechaMarca2"),
								(String) u.get("txtHoraIni")+":00",
								(String) u.get("txtHoraFin")+":00","2","1","1");//2=aprobado 1=en seguimiento y 1=seguimiento despues de aprobar supervisor
						log.debug("verificaListaAux2(en seguimiento luego de aprob de supervisor): "+verificaListaAux2);
						if(verificaListaAux2.size()>0){
							log.debug("llego aca4.1 (en seguimiento luego de aprob de supervisor)");
							mensajes.add("Ya se encuentra(n) solicitud(es) en seguimiento para la fecha: "+ u.get("fechaMarca2") + " en el rango de horas.");
							break;
						}
					}
					//fin add
					//}//ICAPUNAY 21/06/2012 NO DEJA REGISTRAR UN RANGO NUEVO QUE INTERSECTE CON RANGO EN SEGUIMIENTO, APROBADOS O RECHAZADOS	
										
					if(t4821dao.verificaRegistroCompensacion(dbpool,(String) mapa.get("userOrig"),(String) u.get("fechaMarca2"))){
						mensajes.add("Fecha de Solicitud: "+ u.get("fechaMarca2") + " ya se encuentra compensada.");
						break;
					}
					
					Map prm = new HashMap();
					prm.put("cod_pers", (String) mapa.get("userOrig"));
					prm.put("fechaIni", (String) u.get("fechaMarca2"));
					prm.put("fechaFin", (String) u.get("fechaMarca2"));
					boolean bVacacion = VacacionDAO.findByCodPersFIniFFin(prm);
					if (bVacacion) {
						mensajes.add("El trabajador se encuentra de vacaciones durante esas fechas.");
						break;
					}
					
					//11/06/2012
					boolean bVacacion1 = VacacionDAO.findByCodPersFIniFFin49(prm);					
					if (bVacacion1) {
						mensajes.add("El trabajador tiene vacaciones programadas durante esas fechas.");
						break;
					}
					//FIN //11/06/2012
					
					Map prms = new HashMap();
					prms.put("cod_pers", (String) mapa.get("userOrig"));
					prms.put("fecha1", (String) u.get("fechaMarca2"));
					prms.put("fecha2", (String) u.get("fechaMarca2"));
					prms.put("numero", "");
					boolean bLicencia = LicenciaDAO.findByCodPersFIniFFin(prms); 		
					if (bLicencia) {
						mensajes.add("El trabajador posee una licencia durante esas fechas.");
						break;
					}					
					boolean diaLab = true; //add icr 18/11                            
					int flag2 = 0;					
					BeanTurnoTrabajo turnoDx = null;
					String HoraIni = (String) u.get("txtHoraIni") + ":00";
					String HoraFin = (String) u.get("txtHoraFin") + ":00";
					String rangoIni = "";
					String rangoFin = "";
					float rango1 = 0;
              	  	float rango2 = 0;
              	  	float rango3 = 0;
              	  	float rango4 = 0;
              	  	String fechaAntes = Utiles.dameFechaAnterior((String) u.get("fechaMarca2"), 1);
              	  	log.debug("fechaAntes: "+fechaAntes);
              	  	
              	    BeanTurnoTrabajo turnogeneral = turnoDAO.joinWithT45ByCodFecha(dbpool, (String) mapa.get("userOrig"),(String) u.get("fechaMarca2"));              	  	
              	    //Si dï¿½a "d" tiene turno
              	    if (turnogeneral != null){
	              	    log.debug("existe turnogeneral dia d sin filtrar si es R67 o no controlado");
	              	    //ICR - 30/11/2012 PAS20124E550000064 labor excepcional
	              	    log.debug("viendo si turno es no controlado turno dia d-1");
    	  				if (!turnogeneral.isControla() || turnogeneral.getTurno().toString().trim().equals("R67")){
    	  					log.debug("dia d(turno ES no controlado o R67)");
    	  					mensajes.add("No puede registrar labor para la fecha: "+ u.get("fechaMarca2") + " porque tiene turno R67 o no controlado.");
    	  				}else{
    	  					log.debug("dia d(turno ES controlado)");
    	  				//FIN ICR - 30/11/2012 PAS20124E550000064 labor excepcional	
    	  					BeanTurnoTrabajo turno1 = turnoDAO.joinWithT45ByCodPersFecha_Controlado_OperAdm1dia(dbpool,(String) mapa.get("userOrig"),(String) u.get("fechaMarca2"));             
	              	  		//Si la hora de inicio del turno es menor a la hora del fin del turno
	              	  		if (turno1!=null){ 
	              	  			log.debug("existe turno1");
	              	  			turnoDx=turno1;  
	              	  			log.debug("turnoDx (x turno1): "+turnoDx);
	              	  			if(!turno1.isOperativo()){
	              	  				log.debug("es adm");
	              	  				if (Utiles.isDiaSemana((String) u.get("fechaMarca2"), Constantes.SABADO)								
										|| Utiles.isDiaSemana((String) u.get("fechaMarca2"), Constantes.DOMINGO)
										|| paramDAO.findByFechaFeriado(dbpool, (String) u.get("fechaMarca2"))) {              	  				
									    	diaLab = false; //add icr 18/11								    	
									    	log.debug("sabado,domingo,feriado");							    	
		              	  			}
	              	  			}              	  			
	              	  			if (diaLab){//add icr 18/11	
	              	  				log.debug("es diaLab");
		              	  			Map mdatos = new HashMap();			
		              	  			mdatos.put("cod_pers", (String) mapa.get("userOrig"));
		              	  			String fecha = u.get("fechaMarca2").toString();
		              	  			log.debug("fecha: "+fecha);
		              	  			SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy"); 
		              	  			Date fechaDate = null;
		              	  			fechaDate = formato.parse(fecha); 
		              	  			log.debug("fechadate: "+fechaDate);
		              	  			mdatos.put("fechaMarca2", fechaDate);
		              	  			mdatos.put("HoraIni", HoraIni);
		              	  			mdatos.put("HoraFin", HoraFin);	
		              	  			log.debug("mdatos: "+mdatos);
		              	  			Map mturno = new HashMap();
		              	  			mturno = t1270dao.findTurnoPersonaExcepcional(mdatos);
		              	  			log.debug("mturno: "+mturno);
		              	  			if (mturno== null){
		              	  				flag2 = 1;
		              	  				log.debug("mturno==null");
		              	  			}		
	              	  			}//add icr 18/11	
	              	  			
	              	  		}else{
	              	  			//Si la hora de inicio del turno es mayor o igual que la hora de fin del turno 
	              	  			BeanTurnoTrabajo turno2 = turnoDAO.joinWithT45ByCodPersFecha_Controlado_Oper2dias(dbpool,(String) mapa.get("userOrig"),(String) u.get("fechaMarca2"));
	              	  			if (turno2!=null){//tiene turno dia d
	              	  				turnoDx=turno2; 
	              	  				log.debug("turnoDx (x turno2): "+turnoDx);              	  				
	              	  				if (turnoDx!=null){//tiene turno dia d              	  					
		              	  				BeanTurnoTrabajo turnoDw = turnoDAO.joinWithT45ByCodPersFecha_Controlado_Oper2dias(dbpool,(String) mapa.get("userOrig"),fechaAntes);
		                  	  			//Si el dï¿½a "d-1" tiene turno
		                  	  			if(turnoDw!=null){
		                  	  				log.debug("existe turnoDw");
		                  	  				rangoIni = turnoDw.getHoraFin();	                  	  				
		                  	  				rangoFin = turnoDx.getHoraIni();
		                  	  				rango1 = Utiles.obtenerHorasDiferencia(rangoIni, HoraIni);
		                  	  				rango2 = Utiles.obtenerHorasDiferencia(rangoIni, HoraFin);
		                  	  				rango3 = Utiles.obtenerHorasDiferencia(HoraIni, rangoFin);
		                  	  				rango4 = Utiles.obtenerHorasDiferencia(HoraFin, rangoFin);
		                  	  				log.debug("rangoIni: "+rangoIni);
		                  	  				log.debug("rangoFin: "+rangoFin);
		                  	  				log.debug("rango1: "+rango1);
		                  	  				log.debug("rango2: "+rango2);
		                  	  				log.debug("rango3: "+rango3);
		                  	  				log.debug("rango4: "+rango4);
		                  	  				if (rango1 < 0 || rango2 < 0 || rango3 < 0 || rango4 < 0){
		                  	  					flag2 = 1;
		                  	  					log.debug("llego aca rangos");
		                  	  				}
		                  	  			}else{
		                  	  				log.debug("NO existe turnoDw");
		                  	  				//Si el dï¿½a "d-1" no tiene turno
		                  	  				rangoIni = turnoDx.getHoraIni();
		                  	  				rango1 = Utiles.obtenerHorasDiferencia(HoraIni, rangoIni);
		                  	  				rango2 = Utiles.obtenerHorasDiferencia(HoraFin, rangoIni);
		                  	  				log.debug("rangoIni: "+rangoIni);
		                  	  				log.debug("rango1: "+rango1);
		                  	  				log.debug("rango2: "+rango2);
		                  	  				if (rango1 <0 || rango2 < 0){
		                  	  					flag2 = 1;
		                  	  					log.debug("llego aca rangos2");
		                  	  				}
		                  	  			}
	              	  				}              	  				
	              	  			}              	  			           	  			
	              	  			//Operativo              	  			
	              	  		}
    	  				}//ICR - 30/11/2012 PAS20124E550000064 labor excepcional	              	  			
              	  	}else{
              	  		log.debug("NO existe turnogeneral dia d sin filtrar si es R67 o no controlado");
              	  		log.debug("fechaAntes: "+fechaAntes);
              	  		//ICR - 30/11/2012 PAS20124E550000064 labor excepcional
              	  		BeanTurnoTrabajo turnogeneral2 = turnoDAO.joinWithT45ByCodFecha(dbpool, (String) mapa.get("userOrig"),fechaAntes);
	      	  			if (turnogeneral2 != null){
	      	  				log.debug("viendo si turno es no controlado turno dia d-1");
	      	  				if (!turnogeneral2.isControla() || turnogeneral2.getTurno().toString().trim().equals("R67")){
	      	  					log.debug("dia d-1(turno ES no controlado o R67)");
	      	  					mensajes.add("No puede registrar labor para la fecha: "+ u.get("fechaMarca2") + " porque tiene turno R67 o no controlado.");
	      	  				}             	  			 
	      	  			}
	      	  			else{
	      	  				log.debug("dia d(turno ES controlado)");
	      	  			//FIN ICR - 30/11/2012 PAS20124E550000064 labor excepcional
	      	  				//Si dï¿½a "d" no tiene turno
	              	  		BeanTurnoTrabajo turnoDw = turnoDAO.joinWithT45ByCodPersFecha_Controlado_Oper2dias(dbpool,(String) mapa.get("userOrig"),fechaAntes);
	              	  		//Si el dï¿½a "d-1" tiene turno
	              	  		if(turnoDw!=null){
	              	  			log.debug("existe turnoDw");
	              	  			rangoFin = turnoDw.getHoraFin();
	              	  			rango1 = Utiles.obtenerHorasDiferencia(rangoFin, HoraIni);
	              	  			rango2 = Utiles.obtenerHorasDiferencia(rangoFin, HoraFin);
	              	  			log.debug("rangoFin: "+rangoFin);
	              	  			log.debug("rango1: "+rango1);
	              	  			log.debug("rango2: "+rango2);
	              	  			if (rango1 < 0 || rango2 < 0){
	              	  				flag2 = 1;
	              	  				log.debug("llego aca rangos3");	
	              	  			}	
	              	  		}
	      	  			}//ICR - 30/11/2012 PAS20124E550000064 labor excepcional             	  		
              	  	}
                    
                    if(flag2 == 1){
                    	mensajes.add("La fecha: "+ u.get("fechaMarca2") + " y rango de horas: "+(String) u.get("txtHoraIni")+" - "+(String) u.get("txtHoraFin")+" intersecta con las horas de trabajo.");
						//break; //add icr 18/11
                    }
														
					if(flag==1 && mensajes.isEmpty()){
						log.debug("llego aca5");
						log.debug("verificaLista: "+verificaLista);
						if (verificaLista!=null && verificaLista.size()>0){
							log.debug("verificaLista.size(): "+verificaLista.size());
							for (int j=0; j<verificaLista.size();j++){
								HashMap prmsLista = (HashMap)verificaLista.get(j);
								Map prms2 = new HashMap();
								prms2.put("cod_pers", (String) mapa.get("userOrig"));						
								log.debug("llego aca6");
								prms2.put("fec_aut", (Date)prmsLista.get("fec_aut"));
								prms2.put("hor_ini_aut", (String) prmsLista.get("hor_ini_aut"));
								prms2.put("hor_fin_aut", (String) prmsLista.get("hor_fin_aut"));
								log.debug("prms2: "+prms2);
							    log.debug("borrar del "+ (String) mapa.get("userOrig")+" fecha: "+prmsLista.get("fec_aut").toString()+" y rango: "+prmsLista.get("hor_ini_aut").toString()+" - "+prmsLista.get("hor_fin_aut").toString());
								t4818dao.deleteRegistroAutorizaExc(dbpool, prms2);
								log.debug("si borro");							
							}
						}						
					}					
				}//fin for lista.size()
				
			} catch (Exception e) {
				log.error(e);			
				mensajes.add(e.toString());
			}
			return mensajes;
		}	
		
		/*             */
		 
		 //MTM
		  /**
		   * Metodo encargado de cargar la lista de fechas y horas de solicitudes de labor expcecional
		   * @param param Map
		   * @return List
		   * @throws IncompleteConversationalState
		   * @throws RemoteException
		   * @ejb.interface-method view-type="remote"
			 * @ejb.transaction type="NotSupported"
		   */
		 public List listaSolicitud(String dbpool, String anno, String numero, String coduo, String codPers,
					String tipo) throws  FacadeException {
		  	
		  	MensajeBean beanM = new MensajeBean();
		  	T4821DAO t4821dao = new T4821DAO(dbpool);
		  	List solicitud = new ArrayList();
		  	try {
		  		solicitud=t4821dao.listaSolicitudes(dbpool, anno, numero, coduo, codPers, tipo);
		  		
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
		  	
		  	return solicitud;
		  }
		 
		  /**
		   * Metodo encargado de cargar la lista de fechas y horas de solicitudes de labor expcecional: jquispecoi 02/2014
		   * @param param Map
		   * @return List
		   * @throws IncompleteConversationalState
		   * @throws RemoteException
		   * @ejb.interface-method view-type="remote"
			 * @ejb.transaction type="NotSupported"
		   */
		 public List listaSolicitudAdministracion(String dbpool, String anno, String numero, String coduo, String codPers,
					String tipo) throws  FacadeException {
		  	
		  	MensajeBean beanM = new MensajeBean();
		  	T4821DAO t4821dao = new T4821DAO(dbpool);
		  	List solicitud = new ArrayList();
		  	try {
		  		solicitud=t4821dao.listaSolicitudesAdministracion(dbpool, anno, numero, coduo, codPers, tipo);
		  		
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
		  	return solicitud;
		  }
		  
		 	/*EBV 18/05/2012*/
			/** Obtiene el saldo de la labor autorizada
			 * @throws Exception 
			 * @ejb.interface-method view-type="remote"
			 * @ejb.transaction type="NotSupported"
			 */
			public Integer findLaborAutorizadaSaldo( HashMap datos)
					throws FacadeException {

				String strDataSource = "java:comp/env/jdbc/dcsp";
				MensajeBean beanM = new MensajeBean();
				T4819DAO dao = new T4819DAO(sl.getDataSource(strDataSource));
				Integer saldo;
				try {
					pe.gob.sunat.sp.dao.T02DAO empleadoDAO = new pe.gob.sunat.sp.dao.T02DAO();		//ICR 08/05/2015-PAS20155E230000154-labor antigua  (autorizacion y compensacion) y labor nueva (sol. compensacion) mayor o igual fecha ingreso a sunat
					Map mapaEmpleado = new HashMap();
					Date fecha_ingreso;	
					mapaEmpleado = empleadoDAO.findEmpleado(strDataSource,(String)datos.get("cod_pers"));	
					fecha_ingreso = (mapaEmpleado!=null && !mapaEmpleado.isEmpty())?((Date)mapaEmpleado.get("t02f_ingsun")):null;	
					datos.put("fecha_ingreso", fecha_ingreso);										//ICR 08/05/2015 - fecha de ingreso no puede ser null
	
					saldo = dao.findSaldoLaborAutorizadas(datos);
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
					
				return saldo;
			}	
			//dtn 04/05/2018
			/** Obtiene la planilla principal activa
			 * @throws Exception 
			 * @ejb.interface-method view-type="remote"
			 * @ejb.transaction type="NotSupported"
			 */
			public Map findPlanillaActiva( HashMap datos)
					throws FacadeException {
				String strDataSource = "java:comp/env/jdbc/dcsp";
				DataSource dsSig = sl.getDataSource("java:comp/env/jdbc/dcsig");
				log.debug("Entrando a findPlanillaActiva.");
				MensajeBean beanM = new MensajeBean();
				TPeriodosDAO tpdao=new TPeriodosDAO(dsSig);
				Map planillaAct=null;
				try {					
					pe.gob.sunat.sp.dao.T02DAO empleadoDAO = new pe.gob.sunat.sp.dao.T02DAO();		//ICR 08/05/2015-PAS20155E230000154-labor antigua  (autorizacion y compensacion) y labor nueva (sol. compensacion) mayor o igual fecha ingreso a sunat
					Map mapaEmpleado = new HashMap();
					//Date fecha_ingreso;	
					mapaEmpleado = empleadoDAO.findEmpleado(strDataSource,datos.get("cod_pers").toString());	
					//fecha_ingreso = (mapaEmpleado!=null && !mapaEmpleado.isEmpty())?((Date)mapaEmpleado.get("t02f_ingsun")):null;
					String regimen=(mapaEmpleado!=null && !mapaEmpleado.isEmpty())?mapaEmpleado.get("t02cod_rel").toString():"";
					
					String tipoPlanilla="";
					if(regimen.equals("09") || regimen.equals("10"))
						tipoPlanilla="02";
					else
						tipoPlanilla="01";
					
					planillaAct=tpdao.findByTPlanilla(tipoPlanilla); //01 planilla activa de régmen 728 y 276, 02 planilla activa de cas
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
					
				return planillaAct;
			}	
			
			/** Obtiene el estado del flag de cruce con planilla activa
			 * @throws Exception 
			 * @ejb.interface-method view-type="remote"
			 * @ejb.transaction type="NotSupported" findParamByCodTabCodigo
			 */
			public String findEstadoByCodTabCodigo( HashMap datos)
					throws FacadeException {
				log.debug("Entrando a findEstadoByCodTabCodigo.");
				MensajeBean beanM = new MensajeBean();
				//DataSource ds = sl.getDataSource("java:comp/env/jdbc/dcsp");
				T99DAO t99dao = new T99DAO();
	            //pe.gob.sunat.rrhh.dao.T99DAO t99dao = new pe.gob.sunat.rrhh.dao.T99DAO(ds);
	            
				String estado="";
				try {					
					estado=(t99dao.findByCodParam(datos.get("dbpool").toString(), datos.get("t99cod_tab").toString(), datos.get("t99codigo").toString())).get("t99estado").toString();
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
			
			//fin dtn
			
			private ArrayList validaSolicitudCompensacion(HashMap mapa, HashMap tipoMov) 
			throws IncompleteConversationalState,RemoteException {
				
				if (log.isDebugEnabled()) log.debug("ingreso a validaSolicitudCompensacion");
				String dbpool = (String) mapa.get("dbpool");
				ArrayList mensajes = new ArrayList();
				T4821DAO t4821DAO = new T4821DAO(sl.getDataSource("java:comp/env/jdbc/dgsp"));
				String fechaIni = (String) mapa.get("fechaIni");
				String fechaFin = (String) mapa.get("fechaFin");
				FechaBean f= new FechaBean(fechaIni);
				int j = new FechaBean().calculaNumDiasHabiles(new FechaBean(fechaIni), new FechaBean(fechaFin))+1;
				if (log.isDebugEnabled()) log.debug(" PRUEBA cantidad de dias habiles: "+ j);
				if (log.isDebugEnabled()) log.debug(" PRUEBA - fechaIni: "+ fechaIni);
				try {
					if(t4821DAO.verificaRegistroSolicitudCompensacion(dbpool, 
							//(String) mapa.get("UUOOFIN"), 
							(String) mapa.get("userOrig"),
							Constantes.MOV_SOLICITUD_COMPENSACION,
							//f.getFormatDate("dd/MM/yyyy")) == false){
							f.getFormatDate("dd/MM/yyyy")) == true){
						mensajes.add("Ya se encuentran solicitudes para la fecha: "+ f.getFormatDate("dd/MM/yyyy") + " en el rango de fechas.");
						
					}				
					for (int i=2;i<=j;i++){
						f = new FechaBean().calculaFecFinHabiles(new FechaBean(fechaIni), i-1);
						if(t4821DAO.verificaRegistroSolicitudCompensacion(dbpool, 
								//(String) mapa.get("UUOOFIN"), 
								(String) mapa.get("userOrig"),
								Constantes.MOV_SOLICITUD_COMPENSACION,
								//f.getFormatDate("dd/MM/yyyy")) == false){
								f.getFormatDate("dd/MM/yyyy")) == true){
							mensajes.add("Ya se encuentran solicitudes para la fecha: "+ f.getFormatDate("dd/MM/yyyy") + " en el rango de fechas.");
							break;
						}	
					}
				} catch (Exception e) {
					log.error(e);			
					mensajes.add(e.toString());
				}
				return mensajes;
			}
			
			//add ICR 20/12 Ajustes Sol. Compen(125) de Pase Labor Excepcional y Compensaciones
			/**
			 * @ejb.interface-method view-type="remote"
			 * @ejb.transaction type="NotSupported"
			 */
			public ArrayList validaLicenciaCompJefe(HashMap mapa, HashMap tipoMov,
					boolean validaRango) throws IncompleteConversationalState,	//jquispecoi: 03/2014
					RemoteException {

				ArrayList mensajes = new ArrayList();
				try {
					log.debug("entro a metodo validaLicenciaCompJefe.... mapa:"+mapa+"     , tipoMov:"+tipoMov+",         validaRango:"+validaRango);
					AsistenciaFacadeHome facadeHome = (AsistenciaFacadeHome) ServiceLocator
							.getInstance().getRemoteHome(AsistenciaFacadeHome.JNDI_NAME,
							AsistenciaFacadeHome.class);
					AsistenciaFacadeRemote facadeRemote = facadeHome.create();			
					
					String tipo = (String)tipoMov.get("mov");
					String qvalida = (String)tipoMov.get("qvalida");
					String dbpool = (String) mapa.get("dbpool");
					String codPers = (String) mapa.get("userOrig");
					String fechaIni = (String) mapa.get("fechaIni");
					String fechaFin = (String) mapa.get("fechaFin");
					String fechaNac = (String) mapa.get("fechaNac");
					String regimenCol = (String) mapa.get("regimenCol");
					if (log.isDebugEnabled()) log.debug("validaLicenciaCompJefe(regimenCol): "+regimenCol);					
					if (log.isDebugEnabled()) log.debug("tipo: "+tipo);
					if (log.isDebugEnabled()) log.debug("qvalida: "+qvalida);
					if (log.isDebugEnabled()) log.debug("codPers: "+codPers);
					if (log.isDebugEnabled()) log.debug("fechaIni: "+fechaIni);
					if (log.isDebugEnabled()) log.debug("fechaFin: "+fechaFin);
					if (log.isDebugEnabled()) log.debug("fechaNac: "+fechaNac);	
				
					
					T99DAO codigoDAO = new T99DAO();				
					log.debug("DBPOOL :"+dbpool);
					T1273DAO licenciaDAO = new T1273DAO(dbpool);
					T1282DAO vacacionDAO = new T1282DAO(dbpool);					
					T01DAO paramDAO = new T01DAO();//ICAPUNAY 07/06 PASE 2012-64 LABOR EXCEPCIONAL
					T4821DAO t4821DAO = new T4821DAO(sl.getDataSource("java:comp/env/jdbc/dgsp"));//ICAPUNAY 07/06 PASE 2012-64 LABOR EXCEPCIONAL
					
				
					if (log.isDebugEnabled()) log.debug("entro a LE de validaLicenciaCompJefe");
					if (log.isDebugEnabled()) log.debug("fechaIni: "+fechaIni);
					if (log.isDebugEnabled()) log.debug("fechaFin: "+fechaFin);
					if (log.isDebugEnabled()) log.debug("regimenCol: "+regimenCol);

					boolean esDiaHabil = true;
					boolean esNoHabil = false;
					T1270DAO tpDAO = new T1270DAO();
					int diferencia = 0;
					diferencia = Utiles.obtenerDiasDiferencia(fechaIni, fechaFin)+1;
					List fechas=new ArrayList();				//jquispecoi
					int descuento = 0;
					int diferencia2 = 0;
					int diferencia3 = 0;
					float horas = 0;
					int	dias = 0;
					int dias_log = 0;							//jquispecoi
					String fechaEval="";

					if (esDiaHabil){
						//buscar si en el rango de fecha existe un turno no asignado o si tiene un turno operativo
						for (int i = 0; i < diferencia; i++) {
							log.debug("inicio for de diferencia");
							log.debug("i :" + i);
							log.debug("diferencia :" + diferencia);
							log.debug("fechaIni :" + fechaIni);
							String fechaSig = Utiles.dameFechaSiguiente(fechaIni, i);
							String fechaMostrar = fechaSig;
							log.debug("fechaSig :" + fechaSig);
							log.debug("fechaMostrar :" + fechaMostrar);
							BeanTurnoTrabajo turno1 = tpDAO.joinWithT45ByCodPersFecha_Controlado_OperAdm1dia(dbpool, codPers, fechaSig);
							if (turno1 != null){
								log.debug("turno de 1 dia");
								Date fechaDate = new FechaBean(turno1.getFechaFin()).getSQLDate();											
								log.debug("finTurno :" + new FechaBean(fechaDate.toString(),"yyyy-MM-dd").getFormatDate("dd/MM/yyyy").toString());							
								diferencia2 = Utiles.obtenerDiasDiferencia(fechaSig,new FechaBean(fechaDate.toString(),"yyyy-MM-dd").getFormatDate("dd/MM/yyyy").toString())+1;
								log.debug("diferencia2 :" + diferencia2);
								diferencia3 = diferencia - i;
								log.debug("diferencia3 :" + diferencia3);							
								if(diferencia2 > 0){
									log.debug("entrada1(diferencia2 > 0)");
									if (diferencia3 > diferencia2){
										log.debug("validaLicenciaCompJefe entro(diferencia3>diferencia2)");
										descuento = 0;
										for ( int j = i;j<i+diferencia2;j++){										
											log.debug("j: "+j);
											log.debug("i: "+i);									
											fechaEval = Utiles.dameFechaSiguiente(fechaIni,j);//fechaSig debe ser fechaIni										
											log.debug("fechaEval :" + fechaEval);
											if (fechaEval!=null && !fechaEval.equals("")){										
												log.debug("entro a validar extremos fechaIni o fechaFin(fechaEval): "+fechaEval);
												if(!turno1.isOperativo()){//adm
													log.debug("turno es adm");
													if ((paramDAO.findByFechaFeriado(dbpool,fechaEval) || Utiles.isWeekEnd(fechaEval))) {
														log.debug("dia es feriado o fin de semana(fechaEval): "+fechaEval);
														esNoHabil = true;
														descuento = descuento + 1;	
														log.debug("descuento :" + descuento);
													}else	fechas.add(fechaEval);				//jquispecoi: 03/2014 add else
												}else	fechas.add(fechaEval);																							
											}else{
												log.debug("no existe fechaEval: " + fechaEval);
											}										
										}
										log.debug("i aca :" + i);
										log.debug("diferencia2 aca :" + diferencia2);
										i=i+(diferencia2-1);
										log.debug("i final aca :" + i);									
										dias = dias + (diferencia2-descuento);
										log.debug("entrada1.0(dias) :" + dias);							
										if (!regimenCol.equals("")){							//jquispe: ini(26/07/2013)
											if (Math.abs(Utiles.obtenerHorasDiferencia(turno1.getHoraIni(), turno1.getHoraFin()))<=8){//<=8 horas --- queda 8 horas																	
													horas =  horas + Math.abs(Utiles.obtenerHorasDiferencia(turno1.getHoraIni(), turno1.getHoraFin()))*(diferencia2-descuento);
													if (log.isDebugEnabled()) log.debug("horas1(cas_noCas <= 8): " + horas);																												
											}else{																					
													horas =  horas + Math.abs(Utiles.obtenerHorasDiferencia(turno1.getHoraIni(), turno1.getHoraFin())-1)*(diferencia2-descuento);
													if (log.isDebugEnabled()) log.debug("horas1(cas_noCas > 8): " + horas);
											}								
										}														//jquispe: fin								
									}else{
										log.debug("validaLicenciaCompJefe entro2(diferencia3<=diferencia2)");
										descuento=0;
										for ( int j = i;j<i+diferencia3;j++){
											log.debug("j: "+j);
											log.debug("i: "+i);
											fechaEval = Utiles.dameFechaSiguiente(fechaIni,j);//fechaSig debe ser fechaIni									
											log.debug("fechaEval :" + fechaEval);
											if (fechaEval!=null && !fechaEval.equals("")){									
												log.debug("entro2 a validar extremos fechaIni o fechaFin(fechaEval): "+fechaEval);
												if(!turno1.isOperativo()){//adm
													log.debug("turno es adm");
													if ((paramDAO.findByFechaFeriado(dbpool,fechaEval) || Utiles.isWeekEnd(fechaEval))) {
														log.debug("dia2 es feriado o fin de semana(fechaEval): "+fechaEval);
														esNoHabil = true;
														descuento = descuento + 1;
														log.debug("descuento :" + descuento);
													}else fechas.add(fechaEval);				//jquispecoi: 03/2014 add else
												}else fechas.add(fechaEval);																						
											}else{
												log.debug("no existe fechaEval2:" + fechaEval);
											}											
										}
										log.debug("i aca2 :" + i);
										log.debug("diferencia3 aca :" + diferencia3);									
										i=i+(diferencia3-1);
										log.debug("i final aca3 :" + i);								
										dias = dias + (diferencia3-descuento);
										log.debug("entrada1.1(dias) :" + dias);								
										if (!regimenCol.equals("")){							//jquispe: ini(26/07/2013)
											if (Math.abs(Utiles.obtenerHorasDiferencia(turno1.getHoraIni(), turno1.getHoraFin()))<=8){//<=8 horas --- queda 8 horas																
													horas =  horas + Math.abs(Utiles.obtenerHorasDiferencia(turno1.getHoraIni(), turno1.getHoraFin()))*(diferencia3-descuento);
													if (log.isDebugEnabled()) log.debug("horas2(cas_noCas <= 8): " + horas);																												
											}else{																					
													horas =  horas + Math.abs(Utiles.obtenerHorasDiferencia(turno1.getHoraIni(), turno1.getHoraFin())-1)*(diferencia3-descuento);
													if (log.isDebugEnabled()) log.debug("horas2(cas_noCas > 8): " + horas);
											}								
										}														//jquispe: fin					
									}
								}else{
									log.debug("entrada2(diferencia2 <= 0)");
									descuento=0;
									log.debug("fechaSig :" + fechaSig);	
									if (fechaSig!=null && !fechaSig.equals("")){								
										log.debug("entro3 a validar extremos fechaIni o fechaFin(fechaSig): "+fechaSig);
										if(!turno1.isOperativo()){//adm
											log.debug("turno es adm");
											if ((paramDAO.findByFechaFeriado(dbpool,fechaSig) || Utiles.isWeekEnd(fechaSig))) {
												log.debug("dia3 es feriado o fin de semana(fechaSig): "+fechaSig);
												esNoHabil = true;
												descuento = 1;
												log.debug("descuento :" + descuento);
											}else		
												fechas.add(fechaSig);				//jquispecoi: 03/2014 add else
										}																	
									}else{
										log.debug("no existe fechaSig:" + fechaSig);
									}														
									if (!regimenCol.equals("")){
										if (descuento==0){								//jquispe: ini(26/07/2013)
											if (Math.abs(Utiles.obtenerHorasDiferencia(turno1.getHoraIni(), turno1.getHoraFin()))<=8){//<=8 horas --- queda 8 horas																
													horas =  horas + Math.abs(Utiles.obtenerHorasDiferencia(turno1.getHoraIni(), turno1.getHoraFin()));
													if (log.isDebugEnabled()) log.debug("horas3(cas_noCas <= 8): " + horas);																												
											}else{																					
													horas =  horas + Math.abs(Utiles.obtenerHorasDiferencia(turno1.getHoraIni(), turno1.getHoraFin())-1);
													if (log.isDebugEnabled()) log.debug("horas3(cas_noCas > 8): " + horas);
											}											//jquispe: fin
											dias = dias + 1;
										}																	
									}							
									log.debug("entrada2(dias) :" + dias);																
								}
							}else{
								BeanTurnoTrabajo turno2 = tpDAO.joinWithT45ByCodPersFecha_Controlado_Oper2dias(dbpool, codPers, fechaSig);
								if (turno2 != null){
									log.debug("turno de 2 dias");
									log.debug("fechaSig :" + fechaSig);										
									if (!regimenCol.equals("")){					//jquispe: ini(26/07/2013)									
										if (Math.abs(Utiles.obtenerHorasDiferencia(turno2.getHoraIni(), turno2.getHoraFin()))<=8){//<=8 horas --- queda 8 horas																
												//horas =  horas + Math.abs(Utiles.obtenerHorasDiferencia(turno2.getHoraIni(), turno2.getHoraFin())); ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
											 	horas =  horas + Math.abs(Utiles.obtenerHorasDiferencia(turno2.getHoraIni(), "24:00:00")); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
											    horas =  horas + Math.abs(Utiles.obtenerHorasDiferencia("00:00:00", turno2.getHoraFin())); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
												if (log.isDebugEnabled()) log.debug("horas4(cas_noCas <= 8): " + horas);																												
										}else{																					
												//horas =  horas + Math.abs(Utiles.obtenerHorasDiferencia(turno2.getHoraIni(), turno2.getHoraFin())-1); ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
												horas =  horas + Math.abs(Utiles.obtenerHorasDiferencia(turno2.getHoraIni(), "24:00:00")); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
												horas =  horas + Math.abs(Utiles.obtenerHorasDiferencia("00:00:00", turno2.getHoraFin())); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
												horas =  horas - 1;
												if (log.isDebugEnabled()) log.debug("horas4(cas_noCas > 8): " + horas);
										}											//jquispe: fin
										dias = dias + 1;
										fechas.add(fechaSig);		//jquispecoi: 03/2014 add 
									}							
									log.debug("dias :" + dias);														
								}else{
									String fechaAntes = Utiles.dameFechaAnterior(fechaSig, 1);
									BeanTurnoTrabajo turno3 = tpDAO.joinWithT45ByCodPersFecha_Controlado_Oper2dias(dbpool, codPers, fechaAntes);
									//add icr
									if (turno3!=null){
										log.debug("turno de 2 dias(dia anterior): "+fechaAntes);
										log.debug("entro turno3");
										if (fechaAntes.compareTo(fechaIni)>=0 && fechaAntes.compareTo(fechaFin)<=0){
											log.debug("no suma dias ni horas");									
										}else{
											log.debug("fechaAntes :" + fechaAntes);												
											if (!regimenCol.equals("")){					//jquispe: ini(26/07/2013)									
												if (Math.abs(Utiles.obtenerHorasDiferencia(turno3.getHoraIni(), turno3.getHoraFin()))<=8){//<=8 horas --- queda 8 horas																
														//horas =  horas + Math.abs(Utiles.obtenerHorasDiferencia(turno3.getHoraIni(), turno3.getHoraFin())); ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
														horas =  horas + Math.abs(Utiles.obtenerHorasDiferencia(turno3.getHoraIni(), "24:00:00")); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
														horas =  horas + Math.abs(Utiles.obtenerHorasDiferencia("00:00:00", turno3.getHoraFin())); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
														if (log.isDebugEnabled()) log.debug("horas5(cas_noCas <= 8): " + horas);																												
												}else{																					
														//horas =  horas + Math.abs(Utiles.obtenerHorasDiferencia(turno3.getHoraIni(), turno3.getHoraFin())-1); ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
														horas =  horas + Math.abs(Utiles.obtenerHorasDiferencia(turno3.getHoraIni(), "24:00:00")); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
														horas =  horas + Math.abs(Utiles.obtenerHorasDiferencia("00:00:00", turno3.getHoraFin())); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
														horas =  horas - 1;
														if (log.isDebugEnabled()) log.debug("horas5(cas_noCas > 8): " + horas);
												}											//jquispe: fin
												dias = dias + 1;
												fechas.add(fechaSig);		//jquispecoi: 03/2014 add 
											}									
											log.debug("dias :" + dias);																					
											log.debug("si suma dias y horas");	
										}								
									}
									//fin add
									else{
										log.debug("NO HAY TURNO PARA FECHA: "+fechaMostrar);
										mensajes.add("Fecha: "+fechaMostrar+ " no tiene turno o tiene turno R67 o no controlado - Modifique rango a compensar");								
									}
								}
							}		
						}
						if (log.isDebugEnabled()) log.debug("termino for(esDiaHabil): "+esDiaHabil);

						if (esDiaHabil){					
							if (log.isDebugEnabled()) log.debug("entro a esDiaHabil: "+esDiaHabil);
							if (log.isDebugEnabled()) log.debug("validaLicenciaCompJefe(dias): "+dias);
							if (log.isDebugEnabled()) log.debug("validaLicenciaCompJefe(horas): "+horas);
							dias_log = dias;		//jquispecoi 03/2014
							
							FechaBean dia = new FechaBean();
							String fechafinComp = dia.getFormatDate("dd/MM/yyyy");
							T4819DAO t4819DAO = new T4819DAO(sl.getDataSource("java:comp/env/jdbc/dcsp"));

							String fechainiComp = codigoDAO.findParamByCodTabCodigo(dbpool,Constantes.CODTAB_PARAMETROS_ASISTENCIA,Constantes.FECHA_INICIO_COMPENSACION);
							if (log.isDebugEnabled()) log.debug("fechainiComp: " +fechainiComp + " - fechafinComp: " +fechafinComp);
							
							//ICAPUNAY-PAS20155E230000052-ajuste comparacion fecha inicio con parametro fecha inicio compensacion para registro y aprobacion sol. compensacion 125
							Date feciniD= new FechaBean(fechaIni).getSQLDate();
							Date fecinicompD= new FechaBean(fechainiComp).getSQLDate();
				
							if(((Integer)mapa.get("dias_ini")).intValue()==-1) //jquispecoi 03/2014
							//if (fechaIni.compareTo(fechainiComp)>=0){
							if (feciniD.compareTo(fecinicompD)>=0){
							//FIN ICAPUNAY-PAS20155E230000052-ajuste comparacion fecha inicio con parametro fecha inicio compensacion para registro y aprobacion sol. compensacion 125
								
								pe.gob.sunat.sp.dao.T02DAO empleadoDAO = new pe.gob.sunat.sp.dao.T02DAO();		//ICR 08/05/2015-PAS20155E230000154-labor antigua  (autorizacion y compensacion) y labor nueva (sol. compensacion) mayor o igual fecha ingreso a sunat
								Map mapaEmpleado = new HashMap();
								Date fecha_ingreso;	
								mapaEmpleado = empleadoDAO.findEmpleado("java:comp/env/jdbc/dcsp",(String) mapa.get("userOrig"));	
								fecha_ingreso = (mapaEmpleado!=null && !mapaEmpleado.isEmpty())?((Date)mapaEmpleado.get("t02f_ingsun")):null;	
								mapa.put("fecha_ingreso", fecha_ingreso);
								
								if (log.isDebugEnabled()) log.debug("fechaIni: "+fechaIni+" mayor que fechainiComp: "+fechainiComp);
								mapa.put("cod_pers",(String) mapa.get("userOrig"));
								Integer saldo = t4819DAO.findSaldoLaborAutorizadas(mapa);
								if (log.isDebugEnabled()) log.debug("saldo: " + saldo);							
								//Calculamos la cantidad de minutos a compensar			
								dias = (int)horas * 60;
								if (log.isDebugEnabled()) log.debug("dias: "+dias);									
								int saldored = saldo.intValue();
								if (log.isDebugEnabled()) log.debug("saldored: "+saldored);	
								if (saldored < dias){							
									mensajes.add("Usted no posee la cantidad de horas suficientes para compensar.");
								}
							}else{
								if (log.isDebugEnabled()) log.debug("fechaIni: "+fechaIni+" menor que fechainiComp: "+fechainiComp);
								mensajes.add("Fecha de inicio: "+fechaIni+" debe ser mayor que la fecha: "+fechainiComp);
							}
							mapa.put("dias_ini", new Integer(dias_log));		//jquispecoi 03/2014
							mapa.put("fechas", fechas);
						}//fin esDiaHabil
					}					
					
					Map prm = new HashMap();
					prm.put("cod_pers", codPers.trim());					
					if ((fechaNac!=null) && (!fechaNac.trim().equals("")))
						prm.put("fechaIni", fechaNac.trim());
					else
						prm.put("fechaIni", fechaIni.trim());
					
					prm.put("fechaFin", fechaFin.trim());
					log.debug("vacacionDAO.findByCodPersFIniFFin(prm)... prm:"+prm);				
					boolean bVacacion = vacacionDAO.findByCodPersFIniFFin(prm);					
					if (bVacacion) {
						mensajes.add("El trabajador se encuentra de vacaciones durante esas fechas.");
					}					
					boolean bVacacion1 = vacacionDAO.findByCodPersFIniFFin49(prm);					
					if (bVacacion1) {
						mensajes.add("El trabajador tiene vacaciones programadas durante esas fechas.");
					}
				
					Map prms = new HashMap();
					prms.put("cod_pers", codPers);					
					if ((fechaNac!=null) && (!fechaNac.trim().equals("")))
						prms.put("fecha1", fechaNac.trim());
					else
						prms.put("fecha1", fechaIni.trim());

					prms.put("fecha2", fechaFin);
					//JMaravi: Este cambio permite ignorar la solicitud actual en la validaciï¿½n de la existencia de otras licencias en los dï¿½as ingresados.
					if(mapa.get("licencia_numero") == null)
						prms.put("numero", "");
					else
						prms.put("numero", mapa.get("licencia_numero"));
					
					log.debug("licenciaDAO.findByCodPersFIniFFin(prms)... prms:"+prms);
					boolean bLicencia = licenciaDAO.findByCodPersFIniFFin(prms); //(dbpool,codPers, fechaIni, fechaFin, "");					
					if (bLicencia) {
						mensajes.add("El trabajador posee una licencia aprobada durante esas fechas.");
					}
					
					ArrayList params = codigoDAO.findByCodTab(dbpool, "0", tipo, Constantes.CODTAB_DIAS_LICENCIA);					
					if (params != null && params.size() > 0) {
						int numDias = 0;
						//si la validacion es por dias habiles
						if (((String)tipoMov.get("ind_dias")).equals("1")) {
							numDias = facadeRemote.obtenerDiasHabilesDiferencia(dbpool,fechaIni,fechaFin) + 1;
						}
						else{
							numDias = Utiles.obtenerDiasDiferencia(fechaIni, fechaFin) + 1;
						}
						log.debug("Dias Habiles ? "+tipoMov.get("ind_dias"));
						log.debug("NumDias Diferencia : "+numDias);

						// verificamos q el numero de dias coincida con algunos de los parametros de dia
						boolean existe = false;
						for (int i = 0; i < params.size() && !existe; i++) {
							BeanT99 p = (BeanT99) params.get(i);
							int dias2 = 0;
							try {
								dias2 = Integer.parseInt(p.getT99descrip().trim());
							} catch (Exception e) {
							}
							existe = dias2 == numDias;
						}
						if (!existe) {
							mensajes.add("El rango de fechas ingresado no corresponde a los par&aacute;metros requeridos por la licencia.");
						}
					}
										
					//verificamos los dias de validacion (qvalida)
					if (Integer.parseInt(qvalida)>0){
						mapa.put("tipoMov",tipo);
						int histoLic = licenciaDAO.findCantidadByTipo(mapa);
						if (Integer.parseInt(qvalida)<=histoLic) {
							mensajes.add("El trabajador posee "+histoLic+" licencia(s) registradas. " + "El m&aacute;ximo permitido es "+qvalida+".");
						}	
					}												
					
				} catch (Exception e) {
					log.error(e);
					mensajes.add(e.toString());
				}
				if (log.isDebugEnabled()) log.debug("validaLicenciaCompJefe(mensajes): "+mensajes);
				return mensajes;
			}
			//fin add ICR 20/12 Ajustes Sol. Compen(125) de Pase Labor Excepcional y Compensaciones
	
	
	//PAS20171U230200001 - solicitud de reintegro  
	//PAS20181U230200067 - solicitud de reintegro, planillas adicionales
	/**
	* Metodo para obtener  solicitud incluido conceptos
	* @param codPers : Codigo de personal
	* @param anno : año de solicitud
	* @param numero : numero de solicitud
	* @return : datos de la solicitud de reintegro
	* @throws pe.gob.sunat.framework.core.ejb.FacadeException
	* @throws java.rmi.RemoteException
	* @ejb.interface-method view-type="remote"
	* @ejb.transaction type="NotSupported"
	*/
	public Map obtenerSolicitudReintegro(String codPers, String anno, String numero) throws pe.gob.sunat.framework.core.ejb.FacadeException,
			java.rmi.RemoteException {

		DataSource dbpool = sl.getDataSource("java:comp/env/jdbc/dcsp");
		DataSource dgsig = sl.getDataSource("jdbc/dgsig");
		
		T8155DAO t8155dao = new T8155DAO(dbpool);
		T8153DAO t8153dao = new T8153DAO(dbpool);
		TPeriodosDAO periodosDAO = new TPeriodosDAO(dgsig);
		
		Map solicitud = new HashMap();
		try {
			log.debug("->Ingreso a obtenerSolicitudReintegro ");
			solicitud = t8155dao.findSolicitudReintegro(codPers, anno, numero);
			
			//traer desripcion de la planilla 
			Map paramPlanOrig = new HashMap();
			paramPlanOrig.put("tipo_plan_tpl", solicitud.get("cod_planorig"));
			paramPlanOrig.put("subt_plan_tpl", solicitud.get("cod_splanorig"));
			paramPlanOrig.put("ano_peri_tpe", solicitud.get("ann_solplan"));
			paramPlanOrig.put("nume_peri_tpe", solicitud.get("mes_solplan")); 
			
			// descripcion de la planilla			
			Map planillaOrigen = periodosDAO.findDescripcionByTPeriodo(paramPlanOrig);
			if(planillaOrigen!=null){
				solicitud.put("desc_splanorig", planillaOrigen.get("desc_subt_stp"));	
			}else{
				solicitud.put("desc_splanorig", "");
			}
			 
			String idSolicitud = (String) solicitud.get("num_seqrein");
			List conceptos = t8153dao.findConceptos(idSolicitud);
			solicitud.put("conceptos", conceptos);
			log.debug("Solicitud  " + solicitud);

		} catch (SQLException e) {
			log.error("Error al intentar obtener Solicitud de Reintegro", e);
			e.printStackTrace();
		}

		return solicitud;
	}

	// PAS20171U230200001 - solicitud de reintegro  
	//PAS20181U230200067 - solicitud de reintegro, planillas adicionales
	/**
	* Metodo para obtener  solicitud incluido conceptos	  
	* @param codPers : Codigo de personal
	* @param anno : año de solicitud
	* @param numero : numero de solicitud
	* @return datos de la solicitud de reintegro
	* @throws pe.gob.sunat.framework.core.ejb.FacadeException
	* @throws java.rmi.RemoteException
	* @ejb.interface-method view-type="remote"
	* @ejb.transaction type="NotSupported"
	*/
		public Map obtenerSolicitudReintegroFinal(String codPers, String anno, String numero, String codRel)
			throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException {

		Map solicitud = null;
		DataSource dsSig = sl.getDataSource("java:comp/env/jdbc/dcsig");

		try {
			if (log.isDebugEnabled())
				log.debug("->Ingreso a obtenerSolicitudReintegroFinal ");
			solicitud = this.obtenerSolicitudReintegro(codPers, anno, numero);

			boolean esCesado = this.esColaboradorCesado(codPers);
			solicitud.put("esCesado", esCesado +"");

			Map params = new HashMap();
			params.put("codPers", codPers);
			params.put("fecha", new FechaBean().getTimestamp()); 
			
			MetasEmpleadoDAO metasEmpleado = new MetasEmpleadoDAO(dsSig);
			String tipoPlanilla =Constantes.CODREL_REG1057.equals(codRel) ? Constantes.PLANILLA_CAS : Constantes.PLANILLA_HABERES;
			MaestroPersonalDAO personalDAO = new MaestroPersonalDAO(dsSig);
			Map empleado = personalDAO.findByCodReg (codPers);			
			log.debug("Colaborador  " + empleado);
			
			String codEmpleado = empleado.get("codi_empl_per").toString();
			String annoOrig = solicitud.get("ann_solplan").toString();
			String numOrig = solicitud.get("mes_solplan").toString();
			String periodoOrig = annoOrig + numOrig;
			log.debug("Periodo origen " + periodoOrig);
			
			Map planDev = metasEmpleado.findByTPlanillaReintegro(tipoPlanilla,codEmpleado ,periodoOrig);			
			if (planDev != null && planDev.size() > 0) { 
				String annoPlanDev = planDev.get("ano_peri_tpe").toString();
				String numPlanDev = planDev.get("nume_peri_tpe").toString(); 
				solicitud.put("emp_ins_planilla", "1"); 		 
				solicitud.put("ann_plandev", annoPlanDev);
				solicitud.put("mes_plandev",numPlanDev );
				int mes = Integer.parseInt(numPlanDev);
				solicitud.put("mes_plandev_desc", new FechaBean().getMesletras(mes - 1));
				solicitud.put("cod_plandev", planDev.get("tipo_plan_tpl"));
				solicitud.put("cod_splandev", planDev.get("subt_plan_tpl"));
				solicitud.put("desc_subt_stp", planDev.get("desc_subt_stp"));
				if(validarSiColaboradorLaboro(annoPlanDev, numPlanDev, codEmpleado, codPers)){
					solicitud.put("emp_laboro_en_periodo", "1");	
				}  
			} 
			if (log.isDebugEnabled())
				log.debug("Solicitud  " + solicitud);

		} catch (Exception e) {
			log.error("Error al intentar obtener Solicitud de Reintegro Final", e);
			e.printStackTrace();
		}

		return solicitud;
	}

	
     //AGONZALESF -PAS20191U230200011 - solicitud de reintegro
	/**
	 * Metodo que valida si el colaborador tienen menos de 30 dias de licencia en un mes 
	 * @param anno
	 * @param numero
	 * @param codEmplPer
	 * @param codPers
	 * @return
	 */
	public boolean validarSiColaboradorLaboro(String anno , String numero , String codEmplPer,String codPers ){
		boolean rpta = false;
		if (log.isDebugEnabled())log.debug("validarSiColaboradorLaboro");
		try {	
			DataSource dsSig = sl.getDataSource("jdbc/dcsig");	
			LicenciasDAO licenciaDAO = new LicenciasDAO(dsSig);
			Map prmtLic = new HashMap();
			prmtLic.put("codiEmplPer",codEmplPer);
			prmtLic.put("anno",anno);			
			prmtLic.put("numero",numero);			
			Map resultado = licenciaDAO.getSumDiasLicencia(prmtLic);
			Integer diasAsistencia  = new Integer( resultado.get("diaslicencia").toString());
			return diasAsistencia.intValue()<30;

		}catch (Exception e) {
			log.error("Error al intentar obtener validarColaboradorPlanilla", e);
			e.printStackTrace();
		}
		return rpta;
	}
	

	//PAS20171U230200001 - solicitud de reintegro 
	/** 
	* Metodo para obtener lista con meses para la solicitud de reintegro  
	* @param filtro : filtro para la busqueda
	* @return : Periodos (meses y años) validos a reintegro
	* @throws RemoteException
	* @ejb.interface-method view-type="remote"
	* @ejb.transaction type="NotSupported"
	*/
	public Map obtenerAnioMesesParaSolReintegro(Map filtro) throws RemoteException {
		Map anioMes = new HashMap();
		Map anios = new HashMap();
		DataSource dsSig = sl.getDataSource("java:comp/env/jdbc/dcsig");
		try {
			if (log.isDebugEnabled())log.debug("->Ingreso a obtenerAnioMesesParaSolReintegro :  " + filtro);
			MaestroPersonalDAO personalDAO = new MaestroPersonalDAO(dsSig);
			Map empleado = personalDAO.findByCodReg(filtro.get("num_registro").toString());
			filtro.put("tipo_plan_tpl", empleado.get("tipo_plan_tpl"));

			HistMetasEmpleaDAO dao = new HistMetasEmpleaDAO(dsSig);
			if (log.isDebugEnabled())log.debug("findAnioMesSolReintegro -> :" + filtro);
			List meses = dao.findAnioMesSolReintegro(filtro);
			if (log.isDebugEnabled())log.debug("findAnioMesSolReintegro -> :" + meses);
			for (int i = 0; i < meses.size(); i++) {
				Map mes = (Map) meses.get(i);
				anios.put(mes.get("anio"), mes.get("anio"));
			}
			anioMes.put("anios", new ArrayList(anios.values()));
			anioMes.put("meses", meses);

		} catch (SQLException e) {
			log.error("Error al obtener el archivo", e);
			throw new RemoteException(e.getMessage());
		}
		return anioMes;
	}

	
	//AGONZALESF -PAS20181U230200067 - solicitud de reintegro, planillas adicionales
	//AGONZALESF -PAS20191U230200011 - ajuste de query para limitar fecha de ingreso del colaborador
	/** 
	* Metodo para obtener lista planillas para la solicitud de reintegro  
	* @param filtro : filtro para la busqueda
	* @return : Planillas validas para reintegro
	* @throws RemoteException
	* @ejb.interface-method view-type="remote"
	* @ejb.transaction type="NotSupported"
	*/
	public Map obtenerPlanillasParaSolReintegro(Map filtro) throws RemoteException {
		Map anioMes = new HashMap();		
		DataSource dsSig = sl.getDataSource("java:comp/env/jdbc/dcsig");
		try {
			if (log.isDebugEnabled())log.debug("->Ingreso a obtenerPlanillasParaSolReintegro :  " + filtro);
			MaestroPersonalDAO personalDAO = new MaestroPersonalDAO(dsSig);
			Map empleado = personalDAO.findByCodReg(filtro.get("num_registro").toString());
			filtro.put("codi_empl_per", empleado.get("codi_empl_per"));
			filtro.put("tipo_plan_tpl", empleado.get("tipo_plan_tpl"));
			Integer limite = new Integer( 0);
			HistMetasEmpleaDAO dao = new HistMetasEmpleaDAO(dsSig);
			Map parametro = dao.findLimitePlanillasSolReintegro();
			if(parametro!= null){
				limite = new Integer(parametro.get("desc_larga").toString());
			}

			if (log.isDebugEnabled())log.debug("filtro para planillas -> :" + filtro);
			List planillas = dao.findPlanillasSolReintegro(filtro,limite);
			if (log.isDebugEnabled())log.debug("listado planillas -> :" + planillas);  
			anioMes.put("planillas", planillas);

		} catch (SQLException e) {
			log.error("Error al obtener el archivo", e);
			throw new RemoteException(e.getMessage());
		}
		return anioMes;
	}

	//PAS20171U230200001 - solicitud de reintegro  
	/** 
	* Funcion para la busqueda de conceptos de solicitud de reintegro - asistencia(desde base planilla) 
	* @param pool_sig : pool siga
	* @param pool_sp_c : pool sp 
	* @param filtro : filtro de busqueda ->tipo_plan_tpl,codi_empl_per,anio,mes
	* @return Lista de conceptos de asistencia/licencias
	* @throws IncompleteConversationalState
	* @throws RemoteException
	* @ejb.interface-method view-type="remote"
	* @ejb.transaction type="NotSupported" 
	*/
	public List buscarConceptosAsistencia(String pool_sig, String pool_sp_c, Map filtro) throws IncompleteConversationalState, RemoteException {
		List conceptos = new ArrayList();
		try {

			if (log.isDebugEnabled())log.debug("Ingreso a buscarConceptosAsistencia ->" + filtro);
			List conceptosAsistencia = procesarConceptosAsistencia(pool_sig, pool_sp_c, filtro);
			String codTiplic1 = "L";
			String codTiplic2 = "P";
			String codTiplic3 = "null";
			filtro.put("cod_tiplic1", codTiplic1);
			filtro.put("cod_tiplic2", codTiplic2);
			filtro.put("cod_tiplic3", codTiplic3);
			List conceptosLicencias = procesarConceptosLicencia(pool_sig, pool_sp_c, filtro);
			log.debug("Asistencia");
			int numSeq = 1;
			for (int i = 0; i < conceptosAsistencia.size(); i++) {
				((BeanSolReinConcepto) conceptosAsistencia.get(i)).setNumSeq(new Integer(numSeq));
				numSeq++;
			}
			conceptos.addAll(conceptosAsistencia);
			log.debug("Licencia");
			for (int i = 0; i < conceptosLicencias.size(); i++) {
				((BeanSolReinConcepto) conceptosLicencias.get(i)).setNumSeq(new Integer(numSeq));
				numSeq++;
			}
			conceptos.addAll(conceptosLicencias);

		} catch (Exception e) {
			log.debug("un error buscarConceptosAsistencia  " + e);
		}
		return conceptos;
	}

	// PAS20171U230200001 - solicitud de reintegro  
	/** 
	* Funcion para la busqueda de conceptos para solicitud de reintegro - licencias , permisos y subsidios   (desde base planilla)
	* @param pool_sig : pool siga
	* @param pool_sp_c : pool sp 
	* @param filtro : filtro de busqueda ->tipo_plan_tpl,codi_empl_per,anio,mes
	* @return Lista de conceptos de asistencia/licencias
	* @throws IncompleteConversationalState
	* @throws RemoteException
	* @ejb.interface-method view-type="remote"
	* @ejb.transaction type="NotSupported" 
	*/
	public List buscarConceptosSubsidios(String pool_sig, String pool_sp_c, Map filtro) throws IncompleteConversationalState, RemoteException {
		String codTiplic1 = "null";
		String codTiplic2 = "null";
		String codTiplic3 = "S";
		filtro.put("cod_tiplic1", codTiplic1);
		filtro.put("cod_tiplic2", codTiplic2);
		filtro.put("cod_tiplic3", codTiplic3);
		List conceptosLicencias = procesarConceptosLicencia(pool_sig, pool_sp_c, filtro);
		int numSeq = 1;
		for (int i = 0; i < conceptosLicencias.size(); i++) {
			((BeanSolReinConcepto) conceptosLicencias.get(i)).setNumSeq(new Integer(numSeq));
			numSeq++;
		}
		return conceptosLicencias;

	}

	// PAS20171U230200001 - solicitud de reintegro  
	/** 
	* Metodo para btener fechas con movimientos  
	* @param filtro : parametros de busqueda
	* @return lista de fechas donde el trabajador tiene movimientos
	* @throws IncompleteConversationalState
	* @throws RemoteException
	* @ejb.interface-method view-type="remote"
	* @ejb.transaction type="NotSupported" 
	*/
	public List buscarFechasConMovimientos(Map filtro) throws IncompleteConversationalState, RemoteException {
		T1454DAO t1454dao = new T1454DAO();
		
		List fechas;
		List fechasOmisionAnulacion = new ArrayList();
		List fechasConMovimiento = new ArrayList();
		try {
			DataSource ds = sl.getDataSource("java:comp/env/jdbc/dcsp");
			DataSource dsSig = sl.getDataSource("java:comp/env/jdbc/dcsig");

			log.debug("Ingreso a buscarFechasConMovimientos -> filtro " + filtro);
			int anioConsulta = Integer.parseInt(filtro.get("anio").toString()); //2017-01
			int mesConsulta = Integer.parseInt(filtro.get("mes").toString());
			if (mesConsulta == 1) {
				anioConsulta = anioConsulta - 1;
				mesConsulta = 12;
			} else {
				mesConsulta = mesConsulta - 1;
			}
			log.debug("Anio y mes ajustados " + anioConsulta + "-" + mesConsulta); //2016-12
			filtro.put("anioConsulta", Utiles.intToString(anioConsulta, 4));
			filtro.put("mesConsulta", Utiles.intToString(mesConsulta, 2));
			fechas = t1454dao.findByFechasConMovimientosParaValidarReintegro(ds, filtro);
			if (fechas != null && !fechas.isEmpty()) {
				for (int i = 0; i < fechas.size(); i++) {
					fechasConMovimiento.add(Utiles.dateToString((Date) fechas.get(i)));
				}
			}
			
			// PAS20181U230200067 - solicitud de reintegro , planillas adicionales, agregar omisiones 
			T1277DAO solicitudDAO = new T1277DAO();
			fechasOmisionAnulacion = solicitudDAO.findFechasSolicitudesOmisionAnulacionMarcas(ds, filtro);  
			if (fechasOmisionAnulacion != null && !fechasOmisionAnulacion.isEmpty()) {
				for (int i = 0; i < fechasOmisionAnulacion.size(); i++) {
					fechasConMovimiento.add(Utiles.dateToString((Date) fechasOmisionAnulacion.get(i)));
				}
			}
			
			log.debug("buscarFechasConMovimientos-->" + fechasConMovimiento);
		} catch (SQLException e) {
			log.error("Error al obtener fechas con movimientos", e);
			throw new RemoteException(e.getMessage());
		}
		return fechasConMovimiento;

	}

	// PAS20171U230200001 - solicitud de reintegro 
	//AGONZALESF -PAS20181U230200067 - solicitud de reintegro, planillas adicionales
	/**
	* Funcion auxiliar para procesar conceptos asistencia para solicitud de reintegro
	* @param pool_sig  : pool de base de datos
	* @param pool_sp_c  : pool de base de datos
	* @param filtro : parametros de busqueda
	* @return lista de conceptos de asistencia
	* @throws IncompleteConversationalState
	* @throws RemoteException
	*/
	private List procesarConceptosAsistencia(String pool_sig, String pool_sp_c, Map filtro) throws IncompleteConversationalState, RemoteException {
		List asistencia = new ArrayList();
		Map conceptos = new TreeMap();
		List conceptosValidos = new ArrayList();
		try {
			if (log.isDebugEnabled())log.debug("Ingreso a procesarConceptosAsistencia -> filtro " + filtro);
			DataSource dcsig = sl.getDataSource(pool_sig);
			DataSource dcsp = sl.getDataSource(pool_sp_c);
			ConsolAsistMensDAO asistMensDAO = new ConsolAsistMensDAO(dcsig);
			T5737DAO t5737dao = new T5737DAO(dcsig);
			T8153DAO t8153dao = new T8153DAO(dcsp);
			
			int anioConsulta = Integer.parseInt(filtro.get("anio").toString()); //2017-01
			int mesConsulta = Integer.parseInt(filtro.get("mes").toString());
			if (mesConsulta == 1) {
				anioConsulta = anioConsulta - 1;
				mesConsulta = 12;
			} else {
				mesConsulta = mesConsulta - 1;
			}
			if (log.isDebugEnabled())log.debug("Anio y mes ajustados " + anioConsulta + "-" + mesConsulta); //2016-12

			filtro.put("anioConsulta", Utiles.intToString(anioConsulta, 4));
			filtro.put("mesConsulta", Utiles.intToString(mesConsulta, 2));

			log.debug("Obtener concepto descuento de la boleta ->" + filtro);
			List descuentos =  obtenerDescuentosBoleta(pool_sig, filtro.get("codi_empl_per").toString(), filtro.get("anio").toString(), filtro.get("mes").toString(), filtro.get("tipoPlanilla").toString(), filtro.get("subtipoPlanilla").toString());
			Map descuentosMap = new HashMap();
			for (int i = 0; i < descuentos.size(); i++) {
				Map descuento = (Map)descuentos.get(i);
				descuentosMap.put(descuento.get("concepto").toString(), descuento );
			}
			
			log.debug("Descuentos en boleta  ->" + descuentos);
			
			log.debug("Obtener concepto ->" + filtro);
			asistencia = asistMensDAO.findConceptosAsistencia(filtro);
			for (int i = 0; i < asistencia.size(); i++) {
				Map concepto = (Map) asistencia.get(i);
				log.debug(concepto);
				BeanSolReinConcepto sol = new BeanSolReinConcepto();
				
				sol.setCodTipLi("");
				sol.setCodConc((String) concepto.get("concepto"));
				sol.setConDesc((String) concepto.get("desc_concepto"));
				sol.setCodMov((String) concepto.get("tipo_movimiento"));
				sol.setDesMov((String) concepto.get("desc_movimiento"));
				BigDecimal devueltos = (BigDecimal) concepto.get("minutos_dscto");
				sol.setDsctoOriginal(devueltos.intValue());
				//solo los descuentos que estan en la boleta solicitada
				String codConcepto = (String) concepto.get("concepto");
				if(descuentosMap.containsKey(codConcepto)){
				conceptos.put(sol.getCodMov(), sol);
				}
			}

			if (log.isDebugEnabled())log.debug("obtener los conceptos tienen  devoluciones -asistencia");
			if (conceptos.size() > 0) {
				List devoluciones = t5737dao.findDevolucionAsistencia(filtro);
				if (devoluciones != null && !devoluciones.isEmpty()) {
					for (int i = 0; i < devoluciones.size(); i++) {
						Map data = (Map) devoluciones.get(i);
						log.debug("devolucion->" + data);
						String movimiento = data.get("movimiento").toString();
						BigDecimal devueltos = (BigDecimal) data.get("devueltos");
						if (conceptos.containsKey(movimiento)) {
							log.debug("devolucion->" + devueltos.intValue());
							((BeanSolReinConcepto) conceptos.get(movimiento)).setDsctoDevuelto(devueltos.intValue());
						}
					}
				}
			}
			if (log.isDebugEnabled())log.debug("obtener los conceptos tienen  solicitudes pendientes  -asistencia");
			if (conceptos.size() > 0) {
				List enProceso = t8153dao.findSolReinConceptoEnProceso(filtro);
				if (enProceso != null && !enProceso.isEmpty()) {
					for (int i = 0; i < enProceso.size(); i++) {
						Map data = (Map) enProceso.get(i);
						log.debug("enProceso->" + data);
						String movimiento = data.get("movimiento").toString();
						BigDecimal minEnProceso = (BigDecimal) data.get("minenproceso");
						if (conceptos.containsKey(movimiento)) {
							log.debug("minEnProceso->" + minEnProceso.intValue());
							((BeanSolReinConcepto) conceptos.get(movimiento)).setDsctoEnProceso(minEnProceso.intValue());
						}
					}
				}
			}
			if (log.isDebugEnabled())log.debug("terminar proceso conceptos  -asistencia");
			List conceptosF = new ArrayList(conceptos.values());
			for (int i = 0; i < conceptos.size(); i++) {
				BeanSolReinConcepto concepto = (BeanSolReinConcepto) conceptosF.get(i);
				int saldo = concepto.getDsctoOriginal() - concepto.getDsctoDevuelto() - concepto.getDsctoEnProceso();
				if (log.isDebugEnabled())log.debug("conceptos  -asistencia valido: " + (saldo > 0) + "->" + concepto.getCodConc() + "-" + concepto.getCodMov() + "..." + concepto.getDsctoOriginal() + "-"
						+ concepto.getDsctoDevuelto() + "-" + concepto.getDsctoEnProceso() + "= " + saldo);
				if (saldo > 0) {

					concepto.setDscto(new Integer(saldo));
					conceptosValidos.add(concepto);
				}
			}

		} catch (Exception e) {
			log.error("Error al obtener detalles de Concepto de asistencia", e);
			throw new RemoteException(e.getMessage());
		}
		return conceptosValidos;

	}

	// PAS20171U230200001 - solicitud de reintegro   
	//AGONZALESF -PAS20181U230200067 - solicitud de reintegro, planillas adicionales
	/**
	* Funcion auxiliar para  procesar conceptos de licencias, subsidios,permisos
	* @param pool_sig  : pool de base de datos
	* @param pool_sp_c  : pool de base de datos
	* @param filtro : parametros de busqueda
	* @return lista de conceptos de licencia 
	* @throws IncompleteConversationalState
	* @throws RemoteException
	*/
	private List procesarConceptosLicencia(String pool_sig, String pool_sp_c, Map filtro) throws IncompleteConversationalState, RemoteException {

		List licencias = new ArrayList();
		Map conceptos = new TreeMap();
		List conceptosValidos = new ArrayList();
		try {
			if (log.isDebugEnabled())log.debug("Ingreso a procesarConceptosLicencia -> filtro " + filtro);
			DataSource dcsig = sl.getDataSource(pool_sig);
			DataSource dcsp = sl.getDataSource(pool_sp_c);
			LicenciasDAO licenciasDAO = new LicenciasDAO(dcsig);
			T5738DAO t5738dao = new T5738DAO(dcsig);
			T8153DAO t8153dao = new T8153DAO(dcsp);

			int anioConsulta = Integer.parseInt(filtro.get("anio").toString()); //2017-01
			int mesConsulta = Integer.parseInt(filtro.get("mes").toString());
			if (mesConsulta == 1) {
				anioConsulta = anioConsulta - 1;
				mesConsulta = 12;
			} else {
				mesConsulta = mesConsulta - 1;
			}
			if (log.isDebugEnabled())log.debug("Anio y mes ajustados " + anioConsulta + "-" + mesConsulta); //2016-12
			filtro.put("anioConsulta", Utiles.intToString(anioConsulta, 4));
			filtro.put("mesConsulta", Utiles.intToString(mesConsulta, 2));
			
			
			log.debug("Obtener concepto descuento de la boleta ->" + filtro);
			List descuentos =  obtenerDescuentosBoleta(pool_sig, filtro.get("codi_empl_per").toString(), filtro.get("anio").toString(), filtro.get("mes").toString(), filtro.get("tipoPlanilla").toString(), filtro.get("subtipoPlanilla").toString());
			Map descuentosMap = new HashMap();
			for (int i = 0; i < descuentos.size(); i++) {
				Map descuento = (Map)descuentos.get(i);
				descuentosMap.put(descuento.get("concepto").toString(), descuento );
			}
			
			log.debug("Descuentos en boleta  ->" + descuentos);
			
			
			if (log.isDebugEnabled())log.debug("obtener concepto");

			licencias = licenciasDAO.findConceptosLicencia(filtro);
			for (int i = 0; i < licencias.size(); i++) {
				Map concepto = (Map) licencias.get(i);
				log.debug(concepto);
				BeanSolReinConcepto sol = new BeanSolReinConcepto();
				sol.setCodTipLi((String) concepto.get("cod_tiplic"));
				sol.setCodConc((String) concepto.get("cod_concepto"));
				sol.setConDesc((String) concepto.get("desc_conc_tco"));
				sol.setCodMov((String) concepto.get("codi_moti_tmo"));
				sol.setDesMov((String) concepto.get("motivo"));
				BigDecimal devueltos = (BigDecimal) concepto.get("dias_dscto");
				sol.setDsctoOriginal(devueltos.intValue());
				//solo los descuentos que estan en la boleta solicitada
				String codConcepto = (String) concepto.get("cod_concepto");
				if(descuentosMap.containsKey(codConcepto)){
				conceptos.put(sol.getCodMov(), sol);
				}
				
			

			}

			if (log.isDebugEnabled())log.debug("obtener si las conceptos tienen devoluciones -licencia");
			if (conceptos.size() > 0) {
				List devoluciones = t5738dao.findDevolucionLicencia(filtro);
				if (devoluciones != null && !devoluciones.isEmpty()) {
					for (int i = 0; i < devoluciones.size(); i++) {
						Map data = (Map) devoluciones.get(i);
						log.debug("devolucion->" + data);
						String movimiento = data.get("cod_mot_licencia").toString();
						BigDecimal devueltos = (BigDecimal) data.get("devueltos");
						if (conceptos.containsKey(movimiento)) {
							((BeanSolReinConcepto) conceptos.get(movimiento)).setDsctoDevuelto(devueltos.intValue());
						}
					}
				}
			}
			if (log.isDebugEnabled())log.debug("obtener si los conceptos tienen solicitudes en proceso - licencia");
			if (conceptos.size() > 0) {
				List enProceso = t8153dao.findSolReinConceptoEnProceso(filtro);
				if (enProceso != null && !enProceso.isEmpty()) {
					for (int i = 0; i < enProceso.size(); i++) {
						Map data = (Map) enProceso.get(i);
						log.debug("enProceso->" + data);
						String movimiento = data.get("movimiento").toString();
						BigDecimal diasProceso = (BigDecimal) data.get("diaenproceso");
						if (conceptos.containsKey(movimiento)) {
							((BeanSolReinConcepto) conceptos.get(movimiento)).setDsctoEnProceso(diasProceso.intValue());
						}
					}
				}
			}
			List conceptosF = new ArrayList(conceptos.values());
			if (log.isDebugEnabled())log.debug("terminar proceso -concepto licencias");
			for (int i = 0; i < conceptosF.size(); i++) {
				BeanSolReinConcepto concepto = (BeanSolReinConcepto) conceptosF.get(i);
				int saldo = concepto.getDsctoOriginal() - concepto.getDsctoDevuelto() - concepto.getDsctoEnProceso();
				if (log.isDebugEnabled())log.debug("concepto licencias valido: " + (saldo > 0) + "->" + concepto.getCodConc() + "-" + concepto.getCodMov() + "..." + concepto.getDsctoOriginal() + "-"
						+ concepto.getDsctoDevuelto() + "-" + concepto.getDsctoEnProceso() + "= " + saldo);

				if (saldo > 0) {

					concepto.setDscto(new Integer(saldo));
					conceptosValidos.add(concepto);
				}
			}

		} catch (Exception e) {
			log.error("Error al obtener  concepto de asistencia", e);
			throw new RemoteException(e.getMessage());
		}
		return conceptosValidos;

	}

	//PAS20171U230200001 - solicitud de reintegro  
	/**	 
	 * Metodo para buscar los detalles de asistencia por movimiento 
	 * @param pool_sig : pool de base de datos
	 * @param pool_sp_c : pool de base de datos
	 * @param filtro : parametro de busqueda -> tipo_plan_tpl , codi_empl_per,anio,  mes,  movi , tipo solicitud
	 * @return Lista de detalles de concepto de asistencia
	 * @throws IncompleteConversationalState
	 * @throws RemoteException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported" 
	 */
	public List buscarDetalleConceptoAsistencia(String pool_sig, String pool_sp_c, Map filtro) throws IncompleteConversationalState, RemoteException {
		List asistencia = new ArrayList();
		Map detalles = new TreeMap();
		List detallesValidos = new ArrayList();
		try {
			if (log.isDebugEnabled())log.debug("Ingreso a buscarDetalleConceptoAsistencia -> filtro " + filtro);
			DataSource dcsig = sl.getDataSource(pool_sig);
			DataSource dcsp = sl.getDataSource(pool_sp_c);
			ConsolAsistDiaDAO asistDiaDAO = new ConsolAsistDiaDAO(dcsig);
			T5737DAO t5737dao = new T5737DAO(dcsig);
			T8154DAO soliReinDetalle = new T8154DAO(dcsp);

			int anioConsulta = Integer.parseInt(filtro.get("anio").toString()); //2017-01
			int mesConsulta = Integer.parseInt(filtro.get("mes").toString());
			if (mesConsulta == 1) {
				anioConsulta = anioConsulta - 1;
				mesConsulta = 12;
			} else {
				mesConsulta = mesConsulta - 1;
			}
			if (log.isDebugEnabled())log.debug("Anio y mes ajustados " + anioConsulta + "-" + mesConsulta); //2016-12
			filtro.put("anioConsulta", Utiles.intToString(anioConsulta, 4));
			filtro.put("mesConsulta", Utiles.intToString(mesConsulta, 2));
			log.debug("obtener concepto");
			asistencia = asistDiaDAO.findDetalleAsistenciaDiaria(filtro);
			for (int i = 0; i < asistencia.size(); i++) {
				Map data = (Map) asistencia.get(i);
				log.debug(data);
				BeanSolReinDetalle detalle = new BeanSolReinDetalle();
				detalle.setFecO(Utiles.dateToString((Date) data.get("fecha")));
				detalle.setCodMov((String) filtro.get("movimiento"));
				detalle.setCodConc((String) filtro.get("concepto"));
				BigDecimal minDescontados = (BigDecimal) data.get("minutos");
				detalle.setDsctoOriginal(minDescontados.intValue());
				detalles.put(detalle.getFecO(), detalle);

			}

			if (log.isDebugEnabled())log.debug("obtener si los detalles de asistencia tienen devoluciones");
			if (asistencia.size() > 0) {
				List devoluciones = t5737dao.findDevolucionAsistenciaDetalle(filtro);
				if (devoluciones != null && !devoluciones.isEmpty()) {
					for (int i = 0; i < devoluciones.size(); i++) {
						Map data = (Map) devoluciones.get(i);
						log.debug("devolucion->" + data);
						String fecha = Utiles.dateToString((Date) data.get("fec_asis_origen"));
						BigDecimal devueltos = (BigDecimal) data.get("devueltos");
						if (detalles.containsKey(fecha)) {
							((BeanSolReinDetalle) detalles.get(fecha)).setDsctoDevuelto(devueltos.intValue());
						}
					}
				}
			}
			if (log.isDebugEnabled())log.debug("obtener si los detalles de asistencias tienen solicitudes en proceso");
			if (detalles.size() > 0) {
				List enProceso = soliReinDetalle.findSolReinDetalleEnProcesoAsistencia(filtro);
				if (enProceso != null && !enProceso.isEmpty()) {
					for (int i = 0; i < enProceso.size(); i++) {
						Map data = (Map) enProceso.get(i);
						log.debug("enProceso->" + data);
						String fecha = Utiles.dateToString((Date) data.get("fec_asisorig"));
						BigDecimal devueltos = (BigDecimal) data.get("minenproceso");
						if (detalles.containsKey(fecha)) {
							((BeanSolReinDetalle) detalles.get(fecha)).setDsctoEnProceso(devueltos.intValue());
						}
					}
				}
			}
			if (log.isDebugEnabled())log.debug("terminar proceso detalle asistencia");
			List detallesF = new ArrayList(detalles.values());
			for (int i = 0; i < detalles.size(); i++) {
				BeanSolReinDetalle detalle = (BeanSolReinDetalle) detallesF.get(i);
				int saldo = detalle.getDsctoOriginal() - detalle.getDsctoDevuelto() - detalle.getDsctoEnProceso();
				if (log.isDebugEnabled())log.debug("detalle asistencia valido:" + (saldo > 0) + "->" + detalle.getFecO() + "... " + detalle.getDsctoOriginal() + "- " + detalle.getDsctoDevuelto() + "-"
						+ detalle.getDsctoEnProceso() + "=" + saldo);
				if (saldo > 0) {
					detalle.setDscto(new Integer(saldo));
					detallesValidos.add(detalle);
				}
			}

		} catch (Exception e) {
			log.error("Error al obtener detalles de Concepto de asistencia", e);
			throw new RemoteException(e.getMessage());
		}
		return detallesValidos;

	}

	// PAS20171U230200001 - solicitud de reintegro 
	/**  
	* Metodo para buscar los detalles de licencias por movimiento
	* @param pool_sig : pool de base de datos
	* @param pool_sp_c : pool de base de datos
	* @param filtro : parametro de busqueda -> tipo_plan_tpl , codi_empl_per,anio,  mes,  movi , tipo solicitud
	* @return Lista de detalles de concepto licencia
	* @throws IncompleteConversationalState
	* @throws RemoteException
	 * @ejb.interface-method view-type="remote"
	* @ejb.transaction type="NotSupported" 
	*/
	public List buscarDetalleConceptoLicencia(String pool_sig, String pool_sp_c, Map filtro) throws IncompleteConversationalState, RemoteException {
		List detallesLicencia = new ArrayList();
		Map detalles = new TreeMap();
		List detallesValidos = new ArrayList();
		try {
			if (log.isDebugEnabled())log.debug("->Ingreso a buscarDetalleConceptoLicencia ");
			if (log.isDebugEnabled())log.debug(filtro);
			DataSource dcsig = sl.getDataSource(pool_sig);
			DataSource dcsp = sl.getDataSource(pool_sp_c);
			LicenciasDAO licenciasDAO = new LicenciasDAO(dcsig);
			T5738DAO t5738dao = new T5738DAO(dcsig);
			T8154DAO soliReinDetalle = new T8154DAO(dcsp);
			T1273DAO t1273dao = new T1273DAO(dcsp);
			T1454DAO t1454dao = new T1454DAO();
			T1277DAO t1277dao = new T1277DAO();
			

			int anioConsulta = Integer.parseInt(filtro.get("anio").toString()); //2017-01
			int mesConsulta = Integer.parseInt(filtro.get("mes").toString());
			if (mesConsulta == 1) {
				anioConsulta = anioConsulta - 1;
				mesConsulta = 12;
			} else {
				mesConsulta = mesConsulta - 1;
			}
			if (log.isDebugEnabled())log.debug("Anio y mes ajustados " + anioConsulta + "-" + mesConsulta); //2016-12
			filtro.put("anioConsulta", Utiles.intToString(anioConsulta, 4));
			filtro.put("mesConsulta", Utiles.intToString(mesConsulta, 2));
			if (log.isDebugEnabled())log.debug("obtener detalles de un concepto");
			detallesLicencia = licenciasDAO.findDetalleLicencia(filtro);
			for (int i = 0; i < detallesLicencia.size(); i++) {
				Map data = (Map) detallesLicencia.get(i);
				log.debug(data);
				BeanSolReinDetalle detalle = new BeanSolReinDetalle();
				detalle.setCodMov((String) filtro.get("movimiento"));
				detalle.setCodConc((String) filtro.get("concepto"));
				BigDecimal diadevueltos = (BigDecimal) data.get("dias_dscto");
				detalle.setDsctoOriginal(diadevueltos.intValue());
				detalle.setFecLiIni(Utiles.dateToString((Date) data.get("fech_inic_lic")));
				detalle.setFecLiFin(Utiles.dateToString((Date) data.get("fech_fina_lic")));
				detalle.setCodLi((BigDecimal) data.get("cod_licencia"));
				detalle.setTipLic((String) data.get("cod_tiplic"));
				detalles.put(detalle.getCodLi(), detalle);
			}

			if (log.isDebugEnabled())log.debug("obtener los detalles de licencia tienen devoluciones");
			if (detalles.size() > 0) {
				List devoluciones = t5738dao.findDevolucionLicenciaDetalle(filtro);
				if (devoluciones != null && !devoluciones.isEmpty()) {
					for (int i = 0; i < devoluciones.size(); i++) {
						Map data = (Map) devoluciones.get(i);
						log.debug("devolucion->" + data);
						BigDecimal codLicencia = (BigDecimal) data.get("cod_licencia");
						BigDecimal devueltos = (BigDecimal) data.get("devueltos");
						if (detalles.containsKey(codLicencia)) {
							((BeanSolReinDetalle) detalles.get(codLicencia)).setDsctoDevuelto(devueltos.intValue());
						}
					}
				}
			}
			if (log.isDebugEnabled())log.debug("obtener los detalles de licencia tienen solicitudes en proceso");
			if (detalles.size() > 0) {
				List enProceso = soliReinDetalle.findSolReinDetalleEnProcesoLicencia(filtro);
				if (enProceso != null && !enProceso.isEmpty()) {
					for (int i = 0; i < enProceso.size(); i++) {
						Map data = (Map) enProceso.get(i);
						log.debug("enProceso->" + data);
						BigDecimal codLicencia = (BigDecimal) data.get("cod_licencia");
						BigDecimal devueltos = (BigDecimal) data.get("diaenproceso");
						if (detalles.containsKey(codLicencia)) {
							((BeanSolReinDetalle) detalles.get(codLicencia)).setDsctoEnProceso(devueltos.intValue());
						}
					}
				}
			}
			if (log.isDebugEnabled())log.debug("terminar proceso detalle licencias");
			List detallesF = new ArrayList(detalles.values());
			for (int i = 0; i < detalles.size(); i++) {
				BeanSolReinDetalle detalle = (BeanSolReinDetalle) detallesF.get(i);
				log.debug(detalle.getDsctoOriginal() + "");
				log.debug(detalle.getDsctoDevuelto() + "");
				log.debug(detalle.getDsctoEnProceso() + "");
				int saldo = detalle.getDsctoOriginal() - detalle.getDsctoDevuelto() - detalle.getDsctoEnProceso();
				if (log.isDebugEnabled())log.debug("detalle licencias valido:" + (saldo > 0) + "->" + detalle.getCodLi() + "... " + detalle.getDsctoOriginal() + "- " + detalle.getDsctoDevuelto() + "-"
						+ detalle.getDsctoEnProceso() + "=" + saldo);
				
				if (saldo > 0) {
					filtro.put("ffinicio", detalle.getFecLiIni());
					filtro.put("ffin", detalle.getFecLiFin());
					filtro.put("cod_licencia", detalle.getCodLi());
					detalle.setExisteSustento(existeMovSustentoReintegro(  filtro));		
					detalle.setDscto(new Integer(saldo));
					detalle.setExisteDev(existeDevolucionesActivas(filtro));
					detallesValidos.add(detalle);
				}
				
			}

		} catch (Exception e) {
			log.error("Error al obtener detalles de Concepto de licencia, permiso,subsidio", e);
			throw new RemoteException(e.getMessage());
		}
		return detallesValidos;

	}

	// PAS20171U230200001 - solicitud de reintegro 
	/**  
	* Metodo para validar datos de solicitud de reintegro - asistencia
	* @param dbpool
	* @param mapa
	* @return 
	*/
	private ArrayList validaReintegroPorDsctoAsistencia(String dbpool, HashMap mapa) {
		if (log.isDebugEnabled())log.debug(mapa);
		return new ArrayList();
	}

	// PAS20171U230200001 - solicitud de reintegro 
	/**  
	* Metodo para validar datos de solicitud de reintegro - licencia
	* @param dbpool
	* @param mapa
	* @return 
	*/
	private ArrayList validaReintegroPorDsctoLicencia(String dbpool, HashMap mapa) {
		if (log.isDebugEnabled())log.debug(mapa);
		return new ArrayList();
	}

	// PAS20171U230200001 - solicitud de reintegro 
	//PAS20181U230200067 - solicitud de reintegro, planillas adicionales
	/**  
	* Metodo para registro de solicitud de reintegro   
	* @param anio
	* @param numero
	* @param mapa :  bean con datos de la solicitud , archivo
	* @param usuario
	* @return
	* @throws SQLException 
	* @ejb.interface-method view-type="remote"
	* @ejb.transaction type="NotSupported"
	*/
	public ArrayList registrarSolReintegro(String anio, Integer numero, HashMap mapa) throws IncompleteConversationalState, RemoteException {

		DataSource dcsp = ServiceLocator.getInstance().getDataSource("jdbc/dcsp");
		DataSource dgsp = ServiceLocator.getInstance().getDataSource("jdbc/dgsp");
		ArrayList mensajes = new ArrayList();
		T8155DAO soliRein = new T8155DAO(dgsp);
		T8153DAO soliReinConc = new T8153DAO(dgsp);
		T8154DAO soliReinDeta = new T8154DAO(dgsp);
		T1277DAO solicitudDAO = new T1277DAO();
		int nroArchivos = 0;
		BeanSolReintegro data = (BeanSolReintegro) mapa.get("solicitudRein");
		try {
			if (log.isDebugEnabled())
				log.debug("Insertar solicitud de reintegro  Filtro->" + mapa);
			Map solicitud = solicitudDAO.findSolicitudByAnioByNro(dcsp, anio, numero);

			if (log.isDebugEnabled())
				log.debug("Llamar a archivo facade para grabar los archivos");

			Integer numArchivo = data.getNumArchivo();
			if (numArchivo != null) {
				ArchivoFacadeHome facadeHome = (ArchivoFacadeHome) ServiceLocator.getInstance().getRemoteHome(ArchivoFacadeHome.JNDI_NAME,
						ArchivoFacadeHome.class);
				ArchivoFacadeRemote facadeRemote = facadeHome.create();
				
				ArchivoTmpFacadeHome facadeTmpHome = (ArchivoTmpFacadeHome) ServiceLocator.getInstance().getRemoteHome(ArchivoTmpFacadeHome.JNDI_NAME,
						ArchivoTmpFacadeHome.class);
				ArchivoTmpFacadeRemote facadeTmpRemote = facadeTmpHome.create();
				
				Map filtro = new HashMap();
				
				filtro.put("cod_estado", Constantes.SOL_REINTEGRO_ESTADO_ACTIVO);
				filtro.put("cod_usucrea", solicitud.get("cod_pers"));
			
				List conceptos = data.getConceptos();
				for (int i = 0; i < conceptos.size(); i++) {
					BeanSolReinConcepto concepto = (BeanSolReinConcepto) conceptos.get(i);
					filtro.put("num_archivo", data.getNumArchivo());
					filtro.put("num_seqdoc", concepto.getNumSeq());
					filtro.put("ind_del", Constantes.EST_ARC_TEMP_OK);
					facadeTmpRemote.cambiarEstado("jdbc/dgsp", filtro, Constantes.EST_ARC_TEMP_COPIAR_PERMANENTE);
				}
				
				
				Map archivo = new HashMap();
				archivo.put("num_archivo", data.getNumArchivo());
				archivo.put("cod_estado", Constantes.SOL_REINTEGRO_ESTADO_ACTIVO);
				archivo.put("cod_usucrea", solicitud.get("cod_pers"));
				archivo.put("ind_del", Constantes.EST_ARC_TEMP_COPIAR_PERMANENTE);
				nroArchivos = facadeRemote.registrarEnPermanente("jdbc/dcsp", archivo);
				
				  archivo = new HashMap();
				archivo.put("num_archivo", data.getNumArchivo());
				facadeTmpRemote.eliminarArchivosTemporales("jdbc/dgsp", archivo);
				
			}

			if (log.isDebugEnabled())
				log.debug("Insertar solicitud de reintegro Solicitud->" + solicitud);

			String numSeqrein = anio + Utiles.intToString(numero.intValue(), 7);
			if (log.isDebugEnabled())
				log.debug("num_seqrein (11digitos)-> ann_solicitud (4digitos) ||  num_solicitud (7digitos) : " + numSeqrein);

			Map solReintegro = new HashMap();
			solReintegro.put("num_seqrein", numSeqrein);
			solReintegro.put("ann_solicitud", solicitud.get("anno"));
			solReintegro.put("num_solicitud", solicitud.get("numero"));
			solReintegro.put("cod_uuoo", solicitud.get("u_organ"));
			solReintegro.put("cod_pers", solicitud.get("cod_pers"));
			solReintegro.put("cod_mov", solicitud.get("licencia"));
			solReintegro.put("fec_solicitud", solicitud.get("ffinicio"));
			solReintegro.put("cod_planorig", data.getCodPlanorig()); //01 haberes 02cas 
			solReintegro.put("cod_splanorig", data.getCodSplanorig()); // planillas adicionales
			solReintegro.put("ann_solplan", data.getAnnSolplan());
			solReintegro.put("mes_solplan", data.getMesSolplan());
			solReintegro.put("cod_usucrea", solicitud.get("cod_pers"));
			solReintegro.put("fec_creacion", new Date());
			solReintegro.put("ind_del", Constantes.INACTIVO);
			if (nroArchivos > 0) {
				solReintegro.put("num_archivo", data.getNumArchivo());
			} else {
				solReintegro.put("num_archivo", null);
			}

			if (log.isDebugEnabled())
				log.debug("Datos solicitud de reintegro: " + solReintegro);
			boolean res = soliRein.insertSolReintegro(solReintegro);
			if (res) {
				if (log.isDebugEnabled())log.debug("Ingreso de conceptos : " + data.getConceptos());
				List conceptos = data.getConceptos();
				for (int i = 0; i < conceptos.size(); i++) {
					BeanSolReinConcepto concepto = (BeanSolReinConcepto) conceptos.get(i);
					Integer numSeqCon = concepto.getNumSeq();
					String codTipLicencia = Utiles.esNuloesVacio(concepto.getCodTipLi()) ? "" : concepto.getCodTipLi(); //<blanco>, L,P,S
					if (log.isDebugEnabled())log.debug("Tipo de licencia " + concepto.getCodTipLi() + "..." + codTipLicencia);
					Map solReinConcepto = new HashMap();
					if (codTipLicencia.equals("")) {
						if (log.isDebugEnabled())
							log.debug("Insertar concepto - asistencia");
						solReinConcepto.put("num_seqrein", numSeqrein);
						solReinConcepto.put("num_seqcon", numSeqCon);
						solReinConcepto.put("cod_tiplicencia", codTipLicencia);
						solReinConcepto.put("cod_concepto", concepto.getCodConc());
						solReinConcepto.put("des_concepto", concepto.getConDesc());
						solReinConcepto.put("cod_movimiento", concepto.getCodMov());
						solReinConcepto.put("des_movimiento", concepto.getDesMov());
						solReinConcepto.put("des_motivo", concepto.getDesMot());
						solReinConcepto.put("cnt_mindscto", concepto.getDscto());
						solReinConcepto.put("cnt_diadscto", new Integer(0));
						solReinConcepto.put("cnt_minsol", concepto.getSolic());
						solReinConcepto.put("cnt_diasol", new Integer(0));
						solReinConcepto.put("cod_estado", Constantes.SOL_REINTEGRO_ESTADO_ACTIVO);
						solReinConcepto.put("cod_usucrea", solicitud.get("cod_pers"));
						solReinConcepto.put("fec_creacion", new Date());
						solReinConcepto.put("ind_del", Constantes.INACTIVO);

					} else {
						if (log.isDebugEnabled())
							log.debug("Insertar concepto - licencia,subsidio ,permiso");
						solReinConcepto.put("num_seqrein", numSeqrein);
						solReinConcepto.put("num_seqcon", numSeqCon);
						solReinConcepto.put("cod_tiplicencia", codTipLicencia);
						solReinConcepto.put("cod_concepto", concepto.getCodConc());
						solReinConcepto.put("des_concepto", concepto.getConDesc());
						solReinConcepto.put("cod_movimiento", concepto.getCodMov());
						solReinConcepto.put("des_movimiento", concepto.getDesMov());
						solReinConcepto.put("des_motivo", concepto.getDesMot());
						solReinConcepto.put("cnt_mindscto", new Integer(0));
						solReinConcepto.put("cnt_diadscto", concepto.getDscto());
						solReinConcepto.put("cnt_minsol", new Integer(0));
						solReinConcepto.put("cnt_diasol", concepto.getSolic());
						solReinConcepto.put("cod_estado", Constantes.SOL_REINTEGRO_ESTADO_ACTIVO);
						solReinConcepto.put("cod_usucrea", solicitud.get("cod_pers"));
						solReinConcepto.put("fec_creacion", new Date());
						solReinConcepto.put("ind_del", Constantes.INACTIVO);
					}
					if (log.isDebugEnabled())
						log.debug("Datos Concepto: " + solReinConcepto);
					boolean resC = soliReinConc.insertConceptoSolReintegro(solReinConcepto);
					if (resC) {
						for (int j = 0; j < concepto.getDetalles().size(); j++) {
							BeanSolReinDetalle detalle = (BeanSolReinDetalle) concepto.getDetalles().get(j);
							Map solReinDetalle = new HashMap();
							if (codTipLicencia.equals("")) {
								if (log.isDebugEnabled())
									log.debug("Insertar detalle de concepto - asistencia");
								Date fechaAsisOrig = Utiles.stringToDate(detalle.getFecO());
								solReinDetalle.put("num_seqrein", numSeqrein);
								solReinDetalle.put("num_seqcon", numSeqCon);
								solReinDetalle.put("cod_licencia", new Integer(0));
								solReinDetalle.put("fec_asisorig", fechaAsisOrig);
								solReinDetalle.put("cnt_mindsctorig", detalle.getDscto());
								solReinDetalle.put("cnt_diadsctorig", new Integer(0));
								solReinDetalle.put("cnt_diasol", new Integer(0));
								solReinDetalle.put("cnt_minsol", detalle.getSolic());
								solReinDetalle.put("cod_tipdev", detalle.getTipDev());
								solReinDetalle.put("des_tipdev", detalle.getTipDevDesc());
								solReinDetalle.put("cod_origmov", concepto.getCodMov());
								solReinDetalle.put("ann_licencia", "");
								solReinDetalle.put("mes_licencia", "");
								solReinDetalle.put("cod_usucrea", solicitud.get("cod_pers"));
								solReinDetalle.put("fec_creacion", new Date());
								solReinDetalle.put("ind_del", Constantes.INACTIVO);
							} else {
								if (log.isDebugEnabled())
									log.debug("Insertar detalle de concepto - licencia ,subsidio ,permiso");
								solReinDetalle.put("num_seqrein", numSeqrein);
								solReinDetalle.put("num_seqcon", numSeqCon);
								solReinDetalle.put("cod_licencia", detalle.getCodLi()); //CON ESTE CODIGO LLMAR A LICENCIA PARA PEDIR MES Y AÑO
								solReinDetalle.put("fec_asisorig", solicitud.get("ffinicio"));
								solReinDetalle.put("cnt_mindsctorig", new Integer(0));
								solReinDetalle.put("cnt_diadsctorig", detalle.getDscto());
								solReinDetalle.put("cnt_diasol", detalle.getSolic());
								solReinDetalle.put("cnt_minsol", new Integer(0));
								solReinDetalle.put("cod_tipdev", detalle.getTipDev());
								solReinDetalle.put("des_tipdev", detalle.getTipDevDesc());
								solReinDetalle.put("cod_origmov", concepto.getCodMov());
								solReinDetalle.put("ann_licencia", data.getAnnSolplan());
								solReinDetalle.put("mes_licencia", data.getMesSolplan());
								solReinDetalle.put("cod_usucrea", solicitud.get("cod_pers"));
								solReinDetalle.put("fec_creacion", new Date());
								solReinDetalle.put("ind_del", Constantes.INACTIVO);
							}
							if (log.isDebugEnabled())
								log.debug("Datos detalle: " + solReinDetalle);
							boolean resD = soliReinDeta.insertDetalleSolReintegro(solReinDetalle);
							if (!resD) {
								mensajes.add("Error al guardar solicitud de reintegro - detalle");
							}
						}
					} else {
						mensajes.add("Error al guardar solicitud de reintegro - concepto");
					}

				}
			} else {
				mensajes.add("Error al guardar solicitud de reintegro");
			}
		} catch (Exception e) {
			log.error("Error en guardar de solicitud", e);
			mensajes.add("Error al guardar la solicitud ");
			return mensajes;
		}
		return mensajes;
	}

	/**
	 * Metodo auxiliar para el envio de notificacion de rechazo de reintegro 
	 * @param solicitud : datos de la solicitud
	 * @param mapa : datos de la solicitud 
	 * @param observacion : observacion de rechazo de reintegro
	 * @throws SQLException
	 */
	private void enviarNotificacionRechazoReintegro(Map solicitud, Map mapa, String observacion) throws SQLException {
	 
		String colaborador = (String) solicitud.get("codPers");

		T02DAO personalDAO = new T02DAO();
		String nombre = colaborador + " - " + personalDAO.findNombreCompletoByCodPers("jdbc/dcsp", colaborador);

		DataSource ds = sl.getDataSource("java:comp/env/jdbc/dcsp");
		pe.gob.sunat.rrhh.dao.T99DAO t99dao = new pe.gob.sunat.rrhh.dao.T99DAO(ds);
		Map filtro = new HashMap();
		filtro.put("t99cod_tab", "R06");
		filtro.put("t99tip_desc", "D");
		filtro.put("t99estado", "1");
		filtro.put("t99codigo", "01");
		Map mapaParam = t99dao.findParamByCodTabCodigo(filtro);
		String correoAsis = (String) mapaParam.get("t99descrip");

		filtro = new HashMap();
		filtro.put("t99cod_tab", "R05");
		filtro.put("t99tip_desc", "D");
		filtro.put("t99estado", "1");
		filtro.put("t99codigo", "03");
		mapaParam = t99dao.findParamByCodTabCodigo(filtro);
		String titulo = Utiles.escapeHtml4((String) mapaParam.get("t99descrip"));
		String asunto = (String) mapaParam.get("t99descrip");

		if (log.isDebugEnabled())log.debug("solicitud" + solicitud);
		Map reintegro = (Map) solicitud.get("solRein");
		String texto = Utiles.textoCorreoNotificacionRechazoReintegro(titulo, nombre, (String) solicitud.get("numSol"), observacion,
				reintegro.get("ann_solplan") + "-" + reintegro.get("mes_solplan"));

		CorreoDAO correoDAO = new CorreoDAO();
		String smtpColaborador  = correoDAO.findCorreoByCodPers("jdbc/dcsp", colaborador);
		//obteniendo el jefe de la uo solicitante 
		T12DAO t12dao = new T12DAO();
		String jefeUO = t12dao.findJefeByUO("jdbc/dcsp",reintegro.get("cod_uuoo").toString());
		if (log.isDebugEnabled()) log.debug("jefeUO: " + jefeUO); 

		String smtpJefe  = correoDAO.findCorreoByCodPers("jdbc/dcsp", jefeUO); 
		if (smtpColaborador != null && !"".equals(smtpColaborador)) {
			try { 
				if (log.isDebugEnabled())log.debug("asunto" + asunto );
				if (log.isDebugEnabled())log.debug("usuario : " + colaborador + "---" + smtpColaborador);
				if (log.isDebugEnabled())log.debug("aprobador  cc : " + jefeUO+ "---" + smtpJefe);
			 	Correo correo = new Correo(smtpColaborador, texto);
			 	Direccion remitente  = new Direccion(correoAsis, "RRHH División de Compensaciones - Asistencia ", 0); 
			 	correo.setRemitente(remitente);
			 	correo.setAsunto(asunto);
				if (smtpJefe != null && !"".equals(smtpJefe)) {
					correo.agregarConCopia(smtpJefe);	
				}
				correo.enviarHtml(); 
			} catch (CorreoException ce) {
				if (log.isDebugEnabled())
					log.debug("Error en CorreoException: " + ce.toString());
				if (log.isDebugEnabled())
					log.debug("No se pudo enviar correo al trabajador con registro: " + colaborador);
			} 
		}
	}

	// PAS20171U230200001 - solicitud de reintegro 
	/**  
	* Metodo auxiliar para  Enviar Notificar al trabajador motivo de aprobacion
	* @param solicitud : datos de la solicitud
	* @param mapa : datos de la solicitud  
	* @throws SQLException 
	*/
	private void enviarMensajeNotificacionAprobacion(Map solicitud, Map mapa) throws SQLException {

		Map reintegro = (Map) solicitud.get("solRein");
		DataSource ds = sl.getDataSource("java:comp/env/jdbc/dcsp");
		T8153DAO t8153dao = new T8153DAO(ds);
		T8155DAO t8155dao = new T8155DAO(ds);
		List conceptos = t8153dao.findConceptoAprobadosBySolicitud(reintegro);
		String numseqrein = (String) reintegro.get("num_seqrein");
		String numSol = (String) solicitud.get("numSol");

	 
		String colaborador = (String) solicitud.get("codPers");

		T02DAO personalDAO = new T02DAO();
		String nombre = colaborador + " - " + personalDAO.findNombreCompletoByCodPers("jdbc/dcsp", colaborador);

		pe.gob.sunat.rrhh.dao.T99DAO t99dao = new pe.gob.sunat.rrhh.dao.T99DAO(ds);
		Map filtro = new HashMap();
		filtro.put("t99cod_tab", "R06");
		filtro.put("t99tip_desc", "D");
		filtro.put("t99estado", "1");
		filtro.put("t99codigo", "01");
		Map mapaParam = t99dao.findParamByCodTabCodigo(filtro);
		String correoAsis = (String) mapaParam.get("t99descrip");

		filtro = new HashMap();
		filtro.put("t99cod_tab", "R05");
		filtro.put("t99tip_desc", "D");
		filtro.put("t99estado", "1");
		filtro.put("t99codigo", "02");
		mapaParam = t99dao.findParamByCodTabCodigo(filtro);
		String titulo = Utiles.escapeHtml4((String) mapaParam.get("t99descrip"));
		String asunto = (String) mapaParam.get("t99descrip");

		Map map = t8155dao.findSolicitudReintegroByNumSeqRein(numseqrein);
		String periodo = map.get("ann_plandev") + "-" + map.get("mes_plandev");

		String texto = Utiles.textoCorreoNotificacionAprobacionReintegro(titulo, nombre, numSol, conceptos, periodo);
		CorreoDAO correoDAO = new CorreoDAO();
		String smtpColaborador  = correoDAO.findCorreoByCodPers("jdbc/dcsp", colaborador);
		//obteniendo el jefe de la uo solicitante 
		T12DAO t12dao = new T12DAO();
		String jefeUO = t12dao.findJefeByUO("jdbc/dcsp",reintegro.get("cod_uuoo").toString());
		if (log.isDebugEnabled()) log.debug("jefeUO: " + jefeUO);
		
		String smtpJefe  = correoDAO.findCorreoByCodPers("jdbc/dcsp", jefeUO);
		if (smtpColaborador != null && !"".equals(smtpColaborador)) {
			try { 
				if (log.isDebugEnabled())log.debug("asunto" + asunto );
				if (log.isDebugEnabled())log.debug("usuario : " + colaborador + "---" + smtpColaborador);
				if (log.isDebugEnabled())log.debug("aprobador  cc : " + jefeUO+ "---" + smtpJefe);
			 	Correo correo = new Correo(smtpColaborador, texto);
			 	Direccion remitente  = new Direccion(correoAsis, "RRHH División de Compensaciones - Asistencia ", 0); 
			 	correo.setRemitente(remitente);
			 	correo.setAsunto(asunto); 
				if (smtpJefe != null && !"".equals(smtpJefe)) {
					correo.agregarConCopia(smtpJefe);	
				}
				correo.enviarHtml(); 
			} catch (CorreoException ce) {
				if (log.isDebugEnabled())
					log.debug("Error en CorreoException: " + ce.toString());
				if (log.isDebugEnabled())
					log.debug("No se pudo enviar correo al trabajador con registro: " + colaborador);
			} 
		}

	}

	/** 
	 * PAS20171U230200001 - solicitud de reintegro  
	 * Metodo auxiliar para enviar mensaje de notificaciobn de rechazo de concepto de solicitud de reintegro
	 * @param solicitud : datos de la solicitud
	 * @param mapa : datos de la solicitud  
	 * @throws SQLException  
	 */
	private void enviarMensajeConceptosExcluidos(Map solicitud, Map mapa) throws SQLException {

		Map reintegro = (Map) solicitud.get("solRein");
		DataSource ds = sl.getDataSource("java:comp/env/jdbc/dcsp");
		T8153DAO t8153dao = new T8153DAO(ds);
		List conceptos = t8153dao.findConceptoExcluidosBySolicitud(reintegro);

		if (conceptos != null && !conceptos.isEmpty()) {
		 
			String colaborador = (String) solicitud.get("codPers");

			T02DAO personalDAO = new T02DAO();
			String nombre = colaborador + " - " + personalDAO.findNombreCompletoByCodPers("jdbc/dcsp", colaborador);

			pe.gob.sunat.rrhh.dao.T99DAO t99dao = new pe.gob.sunat.rrhh.dao.T99DAO(ds);
			Map filtro = new HashMap();
			filtro.put("t99cod_tab", "R06");
			filtro.put("t99tip_desc", "D");
			filtro.put("t99estado", "1");
			filtro.put("t99codigo", "01");
			Map mapaParam = t99dao.findParamByCodTabCodigo(filtro);
			String correoAsis = (String) mapaParam.get("t99descrip");

			filtro = new HashMap();
			filtro.put("t99cod_tab", "R05");
			filtro.put("t99tip_desc", "D");
			filtro.put("t99estado", "1");
			filtro.put("t99codigo", "01");
			mapaParam = t99dao.findParamByCodTabCodigo(filtro);
			String titulo = Utiles.escapeHtml4((String) mapaParam.get("t99descrip"));
			String asunto = (String) mapaParam.get("t99descrip");

			String texto = Utiles.textoCorreoNotificacionRechazoConcepto(titulo, nombre, (String) solicitud.get("numSol"), conceptos);
			CorreoDAO correoDAO = new CorreoDAO();
			String smtpColaborador  = correoDAO.findCorreoByCodPers("jdbc/dcsp", colaborador);
			//obteniendo el jefe de la uo solicitante 
			T12DAO t12dao = new T12DAO();
			String jefeUO = t12dao.findJefeByUO("jdbc/dcsp",reintegro.get("cod_uuoo").toString());
			if (log.isDebugEnabled()) log.debug("jefeUO: " + jefeUO);
			
			
			String smtpJefe  = correoDAO.findCorreoByCodPers("jdbc/dcsp", jefeUO);
			if (smtpColaborador != null && !"".equals(smtpColaborador)) {
				try {  
					if (log.isDebugEnabled())log.debug("asunto" + asunto );
					if (log.isDebugEnabled())log.debug("usuario : " + colaborador + "---" + smtpColaborador);
					if (log.isDebugEnabled())log.debug("aprobador  cc : " + jefeUO+ "---" + smtpJefe);

				 	Correo correo = new Correo(smtpColaborador, texto);
				 	Direccion remitente  = new Direccion(correoAsis, "RRHH División de Compensaciones - Asistencia ", 0); 
				 	correo.setRemitente(remitente);
				 	correo.setAsunto(asunto);
					if (smtpJefe != null && !"".equals(smtpJefe)) {
						correo.agregarConCopia(smtpJefe);	
					}
					correo.enviarHtml(); 
				} catch (CorreoException ce) {
					if (log.isDebugEnabled())
						log.debug("Error en CorreoException: " + ce.toString());
					if (log.isDebugEnabled())
						log.debug("No se pudo enviar correo al trabajador con registro: " + colaborador);
				} 
			}

		}

	}

	// PAS20171U230200001 - solicitud de reintegro 
	/**  
	* Funcion para excluir de concepto por parte de jefe
	* @param dbpool pool de conexion
	* @param mapa datos de concepto
	* @return mensajes de error
	* @ejb.interface-method view-type="remote"
	* @ejb.transaction type="NotSupported" 
	
	*/
	public ArrayList marcarExcluirConcepto(String dbpool, HashMap mapa) {
		ArrayList mensajes = new ArrayList();

		DataSource dcsp = sl.getDataSource(dbpool);
		T8153DAO soliReinConc = new T8153DAO(dcsp);
		mapa.put("ind_del", Constantes.ACTIVO);
		boolean res = soliReinConc.cambiarEstado(mapa, Constantes.SOL_REINTEGRO_ESTADO_INACTIVO);
		return mensajes;

	}

	// PAS20171U230200001 - solicitud de reintegro 
	/**  
	* Metodo para actualizar el concepto , 
	* llamado desde la aprobacion de rrhhh con la cantidad a devolver y el tipo de devolucion
	* @param mapa datos de conceptos
	* @return lista de mensajes de error
	* @ejb.interface-method view-type="remote"
	* @ejb.transaction type="Required"
	*/
	public List procesarDetalleConcSolReintegro(HashMap mapa) {
		ArrayList mensajes = null;

		DataSource dcsp = sl.getDataSource("java:comp/env/jdbc/dgsp");
		DataSource dcsig = sl.getDataSource("java:comp/env/jdbc/dcsig");
		T8154DAO soliReinDeta = new T8154DAO(dcsp);
		T8153DAO soliReinConc = new T8153DAO(dcsp);
		T8155DAO soliReinDAO = new T8155DAO(dcsp);
		TipoSalidasDAO salidas = new TipoSalidasDAO(dcsig);
		Map tipos =  salidas.getAll();
		
		
		FechaBean fActual = new FechaBean();

		try {
			mapa.put("fec_modif", fActual.getTimestamp());

			soliReinDAO.updateDevSolicitud(mapa);

			List lstConceptos = (ArrayList) mapa.get("conceptos");

			if (lstConceptos != null && lstConceptos.size() > 0) {
				for (int i = 0; i < lstConceptos.size(); i++) {
					Map concepto = (HashMap) lstConceptos.get(i);
					if (concepto.get("ind_del") == null || ((String) concepto.get("ind_del")).equals("0")) {

						concepto.put("cod_usumodif", mapa.get("cod_usumodif"));
						concepto.put("fec_modif", fActual.getTimestamp());

						soliReinConc.updateDevolucionConcepto(concepto);

						//PAS20171U230200033 -agonzalesf se procesa uno por uno en actualizar detalle
//						List lstDetSol = (ArrayList) concepto.get("lstDetSolReintegro");
//
//						if (lstDetSol != null && lstDetSol.size() > 0) {
//							for (int j = 0; j < lstDetSol.size(); j++) {
//								Map detalle = (HashMap) lstDetSol.get(j);
//								detalle.put("cod_usumodif", mapa.get("cod_usumodif"));
//								detalle.put("fec_modif", fActual.getTimestamp());
//								detalle.put("num_seqrein", mapa.get("num_seqrein"));
//								detalle.put("num_seqcon", concepto.get("num_seqcon"));
//								String tipDev =(String) detalle.get("cod_tipdev");
//								detalle.put("des_tipdev", tipos.get(tipDev)); 
//								soliReinDeta.updateDevolucionDetalles((HashMap) detalle); //actualizar detalles
//							}
//						}
					}
				}
			}
		} catch (Exception e) {
			mensajes.add(e.getMessage());
		}

		return mensajes;
	}

	// PAS20171U230200001 - solicitud de reintegro 
	/**  
	* Metodo para Aprobacion final de rrhh para la solicitud de Sol. Reintegro Subsidio
	* @param filtro : datos de solicitud a aprobar	 
	* @return Lista de mensajes error
	* @throws SQLException   
	*/
	private List aprobarRRHHSolReintegroSubsidio(Map filtro, Map solicitud) throws SQLException {
		ArrayList mensajes = new ArrayList();
		DataSource dgsig = sl.getDataSource("jdbc/dgsig");
		DataSource dcsp = sl.getDataSource("jdbc/dcsp");
		T5738DAO devLicencia = new T5738DAO(dgsig);
		log.debug("Mapa ->" + filtro);
		//filtro  num_seqrein=   codPers=

		Map conceptos =  new HashMap();
		List conceptosSol =(List)solicitud.get("conceptos");
		for (int i = 0; i < conceptosSol.size(); i++) {
			Map concepto = (Map)conceptosSol.get(i);
			String numSeqCon = concepto.get("num_seqcon").toString(); 
			BigDecimal monto = (BigDecimal)concepto.get("mtoUnitario");
			conceptos.put(numSeqCon, monto);
		}
		

		T8154DAO soliReinDeta = new T8154DAO(dcsp);
		List detalles = soliReinDeta.findSolicitudDetallesByNumSeqRein(filtro.get("num_seqrein").toString());
		MaestroPersonalDAO dao = new MaestroPersonalDAO(dgsig);
		Map empleado = dao.findByCodReg(filtro.get("codPers").toString());
		for (int i = 0; i < detalles.size(); i++) {
			Map detalle = (Map) detalles.get(i);
			if (log.isDebugEnabled())log.debug("detalle ->" + detalle);
			String codTipLicencia = detalle.get("cod_tiplicencia").toString() == null ? "" : detalle.get("cod_tiplicencia").toString(); //  S
			if (log.isDebugEnabled())log.debug("Tipo de licencia " + codTipLicencia);
			String cantidad = Utiles.esNuloesVacio(detalle.get("cnt_diadev").toString()) ? "0" : detalle.get("cnt_diadev").toString();
			int solicitado = Integer.parseInt(cantidad) ;  
			if (solicitado>0) { 
			Map devolucion = new HashMap();
			devolucion.put("p_tipo", detalle.get("cod_plandev"));
			devolucion.put("p_subtipo", detalle.get("cod_splandev"));
			devolucion.put("p_anio", detalle.get("ann_plandev"));
			devolucion.put("p_mes", detalle.get("mes_plandev"));
			devolucion.put("p_empl", empleado.get("codi_empl_per"));
			devolucion.put("p_cod_licencia", detalle.get("cod_licencia"));
			devolucion.put("p_cod_tiplic", detalle.get("cod_tiplicencia"));
			devolucion.put("p_cod_origmov", detalle.get("cod_origmov"));
			devolucion.put("p_con_dscto", detalle.get("cod_concepto"));
			devolucion.put("p_cnt_diadev", detalle.get("cnt_diadev"));
			devolucion.put("p_des_observa", detalle.get("des_observa")!=null ?detalle.get("des_observa") : " ");
			devolucion.put("p_ann_lic_calc", detalle.get("ann_licencia"));
			devolucion.put("p_mes_lic_calc", detalle.get("mes_licencia"));
			devolucion.put("p_ind_origen", new Integer(2));
			devolucion.put("p_num_solicitud", detalle.get("num_seqrein"));
			BigDecimal monto =(BigDecimal)conceptos.get(detalle.get("num_seqcon").toString()) ;
			devolucion.put("p_mto_unitario",monto);
			devolucion.put("p_cod_tipdev", detalle.get("cod_tipdev"));
			if (log.isDebugEnabled())log.debug("devolucion ->" + detalle);
			devLicencia.insertDevLicencia(devolucion);
			}
			

		}

		return mensajes;
	}

	// PAS20171U230200001 - solicitud de reintegro 
	/**  
	* Aprobacion final de rrhh para la solicitud de Sol. Reintegro Asistencia
	* @param filtro : datos de solicitud a aprobar	 
	* @return Lista de mensajes error
	* @throws SQLException  
	*/
	private List aprobarRRHHSolReintegroAsistencia(Map filtro, Map solicitud) throws SQLException {
		ArrayList mensajes = new ArrayList();
		DataSource dgsig = sl.getDataSource("jdbc/dgsig");
		DataSource dcsp = sl.getDataSource("jdbc/dcsp");
		
		T5737DAO devAsistencia = new T5737DAO(dgsig);
		T5738DAO devLicencia = new T5738DAO(dgsig);
	 
		log.debug("Mapa ->" + filtro);
		T8154DAO soliReinDeta = new T8154DAO(dcsp);

		Map conceptos =  new HashMap();
		List conceptosSol =(List)solicitud.get("conceptos");
		for (int i = 0; i < conceptosSol.size(); i++) {
			Map concepto = (Map)conceptosSol.get(i);
			String numSeqCon = concepto.get("num_seqcon").toString(); 
			BigDecimal monto = (BigDecimal)concepto.get("mtoUnitario");
			conceptos.put(numSeqCon, monto);
		}
		
		List detalles = soliReinDeta.findSolicitudDetallesByNumSeqRein(filtro.get("num_seqrein").toString());
		MaestroPersonalDAO dao = new MaestroPersonalDAO(dgsig);
		Map empleado = dao.findByCodReg(filtro.get("codPers").toString());
		for (int i = 0; i < detalles.size(); i++) {
			Map detalle = (Map) detalles.get(i);
			if (log.isDebugEnabled())log.debug("detalle ->" + detalle);
			String codTipLicencia = Utiles.esNuloesVacio(detalle.get("cod_tiplicencia").toString()) ? "" : detalle.get("cod_tiplicencia").toString(); //<blanco>, L,P,S			
			if (log.isDebugEnabled())log.debug("Tipo de licencia " + codTipLicencia.trim());
			 
			if (codTipLicencia.equals("")) {
				String cantidad = Utiles.esNuloesVacio(detalle.get("cnt_mindev").toString()) ? "0" : detalle.get("cnt_mindev").toString();
				int solicitado = Integer.parseInt(cantidad) ;  
				if (solicitado>0) { 
				if (log.isDebugEnabled())log.debug("devolucion de asistencia");
				Map devolucion = new HashMap();
				devolucion.put("p_tipo", detalle.get("cod_plandev"));
				devolucion.put("p_subtipo", detalle.get("cod_splandev"));
				devolucion.put("p_anio", detalle.get("ann_plandev"));
				devolucion.put("p_mes", detalle.get("mes_plandev"));
				devolucion.put("p_empl", empleado.get("codi_empl_per"));
				devolucion.put("p_fec_asis", Utiles.dateToString((Date) detalle.get("fec_asisorig")));
				devolucion.put("p_cod_origmov", detalle.get("cod_origmov"));
				devolucion.put("p_con_dscto", detalle.get("cod_concepto"));
				devolucion.put("p_cod_tipdev", detalle.get("cod_tipdev"));
				devolucion.put("p_cnt_mindev", detalle.get("cnt_mindev"));
				devolucion.put("p_des_observa", detalle.get("des_observa") != null ? detalle.get("des_observa") : "");
				devolucion.put("p_anioorig", detalle.get("ann_solplan"));
				devolucion.put("p_mesorig", detalle.get("mes_solplan"));
				devolucion.put("p_ind_origen", new Integer(2));
				devolucion.put("p_num_solicitud", detalle.get("num_seqrein"));
				BigDecimal monto =(BigDecimal)conceptos.get(detalle.get("num_seqcon").toString()) ;
				devolucion.put("p_mto_unitario",monto);
				log.debug("devolucion ->" + devolucion);
				devAsistencia.insertDevAsistencia(devolucion);
				}

			} else {//L Y P
				if (log.isDebugEnabled())log.debug("devolucion de licencia");
				String cantidad = Utiles.esNuloesVacio(detalle.get("cnt_diadev").toString()) ? "0" : detalle.get("cnt_diadev").toString();
				int solicitado = Integer.parseInt(cantidad) ;  
				if (solicitado>0) { 
				Map devolucion = new HashMap();
				devolucion.put("p_tipo", detalle.get("cod_plandev"));
				devolucion.put("p_subtipo", detalle.get("cod_splandev"));
				devolucion.put("p_anio", detalle.get("ann_plandev"));
				devolucion.put("p_mes", detalle.get("mes_plandev"));
				devolucion.put("p_empl", empleado.get("codi_empl_per"));
				devolucion.put("p_cod_licencia", detalle.get("cod_licencia"));
				devolucion.put("p_cod_tiplic", detalle.get("cod_tiplicencia"));
				devolucion.put("p_cod_origmov", detalle.get("cod_origmov"));
				devolucion.put("p_con_dscto", detalle.get("cod_concepto"));
				devolucion.put("p_cnt_diadev", detalle.get("cnt_diadev"));
				devolucion.put("p_des_observa", detalle.get("des_observa") != null ? detalle.get("des_observa") : "");
				devolucion.put("p_ann_lic_calc", detalle.get("ann_licencia"));
				devolucion.put("p_mes_lic_calc", detalle.get("mes_licencia"));
				devolucion.put("p_ind_origen", new Integer(2));
				devolucion.put("p_num_solicitud", detalle.get("num_seqrein"));
				BigDecimal monto =(BigDecimal)conceptos.get(detalle.get("num_seqcon").toString()) ;
				devolucion.put("p_mto_unitario",monto);
				devolucion.put("p_cod_tipdev", detalle.get("cod_tipdev"));
				log.debug("devolucion ->" + devolucion);
				devLicencia.insertDevLicencia(devolucion);
			}
			}

		}

		return mensajes;
	}

	// PAS20171U230200001 - solicitud de reintegro 
	//AGONZALESF -PAS20191U230200011 - solicitud de reintegro  deja de usar  esta validacion 
	/**  
	* Metodo  para verificar el cese de trabajador
	* @param usuario
	* @return true/false
	* @ejb.interface-method view-type="remote"
	* @ejb.transaction type="NotSupported"
	*/
	public boolean esTrabajadorCesado(String usuario) {
		//MaestroPersonalDAO maestroPersonalDAO = new MaestroPersonalDAO(dbpool);
		//maestroPersonalDAO.findEstado(filtro);
		//verificar si es cesado
		pe.gob.sunat.rrhh.dao.T02DAO persDAO = new pe.gob.sunat.rrhh.dao.T02DAO(sl.getDataSource("java:comp/env/jdbc/dcsp"));

		Map filtro = new HashMap();
		filtro.put("codPers", usuario);
		filtro.put("fecCompara", (new FechaBean()).getTimestamp());
		return persDAO.esCesado(filtro);

	}

	
	 
	//AGONZALESF -PAS20191U230200011 - solicitud de reintegro
	/**
	 * Metodo  para verificar el cese de trabajador en siga
	 * @param usuario
	 * @return
	 */
	private boolean esColaboradorCesado(String codPers) { 
		
		//obtener colaborador de tabla maestro personal
		MaestroPersonalDAO maestroPersonalDAO = new MaestroPersonalDAO(sl.getDataSource("jdbc/dcsig"));
		Map colaborador =  maestroPersonalDAO.findEstadoByCodPers(codPers);
		Date fecCese  =(Date)colaborador.get("fech_cese_per");
		String estado = (String)colaborador.get("esta_trab_per");
		
		
		Calendar hoy =  Calendar.getInstance(); 
		Calendar cFecCese = Calendar.getInstance();
		if(fecCese!=null){
			cFecCese.setTime(fecCese);	
		}
		log.debug("estado: " + estado);
		log.debug("fecCese: " + fecCese);
		log.debug("cese1: " + hoy.after(cFecCese));
		log.debug("cese2: " + Constantes.INACTIVO.equals(estado));
		return  hoy.after(cFecCese) && Constantes.INACTIVO.equals(estado);		 

		}

	// PAS20171U230200001 - solicitud de reintegro 
	/**  
	* Metodo para enviar Notificacion  de no reintegro  al trabajador  
	* @param mapa : datos de la solicitud
	* @return Lista mensajes de error
	* @throws java.rmi.RemoteException
	* @ejb.interface-method view-type="remote"
	* @ejb.transaction type="NotSupported"
	*/
	public List enviarMensajeNotificacion(Map mapa) throws IncompleteConversationalState, RemoteException {
		List mensajes = new ArrayList();
		try {

			if (log.isDebugEnabled())log.debug(mapa);
			DataSource ds = sl.getDataSource("java:comp/env/jdbc/dcsp");
			String numSol = (String) mapa.get("numSol");
			String colaborador = (String) mapa.get("codPers");

			T02DAO personalDAO = new T02DAO();
			String nombre = colaborador + " - " + personalDAO.findNombreCompletoByCodPers("java:comp/env/jdbc/dcsp", colaborador);

			pe.gob.sunat.rrhh.dao.T99DAO t99dao = new pe.gob.sunat.rrhh.dao.T99DAO(ds);

			Map filtro = new HashMap();
			filtro.put("t99cod_tab", "R06");
			filtro.put("t99tip_desc", "D");
			filtro.put("t99estado", "1");
			filtro.put("t99codigo", "01");
			Map mapaParam = t99dao.findParamByCodTabCodigo(filtro);
			String correoAsis = (String) mapaParam.get("t99descrip");

			filtro = new HashMap();
			filtro.put("t99cod_tab", "R05");
			filtro.put("t99tip_desc", "D");
			filtro.put("t99estado", "1");
			filtro.put("t99codigo", "04");
			mapaParam = t99dao.findParamByCodTabCodigo(filtro);
			String titulo = Utiles.escapeHtml4((String) mapaParam.get("t99descrip"));
			String asunto = (String) mapaParam.get("t99descrip");

			Map reintegro = (Map) mapa.get("solRein");
			String periodo = reintegro.get("ann_solplan") + " - " + reintegro.get("mes_solplan");

			String texto = Utiles.textoCorreoNotificacionAlerta(titulo, nombre, (String) mapa.get("numSol"), periodo);
			CorreoDAO correoDAO = new CorreoDAO();
			String smtpColaborador  = correoDAO.findCorreoByCodPers("jdbc/dcsp", colaborador);			
			if (smtpColaborador != null && !"".equals(smtpColaborador)) {
				try { 
					if (log.isDebugEnabled())log.debug("asunto" + asunto );
					if (log.isDebugEnabled())log.debug("usuario : " + colaborador + "---" + smtpColaborador); 
				 	Correo correo = new Correo(smtpColaborador, texto);
				 	Direccion remitente  = new Direccion(correoAsis, "RRHH División de Compensaciones - Asistencia ", 0); 
				 	correo.setRemitente(remitente);
				 	correo.setAsunto(asunto); 
					correo.enviarHtml(); 
				} catch (CorreoException ce) {
					if (log.isDebugEnabled())
						log.debug("Error en CorreoException: " + ce.toString());
					if (log.isDebugEnabled())
						log.debug("No se pudo enviar correo al trabajador con registro: " + colaborador);
				} 
			}

		} catch (Exception e) {
			log.error("Error al enviar Notificacion", e);
			throw new RemoteException(e.getMessage());
		}

		return mensajes;

	}

	/**
	 *PAS20171U230200001 - solicitud de reintegro   
	 * Metodo  para obtener lista de documentos aprobados
	 * @param filtro  : datos para la busqueda
	 * @return Mapa con listas de documentos aprobados (papeletas , licencias/vacaciones , solicitudes o solicitudes de subsidio)
	 * @throws RemoteException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public Map listaDocumentosAprobados(HashMap filtro) throws RemoteException {
		DataSource dcsp = sl.getDataSource("jdbc/dcsp");
		Map documentos = new HashMap();
		T8155DAO t8155dao = new T8155DAO(dcsp);
		Map solicitud;
		try {

			if (log.isDebugEnabled())log.debug("listaDocumentosAprobados ->" + filtro);
			solicitud = t8155dao.findSolicitudReintegroByNumSeqRein(filtro.get("num_seqrein").toString());
			int anioConsulta = Integer.parseInt(solicitud.get("ann_solplan").toString()); //2017-01
			int mesConsulta = Integer.parseInt(solicitud.get("mes_solplan").toString());
			documentos.put("solicitud", solicitud);
			if (log.isDebugEnabled())log.debug("solicitud  " + solicitud);

			if (mesConsulta == 1) {
				anioConsulta = anioConsulta - 1;
				mesConsulta = 12;
			} else {
				mesConsulta = mesConsulta - 1;
			}

			if (log.isDebugEnabled())log.debug("Anio y mes ajustados " + anioConsulta + "-" + mesConsulta); //2016-12

			Calendar inicio = Calendar.getInstance();
			inicio.set(anioConsulta, mesConsulta - 1, 1);

			Calendar fin = Calendar.getInstance();
			fin.set(anioConsulta, mesConsulta - 1, 1);
			fin.add(Calendar.MONTH, 1);
			fin.add(Calendar.DATE, -1);

			filtro.put("cod_pers", solicitud.get("cod_pers"));
			filtro.put("periodo", Utiles.intToString(anioConsulta, 4) + Utiles.intToString(mesConsulta, 2));
			filtro.put("ffinicio", Utiles.dateToString(inicio.getTime()));
			filtro.put("ffin", Utiles.dateToString(fin.getTime()));

			documentos = obtenerDocumentos(filtro);
			documentos.put("solicitud", solicitud);



		} catch (SQLException e) {
			log.error("Error al obtener listaDocumentosAprobados", e);
			throw new RemoteException(e.getMessage());
		}

		return documentos;
	}

	// PAS20171U230200001 - solicitud de reintegro 
	/**   
	* Metodo para obtener datos sobre personal incluye tipo de regimen y tipo de planilla 
	* @param dbpool :pool de conexion 
	* @param codPers : codigo del trabajador
	* @return
	* @ejb.interface-method view-type="remote"
	* @ejb.transaction type="NotSupported"
	*/
	public Map obtenerDataTrabajador(String dbpool, String codPers) {
		MaestroPersonalDAO maestroPersonalDAO = new MaestroPersonalDAO(dbpool);
		Map data = maestroPersonalDAO.findByCodReg(codPers);
		return data;
	}

	// PAS20171U230200001 - solicitud de reintegro 
	/**   
	* Metodo para obtener tipos de devolucion (asistencia)
	* @param dbpool : pool de conexion  
	* @return Lista de tipos de devolucion asistencia
	* @ejb.interface-method view-type="remote"
	* @ejb.transaction type="NotSupported"
	*/
	public List obtenerTiposDevolucion(String dbpool) {
		TipoSalidasDAO tSalidasDAO = new TipoSalidasDAO(dbpool);
		List data = tSalidasDAO.findByTipoDevolucionAsistencia();
		return data;
	}

	// PAS20171U230200001 - solicitud de reintegro 
	/**   
	* Metodo para obtener  tipos de devolucion (licencias)
	* @param dbpool:pool de conexion   
	* @return Lista de tipos de devolucion licencias,permisos
	* @ejb.interface-method view-type="remote"
	* @ejb.transaction type="NotSupported"
	*/
	public List obtenerTiposDevolucionLicencias(String dbpool) {
		TipoSalidasDAO tSalidasDAO = new TipoSalidasDAO(dbpool);
		List data = tSalidasDAO.findByTipoDevolucionLicencia();
		return data;
	}

	// PAS20171U230200001 - solicitud de reintegro 
	/**   
	* Metodo para obtener  tipos de devolucion (subsidio)
	* @param dbpool : pool de conexion   
	* @return Lista de tipos de devolucion licencias,permisos
	* @ejb.interface-method view-type="remote"
	* @ejb.transaction type="NotSupported"
	*/
	public List obtenerTiposDevolucionSubsudios(String dbpool) {
		TipoSalidasDAO tSalidasDAO = new TipoSalidasDAO(dbpool);
		List data = tSalidasDAO.findByTipoDevolucionSubsidio();
		return data;
	}

	// PAS20171U230200001 - solicitud de reintegro 
	//AGONZALESF -PAS20191U230200011 - se deja de usar , solo se obtendran las planillas validas 
	/**  
	* Metodo para  verificar que el regimen laboral de la planilla consultada coincida con el regimen de empleado actual
	* @param dbpool :pool de conexion   
	* @return true/false 
	* @throws java.rmi.RemoteException
	 * @ejb.interface-method view-type="remote"
	* @ejb.transaction type="NotSupported" 
	*/
	public boolean verificarRegimenLaboral(Map filtro) throws java.rmi.RemoteException {
		boolean rpta = false;
		try {
			if (log.isDebugEnabled())log.debug("->Ingreso a verificarRegimenLaboral :  " + filtro);
			DataSource dcsig = sl.getDataSource("java:comp/env/jdbc/dcsig");
			HistMetasEmpleaDAO dao = new HistMetasEmpleaDAO(dcsig);

			Map solicitado = dao.findPlanillaIncSubtipos(filtro);
			if (log.isDebugEnabled())log.debug("->planilla solicitada :  " + solicitado);

			String regimenSolicitado = (String) solicitado.get("reg_lab_per");
			String regimenActual = (String) filtro.get("reg_lab_per");
			if (log.isDebugEnabled())log.debug("->Comparar regimen :  " + regimenSolicitado + ".." + regimenActual);
			if (regimenSolicitado != null && regimenActual != null) {
				if (log.isDebugEnabled())log.debug("->Comparar regimen :  " + regimenSolicitado + ".." + regimenActual + ".." + regimenSolicitado.equals(regimenActual));
				rpta = regimenSolicitado.equals(regimenActual);
			}

		} catch (SQLException e) {
			log.error("Error verificar regimen laboral", e);
			throw new RemoteException(e.getMessage());
		}
		return rpta;

	}

	// PAS20171U230200001 - solicitud de reintegro 
	//AGONZALESF -PAS20181U230200067 - solicitud de reintegro, planillas adicionales
	//AGONZALESF -PAS20191U230200011 - se deja de usar , solo se obtendran las planillas validas 
	/**   
	* Metodo para  verifica reingreso de trabajador
	* Valida si la planilla es de una fecha posterior al ingreso 
	* @param filtro :parametros de busqueda     
	* @return  true/false 
	* @throws java.rmi.RemoteException
	* @ejb.interface-method view-type="remote"
	* @ejb.transaction type="NotSupported"
	*/
	public boolean verificarReIngreso(Map filtro) throws java.rmi.RemoteException {
		boolean rpta = true;
		try {
			if (log.isDebugEnabled())log.debug("->Ingreso a verificarReIngreso :  " + filtro);
			DataSource dcsig = sl.getDataSource("java:comp/env/jdbc/dcsig");
			HistMetasEmpleaDAO dao = new HistMetasEmpleaDAO(dcsig);
			String anioSol = (String) filtro.get("anio");
			String mesSol = (String) filtro.get("mes");
			Map planilla = dao.findPlanillaByAnioNumero(filtro);
			String mesAnioPeriodo = (String) planilla.get("ma_evaluado");
			String mesAnioIngreso = (String) planilla.get("ma_ingreso");
			if (log.isDebugEnabled())log.debug("->Comparar anio y mes  :  " + mesAnioPeriodo + ".." + mesAnioIngreso + ".." + mesAnioPeriodo.compareTo(mesAnioIngreso));
			return mesAnioPeriodo.compareTo(mesAnioIngreso) < 0;
			//      201608 -- 201702 -1    ... reingreso , trabajador tiene planilla en el periodo  pero es anterior a fecha de ingreso  y no es valida para reintegro
			//      201702 -- 201702  0      
			//      201703 -- 201702  1      

		} catch (SQLException e) {
			log.error("Error al verificar continuidad del trabajador", e);
			throw new RemoteException(e.getMessage());
		}

	}

	// PAS20171U230200001 - solicitud de reintegro 
	//AGONZALESF -PAS20191U230200011 - se deja de usar , solo se obtendran las planillas validas 
	/**  
	* Metodo para Verificar que el trabajador esta presente en planilla cas o haberes principal solicitado  
	* @param filtro :parametros de busqueda     
	* @return  true/false
	* @throws java.rmi.RemoteException
	* @ejb.interface-method view-type="remote"
	* @ejb.transaction type="NotSupported"
	*/
	public boolean verificarExistePlanilla(Map filtro) throws java.rmi.RemoteException {
		boolean rpta = true;
		try {
			if (log.isDebugEnabled())log.debug("->Ingreso a verificarExistePlanilla :  " + filtro);
			DataSource dcsig = sl.getDataSource("java:comp/env/jdbc/dcsig");
			HistMetasEmpleaDAO dao = new HistMetasEmpleaDAO(dcsig);
			String anioSol = (String) filtro.get("anio");
			String mesSol = (String) filtro.get("mes");
			Map planilla = dao.findPlanillaByAnioNumero(filtro);
			if (log.isDebugEnabled())log.debug("->Planilla :  " + planilla);
			return planilla != null && !planilla.isEmpty();

		} catch (SQLException e) {
			log.error("El trabajador no esta en la planilla solicitada ", e);
			throw new RemoteException(e.getMessage());
		}

	}

	// PAS20171U230200001 - solicitud de reintegro 
	/**   
	* Metodo para Verificar que el regimen del trabajador este configurado 
	* @param filtro :parametros de busqueda     
	* @return  true/false
	* @throws java.rmi.RemoteException
	* @ejb.interface-method view-type="remote"
	* @ejb.transaction type="NotSupported"
	*/
	public boolean verificarRegimenConfigurado(Map filtro) throws java.rmi.RemoteException {
		boolean rpta = false;
		try {
			if (log.isDebugEnabled())log.debug("->Ingreso a verificarRegimenConfigurado :  " + filtro);
			DataSource dcsig = sl.getDataSource("java:comp/env/jdbc/dcsig");

			String tipoMov = filtro.get("tipo").toString();
			String tipoPlan = (String) filtro.get("tipo_plan_tpl");
			String regimenLab = (String) filtro.get("reg_lab_per");

			T01ParametroDAO parametroDAO = new T01ParametroDAO(dcsig);
			Map filtroParametro = new HashMap();

			if (tipoPlan.equals(Constantes.PLANILLA_HABERES) && tipoMov.equals(Constantes.MOV_REINTEGRO_POR_DESCUENTO_ASISTENCIA)) {
				filtroParametro.put("cod_parametro", "1026");
				filtroParametro.put("cod_modulo", "SIGA");
				filtroParametro.put("cod_tipo", "D");
				filtroParametro.put("cod_argumento", "REGLAB_DEVOASIS");
			}
			if (tipoPlan.equals(Constantes.PLANILLA_HABERES) && tipoMov.equals(Constantes.MOV_REINTEGRO_POR_DESCUENTO_SUBSIDIO)) {
				filtroParametro.put("cod_parametro", "1026");
				filtroParametro.put("cod_modulo", "SIGA");
				filtroParametro.put("cod_tipo", "D");
				filtroParametro.put("cod_argumento", "REGLAB_DEVOLICE");
			}

			if (tipoPlan.equals(Constantes.PLANILLA_CAS) && tipoMov.equals(Constantes.MOV_REINTEGRO_POR_DESCUENTO_ASISTENCIA)) {
				filtroParametro.put("cod_parametro", "1045");
				filtroParametro.put("cod_modulo", "SIGA");
				filtroParametro.put("cod_tipo", "D");
				filtroParametro.put("cod_argumento", "01");
			}
			if (tipoPlan.equals(Constantes.PLANILLA_CAS) && tipoMov.equals(Constantes.MOV_REINTEGRO_POR_DESCUENTO_SUBSIDIO)) {
				filtroParametro.put("cod_parametro", "1045");
				filtroParametro.put("cod_modulo", "SIGA");
				filtroParametro.put("cod_tipo", "D");
				filtroParametro.put("cod_argumento", "02");
			}

			Map res = parametroDAO.findByCodigoParametro(filtroParametro);
			String cadena = res.get("desc_corta").toString();

			if (log.isDebugEnabled())log.debug("->regimen : " + regimenLab + " cadena:  " + cadena);
			StringTokenizer tokens = new StringTokenizer(cadena, ",");
			int nDatos = tokens.countTokens();
			int i = 0;

			while (tokens.hasMoreTokens()) {
				String str = tokens.nextToken();
				log.debug("->regimen : " + regimenLab + " str:  " + str);
				if (str.equals(regimenLab)) {
					rpta = true;
					break;
				}

			}

		} catch (Exception e) {
			log.error("Error al verificar continuidad del trabajador", e);
			throw new RemoteException(e.getMessage());
		}
		return rpta;

	}

	// PAS20171U230200001 - solicitud de reintegro 
	/**  
	* Metodo para carga el detalle de solicitudes de reintegro
	* @param concepto : datos de concepto
	* @return : lista de detalles
	* @throws java.rmi.RemoteException
	* @ejb.interface-method view-type="remote"
	* @ejb.transaction type="NotSupported"
	*/
	public List cargarDetSolReintegro(Map concepto) throws java.rmi.RemoteException {
		try {
		DataSource ds = sl.getDataSource("java:comp/env/jdbc/dcsp");
		DataSource dsSig = sl.getDataSource("java:comp/env/jdbc/dcsig");

		T8154DAO detSolDAO = new T8154DAO(ds);
			T8155DAO solDao = new T8155DAO(ds);
			
			T5737DAO t5737dao = new T5737DAO(dsSig);
			T5738DAO t5738dao = new T5738DAO(dsSig);
			
			
		Map params = new HashMap();
		params.put("num_seqrein", concepto.get("num_seqrein"));
		params.put("num_seqcon", concepto.get("num_seqcon"));
		List lstDetSolReintegro = detSolDAO.findDetSolicitudesByPK(params);
			
			Map solicitud = solDao.findSolicitudReintegroByNumSeqRein(concepto.get("num_seqrein").toString());
			String annSol =solicitud.get("ann_solplan").toString();
			String mesSol =solicitud.get("mes_solplan").toString();
			String tipPla =solicitud.get("cod_planorig").toString();
			String sTipoPla =solicitud.get("cod_splanorig").toString(); 
			String codOrigMov = concepto.get("cod_movimiento").toString(); 
		String codPers = (String)solicitud.get("cod_pers") ;
		String tipoLic = (concepto.get("cod_tiplicencia") != null ? (String) concepto.get("cod_tiplicencia") : "").trim();
			List fol= new ArrayList(); 
			for (int i = 0; i < lstDetSolReintegro.size(); i++) {
				Map deta = (Map) lstDetSolReintegro.get(i);
				if (!"".equals(tipoLic)) {
					//licencia
					fol.add(deta.get("cod_licencia"));
										
				}else{
					//asistencia
					//fec_asisorig   concepto->cod_origmov
					fol.add(deta.get("fec_asisorig"));
				}
			}

		if (lstDetSolReintegro != null && lstDetSolReintegro.size() > 0) {
			for (int i = 0; i < lstDetSolReintegro.size(); i++) {
				Map deta = (Map) lstDetSolReintegro.get(i);
				if (!"".equals(tipoLic)) {
					//Obtenemos las fechas de la licencia
					LicenciasDAO licenciaDAO = new LicenciasDAO(dsSig);
					if (deta.get("cod_licencia") != null && "0" != deta.get("cod_licencia").toString()) {

						Map licencia = licenciaDAO.obtenerFechasLicencia(deta.get("cod_licencia").toString());

						if (licencia != null && licencia.size() > 0) {
							FechaBean fIni = new FechaBean((Timestamp) licencia.get("fech_inic_lic"));
							FechaBean fFin = new FechaBean((Timestamp) licencia.get("fech_fina_lic"));
							deta.put("fech_inic_lic", licencia.get("fech_inic_lic"));
							deta.put("fech_fina_lic", licencia.get("fec_fina_lic"));
							deta.put("fech_inic_lic_desc", fIni.getFormatDate("dd/MM/yyyy"));
							deta.put("fech_fina_lic_desc", fFin.getFormatDate("dd/MM/yyyy"));
							
							Map filtro = new HashMap();
							filtro.put("cod_pers",  codPers);
							filtro.put("ffinicio",   fIni.getFormatDate("dd/MM/yyyy"));
							filtro.put("ffin", fFin.getFormatDate("dd/MM/yyyy"));				
							if(log.isDebugEnabled()) log.debug("Filtro ->" +filtro); 
							Boolean  existeSustento = new Boolean( existeMovSustentoReintegro(filtro));
							deta.put("existeSustento",existeSustento);  
						}
					}
				}else{
						FechaBean fIni = new FechaBean((Timestamp) deta.get("fec_asisorig")); 
						Map filtro = new HashMap();
						filtro.put("cod_pers",  codPers);
						filtro.put("ffinicio",   fIni.getFormatDate("dd/MM/yyyy"));
						filtro.put("ffin", fIni.getFormatDate("dd/MM/yyyy"));				
						if(log.isDebugEnabled()) log.debug("Filtro ->" +filtro); 
						Boolean  existeSustento = new Boolean( existeMovSustentoReintegro(filtro));
						deta.put("existeSustento",existeSustento);  
					 
				}
			}
		}

		return lstDetSolReintegro;
	 
		} catch (SQLException e) {
			log.error("Error buscar detalle cargarDetSolReintegro", e);
			throw new RemoteException(e.getMessage());
		}
	}

	// PAS20171U230200033 - solicitud de reintegro 
	/**  
	* Metodo para actualizar el detalle de solicitudes de reintegro
	* @param concepto : datos de concepto
	* @return : lista de detalles
	* @throws java.rmi.RemoteException
	* @ejb.interface-method view-type="remote"
	* @ejb.transaction type="NotSupported"
	*/
	public boolean actualizarDetSolReintegro(Map concepto) throws java.rmi.RemoteException {
		DataSource dcsp = sl.getDataSource("java:comp/env/jdbc/dcsp");
		DataSource dgsp = sl.getDataSource("java:comp/env/jdbc/dgsp");
		DataSource dcsig = sl.getDataSource("java:comp/env/jdbc/dcsig");
	 
		T8153DAO soliReinConc = new T8153DAO(dgsp);
		TipoSalidasDAO salidas = new TipoSalidasDAO(dcsig);
		T8154DAO soliReinDeta = new T8154DAO(dgsp);
		
		FechaBean factual = new FechaBean();	
		Map tipos =  salidas.getAll();
		if (concepto.get("ind_del") == null || ((String) concepto.get("ind_del")).equals("0")) {

			concepto.put("cod_usumodif", concepto.get("cod_usumodif"));
			concepto.put("fec_modif", factual.getTimestamp()); 
			soliReinConc.updateDevolucionConcepto(concepto);  
			List lstDetSol = (ArrayList) concepto.get("lstDetSolReintegro"); 
			if (lstDetSol != null && lstDetSol.size() > 0) {
				for (int j = 0; j < lstDetSol.size(); j++) {
					Map detalle = (HashMap) lstDetSol.get(j);
					detalle.put("cod_usumodif", concepto.get("cod_usumodif"));
					detalle.put("fec_modif", factual.getTimestamp());
					detalle.put("num_seqrein", concepto.get("num_seqrein"));
					detalle.put("num_seqcon", concepto.get("num_seqcon"));
					String tipDev =(String) detalle.get("cod_tipdev");
					detalle.put("des_tipdev", tipos.get(tipDev)); 
					soliReinDeta.updateDevolucionDetalles((HashMap) detalle); //actualizar detalles
				}
			}
		}
		return true;
		
		
	}
	 
	 
	 
	 
	// PAS20171U230200001 - solicitud de reintegro 
	/**  
	* Carga planilla devolucion
	* @param params datos de busqueda
	* @return : datos de planilla
	* @throws java.rmi.RemoteException
	* @ejb.interface-method view-type="remote"
	* @ejb.transaction type="NotSupported"
	*/
	public Map obtenerPlanillaDevolucion(Map params) throws java.rmi.RemoteException {
		Map res = null;
		DataSource dsSig = sl.getDataSource("java:comp/env/jdbc/dcsig");

		HistMetasEmpleaDAO hEmpDAO = new HistMetasEmpleaDAO(dsSig);
		res = hEmpDAO.findUltPlanPeriodo(params);
		return res;
	}
			
	// PAS20171U230200001 - solicitud de reintegro 	
	/**
	 * Obtener los datos de la devolucion ya realizadas (en tablas de devolucion) 
	 * y los carga a la lista de solicitudes de devolucion
	 * @param sol
	 * @return : Lista de devoluciones
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public List obtenerDevoluciones(Map solicitud ,Map concepto,List listDevol){
		DataSource dsSig = sl.getDataSource("java:comp/env/jdbc/dcsig");
	 
		MaestroPersonalDAO maestroPersonalDAO = new MaestroPersonalDAO(dsSig);
		Map filtro = new HashMap();
		if(log.isDebugEnabled())log.debug(solicitud);
		if(log.isDebugEnabled())log.debug(listDevol);
		 
 
		String codTipLicencia = Utiles.esNuloesVacio(concepto.get("cod_tiplicencia").toString()) ? "" : concepto.get("cod_tiplicencia").toString(); //<blanco>, L,P,S			
		if (codTipLicencia.equals("")) {  
			T5737DAO t5737dao = new T5737DAO(dsSig);
			ConsolAsistDiaDAO asistDiaDAO = new ConsolAsistDiaDAO(dsSig);
			
			if (log.isDebugEnabled())log.debug("Verficar asistencia");
			String codPers= solicitud.get("cod_pers").toString();
			Map maestroPersonal= maestroPersonalDAO.findByCodReg(codPers);
			String codiEmplPer = maestroPersonal.get("codi_empl_per").toString();
			filtro.put("cod_empl_per", codiEmplPer);
			filtro.put("cod_tip_planilla", solicitud.get("cod_plandev"));
			filtro.put("cod_sub_planilla", solicitud.get("cod_splandev"));
			filtro.put("ann_devolucion",solicitud.get("ann_plandev")) ; //PAS20171U230200028
			filtro.put("mes_devolucion",solicitud.get("mes_plandev")); //PAS20171U230200028
			for (int i = 0; i < listDevol.size(); i++) { 
				Map detalle = (Map) listDevol.get(i); 
				filtro.put("fec_asis_origen",  detalle.get("fec_asisorig"));
				filtro.put("cod_mov_origen", detalle.get("cod_origmov").toString()); 
				if(log.isDebugEnabled()) log.debug("Filtro ->" +filtro);
				Map devolucionPeriodo = t5737dao.findDevueltoAsistenciaPeriodo(filtro);				
				Map original =  asistDiaDAO.findDetalleOriginal(filtro);
				((Map) listDevol.get(i)).put("devueltoPeriodo",devolucionPeriodo!=null ? devolucionPeriodo.get("devolucion"):"0"); 
				((Map) listDevol.get(i)).put("original", original.get("original"));
			}
		 
			
		}else{
			
			T5738DAO t5738dao = new T5738DAO(dsSig);			
			LicenciasDAO licenciasDAO = new LicenciasDAO(dsSig);
			
			if (log.isDebugEnabled())log.debug("Verificar licencias/permiso/subsidio");
			String codPers= solicitud.get("cod_pers").toString(); //PAS20171U230200028
			Map maestroPersonal= maestroPersonalDAO.findByCodReg(codPers); //PAS20171U230200028
			String codiEmplPer = maestroPersonal.get("codi_empl_per").toString(); //PAS20171U230200028
			filtro.put("cod_tip_planilla", solicitud.get("cod_plandev")); //PAS20171U230200028
			filtro.put("cod_sub_planilla", solicitud.get("cod_splandev")); //PAS20171U230200028
			filtro.put("ann_devolucion",solicitud.get("ann_plandev")) ; //PAS20171U230200028
			filtro.put("mes_devolucion",solicitud.get("mes_plandev")); //PAS20171U230200028
			filtro.put("cod_empl_per", codiEmplPer);	 //PAS20171U230200028		 

			
			for (int i = 0; i < listDevol.size(); i++) { 
				Map detalle = (Map) listDevol.get(i);
				String codLicencia = detalle.get("cod_licencia").toString();
				filtro.put("cod_licencia", codLicencia);
				filtro.put("ann_lic_calc",solicitud.get("ann_solplan")) ;
				filtro.put("mes_lic_calc",solicitud.get("mes_solplan"));
				if(log.isDebugEnabled()) log.debug("Filtro ->" +filtro);
				Map devolucionPeriodo = t5738dao.findDevueltoLicenciaEmpleado(filtro);  
				Map original = licenciasDAO.findOriginalLicencia(filtro);
				((Map) listDevol.get(i)).put("devueltoPeriodo", devolucionPeriodo!=null ?devolucionPeriodo.get("devolucion"):"0"); 
				((Map) listDevol.get(i)).put("original", original.get("original"));
			}
		} 
		return listDevol;
	}
	
	 
	// PAS20171U230200001 - solicitud de reintegro 
	/**
	 * Funcion para validar data previo a aprobar
	 * @param solicitud
	 * @return Lista de mensajes de error
	 */
	private List validarDataSolicitud(Map parametros) {
		if (log.isDebugEnabled())
			log.debug("validarDataSolicitud -->" + parametros);
		List mensajes = new ArrayList();

		DataSource dsSig = sl.getDataSource("java:comp/env/jdbc/dcsig");
		TPeriodosDAO periodosDAO = new TPeriodosDAO(dsSig);
		MaestroPersonalDAO dao = new MaestroPersonalDAO(dsSig);

		Map paramSolicitud = (HashMap) parametros.get("paramSolicitud");
		Map solicitud = (Map) paramSolicitud.get("solRein");

		List fechasInvalidas = new ArrayList();
		List yaProcesado = new ArrayList();
		List noPuedeRegistrarse = new ArrayList(); //PAS20171U230200028

		Map filtro = new HashMap();
		filtro.put("tipo_plan_tpl", solicitud.get("cod_plandev"));
		filtro.put("subt_plan_tpl", solicitud.get("cod_splandev"));
		filtro.put("ano_peri_tpe", solicitud.get("ann_plandev"));
		filtro.put("nume_peri_tpe", solicitud.get("mes_plandev"));

		String codPers = solicitud.get("cod_pers").toString(); //PAS20171U230200028
		Map maestroPersonal = dao.findByCodReg(codPers); //PAS20171U230200028
		String codiEmplPer = maestroPersonal.get("codi_empl_per").toString();  //PAS20171U230200028

		//PAS20171U230200033
		// AGONZALESF -PAS20191U230200011 - solicitud de reintegro , se deja de  usar
		/*
		 * Map estadoPlanilla =periodosDAO.findEstadoByTPeriodo(filtro); String
		 * estado = estadoPlanilla.get("esta_plan_tpe").toString();
		 * 
		 * if (!("1".equals(estado))) { StringBuffer mensajeError = new
		 * StringBuffer(
		 * "Los reintegros se encuentran en proceso de calculo de planilla y ya no es posible aprobar esta solicitud "
		 * ); mensajes.add(mensajeError.toString()); return mensajes;
		 * 
		 * }
		 */

		//PAS20171U230200033 - validacion de bloqueo de asistencia
		// AGONZALESF -PAS20191U230200011 - solicitud de reintegro , se deja de usar
		/*
		 * if(estadoPlanilla.get("ind_bloqueo")!=null){ String bloqueo =
		 * estadoPlanilla.get("ind_bloqueo").toString(); if
		 * ("1".equals(bloqueo)) { if(log.isDebugEnabled())
		 * log.debug("Indicador de bloqueo esta activo"); StringBuffer
		 * mensajeError = new
		 * StringBuffer("Los servicios de la planilla ya fueron cerrados. ");
		 * mensajes.add(mensajeError.toString()); return mensajes;
		 * 
		 * } }
		 */

			filtro = new HashMap();
			List conceptos = (List) solicitud.get("conceptos");
		BigDecimal cero = new BigDecimal(0);
			for (int i = 0; i < conceptos.size(); i++) {
				Map concepto = (Map) conceptos.get(i);
				String codTipLicencia = Utiles.esNuloesVacio(concepto.get("cod_tiplicencia").toString()) ? "" : concepto.get("cod_tiplicencia").toString(); //<blanco>, L,P,S	
				List listDevol = (List) concepto.get("lstDetSolReintegro");

				//asistencia
				if (codTipLicencia.equals("")) {
					T5737DAO t5737dao = new T5737DAO(dsSig);
					ConsolAsistDiaDAO asistDiaDAO = new ConsolAsistDiaDAO(dsSig);
					if (log.isDebugEnabled()) log.debug("Verificar asistencia");
					
					filtro.put("cod_empl_per", codiEmplPer);
					filtro.put("cod_tip_planilla", solicitud.get("cod_plandev"));
					filtro.put("cod_sub_planilla", solicitud.get("cod_splandev"));
					filtro.put("ann_devolucion", solicitud.get("ann_plandev")); //PAS20171U230200028
					filtro.put("mes_devolucion", solicitud.get("mes_plandev")); //PAS20171U230200028
					
				//PAS20171U230200033 solo validar si la cantidad de minutos del concepto es mayor a 0   cnt_minapro>0 
				int aprobadoPorConcepto = Integer.parseInt(concepto.get("cnt_minapro").toString());
				if(aprobadoPorConcepto>0){  
					if (!concepto.containsKey("mtoUnitario")) {
						mensajes.add("Revisar el Monto : "	+ concepto.get("cod_concepto") + ":blanco");
						return mensajes;
					}  

						BigDecimal monto = (BigDecimal)concepto.get("mtoUnitario");	   
						if(monto.compareTo(cero)!=1){ 
							mensajes.add("Revisar el Monto : " +concepto.get("cod_concepto") ); 
						return mensajes;
					}


					for (int j = 0; listDevol!=null&&j < listDevol.size(); j++) {
						Map detalle = (Map) listDevol.get(j);
						filtro.put("fec_asis_origen", detalle.get("fec_asisorig"));
						filtro.put("cod_mov_origen", detalle.get("cod_origmov").toString());

						Map devolucionTotal = t5737dao.findDevueltoAsistencia(filtro);
						Map original = asistDiaDAO.findDetalleOriginal(filtro);
						Map devolucion = t5737dao.findDatosDevolucion(filtro);   //PAS20171U230200028

						int devueltoTotal = Integer.parseInt(devolucionTotal != null ? devolucionTotal.get("devolucion").toString() : "0");
						int valorOrigen = Integer.parseInt(original.get("original").toString());
						String cantidad = Utiles.esNuloesVacio(detalle.get("cnt_mindev").toString()) ? "0" : detalle.get("cnt_mindev").toString();
						int solicitado = Integer.parseInt(cantidad);		//PAS20171U230200028 solo se valida los detalles mayores a 0  
						if(solicitado>0){ 
							if(log.isDebugEnabled()) log.debug("Devolucion : " + devolucion);
							if(log.isDebugEnabled()) log.debug("Solicitud : " + solicitud);
							if(log.isDebugEnabled()) log.debug("Detalle : " + detalle );
						if (valorOrigen < devueltoTotal + solicitado) {
							fechasInvalidas.add(devueltoTotal + " minuto(s)  para fecha " + Utiles.dateToString((Date) detalle.get("fec_asisorig")));
						}
							// AGONZALESF -PAS20191U230200011 - solicitud de reintegro
							/*if (devolucion != null) {
							String indCierre =  devolucion.get("ind_cierre").toString() ;
						if (!indCierre.equals("0")) {
							yaProcesado.add(Utiles.dateToString((Date) detalle.get("fec_asisorig")));
						}
							}*/
					}

					}
				}

					//licencias/subsidios l,p,s
				} else {

					T5738DAO t5738dao = new T5738DAO(dsSig);
					LicenciasDAO licenciasDAO = new LicenciasDAO(dsSig);
				if (log.isDebugEnabled()) log.debug("Verificar licencias/permiso/subsidio");

				// //PAS20171U230200033 solo validar si la cantidad de minutos
				// del concepto es mayor a 0 cnt_diaapro>0
				int aprobadoPorConcepto = Integer.parseInt(concepto.get("cnt_diaapro").toString());
				if(aprobadoPorConcepto>0){  
					if (!concepto.containsKey("mtoUnitario")) {
						mensajes.add("Revisar el Monto : " 	+ concepto.get("cod_concepto") + ":blanco");
						return mensajes;
						
					} 
					
						BigDecimal monto = (BigDecimal)concepto.get("mtoUnitario");	   
						if(monto.compareTo(cero)!=1){ 
							mensajes.add("Revisar el Monto : " +concepto.get("cod_concepto") ); 
						}


					for (int j = 0; listDevol!=null&&j < listDevol.size(); j++) {
						Map detalle = (Map) listDevol.get(j);
						String codLicencia = detalle.get("cod_licencia").toString();
						filtro.put("cod_licencia", codLicencia);
						filtro.put("ann_lic_calc", solicitud.get("ann_solplan"));
						filtro.put("mes_lic_calc", solicitud.get("mes_solplan"));
						filtro.put("cod_empl_per", codiEmplPer);  //PAS20171U230200028
						filtro.put("cod_tip_planilla", solicitud.get("cod_plandev")); //PAS20171U230200028
						filtro.put("cod_sub_planilla", solicitud.get("cod_splandev")); //PAS20171U230200028
						filtro.put("ann_devolucion", solicitud.get("ann_plandev")); //PAS20171U230200028
						filtro.put("mes_devolucion", solicitud.get("mes_plandev")); //PAS20171U230200028
						
						Map devolucion = t5738dao.findDatosDevolucion(filtro);

						Map original = licenciasDAO.findOriginalLicencia(filtro);
						Map devolucionTotal = t5738dao.findDevueltoLicencia(filtro);

						int devueltoTotal = Integer.parseInt(devolucionTotal != null ? devolucionTotal.get("devolucion").toString() : "0");
						int valorOrigen = Integer.parseInt(original.get("original").toString());
						String cantidad = Utiles.esNuloesVacio(detalle.get("cnt_diadev").toString()) ? "0" : detalle.get("cnt_diadev").toString();
						int solicitado = Integer.parseInt(cantidad);  //PAS20171U230200028 solo se valida los detalles mayores a 0
						if(solicitado>0){   
							
							if (devolucion != null ) { //PAS20171U230200028 existe la devolucion
								if(log.isDebugEnabled()) log.debug("Devolucion : " + devolucion);
								if(log.isDebugEnabled()) log.debug("Solicitud : " + solicitud);
								if(log.isDebugEnabled()) log.debug("Detalle : " + detalle );
						

								String indCierre = devolucion.get("ind_cierre").toString() ;
								String annLic =  devolucion.get("ann_lic_calc").toString() ;
								String mesLic =  devolucion.get("mes_lic_calc").toString() ;
								
								if (annLic.equals(solicitud.get("ann_solplan"))&&mesLic.equals(solicitud.get("mes_solplan"))) { //  devolucion del mismo periodo a la solicitud?
								    //  PAS20171U230200028 si es del mismo periodo  calculado
									// AGONZALESF -PAS20191U230200011 - solicitud de reintegro
									/*if (!indCierre.equals("0")) {
										Map fechas = licenciasDAO.obtenerFechasLicencia(codLicencia);
										yaProcesado	.add(Utiles.dateToString((Date) fechas.get("fech_inic_lic"))+ "-"+ Utiles.dateToString((Date) fechas.get("fech_fina_lic")));
									}*/
						if (valorOrigen < devueltoTotal + solicitado) {
							Map fechas = licenciasDAO.obtenerFechasLicencia(codLicencia);
							fechasInvalidas.add(devueltoTotal + " dia(s) para fecha " + Utiles.dateToString((Date) fechas.get("fech_inic_lic")) + "-"
									+ Utiles.dateToString((Date) fechas.get("fech_fina_lic")));
						}
								}else{
								    //  PAS20171U230200028 no es del mismo periodo calculado no puede ser registrada
								Map fechas = licenciasDAO.obtenerFechasLicencia(codLicencia);
								noPuedeRegistrarse.add(Utiles.dateToString((Date) fechas.get("fech_inic_lic")) + "-"
										+ Utiles.dateToString((Date) fechas.get("fech_fina_lic"))
										+ " periodo " + annLic + "-" + mesLic									
										);
						}
					}
				}
					}
			}
			}

			}
			StringBuffer mensajeError = new StringBuffer();
			if (fechasInvalidas.size() > 0) {
				 mensajeError = new StringBuffer("En el modulo SIGA - Planilla se ha registrado el reintegro el cual corresponde a ");
				for (int i = 0; i < fechasInvalidas.size(); i++) {
					mensajeError.append(fechasInvalidas.get(i));
					mensajeError.append(",");
				}
				mensajeError.append(" y no hay saldo a devolver, por favor verificar");
				mensajes.add(mensajeError.toString());
			}
			if (noPuedeRegistrarse.size() > 0) { //PAS20171U230200028
				 mensajeError = new StringBuffer("En el modulo SIGA - Planilla se ha registrado el reintegro el cual corresponde a ");
				for (int i = 0; i < noPuedeRegistrarse.size(); i++) {
					mensajeError.append(noPuedeRegistrarse.get(i));
					mensajeError.append(",");
				}
				mensajeError.append(" por favor verificar");
				mensajes.add(mensajeError.toString());
			}
		
		/*if (yaProcesado.size() > 0) {
				 mensajeError = new StringBuffer("Los reintegros se encuentran en proceso de calculo de planilla y ya no es posible aprobar esta solicitud");				 
				mensajes.add(mensajeError.toString());
		}*/

		return mensajes;

	}

	
	
	// PAS20171U230200001 - solicitud de reintegro 
	/**
	 * Funcion para obtener el monto unitario
	 * @param concepto datos del concepto
	 * @return monto unitario 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public BigDecimal obtenerMontoUnitario(Map solicitud,Map concepto) { 
		DataSource dsSig = sl.getDataSource("java:comp/env/jdbc/dcsig"); 
		MaestroPersonalDAO maestroPersonalDAO = new MaestroPersonalDAO(dsSig);
		String codPers= solicitud.get("cod_pers").toString();
		Map maestroPersonal= maestroPersonalDAO.findByCodReg(codPers);
		String codiEmplPer = maestroPersonal.get("codi_empl_per").toString();
		
		BigDecimal monto = new BigDecimal("0");
		String codTipLicencia = Utiles.esNuloesVacio(concepto.get("cod_tiplicencia").toString()) ? "" : concepto.get("cod_tiplicencia").toString(); //<blanco>, L,P,S
				
		if (codTipLicencia.equals("")) {  
			T5737DAO t5737dao = new T5737DAO(dsSig);  
			 Map filtro = new HashMap();
			 filtro.put("pc_tipo", solicitud.get("cod_planorig"));
			 filtro.put("pc_subtipo", solicitud.get("cod_splanorig"));
			 filtro.put("pc_anioorig", solicitud.get("ann_solplan"));
			 filtro.put("pc_mesorig", solicitud.get("mes_solplan"));
			 filtro.put("pc_empl", codiEmplPer);
			 filtro.put("pc_con_dscto", concepto.get("cod_concepto"));
			 filtro.put("pc_cod_origmov", concepto.get("cod_movimiento")); 
			 monto = new BigDecimal( t5737dao.obtenerMontoUnitario(filtro).get("monto").toString());
			
		}else{			
			T5738DAO t5738dao = new T5738DAO(dsSig);
			Map filtro = new HashMap();
			 filtro.put("pc_tipo", solicitud.get("cod_planorig"));
			 filtro.put("pc_subtipo", solicitud.get("cod_splanorig"));
			 filtro.put("pc_ann_lic_calc", solicitud.get("ann_solplan"));
			 filtro.put("pc_mes_lic_calc", solicitud.get("mes_solplan"));
			 filtro.put("pc_empl", codiEmplPer);
			 filtro.put("pc_con_dscto", concepto.get("cod_concepto"));
			 filtro.put("pc_cod_origmov", concepto.get("cod_movimiento")); 
			 filtro.put("pc_cod_tiplic", concepto.get("cod_tiplicencia")); 
			 monto = new BigDecimal(  t5738dao.obtenerMontoUnitario(filtro).get("monto").toString());
		}
		return monto;
	}
	
	
	private HashMap cargarDetallesPreProceso(HashMap solicitud) throws RemoteException {
		log.isDebugEnabled(); log.debug("Antes--->"+ solicitud);
		Map solrein = (Map) solicitud.get("solRein");
		List conceptos = (List)solrein.get("conceptos") ;
		for (int i = 0; i < conceptos.size(); i++) {
			Map concepto = (Map)conceptos.get(i);
			List lstDetSolReintegro = cargarDetSolReintegro(concepto);
			lstDetSolReintegro= obtenerDevoluciones (solrein, concepto,lstDetSolReintegro);
			BigDecimal montoUnitario =  obtenerMontoUnitario(solrein, concepto);
			concepto.put("mtoUnitario", montoUnitario);
			String codTipLicencia = Utiles.esNuloesVacio(concepto.get("cod_tiplicencia").toString()) ? "" : concepto.get("cod_tiplicencia").toString(); //<blanco>, L,P,S
			if(codTipLicencia.equals("")){
				concepto.put("mtoUnitarioDesc", montoUnitario.setScale(4, BigDecimal.ROUND_HALF_UP));	
			}
			else{
				concepto.put("mtoUnitarioDesc", montoUnitario.setScale(2, BigDecimal.ROUND_HALF_UP));
			}
			concepto.put("lstDetSolReintegro", lstDetSolReintegro);
		}
		solrein.put("conceptos", conceptos);
		solicitud.put("solRein", solrein);
		log.isDebugEnabled(); log.debug("Despues--->"+ solicitud);
		return solicitud;
		
	}

	// PAS20181U230200023 - licencia por enfermedad 
	/**
	 * Funcion auxiliar para registro de datos de licencia medica
	 * @param annoActual
	 * @param integer
	 * @param mapa
	 * @return
	 */
	private ArrayList registrarSolMedica(String anio, Integer numero,
			HashMap mapa) {
		
 
		DataSource dcsp = ServiceLocator.getInstance().getDataSource("jdbc/dcsp");
		DataSource dgsp = ServiceLocator.getInstance().getDataSource("jdbc/dgsp");
		ArrayList mensajes = new ArrayList();
		T1277DAO solicitudDAO = new T1277DAO();
		T9388DAO txxxxDAO = new T9388DAO(dgsp); 
		
		int nroArchivos = 0; 
		try {
			
			if (log.isDebugEnabled())
				log.debug("Inicio-- registrarSolMedica   mapa->" + mapa);
			Map solicitud = solicitudDAO.findSolicitudByAnioByNro(dcsp, anio, numero);
			if (mapa.containsKey("numArchivo")) {
				int numArchivo =  Integer.parseInt(mapa.get("numArchivo").toString());
				ArchivoFacadeHome facadeHome = (ArchivoFacadeHome) ServiceLocator.getInstance().getRemoteHome(ArchivoFacadeHome.JNDI_NAME,
						ArchivoFacadeHome.class);
				ArchivoFacadeRemote facadeRemote = facadeHome.create();
				
				ArchivoTmpFacadeHome facadeTmpHome = (ArchivoTmpFacadeHome) ServiceLocator.getInstance().getRemoteHome(ArchivoTmpFacadeHome.JNDI_NAME,
						ArchivoTmpFacadeHome.class);
				ArchivoTmpFacadeRemote facadeTmpRemote = facadeTmpHome.create();
				
				Map filtro = new HashMap();
				
				filtro.put("cod_estado", Constantes.SOL_REINTEGRO_ESTADO_ACTIVO);
				filtro.put("cod_usucrea", solicitud.get("cod_pers"));
				filtro.put("num_archivo", numArchivo +"");
				filtro.put("num_seqdoc", "0");
				filtro.put("ind_del", Constantes.EST_ARC_TEMP_OK);
				facadeTmpRemote.cambiarEstado("jdbc/dgsp", filtro, Constantes.EST_ARC_TEMP_COPIAR_PERMANENTE);
				 
				Map archivo = new HashMap();
				archivo.put("num_archivo", numArchivo+"");
				archivo.put("cod_estado", Constantes.SOL_REINTEGRO_ESTADO_ACTIVO);
				archivo.put("cod_usucrea", solicitud.get("cod_pers"));
				archivo.put("ind_del", Constantes.EST_ARC_TEMP_COPIAR_PERMANENTE);
				nroArchivos = facadeRemote.registrarEnPermanente("jdbc/dcsp", archivo);  //todo
				archivo = new HashMap();
				archivo.put("num_archivo", numArchivo+"");
				facadeTmpRemote.eliminarArchivosTemporales("jdbc/dgsp", archivo);
				
			}
			
		
	
			String numSolicitud = anio + Utiles.intToString(numero.intValue(), 7);
			if (log.isDebugEnabled())
				log.debug("num_seqrein (11digitos)-> ann_solicitud (4digitos) ||  num_solicitud (7digitos) : " + numSolicitud);

			Map solMedica = new HashMap();
			solMedica.put("num_solicitud", numSolicitud);
			solMedica.put("ann_solicitud", solicitud.get("anno"));
			solMedica.put("num_sol", solicitud.get("numero"));
			solMedica.put("cod_uuoo", solicitud.get("u_organ"));
			solMedica.put("cod_pers", solicitud.get("cod_pers"));
			solMedica.put("cod_mov", solicitud.get("licencia"));
			solMedica.put("fec_solicitud", solicitud.get("ffinicio"));
			solMedica.put("cod_cmp", mapa.get("codCMP"));  
			if(nroArchivos>0){
				solMedica.put("num_archivo", mapa.get("numArchivo"));   
			} else{
				solMedica.put("num_archivo", 0+"");   
			} 
		
			solMedica.put("cod_cie10", mapa.get("codDiagnostico"));  
			solMedica.put("ind_del", Constantes.INACTIVO); 
			solMedica.put("cod_user_crea", solicitud.get("cod_pers"));
			solMedica.put("fec_creacion", new Date()); 
	
			if (log.isDebugEnabled())
				log.debug("Datos solicitud de solMedica: " + solMedica);
			boolean res = txxxxDAO.insertSolMedica (solMedica);
			 
		} catch (Exception e) {
			log.error("Error en guardar de solicitud", e);
			mensajes.add("Error al guardar la solicitud ");
			return mensajes;
		}
		return mensajes;
	}
	
	
	// PAS20181U230200023 - solicitud de licencia enfermedad
	/**
	 * Metodo para obtener datos adicionales de solicitud de licencia por
	 * enfermedad 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public Map obtenerDatosSolLicenciaEnfermedad(String codPers, String anno,
			String numero) {

		DataSource dbpool = sl.getDataSource("java:comp/env/jdbc/dcsp");
		T9388DAO solLicMedDAO = new T9388DAO(dbpool);
		T8151DAO archivoDAO = new T8151DAO(dbpool);
		T5864DAO parametroDAO = new T5864DAO(dbpool);
		Map datosAdicionales = new HashMap();
		datosAdicionales.put("archivoEncontrado", Boolean.FALSE);
		datosAdicionales.put("numArchivo", new Integer(0));
		datosAdicionales.put("numSeqdoc", new Integer(0));
		datosAdicionales.put("numArcDet", "0");
		Map solicitud = new HashMap();
		try {
			log.debug("->Ingreso a obtenerSolicitudReintegro ");
			solicitud = solLicMedDAO.findSolicitudLicMedica(codPers, anno,
					numero); 
			if (solicitud != null) {
				Integer numArchivo = (Integer) solicitud.get("num_archivo");
				if (numArchivo != null && !numArchivo.equals(new Integer(0))) {
					datosAdicionales.put("numArchivo", numArchivo);
					datosAdicionales.put("numSeqdoc", new Integer(0));
					List archivos = archivoDAO .findArchivosByNumArchivoNumSeqDoc(numArchivo,
									new Integer(0));
					if (archivos != null && !archivos.isEmpty()) {
						Map object = (Map) archivos.get(0);
						datosAdicionales.put("numArcDet",
								object.get("num_arcdet") + "");
						datosAdicionales.put("archivoEncontrado", Boolean.TRUE);
					}
				}
				String codcmp =(String)solicitud.get("cod_cmp") ;
				if (codcmp != null && !codcmp.trim().equals("")) {
					datosAdicionales.put("cmp", codcmp+"");	
				}
				
				String codDiagnostico = (String) solicitud.get("cod_cie10");
				if (codDiagnostico != null && !codDiagnostico.trim().equals("")) {
					HashMap filtro = new HashMap();
					filtro.put("cod_tab", Constantes.T5864_CODTAB_DIAG_CIE10);
					filtro.put("cod_tip_desc", "D");
					filtro.put("cod_estado", "A");
					filtro.put("num_codigo", codDiagnostico);
					List habilitacion = parametroDAO.findByFiltro(filtro);
					if (habilitacion != null && !habilitacion.isEmpty()) {
						Map hb = (Map) habilitacion.get(0);
						String diagnostico = hb.get("des_larga").toString()
								.trim();
						datosAdicionales.put("diagnostico", diagnostico);
					}
				}

			}

			log.debug("Solicitud  " + datosAdicionales);

		} catch (Exception e) {
			log.error(
					"Error al intentar obtener Solicitud de Licencia enfermedad",
					e);
		}

		return datosAdicionales;
	}
	
	
	//AGONZALESF -PAS20181U230200067 - solicitud de reintegro, planillas adicionales
	public List obtenerDescuentosBoleta(String pool_sig, String codigoEmpl, String anio,String mes,String tipo,String subtipo){
		DataSource dcsig = sl.getDataSource(pool_sig);
		PlaniHistoricasDAO ph = new PlaniHistoricasDAO(dcsig);
		HistMetasEmpleaDAO hme = new HistMetasEmpleaDAO(dcsig);
		HashMap mapa = new HashMap();
		mapa.put("codi_empl_per", codigoEmpl); 
		mapa.put("anio", anio);
		mapa.put("mes", mes);
		mapa.put("tipo_plan_tpl", tipo);
		mapa.put("subtipoplan",subtipo); 
		ArrayList descuentos = null;
		if (log.isDebugEnabled()) log.debug("Entre a MaestroPersonalDAO");
		HashMap mapaMaestroPersonal = (HashMap) hme.findByCabeceraBoleta(mapa); 
		if (log.isDebugEnabled()) log.debug("Resultados a MaestroPersonalDAO: " + mapaMaestroPersonal); 
		if (mapaMaestroPersonal != null	&& mapaMaestroPersonal.size() > 0) {
			if (log.isDebugEnabled())
				log.debug("Datos Maestro Personal "+ mapaMaestroPersonal);
			String ind_extorno = (String) mapaMaestroPersonal.get("ind_extorno");
			if (log.isDebugEnabled())
				log.debug("ind_extorno: " + ind_extorno);		 
				
			 	if (ind_extorno.equals("0")) { 
					if (log.isDebugEnabled())
						log.debug("ptipo_plan mostrar boleta: "+tipo); 
					// sin extorno
					if(tipo.equals("01")){ 
						descuentos = (ArrayList) ph.findByDetalleBoleta(mapa, "3"); 
					}else if(tipo.equals("02")){ 
						descuentos = (ArrayList) ph.findByDetalleBoleta(mapa, "3"); 
					}	 
					//
					if (log.isDebugEnabled())
						log.debug("se obtiene informacion de la descuentos en boleta sin aplicación de extornos ");
				} else {// con extorno 
					
					if(tipo.equals("01")){ 
						descuentos = (ArrayList) ph.findByDetalleBoletaWhitExtorno(mapa, "3"); 	
					}else if(tipo.equals("02")){ 
						descuentos = (ArrayList) ph.findByDetalleBoletaWhitExtorno(mapa, "3"); 
					}	 
					if (log.isDebugEnabled())
						log.debug("se obtiene informacion de la descuentos en boleta con aplicación de extornos ");
				}
		}
		
		return descuentos;
	}
	
  	// APGONZALESF -PAS20191U230200011- Documentos aprobados
	private Map obtenerDocumentos(Map filtro) {
		Map documentos = new HashMap();
		int indData = 0;
		DataSource dcsp = sl.getDataSource("jdbc/dcsp");
		try {
			if (log.isDebugEnabled())	log.debug("FILTRO ->" + filtro);
			T1271DAO t1271dao = new T1271DAO(dcsp);
			List papeletas = t1271dao.findDocAprobadoPapeletas(filtro);
			if (papeletas != null && !papeletas.isEmpty()) {
				indData++;
			}
			if (log.isDebugEnabled())log.debug("papeletas  " + papeletas);
			documentos.put("papeletas", papeletas);

			T1282DAO t1282dao = new T1282DAO(dcsp);
			List vacaciones = t1282dao.findDocAprobadoVacaciones(filtro);
			if (vacaciones != null && !vacaciones.isEmpty()) {
				indData++;
			}
			if (log.isDebugEnabled())log.debug("vacaciones   " + vacaciones);
			documentos.put("vacaciones", vacaciones);

			T1273DAO t1273dao = new T1273DAO(dcsp);
			List licencias = t1273dao.findDocAprobadoLicencias(filtro);
			if (licencias != null && !licencias.isEmpty()) {
				indData++;
			}
			if (log.isDebugEnabled())log.debug("licencias  " + licencias);
			documentos.put("licencias", licencias);

			T1277DAO t1277dao = new T1277DAO();
			List solicitudes = t1277dao.findSolicitudesByRangoByEmpleado(dcsp,
					filtro);
			if (solicitudes != null && !solicitudes.isEmpty()) {
				indData++;
			}
			if (log.isDebugEnabled())log.debug("solicitudes  " + solicitudes);
			documentos.put("solicitudes", solicitudes);
			
			documentos.put("existeDatos", new Boolean(indData > 0));

		} catch (Exception e) {
			log.error("Error en busqueda de movimientos de sustento",e);
			documentos.put("existeDatos", Boolean.FALSE);
		}
		return documentos;

	}
	
	//AGONZALESF -PAS20191U230200011 - sustento para movimientos de reintegro
	private boolean existeMovSustentoReintegro( Map filtro){
		try {
			
			log.debug("movimientos de sustento");	
			log.debug("FILTRO  :" + filtro);	
			Map documentos = obtenerDocumentos(filtro);
			if(!documentos.containsKey("existeDatos")){
				return false;
			}
			log.debug("documentos  :" + documentos);	
			Boolean existe = (Boolean)documentos.get("existeDatos");
			return existe.booleanValue(); 
			
		} catch (Exception e) {
			log.error("existeMovSustentoReintegro",e);
			return false;
		}
		
	}
	//AGONZALESF -PAS20191U230200011 - validar si existen devoluciones activas	
	private boolean existeDevolucionesActivas( Map filtro){
		try { 
			DataSource dsSig = sl.getDataSource("java:comp/env/jdbc/dcsig"); 			 
			T5738DAO t5738dao = new T5738DAO(dsSig); 			
			log.debug("verificar si existe devolucion");	
			Map map =  t5738dao.findConteoDevolucionesAprobadas(filtro);
			BigDecimal conteo = (BigDecimal)map.get("conteo");
			log.debug("conteo de devoluciones activas "+ conteo.intValue());	
			
			return conteo.intValue()>0;
		} catch (Exception e) {
			log.error(e);
			return true;
		}
		
	}
			
	
}