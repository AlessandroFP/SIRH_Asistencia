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
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.apache.log4j.NDC;

import pe.gob.sunat.framework.core.bean.MensajeBean;
import pe.gob.sunat.framework.core.pattern.DynaBean;
import pe.gob.sunat.framework.core.pattern.ServiceLocator;
import pe.gob.sunat.framework.core.servlet.ServletAbstract;
import pe.gob.sunat.framework.util.cache.dao.ParamDAO;
import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.rrhh.asistencia.dao.T4819DAO;
import pe.gob.sunat.sol.BeanMensaje;
//import pe.gob.sunat.sol.seguridad.acceso.jaas.AutenticacionException;
//import pe.gob.sunat.sol.seguridad.acceso.jaas.ServletAutenticacionIntranet;
//import pe.gob.sunat.sol.seguridad.acceso.portal.bean.BeanUsuario;
import pe.gob.sunat.sp.asistencia.bean.BeanHoraExtra;
import pe.gob.sunat.sp.asistencia.bean.BeanTurnoTrabajo;
import pe.gob.sunat.sp.asistencia.dao.T1270DAO;
import pe.gob.sunat.sp.asistencia.ejb.delegate.AsistenciaDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.AsistenciaException;
import pe.gob.sunat.sp.asistencia.ejb.delegate.HoraExtraDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.HoraExtraException;
import pe.gob.sunat.sp.asistencia.ejb.delegate.SolicitudDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.UtilesDelegate;
import pe.gob.sunat.tecnologia.menu.bean.UsuarioBean;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;

/**
 * @web.servlet name="ServletHoraExtra"
 * @web.servlet-mapping url-pattern = "/asisS08Alias"
 * @web.servlet-init-param name = "pool_sp" value = "jdbc/dcsp"
 * @web.servlet-init-param name = "pool_recauda" value = "jdbc/dcrecauda"
 * @web.servlet-init-param name = "pool_sp_g" value = "jdbc/dgsp"
 *  
 * Title: ServletHoraExtra
 * Description: Clase encargada de administrar las funcionalidades de labor
 * excepcional
 * Copyright: Copyright (c) 2004
 * Company: Sunat
 * @author cgarratt
 * @version 1.0
 */
public class ServletHoraExtra extends ServletAbstract {

	private static final Logger log = Logger.getLogger(ServletHoraExtra.class);
	private static String pool_sp;
	private static String pool_sp_g;
	private static String pool_recauda;//ICAPUNAY 09/04/2012 AOM 06A4T11 LABOR EXCEPCIONAL Y COMPENSACIONES-PAS20124E550000064
	private static DataSource dsRecauda;//ICAPUNAY 09/04/2012 AOM 06A4T11 LABOR EXCEPCIONAL Y COMPENSACIONES-PAS20124E550000064

	public ServletHoraExtra() {
	}

	public void destroy() {
		super.destroy();
	}

	public void init(ServletConfig config) throws ServletException {
		try {//ICAPUNAY 09/04/2012 AOM 06A4T11 LABOR EXCEPCIONAL Y COMPENSACIONES-PAS20124E550000064		
			super.init(config);
			pool_sp = config.getInitParameter("pool_sp");
			pool_sp_g = config.getInitParameter("pool_sp_g");
			pool_recauda = config.getInitParameter("pool_recauda");//ICAPUNAY 09/04/2012 AOM 06A4T11 LABOR EXCEPCIONAL Y COMPENSACIONES-PAS20124E550000064

			//ICAPUNAY 09/04/2012 AOM 06A4T11 LABOR EXCEPCIONAL Y COMPENSACIONES-PAS20124E550000064
			cargarParametros();
		} catch (Exception e) {
			log.error("**** Error ****", e);
			throw new ServletException(e.getMessage());
		} finally {
		}
		//FIN ICAPUNAY 09/04/2012 AOM 06A4T11 LABOR EXCEPCIONAL Y COMPENSACIONES-PAS20124E550000064
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
		//BeanUsuario bUsuario = (BeanUsuario) session.getAttribute("beanusuario");
		//NDC.push(bUsuario.getNumreg());		
		log.info("Accion : " + accion);

		if (accion.equals("cargarHorasExtras")) {
			cargarHorasExtras(request, response, session);
		}
		if (accion.equals("registrarAutorizacionHE")) {
			registrarAutorizacionHE(request, response, session);
		}
		if (accion.equals("cargarDatosHoraExtra")) {
			cargarDatosHoraExtra(request, response, session);
		}
		if (accion.equals("modificarAutorizacionHE")) {
			modificarAutorizacionHE(request, response, session);
		}
		if (accion.equals("eliminarHorasExtras")) {
			eliminarHorasExtras(request, response, session);
		}
		if (accion.equals("eliminarTrabHE")) {
			eliminarTrabHE(request, response, session);
		}
		if (accion.equals("buscarHorasExtras")) {
			buscarHorasExtras(request, response, session);
		}
		if (accion.equals("verificaSalidaHE")) {
			verificaSalidaHE(request, response, session);
		}
		if (accion.equals("registrarSalidaHE")) {
			registrarSalidaHE(request, response, session);
		}
		if (accion.equals("cargaAcumulacionHE")) {
			cargarAcumulacionHE(request, response, session);
		}
		if (accion.equals("procesarAcumulacionHE")) {
			procesarAcumulacionHE(request, response, session);
		}
		if (accion.equals("cargarCompensacionHE")) {
			cargarCompensacionHE(request, response, session);
		}
		if (accion.equals("buscarAcumulados")) {
			buscarAcumulados(request, response, session);
		}
		if (accion.equals("cargarDatosCompensacionHE")) {
			cargarDatosCompensacionHE(request, response, session);
		}
		if (accion.equals("registrarCompensacionHE")) {
			registrarCompensacionHE(request, response, session);
		}
		if (accion.equals("verAutorizaciones")) {
			verAutorizaciones(request, response, session);
		}
		//ICAPUNAY 09/04/2012 AOM 06A4T11 LABOR EXCEPCIONAL Y COMPENSACIONES-PAS20124E550000064
		if (accion.equals("cargarProcesarLaborExcep")) {
			cargarProcesarLaborExcep(request, response, session);
		}
		if (accion.equals("procesarLaborExcepcional")) {
			procesarLaborExcepcional(request, response, session);
		}
		//FIN ICAPUNAY 09/04/2012 AOM 06A4T11 LABOR EXCEPCIONAL Y COMPENSACIONES-PAS20124E550000064
		
		//ICAPUNAY 06/05/2012 AOM 06A4T11 LABOR EXCEPCIONAL Y COMPENSACIONES-PAS20124E550000064
		if (accion.equals("cargarRegistroTemporal")) {
			cargarRegistroTemporal(request, response, session);
		}
		if (accion.equals("buscarHorasAutorizadas")) {
			buscarHorasAutorizadas(request, response, session);
		}
		if (accion.equals("registrarCompensacionTemporal")) {
			registrarCompensacionTemporal(request, response, session);
		}
		//FIN ICAPUNAY 06/05/2012 AOM 06A4T11 LABOR EXCEPCIONAL Y COMPENSACIONES-PAS20124E550000064
		}
		catch(Exception e){
			  log.error("*** Error ***", e);
			  
		} finally{
			  NDC.pop();
			  NDC.remove();
		}	
	}

	/**
	 * Mï¿½todo encargado de buscar los saldos acumulados de horas extras
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarAcumulados(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();//.getNumreg();
			//BeanUsuario bUsuario = (BeanUsuario) session.getAttribute("beanusuario");
			//String codPers = bUsuario.getNumreg();
			String codUO = bUsuario.getCodUO();
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
			String valor = request.getParameter("txtValor") != null ? request
					.getParameter("txtValor") : "";

			HoraExtraDelegate hed = new HoraExtraDelegate();
			ArrayList listaAcumulados = hed.buscarAcumulados(pool_sp, criterio,
					valor, codUO, seguridad);

			session.removeAttribute("listaAcumulados");
			session.setAttribute("listaAcumulados", listaAcumulados);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/compensacionHE.jsp");
			dispatcher.forward(request, response);
			return;

		} catch (HoraExtraException e) {
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
	 * Mï¿½todo encargado de cargar los datos para la compensaciï¿½n
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarDatosCompensacionHE(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			ArrayList lista = (ArrayList) session
					.getAttribute("listaAcumulados");
			String indice = request.getParameter("indice");
			BeanHoraExtra trabHE = (BeanHoraExtra) lista.get(Integer
					.parseInt(indice));

			session.removeAttribute("trabHE");
			session.setAttribute("trabHE", trabHE);

			session.removeAttribute("listaCompensacion");

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/registrarCompensacionHE.jsp");
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
	 * Mï¿½todo encargado de registrar la compensaciï¿½n de horas extras
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void registrarCompensacionHE(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();//.getNumreg();
			//String codPers = bUsuario.getNumreg();
			String codUO = bUsuario.getCodUO();
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
			seguridad.put("codUO", codUO);
			seguridad.put("codOpcion", Constantes.DELEGA_LABOR_EXCEP);
			seguridad.put("dbpool", pool_sp);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("uoAO", bUsuario.getCodUO());//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			BeanHoraExtra trabHE = (BeanHoraExtra) session.getAttribute("trabHE");

			String fechaComp = request.getParameter("fechaComp");
			//actualizamos el acumulado y registramos la licencia
			HoraExtraDelegate hed = new HoraExtraDelegate();
			String res = hed.registraCompensacionHE(pool_sp_g, trabHE
					.getCodPers(), codUO, fechaComp, usuario);

			if (res.equals(Constantes.OK)) {

				//actualizamos la lista con los acumulados
				ArrayList listaAcumulados = hed.buscarAcumulados(pool_sp,
						"", "", codUO, seguridad);

				session.removeAttribute("trabHE");
				session.setAttribute("trabHE", trabHE);

				session.removeAttribute("listaAcumulados");
				session.setAttribute("listaAcumulados", listaAcumulados);
			}

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/compensacionHE.jsp");
			dispatcher.forward(request, response);

		} catch (HoraExtraException e) {
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
	 * Mï¿½todo encargado de buscar las autorizaciones de horas extras
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarHorasExtras(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codJefe = bUsuario.getNroRegistro();//.getNumreg();
			//String codJefe = bUsuario.getNumreg();
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
			seguridad.put("codPers", codJefe);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("uoAO", bUsuario.getCodUO());//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			String criterio = request.getParameter("cmbCriterio") != null ? request
					.getParameter("cmbCriterio") : "";
			String valor = request.getParameter("txtValor") != null ? request
					.getParameter("txtValor") : "";

			HoraExtraDelegate hed = new HoraExtraDelegate();
			ArrayList horas = hed.buscarHorasExtras(pool_sp, criterio, valor,
					codJefe, seguridad);

			session.removeAttribute("listaHorasExtras");
			session.setAttribute("listaHorasExtras", horas);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/mantenimientoHorasExtras.jsp");
			dispatcher.forward(request, response);
			return;
		} catch (HoraExtraException e) {
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
	 * Mï¿½todo encargado de verificar si a un trabajador posee autorizaciï¿½n para
	 * la fecha actual
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void verificaSalidaHE(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean user = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPersonal = user.getNroRegistro();//.getNumreg();
			//String codPersonal = user.getNumreg();

			HoraExtraDelegate hd = new HoraExtraDelegate();
			BeanHoraExtra salida = hd.verificaSalidaHE(pool_sp, codPersonal);

			if (salida != null) {

				session.removeAttribute("salidaHE");
				session.setAttribute("salidaHE", salida);

				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/registrarSalidaHE.jsp");
				dispatcher.forward(request, response);
				return;
			} else {
				throw new Exception(
						"Ud. no posee autorizaci&oacute;n de labor excepcional para el d&iacute;a de hoy.");
			}

		} catch (HoraExtraException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opciÃ³n.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * Mï¿½todo que registra la salida de un trabajador para labor excepcional
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void registrarSalidaHE(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPersonal = bUsuario.getNroRegistro();//.getNumreg();
			//String codPersonal = user.getNumreg();

			BeanHoraExtra salida = (BeanHoraExtra) session
					.getAttribute("salidaHE");
			String horaSalida = Utiles.obtenerHoraActual();

			HoraExtraDelegate hed = new HoraExtraDelegate();
			hed.registrarSalidaHE(pool_sp_g, codPersonal, salida
					.getFechaAutorizacion(), salida.getHoraIni(), horaSalida,
					Utiles.obtenerFechaActual());

			salida.setHoraSalida(horaSalida);

			session.removeAttribute("salidaHE");
			session.setAttribute("salidaHE", salida);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/registrarSalidaHE.jsp");
			dispatcher.forward(request, response);
			return;

		} catch (HoraExtraException e) {
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
	 * Mï¿½todo encargado de invocar a la pï¿½gina para el proceso de acumulaciï¿½n de
	 * horas extras
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarAcumulacionHE(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			session.setAttribute("fechaMinima",Utiles.obtenerFechaMinima(pool_sp_g));		//jquispecoi PAS20155E230000073

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/acumulacionHE.jsp");
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
	 * Mï¿½todo encargado de ejecutar el proceso de acumulaciï¿½n de horas extras
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void procesarAcumulacionHE(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			//String codigo = bUsuario.getNumreg();
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

			String criterio = request.getParameter("cmbCriterio");
			String valor = request.getParameter("txtValor") != null ? request
					.getParameter("txtValor") : "";

			//JRR - 31/03/2009
			String fecIni = request.getParameter("fechaIni");
			String fecFin = request.getParameter("fechaFin");
			
			Map mapa = new HashMap();
			mapa.put("dbpool", pool_sp_g);			
			mapa.put("criterio", criterio);
			mapa.put("valor", valor);
			mapa.put("fechaIni", fecIni);
			mapa.put("fechaFin", fecFin);
			mapa.put("codPers", codigo);
			mapa.put("usuario", usuario);
			mapa.put("seguridad", seguridad);
			//		

			HoraExtraDelegate hed = new HoraExtraDelegate();
			String res = hed.acumularHE(mapa);
/*			String res = hed.acumularHE(pool_sp_g, criterio, valor, codigo,
					usuario, seguridad); */

			if (res.equals(Constantes.OK)) {

				request.setAttribute("nomProceso",
						"ACUMULACI&Oacute;N DE HORAS DE LABOR EXCEPCIONAL");
				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/resultadoProceso.jsp");
				dispatcher.forward(request, response);
				return;
			} else {
				throw new Exception(res);
			}

		} catch (HoraExtraException e) {
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
	 * Mï¿½todo encargado de cargar los datos necesarios para la compensaciï¿½n de
	 * horas extras
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarCompensacionHE(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();//.getNumreg();
			//String codPers = bUsuario.getNumreg();
			String codUO = bUsuario.getCodUO();
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
			seguridad.put("codUO", codUO);
			seguridad.put("dbpool", pool_sp);
			seguridad.put("codOpcion", Constantes.DELEGA_LABOR_EXCEP);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("uoAO", bUsuario.getCodUO());//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			AsistenciaDelegate ad = new AsistenciaDelegate();
			if (ad.esJefeEncargadoDelegado(seguridad)) {			

				HoraExtraDelegate hed = new HoraExtraDelegate();
				ArrayList listaAcumulados = hed.buscarAcumulados(pool_sp, "", "",
						codUO, seguridad);
	
				session.removeAttribute("listaAcumulados");
				session.setAttribute("listaAcumulados", listaAcumulados);
	
				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/compensacionHE.jsp");
				dispatcher.forward(request, response);
				return;
			}
			else {
				throw new Exception(
					"Usted no se encuentra habilitado para ejecutar esta opciÃ³n.");
			}			

		} catch (HoraExtraException e) {
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
	 * Mï¿½todo encargado de cargar los datos de una autorizaciï¿½n de hora extra
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarDatosHoraExtra(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			ArrayList lista = (ArrayList) session
					.getAttribute("listaHorasExtras");
			String indice = request.getParameter("indice");
			BeanHoraExtra he = (BeanHoraExtra) lista.get(Integer
					.parseInt(indice));

			session.removeAttribute("he");
			session.setAttribute("he", he);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/autorizacionHorasExtras.jsp");
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
	 * Mï¿½todo encargado de modificar una autorizaciï¿½n de hora extra
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void modificarAutorizacionHE(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			String fechaAut = request.getParameter("fechaAut");
			String horaIni = request.getParameter("horaIni");
			String horaFin = request.getParameter("horaFin");
			String horaSalida = request.getParameter("horaSalida") != null ? request
					.getParameter("horaSalida")
					: "";
			String txtObs = request.getParameter("txtObs");
			UsuarioBean user = (UsuarioBean) session.getAttribute("usuarioBean");
			
			String usuario = user.getLogin();

			BeanHoraExtra he = (BeanHoraExtra) session.getAttribute("he");

			HoraExtraDelegate hed = new HoraExtraDelegate();
			String res = hed.modificarHoraExtra(he, fechaAut, horaIni, horaFin,
					horaSalida, txtObs, usuario);

			if (res.equals(Constantes.OK)) {
				cargarHorasExtras(request, response, session);
			} else {
				throw new Exception(res);
			}

		} catch (HoraExtraException e) {
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
	 * Mï¿½todo encargado de eliminar una autorizaciï¿½n de hora extra
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void eliminarHorasExtras(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			ArrayList lista = (ArrayList) session
					.getAttribute("listaHorasExtras");

			String[] params = request.getParameterValues("chk_opcion");

			HoraExtraDelegate hed = new HoraExtraDelegate();
			lista = hed.eliminarHorasExtras(params, lista);

			session.removeAttribute("listaHorasExtras");
			session.setAttribute("listaHorasExtras", lista);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/mantenimientoHorasExtras.jsp");
			dispatcher.forward(request, response);
			return;
		} catch (HoraExtraException e) {
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
	 * Metodo encargado de cargar las autorizaciones de horas extras
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarHorasExtras(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codJefe = bUsuario.getNroRegistro();//.getNumreg();
			//String codJefe = bUsuario.getNumreg();
			String codUO = bUsuario.getCodUO();
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
			seguridad.put("codPers", codJefe);
			seguridad.put("codUO", codUO);
			seguridad.put("dbpool", pool_sp);
			seguridad.put("codOpcion", Constantes.DELEGA_LABOR_EXCEP);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("uoAO", bUsuario.getCodUO());//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			AsistenciaDelegate ad = new AsistenciaDelegate();
			if (ad.esJefeEncargadoDelegado(seguridad)) {			
			
				HoraExtraDelegate hed = new HoraExtraDelegate();
				ArrayList horas = hed.buscarHorasExtras(pool_sp, "", "", codJefe,seguridad);
	
				session.removeAttribute("listaHorasExtras");
				session.setAttribute("listaHorasExtras", horas);
	
				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/mantenimientoHorasExtras.jsp");
				dispatcher.forward(request, response);
				return;
			}
			else {
				throw new Exception(
					"Usted no se encuentra habilitado para ejecutar esta opciÃ³n.");
			}						

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
	 * Metodo encargado de registrar una autorizacion de hora extra
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void registrarAutorizacionHE(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codJefe = bUsuario.getNroRegistro();//.getNumreg();
			//String codJefe = bUsuario.getNumreg();
			String codUO = bUsuario.getCodUO();
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
			seguridad.put("codPers", codJefe);
			seguridad.put("codUO", codUO);
			seguridad.put("dbpool", pool_sp);
			seguridad.put("codOpcion", Constantes.DELEGA_LABOR_EXCEP);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("uoAO", bUsuario.getCodUO());//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			ArrayList listaTrabajadores = (ArrayList) session
					.getAttribute("listaTrabajadores");

			String fechaAut = request.getParameter("fechaAut");
			String horaIni = request.getParameter("horaIni");
			String horaFin = request.getParameter("horaFin");
			String txtObs = request.getParameter("txtObs");

			HoraExtraDelegate hed = new HoraExtraDelegate();

			//registramos la autorizaciones
			hed.registrarAutorizacionHE(pool_sp_g, listaTrabajadores, codUO,
					fechaAut, horaIni, horaFin, txtObs, codJefe, usuario,
					seguridad);

			session.removeAttribute("listaHorasExtras");
			session.removeAttribute("listaTrabajadores");

			request.setAttribute("nomProceso",
					"AUTORIZACI&Oacute;N DE LABOR EXCEPCIONAL");
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/resultadoProceso.jsp");
			dispatcher.forward(request, response);


		} catch (HoraExtraException e) {
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
	 * Mï¿½todo encargado de listar las autorizaciones de un trabajador
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void verAutorizaciones(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		
		try {

			BeanHoraExtra trabHE = (BeanHoraExtra) session
					.getAttribute("trabHE");

			UtilesDelegate ud = new UtilesDelegate();
			HashMap trabComp = ud.buscarTrabajador(pool_sp,
					trabHE.getCodPers(), null);

			HoraExtraDelegate hed = new HoraExtraDelegate();
			ArrayList autorizaciones = hed.cargarAutorizaciones(pool_sp, trabHE
					.getCodPers());

			session.removeAttribute("trabComp");
			session.setAttribute("trabComp", trabComp);

			session.removeAttribute("listaAutorHE");
			session.setAttribute("listaAutorHE", autorizaciones);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/listaAutorizaciones.jsp");
			dispatcher.forward(request, response);
			return;

		} catch (HoraExtraException e) {
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
	 * Metodo encargado de eliminar los trabajadores de la lista de horas extras
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void eliminarTrabHE(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			ArrayList listaTrabajadores = (ArrayList) session
					.getAttribute("listaTrabajadores");
			String[] params = request.getParameterValues("chk_opcion");

			if (params != null) {
				for (int i = params.length - 1; i >= 0; i--) {
					String indice = params[i];
					HashMap t = (HashMap) listaTrabajadores.get(Integer
							.parseInt(indice));
					listaTrabajadores.remove(t);
				}
			}

			session.removeAttribute("listaTrabajadores");
			session.setAttribute("listaTrabajadores", listaTrabajadores);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher(
							"/autorizacionHorasExtras.jsp?tamanioPagina=5");
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
	
	//ICAPUNAY 09/04/2012 AOM 06A4T11 LABOR EXCEPCIONAL Y COMPENSACIONES-PAS20124E550000064
	/**
	 * Metodo encargado de cargar los objetos a memoria
	 * 
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarParametros() throws ServletException, IOException {
		try {
			
			ParamDAO paramDAO = new ParamDAO();
			dsRecauda = ServiceLocator.getInstance().getDataSource(pool_recauda);
			
			//Intendencias activas
			String query = "select t12cod_uorga, t12des_uorga from t12uorga where " +
			"t12ind_estad = '1' and ( " +		
			"(t12cod_uorga like '1%0000' and t12cod_uorga != '100000') or " + 
		    "(t12cod_uorga like '2%0000' and t12cod_uorga != '200000') or " + 		    
		    "(t12cod_uorga like '3%0000' and t12cod_uorga != '300000') or " +
		    "(t12cod_uorga like '4%0000' and t12cod_uorga != '400000') or " + 
		    //ICAPUNAY-PAS20144EB20000106-Cambio ROF 2014
		    "(t12cod_uorga like '5%0000' and t12cod_uorga != '500000') or " + 
		    "(t12cod_uorga like '6%0000' and t12cod_uorga != '600000') or " +	
		    "(t12cod_uorga like '7%0000' and t12cod_uorga != '700000') or " +	//ICAPUNAY - PAS20175E230300114 - Cambio Rof 2017
		    "(t12cod_uorga like '8%0000' and t12cod_uorga != '800000') )";
			//ICAPUNAY-PAS20144EB20000106-Cambio ROF 2014		
			
			paramDAO.cargar(query, pool_sp, "spprmInten");
			
			//cargamos los parametros necesarios para la validacion de feriados
		      paramDAO.cargar(new String[]{"000"}, dsRecauda, "recaudaprm000", ParamDAO.TIPO1);
		      paramDAO.cargar(new String[]{"400"}, dsRecauda, "recaudaprm400", ParamDAO.TIPO1);			
			
		} catch (Exception e) {
			log.error("error al cargar Cache:"+e);
		}
	}	
	
	/**
	 * Metodo encargado de invocar a la pagina para el nuevo proceso de acumulacion de horas extras	
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarProcesarLaborExcep(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			session.setAttribute("fechaMinima",Utiles.obtenerFechaMinima(pool_sp_g));		//jquispecoi PAS20155E230000073

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/procesarLaborExcep.jsp");
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
	 * Metodo encargado de procesar la acumulación de horas de labor excepcional de colaboradores por regimen/modalidad
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
	
	private void procesarLaborExcepcional(HttpServletRequest request,HttpServletResponse response, HttpSession session) throws ServletException, IOException {
	
		try {
			log.debug("Metodo procesarLaborExcepcional-ServletHoraExtra");			
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();
			String usuario = bUsuario.getLogin();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles");
			log.debug("roles1: "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			log.debug("roles: "+roles);
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codigo);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("uoAO", bUsuario.getCodUO());//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVOs
			
			String regimen = request.getParameter("cmbTipoDL");//0=planilla,1=cas,2=MF
			String criterio = request.getParameter("cmbCriterio");//0=registro,1=uuoo,4=intendencia,3=institucional
			String valor =  (request.getParameter("txtValor")!= null )?request.getParameter("txtValor"):"";
			String fechaIni = request.getParameter("fechaIni");
			String fechaFin = request.getParameter("fechaFin");			
			if(log.isDebugEnabled())log.debug("valor: " + valor);
			String codIntendencia = request.getParameter("cod_intendencia");
			if(log.isDebugEnabled())log.debug("codIntendencia: " + codIntendencia);
			
			if (criterio.equals("4")){//intendencia
				if (valor != null && valor.trim().equals("")) {
					valor = codIntendencia;
					if(log.isDebugEnabled())log.debug("valor2: " + valor);
				}
			}			
						
			HashMap mapa = new HashMap();
			mapa.put("dbpool", pool_sp_g);	
			mapa.put("regimen", regimen);
			mapa.put("criterio", criterio);
			mapa.put("valor", valor.trim().toUpperCase());
			mapa.put("fechaIni", fechaIni);
			mapa.put("fechaFin", fechaFin);
			mapa.put("codPers", codigo);
			mapa.put("usuario", usuario);
			mapa.put("seguridad", seguridad);

			if(log.isDebugEnabled()) log.debug("Llamando a procesarLaborExcepcional-HoraExtraDelegate: "+mapa);
			
			HoraExtraDelegate ad = new HoraExtraDelegate();
			boolean res = ad.procesarLaborExcepcional(mapa);
			
			if (res) {
				request.setAttribute("nomProceso","ACUMULACI&Oacute;N DE HORAS DE LABOR EXCEPCIONAL");
				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/resultadoProceso.jsp");
				dispatcher.forward(request, response);
				return;
			} else {
				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/procesarLaborExcep.jsp?res=" + res);
				dispatcher.forward(request, response);
				return;
			}
		} catch (HoraExtraException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/PagE.jsp");
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
	//FIN ICAPUNAY 09/04/2012 AOM 06A4T11 LABOR EXCEPCIONAL Y COMPENSACIONES-PAS20124E550000064
	
	//ICAPUNAY 06/05/2012 AOM 06A4T11 LABOR EXCEPCIONAL Y COMPENSACIONES-PAS20124E550000064
	/**
	 * Metodo encargado de invocar a la pagina para el nuevo registro temporal de compensaciones antiguas
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarRegistroTemporal(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			
			session.removeAttribute("habilitar");		
			session.setAttribute("habilitar","disabled='disabled'");

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/registroCompensacionesAntiguas.jsp");
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
	 * Metodo encargado de listar el total de horas autorizadas con saldo del colaborador
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarHorasAutorizadas(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			T1270DAO turnoDAO = new T1270DAO();
			T4819DAO t4819DAO = new T4819DAO(pool_sp);
			
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();
			HashMap roles = (HashMap) bUsuario.getMap().get("roles");
			log.debug("Roles1: "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			log.debug("Roles2: "+roles);
			
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codigo);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("uoAO", bUsuario.getCodUO());//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
			
			String trabajador = request.getParameter("txtResp")!=null ? request.getParameter("txtResp").trim().toUpperCase():"";			
			
			UtilesDelegate ud = new UtilesDelegate();
			int horasHabil = 0;
			int minutosHabil = 0;
			
			if(!trabajador.trim().equals("")){
				
				session.removeAttribute("solicitante");
				HashMap solicitante = ud.buscarTrabajadorJefe(pool_sp, trabajador, seguridad);				
				if (log.isDebugEnabled()) log.debug("solicitante: " + solicitante);				
				
				if (solicitante.get("t02cod_pers") == null || ((String) solicitante.get("t02cod_pers")).equals("")) {
					throw new Exception("El c&oacute;digo ingresado no es v&aacute;lido.");					
				}else{					
					//obteniendo el turno para la fecha actual de registro de la compensacion (validacion 3 botón registrar)
					BeanTurnoTrabajo turno = turnoDAO.joinWithT45ByCodFecha(pool_sp,trabajador,new FechaBean().getFormatDate("dd/MM/yyyy"));					
				    if (turno!=null){
				    	if (log.isDebugEnabled()) log.debug("turno: " + turno);				    	
				    	if((turno.isOperativo()) || (!turno.isControla())){//turno es operativo o turno es administrativo no es controlado
				    		throw new Exception("No puede registrar compensación porque tiene turno operativo o no controlado. Turno: "+turno.getDescTurno()+" ( "+turno.getHoraIni()+" - "+turno.getHoraIni());	
				    	}else{
				    		solicitante.put("NombreTurno", turno.getDescTurno());
					    	solicitante.put("InicioTurno", turno.getHoraIni());
					    	solicitante.put("FinTurno", turno.getHoraFin());
					    	solicitante.put("EsOperativo", String.valueOf(turno.isOperativo()));
					    	solicitante.put("EsControlado", String.valueOf(turno.isControla()));
					    	
					    	//add recien
					    	HashMap datos = new HashMap();
							datos.put("dbpool", pool_sp);
							datos.put("cod_pers",trabajador);
							
							SolicitudDelegate sd = new SolicitudDelegate();
							List lista = sd.findHorasLaborAutorizadasConSaldo(datos);//cod_pers
							if (log.isDebugEnabled()) log.debug("lista: " + lista);
							
							session.removeAttribute("listaAutorHE");
							session.setAttribute("listaAutorHE", lista);			
							
							Integer saldo = t4819DAO.findSaldoLaborAutorizadas(datos);//cod_pers							
							int saldoFinal = saldo.intValue();	
							if (log.isDebugEnabled()) log.debug("saldoFinal en minutos: " + saldoFinal);
							
							session.removeAttribute("saldoTotal");					
							session.setAttribute("saldoTotal", String.valueOf(saldoFinal));
							
							FechaBean fechaHabil = new FechaBean().calculaFecFinHabiles(new FechaBean(), 0);
							BeanTurnoTrabajo turnoHabil = turnoDAO.joinWithT45ByCodFecha(pool_sp,trabajador,fechaHabil.getFormatDate("dd/MM/yyyy"));
							
							if (turnoHabil != null) {
								horasHabil = Math.round(Utiles.obtenerHorasDifDia(turnoHabil.getHoraIni(),turnoHabil.getHoraFin()));//horas enteras del turno de la fecha habil
								minutosHabil = 60 * horasHabil;//minutos del turno de la fecha habil
								
								if (saldoFinal >= minutosHabil){//minimo para un dia para compensar se habilita registrar						
									session.removeAttribute("habilitar");		
									session.setAttribute("habilitar","");
								}	
							}
							//fin add recien					
				    	}				    	
				    }else{
				    	if (log.isDebugEnabled()) log.debug("sin turno: " + turno);
				    	solicitante.put("NombreTurno", "Sin turno");
				    }
				    session.setAttribute("solicitante", solicitante);
				    //fin obteniendo				   				
				}				
			}			
			RequestDispatcher dispatcher = getServletContext()
			.getRequestDispatcher("/registroCompensacionesAntiguas.jsp");
			dispatcher.forward(request, response);						
			return;
		} catch (HoraExtraException e) {
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
		 finally {
		    }
	}
	
	/**
	 * Metodo encargado de registrar temporalmente las compensaciones antiguas
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
	
	private void registrarCompensacionTemporal(HttpServletRequest request,HttpServletResponse response, HttpSession session) throws ServletException, IOException {
	
		BeanMensaje beanMsg = null;
		try {
			ArrayList res= new ArrayList();
			HashMap mapa = new HashMap();
			T4819DAO t4819DAO = new T4819DAO(pool_sp);
			log.debug("Metodo registrarCompensacionTemporal-ServletHoraExtra");			
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String usuario = bUsuario.getLogin();
			String codigo = bUsuario.getNroRegistro();
						
			HashMap roles = (HashMap) bUsuario.getMap().get("roles");
			log.debug("roles1: "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			log.debug("roles2: "+roles);
			String uoSeg = bUsuario.getVisibilidad();
			
			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codigo);
			seguridad.put("uoSeg", uoSeg);
			
			HashMap solicitante = (HashMap) session.getAttribute("solicitante");
			//ArrayList listaAutorHE = (ArrayList) session.getAttribute("listaAutorHE");
			//String saldoTotal = (String) session.getAttribute("saldoTotal");
			
			if(solicitante!=null && !solicitante.isEmpty()){
				String cod_pers = (String)solicitante.get("t02cod_pers");
				String uuoo_pers = (String)solicitante.get("t02cod_uorg");
				String fechaIniComp = request.getParameter("fechaIniComp");
				String fechaFinComp = request.getParameter("fechaFinComp");
				String observacion = request.getParameter("txtObs")!=null?(String)request.getParameter("txtObs"):"";		
				
				mapa.put("dbpool", pool_sp_g);	
				mapa.put("cod_pers", cod_pers.trim().toUpperCase());
				mapa.put("uuoo_pers", uuoo_pers);
				mapa.put("fechaIniComp", fechaIniComp.trim());
				mapa.put("fechaFinComp", fechaFinComp.trim());
				mapa.put("observacion", observacion);			
				mapa.put("usuario", usuario);
				mapa.put("codPers", codigo);			
				mapa.put("seguridad", seguridad);

				HoraExtraDelegate hed = new HoraExtraDelegate();
				res = hed.procesarCompensacionTemporal(mapa);
			}			
			session.removeAttribute("habilitar");		
			session.setAttribute("habilitar","disabled='disabled'");
			if (res != null && !res.isEmpty()) {//existen errores

				beanMsg = new BeanMensaje();
				beanMsg.setListaobjetos(res.toArray());
				beanMsg.setMensajesol("Por favor intente nuevamente.");
				session.setAttribute("beanErr", beanMsg);
				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/PagM.jsp");
				dispatcher.forward(request, response);
				return;

			} else {
				
				SolicitudDelegate sd = new SolicitudDelegate();
				List nueva_lista = sd.findHorasLaborAutorizadasConSaldo(mapa);//cod_pers
				if (log.isDebugEnabled()) log.debug("nueva_lista: " + nueva_lista);
				
				session.removeAttribute("listaAutorHE");
				session.setAttribute("listaAutorHE", nueva_lista);			
				
				Integer saldo = t4819DAO.findSaldoLaborAutorizadas(mapa);//cod_pers
				if (log.isDebugEnabled()) log.debug("saldo en minutos: " + saldo);
				int saldoFinal = saldo.intValue();				
				session.removeAttribute("saldoTotal");	
				if (log.isDebugEnabled()) log.debug("saldoFinal: "+saldoFinal);
				session.setAttribute("saldoTotal", String.valueOf(saldoFinal));

				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/registroCompensacionesAntiguas.jsp");
				dispatcher.forward(request, response);
				return;
			}
		} catch (HoraExtraException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/PagE.jsp");
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
	//FIN ICAPUNAY 06/05/2012 AOM 06A4T11 LABOR EXCEPCIONAL Y COMPENSACIONES-PAS20124E550000064
}