package pe.gob.sunat.sp.asistencia;

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

//ICAPUNAY 15/12/2010 AOM URGENTE: 44U3T10 Gestión de turnos y calificacion de procesos
import pe.gob.sunat.framework.core.pattern.DynaBean;
import pe.gob.sunat.framework.core.pattern.ServiceLocator;
import pe.gob.sunat.rrhh.utils.ConvertidorCSVaLIST;
import java.util.List;
import java.io.File;
//FIN ICAPUNAY 15/12/2010 AOM URGENTE: 44U3T10 Gestión de turnos y calificacion de procesos

import pe.gob.sunat.framework.core.servlet.ServletAbstract;
import pe.gob.sunat.sol.BeanMensaje;
//import pe.gob.sunat.sol.seguridad.acceso.jaas.AutenticacionException;
//import pe.gob.sunat.sol.seguridad.acceso.jaas.ServletAutenticacionIntranet;
//import pe.gob.sunat.sol.seguridad.acceso.portal.bean.BeanUsuario;
import pe.gob.sunat.sp.asistencia.bean.BeanTurno;
import pe.gob.sunat.sp.asistencia.bean.BeanTurnoTrabajo;
import pe.gob.sunat.sp.asistencia.ejb.delegate.MantenimientoDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.MantenimientoException;
import pe.gob.sunat.sp.asistencia.ejb.delegate.TurnoTrabajoDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.TurnoTrabajoException;
import pe.gob.sunat.tecnologia.menu.bean.UsuarioBean;
import pe.gob.sunat.utils.Constantes;

/**
 * @web.servlet name="ServletTurnoTrabajo"
 * @web.servlet-mapping url-pattern = "/asisS07Alias"
 * @web.servlet-init-param name = "pool_sp" value = "jdbc/dcsp"
 * @web.servlet-init-param name = "pool_sp_g" value = "jdbc/dgsp"
 *
 * Title: Control de Asistencia
 * Description:
 * Copyright: Copyright (c) 2004
 * Company: Sunat
 * @author cgarratt
 * @version 1.0
 * 
 * @web.env-entry name="dir_upload"
 *                type="java.lang.String"
 *                value="/data0/sip/dat/"

 */
public class ServletTurnoTrabajo extends ServletAbstract {

	private static final Logger log = Logger.getLogger(ServletTurnoTrabajo.class);
	private static String pool_sp;
	private static String pool_sp_g;
	private TurnoTrabajoDelegate ttdelegate;

	public ServletTurnoTrabajo() {
	}

	public void destroy() {
		super.destroy();
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
        pool_sp = config.getInitParameter("pool_sp");
        pool_sp_g = config.getInitParameter("pool_sp_g");
		ttdelegate=new TurnoTrabajoDelegate();
	}

	public void procesa(
		HttpServletRequest request,
		HttpServletResponse response,
		HttpSession session)
		throws ServletException, IOException {
try{

		//ICAPUNAY AOM URGENTE: 44U3T10 Gestión de turnos y calificacion de procesos	
		DynaBean dynaBean = new DynaBean(request);		
		if (log.isDebugEnabled()) log.debug("dynaBean en procesa: "+dynaBean);		
		String accion=dynaBean.getString("accion");		
		if (log.isDebugEnabled()) log.debug("Accion en procesa: "+accion);
		if (log.isDebugEnabled()) log.debug("session en procesa: "+session);		
		//FIN ICAPUNAY AOM URGENTE: 44U3T10 Gestión de turnos y calificacion de procesos
		

		if (session == null) {
			RequestDispatcher dispatcher =
				getServletContext().getRequestDispatcher("/PagSession.jsp");
			dispatcher.forward(request, response);
			return;
		}

		UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
		
		//ICAPUNAY AOM URGENTE: 44U3T10 Gestión de turnos y calificacion de procesos
		if (log.isDebugEnabled()) log.debug("UsuarioBean en procesa: "+bUsuario);		
		if (log.isDebugEnabled()) log.debug("busuario en procesa: "+bUsuario.getLogin());
		//FIN ICAPUNAY AOM URGENTE: 44U3T10 Gestión de turnos y calificacion de procesos
		
		if (bUsuario!=null){
			NDC.push(bUsuario.getLogin().concat("-").concat(bUsuario.getTicket()));
		   		  		    
			//ICAPUNAY AOM URGENTE: 44U3T10 Gestión de turnos y calificacion de procesos			
	        if (dynaBean.isMultipart() && !dynaBean.isMultipartOK()){	            	
	        		 BeanMensaje beanM = new BeanMensaje();
	                 beanM.setError(true);
	                 beanM.setMensajeerror("El archivo no se puede adjuntar porque su tamaño supera al máximo permitido 64KB.");
	                 beanM.setMensajesol("Intente adjuntando otro archivo de menor tamaño");
	                 session.setAttribute("beanErr", beanM);	                 
	                 //forward(request,response,"/registrarTurnoMasivo.jsp"+"?idSession="+System.currentTimeMillis());	                  
	                 RequestDispatcher dispatcher =getServletContext().getRequestDispatcher("/PagE.jsp");
	     			 dispatcher.forward(request, response);
	                 return;	            		
	        }			
            //FIN ICAPUNAY AOM URGENTE: 44U3T10 Gestión de turnos y calificacion de procesos
		}	
		if (accion.equals("cargarTurnosTrabajo")) {
			cargarTurnosTrabajo(request, response, session);
		}
		if (accion.equals("buscarTurnosTrabajo")) {
			buscarTurnosTrabajo(request, response, session);
		}
		if (accion.equals("eliminarTurnosTrabajo")) {
			eliminarTurnosTrabajo(request, response, session);
		}
		if (accion.equals("registrarTurnoTrabajo")) {
			registrarTurnosTrabajo(request, response, session);
		}
		if (accion.equals("cargarDatosTurnoTrabajo")) {
			cargarDatosTurnoTrabajo(request, response, session);
		}
		if (accion.equals("modificarTurnoTrabajo")) {
			modificarTurnoTrabajo(request, response, session);
		}
		if (accion.equals("cargarTurnosOperativos")) {
			cargarTurnosOperativos(request, response, session);
		}
		if (accion.equals("eliminarTrabOperat")) {
			eliminarTrabOperat(request, response, session);
		}
		if (accion.equals("eliminarDetalle")) {
			eliminarDetallePlanificacion(request, response, session);
		}
		if (accion.equals("agregarDetallePlanificacion")) {
			agregarDetallePlanificacion(request, response, session);
		}
		if (accion.equals("procesarOperativo")) {
			procesarOperativo(request, response, session);
		}		
		//ICAPUNAY 15/12/2010 AOM URGENTE: 44U3T10 Gestión de turnos y calificacion de procesos
		if (accion.equals("cargarUUOO")) {
			if (log.isDebugEnabled()) log.debug("session en cargarUUOO: "+session);
			if (log.isDebugEnabled()) log.debug("accion cargarUUOO: "+accion);
			cargarUUOO(request, response, session);
		}	
		if (accion.equals("planificarTurnosMasivos")) {
			if (log.isDebugEnabled()) log.debug("session en planificarTurnosMasivos: "+session);
			if (log.isDebugEnabled()) log.debug("accion planificarTurnosMasivos: "+accion);
			planificarTurnosMasivos(request, response, session,dynaBean);
		}	
		//FIN ICAPUNAY 15/12/2010 AOM URGENTE: 44U3T10 Gestión de turnos y calificacion de procesos

}catch(Exception e){
	log.error("*** Error en Exception ***", e);	  
}finally{
	  NDC.pop();
	  NDC.remove();
}
	}

	/**
	 * Metodo encargado de buscar los turnos de trabajo
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarTurnosTrabajo(
		HttpServletRequest request,
		HttpServletResponse response,
		HttpSession session)
		throws ServletException, IOException {

		try {

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

			String fechaIni = request.getParameter("fechaIni");
			String fechaFin = request.getParameter("fechaFin");
			String cmbTurno = request.getParameter("cmbTurno");
			String criterio = request.getParameter("cmbCriterio");
			String valor =
				request.getParameter("txtValor") != null
					? request.getParameter("txtValor")
					: "";

			String codTurno = "";
			if (!cmbTurno.trim().equals("-1")) {
				ArrayList turnos =
					(ArrayList) session.getAttribute("listaTurnos");
				BeanTurno turno =
					(BeanTurno) turnos.get(Integer.parseInt(cmbTurno));
				codTurno = turno.getCodTurno();
			}

			TurnoTrabajoDelegate ttd = new TurnoTrabajoDelegate();
			ArrayList listaTurnos =
				ttd.buscarTurnosTrabajo(
					pool_sp,
					fechaIni,
					fechaFin,
					codTurno,
					criterio,
					valor,
					seguridad);

			session.removeAttribute("listaTurnosTrabajo");
			log.debug("Lista TT:"+listaTurnos.toString());
			session.setAttribute("listaTurnosTrabajo", listaTurnos);

			RequestDispatcher dispatcher =
				getServletContext().getRequestDispatcher(
					"/mantenimientoTurnoTrabajo.jsp");
			dispatcher.forward(request, response);
			return;
		} catch (TurnoTrabajoException e) {
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
	 * M�todo encargado de cargar los turnos de trabajo
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarTurnosTrabajo(
		HttpServletRequest request,
		HttpServletResponse response,
		HttpSession session)
		throws ServletException, IOException {

		try {

			MantenimientoDelegate md = new MantenimientoDelegate();
			ArrayList turnos = md.buscarTurnos(pool_sp, "", Constantes.ACTIVO);

			session.removeAttribute("listaTurnosTrabajo");
			session.removeAttribute("listaTurnos");
			session.setAttribute("listaTurnos", turnos);

			RequestDispatcher dispatcher =
				getServletContext().getRequestDispatcher(
					"/mantenimientoTurnoTrabajo.jsp");
			dispatcher.forward(request, response);
			return;

		} catch (MantenimientoException e) {
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
	 * Metodo encargado de eliminar los turnos de trabajo
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void eliminarTurnosTrabajo(
		HttpServletRequest request,
		HttpServletResponse response,
		HttpSession session)
		throws ServletException, IOException {

		try {

			ArrayList turnos =
				(ArrayList) session.getAttribute("listaTurnosTrabajo");
			String[] params = request.getParameterValues("chk_opcion");

			TurnoTrabajoDelegate ttd = new TurnoTrabajoDelegate();
			turnos = ttd.eliminarTurnosTrabajo(params, turnos);

			session.removeAttribute("listaTurnosTrabajo");
			session.setAttribute("listaTurnosTrabajo", turnos);

			RequestDispatcher dispatcher =
				getServletContext().getRequestDispatcher(
					"/mantenimientoTurnoTrabajo.jsp");
			dispatcher.forward(request, response);
			return;
		} catch (TurnoTrabajoException e) {
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
	 * Metodo encargado de registrar los turnos de trabajo
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void registrarTurnosTrabajo(
		HttpServletRequest request,
		HttpServletResponse response,
		HttpSession session)
		throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();//.getNumreg();
			String usuario = bUsuario.getLogin();
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			if (log.isDebugEnabled()) log.debug("Roles "+roles);			
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			if (log.isDebugEnabled()) log.debug("Roles "+roles);			
			
		    String uoSeg = bUsuario.getVisibilidad();
		    if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codPers);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			String cmbCriterio = request.getParameter("cmbCriterio");
			String txtValor = request.getParameter("txtValor");
			String cmbTurno = request.getParameter("cmbTurno");
			String fechaIni = request.getParameter("fechaIni");
			String fechaFin = request.getParameter("fechaFin");
			String obsSustento=request.getParameter("txtObsSustento").toUpperCase();

			boolean cruce = request.getParameter("cmbCruce").equals("1");

			ArrayList turnos = (ArrayList) session.getAttribute("listaTurnos");
			BeanTurno turno =
				(BeanTurno) turnos.get(Integer.parseInt(cmbTurno));

			TurnoTrabajoDelegate ttd = new TurnoTrabajoDelegate();
			String res =
				ttd.registrarTurnoTrabajo(
					pool_sp_g,
					cmbCriterio,
					txtValor,
					turno.getCodTurno(),
					fechaIni,
					fechaFin,
					obsSustento,
					usuario,
					Constantes.INACTIVO,
					cruce,
					codPers,
					seguridad);

			if (res.equals(Constantes.OK)) {
				session.removeAttribute("listaTurnosTrabajo");

				request.setAttribute(
					"nomProceso",
					"REGISTRO DE TURNO DE TRABAJO");
				RequestDispatcher dispatcher =
					getServletContext().getRequestDispatcher(
						"/resultadoProceso.jsp");
				dispatcher.forward(request, response);
				return;

			} else {
				throw new Exception(res);
			}

		} catch (TurnoTrabajoException e) {
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
	 * Metodo encargado de cargar los turnos de trabajo
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarDatosTurnoTrabajo(
		HttpServletRequest request,
		HttpServletResponse response,
		HttpSession session)
		throws ServletException, IOException {

		try {

			ArrayList lista =
				(ArrayList) session.getAttribute("listaTurnosTrabajo");
			String indice = request.getParameter("indice");
			BeanTurnoTrabajo turno =
				(BeanTurnoTrabajo) lista.get(Integer.parseInt(indice));

			session.removeAttribute("turnoTrabajo");
			session.setAttribute("turnoTrabajo", turno);

			RequestDispatcher dispatcher =
				getServletContext().getRequestDispatcher(
					"/registrarTurnoTrabajo.jsp");
			dispatcher.forward(request, response);
			return;
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
	 * Metodo encargado de modificar los turnos de trabajo
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void modificarTurnoTrabajo(
		HttpServletRequest request,
		HttpServletResponse response,
		HttpSession session)
		throws ServletException, IOException {

		try {

			String fechaFin = request.getParameter("fechaFin") + " 00:00:00";
			String obsSustento=request.getParameter("txtObsSustento").toUpperCase();
			String estado = request.getParameter("estado");
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			
			String usuario = bUsuario.getLogin();
			
			boolean cruce = request.getParameter("cmbCruce").equals("1");

			BeanTurnoTrabajo turnoTrab =
				(BeanTurnoTrabajo) session.getAttribute("turnoTrabajo");

			TurnoTrabajoDelegate ttd = new TurnoTrabajoDelegate();

			//ASANCHEZZ 20100617
			String codiPersProceso = bUsuario.getNroRegistro();
			/*
			String res =
				ttd.modificarTurnoTrabajo(
					pool_sp_g,
					turnoTrab.getCodPers(),
					turnoTrab.getCodUOrg(),
					turnoTrab.getTurno(),
					turnoTrab.getFechaIni(),
					fechaFin,
					estado,
					usuario,
					cruce);
			*/
			Map mapa = new HashMap();
			mapa.put("dbpool", pool_sp_g);
			mapa.put("codPers", turnoTrab.getCodPers());
			mapa.put("codUO", turnoTrab.getCodUOrg());
			mapa.put("turno", turnoTrab.getTurno());
			mapa.put("fechaIni", turnoTrab.getFechaIni());
			mapa.put("fechaFin", fechaFin);
			mapa.put("obsSustento", obsSustento);
			mapa.put("estado", estado);
			mapa.put("usuario", usuario);
			mapa.put("vCruce", cruce == true ? "1" : "0");
			mapa.put("codiPersProceso", codiPersProceso);
			
			String res = ttd.modificarTurnoTrabajo(mapa);
			//FIN

			if (res.equals(Constantes.OK)) {

				session.removeAttribute("listaTurnosTrabajo");

				RequestDispatcher dispatcher =
					getServletContext().getRequestDispatcher(
						"/mantenimientoTurnoTrabajo.jsp");
				dispatcher.forward(request, response);
				return;
			} else {
				throw new Exception(res);
			}

		} catch (TurnoTrabajoException e) {
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
	 * Metodo encargado de cargar la pagina de turnos operativos
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarTurnosOperativos(
		HttpServletRequest request,
		HttpServletResponse response,
		HttpSession session)
		throws ServletException, IOException {

		try {

			MantenimientoDelegate md = new MantenimientoDelegate();
			ArrayList turnos = md.buscarTurnos(pool_sp, "", Constantes.ACTIVO);

			session.removeAttribute("listaTurnos");
			session.setAttribute("listaTurnos", turnos);

			session.removeAttribute("listaTrabajadores");
			session.removeAttribute("detallePlani");
			
			session.setAttribute("fechaIni","");
			session.setAttribute("fechaFin","");
			
			RequestDispatcher dispatcher =
				getServletContext().getRequestDispatcher(
					"/planifTurnoOperativo.jsp?tamanioPagina=5");
			dispatcher.forward(request, response);
			return;

		} catch (MantenimientoException e) {
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
	 * Metodo encargado de eliminar los trabajadores de la lista de turnos de operativos
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void eliminarTrabOperat(
		HttpServletRequest request,
		HttpServletResponse response,
		HttpSession session)
		throws ServletException, IOException {

		try {

			ArrayList listaTrabajadores =
				(ArrayList) session.getAttribute("listaTrabajadores");
			String[] params = request.getParameterValues("chk_opcion");

			if (params != null) {
				for (int i = params.length - 1; i >= 0; i--) {
					String indice = params[i];
					HashMap t =
						(HashMap) listaTrabajadores.get(
							Integer.parseInt(indice));
					listaTrabajadores.remove(t);
				}
			}

			session.removeAttribute("listaTrabajadores");
			session.setAttribute("listaTrabajadores", listaTrabajadores);

			RequestDispatcher dispatcher =
				getServletContext().getRequestDispatcher(
					"/planifTurnoOperativo.jsp?tamanioPagina=5");
			dispatcher.forward(request, response);
			return;
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
	 * Metodo encargado de eliminar un detalle de la planificacion operativa
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void eliminarDetallePlanificacion(
		HttpServletRequest request,
		HttpServletResponse response,
		HttpSession session)
		throws ServletException, IOException {

		try {

			ArrayList detallePlani =
				(ArrayList) session.getAttribute("detallePlani");
			String[] params = request.getParameterValues("chk_detalle");

			if (params != null) {
				for (int i = params.length - 1; i >= 0; i--) {
					String indice = params[i];
					BeanTurnoTrabajo det =
						(BeanTurnoTrabajo) detallePlani.get(
							Integer.parseInt(indice));
					detallePlani.remove(det);
				}
			}

			session.removeAttribute("detallePlani");
			session.setAttribute("detallePlani", detallePlani);

			RequestDispatcher dispatcher =
				getServletContext().getRequestDispatcher(
					"/planifTurnoOperativo.jsp?tamanioPagina=5");
			dispatcher.forward(request, response);
			return;
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
	 * Metodo encargado de agregar un detalle a la planificacion operativa
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void agregarDetallePlanificacion(
		HttpServletRequest request,
		HttpServletResponse response,
		HttpSession session)
		throws ServletException, IOException {

		try {

			ArrayList listaTurnos =
				(ArrayList) session.getAttribute("listaTurnos");
			ArrayList detallePlani =
				(ArrayList) session.getAttribute("detallePlani");

			String codigo = "DESCANSO";
			String detalle = "DESCANSO";
			String tipo = request.getParameter("tipo");
			String duracion = request.getParameter("txt_duracion");

			if (tipo.equals("turno")) {
				String indice = request.getParameter("cmbTurno");
				BeanTurno t =
					(BeanTurno) listaTurnos.get(Integer.parseInt(indice));
				detalle = t.getDesTurno();
				codigo = t.getCodTurno();
			}

			BeanTurnoTrabajo det = new BeanTurnoTrabajo();
			det.setTurno(codigo);
			det.setDescTurno(detalle);
			det.setDuracion(Integer.parseInt(duracion.trim()));
			detallePlani.add(det);

			session.removeAttribute("detallePlani");
			session.setAttribute("detallePlani", detallePlani);

			RequestDispatcher dispatcher =
				getServletContext().getRequestDispatcher(
					"/detallePlanifOperativo.jsp");
			dispatcher.forward(request, response);
			return;
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
	 * Metodo encargado de procesar la planificacion operativa
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void procesarOperativo(
		HttpServletRequest request,
		HttpServletResponse response,
		HttpSession session)
		throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();//.getNumreg();
			String usuario = bUsuario.getLogin();

			ArrayList listaTrabajadores =
				(ArrayList) session.getAttribute("listaTrabajadores");
			ArrayList detallePlani =
				(ArrayList) session.getAttribute("detallePlani");
			
			String fechaIni = request.getParameter("fechaIni");
			String fechaFin = request.getParameter("fechaFin");
		
			boolean bSabado =
				request.getParameter("chk_sabado") != null
					? request.getParameter("chk_sabado").equals("true")
					: false;
			boolean bDomingo =
				request.getParameter("chk_domingo") != null
					? request.getParameter("chk_domingo").equals("true")
					: false;
			boolean bFeriado =
				request.getParameter("chk_feriado") != null
					? request.getParameter("chk_feriado").equals("true")
					: false;

			TurnoTrabajoDelegate ttd = new TurnoTrabajoDelegate();
			ttd.procesarOperativo(
				pool_sp_g,
				listaTrabajadores,
				detallePlani,
				fechaIni,
				fechaFin,
				bSabado,
				bDomingo,
				bFeriado,
				codPers,
				usuario);

			request.setAttribute("nomProceso", "PLANIFICACI&Oacute;N OPERATIVA");
			RequestDispatcher dispatcher =
				getServletContext().getRequestDispatcher(
					"/resultadoProceso.jsp");
			dispatcher.forward(request, response);
			return;

		} catch (TurnoTrabajoException e) {
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
	
	//ICAPUNAY AOM URGENTE: 44U3T10 Gestión de turnos y calificacion de procesos
	/**
	 * Metodo encargado de listar las UUOO
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param session HttpSession
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarUUOO(
		HttpServletRequest request,
		HttpServletResponse response,
		HttpSession session)
		throws ServletException, IOException {

		try {			
						
			/* ICAPUNAY 27/04/2011- REQUERIDO X RUBEN LAZARTE 
			List unidadesOrg = ttdelegate.buscarUUOOActivos("","", "1");
			session.removeAttribute("listaUUOO");			
			setAttribute(session,"listaUUOO", unidadesOrg);*/	

			forward(request,response,"/registrarTurnoMasivo.jsp"+"?idSession="+System.currentTimeMillis());
			
			return;

		/* ICAPUNAY 27/04/2011- REQUERIDO X RUBEN LAZARTE 
		} catch (TurnoTrabajoException e) {
			
			forwardError(request,response,e.getMessage());
			
		}*/ 
		}catch (Exception e) {
			
			//forwardError(request,response,e.getMessage());
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
	 * Metodo encargado de validar y cargar los turnos de trabajo masivos
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param session HttpSession
	 * @param dbean DynaBean
	 * @throws ServletException
	 * @throws IOException
	 */
	private void planificarTurnosMasivos(
		HttpServletRequest request,
		HttpServletResponse response,
		HttpSession session,DynaBean dbean)
		throws ServletException, IOException {

		try {		
			
			int tamanio=0; //ICAPUNAY MODIFICADO EL 16/05/2011
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();
			String usuario = bUsuario.getLogin();
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); 
			if (log.isDebugEnabled()) log.debug("Roles1 actuales: "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			if (log.isDebugEnabled()) log.debug("Roles2 actuales: "+roles);
			
		    String uoSeg = bUsuario.getVisibilidad();
		    if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");
		    
			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codPers);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
			
			if (log.isDebugEnabled()) log.debug("seguridad planificarTurnosMasivos: " + seguridad);				
				
			if (log.isDebugEnabled()) log.debug("Es Multipart?: "+dbean.isMultipart());		
							
				String dir_upload = ServiceLocator.getInstance().getString("java:comp/env/dir_upload");				
				String ruta_arch=dir_upload+dbean.getString("archivo-filter-filename");
				
				if(log.isDebugEnabled()) log.debug("dir_upload " + dir_upload);
				if(log.isDebugEnabled()) log.debug("ruta_arch " + ruta_arch);
				
				//ICAPUNAY MODIFICADO EL 16/05/2011
				String size_arch=dbean.getString("archivo-size");
				if(log.isDebugEnabled()) log.debug("size_arch " + size_arch);
				tamanio = Integer.parseInt(size_arch);
				if(log.isDebugEnabled()) log.debug("tamanio archivo " + String.valueOf(tamanio));
				if(tamanio>0 && tamanio<=65536){ //validando que el archivo sea menor o igual a 64 KB (65536 bytes)
					File archivoCsv = new File(ruta_arch);				
					ConvertidorCSVaLIST convertidor= new ConvertidorCSVaLIST();
					List archivo=convertidor.convertir(archivoCsv);	
					
					if(log.isDebugEnabled()) log.debug("archivo " + archivo);
					
					dbean.put("codPers", codPers);
					dbean.put("usuario", usuario);
					dbean.put("onomastico", Constantes.INACTIVO);
					dbean.put("seguridad", seguridad);
					dbean.put("archivo", archivo);
					dbean.put("dbpool", pool_sp_g);				
				
					if (log.isDebugEnabled()) log.debug("Ingresando a planificarTurnosMasivos-delegate");				
					
					String resultado = ttdelegate.planificarTurnosMasivos(dbean);				
					
					if (log.isDebugEnabled()) log.debug("Saliendo de planificarTurnosMasivos-delegate");
					if (log.isDebugEnabled()) log.debug("Estado Planificacion: "+resultado);
					
					if (resultado.equals(Constantes.OK)) {	
						
						request.setAttribute("nomProceso","PLANIFICAR TURNOS MASIVOS");					
						forward(request,response,"/resultadoProceso.jsp"+"?idSession="+System.currentTimeMillis());
						return;
						
					}else {						
						throw new Exception(resultado);
					}				
				}else{
					
					if (log.isDebugEnabled()) log.debug("Error en tamaño de archivo");
					throw new Exception("Sólo puede subir archivos de hasta un tamaño de 64 KB o 2000 registros. Por favor intente adjuntar otro archivo de menor tamaño.");
				}
				//				

		} catch (TurnoTrabajoException e) {			
			if (log.isDebugEnabled()) log.debug("Error en TurnoTrabajoException: "+e.getMessage());
			forwardError(request,response,e.getMessage());
			
		} catch (Exception e) {			
			if (log.isDebugEnabled()) log.debug("Error en Exception: "+e.getMessage());
			//forwardError(request,response,e.getMessage());
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. " + e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher =getServletContext().getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
			
		}

	}
	//FIN ICAPUNAY AOM URGENTE: 44U3T10 Gestión de turnos y calificacion de procesos

}