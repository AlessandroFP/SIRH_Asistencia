package pe.gob.sunat.rrhh.asistencia.dao;

import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;

/** 
 * 
 * Clase       		: T45DAO 
 * Descripcion 		: Clase encargada de administrar las consultas a la tabla T45Turno 
 * Proyecto    		: ASISTENCIA 
 * Autor      		: ICAPUNAY
 * Fecha      		: 19-ENE-2011
 * 
 * */

public class T45DAO extends DAOAbstract {	
	
	private DataSource datasource;	
	
	//ICAPUNAY
	private static final StringBuffer FIND_TURNO_BY_CODTURNO = new StringBuffer("select cod_turno ")	
	.append(" from  t45turno where cod_turno = ?"); 
	
	/**
	* 
	* Este constructor del DAO dicierne como crear el datasource
	* dependiendo del tipo de objeto que recibe. Esto nos ayuda a
	* mejorar la invocacion del dao.
	* 
	* @param datasource Object
	*/
	public T45DAO(Object datasource) {
		if (datasource instanceof DataSource)
		  this.datasource = (DataSource)datasource;
		else if (datasource instanceof String)
		  this.datasource = getDataSource((String)datasource);
		else
		  throw new DAOException(this, "Datasource no valido");
	}
		

	//ICAPUNAY  19/01/2011
	/**
	 * Metodo encargado de listar los datos de un turno existente
	 * @param codTurno String 
	 * @return Map
	 * @throws DAOException
	 */
	public Map findByCodTurno(String codTurno) throws DAOException {

		Map turno = null;		
		
		if (log.isDebugEnabled()) log.debug("findByCodTurno - codTurno: "+codTurno);
			
		turno = executeQueryUniqueResult(datasource, FIND_TURNO_BY_CODTURNO.toString(), new Object[]{codTurno});
				
		return turno;
	}
	//ICR CAPUNAY FIN

	
}
