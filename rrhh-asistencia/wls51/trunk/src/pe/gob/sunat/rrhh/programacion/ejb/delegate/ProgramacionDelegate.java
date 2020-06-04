package pe.gob.sunat.rrhh.programacion.ejb.delegate;

import pe.gob.sunat.framework.core.ejb.DelegateException;
import org.apache.log4j.Logger;

//import pe.gob.sunat.rrhh.formativa.ejb.AsisForFacadeHome;
import pe.gob.sunat.rrhh.programacion.ejb.ProgramacionFacadeHome;
import pe.gob.sunat.rrhh.programacion.ejb.ProgramacionFacadeRemote;
import pe.gob.sunat.framework.core.bean.MensajeBean;
import pe.gob.sunat.framework.core.pattern.ServiceLocator;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
 
/**
 * <p> Title : ProgramacionDelegate</p>
 * <p>Description : Clase encargado de realizar llamadas a los m�todos de la clase ProgramacionFacade </p>
 * <p>Copyright   : Copyright (c) 2008</p>
 * <p>Company     : COMSA </p>
 * @author FRANK PICOY 
 * @version 1.0 
 */
 
public class ProgramacionDelegate {
	 private Logger log = Logger.getLogger(this.getClass());
	 private ProgramacionFacadeRemote cpf;
	  
	 public ProgramacionDelegate() throws DelegateException {
	    try {
	    	ProgramacionFacadeHome cpFachome = (ProgramacionFacadeHome) ServiceLocator.getInstance().getRemoteHome("ejb/facade/rrhh/programacion/AsisProgramacionFacadeEJB",ProgramacionFacadeHome.class);
	    																											//ProgramacionFacadeHome.JNDI_NAME									
	    	cpf = cpFachome.create();
	    } catch (Exception e) {
	      MensajeBean beanM = new MensajeBean();
	      beanM.setError(true);
	      beanM.setMensajeerror("Ha ocurrido un error inesperado en la obtencion de un acceso .");
	      beanM.setMensajesol("Por favor intente nuevamente ejecutar la opcion, en caso de continuar con el problema, comuniquese con nuestro webmaster.");
	      throw new DelegateException(this, beanM);
	    } finally {
	    }
	  }
	  
	  /**
	   * Metodo encargado de cargar la programacion
	   * @param Map params
	   * @return List
	   * @throws DelegateException
	   */  
	  public List cargarProgramacion(Map params) throws  DelegateException {
	    List lista=null;
	    try {      
	      lista=cpf.cargarProgramacion(params);
	    } catch (RemoteException e) {      
	      MensajeBean beanM = new MensajeBean();
	      if ("pe.gob.sunat.framework.core.ejb.FacadeException".equals(e.detail.getClass().getName())) {
	        beanM = ((pe.gob.sunat.framework.core.ejb.FacadeException) e.detail).getMensaje();
	      } else {
	      	log.error(e.getMessage());
	        log.error("Error", e);
	        beanM.setError(true);
	        beanM.setMensajeerror("Ha ocurrido un error al cargar la programacion.");
	        beanM.setMensajesol("Por favor intente nuevamente.");
	      }
	      throw new DelegateException(this, beanM);
	    } finally {
	    }
	    
	    return lista;
	  }
	  /**
	   * Metodo encargado de obtener los datos personales y de programacion de vacaciones 
     * del codigo de registro ingresado
	   * @param Map datos
	   * @return Map
	   * @throws DelegateException
	   */  
	  public Map obtenerUsuProgramacion(Map datos) throws  DelegateException {
	    Map map=null;
	    try {      
	      map=cpf.obtenerUsuProgramacion(datos);
	    } catch (RemoteException e) {      
	      MensajeBean beanM = new MensajeBean();
	      if ("pe.gob.sunat.framework.core.ejb.FacadeException".equals(e.detail.getClass().getName())) {
	        beanM = ((pe.gob.sunat.framework.core.ejb.FacadeException) e.detail).getMensaje();
	      } else {
	      	log.error(e.getMessage());
	        log.error("Error", e);
	        beanM.setError(true);
	        beanM.setMensajeerror("Ha ocurrido un error al al obtener la Programacion del Usuario");
	        beanM.setMensajesol("Por favor intente nuevamente.");
	      }
	      throw new DelegateException(this, beanM);
	    } finally {
	    }
	    
	    return map;
	  } 
	  
	  /**
	   * Metodo encargado de grabar las programaciones de vacacion ingresadas
	   * @param Map datos
	   * @return void
	   * @throws DelegateException
	   */  
	  
	  public String grabarProgVacaciones(Map datos) throws  DelegateException {
		  String mensaje = "";
	    try {      
	      mensaje = cpf.grabarProgVacaciones(datos);
	    } catch (RemoteException e) {      
	      MensajeBean beanM = new MensajeBean();
	      if ("pe.gob.sunat.framework.core.ejb.FacadeException".equals(e.detail.getClass().getName())) {
	        beanM = ((pe.gob.sunat.framework.core.ejb.FacadeException) e.detail).getMensaje();
	      } else {
	      	log.error(e.getMessage());
	        log.error("Error", e);
	        beanM.setError(true);
	        beanM.setMensajeerror("Ha ocurrido un error al grabar las programaciones de vacacion");
	        beanM.setMensajesol("Por favor intente nuevamente.");
	      }
	      throw new DelegateException(this, beanM);
	    } finally {
	    }
	    return mensaje;
	  } 
	  
	  /**
	   * Metodo encargado de generar el reporte de trabajadores que faltan programar sus vacaciones
	   * @param Map datos
	   * @return List
	   * @throws DelegateException
	   */  
	  public List generarRepFaltantesProg(Map datos) throws  DelegateException {
	    List lista=null;
	  	try {      
	  		lista = cpf.generarRepFaltantesProg(datos);
	    } catch (RemoteException e) {      
	      MensajeBean beanM = new MensajeBean();
	      if ("pe.gob.sunat.framework.core.ejb.FacadeException".equals(e.detail.getClass().getName())) {
	        beanM = ((pe.gob.sunat.framework.core.ejb.FacadeException) e.detail).getMensaje();
	      } else {
	      	log.error(e.getMessage());
	        log.error("Error", e);
	        beanM.setError(true);
	        beanM.setMensajeerror("Ha ocurrido un error al al generar el reporte de consulta de trabajadores");
	        beanM.setMensajesol("Por favor intente nuevamente.");
	      }
	      throw new DelegateException(this, beanM);
	    } finally {
	    }
	    return lista;
	  } 
	  
	  /**
	   * Metodo encargado de agregar una programacion de vacaciones
	   * @param Map datos
	   * @return Map
	   * @throws DelegateException
	   */  
	  public Map agregarProgramacion(Map datos) throws  DelegateException {
	  	Map mapaResult=null;
	  	try {      
	  		mapaResult = cpf.agregarProgramacion(datos);
	    } catch (RemoteException e) {      
	      MensajeBean beanM = new MensajeBean();
	      if ("pe.gob.sunat.framework.core.ejb.FacadeException".equals(e.detail.getClass().getName())) {
	        beanM = ((pe.gob.sunat.framework.core.ejb.FacadeException) e.detail).getMensaje();
	      } else {
	      	log.error(e.getMessage());
	        log.error("Error", e);
	        beanM.setError(true);
	        beanM.setMensajeerror("Ha ocurrido un error al agregar una programacion de vacaciones");
	        beanM.setMensajesol("Por favor intente nuevamente.");
	      }
	      throw new DelegateException(this, beanM);
	    } finally {
	    }
	    return mapaResult;
	  } 
	  
	  /**
	   * Metodo encargado de eliminar una programacion de vacaciones
	   * @param Map datos
	   * @return Map
	   * @throws DelegateException
	   */  
	  public List eliminarProgramacion(Map datos) throws  DelegateException {
	  	List listaProgramacion=null;
	  	try {      
	  		listaProgramacion = cpf.eliminarProgramacion(datos);
	    } catch (RemoteException e) {      
	      MensajeBean beanM = new MensajeBean();
	      if ("pe.gob.sunat.framework.core.ejb.FacadeException".equals(e.detail.getClass().getName())) {
	        beanM = ((pe.gob.sunat.framework.core.ejb.FacadeException) e.detail).getMensaje();
	      } else {
	      	log.error(e.getMessage());
	        log.error("Error", e);
	        beanM.setError(true);
	        beanM.setMensajeerror("Ha ocurrido un error al eliminar una programacion de vacaciones");
	        beanM.setMensajesol("Por favor intente nuevamente.");
	      }
	      throw new DelegateException(this, beanM);
	    } finally {
	    }
	    return listaProgramacion;
	  } 
	  
	  /**
	   * Metodo encargado de generar el reporte de trabajadores que faltan programar sus vacaciones
	   * @param Map datos
	   * @return List
	   * @throws DelegateException
	   */  
	  public List generarRepVacProgramadas(Map datos) throws  DelegateException {
	    try {
	      return cpf.generarRepVacProgramadas(datos);
	    } catch (RemoteException e) {      
	      MensajeBean beanM = new MensajeBean();
	      if ("pe.gob.sunat.framework.core.ejb.FacadeException".equals(e.detail.getClass().getName())) {
	        beanM = ((pe.gob.sunat.framework.core.ejb.FacadeException) e.detail).getMensaje();
	      } else {
	      	log.error(e.getMessage());
	        log.error("Error", e);
	        beanM.setError(true);
	        beanM.setMensajeerror("Ha ocurrido un error al generar el reporte de consulta de trabajadores");
	        beanM.setMensajesol("Por favor intente nuevamente.");
	      }
	      throw new DelegateException(this, beanM);
	    } finally {
	    }
	  } 
	  
	  /**
		 * M�todo encargado de la b�squeda de par�metros activos filtrados por
		 * un criterio y valor espec�ficos
		 * 
		 * @param params          HashMap. Contiene los par�metros de la b�squeda
		 * @return                ArrayList conteniendo los registros cargados
		 *                        en HashMaps
		 * @throws DelegateException
		 * 
		 * */
		public List buscarT02(Map params) throws DelegateException {
			List lista = null;
			
			try {
				lista = cpf.joinWithT02T12(params);
			} catch (Exception e) {
				MensajeBean beanM = new MensajeBean();
				
				log.error(e);
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error inesperado en buscar parametros.");
				beanM.setMensajesol("Por favor intente nuevamente ejecutar la opcion, en"
						.concat("caso de continuar con el problema, comuniquese con nuestro webmaster."));
				throw new DelegateException(this, beanM);
			} finally {
				
			}
			
			return lista;		
		}
		
		/**
	   * M�todo encargado de la b�squeda de par�metros activos filtrados por
	   * un criterio y valor espec�ficos
	   * @param params Map. Contiene los par�metros de la b�squeda
	   * @return List conteniendo los registros cargados
	   * en HashMaps
	   * @throws DelegateException
	   * */
	  public List buscarUOeIntendencia(Map params) throws DelegateException {
	    List lista = null;
	    try {
	      lista = cpf.buscarUOeIntendencia(params);
	    } catch (RemoteException e) {
	      MensajeBean beanM = new MensajeBean();
	      if (e.detail.getClass().getName().equals("pe.gob.sunat.framework.core.ejb.FacadeException")) {
	        beanM = ((pe.gob.sunat.framework.core.ejb.FacadeException) e.detail).getMensaje();
	      } else {
	        log.error(e.getMessage());
	        log.error("Error", e);
	        beanM.setError(true);
	        beanM.setMensajeerror("Ha ocurrido un error al hacer la busqueda.");
	        beanM.setMensajesol("Por favor intente nuevamente.");
	        throw new DelegateException(this, beanM);
	      }
	    } finally {
	    }
	    return lista;    
	  }  
}
