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
 * Clase       : T4752DAO 
 * Descripcion : Clase encargada de administrar los datos de la tabla t4752MensXMovi (Tabla que almacena la suma de horas y dias de los registros agrupados por regimen, unidad organica, periodo y movimiento de la tabla t4707Asistencia_h)
 * Proyecto    : ASISTENCIA 
 * Autor       : ICAPUNAY
 * Fecha       : 10-ENERO-2012 
 * 
 * */

public class T4752DAO extends DAOAbstract{
	
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
	public T4752DAO(Object datasource) {
		if (datasource instanceof DataSource)
		  this.datasource = (DataSource)datasource;
		else if (datasource instanceof String)
		  this.datasource = getDataSource((String)datasource);
		else
		  throw new DAOException(this, "Datasource no valido");
	}
		
	
	//consultas reporte mensual por movimiento
	/**
	 * metodo findCabecera_rptMenXMov_ByUUOOByInteByInst : encargado de buscar el total de horas y dias de los movimientos por periodos de una uuoo, intendencia o institucional de acuerdo a regimen, anio, criterio(unidad organica, intendencia o institucional) y categoria	
	 * @param Map params (String regimen,String anio,String criterio,String valor,String categoria,HashMap seguridad)	 
	 * @return List
	 * @exception DAOException
	 * */
	public List findCabecera_rptMenXMov_ByUUOOByInteByInst(Map params) throws DAOException {				
		
		if(log.isDebugEnabled()) log.debug("T4752DAO findCabecera_rptMenXMov_ByUUOOByInteByInst - params: " + params);	
		List periodos = null;		
		StringBuffer SQL = new StringBuffer("");
		
		try{					
			String regimen = params.get("regimen").toString(); //0{276 - 728}, 1{1057} y 2{Formativas}			
			String anio = params.get("anio").toString();//2008
			String criterio = params.get("criterio").toString();//1=unidad organica; 4=intendencia; 3=institucional 
			String valor = params.get("valor").toString();//2B2100 o 2B0000
			String categoria = params.get("categoria").toString();//009 {otros}
			if (criterio.equals("4")){//intendencia				
				valor = valor.substring(0,2);//2B	
			}		
			HashMap seguridad = (HashMap)params.get("seguridad");
					
			SQL.append("select mm.per_mov, ")	
					.append("sum(mm.mto_horas) as horas,sum(mm.mto_dias) as dias ")  
					.append("from t4636MovXCat m,t4752MensXMovi mm,t1276periodo p ")
					.append("where m.cod_mov=mm.cod_mov ")
					.append("and mm.per_mov = p.periodo "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					//regimen/modalidad
					if (regimen.equals("0")) { // Regimen 276 - 728				
						SQL.append("and mm.cod_reg not in (?,?) ");
						SQL.append("and p.fcierre[7,10]||p.fcierre[4,5]||p.fcierre[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("1")){//Regimen 1057
						SQL.append("and mm.cod_reg = ? ");
						SQL.append("and p.fec_cierre_cas[7,10]||p.fec_cierre_cas[4,5]||p.fec_cierre_cas[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("2")){//Modalidad Formativa
						SQL.append("and mm.cod_reg = ? ");
						SQL.append("and p.fec_cierre_mf[7,10]||p.fec_cierre_mf[4,5]||p.fec_cierre_mf[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}					
					//anio
					SQL.append("and mm.per_mov[1,4] = ? ");
					//criterio: unidad organica o intendencia
					if (criterio.equals("1")){
						//unidad organica
						SQL.append("and mm.cod_uo = ? ");		
					}else if (criterio.equals("4")){
						//intendencia
						SQL.append("and mm.cod_uo[1,2] = ? ");		
					}								
					//categoria
					SQL.append("and m.cod_cate = ? ");										
					//criterios de visibilidad
					if (seguridad != null) {					
						HashMap roles = (HashMap) seguridad.get("roles");
						String uoSeg = (String) seguridad.get("uoSeg");	
						String uoAO = (String) seguridad.get("uoAO");//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

						if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
							SQL.append("and 1=1 ");
						} else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null) {
							SQL.append("and ((mm.cod_uo like '").append(uoSeg).append( "') ");
							SQL.append("or (mm.cod_uo in ")
							.append("(select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '")
							.append(uoAO).append( "'))) ");
						}  else if (//roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null ||
								 roles.get(Constantes.ROL_SECRETARIA) != null
								|| roles.get(Constantes.ROL_JEFE) != null) {
							SQL.append("and mm.cod_uo like '").append(uoSeg).append( "' ");
						} else {
							SQL.append("and 1=2 ");
						}
					}								
				SQL.append("group by 1 "); 	
				//SQL.append("order by 1 asc"); 			
			
			Object o[]=null;		
			
			//regimen/modalidad
			if (regimen.equals("0")) { // Regimen 276 - 728	
				if(criterio.equals("1") || criterio.equals("4")){//uuoo o intendencia
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,valor,categoria};
				}else if(criterio.equals("3")){//institucional
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria};
				}				
			}else if(regimen.equals("1")){//Regimen 1057
				if(criterio.equals("1") || criterio.equals("4")){//uuoo o intendencia
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),new FechaBean().getFormatDate("yyyyMMdd"),anio,valor,categoria};
				}else if(criterio.equals("3")){//institucional
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria};
				}				
			}else if(regimen.equals("2")){//Modalidad Formativa
				if(criterio.equals("1") || criterio.equals("4")){//uuoo o intendencia
					o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,valor,categoria};
				}else if(criterio.equals("3")){//institucional
					o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria};
				}				
			}						
			setIsolationLevel(T4752DAO.TX_READ_UNCOMMITTED);
			periodos = executeQuery(datasource, SQL.toString(), o);
			setIsolationLevel(-1);			
			if(log.isDebugEnabled()) log.debug("T4752DAO findCabecera_rptMenXMov_ByUUOOByInteByInst - periodos:  " + periodos);			
		
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta de metodo: findCabecera_rptMenXMov_ByUUOOByInteByInst");
		}
		return periodos;		
	}	
	
	/**
	 * metodo findDetalle_rptMenXMov_ByUUOOByInteByInst : encargado de buscar los detalles de movimientos por periodo del total de horas y dias de los movimientos de una uuoo, intendencia o institucional de acuerdo a regimen, anio, criterio(unidad organica, intendencia o institucional) y categoria	
	 * @param Map params (String regimen,String anio,String criterio,String valor,String categoria)	
	 * @return List
	 * @exception DAOException
	 * */
	public List findDetalle_rptMenXMov_ByUUOOByInteByInst(Map params) throws DAOException {				
		
		if(log.isDebugEnabled()) log.debug("T4752DAO findDetalle_rptMenXMov_ByUUOOByInteByInst - params: " + params);	
		List lista = null;		
		StringBuffer SQL = new StringBuffer("");
		
		try{					
			String regimen = params.get("regimen").toString(); //0{276 - 728}, 1{1057} y 2{Formativas}	
			String anio = params.get("anio").toString();//2008
			String criterio = params.get("criterio").toString();//1=unidad organica; 4=intendencia; 3=institucional 
			String valor = params.get("valor").toString();//2B2100{uuoo} o 2B0000{intendencia}
			if (criterio.equals("4")){//intendencia				
				valor = valor.substring(0,2);//2B	
			}			
			String categoria = params.get("categoria").toString();//009 {otros}
					
			SQL.append("select mm.cod_mov,mm.per_mov, ")	
					.append("sum(mm.mto_horas) as horas, ")  
					.append("sum(mm.mto_dias) as dias ")
					.append("from t4636MovXCat m,t4752MensXMovi mm,t1276periodo p ")
					.append("where m.cod_mov = mm.cod_mov ")
					.append("and mm.per_mov = p.periodo "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					//regimen/modalidad
					if (regimen.equals("0")) { // Regimen 276 - 728				
						SQL.append("and mm.cod_reg not in (?,?) ");
						SQL.append("and p.fcierre[7,10]||p.fcierre[4,5]||p.fcierre[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("1")){//Regimen 1057
						SQL.append("and mm.cod_reg = ? ");
						SQL.append("and p.fec_cierre_cas[7,10]||p.fec_cierre_cas[4,5]||p.fec_cierre_cas[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("2")){//Modalidad Formativa
						SQL.append("and mm.cod_reg = ? ");
						SQL.append("and p.fec_cierre_mf[7,10]||p.fec_cierre_mf[4,5]||p.fec_cierre_mf[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}					
					//anio
					SQL.append("and mm.per_mov[1,4] = ? ");					
					//criterio: unidad organica o intendencia
					if (criterio.equals("1")){
						//unidad organica
						SQL.append("and mm.cod_uo = ? ");		
					}else if (criterio.equals("4")){
						//intendencia
						SQL.append("and mm.cod_uo[1,2] = ? ");		
					}							
					//categoria
					SQL.append("and m.cod_cate = ? ");									
							
				SQL.append("group by 1,2 "); 	
				//SQL.append("order by 1,2 "); 			
			
			Object o[]=null;			
			//regimen/modalidad
			if (regimen.equals("0")) { // Regimen 276 - 728	
				if(criterio.equals("1") || criterio.equals("4")){//uuoo o intendencia
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,valor,categoria};
				}else if(criterio.equals("3")){//institucional
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria};
				}				
			}else if(regimen.equals("1")){//Regimen 1057
				if(criterio.equals("1") || criterio.equals("4")){//uuoo o intendencia
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),new FechaBean().getFormatDate("yyyyMMdd"),anio,valor,categoria};
				}else if(criterio.equals("3")){//institucional
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria};
				}				
			}else if(regimen.equals("2")){//Modalidad Formativa
				if(criterio.equals("1") || criterio.equals("4")){//uuoo o intendencia
					o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,valor,categoria};
				}else if(criterio.equals("3")){//institucional
					o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria};
				}				
			}						
			setIsolationLevel(T4752DAO.TX_READ_UNCOMMITTED);
			lista = executeQuery(datasource, SQL.toString(), o);
			setIsolationLevel(-1);
			
			if(log.isDebugEnabled()) log.debug("T4752DAO findDetalle_rptMenXMov_ByUUOOByInteByInst - lista:  " + lista);			
		
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta de metodo: findDetalle_rptMenXMov_ByUUOOByInteByInst");
		}
		return lista;		
	}
	
	/**
	 * metodo findMovimientos_rptMenXMov_ByUUOOByInteByInst : encargado de buscar los movimientos involucrados de una uuoo,intendencia o institucional de acuerdo a regimen, anio, criterio(unidad organica, intendencia o institucional) y categoria	
	 * @param Map params (String regimen,String anio,String criterio,String valor,String categoria)	
	 * @return List
	 * @exception DAOException
	 * */
	public List findMovimientos_rptMenXMov_ByUUOOByInteByInst(Map params) throws DAOException {				
		
		if(log.isDebugEnabled()) log.debug("T4752DAO findMovimientos_rptMenXMov_ByUUOOByInteByInst - params: " + params);
		List movimientos = null;		
		StringBuffer SQL = new StringBuffer("");
		
		try{					
			String regimen = params.get("regimen").toString(); //0{276 - 728}, 1{1057} y 2{Formativas}			
			String anio = params.get("anio").toString();//2008	
			String criterio = params.get("criterio").toString();//1=unidad organica; 4=intendencia; 3=institucional 
			String valor = params.get("valor").toString();//2B2100{uuoo} o 2B0000{intendencia}
			if (criterio.equals("4")){//intendencia				
				valor = valor.substring(0,2);//2B	
			}		
			String categoria = params.get("categoria").toString();//009 {otros}
					
			SQL.append("select distinct(mm.cod_mov) ")	
					.append("from t4636MovXCat m,t4752MensXMovi mm,t1276periodo p ")  
					.append("where m.cod_mov = mm.cod_mov ")
					.append("and mm.per_mov = p.periodo "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					//regimen/modalidad
					if (regimen.equals("0")) { // Regimen 276 - 728				
						SQL.append("and mm.cod_reg not in (?,?) ");
						SQL.append("and p.fcierre[7,10]||p.fcierre[4,5]||p.fcierre[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("1")){//Regimen 1057
						SQL.append("and mm.cod_reg = ? ");
						SQL.append("and p.fec_cierre_cas[7,10]||p.fec_cierre_cas[4,5]||p.fec_cierre_cas[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("2")){//Modalidad Formativa
						SQL.append("and mm.cod_reg = ? ");
						SQL.append("and p.fec_cierre_mf[7,10]||p.fec_cierre_mf[4,5]||p.fec_cierre_mf[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}					
					//anio
					SQL.append("and mm.per_mov[1,4] = ? ");	
					//criterio: unidad organica o intendencia
					if (criterio.equals("1")){
						//unidad organica
						SQL.append("and mm.cod_uo = ? ");		
					}else if (criterio.equals("4")){
						//intendencia
						SQL.append("and mm.cod_uo[1,2] = ? ");		
					}									
					//categoria
					SQL.append("and m.cod_cate = ? ");									
							
				//SQL.append("order by 1 "); 			
			
			Object o[]=null;			
			//regimen/modalidad
			if (regimen.equals("0")) { // Regimen 276 - 728	
				if(criterio.equals("1") || criterio.equals("4")){//uuoo o intendencia
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,valor,categoria};
				}else if(criterio.equals("3")){//institucional
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria};
				}				
			}else if(regimen.equals("1")){//Regimen 1057
				if(criterio.equals("1") || criterio.equals("4")){//uuoo o intendencia
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),new FechaBean().getFormatDate("yyyyMMdd"),anio,valor,categoria};
				}else if(criterio.equals("3")){//institucional
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria};
				}				
			}else if(regimen.equals("2")){//Modalidad Formativa
				if(criterio.equals("1") || criterio.equals("4")){//uuoo o intendencia
					o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,valor,categoria};
				}else if(criterio.equals("3")){//institucional
					o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria};
				}				
			}						
			setIsolationLevel(T4752DAO.TX_READ_UNCOMMITTED);
			movimientos = executeQuery(datasource, SQL.toString(), o);
			setIsolationLevel(-1);
			
			if(log.isDebugEnabled()) log.debug("T4752DAO findMovimientos_rptMenXMov_ByUUOOByInteByInst - movimientos:  " + movimientos);			
		
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta de metodo: findMovimientos_rptMenXMov_ByUUOOByInteByInst");
		}
		return movimientos;		
	}
	
	/**
	 * metodo findHorasDiasByMovimiento_rptMenXMov_ByUUOOByInteByInst : encargado de buscar el total de horas y dias de los movimientos anuales de una uuoo,intendencia o institucional de acuerdo a regimen, anio, criterio(unidad organica, intendencia o institucional), categoria y movimiento	
	 * @param Map params (String regimen,String cod_uo,String anio,String categoria)	 
	 * @return Map
	 * @exception DAOException
	 * */
	public Map findHorasDiasByMovimiento_rptMenXMov_ByUUOOByInteByInst(Map params) throws DAOException {				
		
		if(log.isDebugEnabled()) log.debug("T4752DAO findHorasDiasByMovimiento_rptMenXMov_ByUUOOByInteByInst - params: " + params);		
		Map mMovimiento = null;		
		StringBuffer SQL = new StringBuffer("");
		
		try{					
			String regimen = params.get("regimen").toString(); //0{276 - 728}, 1{1057} y 2{Formativas}	
			String anio = params.get("anio").toString();//2008
			String criterio = params.get("criterio").toString();//1=unidad organica; 4=intendencia; 3=institucional 
			String valor = params.get("valor").toString();//2B2100{uuoo} o 2B{intendencia}
			if (criterio.equals("4")){//intendencia				
				valor = valor.substring(0,2);//2B	
			}			
			String categoria = params.get("categoria").toString();//009 {otros}
			String movimiento = params.get("movimiento").toString();//38 {licencia por feriado compensable}
					
			SQL.append("select mm.cod_mov, ")	
					.append("sum(mm.mto_horas) as horas,sum(mm.mto_dias) as dias ")  
					.append("from t4636MovXCat m,t4752MensXMovi mm,t1276periodo p ")
					.append("where m.cod_mov = mm.cod_mov ")
					.append("and mm.per_mov = p.periodo "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					//regimen/modalidad
					if (regimen.equals("0")) { // Regimen 276 - 728				
						SQL.append("and mm.cod_reg not in (?,?) ");
						SQL.append("and p.fcierre[7,10]||p.fcierre[4,5]||p.fcierre[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("1")){//Regimen 1057
						SQL.append("and mm.cod_reg = ? ");
						SQL.append("and p.fec_cierre_cas[7,10]||p.fec_cierre_cas[4,5]||p.fec_cierre_cas[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("2")){//Modalidad Formativa
						SQL.append("and mm.cod_reg = ? ");
						SQL.append("and p.fec_cierre_mf[7,10]||p.fec_cierre_mf[4,5]||p.fec_cierre_mf[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}					
					//anio
					SQL.append("and mm.per_mov[1,4] = ? ");					
					//criterio: unidad organica o intendencia
					if (criterio.equals("1")){
						//unidad organica
						SQL.append("and mm.cod_uo = ? ");		
					}else if (criterio.equals("4")){
						//intendencia
						SQL.append("and mm.cod_uo[1,2] = ? ");		
					}							
					//categoria
					SQL.append("and m.cod_cate = ? ");
					//movimiento
					SQL.append("and mm.cod_mov = ? ");	
									
				SQL.append("group by 1 ");			 			
			
			Object o[]=null;		
			
			//regimen/modalidad
			if (regimen.equals("0")) { // Regimen 276 - 728		
				if(criterio.equals("1") || criterio.equals("4")){//uuoo o intendencia
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,valor,categoria,movimiento};
				}else if(criterio.equals("3")){//institucional
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria,movimiento};
				}				
			}else if(regimen.equals("1")){//Regimen 1057
				if(criterio.equals("1") || criterio.equals("4")){//uuoo o intendencia
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),new FechaBean().getFormatDate("yyyyMMdd"),anio,valor,categoria,movimiento};
				}else if(criterio.equals("3")){//institucional
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria,movimiento};
				}				
			}else if(regimen.equals("2")){//Modalidad Formativa
				if(criterio.equals("1") || criterio.equals("4")){//uuoo o intendencia
					o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,valor,categoria,movimiento};
				}else if(criterio.equals("3")){//institucional
					o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria,movimiento};
				}				
			}						
			setIsolationLevel(T4752DAO.TX_READ_UNCOMMITTED);
			mMovimiento = executeQueryUniqueResult(datasource, SQL.toString(), o);
			setIsolationLevel(-1);			
			if(log.isDebugEnabled()) log.debug("T4752DAO findHorasDiasByMovimiento_rptMenXMov_ByUUOOByInteByInst - mMovimiento:  " + mMovimiento);			
		
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta de metodo: findHorasDiasByMovimiento_rptMenXMov_ByUUOOByInteByInst");
		}
		return mMovimiento;		
	}
	//fin consultas reporte mensual por movimiento
	
	
	//consultas reporte diario por movimiento
	/**
	 * metodo findMovimientos_rptDiaXMov_ByUUOOByInteByInst : encargado de buscar los movimientos involucrados de acuerdo a regimen, periodo, criterio(unidad organica, intendencia o institucional), valor(solo unidad organica e intendencia) y categoria	
	 * @param Map params (String regimen,String periodo,String categoria,String criterio,String valor)	
	 * @return List
	 * @exception DAOException
	 * */
	public List findMovimientos_rptDiaXMov_ByUUOOByInteByInst(Map params) throws DAOException {				
		
		if(log.isDebugEnabled()) log.debug("T4752DAO findMovimientos_rptDiaXMov_ByUUOOByInteByInst - params: " + params);
		List movimientos = null;		
		StringBuffer SQL = new StringBuffer("");
		
		try{					
			String regimen = params.get("regimen").toString(); //0{276 - 728}, 1{1057} y 2{Formativas}			
			String periodo = params.get("periodo").toString();//200805
			String categoria = params.get("categoria").toString();//009 {otros}
			String criterio = params.get("criterio").toString();//1=unidad organica; 4=intendencia; 3=institucional 
			String valor = params.get("valor").toString();//2B2100 (unidad organica) y 2B0000 (intendencia)
			if (criterio.equals("4")){//intendencia				
				valor = valor.substring(0,2);//2B	
			}	
					
			SQL.append("select distinct(mm.cod_mov) ")	
					.append("from t4636MovXCat m,t4752MensXMovi mm,t1276periodo p ")  
					.append("where m.cod_mov = mm.cod_mov ")
					.append("and mm.per_mov = p.periodo "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					//regimen/modalidad
					if (regimen.equals("0")) { // Regimen 276 - 728				
						SQL.append("and mm.cod_reg not in (?,?) ");
						SQL.append("and p.fcierre[7,10]||p.fcierre[4,5]||p.fcierre[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("1")){//Regimen 1057
						SQL.append("and mm.cod_reg = ? ");
						SQL.append("and p.fec_cierre_cas[7,10]||p.fec_cierre_cas[4,5]||p.fec_cierre_cas[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("2")){//Modalidad Formativa
						SQL.append("and mm.cod_reg = ? ");
						SQL.append("and p.fec_cierre_mf[7,10]||p.fec_cierre_mf[4,5]||p.fec_cierre_mf[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}					
					//periodo
					SQL.append("and mm.per_mov = ? ");
					//categoria
					SQL.append("and m.cod_cate = ? ");	
					//criterio: unidad organica o intendencia					
					if (criterio.equals("1")){//unidad organica						
						SQL.append("and mm.cod_uo = ? ");		
					}else if (criterio.equals("4")){//intendencia						
						SQL.append("and mm.cod_uo[1,2] = ? ");		
					}				
				//SQL.append("order by 1 "); 			
			
			Object o[]=null;		
			
			//regimen/modalidad
			if (regimen.equals("0")) { // Regimen 276 - 728	
				if(criterio.equals("1") || criterio.equals("4")){//uuoo o intendencia
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),periodo,categoria,valor};
				}else if(criterio.equals("3")){//institucional
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),periodo,categoria};
				}				
			}else if(regimen.equals("1")){//Regimen 1057
				if(criterio.equals("1") || criterio.equals("4")){//uuoo o intendencia
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),new FechaBean().getFormatDate("yyyyMMdd"),periodo,categoria,valor};
				}else if(criterio.equals("3")){//institucional
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),new FechaBean().getFormatDate("yyyyMMdd"),periodo,categoria};
				}				
			}else if(regimen.equals("2")){//Modalidad Formativa
				if(criterio.equals("1") || criterio.equals("4")){//uuoo o intendencia
					o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),periodo,categoria,valor};
				}else if(criterio.equals("3")){//institucional
					o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),periodo,categoria};
				}				
			}						
			setIsolationLevel(T4752DAO.TX_READ_UNCOMMITTED);
			movimientos = executeQuery(datasource, SQL.toString(), o);
			setIsolationLevel(-1);			
			if(log.isDebugEnabled()) log.debug("T4752DAO findMovimientos_rptDiaXMov_ByUUOOByInteByInst - movimientos:  " + movimientos);			
		
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta de metodo: findMovimientos_rptDiaXMov_ByUUOOByInteByInst");
		}
		return movimientos;		
	}
	
	/**
	 * metodo findHorasDiasByMovimiento_rptDiaXMov_ByUUOOByInteByInst : encargado de buscar el total de horas y dias de los movimientos por periodo de acuerdo a regimen, periodo, criterio(unidad organica, intendencia o institucional), valor(solo unidad organica e intendencia), categoria y movimiento	
	 * @param Map params (String regimen,String periodo,String categoria,String movimiento,String criterio,String valor)	 
	 * @return Map
	 * @exception DAOException
	 * */
	public Map findHorasDiasByMovimiento_rptDiaXMov_ByUUOOByInteByInst(Map params) throws DAOException {				
		
		if(log.isDebugEnabled()) log.debug("T4752DAO findHorasDiasByMovimiento_rptDiaXMov_ByUUOOByInteByInst - params: " + params);		
		Map mMovimiento = null;		
		StringBuffer SQL = new StringBuffer("");
		
		try{					
			String regimen = params.get("regimen").toString(); //0{276 - 728}, 1{1057} y 2{Formativas}			
			String periodo = params.get("periodo").toString();//200805
			String categoria = params.get("categoria").toString();//009 {otros}
			String movimiento = params.get("movimiento").toString();//38 {licencia por feriado compensable}
			String criterio = params.get("criterio").toString();//1=unidad organica; 4=intendencia; 3=institucional 
			String valor = params.get("valor").toString();//2B2100 (unidad organica) y 2B0000 (intendencia)	
			if (criterio.equals("4")){//intendencia				
				valor = valor.substring(0,2);//2B	
			}	
					
			SQL.append("select mm.cod_mov, ")	
					.append("sum(mm.mto_horas) as horas, ")  
					.append("sum(mm.mto_dias) as dias ")
					.append("from t4636MovXCat m,t4752MensXMovi mm,t1276periodo p ")
					.append("where m.cod_mov = mm.cod_mov ")
					.append("and mm.per_mov = p.periodo "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					//regimen/modalidad
					if (regimen.equals("0")) { // Regimen 276 - 728				
						SQL.append("and mm.cod_reg not in (?,?) ");
						SQL.append("and p.fcierre[7,10]||p.fcierre[4,5]||p.fcierre[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("1")){//Regimen 1057
						SQL.append("and mm.cod_reg = ? ");
						SQL.append("and p.fec_cierre_cas[7,10]||p.fec_cierre_cas[4,5]||p.fec_cierre_cas[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("2")){//Modalidad Formativa
						SQL.append("and mm.cod_reg = ? ");
						SQL.append("and p.fec_cierre_mf[7,10]||p.fec_cierre_mf[4,5]||p.fec_cierre_mf[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}					
					//periodo
					SQL.append("and mm.per_mov = ? ");
					//categoria
					SQL.append("and m.cod_cate = ? ");	
					//movimiento
					SQL.append("and m.cod_mov = ? ");
					//criterio: unidad organica o intendencia					
					if (criterio.equals("1")){//unidad organica						
						SQL.append("and mm.cod_uo = ? ");		
					}else if (criterio.equals("4")){//intendencia						
						SQL.append("and mm.cod_uo[1,2] = ? ");		
					}				
				SQL.append("group by 1 "); 			
			
			Object o[]=null;		
			
			//regimen/modalidad
			if (regimen.equals("0")) { // Regimen 276 - 728	
				if(criterio.equals("1") || criterio.equals("4")){//uuoo o intendencia
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),periodo,categoria,movimiento,valor};
				}else if(criterio.equals("3")){//institucional
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),periodo,categoria,movimiento};
				}				
			}else if(regimen.equals("1")){//Regimen 1057
				if(criterio.equals("1") || criterio.equals("4")){//uuoo o intendencia
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),new FechaBean().getFormatDate("yyyyMMdd"),periodo,categoria,movimiento,valor};
				}else if(criterio.equals("3")){//institucional
					o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),new FechaBean().getFormatDate("yyyyMMdd"),periodo,categoria,movimiento};
				}				
			}else if(regimen.equals("2")){//Modalidad Formativa
				if(criterio.equals("1") || criterio.equals("4")){//uuoo o intendencia
					o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),periodo,categoria,movimiento,valor};
				}else if(criterio.equals("3")){//institucional
					o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),periodo,categoria,movimiento};
				}				
			}						
			setIsolationLevel(T4752DAO.TX_READ_UNCOMMITTED);
			mMovimiento = executeQueryUniqueResult(datasource, SQL.toString(), o);
			setIsolationLevel(-1);			
			if(log.isDebugEnabled()) log.debug("T4752DAO findHorasDiasByMovimiento_rptDiaXMov_ByUUOOByInteByInst - mMovimiento:  " + mMovimiento);			
		
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta de metodo: findHorasDiasByMovimiento_rptDiaXMov_ByUUOOByInteByInst");
		}
		return mMovimiento;		
	}
	//fin consultas reporte diario por movimiento
	
}
