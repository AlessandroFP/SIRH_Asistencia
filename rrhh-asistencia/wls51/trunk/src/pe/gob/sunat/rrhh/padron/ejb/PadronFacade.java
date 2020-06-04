package pe.gob.sunat.rrhh.padron.ejb;

/**
 * <p> Title: Consulta Ficha Padron</p>
 * <p> Description: Componente ejb que realiza consulta de datos de ficha padron</p>
 * <p> Copyright: Copyright (c) 2007 </p>
 * <p> Company: SUNAT </p>
 * 
 * @author PRAC-JCALLO
 * @version 1.0
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pe.gob.sunat.framework.core.bean.MensajeBean;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.core.ejb.FacadeException;
import pe.gob.sunat.framework.core.ejb.StatelessAbstract;
import pe.gob.sunat.framework.core.pattern.ServiceLocator;
import pe.gob.sunat.framework.util.cache.dao.ParamDAO;
import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.rrhh.dao.RegistroSirhDAO;
import pe.gob.sunat.rrhh.dao.T02DAO;
import pe.gob.sunat.rrhh.dao.T02FotoDAO;
import pe.gob.sunat.rrhh.dao.T12DAO;
import pe.gob.sunat.administracion.ctrlpatrim.dao.T75DAO;
import pe.gob.sunat.rrhh.dao.TelefonoDAO;

/**
 * 
 * @ejb.bean name="PadronFacade"
 * 			 description="Clase que realiza consulta de ficha padron"
 * 			 jndi-name="ejb/facade/rrhh/padron/PadronFacadeAsistencia"
 *           type="Stateless" view-type="remote"
 * 
 * @ejb.home remote-class="pe.gob.sunat.rrhh.padron.ejb.PadronFacadeHome"
 *           extends="javax.ejb.EJBHome"
 * 
 * @ejb.interface remote-class="pe.gob.sunat.rrhh.padron.ejb.PadronFacadeRemote"
 * 				  extends="javax.ejb.EJBObject"
 * 
 * @ejb.resource-ref res-ref-name="jdbc/dcsp" res-type="javax.sql.DataSource" res-auth="Container"
 * @weblogic.resource-description res-ref-name="jdbc/dcsp" jndi-name="jdbc/dcsp"
 * 
 * @ejb.resource-ref res-ref-name="jdbc/dgsp" res-type="javax.sql.DataSource" res-auth="Container"
 * @weblogic.resource-description res-ref-name="jdbc/dgsp" jndi-name="jdbc/dgsp"
 * 
 * @ejb.resource-ref res-ref-name="jdbc/dcsa" res-type="javax.sql.DataSource" res-auth="Container"
 * @weblogic.resource-description res-ref-name="jdbc/dcsa" jndi-name="jdbc/dcsa"
 * 
 * @ejb.resource-ref res-ref-name="jdbc/dctelef" res-type="javax.sql.DataSource" res-auth="Container"
 * @weblogic.resource-description res-ref-name="jdbc/dctelef" jndi-name="jdbc/dctelef"
 * 
 * @weblogic.enable-call-by-reference True
 * @weblogic.clustering home-is-clusterable="True" stateless-bean-is-clusterable="True"
 * @weblogic.pool max-beans-in-free-pool="10" initial-beans-in-free-pool="5"
 * @weblogic.transaction-descriptor trans-timeout-seconds = "300"
 *  
 */

public class PadronFacade extends StatelessAbstract {
		
	/**
	 * Metodo findBycodPers
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * 
	 * @param String codPers
	 * @return ArrayList mRpta
	 * @throws FacadeException
	 */  
	public Map findByCodPers(String codPers) throws FacadeException
	{
		Map mRpta = null;
		MensajeBean msg = new MensajeBean();
		try
		{
			ServiceLocator sl = ServiceLocator.getInstance();
			T02DAO persDAO = new T02DAO(sl.getDataSource("java:/comp/env/jdbc/dcsp"));			
			mRpta = persDAO.findByRegistro(codPers);			
		} catch (DAOException e) {
			log.error(e);
			throw new FacadeException (this,e.getMensaje());
		} catch(Exception e){
			log.error(e);
			msg.setError(true);
			msg.setMensajeerror(e.getMessage());
			msg.setMensajesol("Por favor intente nuevamente ejecutar la opcion, en caso de continuar con el problema, comuniquese con nuestro webmaster.");
			throw new FacadeException(this,msg);			
		} finally{
			
		}
		return mRpta;
	}

	/**
	 * Metodo fichaPersonaPadron
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * 
	 * @param Map params 
	 * @return HashMap mRpta
	 * @throws FacadeException
	 */
	public Map fichaPersonaPadron(Map params) throws FacadeException
	{
		Map mRpta = new HashMap();
		Map datasources = new HashMap();
		MensajeBean msg = new MensajeBean();		
		try
		{
			ServiceLocator sl = ServiceLocator.getInstance();			
			String codReg = params.get("codReg").toString();
			String codUorg = params.get("codUorg").toString();
			String codUsu = params.get("codUsu").toString();
			String ip = params.get("ip").toString();
			String pc = params.get("pc").toString();
			String accion = params.get("accion").toString();
			String valor = params.get("valor").toString();
			Map fileFoto = null;
			
			T02FotoDAO foto = new T02FotoDAO(sl.getDataSource("java:/comp/env/jdbc/dcsp"));			
			fileFoto = foto.cargar(codReg);
			
			T12DAO daospt12 = new T12DAO(sl.getDataSource("java:/comp/env/jdbc/dcsp"));
			Map   Jefaturas = daospt12.findByJefe(codUorg, codReg);
			TelefonoDAO daotelefono = new TelefonoDAO(sl.getDataSource("java:comp/env/jdbc/dctelef"));
			List Telefonos = daotelefono.findTelefonos(codReg);	
			datasources.put("dscsa", sl.getDataSource("java:comp/env/jdbc/dcsa"));
			datasources.put("dsctelef", sl.getDataSource("java:comp/env/jdbc/dctelef"));			
			/** se le pasa como paremetro un HashMap con los dos data source (sa, sp)**/
			
			T75DAO daosat75 = new T75DAO(datasources);
			
		  	/** el  objetivo de este metodo es lista todos los locales de por cada unidad organizacional**/
			List Locales   = daosat75.findByCodUorgan(codUorg);
			mRpta.put("mapFoto", fileFoto);
			mRpta.put("Jefaturas",Jefaturas);
	  		mRpta.put("Locales",Locales);
	  		mRpta.put("Telefonos",Telefonos);
			/** aqui se agrega el historial **/
			RegistroSirhDAO sirhDAO = new RegistroSirhDAO(sl.getDataSource("java:comp/env/jdbc/dgsp"));
			FechaBean fechaBean = new FechaBean();
			
			Map mapa = new HashMap();			
			mapa.put("codReg",codReg);
			mapa.put("codUsu",codUsu);
			mapa.put("ip", ip);
			mapa.put("pc", pc);
			mapa.put("accion", accion);
			mapa.put("fecha", fechaBean.getSQLDate());
			mapa.put("hora", fechaBean.getHora24()+":"+fechaBean.getMinuto()+":"+fechaBean.getSegundo());
			mapa.put("valor", valor);
			sirhDAO.insertLogSirh(mapa);
		} catch (DAOException e) {
			log.error(e);
			throw new FacadeException (this,e.getMensaje());
		} catch(Exception e){
			log.error(e);
			msg.setError(true);
			msg.setMensajeerror(e.getMessage());
			msg.setMensajesol("Por favor intente nuevamente ejecutar la opcion, en caso de continuar con el problema, comuniquese con nuestro webmaster.");
			throw new FacadeException(this,msg);			
		} finally{
			
		}
		return mRpta;
	}
	
	/**
	 * Metodo fichaPersonaPadronExt
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * 
	 * @param HashMap params 
	 * @return HashMap mRpta
	 * @throws FacadeException
	 */
	public Map fichaPersonaPadronExt(Map params) throws FacadeException
	{
		Map mRpta = new HashMap();
		Map datasources = new HashMap();
		MensajeBean msg = new MensajeBean();		
		try
		{
			ServiceLocator sl = ServiceLocator.getInstance();
			String codReg = params.get("codReg").toString();
			String codUorg = params.get("codUorg").toString();
			
			T12DAO daospt12 = new T12DAO(sl.getDataSource("java:/comp/env/jdbc/dcsp"));			
			Map Jefaturas = daospt12.findByJefe(codUorg, codReg);			
			
			TelefonoDAO daotelefono = new TelefonoDAO(sl.getDataSource("java:comp/env/jdbc/dctelef"));			
			List Telefonos = daotelefono.findTelefonos(codReg);			
			
			datasources.put("dscsa", sl.getDataSource("java:comp/env/jdbc/dcsa"));
			datasources.put("dsctelef", sl.getDataSource("java:comp/env/jdbc/dctelef"));			
			/** se le pasa como paremetro un HashMap con los dos data source (sa, sp)**/
			
			T75DAO daosat75 = new T75DAO(datasources);
			
		  	/** el  objetivo de este metodo es lista todos los locales de por cada unidad organizacional**/
			List Locales   = daosat75.findByCodUorgan(codUorg);
			
			mRpta.put("Jefaturas",Jefaturas);
	  		mRpta.put("Locales",Locales);
	  		mRpta.put("Telefonos",Telefonos);
						
		} catch (DAOException e) {
			log.error(e);
			throw new FacadeException (this,e.getMensaje());
		} catch(Exception e){
			log.error(e);
			msg.setError(true);
			msg.setMensajeerror(e.getMessage());
			msg.setMensajesol("Por favor intente nuevamente ejecutar la opcion, en caso de continuar con el problema, comuniquese con nuestro webmaster.");
			throw new FacadeException(this,msg);			
		} finally{
			
		}
		return mRpta;
	}

	/**
	 * Metodo joinWithT02T12
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param Map params
	 * @return ArrayList listados
	 * @throws FacadeException
	 */
	public List joinWithT02T12(Map params) throws FacadeException
	{
		List listados = new ArrayList();
		MensajeBean msg = new MensajeBean();
		try
		{
			ServiceLocator sl = ServiceLocator.getInstance();
			// datasources de parametro
			Map dsParams = new HashMap();
			dsParams.put("dscSp", sl.getDataSource("java:/comp/env/jdbc/dcsp"));
			dsParams.put("dscTelef", sl.getDataSource("java:comp/env/jdbc/dctelef"));
			TelefonoDAO dao = new TelefonoDAO(dsParams);			
			listados = dao.joinWithT02T12(params);						
		} catch (DAOException e) {
			log.error(e);
			throw new FacadeException (this,e.getMensaje());
		} catch(Exception e){
			log.error(e);
			msg.setError(true);
			msg.setMensajeerror(e.getMessage());
			msg.setMensajesol("Por favor intente nuevamente ejecutar la opcion, en caso de continuar con el problema, comuniquese con nuestro webmaster.");
			throw new FacadeException(this,msg);			
		} finally{
			
		}
		return listados;
	}
	
	/**
	 * metodo buscar: que se encarga de buscar parametros como es categoria y unidad organizacional
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * 
	 * @param Map params
	 * @return ArrayList listados
	 * @throws FacadeException
	 */
	public List buscar(Map params) throws FacadeException {
		List listados = null;
		MensajeBean msg = new MensajeBean();
		try 
		{	
			String opcion = ((String)params.get("opcion")).trim();			
			if (opcion.equals("uor")) 
			{
				ServiceLocator sl = ServiceLocator.getInstance();
				T12DAO daospt12 = new T12DAO(sl.getDataSource("java:/comp/env/jdbc/dcsp"));
				listados = daospt12.findByBusquedaGen(params);
			}
			if (opcion.equals("cat")) 
			{
				ServiceLocator sl = ServiceLocator.getInstance();
				String criterio = params.get("criterio").toString();
				String valor = params.get("valor").toString();
				
				ParamDAO paramDAO = new ParamDAO();
				
				StringBuffer query = new StringBuffer(" SELECT t99codigo, t99descrip, t99abrev, t99siglas ")
											.append(" FROM t99codigos WHERE t99cod_tab = '001' and t99tip_desc = 'D'");
				//por codigo
				if(criterio.equalsIgnoreCase("0")){					
					query.append(" and t99codigo = '" + valor.toUpperCase().replaceAll("'", "''") + "' ");
				}
				//por descripcion larga
				if(criterio.equalsIgnoreCase("1")){					
					query.append(" and t99descrip like '%" + valor.toUpperCase().replaceAll("'", "''") + "%' ");
				}
				//por descripcion corta
				if(criterio.equalsIgnoreCase("2")){
					query.append(" and t99abrev like '%" + valor.toUpperCase().replaceAll("'", "''") + "%' ");
				}
				
				query.append(" order by t99descrip");
								
				listados = (List)paramDAO.cargarNoCache(query.toString(), sl.getDataSource("java:/comp/env/jdbc/dcsp"), paramDAO.LIST);				
				if(log.isDebugEnabled()) log.debug("tam listados: "+listados.size());				
			}
		}
		catch (DAOException e) {
			log.error(e);
			throw new FacadeException (this,e.getMensaje());
		} catch(Exception e){
			log.error(e);
			msg.setError(true);
			msg.setMensajeerror(e.getMessage());
			msg.setMensajesol("Por favor intente nuevamente ejecutar la opcion, en caso de continuar con el problema, comuniquese con nuestro webmaster.");
			throw new FacadeException(this,msg);			
		} finally{
			
		}
		return listados;
	}	
}
