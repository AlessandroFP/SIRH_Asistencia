package pe.gob.sunat.rrhh.dao;

import java.util.ArrayList;
import java.util.Map;
import java.util.List; //ECR 08/04/2009
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;

/**
 * Clase : CorreoDAO
 * Autor : CGARRATT
 * Fecha : 21/11/2005
 */
public class CorreoDAO extends DAOAbstract {
	
	protected final Log log = LogFactory.getLog(getClass());
	private DataSource dataSource = null;
	
	private static final String findCorreoByRegistro = "select smtp from correos where cod_pers = ?";
	private static final String FINDCORREOBYREGISTROS = "select smtp from correos where cod_pers in (?)";
	private final StringBuffer insertByRtps = new StringBuffer("insert into correos (cod_pers,smtp) values(?,?)");
	private final StringBuffer updateByRtps = new StringBuffer("update correos set smtp = ? where cod_pers = ?");
		
//	public CorreoDAO(String jndi) {
//		dataSource = getDataSource(jndi);
//	}
	
    /**
     * Constructor de carga
     * @param dataSource	 datasource o un String JNDI	
     */  
    public CorreoDAO(Object dataSource){
	    if (dataSource instanceof DataSource)
	      this.dataSource = (DataSource)dataSource;
	    else if (dataSource instanceof String)
	      this.dataSource = getDataSource((String)dataSource);
	    else
	      throw new DAOException(this, "Datasource no valido");
	  } 
	  
	/**
	 * 
	 * @param registro
	 * @return
	 * @throws DAOException
	 */
    public String findCorreoByRegistro(String registro) throws DAOException {
            
		String correo = "";
		try {
			Map map = executeQueryUniqueResult(dataSource,findCorreoByRegistro, new Object[]{registro});						
			if (map != null && !map.isEmpty()) {											
				correo = map.get("smtp") != null ? ((String)map.get("smtp")).trim() : "";				
			}
    	} catch(Exception e){
    		throw new DAOException(this,"No se pudo cargar la informacion del correo del trabajador");
    	}   
    	return correo;
    }	
    
    /** agregado prac-jcallo 15/11/2007**/
    
    /**
	 * Metodo que se encargada de insertar un nuevo registro 
	 *  
	 * @param hm		HashMap. datos de del personal(des_correo,cod_personal)
	 * @return          ArrayList conteniendo los registros cargados
	 *                  en HashMaps.
	 * @throws DAOException
	 * */
	public void insertByRtps(Map hm) throws DAOException {
				
		executeUpdate(dataSource, insertByRtps.toString(), new Object[]{ hm.get("cod_personal"), hm.get("des_correo")});
				
	}
	
	/**
	 * Metodo que se encarga de actualizar algunos datos del personal 
	 *  
	 * @param hm		HashMap. datos de del personal(des_correo,cod_personal)
	 * @return          ArrayList conteniendo los registros cargados
	 *                  en HashMaps.
	 * @throws DAOException
	 * */
	public void updateByRtps(Map hm) throws DAOException {
				
		executeUpdate(dataSource, updateByRtps.toString(), new Object[]{hm.get("des_correo"), hm.get("cod_personal")});
				
	}	
	/** fin agregado prac-jcallo 15/11/2007**/

	/** agregado ECR 08/04/2009 **/
	/**
	 * Metodo que se encarga de obtener los correos en una lista 
	 *  
	 * @param destinatarios	String. lista de codigos de registro
	 * @return ArrayList 
	 * @throws DAOException
	 * */
	public List findCorreoMultiple(String destinatarios) throws DAOException {				
		List Cpta = new ArrayList();
		try {
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);		
			Cpta = executeQuery(dataSource, FINDCORREOBYREGISTROS.toString(), new Object[]{
			(String)destinatarios}); 
		} catch (Exception e) {
			log.error("*** SQL Error ****",e);
			throw new DAOException(this, "Error en consulta. [FINDCORREOBYREGISTROS]");
		}  
			return (ArrayList)Cpta;	  
	}	
	/** fin agregado ECR 08/04/2009 **/		
			
}