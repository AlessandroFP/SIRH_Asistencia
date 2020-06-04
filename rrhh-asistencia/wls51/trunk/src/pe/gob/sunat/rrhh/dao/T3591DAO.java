/*
 * Created on 31/01/2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package pe.gob.sunat.rrhh.dao;

import java.io.File;
import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.core.pattern.DynaBean;
/**
 * DAO de acceso a la tabla t3351firmas que almacena las firmas digitalizadas de todo el 
 * personal.
 * 
 * @author JVALDEZ
 *
 */
public class T3591DAO extends DAOAbstract {
    private DataSource datasource = null;
    private static final String QUERY1_SENTENCE = "SELECT img_firma FROM t3591firmas WHERE cod_pers = ?";
    private static final String INSERT_SENTENCE = "insert into t3591firmas(cod_pers,img_firma,fec_graba,cod_usuario) values (?,?,?,?)";

    /**
     * 
     */
    public T3591DAO() {
      super();
      // TODO Auto-generated constructor stub
    }

    /**
     * @param dataSource
     */
    public T3591DAO(Object datasource) {
      if (datasource instanceof DataSource)
        this.datasource = (DataSource)datasource;
      else if (datasource instanceof String)
        this.datasource = getDataSource((String)datasource);
      else
        throw new DAOException(this, "Datasource no valido");
    }
    /**
     * @param datasource The datasource to set.
     */
    public void setDatasource(DataSource datasource) {
      this.datasource = datasource;
    }

    /**
     * Obtiene la firma digitalizada de la persona, almacenada en los servidores de 
     * rrhh.
     *  
     * @param codpers String numero de registro de la persona
     * @return File null de no existir datos o una referencia File a la imagen de la firma en disco.
     */
    public File findByPK(String codpers){
      Map o = executeQueryUniqueResult(datasource, QUERY1_SENTENCE, new Object[]{codpers});
      if (o != null)
        return (File) o.get("img_firma");
      return null; 
    }

    /**
     * 
     * @param dbean DynaBean
     */
    public void crear(DynaBean dbean){
      
      insert(
        datasource,
        INSERT_SENTENCE,
        new Object[]{
            dbean.get("cod_pers"),dbean.get("img_firma"),
            dbean.get("fec_graba"),dbean.get("cod_usuario")
        }
      );
    }

}
