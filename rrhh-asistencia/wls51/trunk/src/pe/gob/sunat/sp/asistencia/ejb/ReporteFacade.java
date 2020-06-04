package pe.gob.sunat.sp.asistencia.ejb;

import java.io.ByteArrayOutputStream;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pe.gob.sunat.batch.dao.QueueDAO;
import pe.gob.sunat.framework.core.bean.MensajeBean;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.core.ejb.FacadeException;
import pe.gob.sunat.framework.core.ejb.StatelessAbstract;
import pe.gob.sunat.framework.core.pattern.DynaBean;
import pe.gob.sunat.framework.core.pattern.ServiceLocator;
import pe.gob.sunat.framework.util.Propiedades;
import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.framework.util.lang.Numero;
import pe.gob.sunat.rrhh.asistencia.dao.T130DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T3792DAO;
import pe.gob.sunat.rrhh.dao.T02DAO;
import pe.gob.sunat.rrhh.formativa.dao.T3339DAO;
import pe.gob.sunat.rrhh.planilla.dao.HistMetasEmpleaDAO;
import pe.gob.sunat.rrhh.planilla.dao.MaestroPersonalDAO;
import pe.gob.sunat.rrhh.planilla.dao.PlaniHistoricasDAO; 
import pe.gob.sunat.sol.BeanMensaje;
import pe.gob.sunat.sol.IncompleteConversationalState;
import pe.gob.sunat.sp.asistencia.bean.BeanHoraExtra;
import pe.gob.sunat.sp.asistencia.bean.BeanMarcacion;
import pe.gob.sunat.sp.asistencia.bean.BeanPeriodo;
import pe.gob.sunat.sp.asistencia.bean.BeanReporte;
import pe.gob.sunat.sp.asistencia.bean.BeanTipoMovimiento;
import pe.gob.sunat.sp.asistencia.bean.BeanTurnoTrabajo;
import pe.gob.sunat.sp.asistencia.dao.ReporteDAO;
import pe.gob.sunat.sp.asistencia.dao.T1270DAO;
import pe.gob.sunat.sp.asistencia.dao.T1275DAO;
import pe.gob.sunat.sp.asistencia.dao.T1276DAO;
import pe.gob.sunat.sp.asistencia.dao.T1279DAO;
import pe.gob.sunat.sp.asistencia.dao.T1281DAO;
import pe.gob.sunat.sp.asistencia.dao.T1454DAO;
import pe.gob.sunat.sp.bean.BeanT12;
import pe.gob.sunat.sp.dao.T01DAO;
import pe.gob.sunat.sp.dao.T12DAO;
import pe.gob.sunat.sp.dao.T99DAO;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;
import pe.gob.sunat.utils.convertidor.pdf.PDFDocumento;
import pe.gob.sunat.utils.convertidor.pdf.PDFTabla;


/* ICAPUNAY - TRABAJADORES CON VACIONES PROGRAMADAS ACEPTADAS O ACTIVAS - 28/02/2011 */	
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
/* FIN ICAPUNAY - TRABAJADORES CON VACIONES PROGRAMADAS ACEPTADAS O ACTIVAS - 28/02/2011 */	


//ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA - 22/08/2011
import pe.gob.sunat.rrhh.asistencia.dao.T1282DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T4635DAO; 
import pe.gob.sunat.rrhh.asistencia.dao.T4636DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T4707DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T4752DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T4751DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T4753DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T4754DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T4819DAO;

import java.math.BigDecimal;

//FIN ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA - 22/08/2011
import pe.gob.sunat.rrhh.asistencia.dao.T8167DAO; //ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
import pe.gob.sunat.rrhh.asistencia.dao.T9036DAO; //ICAPUNAY - PAS20171U230300074 - Mejoras vacaciones
/**
 * 
 * @ejb.bean name="ReporteFacadeEJB"
 *           description="ReporteFacade"
 *           jndi-name="ejb/rrhh/facade/sp/asistencia/ReporteFacadeEJB"
 *           type="Stateless" view-type="remote"
 * 
 * @ejb.home remote-class="pe.gob.sunat.sp.asistencia.ejb.ReporteFacadeHome"
 *           extends="javax.ejb.EJBHome"
 * 
 * @ejb.interface remote-class="pe.gob.sunat.sp.asistencia.ejb.ReporteFacadeRemote"
 *                extends="javax.ejb.EJBObject"
 * 
 * @ejb.resource-ref res-ref-name="jdbc/dcsp" res-type="javax.sql.DataSource" res-auth="Container"
 * @weblogic.resource-description res-ref-name="jdbc/dcsp" jndi-name="jdbc/dcsp"
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
public class ReporteFacade extends StatelessAbstract {
	
	private final Log log = LogFactory.getLog(getClass());
	//JRR - 07/05/2010
	ServiceLocator sl = ServiceLocator.getInstance();
	//PROG - 20/05/2010 
	Propiedades constantes = new Propiedades(getClass(),"/constantes.properties");
	
	/* ICAPUNAY - TRABAJADORES CON VACACIONES PROGRAMADAS ACEPTADAS O ACTIVAS - 28/02/2011 */	
	public PrintStream output = new PrintStream(System.out);
	public int numLinea = 0;
	public int maxColumnas = 120;
	public int maxLineas = 40;
	public int numPagina = 1;

	/* FIN ICAPUNAY - TRABAJADORES CON VACACIONES PROGRAMADAS ACEPTADAS O ACTIVAS - 28/02/2011 */
	
	/* NVILLARS - LABOR EXCEPCIONAL*/
	private PDFDocumento document;
	MensajeBean beanMsg = null;
	/*FIN - NVILLARS*/
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList tipoLicencia(String dbpool, String tipo, String fechaIni,
			String fechaFin, String criterio, String valor, String tipoQuiebre,
			HashMap seguridad) throws IncompleteConversationalState,
			RemoteException {

		ArrayList reporte = null;
		BeanMensaje beanM = new BeanMensaje();
		try {

			T1279DAO movDAO = new T1279DAO();
			ReporteDAO reporteDAO = new ReporteDAO();
			
			//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
			//ICAPUNAY-MEMO 32-4F3100-2013
			/*HashMap roles = (HashMap) seguridad.get("roles");
	    	log.debug("tipoLicencia-roles: "+roles);
	    	if (roles.get(Constantes.ROL_JEFE) != null) {
	    		log.debug("tipoLicencia-seguridad antes: "+seguridad);			
				String uoAO = (String) seguridad.get("uoAO");
				seguridad.put("uoSeg", findUuooJefe(uoAO)+"%");//4EB2%
				log.debug("tipoLicencia-seguridad final: "+seguridad);
	    	}*/			
			//ICAPUNAY-MEMO 32-4F3100-2013
	    	//ICAPUNAY - PAS20165E230300132

			ArrayList lista = new ArrayList();

			//QUIEBRE POR PERSONA
			//EBV 23/05/2006 se cambia joinWithT02T12T99findPersonalByCriterioValor por joinWithT02T12T99findPersonalByCriterioValorInact
			//Personal Inactivo
			if (tipoQuiebre.equals("0")) {
				lista = reporteDAO
						.joinWithT02T12T99findPersonalByCriterioValorInact(dbpool,
								criterio, valor, seguridad);
			}
			//QUIEBRE POR TIPO DE LICENCIA
			else {

				ArrayList tipos = movDAO.joinWithT99T99ByCritValTipo(dbpool,
						"0", tipo.trim().equals("-1") ? "" : tipo,
						Constantes.CORREL_LICENCIA);

				if (tipos != null) {
				    BeanTipoMovimiento mov = null;
				    BeanReporte rep = null;
					for (int i = 0; i < tipos.size(); i++) {

						mov = (BeanTipoMovimiento) tipos.get(i);
						rep = new BeanReporte();

						rep.setCodigo(mov.getMov());
						rep.setNombre(mov.getDescrip());
						lista.add(rep);
					}
				}
			}

			if (lista != null) {
				reporte = new ArrayList();
				BeanReporte quiebre = null;
				ArrayList detalle = null;
				for (int i = 0; i < lista.size(); i++) {
					quiebre = (BeanReporte) lista.get(i);
					detalle = null;
					log.debug("tipoQuiebre:"+tipoQuiebre);
					if (tipoQuiebre.equals("0")) {
						detalle = reporteDAO.findLicenciasByTipoCodPers(dbpool,
								tipo, fechaIni, fechaFin, quiebre.getCodigo());
						log.debug("Quiebre0:"+tipo+","+fechaIni+","+fechaFin+","+quiebre.getCodigo());
					} else {
						detalle = reporteDAO.findPersonalByLicencia(dbpool,
								quiebre.getCodigo(), fechaIni, fechaFin,
								criterio, valor);
						log.debug("QuiebreNo0:"+tipo+","+fechaIni+","+fechaFin+","+quiebre.getCodigo());
					}

					//EBV Prioridad 1 Solo mostrar si tiene informacion por Tipo de Licencia
					
					if ((detalle != null ) && (detalle.size() > 0)) {

					//insertamos el detalle del quiebre
					quiebre.setDetalle(detalle);
					//insertamos el quiebre al reporte
					reporte.add(quiebre);
					}
				}

			}
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return reporte;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList acumuladoLicencia(String dbpool, String tipo,
			String fechaIni, String fechaFin, String criterio, String valor,
			String tipoQuiebre, HashMap seguridad)
			throws IncompleteConversationalState, RemoteException {

		ArrayList reporte = null;
		BeanMensaje beanM = new BeanMensaje();
		try {

			T1279DAO movDAO = new T1279DAO();
			ReporteDAO reporteDAO = new ReporteDAO();

			ArrayList lista = new ArrayList();

			//QUIEBRE POR PERSONA
			if (tipoQuiebre.equals("0")) {
				lista = reporteDAO
						.joinWithT02T12T99findPersonalByCriterioValor(dbpool,
								criterio, valor, seguridad);
			}
			//QUIEBRE POR TIPO DE LICENCIA
			else {

				ArrayList tipos = movDAO.joinWithT99T99ByCritValTipo(dbpool,
						"0", tipo.trim().equals("-1") ? "" : tipo,
						Constantes.CORREL_LICENCIA);

				if (tipos != null) {
				    BeanTipoMovimiento mov = null;
				    BeanReporte rep = null;
				    
					for (int i = 0; i < tipos.size(); i++) {

						mov = (BeanTipoMovimiento) tipos.get(i);
						rep = new BeanReporte();

						rep.setCodigo(mov.getMov());
						rep.setNombre(mov.getDescrip());
						lista.add(rep);
					}
				}
			}

			if (lista != null) {

				reporte = new ArrayList();

				for (int i = 0; i < lista.size(); i++) {

					BeanReporte quiebre = (BeanReporte) lista.get(i);

					ArrayList detalle = null;

					if (tipoQuiebre.equals("0")) {
						detalle = reporteDAO
								.findAcumuladoLicenciasByTipoCodPers(dbpool,
										tipo, fechaIni, fechaFin, quiebre
												.getCodigo());
					} else {
						detalle = reporteDAO.findAcumuladoPersonalByLicencia(
								dbpool, quiebre.getCodigo(), fechaIni,
								fechaFin, criterio, valor);
					}

					//EBV Prioridad 1 Solo mostrar si tiene Acumulado de Licencia
					
					if ((detalle != null ) && (detalle.size() > 0)) {

					//insertamos el detalle del quiebre
					quiebre.setDetalle(detalle);
					//insertamos el quiebre al reporte
					reporte.add(quiebre);
					}
				}
			}
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return reporte;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */	
	public ArrayList marcaciones(Map params) throws FacadeException,
			RemoteException {
		String dbpool = (String)params.get("dbpool");
		String regimen = (String)params.get("regimen");				
		String fechaIni = (String)params.get("fechaIni");
		String fechaFin = (String)params.get("fechaFin");
		String horaMarca = (String)params.get("horaMarca");
		String reloj = (String)params.get("reloj");
		String criterio = (String)params.get("criterio");
		String valor = (String)params.get("valor");		
		Map seguridad = (HashMap)params.get("seguridad");		
		
		ArrayList reporte = null;
		BeanMensaje beanM = new BeanMensaje();		
		ArrayList personal = null;
		try {

			ReporteDAO reporteDAO = new ReporteDAO();
			DataSource dbpool_sp = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dcsp");
			pe.gob.sunat.rrhh.asistencia.dao.ReporteDAO reportDAO = new pe.gob.sunat.rrhh.asistencia.dao.ReporteDAO(dbpool_sp);			
						
			Map datos = new HashMap();
			datos.put("regimen", regimen);
			datos.put("criterio", criterio);
			datos.put("valor", valor);
			datos.put("seguridad", seguridad);
			
			//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
			//ICAPUNAY-MEMO 32-4F3100-2013
			/*HashMap roles = (HashMap) seguridad.get("roles");
	    	log.debug("marcaciones-roles: "+roles);
	    	if (roles.get(Constantes.ROL_JEFE) != null) {
	    		log.debug("marcaciones-seguridad antes: "+seguridad);			
				String uoAO = (String) seguridad.get("uoAO");
				seguridad.put("uoSeg", findUuooJefe(uoAO)+"%");//4EB2%
				log.debug("marcaciones-seguridad final: "+seguridad);
	    	}*/			
			//ICAPUNAY-MEMO 32-4F3100-2013
	    	//ICAPUNAY - PAS20165E230300132
	    	
			List lista = reportDAO
					.joinWithT02T12T99findPersonalByCriterioValorAct(datos);					
			if(log.isDebugEnabled()) log.debug("personal:"+lista);
			
			if (lista != null) {
				Map rs = null;				
				String uuoo = "";
				personal = new ArrayList();				
				StringBuffer texto = new StringBuffer("");					
				for (int i = 0; i < lista.size(); i++) {
					rs = (HashMap) lista.get(i);				
					texto = new StringBuffer(String.valueOf(rs.get("t02cod_rel")!=null?rs.get("t02cod_rel"):"").trim())
							.append( " - ")		
							.append(String.valueOf(rs.get("t02cod_pers")!=null?rs.get("t02cod_pers"):"").trim()).append( " ")
							.append( " - ")
							.append(String.valueOf(rs.get("t02ap_pate")!=null?rs.get("t02ap_pate"):"").trim()).append( " ")
							.append(String.valueOf(rs.get("t02ap_mate")!=null?rs.get("t02ap_mate"):"").trim()).append( ", ")
							.append(String.valueOf(rs.get("t02nombres")!=null?rs.get("t02nombres"):"").trim()).append( " (")					
							.append(rs.get("t02f_ingsun")!=null ? String.valueOf(rs.get("t02f_ingsun")):"").append( ")  -  ")		
							.append(String.valueOf(rs.get("t99descrip")).trim()).append( " / ")
							.append(String.valueOf(rs.get("t12des_corta")).trim());
					
					rs.put("nombre", texto.toString().trim());					
					uuoo = String.valueOf(rs.get("cod_uorg"));
					rs.put("codigo", (String)rs.get("t02cod_pers"));
					rs.put("unidad", uuoo.trim().concat( " - ")
							.concat(String.valueOf(rs.get("t12des_uorga")).trim()));
					personal.add(rs);						
				}	
			}						
			
			if (personal != null && personal.size() > 0) {
				reporte = new ArrayList();				
				Map quiebre = null;
				ArrayList detalle = null;
				
				for (int j = 0; j < personal.size(); j++) {					
					quiebre = (HashMap) personal.get(j);
					detalle = reporteDAO.findMarcaciones(dbpool, fechaIni, fechaFin, horaMarca, reloj, (String)quiebre.get("t02cod_pers"));					
					if(log.isDebugEnabled()) log.debug("marcacion:" + j + detalle);					
					if ((detalle != null ) && (detalle.size() > 0)) {						
						quiebre.put("detalle",detalle);						
						reporte.add(quiebre);
					}
				}
			}
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return reporte;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList marcacionesImpares(String dbpool, String fechaIni,
			String fechaFin, String criterio, String valor, HashMap seguridad)
			throws IncompleteConversationalState, RemoteException {

		ArrayList reporte = null;
		BeanMensaje beanM = new BeanMensaje();
		try {

			ReporteDAO reporteDAO = new ReporteDAO();
			
			//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
			//ICAPUNAY-MEMO 32-4F3100-2013
			/*HashMap roles = (HashMap) seguridad.get("roles");
	    	log.debug("marcacionesImpares-roles: "+roles);
	    	if (roles.get(Constantes.ROL_JEFE) != null) {
	    		log.debug("marcacionesImpares-seguridad antes: "+seguridad);			
				String uoAO = (String) seguridad.get("uoAO");
				seguridad.put("uoSeg", findUuooJefe(uoAO)+"%");//4EB2%
				log.debug("marcacionesImpares-seguridad final: "+seguridad);
	    	}*/			
			//ICAPUNAY-MEMO 32-4F3100-2013
	    	//ICAPUNAY - PAS20165E230300132

			ArrayList lista = reporteDAO
					.joinWithT02T12T99findPersonalByCriterioValor(dbpool,
							criterio, valor, seguridad);
			log.debug("marcacionesImpares-lista: "+lista);

			if (lista != null) {

				reporte = new ArrayList();
				BeanReporte quiebre = null;
				ArrayList detalle = null;
				
				for (int i = 0; i < lista.size(); i++) {

					quiebre = (BeanReporte) lista.get(i);
					detalle = reporteDAO.findMarcacionesImpares(dbpool, fechaIni, fechaFin, quiebre.getCodigo());

					//EBV Prioridad 1 Solo mostrar si tiene Marcaciones Impares
					
					if ((detalle != null ) && (detalle.size() > 0)) {

					//insertamos el detalle del quiebre
					quiebre.setDetalle(detalle);
					//insertamos el quiebre al reporte
					reporte.add(quiebre);
					}
				}

			}
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return reporte;
	}

	
	/**PRAC-ASANCHEZ 26/08/2009
	 * Metodo encargado de devolver el reporte por persona de labor excepcional
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param params
	 * @param seguridad
	 * @return List
	 * @throws RemoteException
	 */
	public List laborExcepcional(HashMap params, Map seguridad)
	throws RemoteException {

		List reporte = null;
		//BeanMensaje beanM = new BeanMensaje();
		try {
			ReporteDAO reporteDAO = new ReporteDAO();
			
			// ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
			//ICAPUNAY-MEMO 32-4F3100-2013
			/*HashMap roles = (HashMap) seguridad.get("roles");
	    	log.debug("laborExcepcional-roles: "+roles);
	    	if (roles.get(Constantes.ROL_JEFE) != null) {
	    		log.debug("laborExcepcional-seguridad antes: "+seguridad);			
				String uoAO = (String) seguridad.get("uoAO");
				seguridad.put("uoSeg", findUuooJefe(uoAO)+"%");//4EB2%
				log.debug("laborExcepcional-seguridad final: "+seguridad);
	    	}*/			
			//ICAPUNAY-MEMO 32-4F3100-2013
	    	//ICAPUNAY - PAS20165E230300132

			List listaPersonas = reporteDAO.joinWithT02T12T99findPersonalByCriterioValor((String)params.get("dbpool"),
					(String)params.get("criterio"), (String)params.get("valor"), (HashMap)seguridad);

			T02DAO t02dao = new T02DAO(sl.getDataSource((String)params.get("dbpool")));
			Map trabajador = new HashMap();
		
			T130DAO t130dao = new T130DAO(sl.getDataSource((String)params.get("dbpool")));

			if (listaPersonas != null) {
				reporte = new ArrayList();
				BeanReporte quiebre = null;
				List detalle = null;
				Map mapaDetalle = new HashMap();
				Map mapa;
				int totalMinutos;
				float totalDias;
				String totalDiasTexto;

				List listasMarcaciones = null;
				String fecha = "";
				
				int minutosDiarios = 0;
				float diasDiarios = 0;
				String diasDiariosTexto;
				
				List listaMarcaciones = new ArrayList();
				List listaMarcacionesDiarias = null;
				Map rango = new HashMap();
				
				Map mapaDiario;
				String h_inicio;
				String h_fin;
				float minEnDias = 0;
				int minutos = 0;
				Map mapaAux = new HashMap();				

				for (int i = 0; i < listaPersonas.size(); i++) {
					//creaciones de objetos necesarios dentro de este for
					listasMarcaciones = new ArrayList();
					listaMarcacionesDiarias = new ArrayList();
					mapa = new HashMap();	
					quiebre = (BeanReporte) listaPersonas.get(i);
					//trabajador = (HashMap)t02dao.joinWithT12T99((String) quiebre.getCodigo(),seguridad); ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
					trabajador = (HashMap)t02dao.joinWithT12T99_ByAllUUOOsJefe((String) quiebre.getCodigo(),seguridad); //ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
					if (log.isDebugEnabled()) log.debug(quiebre.getCodigo() + " - trabajador: " + trabajador);
					totalMinutos = 0;
					totalDias = 0;
					params.put("trabajador",quiebre.getCodigo());
					params.put("uuoo", (String)trabajador.get("cod_uorg"));
					detalle = t130dao.findLEPendientesCompensar(params);
					
					if (log.isDebugEnabled()) log.debug(quiebre.getCodigo() + " - detalle: " + detalle);
					
					if(detalle.size()>0){
						mapaDetalle = (HashMap)detalle.get(0);
						fecha = mapaDetalle.get("fecha").toString();
					}
					
					minutosDiarios = 0;
					diasDiarios = 0;
					
					for(int j = 0; j < detalle.size(); j++){
						mapaDetalle = (HashMap)detalle.get(j);
						
						if(log.isDebugEnabled())log.debug("mapaDetalle: " + mapaDetalle);
						
						h_inicio = mapaDetalle.get("h_inicio").toString();
						h_fin = mapaDetalle.get("h_fin").toString();
						
						if (fecha.equals(mapaDetalle.get("fecha").toString())){
							minutosDiarios += Integer.parseInt(mapaDetalle.get("minutosle").toString());
							minutos = Integer.parseInt(mapaDetalle.get("minutosle").toString());
							minEnDias= minutos / ( 60 * Utiles.obtenerHorasDiferencia(h_inicio, h_fin));
							
							if(log.isDebugEnabled()) log.debug("minEnDias: " + minEnDias);
							diasDiarios += minEnDias;

							mapaAux.put("dbpool", (String)params.get("dbpool"));
							mapaAux.put("codpers", quiebre.getCodigo());
							mapaAux.put("fecha", fecha);
							mapaAux.put("h_inic", mapaDetalle.get("h_inic").toString());
							mapaAux.put("h_term", mapaDetalle.get("h_term").toString());
							
							listaMarcaciones = calcularListaMarcaciones(mapaAux);
							
/*							listaMarcaciones = calcularListaMarcaciones((String)params.get("dbpool"), 
									quiebre.getCodigo(), fecha, mapaDetalle.get("h_inic").toString(),
									mapaDetalle.get("h_term").toString()); */
							
							if (log.isDebugEnabled()) log.debug("listaMarcaciones: " + listaMarcaciones);
							
							for(int k=0; k<listaMarcaciones.size();k++){
								rango = (HashMap)listaMarcaciones.get(k);
								listaMarcacionesDiarias.add(rango);
							}
							
						}else{
							//JROJAS4 - 16/09/2009
							diasDiariosTexto = Float.toString(diasDiarios);
							if (diasDiariosTexto.substring(diasDiariosTexto.indexOf(".")+1, diasDiariosTexto.length() - diasDiariosTexto.indexOf(".")+1).length() >= 2)
								diasDiariosTexto = diasDiariosTexto.substring(0, diasDiariosTexto.indexOf(".")+3);
							
							mapaDiario = new HashMap();
							mapaDiario.put("listaMarcaciones", listaMarcacionesDiarias);
							mapaDiario.put("minutosDiarios",String.valueOf(minutosDiarios));
							mapaDiario.put("diasDiarios", diasDiariosTexto);
							listasMarcaciones.add(mapaDiario);
							minutosDiarios = Integer.parseInt(mapaDetalle.get("minutosle").toString());
							minEnDias = minutosDiarios / ( 60 * Utiles.obtenerHorasDiferencia(h_inicio, h_fin));
							diasDiarios = minEnDias;
							
							mapaAux.put("dbpool", (String)params.get("dbpool"));
							mapaAux.put("codpers", quiebre.getCodigo());
							mapaAux.put("fecha", mapaDetalle.get("fecha").toString());
							mapaAux.put("h_inic", mapaDetalle.get("h_inic").toString());
							mapaAux.put("h_term", mapaDetalle.get("h_term").toString());

							listaMarcacionesDiarias = calcularListaMarcaciones(mapaAux);
							
/*							listaMarcacionesDiarias = calcularListaMarcaciones((String)params.get("dbpool"), 
									quiebre.getCodigo(), mapaDetalle.get("fecha").toString(), 
									mapaDetalle.get("h_inic").toString(),
									mapaDetalle.get("h_term").toString());*/
							
							fecha = mapaDetalle.get("fecha").toString();
						}
						
						if( j == detalle.size() - 1){
							//JROJAS4 - 16/09/2009
							diasDiariosTexto = Float.toString(diasDiarios);
							if (diasDiariosTexto.substring(diasDiariosTexto.indexOf(".")+1, diasDiariosTexto.length() - diasDiariosTexto.indexOf(".")+1).length() >= 2)
								diasDiariosTexto = diasDiariosTexto.substring(0, diasDiariosTexto.indexOf(".")+3);
							
							mapaDiario = new HashMap();
							mapaDiario.put("listaMarcaciones", listaMarcacionesDiarias);
							mapaDiario.put("minutosDiarios",String.valueOf(minutosDiarios));
							mapaDiario.put("diasDiarios", diasDiariosTexto);
							listasMarcaciones.add(mapaDiario);
						}	
						totalMinutos += Integer.parseInt(mapaDetalle.get("minutosle").toString());
						totalDias += minEnDias;
					}
					
					if (log.isDebugEnabled()) log.debug("listaMarcacionesDiarias: " + listaMarcacionesDiarias);
					
					totalDiasTexto = Float.toString(totalDias);
					if (totalDiasTexto.substring(totalDiasTexto.indexOf(".")+1, totalDiasTexto.length() - totalDiasTexto.indexOf(".")+1).length() >= 2)
						totalDiasTexto = totalDiasTexto.substring(0, totalDiasTexto.indexOf(".")+3);
					
					mapa.put("listasMarcaciones", listasMarcaciones );
					mapa.put("totalMinutos", String.valueOf(totalMinutos));
					mapa.put("totalDias", totalDiasTexto);
					
					if ((detalle != null ) && (detalle.size() > 0)) {
						quiebre.setMap((HashMap)mapa);
						quiebre.setDetalle((ArrayList)detalle);
						reporte.add(quiebre);
					}
				}
			}		
		} catch (Exception e) {
			log.error(e,e);
			MensajeBean beanM = new MensajeBean();
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new FacadeException(this, beanM);
		}
		if (log.isDebugEnabled()) log.debug("REPORTE: " + reporte);
		return reporte;
	}		

	
	/**PRAC-ASANCHEZ 26/08/2009
	 * Metodo encargado de devolver una lista de intervalos de las horas de permanencia de un trabajador en una
	 * fecha determinada y solo las autorizadas.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param mapa
	 * @return List
	 * @throws RemoteException
	 */
	
	public List calcularListaMarcaciones(Map mapa)
		throws RemoteException {

		String dbpool = mapa.get("dbpool").toString();
		String codpers = mapa.get("codpers").toString();
		String fecha = mapa.get("fecha").toString();
		String h_inic = mapa.get("h_inic").toString();
		String h_term = mapa.get("h_term").toString();

		List listaMarcaciones = new ArrayList();
		List listaMarcaciones2 = new ArrayList();
		try {
			T1275DAO daoMarcacion = new T1275DAO();
			T1270DAO turnoDAO = new T1270DAO();
			T01DAO paramDAO = new T01DAO();
			
			BeanTurnoTrabajo turno = turnoDAO.joinWithT45ByCodFecha(dbpool, codpers, fecha);
			List marcaciones = daoMarcacion.findByCodPersFecha(dbpool, codpers, fecha);
			
			if(log.isDebugEnabled()) log.debug("marcaciones:" + marcaciones);
			
			if (marcaciones!=null && marcaciones.size()>0){
		
				boolean diaEspecial = Utiles.isDiaSemana(fecha, Constantes.SABADO)
				|| Utiles.isDiaSemana(fecha, Constantes.DOMINGO)
				|| paramDAO.findByFechaFeriado(dbpool, fecha);	
			
				if(turno!=null){
					String horaIni = "";
					String horaFin = "";
					String hora = "";
					Map rangoHoras;	
					BeanMarcacion marca = new BeanMarcacion();
					Map rango;
					
					if(diaEspecial){
						for (int i=0; i<marcaciones.size();i++){
							rangoHoras = new HashMap();
							marca = (BeanMarcacion) marcaciones.get(i);
							horaIni = marca.getHora();			
							rangoHoras.put("horaIni", horaIni);
							marca = (BeanMarcacion) marcaciones.get(i+1);
							hora = marca.getHora();
							rangoHoras.put("horaFin", hora);
							listaMarcaciones.add(rangoHoras);
							i++;
						}
					}else{
						String horaInicioTurno = turno.getHoraIni();
						String horaFinTurno = turno.getHoraFin();
						
						//o h_term es la hora de inicio de turno o h_inic es la hora fin del turno.
		
						if(h_term.equals(horaInicioTurno)){
							for (int i=0; i<marcaciones.size();i++){
								marca = (BeanMarcacion) marcaciones.get(i);
								hora = marca.getHora();
						
								if(hora.compareTo(horaInicioTurno)<0){
									horaIni = hora;
									rangoHoras = new HashMap();
									//La primera marca genera LE
									rangoHoras.put("horaIni", horaIni);
									//Busco la segunda
									marca = (BeanMarcacion) marcaciones.get(i+1);
									hora = marca.getHora();
									
									if(hora.compareTo(horaInicioTurno)<0){
										rangoHoras.put("horaFin", hora);
										i++;
									}else{
										rangoHoras.put("horaFin", horaInicioTurno);
									}
									listaMarcaciones.add(rangoHoras);
								}
							}
						}
						
						if(h_inic.equals(horaFinTurno)){
							//for salidas
							for (int i=marcaciones.size()-1; i>=0; i--){
								marca = (BeanMarcacion) marcaciones.get(i);
								hora = marca.getHora();
									
								
								if(hora.compareTo(horaFinTurno)>0){
									horaFin = hora;
									rangoHoras = new HashMap();
									//La primera marca genera LE
									rangoHoras.put("horaFin", horaFin);
									//Busco la penultima
									marca = (BeanMarcacion) marcaciones.get(i-1);
									hora = marca.getHora();
									
									if(hora.compareTo(horaFinTurno)>0){
										rangoHoras.put("horaIni", hora);
										i--;
									}else{
										rangoHoras.put("horaIni", horaFinTurno);
									}
									
									//Guardar los pares
									listaMarcaciones2.add(rangoHoras);
								}
							}
							
							//ordenando la lista2
							
							for(int i=listaMarcaciones2.size()-1;i>=0;i--){
								rango = (HashMap)listaMarcaciones2.get(i);
								listaMarcaciones.add(rango);
							}
						}					
					}
				}
			}
		
		}catch (Exception e){
			log.error(e);
		}	
		
		return listaMarcaciones;
	}	
	
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */	
	public ArrayList calificado(Map params, HashMap seguridad)
			throws IncompleteConversationalState, RemoteException {
		
		ArrayList reporte = null;
		BeanMensaje beanM = new BeanMensaje();
		String dbpool =	(params.get("dbpool") != null) ? (String)params.get("dbpool"): "";
		if(log.isDebugEnabled())log.debug("dbpool:" + dbpool);		
		String fechaIni =	(params.get("fechaIni") != null) ? (String)params.get("fechaIni"): "";
		String fechaFin =	(params.get("fechaFin") != null) ? (String)params.get("fechaFin"): "";
		String regimen =	(params.get("regimen") != null) ? (String)params.get("regimen"): "";
		String criterio =	(params.get("criterio") != null) ? (String)params.get("criterio"): "";
		String valor =	(params.get("valor") != null) ? (String)params.get("valor"): "";
		String mov =	(params.get("mov") != null) ? (String)params.get("mov"): "";
		if(log.isDebugEnabled())log.debug("calificado-params:" + params);
		int cant130;
		int cant4819;
			
		try {

			ReporteDAO reporteDAO = new ReporteDAO();
			T130DAO t130DAO = new T130DAO(sl.getDataSource(dbpool));
			T4819DAO t4819DAO = new T4819DAO(sl.getDataSource(dbpool));

			/*EBV REq 30-2007-2F3100 16/01/2008
			 ArrayList lista = reporteDAO
					.joinWithT02T12T99findPersonalByCriterioValor(dbpool,
					        criterio.equals("2")?"0":criterio, valor, seguridad);
					        */
			//JVV-ini --se agrego regimen
			
			//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
			//ICAPUNAY-MEMO 32-4F3100-2013
			/*HashMap roles = (HashMap) seguridad.get("roles");
	    	log.debug("calificado-roles: "+roles);
	    	if (roles.get(Constantes.ROL_JEFE) != null) {
	    		log.debug("calificado-seguridad antes: "+seguridad);			
				String uoAO = (String) seguridad.get("uoAO");
				seguridad.put("uoSeg", findUuooJefe(uoAO)+"%");//4EB2%
				log.debug("calificado-seguridad final: "+seguridad);
	    	}*/			
			//ICAPUNAY-MEMO 32-4F3100-2013
	    	//ICAPUNAY - PAS20165E230300132
			
			ArrayList lista = reporteDAO
			.joinWithT02T12T99findPersonalByCriterioValorPersonalRegimen(dbpool, regimen,
			        criterio.equals("2")?"0":criterio, valor, seguridad);
			//JVV-fin
	

			if (lista != null) {

				reporte = new ArrayList();
				BeanReporte quiebre = null;
				ArrayList detalle = null;
				
				for (int i = 0; i < lista.size(); i++) {

					quiebre = (BeanReporte) lista.get(i);
					detalle = reporteDAO.findCalificaciones(dbpool,
							fechaIni, fechaFin, quiebre.getCodigo(), criterio, mov);

					//EBV Prioridad 1 Solo mostrar si tiene Calificaciones
					
					Date dateIni = new SimpleDateFormat("dd/MM/yyyy").parse(fechaIni);
					Date dateFin = new SimpleDateFormat("dd/MM/yyyy").parse(fechaFin);
					Date dateLim = new SimpleDateFormat("dd/MM/yyyy").parse("13/12/2012");
					
					if ((detalle != null ) && (detalle.size() > 0)) {
						if(dateFin.before(dateLim)){																					//jquispecoi 05/2014
							quiebre.setCantidad(t130DAO.findSaldobyRango(quiebre.getCodigo(), fechaIni,fechaFin).intValue());
						}else{
							if(dateIni.after(dateLim) || dateIni.equals(dateLim)){
								quiebre.setCantidad(t4819DAO.findAcumuladobyRango(quiebre.getCodigo(), fechaIni,fechaFin).intValue());
							}else{
								cant130 = t130DAO.findSaldobyRango(quiebre.getCodigo(), fechaIni, "12/12/2012").intValue();
								cant4819 = t4819DAO.findAcumuladobyRango(quiebre.getCodigo(), "13/12/2012", fechaFin).intValue();
								quiebre.setCantidad(cant130+cant4819);
							}
						}																												//
						
						//insertamos el detalle del quiebre
						quiebre.setDetalle(detalle);
						//insertamos el quiebre al reporte
						reporte.add(quiebre);
					}
				}

			}
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return reporte;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	
	public ArrayList turnosTrabajo(HashMap params)
			throws IncompleteConversationalState, RemoteException {

		ArrayList reporte = null;
		BeanMensaje beanM = new BeanMensaje();
		String dbpool =	(params.get("dbpool") != null) ? (String)params.get("dbpool"): "";
		String fechaIni =	(params.get("fechaIni") != null) ? (String)params.get("fechaIni"): "";
		String fechaFin =	(params.get("fechaFin") != null) ? (String)params.get("fechaFin"): "";
		String regimen =	(params.get("regimen") != null) ? (String)params.get("regimen"): "";
		String criterio =	(params.get("criterio") != null) ? (String)params.get("criterio"): "";
		String valor =	(params.get("valor") != null) ? (String)params.get("valor"): "";
		HashMap seguridad = (HashMap) params.get("seguridad");
		
		try {
			
			//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
			//ICAPUNAY-MEMO 32-4F3100-2013
			/*HashMap roles = (HashMap) seguridad.get("roles");
	    	log.debug("turnosTrabajo-roles: "+roles);
	    	if (roles.get(Constantes.ROL_JEFE) != null) {
	    		log.debug("turnosTrabajo-seguridad antes: "+seguridad);			
				String uoAO = (String) seguridad.get("uoAO");
				seguridad.put("uoSeg", findUuooJefe(uoAO)+"%");//4EB2%
				log.debug("turnosTrabajo-seguridad final: "+seguridad);
	    	}*/			
			//ICAPUNAY-MEMO 32-4F3100-2013
	    	//ICAPUNAY - PAS20165E230300132
		
			
			ReporteDAO reporteDAO = new ReporteDAO();
			//JVV-ini --se agrego regimen
			ArrayList lista = reporteDAO
					.joinWithT02T12T99findPersonalByCriterioVal(dbpool,
							regimen, criterio, valor, seguridad);
			//JVV-fin
			if (lista != null) { //lista=personal.add(a)

				reporte = new ArrayList();
				BeanReporte quiebre = null;
				ArrayList detalle = null;
				
				for (int i = 0; i < lista.size(); i++) {

					quiebre = (BeanReporte) lista.get(i);
					detalle = reporteDAO.findTurnosTrabajo(dbpool,
							fechaIni, fechaFin, quiebre.getCodigo()); //quiebre.getUnidad(), quiebre.getNombre()

					//EBV Prioridad 1 Solo mostrar si tiene Turno
					
					if ((detalle != null ) && (detalle.size() > 0)) {

					//insertamos el detalle del quiebre
					quiebre.setDetalle(detalle);
					//insertamos el quiebre al reporte
					reporte.add(quiebre);
					}
				}
			}
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return reporte;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList horaExtra(String dbpool, String fechaIni, String fechaFin,
			String criterio, String valor, HashMap seguridad, String indDia, String indMin)
			throws IncompleteConversationalState, RemoteException {

		ArrayList reporte = null;
		BeanMensaje beanM = new BeanMensaje();
		try {

			ReporteDAO reporteDAO = new ReporteDAO();

			ArrayList lista = reporteDAO
					.joinWithT02T12T99findPersonalByCriterioValor(dbpool,
							criterio, valor, seguridad);

			if (lista != null) {

				reporte = new ArrayList();
				BeanReporte quiebre = null;
				ArrayList detalle = null;
				ArrayList detalle1 = null;
				ArrayList detalle2 = null;
				HashMap mapa = null;
				
				for (int i = 0; i < lista.size(); i++) {

					quiebre = (BeanReporte) lista.get(i);
					detalle = reporteDAO.findAutorizacionesHoraExtra(dbpool, fechaIni, fechaFin, quiebre.getCodigo());
					if (indDia.equals("1")){
						detalle1 = reporteDAO.findCompensaciones(dbpool,
								fechaIni, fechaFin, quiebre.getCodigo());
					}
					if (indMin.equals("1")){
						detalle2 = reporteDAO.findPapeletasLE(dbpool, fechaIni,
								fechaFin, quiebre.getCodigo());
					}
					//EBV Prioridad 1 Solo mostrar si tiene Labor Excepcional
					mapa = new HashMap();
					mapa.put("Licencias",detalle1);
					mapa.put("Papeletas",detalle2);
					if ((detalle != null ) && (detalle.size() > 0)) {

					//insertamos el detalle del quiebre
					quiebre.setDetalle(detalle);
					quiebre.setMap(mapa);
					//insertamos el quiebre al reporte
					reporte.add(quiebre);
					}
				}

			}
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return reporte;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList compensaciones(String dbpool, String fechaIni,
			String fechaFin, String criterio, String valor, String tQuiebre,
			HashMap seguridad) throws IncompleteConversationalState,
			RemoteException {

		ArrayList reporte = null;
		BeanMensaje beanM = new BeanMensaje();
		try {

			ReporteDAO reporteDAO = new ReporteDAO();
			
			//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
			//ICAPUNAY-MEMO 32-4F3100-2013
			/*HashMap roles = (HashMap) seguridad.get("roles");
	    	log.debug("compensaciones-roles: "+roles);
	    	if (roles.get(Constantes.ROL_JEFE) != null) {
	    		log.debug("compensaciones-seguridad antes: "+seguridad);			
				String uoAO = (String) seguridad.get("uoAO");
				seguridad.put("uoSeg", findUuooJefe(uoAO)+"%");//4EB2%
				log.debug("compensaciones-seguridad final: "+seguridad);
	    	}*/			
			//ICAPUNAY-MEMO 32-4F3100-2013
	    	//ICAPUNAY - PAS20165E230300132

			ArrayList lista = reporteDAO
					.joinWithT02T12T99findPersonalByCriterioValor(dbpool,
							criterio, valor, seguridad);

			if (lista != null) {

				reporte = new ArrayList();
				BeanReporte quiebre = null;
				ArrayList detalle = null;
				
				for (int i = 0; i < lista.size(); i++) {

					quiebre = (BeanReporte) lista.get(i);

					//Compensaciones gozadas
					if (tQuiebre.equals("0")) {
						detalle = reporteDAO.findCompensaciones(dbpool,
								fechaIni, fechaFin, quiebre.getCodigo());
					}
					//Pendientes de goce
					if (tQuiebre.equals("1")) {
						detalle = reporteDAO.findCompensacionesPendientes(
								dbpool, quiebre.getCodigo());
					}

					//EBV Prioridad 1 Solo mostrar si tiene Compensaciones 
					
					if ((detalle != null ) && (detalle.size() > 0)) {

					//insertamos el detalle del quiebre
					quiebre.setDetalle(detalle);
					//insertamos el quiebre al reporte
					reporte.add(quiebre);
					}
				}

			}
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return reporte;
	}

	//ASANCHEZZ 20100424
	/** 
	 * Metodo encargado de obtener el reporte de Resumen Mensual
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param params
	 * @return
	 * @throws RemoteException
	 */	
	public List resumenMensual(Map params) 
	throws IncompleteConversationalState, RemoteException {

/*
public ArrayList resumenMensual(String dbpool, String periodo,
	String criterio, String valor, HashMap seguridad)
	throws IncompleteConversationalState, RemoteException {
 */
	List reporte = null;
//FIN

	BeanMensaje beanM = new BeanMensaje();

	//ASANCHEZZ 20100424
	String dbpool = (String)params.get("dbpool");
	String periodo = (String)params.get("periodo");
	//String criterio = (String)params.get("criterio");
	//String valor = (String)params.get("valor");
	//Map seguridad = (HashMap)params.get("seguridad");
	//FIN

	try {
		
		//ASANCHEZZ 20100424
		MantenimientoFacadeHome facadeMantHome = (MantenimientoFacadeHome) sl.getRemoteHome(
				MantenimientoFacadeHome.JNDI_NAME,
				MantenimientoFacadeHome.class);
		MantenimientoFacadeRemote facadeMantRemote = facadeMantHome.create();
		String tipoDL = (params.get("regimen") != null ) ? params.get("regimen").toString():"";
		boolean ejecutarReporte = true;
		boolean periodoCerradoCAS = false;
		boolean periodoCerradoModFormativa = false; //JVILLACORTA 16/05/2011 - AOM:MODALIDADES FORMATIVAS
		//Si el reporte es para los CAS, valido que el periodo NO este cerrado
		if(tipoDL.equals("1")){//1057
			Map mapa = new HashMap();
			mapa.put("dbpool", dbpool);
			mapa.put("periodo", periodo);
			periodoCerradoCAS = facadeMantRemote.periodoCerradoCAS(mapa);
			//if(periodoCerradoCAS)ejecutarReporte = false;
			if(periodoCerradoCAS)ejecutarReporte = true; //JVILLACORTA 20/10/2011 - A solicitud de RLAZARTE y JCASTRO
		}
		//JVILLACORTA 16/05/2011 - AOM:MODALIDADES FORMATIVAS
		if(tipoDL.equals("2")){ 
			Map mapa = new HashMap();
			mapa.put("dbpool", dbpool);
			mapa.put("periodo", periodo);
			periodoCerradoModFormativa = facadeMantRemote.periodoCerradoModFormativa(mapa);
			//if(periodoCerradoModFormativa)ejecutarReporte = false;
			if(periodoCerradoModFormativa)ejecutarReporte = true; //JVILLACORTA 20/10/2011 - A solicitud de RLAZARTE y JCASTRO
		}
		//FIN - JVILLACORTA 16/05/2011 - AOM:MODALIDADES FORMATIVAS
		if (ejecutarReporte){
			ReporteDAO reporteDAO = new ReporteDAO();
			/*
			ArrayList lista = reporteDAO.joinWithT02T12T99findPersonalTotalUOPeriodoByCriterioValor(dbpool,
							criterio, valor, seguridad, periodo);
			*/
			DataSource dcsp = sl.getDataSource("java:comp/env/jdbc/dcsp");
			T02DAO t02DAO = new T02DAO(dcsp);
			List listaAux = t02DAO.buscarPersonal(params);
			List lista = new ArrayList();

			//lo que habia en ReporteDAO - joinWithT02T12T99findPersonalTotalUOPeriodoByCriterioValor, pero estandarizado
			Map mapa = new HashMap();
			BeanReporte a = null;
			StringBuffer texto = null;
			for(int i = 0; i < listaAux.size(); i++){
				mapa = (HashMap)listaAux.get(i);
				a = new BeanReporte();
				texto = new StringBuffer(mapa.get("t02cod_pers").toString().trim()).append(" - ")
				.append(mapa.get("t02ap_pate").toString().trim()).append(" ")
				.append(mapa.get("t02ap_mate").toString().trim()).append(", ")
				.append(mapa.get("t02nombres").toString().trim());
				a.setCodigo(mapa.get("t02cod_pers").toString().trim());
				a.setUnidad(mapa.get("t02cod_uorg").toString().trim());
				a.setNombre(texto.toString().trim());
				lista.add(a);
			}			
			//
			
		//FIN
			
			if (lista != null) {

				reporte = new ArrayList();
				BeanReporte quiebre = null;
				HashMap map = null;
				for (int i = 0; i < lista.size(); i++) {

					quiebre = (BeanReporte) lista.get(i);

					map = new HashMap();

					float tInasistencia = reporteDAO
							.findTotalDescuentoMovimiento(dbpool, periodo,
									quiebre.getCodigo(),
									Constantes.MOV_INASISTENCIA, "", true);
					float tTardanza = reporteDAO.findTotalDescuentoMovimiento(
							dbpool, periodo, quiebre.getCodigo(),
							Constantes.MOV_TARDANZA_EFECTIVA, "", true);
					
					float tPermiso = 0; //JVV - 13/06/2011
					if(tipoDL.equals("2")){ //JVV - 13/06/2011
						tPermiso = reporteDAO.findTotalDescuentoMovimiento(
								dbpool, periodo, quiebre.getCodigo(), Constantes.MOV_PERMISO_NO_SUBVENCIONADO,
								"", true);
					} else { 
						tPermiso = reporteDAO.findTotalDescuentoMovimiento(
								dbpool, periodo, quiebre.getCodigo(), "",
								Constantes.TIPO_MOV_PAPELETA, true);
					}
					
					float tLicencia = reporteDAO.findTotalDescuentoMovimiento(
							dbpool, periodo, quiebre.getCodigo(), "",
							Constantes.TIPO_MOV_LICENCIA, true);
					float tRefrigerio = reporteDAO
							.findTotalDescuentoMovimiento(dbpool, periodo,
									quiebre.getCodigo(),
									Constantes.MOV_EXCESO_REFRIGERIO, "", true);
					float tSalida = reporteDAO.findTotalDescuentoMovimiento(
							dbpool, periodo, quiebre.getCodigo(),
							Constantes.MOV_SALIDA_NO_AUTORIZADA, "", true);
					float tSuspension = reporteDAO
					.findTotalDescuentoMovimiento(dbpool, periodo,
							quiebre.getCodigo(),
							Constantes.MOV_SUSPENSION, "", true);
					tLicencia = tLicencia - tSuspension;
					
					float total = 0;//JVV - 11/06/2011
					if(tipoDL.equals("2")){//JVV - 11/06/2011
						total = (tInasistencia) + tTardanza + tPermiso//JVV - 11/06/2011
						+ (tLicencia) + tRefrigerio + tSalida + (tSuspension);//JVV - 11/06/2011
					} else {//JVV - 11/06/2011
						total = (tInasistencia * 480) + tTardanza + tPermiso
						+ (tLicencia * 480) + tRefrigerio + tSalida + (tSuspension * 480);
					}//JVV - 11/06/2011
					
					if(tipoDL.equals("2")){//JVV - 11/06/2011
						map.put("Inasistencia", "" + (int) (tInasistencia));//JVV - 11/06/2011
					} else {//JVV - 11/06/2011
						map.put("Inasistencia", "" + (int) (tInasistencia * 480));
					}	//JVV - 11/06/2011	
					
					map.put("Tardanza", "" + (int) tTardanza);
					map.put("Permiso", "" + (int) tPermiso);
					if(tipoDL.equals("2")){//JVV - 11/06/2011
						map.put("Licencia", "" + (int) (tLicencia));//JVV - 11/06/2011
					} else {//JVV - 11/06/2011
						map.put("Licencia", "" + (int) (tLicencia * 480 ));
					}//JVV - 11/06/2011
					
					map.put("Refrigerio", "" + (int) tRefrigerio);
					map.put("Salida", "" + (int) tSalida);
					if(tipoDL.equals("2")){//JVV - 11/06/2011
						map.put("Suspension", "" + (int) (tSuspension));//JVV - 11/06/2011
					} else {//JVV - 11/06/2011
						map.put("Suspension", "" + (int) (tSuspension * 480));
					}//JVV - 11/06/2011
					
					map.put("Total", "" + (int) total);

					//insertamos el quiebre al reporte
					quiebre.setMap(map);
					reporte.add(quiebre);
				}

			}
		//ASANCHEZZ 20100424
		}//cierre de if(ejecutarReporte)
		//
	} catch (Exception e) {
		log.error(e,e);
		beanM.setMensajeerror(e.getMessage());
		beanM.setMensajesol("Por favor intente nuevamente.");
		throw new IncompleteConversationalState(beanM);
	}

	return reporte;
}


	/** 
	 * Metodo encargado de obtener el reporte de Resumen Diario
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param params
	 * @return
	 * @throws RemoteException
	 */	
	public List resumenDiario(Map params)
			throws RemoteException {
	/*public ArrayList resumenDiario(String dbpool, String periodo,
			String criterio, String valor, HashMap seguridad)
			throws IncompleteConversationalState, RemoteException {*/

		List reporte = null;
		BeanMensaje beanM = new BeanMensaje();
		try {

			T1454DAO diarioDAO = new T1454DAO();
			//ReporteDAO reporteDAO = new ReporteDAO();
			T1276DAO periodoDAO = new T1276DAO();
			
			//JROJASR - 30/04/2010
			String dbpool = params.get("dbpool").toString();
			String periodo = params.get("periodo").toString();
			
			BeanPeriodo bPer = periodoDAO.findByCodigo(dbpool, periodo);
						
	          //EBENAVID -FORMATIVAS 16/06/2011        
			if ("1".equals(params.get("regimen").toString())) {
				bPer.setFechaIni(bPer.getFechaIniCAS());
				bPer.setFechaFin(bPer.getFechaFinCAS());
			} else{
				if ("2".equals(params.get("regimen").toString())) {
					bPer.setFechaIni(bPer.getFechaIniModForm());
					bPer.setFechaFin(bPer.getFechaFinModForm());
				}
			}
			
			//FIN EBENAVID -FORMATIVAS 16/06/2011
			String fIni = bPer.getFechaIni();
			String fFin = bPer.getFechaFin();
			
			
			//ASANCHEZZ 20100424
			MantenimientoFacadeHome facadeMantHome = (MantenimientoFacadeHome) sl.getRemoteHome(
					MantenimientoFacadeHome.JNDI_NAME,
					MantenimientoFacadeHome.class);
			MantenimientoFacadeRemote facadeMantRemote = facadeMantHome.create();
			String regimen = (params.get("regimen") != null ) ? params.get("regimen").toString():"";
			boolean ejecutarReporte = true;
			boolean periodoCerradoCAS = false;
			boolean periodoCerradoModFormativa = false; //JVILLACORTA 07/06/2011 - AOM:MODALIDADES FORMATIVAS
			//Si el reporte es para los CAS, valido que el periodo NO este cerrado
			if(regimen.equals("1")){//1057
				Map mapa = new HashMap();
				mapa.put("dbpool", dbpool);
				mapa.put("periodo", periodo);
				periodoCerradoCAS = facadeMantRemote.periodoCerradoCAS(mapa);
				//if(periodoCerradoCAS)ejecutarReporte = false;
				if(periodoCerradoCAS)ejecutarReporte = true; //JVILLACORTA 20/10/2011 - A solicitud de RLAZARTE y JCASTRO
			}
			
			//JVILLACORTA 07/06/2011 - AOM:MODALIDADES FORMATIVAS
			if(regimen.equals("2")){ 
				Map mapa = new HashMap();
				mapa.put("dbpool", dbpool);
				mapa.put("periodo", periodo);
				periodoCerradoModFormativa = facadeMantRemote.periodoCerradoModFormativa(mapa);
				//if(periodoCerradoModFormativa)ejecutarReporte = false;
				if(periodoCerradoModFormativa)ejecutarReporte = true; //JVILLACORTA 20/10/2011 - A solicitud de RLAZARTE y JCASTRO
				
			}
			//FIN - JVILLACORTA 07/06/2011 - AOM:MODALIDADES FORMATIVAS
			
			if (ejecutarReporte){
				//ReporteDAO reporteDAO = new ReporteDAO();
				/*
				ArrayList lista = reporteDAO.joinWithT02T12T99findPersonalTotalUOPeriodoByCriterioValor(dbpool,
								criterio, valor, seguridad, periodo);
				*/
				DataSource dcsp = sl.getDataSource("java:comp/env/jdbc/dcsp");
				T02DAO t02DAO = new T02DAO(dcsp);
				List listaAux = t02DAO.buscarPersonal(params);
				log.debug("listaAux:" + listaAux); //JVILLACORTA 20/06/2011 - AOM:MODALIDADES FORMATIVAS
				List lista = new ArrayList();

				//lo que habia en ReporteDAO - joinWithT02T12T99findPersonalTotalUOPeriodoByCriterioValor, pero estandarizado
				Map mapa = new HashMap();
				BeanReporte a = null;
				StringBuffer texto = null;
				for(int i = 0; i < listaAux.size(); i++){
					mapa = (HashMap)listaAux.get(i);
					log.debug("mapa:" + mapa);//JVILLACORTA 20/06/2011 - AOM:MODALIDADES FORMATIVAS
					a = new BeanReporte();
					texto = new StringBuffer(mapa.get("t02cod_pers").toString().trim()).append(" - ")
					.append(mapa.get("t02ap_pate").toString().trim()).append(" ")
					.append(mapa.get("t02ap_mate").toString().trim()).append(", ")
					.append(mapa.get("t02nombres").toString().trim());
					log.debug("t02cod_pers:" + mapa.get("t02cod_pers").toString().trim());//JVILLACORTA 20/06/2011 - AOM:MODALIDADES FORMATIVAS
					log.debug("t02cod_uorg:" + mapa.get("t02cod_uorg").toString().trim());//JVILLACORTA 20/06/2011 - AOM:MODALIDADES FORMATIVAS
					a.setCodigo(mapa.get("t02cod_pers").toString().trim());
					a.setUnidad(mapa.get("t02cod_uorg").toString().trim());
					a.setNombre(texto.toString().trim());
					lista.add(a);
				}			
				//
				
			//FIN
			
			
			//ArrayList lista = reporteDAO.joinWithT02T12T99findPersonalTotalByCriterioValor(dbpool, criterio, valor, seguridad, periodo);
			
			if (lista != null) {

				reporte = new ArrayList();
				BeanReporte quiebre = null;
				HashMap map = null;
				ArrayList detalle = null;
				BeanTipoMovimiento mov = null;
				for (int i = 0; i < lista.size(); i++) {

					quiebre = (BeanReporte) lista.get(i);
					map = new HashMap();

					String fAct = fIni;
					String fReal = bPer.getFechaIni();
					
					int frecuencia = 0;
					float total = 0;
					
					while (fAct.compareTo(fFin) <= 0) {

						float acumulado = 0;
						float total_dia = 0;
						detalle = diarioDAO.findByCodPersFechaPeriodo(dbpool, quiebre.getCodigo(), fReal, periodo);
						
						if (detalle != null) {
							for (int j = 0; j < detalle.size(); j++) {

								mov = (BeanTipoMovimiento) detalle.get(j);
								total_dia = mov.getTotal();
								
								String medida = mov.getMedida().trim();

								if (medida.equals(Constantes.MINUTO)) {
									acumulado += total_dia;
								}
								if (medida.equals(Constantes.HORA)) {
									acumulado += total_dia * 60;
								}
								if (medida.equals(Constantes.DIA)) {
									if(regimen.equals("2")){   //JVV - 11/06/2011
										acumulado += total_dia; //JVV - 11/06/2011
									} else { //JVV - 11/06/2011
										acumulado += total_dia * 60 * 8;
									} //JVV - 11/06/2011
									
								}
								if (medida.equals(Constantes.MES)) {
									acumulado += total_dia * 60 * 8 * 20;
								}
								if (medida.equals(Constantes.ANNO)) {
									acumulado += total_dia * 60 * 8 * 20 * 240;
								}
							}
						}

						
						if (fReal.indexOf("/")>3){ 
							fReal = fReal.substring(8,10) + "/" + fReal.substring(5,7) + "/" + fReal.substring(0,4) ;
						}
						
						if (acumulado > 0) {
							total += acumulado;
							frecuencia++;
						}

						map.put(fReal, "" + (int) acumulado);

						fReal = Utiles.dameFechaSiguiente(fReal, 1);
						fAct = Utiles.toYYYYMMDD(fReal);
					
						
					}

					map.put("frecuencia", "" + frecuencia);
					map.put("total", "" + (int) total);

					//insertamos el quiebre al reporte
					quiebre.setMap(map);
					reporte.add(quiebre);
				}
				
			}//
			
			}
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return reporte;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList vacacionesGozadas(String dbpool, String regimen, String fechaIni,
			String fechaFin, String criterio, String valor, String mayorA,
			HashMap seguridad) throws IncompleteConversationalState,
			RemoteException {

		ArrayList reporte = null;
		BeanMensaje beanM = new BeanMensaje();
		try {

			ReporteDAO reporteDAO = new ReporteDAO();
			T1281DAO vacDAO = new T1281DAO();
			
			//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
			//ICAPUNAY-MEMO 32-4F3100-2013
			/*HashMap roles = (HashMap) seguridad.get("roles");
	    	log.debug("vacacionesGozadas-roles: "+roles);	
	    	if (roles.get(Constantes.ROL_JEFE) != null) {
	    		log.debug("vacacionesGozadas-seguridad antes: "+seguridad);			
				String uoAO = (String) seguridad.get("uoAO");
				seguridad.put("uoSeg", findUuooJefe(uoAO)+"%");//4EB2%
				log.debug("vacacionesGozadas-seguridad final: "+seguridad);
	    	}*/			
			//ICAPUNAY-MEMO 32-4F3100-2013
	    	//ICAPUNAY - PAS20165E230300132

			ArrayList lista = reporteDAO
			.joinWithT02T12T99findPersonalByCriterioValorInactMF(dbpool,
					regimen, criterio, valor, seguridad);//JVILLACORTA 01/07/2011 - AOM:MODALIDADES FORMATIVAS - regimen
			//.joinWithT02T12T99findPersonalByCriterioValorInact(dbpool,
					//criterio, valor, seguridad);
					//.joinWithT02T12T99findPersonalByCriterioValor(dbpool,
						//	criterio, valor, seguridad);

			if (lista != null) {

				reporte = new ArrayList();
				BeanReporte quiebre = null;
				ArrayList detalle = null;
				//PRAC-ASANCHEZ 17/06/2009
				//detalle1 incluira las vacaciones de 1 dia (es decir registros que tiene finicio=ffin, pero que 
				//han sido gozados.
				BeanReporte vac = new BeanReporte();
				int j;
				//
				ArrayList detalleVac = null;
				
				
				for (int i = 0; i < lista.size(); i++) {
					HashMap mapa = new HashMap();
					quiebre = (BeanReporte) lista.get(i);

					//Vacaciones gozadas
					detalle = reporteDAO.findVacacionesGozadas(dbpool,
							fechaIni, fechaFin, quiebre.getCodigo(), Integer
									.parseInt(mayorA), false, false);

					//PRAC-ASANCHEZ 17/06/2009
/*					ArrayList detalle1 = new ArrayList();
					//RECORRIENDO LA LISTA:
					for(j = 0; j < detalle.size(); j++){
						vac = (BeanReporte)detalle.get(j);
						detalle1.add(vac);
						if(vac.getCodigo().toString().equals(Constantes.VACACION_INDEMNIZADA)){
							j++;	
						}
					}*/
					//
					
					//EBV Prioridad 1 Solo mostrar si tiene Vacaciones Gozadas
					
					//PRAC-ASANCHEZ 17/06/2009
					//CAMBIO DE DETALLE POR DETALLE1
					if ((detalle != null ) && (detalle.size() > 0)) {
						detalleVac = vacDAO.findByCodPersSaldoAnnoIniAnnoFin(dbpool,
								quiebre.getCodigo(), true, Utiles.dameAnho(fechaIni), Utiles.dameAnho(fechaFin));
					//insertamos el detalle del quiebre
						mapa.put("vacacion",detalleVac);
					quiebre.setMap(mapa);
					quiebre.setDetalle(detalle);
					//log.debug("MAPA "+ mapa);
					//log.debug("QUIEBRE " + quiebre);
					//insertamos el quiebre al reporte
					reporte.add(quiebre);
					}
					//
				}

			}
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return reporte;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList vacacionesPendientes(String dbpool, String regimen, String anhoIni,//JVILLACORTA 28/06/2011 - AOM:MODALIDADES FORMATIVAS - String regimen
			String anhoFin, String criterio, String valor, String mayorA,
			HashMap seguridad) throws IncompleteConversationalState,
			RemoteException {

		ArrayList reporte = null;
		BeanMensaje beanM = new BeanMensaje();
		try {

			ReporteDAO reporteDAO = new ReporteDAO();
			T1281DAO vacDAO = new T1281DAO();

			ArrayList lista = reporteDAO
					.joinWithT02T12T99findPersVacaPendientes(dbpool, regimen, anhoIni,//JVILLACORTA 28/06/2011 - AOM:MODALIDADES FORMATIVAS
							anhoFin, criterio, valor, Integer.parseInt(mayorA),
							seguridad);

			if (lista != null) {

				reporte = new ArrayList();
				BeanReporte quiebre = null;
				ArrayList detalle = null;
				
				for (int i = 0; i < lista.size(); i++) {

					quiebre = (BeanReporte) lista.get(i);

					//Vacaciones gozadas
					detalle = vacDAO.findByCodPersSaldoAnnoIniAnnoFin(dbpool,
							quiebre.getCodigo(), true, anhoIni, anhoFin);

					//EBV Prioridad 1 Solo mostrar si tiene Vacaciones Pendientes
					
					if ((detalle != null ) && (detalle.size() > 0)) {

					//insertamos el detalle del quiebre
					quiebre.setDetalle(detalle);
					//insertamos el quiebre al reporte
					reporte.add(quiebre);
					}
				}

			}
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return reporte;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList vacacionesPendientesYProg(String dbpool, String anhoIni,
			String anhoFin, String criterio, String valor, String mayorA,
			HashMap seguridad) throws IncompleteConversationalState,
			RemoteException {

		ArrayList reporte = null;
		BeanMensaje beanM = new BeanMensaje();
		try {

			ReporteDAO reporteDAO = new ReporteDAO();
			
			//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
			//ICAPUNAY-MEMO 32-4F3100-2013
			/*HashMap roles = (HashMap) seguridad.get("roles");
	    	log.debug("vacacionesPendientesYProg-roles: "+roles);
	    	if (roles.get(Constantes.ROL_JEFE) != null) {
	    		log.debug("vacacionesPendientesYProg-seguridad antes: "+seguridad);			
				String uoAO = (String) seguridad.get("uoAO");
				seguridad.put("uoSeg", findUuooJefe(uoAO)+"%");//4EB2%
				log.debug("vacacionesPendientesYProg-seguridad final: "+seguridad);
	    	}*/			
			//ICAPUNAY-MEMO 32-4F3100-2013
	    	//ICAPUNAY - PAS20165E230300132

			ArrayList lista = reporteDAO
					.joinWithT02T12T99findPersonalByCriterioValor(dbpool,
							criterio, valor, seguridad);
			ArrayList detalle = null;

			BeanReporte bRepo1 = null;
			BeanReporte bRepo2 = null;

			ArrayList deta1 = null;
			ArrayList deta2 = null;

			if (lista != null) {

				reporte = new ArrayList();
				BeanReporte quiebre = null;
				
				for (int i = 0; i < lista.size(); i++) {

					quiebre = (BeanReporte) lista.get(i);

					bRepo1 = new BeanReporte();
					bRepo2 = new BeanReporte();

					//Periodos pendientes
					deta1 = reporteDAO.findAnhosPendByCodPersSaldoAnno(dbpool,
							quiebre.getCodigo(), true, anhoIni, anhoFin);
					bRepo1.setDetalle(deta1);

					//Vacaciones programadas pendientes
					deta2 = reporteDAO.findVacPendByCodPersAnhoDias(dbpool,
							quiebre.getCodigo(), Utiles.obtenerFechaActual(),
							Integer.parseInt(mayorA));
					if (deta2 != null) {
						bRepo2.setDetalle(deta2);
					}

					//Creamos el array list de detalle del bean principal.
					detalle = new ArrayList();
					detalle.add(bRepo1);
					detalle.add(bRepo2);

					//EBV Prioridad 1 Solo mostrar si tiene Marcaciones
					
					if (bRepo2 != null ) {

					//Insertamos el detalle del quiebre
					quiebre.setDetalle(detalle);

					//Insertamos el quiebre al reporte
					reporte.add(quiebre);
					}
				}

			}
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return reporte;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList vacacionesCompensadas(String dbpool, String fechaIni,
			String fechaFin, String criterio, String valor, HashMap seguridad)
			throws IncompleteConversationalState, RemoteException {

		ArrayList reporte = null;
		BeanMensaje beanM = new BeanMensaje();
		try {

			ReporteDAO reporteDAO = new ReporteDAO();
			
			//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
			//ICAPUNAY-MEMO 32-4F3100-2013
			/*HashMap roles = (HashMap) seguridad.get("roles");
	    	log.debug("vacacionesCompensadas-roles: "+roles);	
	    	if (roles.get(Constantes.ROL_JEFE) != null) {
	    		log.debug("vacacionesCompensadas-seguridad antes: "+seguridad);			
				String uoAO = (String) seguridad.get("uoAO");
				seguridad.put("uoSeg", findUuooJefe(uoAO)+"%");//4EB2%
				log.debug("vacacionesCompensadas-seguridad final: "+seguridad);
	    	}*/			
			//ICAPUNAY-MEMO 32-4F3100-2013
	    	//ICAPUNAY - PAS20165E230300132

			ArrayList lista = reporteDAO
					.joinWithT02T12T99findPersonalByCriterioValor(dbpool,
							criterio, valor, seguridad);

			//ArrayList detalle = null;

			if (lista != null) {

				reporte = new ArrayList();
				BeanReporte quiebre = null;
				ArrayList detalle = null;
				
				for (int i = 0; i < lista.size(); i++) {

					quiebre = (BeanReporte) lista.get(i);

					//Vacaciones Compenzadas
					detalle = reporteDAO.findCompVacByCodPersFFiniFFin(dbpool, quiebre
									.getCodigo(), fechaIni, fechaFin);

					//EBV Prioridad 1 Solo mostrar si tiene Vacaciones Compensadas
					
					if ((detalle != null ) && (detalle.size() > 0)) {

					//insertamos el detalle del quiebre
					quiebre.setDetalle(detalle);
					//insertamos el quiebre al reporte
					reporte.add(quiebre);
					}
				}

			}
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return reporte;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList resumenVacacionesUOrg(String dbpool, String fechaIni,
			String fechaFin, HashMap seguridad)
			throws IncompleteConversationalState, RemoteException {

		ArrayList reporte = null;
		BeanMensaje beanM = new BeanMensaje();
		try {

			T12DAO uOrgDAO = new T12DAO();
			ReporteDAO reporteDAO = new ReporteDAO();
			int cantProgramadas = 0;
			int cantEfectivas = 0;
			int cantCompensadas = 0;

			ArrayList lista = uOrgDAO.findByCodDesc(dbpool, "", "", seguridad);

			if (lista != null) {

				reporte = new ArrayList();
				BeanT12 bUOrg = null;
				ArrayList acumulados = null;
				BeanReporte bRepo = null;
				for (int i = 0; i < lista.size(); i++) {

					bUOrg = (BeanT12) lista.get(i);

					//Obtenemos el resumen de vacaciones de la Unidad.
					acumulados = reporteDAO.findAcumVacByCodUOrgFFiniFFin(dbpool, bUOrg
									.getT12cod_uorgan(), fechaIni, fechaFin);

					cantProgramadas = 0;
					cantEfectivas = 0;
					cantCompensadas = 0;

					if (acumulados != null && acumulados.size() > 0) {
						for (int j = 0; j < acumulados.size(); j++) {
							bRepo = (BeanReporte) acumulados.get(j);

							if (bRepo.getCodigo().trim().equals(
									Constantes.VACACION_PROGRAMADA)) {
								cantProgramadas += bRepo.getCantidad();
							}

							if (bRepo.getCodigo().trim().equals(
									Constantes.VACACION)
									|| bRepo.getCodigo().trim().equals(
											Constantes.VACACION_ESPECIAL)) {
								cantEfectivas += bRepo.getCantidad();
							}

							if (bRepo.getCodigo().trim().equals(
									Constantes.VACACION_VENTA)) {
								cantCompensadas += bRepo.getCantidad();
							}
						}
					}

					//Insertamos los datos del bean
					BeanReporte quiebre = new BeanReporte();
					quiebre.setCodigo(bUOrg.getT12cod_uorgan());
					quiebre.setNombre(bUOrg.getT12descr_uorg());
					quiebre.setHora("" + cantProgramadas);
					quiebre.setHora1("" + cantEfectivas);
					quiebre.setHora2("" + cantCompensadas);

					//insertamos el quiebre al reporte
					reporte.add(quiebre);
				}

			}
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return reporte;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList vacacionesGoceEfectivo(String dbpool, String fechaIni,
			String fechaFin, String criterio, String valor, String mayorA,
			HashMap seguridad) throws IncompleteConversationalState,
			RemoteException {

		ArrayList reporte = null;
		BeanMensaje beanM = new BeanMensaje();
		try {

			ReporteDAO reporteDAO = new ReporteDAO();
			//EBV 23/05/2006 se cambia joinWithT02T12T99findPersonalByCriterioValor por joinWithT02T12T99findPersonalByCriterioValorInact
			//Personal Inactivo
			ArrayList lista = reporteDAO
					.joinWithT02T12T99findPersonalByCriterioValorInact(dbpool,
							criterio, valor, seguridad);
			if(log.isDebugEnabled())log.debug("lista: " + lista);

			if (lista != null) {

				reporte = new ArrayList();
				ArrayList detalle = null;
				BeanReporte quiebre = null;
				
				for (int i = 0; i < lista.size(); i++) {

					quiebre = (BeanReporte) lista.get(i);

					//Vacaciones con goce efectivo
					detalle = reporteDAO.findVacacionesGozadas(dbpool,
							fechaIni, fechaFin, quiebre.getCodigo(), Integer
									.parseInt(mayorA), true, true);
					//if(log.isDebugEnabled())log.debug("detalle: " + detalle);

					if (detalle != null && detalle.size() > 0) {
						//insertamos el detalle del quiebre
						quiebre.setDetalle(detalle);
						//insertamos el quiebre al reporte
						
					}
					reporte.add(quiebre);
				}

			}
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return reporte;
	}
	

/* JRR - FUENTES COMSA PROGRAMACION - 20/05/2010 */	
	/**
	 * Metodo encargado de listar las vacaciones de Goce Efectivo
	 * @param Map parametros
	 * @return List
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public List vacacionesGoceEfectivo(Map parametros) throws  FacadeException {
		String fechaIni = (String)parametros.get("fechaIni");
		String fechaFin = (String)parametros.get("fechaFin");
		String criterio = (String)parametros.get("criterio");
		String valor = (String)parametros.get("valor");
		String mayorA = (String)parametros.get("mayorA");
		Map seguridad = (HashMap)parametros.get("seguridad");
		
		List reporte = null;
		try {			
			if(log.isDebugEnabled()){log.debug("en elFacade repo");}
			DataSource dbpool_sp = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dcsp");
			pe.gob.sunat.rrhh.asistencia.dao.ReporteDAO reporteDAO = new pe.gob.sunat.rrhh.asistencia.dao.ReporteDAO(dbpool_sp);
			
			
			//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
			//ICAPUNAY-MEMO 32-4F3100-2013			
			/*HashMap roles = (HashMap) seguridad.get("roles");
	    	log.debug("vacacionesGoceEfectivo-roless: "+roles);    	
	    	if ("usuario_Jefe".equals(seguridad.get("perfil_usuario")!=null?seguridad.get("perfil_usuario").toString():"") || roles.get(Constantes.ROL_JEFE)!=null) {
	    		log.debug("vacacionesGoceEfectivo-seguridad antess: "+seguridad);			
				String uoAO = (String) seguridad.get("uoAO");
				seguridad.put("uuoo", findUuooJefe(uoAO)+"%");//4EB2%
				log.debug("vacacionesGoceEfectivo-seguridad finall(uuoo): "+seguridad);
	    	}*/			
			//ICAPUNAY-MEMO 32-4F3100-2013
	    	//ICAPUNAY - PAS20165E230300132
			
			//Personal Inactivo
			Map datos = new HashMap();
			datos.put("criterio", criterio);
			datos.put("valor", valor);
			datos.put("seguridad", seguridad);
						
			List lista = reporteDAO.joinWithT02T12T99findPersonalByCriterioValorInact(datos);
			log.debug("lista: " + lista);
			
			if (lista != null) {
				reporte = new ArrayList();
				List detalle = null;
				List detalleVac = new ArrayList();
				List detalleProg = new ArrayList();
				List detalleSald = new ArrayList();
				Map quiebre = null;
				Map quiebreTemp = null;
				String codigoTemp = "";
				String t02f_ingsun = "";
				String uuoo = "";
				StringBuffer texto = new StringBuffer("");
				if(log.isDebugEnabled()){log.debug("en el fr3");}
				for (int i = 0; i < lista.size(); i++) {
					quiebre = (HashMap) lista.get(i);
					if(log.isDebugEnabled()){log.debug("en el quiebre: " + quiebre);}
					t02f_ingsun = quiebre.get("t02f_ingsun_desc")!=null ? String.valueOf(quiebre.get("t02f_ingsun_desc")):"";
					
					
					if(log.isDebugEnabled()){log.debug("en el fr4");}
					texto =  new StringBuffer(String.valueOf(quiebre.get("t02cod_pers")!=null?quiebre.get("t02cod_pers"):"").trim())
							.append( " - ")
					.append(String.valueOf(quiebre.get("t02ap_pate")!=null?quiebre.get("t02ap_pate"):"").trim()).append( " ")
					.append(String.valueOf(quiebre.get("t02ap_mate")!=null?quiebre.get("t02ap_mate"):"").trim()).append( ", ")
					.append(String.valueOf(quiebre.get("t02nombres")!=null?quiebre.get("t02nombres"):"").trim()).append( " (")
					.append(t02f_ingsun).append( ")  -  ")	
					.append(String.valueOf(quiebre.get("t99descrip")).trim()).append( " / ")
					.append(String.valueOf(quiebre.get("t12des_corta")).trim());
					
					quiebre.put("nombre", texto.toString().trim());
					uuoo = String.valueOf(quiebre.get("t02cod_uorg")) != "" ? String.valueOf(quiebre.get("t02cod_uorg"))
							: String.valueOf(quiebre.get("t02cod_uorgl"));
					quiebre.put("codigo", (String)quiebre.get("t02cod_pers"));
					quiebre.put("unidad", uuoo.trim().concat( " - ")
							.concat(String.valueOf(quiebre.get("t12des_uorga")).trim()));
					quiebre.put("fechaIni", fechaIni);
					quiebre.put("fechaFin", fechaFin);
					quiebre.put("numDias", mayorA);
					//Vacaciones con goce efectivo
					quiebre.put("incluyePend","true");
					quiebre.put("incluyeProg","true");
					detalle = reporteDAO.findVacacionesGozadas(quiebre);
					log.debug("detalle: " + detalle);
					//Para Saldo
					
					
					quiebre.put("anhoIni", "");
					quiebre.put("anhoIni", "");
					quiebre.put("saldoFavor","true");
					detalleSald = reporteDAO.findAnhosPendByCodPersSaldoAnno(quiebre);
					log.debug("detalleSald: " + detalleSald);
					if (detalle != null && detalle.size() > 0) {
						//vamos a armar el detalle:
						for (int j = 0; j < detalle.size(); j++) {
							quiebreTemp = (HashMap) detalle.get(j);
							codigoTemp = String.valueOf(quiebreTemp.get("licencia")).trim();
							if(log.isDebugEnabled()) { log.debug("quiebreTemp---  " + quiebreTemp);}
							if(log.isDebugEnabled()) { log.debug("codigoTemp---  " + codigoTemp + "--");}
							if (codigoTemp!=null){
								if(log.isDebugEnabled()) { log.debug("codigoTemp2---  " + codigoTemp);}
								if (codigoTemp.equals(constantes.leePropiedad("VACACION")) 
										|| codigoTemp.equals(constantes.leePropiedad("VACACION_VENTA"))){
									detalleVac.add(quiebreTemp);
									if(log.isDebugEnabled()) { log.debug("VAC---  " + codigoTemp);}
									
								}else if(codigoTemp.equals(constantes.leePropiedad("VACACION_PROGRAMADA"))){
									detalleProg.add(quiebreTemp);
									if(log.isDebugEnabled()) { log.debug("PROG---  " + codigoTemp);}
									
								}
							}
							
						}
						
						//insertamos el detalle del quiebre						
						quiebre.put("detalleVac", detalleVac);
						if(log.isDebugEnabled()) { log.debug("Lista VAC---  " + detalleVac);}
						quiebre.put("detalleProg", detalleProg);
						if(log.isDebugEnabled()) { log.debug("Lista PROG---  " + detalleProg);}
						//inicializamos para limpiar lista
						detalleVac = new ArrayList();
						detalleProg = new ArrayList();
					}
					quiebre.put("detalleSald", detalleSald);
					//insertamos el quiebre al reporte
					reporte.add(quiebre);
				}
				
			}
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
		
		return reporte;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */	
	public ArrayList inasistencias(String dbpool, String regimen, String criterio,
			String valor, String fechaIni, String fechaFin, String numDias,
			HashMap seguridad) throws IncompleteConversationalState,
			RemoteException {
	//log.debug("============================");
		//log.debug("dentro del facade...metodo inasistencias");
		//log.debug("============================");
		//log.debug("parametros recibidos");
		log.debug("inasistencias("+dbpool+","+ regimen+","+ criterio+","+valor+","+fechaIni+","+fechaFin+","+numDias+","+seguridad);
		
		ArrayList reporte = null;
		BeanMensaje beanM = new BeanMensaje();
		try {							
			
			//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
			//ICAPUNAY-MEMO 32-4F3100-2013
			/*HashMap roles = (HashMap) seguridad.get("roles");
	    	log.debug("inasistencias-roles: "+roles);
	    	if (roles.get(Constantes.ROL_JEFE) != null) {
	    		log.debug("inasistencias-seguridad antes: "+seguridad);			
				String uoAO = (String) seguridad.get("uoAO");
				seguridad.put("uoSeg", findUuooJefe(uoAO)+"%");//4EB2%
				log.debug("inasistencias-seguridad final: "+seguridad);
	    	}*/			
			//ICAPUNAY-MEMO 32-4F3100-2013
	    	//ICAPUNAY - PAS20165E230300132
			
			if (criterio.equals("1")) {//JVV - por unidad organizacional					
				pe.gob.sunat.rrhh.dao.T12DAO uOrgDAO = new pe.gob.sunat.rrhh.dao.T12DAO(dbpool);
				List listaUorgan = null;
				Map p = new HashMap();//parametros de busqueda t12uorga
				
				p.put("criterio", criterio);
				p.put("valor", valor);
				p.put("seguridad", seguridad);
				p.put("estado", "1");//estado activo de unidades organizacionales
				//log.debug("============================");
				//log.debug("al momento de invocar al metodo findByCodSeguridad de la T12DAO para validar qeu uorgna puede visualizar el usuario");
				//log.debug("los parametros.. P="+p);
				listaUorgan = uOrgDAO.findByCodSeguridad(p);
				if (log.isDebugEnabled()) log.debug("listaUorgan: "+listaUorgan);
				
				if (listaUorgan != null) {
					/**se instancio de esta forma por que hay conflicto al tener otro dao con el mismo nombre en la clase*/
					pe.gob.sunat.rrhh.asistencia.dao.T1454DAO t1454dao = new pe.gob.sunat.rrhh.asistencia.dao.T1454DAO(dbpool);
					List listaDetalle = null;//se refiere al resultado de la busqueda por cada unidad organizacional de lista
					Map quiebre = null;//se refiere a los datos de la unidad organizacional mas el detalle
					reporte = new ArrayList();//resultado de tode el reporte,contiene varios quiere				
					Map params = null;
					Map mt12 = null;
					Map map_aux = null;
					List listaDetalleAux = new ArrayList();
					for (int i = 0; i < listaUorgan.size(); i++) {
						mt12 = (HashMap) listaUorgan.get(i);
						listaDetalleAux.clear();
						/*parametros para la busqueda*/
						params = new HashMap();
						params.put("cod_uorgan", mt12.get("t12cod_uorga"));
						params.put("regimen", regimen);
						params.put("criterio", criterio);
						params.put("fechaIni", fechaIni);
						params.put("fechaFin", fechaFin);
						params.put("numDias", numDias);
						params.put("seguridad", seguridad);						
						
						//ICAPUNAY 27/07/2011 - CORRECCION PARA REPORTE DE INASISTENCIAS DE FORMATIVOS (ERROR: DIAS EN DECIMALES)
						if(regimen.equals("esModFormativa")){
							listaDetalle = t1454dao.findByFechas_Formativos(params);//buscar inasistencias de MF en la tabla t1454
						}else{
							listaDetalle = t1454dao.findByFechas(params);//buscar inasistencias en la tabla t1454
						}
						//listaDetalle = t1454dao.findByFechas(params);//buscar inasistencia la tabla t1454 
						//FIN ICAPUNAY 27/07/2011 - CORRECCION PARA REPORTE DE INASISTENCIAS DE FORMATIVOS (ERROR: DIAS EN DECIMALES)
						
						if (log.isDebugEnabled()) log.debug("listaDetalle SIZE: " + listaDetalle.size());
						
						for(int j = 0; j < listaDetalle.size(); j++){
							map_aux = (HashMap) listaDetalle.get(j);
							if (log.isDebugEnabled()) log.debug("map_aux: " + map_aux);
							if (map_aux.get("totales")!= null) { 
								/*ICAPUNAY 27/07/2011 - CORRECCION PARA REPORTE DE INASISTENCIAS DE FORMATIVOS (ERROR: DIAS EN DECIMALES)
								//JVILLACORTA 13/06/2011 - AOM:MODALIDADES FORMATIVAS
								if(regimen.equals("esModFormativa")){									
									//cantidad de horas de turno de trabajo del dia de hoy
			                        T1270DAO tpDAO = new T1270DAO();
			                        int horas = 0;
			                        int minutos = 0;
			                        BeanTurnoTrabajo turnoTrab = tpDAO.joinWithT45ByCodFecha(dbpool, (String)map_aux.get("t02cod_pers"), map_aux.get("fecha_desc").toString());

			                        if (turnoTrab != null) {
			                             horas = Math.round(Utiles.obtenerHorasDifDia(turnoTrab.getHoraIni(), turnoTrab.getHoraFin()));
			                             minutos = 60 * horas;
			                        }                       
			                        
			                        float total_dia = new Float (map_aux.get("totales").toString()).floatValue();
			                        map_aux.put("totales", "" + (total_dia / minutos));
			                    //FIN - JVILLACORTA 13/06/2011 - AOM:MODALIDADES FORMATIVAS
									//float total_dia = new Float (map_aux.get("totales").toString()).floatValue();																		
									//map_aux.put("totales", "" + (total_dia / 480));
									//if (log.isDebugEnabled()) log.debug("total dias formativas: " + (total_dia / 480));
									
									listaDetalleAux.add(map_aux);
								} else {
									listaDetalleAux.add(map_aux);
								}							
								*///FIN ICAPUNAY 27/07/2011 - CORRECCION PARA REPORTE DE INASISTENCIAS DE FORMATIVOS (ERROR: DIAS EN DECIMALES)
								
								//ICAPUNAY 27/07/2011 - CORRECCION PARA REPORTE DE INASISTENCIAS DE FORMATIVOS (ERROR: DIAS EN DECIMALES)
								if (log.isDebugEnabled()) log.debug("total dias: " + map_aux.get("totales").toString());
								listaDetalleAux.add(map_aux);
								//FIN ICAPUNAY 27/07/2011 - CORRECCION PARA REPORTE DE INASISTENCIAS DE FORMATIVOS (ERROR: DIAS EN DECIMALES)
															
							}	
						}
						
						//if (log.isDebugEnabled()) log.debug("listaDetalleAux: " + listaDetalleAux);
						if (log.isDebugEnabled()) log.debug("listaDetalleAux SIZE: " + listaDetalleAux.size());
						
						listaDetalle = new ArrayList(listaDetalleAux);
						
						//Insertamos los valores
						quiebre = new HashMap();
						quiebre.put("cod_uorgan",mt12.get("t12cod_uorga"));
						quiebre.put("desc_uorgan",mt12.get("t12des_uorga"));
						quiebre.put("listaDetalle",listaDetalle); 
						//quiebre.put("cantRegistros",listaDetalleAux != null ? "" + listaDetalleAux.size(): "0");
	
						//insertamos el quiebre al reporte
						reporte.add(quiebre);
					}	
				}
			}  //JVV
			if (criterio.equals("0") || criterio.equals("4") || criterio.equals("3")) {					
					pe.gob.sunat.rrhh.asistencia.dao.T1454DAO t1454dao = new pe.gob.sunat.rrhh.asistencia.dao.T1454DAO(dbpool);					
					reporte = new ArrayList();
					Map params = null;
					Map map_aux = null;
					List listaDetalle = null;
					Map quiebre = null;
					List listaDetalleAux = new ArrayList();
					listaDetalleAux.clear();
					/*parametros para la busqueda*/
					params = new HashMap();					
					params.put("regimen", regimen);
					params.put("criterio", criterio);
					params.put("valor", valor);
					params.put("fechaIni", fechaIni);
					params.put("fechaFin", fechaFin);
					params.put("numDias", numDias);
					params.put("seguridad", seguridad);
					//params.put("dbpool", dbpool);
					
					//ICAPUNAY 27/07/2011 - CORRECCION PARA REPORTE DE INASISTENCIAS DE FORMATIVOS (ERROR: DIAS EN DECIMALES)
					if(regimen.equals("esModFormativa")){
						listaDetalle = t1454dao.findByFechas_Formativos(params);//buscar inasistencias de MF en la tabla t1454
					}else{
						listaDetalle = t1454dao.findByFechas(params);//buscar inasistencias en la tabla t1454
					}
					//listaDetalle = t1454dao.findByFechas(params);//buscar inasistencia la tabla t1454 
					//FIN ICAPUNAY 27/07/2011 - CORRECCION PARA REPORTE DE INASISTENCIAS DE FORMATIVOS (ERROR: DIAS EN DECIMALES)
					
					if (log.isDebugEnabled()) log.debug("listaDetalle SIZE: " + listaDetalle.size());
					
					for(int j = 0; j < listaDetalle.size(); j++){
						map_aux = (HashMap) listaDetalle.get(j);
						if (log.isDebugEnabled()) log.debug("map_aux: " + map_aux);
						if (map_aux.get("totales")!= null) {
							/*ICAPUNAY 27/07/2011 - CORRECCION PARA REPORTE DE INASISTENCIAS DE FORMATIVOS (ERROR: DIAS EN DECIMALES)
							//JVILLACORTA 13/06/2011 - AOM:MODALIDADES FORMATIVAS
							if(regimen.equals("esModFormativa")){																	
								//cantidad de horas de turno de trabajo del dia de hoy
		                        T1270DAO tpDAO = new T1270DAO();
		                        int horas = 0;
		                        int minutos = 0;
		                        BeanTurnoTrabajo turnoTrab = tpDAO.joinWithT45ByCodFecha(dbpool, (String)map_aux.get("t02cod_pers"), map_aux.get("fecha_desc").toString());

		                        if (turnoTrab != null) {
		                             horas = Math.round(Utiles.obtenerHorasDifDia(turnoTrab.getHoraIni(), turnoTrab.getHoraFin()));		                             
		                             minutos = 60 * horas;
		                             if (log.isDebugEnabled()) log.debug("minutos por turno: " + minutos);//BORRAR LOG
		                        }								
								
								float total_dia = new Float (map_aux.get("totales").toString()).floatValue();
								if (log.isDebugEnabled()) log.debug("Total minutos de inasistencias: " + String.valueOf(total_dia));//BORRAR LOG
								map_aux.put("totales", "" + (total_dia / minutos));
								if (log.isDebugEnabled()) log.debug("Total dias de inasistencias: " + String.valueOf(total_dia / minutos));//BORRAR LOG
						     //FIN - JVILLACORTA 13/06/2011 - AOM:MODALIDADES FORMATIVAS
								//float total_dia = new Float (map_aux.get("totales").toString()).floatValue();																	
								//map_aux.put("totales", "" + (total_dia / 480));
								//if (log.isDebugEnabled()) log.debug("total dias formativas: " + (total_dia / 480));
								listaDetalleAux.add(map_aux);
							} else {
								listaDetalleAux.add(map_aux);
							}
							*///FIN ICAPUNAY 27/07/2011 - CORRECCION PARA REPORTE DE INASISTENCIAS DE FORMATIVOS (ERROR: DIAS EN DECIMALES)
							
							//ICAPUNAY 27/07/2011 - CORRECCION PARA REPORTE DE INASISTENCIAS DE FORMATIVOS (ERROR: DIAS EN DECIMALES)
							if (log.isDebugEnabled()) log.debug("total dias: " + map_aux.get("totales").toString());
							listaDetalleAux.add(map_aux);
							//FIN ICAPUNAY 27/07/2011 - CORRECCION PARA REPORTE DE INASISTENCIAS DE FORMATIVOS (ERROR: DIAS EN DECIMALES)
						}	
					}
					
					if (log.isDebugEnabled()) log.debug("listaDetalleAux SIZE: " + listaDetalleAux.size());
					
					listaDetalle = new ArrayList(listaDetalleAux);
					
					//Insertamos los valores
					quiebre = new HashMap();
					//quiebre.put("cod_uorgan",mt12.get("t12cod_uorga"));
					//quiebre.put("desc_uorgan",mt12.get("t12des_uorga"));
					quiebre.put("listaDetalle",listaDetalle); 				
	
					//insertamos el quiebre al reporte
					reporte.add(quiebre);					
			   }			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return reporte;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList devolucionesDescuentos(String dbpool, String periodo,
			String criterio, String valor, HashMap seguridad)
			throws IncompleteConversationalState, RemoteException {

		ArrayList reporte = null;
		BeanMensaje beanM = new BeanMensaje();
		try {

			T1279DAO movDAO = new T1279DAO();
		    ReporteDAO reporteDAO = new ReporteDAO();

			ArrayList lista = reporteDAO.joinWithT02T12T99findPersonalByCriterioValor(dbpool,
							criterio, valor, seguridad);

			if (lista != null) {

				reporte = new ArrayList();
				ArrayList movimientos = movDAO.joinWithT99T99ByCritValTipo(dbpool, "5",
                        "S", "-1");
				
				BeanReporte quiebre = null; 
				HashMap detalle = null;
				HashMap resumen = null;
				HashMap devoluciones = null;
				HashMap saldos = null;
				BeanTipoMovimiento mov = null;
				
				for (int i = 0; i < lista.size(); i++) {

					quiebre = (BeanReporte) lista.get(i);
					detalle = new HashMap();
					
					resumen = reporteDAO.findResumenMensual(dbpool, periodo, quiebre.getCodigo());
					devoluciones = reporteDAO.findDevoluciones(dbpool, periodo, quiebre.getCodigo());
					
					saldos = new HashMap();	
					
					//EBV Prioridad 1 ..Mostrar solo los que tienen devoluciones
					if (devoluciones != null && devoluciones.size() > 0) {
						
					for (int j = 0; j < movimientos.size(); j++) {					 
					    
					    mov = (BeanTipoMovimiento)movimientos.get(j);
					    
					    float fRes = 0;
					    float fDev = 0;
					    float fSaldo = 0;
					    
					    try{
					        fRes = ((Float)resumen.get(mov.getMov())).floatValue();
					    }
					    catch(Exception e){
					        fRes = 0;
					        resumen.put(mov.getMov(),new Float("0"));
					    }					    
					    try{
					        fDev = ((Float)devoluciones.get(mov.getMov())).floatValue();
					    }
					    catch(Exception e){
					        fDev = 0;
					        devoluciones.put(mov.getMov(),new Float("0"));
					    }					    
					    fSaldo = fRes - fDev;
					    saldos.put(mov.getMov(),new Float(fSaldo));
					    
					}

					detalle.put("resumen",resumen);
					detalle.put("devoluciones",devoluciones);
					detalle.put("saldos",saldos);
					
					//insertamos el detalle del quiebre
					quiebre.setMap(detalle);
					//insertamos el quiebre al reporte
					reporte.add(quiebre);
					}
				}

			}
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return reporte;
	}
	
	//dtarazona 3er entregable
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList estadisticoAnualVacaciones(String dbpool,
			String anhoIni, String anhoFin, String criterio, String valor, HashMap seguridad)
			throws IncompleteConversationalState, RemoteException {
		if(log.isDebugEnabled()) log.debug("method estadisticoAnualVacaciones");
		
		ArrayList lista = null;
		BeanMensaje beanM = new BeanMensaje();
		try {

			ReporteDAO dao = new ReporteDAO();

			lista = dao.findEstadisticoAnualVacaciones(dbpool, anhoIni, anhoFin,
					criterio, valor, seguridad);

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
	public ArrayList personalNoGeneroSaldoVacacional(String dbpool,
			String fechaIni, String fechaFin, String criterio, String valor, HashMap seguridad)
			throws IncompleteConversationalState, RemoteException {
		if(log.isDebugEnabled()) log.debug("method estadisticoAnualVacaciones");
		
		ArrayList lista = null;
		BeanMensaje beanM = new BeanMensaje();
		try {

			ReporteDAO dao = new ReporteDAO();

			lista = dao.findPersonalNoGeneroSaldoVacacional(dbpool, fechaIni, fechaFin,
					criterio, valor, seguridad);

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
	public ArrayList findDetalleSaldoVacacional(String dbpool,
			String numero)
			throws IncompleteConversationalState, RemoteException {
		if(log.isDebugEnabled()) log.debug("method findDetalleSaldoVacacional");
		
		ArrayList lista = new ArrayList(),dias=new ArrayList();
		BeanMensaje beanM = new BeanMensaje();
		try {

			ReporteDAO dao = new ReporteDAO();

			dias = dao.findDetalleSaldoVacacional(dbpool, numero);
			String diasCadena="",periodo="";
			ArrayList diasLaborables=new ArrayList(),diasDescontables=new ArrayList(),diasEnfermedad=new ArrayList();
			HashMap data=new HashMap();
			if(dias.size()>0){
				for(int i=0;i<dias.size();i++){
					HashMap detalle=(HashMap)dias.get(i);
					log.debug("tamaño:"+dias.size()+"-i="+i);
					String tipo=detalle.get("ind_saldovac_d").toString();
					FechaBean fb=new FechaBean(detalle.get("dia").toString());
					
					String dia=fb.getDia();
					if(log.isDebugEnabled()) log.debug("Dia:"+dia);
					String mes=fb.getMes();
					if(log.isDebugEnabled()) log.debug("Mes:"+mes);
					String anio=fb.getAnho();
					if(log.isDebugEnabled()) log.debug("Año:"+anio);
					
					if(i==0){
						diasCadena+=dia;
						periodo=fb.getMesletras()+" "+anio;
						if(dias.size()==i+1){
							HashMap regPeriodo=new HashMap();
							regPeriodo.put("periodo", periodo);
							regPeriodo.put("dias", diasCadena);
							if(tipo.equals("1")){
								diasLaborables.add(regPeriodo);
							}else if(tipo.equals("2")){
								diasDescontables.add(regPeriodo);
							}else{
								diasEnfermedad.add(regPeriodo);
							}
						}
					}else{
						HashMap detalleAnt=(HashMap)dias.get(i-1);
						FechaBean fb1=new FechaBean(detalleAnt.get("dia").toString());
						String tipoAnt=detalleAnt.get("ind_saldovac_d").toString();
						String diaAnt=fb1.getDia();
						String mesAnt=fb1.getMes();
						String anioAnt=fb1.getAnho();
						//diasCadena=dia;
						if(mes.equals(mesAnt) && anio.equals(anioAnt)){
							diasCadena+=", "+dia;
							if(dias.size()==i+1){
								HashMap regPeriodo=new HashMap();
								regPeriodo.put("periodo", periodo);
								regPeriodo.put("dias", diasCadena);
								if(tipo.equals("1")){
									diasLaborables.add(regPeriodo);
								}else if(tipo.equals("2")){
									diasDescontables.add(regPeriodo);
								}else{
									diasEnfermedad.add(regPeriodo);
								}
							}
						}else{
							HashMap regPeriodo=new HashMap();
							regPeriodo.put("periodo", fb1.getMesletras()+" "+fb1.getAnho());
							regPeriodo.put("dias", diasCadena);
							if(tipoAnt.equals("1")){
								diasLaborables.add(regPeriodo);
							}else if(tipoAnt.equals("2")){
								diasDescontables.add(regPeriodo);
							}else{
								diasEnfermedad.add(regPeriodo);
							}
							log.debug("Agregado:"+regPeriodo);
							diasCadena=dia;//Reseteamos Dias
							periodo=fb.getMesletras()+" "+anio;//Camiamos al nuevo periodo		
							if(dias.size()==i+1){
								HashMap regPeriodo1=new HashMap();
								regPeriodo1.put("periodo", periodo);
								regPeriodo1.put("dias", diasCadena);
								if(tipo.equals("1")){
									diasLaborables.add(regPeriodo1);
								}else if(tipo.equals("2")){
									diasDescontables.add(regPeriodo1);
								}else{
									diasEnfermedad.add(regPeriodo1);
								}
							}
						}
					}
				}
				data.put("diasLaborables", diasLaborables);
				data.put("diasDescontables", diasDescontables);
				data.put("diasEnfermedad", diasEnfermedad);
				if(log.isDebugEnabled()) log.debug("DataDetalle:"+data.toString());
				lista.add(data);
			}

		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		return lista;
	}
	//dtarazona findDetalleSaldoVacacional

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList buscarAnnosVacacionesPendientes(String dbpool,
			String anhoIni, String anhoFin, String criterio, String valor,
			String dias, HashMap seguridad)
			throws IncompleteConversationalState, RemoteException {

		ArrayList lista = null;
		BeanMensaje beanM = new BeanMensaje();
		try {

			ReporteDAO dao = new ReporteDAO();

			lista = dao.findAnhosVacacionesPendientes(dbpool, anhoIni, anhoFin,
					criterio, valor, Integer.parseInt(dias), seguridad);

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
	public void masivoMarcaciones(String dbpool, HashMap params, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {
			QueueDAO qd = new QueueDAO();
			params.put("dbpool", dbpool);
			String fechaIni = (String) params.get("fechaIni");
			String fechaFin = (String) params.get("fechaFin");
			params.put("observacion", "Reporte de Marcaciones del " + fechaIni
					+ " al " + fechaFin);

			qd.encolaReporte(ReporteMasivoFacadeHome.JNDI_NAME,
					"marcaciones", params, usuario);
			
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
	public void masivoTurnos(String dbpool, HashMap params, String usuario, HashMap seguridad)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {
			QueueDAO qd = new QueueDAO();
			params.put("dbpool", dbpool);
			params.put("seguridad", seguridad);			
			String fechaIni = (String) params.get("fechaIni");
			String fechaFin = (String) params.get("fechaFin");
			params.put("observacion", "Reporte de Turnos de Trabajo del " + fechaIni
					+ " al " + fechaFin);

			qd.encolaReporte(ReporteMasivoFacadeHome.JNDI_NAME,
					"turnosTrabajo", params, usuario);
			
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
	public void masivoInasistencias(String dbpool, HashMap params,
			String usuario) throws IncompleteConversationalState,
			RemoteException {
		//log.debug("===========================");			
		//log.debug("dentro de del reporteFacade metodo...masivoInasistencias("+dbpool+", "+params+", "+usuario);		
		BeanMensaje beanM = new BeanMensaje();
		try {

			QueueDAO qd = new QueueDAO();
			params.put("dbpool", dbpool);
			
			String fechaIni = (String) params.get("fechaIni");
			String fechaFin = (String) params.get("fechaFin");
			
			params.put("observacion","Reporte de Inasistencias del " + fechaIni+" al "+fechaFin);

			qd.encolaReporte(ReporteMasivoFacadeHome.JNDI_NAME,
					"inasistencias", params, usuario);
			
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
	public void masivoImpares(String dbpool, HashMap params, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {

			QueueDAO qd = new QueueDAO();
			params.put("dbpool", dbpool);

			String fechaIni = (String) params.get("fechaIni");
			String fechaFin = (String) params.get("fechaFin");
			params.put("observacion", "Reporte de Marcaciones Impares del "
					+ fechaIni + " al " + fechaFin);

			qd.encolaReporte(ReporteMasivoFacadeHome.JNDI_NAME,
					"marcacionesImpares", params, usuario);

		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
	}

	//PRAC-ASANCHEZ 18/06/2009
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void masivoPersonalSinTurno(String dbpool, HashMap params, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {

			QueueDAO qd = new QueueDAO();
			params.put("dbpool", dbpool);

			String fechaIni = (String) params.get("fechaIni");
			String fechaFin = (String) params.get("fechaFin");
			params.put("observacion", "Reporte de Personal sin Turno del "
					+ fechaIni + " al " + fechaFin);

			qd.encolaReporte(ReporteMasivoFacadeHome.JNDI_NAME,
					"personalSinTurno", params, usuario);

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
	public void masivoCalificado(String dbpool, HashMap params, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {
			QueueDAO qd = new QueueDAO();
			params.put("dbpool", dbpool);
			params.put("observacion", "Registro de Asistencia del "					//jquispecoi 05/2014
					+ (String) params.get("fechaIni") + " al " + (String) params.get("fechaFin"));

			qd.encolaReporte(ReporteMasivoFacadeHome.JNDI_NAME,
					"calificado", params, usuario);

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
	public void masivoResumenMensual(String dbpool, HashMap params,
			String usuario) throws IncompleteConversationalState,
			RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {

			QueueDAO qd = new QueueDAO();
			params.put("dbpool", dbpool);

			params.put("observacion","Resumen Mensual de Asistencia del Periodo " + (String) params.get("periodo"));

			qd.encolaReporte(ReporteMasivoFacadeHome.JNDI_NAME,
					"resumenMensual", params, usuario);
			
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
	public void masivoResumenDiario(String dbpool, HashMap params,
			String usuario) throws IncompleteConversationalState,
			RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {

			QueueDAO qd = new QueueDAO();
			params.put("dbpool", dbpool);

			params.put("observacion","Resumen Diario de Asistencia del Periodo " + (String) params.get("periodo"));

			qd.encolaReporte(ReporteMasivoFacadeHome.JNDI_NAME,
					"resumenDiario", params, usuario);

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
	public void masivoVacacionesPendientes(HashMap params, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {

			QueueDAO qd = new QueueDAO();

			params.put("observacion", "Reporte de vacaciones pendientes del " + (String) params.get("anhoIni") + " al " + (String) params.get("anhoFin"));
			
			qd.encolaReporte(ReporteMasivoFacadeHome.JNDI_NAME,
					"vacacionesPendientes", params, usuario);
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
	}
	//DTARAZONA masivoVacacionesProgramadas
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void masivoVacacionesProgramadas(Map params, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {

			QueueDAO qd = new QueueDAO();

			params.put("observacion", "Reporte de vacaciones programadas del " + (String) params.get("periodoIni") + " al " + (String) params.get("periodoFin"));
			log.debug("Paso 2: "+"Reporte de vacaciones programadas del " + (String) params.get("periodoIni") + " al " + (String) params.get("periodoFin"));
			qd.encolaReporte(ReporteMasivoFacadeHome.JNDI_NAME,"vacacionesProgramadas",(HashMap)params, usuario);
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
	}
	//FIN DTARAZONA
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void masivoVacacionesEfectivasPendientes(HashMap params, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {

			QueueDAO qd = new QueueDAO();

			params.put("observacion", "Reporte de vacaciones efectivas pendientes del "
					+ (String) params.get("anhoIni") + " al " + (String) params.get("anhoFin"));

			qd.encolaReporte(ReporteMasivoFacadeHome.JNDI_NAME,
					"vacacionesEfectivasPendientes", params, usuario);
			
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
	public void masivoReportePorTipoLicencia(HashMap params)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {

			QueueDAO qd = new QueueDAO();

			params.put("observacion", "Reporte por tipo de licencia del "
					+ (String) params.get("fechaIni") + " al " + (String) params.get("fechaFin"));

			qd.encolaReporte(ReporteMasivoFacadeHome.JNDI_NAME,
					"reportePorTipoLicencia", params, params.get("usuario").toString());
			
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
	public void masivoPapeletas(HashMap params)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {

			QueueDAO qd = new QueueDAO();

			params.put("observacion", "Reporte masivo de Papeletas del "
					+ (String) params.get("fechaIni") + " al " + (String) params.get("fechaFin"));

			qd.encolaReporte(ReporteMasivoFacadeHome.JNDI_NAME,
					"reportePapeletas", params, params.get("usuario").toString());
			
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
	public void masivoAcumuladoLicencia(HashMap params)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {

			QueueDAO qd = new QueueDAO();

			params.put("observacion", "Reporte acumulado de licencias del "
					+ (String) params.get("fechaIni") + " al " + (String) params.get("fechaFin"));

			qd.encolaReporte(ReporteMasivoFacadeHome.JNDI_NAME,
					"reporteAcumuladoLicencias", params, params.get("usuario").toString());
			
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
	public void masivoVacaciones(HashMap params, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {

			QueueDAO qd = new QueueDAO();

			params.put("observacion", "Reporte de vacaciones");

			qd.encolaReporte(ReporteMasivoFacadeHome.JNDI_NAME,
					"vacaciones", params, usuario);
			
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
	public void masivoVacacionesEfectuadas(HashMap params, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {

			QueueDAO qd = new QueueDAO();

			params.put("observacion", "Reporte de vacaciones efectuadas");

			qd.encolaReporte(ReporteMasivoFacadeHome.JNDI_NAME,
					"vacacionesEfectuadas", params, usuario);
			
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
	public void masivoVacacionesCompensadas(HashMap params, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {

			QueueDAO qd = new QueueDAO();

			params.put("observacion", "Reporte de vacaciones compensadas");

			qd.encolaReporte(ReporteMasivoFacadeHome.JNDI_NAME,
					"vacacionesCompensadas", params, usuario);
			
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
	public ArrayList sinTurno(String dbpool, String fechaIni,
			String fechaFin, String codUO, HashMap seguridad)
			throws IncompleteConversationalState, RemoteException {

		ArrayList reporte = null;
		BeanMensaje beanM = new BeanMensaje();
		try {

			ReporteDAO reporteDAO = new ReporteDAO();
			T12DAO uOrgDAO = new T12DAO();
			
			BeanT12 uo = uOrgDAO.findByCodigo(dbpool,codUO);
			
			seguridad.put("cod_nivel",uo!=null?uo.getT12cod_nivel():"2");
			ArrayList lista = uOrgDAO.findByCodDesc(dbpool, "3", codUO, seguridad);

			if (lista != null) {

				reporte = new ArrayList();
				BeanT12 bUOrg = null;
				BeanReporte quiebre = null;
				ArrayList detalle = null;
				
				for (int i = 0; i < lista.size(); i++) {

					bUOrg = (BeanT12) lista.get(i);
					
					quiebre = new BeanReporte();
					quiebre.setCodigo(bUOrg.getT12cod_uorgan());
					quiebre.setNombre(bUOrg.getT12descr_uorg());
					
					detalle = reporteDAO.findSinTurno(dbpool,fechaIni, fechaFin, bUOrg.getT12cod_uorgan());

					//EBV Prioridad 1 Solo mostrar si no tienen turno
					
					if ((detalle != null ) && (detalle.size() > 0)) {

					//insertamos el detalle del quiebre
					quiebre.setDetalle(detalle);
					//insertamos el quiebre al reporte
					reporte.add(quiebre);
					}
				}
			}
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.toString());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return reporte;
	}

	//PRAC-ASANCHEZ 18/06/2009
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */	
	public ArrayList personalSinTurno(Map params, HashMap seguridad)
			throws IncompleteConversationalState, RemoteException {

		ArrayList reporte = null;
		BeanMensaje beanM = new BeanMensaje();
		String dbpool =	(params.get("dbpool") != null) ? (String)params.get("dbpool"): "";
		String fechaIni =	(params.get("fechaIni") != null) ? (String)params.get("fechaIni"): "";
		String fechaFin =	(params.get("fechaFin") != null) ? (String)params.get("fechaFin"): "";
		String regimen =	(params.get("regimen") != null) ? (String)params.get("regimen"): "";
		String criterio =	(params.get("criterio") != null) ? (String)params.get("criterio"): "";
		String valor =	(params.get("valor") != null) ? (String)params.get("valor"): "";		
		if(log.isDebugEnabled())log.debug("personalSinTurno-params:" + params);
		
		try {
			ReporteDAO reporteDAO = new ReporteDAO();			
			//JVV-ini --se agrego regimen
			ArrayList lista = reporteDAO.joinWithT02T12T99findPersonalByCriterioVal(dbpool, regimen, criterio, valor, seguridad);
			if(log.isDebugEnabled()) log.debug("Lista1:"+lista.toString());
			if (lista != null) {
				reporte = new ArrayList();
				BeanReporte quiebre = null;
				/*for (int i = 0; i < lista.size(); i++) {
					quiebre = (BeanReporte)lista.get(i);
					detalle = reporteDAO.findPersonalSinTurno(dbpool, fechaIni, fechaFin, quiebre.getCodigo());		
					if(log.isDebugEnabled()) log.debug("Detalle:"+detalle);
					if ((detalle != null ) && (detalle.size() > 0)) {
					quiebre.setDetalle(detalle);
					reporte.add(quiebre);
					}
				}*/
				//DTARAZONA 
				for (int i = 0; i < lista.size(); i++) {
					fechaIni =	(params.get("fechaIni") != null) ? (String)params.get("fechaIni"): "";
					
					quiebre = (BeanReporte)lista.get(i);
					ArrayList detalle=reporteDAO.findTurnosActivosPorCodigo(dbpool,quiebre.getCodigo(), fechaIni, fechaFin);
					if(log.isDebugEnabled()) log.debug("Turnos Por Código:"+quiebre.getCodigo()+"-"+detalle.toString());
					
					String diasSinTurno="";
					String[] diasSinTurnoToArr=null;
					
					pe.gob.sunat.sp.dao.T02DAO empleadoDAO = new pe.gob.sunat.sp.dao.T02DAO();		//ICR 08/05/2015-PAS20155E230000154-labor antigua  (autorizacion y compensacion) y labor nueva (sol. compensacion) mayor o igual fecha ingreso a sunat
					Map mapaEmpleado = new HashMap();
					String fecha_ingreso="";					
					mapaEmpleado = empleadoDAO.findEmpleadoPorRegistro(dbpool,quiebre.getCodigo());
					fecha_ingreso = (mapaEmpleado!=null && !mapaEmpleado.isEmpty())?mapaEmpleado.get("t02f_ingsun").toString().trim():"";
					log.debug("FechaIn:"+fecha_ingreso);
					String fi="1990-01-01";
					if(!fecha_ingreso.equals(""))
					fi=fecha_ingreso.substring(0,10);
					String[] fechaFormat=fi.toString().trim().split("-");
					String nvaFechaIngreso="";
					if(fechaFormat.length>0)
					nvaFechaIngreso=""+fechaFormat[2]+"/"+fechaFormat[1]+"/"+fechaFormat[0];
					
					int numDiasSinTurno=Utiles.obtenerDiasDiferencia(fechaIni, fechaFin);
					if((detalle != null ) && (detalle.size() > 0)){
						
						//OBTENER EL MINIMO
						String fecMinimo="";
						for(int j=0;j<detalle.size();j++){
							if(j>0){								
								HashMap turno=(HashMap)detalle.get(j);
								String fIniTurno=(turno.get("fini") != null) ? (String)turno.get("fini"): "";
								if(Utiles.obtenerDiasDiferencia(fIniTurno, fecMinimo)>0){
									fecMinimo=fIniTurno;
								}
							}else{
								HashMap turno=(HashMap)detalle.get(j);
								fecMinimo=(turno.get("fini") != null) ? (String)turno.get("fini"): "";
							}
						}
						log.debug("FecMinimo:"+fecMinimo);
						
						if(Utiles.obtenerDiasDiferencia(fechaIni, nvaFechaIngreso)>=0){
							fechaIni=nvaFechaIngreso;							
						}
						
						String fIniTurno="",fFinTurno="";
						for(int j=0;j<detalle.size();j++){
							HashMap turno=(HashMap)detalle.get(j);
							fIniTurno=(turno.get("fini") != null) ? (String)turno.get("fini"): "";
							/*String fIniTurno=fecMinimo;
							if(Utiles.obtenerDiasDiferencia(fIniTurno, nvaFechaIngreso)>=0){
								fIniTurno=nvaFechaIngreso;							
							}*/
							
							fFinTurno=(turno.get("ffin") != null) ? (String)turno.get("ffin"): "";
							
							if(Utiles.obtenerDiasDiferencia(fIniTurno, fechaIni)>=0){
								fechaIni=Utiles.dameFechaSiguiente(fFinTurno, 1);
							}else{
								while(Utiles.obtenerDiasDiferencia(fechaIni, fIniTurno)>0){
									diasSinTurno=diasSinTurno+fechaIni+"-";
									fechaIni=Utiles.dameFechaSiguiente(fechaIni, 1);
								}
								fechaIni=Utiles.dameFechaSiguiente(fFinTurno, 1);
							}
						}
						if(Utiles.obtenerDiasDiferencia(fFinTurno, fechaFin)>=0){
							fechaIni=Utiles.dameFechaSiguiente(fFinTurno, 1);
							while(Utiles.obtenerDiasDiferencia(fechaIni, fechaFin)>=0){							
								diasSinTurno=diasSinTurno+fechaIni+"-";
								fechaIni=Utiles.dameFechaSiguiente(fechaIni, 1);							
							}
						}
						
						diasSinTurnoToArr=diasSinTurno.split("-");
						numDiasSinTurno=diasSinTurnoToArr.length-1;
					}else{
						if(Utiles.obtenerDiasDiferencia(fechaIni, nvaFechaIngreso)>=0){
							fechaIni=nvaFechaIngreso;							
						}
						while(Utiles.obtenerDiasDiferencia(fechaIni, fechaFin)>=0){							
							diasSinTurno=diasSinTurno+fechaIni+"-";
							fechaIni=Utiles.dameFechaSiguiente(fechaIni, 1);							
						}
						if(log.isDebugEnabled()) log.debug("fini:"+diasSinTurno+" - ffin:"+numDiasSinTurno);
					}
					if(log.isDebugEnabled()) log.debug("numDiasSinTurno:"+numDiasSinTurno+"-diasSinTurno:"+diasSinTurno);
					if(numDiasSinTurno>0 && !diasSinTurno.equals(""))
					{
						HashMap usuarios=new HashMap();
						usuarios.put("traba", quiebre);
						
						String[] diasPorMes = diasSinTurno.split("-");
						int cantPorMes=0; String queDias="";
						String periodo="";
						ArrayList arrPeriodo=new ArrayList();
						for(int j=0;j<diasPorMes.length;j++){
							String[] fechaActual=diasPorMes[j].split("/");
							String mesActual=fechaActual[1];							
							if(j>=1){
								String[] fechaAnterior=diasPorMes[j-1].split("/");
								String mesAnterior=fechaAnterior[1];
								if(mesActual.equals(mesAnterior)){
									if(diasPorMes.length==j+1){
										cantPorMes++;
										periodo=periodo+cantPorMes+"/";
										periodo=periodo+queDias+"-"+fechaActual[0];
										arrPeriodo.add(periodo);
									}else{
										queDias=queDias+"-"+fechaActual[0];
										cantPorMes++;
									}																		
								}else{										
									periodo=periodo+cantPorMes+"/";
									periodo=periodo+queDias;
									arrPeriodo.add(periodo);
									FechaBean fb = new FechaBean(diasPorMes[j]);	
									String perStr=fb.getMesletras().substring(0,3)+"-"+fb.getAnho();
									periodo=perStr+"/";//Resetear
									if(diasPorMes.length==j+1){
										cantPorMes=1;										
										periodo=periodo+cantPorMes+"/";
										queDias=fechaActual[0];
										periodo=periodo+queDias;
										arrPeriodo.add(periodo);
									}else{	
										cantPorMes=1;
										queDias=fechaActual[0];
									}	
								}
							}else{
								cantPorMes++;
								FechaBean fb = new FechaBean(diasPorMes[j]);	
								String perStr=fb.getMesletras().substring(0,3)+"-"+fb.getAnho();
								periodo=periodo+perStr+"/";
								if(diasPorMes.length==j+1){
									periodo=periodo+cantPorMes;
									arrPeriodo.add(periodo);
								}else{
									queDias=queDias+fechaActual[0];									
								}																
							}
						}						
						usuarios.put("periodo", arrPeriodo);
						reporte.add(usuarios);
					}
				}
				//FIN DTARAZONA
			}			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.toString());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		if(log.isDebugEnabled()) log.debug("Array A devolver:"+reporte.toString());
		return reporte;
	}
	//
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList marcacionesPersonal(String dbpool, String fechaIni,
			String fechaFin, String criterio, String valor, HashMap seguridad)
			throws IncompleteConversationalState, RemoteException {

		ArrayList reporte = null;
		BeanMensaje beanM = new BeanMensaje();
		try {

			ReporteDAO reporteDAO = new ReporteDAO();
			T99DAO t99DAO = new T99DAO();
			
			ArrayList lista = reporteDAO
					.joinWithT02T12T99findPersonalByCriterioValorPersonalRegimen(dbpool,
							null, criterio, valor, seguridad);
			String fecha = t99DAO.findParamByCodTabCodigo(dbpool, "000","02");
			if (lista != null) {

				reporte = new ArrayList();
				BeanReporte quiebre = null;
				ArrayList detalle = null;
				
				for (int i = 0; i < lista.size(); i++) {

					quiebre = (BeanReporte) lista.get(i);
					detalle = reporteDAO.findMarcacionesPersonal(dbpool,fechaIni, fechaFin, quiebre.getCodigo(), fecha);

					//EBV Prioridad 1 Solo mostrar si tiene Marcaciones Personal
					
					if ((detalle != null ) && (detalle.size() > 0)) {

					//insertamos el detalle del quiebre
					quiebre.setDetalle(detalle);
					//insertamos el quiebre al reporte
					reporte.add(quiebre);
					}
				}
			}
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return reporte;
	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList papeletas(HashMap datos, HashMap seguridad)
			throws IncompleteConversationalState, RemoteException {

		ArrayList reporte = null;
		BeanMensaje beanM = new BeanMensaje();
		try {
		
			ReporteDAO reporteDAO = new ReporteDAO();
			
			//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
			//ICAPUNAY-MEMO 32-4F3100-2013
			/*HashMap roles = (HashMap) seguridad.get("roles");
	    	log.debug("papeletas-roles: "+roles);
	    	if (roles.get(Constantes.ROL_JEFE) != null) {
	    		log.debug("papeletas-seguridad antes: "+seguridad);			
				String uoAO = (String) seguridad.get("uoAO");
				seguridad.put("uoSeg", findUuooJefe(uoAO)+"%");//4EB2%
				log.debug("papeletas-seguridad final: "+seguridad);
	    	}*/			
			//ICAPUNAY-MEMO 32-4F3100-2013
	    	//ICAPUNAY - PAS20165E230300132

			ArrayList lista = reporteDAO.joinWithT02T12T99findPersonalByCriterioValor((String)datos.get("dbpool"),
			        (String)datos.get("criterio"), (String)datos.get("valor"), seguridad);

			if (lista != null) {

				reporte = new ArrayList();
				BeanReporte quiebre = null;
				ArrayList detalle = null;				
				    
				for (int i = 0; i < lista.size(); i++) {

					quiebre = (BeanReporte) lista.get(i);
					
					datos.put("cod_pers",quiebre.getCodigo());
					detalle = reporteDAO.findPapeletas(datos);

					//EBV Prioridad 1 Solo mostrar si tiene Papeletas
					
					if ((detalle != null ) && (detalle.size() > 0)) {

					//insertamos el detalle del quiebre
					quiebre.setDetalle(detalle);
					//insertamos el quiebre al reporte
					reporte.add(quiebre);
					}
				}

			}
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.toString());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return reporte;
	}	
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList LicenciaMedica(String dbpool, String fechaIni,
			String fechaFin, String criterio, String valor, String tipoLic,
			String dias, HashMap seguridad) throws IncompleteConversationalState,
			RemoteException {

		ArrayList reporte = null;
		BeanMensaje beanM = new BeanMensaje();
		try {

			ReporteDAO reporteDAO = new ReporteDAO();

			ArrayList lista = reporteDAO
					.joinWithT02T12T99findPersonalByCriterioValor(dbpool,
							criterio, valor, seguridad);

			if (lista != null) {

				reporte = new ArrayList();
				BeanReporte quiebre = null;
				ArrayList detalle = null;
				
				for (int i = 0; i < lista.size(); i++) {

					quiebre = (BeanReporte) lista.get(i);

					//Licencia Medica - Enfermeda, Pre-Post Natal, Parto Multiple
					//EBV 22/08/2007 Lic. Gravidez , Gravidez Multiple
					//log.debug("Tipo Lic"+tipoLic);
					detalle = reporteDAO.findLicenciasMedByTipoCodPers(dbpool,
							tipoLic, fechaIni, fechaFin, quiebre.getCodigo(),Integer.parseInt(dias));

					//EBV Prioridad 1 Solo mostrar si tiene Licencia Medicas
					
					if ((detalle != null ) && (detalle.size() > 0)) {

					//insertamos el detalle del quiebre
					quiebre.setDetalle(detalle);
					//insertamos el quiebre al reporte
					reporte.add(quiebre);
					}
				}

			}
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return reporte;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void masivoLicenciaMedica(HashMap params, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {

			QueueDAO qd = new QueueDAO();

			params.put("observacion", "Reporte de Licencias : Subsidio");

			qd.encolaReporte(ReporteMasivoFacadeHome.JNDI_NAME,
					"licenciaMedica", params, usuario);
			
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
	public void masivoHoraExtra(String dbpool, HashMap params,
			String usuario) throws IncompleteConversationalState,
			RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {

			QueueDAO qd = new QueueDAO();
			params.put("dbpool", dbpool);

			params.put("observacion","Reporte de Labor Excepcional");

			qd.encolaReporte(ReporteMasivoFacadeHome.JNDI_NAME,
					"horaExtra", params, usuario);
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
	}

	//PRAC-ASANCHEZ 03/08/2009
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList detalleDiario(String dbpool, String fechaIni,
			String fechaFin, String criterio, String valor, HashMap seguridad,  String mov)
			throws IncompleteConversationalState, RemoteException {

		ArrayList reporte = null;
		BeanMensaje beanM = new BeanMensaje();
		try {
			ReporteDAO reporteDAO = new ReporteDAO();
			T130DAO t130DAO = new T130DAO(sl.getDataSource(dbpool));
			ArrayList lista = reporteDAO
			.joinWithT02T12T99findPersonalByCriterioValorPersonal(dbpool,
			        criterio.equals("2")?"0":criterio, valor, seguridad);

			if (lista != null) {

				reporte = new ArrayList();
				BeanReporte quiebre = null;
				ArrayList detalle = null;
				
				for (int i = 0; i < lista.size(); i++) {

					quiebre = (BeanReporte) lista.get(i);
					detalle = reporteDAO.findDetalleDiario(dbpool,
							fechaIni, fechaFin, quiebre.getCodigo(), criterio, mov);
					
					if ((detalle != null ) && (detalle.size() > 0)) {
					//quiebre.setCantidad(t130DAO.findSaldobyRango(quiebre.getCodigo(), fechaIni,fechaFin).intValue());
					//insertamos el detalle del quiebre
					quiebre.setDetalle(detalle);
					//insertamos el quiebre al reporte
					reporte.add(quiebre);
					}
				}
			}
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return reporte;
	}
	//

	//PRAC-ASANCHEZ 03/08/2009
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void masivoDetalleDiario(String dbpool, HashMap params, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {
			QueueDAO qd = new QueueDAO();
			params.put("dbpool", dbpool);
			params.put("observacion", "Reporte de Detalle Diario de Asistencia del "
					+ (String) params.get("fechaIni") + " al " + (String) params.get("fechaFin"));
			qd.encolaReporte(ReporteMasivoFacadeHome.JNDI_NAME,
					"detalleDiario", params, usuario);
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
	}
	//
	
	
/*************** JRR ********************/
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public List compensacionesBolsa(Map datos, Map seguridad)
			throws IncompleteConversationalState, RemoteException {
		
		List reporte = null;
		BeanMensaje beanM = new BeanMensaje();
		try {
			ReporteDAO reporteDAO = new ReporteDAO();

			List lista_pers = reporteDAO.joinWithT02T12T99findPersonalByCriterioValor((String)datos.get("dbpool"),
			        (String)datos.get("criterio"), (String)datos.get("valor"), (HashMap)seguridad);
			
			T3792DAO t3792dao = new T3792DAO((String)datos.get("dbpool")); 

			if (lista_pers != null) {
				reporte = new ArrayList();
				BeanReporte quiebre;
				List detalle = null;
				
				for (int i = 0; i < lista_pers.size(); i++) {
					quiebre = (BeanReporte) lista_pers.get(i);	
					datos.put("cod_pers",quiebre.getCodigo());
					detalle = t3792dao.findCompensacionesByCriterio(datos);
					if (log.isDebugEnabled()) log.debug(quiebre.getCodigo() + " - detalle: " + detalle);
					
					if ((detalle != null ) && (detalle.size() > 0)) {
						//insertamos el detalle del quiebre
						quiebre.setDetalle((ArrayList)detalle);
						//insertamos el quiebre al reporte
						reporte.add(quiebre);
					}
				}

			}
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.toString());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return reporte;
	}	

	/**PRAC-ASANCHEZ 28/08/2009
	 * Metodo encargado de encolar el reporte de Labor Excepcional
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param dbpool
	 * @param params
	 * @param usuario
	 * @return
	 * @throws RemoteException
	 */

	public void masivoLaborExcepcional(String dbpool, HashMap params, String usuario)
			throws RemoteException {
		BeanMensaje beanM = new BeanMensaje();
		try {
			QueueDAO qd = new QueueDAO();
			params.put("dbpool", dbpool);
			params.put("observacion", "Reporte de Labor Excepcional del "
					+ (String) params.get("fechaIni") + " al " + (String) params.get("fechaFin"));
			qd.encolaReporte(ReporteMasivoFacadeHome.JNDI_NAME,
					"laborExcepcional", params, usuario);
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
	}
	
	/**JVILLACORTA - 19/04/2011 - ALERTA DE SOLICITUDES
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */	
	public ArrayList notificacionesDirectivos(Map params) throws FacadeException,
			RemoteException {		
				
		ArrayList reporte = null;
		BeanMensaje beanM = new BeanMensaje();		
		ArrayList directivos = null;
		
		try {			
			DataSource dbpool_sp = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dcsp");
			pe.gob.sunat.rrhh.asistencia.dao.ReporteDAO reportDAO = new pe.gob.sunat.rrhh.asistencia.dao.ReporteDAO(dbpool_sp);			
								
			List lista = reportDAO.findNotificaDirectivosByCriterioValor(params);					
			if(log.isDebugEnabled()) log.debug("personal:"+lista);
			
			if (lista != null) {
				Map rs = null;				
				//String uuoo = "";
				directivos = new ArrayList();				
				StringBuffer texto = new StringBuffer("");
				StringBuffer texto2 = new StringBuffer("");
				StringBuffer texto3 = new StringBuffer("");
				StringBuffer texto4 = new StringBuffer("");
				for (int i = 0; i < lista.size(); i++) {
					rs = (HashMap) lista.get(i);				
					texto = new StringBuffer(String.valueOf(rs.get("cod_pers_notif")!=null?rs.get("cod_pers_notif"):"").trim())
							.append( " - ")							
							.append(String.valueOf(rs.get("t02ap_pate")!=null?rs.get("t02ap_pate"):"").trim()).append( " ")
							.append(String.valueOf(rs.get("t02ap_mate")!=null?rs.get("t02ap_mate"):"").trim()).append( ", ")
							.append(String.valueOf(rs.get("t02nombres")!=null?rs.get("t02nombres"):"").trim())
							.append( " - ")		
							.append(String.valueOf(rs.get("t12des_corta")).trim()).append( " / ")
							.append(String.valueOf(rs.get("t99descrip")).trim());
					
					texto2 = new StringBuffer(String.valueOf(rs.get("t02ap_pate")!=null?rs.get("t02ap_pate"):"").trim())
					.append( " ")
					.append(String.valueOf(rs.get("t02ap_mate")!=null?rs.get("t02ap_mate"):"").trim()).append( ", ")
					.append(String.valueOf(rs.get("t02nombres")!=null?rs.get("t02nombres"):"").trim());		
					
					texto3 = new StringBuffer(String.valueOf(rs.get("t99descrip")).trim());
					texto4 = new StringBuffer(String.valueOf(rs.get("t12cod_uorga")).trim());
					
					rs.put("nombre", texto.toString().trim());					
					//uuoo = String.valueOf(rs.get("cod_uorg"));
					rs.put("codigo", (String)rs.get("cod_pers_notif"));
					rs.put("apenom", texto2.toString().trim());
					rs.put("cargo", texto3.toString().trim());
					rs.put("uuoo", texto4.toString().trim());
					//rs.put("unidad", uuoo.trim().concat( " - ").concat(String.valueOf(rs.get("t12des_uorga")).trim()));
					directivos.add(rs);
					if(log.isDebugEnabled()) log.debug("rs:" + rs);
					if(log.isDebugEnabled()) log.debug("directivos:" + directivos);					
					
				}	
			}						
			
			if (directivos != null && directivos.size() > 0) {
				reporte = new ArrayList();				
				Map quiebre = null;
				List detalle = null;
				
				for (int j = 0; j < directivos.size(); j++) {					
					quiebre = (HashMap) directivos.get(j);
					detalle = reportDAO.findNotificacionesDirec(params, (String)quiebre.get("codigo"));					
					if(log.isDebugEnabled()) log.debug("notificacion:" + j + detalle);					
					if ((detalle != null ) && (detalle.size() > 0)) {						
						quiebre.put("detalle",detalle);						
						reporte.add(quiebre);
					}
				}
			}
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		return reporte;
	}//FIN - JVILLACORTA - 20/04/2011 - ALERTA DE SOLICITUDES
	
	/**JVILLACORTA - 20/04/2011 - ALERTA DE SOLICITUDES
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */	
	public ArrayList buscaDetalleNotificaDirec(Map params) throws FacadeException,
			RemoteException {
		
		String fechaIni = (String)params.get("fechaIni");
		String fechaFin = (String)params.get("fechaFin");
		ArrayList reporte = null;
		BeanMensaje beanM = new BeanMensaje();		
		try {			
			DataSource dbpool_sp = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dcsp");
			pe.gob.sunat.rrhh.asistencia.dao.ReporteDAO reportDAO = new pe.gob.sunat.rrhh.asistencia.dao.ReporteDAO(dbpool_sp);			
			
			FechaBean fb = new FechaBean();			
			String ffSem1 = fb.getOtraFecha(fechaIni, 7, Calendar.DATE); //<08-03
			String ffSem2 = fb.getOtraFecha(ffSem1, 7, Calendar.DATE); //<15-03
			String ffSem3 = fb.getOtraFecha(ffSem2, 7, Calendar.DATE); //<22-03
			String ffSem4 = fb.getOtraFecha(ffSem3, 7, Calendar.DATE); //<29-03
			//String ffSem5 = fb.getOtraFecha(ffSem4, 1, Calendar.DATE); //<30-03
			
			if(params.get("sem").equals("Sem1")){				
				params.put("fIni", fechaIni);
				params.put("fFin", ffSem1);
			}			
			if(params.get("sem").equals("Sem2")){				
				params.put("fIni", ffSem1);
				params.put("fFin", ffSem2);
			}
			if(params.get("sem").equals("Sem3")){				
				params.put("fIni", ffSem2);
				params.put("fFin", ffSem3);
			}
			if(params.get("sem").equals("Sem4")){				
				params.put("fIni", ffSem3);
				params.put("fFin", ffSem4);	
			}
			if(params.get("sem").equals("Sem5")){				
				params.put("fIni", ffSem4);
				params.put("fFin", fechaFin);	
			}
			
			List lista = reportDAO.findDetalleNotificaDirec(params);					
			if(log.isDebugEnabled()) log.debug("los_solicitantes:"+lista);
			 
			if (lista != null && lista.size() > 0) {
				reporte = new ArrayList();
				Map rs = null;								
				StringBuffer texto = new StringBuffer("");		
				StringBuffer fechaSolicitud = new StringBuffer("");//ICAPUNAY 11/08/2011 visualizar fecha solicitud en formato dd/MM/yyyy
				for (int i = 0; i < lista.size(); i++) {
					rs = (HashMap) lista.get(i);				
					texto = new StringBuffer(String.valueOf(rs.get("t02nombres")!=null?rs.get("t02nombres"):"").trim())
							.append( " ")							
							//.append(String.valueOf(rs.get("t02ap_pate")!=null?rs.get("t02ap_pate"):"").trim()); //ICAPUNAY 11/08/2011 visualizar anno, numero y fecha solicitud y ape_mate solicitante
							.append(String.valueOf(rs.get("t02ap_pate")!=null?rs.get("t02ap_pate"):"").trim()) //ICAPUNAY 11/08/2011 visualizar anno, numero y fecha solicitud y ape_mate solicitante
							.append( " ")//ICAPUNAY 11/08/2011 visualizar anno, numero y fecha solicitud y ape_mate solicitante							
							.append(String.valueOf(rs.get("t02ap_mate")!=null?rs.get("t02ap_mate"):"").trim());//ICAPUNAY 11/08/2011 visualizar anno, numero y fecha solicitud y ape_mate solicitante
					fechaSolicitud = new StringBuffer(String.valueOf(rs.get("fecha")!=null? new FechaBean((Timestamp)rs.get("fecha")).getFormatDate("dd/MM/yyyy"):"").trim()); //ICAPUNAY 11/08/2011 visualizar fecha solicitud en formato dd/MM/yyyy							
					
					rs.put("emisor", texto.toString().trim());										
					rs.put("fechaSolicitud", fechaSolicitud.toString().trim());//ICAPUNAY 11/08/2011 visualizar fecha solicitud en formato dd/MM/yyyy
					reporte.add(rs);						
				}	
			}			
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return reporte;
	}//FIN - JVILLACORTA - 20/04/2011 - ALERTA DE SOLICITUDES
	
	/**JVILLACORTA - 18/04/2011 - ALERTA DE SOLICITUDES
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */	
	public ArrayList notificacionesTrabajadores(Map params) throws FacadeException,
			RemoteException {		
				
		ArrayList reporte = null;
		BeanMensaje beanM = new BeanMensaje();		
		ArrayList trabajadores = null;
		
		try {			
			DataSource dbpool_sp = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dcsp");
			pe.gob.sunat.rrhh.asistencia.dao.ReporteDAO reportDAO = new pe.gob.sunat.rrhh.asistencia.dao.ReporteDAO(dbpool_sp);
			
			List lista = reportDAO.findNotificaTrabajadoresByCriterioValor(params);					
			if(log.isDebugEnabled()) log.debug("personal:"+lista);
			
			if (lista != null) {
				Map rs = null;				
				//String uuoo = "";
				trabajadores = new ArrayList();				
				StringBuffer texto = new StringBuffer("");	
				StringBuffer texto2 = new StringBuffer("");
				StringBuffer texto3 = new StringBuffer("");
				StringBuffer texto4 = new StringBuffer("");
				for (int i = 0; i < lista.size(); i++) {
					rs = (HashMap) lista.get(i);				
					texto = new StringBuffer(String.valueOf(rs.get("cod_pers")!=null?rs.get("cod_pers"):"").trim())
							.append( " - ")							
							.append(String.valueOf(rs.get("t02ap_pate")!=null?rs.get("t02ap_pate"):"").trim()).append( " ")
							.append(String.valueOf(rs.get("t02ap_mate")!=null?rs.get("t02ap_mate"):"").trim()).append( ", ")
							.append(String.valueOf(rs.get("t02nombres")!=null?rs.get("t02nombres"):"").trim())
							.append( " - ")		
							.append(String.valueOf(rs.get("t12des_corta")).trim()).append( " / ")
							.append(String.valueOf(rs.get("t99descrip")).trim());
					
					texto2 = new StringBuffer(String.valueOf(rs.get("t02ap_pate")!=null?rs.get("t02ap_pate"):"").trim())
							.append( " ")
							.append(String.valueOf(rs.get("t02ap_mate")!=null?rs.get("t02ap_mate"):"").trim()).append( ", ")
							.append(String.valueOf(rs.get("t02nombres")!=null?rs.get("t02nombres"):"").trim());		
					
					texto3 = new StringBuffer(String.valueOf(rs.get("t99descrip")).trim());
					texto4 = new StringBuffer(String.valueOf(rs.get("t12cod_uorga")).trim());
					
					rs.put("nombre", texto.toString().trim());					
					//uuoo = String.valueOf(rs.get("cod_uorg"));
					rs.put("codigo", (String)rs.get("cod_pers"));
					rs.put("apenom", texto2.toString().trim());
					rs.put("cargo", texto3.toString().trim());
					rs.put("uuoo", texto4.toString().trim());
					//rs.put("unidad", uuoo.trim().concat( " - ").concat(String.valueOf(rs.get("t12des_uorga")).trim()));
					trabajadores.add(rs);						
				}	
			}						
			
			if (trabajadores != null && trabajadores.size() > 0) {
				reporte = new ArrayList();				
				Map quiebre = null;
				List detalle = null;
				
				for (int j = 0; j < trabajadores.size(); j++) {					
					quiebre = (HashMap) trabajadores.get(j);
					detalle = reportDAO.findNotificacionesTrab(params, (String)quiebre.get("codigo"));					
					if(log.isDebugEnabled()) log.debug("notificacion:" + j + detalle);					
					if ((detalle != null ) && (detalle.size() > 0)) {						
						quiebre.put("detalle",detalle);						
						reporte.add(quiebre);
					}
				}
			}
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		return reporte;
	}//FIN - JVILLACORTA - 20/04/2011 - ALERTA DE SOLICITUDES
	
	/**JVILLACORTA - 18/04/2011 - ALERTA DE SOLICITUDES
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void masivoNotificacionesDirectivos(String dbpool, HashMap params, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {
			QueueDAO qd = new QueueDAO();
			params.put("dbpool", dbpool);
			String fechaIni = (String) params.get("fechaIni");
			log.debug("masivoNotificacionesDirectivos - params:" + params);
			log.debug("masivoNotificacionesDirectivos - usuario:" + usuario);
			String fechFin = (String) params.get("fechFin");
			params.put("observacion", "Reporte de Notificaciones Directivos del " + fechaIni 
					+ " al " + fechFin);
			
			qd.encolaReporte(ReporteMasivoFacadeHome.JNDI_NAME,
					"notificaDirectivos", params, usuario);
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
	}//FIN - JVILLACORTA - 20/04/2011 - ALERTA DE SOLICITUDES
	
	/**JVILLACORTA - 20/04/2011 - ALERTA DE SOLICITUDES
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void masivoNotificacionesTrabajadores(String dbpool, HashMap params, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {
			QueueDAO qd = new QueueDAO();
			params.put("dbpool", dbpool);
			String fechaIni = (String) params.get("fechaIni");
			log.debug("masivoNotificacionesTrabajadores - params:" + params);
			log.debug("masivoNotificacionesTrabajadores - usuario:" + usuario);
			String fechFin = (String) params.get("fechFin");
			params.put("observacion", "Reporte de Notificaciones Trabajadores del " + fechaIni
					+ " al " + fechFin);

			qd.encolaReporte(ReporteMasivoFacadeHome.JNDI_NAME,
					"notificaTrabajadores", params, usuario);
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
	}//FIN - JVILLACORTA - 20/04/2011 - ALERTA DE SOLICITUDES	
	
	/* ICAPUNAY - TRABAJADORES CON VACACIONES PROGRAMADAS ACEPTADAS O ACTIVAS - 28/02/2011 */		
	/**
	 * Metodo que crea el archivo formato txt para reporte
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param nombreArchivo String
	 * @param nombreReporte String
	 * @param params Map
	 * @param usuario String
	 * @throws IncompleteConversationalState
	 * @throws RemoteException
	 */
	public void creaReporte(String nombreArchivo, String nombreReporte, Map params,
			String usuario) throws IncompleteConversationalState,RemoteException {

		try {
			
			if (log.isDebugEnabled()) log.debug("Entro a creaReporte-ReporteFacade");//B
			numLinea = 0;
			numPagina = 1;
			String path = Constantes.RUTA_LOG_REPORTES + nombreArchivo + ".txt";
			if (log.isDebugEnabled()) log.debug("path= "+path);//B
			output = new PrintStream(new FileOutputStream(path));
			cabeceraReporte(nombreReporte, params, usuario);

		} catch (Exception e) {
			log.debug("Error al crear el archivo txt para reporte : " + e.getMessage());
		}
	}
	
	/**
	 * Metodo que crea la cabecera del archivo formato txt para reporte
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param nombreReporte String
	 * @param params Map
	 * @param usuario String
	 * @throws IncompleteConversationalState
	 * @throws RemoteException
	 */
	public void cabeceraReporte(String nombreReporte, Map params, String usuario)
			throws IncompleteConversationalState,RemoteException {

		try {

			if (log.isDebugEnabled()) log.debug("Entro a cabeceraReporte-ReporteFacade");//B
			String linea = "";
			String lineaTitulo = "";
			for (int i = 0; i < maxColumnas; i++) {
				lineaTitulo += "_";
			}

			String fechaIni = params.get("fechaIni") != null ? (String) params.get("fechaIni") : "";
			String fechaFin = params.get("fechaFin") != null ? (String) params.get("fechaFin") : "";
			if (log.isDebugEnabled()) log.debug("fechaIni= "+fechaIni);//B
			if (log.isDebugEnabled()) log.debug("fechaFin= "+fechaFin);//B

			output.println("");
			output.println(lineaTitulo);
			output.println(Utiles.formateaCadena("REPORTE DE "+ nombreReporte.toUpperCase(), maxColumnas, true));
			
			if (!fechaIni.equals("") || !fechaFin.equals("")) {
				linea = Utiles.formateaCadena("Del " + fechaIni + " al "
						+ fechaFin, maxColumnas, true);
				output.println(linea);
				if (log.isDebugEnabled()) log.debug("linea= "+linea);//B
			} else {
				output.println("");
			}
			
			if (!fechaIni.equals("") || !fechaFin.equals("")) {
				linea = Utiles.formateaCadena("", maxColumnas - 25, false)
					+ "Usuario : " + usuario;
				output.println(linea);
				if (log.isDebugEnabled()) log.debug("linea= "+linea);//B
			}else{
				output.println("");
			}
			
			linea = Utiles.formateaCadena("", maxColumnas - 25, false)
					+ "  Fecha : " + Utiles.obtenerFechaActual();
			output.println(linea);
			if (log.isDebugEnabled()) log.debug("linea= "+linea);//B
			linea = Utiles.formateaCadena("", maxColumnas - 25, false)
					+ "   Hora : " + Utiles.obtenerHoraActual();
			output.println(linea);
			if (log.isDebugEnabled()) log.debug("linea= "+linea);//B

			output.println(lineaTitulo);
			if (log.isDebugEnabled()) log.debug("lineaTitulo= "+lineaTitulo);//B
			output.println("");
			numLinea = 10;

		} catch (Exception e) {
		}
	}
	
	/**
	 * Metodo que escribe una linea en el archivo formato txt para reporte
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param textoImpresion String
	 * @param nombreReporte String
	 * @param params Map
	 * @param usuario String
	 */
	public void escribe(String textoImpresion, String nombreReporte, Map params,
			String usuario) {

		try {

			if (log.isDebugEnabled()) log.debug("Entro a escribe-ReporteFacade");//B
			output.println(textoImpresion);
			if (log.isDebugEnabled()) log.debug("textoImpresion= "+textoImpresion);//B
			numLinea++;

			if (numLinea >= maxLineas) {
				numPagina++;

			}

		} catch (Exception e) {

		}
	}
	
	/**
	 * Metodo que finaliza o cierra el archivo formato txt para reporte
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param nombreArchivo String	
	 * @param params Map
	 * @param usuario String
	 * @return File
	 * @throws IncompleteConversationalState
	 * @throws RemoteException
	 */
	public File registraReporte(String nombreArchivo,Map params, String usuario)
			throws IncompleteConversationalState, RemoteException {

		File fileZipeado=null;
		File fileTxt=null;
		File fileExcelX=null;
		try {

			if (log.isDebugEnabled()) log.debug("Entro a registraReporte-ReporteFacade");//B
					
			//obtenemos el path del archivo en formato .txt , .xlsx y .zip
			String path = nombreArchivo + ".txt";
			String pathExcelX = nombreArchivo + ".xlsx";//ICAPUNAY - PAS20171U230300074 - Mejoras vacaciones
			String pathZip = nombreArchivo + ".zip";
			if (log.isDebugEnabled()) log.debug("path= "+path);//B
			if (log.isDebugEnabled()) log.debug("pathZip= "+pathZip);//B
			if (log.isDebugEnabled()) log.debug("pathExcelX= "+pathExcelX);
			
			fileTxt = new File(Constantes.RUTA_LOG_REPORTES + path);
			fileExcelX = new File(Constantes.RUTA_LOG_REPORTES + pathExcelX);//ICAPUNAY - PAS20171U230300074 - Mejoras vacaciones
			if (log.isDebugEnabled()) log.debug("fileTxt= "+fileTxt);//B
			if (fileTxt.exists()) {
				//generamos o cerramos el archivo
				output.close();
				
				//comprimiendo archivo
				boolean zip = comprimeLog(Constantes.RUTA_LOG_REPORTES, path, pathZip);
				if (log.isDebugEnabled()) log.debug("zip= "+zip);//B				
				if (zip) {					
					fileZipeado = new File(Constantes.RUTA_LOG_REPORTES + pathZip);
					if (log.isDebugEnabled()) log.debug("fileZipeado= "+fileZipeado);//B
					if (fileZipeado.exists()) {
						return fileZipeado;
					}
					
				}	
			}
			//ICAPUNAY - PAS20171U230300074 - Mejoras vacaciones
			if (fileExcelX.exists()) {			
				//comprimiendo archivo
				boolean zip2 = comprimeLog(Constantes.RUTA_LOG_REPORTES, pathExcelX, pathZip);
				if (log.isDebugEnabled()) log.debug("zip2= "+zip2);				
				if (zip2) {					
					fileZipeado = new File(Constantes.RUTA_LOG_REPORTES + pathZip);
					if (log.isDebugEnabled()) log.debug("fileZipeado= "+fileZipeado);//B
					if (fileZipeado.exists()) {
						return fileZipeado;
					}					
				}	
			}
			//FIN ICAPUNAY - PAS20171U230300074 - Mejoras vacaciones

		} catch (Exception e) {
			log.error("Error al generar el archivo con nombre: " + nombreArchivo,e);
			fileZipeado = null;
		}		
		if (log.isDebugEnabled()) log.debug("fileZipeado= "+fileZipeado);//B
		return fileZipeado;
	}
	
	/**
	 * Metodo que elimina los archivos temporales creados con formato .txt y .zip
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param nombreArchivo String	
	 * @throws IncompleteConversationalState
	 * @throws RemoteException
	 */
	public void eliminaArchivosTemporales(String nombreArchivo)
			throws IncompleteConversationalState, RemoteException {

		try {

			if (log.isDebugEnabled()) log.debug("Entro a eliminaArchivosTemporales-ReporteFacade");//B
					
			//obtenemos el path del archivo en formato .txt y .zip
			String path = nombreArchivo + ".txt";
			String pathExcelX = nombreArchivo + ".xlsx";//ICAPUNAY - PAS20171U230300074 - Mejoras vacaciones
			String pathZip = nombreArchivo + ".zip";
			if (log.isDebugEnabled()) log.debug("path= "+path);//B
			if (log.isDebugEnabled()) log.debug("pathZip= "+pathZip);//B
			if (log.isDebugEnabled()) log.debug("pathExcelX= "+pathExcelX);
			
			//eliminando el archivo temporal con formato .txt
			File f = new File(Constantes.RUTA_LOG_REPORTES + path);
			if (log.isDebugEnabled()) log.debug("f= "+f);//B
			
			if (f.exists()) {
				f.delete();
			}
			
			//ICAPUNAY - PAS20171U230300074 - Mejoras vacaciones
			//eliminando el archivo temporal con formato .xlsx
			File f1 = new File(Constantes.RUTA_LOG_REPORTES + pathExcelX);
			if (log.isDebugEnabled()) log.debug("f1= "+f1);
			
			if (f1.exists()) {
				f1.delete();
			}
			//

			//eliminando el archivo temporal con formato .zip
			File fZ = new File(Constantes.RUTA_LOG_REPORTES + pathZip);
			if (log.isDebugEnabled()) log.debug("fZ= "+fZ);//B
			if (fZ.exists()) {
				fZ.delete();
			}
			
			
		} catch (Exception e) {
			log.error("Error al eliminar los archivos temporales",e);			
		}	
	
	}
	
	/**
	 * Metodo encargado de comprimir el archivo formato txt
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param ruta String
	 * @param fileOrigen String
	 * @param fileDestino String
	 * @return boolean
	 */
	public boolean comprimeLog(String ruta, String fileOrigen, String fileDestino) {
		boolean res = true;
		try {

			if (log.isDebugEnabled()) log.debug("Entro a comprimeLog-ReporteFacade");//B
			String path = ruta + fileOrigen;
			String pathZip = ruta + fileDestino;

			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
					new File(pathZip)));
			ZipEntry zentry = new ZipEntry(fileOrigen);
			out.putNextEntry(zentry);

			FileInputStream in = new FileInputStream(new File(path));
			byte[] buffer = new byte[0x10000]; //64kb
			int n;
			while ((n = in.read(buffer)) > 0) {
				out.write(buffer, 0, n);
			}

			in.close();
			out.closeEntry();
			out.close();

		} catch (Exception e) {
			output.println("Error al comprimir archivo: " + e.getMessage());
			res = false;
		}
		return res;
	}
	
	/**
	 * Metodo encargado de la generacion del reporte de Trabajadores notificados para Goce Vacacional
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param dbpool String
	 * @param fechaNotific String
	 * @param fechaIniGoce String
	 * @param criterio String
	 * @param valor String
	 * @param solicitud String
	 * @return reporte List	
	 * @throws IncompleteConversationalState
	 * @throws RemoteException
	 */
	public List notificacionesXVacacionesATrabajadores(String dbpool, String regimen, String fechaNotific,
			String fechaNotificFin, String fechaIniGoce, String fechaFinGoce, String criterio, String valor,String solicitud,HashMap seguridad)
			throws IncompleteConversationalState, RemoteException {

		List reporte = null;
		BeanMensaje beanM = new BeanMensaje();
		try {
			if (log.isDebugEnabled()) log.debug("Ingresara a notificacionesXVacacionesATrabajadores-ReporteFacade");
			
			ReporteDAO reporteDAO = new ReporteDAO();			
			ArrayList lista = reporteDAO.joinWithT02T12T99findPersonalByCriterioValor_NotificacionesVacaciones(dbpool, regimen, criterio, valor, seguridad);
			
			if (lista != null) {
				if (log.isDebugEnabled()) log.debug("lista.size: "+lista.size());							
				reporte = new ArrayList();
				
				BeanReporte quiebre = null;
				List detalle = null;
				HashMap det = new HashMap();
				HashMap detInicio = new HashMap();
				List detalleFinal = null;
								
				for (int i = 0; i < lista.size(); i++) {					
					detalleFinal = new ArrayList();
					quiebre = (BeanReporte)lista.get(i);
					if (log.isDebugEnabled()) log.debug("quiebre("+i+"): "+quiebre);
					
					detalle = reporteDAO.findNotificacionesDeVacacionesEnviadas(dbpool, fechaNotific, fechaNotificFin, fechaIniGoce, fechaFinGoce, solicitud, quiebre.getCodigo());
					if (log.isDebugEnabled()) log.debug("detalle("+i+"): "+detalle);
					
					if ((detalle != null ) && (detalle.size() > 0)) {	
						detInicio = (HashMap)detalle.get(0);
						for (int j = 0; j < detalle.size(); j++) {
							det = (HashMap)detalle.get(j);							
							if (j!=0){							
								detInicio.put("fecnotif"+j,det.get("fecnotif"));
							}
						}
						detalleFinal.add(detInicio);
						if (log.isDebugEnabled()) log.debug("detalleFinal: "+detalleFinal);

						//insertamos el detalle del quiebre (de tipo BeanReporte)
						quiebre.setDetalle((ArrayList)detalleFinal);
						if (log.isDebugEnabled()) log.debug("quiebre.getDetalle("+i+"): "+quiebre.getDetalle());
						//insertamos el quiebre al reporte (de tipo ArrayList)
						reporte.add(quiebre);
					}
				}
			}
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.toString());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		if (log.isDebugEnabled()) log.debug("reporte final: "+reporte);
		return reporte;
		
	}
	
	/**
	 * Metodo encargado de la generacion del reporte masivo de Trabajadores notificados para Goce Vacacional
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param dbpool String
	 * @param params Map
	 * @param usuario String 
	 * @throws IncompleteConversationalState
	 * @throws RemoteException
	 */
	public void masivoNotificacionesXVacacionesATrabajadores(String dbpool, Map params, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {

			if (log.isDebugEnabled()) log.debug("Ingresara a masivoNotificacionesXVacacionesATrabajadores-Facade");
			QueueDAO qd = new QueueDAO();
			params.put("dbpool", dbpool);
			params.put("observacion", "Reporte de Trabajadores Notificados Goce Vacacional");					
			/*String codUO = (String) params.get("valor");
			params.put("observacion", "Reporte de Trabajadores Notificados Goce Vacacional-UO:"
					//+codUO);
					+codUO.toUpperCase());*/

			qd.encolaReporte(ReporteMasivoFacadeHome.JNDI_NAME,"notificacionesXVacacionesATrabajadores", (HashMap)params, usuario);

		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
	}
	/* FIN ICAPUNAY - TRABAJADORES CON VACIONES PROGRAMADAS ACEPTADAS O ACTIVAS - 28/02/2011 */
	
	/**JVILLACORTA - 29/11/2011 - ALERTA DE SOLICITUDES
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */	
	public ArrayList buscaDetalleNotificaTrab(Map params) throws FacadeException,
			RemoteException {
		
		String fechaIni = (String)params.get("fechaIni");
		String fechaFin = (String)params.get("fechaFin");
		ArrayList reporte = null;
		BeanMensaje beanM = new BeanMensaje();		
		try {			
			DataSource dbpool_sp = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dcsp");
			pe.gob.sunat.rrhh.asistencia.dao.ReporteDAO reportDAO = new pe.gob.sunat.rrhh.asistencia.dao.ReporteDAO(dbpool_sp);			
			
			FechaBean fb = new FechaBean();			
			String ffSem1 = fb.getOtraFecha(fechaIni, 7, Calendar.DATE); //<08-03
			String ffSem2 = fb.getOtraFecha(ffSem1, 7, Calendar.DATE); //<15-03
			String ffSem3 = fb.getOtraFecha(ffSem2, 7, Calendar.DATE); //<22-03
			String ffSem4 = fb.getOtraFecha(ffSem3, 7, Calendar.DATE); //<29-03
			//String ffSem5 = fb.getOtraFecha(ffSem4, 1, Calendar.DATE); //<30-03

			if(params.get("sem").equals("Sem1")){				
				params.put("fIni", fechaIni);
				params.put("fFin", ffSem1);
			}			
			if(params.get("sem").equals("Sem2")){				
				params.put("fIni", ffSem1);
				params.put("fFin", ffSem2);
			}
			if(params.get("sem").equals("Sem3")){				
				params.put("fIni", ffSem2);
				params.put("fFin", ffSem3);
			}
			if(params.get("sem").equals("Sem4")){				
				params.put("fIni", ffSem3);
				params.put("fFin", ffSem4);	
			}
			if(params.get("sem").equals("Sem5")){				
				params.put("fIni", ffSem4);
				//params.put("fFin", ffSem5);	
				params.put("fFin", fechaFin);
			}
			
			List lista = reportDAO.findDetalleNotificaTrab(params);					
			if(log.isDebugEnabled()) log.debug("los_solicitantes:"+lista);
			 
			if (lista != null && lista.size() > 0) {
				reporte = new ArrayList();
				Map rs = null;								
				StringBuffer texto = new StringBuffer("");		
				StringBuffer fechaSolicitud = new StringBuffer("");
				for (int i = 0; i < lista.size(); i++) {
					rs = (HashMap) lista.get(i);				
					texto = new StringBuffer(String.valueOf(rs.get("t02nombres")!=null?rs.get("t02nombres"):"").trim())
							.append( " ")							
							.append(String.valueOf(rs.get("t02ap_pate")!=null?rs.get("t02ap_pate"):"").trim()) //ICAPUNAY 11/08/2011 visualizar anno, numero y fecha solicitud y ape_mate solicitante
							.append( " ")//ICAPUNAY 11/08/2011 visualizar anno, numero y fecha solicitud y ape_mate solicitante							
							.append(String.valueOf(rs.get("t02ap_mate")!=null?rs.get("t02ap_mate"):"").trim());//ICAPUNAY 11/08/2011 visualizar anno, numero y fecha solicitud y ape_mate solicitante
					fechaSolicitud = new StringBuffer(String.valueOf(rs.get("fecha")!=null? new FechaBean((Timestamp)rs.get("fecha")).getFormatDate("dd/MM/yyyy"):"").trim()); //ICAPUNAY 11/08/2011 visualizar fecha solicitud en formato dd/MM/yyyy							
					
					rs.put("emisor", texto.toString().trim());										
					rs.put("fechaSolicitud", fechaSolicitud.toString().trim());//ICAPUNAY 11/08/2011 visualizar fecha solicitud en formato dd/MM/yyyy
					reporte.add(rs);						
				}	
			}			
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return reporte;
	}//FIN - JVILLACORTA - 29/11/2011 - ALERTA DE SOLICITUDES
	
	/*ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA - 22/08/2011 */
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList buscarPeriodosCerradosPorRegimen(String dbpool,String tipoRegimen,String fechaInicio)
			throws IncompleteConversationalState, RemoteException {

		ArrayList periodos = null;
		BeanMensaje beanM = new BeanMensaje();
		try {

			
			T1276DAO t1276DAO = new T1276DAO();
			periodos=t1276DAO.findPeriodosCerradosByRegimen(dbpool,tipoRegimen,fechaInicio);
			
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return periodos;
	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList buscarPeriodosCerradosPorRegimenPorAnio(String dbpool,String tipoRegimen,String anio)
			throws IncompleteConversationalState, RemoteException {

		ArrayList periodosCerradosXanio = null;
		BeanMensaje beanM = new BeanMensaje();
		try {

			
			T1276DAO t1276DAO = new T1276DAO();
			periodosCerradosXanio=t1276DAO.findPeriodosCerradosByRegimenByAnio(dbpool, tipoRegimen, anio);
			
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return periodosCerradosXanio;
	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void masivoResumenMensualUUOO(String dbpool, HashMap params,
			String usuario) throws IncompleteConversationalState,
			RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {

			QueueDAO qd = new QueueDAO();
			params.put("dbpool", dbpool);
			String criterio = params.get("criterio").toString();
			String valor = params.get("valor").toString();
			criterio = criterio.equals("0")?"Registro":criterio.equals("1")?"UUOO":criterio.equals("4")?"Intendencia":"Institucional";

			params.put("observacion","Reporte Mensual de Asistencia por Unidad Organica del Año "+(String)params.get("anio")+" - Criterio: "+criterio+" "+valor);

			qd.encolaReporte(ReporteMasivoFacadeHome.JNDI_NAME,"masivoMensualUUOO", params, usuario);

		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
	}
	
	/** 
	 * Metodo encargado de obtener el reporte de gestion mensual por unidad organica
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param params
	 * @return reporte List
	 * @throws RemoteException
	 */	
	public List resumenMensualUUOO(Map params) throws RemoteException {
		
		List reporte = null;//lista reporte a devolver
		BeanMensaje beanM = new BeanMensaje();
		String dbpool = params.get("dbpool").toString();
		T4707DAO t4707DAO = new T4707DAO(dbpool); //tabla t4707asistencia_h
		T4751DAO t4751DAO = new T4751DAO(dbpool); //tabla t4751MensXUuoo
		DataSource dcsp = sl.getDataSource("java:comp/env/jdbc/dcsp");	
		pe.gob.sunat.rrhh.dao.T12DAO t12DAOrh = new pe.gob.sunat.rrhh.dao.T12DAO(dcsp); //trabaja con la t12uorga
		if (log.isDebugEnabled()) log.debug("ENTRO AL METODO resumenMensualUUOO");
		if (log.isDebugEnabled()) log.debug("params: "+params);
		
		try {			
			String indicador = params.get("indicador").toString(); //h=horas; d=dias; nc=numero de colaboradores; dc=dias por colaborador
			String criterio = params.get("criterio").toString(); //0=registro; 1=unidad organica; 4=intendencia; 3=institucional			
			String cod_cate = params.get("cod_cate").toString().trim(); //Codigo de la categoria seleccionada - ejem: 009 (Otros)
			params.put("categoria", cod_cate);
			List listIntendencias = new ArrayList();		
			reporte = new ArrayList();
			BeanReporte quiebre = null;	
			HashMap map = null;
			String cod_uo = "";
			String subuo = "";
			Map intendencia = new HashMap();
			List listaSubunidades = new ArrayList();
			List listaAnualInte = null;
			List listaDetalle = null;
			List listaDetalleSubuo = null; //02/02/2012 para criterio intendencia o institucional
			List listaSubuuoos = null;			
			Map anualHorasDiasNcDcSubuuoo = null; //para criterio registro, uuoo, intendencia o institucional			
			Map mapaNcDcAnualInte = null; //para criterio registro			
			BigDecimal horasPeriodo = new BigDecimal("0.0"); //horas por periodo de la intendencia
			BigDecimal diasPeriodo = new BigDecimal("0.0"); //dias por periodo de la intendencia
			BigDecimal ncPeriodo = new BigDecimal("0"); //numero de colaboradores por periodo de la intendencia
			BigDecimal dcPeriodo = new BigDecimal("0.0"); //dias por colaborador por periodo de la intendencia
			BigDecimal horasAnual = new BigDecimal("0.0"); //horas anuales de la intendencia
			BigDecimal diasAnual = new BigDecimal("0.0"); //dias anuales de la intendencia
			BigDecimal ncAnual = new BigDecimal("0"); //numero de colaboradores anuales de la intendencia
			BigDecimal dcAnual = new BigDecimal("0.0"); //dias por colaborador anuales de la intendencia
			
			BigDecimal horasPeriodoSubuo = new BigDecimal("0.0"); //horas por periodo de la subuo
			BigDecimal diasPeriodoSubuo = new BigDecimal("0.0"); //dias por periodo de la subuo
			BigDecimal ncPeriodoSubuo = new BigDecimal("0"); //numero de colaboradores por periodo de la subuo
			BigDecimal dcPeriodoSubuo = new BigDecimal("0.0"); //dias por colaborador por periodo de la subuo
			BigDecimal horasAnualSubuo = new BigDecimal("0.0"); //horas anuales de la subuo
			BigDecimal diasAnualSubuo = new BigDecimal("0.0"); //dias anuales de la subuo
			BigDecimal ncAnualSubuo = new BigDecimal("0"); //numero de colaboradores anuales de la subuo
			BigDecimal dcAnualSubuo = new BigDecimal("0.0"); //dias por colaborador anuales de la subuo
			
			//22/03/2012 numero de periodos de la intendencia cuyos valores en el campo mto_dias es > 0
			BigDecimal diasXPeriodo = new BigDecimal("0.0");
			int contador=0;
			//numero de periodos de la intendencia cuyos valores en el campo mto_dias es > 0
			//22/03/2012 numero de periodos de la intendencia cuyos valores en el campo num_cola es > 0
			BigDecimal ncXPeriodo = new BigDecimal("0");
			int contador2=0;
			//numero de periodos de la intendencia cuyos valores en el campo num_cola es > 0
			
			//obteniendo las intendencias segun criterios
			if(criterio.equals("0")){//registro
				listIntendencias = t4707DAO.findIntendencias_rptMenXUo_ByRegistro(params);//puede tener mas de una intendencia, devuelve los campos:(cod_inte y t12des_corta de la inte) y cod_uo del colaborador				
			}else{//unidad organica; intendencia; institucional
				listIntendencias = t4751DAO.findIntendencias_rptMenXUo_ByUUOOByInteByInst(params);//si es uuoo{1 inte}, si es intendencia{1 inte} y si es institu{varias inte}
			}
			if(listIntendencias!=null && listIntendencias.size()>0){
				if (log.isDebugEnabled()) log.debug("listIntendencias: "+listIntendencias);
				for(int i = 0; i < listIntendencias.size(); i++){
					horasAnual = new BigDecimal("0.0");
					diasAnual = new BigDecimal("0.0");
					ncAnual = new BigDecimal("0");
					dcAnual = new BigDecimal("0.0");
					map = new HashMap();
					intendencia = (HashMap)listIntendencias.get(i);
					if (log.isDebugEnabled()) log.debug("intendencia: "+intendencia);
					quiebre = new BeanReporte();
					quiebre.setCodigo(intendencia.get("cod_inte").toString().trim());
					quiebre.setDescripcion(intendencia.get("t12des_corta").toString().trim());
					if(criterio.equals("0")){//registro
						cod_uo = intendencia.get("cod_uo").toString().trim();//cod_uo=2B2100						
					}else if(criterio.equals("1")){//uuoo
						cod_uo = params.get("valor").toString().trim();//valor=2B2100	
					}else{//intendencia o institucional
						cod_uo = quiebre.getCodigo().toString().trim();//intendencia=2B0000 o institucional=100000, 1A0000, 2A0000,etc 
					}
					if (log.isDebugEnabled()) log.debug("cod_uo: "+cod_uo);
					params.put("intendencia", quiebre.getCodigo());	
					if (log.isDebugEnabled()) log.debug("params.put(intendencia): "+quiebre.getCodigo());
					Map mapIntend = t12DAOrh.findIntendenciaByCodUO(cod_uo);//uuoo{100000 o 2B2100} o 2B0000 
					if (log.isDebugEnabled()) log.debug("mapIntend: "+mapIntend);
					if(mapIntend!=null){ //quiebre.getCodigo()=2A0000 SI es intendencia (nivel=1)						
						if (criterio.equals("0") || criterio.equals("1")){//si es intendencia y el criterio es registro o uuoo
							listaSubunidades = t12DAOrh.findUObyCodUo(cod_uo);// devuelve solo la uuoo intendencia/cod_uo=2A0000/t12cod_uorga,t12des_corta,t12cod_nivel
						}else if (criterio.equals("4") || criterio.equals("3")){//si es intendencia y el criterio es intendencia o institucional
							listaSubunidades = t12DAOrh.findUUOOsByIntendencia(cod_uo);//cod_uo=2A0000/ listaSubuuoos=t12cod_uorga,t12des_corta,t12cod_nivel
						}						
						if (listaSubunidades != null  && listaSubunidades.size() > 0){
							quiebre.setDetalle((ArrayList)listaSubunidades);//subunidades (2B0000,2B2100,etc) totales de la intendencia
						}						
					}else{//quiebre.getCodigo()=100000 NO es intendencia (nivel<>1)
						listaSubunidades = t12DAOrh.findUObyCodUo(cod_uo);//cod_uo=100000/ listaSubuuoos=t12cod_uorga,t12des_corta,t12cod_nivel
						if (listaSubunidades != null  && listaSubunidades.size() > 0){
							quiebre.setDetalle((ArrayList)listaSubunidades);//1 subunidad (100000 o 2B2100)
						}					
					}
					if (log.isDebugEnabled()) log.debug("listaSubunidades: "+listaSubunidades);
					if (criterio.equals("0")){//registro
						listaAnualInte = t4707DAO.findCabecera_rptMenXUo_ByRegistro(params);						
					}else{//uuoo o intendencia o institucional						
						listaAnualInte = t4751DAO.findCabecera_rptMenXUo_ByUUOOByInteByInst(params);				
					}
					if (listaAnualInte != null  && listaAnualInte.size() > 0){
						if (log.isDebugEnabled()) log.debug("listaAnualInte.size(): "+listaAnualInte.size());
						if (log.isDebugEnabled()) log.debug("listaAnualInte: "+listaAnualInte);
						for(int pe = 0; pe < listaAnualInte.size(); pe++){
							Map mperiodo = (HashMap)listaAnualInte.get(pe);
							if (log.isDebugEnabled()) log.debug("mperiodo: "+mperiodo);
							String codPer = mperiodo.get("per_mov").toString().trim();
							if (log.isDebugEnabled()) log.debug("codPer: "+codPer);
							if (indicador.equals("h")){//horas
								horasPeriodo= new BigDecimal(mperiodo.get("horas").toString());							
								map.put("horas&"+codPer,horasPeriodo.setScale(1).toString());//horas x periodo de la intendencia
								if (log.isDebugEnabled()) log.debug("map.put(horas&"+codPer+"): "+map.get("horas&"+codPer));
								if (log.isDebugEnabled()) log.debug("horasAnual1: "+horasAnual);
								horasAnual = horasAnual.add(horasPeriodo);//acumula horasAnual de la intendencia
								if (log.isDebugEnabled()) log.debug("horasAnual: "+horasAnual);
							}else if (indicador.equals("d")) {//dias
								diasPeriodo= new BigDecimal(mperiodo.get("dias").toString());								
								map.put("dias&"+codPer,diasPeriodo.setScale(1).toString());//dias x periodo de la intendencia
								if (log.isDebugEnabled()) log.debug("map.put(dias&"+codPer+"): "+map.get("dias&"+codPer));
								if (log.isDebugEnabled()) log.debug("diasAnual1: "+diasAnual);
								diasAnual = diasAnual.add(diasPeriodo);//acumula diasAnual de la intendencia
								if (log.isDebugEnabled()) log.debug("diasAnual: "+diasAnual);
							}else if (indicador.equals("nc")){//numero de colaboradores
								ncPeriodo= new BigDecimal(mperiodo.get("nc").toString());								
								map.put("nc&"+codPer,ncPeriodo.toString());//nc x periodo de la intendencia
								if (log.isDebugEnabled()) log.debug("map.put(nc&"+codPer+"): "+map.get("nc&"+codPer));
								if (log.isDebugEnabled()) log.debug("ncAnual1: "+ncAnual);
								ncAnual = ncAnual.add(ncPeriodo);//acumula ncAnual de la intendencia
								if (log.isDebugEnabled()) log.debug("ncAnual: "+ncAnual);
								//22/03/2012 numero de periodos de la intendencia cuyos valores en el campo num_cola es > 0
								if(!criterio.equals("0")){//uuoo, intendencia e institucional
									ncXPeriodo= new BigDecimal(mperiodo.get("nc").toString());
									if(ncXPeriodo.compareTo(new BigDecimal("0")) > 0){
										contador2=contador2+1;									
									}
								}								
								//numero de periodos de la intendencia cuyos valores en el campo num_cola es > 0
							}else if (indicador.equals("dc")){//dias por colaborador
								//22/03/2012 numero de periodos de la intendencia cuyos valores en el campo mto_dias es > 0
								diasXPeriodo= new BigDecimal(mperiodo.get("dias").toString());
								if(diasXPeriodo.compareTo(new BigDecimal("0")) > 0){
									contador=contador+1;									
								}
								//numero de periodos de la intendencia cuyos valores en el campo mto_dias es > 0
								dcPeriodo= new BigDecimal(mperiodo.get("dc").toString());								
								map.put("dc&"+codPer,dcPeriodo.setScale(2).toString());//dc x periodo de la intendencia
								if (log.isDebugEnabled()) log.debug("map.put(dc&"+codPer+"): "+map.get("dc&"+codPer));
								if (log.isDebugEnabled()) log.debug("dcAnual1: "+dcAnual);
								dcAnual = dcAnual.add(dcPeriodo);//acumula dcAnual de la intendencia
								if (log.isDebugEnabled()) log.debug("dcAnual: "+dcAnual);
							}							
						}//fin for listaAnualInte
						if (indicador.equals("h")){//horas
							map.put("horasAnual",horasAnual.setScale(1).toString());//horasAnual de la intendencia
							if (log.isDebugEnabled()) log.debug("map.put(horasAnual): "+map.get("horasAnual"));
						}else if (indicador.equals("d")) {//dias
							map.put("diasAnual",diasAnual.setScale(1).toString());//diasAnual de la intendencia	
							if (log.isDebugEnabled()) log.debug("map.put(diasAnual): "+map.get("diasAnual"));
						}else if (indicador.equals("nc") || indicador.equals("dc")){
							//BigDecimal numPeriodosInte = new BigDecimal(listaAnualInte.size()); 
							//22/03/2012 numero de periodos de la intendencia cuyos valores en el campo mto_dias es > 0							
							if (log.isDebugEnabled()) log.debug("contador: "+contador);
							BigDecimal numPeriodos_dc = new BigDecimal(String.valueOf(contador));
							if (log.isDebugEnabled()) log.debug("numPeriodos_dc: "+numPeriodos_dc);
							//numero de periodos de la intendencia cuyos valores en el campo mto_dias es > 0
							if (criterio.equals("0")){//registro
								if (log.isDebugEnabled()) log.debug("criterio registro: nc o dc");
								mapaNcDcAnualInte = t4707DAO.findNCAnualDCAnualByInte_rptMenXUo_ByRegistro(params);//ncAnual y dcAnual de la intendencia
								if (mapaNcDcAnualInte != null  && mapaNcDcAnualInte.size() > 0){
									if (log.isDebugEnabled()) log.debug("mapaNcDcAnualInte: "+mapaNcDcAnualInte);
									ncAnual= new BigDecimal(mapaNcDcAnualInte.get("nc").toString());								
									dcAnual= new BigDecimal(mapaNcDcAnualInte.get("dias").toString());									
									map.put("ncAnual",ncAnual.toString());//ncAnual de la intendencia para registro
									if (log.isDebugEnabled()) log.debug("map.put(ncAnual): "+map.get("ncAnual"));
									//22/03/2012 numero de periodos de la intendencia cuyos valores en el campo mto_dias es > 0		
									if (indicador.equals("dc")){
										map.put("dcAnual",dcAnual.divide(numPeriodos_dc,BigDecimal.ROUND_HALF_UP).setScale(2).toString());//ncAnual de la intendencia para registro	
										if (log.isDebugEnabled()) log.debug("map.put(dcAnual): "+map.get("dcAnual"));
									}
									//22/03/2012 numero de periodos de la intendencia cuyos valores en el campo mto_dias es > 0		
								}		
							}else{//uuoo o intendencia o institucional
								if (log.isDebugEnabled()) log.debug("criterio uuoo o intendencia o institucional: nc o dc");
								if (indicador.equals("nc")){
									//22/03/2012 numero de periodos de la intendencia cuyos valores en el campo num_cola es > 0							
									if (log.isDebugEnabled()) log.debug("contador2: "+contador2);
									BigDecimal numPeriodos_nc = new BigDecimal(String.valueOf(contador2));
									if (log.isDebugEnabled()) log.debug("numPeriodos_nc: "+numPeriodos_nc);
									//numero de periodos de la intendencia cuyos valores en el campo num_cola es > 0
									map.put("ncAnual",ncAnual.divide(numPeriodos_nc,BigDecimal.ROUND_HALF_UP).setScale(1).toBigInteger().toString());//ncAnual promedio de la intendencia (ncAnual=ncAnual/numPeriodos) redondeado a entero
									if (log.isDebugEnabled()) log.debug("map.put(ncAnual): "+map.get("ncAnual"));
								}			
								//22/03/2012 numero de periodos de la intendencia cuyos valores en el campo mto_dias es > 0		
								if (indicador.equals("dc")){
									map.put("dcAnual",dcAnual.divide(numPeriodos_dc,BigDecimal.ROUND_HALF_UP).setScale(2).toString());//dcAnual promedio de la intendencia	(dcAnual=dcAnual/numPeriodos) redondeado a 2 decimales									
									if (log.isDebugEnabled()) log.debug("map.put(dcAnual): "+map.get("dcAnual"));
								}
								//22/03/2012 numero de periodos de la intendencia cuyos valores en el campo mto_dias es > 0	
							}
						}						
					}//fin listaAnualInte.size() > 0
					if (criterio.equals("0")){//registro
						if (log.isDebugEnabled()) log.debug("criterio registro");
						listaDetalle = t4707DAO.findDetalle_rptMenXUo_ByRegistro(params);
						if (listaDetalle != null  && listaDetalle.size() > 0){
							subuo = ((HashMap)listaDetalle.get(0)).get("cod_uo").toString().trim();//unica subuo
							params.put("subuuoo",subuo);
							if (log.isDebugEnabled()) log.debug("params.put(subuuoo): "+params.get("subuuoo"));
						}						
					}else if(criterio.equals("1")){//uuoo	
						if (log.isDebugEnabled()) log.debug("criterio uuoo");
						listaDetalle = t4751DAO.findDetalle_rptMenXUo_ByUUOOByInteByInst(params);
						if (listaDetalle != null  && listaDetalle.size() > 0){
							subuo = ((HashMap)listaDetalle.get(0)).get("cod_uo").toString().trim();//unica subuo
							params.put("subuuoo",subuo);
							if (log.isDebugEnabled()) log.debug("params.put(subuuoo): "+params.get("subuuoo"));
						}						
					}else{//intendencia o institucional
						if (log.isDebugEnabled()) log.debug("criterio intendencia o institucional");
						listaDetalle = t4751DAO.findDetalle_rptMenXUo_ByUUOOByInteByInst(params);						
					}
					if (listaDetalle != null  && listaDetalle.size() > 0){
						log.debug("listaDetalle: "+listaDetalle);
						log.debug("listaDetalle.size(): "+listaDetalle.size());
						for (int d = 0; d < listaDetalle.size(); d++){
							Map registro = (HashMap)listaDetalle.get(d);
							log.debug("registro: "+registro);
							String codSubuo = registro.get("cod_uo").toString().trim();
							String codPer = registro.get("per_mov").toString().trim();
							log.debug("codSubuo: "+codSubuo);
							log.debug("codPer: "+codPer);
							if (indicador.equals("h")){//horas	
								horasPeriodoSubuo= new BigDecimal(registro.get("horas").toString());
								map.put("horas&"+codPer+"&"+codSubuo,horasPeriodoSubuo.setScale(1).toString());
								if (log.isDebugEnabled()) log.debug("map.put(horas&"+codPer+"&"+codSubuo+"): "+map.get("horas&"+codPer+"&"+codSubuo));
								if(criterio.equals("1")){//uuoo
									if (log.isDebugEnabled()) log.debug("criterio uuoo");
									if (log.isDebugEnabled()) log.debug("horasAnualSubuo1: "+horasAnualSubuo);
									horasAnualSubuo = horasAnualSubuo.add(horasPeriodoSubuo);//acumula horasAnualSubuo de la unica subuo
									if (log.isDebugEnabled()) log.debug("horasAnualSubuo: "+horasAnualSubuo);
								}								
							}else if (indicador.equals("d")) {//dias
								diasPeriodoSubuo= new BigDecimal(registro.get("dias").toString());
								map.put("dias&"+codPer+"&"+codSubuo,diasPeriodoSubuo.setScale(1).toString());
								if (log.isDebugEnabled()) log.debug("map.put(dias&"+codPer+"&"+codSubuo+"): "+map.get("dias&"+codPer+"&"+codSubuo));
								if(criterio.equals("1")){//uuoo
									if (log.isDebugEnabled()) log.debug("criterio uuoo");
									if (log.isDebugEnabled()) log.debug("diasAnualSubuo1: "+diasAnualSubuo);
									diasAnualSubuo = diasAnualSubuo.add(diasPeriodoSubuo);//acumula diasAnualSubuo de la unica subuo
									if (log.isDebugEnabled()) log.debug("diasAnualSubuo: "+diasAnualSubuo);
								}
							}else if (indicador.equals("nc")){//numero de colaboradores	
								ncPeriodoSubuo= new BigDecimal(registro.get("nc").toString());
								map.put("nc&"+codPer+"&"+codSubuo,ncPeriodoSubuo.toString());
								if (log.isDebugEnabled()) log.debug("map.put(nc&"+codPer+"&"+codSubuo+"): "+map.get("nc&"+codPer+"&"+codSubuo));
								if(criterio.equals("1")){//uuoo
									if (log.isDebugEnabled()) log.debug("criterio uuoo");
									if (log.isDebugEnabled()) log.debug("ncAnualSubuo1: "+ncAnualSubuo);
									ncAnualSubuo = ncAnualSubuo.add(ncPeriodoSubuo);//acumula ncAnualSubuo de la unica subuo
									if (log.isDebugEnabled()) log.debug("ncAnualSubuo: "+ncAnualSubuo);
								}
							}else if (indicador.equals("dc")){//dias por colaborador
								dcPeriodoSubuo= new BigDecimal(registro.get("dc").toString());
								map.put("dc&"+codPer+"&"+codSubuo,dcPeriodoSubuo.setScale(2).toString());
								if (log.isDebugEnabled()) log.debug("map.put(dc&"+codPer+"&"+codSubuo+"): "+map.get("dc&"+codPer+"&"+codSubuo));
								if(criterio.equals("1")){//uuoo
									if (log.isDebugEnabled()) log.debug("criterio uuoo");
									if (log.isDebugEnabled()) log.debug("dcAnualSubuo1: "+dcAnualSubuo);
									dcAnualSubuo = dcAnualSubuo.add(dcPeriodoSubuo);//acumula dcAnualSubuo de la unica subuo
									if (log.isDebugEnabled()) log.debug("dcAnualSubuo: "+dcAnualSubuo);
								}
							}							
						}//fin for					
						if (criterio.equals("0")){//registro
							if (log.isDebugEnabled()) log.debug("criterio registro");
							String codSubuuoo = params.get("subuuoo").toString().trim();//2B2100
							if (log.isDebugEnabled()) log.debug("codSubuuoo: "+codSubuuoo);
							BigDecimal numPeriodosSubuuoo = new BigDecimal(listaDetalle.size());//numero de periodos con valores de la subuuoo del registro
							anualHorasDiasNcDcSubuuoo = t4707DAO.findAnualHorasDiasNcDcByCodUo_rptMenXUo_ByRegistro(params);//valores de todos sus indicadores anuales para la unica subuo
							if (anualHorasDiasNcDcSubuuoo != null  && !anualHorasDiasNcDcSubuuoo.isEmpty()){
								if (log.isDebugEnabled()) log.debug("anualHorasDiasNcDcSubuuoo: "+anualHorasDiasNcDcSubuuoo);							
								if (indicador.equals("h")){//horas								
									map.put("horasAnual&"+codSubuuoo,new BigDecimal(anualHorasDiasNcDcSubuuoo.get("horas").toString()).setScale(1).toString());
									if (log.isDebugEnabled()) log.debug("map.put(horasAnual&"+codSubuuoo+"): "+map.get("horasAnual&"+codSubuuoo));
								}else if (indicador.equals("d")) {//dias								
									map.put("diasAnual&"+codSubuuoo,new BigDecimal(anualHorasDiasNcDcSubuuoo.get("dias").toString()).setScale(1).toString());
									if (log.isDebugEnabled()) log.debug("map.put(diasAnual&"+codSubuuoo+"): "+map.get("diasAnual&"+codSubuuoo));
								}else if (indicador.equals("nc")){//numero de colaboradores							
									map.put("ncAnual&"+codSubuuoo,new BigDecimal(anualHorasDiasNcDcSubuuoo.get("nc").toString()).toString());
									if (log.isDebugEnabled()) log.debug("map.put(ncAnual&"+codSubuuoo+"): "+map.get("ncAnual&"+codSubuuoo));
								}else if (indicador.equals("dc")){//dias por colaborador								
									map.put("dcAnual&"+codSubuuoo,new BigDecimal(anualHorasDiasNcDcSubuuoo.get("dc").toString()).divide(numPeriodosSubuuoo, BigDecimal.ROUND_HALF_UP).setScale(2).toString());
									if (log.isDebugEnabled()) log.debug("map.put(dcAnual&"+codSubuuoo+"): "+map.get("dcAnual&"+codSubuuoo));
								}
							}							
						}else if (criterio.equals("1")){//uuoo
							if (log.isDebugEnabled()) log.debug("criterio uuoo");
							BigDecimal numPeriodosSubuuoo = new BigDecimal(listaDetalle.size());	//numero de periodos con valores de la unica subuuoo
							String codSubuuoo = params.get("subuuoo").toString().trim();//2B2100
							if (log.isDebugEnabled()) log.debug("numPeriodosSubuuoo: "+numPeriodosSubuuoo);
							if (log.isDebugEnabled()) log.debug("codSubuuoo: "+codSubuuoo);
							if (indicador.equals("h")){//horas								
								map.put("horasAnual&"+codSubuuoo,horasAnualSubuo.setScale(1).toString());
								if (log.isDebugEnabled()) log.debug("map.put(horasAnual&"+codSubuuoo+"): "+map.get("horasAnual&"+codSubuuoo));
							}else if (indicador.equals("d")) {//dias								
								map.put("diasAnual&"+codSubuuoo,diasAnualSubuo.setScale(1).toString());	
								if (log.isDebugEnabled()) log.debug("map.put(diasAnual&"+codSubuuoo+"): "+map.get("diasAnual&"+codSubuuoo));
							}else if (indicador.equals("nc")){//numero de colaboradores							
								map.put("ncAnual&"+codSubuuoo,ncAnualSubuo.divide(numPeriodosSubuuoo,BigDecimal.ROUND_HALF_UP).setScale(1).toBigInteger().toString());//ncAnual promedio de la unica subuuoo (ncAnualSubuuoo=ncAnualSubuuoo/numPeriodosSubuuoo) redondeado a entero
								if (log.isDebugEnabled()) log.debug("map.put(ncAnual&"+codSubuuoo+"): "+map.get("ncAnual&"+codSubuuoo));
							}else if (indicador.equals("dc")){//dias por colaborador								
								map.put("dcAnual&"+codSubuuoo,dcAnualSubuo.divide(numPeriodosSubuuoo,BigDecimal.ROUND_HALF_UP).setScale(2).toString());//dcAnual promedio de la unica subuuoo (dcAnualSubuuoo=dcAnualSubuuoo/numPeriodosSubuuoo) redondeado a 2 decimales
								if (log.isDebugEnabled()) log.debug("map.put(dcAnual&"+codSubuuoo+"): "+map.get("dcAnual&"+codSubuuoo));
							}							
						}else{//intendencia o institucional (revisar aca tbien pa intendencia)
							if (log.isDebugEnabled()) log.debug("criterio intendencia o institucional");
							listaSubuuoos = t4751DAO.findUUOOs_rptMenXMov_ByInte(params); //todas las subunidades por intendencia del BeanReporte
							if (listaSubuuoos != null  && listaSubuuoos.size() > 0){
								log.debug("listaSubuuoos: "+listaSubuuoos);
								log.debug("listaSubuuoos.size(): "+listaSubuuoos.size());
								for (int e = 0; e < listaSubuuoos.size(); e++){
									String codSubuuoo = ((HashMap)listaSubuuoos.get(e)).get("cod_uo").toString().trim();
									log.debug("codSubuuoo: "+codSubuuoo);
									params.put("subuuoo",codSubuuoo);
									if (log.isDebugEnabled()) log.debug("params.put(subuuoo): "+params.get("subuuoo"));
									listaDetalleSubuo = t4751DAO.findDetalle_rptMenXUo_ByUUOOByInteByInst(params);//para determinar el numero de periodos con valores por subuo
									if (listaDetalleSubuo != null  && listaDetalleSubuo.size() > 0){
										log.debug("listaDetalleSubuo: "+listaDetalleSubuo);
										log.debug("listaDetalleSubuo.size(): "+listaDetalleSubuo.size());
										BigDecimal numPeriodosSubuuoo = new BigDecimal(listaDetalleSubuo.size());	//numero de periodos con valores por subuo
										log.debug("numPeriodosSubuuoo: "+numPeriodosSubuuoo);
										
										anualHorasDiasNcDcSubuuoo = t4751DAO.findAnualHorasDiasNcDcByCodUo_rptMenXUo_ByUUOOByInteByInst(params);//valores de los indicadores anuales (horas,dias,nc y dc) por subuo
										if (anualHorasDiasNcDcSubuuoo != null  && !anualHorasDiasNcDcSubuuoo.isEmpty()){
											log.debug("anualHorasDiasNcDcSubuuoo: "+anualHorasDiasNcDcSubuuoo);
											BigDecimal ncAnualSubuuoo= new BigDecimal(anualHorasDiasNcDcSubuuoo.get("nc").toString());
											BigDecimal dcAnualSubuuoo= new BigDecimal(anualHorasDiasNcDcSubuuoo.get("dc").toString());								
											log.debug("ncAnualSubuuoo: "+ncAnualSubuuoo);
											log.debug("dcAnualSubuuoo: "+dcAnualSubuuoo);
											if (indicador.equals("h")){//horas								
												map.put("horasAnual&"+codSubuuoo,new BigDecimal(anualHorasDiasNcDcSubuuoo.get("horas").toString()).setScale(1).toString());
												if (log.isDebugEnabled()) log.debug("map.put(horasAnual&"+codSubuuoo+"): "+map.get("horasAnual&"+codSubuuoo));
											}else if (indicador.equals("d")) {//dias								
												map.put("diasAnual&"+codSubuuoo,new BigDecimal(anualHorasDiasNcDcSubuuoo.get("dias").toString()).setScale(1).toString());
												if (log.isDebugEnabled()) log.debug("map.put(diasAnual&"+codSubuuoo+"): "+map.get("diasAnual&"+codSubuuoo));
											}else if (indicador.equals("nc")){//numero de colaboradores							
												map.put("ncAnual&"+codSubuuoo,ncAnualSubuuoo.divide(numPeriodosSubuuoo,BigDecimal.ROUND_HALF_UP).setScale(1).toBigInteger().toString());
												if (log.isDebugEnabled()) log.debug("map.put(ncAnual&"+codSubuuoo+"): "+map.get("ncAnual&"+codSubuuoo));
											}else if (indicador.equals("dc")){//dias por colaborador								
												map.put("dcAnual&"+codSubuuoo,dcAnualSubuuoo.divide(numPeriodosSubuuoo,BigDecimal.ROUND_HALF_UP).setScale(2).toString());
												if (log.isDebugEnabled()) log.debug("map.put(dcAnual&"+codSubuuoo+"): "+map.get("dcAnual&"+codSubuuoo));
											}		
										}
									}															
								}//fin for listaSubuuoos							
							}//fin listaSubuuoos.size() > 0
						}//fin intendencia o institucional
					}//fin listaDetalle									
					
					//insertamos los quiebres de c/intendencia al reporte
					log.debug("quiebre.setMap(map): " + map);
					quiebre.setMap(map);//guarda los valores del BeanReporte intendencia								
					reporte.add(quiebre);
					
					params.put("subuuoo","");
					
				}//fin for listIntendencias				
			}//fin listIntendencias	
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}		
		return reporte;//tiene getCodigo(Intendencia), getDescripcion(nombre de la Intendencia), getDetalle(Subunidades de la Intendencia) y getMap(valores de la Intendencia)		
	}

	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void masivoResumenMensualColaborador(String dbpool, HashMap params,
			String usuario) throws IncompleteConversationalState,
			RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {

			QueueDAO qd = new QueueDAO();
			params.put("dbpool", dbpool);
			String criterio = params.get("criterio").toString();
			String valor = params.get("valor").toString();
			criterio = criterio.equals("0")?"Registro":criterio.equals("1")?"UUOO":criterio.equals("4")?"Intendencia":"Institucional";

			params.put("observacion","Reporte Mensual de Asistencia por Colaborador del Año "+(String)params.get("anio")+" - Criterio: "+criterio+" "+valor);

			qd.encolaReporte(ReporteMasivoFacadeHome.JNDI_NAME,"masivoMensualColaborador", params, usuario);

		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
	}
	
	/** 
	 * Metodo encargado de obtener el reporte de gestion mensual por colaborador
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param params
	 * @return reporte List
	 * @throws RemoteException
	 */	
	public List resumenMensualColaborador(Map params) throws RemoteException {
	
		List reporte = null;//lista reporte a devolver
		BeanMensaje beanM = new BeanMensaje();
		String dbpool = params.get("dbpool").toString();	
		T4753DAO t4753DAO = new T4753DAO(dbpool); //tabla txxxxMensxCola		
		try {			
			String indicador = params.get("indicador").toString(); //h=horas; d=dias;			
			List detalle = null;
			Map trabajador = new HashMap();
			BeanReporte quiebre = null;
			StringBuffer texto = null;
			BigDecimal horasPeriodo = new BigDecimal("0.0").setScale(1); //horas por periodo
			BigDecimal diasPeriodo = new BigDecimal("0.0").setScale(1); //dias por periodo
			BigDecimal horasAnual = new BigDecimal("0.0").setScale(1); //horas anuales del colaborador
			BigDecimal diasAnual = new BigDecimal("0.0").setScale(1); //dias anuales del colaborador
			//reporte = new ArrayList();			
			HashMap map = null;			
			List listaPersonal = t4753DAO.findColaboradores_rptMenXCol(params);			
			
			if(listaPersonal!=null && listaPersonal.size()>0){
				reporte = new ArrayList();
				log.debug("listaPersonal.size(): " + listaPersonal.size());
				log.debug("listaPersonal: " + listaPersonal);
				for(int p = 0; p < listaPersonal.size(); p++){
					horasAnual = new BigDecimal("0.0").setScale(1); //horas anuales del colaborador
					diasAnual = new BigDecimal("0.0").setScale(1); //dias anuales del colaborador
					map = new HashMap();
					trabajador = (HashMap)listaPersonal.get(p);
					log.debug("trabajador: " + trabajador);
					quiebre = new BeanReporte();					
					texto = new StringBuffer(trabajador.get("cod_pers").toString().trim()).append(" - ")
					.append(trabajador.get("t02ap_pate").toString().trim()).append(" ")
					.append(trabajador.get("t02ap_mate").toString().trim()).append(", ")
					.append(trabajador.get("t02nombres").toString().trim());
					quiebre.setCodigo(trabajador.get("cod_pers").toString().trim());
					quiebre.setNombre(texto.toString().trim());
					quiebre.setDescripcion(trabajador.get("cod_uo").toString().trim()+" - "+trabajador.get("t12des_uorga").toString().trim());//Seteamos Cod_UO y Nombre_UO (2A5500-DIV. DESAROLLO)
					quiebre.setUnidad(trabajador.get("cod_uo").toString().trim());//Seteamos Cod_UO (2A5500)
										
					params.put("colaborador",quiebre.getCodigo());	
					params.put("uuooColaborador",quiebre.getUnidad());
					log.debug("colaborador: " + quiebre.getCodigo() + " uuooColaborador: " + quiebre.getUnidad());
					
					detalle = t4753DAO.findHorasDiasByCategoriaByColaborador_rptMenXCol(params);
					if (detalle != null  && detalle.size() > 0){
						log.debug("detalle: "+detalle);						
						for (int d = 0; d < detalle.size(); d++){							
							Map registro = (HashMap)detalle.get(d);
							String codPer = registro.get("per_mov").toString().trim();
							if (indicador.equals("h")){	
								horasPeriodo= new BigDecimal(registro.get("horas").toString()).setScale(1);
								map.put("horas&"+codPer,horasPeriodo.toString());
								horasAnual = horasAnual.add(horasPeriodo);
							}else{
								diasPeriodo= new BigDecimal(registro.get("dias").toString()).setScale(1);
								map.put("dias&"+codPer,diasPeriodo.toString());
								diasAnual = diasAnual.add(diasPeriodo);	
							}							
						}//fin for detalle
						if (indicador.equals("h")){
							map.put("horasAnual",horasAnual.toString());
						}else{
							map.put("diasAnual",diasAnual.toString());		
						}	
					}														
					//insertamos los quiebres de c/categoria al reporte
					log.debug("quiebre.setMap(map): " + map);
					quiebre.setMap(map);//un hashmap guarda diferentes tipos de objetos									
					reporte.add(quiebre);	
				}//fin for listaPersonal						
			}//fin listaPersonal			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		return reporte;
	}

	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void masivoResumenMensualMovimiento(String dbpool, HashMap params,
			String usuario) throws IncompleteConversationalState,
			RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {

			QueueDAO qd = new QueueDAO();
			params.put("dbpool", dbpool);
			String criterio = params.get("criterio").toString();
			String valor = params.get("valor").toString();
			criterio = criterio.equals("0")?"Registro":criterio.equals("1")?"UUOO":criterio.equals("4")?"Intendencia":"Institucional";

			params.put("observacion","Reporte Mensual de Asistencia por Movimiento del Año "+(String)params.get("anio")+" - Criterio: "+criterio+" "+valor);

			qd.encolaReporte(ReporteMasivoFacadeHome.JNDI_NAME,"masivoMensualMovimiento", params, usuario);

		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
	}
	
	/** 
	 * Metodo encargado de obtener el reporte de gestion mensual por movimiento
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param params
	 * @return reporte List
	 * @throws RemoteException
	 */	
	public List resumenMensualMovimiento(Map params) throws RemoteException {
	
		List reporte = null;//lista reporte a devolver
		BeanMensaje beanM = new BeanMensaje();
		String dbpool = params.get("dbpool").toString();	
		T4635DAO categoriaDAO = new T4635DAO(dbpool); //tabla t4635categoria
		T4636DAO moviCategDAO = new T4636DAO(dbpool); //tabla t4636movxcat			
		T4707DAO t4707DAO = new T4707DAO(dbpool); //tabla t4707asistencia_h		
		T4752DAO t4752DAO = new T4752DAO(dbpool); //tabla txxxxMensXMovi		
		try {			
			String indicador = params.get("indicador").toString(); //h=horas; d=dias;			
			String criterio = params.get("criterio").toString(); //0 {registro}, 1{uuoo},4{intendencia} y 3{institucional}			
			List listaCategorias = new ArrayList();//total categorias tipo:BeanReporte
			Map categoria = new HashMap();
			BeanReporte c = null;
			List listaAnualCate = null;
			List listaDetalle = null;
			List listaMovs = null;
			Map anualMovi = null;
			BigDecimal horasPeriodo = new BigDecimal("0.0").setScale(1); //horas por periodo
			BigDecimal diasPeriodo = new BigDecimal("0.0").setScale(1); //dias por periodo
			BigDecimal horasAnual = new BigDecimal("0.0").setScale(1); //horas anuales de la categoria
			BigDecimal diasAnual = new BigDecimal("0.0").setScale(1); //dias anuales de la categoria
			reporte = new ArrayList();
			BeanReporte quiebre = null;				
			HashMap map = null;				
			String cod_cate = "";	
			List lCategorias=categoriaDAO.findAllCategorias();//todas categorias
			log.debug("lCategorias.size(): " + lCategorias.size());
			log.debug("lCategorias: " + lCategorias);
			if(lCategorias!=null && lCategorias.size()>0){
				for(int i = 0; i < lCategorias.size(); i++){
					categoria = (HashMap)lCategorias.get(i);
					c = new BeanReporte();
					c.setCategoria(categoria.get("cod_cate").toString().trim());
					c.setDescripcion(categoria.get("descrip").toString().trim());
					listaCategorias.add(c);
				}
			}			
			if(listaCategorias!=null && listaCategorias.size()>0){
				for(int ca = 0; ca < listaCategorias.size(); ca++){//c/categoria  / Ejem: 006 (Ausencia no justificada) /009 (Otros)
					horasAnual = new BigDecimal("0.0").setScale(1);
					diasAnual = new BigDecimal("0.0").setScale(1);
					map = new HashMap();
					quiebre = (BeanReporte) listaCategorias.get(ca); //quiebre de categoria (cod_cate y descrip)
					cod_cate = quiebre.getCategoria().toString().trim();
					params.put("categoria", cod_cate);					
					List lmovimientos = moviCategDAO.findMovimientosByCategoria(cod_cate);//Ejem: 006{01(TCD),02(TSD),09(ER),12(SNA)y 98(T)} / 008{16(Trab campo) y 76(RS-67)}
					if(lmovimientos!=null && lmovimientos.size()>0){
						log.debug("Movimientos: "+lmovimientos+" de la categoria: "+cod_cate);
					}						
					if (criterio.equals("0")){//registro
						listaAnualCate = t4707DAO.findCabecera_rptMenXMov_ByRegistro(params);	
					}else{//uuoo o intendencia o institucional						
						listaAnualCate = t4752DAO.findCabecera_rptMenXMov_ByUUOOByInteByInst(params);
					}	
					if (listaAnualCate != null  && listaAnualCate.size() > 0){
						if (log.isDebugEnabled()) log.debug("listaAnualCate.size(): "+listaAnualCate.size());
						if (log.isDebugEnabled()) log.debug("listaAnualCate: "+listaAnualCate);						
						for(int pe = 0; pe < listaAnualCate.size(); pe++){
							Map mperiodo = (HashMap)listaAnualCate.get(pe);
							String codPer = mperiodo.get("per_mov").toString().trim();
							if (indicador.equals("h")){
								horasPeriodo= new BigDecimal(mperiodo.get("horas").toString()).setScale(1);
								map.put("horas&"+codPer,horasPeriodo.toString());
								horasAnual = horasAnual.add(horasPeriodo);
							}else{
								diasPeriodo= new BigDecimal(mperiodo.get("dias").toString()).setScale(1);
								map.put("dias&"+codPer,diasPeriodo.toString());									
								diasAnual = diasAnual.add(diasPeriodo);	
							}	
						}//fin for listaAnualCate
						if (indicador.equals("h")){
							map.put("horasAnual",horasAnual.toString());
						}else{
							map.put("diasAnual",diasAnual.toString());		
						}	
					}//fin listaAnualCate.size() > 0
					if (criterio.equals("0")){//registro
						listaDetalle = t4707DAO.findDetalle_rptMenXMov_ByRegistro(params);//valores de movimientos del colaborador agrupados por periodo segun categoria
					}else{//uuoo o intendencia o institucional
						listaDetalle = t4752DAO.findDetalle_rptMenXMov_ByUUOOByInteByInst(params);//valores de movimientos de la uuoo, intendencia o institucional agrupados por periodo segun categoria
					}											
					if (listaDetalle != null  && listaDetalle.size() > 0) {
						log.debug("listaDetalle: "+listaDetalle);
						for (int d = 0; d < listaDetalle.size(); d++){
							Map registro = (HashMap)listaDetalle.get(d);
							String codMov = registro.get("cod_mov").toString().trim();
							String codPer = registro.get("per_mov").toString().trim();
							if (indicador.equals("h")){							
								map.put("horas&"+codMov+"&"+codPer,new BigDecimal(registro.get("horas").toString()).setScale(1).toString());
							}else{
								map.put("dias&"+codMov+"&"+codPer,new BigDecimal(registro.get("dias").toString()).setScale(1).toString());
							}	
						}
					}//fin listaDetalle
					if (criterio.equals("0")){//registro
						listaMovs = t4707DAO.findMovimientos_rptMenXMov_ByRegistro(params);//movimientos con valores del colaborador segun categoria
					}else{//uuoo o intendencia o institucional
						listaMovs = t4752DAO.findMovimientos_rptMenXMov_ByUUOOByInteByInst(params);//movimientos con valores de la uuoo, intendencia o institucional segun categoria
					}					
					if (listaMovs != null  && listaMovs.size() > 0){
						log.debug("listaMovs: "+listaMovs);
						for (int e = 0; e < listaMovs.size(); e++){
							String codMovi = ((HashMap)listaMovs.get(e)).get("cod_mov").toString().trim();
							params.put("movimiento", codMovi);
							if (criterio.equals("0")){//registro
								anualMovi = t4707DAO.findHorasDiasByMovimiento_rptMenXMov_ByRegistro(params);
							}else{//uuoo o intendencia o institucional
								anualMovi = t4752DAO.findHorasDiasByMovimiento_rptMenXMov_ByUUOOByInteByInst(params);
							}															
							if (anualMovi != null  && anualMovi.size() > 0) {
								if (log.isDebugEnabled()) log.debug("anualMovi: "+anualMovi);
								if (indicador.equals("h")){
									map.put("horasAnual&"+codMovi,new BigDecimal(anualMovi.get("horas").toString()).setScale(1).toString());
								}else{
									map.put("diasAnual&"+codMovi,new BigDecimal(anualMovi.get("dias").toString()).setScale(1).toString());		
								}		
							}	
						}//fin for listaMovs
					}//fin listaMovs					
																										
					//insertamos los quiebres de c/categoria al reporte
					log.debug("quiebre.setMap(map): " + map);
					quiebre.setMap(map);//un hashmap guarda diferentes tipos de objetos					
					log.debug("quiebre.setDetalle: " + lmovimientos);
					quiebre.setDetalle((ArrayList)lmovimientos);//Seteamos los movimientos (codigo y nombre) de c/categoria						
					reporte.add(quiebre);					
				}//fin for c/categoria				
			}//fin listaCategorias			
					
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		return reporte;
	}

	/** 
	 * Metodo encargado de obtener el reporte de gestion diario por movimiento
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param params
	 * @return reporte List
	 * @throws RemoteException
	 */	
	public List resumenDiarioMovimiento(Map params) throws RemoteException {
	
		List reporte = null;//lista a devolver
		BeanMensaje beanM = new BeanMensaje();
		String dbpool = params.get("dbpool").toString();
		T4635DAO categoriaDAO = new T4635DAO(dbpool); //tabla t4635categoria	
		T4636DAO moviCategDAO = new T4636DAO(dbpool); //tabla t4636movxcat
		T4707DAO t4707DAO = new T4707DAO(dbpool); //tabla t4707asistencia_h
		T4754DAO t4754DAO = new T4754DAO(dbpool); //tabla txxxxDiarXMovi
		T4752DAO t4752DAO = new T4752DAO(dbpool); //tabla txxxxMensXMovi
		
		try {					
			String indicador = params.get("indicador").toString(); //h=horas; d=dias;
			String criterio = params.get("criterio").toString(); //0 {registro}, 1{uuoo},4{intendencia} y 3{institucional}	
			List listaCategorias = new ArrayList();//total categorias tipo:BeanReporte
			Map categoria = new HashMap();
			BeanReporte c = null;
			List listaMensualCate = null;
			List listaDetalle = null;
			List listaMovs = null;
			Map mensualMovi = null;
			BigDecimal horasFecha = new BigDecimal("0.0").setScale(1); //horas por fecha de la categoria 
			BigDecimal diasFecha = new BigDecimal("0.0").setScale(1); //dias por fecha de la categoria 
			BigDecimal horasMensual = new BigDecimal("0.0").setScale(1); //horas mensuales de la categoria
			BigDecimal diasMensual = new BigDecimal("0.0").setScale(1); //dias mensuales de la categoria		
			reporte = new ArrayList();
			BeanReporte quiebre = null;
			HashMap map = null;	
			String cod_cate = "";
						
			List lCategorias=categoriaDAO.findAllCategorias();//todas categorias
			log.debug("lCategorias.size(): " + lCategorias.size());
			log.debug("lCategorias: " + lCategorias);
			
			if(lCategorias!=null && lCategorias.size()>0){
				for(int i = 0; i < lCategorias.size(); i++){
					categoria = (HashMap)lCategorias.get(i);
					c = new BeanReporte();
					c.setCategoria(categoria.get("cod_cate").toString().trim());
					c.setDescripcion(categoria.get("descrip").toString().trim());
					listaCategorias.add(c);
				}									
				for(int ca = 0; ca < listaCategorias.size(); ca++){//para c/categoria  / Ejem: 006 (Ausencia no justificada) /009 (Otros)
					horasMensual = new BigDecimal("0.0").setScale(1);
					diasMensual = new BigDecimal("0.0").setScale(1);
					map = new HashMap();
					quiebre = (BeanReporte) listaCategorias.get(ca); //quiebre de categoria (cod_cate y descrip)
					cod_cate = quiebre.getCategoria().toString().trim();
					params.put("categoria", cod_cate);					
					List lmovimientos = moviCategDAO.findMovimientosByCategoria(cod_cate);//Ejem: 006{01(TCD),02(TSD),09(ER),12(SNA)y 98(T)} / 008{16(Trab campo) y 76(RS-67)}
					if(lmovimientos!=null && lmovimientos.size()>0){
						log.debug("Movimientos: "+lmovimientos+" de la categoria: "+cod_cate);
					}
					if (criterio.equals("0")){//registro
						listaMensualCate = t4707DAO.findCabecera_rptDiaXMov_ByRegistro(params);//valores de la categoria por fechas de periodo
					}else{//uuoo o intendencia o institucional
						listaMensualCate = t4754DAO.findCabecera_rptDiaXMov_ByUUOOByInteByInst(params);
					}					
					if (listaMensualCate != null  && listaMensualCate.size() > 0){
						if (log.isDebugEnabled()) log.debug("listaMensualCate.size(): "+listaMensualCate.size());
						if (log.isDebugEnabled()) log.debug("listaMensualCate: "+listaMensualCate);
						for(int fe = 0; fe < listaMensualCate.size(); fe++){
							Map mfecha = (HashMap)listaMensualCate.get(fe);
							String fecha = mfecha.get("fec_mov_desc").toString().trim();//dd/MM/yyyy
							if (indicador.equals("h")){
								horasFecha= new BigDecimal(mfecha.get("horas").toString()).setScale(1);
								map.put("horas&"+fecha,horasFecha.toString());
								horasMensual = horasMensual.add(horasFecha);
							}else{
								diasFecha= new BigDecimal(mfecha.get("dias").toString()).setScale(1);
								map.put("dias&"+fecha,diasFecha.toString());									
								diasMensual = diasMensual.add(diasFecha);	
							}							
						}//fin for listaMensualCate
						if (indicador.equals("h")){
							map.put("horasPeriodo",horasMensual.toString());
						}else{
							map.put("diasPeriodo",diasMensual.toString());		
						}						
					}//fin listaMensualCate	
					if (criterio.equals("0")){//registro
						listaDetalle = t4707DAO.findDetalle_rptDiaXMov_ByRegistro(params);//valores de movimientos de la categoria por fechas del periodo
					}else{//uuoo o intendencia o institucional
						listaDetalle = t4754DAO.findDetalle_rptDiaXMov_ByUUOOByInteByInst(params);
					}					
					if (listaDetalle != null  && listaDetalle.size() > 0) {
						log.debug("listaDetalle: "+listaDetalle);
						for (int d = 0; d < listaDetalle.size(); d++){
							Map registro = (HashMap)listaDetalle.get(d);
							String codMov = registro.get("cod_mov").toString().trim();//38
							String fecha = registro.get("fec_mov_desc").toString().trim();//dd/MM/yyyy (02/05/2008)
							if (indicador.equals("h")){							
								map.put("horas&"+fecha+"&"+codMov,new BigDecimal(registro.get("horas").toString()).setScale(1).toString());
							}else{
								map.put("dias&"+fecha+"&"+codMov,new BigDecimal(registro.get("dias").toString()).setScale(1).toString());
							}	
						}
					}//fin listaDetalle
					if (criterio.equals("0")){//registro
						listaMovs = t4707DAO.findMovimientos_rptDiaXMov_ByRegistro(params); //movimientos de la categoria por periodo que tienen valores
					}else{//uuoo o intendencia o institucional
						listaMovs = t4752DAO.findMovimientos_rptDiaXMov_ByUUOOByInteByInst(params);
					}					
					if (listaMovs != null  && listaMovs.size() > 0){
						log.debug("listaMovs: "+listaMovs);
						for (int e = 0; e < listaMovs.size(); e++){
							String codMovi = ((HashMap)listaMovs.get(e)).get("cod_mov").toString().trim();
							params.put("movimiento", codMovi);
							if (criterio.equals("0")){//registro
								mensualMovi = t4707DAO.findHorasDiasByMovimiento_rptDiaXMov_ByRegistro(params);//valores mensuales de horas o dias de c/movimiento
							}else{//uuoo o intendencia o institucional
								mensualMovi = t4752DAO.findHorasDiasByMovimiento_rptDiaXMov_ByUUOOByInteByInst(params);
							}																										
							if (mensualMovi != null  && mensualMovi.size() > 0) {
								if (log.isDebugEnabled()) log.debug("mensualMovi: "+mensualMovi);
								if (indicador.equals("h")){
									map.put("horasPeriodo&"+codMovi,new BigDecimal(mensualMovi.get("horas").toString()).setScale(1).toString());
								}else{
									map.put("diasPeriodo&"+codMovi,new BigDecimal(mensualMovi.get("dias").toString()).setScale(1).toString());		
								}		
							}	
						}//fin for listaMovs
					}//fin listaMovs	
					
					//insertamos los quiebres al reporte
					log.debug("quiebre.setMap(map): " + map);
					quiebre.setMap(map);//un hashmap guarda diferentes tipos de objetos					
					log.debug("quiebre.setDetalle: " + lmovimientos);
					quiebre.setDetalle((ArrayList)lmovimientos);//Seteamos los movimientos (codigo y nombre) de la categoria									
					reporte.add(quiebre);
				}//fin for c/categoria				
			}//fin lCategorias			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		return reporte;
	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void masivoResumenDiarioMovimiento(String dbpool, HashMap params,
			String usuario) throws IncompleteConversationalState,
			RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {

			QueueDAO qd = new QueueDAO();
			params.put("dbpool", dbpool);
			String criterio = params.get("criterio").toString();
			String valor = params.get("valor").toString();
			criterio = criterio.equals("0")?"Registro":criterio.equals("1")?"UUOO":criterio.equals("4")?"Intendencia":"Institucional";

			params.put("observacion","Reporte Diario de Asistencia por Movimiento del Periodo "+(String)params.get("periodo")+" - Criterio: "+criterio+" "+valor);

			qd.encolaReporte(ReporteMasivoFacadeHome.JNDI_NAME,"masivoDiarioMovimiento", params, usuario);

		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
	}
	
	/**
	 * Metodo que redondea un numero float a un numero determinado de decimales. donde numDeci es el numero de decimales a redondear
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param numero float
	 * @param numDeci float
	 * @return numero float
	 */
	public float redondear(float numero,int numDeci) throws IncompleteConversationalState, RemoteException {
		try{
			numero = numero * (float)Math.pow(10,numDeci);
			numero = Math.round(numero);
			numero = numero / (float)Math.pow(10,numDeci);
			return numero;
		}catch(Exception e){
			log.error(e,e);
			return 0;
		}
	}
	
	//Metodos nuevos agregados el 14/03/2012 icapunay
	/** 
	 * Metodo encargado de obtener la cabecera del reporte de gestion mensual por unidad organica (opcion Buscar)
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param params
	 * @return reporte List
	 * @throws RemoteException
	 */	
	public List resumenMensualUUOO_cabecera(Map params) throws RemoteException {
		
		List reporte = null;//lista reporte a devolver
		BeanMensaje beanM = new BeanMensaje();
		String dbpool = params.get("dbpool").toString();
		T4707DAO t4707DAO = new T4707DAO(dbpool); //tabla t4707asistencia_h
		T4751DAO t4751DAO = new T4751DAO(dbpool); //tabla t4751MensXUuoo
		DataSource dcsp = sl.getDataSource("java:comp/env/jdbc/dcsp");	
		pe.gob.sunat.rrhh.dao.T12DAO t12DAOrh = new pe.gob.sunat.rrhh.dao.T12DAO(dcsp); //trabaja con la t12uorga
		if (log.isDebugEnabled()) log.debug("ENTRO AL METODO resumenMensualUUOO_cabecera");
		if (log.isDebugEnabled()) log.debug("params: "+params);
		
		try {			
			String indicador = params.get("indicador").toString(); //h=horas; d=dias; nc=numero de colaboradores; dc=dias por colaborador
			String criterio = params.get("criterio").toString(); //0=registro; 1=unidad organica; 4=intendencia;			
			String cod_cate = params.get("cod_cate").toString().trim(); //Codigo de la categoria seleccionada - ejem: 009 (Otros)
			params.put("categoria", cod_cate);
			List listIntendencias = new ArrayList();		
			reporte = new ArrayList();
			BeanReporte quiebre = null;	
			HashMap map = null;
			String cod_uo = "";			
			Map intendencia = new HashMap();
			List listaSubunidades = new ArrayList();
			List listaAnualInte = null;				
			Map mapaNcDcAnualInte = null; //para criterio registro			
			BigDecimal horasPeriodo = new BigDecimal("0.0"); //horas por periodo de la intendencia
			BigDecimal diasPeriodo = new BigDecimal("0.0"); //dias por periodo de la intendencia
			BigDecimal ncPeriodo = new BigDecimal("0"); //numero de colaboradores por periodo de la intendencia
			BigDecimal dcPeriodo = new BigDecimal("0.0"); //dias por colaborador por periodo de la intendencia
			BigDecimal horasAnual = new BigDecimal("0.0"); //horas anuales de la intendencia
			BigDecimal diasAnual = new BigDecimal("0.0"); //dias anuales de la intendencia
			BigDecimal ncAnual = new BigDecimal("0"); //numero de colaboradores anuales de la intendencia
			BigDecimal dcAnual = new BigDecimal("0.0"); //dias por colaborador anuales de la intendencia
			
			//22/03/2012 numero de periodos de la intendencia cuyos valores en el campo mto_dias es > 0
			BigDecimal diasXPeriodo = new BigDecimal("0.0");
			int contador=0;
			//numero de periodos de la intendencia cuyos valores en el campo mto_dias es > 0
			//22/03/2012 numero de periodos de la intendencia cuyos valores en el campo num_cola es > 0
			BigDecimal ncXPeriodo = new BigDecimal("0");
			int contador2=0;
			//numero de periodos de la intendencia cuyos valores en el campo num_cola es > 0
			
			//obteniendo las intendencias segun criterios
			if(criterio.equals("0")){//registro
				listIntendencias = t4707DAO.findIntendencias_rptMenXUo_ByRegistro(params);//puede tener mas de una intendencia, devuelve los campos:(cod_inte y t12des_corta de la inte) y cod_uo del colaborador				
			}else if (criterio.equals("1") || criterio.equals("4")){//unidad organica o intendencia
				listIntendencias = t4751DAO.findIntendencias_rptMenXUo_ByUUOOByInteByInst(params);//si es uuoo{1 inte}, si es intendencia{1 inte} y si es institu{varias inte}
			}
			if(listIntendencias!=null && listIntendencias.size()>0){
				if (log.isDebugEnabled()) log.debug("listIntendencias: "+listIntendencias);
				for(int i = 0; i < listIntendencias.size(); i++){
					horasAnual = new BigDecimal("0.0");
					diasAnual = new BigDecimal("0.0");
					ncAnual = new BigDecimal("0");
					dcAnual = new BigDecimal("0.0");
					map = new HashMap();
					intendencia = (HashMap)listIntendencias.get(i);
					if (log.isDebugEnabled()) log.debug("intendencia: "+intendencia);
					quiebre = new BeanReporte();
					quiebre.setCodigo(intendencia.get("cod_inte").toString().trim());
					quiebre.setDescripcion(intendencia.get("t12des_corta").toString().trim());
					if(criterio.equals("0")){//registro
						cod_uo = intendencia.get("cod_uo").toString().trim();//cod_uo=2B2100						
					}else if(criterio.equals("1")){//uuoo
						cod_uo = params.get("valor").toString().trim();//valor=2B2100	
					}else if(criterio.equals("4")){//intendencia
						cod_uo = quiebre.getCodigo().toString().trim();//intendencia=2B0000
					}
					if (log.isDebugEnabled()) log.debug("cod_uo: "+cod_uo);
					params.put("intendencia", quiebre.getCodigo());	
					if (log.isDebugEnabled()) log.debug("params.put(intendencia): "+quiebre.getCodigo());
					Map mapIntend = t12DAOrh.findIntendenciaByCodUO(cod_uo);//uuoo{100000 o 2B2100} o 2B0000					
					if(mapIntend!=null){ //quiebre.getCodigo()=2A0000 SI es intendencia (nivel=1)
						if (log.isDebugEnabled()) log.debug("mapIntend: "+mapIntend);
						if (criterio.equals("0") || criterio.equals("1")){//si es intendencia y el criterio es registro o uuoo
							listaSubunidades = t12DAOrh.findUObyCodUo(cod_uo);// devuelve solo la uuoo intendencia/cod_uo=2A0000/t12cod_uorga,t12des_corta,t12cod_nivel
						}else if (criterio.equals("4")){//si es intendencia y el criterio es intendencia
							listaSubunidades = t12DAOrh.findUUOOsByIntendencia(cod_uo);//cod_uo=2A0000/ listaSubuuoos=t12cod_uorga,t12des_corta,t12cod_nivel
						}						
						if (listaSubunidades != null  && listaSubunidades.size() > 0){
							quiebre.setDetalle((ArrayList)listaSubunidades);//subunidades (2B0000,2B2100,etc) totales de la intendencia
						}						
					}else{//quiebre.getCodigo()=100000 NO es intendencia (nivel<>1)
						listaSubunidades = t12DAOrh.findUObyCodUo(cod_uo);//cod_uo=100000/ listaSubuuoos=t12cod_uorga,t12des_corta,t12cod_nivel
						if (listaSubunidades != null  && listaSubunidades.size() > 0){
							quiebre.setDetalle((ArrayList)listaSubunidades);//1 subunidad (100000 o 2B2100)
						}					
					}
					if (log.isDebugEnabled()) log.debug("listaSubunidades: "+listaSubunidades);
					if (criterio.equals("0")){//registro
						listaAnualInte = t4707DAO.findCabecera_rptMenXUo_ByRegistro(params);						
					}else if (criterio.equals("1") || criterio.equals("4")){//uuoo o intendencia						
						listaAnualInte = t4751DAO.findCabecera_rptMenXUo_ByUUOOByInteByInst(params);				
					}
					if (listaAnualInte != null  && listaAnualInte.size() > 0){
						if (log.isDebugEnabled()) log.debug("listaAnualInte.size(): "+listaAnualInte.size());
						if (log.isDebugEnabled()) log.debug("listaAnualInte: "+listaAnualInte);
						for(int pe = 0; pe < listaAnualInte.size(); pe++){
							Map mperiodo = (HashMap)listaAnualInte.get(pe);
							if (log.isDebugEnabled()) log.debug("mperiodo: "+mperiodo);
							String codPer = mperiodo.get("per_mov").toString().trim();
							if (log.isDebugEnabled()) log.debug("codPer: "+codPer);
							if (indicador.equals("h")){//horas
								horasPeriodo= new BigDecimal(mperiodo.get("horas").toString());							
								map.put("horas&"+codPer,horasPeriodo.setScale(1).toString());//horas x periodo de la intendencia
								if (log.isDebugEnabled()) log.debug("map.put(horas&"+codPer+"): "+map.get("horas&"+codPer));
								if (log.isDebugEnabled()) log.debug("horasAnual1: "+horasAnual);
								horasAnual = horasAnual.add(horasPeriodo);//acumula horasAnual de la intendencia
								if (log.isDebugEnabled()) log.debug("horasAnual: "+horasAnual);
							}else if (indicador.equals("d")) {//dias
								diasPeriodo= new BigDecimal(mperiodo.get("dias").toString());								
								map.put("dias&"+codPer,diasPeriodo.setScale(1).toString());//dias x periodo de la intendencia
								if (log.isDebugEnabled()) log.debug("map.put(dias&"+codPer+"): "+map.get("dias&"+codPer));
								if (log.isDebugEnabled()) log.debug("diasAnual1: "+diasAnual);
								diasAnual = diasAnual.add(diasPeriodo);//acumula diasAnual de la intendencia
								if (log.isDebugEnabled()) log.debug("diasAnual: "+diasAnual);
							}else if (indicador.equals("nc")){//numero de colaboradores
								ncPeriodo= new BigDecimal(mperiodo.get("nc").toString());								
								map.put("nc&"+codPer,ncPeriodo.toString());//nc x periodo de la intendencia
								if (log.isDebugEnabled()) log.debug("map.put(nc&"+codPer+"): "+map.get("nc&"+codPer));
								if (log.isDebugEnabled()) log.debug("ncAnual1: "+ncAnual);
								ncAnual = ncAnual.add(ncPeriodo);//acumula ncAnual de la intendencia
								if (log.isDebugEnabled()) log.debug("ncAnual: "+ncAnual);
								//22/03/2012 numero de periodos de la intendencia cuyos valores en el campo num_cola es > 0
								if(!criterio.equals("0")){//uuoo, intendencia e institucional
									ncXPeriodo= new BigDecimal(mperiodo.get("nc").toString());
									if(ncXPeriodo.compareTo(new BigDecimal("0")) > 0){
										contador2=contador2+1;									
									}
								}								
								//numero de periodos de la intendencia cuyos valores en el campo num_cola es > 0
							}else if (indicador.equals("dc")){//dias por colaborador
								//22/03/2012 numero de periodos de la intendencia cuyos valores en el campo mto_dias es > 0
								diasXPeriodo= new BigDecimal(mperiodo.get("dias").toString());
								if(diasXPeriodo.compareTo(new BigDecimal("0")) > 0){
									contador=contador+1;
									if (log.isDebugEnabled()) log.debug("contador"+contador);
								}
								//numero de periodos de la intendencia cuyos valores en el campo mto_dias es > 0
								dcPeriodo= new BigDecimal(mperiodo.get("dc").toString());								
								map.put("dc&"+codPer,dcPeriodo.setScale(2).toString());//dc x periodo de la intendencia
								if (log.isDebugEnabled()) log.debug("map.put(dc&"+codPer+"): "+map.get("dc&"+codPer));
								if (log.isDebugEnabled()) log.debug("dcAnual1: "+dcAnual);
								dcAnual = dcAnual.add(dcPeriodo);//acumula dcAnual de la intendencia
								if (log.isDebugEnabled()) log.debug("dcAnual: "+dcAnual);
							}							
						}//fin for listaAnualInte
						if (indicador.equals("h")){//horas
							map.put("horasAnual",horasAnual.setScale(1).toString());//horasAnual de la intendencia
							if (log.isDebugEnabled()) log.debug("map.put(horasAnual): "+map.get("horasAnual"));
						}else if (indicador.equals("d")) {//dias
							map.put("diasAnual",diasAnual.setScale(1).toString());//diasAnual de la intendencia	
							if (log.isDebugEnabled()) log.debug("map.put(diasAnual): "+map.get("diasAnual"));
						}else if (indicador.equals("nc") || indicador.equals("dc")){
							//BigDecimal numPeriodosInte = new BigDecimal(listaAnualInte.size()); 							
							//22/03/2012 numero de periodos de la intendencia cuyos valores en el campo mto_dias es > 0								
							if (log.isDebugEnabled()) log.debug("contador: "+contador);
							BigDecimal numPeriodos_dc = new BigDecimal(String.valueOf(contador));
							if (log.isDebugEnabled()) log.debug("numPeriodos_dc: "+numPeriodos_dc);
							//numero de periodos de la intendencia cuyos valores en el campo mto_dias es > 0											
							if (criterio.equals("0")){//registro
								if (log.isDebugEnabled()) log.debug("criterio registro: nc o dc");
								mapaNcDcAnualInte = t4707DAO.findNCAnualDCAnualByInte_rptMenXUo_ByRegistro(params);//ncAnual y dcAnual de la intendencia
								if (mapaNcDcAnualInte != null  && mapaNcDcAnualInte.size() > 0){
									if (log.isDebugEnabled()) log.debug("mapaNcDcAnualInte: "+mapaNcDcAnualInte);
									ncAnual= new BigDecimal(mapaNcDcAnualInte.get("nc").toString());								
									dcAnual= new BigDecimal(mapaNcDcAnualInte.get("dias").toString());									
									map.put("ncAnual",ncAnual.toString());//ncAnual de la intendencia para registro
									if (log.isDebugEnabled()) log.debug("map.put(ncAnual): "+map.get("ncAnual"));
									//22/03/2012 numero de periodos de la intendencia cuyos valores en el campo mto_dias es > 0		
									if (indicador.equals("dc")){
										map.put("dcAnual",dcAnual.divide(numPeriodos_dc,BigDecimal.ROUND_HALF_UP).setScale(2).toString());//ncAnual de la intendencia para registro	
										if (log.isDebugEnabled()) log.debug("map.put(dcAnual): "+map.get("dcAnual"));
									}
									//22/03/2012 numero de periodos de la intendencia cuyos valores en el campo mto_dias es > 0											
								}		
							}else if (criterio.equals("1") || criterio.equals("4")){//uuoo o intendencia
								if (log.isDebugEnabled()) log.debug("criterio uuoo o intendencia o institucional: nc o dc");
								if (indicador.equals("nc")){
									//22/03/2012 numero de periodos de la intendencia cuyos valores en el campo num_cola es > 0							
									if (log.isDebugEnabled()) log.debug("contador2: "+contador2);
									BigDecimal numPeriodos_nc = new BigDecimal(String.valueOf(contador2));
									if (log.isDebugEnabled()) log.debug("numPeriodos_nc: "+numPeriodos_nc);
									//numero de periodos de la intendencia cuyos valores en el campo num_cola es > 0
									map.put("ncAnual",ncAnual.divide(numPeriodos_nc,BigDecimal.ROUND_HALF_UP).setScale(1).toBigInteger().toString());//ncAnual promedio de la intendencia (ncAnual=ncAnual/numPeriodos) redondeado a entero
									if (log.isDebugEnabled()) log.debug("map.put(ncAnual): "+map.get("ncAnual"));
								}
								//22/03/2012 numero de periodos de la intendencia cuyos valores en el campo mto_dias es > 0		
								if (indicador.equals("dc")){
									map.put("dcAnual",dcAnual.divide(numPeriodos_dc,BigDecimal.ROUND_HALF_UP).setScale(2).toString());//dcAnual promedio de la intendencia	(dcAnual=dcAnual/numPeriodos) redondeado a 2 decimales									
									if (log.isDebugEnabled()) log.debug("map.put(dcAnual): "+map.get("dcAnual"));
								}
								//22/03/2012 numero de periodos de la intendencia cuyos valores en el campo mto_dias es > 0								
							}
						}						
					}//fin listaAnualInte.size() > 0							
					
					//insertamos los quiebres de c/intendencia al reporte
					log.debug("quiebre.setMap(map): " + map);
					quiebre.setMap(map);//guarda los valores del BeanReporte intendencia								
					reporte.add(quiebre);
					
					//params.put("subuuoo","");
					
				}//fin for listIntendencias				
			}//fin listIntendencias	
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}		
		return reporte;//tiene getCodigo(Intendencia), getDescripcion(nombre de la Intendencia), getDetalle(Subunidades de la Intendencia) y getMap(valores de la Intendencia)		
	}
	
	/** 
	 * Metodo encargado de obtener el detalle del reporte de gestion mensual por unidad organica (opcion Buscar)
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param params
	 * @return detalle Map
	 * @throws RemoteException
	 */	
	public Map resumenMensualUUOO_detalle(Map params) throws RemoteException {
		
		Map detalle = null;//Mapa detalle a devolver
		BeanMensaje beanM = new BeanMensaje();
		String dbpool = params.get("dbpool").toString();
		T4707DAO t4707DAO = new T4707DAO(dbpool); //tabla t4707asistencia_h
		T4751DAO t4751DAO = new T4751DAO(dbpool); //tabla t4751MensXUuoo		
		if (log.isDebugEnabled()) log.debug("ENTRO AL METODO resumenMensualUUOO_detalle");
		if (log.isDebugEnabled()) log.debug("params: "+params);
		
		try {			
			String indicador = params.get("indicador").toString(); //h=horas; d=dias; nc=numero de colaboradores; dc=dias por colaborador
			String criterio = params.get("criterio").toString(); //0=registro; 1=unidad organica; 4=intendencia;			
			String cod_cate = params.get("cod_cate").toString().trim(); //Codigo de la categoria seleccionada - ejem: 009 (Otros)
			params.put("categoria", cod_cate);		
			String subuo = "";			
			List listaDetalle = null;
			List listaDetalleSubuo = null; //02/02/2012 para criterio intendencia
			List listaSubuuoos = null;			
			Map anualHorasDiasNcDcSubuuoo = null; //para criterio registro, uuoo o intendencia		
						
			BigDecimal horasPeriodoSubuo = new BigDecimal("0.0"); //horas por periodo de la subuo
			BigDecimal diasPeriodoSubuo = new BigDecimal("0.0"); //dias por periodo de la subuo
			BigDecimal ncPeriodoSubuo = new BigDecimal("0"); //numero de colaboradores por periodo de la subuo
			BigDecimal dcPeriodoSubuo = new BigDecimal("0.0"); //dias por colaborador por periodo de la subuo
			BigDecimal horasAnualSubuo = new BigDecimal("0.0"); //horas anuales de la subuo
			BigDecimal diasAnualSubuo = new BigDecimal("0.0"); //dias anuales de la subuo
			BigDecimal ncAnualSubuo = new BigDecimal("0"); //numero de colaboradores anuales de la subuo
			BigDecimal dcAnualSubuo = new BigDecimal("0.0"); //dias por colaborador anuales de la subuo
			
			detalle = new HashMap();				

			if (criterio.equals("0")){//registro
				if (log.isDebugEnabled()) log.debug("criterio registro");
				listaDetalle = t4707DAO.findDetalle_rptMenXUo_ByRegistro(params);
				if (listaDetalle != null  && listaDetalle.size() > 0){
					subuo = ((HashMap)listaDetalle.get(0)).get("cod_uo").toString().trim();//unica subuo
					params.put("subuuoo",subuo);
					if (log.isDebugEnabled()) log.debug("params.put(subuuoo): "+params.get("subuuoo"));
				}						
			}else if(criterio.equals("1")){//uuoo	
				if (log.isDebugEnabled()) log.debug("criterio uuoo");
				listaDetalle = t4751DAO.findDetalle_rptMenXUo_ByUUOOByInteByInst(params);
				if (listaDetalle != null  && listaDetalle.size() > 0){
					subuo = ((HashMap)listaDetalle.get(0)).get("cod_uo").toString().trim();//unica subuo
					params.put("subuuoo",subuo);
					if (log.isDebugEnabled()) log.debug("params.put(subuuoo): "+params.get("subuuoo"));
				}						
			}else if(criterio.equals("4")){//intendencia
				if (log.isDebugEnabled()) log.debug("criterio intendencia o institucional");
				listaDetalle = t4751DAO.findDetalle_rptMenXUo_ByUUOOByInteByInst(params);						
			}
			if (listaDetalle != null  && listaDetalle.size() > 0){
				log.debug("listaDetalle: "+listaDetalle);
				log.debug("listaDetalle.size(): "+listaDetalle.size());
				for (int d = 0; d < listaDetalle.size(); d++){
					Map registro = (HashMap)listaDetalle.get(d);
					log.debug("registro: "+registro);
					String codSubuo = registro.get("cod_uo").toString().trim();
					String codPer = registro.get("per_mov").toString().trim();
					log.debug("codSubuo: "+codSubuo);
					log.debug("codPer: "+codPer);
					if (indicador.equals("h")){//horas	
						horasPeriodoSubuo= new BigDecimal(registro.get("horas").toString());
						detalle.put("horas&"+codPer+"&"+codSubuo,horasPeriodoSubuo.setScale(1).toString());
						if (log.isDebugEnabled()) log.debug("detalle.put(horas&"+codPer+"&"+codSubuo+"): "+detalle.get("horas&"+codPer+"&"+codSubuo));
						if(criterio.equals("1")){//uuoo
							if (log.isDebugEnabled()) log.debug("criterio uuoo");
							if (log.isDebugEnabled()) log.debug("horasAnualSubuo1: "+horasAnualSubuo);
							horasAnualSubuo = horasAnualSubuo.add(horasPeriodoSubuo);//acumula horasAnualSubuo de la unica subuo
							if (log.isDebugEnabled()) log.debug("horasAnualSubuo: "+horasAnualSubuo);
						}								
					}else if (indicador.equals("d")) {//dias
						diasPeriodoSubuo= new BigDecimal(registro.get("dias").toString());
						detalle.put("dias&"+codPer+"&"+codSubuo,diasPeriodoSubuo.setScale(1).toString());
						if (log.isDebugEnabled()) log.debug("detalle.put(dias&"+codPer+"&"+codSubuo+"): "+detalle.get("dias&"+codPer+"&"+codSubuo));
						if(criterio.equals("1")){//uuoo
							if (log.isDebugEnabled()) log.debug("criterio uuoo");
							if (log.isDebugEnabled()) log.debug("diasAnualSubuo1: "+diasAnualSubuo);
							diasAnualSubuo = diasAnualSubuo.add(diasPeriodoSubuo);//acumula diasAnualSubuo de la unica subuo
							if (log.isDebugEnabled()) log.debug("diasAnualSubuo: "+diasAnualSubuo);
						}
					}else if (indicador.equals("nc")){//numero de colaboradores	
						ncPeriodoSubuo= new BigDecimal(registro.get("nc").toString());
						detalle.put("nc&"+codPer+"&"+codSubuo,ncPeriodoSubuo.toString());
						if (log.isDebugEnabled()) log.debug("detalle.put(nc&"+codPer+"&"+codSubuo+"): "+detalle.get("nc&"+codPer+"&"+codSubuo));
						if(criterio.equals("1")){//uuoo
							if (log.isDebugEnabled()) log.debug("criterio uuoo");
							if (log.isDebugEnabled()) log.debug("ncAnualSubuo1: "+ncAnualSubuo);
							ncAnualSubuo = ncAnualSubuo.add(ncPeriodoSubuo);//acumula ncAnualSubuo de la unica subuo
							if (log.isDebugEnabled()) log.debug("ncAnualSubuo: "+ncAnualSubuo);
						}
					}else if (indicador.equals("dc")){//dias por colaborador
						dcPeriodoSubuo= new BigDecimal(registro.get("dc").toString());
						detalle.put("dc&"+codPer+"&"+codSubuo,dcPeriodoSubuo.setScale(2).toString());
						if (log.isDebugEnabled()) log.debug("detalle.put(dc&"+codPer+"&"+codSubuo+"): "+detalle.get("dc&"+codPer+"&"+codSubuo));
						if(criterio.equals("1")){//uuoo
							if (log.isDebugEnabled()) log.debug("criterio uuoo");
							if (log.isDebugEnabled()) log.debug("dcAnualSubuo1: "+dcAnualSubuo);
							dcAnualSubuo = dcAnualSubuo.add(dcPeriodoSubuo);//acumula dcAnualSubuo de la unica subuo
							if (log.isDebugEnabled()) log.debug("dcAnualSubuo: "+dcAnualSubuo);
						}
					}							
				}//fin for		

				if (criterio.equals("0")){//registro
					if (log.isDebugEnabled()) log.debug("criterio registro");
					String codSubuuoo = params.get("subuuoo").toString().trim();//2B2100
					if (log.isDebugEnabled()) log.debug("codSubuuoo: "+codSubuuoo);
					BigDecimal numPeriodosSubuuoo = new BigDecimal(listaDetalle.size());//numero de periodos con valores de la subuuoo del registro
					anualHorasDiasNcDcSubuuoo = t4707DAO.findAnualHorasDiasNcDcByCodUo_rptMenXUo_ByRegistro(params);//valores de todos sus indicadores anuales para la unica subuo
					if (anualHorasDiasNcDcSubuuoo != null  && !anualHorasDiasNcDcSubuuoo.isEmpty()){
						if (log.isDebugEnabled()) log.debug("anualHorasDiasNcDcSubuuoo: "+anualHorasDiasNcDcSubuuoo);							
						if (indicador.equals("h")){//horas								
							detalle.put("horasAnual&"+codSubuuoo,new BigDecimal(anualHorasDiasNcDcSubuuoo.get("horas").toString()).setScale(1).toString());
							if (log.isDebugEnabled()) log.debug("detalle.put(horasAnual&"+codSubuuoo+"): "+detalle.get("horasAnual&"+codSubuuoo));
						}else if (indicador.equals("d")) {//dias								
							detalle.put("diasAnual&"+codSubuuoo,new BigDecimal(anualHorasDiasNcDcSubuuoo.get("dias").toString()).setScale(1).toString());
							if (log.isDebugEnabled()) log.debug("detalle.put(diasAnual&"+codSubuuoo+"): "+detalle.get("diasAnual&"+codSubuuoo));
						}else if (indicador.equals("nc")){//numero de colaboradores							
							detalle.put("ncAnual&"+codSubuuoo,new BigDecimal(anualHorasDiasNcDcSubuuoo.get("nc").toString()).toString());
							if (log.isDebugEnabled()) log.debug("detalle.put(ncAnual&"+codSubuuoo+"): "+detalle.get("ncAnual&"+codSubuuoo));
						}else if (indicador.equals("dc")){//dias por colaborador								
							detalle.put("dcAnual&"+codSubuuoo,new BigDecimal(anualHorasDiasNcDcSubuuoo.get("dc").toString()).divide(numPeriodosSubuuoo, BigDecimal.ROUND_HALF_UP).setScale(2).toString());
							if (log.isDebugEnabled()) log.debug("detalle.put(dcAnual&"+codSubuuoo+"): "+detalle.get("dcAnual&"+codSubuuoo));
						}
					}							
				}else if (criterio.equals("1")){//uuoo
					if (log.isDebugEnabled()) log.debug("criterio uuoo");
					BigDecimal numPeriodosSubuuoo = new BigDecimal(listaDetalle.size());	//numero de periodos con valores de la unica subuuoo
					String codSubuuoo = params.get("subuuoo").toString().trim();//2B2100
					if (log.isDebugEnabled()) log.debug("numPeriodosSubuuoo: "+numPeriodosSubuuoo);
					if (log.isDebugEnabled()) log.debug("codSubuuoo: "+codSubuuoo);
					if (indicador.equals("h")){//horas								
						detalle.put("horasAnual&"+codSubuuoo,horasAnualSubuo.setScale(1).toString());
						if (log.isDebugEnabled()) log.debug("detalle.put(horasAnual&"+codSubuuoo+"): "+detalle.get("horasAnual&"+codSubuuoo));
					}else if (indicador.equals("d")) {//dias								
						detalle.put("diasAnual&"+codSubuuoo,diasAnualSubuo.setScale(1).toString());	
						if (log.isDebugEnabled()) log.debug("detalle.put(diasAnual&"+codSubuuoo+"): "+detalle.get("diasAnual&"+codSubuuoo));
					}else if (indicador.equals("nc")){//numero de colaboradores							
						detalle.put("ncAnual&"+codSubuuoo,ncAnualSubuo.divide(numPeriodosSubuuoo,BigDecimal.ROUND_HALF_UP).setScale(1).toBigInteger().toString());//ncAnual promedio de la unica subuuoo (ncAnualSubuuoo=ncAnualSubuuoo/numPeriodosSubuuoo) redondeado a entero
						if (log.isDebugEnabled()) log.debug("detalle.put(ncAnual&"+codSubuuoo+"): "+detalle.get("ncAnual&"+codSubuuoo));
					}else if (indicador.equals("dc")){//dias por colaborador								
						detalle.put("dcAnual&"+codSubuuoo,dcAnualSubuo.divide(numPeriodosSubuuoo,BigDecimal.ROUND_HALF_UP).setScale(2).toString());//dcAnual promedio de la unica subuuoo (dcAnualSubuuoo=dcAnualSubuuoo/numPeriodosSubuuoo) redondeado a 2 decimales
						if (log.isDebugEnabled()) log.debug("detalle.put(dcAnual&"+codSubuuoo+"): "+detalle.get("dcAnual&"+codSubuuoo));
					}							
				}else if (criterio.equals("4")){//intendencia
					if (log.isDebugEnabled()) log.debug("criterio intendencia o institucional");
					listaSubuuoos = t4751DAO.findUUOOs_rptMenXMov_ByInte(params); //todas las subunidades por intendencia del BeanReporte
					if (listaSubuuoos != null  && listaSubuuoos.size() > 0){
						log.debug("listaSubuuoos: "+listaSubuuoos);
						log.debug("listaSubuuoos.size(): "+listaSubuuoos.size());
						for (int e = 0; e < listaSubuuoos.size(); e++){
							String codSubuuoo = ((HashMap)listaSubuuoos.get(e)).get("cod_uo").toString().trim();
							log.debug("codSubuuoo: "+codSubuuoo);
							params.put("subuuoo",codSubuuoo);
							if (log.isDebugEnabled()) log.debug("params.put(subuuoo): "+params.get("subuuoo"));
							listaDetalleSubuo = t4751DAO.findDetalle_rptMenXUo_ByUUOOByInteByInst(params);//para determinar el numero de periodos con valores por subuo
							if (listaDetalleSubuo != null  && listaDetalleSubuo.size() > 0){
								log.debug("listaDetalleSubuo: "+listaDetalleSubuo);
								log.debug("listaDetalleSubuo.size(): "+listaDetalleSubuo.size());
								BigDecimal numPeriodosSubuuoo = new BigDecimal(listaDetalleSubuo.size());	//numero de periodos con valores por subuo
								log.debug("numPeriodosSubuuoo: "+numPeriodosSubuuoo);

								anualHorasDiasNcDcSubuuoo = t4751DAO.findAnualHorasDiasNcDcByCodUo_rptMenXUo_ByUUOOByInteByInst(params);//valores de los indicadores anuales (horas,dias,nc y dc) por subuo
								if (anualHorasDiasNcDcSubuuoo != null  && !anualHorasDiasNcDcSubuuoo.isEmpty()){
									log.debug("anualHorasDiasNcDcSubuuoo: "+anualHorasDiasNcDcSubuuoo);
									BigDecimal ncAnualSubuuoo= new BigDecimal(anualHorasDiasNcDcSubuuoo.get("nc").toString());
									BigDecimal dcAnualSubuuoo= new BigDecimal(anualHorasDiasNcDcSubuuoo.get("dc").toString());								
									log.debug("ncAnualSubuuoo: "+ncAnualSubuuoo);
									log.debug("dcAnualSubuuoo: "+dcAnualSubuuoo);
									if (indicador.equals("h")){//horas								
										detalle.put("horasAnual&"+codSubuuoo,new BigDecimal(anualHorasDiasNcDcSubuuoo.get("horas").toString()).setScale(1).toString());
										if (log.isDebugEnabled()) log.debug("detalle.put(horasAnual&"+codSubuuoo+"): "+detalle.get("horasAnual&"+codSubuuoo));
									}else if (indicador.equals("d")) {//dias								
										detalle.put("diasAnual&"+codSubuuoo,new BigDecimal(anualHorasDiasNcDcSubuuoo.get("dias").toString()).setScale(1).toString());
										if (log.isDebugEnabled()) log.debug("detalle.put(diasAnual&"+codSubuuoo+"): "+detalle.get("diasAnual&"+codSubuuoo));
									}else if (indicador.equals("nc")){//numero de colaboradores							
										detalle.put("ncAnual&"+codSubuuoo,ncAnualSubuuoo.divide(numPeriodosSubuuoo,BigDecimal.ROUND_HALF_UP).setScale(1).toBigInteger().toString());
										if (log.isDebugEnabled()) log.debug("detalle.put(ncAnual&"+codSubuuoo+"): "+detalle.get("ncAnual&"+codSubuuoo));
									}else if (indicador.equals("dc")){//dias por colaborador								
										detalle.put("dcAnual&"+codSubuuoo,dcAnualSubuuoo.divide(numPeriodosSubuuoo,BigDecimal.ROUND_HALF_UP).setScale(2).toString());
										if (log.isDebugEnabled()) log.debug("detalle.put(dcAnual&"+codSubuuoo+"): "+detalle.get("dcAnual&"+codSubuuoo));
									}		
								}
							}															
						}//fin for listaSubuuoos							
					}//fin listaSubuuoos.size() > 0
				}//fin intendencia o institucional
			}//fin listaDetalle						
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}		
		return detalle;//tiene getCodigo(Intendencia), getDescripcion(nombre de la Intendencia), getDetalle(Subunidades de la Intendencia) y getMap(valores de la Intendencia)
	}
	
	/** 
	 * Metodo encargado de obtener la cabecera del reporte de gestion mensual por movimientos (opcion Buscar)
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param params
	 * @return reporte List
	 * @throws RemoteException
	 */	
	public List resumenMensualMovimiento_cabecera(Map params) throws RemoteException {
	
		List reporte = null;//lista reporte a devolver
		BeanMensaje beanM = new BeanMensaje();
		String dbpool = params.get("dbpool").toString();	
		T4635DAO categoriaDAO = new T4635DAO(dbpool); //tabla t4635categoria
		T4636DAO moviCategDAO = new T4636DAO(dbpool); //tabla t4636movxcat			
		T4707DAO t4707DAO = new T4707DAO(dbpool); //tabla t4707asistencia_h		
		T4752DAO t4752DAO = new T4752DAO(dbpool); //tabla t4752MensXMovi		
		try {			
			String indicador = params.get("indicador").toString(); //h=horas; d=dias;			
			String criterio = params.get("criterio").toString(); //0 {registro}, 1{uuoo}	
			List listaCategorias = new ArrayList();//total categorias tipo:BeanReporte
			Map categoria = new HashMap();
			BeanReporte c = null;
			List listaAnualCate = null;			
			BigDecimal horasPeriodo = new BigDecimal("0.0").setScale(1); //horas por periodo
			BigDecimal diasPeriodo = new BigDecimal("0.0").setScale(1); //dias por periodo
			BigDecimal horasAnual = new BigDecimal("0.0").setScale(1); //horas anuales de la categoria
			BigDecimal diasAnual = new BigDecimal("0.0").setScale(1); //dias anuales de la categoria
			reporte = new ArrayList();
			BeanReporte quiebre = null;				
			HashMap map = null;				
			String cod_cate = "";	
			List lCategorias=categoriaDAO.findAllCategorias();//todas categorias
			log.debug("lCategorias.size(): " + lCategorias.size());
			log.debug("lCategorias: " + lCategorias);
			if(lCategorias!=null && lCategorias.size()>0){
				for(int i = 0; i < lCategorias.size(); i++){
					categoria = (HashMap)lCategorias.get(i);
					c = new BeanReporte();
					c.setCategoria(categoria.get("cod_cate").toString().trim());
					c.setDescripcion(categoria.get("descrip").toString().trim());
					listaCategorias.add(c);
				}
			}			
			if(listaCategorias!=null && listaCategorias.size()>0){
				for(int ca = 0; ca < listaCategorias.size(); ca++){//c/categoria  / Ejem: 006 (Ausencia no justificada) /009 (Otros)
					horasAnual = new BigDecimal("0.0").setScale(1);
					diasAnual = new BigDecimal("0.0").setScale(1);
					map = new HashMap();
					quiebre = (BeanReporte) listaCategorias.get(ca); //quiebre de categoria (cod_cate y descrip)
					cod_cate = quiebre.getCategoria().toString().trim();
					params.put("categoria", cod_cate);					
					List lmovimientos = moviCategDAO.findMovimientosByCategoria(cod_cate);//Ejem: 006{01(TCD),02(TSD),09(ER),12(SNA)y 98(T)} / 008{16(Trab campo) y 76(RS-67)}
					if(lmovimientos!=null && lmovimientos.size()>0){
						log.debug("Movimientos: "+lmovimientos+" de la categoria: "+cod_cate);
					}						
					if (criterio.equals("0")){//registro
						listaAnualCate = t4707DAO.findCabecera_rptMenXMov_ByRegistro(params);	
					}else if (criterio.equals("1")){//uuoo					
						listaAnualCate = t4752DAO.findCabecera_rptMenXMov_ByUUOOByInteByInst(params);
					}	
					if (listaAnualCate != null  && listaAnualCate.size() > 0){
						if (log.isDebugEnabled()) log.debug("listaAnualCate.size(): "+listaAnualCate.size());
						if (log.isDebugEnabled()) log.debug("listaAnualCate: "+listaAnualCate);						
						for(int pe = 0; pe < listaAnualCate.size(); pe++){
							Map mperiodo = (HashMap)listaAnualCate.get(pe);
							String codPer = mperiodo.get("per_mov").toString().trim();
							if (indicador.equals("h")){
								horasPeriodo= new BigDecimal(mperiodo.get("horas").toString()).setScale(1);
								map.put("horas&"+codPer,horasPeriodo.toString());
								horasAnual = horasAnual.add(horasPeriodo);
							}else{
								diasPeriodo= new BigDecimal(mperiodo.get("dias").toString()).setScale(1);
								map.put("dias&"+codPer,diasPeriodo.toString());									
								diasAnual = diasAnual.add(diasPeriodo);	
							}	
						}//fin for listaAnualCate
						if (indicador.equals("h")){
							map.put("horasAnual",horasAnual.toString());
						}else{
							map.put("diasAnual",diasAnual.toString());		
						}	
					}//fin listaAnualCate.size() > 0					
																										
					//insertamos los quiebres de c/categoria al reporte
					log.debug("quiebre.setMap(map): " + map);
					quiebre.setMap(map);//un hashmap guarda diferentes tipos de objetos					
					log.debug("quiebre.setDetalle: " + lmovimientos);
					quiebre.setDetalle((ArrayList)lmovimientos);//Seteamos los movimientos (codigo y nombre) de c/categoria						
					reporte.add(quiebre);					
				}//fin for c/categoria				
			}//fin listaCategorias			
					
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		return reporte;
	}
	
	/** 
	 * Metodo encargado de obtener el detalle del reporte de gestion mensual por movimientos (opcion Buscar)
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param params
	 * @return detalle Map
	 * @throws RemoteException
	 */	
	public Map resumenMensualMovimiento_detalle(Map params) throws RemoteException {
	
		Map detalle = null;//Mapa detalle a devolver
		BeanMensaje beanM = new BeanMensaje();
		String dbpool = params.get("dbpool").toString();			
		T4707DAO t4707DAO = new T4707DAO(dbpool); //tabla t4707asistencia_h		
		T4752DAO t4752DAO = new T4752DAO(dbpool); //tabla t4752MensXMovi		
		try {			
			String indicador = params.get("indicador").toString(); //h=horas; d=dias;			
			String criterio = params.get("criterio").toString(); //0 {registro}, 1{uuoo}			
			List listaDetalle = null;
			List listaMovs = null;
			Map anualMovi = null;			
			
			detalle = new HashMap();	
				
			if (criterio.equals("0")){//registro
				listaDetalle = t4707DAO.findDetalle_rptMenXMov_ByRegistro(params);//valores de movimientos del colaborador agrupados por periodo segun categoria
			}else if (criterio.equals("1")){//uuoo
				listaDetalle = t4752DAO.findDetalle_rptMenXMov_ByUUOOByInteByInst(params);//valores de movimientos de la uuoo, intendencia o institucional agrupados por periodo segun categoria
			}											
			if (listaDetalle != null  && listaDetalle.size() > 0) {
				log.debug("listaDetalle: "+listaDetalle);
				for (int d = 0; d < listaDetalle.size(); d++){
					Map registro = (HashMap)listaDetalle.get(d);
					String codMov = registro.get("cod_mov").toString().trim();
					String codPer = registro.get("per_mov").toString().trim();
					if (indicador.equals("h")){							
						detalle.put("horas&"+codMov+"&"+codPer,new BigDecimal(registro.get("horas").toString()).setScale(1).toString());
					}else{
						detalle.put("dias&"+codMov+"&"+codPer,new BigDecimal(registro.get("dias").toString()).setScale(1).toString());
					}	
				}
			}//fin listaDetalle
			if (criterio.equals("0")){//registro
				listaMovs = t4707DAO.findMovimientos_rptMenXMov_ByRegistro(params);//movimientos con valores del colaborador segun categoria
			}else if (criterio.equals("1")){//uuoo
				listaMovs = t4752DAO.findMovimientos_rptMenXMov_ByUUOOByInteByInst(params);//movimientos con valores de la uuoo, intendencia o institucional segun categoria
			}					
			if (listaMovs != null  && listaMovs.size() > 0){
				log.debug("listaMovs: "+listaMovs);
				for (int e = 0; e < listaMovs.size(); e++){
					String codMovi = ((HashMap)listaMovs.get(e)).get("cod_mov").toString().trim();
					params.put("movimiento", codMovi);
					if (criterio.equals("0")){//registro
						anualMovi = t4707DAO.findHorasDiasByMovimiento_rptMenXMov_ByRegistro(params);
					}else if (criterio.equals("1")){//uuoo
						anualMovi = t4752DAO.findHorasDiasByMovimiento_rptMenXMov_ByUUOOByInteByInst(params);
					}															
					if (anualMovi != null  && anualMovi.size() > 0) {
						if (log.isDebugEnabled()) log.debug("anualMovi: "+anualMovi);
						if (indicador.equals("h")){
							detalle.put("horasAnual&"+codMovi,new BigDecimal(anualMovi.get("horas").toString()).setScale(1).toString());
						}else{
							detalle.put("diasAnual&"+codMovi,new BigDecimal(anualMovi.get("dias").toString()).setScale(1).toString());		
						}		
					}	
				}//fin for listaMovs
			}//fin listaMovs
				
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		return detalle;
	}
	
	/** 
	 * Metodo encargado de obtener el reporte de gestion diario por movimiento
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param params
	 * @return reporte List
	 * @throws RemoteException
	 */	
	public List resumenDiarioMovimiento_cabecera(Map params) throws RemoteException {
	
		List reporte = null;//lista a devolver
		BeanMensaje beanM = new BeanMensaje();
		String dbpool = params.get("dbpool").toString();
		T4635DAO categoriaDAO = new T4635DAO(dbpool); //tabla t4635categoria	
		T4636DAO moviCategDAO = new T4636DAO(dbpool); //tabla t4636movxcat
		T4707DAO t4707DAO = new T4707DAO(dbpool); //tabla t4707asistencia_h
		T4754DAO t4754DAO = new T4754DAO(dbpool); //tabla t4754DiarXMovi		
		
		try {					
			String indicador = params.get("indicador").toString(); //h=horas; d=dias;
			String criterio = params.get("criterio").toString(); //0 {registro}, 1{uuoo}
			List listaCategorias = new ArrayList();//total categorias tipo:BeanReporte
			Map categoria = new HashMap();
			BeanReporte c = null;
			List listaMensualCate = null;			
			BigDecimal horasFecha = new BigDecimal("0.0").setScale(1); //horas por fecha de la categoria 
			BigDecimal diasFecha = new BigDecimal("0.0").setScale(1); //dias por fecha de la categoria 
			BigDecimal horasMensual = new BigDecimal("0.0").setScale(1); //horas mensuales de la categoria
			BigDecimal diasMensual = new BigDecimal("0.0").setScale(1); //dias mensuales de la categoria		
			reporte = new ArrayList();
			BeanReporte quiebre = null;
			HashMap map = null;	
			String cod_cate = "";
						
			List lCategorias=categoriaDAO.findAllCategorias();//todas categorias
			log.debug("lCategorias.size(): " + lCategorias.size());
			log.debug("lCategorias: " + lCategorias);
			
			if(lCategorias!=null && lCategorias.size()>0){
				for(int i = 0; i < lCategorias.size(); i++){
					categoria = (HashMap)lCategorias.get(i);
					c = new BeanReporte();
					c.setCategoria(categoria.get("cod_cate").toString().trim());
					c.setDescripcion(categoria.get("descrip").toString().trim());
					listaCategorias.add(c);
				}									
				for(int ca = 0; ca < listaCategorias.size(); ca++){//para c/categoria  / Ejem: 006 (Ausencia no justificada) /009 (Otros)
					horasMensual = new BigDecimal("0.0").setScale(1);
					diasMensual = new BigDecimal("0.0").setScale(1);
					map = new HashMap();
					quiebre = (BeanReporte) listaCategorias.get(ca); //quiebre de categoria (cod_cate y descrip)
					cod_cate = quiebre.getCategoria().toString().trim();
					params.put("categoria", cod_cate);					
					List lmovimientos = moviCategDAO.findMovimientosByCategoria(cod_cate);//Ejem: 006{01(TCD),02(TSD),09(ER),12(SNA)y 98(T)} / 008{16(Trab campo) y 76(RS-67)}
					if(lmovimientos!=null && lmovimientos.size()>0){
						log.debug("Movimientos: "+lmovimientos+" de la categoria: "+cod_cate);
					}
					if (criterio.equals("0")){//registro
						listaMensualCate = t4707DAO.findCabecera_rptDiaXMov_ByRegistro(params);//valores de la categoria por fechas de periodo
					}else if (criterio.equals("1")){//uuoo
						listaMensualCate = t4754DAO.findCabecera_rptDiaXMov_ByUUOOByInteByInst(params);
					}					
					if (listaMensualCate != null  && listaMensualCate.size() > 0){
						if (log.isDebugEnabled()) log.debug("listaMensualCate.size(): "+listaMensualCate.size());
						if (log.isDebugEnabled()) log.debug("listaMensualCate: "+listaMensualCate);
						for(int fe = 0; fe < listaMensualCate.size(); fe++){
							Map mfecha = (HashMap)listaMensualCate.get(fe);
							String fecha = mfecha.get("fec_mov_desc").toString().trim();//dd/MM/yyyy
							if (indicador.equals("h")){
								horasFecha= new BigDecimal(mfecha.get("horas").toString()).setScale(1);
								map.put("horas&"+fecha,horasFecha.toString());
								horasMensual = horasMensual.add(horasFecha);
							}else{
								diasFecha= new BigDecimal(mfecha.get("dias").toString()).setScale(1);
								map.put("dias&"+fecha,diasFecha.toString());									
								diasMensual = diasMensual.add(diasFecha);	
							}							
						}//fin for listaMensualCate
						if (indicador.equals("h")){
							map.put("horasPeriodo",horasMensual.toString());
						}else{
							map.put("diasPeriodo",diasMensual.toString());		
						}						
					}//fin listaMensualCate						
					
					//insertamos los quiebres al reporte
					log.debug("quiebre.setMap(map): " + map);
					quiebre.setMap(map);//un hashmap guarda diferentes tipos de objetos					
					log.debug("quiebre.setDetalle: " + lmovimientos);
					quiebre.setDetalle((ArrayList)lmovimientos);//Seteamos los movimientos (codigo y nombre) de la categoria									
					reporte.add(quiebre);
				}//fin for c/categoria				
			}//fin lCategorias			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		return reporte;
	}
	
	/** 
	 * Metodo encargado de obtener el reporte de gestion diario por movimiento
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param params
	 * @return detalle Map
	 * @throws RemoteException
	 */	
	public Map resumenDiarioMovimiento_detalle(Map params) throws RemoteException {
	
		Map detalle = null;//Mapa detalle a devolver
		BeanMensaje beanM = new BeanMensaje();
		String dbpool = params.get("dbpool").toString();		
		T4707DAO t4707DAO = new T4707DAO(dbpool); //tabla t4707asistencia_h
		T4754DAO t4754DAO = new T4754DAO(dbpool); //tabla t4754DiarXMovi
		T4752DAO t4752DAO = new T4752DAO(dbpool); //tabla t4752MensXMovi
		
		try {					
			String indicador = params.get("indicador").toString(); //h=horas; d=dias;
			String criterio = params.get("criterio").toString(); //0 {registro}, 1{uuoo}			
			List listaDetalle = null;
			List listaMovs = null;
			Map mensualMovi = null;
			
			detalle = new HashMap();
						
			if (criterio.equals("0")){//registro
				listaDetalle = t4707DAO.findDetalle_rptDiaXMov_ByRegistro(params);//valores de movimientos de la categoria por fechas del periodo
			}else if (criterio.equals("1")){//uuoo
				listaDetalle = t4754DAO.findDetalle_rptDiaXMov_ByUUOOByInteByInst(params);
			}					
			if (listaDetalle != null  && listaDetalle.size() > 0) {
				log.debug("listaDetalle: "+listaDetalle);
				for (int d = 0; d < listaDetalle.size(); d++){
					Map registro = (HashMap)listaDetalle.get(d);
					String codMov = registro.get("cod_mov").toString().trim();//38
					String fecha = registro.get("fec_mov_desc").toString().trim();//dd/MM/yyyy (02/05/2008)
					if (indicador.equals("h")){							
						detalle.put("horas&"+fecha+"&"+codMov,new BigDecimal(registro.get("horas").toString()).setScale(1).toString());
					}else{
						detalle.put("dias&"+fecha+"&"+codMov,new BigDecimal(registro.get("dias").toString()).setScale(1).toString());
					}	
				}
			}//fin listaDetalle
			if (criterio.equals("0")){//registro
				listaMovs = t4707DAO.findMovimientos_rptDiaXMov_ByRegistro(params); //movimientos de la categoria por periodo que tienen valores
			}else if (criterio.equals("1")){//uuoo
				listaMovs = t4752DAO.findMovimientos_rptDiaXMov_ByUUOOByInteByInst(params);
			}					
			if (listaMovs != null  && listaMovs.size() > 0){
				log.debug("listaMovs: "+listaMovs);
				for (int e = 0; e < listaMovs.size(); e++){
					String codMovi = ((HashMap)listaMovs.get(e)).get("cod_mov").toString().trim();
					params.put("movimiento", codMovi);
					if (criterio.equals("0")){//registro
						mensualMovi = t4707DAO.findHorasDiasByMovimiento_rptDiaXMov_ByRegistro(params);//valores mensuales de horas o dias de c/movimiento
					}else{//uuoo o intendencia o institucional
						mensualMovi = t4752DAO.findHorasDiasByMovimiento_rptDiaXMov_ByUUOOByInteByInst(params);
					}																										
					if (mensualMovi != null  && mensualMovi.size() > 0) {
						if (log.isDebugEnabled()) log.debug("mensualMovi: "+mensualMovi);
						if (indicador.equals("h")){
							detalle.put("horasPeriodo&"+codMovi,new BigDecimal(mensualMovi.get("horas").toString()).setScale(1).toString());
						}else{
							detalle.put("diasPeriodo&"+codMovi,new BigDecimal(mensualMovi.get("dias").toString()).setScale(1).toString());		
						}		
					}	
				}//fin for listaMovs
			}//fin listaMovs				
					
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		return detalle;
	}
	//fin Metodos nuevos
	
	/*FIN ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA - 22/08/2011 */

	/**NVILLAR - 13/03/2012 - LABOR EXCEPCIONAL
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */	
	public ArrayList consultarSolicitudesLE(Map params) throws FacadeException,
			RemoteException {
		if (log.isDebugEnabled()) log.debug("Ingresara a buscaDetalleSolicitudesLE-Facade");		
		ArrayList reporte = null;
		BeanMensaje beanM = new BeanMensaje();		
		try {			
			DataSource dbpool_sp = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dcsp");
			pe.gob.sunat.rrhh.asistencia.dao.T4821DAO reportDAO = new pe.gob.sunat.rrhh.asistencia.dao.T4821DAO(dbpool_sp);
			
			List lista = reportDAO.findDetalleSolicitudLE(params);					
			if(log.isDebugEnabled()) log.debug("las_solicitudes:"+lista); 
			if (lista != null && lista.size() > 0) {
				reporte = new ArrayList();
				Map rs = null;				
				StringBuffer solicitud = new StringBuffer("");
				String det_estado = new String();				
				
				for (int i = 0; i < lista.size(); i++) {
					rs = (HashMap) lista.get(i);//fila				
					solicitud = new StringBuffer(String.valueOf(rs.get("anio")!=null?rs.get("anio"):"").trim())
							.append( "-")		
							.append(String.valueOf(rs.get("numero")!=null?rs.get("numero"):"").trim());
					rs.put("solicitud", solicitud.toString());

					if(rs.get("estado").toString().trim().equals(constantes.leePropiedad("ACCION_INICIAR"))){
						det_estado = "Iniciada";
					}
					if(rs.get("estado").toString().trim().equals(constantes.leePropiedad("ACCION_APROBAR"))){
						det_estado = "Aprobada";
					}
					if(rs.get("estado").toString().trim().equals(constantes.leePropiedad("ACCION_RECHAZAR"))){
						det_estado = "Rechazada";
					}
					
					rs.put("det_estado", det_estado);					
					
					reporte.add(rs);
					if(log.isDebugEnabled()) log.debug("OBJETO reporte - FACADE:"+reporte);
				}	
			}		
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		return reporte;
	}//FIN - NVILLAR - 13/03/2012 - LABOR EXCEPCIONAL
	
	/**NVILLAR - 02/05/2012 - LABOR EXCEPCIONAL
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */	
	public ArrayList consultarSolicitudesLE2(Map params) throws FacadeException,
			RemoteException {
		if (log.isDebugEnabled()) log.debug("Ingresara a buscaDetalleSolicitudesLE-Facade");		
		ArrayList reporte = null;
		BeanMensaje beanM = new BeanMensaje();		
		try {			
			DataSource dbpool_sp = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dcsp");
			pe.gob.sunat.rrhh.asistencia.dao.T4821DAO reportDAO = new pe.gob.sunat.rrhh.asistencia.dao.T4821DAO(dbpool_sp);
			
			List lista = reportDAO.findDetalleSolicitudLE2(params);					
			if(log.isDebugEnabled()) log.debug("las_solicitudes:"+lista);
			 
			if (lista != null && lista.size() > 0) {
				reporte = new ArrayList();
				Map rs = null;				
				StringBuffer solicitud = new StringBuffer("");
				String det_estado = new String();				
				for (int i = 0; i < lista.size(); i++) {
					rs = (HashMap) lista.get(i);//fila				
					solicitud = new StringBuffer(String.valueOf(rs.get("anio")!=null?rs.get("anio"):"").trim())
							.append( "-")		
							.append(String.valueOf(rs.get("numero")!=null?rs.get("numero"):"").trim());
					rs.put("solicitud", solicitud.toString());
					if(rs.get("estado").toString().trim().equals(constantes.leePropiedad("ACCION_INICIAR"))){
						det_estado = "Iniciada";
					}
					if(rs.get("estado").toString().trim().equals(constantes.leePropiedad("ACCION_APROBAR"))){
						det_estado = "Aprobada";
					}
					if(rs.get("estado").toString().trim().equals(constantes.leePropiedad("ACCION_RECHAZAR"))){
						det_estado = "Rechazada";
					}
					rs.put("det_estado", det_estado);					
					reporte.add(rs);
					if(log.isDebugEnabled()) log.debug("OBJETO reporte - FACADE:"+reporte);
				}	
			}		
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		return reporte;
	}//FIN - NVILLAR - 02/05/2012 - LABOR EXCEPCIONAL
	
	/**NVILLAR - 19/03/2012 - LABOR EXCEPCIONAL
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */	
	public ArrayList buscaDetalleConsultaSolicitudesLE(Map params) throws FacadeException,
			RemoteException {
		if (log.isDebugEnabled()) log.debug("Ingresara a buscaDetalleSolicitudesLE-Facade");	
		ArrayList reporte = null;
		BeanMensaje beanM = new BeanMensaje();		
		try {			
			DataSource dbpool_sp = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dcsp");
			pe.gob.sunat.rrhh.asistencia.dao.T4821DAO reportDAO = new pe.gob.sunat.rrhh.asistencia.dao.T4821DAO(dbpool_sp);	
			List lista = reportDAO.buscaDetalleConsultaSolicitudesLE(params);					
			if(log.isDebugEnabled()) log.debug("las_solicitudes:"+lista);
			if (lista != null && lista.size() > 0) {
				reporte = new ArrayList();
				Map rs = null;
				for (int i = 0; i < lista.size(); i++) {
					rs = (HashMap) lista.get(i);//fila				
					reporte.add(rs);
					if(log.isDebugEnabled()) log.debug("OBJETO reporte - FACADE:"+reporte);
				}	
			}			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		return reporte;
	}//FIN - NVILLAR - 19/03/2012 - LABOR EXCEPCIONAL
	
	/**NVILLAR - 02/05/2012 - LABOR EXCEPCIONAL
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */	
	public ArrayList buscaDetalleConsultaSolicitudesLE2(Map params) throws FacadeException,
			RemoteException {
		if (log.isDebugEnabled()) log.debug("Ingresara a buscaDetalleSolicitudesLE2-Facade");	
		ArrayList reporte = null;
		BeanMensaje beanM = new BeanMensaje();		
		try {			
			DataSource dbpool_sp = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dcsp");
			pe.gob.sunat.rrhh.asistencia.dao.T4821DAO reportDAO = new pe.gob.sunat.rrhh.asistencia.dao.T4821DAO(dbpool_sp);	
			List lista = reportDAO.buscaDetalleConsultaSolicitudesLE2(params);					
			if(log.isDebugEnabled()) log.debug("las_solicitudes:"+lista);
			if (lista != null && lista.size() > 0) {
				reporte = new ArrayList();
				Map rs = null;								
				for (int i = 0; i < lista.size(); i++) {
					rs = (HashMap) lista.get(i);//fila				
					reporte.add(rs);
					if(log.isDebugEnabled()) log.debug("OBJETO reporte - FACADE:"+reporte);
				}	
			}			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		return reporte;
	}//FIN - NVILLAR - 02/05/2012 - LABOR EXCEPCIONAL
	
	/** //INICIO - NVILLAR - 19/04/2012 - LABOR EXCEPCIONAL
	 * Metodo encargado de obtener el reporte de gestion mensual por unidad organica
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param params
	 * @return reporte List
	 * @throws RemoteException
	 */	
	public ArrayList resumenSolLabExcepcional(Map params) throws RemoteException {
		
		ArrayList reporte = null;//lista reporte a devolver
		BeanMensaje beanM = new BeanMensaje();
		if (log.isDebugEnabled()) log.debug("ENTRO AL METODO resumenSolLabExcepcional");
		if (log.isDebugEnabled()) log.debug("params: "+params);
		try {
			DataSource dbpool_sp = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dcsp");
			pe.gob.sunat.rrhh.asistencia.dao.T4821DAO reportDAO = new pe.gob.sunat.rrhh.asistencia.dao.T4821DAO(dbpool_sp);
			List lista = reportDAO.findDetalleSolicitudLE(params);
			if(log.isDebugEnabled()) log.debug("las_solicitudes:"+lista);
			if (lista != null && lista.size() > 0) {
				reporte = new ArrayList();
				Map rs = null;
				String det_estado = new String();
				for (int i = 0; i < lista.size(); i++) {
					rs = (HashMap) lista.get(i);//fila	
					if(rs.get("estado").toString().trim().equals(constantes.leePropiedad("ACCION_INICIAR"))){
						det_estado = "Iniciada";
					}
					if(rs.get("estado").toString().trim().equals(constantes.leePropiedad("ACCION_APROBAR"))){
						det_estado = "Aprobada";
					}
					if(rs.get("estado").toString().trim().equals(constantes.leePropiedad("ACCION_RECHAZAR"))){
						det_estado = "Rechazada";
					}
					rs.put("det_estado", det_estado);
					
					String anio = rs.get("anio").toString();
					String numero = rs.get("numero").toString();
					String licencia = rs.get("licencia").toString();
					String fecha_desc = rs.get("fecha_desc").toString();
					
					if (log.isDebugEnabled()) log.debug("anio: "+anio);
		            if (log.isDebugEnabled()) log.debug("numero: "+numero);
		            if (log.isDebugEnabled()) log.debug("licencia: "+licencia);
		            if (log.isDebugEnabled()) log.debug("fecha_desc: "+fecha_desc);
		            Map detalles = new HashMap();
		            //HashMap params = new HashMap();        	
		            detalles.put("anio", anio);
		            detalles.put("numero", numero);
		            detalles.put("licencia", licencia);
		            detalles.put("fecha_desc", fecha_desc);
		            List listaDetalle = reportDAO.buscaDetalleConsultaSolicitudesLE(detalles);
		            rs.put("listaDetalle",listaDetalle);
		            reporte.add(rs);
		            if(log.isDebugEnabled()) log.debug("OBJETO reporte - FACADE:"+reporte);
				  }
			  }	
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}		
		return reporte;		
	}//FIN - NVILLAR - 19/04/2012 - LABOR EXCEPCIONAL
	
	/** //INICIO - NVILLAR - 02/05/2012 - LABOR EXCEPCIONAL
	 * Metodo encargado de obtener el reporte de gestion mensual por unidad organica
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param params
	 * @return reporte List
	 * @throws RemoteException
	 */	
	public ArrayList resumenSolLabExcepcional2(Map params) throws RemoteException {
		ArrayList reporte = null;//lista reporte a devolver
		BeanMensaje beanM = new BeanMensaje();
		if (log.isDebugEnabled()) log.debug("ENTRO AL METODO resumenSolLabExcepcional");
		if (log.isDebugEnabled()) log.debug("params: "+params);
		try {
			DataSource dbpool_sp = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dcsp");
			pe.gob.sunat.rrhh.asistencia.dao.T4821DAO reportDAO = new pe.gob.sunat.rrhh.asistencia.dao.T4821DAO(dbpool_sp);
			List lista = reportDAO.findDetalleSolicitudLE2(params);
			if(log.isDebugEnabled()) log.debug("las_solicitudes:"+lista);
			if (lista != null && lista.size() > 0) {
				reporte = new ArrayList();
				Map rs = null;
				String det_estado = new String();
				for (int i = 0; i < lista.size(); i++) {
					rs = (HashMap) lista.get(i);//fila	
					if(rs.get("estado").toString().trim().equals(constantes.leePropiedad("ACCION_INICIAR"))){
						det_estado = "Iniciada";
					}
					if(rs.get("estado").toString().trim().equals(constantes.leePropiedad("ACCION_APROBAR"))){
						det_estado = "Aprobada";
					}
					if(rs.get("estado").toString().trim().equals(constantes.leePropiedad("ACCION_RECHAZAR"))){
						det_estado = "Rechazada";
					}
					rs.put("det_estado", det_estado);
					String anio = rs.get("anio").toString();
					String numero = rs.get("numero").toString();
					String registro = rs.get("registro").toString();
					String licencia = rs.get("licencia").toString();
					String fecha = rs.get("fecha_desc").toString();
					if (log.isDebugEnabled()) log.debug("anio: "+anio);
		            if (log.isDebugEnabled()) log.debug("numero: "+numero);
		            if (log.isDebugEnabled()) log.debug("registro: "+registro);
		            if (log.isDebugEnabled()) log.debug("licencia: "+licencia);
		            if (log.isDebugEnabled()) log.debug("fecha: "+fecha);
		            Map detalles = new HashMap();
		            //HashMap params = new HashMap();        	
		            detalles.put("anio", anio);
		            detalles.put("numero", numero);
		            detalles.put("registro", registro);
		            detalles.put("licencia", licencia);
		            detalles.put("fecha", fecha);
		            List listaDetalle = reportDAO.buscaDetalleConsultaSolicitudesLE2(detalles);
		            rs.put("listaDetalle",listaDetalle);
		            reporte.add(rs);
		            if(log.isDebugEnabled()) log.debug("OBJETO reporte - FACADE:"+reporte);
				  }
			  }	
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}		
		return reporte;		
	}//FIN - NVILLAR - 02/05/2012 - LABOR EXCEPCIONAL
	
	/**INICIO - NVILLAR 23/04/2012 - LABOR EXCEPCIONAL
	 * Metodo encargado de encolar el reporte de Labor Excepcional
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param dbpool
	 * @param params
	 * @param usuario
	 * @return
	 * @throws RemoteException
	 */
	public ByteArrayOutputStream masivoLaborExcepcional1(String dbpool, HashMap params, String usuario)
			throws RemoteException {
		if (log.isDebugEnabled()) {
		      log.debug(this.toString()
		      .concat( " INICIO - masivoLaborExcepcional1 FACADE)"));
		    }
		
		ByteArrayOutputStream liquidacionPDF = new ByteArrayOutputStream();
		document = new PDFDocumento(PDFDocumento.A4);
	    Map regTrab = null;
	    Map cabecera= null;
	    ArrayList reporte= null;
	    Map datosRecibo= null;
	    List detalleLiquidacion = null;
	    Map totalesLiquidacion = null;
		
	    try {
	        String codPers = ((String) params.get("codigo")).trim();      
	        //datos generales      
	        document.setWriter(liquidacionPDF);
	        document.getWriter().setEncryption(false, null, null, PDFDocumento.IMPRIMR);
	        // Meta Data del Archivo
	        Map meta = new HashMap();
	        meta.put("title","SIRH - SUNAT");
	        meta.put("subject",(String) "");
	        meta.put("keywords",(String) "");
	        meta.put("creator", codPers);
	        meta.put("author",codPers);
	        meta.put("expires","0");
	        document.setMetadata((HashMap)meta);
	        //definimos los margenes del documento
	        document.setMargins(30,30,20,15);
	        //cargamos el pie de pagina
	        this.getPieFormato();        
	        //abrimos el documento para escritura
	        document.abrir();
	        //cargamos la cabecera de la ficha 
	        this.getCabecera();            
	        
	        ReporteFacade reporteFacade = new ReporteFacade();
	        //reporte = this.resumenSolLabExcepcional(params);
	        reporte = reporteFacade.resumenSolLabExcepcional(params);
	        if (log.isDebugEnabled()) log.debug("reporte: " + reporte);
	        
	        if (null == reporte) {
	      	StringBuffer consultaSol = new StringBuffer("NO PRESENTA SOLICITUDES PARA LOS CRITERIOS DE BUSQUEDA");
	          document.setFuente(PDFDocumento.TIMES, 13, PDFDocumento.BOLD | PDFDocumento.UNDERLINE);                        
	          document.setParrafo(consultaSol.toString(),document.getFuente());
	          document.getParrafo().setAlignment(PDFTabla.ALIGN_CENTER);
	          document.agregar(document.getParrafo());
	        } else {
	          this.getDatosSolicitud(params, reporte);            
	        }
           //cerramos el documento
	        document.cerrar(); 
	      } catch (DAOException e) {
	        log.error(e, e);
	        throw new FacadeException(this, e.getMessage());
	      } catch (Exception e) {
	        beanMsg = new MensajeBean();
	        beanMsg.setMensajeerror(e.getMessage());
	        beanMsg.setMensajesol("ERROR - Por favor intente nuevamente.");
	        log.error(e, e);
	        throw new FacadeException(this, beanMsg);
	      } finally {      
	      } 
	      if (log.isDebugEnabled()) {
	        log.debug(this.toString() 
	        .concat( " FIN - masivoLaborExcepcional1 FACADE"));
	      }
	      return liquidacionPDF;
	    
	    
	    
		/*BeanMensaje beanM = new BeanMensaje();
		try {
			QueueDAO qd = new QueueDAO();
			params.put("dbpool", dbpool);
			log.debug("masivoLaborExcepcional - params:" + params);//JVV
			log.debug("masivoLaborExcepcional - usuario:" + usuario);//JVV
			params.put("observacion", "Reporte de Solicitudes de Labor Excepcional del "
					+ (String) params.get("fechaIni") + " al " + (String) params.get("fechaFin"));
			qd.encolaReporte(ReporteMasivoFacadeHome.JNDI_NAME,
					"laborExcepcional1", params, usuario);
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}*/
	}// FIN- NVILLAR 23/04/2012 - LABOR EXCEPCIONAL
	
	  
	  /**
	     * Método encargado de generar el pie del pdf 
	     */  
	  private void getPieFormato() {
	    if (log.isDebugEnabled()) {
	      log.debug(this.toString()
	      .concat( " INICIO - getPieFormato()"));
	    }
	    try {
	      FechaBean fb = new FechaBean();
	      String fechaHora = fb.getFormatDate("dd/MM/yyyy 'a las' HH:mm:ss");
	      document.setFuente(PDFDocumento.HELVETICA, 7, PDFDocumento.ITALIC);
	      document.setFrase("*Fuente : SIGA - SUNAT. Liquidación de Pago CAS consultada el "+fechaHora,document.getFuente());            
	      document.setCabecera(document.getFrase(), false);
	      document.getCabecera().setAlignment(PDFTabla.ALIGN_RIGHT);
	      document.getCabecera().setBorder(PDFTabla.NO_BORDER);
	      document.setFooter(document.getCabecera());
	    } catch (Exception e) {
	      beanMsg = new MensajeBean();
	      beanMsg.setMensajeerror(e.getMessage());
	      beanMsg.setMensajesol("ERROR - Por favor intente nuevamente.");
	      log.error(e, e);
	      throw new FacadeException(this, beanMsg);
	    } finally {      
	    } 
	    if (log.isDebugEnabled()) {
	      log.debug(this.toString() 
	      .concat( " FIN - getPieFormato()"));
	    }
	  }
	  	
	   /**
	     * Cabecera de la Reporte Solicitud Labor Excepcional 
	     * @param params
	     */
	  private void getCabecera() {
	    if (log.isDebugEnabled()) {
	      log.debug(this.toString()
	      .concat( " INICIO - getCabecera()"));
	    }
	    try {            
	      document.setTabla(1); 
	      document.getTabla().setPadding(1);
	      document.getTabla().setSpacing(1);
	      document.getTabla().setBorderWidth(0);
	      document.getTabla().setBackgroundColor(PDFDocumento.WHITE);
	      document.getTabla().setDefaultCellBorderColor(PDFDocumento.WHITE);
	      document.getTabla().setDefaultHorizontalAlignment(PDFTabla.ALIGN_CENTER);
	      document.getTabla().setDefaultVerticalAlignment(PDFTabla.ALIGN_MIDDLE);
	      document.getTabla().setTableFitsPage(false);
	      document.getTabla().setCellsFitPage(true);
	      int headerwidths[] = {100};
	      document.getTabla().setWidths(headerwidths);
	      document.getTabla().setWidth(95);
	      document.setFuente(PDFDocumento.HELVETICA, 9, PDFDocumento.ITALIC);
	      document.setParrafo("",document.getFuente());            
	      document.getParrafo().add("Intendencia Nacional de Recursos Humanos - INRH");                        
	      document.getParrafo().add("\n");document.getParrafo().add("Intranet en Línea ");
	      document.getTabla().setCelda(document.getParrafo());
	      document.getTabla().getCelda().setHorizontalAlignment(PDFTabla.ALIGN_RIGHT);
	      document.getTabla().getCelda().setVerticalAlignment(PDFTabla.ALIGN_MIDDLE);
	      document.getTabla().addCell(document.getTabla().getCelda());
	      document.agregar(document.getTabla());
	    } catch (Exception e) {
	      beanMsg = new MensajeBean();
	      beanMsg.setMensajeerror(e.getMessage());
	      beanMsg.setMensajesol("ERROR - Por favor intente nuevamente.");
	      log.error(e, e);
	      throw new FacadeException(this, beanMsg);
	    } finally {      
	    } 
	    if (log.isDebugEnabled()) {
	      log.debug(this.toString() 
	      .concat( " FIN - getCabecera()"));
	    }
	    }
	 	
	   /**
	    * Método encargado de obtener los datos del Trabajador 
	    * @param cabecera Map
	    * @param reporte List
	    */
	     
	  private void getDatosSolicitud(Map cabecera, List reporte) {
	    if (log.isDebugEnabled()) {
	      log.debug(this.toString()
	      .concat( " INICIO - getDatosTrabajador()"));
	    }
	    try {
	      StringBuffer consultaSol = null;
	      String labor = (String) cabecera.get("labor");
	      /*Título del PDF*/
	      if(labor.equals("Labor Excepcional")){
	    	  consultaSol = new StringBuffer("SOLICITUDES DE LABOR EXCEPCIONAL");
		  }
		  if(labor.equals("Compensacion")){
			  consultaSol = new StringBuffer("SOLICITUDES DE COMPENSACION");
		  }

	      document.setFuente(PDFDocumento.TIMES, 13, PDFDocumento.BOLD | PDFDocumento.UNDERLINE);                        
	      document.setParrafo(consultaSol.toString(),document.getFuente());
	      document.getParrafo().setAlignment(PDFTabla.ALIGN_CENTER);
	      document.agregar(document.getParrafo());  
	      
	      /*Criterios de Busqueda*/
	      document.setFuente(PDFDocumento.TIMES, 13, PDFDocumento.BOLD | PDFDocumento.UNDERLINE);                        
	      document.setParrafo((String) cabecera.get("criterio"),document.getFuente());
	      document.getParrafo().setAlignment(PDFTabla.ALIGN_CENTER);
	      document.agregar(document.getParrafo());  
	      
	      /*Tabla con la Cabecera y el Detalle del Reporte*/
	      document.setTabla(8);                       
	      document.getTabla().setPadding(1);
	      document.getTabla().setSpacing(1);
	      document.getTabla().setBorderWidth(0);            
	      document.getTabla().setDefaultCellBorderColor(PDFDocumento.LIGHT_GRAY);
	      document.getTabla().setAutoFillEmptyCells(true);
	      document.getTabla().setTableFitsPage(false);
	      document.getTabla().setCellsFitPage(true);
	      int headerwidths1[] = { 10, 20, 12, 12, 12, 8, 8, 20 };
	      document.getTabla().setWidths(headerwidths1);
	      document.getTabla().setWidth(95);
	      document.getTabla().setDefaultHorizontalAlignment(PDFTabla.ALIGN_CENTER);
	      document.getTabla().setBackgroundColor(PDFDocumento.WHITE);
	      
	      document.setFuente(PDFDocumento.HELVETICA, 7, PDFDocumento.BOLD, PDFDocumento.WHITE); 
	          
	      //Primera Linea . Registro, Apellidos y Nombres, Categoria
	      document.getTabla().setCelda("N° Solicitud.", document.getFuente());            
	      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.LIGHT_GRAY);
	      document.getTabla().addCell(document.getTabla().getCelda());
	      
	      document.getTabla().setCelda("Asunto", document.getFuente());
	      //document.getTabla().getCelda().setColspan(2);
	      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.LIGHT_GRAY);
	      document.getTabla().getCelda().setBorderColor(PDFDocumento.LIGHT_GRAY);
	      document.getTabla().addCell(document.getTabla().getCelda());
	      
	      document.getTabla().setCelda("Fecha Sol.", document.getFuente());    
	      //document.getTabla().getCelda().setColspan(2);        
	      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.LIGHT_GRAY);
	      document.getTabla().addCell(document.getTabla().getCelda());
	      
	      document.getTabla().setCelda("Estado", document.getFuente());            
	      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.LIGHT_GRAY);
	      document.getTabla().addCell(document.getTabla().getCelda());
	      
	      if(labor.equals("Labor Excepcional")){
	    	  document.getTabla().setCelda("Fec.Permiso", document.getFuente());            
		      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.LIGHT_GRAY);
		      document.getTabla().addCell(document.getTabla().getCelda());
		      
		      document.getTabla().setCelda("Inicio", document.getFuente());            
		      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.LIGHT_GRAY);
		      document.getTabla().addCell(document.getTabla().getCelda());
		      
		      document.getTabla().setCelda("Fin", document.getFuente());            
		      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.LIGHT_GRAY);
		      document.getTabla().addCell(document.getTabla().getCelda());
		      
		      document.getTabla().setCelda("Sustento", document.getFuente());            
		      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.LIGHT_GRAY);
		      document.getTabla().addCell(document.getTabla().getCelda());  
	      }
	      
	      if(labor.equals("Compensacion")){
	    	  document.getTabla().setCelda("Fec.Compensacion", document.getFuente());            
		      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.LIGHT_GRAY);
		      document.getTabla().addCell(document.getTabla().getCelda());
		      
		      document.getTabla().setCelda("Observacion", document.getFuente());            
		      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.LIGHT_GRAY);
		      document.getTabla().addCell(document.getTabla().getCelda()); 
	      }
	      
	      
	      
	      /**/
	      Map quiebre = null;
		  ArrayList detalles = null;
		  
		  for (int i = 0; i < reporte.size(); i++) {
			  quiebre = (HashMap)reporte.get(i);			
			  detalles = (ArrayList)quiebre.get("listaDetalle");
			  
			  if (quiebre!= null && quiebre.size() > 0) {
				  Map detalle = null;
				  if (detalles != null && detalles.size() > 0){
					  for (int j = 0; j < detalles.size(); j++) {
						  detalle = (HashMap)detalles.get(j);
						  if(j==0){
							  document.setFuente(PDFDocumento.HELVETICA, 7, PDFDocumento.BOLD, PDFDocumento.BLACK);        
						      document.getTabla().setCelda((String) quiebre.get("anio"), document.getFuente());        
						      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.WHITE);
						      document.getTabla().getCelda().setHorizontalAlignment(PDFTabla.ALIGN_CENTER);
						      document.getTabla().addCell(document.getTabla().getCelda());     
						      
						      
						      document.setFuente(PDFDocumento.HELVETICA, 7, PDFDocumento.BOLD, PDFDocumento.BLACK);        
						      document.getTabla().setCelda((String) quiebre.get("asunto"), document.getFuente());        
						      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.WHITE);
						      document.getTabla().getCelda().setHorizontalAlignment(PDFTabla.ALIGN_CENTER);
						      document.getTabla().addCell(document.getTabla().getCelda());    
						      
						      document.setFuente(PDFDocumento.HELVETICA, 7, PDFDocumento.BOLD, PDFDocumento.BLACK);        
						      document.getTabla().setCelda((String) quiebre.get(""), document.getFuente());
						      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.WHITE);
						      document.getTabla().getCelda().setHorizontalAlignment(PDFTabla.ALIGN_CENTER);
						      document.getTabla().addCell(document.getTabla().getCelda());
						      
						      document.setFuente(PDFDocumento.HELVETICA, 7, PDFDocumento.BOLD, PDFDocumento.BLACK);        
						      document.getTabla().setCelda((String) quiebre.get("det_estado"), document.getFuente());        
						      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.WHITE);
						      document.getTabla().getCelda().setHorizontalAlignment(PDFTabla.ALIGN_CENTER);
						      document.getTabla().addCell(document.getTabla().getCelda());
						  }else {
							  document.setFuente(PDFDocumento.HELVETICA, 7, PDFDocumento.BOLD, PDFDocumento.BLACK);        
						      document.getTabla().setCelda((String) "", document.getFuente());        
						      document.getTabla().getCelda().setColspan(4);
						      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.WHITE);
						      document.getTabla().getCelda().setHorizontalAlignment(PDFTabla.ALIGN_CENTER);
						      document.getTabla().addCell(document.getTabla().getCelda());
						  }
						  
						  
							  
						  if(labor.equals("Labor Excepcional")){
							  document.setFuente(PDFDocumento.HELVETICA, 7, PDFDocumento.BOLD, PDFDocumento.BLACK);        
						      document.getTabla().setCelda((String) detalle.get("fec_permiso_desc"), document.getFuente());
						      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.WHITE);
						      document.getTabla().getCelda().setHorizontalAlignment(PDFTabla.ALIGN_CENTER);
						      document.getTabla().addCell(document.getTabla().getCelda());
						      
						      document.setFuente(PDFDocumento.HELVETICA, 7, PDFDocumento.BOLD, PDFDocumento.BLACK);        
						      document.getTabla().setCelda((String) detalle.get("hora_inicio"), document.getFuente());
						      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.WHITE);
						      document.getTabla().getCelda().setHorizontalAlignment(PDFTabla.ALIGN_CENTER);
						      document.getTabla().addCell(document.getTabla().getCelda());
						      
						      document.setFuente(PDFDocumento.HELVETICA, 7, PDFDocumento.BOLD, PDFDocumento.BLACK);        
						      document.getTabla().setCelda((String) detalle.get("hora_fin"), document.getFuente());        
						      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.WHITE);
						      document.getTabla().getCelda().setHorizontalAlignment(PDFTabla.ALIGN_CENTER);
						      document.getTabla().addCell(document.getTabla().getCelda());    
						      
						      document.setFuente(PDFDocumento.HELVETICA, 7, PDFDocumento.BOLD, PDFDocumento.BLACK);        
						      document.getTabla().setCelda((String) detalle.get("sustento"), document.getFuente());
						      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.WHITE);
						      document.getTabla().getCelda().setHorizontalAlignment(PDFTabla.ALIGN_CENTER);
						      document.getTabla().addCell(document.getTabla().getCelda()); 
						  }
						  
						  if(labor.equals("Compensacion")){
							  document.setFuente(PDFDocumento.HELVETICA, 7, PDFDocumento.BOLD, PDFDocumento.BLACK);        
						      document.getTabla().setCelda((String) detalle.get("fec_permiso_desc"), document.getFuente());
						      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.WHITE);
						      document.getTabla().getCelda().setHorizontalAlignment(PDFTabla.ALIGN_CENTER);
						      document.getTabla().addCell(document.getTabla().getCelda());
						      
						      document.setFuente(PDFDocumento.HELVETICA, 7, PDFDocumento.BOLD, PDFDocumento.BLACK);        
						      document.getTabla().setCelda((String) detalle.get("sustento"), document.getFuente());
						      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.WHITE);
						      document.getTabla().getCelda().setHorizontalAlignment(PDFTabla.ALIGN_CENTER);
						      document.getTabla().addCell(document.getTabla().getCelda()); 
						  }
						  
					  }
				  }else{
					  document.setFuente(PDFDocumento.HELVETICA, 7, PDFDocumento.BOLD, PDFDocumento.BLACK);        
				      document.getTabla().setCelda((String) quiebre.get("anio"), document.getFuente());        
				      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.WHITE);
				      document.getTabla().getCelda().setHorizontalAlignment(PDFTabla.ALIGN_CENTER);
				      document.getTabla().addCell(document.getTabla().getCelda());     
				      
				      
				      document.setFuente(PDFDocumento.HELVETICA, 7, PDFDocumento.BOLD, PDFDocumento.BLACK);        
				      document.getTabla().setCelda((String) quiebre.get("asunto"), document.getFuente());        
				      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.WHITE);
				      document.getTabla().getCelda().setHorizontalAlignment(PDFTabla.ALIGN_CENTER);
				      document.getTabla().addCell(document.getTabla().getCelda());    
				      
				      document.setFuente(PDFDocumento.HELVETICA, 7, PDFDocumento.BOLD, PDFDocumento.BLACK);        
				      document.getTabla().setCelda((String) quiebre.get("fecha"), document.getFuente());
				      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.WHITE);
				      document.getTabla().getCelda().setHorizontalAlignment(PDFTabla.ALIGN_CENTER);
				      document.getTabla().addCell(document.getTabla().getCelda());
				      
				      document.setFuente(PDFDocumento.HELVETICA, 7, PDFDocumento.BOLD, PDFDocumento.BLACK);        
				      document.getTabla().setCelda((String) quiebre.get("det_estado"), document.getFuente());        
				      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.WHITE);
				      document.getTabla().getCelda().setHorizontalAlignment(PDFTabla.ALIGN_CENTER);
				      document.getTabla().addCell(document.getTabla().getCelda());
				  }
				  
			  } 
		  }
	      document.getTabla().setOffset(15);
	      document.agregar(document.getTabla());    
	    } catch (Exception e) {
	      beanMsg = new MensajeBean();
	      beanMsg.setMensajeerror(e.getMessage());
	      beanMsg.setMensajesol("ERROR - Por favor intente nuevamente.");
	      log.error(e, e);
	      throw new FacadeException(this, beanMsg);
	    } finally {      
	    } 
	    if (log.isDebugEnabled()) {
	      log.debug(this.toString() 
	      .concat( " FIN - getDatosTrabajador()"));
	    }
	  }

	     
	  
	   
	  private void getDatosReportePerm(Map cabecera, List reporte) {
	    if (log.isDebugEnabled()) {
	      log.debug(this.toString()
	      .concat( " INICIO - getDatosTrabajador()"));
	    }
	    try {
	      /*Título del PDF*/
	      
	      StringBuffer reportePerm = new StringBuffer("REPORTE DE PERMANENCIAS DE LABOR EXCEPCIONAL");

	      document.setFuente(PDFDocumento.TIMES, 13, PDFDocumento.BOLD | PDFDocumento.UNDERLINE);                        
	      document.setParrafo(reportePerm.toString(),document.getFuente());
	      document.getParrafo().setAlignment(PDFTabla.ALIGN_CENTER);
	      document.agregar(document.getParrafo());  
	      
	      /*Criterios de Busqueda*/
	      document.setFuente(PDFDocumento.TIMES, 13, PDFDocumento.BOLD | PDFDocumento.UNDERLINE);                        
	      document.setParrafo((String) cabecera.get("criterio"),document.getFuente());
	      document.getParrafo().setAlignment(PDFTabla.ALIGN_CENTER);
	      document.agregar(document.getParrafo());  
	      
	      /*Tabla con la Cabecera y el Detalle del Reporte*/
	      document.setTabla(10);                       
	      document.getTabla().setPadding(1);
	      document.getTabla().setSpacing(1);
	      document.getTabla().setBorderWidth(0);            
	      document.getTabla().setDefaultCellBorderColor(PDFDocumento.LIGHT_GRAY);
	      document.getTabla().setAutoFillEmptyCells(true);
	      document.getTabla().setTableFitsPage(false);
	      document.getTabla().setCellsFitPage(true);
	      int headerwidths1[] = { 10, 10, 40, 6, 6, 12, 10, 8, 8, 7 };
	      document.getTabla().setWidths(headerwidths1);
	      document.getTabla().setWidth(95);
	      document.getTabla().setDefaultHorizontalAlignment(PDFTabla.ALIGN_CENTER);
	      document.getTabla().setBackgroundColor(PDFDocumento.WHITE);
	      
	      document.setFuente(PDFDocumento.HELVETICA, 7, PDFDocumento.BOLD, PDFDocumento.WHITE); 
	          
	      //Primera Linea . Registro, Apellidos y Nombres, Categoria
	      document.getTabla().setCelda("Fecha", document.getFuente());            
	      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.LIGHT_GRAY);
	      document.getTabla().addCell(document.getTabla().getCelda());
	      
	      document.getTabla().setCelda("Registro", document.getFuente());
	      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.LIGHT_GRAY);
	      document.getTabla().getCelda().setBorderColor(PDFDocumento.LIGHT_GRAY);
	      document.getTabla().addCell(document.getTabla().getCelda());
	      
	      document.getTabla().setCelda("Nombre", document.getFuente());          
	      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.LIGHT_GRAY);
	      document.getTabla().addCell(document.getTabla().getCelda());
	      
	      document.getTabla().setCelda("Inicio", document.getFuente());            
	      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.LIGHT_GRAY);
	      document.getTabla().addCell(document.getTabla().getCelda());

    	  document.getTabla().setCelda("Fin", document.getFuente());            
	      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.LIGHT_GRAY);
	      document.getTabla().addCell(document.getTabla().getCelda());
	      
	      document.getTabla().setCelda("N.Solicitud", document.getFuente());            
	      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.LIGHT_GRAY);
	      document.getTabla().addCell(document.getTabla().getCelda());
	      
	      document.getTabla().setCelda("Estado", document.getFuente());
	      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.LIGHT_GRAY);
	      document.getTabla().getCelda().setBorderColor(PDFDocumento.LIGHT_GRAY);
	      document.getTabla().addCell(document.getTabla().getCelda());
	      
	      document.getTabla().setCelda("Inicio", document.getFuente());          
	      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.LIGHT_GRAY);
	      document.getTabla().addCell(document.getTabla().getCelda());
	      
	      document.getTabla().setCelda("Fin", document.getFuente());            
	      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.LIGHT_GRAY);
	      document.getTabla().addCell(document.getTabla().getCelda());

    	  document.getTabla().setCelda("Minutos", document.getFuente());            
	      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.LIGHT_GRAY);
	      document.getTabla().addCell(document.getTabla().getCelda());
  
	      /**/
	      Map quiebre = null;
		  ArrayList detalles = null;
		  
		  for (int i = 0; i < reporte.size(); i++) {
			  quiebre = (HashMap)reporte.get(i);			
			  detalles = (ArrayList)quiebre.get("listaDetalle");
			  
			  if (quiebre!= null && quiebre.size() > 0) {
				  Map detalle = null;
				  if (detalles != null && detalles.size() > 0){
					  for (int j = 0; j < detalles.size(); j++) {
						  detalle = (HashMap)detalles.get(j);
						  if(j==0){
							  document.setFuente(PDFDocumento.HELVETICA, 7, PDFDocumento.BOLD, PDFDocumento.BLACK);        
						      document.getTabla().setCelda((String) quiebre.get("fecha_desc"), document.getFuente());        
						      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.WHITE);
						      document.getTabla().getCelda().setHorizontalAlignment(PDFTabla.ALIGN_CENTER);
						      document.getTabla().addCell(document.getTabla().getCelda());     
						      
						      
						      document.setFuente(PDFDocumento.HELVETICA, 7, PDFDocumento.BOLD, PDFDocumento.BLACK);        
						      document.getTabla().setCelda((String) quiebre.get("registro"), document.getFuente());        
						      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.WHITE);
						      document.getTabla().getCelda().setHorizontalAlignment(PDFTabla.ALIGN_CENTER);
						      document.getTabla().addCell(document.getTabla().getCelda());    
						      
						      document.setFuente(PDFDocumento.HELVETICA, 7, PDFDocumento.BOLD, PDFDocumento.BLACK);        
						      document.getTabla().setCelda((String) quiebre.get("nombre"), document.getFuente());
						      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.WHITE);
						      document.getTabla().getCelda().setHorizontalAlignment(PDFTabla.ALIGN_CENTER);
						      document.getTabla().addCell(document.getTabla().getCelda());
						      
						      document.setFuente(PDFDocumento.HELVETICA, 7, PDFDocumento.BOLD, PDFDocumento.BLACK);        
						      document.getTabla().setCelda((String) quiebre.get("inicio"), document.getFuente());        
						      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.WHITE);
						      document.getTabla().getCelda().setHorizontalAlignment(PDFTabla.ALIGN_CENTER);
						      document.getTabla().addCell(document.getTabla().getCelda());
						      
						      document.setFuente(PDFDocumento.HELVETICA, 7, PDFDocumento.BOLD, PDFDocumento.BLACK);        
						      document.getTabla().setCelda((String) quiebre.get("fin"), document.getFuente());        
						      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.WHITE);
						      document.getTabla().getCelda().setHorizontalAlignment(PDFTabla.ALIGN_CENTER);
						      document.getTabla().addCell(document.getTabla().getCelda());
						  }else {
							  document.setFuente(PDFDocumento.HELVETICA, 7, PDFDocumento.BOLD, PDFDocumento.BLACK);        
						      document.getTabla().setCelda((String) "", document.getFuente());        
						      document.getTabla().getCelda().setColspan(5);
						      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.WHITE);
						      document.getTabla().getCelda().setHorizontalAlignment(PDFTabla.ALIGN_CENTER);
						      document.getTabla().addCell(document.getTabla().getCelda());
						  }
						  
						  

						  document.setFuente(PDFDocumento.HELVETICA, 7, PDFDocumento.BOLD, PDFDocumento.BLACK);        
					      document.getTabla().setCelda((String) detalle.get("solicitud"), document.getFuente());
					      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.WHITE);
					      document.getTabla().getCelda().setHorizontalAlignment(PDFTabla.ALIGN_CENTER);
					      document.getTabla().addCell(document.getTabla().getCelda());
					      
					      document.setFuente(PDFDocumento.HELVETICA, 7, PDFDocumento.BOLD, PDFDocumento.BLACK);        
					      document.getTabla().setCelda((String) detalle.get("estado2"), document.getFuente());
					      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.WHITE);
					      document.getTabla().getCelda().setHorizontalAlignment(PDFTabla.ALIGN_CENTER);
					      document.getTabla().addCell(document.getTabla().getCelda());
					      
					      document.setFuente(PDFDocumento.HELVETICA, 7, PDFDocumento.BOLD, PDFDocumento.BLACK);        
					      document.getTabla().setCelda((String) detalle.get("hinicio"), document.getFuente());        
					      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.WHITE);
					      document.getTabla().getCelda().setHorizontalAlignment(PDFTabla.ALIGN_CENTER);
					      document.getTabla().addCell(document.getTabla().getCelda());    
					      
					      document.setFuente(PDFDocumento.HELVETICA, 7, PDFDocumento.BOLD, PDFDocumento.BLACK);        
					      document.getTabla().setCelda((String) detalle.get("hfin"), document.getFuente());
					      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.WHITE);
					      document.getTabla().getCelda().setHorizontalAlignment(PDFTabla.ALIGN_CENTER);
					      document.getTabla().addCell(document.getTabla().getCelda()); 
					      
					      document.setFuente(PDFDocumento.HELVETICA, 7, PDFDocumento.BOLD, PDFDocumento.BLACK);        
					      document.getTabla().setCelda((String) detalle.get(""), document.getFuente());
					      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.WHITE);
					      document.getTabla().getCelda().setHorizontalAlignment(PDFTabla.ALIGN_CENTER);
					      document.getTabla().addCell(document.getTabla().getCelda()); 
							  

						  
					  }
				  } else{
					  document.setFuente(PDFDocumento.HELVETICA, 7, PDFDocumento.BOLD, PDFDocumento.BLACK);        
				      document.getTabla().setCelda((String) quiebre.get("fecha_desc"), document.getFuente());        
				      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.WHITE);
				      document.getTabla().getCelda().setHorizontalAlignment(PDFTabla.ALIGN_CENTER);
				      document.getTabla().addCell(document.getTabla().getCelda());     
				      
				      
				      document.setFuente(PDFDocumento.HELVETICA, 7, PDFDocumento.BOLD, PDFDocumento.BLACK);        
				      document.getTabla().setCelda((String) quiebre.get("registro"), document.getFuente());        
				      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.WHITE);
				      document.getTabla().getCelda().setHorizontalAlignment(PDFTabla.ALIGN_CENTER);
				      document.getTabla().addCell(document.getTabla().getCelda());    
				      
				      document.setFuente(PDFDocumento.HELVETICA, 7, PDFDocumento.BOLD, PDFDocumento.BLACK);        
				      document.getTabla().setCelda((String) quiebre.get("nombre"), document.getFuente());
				      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.WHITE);
				      document.getTabla().getCelda().setHorizontalAlignment(PDFTabla.ALIGN_CENTER);
				      document.getTabla().addCell(document.getTabla().getCelda());
				      
				      document.setFuente(PDFDocumento.HELVETICA, 7, PDFDocumento.BOLD, PDFDocumento.BLACK);        
				      document.getTabla().setCelda((String) quiebre.get("inicio"), document.getFuente());        
				      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.WHITE);
				      document.getTabla().getCelda().setHorizontalAlignment(PDFTabla.ALIGN_CENTER);
				      document.getTabla().addCell(document.getTabla().getCelda());
				      
				      document.setFuente(PDFDocumento.HELVETICA, 7, PDFDocumento.BOLD, PDFDocumento.BLACK);        
				      document.getTabla().setCelda((String) quiebre.get("fin"), document.getFuente());        
				      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.WHITE);
				      document.getTabla().getCelda().setHorizontalAlignment(PDFTabla.ALIGN_CENTER);
				      document.getTabla().addCell(document.getTabla().getCelda());
				  }
				  
				  
			  } 
		  }
	      document.getTabla().setOffset(15);
	      document.agregar(document.getTabla());    
	    } catch (Exception e) {
	      beanMsg = new MensajeBean();
	      beanMsg.setMensajeerror(e.getMessage());
	      beanMsg.setMensajesol("ERROR - Por favor intente nuevamente.");
	      log.error(e, e);
	      throw new FacadeException(this, beanMsg);
	    } finally {      
	    } 
	    if (log.isDebugEnabled()) {
	      log.debug(this.toString() 
	      .concat( " FIN - getDatosTrabajador()"));
	    }
	  }
   
	  private void getNeto(Map totalesLiquidacion) {
	    if (log.isDebugEnabled()) {
	      log.debug(this.toString()
	      .concat( " INICIO - getNeto()"));
	    }
	    try {    
	      document.setTabla(5);                       
	      document.getTabla().setPadding(1);
	      document.getTabla().setSpacing(1);
	      document.getTabla().setBorderWidth(0);            
	      document.getTabla().setDefaultCellBorderColor(PDFDocumento.LIGHT_GRAY);
	      document.getTabla().setAutoFillEmptyCells(true);
	      document.getTabla().setTableFitsPage(false);
	      document.getTabla().setCellsFitPage(true);
	      
	      int headerwidths1[] = { 15, 49, 12, 12, 12 };
	      document.getTabla().setWidths(headerwidths1);
	      document.getTabla().setWidth(95);
	      document.getTabla().setDefaultHorizontalAlignment(PDFTabla.ALIGN_CENTER);
	      document.getTabla().setBackgroundColor(PDFDocumento.WHITE);                   
	         
	      document.setFuente(PDFDocumento.HELVETICA, 7, PDFDocumento.BOLD, PDFDocumento.WHITE);
	      //Primera Linea . Registro, Apellidos y Nombres, Categoria    
	      document.getTabla().setCelda("NETO A PAGAR", document.getFuente());
	      document.getTabla().getCelda().setBorderColor(PDFDocumento.LIGHT_GRAY);
	      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.LIGHT_GRAY);
	      document.getTabla().getCelda().setColspan(3);
	      document.getTabla().addCell(document.getTabla().getCelda());           
	           
	      String ingresos = "";
	      String descuentos = "";                      
	      BigDecimal ingreso = null;
	      BigDecimal descuento = null;
	      BigDecimal neto = null;          
	      neto = new BigDecimal("0");                    
	      String pagoneto = "0";
	      if (null != totalesLiquidacion){                      
	        ingresos = null!=totalesLiquidacion.get("tot_ing")?totalesLiquidacion.get("tot_ing").toString():"0"; 
	        descuentos = null!=totalesLiquidacion.get("tot_desc")?totalesLiquidacion.get("tot_desc").toString():"0";
	        ingreso = new BigDecimal(ingresos);
	        descuento = new BigDecimal(descuentos);  
	        neto = ingreso.subtract(descuento);
	        pagoneto = Numero.format(neto.setScale(2,BigDecimal.ROUND_HALF_UP),"#,##0.00");
	      }
	               
	      document.setFuente(PDFDocumento.HELVETICA, 7, PDFDocumento.BOLD, PDFDocumento.BLACK);        
	      document.getTabla().setCelda(pagoneto, document.getFuente());
	      document.getTabla().getCelda().setColspan(2);
	      document.getTabla().getCelda().setBackgroundColor(PDFDocumento.WHITE);
	      document.getTabla().getCelda().setHorizontalAlignment(PDFTabla.ALIGN_CENTER);
	      document.getTabla().addCell(document.getTabla().getCelda());                
	                   
	      document.getTabla().setOffset(0);
	      document.agregar(document.getTabla());        
	    } catch (Exception e) {
	      beanMsg = new MensajeBean();
	      beanMsg.setMensajeerror(e.getMessage());
	      beanMsg.setMensajesol("ERROR - Por favor intente nuevamente.");
	      log.error(e, e);
	      throw new FacadeException(this, beanMsg);
	    } finally {      
	    } 
	    if (log.isDebugEnabled()) {
	      log.debug(this.toString() 
	      .concat( " FIN - getNeto()"));
	    }
	  }
	  
	
	/**INICIO - NVILLAR 03/05/2012 - LABOR EXCEPCIONAL
	 * Metodo encargado de encolar el reporte de Labor Excepcional
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param dbpool
	 * @param params
	 * @param usuario
	 * @return
	 * @throws RemoteException
	 */
	public void masivoLaborExcepcional2(String dbpool, HashMap params, String usuario)
			throws RemoteException {
		BeanMensaje beanM = new BeanMensaje();
		try {
			QueueDAO qd = new QueueDAO();
			params.put("dbpool", dbpool);
			log.debug("masivoLaborExcepcional2 - params:" + params);//JVV
			log.debug("masivoLaborExcepcional2 - usuario:" + usuario);//JVV
			params.put("observacion", "Reporte de Solicitudes de Labor Excepcional para Analista de Asistencia del "
					+ (String) params.get("fechaIni") + " al " + (String) params.get("fechaFin"));
			qd.encolaReporte(ReporteMasivoFacadeHome.JNDI_NAME,
					"laborExcepcional2", params, usuario);
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
	}// FIN- NVILLAR 23/04/2012 - LABOR EXCEPCIONAL
	
	

	/** NVILLAR - 22/03/2012 - LABOR EXCEPCIONAL
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */	
	public ArrayList permanencias(Map params) throws FacadeException,
			RemoteException {
		String regimen = (String)params.get("regimen");				
		String criterio = (String)params.get("criterio");
		String valor = (String)params.get("valor");		
		ArrayList reporte = null;
		BeanMensaje beanM = new BeanMensaje();		
		try {
			DataSource dbpool_sp = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dcsp");
			pe.gob.sunat.rrhh.asistencia.dao.T4819DAO reportDAO = new pe.gob.sunat.rrhh.asistencia.dao.T4819DAO(dbpool_sp);
			if(log.isDebugEnabled()) log.debug(">>DAO>>regimen:"+regimen);
			if(log.isDebugEnabled()) log.debug(">>DAO>>criterio:"+criterio);
			if(log.isDebugEnabled()) log.debug(">>DAO>>valor:"+valor);
			List lista = reportDAO.findPermanencias(params);			
			if(log.isDebugEnabled()) log.debug("las_solicitudes:"+lista);
			if (lista != null && lista.size() > 0) {
				reporte = new ArrayList();
				Map rs = null;								
				for (int i = 0; i < lista.size(); i++) {
					rs = (HashMap) lista.get(i);//fila
					reporte.add(rs);
					if(log.isDebugEnabled()) log.debug("OBJETO reporte - FACADE:"+reporte);
				}	
			}	
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		return reporte;
	}//FIN - NVILLAR - 22/03/2012 - LABOR EXCEPCIONAL
	
	/** NVILLAR - 03/05/2012 - LABOR EXCEPCIONAL
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */	
	public ArrayList permanencias2(Map params) throws FacadeException,
			RemoteException {
		String regimen = (String)params.get("regimen");				
		String criterio = (String)params.get("criterio");
		ArrayList reporte = new ArrayList();
		BeanMensaje beanM = new BeanMensaje();		
		try {
			DataSource dbpool_sp = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dcsp");
			pe.gob.sunat.rrhh.asistencia.dao.T4819DAO reportDAO = new pe.gob.sunat.rrhh.asistencia.dao.T4819DAO(dbpool_sp);
			if(log.isDebugEnabled()) log.debug(">>DAO>>regimen:"+regimen);
			if(log.isDebugEnabled()) log.debug(">>DAO>>criterio:"+criterio);
			
			String solicitud1 = (params.get("solicitud1") != null ) ? params.get("solicitud1").toString():"";
			String solicitud2 = (params.get("solicitud2") != null ) ? params.get("solicitud2").toString():"";
			String solicitud3 = (params.get("solicitud3") != null ) ? params.get("solicitud3").toString():"";
			
			Map mapa = reportDAO.findPermanencias2(params);
			List lista = (List)mapa.get("lista"); 
			List lista2 = (List)mapa.get("lista2");		
			if(log.isDebugEnabled()) log.debug("las_solicitudes:"+lista);
			if(log.isDebugEnabled()) log.debug("LISTA2:"+lista2);
			if (lista != null && lista.size() > 0) {
				//reporte = new ArrayList();
				Map rs = null;	
				Map mapa1 = new HashMap();
				Map mapa2 = new HashMap();
				Map mapa3 = new HashMap();
				Map mapa4 = new HashMap();
				Map suma = new HashMap();	
				int suma_minutos = 0;
				String indicador=null;
				String fecha_desc=null;
				String des_unidad=null;
				String registro=null;
				String nombre=null;
				String inicio=null;
				String fin=null;
				
				int min_acu=0;
				int min_sal=0;
				int dif_min=0;

				for (int i = 0; i < lista.size(); i++) {
					rs = (HashMap) lista.get(i);//fila
					
					indicador = (String) rs.get("indicador");
					fecha_desc = (String) rs.get("fecha_desc");
					des_unidad = (String) rs.get("des_unidad");
					registro = (String) rs.get("registro");
					nombre = (String) rs.get("nombre");
					inicio = (String) rs.get("inicio");
					fin = (String) rs.get("fin");
					min_acu = Integer.parseInt( rs.get("minutos_acu").toString());
					min_sal = Integer.parseInt( rs.get("minutos_sal").toString());
					dif_min=min_acu-min_sal;
					
					
					if (solicitud3.equals("1")){ // Se busca con Estado Compensada.
						if (indicador.equals("1")){
							if (dif_min > 0){
								mapa1=null;
								mapa1 = new HashMap();
								mapa1.put("fecha_desc",fecha_desc);
								mapa1.put("des_unidad",des_unidad);
								mapa1.put("registro",registro);
								mapa1.put("nombre",nombre);
								mapa1.put("inicio",inicio);
								mapa1.put("fin",fin);
								mapa1.put("minutos_sal",""+dif_min);
								mapa1.put("estado","Compensada");
								suma_minutos = suma_minutos + dif_min;
								if(log.isDebugEnabled()) log.debug("suma_minutos: "+suma_minutos);
								if(log.isDebugEnabled()) log.debug("COMPENSADO: "+mapa1);
								reporte.add(mapa1);
								if(log.isDebugEnabled()) log.debug("OBJETO reporte - FACADE:"+reporte);
							}
						}
						
						if (indicador.equals("3")){
							mapa2=null;
							mapa2 = new HashMap();
							mapa2.put("fecha_desc",fecha_desc);
							mapa2.put("des_unidad",des_unidad);
							mapa2.put("registro",registro);
							mapa2.put("nombre",nombre);
							mapa2.put("inicio",inicio);
							mapa2.put("fin",fin);
							mapa2.put("minutos_sal",""+min_acu);
							mapa2.put("estado","Compensada");
							//suma_minutos = suma_minutos + min_sal;DEC
							suma_minutos = suma_minutos + min_acu;
							if(log.isDebugEnabled()) log.debug("SUMA123: "+suma_minutos);
							if(log.isDebugEnabled()) log.debug("COMPENSADO puro: "+mapa2);
							reporte.add(mapa2);
							if(log.isDebugEnabled()) log.debug("OBJETO reporte - FACADE:"+reporte);
						}
						
					} 
					
					if (solicitud2.equals("1")){ // Se busca con Estado No Autorizada.
						if (indicador.equals("4")){
							mapa3=null;
							mapa3 = new HashMap();
							mapa3= rs;
							suma_minutos = suma_minutos + min_sal;
							if(log.isDebugEnabled()) log.debug("SUMA123: "+suma_minutos);
							if(log.isDebugEnabled()) log.debug("NO AUTORIZADO: "+mapa3);
							reporte.add(mapa3);
							if(log.isDebugEnabled()) log.debug("OBJETO reporte - FACADE:"+reporte);
						}
					}
					
					if (solicitud1.equals("1")){ // Se busca con Estado Autorizada.
						if (indicador.equals("1")){
							mapa4=null;
							mapa4 = new HashMap();
							mapa4.put("fecha_desc",fecha_desc);
							mapa4.put("des_unidad",des_unidad);
							mapa4.put("registro",registro);
							mapa4.put("nombre",nombre);
							mapa4.put("inicio",inicio);
							mapa4.put("fin",fin);
							mapa4.put("minutos_sal",""+min_sal);
							mapa4.put("estado","Autorizada");
							suma_minutos = suma_minutos + min_sal;
							if(log.isDebugEnabled()) log.debug("SUMA123: "+suma_minutos);
							if(log.isDebugEnabled()) log.debug("AUTORIZADO: "+mapa4);
							reporte.add(mapa4);
							if(log.isDebugEnabled()) log.debug("OBJETO reporte - FACADE:"+reporte);
						}
					}
				}
				suma.put("suma_minutos",""+suma_minutos);
				if(log.isDebugEnabled()) log.debug("SUMATOTAL:"+suma_minutos);
				reporte.add(suma);
			}
			if (lista2 != null && lista2.size() > 0 && lista.size() > 0) {
				Map cantidad = null;								
					cantidad =  (HashMap)lista2.get(0);//fila
					reporte.add(cantidad);
					if(log.isDebugEnabled()) log.debug("CANTIDAD:"+cantidad);
			}
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		return reporte;
	}//FIN - NVILLAR - 03/05/2012 - LABOR EXCEPCIONAL
	
	/**NVILLAR - 17/04/2012 - LABOR EXCEPCIONAL
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */	
	public ArrayList buscaDetallePermanenciasLE(Map params) throws FacadeException,
			RemoteException {

		if (log.isDebugEnabled()) log.debug("Ingresara a buscaDetalleSolicitudesLE-Facade");	      
		ArrayList reporte = null;
		BeanMensaje beanM = new BeanMensaje();		
		try {			
			DataSource dbpool_sp = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dcsp");
			pe.gob.sunat.rrhh.asistencia.dao.T4819DAO reportDAO = new pe.gob.sunat.rrhh.asistencia.dao.T4819DAO(dbpool_sp);
			List lista = reportDAO.buscaDetallePermanenciaLE(params);				
			if(log.isDebugEnabled()) log.debug("las_solicitudes:"+lista);
			//ICAPUNAY 19/06 MODIFICANDO A SALDO DE SOLICITUD NO DE COMPENSA
			float difMin = 0;
			int difMin2 = 0;
			String hor_ini_permiso="";
			String hor_fin_permiso="";
			//FIN ICAPUNAY 19/06 MODIFICANDO A SALDO DE SOLICITUD NO DE COMPENSA
			if (lista != null && lista.size() > 0) {
				reporte = new ArrayList();
				Map rs = null;								
				String det_estado = new String();
				for (int i = 0; i < lista.size(); i++) {
					rs = (HashMap) lista.get(i);//fila				
					if(rs.get("estado").toString().trim().equals(constantes.leePropiedad("ACCION_INICIAR"))){
						det_estado = "Iniciada";
					}
					if(rs.get("estado").toString().trim().equals(constantes.leePropiedad("ACCION_APROBAR"))){
						det_estado = "Aprobada";
					}
					if(rs.get("estado").toString().trim().equals(constantes.leePropiedad("ACCION_RECHAZAR"))){
						det_estado = "Rechazada";
					}
					rs.put("det_estado", det_estado);	
					//ICAPUNAY 19/06 MODIFICANDO A SALDO DE SOLICITUD NO DE COMPENSA
					hor_ini_permiso= rs.get("hinicio").toString().trim()+":00";//hor_ini_aut=08:00
					hor_fin_permiso= rs.get("hfin").toString().trim()+":00";//hor_fin_aut=15:00
					difMin = Utiles.obtenerMinutosDiferencia(hor_ini_permiso,hor_fin_permiso);
					difMin2 = Math.round(difMin);
					rs.put("minutos", String.valueOf(difMin2));
					//FIN ICAPUNAY 19/06 MODIFICANDO A SALDO DE SOLICITUD NO DE COMPENSA
					reporte.add(rs);
					if(log.isDebugEnabled()) log.debug("OBJETO reporte - FACADE:"+reporte);
				}	
			}			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return reporte;
	}//FIN - NVILLAR - 17/04/2012 - LABOR EXCEPCIONAL
	
	/** //INICIO - NVILLAR - 20/04/2012 - LABOR EXCEPCIONAL
	 * Metodo encargado de obtener el reporte de gestion mensual por unidad organica
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param params
	 * @return reporte List
	 * @throws RemoteException
	 */	
	public ArrayList resumenPermanenciaExcepcional(Map params) throws RemoteException {
		ArrayList reporte = null;//lista reporte a devolver
		BeanMensaje beanM = new BeanMensaje();
		if (log.isDebugEnabled()) log.debug("ENTRO AL METODO resumenPermanenciaExcepcional");
		if (log.isDebugEnabled()) log.debug("params: "+params);
		try {
			DataSource dbpool_sp = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dcsp");
			pe.gob.sunat.rrhh.asistencia.dao.T4819DAO reportDAO = new pe.gob.sunat.rrhh.asistencia.dao.T4819DAO(dbpool_sp);
			List lista = reportDAO.findPermanencias(params);
			if(log.isDebugEnabled()) log.debug("las_permanencias:"+lista);
			//ICAPUNAY 19/06 MODIFICANDO A SALDO DE SOLICITUD NO DE COMPENSA
			float difMin = 0;
			int difMin2 = 0;
			String hor_ini_permiso="";
			String hor_fin_permiso="";
			//FIN ICAPUNAY 19/06 MODIFICANDO A SALDO DE SOLICITUD NO DE COMPENSA
			Map seguridad = (HashMap)params.get("seguridad");
			if (lista != null && lista.size() > 0) {
				reporte = new ArrayList();
				Map rs = null;
				Map r = null;
				String det_estado = new String();
				for (int i = 0; i < lista.size(); i++) {
					rs = (HashMap) lista.get(i);//fila	
					String registro = rs.get("registro").toString();
					String fecha = rs.get("fecha").toString();
					String autorizada = rs.get("autorizada").toString();
					if (log.isDebugEnabled()) log.debug("registro: "+registro);
		            if (log.isDebugEnabled()) log.debug("fecha: "+fecha);
		            if (log.isDebugEnabled()) log.debug("autorizada: "+autorizada);
		            Map detalles = new HashMap();       	
		            detalles.put("seguridad", seguridad);
		            detalles.put("registro", registro);
		            detalles.put("fecha", fecha);
		            detalles.put("autorizada", autorizada);
		            List listaDetalle = reportDAO.buscaDetallePermanenciaLE(detalles);
		            List listaDetalle2 = new ArrayList();
		            if (listaDetalle != null && listaDetalle.size() > 0) {
		            	for (int j = 0; j < listaDetalle.size(); j++) {
		            		r = (HashMap) listaDetalle.get(j);//fila
		            		if(r.get("estado").toString().trim().equals(constantes.leePropiedad("ACCION_INICIAR"))){
								det_estado = "Iniciada";
							}
							if(r.get("estado").toString().trim().equals(constantes.leePropiedad("ACCION_APROBAR"))){
								det_estado = "Aprobada";
							}
							if(r.get("estado").toString().trim().equals(constantes.leePropiedad("ACCION_RECHAZAR"))){
								det_estado = "Rechazada";
							}
							r.put("estado2", det_estado);
							//ICAPUNAY 19/06 MODIFICANDO A SALDO DE SOLICITUD NO DE COMPENSA
							hor_ini_permiso= r.get("hinicio").toString().trim()+":00";//hor_ini_aut=08:00
							hor_fin_permiso= r.get("hfin").toString().trim()+":00";//hor_fin_aut=15:00
							difMin = Utiles.obtenerMinutosDiferencia(hor_ini_permiso,hor_fin_permiso);
							difMin2 = Math.round(difMin);
							r.put("minutos", String.valueOf(difMin2));
							//FIN ICAPUNAY 19/06 MODIFICANDO A SALDO DE SOLICITUD NO DE COMPENSA
							listaDetalle2.add(r);
		            	}
		            }
		            rs.put("listaDetalle",listaDetalle2);
		            reporte.add(rs);
		            if(log.isDebugEnabled()) log.debug("OBJETO reporte - FACADE:"+reporte);
				  }
			  }	
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}		
		return reporte;		
	}//FIN - NVILLAR - 20/04/2012 - LABOR EXCEPCIONAL
	
	/** //INICIO - NVILLAR - 04/05/2012 - LABOR EXCEPCIONAL
	 * Metodo encargado de obtener el reporte de gestion mensual por unidad organica
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param params
	 * @return reporte List
	 * @throws RemoteException
	 */	
	public ArrayList resumenPermanenciaExcepcional2(Map params) throws RemoteException {
		ArrayList reporte = null;//lista reporte a devolver
		BeanMensaje beanM = new BeanMensaje();
		if (log.isDebugEnabled()) log.debug("ENTRO AL METODO resumenPermanenciaExcepcional");
		if (log.isDebugEnabled()) log.debug("params: "+params);
		try {
			DataSource dbpool_sp = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dcsp");
			pe.gob.sunat.rrhh.asistencia.dao.T4819DAO reportDAO = new pe.gob.sunat.rrhh.asistencia.dao.T4819DAO(dbpool_sp);
			
			String solicitud1 = (params.get("solicitud1") != null ) ? params.get("solicitud1").toString():"";
			String solicitud2 = (params.get("solicitud2") != null ) ? params.get("solicitud2").toString():"";
			String solicitud3 = (params.get("solicitud3") != null ) ? params.get("solicitud3").toString():"";
			
			Map mapa = reportDAO.findPermanencias2(params);
			//List lista = (List)mapa.get("lista");//ICR 17/12/2012 que no caiga por nullpointer 
			//List lista2 = (List)mapa.get("lista2");//ICR 17/12/2012 que no caiga por nullpointer 
			List lista = mapa.get("lista")!=null?(List)mapa.get("lista"):null;//ICR 17/12/2012 que no caiga por nullpointer 			
			List lista2 = mapa.get("lista2")!=null?(List)mapa.get("lista2"):null;//ICR 17/12/2012 que no caiga por nullpointer  
			if(log.isDebugEnabled()) log.debug("las_permanencias:"+lista);
			if (lista != null && lista.size() > 0) {
				reporte = new ArrayList();
				Map rs = null;
				Map mapa1 = new HashMap();
				Map mapa2 = new HashMap();
				Map mapa3 = new HashMap();
				Map mapa4 = new HashMap();
				Map suma = new HashMap();
				int suma_minutos = 0;
				String indicador=null;
				String fecha_desc=null;
				String des_unidad=null;
				String registro=null;
				String nombre=null;
				String inicio=null;
				String fin=null;
				
				int min_acu=0;
				int min_sal=0;
				int dif_min=0;
				
				for (int i = 0; i < lista.size(); i++) {
					rs = (HashMap) lista.get(i);//fila
					
					indicador = (String) rs.get("indicador");
					fecha_desc = (String) rs.get("fecha_desc");
					des_unidad = (String) rs.get("des_unidad");
					registro = (String) rs.get("registro");
					nombre = (String) rs.get("nombre");
					inicio = (String) rs.get("inicio");
					fin = (String) rs.get("fin");
					min_acu = Integer.parseInt( rs.get("minutos_acu").toString());
					min_sal = Integer.parseInt( rs.get("minutos_sal").toString());
					dif_min=min_acu-min_sal;
					
					
					if (solicitud3.equals("1")){ // Se busca con Estado Compensada.
						if (indicador.equals("1")){
							if (dif_min > 0){
								mapa1=null;
								mapa1 = new HashMap();
								mapa1.put("fecha_desc",fecha_desc);
								mapa1.put("des_unidad",des_unidad);
								mapa1.put("registro",registro);
								mapa1.put("nombre",nombre);
								mapa1.put("inicio",inicio);
								mapa1.put("fin",fin);
								mapa1.put("minutos_sal",""+dif_min);
								mapa1.put("estado","Compensada");
								suma_minutos = suma_minutos + dif_min;
								if(log.isDebugEnabled()) log.debug("suma_minutos: "+suma_minutos);
								if(log.isDebugEnabled()) log.debug("COMPENSADO: "+mapa1);
								reporte.add(mapa1);
								if(log.isDebugEnabled()) log.debug("OBJETO reporte - FACADE:"+reporte);
							}
						}
						
						if (indicador.equals("3")){
							mapa2=null;
							mapa2 = new HashMap();
							mapa2.put("fecha_desc",fecha_desc);
							mapa2.put("des_unidad",des_unidad);
							mapa2.put("registro",registro);
							mapa2.put("nombre",nombre);
							mapa2.put("inicio",inicio);
							mapa2.put("fin",fin);
							mapa2.put("minutos_sal",""+min_acu);
							mapa2.put("estado","Compensada");
							//suma_minutos = suma_minutos + min_sal
							suma_minutos = suma_minutos + min_acu;							
							if(log.isDebugEnabled()) log.debug("SUMA123: "+suma_minutos);
							if(log.isDebugEnabled()) log.debug("COMPENSADO puro: "+mapa2);
							reporte.add(mapa2);
							if(log.isDebugEnabled()) log.debug("OBJETO reporte - FACADE:"+reporte);
						}
						
					} 
					
					if (solicitud2.equals("1")){ // Se busca con Estado No Autorizada.
						if (indicador.equals("4")){
							mapa3=null;
							mapa3 = new HashMap();
							mapa3= rs;
							suma_minutos = suma_minutos + min_sal;
							if(log.isDebugEnabled()) log.debug("SUMA123: "+suma_minutos);
							if(log.isDebugEnabled()) log.debug("NO AUTORIZADO: "+mapa3);
							reporte.add(mapa3);
							if(log.isDebugEnabled()) log.debug("OBJETO reporte - FACADE:"+reporte);
						}
					}
					
					if (solicitud1.equals("1")){ // Se busca con Estado Autorizada.
						if (indicador.equals("1")){
							mapa4=null;
							mapa4 = new HashMap();
							mapa4.put("fecha_desc",fecha_desc);
							mapa4.put("des_unidad",des_unidad);
							mapa4.put("registro",registro);
							mapa4.put("nombre",nombre);
							mapa4.put("inicio",inicio);
							mapa4.put("fin",fin);
							mapa4.put("minutos_sal",""+min_sal);
							mapa4.put("estado","Autorizada");
							suma_minutos = suma_minutos + min_sal;
							if(log.isDebugEnabled()) log.debug("SUMA123: "+suma_minutos);
							if(log.isDebugEnabled()) log.debug("AUTORIZADO: "+mapa4);
							reporte.add(mapa4);
							if(log.isDebugEnabled()) log.debug("OBJETO reporte - FACADE:"+reporte);
						}
					}
				}
				suma.put("suma_minutos",""+suma_minutos);
				reporte.add(suma);
			  }
			if (lista2 != null && lista2.size() > 0) {
				if(log.isDebugEnabled()) log.debug("lista2: "+lista2);//ICR 17/12/2012 logs para visualizar si hay nullpointer
				Map cantidad = null;
					if(log.isDebugEnabled()) log.debug("lista2.get(0): "+lista2.get(0));//ICR 17/12/2012 logs para visualizar si hay nullpointer
					cantidad =  (HashMap)lista2.get(0);//fila
					if(log.isDebugEnabled()) log.debug("cantidad: "+cantidad);//ICR 17/12/2012 logs para visualizar si hay nullpointer
					reporte.add(cantidad);
					if(log.isDebugEnabled()) log.debug("reporte: "+reporte);//ICR 17/12/2012 logs para visualizar si hay nullpointer
					if(log.isDebugEnabled()) log.debug("CANTIDAD:"+cantidad);
			}
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}		
		return reporte;	
	}//FIN - NVILLAR - 04/05/2012 - LABOR EXCEPCIONAL

	/**INICIO - NVILLAR 25/04/2012 - LABOR EXCEPCIONAL
	 * Metodo encargado de encolar el reporte de Labor Excepcional
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param dbpool
	 * @param params
	 * @param usuario
	 * @return
	 * @throws RemoteException
	 */
	public void masivoConsultaPermanencia(String dbpool, HashMap params, String usuario)
	throws IncompleteConversationalState, RemoteException {
	BeanMensaje beanM = new BeanMensaje();
	try {
		QueueDAO qd = new QueueDAO();
		params.put("dbpool", dbpool);
		log.debug("masivoConsultaPermanencia - params:" + params);
		log.debug("masivoConsultaPermanencia - usuario:" + usuario);
		params.put("observacion", "Reporte de Permanencias de Labor Excepcional del " + (String) params.get("fechaIni") + " al " + (String) params.get("fechaFin"));
		qd.encolaReporte(ReporteMasivoFacadeHome.JNDI_NAME,
				"consultaPermanencias", params, usuario);
	} catch (Exception e) {
		log.error(e,e);
		beanM.setMensajeerror(e.getMessage());
		beanM.setMensajesol("Por favor intente nuevamente.");
		throw new IncompleteConversationalState(beanM);
	}
}// FIN- NVILLAR 25/04/2012 - LABOR EXCEPCIONAL
	
	/**INICIO - NVILLAR 04/05/2012 - LABOR EXCEPCIONAL
	 * Metodo encargado de encolar el reporte de Labor Excepcional
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param dbpool
	 * @param params
	 * @param usuario
	 * @return
	 * @throws RemoteException
	 */
	public void masivoConsultaPermanencia2(String dbpool, HashMap params, String usuario)
			throws IncompleteConversationalState, RemoteException {
		BeanMensaje beanM = new BeanMensaje();
		try {
			QueueDAO qd = new QueueDAO();
			params.put("dbpool", dbpool);
			log.debug("masivoConsultaPermanencia2 - params:" + params);
			log.debug("masivoConsultaPermanencia2 - usuario:" + usuario);
			params.put("observacion", "Reporte de Permanencias de Labor Excepcional para Analista de Asistencia del " + (String) params.get("fechaIni") + " al " + (String) params.get("fechaFin"));
			qd.encolaReporte(ReporteMasivoFacadeHome.JNDI_NAME,
					"consultaPermanencias2", params, usuario);
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
	}// FIN- NVILLAR 04/05/2012 - LABOR EXCEPCIONAL
	
    /** NVILLAR - 16/04/2012 - LABOR EXCEPCIONAL
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */	
	public ArrayList consultaHorasAutNoAutComp(Map params) throws FacadeException,
			RemoteException {
		String medida = (String)params.get("medida");
		String criterio = (String)params.get("criterio");
		ArrayList reporte = null;
		BeanMensaje beanM = new BeanMensaje();		
		try {
			DataSource dbpool_sp = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dcsp");	
			pe.gob.sunat.rrhh.asistencia.dao.T4819DAO reportDAO = new pe.gob.sunat.rrhh.asistencia.dao.T4819DAO(dbpool_sp);
			if(log.isDebugEnabled()) log.debug(">>DAO>>regimen:"+medida);
			if(log.isDebugEnabled()) log.debug(">>DAO>>criterio:"+criterio);
			List lista = reportDAO.findHorasAutNoAutComp(params);			
			if(log.isDebugEnabled()) log.debug("las_solicitudes:"+lista);
			if (lista != null && lista.size() > 0) {
				reporte = new ArrayList();
				Map rs = null;								
				double suma1=0;
				double suma2=0;
				double suma3=0;
				double suma4=0;
				for (int i = 0; i < lista.size(); i++) {
					rs = (HashMap) lista.get(i);//fila
					DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
            		simbolos.setDecimalSeparator('.');
					
            		// minutos
            		if(medida.equals("01")){
            			rs.put("xautorizada", rs.get("autorizada").toString());
            			rs.put("xnoautorizada", rs.get("noautorizada").toString());
            			rs.put("xrechazada", rs.get("rechazada").toString());
            			rs.put("xcompensada", rs.get("compensada").toString());
            		}
            		
            		// horas
					if(medida.equals("02")){
						DecimalFormat formateador = new DecimalFormat("########.##",simbolos);
						
						//autorizada
						double xautorizada = Double.parseDouble(rs.get("autorizada").toString());
						xautorizada = xautorizada/60;
						xautorizada = new Double(formateador.format(xautorizada)).doubleValue();
						Double dObj1 = new Double(xautorizada);
						rs.put("xautorizada", dObj1);
						
						//no autorizada
						double xnoautorizada = Double.parseDouble(rs.get("noautorizada").toString());
						xnoautorizada = xnoautorizada/60;
						xnoautorizada = new Double(formateador.format(xnoautorizada)).doubleValue();
						Double dObj2 = new Double(xnoautorizada);
						rs.put("xnoautorizada", dObj2);
						
						//rechazada
						double xrechazada = Double.parseDouble(rs.get("rechazada").toString());
						xrechazada = xrechazada/60;
						xrechazada = new Double(formateador.format(xrechazada)).doubleValue();
						Double dObj3 = new Double(xrechazada);
						rs.put("xrechazada", dObj3);
						
						//compensada
						double xcompensada = Double.parseDouble(rs.get("compensada").toString());
						xcompensada = xcompensada/60;
						xcompensada = new Double(formateador.format(xcompensada)).doubleValue();
						Double dObj4 = new Double(xcompensada);
						rs.put("xcompensada", dObj4);
						
					}
					
					//dias
					if(medida.equals("03")){
                        DecimalFormat formateador = new DecimalFormat("########.##",simbolos);
						
						//autorizada
						double xautorizada = Double.parseDouble(rs.get("autorizada").toString());
						xautorizada = xautorizada/480;
						xautorizada = new Double(formateador.format(xautorizada)).doubleValue();
						Double dObj1 = new Double(xautorizada);
						rs.put("xautorizada", dObj1);
						
						//no autorizada
						double xnoautorizada = Double.parseDouble(rs.get("noautorizada").toString());
						xnoautorizada = xnoautorizada/480;
						xnoautorizada = new Double(formateador.format(xnoautorizada)).doubleValue();
						Double dObj2 = new Double(xnoautorizada);
						rs.put("xnoautorizada", dObj2);
						
						//rechazada
						double xrechazada = Double.parseDouble(rs.get("rechazada").toString());
						xrechazada = xrechazada/480;
						xrechazada = new Double(formateador.format(xrechazada)).doubleValue();
						Double dObj3 = new Double(xrechazada);
						rs.put("xrechazada", dObj3);
						
						//compensada
						double xcompensada = Double.parseDouble(rs.get("compensada").toString());
						xcompensada = xcompensada/480;
						xcompensada = new Double(formateador.format(xcompensada)).doubleValue();
						Double dObj4 = new Double(xcompensada);
						rs.put("xcompensada", dObj4);
					}
					
					suma1=suma1+Integer.parseInt(rs.get("autorizada").toString());
					suma2=suma2+Integer.parseInt(rs.get("noautorizada").toString());
					suma3=suma3+Integer.parseInt(rs.get("rechazada").toString());
					suma4=suma4+Integer.parseInt(rs.get("compensada").toString());
					reporte.add(rs);
					if(log.isDebugEnabled()) log.debug("OBJETO reporte - FACADE:"+reporte);
				}
				
				//criterio de unidad de medida
				//if(medida.equals("01")){ // minutos
				//}
                if(medida.equals("02")){ // horas
                	
                	DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
            		simbolos.setDecimalSeparator('.');
            		DecimalFormat formateador = new DecimalFormat("########.##",simbolos);

            		
                	suma1 = suma1/60;
                	suma2 = suma2/60;
                	suma3 = suma3/60;
                	suma4 = suma4/60;
                	
                	suma1 = new Double(formateador.format(suma1)).doubleValue();
                	suma2 = new Double(formateador.format(suma2)).doubleValue();
                	suma3 = new Double(formateador.format(suma3)).doubleValue();
                	suma4 = new Double(formateador.format(suma4)).doubleValue();

				}
                
                if(medida.equals("03")){ // dias
                	DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
            		simbolos.setDecimalSeparator('.');
            		DecimalFormat formateador = new DecimalFormat("########.##",simbolos);

                	suma1 = suma1/480;
                	suma2 = suma2/480;
                	suma3 = suma3/480;
                	suma4 = suma4/480;
                	
                	suma1 = new Double(formateador.format(suma1)).doubleValue(); 
                	suma2 = new Double(formateador.format(suma2)).doubleValue();
                	suma3 = new Double(formateador.format(suma3)).doubleValue();
                	suma4 = new Double(formateador.format(suma4)).doubleValue();
				}
				
				Map sumas = new HashMap();
				sumas.put("suma1", ""+suma1);
				sumas.put("suma2", ""+suma2);
				sumas.put("suma3", ""+suma3);
				sumas.put("suma4", ""+suma4);
				reporte.add(sumas);
			}	
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		return reporte;
	}//FIN - NVILLAR - 16/04/2012 - LABOR EXCEPCIONAL
	
	/** NVILLAR - 24/04/2012 - LABOR EXCEPCIONAL
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void masivoConsultaHorasAutNoAutComp(String dbpool, HashMap params, String usuario)
			throws IncompleteConversationalState, RemoteException {
		BeanMensaje beanM = new BeanMensaje();
		try {
			QueueDAO qd = new QueueDAO();
			params.put("dbpool", dbpool);
			String fechaIni = (String) params.get("fechaIni");
			String fechaFin = (String) params.get("fechaFin");
			params.put("observacion", "Reporte de Horas de Permanencia Autorizadas, No Autorizadas y Rechazadas para Analista de Asistencia del " + fechaIni + " al " + fechaFin);

			qd.encolaReporte(ReporteMasivoFacadeHome.JNDI_NAME,
					"ConsultaHorasAutNoAutComp", params, usuario);
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
	}//FIN - NVILLAR - 24/04/2012 - LABOR EXCEPCIONAL
	
	/** NVILLAR - LABOR EXCEPCIONAL
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
	public String buscarNombCompleto(String dbpool_sp, String codigo) throws FacadeException{
		String cadena = "";		
		try{			
			pe.gob.sunat.rrhh.dao.T02DAO t02DAO = new pe.gob.sunat.rrhh.dao.T02DAO(dbpool_sp);			
			cadena = t02DAO.findNombreCompleto(codigo);
			
		} catch(Exception e){
			log.error(e);
			throw new FacadeException(this,e.getMessage());
		} finally{
		}
		return cadena.toString(); 
	} //FIN - NVILLAR - LABOR EXCEPCIONAL

	
	//ICAPUNAY-MEMO 32-4F3100-2013
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
        		log.debug("entro if");
        		int nroCar = uoJefe.length();
            	log.debug("nroCar: "+nroCar);
            	char v= '9';  //solo 1 caracter almacena              	
            	for (int p = nroCar-1; p >= 0; p--) {
            		log.debug("entro for");
            		log.debug("uoJefe: "+uoJefe);
            		log.debug("v: "+v);
            		log.debug("p: "+p);
            		v= uoJefe.charAt(p);
            		if (p!=0){
            			log.debug("entro p!=0");
                		if ('0'==v){
                			uoJefe=uoJefe.substring(0, p);
                			log.debug("uoJefe2: "+uoJefe);
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
	//ICAPUNAY-MEMO 32-4F3100-2013
	
	
	//ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta 
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList vacacionesGozadasMatrimonio(String dbpool, String regimen, String fechaIni,
			String fechaFin, String criterio, String valor, HashMap seguridad) throws IncompleteConversationalState,
			RemoteException {

		ArrayList reporte = null;
		BeanMensaje beanM = new BeanMensaje();
		try {

			ReporteDAO reporteDAO = new ReporteDAO();
			T1281DAO vacDAO = new T1281DAO();
			
			//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
			//ICAPUNAY-MEMO 32-4F3100-2013
			/*HashMap roles = (HashMap) seguridad.get("roles");
	    	log.debug("vacacionesGozadasMatrimonio-roles: "+roles);	
	    	if (roles.get(Constantes.ROL_JEFE) != null) {
	    		log.debug("vacacionesGozadasMatrimonio-seguridad antes: "+seguridad);			
				String uoAO = (String) seguridad.get("uoAO");
				seguridad.put("uoSeg", findUuooJefe(uoAO)+"%");//4EB2%
				log.debug("vacacionesGozadasMatrimonio-seguridad final: "+seguridad);
	    	}*/			
			//ICAPUNAY-MEMO 32-4F3100-2013
	    	//ICAPUNAY - PAS20165E230300132

			ArrayList lista = reporteDAO.joinWithT02T12T99findPersonalByCriterioValorInactMF(dbpool,
					regimen, criterio, valor, seguridad);

			if (lista != null) {

				reporte = new ArrayList();
				BeanReporte quiebre = null;
				ArrayList detalle = null;			
				ArrayList detalleVac = null;				
				
				for (int i = 0; i < lista.size(); i++) {
					//HashMap mapa = new HashMap();
					quiebre = (BeanReporte) lista.get(i);

					//Vacaciones gozadas o efectuadas por licencia matrimonio CAS
					detalle = reporteDAO.findVacacionesGozadasMatrimonio(dbpool,
							fechaIni, fechaFin, quiebre.getCodigo());

					if ((detalle != null ) && (detalle.size() > 0)) {
						/*detalleVac = vacDAO.findByCodPersSaldoAnnoIniAnnoFin(dbpool,
								quiebre.getCodigo(), true, Utiles.dameAnho(fechaIni), Utiles.dameAnho(fechaFin));*/
						//insertamos el detalle del quiebre
						//mapa.put("vacacion",detalleVac);
						//quiebre.setMap(mapa);
						quiebre.setDetalle(detalle);
						//log.debug("MAPA "+ mapa);
						log.debug("QUIEBRE " + quiebre);
						//insertamos el quiebre al reporte
						reporte.add(quiebre);
					}
					
				}

			}
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		return reporte;
	}
	
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void masivoVacacionesGozadasMatrimonio(HashMap params, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {

			QueueDAO qd = new QueueDAO();
			String fechaIni = (String) params.get("fechaIni");
			String fechaFin = (String) params.get("fechaFin");
			
			String criterio = params.get("criterio").toString();
			String valor = params.get("valor")!=null?params.get("valor").toString():"";
			criterio = criterio.equals("0")?"Regis":criterio.equals("1")?"UUOO":criterio.equals("5")?"Inten":"Insti";
			
			params.put("observacion", "Reporte vacaciones efectuadas por licencia matrimonio del " + fechaIni
					+ " al " + fechaFin + " - Criterio: "+criterio +(!valor.equals("")?" "+valor:""));

			qd.encolaReporte(ReporteMasivoFacadeHome.JNDI_NAME,
					"vacacionesGozadasMatrimonio", params, usuario);
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
	}	
	//ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta 
	
	//ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral 
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList autorizacionesClimaLaboral(String dbpool, String regimen, String fechaIni,
			String fechaFin, String criterio, String valor, HashMap seguridad) throws IncompleteConversationalState,
			RemoteException {

		ArrayList reporte = null;
		BeanMensaje beanM = new BeanMensaje();
		try {				
			ReporteDAO reporteDAO = new ReporteDAO();
			T8167DAO vacDAO = new T8167DAO(dbpool);
			
			ArrayList lista = reporteDAO.joinWithT02T12T99findPersonalByCriterioValorInactMF(dbpool,
					regimen, criterio, valor, seguridad);
			log.debug("lista(autorizacionesClimaLaboral): " + lista);

			if (lista != null) {

				reporte = new ArrayList();
				BeanReporte quiebre = null;
				ArrayList detalle = null;							
				
				for (int i = 0; i < lista.size(); i++) {
					//HashMap mapa = new HashMap();
					quiebre = (BeanReporte) lista.get(i);
					log.debug("quiebre.getCodigo(): " + quiebre.getCodigo());
					//autorizaciones de clima laboral (fecha/min. clima/min. exceso) por colaborador
					detalle = reporteDAO.findAutorizacionesClimaLaboral(dbpool,
							fechaIni, fechaFin, quiebre.getCodigo());
					log.debug("detalle: " + detalle);	
					if ((detalle != null ) && (detalle.size() > 0)) {						
						quiebre.setDetalle(detalle);						
						log.debug("quiebre: " + quiebre);
						//insertamos el quiebre al reporte
						reporte.add(quiebre);
					}					
				}
			}
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		return reporte;
	}
	
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void masivoAutorizacionesClimaLaboral(HashMap params, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {
			QueueDAO qd = new QueueDAO();
			String fechaIni = (String) params.get("fechaIni");
			String fechaFin = (String) params.get("fechaFin");
			
			String criterio = params.get("criterio").toString();
			String valor = params.get("valor")!=null?params.get("valor").toString():"";
			criterio = criterio.equals("0")?"Regis":criterio.equals("1")?"UUOO":criterio.equals("5")?"Inten":"Insti";
			
			params.put("observacion", "Reporte de autorizaciones de clima laboral del " + fechaIni
					+ " al " + fechaFin + " - Criterio: "+criterio +(!valor.equals("")?" "+valor:""));

			qd.encolaReporte(ReporteMasivoFacadeHome.JNDI_NAME,"autorizacionesClimaLaboral", params, usuario);
			//PRUEBAS LOCALES
			/*ReporteMasivoFacadeHome facadeHome = (ReporteMasivoFacadeHome) sl.getRemoteHome(ReporteMasivoFacadeHome.JNDI_NAME,ReporteMasivoFacadeHome.class);
			ReporteMasivoFacadeRemote remote = facadeHome.create();
			remote.autorizacionesClimaLaboral(params,usuario);*/
			//FIN PRUEBAS
			
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
	}	
	//ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral 
	
	
	//ICAPUNAY - PAS20171U230300074 - Mejoras vacaciones
	/**
	 * Genera el reporte de directivos notificados que no programaron vacaciones de trabajadores 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param dbpool String
	 * @param fechaNotific String
	 * @param fechaNotificFin String	 
	 * @param criterio String
	 * @param valor String	
	 * @return reporte List	
	 * @throws IncompleteConversationalState
	 * @throws RemoteException
	 */
	public ArrayList notificaDirectivosNoVacaciones(String dbpool, String fechaNotific,
			String fechaNotificFin, String criterio, String valor, HashMap seguridad)
			throws IncompleteConversationalState, RemoteException {

		ArrayList reporte = null;
		BeanMensaje beanM = new BeanMensaje();
		DataSource dcsp = sl.getDataSource("java:comp/env/jdbc/dcsp");//datasource de lectura
		pe.gob.sunat.rrhh.asistencia.dao.ReporteDAO reporteDAO = new pe.gob.sunat.rrhh.asistencia.dao.ReporteDAO(dbpool);
		T9036DAO notdir1DAO = new T9036DAO(dbpool);
		T1282DAO t1282dao = new T1282DAO(dcsp);
		pe.gob.sunat.rrhh.dao.T12DAO t12dao = new pe.gob.sunat.rrhh.dao.T12DAO(dcsp);
		String unidadAnterior = "";
		String codUnidad = "";
		
		try {

			Map datos = new HashMap();
			datos.put("unidadAnterior","");
			if (log.isDebugEnabled()) log.debug("Ingresara a notificaDirectivosNoVacaciones-ReporteFacade");
			
			if (constantes.leePropiedad("CRITERIO_UUOO").equals(criterio) || constantes.leePropiedad("CRITERIO_INTD").equals(criterio)) { //uuoo o intendencia			
				Map unidad = t12dao.findByCodUorga(valor);
				if (log.isDebugEnabled()) log.debug("unidad: "+unidad);
				if (unidad!=null && !unidad.isEmpty()){					
					if (!unidad.get("t12cod_uorga").equals("-")){
						unidadAnterior = unidad.get("t12cod_anter")!=null?unidad.get("t12cod_anter").toString().trim():"";
						datos.put("unidadAnterior",unidadAnterior);
					}
				}
			}		
			datos.put("criterio",criterio);
			datos.put("valor",valor);
			datos.put("fechaNotific",fechaNotific);
			datos.put("fechaNotificFin",fechaNotificFin);
			datos.put("seguridad",seguridad);
			datos.put("tipoNotif","2");//2=alerta a directivos con relacion de trabajadores SIN programacion de vacaciones
			if (log.isDebugEnabled()) log.debug("datos: "+datos);
			ArrayList lista = reporteDAO.findNotificaDirectivosVacaciones(datos);
			if (log.isDebugEnabled()) log.debug("lista: "+lista);

			if (lista != null && !lista.isEmpty()) {

				if (log.isDebugEnabled()) log.debug("lista.size: "+lista.size());				
								
				reporte = new ArrayList();
				
				Map quiebre = null;				
				Map colab = null;
				String des_notif="";
				String coduo="";
				String desuo="";
				String regdirec="";
				String apenomdirec="";
				String fecnotif_desc="";
								
				for (int i = 0; i < lista.size(); i++) {
					ArrayList detalle = null;
					detalle = new ArrayList();
					colab = null;
					
					quiebre = (HashMap)lista.get(i);
					des_notif = quiebre.get("des_notif").toString().trim();
					coduo=quiebre.get("coduo").toString().trim();
					desuo=quiebre.get("desuo").toString().trim();
					regdirec=quiebre.get("regdirec").toString().trim();
					apenomdirec=quiebre.get("apenomdirec").toString().trim();
					fecnotif_desc=quiebre.get("fecnotif_desc").toString().trim(); //dd/mm/yyyy
					if (log.isDebugEnabled()) log.debug("quiebre("+i+"): "+quiebre);				
					
					if (!des_notif.equals("")){
						String[] registroTrab = des_notif.split("\\|");
						if (log.isDebugEnabled()) log.debug("registroTrab1: "+registroTrab);
						if (log.isDebugEnabled()) log.debug("registroTrab.length: "+registroTrab.length);
				        for(int a=0; a<registroTrab.length; a++){
				        	if (log.isDebugEnabled()) log.debug("registroTrab1[a]: "+registroTrab[a]);
				        	colab = notdir1DAO.findTrabajadorByRegistro(registroTrab[a]);
				        	if (log.isDebugEnabled()) log.debug("colab: "+colab);
				        	if (colab!=null){
				        		colab.put("coduo",coduo);	
					        	colab.put("desuo",desuo);	
					        	colab.put("regdirec",regdirec);	
					        	colab.put("apenomdirec",apenomdirec);	
					        	colab.put("fecnotif_desc",fecnotif_desc);
					        	if (log.isDebugEnabled()) log.debug("colab2: "+colab);
					        	detalle.add(colab);
					        	if (log.isDebugEnabled()) log.debug("detalle: "+detalle);
				        	}			        	
				        }
				        
				        if (log.isDebugEnabled()) log.debug("detalle("+i+"): "+detalle);
						
						if ((detalle != null ) && (detalle.size() > 0)) {
							
							if (log.isDebugEnabled()) log.debug("detalle.size(): "+detalle.size());
							//insertamos el detalle del quiebre (de tipo Map)
							quiebre.put("detalle",detalle);							
							//insertamos el quiebre al reporte (de tipo List)
							reporte.add(quiebre);
						}		
					}else{//des_notif="", colaboradores en subunidades
						colab = new HashMap();
						colab.put("coduo",coduo);	
			        	colab.put("desuo",desuo);	
			        	colab.put("regdirec",regdirec);	
			        	colab.put("apenomdirec",apenomdirec);	
			        	colab.put("fecnotif_desc",fecnotif_desc);
						colab.put("registro","");	
			        	colab.put("apenom","COLABORADORES SE ENCUENTRAN EN SUBUNIDADES");	
			        	colab.put("fechaingreso_desc","");
			        	detalle.add(colab);
						quiebre.put("detalle",detalle);
						reporte.add(quiebre);
					}	        			
				}
			}
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.toString());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

		if (log.isDebugEnabled()) log.debug("reporte final: "+reporte);
		return reporte;
		
	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void masivoNotificaDirectivosNoVacaciones(String dbpool, HashMap params, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {
			QueueDAO qd = new QueueDAO();
			params.put("dbpool", dbpool);
			String fechaNotific = (String) params.get("fechaNotific");
			log.debug("masivoNotificaDirectivosNoVacaciones - params:" + params);
			log.debug("masivoNotificaDirectivosNoVacaciones - usuario:" + usuario);
			String fechaNotificFin = (String) params.get("fechaNotificFin");
			params.put("observacion", "Reporte de Notificaciones Directivos del " + fechaNotific 
					+ " al " + fechaNotificFin);
			
			qd.encolaReporte(ReporteMasivoFacadeHome.JNDI_NAME,
					"notificaDirectivosNoVacaciones", params, usuario);
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
	}
	
	/**
	 * Genera el reporte de directivos notificados que no programaron vacaciones de trabajadores 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param dbpool String
	 * @param regimen String
	 * @param fechaCorte String	 
	 * @param estado String	
	 * @param criterio String
	 * @param valor String	
	 * @return reporte List	
	 * @throws IncompleteConversationalState
	 * @throws RemoteException
	 */
	public ArrayList vacacionesTruncas(String dbpool, HashMap datos)
			throws IncompleteConversationalState, RemoteException {

		ArrayList reporte = null;
		BeanMensaje beanM = new BeanMensaje();		
		pe.gob.sunat.rrhh.asistencia.dao.ReporteDAO reporteDAO = new pe.gob.sunat.rrhh.asistencia.dao.ReporteDAO(dbpool);
		pe.gob.sunat.sp.asistencia.dao.T1456DAO t1456dao = new pe.gob.sunat.sp.asistencia.dao.T1456DAO();
		
		try {
			if (log.isDebugEnabled()) log.debug("Ingresara a vacacionesTruncas-ReporteFacade");
			String fechaCorte =	(datos.get("fechaCorte") != null) ? ((String)datos.get("fechaCorte")).trim(): "";	
			
			ArrayList lista = (ArrayList)reporteDAO.joinWithT02T12T99findPersonalByCriterioValorEstado(datos);
			if (log.isDebugEnabled()) log.debug("lista: "+lista);

			if (lista != null && !lista.isEmpty()) {

				if (log.isDebugEnabled()) log.debug("lista.size: "+lista.size());				
								
				reporte = new ArrayList();
				
				Map quiebre = null;
				String fechaingreso="";
				String fechaingresomod="";
				String fechacese="";
				String diames_fecing="";
				String anioactual="";
				String anioanterior="";
				FechaBean fbactual = new FechaBean();
				anioactual= fbactual.getAnho();
				log.debug("anioactual: "+anioactual);
				anioanterior=new Integer(Integer.parseInt(anioactual)-1)+"";
				log.debug("anioanterior: "+anioanterior);
				
				Date dfechacorte=new FechaBean(fechaCorte).getSQLDate();
				log.debug("dfechacorte: "+dfechacorte);
				Date dfechaingresoactual=new FechaBean().getSQLDate();
				log.debug("dfechaingresoactual inicial: "+dfechaingresoactual);
				int diasvacaciones=0;				
								
				String registro="";				
				Map hmVacGen= new HashMap();
								
				for (int i = 0; i < lista.size(); i++) {
					quiebre = (HashMap)lista.get(i);
					log.debug("quiebre("+i+"): "+quiebre);	
					registro = quiebre.get("registro")!=null?quiebre.get("registro").toString().trim():"";
					hmVacGen = t1456dao.joinWithT02ByCodPersEstId(dbpool, registro);
					if (log.isDebugEnabled()) log.debug("hmVacGen (truncas): " + hmVacGen);
					if (hmVacGen!=null && hmVacGen.get("fecha")!=null && !hmVacGen.get("fecha").toString().equals("")){
						fechaingreso=hmVacGen.get("fecha").toString();
			    	} else {
			    		fechaingreso = quiebre.get("fechaingreso_desc")!=null?quiebre.get("fechaingreso_desc").toString().trim():"";
			    	}
					log.debug("fechaingreso: "+fechaingreso);
					quiebre.put("fechaingreso_desc",fechaingreso);
					fechacese = quiebre.get("fechacese")!=null?quiebre.get("fechacese_desc").toString().trim():"";
					if (!fechaingreso.equals("")){
						diames_fecing = fechaingreso.substring(0,6);
						log.debug("fechacese: "+fechacese);
						log.debug("fechaingreso: "+fechaingreso);
						log.debug("diames_fecing: "+diames_fecing);
						dfechaingresoactual=new FechaBean(diames_fecing+anioactual).getSQLDate();
						log.debug("dfechaingresoactual: "+dfechaingresoactual);
						if(dfechaingresoactual.after(dfechacorte)){//mayor
							log.debug("dfechaingresoactual>dfechacorte");
							fechaingresomod=diames_fecing+anioanterior;
						}else{//menor igual
							log.debug("dfechaingresoactual<=dfechacorte");
							fechaingresomod=diames_fecing+anioactual;						
						}
						log.debug("fechaCorte: "+fechaCorte);
						log.debug("fechaingresomod: "+fechaingresomod);
						if (!diames_fecing.equals("")){
							diasvacaciones = Utiles.obtenerDiasDiferencia(fechaingresomod, fechaCorte)+1;
							log.debug("diasvacaciones: "+diasvacaciones);
						}										
						if(diasvacaciones>360){
							diasvacaciones=360;
						}
					}						
					quiebre.put("fechacese_desc",fechacese);
					quiebre.put("fechacorte_desc",fechaCorte);
					quiebre.put("fechaingresomod_desc",fechaingresomod);
					quiebre.put("diasvacaciones",diasvacaciones+"");
					
					//insertamos el quiebre al reporte (de tipo List)
					reporte.add(quiebre);						
				}
			}
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.toString());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		if (log.isDebugEnabled()) log.debug("reporte final: "+reporte);
		return reporte;
		
	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void masivoVacacionesTruncas(String dbpool, HashMap params, String usuario)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {
			QueueDAO qd = new QueueDAO();
			params.put("dbpool", dbpool);
			String fechaCorte = (String) params.get("fechaCorte");
			log.debug("masivoVacacionesTruncas - params:" + params);
			log.debug("masivoVacacionesTruncas - usuario:" + usuario);			
			params.put("observacion", "Reporte de Vacaciones Truncas con fecha de corte: " + fechaCorte);
			
			qd.encolaReporte(ReporteMasivoFacadeHome.JNDI_NAME,
					"vacacionesTruncas", params, usuario);
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
	}
	//FIN ICAPUNAY - PAS20171U230300074 - Mejoras vacaciones


}