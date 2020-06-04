package pe.gob.sunat.rrhh.dao;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;

/**
 * 
 * Clase : T1797DAO
 * Autor : CGARRATT
 * Fecha : 21/11/2005
 */
public class T1797DAO extends DAOAbstract {
	
	protected final Log log = LogFactory.getLog(getClass());
	private DataSource dataSource = null;
	
	private static final String findDependenciaByCodUO = "SELECT cod_depend FROM T1797DEPENUORGA WHERE cod_uorga = ? and tip_depend = ?";
	private static final String findUOByDependencia = "SELECT cod_uorga FROM T1797DEPENUORGA WHERE cod_depend=? ";
	
	public T1797DAO(Object obj) {
        if(obj instanceof DataSource)
            dataSource = (DataSource)obj;
        else if(obj instanceof String)
            dataSource = getDataSource((String)obj);
        else
            throw new DAOException(this, "Datasource no valido");
	}
	
	/**
	 * 
	 * @param coduo
	 * @return
	 * @throws DAOException
	 */
    public String findDependenciaByCodUO(String coduo) throws DAOException {
            
    	String dependencia = "";
    	boolean buscaenregional = false;
    	
		try {
			Map mapa = null;
			mapa = executeQueryUniqueResult(dataSource,
					findDependenciaByCodUO, 
					new Object[] {coduo.substring(0, 3) + "000" , "1"});			
			
			if (mapa == null) 
					buscaenregional = true;
			else if (mapa.isEmpty())
					buscaenregional = true;
			
			if (buscaenregional)
				mapa = executeQueryUniqueResult(dataSource,
						findDependenciaByCodUO, 
						new Object[] {coduo.substring(0, 2) + "0000" , "1"});
			
			if (mapa!=null && !mapa.isEmpty()) {											
				dependencia = mapa.get("cod_depend")!=null?(String)mapa.get("cod_depend"):"";
			}
    	}
    	catch(Exception e){
            throw new DAOException(this,"No se pudo cargar la informaciÃ³n de la dependencia");
    	}   
    	return dependencia;
    }
    
    /**
     * Obtiene el código de dependencia de la unidad organizacional.
     * @param codUO
     * @param tipoDepen
     * @return
     */
    public List findDependenciaByCodUO(String codUO, String tipoDepen){
    	return executeQuery(dataSource, findDependenciaByCodUO, new Object[] {codUO, tipoDepen});
    }

	public List findUOByDependencia(String cod_depend){
	
        return super.executeQuery(dataSource, "SELECT cod_uorga FROM T1797DEPENUORGA WHERE cod_depend=? AND tip_depend = '2'  " ,  new Object[] {
            cod_depend
        });
    }	
	
}