package pe.gob.sunat.rrhh.dao;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;

/**
 * 
 * Clase : T07DAO 
 * Autor : RMONTES
 * Fecha : 07/11/2007
 * 
 * Descripcion: Esta clase se creo con la finalidad de obtener los datos de la tabla
 * T07PDCTO de personal.
 */
public class T07DAO extends DAOAbstract {
	private DataSource dataSource = null;
	
	private static final StringBuffer FIND_BY_CODPERS = new StringBuffer("select d.t07cod_pers, d.t07correl, d.t07cod_dcto, d.t07nro_dcto, ") 
																 .append("(select t.t01des_corta from t01param t ")
																 .append("where t.t01_numero = '708' and t.t01_tipo = 'D' and ")
																 .append("d.t07cod_dcto = t.t01_argumento) as t07cod_dcto_desc ")
																 .append("from t07pdcto d ")
																 .append("where d.t07cod_pers = ?");
	
	/* JRR - 06/04/2011 - RECUPERACION DE FUENTES - Decompilados */
	  private StringBuffer QUERY1_SENTENCE = new StringBuffer("SELECT ")
	    	.append("T.t07nro_dcto FROM T07PDCTO T ")
	    	.append("WHERE T.t07cod_pers=? AND T.t07cod_dcto=? ");
	/*      */
	  
	/**
	 * @param datasource Object
	 */
	public T07DAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.dataSource = (DataSource)datasource;
	    else if (datasource instanceof String)
	    	this.dataSource = getDataSource((String)datasource);
	    else
	    	throw new DAOException(this, "Datasource no valido");
	}
	
	/**
	 * Metodo que devuelve los datos de un registro especifico de la tabla T07PDCTO
	 * filtrado por el numero de registro del trabajador.
	 * 
	 * @param codpers
	 * @return
	 */
	public List findByCodPers(String codpers){
		return executeQuery(dataSource, FIND_BY_CODPERS.toString(), new Object[]{codpers});
	}
	
	
	/* JRR - 07/04/2011 - RECUPERACION DE FUENTES - Decompilados */
	public Map findNumeroByRegistro(Map params)
	    throws DAOException
	  {
	    Map mapaHist = null;
	    Object[] objs = { 
	      params.get("num_doc_afec"), 
	      params.get("t07cod_dcto") };

	    mapaHist = executeQueryUniqueResult(this.dataSource, this.QUERY1_SENTENCE.toString(), objs);
	    return mapaHist;
	  }
	/*          */
	
}
