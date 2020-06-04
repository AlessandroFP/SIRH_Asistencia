package pe.gob.sunat.sp.asistencia;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import pe.gob.sunat.sp.asistencia.ejb.delegate.AsistenciaDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.AsistenciaException;
import pe.gob.sunat.sp.asistencia.ejb.delegate.MantenimientoDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.MantenimientoException;
import pe.gob.sunat.sp.asistencia.ejb.delegate.ReporteDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.ReporteException;
import pe.gob.sunat.sp.asistencia.ejb.delegate.SolicitudDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.SolicitudException;
import pe.gob.sunat.sp.asistencia.ejb.delegate.UtilesDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.UtilesException;
import pe.gob.sunat.tecnologia.menu.bean.UsuarioBean;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;

/**
 * @web.servlet name="ServletMensajeria"
 * @web.servlet-mapping url-pattern = "/asisS13Alias"
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
public class ServletMensajeria extends ServletAbstract {

	private static final Logger log = Logger.getLogger(ServletMensajeria.class);
	private static String pool_sp;
	private static String pool_sp_g;

	public ServletMensajeria() {
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
		
		if (accion.equals("cargarLogProcesos")) {
			cargarLogProcesos(request, response, session);
		}

		if (accion.equals("cargarLogReportes")) {
			cargarLogReportes(request, response, session);
		}

		if (accion.equals("cargarPapeletas")) {
			cargarPapeletas(request, response, session);
		}

		if (accion.equals("cargarRecibidas")) {
			cargarRecibidas(request, response, session);
		}

		if (accion.equals("verSolicitud")) {
			verSolicitud(request, response, session);
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
	 * M�todo encargado de listar los procesos realizados por un usuario
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarLogProcesos(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();//.getNumreg();

			AsistenciaDelegate ad = new AsistenciaDelegate();
			ArrayList listaLog = ad.cargarLogProcesos(pool_sp, codigo);

			session.removeAttribute("listaLog");
			session.setAttribute("listaLog", listaLog);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/administradorProcesos.jsp");
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
	 * M�todo encargado de listar los reportes realizados por un usuario
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarLogReportes(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();//.getNumreg();

			ReporteDelegate rd = new ReporteDelegate();
			ArrayList listaLog = rd.cargarLogReportes(pool_sp, codigo);

			session.removeAttribute("listaLogReportes");
			session.setAttribute("listaLogReportes", listaLog);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/administradorReportes.jsp");
			dispatcher.forward(request, response);
			return;

		} catch (ReporteException e) {
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
	 * M�todo encargado de listas las solicitudes recibidas por un usuario
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarRecibidas(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();//.getNumreg();

			MantenimientoDelegate md = new MantenimientoDelegate();
			ArrayList tipos = md.buscarMovimientosSolicitud(pool_sp,
					Constantes.ACTIVO);

			session.removeAttribute("tiposSolicitud");
			session.setAttribute("tiposSolicitud", tipos);

			SolicitudDelegate sd = new SolicitudDelegate();
			ArrayList listaRecibidas = sd.cargarSolicitudesRecibidas(pool_sp,
					codPers, "-1", "", "");

			session.removeAttribute("listaRecibidas");
			session.setAttribute("listaRecibidas", listaRecibidas);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/solicitudesRecibidas.jsp");
			dispatcher.forward(request, response);
			return;

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
	 * M�todo encargado de visualizar una solicitud
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void verSolicitud(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();//.getNumreg();
			
			//String codPers = bUsuario.getNumreg();

			String anno = request.getParameter("anno") != null ? request
					.getParameter("anno") : "";
			String numero = request.getParameter("numero") != null ? request
					.getParameter("numero") : "";

			SolicitudDelegate sd = new SolicitudDelegate();
			UtilesDelegate ud = new UtilesDelegate();

			if (!anno.equals("") && !numero.equals("")) {

				HashMap solicitud = sd.cargarSolicitud(pool_sp, codPers, anno,
						numero);
				ArrayList seguimientos = sd.cargarSeguimientos(pool_sp,
						codPers, anno, numero);

				HashMap solicitante = ud.buscarTrabajadorJefe(pool_sp, codPers, null);

				session.removeAttribute("seguimientos");
				session.setAttribute("seguimientos", seguimientos);

				session.removeAttribute("solicitudIniciada");
				session.setAttribute("solicitudIniciada", solicitud);

				session.removeAttribute("solicitante");
				session.setAttribute("solicitante", solicitante);

				request.removeAttribute("correo");
				request.setAttribute("correo", "true");

				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/visualizarSolicitud.jsp");
				dispatcher.forward(request, response);
				return;

			} else {
				throw new Exception("Los datos de la solicitud no son validos.");
			}
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
	 * M�todo encargado de listar las papeletas de un trabajador para una fecha
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarPapeletas(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			log.debug("Roles "+roles);
			//BeanUsuario bUsuario = (BeanUsuario) session.getAttribute("beanusuario");
			//String codPers = bUsuario.getNumreg();
			//HashMap roles = super.getRoles(session);
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");


			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codPers);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			String fecha = request.getParameter("fecha") != null ? request
					.getParameter("fecha") : "";

			if (!fecha.equals("")) {

				AsistenciaDelegate ad = new AsistenciaDelegate();
				MantenimientoDelegate md = new MantenimientoDelegate();

				ArrayList estadosPapeleta = md.cargarT99(pool_sp,
						Constantes.CODTAB_ESTADO_PAPELETA);
				String fechaActual = Utiles.obtenerFechaActual();

				session.removeAttribute("listaAsistencias");
				session.removeAttribute("tiposPapeleta");
				session.removeAttribute("beanPersona");
				session.removeAttribute("estadosPapeleta");

				List listaAsistencias = ad.buscarAsistencias(pool_sp,
						codPers, fecha, seguridad);
				ArrayList tiposPapeleta = md.buscarTipoMovimientos(pool_sp,
						Constantes.CODTAB_TIPO_PAPELETA, Constantes.ACTIVO,
						fechaActual, fechaActual);

				session.setAttribute("listaAsistencias", listaAsistencias);
				session.setAttribute("tiposPapeleta", tiposPapeleta);
				session.setAttribute("estadosPapeleta", estadosPapeleta);
				request.setAttribute("fecha", fecha);

				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher(
								"/registrarPapeletas.jsp?esPapeleta=1");
				dispatcher.forward(request, response);
				return;

			} else {
				throw new Exception("La fecha no es valida.");
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