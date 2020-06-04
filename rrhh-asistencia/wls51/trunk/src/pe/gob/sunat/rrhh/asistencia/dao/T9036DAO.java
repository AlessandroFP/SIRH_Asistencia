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
 * Title: T9036DAO
 * </p>
 * <p>
 * Description: Clase encargada de administrar las consultas a la tabla
 * t9036notifvacdir
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

public class T9036DAO extends DAOAbstract {

	private DataSource dataSource = null;

	Propiedades constantes = new Propiedades(getClass(),"/constantes.properties");
	
	
	//OK
	private final StringBuffer INSERTA_REGISTRO = new StringBuffer(
	"Insert into t9036notifvacdir (").append(
	"num_seqdir,ind_tip_notif,des_notif,cod_pers_jefe,cod_uorg_notif,num_trabaj,fec_envio_notif,cod_user_crea,fec_creacion) ").append(
	"Values (?,?,?,?,?,?,?,?,?)");
	
	
	//OK
	private final StringBuffer FINDTRABAJADOR_BYCODPERS = new StringBuffer("select p.t02cod_pers as registro,(trim(p.t02ap_pate)||' '||trim(p.t02ap_mate)||' '||trim(p.t02nombres)) as apenom, "	
	).append("u.t12cod_uorga as coduo,u.t12des_corta as desuo,p.t02f_ingsun as fechaingreso ")
	//.append("from t02perdp p, t12uorga u ")
	.append("from t02perdp p left join t12uorga u ")
	//.append("where substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6)=u.t12cod_uorga ")
	.append("on substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6)=u.t12cod_uorga ")
	//.append("and p.t02cod_pers=? ");
	.append("where p.t02cod_pers=? ");
	
	//OK
	private final StringBuffer FIND_BYFECHA = new StringBuffer("select first 1 date(fec_envio_notif) as fechaNotif "	
	).append("from t9036notifvacdir where month(date(fec_envio_notif))=month(?) ");
	
	//OK
	private final StringBuffer FIND_BYFECNOTIF= new StringBuffer("SELECT nvd.num_seqdir,nvd.cod_pers_jefe as regdirec, "	
	).append("(trim(p.t02ap_pate)||' '||trim(p.t02ap_mate)||' '||trim(p.t02nombres)) as apenomdirec, ")
	.append("(nvd.cod_uorg_notif||' - '||u.t12des_corta) as desuo,nvd.num_trabaj, ")
	.append("(DATE(nvd.fec_envio_notif)) as fecnotif,nvd.fec_envio_notif as fecnotiffinal ")
	.append("FROM t9036notifvacdir nvd,t12uorga u, t02perdp p ")
	.append("where nvd.cod_uorg_notif=u.t12cod_uorga ")
	.append("and nvd.cod_pers_jefe=p.t02cod_pers ")
	.append("and nvd.ind_tip_notif=? ")
	.append("and DATE(nvd.fec_envio_notif)=? ")
	.append("order by desuo asc, regdirec asc, fecnotif asc ");
		
	//OK
	private final StringBuffer FINDTRABAJADORES_BYESTADOUNIDAD= new StringBuffer("select (trim(p.t02ap_pate)||' '||trim(p.t02ap_mate)||' '||trim(p.t02nombres)) as apenom,u.t12cod_uorga as coduo,(u.t12cod_uorga||' - '||u.t12des_corta) as desuo,p.* "	
	).append("from t02perdp p, t12uorga u ")
	.append("where substr(trim(nvl(P.t02cod_uorgl,'')||nvl(P.t02cod_uorg,'')),1,6)=u.t12cod_uorga ")
	.append("and p.t02cod_stat=? ")
	.append("and p.t02f_ingsun<=? ")
	.append("and substr(trim(nvl(P.t02cod_uorgl,'')||nvl(P.t02cod_uorg,'')),1,6) like ? ")
	.append("and p.t02cod_rel not in (?) ")
	.append("order by coduo asc, t02cod_pers asc ");
	
	public T9036DAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.dataSource = (DataSource) datasource;
		else if (datasource instanceof String)
			this.dataSource = getDataSource((String) datasource);
		else
			throw new DAOException(this, "Datasource no valido");
	}
	
  
	//OK
    /** 
	 * Metodo encargado de insertar una notificacion a directivo con relacion de colaboradores con programacion vacaciones
	 * @param dbpool String
	 * @param datos  Map
	 * @return insertado int
	 * @throws SQLException
	 */
	public boolean insertNotificDirectivoProgramaciones(Map datos)
			throws SQLException {
		
	    int insertado = 0;
	    try{	
		
	    	insertado = executeUpdate(dataSource, INSERTA_REGISTRO.toString(),
				new Object[] {datos.get("num_seqdir"),datos.get("ind_tip_notif"),datos.get("des_notif"),datos.get("cod_pers_jefe"),
				datos.get("cod_uorg_notif"),datos.get("num_trabaj"),datos.get("fec_envio_notif"),datos.get("cod_user_crea"),datos.get("fec_creacion")});
	    }catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error al ejecutar. [insertNotificDirectivoProgramaciones]");
		}	
		return (insertado > 0);
	}
	

	//OK
	/**
	  * Metodo que busca apellidos/nombres y fecha ingreso de colaborador segun registro	
	  * @param codPers String	
	  * @return mdatos Map
	  * @throws DAOException
	  */
	public Map findTrabajadorByRegistro(String codPers) throws DAOException {
				
		Map mdatos = null;
		if (log.isDebugEnabled()) log.debug("T9036DAO - findTrabajadorByRegistro - codPers: " + codPers);		
		try{
			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);			
			mdatos = executeQueryUniqueResult(dataSource, FINDTRABAJADOR_BYCODPERS.toString(), new Object[]{codPers});
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [findTrabajadorByRegistro]");
		}	
		return mdatos;
	}
	
	
	//OK
	/**
	  * Metodo que busca si existe en el mes alguna notificacion ya enviada	
	  * @param fechaEjecucion String	 
	  * @return notificacion Map
	  * @throws DAOException
	  */
	public Map findNotificacionEnMesByFecha(String fechaEjecucion) throws DAOException {
				
		Map notificacion = null;
		if (log.isDebugEnabled()) log.debug("T9036DAO - findNotificacionEnMesByFecha - fechaEjecucion: " + fechaEjecucion );		
		try{
			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);			
			notificacion = executeQueryUniqueResult(dataSource, FIND_BYFECHA.toString(), new Object[]{new FechaBean(fechaEjecucion).getSQLDate()});
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [findNotificacionEnMesByFecha]");
		}	
		return notificacion;
	}
	
	
	//OK
	/**
	  * Metodo que devuelve todas las notificaciones realizadas a directivos en una fecha 
	  * @param datos Map
	  * @return notificaciones List
	  * @throws DAOException
	  */
	public List findNotificacionesByFecha(Map datos) throws DAOException {		
		List notificaciones= null;
		if (log.isDebugEnabled()) log.debug("T9036DAO - findNotificacionesByFecha - datos: " + datos );			
		try{			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			notificaciones = executeQuery(dataSource, FIND_BYFECNOTIF.toString(), new Object[]{datos.get("tipoNotif"),new FechaBean((String)datos.get("fechaNotif")).getSQLDate()}); //datos.get("fechaNotif") = date
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [findNotificacionesByFecha]");
		}		
		return notificaciones;
	}
	
	//OK
	/**
	  * Metodo que devuelve los trabajadores por estado y unidad 
	  * @param datos Map
	  * @return trabajadores List
	  * @throws DAOException
	  */
	public List findTrabajadoresByEstadoUnidad(Map datos) throws DAOException {		
		List trabajadores= null;
		if (log.isDebugEnabled()) log.debug("T9036DAO - findTrabajadoresByEstadoUnidad - datos: " + datos );			
		try{			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			//trabajadores = executeQuery(dataSource, FINDTRABAJADORES_BYESTADOUNIDAD.toString(), new Object[]{datos.get("estado"),new FechaBean((String)datos.get("fechaIngreso")).getSQLDate(),(String)datos.get("unidad")+"%",datos.get("noregimen")}); 
			trabajadores = executeQuery(dataSource, FINDTRABAJADORES_BYESTADOUNIDAD.toString(), new Object[]{datos.get("estado"),new FechaBean((String)datos.get("fechaIngreso")).getSQLDate(),(String)datos.get("unidad"),datos.get("noregimen")});
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [findTrabajadoresByEstadoUnidad]");
		}		
		return trabajadores;
	}

}
