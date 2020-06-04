package pe.gob.sunat.rrhh.asistencia.ejb;

import java.io.File;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.ejb.CreateException;
import javax.sql.DataSource;

import pe.gob.sunat.batch.dao.QueueDAO; //ICAPUNAY - PAS20165E230300096 - Alerta de Cambio de turno
import pe.gob.sunat.framework.core.bean.MensajeBean;
import pe.gob.sunat.framework.core.bean.ParamBean;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.core.ejb.FacadeException;
import pe.gob.sunat.framework.core.ejb.StatelessAbstract;
import pe.gob.sunat.framework.core.pattern.ServiceLocator;
import pe.gob.sunat.framework.util.Propiedades;
import pe.gob.sunat.framework.util.cache.dao.ParamDAO;
import pe.gob.sunat.framework.util.common.T3Service;
import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.framework.util.lang.Numero;
import pe.gob.sunat.framework.util.lang.Ordenamiento;
import pe.gob.sunat.framework.util.mail.Correo;
import pe.gob.sunat.framework.util.mail.CorreoException;
import pe.gob.sunat.rrhh.asistencia.dao.T1271DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T1276DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T1277DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T1281DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T1282DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T1454DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T1455DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T1456DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T3701DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T4502DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T4562DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T4563DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T72DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T1270DAO; //ICAPUNAY - PAS20165E230300096 - Alerta de Cambio de turno
import pe.gob.sunat.rrhh.asistencia.dao.T8167DAO; //ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
//ICAPUNAY - PAS20171U230300074 - Mejoras vacaciones
import pe.gob.sunat.rrhh.asistencia.dao.T9036DAO; 
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import java.io.FileOutputStream;
import pe.gob.sunat.framework.util.dao.SequenceDAO;
//FIN 
import pe.gob.sunat.rrhh.dao.CorreoDAO;
import pe.gob.sunat.rrhh.dao.T02DAO;
import pe.gob.sunat.rrhh.dao.T12DAO;
import pe.gob.sunat.rrhh.dao.T99DAO;
import pe.gob.sunat.sol.BeanFechaHora;
import pe.gob.sunat.sp.asistencia.ejb.AsistenciaFacadeHome;
import pe.gob.sunat.sp.asistencia.ejb.AsistenciaFacadeRemote;
import pe.gob.sunat.sp.asistencia.ejb.HoraExtraFacadeHome;
import pe.gob.sunat.sp.asistencia.ejb.HoraExtraFacadeRemote;
import pe.gob.sunat.sp.asistencia.ejb.ReporteFacadeHome;
import pe.gob.sunat.rrhh.planilla.dao.TPeriodosDAO;
import pe.gob.sunat.rrhh.programacion.ejb.ProgramacionFacadeHome;// ICAPUNAY - PAS20171U230300074 - Mejoras vacaciones
import pe.gob.sunat.rrhh.programacion.ejb.ProgramacionFacadeRemote;// ICAPUNAY - PAS20171U230300074 - Mejoras vacaciones
import pe.gob.sunat.sp.asistencia.ejb.ReporteFacadeRemote;
import pe.gob.sunat.sp.asistencia.ejb.VacacionFacadeHome;
import pe.gob.sunat.sp.asistencia.ejb.VacacionFacadeRemote;
import pe.gob.sunat.tecnologia.menu.bean.MenuCliente;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;
import weblogic.utils.classfile.expr.NewArrayExpression;


/**
 * 
 * @ejb.bean name="AutomatizarAsistenciaFacadeEJB" 
 * 			 description="AutomatizarAsistenciaFacade"
 *           jndi-name="ejb/facade/rrhh/asistencia/AutomatizarAsistenciaFacadeEJB"
 *           type="Stateless" view-type="remote"
 * 
 * @ejb.home remote-class="pe.gob.sunat.rrhh.asistencia.ejb.AutomatizarAsistenciaFacadeHome"
 *           extends="javax.ejb.EJBHome"
 * 
 * @ejb.interface remote-class="pe.gob.sunat.rrhh.asistencia.ejb.AutomatizarAsistenciaFacadeRemote"
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
 * @ejb.resource-ref description="DS a la base de Secuencias" res-ref-name="jdbc/dcbdseq" res-type="java.sql.DataSource" res-auth="Container" 
 * @weblogic.resource-description jndi-name="jdbc/dcbdseq" res-ref-name="jdbc/dcbdseq"
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
 * @author PRAC-JCALLO </a>
 * Copyright: Copyright (c) 2008
 * Company: SUNAT
 * @version 1.0
 */

public class AutomatizarAsistenciaFacade extends StatelessAbstract {
	
	ServiceLocator sl = ServiceLocator.getInstance();
	
	/* ICAPUNAY - TRABAJADORES CON VACIONES PROGRAMADAS ACEPTADAS O ACTIVAS - 28/02/2011 */      
    Propiedades propiedades = new Propiedades(getClass(), "/correo.properties");
    Propiedades constantes_properties = new Propiedades(getClass(), "/constantes.properties");
    /* FIN ICAPUNAY - TRABAJADORES CON VACIONES PROGRAMADAS ACEPTADAS O ACTIVAS - 28/02/2011 */
   

	/**
	 * Encargado de generar registro de asistencia en forma automatico
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * 
	 * @throws DAOException
	 * @throws RemoteException
	 */	
	public void mainSchedulerAsistencia() throws FacadeException, RemoteException{
		
		try {
			
			/**** PARA VALIDAR LA CONFIGURACION T3***/
			
			String flagCtrl = "-";
			try {
			    flagCtrl = T3Service.getInstance("fileT3").getT3FileProperty("asistencia.t3", "flagCtrl");
			} catch(Exception e) {
				log.error("Error al obtener flag de control del archivo asistencia.t3");
				return;
			}

			if(flagCtrl != null) {
			    flagCtrl = flagCtrl.trim();
		        if(!flagCtrl.equals("1")) {
		            if(log.isDebugEnabled()){log.debug("Scheduler de este nodo no esta activado");}
		            if(log.isDebugEnabled()){log.debug("valor de flagControl:"+flagCtrl); }
		            return;
		        }
			} else {
				log.info("Scheduler de este nodo no esta inscrito");
			    return;
			}

			
			/*** fin de la configuracion t3**/
			
			
			
			if(log.isDebugEnabled()) log.debug("METODO MAIN SCHEDULER.....");
			FechaBean fecBeanAct = new FechaBean();
			String hora = fecBeanAct.getFormatDate("HH");
			String dbpool = "jdbc/dcsp";
			//JVILLACORTA - 13/06/2011 - ALERTA DE SOLICITUDES
			String dia_actual = fecBeanAct.getDiasemana();// Monday or Lunes
			//String dia_actual = "Lunes";
			if (log.isDebugEnabled()) log.debug("fecBeanAct.getDiasemana(): "+dia_actual);
			//FIN - JVILLACORTA - 13/06/2011 - ALERTA DE SOLICITUDES
			
			if(log.isDebugEnabled()) log.debug("SE EJECUTO A LAS "+fecBeanAct.getFormatDate("dd/MM/yyyy HH:mm:ss"));
			T99DAO t99dao = new T99DAO(dbpool);// ICAPUNAY - PAS20171U230300074 - Mejoras vacaciones
			
			DataSource dcsp = sl.getDataSource(dbpool);			
			/** parametro de minimo de dias laborables**/
			//ParamBean prmbean = t99dao. buscar(new String []{"470"}, dcsp, "03");
			ParamDAO prmdao = new ParamDAO();
			List lista = (List)prmdao.cargarNoCache("select t99codigo, t99descrip, t99abrev, t99siglas  from t99codigos where t99cod_tab = '470' and t99tip_desc ='D' and t99estado = '1' and t99descrip like '"+hora+"%'", dcsp, prmdao.LIST);
			if(log.isDebugEnabled()) log.debug("hora:"+hora);
			//JVILLACORTA - 03/11/2011 - MODIFICA ALERTA DE SOLICITUDES
			List listaDiaSem = (List)prmdao.cargarNoCache("select t99codigo, t99descrip from t99codigos where t99cod_tab ='901' and t99estado='1' and t99_modulo='1'", dcsp, prmdao.LIST);
			//FIN - JVILLACORTA - 03/11/2011 - MODIFICA ALERTA DE SOLICITUDES
			List diasAlertaPerSinTurno = (List)prmdao.cargarNoCache("select t99codigo, t99descrip, t99abrev, t99siglas  from t99codigos where t99cod_tab = '510' and t99tip_desc ='D' and t99codigo='49'", dcsp, prmdao.LIST);
			//ICAPUNAY - PAS20171U230300074 - Mejoras vacaciones
			Date fechaActual = fecBeanAct.getSQLDate();
			String sfechaActual = fecBeanAct.getFormatDate("dd/MM/yyyy");
			log.debug("fechaActual: "+fechaActual);
			ParamBean prmFechaEjecucion1 = t99dao.buscar(new String []{"510"}, dcsp, "40");
			String fechaEjecucion1=prmFechaEjecucion1!=null?prmFechaEjecucion1.getDescripcion().trim():"";
			ParamBean prmFechaEjecucion2 = t99dao.buscar(new String []{"510"}, dcsp, "41");
			String fechaEjecucion2=prmFechaEjecucion2!=null?prmFechaEjecucion2.getDescripcion().trim():"";
			log.debug("fechaEjecucion1: "+fechaEjecucion1);
			log.debug("fechaEjecucion2: "+fechaEjecucion2);
			//
			
			if( lista.size() > 0 ) {
				
				ParamBean prmbean = (ParamBean)lista.get(0);
				if(log.isDebugEnabled()) log.debug("SI ENCONTRO ALGUN DATO.. : codigo:"+prmbean.getCodigo()+", descrip:"+prmbean.getDescripcion()+", descripcion corta :"+prmbean.getDescripcionCorta());
				if ( "1".equals(prmbean.getCodigo().trim()) ) {//GENERAR ASISTENCIA
					if(log.isDebugEnabled()) log.debug("HORA : "+hora+" GENERAR ASISTENCIA");
					generarRegistroAsistencia();
				}else if ( "2".equals(prmbean.getCodigo().trim()) ) {//PROCESAR ASISTENCIA
					if(log.isDebugEnabled()) log.debug("HORA : "+hora+" PROCESAR ASISTENCIA");
					procesarAsistencia();
				}else if ( "3".equals(prmbean.getCodigo().trim()) ) {//GENERAR SALDOS VACACIONALES
					if(log.isDebugEnabled()) log.debug("HORA : "+hora+" GENERAR SALDOS VACACIONALES");
					generarSaldosVacacionales();
				}else if ( "4".equals(prmbean.getCodigo().trim()) ) {//ENVIAR ALERTA DE INASISTENCIA
					if(log.isDebugEnabled()) log.debug("HORA : "+hora+" ENVIAR ALERTA DE INASISTENCIA");
					enviarAlertaInasistencias();
				}
				else if ( "5".equals(prmbean.getCodigo().trim()) ) {//GENERAR NOVEDADES GENERACION
					if(log.isDebugEnabled()) log.debug("HORA : "+hora+" GENERAR NOVEDADES");
					novedad_generarRegistroAsistencia();
				}
				else if ( "6".equals(prmbean.getCodigo().trim()) ) {//GENERAR NOVEDADES PROCESO
					if(log.isDebugEnabled()) log.debug("HORA : "+hora+" NOVEDADES PROCESO ");
					novedad_procesarAsistencia();
				}
				/* ICAPUNAY - TRABAJADORES CON VACIONES PROGRAMADAS ACEPTADAS O ACTIVAS - 28/02/2011 */	
				else if ( "7".equals(prmbean.getCodigo().trim()) ) {//ENVIAR ALERTAS DE VACACIONES A TRABAJADORES
					if(log.isDebugEnabled()) log.debug("HORA : "+hora+" ENVIAR ALERTAS VACACIONES ");
					enviarAlertaVacaciones();
				}
				/* FIN ICAPUNAY - TRABAJADORES CON VACIONES PROGRAMADAS ACEPTADAS O ACTIVAS - 28/02/2011 */	
				//JVILLACORTA - 03/11/2011 - MODIFICA ALERTA DE SOLICITUDES 
				else if ( "8".equals(prmbean.getCodigo().trim()) ) {//ENVIAR ALERTA DE SOLICITUDES					
					if( listaDiaSem.size() > 0 ) {
						ParamBean parmbean = (ParamBean)listaDiaSem.get(0);
						if(log.isDebugEnabled()) log.debug("DIA DE SEMANA.. : codigo:"+parmbean.getCodigo()+", descrip:"+parmbean.getDescripcion());
						if ( dia_actual.equals(parmbean.getDescripcion().trim()) ) {							
							enviarAlertaSolicitudes();
						}
					} 				
				}
				else if ( "9".equals(prmbean.getCodigo().trim()) ) {//ENVIAR ALERTA DE MOVIMIENTOS DE ASISTENCIA					
					if( listaDiaSem.size() > 0 ) {
						ParamBean parmbean = (ParamBean)listaDiaSem.get(0);
						if(log.isDebugEnabled()) log.debug("DIA DE SEMANA.. : codigo:"+parmbean.getCodigo()+", descrip:"+parmbean.getDescripcion());
						if ( dia_actual.equals(parmbean.getDescripcion().trim()) ) {							
							enviarAlertaMovAsistencia();
						}
					}					 
				}
				//FIN - JVILLACORTA - 03/11/2011 - MODIFICA ALERTA DE SOLICITUDES
				//INICIO - MTM - 18/04/2012 - PROCESAR LABOR EXCEPCIONAL
				else if("10".equals(prmbean.getCodigo().trim()) ) {
					if(log.isDebugEnabled()) log.debug("HORA : "+hora+" PROCESAR LABOR EXCEPCIONAL");
					procesarLaborExcepcional();
				}
				else if("11".equals(prmbean.getCodigo().trim()) ) {
					if(log.isDebugEnabled()) log.debug("HORA : "+hora+" PROCESAR LABOR EXCEPCIONAL");
					novedad_procesarLaborExcepcional();
				}
				//FIN - MTM - 18/04/2012 - PROCESAR LABOR EXCEPCIONAL
				//ICAPUNAY - PAS20165E230300096 - Alerta de Cambio de turno
				else if("12".equals(prmbean.getCodigo().trim()) ) {
					if(log.isDebugEnabled()) log.debug("HORA : "+hora+" ENVIAR ALERTAS CAMBIO DE TURNO");
					enviarAlertaCambioTurno();
				}
				//FIN ICAPUNAY - PAS20165E230300096 - Alerta de Cambio de turno
				//ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
				else if("13".equals(prmbean.getCodigo().trim()) ) {
					if(log.isDebugEnabled()) log.debug("HORA : "+hora+" PROCESAR CLIMA LABORAL");
					procesarClimaLaboral();
				}
				//FIN ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
				//ICAPUNAY - PAS20171U230300074 - Mejoras vacaciones
				else if ( "14".equals(prmbean.getCodigo().trim()) ) {					
					if(log.isDebugEnabled()) log.debug("alerta directivo con programacion (FECHA EJECUCION): "+fechaEjecucion1);
					if (sfechaActual.equals(fechaEjecucion1)) {
						//T9036DAO t9036dao = new T9036DAO(dcsp); //notificaciones a directivos
						//Map notificacion = t9036dao.findNotificacionEnMesByFecha(fechaEjecucion1);
						//if (notificacion==null){//no se ejecuto en el mes la alerta
							enviarAlertaConProgramacionVacaciones();
						//}					
					}									
				}
				else if ( "15".equals(prmbean.getCodigo().trim()) ) {					
					if(log.isDebugEnabled()) log.debug("alerta directivo sin programacion (FECHA EJECUCION): "+fechaEjecucion2);
					if (sfechaActual.equals(fechaEjecucion2)) {
						//T9036DAO t9036dao = new T9036DAO(dcsp); //notificaciones a directivos
						//Map notificacion = t9036dao.findNotificacionEnMesByFecha(fechaEjecucion2);
						//if (notificacion==null){
							enviarAlertaSinProgramacionVacaciones();
						//}					
					}									
				}
				//FIN ICAPUNAY
				
				//DTARAZONA - PAS - ALERTA AUTOMÁTICA DE VACACIONES PENDIENTES DE APROBAR 22/01/2018
				else if("16".equals(prmbean.getCodigo().trim())){
					if(log.isDebugEnabled()) log.debug("HORA: "+hora+" ENVIAR ALERTA DE SOLICITUD VACACIONAL PENDIENTES DE APROBACIÓN");
					
					enviarAlertaSolicitudVacacionesPendientes();
				}
				else if ( "17".equals(prmbean.getCodigo().trim()) ) {//ENVIAR ALERTA DE MOVIMIENTOS DE ASISTENCIA					
					if( listaDiaSem.size() > 0 ) {
						ParamBean parmbean = (ParamBean)diasAlertaPerSinTurno.get(0);
						if(log.isDebugEnabled()) log.debug("Dia a enviar la alerta:"+parmbean.getDescripcion()+", Hoy:"+dia_actual);
						if ( dia_actual.equals(parmbean.getDescripcion().trim()) ) {							
							enviarAlertaPersonalSinTurno();
						}
					}					 
				}
				//FIN - PAS - ALERTA AUTOMÁTICA DE VACACIONES PENDIENTES DE APROBAR 22/01/2018
			}
			if(log.isDebugEnabled()) log.debug(" SE EJECUTO EL SCHEDULER..termino a las "+new FechaBean().getFormatDate("dd/MM/yyyy HH:mm:ss"));
		} catch (DAOException d) {
			log.error("*** SQL Error ****",d);			
			StringBuffer msg = new StringBuffer().append("ERROR PROCESO AUTOMATICO SCHEDULER ")
												 .append(d.toString());
        	throw new FacadeException(this, msg.toString());
        } catch (Exception e) {
			throw new FacadeException(this, e.toString());
		}
	}//FIN METODO
	
	/**
	 * Encargado de generar registro de asistencia en forma automatico
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * 
	 * @throws DAOException
	 * @throws RemoteException
	 */	
	public void generarRegistroAsistencia() throws FacadeException, RemoteException, CreateException {
		
		try {
			FechaBean fecha_act = new FechaBean();//hallando fecha actual
			
			String fechaFin = fecha_act.getFormatDate("dd/MM/yyyy");
			fechaFin = fecha_act.getOtraFecha(fechaFin, -1, Calendar.DAY_OF_YEAR );//fecha fin es un dia anterior a la fecha actual
			String criterio = "1";//por unidad organizacional
			Map seguridad = new HashMap();
			String dbpool = "jdbc/dgsp";
			
			DataSource ds = sl.getDataSource(dbpool);			
			T99DAO t99dao = new T99DAO(dbpool);
			
			ParamBean prmUsuRrhh = t99dao.buscar(new String []{"510"}, ds, "15");//parametro de usuario quien ejecuta asistencia
			if(log.isDebugEnabled()) log.debug("prmUsuario : nro_registro :"+prmUsuRrhh.getDescripcion()+", usuario:"+prmUsuRrhh.getDescripcionCorta());
			/** definiendo los parametros**/
			HashMap mapa = new HashMap();
			mapa.put("dbpool", dbpool);			
			mapa.put("criterio", criterio);
			mapa.put("valor", "");
			mapa.put("fechaIni", fechaFin);//es para el mismo dia, no existe rango de dias al momento de generar asistencia 
			mapa.put("fechaFin", fechaFin);
			mapa.put("codPers", prmUsuRrhh.getDescripcion());//numero de regitro por ejem 4710
			mapa.put("usuario", prmUsuRrhh.getDescripcionCorta());//USUARIO por ejemplo NMEDINA
			mapa.put("observacion", "Registro de asistencia del " + fechaFin + " al " + fechaFin);
			
			/** OBTENIENDO LISTADO DE UNIDADES ORGANIZACIONALES ACTIVOS**/
			T12DAO t12dao = new T12DAO("jdbc/dcsp");
			Map prms=new HashMap();
			prms.put("criterio", "");
			prms.put("valor", "");
			prms.put("orden", "1");
			List listaUnidades=  t12dao.findByCodDesc(prms);//OBTENEMOS TODAS LAS UNIDADES ORGANIZACIONALES ACTIVAS
			
			Map mUniOrg = new HashMap();//mapa de unidad organizacional
			
			Map prm = new  HashMap();
			prm.put("criterio", criterio);
			prm.put("seguridad", seguridad);
			
			AsistenciaFacadeHome asistenciaFacadeHome = (AsistenciaFacadeHome) sl.getRemoteHome(
					AsistenciaFacadeHome.JNDI_NAME,	AsistenciaFacadeHome.class);
			AsistenciaFacadeRemote asistenciaFacadeRemote;
			
			for (int i = 0; i < listaUnidades.size(); i++) {
				mUniOrg = (Map)listaUnidades.get(i);
				
				mapa.put("valor", mUniOrg.get("t12cod_uorga").toString());//CODIGO DE UNIDAD ORGANIZACIONAL A PROCESAR
				asistenciaFacadeRemote = asistenciaFacadeHome.create();	
				
				/** GENERANDO REGISTRO DE ASISTENCIA **/
				if(log.isDebugEnabled()) log.debug("GENERANDO ASISTENCIA PARA mapa: "+mapa+"   con el usuario: "+(String)mapa.get("usuario"));
				asistenciaFacadeRemote.generarRegistros(mapa, (String)mapa.get("usuario"));				
			}
		
		} catch (DAOException d) {
			log.error("*** SQL Error ****",d);			
			StringBuffer msg = new StringBuffer().append("ERROR PROCESO AUTOMATICO DE GENERACION ASISTENCIA: ")
												 .append(d.toString());
        	throw new FacadeException(this, msg.toString());
        } catch (Exception e) {
			throw new FacadeException(this, e.toString());
		}
	}//FIN METODO
	
	/**
	 * encargado de procesar la asistencia de forma automatica
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * 
	 * @throws DAOException
	 * @throws RemoteException
	 */	
	public void procesarAsistencia() throws FacadeException, RemoteException{
		try {
			FechaBean fecha_act = new FechaBean();//hallando fecha actual
			
			//String fechaIni = "";			
			String fechaFin = fecha_act.getFormatDate("dd/MM/yyyy");
			fechaFin = fecha_act.getOtraFecha(fechaFin, "yyyy/MM/dd", -1, Calendar.DAY_OF_YEAR );//fecha fin es un dia anterior a la fecha actual yyy/mm/dd
			String criterio = "1";//por unidad organizacional
			HashMap seguridad = new HashMap();
			
			String dbpool = "jdbc/dgsp";
			
			DataSource ds = sl.getDataSource(dbpool);			
			T99DAO t99dao = new T99DAO(dbpool);
			
			ParamBean prmUsuRrhh = t99dao.buscar(new String []{"510"}, ds, "15");//parametro de usuario quien ejecuta asistencia
			if(log.isDebugEnabled()) log.debug("prmUsuario : nro_registro :"+prmUsuRrhh.getDescripcion()+", usuario:"+prmUsuRrhh.getDescripcionCorta());
			String periodo = "";			
			String valor="";			
			String codPers = prmUsuRrhh.getDescripcion().trim();//numero de registro por ejm 4710
			String usuario = prmUsuRrhh.getDescripcionCorta().trim();//nombre de usuario
			//JRR 09072008 - Incluir Papeletas
			String indPap = "1"; //"0";
			
			/** OBTENIENDO EL PERIODO CORRESPONDIENTE A LA FECHA ACTUAL**/
			T1276DAO periodoDao = new T1276DAO("jdbc/dcsp");		
			Map mPeriodo = periodoDao.findPeriodoByFecha(fechaFin);//formato de la fecha yyyy/MM/dd
			if(mPeriodo == null || mPeriodo.isEmpty()){
				if(log.isDebugEnabled()) log.debug("error: Periodo NO asignado para la fecha :"+fechaFin);
			} else {
				periodo = (mPeriodo.get("periodo") != null ) ? mPeriodo.get("periodo").toString():"";
			}
			
			//fechaIni = mPeriodo.get("finicio").toString();//fecha de inicio se toma el inicio del periodo
			
			/** OBTENIENDO LISTADO DE UNIDADES ORGANIZACIONALES ACTIVOS**/
			T12DAO t12dao = new T12DAO("jdbc/dcsp");
			Map prms=new HashMap();
			prms.put("criterio", "");
			prms.put("valor", "");
			prms.put("orden", "1");
			List listaUnidades=  t12dao.findByCodDesc(prms);//OBTENEMOS TODAS LAS UNIDADES ORGANIZACIONALES ACTIVAS
			
			Map mUniOrg = new HashMap();//mapa de unidad organizacional

			AsistenciaFacadeHome spAssistenciaFacadeHome = (AsistenciaFacadeHome) sl.getRemoteHome(
					AsistenciaFacadeHome.JNDI_NAME,	AsistenciaFacadeHome.class);
			AsistenciaFacadeRemote spAsistenciaFacadeRemote;
			
			//JRR - 24/04/2010
			Map mProceso = new HashMap();
			mProceso.put("dbpool", dbpool);
			mProceso.put("regimen", "0");
			mProceso.put("periodo", periodo);
			mProceso.put("criterio", criterio);
			//mProceso.put("valor", valor);
			mProceso.put("codigo", codPers);
			mProceso.put("usuario", usuario);
			mProceso.put("seguridad", seguridad);
			mProceso.put("indPap", indPap);
			//
			
			for (int i = 0; i < listaUnidades.size(); i++) {
				mUniOrg = (Map)listaUnidades.get(i);
				
				valor = mUniOrg.get("t12cod_uorga").toString();//CODIGO DE UNIDAD ORGANIZACIONAL A PROCESAR
				
				mProceso.put("valor", valor);
				
				spAsistenciaFacadeRemote = spAssistenciaFacadeHome.create();	
				
				/** GENERANDO REGISTRO DE ASISTENCIA **/
				if(log.isDebugEnabled()) log.debug("**** PROCESANDO ASISTENCIA PARA LA UNIDAD : "+valor+" ****");
				//spAsistenciaFacadeRemote.procesarAsistencia(dbpool, periodo, criterio, valor, codPers, usuario, seguridad, indPap);				
				spAsistenciaFacadeRemote.procesarAsistencia(mProceso);
			}

		} catch (DAOException d) {
			log.error("*** SQL Error ****",d);			
			StringBuffer msg = new StringBuffer().append("ERROR PROCESO AUTOMATICO DE PROCESAR ASISTENCIA: ")
												 .append(d.toString());
        	throw new FacadeException(this, msg.toString());
        } catch (Exception e) {
			throw new FacadeException(this, e.toString());
		}
	}
	/*MTM*/
	/**
	 * encargado de procesar novedad de la labor excepcional de forma automatica
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * 
	 * @throws DAOException
	 * @throws RemoteException
	 */	
	public void novedad_procesarLaborExcepcional() throws FacadeException, RemoteException{
		try {
			FechaBean fecha_act = new FechaBean();//hallando fecha actual
			
			//String fechaIni = "";			
			String fechaFin = fecha_act.getFormatDate("dd/MM/yyyy");
			fechaFin = fecha_act.getOtraFecha(fechaFin, "dd/MM/yyyy", -1, Calendar.DAY_OF_YEAR );//fecha fin es un dia anterior a la fecha actual yyy/mm/dd
			String criterio = "0";
			HashMap seguridad = new HashMap();
			
			String dbpool = "jdbc/dgsp";
			
			DataSource ds = sl.getDataSource(dbpool);			
			T99DAO t99dao = new T99DAO(dbpool);
			pe.gob.sunat.sp.dao.T99DAO t99SpDAO = new pe.gob.sunat.sp.dao.T99DAO();	//ICAPUNAY 11/06/2012 NOVEDADES CON INDICADOR=2 DESDE FECHA PARAMETRICA
			
			ParamBean prmUsuRrhh = t99dao.buscar(new String []{"510"}, ds, "15");//parametro de usuario quien ejecuta asistencia
			if(log.isDebugEnabled()) log.debug("prmUsuario : nro_registro :"+prmUsuRrhh.getDescripcion()+", usuario:"+prmUsuRrhh.getDescripcionCorta());
			String periodo = "";			
			String valor="";			
			String codPers = prmUsuRrhh.getDescripcion().trim();//numero de registro por ejm 1548
			String usuario = prmUsuRrhh.getDescripcionCorta().trim();//nombre de usuario ejm auto_ebenavid
			
			/** OBTENIENDO LISTADO DE COLABORADORES**/
			T3701DAO t3701dao = new T3701DAO("jdbc/dcsp");
			Map prms=new HashMap();
			prms.put("regimen","10");
			//prms.put("proceso1","4");//ICAPUNAY 27/06/2012 SOLO PROCESA CON UN INDICADOR 2
			prms.put("proceso2","2");
			//ICAPUNAY 11/06/2012 NOVEDADES CON INDICADOR=2 DESDE FECHA PARAMETRICA			
			String fechaFiltro = t99SpDAO.findParamByCodTabCodigo(dbpool,Constantes.CODTAB_PARAMETROS_ASISTENCIA,Constantes.FECHA_INICIO_LABOR_EXCEPCIONAL);
			if(log.isDebugEnabled()) log.debug("fechaFiltro: "+fechaFiltro);
			prms.put("fechaFiltro2",new FechaBean(fechaFiltro).getSQLDate());
			//ICAPUNAY 11/06/2012 NOVEDADES CON INDICADOR=2 DESDE FECHA PARAMETRICA
			
			List listaColaborador=  t3701dao.findNovedadByRegimen(prms);//OBTENEMOS TODAS LAS NOVEDADES POR REGIMEN
			
			HoraExtraFacadeHome spHoraExtraFacadeHome = (HoraExtraFacadeHome) sl.getRemoteHome(
					HoraExtraFacadeHome.JNDI_NAME,	HoraExtraFacadeHome.class);
			HoraExtraFacadeRemote spHoraExtraFacadeRemote;
					
			Map mUniCol = new HashMap();//mapa de colaborador
			
			Map mProceso = new HashMap();
			mProceso.put("dbpool", dbpool);
			mProceso.put("criterio", criterio);
			mProceso.put("cod_pers", codPers);
			mProceso.put("codPers", codPers);//para registrar el log del proceso (1548)
			mProceso.put("usuario", usuario); //auto_ebenavid
			
			for (int i = 0; i < listaColaborador.size(); i++) {
			
				mUniCol = (Map)listaColaborador.get(i);	
				
				try{
					mProceso.put("fechaIni", mUniCol.get("fec_refer_desc").toString());
					mProceso.put("fechaFin", mUniCol.get("fec_refer_desc").toString());
					if(log.isDebugEnabled()) log.debug(""+mUniCol.get("fec_refer_desc").toString());
					mProceso.put("valor", mUniCol.get("cod_pers").toString());//Registro A PROCESAR
					String observacion = "Acumulación de Labor Excepcional del "+mUniCol.get("fec_refer")+" al "+mUniCol.get("fec_refer");
					mProceso.put("observacion", observacion);
					spHoraExtraFacadeRemote = spHoraExtraFacadeHome.create();	
					
					/** GENERANDO REGISTRO DE ASISTENCIA **/
					if(log.isDebugEnabled()) log.debug("**** PROCESANDO ASISTENCIA PARA EL COLABORADOR  ****");	
					spHoraExtraFacadeRemote.procesarLaborExcepcional((HashMap)mProceso,usuario);
					//ICAPUNAY 27/06/2012 SOLO PROCESA CON UN INDICADOR 2
					/*
					Map prms1=new HashMap();				
					prms1.put("nuevo","5");
					prms1.put("usuario",usuario);
					prms1.put("cod_pers",mUniCol.get("cod_pers").toString());			
					prms1.put("fec_refer",new FechaBean((String)mUniCol.get("fec_refer_desc")).getSQLDate());
					prms1.put("proceso","4");
					prms1.put("ind_proceso", "4");
					Map registro1 = t3701dao.findNovedadByPK(prms1);
					if (registro1!=null && !registro1.isEmpty()){
						t3701dao.deleteRegistroNovedad(prms1);
						t3701dao.actualizar(prms1);
					}*/
					//ICAPUNAY 27/06/2012 SOLO PROCESA CON UN INDICADOR 2
					
					Map prms2=new HashMap();				
					prms2.put("nuevo","6");
					prms2.put("usuario",usuario);
					prms2.put("cod_pers",mUniCol.get("cod_pers").toString());			
					prms2.put("fec_refer",new FechaBean((String)mUniCol.get("fec_refer_desc")).getSQLDate());
					prms2.put("proceso","2");
					prms2.put("ind_proceso","2");
					Map registro2 = t3701dao.findNovedadByPK(prms2);
					if (registro2!=null && !registro2.isEmpty()){
						//t3701dao.deleteRegistroNovedad(prms2);
						t3701dao.actualizar(prms2);
					}
				}catch(Exception e){
					log.error("Ha ocurrido un error en novedad_procesarLaborExcepcional: " + e.getMessage() + "; datos: " + mUniCol, e);
				}
			}

		} catch (DAOException d) {
			log.error("*** SQL Error ****",d);			
			StringBuffer msg = new StringBuffer().append("ERROR PROCESO AUTOMATICO DE NOVEDADES PARA PROCESAR LABOR EXCEPCIONAL: ")
												 .append(d.toString());
        	throw new FacadeException(this, msg.toString());
        } catch (Exception e) {
			throw new FacadeException(this, e.toString());
		}
	}
	
	/*MTM*/
	/**
	 * encargado de procesar la labor excepcional de forma automatica
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * 
	 * @throws DAOException
	 * @throws RemoteException
	 */	
	public void procesarLaborExcepcional() throws FacadeException, RemoteException{
		try {
			FechaBean fecha_act = new FechaBean();//hallando fecha actual
			
			//String fechaIni = "";			
			String fechaFin = fecha_act.getFormatDate("dd/MM/yyyy");
			fechaFin = fecha_act.getOtraFecha(fechaFin, "dd/MM/yyyy", -1, Calendar.DAY_OF_YEAR );//fecha fin es un dia anterior a la fecha actual yyy/mm/dd
			String criterio = "1";//por unidad organizacional
			HashMap seguridad = new HashMap();
			
			String dbpool = "jdbc/dgsp";
			
			DataSource ds = sl.getDataSource(dbpool);			
			T99DAO t99dao = new T99DAO(dbpool);
			
			ParamBean prmUsuRrhh = t99dao.buscar(new String []{"510"}, ds, "15");//parametro de usuario quien ejecuta asistencia
			if(log.isDebugEnabled()) log.debug("prmUsuario : nro_registro :"+prmUsuRrhh.getDescripcion()+", usuario:"+prmUsuRrhh.getDescripcionCorta());
			String periodo = "";			
			String valor="";			
			String codPers = prmUsuRrhh.getDescripcion().trim();//numero de registro por ejm 4710
			String usuario = prmUsuRrhh.getDescripcionCorta().trim();//nombre de usuario
			
			/** OBTENIENDO LISTADO DE UNIDADES ORGANIZACIONALES ACTIVOS**/
			T12DAO t12dao = new T12DAO("jdbc/dcsp");
			Map prms=new HashMap();
			prms.put("criterio", "");
			prms.put("valor", "");
			prms.put("orden", "1");
			List listaUnidades=  t12dao.findByCodDesc(prms);//OBTENEMOS TODAS LAS UNIDADES ORGANIZACIONALES ACTIVAS
			
			HoraExtraFacadeHome spHoraExtraFacadeHome = (HoraExtraFacadeHome) sl.getRemoteHome(
					HoraExtraFacadeHome.JNDI_NAME,	HoraExtraFacadeHome.class);
			HoraExtraFacadeRemote spHoraExtraFacadeRemote;
					
			Map mUniOrg = new HashMap();//mapa de unidad organizacional
			String observacion = "Acumulación de Labor Excepcional del "+fechaFin+" al "+fechaFin;
			Map mProceso = new HashMap();
			mProceso.put("dbpool", dbpool);
			mProceso.put("criterio", criterio);
			mProceso.put("fechaIni", fechaFin);
			mProceso.put("fechaFin", fechaFin);
			mProceso.put("codPers", codPers);
			mProceso.put("usuario", usuario);
			mProceso.put("observacion", observacion);
			
			for (int i = 0; i < listaUnidades.size(); i++) {
				mUniOrg = (Map)listaUnidades.get(i);
				
				try{
					valor = mUniOrg.get("t12cod_uorga").toString();//CODIGO DE UNIDAD ORGANIZACIONAL A PROCESAR
					
					mProceso.put("valor", valor);
					mProceso.put("regimen", "");
					
					spHoraExtraFacadeRemote = spHoraExtraFacadeHome.create();	
					
					/** GENERANDO REGISTRO DE ASISTENCIA **/
					if(log.isDebugEnabled()) log.debug("**** PROCESANDO ASISTENCIA PARA LA UNIDAD : "+valor+" ****");	
					spHoraExtraFacadeRemote.procesarLaborExcepcional((HashMap)mProceso,usuario);
				}catch(Exception e){
					log.error("Ha ocurrido un error en procesarLaborExcepcional: " + e.getMessage() + "; datos: " + mUniOrg, e);
				}
			}
		
			/*mProceso.put("valor", "4E5500");
			mProceso.put("regimen", "");
			
			spHoraExtraFacadeRemote = spHoraExtraFacadeHome.create();			
			spHoraExtraFacadeRemote.procesarLaborExcepcional((HashMap)mProceso,usuario);*/
			

		} catch (DAOException d) {
			log.error("*** SQL Error ****",d);			
			StringBuffer msg = new StringBuffer().append("ERROR PROCESO AUTOMATICO DE PROCESAR LABOR EXCEPCIONAL: ")
												 .append(d.toString());
        	throw new FacadeException(this, msg.toString());
        } catch (Exception e) {
			throw new FacadeException(this, e.toString());
		}
	}
	
	/**
	 * Encargado de generar registro de asistencia en forma automatico
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * 
	 * @throws DAOException
	 * @throws RemoteException
	 */	
	public void generarSaldosVacacionales() throws FacadeException, RemoteException, CreateException {
		
		try {
			FechaBean fecha_act = new FechaBean();//hallando fecha actual
			String diames = fecha_act.getFormatDate("dd/MM");
			String anno = fecha_act.getAnho();
			if(log.isDebugEnabled()) log.debug("**** AutomatizarAsistenciaFacade-generarSaldosVacacionales-fecha_act: "+fecha_act);//BORRAR		
			if(log.isDebugEnabled()) log.debug("**** AutomatizarAsistenciaFacade-generarSaldosVacacionales-anno: "+anno);//BORRAR
			/*** buscar todos los que tiene fecha de ingreso o reingreso a la institucion ***/
			String dbpool = "jdbc/dgsp";
			
			DataSource ds = sl.getDataSource(dbpool); 
			
			T02DAO t02dao = new T02DAO(dbpool);
			if(log.isDebugEnabled()) log.debug("DIA/MES :"+diames);
			List listaIngXDia = t02dao.findIngresoPersonal(diames);
			if(log.isDebugEnabled()) log.debug("**** AutomatizarAsistenciaFacade-generarSaldosVacacionales-listaIngXDia: "+listaIngXDia);//BORRAR
			if(log.isDebugEnabled()) log.debug("total :"+listaIngXDia.size()+" , trabajadores en total que ingresaron o reingresaron a la institucion un dia como hoy");
			Map trab = new HashMap();
			
			T99DAO t99dao = new T99DAO(dbpool);
			
			ParamBean prmUsuRrhh = t99dao.buscar(new String []{"510"}, ds, "15");//parametro de usuario quien ejecuta asistencia
			String codigo = prmUsuRrhh.getDescripcion().trim();//numero de registro
			if(log.isDebugEnabled()) log.debug("**** AutomatizarAsistenciaFacade-generarSaldosVacacionales-codigo: "+codigo);//BORRAR
			String usuario = prmUsuRrhh.getDescripcionCorta().trim();//login de usuario
			if(log.isDebugEnabled()) log.debug("**** AutomatizarAsistenciaFacade-generarSaldosVacacionales-usuario: "+usuario);//BORRAR
			
			VacacionFacadeHome spInstanciaVacacionFacadeHome = (VacacionFacadeHome) sl.getRemoteHome(
					VacacionFacadeHome.JNDI_NAME,	VacacionFacadeHome.class);
			VacacionFacadeRemote spVacacionFacadeRemote;
			
		    HashMap seguridad = new HashMap();
    	    HashMap params = new HashMap();
    	    
    	    //seguridad.put("codPers", codigo);
    	    
		    params.put("anno", anno);
		    params.put("criterio", "0");//xRegistro
		    params.put("usuario", usuario);
		    params.put("dbpool", dbpool);
		    params.put("codPers", codigo);
		    params.put("seguridad", seguridad);
 	        params.put("observacion", "Proceso de generacion de saldos vacacionales para el " + anno);
 	        
 	        //JRR - 10/05/2011 - Por si acaso, aunque se valida tambien en T1456DAO
 	        params.put("regimen", "0");//Preguntar para que sirve este valor 0 en regimen 	      
 	        
 	        //ICAPUNAY 21/06/2011 FORMATIVAS-PARTEII
 	        String annoMesDia = fecha_act.getFormatDate("yyyyMMdd").toString();
	        if(log.isDebugEnabled()) log.debug("**** AutomatizarAsistenciaFacade-generarSaldosVacacionales-annoMesDiaFormativas: "+annoMesDia);//BORRAR 	        
 	        String fecIniFormativas=annoMesDia; 	        
 	        String fecFinFormativas=annoMesDia; 
 	        params.put("fecIniFormativas", fecIniFormativas);
 	        params.put("fecFinFormativas", fecFinFormativas);
 	        //ICAPUNAY 21/06/2011 FORMATIVAS-PARTEII
 	        if(log.isDebugEnabled()) log.debug("**** AutomatizarAsistenciaFacade-generarSaldosVacacionales-params: "+params);//BORRAR
			
			for (int i = 0; i < listaIngXDia.size(); i++) {
				trab = (Map)listaIngXDia.get(i);
				try{
					if(log.isDebugEnabled()) log.debug("**** AutomatizarAsistenciaFacade-generarSaldosVacacionales-listaIngXDia.get("+i+"): "+listaIngXDia.get(i));//BORRAR
					
					if(log.isDebugEnabled()) log.debug("**** AutomatizarAsistenciaFacade-generarSaldosVacacionales-trab: "+trab);//BORRAR
				    params.put("valor", trab.get("t02cod_pers"));
				    if(log.isDebugEnabled()) log.debug("**** AutomatizarAsistenciaFacade-generarSaldosVacacionales-trab.get(t02cod_pers): "+trab.get("t02cod_pers"));//BORRAR
					spVacacionFacadeRemote = spInstanciaVacacionFacadeHome.create();
					
					if(log.isDebugEnabled()) log.debug("**** PROCESANDO GENERACION AUTOMATICA DE SALDOS VACACIONALES PARA EL REGISTRO: "+trab.get("t02cod_pers")+" ****");
				    spVacacionFacadeRemote.generarVacaciones(params, usuario);
				}catch(Exception e){
					log.error("Error en generarSaldosVacacionales: " + e.getMessage() + "; datos: " + trab, e);
				}
			    
			}
		
		} catch (Exception d) {
			log.error("*** SQL Error ****",d);			
			StringBuffer msg = new StringBuffer().append("ERROR PROCESO AUTOMATICO DE GENERACION SALDOS VACACIONALES: ")
												 .append(d.toString());
        	throw new FacadeException(this, msg.toString());
        } 
	}//FIN METODO

	
	/**
	 * encargado de calcular y enviar alertas por acumulacion de inasistencias
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * 
	 * @throws DAOException
	 * @throws RemoteException
	 */	
	public void enviarAlertaInasistencias() throws FacadeException, RemoteException{
		try {
			//log.info("ENTRO AL METODO enviarAlertaInasistencias ..facade");
			FechaBean fecha_act = new FechaBean();//hallando fecha actual
			
			String fechaIni = "";
			String fechaFin = fecha_act.getFormatDate("dd/MM/yyyy");
			fechaFin = fecha_act.getOtraFecha(fechaFin, -1, Calendar.DAY_OF_YEAR );//fecha fin es un dia anterior a la fecha actual
			//String dbpool = "jdbc/dcsp";
			DataSource ds = sl.getDataSource("java:comp/env/jdbc/dcsp");
			//log.info("FECHA A PROCESAR : "+fechaFin);
			//LISTADO DE INASISTENCIA DE UN DETERMINADO DIA
			T1454DAO t1454dao = new T1454DAO(ds);						
			List listaInasistencias = t1454dao.findInasistenciasByFecha(fechaFin);//listado de trabajadores que inasistieron
			
			String numDias ="0";
			/*listado personas inasistencia 3 dias consecutivos*/
			List listaInaTresDias = new ArrayList();
			/*listado personas inasistencia 5 dias en 30 dias*/
			List listaInaCincoDias = new ArrayList();
			/*listado personas inasistencia 15 dias en 180 dias*/
			List listaInaQuinceDias = new ArrayList();
			
			T02DAO t02dao = new T02DAO(ds);
			
			//buscado inasistencias de 3 dias consecutivos injustificados
			Map mTrab = new HashMap();
			Map prms=new HashMap();
			prms.put("fechaIni", fechaIni);
			prms.put("fechaFin", fechaFin);			
			for (int i = 0; i < listaInasistencias.size(); i++) {
				mTrab = (Map)listaInasistencias.get(i);
				/**verificando si esta entre los que tiene 3**/
				prms.put("cod_pers", mTrab.get("cod_pers"));
				fechaIni = fecha_act.getOtraFecha(fechaFin, -15, Calendar.DAY_OF_YEAR );
				prms.put("fechaIni", fechaIni);
				//log.info("params3 : "+prms);
				List listaMovis = t1454dao.findMovByFechasAndCodPers(prms);
				if(listaMovis.size()>2){
					Map ma0= (Map)listaMovis.get(0);
					Map ma1= (Map)listaMovis.get(1);
					Map ma2= (Map)listaMovis.get(2);
					if(ma0.get("mov").toString().trim().equalsIgnoreCase("00") && 
							ma1.get("mov").toString().trim().equalsIgnoreCase("00") &&
							ma2.get("mov").toString().trim().equalsIgnoreCase("00")){//si inasistido 3 dias consecutivos
						Map mTrabIna = t02dao.findByRegistro(mTrab.get("cod_pers").toString());
						listaInaTresDias.add(mTrabIna);//agregando a la lista de inasistentes de 3 dias consecutivos
					}
				}
				/**verificando si esta entre los que tiene 5 dias en 30 dias**/
				numDias = "5";
				prms.put("numDias", numDias);
				fechaIni = fecha_act.getOtraFecha(fechaFin, -30, Calendar.DAY_OF_YEAR );
				prms.put("fechaIni", fechaIni);
				//log.info("params5 : "+prms);
				Map mIna5 = t1454dao.findInaByFechasAndCodPers(prms);
				if(mIna5 != null && !mIna5.isEmpty()){//quiere decir que si tiene 5 inasistencia en 30 dias
					Map mTrabIna = t02dao.findByRegistro(mTrab.get("cod_pers").toString());					
					listaInaCincoDias.add(mTrabIna);
				}
				/**verificando si esta entre los que tiene 15 dias en 180 dias**/
				numDias = "15";
				prms.put("numDias", numDias);
				fechaIni = fecha_act.getOtraFecha(fechaFin, -180, Calendar.DAY_OF_YEAR );
				prms.put("fechaIni", fechaIni);
				//log.info("params5 : "+prms);
				Map mIna15 = t1454dao.findInaByFechasAndCodPers(prms);
				if(mIna15 != null && !mIna15.isEmpty()){//quiere decir que si tiene 15 inasistencia en 180 dias
					Map mTrabIna = t02dao.findByRegistro(mTrab.get("cod_pers").toString());
					listaInaQuinceDias.add(mTrabIna);
				}
			}
			
			if(log.isDebugEnabled()) {
			log.debug("VERIFICANDO INASISTENCIA DE 3, 5 Y 15 de inasistencias .... TERMINADO");
			log.debug("cantidad de personas con 3 INA consecutivas: "+listaInaTresDias.size());
			log.debug("cantidad de personas con 5 INA en 30 dias  : "+listaInaCincoDias.size());
			log.debug("cantidad de personas con 15 INA en 180 dias: "+listaInaQuinceDias.size());
			}
			
			/**ORDENANDO LA LISTA DE RESULTADOS POR UNIDAD ORGANIZACIONAL**/
			Ordenamiento.sort(listaInaTresDias, "cod_uorg" + Ordenamiento.SEPARATOR+Ordenamiento.ASC);//ordenando por unidad organizacional
			Ordenamiento.sort(listaInaCincoDias, "cod_uorg" + Ordenamiento.SEPARATOR+Ordenamiento.ASC);//ordenando por unidad organizacional
			Ordenamiento.sort(listaInaQuinceDias, "cod_uorg" + Ordenamiento.SEPARATOR+Ordenamiento.ASC);//ordenando por unidad organizacional
			
			/********* ENVIO DE CORREO DE 3 DIAS ************/
			StringBuffer mensaje = new StringBuffer("<html><head><title>NOTIFICACI&Oacute;N DE INSISTENCIAS - 3 DIAS - UNIDAD ORGANIZACIONAL</title>")
			.append("</head><body><table><tr><td class='membrete' colspan='7'><font size='2' face='Verdana' color='black'><b>")
			
			.append("Los siguientes trabajadores tienen <b>3</b> dias de inasistencias consecutivas injustificados.<br>")
			
			.append("</b></font></td></tr><tr><td>&nbsp;</td></tr>")			
			.append("<tr><td style='background-color:#B6CBEB;'>")
			.append("<table border='0' cellpadding='0' cellspacing='1'> <tr align='center'>")
			.append("<th width='4%' nowrap><font face='Verdana' size='2'>N&uacute;mero</font></th>")
			.append("<th width='5%' ><font face='Verdana' size='2'>N&uacute;m. Registro</font></th>")
			.append("<th width='30%' ><font face='Verdana' size='2'>Apellidos - Nombres</font></th>")
			.append("</tr>");
			
			String asunto = "ALERTA DE INASISTENCIAS";
			
			
			if(log.isDebugEnabled()) log.debug("PROCESANDO ... tam lista:"+listaInaTresDias.size());
			
			StringBuffer  mensaje3 = null;
			
			String cod_uorga = "";
			int i = 0;
			int nro = 0;		
			
			T99DAO t99dao = new T99DAO(ds);
			
			ParamBean prmUsuRrhh = t99dao.buscar(new String []{"510"}, ds, "15");//parametro de usuario quien ejecuta asistencia
			String codigo = prmUsuRrhh.getDescripcion().trim();//numero de registro
			CorreoDAO correoDAO = new CorreoDAO(ds);
			String smtp_destino = correoDAO.findCorreoByRegistro(codigo);
			if(log.isDebugEnabled()) {
				log.debug("smtp_destino "+ smtp_destino );
			}
			while (i<listaInaTresDias.size()) {
				//StringBuffer  mensaje3 = new StringBuffer(mensaje.toString());
				mTrab = (Map)listaInaTresDias.get(i);
				if(cod_uorga.equals(mTrab.get("cod_uorg").toString().trim())){//los de la misma unidad organizacional
					mensaje3.append("<tr align='left' class='dato'><td align='center' style='background-color:#FFF;'><font face='Verdana' size='2'>")
					.append( nro+"")
					.append("</font></td><td align='center' style='background-color:#FFF;'><font face='Verdana' size='2'>")
					.append( mTrab.get("t02cod_pers").toString())
					.append("</font></td>")
					.append("<td style='background-color:#FFF;'><font face='Verdana' size='2'>")
					.append( mTrab.get("t02ap_pate").toString().trim()+" "+mTrab.get("t02ap_pate").toString().trim())
					.append(", "+mTrab.get("t02nombres").toString().trim())
					.append("</font></td></tr>");					
				}else{//si el codigo de organizacion es diferente
					//se creo el mensaje del correo
					/** aqui se deberia ver el parametro de encargado de la unidad organizacional**/
					
					if(! "".equals(cod_uorga)){//enviando el correo
						mensaje3.append("</table></td></tr><tr><td>&nbsp;</td></tr>")
						.append("<tr><td class='membrete'  colspan='7'><font size='2' face='Verdana' color='black'><b>")
						
						.append(" Realice las acciones correspondientes del caso! ")				
						.append("</b></font> </td></tr>")
						.append("<tr><td>&nbsp;</td></tr></table></body></html>");
						
						Correo correo = new Correo(mensaje3.toString());
			    		correo.setAsunto(asunto+" - "+cod_uorga);
			    		correo.setRemitente("AsistenciaSIRH@sunat.gob.pe", "SUNAT-RRHH");    		
			    		correo.agregarDestinatario(smtp_destino.trim());    		
			    		correo.enviarHtml();
					}//fin de envio de correo
					
					/**empezando a formar el siguiente correo**/
					
					mensaje3 = new StringBuffer(mensaje.toString());
					cod_uorga = mTrab.get("cod_uorg").toString().trim();
					nro = 1;
					
					mensaje3.append("<tr align='left' class='dato'><td align='center' style='background-color:#FFF;'><font face='Verdana' size='2'>")
					.append( nro+"")
					.append("</font></td><td align='center' style='background-color:#FFF;'><font face='Verdana' size='2'>")
					.append( mTrab.get("t02cod_pers").toString())
					.append("</font></td>")
					.append("<td style='background-color:#FFF;'><font face='Verdana' size='2'>")
					.append( mTrab.get("t02ap_pate").toString().trim()+" "+mTrab.get("t02ap_pate").toString().trim())
					.append(", "+mTrab.get("t02nombres").toString().trim())
					.append("</font></td></tr>");
					
				}
				nro++;
				i++;
			}
			
			/*** ENVIO DE CORREO DE 5 INASISTENCIAS EN 30 DIAS CALENDARIO**/
			StringBuffer msj5 = new StringBuffer("<html><head><title>NOTIFICACI&Oacute;N DE INSISTENCIAS - 5 DIAS - UNIDAD ORGANIZACIONAL</title>")
			.append("</head><body><table><tr><td class='membrete' colspan='7'><font size='2' face='Verdana' color='black'><b>")
			
			.append("Los siguientes trabajadores tienen <b>5</b> dias de inasistencias injustificados en 30 dias calendario.<br>")
			
			.append("</b></font></td></tr><tr><td>&nbsp;</td></tr>")			
			.append("<tr><td style='background-color:#B6CBEB;'>")
			.append("<table border='0' cellpadding='0' cellspacing='1'> <tr align='center'>")
			.append("<th width='4%' nowrap><font face='Verdana' size='2'>N&uacute;mero</font></th>")
			.append("<th width='5%' ><font face='Verdana' size='2'>N&uacute;m. Registro</font></th>")
			.append("<th width='30%' ><font face='Verdana' size='2'>Apellidos - Nombres</font></th>")
			.append("</tr>");
			
			
			StringBuffer  mensaje5 = null;			
			cod_uorga = "";
			i = 0;
			nro = 0;			
			while (i<listaInaCincoDias.size()) {
				mTrab = (Map)listaInaCincoDias.get(i);
				if(cod_uorga.equals(mTrab.get("cod_uorg").toString().trim())){//los de la misma unidad organizacional
					mensaje5.append("<tr align='left' class='dato'><td align='center' style='background-color:#FFF;'><font face='Verdana' size='2'>")
					.append( nro+"")
					.append("</font></td><td align='center' style='background-color:#FFF;'><font face='Verdana' size='2'>")
					.append( mTrab.get("t02cod_pers").toString())
					.append("</font></td>")
					.append("<td style='background-color:#FFF;'><font face='Verdana' size='2'>")
					.append( mTrab.get("t02ap_pate").toString().trim()+" "+mTrab.get("t02ap_pate").toString().trim())
					.append(", "+mTrab.get("t02nombres").toString().trim())
					.append("</font></td></tr>");					
				}else{//si el codigo de organizacion es diferente
					//se creo el mensaje del correo
					/** aqui se deberia ver el parametro de encargado de la unidad organizacional**/
					
					if(! "".equals(cod_uorga)){//enviando el correo
						mensaje5.append("</table></td></tr><tr><td>&nbsp;</td></tr>")
						.append("<tr><td class='membrete'  colspan='7'><font size='2' face='Verdana' color='black'><b>")
						
						.append(" Realice las acciones correspondientes del caso! ")				
						.append("</b></font> </td></tr>")
						.append("<tr><td>&nbsp;</td></tr></table></body></html>");
						
						Correo correo = new Correo(mensaje5.toString());
			    		correo.setAsunto(asunto+" - "+cod_uorga);
			    		correo.setRemitente("AsistenciaSIRH@sunat.gob.pe", "SUNAT-RRHH");    		
			    		correo.agregarDestinatario(smtp_destino.trim());    		
			    		correo.enviarHtml();
					}//fin de envio de correo ssssx
					
					/**empezando a formar el siguiente correo**/
					
					mensaje5 = new StringBuffer(msj5.toString());
					cod_uorga = mTrab.get("cod_uorg").toString().trim();
					nro = 1;
					
					mensaje5.append("<tr align='left' class='dato'><td align='center' style='background-color:#FFF;'><font face='Verdana' size='2'>")
					.append( nro+"")
					.append("</font></td><td align='center' style='background-color:#FFF;'><font face='Verdana' size='2'>")
					.append( mTrab.get("t02cod_pers").toString())
					.append("</font></td>")
					.append("<td style='background-color:#FFF;'><font face='Verdana' size='2'>")
					.append( mTrab.get("t02ap_pate").toString().trim()+" "+mTrab.get("t02ap_pate").toString().trim())
					.append(", "+mTrab.get("t02nombres").toString().trim())
					.append("</font></td></tr>");
					
				}
				nro++;
				i++;
			}
			//ummmmm
			/*** ENVIO DE CORREO DE 15 INASISTENCIAS EN 180 DIAS CALENDARIO**/
			StringBuffer msj15 = new StringBuffer("<html><head><title>NOTIFICACI&Oacute;N DE INSISTENCIAS - 15 DIAS - UNIDAD ORGANIZACIONAL</title>")
			.append("</head><body><table><tr><td class='membrete' colspan='7'><font size='2' face='Verdana' color='black'><b>")
			
			.append("Los siguientes trabajadores tienen <br>15<br> dias de inasistencias injustificados en 180 dias calendario.<br>")
			
			.append("</b></font></td></tr><tr><td>&nbsp;</td></tr>")			
			.append("<tr><td style='background-color:#B6CBEB;'>")
			.append("<table border='0' cellpadding='0' cellspacing='1'> <tr align='center'>")
			.append("<th width='4%' nowrap><font face='Verdana' size='2'>N&uacute;mero</font></th>")
			.append("<th width='5%' ><font face='Verdana' size='2'>N&uacute;m. Registro</font></th>")
			.append("<th width='30%' ><font face='Verdana' size='2'>Apellidos - Nombres</font></th>")
			.append("</tr>");
			
			
			StringBuffer  mensaje15 = null;			
			cod_uorga = "";
			i = 0;
			nro = 0;			
			while (i<listaInaCincoDias.size()) {
				mTrab = (Map)listaInaCincoDias.get(i);
				if(cod_uorga.equals(mTrab.get("cod_uorg").toString().trim())){//los de la misma unidad organizacional
					mensaje15.append("<tr align='left' class='dato'><td align='center' style='background-color:#FFF;'><font face='Verdana' size='2'>")
					.append( nro+"")
					.append("</font></td><td align='center' style='background-color:#FFF;'><font face='Verdana' size='2'>")
					.append( mTrab.get("t02cod_pers").toString())
					.append("</font></td>")
					.append("<td style='background-color:#FFF;'><font face='Verdana' size='2'>")
					.append( mTrab.get("t02ap_pate").toString().trim()+" "+mTrab.get("t02ap_pate").toString().trim())
					.append(", "+mTrab.get("t02nombres").toString().trim())
					.append("</font></td></tr>");					
				}else{//si el codigo de organizacion es diferente
					//se creo el mensaje del correo
					/** aqui se deberia ver el parametro de encargado de la unidad organizacional**/
					
					if(! "".equals(cod_uorga)){//enviando el correo
						mensaje15.append("</table></td></tr><tr><td>&nbsp;</td></tr>")
						.append("<tr><td class='membrete'  colspan='7'><font size='2' face='Verdana' color='black'><b>")
						
						.append(" Realice las acciones correspondientes del caso! ")				
						.append("</b></font> </td></tr>")
						.append("<tr><td>&nbsp;</td></tr></table></body></html>");
						
						Correo correo = new Correo(mensaje15.toString());
			    		correo.setAsunto(asunto+" - "+cod_uorga);
			    		correo.setRemitente("AsistenciaSIRH@sunat.gob.pe", "SUNAT-RRHH");    		
			    		correo.agregarDestinatario(smtp_destino.trim());    		
			    		correo.enviarHtml();
					}//fin de envio de correo
					
					/**empezando a formar el siguiente correo**/
					
					mensaje15 = new StringBuffer(msj15.toString());
					cod_uorga = mTrab.get("cod_uorg").toString().trim();
					nro = 1;
					
					mensaje15.append("<tr align='left' class='dato'><td align='center' style='background-color:#FFF;'><font face='Verdana' size='2'>")
					.append( nro+"")
					.append("</font></td><td align='center' style='background-color:#FFF;'><font face='Verdana' size='2'>")
					.append( mTrab.get("t02cod_pers").toString())
					.append("</font></td>")
					.append("<td style='background-color:#FFF;'><font face='Verdana' size='2'>")
					.append( mTrab.get("t02ap_pate").toString().trim()+" "+mTrab.get("t02ap_pate").toString().trim())
					.append(", "+mTrab.get("t02nombres").toString().trim())
					.append("</font></td></tr>");
					
				}
				nro++;
				i++;
			}
			
			
			if(log.isDebugEnabled()) {
				log.debug("FIN" );
			}
			/*** SE REEMPLAZO POR LA NUEVA LOGICA PARA EL ENVIO DE CORREOS**/
			//
			/*while( listaInaTresDias.size()>0 ){
				mTrab = (Map)listaInaTresDias.get(0);
				Map mUno= t02dao.findByRegistro(mTrab.get("cod_pers").toString());//el primero
				log.info("TRABAJADOR 1 de 3ina map:"+mUno);
				if( !cod_uorga.equalsIgnoreCase(mUno.get("cod_uorg").toString().trim()) ){
					cod_uorga = mUno.get("cod_uorg").toString().trim();
					log.info("*********** PROCESANDO unidad : "+cod_uorga+" *****************");
					
					StringBuffer  mensaje3 = new StringBuffer(mensaje.toString());
					numero = 1;
					
					mensaje3.append("<tr align='left' class='dato'><td align='center' style='background-color:#FFF;'><font face='Verdana' size='2'>")
					.append( numero+"")
					.append("</font></td><td align='center' style='background-color:#FFF;'><font face='Verdana' size='2'>")
					.append( mUno.get("t02cod_pers").toString())
					.append("</font></td>")
					.append("<td style='background-color:#FFF;'><font face='Verdana' size='2'>")
					.append( mUno.get("t02ap_pate").toString().trim()+" "+mUno.get("t02ap_pate").toString().trim())
					.append(", "+mUno.get("t02nombres").toString().trim())
					.append("</font></td></tr>");
					
					numero++;
					log.info("*****************************");
					listaInaTresDias.remove(0);//quitando de la lista
					int i=0;
					while(i<listaInaTresDias.size()){
						log.info("los demas registros de la misma unidad");
						Map mAux = (Map)listaInaTresDias.get(i);
						Map mDos= t02dao.findByRegistro(mAux.get("cod_pers").toString());//recorre todos los que tengan el mismo cod_uorga
						log.info("TRABAJADOR 2 de 3ina map:"+mDos);
						if(mDos.get("cod_uorg").toString().trim().equals(mUno.get("cod_uorg").toString().trim())){							
							mensaje3.append("<tr align='left' class='dato'><td align='center' style='background-color:#FFF;'><font face='Verdana' size='2'>")
							.append( numero+"")
							.append("</font></td><td align='center' style='background-color:#FFFFFF;'><font face='Verdana' size='2'>")
							.append( mDos.get("t02cod_pers").toString())
							.append("</font></td>")
							.append("<td style='background-color:#FFFFFF;'><font face='Verdana' size='2'>")
							.append( mDos.get("t02ap_pate").toString().trim()+" "+mDos.get("t02ap_pate").toString().trim())
							.append(", "+mDos.get("t02nombres").toString().trim())
							.append("</font></td></tr>");
							
							numero++;
							
							listaInaTresDias.remove(i);//quitando de la lista
							i--;
						}
						i++;
					}
					log.info("enviando correo  mensaje:");
					log.info(mensaje3.toString());
					mensaje3.append("</table></td></tr><tr><td>&nbsp;</td></tr>")
					.append("<tr><td class='membrete'  colspan='7'><font size='2' face='Verdana' color='black'><b>")
					
					.append(" Realice las acciones correspondientes del caso! ")				
					.append("</b></font> </td></tr>")
					.append("<tr><td>&nbsp;</td></tr></table></body></html>");
					
					Correo correo = new Correo(mensaje3.toString());
		    		correo.setAsunto(asunto+" - "+cod_uorga);
		    		correo.setRemitente("prac-jcallo@sunat.gob.pe", "SUNAT-RRHH");    		
		    		correo.agregarDestinatario("prac-jcallo@sunat.gob.pe");    		
		    		correo.enviarHtml();
					
				}
			}*/
			
    		/**FIN DE ENVIO DE CORREO 3 insistencias consecutivos**/
			
			/********* ENVIO DE CORREO DE 5 DIAS ************/
			/*StringBuffer msj5 = new StringBuffer("<html><head><title>NOTIFICACI&Oacute;N DE INSISTENCIAS - 5 DIAS - UNIDAD ORGANIZACIONAL</title>")
			.append("</head><body><table><tr><td class='membrete' colspan='7'><font size='2' face='Verdana' color='black'><b>")
			
			.append("Los siguientes trabajadores tienen <b>5</b> dias de inasistencias injustificados en 30 dias calendario.<br>")
			
			.append("</b></font></td></tr><tr><td>&nbsp;</td></tr>")			
			.append("<tr><td style='background-color:#B6CBEB;'>")
			.append("<table border='0' cellpadding='0' cellspacing='1'> <tr align='center'>")
			.append("<th width='4%' nowrap><font face='Verdana' size='2'>N&uacute;mero</font></th>")
			.append("<th width='5%' ><font face='Verdana' size='2'>N&uacute;m. Registro</font></th>")
			.append("<th width='30%' ><font face='Verdana' size='2'>Apellidos - Nombres</font></th>")
			.append("</tr>");
			numero = 1;
			log.info("PROCESANDO 5 DIAS... tam lista:"+listaInaCincoDias.size());
			while( listaInaCincoDias.size()>0 ){
				mTrab = (Map)listaInaCincoDias.get(0);
				Map mUno= t02dao.findByRegistro(mTrab.get("cod_pers").toString());//el primero
				log.info("TRABAJADOR 1 de 5ina map:"+mUno);
				if( !cod_uorga.equalsIgnoreCase(mUno.get("cod_uorg").toString().trim()) ){
					cod_uorga = mUno.get("cod_uorg").toString().trim();
					log.info("/************ PROCESANDO unidad : "+cod_uorga+" ******************//*");*/
					
					/*StringBuffer mensaje5 = new StringBuffer(msj5.toString());
					numero = 1;
					
					mensaje5.append("<tr align='left' class='dato'><td align='center' style='background-color:#FFF;'><font face='Verdana' size='2'>")
					.append( numero+"")
					.append("</font></td><td align='center' style='background-color:#FFFFFF;'><font face='Verdana' size='2'>")
					.append( mUno.get("t02cod_pers").toString())
					.append("</font></td>")
					.append("<td style='background-color:#FFFFFF;'><font face='Verdana' size='2'>")
					.append( mUno.get("t02ap_pate").toString().trim()+" "+mUno.get("t02ap_pate").toString().trim())
					.append(", "+mUno.get("t02nombres").toString().trim())
					.append("</font></td></tr>");
					
					numero++;
					
					listaInaCincoDias.remove(0);//quitando de la lista
					int i=0;
					while(i<listaInaCincoDias.size()){
						Map mAux = (Map)listaInaCincoDias.get(i);
						Map mDos= t02dao.findByRegistro(mAux.get("cod_pers").toString());//recorre todos los que tengan el mismo cod_uorga
						log.info("TRABAJADOR 2 de 5ina map:"+mDos);
						if(mDos.get("cod_uorg").toString().trim().equals(mUno.get("cod_uorg").toString().trim())){							
							mensaje5.append("<tr align='left' class='dato'><td align='center' style='background-color:#FFF;'><font face='Verdana' size='2'>")
							.append( numero+"")
							.append("</font></td><td align='center' style='background-color:#FFFFFF;'><font face='Verdana' size='2'>")
							.append( mDos.get("t02cod_pers").toString())
							.append("</font></td>")
							.append("<td style='background-color:#FFFFFF;'><font face='Verdana' size='2'>")
							.append( mDos.get("t02ap_pate").toString().trim()+" "+mDos.get("t02ap_pate").toString().trim())
							.append(", "+mDos.get("t02nombres").toString().trim())
							.append("</font></td></tr>");
							
							numero++;
							
							listaInaCincoDias.remove(i);//quitando de la lista
							i--;
						}
						i++;
					}
					
					mensaje5.append("</table></td></tr><tr><td>&nbsp;</td></tr>")
					.append("<tr><td class='membrete'  colspan='7'><font size='2' face='Verdana' color='black'><b>")
					
					.append(" Realice las acciones correspondientes del caso! ")				
					.append("</b></font> </td></tr>")
					.append("<tr><td>&nbsp;</td></tr></table></body></html>");
					log.info("*******enviando corrreeeeooo de 5 inasistecias");
					Correo correo = new Correo(mensaje5.toString());
		    		correo.setAsunto(asunto+" - "+cod_uorga);
		    		correo.setRemitente("prac-jcallo@sunat.gob.pe", "SUNAT-RRHH");    		
		    		correo.agregarDestinatario("prac-jcallo@sunat.gob.pe");    		
		    		correo.enviarHtml();
					
				}
			}*/
			
    		/**FIN DE ENVIO DE CORREO 5 insistencias consecutivos**/
			
			/********* ENVIO DE CORREO DE 15 DIAS ************/
			/*StringBuffer msj15 = new StringBuffer("<html><head><title>NOTIFICACI&Oacute;N DE INSISTENCIAS - 15 DIAS - UNIDAD ORGANIZACIONAL</title>")
			.append("</head><body><table><tr><td class='membrete' colspan='7'><font size='2' face='Verdana' color='black'><b>")
			
			.append("Los siguientes trabajadores tienen <br>15<br> dias de inasistencias injustificados en 180 dias calendario.<br>")
			
			.append("</b></font></td></tr><tr><td>&nbsp;</td></tr>")			
			.append("<tr><td style='background-color:#B6CBEB;'>")
			.append("<table border='0' cellpadding='0' cellspacing='1'> <tr align='center'>")
			.append("<th width='4%' nowrap><font face='Verdana' size='2'>N&uacute;mero</font></th>")
			.append("<th width='5%' ><font face='Verdana' size='2'>N&uacute;m. Registro</font></th>")
			.append("<th width='30%' ><font face='Verdana' size='2'>Apellidos - Nombres</font></th>")
			.append("</tr>");
			numero = 1;
			log.info("PROCESANDO 15 DIAS... tam lista:"+listaInaQuinceDias.size());
			while( listaInaQuinceDias.size()>0 ){
				mTrab = (Map)listaInaQuinceDias.get(0);
				Map mUno= t02dao.findByRegistro(mTrab.get("cod_pers").toString());//el primero
				log.info("TRABAJADOR 1 de 15ina map:"+mUno);
				if( !cod_uorga.equalsIgnoreCase(mUno.get("cod_uorg").toString().trim()) ){
					cod_uorga = mUno.get("cod_uorg").toString().trim();
					log.info("/************ PROCESANDO unidad : "+cod_uorga+" ******************//*");*/
					
					/*StringBuffer mensaje15 = new StringBuffer(msj15.toString());
					numero = 1;
					
					mensaje15.append("<tr align='left' class='dato'><td align='center' style='background-color:#FFF;'><font face='Verdana' size='2'>")
					.append( numero+"")
					.append("</font></td><td align='center' style='background-color:#FFF;'><font face='Verdana' size='2'>")
					.append( mUno.get("t02cod_pers").toString())
					.append("</font></td>")
					.append("<td style='background-color:#FFF;'><font face='Verdana' size='2'>")
					.append( mUno.get("t02ap_pate").toString().trim()+" "+mUno.get("t02ap_pate").toString().trim())
					.append(", "+mUno.get("t02nombres").toString().trim())
					.append("</font></td></tr>");
					
					numero++;
					
					listaInaQuinceDias.remove(0);//quitando de la lista
					int i=0;
					while(i<listaInaQuinceDias.size()){
						Map mAux = (Map)listaInaQuinceDias.get(i);
						Map mDos= t02dao.findByRegistro(mAux.get("cod_pers").toString());//recorre todos los que tengan el mismo cod_uorga
						log.info("TRABAJADOR 2 de 15ina map:"+mUno);
						if(mDos.get("cod_uorg").toString().trim().equals(mUno.get("cod_uorg").toString().trim())){							
							mensaje15.append("<tr align='left' class='dato'><td align='center' style='background-color:#FFF;'><font face='Verdana' size='2'>")
							.append( numero+"")
							.append("</font></td><td align='center' style='background-color:#FFFFFF;'><font face='Verdana' size='2'>")
							.append( mDos.get("t02cod_pers").toString())
							.append("</font></td>")
							.append("<td style='background-color:#FFFFFF;'><font face='Verdana' size='2'>")
							.append( mDos.get("t02ap_pate").toString().trim()+" "+mDos.get("t02ap_pate").toString().trim())
							.append(", "+mDos.get("t02nombres").toString().trim())
							.append("</font></td></tr>");
							
							numero++;
							
							listaInaQuinceDias.remove(i);//quitando de la lista
							i--;
						}
						i++;
					}
					
					mensaje15.append("</table></td></tr><tr><td>&nbsp;</td></tr>")
					.append("<tr><td style='background-color:#FFFFFF;' colspan='7'><font size='2' face='Verdana' color='black'><b>")
					
					.append(" Realice las acciones correspondientes del caso! ")				
					.append("</b></font> </td></tr>")
					.append("<tr><td>&nbsp;</td></tr></table></body></html>");
					
					Correo correo = new Correo(mensaje15.toString());
		    		correo.setAsunto(asunto+" - "+cod_uorga);
		    		correo.setRemitente("prac-jcallo4@sunat.gob.pe", "SUNAT-RRHH");    		
		    		correo.agregarDestinatario("prac-jcallo@sunat.gob.pe");    		
		    		correo.enviarHtml();
					
				}
			}*/
			
    		/**FIN DE ENVIO DE CORREO 15 insistencias consecutivos**/
			
		} catch (DAOException d) {
			log.error("*** SQL Error ****",d);			
			StringBuffer msg = new StringBuffer().append("ERROR PROCESO AUTOMATICO DE ENVIAR ALERTAS POR  INASISTENCIA: ")
												 .append(d.toString());
        	throw new FacadeException(this, msg.toString());
        } catch (Exception e) {
			throw new FacadeException(this, e.toString());
		}
	}
	
	/**
	 * Encargado de generar registro de asistencia en forma automatico
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * 
	 * @throws DAOException
	 * @throws RemoteException
	 */	
	public void novedad_generarRegistroAsistencia() throws FacadeException, RemoteException, CreateException {
		
		try {
			FechaBean fecha_act = new FechaBean();//hallando fecha actual
			
			String fechaFin = fecha_act.getFormatDate("dd/MM/yyyy");
			fechaFin = fecha_act.getOtraFecha(fechaFin, -1, Calendar.DAY_OF_YEAR );//fecha fin es un dia anterior a la fecha actual
			String criterio = "0";//por registro
			//Map seguridad = new HashMap();
			String dbpool = "jdbc/dgsp";
			
			DataSource ds = sl.getDataSource(dbpool);			
			T99DAO t99dao = new T99DAO(dbpool);
			
			ParamBean prmUsuRrhh = t99dao.buscar(new String []{"510"}, ds, "15");//parametro de usuario quien ejecuta asistencia
			 
			if(log.isDebugEnabled()) log.debug("prmUsuario : nro_registro :"+prmUsuRrhh.getDescripcion()+", usuario:"+prmUsuRrhh.getDescripcionCorta());
			
			ParamBean prmLimiteGenerar = t99dao.buscar(new String []{"510"}, ds, "55");//parametro de limite de meses para generacion
			Integer limite = new Integer( prmLimiteGenerar.getDescripcion()); 
			if(log.isDebugEnabled()) log.debug("prmLimiteGenerar : "+ limite);
			
			/** definiendo los parametros**/
			HashMap mapa = new HashMap();
			mapa.put("dbpool", dbpool);			
			mapa.put("criterio", criterio);
			mapa.put("valor", "");

			mapa.put("codPers", prmUsuRrhh.getDescripcion());//numero de regitro por ejem 4710
			mapa.put("usuario", prmUsuRrhh.getDescripcionCorta());//USUARIO por ejemplo NMEDINA
			
			
			/** OBTENIENDO LISTADO DE NOVEDADES A GENERAR ASISTENCIA**/
			T3701DAO t3701dao = new T3701DAO("jdbc/dcsp");
			Calendar today = new GregorianCalendar();
			today.add(Calendar.MONTH, (limite.intValue()*-1));
			Map prms=new HashMap();
			prms.put("proceso", "0");
			prms.put("fechaLimite", today.getTime());
			List listaUnidades=  t3701dao.findByEstadoAndFecha(prms);//OBTENEMOS TODAS LAS NOVEDADES
			
			Map mUniPer = new HashMap();//mapa de unidad organizacional
			
			/*** comentado para hacer pruenbas solamente con la unidad 2A5200***/
			AsistenciaFacadeHome asistenciaFacadeHome = (AsistenciaFacadeHome) sl.getRemoteHome(
					AsistenciaFacadeHome.JNDI_NAME,	AsistenciaFacadeHome.class);
			AsistenciaFacadeRemote asistenciaFacadeRemote;
			t3701dao = new T3701DAO("jdbc/dgsp");
			for (int i = 0; i < listaUnidades.size(); i++) {
				mUniPer = (Map)listaUnidades.get(i);
				try{
					prms=new HashMap();
					
					mapa.put("fechaIni", mUniPer.get("fec_refer_desc").toString());//es para el mismo dia, no existe rango de dias al momento de generar asistencia 
					mapa.put("fechaFin", mUniPer.get("fec_refer_desc").toString());
					mapa.put("observacion", "Registro de asistencia del " + mUniPer.get("fec_refer_desc").toString() + " al " + mUniPer.get("fec_refer_desc").toString());
					
					mapa.put("valor", mUniPer.get("cod_pers").toString());//Registro A PROCESAR
					asistenciaFacadeRemote = asistenciaFacadeHome.create();	
					
					/** GENERANDO REGISTRO DE ASISTENCIA **/
					if(log.isDebugEnabled()) log.debug("GENERANDO ASISTENCIA PARA mapa: "+mapa+"   con el usuario: "+(String)mapa.get("usuario"));
					asistenciaFacadeRemote.generarRegistros(mapa, (String)mapa.get("usuario"));		
					//EBV - Actualizar Tabla de Novedades con Proceso Ejecutado
					//params.get("nuevo"),new FechaBean().getTimestamp(),params.get("usuario"),params.get("cod_pers"),params.get("fec_refer"),params.get("proceso")
					prms.put("proceso", "0");
					prms.put("nuevo", "1");
					prms.put("usuario", mapa.get("usuario").toString());
					prms.put("cod_pers", mUniPer.get("cod_pers").toString());
					prms.put("fec_refer", mUniPer.get("fec_refer"));
					
					t3701dao.actualizar(prms);
				}catch(Exception e){
					HashMap hmRevert = new HashMap();
					hmRevert.put("proceso", "1");
					hmRevert.put("nuevo", "0");
					hmRevert.put("usuario", "BATCH-ERR-GEN");
					hmRevert.put("cod_pers", mUniPer.get("cod_pers").toString());
					hmRevert.put("fec_refer", mUniPer.get("fec_refer"));
					t3701dao.actualizar(hmRevert);
					
					log.error("Ha ocurrido un error en novedad_generarRegistroAsistencia: " + e.getMessage() + "; novedad: " + mUniPer, e);
				}
			}
		
		} catch (DAOException d) {
			log.error("*** SQL Error ****",d);			
			StringBuffer msg = new StringBuffer().append("ERROR PROCESO AUTOMATICO DE GENERACION ASISTENCIA: ")
												 .append(d.toString());
        	throw new FacadeException(this, msg.toString());
        } catch (Exception e) {
			throw new FacadeException(this, e.toString());
		}
	}//FIN METODO
	
	/**
	 * encargado de procesar la asistencia de forma automatica
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * 
	 * @throws DAOException
	 * @throws RemoteException
	 */	
	public void novedad_procesarAsistencia() throws FacadeException, RemoteException{
		try {
			FechaBean fecha_act = new FechaBean();//hallando fecha actual
			
			//String fechaIni = "";			
			String fechaFin = fecha_act.getFormatDate("dd/MM/yyyy");
			fechaFin = fecha_act.getOtraFecha(fechaFin, "yyyy/MM/dd", -1, Calendar.DAY_OF_YEAR );//fecha fin es un dia anterior a la fecha actual yyy/mm/dd
			String criterio = "0";//por trabajador
			HashMap seguridad = new HashMap();
			
			String dbpool = "jdbc/dgsp";
			
			DataSource ds = sl.getDataSource(dbpool);			
			T99DAO t99dao = new T99DAO(dbpool);
			
			ParamBean prmUsuRrhh = t99dao.buscar(new String []{"510"}, ds, "15");//parametro de usuario quien ejecuta asistencia
			if(log.isDebugEnabled()) log.debug("prmUsuario : nro_registro :"+prmUsuRrhh.getDescripcion()+", usuario:"+prmUsuRrhh.getDescripcionCorta());
			
			ParamBean prmLimiteProcesar = t99dao.buscar(new String []{"510"}, ds, "56");//parametro de limite de meses para procesar
			Integer limite = new Integer( prmLimiteProcesar.getDescripcion()); 
			if(log.isDebugEnabled()) log.debug("prmLimiteProcesar : "+ limite);
			
			String periodo = "";			
			String valor="";	
			String periodoOld = "";			
			String valorOld="";
			String codPers = prmUsuRrhh.getDescripcion().trim();//numero de registro por ejm 4710
			String usuario = prmUsuRrhh.getDescripcionCorta().trim();//nombre de usuario
			//JRR 09072008 - Incluir Papeletas
			String indPap = "1"; //"0";

			//fechaIni = mPeriodo.get("finicio").toString();//fecha de inicio se toma el inicio del periodo
			
			Map mUniOrg = new HashMap();//mapa de unidad organizacional
			
			Map prm = new  HashMap();
			prm.put("criterio", criterio);
			prm.put("seguridad", seguridad);

			AsistenciaFacadeHome spAssistenciaFacadeHome = (AsistenciaFacadeHome) sl.getRemoteHome(
					AsistenciaFacadeHome.JNDI_NAME,	AsistenciaFacadeHome.class);
			AsistenciaFacadeRemote spAsistenciaFacadeRemote;
			
			/** OBTENIENDO LISTADO DE NOVEDADES A GENERAR ASISTENCIA**/
			T3701DAO t3701dao = new T3701DAO("jdbc/dcsp");
			Calendar today = new GregorianCalendar();			
			today.add(Calendar.MONTH, limite.intValue()*-1) ;
			Map prms=new HashMap();
			prms.put("proceso", "1");
			prms.put("fechaLimite", today.getTime());
			prms.put("fechaLimite", today.getTime());
			
			List listaUnidades=  t3701dao.findByEstadoAndFecha(prms);//OBTENEMOS TODAS LAS NOVEDADES
			T1276DAO periodoDao = new T1276DAO("jdbc/dcsp");
			Map mPeriodo ;
			FechaBean fecha_hoy = new FechaBean();
			//FechaBean fcierre = new FechaBean();
			//JRR - 28/04/2011
			String fcierre = new String();
			T02DAO t02dao = new T02DAO(ds);
			Map trab = new HashMap();
			T72DAO t72dao = new T72DAO(ds);
			boolean cerrado = false;
			
			//JRR - 16/02/2009 - Obtener periodos abiertos		
			/* MantenimientoFacadeHome spMantenimientoFacadeHome = (MantenimientoFacadeHome) sl.getRemoteHome(
					MantenimientoFacadeHome.JNDI_NAME,	MantenimientoFacadeHome.class);
			MantenimientoFacadeRemote spMantenimientoFacadeRemote; */
			
			Map mProceso = new HashMap();
			
			for (int i = 0; i < listaUnidades.size(); i++) {
				mUniOrg = (Map)listaUnidades.get(i);
				try{
					if(log.isDebugEnabled()) log.debug("Novedad - mUniOrg: " + mUniOrg);
					
					valor = mUniOrg.get("cod_pers").toString();//REGISTRO A PROCESAR
					fecha_act = new FechaBean( mUniOrg.get("fec_refer_desc").toString(),"dd/MM/yyyy");//hallando fecha actual
					fechaFin = fecha_act.getFormatDate("yyyy/MM/dd");
					//fechaFin = fecha_act.getOtraFecha(fechaFin, "yyyy/MM/dd", 0, Calendar.DAY_OF_YEAR );
					//String fechaIni = "";			
					if(log.isDebugEnabled()) log.debug("Periodo :"+fechaFin);
					
					/** OBTENIENDO EL PERIODO CORRESPONDIENTE A LA FECHA ACTUAL**/
					//JRR - 28/04/2011 - Aqui debemos obtener los periodos dependiendo del tipo de Trabajador				
					trab = t02dao.findByRegistro(valor);
					
					if (trab.get("t02cod_rel")!=null && trab.get("t02cod_rel").toString().trim().equals("09")) { //Para el reg. CAS
						mPeriodo = periodoDao.findPeriodoByFechaCAS(fechaFin);
						fcierre = (mPeriodo!=null && mPeriodo.get("fec_cierre_cas")!=null) ? mPeriodo.get("fec_cierre_cas").toString() : "";
						if(log.isDebugEnabled()) log.debug("fcierre CAS: " + fcierre);
						
					} else if (trab.get("t02cod_rel")!=null && trab.get("t02cod_rel").toString().trim().equals("10")) { //JRR - 28/04/2011 - Para Modalidad Formativa
						mPeriodo = periodoDao.findPeriodoByFechaModFormativa(fechaFin);
						//fcierre = mPeriodo.get("fec_cierre_mf").toString();
						fcierre = (mPeriodo!=null && mPeriodo.get("fec_cierre_mf")!=null) ? mPeriodo.get("fec_cierre_mf").toString() : "";
						if(log.isDebugEnabled()) log.debug("fcierre Mod Formativa: " + fcierre);
						
					} else {
						mPeriodo = periodoDao.findPeriodoByFecha(fechaFin);//formato de la fecha yyyy/MM/dd
						//fcierre = mPeriodo.get("fcierre").toString();
						fcierre = (mPeriodo!=null && mPeriodo.get("fcierre")!=null) ? mPeriodo.get("fcierre").toString() : "";
						if(log.isDebugEnabled()) log.debug("fcierre: " + fcierre);
						
					}
					
					//JRR - si no existe fecha de cierre para CAS o Mod formativa, entonces no se debe enviar a procesar
					if (fcierre.equals("")) {
						mPeriodo = null;
						cerrado = true;
						if(log.isDebugEnabled()) log.debug("No se ha configurado fecha de cierre para el periodo. No se podra procesar asistencia para esta persona.");
					}
					//
					
					
					//mPeriodo = periodoDao.findPeriodoByFecha(fechaFin);//formato de la fecha yyyy/MM/dd
					//fcierre = new FechaBean( mPeriodo.get("fcierre").toString(),"dd/MM/yyyy");
					
					if(mPeriodo == null || mPeriodo.isEmpty() ){
						if(log.isDebugEnabled()) log.debug("error: Periodo NO asignado para la fecha :"+fechaFin);
					} else {
						int diferencia = Utiles.obtenerDiasDiferencia(fecha_hoy.getFormatDate("dd/MM/yyyy"), fcierre);
						//int diferencia = Utiles.obtenerDiasDiferencia(fecha_hoy.getFormatDate("dd/MM/yyyy"),mPeriodo.get("fcierre").toString());
						//(int) new FechaBean().getDiferencia(fcierre.getCalendar(), fecha_hoy.getCalendar(),Calendar.DATE);
						if(log.isDebugEnabled()) log.debug("valor de diferencia: " + diferencia);
						if   ( diferencia < 0){
							if(log.isDebugEnabled()) log.debug("Periodo Cerrado por fecha de cierre");
							periodoOld = "0";
						}
						periodo = (mPeriodo.get("periodo") != null ) ? mPeriodo.get("periodo").toString():"";
					}
					
					//JRR - 16/02/2009 - Validar que el perÃ­odo no estÃ© cerrado
					/* spMantenimientoFacadeRemote = spMantenimientoFacadeHome.create();
					boolean cerrado = spMantenimientoFacadeRemote.periodoCerradoAFecha(dbpool,
							periodo, Utiles.obtenerFechaActual());*/
	
					//JRR - 29/04/2011
					if (!cerrado) cerrado = t72dao.verificarEnvioPlanillas(periodo);
					
					if(log.isDebugEnabled()) log.debug("Perido cerrado planillas?: " + cerrado);
					
					if ((periodoOld!="0") && (!cerrado))
					{
						if ((valor.equals(valorOld)) && (periodo.equals(periodoOld)) ){
							
						} else
						{
							spAsistenciaFacadeRemote = spAssistenciaFacadeHome.create();
							
							//JRR - 26/04/2010
							mProceso.put("dbpool", dbpool);
							mProceso.put("regimen", "0");
							mProceso.put("periodo", periodo);
							mProceso.put("criterio", criterio);
							mProceso.put("valor", valor);
							mProceso.put("codigo", codPers);
							mProceso.put("usuario", usuario);
							mProceso.put("seguridad", seguridad);
							mProceso.put("indPap", indPap);
							//
						
							/** GENERANDO REGISTRO DE ASISTENCIA **/
							if(log.isDebugEnabled()) log.debug("**** PROCESANDO ASISTENCIA PARA EL REGISTRO : "+valor+" ****");
							//spAsistenciaFacadeRemote.procesarAsistencia(dbpool, periodo, criterio, valor, codPers, usuario, seguridad, indPap);	
							spAsistenciaFacadeRemote.procesarAsistencia(mProceso);
						}
					}
					
					periodoOld = periodo;
					valorOld = valor;
					//EBV - Actualizar Tabla de Novedades con Proceso Ejecutado
					//params.get("nuevo"),new FechaBean().getTimestamp(),params.get("usuario"),params.get("cod_pers"),params.get("fec_refer"),params.get("proceso")
					prms.put("proceso", "1");
					prms.put("nuevo", "2");
					prms.put("usuario",usuario);
					prms.put("cod_pers", mUniOrg.get("cod_pers").toString());
					prms.put("fec_refer", mUniOrg.get("fec_refer"));
					
					t3701dao.actualizar(prms);
				
				}catch(Exception e){
					HashMap hmRevert = new HashMap();
					hmRevert.put("proceso", "2");
					hmRevert.put("nuevo", "1");
					hmRevert.put("usuario","BATCH-ERR-PROC");
					hmRevert.put("cod_pers", mUniOrg.get("cod_pers").toString());
					hmRevert.put("fec_refer", mUniOrg.get("fec_refer"));
					t3701dao.actualizar(hmRevert);
					log.error("Ha ocurrido un error en novedad_procesarAsistencia: " + e.getMessage() + "; novedad: " + mUniOrg, e);
				}
			}

		} catch (DAOException d) {
			log.error("*** SQL Error ****",d);			
			StringBuffer msg = new StringBuffer().append("ERROR PROCESO AUTOMATICO DE PROCESAR ASISTENCIA: ")
												 .append(d.toString());
        	throw new FacadeException(this, msg.toString());
        } catch (Exception e) {
			throw new FacadeException(this, e.toString());
		}
	}
	
	
	/* ICAPUNAY - TRABAJADORES CON VACACIONES PROGRAMADAS ACEPTADAS O ACTIVAS - 28/02/2011 */
	/**
	 * MÃƒÆ’Ã‚Â©todo encargado de enviar alertas de vacaciones a trabajadores en forma automÃƒÆ’Ã‚Â¡tica	 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @throws FacadeException
	 * @throws RemoteException
	 * @throws CreateException
	 */	
	public void enviarAlertaVacaciones() throws FacadeException,RemoteException,CreateException{
				
		log.info("ENTRO AL METODO enviarAlertaVacaciones ..facade");			
		DataSource dcsp = sl.getDataSource("java:comp/env/jdbc/dcsp");//datasource de lectura
		DataSource dgsp = sl.getDataSource("java:comp/env/jdbc/dgsp");//datasource de escritura		
		String dcspPool="jdbc/dcsp";
		Timestamp ahora = new Timestamp(System.currentTimeMillis());
		FechaBean fb_fecha = new FechaBean(ahora);			
		String st_fecha = fb_fecha.getFormatDate("dd/MM/yyyy");			
		boolean ocurrioError=false;
		//ENVIO DE REPORTE RESUMEN DE NOTIFICACIONES ENVIADAS
		Map mapaParametros = new HashMap();		
		mapaParametros.put("ahora",ahora);
		mapaParametros.put("fb_fecha",fb_fecha);
		mapaParametros.put("st_fecha",st_fecha);	
		//FIN DE ENVIO REPORTE RESUMEN DE NOTIFICACIONES ENVIADAS
		
		try {			
			
			T1282DAO t1282dao = new T1282DAO(dcsp);//tabla t1282vacaciones_d
			T4502DAO t4502dao = new T4502DAO(dgsp);//tabla t4502NotificaVac
			T4502DAO t4502daoSel = new T4502DAO(dcsp);//tabla t4502NotificaVac
			CorreoDAO correoDAO = new CorreoDAO(dcsp);//tabla correos
			pe.gob.sunat.sp.dao.T02DAO t02dao = new pe.gob.sunat.sp.dao.T02DAO();//tabla t02perdp
			pe.gob.sunat.sp.dao.T12DAO t12dao= new pe.gob.sunat.sp.dao.T12DAO();//tabla t12uorga
			Map mVacaciones = new HashMap();			
			String cod_pers=null;
			String num_periodo=null;
			String cod_licencia=null;
			Timestamp fec_ini_vacacion=null;
			String num_notificacion=null;
			String numero_notificacion=null;
			String anio=null;
			int correlativo=0;			
			Map mParams = new HashMap();					
			String annoActual=st_fecha.substring(6, 10);			
			List listaTodasNotificaciones = null;
			Map mUltimaNotificacion = new HashMap();
			boolean insercionExitosa=false;			
			StringBuffer mensaje=null;			
			String des_link=null;
			String url=null;
			HashMap mTrabajador=new HashMap();			
			String uoTrabajador="";		
			String direCorreo;				
										
			String server_intranet = constantes_properties.leePropiedad("SERVER_INTRANET");						
			String registro_solicitud = constantes_properties.leePropiedad("REGISTRO_SOLICITUD");
			
			//ICAPUNAY - PAS20181U230300016 - Mejoras vacaciones2
			String jefeUO ="";
			String delegadoUO ="";
			String correojefeUO ="";
			String correodelegadoUO ="";
			
			String nombreTrabaj="";
			String apPate="";
			String apMate="";
			String nombres="";
			//FIN
									
			//ENVIO INDIVIDUAL DE NOTIFICACIONES DE VACACIONES
			List listaVacaciones = t1282dao.findVacacionesProgramadasActivas();//listado de trabajadores con vacaciones programadas activas	con 5, 4, 3, 2, y 1 dia antes de su inicio		
			if(listaVacaciones!=null && !listaVacaciones.isEmpty()){
				try {
					//INICIO DE FOR
					if (log.isDebugEnabled()) log.debug("listaVacaciones.size()= " + String.valueOf(listaVacaciones.size()));
					for (int i = 0; i < listaVacaciones.size(); i++) {
						delegadoUO ="";
						correodelegadoUO="";
						mUltimaNotificacion = null;
						numero_notificacion=null;
						HashMap mapDelegado = new HashMap();//DTARAZONA 2DO ENTREGABLE CORRECCION RESBUGS
						HashMap aux = new HashMap();//DTARAZONA 2DO ENTREGABLE CORRECCION RESBUGS
						anio=null;
						correlativo=0;
						mensaje = null;
						direCorreo = "";									
						uoTrabajador = "";									
						mVacaciones = (Map) listaVacaciones.get(i);		
						url = "/ol-ti-iaasistencia/asisS11Alias";			
						cod_pers = (String) mVacaciones.get("cod_pers");					
						num_periodo = (String) mVacaciones.get("periodo");					
						cod_licencia = (String) mVacaciones.get("licencia");					
						fec_ini_vacacion = (Timestamp) mVacaciones.get("ffinicio");				
		
						// VALIDANDO SI TRABAJADOR TIENE CORREO Y LOGIN						
						direCorreo=correoDAO.findCorreoByRegistro(cod_pers);
						if (log.isDebugEnabled()) log.debug("direCorreo("+cod_pers+"): "+ direCorreo);
						
						mTrabajador = t02dao.findByCodPers(dcspPool, cod_pers);													
						if(mTrabajador!=null && !mTrabajador.isEmpty()){
							uoTrabajador = mTrabajador.get("t02cod_uuoo")!=null?mTrabajador.get("t02cod_uuoo").toString().trim():"";
							apPate=mTrabajador.get("t02ap_pate")!=null?mTrabajador.get("t02ap_pate").toString().trim()+" ":"";
							apMate=mTrabajador.get("t02ap_mate")!=null?mTrabajador.get("t02ap_mate").toString().trim()+", ":"";
							nombres=mTrabajador.get("t02nombres")!=null?mTrabajador.get("t02nombres").toString().trim():"";
							nombreTrabaj = apPate.concat(apMate).concat(nombres);
							if (log.isDebugEnabled()) log.debug("nombreTrabaj2: "+ nombreTrabaj);
							/*nombreTrabaj = nombreTrabaj + mTrabajador.get("t02ap_mate")!=null?mTrabajador.get("t02ap_mate").toString().trim()+", ":"";
							nombreTrabaj = nombreTrabaj + mTrabajador.get("t02nombres")!=null?mTrabajador.get("t02nombres").toString().trim():"";*/
							if (!uoTrabajador.equals("-")){
								
								//obteniendo el jefe de la uo  
								jefeUO = t12dao.findJefeByUO(dcspPool,uoTrabajador);					
								jefeUO = jefeUO!=null?jefeUO.trim():"";							
								//correo del jefe de la uo
								correojefeUO=correoDAO.findCorreoByRegistro(jefeUO);
								if (log.isDebugEnabled()) log.debug("correojefeUO("+jefeUO+"): "+ correojefeUO);
								
								//obteniendo el delegado de la uo											
								aux.put("dbpool",dcspPool);
								aux.put("codUO",uoTrabajador);
								aux.put("codOpcion",Constantes.DELEGA_SOLICITUDES);	
								if(log.isDebugEnabled()) log.debug("Aux: "+aux.toString());//DTARAZONA 2DO ENTREGABLE CORRECCION RESBUGS
								
								mapDelegado = t12dao.findDelegado(aux);
								if(log.isDebugEnabled()) 
								{
									if(mapDelegado!=null)
										log.debug("MapDelegado: "+mapDelegado.toString());//DTARAZONA 2DO ENTREGABLE CORRECCION RESBUGS
					
								}
									
								//delegadoUO = (mapDelegado!=null && !mapDelegado.isEmpty())?((String)mapDelegado.get("t02cod_pers")).trim():"";
								delegadoUO = (mapDelegado!=null && !mapDelegado.isEmpty() && mapDelegado.get("cod_delega")!=null)?((String)mapDelegado.get("cod_delega")).trim():"";
								if(log.isDebugEnabled()) log.debug("delegadoUO (cod_delega): "+delegadoUO);//DTARAZONA 2DO ENTREGABLE CORRECCION RESBUGS
								//correo del delegado de la uo	
								if(!"".equals(delegadoUO))
									correodelegadoUO=correoDAO.findCorreoByRegistro(delegadoUO);
								
								if (log.isDebugEnabled()) log.debug("correodelegadoUO("+delegadoUO+"): "+ correodelegadoUO);
							}
						}			
						
						listaTodasNotificaciones=t4502daoSel.findAllNotificacionesEnviadas();
						if (listaTodasNotificaciones==null || listaTodasNotificaciones.isEmpty()){ 							
							num_notificacion = annoActual+Numero.format(new Integer(1), "00000");								
							if (log.isDebugEnabled()) log.debug("num_notificacion: "+ num_notificacion);
						}
						if(listaTodasNotificaciones!=null && !listaTodasNotificaciones.isEmpty()){								
							mUltimaNotificacion=t4502daoSel.findUltimaNotificacionRegistrada();
							if (mUltimaNotificacion!=null && !mUltimaNotificacion.isEmpty()){ 									
								numero_notificacion=(String)mUltimaNotificacion.get("num_notificacion");								
								anio=numero_notificacion.substring(0, 4);								
								correlativo=Integer.parseInt(numero_notificacion.substring(4, 9));								
								if(anio.equals(annoActual)){																			
									num_notificacion = anio+Numero.format(new Integer(correlativo+1), "00000"); 
									if (log.isDebugEnabled()) log.debug("num_notificacion: "+ num_notificacion);										
								}else{																				
									num_notificacion = annoActual+Numero.format(new Integer(1), "00000");
									if (log.isDebugEnabled()) log.debug("num_notificacion: "+ num_notificacion);									
								}
							}
						}										

						des_link = server_intranet + MenuCliente.generaInvocacionURL(registro_solicitud,null, true,MenuCliente.INTRANET, url);
						if (log.isDebugEnabled()) log.debug("des_link= " + des_link);						

						try{
							mensaje = new StringBuffer("<html><head><title>NOTIFICACI&Oacute;N DE VACACIONES</title><style>")
							.append(".T1 {font-style  : normal;text-transform : uppercase;font-size :10pt;color:")
							.append("#FFFFFF;background  : #336699;font-family : Tahoma,Verdana,Arial,Helvetica,sans-serif ;}</style></head>")
							.append("<body><table width='100%' align='center' class='T1'><tr><td><center><strong>NOTIFICACI&Oacute;N DE VACACIONES</strong></center></td></tr></table><br><br>")
							.append("<table>")
							.append("<tr><td class='membrete'><strong>Notificaci&oacute;n:</strong></td></tr>")
							.append("<tr><td class='dato'>")										
							.append(num_notificacion)
							.append("</td></tr>")
							.append("<tr><td class='membrete'><strong>Fecha:</strong></td></tr>")
							.append("<tr><td class='dato'>")									
							.append(new FechaBean().getFormatDate("dd/MM/yyyy")) 																									
							.append("</td></tr>")
							.append("<tr><td class='membrete'><strong>Asunto:</strong></td></tr>")
							.append("<tr><td class='dato'>Alerta de registro de solicitud de vacaciones</td></tr>")
							.append("<tr><td><strong>Sr(a)(ita).</strong> <em>").append(cod_pers+" - "+nombreTrabaj)
							.append("</em></td></tr></table><br>")										
							.append("<tr><td class='membrete'><strong>Detalle:</strong></td></tr><br><br>")
							.append("<tr><td><em>")										
							.append("Estimado Colaborador,")
							.append("</em></td></tr><br><br>")
							.append("<tr><td class='dato'>")										
							//.append("1. Estando a 5 d&iacute;as del inicio de la fecha de goce vacacional y de acuerdo a la")
							.append("1. Se ha detectado que usted no ha generado dentro de los 5 d&iacute;as de anticipaci&oacute;n la solicitud de vacaciones debidamente")
							.append("</td></tr>")
							.append("<tr><td class='dato'>")									
							//.append(" programaci&oacute;n efectuada, es obligatorio realizar el registro de la solicitud ingresando al v&iacute;nculo.")
							.append(" autorizada por su jefe inmediato, conforme a lo establecido en el inciso 4.7.1 de la Resoluci&oacute;n de Intendencia N° 060 - 2016-8A0000.")
							.append("</td></tr>")
							.append("<tr><td class='dato'>")									
							.append(" Se le recuerda que el incumplimiento de dichas disposiciones genera la aplicaci&oacute;n de las medidas administrativas que pudieran conrresponder")
							.append("</td></tr>")
							.append("<tr><td class='dato'>")									
							.append(" según el numeral 5.5 de las disposiciones finales del la Resoluci&oacute;n de Intendencia N° 060 - 2016-8A0000.")
							.append("</td></tr><br>")
							.append("<tr><td class='dato'>")									
							.append("2. Es obligatorio realizar el registro de la solicitud ingresando al v&iacute;nculo.")
							.append("</td></tr><br>")
							.append("<tr><td class='dato'>")
							.append("3. Se proceder&aacute; a bloquear en forma autom&aacute;tica la lectora, desde la fecha de inicio")
							.append("</td></tr>")
							.append("<tr><td class='dato'>")
							.append(" hasta la fecha de fin de goce que figura en la programaci&oacute;n.")
							.append("</td></tr>")
							.append("<tr><td></td></tr><br><br>")
							.append("<tr><td class='membrete'><strong>Para registrar su solicitud de vacaciones ingresar a:</strong></td></tr><br><br>")
							.append("<tr><td class='dato'><a href='")
							.append(des_link)								
							.append("'>")
							.append(des_link)
							.append("</a></td></tr><br><br>")
							.append("<tr><td>&nbsp;</td></tr>")
							.append("<tr><td>Atentamente,</td></tr><br>")
							.append("<tr><td class='membrete'><strong>División de compensaciones</strong></td></tr><br>")
							.append("<tr><td class='membrete'><strong>Intendencia Nacional de Recursos Humanos</strong></td></tr>");
							mensaje.append("</table></body></html>");										
							if (log.isDebugEnabled()) log.debug("mensaje= " + mensaje.toString());																			
																	
							mParams.put("num_notificacion", num_notificacion);
							mParams.put("cod_pers", cod_pers);
							mParams.put("num_periodo", num_periodo);
							mParams.put("cod_licencia", cod_licencia);
							mParams.put("fec_ini_vacacion", fec_ini_vacacion);
							mParams.put("fec_envio_notific", ahora);
							mParams.put("cod_usucreac", "scheduler");
							mParams.put("fec_creac", ahora);										
							if (log.isDebugEnabled()) log.debug("mParams antes de insertar: "+mParams);//B
							insercionExitosa = t4502dao.insertNotificacionByVacaciones(mParams);
							if (insercionExitosa==true){
								if (log.isDebugEnabled()) log.debug("Se inserto la notificacion: "+ num_notificacion+" para el trabajador: "+cod_pers);
								Correo objCorreo = new Correo(mensaje.toString());									
								objCorreo.setServidor(propiedades.leePropiedad("servidor"));									
								//objCorreo.setRemitente(propiedades.leePropiedad("correoRemitenteVacaciones"),propiedades.leePropiedad("nombreRemitenteVacaciones")); //ICAPUNAY - PAS20181U230300016 - Mejoras vacaciones2
								objCorreo.setRemitente(propiedades.leePropiedad("correoReceptorAlertasVacaciones"),propiedades.leePropiedad("nombreReceptorAlertasVacaciones"));
								if(!direCorreo.equals("")){
									objCorreo.agregarDestinatario(direCorreo.trim());
									if(!correojefeUO.equals("")){
										objCorreo.agregarConCopia(correojefeUO.trim());
									}
									if(!correodelegadoUO.equals("")){
										objCorreo.agregarConCopia(correodelegadoUO.trim());
									}
								}									
								//objCorreo.setAsunto("Alerta de registro de solicitud de vacaciones y bloqueo de fotocheck"); //ICAPUNAY - PAS20181U230300016 - Mejoras vacaciones2
								objCorreo.setAsunto("Alerta de registro de solicitud de vacaciones");
								objCorreo.enviarHtml();
								if (log.isDebugEnabled()) log.debug("Se envio correo a trabajador.");
							}									
						} catch (CorreoException ce) {
							if (log.isDebugEnabled()) log.debug("Error en CorreoException: "+ce.toString());									 	
							if (log.isDebugEnabled()) log.debug("No se pudo enviar correo al trabajador con registro: " + cod_pers);									 										 	
						}					
					}//FIN DE FOR
				} catch (Exception ex) {
					if (log.isDebugEnabled()) log.debug("*** Exception ex - Error en enviarAlertaVacaciones****",ex);
					ocurrioError=true;
					enviarCorreo_NotificacionesVacacionesEnviadas(dcsp,mapaParametros);
				}	
				//ENVIO DE REPORTE RESUMEN DE NOTIFICACIONES ENVIADAS	
				if(ocurrioError==false){
					enviarCorreo_NotificacionesVacacionesEnviadas(dcsp,mapaParametros);
				}
				//FIN DE ENVIO REPORTE RESUMEN DE NOTIFICACIONES ENVIADAS							
			}else{
				if (log.isDebugEnabled()) log.debug("No existen vacaciones programadas activas para notificar a trabajadores.");				
			}
			//FIN DE ENVIO INDIVIDUAL DE NOTIFICACIONES			
						
		} catch (DAOException d) {
			if (log.isDebugEnabled()) log.error("*** DAOException Final - SQL Error en enviarAlertaVacaciones****",d);
			enviarCorreo_NotificacionesVacacionesEnviadas(dcsp,mapaParametros);						
			StringBuffer msg = new StringBuffer().append("ERROR PROCESO AUTOMATICO DE ENVIAR ALERTAS POR VACACIONES: ").append(d.toString());
        	throw new FacadeException(this, msg.toString());
        	
        } catch (CreateException e) {
        	if (log.isDebugEnabled()) log.error("*** CreateException Final - Error en enviarAlertaVacaciones****",e);
        	enviarCorreo_NotificacionesVacacionesEnviadas(dcsp,mapaParametros);        	
			throw new FacadeException(this, e.toString());			
		} catch (Exception e) {
			if (log.isDebugEnabled()) log.error("*** Exception Final - Error en enviarAlertaVacaciones****",e);
        	enviarCorreo_NotificacionesVacacionesEnviadas(dcsp,mapaParametros);        	
			throw new FacadeException(this, e.toString());			
		}
	}	
	
	/**
	 * Metodo que envia correo con archivo zipeado resumen de notificaciones de vacaciones enviadas en una fecha
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"	
	 * @param dcsp DataSource	
	 * @param mapaDatos Map	
	 * @throws FacadeException
	 * @throws RemoteException
	 * @throws CreateException
	 */	
	public void enviarCorreo_NotificacionesVacacionesEnviadas(DataSource dcsp,Map mapaDatos) throws FacadeException,RemoteException, CreateException{
		
		StringBuffer mensajeReporte=null;		
		File[] listaArchivosReporte=new File[1];// solo se adjuntara 1 archivo
		List notificacionesEnviadas = null;
		SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfformatoArchivo = new SimpleDateFormat("yyyyMMddHHmm");
		String nombreArchivo=null;
		Map mNotifEnviada = new HashMap();
		String uuooNotifEnviada=null;
		String regiNotifEnviada=null;
		String trabajNotifEnviada=null;
		String iniVacaNotifEnviada=null;
		String filaNotifEnviada=null;						
		ReporteFacadeHome reporteFacadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,ReporteFacadeHome.class);
		ReporteFacadeRemote reporteFacadeRemote = reporteFacadeHome.create();
		
		T4502DAO t4502dao = new T4502DAO(dcsp);//tabla t4502NotificaVac					
		Timestamp ahora = (Timestamp) mapaDatos.get("ahora");
		FechaBean fb_fecha = (FechaBean) mapaDatos.get("fb_fecha");					
		String st_fecha = (String) mapaDatos.get("st_fecha");				
		
		if (log.isDebugEnabled()) log.debug("Ingreso al metodo enviarCorreo_NotificacionesVacacionesEnviadas");
		if (log.isDebugEnabled()) log.debug("mapaDatos: "+mapaDatos);
		
		try {

			notificacionesEnviadas=t4502dao.findNotificacionesEnviadasByFechaEnvio(formatoFecha.parse(fb_fecha.getFormatDate("yyyy-MM-dd")));				
			File reporteCreado=null;
			
			if (notificacionesEnviadas!=null && !notificacionesEnviadas.isEmpty()){					
			
				int contMismaUUOO=1; //contador de trabajadores notificados de la misma UUOO
				Map notificacionJ=new HashMap();
				String uuooFilaJ=null;
				Map mDatos=new HashMap();
				mDatos.put("fechaIni",st_fecha);
				mDatos.put("fechaFin",st_fecha);				
				
				nombreArchivo="rptNoti"+sdfformatoArchivo.format(ahora).toString();
				reporteFacadeRemote.creaReporte(nombreArchivo, "NOTIFICACIONES DE VACACIONES REMITIDAS", mDatos, "scheduler");					
				reporteFacadeRemote.escribe("UUOO       REGISTRO                    NOMBRES                           INICIO GOCE      ", "NOTIFICACIONES DE VACACIONES REMITIDAS", mDatos, "scheduler");
				reporteFacadeRemote.escribe("==========================================================================================", "NOTIFICACIONES DE VACACIONES REMITIDAS", mDatos, "scheduler");
				reporteFacadeRemote.escribe(" ", "NOTIFICACIONES DE VACACIONES REMITIDAS", mDatos, "scheduler");
									
				for (int i = 0; i <= notificacionesEnviadas.size()-1; i++) {					
					
					mNotifEnviada=null;
					notificacionJ=null;
					
					mNotifEnviada=(HashMap)notificacionesEnviadas.get(i);					
					uuooNotifEnviada=(String)mNotifEnviada.get("cod_uorg");
					regiNotifEnviada=(String)mNotifEnviada.get("cod_pers");
					trabajNotifEnviada=((String)mNotifEnviada.get("nombre")).trim();
					iniVacaNotifEnviada=((Date)mNotifEnviada.get("fec_ini_vacacion")).toString();
					filaNotifEnviada=uuooNotifEnviada+" "+regiNotifEnviada+" "+trabajNotifEnviada+" "+iniVacaNotifEnviada;
											
					//codigo de impresion en el archivo filaNotifEnviada(i)
					reporteFacadeRemote.escribe(filaNotifEnviada, "NOTIFICACIONES DE VACACIONES REMITIDAS", mDatos, "scheduler");					
					
					if (i<notificacionesEnviadas.size()-1){								
							
							notificacionJ=(HashMap)notificacionesEnviadas.get(i+1);							
							uuooFilaJ=(String)notificacionJ.get("cod_uorg");	
							
							if (uuooFilaJ.equals(uuooNotifEnviada)){	
								contMismaUUOO=contMismaUUOO+1;									
							}else{									
								//codigo de impresion en el archivo (subtotal y contMismaUUOO)
								reporteFacadeRemote.escribe("==========================================================================================", "NOTIFICACIONES DE VACACIONES REMITIDAS", mDatos, "scheduler");
								reporteFacadeRemote.escribe("Subtotal"+" "+uuooNotifEnviada+" =  "+String.valueOf(contMismaUUOO), "NOTIFICACIONES DE VACACIONES REMITIDAS", mDatos, "scheduler");
								reporteFacadeRemote.escribe(" ", "NOTIFICACIONES DE VACACIONES REMITIDAS", mDatos, "scheduler");
								contMismaUUOO=1;							
							}							
					}else{						
						
						//codigo de impresion en el archivo (ultimo subtotal y contMismaUUOO)
						reporteFacadeRemote.escribe("==========================================================================================", "NOTIFICACIONES DE VACACIONES REMITIDAS", mDatos, "scheduler");
						reporteFacadeRemote.escribe("Subtotal"+" "+uuooNotifEnviada+" =  "+String.valueOf(contMismaUUOO), "NOTIFICACIONES DE VACACIONES REMITIDAS", mDatos, "scheduler");							
						reporteFacadeRemote.escribe(" ", "NOTIFICACIONES DE VACACIONES ENVIADAS", mDatos, "scheduler");
						//codigo de impresion en el archivo (total de filas)
						reporteFacadeRemote.escribe("==========================================================================================", "NOTIFICACIONES DE VACACIONES ENVIADAS", mDatos, "scheduler");
						reporteFacadeRemote.escribe("Total General"+" =  "+String.valueOf(notificacionesEnviadas.size()), "NOTIFICACIONES DE VACACIONES REMITIDAS", mDatos, "scheduler");							
					}
					
				}
				reporteCreado=reporteFacadeRemote.registraReporte(nombreArchivo, mDatos, "scheduler");
				
				if (reporteCreado.exists()){
					
					try{			
						
						listaArchivosReporte[0]=reporteCreado;						
						//ENVIO DE CORREO CON ARCHIVO RESUMEN ADJUNTO
						mensajeReporte = new StringBuffer("<html><head><title>REPORTE DE NOTIFICACIONES DE VACACIONES</title><style>")
						.append(".T1 {font-style  : normal;text-transform : uppercase;font-size :10pt;color:")
						.append("#FFFFFF;background  : #336699;font-family : Tahoma,Verdana,Arial,Helvetica,sans-serif ;}</style></head>")
						.append("<body><table width='100%' align='center' class='T1'><tr><td><center><strong>REPORTE DE NOTIFICACIONES DE VACACIONES</strong></center></td></tr></table><br><br>")
						.append("<table>")		
						.append("<tr><td class='membrete'><strong>Fecha:</strong></td></tr>")
						.append("<tr><td class='dato'>")
						.append(new FechaBean().getFormatDate("dd/MM/yyyy"))
						.append("</td></tr>")			
						.append("<tr><td class='membrete'><strong>Detalle:</strong></td></tr>")
						.append("<tr><td class='dato'>")
						.append("Se adjunta reporte de notificaciones de vacaciones remitidas correspondientes al "+new FechaBean().getFormatDate("dd/MM/yyyy"))
						.append("</td></tr>");		
						mensajeReporte.append("</table></body></html>");
											
						Correo objCorreoReporte = new Correo(mensajeReporte.toString());							
						objCorreoReporte.setServidor(propiedades.leePropiedad("servidor"));
						objCorreoReporte.setRemitente(propiedades.leePropiedad("correoReceptorAlertasVacaciones"),propiedades.leePropiedad("nombreReceptorAlertasVacaciones"));
						objCorreoReporte.agregarDestinatario(propiedades.leePropiedad("correoReceptorAlertasVacaciones"),propiedades.leePropiedad("nombreReceptorAlertasVacaciones"));			
						//objCorreoReporte.agregarDestinatario("icapunay@sunat.gob.pe");
						objCorreoReporte.setAsunto("Reporte resumen de notificaciones de vacaciones remitidas el "+new FechaBean().getFormatDate("dd/MM/yyyy"));
						if(listaArchivosReporte!=null && listaArchivosReporte.length>0){
							objCorreoReporte.setAdjuntos(listaArchivosReporte);		
						}									
						objCorreoReporte.enviarHtml();						
						if (log.isDebugEnabled()) log.debug("Se envio correo con archivo resumen de notificaciones.");
						
						reporteFacadeRemote.eliminaArchivosTemporales(nombreArchivo);
						//FIN DE ENVIO REPORTE RESUMEN DE NOTIFICACIONES ENVIADAS					
						
					} catch (Exception e) {
						if (log.isDebugEnabled()) log.debug("Exception Inicial en Envio Correo Resumen");
						throw new Exception("No se pudo enviar correo con archivo resumen de notificaciones de vacaciones");						
					}						
				}else{
					if (log.isDebugEnabled()) log.debug("Archivo Resumen de notificaciones enviadas no existe.");					
				}					
			}else{
				if (log.isDebugEnabled()) log.debug("No se enviaron notificaciones de vacaciones el dia de hoy.");					
			}	

		} catch (CreateException e) {
			log.debug("CreateException Final en Envio Correo Resumen");
			log.error(e.getMessage());			
		} catch (Exception e) {
			log.debug("Exception Final en Envio Correo Resumen");			
			log.error(e.getMessage());			
		} 
		
	}	
	/* FIN ICAPUNAY - TRABAJADORES CON VACACIONES PROGRAMADAS ACEPTADAS O ACTIVAS - 28/02/2011 */
	
	//JVILLACORTA - 03/11/2011 - MODIFICA ALERTA DE SOLICITUDES
	/**
	 * Encargado de calcular y enviar alertas de solicitudes a jefes y directivos
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * 
	 * @throws DAOException
	 * @throws RemoteException
	 */	
	public void enviarAlertaSolicitudes() throws FacadeException, RemoteException{
		try {			
				DataSource dcsp = sl.getDataSource("java:comp/env/jdbc/dcsp"); //sl esta arriba
				DataSource dgsp = sl.getDataSource("java:comp/env/jdbc/dgsp");
				T1455DAO t1455dao = new T1455DAO(dcsp);
				T4562DAO t4562dao = new T4562DAO(dgsp);
				pe.gob.sunat.rrhh.dao.T02DAO personalDAO = new pe.gob.sunat.rrhh.dao.T02DAO(dcsp);
				CorreoDAO correoDAO = new CorreoDAO(dcsp);
				String mensajeSol = "Se ha detectado que Ud. tiene las siguientes <strong> solicitudes pendientes de aprobaci&oacute;n:</strong>";
				
				List responsables = t1455dao.findRespSolicitudesPendientes();
				if (log.isDebugEnabled()) log.debug("responsables: " + responsables);
				Map responsable = new HashMap();
				
				String num_notificacion = null;
				FechaBean fec_actual = new FechaBean();			
				
				Timestamp ahora = new Timestamp(System.currentTimeMillis());
				if (log.isDebugEnabled()) log.debug("ahora: "+ahora);
				FechaBean fb_fecha = new FechaBean(ahora);
				if (log.isDebugEnabled()) log.debug("fb_fecha: "+fb_fecha);			
				String st_fecha = fb_fecha.getFormatDate("dd/MM/yyyy");
				if (log.isDebugEnabled()) log.debug("st_fecha: "+st_fecha);
				String annoActual = st_fecha.substring(6,10);
				if (log.isDebugEnabled()) log.debug("annoActual: "+annoActual);
				String hoy = fec_actual.getFormatDate("dd/MM/yyyy");//JVILLACORTA - 03/11/2011 - MODIFICA ALERTA DE SOLICITUDES
				if (log.isDebugEnabled()) log.debug("hoy: "+hoy);//JVILLACORTA - 03/11/2011 - MODIFICA ALERTA DE SOLICITUDES
				
				boolean insercionExitosa = false;
				Map mUltimaNotificacion = new HashMap();
				mUltimaNotificacion = null;
				String numero_notificacion = null;
				String anio = null;
				int correlativo = 0;
				
				Map mParams = new HashMap();
				Map mParams2 = new HashMap();//ICAPUNAY 09/08/2011 Para insertar todas las solicitudes para directivo 

				if (responsables != null && responsables.size()>0){
					for (int i=0; i<responsables.size(); i++) {
						responsable = (HashMap)responsables.get(i);
						if (responsable.get("cuser_dest")!= null && !responsable.get("cuser_dest").toString().trim().equals("")){
							try{//ICAPUNAY 02/08/2011 POR CAIDA CON CORREO ERRONEO
								if (log.isDebugEnabled()) log.debug("cuser_dest: " + responsable.get("cuser_dest").toString().trim());
								String nombre = personalDAO.findNombreCompleto(responsable.get("cuser_dest").toString().trim());
								if (log.isDebugEnabled()) log.debug("nombre jefe: " + nombre);
								
								StringBuffer sb_mensaje = new StringBuffer("");
								String mensajeF = "";
								responsable.put("hoy", hoy); //JVILLACORTA - 03/11/2011 - MODIFICA ALERTA DE SOLICITUDES
								List personal = t1455dao.findTrabSolicitudesPendientes(responsable);
	
								if(personal != null && personal.size()>0){
									for(int j=0; j<personal.size(); j++) {
										if (log.isDebugEnabled()) log.debug("personal: " + personal);
										Map Trab = (HashMap)personal.get(j); 
										String nombreTrab = personalDAO.findNombreCompleto(Trab.get("cod_pers").toString());
										if (log.isDebugEnabled()) log.debug("nombreTrabajador: " + nombreTrab);
										Trab.put("nombreTrab", nombreTrab);//para agregar un nuevo campo nombreTrab
									}
								}
	
								ResourceBundle bundle = ResourceBundle.getBundle("pe.gob.sunat.sp.asistencia.sirh");
								String servidorIP = bundle.getString("servidorIP");
								String programa = bundle.getString("programa1");
								String url = new MenuCliente().generaInvocacionURL(programa,null,true,MenuCliente.INTRANET,"/ol-ti-iaasistencia/asisS11Alias");
								String strURL = "http://"+servidorIP+url;
								if (log.isDebugEnabled()) log.debug("strURL= " + strURL);
								
								sb_mensaje.append("<tr><td>&nbsp;</td></tr>")			
								.append("<tr><td style='background-color:#B6CBEB;'>")
								//.append(generaTablaDatos(personal))
								.append(generaTablaDatos(personal, strURL))
								.append("</td></tr><tr><td>&nbsp;</td></tr>");
								if (log.isDebugEnabled()) log.debug("mensajeSol= " + mensajeSol);
								if (log.isDebugEnabled()) log.debug("sb_mensaje= " + sb_mensaje);
							
								mensajeF = mensajeSol + sb_mensaje.toString();
												
								List listaTodasNotificaciones = t4562dao.findAllNotificacionesEnviadas();
								if (listaTodasNotificaciones == null ||  listaTodasNotificaciones.isEmpty()){
									num_notificacion = annoActual + Numero.format(new Integer(1), "000000");						
								}
								if(listaTodasNotificaciones != null && !listaTodasNotificaciones.isEmpty()){						
									mUltimaNotificacion=t4562dao.findUltimaNotificacionRegistrada();
									if (mUltimaNotificacion != null && !mUltimaNotificacion.isEmpty()){							
										numero_notificacion = (String)mUltimaNotificacion.get("num_notificacion");							
										anio = numero_notificacion.substring(0,4);							
										correlativo = Integer.parseInt(numero_notificacion.substring(4,10));							
										if(anio.equals(annoActual)){
											num_notificacion = anio + Numero.format(new Integer(correlativo+1), "000000");								
										}else{
											num_notificacion = annoActual + Numero.format(new Integer(1), "000000");								
										}
									}
								}
								if (log.isDebugEnabled()) log.debug("num_notificacion= " + num_notificacion);
								
								if(personal != null && personal.size()>0){
									String texto=null;
									for(int j=0; j<personal.size(); j++) {
										//Map mParams = (HashMap)personal.get(j); 
										mParams = (HashMap)personal.get(j);
										mParams.put("num_notificacion", num_notificacion);
										mParams.put("cod_anno", mParams.get("anno").toString()); //ano de la solicitud
										mParams.put("num_solicitud", mParams.get("numero").toString()); //numero de la solicitud
										//mParams.put("cod_uuoo", mParams.get("u_organ").toString()); //unidad organizacional a la que pertenece la solicitud
										mParams.put("cod_uuoo", mParams.get("u_organ").toString()); //unidad organizacional a la que pertenece el seguimiento - ICAPUNAY 08/08/2011
										mParams.put("cod_pers", mParams.get("cod_pers").toString()); //Codigo de registro del trabajador que emitira la solicitud
										mParams.put("num_seguim", mParams.get("num_seguim").toString()); //Numero de seguimiento de la solicitud.
										mParams.put("tipo_solicitud", mParams.get("mov").toString()); //mov=06 LICENCIA SIN GOCE DE HABER HASTA 3 DIAS1        
										mParams.put("fec_notificacion", fec_actual.getSQLDate()); //Fecha actual de envio de la notificacion
										mParams.put("cod_pers_notif", mParams.get("cuser_dest").toString().trim()); //Codigo de registro del directivo notificado
										
										//Para el link se envia strURL, de lo contrario ""
										if (log.isDebugEnabled()) log.debug("mParams= " + mParams);
										if (log.isDebugEnabled()) log.debug("nombre= " + nombre);
										if (log.isDebugEnabled()) log.debug("mensajeF= " + mensajeF);
											
										texto = Utiles.textoCorreoSolicitudes(mParams, nombre, mensajeF);//ICAPUNAY 02/08/2011 POR CAIDA CON CORREO ERRONEO
											
										if (log.isDebugEnabled()) log.debug("texto= " + texto);										
									}									
									//ICAPUNAY 02/08/2011 POR CAIDA CON CORREO ERRONEO
									if (log.isDebugEnabled()) log.debug("texto final= " + texto);
									Correo objCorreo = new Correo(texto);
									objCorreo.setServidor(propiedades.leePropiedad("servidor"));
									objCorreo.setRemitente(propiedades.leePropiedad("correoRemitenteSolicitudes"),propiedades.leePropiedad("nombreRemitenteSolicitudes"));
									objCorreo.agregarDestinatario(correoDAO.findCorreoByRegistro(responsable.get("cuser_dest").toString().trim()),nombre); //el jefe o directivo
									objCorreo.setAsunto("Notificación de solicitudes pendientes para directivos.");
									objCorreo.enviarHtml();	
									if (log.isDebugEnabled()) log.debug("Se envio correo a responsable: "+ responsable.get("cuser_dest").toString().trim());									
									
								}								
								//ICAPUNAY 09/08/2011 Para insertar todas las solicitudes para directivo 
								if(personal != null && personal.size()>0){
									if (log.isDebugEnabled()) log.debug("solicitudes del directivo: " + responsable.get("cuser_dest").toString().trim()+ " "+ personal);
									for(int j=0; j<personal.size(); j++) {										
										mParams2 = (HashMap)personal.get(j);
										mParams2.put("num_notificacion", num_notificacion);
										mParams2.put("cod_anno", mParams2.get("anno").toString()); //ano de la solicitud
										mParams2.put("num_solicitud", mParams2.get("numero").toString()); //numero de la solicitud										
										mParams2.put("cod_uuoo", mParams2.get("u_organ").toString()); //unidad organizacional a la que pertenece el seguimiento - ICAPUNAY 08/08/2011
										mParams2.put("cod_pers", mParams2.get("cod_pers").toString()); //Codigo de registro del trabajador que emitira la solicitud
										mParams2.put("num_seguim", mParams2.get("num_seguim").toString()); //Numero de seguimiento de la solicitud.
										mParams2.put("tipo_solicitud", mParams2.get("mov").toString()); //mov=06 LICENCIA SIN GOCE DE HABER HASTA 3 DIAS1        
										mParams2.put("fec_notificacion", fec_actual.getSQLDate()); //Fecha actual de envio de la notificacion
										mParams2.put("cod_pers_notif", mParams2.get("cuser_dest").toString().trim()); //Codigo de registro del directivo notificado										
										if (log.isDebugEnabled()) log.debug("mParams2= " + mParams2);
										insercionExitosa = t4562dao.insertNotificacionBySolicitudes(mParams2);								
										
										if (insercionExitosa==true){
											if (log.isDebugEnabled()) log.debug("Se inserto notificacion: "+ num_notificacion+" de la nro solicitud: "+mParams2.get("numero").toString()+" del trabajador: "+mParams2.get("cod_pers").toString()+" para el directivo: "+responsable.get("cuser_dest").toString().trim());
										}										
									}
								}								
							//ICAPUNAY 02/08/2011 POR CAIDA CON CORREO ERRONEO	
							}catch (CorreoException ce) {
								if (log.isDebugEnabled()) log.debug("Error en CorreoException: "+ce.toString());									 	
								if (log.isDebugEnabled()) log.debug("No se pudo enviar correo al responsable: " + responsable.get("cuser_dest").toString().trim());									 										 	
							}							
						}											
					}
				}	
			
		} catch (DAOException d) {
			log.error("*** SQL Error ****",d);			
			StringBuffer msg = new StringBuffer().append("ERROR PROCESO AUTOMATICO DE ENVIAR ALERTAS POR SOLICITUD: ").append(d.toString());
        	throw new FacadeException(this, msg.toString());
        } catch (Exception e) {
			throw new FacadeException(this, e.toString());
		}
	} //FIN - JVILLACORTA - 03/11/2011 - MODIFICA ALERTA DE SOLICITUDES
	
	/* DTARAZONA - ENVIAR ALERTA DE SOLICITUDES VACACIONALES PENDIENTES DE APROBACIÓN - 22/01/2018 */
	/**
	 * MÃƒÆ’Ã‚Â©todo encargado de enviar alertas de solicitudes vacacionales pendientes en forma automÃƒÆ’Ã‚Â¡tica	 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @throws FacadeException
	 * @throws RemoteException
	 * @throws CreateException
	 */	
	public void enviarAlertaSolicitudVacacionesPendientes() throws FacadeException,RemoteException{
		
		try {	
			//INICIO			
			DataSource dcsp = sl.getDataSource("java:comp/env/jdbc/dcsp");//datasource de lectura
			DataSource dgsp = sl.getDataSource("java:comp/env/jdbc/dgsp");//datasource de escritura			
			T1455DAO t1455dao = new T1455DAO(dcsp);
			T4562DAO t4562dao = new T4562DAO(dgsp);
			CorreoDAO correoDAO = new CorreoDAO(dcsp);//tabla correos
		
			List listadoDirectivos = t1455dao.buscarDirectivosSolicitudVacacionesPendientes();//listado de directivos que tienen solicitudes de vacaciones pendientes de aprobacion
			if (log.isDebugEnabled()) log.debug("responsables: " + listadoDirectivos);
			Map mDirectivos = new HashMap();		
			Map mSolicitudesPendientes=new HashMap();
			Map mParams2=new HashMap();
			
			String num_notificacion = null;
			FechaBean fec_actual = new FechaBean();			
			
			boolean insercionExitosa = false;
			Map mUltimaNotificacion = new HashMap();
			mUltimaNotificacion = null;
			String numero_notificacion = null;
			String anio = null;
			int correlativo = 0;
			
			Timestamp ahora = new Timestamp(System.currentTimeMillis());
			if (log.isDebugEnabled()) log.debug("ahora: "+ahora);
			FechaBean fb_fecha = new FechaBean(ahora);
			if (log.isDebugEnabled()) log.debug("fb_fecha: "+fb_fecha);			
			String st_fecha = fb_fecha.getFormatDate("dd/MM/yyyy");
			if (log.isDebugEnabled()) log.debug("st_fecha: "+st_fecha);
			String annoActual = st_fecha.substring(6,10);
			if (log.isDebugEnabled()) log.debug("annoActual: "+annoActual);
			String hoy = fec_actual.getFormatDate("dd/MM/yyyy");
			if (log.isDebugEnabled()) log.debug("hoy: "+hoy);
			
			//ENVIO DE REPORTE RESUMEN DE NOTIFICACIONES ENVIADAS
			Map mapaParametros = new HashMap();		
			mapaParametros.put("ahora",ahora);
			mapaParametros.put("fb_fecha",fb_fecha);
			mapaParametros.put("st_fecha",st_fecha);	
			//FIN DE ENVIO REPORTE RESUMEN DE NOTIFICACIONES ENVIADAS
		
			String cod_dir=null;//ok
			String dir_apPaterno=null;//ok
			String dir_apMaterno=null;//ok
			String dir_nombres=null;//ok
			String dirCorreo; //ok
			String registros="";
			
			StringBuffer mensaje=null;	
			
			boolean tieneCorreoTrab=false;						
			//ENVIO INDIVIDUAL DE NOTIFICACIONES DE VACACIONES
						
			if(listadoDirectivos!=null && !listadoDirectivos.isEmpty()){
				
					//INICIO DE FOR
					if (log.isDebugEnabled()) log.debug("listadoDirectivos.size()= " + String.valueOf(listadoDirectivos.size()));//B
					for (int i = 0; i < listadoDirectivos.size(); i++) {					
						
						mDirectivos = (Map) listadoDirectivos.get(i);		//ok							
						cod_dir= mDirectivos.get("cuser_dest")!=null?(String) mDirectivos.get("cuser_dest"):"";	//ok				
						dir_apPaterno = mDirectivos.get("t02ap_pate")!=null?(String) mDirectivos.get("t02ap_pate"):"";	//ok				
						dir_apMaterno = mDirectivos.get("t02ap_mate")!=null?(String) mDirectivos.get("t02ap_mate"):"";	//ok
						dir_nombres = mDirectivos.get("t02nombres")!=null?(String) mDirectivos.get("t02nombres"):"";	//ok
						dirCorreo = "";//ok
						mUltimaNotificacion = null;
						numero_notificacion=null;
						anio=null;
						correlativo=0;
						mensaje = null;	
						tieneCorreoTrab = false;					
							
						// VALIDANDO SI EL DIRECTIVO TIENE CORREO Y LOGIN						
						dirCorreo=correoDAO.findCorreoByRegistro(cod_dir); //ok
						
						if(!dirCorreo.equals("")){
							tieneCorreoTrab=true;
							if (log.isDebugEnabled()) log.debug("direCorreoJefe Jefe= " + dirCorreo);//B		
						} else {
							if (log.isDebugEnabled()) log.debug("Jefe: "+ cod_dir+ " no tiene Correo Asignado.");							
						}
						Map params1 = new HashMap();		
						params1.put("cod_dir",cod_dir);
						
						List solicitudesPendientesAp=new ArrayList();
						registros="";
						if (tieneCorreoTrab == true) {
							solicitudesPendientesAp=t1455dao.buscarSolicitudesPendientesAprobar(params1);
							log.debug("Solicitudes del directivo "+cod_dir+" - "+solicitudesPendientesAp.toString());
							if (solicitudesPendientesAp==null || solicitudesPendientesAp.isEmpty()){ 								
								num_notificacion = annoActual+Numero.format(new Integer(1), "00000");							
								if (log.isDebugEnabled()) log.debug("num_solicitudes: "+ num_notificacion);
							}
							if(solicitudesPendientesAp!=null && !solicitudesPendientesAp.isEmpty()){	
								
								ResourceBundle bundle = ResourceBundle.getBundle("pe.gob.sunat.sp.asistencia.sirh");
								String servidorIP = bundle.getString("servidorIP");
								String programa = bundle.getString("programa1");
								String url1 = new MenuCliente().generaInvocacionURL(programa,null,true,MenuCliente.INTRANET,"/ol-ti-iaasistencia/asisS11Alias");
								String strURL = "http://"+servidorIP+url1;
								if (log.isDebugEnabled()) log.debug("strURL= " + strURL);
								
									
									
									List listaTodasNotificaciones = t4562dao.findAllNotificacionesEnviadas();
									if (listaTodasNotificaciones == null ||  listaTodasNotificaciones.isEmpty()){
										num_notificacion = annoActual + Numero.format(new Integer(1), "000000");						
									}
									if(listaTodasNotificaciones != null && !listaTodasNotificaciones.isEmpty()){						
										mUltimaNotificacion=t4562dao.findUltimaNotificacionRegistrada();
										if (mUltimaNotificacion != null && !mUltimaNotificacion.isEmpty()){							
											numero_notificacion = (String)mUltimaNotificacion.get("num_notificacion");							
											anio = numero_notificacion.substring(0,4);							
											correlativo = Integer.parseInt(numero_notificacion.substring(4,10));							
											if(anio.equals(annoActual)){
												num_notificacion = anio + Numero.format(new Integer(correlativo+1), "000000");								
											}else{
												num_notificacion = annoActual + Numero.format(new Integer(1), "000000");								
											}
										}
									}
									if (log.isDebugEnabled()) log.debug("num_notificacion= " + num_notificacion);
									for(int j=0;j<solicitudesPendientesAp.size();j++)
									{
										mSolicitudesPendientes=(Map)solicitudesPendientesAp.get(j);
										registros+="<tr><td>"+mSolicitudesPendientes.get("cod_pers")+" - "+mSolicitudesPendientes.get("t02ap_pate")+" "+mSolicitudesPendientes.get("t02ap_mate")+", "
										+mSolicitudesPendientes.get("t02nombres")+"</td><td><a href='"+strURL+"' target='_blank'>"+anio+"-"+mSolicitudesPendientes.get("numero")+"</a></td><td>"
										+mSolicitudesPendientes.get("fecha1")+"</td><td>"+mSolicitudesPendientes.get("fec_inicio")+"</td><td>"+mSolicitudesPendientes.get("fec_fin")+"</td></tr>";
									}
								
							}	
							try{
										mensaje = new StringBuffer("<html><head><title>SIRH - ASISTENCIA</title><style>")
										.append(".T1 {font-style  : normal;font-size :10pt;color:")
										.append("#FFFFFF;background  : #336699;font-family : Tahoma,Verdana,Arial,Helvetica,sans-serif ; padding:4px;} table{border-collapse:collapse;}.T2 td,th{border: 1px solid #336699;padding: 2px 8px;}</style></head>")
										.append("<body><table width='100%' align='center' class='T1'><tr><td><center><strong>NOTIFICACI&Oacute;N DE VACACIONES</strong></center></td></tr></table><br><br>")
										.append("<table>")
										.append("<tr><td class='membrete'><strong>Ticket:</strong></td></tr>")
										.append("<tr><td class='dato'>")										
										.append(num_notificacion)
										.append("</td></tr>")
										.append("<tr><td class='membrete'><strong>Fecha:</strong></td></tr>")
										.append("<tr><td class='dato'>")									
										.append(new FechaBean().getFormatDate("dd/MM/yyyy")) 																									
										.append("</td></tr>")
										.append("<tr><td class='membrete'><strong>Asunto:</strong></td></tr>")
										.append("<tr><td class='dato'>")
										.append("Alerta de solicitudes de vacaciones pendientes de trabajadores")
										.append("</td></tr>")
										.append("<tr><td>&nbsp;</td></tr>")
										.append("<tr><td class='membrete'><strong>Sr(a)(ita): </strong>")
										.append(cod_dir+" - "+dir_apPaterno+" "+dir_apMaterno+", "+dir_nombres)
										.append("</td></tr>")
										.append("<tr><td>&nbsp;</td></tr>")
										.append("<tr><td class='dato'>")
										.append("Estimado Directivo,<br>Usted tiene solicitudes de vacaciones pendientes de aprobar de los siguietes colaboradores:")
										.append("</td></tr>")
										.append("<tr><td>&nbsp;</td></tr>")
										.append("<tr><td><table class='T2'><thead class='T1'><th>Colaborador</th><th>Solicitud vacaciones</th><th>Fecha registro</th><th>Fecha inicio vac.</th><th>Fecha fin vac.</th></thead><tbody>")
										.append(registros) //TODAS LAS SOLICITUDES POR CADA DIRECTIVO
										.append("</tbody> </table></td></tr>")
										
										.append("<tr><td>&nbsp;</td></tr>")
										.append("<tr><td>Atentamente,</td></tr>")
										.append("<tr><td class='membrete'><strong>División de compensaciones</strong></td></tr>")
										.append("<tr><td class='membrete'><strong>Intendencia Nacional de Recursos Humanos</strong></td></tr>");
										
										mensaje.append("</table></body></html>");										
										if (log.isDebugEnabled()) log.debug("mensaje= " + mensaje.toString());																			
										Correo objCorreo = new Correo(mensaje.toString());									
										objCorreo.setServidor(propiedades.leePropiedad("servidor"));									
										objCorreo.setRemitente(propiedades.leePropiedad("correoRemitenteSolicitudes"),propiedades.leePropiedad("nombreRemitenteSolicitudes"));	
										objCorreo.agregarDestinatario(correoDAO.findCorreoByRegistro(mDirectivos.get("cuser_dest").toString().trim())); //el jefe o directivo
										objCorreo.setAsunto("Alerta de solicitudes de vacaciones pendientes de trabajadores");
										objCorreo.enviarHtml();
										if (log.isDebugEnabled()) log.debug("Correo enviado al Directivo.");										
										
										//REGISTRAR LAs NOTIFICACION
										if(solicitudesPendientesAp != null && solicitudesPendientesAp.size()>0){
											if (log.isDebugEnabled()) log.debug("solicitudes del directivo: " + mDirectivos.get("cuser_dest").toString().trim()+ " "+ solicitudesPendientesAp);
											for(int j=0; j<solicitudesPendientesAp.size(); j++) {										
												mParams2 = (HashMap)solicitudesPendientesAp.get(j);
												mParams2.put("num_notificacion", num_notificacion);
												mParams2.put("cod_anno", mParams2.get("anno").toString()); //ano de la solicitud
												mParams2.put("num_solicitud", mParams2.get("numero").toString()); //numero de la solicitud										
												mParams2.put("cod_uuoo", mParams2.get("u_organ").toString()); //unidad organizacional a la que pertenece el seguimiento - ICAPUNAY 08/08/2011
												mParams2.put("cod_pers", mParams2.get("cod_pers").toString()); //Codigo de registro del trabajador que emitira la solicitud
												mParams2.put("num_seguim", mParams2.get("num_seguim").toString()); //Numero de seguimiento de la solicitud.
												mParams2.put("tipo_solicitud", mParams2.get("mov").toString()); //mov=06 LICENCIA SIN GOCE DE HABER HASTA 3 DIAS1        
												mParams2.put("fec_notificacion", fec_actual.getSQLDate()); //Fecha actual de envio de la notificacion
												mParams2.put("cod_pers_notif", mParams2.get("cuser_dest").toString().trim()); //Codigo de registro del directivo notificado										
												if (log.isDebugEnabled()) log.debug("mParams2= " + mParams2);
												insercionExitosa = t4562dao.insertNotificacionBySolicitudes(mParams2);								
												
												if (insercionExitosa==true){
													if (log.isDebugEnabled()) log.debug("Se inserto notificacion: "+ num_notificacion+" de la nro solicitud: "+mParams2.get("numero").toString()+" del trabajador: "+mParams2.get("cod_pers").toString()+" para el directivo: "+mSolicitudesPendientes.get("cuser_dest").toString().trim());
												}										
											}
										}
										
							 } catch (CorreoException ce) {
								 		if (log.isDebugEnabled()) log.debug("Error en CorreoException: "+ce.toString());									 	
									 	if (log.isDebugEnabled()) log.debug("No se pudo enviar correo al trabajador con registro: " + cod_dir);									 										 	
							}							
						}
					}
				
			}
						
		} catch (DAOException d) {
			log.error("*** SQL Error ****",d);			
			StringBuffer msg = new StringBuffer().append("ERROR PROCESO AUTOMATICO DE ENVIAR ALERTAS POR SOLICITUDES VACACIONALES PENDIENTES DE APROBAR: ").append(d.toString());
        	throw new FacadeException(this, msg.toString());
        } catch (Exception e) {
			throw new FacadeException(this, e.toString());
		}
	}	
	/**
	 * MÃƒÆ’Ã‚Â©todo encargado de enviar alertas de solicitudes vacacionales pendientes en forma automÃƒÆ’Ã‚Â¡tica	 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @throws FacadeException
	 * @throws RemoteException
	 * @throws CreateException
	 */	
	public void enviarAlertaPersonalSinTurno() throws FacadeException,RemoteException{
		
		try {	
			String num_notificacion = null;
			
			Map mUltimaNotificacion = new HashMap();
			mUltimaNotificacion = null;
			String numero_notificacion = null;
			String anio = null;
			int correlativo = 0;
			
			//Timestamp ahora = new Timestamp(System.currentTimeMillis());
			FechaBean fb_fecha = new FechaBean();
			String hoy = fb_fecha.getFormatDate("dd/MM/yyyy");
			if(log.isDebugEnabled()) log.debug("Hoy:"+hoy);
			String mesActual=fb_fecha.getMes();//hoy.toString().trim().substring(3,2);
			if(log.isDebugEnabled()) log.debug("MesActual:"+mesActual);
			int annoActual =Integer.parseInt(fb_fecha.getAnho());// Integer.parseInt(hoy.toString().trim().substring(6));
			if(log.isDebugEnabled()) log.debug("Anio actual:"+annoActual);
			String mesAnterior="";
			String mesPosterior="",fechaInicio="",fechaFin="",fechaEvaluar="",fechaInicioTemp="";
			
			Calendar c=Calendar.getInstance();
			if(mesActual.equals("01") || mesActual.equals("12")){
				if(mesActual.equals("01"))
				{
					mesAnterior="12";					
					mesPosterior="02";
					c.set(annoActual, Integer.parseInt(mesPosterior)-1, 1);
					fechaFin=(c.getActualMaximum(Calendar.DAY_OF_MONTH)<10?"0"+c.getActualMaximum(Calendar.DAY_OF_MONTH):""+c.getActualMaximum(Calendar.DAY_OF_MONTH))+"/"+mesPosterior+"/"+annoActual;
					fechaInicio="01/12/"+(annoActual-1);
				}else
				{
					mesAnterior="11";
					mesPosterior="01";
					c.set(annoActual+1, Integer.parseInt(mesPosterior)-1, 1);
					fechaFin=(c.getActualMaximum(Calendar.DAY_OF_MONTH)<10?"0"+c.getActualMaximum(Calendar.DAY_OF_MONTH):""+c.getActualMaximum(Calendar.DAY_OF_MONTH))+"/"+mesPosterior+"/"+(annoActual+1);
					fechaInicio="01/11/"+annoActual;
				}
			}else{
				mesAnterior=Integer.parseInt(mesActual)-1>9?""+(Integer.parseInt(mesActual)-1):"0"+(Integer.parseInt(mesActual)-1);
				mesPosterior=(Integer.parseInt(mesActual)+1)>9?""+(Integer.parseInt(mesActual)+1):"0"+(Integer.parseInt(mesActual)+1);
				c.set(annoActual, Integer.parseInt(mesPosterior)-1, 1);
				fechaFin=(c.getActualMaximum(Calendar.DAY_OF_MONTH)<10?"0"+c.getActualMaximum(Calendar.DAY_OF_MONTH):""+c.getActualMaximum(Calendar.DAY_OF_MONTH))+"/"+mesPosterior+"/"+annoActual;
				fechaInicio="01/"+mesAnterior+"/"+annoActual;
			}
			int diaActual=Integer.parseInt(fb_fecha.getDia());
			if(log.isDebugEnabled()) log.debug("FechasRango:"+fechaInicio+"-"+fechaFin);
			if(diaActual<15){
				c.set(annoActual,Integer.parseInt(mesActual)-1, 1);//los meses se cuentan desde 0=Enero y setiembre es 8
				fechaFin=(c.getActualMaximum(Calendar.DAY_OF_MONTH)<10?"0"+c.getActualMaximum(Calendar.DAY_OF_MONTH):""+c.getActualMaximum(Calendar.DAY_OF_MONTH))+"/"+mesActual+"/"+annoActual;
			}
			
			String meses="",fecIni=fechaInicio,mes="";
			FechaBean fbst=new FechaBean(fecIni);
			mes=fbst.getMesletras();
			meses+=mes;
			while(Utiles.obtenerDiasDiferencia(fecIni, fechaFin)>0){
				FechaBean fbst1=new FechaBean(fecIni);	
				if(!fbst1.getMesletras().equals(mes))
				{
					mes=fbst1.getMesletras();
					meses+=", "+mes;
				}
				fecIni=Utiles.dameFechaSiguiente(fecIni, 1);
			}
			
			fechaEvaluar=fechaInicio;
			fechaInicioTemp=fechaInicio;
			if(log.isDebugEnabled()) log.debug("FechasRango:"+fechaInicio+"-"+fechaFin);
			if(log.isDebugEnabled()) log.debug("MesesRango:"+meses);
			//ACCESO A DATA
			DataSource dsSig = sl.getDataSource("java:comp/env/jdbc/dcsig");
			TPeriodosDAO tpdao=new TPeriodosDAO(dsSig);
			
			DataSource dcsp = sl.getDataSource("java:comp/env/jdbc/dcsp");//datasource de lectura
			DataSource dgsp = sl.getDataSource("java:comp/env/jdbc/dgsp");//datasource de escritura			
			T4562DAO t4562dao = new T4562DAO(dgsp);
			CorreoDAO correoDAO = new CorreoDAO(dcsp);//tabla correos
			T12DAO t12dao=new T12DAO(dcsp);
			pe.gob.sunat.rrhh.dao.T02DAO personalDAO = new pe.gob.sunat.rrhh.dao.T02DAO(dcsp);
			T1271DAO t1271dao=new T1271DAO(dcsp);
			List listadoUODirectivos = t12dao.buscarUODirectivos();//listado de UNIDADES con sus jefes
			if (log.isDebugEnabled()) log.debug("Unidades/Jefes: " + listadoUODirectivos);
			String dirCorreo="";
			if(listadoUODirectivos.size()>0){
				for(int a=0;a<5/*listadoUODirectivos.size()*/;a++){
					//OBTENER URL
					ResourceBundle bundle = ResourceBundle.getBundle("pe.gob.sunat.sp.asistencia.sirh");
					String servidorIP = bundle.getString("servidorIP");
					String programa = bundle.getString("programa7");
					String url1 = new MenuCliente().generaInvocacionURL(programa,null,true,MenuCliente.INTRANET,"/ol-ti-iaasistencia/asisS07Alias");
					String strURL = "http://"+servidorIP+url1;
					if (log.isDebugEnabled()) log.debug("strURL= " + strURL);					
					
					HashMap unidad=(HashMap)listadoUODirectivos.get(a);
					String uo=unidad.get("t12cod_uorga")!=null?unidad.get("t12cod_uorga").toString():"";
					String codDir=unidad.get("jefefinal")!=null?unidad.get("jefefinal").toString():"";
					String dir_nombres=personalDAO.findNombreCompleto(codDir);
					// VALIDANDO SI EL DIRECTIVO TIENE CORREO Y LOGIN						
					dirCorreo=correoDAO.findCorreoByRegistro(codDir); //ok
					if (!dirCorreo.equals(""))
					{ 					
					List personalPorUO=personalDAO.findColaboradorByUO(uo);
					if (log.isDebugEnabled()) log.debug("Colaboradores de "+uo+":" + personalPorUO);
					
					String evalTrab="";
					boolean tieneSinTurno=false;					
					if(personalPorUO!=null && personalPorUO.size()>0){
						for(int b=0;b<personalPorUO.size();b++){
							fechaInicio=fechaInicioTemp;
							HashMap colab=(HashMap)personalPorUO.get(b);
							log.debug("FechaIniOrig:"+fechaEvaluar+",fecIngreso:"+colab.get("t02f_ingsun").toString()+"Dif:"+Utiles.obtenerDiasDiferencia(fechaInicio, colab.get("t02f_ingsun").toString()));
							if(Utiles.obtenerDiasDiferencia(fechaInicio, colab.get("t02f_ingsun").toString())>0){
								//fechaEvaluar=colab.get("t02f_ingsun").toString();
								fechaInicio=colab.get("t02f_ingsun").toString();
							}
							fechaEvaluar=fechaInicio;	
							log.debug("FechaIniOrig:"+fechaEvaluar);
							String codPers=(String)colab.get("t02cod_pers");
							String nombresTrab=(String)colab.get("nombres");
							//obtener el contrato vigente desde siga para cada trabajador 	
							FechaBean fini=new FechaBean(fechaInicio);
							FechaBean ffin=new FechaBean(fechaFin);
							Map params=new HashMap();
							params.put("codPers", codPers);
							params.put("fechaIni", fini.getTimestamp());
							params.put("fechaFin", ffin.getTimestamp());
							List contratos=new ArrayList();
							if(((String)colab.get("t02cod_rel")).equals("09") || ((String)colab.get("t02cod_rel")).equals("10"))
							{
								contratos=tpdao.findContratosByFechas(params);
							}else{
								String dbpool = "jdbc/dgsp";
								//DataSource ds = sl.getDataSource(dbpool);			
								T99DAO t99dao = new T99DAO(dbpool);
								
								HashMap hm=new HashMap();
								hm.put("codigo_convenio", "201xxxxxx");
								hm.put("fechaini", fechaInicio);
								hm.put("fechafin", fechaFin);
								
								if(colab.get("t02cod_regl").equals(Constantes.D_LEG_728_T_P))
								{
									HashMap param_t99=new HashMap();
									param_t99.put("t99cod_tab","510");
									param_t99.put("t99tip_desc","D");
									param_t99.put("t99estado","1");
									param_t99.put("t99codigo","51");
									Map map_horas=t99dao.findParamByCodTabCodigo(param_t99);
									hm.put("cant_horas_semanales", map_horas.get("t99descrip")!=null?map_horas.get("t99descrip").toString():"40");
								}else{
									hm.put("cant_horas_semanales", "40");
								}
								
								hm.put("tipo_accion", "0001");
								contratos.add(hm);
							}
							if (log.isDebugEnabled()) log.debug("Contrato de "+codPers+":" + contratos.toString()+"-"+colab.get("t02cod_rel"));
							if(contratos!=null && contratos.size()>0){
								String numContrato="",numContratoTemp=""; 
								String accion="",accionTemp="";
								int horasSemanal=0,horasSemanalTemp=0;
								int numDiasSemana=0;
								String diaEnLetra="";
								String mesEnLetra="";
								int horasAcumulado=0;
								String inicioSemana="";
								int contador=0;
								boolean tieneTurnoAdm=true;
								boolean sinTurno=false;
								while(Utiles.obtenerDiasDiferencia(fechaEvaluar, fechaFin)>=0){
									int filaContrato=0;
									numContratoTemp=numContrato;
									accionTemp=accion;
									horasSemanalTemp=horasSemanal;									
									while(filaContrato<contratos.size()){
										HashMap contrato=(HashMap)contratos.get(filaContrato);
										String fic=(String)contrato.get("fechaini");
										String ffc=(String)contrato.get("fechafin");
										if(Utiles.obtenerDiasDiferencia(fic, fechaEvaluar)>=0){
											if(Utiles.obtenerDiasDiferencia(fechaEvaluar, ffc)>=0){												
												numContrato=contrato.get("codigo_convenio")!=null?contrato.get("codigo_convenio").toString().trim():"";
												accion=contrato.get("tipo_accion")!=null?contrato.get("tipo_accion").toString():"";
												horasSemanal=contrato.get("cant_horas_semanales")!=null?Integer.parseInt(contrato.get("cant_horas_semanales").toString()):0;
												if(log.isDebugEnabled()) log.debug("Datos:"+numContrato+"-"+accion+"-"+horasSemanal);
												if(Utiles.obtenerDiasDiferencia(fechaInicio, fechaEvaluar)==0){
													numContratoTemp=numContrato;
													accionTemp=accion;
													horasSemanalTemp=horasSemanal;
													inicioSemana=fechaEvaluar;
													FechaBean fb1=new FechaBean(fechaEvaluar);
													mesEnLetra=fb1.getMesletras();
												}
												break;
											}else{
												filaContrato++;
											}
										}else{
											filaContrato++;
										}
									}
									FechaBean fb=new FechaBean(fechaEvaluar);
									diaEnLetra=fb.getDiasemana();if(log.isDebugEnabled()) log.debug("Dia:"+diaEnLetra);
									
									//analizamos los turnos y acumulamos las horas dentro de la semana
									List detTurnos=new ArrayList();
									HashMap hm=new HashMap();
									hm.put("codPers", codPers);
									hm.put("fecha", fechaEvaluar);
									detTurnos=t1271dao.buscarDetalleTurnoPersona(hm);
									
									int horasDia=0;
									float minDia=0;
									
									int horasEq=0;
									if(tieneTurnoAdm)
										horasEq=(horasSemanal*contador)/5;
									else
										horasEq=horasSemanal;
									
									if(detTurnos.size()>0){
										sinTurno=true;
										HashMap detTurno=(HashMap)detTurnos.get(0);										
										if(detTurno.get("oper_id").toString().trim().equals("0")){
											if((diaEnLetra.equals("Sabado")||diaEnLetra.equals("Domingo"))){
												minDia=0;
												if(fb.getMesletras().equals(mesEnLetra))
												contador--;
											}else{
												minDia= Utiles.obtenerMinutosDiferencia(detTurno.get("h_inicio").toString(), detTurno.get("h_fin").toString())-(detTurno.get("min_refr")!=null?Integer.parseInt(detTurno.get("min_refr").toString()):0);
											}
											if(diaEnLetra.equals("Lunes"))
												tieneTurnoAdm=true;
										}else{
											if(Utiles.obtenerMinutosDiferencia(detTurno.get("h_inicio").toString(), detTurno.get("h_fin").toString())<0){
												minDia=Utiles.obtenerMinutosDiferencia(detTurno.get("h_inicio").toString(), "24:00:00")+Utiles.obtenerMinutosDiferencia("00:00:00", detTurno.get("h_fin").toString());
												minDia=minDia-(detTurno.get("min_refr")!=null?Integer.parseInt(detTurno.get("min_refr").toString()):0);
											}else{
												minDia= Utiles.obtenerMinutosDiferencia(detTurno.get("h_inicio").toString(), detTurno.get("h_fin").toString())-(detTurno.get("min_refr")!=null?Integer.parseInt(detTurno.get("min_refr").toString()):0);
											}
											tieneTurnoAdm=false;
										}
										horasDia=(int)(minDia/60);
										log.debug("Hora del dia:"+codPers+":"+fechaEvaluar+"-"+minDia);
											if(!diaEnLetra.equals("Lunes")){
												if(!fb.getMesletras().equals(mesEnLetra)){
													if(horasEq<horasAcumulado || (horasAcumulado==0 && horasEq==0)){
														tieneSinTurno=true;
														evalTrab+="<tr><td><a href='"+strURL+"'>"+codPers+"</a></td><td>"+nombresTrab+"</td><td>"+inicioSemana+" - "+Utiles.dameFechaAnterior(fechaEvaluar, 1)+"</td><td>"+mesEnLetra+"</td></tr>";
													}
													horasAcumulado=(int)horasDia;
													numContratoTemp=numContrato;
													accionTemp=accion;
													horasSemanalTemp=horasSemanal;
													inicioSemana=fechaEvaluar;
													mesEnLetra=fb.getMesletras();
													horasEq=0;
													contador=1;
													if(detTurno.get("oper_id").toString().trim().equals("0"))
														tieneTurnoAdm=true;
												}else{
													if(fechaEvaluar.equals(fechaFin)){
														if(horasEq<horasAcumulado || (horasAcumulado==0 && horasEq==0)){
															tieneSinTurno=true;
															evalTrab+="<tr><td><a href='"+strURL+"'>"+codPers+"</a></td><td>"+nombresTrab+"</td><td>"+inicioSemana+" - "+fechaEvaluar+"</td><td>"+mesEnLetra+"</td></tr>";
														}
													}else{
														if(numContrato.equals(numContratoTemp) || accion.equals("0002")){
															horasAcumulado+=(int)horasDia;
															contador++;
														}else{
															if(accion.equals("0003")){
																//ACA PROGRAMAR EL CORTE de contrato Y PRORRATEAR
																if(horasSemanal!=horasSemanalTemp){
																	
																}else{
																	horasAcumulado+=(int)horasDia;
																	contador++;
																}
															}
														}
													}
												}												
											}else{
												if(horasEq<horasAcumulado || (horasAcumulado==0 && horasEq==0)){
													tieneSinTurno=true;
													evalTrab+="<tr><td><a href='"+strURL+"'>"+codPers+"</a></td><td>"+nombresTrab+"</td><td>"+inicioSemana+" - "+Utiles.dameFechaAnterior(fechaEvaluar, 1)+"</td><td>"+mesEnLetra+"</td></tr>";
												}
												horasAcumulado=(int)horasDia;
												numContratoTemp=numContrato;
												accionTemp=accion;
												horasSemanalTemp=horasSemanal;
												inicioSemana=fechaEvaluar;
												contador=1;
												horasEq=0;
											}
									}else{
										if(diaEnLetra.equals("Lunes")){
											log.debug("HorasEqLunes "+codPers+" "+fechaEvaluar+":"+horasEq+"-"+horasAcumulado);
											//reseteamos los valores temporales para la nueva semana de <td>"+horasAcumulado+"</td> evaluacion <td>"+contador+"</td><td>"+horasEq+"</td> 
											if(horasEq<horasAcumulado || (horasAcumulado==0 && horasEq==0)){
												tieneSinTurno=true;
												//if(sinTurno)
												evalTrab+="<tr><td><a href='"+strURL+"'>"+codPers+"</a></td><td>"+nombresTrab+"</td><td>"+inicioSemana+" - "+Utiles.dameFechaAnterior(fechaEvaluar, 1)+"</td><td>"+mesEnLetra+"</td></tr>";
											}
											horasAcumulado=(int)horasDia;
											numContratoTemp=numContrato;
											accionTemp=accion;
											horasSemanalTemp=horasSemanal;
											inicioSemana=fechaEvaluar;
											contador=0;
											horasEq=0;
										}else{
											log.debug("HorasEq "+codPers+" "+fechaEvaluar+":"+horasEq+"-"+horasAcumulado);
											if(!fb.getMesletras().equals(mesEnLetra)){
												if(horasEq<horasAcumulado || (horasAcumulado==0 && horasEq==0)){
													tieneSinTurno=true;
													//if(sinTurno)
													evalTrab+="<tr><td><a href='"+strURL+"'>"+codPers+"</a></td><td>"+nombresTrab+"</td><td>"+inicioSemana+" - "+Utiles.dameFechaAnterior(fechaEvaluar, 1)+"</td><td>"+mesEnLetra+"</td></tr>";
												}
												horasAcumulado=(int)horasDia;
												numContratoTemp=numContrato;
												accionTemp=accion;
												horasSemanalTemp=horasSemanal;
												inicioSemana=fechaEvaluar;
												mesEnLetra=fb.getMesletras();
												horasEq=0;
												contador=0;
											}
											if(fechaEvaluar.equals(fechaFin)){
												if(horasEq<horasAcumulado || (horasAcumulado==0 && horasEq==0)){
													tieneSinTurno=true;
													//if(sinTurno)
													evalTrab+="<tr><td><a href='"+strURL+"'>"+codPers+"</a></td><td>"+nombresTrab+"</td><td>"+inicioSemana+" - "+fechaEvaluar+"</td><td>"+mesEnLetra+"</td></tr>";
												}
											}
										}
									}
									fechaEvaluar=Utiles.dameFechaSiguiente(fechaEvaluar,1);
								}
								if(!sinTurno){
									tieneSinTurno=true;
									//evalTrab+="<tr><td><a href='"+strURL+"'>"+codPers+"</a></td><td>"+nombresTrab+"</td><td>"+fechaInicio+" - "+fechaFin+"</td><td>"+meses+"</td></tr>";;
								}
							}else
							{
								if(log.isDebugEnabled()) log.debug("El trabajador no tiene contratos.");
							}
						}
						if(tieneSinTurno){
							//CREACIÓN DEL CORREO
								List listaTodasNotificaciones = t4562dao.findAllNotificacionesEnviadas();
								if (listaTodasNotificaciones == null ||  listaTodasNotificaciones.isEmpty()){
									num_notificacion = annoActual + Numero.format(new Integer(1), "000000");						
								}
								if(listaTodasNotificaciones != null && !listaTodasNotificaciones.isEmpty()){						
									mUltimaNotificacion=t4562dao.findUltimaNotificacionRegistrada();
									if (mUltimaNotificacion != null && !mUltimaNotificacion.isEmpty()){							
										numero_notificacion = (String)mUltimaNotificacion.get("num_notificacion");							
										anio = numero_notificacion.substring(0,4);							
										correlativo = Integer.parseInt(numero_notificacion.substring(4,10));							
										if(anio.equals(""+annoActual)){
											num_notificacion = anio + Numero.format(new Integer(correlativo+1), "000000");								
										}else{
											num_notificacion = annoActual + Numero.format(new Integer(1), "000000");								
										}
									}
								}
								if (log.isDebugEnabled()) log.debug("num_notificacion= " + num_notificacion);
								StringBuffer mensaje=null;
								Map mParams2=new HashMap();
								try{
									mensaje = new StringBuffer("<html><head><title>SIRH - ASISTENCIA</title><style>")
									.append(".T1 {font-style  : normal;font-size :10pt;color:")
									.append("#FFFFFF;background  : #336699;font-family : Tahoma,Verdana,Arial,Helvetica,sans-serif ; padding:4px;} table{border-collapse:collapse;}.T2 td,th{border: 1px solid #336699;padding: 2px 8px;}</style></head>")
									.append("<body><table width='100%' align='center' class='T1'><tr><td><center><strong>NOTIFICACI&Oacute;N DE TURNOS</strong></center></td></tr></table><br><br>")
									.append("<table>")
									.append("<tr><td class='membrete'><strong>Ticket:</strong></td></tr>")
									.append("<tr><td class='dato'>")										
									.append(num_notificacion)
									.append("</td></tr>")
									.append("<tr><td class='membrete'><strong>Fecha:</strong></td></tr>")
									.append("<tr><td class='dato'>")									
									.append(new FechaBean().getFormatDate("dd/MM/yyyy")) 																									
									.append("</td></tr>")
									.append("<tr><td class='membrete'><strong>Asunto:</strong></td></tr>")
									.append("<tr><td class='dato'>")
									.append("Alerta de trabajadores sin turno")
									.append("</td></tr>")
									.append("<tr><td>&nbsp;</td></tr>")
									.append("<tr><td class='membrete'><strong>Sr(a)(ita): </strong>")
									.append(codDir+" - "+dir_nombres)
									.append("</td></tr>")
									.append("<tr><td>&nbsp;</td></tr>")
									.append("<tr><td class='dato'>")
									.append("Estimado Directivo,<br>Se ha detectado que los siguientes trabajadores <strong>no cuentan con turno de trabajo</strong>, por lo que debe regularizar, informando a la División de Compensaciones o la Oficina de Soporte Administrativo, según corresponda.")
									.append("</td></tr>")
									.append("<tr><td>&nbsp;</td></tr>")
									.append("<tr><td><table class='T2'><thead class='T1'><th>Registro</th><th>Colaborador</th><th>Rango</th><th>Mes</th></thead><tbody>")
									.append(evalTrab) //TODAS LAS SOLICITUDES POR CADA DIRECTIVO
									.append("</tbody> </table></td></tr>")
									.append("<tr><td>&nbsp;</td></tr>")
									.append("<tr><td>Atentamente,</td></tr>")
									.append("<tr><td class='membrete'><strong>División de compensaciones</strong></td></tr>")
									.append("<tr><td class='membrete'><strong>Intendencia Nacional de Recursos Humanos</strong></td></tr>");
									mensaje.append("</table></body></html>");										
									if (log.isDebugEnabled()) log.debug("mensaje= " + mensaje.toString());																			
									Correo objCorreo = new Correo(mensaje.toString());									
									objCorreo.setServidor(propiedades.leePropiedad("servidor"));									
									objCorreo.setRemitente(propiedades.leePropiedad("correoRemitenteSolicitudes"),propiedades.leePropiedad("nombreRemitenteSolicitudes"));	
									objCorreo.agregarDestinatario(correoDAO.findCorreoByRegistro(codDir)); //el jefe o directivo
									//objCorreo.agregarDestinatario("epenac@sunat.gob.pe"); //Para pruebas en calidad
									objCorreo.setAsunto("Alerta de trabajadores sin turno");
									//objCorreo.enviarHtml();
									if (log.isDebugEnabled()) log.debug("Correo enviado al Directivo.");										
									boolean insercionExitosa=false;
									//REGISTRAR LAs NOTIFICACION
										mParams2.put("num_notificacion", num_notificacion);
										mParams2.put("cod_anno", ""+annoActual); //ano de la solicitud
										mParams2.put("num_solicitud", "0"); //numero de la solicitud										
										mParams2.put("cod_uuoo", unidad.get("t12cod_uorga").toString()); //unidad organizacional a la que pertenece el seguimiento - ICAPUNAY 08/08/2011
										mParams2.put("cod_pers",unidad.get("jefefinal").toString()); //Codigo de registro del trabajador que emitira la solicitud
										mParams2.put("num_seguim", "0"); //Numero de seguimiento de la solicitud.
										mParams2.put("tipo_solicitud", "00"); //      
										mParams2.put("fec_notificacion", fb_fecha.getSQLDate()); //Fecha actual de envio de la notificacion
										mParams2.put("cod_pers_notif", unidad.get("jefefinal").toString()); //Codigo de registro del directivo notificado										
										if (log.isDebugEnabled()) log.debug("mParams2= " + mParams2);
										insercionExitosa = t4562dao.insertNotificacionBySolicitudes(mParams2);								
											
										if (insercionExitosa==true)
											if (log.isDebugEnabled()) log.debug("Se inserto notificacion: "+ num_notificacion+" para el directivo: "+unidad.get("jefefinal").toString().trim());
								 } catch (CorreoException ce) {
									 		if (log.isDebugEnabled()) log.debug("Error en CorreoException: "+ce.toString());									 	
										 	if (log.isDebugEnabled()) log.debug("No se pudo enviar correo al trabajador con registro: " + codDir);
								 }
						}
					}
					}
					else{
						if(log.isDebugEnabled()) log.debug("Directivo con registro "+codDir+" no tine correo.");
					}
				}
			}
			}catch (DAOException d) {
				log.error("*** SQL Error ****",d);			
				StringBuffer msg = new StringBuffer().append("ERROR PROCESO AUTOMATICO DE ENVIAR ALERTAS POR SOLICITUDES VACACIONALES PENDIENTES DE APROBAR: ").append(d.toString());
	        	throw new FacadeException(this, msg.toString());
	        } catch (Exception e) {
				throw new FacadeException(this, e.toString());
			}
	}	
	
	//JVILLACORTA - 14/11/2011 - MODIFICA ALERTA DE SOLICITUDES
	/**
	 * Encargado de calcular y enviar alertas de movimientos de asistencia a trabajadores 
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * 
	 * @throws DAOException
	 * @throws RemoteException
	 */	
	public void enviarAlertaMovAsistencia() throws FacadeException, RemoteException{
		try {
			if(log.isDebugEnabled()) log.debug("method: enviarAlertaMovAsistencia");
			log.info("method: enviarAlertaMovAsistencia");
			DataSource dcsp = sl.getDataSource("java:comp/env/jdbc/dcsp"); //sl esta arriba
			DataSource dgsp = sl.getDataSource("java:comp/env/jdbc/dgsp");			
			
			T1454DAO t1454DAO = new T1454DAO(dcsp);
			T4563DAO t4563dao = new T4563DAO(dgsp);
			pe.gob.sunat.rrhh.dao.T02DAO personalDAO = new pe.gob.sunat.rrhh.dao.T02DAO(dcsp);
			CorreoDAO correoDAO = new CorreoDAO(dcsp);
			
			String mensajeSol = "";
			boolean insercionExitosa = false;
			Map mUltimaNotificacion = new HashMap();
			mUltimaNotificacion = null;
			String numero_notificacion = null;	
			String anio = null;
			int correlativo = 0;
			Map mParams = new HashMap();
			Map mParams2 = new HashMap(); 
			SimpleDateFormat sdfInvertido = new SimpleDateFormat("yyyy/MM/dd");
			SimpleDateFormat sdfDirecto = new SimpleDateFormat("dd/MM/yyyy");
			
			String num_notificacion = null;
			int inum_notificacion;
			FechaBean fec_actual = new FechaBean();
			Timestamp ahora = new Timestamp(System.currentTimeMillis());			//if (log.isDebugEnabled()) log.debug("ahora: "+ahora);
			FechaBean fb_fecha = new FechaBean(ahora);								//if (log.isDebugEnabled()) log.debug("fb_fecha: "+fb_fecha);
			String st_fecha = fb_fecha.getFormatDate("dd/MM/yyyy");					//if (log.isDebugEnabled()) log.debug("st_fecha: "+st_fecha);			
			String hoy = fec_actual.getFormatDate("dd/MM/yyyy");					//if (log.isDebugEnabled()) log.debug("hoy: "+hoy);
			
			String ayer3 = fec_actual.getOtraFecha(hoy, -3, Calendar.DATE);			//if (log.isDebugEnabled()) log.debug("ayer: "+ayer);
			
			String annoActual = st_fecha.substring(6,10);							//if (log.isDebugEnabled()) log.debug("annoActual: "+annoActual);
			String fanterior = fec_actual.getOtraFecha(hoy, -1, Calendar.MONTH);	//if (log.isDebugEnabled()) log.debug("fecha anterior: "+fanterior);
			String fposterior = fec_actual.getOtraFecha(hoy, 1, Calendar.MONTH);	//if (log.isDebugEnabled()) log.debug("fecha posterior: "+fposterior);
			String dia_hoy = hoy.substring(0,2);									//if (log.isDebugEnabled()) log.debug("dia hoy: "+dia_hoy);
			int idia_hoy = Integer.parseInt(dia_hoy);
						
			String periodo = "";
			String anhoperiodo = hoy.trim().substring(6,10);						//if (log.isDebugEnabled()) log.debug("anhoperiodo_actual: " + anhoperiodo); 
			String mesperiodo = hoy.trim().substring(3,5);							//if (log.isDebugEnabled()) log.debug("mesperiodo_actual: " + mesperiodo);
			periodo = anhoperiodo + mesperiodo;										//if (log.isDebugEnabled()) log.debug("periodo_actual: " + periodo);

		    String panterior = "";
			String anhoanterior = fanterior.trim().substring(6,10); //09/12/2011	//if (log.isDebugEnabled()) log.debug("anhoperiodo_anterior: " + anhoanterior);
			String mesanterior = fanterior.trim().substring(3,5);					//if (log.isDebugEnabled()) log.debug("mesperiodo_anterior: " + mesanterior);
			panterior = anhoanterior + mesanterior;									//if (log.isDebugEnabled()) log.debug("periodo_anterior: " + panterior);
		    
		    String pposterior = "";
			String anhoposterior = fposterior.trim().substring(6,10); //09/12/2011	//if (log.isDebugEnabled()) log.debug("anhoperiodo_posterior: " + anhoposterior);
			String mesposterior = fposterior.trim().substring(3,5);					//if (log.isDebugEnabled()) log.debug("mesperiodo_posterior: " + mesposterior);
			pposterior = anhoposterior + mesposterior;								//if (log.isDebugEnabled()) log.debug("periodo_posterior: " + pposterior);

			
			//1. obtenemos padron de trabajadores activos
            List trabajadores = personalDAO.buscarPersonalActivo_fromT02(mParams);		///*ICAPUNAY - PASE PAS20112A550001380 Optimizacion de consulta - Modificacion de alertas de solicitudes y movimientos de asistencia - 06/03/2012 */
			if (log.isDebugEnabled()) log.debug("trabajadores.size(): " + trabajadores.size());			
			
			if (trabajadores != null && trabajadores.size()>0){
				HashMap tparamsNom = new HashMap();
				HashMap tparamsCas = new HashMap();
				//HashMap tparamsMfr = new HashMap();
				String fec_ini_mf="";
				String fec_fin_mf="";
				String fec_ini_cas="";
				String fec_fin_cas="";
				String fec_ini_pla="";
				String fec_fin_pla="";
				
				//1. obtenemos periodos segun regimen laboral
				//1.1 modalidades formativas
		    	/*if(21<=idia_hoy && idia_hoy<=27) //21-27									
					tparamsMfr.put("periodo", periodo);
				else if((28<=idia_hoy && idia_hoy<=31))
					tparamsMfr.put("periodo", pposterior);
				else							
					tparamsMfr.put("periodo", periodo);
				List fechasIniPeriodoMfr = t1454DAO.findFchIniPeriodo(tparamsMfr);
				if (fechasIniPeriodoMfr != null && fechasIniPeriodoMfr.size()>0){
					for (int j=0; j<fechasIniPeriodoMfr.size(); j++) {
						Map fechaIniPeriodo = (HashMap)fechasIniPeriodoMfr.get(0);					//solo extraer el primero				
						fec_ini_mf = sdfDirecto.format(sdfInvertido.parse(fechaIniPeriodo.get("fec_ini_mf").toString().trim()));
						fec_fin_mf = sdfDirecto.format(sdfInvertido.parse(fechaIniPeriodo.get("fec_fin_mf").toString().trim()));
					}
				}*/
				
				//1.2 cas y formativas
				if(idia_hoy<=9)	//1-9								
					tparamsCas.put("periodo", periodo);
				else			//10+					
					tparamsCas.put("periodo", pposterior);
				
				List fechasIniPeriodoCas = t1454DAO.findFchIniPeriodo(tparamsCas);
				if (fechasIniPeriodoCas != null && fechasIniPeriodoCas.size()>0){
					for (int j=0; j<fechasIniPeriodoCas.size(); j++) {
						Map fechaIniPeriodo = (HashMap)fechasIniPeriodoCas.get(0);					//solo extraer el primero				
						fec_ini_cas = sdfDirecto.format(sdfInvertido.parse(fechaIniPeriodo.get("fec_ini_cas").toString().trim()));
						fec_fin_cas = sdfDirecto.format(sdfInvertido.parse(fechaIniPeriodo.get("fec_fin_cas").toString().trim()));
						
						fec_ini_mf = sdfDirecto.format(sdfInvertido.parse(fechaIniPeriodo.get("fec_ini_mf").toString().trim()));
						fec_fin_mf = sdfDirecto.format(sdfInvertido.parse(fechaIniPeriodo.get("fec_fin_mf").toString().trim()));
					}
				}
						
				//1.3 nombrados
				if(idia_hoy<=9)	//1-9									
					tparamsNom.put("periodo", panterior);
				else			//10+
					tparamsNom.put("periodo", periodo);
				
				List fechasIniPeriodoNom = t1454DAO.findFchIniPeriodo(tparamsNom);
				if (fechasIniPeriodoNom != null && fechasIniPeriodoNom.size()>0){
					for (int j=0; j<fechasIniPeriodoNom.size(); j++) {
						Map fechaIniPeriodo = (HashMap)fechasIniPeriodoNom.get(0);					//solo extraer el primero				
						fec_ini_pla = sdfDirecto.format(sdfInvertido.parse(fechaIniPeriodo.get("finicio").toString().trim()));
						fec_fin_pla = sdfDirecto.format(sdfInvertido.parse(fechaIniPeriodo.get("ffin").toString().trim()));
					}
				}
				
				//2. enlace correo
				ResourceBundle bundle = ResourceBundle.getBundle("pe.gob.sunat.sp.asistencia.sirh");
				String servidorIP = bundle.getString("servidorIP");
				String programa = bundle.getString("programa3");
				String url = new MenuCliente().generaInvocacionURL(programa,null,true,MenuCliente.INTRANET,"/ol-ti-iaasistencia/asisS11Alias");
				String strURL = "http://"+servidorIP+url;
				if (log.isDebugEnabled()) log.debug("strURL= " + strURL);	
				
				//x. número de notificación
				mUltimaNotificacion=t4563dao.findUltimaNotificacionRegistrada();
				if (mUltimaNotificacion != null && !mUltimaNotificacion.isEmpty()){							
					numero_notificacion = (String)mUltimaNotificacion.get("num_notificacion");							
					anio = numero_notificacion.substring(0,4);							
					correlativo = Integer.parseInt(numero_notificacion.substring(4,10));
					if(anio.equals(annoActual)){
						num_notificacion = anio + Numero.format(new Integer(correlativo+1), "000000");								
					}else{
						num_notificacion = annoActual + Numero.format(new Integer(1), "000000");								
					}
				}else{
					num_notificacion = annoActual + Numero.format(new Integer(1), "000000");
				}
				inum_notificacion = Integer.parseInt(num_notificacion);
				if (log.isDebugEnabled()) log.debug("num_notificacion= " + num_notificacion);
				
							
				//3. ciclo por todos los trabajadores
				for (int i=0; i<trabajadores.size(); i++) {										
					Map trabajador = (HashMap)trabajadores.get(i);
					if (trabajador.get("cod_pers")!= null && !trabajador.get("cod_pers").toString().trim().equals("")){
						try{
							if (log.isDebugEnabled()) log.debug("cod_pers: " + trabajador.get("cod_pers").toString().trim());
							
							String nreg = trabajador.get("cod_pers").toString().trim(); 		//JVILLACORTA 11/04/2012 Memo N° 00069 - 2012 - 4F3100
							//log.info("nreg "+i+": "+nreg);
							String nombre = ((String)trabajador.get("t02ap_pate")).trim() + " " + ((String)trabajador.get("t02ap_mate")).trim() + ", " + ((String)trabajador.get("t02nombres")).trim();
							String codRel = trabajador.get("t02cod_rel").toString().trim();
							StringBuffer sb_mensaje = new StringBuffer("");
							String mensajeF = "";
							
						    //boolean isFin = false;
							
						    //3.1 buscando rango de fechas para los movimientos
							if (codRel.equals("10")){
								/*if(idia_hoy<=9){
									trabajador.put("hoy", fec_fin_mf);
									isFin = true;
								}else*/
									trabajador.put("hoy", ayer3);
								trabajador.put("fanterior", fec_ini_mf);	
								
								mensajeSol = "Se remite reporte de calificaciones de asistencia efectuado por el sistema a partir de sus " +
								//"marcaciones realizadas en el periodo del" + " " + fec_ini_mf + " " + "al" + " " + (isFin?fec_fin_mf:ayer3) + ", " + 
								"marcaciones realizadas en el periodo del" + " " + fec_ini_mf + " " + "al" + " " + ayer3 + ", " +
								"a fin de facilitar la regularización de la información de asistencia, según corresponda, considerando el " +
								"Procedimiento de Control de Asistencia y Permanencia que puede consultar a través del siguiente enlace:" ;
								
							} else if (codRel.equals("09")){
								/*if(idia_hoy<=9){
									trabajador.put("hoy", fec_fin_cas);
									isFin = true;
								}else*/
									trabajador.put("hoy", ayer3);
								trabajador.put("fanterior", fec_ini_cas);
								
								mensajeSol = "Se remite reporte de calificaciones de asistencia efectuado por el sistema a partir de sus " +
								//"marcaciones realizadas en el periodo del" + " " + fec_ini_cas + " " + "al" + " " + (isFin?fec_fin_cas:ayer3) + ", " + 
								"marcaciones realizadas en el periodo del" + " " + fec_ini_cas + " " + "al" + " " + ayer3 + ", " +
								"a fin de facilitar la regularización de la información de asistencia, según corresponda, considerando el " +
								"Procedimiento de Control de Asistencia y Permanencia que puede consultar a través del siguiente enlace:" ;
								
							} else { 
								/*if(idia_hoy<=9){
									trabajador.put("hoy", fec_fin_pla);
									isFin = true;
								}else*/ 
									trabajador.put("hoy", ayer3);
								trabajador.put("fanterior", fec_ini_pla);	
								
								mensajeSol = "Se remite reporte de calificaciones de asistencia efectuado por el sistema a partir de sus " +
								//"marcaciones realizadas en el periodo del" + " " + fec_ini_pla + " " + "al" + " " + (isFin?fec_fin_pla:ayer3) + ", " + 
								"marcaciones realizadas en el periodo del" + " " + fec_ini_pla + " " + "al" + " " + ayer3 + ", " +
								"a fin de facilitar la regularización de la información de asistencia, según corresponda, considerando el " +
								"Procedimiento de Control de Asistencia y Permanencia que puede consultar a través del siguiente enlace:" ;
							}														
							
							List personal = t1454DAO.findMovimientoAsistenciaByTrab(trabajador);//movimientos
							if (log.isDebugEnabled()) log.debug("personal: " + personal);
										
							//3.2 configurando enlace
							sb_mensaje.append("<tr><td>&nbsp;</td></tr>")
							.append("<tr align='left' class='dato'><td align='center' style='background-color:#FFF;'><font face='Verdana' size='2'>")
							.append("<a href='http://intranet/intranet/inicio/recursoshumanos/asistenciaPlanillas/ri080-2015-8A0000.pdf'>")
							.append("http://intranet/intranet/inicio/recursoshumanos/asistenciaPlanillas/ri080-2015-8A0000.pdf</font></a></td>")
							.append("<tr><td>&nbsp;</td></tr>")
							.append("<tr><td style='background-color:#B6CBEB;'>")							
							.append(generaTablaDatosMovAsis(personal, strURL))
							.append("</td></tr><tr><td>&nbsp;</td></tr>");						
							mensajeF = mensajeSol + sb_mensaje.toString();
							if (log.isDebugEnabled()) log.debug("mensajeF= " + mensajeF);
									

							
							//3.4 envia un correo
							try{
								if(personal != null && personal.size()>0){
									if (log.isDebugEnabled()) log.debug("mParams= " + mParams);								
									mParams.put("num_notificacion", inum_notificacion+"");		//numero de la notificacion
									mParams.put("fec_notificacion", hoy); 					//Fecha actual de envio de la notificacion ---DATE
									String texto = Utiles.textoCorreoMovimientoAsistencia(mParams, nreg, nombre, mensajeF); //JVILLACORTA 11/04/2012 Memo N° 00069 - 2012 - 4F3100
									
									if (log.isDebugEnabled()) log.debug("texto final= " + texto);								
									Correo objCorreo = new Correo(texto);
									objCorreo.setServidor(propiedades.leePropiedad("servidor"));									
									objCorreo.setRemitente(propiedades.leePropiedad("correoRemitenteMovimientos"),propiedades.leePropiedad("nombreRemitenteMovimientos"));
									String correo = correoDAO.findCorreoByRegistro(trabajador.get("cod_pers").toString().trim());
									//log.info("correo: "+correo);
									objCorreo.agregarDestinatario(correo,nombre); //el trabajador
									objCorreo.setAsunto("Alerta de calificación de marcaciones");
									objCorreo.enviarHtml();
									if (log.isDebugEnabled()) log.debug("Se envio correo a trabajador: "+ trabajador.get("cod_pers").toString().trim());						
								}
							}catch(Exception ex){
								log.error("Error al enviar correo("+nreg+"): " + ex.getMessage(), ex);
							}						
							
							//3.5 insercion de notificación
							if(personal != null && personal.size()>0){
								if (log.isDebugEnabled()) log.debug("movimientos del trabajador: " + trabajador.get("cod_pers").toString().trim()+ " "+ personal);
								for(int j=0; j<personal.size(); j++) {										
									mParams2 = (HashMap)personal.get(j);
									mParams2.put("num_notificacion", inum_notificacion+"");					//numero de la notificacion
									mParams2.put("cod_pers", mParams2.get("cod_pers").toString()); 		//el registro del trabajador notificado								
									mParams2.put("num_periodo", mParams2.get("periodo").toString()); 	//el numero de periodo del movimiento
									mParams2.put("cod_mov", mParams2.get("mov").toString());			//el codigo del movimiento (tipo)
									mParams2.put("fec_ingreso", mParams2.get("fecha"));					//la fecha de ingreso del movimiento ----DATE
									mParams2.put("fec_notificacion", hoy); 								//Fecha actual de envio de la notificacion ---DATE										
									if (log.isDebugEnabled()) log.debug("mParams2= " + mParams2);
									insercionExitosa = t4563dao.insertNotificacionByMovAsistencia(mParams2);								
									
									if (insercionExitosa==true){
										if (log.isDebugEnabled()) log.debug("Se inserto notificacion: "+ inum_notificacion+" del N° movimiento: "+mParams2.get("mov").toString()+" con fecha: "+mParams2.get("fecha").toString()+ " para el trabajador: "+trabajador.get("cod_pers").toString().trim());										
									}									
								}
							}		
							inum_notificacion++;
						} catch (Exception ex) {
							log.error("Error al procesar trabajador("+trabajador.get("cod_pers")+"): " + ex.getMessage(), ex);									 	
						}							
					}										
				}
			}			
		} catch (DAOException d) {
			log.error("*** SQL Error ****",d);			
			StringBuffer msg = new StringBuffer().append(" ERROR PROCESO AUTOMATICO DE ENVIAR ALERTAS POR MOVIMIENTO DE ASISTENCIA: ").append(d.toString());
        	throw new FacadeException(this, msg.toString());
        } catch (Exception e) {
			throw new FacadeException(this, e.toString());
		}finally{
			log.info("fin method: enviarAlertaMovAsistencia");
		}
        
	} //FIN - JVILLACORTA - 14/11/2011 - MODIFICA ALERTA DE SOLICITUDES
	
	//JVILLACORTA - 03/11/2011 - MODIFICA ALERTA DE SOLICITUDES
	/**
	 * Genera una cadena que contiene una tabla html con los datos del Trabajador Solicitante
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 *
	 * @param dbean DynaBean 
	 * @param hm HashMap
	 * @return 
	 * @throws FacadeException
	 */
	public String generaTablaDatos(List lista, String strURL) throws FacadeException{
		StringBuffer cadenaTabla = new StringBuffer("");
		HashMap registro = null;
		
		try{			
			cadenaTabla.append("<table border='0' cellpadding='0' cellspacing='1'> <tr align='center'>")
			//.append("<th width='5%' nowrap><font face='Verdana' size='2'>N&uacute;mero</font></th>")
			//.append("<th width='5%' nowrap><font face='Verdana' size='2'>NUMERO</font></th>") //de solicitud //ICAPUNAY 09/08 aumentar el ancho del campo "numero"
			//.append("<th width='25%' ><font face='Verdana' size='2'>FECHA</font></th>") //ICAPUNAY 09/08 disminuir el ancho del campo "fecha"
			.append("<th width='10%' nowrap><font face='Verdana' size='2'>NUMERO</font></th>") //de solicitud //ICAPUNAY 09/08 aumentar el ancho del campo "numero"
			.append("<th width='20%' ><font face='Verdana' size='2'>FECHA</font></th>") //ICAPUNAY 09/08 disminuir el ancho del campo "fecha"
			.append("<th width='40%' ><font face='Verdana' size='2'>EMISOR</font></th>")			
			.append("<th width='30%' ><font face='Verdana' size='2'>TIPO</font></th>")
			.append("</tr>");

			for(int i=0;i<lista.size();i++){
				registro = (HashMap) lista.get(i);
				
				cadenaTabla.append("<tr align='left' class='dato'><td align='center' style='background-color:#FFF;'><font face='Verdana' size='2'>");			
				if (!strURL.equals("")) {
					cadenaTabla.append("<a href='")
					.append(strURL)
					.append("'>");
				}
				//cadenaTabla.append( (registro.get("numero")!=null) ? registro.get("numero").toString().trim():"") //ICAPUNAY 08/08 adicionar "anno" antes de numero
				cadenaTabla.append( (registro.get("anno")!=null) ? registro.get("anno").toString().trim()+"-":"") //ICAPUNAY 08/08 adicionar "anno" antes de numero
				.append( (registro.get("numero")!=null) ? registro.get("numero").toString().trim():"") //ICAPUNAY 08/08 adicionar "anio" antes de numero
				.append("</font></a></td><td align='center' style='background-color:#FFF;'><font face='Verdana' size='2'>")
				//.append( (registro.get("fecha_desc")!=null) ? ((String)registro.get("fecha_desc")).trim():"") //ICAPUNAY 08/08 borrar "hora" de fecha
				.append( (registro.get("fecha_desc")!=null) ? new FechaBean(((String)registro.get("fecha_desc")).trim(), "dd/MM/yyyy HH:mm:ss").getFormatDate("dd/MM/yyyy"):"")  //ICAPUNAY 08/08 borrar "hora" de fecha
				.append("</font></td><td style='background-color:#FFF;'><font face='Verdana' size='2'>")
				.append( (registro.get("nombreTrab")!=null) ? (registro.get("nombreTrab").toString().trim()):"")
				.append("</font></td><td align='center' style='background-color:#FFF;'><font face='Verdana' size='2'>")
				.append( (registro.get("descrip")!=null) ?(String)registro.get("descrip"):"")			
				.append("</font></td></tr>");
			}
			
			cadenaTabla.append("</table>");
			
		} catch(Exception e){
			log.error(e);
			throw new FacadeException(this,e.getMessage());
		} finally{
		}
		return cadenaTabla.toString(); 
	} //FIN - JVILLACORTA - 03/11/2011 - MODIFICA ALERTA DE SOLICITUDES
	
	//JVILLACORTA - 14/11/2011 - MODIFICA ALERTA DE SOLICITUDES
	/**
	 * Genera una cadena que contiene una tabla html con los datos de los movimientos de asistencia de trabajadores
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 *
	 * @param dbean DynaBean 
	 * @param hm HashMap
	 * @return 
	 * @throws FacadeException
	 */
	public String generaTablaDatosMovAsis(List lista, String strURL) throws FacadeException{
		StringBuffer cadenaTabla = new StringBuffer("");
		HashMap registro = null;
		
		try{			
			cadenaTabla.append("<table border='0' cellpadding='0' cellspacing='1'> <tr align='center'>")			
			.append("<th width='9%' nowrap><font face='Verdana' size='2'>FECHA</font></th>") //de solicitud
			//.append("<th width='25%' ><font face='Verdana' size='2'>INGRESO</font></th>")
			//.append("<th width='25%' ><font face='Verdana' size='2'>SALIDA</font></th>")			
			.append("<th width='30%' ><font face='Verdana' size='2'>TIPO</font></th>")
			.append("</tr>");

			for(int i=0;i<lista.size();i++){
				registro = (HashMap) lista.get(i);
				
				cadenaTabla.append("<tr align='left' class='dato'><td align='center' style='background-color:#FFF;'><font face='Verdana' size='2'>");				
				if (!strURL.equals("")) {
					cadenaTabla.append("<a href='")
					.append(strURL) 
					//.append(strURL + "&tiburona=" + registro.get("fecha_desc").toString().trim()) 
					.append("'>");
				}				
				cadenaTabla.append( (registro.get("fecha_desc")!=null) ? registro.get("fecha_desc").toString().trim():"")
				//.append("</font></a></td><td align='center' style='background-color:#FFF;'><font face='Verdana' size='2'>")
				.append("</a></font></td>")
				//.append( (registro.get("hing")!=null) ? (String)registro.get("hing"):"")
				//.append("<td style='background-color:#FFF;'><font face='Verdana' size='2'>")
				//.append( (registro.get("hsal")!=null) ? (registro.get("hsal").toString().trim()):"")
				//.append("</font></td><td align='center' style='background-color:#FFF;'><font face='Verdana' size='2'>")
				.append("<td align='center' style='background-color:#FFF;'><font face='Verdana' size='2'>")
				.append( (registro.get("descrip")!=null) ?(String)registro.get("descrip"):"")			
				.append("</font></td></tr>");
			}
			
			cadenaTabla.append("</table>");
			
		} catch(Exception e){
			log.error(e);
			throw new FacadeException(this,e.getMessage());
		} finally{
		}
		return cadenaTabla.toString(); 
	} //FIN - JVILLACORTA - 14/11/2011 - MODIFICA ALERTA DE SOLICITUDES		
	
	
	/* ICAPUNAY - PAS20165E230300096 - Alerta de Cambio de turno */
	/**
	 * Metodo encargado de enviar alertas de cambio de turno a trabajadores en forma automatica	 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @throws FacadeException
	 * @throws RemoteException
	 * @throws CreateException
	 */	
	public void enviarAlertaCambioTurno() throws FacadeException,RemoteException,CreateException{
				
		log.info("inicio metodo: enviarAlertaCambioTurnoo()");			
		DataSource dcsp = sl.getDataSource("java:comp/env/jdbc/dcsp");//datasource de lectura
		
		try {			
			SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
			FechaBean fecha_act = new FechaBean();//hallando fecha actual			
			String fechaAnterior = fecha_act.getFormatDate("dd/MM/yyyy");
			fechaAnterior = fecha_act.getOtraFecha(fechaAnterior, "yyyy-MM-dd", -1, Calendar.DAY_OF_YEAR );//fecha es un dia anterior a la fecha actual yyy/MM/dd
			
			CorreoDAO correoDAO = new CorreoDAO(dcsp);//tabla correos
			T1270DAO t1270dao = new T1270DAO(dcsp);//tabla t1270turnoperson
		
			Map mDatos = new HashMap();
			mDatos.put("estado",Constantes.ACTIVO);
			mDatos.put("fecCreaModif",formatoFecha.parse(fechaAnterior));
			mDatos.put("cod_tab",Constantes.CODTAB_ALERTA_CAMBIO_TURNO);
			mDatos.put("tip_desc",Constantes.T01DETALLE);
			mDatos.put("estado2",Constantes.ACTIVO);
			log.debug("mDatoss: " +mDatos);
			
			Map mColabTurno = new HashMap();
			String cod_pers="";
			String nomApell="";				
			String direCorreo="";
			
			String mensaje = "";
			StringBuffer sb_mensaje = new StringBuffer("");
			String mensajeI = "";
			String mensajeF = "";
			Map mParams = new HashMap();
			mParams.put("fec_alerta",new FechaBean().getFormatDate("dd/MM/yyyy"));//fecha envio alerta
			
     		List listaColab = t1270dao.findColaboradoresByTurnosByFechaCreaModif(mDatos);//listado de colaboradores activos con turnos (t99codigos,t99cod_tab='702') creados o modificados en fecha anterior a la fecha actual
			if (log.isDebugEnabled()) log.debug("listaColabTurnoss: " + listaColab);
			
			if(listaColab!=null && !listaColab.isEmpty()){
				try {
					//INICIO DE FOR
					if (log.isDebugEnabled()) log.debug("listaColabTurnos.size()= " + String.valueOf(listaColab.size()));
					for (int i = 0; i < listaColab.size(); i++) {
						sb_mensaje = new StringBuffer("");//add
						mensajeI = "";//add
						mensajeF = "";//add
						mColabTurno = (Map) listaColab.get(i);
						nomApell = (mColabTurno.get("t02ap_pate")!=null?mColabTurno.get("t02ap_pate").toString():"")+" "+(mColabTurno.get("t02ap_mate")!=null?mColabTurno.get("t02ap_mate").toString():"")+" "+(mColabTurno.get("t02nombres")!=null?mColabTurno.get("t02nombres").toString():"");
						cod_pers = mColabTurno.get("t02cod_pers")!=null?((String) mColabTurno.get("t02cod_pers")).trim():"";	
						mDatos.put("codPers",cod_pers);
						
						List turnos = t1270dao.findTurnosByCodPersByFechaCreaModif(mDatos);//listado de turnos por colaborador con turnos (t99codigos,t99cod_tab='702') creados o modificados en fecha anterior a la fecha actual
						if (log.isDebugEnabled()) log.debug("turnos: " + turnos);
						
						if (log.isDebugEnabled()) log.debug("mensajeI: " + mensajeI);
						sb_mensaje.append("<tr><td>&nbsp;</td></tr>")							
						.append("<tr><td style='background-color:#B6CBEB;'>")							
						.append(generaTablaAlertaCambioTurno(turnos))
						.append("</td></tr><tr><td>&nbsp;</td></tr>");									

						if (log.isDebugEnabled()) log.debug("sb_mensaje: " + sb_mensaje);							
						mensajeF = mensajeI + sb_mensaje.toString();
						if (log.isDebugEnabled()) log.debug("mensajeF: " + mensajeF);
						
						mParams.put("registro",cod_pers);
						if (log.isDebugEnabled()) log.debug("mParams: " + mParams);
						
						mensaje = textoCorreoAlertaCambioTurno(mParams,nomApell,mensajeF);//devuelve todo el mensajeF en html
						if (log.isDebugEnabled()) log.debug("mensaje formateado: " + mensaje);																			
						Correo objCorreo = new Correo(mensaje.toString());									
						objCorreo.setServidor(propiedades.leePropiedad("servidor"));									
						objCorreo.setRemitente(propiedades.leePropiedad("correoRemitenteMovimientos"),propiedades.leePropiedad("nombreRemitenteMovimientos"));
						objCorreo.agregarConCopia(propiedades.leePropiedad("correoReceptorAlertaCambioTurnos"));	
			
     					// VALIDANDO SI TRABAJADOR TIENE CORREO Y LOGIN						
						direCorreo=correoDAO.findCorreoByRegistro(cod_pers);												
						if (!direCorreo.equals("")) {						
							if (log.isDebugEnabled()) log.debug("direCorreo Trabajador= " + direCorreo);
							try{
								//trabajador tiene correo correcto, se debe enviar correo a trabajador con copia a rrhh (solo con copia a rrhh cuando correo de trabajador es erroneo)
								objCorreo.agregarDestinatario(direCorreo.trim());														
								objCorreo.setAsunto("Registro de turno de trabajador");
								objCorreo.enviarHtml();
								if (log.isDebugEnabled()) log.debug("Se envio correoo a trabajadorrr");	
															
							} catch (CorreoException ce) {
								//trabajador tiene correo erroneo, hay destinatario erroneo y se debe copiar a rrhh correo (No ingresa por aca)															
								objCorreo.setAsunto("Registro de turno de trabajador - Correo Erróneo");
								objCorreo.enviarHtml();
						 		if (log.isDebugEnabled()) log.debug("Error en CorreoExceptionn: "+ce.toString());									 	
							 	if (log.isDebugEnabled()) log.debug("SI tiene correo trabajadorrr: "+cod_pers+" pero correo erroneo-se envio a rrhh");									 										 	
							}											
						}//fin if tiene correo
						else {
							//trabajador no tiene correo, no hay destinatario y se debe copiar a rrhh correo																	
							objCorreo.setAsunto("Registro de turno de trabajador - No tiene Correo");
							objCorreo.enviarHtml();							
							if (log.isDebugEnabled()) log.debug("NO tiene correo trabajadorrr: " + cod_pers+"-se envio a rrhh");
						}
						
					}//fin for
				} catch (Exception ex) {
					if (log.isDebugEnabled()) log.debug("*** Exception ex - Error en enviarAlertaCambioTurno()****",ex);					
				}	
										
			}else{
				if (log.isDebugEnabled()) log.debug("No existen colaboradores activos con turnos (t99codigos,t99cod_tab='720') creados o modificados en fecha anterior a la fecha actual.");				
			}				
						
		} catch (DAOException d) {
			if (log.isDebugEnabled()) log.error("*** DAOException Final - SQL Error en enviarAlertaCambioTurno()****",d);
			StringBuffer msg = new StringBuffer().append("ERROR PROCESO AUTOMATICO DE ENVIAR ALERTAS POR CAMBIO DE TURNO: ").append(d.toString());
        	throw new FacadeException(this, msg.toString());
		} catch (Exception e) {
		    if (log.isDebugEnabled()) log.error("*** Exception Final - Error en enviarAlertaCambioTurno()****",e);
		    StringBuffer msg = new StringBuffer().append("ERROR2 PROCESO AUTOMATICO DE ENVIAR ALERTAS POR CAMBIO DE TURNO: ").append(e.toString());
	        throw new FacadeException(this, msg.toString());			
		}finally{
			log.info("fin metodo: enviarAlertaCambioTurnoo()");
		}
	}
	
	
	//ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
	/**
	 * Encargado de procesar clima laboral en un rango de fechas en forma automatico
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @throws FacadeException
	 * @throws DAOException
	 * @throws RemoteException
	 */	
	public void procesarClimaLaboral() throws FacadeException, RemoteException, CreateException {		
		try {
			AsistenciaFacadeHome asistenciaFacadeHome = (AsistenciaFacadeHome) sl.getRemoteHome(AsistenciaFacadeHome.JNDI_NAME,	AsistenciaFacadeHome.class);
			AsistenciaFacadeRemote asistenciaFacadeRemote;
			
			String dbpool = "jdbc/dgsp";
			DataSource ds = sl.getDataSource(dbpool);			
			T99DAO t99dao = new T99DAO(dbpool);
			T8167DAO t8167dao = new T8167DAO(dbpool);
			T1271DAO t1271dao = new T1271DAO(dbpool);
			CorreoDAO correoDAO = new CorreoDAO(dbpool);//tabla correos
			
			FechaBean fecha_act = new FechaBean();//hallando fecha actual			
			String fechaFin = fecha_act.getFormatDate("dd/MM/yyyy");
			fechaFin = fecha_act.getOtraFecha(fechaFin, -1, Calendar.DAY_OF_YEAR );//fecha fin es un dia anterior a la fecha actual
			log.debug("fechaFin: "+fechaFin);
			String criterio = "0";//generar asistencia por registro			
			String fechaIni = "";			
			
			ParamBean prmUsuRrhh = t99dao.buscar(new String []{"510"}, ds, "36");//parametro de fecha de inicio a procesar clima
			fechaIni=prmUsuRrhh.getDescripcion();
			log.debug("fechaIni: "+fechaIni);
			
			ParamBean prmUsuRrhh2 = t99dao.buscar(new String []{"510"}, ds, "15");//parametro de usuario quien ejecuta asistencia
			log.debug("prmUsuRrhh2 : nro_registro :"+prmUsuRrhh2.getDescripcion()+", usuario:"+prmUsuRrhh2.getDescripcionCorta());
			
			/** definiendo los parametros **/
			HashMap mapa = new HashMap();
			mapa.put("dbpool", dbpool);			
			mapa.put("criterio", criterio);
			mapa.put("valor", "");
			mapa.put("codPers", prmUsuRrhh2.getDescripcion());//regitro es 6589
			mapa.put("usuario", prmUsuRrhh2.getDescripcionCorta());//usuario es CLAMA
		
			/** OBTENIENDO LISTADO DE NOVEDADES A GENERAR ASISTENCIA **/			
			Map prms=new HashMap();			
			prms.put("fec_aut", fechaIni);
			prms.put("fec_aut1", fechaFin);
			prms.put("ind_aut", Constantes.INACTIVO);//0			
			log.debug("prms: "+prms);
			List listaProcesar=  t8167dao.findAutorizaByFechasByEstado(prms);//OBTENEMOS LA LISTA A PROCESAR
			log.debug("listaProcesar2: "+listaProcesar);
			
			Map fila = new HashMap();//mapa de unidad organizacional		
			String cod_pers="";
								
			for (int i = 0; i < listaProcesar.size(); i++) {				
				fila = (Map)listaProcesar.get(i);				
				log.debug("fila: "+fila);
				cod_pers = fila.get("cod_pers")!=null?((String) fila.get("cod_pers")).trim():"";
				//cod_auto = fila.get("cod_aut")!=null?((String) fila.get("cod_aut")).trim():"";
				try{
					prms=new HashMap();
					
					mapa.put("fechaIni", fila.get("fec_aut_desc").toString());//es para el mismo dia, no existe rango de dias al momento de generar asistencia 
					mapa.put("fechaFin", fila.get("fec_aut_desc").toString());
					mapa.put("observacion", "Registro de asistencia del " + fila.get("fec_aut_desc").toString() + " al " + fila.get("fec_aut_desc").toString());
					
					mapa.put("valor", cod_pers);//Registro A PROCESAR
					asistenciaFacadeRemote = asistenciaFacadeHome.create();	
					
					/** CALIFICANDO CLIMA LABORAL **/
					if(log.isDebugEnabled()) log.debug("CALIFICANDO CLIMA LABORAL PARA mapa: "+mapa);
					asistenciaFacadeRemote.generarRegistros(mapa, (String)mapa.get("usuario")); //"generar asistencia" es el metodo que califica clima laboral en refrigerio								
					
				}catch(Exception ex){					
					log.error("***Exception ex - Error en procesarClimaLaboral()****: " + ex.getMessage() + " colaborador: " + fila);			
				}
			}		
		} catch (DAOException d) {
			if (log.isDebugEnabled()) log.error("*** DAOException Final - SQL Error en procesarClimaLaboral()****",d);
			StringBuffer msg = new StringBuffer().append("ERROR PROCESO AUTOMATICO DE CLIMA LABORAL: ").append(d.toString());
        	throw new FacadeException(this, msg.toString());
        } catch (Exception e) {
        	if (log.isDebugEnabled()) log.error("*** Exception Final - Error en procesarClimaLaboral()****",e);
		    StringBuffer msg = new StringBuffer().append("ERROR PROCESO AUTOMATICO DE CLIMA LABORAL: ").append(e.toString());
	        throw new FacadeException(this, msg.toString());			
		}finally{
			log.info("fin metodo: procesarClimaLaboral()");
		}
	}
	//FIN ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
	
	//ICAPUNAY - PAS20171U230300074 - Mejoras vacaciones
	/**
	 * Metodo encargado de enviar automaticamente alertas a directivos con relacion adjunta de colaboradores con programacion de vacaciones	 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @throws FacadeException
	 * @throws RemoteException
	 * @throws CreateException
	 */	
	public void enviarAlertaConProgramacionVacaciones() throws FacadeException,RemoteException,CreateException{
		
		log.info("inicio metodo: enviarAlertaConProgramacionVacaciones()");			
		DataSource dcsp = sl.getDataSource("java:comp/env/jdbc/dcsp");//datasource de lectura
		DataSource dgsp = sl.getDataSource("java:comp/env/jdbc/dgsp");//datasource de escritura
		DataSource dsSecuence= sl.getDataSource("java:/comp/env/jdbc/dcbdseq");
		String dbpool = "jdbc/dcsp";
		T99DAO t99dao = new T99DAO(dbpool);
		Timestamp ahora = new Timestamp(System.currentTimeMillis());
		ReporteFacadeHome reporteFacadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,ReporteFacadeHome.class);
		ReporteFacadeRemote reporteFacadeRemote = reporteFacadeHome.create();
		
		//ENVIO A RRHH EXCEL RESUMEN DE NOTIFICACIONES ENVIADAS
		ParamBean prmUsuRrhh = t99dao.buscar(new String []{"510"}, dcsp, "40");//parametro de fecha de ejecucion de alerta a directivos con programacion de trabajadores
		String fechaEjecucion=prmUsuRrhh.getDescripcion().trim();
		//log.debug("fechaEjecucion: "+fechaEjecucion);
		Map parametros = new HashMap();		
		parametros.put("tipoNotif",Constantes.ACTIVO);
		parametros.put("fechaNotif",new FechaBean(ahora).getFormatDate("dd/MM/yyyy"));				
		//FIN DE ENVIO
				
		try {			
			
			
			CorreoDAO correoDAO = new CorreoDAO(dcsp);//tabla correos			
			T12DAO t12dao = new T12DAO(dcsp);//tabla t12uorga
			T1282DAO t1282dao = new T1282DAO(dcsp);
			T9036DAO t9036dao = new T9036DAO(dgsp); //notificaciones a directivos
			T9036DAO t9036daoLectura = new T9036DAO(dcsp); //notificaciones a directivos
			T1456DAO t1456dao = new T1456DAO(dbpool);
			pe.gob.sunat.sp.dao.T99DAO codigoDAO = new pe.gob.sunat.sp.dao.T99DAO();
			pe.gob.sunat.rrhh.asistencia.dao.T1272DAO t1272dao = new pe.gob.sunat.rrhh.asistencia.dao.T1272DAO(dcsp);
			
			pe.gob.sunat.sp.dao.T12DAO t12spdao = new pe.gob.sunat.sp.dao.T12DAO();
			HashMap aux = new HashMap();
		
			String nombreArchivo="";
			Map mUnidad = new HashMap();
			Map colab = new HashMap();
			Map colab2 = new HashMap();
			String nombreJefe = "";
			String nombreDeleg = "";
			String codUnidad ="";
			String codUnidadColab =""; //new
			String jefeUO ="";
			String delegadoUO ="";
			String correojefeUO ="";
			String correodelegadoUO ="";
			HashMap mapDelegado = new HashMap();
			File[] listaArchivos=new File[1];// solo se adjuntara 1 archivo
			Map programacion = new HashMap();
			String registro="";
			String apenom="";
			String fecIngreso="";
			String unidad="";
			String periodo="";
			String fechaIni="";
			String fechaFin="";
			//SimpleDateFormat sdfformatoArchivo = new SimpleDateFormat("yyyyMMddHHmm");			
			String des_notif="";
			int num_trabaj=0;
			Map mcolab = new HashMap();
			String mcodpers="";
			Map datosIns = new HashMap();
			boolean inserto=false;	
			int Secuencia = 0;
			
			String mensaje = "";
			String mensaje2 = "";		
			Map mParams = new HashMap();
			mParams.put("fec_alerta",new FechaBean().getFormatDate("dd/MM/yyyy"));//fecha envio alerta
			
			List colabConProgramFinal = new ArrayList(); //new
			List colaboradoresUnidad = new ArrayList();
			Map trab = null;
			Map mregistro = null;
			String regis ="";
			String regisAnte ="";
			String regimen ="";
			String periodoVac="";
			String fechaIniProy = "";
			String fIniProy = "";
			String fFinProy = "";
			FechaBean fbIng = null;
			int yTrab =0;
			int yTrabAnte =0;
			int yIng =0;
			int diasLaborables=0;
			int diasLicEnferm=0;
			String fecha = "";
			BeanFechaHora bfh2 = null;
			Map mapTrabajador = new HashMap();
			String diasLab = codigoDAO.findParamByCodTabCodigo(dbpool,Constantes.CODTAB_PARAMETROS_ASISTENCIA,Constantes.MIN_DIAS_LABORABLES);			
			int minDiasLab = diasLab != null ? Integer.parseInt(diasLab) : 0;
			//if(log.isDebugEnabled()) log.debug("minDiasLab: "+minDiasLab);
			
			String diasEnfermedad = codigoDAO.findParamByCodTabCodigo(dbpool,Constantes.CODTAB_PARAMETROS_ASISTENCIA,Constantes.DIAS_ENFERMEDAD);
			int numDiasEnfermedad = diasEnfermedad != null ? Integer.parseInt(diasEnfermedad) : 0;
			//if(log.isDebugEnabled()) log.debug("numDiasEnfermedad: "+numDiasEnfermedad);
			
					
			List listaUnidadesActivas = t12dao.findByEstado(Constantes.ACTIVO);//listado de unidades activas ordenadas (t12cod_uorga)	
					
						
			if(listaUnidadesActivas!=null && !listaUnidadesActivas.isEmpty()){
				try {
					//INICIO DE FOR					
					for (int i = 0; i < listaUnidadesActivas.size(); i++) {
						colabConProgramFinal=null; //new
						colabConProgramFinal= new ArrayList(); //new
						colaboradoresUnidad=null;
						colaboradoresUnidad=new ArrayList();
						des_notif="";
						mUnidad = (Map) listaUnidadesActivas.get(i);
						if (log.isDebugEnabled()) log.debug("mUnidad("+i+"): " + mUnidad);
						codUnidad = mUnidad.get("t12cod_uorga")!=null?mUnidad.get("t12cod_uorga").toString().trim():"";						
						datosIns.put("cod_pers_jefe","");
						//obteniendo el jefe de la uo  
						jefeUO = t12spdao.findJefeByUO(dbpool,codUnidad);						
						jefeUO = jefeUO!=null?jefeUO.trim():"";
						//if (log.isDebugEnabled()) log.debug("jefeUO: " + jefeUO);
						//correo del jefe de la uo
						correojefeUO=correoDAO.findCorreoByRegistro(jefeUO);
						//nombre del jefe
						colab = t9036daoLectura.findTrabajadorByRegistro(jefeUO);
						nombreJefe = (colab!=null && !colab.isEmpty())?colab.get("apenom").toString().trim():"";
						//if (log.isDebugEnabled()) log.debug("nombreJefe final: " + nombreJefe);
						//
						
						//obteniendo el delegado de la uo											
						aux.put("dbpool",dbpool);
						aux.put("codUO",codUnidad);
						aux.put("codOpcion",Constantes.DELEGA_SOLICITUDES);						
						mapDelegado = t12spdao.findDelegado(aux);
						delegadoUO = (mapDelegado!=null && !mapDelegado.isEmpty())?((String)mapDelegado.get("t02cod_pers")).trim():"";	
						if (log.isDebugEnabled()) log.debug("delegadoUO: " + delegadoUO);
						//correo del delegado de la uo	
						correodelegadoUO=correoDAO.findCorreoByRegistro(delegadoUO);
						//nombre del delegado
						colab2 = t9036daoLectura.findTrabajadorByRegistro(delegadoUO);
						nombreDeleg = (colab2!=null && !colab2.isEmpty())?colab2.get("apenom").toString().trim():"";
						//if (log.isDebugEnabled()) log.debug("nombreDeleg final: " + nombreDeleg);
						//
						String sanio="";
						String smes="";
						//FechaBean fbfecha = new FechaBean(fechaEjecucion,"dd/MM/yyyy");						
						FechaBean fbfecha = new FechaBean();
						sanio= fbfecha.getAnho();						
						smes= fbfecha.getMes();						
						int imes = Integer.parseInt(smes);
						int ianio = Integer.parseInt(sanio);						
						if (imes==12){ //diciembre							
							ianio=ianio+1;
							imes=1;
							sanio=String.valueOf(ianio);
							smes=String.valueOf(imes);
						}else{							
							smes=String.valueOf(imes+1);
						}						
						//List colabConProgramacion = t1282dao.findVacacionesProgramadasByAnioMes(sanio,smes,findUuooJefe(codUnidad)+"%",Constantes.CODREL_FORMATIVA);
						List colabConProgramacion = t1282dao.findVacacionesProgramadasByAnioMes(sanio,smes,codUnidad,Constantes.CODREL_FORMATIVA);
						if (log.isDebugEnabled()) log.debug("colabConProgramacion: " + colabConProgramacion);
					
						//File excelCreado=null;			
						if (colabConProgramacion!=null && !colabConProgramacion.isEmpty()){
							
							for (int a=0;a<colabConProgramacion.size();a++) {
								mregistro = (HashMap)colabConProgramacion.get(a);
								if (log.isDebugEnabled()) log.debug("mregistro("+a+"): " + mregistro);
								regis=mregistro.get("registro")!=null?((String)mregistro.get("registro")).trim():"";
								periodoVac=mregistro.get("periodo")!=null?((String)mregistro.get("periodo")).trim():"";
								trab = t1456dao.findTrabajadorByFingFrepuesto(regis);
								regisAnte = (trab.get("t02cod_ante")!=null?trab.get("t02cod_ante").toString().trim():"");
								regimen = (trab.get("t02cod_rel")!=null?trab.get("t02cod_rel").toString().trim():"");
								if (log.isDebugEnabled()) log.debug("trab("+a+"): " + trab);
								trab.put("fecha", trab.get("f_repuesto")!= null?trab.get("f_repuesto").toString():trab.get("f_ingsun")!=null?trab.get("f_ingsun").toString():"");
								if (trab.get("fecha")!=null){
									fecha = trab.get("fecha").toString().trim();
									bfh2 = new BeanFechaHora(fecha,"yyyy-MM-dd");
									mregistro.put("fechaingreso_desc",bfh2.getFormatDate("dd/MM/yyyy"));									
								}								
								if (!regimen.equals(Constantes.CODREL_REG1057)){//No CAS
									yTrab = Integer.parseInt(periodoVac); //new
									yTrabAnte = yTrab - 1;
														
									fecha =trab.get("fecha")!=null?trab.get("fecha").toString().trim():"";
									bfh2 = new BeanFechaHora(fecha,"yyyy-MM-dd");
									yIng = Integer.parseInt(bfh2.getFormatDate("yyyy"));
																		
									fechaIniProy = yTrabAnte + fecha.substring(4, 10);
									fbIng = new FechaBean(fechaIniProy, "yyyy-MM-dd");
									fIniProy = fbIng.getFormatDate("yyyy/MM/dd");
									fFinProy = yTrab + fIniProy.substring(4, 10);
																		
									mapTrabajador.put("codPers", regis);
									mapTrabajador.put("finicio", fIniProy);
									mapTrabajador.put("ffin", fFinProy);
									mapTrabajador.put("codPersAnte", regisAnte);
																		
									//dias laborables
									diasLaborables = t1272dao.findDiasLaborablesByFiniFin(mapTrabajador);								
									
									//dias licencia enfermedad
									diasLicEnferm = t1272dao.findDiasEnfermedadByFiniFin(mapTrabajador);								
									
									//decrementamos el exceso de dias con licencia por enfermedad
									diasLaborables -= (diasLicEnferm>numDiasEnfermedad ? (diasLicEnferm-numDiasEnfermedad) : 0);
									if (log.isDebugEnabled()) log.debug("diasLaborables final: "+diasLaborables);
									
									if ( (diasLaborables >= minDiasLab) && ( yIng < yTrab )){
										colabConProgramFinal.add(mregistro);										
									}
								}
								else{//si es CAS
									colabConProgramFinal.add(mregistro);									
								}
							}							
						}//fin if colabConProgramacion	
						
						if (log.isDebugEnabled()) log.debug("colabConProgramFinal: "+colabConProgramFinal);
						File excelCreado=null;
						if (colabConProgramFinal!=null && !colabConProgramFinal.isEmpty()){	
							//excel
							nombreArchivo="Colaboradores con programacion"; //nombreArchivo="Colaboradores con programacion"+sdfformatoArchivo.format(ahora).toString();
							
							FileOutputStream fileOut = new FileOutputStream(Constantes.RUTA_LOG_REPORTES + nombreArchivo + ".xlsx");
							XSSFWorkbook workbook = new XSSFWorkbook();
							XSSFSheet hoja1=workbook.createSheet();
							XSSFRow fila=hoja1.createRow((short) 0);
							fila.createCell((short) 0).setCellValue(new XSSFRichTextString("REGISTRO"));
							fila.createCell((short) 1).setCellValue(new XSSFRichTextString("APELLIDOS Y NOMBRES"));
							fila.createCell((short) 2).setCellValue(new XSSFRichTextString("FECHA DE INGRESO"));
							fila.createCell((short) 3).setCellValue(new XSSFRichTextString("UNIDAD ORGANIZACIONAL"));
							fila.createCell((short) 4).setCellValue(new XSSFRichTextString("PERIODO VACACIONAL"));
							fila.createCell((short) 5).setCellValue(new XSSFRichTextString("FECHA INICIO DE VACACION"));
							fila.createCell((short) 6).setCellValue(new XSSFRichTextString("FECHA FIN DE VACACION"));
							XSSFRow nfila = null;
							int m =0;				
												
							for (int n = 0; n <= colabConProgramFinal.size()-1; n++) {					
								nfila = hoja1.createRow((short) m + 1);
								programacion=null;					
								programacion=(HashMap)colabConProgramFinal.get(n);					
								registro=programacion.get("registro")!=null?(String)programacion.get("registro"):"";
								apenom=programacion.get("apenom")!=null?(String)programacion.get("apenom"):"";
								fecIngreso=programacion.get("fechaingreso_desc")!=null?(String)programacion.get("fechaingreso_desc"):"";
								unidad=programacion.get("desuo")!=null?(String)programacion.get("desuo"):"";
								periodo=programacion.get("periodo")!=null?(String)programacion.get("periodo"):"";
								fechaIni=programacion.get("fechainicio_desc")!=null?(String)programacion.get("fechainicio_desc"):"";
								fechaFin=programacion.get("fechafin_desc")!=null?(String)programacion.get("fechafin_desc"):"";
								codUnidadColab=programacion.get("coduo")!=null?((String)programacion.get("coduo")).trim():"";
								
								if (codUnidadColab.equals(codUnidad)){									
									colaboradoresUnidad.add(programacion);									
								}
								
								nfila.createCell((short) 0).setCellValue(new XSSFRichTextString(registro));
								nfila.createCell((short) 1).setCellValue(new XSSFRichTextString(apenom));
								nfila.createCell((short) 2).setCellValue(new XSSFRichTextString(fecIngreso));
								nfila.createCell((short) 3).setCellValue(new XSSFRichTextString(unidad));
								nfila.createCell((short) 4).setCellValue(new XSSFRichTextString(periodo));
								nfila.createCell((short) 5).setCellValue(new XSSFRichTextString(fechaIni));
								nfila.createCell((short) 6).setCellValue(new XSSFRichTextString(fechaFin));
								m++;				
							}//fin for colab con programacion
							m=m+4;
							nfila = hoja1.createRow((short) m);
							nfila.createCell((short) 0).setCellValue(new XSSFRichTextString("Total: "));
							nfila.createCell((short) 1).setCellValue(new XSSFRichTextString(colabConProgramFinal.size()+" trabajador(es)"));
							//ancho columnas
							XSSFRow row = workbook.getSheetAt(0).getRow(0);
							for(int colNum = 0; colNum<row.getLastCellNum();colNum++)   
				               workbook.getSheetAt(0).autoSizeColumn(colNum);
							//
						    workbook.write(fileOut);
						    fileOut.flush();
							fileOut.close();				
							
							excelCreado=reporteFacadeRemote.registraReporte(nombreArchivo,null, "scheduler"); //se obtiene excel zipeado
							
							if (excelCreado.exists()){//excel zipeado
								listaArchivos[0]=excelCreado;				
							}
							//fin excel
							
							if (colaboradoresUnidad!=null && !colaboradoresUnidad.isEmpty()){
								if (log.isDebugEnabled()) log.debug("colaboradoresUnidad final: " + colaboradoresUnidad);
								num_trabaj=colaboradoresUnidad!=null && !colaboradoresUnidad.isEmpty()?colaboradoresUnidad.size():0;
								for (int x = 0; x <= colaboradoresUnidad.size()-1; x++) {								
									mcolab=(HashMap)colaboradoresUnidad.get(x);
									mcodpers=mcolab.get("registro")!=null?((String)mcolab.get("registro")).trim():"";									
									if (x==num_trabaj-1){
									   des_notif=des_notif+mcodpers;
									}else{
									   des_notif=des_notif+mcodpers+"|";
									}									
								}
							}else{								
								num_trabaj=colabConProgramFinal!=null && !colabConProgramFinal.isEmpty()?colabConProgramFinal.size():0;								
								des_notif="";
							}		
							datosIns.put("cod_uorg_notif",codUnidad);
							datosIns.put("fec_envio_notif",ahora);
							datosIns.put("cod_user_crea","BATCH");
							datosIns.put("fec_creacion",ahora);
							datosIns.put("ind_tip_notif",Constantes.ACTIVO);
							datosIns.put("num_trabaj",String.valueOf(num_trabaj));
							datosIns.put("des_notif",des_notif);	
							
							// VALIDANDO SI JEFE TIENE CORREO																		
							if (!correojefeUO.equals("")) {						
								try{									
									Secuencia = new SequenceDAO().getSequence(dsSecuence,"SET9036");									
									datosIns.put("cod_pers_jefe",jefeUO.trim());					
									datosIns.put("num_seqdir", String.valueOf(Secuencia));
									inserto=t9036dao.insertNotificDirectivoProgramaciones(datosIns);
									if (inserto==true){
										log.debug("SI se inserto notif a jefe de unidad: "+codUnidad);
										mParams.put("registro",jefeUO.trim());
										mParams.put("nombre",nombreJefe);
										mParams.put("ticket",String.valueOf(Secuencia));										
										mensaje = textoCorreoAlertaDirectivoConProgramaciones(mParams);//devuelve todo el mensaje en html
										Correo objCorreo = new Correo(mensaje.toString());									
										objCorreo.setServidor(propiedades.leePropiedad("servidor"));									
										objCorreo.setRemitente(propiedades.leePropiedad("correoReceptorAlertasVacaciones"),propiedades.leePropiedad("nombreReceptorAlertasVacaciones"));						
										objCorreo.agregarDestinatario(correojefeUO.trim());														
										objCorreo.setAsunto("Alerta de colaboradores con programación de vacaciones");
										if(listaArchivos!=null && listaArchivos.length>0){
											objCorreo.setAdjuntos(listaArchivos);		
										}	
										objCorreo.enviarHtml();
										if (log.isDebugEnabled()) log.debug("Se envio correo a jefe");
									}else{
										log.debug("NO se inserto notif a jefe de unidad: "+codUnidad);
									}																
								} catch (CorreoException ce) {
									if (log.isDebugEnabled()) log.debug("Error en CorreoException: "+ce.toString());									 	
								 	if (log.isDebugEnabled()) log.debug("alerta directivo con programacion-No se pudo enviar correo al jefe: " + jefeUO);									 										 	
								}											
							}//fin correo
							// VALIDANDO SI DELEGADO TIENE CORREO																		
							if (!correodelegadoUO.equals("")) {						
								try{
									Secuencia = new SequenceDAO().getSequence(dsSecuence,"SET9036");									
									datosIns.put("cod_pers_jefe",delegadoUO.trim());					
									datosIns.put("num_seqdir", String.valueOf(Secuencia));
									inserto=t9036dao.insertNotificDirectivoProgramaciones(datosIns);
									if (inserto==true){
										log.debug("SI se inserto notif a delegado de unidad: "+codUnidad);
										mParams.put("registro",delegadoUO.trim());
										mParams.put("nombre",nombreDeleg);
										mParams.put("ticket",String.valueOf(Secuencia));										
										mensaje2 = textoCorreoAlertaDirectivoConProgramaciones(mParams);//devuelve todo el mensaje en html
										Correo objCorreo2 = new Correo(mensaje2.toString());									
										objCorreo2.setServidor(propiedades.leePropiedad("servidor"));									
										objCorreo2.setRemitente(propiedades.leePropiedad("correoReceptorAlertasVacaciones"),propiedades.leePropiedad("nombreReceptorAlertasVacaciones"));
										objCorreo2.agregarDestinatario(correodelegadoUO.trim());														
										objCorreo2.setAsunto("Alerta de colaboradores con programación de vacaciones");
										if(listaArchivos!=null && listaArchivos.length>0){
											objCorreo2.setAdjuntos(listaArchivos);		
										}	
										objCorreo2.enviarHtml();
										if (log.isDebugEnabled()) log.debug("Se envio correo a delegado");
									}else{
										log.debug("NO se inserto notif a jefe de unidad: "+codUnidad);
									}															
								} catch (CorreoException ce) {
									if (log.isDebugEnabled()) log.debug("Error en CorreoException: "+ce.toString());									 	
								 	if (log.isDebugEnabled()) log.debug("alerta directivo con programacion-No se pudo enviar correo al delegado: " + delegadoUO);									 										 	
								}											
							}//fin correo
							
						}//fin colabConProgramFinal	
						//}
					
					}//fin for					
					enviarCorreosRRHH_ResumenNotificacionesConProgramacion(dcsp,parametros);
					
				} catch (Exception ex) {
					if (log.isDebugEnabled()) log.debug("*** Exception ex - Error en enviarAlertaConProgramacionVacaciones()****",ex);
					enviarCorreosRRHH_ResumenNotificacionesConProgramacion(dcsp,parametros);
				}										
			}			
						
		} catch (DAOException d) {
			if (log.isDebugEnabled()) log.error("*** DAOException Final - Error en enviarAlertaConProgramacionVacaciones()****",d);
			enviarCorreosRRHH_ResumenNotificacionesConProgramacion(dcsp,parametros);
			StringBuffer msg = new StringBuffer().append("ERROR PROCESO AUTOMATICO DE ENVIAR ALERTAS A DIRECTIVOS CON PROGRAMACION VACACIONES DE COLABORADORES: ").append(d.toString());
        	throw new FacadeException(this, msg.toString());
		} catch (Exception e) {
		    if (log.isDebugEnabled()) log.error("*** Exception Final - Error en enviarAlertaConProgramacionVacaciones()****",e);
		    enviarCorreosRRHH_ResumenNotificacionesConProgramacion(dcsp,parametros);
		    StringBuffer msg = new StringBuffer().append("ERROR2 PROCESO AUTOMATICO DE ENVIAR ALERTAS A DIRECTIVOS CON PROGRAMACION VACACIONES DE COLABORADORES: ").append(e.toString());
	        throw new FacadeException(this, msg.toString());			
		} finally{
			log.info("fin metodo: enviarAlertaConProgramacionVacaciones()");
		}
	}
	
	/**
	 * Metodo que envia correos con archivo zipeado excel de resumen de notificaciones enviadas a directivos en una fecha
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"	
	 * @param dgsp DataSource	
	 * @param mapaDatos Map	
	 * @throws FacadeException
	 * @throws RemoteException
	 * @throws CreateException
	 */	
	public void enviarCorreosRRHH_ResumenNotificacionesConProgramacion(DataSource dcsp,Map mapaDatos) throws FacadeException,RemoteException, CreateException{
		
		log.debug("ingreso enviarCorreosRRHH_ResumenNotificacionesConProgramacion");
		StringBuffer mensajeReporte=null;		
		File[] listaArchivos=new File[1];// solo se adjuntara 1 archivo
		List notificacionesEnviadas = null;		
		String dbpool = "jdbc/dcsp";		
		
		String nombreArchivo="";
		Map notificacion = new HashMap();								
		ReporteFacadeHome reporteFacadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,ReporteFacadeHome.class);
		ReporteFacadeRemote reporteFacadeRemote = reporteFacadeHome.create();
		
		T9036DAO t9036dao = new T9036DAO(dcsp);//tabla t9036notifvacdir	
		T99DAO t99dao = new T99DAO(dbpool);
						
		String fechaNotif = (String)mapaDatos.get("fechaNotif");
		String registro="";
		String apenom="";
		String unidad="";
		String cantTrabaj="";
		String fechaNotifi="";
		
		if (log.isDebugEnabled()) log.debug("mapaDatos: "+mapaDatos);
		
		try {			
			ParamBean prmCorreoRrhh = t99dao.buscar(new String []{"510"}, dcsp, "43");//primer correo para enviar a rrhh
			ParamBean prmCorreoRrhh2 = t99dao.buscar(new String []{"510"}, dcsp, "44");//primer correo para enviar a rrhh
			String correo1rrhh=prmCorreoRrhh!=null?prmCorreoRrhh.getDescripcion().trim():"";
			String correo2rrhh=prmCorreoRrhh2!=null?prmCorreoRrhh2.getDescripcion().trim():"";
			
			notificacionesEnviadas=t9036dao.findNotificacionesByFecha(mapaDatos);
			if (log.isDebugEnabled()) log.debug("notificacionesEnviadas: "+notificacionesEnviadas);
			
			File excelCreado=null;			
			if (notificacionesEnviadas!=null && !notificacionesEnviadas.isEmpty()){					
				//nombreArchivo="directivosNotificadosConProgramacion"+sdfformatoArchivo.format(ahora).toString();
				nombreArchivo="directivosNotificadosConProgramacion";
				
				FileOutputStream fileOut = new FileOutputStream(Constantes.RUTA_LOG_REPORTES + nombreArchivo + ".xlsx");
				XSSFWorkbook workbook = new XSSFWorkbook();
				XSSFSheet hoja1=workbook.createSheet();
				XSSFRow fila=hoja1.createRow((int) 0);
				fila.createCell((int) 0).setCellValue(new XSSFRichTextString("REG. DIRECTIVO"));
				fila.createCell((int) 1).setCellValue(new XSSFRichTextString("APELLIDOS Y NOMBRES DIRECTIVO"));
				fila.createCell((int) 2).setCellValue(new XSSFRichTextString("UNIDAD"));
				fila.createCell((int) 3).setCellValue(new XSSFRichTextString("CANT. TRABAJADORES"));
				fila.createCell((int) 4).setCellValue(new XSSFRichTextString("FECHA NOTIFICACION"));
				XSSFRow nfila = null;
				int m =0;				
									
				for (int i = 0; i <= notificacionesEnviadas.size()-1; i++) {					
					nfila = hoja1.createRow((int) m + 1);
					notificacion=null;					
					notificacion=(HashMap)notificacionesEnviadas.get(i);					
					registro=notificacion.get("regdirec")!=null?(String)notificacion.get("regdirec"):"";
					apenom=notificacion.get("apenomdirec")!=null?(String)notificacion.get("apenomdirec"):"";
					unidad=notificacion.get("desuo")!=null?(String)notificacion.get("desuo"):"";
					cantTrabaj=String.valueOf((Integer)notificacion.get("num_trabaj"));
					fechaNotifi=notificacion.get("fecnotiffinal")!=null?(String)notificacion.get("fecnotiffinal_desc"):"";
					nfila.createCell((int) 0).setCellValue(new XSSFRichTextString(registro));
					nfila.createCell((int) 1).setCellValue(new XSSFRichTextString(apenom));
					nfila.createCell((int) 2).setCellValue(new XSSFRichTextString(unidad));
					nfila.createCell((int) 3).setCellValue(new XSSFRichTextString(cantTrabaj));
					nfila.createCell((int) 4).setCellValue(new XSSFRichTextString(fechaNotifi));					
					m++;				
				}				
				//ancho columnas
				XSSFRow row = workbook.getSheetAt(0).getRow(0);
				for(int colNum = 0; colNum<row.getLastCellNum();colNum++)   
	               workbook.getSheetAt(0).autoSizeColumn(colNum);
				//
				//response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");		
			    //response.setHeader("Content-Disposition", "attachment;filename=" + nombreArchivo);
			    workbook.write(fileOut);
			    fileOut.flush();
				fileOut.close();				
				
				excelCreado=reporteFacadeRemote.registraReporte(nombreArchivo, mapaDatos, "scheduler"); //se obtiene excel zipeado
				
				if (excelCreado.exists()){//excel zipeado					
					try{					
						listaArchivos[0]=excelCreado;						
						//ENVIO DE CORREO CON EXCEL RESUMEN ADJUNTO
						mensajeReporte = new StringBuffer("<html><head><title>SIRH-ASISTENCIA</title><style>")
						.append(".T1 {font-style  : normal;text-transform : uppercase;font-size :10pt;color:")
						.append("#FFFFFF;background  : #336699;font-family : Tahoma,Verdana,Arial,Helvetica,sans-serif ;}</style></head>")
						.append("<body><table width='100%' align='center' class='T1'><tr><td><center><strong>SIRH-ASISTENCIA</strong></center></td></tr></table><br><br>")
						.append("<table>")		
						.append("<tr><td class='membrete'><strong>Fecha:</strong></td></tr>")
						.append("<tr><td class='dato'>")
						.append(new FechaBean().getFormatDate("dd/MM/yyyy"))
						.append("</td></tr>")			
						.append("<tr><td class='membrete'><strong>Detalle:</strong></td></tr>")
						.append("<tr><td class='dato'>")
						.append("Se adjunta excel resumen de notificaciones enviadas a directivos referente a trabajadores con programación vacacional, correspondientes al "+new FechaBean().getFormatDate("dd/MM/yyyy")+".")
						.append("</td></tr>");		
						mensajeReporte.append("</table></body></html>");
											
						Correo objCorreoReporte = new Correo(mensajeReporte.toString());							
						objCorreoReporte.setServidor(propiedades.leePropiedad("servidor"));
						objCorreoReporte.setRemitente(propiedades.leePropiedad("correoReceptorAlertasVacaciones"),propiedades.leePropiedad("nombreReceptorAlertasVacaciones"));
						objCorreoReporte.agregarDestinatario(correo1rrhh);
						objCorreoReporte.agregarConCopia(correo2rrhh);
						objCorreoReporte.setAsunto("Envio resumen de notificaciones a directivos con programacion de trabajadores - enviada el "+new FechaBean().getFormatDate("dd/MM/yyyy"));
						if(listaArchivos!=null && listaArchivos.length>0){
							objCorreoReporte.setAdjuntos(listaArchivos);		
						}									
						objCorreoReporte.enviarHtml();						
						if (log.isDebugEnabled()) log.debug("Se envio correo con excel resumen de notificaciones.");
						
						reporteFacadeRemote.eliminaArchivosTemporales(nombreArchivo); //xlsx y zip
						//FIN DE ENVIO					
						
					} catch (Exception e) {
						if (log.isDebugEnabled()) log.debug("Exception Inicial enviarCorreosRRHH_ResumenNotificacionesConProgramacion");
						throw new Exception("No se pudo enviar correo con archivo resumen de notificaciones de vacaciones");						
					}						
				}else{
					if (log.isDebugEnabled()) log.debug("Archivo Resumen de notificaciones enviadas no existe.");					
				}					
			}else{
				if (log.isDebugEnabled()) log.debug("No se enviaron notificaciones en la fecha: "+fechaNotif);					
			}	

		} catch (CreateException e) {
			log.debug("CreateException Final en enviarCorreosRRHH_ResumenNotificacionesConProgramacion");
			log.error(e.getMessage());			
		} catch (Exception e) {
			log.debug("Exception Final en enviarCorreosRRHH_ResumenNotificacionesConProgramacion");			
			log.error(e.getMessage());			
		} 
		
	}
	
	/**
	 * Metodo encargado de enviar automaticamente alertas a directivos con relacion adjunta de colaboradores sin programacion de vacaciones para periodo vigente respecto a una fecha de corte	 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @throws FacadeException
	 * @throws RemoteException
	 * @throws CreateException
	 */	
	public void enviarAlertaSinProgramacionVacaciones() throws FacadeException,RemoteException,CreateException{
		
		log.info("inicio metodo: enviarAlertaSinProgramacionVacaciones()");			
		DataSource dcsp = sl.getDataSource("java:comp/env/jdbc/dcsp");//datasource de lectura
		DataSource dgsp = sl.getDataSource("java:comp/env/jdbc/dgsp");//datasource de escritura
		DataSource dsSecuence= sl.getDataSource("java:/comp/env/jdbc/dcbdseq");
		String dbpool = "jdbc/dcsp";
		T99DAO t99dao = new T99DAO(dbpool);
		Timestamp ahora = new Timestamp(System.currentTimeMillis());
		ReporteFacadeHome reporteFacadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,ReporteFacadeHome.class);
		ReporteFacadeRemote reporteFacadeRemote = reporteFacadeHome.create();
		
		VacacionFacadeHome facadeHome = (VacacionFacadeHome) sl.getRemoteHome(VacacionFacadeHome.JNDI_NAME,VacacionFacadeHome.class);
	    VacacionFacadeRemote vacacion = facadeHome.create();
	    List colabSinProgramacionRRHH = new ArrayList();
		
			
		//ENVIO A RRHH EXCEL RESUMEN DE NOTIFICACIONES ENVIADAS
		ParamBean prmUsuRrhh = t99dao.buscar(new String []{"510"}, dcsp, "41");//parametro de fecha de ejecucion de alerta a directivos sin programacion de trabajadores
		String fechaEjecucion=prmUsuRrhh.getDescripcion().trim();
		log.debug("fechaEjecucion: "+fechaEjecucion);
		Map parametros = new HashMap();		
		parametros.put("tipoNotif","2"); //2=alerta a directivos con relacion de trabajadores SIN programacion de vacaciones
		parametros.put("fechaNotif",new FechaBean(ahora).getFormatDate("dd/MM/yyyy"));				
		//FIN DE ENVIO
				
		try {			
			//SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
			
			CorreoDAO correoDAO = new CorreoDAO(dcsp);//tabla correos	
			T12DAO t12dao = new T12DAO(dcsp);//tabla t12uorga
			//pe.gob.sunat.rrhh.dao.T02DAO t02dao = new pe.gob.sunat.rrhh.dao.T02DAO(dcsp);
			T1281DAO t1281dao = new T1281DAO(dcsp);
			T1282DAO t1282dao = new T1282DAO(dcsp);
			T9036DAO t9036dao = new T9036DAO(dgsp); //notificaciones a directivos
			T9036DAO t9036daoLectura = new T9036DAO(dcsp); //notificaciones a directivos
			pe.gob.sunat.sp.asistencia.dao.T1456DAO t1456dao = new pe.gob.sunat.sp.asistencia.dao.T1456DAO();
			
			pe.gob.sunat.sp.dao.T12DAO t12spdao = new pe.gob.sunat.sp.dao.T12DAO();
			HashMap aux = new HashMap();
		
			String nombreArchivo="";
			Map mUnidad = new HashMap();
			Map colab = new HashMap();
			Map colab2 = new HashMap();
			String nombreJefe = "";
			String nombreDeleg = "";
			String codUnidad ="";
			String codUnidadColab ="";
			String jefeUO ="";
			String delegadoUO ="";
			String correojefeUO ="";
			String correodelegadoUO ="";
			HashMap mapDelegado = new HashMap();
			File[] listaArchivos=new File[1];// solo se adjuntara 1 archivo
			Map programacion = new HashMap();
			String registro="";
			String apenom="";
			String fecIngreso="";
			String unidad="";
			String periodo="";	
			String periodoSgte="";
			String des_notif="";
			int num_trabaj=0;
			Map mcolab = new HashMap();
			String mcodpers="";
			Map datosIns = new HashMap();
			Map datos = new HashMap();
			boolean inserto=false;	
			int Secuencia = 0;
			List colabSinProgramacion = new ArrayList();			
			List colaboradoresUnidad = new ArrayList();
			
			
			Map mregistro = null;			
			String fecha_ingreso="";
	    	String fecha_gen="";
	    	String saldoAct="0";
	    	int periodo_Act = 0;
	    	FechaBean fechabeanHoy = new FechaBean();
	    	FechaBean fechabeanGen;
	    	FechaBean fechabeanIngreso;
	    	String codPersona="";
	    		    	
	    	String periodo_Act_vac="";	 
	    	String periodo_Sig_vac="";	
			
			String mensaje = "";
			String mensaje2 = "";		
			Map mParams = new HashMap();
			mParams.put("fec_alerta",new FechaBean().getFormatDate("dd/MM/yyyy"));//fecha envio alerta
			
			Map params= new HashMap();//saldos general de colaboradores por anio
			params.put("anno",new Integer(2007));
	    	params.put("saldominimo",new Integer(0));
	    	List listaSaldos = t1281dao.findSaldos(params);
			
			ParamBean prmUsuRrhh3 = t99dao.buscar(new String []{"510"}, dcsp, "42");//parametro de fecha de corte (sera usada para la fecha de ingreso de un colaborador)
			String fechaCorte=prmUsuRrhh3.getDescripcion().trim();
			log.debug("fechaCorte: "+fechaCorte);
			datos.put("fechaIngreso",fechaCorte);//fecha de ingreso es fecha de corte
			/*datos.put("tipo",Constantes.VACACION_PROGRAMADA);//49
			datos.put("estado",Constantes.PROG_ACEPTADA);//1*/
			datos.put("estado",Constantes.ACTIVO);//1
			datos.put("noregimen",Constantes.CODREL_FORMATIVA);
			
			
			List listaUnidadesActivas = t12dao.findByEstado(Constantes.ACTIVO);//listado de unidades activas ordenadas (t12cod_uorga)	
			/*List listaUnidadesActivas = new ArrayList();
			Map xunidad = new HashMap();
			Map xunidad1 = new HashMap();
			Map xunidad2 = new HashMap();
			Map xunidad3 = new HashMap();
			//Map xunidad4 = new HashMap();
			xunidad.put("t12cod_uorga", "1U2300");
			xunidad1.put("t12cod_uorga", "1U2301");
			xunidad2.put("t12cod_uorga", "1U2302");
			xunidad3.put("t12cod_uorga", "1U2303");
			//xunidad4.put("t12cod_uorga", "1U0000");
			listaUnidadesActivas.add(xunidad);
			listaUnidadesActivas.add(xunidad1);
			listaUnidadesActivas.add(xunidad2);
			listaUnidadesActivas.add(xunidad3);
			//listaUnidadesActivas.add(xunidad4);*/
						
			if(listaUnidadesActivas!=null && !listaUnidadesActivas.isEmpty()){
				try {
					//INICIO DE FOR					
					for (int i = 0; i < listaUnidadesActivas.size(); i++) {
						colabSinProgramacion=null;
						colabSinProgramacion= new ArrayList();							
						colaboradoresUnidad=null;
						colaboradoresUnidad=new ArrayList();
						fecha_ingreso="";
						des_notif="";
						mUnidad = (Map) listaUnidadesActivas.get(i);
						log.debug("mUnidad("+i+"): " + mUnidad);
						codUnidad = mUnidad.get("t12cod_uorga")!=null?mUnidad.get("t12cod_uorga").toString().trim():"";						
						//datos.put("unidad",findUuooJefe(codUnidad));
						datos.put("unidad",codUnidad);
						
						datosIns.put("cod_pers_jefe","");
						//obteniendo el jefe de la uo  
						jefeUO = t12spdao.findJefeByUO(dbpool,codUnidad);					
						jefeUO = jefeUO!=null?jefeUO.trim():"";
						//if (log.isDebugEnabled()) log.debug("jefeUO: " + jefeUO);
						//correo del jefe de la uo
						correojefeUO=correoDAO.findCorreoByRegistro(jefeUO);
						//nombre del jefe
						colab = t9036daoLectura.findTrabajadorByRegistro(jefeUO);
						nombreJefe = (colab!=null && !colab.isEmpty())?colab.get("apenom").toString().trim():"";
						//if (log.isDebugEnabled()) log.debug("nombreJefe sp: " + nombreJefe);
						//
						
						//obteniendo el delegado de la uo											
						aux.put("dbpool",dbpool);
						aux.put("codUO",codUnidad);
						aux.put("codOpcion",Constantes.DELEGA_SOLICITUDES);						
						mapDelegado = t12spdao.findDelegado(aux);
						delegadoUO = (mapDelegado!=null && !mapDelegado.isEmpty())?((String)mapDelegado.get("t02cod_pers")).trim():"";	
						//if (log.isDebugEnabled()) log.debug("delegadoUO: " + delegadoUO);
						//correo del delegado de la uo	
						correodelegadoUO=correoDAO.findCorreoByRegistro(delegadoUO);
						//nombre del delegado
						colab2 = t9036daoLectura.findTrabajadorByRegistro(delegadoUO);
						nombreDeleg = (colab2!=null && !colab2.isEmpty())?colab2.get("apenom").toString().trim():"";
						//if (log.isDebugEnabled()) log.debug("nombreDeleg sp: " + nombreDeleg);
						
						List colaboradoresSP = t9036daoLectura.findTrabajadoresByEstadoUnidad(datos);
						if (log.isDebugEnabled()) log.debug("colaboradoresSP final (T9036DAO): " + colaboradoresSP);
						
						if (colaboradoresSP!=null && !colaboradoresSP.isEmpty()){	
							//inicio colabSinProgramacion
							for (int a=0;a<colaboradoresSP.size();a++) {
								mregistro = (HashMap)colaboradoresSP.get(a);
								//fecha_ingreso = mregistro.get("t02f_ingsun")!=null?mregistro.get("t02f_ingsun_desc").toString().trim():"";
								codPersona = mregistro.get("t02cod_pers")!=null?mregistro.get("t02cod_pers").toString().trim():"";
								
								Map hmVacGen = t1456dao.joinWithT02ByCodPersEstId(dbpool, codPersona);
								if (log.isDebugEnabled()) log.debug("hmVacGen (t1456dao): " + hmVacGen);
								if (hmVacGen!=null && hmVacGen.get("fecha")!=null && !hmVacGen.get("fecha").toString().equals("")){
						    		fecha_ingreso=hmVacGen.get("fecha").toString();
						    	} else {
						    		fecha_ingreso=mregistro.get("t02f_ingsun")!=null?mregistro.get("t02f_ingsun_desc").toString().trim():"";
						    	}
								mregistro.put("t02f_ingsun_desc",fecha_ingreso);
								if (log.isDebugEnabled()) log.debug("fecha_ingreso (final): " + fecha_ingreso);
								if (!fecha_ingreso.equals("")){
				    				fecha_gen= new FechaBean(fecha_ingreso).getDia().concat("/").concat(new FechaBean(fecha_ingreso).getMes()).concat("/").concat(fechabeanHoy.getAnho());
						        	fechabeanGen = new FechaBean(fecha_gen);
						        	fechabeanIngreso = new FechaBean(fecha_ingreso);
						        	if (fechabeanHoy.getCalendar().before(fechabeanGen.getCalendar())){//fechabeanHoy=05/01/2018 {22/12/2017} - fechabeanGen=01/12/2018 {01/12/2017}
						        		periodo_Act = Integer.parseInt(fechabeanHoy.getAnho())-1;//2017 {}
						        		if (Integer.parseInt(fechabeanIngreso.getAnho())==periodo_Act){
						        			periodo_Act = periodo_Act +1;
						        		}
						      		} else {//fechabeanHoy=10/12/2018 {22/12/2017}   fechabeanGen=01/12/2018 {01/12/2017}
						      			periodo_Act = Integer.parseInt(fechabeanHoy.getAnho());//2018  {2017}
						      			if (Integer.parseInt(fechabeanIngreso.getAnho())==periodo_Act){
						        			periodo_Act = periodo_Act +1;
						        		}							      			
						      		}
						        	periodo_Act_vac = String.valueOf(periodo_Act);
						        	periodo_Sig_vac = String.valueOf(periodo_Act+1);
						        	if (log.isDebugEnabled()) log.debug("periodo_Act_vac: " + periodo_Act_vac);
						        	if (log.isDebugEnabled()) log.debug("periodo_Sig_vac: " + periodo_Sig_vac);
				    			}  
								
								List vacaciones = t1282dao.findVacacionesByCodPersAnio(codPersona,periodo_Act_vac);
								//if (log.isDebugEnabled()) log.debug("vacaciones (T1282DAO): " + vacaciones);
								
								List vacacionesSgte = t1282dao.findVacacionesByCodPersAnio(codPersona,periodo_Sig_vac);
								//if (log.isDebugEnabled()) log.debug("vacacionesSgte (T1282DAO): " + vacacionesSgte);
								
								if (vacaciones==null || vacaciones.isEmpty()){//sin programacion periodo actual
									if (log.isDebugEnabled()) log.debug("vacaciones==null o vacio - registro: " + codPersona + " - anio: "+periodo_Act_vac);
									mregistro.put("periodo_Act_vac",periodo_Act_vac);
									if (listaSaldos!=null){						      				
					      		        saldoAct = retornaSaldo(listaSaldos,codPersona,periodo_Act_vac);
					      			}
					      			mregistro.put("saldoAct",saldoAct);
					      			
					      			if (vacacionesSgte==null || vacacionesSgte.isEmpty()){//sin programacion periodo sgte
										if (log.isDebugEnabled()) log.debug("vacacionesSgte==null o vacio - registro: " + codPersona + " - anio: "+periodo_Sig_vac);
										mregistro.put("periodo_Sig_vac",periodo_Sig_vac);									
									}
					      			//new
					      			else{
					      				mregistro.put("periodo_Sig_vac","PROGRAMADO");//new
									}
					      			//fin new
									colabSinProgramacion.add(mregistro);
									//if (log.isDebugEnabled()) log.debug("colabSinProgramacion1: " + colabSinProgramacion);
									colabSinProgramacionRRHH.add(mregistro);									
								}else{
									mregistro.put("periodo_Act_vac","PROGRAMADO");//new
									if (vacacionesSgte==null || vacacionesSgte.isEmpty()){//sin programacion periodo sgte
										if (log.isDebugEnabled()) log.debug("vacacionesSgte==null o vacio - registro: " + codPersona + " - anio: "+periodo_Sig_vac);
										mregistro.put("periodo_Sig_vac",periodo_Sig_vac);
										colabSinProgramacion.add(mregistro);
										//if (log.isDebugEnabled()) log.debug("colabSinProgramacion2: " + colabSinProgramacion);
										colabSinProgramacionRRHH.add(mregistro);
									}									
								}
					    	}//fin for colaboradoresSP						
							if (log.isDebugEnabled()) log.debug("colabSinProgramacion finallllll: " + colabSinProgramacion);
						}//fin if colaboradoresSP					
					
						File excelCreado=null;			
						if (colabSinProgramacion!=null && !colabSinProgramacion.isEmpty()){						
							
							//excel
							nombreArchivo="Colaboradores sin programacion";
							
							FileOutputStream fileOut = new FileOutputStream(Constantes.RUTA_LOG_REPORTES + nombreArchivo + ".xlsx");
							XSSFWorkbook workbook = new XSSFWorkbook();
							XSSFSheet hoja1=workbook.createSheet();
							XSSFRow fila=hoja1.createRow((short) 0);
							fila.createCell((short) 0).setCellValue(new XSSFRichTextString("REGISTRO"));
							fila.createCell((short) 1).setCellValue(new XSSFRichTextString("APELLIDOS Y NOMBRES"));
							fila.createCell((short) 2).setCellValue(new XSSFRichTextString("FECHA DE INGRESO"));
							fila.createCell((short) 3).setCellValue(new XSSFRichTextString("UNIDAD ORGANIZACIONAL"));
							fila.createCell((short) 4).setCellValue(new XSSFRichTextString("PERIODO VACACIONAL ACTUAL"));	
							fila.createCell((short) 5).setCellValue(new XSSFRichTextString("PERIODO VACACIONAL SIGUIENTE"));
							XSSFRow nfila = null;
							int m =0;				
												
							for (int n = 0; n <= colabSinProgramacion.size()-1; n++) {					
								nfila = hoja1.createRow((short) m + 1);
								programacion=null;					
								programacion=(HashMap)colabSinProgramacion.get(n);
								//if (log.isDebugEnabled()) log.debug("programacion: " + programacion);
								
								registro=programacion.get("t02cod_pers")!=null?(String)programacion.get("t02cod_pers"):"";
								apenom=programacion.get("apenom")!=null?(String)programacion.get("apenom"):"";
								fecIngreso=programacion.get("t02f_ingsun_desc")!=null?(String)programacion.get("t02f_ingsun_desc"):"";
								unidad=programacion.get("coduo")!=null?(String)programacion.get("desuo"):"";
								codUnidadColab=programacion.get("coduo")!=null?((String)programacion.get("coduo")).trim():"";								
								//periodo=programacion.get("periodo_Act_vac")!=null?((String)programacion.get("periodo_Act_vac")+" ( "+(String)programacion.get("saldoAct")+" ) "):"";
								periodo=programacion.get("periodo_Act_vac")!=null?((String)programacion.get("periodo_Act_vac")).equals("PROGRAMADO")?(String)programacion.get("periodo_Act_vac"):((String)programacion.get("periodo_Act_vac")+" ( "+(String)programacion.get("saldoAct")+" ) "):"";
								periodoSgte=programacion.get("periodo_Sig_vac")!=null?(String)programacion.get("periodo_Sig_vac"):"";
								
								if (codUnidadColab.equals(codUnidad)){
									if (log.isDebugEnabled()) log.debug("codUnidadColab igual codUnidad");
									colaboradoresUnidad.add(programacion);
									//if (log.isDebugEnabled()) log.debug("colaboradoresUnidad: "+colaboradoresUnidad);
								}
								nfila.createCell((short) 0).setCellValue(new XSSFRichTextString(registro));
								nfila.createCell((short) 1).setCellValue(new XSSFRichTextString(apenom));
								nfila.createCell((short) 2).setCellValue(new XSSFRichTextString(fecIngreso));
								nfila.createCell((short) 3).setCellValue(new XSSFRichTextString(unidad));
								nfila.createCell((short) 4).setCellValue(new XSSFRichTextString(periodo));
								nfila.createCell((short) 5).setCellValue(new XSSFRichTextString(periodoSgte));
								m++;				
							}//fin for colab sin programacion
							m=m+4;
							nfila = hoja1.createRow((short) m);
							nfila.createCell((short) 0).setCellValue(new XSSFRichTextString("Total: "));
							nfila.createCell((short) 1).setCellValue(new XSSFRichTextString(colabSinProgramacion.size()+" trabajador(es)"));
							//ancho columnas
							XSSFRow row = workbook.getSheetAt(0).getRow(0);
							for(int colNum = 0; colNum<row.getLastCellNum();colNum++)   
				               workbook.getSheetAt(0).autoSizeColumn(colNum);
							//
						    workbook.write(fileOut);
						    fileOut.flush();
							fileOut.close();				
							
							excelCreado=reporteFacadeRemote.registraReporte(nombreArchivo,null, "scheduler"); //se obtiene excel zipeado
							
							if (excelCreado.exists()){//excel zipeado
								listaArchivos[0]=excelCreado;				
							}
							//fin excel
							
							if (colaboradoresUnidad!=null && !colaboradoresUnidad.isEmpty()){
								if (log.isDebugEnabled()) log.debug("colaboradoresUnidad final: " + colaboradoresUnidad);
								num_trabaj=colaboradoresUnidad!=null && !colaboradoresUnidad.isEmpty()?colaboradoresUnidad.size():0;
								for (int x = 0; x <= colaboradoresUnidad.size()-1; x++) {								
									mcolab=(HashMap)colaboradoresUnidad.get(x);
									mcodpers=mcolab.get("t02cod_pers")!=null?((String)mcolab.get("t02cod_pers")).trim():"";
									//if (log.isDebugEnabled()) log.debug("mcodpers: " + mcodpers);
									if (x==num_trabaj-1){
									   des_notif=des_notif+mcodpers;
									}else{
									   des_notif=des_notif+mcodpers+"|";
									}
									//if (log.isDebugEnabled()) log.debug("des_notif: "+des_notif);
								}
							}else{
								//if (log.isDebugEnabled()) log.debug("entro x aca");
								num_trabaj=colabSinProgramacion!=null && !colabSinProgramacion.isEmpty()?colabSinProgramacion.size():0;
								//if (log.isDebugEnabled()) log.debug("num_trabaj: " + num_trabaj);
								des_notif="";
							}							
							datosIns.put("cod_uorg_notif",codUnidad);
							datosIns.put("fec_envio_notif",ahora);
							datosIns.put("cod_user_crea","BATCH");
							datosIns.put("fec_creacion",ahora);
							datosIns.put("ind_tip_notif","2");
							datosIns.put("num_trabaj",String.valueOf(num_trabaj));
							datosIns.put("des_notif",des_notif);	
							
							// VALIDANDO SI JEFE TIENE CORREO																		
							if (!correojefeUO.equals("")) {						
								try{									
									Secuencia = new SequenceDAO().getSequence(dsSecuence,"SET9036");									
									datosIns.put("cod_pers_jefe",jefeUO.trim());					
									datosIns.put("num_seqdir", String.valueOf(Secuencia));								
									inserto=t9036dao.insertNotificDirectivoProgramaciones(datosIns);
									if (inserto==true){
										log.debug("SI se inserto notif a jefe de unidad: "+codUnidad);
										mParams.put("registro",jefeUO.trim());
										mParams.put("nombre",nombreJefe);
										mParams.put("ticket",String.valueOf(Secuencia));
										if (log.isDebugEnabled()) log.debug("mParams: " + mParams);	
										mensaje = textoCorreoAlertaDirectivoSinProgramaciones(mParams);//devuelve todo el mensaje en html
										Correo objCorreo = new Correo(mensaje.toString());									
										objCorreo.setServidor(propiedades.leePropiedad("servidor"));									
										objCorreo.setRemitente(propiedades.leePropiedad("correoReceptorAlertasVacaciones"),propiedades.leePropiedad("nombreReceptorAlertasVacaciones"));						
										objCorreo.agregarDestinatario(correojefeUO.trim());														
										objCorreo.setAsunto("Alerta de colaboradores sin programación de vacaciones");
										if(listaArchivos!=null && listaArchivos.length>0){
											objCorreo.setAdjuntos(listaArchivos);		
										}	
										objCorreo.enviarHtml();
										if (log.isDebugEnabled()) log.debug("Se envio correo a jefe");
									}else{
										log.debug("NO se inserto notif a jefe de unidad: "+codUnidad);
									}																
								} catch (CorreoException ce) {
									if (log.isDebugEnabled()) log.debug("Error en CorreoException: "+ce.toString());									 	
								 	if (log.isDebugEnabled()) log.debug("alerta directivo sin programacion-No se pudo enviar correo al jefe: " + jefeUO);									 										 	
								}											
							}//fin correo
							// VALIDANDO SI DELEGADO TIENE CORREO																		
							if (!correodelegadoUO.equals("")) {						
								try{
									Secuencia = new SequenceDAO().getSequence(dsSecuence,"SET9036");									
									datosIns.put("cod_pers_jefe",delegadoUO.trim());					
									datosIns.put("num_seqdir", String.valueOf(Secuencia));
									inserto=t9036dao.insertNotificDirectivoProgramaciones(datosIns);
									if (inserto==true){
										log.debug("SI se inserto notif a delegado de unidad: "+codUnidad);
										mParams.put("registro",delegadoUO.trim());
										mParams.put("nombre",nombreDeleg);
										mParams.put("ticket",String.valueOf(Secuencia));
										if (log.isDebugEnabled()) log.debug("mParams delegado: " + mParams);
										mensaje2 = textoCorreoAlertaDirectivoSinProgramaciones(mParams);//devuelve todo el mensaje en html
										Correo objCorreo2 = new Correo(mensaje2.toString());									
										objCorreo2.setServidor(propiedades.leePropiedad("servidor"));									
										objCorreo2.setRemitente(propiedades.leePropiedad("correoReceptorAlertasVacaciones"),propiedades.leePropiedad("nombreReceptorAlertasVacaciones"));
										objCorreo2.agregarDestinatario(correodelegadoUO.trim());														
										objCorreo2.setAsunto("Alerta de colaboradores sin programación de vacaciones");
										if(listaArchivos!=null && listaArchivos.length>0){
											objCorreo2.setAdjuntos(listaArchivos);		
										}	
										objCorreo2.enviarHtml();
										if (log.isDebugEnabled()) log.debug("Se envio correo a delegado");
									}else{
										log.debug("NO se inserto notif a jefe de unidad: "+codUnidad);
									}															
								} catch (CorreoException ce) {
									if (log.isDebugEnabled()) log.debug("Error en CorreoException: "+ce.toString());									 	
								 	if (log.isDebugEnabled()) log.debug("alerta directivo sin programacion-No se pudo enviar correo al delegado: " + delegadoUO);									 										 	
								}											
							}//fin correo
						}//fin colabSinProgramacion
					
					}//fin for	
					if (log.isDebugEnabled()) log.debug("colabSinProgramacionRRHH final: " + colabSinProgramacionRRHH);
					//enviarCorreosRRHH_ResumenNotificacionesSinProgramacion(dcsp,parametros);
					enviarCorreosRRHH_ResumenNotificacionesSinProgramacion(dcsp,parametros,colabSinProgramacionRRHH);
					
				} catch (Exception ex) {
					if (log.isDebugEnabled()) log.debug("*** Exception ex - Error en enviarAlertaSinProgramacionVacaciones()****",ex);
					enviarCorreosRRHH_ResumenNotificacionesSinProgramacion(dcsp,parametros,colabSinProgramacionRRHH);
				}										
			}			
						
		} catch (DAOException d) {
			if (log.isDebugEnabled()) log.error("*** DAOException Final - Error en enviarAlertaSinProgramacionVacaciones()****",d);
			enviarCorreosRRHH_ResumenNotificacionesSinProgramacion(dcsp,parametros,colabSinProgramacionRRHH);
			StringBuffer msg = new StringBuffer().append("ERROR PROCESO AUTOMATICO DE ENVIAR ALERTAS A DIRECTIVOS SIN PROGRAMACION VACACIONES DE COLABORADORES: ").append(d.toString());
        	throw new FacadeException(this, msg.toString());
		} catch (Exception e) {
		    if (log.isDebugEnabled()) log.error("*** Exception Final - Error en enviarAlertaConProgramacionVacaciones()****",e);
		    enviarCorreosRRHH_ResumenNotificacionesSinProgramacion(dcsp,parametros,colabSinProgramacionRRHH);
		    StringBuffer msg = new StringBuffer().append("ERROR2 PROCESO AUTOMATICO DE ENVIAR ALERTAS A DIRECTIVOS SIN PROGRAMACION VACACIONES DE COLABORADORES: ").append(e.toString());
	        throw new FacadeException(this, msg.toString());			
		} finally{
			log.info("fin metodo: enviarAlertaSinProgramacionVacaciones()");
		}
	}
	
	
	/**
	 * Metodo que envia correos con archivo zipeado excel de resumen de notificaciones enviadas a directivos (sin programacion vacaciones) en una fecha
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"	
	 * @param dgsp DataSource	
	 * @param mapaDatos Map	
	 * @param listaRRHH List	
	 * @throws FacadeException
	 * @throws RemoteException
	 * @throws CreateException
	 */	
	public void enviarCorreosRRHH_ResumenNotificacionesSinProgramacion(DataSource dcsp,Map mapaDatos,List listaRRHH) throws FacadeException,RemoteException, CreateException{
		
		log.info("ingreso enviarCorreosRRHH_ResumenNotificacionesSinProgramacion");
		StringBuffer mensajeReporte=null;		
		File[] listaArchivos=new File[2];// solo se adjuntara 1 archivo
		List notificacionesEnviadas = null;
		//SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
		//SimpleDateFormat sdfformatoArchivo = new SimpleDateFormat("yyyyMMddHHmm");
		//Timestamp ahora = new Timestamp(System.currentTimeMillis());
		String dbpool = "jdbc/dcsp";
		//DataSource dcsp = sl.getDataSource("java:comp/env/jdbc/dcsp");//datasource de lectura
		
		String nombreArchivo="";
		String nombreArchivoRH="";
		Map notificacion = new HashMap();								
		ReporteFacadeHome reporteFacadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,ReporteFacadeHome.class);
		ReporteFacadeRemote reporteFacadeRemote = reporteFacadeHome.create();
		
		T9036DAO t9036dao = new T9036DAO(dcsp);//tabla t9036notifvacdir		
		T99DAO t99dao = new T99DAO(dbpool);
						
		String fechaNotif = (String)mapaDatos.get("fechaNotif");
		String registro="";
		String apenom="";
		String unidad="";
		String cantTrabaj="";
		String fechaNotifi="";
		
		Map programacion = new HashMap();
		String registroColab="";
		String apenomColab="";
		String fecIngresoColab="";
		String unidadColab="";
		String periodoColab="";	
		String periodoSgteColab="";
		
		if (log.isDebugEnabled()) log.debug("mapaDatos: "+mapaDatos);
		
		try {			
			ParamBean prmCorreoRrhh = t99dao.buscar(new String []{"510"}, dcsp, "43");//primer correo para enviar a rrhh
			ParamBean prmCorreoRrhh2 = t99dao.buscar(new String []{"510"}, dcsp, "44");//primer correo para enviar a rrhh
			String correo1rrhh=prmCorreoRrhh!=null?prmCorreoRrhh.getDescripcion().trim():"";
			String correo2rrhh=prmCorreoRrhh2!=null?prmCorreoRrhh2.getDescripcion().trim():"";
		
			notificacionesEnviadas=t9036dao.findNotificacionesByFecha(mapaDatos);
			if (log.isDebugEnabled()) log.debug("notificacionesEnviadas: "+notificacionesEnviadas);
			
			File excelCreado=null;			
			if (notificacionesEnviadas!=null && !notificacionesEnviadas.isEmpty()){					
				//nombreArchivo="directivosNotificadosSinProgramacion"+sdfformatoArchivo.format(ahora).toString();
				nombreArchivo="directivosNotificadosSinProgramacion";
				
				FileOutputStream fileOut = new FileOutputStream(Constantes.RUTA_LOG_REPORTES + nombreArchivo + ".xlsx");
				XSSFWorkbook workbook = new XSSFWorkbook();
				XSSFSheet hoja1=workbook.createSheet();
				XSSFRow fila=hoja1.createRow((int) 0);
				fila.createCell((int) 0).setCellValue(new XSSFRichTextString("REG. DIRECTIVO"));
				fila.createCell((int) 1).setCellValue(new XSSFRichTextString("APELLIDOS Y NOMBRES DIRECTIVO"));
				fila.createCell((int) 2).setCellValue(new XSSFRichTextString("UNIDAD"));
				fila.createCell((int) 3).setCellValue(new XSSFRichTextString("CANT. TRABAJADORES"));
				fila.createCell((int) 4).setCellValue(new XSSFRichTextString("FECHA NOTIFICACION"));
				XSSFRow nfila = null;
				int m =0;				
									
				for (int i = 0; i <= notificacionesEnviadas.size()-1; i++) {					
					nfila = hoja1.createRow((int) m + 1);
					notificacion=null;					
					notificacion=(HashMap)notificacionesEnviadas.get(i);					
					registro=notificacion.get("regdirec")!=null?(String)notificacion.get("regdirec"):"";
					apenom=notificacion.get("apenomdirec")!=null?(String)notificacion.get("apenomdirec"):"";
					unidad=notificacion.get("desuo")!=null?(String)notificacion.get("desuo"):"";
					cantTrabaj=String.valueOf((Integer)notificacion.get("num_trabaj"));				
					fechaNotifi=notificacion.get("fecnotiffinal")!=null?(String)notificacion.get("fecnotiffinal_desc"):"";
					nfila.createCell((int) 0).setCellValue(new XSSFRichTextString(registro));
					nfila.createCell((int) 1).setCellValue(new XSSFRichTextString(apenom));
					nfila.createCell((int) 2).setCellValue(new XSSFRichTextString(unidad));
					nfila.createCell((int) 3).setCellValue(new XSSFRichTextString(cantTrabaj));
					nfila.createCell((int) 4).setCellValue(new XSSFRichTextString(fechaNotifi));					
					m++;				
				}				
				//ancho columnas
				XSSFRow row = workbook.getSheetAt(0).getRow(0);
				for(int colNum = 0; colNum<row.getLastCellNum();colNum++)   
	               workbook.getSheetAt(0).autoSizeColumn(colNum);
				//				
			    workbook.write(fileOut);
			    fileOut.flush();
				fileOut.close();	
				
				File excelCreado2=null;			
				if (listaRRHH!=null && !listaRRHH.isEmpty()){
					//excel
					nombreArchivoRH="Colaboradores sin programacion general";
				
					FileOutputStream fileOutRH = new FileOutputStream(Constantes.RUTA_LOG_REPORTES + nombreArchivoRH + ".xlsx");
					XSSFWorkbook workbookRH = new XSSFWorkbook();
					XSSFSheet hojaRH=workbookRH.createSheet();
					XSSFRow filaRH=hojaRH.createRow((int) 0);
					filaRH.createCell((int) 0).setCellValue(new XSSFRichTextString("REGISTRO"));
					filaRH.createCell((int) 1).setCellValue(new XSSFRichTextString("APELLIDOS Y NOMBRES"));
					filaRH.createCell((int) 2).setCellValue(new XSSFRichTextString("FECHA DE INGRESO"));
					filaRH.createCell((int) 3).setCellValue(new XSSFRichTextString("UNIDAD ORGANIZACIONAL"));
					filaRH.createCell((int) 4).setCellValue(new XSSFRichTextString("PERIODO VACACIONAL ACTUAL"));	
					filaRH.createCell((int) 5).setCellValue(new XSSFRichTextString("PERIODO VACACIONAL SIGUIENTE"));
					XSSFRow nfilaRH = null;
					int p =0;				
										
					for (int n = 0; n <= listaRRHH.size()-1; n++) {					
						nfilaRH = hojaRH.createRow((int) p + 1);
						programacion=null;					
						programacion=(HashMap)listaRRHH.get(n);
						//if (log.isDebugEnabled()) log.debug("programacion: " + programacion);
						
						registroColab=programacion.get("t02cod_pers")!=null?(String)programacion.get("t02cod_pers"):"";
						apenomColab=programacion.get("apenom")!=null?(String)programacion.get("apenom"):"";
						fecIngresoColab=programacion.get("t02f_ingsun_desc")!=null?(String)programacion.get("t02f_ingsun_desc"):"";
						unidadColab=programacion.get("coduo")!=null?(String)programacion.get("desuo"):"";	
						periodoColab=programacion.get("periodo_Act_vac")!=null?((String)programacion.get("periodo_Act_vac")).equals("PROGRAMADO")?(String)programacion.get("periodo_Act_vac"):((String)programacion.get("periodo_Act_vac")+" ( "+(String)programacion.get("saldoAct")+" ) "):"";
						//periodoColab=programacion.get("periodo_Act_vac")!=null?((String)programacion.get("periodo_Act_vac")+" ( "+(String)programacion.get("saldoAct")+" ) "):"";
						periodoSgteColab=programacion.get("periodo_Sig_vac")!=null?(String)programacion.get("periodo_Sig_vac"):"";
												
						nfilaRH.createCell((int) 0).setCellValue(new XSSFRichTextString(registroColab));
						nfilaRH.createCell((int) 1).setCellValue(new XSSFRichTextString(apenomColab));
						nfilaRH.createCell((int) 2).setCellValue(new XSSFRichTextString(fecIngresoColab));
						nfilaRH.createCell((int) 3).setCellValue(new XSSFRichTextString(unidadColab));
						nfilaRH.createCell((int) 4).setCellValue(new XSSFRichTextString(periodoColab));
						nfilaRH.createCell((int) 5).setCellValue(new XSSFRichTextString(periodoSgteColab));
						p++;				
					}//fin for colab sin programacion
					p=p+4;
					nfilaRH = hojaRH.createRow((int) p);
					nfilaRH.createCell((int) 0).setCellValue(new XSSFRichTextString("Total: "));
					nfilaRH.createCell((int) 1).setCellValue(new XSSFRichTextString(listaRRHH.size()+" trabajador(es)"));
					//ancho columnas
					XSSFRow rowRH = workbookRH.getSheetAt(0).getRow(0);
					for(int colNum = 0; colNum<rowRH.getLastCellNum();colNum++)   
						workbookRH.getSheetAt(0).autoSizeColumn(colNum);
					//
					workbookRH.write(fileOutRH);
					fileOutRH.flush();
					fileOutRH.close();					
					excelCreado2=reporteFacadeRemote.registraReporte(nombreArchivoRH,null, "scheduler"); //se obtiene excel zipeado						
				}	
				
				excelCreado=reporteFacadeRemote.registraReporte(nombreArchivo, null, "scheduler"); //se obtiene excel zipeado
				
				if (excelCreado.exists()){//excel zipeado					
					try{					
						listaArchivos[0]=excelCreado;	
						
						if (excelCreado2.exists()){//excel zipeado
							listaArchivos[1]=excelCreado2;				
						}
						//fin excel
						//ENVIO DE CORREO CON EXCEL RESUMEN ADJUNTO
						mensajeReporte = new StringBuffer("<html><head><title>SIRH-ASISTENCIA</title><style>")
						.append(".T1 {font-style  : normal;text-transform : uppercase;font-size :10pt;color:")
						.append("#FFFFFF;background  : #336699;font-family : Tahoma,Verdana,Arial,Helvetica,sans-serif ;}</style></head>")
						.append("<body><table width='100%' align='center' class='T1'><tr><td><center><strong>SIRH-ASISTENCIA</strong></center></td></tr></table><br><br>")
						.append("<table>")		
						.append("<tr><td class='membrete'><strong>Fecha:</strong></td></tr>")
						.append("<tr><td class='dato'>")
						.append(new FechaBean().getFormatDate("dd/MM/yyyy"))
						.append("</td></tr>")			
						.append("<tr><td class='membrete'><strong>Detalle:</strong></td></tr>")
						.append("<tr><td class='dato'>")
						.append("Se adjunta excel resumen de notificaciones enviadas a directivos referente a trabajadores sin programación vacacional, correspondientes al "+new FechaBean().getFormatDate("dd/MM/yyyy")+".")
						.append("</td></tr>");		
						mensajeReporte.append("</table></body></html>");
											
						Correo objCorreoReporte = new Correo(mensajeReporte.toString());							
						objCorreoReporte.setServidor(propiedades.leePropiedad("servidor"));
						objCorreoReporte.setRemitente(propiedades.leePropiedad("correoReceptorAlertasVacaciones"),propiedades.leePropiedad("nombreReceptorAlertasVacaciones"));
						objCorreoReporte.agregarDestinatario(correo1rrhh);
						objCorreoReporte.agregarConCopia(correo2rrhh);
						objCorreoReporte.setAsunto("Envio resumen de notificaciones a directivos sin programacion de trabajadores - enviada el "+new FechaBean().getFormatDate("dd/MM/yyyy"));
						if(listaArchivos!=null && listaArchivos.length>0){
							objCorreoReporte.setAdjuntos(listaArchivos);		
						}									
						objCorreoReporte.enviarHtml();						
						log.info("Se envio correo con excel resumen de notificaciones.");
						
						reporteFacadeRemote.eliminaArchivosTemporales(nombreArchivo); //xlsx y zip
						reporteFacadeRemote.eliminaArchivosTemporales(nombreArchivoRH); //xlsx y zip
						//FIN DE ENVIO					
						
					} catch (Exception e) {
						if (log.isDebugEnabled()) log.debug("Exception Inicial enviarCorreosRRHH_ResumenNotificacionesSinProgramacion");
						throw new Exception("No se pudo enviar correo con archivo resumen de notificaciones de vacaciones");						
					}						
				}else{
					if (log.isDebugEnabled()) log.debug("Archivo Resumen de notificaciones enviadas no existe.");					
				}					
			}else{
				if (log.isDebugEnabled()) log.debug("No se enviaron notificaciones en la fecha: "+fechaNotif);					
			}	

		} catch (CreateException e) {
			log.debug("CreateException Final en enviarCorreosRRHH_ResumenNotificacionesSinProgramacion");
			log.error(e.getMessage());			
		} catch (Exception e) {
			log.debug("Exception Final en enviarCorreosRRHH_ResumenNotificacionesSinProgramacion");			
			log.error(e.getMessage());			
		} 
		
	}
	
	/** 
	 * Metodo encargado de obtener el codigo de unidad organica restando los ceros empezando desde el final hasta el caracter que no sea cero	
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"	
	 * @param String unidad
	 * @return String uoJefe
	 * @throws FacadeException
	 */
	public String findUuooJefe(String unidad) throws FacadeException{
		String uoJefe = "";		
		try{			
						
			log.debug("findUuooJefe-unidad: "+unidad); 
			uoJefe= unidad!=null? unidad.trim(): "";
        	if (!"".equals(uoJefe)){        		
        		int nroCar = uoJefe.length();            	
            	char v= '9';  //solo 1 caracter almacena              	
            	for (int p = nroCar-1; p >= 0; p--) {            		
            		v= uoJefe.charAt(p);
            		if (p!=0){            		
                		if ('0'==v){
                			uoJefe=uoJefe.substring(0, p);                		
                		}else{                			
                			break;
                		} 
            		}	
            	}            		
        	}
        	log.debug("findUuooJefe-uoJefe: "+uoJefe);
			
		} catch(Exception e){
			log.error(e);
			throw new FacadeException(this,e.getMessage());
		} finally{
		}
		log.debug("findUuooJefe-uoJefe(final): "+uoJefe);
		return uoJefe; 
	}
	
	/**
	 * Metodo que devuelve el texto en html para la alerta a directivos con programacion de trabajadores
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param params Map	
	 * @return String
	 * @throws FacadeException
	 */
	public String textoCorreoAlertaDirectivoConProgramaciones(Map params) throws FacadeException{
		String head = "";
		String html = "";
		StringBuffer body = new StringBuffer();
		log.debug("params: " + params);		
		String tituloCorreo="SIRH-Asistencia";
		log.debug("ingreso a textoCorreoAlertaDirectivoConProgramaciones");
		
		try {

			head += "<html><head><title>"
					+ tituloCorreo
					+ "</title><style>"
					+ ".T1 {font-style  : normal;text-transform : uppercase;font-size :10pt;color: #FFFFFF;background  : #336699;font-family : Tahoma,Verdana,Arial,Helvetica,sans-serif ;}"
					+ "</style></head>";
			log.debug("head:" + head);
			
			body.append(
					"<body><table width='100%' align='center' class='T1'><tr><td><center><strong>"
							+ tituloCorreo
							+ "</strong></center></td></tr></table><br><br>")
					.append("<table>")
					.append("<tr><td class='membrete'><strong>Ticket:</strong></td></tr>")
					.append("<tr><td class='dato'>")
					.append((String)params.get("ticket"))
					.append("</td></tr>")
					.append("<tr><td class='membrete'><strong>Fecha:</strong></td></tr>")
					.append("<tr><td class='dato'>")
					.append((String)params.get("fec_alerta"))
					.append("</td></tr>")
					.append("<tr><td class='membrete'><strong>Asunto:</strong></td></tr>")
					.append("<tr><td class='dato'>Alerta de colaboradores con programación de vacaciones</td></tr>")
					.append("<tr><td><strong>Sr(a)(ita).</strong> <em>").append((String)params.get("registro")+" - ")
					.append((String)params.get("nombre")).append("</em></td></tr></table><br>")
					.append("<table width='80%'>")
					.append("<tr><td><em>")										
					.append("Estimado Directivo,")
					.append("</em></td></tr><br>")
					.append("<tr><td class='dato'>")
					.append("Adjunto al presente se le hace llegar:")
					.append("</td></tr><br>")
					.append("<tr><td class='dato'>")
					.append("Relación de trabajadores con programación de vacaciones a ser gozadas en el siguiente mes.")
					.append("</td></tr>")
					.append("</table><br><br>");
           
			body.append("<table>")
			.append("<tr><td class='dato'>")
			.append("Atentamente,").append("</td></tr><br><br>")
			.append("<tr><td class='membrete'><strong>Divisi&oacute;n de Compensaciones</strong></td></tr>")			
			.append("<tr><td class='membrete'><strong>Intendencia Nacional de Recursos Humanos</strong></td></tr>")
			.append("</table>");	
			log.debug("body: " + body);
			
			html = head + body.toString();
			log.debug("html: " + html);

		} catch (Exception e) {
			return "";
		}
		return html;
	}
	
	
	/**
	 * Metodo que devuelve el texto en html para la alerta a directivos sin programacion de trabajadores
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param params Map	
	 * @return String
	 * @throws FacadeException
	 */
	public String textoCorreoAlertaDirectivoSinProgramaciones(Map params) throws FacadeException{
		String head = "";
		String html = "";
		StringBuffer body = new StringBuffer();
		log.debug("params: " + params);		
		String tituloCorreo="SIRH-Asistencia";
		log.debug("ingreso a textoCorreoAlertaDirectivoSinProgramaciones");
		
		try {
			head += "<html><head><title>"
					+ tituloCorreo
					+ "</title><style>"
					+ ".T1 {font-style  : normal;text-transform : uppercase;font-size :10pt;color: #FFFFFF;background  : #336699;font-family : Tahoma,Verdana,Arial,Helvetica,sans-serif ;}"
					+ "</style></head>";
			log.debug("head:" + head);
			
			body.append(
					"<body><table width='100%' align='center' class='T1'><tr><td><center><strong>"
							+ tituloCorreo
							+ "</strong></center></td></tr></table><br><br>")
					.append("<table>")
					.append("<tr><td class='membrete'><strong>Ticket:</strong></td></tr>")
					.append("<tr><td class='dato'>")
					.append((String)params.get("ticket"))
					.append("</td></tr>")
					.append("<tr><td class='membrete'><strong>Fecha:</strong></td></tr>")
					.append("<tr><td class='dato'>")
					.append((String)params.get("fec_alerta"))
					.append("</td></tr>")
					.append("<tr><td class='membrete'><strong>Asunto:</strong></td></tr>")
					.append("<tr><td class='dato'>Alerta de colaboradores sin programación de vacaciones</td></tr>")
					.append("<tr><td><strong>Sr(a)(ita).</strong> <em>").append((String)params.get("registro")+" - ")
					.append((String)params.get("nombre")).append("</em></td></tr></table><br>")
					.append("<table width='80%'>")
					.append("<tr><td><em>")										
					.append("Estimado Directivo,")
					.append("</em></td></tr><br>")
					.append("<tr><td class='dato'>")
					.append("Adjunto al presente se le hace llegar:")
					.append("</td></tr><br>")
					.append("<tr><td class='dato'>")
					.append("Relación de trabajadores que a la fecha no registran programación de vacaciones para el periodo vacacional vigente.")
					.append("</td></tr>")
					.append("<tr><td class='dato'>")
					.append("En ese sentido, agradeceremos regularizar a la brevedad posible el registro de la programación vacacional, cuyo incumplimiento genera la aplicación")
					.append("</td></tr>")
					.append("<tr><td class='dato'>")
					.append("de las medidas administrativas que pudieran corresponder en el procedimiento general 5.5 – Disposiciones finales de la Resolución N°060-2016-8A0000.")
					.append("</td></tr>")
					.append("</table><br><br>");
           
			body.append("<table>")
			.append("<tr><td class='dato'>")
			.append("Atentamente,").append("</td></tr><br><br>")
			.append("<tr><td class='membrete'><strong>Divisi&oacute;n de Compensaciones</strong></td></tr>")			
			.append("<tr><td class='membrete'><strong>Intendencia Nacional de Recursos Humanos</strong></td></tr>")
			.append("</table>");	
			log.debug("body: " + body);
			
			html = head + body.toString();
			log.debug("html: " + html);
		} catch (Exception e) {
			return "";
		}
		return html;
	}
	
	 /**
	   * metodo retornaSaldo: que se encarga de retornar el saldo de un anio vacacional para una persona
	   * @param List listaSaldos 
	   * @return String, retorna el saldo
	   * @throws FacadeException
	   * 
	   * @ejb.interface-method view-type="remote"
	   * @ejb.transaction type="NotSupported"
	   */
	  public String retornaSaldo(List listaSaldos,String codPersona,String annovac) throws FacadeException {
	    String saldo="0";
	    try {
	      for (int i=0;i<listaSaldos.size();i++) {
	      	if (codPersona.equals(((HashMap)listaSaldos.get(i)).get("cod_pers").toString()) &&
	      			annovac.equals(((HashMap)listaSaldos.get(i)).get("anno").toString())) {
	      		saldo = ((HashMap)listaSaldos.get(i)).get("saldo").toString();
	      		break;
	      	}
	      }
	    	
	    } catch (DAOException e) {
	      log.error(e, e);
	      MensajeBean beanM = new MensajeBean();
	      beanM.setMensajeerror(e.getMessage());
	      beanM.setMensajesol("Por favor intente nuevamente, posible error en la BD");
	    } catch(Exception e) {
	      log.error(e);
	      MensajeBean beanM = new MensajeBean();
	      beanM.setError(true);
	      beanM.setMensajeerror(e.getMessage());
	      beanM.setMensajesol("Por favor intente nuevamente.");
	      throw new FacadeException(this,beanM);      
	    } finally {
	    }
	    return saldo;
	  }  
	//FIN ICAPUNAY - PAS20171U230300074 - Mejoras vacaciones
	
	
	/**
	 * Genera una cadena que contiene una tabla html con turnos para alerta de cambio de turno
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"	 
	 * @param lista List	
	 * @return String
	 * @throws FacadeException
	 */
	public String generaTablaAlertaCambioTurno(List lista) throws FacadeException{
		StringBuffer cadenaTabla = new StringBuffer("");
		Map turno = null;
		String des_turno="";
		Date dfec_ini = null;
		String fec_ini="";
		Date dfec_fin = null;
		String fec_fin="";
		Integer cnt_min_comp_acu=new Integer(0);
		log.debug("ingreso a generaTablaTurnosCreadosModificados(lista): " + lista);
		
		try{			
			cadenaTabla.append("<table border='0' cellpadding='0' cellspacing='1'> <tr align='center'>")			
			.append("<th width='40%' ><font face='Verdana' size='2'>Turno</font></th>")
			.append("<th width='15%' ><font face='Verdana' size='2'>Fecha inicio</font></th>")
			.append("<th width='15%' ><font face='Verdana' size='2'>Fecha fin</font></th>")		
			.append("</tr>");

			for(int i=0;i<lista.size();i++){
				turno = (HashMap) lista.get(i);
				des_turno= (turno.get("turno")!=null?turno.get("turno").toString().trim():"")+" - " + (turno.get("des_turno")!=null?turno.get("des_turno").toString().trim():"");
				log.debug("des_turno: " + des_turno);
				dfec_ini = turno.get("fini")!=null?(Date)turno.get("fini"):null;
				fec_ini = dfec_ini!=null?Utiles.dateToString(dfec_ini):"";
				log.debug("fec_ini: " + fec_ini);	
				dfec_fin = turno.get("ffin")!=null?(Date)turno.get("ffin"):null;
				fec_fin = dfec_fin!=null?Utiles.dateToString(dfec_fin):"";
				log.debug("fec_fin: " + fec_fin);
											
				cadenaTabla.append("<tr align='left' class='dato'><td align='left' style='background-color:#FFF;'><font face='Verdana' size='2'>");								
				cadenaTabla.append(des_turno)			
				.append("</font></td><td align='center' style='background-color:#FFF;'><font face='Verdana' size='2'>")
				.append(fec_ini)
				.append("</font></td><td align='center' style='background-color:#FFF;'><font face='Verdana' size='2'>")
				.append(fec_fin)
				.append("</font></td>")				
				.append("</tr>");
				log.debug("cadenaTabla1: " + cadenaTabla);	
			}			
			cadenaTabla.append("</table>");
			log.debug("cadenaTabla final: " + cadenaTabla);	
			
		} catch(Exception e){
			log.error(e);
			throw new FacadeException(this,e.getMessage());
		} finally{
		}
		return cadenaTabla.toString(); 
	}
	
	/**
	 * Metodo que devuelve el texto en html para la alerta de cambio de turno
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param params Map
	 * @param nombre String
	 * @param mensaje String
	 * @return String
	 * @throws FacadeException
	 */
	public String textoCorreoAlertaCambioTurno(Map params,String nombre,String mensaje) throws FacadeException{

		String head = "";
		String html = "";
		StringBuffer body = new StringBuffer();
		log.debug("params: " + params);
		log.debug("nombre: " + nombre);
		log.debug("mensaje: " + mensaje);
		String tituloCorreo="SIRH-Asistencia";
		log.debug("ingreso a textoCorreoAlertaCambioTurno");
		
		try {

			head += "<html><head><title>"
					+ tituloCorreo
					+ "</title><style>"
					+ ".T1 {font-style  : normal;text-transform : uppercase;font-size :10pt;color: #FFFFFF;background  : #336699;font-family : Tahoma,Verdana,Arial,Helvetica,sans-serif ;}"
					+ "</style></head>";
			log.debug("head:" + head);
			
			body.append(
					"<body><table width='100%' align='center' class='T1'><tr><td><center><strong>"
							+ tituloCorreo
							+ "</strong></center></td></tr></table><br><br>")
					.append("<table>")					
					.append("<tr><td class='membrete'><strong>Fecha:</strong></td></tr>")
					.append("<tr><td class='dato'>")
					.append((String)params.get("fec_alerta"))
					.append("</td></tr>")
					.append("<tr><td class='membrete'><strong>Asunto:</strong></td></tr>")
					.append("<tr><td class='dato'>Registro de turno de trabajador</td></tr>")
					.append("<tr><td><strong>Sr(a)(ita).</strong> <em>").append((String)params.get("registro")+" - ")
					.append(nombre).append("</em></td></tr></table><br>")
					.append("<table width='80%'><tr><td>").append(mensaje)
					.append("</td></tr></table>");
           
			body.append("<table>")			
			
			.append("<tr><td class='membrete'><strong>Divisi&oacute;n de Compensaciones</strong></td></tr>")			
			.append("<tr><td class='membrete'><strong>Intendencia Nacional de Recursos Humanos</strong></td></tr>")
			.append("</table>");	
			log.debug("body: " + body);
			
			html = head + body.toString();
			log.debug("html: " + html);

		} catch (Exception e) {
			return "";
		}
		return html;
	}
	//FIN ICAPUNAY - PAS20165E230300096 - Alerta de Cambio de turno
	
	//ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
	/**
	 * Metodo que devuelve el texto en html para el envio de correo de procesamiento por clima laboral
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param params Map
	 * @param nombre String
	 * @param mensaje String
	 * @return String
	 * @throws FacadeException
	 */
	public String textoCorreoClimaLaboral(Map params,String nombre,String mensaje) throws FacadeException{

		String head = "";
		String html = "";
		StringBuffer body = new StringBuffer();
		log.debug("paramss: " + params);
		log.debug("nombreee: " + nombre);
		log.debug("mensajeee: " + mensaje);
		String tituloCorreo="Justificación de Salida por Actividad de Clima Laboral";
		log.debug("ingreso a textoCorreoClimaLaboral");
		
		try {

			head += "<html><head><title>"
					+ tituloCorreo
					+ "</title><style>"
					+ ".T1 {font-style  : normal;text-transform : uppercase;font-size :10pt;color: #FFFFFF;background  : #336699;font-family : Tahoma,Verdana,Arial,Helvetica,sans-serif ;}"
					+ "</style></head>";
			log.debug("head:" + head);
			
			body.append(
					"<body><table width='100%' align='center' class='T1'><tr><td><center><strong>"
							+ tituloCorreo
							+ "</strong></center></td></tr></table><br><br>")
					.append("<table>")				
					.append("<tr><td><strong>Sr(a)(ita).</strong> <em>").append((String)params.get("registro")+" - ")
					.append(nombre).append("</em></td></tr></table><br>")
					.append("<tr><td>").append(mensaje)
					.append("</td></tr><br><br><br>");
           
			body.append("<table>")			
			//.append("<tr><td class='dato'>")
			//.append((String)params.get("fec_correo"))
			//.append("</td></tr><br>")
			.append("<br>")
			.append("<tr><td class='membrete'><strong>Divisi&oacute;n de Compensaciones</strong></td></tr>")			
			.append("<tr><td class='membrete'><strong>Intendencia Nacional de Recursos Humanos</strong></td></tr>")
			.append("</table>");	
			log.debug("body: " + body);
			
			html = head + body.toString();
			log.debug("html2: " + html);

		} catch (Exception e) {
			return "";
		}
		return html;
	}
	//FIN ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
	
}
