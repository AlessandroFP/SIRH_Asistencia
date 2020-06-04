package pe.gob.sunat.rrhh.dao;

import java.util.List;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;

/**
 * 
 * Clase : T146DAO 
 * Autor : RMONTES
 * Fecha : 07/11/2007
 * 
 * Descripcion: Esta clase se creo con la finalidad de obtener los datos de la tabla
 * T146CAPAIN de personal.
 */
public class T146DAO extends DAOAbstract {
	private DataSource dataSource = null;
	
	private static final StringBuffer FIND_BY_CODPERS = new StringBuffer("select t146cod_pers, t146cent_capa, t146descr_cur, ")
																 .append("t146anho_capa, t146f_inicio, t146f_fin, t146nota ")
																 .append("from t146capain ")
																 .append("where t146cod_pers = ? ")
																 .append("Order by t146f_inicio desc, t146f_fin desc");
	
	private static final StringBuffer FIND_BY_CODPERS2 = new StringBuffer("select t146cod_pers, t146cent_capa, t146descr_cur, ")
	 .append("t146anho_capa, t146f_inicio, t146f_fin, t146nota ")
	 .append("from t146capain ")
	 .append("where t146cod_pers = ? ")
	 .append(" UNION ")	
	 .append("select t146cod_pers, t146cent_capa, t146descr_cur, ")
	 .append("t146anho_capa, t146f_inicio, t146f_fin, t146nota ")
	 .append("from t146capain ")
	 .append("where t146cod_pers = ? ")
	 .append("Order by 5 desc, 6 desc");
	/**
	 * @param datasource Object
	 */
	public T146DAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.dataSource = (DataSource)datasource;
	    else if (datasource instanceof String)
	    	this.dataSource = getDataSource((String)datasource);
	    else
	    	throw new DAOException(this, "Datasource no valido");
	}
	
	/**
	 * Metodo que devuelve los datos de un registro especifico de la tabla T146CAPAIN
	 * filtrado por el numero de registro del trabajador.
	 * 
	 * @param codpers
	 * @return
	 */
	public List findByCodPers(String codpers){
		return executeQuery(dataSource, FIND_BY_CODPERS.toString(), new Object[]{codpers});
	}
	
	public List findByCodPers2(String codpers, String codpersante){
		return executeQuery(dataSource, FIND_BY_CODPERS2.toString(), new Object[]{codpers,codpersante});
	}
}
