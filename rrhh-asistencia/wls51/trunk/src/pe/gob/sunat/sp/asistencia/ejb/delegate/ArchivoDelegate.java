//PAS20171U230200001 - solicitud de reintegro  
package pe.gob.sunat.sp.asistencia.ejb.delegate;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pe.gob.sunat.framework.core.pattern.ServiceLocator;
import pe.gob.sunat.sol.BeanMensaje;
import pe.gob.sunat.sol.IncompleteConversationalState;
import pe.gob.sunat.sp.asistencia.ejb.ArchivoFacadeHome;
import pe.gob.sunat.sp.asistencia.ejb.ArchivoFacadeRemote;
import pe.gob.sunat.sp.asistencia.ejb.ArchivoTmpFacadeHome;
import pe.gob.sunat.sp.asistencia.ejb.ArchivoTmpFacadeRemote;

public class ArchivoDelegate {
	private final Log log = LogFactory.getLog(getClass());
	private ArchivoTmpFacadeRemote archivoTmp;
	private ArchivoFacadeRemote archivo; 
	

	public ArchivoDelegate() throws AsistenciaException {
		try {
			ArchivoTmpFacadeHome facadeHome = (ArchivoTmpFacadeHome) ServiceLocator.getInstance().getRemoteHome(ArchivoTmpFacadeHome.JNDI_NAME, ArchivoTmpFacadeHome.class);
			archivoTmp = facadeHome.create();
			
			ArchivoFacadeHome afacadeHome = (ArchivoFacadeHome) ServiceLocator.getInstance().getRemoteHome(ArchivoFacadeHome.JNDI_NAME, ArchivoFacadeHome.class);
			archivo = afacadeHome.create();
		} catch (Exception e) {
			BeanMensaje beanM = new BeanMensaje();
			beanM.setError(true); 
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new AsistenciaException(beanM);
		}
	} 

	/**
	 * Funcion encargada de listar archivos por num_archivo y num_seqdoc
	 * @param dbpool : conexion
	 * @param mapa : mapa de parametros para la accion
	 * @return Lista de archivos temporales
	 * @throws ArchivoException
	 */
	public List buscarArchivos(String dbpool, Map mapa) throws ArchivoException {

		List archivos = null;
		try {

			archivos = archivoTmp.buscarArchivos(dbpool, mapa);
		} catch (IncompleteConversationalState e) {
			throw new AsistenciaException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al buscar los archivos.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new AsistenciaException(beanM);
		}

		return archivos;
	}
	/**
	 * Funcion para registro de un  archivo temporal 
	 * @param dbpool : conexion
	 * @param mapa : mapa de parametros para la accion
	 * @return Mayor a cero si es exitoso
	 */
	public int registrarArchivo(String dbpool, Map mapa) {
		int id =  0;
		try {

			id = archivoTmp.registrarArchivo(dbpool, mapa) ;
		} catch (IncompleteConversationalState e) { 
			throw new AsistenciaException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al registrar el archivos.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new AsistenciaException(beanM);
		}
		return id;
	}
	/**
	 * Funcion encargada de  eliminar logicamente un archivo
	 * @param dbpool : conexion
	 * @param mapa : mapa de parametros para la accion
	 * @return Mayor a cero si es exitoso
	 */
	public int cambiarEstado(String dbpool,  Map mapa,String estado ) {
		int id =  0;
		try {

			id = archivoTmp.cambiarEstado(dbpool,mapa,estado);
		} catch (IncompleteConversationalState e) {  
			throw new AsistenciaException(e.getBeanMensaje()); 
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al buscar los archivos.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new AsistenciaException(beanM);
		}
		return id;
	}

	/**
	 * Funcion para descargar el archivo temporal  
	 * @param dbpool : conexion
	 * @param mapa : mapa de parametros para la accion
	 * @return  Mapa que incluye el nombre del archivo y inputstream   
	 */
	
	public Map descargarArchivo(String dbpool, Map mapa) {
		Map res = new HashMap ();
	 
		 try {
			 res = archivoTmp.descargarArchivo(dbpool,mapa);
			} catch (IncompleteConversationalState e) { 
				throw new AsistenciaException(e.getBeanMensaje());
			} catch (RemoteException e) {
				BeanMensaje beanM = new BeanMensaje();
				if (e.detail.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")) {
					beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
				} else {
					beanM.setError(true);
					beanM.setMensajeerror("Ha ocurrido un error al buscar los archivos.");
					beanM.setMensajesol("Por favor intente nuevamente.");
				}
				throw new AsistenciaException(beanM);
			}
			return res;
	}
	

	
	/**
	 * Funcion encargada de eliminar los archivos temporales
	 * @param dbpool : conexion
	 * @param mapa : mapa de parametros para la accion
	 * @return Mayor a cero si es exitoso
	 */
	public int eliminarArchivosTemporales(String dbpool, Map mapa) {
		int res = -1;
		try { 
			 res = archivoTmp.eliminarArchivosTemporales(dbpool,mapa);
			} catch (IncompleteConversationalState e) { 
				throw new AsistenciaException(e.getBeanMensaje());
			} catch (RemoteException e) {
				BeanMensaje beanM = new BeanMensaje();
				if (e.detail.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")) {
					beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
				} else {
					beanM.setError(true);
					beanM.setMensajeerror("Ha ocurrido un error al buscar los archivos.");
					beanM.setMensajesol("Por favor intente nuevamente.");
				}
				throw new AsistenciaException(beanM);
			}
			return res;	
	}
	
	/**
	 * Funcion  para registro de archivos temporales como permanentes
	 * @param dbpool : conexion
	 * @param mapa : mapa de parametros para la accion
	 * @return Mayor a cero si es exitoso
	 */
	public int registrarEnPermanente(String dbpool, Map mapa) {
		int res = -1;
		try {
			 res = archivo.registrarEnPermanente(dbpool,mapa);
			} catch (IncompleteConversationalState e) { 
				throw new AsistenciaException(e.getBeanMensaje());
			} catch (RemoteException e) {
				BeanMensaje beanM = new BeanMensaje();
				if (e.detail.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")) {
					beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
				} else {
					beanM.setError(true);
					beanM.setMensajeerror("Ha ocurrido un error al buscar los archivos.");
					beanM.setMensajesol("Por favor intente nuevamente.");
				}
				throw new AsistenciaException(beanM);
			}
			return res;
		
	}

	/**
	 * Funcion encargada de listar archivos por num_archivo y num_seqdoc 
	 * @param dbpool : conexion
	 * @param mapa : mapa de parametros para la accion
	 * @return Lista de archivos 
	 */
	public List buscarArchivosFinales(String dbpool, Map mapa) {
		List archivos = null;
		try {

			archivos = archivo.buscarArchivos(dbpool, mapa);
		} catch (IncompleteConversationalState e) {
			throw new AsistenciaException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al buscar los archivos.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new AsistenciaException(beanM);
		}

		return archivos;
	}
	

	/**
	 * Funcion para descargar el archivo de base de datos
	 * @param dbpool : conexion
	 * @param mapa : mapa de parametros para la accion
	 * @return   Mapa que incluye el nombre del archivo y inputstream   
	 */
	public Map descargarArchivoFinal(String dbpool, Map mapa) {
		Map res = new HashMap ();
		 
		 try {
			 res = archivo.descargarArchivo(dbpool,mapa);
			} catch (IncompleteConversationalState e) { 
				throw new AsistenciaException(e.getBeanMensaje());
			} catch (RemoteException e) {
				BeanMensaje beanM = new BeanMensaje();
				if (e.detail.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")) {
					beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
				} else {
					beanM.setError(true);
					beanM.setMensajeerror("Ha ocurrido un error al buscar los archivos.");
					beanM.setMensajesol("Por favor intente nuevamente.");
				}
				throw new AsistenciaException(beanM);
			}
			return res;
	}
	
	/**
	 * Funcion para obtener el siguiente numero de archivo	
	 * @return Siguiente numero de la secuencia de archivo
	 */
	public int obtenerNumArchivo() {
		int res = -1;
		 try {
			 res = archivo.obtenerNumArchivo();
			} catch (IncompleteConversationalState e) { 
				throw new AsistenciaException(e.getBeanMensaje());
			} catch (RemoteException e) {
				BeanMensaje beanM = new BeanMensaje();
				if (e.detail.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")) {
					beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
				} else {
					beanM.setError(true);
					beanM.setMensajeerror("Ha ocurrido un error al buscar los archivos.");
					beanM.setMensajesol("Por favor intente nuevamente.");
				}
				throw new AsistenciaException(beanM);
			}
			return res;
	}
	
}
