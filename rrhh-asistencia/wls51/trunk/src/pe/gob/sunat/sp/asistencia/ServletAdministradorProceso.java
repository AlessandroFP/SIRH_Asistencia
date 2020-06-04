package pe.gob.sunat.sp.asistencia;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

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
import pe.gob.sunat.sp.asistencia.ejb.delegate.ReporteDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.ReporteException;
import pe.gob.sunat.tecnologia.menu.bean.UsuarioBean;
import pe.gob.sunat.utils.DescargarArchivo;

/**
 * @web.servlet name="ServletAdministradorProceso"
 * @web.servlet-mapping url-pattern = "/asisS12Alias"
 * @web.servlet-init-param name = "pool_sp" value = "jdbc/dcsp"
 * @web.servlet-init-param name = "pool_sp_g" value = "jdbc/dgsp"
 *
 * <p>Title: Control de Asistencia</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Sunat</p>
 * @author cgarratt
 * @version 1.0
 */
public class ServletAdministradorProceso extends ServletAbstract {

	private static final Logger log = Logger.getLogger(ServletAdministradorProceso.class);
	private static String pool_sp;
	private static String pool_sp_g;

	public ServletAdministradorProceso() {		
	}

	public void destroy() {
		super.destroy();
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
        pool_sp = config.getInitParameter("pool_sp");
        pool_sp_g = config.getInitParameter("pool_sp_g");
	}
/*
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		try {
			super.doGet(request, response);
		} catch (AutenticacionException ex) {
			log.error("*** Error ***",ex);
			HttpSession session = request.getSession();
			session.setAttribute("beanErr", ex.getBeanMensaje());
			RequestDispatcher dispatcher =
				this.getServletContext().getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
			session.invalidate();
			return;
		}
	}

	public void doPost(
		HttpServletRequest request,
		HttpServletResponse response)
		throws ServletException, IOException {
		try {
			super.doPost(request, response);
		} catch (AutenticacionException ex) {
			log.error("*** Error ***",ex);
			HttpSession session = request.getSession();
			session.setAttribute("beanErr", ex.getBeanMensaje());
			RequestDispatcher dispatcher =
				this.getServletContext().getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
			session.invalidate();
			return;
		}
	}
*/
	public void procesa(
		HttpServletRequest request,
		HttpServletResponse response,
		HttpSession session)
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
		//BeanUsuario bUsuario = (BeanUsuario) session.getAttribute("beanusuario");
		//NDC.push(bUsuario.getNumreg());		
		log.info("Accion : " + accion);
		
		if (accion.equals("cargarLogProcesos")) {
			cargarLogProcesos(request, response, session);
		}
		if (accion.equals("cargarReportesMasivos")) {
			cargarReportesMasivos(request, response, session);
		}
		if (accion.equals("descargarLogProceso")) {
			descargarLogProceso(request, response, session);
		}
		if (accion.equals("descargarLogReporte")) {
			descargarLogReporte(request, response, session);
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
	 * Metodo encargado de cargar los log de los procesos masivos generados por el
	 * usuario
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param session HttpSession
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarLogProcesos(
		HttpServletRequest request,
		HttpServletResponse response,
		HttpSession session)
		throws ServletException, IOException {

		try {

			log.debug("Entro a cargarLogProcesos....");		
			
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();

			AsistenciaDelegate ad = new AsistenciaDelegate();
			ArrayList listaLog = ad.cargarLogProcesos(pool_sp, codigo);

			session.removeAttribute("listaLog");
			session.setAttribute("listaLog", listaLog);

			RequestDispatcher dispatcher =
				getServletContext().getRequestDispatcher(
					"/administradorProcesos.jsp");
			dispatcher.forward(request, response);
			return;

		} catch (AsistenciaException e) {
			log.error("*** Error ***",e);
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher =
				getServletContext().getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			log.error("*** Error ***",e);
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(
				"Ha ocurrido un error en la aplicacion. " + e.getMessage());
			bean.setMensajesol(
				"Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher =
				getServletContext().getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	     * M�todo encargado de cargar los log de los reportes masivos generados por el
	 * usuario
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param session HttpSession
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarReportesMasivos(
		HttpServletRequest request,
		HttpServletResponse response,
		HttpSession session)
		throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();


			ReporteDelegate rd = new ReporteDelegate();
			ArrayList listaLog = rd.cargarLogReportes(pool_sp, codigo);

			session.removeAttribute("listaLogReportes");
			session.setAttribute("listaLogReportes", listaLog);

			RequestDispatcher dispatcher =
				getServletContext().getRequestDispatcher(
					"/administradorReportes.jsp");
			dispatcher.forward(request, response);
			return;

		} catch (ReporteException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher =
				getServletContext().getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(
				"Ha ocurrido un error en la aplicacion. " + e.getMessage());
			bean.setMensajesol(
				"Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher =
				getServletContext().getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * M�todo encargado de descargar el log de un procesos masivos para poder ser almacenado
	 * por el trabajador
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param session HttpSession
	 * @throws ServletException
	 * @throws IOException
	 */
	private void descargarLogProceso(
		HttpServletRequest request,
		HttpServletResponse response,
		HttpSession session)
		throws ServletException, IOException {

		try {

			String indice = request.getParameter("indice");
			ArrayList listaLog = (ArrayList) session.getAttribute("listaLog");
			HashMap log = (HashMap) listaLog.get(Integer.parseInt(indice));

			String id = (String) log.get("id_proceso");
			
			AsistenciaDelegate ad = new AsistenciaDelegate();
			InputStream archivo = ad.descargarLogProceso(pool_sp, id);

			if (archivo != null) {
				
				String nombre = id + ".zip";
				DescargarArchivo down = new DescargarArchivo();
				down.descargarAPantalla(
					archivo,
					nombre,
					DescargarArchivo.ZIP,
					response);

			} else {
				throw new Exception("Error al descargar el archivo de log.");
			}

		} catch (AsistenciaException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher =
				getServletContext().getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(
				"Ha ocurrido un error en la aplicacion. " + e.getMessage());
			bean.setMensajesol(
				"Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher =
				getServletContext().getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * Metodo encargado de descargar el log de un reporte masivo para poder ser almacenado
	 * por el trabajador
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param session HttpSession
	 * @throws ServletException
	 * @throws IOException
	 */
	private void descargarLogReporte(
		HttpServletRequest request,
		HttpServletResponse response,
		HttpSession session)
		throws ServletException, IOException {

		try {

			String indice = request.getParameter("indice");
			ArrayList listaLog =
				(ArrayList) session.getAttribute("listaLogReportes");
			HashMap log = (HashMap) listaLog.get(Integer.parseInt(indice));

			String id = (String) log.get("id_proceso");
			
			AsistenciaDelegate ad = new AsistenciaDelegate();
			InputStream archivo = ad.descargarLogProceso(pool_sp, id);

			if (archivo != null) {

				String nombre = id + ".zip";
				DescargarArchivo down = new DescargarArchivo();
				down.descargarAPantalla(
					archivo,
					nombre,
					DescargarArchivo.ZIP,
					response);

			} else {
				throw new Exception("Error al descargar el archivo de log.");
			}

		} catch (AsistenciaException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher =
				getServletContext().getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(
				"Ha ocurrido un error en la aplicacion. " + e.getMessage());
			bean.setMensajesol(
				"Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher =
				getServletContext().getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

}