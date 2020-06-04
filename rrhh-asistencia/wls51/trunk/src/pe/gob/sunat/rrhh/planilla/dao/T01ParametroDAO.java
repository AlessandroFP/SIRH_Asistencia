package pe.gob.sunat.rrhh.planilla.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.normasesoria.siglat.util.ParametroBean;

/**
 * <p>Title: T01ParametroDAO </p>
 * <p>Description: Clase para realizar la consulta de la tabla en Oracle T01Parametro</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: SUNAT</p>
 * @author EMOSTACERO
 * @version 1.0 
 */
public class T01ParametroDAO extends DAOAbstract {
    private DataSource datasource; // Variable creada para instanciar el DataSource
    
    private static final StringBuffer FIND_PARAMETRO =  new StringBuffer("")
    .append("SELECT COD_PARAMETRO, COD_MODULO, COD_TIPO, ")
    .append("COD_ARGUMENTO, DESC_LARGA, DESC_CORTA, ")
    .append("DESC_ABREVIATURA, COD_ESTADO ")
    .append("FROM T01PARAMETRO ")
    .append("WHERE 1=1 ");
    
    //Busqueda de un solo parametro Boleta de Haberes - SIGA
    private static final StringBuffer FIND_PARAMETRO_CODIGO =new StringBuffer("SELECT COD_PARAMETRO,COD_MODULO,COD_TIPO,COD_ARGUMENTO,")
	.append(" DESC_LARGA,DESC_CORTA,DESC_ABREVIATURA,COD_ESTADO,FEC_MODIF,COD_USUMODIF  ")
	.append(" FROM T01PARAMETRO   ")
	.append(" where trim(COD_PARAMETRO)=? and trim(COD_MODULO)=? and COD_TIPO=? and trim(COD_ARGUMENTO)=? ");

    
    /**
     * Método para validar el Datasource a usarse
     * @param Object datasource
     */
    public T01ParametroDAO(Object datasource) {
      if (datasource instanceof DataSource)
        this.datasource = (DataSource)datasource;
      else if (datasource instanceof String)
        this.datasource = getDataSource((String)datasource);
      else
        throw new DAOException(this, "Datasource no valido");
    }

    /**
     * Obtiene los parametros que coincidan con los criterios de búsqueda ingresados
     * @param params Map
     * @return List lista
     * @throws DAOException
     */  
  public List findByParams(ParametroBean[] paramBean) {
    if (log.isDebugEnabled()) {
      log.debug(this.toString()
      .concat("INICIO - findByParams"));
    }
    List lista = null;
    StringBuffer strSQL = new StringBuffer();
    StringBuffer addWhere = new StringBuffer();
    for (int i = 0; i < paramBean.length; i++) {
      addWhere.append("AND ");
      addWhere.append(paramBean[i].addWhere());
    }
    strSQL.append(FIND_PARAMETRO)
    .append(addWhere);
    lista = (ArrayList) this.executeQuery(datasource, strSQL.toString());
    if (log.isDebugEnabled()) log.debug("listaParametro:"+ lista);
    if (log.isDebugEnabled()) {
      log.debug(this.toString().concat(" FIN - findByParams "));
    }
    return lista;
  }
  
  /**
	 * Obtiene los datos del parámetro almacenado
	 * @param params
	 * @return Map parametro 
	 * @throws DAOException
	 */
	public Map findByCodigoParametro(Map params) throws DAOException {
		if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - findByCodigoParametro()"));
		Map totalesLiquidacion = new HashMap();		
		try {			
		    StringBuffer strSQL = null;
		    strSQL = new StringBuffer(FIND_PARAMETRO_CODIGO.toString());			
			totalesLiquidacion = executeQueryUniqueResult(datasource, strSQL.toString(),
					new Object[]{params.get("cod_parametro"), params.get("cod_modulo"), params.get("cod_tipo"), params.get("cod_argumento")});	
		} catch(Exception e) {
			StringBuffer msg = new StringBuffer()
			.append(" ERROR - findByCodigoParametro() : ").append(e.toString());
			if (log.isDebugEnabled()) log.debug(this.toString() + msg.toString());
			throw new DAOException(this, msg.toString());
		}
		if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - findByCodigoParametro() "));
		return totalesLiquidacion;
	}
  
}
