package pe.gob.sunat.rrhh.dao;

import java.util.List;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;

/**
 * 
 * Clase : T112DAO 
 * Autor : RMONTES
 * Fecha : 07/11/2007
 * 
 * Descripcion: Esta clase se creo con la finalidad de obtener los datos de la tabla
 * T112CAPEX de personal.
 */
public class T112DAO extends DAOAbstract {
	private DataSource dataSource = null;
	
	private static final StringBuffer FIND_BY_CODPERS = new StringBuffer("select t112cod_pers, t112cent_capa, t112descr_cur, t112anho_capa, ")
																 .append("t112f_inicio, t112f_fin, t112tip_certi, t112f_presenta, ")
																 .append("t112sustento, t112observa ")
																 .append("from t112capaex ")
																 .append("where t112cod_pers = ?");
	
	private static final StringBuffer FIND_BY_CODPERS2 = new StringBuffer("select t112cod_pers, t112cent_capa, t112descr_cur, t112anho_capa, ")
																 .append("t112f_inicio, t112f_fin, t112tip_certi, t112f_presenta, ")
																 .append("t112sustento, t112observa ")
																 .append("from t112capaex ")
																 .append("where t112cod_pers = ?")
																 .append(" UNION ")
																 .append("select t112cod_pers, t112cent_capa, t112descr_cur, t112anho_capa, ")
																 .append("t112f_inicio, t112f_fin, t112tip_certi, t112f_presenta, ")
																 .append("t112sustento, t112observa ")
																 .append("from t112capaex ")
																 .append("where t112cod_pers = ?")
																 .append("Order by 5");	
	/**
	 * @param datasource Object
	 */
	public T112DAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.dataSource = (DataSource)datasource;
	    else if (datasource instanceof String)
	    	this.dataSource = getDataSource((String)datasource);
	    else
	    	throw new DAOException(this, "Datasource no valido");
	}
	
	/**
	 * Metodo que devuelve los datos de un registro especifico de la tabla T112CAPEX
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
