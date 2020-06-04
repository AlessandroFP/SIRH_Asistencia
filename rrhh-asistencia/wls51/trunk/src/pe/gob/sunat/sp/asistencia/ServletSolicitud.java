package pe.gob.sunat.sp.asistencia;

import java.io.IOException;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

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
import pe.gob.sunat.framework.core.bean.ParamBean;
import pe.gob.sunat.framework.core.ejb.DelegateException;
import pe.gob.sunat.framework.core.pattern.DynaBean;
import pe.gob.sunat.framework.core.pattern.ServiceLocator;
import pe.gob.sunat.framework.core.servlet.ServletAbstract;
import pe.gob.sunat.framework.util.Propiedades;
import pe.gob.sunat.framework.util.cache.dao.ParamDAO;
import pe.gob.sunat.framework.util.lang.Ordenamiento;
import pe.gob.sunat.rrhh.asistencia.dao.T1273DAO;
import pe.gob.sunat.sol.BeanMensaje;
import pe.gob.sunat.sol.IncompleteConversationalState;
import pe.gob.sunat.sp.asistencia.bean.BeanTurnoTrabajo;
import pe.gob.sunat.sp.asistencia.bean.BeanSolReinConcepto;
import pe.gob.sunat.sp.asistencia.bean.BeanSolReinDetalle;
import pe.gob.sunat.sp.asistencia.bean.BeanSolReintegro;
import pe.gob.sunat.sp.asistencia.ejb.delegate.AsistenciaDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.AsistenciaException;
import pe.gob.sunat.sp.asistencia.ejb.delegate.MantenimientoDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.MantenimientoException;
import pe.gob.sunat.sp.asistencia.ejb.delegate.SolicitudDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.SolicitudException;
import pe.gob.sunat.sp.asistencia.ejb.delegate.LicenciaDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.LicenciaException;
import pe.gob.sunat.sp.asistencia.ejb.delegate.TurnoTrabajoDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.UtilesDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.UtilesException;
import pe.gob.sunat.sp.asistencia.ejb.delegate.VacacionDelegate;
import pe.gob.sunat.sp.dao.T01DAO;
import pe.gob.sunat.tecnologia.menu.bean.MenuCliente;
import pe.gob.sunat.tecnologia.menu.bean.UsuarioBean;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;

//ICAPUNAY - 10-03-2011 CAMBIO PARA QUE TOME COMO periodoInicial EL AÑO ACTUAL Y NO LEA DE constantes.properties
import pe.gob.sunat.framework.util.date.FechaBean;
//
import weblogic.utils.StringUtils;

/**
 * @web.servlet name="ServletSolicitud"
 * @web.servlet-mapping url-pattern = "/asisS11Alias"
 * @web.servlet-init-param name = "pool_sp" value = "jdbc/dcsp"
 * @web.servlet-init-param name = "pool_recauda" value = "jdbc/dcrecauda"
 * @web.servlet-init-param name = "pool_sp_g" value = "jdbc/dgsp"
 * @web.servlet-init-param name = "pool_sig" value = "jdbc/dcsig"
 * @web.servlet-init-param name = "pool_sig_g" value = "jdbc/dgsig"
 * 
 *                         Title: Control de Asistencia Description: Copyright:
 *                         Copyright (c) 2004 Company: Sunat
 * @author cgarratt
 * @version 1.0
 */
public class ServletSolicitud extends ServletAbstract {

	private static final Logger log = Logger.getLogger(ServletSolicitud.class);
	private static String pool_sp;
	private static String pool_recauda;
	private static String pool_sp_g;
	private static String pool_sig;
	private static String pool_sig_g;
	private static DataSource dsSP;
	private static DataSource dsRecauda;
	private static DataSource dsSig;
	MensajeBean bean = null;

	List listaFechaDS = null;// JVV-02/03/2012-DECRETO SUPREMO 004-2006

	Propiedades constantes = new Propiedades(getClass(),
			"/constantes.properties");

	// ServiceLocator sl = ServiceLocator.getInstance();//JVV-02/03/2012-DECRETO
	// SUPREMO 004-2006

	public ServletSolicitud() {
	}

	public void destroy() {
		super.destroy();
	}

	public void init(ServletConfig config) throws ServletException {
		try {
			super.init(config);
			pool_sp = config.getInitParameter("pool_sp");
			pool_sp_g = config.getInitParameter("pool_sp_g");
			pool_recauda = config.getInitParameter("pool_recauda");
			pool_sig = config.getInitParameter("pool_sig");
			pool_sig_g = config.getInitParameter("pool_sig_g");

			cargaParametros();
		} catch (Exception e) {
			throw new ServletException(e.getMessage());
		} finally {

		}
	}

	public void procesa(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		try {
			DynaBean dBean = new DynaBean(request);
			String accion = request.getParameter("accion");

			if (session == null) {
				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/PagSession.jsp");
				dispatcher.forward(request, response);
				return;
			}

			// BeanUsuario bUsuario = (BeanUsuario)
			// session.getAttribute("beanusuario");

			UsuarioBean bUsuario = (UsuarioBean) session
					.getAttribute("usuarioBean");
			if (bUsuario != null)
				NDC.push(bUsuario.getLogin().concat("-")
						.concat(bUsuario.getTicket()));

			log.info("Accion : " + accion);

			if (accion.equals("cargarRegistroSolicitud")) {
				cargarRegistroSolicitud(request, response, session);
			}

			if (accion.equals("cargarSolicitante")) {
				cargarSolicitante(request, response, session);
			}

			if (accion.equals("registrarSolicitud")) {
				registrarSolicitud(request, response, session);
			}

			if (accion.equals("cargarSolicitudesIniciadas")) {
				cargarSolicitudesIniciadas(request, response, session);
			}

			if (accion.equals("cargarConsultaGeneral")) {
				cargarConsultaGeneral(request, response, session);
			}

			if (accion.equals("cargarConsultaAdministracion")) { // jquispecoi
																	// 03/2014
				cargarConsultaAdministracion(request, response, session);
			}

			if (accion.equals("cargarSolicitudesConcluidas")) {
				cargarSolicitudesConcluidas(request, response, session);
			}

			if (accion.equals("cargarBandejaEntrada")) {
				cargarBandejaEntrada(request, response, session);
			}

			if (accion.equals("visualizarSolicitud")) {
				visualizarSolicitud(request, response, session);
			}

			if (accion.equals("visualizarSolicitudAdministracion")) {
				visualizarSolicitudAdministracion(request, response, session);
			}

			if (accion.equals("cargarDerivarSolicitud")) {
				cargarDerivarSolicitud(request, response, session);
			}

			if (accion.equals("procesarSolicitud")) {
				procesarSolicitud(request, response, session);
			}

			if (accion.equals("procesarSolicitudAdministracion")) {
				procesarSolicitudAdministracion(request, response, session);
			}

			if (accion.equals("cargarSeguimiento")) {
				cargarSeguimiento(request, response, session);
			}

			if (accion.equals("buscarSolicitudesPendientes")) {
				buscarSolicitudesPendientes(request, response, session);
			}

			if (accion.equals("buscarConsultaGeneral")) {
				buscarConsultaGeneral(request, response, session);
			}

			if (accion.equals("buscarConsultaAdministracion")) {
				buscarConsultaAdministracion(request, response, session);
			}

			if (accion.equals("buscarSolicitudesConcluidas")) {
				buscarSolicitudesConcluidas(request, response, session);
			}

			if (accion.equals("buscarSolicitudesRecibidas")) {
				buscarSolicitudesRecibidas(request, response, session);
			}

			if (accion.equals("registrarSupervisor")) {
				registrarSupervisor(request, response, session);
			}

			if (accion.equals("listarMarcas")) {
				listarMarcas(request, response, session);
			}
			/* JRR - FUSION PROGRAMACION */

			else if (accion.equals("cargaAnulacion")) {
				cargaAnulacion(request, response, session, bUsuario);
			} else if (accion.equals("traerVacFisyProg")) {
				traerVacFisyProg(request, response, session, dBean);
			} else if (accion.equals("agregarReProgramacion")) {
				agregarReProgramacion(request, response, session, dBean);
			} else if (accion.equals("grabarAnulVacFis")) {
				grabarAnulVacFis(request, response, session, bUsuario, dBean);
			} else if (accion.equals("ordenar")) {
				ordenarSoliPendientes(request, response, session, dBean);
			} else if (accion.equals("procesarSolicitudes")) {
				procesarSolicitudes(request, response, session, dBean, bUsuario);
			}

			/*         */
			/* MTM */
			if (accion.equals("registrarAprobador")) {
				registrarAprobador(request, response, session);
			}
			if (accion.equals("eliminarAprobadores")) {
				eliminarAprobadores(request, response, session);
			}
			/*         */
			/*         */
			// EBV 03/04/2012
			if (accion.equals("listarLaborAuto")) {
				listarLaborAuto(request, response, session);
			}

			// PAS20171U230200001 - solicitud de reintegro
			if (accion.equals("listarConceptoReintegro")) {
				listarConceptoReintegro(request, response, session);
			}
			// PAS20171U230200001 - solicitud de reintegro
			if (accion.equals("listarDetConcReintegro")) {
				listarDetalleConceptoReintegro(request, response, session);
			}
			// PAS20171U230200001 - solicitud de reintegro
			if (accion.equals("cargarExcluirConcepto")) {
				cargarExcluirConcepto(request, response, session);
			}
			// PAS20171U230200001 - solicitud de reintegro
			if (accion.equals("excluirConcepto")) {
				excluirConcepto(request, response, session);
			}

			// PAS20171U230200001 - solicitud de reintegro
			if (accion.equals("verDocumentosAprobados")) {
				verDocumentosAprobados(request, response, session);
			}
			// PAS20171U230200001 - solicitud de reintegro
			if (accion.equals("notificarSolReintegro")) {
				notificarSolReintegro(request, response, session);
			}
			// PAS20171U230200001 - solicitud de reintegro
			if (accion.equals("cargarDetSolReintegro")) {
				cargarDetSolReintegro(request, response, session, dBean);
			}
			// PAS20171U230200001 - solicitud de reintegro
			if (accion.equals("actualizarDetSolReintegro")) {
				actualizarDetSolReintegro(request, response, session);
			}

		} catch (Exception e) {
			log.error("*** Error ***", e);

		} finally {
			NDC.pop();
			NDC.remove();
		}
	}

	/**
	 * Metodo encargado de cargar el registro de solicitudes
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarRegistroSolicitud(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			// BeanUsuario bUsuario = (BeanUsuario)
			// session.getAttribute("beanusuario");
			UsuarioBean bUsuario = (UsuarioBean) session
					.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();// .getNumreg();
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); // super.getRoles(session);
			log.debug("Roles " + roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles(usrRoles);
			log.debug("Roles " + roles);
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null)
				uoSeg = uoSeg.substring(0, 2).concat("%");

			MantenimientoDelegate md = new MantenimientoDelegate();
			ArrayList tipos = md.buscarMovimientosSolicitud(pool_sp,
					Constantes.ACTIVO);

			session.removeAttribute("tiposSolicitud");
			session.setAttribute("tiposSolicitud", tipos);

			HashMap solicitante = new HashMap();

			if (roles.get(Constantes.ROL_JEFE) != null
					|| roles.get(Constantes.ROL_SECRETARIA) != null) {
				request.setAttribute("rolCreador", "1");
			} else {
				UtilesDelegate ud = new UtilesDelegate();
				solicitante = ud.buscarTrabajadorJefe(pool_sp, codigo, null);
				solicitante.put("codCreador", codigo);
				request.setAttribute("rolCreador", "0");

				if (solicitante.get("t12cod_jefe_desc") == null
						|| ("").equals(solicitante.get("t12cod_jefe_desc")
								.toString())) {
					throw new Exception(
							"Error en la configuraci&oacute;n. Comun&iacute;quese con su Analista.");
				}
			}
			// JRR - Fecha de reingreso 25 Abril 2008
			VacacionDelegate vd = new VacacionDelegate();
			String fecha_ingreso = "";
			HashMap hmVacGen = vd.buscarVacacionesGen(pool_sp, codigo);
			if (hmVacGen != null && hmVacGen.get("fecha") != null
					&& !hmVacGen.get("fecha").toString().equals("")) {
				fecha_ingreso = hmVacGen.get("fecha").toString();
			} else {
				fecha_ingreso = solicitante.get("t02f_ingsun").toString();
			}
			solicitante.put("fecha_ingreso", fecha_ingreso);
			//

			// EBV - Turno 18/05/2012
			TurnoTrabajoDelegate ttd = new TurnoTrabajoDelegate();
			BeanTurnoTrabajo buscarTrabTurno = ttd.buscarTrabTurno(pool_sp,
					(String) solicitante.get("t02cod_pers"),
					new FechaBean().getFormatDate("dd/MM/yyyy"));
			if (buscarTrabTurno == null) {
				solicitante.put("turno", "SIN TURNO");
				solicitante.put("des_turno", "SIN TURNO");
			} else {
				solicitante.put("turno", buscarTrabTurno.getTurno());
				solicitante.put("des_turno", buscarTrabTurno.getDescTurno());
			}
			session.removeAttribute("solicitante");
			session.setAttribute("solicitante", solicitante);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/registrarSolicitud.jsp");
			dispatcher.forward(request, response);
			return;

		} catch (SolicitudException e) {
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
			log.error("Impresion del error" +e);
			e.printStackTrace();
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion Albert. "
					+ e.getMessage() + "error"+ e); /**/
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}
	}

	/**
	 * Mï¿½todo encargado de cargar el registro de solicitudes
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarSolicitante(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean) session
					.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();// .getNumreg();
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); // super.getRoles(session);
			log.debug("Roles " + roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles(usrRoles);
			log.debug("Roles " + roles);
			// String codigo = bUsuario.getNumreg();
			// HashMap roles = super.getRoles(session);
			String uoSeg = bUsuario.getVisibilidad();
			log.debug("bUsuario.getVisibilidad() inicial: "
					+ bUsuario.getVisibilidad());// ICR 28/12/2012 NUEVAS UUOOS
													// ANALISTA OPERATIVO
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null)
				uoSeg = uoSeg.substring(0, 2).concat("%");
			log.debug("uoSeg recortado: " + uoSeg);// ICR 28/12/2012 NUEVAS
													// UUOOS ANALISTA OPERATIVO

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codigo);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("uoAO", bUsuario.getCodUO());// ICR 28/12/2012 NUEVAS
														// UUOOS ANALISTA
														// OPERATIVO
			log.debug("bUsuario.getCodUO(): " + bUsuario.getCodUO());// ICR
																		// 28/12/2012
																		// NUEVAS
																		// UUOOS
																		// ANALISTA
																		// OPERATIVO

			String usuario = request.getParameter("txtValor") != null ? request
					.getParameter("txtValor").trim().toUpperCase() : "";

			UtilesDelegate ud = new UtilesDelegate();
			HashMap solicitante = ud.buscarTrabajadorJefe(pool_sp, usuario,
					seguridad);

			if (solicitante.get("t02cod_pers") == null
					|| ((String) solicitante.get("t02cod_pers")).equals("")) {
				throw new Exception(
						"El c&oacute;digo ingresado no es v&aacute;lido.");
			}

			solicitante
					.put("userOrig", (String) solicitante.get("t02cod_pers"));
			solicitante.put("codCreador", codigo);

			// JRR - Fecha de reingreso 25 Abril 2008
			VacacionDelegate vd = new VacacionDelegate();
			String fecha_ingreso = "";
			HashMap hmVacGen = vd.buscarVacacionesGen(pool_sp, usuario);
			if (hmVacGen != null && hmVacGen.get("fecha") != null
					&& !hmVacGen.get("fecha").toString().equals("")) {
				fecha_ingreso = hmVacGen.get("fecha").toString();
			} else {
				fecha_ingreso = solicitante.get("t02f_ingsun").toString();
			}
			solicitante.put("fecha_ingreso", fecha_ingreso);
			//

			if (solicitante.get("t12cod_jefe_desc") == null
					|| ("").equals(solicitante.get("t12cod_jefe_desc")
							.toString())) {
				throw new Exception(
						"Error en la configuraci&oacute;n. Comun&iacute;quese con su Analista.");
			}

			// EBV - Turno 18/05/2012
			TurnoTrabajoDelegate ttd = new TurnoTrabajoDelegate();
			BeanTurnoTrabajo buscarTrabTurno = ttd.buscarTrabTurno(pool_sp,
					(String) solicitante.get("t02cod_pers"),
					new FechaBean().getFormatDate("dd/MM/yyyy"));
			if (buscarTrabTurno == null) {
				solicitante.put("turno", "SIN TURNO");
				solicitante.put("des_turno", "SIN TURNO");
			} else {
				solicitante.put("turno", buscarTrabTurno.getTurno());
				solicitante.put("des_turno", buscarTrabTurno.getDescTurno());
			}
			session.removeAttribute("solicitante");
			session.setAttribute("solicitante", solicitante);

			// PAS20171U230200001 - solicitud de reintegro
			log.debug("Reintegro por descuento (Asistencia/Licencia) y  Reintegro por descuento (subsidio) y  solo podrán ser registradas por el propio trabajador");
			log.debug("codigo" + codigo);// .getNumreg();
			log.debug("usuario" + usuario);
			if (!codigo.equals(usuario)) {
				List listSolicitudes = new ArrayList();
				List listSolicitudesActual = (ArrayList) session
						.getAttribute("tiposSolicitud");
				for (int i = 0; i < listSolicitudesActual.size(); i++) {
					HashMap tipoSol = (HashMap) listSolicitudesActual.get(i);
					String mov = tipoSol.get("mov").toString();
					if (!(mov
							.equals(Constantes.MOV_REINTEGRO_POR_DESCUENTO_ASISTENCIA) || mov
							.equals(Constantes.MOV_REINTEGRO_POR_DESCUENTO_SUBSIDIO))) {
						listSolicitudes.add(tipoSol);
					}
				}
				session.removeAttribute("tiposSolicitud");
				session.setAttribute("tiposSolicitud", listSolicitudes);
			}
			// FIN-PAS20171U230200001 - solicitud de reintegro

			// ICAPUNAY - FORMATIVAS 10/06/2011
			if (solicitante.get("t02cod_rel").equals(
					Constantes.CODREL_FORMATIVA)) { // FORMATIVA (10)
				ArrayList tiposSolicitud = (ArrayList) session
						.getAttribute("tiposSolicitud");
				log.debug("tiposSolicitud: " + tiposSolicitud); // ICR 09092015
																// -
																// PAS20155E230300109
																// - Jefes
																// puedan
																// registrar la
																// sol. Permiso
																// especial 111
																// para colab.
																// Modalidad
																// formativa
				ArrayList tiposSolicitudFormativas = new ArrayList();
				if (tiposSolicitud.size() > 0) {
					HashMap tipo = null;
					ParamDAO paramDAO = new ParamDAO();
					for (int i = 0; i < tiposSolicitud.size(); i++) {
						tipo = (HashMap) tiposSolicitud.get(i);
						List lstTiposSolicitFormativa = (List) paramDAO
								.cargarNoCache(
										"select t99codigo, t99descrip from t99codigos where t99cod_tab='521' and t99estado='1' and t99tip_desc='D'",
										dsSP, paramDAO.LIST);
						for (int j = 0; j < lstTiposSolicitFormativa.size(); j++) {
							ParamBean paramBean = (ParamBean) lstTiposSolicitFormativa
									.get(j);
							if (((String) tipo.get("mov")).trim().equals(
									paramBean.getCodigo().trim())) {
								tiposSolicitudFormativas.add(tipo);
								j = lstTiposSolicitFormativa.size();
							}
						}
					}
				}
				if (tiposSolicitudFormativas != null
						&& tiposSolicitudFormativas.size() > 0) {
					session.removeAttribute("tiposSolicitud");
					session.setAttribute("tiposSolicitud",
							tiposSolicitudFormativas);
				}
			}

			// FIN ICAPUNAY - FORMATIVAS

			request.setAttribute("rolCreador", "0");
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/registrarSolicitud.jsp");
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
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}
	}

	/**
	 * Mï¿½todo encargado de cargar las solicitudes concluidas
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarSolicitudesConcluidas(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean) session
					.getAttribute("usuarioBean");

			String codPers = bUsuario.getNroRegistro();
			String usuario = bUsuario.getLogin();

			MantenimientoDelegate md = new MantenimientoDelegate();

			ArrayList tipos = md.buscarMovimientosSolicitud(pool_sp,
					Constantes.ACTIVO);
			session.removeAttribute("tiposSolicitud");
			session.setAttribute("tiposSolicitud", tipos);

			SolicitudDelegate sd = new SolicitudDelegate();
			ArrayList listaSolicitudes = sd.cargarSolicitudesConcluidas(
					pool_sp, codPers, "-1", "", "", usuario);

			session.removeAttribute("listaSolicitudes");
			session.setAttribute("listaSolicitudes", listaSolicitudes);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/solicitudesConcluidas.jsp");
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
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * Metodo encargado de buscar las solicitudes pendientes
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarSolicitudesPendientes(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean) session
					.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();
			String usuario = bUsuario.getLogin();

			String criterio = request.getParameter("cmbCriterio");
			String valor = request.getParameter("txtValor");

			String tipo = request.getParameter("cmbTipo");

			SolicitudDelegate sd = new SolicitudDelegate();
			ArrayList listaSolicitudes = sd.cargarSolicitudesIniciadas(pool_sp,
					codPers, tipo, criterio, valor, usuario);

			session.removeAttribute("listaSolicitudes");
			session.setAttribute("listaSolicitudes", listaSolicitudes);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/solicitudesIniciadas.jsp");
			dispatcher.forward(request, response);
			return;

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
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}
	}

	/**
	 * Metodo encargado de buscar las solicitudes concluidas
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarSolicitudesConcluidas(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean) session
					.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();
			String usuario = bUsuario.getLogin();

			String criterio = request.getParameter("cmbCriterio");
			String valor = request.getParameter("txtValor");

			String tipo = request.getParameter("cmbTipo");

			SolicitudDelegate sd = new SolicitudDelegate();
			ArrayList listaSolicitudes = sd.cargarSolicitudesConcluidas(
					pool_sp, codPers, tipo, criterio, valor, usuario);

			session.removeAttribute("listaSolicitudes");
			session.setAttribute("listaSolicitudes", listaSolicitudes);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/solicitudesConcluidas.jsp");
			dispatcher.forward(request, response);
			return;

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
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}
	}

	/**
	 * Metodo encargado de buscar las solicitudes recibidas
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarSolicitudesRecibidas(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean) session
					.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();

			String criterio = request.getParameter("cmbCriterio");
			String valor = request.getParameter("txtValor");

			String tipo = request.getParameter("cmbTipo");

			SolicitudDelegate sd = new SolicitudDelegate();
			ArrayList listaRecibidas = sd.cargarSolicitudesRecibidas(pool_sp,
					codPers, tipo, criterio, valor);
			
			session.removeAttribute("listaRecibidas");
			session.setAttribute("listaRecibidas", listaRecibidas);	
			
			setAttribute(request, "tipoSolFiltro", tipo);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/solicitudesRecibidas.jsp");
			dispatcher.forward(request, response);
			return;

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
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * Metodo encargado de cargar las solicitudes pendientes
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarSolicitudesIniciadas(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean) session
					.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();
			String usuario = bUsuario.getLogin();

			MantenimientoDelegate md = new MantenimientoDelegate();
			ArrayList tipos = md.buscarMovimientosSolicitud(pool_sp,
					Constantes.ACTIVO);

			session.removeAttribute("tiposSolicitud");
			session.setAttribute("tiposSolicitud", tipos);

			SolicitudDelegate sd = new SolicitudDelegate();
			ArrayList listaSolicitudes = sd.cargarSolicitudesIniciadas(pool_sp,
					codPers, "-1", "", "", usuario);

			session.removeAttribute("listaSolicitudes");
			session.setAttribute("listaSolicitudes", listaSolicitudes);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/solicitudesIniciadas.jsp");
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
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}
	}

	/**
	 * Metodo encargado de cargar la bandeja de entrada de solicitudes recibidas
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarBandejaEntrada(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean) session
					.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();
			String codUO = bUsuario.getCodUO();
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); // super.getRoles(session);
			// log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles(usrRoles);
			// log.debug("Roles "+roles);
			// HashMap roles = super.getRoles(session);
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null)
				uoSeg = uoSeg.substring(0, 2).concat("%");

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codPers);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("codUO", codUO);
			seguridad.put("codOpcion", Constantes.DELEGA_SOLICITUDES);
			seguridad.put("dbpool", pool_sp);

			AsistenciaDelegate ad = new AsistenciaDelegate();

			// if ( (ad.esJefeEncargadoDelegado(seguridad)) ||
			// (ad.esSupervisor(pool_sp,codPers,"07", codUO)) ||
			// (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) ||
			// (roles.get(Constantes.ROL_ANALISTA_CENTRAL)!=null) ) { ICAPUNAY -
			// PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
			if ((ad.esJefeEncargadoDelegadoSolicitudes(seguridad))
					|| (ad.esSupervisor(pool_sp, codPers, "07", codUO))
					|| (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null)
					|| (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null)) { // ICAPUNAY
																				// -
																				// PAS20165E230300005
																				// -
																				// delegacion
																				// sol./Reg
																				// Aut/Reg
																				// Comp

				MantenimientoDelegate md = new MantenimientoDelegate();
				ArrayList tipos = md.buscarMovimientosSolicitud(pool_sp,
						Constantes.ACTIVO);

				session.removeAttribute("tiposSolicitud");
				session.setAttribute("tiposSolicitud", tipos);

				SolicitudDelegate sd = new SolicitudDelegate();
				ArrayList listaRecibidas = sd.cargarSolicitudesRecibidas(
						pool_sp, codPers, "-1", "", "");

				session.removeAttribute("listaRecibidas");
				session.setAttribute("listaRecibidas", listaRecibidas);

				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/solicitudesRecibidas.jsp");
				dispatcher.forward(request, response);
				return;
			} else {
				throw new Exception(
						"Usted no se encuentra habilitado para ejecutar esta opcion.");
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

	// WERR-PAS20155E230300132
	public int verificarDiaNoHabil(String fechaAlta, String tipo,
			String fechaNac, HttpSession session) throws RemoteException {
		int esInicioHabil = 0;// se controla las otras solicitudes
		SolicitudDelegate sd1 = new SolicitudDelegate();
		if (sd1.inicioLicenciaHabil(pool_sp_g, fechaAlta, fechaNac)) {
			esInicioHabil = 2;// pintar el mensaje
		} else {
			esInicioHabil = 1; // se le permite registrar
		}
		log.debug("esInicioHabil-->" + esInicioHabil);
		return esInicioHabil;
	}

	// END WERR-PAS20155E230300132

	/**
	 * Metodo encargado de registrar la solicitud
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void registrarSolicitud(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		BeanMensaje beanMsg = null;
		if (log.isDebugEnabled())
			log.debug("ingreso a registrarSolicitud-por toda solicitud se derive a supervisor primero");// ICR
																										// -
																										// 30/11/2012
																										// PAS20124E550000064
																										// labor
																										// excepcional
		try {
			UsuarioBean bUsuario = (UsuarioBean) session
					.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();
			String usuario = bUsuario.getLogin();

			HashMap solicitante = (HashMap) session.getAttribute("solicitante");

			String tipo = request.getParameter("cmbTipoSolicitud") != null ? request
					.getParameter("cmbTipoSolicitud") : "";
			String asunto = request.getParameter("asunto");
			String fechaIni = request.getParameter("fechaIni");
			String nvaFechaIni = request.getParameter("fechaIni");
			String fechaFin = request.getParameter("fechaFin");
			String fechaNac = request.getParameter("fechaNac"); // es la fecha
																// de Inicio
			String fechaMarca = request.getParameter("fechaMarca");
			String txtHora = request.getParameter("txtHora");
			String txtObs = request.getParameter("txtObs");
			String annoVac = request.getParameter("annoVenta");
			String diasVac = request.getParameter("diasVenta");
			String numDias = request.getParameter("txtDias");
			
			String permitirAdelanto=request.getParameter("permitirAdelanto");
			
			String txtObsSeg = txtObs;
			String numero = "";	
			// dtarazona 3er entregable
			String programdasSel = "";
			String reprogramdas = "";
			// fin dtarazona 3er entregable

			// JRR - 10/11/2009
			String fNacimiento = request.getParameter("fNacimiento");
			String fAlta = request.getParameter("fAlta");
			//
			// MTM
			ArrayList lista = new ArrayList();
			// PRAC-ASANCHEZ 24/06/2009
			// String indPreguntaSiNo =
			// request.getParameter("cmbPreguntaSiNo");//1: dia habil siguiente,
			// 0:dia de Nac
			//

			int indVacacion = Integer.parseInt(request
					.getParameter("indVacacion").trim().equals("") ? "0"
					: request.getParameter("indVacacion"));
			ArrayList listaVacaciones = (ArrayList) session
					.getAttribute("listaVacaciones");

			if (tipo.trim().equals(Constantes.VACACION_SUSPENDIDA)
					|| tipo.trim().equals(Constantes.VACACION_PROGRAMADA)
					|| tipo.trim().equals(Constantes.VACACION_POSTERGADA)
					|| tipo.trim().equals(Constantes.REPROGRAMACION_VACACION)) {

				programdasSel = request.getParameter("dataProgramada");
				if (log.isDebugEnabled())
					log.debug("TxtProgramadas" + programdasSel);
				reprogramdas = request.getParameter("dataReprogramada");
				if (log.isDebugEnabled())
					log.debug("TxtReProgramadas" + reprogramdas);
				
				if(log.isDebugEnabled()) log.debug("fechaInicioPer:"+request.getParameter("fechaInicioPer"));
				if(log.isDebugEnabled()) log.debug("fechaInicioSigPer:"+request.getParameter("fechaInicioSigPer"));

				if (listaVacaciones != null && listaVacaciones.size() > 0) {
					// cambiar esta parte del codigo ya que lista vacaciones es
					// lista de mapas ya no de beanvacacion
					HashMap bVac = (HashMap) listaVacaciones.get(indVacacion);

					// fechaIni =
					// Utiles.timeToFecha((Timestamp)bVac.get("ffinicio"));
					// fechaFin =
					// Utiles.timeToFecha((Timestamp)bVac.get("ffin"));
					if (!tipo.trim().equals(Constantes.VACACION_POSTERGADA)) {
						numDias = "" + bVac.get("dias");
					}
					annoVac = bVac.get("anno_vac").toString();
					if (log.isDebugEnabled())
						log.debug("P1" + annoVac);
					txtObs = bVac.get("periodo").toString().trim().equals("") ? " "
							: bVac.get("periodo").toString().trim();
					txtObs += "&";
					if (log.isDebugEnabled())
						log.debug("P1" + txtObs);
					if (tipo.trim().equals(Constantes.REPROGRAMACION_VACACION)
							|| tipo.trim().equals(
									Constantes.VACACION_POSTERGADA)) {
						txtObs += fechaIni;
						txtObs += "&";
						txtObs += fechaFin;
						if (log.isDebugEnabled())
							log.debug("P1" + txtObs);
					}

				}
			}

			if (tipo.trim().equals(Constantes.MOV_OMISION_MARCA)
					|| tipo.trim().equals(Constantes.MOV_ANULACION_MARCA)) {
				txtObs += "&hora=" + txtHora;
			}

			if (tipo.trim().equals(Constantes.VACACION_VENTA)) {
				txtObs = txtObs.trim() + "&diasVenta=" + diasVac.trim();
				numDias = diasVac.trim();
			}
			// MTM
			if (tipo.trim().equals(Constantes.MOV_LABOR_EXCEPCIONAL)) {
				lista = (ArrayList) session.getAttribute("listaAprobadores");
			}

			// PAS20171U230200001 - solicitud de reintegro
			// Obtener datos para la solicitud a registrar
			ArrayList listaAdjuntos = new ArrayList();
			List listaConceptos = new ArrayList();
			BeanSolReintegro solicitudRein = new BeanSolReintegro();

			if (tipo.trim().equals(
					Constantes.MOV_REINTEGRO_POR_DESCUENTO_ASISTENCIA)) {
				SolicitudDelegate sd = new SolicitudDelegate();
				Map dataTrabajador = sd
						.obtenerDataTrabajador(pool_sig, codPers); // obtener
																	// Regimen
																	// Laboral

				String regLab = (String) dataTrabajador.get("reg_lab_per");
				String tipoPlan = (String) dataTrabajador.get("tipo_plan_tpl");
				solicitudRein.setAnnSolplan(request.getParameter("anioReintegro"));
				solicitudRein.setMesSolplan(request.getParameter("mesReintegro"));				
				solicitudRein.setCodPlanorig(request.getParameter("tipoPlanReintegro"));
				solicitudRein.setCodSplanorig(request.getParameter("tipoSubPlanReintegro"));
				String numArchivo = request.getParameter("numArchivo");
				if (numArchivo != null && numArchivo != "") {
					solicitudRein.setNumArchivo(new Integer(numArchivo));
				}

				//solicitudRein.setCodPlanorig(tipoPlan);
				//solicitudRein.setCodSplanorig("1")
				listaConceptos = procesarConceptos(request);
				solicitudRein.setConceptos(listaConceptos);

				fechaIni = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
				fechaFin = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

			}
			if (tipo.trim().equals(
					Constantes.MOV_REINTEGRO_POR_DESCUENTO_SUBSIDIO)) {
				SolicitudDelegate sd = new SolicitudDelegate();
				Map dataTrabajador = sd
						.obtenerDataTrabajador(pool_sig, codPers); // obtener
																	// Regimen
																	// Laboral

				String regLab = (String) dataTrabajador.get("reg_lab_per");
				String tipoPlan = (String) dataTrabajador.get("tipo_plan_tpl");
				solicitudRein.setAnnSolplan(request.getParameter("anioReintegro"));
				solicitudRein.setMesSolplan(request.getParameter("mesReintegro"));				
				solicitudRein.setCodPlanorig(request.getParameter("tipoPlanReintegro"));
				solicitudRein.setCodSplanorig(request.getParameter("tipoSubPlanReintegro"));
				String numArchivo = request.getParameter("numArchivo");
				if (numArchivo != null && numArchivo != "") {
					solicitudRein.setNumArchivo(new Integer(numArchivo));
				}
			
				listaConceptos = procesarConceptos(request);
				solicitudRein.setConceptos(listaConceptos);

				fechaIni = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
				fechaFin = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

			}
			// fin-PAS20171U230200001 - solicitud de reintegro
			
			//PAS20181U230200023
			String codDiagnostico ="";
			String 	codCMP ="";
			Integer	numArchivo =new Integer(0);
			if (tipo.trim().equals(	Constantes.LICENCIA_ENFERMEDAD)) {
				codDiagnostico = request.getParameter("codDiagnostico");
				codCMP = request.getParameter("codCMP");
				numArchivo = (Integer)session.getAttribute("numArchivo");
			}
			HashMap mapa = new HashMap();
			mapa.put("asunto", asunto);
			mapa.put("fechaIni", fechaIni);
			mapa.put("fechaFin", fechaFin);
			mapa.put("nvaFechaIni", nvaFechaIni);
			mapa.put("fechaNac", fechaNac);
			mapa.put("txtObs", txtObs);
			mapa.put("txtObsSeg", txtObsSeg);
			mapa.put("fechaMarca", fechaMarca);
			mapa.put("txtHora", txtHora);
			mapa.put("annoVac", annoVac);
			mapa.put("diasVac", diasVac);
			mapa.put("numDias", numDias);
			mapa.put("numero", numero);
			mapa.put("userOrig", (String) solicitante.get("t02cod_pers"));
			mapa.put("codUO", (String) solicitante.get("t12cod_uorg_jefe"));
			mapa.put("codCate", (String) solicitante.get("t02cod_cate"));
			mapa.put("userDest", (String) solicitante.get("t12cod_jefe"));
			mapa.put("codCreador", (String) solicitante.get("codCreador"));
			mapa.put("jefedelego", (String) solicitante.get("jefedelego"));
			mapa.put("tipo", tipo);

			mapa.put("programadasSel", programdasSel); // dtarazona 3er
														// entregable
			mapa.put("reprogramadas", reprogramdas);// dtarazona 3er entregable
			mapa.put("permitirAdelanto", permitirAdelanto);
			
			// PRAC-ASANCHEZ 24/06/2009
			// mapa.put("indPreguntaSiNo", indPreguntaSiNo);
			// JRR - 10/11/2009
			mapa.put("fNacimiento", fNacimiento);
			mapa.put("fAlta", fAlta);
			//
			// MTM - 14/03/2012
			mapa.put("lista", lista);
			// ICR - 28/11/2012 PAS20124E550000064 labor excepcional
			mapa.put("regimenCol", (String) solicitante.get("t02cod_rel")); // from
																			// t02dao(sp.dao)

			if (log.isDebugEnabled())
				log.debug("MAPA ANTES DE Vacacion : " + mapa);

			// JRR - 13/02/2009
			mapa.put("userOrigUO", solicitante.get("t02cod_uorg"));

			// PAS20171U230200001 - solicitud de reintegro
			// colocar todos los datos de solicitud (conceptos y detalles)
			mapa.put("listaAdjuntos", listaAdjuntos);
			mapa.put("solicitudRein", solicitudRein);
			// fin- PAS20171U230200001 - solicitud de reintegro

			
			//PAS20181U230200023 -licencia enfermedad 
			mapa.put("codDiagnostico", codDiagnostico);
			mapa.put("codCMP", codCMP); 
			mapa.put("numArchivo", numArchivo); 

			// WERR-PAS20155E230300132
			T01DAO paramDAO = new T01DAO();
			boolean esDiaHabil = true;
			Date dfechaIni = null;
			Date dfechaAlta = null;
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			if (!StringUtils.isEmptyString(fechaNac)) {
				dfechaIni = formatter.parse(fechaNac);
			}
			if (!StringUtils.isEmptyString(fAlta)) {
				dfechaAlta = formatter.parse(fAlta);
			}

			if (tipo.trim().equals(Constantes.LICENCIA_NACIMIENTO)) {
				if (paramDAO.findByFechaFeriado(pool_sp_g, fAlta)
						|| Utiles.isWeekEnd(fAlta)) {
					log.debug("entro0");
					esDiaHabil = false;
				}
				log.debug("esDiaHabil: " + esDiaHabil);
				if (esDiaHabil == false) {
					log.debug("final:entro alta inhabil");
					if (dfechaIni.compareTo(dfechaAlta) > 0) { // add esta linea
																// (permitir fec
																// inicio <= fec
																// alta inhabil)
						log.debug("final:fec inicio mayor fec alta (inhabil)");
						if (verificarDiaNoHabil(fAlta, tipo, fechaNac, session) == 1) {
							log.debug("entro2");
							// fechaIni=fechaNac
							BeanMensaje bean = new BeanMensaje();
							bean.setError(true);
							bean.setMensajeerror("No se puede registrar la solicitud, la fecha de inicio debe de ser maximo el dia habil siguiente a la fecha de alta.");
							bean.setMensajesol("Por favor corregir.");
							session.setAttribute("beanErr", bean);
							RequestDispatcher dispatcher = getServletContext()
									.getRequestDispatcher("/PagE.jsp");
							dispatcher.forward(request, response);
							return;
						}
					}// add esta linea
					log.debug("final: llego aca");
				} else { // alta es dia habil
					log.debug("entro alta habil");
					if (dfechaIni.compareTo(dfechaAlta) > 0) {
						BeanMensaje bean = new BeanMensaje();
						bean.setMensajeerror("La fecha de inicio debe ser menor o igual a la fecha de alta");
						bean.setMensajesol("Por favor corregir.");
						session.setAttribute("beanErr", bean);
						RequestDispatcher dispatcher = getServletContext()
								.getRequestDispatcher("/PagE.jsp");
						dispatcher.forward(request, response);
						return;
					}
				}
			}
			// WERR-PAS20155E230300132

			if (log.isDebugEnabled())
				log.debug("antes de entrar a supervisores(mapa): " + mapa);// ICR
																			// -
																			// 30/11/2012
																			// PAS20124E550000064
																			// labor
																			// excepcional
			if (log.isDebugEnabled())
				log.debug("antes de entrar a supervisores(solicitante): "
						+ solicitante);// ICR - 30/11/2012 PAS20124E550000064
										// labor excepcional
			// if (tipo.trim().equals(Constantes.VACACION)){//ICR - 30/11/2012
			// PAS20124E550000064 labor excepcional
			if ("1".equals("1")) {// ICR - 30/11/2012 PAS20124E550000064 labor
									// excepcional
				if (log.isDebugEnabled())
					log.debug("obteniendo supervisores");

				mapa.put("tipo", tipo);
				session.removeAttribute("mapasol");
				session.setAttribute("mapasol", mapa);
				obtener_supervisores(pool_sp, mapa, tipo, usuario, request,
						response, session);

			} else {
				SolicitudDelegate sd = new SolicitudDelegate();

				/******************* JRR NUEVA SOLICITUD *********************/
				if (tipo.trim().equals(Constantes.VACACION_INDEMNIZADA)) {
					String periodo = request.getParameter("periodo");
					mapa.put("periodo", periodo);
					// if (log.isDebugEnabled())
					// log.debug("NUEVA SOLICITUD - mapa: " + mapa);
				}
				/*************************************************************/

				if (fechaIni == null && fechaNac == null && fechaMarca == null) {
					bean.setError(true);
					bean.setMensajeerror("NO PUEDE REGISTRAR SOLICITUD, NO APLICABLE PARA TURNO R67, OPERATIVO, SIN CONTROL");
					bean.setMensajesol("Por favor comuniquese con Asistencia Tributos Lima.");
					session.setAttribute("beanErr", bean);
					RequestDispatcher dispatcher = getServletContext()
							.getRequestDispatcher("/PagE.jsp");
					dispatcher.forward(request, response);
					return;
				}

				ArrayList mensajes = sd.verificarSolicitud(pool_sp_g, mapa,
						tipo, usuario);

				if (mensajes != null && !mensajes.isEmpty()) {

					beanMsg = new BeanMensaje();
					beanMsg.setListaobjetos(mensajes.toArray());
					beanMsg.setMensajeerror("La solicitud no se ha podido registrar por los siguientes motivos: ");
					beanMsg.setMensajesol("Por favor intente nuevamente.");
					session.setAttribute("beanErr", beanMsg);
					RequestDispatcher dispatcher = getServletContext()
							.getRequestDispatcher("/PagM.jsp");
					dispatcher.forward(request, response);
					return;

				} else {

					ArrayList listaSolicitudes = sd.cargarSolicitudesIniciadas(
							pool_sp, codPers, "-1", "", "", usuario);

					session.removeAttribute("listaSolicitudes");
					session.setAttribute("listaSolicitudes", listaSolicitudes);

					RequestDispatcher dispatcher = getServletContext()
							.getRequestDispatcher("/solicitudesIniciadas.jsp");
					dispatcher.forward(request, response);
					return;
				}
			}

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
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}
	}

	/**
	 * Metodo encargado de cargar una solicitud para su visualizacion
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void visualizarSolicitud(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UtilesDelegate ud = new UtilesDelegate();
			SolicitudDelegate sd = new SolicitudDelegate();

			ArrayList listaSolicitudes = (ArrayList) session
					.getAttribute("listaSolicitudes");
			log.debug("listaSolicitudes ->" + listaSolicitudes);
			String indice = request.getParameter("indice");
			HashMap solicitud = new HashMap();
			if (listaSolicitudes != null) {
				solicitud = (HashMap) listaSolicitudes.get(Integer
						.parseInt(indice));
				HashMap solicitante = ud.buscarTrabajadorJefe(pool_sp,
						(String) solicitud.get("codPers"), null);
				session.removeAttribute("solicitante");
				session.setAttribute("solicitante", solicitante);
				solicitud = sd.cargarSolicitud(pool_sp,
						(String) solicitud.get("codPers"),
						(String) solicitud.get("anno"),
						(String) solicitud.get("numero"));

				if (((String) solicitud.get("tipo")).trim().equals(
						Constantes.MOV_OMISION_MARCA)
						|| ((String) solicitud.get("tipo")).trim().equals(
								Constantes.MOV_ANULACION_MARCA)) {
					String txtObs = (String) solicitud.get("txtObs");
					StringTokenizer st = new StringTokenizer(txtObs, "&hora=");
					solicitud.put("txtObs", st.nextToken());
					solicitud.put("txtHora", st.nextToken());
				}

				if (((String) solicitud.get("tipo")).trim().equals(
						Constantes.VACACION_VENTA)) {
					String txtObs = (String) solicitud.get("txtObs");
					solicitud.put("txtObs",
							txtObs.substring(0, txtObs.indexOf("&diasVenta=")));
					solicitud.put("diasVenta",
							txtObs.substring(
									txtObs.indexOf("&diasVenta=") + 11,
									txtObs.length()));
				}

				// PAS20171U230200001 - solicitud de reintegro
				if (((String) solicitud.get("tipo")).trim().equals(
						Constantes.MOV_REINTEGRO_POR_DESCUENTO_ASISTENCIA)
						|| ((String) solicitud.get("tipo"))
								.trim()
								.equals(Constantes.MOV_REINTEGRO_POR_DESCUENTO_SUBSIDIO)) {

					Map solicitudReintegro = sd.obtenerDatosSolReintegro(
							(String) solicitud.get("codPers"),
							(String) solicitud.get("anno"),
							(String) solicitud.get("numero"));
					log.debug("detalle ->" + solicitudReintegro);
					solicitud.put("solRein", solicitudReintegro);
				}

				// PAS20181U230200023 - solicitud de licencia por enfermedad
				if (((String) solicitud.get("tipo")).trim().equals(Constantes.LICENCIA_ENFERMEDAD)) {
					Map solicitudLicMedica = sd.obtenerDatosSolLicenciaEnfermedad(
							(String) solicitud.get("codPers"),
							(String) solicitud.get("anno"),
							(String) solicitud.get("numero"));
					log.debug("detalle ->" + solicitudLicMedica);
					solicitud.put("solLicenMedica", solicitudLicMedica);
				}

				

				// si el usuario creador es distinto al usuario creador de la
				// solicitud
				HashMap solCreador = new HashMap();
				if (!((String) solicitud.get("codPers"))
						.equals((String) solicitud.get("codCreador"))) {
					String codCreador = (String) solicitud.get("codCreador");
					solCreador = ud.buscarTrabajador(pool_sp, codCreador, null);
					solCreador.put("valido", "1");
				}
				ArrayList seguimientos = sd.cargarSeguimientos(pool_sp,
						(String) solicitud.get("codPers"),
						(String) solicitud.get("anno"),
						(String) solicitud.get("numero"));

				session.removeAttribute("seguimientos");
				session.setAttribute("seguimientos", seguimientos);
				session.removeAttribute("solCreador");
				session.setAttribute("solCreador", solCreador);
				session.removeAttribute("solicitudIniciada");
				session.setAttribute("solicitudIniciada", solicitud);

			}

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/visualizarSolicitud.jsp");
			dispatcher.forward(request, response);
			return;

		} catch (SolicitudException e) {
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
			log.error(e);
			e.printStackTrace();
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
					+ e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * Metodo encargado de cargar una solicitud para su visualizacion:
	 * jquispecoi 03/2014
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void visualizarSolicitudAdministracion(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UtilesDelegate ud = new UtilesDelegate();
			SolicitudDelegate sd = new SolicitudDelegate();
			LicenciaDelegate ld = new LicenciaDelegate();
			VacacionDelegate vd = new VacacionDelegate();// ICAPUNAY 23072015 -
															// PAS20155E230300073
															// - Habilitar Lic.
															// Matrimonio CAS a
															// cuenta

			ArrayList listaSolicitudes = (ArrayList) session
					.getAttribute("listaSolicitudes");
			String indice = request.getParameter("indice");
			HashMap solicitud = new HashMap();
			String btn_accion = "--";
			if (listaSolicitudes != null) {
				solicitud = (HashMap) listaSolicitudes.get(Integer
						.parseInt(indice));
				HashMap solicitante = ud.buscarTrabajadorJefe(pool_sp,
						(String) solicitud.get("codPers"), null);
				session.removeAttribute("solicitante");
				session.setAttribute("solicitante", solicitante);
				solicitud = sd.cargarSolicitud(pool_sp,
						(String) solicitud.get("codPers"),
						(String) solicitud.get("anno"),
						(String) solicitud.get("numero"));
				// CARGAR DETALLE 124
				if (((String) solicitud.get("tipo")).trim().equals(
						Constantes.MOV_LABOR_EXCEPCIONAL)) { // TIPO 124
					List lista = sd.listaSolicitudAdministracion(
							pool_sp,
							(String) solicitud.get("anno"),
							(String) solicitud.get("numero"),
							(String) solicitud.get("uorgan"), // uo sol
							(String) solicitud.get("codPers"),
							(String) solicitud.get("tipo"));
					solicitud.put("lista", lista);
				}

				// si el usuario creador es distinto al usuario creador de
				// lasolicitud
				HashMap solCreador = new HashMap();
				if (!((String) solicitud.get("codPers"))
						.equals((String) solicitud.get("codCreador"))) {
					String codCreador = (String) solicitud.get("codCreador");
					solCreador = ud.buscarTrabajador(pool_sp, codCreador, null);
					solCreador.put("valido", "1");
				}

				// CARGAR SEGUIMIENTOS
				ArrayList seguimientos = sd.cargarSeguimientos(pool_sp,
						(String) solicitud.get("codPers"),
						(String) solicitud.get("anno"),
						(String) solicitud.get("numero"));

				// CARGAR LICENCIA
				String sTipoLic = ((String) solicitud.get("tipo")).trim();
				if (sTipoLic.equals(Constantes.MOV_SOLICITUD_COMPENSACION)
						|| sTipoLic.equals(Constantes.LICENCIA_ENFERMEDAD)
						|| sTipoLic.equals(Constantes.FERIADO_COMPENSABLE)) {
					HashMap licencia = (HashMap) ld.buscarLicenciaSolRef(
							pool_sp, (String) solicitud.get("anno"),
							(String) solicitud.get("numero"),
							(String) solicitud.get("codPers"),
							(String) solicitud.get("tipo"));
					solicitud.put("licencia", licencia);
					log.info("licencia--" + licencia);
				}

				// ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic.
				// Matrimonio CAS a cuenta
				// CARGAR VACACION EFECTUADA RELACIONADA A LA LICENCIA DE
				// MATRIMONIO CAS
				if (((String) solicitud.get("tipo")).trim().equals(
						Constantes.LICENCIA_MATRIMONIO)) { // TIPO 23
					HashMap vacacionEfec = (HashMap) vd
							.buscarVacacionPorSolRef(pool_sp,
									(String) solicitud.get("anno"),
									(String) solicitud.get("numero"),
									(String) solicitud.get("codPers"),
									Constantes.VACACION, Constantes.ACTIVO);
					log.info("vacacionEfec--" + vacacionEfec);
					solicitud.put("vacacionEfec", vacacionEfec);
				}
				// FIN ICAPUNAY 23072015 - PAS20155E230300073

				String accion_id = (String) ((HashMap) seguimientos.get(0))
						.get("accionId");
				String estado_id = (String) ((HashMap) seguimientos.get(0))
						.get("estId");

				if (accion_id.trim().equals(Constantes.ACCION_APROBAR)
						&& estado_id.trim().equals(Constantes.ESTADO_CONCLUIDA)) {
					if (((String) solicitud.get("tipo")).trim().equals(
							Constantes.MOV_LABOR_EXCEPCIONAL))
						btn_accion = "AA";
					else if (((String) solicitud.get("tipo")).trim().equals(
							Constantes.MOV_SOLICITUD_COMPENSACION))
						btn_accion = "MM";
					else if (((String) solicitud.get("tipo")).trim().equals(
							Constantes.LICENCIA_ENFERMEDAD))
						btn_accion = "AA";
					else if (((String) solicitud.get("tipo")).trim().equals(
							Constantes.FERIADO_COMPENSABLE))
						btn_accion = "AA";
					// ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic.
					// Matrimonio CAS a cuenta
					else if (((String) solicitud.get("tipo")).trim().equals(
							Constantes.LICENCIA_MATRIMONIO))
						btn_accion = "AA";
					// FIN ICAPUNAY 23072015 - PAS20155E230300073
				}

				session.removeAttribute("seguimientos");
				session.setAttribute("seguimientos", seguimientos);
				session.removeAttribute("solCreador");
				session.setAttribute("solCreador", solCreador);
				session.removeAttribute("solicitudIniciada");
				session.setAttribute("solicitudIniciada", solicitud);
				session.removeAttribute("btn_accion");
				session.setAttribute("btn_accion", btn_accion);
			}

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher(
							"/visualizarSolicitudAdministracion.jsp");
			dispatcher.forward(request, response);
			return;

		} catch (SolicitudException e) {
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
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	//PAS20181U230200067 - solicitud de reintegro, planillas adicionales
	/**
	 * Metodo encargado de cargar la pagina para la derivacion de solicitudes
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarDerivarSolicitud(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			String pagina = "/derivarSolicitud.jsp";
			SolicitudDelegate sd = new SolicitudDelegate();
			UtilesDelegate ud = new UtilesDelegate();
			
			ArrayList listaRecibidas = (ArrayList) session
					.getAttribute("listaRecibidas");
			String indice = "";
			int indDetalle = -1;

			indice = request.getParameter("indSolicitud") != null ? request
					.getParameter("indSolicitud") : request
					.getParameter("indice");

			HashMap solicitud = new HashMap();
			HashMap solMem = null;
			if (listaRecibidas != null) {

				solicitud = (HashMap) listaRecibidas.get(Integer
						.parseInt(indice));
				if (request.getParameter("indSolicitud") != null) {
					solMem = new HashMap();
					solMem.putAll(solicitud);
				}
				String estadoSol = solicitud.get("estadoSol") != null ? solicitud
						.get("estadoSol").toString().trim()
						: "";
				log.debug("cargarDerivarSolicitud(solicitud recibida): "
						+ solicitud);
				log.debug("cargarDerivarSolicitud(estadoSol): " + estadoSol);
				// ICR - 28/11/2012 PAS20124E550000064 labor excepcional
				String regimenCol = solicitud.get("regimenCol") != null ? solicitud
						.get("regimenCol").toString().trim()
						: "";
				log.debug("cargarDerivarSolicitud(regimenCol): " + regimenCol);
				// FIN ICR - 28/11/2012 PAS20124E550000064 labor excepcional
				HashMap solicitante = ud.buscarTrabajadorJefe(pool_sp,
						(String) solicitud.get("codPers"), null);
				solicitud = sd.cargarSolicitud(pool_sp,
						(String) solicitud.get("codPers"),
						(String) solicitud.get("anno"),
						(String) solicitud.get("numero"));

				log.debug("Solicitud : " + solicitud);

				if (((String) solicitud.get("tipo")).trim().equals(
						Constantes.MOV_OMISION_MARCA)
						|| ((String) solicitud.get("tipo")).trim().equals(
								Constantes.MOV_ANULACION_MARCA)) {
					String txtObs = (String) solicitud.get("txtObs");
					log.debug("txtObs : " + txtObs);
					StringTokenizer st = new StringTokenizer(txtObs, "&hora=");
					solicitud.put("txtObs", st.nextToken());
					solicitud.put("txtHora", st.nextToken());
					log.debug("txtObs : " + solicitud.get("txtObs"));
					log.debug("txtHora : " + solicitud.get("txtHora"));
				}

				if (((String) solicitud.get("tipo")).trim().equals(
						Constantes.VACACION_VENTA)) {

					String txtObs = (String) solicitud.get("txtObs");
					log.debug("txtObs : " + txtObs);
					solicitud.put("txtObs",
							txtObs.substring(0, txtObs.indexOf("&diasVenta=")));
					solicitud.put("diasVenta",
							txtObs.substring(
									txtObs.indexOf("&diasVenta=") + 11,
									txtObs.length()));
					log.debug("txtObs : "
							+ txtObs.substring(0, txtObs.indexOf("&diasVenta=")));
					log.debug("diasVenta : "
							+ txtObs.substring(
									txtObs.indexOf("&diasVenta=") + 11,
									txtObs.length()));
				}

				// MTM
				if (((String) solicitud.get("tipo")).trim().equals(
						Constantes.MOV_LABOR_EXCEPCIONAL)) {
					List lista = sd.listaSolicitud(pool_sp,
							(String) solicitud.get("anno"),
							(String) solicitud.get("numero"),
							(String) solicitud.get("uoOrig"),
							(String) solicitud.get("codPers"),
							(String) solicitud.get("tipo"));
					log.debug("lista servlet:" + lista);
					solicitud.put("lista", lista);
				}

				// PAS20171U230200001 - solicitud de reintegro Cargar
				// informacion para aprobacion de jefe
				if (((String) solicitud.get("tipo")).trim().equals(
						Constantes.MOV_REINTEGRO_POR_DESCUENTO_SUBSIDIO)
						|| ((String) solicitud.get("tipo"))
								.trim()
								.equals(Constantes.MOV_REINTEGRO_POR_DESCUENTO_ASISTENCIA)) {

					if (solMem == null) {
						Map sol = sd.obtenerDatosSolReintegroDerivacion(
								pool_sp, (String) solicitud.get("anno"),
								(String) solicitud.get("numero"),
								(String) solicitud.get("uoOrig"),
								(String) solicitud.get("codPers"),
								(String) solicitud.get("tipo"));

						int mes = Integer.valueOf(
								(String) (sol.get("mes_solplan"))).intValue();
						sol.put("mes_solplan_desc",
								(sol.get("mes_solplan") != null && !""
										.equals(sol.get("mes_solplan")
												.toString().trim())) ? new FechaBean()
										.getMesletras(mes - 1) : "");
						log.debug("lista servlet:" + sol);
						solicitud.put("solRein", sol);
					} else {
						solicitud.put("solRein", solMem.get("solRein"));
					}
				}
				// fin- PAS20171U230200001 - solicitud de reintegro
				// PAS20181U230200023 - solicitud de licencia por enfermedad
				if (((String) solicitud.get("tipo")).trim().equals(Constantes.LICENCIA_ENFERMEDAD)) {
					Map solicitudLicMedica = sd.obtenerDatosSolLicenciaEnfermedad(
							(String) solicitud.get("codPers"),
							(String) solicitud.get("anno"),
							(String) solicitud.get("numero"));
					log.debug("detalle ->" + solicitudLicMedica);
					solicitud.put("solLicenMedica", solicitudLicMedica);
				}

				
				// dtarazona
				if (((String) solicitud.get("tipo")).trim().equals(
						Constantes.VACACION_POSTERGADA)) {
					ArrayList vacProgEst = sd.cargarVacProgramadas(pool_sp,
							(String) solicitud.get("codPers"),
							(String) solicitud.get("numero"));
					session.removeAttribute("vacProgEst");
					session.setAttribute("vacProgEst", vacProgEst);
					if (log.isDebugEnabled())
						log.debug("vacPro:" + vacProgEst);
					ArrayList vacSolRep = sd.cargarVacSolicitadasRep(pool_sp,
							(String) solicitud.get("codPers"),
							(String) solicitud.get("numero"),
							"54");
					session.removeAttribute("vacSolRep");
					session.setAttribute("vacSolRep", vacSolRep);
					if (log.isDebugEnabled())
						log.debug("vacSolRep:" + vacSolRep);
				}
				// fin dtarazona

				// obtenemos el usuario responsable de la aprobacion de la
				// solicitud
				HashMap aprobador = sd.obtenerAprobador(pool_sp, solicitud);

				// si el usuario creador es distinto al usuario creador de la
				// solicitud
				HashMap solCreador = new HashMap();
				if (!((String) solicitud.get("codPers"))
						.equals((String) solicitud.get("codCreador"))) {
					String codCreador = (String) solicitud.get("codCreador");
					solCreador = ud.buscarTrabajador(pool_sp, codCreador, null);
					solCreador.put("valido", "1");
				}

				ArrayList seguimientos = sd.cargarSeguimientos(pool_sp,
						(String) solicitud.get("codPers"),
						(String) solicitud.get("anno"),
						(String) solicitud.get("numero"));
				// log.debug("Primer Seguimiento : " + (HashMap)
				// seguimientos.get(0));
				// HashMap primerSeg = (HashMap) seguimientos.get(0);
				// String primerAprob = (String)primerSeg.get("userOrig");
				// primerAprob = primerAprob.trim() + " - "
				// +(String)primerSeg.get("remitente");
				// log.debug("Primer Aprobador : " + primerAprob);
				// solicitante.put("t12cod_jefe_desc", primerAprob);
				session.removeAttribute("seguimientos");
				session.setAttribute("seguimientos", seguimientos);

				session.removeAttribute("solicitudRecibida");
				solicitud.put("estadoSol", estadoSol);// ICAPUNAY 05/10/2012
														// AJUSTES AOM 06A4T11
														// LABOR EXCEPCIONAL
				solicitud.put("regimenCol", regimenCol);// ICR - 28/11/2012
														// PAS20124E550000064
														// labor excepcional
				session.setAttribute("solicitudRecibida", solicitud);

				// PAS20171U230200001 - solicitud de reintegro RMONTES
				TurnoTrabajoDelegate ttd = new TurnoTrabajoDelegate();
				BeanTurnoTrabajo buscarTrabTurno = ttd.buscarTrabTurno(pool_sp,
						(String) solicitante.get("t02cod_pers"),
						new FechaBean().getFormatDate("dd/MM/yyyy"));
				if (buscarTrabTurno == null) {
					solicitante.put("turno", "SIN TURNO");
					solicitante.put("des_turno", "SIN TURNO");
				} else {
					solicitante.put("turno", buscarTrabTurno.getTurno());
					solicitante
							.put("des_turno", buscarTrabTurno.getDescTurno());
				}

				solicitante.put(
						"esCesado",
						new Boolean(sd.esTrabajadorCesado((String) solicitante
								.get("t02cod_pers"))));

				session.removeAttribute("solicitante");
				session.setAttribute("solicitante", solicitante);

				session.removeAttribute("solCreador");
				session.setAttribute("solCreador", solCreador);

				session.removeAttribute("aprobador");
				session.setAttribute("aprobador", aprobador);

				UsuarioBean bUsuario = (UsuarioBean) session
						.getAttribute("usuarioBean");
				String codPers = bUsuario.getNroRegistro();
				HashMap flujoRRHH = (HashMap) aprobador.get("flujoRRHH");

				// prac-asanchez - 18/05/2009
				Map acciones = new HashMap();
				Map datos = new HashMap();
				if (flujoRRHH != null) {
					if (flujoRRHH.isEmpty()) {// srrhh_id=0, por tanto es
												// instancia unica.
						pagina = "/DerivarSolicitudFinal.jsp";
					} else {
						acciones = (HashMap) flujoRRHH.get(codPers);
						if (log.isDebugEnabled())
							log.debug("acciones : " + acciones);// lo que sigue
						// {2={destino=1548, estado=2}}
						if (acciones != null && !acciones.isEmpty()) {
							datos = (HashMap) acciones
									.get(Constantes.ACCION_APROBAR);
							if (log.isDebugEnabled())
								log.debug("datos : " + datos);
							// datos : {destino=1548, estado=2}
							String estacion = (String) datos.get("estado");
							if (log.isDebugEnabled())
								log.debug("En que estacion estoy???" + estacion);
							// En que estacion estoy???2
							if (estacion.equals(Constantes.ESTACION_FINAL)
									|| estacion
											.equals(Constantes.ESTACION_UNICA)) {

								// PAS20171U230200001 - solicitud de reintegro
								// Estacion final : aprobacion de rrhh
								if (((String) solicitud.get("tipo"))
										.trim()
										.equals(Constantes.MOV_REINTEGRO_POR_DESCUENTO_SUBSIDIO)
										|| ((String) solicitud.get("tipo"))
												.trim()
												.equals(Constantes.MOV_REINTEGRO_POR_DESCUENTO_ASISTENCIA)) {
									// PAS20171U230200033 - solicitud de
									// reintegro debe siempre traer datos
									// guardados
									// if (solMem == null){
									Map sol = sd.obtenerDatosSolReintegroFinal(
											(String) solicitud.get("anno"),
											(String) solicitud.get("numero"),
											(String) solicitud.get("uoOrig"),
											(String) solicitud.get("codPers"),
											(String) solicitud.get("tipo"),
											(String) solicitante
													.get("t02cod_rel"));
									int mes = Integer.valueOf(
											(String) (sol.get("mes_solplan")))
											.intValue();
									sol.put("mes_solplan_desc",
											(sol.get("mes_solplan") != null && !""
													.equals(sol
															.get("mes_solplan")
															.toString().trim())) ? new FechaBean()
													.getMesletras(mes - 1) : "");
									log.debug("lista servlet:" + sol);

									solicitud.put("solRein", sol);
									// } else {
									// solicitud.put("solRein",
									// solMem.get("solRein"));
									// }
								}
								// fin- PAS20171U230200001 - solicitud de
								// reintegro

								pagina = "/DerivarSolicitudFinal.jsp";
								request.setAttribute("indSolicitud", indice);
							}
//							else {
								// PAS20181U230200067 - solicitud de reintegro, codigo comentado se retira la validacion 
//								// PAS20171U230200001 - solicitud de reintegro
//								// Estacion de aprobacion de jefe verificar que
//								// existe flujo de rrhh
//								if (((String) solicitud.get("tipo"))
//										.trim()
//										.equals(Constantes.MOV_REINTEGRO_POR_DESCUENTO_SUBSIDIO)
//										|| ((String) solicitud.get("tipo"))
//												.trim()
//												.equals(Constantes.MOV_REINTEGRO_POR_DESCUENTO_ASISTENCIA)) {
//									// en interface de jefe
//									solicitud.put("ind_aprob_asist_noconf", "0");
//									String aprobadorFinal = datos
//											.get("destino") != null ? datos
//											.get("destino").toString() : "";
//									if (aprobadorFinal != null	&& !("").equals(aprobadorFinal)) {
//										Map accionesFinal = (HashMap) flujoRRHH.get(aprobadorFinal);
//										if (accionesFinal != null)
//											solicitud.put("ind_aprob_asist_noconf","1");
//									}
//
//								}
//								// fin- PAS20171U230200001 - solicitud de
//								// reintegro
//							}
						}
					}
				}
				//
			}

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher(pagina);
			dispatcher.forward(request, response);
			return;

		} catch (SolicitudException e) {
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
					+ e.toString());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * Metodo encargado de procesar una solicitud para su derivacion, aprobacion
	 * o rechazo
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void procesarSolicitud(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		BeanMensaje beanMsg = null;
		try {

			UsuarioBean bUsuario = (UsuarioBean) session
					.getAttribute("usuarioBean");
			String usuario = bUsuario.getLogin();
			String codReg = bUsuario.getNroRegistro();
			if (log.isDebugEnabled())
				log.debug("login: " + usuario);
			HashMap seguridad = null;

			HashMap solicitud = (HashMap) session
					.getAttribute("solicitudRecibida");
			if (log.isDebugEnabled())
				log.debug("solicitud: " + solicitud);
			HashMap aprobador = (HashMap) session.getAttribute("aprobador");
			if (log.isDebugEnabled())
				log.debug("aprobador: " + aprobador);

			String accion = request.getParameter("cmbAccion") != null ? request
					.getParameter("cmbAccion") : "";
			String txtObs = request.getParameter("txtObs");
			String diasVenta = request.getParameter("diasVenta");
			String anno = request.getParameter("anno") != null ? request
					.getParameter("anno") : "";
			if (log.isDebugEnabled())
				log.debug("anno 1: " + anno);
			String tipo = (String) solicitud.get("tipo");
			if (tipo.trim().equals(Constantes.VACACION_VENTA)) {
				anno = (String) solicitud.get("annoVac");
			}
			if (log.isDebugEnabled())
				log.debug("anno 2: " + anno);

			// prac-asanchez - 18/05/2009
			String ffinicio = request.getParameter("fechaIni");
			String ffin = request.getParameter("fechaFin");
			java.sql.Timestamp ini = Utiles.stringToTimestamp(ffinicio
					+ " 00:00:00");
			java.sql.Timestamp fin = Utiles.stringToTimestamp(ffin
					+ " 00:00:00");
			int numDias;
			// solo si ffinicio y ffin son != de nulos y toman algun valor,
			// deber seguir, sino NO.
			if (log.isDebugEnabled())
				log.debug("solicitud:" + solicitud);
			if ((ffinicio != null && ffinicio != "" && ffin != null && ffin != "")) {
				solicitud.put("ffinicio", ini);// la fecha de inicio cambiada
												// por ultimo aprobador
				solicitud.put("ffin", fin);// la fecha de fin cambiada por
											// ultimo aprobador
				numDias = Utiles.obtenerDiasDiferencia(ffinicio, ffin) + 1;
				solicitud.put("dias", new Integer(numDias));
			}
			//

			// if (anno!=null && anno.length()>0){
			// solicitud.put("annoVac",anno);
			// }
			HashMap aux = new HashMap();
			aux.put("dbpool", pool_sp);
			// aux.put("codPers",codReg);
			aux.put("cod_pers", codReg);
			aux.put("codUO", (String) solicitud.get("uoOrig"));

			UtilesDelegate ud = new UtilesDelegate();
			Map superior = new HashMap();
			if (log.isDebugEnabled())
				log.debug("tipo : " + (String) solicitud.get("tipo"));
			// String tipo = (String)solicitud.get("tipo");
			if (tipo.trim().equals(Constantes.VACACION)) {
				if (log.isDebugEnabled())
					log.debug("entre tipo 07");
				superior = ud.buscarTrabajadorJefe(pool_sp, codReg, seguridad);
				// superior.put("codJefe",(String)superior.get("t12cod_jefe"));
				// superior.put("codUOJefe",(String)superior.get("t12cod_uorg_jefe"));
				// 21/01/2009
				superior.put(
						"codJefe",
						superior.get("t12cod_jefe") != null ? (String) superior
								.get("t12cod_jefe") : (String) superior
								.get("t02cod_pers"));
				superior.put(
						"codUOJefe",
						superior.get("t12cod_uorg_jefe") != null ? (String) superior
								.get("t12cod_uorg_jefe") : (String) superior
								.get("t02cod_uorg"));
				//
			} else {
				if (log.isDebugEnabled())
					log.debug("NO entre tipo 07");
				superior = ud.buscarSuperiorSolicitud(aux);
				if (log.isDebugEnabled())
					log.debug("superior: " + superior);
				// 21/01/2009
				superior.put(
						"codJefe",
						superior.get("codJefe") != null ? (String) superior
								.get("codJefe") : (String) aux.get("cod_pers"));
				superior.put(
						"codUOJefe",
						superior.get("codUOJefe") != null ? (String) superior
								.get("codUOJefe") : (String) aux.get("codUO"));
				if (log.isDebugEnabled())
					log.debug("superior final: " + superior);
				//
				if (tipo.trim().equals(Constantes.VACACION_POSTERGADA)) {
					List datosImp = (ArrayList) session
							.getAttribute("vacProgEst");
					List datosImp1 = (ArrayList) session
							.getAttribute("vacSolRep");
					solicitud.put("d1", datosImp);
					solicitud.put("d2", datosImp1);
				}
			}

			if (log.isDebugEnabled())
				log.debug("Superior Solicitud : " + superior);

			HashMap mapa = new HashMap();
			mapa.put("accion", accion);
			mapa.put("txtObs", txtObs + "&diasVenta=" + diasVenta);
			mapa.put("userOrig", codReg);
			mapa.put("codJefe", (String) superior.get("codJefe"));
			mapa.put("codUOSeg", (String) superior.get("codUOJefe"));

			if (log.isDebugEnabled())
				log.debug("Antes de procesarSolicitud : " + mapa);

			SolicitudDelegate sd = new SolicitudDelegate();
			ArrayList res = sd.procesarSolicitud(pool_sp_g, solicitud, mapa,
					aprobador, usuario);

			if (res != null && !res.isEmpty()) {

				beanMsg = new BeanMensaje();
				beanMsg.setListaobjetos(res.toArray());
				beanMsg.setMensajesol("Por favor intente nuevamente.");
				session.setAttribute("beanErr", beanMsg);
				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/PagM.jsp");
				dispatcher.forward(request, response);
				return;

			} else {
				ArrayList listaRecibidas = sd.cargarSolicitudesRecibidas(
						pool_sp, codReg, "-1", "", "");

				session.removeAttribute("listaRecibidas");
				session.setAttribute("listaRecibidas", listaRecibidas);

				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/solicitudesRecibidas.jsp");
				dispatcher.forward(request, response);
				return;
			}

		} catch (SolicitudException e) {
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
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * Metodo encargado de procesar una solicitud aprobada para su rechazo o
	 * modificacion: jquispecoi 03/2014
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void procesarSolicitudAdministracion(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		BeanMensaje beanMsg = null;
		try {

			UsuarioBean bUsuario = (UsuarioBean) session
					.getAttribute("usuarioBean");
			ArrayList listaMensajes = (ArrayList) session
					.getAttribute("listaMensajes");
			String usuario = bUsuario.getLogin();
			String codReg = bUsuario.getNroRegistro();

			HashMap solicitud = (HashMap) session
					.getAttribute("solicitudIniciada");
			String accion_adm = request.getParameter("accion_adm") != null ? request
					.getParameter("accion_adm") : "";
			String ffinicio = request.getParameter("txtFechaIni");
			String ffin = request.getParameter("txtFechaFin");

			if (listaMensajes == null)
				listaMensajes = new ArrayList();

			HashMap mapa = new HashMap();
			mapa.put("accion_adm", accion_adm);
			mapa.put("userOrig", codReg); // el que realiza el cambio
			mapa.put("listaMensajes", listaMensajes);
			if ((ffinicio != null && ffinicio != "" && ffin != null && ffin != "")) {
				mapa.put("ffinicio", ffinicio);
				mapa.put("ffin", ffin);
			}

			if (log.isDebugEnabled())
				log.debug("Antes de procesarAdministracion : " + mapa);
			SolicitudDelegate sd = new SolicitudDelegate();
			ArrayList res = sd.procesarSolicitudAdministracion(pool_sp_g,
					solicitud, mapa, usuario);

			if (res != null && !res.isEmpty()) {
				beanMsg = new BeanMensaje();
				beanMsg.setListaobjetos(res.toArray());
				beanMsg.setMensajesol("Por favor intente nuevamente.");
				session.setAttribute("beanErr", beanMsg);
				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/PagM.jsp");
				dispatcher.forward(request, response);
				return;
			} else {
				// Pantalla a mostrar
				MantenimientoDelegate md = new MantenimientoDelegate();
				ArrayList tipos = md.buscarMovimientosSolicitud(pool_sp,
						Constantes.ACTIVO);

				// solo movimientos permitidos
				ArrayList tipos_final = new ArrayList();
				// String[] movs_p={"21","38","124","125"}; //ICAPUNAY 23072015
				// - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta
				String[] movs_p = { "21", "38", "124", "125", "23" }; // (agrego
																		// 23)ICAPUNAY
																		// 23072015
																		// -
																		// PAS20155E230300073
																		// -
																		// Habilitar
																		// Lic.
																		// Matrimonio
																		// CAS a
																		// cuenta
				for (int i = 0; i < tipos.size(); i++) {
					HashMap tipo = (HashMap) tipos.get(i);

					// ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic.
					// Matrimonio CAS a cuenta
					if (((String) tipo.get("mov")).equals("23")) {
						tipo.put("descrip", ((String) tipo.get("descrip"))
								+ " - CAS");
					}
					// ICAPUNAY 23072015 - PAS20155E230300073

					for (int j = 0; j < movs_p.length; j++)
						if (movs_p[j].equals((String) tipo.get("mov")))
							tipos_final.add(tipo);
				}

				session.removeAttribute("listaMensajes");
				session.setAttribute("listaMensajes", mapa.get("listaMensajes"));
				session.removeAttribute("tiposSolicitud");
				session.setAttribute("tiposSolicitud", tipos_final);
				session.removeAttribute("listaSolicitudes");
				session.setAttribute("ScmbTipo", "");
				session.setAttribute("ScmbCriterio2", "");
				session.setAttribute("StxtValor2", "");
				session.setAttribute("ScmbEstado", "");
				session.setAttribute("ScmbCriterio", "");
				session.setAttribute("StxtValor", "");
				session.setAttribute("StxtFechaFin", "");
				session.setAttribute("cmbTipo", "");
				session.setAttribute("cmbCriterio2", "");
				session.setAttribute("txtValor2", "");
				session.setAttribute("cmbEstado", "");
				session.setAttribute("cmbCriterio", "");
				session.setAttribute("txtValor", "");
				session.setAttribute("txtFechaFin", "");

				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/consultaAdministracion.jsp");
				dispatcher.forward(request, response);
				return;
			}

		} catch (SolicitudException e) {
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
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}
	}

	/**
	 * Metodo encargado de cargar el seguimiento de una solicitud
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarSeguimiento(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UtilesDelegate ud = new UtilesDelegate();

			boolean entrada = request.getParameter("entrada") != null ? request
					.getParameter("entrada").equals("1") : false;
			HashMap solicitudSeguimiento = null;
			if (entrada) {
				solicitudSeguimiento = (HashMap) session
						.getAttribute("solicitudRecibida");
			} else {
				solicitudSeguimiento = (HashMap) session
						.getAttribute("solicitudIniciada");
			}

			ArrayList seguimientos = (ArrayList) session
					.getAttribute("seguimientos");
			String indice = request.getParameter("indice");

			HashMap seguimiento = new HashMap();
			if (seguimientos != null) {

				seguimiento = (HashMap) seguimientos.get(Integer
						.parseInt(indice));

				HashMap remitente = ud.buscarTrabajador(pool_sp,
						(String) seguimiento.get("userOrig"), null);
				HashMap destinatario = ud.buscarTrabajador(pool_sp,
						(String) seguimiento.get("userDest"), null);

				session.removeAttribute("seguimiento");
				session.setAttribute("seguimiento", seguimiento);

				session.removeAttribute("solicitudSeguimiento");
				session.setAttribute("solicitudSeguimiento",
						solicitudSeguimiento);

				request.removeAttribute("remitente");
				request.removeAttribute("destinatario");

				request.setAttribute("remitente", remitente);
				request.setAttribute("destinatario", destinatario);
			}

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/visualizarSeguimiento.jsp");
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
	private void listarLaborAuto(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			HashMap solicitante = (HashMap) session.getAttribute("solicitante");

			HashMap datos = new HashMap();
			datos.put("dbpool", pool_sp);
			datos.put("cod_pers", (String) solicitante.get("t02cod_pers"));

			SolicitudDelegate sd = new SolicitudDelegate();
			List lista = sd.findHorasLaborAutorizadasConSaldo(datos);

			session.removeAttribute("listaAutorHE");
			setAttribute(session, "listaAutorHE", lista);

			forward(request, response, "/ListaLaborAuto.jsp?idSession = "
					+ System.currentTimeMillis());

			return;
		} catch (AsistenciaException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			log.error(e, e);
			bean = new MensajeBean();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error al ordenar las solicitudes");
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion");
			forwardError(request, response, bean);
		} finally {
		}
	}

	/**
	 * Metodo encargado de cargar las solicitudes
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarConsultaGeneral(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			MantenimientoDelegate md = new MantenimientoDelegate();
			ArrayList tipos = md.buscarMovimientosSolicitud(pool_sp,
					Constantes.ACTIVO);
			session.removeAttribute("tiposSolicitud");
			session.setAttribute("tiposSolicitud", tipos);
			session.removeAttribute("listaSolicitudes");

			// prac-asanchez
			session.setAttribute("ScmbTipo", "");
			session.setAttribute("ScmbCriterio2", "");
			session.setAttribute("StxtValor2", "");
			session.setAttribute("ScmbEstado", "");
			session.setAttribute("ScmbCriterio", "");
			session.setAttribute("StxtValor", "");
			session.setAttribute("StxtFechaFin", "");

			session.setAttribute("cmbTipo", "");
			session.setAttribute("cmbCriterio2", "");
			session.setAttribute("txtValor2", "");
			session.setAttribute("cmbEstado", "");
			session.setAttribute("cmbCriterio", "");
			session.setAttribute("txtValor", "");
			session.setAttribute("txtFechaFin", "");

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/consultaGeneral.jsp");
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
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}
	}

	/**
	 * Metodo encargado de cargar las solicitudes para Administracion:
	 * jquispecoi 03/2014
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarConsultaAdministracion(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			MantenimientoDelegate md = new MantenimientoDelegate();
			ArrayList tipos = md.buscarMovimientosSolicitud(pool_sp,
					Constantes.ACTIVO);

			// solo movimientos permitidos
			ArrayList tipos_final = new ArrayList();
			// String[] movs_p={"21","38","124","125"}; //ICAPUNAY 23072015 -
			// PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta
			String[] movs_p = { "21", "38", "124", "125", "23" }; // (agrego
																	// 23)ICAPUNAY
																	// 23072015
																	// -
																	// PAS20155E230300073
																	// -
																	// Habilitar
																	// Lic.
																	// Matrimonio
																	// CAS a
																	// cuenta
			for (int i = 0; i < tipos.size(); i++) {
				HashMap tipo = (HashMap) tipos.get(i);

				// ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic.
				// Matrimonio CAS a cuenta
				if (((String) tipo.get("mov")).equals("23")) {
					tipo.put("descrip", ((String) tipo.get("descrip"))
							+ " - CAS");
				}
				// ICAPUNAY 23072015 - PAS20155E230300073

				for (int j = 0; j < movs_p.length; j++)

					if (movs_p[j].equals((String) tipo.get("mov")))
						tipos_final.add(tipo);
			}

			session.removeAttribute("tiposSolicitud");
			session.setAttribute("tiposSolicitud", tipos_final);
			session.removeAttribute("listaSolicitudes");

			session.setAttribute("ScmbTipo", "");
			session.setAttribute("ScmbCriterio2", "");
			session.setAttribute("StxtValor2", "");
			session.setAttribute("ScmbEstado", "");
			session.setAttribute("ScmbCriterio", "");
			session.setAttribute("StxtValor", "");
			session.setAttribute("StxtFechaFin", "");

			session.setAttribute("cmbTipo", "");
			session.setAttribute("cmbCriterio2", "");
			session.setAttribute("txtValor2", "");
			session.setAttribute("cmbEstado", "");
			session.setAttribute("cmbCriterio", "");
			session.setAttribute("txtValor", "");
			session.setAttribute("txtFechaFin", "");

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/consultaAdministracion.jsp");
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
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}
	}

	/**
	 * Metodo encargado de buscar las solicitudes
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarConsultaGeneral(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			RequestDispatcher dispatcher;

			UsuarioBean bUsuario = (UsuarioBean) session
					.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();// .getNumreg();
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); // super.getRoles(session);
			log.debug("Roles " + roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles(usrRoles);
			log.debug("Roles " + roles);
			// String codigo = bUsuario.getNumreg();
			// HashMap roles = super.getRoles(session);
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null)
				uoSeg = uoSeg.substring(0, 2).concat("%");

			String criterio = request.getParameter("cmbCriterio");
			String valor = request.getParameter("txtValor");
			String criterio2 = request.getParameter("cmbCriterio2");
			String valor2 = request.getParameter("txtValor2");
			String tipo = request.getParameter("cmbTipo");
			String estado_id = request.getParameter("cmbEstado");
			String valorFechaFin = request.getParameter("txtFechaFin");
			// prac-asanchez
			String tipoReporte = request.getParameter("tipoReporte") != null ? request
					.getParameter("tipoReporte") : "buscar";

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codigo);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("uoAO", bUsuario.getCodUO());// ICR 28/12/2012 NUEVAS
														// UUOOS ANALISTA
														// OPERATIVO
			log.debug("bUsuario.getCodUO(): " + bUsuario.getCodUO());// ICR
																		// 28/12/2012
																		// NUEVAS
																		// UUOOS
																		// ANALISTA
																		// OPERATIVO

			HashMap datos = new HashMap();
			datos.put("dbpool", pool_sp);
			datos.put("tipo", tipo);
			datos.put("criterio", criterio);
			datos.put("valor", valor);
			datos.put("criterio2", criterio2);
			datos.put("valor2", valor2);
			datos.put("estado_id", estado_id);
			// prac-asanchez
			datos.put("valorFechaFin", valorFechaFin);

			SolicitudDelegate sd = new SolicitudDelegate();
			ArrayList listaSolicitudes = sd.cargarConsultaGeneral(datos,
					seguridad);

			session.removeAttribute("listaSolicitudes");
			session.setAttribute("listaSolicitudes", listaSolicitudes);

			session.setAttribute("ScmbTipo", tipo);
			session.setAttribute("ScmbCriterio2", criterio2);
			session.setAttribute("StxtValor2", valor2);
			session.setAttribute("ScmbEstado", estado_id);
			session.setAttribute("ScmbCriterio", criterio);
			session.setAttribute("StxtValor", valor);
			session.setAttribute("StxtFechaFin", valorFechaFin);

			// log.debug("antes del buscar y excel");

			// prac-asanchez
			if (tipoReporte.equals("buscar")) {
				dispatcher = getServletContext().getRequestDispatcher(
						"/consultaGeneral.jsp");
				dispatcher.forward(request, response);
			}
			if (tipoReporte.equals("excel")) {
				dispatcher = getServletContext().getRequestDispatcher(
						"/ExcelConsultaGeneral.jsp");
				dispatcher.forward(request, response);
			}
			return;

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
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}
	}

	/**
	 * Metodo encargado de buscar las solicitudes administra: jquispecoi 03/2014
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarConsultaAdministracion(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			RequestDispatcher dispatcher;

			UsuarioBean bUsuario = (UsuarioBean) session
					.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();// .getNumreg();
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); // super.getRoles(session);
			log.debug("Roles " + roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles(usrRoles);
			log.debug("Roles " + roles);
			// String codigo = bUsuario.getNumreg();
			// HashMap roles = super.getRoles(session);
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null)
				uoSeg = uoSeg.substring(0, 2).concat("%");

			String criterio = request.getParameter("cmbCriterio");
			String valor = request.getParameter("txtValor");
			String criterio2 = request.getParameter("cmbCriterio2");
			String valor2 = request.getParameter("txtValor2");
			String tipo = request.getParameter("cmbTipo");
			String estado_id = request.getParameter("cmbEstado");
			String valorFechaFin = request.getParameter("txtFechaFin");
			// prac-asanchez
			String tipoReporte = request.getParameter("tipoReporte") != null ? request
					.getParameter("tipoReporte") : "buscar";

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codigo);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("uoAO", bUsuario.getCodUO());// ICR 28/12/2012 NUEVAS
														// UUOOS ANALISTA
														// OPERATIVO
			log.debug("bUsuario.getCodUO(): " + bUsuario.getCodUO());// ICR
																		// 28/12/2012
																		// NUEVAS
																		// UUOOS
																		// ANALISTA
																		// OPERATIVO

			HashMap datos = new HashMap();
			datos.put("dbpool", pool_sp);
			datos.put("tipo", tipo);
			datos.put("criterio", criterio);
			datos.put("valor", valor);
			datos.put("criterio2", criterio2);
			datos.put("valor2", valor2);
			datos.put("estado_id", estado_id);
			datos.put("valorFechaFin", valorFechaFin);
			datos.put("adminSolicitudes", new Boolean(true));

			// ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic.
			// Matrimonio CAS a cuenta
			if (tipo.equals("23")) {
				datos.put("regimen", "1"); // DL 1057 (cas)
			}
			//

			SolicitudDelegate sd = new SolicitudDelegate();
			ArrayList listaSolicitudes = sd.cargarConsultaGeneral(datos,
					seguridad);

			session.removeAttribute("listaSolicitudes");
			session.setAttribute("listaSolicitudes", listaSolicitudes);

			session.setAttribute("ScmbTipo", tipo);
			session.setAttribute("ScmbCriterio2", criterio2);
			session.setAttribute("StxtValor2", valor2);
			session.setAttribute("ScmbEstado", estado_id);
			session.setAttribute("ScmbCriterio", criterio);
			session.setAttribute("StxtValor", valor);
			session.setAttribute("StxtFechaFin", valorFechaFin);

			if (tipoReporte.equals("buscar")) {
				dispatcher = getServletContext().getRequestDispatcher(
						"/consultaAdministracion.jsp");
				dispatcher.forward(request, response);
			}
			return;

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
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}
	}

	/**
	 * Mï¿½todo encargado de cargar la pantalla de bï¿½squeda correspondiente
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void obtener_supervisores(String dbpool, HashMap mapa, String tipo,
			String usuario, HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		BeanMensaje beanMsg = null;
		try {
			UsuarioBean bUsuario = (UsuarioBean) session
					.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();// .getNumreg();

			// busqueda de unid. organ.

			HashMap solicitante = (HashMap) session.getAttribute("solicitante");

			SolicitudDelegate sd = new SolicitudDelegate();
			ArrayList listaSuperv = sd.buscarSupervisores(pool_sp,
					(String) solicitante.get("t02cod_pers"), tipo,
					(String) solicitante.get("t12cod_uorg_jefe"));
			session.removeAttribute("listaSup");
			session.setAttribute("listaSup", listaSuperv);
			log.debug("Arreglo en Obtener Supervisores : " + listaSuperv);
			if (listaSuperv != null && !listaSuperv.isEmpty()) {
				RequestDispatcher dispatcher =
				// getServletContext().getRequestDispatcher("/solSuperv.jsp");
				getServletContext().getRequestDispatcher(
						"/solSuperv.jsp?idSession="
								+ System.currentTimeMillis());
				dispatcher.forward(request, response);
				return;
			} else {

				// SolicitudDelegate sd = new SolicitudDelegate();
				ArrayList mensajes = sd.verificarSolicitud(pool_sp_g, mapa,
						tipo, usuario);

				if (mensajes != null && !mensajes.isEmpty()) {

					beanMsg = new BeanMensaje();
					beanMsg.setListaobjetos(mensajes.toArray());
					beanMsg.setMensajeerror("La solicitud no se ha podido registrar por los siguientes motivos: ");
					beanMsg.setMensajesol("Por favor intente nuevamente.");
					session.setAttribute("beanErr", beanMsg);
					RequestDispatcher dispatcher = getServletContext()
							.getRequestDispatcher("/PagM.jsp");
					dispatcher.forward(request, response);
					return;

				} else {

					ArrayList listaSolicitudes = sd.cargarSolicitudesIniciadas(
							pool_sp, codPers, "-1", "", "", usuario);

					session.removeAttribute("listaSolicitudes");
					session.setAttribute("listaSolicitudes", listaSolicitudes);

					RequestDispatcher dispatcher = getServletContext()
							.getRequestDispatcher("/solicitudesIniciadas.jsp");
					dispatcher.forward(request, response);
					return;
				}
			}

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
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * Mï¿½todo encargado de cargar la pantalla de bï¿½squeda correspondiente
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void registrarSupervisor(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		BeanMensaje beanMsg = null;
		try {

			UsuarioBean bUsuario = (UsuarioBean) session
					.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();// .getNumreg();

			String usuario = bUsuario.getLogin();

			HashMap mapa = (HashMap) session.getAttribute("mapasol");
			String tipo = (String) mapa.get("tipo");
			String supervisor = request.getParameter("registro");
			String jefe = (String) mapa.get("jefedelego");
			if (jefe == null) {
				mapa.put("jefeSup", (String) mapa.get("userDest"));
			} else {
				mapa.put("jefeSup", jefe);
			}
			mapa.put("userDest", supervisor);
			SolicitudDelegate sd = new SolicitudDelegate();

			// SolicitudDelegate sd = new SolicitudDelegate();
			ArrayList mensajes = sd.verificarSolicitud(pool_sp_g, mapa, tipo,
					usuario);

			if (mensajes != null && !mensajes.isEmpty()) {

				beanMsg = new BeanMensaje();
				beanMsg.setListaobjetos(mensajes.toArray());
				beanMsg.setMensajeerror("La solicitud no se ha podido registrar por los siguientes motivos: ");
				beanMsg.setMensajesol("Por favor intente nuevamente.");
				session.setAttribute("beanErr", beanMsg);
				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/PagM.jsp");
				dispatcher.forward(request, response);
				return;

			} else {

				ArrayList listaSolicitudes = sd.cargarSolicitudesIniciadas(
						pool_sp, codPers, "-1", "", "", usuario);

				session.removeAttribute("listaSolicitudes");
				session.setAttribute("listaSolicitudes", listaSolicitudes);

				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/solicitudesIniciadas.jsp");
				dispatcher.forward(request, response);
				return;
			}

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
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/*
	 * Metodo que se encarga de convertir los ROles de HashMap a ArrayList
	 * 
	 * @param roles
	 */
	public static HashMap obtRoles(ArrayList roles)

	throws ServletException, IOException {

		HashMap res = new HashMap();

		if (roles != null && roles.size() > 0) {

			String tmpRol;

			for (int i = 0; i < roles.size(); i++) {

				tmpRol = (String) roles.get(i);
				System.out.println("rol impreso " + tmpRol); //AQY rol impreso
				res.put(tmpRol, tmpRol);

			}

		}

		return res;

	}

	/* FUSION PROGRAMCION */

	/**
	 * Mtodo encargado de cargar la pagina inicial de Anulacion de Vacaciones
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
	private void cargaAnulacion(HttpServletRequest request,
			HttpServletResponse response, HttpSession session,
			UsuarioBean bUsuario) throws ServletException, IOException {
		MensajeBean bean = null;

		// ICAPUNAY - 10-03-2011 CAMBIO PARA QUE TOME COMO periodoInicial EL
		// AÃ‘O ACTUAL Y NO LEA DE constantes.properties
		FechaBean fecha = new FechaBean();
		//

		try {
			Map roles = (HashMap) MenuCliente.getRoles(bUsuario);

			if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
				session.removeAttribute("existePersonal");
				session.removeAttribute("listaVacFisAprob");
				session.removeAttribute("listaProgramacion");
				setAttribute(session, "saldoAnul", "0");
				Map mapaPeriodo = new HashMap();
				mapaPeriodo.put("periodoInicial",
						constantes.leePropiedad("PERIODO_INICIAL"));
				// ICAPUNAY - 10-03-2011 CAMBIO PARA QUE TOME COMO
				// periodoInicial EL AÃ‘O ACTUAL Y NO LEA DE
				// constantes.properties
				// mapaPeriodo.put("periodoFinal",constantes.leePropiedad("PERIODO_FINAL"));
				mapaPeriodo.put("periodoFinal", fecha.getAnho());
				//

				setAttribute(session, "periodo", mapaPeriodo);
				forward(request, response, "/AnulSolVacaciones.jsp");
			} else {
				forward(request, response, "/PagInhabilitado.jsp");
			}

		} catch (DelegateException e) {
			log.error(e, e);
			forwardError(request, response, e.getMensaje());
		} catch (Exception e) {
			log.error(e, e);
			bean = new MensajeBean();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error al cargar la pagina inicial de Anulacion de Vacaciones");
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion");
			forwardError(request, response, bean);
		} finally {
		}
	}

	/**
	 * Mtodo encargado de traer las Vacaciones fisicas y Programadas del
	 * personal ingresado como parametro
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
	private void traerVacFisyProg(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, DynaBean dBean)
			throws ServletException, IOException {
		MensajeBean bean = null;
		try {
			SolicitudDelegate sd = new SolicitudDelegate();
			List listaProgramacion = null;
			List listaVacFisAprob = null;
			session.removeAttribute("listaVacFisAprob");
			session.removeAttribute("listaProgramacion");
			Map mapaResult = sd.traerVacFisyProg(dBean);
			if (mapaResult != null) {
				listaProgramacion = (ArrayList) mapaResult
						.get("listaProgramacion");
				listaVacFisAprob = (ArrayList) mapaResult
						.get("listaVacFisAprob");
				mapaResult.put("cod_periodo", dBean.getString("cod_periodo"));
				session.removeAttribute("listaProgramacion");
				setAttribute(session, "saldoAnul", "0");
				setAttribute(session, "datos", mapaResult);
				setAttribute(session, "existePersonal", "1");
				setAttribute(session, "listaProgramacion", listaProgramacion);
				setAttribute(session, "listaVacFisAprob", listaVacFisAprob);

			} else {
				setAttribute(session, "existePersonal", "0");
				setAttribute(request, "mensaje",
						"No existe el Personal con el codigo de Registro ingresado.");
			}
			forward(request, response, "/AnulSolVacaciones.jsp");

		} catch (DelegateException e) {
			log.error(e, e);
			forwardError(request, response, e.getMensaje());
		} catch (Exception e) {
			log.error(e, e);
			bean = new MensajeBean();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error al traer las Vacaciones fisicas y Programadas del personal");
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion");
			forwardError(request, response, bean);
		} finally {
		}
	}

	/**
	 * Mtodo encargado de agregar la reprogramacion de vacaciones
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
	private void agregarReProgramacion(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, DynaBean params)
			throws ServletException, IOException {
		MensajeBean bean = null;
		try {
			SolicitudDelegate sd = new SolicitudDelegate();
			List listaProgramacion = (ArrayList) session
					.getAttribute("listaProgramacion");
			params.put("listaProgramacion", listaProgramacion);
			String[] chkEliminar = (String[]) request
					.getParameterValues("chkEliminar");
			params.put("chkEliminar", chkEliminar);
			Map mapaResult = sd.agregarReProgramacion(params);
			if (mapaResult != null) {
				listaProgramacion = (ArrayList) mapaResult
						.get("listaProgramacion");
				params.put("numdiasTotal", mapaResult.get("numdiasTotal"));
				setAttribute(request, "existeFecha",
						mapaResult.get("existeFecha").toString());
				setAttribute(session, "listaProgramacion", listaProgramacion);
				setAttribute(request, "flagAgrega", "1");
				setAttribute(request, "mapchkEliminar",
						(HashMap) mapaResult.get("mapaCheck"));
				forward(request, response, "/AnulSolVacaciones.jsp");
			}
		} catch (DelegateException e) {
			log.error(e, e);
			forwardError(request, response, e.getMensaje());
		} catch (Exception e) {
			log.error(e, e);
			bean = new MensajeBean();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error al agregar la reprogramacion de vacaciones");
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion");
			forwardError(request, response, bean);
		} finally {
		}
	}

	/**
	 * Mtodo encargado de grabar la Anulacion de Vacaciones Fisicas
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
	private void grabarAnulVacFis(HttpServletRequest request,
			HttpServletResponse response, HttpSession session,
			UsuarioBean bUsuario, DynaBean params) throws ServletException,
			IOException {
		MensajeBean bean = null;
		try {
			SolicitudDelegate sd = new SolicitudDelegate();
			List listaProgramacion = (ArrayList) session
					.getAttribute("listaProgramacion");
			List listaVacFisAprob = (ArrayList) session
					.getAttribute("listaVacFisAprob");
			String[] arrobserv = (String[]) request
					.getParameterValues("txtobserv");
			String[] chkEliminarVacFis = (String[]) request
					.getParameterValues("chkEliminarVacFis");
			String[] chkEliminar = (String[]) request
					.getParameterValues("chkEliminar");
			params.put("dbpool_sp", pool_sp);
			params.put("cod_usumodif", bUsuario.getLogin());
			params.put("listaProgramacion", listaProgramacion);
			params.put("listaVacFisAprob", listaVacFisAprob);
			params.put("arrobserv", arrobserv);
			params.put("chkEliminarVacFis", chkEliminarVacFis);
			params.put("chkEliminar", chkEliminar);
			sd.grabarAnulVacFis(params);
			setAttribute(session, "saldoAnul",
					params.get("saldoAnul") != null ? params.get("saldoAnul")
							.toString() : "0");
			setAttribute(request, "mensaje",
					"Los cambios realizados fueron grabados exitosamente.");
			forward(request, response, "/AnulSolVacaciones.jsp");
		} catch (DelegateException e) {
			log.error(e, e);
			forwardError(request, response, e.getMensaje());
		} catch (Exception e) {
			log.error(e, e);
			bean = new MensajeBean();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error al grabar la Anulacion de Vacaciones Fisicas");
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion");
			forwardError(request, response, bean);
		} finally {
		}
	}

	/**
	 * Metodo ordenar : encargado de realizar el ordenamiento de acuerdo al
	 * campo especificado<br>
	 * tanto ascendentemente o descendentemente.
	 * 
	 * @param request
	 *            {@link HttpServletRequest}
	 * @param response
	 *            {@link HttpServletResponse}
	 * @param session
	 *            {@link HttpSession}
	 * @throws ServletException
	 * @throws IOException
	 */
	private void ordenarSoliPendientes(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, DynaBean dBean)
			throws ServletException, IOException {
		try {

			String campo = (String) dBean.get("campo");
			List listado = (ArrayList) session.getAttribute("listaRecibidas");

			if ("1".equals(dBean.getString("campo"))) {
				campo = "numSol";
			} else if ("2".equals(dBean.getString("campo"))) {
				campo = "descrip";
			} else if ("3".equals(dBean.getString("campo"))) {
				campo = "fechaSol";
			} else if ("4".equals(dBean.getString("campo"))) {
				campo = "emisor";
			} else if ("5".equals(dBean.getString("campo"))) {
				campo = "asunto";
			} else if ("6".equals(dBean.getString("campo"))) {
				campo = "ultDeriv";
			} else if ("7".equals(dBean.getString("campo"))) {
				campo = "responsable";
			}

			Ordenamiento.sort(listado,
					campo + Ordenamiento.SEPARATOR + dBean.getString("asc"));

			setAttribute(session, "listaRecibidas", listado);
			forward(request, response, "/solicitudesRecibidas.jsp?idSession = "
					+ System.currentTimeMillis());
			session.removeAttribute("inicio");
			setAttribute(session, "inicio", "0");

		} catch (DelegateException e) {
			log.error(e, e);
			forwardError(request, response, e.getMensaje());
		} catch (Exception e) {
			log.error(e, e);
			bean = new MensajeBean();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error al ordenar las solicitudes");
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion");
			forwardError(request, response, bean);
		} finally {
		}

	}

	/**
	 * Metodo procesar solicitudes : metodo encargado de procesar las
	 * solicitudes tanto ascendentemente o descendentemente.
	 * 
	 * @param request
	 *            {@link HttpServletRequest}
	 * @param response
	 *            {@link HttpServletResponse}
	 * @param session
	 *            {@link HttpSession}
	 * @throws ServletException
	 * @throws IOException
	 * @throws ServletException
	 * @throws IOException
	 */
	private void procesarSolicitudes(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, DynaBean dBean,
			UsuarioBean bUsuario) throws ServletException, IOException {

		MensajeBean beanMsg = null;

		String codigo = null;
		List listado = null;// WERR-PAS20155E230300132
		try {
			codigo = bUsuario.getNroRegistro();
			// String codigo = bUsuario.getNroRegistro();
			String login = bUsuario.getLogin();

			SolicitudDelegate sd = new SolicitudDelegate();
			// List listado = (ArrayList)
			// session.getAttribute("listaRecibidas");
			listado = (ArrayList) session.getAttribute("listaRecibidas"); // WERR-PAS20155E230300132
			String acciones[] = request.getParameterValues("cmbAccion");
			String ids[] = request.getParameterValues("id");

			dBean.put("acciones", acciones);
			dBean.put("ids", ids);
			dBean.put("listaRecibidas", listado);
			dBean.put("usuario", login);
			dBean.put("codReg", codigo);
			List respuesta = sd.procesarSolicitudes(dBean);

			if (respuesta != null && !respuesta.isEmpty()) {

				beanMsg = new MensajeBean();
				beanMsg.setListaobjetos(respuesta.toArray());
				beanMsg.setMensajesol("Por favor intente nuevamente.");
				session.setAttribute("beanErr", beanMsg);
				forward(request, response, "/PagM1.jsp");
				return;

			} else {

				MantenimientoDelegate md = new MantenimientoDelegate();
				List tipos = md.buscarMovimientosSolicitud(pool_sp,
						Constantes.ACTIVO);

				session.removeAttribute("tiposSolicitud");
				setAttribute(session, "tiposSolicitud", tipos);
				List listaRecibidas = sd.cargarSolicitudesRecibidas(pool_sp,
						codigo, "-1", "", "");

				session.removeAttribute("listaRecibidas");
				setAttribute(session, "listaRecibidas", listaRecibidas);

				forward(request,
						response,
						"/solicitudesRecibidas.jsp?idSession = "
								+ System.currentTimeMillis());
				session.removeAttribute("inicio");
				setAttribute(session, "inicio", "0");

			}

		} catch (DelegateException e) {
			log.error(e, e);
			forwardError(request, response, e.getMensaje());
		} catch (Exception e) {
			log.error(e, e);
			BeanMensaje bean = new BeanMensaje(); // WERR-PAS20155E230300132
			String error = "";// WERR-PAS20155E230300132
			int indexError;// WERR-PAS20155E230300132
			indexError = e.toString().indexOf(
					"no posee turno entre las fechas que va a compensar");// WERR-PAS20155E230300132
			if (indexError != -1) { // WERR-PAS20155E230300132
				bean.setError(true);// WERR-PAS20155E230300132
				error = e.toString().substring(61);// WERR-PAS20155E230300132
				bean.setMensajeerror(error);// WERR-PAS20155E230300132
				bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion");// WERR-PAS20155E230300132
				session.setAttribute("beanErr", bean);// WERR-PAS20155E230300132
			} else {
				bean.setError(true);
				bean.setMensajeerror(e.getMessage());
				// bean.setMensajeerror("Ha ocurrido un error al procesar las solicitudes");
				bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion");
				session.setAttribute("beanErr", bean);
			}
			forwardError(request, response, bean);
		}

		finally {
		}
	}

	/**
	 * Metodo encargado de cargar los objetos a memoria
	 * 
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargaParametros() throws ServletException, IOException {
		try {
			dsSP = ServiceLocator.getInstance().getDataSource(pool_sp);
			dsRecauda = ServiceLocator.getInstance()
					.getDataSource(pool_recauda);
			dsSig = ServiceLocator.getInstance().getDataSource(pool_sig);
			ParamDAO paramDAO = new ParamDAO();
			paramDAO.cargar("select mov,descrip from t1279tipo_mov ", dsSP,
					"spprm01t1279");
			paramDAO.cargar(new String[] { "V01" }, dsSP, "spprmV01t99",
					ParamDAO.TIPO2);// Estado de Programacion de Vac
			paramDAO.cargar(new String[] { "P01" }, dsSP, "spprmP01t99",
					ParamDAO.TIPO2);// Dias de Vacaciones

			listaFechaDS = (List) paramDAO
					.cargarNoCache(
							"select t99codigo, t99descrip from t99codigos where t99cod_tab='510' and t99codigo='09'",
							dsSP, paramDAO.LIST);// JVV-02/03/2012-DECRETO
													// SUPREMO 004-2006
			// cargamos los parametros necesarios para la validacion de feriados
			paramDAO.cargar(new String[] { "000" }, dsRecauda, "recaudaprm000",
					ParamDAO.TIPO1);
			paramDAO.cargar(new String[] { "400" }, dsRecauda, "recaudaprm400",
					ParamDAO.TIPO1);

			String tipoSalIni = "SELECT CODI_MOVI_TPR,DESC_TIPO_TPR FROM TIPO_SALIDAS WHERE IND_DEVOL in ( ";
			String tipoSalFin = " ) ORDER BY  DESC_TIPO_TPR";

			paramDAO.cargar(tipoSalIni + Constantes.TIPO_SALIDA_ASIS
					+ tipoSalFin, dsSig, "sigTipoSalAsis");
			paramDAO.cargar(tipoSalIni + Constantes.TIPO_SALIDA_LIC_PER
					+ tipoSalFin, dsSig, "sigTipoSalLicPer");
			paramDAO.cargar(tipoSalIni + Constantes.TIPO_SALIDA_SUB
					+ tipoSalFin, dsSig, "sigTipoSalSubs");

			// paramDAO.cargar("select t99codigo, t99descrip from t99codigos where t99cod_tab='R04' and t99tip_desc='D' and t99estado='1'",
			// dsSP, "spprm99motrechazo");
			// paramDAO.cargar("select t99codigo, t99descrip from t99codigos where t99cod_tab='R05' and t99tip_desc='D' and t99estado='1'",
			// dsSP, "spprm99notif");
			paramDAO.cargar(new String[] { "R04" }, dsSP, "spprm99motrechazo",
					ParamDAO.TIPO2);
			paramDAO.cargar(new String[] { "R05" }, dsSP, "spprm99notif",
					ParamDAO.TIPO2);

		} catch (Exception e) {
			log.error("Error", e);
		} finally {
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
	private void listarMarcas(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			HashMap solicitante = (HashMap) session.getAttribute("solicitante");

			String fechaMarca = request.getParameter("fMarca");
			String seleccionar = request.getParameter("seleccionar");

			HashMap datos = new HashMap();
			datos.put("fMarca", fechaMarca);
			datos.put("seleccionar", seleccionar);
			datos.put("codPers", (String) solicitante.get("t02cod_pers"));

			AsistenciaDelegate ad = new AsistenciaDelegate();
			ArrayList lista = null;

			// JVV-02/03/2012-DECRETO SUPREMO 004-2006
			// DataSource dcsp = sl.getDataSource(pool_sp);
			// ParamDAO prmdao = new ParamDAO();
			// List listaFechaDS =
			// (List)prmdao.cargarNoCache("select t99codigo, t99descrip from t99codigos where t99cod_tab='510' and t99codigo='09'",
			// dcsp, prmdao.LIST);
			if (listaFechaDS.size() > 0) {
				ParamBean parmbean = (ParamBean) listaFechaDS.get(0);
				if (log.isDebugEnabled())
					log.debug("t99cod_tab:" + parmbean.getCodigo()
							+ ", t99descrip:" + parmbean.getDescripcion());
				if (log.isDebugEnabled())
					log.debug("fechaMarca:" + fechaMarca);
				FechaBean fecha_ds = new FechaBean(parmbean.getDescripcion()
						.trim());
				FechaBean fecha_marca = new FechaBean(fechaMarca);
				if (log.isDebugEnabled())
					log.debug("fecha_ds:" + fecha_ds.getSQLDate());
				if (log.isDebugEnabled())
					log.debug("fecha_marca:" + fecha_marca.getSQLDate());

				if (fecha_marca.getSQLDate().compareTo(fecha_ds.getSQLDate()) > 0
						|| fecha_marca.getSQLDate().compareTo(
								fecha_ds.getSQLDate()) == 0) {
					if (solicitante != null && !solicitante.isEmpty()) {
						lista = ad.buscarMarcaciones(pool_sp, fechaMarca,
								(String) solicitante.get("t02cod_pers"), null);
					}
				} else
					lista = null;
			}

			// FIN-JVV-02/03/2012-DECRETO SUPREMO 004-2006

			/*
			 * AsistenciaDelegate ad = new AsistenciaDelegate(); ArrayList lista
			 * = null;
			 * 
			 * if (solicitante!=null && !solicitante.isEmpty()){ lista =
			 * ad.buscarMarcaciones( pool_sp, fechaMarca, (String)
			 * solicitante.get("t02cod_pers"), null); }
			 */

			session.removeAttribute("datosMarcas");
			session.setAttribute("datosMarcas", datos);

			session.removeAttribute("listaMarcas");
			session.setAttribute("listaMarcas", lista);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/listaMarcaciones.jsp");
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
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}
	}

	/*      */
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
			String txtfechaMarca2 = request.getParameter("fechaMarca2") != null ? request
					.getParameter("fechaMarca2") : "";
			String txtHoraIni = request.getParameter("txtHoraIni") != null ? request
					.getParameter("txtHoraIni") : "";
			String txtHoraFin = request.getParameter("txtHoraFin") != null ? request
					.getParameter("txtHoraFin") : "";
			String txtObs2 = request.getParameter("txtObs2") != null ? request
					.getParameter("txtObs2") : "";

			HashMap datos = new HashMap();
			datos.put("fechaMarca2", txtfechaMarca2);
			datos.put("txtHoraIni", txtHoraIni);
			datos.put("txtHoraFin", txtHoraFin);
			datos.put("txtObs2", txtObs2);
			ArrayList lista = new ArrayList();
			lista = (ArrayList) session.getAttribute("listaAprobadores");
			lista.add(datos);
			session.removeAttribute("listaAprobadores");
			session.setAttribute("listaAprobadores", lista);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/solicitudLaborExcepcional.jsp");
			dispatcher.forward(request, response);
			return;

		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
					+ e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
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

			ArrayList lista = (ArrayList) session
					.getAttribute("listaAprobadores");
			String[] params = request.getParameterValues("chk_opcion");

			int id;

			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					id = Integer.parseInt(params[i]) - i;
					lista.remove(id);
				}
				session.removeAttribute("listaAprobadores");
				session.setAttribute("listaAprobadores", lista);

			}
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/solicitudLaborExcepcional.jsp");
			dispatcher.forward(request, response);
			return;
		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
					+ e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	// PAS20171U230200001 - solicitud de reintegro
	// PAS20181U230200067 - solicitud de reintegro -planillas adicionales
	/**
	 * Metodo encargado de cargar los conceptos de reintegro
	 * 
	 * @param request
	 *            :reciben como parámetros los datos de la petición.
	 * @param response
	 *            : el flujo de la salida de respuesta.
	 * @param session
	 *            :recibe como parámetros la sesión que acceso al sistema
	 * @throws ServletException
	 * @throws IOException
	 */
	private void listarConceptoReintegro(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			HashMap solicitante = (HashMap) session.getAttribute("solicitante");
			String tipo = (String) request.getParameter("tipo");
			String anioPeriodo = (String) request.getParameter("anioPeriodo");
			String mesPeriodo = (String) request.getParameter("mesPeriodo");
			String tipoPlanilla = (String) request.getParameter("tipoPlanilla");
			String subtipoPlanilla = (String) request.getParameter("subtipoPlanilla");
			
			Integer numArchivo = new Integer(request.getParameter("numArchivo"));
			String codPers = (String) solicitante.get("t02cod_pers");

			log.debug("solicitante  " + solicitante);
			log.debug("anioPeriodo  " + anioPeriodo);
			log.debug("mesPeriodo  " + mesPeriodo);
			log.debug("tipoPlanilla  " + tipoPlanilla);
			log.debug("subtipoPlanilla  " + subtipoPlanilla);

			SolicitudDelegate sd = new SolicitudDelegate();
			Map dataTrabajador = sd.obtenerDataTrabajador(pool_sig, codPers); // obtener
																				// Regimen
																				// Laboral
			String regLab = (String) dataTrabajador.get("reg_lab_per");
			String tipoPlan = (String) dataTrabajador.get("tipo_plan_tpl");
			String codiEmplPer = (String) dataTrabajador.get("codi_empl_per");

			Map filtro = new HashMap();
			filtro.put("reg_lab_per", regLab);
			filtro.put("tipo_plan_tpl", tipoPlan);
			filtro.put("codi_empl_per", codiEmplPer);
			filtro.put("cod_pers", codPers);
			filtro.put("anio", anioPeriodo);
			filtro.put("mes", mesPeriodo);
			filtro.put("tipoPlanilla", tipoPlanilla);
			filtro.put("subtipoPlanilla", subtipoPlanilla);
			filtro.put("tipo", tipo);

/*          AGONZALESF -PAS20191U230200011 - se deja de usar , solo se obtendran las planillas validas 
			log.debug("Verificar si trabajador existe en planilla cas/haberes ");
			boolean existePlanilla = sd.verificarExistePlanilla(filtro);
			if (!existePlanilla) {
				log.debug("EX091: Información no se encuentra registrada, por favor coordinar con la división de compensaciones de la INRH");
				BeanMensaje bean = new BeanMensaje();
				bean.setError(true);
				bean.setMensajeerror("Información no se encuentra registrada, por favor coordinar con la división de compensaciones de la INRH");
				session.setAttribute("beanErr", bean);
				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/PagE.jsp");
				dispatcher.forward(request, response);
				return;
			}

			log.debug("Verificar que no sea reingreso ");
			boolean esReIngreso = sd.verificarReIngreso(filtro);
			if (esReIngreso) {
				log.debug("EX088:Usted no puede solicitar devoluciones de periodos cesados, por favor coordinar con la división de compensaciones de la INRH");
				BeanMensaje bean = new BeanMensaje();
				bean.setError(true);
				bean.setMensajeerror("Usted no puede solicitar devoluciones de periodos cesados, por favor coordinar con la división de compensaciones de la INRH");
				session.setAttribute("beanErr", bean);
				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/PagE.jsp");
				dispatcher.forward(request, response);
				return;
			}

			log.debug("Verificar regimen de la planilla solicitada  es igual a regimen actual");
			boolean esRegimenValido = sd.verificarRegimenLaboral(filtro);
			if (!esRegimenValido) {
				log.debug("EX087:Debido a su cambio de régimen laboral usted no puede solicitar devoluciones , por favor coordinar con la división de compensaciones de la INRH ");
				BeanMensaje bean = new BeanMensaje();
				bean.setError(true);
				bean.setMensajeerror("Debido a su cambio de régimen laboral usted no puede solicitar devoluciones , por favor coordinar con la división de compensaciones de la INRH");
				session.setAttribute("beanErr", bean);
				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/PagE.jsp");
				dispatcher.forward(request, response);
				return;
			}

			//se verifica en carga de solicitud
			log.debug("Verificar regimende la planilla este configurado ");
			boolean esRegimenConfigurado = sd
					.verificarRegimenConfigurado(filtro);
			if (!esRegimenConfigurado) {
				log.debug("EX087:Debido a su régimen laboral usted no puede solicitar devoluciones , por favor coordinar con la división de compensaciones de la INRH");
				BeanMensaje bean = new BeanMensaje();
				bean.setError(true);
				bean.setMensajeerror("Debido a su régimen laboral usted no puede solicitar devoluciones , por favor coordinar con la división de compensaciones de la INRH");
				session.setAttribute("beanErr", bean);
				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/PagE.jsp");
				dispatcher.forward(request, response);
				return;
			}
			*/ 

			log.debug("Traer conceptos por periodo ->" + filtro);
			List conceptos = new ArrayList();
			conceptos = sd.obtenerConceptos(pool_sp, pool_sig, filtro, tipo);
			log.debug("conceptos" + conceptos);

			BeanSolReintegro sol = new BeanSolReintegro();
			sol.setAnnSolplan(anioPeriodo);
			sol.setMesSolplan(mesPeriodo);
			sol.setConceptos(conceptos);
			sol.setCodTipSol(tipo);
			sol.setCodPlanorig(tipoPlanilla);
			sol.setCodSplanorig(subtipoPlanilla);
			sol.setNumArchivo(numArchivo);

			session.setAttribute("solReintegro", sol);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/listaConceptoReintegro.jsp");
			dispatcher.forward(request, response);

		} catch (Exception e) {
			log.error("**Error**", e);
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
					+ e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * Obtiene listado de detalles
	 * 
	 * @param request
	 *            :reciben como parámetros los datos de la petición.
	 * @param response
	 *            : el flujo de la salida de respuesta.
	 * @param session
	 *            :recibe como parámetros la sesión que acceso al sistema
	 * @throws ServletException
	 * @throws IOException
	 */
	private void listarDetalleConceptoReintegro(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		try {

			HashMap solicitante = (HashMap) session.getAttribute("solicitante");
			BeanSolReintegro solicitud = (BeanSolReintegro) session
					.getAttribute("solReintegro");
			String anioPeriodo = (String) request.getParameter("anioPeriodo");
			String mesPeriodo = (String) request.getParameter("mesPeriodo");
			String tipoPlanilla = (String) request.getParameter("tipoPlanilla");
			String subtipoPlanilla = (String) request.getParameter("subtipoPlanilla");			
			String movimiento = (String) request.getParameter("movimiento");// movimiento
			String concepto = (String) request.getParameter("concepto");// movimiento
			String descMovimiento = (String) request
					.getParameter("descMovimiento");// movimiento
			String tipoDetalle = (String) request.getParameter("tipoDetalle");// tipo
																				// ,
																				// asistencia
																				// o
																				// licencia
			String tipo = solicitud.getCodTipSol();

			String codPers = (String) solicitante.get("t02cod_pers");

			SolicitudDelegate sd = new SolicitudDelegate();
			Map dataTrabajador = sd.obtenerDataTrabajador(pool_sig, codPers); // obtener
																				// Regimen
																				// Laboral
			log.debug("*** dataTrabajador  ***" + dataTrabajador);
			String regLab = (String) dataTrabajador.get("reg_lab_per");
			String tipoPlan = (String) dataTrabajador.get("tipo_plan_tpl");
			String codiEmplPer = (String) dataTrabajador.get("codi_empl_per");
			String colaborador = (String) dataTrabajador.get("ape_pat_per")
					+ " " + (String) dataTrabajador.get("ape_mat_per") + " "
					+ (String) dataTrabajador.get("nom_emp_per");

			// traer conceptos por periodo por movimiento
			Map filtro = new HashMap();
			filtro.put("reg_lab_per", regLab);
			filtro.put("tipo_plan_tpl", tipoPlan);
			filtro.put("codi_empl_per", codiEmplPer);
			filtro.put("anio", anioPeriodo);
			filtro.put("mes", mesPeriodo);
			filtro.put("movimiento", movimiento);
			filtro.put("concepto", concepto);
			filtro.put("cod_pers", codPers);
			filtro.put("tipo", tipo);
			filtro.put("tipoPlanilla", tipoPlanilla);
			filtro.put("subtipoPlanilla", subtipoPlanilla);

			List detalles = new ArrayList();
			List fechasConMovimientos = new ArrayList();
			if (tipoDetalle.equals("asistencia")) {
				log.debug("Traer detalle por concepto asistencia ->" + filtro);
				detalles = sd.obtenerDetalleConceptoAsistencia(pool_sig,
						pool_sp, filtro);
				fechasConMovimientos = sd.buscarFechaConMovimiento(filtro);
			}
			if (tipoDetalle.equals("licencia")) {
				log.debug("Traer detalle por concepto licencia ->" + filtro);
				detalles = sd.obtenerDetalleConceptoLicencia(pool_sig, pool_sp,
						filtro);
				fechasConMovimientos = new ArrayList();
			}

			log.debug("Obtener datos ya modificados anteriormente y fusionarlos con los traidos de base");
			for (int i = 0; i < detalles.size(); i++) {
				BeanSolReinDetalle detalle = (BeanSolReinDetalle) detalles
						.get(i);
				String id = "";
				if (tipoDetalle.equals("asistencia")) {
					id = detalle.getCodConc() + "." + detalle.getCodMov() + "."
							+ detalle.getFecO();
				}
				if (tipoDetalle.equals("licencia")) {
					id = detalle.getCodConc() + "." + detalle.getCodMov() + "."
							+ detalle.getCodLi();
				}

				log.debug("Id a evaluar ->" + id);
				if (request.getParameter(id + ".solic") != null
						&& request.getParameter(id + ".solic") != "") {
					((BeanSolReinDetalle) detalles.get(i))
							.setSolic(new Integer(request.getParameter(id
									+ ".solic")));
				}
				if (request.getParameter(id + ".tipDev") != null
						&& request.getParameter(id + ".tipDev") != "") {
					((BeanSolReinDetalle) detalles.get(i)).setTipDev(request
							.getParameter(id + ".tipDev"));
				}
			}

			List tdevoluciones = new ArrayList();
			log.debug("Tipo " + solicitud.getCodTipSol());
			if (solicitud.getCodTipSol().equals(
					Constantes.MOV_REINTEGRO_POR_DESCUENTO_ASISTENCIA)) {
				if (tipoDetalle.equals("asistencia")) {
					log.debug("Traer detalles de asistencia");
					tdevoluciones = sd.listaTipoDevoluciones(pool_sig, "");
				}
				if (tipoDetalle.equals("licencia")) {
					log.debug("Traer detalles de asistencia - licencias y permisos");
					tdevoluciones = sd.listaTipoDevoluciones(pool_sig, "L");
				}
			}
			if (solicitud.getCodTipSol().equals(
					Constantes.MOV_REINTEGRO_POR_DESCUENTO_SUBSIDIO)) {
				// tipoDetalle debe ser licencia
				log.debug("Traer detalles subsidios");
				tdevoluciones = sd.listaTipoDevoluciones(pool_sig, "S");
			}

			session.setAttribute("tipDevoluciones", tdevoluciones);
			session.setAttribute("detalles", detalles);
			session.setAttribute("descMovimiento", descMovimiento);
			session.setAttribute("identificador", concepto + "." + movimiento);
			session.setAttribute("colaborador", colaborador);
			session.setAttribute("anioPeriodo", anioPeriodo);
			session.setAttribute("mesPeriodo", mesPeriodo);
			session.setAttribute("tipoDetalle", tipoDetalle);
			session.setAttribute("fechasConMovimientos", fechasConMovimientos);

			forward(request,
					response,
					"/listaDetalleConceptoReintegro.jsp?idSession="
							+ System.currentTimeMillis());

		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
					+ e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * Metodo para excluir concepto
	 * 
	 * @param request
	 *            :reciben como parámetros los datos de la petición.
	 * @param response
	 *            : el flujo de la salida de respuesta.
	 * @param session
	 *            :recibe como parámetros la sesión que acceso al sistema
	 * @throws ServletException
	 * @throws IOException
	 */
	private void excluirConcepto(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		SolicitudDelegate sd = new SolicitudDelegate();
		try {
			HashMap mapa = new HashMap();
			// request ->mapa datos que usare para filtrar el concepto que se
			// rechaza
			try {
				UsuarioBean bUsuario = (UsuarioBean) session
						.getAttribute("usuarioBean");
				Map solicitud = (HashMap) session
						.getAttribute("solicitudRecibida");
				Map solRein = (HashMap) solicitud.get("solRein");
				List lstConceptos = (List) solRein.get("conceptos");
				HashMap concepto = (HashMap) lstConceptos.get(Integer.valueOf(
						request.getParameter("indice")).intValue());

				concepto.put("cod_tipobs",
						request.getParameter("codMotRechazo"));
				concepto.put("cod_usumodif", bUsuario.getNroRegistro());
				List mensajes = sd.marcarExcluirConcSolReintegro(pool_sp_g,
						concepto);

				setAttribute(session, "solicitudRecibida", solicitud);

			} catch (Exception e) {
				log.error(e.getMessage());
			}

			// Despues de excluir se debe cargar nuevamente para que se cierre
			// la ventana con javascript (en teoria)
			forward(request, response, "/excluirConcepto.jsp?idSession="
					+ System.currentTimeMillis());

		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
					+ e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}
	}

	/**
	 * Metodo para notificar solicitud de reintegro en caso que no exista
	 * planilla para la devolucion
	 * 
	 * @param request
	 *            :reciben como parámetros los datos de la petición.
	 * @param response
	 *            : el flujo de la salida de respuesta.
	 * @param session
	 *            :recibe como parámetros la sesión que acceso al sistema
	 * @throws IOException
	 * @throws ServletException
	 */
	private void notificarSolReintegro(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			SolicitudDelegate sd = new SolicitudDelegate();
			HashMap solicitud = (HashMap) session
					.getAttribute("solicitudRecibida");
			sd.notificarSolReintegro(solicitud);
			forward(request, response, "/DerivarSolicitudFinal.jsp");

		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
					+ e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}
	}

	/**
	 * Metodo para ver el listado de documentos aprobados
	 * 
	 * @param request
	 *            :reciben como parámetros los datos de la petición.
	 * @param response
	 *            : el flujo de la salida de respuesta.
	 * @param session
	 *            :recibe como parámetros la sesión que acceso al sistema
	 * @throws IOException
	 * @throws ServletException
	 */
	private void verDocumentosAprobados(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		SolicitudDelegate sd = new SolicitudDelegate();
		try {
			HashMap filtro = new HashMap();
			String nroSolicitud = (String) request.getParameter("nro");
			filtro.put("num_seqrein", nroSolicitud);

			Map documentos = sd.listarDocumentosAprobados(filtro);
			log.debug("document" + documentos);
			session.setAttribute("documentos", documentos);

			Map solicitud = (Map) documentos.get("solicitud");
			String numRegistroSolicitante = solicitud.get("cod_pers")
					.toString();
			String anno = solicitud.get("ann_solplan").toString();
			String mes = solicitud.get("mes_solplan").toString();

			UtilesDelegate ud = new UtilesDelegate();
			HashMap solicitante = ud.buscarTrabajador(pool_sp,
					numRegistroSolicitante, null);
			String colaborador = (String) solicitante.get("t02ap_pate") + " "
					+ (String) solicitante.get("t02ap_mate") + " "
					+ (String) solicitante.get("t02nombres");

			session.setAttribute("colaborador", colaborador);
			session.setAttribute("periodo", anno + mes);

			forward(request, response, "/visualizarDocAprobados.jsp");

		} catch (Exception e) {
			e.printStackTrace();
			log.debug("error" + e);
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
					+ e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * Funcion auxiliar para procesa los datos del formulario en
	 * BeanSolReinConcepto
	 * 
	 * @param request
	 *            :reciben como parámetros los datos de la petición.
	 * @return Lista de conceptos (BeanSolReinConcepto)
	 */
	private List procesarConceptos(HttpServletRequest request) {

		String[] conceptos = (String[]) request.getParameterValues("idC");
		Map mapConceptos = new HashMap();
		if (conceptos != null && conceptos.length > 0) {
			for (int i = 0; i < conceptos.length; i++) {
				log.debug("concepto a procesar :" + conceptos[i]);
				BeanSolReinConcepto concepto = obtenerConcepto(request,
						conceptos[i]);
				mapConceptos.put(
						concepto.getCodConc() + "." + concepto.getCodMov(),
						concepto);
			}
		}

		String[] detalles = (String[]) request.getParameterValues("idD");
		if (detalles != null && detalles.length > 0) {
			for (int i = 0; i < detalles.length; i++) {
				log.debug("detalle a procesar :" + detalles[i]);
				;
				BeanSolReinDetalle detalle = obtenerDetalle(request,
						detalles[i]);
				String concepto = detalle.getCodConc() + "."
						+ detalle.getCodMov();
				if (mapConceptos.containsKey(concepto)) {
					((BeanSolReinConcepto) mapConceptos.get(concepto))
							.getDetalles().add(detalle);
				}
			}
		}

		return new ArrayList(mapConceptos.values());
	}

	/**
	 * Funcion auxiliar para procesa los datos del formulario en
	 * BeanSolReinConcepto
	 * 
	 * @param request
	 *            :reciben como parámetros los datos de la petición.
	 * @param id
	 *            de concepto
	 * @return BeanSolReinConcepto
	 */
	public BeanSolReinConcepto obtenerConcepto(HttpServletRequest request,
			String id) {

		BeanSolReinConcepto concepto = new BeanSolReinConcepto();

		if (request.getParameter(id + ".numSeq") != null) {
			concepto.setNumSeq(new Integer(request.getParameter(id + ".numSeq")));
		}
		if (request.getParameter(id + ".codTipLi") != null) {
			concepto.setCodTipLi((String) request
					.getParameter(id + ".codTipLi"));
		}
		if (request.getParameter(id + ".codConc") != null) {
			concepto.setCodConc((String) request.getParameter(id + ".codConc"));
		}
		if (request.getParameter(id + ".conDesc") != null) {
			concepto.setConDesc((String) request.getParameter(id + ".conDesc"));
		}
		if (request.getParameter(id + ".codMov") != null) {
			concepto.setCodMov((String) request.getParameter(id + ".codMov"));
		}
		if (request.getParameter(id + ".desMov") != null) {
			concepto.setDesMov((String) request.getParameter(id + ".desMov"));
		}
		if (request.getParameter(id + ".desMot") != null) {
			concepto.setDesMot((String) request.getParameter(id + ".desMot"));
		}
		if (request.getParameter(id + ".dscto") != null) {
			concepto.setDscto(new Integer(request.getParameter(id + ".dscto")));
		}
		if (request.getParameter(id + ".solic") != null) {
			concepto.setSolic(new Integer(request.getParameter(id + ".solic")));
		}

		return concepto;
	}

	/**
	 * Funcion auxiliar para procesa los datos del formulario en
	 * BeanSolReinDetalle
	 * 
	 * @param request
	 *            :reciben como parámetros los datos de la petición.
	 * @param id
	 *            de concepto
	 * @return eanSolReinDetalle
	 */
	public BeanSolReinDetalle obtenerDetalle(HttpServletRequest request,
			String id) {

		BeanSolReinDetalle detalle = new BeanSolReinDetalle();

		if (request.getParameter(id + ".codLi") != null) {
			detalle.setCodLi(new BigDecimal(request.getParameter(id + ".codLi")));
		}
		if (request.getParameter(id + ".fecO") != null) {
			detalle.setFecO((String) request.getParameter(id + ".fecO"));
		}

		if (request.getParameter(id + ".codMov") != null) {
			detalle.setCodMov((String) request.getParameter(id + ".codMov"));
		}
		if (request.getParameter(id + ".codConc") != null) {
			detalle.setCodConc((String) request.getParameter(id + ".codConc"));
		}
		if (request.getParameter(id + ".dscto") != null) {
			detalle.setDscto(new Integer(request.getParameter(id + ".dscto")));
		}
		if (request.getParameter(id + ".solic") != null) {
			detalle.setSolic(new Integer(request.getParameter(id + ".solic")));
		}
		if (request.getParameter(id + ".tipDev") != null) {
			detalle.setTipDev(request.getParameter(id + ".tipDev"));
		}

		if (request.getParameter(id + ".tipDevDesc") != null) {
			detalle.setTipDevDesc(request.getParameter(id + ".tipDevDesc"));
		}

		if (request.getParameter(id + ".tipLic") != null) {
			detalle.setTipLic(request.getParameter(id + ".tipLic"));
		}
		return detalle;
	}

	// RMONTES Inicio
	/**
	 * Carga la pantalla de detalle de solicitudes de reintegro para el
	 * aprobador
	 * 
	 * @param request
	 *            :reciben como parámetros los datos de la petición.
	 * @param response
	 *            : el flujo de la salida de respuesta.
	 * @param session
	 *            :recibe como parámetros la sesión que acceso al sistema
	 * @param dBean
	 *            : data procesada en Dynabean
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarDetSolReintegro(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, DynaBean dBean)
			throws ServletException, IOException {
		SolicitudDelegate sd = new SolicitudDelegate();
		try {
			HashMap mapa = new HashMap();
			// List mensajes = sd.procesarDetalleConcSolReintegro(mapa);
			Map solicitud = (HashMap) session.getAttribute("solicitudRecibida");
			Map solRein = (HashMap) solicitud.get("solRein");
			List lstConceptos = (List) solRein.get("conceptos");
			String pag = "";
			Map concepto = (HashMap) lstConceptos.get(dBean .getInt("indDetalle"));

			List lstDetSolReintegro = null;
			// PAS20171U230200033 agonzalesf - carga desde lo guardado
			// if (concepto.get("lstDetSolReintegro") != null) {
			// lstDetSolReintegro = (List) concepto.get("lstDetSolReintegro");
			// } else {
			lstDetSolReintegro = sd.cargarDetSolReintegro(concepto);

			// PAS20171U230200033 -agonzalesf - Solo debe cargarse la data de
			// devolucion,monto unitario en caso de derivacion de rrhh
			if ("0".equals(dBean.getString("aprobFinal"))) {
				concepto.put("lstDetSolReintegro", lstDetSolReintegro);
			} else {
				lstDetSolReintegro = sd.cargarDatosDevolucion(solRein,
						concepto, lstDetSolReintegro);
				BigDecimal montoUnitario = sd.cargarMontoUnitario(solRein,
						concepto);
				concepto.put("mtoUnitario", montoUnitario);
				String codTipLicencia = Utiles.esNuloesVacio(concepto.get(
						"cod_tiplicencia").toString()) ? "" : concepto.get(
						"cod_tiplicencia").toString(); // <blanco>, L,P,S
				if (codTipLicencia.equals("")) {
					concepto.put("mtoUnitarioDesc",
							montoUnitario.setScale(4, BigDecimal.ROUND_HALF_UP));
				} else {
					concepto.put("mtoUnitarioDesc",
							montoUnitario.setScale(2, BigDecimal.ROUND_HALF_UP));
				}
				concepto.put("lstDetSolReintegro", lstDetSolReintegro);
			}

			// }

			setAttribute(request, "indSolicitud",
					dBean.getString("indSolicitud"));
			setAttribute(request, "indDetalle", dBean.getString("indDetalle"));
			setAttribute(request, "tipo", dBean.getString("tipo"));
			setAttribute(request, "aprobFinal", dBean.getString("aprobFinal"));
			setAttribute(request, "tipLicencia",
					(String) concepto.get("cod_tiplicencia"));
			setAttribute(request, "codMovimiento",
					(String) concepto.get("cod_movimiento"));
			setAttribute(request, "desMovimiento",
					(String) concepto.get("des_movimiento"));
			setAttribute(request, "lstDetSolReintegro", lstDetSolReintegro);
			setAttribute(session, "solicitudRecibida", solicitud);

			if ("0".equals(dBean.getString("aprobFinal"))) {
				setAttribute(
						request,
						"periodo",
						(String) solRein.get("ann_solplan")
								+ (String) solRein.get("mes_solplan"));
				pag = "/detSolReintegro.jsp";
			} else {
				setAttribute(request, "periodo",
						(String) solRein.get("ann_solplan") + " - "
								+ (String) solRein.get("mes_solplan_desc"));
				setAttribute(request, "solRein", solRein);
				setAttribute(request, "concepto", concepto);
				setAttribute(
						request,
						"numArchivo",
						solRein.get("num_archivo") != null ? solRein.get(
								"num_archivo").toString() : "");

				HashMap filtro = new HashMap();
				String nroSolicitud = (String) request.getParameter("nro");
				filtro.put("num_seqrein", solRein.get("num_seqrein"));
				Map documentos = sd.listarDocumentosAprobados(filtro);
				log.debug("document" + documentos);
				setAttribute(request, "documentos", documentos);

				pag = "/updDetSolReintegro.jsp";
			}
			forward(request, response, pag);

		} catch (Exception e) {
			log.error("error"+ e);
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. " + e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * Actualiza el detalle de la solicitud de reintegro de acuerdo a lo
	 * realizado por el aprobador
	 * 
	 * @param request
	 *            :reciben como parámetros los datos de la petición.
	 * @param response
	 *            : el flujo de la salida de respuesta.
	 * @param session
	 *            :recibe como parámetros la sesión que acceso al sistema
	 * @throws ServletException
	 * @throws IOException
	 */
	private void actualizarDetSolReintegro(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		SolicitudDelegate sd = new SolicitudDelegate();
		try {
			String indDetalle = request.getParameter("indDetalle");

			Map solicitud = (HashMap) session.getAttribute("solicitudRecibida");
			Map solRein = (HashMap) solicitud.get("solRein");
			List lstConceptos = (List) solRein.get("conceptos");
			Map concepto = (HashMap) lstConceptos.get(Integer
					.parseInt(indDetalle));
			List lstDetSolReintegro = (List) concepto.get("lstDetSolReintegro");

			String tSol = ((String) solicitud.get("tipo")).trim();
			String tipo = request.getParameter("tipo").trim();
			if (tSol.equals(Constantes.MOV_REINTEGRO_POR_DESCUENTO_ASISTENCIA)
					|| tSol.equals(Constantes.MOV_REINTEGRO_POR_DESCUENTO_SUBSIDIO)) {
				String[] hdnIndDet;
				try {
					hdnIndDet = request.getParameterValues("hdnIndDet");
				} catch (Exception e) {
					hdnIndDet = new String[] { request
							.getParameter("hdnIndDet") };
				}

				String[] hdnAprob;
				try {
					hdnAprob = request.getParameterValues("hdnAprob");
				} catch (Exception e) {
					hdnAprob = new String[] { request.getParameter("hdnAprob") };
				}

				String[] codTipDevol;
				try {
					codTipDevol = request.getParameterValues("codTipDevol");
				} catch (Exception e) {
					codTipDevol = new String[] { request
							.getParameter("codTipDevol") };
				}

				String[] txtObservacion;
				try {
					txtObservacion = request
							.getParameterValues("txtObservacion");
				} catch (Exception e) {
					txtObservacion = new String[] { request
							.getParameter("txtObservacion") };
				}

				if (lstDetSolReintegro != null && lstDetSolReintegro.size() > 0) {
					int indDet;
					int totAprob = 0;
					for (int i = 0; i < hdnIndDet.length; i++) {
						indDet = Integer.valueOf(hdnIndDet[i]).intValue();

						Map deta = (Map) lstDetSolReintegro.get(indDet);
						// int valAprob =
						// Integer.valueOf(tipo.equals("m")?(deta.get("cnt_mindev")!=null?(String)deta.get("cnt_mindev"):"0"):(deta.get("cnt_diadev")!=null?(String)deta.get("cnt_diadev"):"0"));
						int valSolic = Integer
								.valueOf(
										tipo.equals("m") ? (deta
												.get("cnt_minsol") != null ? deta
												.get("cnt_minsol").toString()
												: "0")
												: (deta.get("cnt_diasol") != null ? deta
														.get("cnt_diasol")
														.toString() : "0"))
								.intValue();
						int valSaldo = Integer
								.valueOf(
										tipo.equals("m") ? (deta
												.get("cnt_minsaldo") != null ? deta
												.get("cnt_minsaldo").toString()
												: "0")
												: (deta.get("cnt_diasaldo") != null ? deta
														.get("cnt_diasaldo")
														.toString() : "0"))
								.intValue();

						// if (valSolic != Integer.valueOf(hdnAprob[indDet])){
						if (tipo.equals("m")) {
							deta.put("cnt_mindev", hdnAprob[indDet]);
							deta.put("cnt_minsol", hdnAprob[indDet]);
							deta.put("cnt_minsaldo", new Integer(valSaldo
									- (Integer.valueOf(hdnAprob[indDet])
											.intValue() - valSolic)));
						} else {
							deta.put("cnt_diadev", hdnAprob[indDet]);
							deta.put("cnt_diasol", hdnAprob[indDet]);
							deta.put("cnt_diasaldo", new Integer(valSaldo
									- (Integer.valueOf(hdnAprob[indDet])
											.intValue() - valSolic)));
						}
						// }
						totAprob += new Integer(hdnAprob[indDet]).intValue();

						if (!codTipDevol[indDet].trim().equals(
								deta.get("cod_tipdev").toString().trim())) {
							deta.put("cod_tipdev", codTipDevol[indDet]);
						}

						if (!"".equals(txtObservacion[indDet].trim())) {
							deta.put("des_observa", txtObservacion[indDet]);
						}
					}
					if (tipo.equals("m")) {
						concepto.put("cnt_minapro", new Integer(totAprob));
					} else {
						concepto.put("cnt_diaapro", new Integer(totAprob));

					}
					// PAS20171U230200033 -agonzalesf
					// Se agrega funcion para guardar los cambios del detalle
					boolean ok = sd.actualizarDetSolReintegro(concepto);
					if (!ok) {
						BeanMensaje bean = new BeanMensaje();
						bean.setError(true);
						bean.setMensajeerror("Ha ocurrido un error al  guardar detalle de la solicitud  ");
						bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
						session.setAttribute("beanErr", bean);
						RequestDispatcher dispatcher = getServletContext()
								.getRequestDispatcher("/PagE.jsp");
						dispatcher.forward(request, response);
					}
					session.setAttribute("solicitudRecibida", solicitud);
					List listaRecibidas = (ArrayList) session
							.getAttribute("listaRecibidas");
					// PAS20171U230200033 -agonzalesf , se trae la informacion
					// guardada no requiere setear en sesion
					// Map sol = (HashMap) listaRecibidas.get(new
					// Integer(request.getParameter("indSolicitud")).intValue());
					// sol.put("solRein", solRein);
					session.setAttribute("listaRecibidas", listaRecibidas);
				}
			}

			cargarDerivarSolicitud(request, response, session);

		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
					+ e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * Carga la pantalla de detalle de la exclusión de conceptos
	 * 
	 * @param request
	 *            :reciben como parámetros los datos de la petición.
	 * @param response
	 *            : el flujo de la salida de respuesta.
	 * @param session
	 *            :recibe como parámetros la sesión que acceso al sistema
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarExcluirConcepto(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		SolicitudDelegate sd = new SolicitudDelegate();
		try {
			Map solicitud = (HashMap) session.getAttribute("solicitudRecibida");
			Map solRein = (HashMap) solicitud.get("solRein");
			List lstConceptos = (List) solRein.get("conceptos");
			Map concepto = (HashMap) lstConceptos.get(Integer.parseInt(request
					.getParameter("indice")));

			setAttribute(request, "codConcepto", concepto.get("cod_concepto"));
			setAttribute(request, "desConcepto",
					(String) concepto.get("des_concepto"));
			setAttribute(request, "indice", request.getParameter("indice"));
			forward(request, response, "/excluirConcepto.jsp");

		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
					+ e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	// RMONTEs Fin

	// fin-PAS20171U230200001 - solicitud de reintegro

}