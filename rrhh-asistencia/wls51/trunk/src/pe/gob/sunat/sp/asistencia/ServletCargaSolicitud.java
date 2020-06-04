package pe.gob.sunat.sp.asistencia;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.rrhh.programacion.ejb.delegate.ProgramacionDelegate;
import pe.gob.sunat.sol.BeanMensaje;
import pe.gob.sunat.sp.asistencia.bean.BeanTurnoTrabajo;
import pe.gob.sunat.sp.asistencia.bean.BeanVacacion;
import pe.gob.sunat.sp.asistencia.ejb.delegate.ArchivoDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.AsistenciaDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.MantenimientoDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.MantenimientoException;
import pe.gob.sunat.sp.asistencia.ejb.delegate.SolicitudDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.TurnoTrabajoDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.UtilesDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.VacacionDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.VacacionException;
import pe.gob.sunat.tecnologia.menu.bean.UsuarioBean;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;

/**
 * @web.servlet name="ServletCargaSolicitud"
 * @web.servlet-mapping url-pattern = "/asisS14Alias"
 * @web.servlet-init-param name = "pool_sp" value = "jdbc/dcsp"
 * @web.servlet-init-param name = "pool_sp_g" value = "jdbc/dgsp"
 * @web.servlet-init-param name = "pool_sig" value = "jdbc/dcsig"
 * 
 *                         Title: ServletCargaSolicitud Description: Clase
 *                         encargada de cargar la pagina con los datos de la
 *                         solicitud Copyright: Copyright (c) 2004 Company:
 *                         Sunat
 * @author cgarratt
 * @version 1.0
 */
public class ServletCargaSolicitud extends HttpServlet {

	private static String MAX_SIZE_FILE_STRING = "512kb";

	private static final Logger log = Logger
			.getLogger(ServletCargaSolicitud.class);
	private static String pool_sp;
	private static String pool_sp_g;
	private static String pool_sig;

	public ServletCargaSolicitud() {
	}

	public void destroy() {
		super.destroy();
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		pool_sp = config.getInitParameter("pool_sp");
		pool_sp_g = config.getInitParameter("pool_sp_g");
		pool_sig = config.getInitParameter("pool_sig");
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		try {
			cargarPaginaDatos(request, response, session);
		} catch (Exception ex) {
			session = request.getSession();
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
					+ ex.getMessage());
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = this.getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
			session.invalidate();
			return;
		}
	}

	/**
	 * Metodo encargado de cargar la pagina para el registro de los datos de la
	 * solicitud
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarPaginaDatos(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			HashMap solicitante = (HashMap) session.getAttribute("solicitante");
			String codPers = (String) solicitante.get("t02cod_pers");
			String tipo = request.getParameter("tipo") != null ? request
					.getParameter("tipo") : "";
			// JVV - INI
			String esReg1057 = (String) solicitante.get("t02cod_rel");
			// JVV - FIN
			boolean entro = false;
			MantenimientoDelegate md = new MantenimientoDelegate();
			VacacionDelegate vd = new VacacionDelegate();
			ProgramacionDelegate pd=new ProgramacionDelegate();
			AsistenciaDelegate asisd=new AsistenciaDelegate();
			HashMap paramVac = null;
			List listaVacaciones = null;
			// JRR - Fecha de reingreso 25 Abril 2008
			String fecha_ingreso = solicitante.get("fecha_ingreso").toString();

			if (tipo.equals("")) {
				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/blank.htm");
				dispatcher.forward(request, response);
				return;
			}

			// JVV - INI
			// COMSERV_CITAJUDICIAL = "103"
			// if (esReg1057.equals("09") &&
			// tipo.equals(Constantes.COMSERV_CITAJUDICIAL) ) {
			Map v = md.validarSolicitud(pool_sp, tipo);
			// if (esReg1057.equals("09") && v.get("t99codigo") == null) {
			if (esReg1057.equals("09") && v == null) {
				BeanMensaje bean = new BeanMensaje();
				bean.setError(true);
				bean.setMensajeerror("NO PUEDE REGISTRAR SOLICITUD, NO APLICABLE PARA CAS - DEC LEG NÂ° 1057");
				bean.setMensajesol("Por favor comuniquese con el Webmaster.");
				session.setAttribute("beanErr", bean);
				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/PagE.jsp");
				dispatcher.forward(request, response);
				return;
			}
			// JVV -FIN

			// wrodriguez PAS20165E230300184
			if (!esReg1057.equals("10") && tipo.equals(Constantes.LICENCIA_JUSTIF_INASIST_DESCONT)) {
				BeanMensaje bean = new BeanMensaje();
				bean.setError(true);
				bean.setMensajeerror("NO PUEDE REGISTRAR SOLICITUD, NO APLICABLE PARA DEC LEG NÂ° 276-728");
				bean.setMensajesol("Por favor comuniquese con el Webmaster.");
				session.setAttribute("beanErr", bean);
				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/PagE.jsp");
				dispatcher.forward(request, response);
				return;
			}
			// fin wrodriguez

			if (tipo.equals(Constantes.VACACION_VENTA)) {
				entro = true;

				// JRR - REVERTIDO 17 MARZO 2008
				// EBV - DesRevertido 08 Abril 2008
				String fecha_actual = Utiles.obtenerFechaActual();
				String anioActual = Utiles.dameAnho(fecha_actual);
				String fecha_base = fecha_ingreso.substring(0, 6) + anioActual;
				String anioSaldoVacVig = "";
				String controlEditable = "0";

				int difDias = Utiles.obtenerDiasDiferencia(fecha_base,
						fecha_actual);
				if (difDias >= 0) {
					anioSaldoVacVig = anioActual;
				} else {
					anioSaldoVacVig = (Integer.parseInt(anioActual) - 1) + "";
				}
				session.setAttribute("annoVenta", anioSaldoVacVig);

				// DTARAZONA PARAMETRIZAR NUMERO DE DIAS MÁXIMO A VENDER DE LAS
				// VACACIONES PROGRMADAS
				String maximoVenta = "";
				String diasVendidas = "";
				
				paramVac = new HashMap();
				paramVac.put("cod_pers", codPers);
				paramVac.put("dbpool", pool_sp);
				paramVac.put("annoVac", anioSaldoVacVig);
				log.debug("Mapa:"+paramVac);
				String diasFraccionados=""+vd.totalFraccionadoPorAnio(paramVac);
				log.debug("Dias Fraccionado:"+diasFraccionados);

				maximoVenta = md.findMaximoVentaVacaciones(pool_sp)
						.get("t99descrip").toString();

				if (md.findDiasVacacionesVendidas(pool_sp, anioSaldoVacVig,	codPers) != null && md.findDiasVacacionesVendidas(pool_sp,
								anioSaldoVacVig, codPers).get("dias_vendidos") != null)
					diasVendidas = md
							.findDiasVacacionesVendidas(pool_sp,
									anioSaldoVacVig, codPers)
							.get("dias_vendidos").toString();
				else
					diasVendidas = "0";
				session.setAttribute("maximoVenta", maximoVenta);
				session.setAttribute("diasVendidas", diasVendidas);
				session.setAttribute("diasFraccionados", diasFraccionados);
				// FIN DTARAZONA

				// JRR - 10 ABRIL 2008
				Map hmMov = md.cargarFindByMov(pool_sp, tipo);
				if (hmMov != null) {
					String qvalida = hmMov.get("qvalida").toString().trim();
					if (!qvalida.equals("1"))
						controlEditable = "1";
				}
				session.setAttribute("controlEditable", controlEditable);
				//

				/***** JRR - 05/12/2008 *****/
				Map params = new HashMap();
				params.put("cod_pers", codPers);
				params.put("anno_vac", anioSaldoVacVig);
				params.put("est_id", Constantes.ACTIVO);
				params.put("licencia", Constantes.VACACION_PROGRAMADA);
				params.put("controlEditable", "0");

				/***** EBV - 11/03/2009 *****/
				Map programacion = vd.buscarProgramacionTrabajador(params);
				List listaProg = (List) programacion.get("lista");
				/***** EBV - 11/03/2009 *****/
				if (log.isDebugEnabled())
					log.debug("listaProg1: " + listaProg);

				if (listaProg != null && listaProg.size() > 0) {
					request.setAttribute("programacion", listaProg);
				} else {
					request.setAttribute(
							"mensajeProgramacion",
							"Para realizar su Solicitud, debe registrar antes la ProgramaciÃ³n y contar con Saldo Vacacional Generado.");
				}
				/*******************************/

				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/solicitudVentaVacaciones.jsp");
				dispatcher.forward(request, response);
				return;
			}

			if (tipo.equals(Constantes.VACACION)) {
				entro = true;

				// JRR - REVERTIDO 17 MARZO 2008
				// EBV - DesRevertido 08 Abril 2008
				String fecha_actual = Utiles.obtenerFechaActual();
				String anioActual = Utiles.dameAnho(fecha_actual);
				String fecha_base = fecha_ingreso.substring(0, 6) + anioActual;
				String anioSaldoVacVig = "";
				String controlEditable = "0";

				int difDias = Utiles.obtenerDiasDiferencia(fecha_base,
						fecha_actual);
				if (difDias >= 0) {
					anioSaldoVacVig = anioActual;
				} else {
					anioSaldoVacVig = (Integer.parseInt(anioActual) - 1) + "";
				}
				session.setAttribute("annoVenta", anioSaldoVacVig);
				//

				// JRR - 10 ABRIL 2008
				Map hmMov = md.cargarFindByMov(pool_sp, tipo);
				if (hmMov != null) {
					String qvalida = hmMov.get("qvalida").toString().trim();
					if (!qvalida.equals("1"))
						controlEditable = "1";
				}
				session.setAttribute("controlEditable", controlEditable);
				//
				String annoVac =request.getParameter("annoVac1").equals("")?anioSaldoVacVig:request.getParameter("annoVac1");//MOD DTN
				if(log.isDebugEnabled()) log.debug("Anio recibido:"+annoVac);
				
				request.setAttribute("annoEvaluado", annoVac);
				/***** JRR - 05/12/2008 *****/
				Map params = new HashMap();
				params.put("cod_pers", codPers);
				/**** EBV - 11/03/2009 ***/
				params.put("codPers", codPers);
				params.put("anho", "");
				params.put("saldo", "1");
				/**** EBV - 11/03/2009 ***/
				params.put("anno_vac", annoVac);//MOD DTN

				params.put("est_id", Constantes.ACTIVO);
				params.put("licencia", Constantes.VACACION_PROGRAMADA);
				params.put("controlEditable", controlEditable);
				/***** EBV - 11/03/2009 *****/
				Map programacion = vd.buscarProgramacionTrabajador(params);
				List listaProg = (List) programacion.get("lista");
				List listaAnno = (List) programacion.get("lanno");
				String ind_convenio = (String) programacion.get("ind_convenio");
				if (("1".equals(controlEditable.trim())) && listaAnno != null
						&& listaAnno.size() > 0) {
					session.setAttribute("annoVenta", ((Map) listaAnno.get(0))
							.get("anno").toString().trim());
				}

				/***** EBV - 11/03/2009 *****/

				if (log.isDebugEnabled())
					log.debug("listaProg2: " + listaProg);

				if (listaProg != null && listaProg.size() > 0) {
					request.setAttribute("programacion", listaProg);
				} else {
					request.setAttribute(
							"mensajeProgramacion",
							"Para realizar su Solicitud, debe registrar antes la Programación y contar con Saldo Vacacional Generado.");
				}
				/*******************************/

				ArrayList listaDias = md.cargarT99(pool_sp,
						Constantes.CODTAB_DIAS_VACACIONES);

				session.removeAttribute("listaDias");
				session.setAttribute("listaDias", listaDias);

				/***** EBV - 12/03/2009 *****/
				session.removeAttribute("ind_convenio");
				session.setAttribute("ind_convenio", ind_convenio);
				/***** EBV - 12/03/2009 *****/
				
				//Ajuste adelanto de vacaciones
				Map datos = new HashMap();//pd.obtenerUsuProgramacion(params);
				datos.put("periodo_Act_vac", anioSaldoVacVig);
				datos.put("periodo_Sig_vac", new Integer(Integer.parseInt(anioSaldoVacVig)+1));
				if(log.isDebugEnabled()) log.debug("Año vigente:"+anioSaldoVacVig);
				
				FechaBean fechabeanIngreso = new FechaBean(fecha_ingreso);
		    	
				String fecha_ini_vac= fechabeanIngreso.getDia().concat("/").concat(fechabeanIngreso.getMes()).concat("/").concat(String.valueOf(Integer.parseInt(anioSaldoVacVig)));
		    	String fecha_gen_sig= fechabeanIngreso.getDia().concat("/").concat(fechabeanIngreso.getMes()).concat("/").concat(String.valueOf(Integer.parseInt(anioSaldoVacVig)+1));
		    	String fecha_gen_sigper = fechabeanIngreso.getDia().concat("/").concat(fechabeanIngreso.getMes()).concat("/").concat(String.valueOf(Integer.parseInt(anioSaldoVacVig)+2));
		   		    	
		    	datos.put("fecha_gen_sig",fecha_gen_sig);
		    	datos.put("fecha_gen_sig_val",fecha_gen_sig);
		    	datos.put("fecha_gen_sigper",fecha_gen_sigper);		 
		    	datos.put("fecha_inicio_vac",fecha_ini_vac);
		    	datos.put("fecha_ini_vac",fecha_ini_vac);
		    	datos.put("fecha_ini_vac_sig",fecha_gen_sig);
		    	if(log.isDebugEnabled()) log.debug("DatosInServlet:"+datos);
				session.setAttribute("datos", datos);
				
				String saldoAnioVig="0";BeanVacacion saldoPorAnio;
				if(vd.buscarSaldoGeneralPorAnno(pool_sp, codPers,false,anioSaldoVacVig,anioSaldoVacVig).size()>0){
					saldoPorAnio=(BeanVacacion)vd.buscarSaldoGeneralPorAnno(pool_sp, codPers,false,anioSaldoVacVig,anioSaldoVacVig).get(0);
					saldoAnioVig=""+saldoPorAnio.getSaldo();
				}
				session.setAttribute("saldoAnioVig", saldoAnioVig);
				//fin

				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher(
								"/solicitudVacacionesEfectivas.jsp");
				dispatcher.forward(request, response);
				return;
			}

			if (tipo.equals(Constantes.VACACION_SUSPENDIDA)
					|| tipo.equals(Constantes.VACACION_PROGRAMADA)
					|| tipo.equals(Constantes.VACACION_POSTERGADA)
					|| tipo.equals(Constantes.REPROGRAMACION_VACACION)) {

				entro = true;
				String annoVac =request.getParameter("annoVac1");
				Map datos = new HashMap();//pd.obtenerUsuProgramacion(params);
				String fecha_actual = Utiles.obtenerFechaActual();
				String anioActual = Utiles.dameAnho(fecha_actual);
				String fecha_base = fecha_ingreso.substring(0, 6) + anioActual;
				String anioSaldoVacVig = "";
				
				int difDias = Utiles.obtenerDiasDiferencia(fecha_base,
						fecha_actual);
				if (difDias >= 0) {
					anioSaldoVacVig = anioActual;
				} else {
					anioSaldoVacVig = (Integer.parseInt(anioActual) - 1) + "";
				}
				datos.put("periodo_Act_vac", anioSaldoVacVig);
				datos.put("periodo_Sig_vac", new Integer(Integer.parseInt(anioSaldoVacVig)+1));
				if(log.isDebugEnabled()) log.debug("Año vigente:"+anioSaldoVacVig);
				
				FechaBean fechabeanIngreso = new FechaBean(fecha_ingreso);
		    	
				String fecha_ini_vac= fechabeanIngreso.getDia().concat("/").concat(fechabeanIngreso.getMes()).concat("/").concat(String.valueOf(Integer.parseInt(anioSaldoVacVig)));
		    	String fecha_gen_sig= fechabeanIngreso.getDia().concat("/").concat(fechabeanIngreso.getMes()).concat("/").concat(String.valueOf(Integer.parseInt(anioSaldoVacVig)+1));
		    	String fecha_gen_sigper = fechabeanIngreso.getDia().concat("/").concat(fechabeanIngreso.getMes()).concat("/").concat(String.valueOf(Integer.parseInt(anioSaldoVacVig)+2));
		   		    	
		    	datos.put("fecha_gen_sig",fecha_gen_sig);
		    	datos.put("fecha_gen_sig_val",fecha_gen_sig);
		    	datos.put("fecha_gen_sigper",fecha_gen_sigper);		 
		    	datos.put("fecha_inicio_vac",fecha_ini_vac);
		    	datos.put("fecha_ini_vac",fecha_ini_vac);
		    	datos.put("fecha_ini_vac_sig",fecha_gen_sig);
		    	if(log.isDebugEnabled()) log.debug("DatosInServlet:"+datos);
				session.setAttribute("datos", datos);
				paramVac = new HashMap();
				paramVac.put("cod_pers", codPers);
				paramVac.put("orden", "");
				paramVac.put("dbpool", pool_sp);
				paramVac.put("annoVac", annoVac);
				if (tipo.equals(Constantes.VACACION_SUSPENDIDA)) {
					paramVac.put("licencia", "");
				} else if (tipo.equals(Constantes.VACACION_POSTERGADA)) {
					paramVac.put("licencia", "");
					//paramVac.put("", "");
				} else {
					paramVac.put("licencia", tipo);
				}

				listaVacaciones = vd.buscarPendientes(paramVac);
				int totalDiasVendidos=vd.totalDiasVendidosPorAnno(paramVac);
				int totalFraccionado=vd.totalFraccionadoPorAnio(paramVac);
				int totalVentasEnSol=vd.totalVentasEnSol(paramVac);
				int totalReprogEnSol=vd.totalReprogEnSol(paramVac);
				
				int saldo=0,diasVac=0;BeanVacacion saldoPorAnio;
				if(vd.buscarSaldoGeneralPorAnno(pool_sp, codPers,false,annoVac,annoVac).size()>0){
					saldoPorAnio=(BeanVacacion)vd.buscarSaldoGeneralPorAnno(pool_sp, codPers,false,annoVac,annoVac).get(0);
					saldo=saldoPorAnio.getSaldo();
					diasVac=saldoPorAnio.getDias();
				}
				log.debug("Año:"+annoVac);
				HashMap mapa=new HashMap();
				mapa.put("dbpool", pool_sp);
				mapa.put("codPers",codPers);
				mapa.put("annoVac", annoVac);
				mapa.put("fecIngreso", solicitante.get("t02f_ingsun"));
				int diasDispAdelanto=vd.numDiasDisponibleAdelanto(mapa);
				
				session.removeAttribute("listaVacaciones");
				session.setAttribute("listaVacaciones", listaVacaciones);	
				session.setAttribute("diasvendidos", ""+totalDiasVendidos);
				session.setAttribute("totalFraccionado", ""+totalFraccionado);
				session.setAttribute("totalVentasEnSol", ""+totalVentasEnSol);
				session.setAttribute("totalReprogEnSol", ""+totalReprogEnSol);
				session.setAttribute("saldo", ""+saldo);
				session.setAttribute("diasVac", ""+diasVac);
				session.setAttribute("annoVac", annoVac);
				session.setAttribute("diasAdelanto", ""+diasDispAdelanto);
				
				List listaVentas=vd.listaVentas(paramVac);
				session.removeAttribute("listaVentas");
				session.setAttribute("listaVentas", listaVentas);

				if (tipo.equals(Constantes.REPROGRAMACION_VACACION)) {
					RequestDispatcher dispatcher = getServletContext()
							.getRequestDispatcher(
									"/solicitudReprogramacion.jsp?tamanioPagina=3");
					dispatcher.forward(request, response);
					return;
				} else if (tipo.equals(Constantes.VACACION_POSTERGADA)) {

					ArrayList listaDias = md.cargarT99(pool_sp,
							Constantes.CODTAB_DIAS_VACACIONES);
					ArrayList annos=md.cargarAnnosVac(pool_sp,codPers);
					if(log.isDebugEnabled()) log.debug("annosVac:"+annos);
					ArrayList dias=md.cargarDiasVac(pool_sp);
					
					HashMap param=new HashMap();
					param.put("dbpool", pool_sp);
					param.put("t99cod_tab", Constantes.CODTAB_PARAMETROS_ASISTENCIA);
					param.put("t99codigo", "52");
					String diasMaximoFraccionar=asisd.findParamByCodTabCodigo(param);
					
					param.put("t99codigo", "53");
					String permitirAdelanto=asisd.findParamByCodTabCodigo(param);
					
					param.put("t99codigo", "54");
					String ffinMayorGenera=asisd.findParamByCodTabCodigo(param);
					
					session.removeAttribute("listaDias");
					session.setAttribute("listaDias", listaDias);
					session.setAttribute("AnnosVac", annos);
					session.setAttribute("DiasVac", dias);
					session.setAttribute("diasMaximoFraccionar", diasMaximoFraccionar);
					session.setAttribute("permitirAdelantoParam", permitirAdelanto);
					session.setAttribute("ffinMayorGenera", ffinMayorGenera);
					//session.setAttribute("permitirFrac8", permitirFrac8);
					if(log.isDebugEnabled()) log.debug("Annos y dias:"+annos+"-"+dias+"-"+diasMaximoFraccionar);

					RequestDispatcher dispatcher = getServletContext()
							.getRequestDispatcher(
									"/solicitudPostergacion.jsp?tamanioPagina=3");
					dispatcher.forward(request, response);
					return;
				} else {
					RequestDispatcher dispatcher = getServletContext()
							.getRequestDispatcher(
									"/solicitudVacacionesListado.jsp?tamanioPagina=3");
					dispatcher.forward(request, response);
					return;
				}
			}

			if (tipo.equals(Constantes.LICENCIA_NACIMIENTO)) {
				entro = true;
				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher(
								"/solicitudLicenciaNacimiento.jsp");
				dispatcher.forward(request, response);
				return;
			}

			if (tipo.equals(Constantes.MOV_OMISION_MARCA)) {
				entro = true;
				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/solicitudOmisionMarca.jsp");
				dispatcher.forward(request, response);
				return;
			}

			if (tipo.equals(Constantes.MOV_ANULACION_MARCA)) {
				entro = true;
				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/solicitudAnulacionMarca.jsp");
				dispatcher.forward(request, response);
				return;
			}

			if (tipo.equals(Constantes.LICENCIA_ENFERMEDAD)) {
				entro = true;
				session.removeAttribute("numArchivo");
				UtilesDelegate ud = new UtilesDelegate();
				HashMap filtro = new HashMap();
				filtro.put("cod_tab", Constantes.T5864_CODTAB_HABILITACIONES);        	
	           	filtro.put("cod_tip_desc", "D");
	           	//filtro.put("cod_estado", "A"); //@jhuamanr
	           	filtro.put("num_codigo", Constantes.T5864_CODTAB_HABILITACIONES_MENSAJE_LICENCIA);   
				ArrayList habilitacion  = ud.buscarT5864Parametro(filtro); 
				if(habilitacion!=null&& !habilitacion.isEmpty()) {
					Map hb =(Map) habilitacion.get(0);
					String mensaje =hb.get("des_larga").toString().trim();
					session.setAttribute("mensajeDocumento", mensaje );
				}else{
					session.setAttribute("mensajeDocumento", "" );
				}
				 	
				 
				ArchivoDelegate ad = new ArchivoDelegate();
				Integer  numArchivo =new Integer( ad.obtenerNumArchivo());
				session.setAttribute("numArchivo",   numArchivo); 
				 
				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/SolicitudEnfermedad.jsp");
				dispatcher.forward(request, response);
				return;
			}

			// FRD - 20/04/2009
			if (tipo.equals(Constantes.VACACION_INDEMNIZADA)) {
				Map saldoHM = new HashMap();
				List Lsaldos = new ArrayList();
				List LsaldosPuro = new ArrayList();
				String fecha_actual = Utiles.obtenerFechaActual();
				String periodo = Utiles.obtenerAnhoActual()
						+ fecha_actual.substring(3, 5);
				String fechasVigentes = "";
				Map hmVV = new HashMap();
				entro = true;
				hmVV.put("CodPers", codPers);

				Lsaldos = vd.validarVacacionesVencidas(hmVV);
				if (log.isDebugEnabled())
					log.debug("Lsaldos: " + Lsaldos);

				if (Lsaldos != null && Lsaldos.size() > 0) {
					for (int i = 0; i < Lsaldos.size(); i++) {
						saldoHM = (HashMap) Lsaldos.get(i);
						fechasVigentes = (String) saldoHM.get("t99descrip");
						if (fechasVigentes.indexOf(fecha_actual) == -1) {
						} else {
							LsaldosPuro.add(saldoHM);
						}
						// log.debug("LsaldosPuro: "+ LsaldosPuro);
					}
				}

				if (LsaldosPuro != null && LsaldosPuro.size() > 0) {
					session.removeAttribute("periodo");
					session.setAttribute("periodo", periodo);
					session.removeAttribute("Lsaldos");
					session.setAttribute("Lsaldos", LsaldosPuro);
					RequestDispatcher dispatcher = getServletContext()
							.getRequestDispatcher(
									"/SolicitudVacacionesVencidas.jsp");
					dispatcher.forward(request, response);
				} else {
					BeanMensaje bean = new BeanMensaje();
					bean.setError(true);
					bean.setMensajeerror("No se ha encontrado informaciÃ³n para procesar esta opciÃ³n segÃºn la Fecha Actual.");
					bean.setMensajesol("Por favor revise sus datos de vacaciones y el Comunicado enviado por RRHH al respecto.");
					session.setAttribute("beanErr", bean);
					RequestDispatcher dispatcher = getServletContext()
							.getRequestDispatcher("/PagE.jsp");
					dispatcher.forward(request, response);
				}
				return;
			}
			//

			// EBV 03/04/2012
			if (tipo.equals(Constantes.MOV_SOLICITUD_COMPENSACION)) {
				entro = true;
				/*
				 * TurnoTrabajoDelegate ttd = new TurnoTrabajoDelegate();
				 * BeanTurnoTrabajo bturno = ttd.buscarTrabTurno(pool_sp,
				 * codPers, new FechaBean().getFormatDate("dd/MM/yyyy")); if (
				 * bturno==null ) { BeanMensaje bean = new BeanMensaje();
				 * bean.setError(true); bean.setMensajeerror(
				 * "NO PUEDE REGISTRAR SOLICITUD, NO TIENE TURNO ASIGNADO");
				 * bean.setMensajesol(
				 * "Por favor comuniquese con Asistencia Tributos Lima.");
				 * session.setAttribute("beanErr", bean); RequestDispatcher
				 * dispatcher =
				 * getServletContext().getRequestDispatcher("/PagE.jsp");
				 * dispatcher.forward(request, response); return; }else{ if
				 * (!bturno.isControla() ) { if (log.isDebugEnabled())
				 * log.debug("R67: " + bturno.getTurno()); if
				 * (log.isDebugEnabled()) log.debug("Operativo: " +
				 * !bturno.isOperativo()); if (log.isDebugEnabled())
				 * log.debug("Controla: " + !bturno.isControla()); BeanMensaje
				 * bean = new BeanMensaje(); bean.setError(true);
				 * bean.setMensajeerror
				 * ("NO PUEDE REGISTRAR SOLICITUD, NO APLICABLE PARA SIN CONTROL"
				 * ); bean.setMensajesol(
				 * "Por favor comuniquese con Asistencia Tributos Lima.");
				 * session.setAttribute("beanErr", bean); RequestDispatcher
				 * dispatcher =
				 * getServletContext().getRequestDispatcher("/PagE.jsp");
				 * dispatcher.forward(request, response); return; } }
				 */

				HashMap datos = new HashMap();
				datos.put("cod_pers", codPers);
				SolicitudDelegate sd = new SolicitudDelegate();
				String saldole = sd.findLaborAutorizadaSaldo(datos).toString();
				session.removeAttribute("saldole");
				session.setAttribute("saldole", saldole);
				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/SolicitudCompensa.jsp");
				dispatcher.forward(request, response);
				return;
			}
			//

			// MTM - 12/03/2012
			if (tipo.equals(Constantes.MOV_LABOR_EXCEPCIONAL)) {
				entro = true;
				/*
				 * TurnoTrabajoDelegate ttd = new TurnoTrabajoDelegate();
				 * BeanTurnoTrabajo bturno = ttd.buscarTrabTurno(pool_sp,
				 * codPers, new FechaBean().getFormatDate("dd/MM/yyyy")); if (
				 * bturno==null ) { BeanMensaje bean = new BeanMensaje();
				 * bean.setError(true); bean.setMensajeerror(
				 * "NO PUEDE REGISTRAR SOLICITUD, NO TIENE TURNO ASIGNADO");
				 * bean.setMensajesol(
				 * "Por favor comuniquese con Asistencia Tributos Lima.");
				 * session.setAttribute("beanErr", bean); RequestDispatcher
				 * dispatcher =
				 * getServletContext().getRequestDispatcher("/PagE.jsp");
				 * dispatcher.forward(request, response); return; }else{ if
				 * ((!bturno.isControla()) ) { if (log.isDebugEnabled())
				 * log.debug("R67: " + bturno.getTurno()); if
				 * (log.isDebugEnabled()) log.debug("Operativo: " +
				 * !bturno.isOperativo()); if (log.isDebugEnabled())
				 * log.debug("Controla: " + !bturno.isControla()); BeanMensaje
				 * bean = new BeanMensaje(); bean.setError(true);
				 * bean.setMensajeerror
				 * ("NO PUEDE REGISTRAR SOLICITUD, NO APLICABLE PARA SIN CONTROL"
				 * ); bean.setMensajesol(
				 * "Por favor comuniquese con Asistencia Tributos Lima.");
				 * session.setAttribute("beanErr", bean); RequestDispatcher
				 * dispatcher =
				 * getServletContext().getRequestDispatcher("/PagE.jsp");
				 * dispatcher.forward(request, response); return; } }
				 */
				SolicitudDelegate sd = new SolicitudDelegate();
				
				HashMap param=new HashMap();
	            param.put("t99cod_tab", "510");
	            param.put("t99codigo", "47");
	            param.put("dbpool",pool_sp);
	            String stado=sd.findEstadoByCodTabCodigo(param);
	            session.removeAttribute("estadoFlag");
				session.setAttribute("estadoFlag", stado);
				
				HashMap datos = new HashMap();
				datos.put("cod_pers", codPers);
				
				HashMap planillaActiva =(HashMap)sd.findPlanillaActiva(datos);
				session.removeAttribute("planillaActiva");
				session.setAttribute("planillaActiva", planillaActiva);
				log.debug("Planilla:"+planillaActiva+"-"+pool_sp);
				
				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/solicitudLaborExcepcional.jsp");
				dispatcher.forward(request, response);
				return;
			}
			// AGONZALESF -PAS20171U230200001 - solicitud de reintegro
			session.removeAttribute("solReintegro");
			session.removeAttribute("tipDevoluciones");
			session.removeAttribute("detalles");
			session.removeAttribute("descMovimiento");
			session.removeAttribute("identificador");
			session.removeAttribute("colaborador");
			session.removeAttribute("anioPeriodo");
			session.removeAttribute("mesPeriodo");
			session.removeAttribute("tipoDetalle");
			session.removeAttribute("numArchivo");
			if (tipo.equals(Constantes.MOV_REINTEGRO_POR_DESCUENTO_ASISTENCIA)
					|| tipo.equals(Constantes.MOV_REINTEGRO_POR_DESCUENTO_SUBSIDIO)) {
				entro = true;
				session.setAttribute("tipo", tipo);
				UsuarioBean bUsuario = (UsuarioBean) session
						.getAttribute("usuarioBean");
				String codigo = bUsuario.getNroRegistro();
				log.debug("Codigo de trabajador en login ->" + codigo);
				log.debug("Codigo de trabajador solicitante  ->" + codPers);
				if (!codigo.equals(codPers)) {
					BeanMensaje bean = new BeanMensaje();
					bean.setError(true);
					bean.setMensajeerror("No puede solicitar");
					session.setAttribute("beanErr", bean);
					RequestDispatcher dispatcher = getServletContext()
							.getRequestDispatcher("/PagE.jsp");
					dispatcher.forward(request, response);
					return;
				}
				SolicitudDelegate sd = new SolicitudDelegate();
				Map dataTrabajador = sd.obtenerDataTrabajador(pool_sig, codPers); // obtener Regimen Laboral
				log.debug(dataTrabajador);
				String regimenLaboral = (String) dataTrabajador.get("reg_lab_per");
				String tipoPlan = (String) dataTrabajador.get("tipo_plan_tpl");
				
				// verificar que pertenece 276,728 o CAS
				//AGONZALESF - PAS20191U230200011 - se deja de usar , se validara contra el parametro
				/*if (!(regimenLaboral.equals(Constantes.D_LEG_276)
						|| regimenLaboral.equals(Constantes.D_LEG_728_P_I)
						|| regimenLaboral.equals(Constantes.D_LEG_728_S_M)
						|| regimenLaboral.equals(Constantes.D_LEG_728_T_P)
						|| regimenLaboral.equals(Constantes.D_LEG_728_C_E_S)
						|| regimenLaboral.equals(Constantes.D_LEG_728_C_N_TEMP) || regimenLaboral
							.equals(Constantes.D_LEG_1057_CAS))) {
					BeanMensaje bean = new BeanMensaje();
					bean.setError(true);
					bean.setMensajeerror("Su régimen laboral no permite seleccionar este tipo de solicitud");
					session.setAttribute("beanErr", bean);
					RequestDispatcher dispatcher = getServletContext()
							.getRequestDispatcher("/PagE.jsp");
					dispatcher.forward(request, response);
					return;
 				} */
				
				Map filtro = new HashMap();
				filtro.put("tipo",tipo);
				filtro.put("tipo_plan_tpl", tipoPlan );
				filtro.put("reg_lab_per",regimenLaboral);
				
				log.debug("Verificar regimen de la planilla para solicitar devoluciones");
				boolean esRegimenConfigurado = sd.verificarRegimenConfigurado(filtro);
				if (!esRegimenConfigurado) {
					log.debug("EX081:Debido a su régimen laboral usted no puede solicitar devoluciones , por favor coordinar con la división de compensaciones de la INRH");
					BeanMensaje bean = new BeanMensaje();
					bean.setError(true);
					bean.setMensajeerror("Debido a su régimen laboral usted no puede solicitar devoluciones , por favor coordinar con la división de compensaciones de la INRH");
					session.setAttribute("beanErr", bean);
					RequestDispatcher dispatcher = getServletContext()
							.getRequestDispatcher("/PagE.jsp");
					dispatcher.forward(request, response);
					return;
				}
				
				
				
				ArchivoDelegate ad = new ArchivoDelegate();
				int numArchivo = ad.obtenerNumArchivo();
				session.setAttribute("numArchivo", new Integer(numArchivo));
				HashMap datos = new HashMap();
				datos.put("num_registro", codPers);
// 	                        Map anioMeses = sd.obtenerAnioMesesParaSolReintegro(datos);
//				session.setAttribute("anioMeses", anioMeses);
				Map planillas  = sd.obtenerPlanillasParaSolReintegro(datos);
				session.setAttribute("planillas", planillas);
				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher(
								"/solicitudReintegroDescuento.jsp");
				dispatcher.forward(request, response);
				return;
			}
			// FIN AGONZALESF -PAS20171U230200001 - solicitud de reintegro
			if (!entro) {
				session.setAttribute("tipo", tipo); // jquispecoi licencia
													// matrimonio
				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/solicitudBase.jsp");
				dispatcher.forward(request, response);
				return;
			}

		} catch (MantenimientoException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (VacacionException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
					+ e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}
}