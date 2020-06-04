package pe.gob.sunat.sp.asistencia;

import java.io.IOException;
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
import pe.gob.sunat.sp.asistencia.bean.BeanPeriodo;
import pe.gob.sunat.sp.asistencia.ejb.delegate.UtilesDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.UtilesException;
import pe.gob.sunat.tecnologia.menu.bean.UsuarioBean;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;

/**
 * @web.servlet name="ServletBusqueda"
 * @web.servlet-mapping url-pattern = "/asisS04Alias"
 * @web.servlet-init-param name = "pool_sp" value = "jdbc/dcsp"
 * @web.servlet-init-param name = "pool_sp_g" value = "jdbc/dgsp"
 * 
 * <p>Title: ServletBusqueda</p>
 * <p>Description: Clase encargada de administrar la busqueda por persona, uo y categoria</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Sunat</p>
 * @author cgarratt
 * @version 1.0
 */
public class ServletBusqueda extends ServletAbstract {

	private static final Logger log = Logger.getLogger(ServletBusqueda.class);
	private static String pool_sp;
	private static String pool_sp_g;

	public ServletBusqueda() {
	}

	public void destroy() {
		super.destroy();
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
        pool_sp = config.getInitParameter("pool_sp");
        pool_sp_g = config.getInitParameter("pool_sp_g");
	}


	public void procesa(
		HttpServletRequest request,
		HttpServletResponse response,
		HttpSession session)
		throws ServletException, IOException {

		String accion = "";
		try {

			accion = request.getParameter("accion");
			log.info("Accion : " + accion);
			
		} catch (Exception e) {
			log.info("Accion : " + e.getStackTrace());
			
		}
		
		if (session == null) {
			RequestDispatcher dispatcher =
				this.getServletContext().getRequestDispatcher(
					"/PagSession.jsp");
			dispatcher.forward(request, response);
			return;
		}

		if (accion.equals("cargarBusqueda")) {
			cargarBusqueda(request, response, session);
		}

		if (accion.equals("buscarUO")) {
			buscarUO(request, response, session);
		}
		//JRR - 21/06/2010 - FUSION PROGRAMACION
		if (accion.equals("buscarIntendencias")) {
			buscarIntendencias(request, response, session);
		}
		//
		if (accion.equals("buscarPersonal")) {
			buscarPersonal(request, response, session);
		}

		if (accion.equals("buscarCategoria")) {
			buscarCategoria(request, response, session);
		}

		if (accion.equals("buscarUOPeriodo")) {
			buscarUOPeriodo(request, response, session);
		}

		if (accion.equals("buscarPersonalM")) {
			buscarPersonalM(request, response, session);
		}

		if (accion.equals("agregarPersonal")) {
			agregarPersonal(request, response, session);
		}
		
		if (accion.equals("buscarDiagnosticoCIE")) {
			buscarDiagnosticoCIE(request, response, session);
		}

	}

	/**
	 * M�todo encargado de cargar la pantalla de b�squeda correspondiente
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarBusqueda(
		HttpServletRequest request,
		HttpServletResponse response,
		HttpSession session)
		throws ServletException, IOException {

		try {

			String criterio = request.getParameter("cmbCriterio");
			String txtValor = request.getParameter("txtValor")!=null?request.getParameter("txtValor"):"txtValor";			    
			String detalle = request.getParameter("detalle")!=null?request.getParameter("detalle"):"detalle";
			
			//busqueda de personal
			if (criterio.equals("0")) {

				log.debug("criterio: "+criterio);
			    session.removeAttribute("listaPersonal");

			    session.removeAttribute("txtValor");
			    session.setAttribute("txtValor",txtValor);		    
			    session.removeAttribute("detalle");
			    session.setAttribute("detalle",detalle);

				RequestDispatcher dispatcher =
					getServletContext().getRequestDispatcher(
						"/busquedaPersonal.jsp");
				dispatcher.forward(request, response);
				return;
			}

			//busqueda de unid. organ.
			if (criterio.equals("1")) {

				session.removeAttribute("listaUO");
				
				session.removeAttribute("txtValor");
				session.setAttribute("txtValor",txtValor);
				session.removeAttribute("detalle");
			    session.setAttribute("detalle",detalle);
				
				RequestDispatcher dispatcher =
					getServletContext().getRequestDispatcher("/busquedaUO.jsp");
				dispatcher.forward(request, response);
				return;
			}

			//busqueda de categorias
			if (criterio.equals("2")) {

				session.removeAttribute("listaCategoria");

				RequestDispatcher dispatcher =
					getServletContext().getRequestDispatcher(
						"/busquedaCategoria.jsp");
				dispatcher.forward(request, response);
				return;
			}

			//busqueda de multiples trabajadores
			if (criterio.equals("3")) {
				log.debug("criterio3: "+criterio);

				String pagina = request.getParameter("pagina");

				session.removeAttribute("listaPersonalM");
                
				request.removeAttribute("pagina");
				request.setAttribute("pagina", pagina);
                
				String fechaIni = request.getParameter("fechaIni");
				String fechaFin = request.getParameter("fechaFin");
				
				session.setAttribute("fechaIni", fechaIni);
				session.setAttribute("fechaFin", fechaFin);
				
				RequestDispatcher dispatcher =
					getServletContext().getRequestDispatcher(
						"/busquedaPersonalMult.jsp");
				dispatcher.forward(request, response);
				return;
			}
			
			//JRR - 21/06/2010 - FUSION PROGRAMACION
			//busqueda de Intendencias
			if (criterio.equals("4")) {
				
				session.removeAttribute("listaUO");
				
				session.removeAttribute("txtValor");
				setAttribute(session,"txtValor",txtValor);
				session.removeAttribute("detalle");
				setAttribute(session,"detalle",detalle);
				
				forward(request,response,"/BusquedaIntend.jsp");
				return;
			}
			
			//PAS20181U230200023
			if (criterio.equals("5")) {
				String codigo = request.getParameter("destinoCodigo"); 
				String descripcion = request.getParameter("destinoDescripcion"); 
				session.setAttribute("destinoCodigo", codigo);
				session.setAttribute("destinoDescripcion", descripcion);

				session.removeAttribute("listaDiagnosticos"); 
				session.removeAttribute("txtValor");
				setAttribute(session,"txtValor",txtValor);
				session.removeAttribute("detalle");
				setAttribute(session,"detalle",detalle); 
				forward(request,response,"/busquedaCIE.jsp");
				return;
			}
			//
		}
	    catch (Exception e) {
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
	 * M�todo encargado de buscar las UO
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarUO(
		HttpServletRequest request,
		HttpServletResponse response,
		HttpSession session)
		throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			log.debug("Roles "+roles);
			
			//HashMap roles = super.getRoles(session);
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codPers);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("uoAO", bUsuario.getCodUO());//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			String criterio = request.getParameter("cmbCriterio");
			String valor = request.getParameter("txtValor");

			UtilesDelegate ud = new UtilesDelegate();
			ArrayList listaUO =
				ud.buscarUOrgan(pool_sp, criterio, valor, seguridad);

			session.removeAttribute("listaUO");
			session.setAttribute("listaUO", listaUO);

			RequestDispatcher dispatcher =
				getServletContext().getRequestDispatcher("/busquedaUO.jsp");
			dispatcher.forward(request, response);
			return;

		} catch (UtilesException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher =
				getServletContext().getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} 
	    catch (Exception e) {
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
	
	//JRR - 21/06/2010 - FUSION PROGRAMACION
	/**
	 * Mtodo encargado de buscar las Intendencias
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarIntendencias(
			HttpServletRequest request,
			HttpServletResponse response,
			HttpSession session)
	throws ServletException, IOException {
		
		try {
			
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			 String codPers = bUsuario.getNroRegistro();
			 HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			 ArrayList usrRoles = (ArrayList) roles.get("*");
			 roles = obtRoles (usrRoles);
			 
			 //HashMap roles = super.getRoles(session);
			  String uoSeg = bUsuario.getVisibilidad();
			
			
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");
			
			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codPers);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("uoAO", bUsuario.getCodUO());//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
			
			String criterio = request.getParameter("cmbCriterio");
			String valor = request.getParameter("txtValor");
			
			UtilesDelegate ud = new UtilesDelegate();
			Map params = new HashMap();
			params.put("criterio",criterio);
			params.put("valor",valor);
			params.put("seguridad",seguridad);
			List listaIntendencias =ud.buscarIntendencias(params);
			if(log.isDebugEnabled()) log.debug("en el ServletBusq");
			session.removeAttribute("listaIntendencias");
			setAttribute(session,"listaIntendencias", listaIntendencias);
			forward(request, response, "/BusquedaIntend.jsp");
			
		} catch (UtilesException e) {
			setAttribute(session,"beanErr", e.getBeanMensaje());
			forward(request, response, "/PagE.jsp");
		} 
		catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(
					"Ha ocurrido un error en la aplicacion. " + e.getMessage());
			bean.setMensajesol(
			"Por favor intente nuevamente ejecutar la opcion.");
			setAttribute(session,"beanErr", bean);
			forward(request, response, "/PagE.jsp");
		}
		
	}
//
	
	/**
	 * M�todo encargado de buscar las UO de un per�odo
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarUOPeriodo(
		HttpServletRequest request,
		HttpServletResponse response,
		HttpSession session)
		throws ServletException, IOException {

		try {

			BeanPeriodo periodo = (BeanPeriodo) session.getAttribute("periodo");
			String criterio = request.getParameter("cmbCriterio");
			String valor = request.getParameter("txtValor");

			UtilesDelegate ud = new UtilesDelegate();
			ArrayList listaUO =
				ud.buscarUOPeriodo(
						pool_sp,
					periodo.getPeriodo(),
					criterio,
					valor);

			session.removeAttribute("listaUOPeriodo");
			session.setAttribute("listaUOPeriodo", listaUO);
			
			//JRR - 04082008
			session.removeAttribute("criterio");
			session.removeAttribute("valor");
			session.setAttribute("criterio", criterio);
			session.setAttribute("valor", valor);

			RequestDispatcher dispatcher =
				getServletContext().getRequestDispatcher(
					"/registrarPeriodo.jsp");
			dispatcher.forward(request, response);
			return;

		} catch (UtilesException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher =
				getServletContext().getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}
	    catch (Exception e) {
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
	 * Metodo encargado de buscar los datos del personal
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarPersonal(
		HttpServletRequest request,
		HttpServletResponse response,
		HttpSession session)
		throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();
			//String codPers = bUsuario.getNumreg();
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
			seguridad.put("codPers", codPers);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("uoAO", bUsuario.getCodUO());//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			String criterio = request.getParameter("cmbCriterio");
			String valor = request.getParameter("txtValor");
			
			//prac-asanchez
			String apeMat = request.getParameter("txtApeMat");
			if(criterio.equals("4")){
				valor = valor.trim() + "|" + apeMat.trim();
			}

			UtilesDelegate ud = new UtilesDelegate();
			ArrayList listaPersonal =
				ud.buscarPersonal(pool_sp, criterio, valor, seguridad);

			session.removeAttribute("listaPersonal");
			session.setAttribute("listaPersonal", listaPersonal);

			RequestDispatcher dispatcher =
				getServletContext().getRequestDispatcher(
					"/busquedaPersonal.jsp");
			dispatcher.forward(request, response);
			return;

		} catch (UtilesException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher =
				getServletContext().getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} 
	    catch (Exception e) {
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
	 * M�todo encargado de buscar las categor�as
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarCategoria(
		HttpServletRequest request,
		HttpServletResponse response,
		HttpSession session)
		throws ServletException, IOException {

		try {

			String criterio = request.getParameter("cmbCriterio");
			String valor = request.getParameter("txtValor");

			UtilesDelegate ud = new UtilesDelegate();
			ArrayList listaCategoria =
				ud.buscarT99Codigo(
						pool_sp,
					criterio,
					valor,
					Constantes.CODTAB_CATEGORIA);

			session.removeAttribute("listaCategoria");
			session.setAttribute("listaCategoria", listaCategoria);

			RequestDispatcher dispatcher =
				getServletContext().getRequestDispatcher(
					"/busquedaCategoria.jsp");
			dispatcher.forward(request, response);
			return;

		} catch (UtilesException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher =
				getServletContext().getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} 
	    catch (Exception e) {
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
	 * M�todo encargado de buscar los datos de personal con selecci�n m�ltiple
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarPersonalM(
		HttpServletRequest request,
		HttpServletResponse response,
		HttpSession session)
		throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();
//			String codPers = bUsuario.getNumreg();
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
			seguridad.put("codPers", codPers);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("uoAO", bUsuario.getCodUO());//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			String criterio = request.getParameter("cmbCriterio");
			String valor = request.getParameter("txtValor");

			//prac-asanchez
			String apeMat = request.getParameter("txtApeMat");
			if(criterio.equals("4")){
				valor = valor.trim() + "|" + apeMat.trim();
			}
						
			String pagina = request.getParameter("idpagina");
			request.removeAttribute("pagina");
			request.setAttribute("pagina", pagina);
			
			UtilesDelegate ud = new UtilesDelegate();
			ArrayList listaPersonal =
				ud.buscarPersonal(pool_sp, criterio, valor, seguridad);

			session.removeAttribute("listaPersonalM");
			session.setAttribute("listaPersonalM", listaPersonal);

			RequestDispatcher dispatcher =
				getServletContext().getRequestDispatcher(
					"/busquedaPersonalMult.jsp");
			dispatcher.forward(request, response);
			return;

		} catch (UtilesException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher =
				getServletContext().getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} 
	    catch (Exception e) {
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
	 * M�todo encargado de agregar personal a la lista de selecci�n m�ltiple
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void agregarPersonal(
		HttpServletRequest request,
		HttpServletResponse response,
		HttpSession session)
		throws ServletException, IOException {

		try {

			ArrayList listaPersonal =
				(ArrayList) session.getAttribute("listaPersonalM");
			ArrayList listaTrabajadores =
				(ArrayList) session.getAttribute("listaTrabajadores");

			String[] params = request.getParameterValues("chk_opcion");

			if (params != null) {
				for (int i = params.length - 1; i >= 0; i--) {
					String indice = params[i];
					HashMap t =
						(HashMap) listaPersonal.get(Integer.parseInt(indice));

					if (!Utiles
						.estaEnLista(
							listaTrabajadores,
							(String) t.get("t02cod_pers"))) {
						listaTrabajadores.add(t);
					}
				}
			}

			String pagina = request.getParameter("idpagina");
			request.removeAttribute("pagina");
			request.setAttribute("pagina", pagina);

			session.removeAttribute("listaTrabajadores");
			session.setAttribute("listaTrabajadores", listaTrabajadores);

			RequestDispatcher dispatcher =
				getServletContext().getRequestDispatcher(
					"/busquedaPersonalMult.jsp");
			dispatcher.forward(request, response);
			return;

		} catch (UtilesException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher =
				getServletContext().getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} 
	    catch (Exception e) {
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
	 * PAS20181U230200023
	 * Metodo encargado de buscar las diagnosticos
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarDiagnosticoCIE(
		HttpServletRequest request,
		HttpServletResponse response,
		HttpSession session)
		throws ServletException, IOException {

		try {

			String criterio = request.getParameter("cmbCriterio");
			String valor = request.getParameter("txtValor"); 
		
			UtilesDelegate ud = new UtilesDelegate();
			ArrayList listaDiagnosticos = ud.buscarT5864Parametro(criterio, valor,Constantes.T5864_CODTAB_DIAG_CIE10); 
			session.removeAttribute("listaDiagnosticos");
			session.setAttribute("listaDiagnosticos", listaDiagnosticos);
		
			RequestDispatcher dispatcher =
				getServletContext().getRequestDispatcher(
					"/busquedaCIE.jsp");
			dispatcher.forward(request, response);
			return;

		} catch (UtilesException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher =
				getServletContext().getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} 
	    catch (Exception e) {
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