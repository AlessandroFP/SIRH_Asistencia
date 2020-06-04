package pe.gob.sunat.rrhh.formativa.ejb;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pe.gob.sunat.batch.dao.QueueDAO;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.core.ejb.FacadeException;
import pe.gob.sunat.framework.core.ejb.StatelessAbstract;
import pe.gob.sunat.framework.core.pattern.ServiceLocator;
import pe.gob.sunat.rrhh.formativa.dao.T3339DAO;
import pe.gob.sunat.sol.BeanMensaje;
import pe.gob.sunat.sol.IncompleteConversationalState;

/**
 * 
 * @ejb.bean name="AsisForFacadeEJB"
 *           description="AsisForFacade"
 *           jndi-name="ejb/rrhh/facade/rrhh/formativa/AsisForFacadeAsistencia"
 *           type="Stateless" view-type="remote"
 * 
 * @ejb.home remote-class="pe.gob.sunat.rrhh.formativa.ejb.AsisForFacadeHome"
 *           extends="javax.ejb.EJBHome"
 * 
 * @ejb.interface remote-class="pe.gob.sunat.rrhh.formativa.ejb.AsisForFacadeRemote"
 *                extends="javax.ejb.EJBObject"
 * 
 * @ejb.resource-ref res-ref-name="jdbc/dcsp" res-type="javax.sql.DataSource" res-auth="Container"
 * @weblogic.resource-description res-ref-name="jdbc/dcsp" jndi-name="jdbc/dcsp"
 * 
 * @ejb.transaction-type type="Container"
 * 
 * @jboss.container-configuration name="Standard Stateless SessionBean"
 * @jboss.version="3.2" 
 * @weblogic.enable-call-by-reference True
 * @weblogic.clustering home-is-clusterable="True" stateless-bean-is-clusterable="True"
 * @weblogic.transaction-descriptor trans-timeout-seconds = "300"
 * @weblogic.cache max-beans-in-cache="100"
 * @weblogic.pool max-beans-in-free-pool="100" initial-beans-in-free-pool="0"
 * 
 * @version 1.0
 */
public class AsisForFacade extends StatelessAbstract {
	
	private final Log log = LogFactory.getLog(getClass());	
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ArrayList marcaciones(Map datos) throws FacadeException {		
		String fechaIni = (String) datos.get("fechaIni");
		String fechaFin = (String) datos.get("fechaFin");		
		//log.debug("Dentro de Dao");
		ArrayList reporte = null;
				
		try {
			T3339DAO asisforDAO = new T3339DAO(ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dcsp"));			
			List lista = asisforDAO.getPracticantes(datos);
			List modalidades = asisforDAO.getModalidades();
			
			//log.debug("lista: " + lista);
			if (lista != null) {
				reporte = new ArrayList();
				Map quiebre ;
				List detalle = null;
				Map params = new HashMap();
				
				for (int i = 0; i < lista.size(); i++) {
					quiebre = (HashMap) lista.get(i);					
					//log.debug("Modalidades!!!!!!!!!!: " + modalidades);
					if(modalidades != null){
						int k;
						boolean encontrado = false; 
						for(k = 0; k < modalidades.size(); k++){
							HashMap mapa = (HashMap) modalidades.get(k);
							//log.debug("K:" + k);
							//log.debug("cod_modalidad" + quiebre.get("cod_modalidad").toString());
							//log.debug("t99codigo" + mapa.get("t99codigo").toString());
							if(quiebre.get("cod_modalidad").toString().trim().equals(mapa.get("t99codigo").toString().trim())){
								//log.debug("Entro!!!");
								quiebre.put("modalidad", mapa.get("t99descrip").toString());
								encontrado = true;
							}
						}
						if(!encontrado){
							quiebre.put("modalidad", "No Especificada");
							//log.debug("No Entro!!!");
						}
					}
					
					params.put("fechaIni", fechaIni);
					params.put("fechaFin", fechaFin);
					params.put("cod_registro", quiebre.get("cod_registro"));
					detalle = asisforDAO.findMarcaciones(params);

					//EBV Prioridad 1 Solo mostrar si tiene Marcaciones					
					if ((detalle != null ) && (detalle.size() > 0)) {					
						//insertamos el detalle del quiebre
						quiebre.put("detalle",detalle);
						//insertamos el quiebre al reporte
						reporte.add(quiebre);
					}
				}
			}
		}  catch (DAOException d) {
			log.error("*** SQL Error ****",d);			
			StringBuffer msg = new StringBuffer().append("Error en la grabacion. T3464DAO ")
												 .append(d.toString());
        	throw new FacadeException(this, msg.toString());
        } catch (Exception e) {
			throw new FacadeException(this, e.toString());
		}
		return reporte;
	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void masivoMarcaciones(Map params)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {
			
			String fechaIni = (String) params.get("fechaIni");
			String fechaFin = (String) params.get("fechaFin");
			String usuario = (String) params.get("usuario");
			params.put("observacion", "Reporte de Marcaciones del " + fechaIni + " al " + fechaFin);
			
			QueueDAO qd = new QueueDAO();			

			qd.encolaReporte(AsisForMasivoFacadeHome.JNDI_NAME, "marcaciones", (HashMap) params, usuario);
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
	}
}