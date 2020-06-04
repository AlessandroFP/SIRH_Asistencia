package pe.gob.sunat.sp.asistencia;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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

import pe.gob.sunat.framework.core.bean.MensajeBean;
import pe.gob.sunat.framework.core.ejb.DelegateException;
import pe.gob.sunat.framework.core.pattern.DynaBean;
import pe.gob.sunat.framework.core.pattern.ServiceLocator;
import pe.gob.sunat.framework.core.servlet.ServletAbstract;
import pe.gob.sunat.framework.util.date.FechaBean;//ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA
import pe.gob.sunat.rrhh.asistencia.dao.T1276DAO;
import pe.gob.sunat.rrhh.utils.FileConverter;
import pe.gob.sunat.sol.BeanMensaje;
//import pe.gob.sunat.sol.seguridad.acceso.jaas.AutenticacionException;
//import pe.gob.sunat.sol.seguridad.acceso.jaas.ServletAutenticacionIntranet;
//import pe.gob.sunat.sol.seguridad.acceso.portal.bean.BeanUsuario;
import pe.gob.sunat.sp.asistencia.bean.BeanPeriodo;
import pe.gob.sunat.sp.asistencia.bean.BeanTipoMovimiento;
import pe.gob.sunat.sp.asistencia.bean.BeanTipoReloj;
import pe.gob.sunat.sp.asistencia.bean.BeanTurno;
import pe.gob.sunat.sp.asistencia.ejb.delegate.AsistenciaDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.MantenimientoDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.MantenimientoException;
import pe.gob.sunat.sp.asistencia.ejb.delegate.ReporteDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.SolicitudDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.UtilesDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.UtilesException;
import pe.gob.sunat.sp.asistencia.ejb.delegate.VacacionDelegate;
import pe.gob.sunat.sp.bean.BeanT12;
import pe.gob.sunat.sp.bean.BeanT99;
import pe.gob.sunat.sp.dao.T02DAO;
import pe.gob.sunat.sp.dao.T12DAO;
import pe.gob.sunat.tecnologia.menu.bean.UsuarioBean;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;
import pe.gob.sunat.utils.web.MessagesWeb;

/**
 * @web.servlet name="ServletMantenimiento"
 * @web.servlet-mapping url-pattern = "/asisS03Alias"
 * @web.servlet-init-param name = "pool_sp" value = "jdbc/dcsp"
 * @web.servlet-init-param name = "pool_sp_g" value = "jdbc/dgsp"
 * @web.servlet-init-param name = "pool_sa" value = "jdbc/dcsa" 
 *
 * Title: Control de Asistencia
 * Description:
 * Copyright: Copyright (c) 2004
 * Company: Sunat
 * @author cgarratt
 * @version 1.0
 * 
 * 
 */
public class ServletMantenimiento extends ServletAbstract {


	private static final Logger log = Logger.getLogger(ServletMantenimiento.class);
	private static String pool_sp;
	private static String pool_sp_g;
	private static String pool_sa;
	MensajeBean bean= null;
	MessagesWeb messagesWeb;

	public ServletMantenimiento() {
	}

	public void destroy() {
		super.destroy();
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
        pool_sp = config.getInitParameter("pool_sp");
        pool_sp_g = config.getInitParameter("pool_sp_g");
        pool_sa = config.getInitParameter("pool_sa");
	}

	public void procesa(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try{
			
			if (session == null) {
				RequestDispatcher dispatcher =
					getServletContext().getRequestDispatcher("/PagSession.jsp");
				dispatcher.forward(request, response);
				return;
			}
			
			//Permite gestionar los mensajes web que se mostrarán al usuario.
			messagesWeb = new MessagesWeb(session);
			
			String accion = "";
			DynaBean dynaBean = null;
			try{
				dynaBean = new DynaBean(request);		
				accion=dynaBean.getString("accion");
				if (log.isDebugEnabled()) log.debug("dynaBean en ServletMantenimiento: "+dynaBean);
			}catch(Exception e){
				log.error("ERROR: No se supero la validación de los filtros.:::"+e.getMessage());
				BeanMensaje beanM = new BeanMensaje();
				beanM.setError(true);
				beanM.setMensajeerror("Se produjo un error al procesar la solicitud.<br /> Si estaba cargando un archivo, es probable que su peso es mayor al máximo permitido.");
				beanM.setMensajesol("Verifique los datos ingresados y vuelva a intentarlo.");
				session.setAttribute("beanErr", beanM);           
				RequestDispatcher dispatcher =getServletContext().getRequestDispatcher("/PagE.jsp");
				 dispatcher.forward(request, response);
				return;				
			}
		
		if(accion.equals("")){
			accion = request.getParameter("accion");
		}
		
		//String accion = request.getParameter("accion");

		UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
		if (bUsuario!=null){
			NDC.push(bUsuario.getLogin().concat("-").concat(bUsuario.getTicket()));
			
	        if (dynaBean.isMultipart() && !dynaBean.isMultipartOK()){	            	
				BeanMensaje beanM = new BeanMensaje();
				beanM.setError(true);
				beanM.setMensajeerror("El archivo no se puede adjuntar porque su tamaño supera al máximo permitido 64KB.");
				beanM.setMensajesol("Intente adjuntando otro archivo de menor tamañoo");
				session.setAttribute("beanErr", beanM);	                 
				//forward(request,response,"/registrarTurnoMasivo.jsp"+"?idSession="+System.currentTimeMillis());	                  
				RequestDispatcher dispatcher =getServletContext().getRequestDispatcher("/PagE.jsp");
				 dispatcher.forward(request, response);
				return;
	        }	
		}
		log.info("Accion : " + accion);

		if (accion.equals("cargarPeriodos")) {
			cargarPeriodos(request, response, session);
		}
		if (accion.equals("buscarPeriodos")) {
			buscarPeriodos(request, response, session);
		}
		if (accion.equals("eliminarPeriodos")) {
			eliminarPeriodos(request, response, session);
		}
		if (accion.equals("cargaDatosPeriodo")) {
			cargarDatosPeriodo(request, response, session);
		}
		if (accion.equals("modificarPeriodo")) {
			modificarPeriodo(request, response, session);
		}
		if (accion.equals("desactivarDetallePeriodo")) {
			desactivarDetallePeriodo(request, response, session);
		}
		if (accion.equals("registrarPeriodo")) {
			registrarPeriodo(request, response, session);
		}
		if (accion.equals("cargarTurnos")) {
			cargarTurnos(request, response, session);
		}
		if (accion.equals("buscarTurnos")) {
			buscarTurnos(request, response, session);
		}
		if (accion.equals("eliminarTurnos")) {
			eliminarTurnos(request, response, session);
		}
		if (accion.equals("cargaDatosTurno")) {
			cargarDatosTurno(request, response, session);
		}
		if (accion.equals("modificarTurno")) {
			modificarTurno(request, response, session);
		}
		if (accion.equals("registrarTurno")) {
			registrarTurno(request, response, session);
		}
		if (accion.equals("buscarRelojes")) {
			buscarRelojes(request, response, session);
		}
		if (accion.equals("cargarRelojes")) {
			cargarRelojes(request, response, session);
		}
		if (accion.equals("eliminarRelojes")) {
			eliminarRelojes(request, response, session);
		}
		if (accion.equals("cargaDatosReloj")) {
			cargarDatosReloj(request, response, session);
		}
		if (accion.equals("modificarReloj")) {
			modificarReloj(request, response, session);
		}
		if (accion.equals("registrarReloj")) {
			registrarReloj(request, response, session);
		}
		if (accion.equals("buscarMovimientos")) {
			buscarMovimientos(request, response, session);
		}
		if (accion.equals("cargarMovimientos")) {
			cargarMovimientos(request, response, session);
		}
		if (accion.equals("eliminarMovimientos")) {
			eliminarMovimientos(request, response, session);
		}
		if (accion.equals("cargaDatosMovimiento")) {
			cargarDatosMovimiento(request, response, session);
		}
		if (accion.equals("cargarInsercionMovimiento")) {
			cargarInsercionMovimiento(request, response, session);
		}
		if (accion.equals("modificarMovimiento")) {
			modificarMovimiento(request, response, session);
		}
		if (accion.equals("registrarMovimiento")) {
			registrarMovimiento(request, response, session);
		}
		if (accion.equals("cargarSaldos")) {
		    cargarSaldos(request, response, session);
		}
		if (accion.equals("buscarSaldos")) {
		    buscarSaldos(request, response, session);
		}
		if (accion.equals("actualizarSaldo")) {
		    actualizarSaldo(request, response, session);
		}
		if (accion.equals("cargarProcesos")) {
		    cargarProcesos(request, response, session);
		}
		if (accion.equals("buscarProcesos")) {
		    buscarProcesos(request, response, session);
		}
		if (accion.equals("eliminarResponsablesProceso")) {
		    eliminarResponsablesProceso(request, response, session);
		}
		if (accion.equals("registrarResponsable")) {
		    registrarResponsable(request, response, session);
		}
		if (accion.equals("cargarAprobadores")) {
		    cargarAprobadores(request, response, session);
		}		
		if (accion.equals("buscarAprobadores")) {
		    buscarAprobadores(request, response, session);
		}
		if (accion.equals("registrarAprobador")) {
		    registrarAprobador(request, response, session);
		}
		if (accion.equals("cargarDelegados")) {
			cargarDelegados(request, response, session);
		}
		if (accion.equals("registrarDelegados")) {
		    registrarDelegados(request, response, session);
		}
		if (accion.equals("eliminarAprobadores")) {
			eliminarAprobadores(request, response, session);
		}
		if (accion.equals("cargarBusquedaUOsinAprob")) {
			cargarBusquedaUOsinAprob(request, response, session);
		}
		if (accion.equals("cambiarAprobador")) {
			cambiarAprobador(request, response, session);
		}
		if (accion.equals("cargarDatosSupervisor")) {
			cargarDatosSupervisor(request, response, session);
		}
		if (accion.equals("eliminarSupervisores")) {
			eliminarSupervisores(request, response, session);
		}
		if (accion.equals("cargarSupervisor")) {
			cargarSupervisor(request, response, session);
		}
		if (accion.equals("modificarSupervisor")) {
			modificarSupervisor(request, response, session);
		}
		if (accion.equals("registrarSupervisorMovs")) {				//jquispe 05/06/2013
			registrarSupervisorMovs(request, response, session);	//jquispe
		}
		if (accion.equals("buscarSupervisores")) {
			buscarSupervisores(request, response, session);
		}
		if (accion.equals("cargarDeleAnalista")) {
			cargarDeleAnalista(request, response, session);
		}
		if (accion.equals("cargarDeleAnalIng")) {
			cargarDeleAnalIng(request, response, session);
		}
		if (accion.equals("registrarDeleAnalista")) {
		    registrarDeleAnalista(request, response, session);
		}
		/* ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA - 22/08/2011 */
		if (accion.equals("cargarCategorias")) {
			cargarCategorias(request, response, session);
		}
		if (accion.equals("eliminarCategorias")) {
			eliminarCategorias(request, response, session);
		}
		if (accion.equals("buscarCategorias")) {
			buscarCategorias(request, response, session);
		}	
		if (accion.equals("cargarDatosCategoria")) {
			cargarDatosCategoria(request, response, session);
		}
		if (accion.equals("registrarCategoriaMovimientos")) {
			registrarCategoriaMovimientos(request, response, session);
		}
		/* ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA - 22/08/2011 */
		
		if (accion.equals("cargarRegistroAprobadores")) {
			cargarRegistroAprobadores(request, response, session);
		}
		if (accion.equals("mostrarPendientesAprobacion")) {
			mostrarPendientesAprobacion(request, response, session);
		}
		if (accion.equals("registrarFlujosAprobadores")) {
			registrarFlujosAprobadores(request, response, session);
		}
		if (accion.equals("registrarFlujosAprobadoresMasivo")) {			
			registrarFlujosAprobadoresMasivo(request, response, session, dynaBean);
		}	
		if (accion.equals("mostrarNuevasUnidades")) {
			mostrarNuevasUnidades(request, response, session);
		}
		if (accion.equals("asignarPeriodo")) {
			asignarPeriodo(request, response, session);
		}
		}
		catch(Exception e){
			  log.error("*** Error ***", e);
			  
		} finally{
			  NDC.pop();
			  NDC.remove();
		}

	}

	private void asignarPeriodo(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
		throws ServletException, IOException {
		String mensaje = "";
			try {

				UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
				BeanPeriodo periodo = (BeanPeriodo) session.getAttribute("periodo");
				
				MantenimientoDelegate md = new MantenimientoDelegate();

				ArrayList listaUOSinPeriodo = (ArrayList) session
						.getAttribute("listaUOSinPeriodo");
				
				Map params = new HashMap();
				String[] params2 = request.getParameterValues("chk_opcion");
				
				params.put("pool_sp_g", pool_sp_g);
				params.put("params2", params2);
				
				params.put("periodo", periodo.getPeriodo());
				params.put("cuser", bUsuario.getNroRegistro());
				
				md.asignarPeriodos(params, listaUOSinPeriodo);
				
				session.removeAttribute("mensaje");
				mensaje = "La asignacion ha sido realizada satisfactoriamente.";

				session.setAttribute("mensaje",mensaje);
				
				
				/*ArrayList lista = (ArrayList) session
						.getAttribute("listaUOPeriodo");*/

				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/registrarPeriodo.jsp");
				dispatcher.forward(request, response);
				return;
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

	private void mostrarNuevasUnidades(HttpServletRequest request,
			HttpServletResponse response, HttpSession session) 
					throws ServletException, IOException{
		try {

			log.debug("entra:mostrarNuevasUnidades");
			BeanPeriodo periodo = (BeanPeriodo) session.getAttribute("periodo");
			log.debug("periodo:"+periodo);

			UtilesDelegate ud = new UtilesDelegate();
			ArrayList listaUOSinPeriodo = ud.buscarUOSinPeriodo(pool_sp,periodo.getPeriodo());

			session.removeAttribute("mensaje");
			session.setAttribute("mensaje","");
			session.removeAttribute("listaUOSinPeriodo");
			session.setAttribute("listaUOSinPeriodo", listaUOSinPeriodo);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/mostrarUnidadesNuevas.jsp");
			dispatcher.forward(request, response);
			return;
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
	 * Mï¿½todo encargado de cargar la lista de perï¿½odos
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarPeriodos(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			MantenimientoDelegate md = new MantenimientoDelegate();
			ArrayList periodos = md.cargarPeriodos(pool_sp);

			session.removeAttribute("listaPeriodos");
			session.setAttribute("listaPeriodos", periodos);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/mantenimientoPeriodo.jsp");
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
	 * Mï¿½todo encargado de eliminar perï¿½odos
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void eliminarPeriodos(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			MantenimientoDelegate md = new MantenimientoDelegate();

			ArrayList listaPeriodos = (ArrayList) session
					.getAttribute("listaPeriodos");

			String[] params = request.getParameterValues("chk_opcion");
			md.eliminarPeriodos(params, listaPeriodos);

			cargarPeriodos(request, response, session);

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
	 * Mï¿½todo encargado de desactivar la uo de un determinado perï¿½odo
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void desactivarDetallePeriodo(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			BeanPeriodo periodo = (BeanPeriodo) session.getAttribute("periodo");

			int rowini = request.getParameter("filaIni") != null ? Integer
					.parseInt(request.getParameter("filaIni")) : 0;
			int rowfin = request.getParameter("filaFin") != null ? Integer
					.parseInt(request.getParameter("filaFin")) : 0;

			ArrayList lista = (ArrayList) session
					.getAttribute("listaUOPeriodo");
			String[] checks = new String[lista.size()];
			if (lista != null) {

				if (lista.size() < rowfin) {
					rowfin = lista.size();
				}

				for (int i = rowini; i < rowfin; i++) {
					String check = request.getParameter("chk_" + i);
					checks[i] = check;
				}

				//desactivamos el detalle de los perï¿½odos
				MantenimientoDelegate md = new MantenimientoDelegate();
				md.desactivarDetallePeriodo(lista, checks,
						periodo.getPeriodo(), rowini, rowfin);
			}

//JRR - 04082008 - PARA QUE MANTENGA LA BUSQUEDA EN MEMORIA
			String criterio = (String) session.getAttribute("criterio");
			String valor = (String) session.getAttribute("valor");
			
//			log.debug("criterio: " + criterio);
//			log.debug("valor: " + valor);
			
			UtilesDelegate ud = new UtilesDelegate();
			ArrayList listaUOPeriodo = null;
			
			if (criterio!=null && valor!=null)
				listaUOPeriodo = ud.buscarUOPeriodo(pool_sp, periodo.getPeriodo(), criterio, valor);
			else
				listaUOPeriodo = ud.buscarUOPeriodo(pool_sp, periodo.getPeriodo(), "", "");
			
			//session.removeAttribute("criterio");
			//session.removeAttribute("valor");
//
			
/*			UtilesDelegate ud = new UtilesDelegate();
			ArrayList listaUOPeriodo = ud.buscarUOPeriodo(pool_sp, periodo.getPeriodo(), "", "");
*/			
			session.removeAttribute("listaUOPeriodo");
			session.setAttribute("listaUOPeriodo", listaUOPeriodo);
			
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/registrarPeriodo.jsp");
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
	 * Mï¿½todo encargado de cargar los datos de un perï¿½odo
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarDatosPeriodo(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			ArrayList lista = (ArrayList) session.getAttribute("listaPeriodos");
			String indice = request.getParameter("indice");
			BeanPeriodo periodo = (BeanPeriodo) lista.get(Integer
					.parseInt(indice));

			session.removeAttribute("periodo");
			session.setAttribute("periodo", periodo);
			
			session.removeAttribute("mensaje");
			session.setAttribute("mensaje","");

			UtilesDelegate ud = new UtilesDelegate();
			ArrayList listaUOPeriodo = ud.buscarUOPeriodo(pool_sp, periodo
					.getPeriodo(), "", "");

			session.removeAttribute("listaUOPeriodo");
			session.setAttribute("listaUOPeriodo", listaUOPeriodo);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/registrarPeriodo.jsp");
			dispatcher.forward(request, response);
			return;
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
	 * Mï¿½todo encargado de modificar los datos de un perï¿½odo
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void modificarPeriodo(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			BeanPeriodo periodo = (BeanPeriodo) session.getAttribute("periodo");

			String fechaIni = request.getParameter("fechaIni");
			String fechaFin = request.getParameter("fechaFin");
			String fechaCierre = request.getParameter("fechaCierre");

			//JRR - 08/03/2010
			String fechaIniCAS = request.getParameter("fechaIniCAS");
			String fechaFinCAS = request.getParameter("fechaFinCAS");
			String fechaCierreCAS = request.getParameter("fechaCierreCAS");

			//JRR - 01/06/2011
			String fechaIniMF = request.getParameter("fechaIniMF");
			String fechaFinMF = request.getParameter("fechaFinMF");
			String fechaCierreMF = request.getParameter("fechaCierreMF");
			
			Map params = new HashMap();
			params.put("periodo", periodo.getPeriodo());
			params.put("finicio", fechaIni);
			params.put("ffin", fechaFin);
			params.put("fcierre", fechaCierre);
			params.put("fec_ini_cas", fechaIniCAS);
			params.put("fec_fin_cas", fechaFinCAS);
			params.put("fec_cierre_cas", fechaCierreCAS);
			params.put("fec_ini_mf", fechaIniMF);
			params.put("fec_fin_mf", fechaFinMF);
			params.put("fec_cierre_mf", fechaCierreMF);			
			params.put("pool_sp_g", pool_sp_g);
			params.put("cuser", bUsuario.getNroRegistro());

			MantenimientoDelegate md = new MantenimientoDelegate();
			md.modificarPeriodo(params);
			//md.modificarPeriodo(pool_sp_g, periodo.getPeriodo(), fechaIni, fechaFin, fechaCierre);
			//

			cargarPeriodos(request, response, session);

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
	 * Mï¿½todo encargado de registrar un perï¿½odo
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void registrarPeriodo(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			String periodo = request.getParameter("txtPeriodo").toUpperCase();
			String fechaIni = request.getParameter("fechaIni");
			String fechaFin = request.getParameter("fechaFin");
			String fechaCierre = request.getParameter("fechaCierre");
			UsuarioBean user = (UsuarioBean) session.getAttribute("usuarioBean");
			
			//String usuario = user.getLogin();

			//JRR - 12/03/2010
			String fechaIniCAS = request.getParameter("fechaIniCAS");
			String fechaFinCAS = request.getParameter("fechaFinCAS");
			String fechaCierreCAS = request.getParameter("fechaCierreCAS");
			
			//JRR - 01/06/2011
			String fechaIniMF = request.getParameter("fechaIniMF");
			String fechaFinMF = request.getParameter("fechaFinMF");
			String fechaCierreMF = request.getParameter("fechaCierreMF");

			Map params = new HashMap();
			params.put("periodo", periodo);
			params.put("finicio", fechaIni);
			params.put("ffin", fechaFin);
			params.put("fcierre", fechaCierre);
			params.put("fec_ini_cas", fechaIniCAS);
			params.put("fec_fin_cas", fechaFinCAS);
			params.put("fec_cierre_cas", fechaCierreCAS);
			params.put("fec_ini_mf", fechaIniMF);
			params.put("fec_fin_mf", fechaFinMF);
			params.put("fec_cierre_mf", fechaCierreMF);			
			params.put("pool_sp_g", pool_sp_g);
			params.put("cuser", user.getNroRegistro());

			MantenimientoDelegate md = new MantenimientoDelegate();
			md.registrarPeriodo(params);
			//
			
			/*registramos el periodo
			MantenimientoDelegate md = new MantenimientoDelegate();
			md.registrarPeriodo(pool_sp_g, periodo, fechaIni, fechaFin,
					fechaCierre, usuario);
			*/

			ArrayList periodos = md.cargarPeriodos(pool_sp);
			session.removeAttribute("listaPeriodos");
			session.setAttribute("listaPeriodos", periodos);

			BeanPeriodo bPeriodo = md.buscarPeriodoCodigo(pool_sp, periodo);
			session.removeAttribute("periodo");
			session.setAttribute("periodo", bPeriodo);

			UtilesDelegate ud = new UtilesDelegate();
			ArrayList listaUOPeriodo = ud.buscarUOPeriodo(pool_sp, periodo, "",	"");

			session.removeAttribute("listaUOPeriodo");
			session.setAttribute("listaUOPeriodo", listaUOPeriodo);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/registrarPeriodo.jsp");
			dispatcher.forward(request, response);
			return;
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
	 * Mï¿½todo encargado de buscar periodos
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarPeriodos(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			String criterio = request.getParameter("cmbCriterio");
			String valor = request.getParameter("txtValor") != null ? request
					.getParameter("txtValor") : "";

			MantenimientoDelegate md = new MantenimientoDelegate();
			ArrayList periodos = md.buscarPeriodos(pool_sp, criterio, valor);

			session.removeAttribute("listaPeriodos");
			session.setAttribute("listaPeriodos", periodos);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/mantenimientoPeriodo.jsp");
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
	 * Mï¿½todo encargado de cargar los turnos
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarTurnos(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			MantenimientoDelegate md = new MantenimientoDelegate();
			ArrayList turnos = md.cargarTurnos(pool_sp);

			session.removeAttribute("listaTurnos");
			session.setAttribute("listaTurnos", turnos);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/mantenimientoTurno.jsp");
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
	 * Mï¿½todo encargado de eliminar los turnos
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void eliminarTurnos(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			ArrayList lista = (ArrayList) session.getAttribute("listaTurnos");
			String[] params = request.getParameterValues("chk_opcion");

			MantenimientoDelegate md = new MantenimientoDelegate();
			md.eliminarTurnos(params, lista);

			cargarTurnos(request, response, session);
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
	 * Mï¿½todo encargado de cargar los datos de un turno
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarDatosTurno(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			ArrayList listaTurnos = (ArrayList) session
					.getAttribute("listaTurnos");
			String indice = request.getParameter("indice");
			BeanTurno turno = (BeanTurno) listaTurnos.get(Integer
					.parseInt(indice));

			session.removeAttribute("turno");
			session.setAttribute("turno", turno);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/registrarTurno.jsp");
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
	 * Mï¿½todo encargado de modificar un turno
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void modificarTurno(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			BeanTurno turno = (BeanTurno) session.getAttribute("turno");

			String descripcion = request.getParameter("txtDescripcion");
			String fechaIni = request.getParameter("fechaIni");
			String fechaFin = request.getParameter("fechaFin");
			String horaIni = request.getParameter("horaIni");
			String horaFin = request.getParameter("horaFin");
			String horaLimite = request.getParameter("horaLimite");
			String dias = request.getParameter("Dias");
			String tolerancia = request.getParameter("Tolerancia");
			String refrIni = request.getParameter("refrIni");
			String refrFin = request.getParameter("refrFin");
			String refrMin = request.getParameter("refrMinutos");
			String control = request.getParameter("chkControl") != null ? "1" : "0";

			MantenimientoDelegate md = new MantenimientoDelegate();
			md.modificarTurno(turno.getCodTurno(), descripcion, fechaIni,
					horaIni, fechaFin, horaFin, dias, tolerancia, horaLimite,
					refrIni, refrFin, refrMin, control);

			//cargamos la lista de turnos
			cargarTurnos(request, response, session);

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
	 * Mï¿½todo encargado de registrar un turno
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void registrarTurno(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			String codTurno = request.getParameter("txtTurno").toUpperCase();
			String descripcion = request.getParameter("txtDescripcion");
			String fechaIni = request.getParameter("fechaIni");
			String fechaFin = request.getParameter("fechaFin");
			String horaIni = request.getParameter("horaIni");
			String horaFin = request.getParameter("horaFin");
			String horaLimite = request.getParameter("horaLimite");
			String dias = request.getParameter("Dias");
			String tolerancia = request.getParameter("Tolerancia");
			String operativo = request.getParameter("chkOperativo") != null ? "1" : "0";
			String control = request.getParameter("chkControl") != null ? "1" : "0";			
			String refrIni = request.getParameter("refrIni");
			String refrFin = request.getParameter("refrFin");
			String refrMin = request.getParameter("refrMinutos");

			UsuarioBean user = (UsuarioBean) session.getAttribute("usuarioBean");
			String usuario = user.getLogin();

			//registramos el turno
			MantenimientoDelegate md = new MantenimientoDelegate();
			md.registrarTurno(codTurno, descripcion, fechaIni, horaIni,
					fechaFin, horaFin, dias, tolerancia, horaLimite, operativo,
					refrIni, refrFin, refrMin, usuario, control);

			//cargamos la lista de turnos
			cargarTurnos(request, response, session);

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
	 * Mï¿½todo encargado de buscar los turnos
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarTurnos(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			RequestDispatcher dispatcher;
			String criterio = request.getParameter("cmbCriterio") != null ? request.getParameter("cmbCriterio") : "";
            String valor = request.getParameter("txtValor") != null ? request.getParameter("txtValor") : "";
			//PRAC-ASANCHEZ 08/04/2009
            String tipoReporte = request.getParameter("tipoReporte") != null ? request.getParameter("tipoReporte") : "buscar";

            MantenimientoDelegate md = new MantenimientoDelegate();
			ArrayList turnos = md.buscarTurnos(pool_sp, criterio, valor);
			
			session.removeAttribute("listaTurnos");
			session.setAttribute("listaTurnos", turnos);
            request.setAttribute("criterio", criterio);
            request.setAttribute("valor", valor);

            //PRAC-ASANCHEZ 08/04/2009
			if(tipoReporte.equals("buscar")){
				dispatcher = getServletContext().getRequestDispatcher("/mantenimientoTurno.jsp");
				dispatcher.forward(request, response);
			} 
			if(tipoReporte.equals("excel")){
				dispatcher = getServletContext().getRequestDispatcher("/ExcelTurnos.jsp");
				dispatcher.forward(request, response);
			}
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
	 * Mï¿½todo encargado de buscar una lista de relojes de acuerdo a un criterio
	 * y valor
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarRelojes(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			String criterio = request.getParameter("cmbCriterio");
			String valor = request.getParameter("txtValor") != null ? request
					.getParameter("txtValor") : "";

			MantenimientoDelegate md = new MantenimientoDelegate();
			ArrayList relojes = md.buscarRelojes(pool_sp, criterio, valor);

			session.removeAttribute("listaRelojes");
			session.setAttribute("listaRelojes", relojes);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/mantenimientoReloj.jsp");
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
	 * Mï¿½todo encargado de cargar la lista de relojes
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarRelojes(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			log.debug("pool_sa : "+pool_sa);
			
			MantenimientoDelegate md = new MantenimientoDelegate();
			ArrayList relojes = md.cargarRelojes(pool_sp, Constantes.ACTIVO);
			ArrayList listaLocales = md.buscarSedes(pool_sa, Constantes.ACTIVO);

			log.debug("listaLocales : "+listaLocales);
			
			session.removeAttribute("listaRelojes");
			session.removeAttribute("listaLocales");

			session.setAttribute("listaRelojes", relojes);
			session.setAttribute("listaLocales", listaLocales);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/mantenimientoReloj.jsp");
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
	 * Mï¿½todo encargado de eliminar relojes
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void eliminarRelojes(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			ArrayList lista = (ArrayList) session.getAttribute("listaRelojes");
			String[] params = request.getParameterValues("chk_opcion");

			MantenimientoDelegate md = new MantenimientoDelegate();
			md.eliminarRelojes(params, lista);

			cargarRelojes(request, response, session);
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
	 * Mï¿½todo encargado de cargar los datos de un reloj
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarDatosReloj(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			ArrayList lista = (ArrayList) session.getAttribute("listaRelojes");
			String indice = request.getParameter("indice");
			BeanTipoReloj reloj = (BeanTipoReloj) lista.get(Integer
					.parseInt(indice));

			session.removeAttribute("reloj");
			session.setAttribute("reloj", reloj);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/registrarReloj.jsp");
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
	 * Mï¿½todo encargado de modificar los datos de un reloj
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void modificarReloj(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			BeanTipoReloj reloj = (BeanTipoReloj) session.getAttribute("reloj");

			String descripcion = request.getParameter("txtDescripcion");
			String sede = request.getParameter("cmbSede");

			MantenimientoDelegate md = new MantenimientoDelegate();
			md.modificarReloj(reloj.getReloj(), descripcion, sede);

			//cargamos la lista de relojes
			cargarRelojes(request, response, session);
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
	 * Mï¿½todo encargado de registrar un nuevo reloj
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void registrarReloj(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			String reloj = request.getParameter("reloj").toUpperCase();
			String descripcion = request.getParameter("txtDescripcion");
			String sede = request.getParameter("cmbSede");
			UsuarioBean user = (UsuarioBean) session.getAttribute("usuarioBean");
			
			String usuario = user.getLogin();

			//registramos el reloj
			MantenimientoDelegate md = new MantenimientoDelegate();
			md.registrarReloj(reloj, descripcion, sede, usuario);

			//cargamos la lista de relojes
			cargarRelojes(request, response, session);

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
	 * Mï¿½todo encargado de cargar la lista de tipos de movimiento
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarMovimientos(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			MantenimientoDelegate md = new MantenimientoDelegate();

			ArrayList tiposMovimientos = md.cargarT99(pool_sp,
					Constantes.CODTAB_TIPOMOV);
			ArrayList medidas = md.cargarT99(pool_sp, Constantes.CODTAB_MEDIDA);
			ArrayList movimientos = md.buscarMovimientos(pool_sp, "-1", "",
					"-1");

		    UtilesDelegate ud = new UtilesDelegate();
		    ArrayList instancias = ud.buscarT99Codigo(pool_sp,"","",Constantes.CODTAB_INSTANCIA);    		    
		    session.removeAttribute("instancias");
		    session.setAttribute("instancias",instancias);
			
			session.removeAttribute("listaMedidas");
			session.removeAttribute("listaMovimientos");
			session.removeAttribute("listaTipos");
			session.removeAttribute("listaAprobadores");

			session.setAttribute("listaMovimientos", movimientos);
			session.setAttribute("listaTipos", tiposMovimientos);
			session.setAttribute("listaMedidas", medidas);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/mantenimientoMovimiento.jsp");
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
	 * Mï¿½todo encargado de eliminar un tipo de movimiento
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void eliminarMovimientos(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			ArrayList lista = (ArrayList) session
					.getAttribute("listaMovimientos");

			String[] params = request.getParameterValues("chk_opcion");

			MantenimientoDelegate md = new MantenimientoDelegate();
			md.eliminarMovimientos(params, lista);

			cargarMovimientos(request, response, session);

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
	 * Mï¿½todo encargado de cargar los datos de un tipo de movimiento
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarDatosMovimiento(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		
		try {

			ArrayList listaMovimientos = (ArrayList) session
					.getAttribute("listaMovimientos");
			String indice = request.getParameter("indice");
			BeanTipoMovimiento movimiento = (BeanTipoMovimiento) listaMovimientos
					.get(Integer.parseInt(indice));

			MantenimientoDelegate md = new MantenimientoDelegate();

			ArrayList tiposMovimientos = md.cargarT99(pool_sp,
					Constantes.CODTAB_TIPOMOV);
			ArrayList medidas = md.cargarT99(pool_sp, Constantes.CODTAB_MEDIDA);

		    HashMap datos = new HashMap();
		    datos.put("dbpool",pool_sp);
		    datos.put("mov",movimiento.getMov());
		    
		    ArrayList lista = md.buscarAprobadores(datos);
		    session.removeAttribute("listaAprobadores");
		    session.setAttribute("listaAprobadores",lista);
			
			session.removeAttribute("movimiento");
			session.removeAttribute("listaTipos");
			session.removeAttribute("listaMedidas");

			session.setAttribute("movimiento", movimiento);
			session.setAttribute("listaTipos", tiposMovimientos);
			session.setAttribute("listaMedidas", medidas);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/registrarMovimiento.jsp");
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
	 * Mï¿½todo encargado de cargar los datos para el registro de un tipo de
	 * movimiento
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarInsercionMovimiento(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			MantenimientoDelegate md = new MantenimientoDelegate();

			ArrayList tiposMovimientos = md.cargarT99(pool_sp,
					Constantes.CODTAB_TIPOMOV);
			ArrayList medidas = md.cargarT99(pool_sp, Constantes.CODTAB_MEDIDA);

			session.removeAttribute("listaTipos");
			session.removeAttribute("listaMedidas");

			session.setAttribute("listaTipos", tiposMovimientos);
			session.setAttribute("listaMedidas", medidas);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/registrarMovimiento.jsp");
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
	 * Mï¿½todo encargado de modificar un tipo de movimiento
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void modificarMovimiento(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean user = (UsuarioBean) session.getAttribute("usuarioBean");
			
			String usuario = user.getLogin();
			
			MantenimientoDelegate md = new MantenimientoDelegate();
			BeanTipoMovimiento movimiento = (BeanTipoMovimiento) session
					.getAttribute("movimiento");

			String descripcion = request.getParameter("txtDescripcion");
			String califica = request.getParameter("cmbCalifica");
			String medida = request.getParameter("cmbMedida");
			String tipo = request.getParameter("cmbTipoMovimiento");
			String fechaIni = request.getParameter("fechaIni");
			String fechaFin = request.getParameter("fechaFin");

			String esSolicitud = request.getParameter("chk_Solicitud") != null ? Constantes.ACTIVO
					: Constantes.INACTIVO;
			String vbRrHh = request.getParameter("chkRrHh");
			String obligId = request.getParameter("chkJerarquia");
			String esCalificacion = request.getParameter("chk_Asistencia") != null ? Constantes.ACTIVO
					: Constantes.INACTIVO;
			String indDias = request.getParameter("chk_ind_dias")!= null ? Constantes.ACTIVO
					: Constantes.INACTIVO;
			String indProc = request.getParameter("chk_ind_proc")!= null ? Constantes.ACTIVO
					: Constantes.INACTIVO;

			String calif = request.getParameter("optCalif");

			String diasAntes = request.getParameter("diasAntes");
			String diasDespues = request.getParameter("diasDespues");
			String diasAcum = request.getParameter("diasAcum");
			String qvalida = request.getParameter("qvalida");

			String calEntr = calif.trim().equals("1") ? Constantes.ACTIVO
					: Constantes.INACTIVO;
			String calRefr = calif.trim().equals("2") ? Constantes.ACTIVO
					: Constantes.INACTIVO;
			String calSali = calif.trim().equals("3") ? Constantes.ACTIVO
					: Constantes.INACTIVO;

			java.util.Date fI = Utiles.stringToDate(fechaIni);
			java.util.Date fF = Utiles.stringToDate(fechaFin);

			Timestamp fIni = new java.sql.Timestamp(fI.getTime());
			Timestamp fFin = new java.sql.Timestamp(fF.getTime());
			
			//JRR - 11 ABRIL 2008 - PARA GUARDAR EN LOG
	            String ip = request.getRemoteAddr();
	            String pc = request.getRemoteHost();
	            String razon = request.getParameter("txtRazon")!=null?request.getParameter("txtRazon"):"";
	            String cod_usuario = user.getNroRegistro();
			//

	       //25/06/2008     
	       Map  params = new HashMap();
	       params.put("codMovimiento", movimiento.getMov());
	       params.put("descripcion", descripcion);
	       params.put("califica", califica);
	       params.put("medida", medida.trim());
	       params.put("tipo", tipo.trim());
	       //params.put("fechaIni", fIni);
	       //params.put("fechaFin", fFin);
	       params.put("soliId", esSolicitud);
	       params.put("vbRrHh", vbRrHh);
	       params.put("procId", esCalificacion);
	       params.put("entrId", calEntr);
	       params.put("refrId", calRefr);
	       params.put("saliId", calSali);
	       params.put("diasAntes", diasAntes);
	       params.put("diasDespues", diasDespues);
	       params.put("diasAcum", diasAcum);
	       params.put("obligId", obligId);
	       params.put("qvalida", qvalida);
	       params.put("usuario", usuario);
	       params.put("ind_dias", indDias);
	       params.put("ind_proc", indProc);
	       params.put("ip", ip);
	       params.put("pc", pc);
	       params.put("razon", razon);
	       params.put("cod_usuario", cod_usuario);
	       
	       md.modificarMovimiento(params,fIni, fFin);
	            
/*    	   md.modificarMovimiento(movimiento.getMov(), descripcion, califica,
					medida.trim(), tipo.trim(), fIni, fFin, esSolicitud,
					vbRrHh, esCalificacion, calEntr, calRefr, calSali,
					diasAntes, diasDespues, diasAcum, obligId, qvalida, usuario, indDias, indProc,
					ip, pc, razon, cod_usuario);
*/
	       
			//cargamos la lista de movimientos
			cargarMovimientos(request, response, session);
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
	 * Mï¿½todo encargado de registrar un tipo de movimiento
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void registrarMovimiento(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			MantenimientoDelegate md = new MantenimientoDelegate();

			String codMovimiento = request.getParameter("txtMov").toUpperCase();
			String descripcion = request.getParameter("txtDescripcion");
			String califica = request.getParameter("cmbCalifica");
			String medida = request.getParameter("cmbMedida");
			String tipo = request.getParameter("cmbTipoMovimiento");
			String fechaIni = request.getParameter("fechaIni");
			String fechaFin = request.getParameter("fechaFin");

			String esSolicitud = request.getParameter("chk_Solicitud") != null ? Constantes.ACTIVO
					: Constantes.INACTIVO;
			String vbRrHh = request.getParameter("chkRrHh");
			String obligId = request.getParameter("chkJerarquia");
			String esCalificacion = request.getParameter("chk_Asistencia") != null ? Constantes.ACTIVO
					: Constantes.INACTIVO;
			String calif = request.getParameter("optCalif");
			
			String diasAntes = request.getParameter("diasAntes");
			String diasDespues = request.getParameter("diasDespues");
			String diasAcum = request.getParameter("diasAcum");
			String qvalida = request.getParameter("qvalida");
			String indDias = request.getParameter("chk_ind_dias")!= null ? Constantes.ACTIVO
					: Constantes.INACTIVO;
			String indProc = request.getParameter("chk_ind_proc")!= null ? Constantes.ACTIVO
					: Constantes.INACTIVO;

			String calEntr = calif.trim().equals("1") ? Constantes.ACTIVO
					: Constantes.INACTIVO;
			String calRefr = calif.trim().equals("2") ? Constantes.ACTIVO
					: Constantes.INACTIVO;
			String calSali = calif.trim().equals("3") ? Constantes.ACTIVO
					: Constantes.INACTIVO;

			UsuarioBean user = (UsuarioBean) session.getAttribute("usuarioBean");
			
			String usuario = user.getLogin();

			java.util.Date fI = Utiles.stringToDate(fechaIni);
			java.util.Date fF = Utiles.stringToDate(fechaFin);

			//registramos el movimiento
			md.registrarMovimiento(codMovimiento, descripcion, califica, medida
					.trim(), tipo.trim(), new Timestamp(fI.getTime()),
					new Timestamp(fF.getTime()), esSolicitud, vbRrHh,
					esCalificacion, calEntr, calRefr, calSali, diasAntes,
					diasDespues, diasAcum, obligId, qvalida, indDias, indProc, usuario);

			//cargamos la lista de movimientos
			cargarMovimientos(request, response, session);

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
	 * Mï¿½todo encargado de buscar una lista de movimientos
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarMovimientos(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			MantenimientoDelegate md = new MantenimientoDelegate();

			String tipo = request.getParameter("cmbTipoMovimiento");
			String criterio = request.getParameter("cmbCriterio");
			String valor = request.getParameter("txtValor") != null ? request
					.getParameter("txtValor") : "";

			ArrayList movimientos = md.buscarMovimientos(pool_sp, criterio,
					valor, tipo);

			session.removeAttribute("listaMovimientos");
			session.setAttribute("listaMovimientos", movimientos);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/mantenimientoMovimiento.jsp");
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
	 * Metodo encargado de cargar la pagina para el mantenimiento de saldos vacacionales
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarSaldos(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			session.removeAttribute("listaSaldos");
			
			session.setAttribute("fechaIncorporado","");
			session.setAttribute("datosTrabajador",null);
			

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/mantenimientoSaldos.jsp");
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
	 * Metodo encargado de buscar la lista de saldos vacacionales
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarSaldos(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			MantenimientoDelegate md = new MantenimientoDelegate();
			String valor = request.getParameter("txtValor") != null ? request.getParameter("txtValor") : "";
			ArrayList saldos = md.buscarSaldosVacacionales(pool_sp, valor);
			session.removeAttribute("listaSaldos");
			session.setAttribute("listaSaldos", saldos);
			UtilesDelegate ud = new UtilesDelegate();
			HashMap datosTrabajador = ud.buscarTrabajador(pool_sp, valor,null);
			String fecha = "";
			String fechaIng = "";
			VacacionDelegate vd = new VacacionDelegate();
			HashMap hmVacGen = vd.buscarVacacionesGen(pool_sp, valor);
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
					.getRequestDispatcher("/mantenimientoSaldos.jsp");
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
	 * Metodo encargado de actualizar los saldos vacacionales
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 *
	private void actualizarSaldo(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
		    
			MantenimientoDelegate md = new MantenimientoDelegate();

			ArrayList saldos = (ArrayList)session.getAttribute("listaSaldos");
			String valor = request.getParameter("indice");
			String nuevo_saldo = request.getParameter("txt_saldo_"+valor);
			HashMap saldo = (HashMap)saldos.get(Integer.parseInt(valor));
			saldo.put("nuevo_saldo",nuevo_saldo);
			saldo.put("usuario",bUsuario.getNroRegistro());
			saldo.put("ip",request.getRemoteAddr());
			saldo.put("pc",request.getRemoteHost());
			saldo.put("accion",Constantes.ACCION_LOG_SALDOS);			
			
			//actualizamos el saldo vacacional
			md.actualizarSaldo(pool_sp_g,saldo);		
			
			saldos = md.buscarSaldosVacacionales(pool_sp, (String)saldo.get("cod_pers"));			
			session.removeAttribute("listaSaldos");
			session.setAttribute("listaSaldos", saldos);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/mantenimientoSaldos.jsp");
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
*/
	
/* JRR - PROGRAMACION - 18/05/2010  */
	/**
	 * Metodo encargado de actualizar los saldos vacacionales
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */ 
	private void actualizarSaldo(HttpServletRequest request, HttpServletResponse response, HttpSession session)
	throws ServletException, IOException {
		try {
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			MantenimientoDelegate md = new MantenimientoDelegate();	
			boolean exito = false;			
			List saldos = (ArrayList)session.getAttribute("listaSaldos");
			String valor = request.getParameter("indice");
			String nuevo_saldo = request.getParameter("txt_saldo_"+valor);
			Map saldo = (HashMap)saldos.get(Integer.parseInt(valor));
			saldo.put("nuevo_saldo",nuevo_saldo);
			saldo.put("usuario",bUsuario.getNroRegistro());
			saldo.put("ip",request.getRemoteAddr());
			saldo.put("pc",request.getRemoteHost());
			saldo.put("accion",Constantes.ACCION_LOG_SALDOS);	
			
			exito = md.validaSaldo(saldo);		
			
			if (!exito){				
				setAttribute(request,"valida","1");	
			}			
			saldos = md.buscarSaldosVacacionales(pool_sp, (String)saldo.get("cod_pers"));		
			session.removeAttribute("listaSaldos");
			setAttribute(session, "listaSaldos", saldos);			
			forward(request, response, "/mantenimientoSaldos.jsp");
			return;
		} catch (DelegateException e) {
		  log.error(e,e);
		  forwardError(request, response, e.getMensaje());
		} catch (Exception e) {
		  log.error(e,e);
		  bean= new MensajeBean();
		  bean.setError(true);
		  bean.setMensajeerror("Ha ocurrido un error al agregar la programacion");
		  bean.setMensajesol("Por favor intente nuevamente ejecutar la opcin");
		  forwardError(request, response, bean);
		} finally {
		}
	}
/*      */	
	
	/**
	 * Metodo encargado de cargar la pagina para el mantenimiento de procesos
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarProcesos(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

		    session.removeAttribute("listaProcesos");

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/mantenimientoProcesos.jsp");
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
	 * Metodo encargado de buscar la lista de responsables por proceso
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarProcesos(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			MantenimientoDelegate md = new MantenimientoDelegate();

			String proceso = request.getParameter("cmb_proceso");
			String uorgan = request.getParameter("txtValor");

			ArrayList procesos = md.buscarResponsablesProceso(pool_sp, proceso, uorgan);

			session.removeAttribute("listaProcesos");
			session.setAttribute("listaProcesos", procesos);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/mantenimientoProcesos.jsp?id_proc="+proceso+"&id_uorg="+uorgan);
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
	 * Metodo encargado de eliminar los responsables de un proceso
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void eliminarResponsablesProceso(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			ArrayList lista = (ArrayList) session.getAttribute("listaProcesos");
			String[] params = request.getParameterValues("chk_opcion");

			MantenimientoDelegate md = new MantenimientoDelegate();
			md.eliminarResponsablesProceso(params, lista);

			cargarProcesos(request, response, session);

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
	 * Metodo encargado de registrar los responsables de un proceso
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void registrarResponsable(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			MantenimientoDelegate md = new MantenimientoDelegate();

			String operacion = request.getParameter("cmb_proceso");
			String u_organ = request.getParameter("txtUO");
			String cod_pers = request.getParameter("txtResp");	
			
			HashMap datos = new HashMap();
			datos.put("operacion",operacion);
			if(u_organ==null)
				u_organ="TODOS";
			
			datos.put("u_organ",u_organ.trim().toUpperCase());
			datos.put("cod_pers",cod_pers.trim().toUpperCase());
			datos.put("cuser",bUsuario.getLogin());
			datos.put("codPers",bUsuario.getNroRegistro());
			datos.put("dbpool", pool_sp);
			log.debug("UORG: "+u_organ);	
			if(u_organ=="TODOS"){
				md.asignacionResponsablesMasivo(pool_sp_g,datos);
                log.debug("Paso 0: "+pool_sp);	
                request.setAttribute("nomReporte",
                        "ASIGNACIÓN DE RESPONSABLES");
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/resultadoReporte.jsp");
                dispatcher.forward(request, response);
                return;				
			}else{
				//registramos al responsable
				md.registrarResponsable(pool_sp_g,datos);
				cargarProcesos(request,response,session);
				
			}
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
	 * Metodo encargado de cargar la pagina para el mantenimiento de aprobadores de solicitud
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarAprobadores(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

		    MantenimientoDelegate md = new MantenimientoDelegate();
		    BeanTipoMovimiento movimiento = (BeanTipoMovimiento)session.getAttribute("movimiento");		    
		    
		    HashMap datos = new HashMap();
		    datos.put("dbpool",pool_sp);
		    datos.put("mov",movimiento.getMov());
		    
		    ArrayList lista = md.buscarAprobadores(datos);
		    session.removeAttribute("listaAprobadores");
		    session.setAttribute("listaAprobadores",lista);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/mantenimientoAprobadores.jsp");
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
	 * Metodo encargado de buscar los aprobadores de una solicitud
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarAprobadores(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

		    MantenimientoDelegate md = new MantenimientoDelegate();
		    
		    BeanTipoMovimiento movimiento = (BeanTipoMovimiento)session.getAttribute("movimiento");
		    
		    String txtUO = request.getParameter("txtUO")!=null?request.getParameter("txtUO"):"";
		    String txtOri = request.getParameter("txtOri")!=null?request.getParameter("txtOri"):"";
		    String txtDes = request.getParameter("txtDes")!=null?request.getParameter("txtDes"):"";
		    String instancia = request.getParameter("cmbInstancia")!=null?request.getParameter("cmbInstancia"):"";
		    String id_agregar = request.getParameter("id_agregar");
		    
		    HashMap datos = new HashMap();
		    datos.put("dbpool",pool_sp);
		    datos.put("mov",movimiento.getMov());
		    datos.put("codUO",txtUO.trim().toUpperCase());
		    datos.put("cod_pers_ori",txtOri.trim().toUpperCase());
		    datos.put("cod_pers_des",txtDes.trim().toUpperCase());
		    datos.put("instancia",instancia);
		    datos.put("accion",Constantes.ACCION_APROBAR);
		    datos.put("id_agregar",id_agregar);
		    
		    ArrayList lista = md.buscarAprobadores(datos);
		    session.removeAttribute("listaAprobadores");
		    session.setAttribute("listaAprobadores",lista);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/mantenimientoAprobadores.jsp");
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
	 * Metodo encargado de registrar los datos de los aprobadores
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void registrarAprobador(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
		    MantenimientoDelegate md = new MantenimientoDelegate();
		    
		    BeanTipoMovimiento movimiento = (BeanTipoMovimiento)session.getAttribute("movimiento");
		    
		    String txtUO = request.getParameter("txtUO")!=null?request.getParameter("txtUO"):"";
		    String txtOri = request.getParameter("txtOri")!=null?request.getParameter("txtOri"):"";
		    String txtDes = request.getParameter("txtDes")!=null?request.getParameter("txtDes"):"";
		    String instancia = request.getParameter("cmbInstancia")!=null?request.getParameter("cmbInstancia"):"";
		    String id_agregar = request.getParameter("id_agregar");
		    
		    HashMap datos = new HashMap();
		    datos.put("dbpool",pool_sp);
		    datos.put("mov",movimiento.getMov());
		    datos.put("codUO",txtUO.trim().toUpperCase());
		    datos.put("cod_pers_ori",txtOri.trim().toUpperCase());
		    datos.put("cod_pers_des",txtDes.trim().toUpperCase());
		    datos.put("instancia",instancia);
		    datos.put("accion",Constantes.ACCION_APROBAR);
		    datos.put("id_agregar",id_agregar);
		    datos.put("cuser",bUsuario.getLogin());
		    		    
		    md.registrarAprobador(datos);		    
		    cargarAprobadores(request,response,session);
			
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
	 * Metodo encargado de cargar la pagina para el mantenimiento de delegados
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarDelegados(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
		    
			HashMap datos = new HashMap();
		    datos.put("dbpool",pool_sp); //se usa //para delegados
		    datos.put("codJefe",bUsuario.getNroRegistro());
		    datos.put("codPers",bUsuario.getNroRegistro()); //se usa
		    datos.put("codOpcion", Constantes.DELEGA_TODO); //se usa
		    datos.put("codUO",bUsuario.getCodUO()); //para delegados 
		    datos.put("descUO",bUsuario.getDesUO());
		    datos.put("nomJefe",bUsuario.getNombres().toUpperCase());
		    
		    log.debug("datos1: "+datos);
		    
		    UtilesDelegate ud = new UtilesDelegate();
		    AsistenciaDelegate ad = new AsistenciaDelegate();
		    //if (ad.esJefeEncargadoDelegado(datos)){  ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
		    if (ad.esJefeEncargadoDelegadoSolicitudes(datos)){  //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
		    	
		    	//ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
		    	//importante codJefe, nomJefe , descUO
		    	HashMap uuooMap = new HashMap();
		    	T12DAO daoUO = new T12DAO();
				ArrayList uuoos = new ArrayList();
				uuoos = daoUO.findJefeEncargadoInUUOOs(datos); //lista unidades donde es jefe o encargado el usuario
				log.debug("cargarDelegados(uuoos): "+uuoos);
				if (uuoos!=null && !uuoos.isEmpty()){
					uuooMap = (HashMap)uuoos.get(0); //solo se obtiene 1 unidad para delegacion (puede ser delegado de mas de 1)
					datos.put("codUO",uuooMap.get("t12cod_uorga").toString().trim()); 
				}
				log.debug("datos2: "+datos);
				HashMap jefe = ud.buscarJefe(pool_sp, datos.get("codUO").toString(), " ");			    
			    log.debug("cargarDelegados(jefe): "+ jefe);
			    String codjefeEncargado = (String)jefe.get("t12cod_encar" );
			    String codjefe = (String)jefe.get("t12cod_jefat" );
			    String desjefe = (String)jefe.get("t12cod_jefat_desc" );
			    log.debug("cargarDelegados(codjefe): "+ codjefe);
			    if (codjefe.equals(" ") || codjefe.equals("") || codjefe ==null ||(codjefeEncargado!=null  && codjefeEncargado.trim().length()>0) ){			    	
			    	codjefe = (String)jefe.get("t12cod_encar" );
			    	desjefe = (String)jefe.get("t12cod_encar_desc" );
			    }
			    datos.put("codJefe",codjefe);		
			    datos.put("nomJefe",desjefe.toUpperCase());
			    BeanT12 unidadUO = ud.buscarUObyCodigo(pool_sp, datos.get("codUO").toString());
			    datos.put("descUO", unidadUO!=null?unidadUO.getT12descr_uorg():"Unidad no encontrada");

				/*
		    	HashMap trab = ud.buscarTrabajador(pool_sp,bUsuario.getNroRegistro(),null);
		    	
		    	if (!((String)trab.get("t12cod_jefe")).equals(bUsuario.getNroRegistro())){		    	
		    		trab = ud.buscarTrabajador(pool_sp,(String)trab.get("t12cod_jefe"),null);		    		
		    		datos.put("codJefe",trab.get("t02cod_pers"));
		    		datos.put("nomJefe",trab.get("t02ap_pate")+" "+trab.get("t02ap_mate")+", "+trab.get("t02nombres"));		    	
		    	}*/
		    	//ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
		    					
				ArrayList listaProcesos = ud.buscarT99Codigo(pool_sp,"","",Constantes.CODTAB_DELEGACION);						
				
				log.debug("datos3: "+datos);
				MantenimientoDelegate md = new MantenimientoDelegate();		    
				HashMap delegados = md.buscarDelegados(datos); //usa  codUO y dbpool
	
			    session.removeAttribute("listaProcesos");
			    session.setAttribute("listaProcesos",listaProcesos);
			    
			    session.removeAttribute("delegados");
			    session.setAttribute("delegados",delegados);
			    
			    session.removeAttribute("datos");
			    session.setAttribute("datos",datos);
			    
			    //Número de Solicitudes en bandeja							//jquispe: ini 05/08/2013
				SolicitudDelegate sd = new SolicitudDelegate();
				ArrayList listaRecibidas = sd.cargarSolicitudesRecibidas(pool_sp,bUsuario.getNroRegistro(), "-1", "", "");
				Integer nroSolicitudes=new Integer(listaRecibidas.size());
				log.debug("nroSolicitudes: "+nroSolicitudes);
				session.removeAttribute("nroSolicitudes");
				session.setAttribute("nroSolicitudes", nroSolicitudes);
				
				//Número de Papeletas en bandeja
				HashMap roles = (HashMap) bUsuario.getMap().get("roles");
				//String codUO = bUsuario.getCodUO(); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
				String codUO = datos.get("codUO").toString(); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
				String uoSeg = bUsuario.getVisibilidad();
				if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");
				if (uoSeg.equals("%")) {
					uoSeg = codUO.substring(0,1);
				}else{
					uoSeg = uoSeg.substring(0,2);
				}
				uoSeg= uoSeg.concat("%");
				log.debug("uoSeg :"+uoSeg);
				
				HashMap seguridad = new HashMap();
				seguridad.put("roles", roles);
				seguridad.put("codPers", bUsuario.getNroRegistro());
				seguridad.put("uoSeg", uoSeg);
				//seguridad.put("uoAO", bUsuario.getCodUO()); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
				seguridad.put("uoAO", codUO); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
				seguridad.put("codUO", codUO);
				seguridad.put("codOpcion", Constantes.DELEGA_PAPELETAS);
				seguridad.put("dbpool", pool_sp);
				
				List listaPapeletas = ad.buscarPapeletasGeneradas(pool_sp, bUsuario.getNroRegistro(), seguridad);
				Integer nroPapeletas=new Integer(listaPapeletas.size());
				log.debug("nroPapeletas2: "+nroPapeletas);
				session.removeAttribute("nroPapeletas");
				session.setAttribute("nroPapeletas", nroPapeletas);				//jquispe: fin
			    
			    RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/mantenimientoDelegados.jsp");
				dispatcher.forward(request, response);
		    }				
			else{
				throw new Exception("Usted no se encuentra habilitado para ejecutar esta opciÃ³n.");			
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
	 * Metodo encargado de registrar los delegados de una UUOO
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void registrarDelegados(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");		    
		    MantenimientoDelegate md = new MantenimientoDelegate();
		    
		    ArrayList listaProcesos = (ArrayList)session.getAttribute("listaProcesos");
		    HashMap delegOrig = (HashMap)session.getAttribute("delegados");
		    HashMap datos1 = (HashMap)session.getAttribute("datos");
		    log.debug("registrarDelegados(delegOrig) :"+delegOrig);
		    log.debug("registrarDelegados(datos1) :"+datos1);
		    
		    HashMap datos = new HashMap();
		    datos.put("dbpool",pool_sp_g);
		    datos.put("codJefe",datos1.get("codJefe"));
		    datos.put("codPers",datos1.get("codJefe"));
		    //datos.put("codUO",bUsuario.getCodUO()); ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
		    datos.put("codUO",datos1.get("codUO")); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
		    //JRR - 09/02/2009 
		    datos.put("cuser_mod", bUsuario.getNroRegistro());
		    log.debug("registrarDelegados(datos) :"+datos);
		    
	    	HashMap delegNuevo = new HashMap();
	    	HashMap info = null;
	    	if ((listaProcesos!=null) && (listaProcesos.size()>0)){
				for(int i=0; i< listaProcesos.size() ;i++){
					
					info = new HashMap();
					
					String cod_deleg = request.getParameter("txtReg"+i);						
					if (cod_deleg!=null && !cod_deleg.trim().equals("")){
						info.put("cod_deleg",cod_deleg);
						info.put("fini",request.getParameter("fechaIni"+i));
						info.put("ffin",request.getParameter("fechaFin"+i));
					}
					
					BeanT99 proceso = (BeanT99)listaProcesos.get(i);	
					delegNuevo.put(proceso.getT99codigo(),info);
				} 
			}
	    	
	    	md.registrarDelegados(listaProcesos,datos,delegOrig,delegNuevo);
	    	cargarDelegados(request,response,session);
			
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
	 * Metodo encargado de eliminar aprobadores de solicitud
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void eliminarAprobadores(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			ArrayList lista = (ArrayList) session.getAttribute("listaAprobadores");
			String[] params = request.getParameterValues("chk_opcion");

			MantenimientoDelegate md = new MantenimientoDelegate();
			md.eliminarAprobadores(params, lista);

			cargarAprobadores(request, response, session);
			
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
	 * Mï¿½todo encargado de cargar la pantalla de bï¿½squeda correspondiente
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarBusquedaUOsinAprob(
		HttpServletRequest request,
		HttpServletResponse response,
		HttpSession session)
		throws ServletException, IOException {

		try {

			String txtValor = request.getParameter("txtValor")!=null?request.getParameter("txtValor"):"txtValor";			    
			String detalle = request.getParameter("detalle")!=null?request.getParameter("detalle"):"detalle";
			BeanTipoMovimiento movimiento = (BeanTipoMovimiento)session.getAttribute("movimiento");
			//busqueda de unid. organ.
			

				session.removeAttribute("listaUO");
				
				session.removeAttribute("txtValor");
				session.setAttribute("txtValor",txtValor);
				session.removeAttribute("detalle");
			    session.setAttribute("detalle",detalle);
			    
			    UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
				String codPers = bUsuario.getNroRegistro();
				
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

				MantenimientoDelegate md = new MantenimientoDelegate();
				ArrayList listaUO =
					md.buscarUOsinAprob(pool_sp, movimiento.getMov(), txtValor, seguridad);

				session.removeAttribute("listaUO");
				session.setAttribute("listaUO", listaUO);

				RequestDispatcher dispatcher =
					getServletContext().getRequestDispatcher("/UOSinApro.jsp");
				dispatcher.forward(request, response);
				return;
			

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
	 * Mï¿½todo encargado de cargar la pantalla de bï¿½squeda correspondiente
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cambiarAprobador(
		HttpServletRequest request,
		HttpServletResponse response,
		HttpSession session)
		throws ServletException, IOException {

		try {
			String txtUO = request.getParameter("txtUO")!=null?request.getParameter("txtUO"):"txtUO";
			String txtOri = request.getParameter("txtOri")!=null?request.getParameter("txtOri"):"txtOri";			    
			String txtDes = request.getParameter("txtDes")!=null?request.getParameter("txtDes"):"txtDes";
			BeanTipoMovimiento movimiento = (BeanTipoMovimiento)session.getAttribute("movimiento");
			//busqueda de unid. organ.
			

				session.removeAttribute("txtUO");
				session.setAttribute("txtUO",txtUO);
				session.removeAttribute("txtOri");
				session.setAttribute("txtOri",txtOri);
				session.removeAttribute("txtDes");
				session.setAttribute("txtDes",txtDes);
			    
				UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
				String codPers = bUsuario.getNroRegistro();
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
				seguridad.put("codPers", codPers);
				seguridad.put("uoSeg", uoSeg);
				seguridad.put("uoAO", bUsuario.getCodUO());//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

				MantenimientoDelegate md = new MantenimientoDelegate();
				boolean Aprobador =
					md.cambiarAprobador(pool_sp_g, movimiento.getMov(), txtUO, txtOri, txtDes, usuario);
				
				session.removeAttribute("cambiaraprobador");
				String mensaje = null;
				if (Aprobador){
					mensaje = "El cambio de Aprobador ha sido realizado Satisfactoriamente.";
				}else{
					mensaje = "El cambio de Aprobador ha tenido problemas. Intente Nuevamente. De persistir el Problema favor comunicarse con Sistemas.";
				}
				session.setAttribute("cambiaraprobador",mensaje);
			    HashMap datos = new HashMap();
			    datos.put("dbpool",pool_sp);
			    datos.put("mov",movimiento.getMov());
			    
			    ArrayList lista = md.buscarAprobadores(datos);
			    session.removeAttribute("listaAprobadores");
			    session.setAttribute("listaAprobadores",lista);
				RequestDispatcher dispatcher =
					getServletContext().getRequestDispatcher("/mantenimientoCambiarAprobadorRes.jsp");
				dispatcher.forward(request, response);
				return;
			

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
	 * Mï¿½todo encargado de cargar los datos de supervisores
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarDatosSupervisor(HttpServletRequest request,
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
			String uoSeg = bUsuario.getCodUO();
			if (roles.get(Constantes.ROL_ANALISTA_CENTRAL)!=null) uoSeg = "";
			
			MantenimientoDelegate md = new MantenimientoDelegate();

			ArrayList tiposMovimientos = md.cargarT99(pool_sp, Constantes.CODTAB_SUPERVISOR);
		    
		    HashMap datos = new HashMap();
		    datos.put("dbpool",pool_sp);
		    datos.put("codUO",uoSeg);
		    
		    
		    //ArrayList lista = md.buscarSupervisores(datos);							//jquispe:ini	
		    ArrayList lista=new ArrayList();											
		    session.removeAttribute("listaSupervisores");
		    session.setAttribute("listaSupervisores",lista);
		    
		    ArrayList listaMov = md.cargarT99(pool_sp, Constantes.CODTAB_SUPERVISOR);	
		    session.removeAttribute("listaMovSupervisor");								
		    session.setAttribute("listaMovSupervisor",listaMov);						//jquispe:fin
		    
			session.removeAttribute("movimiento");
			session.removeAttribute("listaTipos");
			session.removeAttribute("listaMedidas");

			session.setAttribute("listaTipos", tiposMovimientos);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/mantenimientoSupervisor.jsp");
			dispatcher.forward(request, response);
			return;
		} catch (MantenimientoException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			log.error("Error Cargar Supervisor : " + e.getStackTrace());
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
	 * Metodo encargado de eliminar supervisores
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void eliminarSupervisores(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			ArrayList lista = (ArrayList) session.getAttribute("listaSupervisores");
			String[] params = request.getParameterValues("chk_opcion");

			MantenimientoDelegate md = new MantenimientoDelegate();
			md.eliminarSupervisores(pool_sp_g,params, lista);

			cargarDatosSupervisor(request, response, session);
			
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
	 * Mï¿½todo encargado de cargar los datos de un supervisor
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarSupervisor(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			ArrayList listaSupervisores = (ArrayList) session.getAttribute("listaSupervisores");
			log.debug("Lista " + listaSupervisores);
			String indice = request.getParameter("indice");
			log.debug("Indice " + indice);
			HashMap superv = (HashMap) listaSupervisores.get(Integer.parseInt(indice));
			log.debug("Superv " + superv);
			session.removeAttribute("superv");
			session.setAttribute("superv", superv);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/mantenimientoModifSupervisor.jsp");
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
	 * Mï¿½todo encargado de modificar los datos de un perï¿½odo
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void modificarSupervisor(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			HashMap superv = (HashMap) session.getAttribute("superv");

			String fechaIni = request.getParameter("fechaIni");
			String fechaFin = request.getParameter("fechaFin");
			String estado = request.getParameter("estado");

			UsuarioBean user = (UsuarioBean) session.getAttribute("usuarioBean");
			String usuario = user.getLogin();
			MantenimientoDelegate md = new MantenimientoDelegate();
			md.modificarSupervisor(pool_sp_g, superv, fechaIni,
					fechaFin, estado, usuario);

			cargarDatosSupervisor(request, response, session);

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
	 * Mï¿½todo encargado de registrar un Supervisor
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void registrarSupervisor(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, String codMov)
			throws ServletException, IOException {

		try {

			//String codMov = request.getParameter("cmb_mov"); 		//jquispe
			String UO = request.getParameter("txtUO");
			String superv = request.getParameter("txtResp");
			String fechaIni = request.getParameter("fechaIni");
			String fechaFin = request.getParameter("fechaFin");

			UsuarioBean user = (UsuarioBean) session.getAttribute("usuarioBean");
			String usuario = user.getLogin();

			//registramos el turno
			MantenimientoDelegate md = new MantenimientoDelegate();
			md.registrarSupervisor(pool_sp_g, codMov, UO.trim().toUpperCase(), superv.trim().toUpperCase(), fechaIni, 
					fechaFin, usuario);

			//cargamos los supervisores
			//cargarDatosSupervisor(request, response, session);	//jquispe

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
	 * Metodo encargado de registrar un Supervisor para varios movimientos
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 * @author jquispe 05/06/2013
	 */	
	private void registrarSupervisorMovs(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
	
			String movs = request.getParameter("movs");
			ArrayList listaMovSupervisor = (ArrayList)session.getAttribute("listaMovSupervisor");
			for (int i = 0; i < listaMovSupervisor.size(); i++) {
				BeanT99 tipo = (BeanT99) listaMovSupervisor.get(i);
				if (movs.substring(i,i+1).compareTo("1")==0){
					registrarSupervisor(request, response, session, tipo.getT99codigo());
				}
			}

			//cargamos los supervisores
			cargarDatosSupervisor(request, response, session);

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
	 * Metodo encargado de buscar los aprobadores de una solicitud
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarSupervisores(HttpServletRequest request,
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
			String uoSeg = bUsuario.getCodUO();
		    String txtUO = request.getParameter("txtUO")!=null?request.getParameter("txtUO"):"";
		    String txtOri = request.getParameter("txtOri")!=null?request.getParameter("txtOri"):"";
			if (roles.get(Constantes.ROL_ANALISTA_CENTRAL)!=null) uoSeg = txtUO;
			
		    
		    MantenimientoDelegate md = new MantenimientoDelegate();
		    

		    
		    HashMap datos = new HashMap();
		    datos.put("dbpool",pool_sp);
		    datos.put("codUO",uoSeg);
		    log.debug("DATOS " + datos);
		    if (txtOri.equals("")) {
		    	txtUO=uoSeg;
		    }
		    datos.put("codUO",txtUO.trim().toUpperCase());
		    datos.put("cod_pers_ori",txtOri.trim().toUpperCase());
		    log.debug("DATOS1 " + datos);
		    ArrayList lista = md.buscarSupervisores(datos);
		   
		    session.removeAttribute("listaSupervisores");
		    session.setAttribute("listaSupervisores",lista);	
		    
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/mantenimientoSupervisor.jsp");
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
	 * Metodo encargado de cargar la pagina para el mantenimiento de delegados
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarDeleAnalista(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

				log.debug("ENTRE cargarDeleAnalista");
			    RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/mantenimientoDeleAnalIng.jsp");
				dispatcher.forward(request, response);

			
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
	 * Metodo encargado de cargar la pagina para el mantenimiento de delegados
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarDeleAnalIng(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			//BeanUsuario bUsuario = (BeanUsuario)session.getAttribute("beanusuario");
		    String UO = request.getParameter("txtUO");
		    if (UO==null){
		    	UO = (String)session.getAttribute("txtUO");
		    }
		    UO= UO.toUpperCase();
			HashMap datos = new HashMap();
		    datos.put("dbpool",pool_sp);
		    datos.put("codOpcion", Constantes.DELEGA_TODO);
		    datos.put("codUO",UO);
		    
		    log.debug("DATOS : "+datos);
		    UtilesDelegate ud = new UtilesDelegate();
		    //AsistenciaDelegate ad = new AsistenciaDelegate();
		    HashMap jefe = ud.buscarJefe(pool_sp, UO, " ");
		    
		    log.debug("Encontre JEFE ANALISTA "+ jefe);
		    String codjefeEncargado = (String)jefe.get("t12cod_encar" );
		    String codjefe = (String)jefe.get("t12cod_jefat" );
		    String desjefe = (String)jefe.get("t12cod_jefat_desc" );
		    log.debug("Encontre JEFE ANALISTA "+ codjefe);
		    log.debug("codjefeEncargado" + codjefeEncargado.length());
		    log.debug("codjefeEncargado" + codjefeEncargado.trim().length());
		    if (codjefe.equals(" ") || codjefe.equals("") || codjefe ==null ||(codjefeEncargado!=null  && codjefeEncargado.trim().length()>0) ){
		    	
		    	codjefe = (String)jefe.get("t12cod_encar" );
		    	desjefe = (String)jefe.get("t12cod_encar_desc" );
		    }
		    datos.put("codJefe",codjefe);
		    datos.put("codPers",codjefe);
		    
		    log.debug("Encontre DATOS ANALISTA "+ datos);
		    datos.put("nomJefe",desjefe.toUpperCase());
		    BeanT12 unidadUO = ud.buscarUObyCodigo(pool_sp, UO.trim());
		    datos.put("descUO", unidadUO.getT12descr_uorg());

		    	/* ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp (se comenta porque no trae el encargado o jefe correcto de la unidad a delegar)
		        HashMap trab = ud.buscarTrabajador(pool_sp,codjefe,null);
		    	log.debug("Encontre TRAB "+ trab);
		    	if (!((String)trab.get("t12cod_jefe")).equals(codjefe)){		    	
		    		trab = ud.buscarTrabajador(pool_sp,(String)trab.get("t12cod_jefe"),null);		    		
		    		datos.put("codJefe",trab.get("t02cod_pers"));
		    		datos.put("nomJefe",trab.get("t02ap_pate")+" "+trab.get("t02ap_mate")+", "+trab.get("t02nombres"));		    	
		    	}*/ //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
		    					
				ArrayList listaProcesos = ud.buscarT99Codigo(pool_sp,"","",Constantes.CODTAB_DELEGACION);						
				
				MantenimientoDelegate md = new MantenimientoDelegate();		    
				HashMap delegados = md.buscarDelegados(datos);  
	
			    session.removeAttribute("listaProcesos");
			    session.setAttribute("listaProcesos",listaProcesos);
			    
			    session.removeAttribute("delegados");
			    session.setAttribute("delegados",delegados);
			    
			    session.removeAttribute("datos");
			    session.setAttribute("datos",datos);
			    
			    RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/mantenimientoDeleAnalista.jsp");
				dispatcher.forward(request, response);

			
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
	 * Metodo encargado de registrar los delegados de una UUOO
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void registrarDeleAnalista(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

		    //BeanUsuario bUsuario = (BeanUsuario)session.getAttribute("beanusuario");
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
		    MantenimientoDelegate md = new MantenimientoDelegate();
		    
		    ArrayList listaProcesos = (ArrayList)session.getAttribute("listaProcesos");
		    HashMap delegOrig = (HashMap)session.getAttribute("delegados");
		    HashMap datos1 = (HashMap)session.getAttribute("datos");
		    log.debug("registrarDeleAnalista(delegOrig) :"+delegOrig);
		    log.debug("registrarDeleAnalista(datos1) :"+datos1);
		    
		    HashMap datos = new HashMap();
		    datos.put("dbpool",pool_sp_g);
		    datos.put("codJefe",datos1.get("codJefe"));
		    datos.put("codPers",datos1.get("codJefe"));
		    datos.put("codUO",datos1.get("codUO"));
		    //JRR - 09/02/2009
		    datos.put("cuser_mod", bUsuario.getNroRegistro());
		    log.debug("registrarDeleAnalista(datos) :"+datos);
		    
	    	HashMap delegNuevo = new HashMap();
	    	HashMap info = null;
	    	if ((listaProcesos!=null) && (listaProcesos.size()>0)){
				for(int i=0; i< listaProcesos.size() ;i++){
					
					info = new HashMap();
					
					String cod_deleg = request.getParameter("txtReg"+i);						
					if (cod_deleg!=null && !cod_deleg.trim().equals("")){
						info.put("cod_deleg",cod_deleg);
						info.put("fini",request.getParameter("fechaIni"+i));
						info.put("ffin",request.getParameter("fechaFin"+i));
					}
					
					BeanT99 proceso = (BeanT99)listaProcesos.get(i);	
					delegNuevo.put(proceso.getT99codigo(),info);
				} 
			}
	    	
	    	md.registrarDelegados(listaProcesos,datos,delegOrig,delegNuevo);
	    	session.setAttribute("txtUO", datos.get("codUO"));
	    	cargarDeleAnalIng(request,response,session);
			
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
	
	/* ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA - 22/08/2011 */
	/**
	 * Metodo encargado de cargar todos los movimientos activos de la t1279tipo_mov
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarCategorias(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		
		try {
			MantenimientoDelegate md = new MantenimientoDelegate();
			//ArrayList movimientosT1279 = md.buscarMovimientosActivos(pool_sp); //nuevo agregado 05/12/2011 icapunay
			ArrayList movimientosT1279 = md.buscarMovimientosActivosInactivos(pool_sp); //nuevo agregado 05/12/2011 icapunay
			
			
			ArrayList categorias = md.buscarCategorias(pool_sp);

			session.removeAttribute("listaCategorias");
			session.removeAttribute("listaMovimientos");
			session.setAttribute("listaMovimientos", movimientosT1279);
			session.setAttribute("listaCategorias", categorias);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/mantenimientoCategorias.jsp");
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
	 * Metodo encargado de eliminar categorias y sus movimientos incluidos
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void eliminarCategorias(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			MantenimientoDelegate md = new MantenimientoDelegate();
			ArrayList listaCategorias = (ArrayList) session.getAttribute("listaCategorias");
			String[] params = request.getParameterValues("chk_opcion");
			md.eliminarCategorias(params, listaCategorias); //elimina las categorias seleccionadas y sus movimientos
			cargarCategorias(request, response, session); //carga de nuevo la pagina mantenimientoCategorias.jsp

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
	 * Metodo encargado de buscar categorias de acuerdo al movimiento que incluyen
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarCategorias(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			String movimiento = request.getParameter("cmbMovimiento");
			
			MantenimientoDelegate md = new MantenimientoDelegate();	
			
			if(!movimiento.equals("-1")){ //SI selecciono un movimiento
				ArrayList listaCategoriasFiltradas = md.buscarCategoriasByMovimiento(pool_sp,movimiento);

				session.removeAttribute("listaCategorias");
				session.setAttribute("listaCategorias", listaCategoriasFiltradas);
			}
			else{//NO selecciono un movimiento
				ArrayList listaAllCategorias = md.buscarCategorias(pool_sp);

				session.removeAttribute("listaCategorias");
				session.setAttribute("listaCategorias", listaAllCategorias);
			}
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/mantenimientoCategorias.jsp");
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
	 * Metodo encargado de registrar y modificar una categoria con sus movimientos
	 * 
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param session HttpSession
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarDatosCategoria(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			
			String indice = request.getParameter("indice");
			MantenimientoDelegate md = new MantenimientoDelegate();			
			HashMap moviCategorias = new HashMap();
			HashMap categoriaSeleccionada=null;
			ArrayList listaMovPorAsignar= new ArrayList();
			
			ArrayList listaAllMovT1279Activos = (ArrayList) session.getAttribute("listaMovimientos");
			
			ArrayList listaMovAsigAllCategorias = md.cargarMovAsignadosAllCategorias();
			
			if(listaMovAsigAllCategorias!=null && listaMovAsigAllCategorias.size()>0){
				listaMovPorAsignar = md.cargarMovPorAsignar(listaMovAsigAllCategorias,listaAllMovT1279Activos);
			}else{
				listaMovPorAsignar = listaAllMovT1279Activos;
			}
								
			if (!indice.equals("-1")) { //modificar categoria y movimientos

				ArrayList listaCategorias = (ArrayList) session.getAttribute("listaCategorias");
				categoriaSeleccionada = (HashMap) listaCategorias.get(Integer.parseInt(indice));
				session.removeAttribute("categoria");
				session.setAttribute("categoria", categoriaSeleccionada);

				ArrayList listaMovAsignados = md.cargarMovAsignados(categoriaSeleccionada.get("cod_cate").toString());				

				moviCategorias.put("listaAsignados", listaMovAsignados);
				moviCategorias.put("listaPorAsignar", listaMovPorAsignar);
				
			} //fin modificar categoria y movimientos
			else 
			{ //nueva categoria y movimientos	
				
				log.debug("listaMovPorAsignar-cargarDatosCategoria: "+listaMovPorAsignar);
				moviCategorias.put("listaPorAsignar", listaMovPorAsignar);
				String nuevo_codCate = md.devolverNuevoCodigoCategoria(pool_sp);
				session.removeAttribute("nuevo_codCate");
				session.setAttribute("nuevo_codCate", nuevo_codCate);
				log.debug("nuevo_codCate-cargarDatosCategoria: "+nuevo_codCate);
				
			}// fin nueva categoria y movimientos

			session.removeAttribute("moviCategorias");
			session.setAttribute("moviCategorias", moviCategorias);	
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/registroCategorias.jsp");
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
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void registrarCategoriaMovimientos(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean)session.getAttribute("usuarioBean");
			HashMap categoriaSeleccionada = (HashMap) session.getAttribute("categoria");
			String nuevo_codCate = (String) session.getAttribute("nuevo_codCate");					
			String descrip_cate = request.getParameter("descrip_cate");
			String movimientosAsignadosConcatenados = request.getParameter("movimientos");
			HashMap moviCategorias = new HashMap();
			MantenimientoDelegate md = new MantenimientoDelegate();
			
			if(categoriaSeleccionada!=null && !categoriaSeleccionada.isEmpty()){
				moviCategorias.put("cod_cate", categoriaSeleccionada.get("cod_cate").toString());
			}else{
				moviCategorias.put("nuevo_codCate", nuevo_codCate);
			}			
			moviCategorias.put("descrip", descrip_cate);
			moviCategorias.put("cod_usucreac",bUsuario.getLogin());
			moviCategorias.put("fec_creac",  new FechaBean().getFormatDate("yyyy-MM-dd"));
			moviCategorias.put("movimientos", movimientosAsignadosConcatenados);
						
			md.registrarCategoria(pool_sp_g,moviCategorias);//registra y/o actualiza la categoria y sus movimientos 		
			
			ArrayList listaCategoriasActuales = md.buscarCategorias(pool_sp);			
			session.removeAttribute("listaCategorias");
			session.setAttribute("listaCategorias", listaCategoriasActuales);	
			
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/mantenimientoCategorias.jsp");
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
	/* FIN ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA - 22/08/2011 */

	/**
	 * 
	 * Metodo encargado de cargar la pagina para el mantenimiento de aprobadores de solicitud
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException, IOException
	 * @author jmaravi
	 * @since 13/03/2014
	 */
	private void cargarRegistroAprobadores(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			
			String sUniOrgan = request.getParameter("txtUniOrg") == null ?"":request.getParameter("txtUniOrg").toString().toUpperCase();
			MantenimientoDelegate md = new MantenimientoDelegate();

			pe.gob.sunat.rrhh.dao.T12DAO t12dao = new pe.gob.sunat.rrhh.dao.T12DAO(pool_sp); 
			HashMap hmUniOrgan = (HashMap) t12dao.findByCodUorga(sUniOrgan);

			HashMap datos = new HashMap();
			datos.put("uniOrgan", hmUniOrgan.get("t12cod_uorga"));
			ArrayList alMovimientos = md.findMovimientosFlujosAprobadores(pool_sp,datos);			

		    session.removeAttribute("alMovimientos");
		    session.setAttribute("alMovimientos",alMovimientos);
		    
		    session.removeAttribute("hmUniOrgan");
		    session.setAttribute("hmUniOrgan",hmUniOrgan);
		    
			if(hmUniOrgan.get("t12cod_uorga").equals("-") && !sUniOrgan.equals("")){
				messagesWeb.setMensajeWeb(MessagesWeb.TYPE_ERROR,"El código de unidad ingresado no existe, por favor verifique y vuelva a intentarlo.");
			}		
			
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/mantenimientoRegistroAprobadores.jsp");
			dispatcher.forward(request, response);
			return;
			
			
		} catch (MantenimientoException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "	+ e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}
	}	
	
	/**
	 * 
	 * Metodo encargado de mostrar el detalle de las solicitudes pendientes de aprobación.
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException, IOException
	 * @author jmaravi
	 * @since 10/04/2014
	 */
	private void mostrarPendientesAprobacion(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			String uniOrgan = (String)request.getParameter("uni_org");
			String tipo_mov = (String)request.getParameter("tipo_mov");
			String cod_aprob = (String)request.getParameter("cod_aprob");
			
			MantenimientoDelegate md = new MantenimientoDelegate();
			
			HashMap datos = new HashMap();
			datos.put("uniOrgan", uniOrgan);
			datos.put("tipo_mov", tipo_mov);
			datos.put("cod_aprob", cod_aprob);

			ArrayList alPendientesAprob = md.obtenerPendientesxAprobador(pool_sp,datos);

		    session.removeAttribute("alPendientesAprob");
		    session.setAttribute("alPendientesAprob",alPendientesAprob);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/mantenimientoRegistroAprobadoresPend.jsp");
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
	 * 
	 * Permite capturar todas las modificaciones en los flujos, las valida y
	 * si pasan exitosamente las verificaciones se procede a grabar el cambio.
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException, IOException
	 * @author jmaravi
	 * @since 14/04/2014
	 */
	private void registrarFlujosAprobadores(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			ArrayList alMovimientos = (ArrayList)session.getAttribute("alMovimientos");
			String sDerivarPendientes = (String)request.getParameter("cmbDerivar");
			MantenimientoDelegate md = new MantenimientoDelegate();
			UsuarioBean bUsuario = (UsuarioBean)session.getAttribute("usuarioBean");			
			
			session.removeAttribute("cmbDerivarValue");
			session.setAttribute("cmbDerivarValue", sDerivarPendientes);
			
			String u_organ = ((HashMap)alMovimientos.get(0)).get("u_organ").toString().trim().toUpperCase();
			
			HashMap flujoMov;	
			String mov_cod="";
			String sAprobNew="";
			//Cargamos todos los valores registrados por el usuario 
			for(int i = 0; i < alMovimientos.size(); i++ ){
				flujoMov = (HashMap)alMovimientos.get(i);
				mov_cod = (String)flujoMov.get("mov_cod");
				sAprobNew = (String)request.getParameter(u_organ+"_"+mov_cod+"_"+"1");
				flujoMov.put("aprob_ini_new", sAprobNew == null?"":sAprobNew.trim().toUpperCase() );
				sAprobNew = (String)request.getParameter(u_organ+"_"+mov_cod+"_"+"2");
				flujoMov.put("aprob_int_new", sAprobNew == null?"":sAprobNew.trim().toUpperCase());
				sAprobNew = (String)request.getParameter(u_organ+"_"+mov_cod+"_"+"3");
				flujoMov.put("aprob_fin_new", sAprobNew == null?"":sAprobNew.trim().toUpperCase());		
			}
			
			HashMap datos = new HashMap();
			datos.put("alMovimientos", alMovimientos);
			datos.put("sDerivarPendientes", sDerivarPendientes);
			datos.put("usuarioBean", bUsuario);
			datos.put("messagesWeb",messagesWeb);
			md.registrarFlujosAprobadores(pool_sp_g, datos);			
			
			//actualizamos los datos a mostrar.
			HashMap hmDatos = new HashMap();
			hmDatos.put("uniOrgan", u_organ);
			alMovimientos = md.findMovimientosFlujosAprobadores(pool_sp,hmDatos);

		    session.removeAttribute("alMovimientos");
		    session.setAttribute("alMovimientos",alMovimientos);
			
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/mantenimientoRegistroAprobadores.jsp");
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
	 * 
	 * Metodo encargado de realizar la carga masiva (.xlsx) de aprobadores.
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException, IOException
	 * @author jmaravi
	 * @since 21/04/2014
	 */
	private void registrarFlujosAprobadoresMasivo(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, DynaBean dbean)
			throws ServletException, IOException {

		try {
			MantenimientoDelegate mantenDelegate = new MantenimientoDelegate();
			//String sDerivarPendientes = request.getParameter("cmbDerivarMasivo")==null?"XX":request.getParameter("cmbDerivarMasivo").toString();
			int tamanio=0; 
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
			
			if (log.isDebugEnabled()) log.debug("seguridad registrarFlujosAprobadoresMasivo: " + seguridad);				
				
			if (log.isDebugEnabled()) log.debug("Es Multipart?: "+dbean.isMultipart());		
							
			String dir_upload = ServiceLocator.getInstance().getString("java:comp/env/dir_upload");
			
			String ruta_arch=dir_upload+dbean.getString("archivo-filter-filename");
			
			if(log.isDebugEnabled()) log.debug("dir_upload " + dir_upload);
			if(log.isDebugEnabled()) log.debug("ruta_arch " + ruta_arch);			
			
			String size_arch=dbean.getString("archivo-size");
			if(log.isDebugEnabled()) log.debug("size_arch " + size_arch);
			tamanio = Integer.parseInt(size_arch);
			if(log.isDebugEnabled()) log.debug("tamanio archivo " + String.valueOf(tamanio));
			
			if(tamanio>0 && tamanio<=512000){ //validando que el archivo sea menor o igual a 64 KB (65536 bytes)
				File archivoXlsx = new File(ruta_arch);			
				String[] cabecera = new String[]{"UniOrg", "Instancia1", "Instancia2","Instancia3"};
				dbean.put("cabeceraXlsx", cabecera);
				dbean.put("rowIniData", Integer.valueOf(String.valueOf(3) ));
				dbean.put("derivarPendientes", dbean.get("cmbDerivarMasivo"));
				
				FileConverter converter = new FileConverter();				
				List archivo=converter.flujoAprobadoresXlsxToList(archivoXlsx,dbean);	
				
				if(log.isDebugEnabled()) log.debug("archivo " + archivo);
				
				dbean.put("codPers", codPers);
				dbean.put("usuario", usuario);
				dbean.put("seguridad", seguridad);
				dbean.put("archivo", archivo);
				dbean.put("dbpool", pool_sp_g);				
			
				if (log.isDebugEnabled()) log.debug("Ingresando a MantenimientoDelegate");				
				
				String resultado = mantenDelegate.registroFlujoAprobadoresMasivo(dbean);				
				
				if (log.isDebugEnabled()) log.debug("Saliendo de MantenimientoDelegate");
				if (log.isDebugEnabled()) log.debug("Estado registro masivo: "+resultado);
				
				if (resultado.equals(Constantes.OK)) {	
					
					request.setAttribute("nomProceso","REGISTRO FLUJO APROBADORES");					
					forward(request,response,"/resultadoProceso.jsp"+"?idSession="+System.currentTimeMillis());
					return;
					
				}else {						
					throw new Exception(resultado);
				}				
			}else{
				
				if (log.isDebugEnabled()) log.debug("Error en tamaño de archivo");
				throw new Exception("Sólo puede subir archivos de hasta un tamaño de 500 KB . Por favor intente adjuntar otro archivo de menor tamaño.");
			}

			//RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/mantenimientoRegistroAprobadoresMasivo.jsp");
			//dispatcher.forward(request, response);
			
			
			
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
		return;
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