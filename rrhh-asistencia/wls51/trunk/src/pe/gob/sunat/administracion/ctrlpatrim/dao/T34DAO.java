package pe.gob.sunat.administracion.ctrlpatrim.dao;

import java.util.Map;

import javax.sql.DataSource;
import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;

/**
 * Clase       : T34DAO 
 * Descripcion : DAO de la tabla t34local
 * Proyecto    : Consultas Sisa 
 * Autor       : JGARCIA9
 * Fecha       : 03-Oct-2006
 */

public class T34DAO extends DAOAbstract {
	
	private DataSource dataSource;
	
	public static final StringBuffer FIND_BY_CODLOCAL = new StringBuffer("SELECT t34cod_local,t34nom_local FROM t34local WHERE t34cod_local = ? ");
	
	public T34DAO() {
	    super();
	}
	
	/**
	 * @param datasource Object
	*/
	public T34DAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.dataSource = (DataSource)datasource;
	    else if (datasource instanceof String)
	    	this.dataSource = getDataSource((String)datasource);
	    else
	    	throw new DAOException(this, "Datasource no valido");
	}
	
	/**
	 * Metodo findByCodLocalHash
	 * @param String valor
	 * @return Map res
	 * @throws DAOException
	 */
	public Map findByCodLocalHash(String valor) throws DAOException {
		
		Map res = executeQueryUniqueResult(dataSource, FIND_BY_CODLOCAL.toString(), new Object[]{valor});
		
		return res;
	}
}