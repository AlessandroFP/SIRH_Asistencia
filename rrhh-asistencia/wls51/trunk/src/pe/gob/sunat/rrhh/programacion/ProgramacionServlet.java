package pe.gob.sunat.rrhh.programacion;


import pe.gob.sunat.framework.core.servlet.ServletAbstract;
import pe.gob.sunat.framework.core.bean.MensajeBean;
import javax.sql.DataSource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletConfig;
import org.apache.log4j.NDC;
import pe.gob.sunat.framework.core.pattern.DynaBean;
import pe.gob.sunat.framework.core.pattern.ServiceLocator;
import pe.gob.sunat.sol.BeanMensaje;
import pe.gob.sunat.sp.asistencia.ejb.delegate.ReporteDelegate;
import pe.gob.sunat.tecnologia.menu.bean.MenuCliente;
import pe.gob.sunat.tecnologia.menu.bean.UsuarioBean;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.framework.core.ejb.DelegateException;
import pe.gob.sunat.framework.util.cache.dao.ParamDAO;
import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.framework.util.io.Archivo;
import pe.gob.sunat.framework.util.lang.Ordenamiento;
import pe.gob.sunat.framework.util.report.factory.ReportFactory;
import pe.gob.sunat.framework.util.report.interfaz.Report;
import pe.gob.sunat.rrhh.padron.ejb.delegate.PadronDelegate;
import pe.gob.sunat.rrhh.programacion.ejb.delegate.ProgramacionDelegate;
import pe.gob.sunat.framework.util.Propiedades;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Title       : ProgramacionServlet</p>
 * <p>Descripcion : Clase que contiene todas las metodos de mantenimiento para la Programacion de vacaciones </p>
 * <p>Copyright   : Copyright(c) 2008</p>
 * <p>Company     : COMSA
 * @author        : FRANK PICOY (COMSA)
 * @version 1.0 
 *  
 * @web.servlet name="ProgramacionServlet"
 * @web.servlet-mapping url-pattern = "/rhpS01Alias"
 * 
 * @web.ejb-ref name = "ejb/facade/rrhh/programacion/ProgramacionFacadeEJB"
 *        type = "session"
 *        home = "pe.gob.sunat.rrhh.programacion.ejb.ProgramacionFacadeHome"
 *        remote = "pe.gob.sunat.rrhh.programacion.ejb.ProgramacionFacadeRemote"
 *        link = "iaprogramacion/ejbrrhh-programacionfacade.jar#ProgramacionFacadeEJB"
 *        
 * @web.ejb-ref name = "ejb/facade/rrhh/programacion/PadronProgPrFacade"
 *        type = "session"
 *        home = "pe.gob.sunat.rrhh.padron.ejb.PadronProgPrFacadeHome"
 *        remote = "pe.gob.sunat.rrhh.padron.ejb.PadronProgPrFacadeRemote"
 *        link = "iaprogramacion/ejbrrhh-padronfacade.jar#PadronFacade"
 *
 * @web.resource-ref name="jdbc/dcsp" 
 *                   type="javax.sql.DataSource"
 *                   auth="Container" description="datasource a la base de datos personas"
 *                   scope="Shareable"
 * @weblogic.resource-description      res-ref-name="jdbc/dcsp"
 *                                              jndi-name="jdbc/dcsp"
 * @web.resource-ref name="jdbc/dcrecauda" 
 *                   type="javax.sql.DataSource"
 *                   auth="Container" description="datasource a la base de datos recauda"
 *                   scope="Shareable"
 * @weblogic.resource-description      res-ref-name="jdbc/dcrecauda"
 *                                              jndi-name="jdbc/dcrecauda"
 *
 */

public class ProgramacionServlet extends ServletAbstract{
	private static final long serialVersionUID = 1L;
  ProgramacionDelegate pd = null;
  PadronDelegate pad = null;
	MensajeBean bean= null;
	private static String pool_sp;
  
  private static DataSource dsSP=null;
  DataSource dsRecauda=null;
  public static final String ROLES_PROPERTIES_FILE_NAME = "/pe/gob/sunat/rrhh/roles.properties";
  private static String RUTA_TEMP_REPORTE = "/data0/tempo/";
	private static String EXTENSION_ARCHIVO_XLS=".xls";
	private static String EXTENSION_ARCHIVO_PDF=".pdf";
	private static String EXTENSION_ARCHIVO_JASPER=".jasper"; 
  
  public void init(ServletConfig config) throws ServletException {
    try {
      super.init(config);
      pd = new ProgramacionDelegate();
      pad =new PadronDelegate();
      pool_sp = config.getInitParameter("pool_sp");
      cargaParametros();
    } catch (Exception e) {
      throw new ServletException(e.getMessage());
    } finally{
      
    }  
  }
  
  /** Metodo procesa
   * @param request
   * @param response
   * @param session
   * @throws ServletException
   * @throws IOException
   */
  public void procesa(HttpServletRequest request,
      HttpServletResponse response, HttpSession session)throws ServletException, IOException {    
    DynaBean dBean = new DynaBean(request);
    String accion = "";   
    try {
      UsuarioBean bUsuario = (UsuarioBean)session.getAttribute("usuarioBean");
      if (bUsuario!=null){
        NDC.push(bUsuario.getLogin().concat("-").concat(bUsuario.getTicket()));
      } else {
        NDC.push("usuario no logueado".concat("-").concat("..."));
      }
      if (dBean.get("accion") == null) {
        accion = "init";
      } else {
        if (session == null) {
          forward(request, response, "/PagM.jsp");
          return;
        }
      }  
      if ("init".equals(accion)) {
      	session.removeAttribute("datos");
      	setAttribute(session,"datos",null);
      	setAttribute(session,"transaccion","inicio");
      	session.removeAttribute("perfil_usuario");
        cargarRegProgramacion(request, response, session, dBean,bUsuario);
      } else if ("cargarRegProgramacion".equals(dBean.getString("accion"))){ 
      	cargarRegProgramacion(request, response, session, dBean,bUsuario);
      } else if ("buscar".equals(dBean.getString("accion"))){ 
        obtenerUsuProgramacion(request, response, session, dBean,bUsuario);
      } else if ("agregar".equals(dBean.getString("accion"))) {
        agregarProgramacion(request, response, session, dBean);
      } else if ("eliminar".equals(dBean.getString("accion"))) {
        eliminarProgramacion(request, response, session, dBean);
      } else if ("validaPeriodo".equals(dBean.getString("accion"))) {
      	validaPeriodoProgramacion(request, response, session, dBean);
      } else if ("grabar".equals(dBean.getString("accion"))) {
        grabarProgVacaciones(request, response, session, dBean,bUsuario);
      } else if ("cargarConsProgramadas".equals(dBean.getString("accion"))){ 
				cargarConsProgramadas(request, response, session, dBean, bUsuario);
			} else if ("generarRepVacProgramadas".equals(dBean.getString("accion"))) {
				generarRepVacProgramadas(request, response, session, dBean, bUsuario);
			}  else if ("consFaltaProg".equals(dBean.getString("accion"))) {
        cargarConsFaltaProgramacion(request, response, session, dBean,bUsuario);
      } else if ("generarRepFaltantesProg".equals(dBean.getString("accion"))){ 
      	generarRepFaltantesProg(request, response, session, dBean,bUsuario);        
      } else if ("inicioBuscaPersonal".equals(dBean.getString("accion"))){ 
      	inicioBuscaPersonal(request, response, session);
      } else if ("buscarPersonal".equals(dBean.getString("accion"))){ 
      	buscar(request, response, session, dBean);
      } else if ("ordenar".equals(dBean.getString("accion"))){ 
      	ordenar(request, response, session, dBean);
      } else if ("cargarBusqueda".equals(dBean.getString("accion"))){ 
      	cargarBusqueda(request, response, session, dBean);
      } else if ("buscarUOeIntendencia".equals(dBean.getString("accion"))){ 
      	buscarUOeIntendencia(request, response, session, dBean);
      } 
    } catch (DelegateException e) {
      log.error(e,e);
      forwardError(request, response, e.getMensaje());
    } catch (Exception e) {
      log.error(e,e);
      bean= new MensajeBean();
      bean.setError(true);
      bean.setMensajeerror("Ha ocurrido un error al procesar la opcion");
      bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion");
      forwardError(request, response, bean);
    } finally {
      NDC.pop();
      NDC.remove();
    }
  }  
  
  /**
   * Mtodo encargado de agregar una programacion de vacaciones
   * @param request HttpServletRequest
   * @param response HttpServletResponse
   * @param session HttpSession
   * @param params DynaBean
   * @throws ServletException
   * @throws IOException
   */
  private void cargarRegProgramacion(HttpServletRequest request,
  		HttpServletResponse response, HttpSession session,DynaBean params, UsuarioBean bUsuario) throws ServletException, IOException {      
    try {
    	
    	getRoles(request, response, session,bUsuario);
    	if ("usuario_AnalisCentral".equals(session.getAttribute("perfil_usuario").toString())
    			|| "usuario_Jefe".equals(session.getAttribute("perfil_usuario").toString())) {
    		session.removeAttribute("datos");
    		session.removeAttribute("listaProgramacion");
	    	setAttribute(session,"transaccion","inicio");
	    	
	    	setAttribute(session,"perfil_usuario",session.getAttribute("perfil_usuario").toString());
	    	
	      forward(request, response, "/ProgVacaciones.jsp");
    	} else {
    		forward(request, response, "/PagInhabilitado.jsp");
    	}
	    	
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
    /**
     * Metodo encargado de obtener los datos personales y de programacion de vacaciones 
     * del codigo de registro ingresado
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param session HttpSession
     * @param params DynaBean
     * @throws ServletException
     * @throws IOException
     */
    private void obtenerUsuProgramacion(HttpServletRequest request,
    		HttpServletResponse response, HttpSession session,DynaBean params,UsuarioBean bUsuario) throws ServletException, IOException {      
      try {
    	//Agregado FRD 18/06/09  
      	HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);			
		ArrayList usrRoles = (ArrayList) roles.get("*");
		roles = obtRoles (usrRoles);
		String uoSeg2 = bUsuario.getVisibilidad();
		//
   	  
      	getRoles(request, response, session,bUsuario);
      	List listaProgramacion= new ArrayList();
      	params.put("perfil_usuario",session.getAttribute("perfil_usuario").toString());
        params.put("uuoo_usuario",bUsuario.getCodUO());
        params.put("usuario",bUsuario.getNroRegistro());
		String uoSeg = bUsuario.getVisibilidad();
		if ("usuario_AnalisOperativo".equals(session.getAttribute("perfil_usuario").toString().trim())){
			uoSeg = uoSeg.substring(0,2).concat("%");
		}else{
			//Agregado FRD 18/06/09
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) {
				uoSeg = uoSeg2.substring(0,2).concat("%");	
			}else{
				//Agregado FRD 18/06/09
				if (roles.get(Constantes.ROL_ANALISTA_CENTRAL)!=null) uoSeg = "%";
			}
		}
		params.put("uoSeg",uoSeg);
		params.put("roles",roles);
      	Map datos = pd.obtenerUsuProgramacion(params);
    	
      	if (datos!=null) {
      		setAttribute(session,"existePersonal", "0");
      		if ("0".equals(datos.get("usuarioxUUOOValido"))){
     			setAttribute(request,"mensaje", "El Nro. de Registro ingresado no pertenece a su Unidad Organizacional... por favor verifique");
      		} else if ("0".equals(datos.get("t02cod_stat").toString())){
      			setAttribute(request,"mensaje", "El Nro. de Registro ingresado se encuentra inactivo... por favor verifique");
      		} else {
      			listaProgramacion = (ArrayList)datos.get("listaProgramadasAct");
        		setAttribute(session,"existePersonal", "1");	
      		}
      	} else {
      		setAttribute(session,"existePersonal", "0");
      		setAttribute(request,"mensaje", "El Nro. de Registro ingresado no existe... por favor verifique");
      	}
        session.removeAttribute("solicitante");
        session.removeAttribute("listaProgramacion");
      	setAttribute(session,"solicitante", datos);
      	setAttribute(session,"datos", datos);
        setAttribute(session,"listaProgramacion", listaProgramacion);

        setAttribute(session,"transaccion", "consultar");
        forward(request, response, "/ProgVacaciones.jsp");      
      } catch (DelegateException e) {
        log.error(e,e);
        forwardError(request, response, e.getMensaje());
      } catch (Exception e) {
        log.error(e,e);
        bean= new MensajeBean();
        bean.setError(true);
        bean.setMensajeerror("Ha ocurrido un error al obtener la Programacion del Usuario");
        bean.setMensajesol("Por favor intente nuevamente ejecutar la opcin");
        forwardError(request, response, bean);
      } finally {
      }
    }
    
    /**
     * Mtodo encargado de agregar una programacion de vacaciones
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param session HttpSession
     * @param params DynaBean
     * @throws ServletException
     * @throws IOException
     */
    
    private void agregarProgramacion(HttpServletRequest request,
    		HttpServletResponse response, HttpSession session,DynaBean params) throws ServletException, IOException {      
      try {
      	List listaProgramacion = (ArrayList)session.getAttribute("listaProgramacion");
      	params.put("listaProgramacion",listaProgramacion);
      	Map mapaResult = pd.agregarProgramacion(params);
      	if (mapaResult!=null) {
      		listaProgramacion = (ArrayList)mapaResult.get("listaProgramacion");
      		params.put("numdiasTotal",mapaResult.get("numdiasTotal"));
      		setAttribute(request,"existeFecha", mapaResult.get("existeFecha").toString());
      		setAttribute(session,"listaProgramacion",listaProgramacion);
          forward(request, response, "/ProgVacaciones.jsp");      		
      	}
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
    
    /**
     * Metodo encargado de eliminar una programacion de vacaciones
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param session HttpSession
     * @param params DynaBean
     * @throws ServletException
     * @throws IOException
     */
    private void eliminarProgramacion(HttpServletRequest request,
    		HttpServletResponse response, HttpSession session,DynaBean params) throws ServletException, IOException {      
      try {      
      	List listaProgramacion = (ArrayList)session.getAttribute("listaProgramacion");
      	params.put("listaProgramacion",listaProgramacion);
      	listaProgramacion = pd.eliminarProgramacion(params);
        setAttribute(session,"listaProgramacion",listaProgramacion);
        forward(request, response, "/ProgVacaciones.jsp");      
      } catch (DelegateException e) {
        log.error(e,e);
        forwardError(request, response, e.getMensaje());
      } catch (Exception e) {
        log.error(e,e);
        bean= new MensajeBean();
        bean.setError(true);
        bean.setMensajeerror("Ha ocurrido un error al eliminar la programacion");
        bean.setMensajesol("Por favor intente nuevamente ejecutar la opcin");
        forwardError(request, response, bean);
      } finally {
      }
    }

    /**
     * Metodo encargado de grabar las programaciones de vacacion ingresadas.
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param session HttpSession
     * @param params DynaBean
     * @throws ServletException
     * @throws IOException
     */
    private void grabarProgVacaciones(HttpServletRequest request,
  	HttpServletResponse response, HttpSession session,DynaBean params,UsuarioBean bUsuario) throws ServletException, IOException {      
      try {      
      	List listaProgramacion = (ArrayList)session.getAttribute("listaProgramacion");
      	params.put("listaProgramacion",listaProgramacion);
      	params.put("cuser_crea",bUsuario.getLogin());
      	String mensaje = pd.grabarProgVacaciones(params);
      	if (log.isDebugEnabled()){
      		log.debug("Mensaje " +mensaje);
      	}
      	if ("OK".equals(mensaje.trim())){
      	setAttribute(request,"mensaje", "Sus datos han sido grabados satisfactoriamente.");
        forward(request, response, "/ProgVacaciones.jsp");
      	}
      	else {
            BeanMensaje bean= new BeanMensaje();
            List respuesta=new ArrayList();
            respuesta.add(mensaje);
            bean.setError(true);
            bean.setListaobjetos(respuesta.toArray());
            bean.setMensajeerror(mensaje);
            session.setAttribute("beanErr", bean);
            bean.setMensajesol("Por favor Verifique y vuelva a intentar.");
            forwardError(request, response, "/PagM.jsp");
      	}
      } catch (DelegateException e) {
        log.error(e,e);
        forwardError(request, response, e.getMensaje());
      } catch (Exception e) {
        log.error(e,e);
        bean= new MensajeBean();
        bean.setError(true);
        bean.setMensajeerror("Ha ocurrido un error al grabar las programaciones de vacacion");
        bean.setMensajesol("Por favor intente nuevamente ejecutar la opcin");
        forwardError(request, response, bean);
      } finally {
      }
    }
    
    /**
     * Mtodo encargado de cargar la pagina inicial de consulta de trabajadores que faltan
     * programar sus vacaciones
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param session HttpSession
     * @param params DynaBean
     * @throws ServletException
     * @throws IOException
     */
    private void cargarConsFaltaProgramacion(HttpServletRequest request,
    		HttpServletResponse response, HttpSession session,DynaBean params, UsuarioBean bUsuario) throws ServletException, IOException {      
      try {     
      	getRoles(request, response, session,bUsuario);
      	if ("usuario_Jefe".equals(session.getAttribute("perfil_usuario").toString()) ||
      			"usuario_AnalisCentral".equals(session.getAttribute("perfil_usuario").toString()) ||
      			"usuario_AnalisOperativo".equals(session.getAttribute("perfil_usuario").toString())){
	      	setAttribute(session,"perfil_usuario", session.getAttribute("perfil_usuario").toString());
	        forward(request, response, "/ConsFaltaProgramar.jsp");
      	} else {
      		forward(request, response, "/PagInhabilitado.jsp");
      	}
      } catch (DelegateException e) {
        log.error(e,e);
        forwardError(request, response, e.getMensaje());
      } catch (Exception e) {
        log.error(e,e);
        bean= new MensajeBean();
        bean.setError(true);
        bean.setMensajeerror("Ha ocurrido un error al cargar la pagina inicial de consulta de trabajadores");
        bean.setMensajesol("Por favor intente nuevamente ejecutar la opcin");
        forwardError(request, response, bean);
      } finally {
      }
    }
    
    /**
     * Mtodo encargado de validar el periodo de programacion
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param session HttpSession
     * @param params DynaBean
     * @throws ServletException
     * @throws IOException
     */
    private void validaPeriodoProgramacion(HttpServletRequest request,
    		HttpServletResponse response, HttpSession session,DynaBean params) throws ServletException, IOException {      
      try {      
      	Map solicitante = (HashMap)session.getAttribute("solicitante");
      	List listaProgramacionAct = (ArrayList)solicitante.get("listaProgramadasAct");
      	List listaProgramacionSig = (ArrayList)solicitante.get("listaProgramadasSig");
      	List listaProgramacion = null;
      	String saldoAct=solicitante.get("saldoAct").toString();
      	String saldoSig=solicitante.get("saldoSig").toString();
      	String saldo="";
      	setAttribute(request,"existeFecha","0");
      	if (params.getString("cod_periodo")!=null) {
	      	if (params.getString("cod_periodo").equals(solicitante.get("periodo_Act_vac").toString())) {
	      		listaProgramacion = listaProgramacionAct;
	      		saldo=saldoAct;
	      		solicitante.put("fecha_gen_sig_val",solicitante.get("fecha_gen_sig").toString());
	      		solicitante.put("fecha_inicio_vac",solicitante.get("fecha_ini_vac").toString());
	      	} else if (params.getString("cod_periodo").equals(solicitante.get("periodo_Sig_vac").toString())) {
	      		listaProgramacion = listaProgramacionSig;
	      		saldo=saldoSig;
	      		solicitante.put("fecha_gen_sig_val",solicitante.get("fecha_gen_sigper").toString());
	      		solicitante.put("fecha_inicio_vac",solicitante.get("fecha_ini_vac_sig").toString());
	      	}
	      	solicitante.put("saldo",saldo);
	      	solicitante.put("cod_periodo",params.getString("cod_periodo"));
	      	setAttribute(session,"listaProgramacion", listaProgramacion);
	      	setAttribute(session,"datos", solicitante);
      	}
        forward(request, response, "/ProgVacaciones.jsp");      
      } catch (DelegateException e) {
        log.error(e,e);
        forwardError(request, response, e.getMensaje());
      } catch (Exception e) {
        log.error(e,e);
        bean= new MensajeBean();
        bean.setError(true);
        bean.setMensajeerror("Ha ocurrido un error al validar el periodo de programacion");
        bean.setMensajesol("Por favor intente nuevamente ejecutar la opcin");
        forwardError(request, response, bean);
      } finally {
      }
    }
    
    /**
     * Mtodo encargado de generar el reporte de trabajadores que faltan programar sus vacaciones
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param session HttpSession
     * @param params DynaBean
     * @throws ServletException
     * @throws IOException
     */
    private void generarRepFaltantesProg(HttpServletRequest request,
    		HttpServletResponse response, HttpSession session,DynaBean params, UsuarioBean bUsuario) throws ServletException, IOException {      
      List listaFaltaProgVac =null;
    	try {      
	      	params.put("perfil_usuario",session.getAttribute("perfil_usuario").toString());
	        params.put("uuoo_usuario",bUsuario.getCodUO());
			String uoSeg = bUsuario.getVisibilidad();
			String uoAO = bUsuario.getCodUO();//ICAPUNAY - PAS20181U230300016 - Mejoras vacaciones2
			String codPers = bUsuario.getNroRegistro();//ICAPUNAY - PAS20181U230300016 - Mejoras vacaciones2
			String indSub = request.getParameter("chk_ind_sub")!= null ? Constantes.ACTIVO: Constantes.INACTIVO; //ICAPUNAY - PAS20181U230300016 - Mejoras vacaciones2 (1 o 0)
			String indPerSgte = request.getParameter("chk_ind_per")!= null ? Constantes.ACTIVO: Constantes.INACTIVO; //ICAPUNAY - PAS20181U230300016 - Mejoras vacaciones2 (1 o 0)
			String fechaIngreso = request.getParameter("fechaIngreso");
			if ("usuario_AnalisOperativo".equals(session.getAttribute("perfil_usuario").toString().trim())) uoSeg = uoSeg.substring(0,2).concat("%");
			params.put("uoSeg",uoSeg);
			params.put("uoAO",uoAO);//ICAPUNAY - PAS20181U230300016 - Mejoras vacaciones2
			params.put("codPers",codPers);//ICAPUNAY - PAS20181U230300016 - Mejoras vacaciones2
			params.put("indSub",indSub);//ICAPUNAY - PAS20181U230300016 - Mejoras vacaciones2
			params.put("indPerSgte",indPerSgte);//ICAPUNAY - PAS20181U230300016 - Mejoras vacaciones2
			params.put("fechaIngreso",fechaIngreso);//ICAPUNAY - PAS20181U230300016 - Mejoras vacaciones2
	      	listaFaltaProgVac = pd.generarRepFaltantesProg(params);
	        setAttribute(session,"datos", params);
	        setAttribute(session,"listaFaltaProgVac",listaFaltaProgVac);
	        setAttribute(session,"fechaIngreso",fechaIngreso);//ICAPUNAY - PAS20181U230300016 - Mejoras vacaciones2
	      	forward(request, response, "/ConsFaltaProgramarExcel.jsp");      
      } catch (DelegateException e) {
        log.error(e,e);
        forwardError(request, response, e.getMensaje());
      } catch (Exception e) {
        log.error(e,e);
        bean= new MensajeBean();
        bean.setError(true);
        bean.setMensajeerror("Ha ocurrido un error al generar el reporte de consulta de trabajadores");
        bean.setMensajesol("Por favor intente nuevamente ejecutar la opcin");
        forwardError(request, response, bean);
      } finally {
      }
    }
    
    /**
  	 * Mtodo encargado de cargar la pagina inicial de consulta de Vacaciones Programadas
  	 * @param request HttpServletRequest
  	 * @param response HttpServletResponse
  	 * @param session HttpSession
  	 * @param params DynaBean
  	 * @throws ServletException
  	 * @throws IOException
  	 */
    
    private void cargarConsProgramadas(HttpServletRequest request,
  			HttpServletResponse response, HttpSession session,DynaBean params, UsuarioBean bUsuario) throws ServletException, IOException {      
  		try {
  			getRoles(request, response, session,bUsuario);
      	if ("usuario_Jefe".equals(session.getAttribute("perfil_usuario").toString()) ||
      			"usuario_AnalisCentral".equals(session.getAttribute("perfil_usuario").toString()) ||
      			"usuario_AnalisOperativo".equals(session.getAttribute("perfil_usuario").toString())){
	      	setAttribute(session,"perfil_usuario", session.getAttribute("perfil_usuario").toString());
	      	session.removeAttribute("detalleReporte");
	  			forward(request, response, "/consVacProgramadas.jsp");
      	} else {
      		forward(request, response, "/PagInhabilitado.jsp");
      	}
  			
  		} catch (DelegateException e) {
  			log.error(e,e);
  			forwardError(request, response, e.getMensaje());
  		} catch (Exception e) {
  			log.error(e,e);
  			bean= new MensajeBean();
  			bean.setError(true);
  			bean.setMensajeerror("Ha ocurrido un error al cargar la consulta de Vacaciones Programadas");
  			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcin");
  			forwardError(request, response, bean);
  		} finally {
  		}
  	}

    
    /*
	 * Metodo que se encarga de convertir los ROles de HashMap a ArrayList
	 * @param roles
	 */
	public static HashMap obtRoles(ArrayList roles)	throws ServletException, IOException {
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
	
	
  	/**
  	 * Mtodo encargado de cargar los filtros para la consulta de Vac. Programadas
  	 * @param request HttpServletRequest
  	 * @param response HttpServletResponse
  	 * @param session HttpSession
  	 * @param params DynaBean
  	 * @throws ServletException
  	 * @throws IOException
  	 */
  	
  	private void generarRepVacProgramadas(HttpServletRequest request,
  			HttpServletResponse response, HttpSession session,DynaBean params, UsuarioBean bUsuario) throws ServletException, IOException {      
  		try {  
  			
  		log.debug("INGRESO generarRepVacProgramadas()");	
  		UsuarioBean bUsuario1 = (UsuarioBean) session.getAttribute("usuarioBean");
  		HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);	
  		String codigo = bUsuario.getNroRegistro();//.getNumreg();
		ArrayList usrRoles = (ArrayList) roles.get("*");
		roles = obtRoles (usrRoles);
		
		String usuario = bUsuario.getLogin();
		String uoSeg = bUsuario.getVisibilidad();
		if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");
		
		HashMap seguridad = new HashMap();
        seguridad.put("roles", roles);
        seguridad.put("codPers", codigo);
        seguridad.put("uoSeg", uoSeg);
        seguridad.put("uoAO", bUsuario1.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
		log.debug("bUsuario.getCodUO(): "+bUsuario1.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

        String criterio = request.getParameter("cmbCriterio");
        String valor = request.getParameter("txtValor");
        String estado = request.getParameter("cmbEstado");
        String anhoIni = request.getParameter("periodoIni");
        String anhoFin = request.getParameter("periodoFin");
      
		
		if (roles.get(Constantes.ROL_ANALISTA_CENTRAL)!=null) uoSeg = "%";
      	params.put("uuoo_usuario",bUsuario.getCodUO());
      	params.put("roles", roles);//ICAPUNAY-MEMO 32-4F3100-2013
        params.put("uoSeg",uoSeg);
      	params.put("codPers",  bUsuario.getNroRegistro());
      	params.put("perfil_usuario",session.getAttribute("perfil_usuario").toString());
      	params.put("dbpool", "jdbc/dcsp");//diego
      	String tipoR = params.getString("tipoReporte") != null ? params.getString("tipoReporte") : "reporte";
  		params.put("tipoR", tipoR);
  			
  			ReporteDelegate rd=new ReporteDelegate();
  			String fecha =  new FechaBean().getFormatDate("dd/MM/yyyy").toString(); 
  			String hora =   new FechaBean().getFormatDate("HH:mm:ss").toString(); 
  			
  			Map mapaReport = new HashMap();
  			mapaReport.put("periodoini",params.getString("periodoIni"));
  			mapaReport.put("periodofin",params.getString("periodoFin"));
  			mapaReport.put("fecha",fecha.concat(" ").concat(hora));
  			
  			String nombreArchivo = "";
  			//  			
  			if ("reporte".equals(tipoR)){
  				
  				List lista = pd.generarRepVacProgramadas(params);
  				//ASZ
  				if(log.isDebugEnabled()){ log.debug("lista: " + lista);}
  				//
  				String mensaje = "";
  				if (lista.size()<=0){
  					mensaje= "No se encontraron registros";
  					lista.add(new HashMap());
  				}
  				mapaReport.put("mensaje",mensaje);
  				
  	 			//ASZ
  	  			if(log.isDebugEnabled()){ log.debug("llega al pdf");}
  	  			//  				
  				nombreArchivo = "reporteVacProgramadas";
  				this.exportarReportePDF(request,response,session,nombreArchivo,mapaReport,lista);
  	 			//ASZ
  	  			if(log.isDebugEnabled()){ log.debug("sale del pdf");}
  	  			//  				
  			}else if("excel".equals(tipoR)){
  				
  				List lista = pd.generarRepVacProgramadas(params);
  				//ASZ
  				if(log.isDebugEnabled()){ log.debug("lista: " + lista);}
  				//
  				String mensaje = "";
  				if (lista.size()<=0){
  					mensaje= "No se encontraron registros";
  					lista.add(new HashMap());
  				}
  				mapaReport.put("mensaje",mensaje);
  	 			//ASZ
  	  			if(log.isDebugEnabled()){ log.debug("llega al excel");}
  	  			//  				
  				nombreArchivo = "excelVacProgramadas";
  				this.exportarReporteXLS(request,response,session,nombreArchivo,mapaReport,lista);
  	 			//ASZ
  	  			if(log.isDebugEnabled()){ log.debug("sale del excel");}
  	  			//  				
  			}else if("imprimir".equals(tipoR)){
  				
  				/*HashMap mapa = new HashMap();
                mapa.put("dbpool", pool_sp);
                mapa.put("criterio", criterio);
                mapa.put("valor", valor);
                mapa.put("estado", estado);
                mapa.put("anhoIni", anhoIni);
                mapa.put("anhoFin", anhoFin);
                mapa.put("seguridad", seguridad);
                mapa.put("codPers", codigo);*/
  				
  				rd.masivoVacacionesProgramadas(params,usuario);
  				
  				request.setAttribute("nomReporte","REPORTE DE VACACIONES PROGRAMADAS");
                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/resultadoReporte.jsp");
                dispatcher.forward(request, response);
                return;
  			}
  		} catch (DelegateException e) {
  			log.error(e,e);
  			forwardError(request, response, e.getMensaje());
  		} catch (Exception e) {
  			log.error(e,e);
  			bean= new MensajeBean();
  			bean.setError(true);
  			bean.setMensajeerror("Ha ocurrido un error al Verificar el Ruc del Centro de Formacin");
  			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcin");
  			forwardError(request, response, bean);
  		} finally {
  		}
  	}
  	
  	/**
  	 * Metodo encargado de exportar los reportes a PDF
  	 * @param request HttpServletRequest 
  	 * @param response HttpServletResponse
  	 * @param session HttpSession
  	 * @param nombreArchivo String
  	 * @param map Map 
  	 * @param lista List
  	 * @return void
  	 */
  	 private void exportarReporteXLS(HttpServletRequest request, HttpServletResponse response, HttpSession session, String nombreArchivo, Map map, List lista)
     throws ServletException, IOException
 {
     Report reportxls = null;
     try
     {
         File reporte = File.createTempFile("rhp", nombreArchivo);
         
         if(log.isDebugEnabled()){
        	 log.debug("reporte");
        	 log.debug(reporte);

        	 log.debug("'/' + nombreArchivo.trim() + EXTENSION_ARCHIVO_JASPER");
        	 log.debug("/" + nombreArchivo.trim() + EXTENSION_ARCHIVO_JASPER);

        	 log.debug("RUTA_TEMP_REPORTE + reporte.getName() + EXTENSION_ARCHIVO_XLS");
        	 log.debug(RUTA_TEMP_REPORTE + reporte.getName() + EXTENSION_ARCHIVO_XLS);

        	 log.debug("map");
        	 log.debug(map);

        	 log.debug("lista");
        	 log.debug(lista);
         }
         
         reportxls = ReportFactory.getReporteBatch(0, 
         "/" + nombreArchivo.trim() + EXTENSION_ARCHIVO_JASPER, 
         RUTA_TEMP_REPORTE + reporte.getName() + EXTENSION_ARCHIVO_XLS, 
         new HashMap(map), 
         (ArrayList)lista);
        
         if(log.isDebugEnabled()){
        	 log.debug("reportxls");
        	 log.debug(reportxls);
         }
         
         reportxls.exportarXLS();
         
         if(log.isDebugEnabled()){
        	 log.debug("PASA reportxls.exportarXLS();");

        	 log.debug("RUTA_TEMP_REPORTE");
        	 log.debug(RUTA_TEMP_REPORTE);

        	 log.debug("reporte.getName()");
        	 log.debug(reporte.getName());

        	 log.debug("EXTENSION_ARCHIVO_XLS");
        	 log.debug(EXTENSION_ARCHIVO_XLS);
         }
         
         File temp = new File(RUTA_TEMP_REPORTE, reporte.getName() + EXTENSION_ARCHIVO_XLS);
         
         if(log.isDebugEnabled()){         
        	 log.debug("temp");
        	 log.debug(temp);

        	 log.debug("nombreArchivo + EXTENSION_ARCHIVO_XLS");
        	 log.debug(nombreArchivo + EXTENSION_ARCHIVO_XLS);

        	 log.debug("response");
        	 log.debug(response);
         }
         
         (new Archivo()).descargar(temp, nombreArchivo + EXTENSION_ARCHIVO_XLS, response, true);
         
         //log.debug("pas�?????? ================");
         
         if(!temp.delete())
        	 temp = null;
         
     }
     catch(DelegateException e)
     {
         log.error(e, e);
         forwardError(request, response, e.getMessage());
     }
     catch(Exception e)
     {
         bean.setError(true);
         bean.setMensajeerror(e.toString());
         bean.setMensajesol("No se pudo generar el reporte, por favor intente nuevamente ejecutar la opcion");
         setAttribute(request, "beanErr", bean);
         forwardError(request, response, e.getMessage());
     }
 }

  	/**
  	private void exportarReporteXLS (HttpServletRequest request, 
  			HttpServletResponse response, HttpSession session, String nombreArchivo,
  			Map map,List lista) throws ServletException, IOException { 
  		Report reportxls = null;
  		try {  		
  			
  			log.debug("nombreArchivo");
  			log.debug(nombreArchivo);
  			log.debug("map");
  			log.debug(map);
  			log.debug("lista");
  			log.debug(lista);
  			
  			File reporte = File.createTempFile("rhp", nombreArchivo);
  			
  			log.debug("reporte");
  			log.debug(reporte);
  			
  			reportxls = ReportFactory.getReporteBatch(0,new StringBuffer("/").append(nombreArchivo.trim()).append(EXTENSION_ARCHIVO_JASPER).toString(), 
  					new StringBuffer(RUTA_TEMP_REPORTE).append(reporte.getName()).append(EXTENSION_ARCHIVO_XLS).toString(), 
  					new HashMap(map), 
  					(ArrayList)lista);
  			
  			log.debug("reportxls");
  			log.debug(reportxls);
  			
  			reportxls.exportarXLS();
  			
  			log.debug("PASO reportxls.exportarXLS();");
  			log.debug("=================================");
  			
  			log.debug("RUTA_TEMP_REPORTE");
  			log.debug(RUTA_TEMP_REPORTE);
  			
  			log.debug("reporte.getName()");
  			log.debug(reporte.getName());
  			
  			log.debug("EXTENSION_ARCHIVO_XLS");
  			log.debug(EXTENSION_ARCHIVO_XLS);
  			
 			File temp = new File(RUTA_TEMP_REPORTE, 
  					    new StringBuffer(reporte.getName()).append(EXTENSION_ARCHIVO_XLS).toString());
  			(new Archivo()).descargar(temp, new StringBuffer(nombreArchivo).append(EXTENSION_ARCHIVO_XLS).toString(), response, true);

  			
  			
  			//File temp = new File("C:/", 
			//		    "demo.xls");
			//(new Archivo()).descargar(temp, "demo.xls", response, true);
			//

  			log.debug("temp");
  			log.debug(temp);
  			
  			
  			if (!temp.delete()){
  				temp = null;
  			}

  		} catch (DelegateException e) {
  			log.error(e,e); 
  			forwardError(request, response, e.getMessage());
  		} catch (Exception e) {
  			bean.setError(true);
  			bean.setMensajeerror(e.toString());
  			bean.setMensajesol("No se pudo generar el reporte, por favor intente nuevamente ejecutar la opcion");
  			setAttribute(request,"beanErr", bean);
  			forwardError(request, response, e.getMessage());
  		} finally {       
  		} 
  	}
  	*/
  	
  	/**
  	 * Metodo encargado de exportar los reportes a PDF
  	 * @param request HttpServletRequest 
  	 * @param response HttpServletResponse
  	 * @param session HttpSession
  	 * @param nombreArchivo String
  	 * @param map Map 
  	 * @param lista List
  	 * @return void
  	 */
  	private void exportarReportePDF (HttpServletRequest request, 
  			HttpServletResponse response, HttpSession session, String nombreArchivo,
  			Map map,List lista) throws ServletException, IOException { 
  		Report reportxls = null;
  		try {  		
  			File reporte = File.createTempFile("rhp", nombreArchivo); 
  			reportxls = ReportFactory.getReporteBatch(0, 
  					new StringBuffer("/").append(nombreArchivo.trim()).append(EXTENSION_ARCHIVO_JASPER).toString(), 
  					new StringBuffer(RUTA_TEMP_REPORTE).append(reporte.getName()).append(EXTENSION_ARCHIVO_PDF).toString(), 
  					new HashMap(map), 
  					(ArrayList)lista);
  			reportxls.exportarPDF();
  			
  			File temp = new File(RUTA_TEMP_REPORTE, new StringBuffer(reporte.getName()).append(EXTENSION_ARCHIVO_PDF).toString());
  			(new Archivo()).descargar(temp, new StringBuffer(nombreArchivo).append(EXTENSION_ARCHIVO_PDF).toString(),
  					response, true);
  			
  			if (!temp.delete()){
  				temp = null;
  			}
  						
  		} catch (DelegateException e) {
  			log.error(e,e); 
  			forwardError(request, response, e.getMessage());
  		} catch (Exception e) {
  			bean.setError(true);
  			bean.setMensajeerror(e.toString());
  			bean.setMensajesol("No se pudo generar el reporte, por favor intente nuevamente ejecutar la opcion");
  			setAttribute(request,"beanErr", bean);
  			forwardError(request, response, e.getMessage());
  		} finally {       
  		} 
  	}
  	
  	/**
  	 * Metodo inicio carga los valores por defecto(Metodo importado de Sunat para la busqueda de Personal - Proy Padron)
  	 * 
  	 * @param request {@link HttpServletRequest}
  	 * @param response {@link HttpServletResponse}
  	 * @param session {@link HttpSession}
  	 * @throws ServletException
  	 * @throws IOException
  	 */
  	private void inicioBuscaPersonal(HttpServletRequest request,
              HttpServletResponse response, HttpSession session)
              throws ServletException, IOException {		
  		try {        	
      	session.removeAttribute("fld_nreg");
          session.removeAttribute("fld_appat");
          session.removeAttribute("fld_apmat");
          session.removeAttribute("fld_nomb");
          session.removeAttribute("fld_coduorg");
          session.removeAttribute("fld_check");
          session.removeAttribute("fld_codcate");
          session.removeAttribute("listaFicha");       
          
          forward(request, response,"/ConsultaPadron.jsp");

      }  catch (Exception e) {
      	log.error("*** Error ***",e);
      	forwardError(request, response, "Por favor intente nuevamente ejecutar la opcion." ,
      			"/pagE.jsp?idSession = " + System.currentTimeMillis());
      }
  	}
  	
  	/**
  	 * Metodo buscar : encargado de realizar la busqueda de Personal(Metodo importado de Sunat para la busqueda de Personal - Proy Padron)
  	 * 
  	 * @param request {@link HttpServletRequest}
  	 * @param response {@link HttpServletResponse}
  	 * @param session {@link HttpSession}
  	 * @throws ServletException
  	 * @throws IOException
  	 */
  	private void buscar(HttpServletRequest request,
              HttpServletResponse response, HttpSession session, DynaBean params)
              throws ServletException, IOException {
  		try {
  			String fld_estado = "1";
  			params.put("estado", fld_estado);
  			List listados =pad.buscarT02(params);
  			session.setAttribute("listaFicha", listados);
  			/** seteando campos a session**/
  			session.setAttribute("fld_nreg", params.getString("nreg"));
  			session.setAttribute("fld_appat", params.getString("appat"));
  			session.setAttribute("fld_apmat", params.getString("apmat"));
  			session.setAttribute("fld_nomb", params.getString("nomb"));
  			session.setAttribute("fld_coduorg", params.getString("coduorg"));
  			session.setAttribute("fld_codcate", params.getString("codcate"));            
  			session.removeAttribute("fld_inicio");
  			session.setAttribute("fld_inicio", "0");
        /**fin seteando campos a session**/			
        forward(request, response,"/ConsultaPadron.jsp");            
      } catch (Exception e) {
        log.error("*** Error ***",e);
        forwardError(request, response, "Por favor intente nuevamente ejecutar la opcion." ,
        "/pagE.jsp?idSession = " + System.currentTimeMillis());
      }
  	}
  	
  	/**
  	 * Metodo ordenar : encargado de realizar el ordenamiento de acuerdo al campo especificado<br>
  	 *                  tanto ascendentemente o descendentemente. (Metodo importado de Sunat para la busqueda de Personal - Proy Padron)
  	 *                  
  	 * @param request {@link HttpServletRequest}
  	 * @param response {@link HttpServletResponse}
  	 * @param session {@link HttpSession}
  	 * @throws ServletException
  	 * @throws IOException
  	 */
  	private void ordenar(HttpServletRequest request,
              HttpServletResponse response, HttpSession session, DynaBean params)
              throws ServletException, IOException {		
  		try {        	        	
  			String campo = "";
        List listado = (ArrayList)session.getAttribute("listaFicha");
          
        if("1".equals(params.getString("campo"))){
         	campo = "t02cod_pers";
        } else if("2".equals(params.getString("campo"))){
          campo = "t02ap_pate";
        } else if("3".equals(params.getString("campo"))){
          campo = "t02cod_uorg";
        } else if("4".equals(params.getString("campo"))){
          campo = "t02cod_cate";
        } else {
         	campo = "t02cod_pers";
        }
          
        Ordenamiento.sort(listado, campo + Ordenamiento.SEPARATOR + params.getString("asc"));
         
        session.setAttribute("listaFicha", listado);
          
        forward(request, response,"/ConsultaPadron.jsp");
        session.removeAttribute("inicio");
        session.setAttribute("inicio", "0");
          
      } catch (Exception e) {
      	log.error("*** Error ***",e);
      	forwardError(request, response, "Por favor intente nuevamente ejecutar la opcion." ,
      			"/pagE.jsp?idSession = " + System.currentTimeMillis());
      }
  	}
  	
    /**
     * Metodo cargarBusqueda : encargado de redirigir a la pagina invocada
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param session  HttpSession
     * @param dBean DynaBean
     * @throws ServletException
     * @throws IOException
     */
    private void cargarBusqueda(HttpServletRequest request,
        HttpServletResponse response, HttpSession session,DynaBean dBean) throws ServletException, IOException {
      try {
        String opcion = (String)dBean.get("txtOpcion");
        session.removeAttribute("listaUor");
        session.removeAttribute("listaRep");
        session.removeAttribute("fld_criterio");             
        session.removeAttribute("fld_valor");
        setAttribute(session,"fld_criterio","-1");
        if ( "int".equals(opcion)) {
          forward(request, response,"/BusquedaInt.jsp" );         
        } else if ("uor".equals(opcion)) {
          forward(request, response,"/BusquedaUor.jsp" );         
        } 
      } catch (DelegateException e) {
        log.error(e,e);
        forwardError(request, response, e.getMensaje());
      } catch (Exception e) {
        log.error(e,e);
        bean= new MensajeBean();
        bean.setError(true);
        bean.setMensajeerror("Ha ocurrido un error al redireccionar a una pagina de busqueda");
        bean.setMensajesol("Por favor intente nuevamente ejecutar la opcin");
        forwardError(request, response, bean);
      } finally {
      }
    }
    
    /**
     * Metodo buscar : encargado de realizar la busqueda de las categorias o unidades organizacionales
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param session HttpSession
     * @param params DynaBean
     * @throws ServletException
     * @throws IOException
     */
    private void buscarUOeIntendencia(HttpServletRequest request,
        HttpServletResponse response, HttpSession session,DynaBean params) throws ServletException, IOException {        
      List listados = null;        
      try {      
        params.put("criterio", params.get("cmbCriterio"));
        params.put("opcion", params.get("txtOpcion"));
        params.put("valor", params.get("txtValor"));
        params.put("vigente", "1");            
        listados = pd.buscarUOeIntendencia(params);
        setAttribute(session,"listaRep",listados);            
        setAttribute(session,"fld_valor", params.get("txtValor"));
        setAttribute(session,"fld_criterio", params.get("cmbCriterio"));            
        setAttribute(session,"listaUor", listados);
        if ("int".equals((String)params.get("txtOpcion"))) {
          forward(request, response,"/BusquedaInt.jsp" );
        } else if("uor".equals((String)params.get("txtOpcion"))) {
          forward(request, response, "/BusquedaUor.jsp");
        } 
      } catch (DelegateException e) {
        log.error(e,e);
        forwardError(request, response, e.getMensaje());
      } catch (Exception e) {
        log.error(e,e);
        bean= new MensajeBean();
        bean.setError(true);
        bean.setMensajeerror("Ha ocurrido un error al realizar la busqueda de las categorias o unidades organizacionales");
        bean.setMensajesol("Por favor intente nuevamente ejecutar la opcin");
        forwardError(request, response, bean);
      } finally {
      }
    }     
    
    /*
     * Obtiene el archivo de propiedades
     * @return Archivo de Propiedades 
     * @since 03-oct-2007
     */
    public Propiedades getPropiedades() {
      return (new Propiedades(getClass(), ROLES_PROPERTIES_FILE_NAME));
    }
    
    /**
     * Metodo que se encarga de obtener los Roles del Usuario
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param session HttpSession
     * @param bUsuario UsuarioBean
     * @throws ServletException
     * @throws IOException
     */
    public void getRoles(HttpServletRequest request,
        HttpServletResponse response, HttpSession session, UsuarioBean bUsuario)
    throws ServletException,IOException {

    	Map roles = (HashMap)MenuCliente.getRoles(bUsuario);
    	
      if (roles.get(getPropiedades().leePropiedad("ROL_EMPLEADO"))!=null){
        session.setAttribute("perfil_usuario", "usuario_Empleado");
      }else if (roles.get(getPropiedades().leePropiedad("ROL_JEFE"))!=null){
        session.setAttribute("perfil_usuario", "usuario_Jefe");
      }else if (roles.get(getPropiedades().leePropiedad("ROL_ANALISTA_CENTRAL"))!=null){
        session.setAttribute("perfil_usuario", "usuario_AnalisCentral");
      }else if (roles.get(getPropiedades().leePropiedad("ROL_ANALISTA_OPERATIVO"))!=null){
        session.setAttribute("perfil_usuario", "usuario_AnalisOperativo");
      }else{
        forward(request, response, "/PagInhabilitado.jsp");
      }
    }
    
    /**
     * Metodo encargado de cargar los objetos a memoria
     * @throws ServletException
     * @throws IOException
     */
    private void cargaParametros() throws ServletException, IOException {
      try {
        dsSP = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dcsp");
        ParamDAO paramDAO = new ParamDAO();
        paramDAO.cargar(new String[]{"001"}, dsSP, "spprm001t99", ParamDAO.TIPO2);//Categoria
        paramDAO.cargar(new String[]{"P01"}, dsSP, "spprmP01t99", ParamDAO.TIPO2);//Dias de Vacaciones
        //UUOO
        paramDAO.cargar("select t12cod_uorga, t12des_corta from t12uorga where (t12ind_estad = 1) and length(t12cod_uorga) = 6", dsSP, "spprm16t12");
        paramDAO.cargar(new String[]{"V01"}, dsSP, "spprmV01t99", ParamDAO.TIPO2);//Estado de Programacion de Vac
      } catch (Exception e) { 
        log.error("Error",e);
      } finally {
      } 
    }
}
