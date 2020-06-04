package pe.gob.sunat.rrhh.asistencia.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.Propiedades;

/** 
 * 
 * Clase       : T4635DAO 
 * Descripcion : Clase encargada de administrar los datos de la tabla t4635Categoria (Categorias de clasificacion de movimientos de asistencia)
 * Proyecto    : ASISTENCIA 
 * Autor       : ICAPUNAY
 * Fecha       : 23-AGOSTO-2011 
 * 
 * */

public class T4635DAO extends DAOAbstract{
	
	private DataSource datasource;
	
	Propiedades constantes = new Propiedades(getClass(), "/constantes.properties");
	
	
	private final StringBuffer INSERTAR_CATEGORIA = new StringBuffer("INSERT INTO t4635Categoria "
			).append("(cod_cate,descrip,cod_usucreac,fec_creac) "
			).append("VALUES (?, ?, ?, ?) ");

	
	private final StringBuffer FIND_CATEGORIAS_BY_MOVIMIENTO = new StringBuffer("select c.* from t4635categoria c, t4636MovXCat mc, t1279tipo_mov m "
			).append("where c.cod_cate=mc.cod_cate and mc.cod_mov=m.mov "	
			).append("and m.mov=? ");		
	
	private final StringBuffer FIND_ALL_CATEGORIAS = new StringBuffer("select * "
	).append("from t4635categoria order by cod_cate asc ");	
	
	private final StringBuffer FIND_ALL_CATEGORIAS_ORDER_BY_DESCRIP = new StringBuffer("select cod_cate,descrip "
	).append("from t4635Categoria order by descrip asc ");	
		
	private final StringBuffer FIND_ULTIMA_CATEGORIA = new StringBuffer("select first 1 * from t4635categoria "	
	).append("order by cod_cate desc ");
	
	private final StringBuffer FIND_CATEGORIA = new StringBuffer("select cod_cate,descrip from t4635categoria "	
	).append("where cod_cate = ? ");

	private final StringBuffer UPDATE_DESCRIPCION_CATEGORIA = new StringBuffer("UPDATE t4635categoria "
			).append("SET descrip = ? "			
			).append("where cod_cate= ? ");
	
	private final StringBuffer DELETE_CATEGORIA_BY_CODIGO = new StringBuffer("DELETE FROM t4635categoria "	
			).append("WHERE cod_cate = ? ");
			

	
	/**
	* 
	* Este constructor del DAO dicierne como crear el datasource
	* dependiendo del tipo de objeto que recibe. Esto nos ayuda a
	* mejorar la invocacion del dao.
	* 
	* @param datasource Object
	*/
	public T4635DAO(Object datasource) {
		if (datasource instanceof DataSource)
		  this.datasource = (DataSource)datasource;
		else if (datasource instanceof String)
		  this.datasource = getDataSource((String)datasource);
		else
		  throw new DAOException(this, "Datasource no valido");
	}
	
	/**
	 * Metodo que se encarga de registrar una categoria
	 * @param params Map (cod_cate,descrip,cod_usucreac,fec_creac)  
	 * @return boolean	 
	 * @throws DAOException
	 */	
	public boolean insertCategoria(Map params) throws DAOException {
	
		int insertado=0;
		if (log.isDebugEnabled()) log.debug("T4635DAO - insertCategoria - params " + params);		
		try{
			insertado = executeUpdate(datasource, INSERTAR_CATEGORIA.toString(), new Object[]{params.get("cod_cate"), 
				params.get("descrip"), params.get("cod_usucreac") , params.get("fec_creac")});
		} catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [INSERTAR_CATEGORIA]");
		}
		
		return (insertado>0);
	}
	
	/**
	  * Metodo que devuelve las categorias que incluyen al codigo de movimiento ingresado
	  * @param codMovimiento String
	  * @return List
	  * @throws DAOException
	  */
	public List findCategoriasByMovimiento(String codMovimiento) throws DAOException {
		
		List notificaciones= null;
		if (log.isDebugEnabled()) log.debug("T4635DAO - findCategoriasByMovimiento - codMovimiento " + codMovimiento);
		try{
			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			notificaciones = executeQuery(datasource, FIND_CATEGORIAS_BY_MOVIMIENTO.toString(), new Object[]{codMovimiento});
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [FIND_CATEGORIAS_BY_MOVIMIENTO]");
		}	
		
		return notificaciones;
	}
	
	/**
	  * Metodo que obtiene todas las categorias registradas	
	  * @return List
	  * @throws DAOException
	  */
	public List findAllCategorias() throws DAOException {
		
		List categorias= null;
		if (log.isDebugEnabled()) log.debug("T4635DAO - findAllCategorias");
		try{
			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			categorias = executeQuery(datasource, FIND_ALL_CATEGORIAS.toString());
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [FIND_ALL_CATEGORIAS]");
		}	
		
		return categorias;
	}
	
	/**
	  * Metodo que obtiene todas las categorias ordenadas por descripción de la categoria
	  * @return List
	  * @throws DAOException
	  */
	public List findAllCategoriasOrderByDescrip() throws DAOException {
		
		List categorias= null;
		if (log.isDebugEnabled()) log.debug("T4635DAO - findAllCategoriasOrderByDescrip");
		try{
			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			categorias = executeQuery(datasource, FIND_ALL_CATEGORIAS_ORDER_BY_DESCRIP.toString());
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [FIND_ALL_CATEGORIAS_ORDER_BY_DESCRIP]");
		}	
		
		return categorias;
	}
	
	
	/**
	  * Metodo que devuelve la ultima categoria registrada	
	  * @return Map
	  * @throws DAOException
	  */
	public Map findUltimaCategoriaRegistrada() throws DAOException {
		
		
		Map categoria= null;
		if (log.isDebugEnabled()) log.debug("T4635DAO - findUltimaCategoriaRegistrada");
		try{
			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);			
			categoria = executeQueryUniqueResult(datasource, FIND_ULTIMA_CATEGORIA.toString());
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [FIND_ULTIMA_CATEGORIA]");
		}	
		
		return categoria;
	}
	
	/**
	  * Metodo que devuelve una categoria registrada	
	  * @param codCategoria String
	  * @return Map
	  * @throws DAOException
	  */
	public Map findCategoria(String codCategoria) throws DAOException {
		
		
		Map categoria= null;
		if (log.isDebugEnabled()) log.debug("T4635DAO - findCategoria - codCategoria " + codCategoria);
		try{
			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);			
			categoria = executeQueryUniqueResult(datasource, FIND_CATEGORIA.toString(), new Object[]{codCategoria});
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [FIND_CATEGORIA]");
		}	
		
		return categoria;
	}
	
	/**
	 * Metodo que se encarga de actualizar la descripción de una categoria
	 * @param params Map (descrip,cod_cate)
	 * @return boolean
	 * @throws DAOException
	 */
	
	public boolean updateCategoriaByPK(Map params) throws DAOException {
		
		List listaVal = new ArrayList();		
		listaVal.add(params.get("descrip"));
		listaVal.add(params.get("cod_cate"));		
		int actualizaDescrip=0;
		
		if (log.isDebugEnabled()) log.debug("T4635DAO - updateCategoriaByPK - params " + params);
		try{
		
			actualizaDescrip=executeUpdate(datasource,UPDATE_DESCRIPCION_CATEGORIA.toString(), listaVal.toArray());
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [UPDATE_DESCRIPCION_CATEGORIA]");
		}		
		
		return (actualizaDescrip>0);	
		
	}	
	
	/**
	 * Metodo encargado de eliminar(fisico) una categoria a partir de la llave cod_cate
	 * @param codCategoria String
	 * @return boolean
	 * @throws DAOException
	 */
	public boolean deleteCategoriaByPK(String codCategoria) throws DAOException {
		
		int eliminado=0;
		if (log.isDebugEnabled()) log.debug("T4635DAO - deleteCategoriaByPK - codCategoria " + codCategoria);
		try{
					
			eliminado = executeUpdate(datasource, DELETE_CATEGORIA_BY_CODIGO.toString(), new Object[]{codCategoria});
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [DELETE_CATEGORIA_BY_CODIGO]");
		}		
		
		return (eliminado>0);
	}
	
	
}
