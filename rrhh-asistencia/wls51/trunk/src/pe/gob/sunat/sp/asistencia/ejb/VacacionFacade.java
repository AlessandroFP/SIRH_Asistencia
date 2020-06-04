package pe.gob.sunat.sp.asistencia.ejb;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import javax.ejb.SessionContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pe.gob.sunat.batch.dao.QueueDAO;
import pe.gob.sunat.framework.core.bean.MensajeBean;
import pe.gob.sunat.framework.core.bean.ParamBean;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.core.ejb.FacadeException;
import pe.gob.sunat.framework.core.ejb.StatelessAbstract;
import pe.gob.sunat.framework.core.pattern.ServiceLocator;
import pe.gob.sunat.framework.util.Propiedades;
import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.framework.util.mail.Correo;
import pe.gob.sunat.rrhh.asistencia.dao.T1273DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T1277DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T1282DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T3886DAO;
import pe.gob.sunat.sol.BeanMensaje;
import pe.gob.sunat.sol.IncompleteConversationalState;
import pe.gob.sunat.sp.asistencia.bean.BeanPeriodo;
import pe.gob.sunat.sp.asistencia.bean.BeanVacacion;
import pe.gob.sunat.sp.asistencia.dao.T1272DAO;
import pe.gob.sunat.sp.asistencia.dao.T1276DAO;
import pe.gob.sunat.sp.asistencia.dao.T1281DAO;
import pe.gob.sunat.sp.asistencia.dao.T1456DAO;
import pe.gob.sunat.sp.dao.T02DAO;
import pe.gob.sunat.sp.dao.T99DAO;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;

/**
 * 
 * @ejb.bean name="VacacionFacadeEJB"
 *           description="VacacionFacade"
 *           jndi-name="ejb/rrhh/facade/sp/asistencia/VacacionFacadeEJB"
 *           type="Stateless" view-type="remote"
 * 
 * @ejb.home remote-class="pe.gob.sunat.sp.asistencia.ejb.VacacionFacadeHome"
 *           extends="javax.ejb.EJBHome"
 * 
 * @ejb.interface remote-class="pe.gob.sunat.sp.asistencia.ejb.VacacionFacadeRemote"
 *                extends="javax.ejb.EJBObject"
 * 
 * @ejb.resource-ref res-ref-name="jdbc/dcsp" res-type="javax.sql.DataSource" res-auth="Container"
 * @weblogic.resource-description res-ref-name="jdbc/dcsp" jndi-name="jdbc/dcsp"
 * 
 * @ejb.resource-ref res-ref-name="jdbc/dgsp" res-type="javax.sql.DataSource" res-auth="Container"
 * @weblogic.resource-description res-ref-name="jdbc/dgsp" jndi-name="jdbc/dgsp"
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
public class VacacionFacade extends StatelessAbstract {

	private final Log log = LogFactory.getLog(getClass());
	private SessionContext sessionContext;
	
	ServiceLocator sl = ServiceLocator.getInstance();
	Propiedades constantes = new Propiedades(getClass(), "/constantes.properties");

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public String firmarLibro(String dbpool, String codPers, String[] params,
			ArrayList detalle, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		String res = Constantes.OK;
		try {

			T1281CMPHome cmpHome = (T1281CMPHome) sl.getLocalHome(T1281CMPHome.JNDI_NAME);
			//T1282CMPHome detalleHome = (T1282CMPHome) ServiceLocator.getInstance().getLocalHome(T1282CMPHome.JNDI_NAME);

			if (params != null) {
			    
			    BeanVacacion vacacion = null;
			    T1281CMPLocal cmpLocal = null;
			    //T1282CMPLocal detalleLocal = null; 
			    
				for (int i = 0; i < params.length; i++) {

					vacacion = (BeanVacacion) detalle.get(Integer.parseInt(params[i]));

					cmpLocal = cmpHome.findByPrimaryKey(new T1281CMPPK(codPers,
									vacacion.getAnnoVac()));
					//PRAC-JCALLO
					/*detalleLocal = detalleHome.findByPrimaryKey(new T1282CMPPK(
									codPers,
									vacacion.getPeriodo(), vacacion
											.getFechaInicio()));*/
					
					int saldo = cmpLocal.getSaldo().intValue();

					String tipo = vacacion.getLicencia();
					if ((tipo.equals(Constantes.VACACION_ESPECIAL))
							|| (saldo - vacacion.getDias() >= 0)) {

						//actualizamos la cabecera
						cmpLocal.setSaldo(new Integer(""
								+ (saldo - vacacion.getDias())));
						cmpLocal.setFmod(new Timestamp(System
								.currentTimeMillis()));
						cmpLocal.setCuserMod(usuario);

						//actualizamos el detalle
						
						//PRAC-JCALLO
						T1282DAO t1282dao = new T1282DAO(dbpool);
						Map dtos = new HashMap();
						//columnas a actualizar
						Map columns = new HashMap();
						columns.put("est_id", Constantes.ACTIVO);
						columns.put("fmod", new FechaBean().getTimestamp());
						columns.put("cuser_mod", usuario);
						dtos.put("columns", columns);
						//datos de la llave primaria
						dtos.put("cod_pers", codPers);
						dtos.put("periodo", vacacion.getPeriodo());
						dtos.put("licencia", Constantes.VACACION_ESPECIAL);//parametro agregado por tema de cambio de PK en t1282
						dtos.put("ffinicio", vacacion.getFechaInicio());
						log.debug("t1282dao.updateCustomColumns(dtos)... dtos:"+dtos);
						t1282dao.updateCustomColumns(dtos);
						//PRAC-JCALLO
						/*detalleLocal.setEstId(Constantes.ACTIVO);
						detalleLocal.setFmod(new Timestamp(System
								.currentTimeMillis()));
						detalleLocal.setCuserMod(usuario);*/

					} else {
						res = "Ud. no posee el saldo acumulado necesario para firmar la vacacion.";
					}

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

	
/* JRR - MODIF BY PROGRAMACION - 04/06/2010 */
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
     public String venderVacaciones(String dbpool, String codPers, String anno, String dias, String usuario, String annoRef, String areaRef, String numeroRef,String obs/*, Timestamp fIni*/)
	 throws IncompleteConversationalState, RemoteException {
	 BeanMensaje beanM = new BeanMensaje();
	 String res = Constantes.OK;
	 String men = "";
	 DataSource dbpool_g = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dgsp");
		
//	 DataSource dbpool_g = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dgsp");
     try {
	    //T1281DAO vacDAO = new T1281DAO();
		
		T1282DAO vacDAO2 = new T1282DAO(dbpool_g);
		log.debug("dbpool: "+dbpool);	  
		
		//Comentado por FRD boolean poseeSaldo = vacDAO.findSaldoVacacional(dbpool, codPers, anno, dias);
		//prac-jcallo
		Map params = new HashMap();
		params.put("cod_pers", codPers);
		params.put("licencia", Constantes.VACACION_VENTA);
		params.put("anho", anno);
		log.debug("vacDAO2.findByCodPersLicenciaAnhoVac(params)... params:"+params);
		//int diasAcumVta = vacDAO2.findByCodPersLicenciaAnhoVac(dbpool, codPers, Constantes.VACACION_VENTA, anno);
//		Comentado por FRD		int diasAcumVta = vacDAO2.findByCodPersLicenciaAnhoVac(params);
//		Comentado por FRD		int diasVender = Integer.parseInt(dias) + diasAcumVta;
			  
//			Comentado por FRD if (poseeSaldo){
//		 	Comentado por FRD          if (diasVender <= 15){ 
		    //T02DAO personalDAO = new T02DAO();
		    //T99DAO codigoDAO = new T99DAO();
		    //Este trae la constante de 15 dias... ya na deebra ser asi
		    //los dias vienen del JSP respectivo
			   
		    //String diasVenta = codigoDAO.findParamByCodTabCodigo(dbpool,
		    //  Constantes.CODTAB_PARAMETROS_ASISTENCIA,
		    //  Constantes.DIAS_VENTA_VACACIONES);
			String diasVenta = dias; 
			int numDiasVenta = diasVenta != null ? Integer.parseInt(diasVenta) : 0;
		    T1281CMPHome cmpHome = (T1281CMPHome) ServiceLocator.getInstance().getLocalHome(T1281CMPHome.JNDI_NAME);
			//T1282CMPHome detalleHome = (T1282CMPHome)ServiceLocator.getInstance().getLocalHome(T1282CMPHome.JNDI_NAME);
		   
		    T1281CMPLocal cmpLocal = cmpHome.findByPrimaryKey(new T1281CMPPK(codPers, anno));
			int saldo = cmpLocal.getSaldo().intValue();
			
		    //HashMap regPerson =personalDAO.findByCodPers(dbpool, codPers);
		    pe.gob.sunat.rrhh.dao.T02DAO personalDAO = new pe.gob.sunat.rrhh.dao.T02DAO(dbpool_g);
		    Map regPerson=personalDAO.findByCodPers(codPers);
		    if(log.isDebugEnabled()) log.debug("Paso1:"+regPerson);
		    String uOrg = "";
			if (regPerson.get("t02cod_uorgl") == null || ((String) regPerson.get("t02cod_uorgl")).trim().equals("")) {
			   if (regPerson.get("t02cod_uorg") != null) {
			      uOrg = (String) regPerson.get("t02cod_uorg");
			   }
			}else {
			   uOrg = (String) regPerson.get("t02cod_uorgl");
			}
			cmpLocal.setSaldo(new Integer("" + (saldo - numDiasVenta)));
			//cmpRemote.setSaldoTemp(new Integer("" +(saldoT -numDiasVenta)));
			cmpLocal.setFmod(new Timestamp(System.currentTimeMillis()));
			cmpLocal.setCuserMod(usuario);
		    //creamos el detalle de la venta
			//T1276DAO periodoDAO = new T1276DAO();
			pe.gob.sunat.rrhh.asistencia.dao.T1276DAO periodoDAO = new pe.gob.sunat.rrhh.asistencia.dao.T1276DAO(dbpool_g);
			//Si viene de Solicitudes el areaRef llega con valor vaco y el numeroRef tambien
			//pero si viene de Vacaciones el areaRef toma el valor expresado abajo y el numeroRef
			//toma el valor de Fecha ... un cambio que pidieron el 30/12/2005
			String fActual = "";
			String fActualMasDiasVta = "";
			   if (areaRef.equals("Vacaciones")){
			     fActual = numeroRef;
			     fActualMasDiasVta = Utiles.dameFechaSiguiente(fActual, numDiasVenta);
			     //prac-jcallo
			     //cod_pers, periodo, licencia, ffinicio, anno_vac, ffin, 
			     //dias, anno, u_organ, anno_ref, area_ref, numero_ref, observ, est_id, fcreacion, cuser_crea
			     Map prms = new HashMap();
			     prms.put("cod_pers", codPers);
			     prms.put("periodo", periodoDAO.findPeriodoByFecha(new FechaBean().getFormatDate("yyyy/MM/dd")).get("periodo"));
			     prms.put("licencia", Constantes.VACACION_VENTA);
			     prms.put("ffinicio", new FechaBean(fActual).getTimestamp());
			     prms.put("anno_vac", anno);
			     prms.put("ffin", Utiles.stringToTimestamp(fActualMasDiasVta + " 00:00:00"));
			     prms.put("dias", new Integer(numDiasVenta));
			     prms.put("anno", "");
			     prms.put("u_organ", uOrg);
			     prms.put("anno_ref", annoRef);
			     prms.put("area_ref", "");
			     //JR 27/05/2009
			     //prms.put("numero_ref", new Integer(0));
			     prms.put("numero_ref", null);
			     prms.put("observ", obs);
			     prms.put("est_id", Constantes.ACTIVO);
			     prms.put("fcreacion", new FechaBean().getTimestamp());
			     prms.put("cuser_crea", usuario);
			     log.debug("vacDAO2.insertarVacacionesDetalle(prms)... prms:"+prms);
			     vacDAO2.insertarVacacionesDetalle(prms);
			     //PRAC-JCALLO
			     /*detalleHome.create(
			     codPers, periodoDAO.findPeriodoActual(
			     dbpool).getPeriodo(),
			     new BeanFechaHora(fActual,"dd/MM/yyyy").getTimestamp(),      
			     Constantes.VACACION_VENTA,
			     anno, 
			     new Integer(numDiasVenta), 
			     Utiles.stringToTimestamp(fActualMasDiasVta + " 00:00:00"),
			     Constantes.ACTIVO, "", usuario, uOrg, annoRef, "",
			     "");*/
			   }else{
			     fActual = Utiles.obtenerFechaActual();
			     fActual = Utiles.dameFechaSiguiente(fActual, numDiasVenta);
			     //prac-jcallo
			     //cod_pers, periodo, licencia, ffinicio, anno_vac, ffin, 
			     //dias, anno, u_organ, anno_ref, area_ref, numero_ref, observ, est_id, fcreacion, cuser_crea
			     Map prms = new HashMap();
			     prms.put("cod_pers", codPers);
			     prms.put("periodo", periodoDAO.findPeriodoByFecha(new FechaBean().getFormatDate("yyyy/MM/dd")).get("periodo"));
			     prms.put("licencia", Constantes.VACACION_VENTA);
			     prms.put("ffinicio", new FechaBean().getTimestamp());
			     prms.put("anno_vac", anno);
			     prms.put("ffin", Utiles.stringToTimestamp(fActual + " 00:00:00"));
			     prms.put("dias", new Integer(numDiasVenta));
			     prms.put("anno", "");
			     prms.put("u_organ", uOrg);
			     prms.put("anno_ref", annoRef);
			     prms.put("area_ref", areaRef);
			     //JR 27/05/2009
			     //prms.put("numero_ref", numeroRef!=null?new Integer(numeroRef):new Integer(0));
			     prms.put("numero_ref", (numeroRef!=null&&!numeroRef.trim().equals("0"))?new Integer(numeroRef):null);
			     prms.put("observ", "");
			     prms.put("est_id", Constantes.ACTIVO);
			     prms.put("fcreacion", new FechaBean().getTimestamp());
			     prms.put("cuser_crea", usuario);
			     log.debug("vacDAO2.insertarVacacionesDetalle(prms)... prms:"+prms);
			     vacDAO2.insertarVacacionesDetalle(prms);
			     //PRAC-JCALLO
			     /*detalleHome.create(
			      codPers, periodoDAO.findPeriodoActual(
			      dbpool).getPeriodo(), 
			      new Timestamp(System.currentTimeMillis()), 
			      Constantes.VACACION_VENTA,
			      anno, 
			      new Integer(numDiasVenta), 
			      Utiles.stringToTimestamp(fActual + " 00:00:00"),
			      Constantes.ACTIVO, "", usuario, uOrg, annoRef, areaRef,
			      numeroRef);*/
			     Map param1 = new HashMap();
			     param1.put("cod_pers", codPers);
			     param1.put("licencia", Constantes.VACACION_PROGRAMADA);
			     //param1.put("ffinicio", "");//No buscara por este campo, no importa su valor
			     param1.put("anno_vac", anno);
			     param1.put("estado_id", constantes.leePropiedad("PROGRAMACION_EN_SOLICITUD"));
			     param1.put("fmod", new FechaBean().getTimestamp());
			     param1.put("cuser_mod", usuario);
			     param1.put("estado_new", constantes.leePropiedad("PROGRAMACION_VENDIDA"));
			     param1.put("apruebaSolVenta", "1");
			     param1.put("anno_ref", annoRef);
			     param1.put("area_ref", areaRef);
			     
			     //param1.put("dias", new Integer(numDiasVenta));//Modificado 3er Entregable Venta
			     //JR 27/05/2009
			     //param1.put("numero_ref", numeroRef!=null?new Integer(numeroRef):new Integer(0));
			     param1.put("numero_ref", (numeroRef!=null&&!numeroRef.trim().equals("0"))?new Integer(numeroRef):null);
			     vacDAO2.actualizaProgramacion(param1);
			   }
//			 Comentado por FRD }else{
//			 Comentado por FRD             res = "El trabajador ya vendi, para el ao "+ anno +", "+ 
//			 Comentado por FRD 			String.valueOf(diasAcumVta) + " dias. No puede vender " + dias + 
//			 Comentado por FRD 			" ms por que pasa el lmite mximo de venta de 15 dias por Ao";  
//			 Comentado por FRD           } 
//			 Comentado por FRD         }else{
//			 Comentado por FRD 		   res = "El trabajador no posee saldo vacacional suficiente (Mximo 15 dias de venta por ao) para el a&ntilde;o "+ anno +".";
//			 Comentado por FRD 		}



     } catch (Exception e) {
	   men = String.valueOf(e);
	   if (men.indexOf("Bean with primary key")> 0){
	      men = "Ya Existe Informacin para el N de Registro, Ao y Fecha. Favor de Verificar...";
		  log.error(men,e);
		  beanM.setMensajeerror(men);
	   }else{
		  log.error(e,e);
		  beanM.setMensajeerror(e.getMessage());
	   }
	   beanM.setMensajesol("Por favor intente nuevamente.");
	   throw new IncompleteConversationalState(beanM);
	 }
	 return res;
     }

	
	
	/***
	 *  Metodo encargado de actualizar el saldo ,actualizar el estado de la Programacion e insertar la venta
	 * @param param Map
	 * @return String
	 * @throws FacadeException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
public String venderVacaciones(Map param) throws FacadeException {
		
		MensajeBean beanM = new MensajeBean();
		String mensajes=Constantes.OK;
		try {
			//creamos el detalle de la venta
			DataSource dbpool_g = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dgsp");
			
			pe.gob.sunat.rrhh.asistencia.dao.T1281DAO t1281dao = new pe.gob.sunat.rrhh.asistencia.dao.T1281DAO(dbpool_g);
			T1282DAO t1282dao = new T1282DAO(dbpool_g);
			param.put("cod_pers",param.get("codPers"));
			String annoInicial=param.get("anno").toString();
			param.put("anno",param.get("annoVac"));
			Map saldo=t1281dao.findAllColumnsByKey(param);
			Map columns= new HashMap();
			columns.put("saldo",new Integer(((Integer)saldo.get("saldo")).intValue()-(new Integer((String)param.get("dias"))).intValue()));
			param.put("columns",columns);
			t1281dao.updateCustomColumns(param);
			param.put("anno",annoInicial);
			param.put("estid",constantes.leePropiedad("PROGRAMACION_VENDIDA"));
			param.put("licencia",constantes.leePropiedad("VACACION_PROGRAMADA"));
			columns.remove("saldo");
  		    columns.put("est_id",constantes.leePropiedad("PROGRAMACION_VENDIDA"));
  		    columns.put("cuser_mod",param.get("usuario").toString());
  		    columns.put("fmod",new FechaBean().getTimestamp());
  		    param.put("columns",columns);
  		    t1282dao.updateCustomColumnsSinPeriodo(param);
 		    pe.gob.sunat.rrhh.dao.T02DAO personalDAO = new pe.gob.sunat.rrhh.dao.T02DAO(dbpool_g);
  		    Map regPerson=personalDAO.findByCodPers((String)param.get("cod_pers"));
			String uOrg = "";
			if (regPerson.get("t02cod_uorgl") == null || ((String) regPerson.get("t02cod_uorgl")).trim().equals("")) {
				if (regPerson.get("t02cod_uorg") != null) {
					uOrg = (String) regPerson.get("t02cod_uorg");
				}
			} else {
				uOrg = (String) regPerson.get("t02cod_uorgl");
			}
			//////////
			pe.gob.sunat.rrhh.asistencia.dao.T1276DAO periodoDAO = new pe.gob.sunat.rrhh.asistencia.dao.T1276DAO(dbpool_g);
			//periodoDAO.findPeriodoByFecha
			//JR 27/05/2009
			String numeroRef = String.valueOf(param.get("numero"));
			Map params = new HashMap(); 
			params.put("cod_pers",param.get("codPers")); 
			params.put("periodo",(periodoDAO.findPeriodoByFecha(new FechaBean().getFormatDate("yyyy/MM/dd"))).get("periodo"));
			params.put("licencia",param.get("tipo"));
			params.put("ffinicio",param.get("ffinicio")); 
			params.put("anno_vac",param.get("annoVac"));
			params.put("ffin",param.get("ffin"));
			params.put("dias",param.get("dias")); 
			params.put("anno",param.get("anno")) ;
			params.put("u_organ",uOrg); 
			params.put("anno_ref",param.get("annoVac"));
			params.put("area_ref",param.get("uorgan"));
			//JR 27/05/2009
			//params.put("numero_ref",param.get("numero"));
			params.put("numero_ref",(numeroRef!=null&&!numeroRef.trim().equals("0"))?new Integer(numeroRef):null);
			params.put("observ","");
			params.put("est_id",constantes.leePropiedad("ACTIVO")); 
			params.put("fcreacion",new FechaBean().getTimestamp()); 
			params.put("cuser_crea",param.get("usuario"));
			t1282dao.insertarVacacionesDetalle(params);
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
/*      */

	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void registrarNvaVacacion(String dbpool, String codPers,
			String licencia, java.sql.Timestamp fechaIni,
			java.lang.Integer dias, String anio, java.sql.Timestamp fechaFin,
			String observacion, String estado, String usuario, String annoRef,
			String areaRef, String numeroRef, HashMap hmPermitidos,
			HashMap seguridad, boolean adelanto) throws IncompleteConversationalState,
			RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		log.debug("VacacionFacade.registrarNvaVacacion... annoRef: "+annoRef);//ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII
		log.debug("VacacionFacade.registrarNvaVacacion... numeroRef: "+numeroRef);//ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII
		log.debug("VacacionFacade.registrarNvaVacacion... areaRef: "+areaRef);//ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII
		
		try {
			String esAdelanto=adelanto?"1":"0";
			adelanto=false;
			
			//Verificamos seguridad.
			AsistenciaFacadeHome facadeHome = (AsistenciaFacadeHome) ServiceLocator
					.getInstance().getRemoteHome(
							AsistenciaFacadeHome.JNDI_NAME,
							AsistenciaFacadeHome.class);
			AsistenciaFacadeRemote facadeRemote = facadeHome.create();

			HashMap hPers = facadeRemote.buscarTrabajador(dbpool, codPers, seguridad);

			if (hPers.get("t02cod_pers") == null) {
				throw new Exception(
						"El usuario no puede registrar una vacacion para el trabajador con registro : "+ codPers);
			}
			
			if (adelanto){
				if (!((String)hPers.get("t02cod_regl")).trim().equals(Constantes.REGIMEN_LABORAL_276)) {
					throw new Exception(
							"El trabajador no pertenece al Regimen Laboral 276.");
				}
			}
			T1281DAO dao = new T1281DAO();
			T99DAO codigoDAO = new T99DAO();
			
			T1281CMPHome cmpHome = (T1281CMPHome) sl.getLocalHome(T1281CMPHome.JNDI_NAME);
			
			ArrayList saldos = new ArrayList();
			
			if (anio.equals("NADA")){
				saldos = dao.findByCodPersSaldoAnno(dbpool, codPers, true, "", null);
			}else{
				saldos = dao.findByCodPersSaldoAnno(dbpool, codPers, true, anio, null);
			}
			
			log.debug("Saldos log:"+saldos);
			String diasVaca = codigoDAO.findParamByCodTabCodigo(dbpool,
					Constantes.CODTAB_PARAMETROS_ASISTENCIA,
					Constantes.DIAS_VACACIONES);
			int numDiasVaca = diasVaca != null ? Integer.parseInt(diasVaca) : 0;
			
			int diasSolicitados = dias.intValue();
			int diasConcedidos = 0;
			log.debug("diasSolicitados:"+diasSolicitados);
			BeanVacacion bVac = null;
			String fecFin = "";
			int saldoTemp = diasSolicitados;
			boolean vacEspecial = licencia.trim().equals(Constantes.VACACION_ESPECIAL);
			boolean cantAceptada = true;
			int cantEvaluada = 0;
			String ultAno = "";
			
			/*if(diasSolicitados<6){
				cantAceptada=false;
			}*/
		
			if (saldos!=null){
				for (int i = 0; i < saldos.size() && cantAceptada; i++) {
				
					bVac = (BeanVacacion) saldos.get(i);
					diasConcedidos = saldoTemp;
					fecFin = "";
					ultAno = bVac.getAnno();
					log.debug("P0."+saldoTemp+"-"+bVac.getSaldo());
					if (saldoTemp <= bVac.getSaldo()) {
						log.debug("P1."+saldoTemp);
						if (vacEspecial || adelanto) {
							cantAceptada = true;
						} else {
							
							cantAceptada = esCantidadAceptada(dbpool, codPers, bVac
									.getAnno(), hmPermitidos, saldoTemp, "");
							log.debug("P2."+cantAceptada);
						}
	
						if (cantAceptada) {							
							diasSolicitados -= saldoTemp;
							saldoTemp = bVac.getSaldo() - saldoTemp;	
							log.debug("P3."+cantAceptada+"-"+diasSolicitados+"-"+saldoTemp);
						}				
					} 
					else {
						log.debug("P4."+saldoTemp);
						if (vacEspecial || adelanto) {
							cantAceptada = true;							
						} 
						else {
							cantEvaluada = saldoTemp - bVac.getSaldo();	
							cantAceptada = ((cantEvaluada == 7)
									|| (cantEvaluada == 8)
									|| (cantEvaluada == 14)
									|| (cantEvaluada == 15)
									|| (cantEvaluada == 16)
									|| (cantEvaluada == 22) 
									|| (cantEvaluada == 23) || (cantEvaluada < 6) );
						}
	
						if (cantAceptada) {
							saldoTemp -= bVac.getSaldo();	
							diasConcedidos = bVac.getSaldo();
							diasSolicitados -= bVac.getSaldo();
						}
					}
	
					if (cantAceptada) {

						fecFin = Utiles.dameFechaSiguiente(Utiles
								.timeToFecha(fechaIni), diasConcedidos - 1);
						log.debug("ObservUlt:"+observacion);
						registrarVacacion(
								dbpool, 
								codPers, 
								adelanto?Constantes.VACACION_ESPECIAL:licencia, 
								fechaIni, 
								bVac.getAnno(), 
								new Integer(diasConcedidos), 
								Utiles.stringToTimestamp(fecFin + " 00:00:00"),
								observacion, 
								estado, 
								usuario, 
								annoRef, 
								areaRef,
								numeroRef);
	
						//Cambiamos la fecha de inicio del proximo registro
						fechaIni = Utiles.stringToTimestamp(Utiles.dameFechaSiguiente(fecFin, 1)+ " 00:00:00");
	
						try{
							//Actualizamos el saldo:
							if(log.isDebugEnabled()) log.debug("PERSONA : "+codPers);
							if(log.isDebugEnabled()) log.debug("dias concedidos : "+diasConcedidos);
							if(log.isDebugEnabled()) log.debug("Saldo : "+bVac.getSaldo());
							T1281CMPLocal cmpLocal = cmpHome
									.findByPrimaryKey(new T1281CMPPK(codPers,bVac.getAnno()));
							
							cmpLocal.setSaldo(new Integer(bVac.getSaldo()-diasConcedidos));
							cmpLocal.setFmod(new Timestamp(System.currentTimeMillis()));
							cmpLocal.setCuserMod(usuario);
						}
						catch(Exception e){
							throw new Exception("No se encontr&oacute; data para la cabecera del "+bVac.getAnno()+".");
						}
	
						if (diasSolicitados == 0) {
							break;
						}
					}
				}
			}

			if (diasSolicitados > 0) {
				
				if (adelanto){

					//String sigAno = Utiles.obtenerAnhoActual();
					String sigAno = anio;
					
					if (!ultAno.equals("")) sigAno = ""+(Integer.parseInt(ultAno)+1);
					
					try{					
						
						T1281CMPLocal cmpLocal = cmpHome.create(codPers,sigAno);

						cmpLocal.setSaldoTemp(new Integer(diasSolicitados));
						cmpLocal.setSaldo(new Integer(0));
						cmpLocal.setDias(new Integer(0));
						cmpLocal.setFcreacion(new Timestamp(System.currentTimeMillis()));
						cmpLocal.setCuserCrea(usuario);
	
						registrarVacacion(dbpool, codPers, Constantes.VACACION_ESPECIAL, 
								fechaIni, sigAno, 
								new Integer(diasSolicitados), 
								fechaFin,
								observacion, estado, usuario, 
								annoRef, areaRef, numeroRef);							
						
					}
					catch(Exception e){

						T1281CMPLocal cmpLocal = cmpHome
									.findByPrimaryKey(new T1281CMPPK(codPers,sigAno));

						int stemp = cmpLocal.getSaldoTemp().intValue();		
						
						if (stemp+diasSolicitados>numDiasVaca){
							throw new Exception("Ha excedido la cantidad de d&iacute;as permitidos para registrar la vacaci&oacute;n.");	
						}
												
						cmpLocal.setSaldoTemp(new Integer(diasSolicitados+stemp));
						cmpLocal.setSaldo(new Integer(0));
						cmpLocal.setDias(new Integer(0));
						cmpLocal.setFcreacion(new Timestamp(System.currentTimeMillis()));
						cmpLocal.setCuserCrea(usuario);
						
						registrarVacacion(dbpool, codPers, Constantes.VACACION_ESPECIAL, 
								fechaIni, sigAno, 
								new Integer(diasSolicitados), 
								fechaFin,
								observacion, estado, usuario, 
								annoRef, areaRef, numeroRef);	
					}
				}
				else{
					if (cantAceptada){
						if(esAdelanto.equals("1")){
							if(log.isDebugEnabled()) log.debug("Entrando a registrar vacaciones adelantadas como efectivas."+numDiasVaca);
							
							//Finalmente actualizamos el saldo si existe adelanto de vacaciones
								pe.gob.sunat.rrhh.asistencia.dao.T1281DAO cabvacDAO = new pe.gob.sunat.rrhh.asistencia.dao.T1281DAO(dbpool);
								
								Map auxTrab = new HashMap();
								auxTrab.put("cod_pers", codPers);
								auxTrab.put("anno", anio);
								
								Map cabVacacion = cabvacDAO.findAllColumnsByKey(auxTrab); //ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta
								
								if(-1*(Integer.parseInt(cabVacacion!=null?cabVacacion.get("saldo").toString():"0") + ((-1)*diasSolicitados))<=numDiasVaca){
									if(log.isDebugEnabled()) log.debug("cabVacacion CAS:"+ cabVacacion);
									if (cabVacacion==null) {
										auxTrab.put("dias", new Integer(0)); //Número de días 0 
										auxTrab.put("saldo", new Integer((-1)*diasSolicitados)); //Saldo negativo
										auxTrab.put("saldo_temp", new Integer(0));
										auxTrab.put("cuser_crea", codPers);
										auxTrab.put("fcreacion", new Timestamp(System.currentTimeMillis()));
										if(log.isDebugEnabled()) log.debug("**** ProcesoFacade-generarVacaciones-auxTrab: "+auxTrab);//BORRAR
										cabvacDAO.insertarCabVacacion(auxTrab);
									}else{
										Map columns= new HashMap();
										//columns.put("dias", new Integer());  
										columns.put("saldo", new Integer(Integer.parseInt(cabVacacion.get("saldo")+"") + ((-1)*diasSolicitados)));
										columns.put("cuser_mod",codPers);
							  		    columns.put("fmod",new Timestamp(System.currentTimeMillis()));
							  		    if (log.isDebugEnabled()) log.debug("columns CAS: " + columns);
							  		    auxTrab.put("columns", columns);
										cabvacDAO.updateCustomColumns(auxTrab);
									}
									
									registrarVacacion(dbpool, codPers, licencia, 
											fechaIni, anio, 
											new Integer(diasSolicitados), 
											fechaFin,
											observacion, estado, usuario, 
											annoRef, areaRef, numeroRef);	
									log.debug("anio:"+anio+"-"+fechaIni+"-"+fechaFin+"-"+estado+"-"+licencia+"-"+numeroRef);
								}else{
									throw new Exception("Ha excedido la cantidad de días permitidos para registrar la vacación.");
								}
						}else{
							log.debug("Entrando a cantAceptada=true");
							throw new IncompleteConversationalState("No dispone de la cantidad suficiente"
									  + " de d&iacute;as para registrar la vacaci&oacute;n.");		
						}
					}
					else{
						throw new Exception(
								"La cantidad de d&iacute;as ingresada no es v&aacute;lida.");						
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
	

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void registrarVacacion(String dbpool, String codPers,
			String licencia, java.sql.Timestamp fechaIni, String anhoVac,
			java.lang.Integer dias, java.sql.Timestamp fechaFin,
			String observacion, String estado, String usuario, String annoRef,
			String areaRef, String numeroRef)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		log.debug("VacacionFacade.registrarVacacion... numeroRef: "+numeroRef);//ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII
		log.debug("ObservUlt1:"+observacion);
		try {
			
			T1282DAO VacacionDAO = new T1282DAO(dbpool);
			//prac-jcallo
			T1273DAO LicenciaDAO = new T1273DAO(dbpool);
			T1456DAO reinDAO = new T1456DAO();
			
			//prac-jcallo
			Map prms = new HashMap();
			prms.put("cod_pers", codPers);
			prms.put("fecha1", Utiles.timeToFecha(fechaIni));
			prms.put("fecha2", Utiles.timeToFecha(fechaFin));
			prms.put("prog", licencia.equals(Constantes.VACACION_PROGRAMADA)?"si":"no");
			prms.put("fechaIni", "");
			log.debug("VacacionDAO.findByCodPersFIniFFinProg(prms)... prms:"+prms);
			//boolean cruce = VacacionDAO.findByCodPersFIniFFin(dbpool, codPers,Utiles.timeToFecha(fechaIni), Utiles.timeToFecha(fechaFin),licencia.equals(Constantes.VACACION_PROGRAMADA)?true:false, "");
			boolean cruce = VacacionDAO.findByCodPersFIniFFinProg(prms);

			if (cruce) {
				throw new IncompleteConversationalState(
						"La vacación presenta cruces con alguna vacaci&oacute;n programada o efectiva.");
			}
			//prac-jcallo
			Map params = new HashMap();
			params.put("cod_pers", codPers);
			params.put("fecha1", Utiles.timeToFecha(fechaIni));
			params.put("fecha2", Utiles.timeToFecha(fechaFin));
			params.put("numero", "");
			log.debug("LicenciaDAO.findByCodPersFIniFFin(params)... params:"+params);
			boolean tieneLicencia = LicenciaDAO.findByCodPersFIniFFin(params); //(dbpool,codPers, Utiles.timeToFecha(fechaIni), Utiles.timeToFecha(fechaFin), "");

			if (tieneLicencia) {
				throw new IncompleteConversationalState("La vacación presenta cruces con alguna licencia programada.");
			}

			if (licencia.trim().equals(Constantes.VACACION_PROGRAMADA)){
				
				int nextAnho = Integer.parseInt(Utiles.obtenerAnhoActual())+2;

				HashMap hmVacGen = reinDAO.joinWithT02ByCodPersEstId(dbpool, codPers);
				Timestamp fIngreso = Utiles.stringToTimestamp(
						((String) hmVacGen.get("fecha")).substring(0, 6)
						+ nextAnho + " 00:00:00");

				if (fIngreso.compareTo(fechaFin) < 0) {
					throw new IncompleteConversationalState("El rango ingresado excede la fecha l&iacute;mite de programación "+Utiles.timeToFecha(fIngreso)+".");
				}
				
				if (!this.programacionValida(dbpool, codPers, anhoVac, fechaIni, dias.intValue())) {
					throw new IncompleteConversationalState("No dispone de saldo vacacional para el "+anhoVac+".");
				}			
			}

			T1276DAO periodoDAO = new T1276DAO();
			String periodo = periodoDAO.findPeriodoActual(dbpool).getPeriodo();

			T02DAO personalDAO = new T02DAO();
			HashMap regPerson = personalDAO.findByCodPers(dbpool, codPers);
			String uOrg = "";

			if (regPerson.get("t02cod_uorgl") == null || ((String) regPerson.get("t02cod_uorgl")).trim().equals("")) {
				if (regPerson.get("t02cod_uorg") != null) {
					uOrg = (String) regPerson.get("t02cod_uorg");
				}
			} else {
				uOrg = (String) regPerson.get("t02cod_uorgl");
			}

			if (licencia.trim().equals(Constantes.VACACION_PROGRAMADA)) {
				estado = Constantes.PROG_PROGRAMADA;
			}
			
			//T1282CMPHome detalleHome = (T1282CMPHome) ServiceLocator.getInstance().getLocalHome(T1282CMPHome.JNDI_NAME);

			//insertamos el registro
			//prac-jcallo
			//cod_pers, periodo, licencia, ffinicio, anno_vac, ffin, 
			//dias, anno, u_organ, anno_ref, area_ref, numero_ref, observ, est_id, fcreacion, cuser_crea
			Map dtos = new HashMap();
			dtos.put("cod_pers", codPers);
			dtos.put("periodo", periodo);
			dtos.put("licencia", licencia);
			dtos.put("ffinicio", fechaIni);
			dtos.put("anno_vac", anhoVac);
			dtos.put("ffin", fechaFin);
			dtos.put("dias", dias);
			dtos.put("anno", "");
			dtos.put("u_organ", uOrg);
			dtos.put("anno_ref", annoRef);
			dtos.put("area_ref", areaRef);
			//ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII
			log.debug("VacacionFacade.registrarVacacion... llego antes de numero_ref");
			log.debug("VacacionFacade.registrarVacacion... numeroRef==null?: "+ numeroRef);
			log.debug("VacacionFacade.registrarVacacion... numeroRef.trim.equals(vacio)?: "+ String.valueOf(numeroRef.trim().equals("")));
			log.debug("VacacionFacade.registrarVacacion... numeroRef.trim.equals(0)?: "+ String.valueOf(numeroRef.trim().equals("0")));
			//FIN ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII
			dtos.put("numero_ref", (numeroRef!=null&&!numeroRef.trim().equals("0"))?new Integer(numeroRef):null);
			dtos.put("observ", observacion);
			dtos.put("sustento", observacion);
			dtos.put("est_id", estado);
			dtos.put("fcreacion", new FechaBean().getTimestamp());
			dtos.put("cuser_crea", usuario);
			log.debug("VacacionDAO.insertarVacacionesDetalle(dtos)... dtos:"+dtos);
			VacacionDAO.insertarVacacionesDetalle(dtos);
			
			Map param1 = new HashMap();
			param1.put("cod_pers", codPers);
			param1.put("licencia", Constantes.VACACION_PROGRAMADA);
			param1.put("ffinicio", fechaIni);
			param1.put("anno_vac", anhoVac);
			param1.put("estado_id", constantes.leePropiedad("PROGRAMACION_EN_SOLICITUD"));
			param1.put("fmod", new FechaBean().getTimestamp());
			param1.put("cuser_mod", usuario);
			param1.put("estado_new", Constantes.PROG_EFECTUADA);
			
			param1.put("anno_ref", annoRef);
			param1.put("area_ref", areaRef);
			param1.put("numero_ref", (numeroRef!=null&&!numeroRef.trim().equals("0"))?new Integer(numeroRef):null);
			
			VacacionDAO.actualizaProgramacion(param1);
			/*detalleHome.create(codPers, periodo, fechaIni, licencia,
					anhoVac, dias, fechaFin, estado, observacion, usuario,
					uOrg, annoRef, areaRef, numeroRef);*/			
			
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
	public void firmarVacacion(String dbpool,String numero, String codPers, String anho,
			String periodo, String licencia ,java.sql.Timestamp fechaIni, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {
			//PRAC-JCALLO
			//T1282CMPHome cmpDetalleHome = (T1282CMPHome) ServiceLocator.getInstance().getLocalHome(T1282CMPHome.JNDI_NAME);

			/*T1282CMPLocal cmpDetalleLocal = cmpDetalleHome.findByPrimaryKey(new T1282CMPPK(codPers, periodo, fechaIni));*/

			Map dtos = new HashMap();
			//campos a modificar
			Map columns= new HashMap();			
			columns.put("est_id", "1");
			columns.put("cuser_mod", usuario);
			columns.put("fmod", new FechaBean().getTimestamp());			
			dtos.put("columns", columns);			
			//campos de la llave primaria			
			dtos.put("cod_pers", codPers);
			dtos.put("periodo", periodo);
			dtos.put("licencia", licencia);//parametro agregado por tema de cambio de PK t1282
			dtos.put("ffinicio", fechaIni);
			
			/*cmpDetalleLocal.setEstId("1");
			cmpDetalleLocal.setCuserMod(usuario);
			cmpDetalleLocal.setFmod(new Timestamp(System.currentTimeMillis()));*/
			
			T1282DAO t1282dao = new T1282DAO(dbpool);
			log.debug("t1282dao.updateCustomColumns(dtos)... dtos:"+dtos);
			t1282dao.updateCustomColumns(dtos);
			
			//AQUIIII NOS QUEDAMOS HOY MIERCOLES... :p
			T1281CMPHome cmpHome = (T1281CMPHome) sl.getLocalHome(T1281CMPHome.JNDI_NAME);

			T1281CMPLocal cmpLocal = cmpHome
					.findByPrimaryKey(new T1281CMPPK(codPers, anho));
			//necesitamos recuperar el numero de dias..
			//prac-jcallo
			Map datos = new HashMap();
			datos.put("cod_pers", codPers);
			datos.put("periodo", periodo);
			datos.put("licencia", licencia);//parametro agregado por tema de cambio de PK en la t1282
			datos.put("ffinicio", fechaIni);			
			Map vacad = t1282dao.findAllColumnsByKey(datos);
			Integer numDias = (Integer)vacad.get("dias");
			
			cmpLocal.setSaldo(new Integer(cmpLocal.getSaldo().intValue()
					- numDias.intValue()));
			
			cmpLocal.setCuserMod(usuario);
			cmpLocal.setFmod(new Timestamp(System.currentTimeMillis()));

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
	public void modificarVacacion(String dbpool, String numero, String codPers,
			String periodo, String licencia, java.sql.Timestamp fechaIni,
			java.sql.Timestamp nvaFechaIni, java.lang.Integer dias, String anio,
			java.sql.Timestamp nvaFechaFin, String observacion, String usuario,
			HashMap hmPermitidos, HashMap seguridad)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {
		
			Timestamp fechaAct = new Timestamp(System.currentTimeMillis());

/**
			if (fechaAct.after(nvaFechaIni)) {
				throw new IncompleteConversationalState(
						"El registro de vacaciones no puede ser modificado porque"
								+ " la nueva fecha de inicio es anterior a la fecha actual.");
			}
*/
			
			//T1282CMPHome cmpDetalleHome = (T1282CMPHome) ServiceLocator.getInstance().getLocalHome(T1282CMPHome.JNDI_NAME);
			//prac-jcallo
			/*T1282CMPLocal cmpDetalleLocal = cmpDetalleHome
					.findByPrimaryKey(new T1282CMPPK(codPers, periodo, fechaIni));*/
			T1282DAO t1282dao = new T1282DAO(dbpool);
			Map params = new HashMap();
			params.put("cod_pers", codPers);
			params.put("periodo", periodo);
			params.put("licencia", licencia);//parametro agregado por cambio de PK t1282
			params.put("ffinicio", fechaIni);
			log.debug("t1282dao.findAllColumnsByKey(params)... params:"+params);
			Map vacad = t1282dao.findAllColumnsByKey(params);
			
/**			if (fechaAct.after(cmpDetalleLocal.getFfinicio())) {
				throw new IncompleteConversationalState(
						"El registro de vacaciones no puede ser modificado porque"
								+ " la fecha de incio del mismo es anterior a la fecha actual.");

			} else {*/
			log.debug("VacacionFacade.modificarVacacion... vacad: "+vacad);//ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII
				//prac-jcallo
				//if (cmpDetalleLocal.getLicencia().trim().equals(
				if (vacad.get("licencia").toString().trim().equals(
						Constantes.VACACION_SUSPENDIDA)) {

					throw new IncompleteConversationalState(
							"El registro de vacaciones no puede ser modificado porque"
									+ " ya ha sido suspendido.");
				} else {
					
					//PRAC-JCALLO
					String annoRef = (vacad.get("anno_ref") != null ) ? vacad.get("anno_ref").toString():"";
					String areaRef = (vacad.get("area_ref") != null ) ? vacad.get("area_ref").toString():"";
					
					/*ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII
					//JR 27/05/2009
					//String numeroRef = (vacad.get("numero_ref") != null ) ? vacad.get("numero_ref").toString():"0";					
					String numeroRef = String.valueOf(vacad.get("numero_ref"));
					if (numeroRef!=null&&!numeroRef.trim().equals("0")){
					}else{
					  numeroRef = null; 	
					}
					*/ //FIN ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII
					
					//ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII	
					log.debug("VacacionFacade.modificarVacacion... vacad.get(numero_ref)==null?: "+vacad.get("numero_ref"));//ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII
					String numeroRef = (vacad.get("numero_ref") != null ) ? vacad.get("numero_ref").toString():"0";
					log.debug("VacacionFacade.modificarVacacion... numeroRef: "+numeroRef);//ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII
					//FIN ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII							
					
					//Eliminamos el registro fisico
					eliminarVacacion(dbpool,numero, codPers, periodo,licencia ,fechaIni,
							usuario);
					//ESTA PARTE DEL CODIGO NO ES ENTENDIBLE
					//LO QUE SE HA SUPUESTO QUE SI no encuentra el registro con los parametros enviados
					//se debe registrar un nuevo registro de ello con estado inactivo
					
					/*** try {
						cmpDetalleLocal = cmpDetalleHome
								.findByPrimaryKey(new T1282CMPPK(codPers, periodo,fechaIni));
					} catch (Exception ex) {
						//Creamos nuevo reg. de vacacion con estado Inactivo
						registrarNvaVacacion(dbpool, codPers, licencia,
								nvaFechaIni, dias, anio, nvaFechaFin, observacion,
								Constantes.INACTIVO, usuario, annoRef, areaRef,
								numeroRef, hmPermitidos, seguridad,false);

					}*/
					//PRAC-JCALLO					
					Map mVacad = t1282dao.findAllColumnsByKey(params);//params tiene los mismo datos que los usado lineas arriba
					if(mVacad == null || mVacad.isEmpty()){
						registrarNvaVacacion(dbpool, codPers, licencia,
								nvaFechaIni, dias, anio, nvaFechaFin, observacion,
								Constantes.ACTIVO, usuario, annoRef, areaRef,
								numeroRef, hmPermitidos, seguridad,false);
					}
				}
		//	}
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
	public void suspenderVacacion(String dbpool, String numero, String codPers,
			String periodo, String licencia, java.sql.Timestamp fechaIni, String anhoRef,
			String areaRef, String numRef, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {
			
			//T1282CMPHome cmpDetalleHome = (T1282CMPHome) ServiceLocator.getInstance().getLocalHome(T1282CMPHome.JNDI_NAME);
			T1282DAO t1282dao = new T1282DAO(dbpool);

			T1276DAO daoPeriodo = new T1276DAO();
			T02DAO daoPersona = new T02DAO();

			HashMap bPers = null;
			String periodoCod = "";
			BeanPeriodo perCorresponde = null;
			boolean suspender = true;
			boolean partirVacacion = true;
			Timestamp fechaAct = new Timestamp(System.currentTimeMillis());
			int diasXCompensar = 0;
			
			//PRAC-JCALLO
			/*T1282CMPLocal cmpDetalleLocal = cmpDetalleHome
					.findByPrimaryKey(new T1282CMPPK(codPers, periodo, fechaIni));*/
			
			Map params = new HashMap();
			params.put("cod_pers", codPers);
			params.put("periodo", periodo);
			params.put("licencia", licencia); //parametro agregado por tema de cambio de PK de t1282
			params.put("ffinicio", fechaIni);
			log.debug("t1282dao.findAllColumnsByKey(params)... params:"+params);
			Map mVacad = t1282dao.findAllColumnsByKey(params);

			//if (fechaAct.after(cmpDetalleLocal.getFfin())) {
			if (fechaAct.after((Timestamp)mVacad.get("ffin"))) {
				bPers = daoPersona.joinWithT12T99ByCodPers(dbpool, codPers,
						null);

				periodoCod = daoPeriodo.joinWithT1278(dbpool, (String) bPers
						.get("t02cod_uorg"), Utiles.timeToFecha((Timestamp)mVacad.get("ffinicio")));
				perCorresponde = daoPeriodo.findByCodigo(dbpool, periodoCod);

				if (perCorresponde.getPeriodo() != null
						&& !perCorresponde.getPeriodo().trim().equals("")) {
					suspender = fechaAct.before(Utiles
							.stringToTimestamp(perCorresponde.getFechaCie()
									+ " 00:00:00"));
				}
			}

			if (!suspender) {
				throw new IncompleteConversationalState(
						"El registro de vacaciones no puede ser suspendido porque"
								+ " el periodo al que corresponde ya ha sido cerrado.");
			} else {
				if (!mVacad.get("licencia").toString().trim().equals(Constantes.VACACION)
						&& !mVacad.get("licencia").toString().trim().equals(Constantes.VACACION_ESPECIAL)) {
					throw new IncompleteConversationalState(
							"El registro de vacaciones no puede ser suspendido porque"
									+ " no es una vacaci&oacute;n efectiva o especial.");
				} else {

					partirVacacion = (fechaAct.after((Timestamp)mVacad.get("ffinicio"))
							&& fechaAct.before((Timestamp)mVacad.get("ffin")) 
							&& mVacad.get("est_id").toString().equals(Constantes.ACTIVO));
					
					if (partirVacacion) {

						String fechaNva = Utiles.dameFechaSiguiente(Utiles
								.timeToFecha(fechaAct), 1);
						int diasDif = Utiles.obtenerDiasDiferencia(Utiles
								.timeToFecha((Timestamp)mVacad.get("ffinicio")),
								Utiles.timeToFecha(fechaAct)) + 1;
						int diasRestantes = ((Integer)mVacad.get("dias")).intValue()
								- diasDif;
						Timestamp fechaFin = (Timestamp)mVacad.get("ffin");

						//Actualizamos el registro no suspendido
						
						//prac-jcallo
						/*cmpDetalleLocal.setFfin(fechaAct);
						cmpDetalleLocal.setDias(new Integer(diasDif));
						cmpDetalleLocal.setCuserMod(usuario);
						cmpDetalleLocal.setFmod(new Timestamp(System
								.currentTimeMillis()));*/
						Map datos = new HashMap();
						//campos a actualizar
						Map columns = new HashMap();
						columns.put("ffin", fechaAct);
						columns.put("dias", new Integer(diasDif));
						columns.put("cuser_mod", usuario);
						columns.put("fmod", new FechaBean().getTimestamp());
						datos.put("columns", columns);
						//campos la llave primeria
						datos.put("cod_pers", codPers);
						datos.put("periodo", periodo);
						datos.put("licencia", licencia);//parametro agregado por el tema de cambio de PK t1282
						datos.put("ffinicio", fechaIni);
						log.debug("t1282dao.updateCustomColumns(datos)... datos:"+datos);
						t1282dao.updateCustomColumns(datos);
						//prac-jcallo
						/*cmpDetalleLocal = cmpDetalleHome
								.findByPrimaryKey(new T1282CMPPK(codPers, periodo,fechaIni));*/
						//el 
						Map mVacacionesD = t1282dao.findAllColumnsByKey(params);

						//Agregamos registro de vacaciones suspendido para los
						// dias siguientes
						registrarVacacionSuspendida(dbpool, codPers,
								Constantes.VACACION_SUSPENDIDA, Utiles
										.stringToTimestamp(fechaNva
												+ " 00:00:00"), mVacacionesD.get("anno_vac").toString(), new Integer(
										diasRestantes), fechaFin,
										mVacacionesD.get("observ").toString(), anhoRef, areaRef,
								numRef, usuario, mVacacionesD.get("u_organ").toString());

						diasXCompensar = diasRestantes;
					} else {
						//actualizar
						//prac-jcallo
						Map datos = new HashMap();
						//campos a actualizar
						Map columns = new HashMap();
						columns.put("licencia", Constantes.VACACION_SUSPENDIDA);						
						columns.put("anno_ref", anhoRef);
						columns.put("area_ref", areaRef);
						//JR 27/05/2009
						//columns.put("numero_ref", numRef!=null?new Integer(numRef):new Integer(0));
						columns.put("numero_ref", (numRef!=null&&!numRef.trim().equals("0"))?new Integer(numRef):null);
						columns.put("cuser_mod", usuario);
						columns.put("fmod", new FechaBean().getTimestamp());
						
						datos.put("columns", columns);
						//campos la llave primeria
						datos.put("cod_pers", codPers);
						datos.put("periodo", periodo);
						datos.put("licencia", licencia);//parametro agregado por el tema de cambio de PK t1282
						datos.put("ffinicio", fechaIni);
						log.debug("t1282dao.updateCustomColumns(datos)... datos:"+datos);
						t1282dao.updateCustomColumns(datos);
						
						/*cmpDetalleLocal
								.setLicencia(Constantes.VACACION_SUSPENDIDA);
						cmpDetalleLocal.setCuserMod(usuario);
						cmpDetalleLocal.setFmod(new Timestamp(System
								.currentTimeMillis()));
						cmpDetalleLocal.setAnnoRef(anhoRef);
						cmpDetalleLocal.setAreaRef(areaRef);
						cmpDetalleLocal.setNumeroRef(new Short(numRef));*/

						diasXCompensar = ((Integer)mVacad.get("dias")).intValue();
					}

					//Actualizamos la cabecera de las vacaciones
					T1281CMPHome cmpHome = (T1281CMPHome) sl.getLocalHome(T1281CMPHome.JNDI_NAME);

					T1281CMPLocal cmpLocal = cmpHome
							.findByPrimaryKey(new T1281CMPPK(codPers,
									mVacad.get("anno_vac").toString()));

					cmpLocal.setSaldo(new Integer(cmpLocal.getSaldo()
							.intValue()
							+ diasXCompensar));

					cmpLocal.setCuserMod(usuario);
					cmpLocal.setFmod(new Timestamp(System.currentTimeMillis()));
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
	 * @ejb.transaction type="Required"
	 */
	public void registrarVacacionSuspendida(String dbpool, String codPers,
			String licencia, java.sql.Timestamp fechaIni, String anhoVac,
			java.lang.Integer dias, java.sql.Timestamp fechaFin,
			String observacion, String anhoRef, String areaRef, String numRef,
			String usuario, String uOrg) throws IncompleteConversationalState,
			RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {
			//prac-jcallo
			T1282DAO t1282dao = new T1282DAO(dbpool);
			Map params = new HashMap();
			params.put("cod_pers", codPers);
			params.put("fechaIni", Utiles.timeToFecha(fechaIni));
			params.put("fechaFin", Utiles.timeToFecha(fechaFin));
			log.debug("t1282dao.findByCodPersFIniFFin(params)... params:"+params);
			//boolean cruce = dao.findByCodPersFIniFFin(dbpool, codPers, Utiles.timeToFecha(fechaIni), Utiles.timeToFecha(fechaFin));*/
			boolean cruce = t1282dao.findByCodPersFIniFFin(params);

			if (cruce) {
				throw new IncompleteConversationalState(
						"Imposible registrar las vacaciones solicitadas debido a que la"
								+ " programaci&oacute;n que intenta registrar presenta uno o m&aacute;s cruces"
								+ " con otras vacaciones ya programadas.");
			}

			T1276DAO periodoDAO = new T1276DAO();
			String periodo = periodoDAO.findPeriodoActual(dbpool).getPeriodo();

			//T1282CMPHome detalleHome = (T1282CMPHome) ServiceLocator.getInstance().getLocalHome(T1282CMPHome.JNDI_NAME);

			//insertamos el registro
			//prac-jcallo
			/*T1282CMPLocal detalle = detalleHome.create(codPers, periodo, fechaIni, licencia,
					anhoVac, dias, fechaFin, Constantes.INACTIVO, observacion,
					usuario, uOrg, anhoRef, areaRef, numRef);*/

			/*detalle.setAnnoRef(anhoRef);
			detalle.setAreaRef(areaRef);
			detalle.setNumeroRef(new Short(numRef));*/
			
			//cod_pers, periodo, licencia, ffinicio, anno_vac, ffin, ")
			//dias, anno, u_organ, anno_ref, area_ref, numero_ref, observ, est_id, fcreacion, cuser_crea
			Map datos = new HashMap();
			datos.put("cod_pers", codPers);
			datos.put("periodo", periodo);
			datos.put("licencia", licencia);
			datos.put("ffinicio", fechaIni);
			datos.put("anno_vac", anhoVac);
			datos.put("ffin", fechaFin);
			datos.put("dias", dias);
			datos.put("anno", "");
			datos.put("u_organ", codPers);
			datos.put("anno_ref", anhoRef);
			datos.put("area_ref", areaRef);
			datos.put("numero_ref", (numRef!=null&&!numRef.trim().equals("0"))?new Integer(numRef):null);
			datos.put("observ", observacion);
			datos.put("est_id", Constantes.INACTIVO);
			datos.put("fcreacion", new FechaBean().getTimestamp());
			datos.put("cuser_crea", usuario);
			log.debug("t1282dao.insertarVacacionesDetalle(datos)... datos:"+datos);
			t1282dao.insertarVacacionesDetalle(datos);

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
	public void eliminarVacacion(String dbpool, String numero, String codPers, String periodo,
			String licencia, java.sql.Timestamp fechaIni, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {
			//PRAC-JCALLO
			//T1282CMPHome cmpDetalleHome = (T1282CMPHome) ServiceLocator.getInstance().getLocalHome(T1282CMPHome.JNDI_NAME);
			/*T1282CMPLocal cmpDetalleLocal = cmpDetalleHome
					.findByPrimaryKey(new T1282CMPPK(codPers, periodo, fechaIni));*/
						
			T1282DAO t1282dao = new T1282DAO(dbpool);
			
			Map params = new HashMap();
			params.put("cod_pers", codPers);
			params.put("periodo", periodo);
			params.put("licencia", licencia);//parametro agregado por cambio de PK t1282
			params.put("ffinicio", fechaIni);
			log.debug("t1282dao.findAllColumnsByKey(params)...params:"+params);
			Map mVacad = t1282dao.findAllColumnsByKey(params);
			//String licencia = (mVacad.get("licencia") != null ) ? mVacad.get("licencia").toString():"";
			

			if (licencia.trim().equals(
					Constantes.VACACION_SUSPENDIDA)) {
				throw new IncompleteConversationalState(
						"No se puede eliminar una vacaci&oacute;n suspendida.");
			}

			if (!licencia.trim().equals(Constantes.VACACION_PROGRAMADA) &&
				!licencia.trim().equals(Constantes.REPROGRAMACION_VACACION)){
				
				T1281CMPHome cmpHome = (T1281CMPHome) sl.getLocalHome(T1281CMPHome.JNDI_NAME);

				T1281CMPLocal cmpLocal = cmpHome
						.findByPrimaryKey(new T1281CMPPK(codPers,mVacad.get("anno_vac").toString()));

				cmpLocal.setSaldo(new Integer(cmpLocal.getSaldo().intValue()
						+ ((Integer)mVacad.get("dias")).intValue()));

				cmpLocal.setCuserMod(usuario);
				cmpLocal.setFmod(new Timestamp(System.currentTimeMillis()));

				
			}
			
			//eliminacion fisica del registro de vacacion_d
			//PRAC-JCALLO
			//cmpDetalleLocal.remove();
			log.debug("t1282dao.deleteByPrimaryKey(params)... params:"+params);
			t1282dao.deleteByPrimaryKey(params);//el mapa params ya esta setea con los valores de la llave primaria lineas arriba
			
						
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
	public boolean esCantidadAceptada(String dbpool, String codPers,
			String anho, HashMap hmReferencia, int cantAsignada, String fechaIni)
			throws IncompleteConversationalState, RemoteException {

		boolean res = false;
		BeanMensaje beanM = new BeanMensaje();
		try {
			//prac-jcallo
			T1282DAO dao = new T1282DAO(dbpool);
			Map params = new HashMap();
			
			params.put("cod_pers", codPers);
			params.put("anno_vac", anho);
			params.put("licencia", Constantes.VACACION);
			params.put("ffinicio", fechaIni);
			log.debug("dao.findByCodPersAnnoVacLicencia(params)... params:"+params);
			List lista = dao.findByCodPersAnnoVacLicencia(params);
			log.debug("lista.size(): "+lista);
			HashMap hmResult = (HashMap) hmReferencia.clone();
			int cantActual = 0;
			log.debug("mapa sntes de ev."+hmResult);
			if (lista != null && lista.size() > 0) {
				for (int i = 0; i < lista.size(); i++) {
					
					if (hmReferencia.get(lista.get(i).toString()) != null) {
						
						cantActual = Integer.parseInt(hmReferencia.get(lista.get(i).toString()).toString());

						cantActual--;
						hmResult.put(lista.get(i).toString(), "" + cantActual);
						
						if (lista.get(i).toString().trim().equals("7")) {
							hmResult.put("14", "0");
							hmResult.put("22", "0");
							hmResult.put("30", "0");
						}

						if (lista.get(i).toString().trim().equals("8")) {
							hmResult.put("16", "0");
							hmResult.put("23", "0");
							hmResult.put("30", "0");
						}

						if (lista.get(i).toString().trim().equals("14")) {
							hmResult.put("7", "0");
							hmResult.put("15", "0");
							hmResult.put("22", "0");
							hmResult.put("23", "0");
							hmResult.put("30", "0");
						}

						if (lista.get(i).toString().trim().equals("15")) {
							hmResult.put("7", ""
									+ (Integer.parseInt(hmResult.get("7")
											.toString()) - 1));
							hmResult.put("8", ""
									+ (Integer.parseInt(hmResult.get("8")
											.toString()) - 1));
							hmResult.put("14", "0");
							hmResult.put("16", "0");
							hmResult.put("22", "0");
							hmResult.put("23", "0");
							hmResult.put("30", "0");
						}

						if (lista.get(i).toString().trim().equals("16")) {
							hmResult.put("8", "0");
							hmResult.put("15", "0");
							hmResult.put("22", "0");
							hmResult.put("23", "0");
							hmResult.put("30", "0");
						}

						if (lista.get(i).toString().trim().equals("22")) {
							hmResult.put("7", "0");
							hmResult.put("14", "0");
							hmResult.put("15", "0");
							hmResult.put("16", "0");
							hmResult.put("23", "0");
							hmResult.put("30", "0");
						}

						if (lista.get(i).toString().trim().equals("23")) {
							hmResult.put("8", "0");
							hmResult.put("14", "0");
							hmResult.put("15", "0");
							hmResult.put("16", "0");
							hmResult.put("22", "0");
							hmResult.put("30", "0");
						}
					}
				}
				log.debug("cantAsignada:"+cantAsignada);
				log.debug("mapaEvaluado:"+hmResult);
				res = ((hmResult.get("" + cantAsignada) != null) && (Integer
						.parseInt(hmResult.get("" + cantAsignada).toString()) > 0));
			} else {
				res = ((hmResult.get("" + cantAsignada) != null) && (Integer
						.parseInt(hmResult.get("" + cantAsignada).toString()) > 0));
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
	public ArrayList buscarSaldos(HashMap mapa)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		ArrayList listado = null;
		try {
			T1281DAO daoCab = new T1281DAO();

			String codPers = mapa.get("codPers") != null ? mapa.get("codPers")
					.toString() : "";
			boolean saldoFavor = mapa.get("saldoFavor") != null ? (mapa.get(
					"saldoFavor").toString().trim().equals("true")) : false;
			String anho = mapa.get("anho") != null ? mapa.get("anho")
					.toString() : "";
			String dbpool = (String) mapa.get("dbpool");
			HashMap seguridad = (HashMap) mapa.get("seguridad");
			
			if(mapa.get("from2010")!=null){														//jquispecoi 04/2014
				if(seguridad==null) {seguridad=new HashMap(); seguridad.put("wasNull", "SI");}
				seguridad.put("from2010", (String)mapa.get("from2010"));
			}																					//
			
			listado = daoCab.findByCodPersSaldoAnnoDias(dbpool, codPers,
					saldoFavor,false, anho, seguridad);
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return listado;

	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public List buscarVacacionesXLicencia(HashMap mapa)
			throws IncompleteConversationalState, RemoteException {

		List listado = null;
		//PRAC-ASANCHEZ 16/06/2009
		//List listado1 = new ArrayList();
		//
		BeanMensaje beanM = new BeanMensaje();
		try {
			//prac-jcallo
			String dbpool = (String) mapa.get("dbpool");			
			T1282DAO daoDet = new T1282DAO(dbpool);
			String codPers = mapa.get("codPers") != null ? mapa.get("codPers").toString() : "";
			String estado = mapa.get("estado") != null ? mapa.get("estado").toString() : "";
			String licencia = mapa.get("licencia") != null ? mapa.get("licencia").toString() : "";
			String anho = mapa.get("anho") != null ? mapa.get("anho").toString() : "";
			
			HashMap seguridad = (HashMap) mapa.get("seguridad");
			
			Map params = new HashMap();
			params.put("cod_pers", codPers);
			params.put("estado", estado);
			params.put("licencia", licencia);
			params.put("anno_vac", anho);
			params.put("seguridad", seguridad);
			params.put("from2010", mapa.get("from2010"));		//jquispecoi 04/2014
			if(log.isDebugEnabled()) log.debug("daoDet.joinWithT02AndTipoMov(params)... params:"+params);
			//listado = daoDet.joinWithT99ByCodPersEstIdLicenciaAnhoFechaIni(dbpool, codPers, estado, licencia, anho, seguridad);
			listado = daoDet.joinWithT02AndTipoMov(params);
			
			//PRAC-ASANCHEZ 16/06/2009
/*			if(log.isDebugEnabled())log.debug("params: " + params);
			if(log.isDebugEnabled())log.debug("params.get('licencia'): " + params.get("licencia"));
			if("".equals(params.get("licencia"))){//es decir, si son vacas efectivas
				if(log.isDebugEnabled())log.debug("listado: " + listado);
				//RECORRIENDO LA LISTA:
				Map vac = new HashMap();
				for(int i = 0; i < listado.size(); i++){
					vac = (HashMap)listado.get(i);
					if(log.isDebugEnabled())log.debug("vac: " + vac);
					listado1.add(listado.get(i));
					if(vac.get("licencia").toString().trim().equals(Constantes.VACACION_INDEMNIZADA)){
						i++;	
					}
				}
				listado = listado1;
			}*/
			//
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return listado;

	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public List buscarVacacionesEfecPend(HashMap mapa)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		List listado = null;
		try {
			//prac-jcallo
			T1282DAO daoDet = new T1282DAO(mapa.get("dbpool").toString());
			log.debug("daoDet.findByCodPersFechaFinDiasPend(mapa)... mapa:"+mapa);
			listado = daoDet.findByCodPersFechaFinDiasPend(mapa);//fijarte el parametro cod_pers y los demas
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return listado;

	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public List buscarVacacionesEfecGoz(HashMap mapa)
			throws IncompleteConversationalState, RemoteException {

		List listado = null;
		BeanMensaje beanM = new BeanMensaje();
		try {
			//prac-jcallo
			T1282DAO daoDet = new T1282DAO(mapa.get("dbpool").toString());
			log.debug("daoDet.findByCodPersFechaFinDiasGoz(mapa)... mapa:"+mapa);
			listado = daoDet.findByCodPersFechaFinDiasGoz(mapa);
			log.debug("listado.size():"+listado.size());
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return listado;

	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public HashMap buscarVacacionesGen(String dbpool, String codPers)
			throws IncompleteConversationalState, RemoteException {

		HashMap registro = null;
		BeanMensaje beanM = new BeanMensaje();
		try {
			T1456DAO dao = new T1456DAO();
			registro = dao.joinWithT02ByCodPersEstId(dbpool, codPers);
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		return registro;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void registrarVacacionesGen(HashMap mapa)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {
			String codPers = (String) mapa.get("codPers");
			java.util.Date fecha = Utiles.stringToDate(((String) mapa.get("fecha")).trim());
			String usuario = (String) mapa.get("usuario");

			T1456CMPHome cmpHome = (T1456CMPHome) sl.getLocalHome(T1456CMPHome.JNDI_NAME);

			//Si existe un registro con la misma fecha para la persona
			try {
				T1456CMPLocal cmpLocal = cmpHome
						.findByPrimaryKey(new T1456CMPPK(codPers,
								new java.sql.Date(fecha.getTime())));

				if (cmpLocal.getEstId().trim().equals(Constantes.INACTIVO)) {
					//Desactivamos los registros anteriores correspondientes al
					// personal
					T1456DAO dao = new T1456DAO();
					dao.desactivarByCodPers(mapa);

					cmpLocal.setEstId(Constantes.ACTIVO);
					cmpLocal.setCuserMod(usuario);
					cmpLocal.setFmod(new Timestamp(System.currentTimeMillis()));
				}
			} catch (Exception ex) {
				//Desactivamos los registros anteriores correspondientes al
				// personal
				T1456DAO dao = new T1456DAO();
				dao.desactivarByCodPers(mapa);

				//Creamos un nuevo registro
				cmpHome.create(codPers,
						new java.sql.Date(fecha.getTime()), usuario);
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
	public List buscarVacacionesPorFirmar(String dbpool, String codPers,
			String anno, String estado) throws IncompleteConversationalState,
			RemoteException {

		List listado = null;
		BeanMensaje beanM = new BeanMensaje();
		try {
			//prac-jcallo
			T1282DAO dao = new T1282DAO(dbpool);
			//listado = dao.findVacacionesPorFirmar(dbpool, codPers, anno, estado);
			Map params = new HashMap();
			params.put("cod_pers", codPers);
			params.put("anno", anno);
			params.put("estado", estado);		
			log.debug("dao.findVacacionesPorFirmar(params).... params:"+params);
			listado = dao.findVacacionesPorFirmar(params);
			log.debug("listado.size() : "+listado);
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return listado;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public List buscarVacacionesPorAnnoEstadoLicencia(String dbpool,
			String codPers, String anno, String estado, String tipo)
			throws IncompleteConversationalState, RemoteException {

		List listado = null;
		BeanMensaje beanM = new BeanMensaje();
		try {

			//prac-jcallo			
			T1282DAO detalleDAO = new T1282DAO(dbpool);
			Map params = new HashMap();
			params.put("cod_pers", codPers);
			params.put("anno_vac", anno);
			params.put("est_id", estado);
			params.put("licencia", tipo);
			log.debug("detalleDAO.findByCodPersAnnoVacEstIdLicencia(params)... params:"+params);
			listado = detalleDAO.findByCodPersAnnoVacEstIdLicencia(params);
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return listado;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList buscarSaldoVenta(String dbpool, String codPers)
			throws IncompleteConversationalState, RemoteException {

		ArrayList listado = null;
		BeanMensaje beanM = new BeanMensaje();
		try {

			T1281DAO vacaDAO = new T1281DAO();
			listado = vacaDAO.findByCodPersSaldoVenta(dbpool, codPers);

		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return listado;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList buscarSaldoAnno(String dbpool, String codPers,
			boolean saldoFavor, String anno, HashMap seguridad)
			throws IncompleteConversationalState, RemoteException {

		ArrayList listado = null;
		BeanMensaje beanM = new BeanMensaje();
		try {

			T1281DAO vacaDAO = new T1281DAO();
			listado = vacaDAO.findByCodPersSaldoAnno(dbpool, codPers,
					saldoFavor, anno, seguridad);

		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return listado;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public List buscarDetalleVacaciones(String dbpool, String codPers,
			String estado, String licencia, String anno, HashMap seguridad)
			throws IncompleteConversationalState, RemoteException {

		List listado = null;
		BeanMensaje beanM = new BeanMensaje();
		try {
			//prac-jcallo
			T1282DAO daot1282 = new T1282DAO(dbpool);
			Map params = new HashMap();
			params.put("cod_pers", codPers);
			params.put("estado", estado);
			params.put("licencia", licencia);
			params.put("anno_vac", anno);
			params.put("seguridad", seguridad);
			if (log.isDebugEnabled()) log.debug("daot1282.joinWithT02AndTipoMov(params)... params:"+params);
			//listado = daoDet.joinWithT99ByCodPersEstIdLicenciaAnhoFechaIni(dbpool, codPers, estado, licencia, anho, seguridad);
			listado = daot1282.joinWithT02AndTipoMov(params);
			
			//JRR - 08/05/2009 - Lo mismo que el codigo de Paco
			Map lvhm = new HashMap();
			Map lvDatos = new HashMap();
			Map reslv = new HashMap();
			String fecRegSolicitud = "";
			String mesfrs = "";
			String aniofrs = "";

			String numeroSol = "";
			String areaRef = "";
			String annoRef = "";
			Timestamp fechaTs = null;
			
			int mesint = 0;
			T1277DAO t1277 = new T1277DAO(dbpool);
			if (log.isDebugEnabled()) log.debug("listado: "+ listado);			
			
			if (listado.size()>0){}{
				  for(int ci=0; ci< listado.size() ;ci++){
					lvhm = (HashMap)listado.get(ci);
					if (log.isDebugEnabled()) log.debug("lvhm: "+ lvhm);
					//JRR - 27/04/2009
					numeroSol = ((lvhm.get("numero_ref")!=null && !lvhm.get("numero_ref").toString().trim().equals("0")) ? lvhm.get("numero_ref").toString().trim():"");
					areaRef = (lvhm.get("area_ref")!=null?lvhm.get("area_ref").toString().trim():"");
					//JRR - 24/06/2009					
					annoRef = (lvhm.get("anno_ref")!=null?lvhm.get("anno_ref").toString().trim():"");
					
					if ((numeroSol!=null && !numeroSol.equals("")) && (areaRef!=null && !areaRef.equals(""))
							&& (annoRef!=null && !annoRef.equals(""))){
						lvDatos.put("numero_ref",lvhm.get("numero_ref"));
						lvDatos.put("u_organ",lvhm.get("area_ref"));
						lvDatos.put("cod_pers",lvhm.get("cod_pers"));
						lvDatos.put("anno_ref",lvhm.get("anno_ref"));
						reslv = t1277.findFechaRegistroSolicitud(lvDatos);
						if (log.isDebugEnabled()) log.debug("reslv: "+ reslv);
						
						if (reslv!=null) {
							fechaTs = (Timestamp)reslv.get("fecha");
						} else {
							if (log.isDebugEnabled()) log.debug("No existe la solicitud en la BD");
							fechaTs = (Timestamp)lvhm.get("ffinicio");
						}
						
					} else {
						//Venta realizada por el administrador
						//FRD  08/05/2009 ya no fcreacion sino ffinicio
						//fechaTs = (Timestamp)lvhm.get("fcreacion");
						fechaTs = (Timestamp)lvhm.get("ffinicio");
					}
					//
					fecRegSolicitud = Utiles.timeToFecha(fechaTs);
					aniofrs = Utiles.dameAnho(fecRegSolicitud);
					mesfrs = fecRegSolicitud.substring(3,5);
					mesint = Integer.parseInt(mesfrs);

					if (mesint==1)  mesfrs = "Enero";     if (mesint==2)  mesfrs = "Febrero"; 
					if (mesint==3)  mesfrs = "Marzo";     if (mesint==4)  mesfrs = "Abril";   
					if (mesint==5)  mesfrs = "Mayo";	  if (mesint==6)  mesfrs = "Junio";   
					if (mesint==7)  mesfrs = "Julio";     if (mesint==8)  mesfrs = "Agosto";  
					if (mesint==9)  mesfrs = "Setiembre"; if (mesint==10) mesfrs = "Octubre"; 
					if (mesint==11) mesfrs = "Noviembre"; if (mesint==12) mesfrs = "Diciembre";
					   
					lvhm.put("fecPagoPlanilla",mesfrs + " de " + aniofrs);
				  }
				}  	
			//
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return listado;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public boolean programacionValida(String dbpool, String codPers,
			String anho, Timestamp fechaIni, int dias)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		boolean res = true;
		try {
			
			T1281DAO cabDAO = new T1281DAO();
			T1282DAO detDAO = new T1282DAO(dbpool);
			T99DAO codDAO = new T99DAO();
			T1456DAO reinDAO = new T1456DAO();
			
			int sigAnho = Integer.parseInt(Utiles.obtenerAnhoActual())+1;

		
		    if (Integer.parseInt(anho)<sigAnho){
		    	
		    	//obtenemos el saldo acumulado del aÃ±o
		    	int saldo_anho = cabDAO.findSaldoByAnho(dbpool, codPers, anho);

		    	//obtenemos la cantidad de dias de vacaciones programadas
		    	Map prms = new HashMap();
		    	prms.put("cod_pers", codPers);
		    	prms.put("anho", anho);
		    	prms.put("licencia", Constantes.VACACION_PROGRAMADA);
				//int vacAcum = detDAO.findByCodPersLicenciaAnhoVac(codPers, Constantes.VACACION_PROGRAMADA, anho);
		    	log.debug("detDAO.findByCodPersLicenciaAnhoVac(prms)... prms:"+prms);
		    	int vacAcum = detDAO.findByCodPersLicenciaAnhoVac(prms);
				
				if (saldo_anho==0 || saldo_anho < (vacAcum + dias)) {
					res = false;
				}		      
		    }
		    else{		    	
		    	
		    	//Validamos la fecha de ingreso del personal.
				HashMap hmVacGen = reinDAO.joinWithT02ByCodPersEstId(dbpool, codPers);
				Timestamp fIngreso = Utiles.stringToTimestamp(
						((String) hmVacGen.get("fecha")).substring(0, 6)
						+ sigAnho + " 00:00:00");

				
				if (fIngreso.compareTo(fechaIni) > 0) {
					res = false;
				}
				else{
					
			    	//obtenemos la cantidad de dias de vacaciones programadas
					Map prms = new HashMap();
			    	prms.put("cod_pers", codPers);
			    	prms.put("anho", anho);
			    	prms.put("licencia", Constantes.VACACION_PROGRAMADA);
					//int vacAcum = detDAO.findByCodPersLicenciaAnhoVac(dbpool, codPers, Constantes.VACACION_PROGRAMADA, anho);
			    	int vacAcum = detDAO.findByCodPersLicenciaAnhoVac(prms);
			
					
					String diasVaca = codDAO.findParamByCodTabCodigo(dbpool,
							Constantes.CODTAB_PARAMETROS_ASISTENCIA,
							Constantes.DIAS_VACACIONES);
					int numDiasVaca = diasVaca != null ? Integer.parseInt(diasVaca) : 0;
					
					if (numDiasVaca < (vacAcum + dias)) {
						res = false;
					}
					
				}
		    }
			
		} 
		catch (Exception e) {
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
	public void modificarVacacionProgramada(String dbpool, String numero,
			String codPers, String periodo, java.sql.Timestamp fechaIni,
			java.sql.Timestamp nvaFechaIni, java.lang.Integer dias,
			java.sql.Timestamp nvaFechaFin, String observacion, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {

			Timestamp fechaAct = new Timestamp(System.currentTimeMillis());

			if (fechaAct.after(nvaFechaIni)) {
				throw new IncompleteConversationalState(
						"El registro de vacaciones no puede ser modificado porque "
								+ " la nueva fecha de inicio es anterior a la fecha actual.");
			}
			//prac-jcallo
			//T1282CMPHome cmpDetalleHome = (T1282CMPHome) ServiceLocator.getInstance().getLocalHome(T1282CMPHome.JNDI_NAME);

			/*T1282CMPLocal cmpDetalleLocal = cmpDetalleHome
					.findByPrimaryKey(new T1282CMPPK(codPers, periodo, fechaIni));*/
			Map params = new HashMap();
			params.put("cod_pers", codPers);
			params.put("periodo", periodo);
			params.put("licencia", Constantes.VACACION_PROGRAMADA);//parametro agregado por cambio de llave de la t1282
			params.put("ffinicio", fechaIni);
			log.debug("t1282dao.findAllColumnsByKey(params)... params:"+params);
			T1282DAO t1282dao = new T1282DAO(dbpool);		
			Map mVacad = t1282dao.findAllColumnsByKey(params);

			if (mVacad.get("est_id").toString().trim().equals(
					Constantes.PROG_ACEPTADA)) {
				throw new IncompleteConversationalState(
						"Imposible modificar un registro de vacacion programada ya aceptado.");
			} else if (mVacad.get("est_id").toString().trim().equals(
					Constantes.PROG_EFECTUADA)) {
				throw new IncompleteConversationalState(
						"Imposible modificar un registro de vacacion programada convertido en vacacion efectiva.");
			} else {
				
				String anho = mVacad.get("anno_vac").toString();

				//Primero eliminamos la vacacion a modificar
				eliminarVacacion(dbpool,numero, codPers, periodo, Constantes.VACACION_PROGRAMADA ,fechaIni,
						usuario);

				//Luego creamos una nueva vacacion
				this.registrarVacacion(dbpool, codPers,
						Constantes.VACACION_PROGRAMADA, nvaFechaIni, anho,
						dias, nvaFechaFin, observacion,
						Constantes.INACTIVO, usuario, "", "", "");
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
	public List listaVentas(HashMap mapa)
			throws IncompleteConversationalState, RemoteException {
		
		List listado = null;
		try {
			T1282DAO t1282dao = new T1282DAO(mapa.get("dbpool").toString());
			listado = t1282dao.findPrimaryKeyAndId(mapa);//comprabar si esta bien o no el parametro cod_pers
		} catch (Exception e) {
			log.error(e,e);
			throw new IncompleteConversationalState(e.getMessage());
		}
		return listado;

	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public List buscarPendientes(HashMap mapa)
			throws IncompleteConversationalState, RemoteException {
		
		List listado = null;
		try {
			T1282DAO t1282dao = new T1282DAO(mapa.get("dbpool").toString());
			listado = t1282dao.findPendientes(mapa);//comprabar si esta bien o no el parametro cod_pers
		} catch (Exception e) {
			log.error(e,e);
			throw new IncompleteConversationalState(e.getMessage());
		}
		return listado;

	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public int totalDiasVendidosPorAnno(HashMap mapa)
			throws IncompleteConversationalState, RemoteException {
		
		int totalDias=0;
		mapa.put("anno", mapa.get("annoVac"));
		try {
			T1282DAO t1282dao = new T1282DAO(mapa.get("dbpool").toString());
			totalDias = t1282dao.findVentasTotXanio(mapa);
		} catch (Exception e) {
			log.error(e,e);
			throw new IncompleteConversationalState(e.getMessage());
		}
		return totalDias;
	}
	
	//METODOS PARA FRACCIONAMIENTO DE VACACIONES
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public int totalFraccionadoPorAnio(HashMap mapa)
			throws IncompleteConversationalState, RemoteException {
		
		int totalDias=0;
		mapa.put("anno", mapa.get("annoVac"));
		try {
			T1282DAO t1282dao = new T1282DAO(mapa.get("dbpool").toString());
			totalDias = t1282dao.findTotalFraccionadoPorAnio(mapa);
		} catch (Exception e) {
			log.error(e,e);
			throw new IncompleteConversationalState(e.getMessage());
		}
		return totalDias;
	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public int totalVentasEnSol(HashMap mapa)
			throws IncompleteConversationalState, RemoteException {
		
		int totalDias=0;
		try {
			T1282DAO t1282dao = new T1282DAO(mapa.get("dbpool").toString());
			totalDias = t1282dao.findVentasTotXanioEnSolicitud(mapa);
		} catch (Exception e) {
			log.error(e,e);
			throw new IncompleteConversationalState(e.getMessage());
		}
		return totalDias;
	}
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public int totalReprogEnSol(HashMap mapa)
			throws IncompleteConversationalState, RemoteException {
		
		int totalDias=0;
		try {
			T1282DAO t1282dao = new T1282DAO(mapa.get("dbpool").toString());
			totalDias = t1282dao.findTotalReprogEnSolicitud(mapa);
		} catch (Exception e) {
			log.error(e,e);
			throw new IncompleteConversationalState(e.getMessage());
		}
		return totalDias;
	}
	//FIN DE METODOS PARA FRACCIONAMIENTO DE VACACIONES
	//METODOS PARA ADELANTO DE VACACIONES
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public int numDiasDisponibleAdelanto(HashMap mapa)
			throws IncompleteConversationalState, RemoteException {
		log.debug("Entrando a numDiasDisponibleAdelanto");
		int diasDisponibles=0;
		try {
			T1282DAO t1282dao = new T1282DAO(mapa.get("dbpool").toString());
			T99DAO codigoDAO = new T99DAO();
			log.debug("anno:"+mapa.get("annoVac"));
			if(!mapa.get("annoVac").equals("")){
				int annoAnt = Integer.parseInt(mapa.get("annoVac").toString())-1;
				log.debug("fecIngreso:"+mapa.get("fecIngreso"));
				FechaBean fing=new FechaBean(mapa.get("fecIngreso").toString(),"dd/MM/yyyy");
				String fecInicio = annoAnt + "-"+fing.getMes()+"-"+fing.getDia();
				String fecIniSlash =  annoAnt+ "/"+fing.getMes()+"/"+fing.getDia();
				
				FechaBean hoy=new FechaBean();
				mapa.put("finicio", new FechaBean(fecInicio,"yyyy-MM-dd"));
				log.debug("Fechas:"+fecInicio+" "+hoy+" "+fing+"-"+fing.getMes());
				mapa.put("ffin", hoy);
				mapa.put("codPersAnte", "");
				
				//parámetro cantidad máxima permitido de licencia por enfermedad
				String diasEnfermedad = codigoDAO.findParamByCodTabCodigo(mapa.get("dbpool").toString(),Constantes.CODTAB_PARAMETROS_ASISTENCIA,Constantes.DIAS_ENFERMEDAD);
				if(log.isDebugEnabled()) log.debug("**** ProcesoFacade-generarVacaciones-diasEnfermedad: "+diasEnfermedad);//BORRAR
				int numDiasEnfermedad = diasEnfermedad != null ? Integer.parseInt(diasEnfermedad) : 0;
				
				String fecFinSlash = hoy.getAnho() + "/"+(Integer.parseInt(hoy.getMes())<9?"0"+(Integer.parseInt(hoy.getMes())+1):""+hoy.getMes())+"/"+hoy.getDia();
				mapa.put("finicio", fecIniSlash);
				mapa.put("ffin", fecFinSlash);
				log.debug("Fechas:"+fecIniSlash+" "+fecFinSlash);
				pe.gob.sunat.rrhh.asistencia.dao.T1272DAO t1272dao=new pe.gob.sunat.rrhh.asistencia.dao.T1272DAO(mapa.get("dbpool").toString());
				int diasLaborables = t1272dao.findDiasLaborablesByFiniFin(mapa);
				
				int diasInasistLicDesc=t1272dao.findDiasInasistLicDescByFiniFin(mapa);// dtarazona
				if (log.isDebugEnabled()) log.debug("Dias Inasist y lic desc: "+diasInasistLicDesc);
				int diasLabAlm=diasLaborables+diasInasistLicDesc;//dtarazona
				if (log.isDebugEnabled()) log.debug("Dias Laborables: "+diasLaborables);
				
				int diasLicEnferm = t1272dao.findDiasEnfermedadByFiniFin(mapa);
				
				//decrementamos el exceso de dias con licencia por enfermedad
				diasLaborables -= (diasLicEnferm>numDiasEnfermedad ? (diasLicEnferm-numDiasEnfermedad) : 0);
				
				int calc=(diasLaborables*30)/210;
				diasDisponibles=(int)(calc>30?30:calc);if(log.isDebugEnabled()) log.debug("DiasDisp."+diasDisponibles);
			}		
		} catch (Exception e) {
			log.error(e,e);
			throw new IncompleteConversationalState(e.getMessage());
		}
		return diasDisponibles;
	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList buscarSaldoGeneralPorAnno(String dbpool,String codPers,boolean saldoFavor, String anhoIni, String anhoFin)
			throws IncompleteConversationalState, RemoteException {
		
		ArrayList result = new ArrayList();
		try {
			//T1281DAO t1282dao = new T1282DAO(mapa.get("dbpool").toString());
			T1281DAO vacDAO = new T1281DAO();
			result = vacDAO.findByCodPersSaldoAnnoIniAnnoFin(dbpool, codPers, saldoFavor, anhoIni, anhoFin);
		} catch (Exception e) {
			log.error(e,e);
			throw new IncompleteConversationalState(e.getMessage());
		}
		return result;
	}
	
	/* 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
/*	public ArrayList convertirEfectiva(String dbpool, String numero,
			String codPers, String periodo, java.sql.Timestamp fechaIni,
			String annoRef, String areaRef, String numeroRef, String usuario,
			HashMap seguridad) throws IncompleteConversationalState,
			RemoteException {

		ArrayList mensajes = new ArrayList();		
		try {
			//prac-jcallo
			//T1282CMPHome cmpDetalleHome = (T1282CMPHome) ServiceLocator.getInstance().getLocalHome(T1282CMPHome.JNDI_NAME);
			/*T1282CMPLocal cmpDetalleLocal = cmpDetalleHome
					.findByPrimaryKey(new T1282CMPPK(codPers, periodo, fechaIni));*/
/*			T1282DAO t1282dao = new T1282DAO(dbpool);
			Map params = new HashMap();
			params.put("cod_pers", codPers);
			params.put("periodo", periodo);
			params.put("licencia", Constantes.VACACION_PROGRAMADA);//parametro agregado por tema de cambio de PK
			params.put("ffinicio", fechaIni);
			log.debug("t1282dao.findAllColumnsByKey(params)... params:"+params);
			Map mVacad = t1282dao.findAllColumnsByKey(params);

			if (!mVacad.get("licencia").toString().trim().equals(
					Constantes.VACACION_PROGRAMADA)) {
				mensajes.add("No se puede convertir la vacaci&oacute;n porque no es del tipo 'Programada'.");
			}

			if (!mVacad.get("est_id").toString().trim().equals(Constantes.PROG_ACEPTADA)) {
				mensajes.add("La vacaci&oacute;n programada no ha sido aceptada.");
			}

			if (mensajes.isEmpty()) {
				
				HashMap hmPermit = Utiles.diasVacPermitidos();

				try {
					//Creamos el registro de la vacacion efectiva
					// correspondiente.
					this.registrarNvaVacacion(dbpool, codPers,
							Constantes.VACACION, (Timestamp)mVacad.get("ffinicio"),
							(Integer)mVacad.get("dias"), "NADA", (Timestamp)mVacad.get("ffin"),
							mVacad.get("observ").toString(),
							Constantes.ACTIVO, usuario, annoRef, areaRef,
							numeroRef, hmPermit, seguridad,false);				
					
					//Pasamos el estado de la vacacion a "Efectuada".
					//prac-jcallo
					/*cmpDetalleLocal.setEstId(Constantes.PROG_EFECTUADA);
					cmpDetalleLocal.setCuserMod(usuario);
					cmpDetalleLocal.setFmod(new Timestamp(System
							.currentTimeMillis()));
					cmpDetalleLocal.setAnnoRef(annoRef);
					cmpDetalleLocal.setAreaRef(areaRef);
					if (!numeroRef.trim().equals("")) {
						cmpDetalleLocal.setNumeroRef(new Short(numeroRef));
					}*/
					//prac-jcallo
					//columnas a modificar
/*					Map datos = new HashMap();
					Map columns = new HashMap();
					columns.put("est_id", Constantes.PROG_EFECTUADA);
					columns.put("anno_ref", annoRef);
					columns.put("area_ref", areaRef);
					if(!numeroRef.trim().endsWith("")) columns.put("numero_ref", new Integer(numeroRef));
					columns.put("cuser_mod", usuario);
					columns.put("fmod", new FechaBean().getTimestamp());
					
					datos.put("columns", columns);
					//datos de la llave primaria
					datos.put("cod_pers", codPers);
					datos.put("periodo", periodo);
					datos.put("licencia", Constantes.VACACION_PROGRAMADA);//parametro agregado por tema de cambio de PK t1282
					datos.put("ffinicio", fechaIni);
					log.debug("t1282dao.updateCustomColumns(datos)... datos:"+datos);
					t1282dao.updateCustomColumns(datos);
				} catch (Exception ex) {
					mensajes.add(ex.getMessage());
				}
			}
		} catch (Exception e) {
			log.error(e,e);
			mensajes.add(e.getMessage());
		}

		return mensajes;
	} */
	

/* JRR - PROGRAMACION - 04/06/2010 */	
	/**
	 * Metodo que Inserta las Vacaciones Efectivas y Actualiza la Programacion de Vacaciones
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public List convertirEfectiva(String dbpool, String numero,
			String codPers, String periodo, java.sql.Timestamp fechaIni,
			String annoRef, String areaRef, String numeroRef, String usuario,
			Map seguridad) throws IncompleteConversationalState,
			RemoteException {
		MensajeBean beanM = new MensajeBean();
		List mensajes = new ArrayList();		
		try {
			//prac-jcallo
			T1282DAO t1282dao = new T1282DAO(dbpool);
			Map params = new HashMap();
			params.put("cod_pers", codPers);
			params.put("periodo", periodo);
			params.put("licencia", Constantes.VACACION_PROGRAMADA);//parametro agregado por tema de cambio de PK
			params.put("ffinicio", fechaIni);
			
			Map mVacad = new HashMap();
			if (periodo==null) {
				T1277DAO t1277dao = new T1277DAO(dbpool);
				params.put("licencia", Constantes.VACACION);
				params.put("anno", annoRef.trim());
				params.put("numero", numeroRef.trim());
				params.put("u_organ", areaRef.trim());
				mVacad = t1277dao.findAllColumnsByKey(params);
				
			} else
			{
				mVacad = t1282dao.findAllColumnsByKey(params);

				if (!mVacad.get("licencia").toString().trim().equals(
						Constantes.VACACION_PROGRAMADA)) {
					mensajes.add("No se puede convertir la vacaci&oacute;n porque no es del tipo 'Programada'.");
				}
			}
			
			String anioVac=mVacad.get("anno_vac").toString();//COMSA
			if (mensajes.isEmpty()) {
				
				Map hmPermit = Utiles.diasVacPermitidos();

				try {
					//Creamos el registro de la vacacion efectiva
					// correspondiente.
					this.registrarNvaVacacion(dbpool, codPers,
							Constantes.VACACION, (Timestamp)mVacad.get("ffinicio"),
							(Integer)mVacad.get("dias"), anioVac, (Timestamp)mVacad.get("ffin"),//COMSA - anioVac x NADA
							mVacad.get("observ").toString(),
							Constantes.ACTIVO, usuario, annoRef, areaRef,
							numeroRef, (HashMap)hmPermit, (HashMap)seguridad,false);				
					
					//Pasamos el estado de la vacacion a "Efectuada".
					//prac-jcallo
					//columnas a modificar
					Map datos = new HashMap();
					Map columns = new HashMap();
					columns.put("est_id", Constantes.PROG_EFECTUADA);
					columns.put("anno_ref", annoRef);
					columns.put("area_ref", areaRef);
					//JR 27/05/2009 
					//if(!numeroRef.trim().endsWith("")) columns.put("numero_ref", new Integer(numeroRef));
					if (numeroRef!=null&&!numeroRef.trim().equals("0")){
					  columns.put("numero_ref", new Integer(numeroRef));
					}else{
					  columns.put("numero_ref", null);	
					}
					columns.put("cuser_mod", usuario);
					columns.put("fmod", new FechaBean().getTimestamp());
					datos.put("columns", columns);
					//datos de la llave primaria
					datos.put("cod_pers", codPers);
					datos.put("periodo", periodo);
					datos.put("licencia", Constantes.VACACION_PROGRAMADA);//parametro agregado por tema de cambio de PK t1282
					datos.put("ffinicio", fechaIni);
					t1282dao.updateCustomColumnsSinPeriodo(datos);
				} catch (Exception ex) {
				   log.error(ex, ex);
					mensajes.add(ex.getMessage());
				}
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
	 * Metodo que Inserta las Vacaciones Efectivas y Actualiza la Programacion de Vacaciones
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public List convertirEfectivaAdelanto(String dbpool, String numero,
			String codPers, String periodo, java.sql.Timestamp fechaIni,
			String annoRef, String areaRef, String numeroRef, String usuario,String esAdelanto,
			Map seguridad) throws IncompleteConversationalState,
			RemoteException {
		MensajeBean beanM = new MensajeBean();
		List mensajes = new ArrayList();		
		try {
			//prac-jcallo
			T1282DAO t1282dao = new T1282DAO(dbpool);
			Map params = new HashMap();
			params.put("cod_pers", codPers);
			params.put("periodo", periodo);
			params.put("licencia", Constantes.VACACION_PROGRAMADA);//parametro agregado por tema de cambio de PK
			params.put("ffinicio", fechaIni);
			
			Map mVacad = new HashMap();
			if (periodo==null) {
				T1277DAO t1277dao = new T1277DAO(dbpool);
				params.put("licencia", Constantes.VACACION);
				params.put("anno", annoRef.trim());
				params.put("numero", numeroRef.trim());
				params.put("u_organ", areaRef.trim());
				mVacad = t1277dao.findAllColumnsByKey(params);
				
			} else
			{
				mVacad = t1282dao.findAllColumnsByKey(params);

				if (!mVacad.get("licencia").toString().trim().equals(
						Constantes.VACACION_PROGRAMADA)) {
					mensajes.add("No se puede convertir la vacaci&oacute;n porque no es del tipo 'Programada'.");
				}
			}
			
			String anioVac=mVacad.get("anno_vac").toString();//COMSA
			if (mensajes.isEmpty()) {
				
				Map hmPermit = Utiles.diasVacPermitidos();

				try {
					//Creamos el registro de la vacacion efectiva
					//hmPermit.put("esAdelanto", esAdelanto);
					// correspondiente.
					boolean esAdVacacional=false;
					if(esAdelanto.equals("1")) esAdVacacional=true;
					this.registrarNvaVacacion(dbpool, codPers,
							Constantes.VACACION, (Timestamp)mVacad.get("ffinicio"),
							(Integer)mVacad.get("dias"), anioVac, (Timestamp)mVacad.get("ffin"),//COMSA - anioVac x NADA
							mVacad.get("observ").toString(),
							Constantes.ACTIVO, usuario, annoRef, areaRef,
							numeroRef, (HashMap)hmPermit, (HashMap)seguridad,esAdVacacional);				
					
					//Pasamos el estado de la vacacion a "Efectuada".
					//prac-jcallo
					//columnas a modificar
					Map datos = new HashMap();
					Map columns = new HashMap();
					columns.put("est_id", Constantes.PROG_EFECTUADA);
					columns.put("anno_ref", annoRef);
					columns.put("area_ref", areaRef);
					//JR 27/05/2009 
					//if(!numeroRef.trim().endsWith("")) columns.put("numero_ref", new Integer(numeroRef));
					if (numeroRef!=null&&!numeroRef.trim().equals("0")){
					  columns.put("numero_ref", new Integer(numeroRef));
					}else{
					  columns.put("numero_ref", null);	
					}
					columns.put("cuser_mod", usuario);
					columns.put("fmod", new FechaBean().getTimestamp());
					datos.put("columns", columns);
					//datos de la llave primaria
					datos.put("cod_pers", codPers);
					datos.put("periodo", periodo);
					datos.put("licencia", Constantes.VACACION_PROGRAMADA);//parametro agregado por tema de cambio de PK t1282
					datos.put("ffinicio", fechaIni);
					t1282dao.updateCustomColumnsSinPeriodo(datos);
				} catch (Exception ex) {
				   log.error(ex, ex);
					mensajes.add(ex.getMessage());
				}
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
	 * Metodo que Inserta las Vacaciones Efectivas por Matrimonio
	 * Actualiza la Programacion de Vacaciones.
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public List convertirEfectivaMatrimonio(String dbpool, HashMap solicitud, String usuario) throws IncompleteConversationalState,
			RemoteException {
		if(log.isDebugEnabled()) log.debug("method: convertirEfectivaMatrimonio");
		log.debug("solicitud: "+solicitud);
		
		String codPers = (String) solicitud.get("codPers");
		String periodo = (String) solicitud.get("periodo");
		String annoVac = (String) solicitud.get("annoVac");
		String uorgan = (String) solicitud.get("uorgan");
		String asunto = (String) solicitud.get("asunto");
		java.sql.Timestamp fechaIni = (java.sql.Timestamp) solicitud.get("ffinicio");
		java.sql.Timestamp fechaFin = (java.sql.Timestamp) solicitud.get("ffin");
		String annoRef = solicitud.get("anno")!=null?((String)solicitud.get("anno")).trim():null;
		String areaRef = solicitud.get("numero")!=null?((String)solicitud.get("uorgan")).trim():null;//si no hay un numero entonces area_ref es null
		String numeroRef = solicitud.get("numero")!=null?((String)solicitud.get("numero")).trim():null;
		String dias = ((String)solicitud.get("dias"));
		
		MensajeBean beanM = new MensajeBean();
		List mensajes = new ArrayList();		
		try {
			//1. Desactivar programadas
			T1282DAO vacacionesDetalleDAO = new T1282DAO(dbpool);
			Map prmProgramadas = new HashMap();
			prmProgramadas.put("cod_pers", codPers);
			prmProgramadas.put("est_id", Constantes.PROG_ACEPTADA);
			prmProgramadas.put("licencia", Constantes.VACACION_PROGRAMADA);
			log.debug("prmProgramadas: "+prmProgramadas);
			List programadas = vacacionesDetalleDAO.findVacacionesProgramadasByParams(prmProgramadas);
			log.debug("programadas: "+programadas);
			
			if(programadas != null)
			for(int i=0; i<programadas.size(); i++){
				HashMap programada = (HashMap)programadas.get(i);
				programada.put("anno_ref", annoRef);
				programada.put("area_ref", areaRef);
				programada.put("numero_ref", numeroRef);
				programada.put("fmod", new FechaBean().getTimestamp());
				programada.put("cuser_mod", usuario);
				programada.put("estado_new", Constantes.PROG_PROGRAMADA);
				programada.put("ind_matri", Constantes.ACTIVO);
				log.debug("programada: "+programada);
				vacacionesDetalleDAO.actualizaProgramacion(programada);
			}
			
			//2. Insert pseudo-programada
			Map pseuProgramada = new HashMap();
			pseuProgramada.put("cod_pers", codPers);
			pseuProgramada.put("periodo", periodo);
			pseuProgramada.put("licencia", Constantes.VACACION_PROGRAMADA);
			pseuProgramada.put("ffinicio", fechaIni);
			pseuProgramada.put("anno_vac", annoVac);
			pseuProgramada.put("ffin", fechaFin);
			pseuProgramada.put("dias", dias);
			pseuProgramada.put("u_organ", uorgan);
			pseuProgramada.put("anno_ref", annoRef);
			pseuProgramada.put("area_ref", areaRef);
			pseuProgramada.put("numero_ref", numeroRef);
			pseuProgramada.put("observ", asunto);
			pseuProgramada.put("est_id", Constantes.PROG_EFECTUADA);
			pseuProgramada.put("fcreacion", new FechaBean().getTimestamp());
			pseuProgramada.put("cuser_crea", usuario);
			pseuProgramada.put("ind_matri", Constantes.ACTIVO);
			pseuProgramada.put("ind_conv", Constantes.INACTIVO);
			log.debug("prmProgramada: "+pseuProgramada);
			vacacionesDetalleDAO.insertarVacacionesDetalle(pseuProgramada);

			//3. Insertar vacación
			Map prmVacacion = pseuProgramada;
			prmVacacion.remove("ind_conv");
			prmVacacion.put("licencia", Constantes.VACACION);
			prmVacacion.put("est_id", Constantes.PROG_ACEPTADA);
			prmVacacion.put("fcreacion", new FechaBean().getTimestamp());
			prmVacacion.put("cuser_crea", usuario);
			log.debug("prmVacacion: " + prmVacacion);
			vacacionesDetalleDAO.insertarVacacionesDetalle(prmVacacion);
			
			//4. Cabecera de vacaciones
			pe.gob.sunat.rrhh.asistencia.dao.T1281DAO vacacionesCabeceraDAO = new pe.gob.sunat.rrhh.asistencia.dao.T1281DAO(dbpool);
			Map prmCabecera = new HashMap();
			prmCabecera.put("cod_pers", codPers);
			prmCabecera.put("anno", annoVac);
			prmCabecera.put("dias", new Integer(0));  
			prmCabecera.put("saldo", new Integer(-1*Integer.parseInt(dias))); //AMVS-09-2012
			prmCabecera.put("saldo_temp", new Integer(0));
			prmCabecera.put("cuser_crea", usuario);
			prmCabecera.put("fcreacion", new Timestamp(System.currentTimeMillis()));
			log.debug("prmCabecera: "+prmCabecera);
			vacacionesCabeceraDAO.insertarCabVacacion(prmCabecera);
				
		}catch(DAOException e){
			log.error(e, e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente, posible error en la BD");
			throw new FacadeException(this, beanM);
		}catch(Exception e){
			log.error(e, e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new FacadeException(this, beanM);
		}
		return mensajes;
	}
	

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public List buscarVacacionesTipoAnho(HashMap mapa)
			throws IncompleteConversationalState, RemoteException {
		
		List listado = null;
		try {
			
			T1282DAO t1282dao = new T1282DAO(mapa.get("dbpool").toString());
			listado = t1282dao.joinWithT02ByCritValAnhoLicencia(mapa);
			
		} catch (Exception e) {
			log.error(e,e);
			throw new IncompleteConversationalState(e.getMessage());
		}
		return listado;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void calificarVacProg(String dbpool, String[] params,
			ArrayList lista, String tipoaut, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {

			HashMap hVac = null;

			//T1282CMPHome cmpHome = (T1282CMPHome) ServiceLocator.getInstance().getLocalHome(T1282CMPHome.JNDI_NAME);

			for (int i = 0; i < lista.size(); i++) {

				hVac = (HashMap) lista.get(i);

				String estado = params[i];
				if (tipoaut.equals("1") && estado!=null && !estado.equals(Constantes.PROG_EFECTUADA)){
					estado = Constantes.PROG_ACEPTADA;
				}
				
				if (estado != null) {
					if (hVac.get("est_id") == null || 
						!((String) hVac.get("est_id")).trim().equals(estado)) {

						try {
							//asignar autorizacion
							/*T1282CMPLocal cmpLocal = cmpHome
									.findByPrimaryKey(new T1282CMPPK(
											(String) hVac.get("cod_pers"),
											(String) hVac.get("periodo"),
											Utiles
													.stringToTimestamp((String) hVac
															.get("ffinicio")
															+ " 00:00:00")));*/
							T1282DAO t1282dao = new T1282DAO(dbpool);
							Map datos = new HashMap();
							Map columns = new HashMap();
							columns.put("est_id", estado);
							columns.put("cuser_mod", usuario);
							columns.put("fmod", new FechaBean().getTimestamp());							
							datos.put("columns", columns);
							//datos de llave primaria
							datos.put("cod_pers", (String) hVac.get("cod_pers"));
							datos.put("periodo", (String) hVac.get("periodo"));
							datos.put("licencia", Constantes.VACACION_PROGRAMADA);//parametros agregado por cambio de PK T1282
							datos.put("ffinicio", new FechaBean((String) hVac.get("ffinicio")).getTimestamp());
							log.debug("t1282dao.updateCustomColumns(datos)... datos:"+datos);
							t1282dao.updateCustomColumns(datos);
							/*cmpLocal.setEstId(estado);
							cmpLocal.setCuserMod(usuario);
							cmpLocal.setFmod(new Timestamp(System
									.currentTimeMillis()));*/

						} 
						catch (Exception ex) {
							log.debug("Error al autorizar papeleta...");
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

		return;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void generarVacaciones(HashMap mapa, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {
			QueueDAO qd = new QueueDAO();
			qd.encolaProceso(ProcesoFacadeHome.JNDI_NAME,
					"generarVacaciones", mapa, usuario);
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
	public ArrayList reprogramarVacacion(HashMap datos) 
		throws IncompleteConversationalState, RemoteException {

		ArrayList mensajes = new ArrayList();
		try {
						
			String codPers = (String)datos.get("codPers");
			String periodo = (String)datos.get("periodo");
			String fechaIni = (String)datos.get("fechaVac");
			
			//T1282CMPHome cmpDetalleHome = (T1282CMPHome) ServiceLocator.getInstance().getLocalHome(T1282CMPHome.JNDI_NAME);			
			/*T1282CMPLocal cmpDetalleLocal = cmpDetalleHome
					.findByPrimaryKey(new T1282CMPPK(codPers, periodo, Utiles.stringToTimestamp(fechaIni+" 00:00:00")));*/
			T1282DAO t1282dao = new T1282DAO(datos.get("dbpool").toString());
			Map params = new HashMap();
			params.put("cod_pers", codPers);
			params.put("periodo", periodo);
			params.put("licencia", Constantes.VACACION_PROGRAMADA);//parametro agregado de acuerdo al cambio de PK T1282
			params.put("ffinicio", new FechaBean(fechaIni).getTimestamp());
			log.debug("t1282dao.findAllColumnsByKey(params)... params:"+params);
			Map mVacad = t1282dao.findAllColumnsByKey(params);

			String licencia = mVacad.get("licencia").toString().trim();// cmpDetalleLocal.getLicencia().trim();
			String estado = mVacad.get("est_id").toString().trim();//cmpDetalleLocal.getEstId().trim();
			
			if (!licencia.equals(Constantes.VACACION_PROGRAMADA) &&
				!licencia.equals(Constantes.REPROGRAMACION_VACACION)) {
				mensajes.add("No se puede reprogramar la vacaci&oacute;n porque no es del tipo 'Programada'.");
			}
			
			if (
				(licencia.equals(Constantes.VACACION_PROGRAMADA) && !estado.equals(Constantes.PROG_ACEPTADA)) ||
				(licencia.equals(Constantes.REPROGRAMACION_VACACION) && !estado.equals(Constantes.PROG_PROGRAMADA))	
				) {
				mensajes.add("La vacaci&oacute;n programada no ha sido aceptada.");
			}

			String observacion = mVacad.get("observ").toString();//cmpDetalleLocal.getObserv();
			String usuario = mVacad.get("cuser_crea").toString();//cmpDetalleLocal.getCuserCrea();
			String uOrg = mVacad.get("u_organ").toString();//cmpDetalleLocal.getUOrgan();
			String annoVac = mVacad.get("anno_vac").toString();//cmpDetalleLocal.getAnnoVac();			
			
			if (mensajes.isEmpty()) {
				
				SolicitudFacadeHome facadeHome = (SolicitudFacadeHome) ServiceLocator
				.getInstance().getRemoteHome(
						SolicitudFacadeHome.JNDI_NAME,
						SolicitudFacadeHome.class);
				SolicitudFacadeRemote facadeRemote = facadeHome.create();
				
				HashMap mapa = new HashMap();
				mapa.put("dbpool",(String)datos.get("dbpool"));
				mapa.put("userOrig",(String)datos.get("userOrig"));
				mapa.put("nvaFechaIni",Utiles.timeToFecha((Timestamp)datos.get("ffinicio")));
				mapa.put("fechaIni",(String)datos.get("fechaVac"));
				mapa.put("fechaFin",(String)datos.get("fechaFinVac"));
				mapa.put("numDias",(String)datos.get("dias"));
				mapa.put("numero",(String)datos.get("numero"));
				
				mensajes = facadeRemote.validaReprogramacion(mapa);
				
				if (mensajes.isEmpty()){
					
					//eliminamos el detalle anterior
					//cmpDetalleLocal.remove();
					//prac-jcallo
					log.debug("t1282dao.deleteByPrimaryKey(params)... params:"+params);
					t1282dao.deleteByPrimaryKey(params);
				
					//registramos una nueva vacacion programada
					//prac-jcallo
					
					/*cmpDetalleHome.create( 
							codPers, 
							periodo, 
							(Timestamp)datos.get("ffinicio"), 
							Constantes.REPROGRAMACION_VACACION,
							annoVac, 
							new Integer((String)datos.get("dias")), 
							(Timestamp)datos.get("ffin"), 
							Constantes.PROG_ACEPTADA, 
							observacion, 
							usuario,
							uOrg, 
							(String)datos.get("anno"), 
							(String)datos.get("uorgan"),
							(String)datos.get("numero"));*/
					Map dtos = new HashMap();
					dtos.put("cod_pers", codPers);
					dtos.put("periodo", periodo);
					dtos.put("licencia", Constantes.REPROGRAMACION_VACACION);
					dtos.put("ffinicio", (Timestamp)datos.get("ffinicio"));
					dtos.put("anno_vac", annoVac);
					dtos.put("ffin", (Timestamp)datos.get("ffin"));
					dtos.put("dias", new Integer((String)datos.get("dias")));
					dtos.put("anno", "");
					dtos.put("u_organ", uOrg);
					dtos.put("anno_ref", (String)datos.get("anno"));
					dtos.put("area_ref", (String)datos.get("uorgan"));
					dtos.put("numero_ref", (datos.get("numero")!=null&&!datos.get("numero").toString().trim().equals("0"))?new Integer((String)datos.get("numero")):null);
					dtos.put("observ", observacion);
					dtos.put("est_id", Constantes.PROG_ACEPTADA);
					dtos.put("fcreacion", new FechaBean().getTimestamp());
					dtos.put("cuser_crea", usuario);
					log.debug("t1282dao.insertarVacacionesDetalle(dtos)... dtos:"+dtos);
					t1282dao.insertarVacacionesDetalle(dtos);
					
				}
								
			}
			
		} catch (Exception e) {
			log.error(e,e);
			mensajes.add(e.getMessage());
		}

		return mensajes;
	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public ArrayList postergarVacacion(HashMap datos) 
		throws IncompleteConversationalState, RemoteException {

		ArrayList mensajes = new ArrayList();		
		try {

			String codPers = (String)datos.get("codPers");
			String periodo = (String)datos.get("periodo");
			
			String licencia = (String)datos.get("licencia");
			String fechaIni = (String)datos.get("fechaIni");
			String fechaFin = (String)datos.get("fechaFin");			
			
			T1281CMPHome cmpHome = (T1281CMPHome) sl.getLocalHome(T1281CMPHome.JNDI_NAME);
							
				SolicitudFacadeHome facadeHome = (SolicitudFacadeHome) ServiceLocator
				.getInstance().getRemoteHome(
						SolicitudFacadeHome.JNDI_NAME,
						SolicitudFacadeHome.class);
				SolicitudFacadeRemote facadeRemote = facadeHome.create();
				
				T1282DAO t1282dao = new T1282DAO(datos.get("dbpool"));
				
				ArrayList vacSolProg=(ArrayList)datos.get("d1");
				if(log.isDebugEnabled()) log.debug("vacSolProg:"+vacSolProg);
				ArrayList vacSolReprog=(ArrayList)datos.get("d2");
				if(log.isDebugEnabled()) log.debug("vacSolReprog:"+vacSolReprog);
				String vr="",vp="";
				
				HashMap mapa = new HashMap();
				mapa.put("dbpool",(String)datos.get("dbpool"));
				mapa.put("userOrig",(String)datos.get("userOrig"));
				for(int i=0;i<vacSolReprog.size();i++)
				{
					HashMap vacSolReprog1=(HashMap)vacSolReprog.get(i);					
					vr=vr+vacSolReprog1.get("ann_sol").toString()+"-"+vacSolReprog1.get("obs_sustento_obs").toString()+"-"+vacSolReprog1.get("fec_permiso").toString()+"&";
				}
				if(log.isDebugEnabled()) log.debug("vr:"+vr);
				mapa.put("reprogramadas", vr);
				for(int i=0;i<vacSolProg.size();i++)
				{
					HashMap vacSolProg1=(HashMap)vacSolProg.get(i);					
					vp=vp+vacSolProg1.get("anno_vac").toString()+"-"+vacSolProg1.get("dias").toString()+"-"+vacSolProg1.get("ffinicio").toString()+"&";
				}
				if(log.isDebugEnabled()) log.debug("vr:"+vp);
				mapa.put("programadasSel", vp);
				
				if(datos.get("aprobar")!=null){
					mapa.put("codPers", codPers);
					mapa.put("aprobar", "1");
				}
				//String nvaFechaIni = Utiles.timeToFecha((Timestamp)datos.get("ffinicio"));
				//String numDias = (String)datos.get("dias");								
				mensajes = facadeRemote.validaPostergacion(mapa);
				String UO="",usu=(String)datos.get("userOrig");
				if (mensajes.isEmpty()){
					
					for(int i=0;i<vacSolProg.size();i++){
						
						HashMap vacSolProg1=(HashMap)vacSolProg.get(i);	
						Map params = new HashMap();
						
						params.put("cod_pers", codPers);
						params.put("periodo", periodo);
						params.put("licencia","49");// licencia);//parametro agregado por tema de  PK T1282
						params.put("ffinicio", new FechaBean(vacSolProg1.get("ffinicio").toString()).getTimestamp());
						params.put("dias", new Integer(vacSolProg1.get("dias").toString()));
						params.put("cuser_mod", (String)datos.get("cuser_mod"));
						log.debug("cuserMod:"+(String)datos.get("cuser_mod"));
						Map mVacad = t1282dao.findAllColumnsByKey(params);
						log.debug("t1282dao.findAllColumnsByKey(params)... params:"+mVacad);
						
						log.debug("editar:"+params);
						t1282dao.updateByPrimaryKeyAndDias(params);//params ya tiene los datos de la llave primaria lineas arriba
					}					
					//Validamos si existe alguna vacacion programada con los misos datos
					int totalAdelanto=0;
					String annoVac="";
					for(int i=0;i<vacSolReprog.size();i++){
						
						HashMap vacSolReprog1=(HashMap)vacSolReprog.get(i);
						String mes="";
						Calendar c1=Calendar.getInstance();
						if(c1.get(Calendar.MONTH)+1<10)
							mes="0"+(c1.get(Calendar.MONTH)+1);
						else
							mes = ""+(c1.get(Calendar.MONTH)+1);						
						String annio = Integer.toString(c1.get(Calendar.YEAR));
						String periodoReg=""+annio+mes;
						if(log.isDebugEnabled()) log.debug("Periodo Nuevo:"+periodoReg);
						Map prms = new HashMap();
						prms.put("cod_pers", codPers);
						prms.put("periodo", periodoReg);
						prms.put("licencia", Constantes.VACACION_PROGRAMADA);
						prms.put("ffinicio", new FechaBean(vacSolReprog1.get("fec_permiso").toString()).getTimestamp());
						prms.put("anno_vac", vacSolReprog1.get("ann_sol"));
						log.debug("Año Vacacional:"+vacSolReprog1.get("ann_sol"));
						prms.put("ffin", new FechaBean(Utiles.dameFechaSiguiente(vacSolReprog1.get("fec_permiso").toString(), Integer.parseInt(vacSolReprog1.get("obs_sustento_obs").toString())-1)).getTimestamp());
						prms.put("dias", new Integer(vacSolReprog1.get("obs_sustento_obs").toString()));
						prms.put("anno", "");
						prms.put("u_organ", UO);
						prms.put("anno_ref", (String)datos.get("anno"));
						prms.put("area_ref", (String)datos.get("uorgan"));
						prms.put("numero_ref", (datos.get("numero")!=null&&!datos.get("numero").toString().trim().equals("0"))?new Integer((String)datos.get("numero")):null);
						prms.put("observ", "");
						prms.put("est_id", Constantes.ACTIVO);
						prms.put("ind_conv", Constantes.INACTIVO);
						prms.put("fcreacion", new FechaBean().getTimestamp());
						prms.put("cuser_crea", (String)datos.get("cuser_mod"));	
						prms.put("cuser_mod", (String)datos.get("cuser_mod"));
						log.debug("t1282dao.insertarVacacionesDetalle(prms)... prms:"+prms);
						
						List existePK=(ArrayList)t1282dao.findPrimaryKey(prms);
						if(existePK.size()>0){
							prms.put("editAnnoVac",vacSolReprog1.get("ann_sol"));
							t1282dao.updateVacacionExistente(prms);
							log.debug("Editado al aprobar las nuevas programaciones:"+i+":"+prms);
						}							
						else
						{
							t1282dao.insertarVacacionesDetalle(prms);
							log.debug("Insertado :"+i+":"+prms);
						}	
						
						//Verificar si hay adelanto de vacaciones						
						if(Integer.parseInt(vacSolReprog1.get("hor_ini_permiso").toString().trim())>0){
							totalAdelanto=totalAdelanto+Integer.parseInt(vacSolReprog1.get("obs_sustento_obs").toString());
							annoVac=vacSolReprog1.get("ann_sol").toString();
						}
					}
				}			
		} catch (Exception e) {
			log.error(e,e);
			mensajes.add(e.getMessage());
		}
		return mensajes;
	}
	
	
	/****** JRR - 05/12/2008 ********/
	/**
	 * Devuelve una lista que contiene las vacaciones programadas de un trabajador
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 *
	 * @param mapa Map
	 * @return Map EBV 11/03/2009 se cambia a MAPA
	 * @throws FacadeException
	 */
	public Map buscarProgramacionTrabajador(Map mapa) throws FacadeException{

		List lista = new ArrayList();
		List lanno = new ArrayList();
		Map programacion = new HashMap();
		String ind_convenio = "0";
		DataSource ds = sl.getDataSource("java:comp/env/jdbc/dcsp");
		try{
			T1282DAO t1282dao = new T1282DAO(ds);
			if (!"1".equals(mapa.get("controlEditable").toString().trim())) {
				lista = t1282dao.findByCodPersAnnoVacEstIdLicencia(mapa);
			} else {
				
				T3886DAO t3886dao = new T3886DAO(ds);
				Map convenio = t3886dao.findConvenioVigente(mapa);
				pe.gob.sunat.rrhh.asistencia.dao.T1281DAO t1281dao = new pe.gob.sunat.rrhh.asistencia.dao.T1281DAO(ds);
				if (convenio!=null) {
					mapa.put("anno_vac",convenio.get("per_convenio").toString().trim());
					mapa.put("saldo","");
					mapa.put("anho",convenio.get("per_convenio").toString().trim());
					lanno = t1281dao.findByCodPersSaldoAnno(mapa);
					
					lista = t1282dao.findByCodPersAnnoVacEstIdLicencia(mapa);
					ind_convenio = "1";

				}  else
				{
					
					lanno = t1281dao.findByCodPersSaldoAnno(mapa);
					if (lanno!=null && lanno.size()>0) {
						mapa.put("anno_vac",((Map)lanno.get(0)).get("anno").toString().trim());
						lista = t1282dao.findByCodPersAnnoVacEstIdLicencia(mapa);
						if (lista!=null && lista.size()>0) {
							ind_convenio = "1";
						}
					}
				}

			}
			
		} catch(Exception e){
			log.error(e);
			throw new FacadeException(this,e.getMessage());
		} finally{
		}
		programacion.put("lista",lista);
		programacion.put("lanno",lanno);
		programacion.put("ind_convenio",ind_convenio);

		return programacion; 
	}

	
	/****** FRD - 20/04/2009 ********/
	 /**
	  * Devuelve una lista que contiene las vacaciones vencidas y no gozadas de un trabajador
	  * 
	  * @ejb.interface-method view-type="remote"
	  * @ejb.transaction type="NotSupported"
	  *
	  * @param datos Map
	  * @return resul List
	  * @throws FacadeException
	  */
	 public List validarVacacionesVencidas(Map datos) throws FacadeException {
	    List resul = null;
	    String CodPers = "";
	  try {
	    CodPers = (String)datos.get("CodPers");
	    pe.gob.sunat.rrhh.dao.T99DAO t99DAO = new pe.gob.sunat.rrhh.dao.T99DAO(ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dcsp"));
	    resul = t99DAO.findT99T1281T02Codper(CodPers); 
	  } catch (Exception e) {
	   log.error(e,e);
	   throw new FacadeException(this,e.getMessage());
	  }
	  return resul;
	 }

	 
/* JRR - PROGRAMACION - 17/05/2010 */	 
		/**
		 * Genera La consulta de Vacaciones
		 * @ejb.interface-method view-type="remote"
		 * @ejb.transaction type="NotSupported"
		 */
		public Map generarConsultaVacaciones(HashMap paramSaldos, HashMap paramVta, HashMap paramVac, HashMap paramProg)
		throws IncompleteConversationalState, RemoteException {
			paramSaldos.put("from2010", "SI");		//jquispe 04/2014
			paramVta.put("from2010", "SI");
			paramProg.put("from2010", "SI");
			paramVac.put("from2010", "SI");
			
			Map arreglos = new HashMap();
			//JRR - 28/04/2009
			Map paramCompensa = new HashMap(paramProg);
			paramCompensa.put("licencia", Constantes.VACACION_INDEMNIZADA);//Cambiar por la licencia asignada
			if (log.isDebugEnabled()){
				log.debug("paramProg: " + paramProg);
				log.debug("paramCompensa: "+ paramCompensa);
			}
			String dbpool = (String)paramCompensa.get("dbpool");
			List listaSaldos = this.buscarSaldos(paramSaldos);
			String mesPago = "";
			
			//FRD 22/04/2009 - CAMBIO PARA listaVendidas
			List listaVendidas = this.buscarVacacionesXLicencia(paramVta);
			Map lvhm = new HashMap();
			Map lvDatos = new HashMap();
			Map reslv = new HashMap();
			String fecRegSolicitud = "";
			String mesfrs = "";
			String aniofrs = "";
			String numeroSol = "";
			String annoRef = "";
			//JRR - 27/05/2009
			String areaRef = "";
			Timestamp fechaTs = null;
			int mesint = 0;
			//int anioint = 0;
			T1277DAO t1277 = new T1277DAO(dbpool);
			if (log.isDebugEnabled()) log.debug("listaVendidas: "+ listaVendidas);
			if (listaVendidas.size()>0){}{
			  for(int ci=0; ci< listaVendidas.size() ;ci++){
				lvhm = (HashMap)listaVendidas.get(ci);
				//JRR - 27/04/2009
				//numeroSol = (lvhm.get("numero_ref")!=null?lvhm.get("numero_ref").toString().trim():"");
				//JRR - 27/05/2009
				numeroSol = ((lvhm.get("numero_ref")!=null && !lvhm.get("numero_ref").toString().trim().equals("0")) ? lvhm.get("numero_ref").toString().trim():"");
				areaRef = (lvhm.get("area_ref")!=null?lvhm.get("area_ref").toString().trim():"");
				//JRR - PROGRAMACION - 17/05/2010					
				annoRef = (lvhm.get("anno_ref")!=null?lvhm.get("anno_ref").toString().trim():"");
				//
				
				if ((numeroSol!=null && !numeroSol.equals("")) && (areaRef!=null && !areaRef.equals(""))
						&& (annoRef!=null && !annoRef.equals("")) ){//17/05/2010
				//if (numeroSol!=null && !numeroSol.equals("")) {
					lvDatos.put("numero_ref",lvhm.get("numero_ref"));
					//lvDatos.put("u_organ",lvhm.get("u_organ"));
					lvDatos.put("u_organ",lvhm.get("area_ref"));
					lvDatos.put("cod_pers",lvhm.get("cod_pers"));
					//JRR - PROGRAMACION - 17/05/2010					
					lvDatos.put("anno_ref",lvhm.get("anno_ref"));
					//
					reslv = t1277.findFechaRegistroSolicitud(lvDatos);
					
					if (log.isDebugEnabled()) log.debug("reslv: "+ reslv);
					//JRR - PROGRAMACION - 18/05/2010
					if (reslv!=null) {
						fechaTs = (Timestamp)reslv.get("fecha");
					} else {
						if (log.isDebugEnabled()) log.debug("No existe la solicitud en la BD");
						fechaTs = (Timestamp)lvhm.get("ffinicio");
					}
					
				} else {
					//Venta realizada por el administrador
					//FRD  08/05/2009 ya no fcreacion sino ffinicio
					//fechaTs = (Timestamp)lvhm.get("fcreacion");
					fechaTs = (Timestamp)lvhm.get("ffinicio");
				}
				//
				fecRegSolicitud = Utiles.timeToFecha(fechaTs);
				aniofrs = Utiles.dameAnho(fecRegSolicitud);
				//anioint = Integer.parseInt(aniofrs);
				mesfrs = fecRegSolicitud.substring(3,5);
				//aniofrs = String.valueOf(anioint);
			    mesint = Integer.parseInt(mesfrs);
			    if (mesint==1)  mesfrs = "Enero";     if (mesint==2)  mesfrs = "Febrero"; 
			    if (mesint==3)  mesfrs = "Marzo";     if (mesint==4)  mesfrs = "Abril";   
			    if (mesint==5)  mesfrs = "Mayo";	  if (mesint==6)  mesfrs = "Junio";   
			    if (mesint==7)  mesfrs = "Julio";     if (mesint==8)  mesfrs = "Agosto";  
			    if (mesint==9)  mesfrs = "Setiembre"; if (mesint==10) mesfrs = "Octubre"; 
			    if (mesint==11) mesfrs = "Noviembre"; if (mesint==12) mesfrs = "Diciembre";
				lvhm.put("fecPagoPlanilla",mesfrs + " de " + aniofrs);
			  }
			}  	
			
			if (log.isDebugEnabled()) log.debug(paramProg);
			
			List prolisttmp = new ArrayList();
			List listaProgramadas = this.buscarVacacionesXLicencia(paramProg);

			if (log.isDebugEnabled()) log.debug(String.valueOf(listaProgramadas.size()));
			if (log.isDebugEnabled()) log.debug(String.valueOf(listaProgramadas));
			
			Map hmprg = new HashMap();
			String valobs ="";
			String est_id = "";
			if (listaProgramadas.size()>0){}{
			  for(int cp=0; cp< listaProgramadas.size() ;cp++){
				hmprg = (HashMap)listaProgramadas.get(cp);
				if (log.isDebugEnabled()) log.debug(hmprg);
				valobs = String.valueOf(hmprg.get("observ"));
				est_id = String.valueOf(hmprg.get("est_id"));
				if (valobs==null) valobs = "";
				if (est_id==null) est_id = "";
				valobs = valobs.trim();
				est_id = est_id.trim();
				if (log.isDebugEnabled()) log.debug(valobs);
				if (log.isDebugEnabled()) log.debug(est_id);
				
				if ("ART 23 DL 713".equals(valobs) || ("4".equals(est_id))){		   	
				}else{
				   prolisttmp.add(hmprg);
				}
			  }
			  if (log.isDebugEnabled()) {
				  log.debug("prolisttmp.size()========================");
				  log.debug(String.valueOf(prolisttmp.size()));
				  log.debug("prolisttmp========================");
				  log.debug(prolisttmp);
				  log.debug("prolisttmp.size()========================");
				  log.debug(String.valueOf(prolisttmp.size()));
				  log.debug("prolisttmp========================");
				  log.debug(prolisttmp);
			  }
			  listaProgramadas = prolisttmp; 
			
			  if (log.isDebugEnabled()) log.debug(String.valueOf(listaProgramadas.size()));
			  if (log.isDebugEnabled()) log.debug(String.valueOf(listaProgramadas));
				
			}
			
			if (log.isDebugEnabled()) log.debug(listaProgramadas);

			
			//FRD 22/04/2009 - LISTA DE COMPENSADAS Y LOGICA A MOSTRAR
			List listaCompensadas = this.buscarVacacionesXLicencia((HashMap)paramCompensa);
			pe.gob.sunat.rrhh.dao.T99DAO t99dao = new pe.gob.sunat.rrhh.dao.T99DAO(dbpool);
			Map tthm = new HashMap();
			Map mesAbonoHM = new HashMap();
			String feini = "";
			String fefin = "";
			String Anio = "";
			String periodoReg = "";
			String newFeini = "";
			String newMes = "";
			if (listaCompensadas.size()>0){}{
			  for(int i=0; i< listaCompensadas.size() ;i++){
			    tthm = (HashMap)listaCompensadas.get(i);
			    if (tthm.get("ffin")==null ){
				   fefin="";	    
				   Timestamp fechaInicio = (Timestamp)tthm.get("ffinicio");
				   feini = Utiles.timeToFecha(fechaInicio);
			    }else{
				  if ("".equals(tthm.get("ffin").toString().trim())){
				    fefin="";	 
				    Timestamp fechaInicio = (Timestamp)tthm.get("ffinicio");
				    feini = Utiles.timeToFecha(fechaInicio);
				  }else{
				    Timestamp fechafin = (Timestamp)tthm.get("ffin");
				    Timestamp fechaInicio = (Timestamp)tthm.get("ffinicio");
				    feini = Utiles.timeToFecha(fechaInicio);
				    fefin = Utiles.timeToFecha(fechafin);
				  }	   
			    }
			    if ("".equals(fefin)){
			    	Anio = tthm.get("anno_vac").toString();
			    	mesAbonoHM = t99dao.findMesAbonoVacaVencidas(Anio);
			    	periodoReg = mesAbonoHM.get("cod_clasif1").toString();
			    	if ("05".equals(periodoReg)) mesPago = "Mayo - 2009";
			    	if ("06".equals(periodoReg)) mesPago = "Junio - 2009";
			    	if ("07".equals(periodoReg)) mesPago = "Julio - 2009";
			    	if ("08".equals(periodoReg)) mesPago = "Agosto - 2009";
			    	if ("09".equals(periodoReg)) mesPago = "Setiembre - 2009";
			    	if ("10".equals(periodoReg)) mesPago = "Octubre - 2009";
			    	if ("11".equals(periodoReg)) mesPago = "Noviembre - 2009";
			    	if ("12".equals(periodoReg)) mesPago = "Diciembre - 2009";
			    	tthm.put("pago",mesPago);
			    	tthm.put("veFechaPago","SI");
			    	tthm.put("veFecha","NO");
			    	if ((i % 2) == 0){ tthm.put("literal","b");}
			    	else{ tthm.put("literal","c");	}	
			    }else{
			    	Anio = tthm.get("anno_vac").toString();
			    	mesAbonoHM = t99dao.findMesAbonoVacaVencidas(Anio);
			    	periodoReg = mesAbonoHM.get("cod_clasif2").toString();
			    	/**
			    	if ("05".equals(periodoReg)) mesPago = "Mayo - 2009";
			    	if ("06".equals(periodoReg)) mesPago = "Junio - 2009";
			    	if ("07".equals(periodoReg)) mesPago = "Julio - 2009";
			    	if ("08".equals(periodoReg)) mesPago = "Agosto - 2009";
			    	if ("09".equals(periodoReg)) mesPago = "Setiembre - 2009";
			    	if ("10".equals(periodoReg)) mesPago = "Octubre - 2009";
			    	if ("11".equals(periodoReg)) mesPago = "Noviembre - 2009";
			    	if ("12".equals(periodoReg)) mesPago = "Diciembre - 2009";
			    	tthm.put("pago",mesPago);
			    	*/
			    	if (feini.equals(fefin) && ((i % 2) != 0) ){
		    		  tthm.put("literal","c");
		    		  tthm.put("veFechaPago","SI");
		    		  tthm.put("veFecha","NO");
		    		  newFeini = Utiles.dameFechaAnterior(feini, 1);
		    		  newMes = newFeini.substring(3, 5);
		    		  mesint = Integer.parseInt(newMes);
		  		      if (mesint==12) mesfrs = "Enero - 2009";     if (mesint==1)  mesfrs = "Febrero - 2009"; 
		  		      if (mesint==2)  mesfrs = "Marzo - 2009";     if (mesint==3)  mesfrs = "Abril - 2009";   
		  		      if (mesint==4)  mesfrs = "Mayo - 2009";	    if (mesint==5)  mesfrs = "Junio - 2009";   
		  		      if (mesint==6)  mesfrs = "Julio - 2009";     if (mesint==7)  mesfrs = "Agosto - 2009";  
		  		      if (mesint==8)  mesfrs = "Setiembre - 2009"; if (mesint==9) mesfrs = "Octubre - 2009"; 
		  		      if (mesint==10) mesfrs = "Noviembre - 2009"; if (mesint==11) mesfrs = "Diciembre - 2009";
		  		      tthm.put("pago",mesfrs);
		    		  //feini
			    	}else{
			    	  tthm.put("literal","b");
			    	  tthm.put("veFechaPago","NO");
			    	  tthm.put("veFecha","SI");
			    	}
			    }
			    
			  }
			}  	
			
			List listaPendientes = this.buscarVacacionesEfecPend(paramVac);
			List listaGozadas = this.buscarVacacionesEfecGoz(paramVac);
		
			arreglos.put("listaSaldos",listaSaldos);
			arreglos.put("listaVendidas",listaVendidas);
			arreglos.put("listaPendientes",listaPendientes);
			arreglos.put("listaGozadas",listaGozadas);
			arreglos.put("listaProgramadas",listaProgramadas);
			arreglos.put("listaCompensadas",listaCompensadas);
			
			return arreglos;
			
		}
/*         */	 
		
		
		//ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta 
		/**
		 * @ejb.interface-method view-type="remote"
		 * @ejb.transaction type="NotSupported"
		 */
		public Map buscarVacacionPorSolRef(String dbpool, String anno_ref,	
				String numero_ref, String cod_pers, String mov, String estado)
				throws IncompleteConversationalState, RemoteException {

			Map vacEfec = null;
			BeanMensaje beanM = new BeanMensaje();
			try {
				T1282DAO dao = new T1282DAO(dbpool);
				Map params = new HashMap();
				params.put("anno_ref", anno_ref);
				params.put("numero_ref", numero_ref);
				params.put("cod_pers", cod_pers);
				params.put("licencia", mov);
				params.put("est_id", estado);
				log.debug("metodo buscarVacacionPorSolRef(t1282.findByCodPersAnnoNumeroRefLicenciaEstidIndMatri())... params:"+params);
				vacEfec = dao.findByCodPersAnnoNumeroRefLicenciaEstidIndMatri(params); 
				
			} catch (Exception e) {
				log.error(e,e);
				beanM.setMensajeerror(e.getMessage());
				beanM.setMensajesol("Por favor intente nuevamente.");
				throw new IncompleteConversationalState(beanM);
			}

			return vacEfec;
		}
		//FIN ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta 
	 
	
}