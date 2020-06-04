package pe.gob.sunat.rrhh.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;

/**
 * 
 * Clase : T11DAO 
 * Autor : RMONTES
 * Fecha : 07/11/2007
 * 
 * Descripcion: Esta clase se creo con la finalidad de obtener los datos de la tabla
 * T11HISTO de personal.
 */
public class T11DAO extends DAOAbstract {
	private DataSource dataSource = null;
	
	private static final StringBuffer FIND_BY_CODPERS = new StringBuffer("select h.t11anho, h.t11num_acc, h.t11tipo_acc, h.t11cod_pers, ")
																 .append("h.t11cod_uorg, h.t11cod_uorg_ante, h.t11cod_cate, h.t11cod_cate_ante, ")
																 .append("h.t11cod_carg, h.t11f_inicio, h.t11f_fin, ")
																 .append("(select ac.t99descrip from t99codigos ac ")
																 .append("where ac.t99cod_tab = '010' and ac.t99tip_desc = 'D' and ")
																 .append("h.t11tipo_acc = ac.t99codigo) as t11tipo_acc_desc, ")
																 .append("(select c.t99descrip from t99codigos c ")
																 .append("where c.t99cod_tab = '001' and c.t99tip_desc = 'D' and ")
																 .append("h.t11cod_cate = c.t99codigo) as t11cod_cate_desc, ")
																 .append("(select ca.t99descrip from t99codigos ca ")
																 .append("where ca.t99cod_tab = '001' and ca.t99tip_desc = 'D' and ")
																 .append("h.t11cod_cate_ante = ca.t99codigo) as t11cod_cate_ante_d, ")
																 .append("(select uo.t12des_uorga from t12uorga uo ")
																 .append("where uo.t12cod_uorga = h.t11cod_uorg) as t11cod_uorg_desc, ")
																 .append("(select uoa.t12des_uorga from t12uorga uoa ")
																 .append("where uoa.t12cod_uorga = h.t11cod_uorg_ante) as t11cod_uorg_ante_d  ")
																 .append("from t11histo h ")
																 .append("where h.t11cod_pers = ? and h.t11estado = 'C' ") //Order by h.t11f_inicio ");
     												 	    	 //JRR - 28/10/2009
																 .append(" UNION ")
																 .append("select h.t11anho, h.t11num_acc, h.t11tipo_acc, h.t11cod_pers, ")
																 .append("h.t11cod_uorg, h.t11cod_uorg_ante, h.t11cod_cate, h.t11cod_cate_ante, ")
																 .append("h.t11cod_carg, h.t11f_inicio, h.t11f_fin, ")
																 .append("(select ac.t99descrip from t99codigos ac ")
																 .append("where ac.t99cod_tab = '010' and ac.t99tip_desc = 'D' and ")
																 .append("h.t11tipo_acc = ac.t99codigo) as t11tipo_acc_desc, ")
																 .append("(select c.t99descrip from t99codigos c ")
																 .append("where c.t99cod_tab = '001' and c.t99tip_desc = 'D' and ")
																 .append("h.t11cod_cate = c.t99codigo) as t11cod_cate_desc, ")
																 .append("(select ca.t99descrip from t99codigos ca ")
																 .append("where ca.t99cod_tab = '001' and ca.t99tip_desc = 'D' and ")
																 .append("h.t11cod_cate_ante = ca.t99codigo) as t11cod_cate_ante_d, ")
																 .append("(select uo.t12des_uorga from t12uorga uo ")
																 .append("where uo.t12cod_uorga = h.t11cod_uorg) as t11cod_uorg_desc, ")
																 .append("(select uoa.t12des_uorga from t12uorga uoa ")
																 .append("where uoa.t12cod_uorga = h.t11cod_uorg_ante) as t11cod_uorg_ante_d  ")
																 .append("from t11histo h ")
																 .append("where h.t11cod_pers = ? and h.t11estado = 'C' ")
																 .append("ORDER BY 10");
																 //	

	/* JRR - 06/04/2011 - RECUPERACION DE FUENTES */
	private StringBuffer QUERY1_SENTENCE = new StringBuffer("SELECT ")
	  .append("H.t11anho,H.t11num_acc,H.t11tipo_acc,H.t11f_inicio,T.t99descrip ")
	  .append("FROM T11HISTO H LEFT JOIN T99CODIGOS T ON ")
	  .append("H.t11tipo_acc=T.t99codigo AND T.t99cod_tab= ? ") //t99cod_tab='001'
	  .append("WHERE H.t11cod_pers= ? AND H.t11estado= ? AND T.t99tip_desc=? ")
	  .append("ORDER BY T11ANHO DESC "); // estado='C'
	/*        */
	
	/**
	 * @param datasource Object
	 */
	public T11DAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.dataSource = (DataSource)datasource;
	    else if (datasource instanceof String)
	    	this.dataSource = getDataSource((String)datasource);
	    else
	    	throw new DAOException(this, "Datasource no valido");
	}
	
	/**
	 * Metodo que devuelve los datos de un registro especifico de la tabla T11HISTO
	 * filtrado por el numero de registro del trabajador.
	 * 
	 * @param codpers
	 * @return
	 */
	public List findByCodPers(String codpers, String codpersAnte){
		return executeQuery(dataSource, FIND_BY_CODPERS.toString(), new Object[]{codpers, codpersAnte});
	}
	
	
	/* JRR - 06/04/2011 - RECUPERACION DE FUENTES */
	/**
	 * Método encargado de realizar la consulta a
	 * la tabla t11histo.
	 * @param datos Map 
	 * @return Map
	 * @throws DAOException
	 */
	public Map findUltByRegistro(Map params) throws DAOException {
		List listaHist=null;
		Map mapaHist = null;
		Object[] objs = new Object[] {
				params.get("t99cod_tab"),
				params.get("num_doc_afec"),
				params.get("t11estado"),
				params.get("t99tip_desc")
		};
		listaHist = executeQuery(dataSource,QUERY1_SENTENCE.toString(),objs);
		if (listaHist!=null && listaHist.size()>0){
			mapaHist= (HashMap) listaHist.get(0);
		}
		return mapaHist;
	}
	/*       */
	  
}
