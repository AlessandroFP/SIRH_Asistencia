package pe.gob.sunat.rrhh.asistencia.dao;

import java.sql.Timestamp;
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
 * Clase       : T4751DAO 
 * Descripcion : Clase encargada de administrar los datos de la tabla t4751MensXUuoo (Tabla que almacena el numero de colaboradores, la suma de horas y dias de los registros agrupados por regimen, intendencia, unidad organica, periodo y categoria de la tabla t4707Asistencia_h)
 * Proyecto    : ASISTENCIA 
 * Autor       : ICAPUNAY
 * Fecha       : 10-ENERO-2012 
 * 
 * */

public class T4751DAO extends DAOAbstract{
	
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
	public T4751DAO(Object datasource) {
		if (datasource instanceof DataSource)
		  this.datasource = (DataSource)datasource;
		else if (datasource instanceof String)
		  this.datasource = getDataSource((String)datasource);
		else
		  throw new DAOException(this, "Datasource no valido");
	}
		
	
	//consultas reporte mensual por unidad organica
	/**
	 * metodo findIntendencias_rptMenXUo_ByUUOOByInteByInst : encargado de buscar las intendencias de acuerdo a regimen, anio y criterio(unidad organica, intendencia o institucional) 	
	 * @param Map params (String regimen,String anio,String criterio,String valor,HashMap seguridad)	 
	 * @return List
	 * @exception DAOException
	 * */
	public List findIntendencias_rptMenXUo_ByUUOOByInteByInst(Map params) throws DAOException {				
		
		if(log.isDebugEnabled()) log.debug("T4751DAO findIntendencias_rptMenXUo_ByUUOOByInteByInst - params: " + params);	
		List intendencias = null;		
		StringBuffer SQL = new StringBuffer("");
		
		try{					
			String regimen = params.get("regimen").toString(); //0{276 - 728}, 1{1057} y 2{Formativas}			
			String anio = params.get("anio").toString();//2008
			String criterio = params.get("criterio").toString();//1=unidad organica; 4=intendencia; 3=institucional 
			String valor = params.get("valor").toString();//2B2100{uuoo} o 2B0000{intendencia}		
			HashMap seguridad = (HashMap)params.get("seguridad");
					
			SQL.append("select distinct(mu.cod_inte),u.t12des_corta ")					
					.append("from t12uorga u,t4751MensXUuoo mu,t1276periodo p ")
					.append("where mu.cod_inte = u.t12cod_uorga ")
					.append("and mu.per_mov = p.periodo "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					//regimen/modalidad
					if (regimen.equals("0")) { // Regimen 276 - 728				
						SQL.append("and mu.cod_reg not in (?,?) ");
						SQL.append("and p.fcierre[7,10]||p.fcierre[4,5]||p.fcierre[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("1")){//Regimen 1057
						SQL.append("and mu.cod_reg = ? ");
						SQL.append("and p.fec_cierre_cas[7,10]||p.fec_cierre_cas[4,5]||p.fec_cierre_cas[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("2")){//Modalidad Formativa
						SQL.append("and mu.cod_reg = ? ");
						SQL.append("and p.fec_cierre_mf[7,10]||p.fec_cierre_mf[4,5]||p.fec_cierre_mf[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}					
					//anio
					SQL.append("and mu.per_mov[1,4] = ? ");					
					//criterio: unidad organica o intendencia
					if (criterio.equals("1")){//unidad organica						
						SQL.append("and mu.cod_uo = ? ");		
					}else if (criterio.equals("4")){//intendencia						
						SQL.append("and mu.cod_inte = ? ");		
					}												
					//criterios de visibilidad
					if (seguridad != null) {					
						HashMap roles = (HashMap) seguridad.get("roles");
						String uoSeg = (String) seguridad.get("uoSeg");
						String uoAO = (String) seguridad.get("uoAO");//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
				        if (log.isDebugEnabled()) log.debug("roles: "+roles); //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
				        if (log.isDebugEnabled()) log.debug("uoAO: "+uoAO); //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
						if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
							SQL.append("and 1=1 ");
						} 
						//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
						else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null) {
							SQL.append("and ((mu.cod_uo like '").append(uoSeg).append( "') ");
							SQL.append("or (mu.cod_uo in (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '").append(uoSeg).append( "'))) ");
						}
						//else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null || //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
						else if (
						//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
								roles.get(Constantes.ROL_SECRETARIA) != null
								|| roles.get(Constantes.ROL_JEFE) != null) {
							SQL.append("and mu.cod_uo like '").append(uoSeg).append( "' ");
						} else {
							SQL.append("and 1=2 ");
						}
					}			
			Object o[]=null;		
			
			//regimen/modalidad
			if (regimen.equals("0")) { // Regimen 276 - 728	
				if (criterio.equals("1") || criterio.equals("4") ){//unidad organica o intendencia						
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,valor};
				}else if (criterio.equals("3")){//institucional
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio};
				}				
			}else if(regimen.equals("1")){//Regimen 1057
				if (criterio.equals("1") || criterio.equals("4") ){//unidad organica o intendencia	
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),new FechaBean().getFormatDate("yyyyMMdd"),anio,valor};
				}else if (criterio.equals("3")){//institucional
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),new FechaBean().getFormatDate("yyyyMMdd"),anio};
				}				
			}else if(regimen.equals("2")){//Modalidad Formativa
				if (criterio.equals("1") || criterio.equals("4") ){//unidad organica o intendencia	
					o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,valor};
				}else if (criterio.equals("3")){//institucional
					o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio};
				}				
			}						
			setIsolationLevel(T4751DAO.TX_READ_UNCOMMITTED);
			intendencias = executeQuery(datasource, SQL.toString(), o);
			setIsolationLevel(-1);			
			if(log.isDebugEnabled()) log.debug("T4751DAO findIntendencias_rptMenXUo_ByUUOOByInteByInst - intendencias:  " + intendencias);			
		
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta de metodo: findIntendencias_rptMenXUo_ByUUOOByInteByInst");
		}
		return intendencias;		
	}
	
	/**
	 * metodo findCabecera_rptMenXUo_ByUUOOByInteByInst : encargado de buscar los valores de los indicadores por periodos de una intendencia de acuerdo a regimen, anio, categoria e intendencia. Adicional se envia el parametro unidad organica si el criterio es unidad organica	
	 * @param Map params (String regimen,String anio,String categoria,String criterio,String valor,String intendencia)	 
	 * @return List
	 * @exception DAOException
	 * */
	public List findCabecera_rptMenXUo_ByUUOOByInteByInst(Map params) throws DAOException {				
		
		if(log.isDebugEnabled()) log.debug("T4751DAO findCabecera_rptMenXUo_ByUUOOByInteByInst - params: " + params);	
		List periodos = null;		
		StringBuffer SQL = new StringBuffer("");
		
		try{					
			String regimen = params.get("regimen").toString(); //0{276 - 728}, 1{1057} y 2{Formativas}			
			String anio = params.get("anio").toString();//2008
			String categoria = params.get("categoria").toString();//009 {otros}
			String criterio = params.get("criterio").toString();//1=unidad organica; 4=intendencia; 3=institucional 
			String intendencia = params.get("intendencia").toString();//2B0000 {INJ}
			String cod_uo = params.get("valor").toString();//2B2100			
								
			SQL.append("select mu.cod_inte,mu.per_mov, ")	
					.append("sum(mu.num_cola) as nc, sum(mu.mto_horas) as horas, ")
					.append("sum(mu.mto_dias) as dias,sum(mu.mto_dias_cola) as dc ")
					.append("from t4751MensXUuoo mu,t1276periodo p ")
					.append("where ")
					.append("mu.per_mov = p.periodo and "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					//regimen/modalidad
					if (regimen.equals("0")) { // Regimen 276 - 728				
						SQL.append("mu.cod_reg not in (?,?) ");
						SQL.append("and p.fcierre[7,10]||p.fcierre[4,5]||p.fcierre[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("1")){//Regimen 1057
						SQL.append("mu.cod_reg = ? ");
						SQL.append("and p.fec_cierre_cas[7,10]||p.fec_cierre_cas[4,5]||p.fcierre[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("2")){//Modalidad Formativa
						SQL.append("mu.cod_reg = ? ");
						SQL.append("and p.fec_cierre_mf[7,10]||p.fec_cierre_mf[4,5]||p.fec_cierre_mf[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}	
					//anio
					SQL.append("and mu.per_mov[1,4] = ? ");
					//categoria
					SQL.append("and mu.cod_cate = ? ");	
					//intendencia del BeanReporte
					SQL.append("and mu.cod_inte = ? ");
					//criterio: unidad organica
					if (criterio.equals("1")){						
						SQL.append("and mu.cod_uo = ? ");		
					}				
				SQL.append("group by 1,2 "); 	
						
			Object o[]=null;		
			
			//regimen/modalidad
			if (regimen.equals("0")) { // Regimen 276 - 728	
				if (criterio.equals("1")){//unidad organica	
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria,intendencia,cod_uo};
				}else if(criterio.equals("4") || criterio.equals("3")){//intendencia o institucional
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria,intendencia};
				}				
			}else if(regimen.equals("1")){//Regimen 1057
				if (criterio.equals("1")){//unidad organica	
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria,intendencia,cod_uo};
				}else if(criterio.equals("4") || criterio.equals("3")){//intendencia o institucional
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria,intendencia};
				}				
			}else if(regimen.equals("2")){//Modalidad Formativa
				if (criterio.equals("1")){//unidad organica	
					o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria,intendencia,cod_uo};
				}else if(criterio.equals("4") || criterio.equals("3")){//intendencia o institucional
					o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria,intendencia};
				}				
			}						
			setIsolationLevel(T4751DAO.TX_READ_UNCOMMITTED);
			periodos = executeQuery(datasource, SQL.toString(), o);
			setIsolationLevel(-1);			
			if(log.isDebugEnabled()) log.debug("T4751DAO findCabecera_rptMenXUo_ByUUOOByInteByInst - periodos:  " + periodos);			
		
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta de metodo: findCabecera_rptMenXUo_ByUUOOByInteByInst");
		}
		return periodos;		
	}
	
	/**
	 * metodo findDetalle_rptMenXUo_ByUUOOByInteByInst : encargado de buscar el detalle de uuoo por periodo con el total de horas, dias, numero de colaboradores y dias por colaborador de una intendencia seleccionada de acuerdo a regimen, anio, categoria e intendencia. Adicional se envia el parametro unidad organica si el criterio es unidad organica	
	 * @param Map params (String regimen,String anio,String categoria,String criterio,String valor,String intendencia)	 
	 * @return List
	 * @exception DAOException
	 * */
	public List findDetalle_rptMenXUo_ByUUOOByInteByInst(Map params) throws DAOException {				
		
		if(log.isDebugEnabled()) log.debug("T4751DAO findDetalle_rptMenXUo_ByUUOOByInteByInst - params: " + params);	
		List lista = null;		
		StringBuffer SQL = new StringBuffer("");
		String cod_Subuuoo="";
		
		try{					
			String regimen = params.get("regimen").toString(); //0{276 - 728}, 1{1057} y 2{Formativas}			
			String anio = params.get("anio").toString();//2008
			String categoria = params.get("categoria").toString();//009 {otros}
			String criterio = params.get("criterio").toString();//1=unidad organica; 4=intendencia; 3=institucional 
			String intendencia = params.get("intendencia").toString();//2B0000 {INJ}
			if (criterio.equals("1")){ //unidad organica						
				cod_Subuuoo = params.get("valor")!=null?(String)params.get("valor"):"";//2B2100{uuoo}		
			}else if(criterio.equals("4") || criterio.equals("3")){ //intendencia o institucional
				cod_Subuuoo = params.get("subuuoo")!=null?(String)params.get("subuuoo"):"";//2B0000 o 2B2100, etc {subuo de la lista de cada IntendenciaBeanReporte tanto de los criterios intendencia o institucional}
			}
						
			SQL.append("select mu.cod_uo,mu.per_mov, ")	
					.append("sum(mu.num_cola) as nc,sum(mu.mto_horas) as horas, ")
					.append("sum(mu.mto_dias) as dias,sum(mu.mto_dias_cola) as dc ")
					.append("from t4751MensXUuoo mu,t1276periodo p ")
					.append("where ")
					.append("mu.per_mov = p.periodo and "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					//regimen/modalidad
					if (regimen.equals("0")) { // Regimen 276 - 728				
						SQL.append("mu.cod_reg not in (?,?) ");
						SQL.append("and p.fcierre[7,10]||p.fcierre[4,5]||p.fcierre[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("1")){//Regimen 1057
						SQL.append("mu.cod_reg = ? ");
						SQL.append("and p.fec_cierre_cas[7,10]||p.fec_cierre_cas[4,5]||p.fec_cierre_cas[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("2")){//Modalidad Formativa
						SQL.append("mu.cod_reg = ? ");
						SQL.append("and p.fec_cierre_mf[7,10]||p.fec_cierre_mf[4,5]||p.fec_cierre_mf[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}					
					//anio
					SQL.append("and mu.per_mov[1,4] = ? ");
					//categoria
					SQL.append("and mu.cod_cate = ? ");
					//intendencia seleccionada
					SQL.append("and mu.cod_inte = ? ");
					if(!cod_Subuuoo.equals("") && cod_Subuuoo.trim().length()>0 ){
						//criterio: (valor si es unidad organica) y (subuuoo si es intendencia o institucional)
						SQL.append("and mu.cod_uo = ? ");
					}					
				SQL.append("group by 1,2 "); 	
						
			Object o[]=null;		
			
			//regimen/modalidad
			if (regimen.equals("0")) { // Regimen 276 - 728	
				if(!cod_Subuuoo.equals("") && cod_Subuuoo.trim().length()>0 ){
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria,intendencia,cod_Subuuoo};
				}else{
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria,intendencia};
				}				
			}else if(regimen.equals("1")){//Regimen 1057
				if(!cod_Subuuoo.equals("") && cod_Subuuoo.trim().length()>0 ){
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria,intendencia,cod_Subuuoo};
				}else{
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria,intendencia};
				}				
			}else if(regimen.equals("2")){//Modalidad Formativa
				if(!cod_Subuuoo.equals("") && cod_Subuuoo.trim().length()>0 ){	
					o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria,intendencia,cod_Subuuoo}; 
				}else{
					o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria,intendencia};
				}				
			}						
			setIsolationLevel(T4751DAO.TX_READ_UNCOMMITTED);
			lista = executeQuery(datasource, SQL.toString(), o);
			setIsolationLevel(-1);			
			if(log.isDebugEnabled()) log.debug("T4751DAO findDetalle_rptMenXUo_ByUUOOByInteByInst - lista:  " + lista);			
		
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta de metodo: findDetalle_rptMenXUo_ByUUOOByInteByInst");
		}
		return lista;		
	}	
	
	/**
	 * metodo findUUOOs_rptMenXMov_ByInte : encargado de buscar las uuoos involucradas con valores de una intendencia de acuerdo a regimen, anio, categoria e intendencia. Adicional se envia el parametro unidad organica si el criterio es unidad organica	
	 * @param Map params (String regimen,String anio,String categoria,String criterio,String valor)	
	 * @return List
	 * @exception DAOException
	 * */
	public List findUUOOs_rptMenXMov_ByInte(Map params) throws DAOException {				
		
		if(log.isDebugEnabled()) log.debug("T4751DAO findUUOOs_rptMenXMov_ByInte - params: " + params);
		List uuoos = null;		
		StringBuffer SQL = new StringBuffer("");
		
		try{					
			String regimen = params.get("regimen").toString(); //0{276 - 728}, 1{1057} y 2{Formativas}	
			String anio = params.get("anio").toString();//2008
			String categoria = params.get("categoria").toString();//009 {otros}			
			String intendencia = params.get("intendencia").toString();//2B0000 {INJ}
				
			
			SQL.append("select distinct(mu.cod_uo) ")	
					.append("from t4751MensXUuoo mu,t1276periodo p ")  
					.append("where ")
					.append("mu.per_mov = p.periodo and "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					//regimen/modalidad
					if (regimen.equals("0")) { // Regimen 276 - 728				
						SQL.append("mu.cod_reg not in (?,?) ");
						SQL.append("and p.fcierre[7,10]||p.fcierre[4,5]||p.fcierre[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("1")){//Regimen 1057
						SQL.append("mu.cod_reg = ? ");
						SQL.append("and p.fec_cierre_cas[7,10]||p.fec_cierre_cas[4,5]||p.fec_cierre_cas[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("2")){//Modalidad Formativa
						SQL.append("mu.cod_reg = ? ");
						SQL.append("and p.fec_cierre_mf[7,10]||p.fec_cierre_mf[4,5]||p.fec_cierre_mf[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}					
					//anio
					SQL.append("and mu.per_mov[1,4] = ? ");		
					//categoria
					SQL.append("and mu.cod_cate = ? ");		
					//intendencia del BeanReporte
					SQL.append("and mu.cod_inte = ? ");					

			Object o[]=null;			
			//regimen/modalidad
			if (regimen.equals("0")) { // Regimen 276 - 728					
				o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria,intendencia};							
			}else if(regimen.equals("1")){//Regimen 1057				
				o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria,intendencia};								
			}else if(regimen.equals("2")){//Modalidad Formativa				
				o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria,intendencia};					
			}						
			setIsolationLevel(T4751DAO.TX_READ_UNCOMMITTED);
			uuoos = executeQuery(datasource, SQL.toString(), o);
			setIsolationLevel(-1);
			
			if(log.isDebugEnabled()) log.debug("T4751DAO findUUOOs_rptMenXMov_ByInte - uuoos:  " + uuoos);			
		
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta de metodo: findUUOOs_rptMenXMov_ByInte");
		}
		return uuoos;		
	}
	
	/**
	 * metodo findAnualHorasDiasNcDcByCodUo_rptMenXUo_ByUUOOByInteByInst : encargado de buscar el valor de los indicadores anuales: horas y dias por unidad organica de acuerdo a regimen, anio, categoria y unidad organica	
	 * @param Map params (String regimen,String anio,String categoria,String subuuoo)	 
	 * @return Map
	 * @exception DAOException
	 * */
	public Map findAnualHorasDiasNcDcByCodUo_rptMenXUo_ByUUOOByInteByInst(Map params) throws DAOException {				
		
		if(log.isDebugEnabled()) log.debug("T4751DAO findAnualHorasDiasNcDcByCodUo_rptMenXUo_ByUUOOByInteByInst - params: " + params);	
		Map	horasDiasNcDcAnual = null;		
		StringBuffer SQL = new StringBuffer("");
		
		try{					
			String regimen = params.get("regimen").toString(); //0{276 - 728}, 1{1057} y 2{Formativas}			
			String anio = params.get("anio").toString();//2008
			String categoria = params.get("categoria").toString();//009 {otros}
			String intendencia = params.get("intendencia").toString();//2B0000 {INJ} //adicionado el 01/02/2012 para hacer query mas rapido
			String subuuoo = params.get("subuuoo").toString();//2B2100
								
			SQL.append("select mu.cod_uo, ")	
					.append("sum(mu.mto_horas) as horas, ")
					.append("sum(mu.mto_dias) as dias ")					
					.append(",sum(mu.num_cola) as nc, ")//02/02/2012 para optimizacion en tiempo de busqueda por uuoo e intendencia
					.append("sum(mu.mto_dias_cola) as dc ")//02/02/2012 para optimizacion en tiempo de busqueda por uuoo e intendencia
					.append("from t4751MensXUuoo mu,t1276periodo p ")
					.append("where ")
					.append("mu.per_mov = p.periodo and "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					//regimen/modalidad
					if (regimen.equals("0")) { // Regimen 276 - 728				
						SQL.append("mu.cod_reg not in (?,?) ");
						SQL.append("and p.fcierre[7,10]||p.fcierre[4,5]||p.fcierre[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("1")){//Regimen 1057
						SQL.append("mu.cod_reg = ? ");
						SQL.append("and p.fec_cierre_cas[7,10]||p.fec_cierre_cas[4,5]||p.fec_cierre_cas[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("2")){//Modalidad Formativa
						SQL.append("mu.cod_reg = ? ");
						SQL.append("and p.fec_cierre_mf[7,10]||p.fec_cierre_mf[4,5]||p.fec_cierre_mf[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}					
					//anio
					SQL.append("and mu.per_mov[1,4] = ? ");
					//categoria
					SQL.append("and mu.cod_cate = ? ");
					//intendencia del BeanReporte //adicionado el 01/02/2012 para hacer query mas rapido
					SQL.append("and mu.cod_inte = ? ");
					//subuuoo
					SQL.append("and mu.cod_uo = ? ");
				SQL.append("group by 1 "); 	
						
			Object o[]=null;		
			
			//regimen/modalidad
			if (regimen.equals("0")) { // Regimen 276 - 728				
				o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
						constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria,intendencia,subuuoo};
			}else if(regimen.equals("1")){//Regimen 1057
				o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria,intendencia,subuuoo};
			}else if(regimen.equals("2")){//Modalidad Formativa
				o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria,intendencia,subuuoo};
			}						
			setIsolationLevel(T4751DAO.TX_READ_UNCOMMITTED);
			horasDiasNcDcAnual = executeQueryUniqueResult(datasource, SQL.toString(), o);
			setIsolationLevel(-1);			
			if(log.isDebugEnabled()) log.debug("T4751DAO findAnualHorasDiasNcDcByCodUo_rptMenXUo_ByUUOOByInteByInst - horasDiasNcDcAnual:  " + horasDiasNcDcAnual);			
		
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta de metodo: findAnualHorasDiasNcDcByCodUo_rptMenXUo_ByUUOOByInteByInst");
		}
		return horasDiasNcDcAnual;		
	}	
	//fin consultas reporte mensual por unidad organica
	
}
