package pe.gob.sunat.sp.asistencia;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.log4j.NDC;

import pe.gob.sunat.framework.core.servlet.ServletAbstract;
import pe.gob.sunat.sol.BeanMensaje;
//import pe.gob.sunat.sol.seguridad.acceso.jaas.AutenticacionException;
//import pe.gob.sunat.sol.seguridad.acceso.jaas.ServletAutenticacionIntranet;
//import pe.gob.sunat.sol.seguridad.acceso.portal.bean.BeanUsuario;
//import pe.gob.sunat.sp.asistencia.bean.BeanVacacion;
import pe.gob.sunat.sp.asistencia.ejb.delegate.AsistenciaDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.MantenimientoDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.MantenimientoException;
import pe.gob.sunat.sp.asistencia.ejb.delegate.UtilesDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.UtilesException;
import pe.gob.sunat.sp.asistencia.ejb.delegate.VacacionDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.VacacionException;
import pe.gob.sunat.tecnologia.menu.bean.UsuarioBean;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;
import pe.gob.sunat.sp.bean.BeanT99;//ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII
import pe.gob.sunat.framework.util.Propiedades;//ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII

/**
 * @web.servlet name="ServletVacacion"
 * @web.servlet-mapping url-pattern = "/asisS09Alias"
 * @web.servlet-init-param name = "pool_sp" value = "jdbc/dcsp"
 * @web.servlet-init-param name = "pool_sp_g" value = "jdbc/dgsp"
 *
 * Title: Control de Asistencia
 * Description:
 * Copyright: Copyright (c) 2004
 * Company: Sunat
 * @author cgarratt
 * @version 1.0
 */
public class ServletVacacion extends ServletAbstract {

	private static final Logger log = Logger.getLogger(ServletVacacion.class);
	private static String pool_sp;
	private static String pool_sp_g;
	
	Propiedades constantes = new Propiedades(getClass(), "/constantes.properties");//ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII

	public ServletVacacion() {
	}

	public void destroy() {
		super.destroy();
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
        pool_sp = config.getInitParameter("pool_sp");
        pool_sp_g = config.getInitParameter("pool_sp_g");
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
		if (bUsuario!=null)
			NDC.push(bUsuario.getLogin().concat("-").concat(bUsuario.getTicket()));		
		log.info("Accion : " + accion);

	
		//Cargado del HashMap
		cargarCantDiasVacaciones(request, response, session);

		if (accion.equals("cargarGenerarVacaciones")) {
			cargarGenerarVacaciones(request, response, session);
		}
		if (accion.equals("generarVacaciones")) {
			generarVacaciones(request, response, session);
		}
		if (accion.equals("cargarFirmarLibro")) {
			cargarFirmarLibro(request, response, session);
		}
		if (accion.equals("firmarLibro")) {
			firmarLibro(request, response, session);
		}
		if (accion.equals("cargarVentaVacaciones")) {
			cargarVentaVacaciones(request, response, session);
		}
		if (accion.equals("registrarVentaVacacion")) {
			registrarVentaVacacion(request, response, session);
		}

		if (accion.equals("cargarVacacionesAnalista")) {
			session.removeAttribute("tipoVac");
			session.setAttribute("tipoVac", Constantes.VACACION);
			cargarVacaciones(request, response, session);
		}

		if (accion.equals("cargarVacacionesEspeciales")) {
			session.removeAttribute("tipoVac");
			session.setAttribute("tipoVac", Constantes.VACACION_ESPECIAL);

			cargarVacaciones(request, response, session);
		}

		if (accion.equals("cargarSuspencionVacaciones")) {
			cargarSuspencionVacaciones(request, response, session);
		}

		if (accion.equals("buscarVacaciones")) {
			buscarVacaciones(request, response, session);
		}

		if (accion.equals("buscarVacacionesProgramadas")) {
			buscarVacacionesProgramadas(request, response, session);
		}

		if (accion.equals("buscarVacXAnoAnalista")) {
			buscarVacXAnoAnalista(request, response, session);
		}

		if (accion.equals("buscarVacacionesXSuspender")) {
			buscarVacacionesXSuspender(request, response, session);
		}
		
		if (accion.equals("buscarVentasVacaciones")) {
			buscarVentasVacaciones(request, response, session);
		}		

		if (accion.equals("buscarVacXAnoXSuspender")) {
			buscarVacXAnoXSuspender(request, response, session);
		}

		if (accion.equals("cargarNuevaVacacion")) {
			cargarNuevaVacacion(request, response, session);
		}

		if (accion.equals("cargarDatosVacacion")) {
			cargarDatosVacacion(request, response, session);
		}

		if (accion.equals("registrarVacacion")) {
			registrarVacacion(request, response, session);
		}

		if (accion.equals("modificarVacacion")) {
			modificarVacacion(request, response, session);
		}

		if (accion.equals("cargarSuspVacaciones")) {
			cargarSuspVacaciones(request, response, session);
		}

		if (accion.equals("suspenderVacacion")) {
			suspenderVacacion(request, response, session);
		}

		if (accion.equals("cargarVacacionesProgramadas")) {
			cargarVacacionesProgramadas(request, response, session);
		}

		if (accion.equals("cargarNuevaVacacionProgramada")) {
			cargarNuevaVacacionProgramada(request, response, session);
		}

		if (accion.equals("cargarDatosVacacionProgramada")) {
			cargarDatosVacacionProgramada(request, response, session);
		}

		if (accion.equals("buscarVacXAnoProgramadas")) {
			buscarVacXAnoProgramadas(request, response, session);
		}

		if (accion.equals("registrarVacacionProgramada")) {
			registrarVacacionProgramada(request, response, session);
		}

		if (accion.equals("modificarVacacionProgramada")) {
			modificarVacacionProgramada(request, response, session);
		}

		if (accion.equals("eliminarVacacionProgramada")) {
			eliminarVacacionProgramada(request, response, session);
		}

		if (accion.equals("cargarVacacionGen")) {
			cargarVacacionGen(request, response, session);
		}

		if (accion.equals("buscarVacacionGen")) {
			buscarVacacionGen(request, response, session);
		}

		if (accion.equals("registrarVacacionGen")) {
			registrarVacacionGen(request, response, session);
		}

		if (accion.equals("cargarAutProgramacion")) {
			cargarAutProgramacion(request, response, session);
		}

		if (accion.equals("buscarVacacionesProg")) {
			buscarVacacionesProg(request, response, session);
		}

		if (accion.equals("actualizarAutorizacion")) {
			actualizarAutorizacion(request, response, session);
		}
		
		if (accion.equals("eliminarVentaVacaciones")) {
			eliminarVentaVacaciones(request, response, session);
		}		
		
}
catch(Exception e){
	  log.error("*** Error ***", e);
	  
} finally{
	  NDC.pop();
	  NDC.remove();
}
	}

	/**
	 * Metodo encargado de cargar la pagina para la generacion de vacaciones
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarGenerarVacaciones(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/generarVacaciones.jsp");
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
	 * Metodo encargado de generar las vacaciones
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void generarVacaciones(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			String usuario = bUsuario.getLogin();
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
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			String criterio = request.getParameter("cmbCriterio");
			String valor = request.getParameter("txtValor");
			String anno = request.getParameter("anno");

			//verificamos si el año es valido
			if (!Utiles.esAnnoValido(anno)) {
				throw new Exception("El año " + anno + " no es valido.");
			}

			VacacionDelegate vd = new VacacionDelegate();
/*			String res = vd.generarVacaciones(pool_sp_g, anno, criterio, valor,
					codigo, usuario, seguridad);  */

			//JRR - 11/05/2011
			Map mapa = new HashMap();
			mapa.put("dbpool", pool_sp_g);
			mapa.put("anno", anno);
			mapa.put("criterio", criterio);
			mapa.put("valor", valor);
			mapa.put("codigo", codigo);
			mapa.put("usuario", usuario);
			mapa.put("seguridad", seguridad);
			
			String res = vd.generarVacaciones(mapa);
			//
			
			if (!res.equals(Constantes.OK)) {
				throw new Exception(res);
			}

			request.setAttribute("nomProceso",
					"GENERACI&Oacute;N DE VACACIONES");
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/resultadoProceso.jsp");
			dispatcher.forward(request, response);
			return;

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
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * Metodo encargado de cargar la pagina para la firma del libro de
	 * vacaciones
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarFirmarLibro(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean user = (UsuarioBean) session.getAttribute("usuarioBean");


			VacacionDelegate vd = new VacacionDelegate();
			List detalleVacacion = vd.buscarVacacionesPorFirmar(pool_sp,
					user.getNroRegistro(), "", Constantes.INACTIVO);

			session.removeAttribute("detalleVacacion");
			session.setAttribute("detalleVacacion", detalleVacacion);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/firmarLibro.jsp");
			dispatcher.forward(request, response);
			return;

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
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * Metodo encargado de firmar el libro de vacaciones
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void firmarLibro(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean user = (UsuarioBean) session.getAttribute("usuarioBean");


			VacacionDelegate vd = new VacacionDelegate();
			String[] params = request.getParameterValues("chk_opcion");

			ArrayList detalles = (ArrayList) session
					.getAttribute("detalleVacacion");
			String res = vd.firmarLibro(pool_sp_g , user.getNroRegistro(), params, detalles,
					user.getLogin());

			if (!res.equals(Constantes.OK)) {
				throw new Exception(res);
			}

			List detalleVacacion = vd.buscarVacacionesPorFirmar(pool_sp,
					user.getNroRegistro(), "", Constantes.INACTIVO);
			session.removeAttribute("detalleVacacion");
			session.setAttribute("detalleVacacion", detalleVacacion);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/firmarLibro.jsp");
			dispatcher.forward(request, response);
			return;

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
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * Metodo encargado de cargar la pagina de venta de vacaciones
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarVentaVacaciones(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			session.removeAttribute("listaCabeceras");
			session.removeAttribute("listaVentas");
			session.removeAttribute("beanPersona");

			session.setAttribute("fechaIncorporado","");
			session.setAttribute("datosTrabajador",null);
			
			
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/venderVacaciones.jsp");
			dispatcher.forward(request, response);
			return;

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
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * Metodo encargado de registrar la venta de vacaciones
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void registrarVentaVacacion(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codUsuario = bUsuario.getNroRegistro();//.getNumreg();
			String usuario = bUsuario.getLogin();
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			log.debug("Roles "+roles);
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codUsuario);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			UtilesDelegate ud = new UtilesDelegate();
			VacacionDelegate vd = new VacacionDelegate();
			
			String codPers = request.getParameter("txtValor");
			String anho = request.getParameter("txtAno");
			String dias = request.getParameter("txtDias");
			String fechaIni = request.getParameter("fechaIni");
			String obs=request.getParameter("txtObservacion");

			HashMap bPers = ud.buscarTrabajador(pool_sp, codPers, seguridad);
			
			if (bPers.get("t02cod_pers") != null && !((String)bPers.get("t02cod_pers")).equals("")){
				
				String res = vd.venderVacaciones(pool_sp_g, codPers, anho, dias, usuario, "", "Vacaciones", fechaIni,obs);

				if (!res.equals(Constantes.OK)) {
					throw new Exception(res);
				}			
			}			

			cargarVentaVacaciones(request, response, session);

		} catch (VacacionException e) {
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
	 * Metodo encargado de cargar las vacaciones programadas
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarVacaciones(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			session.removeAttribute("listaCabeceras");
			session.removeAttribute("listaProgramadas");
			session.removeAttribute("listaEfectivas");
			session.removeAttribute("beanPersona");

			session.setAttribute("listaCabeceras", null);
			session.setAttribute("listaProgramadas", null);
			session.setAttribute("listaEfectivas", null);
			session.setAttribute("beanPersona", null);
			session.setAttribute("fechaIncorporado", "");
			session.setAttribute("datosTrabajador", null);
			
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/programarVacacionesAnalista.jsp");
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
	 * Metodo encargado de cargar las vacaciones programadas del trabajador
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarVacacionesProgramadas(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			session.removeAttribute("listaCabeceras");
			session.removeAttribute("listaProgramadas");
			session.removeAttribute("listaEfectivas");
			session.removeAttribute("beanPersona");

			session.setAttribute("listaCabeceras", null);
			session.setAttribute("listaProgramadas", null);
			session.setAttribute("listaEfectivas", null);
			session.setAttribute("beanPersona", null);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/programarVacacionesTrabajador.jsp");
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
	 * Metodo encargado de buscar las vacaciones programadas
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarVacacionesProgramadas(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			VacacionDelegate vd = new VacacionDelegate();

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();//.getNumreg();

			ArrayList listaCabeceras = vd.buscarSaldoAnno(pool_sp, codPers,
					false, "", null);
			List listaProgramadas = vd.buscarDetalleVacaciones(pool_sp,
					codPers, "", Constantes.VACACION_PROGRAMADA, "", null);
			List listaEfectivas = vd.buscarDetalleVacaciones(pool_sp,
					codPers, "", "", "", null);
			List listaVendidas = vd.buscarDetalleVacaciones(pool_sp,
					codPers, "", Constantes.VACACION_VENTA, "", null);

			session.removeAttribute("listaCabeceras");
			session.removeAttribute("listaProgramadas");
			session.removeAttribute("listaEfectivas");
			session.removeAttribute("listaVendidas");

			session.setAttribute("listaSaldos", listaCabeceras);
			session.setAttribute("listaProgramadas", listaProgramadas);
			session.setAttribute("listaEfectivas", listaEfectivas);
			session.setAttribute("listaVendidas", listaVendidas);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/programarVacacionesTrabajador.jsp");
			dispatcher.forward(request, response);
			return;
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
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * Metodo encargado de buscar las vacaciones
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarVacaciones(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			//Parte de seguridad
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codUsuario = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			log.debug("Roles "+roles);
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codUsuario);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			VacacionDelegate vd = new VacacionDelegate();

			String codPers = request.getParameter("txtValor");

			UtilesDelegate ud = new UtilesDelegate();
			HashMap paramSaldos = new HashMap();
			HashMap paramVac = new HashMap();

			//HashMap bPers = ud.buscarTrabajador(pool_sp, codPers, seguridad);//ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII
			HashMap bPers = ud.buscarTrabajador(pool_sp, codPers.toUpperCase(), seguridad);//ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII- Se convierte a mayuscula para mostrar el resultado correcto en el jsp
			
			paramSaldos.put("codPers", codPers);
			paramSaldos.put("saldoFavor", "false");
			paramSaldos.put("anho", "");
			paramSaldos.put("dbpool", pool_sp);
			paramSaldos.put("seguridad", seguridad);

			ArrayList listaCabeceras = vd.buscarSaldos(paramSaldos);//este si devuelve listado de beanvacacion

			paramVac.put("codPers", codPers);
			paramVac.put("estado", "");
			paramVac.put("licencia", Constantes.VACACION_PROGRAMADA);
			paramVac.put("anho", "");
			paramVac.put("dbpool", pool_sp);
			paramVac.put("seguridad", seguridad);
			List listaProgramadas = vd.buscarVacacionesXLicencia(paramVac);//este si devuelve arreglo de hashmap

			paramVac.put("licencia", "");
			List listaEfectivas = vd.buscarVacacionesXLicencia(paramVac);

			paramVac.put("licencia", Constantes.VACACION_VENTA);
			List listaVendidas = vd.buscarVacacionesXLicencia(paramVac);

			session.removeAttribute("listaSaldos");
			session.removeAttribute("listaProgramadas");
			session.removeAttribute("listaEfectivas");
			session.removeAttribute("listaVendidas");
			session.removeAttribute("beanPersona");
			session.setAttribute("listaSaldos", listaCabeceras);
			session.setAttribute("listaProgramadas", listaProgramadas);
			session.setAttribute("listaEfectivas", listaEfectivas);
			session.setAttribute("listaVendidas", listaVendidas);
			session.setAttribute("beanPersona", bPers);//hashMap persona
			
			
			HashMap datosTrabajador = ud.buscarTrabajador(pool_sp, codPers,null);
			String fecha = "";
			String fechaIng = "";
			HashMap hmVacGen = vd.buscarVacacionesGen(pool_sp, codPers);
 		    if (hmVacGen!=null && datosTrabajador.get("t02f_ingsun")!=null){
			  fechaIng = (String)datosTrabajador.get("t02f_ingsun");
			  fecha = (String)hmVacGen.get("fecha");
			  if (fechaIng.trim().equals(fecha.trim())){
			    fecha = "";
			  }
			}else{
			    fecha = "";
				fechaIng = "";
		    }
			
			if (datosTrabajador!=null){
			   session.setAttribute("fechaIncorporado", fecha);
			   session.setAttribute("datosTrabajador", datosTrabajador);
			}else{
			   session.setAttribute("datosTrabajador", null);
			}
			

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/programarVacacionesAnalista.jsp");
			dispatcher.forward(request, response);
			return;

		} catch (VacacionException e) {
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
	 * Metodo encargado de cargar una nueva vacaci�n
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarNuevaVacacion(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
						
			HashMap beanPersona = (HashMap) session.getAttribute("beanPersona"); //ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII - //obteniendo datos del trabajador
			if(beanPersona!=null && !beanPersona.isEmpty()){//ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII
				//prac-asanchez
				String numRegistro = request.getParameter("txtValor");
				
				String regimen = (String)beanPersona.get("t02cod_rel");//AMVS-09-2012
				
				session.removeAttribute("listaDias");

				MantenimientoDelegate md = new MantenimientoDelegate();

				ArrayList listaDias = md.cargarT99(pool_sp,
						Constantes.CODTAB_DIAS_VACACIONES);

				session.removeAttribute("listaDias");
				String tipo = (String)session.getAttribute("tipoVac");
				log.debug("tipo "+tipo);
				//session.setAttribute("listaDias", listaDias);//ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII
				session.setAttribute("vacacion", new HashMap());
				session.setAttribute("tipoVac", tipo);
				
				//ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII
				//HashMap beanPersona = (HashMap) session.getAttribute("beanPersona");//obteniendo datos del trabajador			
				//if(beanPersona.get("t02cod_rel").equals(constantes.leePropiedad("CODREL_REG1057")) || beanPersona.get("t02cod_rel").equals(constantes.leePropiedad("CODREL_MOD_FORMATIVA"))){ //CAS (09)Y FORMATIVAS (10)				
				if(beanPersona.get("t02cod_rel").equals(constantes.leePropiedad("CODREL_MOD_FORMATIVA"))){ //CAS (09)Y FORMATIVAS (10)
					ArrayList listaDiasFormativasCas = new ArrayList();
					if(listaDias.size()>0){									
						BeanT99 dia= new BeanT99();	
						for (int i = 0; i < listaDias.size(); i++) {
							dia = (BeanT99)listaDias.get(i);					
							if(dia.getT99descrip().equals(constantes.leePropiedad("DIAS_VACACIONES_FORMATIVAS"))){							
								listaDiasFormativasCas.add(dia);							
							}
						}
					}
					if(listaDiasFormativasCas!=null && listaDiasFormativasCas.size()>0){					
						session.setAttribute("listaDias", listaDiasFormativasCas);
					}				
				}else if(beanPersona.get("t02cod_rel").equals(constantes.leePropiedad("CODREL_REG1057"))) {// CAS //AMVS-09-2012 - Inicio
					ArrayList listaDiasCas = new ArrayList();
					if(listaDias.size()>0){									
						BeanT99 dia= new BeanT99();	
						for (int i = 0; i < listaDias.size(); i++) {
							dia = (BeanT99)listaDias.get(i);					
							if(dia.getT99descrip().equals(constantes.leePropiedad("DIAS_VACACIONES_CAS7")) || 
									dia.getT99descrip().equals(constantes.leePropiedad("DIAS_VACACIONES_CAS8")) ||
									dia.getT99descrip().equals(constantes.leePropiedad("DIAS_VACACIONES_CAS15")) ||
									dia.getT99descrip().equals(constantes.leePropiedad("DIAS_VACACIONES_CAS30"))
									){							
								listaDiasCas.add(dia);							
							}
						}
					}
					if(listaDiasCas!=null && listaDiasCas.size()>0){					
						session.setAttribute("listaDias", listaDiasCas); //AMVS-09-2012
					}	
					
					}else{//PLANILLA
						session.setAttribute("listaDias", listaDias);
					}				
				//FIN ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII
				//AMVS-09-2012 - Fin
				//prac-asanchez
				session.setAttribute("numRegistro", numRegistro);
				session.setAttribute("regimen", regimen);				
				
				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/registrarVacacion.jsp?indice=-1");
				dispatcher.forward(request, response);
				return;
			}	
			//ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII
			else{ 
				if (log.isDebugEnabled()) log.debug("Primero se debe realizar la búsqueda del colaborador");
				throw new Exception("Favor primero debe buscar al colaborador para luego registrar una nueva vacación.");
			}
			//FIN ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII
			

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
	 * Metodo encargado de cargar los datos una vacacion programada
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarDatosVacacion(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			String indice = request.getParameter("indice");

			ArrayList listaEfectivas = (ArrayList) session.getAttribute("listaEfectivas");
			HashMap vacacion = (HashMap) listaEfectivas.get(Integer.parseInt(indice));

			
			MantenimientoDelegate md = new MantenimientoDelegate();
			ArrayList listaDias = md.cargarT99(pool_sp,Constantes.CODTAB_DIAS_VACACIONES);

			session.removeAttribute("listaDias");
			session.removeAttribute("vacacion");

			//session.setAttribute("listaDias", listaDias);//ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII
			session.setAttribute("vacacion", vacacion);
			//ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII
			HashMap beanPersona = (HashMap) session.getAttribute("beanPersona");//obteniendo datos del trabajador		
			//AMVS-09-2012 - Inicio
			if(beanPersona.get("t02cod_rel").equals(constantes.leePropiedad("CODREL_MOD_FORMATIVA"))){ //FORMATIVAS (10) 
				ArrayList listaDiasFormativasCas = new ArrayList();
				if(listaDias.size()>0){									
					BeanT99 dia= new BeanT99();	
					for (int i = 0; i < listaDias.size(); i++) {
						dia = (BeanT99)listaDias.get(i);					
						if(dia.getT99descrip().equals(constantes.leePropiedad("DIAS_VACACIONES_FORMATIVAS"))){							
							listaDiasFormativasCas.add(dia);							
						}
					}
				}
				if(listaDiasFormativasCas!=null && listaDiasFormativasCas.size()>0){					
					session.setAttribute("listaDias", listaDiasFormativasCas);
				}				
			}else if(beanPersona.get("t02cod_rel").equals(constantes.leePropiedad("CODREL_REG1057"))) {//CAS
				ArrayList listaDiasCas = new ArrayList();
				if(listaDias.size()>0){									
					BeanT99 dia= new BeanT99();	
					for (int i = 0; i < listaDias.size(); i++) {
						dia = (BeanT99)listaDias.get(i);					
						if(dia.getT99descrip().equals(constantes.leePropiedad("DIAS_VACACIONES_CAS7")) || 
								dia.getT99descrip().equals(constantes.leePropiedad("DIAS_VACACIONES_CAS8")) ||
								dia.getT99descrip().equals(constantes.leePropiedad("DIAS_VACACIONES_CAS15")) ||
								dia.getT99descrip().equals(constantes.leePropiedad("DIAS_VACACIONES_CAS30"))
								){							
							listaDiasCas.add(dia);							
						}
					}
				}
				if(listaDiasCas!=null && listaDiasCas.size()>0){					
					session.setAttribute("listaDias", listaDiasCas); //AMVS-09-2012
				}	
				
				}else{//PLANILLA
					session.setAttribute("listaDias", listaDias);
				}
			//FIN ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII
			//AMVS-09-2012 - Fin
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/registrarVacacion.jsp");
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
	 * Metodo encargado de registrar una vacacion
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void registrarVacacion(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
						
			//Parte de seguridad
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codUsuario = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			log.debug("Roles "+roles);
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codUsuario);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			VacacionDelegate vd = new VacacionDelegate();
			MantenimientoDelegate md = new MantenimientoDelegate();
			UtilesDelegate ud = new UtilesDelegate();
			
			String codPers = request.getParameter("txtValor");
			String fechaIni = request.getParameter("fechaIni");
			String chk_adelanto = request.getParameter("chk_adelanto");
			int dias = Integer.parseInt(request.getParameter("txtDias"));
			String anio = request.getParameter("anio");
			String tipoVac = session.getAttribute("tipoVac").toString();
			String observacion = request.getParameter("txtObs").trim();
			String ano_ref = request.getParameter("ano_ref")!=null?request.getParameter("ano_ref").trim():"";
			log.debug("ServletVacacion.registrarVacacion... ano_ref: "+ano_ref);//ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII			
			String numero_ref = request.getParameter("numero_ref")!=null?request.getParameter("numero_ref").trim():"0";
			log.debug("ServletVacacion.registrarVacacion... numeroRef: "+numero_ref);//ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII
			String area_ref = request.getParameter("area_ref")!=null?request.getParameter("area_ref").trim():"";
			log.debug("ServletVacacion.registrarVacacion... area_ref: "+area_ref);//ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII

			if (tipoVac.trim().equals(Constantes.VACACION_ESPECIAL)) {
				observacion = Constantes.CADENA_VACACION_ESPECIAL + " "+ observacion;
			}

			String fechaFin = Utiles.dameFechaSiguiente(fechaIni, dias - 1);
			String usr = bUsuario.getLogin() != null ? bUsuario.getLogin(): bUsuario.getNroRegistro();

			HashMap hmPermit = (HashMap) session.getAttribute("diasVPermitidos");
			
			if (tipoVac.trim().equals(Constantes.VACACION)) {
				
				HashMap bPersona = ud.buscarTrabajador(pool_sp,codUsuario,null);
				
				//Por pedido INRH
				if (Integer.parseInt(anio.trim()) >= 2008) {
					//ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII	
					//boolean periodoCerrado = md.periodoCerradoUOFecha(pool_sp,fechaIni,Utiles.obtenerFechaActual(),(String)bPersona.get("t02cod_uorg"));
					HashMap beanCodPers = (HashMap) session.getAttribute("beanPersona");//obteniendo datos del colaborador a registrar vacacion
					boolean periodoCerrado = md.periodoCerradoUOFecha_RegimenModalidad(pool_sp,fechaIni,Utiles.obtenerFechaActual(),(String)bPersona.get("t02cod_uorg"),(String)beanCodPers.get("t02cod_rel"));
					//FIN ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII	
					if (periodoCerrado){
						throw new Exception("El periodo ya se encuentra cerrado para la UO ".concat((String)bPersona.get("t02cod_uorg")).concat("."));
					}				
				}
			}
			
			//registramos el movimiento
			vd.registrarVacacion(pool_sp_g, codPers, tipoVac, Utiles
					.stringToTimestamp(fechaIni + " 00:00:00"), dias, anio, Utiles
					.stringToTimestamp(fechaFin + " 00:00:00"), observacion,
					usr, ano_ref, area_ref, numero_ref, hmPermit, seguridad, chk_adelanto!=null);

			session.removeAttribute("listaSaldos");
			session.removeAttribute("listaProgramadas");
			session.removeAttribute("listaEfectivas");
			session.removeAttribute("listaVendidas");
			session.removeAttribute("beanPersona");
			
			//cargamos nuevamente la ventana de programacion de vacaciones
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/programarVacacionesAnalista.jsp");
			dispatcher.forward(request, response);
			return;

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
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * Metodo encargado de modificar una vacacion
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void modificarVacacion(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			//Parte de seguridad
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codUsuario = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			log.debug("Roles "+roles);
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");
			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codUsuario);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
			VacacionDelegate vd = new VacacionDelegate();
			String fechaIni = request.getParameter("fechaIni");
			int dias = Integer.parseInt(request.getParameter("txtDias"));
			String anio = request.getParameter("anio");
			String observacion = request.getParameter("txtObs");
			String fechaFin = Utiles.dameFechaSiguiente(fechaIni, dias - 1);
			String usr = bUsuario.getLogin() != null ? bUsuario.getLogin()
					: bUsuario.getNroRegistro();
			
/**			
			if (Utiles.obtenerDiasDiferencia(Utiles.obtenerFechaActual(),fechaIni) <= 0) {
				throw new VacacionException("Debe suspender y generar nueva programaci&oacute;n");
			}
*/
			
			HashMap hmPermit = (HashMap) session.getAttribute("diasVPermitidos");

			HashMap vacacion = (HashMap) session.getAttribute("vacacion");
			String codPers = vacacion.get("cod_pers").toString();
			//registramos el movimiento
			vd.modificarVacacion(pool_sp_g, ""/* vacacion.getNumero() */, codPers,
					vacacion.get("periodo").toString(), vacacion.get("licencia").toString(),(java.sql.Timestamp) vacacion
							.get("ffinicio"), Utiles
							.stringToTimestamp(fechaIni + " 00:00:00"), dias,anio,
					Utiles.stringToTimestamp(fechaFin + " 00:00:00"),
					observacion, usr, hmPermit, seguridad);
			
			//Actualizamos la lista de cabeceras y vacaciones efectivas.
			List listaEfectivas = vd.buscarDetalleVacaciones(pool_sp,
					codPers, "", "", "", seguridad);
			
			ArrayList listaSaldos = vd.buscarSaldoAnno(pool_sp, codPers, false,
					"", seguridad);
			session.removeAttribute("listaEfectivas");
			session.removeAttribute("listaSaldos");
			session.setAttribute("listaEfectivas", listaEfectivas);
			session.setAttribute("listaSaldos", listaSaldos);
			//cargamos nuevamente la ventana de programaci�n de vacaciones
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/programarVacacionesAnalista.jsp");
			
			dispatcher.forward(request, response);
			return;
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
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * Metodo encargado de cargar la pagina de suspension de vacaciones
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarSuspencionVacaciones(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		
		try {
			session.removeAttribute("listaCabeceras");
			session.removeAttribute("listaProgramadas");
			session.removeAttribute("listaEfectivas");
			session.removeAttribute("listaVendidas");
			session.removeAttribute("beanPersona");

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/suspenderVacaciones.jsp");
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
	 * Metodo encargado de cargar la pagina de suspension de vacaciones
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarSuspVacaciones(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			HashMap bVac = new HashMap();
			ArrayList lista = (ArrayList) session
					.getAttribute("listaEfectivas");
			String indice = request.getParameter("indice");

			if (indice != null) {
				bVac = (HashMap) lista.get(Integer.parseInt(indice));
			}
			session.removeAttribute("vacacion");
			session.setAttribute("vacacion", bVac);

			//03072008
		    if (bVac.get("anno_ref")!=null && !bVac.get("anno_ref").toString().trim().equals("") && 
		    	bVac.get("area_ref")!=null && !bVac.get("area_ref").toString().trim().equals("") &&
		    	bVac.get("numero_ref")!=null && bVac.get("numero_ref").toString()!="0"){
			   throw new Exception("No se puede Suspender Vacaciones generadas por Solicitud.");}
			   
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/suspenderVacacion.jsp");
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
	 * Metodo encargado de suspender una vacacion
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void suspenderVacacion(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			//Parte de seguridad
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			log.debug("Roles "+roles);
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codPers);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			VacacionDelegate vd = new VacacionDelegate();

			HashMap bVac = (HashMap) session.getAttribute("vacacion");
			String anhoRef = request.getParameter("txtAnho");
			String areaRef = request.getParameter("cmbUOrg");
			String numRef = request.getParameter("txtNumero");

			String usr = bUsuario.getLogin() != null ? bUsuario.getLogin()
					: bUsuario.getNroRegistro();

			if (bVac.get("cod_pers") != null
					&& !bVac.get("licencia").toString().trim().equals(
							Constantes.VACACION_SUSPENDIDA)) {
				vd.suspenderVacacion(pool_sp_g, ""/* bVac.getNumero() */, bVac
						.get("cod_pers").toString().trim(), bVac.get("periodo").toString().trim(), bVac.get("licencia").toString() ,
						(java.sql.Timestamp)bVac.get("ffinicio"), anhoRef, areaRef, numRef, usr);
			}

			session.removeAttribute("listaEfectivas");
			session.removeAttribute("listaSaldos");
			session.removeAttribute("beanPersona");

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/suspenderVacaciones.jsp");
			dispatcher.forward(request, response);
			return;

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
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * Metodo emcargado de buscar la vacaciones por a�o
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarVacXAnoAnalista(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			//Parte de seguridad
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codUsuario = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			log.debug("Roles "+roles);
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codUsuario);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			VacacionDelegate vd = new VacacionDelegate();

			HashMap bPers = (HashMap) session.getAttribute("beanPersona");

			String codPers = (String) bPers.get("t02cod_pers");
			String anho = request.getParameter("txtAnho");

			List listaEfectivas = vd.buscarDetalleVacaciones(pool_sp,
					codPers, "", "", anho, seguridad);

			session.removeAttribute("listaEfectivas");

			session.setAttribute("listaEfectivas", listaEfectivas);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/programarVacacionesAnalista.jsp");
			dispatcher.forward(request, response);
			return;

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
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * Metodo emcargado de buscar las vacaciones por suspender
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarVacacionesXSuspender(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			//Parte de seguridad
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codUsuario = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			log.debug("Roles "+roles);
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codUsuario);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			VacacionDelegate vd = new VacacionDelegate();
			UtilesDelegate ud = new UtilesDelegate();

			String codPers = request.getParameter("txtValor");
			HashMap bPers = ud.buscarTrabajador(pool_sp, codPers, null);
			ArrayList listaCabeceras = vd.buscarSaldoAnno(pool_sp, codPers,
					false, "", seguridad);
			List listaProgramadas = vd.buscarDetalleVacaciones(pool_sp,
					codPers, "", Constantes.VACACION_PROGRAMADA, "", seguridad);
			List listaEfectivas = vd.buscarDetalleVacaciones(pool_sp,
					codPers, "", "", "", seguridad);

			session.removeAttribute("listaSaldos");
			session.removeAttribute("listaProgramadas");
			session.removeAttribute("listaEfectivas");
			session.removeAttribute("beanPersona");

			session.setAttribute("listaSaldos", listaCabeceras);
			session.setAttribute("listaProgramadas", listaProgramadas);
			session.setAttribute("listaEfectivas", listaEfectivas);
			session.setAttribute("beanPersona", bPers);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/suspenderVacaciones.jsp");
			dispatcher.forward(request, response);
			return;

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
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}
	
	/**
	 * Metodo emcargado de buscar las ventas de vacaciones
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarVentasVacaciones(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		
		try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codUsuario = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			log.debug("Roles "+roles);
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codUsuario);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			VacacionDelegate vd = new VacacionDelegate();
			UtilesDelegate ud = new UtilesDelegate();

			String codPers = request.getParameter("txtValor");
			String ano = request.getParameter("txtAno");
			
			HashMap bPers = ud.buscarTrabajador(pool_sp, codPers, seguridad);
			
			session.removeAttribute("listaSaldos");
			session.removeAttribute("listaVentas");
			session.removeAttribute("beanPersona");

			if (bPers.get("t02cod_pers") != null && !((String)bPers.get("t02cod_pers")).equals("")){
				
				ArrayList listaCabeceras = vd.buscarSaldoAnno(pool_sp, codPers, false, "", seguridad);
				List listaVentas = vd.buscarDetalleVacaciones(pool_sp,
						codPers, "", Constantes.VACACION_VENTA, ano, seguridad);

				session.setAttribute("listaSaldos", listaCabeceras);			
				session.setAttribute("listaVentas", listaVentas);
				session.setAttribute("beanPersona", bPers);				
			}			
					
			HashMap datosTrabajador = ud.buscarTrabajador(pool_sp, codPers,null);
			String fecha = "";
			String fechaIng = "";
			HashMap hmVacGen = vd.buscarVacacionesGen(pool_sp, codPers);
 		    if (hmVacGen!=null && datosTrabajador.get("t02f_ingsun")!=null){
			  fechaIng = (String)datosTrabajador.get("t02f_ingsun");
			  fecha = (String)hmVacGen.get("fecha");
			  if (fechaIng.trim().equals(fecha.trim())){
			    fecha = "";
			  }
			}else{
			    fecha = "";
				fechaIng = "";
		    }
			
			if (datosTrabajador!=null){
			   session.setAttribute("fechaIncorporado", fecha);
			   session.setAttribute("datosTrabajador", datosTrabajador);
			}else{
			   session.setAttribute("datosTrabajador", null);
			}
			
			
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/venderVacaciones.jsp");
			dispatcher.forward(request, response);
			return;

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
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}	

	/**
	 * Metodo emcargado de buscar las vacaciones por a�o por suspender
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarVacXAnoXSuspender(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		
		try {
			//Parte de seguridad
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codUsuario = bUsuario.getNroRegistro();//.getNumreg();

			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			log.debug("Roles "+roles);
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codUsuario);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			VacacionDelegate vd = new VacacionDelegate();

			HashMap bPers = (HashMap) session.getAttribute("beanPersona");

			String codPers = (String) bPers.get("t02cod_pers");
			String anho = request.getParameter("txtAnho");

			List listaEfectivas = vd.buscarDetalleVacaciones(pool_sp,
					codPers, "", "", anho, seguridad);

			session.removeAttribute("listaEfectivas");

			session.setAttribute("listaEfectivas", listaEfectivas);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/suspenderVacaciones.jsp");
			dispatcher.forward(request, response);
			return;

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
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * Metodo emcargado de cargar una nueva vacaci�n programada
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarNuevaVacacionProgramada(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		
		try {

			session.removeAttribute("listaDias");

			MantenimientoDelegate md = new MantenimientoDelegate();

			ArrayList listaDias = md.cargarT99(pool_sp,
					Constantes.CODTAB_DIAS_VACACIONES);

			session.removeAttribute("listaDias");

			session.setAttribute("listaDias", listaDias);
			session.setAttribute("vacacion", new HashMap());

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher(
							"/registrarVacacionTrabajador.jsp?indice=-1");
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
	 * Metodo emcargado de cargar los datos de una vacacion programada
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarDatosVacacionProgramada(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		
		try {

			String indice = request.getParameter("indice");

			ArrayList listaProgramadas = (ArrayList) session
					.getAttribute("listaProgramadas");
			HashMap vacacion = (HashMap) listaProgramadas.get(Integer
					.parseInt(indice));

			MantenimientoDelegate md = new MantenimientoDelegate();
			ArrayList listaDias = md.cargarT99(pool_sp,
					Constantes.CODTAB_DIAS_VACACIONES);

			session.removeAttribute("listaDias");
			session.removeAttribute("vacacion");

			session.setAttribute("listaDias", listaDias);
			session.setAttribute("vacacion", vacacion);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/registrarVacacionTrabajador.jsp");
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
	 * Metodo emcargado de buscar las vacaciones por a�o programadas
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarVacXAnoProgramadas(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		
		try {

			VacacionDelegate vd = new VacacionDelegate();

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();//.getNumreg();
			
			String anho = request.getParameter("txtAnho");

			List listaProgramadas = vd.buscarDetalleVacaciones(pool_sp,
					codPers, "", Constantes.VACACION_PROGRAMADA, anho, null);

			session.removeAttribute("listaProgramadas");
			session.setAttribute("listaProgramadas", listaProgramadas);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/programarVacacionesTrabajador.jsp");
			dispatcher.forward(request, response);
			return;

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
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * Metodo emcargado de registrar una vacacion programada
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void registrarVacacionProgramada(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			VacacionDelegate vd = new VacacionDelegate();
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();//.getNumreg();
						
			String user = bUsuario.getLogin();

			String fechaIni = request.getParameter("fechaIni");
			int dias = Integer.parseInt(request.getParameter("txtDias"));
			String observacion = request.getParameter("txtObs").trim();

			String fechaFin = Utiles.dameFechaSiguiente(fechaIni, dias - 1);
			String anho = request.getParameter("anho");
			if (!Utiles.esAnnoValido(anho)) {
				throw new Exception("El a&ntilde;o " + anho
						+ " no es v&aacute;lido.");
			}

			int sigAnho = Integer.parseInt(Utiles.obtenerAnhoActual()) + 1;
			if (Integer.parseInt(anho) > sigAnho) {
				throw new Exception(
						"El a&ntilde;o "
								+ anho
								+ " no es v&aacute;lido.<br>S&oacute;lo se pueden programar vacaciones hasta el "
								+ sigAnho + ".");
			}

			Timestamp fIni = Utiles.stringToTimestamp(fechaIni + " 00:00:00");
			Timestamp primerDia = Utiles.stringToTimestamp("01/01/" + sigAnho
					+ " 00:00:00");

			if (fIni.before(primerDia)) {
				throw new Exception(
						"La fecha de inicio de la programaci&oacute;n no es v&aacute;lida.");
			}

			//registramos el movimiento
			vd.registrarVacacionProgramada(pool_sp_g, codPers,
					Constantes.VACACION_PROGRAMADA, anho, Utiles
							.stringToTimestamp(fechaIni + " 00:00:00"), dias,
					Utiles.stringToTimestamp(fechaFin + " 00:00:00"),
					observacion, user, "", "", "");

			//Actualizamos la lista de cabeceras de vacaciones y de vacaciones
			// efectivas.
			ArrayList listaCabeceras = vd.buscarSaldoAnno(pool_sp, codPers,
					false, "", null);
			List listaProgramadas = vd.buscarDetalleVacaciones(pool_sp,
					codPers, "", Constantes.VACACION_PROGRAMADA, "", null);

			session.removeAttribute("listaCabeceras");
			session.removeAttribute("listaProgramadas");

			session.setAttribute("listaCabeceras", listaCabeceras);
			session.setAttribute("listaProgramadas", listaProgramadas);

			//cargamos nuevamente la ventana de programaci�n de vacaciones
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/programarVacacionesTrabajador.jsp");
			dispatcher.forward(request, response);
			return;

		} catch (VacacionException e) {
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
	 * Metodo emcargado de modificar una vacacion programada
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void modificarVacacionProgramada(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		
		try {
			//Parte de seguridad
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codUsuario = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			log.debug("Roles "+roles);
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codUsuario);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			VacacionDelegate vd = new VacacionDelegate();
			String fechaIni = request.getParameter("fechaIni");

			int dias = Integer.parseInt(request.getParameter("txtDias"));
			String observacion = request.getParameter("txtObs");
			String fechaFin = Utiles.dameFechaSiguiente(fechaIni, dias - 1);

			String usr = bUsuario.getLogin() != null ? bUsuario.getLogin()
					: bUsuario.getNroRegistro();

			if (Utiles.obtenerDiasDiferencia(Utiles.obtenerFechaActual(),
					fechaIni) <= 0) {
				throw new Exception(
						"Debe suspender y generar nueva programaci&oacute;n");
			}

			HashMap vacacion = (HashMap) session
					.getAttribute("vacacion");
			String codPers = vacacion.get("cod_pers").toString();

			//registramos el movimiento
			vd.modificarVacacionProgramada(pool_sp_g,
					""/* vacacion.getNumero() */, codPers,
					vacacion.get("periodo").toString(), (java.sql.Timestamp)vacacion.get("ffinicio"), Utiles
							.stringToTimestamp(fechaIni + " 00:00:00"),
					new Integer(dias), Utiles.stringToTimestamp(fechaFin
							+ " 00:00:00"), observacion, usr);

			//Actualizamos la lista de cabeceras de vacaciones y de vacaciones
			// efectivas.
			List listaProgramadas = vd.buscarDetalleVacaciones(pool_sp,
					codPers, "", Constantes.VACACION_PROGRAMADA, "", seguridad);

			session.removeAttribute("listaProgramadas");
			session.setAttribute("listaProgramadas", listaProgramadas);

			//cargamos nuevamente la ventana de programaci�n de vacaciones
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/programarVacacionesTrabajador.jsp");
			dispatcher.forward(request, response);
			return;
		} catch (VacacionException e) {
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
	 * Metodo emcargado de eliminar una vacacion programada
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void eliminarVacacionProgramada(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			VacacionDelegate vd = new VacacionDelegate();

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			
			String usr = bUsuario.getLogin() != null ? bUsuario.getLogin()
					: bUsuario.getNroRegistro();

			String indice = request.getParameter("indice");

			ArrayList lista = (ArrayList) session
					.getAttribute("listaProgramadas");
			HashMap vacacion = (HashMap) lista.get(Integer
					.parseInt(indice));
			String codPers = vacacion.get("cod_pers").toString();

			//registramos el movimiento
			vd.eliminarVacacion(pool_sp_g,"", codPers, vacacion.get("periodo").toString(), vacacion.get("licencia").toString() ,(java.sql.Timestamp) vacacion.get("ffinicio"), usr);

			//Actualizamos la lista de cabeceras de vacaciones y de vacaciones
			// efectivas.
			List listaProgramadas = vd.buscarDetalleVacaciones(pool_sp,
					codPers, "", Constantes.VACACION_PROGRAMADA, "", null);

			session.removeAttribute("listaProgramadas");
			session.setAttribute("listaProgramadas", listaProgramadas);

			//cargamos nuevamente la ventana de programaci�n de vacaciones
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/programarVacacionesTrabajador.jsp");
			dispatcher.forward(request, response);
			return;
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
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * Metodo emcargado de cargar la cantidad de dias de vacaciones
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarCantDiasVacaciones(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			HashMap hmDiasVPermitidos = (HashMap) session
					.getAttribute("diasVPermitidos");

			if (hmDiasVPermitidos == null) {
				hmDiasVPermitidos = Utiles.diasVacPermitidos();
				session.setAttribute("diasVPermitidos", hmDiasVPermitidos);
			}

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
	 * Metodo emcargado de cargar las vacaciones generadas
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarVacacionGen(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			session.removeAttribute("hmVacGen");

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/registrarFechaReposicion.jsp");
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
	 * Metodo emcargado de buscar las vacaciones generadas
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarVacacionGen(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			VacacionDelegate vd = new VacacionDelegate();

			String codPers = request.getParameter("txtValor");

	
			HashMap hmVacGen = vd.buscarVacacionesGen(pool_sp, codPers);

			if (hmVacGen == null || hmVacGen.get("cod_pers") == null
					|| ((String) hmVacGen.get("cod_pers")).trim().equals("")) {
				throw new Exception(
						"El n&uacute;mero de registro ingresado no es v&aacute;lido.");
			}

			session.removeAttribute("hmVacGen");
			session.setAttribute("hmVacGen", hmVacGen);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/registrarFechaReposicion.jsp");
			dispatcher.forward(request, response);
			return;

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
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * Metodo emcargado de registrar vacaciones generadas
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void registrarVacacionGen(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean user = (UsuarioBean) session.getAttribute("usuarioBean");
			

			String codPers = request.getParameter("codPers");
			String fecha = request.getParameter("fecha");
			String usuario = user.getLogin();

			HashMap mapa = new HashMap();
			mapa.put("codPers", codPers);
			mapa.put("fecha", fecha);
			mapa.put("usuario", usuario);
			mapa.put("dbpool", pool_sp);
			
			log.debug("mapa : "+mapa);

			VacacionDelegate vd = new VacacionDelegate();
			vd.registrarVacacionesGen(mapa);

			session.removeAttribute("hmVacGen");
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/registrarFechaReposicion.jsp");
			dispatcher.forward(request, response);
			return;

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
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * M�todo encargado de cargar los datos necesarios para el la autorizaci�n
	 * anual de vacaciones programadas
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
	private void cargarAutProgramacion(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			MantenimientoDelegate md = new MantenimientoDelegate();

			ArrayList estadosProgramacion = md.cargarT99(pool_sp,
					Constantes.CODTAB_EST_VAC_PROGRAMADA);

			session.removeAttribute("listaVacaciones");
			session.removeAttribute("estadosProgramacion");

			session.setAttribute("listaVacaciones", null);
			session.setAttribute("estadosProgramacion", estadosProgramacion);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher(
							"/registrarAutorizacionVacaciones.jsp");
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
	 * Metodo encargado de buscar las vacaciones programadas para su
	 * autorizaci�n
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarVacacionesProg(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			VacacionDelegate vd = new VacacionDelegate();

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			log.debug("Roles "+roles);
			String codUO = bUsuario.getCodUO();
			
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codPers);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("codUO", codUO);
			seguridad.put("codOpcion", Constantes.DELEGA_VACACIONES);
			seguridad.put("dbpool", pool_sp);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			AsistenciaDelegate ad = new AsistenciaDelegate();

			if (ad.esJefeEncargadoDelegado(seguridad)) {

				String valor = request.getParameter("txtValor");
				String fechaIni = request.getParameter("fechaIni");
				String fechaFin = request.getParameter("fechaFin");

				HashMap mapa = new HashMap();
				mapa.put("valor", valor);
				mapa.put("fechaIni", fechaIni);
				mapa.put("fechaFin", fechaFin);
				mapa.put("codUO", codUO);
				mapa.put("licencia", Constantes.VACACION_PROGRAMADA);
				mapa.put("codUsr", codPers);
				mapa.put("dbpool", pool_sp);
				mapa.put("seguridad", seguridad);

				List listaVacaciones = vd.buscarVacacionesTipoAnho(mapa);

				session.removeAttribute("listaVacaciones");
				session.setAttribute("listaVacaciones", listaVacaciones);

				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher(
								"/registrarAutorizacionVacaciones.jsp");
				dispatcher.forward(request, response);
			} else {
				throw new Exception(
						"Usted no se encuentra habilitado para ejecutar esta opción.");
			}
		} catch (VacacionException e) {
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
	 * M�todo encargado de actualizar las autorizaciones de programaci�n anual
	 * 
	 * @param response
	 *            HttpServletResponse
	 * @param session
	 *            HttpSession
	 * @throws ServletException
	 * @throws IOException
	 */
	private void actualizarAutorizacion(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			//Parte de seguridad
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			
			String usuario = bUsuario.getLogin();
			
			VacacionDelegate vd = new VacacionDelegate();

			ArrayList lista = (ArrayList) session
					.getAttribute("listaVacaciones");

			String[] params = new String[lista.size()];
			for (int i = 0; i < lista.size(); i++) {
				params[i] = request.getParameter("cmbTipo" + i);
			}

			String tipoaut = request.getParameter("tipoaut");
			vd.calificarVacProg(pool_sp_g, params, lista, tipoaut, usuario);

			session.removeAttribute("listaVacaciones");

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher(
							"/registrarAutorizacionVacaciones.jsp");
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
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}
	}
	
	/**
	 * Metodo emcargado de eliminar una venta de vacaciones
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void eliminarVentaVacaciones(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codUsuario = bUsuario.getNroRegistro();//.getNumreg();
			
			VacacionDelegate vd = new VacacionDelegate();

			String indice = request.getParameter("indice");
			ArrayList listaVentas = (ArrayList)session.getAttribute("listaVentas");
			HashMap venta = (HashMap)listaVentas.get(Integer.parseInt(indice));
			
			//03072008
			if (venta.get("anno_ref")!=null && !venta.get("anno_ref").toString().trim().equals("") && 
				venta.get("area_ref")!=null && !venta.get("area_ref").toString().trim().equals("") &&
				venta.get("numero_ref")!=null && venta.get("numero_ref").toString()!="0"){
			    throw new Exception("No se puede eliminar una Venta de Vacaciones generada por Solicitud.");}			
			
			vd.eliminarVacacion(pool_sp_g , "",venta.get("cod_pers").toString(),venta.get("periodo").toString(), venta.get("licencia").toString(),(java.sql.Timestamp)venta.get("ffinicio"),codUsuario);
			
			session.removeAttribute("listaSaldos");
			session.removeAttribute("listaVentas");
			session.removeAttribute("beanPersona");

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/venderVacaciones.jsp");
			dispatcher.forward(request, response);
			return;

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
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}	

	
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
}