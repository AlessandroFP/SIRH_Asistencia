package pe.gob.sunat.rrhh.dao;

import java.util.List;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;

/**
 * 
 * Clase : T50DAO 
 * Autor : RMONTES
 * Fecha : 07/11/2007
 * 
 * Descripcion: Esta clase se creo con la finalidad de obtener los datos de la tabla
 * T50PERMEDE de personal.
 */
public class T50DAO extends DAOAbstract {
	private DataSource dataSource = null;
	
	
	
	private static final StringBuffer FIND_BY_CODPERS = new StringBuffer("select p.t50cod_pers, p.t50cod_doc, p.t50num_doc, p.t50f_doc, ")
															 .append("p.t50tip_doc, p.t50asunto, ")
															 .append("(select ca.t99descrip from t99codigos ca ")
															 .append("where ca.t99cod_tab = '017' and ca.t99tip_desc = 'D' and ")
															 .append("p.t50tip_doc = ca.t99codigo) as t50tip_doc_desc, ")
															 .append("(select ca.t01des_larga from t01param ca ")																 
															 .append("where ca.t01_numero = '003' and ca.t01_tipo = 'D' and ")
															 .append("p.t50cod_doc = ca.t01_argumento) as t50cod_doc_desc ")																 
															 .append("from t50permede p ")
															 .append("where p.t50cod_pers = ? and p.cod_estado = '1' and ")
															 .append("t50tip_doc in (SELECT trim(t99codigo) ")
															 .append("FROM t99codigos WHERE t99cod_tab = '017' and t99tip_desc = 'D' and t99tipo <> 'S') ")
															 .append(" UNION ")
															 .append("select p.t50cod_pers, p.t50cod_doc, p.t50num_doc, p.t50f_doc, ")
															 .append("p.t50tip_doc, p.t50asunto, ")
															 .append("(select ca.t99descrip from t99codigos ca ")
															 .append("where ca.t99cod_tab = '017' and ca.t99tip_desc = 'D' and ")
															 .append("p.t50tip_doc = ca.t99codigo) as t50tip_doc_desc, ")
															 .append("(select ca.t01des_larga from t01param ca ")																 
															 .append("where ca.t01_numero = '003' and ca.t01_tipo = 'D' and ")
															 .append("p.t50cod_doc = ca.t01_argumento) as t50cod_doc_desc ")															 
															 .append("from t50permede p ")
															 .append("where p.t50cod_pers = ? and p.cod_estado = '1' and ")
															 .append("t50tip_doc in (SELECT trim(t99codigo) ")
															 .append("FROM t99codigos WHERE t99cod_tab = '017' and t99tip_desc = 'D' and t99tipo <> 'S') ")															 
															 .append("Order by 4");															 
															 
	
	
	
	
	private static final StringBuffer FIND_BY_CODPERS2 = new StringBuffer("select p.t50cod_pers, p.t50cod_doc, p.t50num_doc,p.fec_inivig, ")
																 .append("p.t50tip_doc, p.t50asunto, ")
																 .append("(select ca.t99descrip from t99codigos ca ")
																 .append("where ca.t99cod_tab = '017' and ca.t99tip_desc = 'D' and ")
																 .append("p.t50tip_doc = ca.t99codigo) as t50tip_doc_desc, ")
																 .append("(select ca.t99descrip from t99codigos ca ")																 
																 .append("where ca.t99cod_tab = '625' and ca.t99tip_desc = 'D' and ")
																 .append("p.t50cod_doc = ca.t99codigo) as t50cod_doc_desc ")																 
																 .append("from t50permede p ")
																 .append("where p.t50cod_pers = ? and p.cod_estado = '1' and ")
																 .append("t50tip_doc in (SELECT trim(t99codigo) ")
															     .append("FROM t99codigos WHERE t99cod_tab = '017' and t99tip_desc = 'D' and t99tipo = 'S' ) ")																 
																 .append(" UNION ")
																 .append("select p.t50cod_pers, p.t50cod_doc, p.t50num_doc,p.fec_inivig, ")
	 															 .append("p.t50tip_doc, p.t50asunto, ")
																 .append("(select ca.t99descrip from t99codigos ca ")
																 .append("where ca.t99cod_tab = '017' and ca.t99tip_desc = 'D' and ")
																 .append("p.t50tip_doc = ca.t99codigo) as t50tip_doc_desc, ")
																 .append("(select ca.t99descrip from t99codigos ca ")																 
																 .append("where ca.t99cod_tab = '625' and ca.t99tip_desc = 'D' and ")
																 .append("p.t50cod_doc = ca.t99codigo) as t50cod_doc_desc ")																 
																 .append("from t50permede p ")
																 .append("where p.t50cod_pers = ? and p.cod_estado = '1' and ")
																 .append("t50tip_doc in (SELECT trim(t99codigo) ")
																 .append("FROM t99codigos WHERE t99cod_tab = '017' and t99tip_desc = 'D' and t99tipo = 'S' )")																		 
																 .append("Order by 4");	
	                        
	
	
	/**
	 * @param datasource Object
	 */
	public T50DAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.dataSource = (DataSource)datasource;
	    else if (datasource instanceof String)
	    	this.dataSource = getDataSource((String)datasource);
	    else
	    	throw new DAOException(this, "Datasource no valido");
	}
	
	/**
	 * Metodo que devuelve los datos de un registro especifico de la tabla T50PERMEDE
	 * filtrado por el numero de registro del trabajador y su registro de contratado.
	 * 
	 * @param codpers,codpersante
	 * @return List 
	 */
	public List findByCodPers(String codpers,String codpersante){
		return executeQuery(dataSource, FIND_BY_CODPERS.toString(), new Object[]{codpers,codpersante});
	}

	/**
	 * Metodo que devuelve los datos de un registro especifico de la tabla T50PERMEDE
	 * filtrado por el numero de registro del trabajador y el registro de contratado.
	 * 
	 * @param codpers,codpersante
	 * @return List
	 */
	public List findByCodPers2(String codpers,String codpersante){
		return executeQuery(dataSource, FIND_BY_CODPERS2.toString(), new Object[]{codpers,codpersante});
	}
}
