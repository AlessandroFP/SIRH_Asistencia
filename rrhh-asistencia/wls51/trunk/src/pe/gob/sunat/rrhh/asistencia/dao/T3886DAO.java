package pe.gob.sunat.rrhh.asistencia.dao;

import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.date.FechaBean;

/** 
 * 
 * Clase       : T3886DAO 
 * Descripcion : clase encargada de administrar los datos de la tabla T3886CONV_VAC 
 * Proyecto    : ASISTENCIA 
 * Autor       : Enzo Benavides
 * Fecha       : 12-MAR-2007 
 * 
 * */
public class T3886DAO extends DAOAbstract {
	
	private DataSource dataSource = null;
	
	private final StringBuffer FINDCONVIGENTE = new StringBuffer("SELECT t3886.cod_pers, t3886.per_convenio ")
	.append(" FROM t3886conv_vac t3886, t1281vacaciones_c t1281 ")
	.append(" WHERE t3886.cod_pers = ? AND t3886.fec_finconvenio > ?  AND t3886.ind_estado = '1' ")
	.append(" and t3886.cod_pers=t1281.cod_pers and t3886.per_convenio=t1281.anno and t1281.saldo>0 ");
	
	private final StringBuffer INSERTCONVENIO =  new StringBuffer("INSERT INTO t3886conv_vac (")
	.append(" cod_pers, per_convenio, ind_estado, ann_convenio, num_convenio, ")
	.append(" fec_finconvenio, cod_usureg, fec_registro, cod_usumodif, fec_modif ) VALUES ")
	.append(" (?,?,?,?,?,?,?,?,?,?) ");
	
	private final StringBuffer FINDCONVENIO = new StringBuffer("SELECT t3886.cod_pers, t3886.per_convenio ")
	.append(" FROM t3886conv_vac t3886 ")
	.append(" WHERE t3886.cod_pers = ? AND t3886.per_convenio = ?  AND t3886.ind_estado = '1' ");
		
	
	/**
	 *@param datasource Object
	 *
	 * */
	public T3886DAO(Object datasource) {
		if (datasource instanceof DataSource)
	      this.dataSource = (DataSource)datasource;
	    else if (datasource instanceof String)
	      this.dataSource = getDataSource((String)datasource);
	    else
	      throw new DAOException(this, "Datasource no valido");
	}
	
	/**
	  * Metodo que obtiene el convenio vigente para un trabajador 
	  * @param datos (String cod_pers)
	  * @return
	  * @throws DAOException
	  */
	public Map findConvenioVigente(Map datos) throws DAOException {
		
		Map convenio = executeQueryUniqueResult(dataSource, FINDCONVIGENTE.toString(), new Object[]{ 
					datos.get("cod_pers"), new FechaBean().getSQLDate()} );
		
		return convenio;
	}
	
	/**
	 * Metodo encargado de insertar un registro en la tabla t3886conv_vac
	 * @param params (cod_pers, per_convenio, ind_estado, ann_convenio, num_convenio,
	 * fec_finconvenio, cod_usureg, fec_registro, cod_usumodif, fec_modif)
	 * @throws DAOException
	 */
	public boolean insertarConvenio(Map params) throws DAOException {
	  //anno, numero, u_organ, cod_pers, num_seguim, fecha_recep, fecha_deriv, accion_id, ")
      //estado_id, cuser_orig, cuser_dest, tobserv, fcreacion, cuser_crea
	  if (log.isDebugEnabled())log.debug("T3886DAO insertarConvenio - params: ");	
	  int modificado = executeUpdate(dataSource, INSERTCONVENIO.toString(), new Object[]{params.get("cod_pers"),
	  params.get("per_convenio"), params.get("ind_estado") , params.get("ann_convenio"), params.get("num_convenio"),
	  params.get("fec_finconvenio"),params.get("cod_usureg"), params.get("fec_registro") , params.get("cod_usumodif"), 
	  params.get("fec_modif")});
	  return (modificado>0);
	}
	
	
	/**
	  * Metodo que obtiene el convenio activo de un trabajador por periodo 
	  * @param datos (String cod_pers)
	  * @return
	  * @throws DAOException
	  */
	public Map findConvenioVigPeriodo(Map datos) throws DAOException {
		
		Map convenio = executeQueryUniqueResult(dataSource, FINDCONVENIO.toString(), new Object[]{ 
					datos.get("cod_pers"), datos.get("per_convenio")} );
		
		return convenio;
	}
	
	
}
