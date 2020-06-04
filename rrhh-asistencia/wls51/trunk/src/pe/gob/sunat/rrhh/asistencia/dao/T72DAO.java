package pe.gob.sunat.rrhh.asistencia.dao;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;

/** 
 * 
 * Clase       : T72DAO 
 * Descripcion : Clase encargada de administrar los datos de la tabla t72resumen 
 * Proyecto    : ASISTENCIA 
 * Autor       : JROJAS4
 * Fecha       : 18-NOV-2008 
 * 
 * */

public class T72DAO extends DAOAbstract {	
	
	private DataSource datasource;	
	
	//JRR
	private final StringBuffer INSERTAR_RESUMEN_CIERRE = new 	StringBuffer("INSERT INTO t72resumen "
			).append("(cod_anho, cod_mes, ind_tip_planil, cod_persona, cod_anho_origen, cod_mes_origen, "
			).append("cod_concepto, num_min_stma, num_min_mod, num_dias, ind_estado, cod_usumodif, "
			//).append("fec_modif, "					
			).append("cod_usucrea, fec_creacion, ind_tipo_carga, ind_devol) "
			).append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");
	
	//JRR
	private final StringBuffer ELIMINAR_RESUMEN_CIERRE_CONCEPTO = new 	StringBuffer("DELETE FROM t72resumen "
			).append("WHERE cod_anho = ? AND cod_mes = ? AND cod_concepto = ? ");

	//JRR
	private final StringBuffer FIND_ENVIO_PLANILLAS = new 	StringBuffer("SELECT unique ind_estado FROM t72resumen "
			).append("WHERE cod_anho = ? AND cod_mes = ? ");

	
	/**
	* 
	* Este constructor del DAO dicierne como crear el datasource
	* dependiendo del tipo de objeto que recibe. Esto nos ayuda a
	* mejorar la invocacion del dao.
	* 
	* @param datasource Object
	*/
	public T72DAO(Object datasource) {
		if (datasource instanceof DataSource)
		  this.datasource = (DataSource)datasource;
		else if (datasource instanceof String)
		  this.datasource = getDataSource((String)datasource);
		else
		  throw new DAOException(this, "Datasource no valido");
	}
		
	/**
	 * Metodo que se encarga de registrar el resumen de cierre
	 * de un trabajador
	 * @throws DAOException
	 */	
	public void registrarResumenCierre(Map datos) throws DAOException {		
		//if (log.isDebugEnabled()) log.debug("registrarResumenCierre - datos" + datos);	
		executeUpdate(datasource, INSERTAR_RESUMEN_CIERRE.toString(), new Object[]{datos.get("cod_anho"), 
		datos.get("cod_mes"), datos.get("ind_tip_planil"), datos.get("cod_persona"), datos.get("cod_anho_origen"), 
		datos.get("cod_mes_origen"), datos.get("cod_concepto"), datos.get("num_min_stma"), datos.get("num_min_mod"), 
		datos.get("num_dias"), datos.get("ind_estado"), datos.get("cod_usumodif"), //datos.get("fec_modif"), 
		datos.get("cod_usucrea"), datos.get("fec_creacion"), datos.get("ind_tipo_carga"), datos.get("ind_devol")});
	}


	/**
	 * Metodo que se encarga de eliminar el resumen de cierre por concepto
	 * de un trabajador
	 * @throws DAOException
	 */	
	public void eliminarResumenCierreConcepto(Map datos) throws DAOException {		
		String cod_anho = datos.get("periodo").toString().substring(0,4);
		String cod_mes = datos.get("periodo").toString().substring(4,6);
		
		executeUpdate(datasource, ELIMINAR_RESUMEN_CIERRE_CONCEPTO.toString(), new Object[]{cod_anho, cod_mes, datos.get("cod_concepto")});
	}

	
	/**
	 * Metodo encargado de verificar si un periodo determinado ya fue enviado a Planillas 
	 * @param params (String periodo)
	 * @throws DAOException
	 */
	public boolean verificarEnvioPlanillas(String periodo) throws DAOException {
		Map estado=new HashMap();
		try{		
			log.debug("MantenimientoFacade.periodoCerradoUOFecha_RegimenModalidad... periodo.trim().substring(0,4): "+periodo.trim().substring(0,4));
			log.debug("MantenimientoFacade.periodoCerradoUOFecha_RegimenModalidad... periodo.trim().substring(4,6): "+periodo.trim().substring(4,6));
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			estado = executeQueryUniqueResult(datasource, FIND_ENVIO_PLANILLAS.toString(), 
					new Object[]{periodo.trim().substring(0,4), periodo.trim().substring(4,6)});

		} catch (Exception e) {
			log.error("*** SQL Error ****",e);
		}
		return ((estado != null && estado.get("ind_estado")!=null) ? (estado.get("ind_estado").equals("2") ? true:false): false);	
	}
	
}
