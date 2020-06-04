package pe.gob.sunat.rrhh.asistencia.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.bean.MensajeBean;
import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.Propiedades;
import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.rrhh.dao.T02DAO;
import pe.gob.sunat.sp.asistencia.bean.BeanReporte;
import pe.gob.sunat.utils.Constantes;

/** 
 * 
 * Clase       : T4754DAO 
 * Descripcion : Clase encargada de administrar los datos de la tabla t4754DiarXMovi (Tabla que almacena la suma de horas y dias de los registros agrupados por regimen, unidad organica, periodo, fecha y movimiento de la tabla t4707Asistencia_h)
 * Proyecto    : ASISTENCIA 
 * Autor       : ICAPUNAY
 * Fecha       : 10-ENERO-2012 
 * 
 * */

public class T4754DAO extends DAOAbstract{
	
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
	public T4754DAO(Object datasource) {
		if (datasource instanceof DataSource)
		  this.datasource = (DataSource)datasource;
		else if (datasource instanceof String)
		  this.datasource = getDataSource((String)datasource);
		else
		  throw new DAOException(this, "Datasource no valido");
	}
	
	//consultas reporte diario por movimiento
	/**
	 * metodo findCabecera_rptDiaXMov_ByUUOOByInteByInst : encargado de buscar el total de horas y dias de los movimientos por fechas de un periodo de un colaborador de acuerdo a regimen, periodo, criterio(unidad organica, intendencia o institucional), valor(solo unidad organica e intendencia) y categoria	
	 * @param Map params (String regimen,String periodo,String categoria,String criterio,String valor,HashMap seguridad)	 
	 * @return List
	 * @exception DAOException
	 * */
	public List findCabecera_rptDiaXMov_ByUUOOByInteByInst(Map params) throws DAOException {				
		
		if(log.isDebugEnabled()) log.debug("T4754DAO findCabecera_rptDiaXMov_ByUUOOByInteByInst - params: " + params);	
		List lFechas = null;		
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
			HashMap seguridad = (HashMap)params.get("seguridad");
					
			SQL.append("select m.cod_cate,dm.fec_mov, ")	
					.append("sum(dm.mto_horas) as horas, ")  
					.append("sum(dm.mto_dias) as dias ")
					.append("from t4636MovXCat m,t4754DiarXMovi dm,t1276periodo p ")
					.append("where m.cod_mov = dm.cod_mov ")
					.append("and dm.per_mov = p.periodo "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					//regimen/modalidad
					if (regimen.equals("0")) { // Regimen 276 - 728				
						SQL.append("and dm.cod_reg not in (?,?) ");
						SQL.append("and p.fcierre[7,10]||p.fcierre[4,5]||p.fcierre[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("1")){//Regimen 1057
						SQL.append("and dm.cod_reg = ? ");
						SQL.append("and p.fec_cierre_cas[7,10]||p.fec_cierre_cas[4,5]||p.fec_cierre_cas[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("2")){//Modalidad Formativa
						SQL.append("and dm.cod_reg = ? ");
						SQL.append("and p.fec_cierre_mf[7,10]||p.fec_cierre_mf[4,5]||p.fec_cierre_mf[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}					
					//periodo
					SQL.append("and dm.per_mov = ? ");
					//categoria
					SQL.append("and m.cod_cate = ? ");	
					//criterio: unidad organica o intendencia
					if (criterio.equals("1")){//unidad organica						
						SQL.append("and dm.cod_uo = ? ");		
					}else if (criterio.equals("4")){//intendencia						
						SQL.append("and dm.cod_uo[1,2] = ? ");		
					}														
					//criterios de visibilidad
					if (seguridad != null) {					
						HashMap roles = (HashMap) seguridad.get("roles");
						String uoSeg = (String) seguridad.get("uoSeg");	
						String uoAO = (String) seguridad.get("uoAO");//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
						
						if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
							SQL.append("and 1=1 ");
						} else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null) {
							SQL.append("and ((dm.cod_uo like '").append(uoSeg).append( "') ");
							
							SQL.append("or (dm.cod_uo in ")
							.append("(select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '")
							.append(uoAO).append( "'))) ");
						} else if (//roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null ||
								 roles.get(Constantes.ROL_SECRETARIA) != null
								|| roles.get(Constantes.ROL_JEFE) != null) {
							SQL.append("and dm.cod_uo like '").append(uoSeg).append( "' ");
						} else {
							SQL.append("and 1=2 ");
						}
					}								
				SQL.append("group by 1,2 "); 	
				//SQL.append("order by 2 "); 			
			
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
			setIsolationLevel(T4754DAO.TX_READ_UNCOMMITTED);
			lFechas = executeQuery(datasource, SQL.toString(), o);
			setIsolationLevel(-1);			
			if(log.isDebugEnabled()) log.debug("T4754DAO findCabecera_rptDiaXMov_ByUUOOByInteByInst - lFechas:  " + lFechas);			
		
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta de metodo: findCabecera_rptDiaXMov_ByUUOOByInteByInst");
		}
		return lFechas;		
	}
	
	/**
	 * metodo findDetalle_rptDiaXMov_ByUUOOByInteByInst : encargado de buscar el total de horas y dias de los movimientos por fechas de un periodo de acuerdo a regimen, periodo, criterio(unidad organica, intendencia o institucional), valor(solo unidad organica e intendencia) y categoria	
	 * @param Map params (String regimen,String periodo,String categoria,String criterio,String valor)	
	 * @return List
	 * @exception DAOException
	 * */
	public List findDetalle_rptDiaXMov_ByUUOOByInteByInst(Map params) throws DAOException {				
		
		if(log.isDebugEnabled()) log.debug("T4754DAO findDetalle_rptDiaXMov_ByUUOOByInteByInst - params: " + params);	
		List lMovimientos = null;		
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
					
			SQL.append("select dm.cod_mov,dm.fec_mov, ")	
					.append("sum(dm.mto_horas) as horas, ")  
					.append("sum(dm.mto_dias) as dias ")
					.append("from t4636MovXCat m,t4754DiarXMovi dm,t1276periodo p ")
					.append("where m.cod_mov = dm.cod_mov ")
					.append("and dm.per_mov = p.periodo "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					//regimen/modalidad
					if (regimen.equals("0")) { // Regimen 276 - 728				
						SQL.append("and dm.cod_reg not in (?,?) ");
						SQL.append("and p.fcierre[7,10]||p.fcierre[4,5]||p.fcierre[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("1")){//Regimen 1057
						SQL.append("and dm.cod_reg = ? ");
						SQL.append("and p.fec_cierre_cas[7,10]||p.fec_cierre_cas[4,5]||p.fec_cierre_cas[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("2")){//Modalidad Formativa
						SQL.append("and dm.cod_reg = ? ");
						SQL.append("and p.fec_cierre_mf[7,10]||p.fec_cierre_mf[4,5]||p.fec_cierre_mf[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}					
					//periodo
					SQL.append("and dm.per_mov = ? ");
					//categoria
					SQL.append("and m.cod_cate = ? ");	
					//criterio: unidad organica o intendencia					
					if (criterio.equals("1")){//unidad organica						
						SQL.append("and dm.cod_uo = ? ");		
					}else if (criterio.equals("4")){//intendencia						
						SQL.append("and dm.cod_uo[1,2] = ? ");		
					}							
				SQL.append("group by 1,2 "); 	
				//SQL.append("order by 1,2 "); 			
			
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
			setIsolationLevel(T4754DAO.TX_READ_UNCOMMITTED);
			lMovimientos = executeQuery(datasource, SQL.toString(), o);
			setIsolationLevel(-1);			
			if(log.isDebugEnabled()) log.debug("T4754DAO findDetalle_rptDiaXMov_ByUUOOByInteByInst - lMovimientos:  " + lMovimientos);			
		
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta de metodo: findDetalle_rptDiaXMov_ByUUOOByInteByInst");
		}
		return lMovimientos;		
	}
	//fin consultas reporte diario por movimiento
		
	
}
