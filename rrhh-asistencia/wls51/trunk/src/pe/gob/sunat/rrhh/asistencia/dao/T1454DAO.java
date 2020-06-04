package pe.gob.sunat.rrhh.asistencia.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.bean.MensajeBean;
import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.Propiedades;
import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.utils.Constantes;

/** 
 * 
 * Clase       : T1454DAO
 * Descripcion : clase encargada de administrar los datos de la tabla t1454asistencia_d 
 * Proyecto    : ASISTENCIA
 * Fecha       : 03-JUN-2008
 * @author PRAC-JCALLO 
 * @version 1.0
 * 
 * */

public class T1454DAO extends DAOAbstract {
	
	private DataSource dataSource = null;
	Propiedades constantes = new Propiedades(getClass(),"/constantes.properties");
/*	
	private final StringBuffer FIND_BY_FECHAS = 
		new StringBuffer("SELECT p.t02cod_pers, p.t02ap_pate, p.t02ap_mate, p.t02nombres, ")
				.append("( select nvl(c.t99descrip,'') from t99codigos c ")
				.append("  where c.t99cod_tab ='001' ")
				.append("  and c.t99tip_desc ='D' ")
				.append("  and c.t99codigo = substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) ")
				.append(") as categoria , ")
				.append("sum(r.total) as totales ")
				.append("FROM t1454asistencia_d r, t02perdp p ")
				.append("WHERE r.cod_pers = p.t02cod_pers ")
				.append(" and periodo is not null ")
				.append(" and r.mov = '00'")
				.append(" and fecha >= ? ")
				.append(" and fecha <= ? ")
				.append(" and r.esta_id='1' ")
				.append(" and p.t02cod_uorg = ? ")
				.append("group by 1, 2, 3, 4, 5 ");
*/	
	//JRR
	private final StringBuffer FIND_BY_FECHAS = 
		//new StringBuffer("SELECT p.t02cod_uorg, p.t02cod_pers, p.t02ap_pate, p.t02ap_mate, p.t02nombres, r.fecha, ")
		new StringBuffer("SELECT substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) as t02cod_uorg, p.t02cod_pers, p.t02ap_pate, p.t02ap_mate, p.t02nombres, r.fecha, ")	
				.append("(SELECT SUM(a.total) FROM t1454asistencia_d a, t02perdp b ")  
				.append("WHERE a.cod_pers = b.t02cod_pers ")
				.append("and b.t02cod_pers = p.t02cod_pers ")
				.append("and periodo is not null ") 
				.append("and a.mov = '00' ")
				.append("and fecha >= ? ") 
				.append("and fecha <= ? ") 
				.append("and a.esta_id='1' ")
				//prac-asanchez - 28/05/2009
				.append("and (b.t02f_cese >= ? or b.t02f_cese is null) ")
				.append("and b.t02f_fallec is null ")
				.append("and b.t02cod_stat ='1' ")     
				//
				.append("having sum(a.total) > ?) as totales ") 
				.append("FROM t1454asistencia_d r, t02perdp p ")
				.append("WHERE r.cod_pers = p.t02cod_pers ")
				.append(" and periodo is not null ")
				.append(" and r.mov = '00'")
				.append(" and fecha >= ? ")
				.append(" and fecha <= ? ")
				.append(" and r.esta_id='1' ")
				//JRR .append(" and p.t02cod_uorg = ? ")
				//prac-asanchez - 28/05/2009
				.append("and (p.t02f_cese >= ? or p.t02f_cese is null) ")
				.append("and p.t02f_fallec is null ")
				.append("and p.t02cod_stat ='1' ")     
				//
				.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = ? ")
				.append("order by 1, 2, 3, 4, 5, 6 ");
	//
	
	private final StringBuffer findInasistenciasByFecha = new StringBuffer("SELECT cod_pers, periodo, mov, ")
	.append(" fecha, total, esta_id, fcreacion, fmod from t1454asistencia_d ")
	.append(" WHERE periodo is not null AND mov = '00' AND fecha = ? AND esta_id = ?");
	
	private final StringBuffer findMovByFechasAndCodPers = new StringBuffer("SELECT fecha, cod_pers, mov ")
	.append(" from t1454asistencia_d WHERE cod_pers = ? AND periodo is not null AND mov = '00' AND fecha >= ? ")
	.append(" AND fecha <= ?  AND esta_id = '1' ")
	.append(" UNION ")
	.append(" SELECT fecha , cod_pers, 'xxxx' as mov from t1454asistencia_d ")
	.append(" WHERE cod_pers = ? AND periodo is not null AND fecha >= ? AND fecha <= ? ")
	.append(" AND fecha NOT IN ( ")
	.append("		    SELECT fecha ")
	.append("           from t1454asistencia_d ")
	.append("           WHERE cod_pers = ? AND periodo is not null AND mov = '00' AND fecha >= ? ")
	.append("           AND fecha <= ?  AND esta_id = '1' ")
	.append("           ) ")
	.append(" AND esta_id = '1' ")
	.append(" group by fecha, cod_pers ")
	.append(" ORDER BY 1 DESC");
	
	private final StringBuffer findInaByFechasAndCodPers = new StringBuffer("SELECT cod_pers, sum(total) as total ")
	.append(" FROM t1454asistencia_d WHERE cod_pers = ? and periodo is not null ")
	.append(" and mov = '00' and fecha >= ? and fecha <= ? and esta_id='1' group by 1");
	
	//JVILLACORTA - 14/11/2011 - MODIFICA ALERTA DE SOLICITUDES	
	private final StringBuffer findFchIniPeriodo = new StringBuffer(" SELECT periodo,finicio,ffin,fec_ini_cas, ")
	.append(" fec_fin_cas,fec_ini_mf,fec_fin_mf ")
	.append(" FROM t1276Periodo ")
	.append(" WHERE periodo = ? ");
	
	private final StringBuffer findTrabMovimientoAsistencia = new StringBuffer(" SELECT unique a.cod_pers, p.t02cod_rel ")
	.append(" FROM t1454Asistencia_d a, t1279Tipo_Mov t, t02perdp p ")
	.append(" WHERE a.mov = t.mov and ")		
	.append(" a.cod_pers = p.t02cod_pers and ")		
	.append(" a.mov in (select t99codigo ")
	.append(" from t99codigos ")
	.append(" where t99cod_tab ='902' and ")
	.append(" t99estado ='1' and ")
	.append(" t99tip_desc='D' and ")
	.append(" t99_modulo in (0,1)) and ")
	.append(" a.esta_id = ? ")		
	.append(" and a.cod_pers is not null ")
	.append(" group by a.cod_pers, p.t02cod_rel ");

	private final StringBuffer findMovimientoAsistenciaByTrabCAS = new StringBuffer(" SELECT a.periodo, a.cod_pers, a.mov, p.t02cod_rel, a.fecha, t.descrip ")
	.append(" FROM t1454Asistencia_d a, t1279Tipo_Mov t, t02perdp p ")
	.append(" WHERE a.mov = t.mov and ")		
	.append(" a.cod_pers = p.t02cod_pers and ")
	.append(" a.mov in (select t99codigo ")
	.append(" from t99codigos ")
	.append(" where t99cod_tab ='902' and ")
	.append(" t99estado ='1' and ")
	.append(" t99tip_desc='D' and ")
	.append(" t99_modulo in (0,1)) and ") 
	.append(" a.esta_id = ? and ") 
	.append(" a.cod_pers = ? and ")
	.append(" a.fecha between ? and ? ")		
	.append(" order by a.fecha asc ");

	private final StringBuffer findMovimientoAsistenciaByTrab = new StringBuffer(" SELECT a.periodo, a.cod_pers, a.mov, p.t02cod_rel, a.fecha, t.descrip ")
	.append(" FROM t1454Asistencia_d a, t1279Tipo_Mov t, t02perdp p ")
	.append(" WHERE a.mov = t.mov and ")		
	.append(" a.cod_pers = p.t02cod_pers and ")
	.append(" a.mov in (select t99codigo ")
	.append(" from t99codigos ")
	.append(" where t99cod_tab ='902' and ")
	.append(" t99estado ='1' and ")
	.append(" t99tip_desc='D' and ")
	.append(" t99_modulo='0') and ")
	.append(" a.esta_id = ? and ")
	.append(" a.cod_pers = ? and ")
	.append(" a.fecha between ? and ? ")		
	.append(" order by a.fecha asc ");	
	
	private final StringBuffer findMovimientoAsistenciaByTrabMF = new StringBuffer(" SELECT a.periodo, a.cod_pers, a.mov, p.t02cod_rel, a.fecha, t.descrip ")
	.append(" FROM t1454Asistencia_d a, t1279Tipo_Mov t, t02perdp p ")
	.append(" WHERE a.mov = t.mov and ")		
	.append(" a.cod_pers = p.t02cod_pers and ")
	.append(" a.mov in ('00') and ")
	.append(" a.esta_id = ? and ")
	.append(" a.cod_pers = ? and ")
	.append(" a.fecha between ? and ? ")		
	.append(" order by a.fecha asc ");	
	//FIN - JVILLACORTA - 14/11/2011 - MODIFICA ALERTA DE SOLICITUDES
	
	//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
	  private static final StringBuffer findUOsByJefe = new StringBuffer("SELECT * ")
	  .append("FROM t12uorga where substr(trim(nvl(t12cod_encar,'')||nvl(t12cod_jefat,'')),1,4)=? ")
	  .append("and t12ind_estad=? "); 
	  //ICAPUNAY - PAS20165E230300132
		
	/**
	 * @param datasource Object
	 * */
	public T1454DAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.dataSource = (DataSource)datasource;
		else if (datasource instanceof String)
			this.dataSource = getDataSource((String)datasource);
		else
			throw new DAOException(this, "Datasource no valido");
	}
	
	//JVILLACORTA 20/05/2011 - AOM:MODALIDADES FORMATIVAS
	/**
	 * metodo findByFechas : encargado de buscar inasistencia de trabajadores de una unidad organizacional por rango de fechas
	 * 						 y es posible especificar numero de dias de inasistencias minimas a buscar
	 * @param Map params(String numDias, String fechaIni, String fechaFin, String cod_uorgan)
	 * @return List
	 * @exception DAOException
	 * */
	public List findByFechas(Map params) throws DAOException {				
		
		if(log.isDebugEnabled()) log.debug("T1454DAO findByFechas - params: " + params);
		List listaResul = new ArrayList();
		//JVV -- ini
		//StringBuffer SQL = new StringBuffer(FIND_BY_FECHAS.toString());
		StringBuffer SQL = new StringBuffer("");
		//Connection con = null;
		//PreparedStatement pre = null;
		String criterio = params.get("criterio").toString();
		String regimen = params.get("regimen").toString();
		HashMap seguridad = (HashMap)params.get("seguridad");
		
		//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
		List objsJefe = new ArrayList();
		List resUOsJefe= new ArrayList();
		HashMap uoMap = new HashMap();
	    String codUoJef = "";
		String codPersUsuario = seguridad!=null && !seguridad.isEmpty()? ((String) seguridad.get("codPers")).trim():"";
		//ICAPUNAY - PAS20165E230300132 
		
		//String dbpool = params.get("dbpool").toString();
		
		SQL.append("SELECT substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) as t02cod_uorg, u.t12des_uorga, p.t02cod_rel, p.t02cod_pers, p.t02ap_pate, p.t02ap_mate, p.t02nombres, r.fecha, ")	
				.append("(SELECT SUM(a.total) FROM t1454asistencia_d a, t02perdp b ")  
				.append("WHERE a.cod_pers = b.t02cod_pers ")
				.append("and b.t02cod_pers = p.t02cod_pers ")
				.append("and periodo is not null ") 
				.append("and a.mov = '00' ")
				.append("and fecha >= ? ") 
				.append("and fecha <= ? ") 
				.append("and a.esta_id='1' ")
				//prac-asanchez - 28/05/2009
				.append("and (b.t02f_cese >= ? or b.t02f_cese is null) ")
				.append("and b.t02f_fallec is null ")
				.append("and b.t02cod_stat in ('1','0') ") // JVILLACORTA 02/09/2011
				//.append("and b.t02cod_stat ='1' ")     
				//
				.append("having sum(a.total) > ?) as totales ") //nDias
				.append("FROM t1454asistencia_d r, t02perdp p, t12uorga u ")
				.append("WHERE r.cod_pers = p.t02cod_pers ")
				.append(" and u.t12cod_uorga = substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) ")
				.append(" and periodo is not null ")
				.append(" and r.mov = '00'")
				.append(" and fecha >= ? ")
				.append(" and fecha <= ? ")
				.append(" and r.esta_id='1' ")
				//JRR .append(" and p.t02cod_uorg = ? ")
				//prac-asanchez - 28/05/2009
				.append("and (p.t02f_cese >= ? or p.t02f_cese is null) ")
				.append("and p.t02f_fallec is null ");
				//.append("and p.t02cod_stat ='1' "); // JVILLACORTA 24/08/2011    
				//
				if (criterio.equals("0")) { //registro					
					SQL.append(" and p.t02cod_stat in ('1','0') "); // JVILLACORTA 24/08/2011 (ver INACTIVOS tambiÃ©n para todos los regimenes pero solo por el criterio REGISTRO)
					SQL.append( " and p.t02cod_pers = '" ).append( params.get("valor").toString().trim().toUpperCase()).append( "'");
					//JVV - 20/05/2011
					if (regimen.equals("es276")) {						
						SQL.append( "and   p.t02cod_rel not in (?,?) ");//09,10					
					} else {
						if (regimen.equals("es1057")) {						
							SQL.append( "and   p.t02cod_rel = ? "); //09						
						} else {
							SQL.append( "and   p.t02cod_rel = ? "); //10
						}						
					}
				}
				if (criterio.equals("1")) { //uuoo
					SQL.append("and p.t02cod_stat ='1' ");// JVILLACORTA 24/08/2011
					SQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = '").append(params.get("cod_uorgan").toString().trim()).append( "'");					
					//JVV - 20/05/2011
					if (regimen.equals("es276")) {						
						SQL.append( "and   p.t02cod_rel not in (?,?) ");//09,10					
					} else {
						if (regimen.equals("es1057")) {						
							SQL.append( "and   p.t02cod_rel = ? "); //09						
						} else {
							SQL.append( "and   p.t02cod_rel = ? "); //10
						}	
					}
				}
				
				if (criterio.equals("4")) {	//Intendencia	
					String intendencia = params.get("valor").toString().trim().substring(0,2); // 2A de 2A0000
					//PROBAR
					SQL.append("and p.t02cod_stat ='1' ");// JVILLACORTA 24/08/2011
					SQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) LIKE '")
					.append( intendencia.trim().toUpperCase() ).append( "%'");				
					//JVV - 20/05/2011
					if (regimen.equals("es276")) {				
						SQL.append( "and   p.t02cod_rel not in (?,?) "); //in (09,10)			
					} else {
						if (regimen.equals("es1057")) {						
							SQL.append( "and   p.t02cod_rel = ? "); //09						
						} else {
							SQL.append( "and   p.t02cod_rel = ? "); //10
						}	
					}
				}
				
				if (criterio.equals("3")) {	//Institucional									
					SQL.append("and p.t02cod_stat ='1' "); // JVILLACORTA 24/08/2011
					//JVV - 20/05/2011
					if (regimen.equals("es276")) {				
						//SQL.append( "and   p.t02cod_rel in (?,?) ");	//in (09,10)	//ICAPUNAY 05/07/2011 A SOLICITUD DE CLAMA POR ERROR EN REPORTE CRITERIO "INSITUCIONAL" Y REGIMEN "276-728"		
						SQL.append( "and   p.t02cod_rel not in (?,?) ");	//in (09,10) //ICAPUNAY 05/07/2011 A SOLICITUD DE CLAMA POR ERROR EN REPORTE CRITERIO "INSITUCIONAL" Y REGIMEN "276-728"			
					} else {
						if (regimen.equals("es1057")) {						
							SQL.append( "and   p.t02cod_rel = ? "); //09						
						} else {
							SQL.append( "and   p.t02cod_rel = ? "); //10
						}	
					}
				}
				
				//criterios de visibilidad
				if (seguridad != null && !seguridad.isEmpty()) {					
					HashMap roles = (HashMap) seguridad.get("roles");
					String uoSeg = (String) seguridad.get("uoSeg");
					String uoAO = (String) seguridad.get("uoAO");//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			        if (log.isDebugEnabled()) log.debug("roles: "+roles); //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
			        if (log.isDebugEnabled()) log.debug("uoAO: "+uoAO); //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

					if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
						SQL.append(" and 1=1 ");
					} 
					//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			        else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null){	
			        	if (log.isDebugEnabled()) log.debug("es ROL_ANALISTA_OPERATIVO");
			        	SQL.append(" and ((substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "') ");
			        	SQL.append(" or (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) in (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '").append(uoAO).append( "'))) ");		        	
			        }
					//else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null || //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
					else if (
					//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
							roles.get(Constantes.ROL_SECRETARIA) != null
							|| roles.get(Constantes.ROL_JEFE) != null) {
						
						//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
						if (roles.get(constantes.leePropiedad("ROL_JEFE")) != null) {
							
							//si es jefe, debe visualizar informacion de colaboradores de todas las unidades donde es encargado o jefe (incluyendo subunidades)
							objsJefe.add(codPersUsuario.toUpperCase());
							objsJefe.add("1");    		  
							resUOsJefe=executeQuery(dataSource, findUOsByJefe.toString(), objsJefe.toArray());
							if(log.isDebugEnabled()) log.debug("resUOsJefe:"+ resUOsJefe);
							if (resUOsJefe.size()>0){    			   		      
								for (int i = 0;i<resUOsJefe.size();i++){ //0,1,2 (3 unidades)
									uoMap=(HashMap)resUOsJefe.get(i);
									codUoJef= uoMap.get("t12cod_uorga").toString().trim();
									if (i==0){//0 (primer registro)
										SQL = SQL.append(" and (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(findUuooJefe(codUoJef)).append("%' "); 
									}else{
										SQL = SQL.append(" or substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(findUuooJefe(codUoJef)).append("%' "); 
									}    		    	  
									if (i==resUOsJefe.size()-1){//2 (ultimo registro)
										SQL = SQL.append(") ");
									}
								}
							}
						}
						else { //secretaria
							SQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "' ");
						}				
						//SQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "' ");
						//FIN ICAPUNAY - PAS20165E230300132
					
					} else {
						SQL.append(" and 1=2 ");
					}
				}
				
			//SQL.append("order by 1, 2, 3, 4, 5, 6");	//ICAPUNAY - FORMATIVAS 16/06/2011	
			SQL.append("order by 1, 2, 3, 4, 5, 6 ,8"); //ICAPUNAY - FORMATIVAS 16/06/2011			
		//JVV -- fin
		String numDias = (params.get("numDias") != null) ? params.get("numDias").toString(): "";		
		if(numDias.length()<1){
			numDias="0.0";
		}		
		Float nDias = new Float(numDias);		
		FechaBean fec_ini = new FechaBean(params.get("fechaIni").toString());		
		FechaBean fec_fin = new FechaBean(params.get("fechaFin").toString());
		//prac-asanchez - 28/05/2009
		FechaBean fec_actual = new FechaBean();
		if(log.isDebugEnabled()) log.debug("query: " + SQL);
		if(log.isDebugEnabled()) log.debug("fec_actual: " + fec_actual);
		if(log.isDebugEnabled()) log.debug("fec_actual.getSQLDate(): " + fec_actual.getSQLDate());
		
		Object o[];
		//JVV - 20/05/2011
		if (regimen.equals("es276")) {
			o = new Object[]{fec_ini.getSQLDate(),fec_fin.getSQLDate(),fec_actual.getSQLDate(),nDias,
					fec_ini.getSQLDate(), fec_fin.getSQLDate(),fec_actual.getSQLDate(),					 
					constantes.leePropiedad("CODREL_REG1057"),
					constantes.leePropiedad("CODREL_MOD_FORMATIVA")};
		} 
		else {
			if (regimen.equals("es1057")) {
				o = new Object[]{fec_ini.getSQLDate(),fec_fin.getSQLDate(),fec_actual.getSQLDate(),nDias,
						fec_ini.getSQLDate(), fec_fin.getSQLDate(),fec_actual.getSQLDate(),					
						constantes.leePropiedad("CODREL_REG1057")};
			} else {
				o = new Object[]{fec_ini.getSQLDate(),fec_fin.getSQLDate(),fec_actual.getSQLDate(),nDias,
						fec_ini.getSQLDate(), fec_fin.getSQLDate(),fec_actual.getSQLDate(),					
						constantes.leePropiedad("CODREL_MOD_FORMATIVA")};
			}										
		}//
		
		if(log.isDebugEnabled()) log.debug("REPORTE INASISTENCIAS CON N DIAS: " + nDias);
		listaResul = executeQuery(dataSource, SQL.toString(), o);
		
		if(log.isDebugEnabled()) log.debug("T1454DAO findByFechas - listaResul: " + listaResul);
		//
/*
		if(nDias.floatValue()>0.0){						
			SQL.append(" having sum(r.total) > ? order by 2");			
			listaResul = executeQuery(dataSource, SQL.toString(), new Object[]{fec_ini.getSQLDate(), fec_fin.getSQLDate(), params.get("cod_uorgan"), nDias});
		} else {			
			SQL.append(" order by 2");
			if(log.isDebugEnabled()) log.debug("ASISTENCIA QUERY REPORTE INASISTENCIAS: "+SQL);
			listaResul = executeQuery(dataSource, SQL.toString(), new Object[]{fec_ini.getSQLDate(), fec_fin.getSQLDate(), params.get("cod_uorgan")});
		}
*/		
		return listaResul;
	}
	//FIN - JVILLACORTA 20/05/2011 - AOM:MODALIDADES FORMATIVAS
	
	/**
	 * metodo findByFechaAndCodPers : encargado de buscar movimientos de un trabajador en una determinada fecha
	 * @param String fecha
	 * @return List
	 * @exception DAOException
	 * */
	public List findInasistenciasByFecha(String fecha) throws DAOException {		
		List listaResul = new ArrayList();				
		FechaBean bfecha = new FechaBean(fecha);		
		
		listaResul = executeQuery(dataSource, findInasistenciasByFecha.toString(),
				new Object[]{bfecha.getSQLDate(), "1"});
		
		return listaResul;
	}
	
	/**
	 * metodo findMovByFechasAndCodPers : encargado de buscar inasistencia y asistencias ordenados por fecha para 
	 *                                    un determinado trabajador
	 * @param Map params (String cod_pers, String fechaIni, String FechaFin)
	 * @return List
	 * @exception DAOException
	 * */
	public List findMovByFechasAndCodPers(Map params) throws DAOException {		
		List listaResul = new ArrayList();				
		FechaBean fechaIni = new FechaBean(params.get("fechaIni").toString());
		FechaBean fechaFin = new FechaBean(params.get("fechaFin").toString());
		
		listaResul = executeQuery(dataSource, findMovByFechasAndCodPers.toString().toString(),
				new Object[]{	params.get("cod_pers") ,fechaIni.getSQLDate(), fechaFin.getSQLDate(),
								params.get("cod_pers") ,fechaIni.getSQLDate(), fechaFin.getSQLDate(),
								params.get("cod_pers") ,fechaIni.getSQLDate(), fechaFin.getSQLDate()});
		
		return listaResul;
	}
	
	/**
	 * metodo findInasistenciaByFechas : encargado de buscar inasistencia de un trabajador por rango de fechas
	 * 						 y es posible especificar numero de dias de inasistencias minimas a buscar
	 * @param Map params(String numDias, String fechaIni, String fechaFin, String cod_pers)
	 * @return List
	 * @exception DAOException
	 * */
	public Map findInaByFechasAndCodPers(Map params) throws DAOException {		
		Map mResul = new HashMap();
		StringBuffer SQL = new StringBuffer(findInaByFechasAndCodPers.toString());
		//log.debug("*************************************************************");
		//log.debug("sql: "+SQL);
		//log.debug("*************************************************************");
		String numDias = (params.get("numDias") != null) ? params.get("numDias").toString(): "";		
		if(numDias.length()<1){
			numDias="0.0";
		}		
		Float nDias = new Float(numDias);		
		FechaBean fec_ini = new FechaBean(params.get("fechaIni").toString());		
		FechaBean fec_fin = new FechaBean(params.get("fechaFin").toString());			
		
		if(nDias.floatValue()>0.0){						
			SQL.append(" having sum(total) > ? ");
			if(log.isDebugEnabled()) log.debug("**** inasistencias por trabajador SQL con having :"+SQL);
			mResul = executeQueryUniqueResult(dataSource, SQL.toString(), new Object[]{params.get("cod_pers"),fec_ini.getSQLDate(), fec_fin.getSQLDate(),  nDias});
		} else {
			if(log.isDebugEnabled()) log.debug("******** inasistencias por trabajador SQL sin having :"+SQL);
			mResul = executeQueryUniqueResult(dataSource, SQL.toString(), new Object[]{params.get("cod_pers"),fec_ini.getSQLDate(), fec_fin.getSQLDate()});
		}
		return mResul;
	}
	
	/**
	 * metodo findDiasLaborablesByFiniFin : encargado de buscar numero de dias laborados entre rango de fechas
	 *  						 
	 * @param Map params(String cod_pers, String fechaIni, String fechaFin)
	 * @return List
	 * @exception DAOException
	 * */
	public Map findDiasLaborablesByFiniFin(Map params) throws DAOException {		
		Map mResul = new HashMap();
		StringBuffer SQL = new StringBuffer(findInaByFechasAndCodPers.toString());
		//log.debug("*************************************************************");
		//log.debug("sql: "+SQL);
		//log.debug("*************************************************************");
		String numDias = (params.get("numDias") != null) ? params.get("numDias").toString(): "";		
		if(numDias.length()<1){
			numDias="0.0";
		}		
		Float nDias = new Float(numDias);		
		FechaBean fec_ini = new FechaBean(params.get("fechaIni").toString());		
		FechaBean fec_fin = new FechaBean(params.get("fechaFin").toString());			
		
		if(nDias.floatValue()>0.0){						
			SQL.append(" having sum(total) > ? ");
			if(log.isDebugEnabled()) log.debug("**** inasistencias por trabajador SQL con having :"+SQL);
			mResul = executeQueryUniqueResult(dataSource, SQL.toString(), new Object[]{params.get("cod_pers"),fec_ini.getSQLDate(), fec_fin.getSQLDate(),  nDias});
		} else {
			if(log.isDebugEnabled()) log.debug("******** inasistencias por trabajador SQL sin having :"+SQL);
			mResul = executeQueryUniqueResult(dataSource, SQL.toString(), new Object[]{params.get("cod_pers"),fec_ini.getSQLDate(), fec_fin.getSQLDate()});
		}
		return mResul;
	}
	
	
	/* JRR - 08/04/2011 - RECUPERACION DE FUENTES - Decompilados */
    public List findDiasLabAcumXTrabajador(Map params)
    {
        List listaResul = new ArrayList();
        listaResul = executeQuery(dataSource, "select a.cod_pers,count(distinct a.fecha) dias from t1454asistencia_d a where a.fecha >=? and a.fecha <=? and a.mov not in (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) group by a.cod_pers", new Object[] {
            params.get("fec_ini"), params.get("fec_fin"), "115", "003", "57", "23", "44", "24", "38", "53", 
            "004", "07", "31", "39", "41", "009", "114", "42", "010", "011", 
            "008", "06", "55", "56", "00"
        });
        return listaResul;
    }

    public List findDiasLicXFecXLimite(Map params)
    {
      List listaResul = new ArrayList();
      listaResul = executeQuery(this.dataSource, "select a.cod_pers,sum(total) dias from t1454asistencia_d a where year(a.fecha) =? and a.mov=? group by a.cod_pers having sum(total)>?", new Object[] { 
        new FechaBean(params.get("fec_ini_desc").toString()).getAnho(), "21", new Integer("20") });
      return listaResul;
    }    

    public int findDiasXTrabXFechaXLic(Map params)
    {
      String resultado = "0";
      Map mapResul = executeQueryUniqueResult(this.dataSource, "select sum(a.total) diasmes from t1454asistencia_d a where a.cod_pers=? and a.fecha >=? and a.fecha <=? and a.mov=?", new Object[] { 
        params.get("cod_pers"), params.get("fec_ini"), params.get("fec_fin"), "21" });
      if ((mapResul != null) && (mapResul.get("diasmes") != null)) {
        resultado = mapResul.get("diasmes").toString();
      }
      return new Float(resultado).intValue();
    }    
	/*             */
    
    //ICAPUNAY 27/07/2011 - CORRECCION PARA REPORTE DE INASISTENCIAS DE FORMATIVOS (ERROR: DIAS EN DECIMALES)
	/**
	 * metodo findByFechas_Formativos : encargado de buscar inasistencia de trabajadores de modalidad formativa de una unidad organizacional por rango de fechas
	 * 						 y es posible especificar numero de dias de inasistencias minimas a buscar
	 * @param Map params(String numDias, String fechaIni, String fechaFin, String cod_uorgan)
	 * @return List
	 * @exception DAOException
	 * */
	public List findByFechas_Formativos(Map params) throws DAOException {				
		
		if(log.isDebugEnabled()) log.debug("T1454DAO findByFechas_Formativos - params: " + params);
		List listaResul = new ArrayList();
		StringBuffer SQL = new StringBuffer("");		
		String criterio = params.get("criterio").toString();		
		HashMap seguridad = (HashMap)params.get("seguridad");
		
		//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
		List objsJefe = new ArrayList();
		List resUOsJefe= new ArrayList();
		HashMap uoMap = new HashMap();
	    String codUoJef = "";
		String codPersUsuario = seguridad!=null && !seguridad.isEmpty()? ((String) seguridad.get("codPers")).trim():"";
		//ICAPUNAY - PAS20165E230300132 	
		
		SQL.append("SELECT substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) as t02cod_uorg, u.t12des_uorga, p.t02cod_rel, p.t02cod_pers, p.t02ap_pate, p.t02ap_mate, p.t02nombres, r.fecha, ")	
				.append("(SELECT count(a.fecha) FROM t1454asistencia_d a, t02perdp b ")  //cuenta los dÃ­as de inasistencias (count(a.fecha)) por cada trabajador formativo
				.append("WHERE a.cod_pers = b.t02cod_pers ")
				.append("and b.t02cod_pers = p.t02cod_pers ")
				.append("and periodo is not null ") 
				.append("and a.mov = '00' ")
				.append("and fecha >= ? ") 
				.append("and fecha <= ? ") 
				.append("and a.esta_id='1' ")				
				.append("and (b.t02f_cese >= ? or b.t02f_cese is null) ")
				.append("and b.t02f_fallec is null ")
				.append("and b.t02cod_stat in ('1','0') ") // JVILLACORTA 02/09/2011
				//.append("and b.t02cod_stat ='1' ")				
				.append("having sum(a.total) > ?) as totales ") //nDias
				.append("FROM t1454asistencia_d r, t02perdp p, t12uorga u ")
				.append("WHERE r.cod_pers = p.t02cod_pers ")
				.append(" and u.t12cod_uorga = substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) ")
				.append(" and periodo is not null ")
				.append(" and r.mov = '00'")
				.append(" and fecha >= ? ")
				.append(" and fecha <= ? ")
				.append(" and r.esta_id='1' ")				
				.append("and (p.t02f_cese >= ? or p.t02f_cese is null) ")
				.append("and p.t02f_fallec is null ");
				//.append("and p.t02cod_stat ='1' ");// JVILLACORTA 24/08/2011     
				
				if (criterio.equals("0")) { //registro					
					SQL.append(" and p.t02cod_stat in ('1','0') "); // JVILLACORTA 24/08/2011 (ver INACTIVOS tambiÃ©n para todos los regimenes pero solo por el criterio REGISTRO)
					SQL.append( " and p.t02cod_pers = '" ).append( params.get("valor").toString().trim().toUpperCase()).append( "'");					
					SQL.append( "and   p.t02cod_rel = ? "); //10					
				}
				
				if (criterio.equals("1")) { //uuoo
					SQL.append("and p.t02cod_stat ='1' ");// JVILLACORTA 24/08/2011
					SQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = '").append(params.get("cod_uorgan").toString().trim()).append( "'");				
					SQL.append( "and   p.t02cod_rel = ? "); //10						
				}
				
				if (criterio.equals("4")) {	//Intendencia	
					String intendencia = params.get("valor").toString().trim().substring(0,2); // 2A de 2A0000
					SQL.append("and p.t02cod_stat ='1' ");// JVILLACORTA 24/08/2011
					SQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) LIKE '")
					.append( intendencia.trim().toUpperCase() ).append( "%'");				
					SQL.append( "and   p.t02cod_rel = ? "); //10						
				}
				
				if (criterio.equals("3")) {	//Institucional						
					SQL.append("and p.t02cod_stat ='1' ");// JVILLACORTA 24/08/2011
					SQL.append( "and   p.t02cod_rel = ? "); //10						
				}
				
				//criterios de visibilidad
				if (seguridad != null && !seguridad.isEmpty()) {					
					HashMap roles = (HashMap) seguridad.get("roles");
					String uoSeg = (String) seguridad.get("uoSeg");
					String uoAO = (String) seguridad.get("uoAO");//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			        if (log.isDebugEnabled()) log.debug("roles: "+roles); //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
			        if (log.isDebugEnabled()) log.debug("uoAO: "+uoAO); //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

					if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
						SQL.append(" and 1=1 ");
					}
					//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			        else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null){	
			        	if (log.isDebugEnabled()) log.debug("es ROL_ANALISTA_OPERATIVO");
			        	SQL.append(" and ((substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "') ");
			        	SQL.append(" or (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) in (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '").append(uoAO).append( "'))) ");		        	
			        }
					//else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null || //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
					else if (
					//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
							roles.get(Constantes.ROL_SECRETARIA) != null
							|| roles.get(Constantes.ROL_JEFE) != null) {
						
						//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
						if (roles.get(constantes.leePropiedad("ROL_JEFE")) != null) {
							
							//si es jefe, debe visualizar informacion de colaboradores de todas las unidades donde es encargado o jefe (incluyendo subunidades)
							objsJefe.add(codPersUsuario.toUpperCase());
							objsJefe.add("1");    		  
							resUOsJefe=executeQuery(dataSource, findUOsByJefe.toString(), objsJefe.toArray());
							if(log.isDebugEnabled()) log.debug("resUOsJefe:"+ resUOsJefe);
							if (resUOsJefe.size()>0){    			   		      
								for (int i = 0;i<resUOsJefe.size();i++){ //0,1,2 (3 unidades)
									uoMap=(HashMap)resUOsJefe.get(i);
									codUoJef= uoMap.get("t12cod_uorga").toString().trim();
									if (i==0){//0 (primer registro)
										SQL = SQL.append(" and (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(findUuooJefe(codUoJef)).append("%' "); 
									}else{
										SQL = SQL.append(" or substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(findUuooJefe(codUoJef)).append("%' "); 
									}    		    	  
									if (i==resUOsJefe.size()-1){//2 (ultimo registro)
										SQL = SQL.append(") ");
									}
								}
							}
						}
						else { //secretaria
							SQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "' ");
						}				
						//SQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "' ");
						//FIN ICAPUNAY - PAS20165E230300132					
				
					} else {
						SQL.append(" and 1=2 ");
					}
				}				
			SQL.append("order by 1, 2, 3, 4, 5, 6 ,8");			
		
		String numDias = (params.get("numDias") != null) ? params.get("numDias").toString(): "";		
		if(numDias.length()<1){
			numDias="0.0";
		}		
		Float nDias = new Float(numDias);		
		FechaBean fec_ini = new FechaBean(params.get("fechaIni").toString());		
		FechaBean fec_fin = new FechaBean(params.get("fechaFin").toString());
	
		FechaBean fec_actual = new FechaBean();
		if(log.isDebugEnabled()) log.debug("query: " + SQL);
		if(log.isDebugEnabled()) log.debug("fec_actual: " + fec_actual);
		if(log.isDebugEnabled()) log.debug("fec_actual.getSQLDate(): " + fec_actual.getSQLDate());
		
		Object o[];
				
		o = new Object[]{fec_ini.getSQLDate(),fec_fin.getSQLDate(),fec_actual.getSQLDate(),nDias,
						fec_ini.getSQLDate(), fec_fin.getSQLDate(),fec_actual.getSQLDate(),					
						constantes.leePropiedad("CODREL_MOD_FORMATIVA")};
			
		
		if(log.isDebugEnabled()) log.debug("REPORTE INASISTENCIAS CON N DIAS: " + nDias);
		listaResul = executeQuery(dataSource, SQL.toString(), o);
		
		if(log.isDebugEnabled()) log.debug("T1454DAO findByFechas_Formativos - listaResul: " + listaResul);
		
		return listaResul;
	}
	//FIN ICAPUNAY 27/07/2011 - CORRECCION PARA REPORTE DE INASISTENCIAS DE FORMATIVOS (ERROR: DIAS EN DECIMALES)
	
	//JVILLACORTA - 14/11/2011 - MODIFICA ALERTA DE SOLICITUDES	
	/**
	 * Metodo que se encarga de buscar las fechas de inicio de cada periodo 
	 * @param params Map(String periodo)
	 * @throws DAOException
	 */
	public List findFchIniPeriodo(Map hm) throws DAOException {
		
		List resultado = new ArrayList();		
		if (log.isDebugEnabled()) log.debug("periodo: " + hm.get("periodo"));		
	
		resultado = executeQuery(dataSource, findFchIniPeriodo.toString(),
				new Object[]{hm.get("periodo").toString().trim()});				
		
		return resultado;
	}//FIN - JVILLACORTA - 14/11/2011 - MODIFICA ALERTA DE SOLICITUDES
	
	//JVILLACORTA - 14/11/2011 - MODIFICA ALERTA DE SOLICITUDES
	/**
	 * Metodo que se encarga de buscar el codigo unico de los trabajadores que tienen 
	 * movimientos de asistencia inconsistentes
	 * @param Map datos
	 * @return List
	 * @throws DAOException
	 */
	public List findTrabMovimientoAsistencia() throws DAOException {
		List resultado = new ArrayList();		
				
		resultado = executeQuery(dataSource, findTrabMovimientoAsistencia.toString(),
				new Object[]{constantes.leePropiedad("ACTIVO")});
				//new Object[]{constantes.leePropiedad("ACTIVO"), fante, fhoy});
		
		return resultado;
	}//FIN - JVILLACORTA - 14/11/2011 - MODIFICA ALERTA DE SOLICITUDES
	
	//JVILLACORTA - 14/11/2011 - MODIFICA ALERTA DE SOLICITUDES	
	/**
	 * Metodo que se encarga de buscar los movimientos de asistencia inconsistentes por trabajador 
	 * @param params Map(String periodo, String cuser_dest)
	 * @throws DAOException
	 */
	public List findMovimientoAsistenciaByTrab(Map hm) throws DAOException {
		
		List resultado = new ArrayList();
		
		if (log.isDebugEnabled()) log.debug("hoy: " + hm.get("hoy"));
		if (log.isDebugEnabled()) log.debug("fanterior: " + hm.get("fanterior"));		
		Date fhoy = new Date();
		Date fant = new Date();
		fhoy = new FechaBean((String)hm.get("hoy")).getSQLDate();
		if (log.isDebugEnabled()) log.debug("ffin: " + fhoy);
		fant = new FechaBean((String)hm.get("fanterior")).getSQLDate();
		if (log.isDebugEnabled()) log.debug("finicio: " + fant);
		
		String codRel = hm.get("t02cod_rel").toString().trim();
		if (log.isDebugEnabled()) log.debug("regimen: " + hm.get("t02cod_rel").toString().trim());
		
		if(codRel.equals("09")){
			resultado = executeQuery(dataSource, findMovimientoAsistenciaByTrabCAS.toString(),
					new Object[]{constantes.leePropiedad("ACTIVO"), 
								hm.get("cod_pers").toString().trim(), fant, fhoy});		
		}else if(codRel.equals("10")){
			resultado = executeQuery(dataSource, findMovimientoAsistenciaByTrabMF.toString(),
					new Object[]{constantes.leePropiedad("ACTIVO"), 
								hm.get("cod_pers").toString().trim(), fant, fhoy});		
		}else{
			resultado = executeQuery(dataSource, findMovimientoAsistenciaByTrab.toString(),
					new Object[]{constantes.leePropiedad("ACTIVO"),
								hm.get("cod_pers").toString().trim(), fant, fhoy});		
		}
		
		return resultado;
	}//FIN - JVILLACORTA - 14/11/2011 - MODIFICA ALERTA DE SOLICITUDES
	
	//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
	/**
	   * Metodo encargado de obtener el codigo de unidad organica restando los ceros empezando desde el final hasta el caracter que no sea cero		 
	   * @param String unidad
	   * @return String uoJefe
	   * @throws SQLException
	   */
	  public String findUuooJefe(String unidad) throws DAOException {
    
	    String uoJefe = "";	
	    try {

	    	log.debug("findUuooJefe-unidadd: "+unidad); 
			uoJefe= unidad!=null? unidad.trim(): "";
        	if (!"".equals(uoJefe)){
        		log.debug("entro if");
        		int nroCar = uoJefe.length();
            	log.debug("nroCar: "+nroCar);
            	char v= '9';  //solo 1 caracter almacena              	
            	for (int p = nroCar-1; p >= 0; p--) {
            		log.debug("entro for");
            		log.debug("uoJefe: "+uoJefe);
            		log.debug("v: "+v);
            		log.debug("p: "+p);
            		v= uoJefe.charAt(p);
            		if (p!=0){
            			log.debug("entro p!=0");
                		if ('0'==v){
                			uoJefe=uoJefe.substring(0, p);
                			log.debug("uoJefe2: "+uoJefe);
                		}else{                			
                			break;
                		} 
            		}	
            	}            		
        	}
        	log.debug("findUuooJefe-uoJefee: "+uoJefe);
	    }
	    catch (Exception e) {
	    	log.error("*** SQL Error ****",e);
			MensajeBean msg = new MensajeBean();
			msg.setMensajeerror("Ha ocurrido un error inesperado: ".concat( e.getMessage() ));
			msg.setMensajesol("Por favor, intente nuevamente realizar la operacion, de continuar con el problema comuniquese con el webmaster");
	    }
	    finally {
	    }
	    log.debug("findUuooJefe-uoJefee(final): "+uoJefe);
		return uoJefe; 
	  }	  
	  //ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
}
