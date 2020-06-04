package pe.gob.sunat.sp.asistencia;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.apache.log4j.NDC;

import pe.gob.sunat.framework.core.bean.ParamBean;
import pe.gob.sunat.framework.core.ejb.FacadeException;
import pe.gob.sunat.framework.core.pattern.DynaBean;
import pe.gob.sunat.framework.core.pattern.ServiceLocator;
import pe.gob.sunat.framework.core.servlet.ServletAbstract;
import pe.gob.sunat.framework.util.cache.dao.ParamDAO;
import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.framework.util.lang.Ordenamiento;
import pe.gob.sunat.sol.BeanMensaje;
//import pe.gob.sunat.sp.asistencia.bean.BeanAsistencia;
import pe.gob.sunat.sp.asistencia.bean.BeanDevolucion;
import pe.gob.sunat.sp.asistencia.bean.BeanResumen;
import pe.gob.sunat.sp.asistencia.bean.BeanPeriodo;
import pe.gob.sunat.sp.asistencia.bean.BeanTurnoTrabajo;
import pe.gob.sunat.sp.asistencia.dao.T1270DAO;
import pe.gob.sunat.sp.asistencia.ejb.delegate.AsistenciaDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.AsistenciaException;
import pe.gob.sunat.sp.asistencia.ejb.delegate.HoraExtraDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.MantenimientoDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.MantenimientoException;
import pe.gob.sunat.sp.asistencia.ejb.delegate.SolicitudDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.SolicitudException;
import pe.gob.sunat.sp.asistencia.ejb.delegate.UtilesDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.UtilesException;
import pe.gob.sunat.sp.asistencia.ejb.delegate.VacacionDelegate;
import pe.gob.sunat.tecnologia.menu.bean.UsuarioBean;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;

/**
 * @web.servlet name="ServletAsistencia"
 * @web.servlet-mapping url-pattern = "/asisS02Alias"
 * @web.servlet-init-param name = "pool_sp" value = "jdbc/dcsp"
 * @web.servlet-init-param name = "pool_sp_g" value = "jdbc/dgsp"
 * 
 * <p>Title: Control de Asistencia</p>
 * <p>Description: Control de Asistencia</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: SUNAT</p>
 * @author cgarratt
 * @version 1.0
 */
public class ServletAsistencia extends ServletAbstract {
	private static final Logger log = Logger.getLogger(ServletAsistencia.class);
	private static String pool_sp;
	private static String pool_sp_g;
	ServiceLocator sl =  ServiceLocator.getInstance();
	
	//JVV-02/03/2012-DECRETO SUPREMO 004-2006
	private static DataSource dsSP;
	List listaFechaDS=null;

	public ServletAsistencia() {
	}

	public void destroy() {
		super.destroy();
	}

	public void init(ServletConfig config) throws ServletException {
		try {		
			super.init(config);
			pool_sp = config.getInitParameter("pool_sp");
			pool_sp_g = config.getInitParameter("pool_sp_g");

			cargarParametros();
		} catch (Exception e) {
			log.error("**** Error ****", e);
			throw new ServletException(e.getMessage());
		} finally {
		}
	}

	public void procesa(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		try{
		String accion = request.getParameter("accion");
		
		if (session == null) {
			RequestDispatcher dispatcher =
				getServletContext().getRequestDispatcher("/PagSession.jsp");
			dispatcher.forward(request, response);
			return;
		}

		UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
		
/*		
		//JRR - PARA PRUEBAS
		log.debug("CONTENIDO DE BEAN USUARIO ANTES DEL CAMBIO: " + bUsuario);
		
		Map mapa = new HashMap();
		Map aux2 = new HashMap();
        ArrayList arraux = new ArrayList();

        arraux.add("SIRH-EMPLEADO");
		arraux.add("SIRH-JEFE");
		arraux.add("SIRH-ANAL.CENTRAL");
		
        aux2.put("*", arraux);		
		mapa.put("roles", aux2);

		bUsuario.setMap(mapa);
		session.setAttribute("usuarioBean", bUsuario);
		
		bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
		log.debug("CONTENIDO DE BEAN USUARIO RELOADED: " + bUsuario);
		//
*/		
		if (bUsuario!=null)
			NDC.push(bUsuario.getLogin().concat("-").concat(bUsuario.getTicket()));

		//BeanUsuario bUsuario = (BeanUsuario) session.getAttribute("beanusuario");
		//NDC.push(bUsuario.getNumreg());		
		log.info("Accion : " + accion);
		
		if (accion.equals("generarRegAsistencia")) {
			//ASANCHEZZ 20100409
			//generarRegAsistencia(request, response, session);
			DynaBean dBean = new DynaBean(request);
			generarRegAsistencia(request, response, session, dBean);
			//FIN
		}
		if (accion.equals("cargarProcesoAsistencia")) {
			cargarProcesoAsistencia(request, response, session);
		}
		if (accion.equals("procesarAsistencia")) {
			procesarAsistencia(request, response, session);
		}
		if (accion.equals("cargarProcesoCalificacion")) {
			cargarProcesoCalificacion(request, response, session);
		}
		if (accion.equals("procesarCalificacion")) {
			procesarCalificacion(request, response, session);
		}
		if (accion.equals("cargarCierreAsistencia")) {
			cargarCierreAsistencia(request, response, session);
		}
		if (accion.equals("cerrarAsistencia")) {
			cerrarAsistencia(request, response, session);
		}
		if (accion.equals("buscarAsistencias")) {
			buscarAsistencias(request, response, session);
		}
		if (accion.equals("cargarAsistencias")) {
			cargarAsistencias(request, response, session);
		}
		if (accion.equals("cargarAutPapeletas")) {
			cargarAutPapeletas(request, response, session);
		}
		if (accion.equals("buscarPapeletas")) {
			buscarPapeletas(request, response, session);
		}
		if (accion.equals("calificarPapeletas")) {
			calificarPapeletas(request, response, session);
		}
		if (accion.equals("cargarCalificaciones")) {
			cargarCalificaciones(request, response, session);
		}
		if (accion.equals("actualizarCalificaciones")) {
			actualizarCalificaciones(request, response, session);
		}
		if (accion.equals("actualizarPapeletas")) {
			actualizarPapeletas(request, response, session);
		}		
		if (accion.equals("cargarAsistenciaResumen")) {
			cargarAsistenciaResumen(request, response, session);
		}
		if (accion.equals("buscarAsisResumen")) {
			buscarAsisResumen(request, response, session);
		}
		if (accion.equals("cargaDatosDevolucion")) {
			cargaDatosDevolucion(request, response, session);
		}
		if (accion.equals("grabarDevolucion")) {
			grabarDevolucion(request, response, session);
		}
		if (accion.equals("cargarGenerarRegAsis")) {
			cargarGenerarRegAsis(request, response, session);
		}
	    if (accion.equals("registrarSupervisor")) {
	    	registrarSupervisor(request, response, session);
	    }
	    if (accion.equals("cargarProcesoUnificado")) {
	    	cargarProcesoUnificado(request, response, session);
	    }
	    if (accion.equals("procesarUnificado")) {
	    	procesarUnificado(request, response, session);
	    }
	    //ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
	    if (accion.equals("cargarPermisoRefrigerio")) {
	    	cargarPermisoRefrigerio(request, response, session);
		}
	    if (accion.equals("registrarPermisoRefrigerio")) {
	    	registrarPermisoRefrigerio(request, response, session);
		}
	    if (accion.equals("buscarColaboradoresClima")) {
	    	buscarColaboradoresClima(request, response, session);
		}
	    //FIN
		}
		catch(Exception e){
			  log.error("*** Error ***", e);
			  
		} finally{
			  NDC.pop();
			  NDC.remove();
		}

	}

	/**
	 * Metodo encargado de migrar las marcaciones al detalle de asistencia
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param session
	 *            HttpSession
	 * @throws ServletException
	 * @throws IOException
	 */
	//ASANCHEZZ 20100409
	/*
	private void generarRegAsistencia(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
	*/
	private void generarRegAsistencia(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, DynaBean dbean)
			throws ServletException, IOException {
	//FIN

		try {
			//log.debug("empiezo el metodo generarRegAsistencia del servlet");
			//BeanUsuario bUsuario = (BeanUsuario) session.getAttribute("beanusuario");
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			String usuario = bUsuario.getLogin();

			String criterio = request.getParameter("cmbCriterio");
			String valor =  (request.getParameter("txtValor")!= null )?request.getParameter("txtValor"):"";
			String fecIni = request.getParameter("fechaIni");
			String fecFin = request.getParameter("fechaFin");
			
			//JRR
			if(log.isDebugEnabled())log.debug("valor: " + valor);
			String codigazo = request.getParameter("cod_intendencia");
			if(log.isDebugEnabled())log.debug("codigazo: " + codigazo);
			
			if (valor != null && valor.trim().equals("")) {
				valor = codigazo;
			}
			//
			
			//ASANCHEZZ 20100409
			String tipoDL = dbean.getString("cmbTipoDL");
			//FIN

			HashMap mapa = new HashMap();
			mapa.put("dbpool", pool_sp_g);			
			mapa.put("criterio", criterio);
			mapa.put("valor", valor);
			mapa.put("fechaIni", fecIni);
			mapa.put("fechaFin", fecFin);
			mapa.put("codPers", codigo);
			mapa.put("usuario", usuario);
			//ASANCHEZZ 20100409
			mapa.put("regimen", tipoDL);
			//FIN

			if(log.isDebugEnabled()) log.debug("Llamando a generarRegistroAsistencia : "+mapa);
			
			AsistenciaDelegate ad = new AsistenciaDelegate();
			boolean res = ad.generarRegistroAsistencia(mapa);
			
			if (res) {
				request.setAttribute("nomProceso",
						"GENERACION DE REGISTRO DE ASISTENCIA");
				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/resultadoProceso.jsp");
				dispatcher.forward(request, response);
				return;
			} else {
				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher(
								"/generarRegistroAsistencia.jsp?res=" + res);
				dispatcher.forward(request, response);
				return;
			}
		} catch (AsistenciaException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
					+ e.getMessage());
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * M�todo encargado de cargar los datos necesarios para el proceso de
	 * asistencia
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param session
	 *            HttpSession
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarProcesoAsistencia(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			MantenimientoDelegate md = new MantenimientoDelegate();
			ArrayList periodos = md.cargarPeriodos(pool_sp);

			session.removeAttribute("listaPeriodos");
			session.setAttribute("listaPeriodos", periodos);

			RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/procesarAsistencia.jsp");
			dispatcher.forward(request, response);
			return;

		} catch (MantenimientoException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
					+ e.getMessage());
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * M�todo encargado de cargar los datos necesarios para el proceso de
	 * calificaci�n
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param session
	 *            HttpSession
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarProcesoCalificacion(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/procesarCalificacion.jsp");
			dispatcher.forward(request, response);
			return;
		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
					+ e.getMessage());
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * M�todo encargado de ejecutar el proceso de asistencia
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param session
	 *            HttpSession
	 * @throws ServletException
	 * @throws IOException
	 */
	private void procesarAsistencia(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			//BeanUsuario bUsuario = (BeanUsuario) session.getAttribute("beanusuario");
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			String usuario = bUsuario.getLogin();
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			if(log.isDebugEnabled()) log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			if(log.isDebugEnabled()) log.debug("Roles "+roles);
			
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			Map seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codigo);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("uoAO", bUsuario.getCodUO());//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
			

			String criterio = request.getParameter("cmbCriterio");
			//String valor = request.getParameter("txtValor");
			String valor =  (request.getParameter("txtValor")!= null )?request.getParameter("txtValor"):"";
			String periodo = request.getParameter("cmbPeriodo");
			String indPap = request.getParameter("chk_ind_pap")!= null ? Constantes.ACTIVO:Constantes.INACTIVO;
			//JROJAS4 - 25/03/2010
			String regimen = request.getParameter("cmbRegimen");
			
			//JRR - 2011
			if(log.isDebugEnabled())log.debug("valor: " + valor);
			String codigazo = request.getParameter("cod_intendencia");
			if(log.isDebugEnabled())log.debug("codigazo: " + codigazo);
			
			if (valor != null && valor.trim().equals("")) {
				valor = codigazo;
			}
			//

			Map mProceso = new HashMap();
			mProceso.put("dbpool", pool_sp_g);
			mProceso.put("regimen", regimen);
			mProceso.put("periodo", periodo);
			mProceso.put("criterio", criterio);
			mProceso.put("valor", valor);
			mProceso.put("codigo", codigo);
			mProceso.put("usuario", usuario);
			mProceso.put("seguridad", seguridad);
			mProceso.put("indPap", indPap);
			//ASANCHEZZ 20100514 - PARA QUE SE ACTUALICE EL ESTADO DEL PROCESO Y GENERE EL ZIP CORRESPONDIENTE
			mProceso.put("codPersT1481", codigo);
			//FIN
			
			AsistenciaDelegate ad = new AsistenciaDelegate();
			/*String res = ad.procesarAsistencia(pool_sp_g, periodo, criterio,
					valor, codigo, usuario, seguridad, indPap);*/
			String res = ad.procesarAsistencia(mProceso);

			if (!res.equals(Constantes.OK)) {
				throw new Exception(res);
			}

			request.setAttribute("nomProceso",
					"GENERACION DE RESUMEN DE ASISTENCIA");
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/resultadoProceso.jsp");
			dispatcher.forward(request, response);
			return;

		} catch (AsistenciaException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
					+ e.getMessage());
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * M�todo encargado de ejecutar el proceso de calificaci�n
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param session
	 *            HttpSession
	 * @throws ServletException
	 * @throws IOException
	 */
	private void procesarCalificacion(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			//BeanUsuario bUsuario = (BeanUsuario) session.getAttribute("beanusuario");
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			String usuario = bUsuario.getLogin();
			//HashMap roles = super.getRoles(session);
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			log.debug("Roles "+roles);
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codigo);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("uoAO", bUsuario.getCodUO());//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			String fecha = request.getParameter("fecha");

			AsistenciaDelegate ad = new AsistenciaDelegate();
			String res = ad.procesarCalificacion(pool_sp_g, fecha, codigo,
					usuario, seguridad);

			if (!res.equals(Constantes.OK)) {
				throw new Exception(res);
			}

			request.setAttribute("nomProceso", "PROCESO DE CALIFICACION DEL "
					+ fecha);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/resultadoProceso.jsp");
			dispatcher.forward(request, response);
			return;

		} catch (AsistenciaException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
					+ e.getMessage());
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * M�todo encargado de cargar los datos necesarios para el cierre de la
	 * asistencia
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param session
	 *            HttpSession
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarCierreAsistencia(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			MantenimientoDelegate md = new MantenimientoDelegate();
			ArrayList periodos = md.cargarPeriodos(pool_sp);

			session.removeAttribute("listaPeriodos");
			session.setAttribute("listaPeriodos", periodos);

			RequestDispatcher dispatcher = getServletContext()
//					.getRequestDispatcher("/cerrarAsistencia.jsp");
					.getRequestDispatcher("/ProcesoCierre.jsp");
			dispatcher.forward(request, response);
			return;

		} catch (MantenimientoException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
					+ e.getMessage());
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * M�todo encargado de ejecutar el proceso de cierre de asistencia
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param session
	 *            HttpSession
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cerrarAsistencia(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			//BeanUsuario bUsuario = (BeanUsuario) session.getAttribute("beanusuario");
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			String usuario = bUsuario.getLogin();
			//HashMap roles = super.getRoles(session);
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			log.debug("Roles "+roles);
			//String codigo = bUsuario.getNumreg();
			//String usuario = bUsuario.getLogin();
			//HashMap roles = super.getRoles(session);
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codigo);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("uoAO", bUsuario.getCodUO());//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			String criterio = request.getParameter("cmbCriterio");
			String valor = request.getParameter("txtValor");
			String periodo = request.getParameter("cmbPeriodo");

			//JRR
			String indMail = request.getParameter("chk_ind_mail")!= null ? Constantes.ACTIVO : Constantes.INACTIVO;
			seguridad.put("indMail", indMail);
			String ind_estado = (request.getParameter("ind_estado")!= null && request.getParameter("ind_estado").trim().equals("2")) ? "2" : "1";
			seguridad.put("ind_estado", ind_estado);

			AsistenciaDelegate ad = new AsistenciaDelegate();
			ad.cerrarAsistencia(pool_sp_g, periodo, criterio, valor,
					codigo, usuario, seguridad);

			request.setAttribute("nomProceso", "CIERRE DE ASISTENCIA");
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/resultadoProceso.jsp");
			dispatcher.forward(request, response);
			return;

		} catch (AsistenciaException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
					+ e.getMessage());
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * M�todo encargado de buscar el detalle de asistencia
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param session
	 *            HttpSession
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarAsistencias(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			
			//HashMap roles = super.getRoles(session);
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			log.debug("Roles "+roles);
			//BeanUsuario bUsuario = (BeanUsuario) session.getAttribute("beanusuario");
			String codPers = "";
			//HashMap roles = super.getRoles(session);
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			HashMap seguridad = null;

			String fecha = request.getParameter("fecha");
			String esPapeleta = request.getParameter("esPap");

			if (esPapeleta.trim().equals("0")) {
				codPers = request.getParameter("txtValor");
				seguridad = new HashMap();
				seguridad.put("roles", roles);
				seguridad.put("codPers",codigo);
				seguridad.put("uoSeg", uoSeg);
				seguridad.put("uoAO", bUsuario.getCodUO());//ICR 15/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO X PROBLEMAS EN CONSULTA POST IMPLANTACION
			} else {
				codPers = codigo;
				
			}

			AsistenciaDelegate ad = new AsistenciaDelegate();
			MantenimientoDelegate md = new MantenimientoDelegate();
			
			List listaAsistencias = null;//JVV-02/03/2012-DECRETO SUPREMO 004-2006
			ArrayList estadosPapeleta = md.cargarT99(pool_sp,Constantes.CODTAB_ESTADO_PAPELETA);

			UtilesDelegate ud = new UtilesDelegate();
			HashMap bPers = ud.buscarTrabajador(pool_sp, codPers, null);

			String fechaActual = Utiles.obtenerFechaActual();

			session.removeAttribute("listaAsistencias");
			session.removeAttribute("tiposPapeleta");
			session.removeAttribute("beanPersona");
			session.removeAttribute("estadosPapeleta");
			
			//JVV-02/03/2012-DECRETO SUPREMO 004-2006
			//DataSource dcsp = sl.getDataSource(pool_sp);
			//ParamDAO prmdao = new ParamDAO();
			//List listaFechaDS = (List)prmdao.cargarNoCache("select t99codigo, t99descrip from t99codigos where t99cod_tab='510' and t99codigo='09'", dcsp, prmdao.LIST);
			if( listaFechaDS.size() > 0 ) {
				ParamBean parmbean = (ParamBean)listaFechaDS.get(0);
				if(log.isDebugEnabled()) log.debug("t99cod_tab:"+parmbean.getCodigo()+", t99descrip:"+parmbean.getDescripcion());
				if(log.isDebugEnabled()) log.debug("fechaMarca:"+fecha);
				FechaBean fecha_ds = new FechaBean(parmbean.getDescripcion().trim());
				FechaBean fecha_marca = new FechaBean(fecha);				
				if(log.isDebugEnabled()) log.debug("fecha_ds:"+fecha_ds.getSQLDate());
				if(log.isDebugEnabled()) log.debug("fecha_marca:"+fecha_marca.getSQLDate());
				
				if (fecha_marca.getSQLDate().compareTo(fecha_ds.getSQLDate())>0 || fecha_marca.getSQLDate().compareTo(fecha_ds.getSQLDate())==0 ) {			
					
					listaAsistencias = ad.buscarAsistencias(pool_sp, codPers, fecha, seguridad);	
					
				} else listaAsistencias = null;
			}
			
			//FIN-JVV-02/03/2012-DECRETO SUPREMO 004-2006

			//List listaAsistencias = ad.buscarAsistencias(pool_sp, codPers, fecha, seguridad);

			String tipo = "";
			if (esPapeleta.equals("1")) {
				tipo = Constantes.CODTAB_TIPO_PAPELETA;
				
			} else {
				tipo = Constantes.CODTAB_TIPO_ASISTENCIA;
			}
			seguridad = new HashMap();
			seguridad.put("roles", roles);
			//ICAPUNAY - FORMATIVAS
			ArrayList tiposPapeleta=new ArrayList();
			if(bPers.get("t02cod_rel").equals(Constantes.CODREL_FORMATIVA)){ //FORMATIVA (10)
				tiposPapeleta = md.buscarTipoMovimientosFormativasPerfil(pool_sp, tipo,Constantes.ACTIVO, fechaActual, fechaActual, seguridad);
			}else{ //D.L.276-728 y D.L.1057 (CAS)
				tiposPapeleta = md.buscarTipoMovimientosPerfil(pool_sp, tipo,Constantes.ACTIVO, fechaActual, fechaActual, seguridad);
			}		
			//ArrayList tiposPapeleta = md.buscarTipoMovimientosPerfil(pool_sp, tipo,
				//	Constantes.ACTIVO, fechaActual, fechaActual, seguridad);
			//FIN ICAPUNAY - FORMATIVAS

			session.setAttribute("listaAsistencias", listaAsistencias);
			session.setAttribute("tiposPapeleta", tiposPapeleta);
			session.setAttribute("beanPersona", bPers);
			session.setAttribute("estadosPapeleta", estadosPapeleta);
			request.setAttribute("fecha", fecha);
			
			//dtarazona - papeleta de compensacion por hora
			HashMap datos = new HashMap();
			datos.put("cod_pers", codPers);
			SolicitudDelegate sd = new SolicitudDelegate();
			String saldole = sd.findLaborAutorizadaSaldo(datos).toString();
			session.removeAttribute("saldole");
			session.setAttribute("saldole", saldole);
			T1270DAO daoTurno = new T1270DAO();
			BeanTurnoTrabajo turno = null;
			//turno = daoTurno.joinWithT45ByCodFecha(pool_sp, codPers, Utiles.dateToString((Date)mAsis.get("fing")));
			//fin dtarazona

			String pagina = "";
			if (esPapeleta.equals("0")) pagina = "/registrarCalificaciones.jsp";
			else pagina = "/registrarPapeletas.jsp";
			
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher(pagina);
			dispatcher.forward(request, response);
			return;
			
		} catch (AsistenciaException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
					+ e.getMessage());
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * Metodo encargado de cargar los datos necesarios para el mantenimiento de
	 * papeletas
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param session
	 *            HttpSession
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarAsistencias(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		
		try {

			MantenimientoDelegate md = new MantenimientoDelegate();

			String fechaActual = Utiles.obtenerFechaActual();
			ArrayList tiposPapeleta = md.buscarTipoMovimientos(pool_sp,
					Constantes.CODTAB_TIPO_PAPELETA, Constantes.ACTIVO,
					fechaActual, fechaActual);

			session.removeAttribute("listaAsistencias");
			session.removeAttribute("tiposPapeleta");

			session.setAttribute("listaAsistencias", null);
			session.setAttribute("tiposPapeleta", tiposPapeleta);

			RequestDispatcher dispatcher = getServletContext()
			.getRequestDispatcher("/registrarPapeletas.jsp");
			dispatcher.forward(request, response);
			return;
			
		} catch (MantenimientoException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
					+ e.getMessage());
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * Metodo encargado de actualizar las calificaciones
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param session
	 *            HttpSession
	 * @throws ServletException
	 * @throws IOException
	 */
	private void actualizarCalificaciones(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		ArrayList mensajes = null;
		HashMap seguridad = null;
		BeanMensaje beanMsg = null;
		try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			log.debug("Roles "+roles);
			
			//BeanUsuario bUsuario = (BeanUsuario) session.getAttribute("beanusuario");
			String codPers = "";
			//HashMap roles = super.getRoles(session);
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			AsistenciaDelegate ad = new AsistenciaDelegate();
			MantenimientoDelegate md = new MantenimientoDelegate();
			
			ArrayList lista = (ArrayList) session.getAttribute("listaAsistencias");
			log.debug("lista: "+lista);
			HashMap beanPersona = (HashMap) session.getAttribute("beanPersona"); //ICAPUNAY - FORMATIVAS

			String[] params = new String[lista.size()];
			for (int i = 0; i < lista.size(); i++) {
				params[i] = request.getParameter("cmbTipo" + i);
				log.debug("params[i]: "+params[i]);
			}

			HashMap mAsis = null;
			String fecha = Utiles.obtenerFechaActual();
			if (lista.size() > 0) {
				mAsis = (HashMap) lista.get(0);
				log.debug("beanAsis");
				fecha = Utiles.dateToString((Date)mAsis.get("fing"));
				codPers = mAsis.get("cod_pers").toString();
			}

			/*ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII
			boolean periodoCerrado = md.periodoCerradoUOFecha(pool_sp,fecha,Utiles.obtenerFechaActual(),mAsis.get("u_organ").toString());
			if (periodoCerrado){
				throw new Exception("El periodo ya se encuentra cerrado para la UO ".concat(mAsis.get("u_organ").toString()).concat("."));
			}
			*/ //FIN ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII
			
			//ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII
			boolean periodoCerrado = md.periodoCerradoUOFecha_RegimenModalidad(pool_sp,fecha,Utiles.obtenerFechaActual(),mAsis.get("u_organ").toString(),(String)beanPersona.get("t02cod_rel"));
			if (periodoCerrado){
				throw new Exception("El periodo ya se encuentra cerrado para la UO ".concat(mAsis.get("u_organ").toString()).concat("."));
			}else{
				 log.debug("El periodo se encuentra abierto para la UO ".concat(mAsis.get("u_organ").toString()).concat("."));
			}
			//FIN ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII
			
			//ICAPUNAY - FORMATIVAS	
			log.debug("params: "+params);
			if(beanPersona.get("t02cod_rel").equals(Constantes.CODREL_FORMATIVA)){ //FORMATIVA (10)
				//ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII mensajes = ad.calificarAsistenciasFormativas(pool_sp_g, params, lista, codigo, fecha);
				mensajes = ad.calificarAsistenciasFormativas(pool_sp_g, params, lista, codigo, fecha,Constantes.CODREL_FORMATIVA);//ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII
			}else{ //D.L.276-728 y D.L.1057 (CAS)
				//ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII mensajes = ad.calificarAsistencias(pool_sp_g, params, lista, codigo, fecha);
				mensajes = ad.calificarAsistenciasCasPlanilla(pool_sp_g, params, lista, codigo, fecha,beanPersona.get("t02cod_rel").toString());
			}		
			//mensajes = ad.calificarAsistencias(pool_sp_g, params, lista, codigo, fecha);
			//FIN ICAPUNAY - FORMATIVAS
			
			seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codigo.trim());
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 15/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO X PROBLEMAS EN CONSULTA POST IMPLANTACION

			session.removeAttribute("listaAsistencias");

			if (!fecha.trim().equals("")) {
				List listaAsistencias = ad.buscarAsistencias(pool_sp, codPers, fecha, seguridad);
				session.setAttribute("listaAsistencias", listaAsistencias);
			}

			if (mensajes != null && !mensajes.isEmpty()) {
				beanMsg = new BeanMensaje();
				beanMsg.setListaobjetos(mensajes.toArray());
				beanMsg.setMensajesol("Por favor intente nuevamente.");
				session.setAttribute("beanErr", beanMsg);
				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/PagM.jsp");
				dispatcher.forward(request, response);
				return;
			} else {
				RequestDispatcher dispatcher = getServletContext()
				.getRequestDispatcher("/registrarCalificaciones.jsp");
				dispatcher.forward(request, response);
			}
			
		} catch (AsistenciaException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
					+ e.getMessage());
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * M�todo encargado de actualizar las calificaciones
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param session
	 *            HttpSession
	 * @throws ServletException
	 * @throws IOException
	 */
	private void actualizarPapeletas(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		ArrayList mensajes = null;
		HashMap seguridad = null;
		BeanMensaje beanMsg = null;
		try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			
			
			//HashMap roles = super.getRoles(session);
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			log.debug("Roles "+roles);
			
			//BeanUsuario bUsuario = (BeanUsuario) session.getAttribute("beanusuario");
			String codPers = "";
			//HashMap roles = super.getRoles(session);
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			AsistenciaDelegate ad = new AsistenciaDelegate();
			MantenimientoDelegate md = new MantenimientoDelegate();
			
			ArrayList lista = (ArrayList) session.getAttribute("listaAsistencias");

			String[] params = new String[lista.size()];
			String[] observs = new String[lista.size()];
			for (int i = 0; i < lista.size(); i++) {
				params[i] = request.getParameter("cmbTipo" + i);
				observs[i] = request.getParameter("txtObs" + i);
			}

			HashMap mAsis = null;
			String fecha = Utiles.obtenerFechaActual();
			if (lista.size() > 0) {
				mAsis = (HashMap) lista.get(0);
				fecha = Utiles.dateToString((Date)mAsis.get("fing"));
				codPers = mAsis.get("cod_pers").toString();
			}

			boolean periodoCerrado = md.periodoCerradoUOFecha(pool_sp,fecha,Utiles.obtenerFechaActual(),bUsuario.getCodUO());
			if (periodoCerrado){
				throw new Exception("El periodo ya se encuentra cerrado para la UO ".concat(mAsis.get("u_organ").toString()).concat("."));
			}
			
			HashMap jefe = ad.buscarTrabajadorJefe(pool_sp,codPers,null);
			//beanAsis.setUOrgan((String)jefe.get("t12cod_uorg_jefe"));
			mAsis.put("u_organ",(String)jefe.get("t12cod_uorg_jefe"));
			log.debug("Jefe : " + jefe);
			
			SolicitudDelegate sd = new SolicitudDelegate();
			ArrayList listaSuperv = 
				sd.buscarSupervisores(pool_sp, bUsuario.getNroRegistro(), "PP", mAsis.get("u_organ").toString());//beanAsis.getUOrgan());
			session.removeAttribute("listaSup");
			session.setAttribute("listaSup", listaSuperv);
			log.debug("Arreglo en Obtener Supervisores : "+ listaSuperv);
			log.debug("beanAsis : " + mAsis);
			log.debug("bUsuario : " + bUsuario.getNivelUO());

			
			if (listaSuperv != null && !listaSuperv.isEmpty()) {
				session.removeAttribute("paramspapsup");
				session.removeAttribute("listapapsup");
				session.removeAttribute("observspapsup");
				session.setAttribute("paramspapsup", params);
				session.setAttribute("listapapsup", lista);
				session.setAttribute("observspapsup", observs);
				RequestDispatcher dispatcher =
					getServletContext().getRequestDispatcher("/papSuperv.jsp");
				dispatcher.forward(request, response);
				return;
			}
			else {
				mensajes = ad.actualizarPapeletas(pool_sp_g, params, lista, bUsuario.getNroRegistro(), fecha, "-", observs);

				session.removeAttribute("listaAsistencias");

				if (!fecha.trim().equals("")) {
					List listaAsistencias = ad.buscarAsistencias(pool_sp, codPers, fecha, seguridad);
					session.setAttribute("listaAsistencias", listaAsistencias);
				}

				if (mensajes != null && !mensajes.isEmpty()) {
					beanMsg = new BeanMensaje();
					beanMsg.setListaobjetos(mensajes.toArray());
					beanMsg.setMensajesol("Por favor intente nuevamente.");
					session.setAttribute("beanErr", beanMsg);
					RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagM.jsp");
					dispatcher.forward(request, response);
					return;
				} else {
					RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/registrarPapeletas.jsp");
					dispatcher.forward(request, response);
				}
			}
			
		} catch (AsistenciaException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "+ e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}	
	
	/**
	 * Metodo encargado de cargar las papeletas autorizadas
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param session
	 *            HttpSession
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarAutPapeletas(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		
		try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			
			
			//HashMap roles = super.getRoles(session);
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			log.debug("Roles "+roles);
			
			//BeanUsuario bUsuario = (BeanUsuario) session.getAttribute("beanusuario");
			String codPers = bUsuario.getNroRegistro();//.getNumreg();
			String codUO = bUsuario.getCodUO();//.getCoduorg();
			//HashMap roles = super.getRoles(session);
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			if (uoSeg.equals("%")) {
				uoSeg = codUO.substring(0,1);
				}
			else {
				uoSeg = uoSeg.substring(0,2);
			}
			uoSeg= uoSeg.concat("%");
			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codPers);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("uoAO", bUsuario.getCodUO());//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
			seguridad.put("codUO", codUO);
			seguridad.put("codOpcion", Constantes.DELEGA_PAPELETAS);
			seguridad.put("dbpool", pool_sp);

			AsistenciaDelegate ad = new AsistenciaDelegate();
			MantenimientoDelegate md = new MantenimientoDelegate();
			
			//if ( (ad.esJefeEncargadoDelegado(seguridad)) || (ad.esSupervisor(pool_sp,codPers,"PP", codUO)))  { 
			if ( (ad.esJefeEncargadoDelegadoSolicitudes(seguridad)) || (ad.esSupervisor(pool_sp,codPers,"PP", codUO))|| ad.esDelegadoEncargadoPapeleta(seguridad))  { //WERR-PAS20155E230300132
				
				ArrayList estadosPapeleta = md.cargarT99(pool_sp, Constantes.CODTAB_ESTADO_PAPELETA);
				ArrayList tiposPapeleta = md.buscarTipoMovimientos(pool_sp, Constantes.CODTAB_TIPO_PAPELETA, Constantes.ACTIVO, "", "");
				
				List listaPapeletas = ad.buscarPapeletasGeneradas(pool_sp, codPers, seguridad);

				session.removeAttribute("listaPapeletas");
				session.setAttribute("listaPapeletas", listaPapeletas);

				session.removeAttribute("tiposPapeleta");
				session.setAttribute("tiposPapeleta", tiposPapeleta);				
				
				session.removeAttribute("estadosPapeleta");
				session.setAttribute("estadosPapeleta", estadosPapeleta);
				
				session.removeAttribute("beanPersona");				

				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/registrarAutorizacionPapeletas.jsp");
				dispatcher.forward(request, response);
				return;
				
			} else {
				throw new Exception("Usted no se encuentra habilitado para ejecutar esta opcion.");
			}			
			
		} catch (MantenimientoException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * Metodo encargado de buscar las papeletas
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param session
	 *            HttpSession
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarPapeletas(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		
		try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
						
			//HashMap roles = super.getRoles(session);
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			log.debug("Roles "+roles);
			//BeanUsuario bUsuario = (BeanUsuario) session.getAttribute("beanusuario");
			String codPers = bUsuario.getNroRegistro();//.getNumreg();
			String codUO = bUsuario.getCodUO();//.getCoduorg();
			//HashMap roles = super.getRoles(session);
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			String cmbEstado = request.getParameter("cmbEstado");
			String cmbCriterio = request.getParameter("cmbCriterio");
			String txtValor = request.getParameter("txtValor");
			
			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codPers);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("uoAO", bUsuario.getCodUO());//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
			seguridad.put("codUO", codUO);
			seguridad.put("codOpcion", Constantes.DELEGA_PAPELETAS);
			seguridad.put("dbpool", pool_sp);
			seguridad.put("cmbCriterio", cmbCriterio);
			seguridad.put("txtValor", txtValor);
			seguridad.put("cmbEstado", cmbEstado);

			AsistenciaDelegate ad = new AsistenciaDelegate();

			if ( (ad.esJefeEncargadoDelegado(seguridad)) || (ad.esSupervisor(pool_sp,codPers,"PP", codUO)) )  {

				String fechaIni = request.getParameter("fechaIni");
				String fechaFin = request.getParameter("fechaFin");

				String cod_jefe = codPers;

				List listaPapeletas = ad.buscarPapeletasSubordinados(
						pool_sp, cod_jefe, fechaIni, fechaFin, seguridad);

				session.removeAttribute("listaPapeletas");
				session.setAttribute("listaPapeletas", listaPapeletas);

				request.setAttribute("fechaIni", fechaIni);
				request.setAttribute("fechaFin", fechaFin);
				request.setAttribute("criterio", cmbCriterio);
				request.setAttribute("valor", txtValor);
				request.setAttribute("estado", cmbEstado);

				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher(
								"/registrarAutorizacionPapeletas.jsp");
				dispatcher.forward(request, response);
				
			} else {
				throw new Exception(
						"Usted no se encuentra habilitado para ejecutar esta opción.");
			}
			
		} catch (AsistenciaException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (MantenimientoException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (UtilesException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}
	}

	/**
	 * Metodo encargado de calificar las papeletas
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param session
	 *            HttpSession
	 * @throws ServletException
	 * @throws IOException
	 */
	private void calificarPapeletas(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		BeanMensaje beanMsg = null;
		try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			//BeanUsuario bUsuario = (BeanUsuario) session.getAttribute("beanusuario");
			//String codigo = bUsuario.getNumreg();
			String usuario = bUsuario.getLogin();

			ArrayList lista = (ArrayList) session.getAttribute("listaPapeletas");
			AsistenciaDelegate ad = new AsistenciaDelegate();

			String tipCalificacion = request.getParameter("tipCalificacion");
			log.debug("tipCalificacion : "+tipCalificacion);
			String[] params = new String[lista.size()];

			String valor = "";
			for (int i = 0; i < lista.size(); i++) {
				valor = request.getParameter("cmbTipo" + i);
				log.debug("cmbTipo" + i + " : "+valor);
				if (valor!=null){
					if (tipCalificacion.equals("1")) params[i] = Constantes.PAPELETA_ACEPTADA;
					else if (tipCalificacion.equals("2")) params[i] = Constantes.PAPELETA_RECHAZADA;
					else params[i] = valor;	
				}
			}
			
			ArrayList mensajes = ad.calificarPapeletas(pool_sp_g, params, lista, codigo, usuario);
			session.removeAttribute("listaPapeletas");

			if (mensajes != null && !mensajes.isEmpty()) {
				beanMsg = new BeanMensaje();
				beanMsg.setListaobjetos(mensajes.toArray());
				beanMsg.setMensajesol("Por favor intente nuevamente.");
				session.setAttribute("beanErr", beanMsg);
				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/PagM.jsp");
				dispatcher.forward(request, response);
				return;
			} else {
				this.cargarAutPapeletas(request, response, session);
				return;
			}
			
		} catch (AsistenciaException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
					+ e.getMessage());
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * Metodo encargado de cargar los datos necesarios para la calificaci�n de
	 * papeletas
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param session
	 *            HttpSession
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarCalificaciones(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		
		try {

			MantenimientoDelegate md = new MantenimientoDelegate();

			String fechaActual = Utiles.obtenerFechaActual();
			ArrayList tiposPapeleta = md.buscarTipoMovimientos(pool_sp, "",
					Constantes.ACTIVO, fechaActual, fechaActual);

			session.removeAttribute("listaAsistencias");
			session.removeAttribute("tiposPapeleta");
			session.removeAttribute("beanPersona");

			session.setAttribute("listaAsistencias", null);
			session.setAttribute("tiposPapeleta", tiposPapeleta);
			session.setAttribute("beanPersona", null);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/registrarCalificaciones.jsp");
			dispatcher.forward(request, response);
			return;
			
		} catch (MantenimientoException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
					+ e.getMessage());
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * Metodo encargado de cargar la asistencia resumen
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param session
	 *            HttpSession
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarAsistenciaResumen(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			MantenimientoDelegate md = new MantenimientoDelegate();
			ArrayList listaPeriodos = md.buscarPeriodosEstado(pool_sp,
					Constantes.ACTIVO);

			session.removeAttribute("listaAsistencias");
			session.removeAttribute("listaPeriodos");

			session.setAttribute("listaAsistencias", null);
			session.setAttribute("listaPeriodos", listaPeriodos);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/mantenimientoDevoluciones.jsp");
			dispatcher.forward(request, response);
			return;
		} catch (MantenimientoException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
					+ e.getMessage());
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * Metodo encargado de buscar la asistencia resumen
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param session
	 *            HttpSession
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarAsisResumen(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			String criterio = request.getParameter("cmbCriterio");
			String valor = request.getParameter("txtValor");
			String periodo = request.getParameter("cmbPeriodo");
			String periodoAsis = request.getParameter("cmbPeriodo1");

			MantenimientoDelegate md = new MantenimientoDelegate();
			AsistenciaDelegate ad = new AsistenciaDelegate();

			ArrayList listaPeriodos = md.buscarPeriodosEstado(pool_sp,
					Constantes.ACTIVO);
			ArrayList listaAsistencias = ad.buscarAsistenciasPeriodo(pool_sp,
					periodo, criterio, valor);

			session.removeAttribute("listaAsistencias");
			session.removeAttribute("listaPeriodos");

			session.setAttribute("listaAsistencias", listaAsistencias);
			session.setAttribute("listaPeriodos", listaPeriodos);
			setAttribute(session,"periodoAsis",periodoAsis);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/mantenimientoDevoluciones.jsp");
			dispatcher.forward(request, response);
			return;
		} catch (AsistenciaException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (MantenimientoException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
					+ e.getMessage());
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}
	}

	/**
	 * Metodo encargado de cargar los datos necesarios para el registro de
	 * devoluciones
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param session
	 *            HttpSession
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargaDatosDevolucion(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			String indice = request.getParameter("indice");
			ArrayList listaAsistencias = (ArrayList) session
					.getAttribute("listaAsistencias");
			BeanResumen bAsis = (BeanResumen) listaAsistencias
					.get(Integer.parseInt(indice));

			UtilesDelegate ud = new UtilesDelegate();
			HashMap beanPersona = ud.buscarTrabajador(pool_sp, bAsis
					.getCodPers(), null);

			AsistenciaDelegate ad = new AsistenciaDelegate();
			BeanDevolucion bDevol = ad.buscarDevoluciones(pool_sp, bAsis
					.getCodPers(), bAsis.getPeriodo(), bAsis.getMov());
			bDevol.setDescMovimiento(bAsis.getDescMovimiento());
			bDevol.setCodUO((String)beanPersona.get("t02cod_uorg"));
			
			session.removeAttribute("beanDevol");
			session.removeAttribute("beanPersona");
			session.removeAttribute("medida");

			session.setAttribute("beanDevol", bDevol);
			session.setAttribute("beanPersona", beanPersona);
			request.setAttribute("medida", bAsis.getMedida());

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/registrarDevolucion.jsp");
			dispatcher.forward(request, response);
			return;
		} catch (AsistenciaException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (UtilesException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
					+ e.getMessage());
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * Metodo encargado de grabar las devoluciones
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param session
	 *            HttpSession
	 * @throws ServletException
	 * @throws IOException
	 */
	private void grabarDevolucion(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			String codPers = request.getParameter("txtCodPers");
			String periodo = request.getParameter("txtPeriodo");
			String mov = request.getParameter("txtMov");
			String total = request.getParameter("txtTotal");
			String observacion = request.getParameter("txtObs");

			UsuarioBean user = (UsuarioBean) session.getAttribute("usuarioBean");
			
			//BeanUsuario user = (BeanUsuario) session.getAttribute("beanusuario");
			String usuario = user.getLogin();

			AsistenciaDelegate ad = new AsistenciaDelegate();
			Map devol = new HashMap();
			devol.put("codPers", codPers);
			devol.put("periodo", periodo);
			devol.put("mov", mov);
			devol.put("total", total);
			devol.put("observacion", observacion);
			devol.put("usuario", usuario);
			devol.put("dbpool", pool_sp_g);
			devol.put("periodoAsis", (String)session.getAttribute("periodoAsis"));
			
			ad.grabarDevolucion(devol);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/mantenimientoDevoluciones.jsp");
			dispatcher.forward(request, response);
			return;

		} catch (AsistenciaException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
					+ e.getMessage());
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarGenerarRegAsis(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/generarRegistroAsistencia.jsp");
			dispatcher.forward(request, response);
			return;
		} catch (AsistenciaException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
					+ e.getMessage());
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}
	}

	/**
	 * Metodo encargado de cargar la pantalla de busqueda correspondiente
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void registrarSupervisor( HttpServletRequest request, HttpServletResponse response, HttpSession session)
		throws ServletException, IOException {

		BeanMensaje beanMsg = null;
		ArrayList mensajes = null;
		HashMap seguridad = null;
		
		try {
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();//.getNumreg();
			//BeanUsuario bUsuario = (BeanUsuario) session.getAttribute("beanusuario");
			//String codPers = bUsuario.getNumreg();
			//String usuario = bUsuario.getLogin();
			String fecha = Utiles.obtenerFechaActual();

			String[] params = (String[]) session.getAttribute("paramspapsup");
			ArrayList lista = (ArrayList) session.getAttribute("listapapsup");
			String[] observs = (String[]) session.getAttribute("observspapsup");
			
			    //HashMap mapa = (HashMap) session.getAttribute("mapasol");
			    //String tipo = (String)mapa.get("tipo");
			    String supervisor = request.getParameter("registro");
			    //mapa.put("userDest", supervisor);
				AsistenciaDelegate ad = new AsistenciaDelegate();
		
				mensajes = ad.actualizarPapeletas(pool_sp_g, params, lista, bUsuario.getNroRegistro(), fecha, supervisor,observs);

				session.removeAttribute("listaAsistencias");

				if (!fecha.trim().equals("")) {
					List listaAsistencias = ad.buscarAsistencias(pool_sp, codPers, fecha, seguridad);
					session.setAttribute("listaAsistencias", listaAsistencias);
				}

				if (mensajes != null && !mensajes.isEmpty()) {
					beanMsg = new BeanMensaje();
					beanMsg.setListaobjetos(mensajes.toArray());
					beanMsg.setMensajesol("Por favor intente nuevamente.");
					session.setAttribute("beanErr", beanMsg);
					RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagM.jsp");
					dispatcher.forward(request, response);
					return;
				} else {
					RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/registrarPapeletas.jsp");
					dispatcher.forward(request, response);
				}

		}
		catch (AsistenciaException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
					+ e.getMessage());
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}


	}
	
	/**
	 * Metodo encargado de cargar los datos necesarios para el proceso 
	 * Unificado
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param session
	 *            HttpSession
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarProcesoUnificado(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			MantenimientoDelegate md = new MantenimientoDelegate();
			ArrayList periodos = md.cargarPeriodos(pool_sp);

			session.removeAttribute("listaPeriodos");
			session.setAttribute("listaPeriodos", periodos);

			//forward(request, response, "/cerrarAsistencia.jsp");
			RequestDispatcher dispatcher = getServletContext()
								.getRequestDispatcher("/ProcesoUnificado.jsp");
			dispatcher.forward(request, response);
			
			return;

		} catch (MantenimientoException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
					+ e.getMessage());
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}
	/**
	 * Metodo encargado de ejecutar el proceso de asistencia, Saldos Vacacionales y Labor Excepcional
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param session
	 *            HttpSession
	 * @throws ServletException
	 * @throws IOException
	 */
	private void procesarUnificado(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			
			//HashMap roles = super.getRoles(session);
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			if (log.isDebugEnabled()) log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			if (log.isDebugEnabled()) log.debug("Roles "+roles);
			
			//BeanUsuario bUsuario = (BeanUsuario) session.getAttribute("beanusuario");
			//String codigo = bUsuario.getNumreg();
			String usuario = bUsuario.getLogin();
			//HashMap roles = super.getRoles(session);
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codigo);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("uoAO", bUsuario.getCodUO());//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			String criterio = request.getParameter("cmbCriterio");
			
			//ASANCHEZZ 20100611
			//String valor = request.getParameter("txtValor");
			String valor =  (request.getParameter("txtValor")!= null )?request.getParameter("txtValor"):"";
			//FIN
			
			//JRR - 24/05/2011
			if(log.isDebugEnabled())log.debug("valor: " + valor);
			String codigazo = request.getParameter("cod_intendencia");
			if(log.isDebugEnabled())log.debug("codigazo: " + codigazo);
			
			if (valor != null && valor.trim().equals("")) {
				valor = codigazo;
			}
			//			
			
			String indPa = request.getParameter("chk_ind_pa")!= null ? Constantes.ACTIVO
					: Constantes.INACTIVO;
			String indGv = request.getParameter("chk_ind_sv")!= null ? Constantes.ACTIVO
					: Constantes.INACTIVO;
/*			String indHe = request.getParameter("chk_ind_le")!= null ? Constantes.ACTIVO
					: Constantes.INACTIVO;   */
			String res = "";
			
			//JRR - 31/03/2009
			String fecIni = request.getParameter("fechaIni");
			String fecFin = request.getParameter("fechaFin");
			
			Map mapa = new HashMap();
			mapa.put("dbpool", pool_sp_g);			
			mapa.put("criterio", criterio);
			mapa.put("valor", valor);
			mapa.put("fechaIni", fecIni);
			mapa.put("fechaFin", fecFin);
			mapa.put("codigo", codigo);
			mapa.put("usuario", usuario);
			mapa.put("seguridad", seguridad);
			//JRR - 2010 - Aca le pongo 1 porque no hay combo en jsp
			//mapa.put("regimen", "1");
			//		
			
			//JRR - 06/05/2011
			String regimen = request.getParameter("cmbRegimen");
			mapa.put("regimen", regimen);
			//
			
			//ASANCHEZZ 20100514 - PARA QUE SE ACTUALICE EL ESTADO DEL PROCESO Y GENERE EL ZIP CORRESPONDIENTE
			mapa.put("codPersT1481", codigo);
			//FIN

			if ("2".equals(criterio.trim()) && (roles.get(Constantes.ROL_ANALISTA_CENTRAL)==null)) {
					throw new Exception("Usted no se encuentra habilitado para ejecutar esta opción.");
			}
				
			//Generacion del Proceso de Asistencia
			if (indPa.equals(Constantes.ACTIVO)){
				String periodo = request.getParameter("cmbPeriodo");
				String indPap = request.getParameter("chk_ind_pap")!= null ? Constantes.ACTIVO
						: Constantes.INACTIVO;
				//JRR - 22/04/2010
				mapa.put("periodo", periodo);
				mapa.put("indPap", indPap);
				
				AsistenciaDelegate ad = new AsistenciaDelegate();
/*				res = ad.procesarAsistencia(pool_sp_g, periodo, criterio,
						valor, codigo, usuario, seguridad, indPap); */
				res = ad.procesarAsistencia(mapa);
			}

			//Generacion de Saldos Vacacionales
			if (indGv.equals(Constantes.ACTIVO)){
				String anno = request.getParameter("anno");
				//verificamos si el año es valido
				if (!Utiles.esAnnoValido(anno)) {
					throw new Exception("El año " + anno + " no es valido.");
				}
				
				//JRR - 06/05/2011
				mapa.put("anno", anno);
				
				VacacionDelegate vd = new VacacionDelegate();
/*				res = vd.generarVacaciones(pool_sp_g, anno, criterio, valor,
						codigo, usuario, seguridad); */
				res = vd.generarVacaciones(mapa);

			}

/*
			//Generacion de Labor Excepcional
			if (indHe.equals(Constantes.ACTIVO)){
				HoraExtraDelegate hed = new HoraExtraDelegate();
				//JRR - 31/03/2009
				res = hed.acumularHE(mapa);
				//res = hed.acumularHE(pool_sp_g, criterio, valor, codigo,
				//		usuario, seguridad); 
			}
*/
			if (!res.equals(Constantes.OK)) {
				throw new Exception(res);
			}

			request.setAttribute("nomProceso",
					"GENERACION DE PROCESO UNIFICADO");
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/resultadoProceso.jsp");
			dispatcher.forward(request, response);
			return;

		} catch (AsistenciaException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
					+ e.getMessage());
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}
	
	//ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
	/**
	 * Metodo encargado de cargar la bandeja de entrada de solicitudes recibidas
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarPermisoRefrigerio(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();
			String codUO = bUsuario.getCodUO();
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); 
			log.debug("roles: "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			log.debug("roles2: "+roles);
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");
			log.debug("uoSeg: "+uoSeg);

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codPers.trim());
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("codUO", codUO);
			seguridad.put("codOpcion", Constantes.DELEGA_SOLICITUDES); //delegado todo (1) o solicitudes (2)
			seguridad.put("dbpool", pool_sp);

			AsistenciaDelegate ad = new AsistenciaDelegate();
					
			ArrayList unidades = new ArrayList();
			ArrayList unidades1 = new ArrayList();//ICAPUNAY - PAS20171U230300040 - Refrigerio Clima Laboral
			ArrayList unidades2 = new ArrayList();
			ArrayList unidadesFinal = new ArrayList();
			ArrayList subunidades = new ArrayList();
			ArrayList subunidades2 = new ArrayList();
			ArrayList subunidades3 = new ArrayList();
			
			String codUnidad ="";
			String codUnidadSC ="";
			String nivel ="";
			String codUnidad2 ="";
			String codUnidadSC2 ="";
						
			if ((ad.esJefeEncargadoDelegadoSolicitudes(seguridad)))  { 
				
				unidades = ad.buscarUnidades(pool_sp,codPers.trim()); //busca unidades de niveles 0,1,2,3,4,5 para jefe/delegado //ICAPUNAY - PAS20171U230300040 - Refrigerio Clima Laboral
				//unidades = ad.buscarUnidades(pool_sp,codPers.trim()); //busca de niveles 0,1,2,3,4 //ICAPUNAY - PAS20171U230300040 - Refrigerio Clima Laboral
				log.debug("unidades: "+unidades);			
				
				if (unidades!=null && unidades.size()>0){
					for (int i = 0; i < unidades.size(); i++) {
						HashMap unidad = (HashMap)unidades.get(i);
						log.debug("unidad(" + i + "): "+unidad);
						unidadesFinal.add(unidad);
						/* ICAPUNAY - PAS20171U230300040 - Refrigerio Clima Laboral
						codUnidad = (String)unidad.get("coduo");
						nivel = unidad.get("nivel").toString();
						codUnidadSC=findUuooJefe(codUnidad);
						log.debug("codUnidadSC(" + i + "): "+codUnidadSC);						
						if (nivel.equals(Constantes.NIVEL4)){ //si la unidad es de nivel 4 se debe adicionar las unidades de nivel 5 y 6
							subunidades2 = ad.buscarSubUnidades(pool_sp,codUnidadSC,codUnidad,Constantes.NIVEL5);
							log.debug("subunidades2: "+subunidades2);
							if (subunidades2!=null && subunidades2.size()>0){								
								for (int b = 0; b < subunidades2.size(); b++){
									HashMap subunidad2 = (HashMap)subunidades2.get(b);
									log.debug("subunidad2(" + b + "): "+subunidad2);
									unidadesFinal.add(subunidad2);
								}
								log.debug("unidadesFinal2: "+unidadesFinal);
							}							
							subunidades3 = ad.buscarSubUnidades(pool_sp,codUnidadSC,codUnidad,Constantes.NIVEL6);
							log.debug("subunidades3: "+subunidades3);
							if (subunidades3!=null && subunidades3.size()>0){									
								for (int c = 0; c < subunidades3.size(); c++) {
									HashMap subunidad3 = (HashMap)subunidades3.get(c);
									log.debug("subunidad3(" + c + "): "+subunidad3);
									unidadesFinal.add(subunidad3);
								}
								log.debug("unidadesFinal3: "+unidadesFinal);
							}													
						}*/						
					}//fin for
					log.debug("unidades parte1: "+unidadesFinal);					
				}//fin if					
				
				/* ICAPUNAY - PAS20171U230300040 - Refrigerio Clima Laboral
				unidades2 = ad.buscarUnidadesNoDependientes(pool_sp,codPers.trim(),Constantes.NIVEL5,Constantes.NIVEL4); //busca de niveles 5 (no dependientes de nivel4) 
				log.debug("unidades2: "+unidades2);				
				if (unidades2!=null && unidades2.size()>0){
					for (int i = 0; i < unidades2.size(); i++) {
						HashMap unidad2 = (HashMap)unidades2.get(i);
						log.debug("unidad2(" + i + "): "+unidad2);
						codUnidad2 = (String)unidad2.get("coduo");						
						codUnidadSC2=findUuooJefe(codUnidad2);
						log.debug("codUnidadSC2(" + i + "): "+codUnidadSC2);
						unidadesFinal.add(unidad2);
						
						subunidades = ad.buscarSubUnidades(pool_sp,codUnidadSC2,codUnidad2,Constantes.NIVEL6); //se debe adicionar las unidades de nivel 6
						log.debug("subunidades: "+subunidades);
						if (subunidades!=null && subunidades.size()>0){
							for (int a = 0; a < subunidades.size(); a++) {
								HashMap subunidad = (HashMap)subunidades.get(a);
								log.debug("subunidad(" + a + "): "+subunidad);
								unidadesFinal.add(subunidad);
							}								
						}
						log.debug("unidadesFinal: "+unidadesFinal);
								
					}//fin for
					log.debug("unidades parte2: "+unidadesFinal);					
				}//fin if*/
				
				
				unidades1 = ad.buscarUnidadesByNivelByReporte(pool_sp,Constantes.NIVEL6,codPers.trim()); //busca unidades niveles 6 que reportan al jefe //ICAPUNAY - PAS20171U230300040 - Refrigerio Clima Laboral
				log.debug("unidades1: "+unidades1);			
				
				if (unidades1!=null && unidades1.size()>0){
					for (int j = 0; j < unidades1.size(); j++) {
						HashMap unidad1 = (HashMap)unidades1.get(j);
						log.debug("unidad1(" + j + "): "+unidad1);
						unidadesFinal.add(unidad1);						
					}//fin for
					log.debug("unidades1 parte: "+unidadesFinal);					
				}//fin if					
				
				if (unidadesFinal!=null && unidadesFinal.size()>0){
					Ordenamiento.sort(unidadesFinal, "coduo"+Ordenamiento.SEPARATOR+Ordenamiento.ASC);//ordenando el resultado ascendentemente por el campo coduo
					log.debug("unidadesFinal ordenadas: "+unidadesFinal);
					
					session.removeAttribute("listaUnidades");
					session.setAttribute("listaUnidades", unidadesFinal);
					session.setAttribute("fechaMinimaClima",Utiles.obtenerFechaMinimaClimaLaboral(pool_sp));	

					RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/registrarPermisoRefrigerio.jsp");
					dispatcher.forward(request, response);
					return;
					
				}else{
					throw new Exception("Usted no se encuentra habilitado para ejecutar esta opci�n.");
				}				
			}
			else {
				throw new Exception("Usted no se encuentra habilitado para ejecutar esta opci�n.");
			}


		} catch (MantenimientoException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (SolicitudException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}
	}
	
	
	/**
	 * Metodo encargado de buscar colaboradores (DL 1057 y DL 276-728) que no se encuentre de licencia o vacaciones o tengan registrada actividad clima laboral
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarColaboradoresClima(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		
		try {
		    String coduo = request.getParameter("cmbUnidad");
		    String fechaEvento = request.getParameter("fechaEvento");
            log.debug("coduo: "+coduo);
            log.debug("fechaEvento: "+fechaEvento); //dd/mm/yyyy           
            String fechaEventoSgte = new FechaBean().getOtraFecha(fechaEvento, "dd/MM/yyyy", +1, Calendar.MONTH );//fecha evento mas 1 mes
            log.debug("fechaEventoSgte2: "+fechaEventoSgte); //dd/mm/yyyy  
			AsistenciaDelegate ad = new AsistenciaDelegate();
			MantenimientoDelegate md = new MantenimientoDelegate();			
			String periodoPlanilla=(fechaEvento.trim().substring(6,10)).concat(fechaEvento.trim().substring(3,5));			
			String periodoCas=(fechaEventoSgte.trim().substring(6,10)).concat(fechaEventoSgte.trim().substring(3,5));
			log.debug("periodoPlanilla2: "+periodoPlanilla);
			log.debug("periodoCas2: "+periodoCas);
			
			BeanPeriodo existeP = md.buscarPeriodoByCodigoByEstado(pool_sp,periodoPlanilla,Constantes.ACTIVO);
			BeanPeriodo existeC = md.buscarPeriodoByCodigoByEstado(pool_sp,periodoCas,Constantes.ACTIVO);
			log.debug("existeP: "+existeP);
			log.debug("existeC: "+existeC);
			
			if (existeP.getFechaCie()==null || existeP.getFechaCie().trim()=="" || existeC.getFechaCieCAS()==null || existeC.getFechaCieCAS().trim()==""){
				throw new Exception("El periodo no existe, no es posible registrar la actividad");
			}else{
				boolean periodoCerradoP = md.periodoCerradoAFecha(pool_sp,periodoPlanilla,Utiles.obtenerFechaActual());
				boolean periodoCerradoC = md.periodoCerradoUOFecha_RegimenModalidad(pool_sp,fechaEvento,Utiles.obtenerFechaActual(),coduo,"09");
				log.debug("periodoCerradoPLA: "+periodoCerradoP);
				log.debug("periodoCerradoCAS: "+periodoCerradoC);	
				if (periodoCerradoP || periodoCerradoC){
					throw new Exception("La fecha corresponde a un periodo cerrado, no es posible registrar la actividad");
				}
			}	
			
			Map datos= new HashMap();
			datos.put("coduo", coduo.trim());
			datos.put("fechaEvento", fechaEvento);
			List listaOrigen = ad.findColaboradoresSinClimaLicenciaVacacionesByUOByFecha(pool_sp,datos); //personal disponible del area
			log.debug("listaOrigen: "+listaOrigen);
						
			request.setAttribute("fechaEve", fechaEvento);
			request.setAttribute("codUnidad", coduo.trim());
			session.removeAttribute("listaOrigen");					
			session.setAttribute("listaOrigen", listaOrigen);

			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/registrarPermisoRefrigerio.jsp");
			dispatcher.forward(request, response);
			return;
		} catch (MantenimientoException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void registrarPermisoRefrigerio(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			log.debug("ingreso registrarPermisoRefrigerio");
			UsuarioBean bUsuario = (UsuarioBean)session.getAttribute("usuarioBean");
			ArrayList listaUnidades = (ArrayList) session.getAttribute("listaUnidades");
			
			String coduo = request.getParameter("cmbUnidad");
		    String fechaEvento = request.getParameter("fechaEvento");
//			String coduo = (String) session.getAttribute("codUnidad");
//		    String fechaEvento = (String) session.getAttribute("fechaEve");
		    String anio = new FechaBean(fechaEvento).getAnho();
            log.debug("coduo2: "+coduo);
            log.debug("fechaEvento2: "+fechaEvento);
            log.debug("anio: "+anio);
            
            String colaboradoresConcatenados = request.getParameter("colaboradores");
			log.debug("colaboradoresConcatenados: "+colaboradoresConcatenados);
			
			HashMap permiso = new HashMap();
			AsistenciaDelegate ad = new AsistenciaDelegate();
			
			permiso.put("anno",anio ); 
			permiso.put("fec_aut", new FechaBean(fechaEvento).getSQLDate()); 
			permiso.put("cod_uorga", coduo.trim()); 
			permiso.put("cod_aut", bUsuario.getNroRegistro().trim()); 
			permiso.put("ind_aut", Constantes.INACTIVO); //0
			permiso.put("cod_user_crea",bUsuario.getLogin());
			permiso.put("fec_creacion",  new FechaBean().getTimestamp());
			permiso.put("colaboradores", colaboradoresConcatenados);
						
			ad.registrarPermisoRefrigerio(pool_sp_g,permiso);
			
			Map datos= new HashMap();
			datos.put("coduo", coduo.trim());
			datos.put("fechaEvento", fechaEvento);
			List listaOrigen = ad.findColaboradoresSinClimaLicenciaVacacionesByUOByFecha(pool_sp,datos); //personal disponible del area
			log.debug("listaOrigen2: "+listaOrigen);						
			session.removeAttribute("listaOrigen");					
			session.setAttribute("listaOrigen", listaOrigen);
			
			session.removeAttribute("listaUnidades");
			session.setAttribute("listaUnidades", listaUnidades);
			
			request.setAttribute("fechaEve", fechaEvento);
			request.setAttribute("codUnidad", coduo.trim());
			
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/registrarPermisoRefrigerio.jsp");
			dispatcher.forward(request, response);	
			return;

		} catch (MantenimientoException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}
	}
	
	/** 
	 * Devuelve una unidad sin ceros a la derecha
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
	//FIN //ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
	
	/*
	 * Metodo que se encarga de convertir los ROles de HashMap a ArrayList
	 * @param roles
	 */
	public static HashMap obtRoles(ArrayList roles)

	throws ServletException, IOException {

	HashMap res = new HashMap();

	if (roles!=null && roles.size()>0){

	String tmpRol;

	for (int i = 0; i < roles.size(); i++){

		tmpRol = (String) roles.get(i);
		res.put(tmpRol,tmpRol);
	
	}

	}

	return res;

	}
		
//JRR	
	/**
	 * Metodo encargado de cargar los objetos a memoria
	 * 
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarParametros() throws ServletException, IOException {
		try {
			dsSP = ServiceLocator.getInstance().getDataSource(pool_sp);
			ParamDAO paramDAO = new ParamDAO();
			
			//SP
			//Intendencias
			String query = "select t12cod_uorga, t12des_uorga from t12uorga where " +
			"t12ind_estad = '1' and ( " +		
			"(t12cod_uorga like '1%0000' and t12cod_uorga != '100000') or " + 
		    "(t12cod_uorga like '2%0000' and t12cod_uorga != '200000') or " + 
		    //ICAPUNAY 09/02/2012 PAS20124E550000022 listado de nuevas intendencias de la uo: 400000
			//"(t12cod_uorga like '3%0000' and t12cod_uorga != '300000') )";
		    "(t12cod_uorga like '3%0000' and t12cod_uorga != '300000') or " +
		    "(t12cod_uorga like '4%0000' and t12cod_uorga != '400000') or " + 
		    //ICAPUNAY-PAS20144EB20000106-Cambio ROF 2014
		    "(t12cod_uorga like '5%0000' and t12cod_uorga != '500000') or " + 
		    "(t12cod_uorga like '6%0000' and t12cod_uorga != '600000') or " +	
		    "(t12cod_uorga like '7%0000' and t12cod_uorga != '700000') or " +	//ICAPUNAY - PAS20175E230300114 - Cambio Rof 2017
		    "(t12cod_uorga like '8%0000' and t12cod_uorga != '800000') )";
			//ICAPUNAY-PAS20144EB20000106-Cambio ROF 2014
			
			
			paramDAO.cargar(query, pool_sp, "spprmInten");
			
			listaFechaDS = (List)paramDAO.cargarNoCache("select t99codigo, t99descrip from t99codigos where t99cod_tab='510' and t99codigo='09'", dsSP, paramDAO.LIST);//JVV-02/03/2012-DECRETO SUPREMO 004-2006
			
		} catch (Exception e) {
			log.error("error al cargar Cache:"+e);
		}
	}	
	
	
}