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
 * Clase       : T4707DAO 
 * Descripcion : Clase encargada de administrar los datos de la tabla t4707Asistencia_h (Tabla que almacena los valores en horas y dias de cada movimiento de la tabla t1454Asistencia_d desde el periodo cerrado 200801 hasta los periodos cerrados actualmente)
 * Proyecto    : ASISTENCIA 
 * Autor       : ICAPUNAY
 * Fecha       : 10-ENERO-2012 
 * 
 * */

public class T4707DAO extends DAOAbstract{
	
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
	public T4707DAO(Object datasource) {
		if (datasource instanceof DataSource)
		  this.datasource = (DataSource)datasource;
		else if (datasource instanceof String)
		  this.datasource = getDataSource((String)datasource);
		else
		  throw new DAOException(this, "Datasource no valido");
	}
	
	//consultas reporte mensual por movimiento
	/**
	 * metodo findCabecera_rptMenXMov_ByRegistro : encargado de buscar el total de horas y dias de los movimientos por periodos de un colaborador de acuerdo a regimen, anio, colaborador y categoria	
	 * @param Map params (String regimen,String cod_pers,String anio,String categoria,HashMap seguridad)	 
	 * @return List
	 * @exception DAOException
	 * */
	public List findCabecera_rptMenXMov_ByRegistro(Map params) throws DAOException {				
		
		if(log.isDebugEnabled()) log.debug("T4707DAO findCabecera_rptMenXMov_ByRegistro - params: " + params);	
		List periodos = null;		
		StringBuffer SQL = new StringBuffer("");
		
		try{					
			String regimen = params.get("regimen").toString(); //0{276 - 728}, 1{1057} y 2{Formativas}			
			String anio = params.get("anio").toString();//2008
			String cod_pers = params.get("valor").toString();//0091
			String categoria = params.get("categoria").toString();//009 {otros}
			HashMap seguridad = (HashMap)params.get("seguridad");
					
			SQL.append("select h.per_mov, ")	
					.append("sum(h.num_horas) as horas,sum(h.num_dias) as dias ")  
					//.append("from t4635categoria c,t4636MovXCat m,t4707asistencia_h h ")//ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					.append("from t4635categoria c,t4636MovXCat m,t4707asistencia_h h,t1276periodo p ")//ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					.append("where m.cod_mov = h.cod_mov ")
					.append("and m.cod_cate = c.cod_cate  ")
					.append("and h.per_mov = p.periodo "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					//regimen/modalidad
					if (regimen.equals("0")) { // Regimen 276 - 728				
						SQL.append("and h.cod_reg not in (?,?) ");
						SQL.append("and p.fcierre[7,10]||p.fcierre[4,5]||p.fcierre[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("1")){//Regimen 1057
						SQL.append("and h.cod_reg = ? ");
						SQL.append("and p.fec_cierre_cas[7,10]||p.fec_cierre_cas[4,5]||p.fec_cierre_cas[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("2")){//Modalidad Formativa
						SQL.append("and h.cod_reg = ? ");
						SQL.append("and p.fec_cierre_mf[7,10]||p.fec_cierre_mf[4,5]||p.fec_cierre_mf[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}					
					//anio
					SQL.append("and h.per_mov[1,4] = ? ");					
					//colaborador
					SQL.append("and h.cod_pers = ? ");					
					//categoria
					SQL.append("and m.cod_cate = ? ");					
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
							SQL.append("and ((h.cod_uo like '").append(uoSeg).append( "') ");
							SQL.append("or (h.cod_uo in (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '").append(uoAO).append( "'))) ");
						}
						//else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null || //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
						else if (
						//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
								roles.get(Constantes.ROL_SECRETARIA) != null
								|| roles.get(Constantes.ROL_JEFE) != null) {
							SQL.append("and h.cod_uo like '").append(uoSeg).append( "' ");
						} else {
							SQL.append("and 1=2 ");
						}
					}								
				SQL.append("group by 1 "); 	
				//SQL.append("order by 1 asc"); 			
			
			Object o[]=null;		
			
			//regimen/modalidad
			if (regimen.equals("0")) { // Regimen 276 - 728				
				o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
						//constantes.leePropiedad("CODREL_MOD_FORMATIVA"),anio,cod_pers,categoria,new FechaBean().getFormatDate("dd/MM/yyyy")};//ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
						constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,cod_pers,categoria};//ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
			}else if(regimen.equals("1")){//Regimen 1057
				//o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),anio,cod_pers,categoria,new FechaBean().getFormatDate("dd/MM/yyyy")};//ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
				o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),new FechaBean().getFormatDate("yyyyMMdd"),anio,cod_pers,categoria};//ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
			}else if(regimen.equals("2")){//Modalidad Formativa
				//o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),anio,cod_pers,categoria,new FechaBean().getFormatDate("dd/MM/yyyy")};//ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
				o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,cod_pers,categoria};//ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
			}						
			setIsolationLevel(T4707DAO.TX_READ_UNCOMMITTED);
			periodos = executeQuery(datasource, SQL.toString(), o);
			setIsolationLevel(-1);			
			if(log.isDebugEnabled()) log.debug("T4707DAO findCabecera_rptMenXMov_ByRegistro - periodos:  " + periodos);			
		
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta de metodo: findCabecera_rptMenXMov_ByRegistro");
		}
		return periodos;		
	}
	
	
	/**
	 * metodo findDetalle_rptMenXMov_ByRegistro : encargado de buscar los detalles de movimientos por periodo del total de horas y dias de los movimientos de un colaborador de acuerdo a regimen, anio, colaborador y categoria	
	 * @param Map params (String regimen,String cod_pers,String anio,String categoria)	
	 * @return List
	 * @exception DAOException
	 * */
	public List findDetalle_rptMenXMov_ByRegistro(Map params) throws DAOException {				
		
		if(log.isDebugEnabled()) log.debug("T4707DAO findDetalle_rptMenXMov_ByRegistro - params: " + params);	
		List lista = null;		
		StringBuffer SQL = new StringBuffer("");
		
		try{					
			String regimen = params.get("regimen").toString(); //0{276 - 728}, 1{1057} y 2{Formativas}			
			String cod_pers = params.get("valor").toString();//0091
			String anio = params.get("anio").toString();//2008
			String categoria = params.get("categoria").toString();//009 {otros}
					
			SQL.append("select h.cod_mov,tm.descrip,h.per_mov, ")	
					.append("sum(h.num_horas) as horas,sum(h.num_dias) as dias ")  
					//.append("from t4636MovXCat m,t1279tipo_mov tm,t4707asistencia_h h ")
					.append("from t4636MovXCat m,t1279tipo_mov tm,t4707asistencia_h h,t1276periodo p ")
					.append("where m.cod_mov = h.cod_mov ")
					.append("and h.cod_mov = tm.mov ")
					.append("and h.per_mov = p.periodo "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					//regimen/modalidad
					if (regimen.equals("0")) { // Regimen 276 - 728				
						SQL.append("and h.cod_reg not in (?,?) ");
						SQL.append("and p.fcierre[7,10]||p.fcierre[4,5]||p.fcierre[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("1")){//Regimen 1057
						SQL.append("and h.cod_reg = ? ");
						SQL.append("and p.fec_cierre_cas[7,10]||p.fec_cierre_cas[4,5]||p.fec_cierre_cas[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("2")){//Modalidad Formativa
						SQL.append("and h.cod_reg = ? ");
						SQL.append("and p.fec_cierre_mf[7,10]||p.fec_cierre_mf[4,5]||p.fec_cierre_mf[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}					
					//anio
					SQL.append("and h.per_mov[1,4] = ? ");					
					//colaborador
					SQL.append("and h.cod_pers = ? ");					
					//categoria
					SQL.append("and m.cod_cate = ? ");					
							
				SQL.append("group by 1,2,3 "); 	
				//SQL.append("order by 1,3 "); 			
			
			Object o[]=null;			
			//regimen/modalidad
			if (regimen.equals("0")) { // Regimen 276 - 728				
				o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
						constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,cod_pers,categoria};
			}else if(regimen.equals("1")){//Regimen 1057
				o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),new FechaBean().getFormatDate("yyyyMMdd"),anio,cod_pers,categoria};
			}else if(regimen.equals("2")){//Modalidad Formativa
				o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,cod_pers,categoria};
			}						
			setIsolationLevel(T4707DAO.TX_READ_UNCOMMITTED);
			lista = executeQuery(datasource, SQL.toString(), o);
			setIsolationLevel(-1);
			
			if(log.isDebugEnabled()) log.debug("T4707DAO findDetalle_rptMenXMov_ByRegistro - lista:  " + lista);			
		
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta de metodo: findDetalle_rptMenXMov_ByRegistro");
		}
		return lista;		
	}
	
	/**
	 * metodo findMovimientos_rptMenXMov_ByRegistro : encargado de buscar los movimientos involucrados de un colaborador de acuerdo a regimen, anio, colaborador y categoria	
	 * @param Map params (String regimen,String cod_pers,String anio,String categoria)	
	 * @return List
	 * @exception DAOException
	 * */
	public List findMovimientos_rptMenXMov_ByRegistro(Map params) throws DAOException {				
		
		if(log.isDebugEnabled()) log.debug("T4707DAO findMovimientos_rptMenXMov_ByRegistro - params: " + params);
		List movimientos = null;		
		StringBuffer SQL = new StringBuffer("");
		
		try{					
			String regimen = params.get("regimen").toString(); //0{276 - 728}, 1{1057} y 2{Formativas}			
			String cod_pers = params.get("valor").toString();//0091
			String anio = params.get("anio").toString();//2008	
			String categoria = params.get("categoria").toString();//009 {otros}
					
			SQL.append("select distinct(h.cod_mov) ")	
					.append("from t4636MovXCat m,t4707asistencia_h h,t1276periodo p ")  
					.append("where m.cod_mov = h.cod_mov ")
					.append("and h.per_mov = p.periodo "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					//regimen/modalidad
					if (regimen.equals("0")) { // Regimen 276 - 728				
						SQL.append("and h.cod_reg not in (?,?) ");
						SQL.append("and p.fcierre[7,10]||p.fcierre[4,5]||p.fcierre[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("1")){//Regimen 1057
						SQL.append("and h.cod_reg = ? ");
						SQL.append("and p.fec_cierre_cas[7,10]||p.fec_cierre_cas[4,5]||p.fec_cierre_cas[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("2")){//Modalidad Formativa
						SQL.append("and h.cod_reg = ? ");
						SQL.append("and p.fec_cierre_mf[7,10]||p.fec_cierre_mf[4,5]||p.fec_cierre_mf[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}					
					//anio
					SQL.append("and h.per_mov[1,4] = ? ");					
					//colaborador
					SQL.append("and h.cod_pers = ? ");					
					//categoria
					SQL.append("and m.cod_cate = ? ");								
				//SQL.append("order by 1 "); 			
			
			Object o[]=null;			
			//regimen/modalidad
			if (regimen.equals("0")) { // Regimen 276 - 728				
				o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
						constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,cod_pers,categoria};
			}else if(regimen.equals("1")){//Regimen 1057
				o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),new FechaBean().getFormatDate("yyyyMMdd"),anio,cod_pers,categoria};
			}else if(regimen.equals("2")){//Modalidad Formativa
				o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,cod_pers,categoria};
			}						
			setIsolationLevel(T4707DAO.TX_READ_UNCOMMITTED);
			movimientos = executeQuery(datasource, SQL.toString(), o);
			setIsolationLevel(-1);
			
			if(log.isDebugEnabled()) log.debug("T4707DAO findMovimientos_rptMenXMov_ByRegistro - movimientos:  " + movimientos);			
		
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta de metodo: findMovimientos_rptMenXMov_ByRegistro");
		}
		return movimientos;		
	}
	
	/**
	 * metodo findHorasDiasByMovimiento_rptMenXMov_ByRegistro : encargado de buscar el total de horas y dias de los movimientos anuales de un colaborador de acuerdo a regimen, anio, colaborador, categoria y movimiento	
	 * @param Map params (String regimen,String cod_pers,String anio,String categoria,String movimiento)	 
	 * @return Map
	 * @exception DAOException
	 * */
	public Map findHorasDiasByMovimiento_rptMenXMov_ByRegistro(Map params) throws DAOException {				
		
		if(log.isDebugEnabled()) log.debug("T4707DAO findHorasDiasByMovimiento_rptMenXMov_ByRegistro - params: " + params);		
		Map mMovimiento = null;		
		StringBuffer SQL = new StringBuffer("");
		
		try{					
			String regimen = params.get("regimen").toString(); //0{276 - 728}, 1{1057} y 2{Formativas}			
			String cod_pers = params.get("valor").toString();//0091
			String anio = params.get("anio").toString();//2008
			String categoria = params.get("categoria").toString();//009 {otros}
			String movimiento = params.get("movimiento").toString();//38 {licencia por feriado compensable}
					
			SQL.append("select h.cod_mov, ")	
					.append("sum(h.num_horas) as horas,sum(h.num_dias) as dias ")  
					.append("from t4636MovXCat m,t4707asistencia_h h,t1276periodo p ")
					.append("where m.cod_mov = h.cod_mov ")
					.append("and h.per_mov = p.periodo "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					//regimen/modalidad
					if (regimen.equals("0")) { // Regimen 276 - 728				
						SQL.append("and h.cod_reg not in (?,?) ");
						SQL.append("and p.fcierre[7,10]||p.fcierre[4,5]||p.fcierre[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("1")){//Regimen 1057
						SQL.append("and h.cod_reg = ? ");
						SQL.append("and p.fec_cierre_cas[7,10]||p.fec_cierre_cas[4,5]||p.fec_cierre_cas[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("2")){//Modalidad Formativa
						SQL.append("and h.cod_reg = ? ");
						SQL.append("and p.fec_cierre_mf[7,10]||p.fec_cierre_mf[4,5]||p.fec_cierre_mf[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}					
					//anio
					SQL.append("and h.per_mov[1,4] = ? ");					
					//colaborador
					SQL.append("and h.cod_pers = ? ");					
					//categoria
					SQL.append("and m.cod_cate = ? ");
					//movimiento
					SQL.append("and h.cod_mov = ? ");	
									
				SQL.append("group by 1 ");			 			
			
			Object o[]=null;		
			
			//regimen/modalidad
			if (regimen.equals("0")) { // Regimen 276 - 728				
				o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
						constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,cod_pers,categoria,movimiento};
			}else if(regimen.equals("1")){//Regimen 1057
				o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),new FechaBean().getFormatDate("yyyyMMdd"),anio,cod_pers,categoria,movimiento};
			}else if(regimen.equals("2")){//Modalidad Formativa
				o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,cod_pers,categoria,movimiento};
			}						
			setIsolationLevel(T4707DAO.TX_READ_UNCOMMITTED);
			mMovimiento = executeQueryUniqueResult(datasource, SQL.toString(), o);
			setIsolationLevel(-1);			
			if(log.isDebugEnabled()) log.debug("T4707DAO findHorasDiasByMovimiento_rptMenXMov_ByRegistro - mMovimiento:  " + mMovimiento);			
		
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta de metodo: findHorasDiasByMovimiento_rptMenXMov_ByRegistro");
		}
		return mMovimiento;		
	}
	//fin consultas reporte mensual por movimiento
	
	//consultas reporte diario por movimiento
	/**
	 * metodo findCabecera_rptDiaXMov_ByRegistro : encargado de buscar el total de horas y dias de los movimientos por fechas de un periodo de un colaborador de acuerdo a regimen, periodo, colaborador y categoria	
	 * @param Map params (String regimen,String periodo,String categoria,String colaborador,HashMap seguridad)	 
	 * @return List
	 * @exception DAOException
	 * */
	public List findCabecera_rptDiaXMov_ByRegistro(Map params) throws DAOException {				
		
		if(log.isDebugEnabled()) log.debug("T4707DAO findCabecera_rptDiaXMov_ByRegistro - params: " + params);	
		List lFechas = null;		
		StringBuffer SQL = new StringBuffer("");
		
		try{					
			String regimen = params.get("regimen").toString(); //0{276 - 728}, 1{1057} y 2{Formativas}			
			String periodo = params.get("periodo").toString();//200805
			String categoria = params.get("categoria").toString();//009 {otros}			
			String colaborador = params.get("valor").toString();//0091 (registro)			
			HashMap seguridad = (HashMap)params.get("seguridad");
					
			SQL.append("select m.cod_cate,h.fec_mov, ")	
					.append("sum(h.num_horas) as horas, ")  
					.append("sum(h.num_dias) as dias ")
					.append("from t4636MovXCat m,t4707asistencia_h h,t1276periodo p ")
					.append("where m.cod_mov = h.cod_mov ")
					.append("and h.per_mov = p.periodo "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					//regimen/modalidad
					if (regimen.equals("0")) { // Regimen 276 - 728				
						SQL.append("and h.cod_reg not in (?,?) ");
						SQL.append("and p.fcierre[7,10]||p.fcierre[4,5]||p.fcierre[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("1")){//Regimen 1057
						SQL.append("and h.cod_reg = ? ");
						SQL.append("and p.fec_cierre_cas[7,10]||p.fec_cierre_cas[4,5]||p.fec_cierre_cas[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("2")){//Modalidad Formativa
						SQL.append("and h.cod_reg = ? ");
						SQL.append("and p.fec_cierre_mf[7,10]||p.fec_cierre_mf[4,5]||p.fec_cierre_mf[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}					
					//periodo
					SQL.append("and h.per_mov = ? ");
					//categoria
					SQL.append("and m.cod_cate = ? ");	
					//colaborador
					SQL.append("and h.cod_pers = ? ");																		
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
							SQL.append("and ((h.cod_uo like '").append(uoSeg).append( "') ");
							SQL.append("or (h.cod_uo in (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '").append(uoAO).append( "'))) ");
						}
						//else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null || //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
						else if (
						//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
								roles.get(Constantes.ROL_SECRETARIA) != null
								|| roles.get(Constantes.ROL_JEFE) != null) {
							SQL.append("and h.cod_uo like '").append(uoSeg).append( "' ");
						} else {
							SQL.append("and 1=2 ");
						}
					}								
				SQL.append("group by 1,2 "); 	
				//SQL.append("order by 2 "); 			
			
			Object o[]=null;		
			
			//regimen/modalidad
			if (regimen.equals("0")) { // Regimen 276 - 728	
				o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),periodo,categoria,colaborador};							
			}else if(regimen.equals("1")){//Regimen 1057				
				o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),new FechaBean().getFormatDate("yyyyMMdd"),periodo,categoria,colaborador};								
			}else if(regimen.equals("2")){//Modalidad Formativa				
				o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),periodo,categoria,colaborador};								
			}						
			setIsolationLevel(T4707DAO.TX_READ_UNCOMMITTED);
			lFechas = executeQuery(datasource, SQL.toString(), o);
			setIsolationLevel(-1);			
			if(log.isDebugEnabled()) log.debug("T4707DAO findCabecera_rptDiaXMov_ByRegistro - lFechas:  " + lFechas);			
		
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta de metodo: findCabecera_rptDiaXMov_ByRegistro");
		}
		return lFechas;		
	}
	
	/**
	 * metodo findDetalle_rptDiaXMov_ByRegistro : encargado de buscar el total de horas y dias de los movimientos por fechas de un periodo de un colaborador de acuerdo a regimen, periodo, colaborador y categoria	
	 * @param Map params (String regimen,String periodo,String categoria,String colaborador)	
	 * @return List
	 * @exception DAOException
	 * */
	public List findDetalle_rptDiaXMov_ByRegistro(Map params) throws DAOException {				
		
		if(log.isDebugEnabled()) log.debug("T4707DAO findDetalle_rptDiaXMov_ByRegistro - params: " + params);	
		List lMovimientos = null;		
		StringBuffer SQL = new StringBuffer("");
		
		try{					
			String regimen = params.get("regimen").toString(); //0{276 - 728}, 1{1057} y 2{Formativas}			
			String periodo = params.get("periodo").toString();//200805
			String categoria = params.get("categoria").toString();//009 {otros}			 
			String colaborador = params.get("valor").toString();//0091 (registro)			
					
			SQL.append("select h.cod_mov,h.fec_mov, ")	
					.append("sum(h.num_horas) as horas, ")  
					.append("sum(h.num_dias) as dias ")
					.append("from t4636MovXCat m,t4707asistencia_h h,t1276periodo p ")
					.append("where m.cod_mov = h.cod_mov ")
					.append("and h.per_mov = p.periodo "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					//regimen/modalidad
					if (regimen.equals("0")) { // Regimen 276 - 728				
						SQL.append("and h.cod_reg not in (?,?) ");
						SQL.append("and p.fcierre[7,10]||p.fcierre[4,5]||p.fcierre[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("1")){//Regimen 1057
						SQL.append("and h.cod_reg = ? ");
						SQL.append("and p.fec_cierre_cas[7,10]||p.fec_cierre_cas[4,5]||p.fec_cierre_cas[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("2")){//Modalidad Formativa
						SQL.append("and h.cod_reg = ? ");
						SQL.append("and p.fec_cierre_mf[7,10]||p.fec_cierre_mf[4,5]||p.fec_cierre_mf[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}					
					//periodo
					SQL.append("and h.per_mov = ? ");
					//categoria
					SQL.append("and m.cod_cate = ? ");	
					//colaborador
					SQL.append("and h.cod_pers = ? ");											
				SQL.append("group by 1,2 "); 	
				//SQL.append("order by 1,2 "); 			
			
			Object o[]=null;		
			
			//regimen/modalidad
			if (regimen.equals("0")) { // Regimen 276 - 728				
				o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),periodo,categoria,colaborador};							
			}else if(regimen.equals("1")){//Regimen 1057				
				o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),new FechaBean().getFormatDate("yyyyMMdd"),periodo,categoria,colaborador};								
			}else if(regimen.equals("2")){//Modalidad Formativa				
				o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),periodo,categoria,colaborador};							
			}						
			setIsolationLevel(T4707DAO.TX_READ_UNCOMMITTED);
			lMovimientos = executeQuery(datasource, SQL.toString(), o);
			setIsolationLevel(-1);			
			if(log.isDebugEnabled()) log.debug("T4707DAO findDetalle_rptDiaXMov_ByRegistro - lMovimientos:  " + lMovimientos);			
		
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta de metodo: findDetalle_rptDiaXMov_ByRegistro");
		}
		return lMovimientos;		
	}
	
	/**
	 * metodo findMovimientos_rptDiaXMov_ByRegistro : encargado de buscar los movimientos involucrados de un colaborador de acuerdo a regimen, periodo, colaborador y categoria	
	 * @param Map params (String regimen,String periodo,String categoria,String colaborador)	
	 * @return List
	 * @exception DAOException
	 * */
	public List findMovimientos_rptDiaXMov_ByRegistro(Map params) throws DAOException {				
		
		if(log.isDebugEnabled()) log.debug("T4707DAO findMovimientos_rptDiaXMov_ByRegistro - params: " + params);
		List movimientos = null;		
		StringBuffer SQL = new StringBuffer("");
		
		try{					
			String regimen = params.get("regimen").toString(); //0{276 - 728}, 1{1057} y 2{Formativas}			
			String periodo = params.get("periodo").toString();//200805
			String categoria = params.get("categoria").toString();//009 {otros}			 
			String colaborador = params.get("valor").toString();//0091 (registro)			
					
			SQL.append("select distinct(h.cod_mov) ")	
					.append("from t4636MovXCat m,t4707asistencia_h h,t1276periodo p ")  
					.append("where m.cod_mov = h.cod_mov ")
					.append("and h.per_mov = p.periodo "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					//regimen/modalidad
					if (regimen.equals("0")) { // Regimen 276 - 728				
						SQL.append("and h.cod_reg not in (?,?) ");
						SQL.append("and p.fcierre[7,10]||p.fcierre[4,5]||p.fcierre[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("1")){//Regimen 1057
						SQL.append("and h.cod_reg = ? ");
						SQL.append("and p.fec_cierre_cas[7,10]||p.fec_cierre_cas[4,5]||p.fec_cierre_cas[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("2")){//Modalidad Formativa
						SQL.append("and h.cod_reg = ? ");
						SQL.append("and p.fec_cierre_mf[7,10]||p.fec_cierre_mf[4,5]||p.fec_cierre_mf[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}					
					//periodo
					SQL.append("and h.per_mov = ? ");
					//categoria
					SQL.append("and m.cod_cate = ? ");	
					//colaborador
					SQL.append("and h.cod_pers = ? ");								
				//SQL.append("order by 1 "); 			
			
			Object o[]=null;		
			
			//regimen/modalidad
			if (regimen.equals("0")) { // Regimen 276 - 728				
				o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),periodo,categoria,colaborador};							
			}else if(regimen.equals("1")){//Regimen 1057				
				o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),new FechaBean().getFormatDate("yyyyMMdd"),periodo,categoria,colaborador};							
			}else if(regimen.equals("2")){//Modalidad Formativa				
				o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),periodo,categoria,colaborador};							
			}						
			setIsolationLevel(T4707DAO.TX_READ_UNCOMMITTED);
			movimientos = executeQuery(datasource, SQL.toString(), o);
			setIsolationLevel(-1);			
			if(log.isDebugEnabled()) log.debug("T4707DAO findMovimientos_rptDiaXMov_ByRegistro - movimientos:  " + movimientos);			
		
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta de metodo: findMovimientos_rptDiaXMov_ByRegistro");
		}
		return movimientos;		
	}
	
	/**
	 * metodo findHorasDiasByMovimiento_rptDiaXMov_ByRegistro : encargado de buscar el total de horas y dias de los movimientos por periodo de un colaborador de acuerdo a regimen, periodo, colaborador, categoria y movimiento	
	 * @param Map params (String regimen,String periodo,String categoria,String movimiento,String colaborador)	 
	 * @return Map
	 * @exception DAOException
	 * */
	public Map findHorasDiasByMovimiento_rptDiaXMov_ByRegistro(Map params) throws DAOException {				
		
		if(log.isDebugEnabled()) log.debug("T4707DAO findHorasDiasByMovimiento_rptDiaXMov_ByRegistro - params: " + params);		
		Map mMovimiento = null;		
		StringBuffer SQL = new StringBuffer("");
		
		try{					
			String regimen = params.get("regimen").toString(); //0{276 - 728}, 1{1057} y 2{Formativas}			
			String periodo = params.get("periodo").toString();//200805
			String categoria = params.get("categoria").toString();//009 {otros}
			String movimiento = params.get("movimiento").toString();//38 {licencia por feriado compensable}			 
			String colaborador = params.get("valor").toString();//0091 (registro)			
					
			SQL.append("select h.cod_mov, ")	
					.append("sum(h.num_horas) as horas, ")  
					.append("sum(h.num_dias) as dias ")
					.append("from t4636MovXCat m,t4707asistencia_h h,t1276periodo p ")
					.append("where m.cod_mov = h.cod_mov ")
					.append("and h.per_mov = p.periodo "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					//regimen/modalidad
					if (regimen.equals("0")) { // Regimen 276 - 728				
						SQL.append("and h.cod_reg not in (?,?) ");
						SQL.append("and p.fcierre[7,10]||p.fcierre[4,5]||p.fcierre[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("1")){//Regimen 1057
						SQL.append("and h.cod_reg = ? ");
						SQL.append("and p.fec_cierre_cas[7,10]||p.fec_cierre_cas[4,5]||p.fec_cierre_cas[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("2")){//Modalidad Formativa
						SQL.append("and h.cod_reg = ? ");
						SQL.append("and p.fec_cierre_mf[7,10]||p.fec_cierre_mf[4,5]||p.fec_cierre_mf[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}					
					//periodo
					SQL.append("and h.per_mov = ? ");
					//categoria
					SQL.append("and m.cod_cate = ? ");	
					//movimiento
					SQL.append("and m.cod_mov = ? ");
					//colaborador
					SQL.append("and h.cod_pers = ? ");								
				SQL.append("group by 1 "); 			
			
			Object o[]=null;		
			
			//regimen/modalidad
			if (regimen.equals("0")) { // Regimen 276 - 728				
				o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
							constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),periodo,categoria,movimiento,colaborador};								
			}else if(regimen.equals("1")){//Regimen 1057				
				o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),new FechaBean().getFormatDate("yyyyMMdd"),periodo,categoria,movimiento,colaborador};								
			}else if(regimen.equals("2")){//Modalidad Formativa				
				o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),periodo,categoria,movimiento,colaborador};								
			}						
			setIsolationLevel(T4707DAO.TX_READ_UNCOMMITTED);
			mMovimiento = executeQueryUniqueResult(datasource, SQL.toString(), o);
			setIsolationLevel(-1);			
			if(log.isDebugEnabled()) log.debug("T4707DAO findMovimientos_rptDiaXMov_ByRegistro - mMovimiento:  " + mMovimiento);			
		
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta de metodo: findMovimientos_rptDiaXMov_ByRegistro");
		}
		return mMovimiento;		
	}
	//fin consultas reporte diario por movimiento
	
	//consultas reporte mensual por unidad organica //un colaborador puede pertenecer a mas de 1 intendencia en un anio?
	/**
	 * metodo findIntendencias_rptMenXUo_ByRegistro : encargado de buscar la intendencia de un colaborador de acuerdo a regimen, anio y colaborador	
	 * @param Map params (String regimen,String anio,String cod_pers,HashMap seguridad)	 
	 * @return List
	 * @exception DAOException
	 * */
	public List findIntendencias_rptMenXUo_ByRegistro(Map params) throws DAOException {				
		
		if(log.isDebugEnabled()) log.debug("T4707DAO findIntendencias_rptMenXUo_ByRegistro - params: " + params);	
		List intendencias = null;		
		StringBuffer SQL = new StringBuffer("");
		
		try{					
			String regimen = params.get("regimen").toString(); //0{276 - 728}, 1{1057} y 2{Formativas}			
			String anio = params.get("anio").toString();//2008
			String cod_pers = params.get("valor").toString();//0091			
			HashMap seguridad = (HashMap)params.get("seguridad");
					
			SQL.append("select distinct(h.cod_inte),u.t12des_corta,h.cod_uo ")					
					.append("from t12uorga u,t4707asistencia_h h,t1276periodo p ")
					.append("where h.cod_inte = u.t12cod_uorga ")	
					.append("and h.per_mov = p.periodo "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					//regimen/modalidad
					if (regimen.equals("0")) { // Regimen 276 - 728				
						SQL.append("and h.cod_reg not in (?,?) ");
						SQL.append("and p.fcierre[7,10]||p.fcierre[4,5]||p.fcierre[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("1")){//Regimen 1057
						SQL.append("and h.cod_reg = ? ");
						SQL.append("and p.fec_cierre_cas[7,10]||p.fec_cierre_cas[4,5]||p.fec_cierre_cas[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("2")){//Modalidad Formativa
						SQL.append("and h.cod_reg = ? ");
						SQL.append("and p.fec_cierre_mf[7,10]||p.fec_cierre_mf[4,5]||p.fec_cierre_mf[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}					
					//anio
					SQL.append("and h.per_mov[1,4] = ? ");					
					//colaborador
					SQL.append("and h.cod_pers = ? ");												
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
							SQL.append("and ((h.cod_uo like '").append(uoSeg).append( "') ");
							SQL.append("or (h.cod_uo in (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '").append(uoAO).append( "'))) ");
						}
						//else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null || //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
						else if (
						//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
								roles.get(Constantes.ROL_SECRETARIA) != null
								|| roles.get(Constantes.ROL_JEFE) != null) {
							SQL.append("and h.cod_uo like '").append(uoSeg).append( "' ");
						} else {
							SQL.append("and 1=2 ");
						}
					}			
			Object o[]=null;		
			
			//regimen/modalidad
			if (regimen.equals("0")) { // Regimen 276 - 728				
				o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
						constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,cod_pers};
			}else if(regimen.equals("1")){//Regimen 1057
				o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),new FechaBean().getFormatDate("yyyyMMdd"),anio,cod_pers};
			}else if(regimen.equals("2")){//Modalidad Formativa
				o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,cod_pers};
			}						
			setIsolationLevel(T4707DAO.TX_READ_UNCOMMITTED);
			intendencias = executeQuery(datasource, SQL.toString(), o);
			setIsolationLevel(-1);			
			if(log.isDebugEnabled()) log.debug("T4707DAO findIntendencias_rptMenXUo_ByRegistro - intendencias:  " + intendencias);			
		
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta de metodo: findIntendencias_rptMenXUo_ByRegistro");
		}
		return intendencias;		
	}	
	
	/**
	 * metodo findCabecera_rptMenXUo_ByRegistro : encargado de buscar los valores de los indicadores por periodos de una intendencia de un colaborador de acuerdo a regimen, anio, colaborador, categoria e intendencia del colaborador	
	 * @param Map params (String regimen,String anio,String cod_pers,String categoria,String intendenciaCodPers)	 
	 * @return List
	 * @exception DAOException
	 * */
	public List findCabecera_rptMenXUo_ByRegistro(Map params) throws DAOException {				
		
		if(log.isDebugEnabled()) log.debug("T4707DAO findCabecera_rptMenXUo_ByRegistro - params: " + params);	
		List periodos = null;		
		StringBuffer SQL = new StringBuffer("");
		
		try{					
			String regimen = params.get("regimen").toString(); //0{276 - 728}, 1{1057} y 2{Formativas}			
			String anio = params.get("anio").toString();//2008
			String categoria = params.get("categoria").toString();//009 {otros}
			String intendenciaCodPers = params.get("intendencia").toString();//2B0000 {INJ}
			String cod_pers = params.get("valor").toString();//0091				
								
			SQL.append("select h.cod_inte,h.cod_pers,h.per_mov, ")	
					.append("1 as nc, sum(h.num_horas) as horas, sum(h.num_dias) as dias, ")
					.append("sum(h.num_dias) as dc ")
					.append("from t4636MovXCat m,t4707asistencia_h h,t1276periodo p ")
					.append("where m.cod_mov = h.cod_mov ")
					.append("and h.per_mov = p.periodo "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy			
					//regimen/modalidad
					if (regimen.equals("0")) { // Regimen 276 - 728				
						SQL.append("and h.cod_reg not in (?,?) ");
						SQL.append("and p.fcierre[7,10]||p.fcierre[4,5]||p.fcierre[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("1")){//Regimen 1057
						SQL.append("and h.cod_reg = ? ");
						SQL.append("and p.fec_cierre_cas[7,10]||p.fec_cierre_cas[4,5]||p.fec_cierre_cas[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("2")){//Modalidad Formativa
						SQL.append("and h.cod_reg = ? ");
						SQL.append("and p.fec_cierre_mf[7,10]||p.fec_cierre_mf[4,5]||p.fec_cierre_mf[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}					
					//anio
					SQL.append("and h.per_mov[1,4] = ? ");
					//categoria
					SQL.append("and m.cod_cate = ? ");
					//intendencia del colaborador
					SQL.append("and h.cod_inte = ? ");
					//colaborador
					SQL.append("and h.cod_pers = ? ");					
				SQL.append("group by 1,2,3 "); 	
						
			Object o[]=null;		
			
			//regimen/modalidad
			if (regimen.equals("0")) { // Regimen 276 - 728				
				o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
						constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria,intendenciaCodPers,cod_pers};
			}else if(regimen.equals("1")){//Regimen 1057
				o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria,intendenciaCodPers,cod_pers};
			}else if(regimen.equals("2")){//Modalidad Formativa
				o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria,intendenciaCodPers,cod_pers};
			}						
			setIsolationLevel(T4707DAO.TX_READ_UNCOMMITTED);
			periodos = executeQuery(datasource, SQL.toString(), o);
			setIsolationLevel(-1);			
			if(log.isDebugEnabled()) log.debug("T4707DAO findCabecera_rptMenXUo_ByRegistro - periodos:  " + periodos);			
		
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta de metodo: findCabecera_rptMenXUo_ByRegistro");
		}
		return periodos;		
	}	
	
	/**
	 * metodo findNCAnualDCAnualByInte_rptMenXUo_ByRegistro : encargado de buscar el valor de los indicadores anuales: numero de colaboradores y dias por colaborador de un colaborador de acuerdo a regimen, anio, colaborador, categoria e intendencia del colaborador	
	 * @param Map params (String regimen,String anio,String categoria,String cod_pers,String intendenciaCodPers)	 
	 * @return Map
	 * @exception DAOException
	 * */
	public Map findNCAnualDCAnualByInte_rptMenXUo_ByRegistro(Map params) throws DAOException {				
		
		if(log.isDebugEnabled()) log.debug("T4707DAO findNCAnualDCAnualByInte_rptMenXUo_ByRegistro - params: " + params);	
		Map	NcDcAnual = null;		
		StringBuffer SQL = new StringBuffer("");
		
		try{					
			String regimen = params.get("regimen").toString(); //0{276 - 728}, 1{1057} y 2{Formativas}			
			String anio = params.get("anio").toString();//2008
			String categoria = params.get("categoria").toString();//009 {otros}
			String intendenciaCodPers = params.get("intendencia").toString();//2B0000 {INJ}
			String cod_pers = params.get("valor").toString();//0091				
								
			SQL.append("select h.cod_inte, ")	
					.append("1 as nc, sum(h.num_dias) as dias ")					
					.append("from t4636MovXCat m,t4707asistencia_h h,t1276periodo p ")
					.append("where m.cod_mov = h.cod_mov ")
					.append("and h.per_mov = p.periodo "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					//regimen/modalidad
					if (regimen.equals("0")) { // Regimen 276 - 728				
						SQL.append("and h.cod_reg not in (?,?) ");
						SQL.append("and p.fcierre[7,10]||p.fcierre[4,5]||p.fcierre[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("1")){//Regimen 1057
						SQL.append("and h.cod_reg = ? ");
						SQL.append("and p.fec_cierre_cas[7,10]||p.fec_cierre_cas[4,5]||p.fec_cierre_cas[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("2")){//Modalidad Formativa
						SQL.append("and h.cod_reg = ? ");
						SQL.append("and p.fec_cierre_mf[7,10]||p.fec_cierre_mf[4,5]||p.fec_cierre_mf[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}					
					//anio
					SQL.append("and h.per_mov[1,4] = ? ");
					//categoria
					SQL.append("and m.cod_cate = ? ");
					//intendencia del colaborador
					SQL.append("and h.cod_inte = ? ");
					//colaborador
					SQL.append("and h.cod_pers = ? ");					
				SQL.append("group by 1 "); 	
						
			Object o[]=null;		
			
			//regimen/modalidad
			if (regimen.equals("0")) { // Regimen 276 - 728				
				o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
						constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria,intendenciaCodPers,cod_pers};
			}else if(regimen.equals("1")){//Regimen 1057
				o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria,intendenciaCodPers,cod_pers};
			}else if(regimen.equals("2")){//Modalidad Formativa
				o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria,intendenciaCodPers,cod_pers};
			}						
			setIsolationLevel(T4707DAO.TX_READ_UNCOMMITTED);
			NcDcAnual = executeQueryUniqueResult(datasource, SQL.toString(), o);
			setIsolationLevel(-1);			
			if(log.isDebugEnabled()) log.debug("T4707DAO findNCAnualDCAnualByInte_rptMenXUo_ByRegistro - NcDcAnual:  " + NcDcAnual);			
		
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta de metodo: findNCAnualDCAnualByInte_rptMenXUo_ByRegistro");
		}
		return NcDcAnual;		
	}	
	
	/*
	/**
	 * metodo findNCAnualDCAnualByInte_rptMenXUo_ByUUOOByInteByInst : encargado de buscar el valor de los indicadores anuales: numero de colaboradores y dias por colaborador de una intendencia de acuerdo a regimen, anio, categoria e intendencia. Adicional se envia el parametro unidad organica si el criterio es unidad organica	
	 * @param Map params (String regimen,String anio,String categoria,String criterio,String valor,String intendencia)	 
	 * @return Map
	 * @exception DAOException
	 * */
	/*public Map findNCAnualDCAnualByInte_rptMenXUo_ByUUOOByInteByInst(Map params) throws DAOException {				
		
		if(log.isDebugEnabled()) log.debug("T4707DAO findNCAnualDCAnualByInte_rptMenXUo_ByUUOOByInteByInst - params: " + params);	
		Map	NcDcAnual = null;		
		StringBuffer SQL = new StringBuffer("");
		
		try{					
			String regimen = params.get("regimen").toString(); //0{276 - 728}, 1{1057} y 2{Formativas}			
			String anio = params.get("anio").toString();//2008
			String categoria = params.get("categoria").toString();//009 {otros}
			String criterio = params.get("criterio").toString();//1=unidad organica; 4=intendencia; 3=institucional 
			String intendencia = params.get("intendencia").toString();//2B0000 {INJ}
			String cod_uo = params.get("valor").toString();//2B2100	si criterio es 1{unidad organica}			
								
			SQL.append("select h.cod_inte, ")	
					.append("count(distinct h.cod_pers) as nc, sum(h.num_dias) as dias ")					
					.append("from t4636MovXCat m,t4707asistencia_h h,t1276periodo p ")
					.append("where m.cod_mov = h.cod_mov ")
					.append("and h.per_mov = p.periodo "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy									
					//regimen/modalidad
					if (regimen.equals("0")) { // Regimen 276 - 728				
						SQL.append("and h.cod_reg not in (?,?) ");
						SQL.append("and p.fcierre[7,10]||p.fcierre[4,5]||p.fcierre[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("1")){//Regimen 1057
						SQL.append("and h.cod_reg = ? ");
						SQL.append("and p.fec_cierre_cas[7,10]||p.fec_cierre_cas[4,5]||p.fec_cierre_cas[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("2")){//Modalidad Formativa
						SQL.append("and h.cod_reg = ? ");
						SQL.append("and p.fec_cierre_mf[7,10]||p.fec_cierre_mf[4,5]||p.fec_cierre_mf[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}					
					//anio
					SQL.append("and h.per_mov[1,4] = ? ");
					//categoria
					SQL.append("and m.cod_cate = ? ");	
					//intendencia del BeanReporte
					SQL.append("and h.cod_inte = ? ");
					//criterio: unidad organica
					if (criterio.equals("1")){						
						SQL.append("and h.cod_uo = ? ");		
					}					
				SQL.append("group by 1 "); 	
						
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
			setIsolationLevel(T4707DAO.TX_READ_UNCOMMITTED);
			NcDcAnual = executeQueryUniqueResult(datasource, SQL.toString(), o);
			setIsolationLevel(-1);			
			if(log.isDebugEnabled()) log.debug("T4707DAO findNCAnualDCAnualByInte_rptMenXUo_ByUUOOByInteByInst - NcDcAnual:  " + NcDcAnual);			
		
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta de metodo: findNCAnualDCAnualByInte_rptMenXUo_ByUUOOByInteByInst");
		}
		return NcDcAnual;		
	}*/	
	
	/**
	 * metodo findDetalle_rptMenXUo_ByRegistro : encargado de buscar el detalle de uuoo por periodo con el total de horas, dias, numero de colaboradores y dias por colaborador de una intendencia seleccionada de acuerdo a regimen, anio, categoria, colaborador e intendencia	
	 * @param Map params (String regimen,String anio,String categoria,String cod_pers,String intendencia)	 
	 * @return List
	 * @exception DAOException
	 * */
	public List findDetalle_rptMenXUo_ByRegistro(Map params) throws DAOException {				
		
		if(log.isDebugEnabled()) log.debug("T4707DAO findDetalle_rptMenXUo_ByRegistro - params: " + params);	
		List lista = null;		
		StringBuffer SQL = new StringBuffer("");
		
		try{					
			String regimen = params.get("regimen").toString(); //0{276 - 728}, 1{1057} y 2{Formativas}			
			String anio = params.get("anio").toString();//2008
			String categoria = params.get("categoria").toString();//009 {otros}
			String intendencia = params.get("intendencia").toString();//2B0000 {INJ}
			String cod_pers = params.get("valor").toString();//0091			
			
			SQL.append("select h.cod_uo,h.cod_pers,h.per_mov, ")	
					.append("1 as nc, sum(h.num_horas) as horas, sum(h.num_dias) as dias, ")
					.append("sum(h.num_dias) as dc ")
					.append("from t4636MovXCat m,t4707asistencia_h h,t1276periodo p ")
					.append("where m.cod_mov = h.cod_mov ")
					.append("and h.per_mov = p.periodo "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					//regimen/modalidad
					if (regimen.equals("0")) { // Regimen 276 - 728				
						SQL.append("and h.cod_reg not in (?,?) ");
						SQL.append("and p.fcierre[7,10]||p.fcierre[4,5]||p.fcierre[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("1")){//Regimen 1057
						SQL.append("and h.cod_reg = ? ");
						SQL.append("and p.fec_cierre_cas[7,10]||p.fec_cierre_cas[4,5]||p.fec_cierre_cas[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("2")){//Modalidad Formativa
						SQL.append("and h.cod_reg = ? ");
						SQL.append("and p.fec_cierre_mf[7,10]||p.fec_cierre_mf[4,5]||p.fec_cierre_mf[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}					
					//anio
					SQL.append("and h.per_mov[1,4] = ? ");
					//categoria
					SQL.append("and m.cod_cate = ? ");
					//intendencia seleccionada
					SQL.append("and h.cod_inte = ? ");
					//colaborador
					SQL.append("and h.cod_pers = ? ");					
				SQL.append("group by 1,2,3 "); 	
						
			Object o[]=null;		
			
			//regimen/modalidad
			if (regimen.equals("0")) { // Regimen 276 - 728				
				o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
						constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria,intendencia,cod_pers};
			}else if(regimen.equals("1")){//Regimen 1057
				o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria,intendencia,cod_pers};
			}else if(regimen.equals("2")){//Modalidad Formativa
				o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria,intendencia,cod_pers};
			}						
			setIsolationLevel(T4707DAO.TX_READ_UNCOMMITTED);
			lista = executeQuery(datasource, SQL.toString(), o);
			setIsolationLevel(-1);			
			if(log.isDebugEnabled()) log.debug("T4707DAO findDetalle_rptMenXUo_ByRegistro - lista:  " + lista);			
		
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta de metodo: findDetalle_rptMenXUo_ByRegistro");
		}
		return lista;		
	}	
	
	/**
	 * metodo findAnualHorasDiasNcDcByCodUo_rptMenXUo_ByRegistro : encargado de buscar el valor de los indicadores anuales: horas, dias, numero de colaboradores y dias por colaborador por la unidad organica de un colaborador de acuerdo a regimen, anio, categoria, colaborador y unidad organica	
	 * @param Map params (String regimen,String anio,String categoria,String cod_pers,String subuuoo)	 
	 * @return Map
	 * @exception DAOException
	 * */
	public Map findAnualHorasDiasNcDcByCodUo_rptMenXUo_ByRegistro(Map params) throws DAOException {				
		
		if(log.isDebugEnabled()) log.debug("T4707DAO findAnualHorasDiasNcDcByCodUo_rptMenXUo_ByRegistro - params: " + params);	
		Map	indicadoresAnual = null;		
		StringBuffer SQL = new StringBuffer("");
		
		try{					
			String regimen = params.get("regimen").toString(); //0{276 - 728}, 1{1057} y 2{Formativas}			
			String anio = params.get("anio").toString();//2008
			String categoria = params.get("categoria").toString();//009 {otros}
			String cod_pers = params.get("valor").toString();//0091
			String intendencia = params.get("intendencia").toString();//2B0000 {INJ} //adicionado el 01/02/2012 para hacer query mas rapido
			String subuuoo = params.get("subuuoo").toString();//2B2100
								
			SQL.append("select h.cod_uo, ")	
					.append("1 as nc, sum(h.num_horas) as horas, ")
					.append("sum(h.num_dias) as dias, sum(h.num_dias) as dc ")
					.append("from t4636MovXCat m,t4707asistencia_h h,t1276periodo p ")
					.append("where m.cod_mov = h.cod_mov ")
					.append("and h.per_mov = p.periodo "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					//regimen/modalidad
					if (regimen.equals("0")) { // Regimen 276 - 728				
						SQL.append("and h.cod_reg not in (?,?) ");
						SQL.append("and p.fcierre[7,10]||p.fcierre[4,5]||p.fcierre[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("1")){//Regimen 1057
						SQL.append("and h.cod_reg = ? ");
						SQL.append("and p.fec_cierre_cas[7,10]||p.fec_cierre_cas[4,5]||p.fec_cierre_cas[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("2")){//Modalidad Formativa
						SQL.append("and h.cod_reg = ? ");
						SQL.append("and p.fec_cierre_mf[7,10]||p.fec_cierre_mf[4,5]||p.fec_cierre_mf[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}					
					//anio
					SQL.append("and h.per_mov[1,4] = ? ");
					//categoria
					SQL.append("and m.cod_cate = ? ");
					//intendencia del BeanReporte //adicionado el 01/02/2012 para hacer query mas rapido
					SQL.append("and h.cod_inte = ? ");
					//colaborador
					SQL.append("and h.cod_pers = ? ");				
					//subuuoo
					SQL.append("and h.cod_uo = ? ");
				SQL.append("group by 1 "); 	
						
			Object o[]=null;		
			
			//regimen/modalidad
			if (regimen.equals("0")) { // Regimen 276 - 728				
				o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),
						constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria,intendencia,cod_pers,subuuoo};
			}else if(regimen.equals("1")){//Regimen 1057
				o = new Object[]{constantes.leePropiedad("CODREL_REG1057"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria,intendencia,cod_pers,subuuoo};
			}else if(regimen.equals("2")){//Modalidad Formativa
				o = new Object[]{constantes.leePropiedad("CODREL_MOD_FORMATIVA"),new FechaBean().getFormatDate("yyyyMMdd"),anio,categoria,intendencia,cod_pers,subuuoo};
			}						
			setIsolationLevel(T4707DAO.TX_READ_UNCOMMITTED);
			indicadoresAnual = executeQueryUniqueResult(datasource, SQL.toString(), o);
			setIsolationLevel(-1);			
			if(log.isDebugEnabled()) log.debug("T4707DAO findAnualHorasDiasNcDcByCodUo_rptMenXUo_ByRegistro - indicadoresAnual:  " + indicadoresAnual);			
		
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta de metodo: findAnualHorasDiasNcDcByCodUo_rptMenXUo_ByRegistro");
		}
		return indicadoresAnual;		
	}
	
	/*
	/**
	 * metodo findAnualNcDcByCodUo_rptMenXUo_ByUUOOByInteByInst : encargado de buscar el valor de los indicadores anuales: numero de colaboradores y dias por colaborador por unidad organica de acuerdo a regimen, anio, categoria y unidad organica	
	 * @param Map params (String regimen,String anio,String categoria,String subuuoo)	 
	 * @return Map
	 * @exception DAOException
	 * */
	/*public Map findAnualNcDcByCodUo_rptMenXUo_ByUUOOByInteByInst(Map params) throws DAOException {				
		
		if(log.isDebugEnabled()) log.debug("T4707DAO findAnualNcDcByCodUo_rptMenXUo_ByUUOOByInteByInst - params: " + params);	
		Map	NcDcAnual = null;		
		StringBuffer SQL = new StringBuffer("");
		
		try{					
			String regimen = params.get("regimen").toString(); //0{276 - 728}, 1{1057} y 2{Formativas}			
			String anio = params.get("anio").toString();//2008
			String categoria = params.get("categoria").toString();//009 {otros}	
			String intendencia = params.get("intendencia").toString();//2B0000 {INJ} //adicionado el 01/02/2012 para hacer query mas rapido
			String subuuoo = params.get("subuuoo").toString();//2B2100
			
			SQL.append("select h.cod_uo, ")	
					.append("count(distinct h.cod_pers) as nc, ")
					.append("sum(h.num_dias) as dias ")
					.append("from t4636MovXCat m,t4707asistencia_h h,t1276periodo p ")
					.append("where m.cod_mov=h.cod_mov ")
					.append("and h.per_mov = p.periodo "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy									
					//regimen/modalidad
					if (regimen.equals("0")) { // Regimen 276 - 728				
						SQL.append("and h.cod_reg not in (?,?) ");
						SQL.append("and p.fcierre[7,10]||p.fcierre[4,5]||p.fcierre[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("1")){//Regimen 1057
						SQL.append("and h.cod_reg = ? ");
						SQL.append("and p.fec_cierre_cas[7,10]||p.fec_cierre_cas[4,5]||p.fec_cierre_cas[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}else if(regimen.equals("2")){//Modalidad Formativa
						SQL.append("and h.cod_reg = ? ");
						SQL.append("and p.fec_cierre_mf[7,10]||p.fec_cierre_mf[4,5]||p.fec_cierre_mf[1,2] <= ? "); //ICAPUNAY 28/02/2012 periodos cerrados con fecha de cierre <= fecha hoy
					}					
					//anio
					SQL.append("and h.per_mov[1,4] = ? ");
					//categoria
					SQL.append("and m.cod_cate = ? ");
					//intendencia del BeanReporte //adicionado el 01/02/2012 para hacer query mas rapido
					SQL.append("and h.cod_inte = ? ");
					//subuuoo
					SQL.append("and h.cod_uo = ? ");
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
			setIsolationLevel(T4707DAO.TX_READ_UNCOMMITTED);
			NcDcAnual = executeQueryUniqueResult(datasource, SQL.toString(), o);
			setIsolationLevel(-1);			
			if(log.isDebugEnabled()) log.debug("T4707DAO findAnualNcDcByCodUo_rptMenXUo_ByUUOOByInteByInst - NcDcAnual:  " + NcDcAnual);			
		
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta de metodo: findAnualNcDcByCodUo_rptMenXUo_ByUUOOByInteByInst");
		}
		return NcDcAnual;		
	}*/	
	//fin consultas reporte mensual por unidad organica
}
