//PAS20171U230200001 - solicitud de reintegro  
package pe.gob.sunat.rrhh.planilla.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.utils.Constantes;
/**
 * <p>Title: TipoSalidasDAO </p>
 * <p>Description: Clase para realizar la consulta de la tabla en Oracle TIPO_SALIDAS</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: SUNAT</p>
 * @author AGONZALESF
 * @version 1.0 
 */
public class TipoSalidasDAO extends DAOAbstract {
	private DataSource datasource;

	  private static final String FIND_TIPO_SALIDA_INI = "SELECT CODI_MOVI_TPR,DESC_TIPO_TPR FROM TIPO_SALIDAS WHERE IND_DEVOL in ( ";
	  private static final String FIND_TIPO_SALIDA_FIN = " ) ORDER BY  DESC_TIPO_TPR";
	  
	  
	  private static final String GETALL = "SELECT  CODI_MOVI_TPR,DESC_TIPO_TPR FROM TIPO_SALIDAS ORDER BY  CODI_MOVI_TPR";
 
	/**
	 * Método para validar el Datasource a usarse
	 * @param Object datasource
	 */
	public TipoSalidasDAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.datasource = (DataSource) datasource;
		else if (datasource instanceof String)
			this.datasource = getDataSource((String) datasource);
		else
			throw new DAOException(this, "Datasource no valido");
	}

	 /**
	  * Metodo que obtiene conceptos para devolucion para asistencia
	  * @return
	  * @throws DAOException
	  */
	public List findByTipoDevolucionAsistencia( ) throws DAOException {
		if (log.isDebugEnabled())
			log.debug( " INICIO - findByTipoDevolucionAsistencia()");
		List lista = null; 
	 	String sql =  FIND_TIPO_SALIDA_INI + Constantes.TIPO_SALIDA_ASIS+FIND_TIPO_SALIDA_FIN;
		lista = executeQuery(datasource, sql );
		if (log.isDebugEnabled())
			log.debug( " FIN - findByTipoDevolucionAsistencia() ");
		return lista;
	}
	
	/**
	 * Metodo que obtiene conceptos para devolucion de licencias y permisos
	 * @param params
	 * @return List listaLicencias
	 * @throws DAOException
	 */
	public List findByTipoDevolucionLicencia( ) throws DAOException {
		if (log.isDebugEnabled())
			log.debug( " INICIO - findByTipoDevolucionLicencia()");
		List lista = null; 
		String sql =  FIND_TIPO_SALIDA_INI + Constantes.TIPO_SALIDA_LIC_PER+FIND_TIPO_SALIDA_FIN;
		lista = executeQuery(datasource, sql );
		if (log.isDebugEnabled())
			log.debug( " FIN - findByTipoDevolucionLicencia() ");
		return lista;
	}
	
	/**
	 * Obtiene conceptos para devolucion de subsidios
	 * @param params
	 * @return List listaLicencias
	 * @throws DAOException
	 */
	public List findByTipoDevolucionSubsidio( ) throws DAOException {
		if (log.isDebugEnabled())
			log.debug( " INICIO - findByTipoDevolucionSubsidio()");
		List lista = null; 
		String sql =  FIND_TIPO_SALIDA_INI + Constantes.TIPO_SALIDA_SUB+FIND_TIPO_SALIDA_FIN;
		lista = executeQuery(datasource, sql );
		if (log.isDebugEnabled())
			log.debug( " FIN - findByTipoDevolucionSubsidio() ");
		return lista;
	}
	
	/**
	 * Obtiene conceptos  
	 * @param params
	 * @return List listaLicencias
	 * @throws DAOException
	 */
	public Map getAll( ) throws DAOException {
		if (log.isDebugEnabled())
			log.debug( " INICIO - getAll()");
		List lista = null;
		Map salidas = new HashMap();
		lista = executeQuery(datasource, GETALL);
		for (int i = 0; i < lista.size(); i++) {
			Map item = (Map) lista.get(i);
			salidas.put(item.get("codi_movi_tpr"), item.get("desc_tipo_tpr"));
		}
		if (log.isDebugEnabled())
			log.debug( " FIN - getAll() ");
		return salidas;
	}
	
}
