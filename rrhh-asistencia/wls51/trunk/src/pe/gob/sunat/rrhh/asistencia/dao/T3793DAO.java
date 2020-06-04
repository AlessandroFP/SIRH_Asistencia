package pe.gob.sunat.rrhh.asistencia.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;

/** 
 * 
 * Clase       : T3793DAO 
 * Descripcion : Clase encargada de administrar los datos de la tabla t3793detcomp_bolsa 
 * Proyecto    : ASISTENCIA 
 * Autor       : JROJAS4
 * Fecha       : 21-OCT-2008 
 * 
 * */

public class T3793DAO extends DAOAbstract{
	
	private DataSource datasource;
	
	//JRR
	private final StringBuffer INSERTAR_DETALLE_COMPENSA = new 	StringBuffer("INSERT INTO t3793detcomp_bolsa "
			).append("(num_licencia, fec_licencia, cod_pers, cnt_tiempo, fec_compensa, fec_creacion, cod_usucrea) "
			).append("VALUES (?, ?, ?, ?, ?, ?, ?) ");

	//JRR
	private final StringBuffer FIND_TIEMPO_COMPENSADO_FECHA = new  StringBuffer("SELECT num_licencia, fec_licencia, "
			).append("cod_pers, fec_compensa, cnt_tiempo "	
			).append("FROM  t3793detcomp_bolsa "
			).append("WHERE num_licencia = ? "
			).append("AND fec_licencia = ? "
			).append("AND cod_pers = ? "
			).append("AND fec_compensa = ? ");

	//JRR
	private final StringBuffer FIND_TIEMPO_COMPENSADO_FECHA_CERRADO = new  StringBuffer("SELECT sum(distinct A.cnt_tiempo) as tiempo_cerrado "
			).append("FROM  t3793detcomp_bolsa A, t3792comp_bolsa B "
			).append("WHERE A.cod_pers = ? "
			).append("AND A.fec_compensa = ? "
			).append("AND A.num_licencia = B.num_licencia "					
			).append("AND A.fec_licencia = B.fec_licencia "
			).append("AND A.cod_pers = B.cod_pers "					
			).append("AND B.ind_estado = '2'");

	//JRR
	private final StringBuffer DELETE_DETALLE_COMPENSACION = new  StringBuffer("DELETE FROM t3793detcomp_bolsa  "	
			).append("WHERE num_licencia = ? "
			).append("AND fec_licencia = ? "
			).append("AND cod_pers = ? "
			).append("AND fec_compensa = ? ");

	
	/**
	* 
	* Este constructor del DAO dicierne como crear el datasource
	* dependiendo del tipo de objeto que recibe. Esto nos ayuda a
	* mejorar la invocacion del dao.
	* 
	* @param datasource Object
	*/
	public T3793DAO(Object datasource) {
		if (datasource instanceof DataSource)
		  this.datasource = (DataSource)datasource;
		else if (datasource instanceof String)
		  this.datasource = getDataSource((String)datasource);
		else
		  throw new DAOException(this, "Datasource no valido");
	}
	
	/**
	 * Metodo que se encarga de registrar la compensacion
	 * de un trabajador
	 * @throws DAOException
	 */	
	public void registrarDetalleCompensacion(Map datos) throws DAOException {		
		if (log.isDebugEnabled()) log.debug("T3793DAO - registrarDetalleCompensacion - datos " + datos);		
		executeUpdate(datasource, INSERTAR_DETALLE_COMPENSA.toString(), 
						new Object[]{datos.get("num_licencia"), 
									 datos.get("fec_licencia"), datos.get("cod_pers"), 
									 datos.get("cnt_tiempo"), datos.get("fec_compensa"), 
									 datos.get("fec_creacion"), datos.get("cod_usucrea")});		
	}
	
	/**
	 * Obtiene los detalles por compensar de alguna licencia en una fecha determinada 
	 * de un trabajador.
	 * @throws DAOException
	 */	
	public List findDetalleCompensaFecha(Map datos) throws DAOException {	
		List lista = new ArrayList();
		try {
			if (log.isDebugEnabled()) log.debug("T3793DAO - findDetalleCompensaFecha - datos: "+datos);
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			lista = executeQuery(datasource, FIND_TIEMPO_COMPENSADO_FECHA.toString(), 
						new Object[]{datos.get("num_licencia"), datos.get("fec_licencia"),
									datos.get("cod_pers"), datos.get("fec_compensa")});
		} catch (Exception e) {
			log.error("*** SQL Error ****",e);
		}
		return lista;
	}
	
	/**
	 * Compensa el detalle por compensar de alguna licencia en una fecha determinada 
	 * de un trabajador.
	 * @throws DAOException
	 */
	public void eliminarDetalleCompensa(Map datos) throws DAOException {		
		if (log.isDebugEnabled()) log.debug("T3793DAO - eliminarDetalleCompensa - datos: " +datos);		
		executeUpdate(datasource, DELETE_DETALLE_COMPENSACION.toString(), 
									new Object[]{datos.get("num_licencia"), datos.get("fec_licencia"),			
													datos.get("cod_pers"), datos.get("fec_compensa")});		
	}

	
	/**
	 * Obtiene el tiempo utilizado de una fecha en compensaciones ya cerradas 
	 * de un trabajador.
	 * @throws DAOException
	 */	
	public int findTiempoCompensadoFechaCerrado(Map datos) throws DAOException {	
		List lista = new ArrayList();
		int entero = 0;
		try {
			if (log.isDebugEnabled()) log.debug("T3793DAO - findTiempoCompensadoFechaCerrado - datos: "+datos);
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			lista = executeQuery(datasource, FIND_TIEMPO_COMPENSADO_FECHA_CERRADO.toString(), 
						new Object[]{datos.get("cod_pers"), datos.get("fec_compensa")});
			
			if (lista!=null && lista.size()>0) {
				Map hm = (HashMap) lista.get(0);
				if (hm.get("tiempo_cerrado")!= null) 
					entero = Integer.parseInt(hm.get("tiempo_cerrado").toString());
			} 
			
		} catch (Exception e) {
			log.error("*** SQL Error ****",e);
		}
		return entero;
	}
	
}
