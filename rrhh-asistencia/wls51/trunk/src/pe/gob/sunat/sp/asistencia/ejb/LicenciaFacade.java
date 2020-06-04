package pe.gob.sunat.sp.asistencia.ejb;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import pe.gob.sunat.rrhh.asistencia.dao.T1608DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T4819DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T4821DAO;
import pe.gob.sunat.sol.BeanMensaje;
import pe.gob.sunat.sol.IncompleteConversationalState;
import pe.gob.sunat.sp.asistencia.bean.BeanCompensaOnom;
import pe.gob.sunat.sp.asistencia.bean.BeanLicencia;
import pe.gob.sunat.sp.asistencia.bean.BeanTurnoTrabajo;
//import pe.gob.sunat.sp.asistencia.bean.BeanTurnoTrabajo;
import pe.gob.sunat.sp.asistencia.dao.T1270DAO;
//import pe.gob.sunat.sp.asistencia.dao.T1273DAO;
import pe.gob.sunat.sp.asistencia.dao.T1276DAO;
import pe.gob.sunat.sp.asistencia.dao.T1279DAO;
import pe.gob.sunat.sp.asistencia.dao.T8151DAO;
//import pe.gob.sunat.sp.asistencia.dao.T1282DAO;
import pe.gob.sunat.sp.bean.BeanT99;
import pe.gob.sunat.sp.dao.T01DAO;
import pe.gob.sunat.sp.dao.T02DAO;
import pe.gob.sunat.sp.dao.T5864DAO;
import pe.gob.sunat.sp.dao.T99DAO;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;
import pe.gob.sunat.utils.bd.OracleSequenceDAO;

/**
 * @ejb.bean name="LicenciaFacadeEJB"
 *           description="LicenciaFacade"
 *           jndi-name="ejb/rrhh/facade/sp/asistencia/LicenciaFacadeEJB"
 *           type="Stateless" view-type="remote"
 * 
 * @ejb.home remote-class="pe.gob.sunat.sp.asistencia.ejb.LicenciaFacadeHome"
 *           extends="javax.ejb.EJBHome"
 * 
 * @ejb.interface remote-class="pe.gob.sunat.sp.asistencia.ejb.LicenciaFacadeRemote"
 *                extends="javax.ejb.EJBObject"
 * 
 * @ejb.resource-ref res-ref-name="pool_oracle" res-type="javax.sql.DataSource" res-auth="Container"
 * @jboss.resource-ref res-ref-name="pool_oracle" jndi-name="java:/pool_scad"
 * @weblogic.resource-description res-ref-name="pool_oracle" jndi-name="jdbc/dcscad"
 * 
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
public class LicenciaFacade implements SessionBean {

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
	public String registrarLicencia(HashMap datos, ArrayList trabajadores)
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
				
				datos.put("trabajadores", listaTrab);
				QueueDAO qd = new QueueDAO();
				qd.encolaProceso(ProcesoFacadeHome.JNDI_NAME,
						"registrarLicencia", datos, (String)datos.get("usuario"));
			}
			else{
				
				ProcesoFacadeHome facadeHome = (ProcesoFacadeHome) ServiceLocator
					.getInstance().getRemoteHome(ProcesoFacadeHome.JNDI_NAME,
							ProcesoFacadeHome.class);
				ProcesoFacadeRemote proceso = facadeHome.create();
				
				T1279DAO movDAO = new T1279DAO();
				HashMap tipoMov = movDAO.findByMov(
						(String)datos.get("dbpool"), 
						(String)datos.get("tipoLicencia"));

				MantenimientoFacadeHome facadeMantHome = (MantenimientoFacadeHome) ServiceLocator.getInstance().getRemoteHome(
						MantenimientoFacadeHome.JNDI_NAME,
						MantenimientoFacadeHome.class);
				MantenimientoFacadeRemote facadeMantRemote = facadeMantHome.create();
				
				boolean bPeriodoCerrado = facadeMantRemote.periodoCerradoUOFecha((String)datos.get("dbpool"),(String)datos.get("fechaIni"),Utiles.obtenerFechaActual(),(String)datos.get("codUO"));
				if (bPeriodoCerrado){				
					throw new Exception("El periodo ya se encuentra cerrado para la UO ".concat((String)datos.get("codUO")).concat("."));
				}
				
				datos.put("ind_dias",tipoMov.get("ind_dias"));
				String error = proceso.registrarLicenciaTrabajador(datos);
				if (!error.equals(Constantes.OK)){
					throw new Exception(error);
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
	public String modificarLicencia(String dbpool, String codPers,
			String codUO, String tipo, String periodo, String licencia,
			Timestamp fIni, String fechaIni, String fechaFin, String ano_ref,
			String numero_ref, String area_ref, String obs, String numero,
			String certif, String cmp, String fechaCita, String tipoEnfermedad,
			String usuario) throws IncompleteConversationalState,
			RemoteException {

		//String res = Constantes.OK;
		String res = "";
		BeanMensaje beanM = new BeanMensaje();
		try {

			T99DAO codigoDAO = new T99DAO();
			//prac-jcallo
			T1273DAO licenciaDAO = new T1273DAO(dbpool);
			T1282DAO vacacionDAO = new T1282DAO(dbpool);

			//T1273CMPHome cmpHome = (T1273CMPHome) ServiceLocator.getInstance().getLocalHome(T1273CMPHome.JNDI_NAME);
			//T1274CMPHome medicaHome = (T1274CMPHome) ServiceLocator.getInstance().getLocalHome(T1274CMPHome.JNDI_NAME);

			MantenimientoFacadeHome facadeHome = (MantenimientoFacadeHome) ServiceLocator
					.getInstance().getRemoteHome(MantenimientoFacadeHome.JNDI_NAME,
							MantenimientoFacadeHome.class);
			MantenimientoFacadeRemote mantenimiento = facadeHome.create();
			
			AsistenciaFacadeHome asisHome = (AsistenciaFacadeHome) ServiceLocator
			.getInstance().getRemoteHome(
					AsistenciaFacadeHome.JNDI_NAME,
					AsistenciaFacadeHome.class);

			AsistenciaFacadeRemote asisRemote = asisHome.create();
			

			String sPeriodoOriginal = periodo;
			String sPeriodoValida = "";
			//Se modifica el criterio de verificaciï¿½n de periodo cerrado PAS20144EB20000076
			if(tipo.trim().equalsIgnoreCase(Constantes.LICENCIA_COMPENSACION)){
				FechaBean fecha1 = new FechaBean(fIni);
				sPeriodoValida = fecha1.getFormatDate("yyyyMM");
			}else sPeriodoValida = periodo;
			
			boolean cerrado = mantenimiento.periodoCerradoAFecha(dbpool,
					sPeriodoValida, Utiles.obtenerFechaActual());

			periodo = sPeriodoOriginal;
			//Fin cambios PAS20144EB20000076
			
			if (cerrado) {
				res = "El periodo " + sPeriodoValida + " se encuentra cerrado.";
			}//JRR - PARA QUE NO MODIFIQUEN TAMPOCO LICENCIA BOLSA
			else if ((tipo.trim().equalsIgnoreCase(Constantes.LICENCIA_BOLSA)))
					//|| (tipo.trim().equalsIgnoreCase(Constantes.LICENCIA_COMPENSACION)))
					{
				res = "No puede modificarse una Licencia por CompensaciÃ³n de Feriado.";
			}
			//
			else {
				//JRR - 25/05/2009	
				if (fechaIni==null || fechaIni.equals(""))
					fechaIni = Utiles.timeToFecha(fIni);
				
				if (log.isDebugEnabled()) log.debug("fechaIni: " + fechaIni);

				//prac-jcallo
				Map prm = new HashMap();
				prm.put("cod_pers", codPers);
				prm.put("fechaIni", fechaIni);
				prm.put("fechaFin", fechaFin);
				log.debug("metodo modificarLicencia... prm:"+prm);
				boolean bVacacion = vacacionDAO.findByCodPersFIniFFin(prm); //findByCodPersFIniFFin(dbpool,codPers, fechaIni, fechaFin);
				if (!bVacacion) {
					//prac-jcallo
					Map prms = new HashMap();
					prms.put("cod_pers", codPers);
					prms.put("fecha1", fechaIni);
					prms.put("fecha2", fechaFin);
					prms.put("numero", numero);
					log.debug("... prms:"+prms);
					boolean bLicencia = licenciaDAO.findByCodPersFIniFFin(prms);
							//dbpool, codPers, fechaIni, fechaFin, numero);
					if (!bLicencia) {

						T1279DAO movDAO = new T1279DAO();
						HashMap tipoMov = movDAO.findByMov( dbpool,	licencia);
						int numDias = 0;
						if (((String)tipoMov.get("ind_dias")).equals("1")) {
							numDias = asisRemote.obtenerDiasHabilesDiferencia(dbpool,fechaIni,fechaFin) + 1;
						}
						else{
							numDias = Utiles.obtenerDiasDiferencia(fechaIni, fechaFin) + 1;
						}
						
						//int numDias = Utiles.obtenerDiasDiferencia(fechaIni,fechaFin) + 1;
						log.debug("numDias"+numDias);
						
						ArrayList params = codigoDAO.findByCodTab(dbpool, "0", tipo, Constantes.CODTAB_DIAS_LICENCIA);

						if (params != null && params.size() > 0) {

							boolean existe = false;
							for (int i = 0; i < params.size() && !existe; i++) {

								BeanT99 p = (BeanT99) params.get(i);

								int dias = 0;
								try {
									dias = Integer.parseInt(p.getT99descrip().trim());
									log.debug("dias" + dias);
								} catch (Exception e) {}
								existe = dias == numDias;
							}

							if (!existe) {
								throw new Exception(
										"El rango de fechas ingresado no corresponde a los parametros requeridos por la licencia.");
							}

						}

						java.sql.Timestamp fLicI = Utiles
								.stringToTimestamp(fechaIni + " 00:00:00");
						java.sql.Timestamp fLicF = Utiles
								.stringToTimestamp(fechaFin + " 00:00:00");
						DataSource ds = ServiceLocator.getInstance().getDataSource(dbpool);
						pe.gob.sunat.rrhh.asistencia.dao.T1273DAO t1273dao = new  pe.gob.sunat.rrhh.asistencia.dao.T1273DAO(ds);

					      
						/*T1273CMPLocal cmpLocal = cmpHome
								.findByPrimaryKey(new T1273CMPPK(
										periodo.trim(),
										new Short(numero.trim()), fIni, codPers
												.trim(), licencia.trim()));*/
						
						//aun en dua si va o no esta porcion de codigo solo para obtener el aÃ±o
						Map dto = new HashMap();
						dto.put("periodo", periodo.trim());
						dto.put("numero", new Integer(numero.trim()));
						dto.put("ffinicio", fIni);
						dto.put("cod_pers", codPers);
						dto.put("licencia", licencia.trim());
						log.debug("licenciaDAO.findAllColumnsByKey(dto)... dto:"+dto);
						Map lic = licenciaDAO.findAllColumnsByKey(dto);
						String anho = lic.get("anno").toString();

						
						//JRR - 21/05/2009
						if (tipo.trim().equalsIgnoreCase(Constantes.FERIADO_COMPENSABLE)) {
							
							T1608DAO t1608dao = new T1608DAO(dbpool);
							Map mapLicComp = new HashMap();
							mapLicComp.put("cod_personal", codPers);
							mapLicComp.put("anno", anho);
							mapLicComp.put("num_refer", new Integer(numero));
							boolean eliminados = t1608dao.eliminarCompensacionPersona(mapLicComp);
							
							if (eliminados) {
								//Insertamos nuevos registros
								TurnoTrabajoFacadeHome TTfacadeHome = (TurnoTrabajoFacadeHome) ServiceLocator
								.getInstance().getRemoteHome(TurnoTrabajoFacadeHome.JNDI_NAME,
										TurnoTrabajoFacadeHome.class);
								TurnoTrabajoFacadeRemote turno = TTfacadeHome.create();

								int diasComp = turno.calcularHorasCompensa(dbpool,
										codPers,
										Utiles.timeToFecha(fLicI), 
										Utiles.timeToFecha(fLicF));

								String fechaCompensa = "";

								if( obs.indexOf("&fecha=") != -1 ){
									fechaCompensa = obs.substring(obs.indexOf("&fecha=")+7, obs.length());
									if(log.isDebugEnabled()) log.debug("CAMPO &fecha=" + fechaCompensa);
								}

								java.sql.Timestamp fLicC = Utiles.stringToTimestamp(fechaCompensa + " 00:00:00");
								HashMap mapa = new HashMap();
								mapa.put("dbpool",dbpool);
								mapa.put("codPers",codPers);
								mapa.put("codUO",codUO);
								mapa.put("fechaLic",Utiles.timeToFecha(fLicF));
								mapa.put("fechaGen",Utiles.obtenerFechaActual());
								mapa.put("diasComp",""+diasComp);
								mapa.put("horComp","1");
								mapa.put("usuario",usuario);
								mapa.put("fechaCompensa",Utiles.timeToFecha(fLicC));
								mapa.put("anno",anho);
								mapa.put("num_refer",new Integer(numero));

								//registramos los turnos de compensacion
								turno.registrarTurnoCompensa(mapa);
							} else {
								res = "No es posible modificar las fechas de compensaciÃ³n de esta licencia mediante la aplicaciÃ³n.";
								throw new IncompleteConversationalState(res);								
							}
						}
						//						
						
												
						//PRAC-JCALLO - LA GRAN DUDA QUE CAMPOS DEBEN SER MODIFICADOS EN ESTA PARTE..?
						/*t1273dao.modificarLicencia(periodo, anho,
								new java.lang.Short(numero), codUO, fLicI,
								fLicF, codPers, numDias, tipo,
								ano_ref != null ? ano_ref.trim() : "",
								numero_ref != null ? numero_ref.trim() : "0",
								area_ref != null ? area_ref.trim() : "", obs,
								new java.sql.Timestamp(System.currentTimeMillis()), usuario);*/
						//PRAC-JCALLO
						Map mapLic = new HashMap();
						mapLic.put("anho", anho);
						mapLic.put("uo", codUO);
						mapLic.put("ffin", fLicF);
						mapLic.put("dias", new Integer(numDias));
						mapLic.put("ano_ref", ano_ref != null ? ano_ref.trim() : "");
						mapLic.put("numero_ref", ( numero_ref != null && !numero_ref.equals("") ) ? new Integer(numero_ref) : new Integer("0"));						
						mapLic.put("area_ref", area_ref != null ? area_ref.trim() : "");
						mapLic.put("observ", obs); 
						mapLic.put("fcrea", new FechaBean().getTimestamp());
						mapLic.put("usuario", usuario); 
						mapLic.put("periodo", periodo); 
						mapLic.put("numero", numero);
						mapLic.put("ffinicio", fLicI); 
						mapLic.put("cod_pers", codPers); 
						mapLic.put("licencia", licencia);
/*
						t1273dao.modificarLicencia(mapLic);//esta podria ser reemplazada por el nuevo metodo t1273dao.updateCustomColumns(params)
						//fin prac-jcallo caso t1273
*/						
						//PRAC-ASANCHEZ 11/09/2009
						if(tipo.trim().equalsIgnoreCase(Constantes.LICENCIA_COMPENSACION)){
							ArrayList mensajes = new ArrayList();
							T1279DAO t1279dao = new T1279DAO();
							
							//String cod_pers = solicitud.get("codPers").toString();
							//String ffinicio = mapa.get("ffinicio").toString();
							//String ffin = mapa.get("ffin").toString();
							
							HashMap tipoMov2 = t1279dao.findByMov(dbpool, tipo);
							HashMap params2 = new HashMap();
							params2.put("userOrig", codPers);
							params2.put("fechaIni", fechaIni);
							params2.put("fechaFin", fechaFin);
							params2.put("regimenCol", "--");
							params2.put("dias_ini", new Integer(0));
							params2.put("fechas", new ArrayList());
							params2.put("dbpool", dbpool);
							
							mensajes = validaLicenciaModif(params2,tipoMov2,false); 	
							
							if(mensajes.isEmpty()){
							
								//inserta el registro nuevo
								boolean resul;
								mapLic.put("fecha", fechaIni);
								mapLic.put("trabajador", codPers);
								mapLic.put("uuoo", codUO);
								if (log.isDebugEnabled()) log.debug("t1273dao.registrarLicenciaCompensacion(mapLic)... mapLic:"+mapLic);
								
								HashMap hmParamsUPD = new HashMap();
								
								HashMap hmColumns = new HashMap();
								hmColumns.put("ffinicio", Utiles.stringToTimestamp(fechaIni+ " 00:00:00"));
								hmColumns.put("ffin", Utiles.stringToTimestamp(fechaFin+ " 00:00:00") );
								hmColumns.put("fmod", new java.sql.Timestamp(System.currentTimeMillis()) );
								hmColumns.put("cuser_mod", usuario );
								
								hmParamsUPD.put("columns", hmColumns);
								hmParamsUPD.put("periodo", periodo);
								hmParamsUPD.put("numero", numero);
								hmParamsUPD.put("ffinicio", fIni);
								hmParamsUPD.put("cod_pers", codPers);
								hmParamsUPD.put("licencia", licencia);								
								
								if (log.isDebugEnabled()){ 
									log.debug("Parametros UpdateCustomColumns(Columns):"+hmColumns);
									log.debug("Parametros UpdateCustomColumns(Columns):"+hmParamsUPD);
								}
								
								t1273dao.updateCustomColumns(hmParamsUPD);	
								
								/*
								
								pre.setTimestamp(1, Utiles.stringToTimestamp(ffinicio + " 00:00:00"));
								pre.setTimestamp(2, Utiles.stringToTimestamp(ffin + " 00:00:00"));
								pre.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
								
								
								resul = t1273dao.registrarLicenciaCompensacion(mapLic);
								//elimina el registro antiguo
								if(resul){
									//fLicI = Utiles.stringToTimestamp(fIni + " 00:00:00");	
									mapLic.put("ffinicio", fIni);
									if (log.isDebugEnabled()) log.debug("t1273dao.deleteByPrimaryKey(mapLic)... mapLic:"+mapLic);
									t1273dao.deleteByPrimaryKey(mapLic);
								}
								*/
								
							}else{
								log.error("ERROR: No se superï¿½ la validaciï¿½n de la licencia: \n"+mensajes);
								res = res + mensajes;
							}
						}else{
							if (log.isDebugEnabled()) log.debug("t1273dao.modificarLicencia(mapLic)... mapLic:"+mapLic);
							t1273dao.modificarLicencia(mapLic);//esta podria ser reemplazada por el nuevo metodo t1273dao.updateCustomColumns(params)
						}
						//
						
						//licencia por enfermedad
						if (tipo.trim().equalsIgnoreCase(Constantes.LICENCIA_ENFERMEDAD)) {
							try {
								//prac-jcallo
								/*T1274CMPLocal medicaLocal = medicaHome
										.findByPrimaryKey(new T1274CMPPK(
												codPers, tipo, periodo, 
												anho,
												new java.lang.Short(numero),
												fLicI));*/
								T1274DAO t1274dao = new T1274DAO(dbpool);								
								Map mapdatos = new HashMap();//datos de las columnas a actualizar
								mapdatos.put("cod_cmp", cmp);
								mapdatos.put("ffin", fLicF);
								mapdatos.put("c_certific", certif);
								mapdatos.put("enfermedad", tipoEnfermedad);
								mapdatos.put("fecha", Utiles.stringToTimestamp(fechaCita+ " 00:00:00"));
								mapdatos.put("fmod", new FechaBean().getTimestamp());
								mapdatos.put("cuser_mod", usuario);
								//le paso los datos a modificar
								Map prmdatos = new HashMap();//mapa que contiene todos los paramtros
								prmdatos.put("columns", mapdatos);
								//parametros de la llave primaria
								prmdatos.put("cod_pers", codPers);
								prmdatos.put("licencia", tipo);
								prmdatos.put("periodo", periodo);
								prmdatos.put("anno", anho);
								prmdatos.put("numero", new Integer(numero));
								prmdatos.put("ffinicio", fLicI);
								log.debug("t1274dao.updateCustomColumns(prmdatos)... prmdatos:"+prmdatos);
								t1274dao.updateCustomColumns(prmdatos);
								
								/*medicaLocal.setCodCmp(cmp);
								medicaLocal.setFfin(fLicF);
								medicaLocal.setCCertific(certif);
								medicaLocal.setEnfermedad(tipoEnfermedad);
								medicaLocal.setFecha(Utiles.stringToTimestamp(fechaCita+ " 00:00:00"));
								medicaLocal.setFmod(new Timestamp(System.currentTimeMillis()));
								medicaLocal.setCuserMod(usuario);*/

							} catch (Exception e) {
								log.debug("Error : " + e.toString());
							}
						}

						
					} else {
						res = "Imposible modificar licencia. El trabajador ya posee una licencia durante esas fechas.";
					}
				} else {
					res = "Imposible modificar licencia. El trabajador se encuentra de vacaciones durante esas fechas.";
				}
			}

		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		if(res.equals(""))
			res = Constantes.OK;
		return res;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public List buscarLicencias(String dbpool, String tipo,
			String criterio, String valor, HashMap seguridad)
			throws IncompleteConversationalState, RemoteException {
		if(log.isDebugEnabled()) log.debug("method: buscarLicencias");

		List lista = null;
		List listaMatrimonioCAS = null;
		BeanMensaje beanM = new BeanMensaje();
		try {
			tipo = tipo.trim();
			if(tipo.equals(Constantes.LICENCIA_MATRIMONIO)){
				
				//1. Consulta de la tabla de vacaciones detalle
				T1282DAO vacacionDAO = new T1282DAO(dbpool);
				HashMap paramsVacacionMatrimonio = new HashMap();
				paramsVacacionMatrimonio.put("licencia", Constantes.VACACION);
				paramsVacacionMatrimonio.put("est_id", Constantes.ACTIVO);
				paramsVacacionMatrimonio.put("ind_matri", Constantes.ACTIVO);
				if(criterio.equals("5"))
					paramsVacacionMatrimonio.put("cod_pers", valor.toUpperCase());
				else if(criterio.equals("3"))
					paramsVacacionMatrimonio.put("ffinicio", Utiles.stringToDate(valor));
				else if(criterio.equals("4"))
					paramsVacacionMatrimonio.put("ffin", Utiles.stringToDate(valor));
				else if(criterio.equals("1"))
					paramsVacacionMatrimonio.put("anno_vac", valor);
				//else if(criterio.equals("0") && valor!=null && valor.equals("0"))
				//		paramsVacacionMatrimonio.put("numero", valor);
				
				if(!criterio.equals("0")){
					log.debug("paramsVacacionMatrimonio: "+paramsVacacionMatrimonio);
					List lstVacacionesMatrimonio = vacacionDAO.findVacacionesProgramadasByParams(paramsVacacionMatrimonio);
					log.debug("lstVacacionesMatrimonio: "+lstVacacionesMatrimonio);
					if(lstVacacionesMatrimonio!=null && lstVacacionesMatrimonio.size()>0){
						listaMatrimonioCAS = new ArrayList();
						T1279DAO tipoMovimiento = new T1279DAO();
						HashMap movimiento = tipoMovimiento.findByMov(dbpool, Constantes.LICENCIA_MATRIMONIO);
						for(int i=0; i<lstVacacionesMatrimonio.size(); i++){
							
							//2. Seteamos solo los campos necesarios
							HashMap vacacionMatrimonio = (HashMap)lstVacacionesMatrimonio.get(i);
							vacacionMatrimonio.put("descrip", movimiento.get("descrip"));
							vacacionMatrimonio.put("numero", "0");
							vacacionMatrimonio.put("anno", vacacionMatrimonio.get("anno_vac"));
							vacacionMatrimonio.put("qdias", vacacionMatrimonio.get("dias"));
							vacacionMatrimonio.put("licencia", Constantes.LICENCIA_MATRIMONIO);
						}
						listaMatrimonioCAS = lstVacacionesMatrimonio;
					}
				}
			}
			
			T1273DAO dao = new T1273DAO(dbpool);
			Map params = new HashMap();
			params.put("tipo", tipo);
			params.put("criterio", criterio);
			params.put("valor", valor);
			params.put("seguridad", seguridad);
			log.debug("metodo buscarLicencias... params:"+params);
			lista = dao.joinWithT02T1279(params); //joinWithT02T1279(dbpool, tipo, criterio, valor,seguridad);
			if(tipo.equals(Constantes.LICENCIA_MATRIMONIO) && listaMatrimonioCAS!=null)
				lista.addAll(listaMatrimonioCAS);
			
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
	public Map buscarLicenciaSolRef(String dbpool, String anno_ref,	//jquispecoi 03/2014
			String numero_ref, String cod_pers, String licencia)
			throws IncompleteConversationalState, RemoteException {

		Map lic = null;
		BeanMensaje beanM = new BeanMensaje();
		try {
			T1273DAO dao = new T1273DAO(dbpool);
			Map params = new HashMap();
			params.put("anno_ref", anno_ref);
			params.put("numero_ref", numero_ref);
			params.put("cod_pers", cod_pers);
			params.put("licencia", licencia);
			log.debug("metodo buscarLicencias... params:"+params);
			lic = dao.findBySolRef(params); //joinWithT02T1279(dbpool, tipo, criterio, valor,seguridad);
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return lic;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public ArrayList eliminarLicencias(String dbpool, String[] params,
			ArrayList lista, ArrayList listaMensajes, String usuario) throws IncompleteConversationalState,
			RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {
			//PRAC-JCALLO
			/*T1273CMPHome cmpHome = (T1273CMPHome) ServiceLocator.getInstance().getLocalHome(T1273CMPHome.JNDI_NAME);
			T1273CMPLocal cmpLocal = null;*/
			/*T1274CMPHome medicaHome = (T1274CMPHome) ServiceLocator.getInstance().getLocalHome(T1274CMPHome.JNDI_NAME);			
			T1274CMPLocal medicaLocal = null;*/
			
			//T1276DAO periodoDAO = new T1276DAO();
			//String periodoActual = (periodoDAO.findPeriodoActual(dbpool)).getPeriodo();
			MantenimientoFacadeHome facadeHome = (MantenimientoFacadeHome) ServiceLocator
			.getInstance().getRemoteHome(MantenimientoFacadeHome.JNDI_NAME,
					MantenimientoFacadeHome.class);
			MantenimientoFacadeRemote mantenimiento = facadeHome.create();
			boolean cerrado = false;
			
			if (params != null) {
				for (int i = params.length - 1; i >= 0; i--) {

					String indice = params[i];
					//BeanLicencia lic = (BeanLicencia) lista.get(Integer.parseInt(indice));
					HashMap lic = (HashMap) lista.get(Integer.parseInt(indice));
					
					if (log.isDebugEnabled()) log.debug("LicenciaFacade eliminarLicencias - lic: " + lic);
					
					//if ( (lic.getLicencia().trim().equals(Constantes.LICENCIA_ELECCIONES)) || (lic.getLicencia().trim().equals(Constantes.LICENCIA_ONOMASTICO)) || (lic.getLicencia().trim().equals(Constantes.FERIADO_COMPENSABLE))
					//EBV 27/09/2007 SAU20072F310000224 se quita la Lic. por Onomastico para que la pueda eliminar el Administrador 
					if ( lic.get("licencia").toString().trim().equals(Constantes.LICENCIA_ELECCIONES) 
							//|| lic.get("licencia").toString().trim().equals(Constantes.FERIADO_COMPENSABLE)
							//JRR - BOLSA
							|| lic.get("licencia").toString().trim().equals(Constantes.LICENCIA_BOLSA)
							){
						log.debug("licencia "+lic.get("licencia"));
						log.debug("anno" + lic.get("anno_ref"));
						log.debug("area" + lic.get("area_ref"));
						log.debug("numero"+lic.get("numero_ref"));
					}
					else{
						if ( !lic.get("licencia").toString().trim().equals(Constantes.FERIADO_COMPENSABLE) && 
								(lic.get("numero_ref")!= null ) && (lic.get("anno_ref").toString().trim().length()>0) && (lic.get("area_ref").toString().trim().length() >0) && !(lic.get("numero_ref").toString().trim().equals("0"))  ) { //jquispecoi 05/2014
							log.debug("licencia "+lic.get("licencia"));
							log.debug("anno" + lic.get("anno_ref"));
							log.debug("area" + lic.get("area_ref"));
							log.debug("numero"+lic.get("numero_ref"));
						}else{

							cerrado = mantenimiento.periodoCerradoAFecha(dbpool,
									lic.get("periodo").toString().trim(), Utiles.obtenerFechaActual());

							if (!cerrado) {

								//if (periodoActual.equalsIgnoreCase(lic.getPeriodo())) {

								//JRR - 21/05/2009
								if (lic.get("licencia").toString().trim().equals(Constantes.FERIADO_COMPENSABLE)) {
									
									T1608DAO t1608dao = new T1608DAO(dbpool);
									Map mapLicComp = new HashMap();
									mapLicComp.put("cod_personal", lic.get("cod_pers"));
									mapLicComp.put("anno", lic.get("anno"));
									mapLicComp.put("num_refer", lic.get("numero"));
									boolean eliminados = t1608dao.eliminarCompensacionPersona(mapLicComp);
									
									if (!eliminados) {
										throw new IncompleteConversationalState("No es posible eliminar las fechas de compensaciÃ³n de esta licencia mediante la aplicaciÃ³n.");									
									}									
								}
								//

								if (lic.get("licencia").toString().trim().equalsIgnoreCase(
										Constantes.LICENCIA_ENFERMEDAD)) {


									try {
										//PRAC-JCALLO
										/*medicaLocal = medicaHome
										.findByPrimaryKey(new T1274CMPPK(
												lic.getCodPers(), lic
														.getLicencia(), lic
														.getPeriodo(), Utiles
														.obtenerAnhoActual(),
												new java.lang.Short(lic
														.getNumero()), lic
														.getFechaIni()));

								medicaLocal.remove();*/
										//prac-jcallo..la misma pregunta es eliminacion fisica?
										T1274DAO t1274dao = new T1274DAO(dbpool);
										Map mapLicM = new HashMap();
										mapLicM.put("cod_pers", lic.get("cod_pers"));
										mapLicM.put("licencia", lic.get("licencia"));
										mapLicM.put("periodo", lic.get("periodo"));
										mapLicM.put("anno", new FechaBean().getAnho());
										mapLicM.put("numero", new Integer(lic.get("numero").toString()));
										mapLicM.put("ffinicio", lic.get("ffinicio"));
										log.debug("metodo eliminarLicencias... mapLicM:"+mapLicM);
										t1274dao.deleteByPrimaryKey(mapLicM);

									} catch (Exception e) {
										log.debug(e.getMessage());
									}
								}

								//prac-jcallo eliminacion FISICA?
								/*cmpLocal = cmpHome
								.findByPrimaryKey(new T1273CMPPK(lic
										.getPeriodo(), new Short(lic
										.getNumero()), lic.getFechaIni(), lic
										.getCodPers(), lic.getLicencia()));

						cmpLocal.remove();*/
								String cod_pers = (String)lic.get("cod_pers");
								T02DAO empleadoDAO = new T02DAO();	
								Map mapaEmpleado = empleadoDAO.joinWithT12T99ByCodPers(dbpool,cod_pers, null);	
								String regimenCol = (mapaEmpleado!=null && !mapaEmpleado.isEmpty())?((String)mapaEmpleado.get("t02cod_rel")):"";
								
								if(lic.get("licencia").equals(Constantes.LICENCIA_MATRIMONIO) && regimenCol.equals(Constantes.CODREL_REG1057)){	//jquispecoi: matrimonio
									//A. Valicaciones
									if(log.isDebugEnabled()) log.debug("LICENCIA_MATRIMONIO CAS: " + lic);
									//1.1. Obtener fecha de ingreso
									String anno_vac = (String)lic.get("anno_vac");
									SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
									String strFechaIngreso = (mapaEmpleado!=null && !mapaEmpleado.isEmpty())?((String)mapaEmpleado.get("t02f_ingsun")):null;
									
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
										log.debug("anioSaldoVacVig(Anulacion Sol-Admin Lic): "+anioSaldoVacVig); //ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta vacaciones (SC)
									int anioSaldoVacProx = Integer.parseInt(anioSaldoVacVig)+1;
									
									//1.3 Sumamos 5 dias a la fecha actual
									Date dteFechaActual = sdf.parse(strFechaActual);
									Date dteProxFechaGeneracion = sdf.parse(strFechaIngreso.substring(0, 6)+anioSaldoVacProx);
									
									//ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta vacaciones (SC)
									if (strFechaIngreso!=null && !strFechaIngreso.trim().equals("")){
										log.debug("fecha ingreso tiene valor");
										if (strFechaActual.substring(0, 6).trim().equals(strFechaIngreso.substring(0, 6).trim())){
											log.debug("fecha actual igual a fecha generacion (solo dias y mes)-(Anulacion Sol-Admin Lic)");
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
										HashMap hmMensaje = new HashMap();																//jquispecoi 06/2014
										hmMensaje.put("mensaje", "Solo es posible anular hasta 5 días antes de su fecha de generación (día y mes de fecha de ingreso) de saldo vacacional para la licencia "+lic.get("anno")+"-"+lic.get("numero")+" del registro "+cod_pers+".");
										hmMensaje.put("tipo", "error");
										listaMensajes.add(hmMensaje);
										
									}else{
										//B. Transacciones
										//1. eliminar registros creados, por llave + ind_matri (t1282vacacion_d)
										
										//1.1 elimina 07
										HashMap prmEliminacion = new HashMap();
										prmEliminacion.put("cod_pers", cod_pers);
										prmEliminacion.put("periodo", lic.get("periodo"));
										prmEliminacion.put("licencia", Constantes.VACACION);
										prmEliminacion.put("ffinicio", lic.get("ffinicio"));
										prmEliminacion.put("ind_matri", Constantes.ACTIVO);
										T1282DAO vacacionesDetalleDAO = new T1282DAO(dbpool);
										vacacionesDetalleDAO.deleteByPKAndMore(prmEliminacion);
										
										//1.1 elimina 49
										prmEliminacion.put("licencia", Constantes.VACACION_PROGRAMADA);
										vacacionesDetalleDAO.deleteByPKAndMore(prmEliminacion);
										
										//3. actualizar cabecera (t1281vacacion_c)
										
										//3.1 buscar el registro cabecera para el aï¿½o
										pe.gob.sunat.rrhh.asistencia.dao.T1281DAO vacacionesCabeceraDAO = new pe.gob.sunat.rrhh.asistencia.dao.T1281DAO(dbpool);
										HashMap prmCabecera = new HashMap();
	                                    prmCabecera.put("cod_pers", cod_pers);
	                                    prmCabecera.put("anno", anno_vac);
	                                    Map cabeceraVacacion = vacacionesCabeceraDAO.findAllColumnsByKey(prmCabecera);  
	                                    int saldo = ((Integer)cabeceraVacacion.get("saldo")).intValue();
	                                    if (saldo==-7) {
	                                    	
	                                    	//3.2. delete registro
	                                    	vacacionesCabeceraDAO.deleteByPK(prmCabecera);
	                                    	
	                                    }else{
	                                    	
	                                    	//3.3. actualizar el registro, sumarle 7 dï¿½as
			                                Map prmActualizar= new HashMap();
			                                prmActualizar.put("saldo", new Integer(Integer.parseInt(cabeceraVacacion.get("saldo")+"") + 30  ));
			                                prmActualizar.put("cuser_mod",usuario);
			                                prmActualizar.put("fmod",new Timestamp(System.currentTimeMillis()));
			                                prmCabecera.put("columns", prmActualizar);
			                                vacacionesCabeceraDAO.updateCustomColumns(prmCabecera);
	                                    }
	                                    
	                                    //4. Validaciones nuevas programaciones y demï¿½s
	                                    
                                        //4.1 verificar no existan nuevas vacaciones programadas no relacionadas a licencia matrimonio
	                                    Map prmsNuevasProgramaciones = new HashMap();
                                        prmsNuevasProgramaciones.put("cod_pers",cod_pers);
                                        prmsNuevasProgramaciones.put("anno",anno_vac);
                                        prmsNuevasProgramaciones.put("licencia",Constantes.VACACION_PROGRAMADA);
                                        prmsNuevasProgramaciones.put("estado",Constantes.PROG_ACEPTADA);
                                        List lstNuevasProgramaciones = vacacionesDetalleDAO.findVacacionesNoGeneradasPorLicenciaMatrimonio(prmsNuevasProgramaciones);
                                        
                                        if (lstNuevasProgramaciones==null || lstNuevasProgramaciones.isEmpty()){
    										//2. actualizar registros actualizados por licencia (t1282vacacion_d)
    										
    										//2.1 buscamos los registros a actualizar
    										Map prmProgramadas = new HashMap();
    										prmProgramadas.put("cod_pers", cod_pers);
    										prmProgramadas.put("est_id", Constantes.PROG_PROGRAMADA);
    										prmProgramadas.put("licencia", Constantes.VACACION_PROGRAMADA);
    										prmProgramadas.put("ind_matri", Constantes.ACTIVO);
    										log.debug("prmProgramadas: "+prmProgramadas);
    										List programadas = vacacionesDetalleDAO.findVacacionesProgramadasByParams(prmProgramadas);
    										if(programadas != null)
    										for(int j=0; j<programadas.size(); j++){
    											
    											//2.2 actualiza los registros encontrados
    											HashMap programadaRevertir = (HashMap)programadas.get(j);
    											programadaRevertir.put("fmod", new Timestamp(System.currentTimeMillis()));
    											programadaRevertir.put("cuser_mod", usuario);
    											programadaRevertir.put("estado_new", Constantes.PROG_ACEPTADA);
    											programadaRevertir.put("ind_matri", Constantes.INACTIVO);
    											log.debug("programada: "+programadaRevertir);
    											vacacionesDetalleDAO.actualizaProgramacion(programadaRevertir);
    										}
                                        }else{
        									//3.1 Desactivamos flag de matrimonio
                                        	HashMap prms = new HashMap();
        									prms.put("licencia",Constantes.VACACION_PROGRAMADA);//49
        									prms.put("cuser_mod", usuario);
        									prms.put("fmod",new Timestamp(System.currentTimeMillis()));
        									prms.put("ind_matri", null);
        									prms.put("est_id", "0");
        									vacacionesDetalleDAO.updateIndmatriTipo49ByNroRefSolByMatrimonio(prms);	
                                        	
                                            HashMap hmMensajeNoActualiza = new HashMap();
                                            hmMensajeNoActualiza.put("mensaje", "No se activaron las vacaciones programadas desactivadas por la licencia "+lic.get("anno")+"-"+lic.get("numero")+" para el colaborador " + cod_pers + ", ya que existen nuevas vacaciones programadas.");
                                            hmMensajeNoActualiza.put("tipo", "warning");                                                                        
                                            listaMensajes.add(hmMensajeNoActualiza);
                                        }
	                                    
										HashMap hmMensaje = new HashMap();															
										hmMensaje.put("mensaje", "La licencia "+lic.get("anno")+"-"+lic.get("numero")+" ha sido eliminada correctamente."); //detvac.anno_vac = lic.anno
										hmMensaje.put("tipo", "success");
										listaMensajes.add(hmMensaje);																
		
										lista.remove(lic);
									}
									
								}else{ //demï¿½s licencias
								
									T1273DAO t1273dao = new T1273DAO(dbpool);
	
									Map prms = new HashMap();
									prms.put("periodo", lic.get("periodo"));
									prms.put("numero", new Integer(lic.get("numero").toString()));
									prms.put("ffinicio", lic.get("ffinicio"));
									prms.put("cod_pers", lic.get("cod_pers"));
									prms.put("licencia", lic.get("licencia"));
									log.debug("t1273dao.deleteByPrimaryKey(prms)... prms:"+prms);
									t1273dao.deleteByPrimaryKey(prms);
									
									HashMap hmMensaje = new HashMap();																//jquispecoi 06/2014
									hmMensaje.put("mensaje", "La licencia "+lic.get("anno")+"-"+lic.get("numero").toString().trim()+" ha sido eliminada correctamente.");
									hmMensaje.put("tipo", "success");
									listaMensajes.add(hmMensaje);																	//
	
									lista.remove(lic);
								}
							} else {
								HashMap hmMensaje = new HashMap();																//jquispecoi 06/2014
								hmMensaje.put("mensaje", "La licencia "+lic.get("anno")+"-"+lic.get("numero").toString().trim()+" NO ha sido eliminada por pertenecer a un periodo cerrado.");
								hmMensaje.put("tipo", "error");
								listaMensajes.add(hmMensaje);																	//
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

		return lista;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public BeanCompensaOnom procesarOnomastico(String dbpool, String codPers,
			boolean compNoLaboral, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		BeanCompensaOnom beanCompensa = null;
		try {
			
			T02DAO daoPerson = new T02DAO();
//			T1270DAO daoTurno = new T1270DAO();
			
			HashMap empleado = daoPerson.joinWithT12T99ByCodPers(dbpool,codPers, null);
			
			String fNacim = (String) empleado.get("t02f_nacim");
//			boolean compensaHoras = !((String) empleado.get("t02cod_regl")).trim().equals(Constantes.REGIMEN_LABORAL_276);

			String anioActual = Utiles.dameAnho(Utiles.obtenerFechaActual());
			fNacim = fNacim.substring(0, 6) + anioActual;

/*			
			//Vemos si su cumpleanoos no cae un dia en el que el empleado tenga vacaciones
			T1282DAO daoVac = new T1282DAO(dbpool);
			//prac-jcallo
			Map prm = new HashMap();
			prm.put("cod_pers", codPers);
			prm.put("fechaIni", fNacim);
			prm.put("fechaFin", "");
			log.debug("metodo procesarOnomastico... prm:"+prm);
			if (daoVac.findByCodPersFIniFFin(prm)) {
				throw new Exception(
						"No se le puede asignar permiso porque para esa fecha tiene programadas vacaciones.");
			}

			//Vemos si su cumpleaos no cae un dia en el que el empleado tenga
			// licencia
			
			//PRAC-JCALLO
			T1273DAO daoLic = new T1273DAO(dbpool);
			Map params = new HashMap();
			params.put("cod_pers", codPers);
			params.put("fecha1", fNacim);
			params.put("fecha2", "");
			params.put("numero", "");
			log.debug("daoLic.findByCodPersFIniFFin(params)... params:"+params);
			if (daoLic.findByCodPersFIniFFin(params)){//(dbpool, codPers, fNacim, "", "")) {
				throw new Exception(
						"No se le puede asignar permiso porque para esa fecha tiene programada una licencia.");
			}
			*/
			
			//registramos la licencia
			registrarLicenciaOnomastico(dbpool, codPers, fNacim, usuario);
			
			//buscamos el turno de la persona
			//BeanTurnoTrabajo turno = daoTurno.joinWithT45ByCodFecha(dbpool,codPers,fNacim);
			
			//en caso el turno no sea operativo y el trabajador no pertenezca al regimen 276
			/*if (turno!=null && !turno.isOperativo() && compensaHoras){
				
				TurnoTrabajoFacadeHome facadeHome = (TurnoTrabajoFacadeHome) ServiceLocator.getInstance().getRemoteHome(
						TurnoTrabajoFacadeHome.JNDI_NAME, 
						TurnoTrabajoFacadeHome.class
						);
				TurnoTrabajoFacadeRemote remote = facadeHome.create();
				
				HashMap datos = new HashMap();
				datos.put("dbpool",dbpool);
				datos.put("codPers",codPers);
				datos.put("codUO",(String)empleado.get("t02cod_uorg"));
				datos.put("fechaLic",fNacim);
				datos.put("fechaGen",Utiles.obtenerFechaActual());
				datos.put("horComp","1");
				datos.put("diasComp","8");
				datos.put("usuario",usuario);
				
				//registramos los turnos de compensacion
				remote.registrarTurnoCompensa(datos);
			}*/

			beanCompensa = new BeanCompensaOnom();
			beanCompensa.setFechaIni(Utiles.obtenerFechaActual());

		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return beanCompensa;
	}	

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void registrarLicenciaOnomastico(String dbpool, String codPers,
			String fecha, String usuario) throws IncompleteConversationalState,
			RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {
			
			T02DAO persDAO = new T02DAO();
			T1282DAO daoVac = new T1282DAO(dbpool);
			T1273DAO daoLic = new T1273DAO(dbpool);
			
			DataSource dsOracle = (DataSource) ServiceLocator.getInstance().getDataSource("java:comp/env/pool_oracle");

			//Verificamos que el trabajador no tenga licencias programadas para
			// la fecha
			
			//PRAC-JCALLO			
			Map params = new HashMap();
			params.put("cod_pers", codPers);
			params.put("fecha1", fecha);
			params.put("fecha2", "");
			params.put("numero", "");
			log.debug("metodo registrarLicenciaOnomastico... params:"+params);
			if (daoLic.findByCodPersFIniFFin(params)){//(dbpool, codPers, fNacim, "", "")) {
				throw new Exception(
						"El trabajador ya tiene una licencia programada para la fecha de su onomastico.");
			}

			//Verificamos que el trabajador no tenga vacaciones programadas
			// para la fecha
			//prac-jcallo
			Map prm = new HashMap();
			prm.put("cod_pers", codPers);
			prm.put("fechaIni", fecha);
			prm.put("fechaFin", "");
			log.debug("daoVac.findByCodPersFIniFFin(prm) ... prm:"+prm);
			if (daoVac.findByCodPersFIniFFin(prm)) {
				throw new Exception(
						"El trabajador tiene vacaciones programadas para la fecha de su onomastico.");
			}

			HashMap beanPersona = persDAO.joinWithT12T99ByCodPers(dbpool,
					codPers, null);
			//prac-jcallo
			//T1273CMPHome cmpHome = (T1273CMPHome) ServiceLocator.getInstance().getLocalHome(T1273CMPHome.JNDI_NAME);

			OracleSequenceDAO seqDAO = new OracleSequenceDAO();
			String numero = seqDAO.getSequenceDS(dsOracle,Constantes.SEQ_LICENCIA);

			T1276DAO dao = new T1276DAO();
			String periodoActual = (dao.findPeriodoActual(dbpool)).getPeriodo();

			java.sql.Timestamp fLic = Utiles.stringToTimestamp(fecha
					+ " 00:00:00");
			String codUO = (String) beanPersona.get("t02cod_uorg");

			/*cmpHome.create(periodoActual, Utiles.obtenerAnhoActual(),
							new java.lang.Short(numero), codUO, fLic,
							fLic, codPers, 1, Constantes.LICENCIA_ONOMASTICO,
							"", "", "", "&fecha=", new java.sql.Timestamp(System
									.currentTimeMillis()), usuario);*/
			//prac-jcallo
			
			T1273DAO t1273dao = new T1273DAO(dbpool);
			Map datos = new HashMap();
			datos.put("periodo", periodoActual);
			datos.put("anno", new FechaBean().getAnho());
			datos.put("numero", new Integer(numero));
			datos.put("u_organ", codUO);
			datos.put("ffinicio", fLic);
			datos.put("ffin", fLic);
			datos.put("cod_pers", codPers);
			datos.put("qdias", new Integer(1));
			datos.put("licencia", Constantes.LICENCIA_ONOMASTICO);
			datos.put("anno_ref", "");
			datos.put("numero_ref", null);
			datos.put("area_ref", "");
			datos.put("observ", "&fecha=");
			datos.put("fcreacion", new FechaBean().getTimestamp());
			datos.put("cuser_crea", usuario);//revisar todaviaaa
			log.debug("t1273dao.registrarLicenciaGeneral(datos)... datos:"+datos);
			t1273dao.registrarLicenciaGeneral(datos);
			

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
	public boolean trabajadorTieneLicencia(String dbpool, String codPers,
			String fecha1, String fecha2) throws IncompleteConversationalState,
			RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		boolean res = false;
		try {
			//prac-jcallo
			T1273DAO dao = new T1273DAO(dbpool);
			Map params = new HashMap();
			params.put("cod_pers", codPers);
			params.put("fecha1", fecha1);
			params.put("fecha2", fecha2);
			params.put("numero", "");
			log.debug("metodo trabajadorTieneLicencia... params:"+params);
			res = dao.findByCodPersFIniFFin(params); //(dbpool, codPers,fecha1, fecha2, "");

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
	public float obtenerDiasAcumulados(String dbpool, String codPers,
			String anno, String licencia) throws IncompleteConversationalState,
			RemoteException {

		float res = 0;
		BeanMensaje beanM = new BeanMensaje();
		try {
			//prac-jcallo
			T1273DAO dao = new T1273DAO(dbpool);
			Map params = new HashMap();
			params.put("cod_pers", codPers);
			params.put("anno", anno);
			params.put("licencia", licencia);
			log.debug("metodo obtenerDiasAcumulados... params:"+params);
			res = dao.findDiasAcumulados(params); //(dbpool, codPers, anno, licencia);

		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return res;
	}
	//HAY DUDAS EN ESTE METODO PARECIERA QUE NUNCA DEVOLVIERA NADA, O ES UN CASO DE ENVIO DE parametros por referencia
	//y dos no se sabe quien lo usa
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public Map agregaDatosLicenciaMedica(String dbpool, HashMap licencia)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {
			//PRAC-JCALLO
			T1273DAO dao = new T1273DAO(dbpool);
			T8151DAO archivoDAO = new T8151DAO(dbpool);
			T5864DAO parametroDAO = new T5864DAO(dbpool);
			Map params = new HashMap();
			params.put("cod_pers", licencia.get("cod_pers")); 
			params.put("periodo", licencia.get("periodo"));
			params.put("numero", licencia.get("numero"));  
			params.put("fechaIni", licencia.get("ffinicio"));			
			//dao.joinWithT1274(dbpool, licencia);
			log.debug("metodo agregaDatosLicenciaMedica ... params:"+params);
			Map joinWithT1274 = dao.joinWithT1274(params);
			if(joinWithT1274==null){
				joinWithT1274=new HashMap();
			}
			
			log.debug("buscar data de archivo");
			joinWithT1274.put("archivoEncontrado", Boolean.FALSE);
			joinWithT1274.put("numArchivo", new Integer(0));
			joinWithT1274.put("numSeqdoc", new Integer(0));
			joinWithT1274.put("numArcDet", "0");
			
			Integer numArchivo = (Integer) joinWithT1274.get("num_archivo");
			if (numArchivo != null && !numArchivo.equals(new Integer(0))) {
				joinWithT1274.put("numArchivo", numArchivo);
				joinWithT1274.put("numSeqdoc", new Integer(0));
				
				List archivos = archivoDAO .findArchivosByNumArchivoNumSeqDoc(numArchivo, new Integer(0));
				if (archivos != null && !archivos.isEmpty()) {
					Map object = (Map) archivos.get(0);
					joinWithT1274.put("numArcDet",object.get("num_arcdet") + "");
					joinWithT1274.put("archivoEncontrado", Boolean.TRUE);
				}
			}			
			
			String codDiagnostico = (String) joinWithT1274.get("cod_cie10");
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
					joinWithT1274.put("diagnostico", diagnostico);
				}
			}
			
			return joinWithT1274 ;/*params.get("cod_pers"), 
			params.get("periodo"), params.get("numero"),  params.get("fechaIni")*/

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
	public BeanCompensaOnom prepararCompOnom(String dbpool, String codPers)
			throws IncompleteConversationalState, RemoteException {

		BeanCompensaOnom bComp = null;
		BeanMensaje beanM = new BeanMensaje();
		try {
			T02DAO daoPers = new T02DAO();

			//Obtenemos la fecha de su onomastico
			HashMap beanPersona = daoPers.joinWithT12T99ByCodPers(dbpool,codPers, null);
			String fNacim = (String) beanPersona.get("t02f_nacim");
			
			String anioActual = Utiles.dameAnho(Utiles.obtenerFechaActual());
			fNacim = fNacim.substring(0, 6) + anioActual;
						
			//Verificamos que se encuentre dentro del rango de
			//fechas permitido
			T1279DAO movDAO = new T1279DAO();
			int numDiasAntes = movDAO.findNumDiasByTipo(dbpool,
					Constantes.LICENCIA_ONOMASTICO, "1");
			int numDiasDespues = movDAO.findNumDiasByTipo(dbpool,
					Constantes.LICENCIA_ONOMASTICO, "2");

			AsistenciaFacadeHome facadeHome = (AsistenciaFacadeHome) ServiceLocator
					.getInstance().getRemoteHome(
							AsistenciaFacadeHome.JNDI_NAME,
							AsistenciaFacadeHome.class);
			AsistenciaFacadeRemote facadeRemote = facadeHome.create();

			int valida = facadeRemote.validaRango(dbpool,
					Constantes.LICENCIA_ONOMASTICO, fNacim, Utiles
							.obtenerFechaActual());

			if (valida < 0) {
				throw new IncompleteConversationalState(
						"La cantidad m&aacute;xima de d&iacute;as de anticipaci&oacute;n para el registro de la solicitud de onom&aacute;stico es de "
								+ numDiasAntes + " d&iacute;as.");
			}

			if (valida > 0) {
				throw new IncompleteConversationalState(
						"La cantidad m&aacute;xima de d&iacute;as posteriores para el registro de la solicitud de onom&aacute;stico es de "
								+ numDiasDespues + " d&iacute;as.");
			}

			bComp = new BeanCompensaOnom();
			if (this.trabajadorTieneLicencia(dbpool, codPers, fNacim, "")) {
				bComp.setFechaIni(fNacim);
			}

		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return bComp;
	}

	
	//Valida las licencias que se modificarï¿½n.
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList validaLicenciaModif(HashMap mapa, HashMap tipoMov,
			boolean validaRango) throws IncompleteConversationalState,	
			RemoteException {

		ArrayList mensajes = new ArrayList();
		try {
			log.debug("entro a metodo validaLicenciaModif.... mapa:"+mapa+"     , tipoMov:"+tipoMov+",         validaRango:"+validaRango);
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
			if (log.isDebugEnabled()) log.debug("validaLicenciaModif(regimenCol): "+regimenCol);					
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
			//T4821DAO t4821DAO = new T4821DAO(ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dgsp"));//ICAPUNAY 07/06 PASE 2012-64 LABOR EXCEPCIONAL
			
		
			if (log.isDebugEnabled()) log.debug("entro a LE de validaLicenciaModif");
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
								log.debug("validaLicenciaModif entro(diferencia3>diferencia2)");

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
												mensajes.add("El día "+fechaEval+" es feriado o fin de semana.");	

											}else		
												fechas.add(fechaEval);				//jquispecoi: 03/2014 add else
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
								log.debug("validaLicenciaModif entro2(diferencia3<=diferencia2)");
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
												mensajes.add("El día "+fechaEval+" es feriado o fin de semana.");
											}else		
												fechas.add(fechaEval);				//jquispecoi: 03/2014 add else
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
										mensajes.add("El día "+fechaEval+" es feriado o fin de semana.");
									}else		
										fechas.add(fechaSig);				//jquispecoi: 03/2014 add else
								}																	
							}else{
								log.debug("no existe fechaSig:" + fechaSig);
							}														
																					
						}
					}		
				}
				
				if (log.isDebugEnabled()) log.debug("termino for(esDiaHabil): "+esDiaHabil);				
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
			prms.put("numero", "");
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
		if (log.isDebugEnabled()) log.debug("LicenciaFacade.validaLicenciaModif(mensajes): "+mensajes);
		return mensajes;
	}
		
	
}