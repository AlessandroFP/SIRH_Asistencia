package pe.gob.sunat.rrhh.formativa.ejb;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import pe.gob.sunat.batch.dao.QueueDAO;
import pe.gob.sunat.framework.core.pattern.ServiceLocator;
import pe.gob.sunat.utils.Utiles;

/**
 * 
 * @ejb.bean name="AsisForMasivoFacadeEJB" description="AsisForMasivoFacade"
 *           jndi-name="ejb/rrhh/facade/rrhh/formativa/AsisForMasivoFacadeEJB"
 *           type="Stateless" view-type="remote"
 * 
 * @ejb.home remote-class="pe.gob.sunat.rrhh.formativa.ejb.AsisForMasivoFacadeHome"
 *           extends="javax.ejb.EJBHome"
 * 
 * @ejb.interface remote-class="pe.gob.sunat.rrhh.formativa.ejb.AsisForMasivoFacadeRemote"
 *                extends="javax.ejb.EJBObject"
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
public class AsisForMasivoFacade extends QueueDAO implements SessionBean{

	private SessionContext sessionContext;

	public void ejbCreate() {		
	}

	public void ejbRemove() {
	}

	public void ejbActivate() {
	}

	public void ejbPassivate() {
	}

	public void setSessionContext(SessionContext sessionContext) {
		this.sessionContext = sessionContext;
	}

	/**
	* Obtiene las MArcaciones Masivas de los Formativas
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param params HashMap
           * @param usuario String
           * @return
	* @throws RemoteException
	 */
	public void marcaciones(HashMap params, String usuario)
			throws RemoteException {

		String id = (String) params.get("messageID");
		String dbpool = (String) params.get("dbpool");
		//String usuario = (String) params.get("usuario");
		String linea = "";
		try {

			super.creaReporte(id, "MARCACIONES", (HashMap) params, usuario);		

			AsisForFacadeHome facadeHome = (AsisForFacadeHome) ServiceLocator
					.getInstance().getRemoteHome(AsisForFacadeHome.JNDI_NAME,
							AsisForFacadeHome.class);
			AsisForFacadeRemote facadeRemote = facadeHome.create();			
			
			ArrayList reporte = facadeRemote.marcaciones((Map)params);
			String unidad = "";

			if (reporte != null) {

				this.escribe("", "MARCACIONES", (HashMap) params, usuario);
				linea = Utiles.formateaCadena("Formativa", 30, true)
						+ Utiles.formateaCadena("Fecha", 30, true)
						+ Utiles.formateaCadena("Hora", 30, true)
						+ Utiles.formateaCadena("Reloj", 30, true);
				this.escribe(linea, "MARCACIONES", (HashMap) params, usuario);
				this.escribe("", "MARCACIONES", (HashMap) params, usuario);

				HashMap quiebre = new HashMap();
				HashMap detalle = new HashMap();
				ArrayList detalles = null;
				String doc;
				for (int i = 0; i < reporte.size(); i++) {

					quiebre = (HashMap) reporte.get(i);
					detalles =  (ArrayList)quiebre.get("detalle");

					//Quiebre por unidad...
					if (!unidad.trim().equals(quiebre.get("cod_uorga").toString().trim())) {
						unidad = quiebre.get("cod_uorga").toString().trim();
						if (i != 0) {
							this.escribe("", "MARCACIONES", (HashMap) params, usuario);
						}
						linea = Utiles.formateaCadena("", 2, false) + unidad;
						this.escribe(linea, "MARCACIONES", (HashMap) params, usuario);
						this.escribe("", "MARCACIONES", (HashMap) params, usuario);
					}
					doc = quiebre.get("cod_registro").toString().trim() + " - " + quiebre.get("nom_ape_pat").toString().trim() + " " +
						  quiebre.get("nom_ape_mat").toString().trim() +"," +  quiebre.get("nom_per").toString().trim() + " " +
						  quiebre.get("fec_ini_conv").toString().trim() + " " +quiebre.get("cod_uorga").toString().trim() + " " +
						  " (DNI : " + quiebre.get("num_doc").toString() ;
					
					if(quiebre.get("num_doc_tutor") != null && !quiebre.get("num_doc_tutor").toString().trim().equals("")){
						doc += " Tutor: " + quiebre.get("num_doc_tutor").toString().trim();
					}
					else{
						doc += ": No Registrado";
					} 
					doc += ") ";
		
					if(quiebre.get("modalidad") != null) {
						doc += " - " + quiebre.get("modalidad").toString().trim();
					}
					linea = Utiles.formateaCadena("", 5, false)
							+ doc.trim();
					this.escribe(linea, "MARCACIONES", (HashMap) params, usuario);
					if (detalles != null && detalles.size() > 0) {
						String fechaAux = "";
						for (int j = 0; j < detalles.size(); j++) {
							detalle = (HashMap)detalles.get(j);
							if (!detalle.get("fecha_desc").toString().equals(fechaAux)){
								j--;
								linea = Utiles.formateaCadena("", 30, false)
										+ Utiles.formateaCadena(detalle.get("fecha_desc").toString(), 30, false);
								this.escribe(linea, "MARCACIONES", (HashMap) params,
										usuario);
							} else {
								linea = Utiles.formateaCadena("", 60, false)
										+ Utiles.formateaCadena(detalle.get("hora").toString().trim(), 30, true)
										+ Utiles.formateaCadena(detalle.get("descrip").toString().trim(), 30,
												true);
								this.escribe(linea, "MARCACIONES", (HashMap) params,
										usuario);
							}
							fechaAux = detalle.get("fecha_desc").toString();
						}
					} else {
						linea = Utiles.formateaCadena("", 30, false)
								+ "El formativa no posee marcaciones";
						this.escribe(linea, "MARCACIONES", (HashMap) params, usuario);
					}
					this.escribe("", "MARCACIONES", (HashMap) params, usuario);
				}
			}
		} catch (Exception e) {
			this.escribe("Error : " + e.getMessage(), "MARCACIONES", (HashMap) params,
					usuario);
			log.debug("Error : " + e.getMessage());
		} finally {
			try {
				//registramos el log del reporte
				super.registraReporte(dbpool, (HashMap) params, usuario);
			} catch (Exception e) {
			}
		}
	}

	
}