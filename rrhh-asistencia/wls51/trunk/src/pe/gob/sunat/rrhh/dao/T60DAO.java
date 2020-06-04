package pe.gob.sunat.rrhh.dao;

import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;

/** 
 * 
 * Clase       : T60DAO 
 * Descripcion : Clase encargada de administrar los datos de la tabla t60exclu 
 * Proyecto    : ASISTENCIA/PLANILLAS 
 * Autor       : JROJAS4
 * Fecha       : 11-DIC-2008 
 * 
 * */

public class T60DAO extends DAOAbstract {	
	
	private DataSource datasource;	
	
	//JRR
	private final StringBuffer INSERTAR_EXCLUSIONES_PERIODO = new 	StringBuffer("INSERT INTO t60exclu "
			).append("(t60anho, t60mes, t60tip_planil, t60cod_pers, t60cod_motivo, t60estado, t60f_inicio, "
			).append(" t60f_fin, t60observa, t60cond_var, t60f_graba, t60cod_user) "
			).append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");
	
	//JRR
	private final StringBuffer ELIMINAR_EXCLUSIONES_PERIODO = new 	StringBuffer("DELETE FROM t60exclu "
			).append("WHERE t60anho = ? AND t60mes = ? ");
	
	/**
	* 
	* Este constructor del DAO dicierne como crear el datasource
	* dependiendo del tipo de objeto que recibe. Esto nos ayuda a
	* mejorar la invocacion del dao.
	* 
	* @param datasource Object
	*/
	public T60DAO(Object datasource) {
		if (datasource instanceof DataSource)
		  this.datasource = (DataSource)datasource;
		else if (datasource instanceof String)
		  this.datasource = getDataSource((String)datasource);
		else
		  throw new DAOException(this, "Datasource no valido");
	}
		
	/**
	 * Metodo que se encarga de registrar las exclusiones del período
	 * @throws DAOException
	 */	
	public void registrarExclusionesPeriodo(Map datos) throws DAOException {		
		//if (log.isDebugEnabled()) log.debug("registrarExclusionesPeriodo - datos" + datos);	
		executeUpdate(datasource, INSERTAR_EXCLUSIONES_PERIODO.toString(), new Object[]{datos.get("cod_anho"), 
		datos.get("cod_mes"), datos.get("ind_tip_planil"), datos.get("cod_persona"), datos.get("cod_motivo"), 
		datos.get("ind_estado"), datos.get("ffinicio"), datos.get("ffin"), datos.get("observa"), 
		datos.get("cond_var"), datos.get("fec_creacion"), datos.get("cod_usucrea")});
	}


	/**
	 * Metodo que se encarga de eliminar las exclusiones del período
	 * @throws DAOException
	 */	
	public void eliminarExclusionesPeriodo(Map datos) throws DAOException {		
		String cod_anho = datos.get("periodo").toString().substring(0,4);
		String cod_mes = datos.get("periodo").toString().substring(4,6);
		
		executeUpdate(datasource, ELIMINAR_EXCLUSIONES_PERIODO.toString(), new Object[]{cod_anho, cod_mes});
	}
	
}
