package pe.gob.sunat.rrhh.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.core.pattern.DynaBean;
import pe.gob.sunat.framework.util.date.FechaBean;

/**
 * 
 * Clase : RegistroSirhDAO 
 * Autor : PRAC-JCALLO
 * Fecha : 28/05/2008
 * 
 * Descripcion: Esta clase se creo con la finalidad de obtener los datos de la tabla Registro_Sirh de sp
 * 
 */

public class RegistroSirhDAO extends DAOAbstract {
	
	private DataSource dataSource = null;
	
	private final StringBuffer INSERT_LOG_SIRH = new StringBuffer("INSERT into registro_sirh ")
		.append("(cod_pers, ip, pc, accion, fecha, hora, valor) VALUES (?, ?, ?, ?, ?, ?, ?)");
	
	private final StringBuffer FIND_BY_PARAMS = new StringBuffer(" SELECT FIRST 200 nvl(cod_pers,' ') as cod_pers, ")
	.append(" nvl(ip,'') as ip , nvl(pc,' ') as pc, nvl(accion,' ') as accion , fecha , nvl(hora,' ') as hora, ")
	.append(" nvl(valor,' ') as valor, nvl(descripcion,' ') as descripcion from registro_sirh ");
	
	/**
	 * @param datasource Object
	*/
	public RegistroSirhDAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.dataSource = (DataSource)datasource;
	    else if (datasource instanceof String)
	    	this.dataSource = getDataSource((String)datasource);
	    else
	    	throw new DAOException(this, "Datasource no valido");
	}
	
	public void insertLogSirh(Map hm) throws DAOException{
		
		this.executeUpdate(dataSource, INSERT_LOG_SIRH.toString(), new Object[]{
				hm.get("codUsu"), hm.get("ip"), hm.get("pc"), hm.get("accion"),
				hm.get("fecha"), hm.get("hora"), hm.get("valor") });
		
	}
	
	/**
	 * Metodo de findByParams - busqueda por parametros 
	 * @param DynaBean params
	 * @return List listaResul
	 * @throws DAOException
	 */
	public List findByParams(DynaBean params) throws DAOException {
		
		List listaResul = new ArrayList();//lista de resultado del query
		List prms = new ArrayList();//listado de parametros para el query
		boolean flag = false;//si es el primero o los demas dentro del where
		
		StringBuffer sql = new StringBuffer(FIND_BY_PARAMS.toString()).append(" where ");
		
		if(params.isSet("cod_pers")&& params.getString("cod_pers").trim().length()>0){
			sql.append(" cod_pers = ? ");
			prms.add(params.getString("cod_pers").trim());
			flag = true;			
		}
		
		if(params.isSet("ip")&& params.getString("ip").trim().length()>0){
			if(flag) sql.append(" and ");			
			sql.append(" ip = ? ");
			prms.add(params.getString("ip").trim());
			flag = true;
		}
		
		if(params.isSet("pc")&& params.getString("pc").trim().length()>0){
			if(flag) sql.append(" and ");			
			sql.append(" pc = ? ");
			prms.add(params.getString("pc").trim());
			flag = true;
		}
		
		if(params.isSet("cod_accion")&& params.getString("cod_accion").trim().length()>0){
			if(flag) sql.append(" and ");			
			sql.append(" accion = ? ");
			prms.add(params.getString("cod_accion").trim());
			flag = true;
		}
		
		/*hora*/
		if(params.isSet("hora_ini")&& params.getString("hora_ini").trim().length()>0){
			if(flag) sql.append(" and ");			
			sql.append(" hora >= ?");
			prms.add(params.getString("hora_ini").trim());
			flag = true;
		}
		if(params.isSet("hora_fin")&& params.getString("hora_fin").trim().length()>0){
			if(flag) sql.append(" and ");			
			sql.append(" hora <= ? ");
			prms.add(params.getString("hora_fin").trim());
			flag = true;
		}
		/*fin hora*/
		
		if(params.isSet("valor")&& params.getString("valor").trim().length()>0){
			if(flag) sql.append(" and ");			 
			sql.append(" valor like '"+params.getString("valor").trim().replaceAll("'", "''")+"%' ");			
			flag = true;
		}
		
		if(params.isSet("descripcion")&& params.getString("descripcion").trim().length()>0){
			if(flag) sql.append(" and ");			
			sql.append(" descripcion = ? ");
			prms.add(params.getString("descripcion").trim());
			flag = true;
		}
		
		/*fecha*/
		
		if(params.isSet("fec_ini")&& params.getString("fec_ini").trim().length()>0){
			if(flag) sql.append(" and ");			
			sql.append(" fecha >= ? ");
			FechaBean fec_ini = new FechaBean(params.getString("fec_ini").trim());
			prms.add(fec_ini.getTimestamp());
			flag = true;
		}
		
		if(params.isSet("fec_fin")&& params.getString("fec_fin").trim().length()>0){
			if(flag) sql.append(" and ");			
			sql.append(" fecha <= ? ");
			FechaBean fec_fin = new FechaBean(params.getString("fec_fin").trim());
			prms.add(fec_fin.getTimestamp());
			flag = true;
		}	
		
		sql.append(" order by 5 desc ");
		
		listaResul = executeQuery(dataSource, sql.toString(), prms.toArray());
		
		return listaResul;
		
	}
}
