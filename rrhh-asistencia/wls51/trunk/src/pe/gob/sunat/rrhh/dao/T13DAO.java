package pe.gob.sunat.rrhh.dao;

import java.util.List;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;

/**
 * 
 * Clase : T13DAO 
 * Autor : RMONTES
 * Fecha : 07/11/2007
 * 
 * Descripcion: Esta clase se creo con la finalidad de obtener los datos de la tabla
 * T13NOTACAT de personal.
 */
public class T13DAO extends DAOAbstract {
	private DataSource dataSource = null;
	
	private static final StringBuffer FIND_BY_CODPERS = new StringBuffer("select n.t13cod_pers, n.t13tip_curs, n.t13num_cat, ")
																 .append("n.t13f_inicio, n.t13f_fin, n.t13n_prome, ")
																 .append("(select ca.t99descrip from t99codigos ca ")
																 .append("where ca.t99cod_tab = '051' and ca.t99tip_desc = 'D' and ")
																 .append("n.t13tip_curs = ca.t99codigo) as t13tip_curs_desc ")
																 .append("from t13notacat n ")
																 .append("where t13cod_pers = ? ")
	 															 .append("Order by t13f_inicio desc, t13f_fin desc");
	/**
	 * @param datasource Object
	 */
	public T13DAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.dataSource = (DataSource)datasource;
	    else if (datasource instanceof String)
	    	this.dataSource = getDataSource((String)datasource);
	    else
	    	throw new DAOException(this, "Datasource no valido");
	}
	
	/**
	 * Metodo que devuelve los datos de un registro especifico de la tabla T13NOTACAT
	 * filtrado por el numero de registro del trabajador.
	 * 
	 * @param codpers
	 * @return
	 */
	public List findByCodPers(String codpers){
		return executeQuery(dataSource, FIND_BY_CODPERS.toString(), new Object[]{codpers});
	}
}
