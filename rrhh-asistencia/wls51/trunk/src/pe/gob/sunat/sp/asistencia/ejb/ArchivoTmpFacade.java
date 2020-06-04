//AGONZALESF -PAS20171U230200001 - solicitud de reintegro  
//Para el manejo de archivos temporales
package pe.gob.sunat.sp.asistencia.ejb;

import java.io.File;
import java.io.FileInputStream;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pe.gob.sunat.framework.core.ejb.StatelessAbstract;
import pe.gob.sunat.framework.core.pattern.ServiceLocator;
import pe.gob.sunat.sol.BeanMensaje;
import pe.gob.sunat.sol.IncompleteConversationalState;
import pe.gob.sunat.sp.asistencia.dao.TmparchivoDAO;
import pe.gob.sunat.utils.Constantes;

/**
 * 
 * @ejb.bean name="ArchivoTmpFacadeEJB"
 *           description="ArchivoTmpFacade"
 *           jndi-name="ejb/rrhh/facade/sp/asistencia/ArchivoTmpFacadeEJB"
 *           type="Stateless" view-type="remote"
 * 
 * @ejb.home remote-class="pe.gob.sunat.sp.asistencia.ejb.ArchivoTmpFacadeHome"
 *           extends="javax.ejb.EJBHome"
 * 
 * @ejb.interface remote-class="pe.gob.sunat.sp.asistencia.ejb.ArchivoTmpFacadeRemote"
 *                extends="javax.ejb.EJBObject"
 * 
 * @ejb.resource-ref res-ref-name="jdbc/dcsp" res-type="javax.sql.DataSource" res-auth="Container"
 * @weblogic.resource-description res-ref-name="jdbc/dcsp" jndi-name="jdbc/dcsp"
 * 
 * @ejb.resource-ref res-ref-name="jdbc/dgsp" res-type="javax.sql.DataSource" res-auth="Container"
 * @weblogic.resource-description res-ref-name="jdbc/dgsp" jndi-name="jdbc/dgsp"
 * 
 * @ejb.env-entry name="dcsp" type="java.lang.String" value="jdbc/dcsp"
 * @ejb.env-entry name="dgsp" type="java.lang.String" value="jdbc/dgsp"            
 * 
 * @ejb.resource-ref res-ref-name="pool_oracle" res-type="javax.sql.DataSource" res-auth="Container"
 * @jboss.resource-ref res-ref-name="pool_oracle" jndi-name="java:/pool_scad"
 * @weblogic.resource-description res-ref-name="pool_oracle" jndi-name="jdbc/dcscad"
 * 
 * @ejb.transaction-type type="Container"
 * 
 * @jboss.container-configuration name="Standard Stateless SessionBean"
 * @jboss.version="3.2"  
 * @weblogic.enable-call-by-reference True
 * @weblogic.clustering home-is-clusterable="True" stateless-bean-is-clusterable="True"
 * @weblogic.transaction-descriptor trans-timeout-seconds = "300"
 * @weblogic.cache max-beans-in-cache="300"
 * @weblogic.pool max-beans-in-free-pool="300" initial-beans-in-free-pool="10"
 * 
 * @version 1.0
 */
public class ArchivoTmpFacade extends StatelessAbstract {

	private static final long serialVersionUID = 1L;
	private final Log log = LogFactory.getLog(getClass());

	ServiceLocator sl = ServiceLocator.getInstance();
	TmparchivoDAO tmpArchivoDAO;

	/**	
	 * Obtener archivo temporal   
	 * @param dbpool: conexión.
	 * @param mapa: Mapa con campos num_archivo , num_arcdet
	 * @return mapa con nombre del archivo e inputstream
	 * @throws IncompleteConversationalState
	 * @throws RemoteException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public Map descargarArchivo(String dbPool, Map mapa) throws IncompleteConversationalState, RemoteException {
		Map resp = null;
		try {
			log.debug("->Ingreso a descargar archivo ");
			Integer numArchivo = (Integer) mapa.get("num_archivo"); //obligatorio			
			Integer numArcdet = (Integer) mapa.get("num_arcdet");
			log.debug(mapa);
			DataSource dgsp = sl.getDataSource(dbPool);
			tmpArchivoDAO = new TmparchivoDAO(dgsp);
			resp = tmpArchivoDAO.findArchivoById(numArchivo, numArcdet);
		} catch (SQLException e) {
			log.error("Error al obtener el archivo", e);
			throw new RemoteException(e.getMessage());
		}
		return resp;

	}

	/**	 
	 * Buscar archivos temporal activos  
	 * @param dbpool: conexion 
	 * @param mapa:   Mapa con campos num_archivo, num_seqdoc
	 * @return  lista de archivos
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported" 
	 */
	public List buscarArchivos(String dbpool, Map mapa) throws IncompleteConversationalState, RemoteException {
		List archivos = new ArrayList();
		try {
			log.debug("->Ingreso a buscar detalles de  archivo y documento");
			DataSource dgsp = sl.getDataSource(dbpool);
			tmpArchivoDAO = new TmparchivoDAO(dgsp);
			archivos = tmpArchivoDAO.findArchivos(mapa) ;
		} catch (SQLException e) {
			log.error("Error al obtener el archivo", e);
			throw new RemoteException(e.getMessage());
		}
		return archivos;

	}

	/**	
	 * Ingresar  temporal archivo  
	 * @param dbpool: conexion 
	 * @param mapa:   Mapa de datos a ingresar
	 * @return Numero identificador de archivo ingreso
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public int registrarArchivo(String dbpool, Map mapa) throws IncompleteConversationalState, RemoteException {
		BeanMensaje beanM = new BeanMensaje();
		int idResultado = 0;
		try {

			log.debug("Ingreso a registrarArchivoDetalle");
			Integer numArchivo = (Integer) mapa.get("num_archivo"); //obligatorio			

			DataSource dgsp = sl.getDataSource(dbpool);
			tmpArchivoDAO = new TmparchivoDAO(dgsp);
			int idArchivoDet = tmpArchivoDAO.findNext(numArchivo);

			//agregar datos adicionales al mapa del request
			((Map) mapa).put("ind_del", Constantes.EST_ARC_TEMP_PARA_AGREGAR);
			((Map) mapa).put("num_arcdet", new Integer(idArchivoDet));
			String ruta = mapa.get("rut_adjunto").toString();
			((Map) mapa).put("arc_adjunto", new FileInputStream(new File(ruta)));

			int row = tmpArchivoDAO.insertArchivo(mapa);
			log.debug("numero de filas detalles--> " + row);
			if (row > 0) {
				idResultado = idArchivoDet;
			}

		} catch (Exception e) {
			log.error("Error en la insercion de archivo", e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("");
			throw new IncompleteConversationalState(beanM);
		}
		return idResultado;
	}

	/**	 
	 * Cambio de estado  
	 * @param dbpool : conexion
	 * @param mapa : Mapa con campos de  filtro  
	 * @return  Mayor a cero si es exitoso
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public int cambiarEstado(String dbpool, Map mapa,String estado) throws IncompleteConversationalState, RemoteException {
		BeanMensaje beanM = new BeanMensaje();
		int idResultado = 0;
		try {

			log.debug("Ingreso a cambiarEstado");
			DataSource dgsp = sl.getDataSource(dbpool);
			tmpArchivoDAO = new TmparchivoDAO(dgsp);
			idResultado = tmpArchivoDAO.updateState(mapa, estado);
		} catch (Exception e) {
			log.error("Error en cambio de estado de archivo", e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("");
			throw new IncompleteConversationalState(beanM);
		}
		return idResultado;
	}

	/**	 
	 * Elimina fisicamente archivos de acuerdo al filtro
	 * @param dbpool : conexion
	 * @param mapa : Mapa con campos de  filtro  
	 * @return  Mayor a cero si es exitoso
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public int eliminarArchivosTemporales(String dbpool, Map mapa) throws java.rmi.RemoteException {
		BeanMensaje beanM = new BeanMensaje();
		int idResultado = 0;
		try {

			log.debug("Ingreso a eliminarArchivosTemporales");
			DataSource dgsp = sl.getDataSource(dbpool);
			tmpArchivoDAO = new TmparchivoDAO(dgsp);
			idResultado = tmpArchivoDAO.deleteArchivos(mapa);

		} catch (Exception e) {
			log.error("Error en la insercion de archivo", e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("");
			throw new IncompleteConversationalState(beanM);
		}
		return idResultado;
	}

}