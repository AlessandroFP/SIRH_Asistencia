//AGONZALESF -PAS20171U230200001 - solicitud de reintegro  
//Para el manejo de archivos permanentes
package pe.gob.sunat.sp.asistencia.ejb;

import java.io.File;
import java.io.FileInputStream;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.core.ejb.StatelessAbstract;
import pe.gob.sunat.framework.core.pattern.ServiceLocator;
import pe.gob.sunat.framework.util.dao.SequenceDAO;
import pe.gob.sunat.sol.BeanMensaje;
import pe.gob.sunat.sol.IncompleteConversationalState;
import pe.gob.sunat.sp.asistencia.dao.T8151DAO;
import pe.gob.sunat.sp.asistencia.dao.T8152DAO;
import pe.gob.sunat.sp.asistencia.dao.TmparchivoDAO;
import pe.gob.sunat.utils.Constantes;

/**
 * 
 * @ejb.bean name="ArchivoFacadeEJB"
 *           description="ArchivoFacade"
 *           jndi-name="ejb/rrhh/facade/sp/asistencia/ArchivoFacadeEJB"
 *           type="Stateless" view-type="remote"
 * 
 * @ejb.home remote-class="pe.gob.sunat.sp.asistencia.ejb.ArchivoFacadeHome"
 *           extends="javax.ejb.EJBHome"
 * 
 * @ejb.interface remote-class="pe.gob.sunat.sp.asistencia.ejb.ArchivoFacadeRemote"
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
 * @weblogic.resource-description jndi-name="jdbc/dcbdseq" res-ref-name="jdbc/dcbdseq"
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
public class ArchivoFacade extends StatelessAbstract {

	private static final long serialVersionUID = 1L;
	private final Log log = LogFactory.getLog(getClass());

	ServiceLocator sl = ServiceLocator.getInstance();

	/**	
	 * Obtener archivo
	 * @param dbPool
	 * @param mapa: Mapa con campos num_archivo , num_arcdet  
	 * @return Mapa con nombre e inputStream con archivo
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
			T8151DAO t8151dao = new T8151DAO(dgsp);
			resp = t8151dao.findArchivoById(numArchivo, numArcdet);
		} catch (SQLException e) {
			log.error("Error al obtener el archivo", e);
			throw new RemoteException(e.getMessage());
		}
		return resp;

	}

	/**	 
	 * Buscar archivos no borrados	
	 * @param dbpool
	 * @param mapa: Mapa con campos num_archivo , num_seqdoc  
	 * @return  lista de archivos
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported" 
	 */
	public List buscarArchivos(String dbpool, Map mapa) throws IncompleteConversationalState, RemoteException {
		List archivos = new ArrayList();
		try {
			log.debug("->Ingreso a buscar detalles de  archivo y documento");
			Integer numArchivo = (Integer) mapa.get("num_archivo"); //obligatorio			
			Integer numSeqDoc = (Integer) mapa.get("num_seqdoc");

			DataSource dgsp = sl.getDataSource(dbpool);
			T8151DAO t8151dao = new T8151DAO(dgsp);
			archivos = t8151dao.findArchivosByNumArchivoNumSeqDoc(numArchivo, numSeqDoc);
		} catch (SQLException e) {
			log.error("Error al obtener el archivo", e);
			throw new RemoteException(e.getMessage());
		}
		return archivos;

	}

	/**
	 * Guarda los archivos temporales a permanentes   
	 * @param dbpool : conexion
	 * @param mapa : mapa con num_archivo
	 * @return Numero de archivos insertados
	 * @throws IncompleteConversationalState
	 * @throws RemoteException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported" 
	 */
	public int registrarEnPermanente(String dbpool, Map mapa) throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException {
		DataSource dgsp = sl.getDataSource(dbpool);
		TmparchivoDAO tmparchivoDAO = new TmparchivoDAO(dgsp);
		int res = 0;
		String usuario = mapa.get("cod_usucrea").toString();
		log.debug("Mapa para archivo cabecera" + mapa);
		try {
		 
			if(mapa!=null ){
				List archivos = tmparchivoDAO.findArchivosByNumArchivo(mapa);
				if (archivos.size() > 0) {
					int resC = insertarArchivoCabecera(dbpool, mapa);
					if (resC > 0) {
						for (int i = 0; i < archivos.size(); i++) {
							log.debug("Mapa para archivo detalle" + archivos);
							((Map) archivos.get(i)).put("cod_usucrea", usuario);
							res = insertarArchivoDetalle(dbpool, (Map) archivos.get(i)); 
						}

					}
				}
			}
			 

		} catch (Exception e) {
			log.error("Error en registrarEnPermanente", e);
			throw new RemoteException(e.getMessage());
		}
		return res;
	}
	
	/**
	 * Insertar Cabecera  
	 * @param dbpool : conexion
	 * @param mapa : mapa con num_archivo
	 * @return Numero de archivos insertados
	 * @throws IncompleteConversationalState
	 * @throws RemoteException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported" 
	 */
	public int insertarArchivoCabecera(String dbpool, Map mapa) throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException {
		DataSource dgsp = sl.getDataSource(dbpool);
		T8152DAO t8152dao = new T8152DAO(dgsp);  
		int res = 0;	
		log.debug("Mapa para archivo"+ mapa); 
		int resCab = t8152dao.insert(mapa); 
		
		return resCab;
	}
	
	/**
	 * Insertar Detalle de archivo  
	 * @param dbpool : conexion
	 * @param mapa : mapa con num_archivo
	 * @return Numero de archivos insertados
	 * @throws IncompleteConversationalState
	 * @throws RemoteException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported" 
	 */
	public int insertarArchivoDetalle(String dbpool, Map mapa) throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException {
		int resCab;
		try {
			DataSource dgsp = sl.getDataSource(dbpool);
			T8151DAO t8151dao = new T8151DAO(dgsp);  
			int res = 0;	
			log.debug("Mapa para archivo"+ mapa);  
			resCab = t8151dao.insert(mapa);
		} catch (SQLException e) {
			log.error("Error en insertarArchivoDetalle", e);
			throw new RemoteException(e.getMessage()); 
		}   
		return resCab;
	}

	/**
	 * Obtener siguiente numero en la secuencia para archivos 
	 * @return siguiente numero de archivo 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported" 
	 */
	public int obtenerNumArchivo( ) {
		int secuencia = 0;
		log.debug("->obtenerNumArchivo");
		DataSource dsSecuence = sl.getDataSource("jdbc/dcbdseq");
		secuencia = new SequenceDAO().getSequence(dsSecuence, "SET8152");
		log.debug("->obtenerNumArchivo " + secuencia);
		return secuencia;
	}

	 

}