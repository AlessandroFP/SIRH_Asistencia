package pe.gob.sunat.sp.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;

public class T5864DAO extends DAOAbstract {
	private static final Logger log = Logger.getLogger(T5864DAO.class);

	private StringBuffer SELECT_QUERY = new StringBuffer("SELECT cod_tab,")
       .append("cod_tip_desc,")
       .append("num_codigo,")
       .append("des_corta,")
       //.append("des_larga,") //@jhuamanr
       .append("case when cod_estado='A' then substring_index(des_larga,'/',1) when cod_estado='I' then substring_index(des_larga,'/',-1) end as des_larga,")
       .append("des_abreviatura,")
       .append("val_param,")
       .append("des_glosa,")
       .append("cod_estado ")
       .append("FROM t5864parametro where 1=1");
	
	private DataSource datasource;

	public T5864DAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.datasource = (DataSource) datasource;
		else if (datasource instanceof String)
			this.datasource = getDataSource((String) datasource);
		else
			throw new DAOException(this, "Datasource no valido");
	}
	
	
	public  List findByFiltro(Map filtro) {
		if (log.isDebugEnabled())log.debug(" INICIO - findByFiltro()-> " +filtro);
		
		StringBuffer query = SELECT_QUERY;
		List listParametros = new ArrayList();
		boolean existeCod= false;
		if (filtro.containsKey("cod_tab") ) {
			query.append(" and cod_tab = '"+ filtro.get("cod_tab") +"' ");   
			existeCod=true;
		}
		if (filtro.containsKey("cod_tip_desc") ) {
			query.append(" and cod_tip_desc = '"+ filtro.get("cod_tip_desc") +"' ");  
		}
		/*if (filtro.containsKey("cod_estado") ) {//jhuamanr
			query.append(" and cod_estado = '"+ filtro.get("cod_estado") +"' "); 
		} */
		if (filtro.containsKey("num_codigo") ) {
			query.append(" and num_codigo = '"+ filtro.get("num_codigo") +"' "); 
		}
		
		
		if (filtro.containsKey("des_corta") ) {
			query.append(" and des_corta like '"+ filtro.get("des_corta") +"' ");
		} 
		
		if (filtro.containsKey("des_larga") ) {
			query.append(" and des_larga like '"+ filtro.get("des_larga") +"' ");
		} 
		if (filtro.containsKey("des_larga_con_acento") ) {
			query.append(" and des_larga matches '"+filtro.get("des_larga_con_acento")+"'");
		} 
		
		if (log.isDebugEnabled())log.debug("query"+query.toString());		
		if(existeCod){
			listParametros =executeQuery(datasource, query.toString() );
		}
		return listParametros;
		
	}
	
	

}
