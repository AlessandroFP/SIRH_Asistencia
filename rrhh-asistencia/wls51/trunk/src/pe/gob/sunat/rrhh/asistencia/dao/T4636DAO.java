package pe.gob.sunat.rrhh.asistencia.dao;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.Propiedades;

/** 
 * 
 * Clase       : T4636DAO 
 * Descripcion : Clase encargada de administrar los datos de la tabla t4636MovXCat (Movimientos asignados a categorias)
 * Proyecto    : ASISTENCIA 
 * Autor       : ICAPUNAY
 * Fecha       : 23-AGOSTO-2011 
 * 
 * */

public class T4636DAO extends DAOAbstract{
	
	private DataSource datasource;
	
	Propiedades constantes = new Propiedades(getClass(), "/constantes.properties");
	
	
	private final StringBuffer INSERTAR_MOVIMIENTO = new StringBuffer("INSERT INTO t4636MovXCat "
			).append("(cod_mov,cod_cate,cod_usucreac,fec_creac) "
			).append("VALUES (?, ?, ?, ?) ");


	private final StringBuffer FIND_MOVIMIENTOS_BY_CATEGORIA = new StringBuffer("select mc.cod_mov, m.descrip,mc.cod_cate,c.descrip as descripCate "
			).append("from t4635categoria c, t4636MovXCat mc, t1279tipo_mov m "	
			).append("where c.cod_cate=mc.cod_cate and mc.cod_mov=m.mov "	
			).append("and mc.cod_cate=? order by m.descrip asc ");		

	//nuevo agregado 05/12/2011 icapunay
	/*
	private final StringBuffer FIND_ALL_MOVIMIENTOS_ACTIVOS = new StringBuffer("select mov,descrip "
			).append("from t1279tipo_mov where est_id=? order by descrip asc ");*/	
	
	private final StringBuffer FIND_ALL_MOVIMIENTOS = new StringBuffer("select mov,descrip,califica,medida,est_id,tipo_id "
	).append("from t1279tipo_mov order by descrip asc ");
	//fin nuevo agregado 05/12/2011 icapunay
	
	private final StringBuffer FIND_ALL_MOVIMIENTOS_ASIGNADOS = new StringBuffer("select distinct(cod_mov) "
	).append("from t4636MovXCat ");	
	
	private final StringBuffer DELETE_ALL_MOVIMIENTOS_BY_CATEGORIA = new StringBuffer("DELETE FROM t4636MovXCat "	
			).append("WHERE cod_cate=? ");
	
	private final StringBuffer DELETE_MOVIMIENTO = new StringBuffer("DELETE FROM t4636MovXCat "	
	).append("WHERE cod_cate=? AND cod_mov=? ");
			
	/**
	* 
	* Este constructor del DAO dicierne como crear el datasource
	* dependiendo del tipo de objeto que recibe. Esto nos ayuda a
	* mejorar la invocacion del dao.
	* 
	* @param datasource Object
	*/
	public T4636DAO(Object datasource) {
		if (datasource instanceof DataSource)
		  this.datasource = (DataSource)datasource;
		else if (datasource instanceof String)
		  this.datasource = getDataSource((String)datasource);
		else
		  throw new DAOException(this, "Datasource no valido");
	}
	
	/**
	 * Metodo que se encarga de registrar un movimiento para una categoria
	 * @param params Map (cod_mov,cod_cate,cod_usucreac,fec_creac)  
	 * @return boolean	 
	 * @throws DAOException
	 */	
	public boolean insertMovimientoParaCategoria(Map params) throws DAOException {
	
		int insertado=0;
		if (log.isDebugEnabled()) log.debug("T4636DAO - insertMovimientoParaCategoria - params " + params);		
		try{
			insertado = executeUpdate(datasource, INSERTAR_MOVIMIENTO.toString(), new Object[]{params.get("cod_mov"), 
				params.get("cod_cate"), params.get("cod_usucreac") , params.get("fec_creac")});
		} catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [INSERTAR_MOVIMIENTO]");
		}
		
		return (insertado>0);
	}
	
	/**
	  * Metodo que devuelve los movimientos asignados a una determinada categoria
	  * @param codCategoria String
	  * @return List
	  * @throws DAOException
	  */
	public List findMovimientosByCategoria(String codCategoria) throws DAOException {
		
		List movimientos= null;
		if (log.isDebugEnabled()) log.debug("T4636DAO - findMovimientosByCategoria - codCategoria " + codCategoria);
		try{
			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			movimientos = executeQuery(datasource, FIND_MOVIMIENTOS_BY_CATEGORIA.toString(), new Object[]{codCategoria});
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [FIND_MOVIMIENTOS_BY_CATEGORIA]");
		}	
		
		return movimientos;
	}
	
	/**
	  * Metodo que obtiene todos los movimientos de la tabla t1279tipo_mov	
	  * @return List
	  * @throws DAOException
	  */
	//public List findAllMovimientosActivos() throws DAOException { //nuevo agregado 05/12/2011 icapunay
	public List findAllMovimientos() throws DAOException { //nuevo agregado 05/12/2011 icapunay
		
		List movimientosT1279= null;
		if (log.isDebugEnabled()) log.debug("T4636DAO - findAllMovimientos");
		try{
			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			//movimientosT1279 = executeQuery(datasource, FIND_ALL_MOVIMIENTOS_ACTIVOS.toString(), new Object[]{constantes.leePropiedad("ACTIVO")}); //nuevo agregado 05/12/2011 icapunay
			movimientosT1279 = executeQuery(datasource, FIND_ALL_MOVIMIENTOS.toString()); //nuevo agregado 05/12/2011 icapunay
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [FIND_ALL_MOVIMIENTOS]");
		}	
		
		return movimientosT1279;
	}	
	
	/**
	  * Metodo que obtiene todos los movimientos asignados a todas las categorias	
	  * @return List
	  * @throws DAOException
	  */
	public List findAllMovimientosAsignados() throws DAOException {
		
		List movimientosAsignados= null;
		if (log.isDebugEnabled()) log.debug("T4636DAO - findAllMovimientosAsignados");
		try{
			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			movimientosAsignados = executeQuery(datasource, FIND_ALL_MOVIMIENTOS_ASIGNADOS.toString());
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [FIND_ALL_MOVIMIENTOS_ASIGNADOS]");
		}	
		
		return movimientosAsignados;
	}	
	
	/**
	 * Metodo encargado de eliminar(fisico) todos los movimientos de una categoria a partir de la llave cod_cate
	 * @param codCategoria String
	 * @return boolean
	 * @throws DAOException
	 */
	public boolean deleteAllMovimientosByCategoria(String codCategoria) throws DAOException {
		
		int eliminado=0;
		if (log.isDebugEnabled()) log.debug("T4636DAO - deleteAllMovimientosByCategoria - codCategoria " + codCategoria);
		try{
					
			eliminado = executeUpdate(datasource, DELETE_ALL_MOVIMIENTOS_BY_CATEGORIA.toString(), new Object[]{codCategoria});
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [DELETE_ALL_MOVIMIENTOS_BY_CATEGORIA]");
		}		
		
		return (eliminado>0);
	}
	
	/**
	 * Metodo encargado de eliminar(fisico) un movimiento asignado a una categoria a partir de las llaves cod_cate y cod_mov
	 * @param codCategoria String
	 * @param codMovimiento String
	 * @return boolean
	 * @throws DAOException
	 */
	public boolean deleteMovimiento(String codCategoria, String codMovimiento) throws DAOException {
		
		int eliminado=0;
		if (log.isDebugEnabled()) log.debug("T4636DAO - deleteMovimiento - codCategoria " + codCategoria);
		if (log.isDebugEnabled()) log.debug("T4636DAO - deleteMovimiento - codMovimiento " + codMovimiento);
		try{
					
			eliminado = executeUpdate(datasource, DELETE_MOVIMIENTO.toString(), new Object[]{codCategoria,codMovimiento});
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [DELETE_MOVIMIENTO]");
		}		
		
		return (eliminado>0);
	}
	
	
}
