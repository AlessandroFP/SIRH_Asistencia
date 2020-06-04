package pe.gob.sunat.rrhh.asistencia.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;

/** 
 * 
 * Clase       : T1281DAO 
 * Descripcion : clase encargada de administrar los datos de la tabla t1281vacaciones_c 
 * Proyecto    : ASISTENCIA 
 * @author     : PRAC-JCALLO
 * Fecha       : 11-JUN-2008 
 * 
 * */

public class T1281DAO extends DAOAbstract {
	
	private DataSource dataSource = null;
		
	private final StringBuffer findAllColumnsByKey = new StringBuffer("SELECT cod_pers, anno, dias, saldo, ")
	.append(" saldo_temp, cuser_mod, fmod from t1281vacaciones_c where cod_pers = ? and anno = ?");
	
	private final StringBuffer insertarCabVacacion =  new StringBuffer("INSERT INTO t1281vacaciones_c ")
	.append(" (cod_pers, anno, dias, saldo, saldo_temp, cuser_crea, fcreacion) VALUES (?,?,?,?,?,?,?) ");

	//COMSA PROGRAMACION DE VACACIONES
	private final StringBuffer findByCodPersSaldoAnno =  new StringBuffer(" ")
	.append("SELECT cod_pers, anno, dias, saldo ")
	.append("FROM  t1281vacaciones_c v, t02perdp p ")
	.append("WHERE v.cod_pers = p.t02cod_pers and " )
	.append("v.cod_pers = ?   ");
	
	private final StringBuffer findSaldos = new StringBuffer("SELECT ")
	.append(" cod_pers, anno, saldo ")
	.append(" from t1281vacaciones_c where anno >= ? and saldo > ? ");
	
	private final String deleteByPK = 
		"DELETE FROM t1281vacaciones_c "+
		"WHERE "+
		"    cod_pers = ? "+
		"    AND anno = ? ";
	
	/**
	 *@param datasource Object
	 *
	 * */
	public T1281DAO(Object datasource) {
		if (datasource instanceof DataSource)
	      this.dataSource = (DataSource)datasource;
	    else if (datasource instanceof String)
	      this.dataSource = getDataSource((String)datasource);
	    else
	      throw new DAOException(this, "Datasource no valido");
	}
	
	/**
	  * Metodo que obtiene los datos a partir de la llave 
	  * @param datos (String cod_pers, String anno)
	  * @return
	  * @throws DAOException
	  */
	public Map findAllColumnsByKey(Map datos) throws DAOException {
		
		Map soli= null; //ICAPUNAY 21/06/2011 FORMATIVAS-PARTEII
		if (log.isDebugEnabled()) log.debug("T1281DAO - findAllColumnsByKey - datos: " + datos);//ICAPUNAY 21/06/2011 FORMATIVAS-PARTEII
		try{//ICAPUNAY 21/06/2011 FORMATIVAS-PARTEII
		
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);//ICAPUNAY 21/06/2011 FORMATIVAS-PARTEII
			soli = executeQueryUniqueResult(dataSource, findAllColumnsByKey.toString(), new Object[]{datos.get("cod_pers"), datos.get("anno")});
			
		}catch (Exception e) {//ICAPUNAY 21/06/2011 FORMATIVAS-PARTEII
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [findAllColumnsByKey]");
		}//ICAPUNAY 21/06/2011 FORMATIVAS-PARTEII	
		
		return soli;
	}
	
	/**
	 * Metodo encargado de insertar un registro en la tabla t1281vacaciones_c
	 * @param params (String cod_pers, String anno, Integer saldo, Integer saldo_temp, String cuser_crea,TimeStamp fcreacion)
	 * @throws DAOException
	 */
	public boolean insertarCabVacacion(Map params) throws DAOException {
		/** cod_pers, anno, dias, saldo, saldo_temp, cuser_crea, fcreacion**/
		int modificado = executeUpdate(dataSource, insertarCabVacacion.toString(), new Object[]{params.get("cod_pers"), 
			params.get("anno"), params.get("dias") , params.get("saldo") , params.get("saldo_temp"), params.get("cuser_crea"),params.get("fcreacion")});
		
		return (modificado>0);
	}
	
	/**
	 * Metodo encargado de actualizar solo las campos especificados de un registro
	 * @param params contiene (Map columns, String cod_pers, String anno)
	 * @throws DAOException
	 */
	public boolean updateCustomColumns(Map params) throws DAOException {
		
		StringBuffer strSQL = new StringBuffer("UPDATE t1281vacaciones_c ");
		List listaVal = new ArrayList();
		Map columns = (params.get("columns") != null) ? (HashMap)params.get("columns"): new HashMap();
		
		if(columns != null && !columns.isEmpty()) {
			Iterator it = columns.entrySet().iterator();
			boolean first = true;//para ver si es el primer campo de la sentencia SQL
			while (it.hasNext()) {
				Map.Entry e = (Map.Entry)it.next();
				if (first) {
					strSQL.append(" set "+e.getKey()+"= ? ");
					listaVal.add(e.getValue());
					first = false;
				} else {
					strSQL.append(", "+e.getKey()+"= ? ");
					listaVal.add(e.getValue());
				}				
			}
		}
		
		strSQL.append(" WHERE cod_pers = ? AND anno = ? ");
		if(log.isDebugEnabled()) log.debug("SQL : "+strSQL.toString()); 
		//los datos de la llave primaria
		listaVal.add(params.get("cod_pers"));
		listaVal.add(params.get("anno"));
				
		int modificado = executeUpdate(dataSource, strSQL.toString(), listaVal.toArray());
		
		return (modificado>0);
	}
	
	//COMSA PROGRAMACION DE VACACIONES		
	/**
	 * Metodo encargado de buscar los registros de cabecera de vacaciones para un
	 * trabajador y ano especifico, opcionalmente filtrados por su saldo a
	 * favor.
	 * @param codPers
	 *            String. Numero de registro del trabajador.
	 * @param saldoFavor
	 *            Boolean. Que indica si se desea obtener solo los registros con
	 *            saldo a favor.
	 * @param anho
	 *            Ano de busqueda.
	 * @return List conteniendo los registros del resultado de la busqueda
	 * 
	 * @throws DAOException
	 */
	
	public List findByCodPersSaldoAnno(Map datos)	throws DAOException {
		StringBuffer sWhere = new StringBuffer("");
    String sOrderby =" order by anno desc ";
		StringBuffer strSQL = new StringBuffer("");
    

		if (datos.get("anho")!=null) {
			if (!"".equals(datos.get("anho").toString().trim())) {
				sWhere.append(" and anno = ").append(datos.get("anho").toString().trim());
			}
		}
		if (datos.get("saldo")!=null) {
			if (!"".equals(datos.get("saldo").toString().trim())) {
				sWhere.append(" and saldo > 0");
			}
		}
		
		strSQL.append(findByCodPersSaldoAnno.toString()).append(sWhere.toString()).append(sOrderby);
		List lista = executeQuery(dataSource, strSQL.toString(), new Object[]{
		datos.get("codPers").toString()});
		
		return lista;
	}
	
	//COMSA PROGRAMACION DE VACACIONES
	/**
	 * Metodo encargado de buscar el saldo para un codigo de Persona y periodo de vacacion dado
	 * @param Map dBean : contiene el Numero de registro del trabajador y el anno de vacacion.
	 * @return String conteniendo el saldo
	 * 
	 * @throws DAOException
	 */
	
	public String traerSaldo(Map dBean)	throws DAOException {
		String saldo="";
		Map mapaSaldo = executeQueryUniqueResult(dataSource, findAllColumnsByKey.toString(), new Object[]{ 
			dBean.get("txtregistro").toString(),dBean.get("cod_periodo").toString()});
		if (mapaSaldo!=null) {
			saldo = mapaSaldo.get("saldo").toString();
		} else {
			saldo="0";
		}
		return saldo;
	}
	
	//COMSA PROGRAMACION DE VACACIONES
	/**
	  * Metodo que obtiene los datos de la tabla de saldos 
	  * @param Map params
	  * @return List
	  * @throws DAOException
	  */
	
	public List findSaldos(Map params) throws DAOException {
		
		List saldos = executeQuery(dataSource, findSaldos.toString(), new Object[]{ 
			params.get("anno") , params.get("saldominimo")} );
		
		return saldos;
	}
	
	
	/**
	 * Metodo encargado de eliminar(fisico) un registro de la tabla t1282vacacion_c
	 * adicionalmente se añade el indicador 
	 * @param params Llave compuesta de la tabla
	 * @return Si el resultado tuvo éxito
	 * @throws DAOException
	 */
	public boolean deleteByPK(Map params) throws DAOException {
		int modificado;
		try {
			List lstParams = new ArrayList();
			lstParams.add(params.get("cod_pers"));
			lstParams.add(params.get("anno"));
			modificado = executeUpdate(dataSource, deleteByPK, lstParams.toArray());
		}catch(Exception e){
			throw new DAOException(this, "No se pudo cargar la informacion");
		}
		return (modificado>0);
	}
}
