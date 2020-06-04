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
import javax.swing.text.StyledEditorKit.BoldAction;

import org.apache.log4j.Logger;
import org.apache.log4j.NDC;

import pe.gob.sunat.framework.core.servlet.ServletAbstract;
import pe.gob.sunat.framework.util.lang.Ordenamiento;
import pe.gob.sunat.sol.BeanMensaje;
import pe.gob.sunat.sp.asistencia.bean.BeanCompensaOnom;
import pe.gob.sunat.sp.asistencia.bean.BeanTipoMovimiento;
import pe.gob.sunat.sp.asistencia.ejb.delegate.ArchivoDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.LicenciaDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.LicenciaException;
import pe.gob.sunat.sp.asistencia.ejb.delegate.MantenimientoDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.MantenimientoException;
import pe.gob.sunat.sp.asistencia.ejb.delegate.UtilesDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.UtilesException;
import pe.gob.sunat.sp.dao.T99DAO;
import pe.gob.sunat.tecnologia.menu.bean.UsuarioBean;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;

/**
 * @web.servlet name="ServletLicencia"
 * @web.servlet-mapping url-pattern = "/asisS05Alias"
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
public class ServletLicencia extends ServletAbstract {

	private static final Logger log = Logger.getLogger(ServletLicencia.class);
	private static String pool_sp;
	private static String pool_sp_g;
	private static String MAX_SIZE_FILE_STRING = "512kb";

	public ServletLicencia() {
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

		if (accion.equals("cargarOnomastico")) {
			cargarOnomastico(request, response, session);
		}

		if (accion.equals("procesarOnomastico")) {
			procesarOnomastico(request, response, session);
		}

		if (accion.equals("cargarLicencia")) {
			cargarLicencia(request, response, session);
		}

		if (accion.equals("cargarNuevaLicencia")) {
			cargarNuevaLicencia(request, response, session);
		}

		if (accion.equals("cargarTrabajadorLicencia")) {
			cargarTrabajadorLicencia(request, response, session);
		}

		if (accion.equals("registrarLicencia")) {
			registrarLicencia(request, response, session);
		}

		if (accion.equals("modificarLicencia")) {
			modificarLicencia(request, response, session);
		}

		if (accion.equals("buscarLicencia")) {
			buscarLicencia(request, response, session);
		}

		if (accion.equals("cargarDatosLicencia")) {
			cargarDatosLicencia(request, response, session);
		}

		if (accion.equals("eliminarLicencias")) {
			eliminarLicencias(request, response, session);
		}

		if (accion.equals("eliminarTrabLicencia")) {
			eliminarTrabLicencia(request, response, session);
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
	 * Metodo encargado de verificar si le corresponde registra onomastico a un
	 * trabajador
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarOnomastico(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		//Obtenemos el codigo del trabajador
		UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
		String codPers = bUsuario.getNroRegistro();//.getNumreg();
		//String codPers = user.getNumreg();
		try {

			UtilesDelegate ud = new UtilesDelegate();
			LicenciaDelegate ld = new LicenciaDelegate();

			//Obtenemos la fecha de su onomastico
			HashMap beanPersona = ud.buscarTrabajador(pool_sp, codPers, null);
			BeanCompensaOnom beanCompensa = ld.prepararCompOnom(pool_sp,codPers);
			
			session.removeAttribute("hPersona");
			session.removeAttribute("beanCompensacion");

			session.setAttribute("hPersona", beanPersona);
			session.setAttribute("beanCompensacion", beanCompensa);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/generarOnomastico.jsp");
			dispatcher.forward(request, response);
			return;
		} catch (LicenciaException e) {
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
	 * Metodo encargado de registrar el onomastico de un trabajador
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void procesarOnomastico(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		BeanCompensaOnom beanCompensa = null;
		try {

			LicenciaDelegate ld = new LicenciaDelegate();
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();//.getNumreg();
			String usuario = bUsuario.getLogin();
			//String codPers = user.getNumreg();

			UtilesDelegate ud = new UtilesDelegate();

			beanCompensa = ld.procesarOnomastico(pool_sp_g, codPers, false,
					usuario);
			HashMap beanPersona = ud.buscarTrabajador(pool_sp, codPers, null);

			session.removeAttribute("beanPersona");
			session.removeAttribute("beanCompensacion");

			session.setAttribute("beanPersona", beanPersona);
			session.setAttribute("beanCompensacion", beanCompensa);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/generarOnomastico.jsp");
			dispatcher.forward(request, response);
			return;
		} catch (LicenciaException e) {
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
	 * Metodo encargado de cargar los datos necesarios para la administracion de
	 * licencias
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarLicencia(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			MantenimientoDelegate md = new MantenimientoDelegate();
			ArrayList tipos = md.buscarMovimientos(pool_sp, "0", "", Constantes.CORREL_LICENCIA);//cargar tipos de licencia

			session.removeAttribute("tiposLicencia");
			session.setAttribute("tiposLicencia", tipos);

			session.removeAttribute("listaLicencias");
			session.removeAttribute("listaTrabajadores");
			
			//borrar los parametros de busqueda.:P
			session.removeAttribute("lcmbCriterio");			
			session.removeAttribute("ltxtValor");			
			session.removeAttribute("lcmbTipo");			

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/administrarLicencias.jsp");
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
	 * Metodo encargado de cargar una nueva licencia
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarNuevaLicencia(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		BeanMensaje beanMsg = null;
		try {

			ArrayList tiposLicencia = (ArrayList) session.getAttribute("tiposLicencia");
			String tipo = request.getParameter("cmbTipo");
			BeanTipoMovimiento mov = (BeanTipoMovimiento) tiposLicencia.get(Integer.parseInt(tipo));
			//BeanLicencia licencia = new BeanLicencia();ACA FALTA MODIFICAR..:p
			/*licencia.setLicencia(mov.getMov());
			licencia.setDescripcion(mov.getDescrip());
			licencia.setMaxDiasAcum(mov.getDiasAcum());
			licencia.setMasiva(mov.getIndProc().equals("1"));*/
			HashMap licencia = new HashMap();
			licencia.put("licencia", mov.getMov());
			licencia.put("descrip", mov.getDescrip());
			licencia.put("dias_acum",new Integer(mov.getDiasAcum()));
			licencia.put("id_proc",mov.getIndProc());
			licencia.put("numero","");
			session.removeAttribute("licencia");
			session.setAttribute("licencia", licencia);
			session.removeAttribute("trabLicencia");
			session.removeAttribute("histoLicencia");
			session.removeAttribute("annoLicencia");
			session.removeAttribute("numArchivo");
			
			//JVV -- INI			
			String valor = request.getParameter("txtValor")==null?request.getParameter("txtValor"):request.getParameter("txtValor");
			String tipoli = licencia.get("licencia").toString().trim();
			MantenimientoDelegate md = new MantenimientoDelegate();
			boolean v = md.validarLicencia(pool_sp, tipoli, valor);	
			if(log.isDebugEnabled()) log.debug("v:"+v);													
				if (v){
					BeanMensaje bean = new BeanMensaje();
					bean.setError(true);
					bean.setMensajeerror("NO PUEDE REGISTRAR LICENCIA, NO APLICABLE PARA CAS - DEC LEG N° 1057");
					bean.setMensajesol("Por favor comuniquese con el Webmaster.");
					session.setAttribute("beanErr", bean);
					RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/PagE.jsp");						
					dispatcher.forward(request, response);
					return;
				}			
			//JVV -- FIN
			
			if (licencia.get("licencia").toString().trim().equals("31")){
				beanMsg = new BeanMensaje();
				ArrayList mensajes = new ArrayList();
				mensajes.add("La Licencia de Compensacion debe ser registrada a traves de la opcion : Labor Excepcional / Registro Compensacion.");
				beanMsg.setListaobjetos(mensajes.toArray());
				beanMsg.setMensajeerror("La Licencia no se ha podido registrar por los siguientes motivos: ");
				beanMsg.setMensajesol("Por favor intente nuevamente.");
				session.setAttribute("beanErr", beanMsg);
				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/PagM.jsp");
				dispatcher.forward(request, response);
				return;
			}
			if (licencia.get("licencia").toString().trim().equals("21")){ 
				 
				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/registrarLicencia.jsp?tamanioPagina=5");
				dispatcher.forward(request, response);
				return;
			}   
			else {
				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/registrarLicencia.jsp?tamanioPagina=5");
				dispatcher.forward(request, response);
				return;
			}
			
		} catch (LicenciaException e) {
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
	 * Metodo encargado de cargar los datos del trabajador a quien se le
	 * registrar la licencia
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarTrabajadorLicencia(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();//.getNumreg();
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

			String valor = request.getParameter("txtValor");
			
			LicenciaDelegate ld = new LicenciaDelegate();
			UtilesDelegate ud = new UtilesDelegate();
			HashMap trabLicencia = ud.buscarTrabajador(pool_sp, valor,seguridad);

			String annoLicencia = request.getParameter("anno");
			if (!Utiles.esAnnoValido(annoLicencia)) {
				throw new Exception("El año " + annoLicencia+ " no es un año v&aacute;lido.");
			}
			session.removeAttribute("annoLicencia");
			session.setAttribute("annoLicencia", annoLicencia);

			if ((String) trabLicencia.get("t02cod_pers") == null || ((String) trabLicencia.get("t02cod_pers")).equals("")) {
				throw new Exception("El n&uacute;mero de registro ingresado no es v&aacute;lido.");
			}

			session.removeAttribute("trabLicencia");
			session.setAttribute("trabLicencia", trabLicencia);

			HashMap licencia = (HashMap) session.getAttribute("licencia");

			if (licencia.get("licencia").toString().trim().equalsIgnoreCase(Constantes.LICENCIA_ENFERMEDAD)) {

				float total = ld.obtenerDiasAcumulados(pool_sp, valor, annoLicencia, licencia.get("licencia").toString());
				log.debug("total : "+total);
				//licencia.setDiasAcumulados(total);
				licencia.put("totaldias",new Float(total));
 				ArchivoDelegate ad = new ArchivoDelegate();
				Integer numArchivo   =new Integer( ad.obtenerNumArchivo());
				licencia.put("numArchivo", numArchivo) ;
				session.setAttribute("numArchivo", numArchivo);
					 
					
			}

			//if (!licencia.isMasiva()){
			if (licencia.get("id_proc")!= null && !licencia.get("id_proc").toString().equals("1")){
				List histo = ld.buscarLicencias(pool_sp, licencia.get("licencia").toString(),
						"5", valor, null);
				
				session.removeAttribute("histoLicencia");
				session.setAttribute("histoLicencia",histo);
								
			}
			else{
				session.removeAttribute("histoLicencia");
			}
			
			session.removeAttribute("licencia");
			session.setAttribute("licencia",licencia);
			log.debug("LICENCIA registrar "+licencia);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/registrarLicencia.jsp");
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
	 * Metodo encargado de registrar la licencia
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void registrarLicencia(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			
			T99DAO codigoDAO = new T99DAO();//ICAPUNAY - PAS20155E230300168 - Modificar dias de licencia prenatal, postnatal y gravidez (ley 30367)
			String dbpool = "jdbc/dgsp"; //ICAPUNAY - PAS20155E230300168 - Modificar dias de licencia prenatal, postnatal y gravidez (ley 30367)

			HashMap licencia = (HashMap) session.getAttribute("licencia");
			UsuarioBean user = (UsuarioBean) session.getAttribute("usuarioBean");
			
			String usuario = user.getLogin();
			String annoLicencia = request.getParameter("anno");

			//verificamos si el año es valido
			if (!Utiles.esAnnoValido(annoLicencia)) {
				throw new Exception("El a&ntilde;o " + annoLicencia+ " no es un a&ntilde;o v&aacute;lido.");
			}

			session.removeAttribute("annoLicencia");
			session.setAttribute("annoLicencia", annoLicencia);

			String fechaIni = request.getParameter("fechaIni");
			String fechaFin = request.getParameter("fechaFin");
			String fechaFin_ = request.getParameter("fechaFin_");		//jquispecoi
			if(fechaFin==null && fechaFin_!=null)		
				fechaFin = fechaFin_;
			
			//log.debug("fecha fin "+ fechaFin);
			String strLicencia = (licencia.get("licencia") != null ) ? licencia.get("licencia").toString():""; 
			//if  ( (strLicencia.trim().equals(Constantes.LICENCIA_PRENATAL)) || (strLicencia.trim().equals(Constantes.LICENCIA_POSTNATAL)) ) { //ICAPUNAY - PAS20155E230300168 - Modificar dias de licencia prenatal, postnatal y gravidez (ley 30367)
			if  ( strLicencia.trim().equals(Constantes.LICENCIA_PRENATAL) ) { //ICAPUNAY - PAS20155E230300168 - Modificar dias de licencia prenatal, postnatal y gravidez (ley 30367)
				//fechaFin = Utiles.dameFechaSiguiente(fechaIni,44); //ICAPUNAY - PAS20155E230300168
				fechaFin = Utiles.dameFechaSiguiente(fechaIni,Integer.parseInt(codigoDAO.findParamByCodTabCodigo(dbpool,Constantes.CODTAB_DIAS_LIC_ADMINLIC,Constantes.LICENCIA_PRENATAL))-1); //ICAPUNAY - PAS20155E230300168 - Modificar dias de licencia prenatal, postnatal y gravidez (ley 30367)				
				log.debug("fecha fin pre2 "+ fechaFin);
			}
			//ICAPUNAY - PAS20155E230300168 - Modificar dias de licencia prenatal, postnatal y gravidez (ley 30367)
			if  ( strLicencia.trim().equals(Constantes.LICENCIA_POSTNATAL) ) {				
				fechaFin = Utiles.dameFechaSiguiente(fechaIni,Integer.parseInt(codigoDAO.findParamByCodTabCodigo(dbpool,Constantes.CODTAB_DIAS_LIC_ADMINLIC,Constantes.LICENCIA_POSTNATAL))-1); //ICAPUNAY - PAS20155E230300168 - Modificar dias de licencia prenatal, postnatal y gravidez (ley 30367)				
				log.debug("fecha fin post2"+ fechaFin);
			}
			//FIN ICAPUNAY - PAS20155E230300168 
			if  ( strLicencia.trim().equals(Constantes.LICENCIA_PARTO_MULTIPLE) ) {
				//fechaFin = Utiles.dameFechaSiguiente(fechaIni,29); //ICAPUNAY - PAS20155E230300168 - Modificar dias de licencia prenatal, postnatal y gravidez (ley 30367)
				fechaFin = Utiles.dameFechaSiguiente(fechaIni,Integer.parseInt(codigoDAO.findParamByCodTabCodigo(dbpool,Constantes.CODTAB_DIAS_LIC_ADMINLIC,Constantes.LICENCIA_PARTO_MULTIPLE))-1); //ICAPUNAY - PAS20155E230300168 - Modificar dias de licencia prenatal, postnatal y gravidez (ley 30367)
				log.debug("fecha fin part mult2 "+ fechaFin);
			}
			
			if  ( (strLicencia.trim().equals(Constantes.LICENCIA_GRAVIDEZ)) ) {
				//fechaFin = Utiles.dameFechaSiguiente(fechaIni,89);//ICAPUNAY - PAS20155E230300168 - Modificar dias de licencia prenatal, postnatal y gravidez (ley 30367)
				fechaFin = Utiles.dameFechaSiguiente(fechaIni,Integer.parseInt(codigoDAO.findParamByCodTabCodigo(dbpool,Constantes.CODTAB_DIAS_LIC_ADMINLIC,Constantes.LICENCIA_GRAVIDEZ))-1);				
				log.debug("fecha fin grav2 "+ fechaFin);
			}
			if  ( strLicencia.trim().equals(Constantes.LICENCIA_GRAVIDEZ_MULTIPLE) ) {
				//fechaFin = Utiles.dameFechaSiguiente(fechaIni,119);//ICAPUNAY - PAS20155E230300168 - Modificar dias de licencia prenatal, postnatal y gravidez (ley 30367)
				fechaFin = Utiles.dameFechaSiguiente(fechaIni,Integer.parseInt(codigoDAO.findParamByCodTabCodigo(dbpool,Constantes.CODTAB_DIAS_LIC_ADMINLIC,Constantes.LICENCIA_GRAVIDEZ_MULTIPLE))-1);
				log.debug("fecha fin grav multi2 "+ fechaFin);
			}
			String ano_ref = request.getParameter("ano_ref");
			String numero_ref = request.getParameter("numero_ref");
			String area_ref = request.getParameter("area_ref");
			String obs = request.getParameter("txtObs");

			int diasDif = 0;
			try{
			
				diasDif = Utiles.obtenerDiasDiferencia(fechaIni,fechaFin)+1; 
				log.debug("diasDif final: "+ diasDif);
				
			}
			catch(Exception e){}
			
			boolean error = false;
			//Licencia con goce menor a 30 dias
			if (strLicencia.trim().equals("06")){
				if (diasDif>30) error = true;
			//Licencia sin goce de 4 a 30 dias				
			}else if (strLicencia.trim().equals("55")){
				if (diasDif<4 || diasDif>30) error = true;
			//Licencia sin goce mayor a 30 dias				
			}else if (strLicencia.trim().equals("56")){
				if (diasDif<=30) error = true;
			//Licencia con goce mayor a 30 dias				
			}else if (strLicencia.trim().equals("57")){
				if (diasDif<=30) error = true;
			}
			
			if (error){
				throw new Exception("La cantidad de días ingresada ("+diasDif+") no se encuentra en el " +
						"rango permitido para esta licencia.");	
			}
			
			String certif = request.getParameter("certificado");
			String cmp = request.getParameter("cmp");
			String fechaCita = request.getParameter("fechaCita");
			String fechaCompensa = request.getParameter("fechaCompensa");
			String fechaFinCompensa = request.getParameter("fechaFinCompensa");

			if (strLicencia.trim().equals(Constantes.LICENCIA_ELECCIONES)
					|| strLicencia.trim().equals(Constantes.FERIADO_COMPENSABLE)
					|| strLicencia.trim().equals(Constantes.LICENCIA_ONOMASTICO)
					|| strLicencia.trim().equals(Constantes.LICENCIA_BOLSA)) {
				obs += "&fecha=" + fechaCompensa;
				
				//JRR
				if (strLicencia.trim().equals(Constantes.LICENCIA_BOLSA)){
					obs += "&fechafin=" + fechaFinCompensa;	
				}
				//						
			}
			HashMap datos = new HashMap();
			datos.put("dbpool", pool_sp_g);
			datos.put("tipoLicencia", strLicencia);
			datos.put("tipoEnfermedad", "");
			datos.put("fechaIni", fechaIni);
			datos.put("fechaFin", fechaFin);
			datos.put("ano_ref", ano_ref);
			datos.put("numero_ref", numero_ref);
			datos.put("area_ref", area_ref);
			datos.put("obs", obs);
			datos.put("certif", certif);
			datos.put("cmp", cmp);
			datos.put("fechaCita", fechaCita);
			datos.put("annoLicencia", annoLicencia);
			datos.put("observacion", "Registro de "+ licencia.get("descrip"));
			datos.put("fechaCompensa", fechaCompensa);
			//JRR
			datos.put("fechaFinCompensa", fechaFinCompensa);

			ArrayList listaTrabajadores = null;
			HashMap trabLicencia = null;			
			//if (licencia.isMasiva()){
			if (licencia.get("id_proc")!= null && licencia.get("id_proc").toString().equals("1")){
				listaTrabajadores = (ArrayList) session.getAttribute("listaTrabajadores");
			}
			else{
				trabLicencia = (HashMap) session.getAttribute("trabLicencia");
				datos.put("codTrab", (String) trabLicencia.get("t02cod_pers"));
				datos.put("codUO", (String) trabLicencia.get("t02cod_uorg"));
			}
			datos.put("codPers", user.getNroRegistro());
			datos.put("usuario", usuario);

			if (strLicencia.trim().equals(Constantes.LICENCIA_ENFERMEDAD)) {
				datos.put("codDiagnostico", request.getParameter("codDiagnostico"));
				datos.put("numArchivo", session.getAttribute("numArchivo"));
			}
			
			
			
			LicenciaDelegate ld = new LicenciaDelegate();
			if(log.isDebugEnabled()) log.debug("datos: "+datos);
			ld.registrarLicencia(datos, listaTrabajadores);

			//if (licencia.isMasiva()){
			if (licencia.get("id_proc")!=null && licencia.get("id_proc").toString().equals("1")){
				session.removeAttribute("histoLicencia");
				session.removeAttribute("listaLicencias");
				session.removeAttribute("trabLicencia");
				session.removeAttribute("licencia");
				
				request.setAttribute("nomProceso",
					"REGISTRO DE LICENCIAS");
				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/resultadoProceso.jsp");
				dispatcher.forward(request, response);
				return;
			}
			else{
				
				//if (!licencia.isMasiva()){
				if (licencia.get("id_proc")!= null && !licencia.get("id_proc").toString().equals("1")){
					
					if (strLicencia.trim().equalsIgnoreCase(Constantes.LICENCIA_ENFERMEDAD)) {
						float total = ld.obtenerDiasAcumulados(pool_sp, 
								(String) trabLicencia.get("t02cod_pers"), 
								annoLicencia, 
								strLicencia);
						log.debug("total : "+total);
						//licencia.setDiasAcumulados(total);
						licencia.put("totaldias",new Float(total));						
						ArchivoDelegate ad = new ArchivoDelegate();
						Integer numArchivo   =new Integer( ad.obtenerNumArchivo());
						licencia.put("numArchivo", numArchivo);
						session.setAttribute("numArchivo",numArchivo);
					}
					log.debug("licencia "+licencia);
					log.debug("licenciastr "+strLicencia);
					List histo = ld.buscarLicencias(pool_sp, strLicencia,"5", datos.get("codTrab").toString(), null);
					log.debug("licencia histo"+ histo);
					session.removeAttribute("histoLicencia");
					session.setAttribute("histoLicencia",histo);
					
					session.removeAttribute("licencia");
					session.setAttribute("licencia",licencia);
				}
				else{
					session.removeAttribute("histoLicencia");
				}
				log.debug("LICENCIA registrar "+licencia);
				RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/registrarLicencia.jsp");
				dispatcher.forward(request, response);
				return;					
			}

		} catch (LicenciaException e) {
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
	 * Metodo encargado de buscar licencias
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarLicencia(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();//.getNumreg();
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
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 15/01/2013 ADD NUEVAS UUOOS ANALISTA OPERATIVO X PROBLEMAS EN CONSULTA POST IMPLANTACION

			String criterio = request.getParameter("cmbCriterio");
			String valor = request.getParameter("txtValor");
			
			String tipo = request.getParameter("cmbTipo");//aqui el valor es indice de la lista de tiposlicencia
			session.removeAttribute("lcmbTipo");
			setAttribute(session, "lcmbTipo", tipo);
			
			ArrayList tiposLicencia = (ArrayList) session.getAttribute("tiposLicencia");
			if(!tipo.equals("-1")){
				BeanTipoMovimiento mov = (BeanTipoMovimiento) tiposLicencia.get(Integer.parseInt(tipo));
				tipo = mov.getMov();//aqui tipo tomar el valor del codigo de tipo de licencia
			}
			
			LicenciaDelegate ld = new LicenciaDelegate();
			List listaLicencias = ld.buscarLicencias(pool_sp, tipo, criterio, valor, seguridad);
			
			Ordenamiento.sort(listaLicencias, "ffinicio"+Ordenamiento.SEPARATOR+Ordenamiento.DESC);//ordenando el resultado por el campo fecha de inicio

			session.removeAttribute("listaLicencias");
			session.setAttribute("listaLicencias", listaLicencias);
			
			//para conservar los parametros de busqueda.:P
			session.removeAttribute("lcmbCriterio");
			setAttribute(session, "lcmbCriterio", criterio);
			session.removeAttribute("ltxtValor");
			setAttribute(session, "ltxtValor", valor);
						

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/administrarLicencias.jsp");
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
	 * Metodo encargado de cargar los datos de una licencia
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarDatosLicencia(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			ArrayList lista = (ArrayList) session.getAttribute("listaLicencias");
			String indice = request.getParameter("indice");
			HashMap licencia = (HashMap) lista.get(Integer.parseInt(indice));
			//se crea otro hashMap por motivo de conservar los datos intactos del arreglo de resultado de licencias
			HashMap mLicencia = new HashMap();			
			mLicencia.putAll(licencia);//copiando todos los valores al nuevo mapa
			if(log.isDebugEnabled()) {
				log.debug("==========================================================");
				log.debug("licencia cargar antes de seterale los otros campos al mapa");
				log.debug("==========================================================");
				log.debug(" licencia:"+licencia);
				log.debug(" MLicencia:"+mLicencia);
				log.debug("==========================================================");
			}
			
			LicenciaDelegate ld = new LicenciaDelegate();
			UtilesDelegate ud = new UtilesDelegate();

			if (mLicencia.get("licencia").toString().trim().equalsIgnoreCase(Constantes.LICENCIA_ENFERMEDAD)) {
				Map licMed = ld.agregaDatosLicenciaMedica(pool_sp_g, mLicencia);
				//if(log.isDebugEnabled()) log.debug("detalle LICENCIA MEDICA:"+licMed);
				if(licMed != null) {
					mLicencia.put("c_certific",(licMed.get("c_certific")!= null ) ? licMed.get("c_certific"):"");
					mLicencia.put("cod_cmp",(licMed.get("cod_cmp")!= null ) ? licMed.get("cod_cmp"):"");
					mLicencia.put("fecha",(licMed.get("fecha")!= null ) ? licMed.get("fecha"):"");
					mLicencia.put("enfermedad",(licMed.get("enfermedad")!= null ) ? licMed.get("enfermedad"):"");
					mLicencia.put("archivoEncontrado",(licMed.get("archivoEncontrado")!= null ) ? licMed.get("archivoEncontrado"):Boolean.FALSE);
					mLicencia.put("numArchivo",(licMed.get("numArchivo")!= null ) ? licMed.get("numArchivo"):Boolean.FALSE);
					mLicencia.put("numSeqdoc",(licMed.get("numSeqdoc")!= null ) ? licMed.get("numSeqdoc"):"");
					mLicencia.put("numArcDet",(licMed.get("numArcDet")!= null ) ? licMed.get("numArcDet"):"");
					mLicencia.put("diagnostico",(licMed.get("diagnostico")!= null ) ? licMed.get("diagnostico"):"");
					 
					
				}else{
					mLicencia.put("c_certific","");
					mLicencia.put("cod_cmp","");
					mLicencia.put("fecha","");
					mLicencia.put("enfermedad","");
					mLicencia.put("archivoEncontrado","");
					mLicencia.put("numArchivo","");
					mLicencia.put("numSeqdoc","");
					mLicencia.put("numArcDet","");
					mLicencia.put("diagnostico","");
				
				}
				
				float total = ld.obtenerDiasAcumulados(pool_sp, mLicencia.get("cod_pers").toString(),
						mLicencia.get("anno").toString(), mLicencia.get("licencia").toString());
				//licencia.setDiasAcumulados(total);
				mLicencia.put("totaldias",new Float(total));
			}
			if (mLicencia.get("licencia").toString().trim().equalsIgnoreCase(Constantes.LICENCIA_ELECCIONES)
					|| mLicencia.get("licencia").toString().trim().equalsIgnoreCase(Constantes.FERIADO_COMPENSABLE)
					|| mLicencia.get("licencia").toString().trim().equalsIgnoreCase(Constantes.LICENCIA_ONOMASTICO)
					|| mLicencia.get("licencia").toString().trim().equalsIgnoreCase(Constantes.LICENCIA_BOLSA)) {
				String txtObs = mLicencia.get("observ").toString();
				//licencia.setObservacion(txtObs.substring(0,txtObs.indexOf("&fecha=")));
				if(log.isDebugEnabled()) {
					log.debug("licencia tipo="+mLicencia.get("licencia").toString().trim()+"***");
					log.debug("Observacion ="+txtObs+"***");
				}
				if( txtObs.indexOf("&fecha=") == -1 ){
					if(log.isDebugEnabled()) log.debug("no se encontro &fecha= en la observacion");
					mLicencia.put("observ", txtObs);
					mLicencia.put("c_certific", "");
				}else {
					if(log.isDebugEnabled()) log.debug("encontro &fecha= en la observacion");
					mLicencia.put("observ", txtObs.substring(0,txtObs.indexOf("&fecha=")));
					//if(log.isDebugEnabled()) log.debug("encontro &fecha= en la observacion");
					//licencia.setCertificado(txtObs.substring(txtObs.indexOf("&fecha=")+7, txtObs.length()));
					mLicencia.put("c_certific", txtObs.substring(txtObs.indexOf("&fecha=")+7, txtObs.length()));
				}
				//JRR
				if (mLicencia.get("licencia").toString().trim().equalsIgnoreCase(Constantes.LICENCIA_BOLSA)){
					if( txtObs.indexOf("&fechafin=") == -1 ){
						if(log.isDebugEnabled()) log.debug("no se encontro &fechafin= en la observacion");
						mLicencia.put("observ", txtObs);
						mLicencia.put("c_certific_fin", "");
					}else {
						//if(log.isDebugEnabled()) log.debug("encontro &fechafin= en la observacion");
						//mLicencia.put("observ", txtObs.substring(0,txtObs.indexOf("&fecha=")));
						if(log.isDebugEnabled()) log.debug("encontro &fechafin= en la observacion");
						mLicencia.put("c_certific_fin", txtObs.substring(txtObs.indexOf("&fechafin=")+10, txtObs.length()));
						
						//Ajustando el contenido  
						mLicencia.put("c_certific", txtObs.substring(txtObs.indexOf("&fecha=")+7, txtObs.indexOf("&fechafin=")));
					}
				}
				//
			}
/*			
			//Aqui seteamos una variable en el map como indicador de visibilidad de los campos de referencia.
			if ( !mLicencia.get("licencia").toString().trim().equalsIgnoreCase(Constantes.LICENCIA_COMPENSACION) || 
					(
					 mLicencia.get("licencia").toString().trim().equalsIgnoreCase(Constantes.LICENCIA_COMPENSACION) && 
					 ( mLicencia.get("anno_ref")!=null ) && ( mLicencia.get("area_ref")!=null ) &&
					 !mLicencia.get("anno_ref").toString().trim().equals("") && !mLicencia.get("area_ref").toString().trim().equals("")
					) ) {
				mLicencia.put("verReferencia", "SI");				
			}else 
				mLicencia.put("verReferencia", "NO");
*/
			mLicencia.put("verReferencia", "SI");	
			
			if(log.isDebugEnabled()) {
				log.debug("===============================================================");
				log.debug("licencia a mostrar despues de seterale los otros campos al mapa");
				log.debug("=============================================================s==");
				log.debug(" "+mLicencia);
			}
			//lo de masiva o no esta en DUDA
			//if (!licencia.isMasiva()){
			if (mLicencia.get("id_proc")!= null && !mLicencia.get("id_proc").toString().equals("1")){
				List histo = ld.buscarLicencias(pool_sp, mLicencia.get("licencia").toString(),
						"5", mLicencia.get("cod_pers").toString(), null);
				
				session.removeAttribute("histoLicencia");
				session.setAttribute("histoLicencia",histo);
			}
			else{
				session.removeAttribute("histoLicencia");
			}
						
			HashMap trabLicencia = ud.buscarTrabajador(pool_sp, mLicencia.get("cod_pers").toString(), null);

			session.removeAttribute("trabLicencia");
			session.setAttribute("trabLicencia", trabLicencia);

			session.removeAttribute("licencia");
			session.setAttribute("licencia", mLicencia);

			session.removeAttribute("annoLicencia");
			session.setAttribute("annoLicencia", (String)mLicencia.get("anno"));

			log.debug("LICENCIA a MOstrar "+mLicencia);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/registrarLicencia.jsp");
			dispatcher.forward(request, response);
			return;
			
		} catch (LicenciaException e) {
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
			log.error(e.getMessage(), e);
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}

	}

	/**
	 * Metodo encargado de eliminar una licencia
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void eliminarLicencias(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			UsuarioBean user = (UsuarioBean) session.getAttribute("usuarioBean");
			String usuario = user.getLogin();

			ArrayList lista = (ArrayList) session.getAttribute("listaLicencias");
			ArrayList listaMensajes = (ArrayList)session.getAttribute("listaMensajes");			//jquispecoi 06/2014
			String[] params = request.getParameterValues("chk_opcion");
			if(listaMensajes==null)listaMensajes = new ArrayList();								//
			
			LicenciaDelegate ld = new LicenciaDelegate();
			lista = ld.eliminarLicencias(pool_sp_g, params, lista, listaMensajes, usuario);

			session.removeAttribute("listaMensajes");											//jquispecoi
			session.setAttribute("listaMensajes", listaMensajes);								//
			session.removeAttribute("listaLicencias");
			session.setAttribute("listaLicencias", lista);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/administrarLicencias.jsp");
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
	 * Metodo encargado de modificar una licencia
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void modificarLicencia(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			
			T99DAO codigoDAO = new T99DAO();//ICAPUNAY - PAS20155E230300168 - Modificar dias de licencia prenatal, postnatal y gravidez (ley 30367)
			String dbpool = "jdbc/dgsp"; //ICAPUNAY - PAS20155E230300168 - Modificar dias de licencia prenatal, postnatal y gravidez (ley 30367)

			HashMap licencia = (HashMap) session.getAttribute("licencia");
			UsuarioBean user = (UsuarioBean) session.getAttribute("usuarioBean");
			
			String usuario = user.getLogin();

			HashMap trabLicencia = (HashMap) session.getAttribute("trabLicencia");
			String fechaIni = request.getParameter("fechaIni");
			String fechaFin = request.getParameter("fechaFin");
			
			String strLicencia= (licencia.get("licencia") != null) ? licencia.get("licencia").toString(): "";
			if  ( strLicencia.trim().equals(Constantes.LICENCIA_PRENATAL) ) { //ICAPUNAY - PAS20155E230300168 - Modificar dias de licencia prenatal, postnatal y gravidez (ley 30367)
				//fechaFin = Utiles.dameFechaSiguiente(fechaIni,44); //ICAPUNAY - PAS20155E230300168
				fechaFin = Utiles.dameFechaSiguiente(fechaIni,Integer.parseInt(codigoDAO.findParamByCodTabCodigo(dbpool,Constantes.CODTAB_DIAS_LIC_ADMINLIC,Constantes.LICENCIA_PRENATAL))-1); //ICAPUNAY - PAS20155E230300168 - Modificar dias de licencia prenatal, postnatal y gravidez (ley 30367)				
				log.debug("fecha fin pre2 "+ fechaFin);
			}
			//ICAPUNAY - PAS20155E230300168 - Modificar dias de licencia prenatal, postnatal y gravidez (ley 30367)
			if  ( strLicencia.trim().equals(Constantes.LICENCIA_POSTNATAL) ) {				
				fechaFin = Utiles.dameFechaSiguiente(fechaIni,Integer.parseInt(codigoDAO.findParamByCodTabCodigo(dbpool,Constantes.CODTAB_DIAS_LIC_ADMINLIC,Constantes.LICENCIA_POSTNATAL))-1); //ICAPUNAY - PAS20155E230300168 - Modificar dias de licencia prenatal, postnatal y gravidez (ley 30367)				
				log.debug("fecha fin post2"+ fechaFin);
			}
			//FIN ICAPUNAY - PAS20155E230300168 
			if  ( strLicencia.trim().equals(Constantes.LICENCIA_PARTO_MULTIPLE) ) {
				//fechaFin = Utiles.dameFechaSiguiente(fechaIni,29); //ICAPUNAY - PAS20155E230300168 - Modificar dias de licencia prenatal, postnatal y gravidez (ley 30367)
				fechaFin = Utiles.dameFechaSiguiente(fechaIni,Integer.parseInt(codigoDAO.findParamByCodTabCodigo(dbpool,Constantes.CODTAB_DIAS_LIC_ADMINLIC,Constantes.LICENCIA_PARTO_MULTIPLE))-1); //ICAPUNAY - PAS20155E230300168 - Modificar dias de licencia prenatal, postnatal y gravidez (ley 30367)
				log.debug("fecha fin part mult2 "+ fechaFin);
			}
			
			if  ( (strLicencia.trim().equals(Constantes.LICENCIA_GRAVIDEZ)) ) {
				//fechaFin = Utiles.dameFechaSiguiente(fechaIni,89);//ICAPUNAY - PAS20155E230300168 - Modificar dias de licencia prenatal, postnatal y gravidez (ley 30367)
				fechaFin = Utiles.dameFechaSiguiente(fechaIni,Integer.parseInt(codigoDAO.findParamByCodTabCodigo(dbpool,Constantes.CODTAB_DIAS_LIC_ADMINLIC,Constantes.LICENCIA_GRAVIDEZ))-1);				
				log.debug("fecha fin grav2 "+ fechaFin);
			}
			if  ( strLicencia.trim().equals(Constantes.LICENCIA_GRAVIDEZ_MULTIPLE) ) {
				//fechaFin = Utiles.dameFechaSiguiente(fechaIni,119);//ICAPUNAY - PAS20155E230300168 - Modificar dias de licencia prenatal, postnatal y gravidez (ley 30367)
				fechaFin = Utiles.dameFechaSiguiente(fechaIni,Integer.parseInt(codigoDAO.findParamByCodTabCodigo(dbpool,Constantes.CODTAB_DIAS_LIC_ADMINLIC,Constantes.LICENCIA_GRAVIDEZ_MULTIPLE))-1);
				log.debug("fecha fin grav multi2 "+ fechaFin);
			}
			
			String ano_ref = request.getParameter("ano_ref");
			String numero_ref = request.getParameter("numero_ref");
			String area_ref = request.getParameter("area_ref");
			String obs = request.getParameter("txtObs");

			String certif = request.getParameter("certificado");
			String cmp = request.getParameter("cmp");
			String fechaCita = request.getParameter("fechaCita");
			String tipoEnfermedad = "";

			String tipoLicencia = licencia.get("licencia").toString();
			
			//JRR - 25/05/2009
			String fechaCompensa = request.getParameter("fechaCompensa");

			if (strLicencia.trim().equalsIgnoreCase(Constantes.LICENCIA_ELECCIONES)
					|| strLicencia.trim().equalsIgnoreCase(Constantes.FERIADO_COMPENSABLE)
					|| strLicencia.trim().equalsIgnoreCase(Constantes.LICENCIA_ONOMASTICO)
					|| strLicencia.trim().equalsIgnoreCase(Constantes.LICENCIA_BOLSA)) {
				
				//JRR - 25/05/2009
				if (strLicencia.trim().equals(Constantes.FERIADO_COMPENSABLE) && fechaCompensa!= null){
					obs += "&fecha=" + fechaCompensa;	
				} else {
					obs+= "&fecha=" + licencia.get("c_certific").toString();
				}
				//	
				
				//JRR - 2008
				if (strLicencia.trim().equalsIgnoreCase(Constantes.LICENCIA_BOLSA)){
					obs+= "&fechafin=" + licencia.get("c_certific_fin").toString();
				}
				//
			}
			LicenciaDelegate ld = new LicenciaDelegate();
			String res = ld.modificarLicencia(pool_sp_g, (String) trabLicencia
					.get("t02cod_pers"), (String) trabLicencia
					.get("t02cod_uorg"), tipoLicencia, tipoEnfermedad, 
					licencia.get("periodo").toString(), strLicencia, (Timestamp)licencia.get("ffinicio"), fechaIni, fechaFin, ano_ref, numero_ref,
					area_ref, obs, licencia.get("numero").toString(), certif, cmp,
					fechaCita, usuario);

			if (!res.equals(Constantes.OK)) {
				throw new Exception(res);
			}

			session.removeAttribute("histoLicencia");
			session.removeAttribute("listaLicencias");
			
			//Si llegue hasta aqu� no hubo errores.
			ArrayList listaMensajes = new ArrayList();
			HashMap hmMensaje = new HashMap();
			hmMensaje.put("tipo", "success");
			hmMensaje.put("mensaje", "La licencia "+licencia.get("numero").toString()+" fue modificada correctamente.");
			listaMensajes.add(hmMensaje);
			
			//Adiciono el mensaje para que sea mostrado.
			session.removeAttribute("listaMensajes");
			session.setAttribute("listaMensajes", listaMensajes);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/administrarLicencias.jsp");
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
	 * Metodo encargado de eliminar los trabajadores de la lista de horas extras
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void eliminarTrabLicencia(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {

			ArrayList listaTrabajadores = (ArrayList) session
					.getAttribute("listaTrabajadores");
			String[] params = request.getParameterValues("chk_opcion");

			if (params != null) {
				for (int i = params.length - 1; i >= 0; i--) {
					String indice = params[i];
					HashMap t = (HashMap) listaTrabajadores.get(Integer.parseInt(indice));
					listaTrabajadores.remove(t);
				}
			}

			session.removeAttribute("listaTrabajadores");
			session.setAttribute("listaTrabajadores", listaTrabajadores);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher(
							"/registrarLicencia.jsp?tamanioPagina=5");
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
}