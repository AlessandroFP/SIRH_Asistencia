package pe.gob.sunat.rrhh.formativa;

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

import pe.gob.sunat.framework.core.servlet.ServletAbstract;
//import pe.gob.sunat.framework.util.cache.dao.ParamDAO;
import pe.gob.sunat.sol.BeanMensaje;
import pe.gob.sunat.framework.core.pattern.DynaBean;

import pe.gob.sunat.rrhh.formativa.ejb.delegate.AsisForDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.AsistenciaDelegate;
import pe.gob.sunat.tecnologia.menu.bean.UsuarioBean;
import pe.gob.sunat.utils.Constantes;

/**
 * @web.servlet name="AsisForServlet"
 * @web.servlet-mapping url-pattern = "/asisS777Alias"
 * @web.servlet-init-param name = "pool_sp" value = "jdbc/dcsp"
 * @web.servlet-init-param name = "pgsp" value = "jdbc/dgsp"
 * @web.ejb-ref name="AsisForFacadeEJB"
 *              type="session"
 *              home="pe.gob.sunat.rrhh.formativa.ejb.AsisForFacadeHome"
 *              remote="pe.gob.sunat.rrhh.formativa.ejb.AsisForFacadeRemote"
 *              link="asistencia/ejbrrhh-asisforfacade.jar#AsisForFacadeEJB"
 *  
 * <p>Title: AsisForServlet</p>
 * <p>Description: Servlet que controla la Asistencia Formativa</p>
 * <p>Fecha: 09-jul-2008 15:14:30</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: SUNAT</p>
 * @author EBENAVID
 * @version 2.0
 *  
 */
public class AsisForServlet extends ServletAbstract {

	private static final Logger log = Logger.getLogger(AsisForServlet.class);

	private static String pool_sp;
	private static String pgsp;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		pool_sp = config.getInitParameter("pool_sp");
		pgsp = config.getInitParameter("pgsp");
		//cargarParametros();
	}

	public void procesa(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
			String accion = request.getParameter("accion");
			try {
				UsuarioBean bUsuario = (UsuarioBean)session.getAttribute("usuarioBean");
      
				if (bUsuario!=null){
					NDC.push(bUsuario.getLogin().concat("-").concat(bUsuario.getTicket()));
				} else {
					NDC.push("usuario no logueado".concat("-").concat("..."));
				}
      
				if (accion == null) {
					accion = "init";
				}else{
					if (session == null) {
						forward(request, response, "/PagM.jsp");					
						return;
					}
				}
			//log.debug("Pool : " + pool_sp);
			//log.debug("Accion : " + accion);

			log.info("Accion : " + accion);
			DynaBean dbean = new DynaBean(request);

			if (accion.equals("cargarMarcaciones")) {
				cargarMarcaciones(request, response, session,dbean, bUsuario);
			}
			if (accion.equals("generarMarcaciones")) {
				generarMarcaciones(request, response, session,dbean, bUsuario);
			}
		} catch (Exception e) {
			log.error("*** Error ***", e);
		} finally {
			NDC.pop();
			NDC.remove();
		}
	}

	/**
	 * Metodo encargado de cargar la pagina del reporte de marcaciones
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarMarcaciones(HttpServletRequest request, HttpServletResponse response, HttpSession session, DynaBean dbean, UsuarioBean usuario)
			throws ServletException, IOException {

		try {
			//log.debug("Pool : " + pool_sp);
			//UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = usuario.getNroRegistro();
			//log.debug("Codigo Persona:" + codPers);
			HashMap roles = (HashMap)usuario.getMap().get("roles");
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles(usrRoles);
			String codUO = usuario.getCodUO();
			String uoSeg = usuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null)
				uoSeg = uoSeg.substring(0, 2).concat("%");
			//log.debug("Roles:" + roles);
			HashMap seguridad = new HashMap();
			seguridad.put("dbpool", pool_sp);
			seguridad.put("roles", roles);
			seguridad.put("codPers", codPers);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("codUO", codUO);
			seguridad.put("codOpcion", Constantes.DELEGA_PAPELETAS);
			//log.debug("Seguridad:" + seguridad);
			AsistenciaDelegate ad = new AsistenciaDelegate();
			// MM es Reporte de Marcaciones
			if ((ad.esSupervisor(pool_sp, codPers, "MM", codUO)) || (ad.esJefeEncargadoDelegado(seguridad))
					|| (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null)
					|| (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null)) {
				session.removeAttribute("detalleReporte");
				forward(request, response, "/reporteMarcacionesFormativa.jsp");

				//RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/reporteMarcacionesFormativa.jsp");
				//dispatcher.forward(request, response);
				return;
			} else {
				throw new Exception("Usted no se encuentra habilitado para ejecutar esta opcion.");
			}
		} catch (Exception e) {
			log.error("**** Error ****", e);
			this.forwardError(request,response,e.getMessage());
		}
	}

	/**
	 * Metodo encargado de generar el reporte de marcaciones
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void generarMarcaciones(HttpServletRequest request, HttpServletResponse response, HttpSession session, DynaBean dbean, UsuarioBean bUsuario)
			throws ServletException, IOException {

		try {
			//UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();
			String usuario = bUsuario.getLogin();
			HashMap roles = (HashMap) bUsuario.getMap().get("roles");
			//log.debug("Roles " + roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles(usrRoles);
			//log.debug("Roles " + roles);

			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null)
				uoSeg = uoSeg.substring(0, 2).concat("%");

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codigo);
			seguridad.put("uoSeg", uoSeg);

			String criterio = dbean.getString("cmbCriterio");//request.getParameter("cmbCriterio");
			String valor = dbean.getString("txtValor");//request.getParameter("txtValor");

			String fechaIni = dbean.getString("fechaIni");//request.getParameter("fechaIni");
			String fechaFin = dbean.getString("fechaFin");//request.getParameter("fechaFin");
			boolean repetir = dbean.getString("repetir").trim().equals("true");//request.getParameter("repetir").toString().trim().equals("true");
			
			//log.debug("Antes de Instanciar Delegate.");
			AsisForDelegate afd = new AsisForDelegate();
			//log.debug("Despues de Instanciar Delegate.");
			String tipoR = dbean.getString("tipo") != null ? dbean.getString("tipo") : "reporte";
			//String tipoR = request.getParameter("tipo") != null ? request.getParameter("tipo") : "reporte";
			//log.debug("tipoR: " + tipoR);
			Map params = new HashMap();
			params.put("fechaIni", fechaIni);
			params.put("fechaFin", fechaFin);
			params.put("criterio", criterio);
			params.put("valor", valor);
			params.put("seguridad", seguridad);
			params.put("codPers", codigo);
			params.put("usuario", usuario);
			params.put("dbpool", pgsp);

			if (tipoR.equals("imprimir")) {
				afd.masivoMarcaciones(params);
			} else {
				if (!repetir) {
					//log.debug("En Repetir");
					//log.debug("Antes de: afd.marcaciones(params)");
					ArrayList detalleReporte = afd.marcaciones(params);
					//log.debug("Despues de: afd.marcaciones(params)");
					session.removeAttribute("detalleReporte");
					session.setAttribute("detalleReporte", detalleReporte);
				}
			}

			if (tipoR.equals("reporte")) {
				//log.debug("En Roporte");
				request.setAttribute("criterio", criterio);
				request.setAttribute("valor", valor);
				request.setAttribute("fechaIni", fechaIni);
				request.setAttribute("fechaFin", fechaFin);
				//log.debug("Antes del RequestDispatcher.");
				forward(request, response, "/reporteMarcacionesFormativaM.jsp");

				//RequestDispatcher dispatcher = getServletContext()
				//		.getRequestDispatcher("/reporteMarcacionesFormativaM.jsp");
				//dispatcher.forward(request, response);
				//log.debug("Despues del RequestDispatcher.");
				return;
			}

			if (tipoR.equals("excel")) {
				request.setAttribute("fechaIniExcel", fechaIni);
				request.setAttribute("fechaFinExcel", fechaFin);
				forward(request, response, "/excelMarcacionesFormativa.jsp");
				//RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/excelMarcacionesFormativa.jsp");
				//dispatcher.forward(request, response);
				return;
			}

			if (tipoR.equals("imprimir")) {
				request.setAttribute("nomReporte", "REPORTE DE MARCACIONES DE MODALIDAD FORMATIVA");
				forward(request, response, "/resultadoReporte.jsp");
				//RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/resultadoReporte.jsp");
				//dispatcher.forward(request, response);
				return;
			}
		} catch (ServletException e) {
			//log.debug("En ServletException: " + e);
			log.error("**** Error ****", e);
			this.forwardError(request,response,e.getMessage());
		} catch (Exception e) {
			log.error("**** Error ****", e);
			this.forwardError(request,response,e.getMessage());
		}
	}

	/**
	 * Metodo que se encarga de convertir los ROles de HashMap a ArrayList
	 * 
	 * @param roles
	 */
	public static HashMap obtRoles(ArrayList roles) throws ServletException, IOException {
		HashMap res = new HashMap();
		if (roles != null && roles.size() > 0) {
			String tmpRol;
			for (int i = 0; i < roles.size(); i++) {
				tmpRol = (String) roles.get(i);
				res.put(tmpRol, tmpRol);
			}
		}
		return res;
	}
	
	/**
	 * Metodo encargado de cargar los objetos a memoria
	 * 
	 * @throws ServletException
	 * @throws IOException
	 */
	/*private void cargarParametros() throws ServletException {
		try {
			ParamDAO paramDAO = new ParamDAO();
			
			//SP
			//Intendencias
			String query = "SELECT t99codigo, t99descrip FROM t99codigos WHERE t99cod_tab = '583' ";			
			paramDAO.cargar(query, pool_sp, "modalidades");
			
		} catch (Exception e) {
			log.error("error al cargar Cache:"+e);
		}
	}	*/
}