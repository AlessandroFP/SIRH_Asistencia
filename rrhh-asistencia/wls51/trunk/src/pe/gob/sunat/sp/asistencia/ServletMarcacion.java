package pe.gob.sunat.sp.asistencia;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.log4j.NDC;

import pe.gob.sunat.framework.core.pattern.DynaBean;
import pe.gob.sunat.framework.core.servlet.ServletAbstract;
import pe.gob.sunat.sol.BeanMensaje;
//import pe.gob.sunat.sol.seguridad.acceso.jaas.AutenticacionException;
//import pe.gob.sunat.sol.seguridad.acceso.jaas.ServletAutenticacionIntranet;
//import pe.gob.sunat.sol.seguridad.acceso.portal.bean.BeanUsuario;
import pe.gob.sunat.sp.asistencia.bean.BeanMarcacion;
import pe.gob.sunat.sp.asistencia.bean.BeanTipoReloj;
import pe.gob.sunat.sp.asistencia.ejb.delegate.AsistenciaDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.AsistenciaException;
import pe.gob.sunat.sp.asistencia.ejb.delegate.LicenciaException;
import pe.gob.sunat.sp.asistencia.ejb.delegate.MantenimientoDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.MantenimientoException;
import pe.gob.sunat.sp.asistencia.ejb.delegate.UtilesDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.UtilesException;
import pe.gob.sunat.tecnologia.menu.bean.UsuarioBean;
import pe.gob.sunat.utils.Constantes;

/**
 * @web.servlet name="ServletMarcacion"
 * @web.servlet-mapping url-pattern = "/asisS06Alias"
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
public class ServletMarcacion extends ServletAbstract {

	private static final Logger log = Logger.getLogger(ServletMarcacion.class);
	private static String pool_sp;
	private static String pool_sp_g;

	public ServletMarcacion() {
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
		if (accion == null) {
			accion = "procesarMarcaciones";
		}
		
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
		
		if (accion.equals("iniciarMarcaciones")) {
			iniciarMarcaciones(request, response, session);
		}
		if (accion.equals("procesarMarcaciones")) {
			procesarMarcaciones(request, response, session);
		}
		if (accion.equals("cargarImpares")) {
			cargarMarcacionesImpares(request, response, session);
		}
		if (accion.equals("buscarImpares")) {
			buscarMarcacionesImpares(request, response, session);
		}
		if (accion.equals("registrarImpar")) {
			registrarImpar(request, response, session);
		}
		if (accion.equals("cargarGenerarData")) {
			cargarGenerarData(request, response, session);
		}
		if (accion.equals("generarData")) {
			generarData(request, response, session);
		}
		if (accion.equals("cargarPases")) {
			cargarPases(request, response, session);
		}
		if (accion.equals("buscarPases")) {
			buscarPases(request, response, session);
		}
		if (accion.equals("procesarPases")) {
			procesarPases(request, response, session);
		}		
		if (accion.equals("cargarManual")) {
			cargarManual(request, response, session);
		}
		if (accion.equals("buscarManual")) {
			buscarManual(request, response, session);
		}
		if (accion.equals("registrarManual")) {
			registrarManual(request, response, session);
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
	 * Mï¿½todo encargado de buscar las marcaciones impares
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarMarcacionesImpares(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();//.getNumreg();
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			//log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			//log.debug("Roles "+roles);
			
			//String codPers = bUsuario.getNumreg();
//			HashMap roles = super.getRoles(session);
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codPers);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			String fechaIni = request.getParameter("fechaIni") != null ? request.getParameter("fechaIni") : "";
			String fechaFin = request.getParameter("fechaFin") != null ? request.getParameter("fechaFin") : "";
			String criterio = request.getParameter("cmbCriterio") != null ? request.getParameter("cmbCriterio") : "";
			String valor = request.getParameter("txtValor") != null ? request.getParameter("txtValor") : "";

			
			AsistenciaDelegate ad = new AsistenciaDelegate();
			ArrayList marcaciones = ad.buscarMarcacionesImpares(pool_sp,
					fechaIni, fechaFin, criterio, valor, seguridad);

			session.removeAttribute("listaImpares");
			session.setAttribute("listaImpares", marcaciones);

			session.setAttribute("fechaIni", fechaIni);
			session.setAttribute("fechaFin", fechaFin);
			session.setAttribute("criterio", criterio);
			session.setAttribute("valor", valor);
			
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/marcacionesImpares.jsp");
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
	 * Mï¿½todo encargado de cargar los datos para el registro de marcaciones
	 * impares
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarMarcacionesImpares(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			String indice = request.getParameter("indice");
			if (indice != null) {

				ArrayList marcaciones = (ArrayList) session
						.getAttribute("listaImpares");
				BeanMarcacion marca = (BeanMarcacion) marcaciones.get(Integer
						.parseInt(indice));

				AsistenciaDelegate ad = new AsistenciaDelegate();
				ArrayList detalle = ad.listarMarcacionesImpares(pool_sp, marca
						.getCodPers(), marca.getFecha());

				UtilesDelegate ud = new UtilesDelegate();
				HashMap trabImpar = ud.buscarTrabajador(pool_sp, marca
						.getCodPers(), null);

				session.removeAttribute("trabImpar");
				session.setAttribute("trabImpar", trabImpar);

				session.removeAttribute("marcacion");
				session.setAttribute("marcacion", marca);

				session.removeAttribute("listaDetalleImpares");
				session.setAttribute("listaDetalleImpares", detalle);

				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/listaMarcacionesImpares.jsp");
				dispatcher.forward(request, response);
				return;
			} else {
				session.removeAttribute("listaImpares");
				
				session.setAttribute("fechaIni","");
				session.setAttribute("fechaFin","");
				session.setAttribute("valor","");
				session.setAttribute("criterio","-1");

				
				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/marcacionesImpares.jsp");
				dispatcher.forward(request, response);
				return;
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
	 * Mï¿½todo encargado de cargar la pï¿½gina para obtener marcaciones de
	 * reloj
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void iniciarMarcaciones(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			MantenimientoDelegate md = new MantenimientoDelegate();
			ArrayList relojes = md.cargarRelojes(pool_sp, Constantes.ACTIVO);

			session.removeAttribute("listaRelojes");
			session.setAttribute("listaRelojes", relojes);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/obtenerMarcaciones.jsp");
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
	 * Mï¿½todo encargado de procesar las marcaciones de reloj
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void procesarMarcaciones(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		HashMap datos = new HashMap();
		try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();
			String usuario = bUsuario.getLogin();

			ArrayList relojes = (ArrayList) session.getAttribute("listaRelojes");

			Boolean multipart = (Boolean)request.getAttribute("IsMultipart");
			DynaBean dbean = new DynaBean( request );
		    
			//log.debug("multi" + multipart);
			//if (multipart!=null && multipart.booleanValue()){
				if (dbean.isMultipart()){
				
				Map multi = (HashMap)request.getAttribute("Multipart");
				//log.debug("Multipart "+multi);
				//log.debug("Reloj "+ dbean.getString("cmbReloj"));
				
				String indReloj =  dbean.getString("cmbReloj");
				String fechaIni =  dbean.getString("fechaIni");
				//String fechaFin = (String) multi.get("fechaFin");
				String fechaFin =  dbean.getString("fechaFin"); //EBV Pedido SIRH 05/05/2006; jquispe 02/08/2013
				String criterio = dbean.getString("cmbCriterio");
				String valor = dbean.getString("txtValor");

				int intReloj = Integer.parseInt(indReloj);
				String codReloj = "-1";
				if (intReloj != -1) {
					BeanTipoReloj reloj = (BeanTipoReloj) relojes.get(intReloj);
					codReloj = reloj.getReloj();
				}

				//File ruta = (File)multi.get("ruta");
				File ruta = (File)dbean.get("ruta");

				datos.put("dbpool",pool_sp);
				datos.put("reloj",codReloj);
				datos.put("fechaIni",fechaIni);
				datos.put("fechaFin",fechaFin);
				datos.put("ruta",ruta.getAbsolutePath());
				datos.put("criterio",criterio);
				datos.put("valor",valor);
				datos.put("codPers",codigo);
				datos.put("usuario",usuario);
				
				AsistenciaDelegate ad = new AsistenciaDelegate();
				ad.procesarMarcaciones(datos);

				request.setAttribute("nomProceso","OBTENER MARCACIONES DE RELOJ");
				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/resultadoProceso.jsp");
				dispatcher.forward(request, response);
				return;
				
			} else {
				throw new Exception("El archivo ingresado no existe o no es valido.");
			}
			
		} catch (AsistenciaException e) {
			log.error("***Error***",e);
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			log.error("***Error***",e);
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "+ e.toString());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * Mï¿½todo encargado de registrar una marcaciï¿½n impar
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void registrarImpar(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean user = (UsuarioBean) session
					.getAttribute("usuarioBean");
			String usuario = user.getLogin();

			ArrayList marcaciones = (ArrayList) session
					.getAttribute("listaImpares");
			BeanMarcacion marca = (BeanMarcacion) session
					.getAttribute("marcacion");
			String hora = request.getParameter("hora");

			AsistenciaDelegate ad = new AsistenciaDelegate();
			ad.registrarMarcacionImpar(pool_sp_g, marca.getCodPers(), marca
					.getFecha(), hora, usuario);
			marcaciones.remove(marca);

			session.removeAttribute("marcacion");
			session.removeAttribute("listaDetalleImpares");

			session.removeAttribute("listaImpares");
			session.setAttribute("listaImpares", marcaciones);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/marcacionesImpares.jsp");
			dispatcher.forward(request, response);
			return;
		} catch (LicenciaException e) {
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
	 * Mï¿½todo encargado de cargar la pï¿½gina para el proceso de la
	 * generaciï¿½n de la data para reloj
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarGenerarData(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/generarData.jsp");
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
	 * Metodo encargado de cargar la pagina de pases provisionales
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarPases(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			session.removeAttribute("listaPases");
			session.removeAttribute("codPase");
			session.removeAttribute("fechaIniPase");
			session.removeAttribute("fechaFinPase");
			
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/procesarPases.jsp");
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
	 * Mï¿½todo encargado de ejecutar el proceso de generaciï¿½n de data de
	 * reloj
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void generarData(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();//.getNumreg();
			String usuario = bUsuario.getLogin();
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			//log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			//log.debug("Roles "+roles);
			//BeanUsuario bUsuario = (BeanUsuario) session.getAttribute("beanusuario");
			//String codPers = bUsuario.getNumreg();
			//String usuario = bUsuario.getLogin();
			//HashMap roles = super.getRoles(session);
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codPers);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			String fecha = request.getParameter("fecha");

			AsistenciaDelegate ad = new AsistenciaDelegate();
			ad.generarDataReloj(pool_sp_g, fecha, codPers, usuario,
					seguridad);

			request.setAttribute("nomProceso",
					"GENERACI&Oacute;N DE DATA DEL RELOJ");
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
	 * Metodo encargado de buscar los pases provisionales
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarPases(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			String fechaIni = request.getParameter("fechaIni");			
			String fechaFin = request.getParameter("fechaFin");
			String codPase = request.getParameter("txtPase") != null ? request.getParameter("txtPase") : "";

			AsistenciaDelegate ad = new AsistenciaDelegate();
			ArrayList marcaciones = ad.buscarMarcacionesPase(pool_sp,fechaIni,fechaFin,codPase,null);
			
			session.removeAttribute("listaPases");
			session.setAttribute("listaPases", marcaciones);
			
			session.setAttribute("codPase", codPase);
			session.setAttribute("fechaIniPase", fechaIni);
			session.setAttribute("fechaFinPase", fechaFin);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/procesarPases.jsp");
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
	 * Metodo encargado de procesar los pases provisionales
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void procesarPases(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");

			ArrayList lista = (ArrayList)session.getAttribute("listaPases");
			String codPers = (request.getParameter("txtValor")!=null) ? (String)request.getParameter("txtValor"):"";
			/*//log.debug("========================================");
			log.debug("dentro del servletMarcacion..metodo procesarPases");
			log.debug("========================================");
			log.debug("listaPases.size="+lista.size());
			log.debug("txtValor==cod_registroe ="+codPers);
			log.debug("========================================");*/
			
			HashMap datos = new HashMap();
			datos.put("dbpool",pool_sp);
			datos.put("lista",lista);
			datos.put("codPers",codPers.trim().toUpperCase());
			datos.put("usuario",bUsuario.getNroRegistro());
			
			AsistenciaDelegate ad = new AsistenciaDelegate();
			ad.procesarPases(datos);

			session.removeAttribute("listaPases");
			session.removeAttribute("codPase");
			session.removeAttribute("fechaIniPase");
			session.removeAttribute("fechaFinPase");
			
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/procesarPases.jsp");
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
	 * Metodo encargado de cargar la pagina de registro manual de marcaciones
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarManual(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		
		try {

			session.removeAttribute("listaMarcas");
			session.removeAttribute("fechaManual");			
			session.removeAttribute("codPersManual");
			
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/registroManual.jsp");
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
	 * Metodo encargado de buscar las marcaciones para el registro manual
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarManual(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			String fecha = request.getParameter("fecha");						
			String txtValor = request.getParameter("txtValor") != null ? request.getParameter("txtValor") : "";

			UtilesDelegate ud = new UtilesDelegate();
			HashMap bPers = ud.buscarTrabajador(pool_sp, txtValor, null);
			
			if (bPers.get("t02cod_pers") == null || ((String)bPers.get("t02cod_pers")).equals("")){
				throw new Exception("El registro ingresado no es válido.");
			}				
			
			AsistenciaDelegate ad = new AsistenciaDelegate();
			ArrayList marcaciones = ad.buscarMarcaciones(pool_sp,fecha,txtValor,null);
			
			session.removeAttribute("listaMarcas");
			session.setAttribute("listaMarcas", marcaciones);
			
			session.setAttribute("codPersManual", txtValor);
			session.setAttribute("fechaManual", fecha);			

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/registroManual.jsp");
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
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}
	}	
	
	/**
	 * Metodo encargado de registrar las marcaciones manuales
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void registrarManual(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");

			String codPersManual = (String)session.getAttribute("codPersManual");
			String fechaManual = (String)session.getAttribute("fechaManual");
			String txtMarca = request.getParameter("txtMarca");
			
			HashMap datos = new HashMap();
			datos.put("dbpool",pool_sp_g);			
			datos.put("codPersManual",codPersManual.trim().toUpperCase());
			datos.put("fechaManual",fechaManual);
			datos.put("txtMarca",txtMarca.trim()+":00");
			datos.put("usuario",bUsuario.getNroRegistro());
			
			AsistenciaDelegate ad = new AsistenciaDelegate();
			ad.registrarManual(datos);

			ArrayList marcaciones = ad.buscarMarcaciones(pool_sp,fechaManual,codPersManual,null);
			
			session.removeAttribute("listaMarcas");
			session.setAttribute("listaMarcas", marcaciones);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/registroManual.jsp");
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