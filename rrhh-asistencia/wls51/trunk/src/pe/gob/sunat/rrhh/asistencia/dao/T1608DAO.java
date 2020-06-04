package pe.gob.sunat.rrhh.asistencia.dao;

import java.sql.Timestamp;
import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;

/** 
 * 
 * Clase       : T1608DAO
 * Version     : 2.0 
 * Descripcion : Clase encargada de administrar los datos de la tabla t1608compensa 
 * Proyecto    : ASISTENCIA 
 * Autor       : JROJAS4
 * Fecha       : 20-MAY-2009 
 * 
 * */

public class T1608DAO extends DAOAbstract { 
	
	private DataSource datasource;	
	
	//JRR
	private final StringBuffer INSERT_COMPENSACION = new StringBuffer("INSERT INTO t1608compensa "
			).append("(cod_personal, fcompensa, qhoras, ind_estado_id, fcreacion, cuser_crea, anno, num_refer) "
			).append("VALUES (?, ?, ?, ?, ?, ?, ?, ?) ");

	//JRR
	private final StringBuffer FIND_COMPENSACION_PERSONA_BY_PK = new StringBuffer("SELECT "
			).append("cod_personal, fcompensa, qhoras, ind_estado_id, fcreacion, cuser_crea, anno, num_refer "
			).append("FROM 	t1608compensa "
			).append("WHERE cod_personal = ? "
			).append("AND fcompensa = ? ");
	
	//JRR
	private final StringBuffer DELETE_COMPENSACION_PERSONA = new StringBuffer("DELETE FROM t1608compensa "
			).append("WHERE cod_personal = ? AND anno = ? AND num_refer = ? ");
	
	//JRR - No se utiliza aun
	/*private final StringBuffer UPDATE_COMPENSACION_PERSONA = new StringBuffer("UPDATE t1608compensa "
			).append("SET qhoras = ?, "
			).append("    fcreacion = ?, "
			).append("    cuser_crea = ? "					
			).append("WHERE cod_personal = ? "
			).append("AND   fcompensa = ? "); */
	
	//JRR
	private final StringBuffer FIND_HORAS_COMPENSA = new StringBuffer("SELECT qhoras FROM t1608compensa "
			).append("WHERE cod_personal = ? "
			).append("AND fcompensa = ? "					
			).append("AND ind_estado_id = ? ");

	/**
	* 
	* Este constructor del DAO dicierne como crear el datasource
	* dependiendo del tipo de objeto que recibe. Esto nos ayuda a
	* mejorar la invocacion del dao.
	* 
	* @param datasource Object
	*/
	public T1608DAO(Object datasource) {
		if (datasource instanceof DataSource)
		  this.datasource = (DataSource)datasource;
		else if (datasource instanceof String)
		  this.datasource = getDataSource((String)datasource);
		else
		  throw new DAOException(this, "Datasource no valido");
	}
	
	
	/**
	 * Metodo que se encarga de registrar la compensacion de un trabajador
	 * @throws DAOException
	 */	
	public void registrarCompensacionPersona(Map datos) throws DAOException {		
		if (log.isDebugEnabled()) log.debug("registrarCompensacionPersona - datos: " + datos);	
		FechaBean fb = new FechaBean(datos.get("fcompensa").toString());
		executeUpdate(datasource, INSERT_COMPENSACION.toString(), new Object[]{datos.get("cod_personal"), 
			fb.getSQLDate(), datos.get("qhoras"), datos.get("ind_estado_id"), 
									datos.get("fcreacion"), datos.get("cuser_crea"), datos.get("anno"), 
									datos.get("num_refer")});
	}
	
	/**
	 * Metodo que busca la compensacion de un trabajador
	 * @throws DAOException
	 */	
	public Map findCompensacionPersonaByPK(Map datos) throws DAOException {		
		Map mapa = null;
		try{
			if (log.isDebugEnabled()) log.debug("findCompensacionPersonaByPK - datos: "+datos);
			FechaBean fb = new FechaBean(datos.get("fcompensa").toString());
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			mapa = executeQueryUniqueResult(datasource, FIND_COMPENSACION_PERSONA_BY_PK.toString(),
					new Object[]{datos.get("cod_personal"), fb.getSQLDate()});
		} catch (Exception e) {
			log.error("*** SQL Error ****",e);
		}
		return mapa;
	}
	
	/**
	  * Metodo que elimina la compensacion de un trabajador
	  * @param datos
	  * @return
	  * @throws DAOException
	  */
	public boolean eliminarCompensacionPersona(Map params) throws DAOException {
		if (log.isDebugEnabled()) log.debug("eliminarCompensacionPersona - params: " + params);
		int eliminados = 0;
		eliminados = executeUpdate(datasource, DELETE_COMPENSACION_PERSONA.toString(), 
				new Object[]{params.get("cod_personal"), params.get("anno"), params.get("num_refer")});
		return (eliminados>0)?true:false;
	}

	/**
	 * Metodo que se encarga de actualizar la compensacion de un trabajador
	 * @throws DAOException
	 */
/*	public void actualizarCompensacionPersona(Map datos) throws DAOException {	
		if (log.isDebugEnabled()) log.debug("actualizarCompensacionPersona - datos: " + datos);	
		
		executeUpdate(datasource, UPDATE_COMPENSACION_PERSONA.toString(), 
						new Object[]{datos.get("qhoras"),
									 datos.get("fcreacion"),
									 datos.get("cuser_crea"),
									 datos.get("cod_personal"),
 									 datos.get("fcompensa")
 									 });
	}*/
	
	/**
	 * Metodo encargado de obtener el total de horas a compensar en un dia
	 * @throws DAOException
	 */	
	public Map findHorasCompensa(Map datos) throws DAOException {		
		Map mapa = null;
		try{
			if (log.isDebugEnabled()) log.debug("findHorasCompensa - datos: "+datos);
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			mapa = executeQueryUniqueResult(datasource, FIND_HORAS_COMPENSA.toString(),
					new Object[]{datos.get("cod_personal"), datos.get("fcompensa"), Constantes.ACTIVO});
		} catch (Exception e) {
			log.error("*** SQL Error ****",e);
		}
		return mapa;
	}

}