package pe.gob.sunat.rrhh.asistencia;

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

import pe.gob.sunat.tecnologia.menu.bean.UsuarioBean;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;
import pe.gob.sunat.rrhh.asistencia.ejb.delegate.AutorizaLaborDelegate;
import pe.gob.sunat.sp.asistencia.bean.BeanHoraExtra;
import pe.gob.sunat.framework.core.bean.MensajeBean;
import pe.gob.sunat.framework.core.ejb.DelegateException;
import pe.gob.sunat.framework.core.servlet.ServletAbstract;

/**
 * @web.servlet name="AutorizaLaborServlet"
 * @web.servlet-mapping url-pattern = "/asisS20Alias"
 *  
 * @web.ejb-ref name="AutorizaLaborFacadeEJB"
 *              type="session"
 *              home="pe.gob.sunat.rrhh.asistencia.ejb.AutorizaLaborFacadeHome"
 *              remote="pe.gob.sunat.rrhh.asistencia.ejb.AutorizaLaborFacadeRemote"
 *              link="asistencia/ejbsp-autorizalaborfacade.jar#AutorizaLaborFacadeEJB"
 *  
 * <p>Title: AutorizaLaborServlet</p>
 * <p>Description: Servlet que controla el registro de la Autorización de Labor Excepcional</p>
 * <p>Fecha: 12-sep-2007 15:23:51</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: SUNAT</p>
 * @author EBENAVID
 * @version 2.0
 *  
 */
public class AutorizaLaborServlet extends ServletAbstract {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AutorizaLaborDelegate cd;

	
	public void init(ServletConfig config) throws ServletException {
		try {
			super.init(config);
			cd = new AutorizaLaborDelegate();
		} catch (Exception e) {
			throw new ServletException(e.getMessage());
		} 
	}
	
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException 
	 * @throws IOException
	 */
	public void procesa(
			HttpServletRequest request,
			HttpServletResponse response,
			HttpSession session)
			throws ServletException, IOException {

		String accion = request.getParameter("accion");
		log.debug("accion : "+ accion);
		try {

			if (accion == null) {
				accion = "init";
			} else {
				session = request.getSession(false);
				if (session == null) {
					//this.forward(request, response,"/PagM.jsp");
					RequestDispatcher dispatcher = this.getServletContext()
							.getRequestDispatcher("/PagM.jsp");
					dispatcher.forward(request, response);
					return;
				}
			}

			if (accion.equals("cargarLabor")) {
			    cargarLabor(request, response, session);
			}
			//FRD - 01/07/2009
			else if (accion.equals("buscarLENoAutorizadas")) {
				buscarLENoAutorizadas(request, response, session);
			}
			else if (accion.equals("registrarAutorizacionesLE")) {
				registrarAutorizacionesLE(request, response, session);
		    }
			//
			//PRAC-ASANCHEZ 26/08/2009
			/*else if (accion.equals("registrarAutorizacionLE")) {
				registrarAutorizacionLE(request, response, session);
		    }*/
			//
/*			else if (accion.equals("registrarAutoriza")) {
			    registrarLabor(request, response, session);
			}*/
			else if (accion.equals("cargarCompensacion")) {
			    cargarCompensacion(request, response, session);
			}
			else if (accion.equals("mostrarAutoriza")) {
			    mostrarAutoriza(request, response, session);
			}
			else if (accion.equals("registrarCompensacion")) {
			    registrarCompensacion(request, response, session);
			}
			
		} catch (Exception e) {
			MensajeBean bean = new MensajeBean();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un problema : " + e.getMessage());
			bean.setMensajesol("Ingrese nuevamente a la opción.");
			request.setAttribute("beanErr", bean);
			//this.forwardError(request, response,"/PagE.jsp");
			this.getServletContext().getRequestDispatcher("/PagE1.jsp").forward(
					request, response);
					
		}
	}

	/**
	 * Metodo encargado de cargar el ingreso de la Autorizacion de LE
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
	private void cargarCompensacion(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
    
			session.removeAttribute("salidaHE");
			session.removeAttribute("datos");

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			log.debug("Roles "+roles);
			
			//BeanUsuario bUsuario = (BeanUsuario) session.getAttribute("beanusuario");
			//String codPers = bUsuario.getNumreg();
			String codUO = bUsuario.getCodUO();
			//HashMap roles = super.getRoles(session);
			String uoSeg = bUsuario.getVisibilidad();
			
			//JRR - 22/09/2009
			if (log.isDebugEnabled()) log.debug("pre uoSeg : "+uoSeg);
			if (roles.get(Constantes.ROL_ANALISTA_LEXC)!=null) {
				if (log.isDebugEnabled()) log.debug("Es analista LE");
				uoSeg = uoSeg.substring(0,4).concat("%");
			}
			
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) {
				if (log.isDebugEnabled()) log.debug("Es analista Operativo");
				uoSeg = bUsuario.getVisibilidad();
				uoSeg = uoSeg.substring(0,2).concat("%");
			}
			if (log.isDebugEnabled()) log.debug("final uoSeg: "+uoSeg);
			//
			//if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codPers);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("codUO", codUO);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 17/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO X PROBLEMAS EN CONSULTA POST IMPLANTACION

			session.setAttribute("seguridad", seguridad);
			//this.forward(request, response,"/RegIniComp.jsp");
			
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/RegIniComp.jsp");
			dispatcher.forward(request, response);
			
			return;

		} catch (DelegateException e) {
			request.setAttribute("beanErr", e.toString());
			log.error(e,e);
			//this.forwardError(request, response,"/PagE.jsp");
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE1.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			log.error(e,e);
			MensajeBean bean = new MensajeBean();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
					+ e.getMessage());
			bean
					.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			request.setAttribute("beanErr", bean);
			//this.forwardError(request, response,"/PagE.jsp");
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE1.jsp");
			dispatcher.forward(request, response);
		}

	}
	
	/**
	 * Metodo encargado de mostrar las Horas de Labor Excepcional de un determinado Trabajador
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void mostrarAutoriza(HttpServletRequest request,
			HttpServletResponse response,
			HttpSession session) throws ServletException, IOException {

		try {											
			
			String trabajador = request.getParameter("txtResp").toUpperCase();
						
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			//log.debug("Usuario" + bUsuario);
			
			Map datos = new HashMap();
			//log.debug("Pase mpools" + bUsuario.getNombreCompleto());
			datos.put("usuario",bUsuario.getNroRegistro());		
			datos.put("desusuario", bUsuario.getNombreCompleto());
			
			//PRAC - ASANCHEZ 03/07/2009
			datos.put("loginUsuario", bUsuario.getLogin());
			//
	        
	        datos.put("trabajador",trabajador);
			
	        //AutorizaLaborDelegate cd = new AutorizaLaborDelegate();
	        //log.debug("datos "+ datos);
	        Map seguridad = (Map)session.getAttribute("seguridad");
	        
	        //JRR - 25/09/2009
	        datos.put("REGISTRO_COMPENSACION", "FALSE");
	        
	        datos = cd.buscarAutorizacion(datos,seguridad);
			
	        if (datos==null){
				throw new Exception("Ud. No puede autorizar compensaciones de trabajadores de otra unidad organizacional o el trabajador se encuentra Inactivo. ");
			}
	        
	        if (log.isDebugEnabled()) log.debug("datos: "+ datos);
	        String saldo = (String) datos.get("saldo");
	        if (log.isDebugEnabled())  log.debug("saldo: " + saldo);
	        
	        if (saldo.trim().equals("0")){
	        	throw new Exception("Trabajador no cuenta con saldo suficiente para compensar un dia de trabajo. ");
	        }
	        session.setAttribute("listafechas",(List)datos.get("arreglo"));
	        session.setAttribute("datos", datos);
	        //this.forward(request, response,"/RegistrarCompensacion.jsp");
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/RegistrarCompensacion.jsp");
			dispatcher.forward(request, response);
			return;
	
		} catch (DelegateException e) {
			log.debug("Error "+ e);
			
			request.setAttribute("beanErr", e.getMensaje());
			session.setAttribute("beanErr", e.getMensaje());
			//this.forward(request, response,"/PagE.jsp");
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE1.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			log.debug("Error EX "+ e);
			log.debug("Error EX "+ e.getMessage());
			MensajeBean bean = new MensajeBean();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor Verifique.");
			request.setAttribute("beanErr", bean);
			log.debug("Error EX1 "+ e);
			//this.forward(request, response,"/PagE.jsp");
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE1.jsp");
			dispatcher.forward(request, response);
		}
	}    
	
	/**
	 * Metodo encargado de cargar la data de una evaluacion
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void registrarCompensacion(HttpServletRequest request,
			HttpServletResponse response,
			HttpSession session) throws ServletException, IOException {

		try {											
			String fecha = request.getParameter("fechaComp");
			String[] params = request.getParameterValues("chk_opcion");
                
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			log.debug("Usuario" + bUsuario);
			
			Map datos = (HashMap)session.getAttribute("datos");
			BeanHoraExtra salidaHE = new BeanHoraExtra();
			salidaHE.setCodPers((String)datos.get("trabajador"));
			salidaHE.setFechaAutorizacion(fecha);
			
			log.debug("registrar Compensacion " + bUsuario.getNombreCompleto());
			
	        datos.put("fecha" , fecha);
			
	        //JRR - 25/09/2009
	        datos.put("REGISTRO_COMPENSACION", "TRUE");
	        //int minutos = (datos.get("minutosTurno")!=null?Integer.parseInt(datos.get("minutosTurno").toString().trim()):480);
	        Map mapa = cd.buscarAutorizacion(datos, null);
	        int minutos = (mapa.get("minutosTurno")!=null?Integer.parseInt(mapa.get("minutosTurno").toString().trim()):480);
			
	        boolean fechaInvalida = false;
			List arreglo = (List)datos.get("arreglo");
			if (params != null) {
                BeanHoraExtra he = null;
                float contar = 0;
                for (int i = 0; i < params.length; i++) {
                	he = (BeanHoraExtra) arreglo.get(Integer.parseInt(params[i]));
                	
                	if (log.isDebugEnabled()) log.debug("Fecha Autorizada: " + he.getFechaCompensacion());
                	if (log.isDebugEnabled()) log.debug("fecha: " + fecha);
                	
                	if (Utiles.obtenerDiasDiferencia(he.getFechaCompensacion(), fecha)<=0) {
                		if (log.isDebugEnabled()) log.debug("Fecha Autorizada Invalida: " + he.getFechaCompensacion());
                		fechaInvalida=true;
                		break;
                	}
                	
                	contar = contar + he.getAcumulado();
                }
                
               	if (fechaInvalida){
                	throw new Exception("No puede seleccionar una fecha autorizada igual o posterior a la fecha de compensación. ");
                }
                
                //if (contar <480){
                if (log.isDebugEnabled()) log.debug("contar: " + contar + "; minutosTurno: " + minutos);
               	if (contar < minutos){
                	//throw new Exception("La cantidad de minutos seleccionados no suma 480. Por favor verifique. ");
                	throw new Exception("La cantidad de minutos seleccionados no suma " + minutos + ". Por favor verifique. ");
                }
			}
			
	        //AutorizaLaborDelegate cd = new AutorizaLaborDelegate();
	        log.debug("datos "+ datos);
	        datos.put("licencia","NO");
	        datos.put("cruce","NO");
	        datos.put("SINSALDO","NO");
	        datos = cd.registrarCompensacion(params, datos);
	        
	        log.debug("Nuevos datos: "+ datos);
			
	        String cruce = (String) datos.get("cruce");
	        if (cruce.trim().equals("SI")){
	        	throw new Exception("El trabajador no puede tomar Dia de Compensacion para un dia que ha generado Labor Excepcional. ");
	        }
	        
	        String sinsaldo = (String) datos.get("SINSALDO");
	        if (sinsaldo.trim().equals("SI")){
	        	throw new Exception("El trabajador no puede tomar Dia de Compensacion para un dia en el cual aun no tenia Saldo suficiente de Labor Excepcional. ");
	        }
	        
	        //JRR - 01/07/2009
	        String periodoCerrado = (String) datos.get("cerrado");
	        if (periodoCerrado.trim().equals("SI")){
	        	throw new Exception("El trabajador no puede tomar Dia de Compensacion para un dia perteneciente a un período cerrado. ");
	        }
	        
	        String diaCompEsOperativo = (String) datos.get("diaCompOperativo");
	        if (diaCompEsOperativo.trim().equals("SI")){
	        	throw new Exception("El trabajador no puede tomar Dia de Compensacion para un dia en el cual no tiene turno o posee turno Operativo. ");
	        }
	        //
	        
	        String licencia = (String) datos.get("licencia");
	        if (licencia.trim().equals("SI")){
	        	throw new Exception("El trabajador ya tiene una licencia para ese dia. ");
	        }
	        
			log.debug("Entro mostrar Resultado");
			salidaHE.setTrabajador((String)datos.get("destrabajador"));
			session.setAttribute("salidaHE",salidaHE);
			session.setAttribute("datos",datos);
			
			//this.forward(request, response,"/ResRegComp.jsp");
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/ResRegComp.jsp");
			dispatcher.forward(request, response);
			return;
	
		} catch (DelegateException e) {
			log.debug("Error "+ e);
			
			request.setAttribute("beanErr", e.getMensaje());
			session.setAttribute("beanErr", e.getMensaje());
			//this.forwardError(request, response,"/PagE.jsp");
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE1.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			log.debug("Error "+ e);
			MensajeBean bean = new MensajeBean();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor Verifique.");
			request.setAttribute("beanErr", bean);
			
			//this.forwardError(request, response,"/PagE.jsp");
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE1.jsp");
			dispatcher.forward(request, response);
		}
	}    
	
	/**
	 * M�etodo encargado de cargar los datos necesarios para el registro de Autorizacion
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
	private void cargarLabor(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			session.setAttribute("fechaMinima",Utiles.obtenerFechaMinima("jdbc/dgsp"));		//jquispecoi PAS20155E230000073
			
    		session.removeAttribute("salidaHE");
			//PRAC-ASANCHEZ 26/08/2009
			//session.removeAttribute("existeAutorizacionRegistrada");
			//

    		session.removeAttribute("esAnalLexc");
    		
    		
    		UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();
			HashMap roles = (HashMap) bUsuario.getMap().get("roles");
			if (log.isDebugEnabled()) log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			if (log.isDebugEnabled()) log.debug("Roles "+roles);
				
			String codUO = bUsuario.getCodUO();
			String uoSeg = bUsuario.getVisibilidad();
			
			//JRR - 22/09/2009
			if (log.isDebugEnabled()) log.debug("pre uoSeg : "+uoSeg);
			if (roles.get(Constantes.ROL_ANALISTA_LEXC)!=null) {
				if (log.isDebugEnabled()) log.debug("Es analista LE");
				uoSeg = uoSeg.substring(0,4).concat("%");
				session.setAttribute("esAnalLexc", "SI");
			}
			
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) {
				if (log.isDebugEnabled()) log.debug("Es analista Operativo");
				uoSeg = bUsuario.getVisibilidad();
				uoSeg = uoSeg.substring(0,2).concat("%");
			}
			if (log.isDebugEnabled()) log.debug("final uoSeg: "+uoSeg);
			//
			
			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codPers);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("codUO", codUO);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 17/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO X PROBLEMAS EN CONSULTA POST IMPLANTACION
			
			session.setAttribute("seguridad", seguridad);
				
			//PRAC-ASANCHEZ 27/08/2009
			session.setAttribute("usuario", bUsuario.getNroRegistro());
			//
				
			//FRD - 01/07/2009
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/RegistrarLEConsulta.jsp");
			//
				
			dispatcher.forward(request, response);
			return;

		} catch (DelegateException e) {
			request.setAttribute("beanErr", e.toString());
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/PagE1.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			MensajeBean bean = new MensajeBean();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
				+ e.getMessage());
			bean
				.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			request.setAttribute("beanErr", bean);
			//this.forwardError(request, response,"/PagE.jsp");
			RequestDispatcher dispatcher = getServletContext()
				.getRequestDispatcher("/PagE1.jsp");
			dispatcher.forward(request, response);
		}
	}
	
	
	
	/**FRD 01/07/2009
	 * Metodo encargado de buscar las Labores Excepcionales registradas y aun no autorizadas 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarLENoAutorizadas(HttpServletRequest request,HttpServletResponse response,
			HttpSession session) throws ServletException, IOException {
		try {					
			Map resultado = new HashMap();
			String trabajador = request.getParameter("txtResp").toUpperCase();
			String fechaIni = request.getParameter("fechaIni");
			String fechaFin = request.getParameter("fechaFin");
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			if (log.isDebugEnabled()) log.debug("Usuario" + bUsuario);
			
			Map datos = new HashMap();
			
			datos.put("usuario",bUsuario.getNroRegistro());		
			datos.put("desusuario", bUsuario.getNombreCompleto());
	        datos.put("trabajador",trabajador);
	        datos.put("fechaIni",fechaIni);
	        datos.put("fechaFin",fechaFin);
			
	        if (log.isDebugEnabled()) log.debug("datos "+ datos);
	        Map seguridad = (Map)session.getAttribute("seguridad");
	        if (log.isDebugEnabled()) log.debug("seguridad "+ seguridad);
	        
	        resultado = cd.buscarLENoAutorizadas(datos,seguridad);
	        
	        if (resultado==null){
				throw new Exception("Ud. No puede autorizar labor excepcional de trabajadores de otra unidad organizacional, o el trabajador se encuentra Inactivo. ");
			}
	        
	        List leNoAutorizadas = (List)resultado.get("leNoAutorizadas");
	        
	        session.setAttribute("fechaIni", fechaIni);
	        session.setAttribute("fechaFin", fechaFin);
	        session.setAttribute("registro", trabajador);
	        session.setAttribute("uo", datos.get("uuoo"));
	        session.setAttribute("nombreTrabajador",datos.get("nombreTrabajador"));
	        
	        if (leNoAutorizadas != null) {  //&& leNoAutorizadas.size()>0){
	        	//Si hay registros de Horas extras sin autorizar
	        	session.setAttribute("leNoAutorizadas",leNoAutorizadas);
	        	/*RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/RegistrarLEAutorizadas.jsp");
	        	dispatcher.forward(request, response);*/
	        	forward(request,response,"/RegistrarLEAutorizadas.jsp?idSession="+System.currentTimeMillis());
	        }
	        /* Pedido de INRH
	        else{
	        	RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/RegistrarAutorizacionLE.jsp");
	        	dispatcher.forward(request, response);
	        }*/
	        
			return;
		} catch (DelegateException e) {
			log.debug("Error "+ e);
			request.setAttribute("beanErr", e.getMensaje());
			session.setAttribute("beanErr", e.getMensaje());
			/*RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/PagE1.jsp");
			dispatcher.forward(request, response);*/
			forward(request,response,"/PagE1.jsp?idSession="+System.currentTimeMillis());
		} catch (Exception e) {
			MensajeBean bean = new MensajeBean();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor Verifique.");
			request.setAttribute("beanErr", bean);
			/*RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/PagE1.jsp");
			dispatcher.forward(request, response);*/
			forward(request,response,"/PagE1.jsp?idSession="+System.currentTimeMillis());
		}
	}    


	
	
	/**FRD 01/07/2009
	 * Metodo encargado de actualizar el estado de las Labores Excepcionales y
	 * de registrar las autorizaciones de Labor Excepcional
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	
	private void registrarAutorizacionesLE(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws ServletException, IOException {
		try {			
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String trabajador = "";
			String desTrabajador = "";
			String fautor = "";
			String hinic = "";
			String[] params = request.getParameterValues("chk_opcion");
			List leNoAutorizadas = (ArrayList)session.getAttribute("leNoAutorizadas");
			String usuario = (String)session.getAttribute("usuario");
			Map seguridad = (Map)session.getAttribute("seguridad");
			if (params != null) {
				Map datos = new HashMap();
				Map mapa = new HashMap();
				for (int i = 0; i < params.length; i++) {
				
					datos = (HashMap)leNoAutorizadas.get(Integer.parseInt(params[i]));
					seguridad = (Map)session.getAttribute("seguridad");
					trabajador = (String)session.getAttribute("registro");
					desTrabajador = (String)session.getAttribute("nombreTrabajador");
					fautor = (String)datos.get("fecha");
					hinic = (String)datos.get("h_inic");
				
					mapa.put("trabajador",trabajador);
					mapa.put("fautor",fautor);
					mapa.put("usuario",usuario);
					mapa.put("hinic",hinic);
					mapa.put("desusuario", bUsuario.getNombreCompleto());
					mapa.put("destrabajador",desTrabajador);
				
					datos = cd.autorizarLE(mapa, seguridad);	
				
				}
				/*RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/ResRegLEPendientes.jsp");
				dispatcher.forward(request, response);*/
				forward(request,response,"/ResRegLEPendientes.jsp?idSession="+System.currentTimeMillis());
				return;
			}
		} catch (DelegateException e) {
			request.setAttribute("beanErr", e.getMensaje());
			session.setAttribute("beanErr", e.getMensaje());
			/*RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/PagE1.jsp");
			dispatcher.forward(request, response);*/
			forward(request,response,"/PagE1.jsp?idSession="+System.currentTimeMillis());
		} catch (Exception e) {
			MensajeBean bean = new MensajeBean();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor Verifique.");
			request.setAttribute("beanErr", bean);
		
			/*RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/PagE1.jsp");
			dispatcher.forward(request, response);*/
			forward(request,response,"/PagE1.jsp?idSession="+System.currentTimeMillis());
		}
	}    

	/**
	 * Metodo encargado de cargar la data de una evaluacion
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 *
	private void registrarLabor(HttpServletRequest request,
			HttpServletResponse response,
			HttpSession session) throws ServletException, IOException {

		try {											
			
			//String trabajador = request.getParameter("txtResp").toUpperCase();
			String trabajador = (String)session.getAttribute("registro");
			String fecha = request.getParameter("fechaIni");
			
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			log.debug("Usuario" + bUsuario);
			
			BeanHoraExtra salidaHE = new BeanHoraExtra();
			salidaHE.setCodPers(trabajador.toUpperCase());
			salidaHE.setFechaAutorizacion(fecha);
			Map datos = new HashMap();
			log.debug("Pase mpools" + bUsuario.getNombreCompleto());
			datos.put("usuario",bUsuario.getNroRegistro());		
			datos.put("desusuario", bUsuario.getNombreCompleto());
	        datos.put("fecha" , fecha);
	        datos.put("trabajador",trabajador);
			
	        //AutorizaLaborDelegate cd = new AutorizaLaborDelegate();
	        log.debug("datos "+ datos);
	        Map seguridad = (Map)session.getAttribute("seguridad");
	        datos = cd.registrarAutorizacion(datos,seguridad);
			
	        if (datos==null){
				throw new Exception("Ud. No puede autorizar trabajadores de otra unidad organizacional  o el trabajador se encuentra Inactivo");
			}
			log.debug("Entro mostrar Resultado");
			salidaHE.setTrabajador((String)datos.get("destrabajador"));
			session.setAttribute("salidaHE",salidaHE);
			//this.forward(request, response,"/ResRegLabor.jsp");
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/ResRegLabor.jsp");
			dispatcher.forward(request, response);
			return;
	
		} catch (DelegateException e) {
			log.debug("Error "+ e);
			
			request.setAttribute("beanErr", e.getMensaje());
			session.setAttribute("beanErr", e.getMensaje());
			//this.forwardError(request, response,"/PagE.jsp");
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE1.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			log.debug("Error "+ e);
			MensajeBean bean = new MensajeBean();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor Verifique.");
			request.setAttribute("beanErr", bean);
			//this.forwardError(request, response,"/PagE.jsp");
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE1.jsp");
			dispatcher.forward(request, response);
		}
	}    
*/

	/**PRAC-ASANCHEZ 21/08/2009
	 * Metodo encargado de registrar la Labor Excepcional de un determinado empleado
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 *
	private void registrarAutorizacionLE(HttpServletRequest request,HttpServletResponse response,
			HttpSession session) throws ServletException, IOException {

		try {											
			Map datos = new HashMap();
			String trabajador = (String)session.getAttribute("registro");
			String fecha = request.getParameter("fechaIni");
			
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			
			BeanHoraExtra salidaHE = new BeanHoraExtra();
			salidaHE.setCodPers(trabajador.toUpperCase());
			salidaHE.setFechaAutorizacion(fecha);
			
			datos.put("usuario",bUsuario.getNroRegistro());		
			datos.put("desusuario", bUsuario.getNombreCompleto());
	        datos.put("fecha" , fecha);
	        datos.put("trabajador",trabajador);
			
	        Map seguridad = (Map)session.getAttribute("seguridad");
	        datos = cd.registrarAutorizacion(datos,seguridad);
			
	        if (datos==null){
				throw new Exception("Ud. No puede autorizar trabajadores de otra unidad organizacional  o el trabajador se encuentra Inactivo");
			}

			salidaHE.setTrabajador((String)datos.get("destrabajador"));
			session.setAttribute("salidaHE",salidaHE);

			if(((String)datos.get("existeAutorizacionRegistrada")).equals("SI")){
				session.setAttribute("existeAutorizacionRegistrada",(String)datos.get("existeAutorizacionRegistrada"));
				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/RegistrarAutorizacionLE.jsp");
				dispatcher.forward(request, response);
			}
			if(((String)datos.get("existeAutorizacionRegistrada")).equals("NO")){
				session.setAttribute("existeAutorizacionRegistrada",(String)datos.get("existeAutorizacionRegistrada"));
				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/ResRegLabor.jsp");
				dispatcher.forward(request, response);
			}

			return;
	
		} catch (DelegateException e) {
			log.debug("Error "+ e);
			
			request.setAttribute("beanErr", e.getMensaje());
			session.setAttribute("beanErr", e.getMensaje());
			//this.forwardError(request, response,"/PagE.jsp");
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE1.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			log.debug("Error "+ e);
			MensajeBean bean = new MensajeBean();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor Verifique.");
			request.setAttribute("beanErr", bean);
			//this.forwardError(request, response,"/PagE.jsp");
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE1.jsp");
			dispatcher.forward(request, response);
		}
	}
*/	
	
	
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