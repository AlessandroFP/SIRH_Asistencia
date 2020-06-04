package pe.gob.sunat.rrhh.asistencia.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.Propiedades;
import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.utils.Constantes;

/** 
 * 
 * Clase       : T4753DAO 
 * Descripcion : Clase encargada de administrar los datos de la tabla t4753MensXCola (Tabla que almacena la suma de horas y dias de los registros agrupados por regimen, unidad organica, periodo , categoria y colaborador de la tabla t4707Asistencia_h)
 * Proyecto    : ASISTENCIA 
 * Autor       : ICAPUNAY
 * Fecha       : 10-ENERO-2012 
 * 
 * */

public class T4753DAO extends DAOAbstract{
	
	private DataSource datasource;
	
	Propiedades constantes = new Propiedades(getClass(), "/constantes.properties");
	
		 
	/**
	* 
	* Este constructor del DAO dicierne como crear el datasource
	* dependiendo del tipo de objeto que recibe. Esto nos ayuda a
	* mejorar la invocacion del dao.
	* 
	* @param datasource Object
	*/
	public T4753DAO(Object datasource) {
		if (datasource instanceof DataSource)
		  this.datasource = (DataSource)datasource;
		else if (datasource instanceof String)
		  this.datasource = getDataSource((String)datasource);
		else
		  throw new DAOException(this, "Datasource no valido");
	}
		
	
	//consultas reporte mensual por colaborador
	/**
	 * metodo findColaboradores_rptMenXCol: encargado de buscar el total de colaboradores de acuerdo a regimen,anio, categoria, criterio(registro,uuoo,intendencia o institucional) y valor(si criterio es registro,uuoo o intendencia)	
	 * @param Map params (String regimen,String anio,String categoria,String criterio,String valor,HashMap seguridad)	 
	 * @return List
	 * @exception DAOException
	 * */
	public List findColaboradores_rptMenXCol(Map params) throws DAOException {				
		
		if(log.isDebugEnabled()) log.debug("T4753DAO findColaboradores_rptMenXCol - params: " + params);	
		List colaboradores = null;		
		StringBuffer SQL = new StringBuffer("");
		
		try{					
			String regimen = params.get("regimen").toString(); //0{276 - 728}, 1{1057} y 2{Formativas}			
			String anio = params.get("anio").toString();//2008
			String categoria = params.get("cod_cate").toString();//009 {otros}
			String criterio = params.get("criterio").toString();//0=registro; 1=unidad organica; 4=intendencia; 3=institucional 
			String valor = params.get("valor").toString();//0091{registro}, 2B2100{uuoo} o 2B0000{intendencia}
			if (criterio.equals("4")){//intendencia				
				valor = valor.substring(0,2);//2B	
			}			
			HashMap seguridad = (HashMap)params.get("seguridad");
					
			SQL.append("select distinct(mc.cod_pers),p.t02ap_pate,p.t02ap_mate,p.t02nombres, ")	
					.append("mc.cod_uo,u.t12des_uorga ")  
					.append("from t12uorga u,t02perdp p,t4753mensxcola mc,t1276periodo pe ")
					.append("where mc.cod_pers=p.t02cod_pers ")
					.append("and mc.cod_uo=u.t12cod_uorga ")
					.append("and mc.per_mov = pe.periodo "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					//regimen/modalidad
					if (regimen.equals("0")) { //Regimen 276 - 728				
						SQL.append("and mc.cod_reg not in (?,?) ");
						SQL.append("and pe.fcierre[7,10]||pe.fcierre[4,5]||pe.fcierre[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("1")){//Regimen 1057
						SQL.append("and mc.cod_reg = ? ");
						SQL.append("and pe.fec_cierre_cas[7,10]||pe.fec_cierre_cas[4,5]||pe.fec_cierre_cas[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("2")){//Modalidad Formativa
						SQL.append("and mc.cod_reg = ? ");
						SQL.append("and pe.fec_cierre_mf[7,10]||pe.fec_cierre_mf[4,5]||pe.fec_cierre_mf[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}					
					//anio
					SQL.append("and mc.per_mov[1,4] = ? ");	
					//categoria
					SQL.append("and mc.cod_cate = ? ");		
					//criterio: registro, unidad organica o intendencia
					if (criterio.equals("0")){//registro						
						SQL.append("and mc.cod_pers = ? ");		
					}else if (criterio.equals("1")){//unidad organica						
						SQL.append("and mc.cod_uo = ? ");		
					}else if (criterio.equals("4")){//intendencia						
						SQL.append("and mc.cod_uo[1,2] = ? ");		
					}							
					//criterios de visibilidad
					if (seguridad != null) {					
						HashMap roles = (HashMap) seguridad.get("roles");
						String uoSeg = (String) seguridad.get("uoSeg");	
						String uoAO = (String) seguridad.get("uoAO");//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

						if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
							SQL.append("and 1=1 ");
						} else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null) {
							SQL.append("and ((mc.cod_uo like '").append(uoSeg).append( "') ");
							
							SQL.append("or (mc.cod_uo in ")
							.append("(select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '")
							.append(uoAO).append( "'))) ");
							
						}  else if (//roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null ||
								   roles.get(Constantes.ROL_SECRETARIA) != null
								|| roles.get(Constantes.ROL_JEFE) != null) {
							SQL.append("and mc.cod_uo like '").append(uoSeg).append( "' ");
						} else {
							SQL.append("and 1=2 ");
						}
					}
					//SQL.append("order by mc.cod_uo asc,mc.cod_pers asc ");		
					SQL.append("order by mc.cod_pers asc ");
					
							
			Object o[]=null;		
			
			//regimen/modalidad
			if (regimen.equals("0")) { // Regimen 276 - 728		
				if (criterio.equals("0") || criterio.equals("1") || criterio.equals("4")){//registro, unidad organica o intendencia
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria,valor};
				}else{//institucional
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria};
				}				
			}else if(regimen.equals("1")){//Regimen 1057
				if (criterio.equals("0") || criterio.equals("1") || criterio.equals("4")){//registro, unidad organica o intendencia
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria,valor};
				}else{//institucional
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria};
				}				
			}else if(regimen.equals("2")){//Modalidad Formativa
				if (criterio.equals("0") || criterio.equals("1") || criterio.equals("4")){//registro, unidad organica o intendencia
					o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria,valor};
				}else{//institucional
					o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria};
				}				
			}						
			setIsolationLevel(T4753DAO.TX_READ_UNCOMMITTED);
			colaboradores = executeQuery(datasource, SQL.toString(), o);
			setIsolationLevel(-1);			
			if(log.isDebugEnabled()) log.debug("T4753DAO findColaboradores_rptMenXCol - colaboradores:  " + colaboradores);			
		
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta de metodo: findColaboradores_rptMenXCol");
		}
		return colaboradores;		
	}
	
	/**
	 * metodo findHorasDiasByCategoriaByColaborador_rptMenXCol: encargado de buscar el total de horas y dias por categoria de un colaborador de acuerdo a regimen, anio, categoria, colaborador y uuoo del colaborador	
	 * @param Map params (String regimen,String anio,String categoria,String colaborador,String uuooColaborador)	 
	 * @return List
	 * @exception DAOException
	 * */
	public List findHorasDiasByCategoriaByColaborador_rptMenXCol(Map params) throws DAOException {				
		
		if(log.isDebugEnabled()) log.debug("T4753DAO findHorasDiasByCategoriaByColaborador_rptMenXCol - params: " + params);	
		List listaColaborador = null;		
		StringBuffer SQL = new StringBuffer("");
		
		try{					
			String regimen = params.get("regimen").toString(); //0{276 - 728}, 1{1057} y 2{Formativas}			
			String anio = params.get("anio").toString();//2008
			String categoria = params.get("cod_cate").toString();//009 {otros}			
			String colaborador = params.get("colaborador").toString();//0091{registro}
			String uuooColaborador = params.get("uuooColaborador").toString();//2B2100{registro}
											
			SQL.append("select cod_pers,per_mov,mto_horas as horas,mto_dias as dias ")	
					.append("from t4753mensxcola,t1276periodo p ")  
					.append("where ")
					.append("per_mov = p.periodo and "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					//regimen/modalidad
					if (regimen.equals("0")) { // Regimen 276 - 728				
						SQL.append("cod_reg not in (?,?) ");
						SQL.append("and p.fcierre[7,10]||p.fcierre[4,5]||p.fcierre[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("1")){//Regimen 1057
						SQL.append("cod_reg = ? ");
						SQL.append("and p.fec_cierre_cas[7,10]||p.fec_cierre_cas[4,5]||p.fec_cierre_cas[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("2")){//Modalidad Formativa
						SQL.append("cod_reg = ? ");
						SQL.append("and p.fec_cierre_mf[7,10]||p.fec_cierre_mf[4,5]||p.fec_cierre_mf[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}	
					//uuooColaborador
					SQL.append("and cod_uo = ? ");
					//anio
					SQL.append("and per_mov[1,4] = ? ");	
					//categoria
					SQL.append("and cod_cate = ? ");		
					//colaborador
					SQL.append("and cod_pers = ? ");					
				//SQL.append("order by per_mov asc "); 							
			
			Object o[]=null;		
			
			//regimen/modalidad
			if (regimen.equals("0")) { // Regimen 276 - 728					
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),uuooColaborador,anio,categoria,colaborador};							
			}else if(regimen.equals("1")){//Regimen 1057				
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),new FechaBean().getFormatDate("yyyyMMdd"),uuooColaborador,anio,categoria,colaborador};								
			}else if(regimen.equals("2")){//Modalidad Formativa				
					o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),uuooColaborador,anio,categoria,colaborador};							
			}						
			setIsolationLevel(T4753DAO.TX_READ_UNCOMMITTED);
			listaColaborador = executeQuery(datasource, SQL.toString(), o);
			setIsolationLevel(-1);			
			if(log.isDebugEnabled()) log.debug("T4753DAO findHorasDiasByCategoriaByColaborador_rptMenXCol - listaColaborador:  " + listaColaborador);			
		
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta de metodo: findHorasDiasByCategoriaByColaborador_rptMenXCol");
		}
		return listaColaborador;		
	}
	//fin consultas reporte mensual por unidad organica
	
}
