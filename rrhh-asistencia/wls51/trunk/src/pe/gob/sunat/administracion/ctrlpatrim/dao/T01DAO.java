package pe.gob.sunat.administracion.ctrlpatrim.dao;

import java.util.HashMap;
import javax.sql.DataSource;
import pe.gob.sunat.framework.core.bean.MensajeBean;
import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.sol.*;

/**
 * Clase       : T01DAO 
 * Descripcion : DAO de la tabla T01param
 * Proyecto    : Consultas Sisa 
 * Autor       : JGARCIA9
 * Fecha       : 03-Oct-2006
 */
public class T01DAO extends DAOAbstract{
	
	private DataSource dataSource;
	
	public static final StringBuffer FIND_BY_CODLOCAL = new StringBuffer("SELECT t01_numero, t01_argumento, t01des_larga,t01des_corta ")
	                                                             .append("FROM t01param ")
	                                                             .append("WHERE t01_numero = ? AND ")
	                                                             .append("t01_tipo = 'D' AND t01_argumento = ? ");
	
	public T01DAO() {
	    super();
	}
	
	/**
	 * @param datasource Object
	*/
	public T01DAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.dataSource = (DataSource)datasource;
	    else if (datasource instanceof String)
	    	this.dataSource = getDataSource((String)datasource);
	    else
	    	throw new DAOException(this, "Datasource no valido");
	}
	
	/**
	 * Método findByClave
	 * @param String dbpool
	 * @param String num
	 * @param String arg
	 * @return HashMap hm
	 * @throws IncompleteConversationalState
	 */	
	public HashMap findByClave(String num, String arg) //Migrado 
	   throws DAOException {
        HashMap res = new HashMap();
        MensajeBean msg = new MensajeBean();
        try {
        	res = (HashMap)executeQueryUniqueResult(dataSource, FIND_BY_CODLOCAL.toString(), new Object[]{num,arg});
        } catch (DAOException e){
        	msg.setMensajeerror("Ha ocurrido un error en la consulta. T01DAO - [findByClave] : ".concat( e.getMessage() ));
        	msg.setMensajesol("Por favor, intente nuevamente realizar la operacion, de continuar con el problema comuniquese con el webmaster");
        	throw new DAOException (this, msg);
	    }
		return res;
	}

}