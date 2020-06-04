package pe.gob.sunat.rrhh.asistencia.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.Propiedades;

/**
 * 
 * Clase       : T1277DAO 
 * Descripcion : 
 * Proyecto    : ASISTENCIA 
 * Autor       : JROJAS4
 * Fecha       : 07-MAR-2007 12:02:21
 */
public class T1277DAO extends DAOAbstract{
	
	private DataSource dataSource = null;
	
	Propiedades constantes = new Propiedades(getClass(), "/constantes.properties");
	
	private final StringBuffer findAllColumnsByKey = new StringBuffer("SELECT anno, numero, u_organ, cod_pers, nvl(asunto,'') as asunto, fecha, nvl(cargo,'') as cargo, ")
	.append(" licencia, ffinicio, ffin, dias, nvl(anno_vac,'') as anno_vac, nvl(est_id,'') as est_id, ")
	.append(" nvl(seguim_act,'') as seguim_act, nvl(observ,'') as observ, nvl(cod_crea,'') as cod_crea, " )
	.append(" fcreacion, nvl(cuser_crea,'') as cuser_crea, fmod, nvl(cuser_mod,'') as cuser_mod")
	.append(" from t1277solicitud where anno = ? and numero = ? and u_organ = ? and cod_pers = ? and licencia = ? and ffinicio = ?");

/*	
	private final StringBuffer insertarSolicitud =  new StringBuffer("INSERT INTO t1277solicitud ( anno, numero, u_organ, cod_pers, asunto, fecha, cargo, ")
	.append(" licencia, ffinicio, ffin, dias, anno_vac,est_id, seguim_act, observ, cod_crea, fcreacion, cuser_crea ) VALUES ")
	.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
*/	
	
	private final StringBuffer totalDiasPendVac = new StringBuffer("SELECT SUM(unique t1277.dias) AS total ")
	.append("FROM	t1277solicitud t1277, t1455sol_seg t1455 ")
	.append("WHERE	t1277.cod_pers = ? AND t1277.anno_vac = ? AND t1455.estado_id = ? ")
	.append("AND (t1277.licencia = ? OR t1277.licencia = ?) ")
	.append("AND t1277.anno = t1455.anno AND t1277.numero = t1455.numero ")
	.append("AND t1277.cod_pers = t1455.cod_pers ")
	//JRR
	.append("AND t1277.seguim_act = t1455.num_seguim ");
	
	//FRD 22/04/2009
	private final StringBuffer findFechaSolicitud = new StringBuffer("Select fecha From t1277solicitud ")
	.append("Where anno = ? ")
	.append("And numero = ? And u_organ = ? And cod_pers = ?");

	//JRR - 10/11/2009 - Mas campos de Lic Nac Hijo
	private final StringBuffer insertarSolicitud =  new StringBuffer("INSERT INTO t1277solicitud ( anno, numero, u_organ, cod_pers, asunto, fecha, cargo, ")
	.append(" licencia, ffinicio, ffin, fec_nacim, fec_alta, dias, anno_vac,est_id, seguim_act, observ, cod_crea, fcreacion, cuser_crea ) VALUES ")
	.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
	
	
	/**
	 *@param datasource Object
	 *
	 * */
	public T1277DAO(Object datasource) {
		if (datasource instanceof DataSource)
	      this.dataSource = (DataSource)datasource;
	    else if (datasource instanceof String)
	      this.dataSource = getDataSource((String)datasource);
	    else
	      throw new DAOException(this, "Datasource no valido");
	}
	
	/**
	  * Metodo que obtiene los datos a partir de la llave 
	  * @param datos (String anno, Short numero, String u_organ, String cod_pers, String licencia, Timestamp ffinicio)
	  * @return
	  * @throws DAOException
	  */
	public Map findAllColumnsByKey(Map datos) throws DAOException {
		
		Map soli = executeQueryUniqueResult(dataSource, findAllColumnsByKey.toString(), new Object[]{ 
					datos.get("anno"), datos.get("numero"),
					datos.get("u_organ"), datos.get("cod_pers"),
					datos.get("licencia"), datos.get("ffinicio") } );
		
		return soli;
	}
	
	/**
	 * Metodo encargado de insertar un registro en la tabla t1274licen_med
	 * @param params (String periodo, Short numero, Timestamp ffinicio, String cod_pers, String licencia)
	 * @throws DAOException
	 *
	public boolean insertarSolicitud(Map params) throws DAOException {
		//anno, numero, u_organ, cod_pers, asunto, fecha, cargo, 
		//licencia, ffinicio, ffin, dias, anno_vac,est_id, seguim_act, observ, cod_crea, fcreacion, cuser_crea
		int modificado = executeUpdate(dataSource, insertarSolicitud.toString(), new Object[]{params.get("anno"), 
			params.get("numero"), params.get("u_organ") , params.get("cod_pers"), params.get("asunto"),params.get("fecha"),
			params.get("cargo"), params.get("licencia") , params.get("ffinicio"), params.get("ffin"), params.get("dias"), 
			params.get("anno_vac"), params.get("est_id"), params.get("seguim_act"), params.get("observ"), params.get("cod_crea"), 
			params.get("fcreacion"), params.get("cuser_crea")});
		
		return (modificado>0);
	}*/
	
	/**
	 * Metodo encargado de actualizar solo las campos especificados de un registro
	 * @param params contiene (Map columns, String anno, Short numero, String u_organ, String cod_pers, String licencia, Timestamp ffinicio)
	 * @throws DAOException
	 */
	public boolean updateCustomColumns(Map params) throws DAOException {
		
		StringBuffer strSQL = new StringBuffer("UPDATE t1277solicitud ");
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
		
		strSQL.append("  WHERE anno = ? and numero = ? and u_organ = ? and cod_pers = ? and licencia = ? and ffinicio = ?");
		if(log.isDebugEnabled()) log.debug("SQL : "+strSQL.toString()); 
		//los datos de la llave primaria
		listaVal.add(params.get("anno"));
		listaVal.add(params.get("numero"));
		listaVal.add(params.get("u_organ"));
		listaVal.add(params.get("cod_pers"));
		listaVal.add(params.get("licencia"));
		listaVal.add(params.get("ffinicio"));
		
		int modificado = executeUpdate(dataSource, strSQL.toString(), listaVal.toArray());
		
		return (modificado>0);
	}

	
	/********************************/
	
	/**
	 * Metodo encargado de obtener el Total de Dias de las Soliciudes Pendientes
	 * de Vacaciones (Goce de Vacaciones y Venta de Vacaciones)
	 * de cada trabajador para un determinado Año.
	 * @param params contiene (String anno, String cod_pers, String propiedad)
	 * @throws DAOException
	 */
	public int getTotalDiasPendVac(Map params) throws DAOException {
		if(log.isDebugEnabled()) log.debug("getTotalDiasPendVac: "+params);
		
		Map resultado = executeQueryUniqueResult(dataSource, totalDiasPendVac.toString(), 
				new Object[] { params.get("cod_pers"),
				params.get("anno"), constantes.leePropiedad("ESTADO_SEGUIMIENTO"),
				constantes.leePropiedad("VACACION"), constantes.leePropiedad("VACACION_VENTA")});
		
		if(log.isDebugEnabled()) log.debug("Total Recibido:"+ resultado);

		int total = resultado.get("total")!=null?(Integer.parseInt(resultado.get("total").toString())):0;
		if(log.isDebugEnabled()) log.debug("Total Convertido: " + total);
		
		return total;
	}
	
	
	/** FRD 22/04/2009
	 * Metodo que obtiene la fecha de creación de una solicitud 
	 * @param datos (int numero_ref, String u_organ, String cod_pers)
	 * @return
	 * @throws DAOException
	 */
	public Map findFechaRegistroSolicitud(Map datos) throws DAOException {
		if (log.isDebugEnabled()) log.debug("findFechaRegistroSolicitud - datos: " + datos);
		Map soli = executeQueryUniqueResult(dataSource, findFechaSolicitud.toString(), new Object[]{ 
			datos.get("anno_ref"), datos.get("numero_ref"), datos.get("u_organ"), datos.get("cod_pers") } );
		return soli;
	}
	
	
	/** JRR 10/11/2009
	 * Metodo encargado de insertar una Solicitud, se agregan campos fecNac y fecAlta para la Lic Nacimiento de hijo
	 * @param params (Todos los campos de la tabla)
	 * @throws DAOException
	 */
	public boolean insertarSolicitud(Map params) throws DAOException {
		if (log.isDebugEnabled()) log.debug("insertarSolicitud - params: " + params);
		int modificado = executeUpdate(dataSource, insertarSolicitud.toString(), new Object[]{params.get("anno"), 
			params.get("numero"), params.get("u_organ") , params.get("cod_pers"), params.get("asunto"),
			params.get("fecha"), params.get("cargo"), params.get("licencia") , params.get("ffinicio"), params.get("ffin"), 
			params.get("fecNac"), params.get("fecAlta"), params.get("dias"), params.get("anno_vac"),
			params.get("est_id"), params.get("seguim_act"), params.get("observ"), params.get("cod_crea"), 
			params.get("fcreacion"), params.get("cuser_crea")});
		
		return (modificado>0);
	}
	
	
}