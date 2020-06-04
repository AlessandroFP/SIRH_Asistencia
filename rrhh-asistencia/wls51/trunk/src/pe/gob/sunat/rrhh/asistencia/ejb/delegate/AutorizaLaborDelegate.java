package pe.gob.sunat.rrhh.asistencia.ejb.delegate;


import java.rmi.RemoteException;

import java.util.Map;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



import pe.gob.sunat.rrhh.asistencia.ejb.AutorizaLaborFacadeHome;
import pe.gob.sunat.rrhh.asistencia.ejb.AutorizaLaborFacadeRemote;
import pe.gob.sunat.framework.core.ejb.DelegateException;
import pe.gob.sunat.framework.core.ejb.FacadeException;
import pe.gob.sunat.framework.core.pattern.ServiceLocator;

/**
 * 
 * Clase       : AutorizaLaborDelegate 
 * Proyecto    : RRHH Asistencia
 * Descripcion : Delegate de Labor Excepcional
 * Autor       : EBENAVID
 * Fecha       : 16-oct-2007
 */

public class AutorizaLaborDelegate {
    private AutorizaLaborFacadeRemote cpf;
    private Log log = LogFactory.getLog(getClass());

    public AutorizaLaborDelegate() throws DelegateException {
        try {
        	AutorizaLaborFacadeHome cpFachome = (AutorizaLaborFacadeHome) ServiceLocator
                    .getInstance().getRemoteHome( AutorizaLaborFacadeHome.JNDI_NAME,
                            AutorizaLaborFacadeHome.class);
            cpf = cpFachome.create();
        } 		catch (Exception e) {
			log.error("*** Error ***",e);
			StringBuffer msg = new StringBuffer("Ha ocurrido un error inesperado al instanciar el delegate.")
			.append("Por favor intente nuevamente ejecutar la opcion, en caso de continuar con el problema, comuniquese con nuestro webmaster.");
			throw new DelegateException(this, msg.toString());
		}

    }


    /**
     * Graba autorizacion de labor excepcional
     * @param datos
     * @return
     * @throws DelegateException
     *    
    public Map registrarAutorizacion(Map datos, Map seguridad)
            throws DelegateException {
        
        try {
             datos = cpf.registrarAutorizacion(datos, seguridad);
        }  catch (FacadeException e) {
			log.error("*** Error ***",e);
			throw new DelegateException(this, e.toString());
		} catch (RemoteException e) {
			log.error("*** Error ***",e);
			StringBuffer msg;
			if (e.detail.getClass().getName().equals(
			"pe.gob.sunat.framework.core.ejb.FacadeException")) {
				msg = new StringBuffer(e.toString());
			} else {
				msg = new StringBuffer("Ha ocurrido un error inesperado en la comunicacion al registrarAutorizacion.")
				.append("Por favor intente nuevamente ejecutar la opcion, en caso de continuar con el problema, comuniquese con nuestro webmaster.");
			}
			throw new DelegateException(this, msg.toString());
		}
      return datos;
    }
*/    
    /**PRAC-ASANCHEZ 26/08/2009
     * Graba y actualiza la autorizacion de labor excepcional
     * @param datos
     * @return
     * @throws DelegateException
     */    
    public Map autorizarLE(Map datos, Map seguridad)
            throws DelegateException {
        
        try {
             datos = cpf.autorizarLE(datos, seguridad);
        }  catch (FacadeException e) {
			log.error("*** Error ***",e);
			throw new DelegateException(this, e.toString());
		} catch (RemoteException e) {
			log.error("*** Error ***",e);
			StringBuffer msg;
			if (e.detail.getClass().getName().equals(
			"pe.gob.sunat.framework.core.ejb.FacadeException")) {
				msg = new StringBuffer(e.toString());
			} else {
				msg = new StringBuffer("Ha ocurrido un error inesperado en la comunicacion al registrarAutorizacion.")
				.append("Por favor intente nuevamente ejecutar la opcion, en caso de continuar con el problema, comuniquese con nuestro webmaster.");
			}
			throw new DelegateException(this, msg.toString());
		}
      return datos;
    }    
    
    /**
     * busca dias de labor excepcional
     * @param datos
     * @return
     * @throws DelegateException
     */    
    public Map buscarAutorizacion(Map datos, Map seguridad)
            throws DelegateException {
        
        try {
             datos = cpf.buscarAutorizacion(datos, seguridad);
        }  catch (FacadeException e) {
			log.error("*** Error ***",e);
			throw new DelegateException(this, e.toString());
		} catch (RemoteException e) {
			log.error("*** Error ***",e);
			StringBuffer msg;
			if (e.detail.getClass().getName().equals(
			"pe.gob.sunat.framework.core.ejb.FacadeException")) {
				msg = new StringBuffer(e.toString());
			} else {
				msg = new StringBuffer("Ha ocurrido un error inesperado en la comunicacion al buscarAutorizacion.")
				.append("Por favor intente nuevamente ejecutar la opcion, en caso de continuar con el problema, comuniquese con nuestro webmaster.");
			}
			throw new DelegateException(this, msg.toString());
		}
      return datos;
    }
    
    /**
     * Graba dia de Compensacion de labor excepcional
     * @param datos
     * @return
     * @throws DelegateException
     */    
    public Map registrarCompensacion(String[] params, Map datos)
            throws DelegateException {
        
        try {
             datos = cpf.registrarCompensacion(params, datos);
        }  catch (FacadeException e) {
			log.error("*** Error ***",e);
			throw new DelegateException(this, e.toString());
		} catch (RemoteException e) {
			log.error("*** Error ***",e);
			StringBuffer msg;
			if (e.detail.getClass().getName().equals(
			"pe.gob.sunat.framework.core.ejb.FacadeException")) {
				msg = new StringBuffer(e.toString());
			} else {
				msg = new StringBuffer("Ha ocurrido un error inesperado en la comunicacion al registrarCompensacion.")
				.append("Por favor intente nuevamente ejecutar la opcion, en caso de continuar con el problema, comuniquese con nuestro webmaster.");
			}
			throw new DelegateException(this, msg.toString());
		}
      return datos;
    }

    //FRD - 01/07/2009
    /**
     * busca dias de labor excepcional
     * @param datos
     * @return
     * @throws DelegateException
     */    
    public Map buscarLENoAutorizadas(Map datos, Map seguridad)
            throws DelegateException {
    	
        try {
             datos = cpf.buscarLENoAutorizadas(datos, seguridad);
        }  catch (FacadeException e) {
			log.error("*** Error ***",e);
			throw new DelegateException(this, e.toString());
		} catch (RemoteException e) {
			log.error("*** Error ***",e);
			StringBuffer msg;
			if (e.detail.getClass().getName().equals(
			"pe.gob.sunat.framework.core.ejb.FacadeException")) {
				msg = new StringBuffer(e.toString());
			} else {
				msg = new StringBuffer("Ha ocurrido un error inesperado en la comunicacion al buscarAutorizacion.")
				.append("Por favor intente nuevamente ejecutar la opcion, en caso de continuar con el problema, comuniquese con nuestro webmaster.");
			}
			throw new DelegateException(this, msg.toString());
		}
      return datos;
    }
    

    
    /**PRAC-ASANCHEZ 26/08/2009
     * Graba autorizacion de labor excepcional
     * @param datos
     * @return
     * @throws DelegateException
     *    
    public Map registrarAutorizacionLE(Map datos, Map seguridad)
            throws DelegateException {
        
        try {
             datos = cpf.registrarAutorizacionLE(datos, seguridad);
        }  catch (FacadeException e) {
			log.error("*** Error ***",e);
			throw new DelegateException(this, e.toString());
		} catch (RemoteException e) {
			log.error("*** Error ***",e);
			StringBuffer msg;
			if (e.detail.getClass().getName().equals(
			"pe.gob.sunat.framework.core.ejb.FacadeException")) {
				msg = new StringBuffer(e.toString());
			} else {
				msg = new StringBuffer("Ha ocurrido un error inesperado en la comunicacion al registrarAutorizacion.")
				.append("Por favor intente nuevamente ejecutar la opcion, en caso de continuar con el problema, comuniquese con nuestro webmaster.");
			}
			throw new DelegateException(this, msg.toString());
		}
      return datos;
    }
*/       
}