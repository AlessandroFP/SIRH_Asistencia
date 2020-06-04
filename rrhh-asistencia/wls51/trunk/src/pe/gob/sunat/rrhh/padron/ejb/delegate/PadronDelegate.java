package pe.gob.sunat.rrhh.padron.ejb.delegate;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import pe.gob.sunat.framework.core.pattern.ServiceLocator;
import pe.gob.sunat.framework.core.pattern.ServiceLocatorException;
import pe.gob.sunat.framework.core.bean.MensajeBean;
import pe.gob.sunat.framework.core.ejb.DelegateException;
import pe.gob.sunat.rrhh.padron.ejb.PadronFacadeHome;
import pe.gob.sunat.rrhh.padron.ejb.PadronFacadeRemote;

/**
 * <p>Title: PadronDelegate</p>
 * <p>Description: Clase encargada de administrar las llamadas del servlet para
 *                 la realizaci�n de las consultas de Fichas de Padron.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: SUNAT</p>
 * @author PRAC-JCALLO	
 * @version 1.0
 */

public class PadronDelegate {
	
	private static Logger log = Logger.getLogger(PadronDelegate.class);
	private PadronFacadeRemote pf;
	
	public PadronDelegate()throws DelegateException{
		try {
			PadronFacadeHome facadeHome = (PadronFacadeHome)ServiceLocator.
          		getInstance().getRemoteHome( PadronFacadeHome.JNDI_NAME,
          				PadronFacadeHome.class);
			pf = facadeHome.create();	
		} catch (ServiceLocatorException e) {
			throw new DelegateException(this,e.getMessage());
		} catch (Exception e) {
			MensajeBean beanM = new MensajeBean();
			
			log.error(e);			
			beanM.setError(true);
			beanM.setMensajeerror("Ha ocurrido un error inesperado ");
			beanM.setMensajesol("Por favor intente nuevamente ejecutar la opcion,"
					.concat("en caso de continuar con el problema, comuniquese con nuestro webmaster."));
			throw new DelegateException(this, beanM);
		} finally {
			
		}
		
	}
		
	/**
	 * M�todo encargado buscar unidades organizacionales filtradas por los
	 * par�metros contenidos en el HashMap.
	 * 
	 * @param codPers         String 
	 * @return                Map.
	 * @throws DelegateException
	 * 
	 * */
	public Map findByCodPers(String codPers) throws DelegateException{
		Map rpta = null;
		
		try {
			rpta = pf.findByCodPers(codPers);
		} catch (Exception e) {
			MensajeBean beanM = new MensajeBean();
			
			log.error(e);
			beanM.setError(true);
			beanM.setMensajeerror("Ha ocurrido un error inesperado en buscar unidades.");
			beanM.setMensajesol("Por favor intente nuevamente ejecutar la opcion, en"
					.concat("caso de continuar con el problema, comuniquese con nuestro webmaster."));
			throw new DelegateException(this, beanM);
		} finally {
			
		}
		
		return rpta;
	}
	
	/**
	 * M�todo encargado de la b�squeda de par�metros activos filtrados por
	 * un criterio y valor espec�ficos
	 * 
	 * @param params          Map. Contiene los par�metros de la b�squeda
	 * @return                List conteniendo los registros cargados
	 *                        en HashMaps
	 * @throws DelegateException
	 * 
	 * */
	public List buscarT02(Map params) throws DelegateException {
		List lista = null;
		
		try {
			lista = pf.joinWithT02T12(params);
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
	 * M�todo encargado de buscar a un usuario espec�fico por su n�mero de registro
	 * 
	 * @param params          Map. Contiene los par�metros de la b�squeda
	 * @return                Map. Conteniendo los datos del registro encontrado.
	 * @throws DelegateException
	 * 
	 * */	
	public Map fichaPersonaPadron(Map params) throws DelegateException{
		Map res = null;
		
		try {
			res = pf.fichaPersonaPadron(params);
		} catch (Exception e) {
			MensajeBean beanM = new MensajeBean();
			
			log.error(e);			
			beanM.setError(true);
			beanM.setMensajeerror("Ha ocurrido un error inesperado ");
			beanM.setMensajesol("Por favor intente nuevamente ejecutar la opcion,"
					.concat("en caso de continuar con el problema, comuniquese con nuestro webmaster."));
			throw new DelegateException(this, beanM);
		} finally {
			
		}		
		return res;
	}	
	
	/**
	 * M�todo encargado de buscar a un usuario espec�fico por su n�mero de registro externo
	 * 
	 * @param params          Map. Contiene los par�metros de la b�squeda
	 * @return                Map. Conteniendo los datos del registro encontrado.
	 * @throws DelegateException
	 * 
	 * */	
	public Map fichaPersonaPadronExt(Map params) throws DelegateException{
		Map res = null;
		
		try {
			res = pf.fichaPersonaPadronExt(params);
		} catch (Exception e) {
			MensajeBean beanM = new MensajeBean();
			
			log.error(e);			
			beanM.setError(true);
			beanM.setMensajeerror("Ha ocurrido un error inesperado ");
			beanM.setMensajesol("Por favor intente nuevamente ejecutar la opcion,"
					.concat("en caso de continuar con el problema, comuniquese con nuestro webmaster."));
			throw new DelegateException(this, beanM);
		} finally {
			
		}		
		return res;
	}
	
	/**
	 * M�todo encargado de la b�squeda de par�metros activos filtrados por
	 * un criterio y valor espec�ficos
	 * 
	 * @param params          Map. Contiene los par�metros de la b�squeda
	 * @return                List conteniendo los registros cargados
	 *                        en HashMaps
	 * @throws DelegateException
	 * 
	 * */
	public List buscar(Map params) throws DelegateException {
		List lista = null;
		
		try {
			lista = pf.buscar(params);
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
}