package pe.gob.sunat.rrhh.dao;

import java.util.List;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;

/**
 * 
 * Clase : T29DAO 
 * Autor : RMONTES
 * Fecha : 07/11/2007
 * 
 * Descripcion: Esta clase se creo con la finalidad de obtener los datos de la tabla
 * T29PERAC de personal.
 */
public class T29DAO extends DAOAbstract {
	private DataSource dataSource = null;
	
	private static final StringBuffer FIND_BY_CODPERS = new StringBuffer("select p.t29cod_pers, p.t29cod_grado, p.t29cod_uni, p.t29cod_esp, ")
																 .append("p.t29num_cole, p.t29fin_grado, p.t29sustento, p.t29observa, ")
																 .append("p.t29des_grado, ")
																 .append("(select g.t99descrip from t99codigos g ")
																 .append("where g.t99cod_tab = '002' and g.t99tip_desc = 'D' and ")
																 .append("p.t29cod_grado = g.t99codigo) as t29cod_grado_desc, ")
																 .append("(select e.t99descrip from t99codigos e ")
																 .append("where e.t99cod_tab = '003' and e.t99tip_desc = 'D' and ")
																 .append("p.t29cod_esp = e.t99codigo) as t29cod_esp_desc, ")
																 .append("(select u.t99descrip from t99codigos u ")
																 .append("where u.t99cod_tab = '004' and u.t99tip_desc = 'D' and ")
																 .append("p.t29cod_uni = u.t99codigo) as t29cod_uni_desc ")
																 .append("from t29perac p ")
																 .append("where p.t29cod_pers = ?");
																
	/**
	 * @param datasource Object
	 */
	public T29DAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.dataSource = (DataSource)datasource;
	    else if (datasource instanceof String)
	    	this.dataSource = getDataSource((String)datasource);
	    else
	    	throw new DAOException(this, "Datasource no valido");
	}
	
	/**
	 * Metodo que devuelve los datos de un registro especifico de la tabla T29PERAC
	 * filtrado por el numero de registro del trabajador.
	 * 
	 * @param codpers
	 * @return
	 */
	public List findByCodPers(String codpers){
		return executeQuery(dataSource, FIND_BY_CODPERS.toString(), new Object[]{codpers});
	}
}
