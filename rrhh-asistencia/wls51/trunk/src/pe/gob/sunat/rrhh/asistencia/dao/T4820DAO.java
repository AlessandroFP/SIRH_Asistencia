package pe.gob.sunat.rrhh.asistencia.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.Propiedades;
import pe.gob.sunat.framework.util.date.FechaBean;


/** 
 * 
 * Clase       : T4820DAO 
 * Descripcion : clase encargada de administrar los datos de la tabla t4820SolDetXComp 
 * Proyecto    : ASISTENCIA 
 * Autor       : EBENAVID
 * Fecha       : 04-ABR-2012 
 * 
 * */

public class T4820DAO extends DAOAbstract {

	
	private DataSource dataSource = null;
	
	Propiedades constantes = new Propiedades(getClass(), "/constantes.properties");
	
	/* EBENAVID - LABOR AUTORIZADA CON SALDO - 03/04/2012 */	
	private final StringBuffer INSERTA_DETALLE = 
		new StringBuffer(" INSERT INTO T4820SOLDETXCOMP (ann_sol, num_sol, cod_uo_sol, cod_pers_sol, cod_mov_sol, fec_registro_sol, num_detalle, fec_perm, hor_ini_perm, hor_ini_comp, cnt_min_comp_usa, cod_user_crea, fec_creacion)")
			.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
	  
		  
	
	/**
	 *@param datasource Object
	 *
	 * */
	public T4820DAO(Object datasource) {
		if (datasource instanceof DataSource)
	      this.dataSource = (DataSource)datasource;
	    else if (datasource instanceof String)
	      this.dataSource = getDataSource((String)datasource);
	    else
	      throw new DAOException(this, "Datasource no valido");
	}
	

	/**
	  * Metodo que obtiene los datos a partir del registro 
	  * @param datos (String cod_pers)
	  * ann_sol, num_sol, cod_uo_sol, cod_pers_sol,
	  * cod_mov_sol, fec_registro_sol, num_detalle,
	  * fec_perm, hor_ini_perm, hor_ini_comp,
	  * cnt_min_comp_usa, cod_user_crea, fec_creacion
	  * @return
	  * @throws DAOException
	  */
	public boolean insertaDetalle(Map datos,Map he) throws DAOException {

		FechaBean fecha1 = new FechaBean();
		boolean result = false;
		if (log.isDebugEnabled()) log.debug("T4820DAO - insertaDetalle - datos: " + datos);	
		if (log.isDebugEnabled()) log.debug("T4820DAO - insertaDetalle - he: " + he);	
		try{
			int resultado = executeUpdate(dataSource, INSERTA_DETALLE.toString(), new Object[]{ 
				datos.get("ann_sol").toString(), datos.get("num_sol").toString(), datos.get("cod_uo_sol").toString(),
				datos.get("cod_pers_sol").toString(),datos.get("cod_mov_sol").toString(),datos.get("fec_registro_sol").toString(),
				new Integer(datos.get("num_detalle").toString()),
				  he.get("fec_perm"), he.get("hor_ini_perm").toString(), he.get("hor_ini_comp").toString() ,
				  new Integer(he.get("min_usado").toString()), he.get("usuario").toString(), fecha1.getTimestamp() } );
			result = (resultado >0);
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta T4820DAO. [INSERTA_DETALLE]");
		}	
		return result;
	}
	
		
}