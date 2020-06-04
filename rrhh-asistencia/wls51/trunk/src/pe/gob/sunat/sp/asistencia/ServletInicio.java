package pe.gob.sunat.sp.asistencia;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.log4j.NDC;

import pe.gob.sunat.framework.core.bean.MensajeBean;
import pe.gob.sunat.framework.core.ejb.DelegateException;
import pe.gob.sunat.framework.core.servlet.ServletAbstract;
import pe.gob.sunat.framework.util.Propiedades;
import pe.gob.sunat.framework.util.cache.dao.ParamDAO;
import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.sol.BeanMensaje;
//import pe.gob.sunat.sol.seguridad.acceso.jaas.AutenticacionException;
//import pe.gob.sunat.sol.seguridad.acceso.jaas.ServletAutenticacionIntranet;
//import pe.gob.sunat.sol.seguridad.acceso.portal.bean.BeanUsuario;
import pe.gob.sunat.sp.asistencia.bean.BeanPeriodo;
import pe.gob.sunat.sp.asistencia.bean.BeanTipoMovimiento;
import pe.gob.sunat.sp.asistencia.bean.BeanTipoReloj;
import pe.gob.sunat.sp.asistencia.ejb.delegate.AsistenciaDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.AsistenciaException;
import pe.gob.sunat.sp.asistencia.ejb.delegate.MantenimientoDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.MantenimientoException;
import pe.gob.sunat.sp.asistencia.ejb.delegate.ReporteDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.ReporteException;
import pe.gob.sunat.sp.asistencia.ejb.delegate.SolicitudDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.VacacionDelegate;
import pe.gob.sunat.tecnologia.menu.bean.MenuCliente;
import pe.gob.sunat.tecnologia.menu.bean.UsuarioBean;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;
import pe.gob.sunat.sp.asistencia.bean.BeanReporte;// ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA - 22/08/2011
import pe.gob.sunat.sp.dao.T02DAO;
import pe.gob.sunat.rrhh.dao.T12DAO;// ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA - 22/08/2011

/**
 * @web.servlet name="ServletInicio"
 * @web.servlet-mapping url-pattern = "/inicioS01Alias"
  */
 
public class ServletInicio extends ServletAbstract {

	private static final Logger log = Logger.getLogger(ServletReporte.class);
	private static String pool_sp;
	
	Propiedades constantes = new Propiedades(getClass(), "/constantes.properties");
	MensajeBean bean= null;
	public static final String ROLES_PROPERTIES_FILE_NAME = "/pe/gob/sunat/rrhh/roles.properties";
	
	public void init(ServletConfig config) throws ServletException {
		try{    
			super.init(config);
					
		} catch (Exception e) {
			log.error("**** Error ****", e);
			throw new ServletException(e.getMessage());
		} finally {
		}

	}

    public void procesa(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
      try{
    	RequestDispatcher dispatcher =
    			getServletContext().getRequestDispatcher("/index.jsp");
    			dispatcher.forward(request, response);

      } catch (Exception e) {
	    log.error("*** Error ***", e);
	  } finally{
		NDC.pop();
		NDC.remove();
	  }	
    }
    
    

	
}