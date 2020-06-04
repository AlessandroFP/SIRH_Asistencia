package pe.gob.sunat.rrhh.formativa.ejb.delegate;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import pe.gob.sunat.framework.core.pattern.ServiceLocator;
import pe.gob.sunat.sol.BeanMensaje;
import pe.gob.sunat.sol.IncompleteConversationalState;
import pe.gob.sunat.rrhh.formativa.ejb.AsisForFacadeHome;
import pe.gob.sunat.rrhh.formativa.ejb.AsisForFacadeRemote;
import pe.gob.sunat.sp.asistencia.ejb.ReporteMasivoFacadeHome;
import pe.gob.sunat.sp.asistencia.ejb.ReporteMasivoFacadeRemote;
import pe.gob.sunat.rrhh.formativa.ejb.delegate.AsisForException;

/**
 * <p>
 * Title: AsistenciaDelegate
 * </p>
 * <p>
 * Description: Clase encargada de administrar las invocaciones para las
 * funcionalidades del modulo de reportes
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: Sunat
 * </p>
 * 
 * @author cgarratt
 * @version 1.0
 */
public class AsisForDelegate {

	private AsisForFacadeRemote asisfor;
	
	Logger log = Logger.getLogger(this.getClass());

	public AsisForDelegate() throws AsisForException {
		try {
			log.debug("Instanciando Delegate.");
			AsisForFacadeHome facadeHome = (AsisForFacadeHome) ServiceLocator
					.getInstance().getRemoteHome(AsisForFacadeHome.JNDI_NAME,
							AsisForFacadeHome.class);
			log.debug("Antes de facadeHome.create().");
			asisfor = facadeHome.create();
			log.debug("Despues de facadeHome.create().");
		} catch (Exception e) {
			log.debug("Exception en AsisForDelegate: " + e);
			e.printStackTrace();
			BeanMensaje beanM = new BeanMensaje();
			beanM.setError(true);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new AsisForException(beanM);
		}
	}
	
	/**
	 * 
	 * @param datos
	 * @return @throws
	 *         AsisForException
	 */
	public ArrayList marcaciones(Map datos) throws AsisForException {

		ArrayList lista = null;
		try {			
			log.debug("Antes de: asisfor.marcaciones(datos)");
			log.debug("datos: " + datos);
			lista = (ArrayList)asisfor.marcaciones(datos);
			log.debug("Despues de: asisfor.marcaciones(datos)");
		} catch (IncompleteConversationalState e) {
			throw new AsisForException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al generar reporte de marcaciones.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new AsisForException(beanM);
		}
		return lista;
	}
	
	/**
	 * Genera el reporte masivo de marcaciones
	 * 
	 * @param dbpool
	 * @param params
	 * @param usuario
	 */
	public void masivoMarcaciones(Map params) {

		try {		
			asisfor.masivoMarcaciones(params);
		} catch (IncompleteConversationalState e) {
			throw new AsisForException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al generar reporte masivo de marcaciones.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new AsisForException(beanM);
		}

	}
}