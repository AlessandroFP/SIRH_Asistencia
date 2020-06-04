package pe.gob.sunat.rrhh.asistencia.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.Propiedades;
import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.utils.Constantes;

import pe.gob.sunat.utils.Utiles;


/**
 * <p>
 * Title: T8167DAO
 * </p>
 * <p>
 * Description: Clase encargada de administrar las consultas a la tabla
 * T8167autorizaclima
 * </p>
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * <p>
 * Company: Sunat
 * </p>
 * 
 * @author icapunay
 * @version 1.0
 */

public class T8167DAO extends DAOAbstract {

	private DataSource dataSource = null;

	Propiedades constantes = new Propiedades(getClass(),"/constantes.properties");
	
	
	//OKKK
	private final StringBuffer INSERTA_REGISTRO = new StringBuffer(
	"Insert into t8167autorizaclima (").append(
	"anno,num_seqcli,cod_pers,fec_aut,cod_uorga,cod_aut,ind_aut,cod_user_crea,fec_creacion) ").append(
	"Values (?,?,?,?,?,?,?,?,?)");
	
	//OKKK
	private final StringBuffer UPDATE_REGISTRO = new StringBuffer(
	"Update t8167autorizaclima "+
	"set ind_aut=?, cod_user_mod=?, fec_modifica=? "+
	"where cod_pers=? and fec_aut = ? ");

	//OKKK
	private final StringBuffer FIND_BYCODPERS = new StringBuffer("select first 1 a.*,p.t02ap_pate,p.t02ap_mate,p.t02nombres from t8167autorizaclima a,t02perdp p "	
	).append("where a.cod_pers=p.t02cod_pers and a.cod_pers=? and a.fec_aut=? ");
	
	//no se usa
	private final StringBuffer FIND_BYCODPERSBYESTADO = new StringBuffer("select * from t8167autorizaclima "	
	).append("where cod_pers=? and fec_aut=? and ind_aut=? ");
	
	private final StringBuffer FIND_BYCODPERSBYFECHA = new StringBuffer("select first 1 a.cod_pers,a.fec_aut from t8167autorizaclima a "	
	//).append("where a.cod_pers=? and month(a.fec_aut)=month(?) "); //PAS20181U230300140 - icapunay - Ajuste en registro de clima laboral (validacion por mes).
	).append("where a.cod_pers=? and month(a.fec_aut)=month(?) and year(a.fec_aut)=year(?) "); //PAS20181U230300140 - icapunay - Ajuste en registro de clima laboral (validacion por mes).		

	
	private final StringBuffer FIND_BYFECAUT = new StringBuffer("select p.t02ap_pate, p.t02ap_mate,p.t02nombres,ac.* from t8167autorizaclima ac,t02perdp p "	
	).append("where ac.cod_pers=p.t02cod_pers ").append("and (fec_aut BETWEEN ? and ?) and ind_aut=? order by fec_aut asc, cod_pers asc ");
	
	private final StringBuffer FINDCOLABORADORES_BYUOBYFECHA = new StringBuffer("select p.t02cod_pers as regcol,(trim(p.t02ap_pate)||' '||trim(p.t02ap_mate)||' '||trim(p.t02nombres)) as colaborador "	
	).append("from t02perdp p where p.t02cod_stat='1' and p.t02cod_rel not in (?) ")
	.append("and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6)=? ")
	.append("and not exists ")
	.append("(select a.cod_pers from t8167autorizaclima a ")
	.append("where a.cod_pers=p.t02cod_pers ")
	.append("and month(a.fec_aut)=month(?) and year(a.fec_aut)=year(?)) ")
	.append("and not exists ")
	.append("(select l.cod_pers from t1273licencia l ")
	.append("where l.cod_pers=p.t02cod_pers ")
	.append("and ? BETWEEN date(l.ffinicio) and date(l.ffin)) ")
	.append("and not exists ")
	.append("(select v.cod_pers from t1282vacaciones_d v ")
	.append("where v.cod_pers=p.t02cod_pers ")
	.append("and (? BETWEEN date(v.ffinicio) and date(v.ffin)) and licencia in (?,?,?) and est_id=?) ")
	.append("order by p.t02cod_pers asc ");

	public T8167DAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.dataSource = (DataSource) datasource;
		else if (datasource instanceof String)
			this.dataSource = getDataSource((String) datasource);
		else
			throw new DAOException(this, "Datasource no valido");
	}
	
    //OKKK
    /** 
	 * Metodo encargado de insertar una autorizacion de refrigerio clima laboral
	 * @param dbpool String
	 * @param datos  Map
	 * @return modificado int
	 * @throws SQLException
	 */
	public boolean insertRegistroAutorizaCli(Map datos)
			throws SQLException {
		
	    int modificado = 0;
	    try{	
		
		modificado = executeUpdate(dataSource, INSERTA_REGISTRO.toString(),
				new Object[] {datos.get("anno"),datos.get("num_seqcli"),datos.get("cod_pers"),datos.get("fec_aut"),datos.get("cod_uorga"),
				datos.get("cod_aut"),datos.get("ind_aut"),datos.get("cod_user_crea"),datos.get("fec_creacion")});
	    }catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error al ejecutar. [insertRegistroAutorizaCli]");
		}	
		return (modificado > 0);
	}
	
	//OKKK
	/** 
	 * Metodo encargado de actualizar el estado de una autorizacion de refrigerio clima laboral
	 * @param datos Map
	 * @return modificado int
	 * @throws SQLException
	 */
	public boolean updateRegistroAutorizaCli (Map datos)
			throws SQLException {
		FechaBean fecha1 = new FechaBean();
		int modificado = 0;
		try{	
			modificado = executeUpdate(dataSource, UPDATE_REGISTRO.toString(),
					new Object[] {
					datos.get("ind_aut"),
					datos.get("cod_user_mod"),
					fecha1.getTimestamp(),
					datos.get("cod_pers"),
					new FechaBean((String)datos.get("fec_aut")).getSQLDate()}); //???			
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error al ejecutar. [updateRegistroAutorizaCli]");
		}	
		return (modificado > 0);
	}
	
	

	//OKKK
	/**
	  * Metodo que busca si existe una fecha de autorizacion de refrigerio clima laboral para un colaborador	
	  * @param codPers String
	  * @param fechaAutoriza String	
	  * @return mfecAutoriza Map
	  * @throws DAOException
	  */
	public Map findAutorizaByRegByFecha(String codPers, String fechaAutoriza) throws DAOException {
				
		Map mfecAutoriza = null;
		if (log.isDebugEnabled()) log.debug("T8167DAO - findAutorizaByRegByFecha - codPers: " + codPers + " - fechaAutoriza: " + fechaAutoriza);		
		try{
			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);			
			mfecAutoriza = executeQueryUniqueResult(dataSource, FIND_BYCODPERS.toString(), new Object[]{codPers,
				new FechaBean(fechaAutoriza).getSQLDate()});//????
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [findAutorizaByRegByFecha]");
		}	
		return mfecAutoriza;
	}
	
	//NO SE USA findAutorizaByRegByFechaByEstado en ningun facade
	/**
	  * Metodo que busca si existe una fecha de autorizacion de refrigerio clima laboral para un colaborador segun estado	
	  * @param codPers String
	  * @param fechaAutoriza String	
	  * @param estado String	
	  * @return mfecAutoriza Map
	  * @throws DAOException
	  */
	public Map findAutorizaByRegByFechaByEstado(String codPers, String fechaAutoriza, String estado) throws DAOException {
				
		Map mfecAutoriza = null;
		if (log.isDebugEnabled()) log.debug("T8167DAO - findAutorizaByRegByFechaByEstado - codPers: " + codPers + " - fechaAutoriza: " + fechaAutoriza  + " - estado: "+ estado );		
		try{
			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);			
			mfecAutoriza = executeQueryUniqueResult(dataSource, FIND_BYCODPERSBYESTADO.toString(), new Object[]{codPers,
				new FechaBean(fechaAutoriza).getSQLDate(),estado});//????
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [findAutorizaByRegByFechaByEstado]");
		}	
		return mfecAutoriza;
	}
	
	/**
	  * Metodo que busca si existe en el mes alguna fecha de autorizacion de refrigerio clima laboral para un colaborador	
	  * @param codPers String
	  * @param fechaAutoriza String	 
	  * @return mfecAutoriza Map
	  * @throws DAOException
	  */
	public Map findAutorizaEnMesByRegByFecha(String codPers, String fechaAutoriza) throws DAOException {
				
		Map mfecAutoriza = null;
		if (log.isDebugEnabled()) log.debug("T8167DAO - findAutorizaEnMesByRegByFecha - codPers: " + codPers + " - fechaAutoriza: " + fechaAutoriza );		
		try{
			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);			
			mfecAutoriza = executeQueryUniqueResult(dataSource, FIND_BYCODPERSBYFECHA.toString(), new Object[]{codPers,
				new FechaBean(fechaAutoriza).getSQLDate(),new FechaBean(fechaAutoriza).getSQLDate()}); //PAS20181U230300140 - icapunay - Ajuste en registro de clima laboral (validacion por mes).	
				//new FechaBean(fechaAutoriza).getSQLDate()}); //PAS20181U230300140 - icapunay - Ajuste en registro de clima laboral (validacion por mes).
				
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [findAutorizaEnMesByRegByFecha]");
		}	
		return mfecAutoriza;
	}
	
		
	/**
	  * Metodo que devuelve las autorizaciones de refrigerio de cima laboral entre un rango de fechas y según estado 
	  * @param datos Map
	  * @return autorizaciones List
	  * @throws DAOException
	  */
	public List findAutorizaByFechasByEstado(Map datos) throws DAOException {
		
		List autorizaciones= null;
		if (log.isDebugEnabled()) log.debug("T8167DAO - findAutorizaByRegByFecha - datos: " + datos );
		if (log.isDebugEnabled()) log.debug("fec_aut date: " + new FechaBean((String)datos.get("fec_aut")).getSQLDate() );
		if (log.isDebugEnabled()) log.debug("fec_aut1 date: " + new FechaBean((String)datos.get("fec_aut1")).getSQLDate() );
		
		try{			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			autorizaciones = executeQuery(dataSource, FIND_BYFECAUT.toString(), new Object[]{new FechaBean((String)datos.get("fec_aut")).getSQLDate(),new FechaBean((String)datos.get("fec_aut1")).getSQLDate(),datos.get("ind_aut")});
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [findAutorizaByFechasByEstado]");
		}		
		return autorizaciones;
	}
	
	
	/**
	  * Metodo que lista colaboradores (DL 1057/DL 276-728) que no tienen actividad de clima registrada en el mes de una fecha y no se encuentran de licencia o vacaciones en una fecha 
	  * @param datos Map
	  * @return colaboradores List
	  * @throws DAOException
	  */
	public List findColaboradoresSinClimaLicenciaVacacionesByUOByFecha(Map datos) throws DAOException {
		
		List colaboradores= null;
		if (log.isDebugEnabled()) log.debug("T8167DAO - findColaboradoresSinClimaLicenciaVacacionesByUOByFecha - datos: " + datos );
		FechaBean fecEvento = new FechaBean((String)datos.get("fechaEvento"));
		try{			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			colaboradores = executeQuery(dataSource, FINDCOLABORADORES_BYUOBYFECHA.toString(), new Object[]{
				Constantes.CODREL_FORMATIVA,
				(String)datos.get("coduo"),
				fecEvento.getSQLDate(),
				fecEvento.getSQLDate(),
				fecEvento.getSQLDate(),
				fecEvento.getSQLDate(),
				Constantes.VACACION,Constantes.VACACION_ESPECIAL,Constantes.VACACION_PROGRAMADA,Constantes.ACTIVO});
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [findColaboradoresSinClimaLicenciaVacacionesByUOByFecha]");
		}		
		return colaboradores;
	}
	

}
