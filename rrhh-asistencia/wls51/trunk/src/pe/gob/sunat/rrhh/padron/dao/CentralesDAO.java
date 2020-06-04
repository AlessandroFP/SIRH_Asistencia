package pe.gob.sunat.rrhh.padron.dao;

import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;

/** 
 * <p> Title: CentralesDAO</p> 
 * <p> Copyright: Copyright (c) 2007 </p>
 * <p> Company: SUNAT </p>
 * 
 * @author PRAC-JCALLO
 * @version 1.0
 *
 **/

public class CentralesDAO extends DAOAbstract{
	
	protected final Logger log = Logger.getLogger(getClass());
	private DataSource dataSource = null;

	private final StringBuffer findByCodLocal = new StringBuffer(" SELECT cod_local , ddn , numero from centrales ")
			.append(" where cod_local = ? order by ddn ");		
		
	/**
	 * @param datasource Object
	*/
	public CentralesDAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.dataSource = (DataSource)datasource;
	    else if (datasource instanceof String)
	    	this.dataSource = getDataSource((String)datasource);
	    else
	    	throw new DAOException(this, "Datasource no valido");
	}
	
	/**
	 * metodo findByCodLocal : busqueda de codigo de ciudad y numero de la central por codigo de local
	 * 
	 * **/
	public List findByCodLocal(String cod_local) throws DAOException{
				
		cod_local = cod_local.replaceAll("'", "''");
		
		List lista = executeQuery(dataSource, findByCodLocal.toString(), new Object[]{cod_local});
		if(log.isDebugEnabled()) log.debug("total resultado:"+lista.size());
		
		return lista;		
	}
}
