package pe.gob.sunat.rrhh.asistencia.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.date.FechaBean;

/** 
 * 
 * Clase       : T3701DAO 
 * Descripcion : clase encargada de administrar los datos de la tabla T3701SIRH_NOVEDAD 
 * Proyecto    : ASISTENCIA 
 * Autor       : EBENAVID
 * Fecha       : 12-AGO-2008 
 * 
 * */

public class T3701DAO extends DAOAbstract {
	
	private DataSource dataSource = null;	
		 
	private final StringBuffer findAllColumnsByKey =  new StringBuffer("SELECT cod_pers, fec_refer FROM t3701sirh_novedad where ind_proceso = ?  order by cod_pers, fec_refer ");
	
	private String findAllByEstadoAndFecha =  ""+
		"SELECT "+
		"	cod_pers, "+
		"	fec_refer " +
		"FROM "+
		"	t3701sirh_novedad "+ 
		"where "+
		"	ind_proceso = ?  "+
		"	and fec_refer >= ? "+
		"order by "+
		"	cod_pers, "+
		"	fec_refer ";
	
	private final StringBuffer UpdateByKey =  new StringBuffer("UPDATE t3701sirh_novedad SET ind_proceso = ?, fec_modif= ?, cod_usumodif = ? where cod_pers = ? and fec_refer = ? and ind_proceso = ? ");
	
	//JRR
	private final StringBuffer INSERT_NOVEDAD = new StringBuffer("INSERT INTO t3701sirh_novedad "
			).append("(cod_pers, fec_refer, ind_proceso, cod_usucrea, fec_creacion) "
			).append("VALUES (?, ?, ?, ?, ?) ");

	//JRR
	private final StringBuffer FIND_NOVEDAD_BY_TRAB_FEC = new StringBuffer("SELECT "
			).append("cod_pers, fec_refer, ind_proceso, cod_usucrea, fec_creacion "
			).append("FROM 	t3701sirh_novedad "
			).append("WHERE cod_pers = ? "
			).append("AND   fec_refer = ? ");

	//JRR
	private final StringBuffer ACTUALIZAR_NOVEDAD_CERRADA =  new StringBuffer("UPDATE t3701sirh_novedad "
			).append("SET ind_proceso = ?, cod_usumodif = ?, fec_modif= ? "
			).append("WHERE cod_pers = ? and fec_refer = ? and ind_proceso = ? ");
	
	//MTM
	private final StringBuffer FIND_NOVEDAD_BY_PK = new StringBuffer("SELECT "
			).append("cod_pers "
			).append("FROM 	t3701sirh_novedad "
			).append("WHERE cod_pers = ? "
			).append("AND fec_refer = ? "
			).append("ANd ind_proceso = ? ");
	//MTM
	/*
	private final StringBuffer FIND_PROCESO_BY_REGIMEN = new StringBuffer("select distinct cod_pers, fec_refer from "
			).append(" t3701sirh_novedad a, t02perdp b where "
			).append(" a.cod_pers = b.t02cod_pers and t02cod_rel != ? and ind_proceso in (?,?)");*/
	
	//ICAPUNAY 11/06/2012 NOVEDADES CON INDICADOR=2 DESDE FECHA PARAMETRICA	
	private final StringBuffer FIND_PROCESO_BY_REGIMEN = new StringBuffer("select distinct cod_pers, fec_refer from "
			).append(" t3701sirh_novedad a, t02perdp b where "
			//).append(" a.cod_pers = b.t02cod_pers and t02cod_rel != ? and (ind_proceso=? or (ind_proceso=? and fec_refer>=?)) ");//ICAPUNAY 27/06/2012 SOLO PROCESA CON UN INDICADOR 2
			).append(" a.cod_pers = b.t02cod_pers and t02cod_rel != ? and ind_proceso=? and fec_refer>=? ");//ICAPUNAY 27/06/2012 SOLO PROCESA CON UN INDICADOR 2		
	//FIN ICAPUNAY 11/06/2012
	
	//MTM
	/*//ICAPUNAY 27/06/2012 SOLO PROCESA CON UN INDICADOR 2
	private final StringBuffer ELIMINA_REGISTRO = new StringBuffer(
	"Delete from t3701sirh_novedad ").append(
	"where cod_pers = ? and fec_refer = ? and ind_proceso = ?");*///ICAPUNAY 27/06/2012 SOLO PROCESA CON UN INDICADOR 2
	//
	
	/**
	 *@param datasource Object
	 *
	 * */
	public T3701DAO(Object datasource) {
		if (datasource instanceof DataSource)
	      this.dataSource = (DataSource)datasource;
	    else if (datasource instanceof String)
	      this.dataSource = getDataSource((String)datasource);
	    else
	      throw new DAOException(this, "Datasource no valido");
	}
	
	/**
	 * Metodo encargado de obtener las novedades segun el ind_proceso enviado.
	 * 0 : Generación
	 * 1 : Proceso
	 * @param params (String cod_pers, String licencia, String periodo, String anno, Short numero, timeStamp ffinicio)
	 * @throws DAOException
	 */
	public List findByCodDesc(Map params) throws DAOException {
		
		List resultado = executeQuery(dataSource, findAllColumnsByKey.toString(), new Object[]{params.get("proceso")});
		
		return resultado;
	}
	
	/**
	 * Metodo encargado de obtener las novedades segun el ind_proceso y límite de fecha inicial enviado.
	 * 0 : Generación
	 * 1 : Proceso
	 * @param params (proceso, fechaLimite)
	 * @throws DAOException
	 */
	public List findByEstadoAndFecha(Map params) throws DAOException {
		
		List resultado = executeQuery(dataSource, findAllByEstadoAndFecha.toString(), new Object[]{params.get("proceso"), params.get("fechaLimite")});
		
		return resultado;
	}
	
	/**
	 * Metodo encargado de actualizar las novedades segun el ind_proceso enviado.
	 * 0 : Generacion
	 * 1 : Proceso
	 * @param params (String ind_proceso, Timestamp fec_modif, String cod_usumodif, String cod_pers, String fec_refer, String ind_proceso)
	 * @throws DAOException
	 */
	public void actualizar(Map params) throws DAOException {
		
		int resultado = executeUpdate(dataSource, UpdateByKey.toString(), new Object[]{params.get("nuevo"),new FechaBean().getTimestamp(),params.get("usuario"),params.get("cod_pers"),params.get("fec_refer"),params.get("proceso")});

	}
	
	/**
	 * Metodo que se encarga de registrar la novedad de un trabajador
	 * @param params (String cod_pers, String fec_refer, String ind_proceso, String cod_usucrea, Timestamp fec_creacion)
	 * @throws DAOException
	 */	
	public void registrarNovedad(Map datos) throws DAOException {		
		if (log.isDebugEnabled()) log.debug("registrarNovedad - datos: " + datos);	
		
		executeUpdate(dataSource, INSERT_NOVEDAD.toString(), new Object[]{datos.get("cod_pers"), 
									datos.get("fec_refer"), datos.get("ind_proceso"), datos.get("cod_usucrea"), 
									datos.get("fec_creacion")});
	}
	
	/**
	 * Metodo que busca una novedad 
	 * @param datos (String cod_pers, String fec_refer) 
	 * @throws DAOException
	 */	
	public Map findNovedadByTrabFec(Map datos) throws DAOException {		
		Map mapa = null;
		try{
			if (log.isDebugEnabled()) log.debug("findNovedadByTrabFec - datos: "+datos);
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			mapa = executeQueryUniqueResult(dataSource, FIND_NOVEDAD_BY_TRAB_FEC.toString(),
					new Object[]{datos.get("cod_pers"), datos.get("fec_refer")});
			
		} catch (Exception e) {
			log.error("*** SQL Error ****",e);
		}
		return mapa;
	}
	
	/**
	 * Metodo que se encarga de actualizar la novedad de un trabajador
	 * @param params (String cod_pers, String fec_refer, String ind_proceso, String cod_usucrea, Timestamp fec_creacion)
	 * @throws DAOException
	 */	
	public void actualizarNovedadCerrada(Map datos) throws DAOException {		
		if (log.isDebugEnabled()) log.debug("actualizarNovedadCerrada - datos: " + datos);	
		
		executeUpdate(dataSource, ACTUALIZAR_NOVEDAD_CERRADA.toString(), new Object[]{datos.get("ind_proceso"),
									datos.get("cod_usucrea"), datos.get("fec_creacion"),
									datos.get("cod_pers"), datos.get("fec_refer"), datos.get("ind_proc_cerrado")});
	}

	/**
	 * MTM
	 * Metodo que busca una novedad por pk 
	 * @param datos (String cod_pers, String fec_refer) 
	 * @throws DAOException
	 */	
	public Map findNovedadByPK(Map datos) throws DAOException {		
		Map mapa = null;
		try{
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			mapa = executeQueryUniqueResult(dataSource, FIND_NOVEDAD_BY_PK.toString(),
					new Object[]{datos.get("cod_pers"), datos.get("fec_refer"), datos.get("ind_proceso")});
			
		} catch (Exception e) {
			log.error("*** SQL Error ****",e);
		}
		return mapa;
	}
	
	/**
	 * MTM
	 * Metodo que busca una novedad de proceso por regimen
	 * @param datos (String cod_pers, String fec_refer) 
	 * @throws DAOException
	 */	
	public List findNovedadByRegimen(Map datos) throws DAOException {				
			//List resultado = executeQuery(dataSource, FIND_PROCESO_BY_REGIMEN.toString(), new Object[]{datos.get("regimen"),datos.get("proceso1"),datos.get("proceso2")});
			//List resultado = executeQuery(dataSource, FIND_PROCESO_BY_REGIMEN.toString(), new Object[]{datos.get("regimen"),datos.get("proceso1"),datos.get("proceso2"),datos.get("fechaFiltro2")});//ICAPUNAY 27/06/2012 SOLO PROCESA CON UN INDICADOR 2
			List resultado = executeQuery(dataSource, FIND_PROCESO_BY_REGIMEN.toString(), new Object[]{datos.get("regimen"),datos.get("proceso2"),datos.get("fechaFiltro2")});//ICAPUNAY 27/06/2012 SOLO PROCESA CON UN INDICADOR 2
			return resultado;
	}		
	
	/**
	 * MTM
	 * Metodo para eliminar una novedad de proceso por regimen
	 * @param datos (String cod_pers, String fec_refer) 
	 * @throws DAOException
	 */	
	/*//ICAPUNAY 27/06/2012 SOLO PROCESA CON UN INDICADOR 2
	public boolean deleteRegistroNovedad(Map params) throws SQLException {
		
		int eliminados=0;
		if (log.isDebugEnabled()) log.debug("T3701DAO - eliminando registro - codPers");
		
		try{				
			eliminados = executeUpdate(dataSource,ELIMINA_REGISTRO.toString(), new Object[]{params.get("cod_pers"), params.get("fec_refer"), 
				params.get("nuevo")});	
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [DELETE_REGISTRO_T3701sirh_novedad]");
		}		
		return (eliminados>0);
	}*///ICAPUNAY 27/06/2012 SOLO PROCESA CON UN INDICADOR 2
		

}
