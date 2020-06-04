package pe.gob.sunat.rrhh.asistencia.ejb;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pe.gob.sunat.framework.core.bean.MensajeBean;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.core.ejb.FacadeException;
import pe.gob.sunat.framework.core.ejb.StatelessAbstract;
import pe.gob.sunat.framework.core.pattern.ServiceLocator;
import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.framework.util.mail.Correo;
import pe.gob.sunat.rrhh.asistencia.dao.T1273DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T130DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T132DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T3464DAO;
import pe.gob.sunat.rrhh.dao.CorreoDAO;
import pe.gob.sunat.rrhh.dao.T02DAO;
import pe.gob.sunat.sp.asistencia.bean.BeanHoraExtra;
import pe.gob.sunat.sp.asistencia.bean.BeanTurnoTrabajo;
import pe.gob.sunat.sp.asistencia.dao.T1270DAO;
import pe.gob.sunat.sp.asistencia.ejb.MantenimientoFacadeHome;
import pe.gob.sunat.sp.asistencia.ejb.MantenimientoFacadeRemote;
import pe.gob.sunat.sp.dao.T99DAO;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;

import pe.gob.sunat.utils.bd.OracleSequenceDAO;


/**
 * 
 * @ejb.bean name="AutorizaLaborFacadeEJB" 
 * 			 description="AutorizaLaborFacade"
 *           jndi-name="ejb/facade/rrhh/asistencia/AutorizaLaborFacadeEJB"
 *           type="Stateless" view-type="remote"
 * 
 * @ejb.home remote-class="pe.gob.sunat.rrhh.asistencia.ejb.AutorizaLaborFacadeHome"
 *           extends="javax.ejb.EJBHome"
 * 
 * @ejb.interface remote-class="pe.gob.sunat.rrhh.asistencia.ejb.AutorizaLaborFacadeRemote"
 *                extends="javax.ejb.EJBObject"
 * 
 * @ejb.resource-ref res-ref-name="jdbc/dcsp" res-type="javax.sql.DataSource" res-auth="Container"
 * @weblogic.resource-description res-ref-name="jdbc/dcsp" jndi-name="jdbc/dcsp"
 * 
 * @ejb.resource-ref res-ref-name="jdbc/dgsp" res-type="javax.sql.DataSource" res-auth="Container"
 * @weblogic.resource-description res-ref-name="jdbc/dgsp" jndi-name="jdbc/dgsp"
 * 
 * @ejb.resource-ref res-ref-name="pool_oracle" res-type="javax.sql.DataSource" res-auth="Container"
 * @weblogic.resource-description res-ref-name="pool_oracle" jndi-name="jdbc/dcscad"
 * 
 * 
 * @ejb.transaction-type type="Container"
 * 
 * @weblogic.pool   max-beans-in-free-pool = "10"
 * 					initial-beans-in-free-pool = "5"
 * 
 * @weblogic.clustering home-is-clusterable = "True"
 * 						stateless-bean-is-clusterable = "True"
 * 
 * @weblogic.transaction-descriptor trans-timeout-seconds = "300"
 * 
 * @weblogic.enable-call-by-reference True
 * 
 * @jboss.container-configuration name="Standard Stateless SessionBean"
 * @jboss.version="3.2"
 *  
 * @author EBENAVID. </a>
 * Copyright: Copyright (c) 2007
 * Company: SUNAT
 * @version 1.0
 */

public class AutorizaLaborFacade extends StatelessAbstract {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ServiceLocator sl =  ServiceLocator.getInstance();
	private Log log = LogFactory.getLog(getClass());
	
	
	/**
	 * Inserta un registro en la tabla t3464altalabor.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * 
	 * @throws FacadeException
	 *
	public Map registrarAutorizacion(Map datos, Map seguridad)
			throws FacadeException {
		try {
			T02DAO t02dao = new T02DAO(sl.getDataSource("java:comp/env/jdbc/dcsp"));
			log.debug("trabajador "+(String)datos.get("trabajador"));
			Map pertenece = (HashMap)t02dao.joinWithT12T99((String) datos.get("trabajador"),seguridad);
			log.debug("pertenece "+ pertenece);
			StringBuffer nombreStr = new StringBuffer("");
			

			if (pertenece==null || pertenece.size()==0) {
				datos = null;
			} else {
				nombreStr.append(((String)pertenece.get("t02ap_pate")).trim()).append(" ");
				nombreStr.append(((String)pertenece.get("t02ap_mate")).trim()).append(", ");
				nombreStr.append(((String)pertenece.get("t02nombres")).trim()).append(" ");
								
				String nombre =  nombreStr.toString();
				datos.put("destrabajador",nombre);
				T3464DAO t3464dao = new T3464DAO(sl.getDataSource("java:comp/env/jdbc/dcsp"));
				boolean existeAut = t3464dao.existeAutorizado((String) datos.get("trabajador"),(String) datos.get("fecha"), seguridad);
				if (!existeAut) {
					this.grabarAutorizacion(datos);
					FechaBean fb = new FechaBean((String) datos.get("fecha"));
					String formato = fb.getFormatDate("dd' de 'MMMM' del 'yyyy");
					String texto = "Usted ha sido Autorizado por <strong>" +
					(String)datos.get("desusuario")+
					" </strong> a realizar labores fuera de su turno de trabajo el dia <strong>" +
					formato + "</strong>.";
					String asunto = "RRHH ASISTENCIA - AUTORIZACION LABOR EXCEPCIONAL";
					datos = this.enviarCorreo(datos,texto, asunto);
				}
				//PRAC-ASANCHEZ 28/08/2009
				String valor = "";
				if(existeAut){
					valor = "SI";
				}else{
					valor = "NO";
				}
				datos.put("existeAutorizacionRegistrada", valor);
				//
			}

		} catch (DAOException d) {
			log.error("*** SQL Error ****",d);			
			StringBuffer msg = new StringBuffer().append("Error en la grabacion. T3464DAO ")
												 .append(d.toString());
        	throw new FacadeException(this, msg.toString());
        } catch (Exception e) {
			throw new FacadeException(this, e.toString());
		}
        return datos;
	}
*/	

	/**PRAC-ASANCHEZ 26/08/2009
	 * Inserta en la t3464 y actualiza la t130
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * 
	 * @throws FacadeException
	 */
	public Map autorizarLE(Map datos, Map seguridad)
			throws FacadeException {
		try {
			//actualizo en T130
			String codPers = (String) datos.get("trabajador");
			String fautor = (String) datos.get("fautor");
			String hinic = (String) datos.get("hinic");
				
			T130DAO t130 = new T130DAO(sl.getDataSource("java:comp/env/jdbc/dgsp"));
			t130.updateEstadoLE(codPers,fautor,hinic);

/*			
			//inserto en la T3464
			T3464DAO t3464dao = new T3464DAO(sl.getDataSource("java:comp/env/jdbc/dgsp"));
			boolean existeAut;
				
			existeAut = t3464dao.existeAutorizado((String) datos.get("trabajador"),(String) datos.get("fautor"), seguridad);
			if(!existeAut){
				boolean res = t3464dao.insertAltaLabor(codPers,fautor,
					(String) datos.get("usuario"),(String) datos.get("usuario"));
				//el metodo devuelve un boolean
			}
*/
		} catch (DAOException d) {
			log.error("*** SQL Error ****",d);			
			StringBuffer msg = new StringBuffer().append("Error en la grabacion. T3464DAO ")
												 .append(d.toString());
        	throw new FacadeException(this, msg.toString());
        } catch (Exception e) {
			throw new FacadeException(this, e.toString());
		}
        return datos;
	}	
	
	/**
	 * Busca las dias con Labor Excepcional de un trabajador y si han sido autorizadas.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * 
	 * @throws DAOException
	 * @throws RemoteException
	 */
	public Map buscarAutorizacion(Map datos, Map seguridad)
			throws FacadeException {
		try {
			T02DAO t02dao = new T02DAO(sl.getDataSource("java:comp/env/jdbc/dcsp"));
			T99DAO codigoDAO = new T99DAO();
			T130DAO t130DAO = new T130DAO(sl.getDataSource("java:comp/env/jdbc/dcsp"));
			String dbpool = "jdbc/dcsp";
			FechaBean dia = new FechaBean();
			String fechafinComp = dia.getFormatDate("dd/MM/yyyy");
			
			//JRR - 25/09/2009
			if (datos.get("REGISTRO_COMPENSACION").toString().equalsIgnoreCase("TRUE")) {

				//cantidad de horas de turno de trabajo del dia de hoy
				T1270DAO tpDAO = new T1270DAO();
				int horas = 0;
				int minutos = 0;
				BeanTurnoTrabajo turnoTrab = tpDAO.joinWithT45ByCodFecha(dbpool, (String)datos.get("trabajador"), datos.get("fecha").toString());

				if (turnoTrab != null) {
					horas = Math.round(Utiles.obtenerHorasDifDia(turnoTrab.getHoraIni(), turnoTrab.getHoraFin()));
					minutos = 60 * horas;
				}				
				datos.put("minutosTurno", ""+minutos);
				
				
			} else {
			
			
			log.debug("trabajador "+(String)datos.get("trabajador"));
			//Map pertenece = (HashMap)t02dao.joinWithT12T99((String) datos.get("trabajador"),seguridad); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
			Map pertenece = (HashMap)t02dao.joinWithT12T99_ByAllUUOOsJefe((String) datos.get("trabajador"),seguridad); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
			log.debug("pertenece "+ pertenece);
			StringBuffer nombreStr = new StringBuffer("");

			if (pertenece==null || pertenece.size()==0) {
				datos = null;
			} else {
				
				nombreStr.append(((String)pertenece.get("t02ap_pate")).trim()).append(" ");
				nombreStr.append(((String)pertenece.get("t02ap_mate")).trim()).append(", ");
				nombreStr.append(((String)pertenece.get("t02nombres")).trim()).append(" ");
 		
				String nombre =  nombreStr.toString();

				datos.put("destrabajador",nombre);

				//JRR - 02/03/2009
				String fechainiComp = codigoDAO.findParamByCodTabCodigo(dbpool,
						Constantes.CODTAB_PARAMETROS_ASISTENCIA,
						Constantes.FECHA_INICIO_COMPENSACION);
				if (log.isDebugEnabled()) log.debug("fechainiComp: " +fechainiComp + " - fechafinComp: " +fechafinComp);
				
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");						//ICR 08/05/2015-PAS20155E230000154-labor antigua  (autorizacion y compensacion) y labor nueva (sol. compensacion) mayor o igual fecha ingreso a sunat
				Date dtFechainiComp = sdf.parse(fechainiComp);
				Date fecha_ingreso = (Date)pertenece.get("t02f_ingsun");
				if(fecha_ingreso.after(dtFechainiComp)){
					fechainiComp = sdf.format(fecha_ingreso);										
				}
				if (log.isDebugEnabled()) log.debug("fechainiComp final: " +fechainiComp );		//ICR 08/05/2015 fecha mÌnima de visualizaciÛn
				

				Integer saldo = t130DAO.findSaldobyRango((String)datos.get("trabajador"), fechainiComp, fechafinComp);//.floatValue();
				if (log.isDebugEnabled()) log.debug("SALDO: " + saldo);
				//
				
				//T132DAO t132dao = new T132DAO("java:comp/env/jdbc/dcsp");
				//float saldo = t132dao.obtenerSaldo((String)datos.get("trabajador"));
				datos.put("saldo",String.valueOf(saldo));
				

				/*********************************
				//26/06/2009 - JRR
				//cantidad de horas de turno de trabajo del dia de hoy
				T1270DAO tpDAO = new T1270DAO();
				int horas = 0;
				int minutos = 0;
				BeanTurnoTrabajo turnoTrab = tpDAO.joinWithT45ByCodFecha(dbpool, (String)datos.get("trabajador"), fechafinComp);

				if (turnoTrab != null) {
					horas = Math.round(Utiles.obtenerHorasDifDia(turnoTrab.getHoraIni(), turnoTrab.getHoraFin()));
					minutos = 60 * horas;
				}				
				/*********************************
				
				//JRR
				datos.put("minutosTurno", ""+minutos);
				
				//if (saldo < 480){
				if (saldo < minutos){
					datos.put("saldo","0");
					datos.put("arreglo", null);
				}
				else {
					*/
					
				datos.put("uuoo",(String)pertenece.get("cod_uorg"));
				T130DAO t130dao = new T130DAO(sl.getDataSource("java:comp/env/jdbc/dcsp"));
				//List afechas = (List)t130dao.findfechas((String)datos.get("trabajador"));ICAPUNAY-PAS20144EB20000059-Visualizar informacion desde 01/01/2009
				List afechas = (List)t130dao.findfechas_Mod((String)datos.get("trabajador"),fechainiComp);//ICAPUNAY-PAS20144EB20000059-Visualizar informacion desde 01/01/2009
				
				//JRR - 30/09/2009
				//Map mapaAcumulado = t130dao.findAcumuladoLE((String)datos.get("trabajador"));ICAPUNAY-PAS20144EB20000059-Visualizar informacion desde 01/01/2009
				Map mapaAcumulado = t130dao.findAcumuladoLE_Mod((String)datos.get("trabajador"),fechainiComp);//ICAPUNAY-PAS20144EB20000059-Visualizar informacion desde 01/01/2009
				if (mapaAcumulado!=null && mapaAcumulado.get("total")!=null) {
					datos.put("acumuladoLE", mapaAcumulado.get("total").toString().trim());
				} else {
					datos.put("acumuladoLE", "0");
				}
				
				
//JRR - 26/06/2009
//Ya no necesito validar autorizacion aca pues lo valido en el query de findfechas de t130dao
//				T3464DAO t3464dao = new T3464DAO(sl.getDataSource("java:comp/env/jdbc/dcsp"));
				BeanHoraExtra he = new BeanHoraExtra();
//				boolean existeAut = false;
				List ffechas = new ArrayList();
				log.debug("afechas "+afechas.size());
				for (int i = 0;i<afechas.size();i++){
					he = (BeanHoraExtra) afechas.get(i);
					log.debug("he "+he);
/*					existeAut = t3464dao.existeAutorizado(he.getCodPers(),he.getFechaCompensacion(), null);
					if (!existeAut) {
						he.setObservacion("NO");
					} else
					{
						he.setObservacion("SI");
					}*/
					ffechas.add(he);
				}
				datos.put("arreglo", ffechas);
//				}
				
			}
			
			}

		} catch (DAOException d) {
			log.error("*** SQL Error ****",d);			
			StringBuffer msg = new StringBuffer().append("Error en la grabacion. T3464DAO ")
												 .append(d.toString());
        	throw new FacadeException(this, msg.toString());
        } catch (Exception e) {
			throw new FacadeException(this, e.toString());
		}
        return datos;
	}
	
	/**FRD 01/07/2009
	 * Busca Labor Excepcional de un trabajador y no han sido autorizadas.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * 
	 * @throws FacadeException
	 */
	public Map buscarLENoAutorizadas(Map datos, Map seguridad) throws FacadeException {
		Map resulBusqueda = new HashMap();
		try {
			
			T02DAO t02dao = new T02DAO(sl.getDataSource("java:comp/env/jdbc/dcsp"));
//			T3464DAO t3464dao = new T3464DAO(sl.getDataSource("java:comp/env/jdbc/dcsp"));
			
			//log.debug("trabajador "+(String)datos.get("trabajador"));
			//Map pertenece = (HashMap)t02dao.joinWithT12T99((String) datos.get("trabajador"),seguridad); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
			Map pertenece = (HashMap)t02dao.joinWithT12T99_ByAllUUOOsJefe((String) datos.get("trabajador"),seguridad); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
			//log.debug("pertenece "+ pertenece);
			StringBuffer nombreStr = new StringBuffer("");

			if (pertenece==null || pertenece.size()==0) {
				resulBusqueda = null;
			} else {
				nombreStr.append(((String)pertenece.get("t02ap_pate")).trim()).append(" ");
				nombreStr.append(((String)pertenece.get("t02ap_mate")).trim()).append(", ");
				nombreStr.append(((String)pertenece.get("t02nombres")).trim()).append(" ");
				String nombre =  nombreStr.toString();
				datos.put("nombreTrabajador",nombre);
				datos.put("uuoo",(String)pertenece.get("cod_uorg"));
				datos.put("fecha_ingreso", (Date)pertenece.get("t02f_ingsun"));		//ICR 08/05/2015-PAS20155E230000154-labor antigua  (autorizacion y compensacion) y labor nueva (sol. compensacion) mayor o igual fecha ingreso a sunat
				T130DAO t130dao = new T130DAO(sl.getDataSource("java:comp/env/jdbc/dcsp"));
				List leNoAutorizadas = (List)t130dao.findLENoAutorizadas(datos);
				
				//JROJAS4 - 16/09/2009
				Map mapAux = new HashMap();
				float decimal;
				for(int i=0; i< leNoAutorizadas.size() ;i++){
					mapAux = (Map)leNoAutorizadas.get(i);
					decimal = Float.parseFloat(mapAux.get("horasle").toString());
					String horaMinuto = Utiles.dameFormatoHHMM(decimal);
					mapAux.put("horaMinuto", horaMinuto);
				}
				//	
				
				resulBusqueda.put("leNoAutorizadas",leNoAutorizadas);
				resulBusqueda.put("datos",datos);
			}

		} catch (DAOException d) {
			log.error("*** SQL Error ****",d);			
			StringBuffer msg = new StringBuffer().append("Error en la grabacion. ")
												 .append(d.toString());
        	throw new FacadeException(this, msg.toString());
        } catch (Exception e) {
			throw new FacadeException(this, e.toString());
		}
		return resulBusqueda;
	}
	
	/**
	 * Inserta un registro en la tabla t3464altalabor.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * 
	 * @throws DAOException
	 * @throws RemoteException
	 */
	public Map registrarCompensacion(String[] params, Map datos)
			throws FacadeException {
		try {
			T130DAO t130 = new T130DAO(sl.getDataSource("java:comp/env/jdbc/dcsp"));
			boolean cruce = t130.findByCodPersFAutor((String)datos.get("trabajador"),(String)datos.get("fecha"));
			if (cruce){
				datos.put("cruce","SI");
			} else {

				/*********************************/
				//26/06/2009 - JRR
				String dbpool = "jdbc/dcsp";
                //SI PERIODO ESTA ABIERTO
    			MantenimientoFacadeHome spMantenimientoFacadeHome = (MantenimientoFacadeHome) sl.getRemoteHome(
    					MantenimientoFacadeHome.JNDI_NAME,	MantenimientoFacadeHome.class);
    			MantenimientoFacadeRemote spMantenimientoFacadeRemote;
    			
				spMantenimientoFacadeRemote = spMantenimientoFacadeHome.create();
				
				FechaBean fecha = new FechaBean((String)datos.get("fecha"));
				if(log.isDebugEnabled()) log.debug("Periodo: " + fecha.getFormatDate("yyyyMM"));
				
				boolean cerrado = spMantenimientoFacadeRemote.periodoCerradoAFecha(dbpool,
						fecha.getFormatDate("yyyyMM"), Utiles.obtenerFechaActual());

				if(log.isDebugEnabled()) log.debug("Periodo cerrado?: " + cerrado);	
				
				if (cerrado) {
					datos.put("cerrado","SI");
				} else {
					datos.put("cerrado","NO");
					
					//Turno no Operativo y cantidad de horas de turno de trabajo del dia de hoy
					T1270DAO tpDAO = new T1270DAO();
					int horas = 0;
					int minutos = 0;
					BeanTurnoTrabajo turnoTrab = tpDAO.joinWithT45ByCodFecha(dbpool, (String)datos.get("trabajador"), (String)datos.get("fecha"));

					if ((turnoTrab==null) || (turnoTrab!= null && turnoTrab.isOperativo())) {
						datos.put("diaCompOperativo","SI");
					} else {
						datos.put("diaCompOperativo","NO");
						
						horas = Math.round(Utiles.obtenerHorasDifDia(turnoTrab.getHoraIni(), turnoTrab.getHoraFin()));
						minutos = 60 * horas;
						datos.put("minutos", String.valueOf(minutos));
						if (log.isDebugEnabled()) log.debug("minutos: " + minutos);
						
						//JRR - 26/06/2009
						T99DAO codigoDAO = new T99DAO();
						String fechainiComp = codigoDAO.findParamByCodTabCodigo(dbpool,
								Constantes.CODTAB_PARAMETROS_ASISTENCIA,
								Constantes.FECHA_INICIO_COMPENSACION);
						if (log.isDebugEnabled()) log.debug("fechainiComp: " +fechainiComp + " - fechafin: " +(String)datos.get("fecha"));
						Integer saldo = t130.findSaldobyRango((String)datos.get("trabajador"), fechainiComp, (String)datos.get("fecha"));//.floatValue();
						if (log.isDebugEnabled()) log.debug("SALDO: " + saldo);
						//Integer saldo = t130.findSaldobyFAutor((String)datos.get("trabajador"),(String)datos.get("fecha"));
						
					/*********************************/				
						
						//if (saldo.intValue() >=480)
						if (saldo.intValue() >= minutos)					
						{

							T1273DAO t1273 = new T1273DAO(sl.getDataSource("java:comp/env/jdbc/dcsp"));
							//prac-jcallo
							Map prms = new HashMap();
							prms.put("cod_pers", (String)datos.get("trabajador"));
							prms.put("fecha1", (String)datos.get("fecha"));
							prms.put("tipo", "");
							prms.put("numero", "");
							//if (t1273.findByCodPersTipoFIniFFin((String)datos.get("trabajador"),"",(String)datos.get("fecha"),""))
							if (t1273.findByCodPersTipoFIniFFin(prms))
							{
								datos.put("licencia","SI");
							}
							else{ 
										
								this.actualizarSaldos(params,datos);
								
								//WERR-PAS20155E230300132
								/*FechaBean fb = new FechaBean((String) datos.get("fecha"));
								String formato = fb.getFormatDate("dd' de 'MMMM' del 'yyyy");
								String texto = "Usted tiene una <strong>Licencia de Compensaci√≥n</strong> autorizada por " +
								(String)datos.get("desusuario")+
								" </strong> para el dia <strong>" +
								formato + "</strong>.";
								String asunto = "RRHH ASISTENCIA - COMPENSACION LABOR EXCEPCIONAL";
								datos = this.enviarCorreo(datos,texto, asunto);*/
								
								/*List arreglo = (List)datos.get("arreglo");
								BeanHoraExtra he = null;//WERR-PAS20155E230300132
								float totalHE=0;//WERR-PAS20155E230300132
								 for (int i = 0; i < params.length; i++) {//WERR-PAS20155E230300132
									 he = (BeanHoraExtra) arreglo.get(Integer.parseInt(params[i]));//WERR-PAS20155E230300132
									 log.debug("acumulado de horas extras-->"+he.getAcumulado());//WERR-PAS20155E230300132
									 totalHE=totalHE+he.getAcumulado();//WERR-PAS20155E230300132
									 if (params != null) { //WERR-PAS20155E230300132
									 if(totalHE>Integer.parseInt(Constantes.SALDO_HORASEXTRA.trim())){//WERR-PAS20155E230300132
										 log.debug("envia el correo-->"+t1273.findByCodPersTipoFIniFFin(prms));//WERR-PAS20155E230300132
											FechaBean fb = new FechaBean((String) datos.get("fecha"));//WERR-PAS20155E230300132
											String formato = fb.getFormatDate("dd' de 'MMMM' del 'yyyy");//WERR-PAS20155E230300132
											String texto = "Usted tiene una <strong>Licencia de Compensaci√≥n</strong> autorizada por " +
											(String)datos.get("desusuario")+
											" </strong> para el dia <strong>" +
											formato + "</strong>.";//WERR-PAS20155E230300132
											String asunto = "RRHH ASISTENCIA - COMPENSACION LABOR EXCEPCIONAL";//WERR-PAS20155E230300132
											datos = this.enviarCorreo(datos,texto, asunto);//WERR-PAS20155E230300132
									 	}//WERR-PAS20155E230300132
									 }//WERR-PAS20155E230300132
								 }*/
								 //WERR-PAS20155E230300132
	
							}
						} else {
							datos.put("SINSALDO","SI");
						}
						
					}//JRR					

				}//JRR
				
			}
			

		} catch (DAOException d) {
			log.error("*** SQL Error ****",d);			
			StringBuffer msg = new StringBuffer().append("Error en la grabacion. Registrar Compensacion ")
												 .append(d.toString());
        	throw new FacadeException(this, msg.toString());
        } catch (Exception e) {
			throw new FacadeException(this, e.toString());
		}
        return datos;
	}
	
	
	/**
	 * Inserta un registro en la tabla t3464altalabor.
	 * 
	 * @ejb.transaction type="RequiresNew"
	 * 
	 * @throws DAOException
	 * @throws RemoteException
	 *
	private void grabarAutorizacion(Map datos)
			throws FacadeException {
		try {
			T3464DAO t3464dao = new T3464DAO(sl.getDataSource("java:comp/env/jdbc/dgsp"));
			t3464dao.insertAltaLabor((String) datos.get("trabajador"),(String) datos.get("fecha"),
					(String) datos.get("usuario"),(String) datos.get("usuario"));
			
			

		} catch (DAOException d) {
			log.error("*** SQL Error ****",d);			
			StringBuffer msg = new StringBuffer().append("Error en la grabacion. T3464DAO ")
												 .append(d.toString());
        	throw new FacadeException(this, msg.toString());
        } catch (Exception e) {
			throw new FacadeException(this, e.toString());
		}
	}
	*/
	
	/**
	 * Actualiza saldos en la tabla t130horext, t132horacu y graba la licencia correspondiente en la
	 * tabla t1273licencia 
	 * @ejb.transaction type="RequiresNew"
	 * 	 
	 * @throws DAOException
	 * @throws RemoteException
	 */
    private void actualizarSaldos(String[] params, Map datos)
            throws FacadeException {

    	List arreglo = (List)datos.get("arreglo");
    	//FechaBean fb = new FechaBean();
    	float nuevomonto = 0;
        try {
            
            T130DAO t130 = new T130DAO(sl.getDataSource("java:comp/env/jdbc/dgsp"));

            T1273DAO t1273 = new T1273DAO(sl.getDataSource("java:comp/env/jdbc/dgsp"));

            if (params != null) {
                BeanHoraExtra he = null;
                
                //float descontar = 480;
                
                
				/*********************************/
				//26/06/2009 - JRR
				//cantidad de horas de turno de trabajo del dia de hoy

                /*int minutos = 0;
                String dbpool = "jdbc/dcsp";
				T1270DAO tpDAO = new T1270DAO();
				int horas = 0;
				BeanTurnoTrabajo turnoTrab = tpDAO.joinWithT45ByCodFecha(dbpool, (String)datos.get("trabajador"), (String)datos.get("fecha"));
				if (turnoTrab != null) {
					horas = Math.round(Utiles.obtenerHorasDifDia(turnoTrab.getHoraIni(), turnoTrab.getHoraFin()));
					minutos = 60 * horas;
				}*/
                
				int minutos = Integer.parseInt(datos.get("minutos").toString());
				if (log.isDebugEnabled()) log.debug("actualizarSaldos - minutos: " + minutos);
				float descontar = minutos;
				/*********************************/				
                
                
                for (int i = 0; i < params.length; i++) {

                    he = (BeanHoraExtra) arreglo.get(Integer
                            .parseInt(params[i]));
                    //fb.setFecha(he.getFechaAutorizacion());
                    descontar = descontar - he.getAcumulado();
                    if (descontar <= 0 ){
                    	nuevomonto = descontar * -1;
                    	t130.updateLE(he.getCodPers(), he.getFechaCompensacion(),he.getHoraIni(), nuevomonto, (String)datos.get("usuario"));
                    	break;
                    } else {
                    	t130.updateLE(he.getCodPers(), he.getFechaCompensacion(),he.getHoraIni(), 0, (String)datos.get("usuario"));
                    }
                    
                }
                String saldo = (String) datos.get("saldo");
                float saldo1 = Float.parseFloat(saldo);
                //saldo1 = saldo1 - 480;
                saldo1 = saldo1 - minutos;

                //JRR 26/06/2009 - Ya no es necesario la t132 aqui tampoco
                //T132DAO t132 = new T132DAO(sl.getDataSource("java:comp/env/jdbc/dgsp"));

                datos.put("saldo",String.valueOf(saldo1));
                //t132.modificarAcum(datos);
                
                /*********************************/	
                //JRR 26/06/2009
                //AQUI DEBEMOS GENERAR NUMERO DE LICENCIA CON SECUENCIADOR

                DataSource dsOracle = sl.getDataSource("java:comp/env/pool_oracle");
                OracleSequenceDAO seqDAO = new OracleSequenceDAO();
                String numero = seqDAO.getSequenceDS(dsOracle,Constantes.SEQ_LICENCIA);
                
                datos.put("numero", numero);
                
                //SI PERIODO ESTA ABIERTO
/*    			MantenimientoFacadeHome spMantenimientoFacadeHome = (MantenimientoFacadeHome) sl.getRemoteHome(
    					MantenimientoFacadeHome.JNDI_NAME,	MantenimientoFacadeHome.class);
    			MantenimientoFacadeRemote spMantenimientoFacadeRemote;
    			
				spMantenimientoFacadeRemote = spMantenimientoFacadeHome.create();
				
				FechaBean fecha = new FechaBean((String)datos.get("fecha"));
				if(log.isDebugEnabled()) log.debug("Periodo: " + fecha.getFormatDate("yyyyMM"));
				
				boolean cerrado = spMantenimientoFacadeRemote.periodoCerradoAFecha(dbpool,
						fecha.getFormatDate("yyyyMM"), Utiles.obtenerFechaActual());

				if(log.isDebugEnabled()) log.debug("Periodo cerrado?: " + cerrado);
				
				if (!cerrado)  */
                
                //WERR-PAS20155E230300132
				//t1273.registrarLicenciaCompensacion(datos); 
                boolean registro= t1273.registrarLicenciaCompensacion(datos);
                if (registro){
                	FechaBean fb = new FechaBean((String) datos.get("fecha"));
					String formato = fb.getFormatDate("dd' de 'MMMM' del 'yyyy");
					String texto = "Usted tiene una <strong>Licencia de Compensaci√≥n</strong> autorizada por " +
					(String)datos.get("desusuario")+
					" </strong> para el dia <strong>" +
					formato + "</strong>.";
					String asunto = "RRHH ASISTENCIA - COMPENSACION LABOR EXCEPCIONAL";
					this.enviarCorreo(datos,texto, asunto);
                }                
                //WERR-PAS20155E230300132
					
                /*********************************/	

            }
            
        }  catch (DAOException d) {
			log.error("*** SQL Error ****",d);			
			StringBuffer msg = new StringBuffer().append("Error en la grabacion. Actualizacion de Saldos de LE")
												 .append(d.toString());
        	throw new FacadeException(this, msg.toString());
        } catch (Exception e) {
			throw new FacadeException(this, e.toString());
		}

    }
    
	/**
	 * Envia Correo indicando la Autorizacion de Labor Excepcional.
	 * 
	 * @ejb.transaction type="NotSupported"
	 * @throws DAOException
	 * @throws RemoteException
	 */
	private Map enviarCorreo(Map datos, String texto, String asunto) throws FacadeException  {

		try {		
			//Obtenemos el correo de la persona autorizada
			CorreoDAO correo = new CorreoDAO(sl.getDataSource("java:comp/env/jdbc/dcsp"));
		
		
		
			FechaBean bfh = new FechaBean();
			String date = bfh.getFormatDate("dd' de 'MMMM' del 'yyyy");
			String headerString = bfh.getDiasemana()+", "+date;
		

			String trabajadormail = correo.findCorreoByRegistro((String) datos.get("trabajador"));
		
			//Formateamos el mensaje a enviar
			StringBuffer mensaje = new StringBuffer("<html>\r\n")
			.append("<head>\r\n")
			.append("<title>NOTIFICACI&Oacute;N DE ASISTENCIA</title>\r\n")
			.append("\r\n")
			.append("<style>"
					+ ".T1 {font-style  : normal;text-transform : uppercase;font-size :10pt;color: #FFFFFF;background  : #336699;font-family : Tahoma,Verdana,Arial,Helvetica,sans-serif ;}"
					+ "</style>")
			.append("</head>\r\n")
			.append("<body bgcolor=\"#FFFFFF\" text=\"#000000\">\r\n")
			.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n")
			.append("  <tr>\r\n")
			.append("<table width='100%' align='center' class='T1'><tr><td><center><strong>NOTIFICACI&Oacute;N DE ASISTENCIA</strong></center></td></tr></table><br><br>")
			.append("<table><tr><td><strong>Sr(a)(ita).</strong> <em>")
			.append((String) datos.get("destrabajador")).append("</em></td></tr></table><br>")
			.append("	<td valign=\"top\" align=\"left\"><b><font color=\"#000066\" size=\"3\"> " ).append(texto)
			.append("</font></b></td>\r\n")
			.append("  </tr>\r\n")
			.append("<br><br><table><tr><td><strong><em>").append(headerString)
			.append("</em></strong></td></tr></table>")
			.append("  <tr>\r\n")
			.append("	<td>&nbsp;</td>\r\n")
			.append("  </tr>\r\n")
			.append("</table>\r\n")
			.append("</body>\r\n")
			.append("</html>\r\n");
			
			//Enviamos el correo informado el fin del proceso
			Correo objCorreo = new Correo(mensaje.toString());
        	objCorreo.agregarDestinatario(trabajadormail);
        	objCorreo.setAsunto(asunto);
        	objCorreo.enviarHtml();
			
		} catch (Exception e) {
			log.error("Error en procesar",e);
			MensajeBean beanM = new MensajeBean();
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new FacadeException(this,beanM);
		}
		
		return datos;
	}


}
