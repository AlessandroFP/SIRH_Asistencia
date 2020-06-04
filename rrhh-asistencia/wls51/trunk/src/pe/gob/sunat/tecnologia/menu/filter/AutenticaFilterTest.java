package pe.gob.sunat.tecnologia.menu.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import pe.gob.sunat.framework.core.bean.MensajeBean;
import pe.gob.sunat.framework.util.Propiedades;
import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.framework.web.filter.FilterAbstract;
import pe.gob.sunat.sp.asistencia.ServletAdministradorProceso;
import pe.gob.sunat.tecnologia.menu.bean.UsuarioBean;
import pe.gob.sunat.tecnologia.menu.filter.AutenticaFilterTest;

public class AutenticaFilterTest extends FilterAbstract
{
 //private static final Log log;
 //private static Propiedades propiedades;
 //static Class class$0;	
 //static  {
 /*if (class$0 == null) { class$0; try {  } catch (ClassNotFoundException classNotFoundException) { throw new NoClassDefFoundError(null.getMessage()); }  }  log = LogFactory.getLog(class$0 = Class.forName("pe.gob.sunat.tecnologia.menu.filter.AutenticaFilterTest"));
 if (class$0 == null) { class$0; try {  } catch (ClassNotFoundException classNotFoundException) { throw new NoClassDefFoundError(null.getMessage()); }  }  super(class$0 = Class.forName("pe.gob.sunat.tecnologia.menu.filter.AutenticaFilterTest"), "/autenticaTest.properties");*/
 //		if (class$0 == null) { log = LogFactory.getLog(class$0 = Class.forName("AutenticaFilterTest"));
 //		if (class$0 == null) { class$0 = (Class.forName("pe.gob.sunat.tecnologia.menu.filter.Autentica//FilterTest"), "/autenticaTest.properties");
 //}

	private HttpSession session;
    Propiedades propiedades = new Propiedades(getClass(), "/autenticaTest.properties");
    private static final Logger log = Logger.getLogger(AutenticaFilterTest.class);

 


 
public void preProcesa(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {}

public void procesa(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
try {
this.session = ((HttpServletRequest)request).getSession(true);
 if (this.session == null) {
log.error("La session no se ha creado correctamente.");
 forwardError(request, response, "La session no se ha creado correctamente.");
    
 return;
} 
  if (request instanceof HttpServletRequest) {
   
  String method = ((HttpServletRequest)request).getMethod();
 log.info("Autenticando (".concat(method).concat(") : ").concat(((HttpServletRequest)request).getRequestURI()));
 if (!method.equalsIgnoreCase("GET")) {
 return;
 }
limpiaAttributes(this.session);
   
UsuarioBean usuarioBean = (UsuarioBean)this.session.getAttribute("usuarioBean");
 if (usuarioBean == null)
{
  usuarioBean = new UsuarioBean();
  usuarioBean.setLogin(propiedades.leePropiedad("usuario.login"));
  usuarioBean.setCorreo(propiedades.leePropiedad("usuario.correo"));
  usuarioBean.setNombres(propiedades.leePropiedad("usuario.nombres"));
  usuarioBean.setApePaterno(propiedades.leePropiedad("usuario.apePaterno"));
  usuarioBean.setApeMaterno(propiedades.leePropiedad("usuario.apeMaterno"));
  usuarioBean.setNombreCompleto(propiedades.leePropiedad("usuario.nombreCompleto"));
  usuarioBean.setNroRegistro(propiedades.leePropiedad("usuario.nroRegistro"));
  usuarioBean.setCodUO(propiedades.leePropiedad("usuario.codUO"));
  usuarioBean.setCodCate(propiedades.leePropiedad("usuario.codCate"));
  usuarioBean.setNivelUO((new Short(propiedades.leePropiedad("usuario.nivelUO"))).shortValue());
  usuarioBean.setNumRUC(propiedades.leePropiedad("usuario.numRUC"));
  usuarioBean.setUsuarioSOL(propiedades.leePropiedad("usuario.usuarioSOL"));
  usuarioBean.setCodDepend(propiedades.leePropiedad("usuario.codDepend"));
  usuarioBean.setIdCelular(propiedades.leePropiedad("usuario.idCelular"));
  usuarioBean.setCodTOpeComer(propiedades.leePropiedad("usuario.codTOpeComer"));
  
  /*Inicio Agregado*/
  Map principal = new HashMap();
  Map secundario = new HashMap();
  List <String> roles = new ArrayList<String> ();
  //roles.add("SIRH-JEFE");
  //roles.add("SIRH-ADMIN.ASISTENCIA");
  roles.add("SIRH-ANAL.CENTRAL");
  secundario.put("*",roles);
  principal.put("roles", secundario);
  /*Fin Agregado*/
  
  
  usuarioBean.setMap(principal);
        
  String tipo_menu = propiedades.leePropiedad("tipo.menu");
  String origen = "IT";
  if (tipo_menu.equals("2")) { origen = "IA"; }
  else if (tipo_menu.equals("3")) { origen = "ET"; }
    
  usuarioBean.setTicket((new FechaBean()).getFormatDate(tipo_menu.concat("DHHmmssSSS")));
  usuarioBean.getMap().put("idMenu", usuarioBean.getTicket());
  usuarioBean.getMap().put("tipOrigen", origen);
        
setAttribute(this.session, "usuarioBean", usuarioBean);
}
      
} 
 } catch (Exception e) {
 log.error("*** Error *** " + e.toString(), e);
 forwardError(request, response, "Ha ocurrido un error al generar los datos del usuario.");
  return;
 } 
  }
 
public void postProcesa(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    try {
     UsuarioBean usuarioBean = (UsuarioBean)this.session.getAttribute("usuarioBean");
      if (usuarioBean == null) {
        log.error("Error al validar la informaci&oacute;n del usuario en postProcesa.");
        forwardError(request, response, "Error al validar la informaci&oacute;n del usuario.");
        
        return;
      } 
     chain.doFilter(request, response);
    }
    catch (Exception e) {
      log.error("*** Error *** " + e.toString(), e);
      forwardError(request, response, e.toString());
      return;
    } 
  }
   
  public void forwardError(ServletRequest request, ServletResponse response, Object error) {
   if (error instanceof MensajeBean) {
     setAttribute(request, "beanErr", error);
   }
    if (error instanceof String) {
     MensajeBean bean = new MensajeBean();
      bean.setError(true);
      bean.setMensajeerror((String)error);
      setAttribute(request, "beanErr", bean);
    } 
    forward((HttpServletRequest)request, (HttpServletResponse)response, "/PagMenuE.jsp");
  }

 
  public void setAttribute(Object scope, String key, Object obj) {
    if (scope instanceof HttpServletRequest) {
     ((HttpServletRequest)scope).removeAttribute(key);
      ((HttpServletRequest)scope).setAttribute(key, obj);
   } 
    
    if (scope instanceof HttpSession) {
     ((HttpSession)scope).removeAttribute(key);
     ((HttpSession)scope).setAttribute(key, obj);
   } 
   
   if (scope instanceof ServletContext) {
      ((ServletContext)scope).removeAttribute(key);
      ((ServletContext)scope).setAttribute(key, obj);
     } 
  }
  
  public void limpiaAttributes(HttpSession session) {
     Enumeration e = session.getAttributeNames();
     while (e.hasMoreElements()) {
     Object obj = e.nextElement();
      log.debug("Eliminando atributo : " + obj);
     try {
        session.removeAttribute((String)obj);
     } catch (Exception ex) {
        log.error("Error al eliminar atributo : " + obj);
      } 
    } 
  }
   
  public void forward(HttpServletRequest request, HttpServletResponse response, String pagina) {
    try {
      RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(pagina);
      dispatcher.forward(request, response);
    } catch (ServletException e) {
      log.error("*** Error *** " + e.toString());
    } catch (IOException e) {
      log.error("*** Error *** " + e.toString());
    } catch (IllegalStateException illegalStateException) {}
 }
}


