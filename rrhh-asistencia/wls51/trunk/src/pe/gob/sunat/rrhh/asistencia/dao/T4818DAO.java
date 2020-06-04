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

import pe.gob.sunat.utils.Utiles;


/**
 * <p>
 * Title: T4818DAO
 * </p>
 * <p>
 * Description: Clase encargada de administrar las consultas a la tabla
 * T4818autorizaexc
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: Sunat
 * </p>
 * 
 * @author mtomaylla/icapunay
 * @version 1.0
 */

public class T4818DAO extends DAOAbstract {

	private DataSource dataSource = null;

	Propiedades constantes = new Propiedades(getClass(),
			"/constantes.properties");
	
	/*
	private final StringBuffer VERIFICA_REGISTRO = new StringBuffer(
	"select cod_pers from t4818autorizaexc ").append( 
	"where cod_pers = ? and fec_aut = ? and ind_aut = '1' "
	).append( "and ( ? >= hor_ini_aut and ? <= hor_fin_aut) "
	).append( "and ( ? >= hor_ini_aut and ? <= hor_fin_aut) ");*/
	
	//ICAPUNAY 21/06/2012 NO DEJA REGISTRAR UN RANGO NUEVO QUE INTERSECTE CON RANGO APROBADO
	/*
	private final StringBuffer VERIFICA_REGISTRO = new StringBuffer(
	"select cod_pers,hor_ini_aut,hor_fin_aut,ind_aut from t4818autorizaexc ").append( 
	"where cod_pers = ? and fec_aut = ? and ind_aut = '1' "
	).append( "and ( ? <= hor_ini_aut or ? <= hor_fin_aut) "
	).append( "and ( ? >= hor_ini_aut or ? >= hor_fin_aut) ");*/
	private final StringBuffer VERIFICA_REGISTRO = new StringBuffer(
	"select cod_pers,hor_ini_aut,hor_fin_aut,ind_aut from t4818autorizaexc ").append( 
	"where cod_pers = ? and fec_aut = ? and ind_aut = '1' "
	).append( "and (( ? <= hor_ini_aut and ? <= hor_ini_aut) "
	).append( "or ( ? >= hor_fin_aut and ? >= hor_fin_aut)) ");
	
	//ICAPUNAY 21/06/2012 NO DEJA REGISTRAR UN RANGO NUEVO QUE INTERSECTE CON RANGO APROBADO
	
	private final StringBuffer VERIFICA_REGISTRO_PK = new StringBuffer(
	"select cod_pers from t4818autorizaexc ").append(
	"where cod_pers = ? and fec_aut = ? "
	).append(" and hor_ini_aut = ? "); 
	
	private final StringBuffer INSERTA_REGISTRO = new StringBuffer(
	"Insert into t4818autorizaexc (").append(
	"cod_pers,fec_aut,hor_ini_aut,hor_fin_aut,obs_sustento,cod_jefe_aut,ind_aut,cod_user_crea,fec_creacion) ").append(
	"Values (?,?,?,?,?,?,?,?,?)");
	
	private final StringBuffer UPDATE_REGISTRO_INDICADOR = new StringBuffer(
	"Update t4818autorizaexc "+
	"set ind_aut=?, cod_user_mod=?, fec_modifica=? "+
	"where cod_pers=? and fec_aut = ? and hor_ini_aut = ? ");
	
	//INICIO ICAPUNAY
	private final StringBuffer FIND_AUTORIZACION = new StringBuffer("select distinct fec_aut from t4818autorizaexc "	
	).append("where cod_pers=? and fec_aut=? ");

	private final StringBuffer FIND_INTERVALOS_INTERSECTADOS_HORAS_AUTORIZADOS_RECHAZADOS = new StringBuffer("select * from t4818autorizaexc "
	).append("where cod_pers=? and fec_aut=? "	
	).append("and ((? <= hor_ini_aut or ? <= hor_fin_aut) "	
	).append("and (? >= hor_ini_aut or ? >= hor_fin_aut))	"	
	).append("order by hor_ini_aut asc ");

	private final StringBuffer FIND_INTERVALO_NO_INTERSECTADO = new StringBuffer("select a.* from t4818autorizaexc a "	
	).append("where a.cod_pers = ? and a.fec_aut = ? "
	).append("and ((? <= a.hor_ini_aut and ? <= a.hor_ini_aut) or "
	).append("(? >= a.hor_fin_aut and ? >= a.hor_fin_aut)) "
	).append("and a.hor_ini_aut=? and a.hor_fin_aut=? ");
	//FIN ICAPUNAY
	
	private final StringBuffer ELIMINA_REGISTRO = new StringBuffer(
	"Delete from t4818autorizaexc ").append(
	"where cod_pers = ? and fec_aut = ? and hor_ini_aut = ?");
	
	public T4818DAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.dataSource = (DataSource) datasource;
		else if (datasource instanceof String)
			this.dataSource = getDataSource((String) datasource);
		else
			throw new DAOException(this, "Datasource no valido");
	}
	
	public boolean verificaRegistroAutorizaExc(String dbpool,
			String userOrig, String fecha, String horaini, String horafin)
			throws SQLException {
		List anno = null;
		String cod_pers = null;
			log.debug("anno1");
			anno = executeQuery(dataSource, VERIFICA_REGISTRO.toString(), new Object[] {
				//userOrig, new FechaBean(fecha).getSQLDate(),horaini,horaini, horafin, horafin}); //ICAPUNAY 21/06/2012 NO DEJA REGISTRAR UN RANGO NUEVO QUE INTERSECTE CON RANGO APROBADO
				userOrig, new FechaBean(fecha).getSQLDate(),horaini,horafin, horaini, horafin}); //ICAPUNAY 21/06/2012 NO DEJA REGISTRAR UN RANGO NUEVO QUE INTERSECTE CON RANGO APROBADO
			log.debug("anno2");
			log.debug(anno.size()+"");
			log.debug(anno+"");
			//ICAPUNAY 21/06/2012 NO DEJA REGISTRAR UN RANGO NUEVO QUE INTERSECTE CON RANGO APROBADO
			/*
			for(int i=0;i <anno.size();i++){
				log.debug(anno.get(i).toString());
				cod_pers = anno.get(i).toString();
				if (cod_pers != null){
					return false;
				}
			}
			return true;
			*/
			if(anno!=null && anno.size()>0){//no intersecta
				return false;
			}else{//si intersecta
				return true;
			}
			//ICAPUNAY 21/06/2012 NO DEJA REGISTRAR UN RANGO NUEVO QUE INTERSECTE CON RANGO APROBADO
	}
    public boolean verificaRegistroAutorizaExcPK(String dbpool,
            String userOrig, String fecha, String horaini) throws SQLException{
      List anno = null;     
      log.debug("entro a metodo:verificaRegistroAutorizaExcPK");
      anno = executeQuery(dataSource, VERIFICA_REGISTRO_PK.toString(), new Object[] {
                  userOrig, new FechaBean(fecha).getSQLDate(),horaini});
      for(int i=0;i <anno.size();i++){
            log.debug(anno.get(i).toString());       
                  return true;
      }
      return false;
}

	
	public boolean insertRegistroAutorizaExc(String dbpool,Map datos)
			throws SQLException {
		
		int modificado = executeUpdate(dataSource, INSERTA_REGISTRO.toString(),
				new Object[] {datos.get("cod_pers"),datos.get("fec_aut"),datos.get("hor_ini_aut"),
				datos.get("hor_fin_aut"),datos.get("obs_sustento"),datos.get("cod_jefe_aut"),
				datos.get("ind_aut"),datos.get("cod_user_crea"),datos.get("fec_creacion")});
		return (modificado > 0);
	}
	
	public boolean updateRegistroAutorizaExcRechazar(Map datos)
			throws SQLException {
		FechaBean fecha1 = new FechaBean();
		
		int modificado = executeUpdate(dataSource, UPDATE_REGISTRO_INDICADOR.toString(),
				new Object[] {
				datos.get("ind_aut").toString(),
				datos.get("cod_user_mod"),
				fecha1.getTimestamp(),
				datos.get("cod_pers"),
				new FechaBean((String)datos.get("fec_aut")).getSQLDate(),
				datos.get("hor_ini_aut")
				});
		return (modificado > 0);
	}
	
	public boolean deleteRegistroAutorizaExc(String dbpool,Map datos) throws SQLException {
		
		int eliminados=0;
		if (log.isDebugEnabled()) log.debug("T4818DAO - eliminando registro AutorizaExc - codPers");
		
		try{				
			eliminados = executeUpdate(dataSource,ELIMINA_REGISTRO.toString(), new Object[]{datos.get("cod_pers"), datos.get("fec_aut"), 
				datos.get("hor_ini_aut")});	
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [DELETE_REGISTRO_AUTORIZAEXC]");
		}		
		return (eliminados>0);
	}
	
	
	//INICIO ICAPUNAY
	/**
	  * Metodo que busca si existe una fecha de autorizacion para un colaborador	
	  * @param codPers String
	  * @param fechaAutorizacion String	
	  * @return mfechaAutorizacion Map
	  * @throws DAOException
	  */
	public Map findFechaAutorizacion(String codPers, String fechaAutorizacion) throws DAOException {
		
		
		Map mfechaAutorizacion = null;
		if (log.isDebugEnabled()) log.debug("T4818DAO - findFechaAutorizacion - codPers: " + codPers + " - fechaAutorizacion: " + fechaAutorizacion);		
		try{
			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);			
			mfechaAutorizacion = executeQueryUniqueResult(dataSource, FIND_AUTORIZACION.toString(), new Object[]{codPers,
				new FechaBean(fechaAutorizacion).getSQLDate()});
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [FIND_AUTORIZACION]");
		}	
		return mfechaAutorizacion;
	}
	
		
	/**
	  * Metodo que devuelve los intervalos de horas autorizadas o rechazadas de un colaborador para una determinada fecha de autorizacion y un intervalo de permanencia extra
	  * @param codPers String
	  * @param fechaAutorizada Date
	  * @param horIniPerm String
	  * @param horFinPerm String
	  * @return intervalos List
	  * @throws DAOException
	  */
	public List findIntervalosAutorizaciones(String codPers, Date fechaAutorizada, String horIniPerm, String horFinPerm) throws DAOException {
		
		List intervalos= null;
		if (log.isDebugEnabled()) log.debug("T4818DAO - findIntervalosAutorizaciones - codPers: " + codPers + " - fechaAutorizada: " + fechaAutorizada);
		if (log.isDebugEnabled()) log.debug("T4818DAO - findIntervalosAutorizaciones - horIniPerm: " + horIniPerm + " - horFinPerm: " + horFinPerm);
		try{
			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			intervalos = executeQuery(dataSource, FIND_INTERVALOS_INTERSECTADOS_HORAS_AUTORIZADOS_RECHAZADOS.toString(), new Object[]{codPers,fechaAutorizada,horIniPerm,horIniPerm,horFinPerm,horFinPerm});
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [FIND_INTERVALOS_INTERSECTADOS_HORAS_AUTORIZADOS_RECHAZADOS]");
		}		
		return intervalos;
	}
	
	/**
	  * Metodo que devuelve un intervalo de autorizacion no intersectado para un colaborador segun la fecha y rango de hora de autorizacion	
	  * @param codPers String
	  * @param fechaAutorizada Date
	  * @param horaIniExtra String
	  * @param horaFinExtra String
	  * @return intervaloNoIntersectado Map
	  * @throws DAOException
	  */
	public Map findIntervaloAutorizadoNoIntersectado(String codPers,Date fechaAutorizada,String horaIniExtra,String horaFinExtra,String horaIniAut,String horaFinAut) throws DAOException {
		
		Map intervaloNoIntersectado = null;
		if (log.isDebugEnabled()) log.debug("T4818DAO - findIntervaloAutorizadoNoIntersectado - codPers: " + codPers + " - fechaAutorizada: " + fechaAutorizada);
		if (log.isDebugEnabled()) log.debug("T4818DAO - findIntervaloAutorizadoNoIntersectado - horaIniExtra: " + horaIniExtra + " - horaFinExtra: " + horaFinExtra);
		if (log.isDebugEnabled()) log.debug("T4818DAO - findIntervaloAutorizadoNoIntersectado - horaIniAut: " + horaIniAut + " - horaFinAut: " + horaFinAut);
		try{
			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);			
			intervaloNoIntersectado = executeQueryUniqueResult(dataSource,FIND_INTERVALO_NO_INTERSECTADO.toString(), new Object[]{codPers,fechaAutorizada,horaIniExtra,horaFinExtra,horaIniExtra,horaFinExtra,horaIniAut,horaFinAut});
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [FIND_INTERVALO_NO_INTERSECTADO]");
		}	
		return intervaloNoIntersectado;
	}
	//FIN ICAPUNAY
	

}
