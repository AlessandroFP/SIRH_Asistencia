package pe.gob.sunat.rrhh.asistencia.dao;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.date.FechaBean;

/** 
 * 
 * Clase       : T3792DAO 
 * Descripcion : Clase encargada de administrar los datos de la tabla t3792comp_bolsa 
 * Proyecto    : ASISTENCIA 
 * Autor       : JROJAS4
 * Fecha       : 21-OCT-2008 
 * 
 * */

public class T3792DAO extends DAOAbstract {	
	
	private DataSource datasource;	
	
	//JRR
	private final StringBuffer INSERTAR_COMPENSA = new 	StringBuffer("INSERT INTO t3792comp_bolsa "
			).append("(num_licencia, fec_licencia, cod_pers, cnt_ttotal, cnt_tsaldo, fec_ini, "
			).append("fec_fin, ind_tipo, ind_estado, cod_usucrea, fec_creacion, cod_usumodif , fec_modif) "
			).append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");
	
	//JRR
	private final StringBuffer FIND_COMPENSA_PEND_BY_PERS = new StringBuffer("SELECT num_licencia, fec_licencia, cod_pers, "
			).append("cnt_ttotal, cnt_tsaldo, fec_ini, fec_fin, ind_estado "
			).append("FROM 	t3792comp_bolsa "
			).append("WHERE fec_ini <= ? "
			).append("AND   fec_fin >= ? "					
			).append("AND   cod_pers = ? "
			).append("AND	ind_estado IN ('0','1') "
			).append("ORDER BY fec_licencia ");
	
	//JRR
	private final StringBuffer FIND_COMPENSA_PEND_BY_FEC_FIN = new StringBuffer("SELECT num_licencia, fec_licencia, cod_pers, "
			).append("cnt_ttotal, cnt_tsaldo, fec_ini, fec_fin, ind_estado "
			).append("FROM 	t3792comp_bolsa "
			).append("WHERE fec_fin < ? "
			).append("AND   cod_pers = ? "
			).append("AND	ind_estado IN ('0','1') "
			).append("ORDER BY fec_licencia ");
	
	//JRR
	private final StringBuffer UPDATE_SALDO_COMPENSA = new 	StringBuffer("UPDATE t3792comp_bolsa "
			).append("SET cnt_tsaldo = ?, "
			).append("    ind_estado = ?, "
			).append("    cod_usumodif = ?, "
			).append("    fec_modif = ?  "
			).append("WHERE num_licencia = ? "
			).append("AND fec_licencia = ? "		
			).append("AND cod_pers = ? ");
	
	//JRR - 15/12/2008
	private final StringBuffer UPDATE_ESTADO_COMPENSA = new 	StringBuffer("UPDATE t3792comp_bolsa "
			).append("SET ind_estado = ?, "
			).append("    cod_usumodif = ?, "
			).append("    fec_modif = ?  "
			).append("WHERE num_licencia = ? "
			).append("AND fec_licencia = ? "		
			).append("AND cod_pers = ? "); 

	//JRR
	private final StringBuffer FIND_COMPENSA_BY_CRITERIO = new StringBuffer("SELECT B.cod_pers, B.num_licencia, "
			).append("B.fec_licencia, B.cnt_ttotal, B.cnt_tsaldo, B.fec_ini, B.fec_fin, B.ind_estado, "
			).append("A.fec_compensa, A.cnt_tiempo "
			).append("FROM 	t3792comp_bolsa B, OUTER t3793detcomp_bolsa A "
			).append("WHERE B.cod_pers = ? "
			).append("AND   B.fec_ini >= ? "
			).append("AND   B.fec_ini <= ? "					
			).append("AND	A.num_licencia = B.num_licencia "
			).append("AND	A.fec_licencia = B.fec_licencia "
			).append("AND	A.cod_pers = B.cod_pers "
			).append("ORDER BY cod_pers ");
	
	/**
	* 
	* Este constructor del DAO dicierne como crear el datasource
	* dependiendo del tipo de objeto que recibe. Esto nos ayuda a
	* mejorar la invocacion del dao.
	* 
	* @param datasource Object
	*/
	public T3792DAO(Object datasource) {
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
	public void registrarCompensacion(Map datos) throws DAOException {		
		if (log.isDebugEnabled()) log.debug("registrarCompensacion - datos" + datos);	
		executeUpdate(datasource, INSERTAR_COMPENSA.toString(), new Object[]{datos.get("num_licencia"), 
									datos.get("fec_licencia"), datos.get("cod_pers"), datos.get("cnt_ttotal"), 
									datos.get("cnt_tsaldo"), datos.get("fec_ini"), datos.get("fec_fin"), 
									datos.get("ind_tipo"), datos.get("ind_estado"), 
									datos.get("cod_usucrea"), datos.get("fec_creacion"),
									datos.get("cod_usumodif"), datos.get("fec_modif")});
	}
	
	/**
	 * Metodo que busca compensaciones pendiente
	 * de un trabajador
	 * @throws DAOException
	 */	
	public List findCompensacionesPendByPers(Map datos) throws DAOException {		
		List lista = null;
		try{
			if (log.isDebugEnabled()) log.debug("findCompensacionesPendByPers - datos: "+datos);
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			lista = executeQuery(datasource, FIND_COMPENSA_PEND_BY_PERS.toString(),
					new Object[]{datos.get("fec_compensa"), datos.get("fec_compensa"), datos.get("cod_pers")});
		} catch (Exception e) {
			log.error("*** SQL Error ****",e);
		}
		return lista;
	}
	
	/**
	 * Metodo que busca compensaciones pendientes por fecha de fin de compensacion
	 * de un trabajador
	 * @throws DAOException
	 */	
	public List findCompensacionesPendByFecFin(Map datos) throws DAOException {		
		List lista = null;
		try{
			if (log.isDebugEnabled()) log.debug("findCompensacionesPendByFecFin - datos: "+datos);
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			lista = executeQuery(datasource, FIND_COMPENSA_PEND_BY_FEC_FIN.toString(),
					new Object[]{datos.get("fec_hoy"), datos.get("cod_pers")});
		} catch (Exception e) {
			log.error("*** SQL Error ****",e);
		}
		return lista;
	}
	
	/**
	 * Metodo que se encarga de Actualizar el Saldo de la Compensacion
	 * de un trabajador
	 * @throws DAOException
	 */
	public void actualizarSaldoCompensacion(Map datos) throws DAOException {	
		log.debug("actualizarSaldoCompensacion - datos: " + datos);	
		executeUpdate(datasource, UPDATE_SALDO_COMPENSA.toString(), 
						new Object[]{datos.get("cnt_tsaldo"),
									 datos.get("ind_estado"),
									 datos.get("cod_usucrea"),
									 datos.get("fec_creacion"),
									 datos.get("num_licencia"),
									 datos.get("fec_licencia"),
 									 datos.get("cod_pers")
 									 });
	}
	
	/**
	 * Metodo que se encarga de actualizar solo el Estado de la Compensacion
	 * de un trabajador
	 * @throws DAOException
	 */
	public void actualizarEstadoCompensacion(Map datos) throws DAOException {	
		log.debug("actualizarEstadoCompensacion - datos: " + datos);	
		executeUpdate(datasource, UPDATE_ESTADO_COMPENSA.toString(), 
						new Object[]{datos.get("ind_estado"),
									 datos.get("cod_usucrea"),
									 datos.get("fec_creacion"),
									 datos.get("num_licencia"),
									 datos.get("fec_licencia"),
 									 datos.get("cod_pers")
 									 });
	}
	

	//JRR
	/**
	 * Metodo que busca las compensaciones y su detalle segun criterio
	 * de un trabajador
	 * @throws DAOException
	 */	
	public List findCompensacionesByCriterio(Map datos) throws DAOException {		
		List lista = null;
		try{
			if (log.isDebugEnabled()) log.debug("findCompensacionesByCriterio - datos: "+datos);
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			lista = executeQuery(datasource, FIND_COMPENSA_BY_CRITERIO.toString(),
					new Object[]{datos.get("cod_pers"), new FechaBean(datos.get("fechaIni").toString()).getSQLDate(), new FechaBean(datos.get("fechaFin").toString()).getSQLDate()});
		} catch (Exception e) {
			log.error("*** SQL Error ****",e);
		}
		return lista;
	}
	
	
}
