package pe.gob.sunat.rrhh.planilla.dao;

 
import java.sql.SQLException; 
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

//import org.apache.commons.lang.StringUtils; //PAS20171U230200001 AGONZALESF

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;

/**
 * <p>Title: HistMetasEmpleaDAO </p>
 * <p>Description: Clase para realizar la consulta de Historico Maestro de Personal</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: SUNAT</p>
 * @author EMOSTACERO
 * @version 1.0 
 */
public class HistMetasEmpleaDAO extends DAOAbstract {
  private DataSource datasource; // Variable creada para instanciar el DataSource
  
    private static final StringBuffer FIND_RECIBO_CAS_HISTORICO = new StringBuffer(" SELECT ")
    .append(" HIST_METAS_EMPLEADO.NUME_SERI_REC, HIST_METAS_EMPLEADO.NUME_COMP_REC, ")    
    .append(" HIST_METAS_EMPLEADO.FECH_EMIS_REC, HIST_METAS_EMPLEADO.FLAG_SUSPENSION_4TA, ")        
    .append(" HIST_METAS_EMPLEADO.NUMERO_FORM_SUSPENSION_4TA, HIST_METAS_EMPLEADO.FECHA_FORM_SUSPENSION ")
    .append(" from HIST_METAS_EMPLEADO, TPLANILLA, CONDICION_LABORAL ")
    .append(" where ( TPLANILLA.CODI_COND_LAB = CONDICION_LABORAL.CODI_COND_LAB ) and ")
    .append(" ( HIST_METAS_EMPLEADO.TIPO_PLAN_TPL = TPLANILLA.TIPO_PLAN_TPL ) and ")
    .append(" ( HIST_METAS_EMPLEADO.ANO_PERI_HME = ? ) AND ( HIST_METAS_EMPLEADO.NUME_PERI_HME = ? ) AND ")
    .append(" ( HIST_METAS_EMPLEADO.TIPO_PLAN_TPL  IN  ( SELECT DISTINCT TPLANILLA.TIPO_PLAN_TPL ")
    .append(" FROM TPLANILLA, CONDICION_LABORAL ")
    .append(" WHERE ( TPLANILLA.CODI_COND_LAB = CONDICION_LABORAL.CODI_COND_LAB ) and ")
    .append(" ( CONDICION_LABORAL.CODI_COND_LAB = '09' ) ) ) and ")
    .append(" ( CONDICION_LABORAL.CODI_COND_LAB = '09' ) AND ")
    .append(" ( HIST_METAS_EMPLEADO.CODI_EMPL_PER = ? ) ");        

    private static final StringBuffer FIND_COUNT_RECIBO_REGISTRADO = new StringBuffer(" SELECT ")
    .append(" COUNT(NUME_COMP_REC) AS CONT_RECIBO ")
    .append(" FROM HIST_METAS_EMPLEADO ")
    .append(" WHERE CODI_EMPL_PER = ? AND ")
    .append(" LPAD(TRIM(NUME_SERI_REC),4,'0') = LPAD( TRIM( ? ),4,'0') AND ")
    .append(" LPAD(TRIM(NUME_COMP_REC),8,'0') = LPAD( TRIM( ? ),8,'0') ");
    
    //Cabecera de la Boleta de Haberes-SIGA
    private static final StringBuffer FIND_CABECERA_BOLETA =  new StringBuffer("Select MAESTRO_PERSONAL.CODI_EMPL_PER,APE_PAT_PER as apepat, APE_MAT_PER as apemat,NOM_EMP_PER as nombres,APE_PAT_PER ||' '  ||APE_MAT_PER ||', '||NOM_EMP_PER empleado,  ")
	 .append("(SELECT CODIGO_ANTERIOR FROM NIVELES WHERE CODI_NIVE_NVL = HIST_METAS_EMPLEADO.CODI_NIVE_NVL) CODIGO_CATEGORIA, 	 ")
	 .append("(SELECT DESC_NIVE_NVL FROM NIVELES WHERE CODI_NIVE_NVL = HIST_METAS_EMPLEADO.CODI_NIVE_NVL)  categoria, 	 ")
	 .append("LIBR_ELEC_PER as dni,")
	 .append("decode(HIST_METAS_EMPLEADO.tipo_plan_tpl,'01', HIST_METAS_EMPLEADO.FEC_INGRESO,'02', MAESTRO_PERSONAL.Fec_Ing_Per) as fingreso, ")
	 .append("(SELECT UNIDAD_ORGANIZACIONAL  FROM TDEPENDENCIAS WHERE CODI_DEPE_TDE = HIST_METAS_EMPLEADO.CODI_DEPE_TDE) CODIGO_UUOO  ,  ")
	 .append("(SELECT DESC_DEPE_TDE FROM TDEPENDENCIAS WHERE CODI_DEPE_TDE = HIST_METAS_EMPLEADO.CODI_DEPE_TDE)  uuoo,  ")
	 .append("HIST_METAS_EMPLEADO.codigo_banco,  ")
	 .append("to_char(HIST_METAS_EMPLEADO.fec_cese,'yyyy-mm-dd HH24:MI:SS') as fec_cese,  ")
	 .append("to_char(HIST_METAS_EMPLEADO.FEC_INGRESO,'yyyy-mm-dd HH24:MI:SS') as fec_ingreso,  ")
	 .append("(SELECT NOMB_BANC_BAN FROM BANCO WHERE CODI_BANC_BAN = HIST_METAS_EMPLEADO.CODIGO_BANCO)   banco_pago,  ")
	 .append("hist_metas_empleado.numero_cuenta_sueldo cuenta_pago,  ")
     .append(" decode(HIST_METAS_EMPLEADO.tipo_plan_tpl, '01', (case when COD_TIPOCTA =1 then 'CUENTA AHORROS' when COD_TIPOCTA =2 then 'CUENTA CORRIENTE' ELSE 'CUENTA MAESTRA' END), '02', (case when MAESTRO_PERSONAL.Tipo_Cuen_Per=1 then 'CUENTA AHORROS' when MAESTRO_PERSONAL.Tipo_Cuen_Per =2 then 'CUENTA CORRIENTE' ELSE 'CUENTA MAESTRA' END), '') as tipo_pago, ")	
  	 .append("hist_metas_empleado.codi_afp , (SELECT NOMB_AFP FROM TAFP WHERE  CODI_AFP = hist_metas_empleado.codi_afp)  afp,  ")
	 .append(" CODI_IPSS_PER as essalud, NUM_REGISTRO as codpers , MAESTRO_PERSONAL.CODI_AFP_PER as cuspp,tregimen_laboral.DESCRIPCION_CORTA as regimen,  HIST_METAS_EMPLEADO.REG_LAB_PER as cregimen, ")
	 .append(" nvl(HIST_METAS_EMPLEADO.Ind_Extorno,'0') ind_extorno ")
	 .append(" FROM MAESTRO_PERSONAL, HIST_METAS_EMPLEADO,tregimen_laboral  ")
	 .append(" WHERE tregimen_laboral.codigo(+) =HIST_METAS_EMPLEADO.REG_LAB_PER ")
	 .append("  and maestro_personal.codi_empl_per(+) = hist_metas_empleado.codi_empl_per ")
	 .append(" and HIST_METAS_EMPLEADO.codi_empl_per = ? ")
	 .append(" and HIST_METAS_EMPLEADO.tipo_plan_tpl = ? ")
	 .append(" and HIST_METAS_EMPLEADO.subt_plan_tpl = ? ")
	 .append(" and HIST_METAS_EMPLEADO.ano_peri_hme = ? ")
	 .append(" and HIST_METAS_EMPLEADO.nume_peri_hme =? ");
    
    //agonzalesf PAS20181U230200020 agregar fecha de inicio de datos historicos - CAS
    private static final StringBuffer FIND_CABECERA_BOLETA_CAS =  new StringBuffer("Select MAESTRO_PERSONAL.CODI_EMPL_PER,APE_PAT_PER as apepat, APE_MAT_PER as apemat,NOM_EMP_PER as nombres,APE_PAT_PER ||' '  ||APE_MAT_PER ||', '||NOM_EMP_PER empleado,  ")
	 .append("(SELECT CODIGO_ANTERIOR FROM NIVELES WHERE CODI_NIVE_NVL = HIST_METAS_EMPLEADO.CODI_NIVE_NVL) CODIGO_CATEGORIA, 	 ")
	 .append("(SELECT DESC_NIVE_NVL FROM NIVELES WHERE CODI_NIVE_NVL = HIST_METAS_EMPLEADO.CODI_NIVE_NVL)  categoria, 	 ")
	 .append("LIBR_ELEC_PER as dni,")	 
	 .append("CASE WHEN trim(HIST_METAS_EMPLEADO.ano_peri_hme) ||trim(HIST_METAS_EMPLEADO.nume_peri_hme) < ? THEN MAESTRO_PERSONAL.Fec_Ing_Per ELSE HIST_METAS_EMPLEADO.FEC_INGRESO   END as fingreso, ")  	 
	 .append("(SELECT UNIDAD_ORGANIZACIONAL  FROM TDEPENDENCIAS WHERE CODI_DEPE_TDE = HIST_METAS_EMPLEADO.CODI_DEPE_TDE) CODIGO_UUOO  ,  ")
	 .append("(SELECT DESC_DEPE_TDE FROM TDEPENDENCIAS WHERE CODI_DEPE_TDE = HIST_METAS_EMPLEADO.CODI_DEPE_TDE)  uuoo,  ")
	 .append("HIST_METAS_EMPLEADO.codigo_banco,  ")
	 .append("to_char(HIST_METAS_EMPLEADO.fec_cese,'yyyy-mm-dd HH24:MI:SS') as fec_cese,  ")
	 .append("to_char(HIST_METAS_EMPLEADO.FEC_INGRESO,'yyyy-mm-dd HH24:MI:SS') as fec_ingreso,  ")
	 .append("(SELECT NOMB_BANC_BAN FROM BANCO WHERE CODI_BANC_BAN = HIST_METAS_EMPLEADO.CODIGO_BANCO)   banco_pago,  ")
	 .append("hist_metas_empleado.numero_cuenta_sueldo cuenta_pago,  ")
     .append(" decode(HIST_METAS_EMPLEADO.tipo_plan_tpl, '01', (case when COD_TIPOCTA =1 then 'CUENTA AHORROS' when COD_TIPOCTA =2 then 'CUENTA CORRIENTE' ELSE 'CUENTA MAESTRA' END), '02', (case when MAESTRO_PERSONAL.Tipo_Cuen_Per=1 then 'CUENTA AHORROS' when MAESTRO_PERSONAL.Tipo_Cuen_Per =2 then 'CUENTA CORRIENTE' ELSE 'CUENTA MAESTRA' END), '') as tipo_pago, ")	
  	 .append("hist_metas_empleado.codi_afp , (SELECT NOMB_AFP FROM TAFP WHERE  CODI_AFP = hist_metas_empleado.codi_afp)  afp,  ")
	 .append(" CODI_IPSS_PER as essalud, NUM_REGISTRO as codpers , MAESTRO_PERSONAL.CODI_AFP_PER as cuspp,tregimen_laboral.DESCRIPCION_CORTA as regimen,  HIST_METAS_EMPLEADO.REG_LAB_PER as cregimen, ")
	 .append(" nvl(HIST_METAS_EMPLEADO.Ind_Extorno,'0') ind_extorno ")
	 .append(" FROM MAESTRO_PERSONAL, HIST_METAS_EMPLEADO,tregimen_laboral  ")
	 .append(" WHERE tregimen_laboral.codigo(+) =HIST_METAS_EMPLEADO.REG_LAB_PER ")
	 .append("  and maestro_personal.codi_empl_per(+) = hist_metas_empleado.codi_empl_per ")
	 .append(" and HIST_METAS_EMPLEADO.codi_empl_per = ? ")
	 .append(" and HIST_METAS_EMPLEADO.tipo_plan_tpl = ? ")
	 .append(" and HIST_METAS_EMPLEADO.subt_plan_tpl = ? ")
	 .append(" and HIST_METAS_EMPLEADO.ano_peri_hme = ? ")
	 .append(" and HIST_METAS_EMPLEADO.nume_peri_hme =? ");
    
    
    
    /* Se agrega para la nueva boleta actualiza el flag ind_consulta de la boleta avargascasq 20150721 */
    private static final StringBuffer UPDATE_IND_CONSULTA_HIST_EMPL = new StringBuffer("Update HIST_METAS_EMPLEADO set ind_consulta = ?, fec_consulta = ? ")
	 .append(" WHERE HIST_METAS_EMPLEADO.codi_empl_per = ? ")
	 .append(" and HIST_METAS_EMPLEADO.tipo_plan_tpl = ? ")
	 .append(" and HIST_METAS_EMPLEADO.subt_plan_tpl = ? ")
	 .append(" and HIST_METAS_EMPLEADO.ano_peri_hme = ? ")
	 .append(" and HIST_METAS_EMPLEADO.nume_peri_hme =? ");

    /* Se agrega para la nueva boleta actualiza el flag ind_consulta de la boleta avargascasq 20150721 */
    private static final StringBuffer FIND_IND_CONSULTA_HIST_EMPL = new StringBuffer("SELECT ind_consulta FROM HIST_METAS_EMPLEADO ")
	 .append(" WHERE HIST_METAS_EMPLEADO.codi_empl_per = ? ")
	 .append(" and HIST_METAS_EMPLEADO.tipo_plan_tpl = ? ")
	 .append(" and HIST_METAS_EMPLEADO.subt_plan_tpl = ? ")
	 .append(" and HIST_METAS_EMPLEADO.ano_peri_hme = ? ")
	 .append(" and HIST_METAS_EMPLEADO.nume_peri_hme =? ");
    
    /*Se agrega para la cabecera de la boleta por Compensacion de Tiempos de Servicios CTS  wrodriguezre 20160707*/
    /* PAS20171U230200037 -- agonzalesf -- Se agrega rubro de intereses por cts y se hace ajuste de consulta  */
    private static final StringBuffer FIND_LIQUIDACION_TIEMPO_SERVICIO_CTS = new StringBuffer("SELECT ")
    .append("(SELECT VALOR_CHAR_PAR FROM SYS_PARAMETROS WHERE NOM_VAR_PAR LIKE '%vgs_titulo%' AND ROWNUM  = 1) NOM_EMPRESA, ")
    .append("(SELECT VALOR_CHAR_PAR FROM SYS_PARAMETROS WHERE NOM_VAR_PAR = 'vgs_dir_boleta' AND PERI_ANNO_PAR = '9999') DIR_EMPRESA, ")
    .append("HME.TIPO_PLAN_TPL, ") 
    .append("HME.SUBT_PLAN_TPL, ")
    .append("HME.ANO_PERI_HME, ")
    .append("HME.NUME_PERI_HME, ")
    .append("HME.CODI_EMPL_PER, ")   
    .append("HME.NUM_REGISTRO numRegistro,   ")
    .append("MP.NOMB_CORT_PER nomCortoPer,   ")
    .append("HME.FEC_INGRESO fecIngreso, ")
    .append("NIV.DESC_NIVE_NVL DESC_NIVE_NVL, ")
    .append("TRIM(TDEP.UNIDAD_ORGANIZACIONAL) || '-' || TRIM(TDEP.DESC_DEPE_TDE) des_uuoo, ")
    .append("TRIM(TDEP.UNIDAD_ORGANIZACIONAL) uuoo, ")
    .append("SUBSTR(TDEP.UNIDAD_ORGANIZACIONAL,1,2) intendencia, ")
    .append("MP.LIBR_ELEC_PER, ")
    .append("HME.CODI_NIVE_NVL, ")
    .append("nvl(bc.nomb_banc_ban,'-') banco, ")
    .append("nvl(upper(trim(MON.DESC_MONE_MON)),'-') moneda, ")
    .append("nvl(trim(HME.NUM_CTA_CTS),'-')  num_cta_cts,   ")
    .append("CTS_EMP.NUM_DIAS_DCTO_ASI AS DIAS_NO_COMPUTABLE, ")
    .append("nvl(HME.MES_CTS_SERV,0) mesliquidar, ")
    .append("nvl(HME.NUM_DIACTS,0) diasliquidar,  ")
    .append("to_char(cts_emp.MTO_MESES_CTS,'99,900.99') AS MTO_LIQ_MES, ")
    .append("to_char(cts_emp.MTO_DIAS_CTS,'99,900.99') AS MTO_LIQ_DIA, ")
    .append("to_char(cts_emp.MTO_GRATI_CTS,'99,900.99') AS MTO_LIQ_GRATI, ")
    .append("to_char(cts_emp.MTO_REINT_CTS - nvl(v_interes_cts.monto, 0),'99,900.99') AS MTO_LIQ_REINT, ") //PAS20171U230200037 agonzalesf Ajuste de rubros 
    .append("to_char(nvl(cts_emp.MTO_MESES_CTS ,0) + nvl(cts_emp.MTO_DIAS_CTS ,0) + nvl(cts_emp.MTO_GRATI_CTS ,0) + nvl(cts_emp.MTO_REINT_CTS ,0),'99,900.99')  AS MTO_LIQ_TOTAL, ")
    .append("to_char(nvl(v_interes_cts.monto, 0),'99,900.99' ) as mto_interes_cts, ")    //PAS20171U230200037 agonzalesf Ajuste de rubros
    .append("TO_CHAR(TP.FECH_INIC_TPE ,'DD/MM/YYYY')  AS PERIODO_FECHA_INICIO, ")
    .append("TO_CHAR(TP.FECH_FINA_TPE,'DD/MM/YYYY')  AS PERIODO_FECHA_FINAL, ")
    .append("TO_CHAR(TP.FECHA_PAGO,'DD/MM/YYYY')  AS PERIDO_FECHA_PAGO ")
    .append("FROM HIST_METAS_EMPLEADO HME ")
    .append("INNER JOIN TPERIODOS TP ON HME.TIPO_PLAN_TPL = TP.TIPO_PLAN_TPL ")
    .append("AND HME.SUBT_PLAN_TPL = TP.SUBT_PLAN_TPL  ")
    .append("AND HME.ANO_PERI_HME =  TP.ANO_PERI_TPE ")
    .append("AND HME.NUME_PERI_HME =  TP.NUME_PERI_TPE ")
    .append("INNER JOIN MAESTRO_PERSONAL MP ON HME.CODI_EMPL_PER = MP.CODI_EMPL_PER ")
    .append("LEFT JOIN CTS_EMPLEADOS CTS_EMP ON HME.TIPO_PLAN_TPL = CTS_EMP.COD_TIP_PLANILLA ")
    .append("AND HME.SUBT_PLAN_TPL = CTS_EMP.COD_SUB_PLANILLA  ")
    .append("AND HME.ANO_PERI_HME = CTS_EMP.ANO_CTS  ")
    .append("AND HME.NUME_PERI_HME =  CTS_EMP.MES_CTS  ")
    .append("AND HME.CODI_EMPL_PER = CTS_EMP.CODI_EMPL_PER  ")
    .append("left join     (select  PH.CODI_EMPL_PER, ") 
    .append("   SUM(NVL(TO_NUMBER(SEG_CIFRADO.DESENCRIPTA(PH.MTO_VALORBASE_CIF, SEG_CIFRADO.SEGCIFRADO),'99999999999.99'),0) *   DECODE(C.TIPO_CONC_TCO,'1',1,'2',-1,0)) monto") 
    .append("   from planilla_historicas ph ") 
    .append("   inner join conceptos c on PH.CODI_CONC_TCO = C.CODI_CONC_TCO ")
    .append("   inner join acumu_conceptos ac on AC.CODI_ACUM_TAC = 'AC603' and AC.FLAG_USAR_TCA = '1' and  AC.CODI_CONC_TCO = PH.CODI_CONC_TCO ")                                              
    .append("   where PH.TIPO_PLAN_TPL = '06' ") 
    .append("   and PH.SUBT_PLAN_TPL = ? ") 
    .append("   and PH.ANO_PERI_TPE = ? ") 
    .append("   and PH.NUME_PERI_TPE = ?") 
    .append("   group by PH.CODI_EMPL_PER ) v_interes_cts on hme.codi_empl_per = v_interes_cts.codi_empl_per ")    //PAS20171U230200037 agonzalesf Ajuste de rubros
    .append("LEFT JOIN TDEPENDENCIAS TDEP ON HME.CODI_DEPE_TDE = TDEP.CODI_DEPE_TDE ")
    .append("LEFT JOIN NIVELES NIV ON HME.CODI_NIVE_NVL = NIV.CODI_NIVE_NVL ")
    .append("LEFT JOIN BANCO BC ON HME.COD_BANC_CTS = BC.CODI_BANC_BAN ")
    .append("LEFT JOIN MONEDA MON ON  HME.COD_MON_CTS  = MON.SIMB_MONE_MON ")
    .append("WHERE ( HME.TIPO_PLAN_TPL ='06') ")
    .append("AND (HME.SUBT_PLAN_TPL = ? ) ")
    .append("AND (HME.ANO_PERI_HME = ? ) ")
    .append("AND (HME.NUME_PERI_HME = ? ) ")
    .append("AND (HME.CODI_EMPL_PER LIKE ? )  ");
//    .append("AND (TDEP.UNIDAD_ORGANIZACIONAL LIKE ? ) ");                                      

    /*Se agrega, para la obtenciÃ³n de la cantidad de planillas del trabajdor wrodriguezre 12-07-2016*/
    private static final StringBuffer GET_PLANILLAS_EMPLEADO_CTS = new StringBuffer("SELECT ")
    .append("SUBT_PLAN_TPL NUMPLANILLAS ")
    .append("FROM  hist_metas_empleado hme ")
    .append("WHERE HME.CODI_EMPL_PER= ?  ")
    .append("AND HME.TIPO_PLAN_TPL='06' ")
    .append("AND HME.NUME_PERI_HME= ? ")
    .append("AND HME.ANO_PERI_HME= ?  ");
    
    
  
   
  
    
	//AGONZALESF -PAS20171U230200001 - solicitud de reintegro  
	private static final StringBuffer SELECT_MES_ANIO = new StringBuffer("SELECT p.ano_peri_tpe anio ,p.nume_peri_tpe nro_mes, p.desc_peri_tpe nombre_mes FROM tperiodos p WHERE p.tipo_plan_tpl = ? AND p.subt_plan_tpl = '1'" )
			//AGONZALESF -PAS20171U230200033 - solicitud de reintegro  no se requiere validar que sea menor a hoy
			//.append(" and to_char( p.fech_inic_tpe,'yyyymm')< to_char(sysdate,'yyyymm') ")
			.append(" and to_char( p.fech_inic_tpe,'yyyymm')> to_char(add_months(sysdate,-13) ,'yyyymm')") 
			.append(" and p.esta_plan_tpe IN ('2', 'C') ")
			.append(" ORDER BY ANO_PERI_tpe DESC , NUME_PERI_tpe DESC"); 
	

	//AGONZALESF -PAS20181U230200067 - solicitud de reintegro, planillas adicionales
	//AGONZALESF -PAS20191U230200011 - ajuste de query para limitar fecha de ingreso del colaborador
	private static final StringBuffer SELECT_PLANILLAS_POR_COLABORADOR = new StringBuffer("" )	
	.append(" SELECT p.ano_peri_tpe   anio,")
	.append("        p.nume_peri_tpe  nro_mes,")
	.append("        p.tipo_plan_tpl  tipo_plan,")
	.append("        p.subt_plan_tpl  sub_plan,")
	.append("        p.desc_peri_tpe  nombre_mes,")
	.append("        sp.desc_subt_stp nombre_sub_plan")
	.append("   from HIST_METAS_EMPLEADO H, tperiodos p, subtplanilla sp")
	.append("  WHERE h.ano_peri_hme = p.ano_peri_tpe")
	.append("    AND h.nume_peri_hme = p.nume_peri_tpe")
	.append("    AND h.tipo_plan_tpl = p.tipo_plan_tpl")
	.append("    AND h.subt_plan_tpl = p.subt_plan_tpl")
	.append("    AND p.subt_plan_tpl = sp.subt_plan_stp")
	.append("    AND p.tipo_plan_tpl = sp.tipo_plan_tpl")
	.append("   AND h.codi_empl_per = ? ")
	.append("   AND h.tipo_plan_tpl = ? ")
	.append("    AND p.esta_plan_tpe IN ('2')")
	.append("    AND to_char(p.fech_inic_tpe, 'yyyymm') > to_char(add_months (sysdate, - ?), 'yyyymm')")
	.append("    AND to_char(p.fech_inic_tpe, 'yyyymm') > (SELECT to_char(m.fec_ing_per, 'yyyymm')   FROM maestro_personal m WHERE codi_empl_per = ? )")
	.append("    AND h.reg_lab_per = (SELECT m.reg_lab_per FROM maestro_personal m WHERE codi_empl_per = ? )  ")
	.append("  ORDER BY ANO_PERI_HME DESC, NUME_PERI_HME DESC");
	
	
	private static final StringBuffer SELECT_LIM_PLANILLAS_POR_COLABORADOR = new StringBuffer("" )	
	.append(" select DESC_LARGA from t01parametro   where cod_parametro = '1237' ")
	.append("and cod_tipo = 'D'  and cod_argumento = '01' and  cod_estado =1	");
			
	
	
	//AGONZALESF -PAS20171U230200001 - solicitud de reintegro   si existe planilla cas o haberes
	//AGONZALESF -PAS20181U230200067 - solicitud de reintegro, planillas adicionales
	private static final StringBuffer SELECT_MES_ANIO_VALIDADO = new StringBuffer("")
    .append(" SELECT h.ANO_PERI_HME anio,")
	.append("        h.NUME_PERI_HME nro,")
	.append("        h.tipo_plan_tpl,")
	.append("        h.subt_plan_tpl,")
	.append("       TO_CHAR(p.fech_inic_tpe, 'yyyymm') ma_evaluado,")
	.append("       TO_CHAR(m.fec_ing_per, 'yyyymm') ma_ingreso ")
	.append(" from HIST_METAS_EMPLEADO H, tperiodos p, maestro_personal m")
	.append(" WHERE h.codi_empl_per = m.codi_empl_per")
	.append(" AND h.ano_peri_hme = p.ano_peri_tpe")
	.append(" AND h.nume_peri_hme = p.nume_peri_tpe")
	.append(" AND h.tipo_plan_tpl = p.tipo_plan_tpl")
	.append(" AND h.subt_plan_tpl = p.subt_plan_tpl")
	.append(" AND h.codi_empl_per = ? ")
	.append(" AND h.ano_peri_hme = ?")
	.append(" AND h.nume_peri_hme = ?")
	.append(" AND h.tipo_plan_tpl = ?")
	.append(" AND h.subt_plan_tpl = ?")
	.append(" AND h.tipo_plan_tpl in ('01', '02')")
	.append(" ORDER BY ANO_PERI_HME DESC, NUME_PERI_HME DESC");  
	
 
	
	
	//AGONZALESF -PAS20171U230200001 - solicitud de reintegro   : Ultima planilla
	private static final String SELECT_ULTIMA_PLANILLA = "select num_registro, reg_lab_per ,tipo_plan_tpl , subt_plan_tpl ,ano_peri_hme,nume_peri_hme "
			+ "from  hist_metas_empleado where codi_empl_per = ? and   subt_plan_tpl='1' and ano_peri_hme ||'-' || nume_peri_hme "
			+ "like ( select   max(ano_peri_hme ||'-' || nume_peri_hme)   from hist_metas_empleado hm where codi_empl_per = ? and hm.subt_plan_tpl='1')";



	//AGONZALESF -PAS20171U230200001 - solicitud de reintegro   : planilla actual
	private static final String SELECT_PLANILLA	="select num_registro, reg_lab_per ,tipo_plan_tpl , subt_plan_tpl ,ano_peri_hme,nume_peri_hme "
			+ "from  hist_metas_empleado where   codi_empl_per = ? and   subt_plan_tpl='1' and	 TRIM(ano_peri_hme)   = ? and TRIM(nume_peri_hme)   = ?";
			 

	//AGONZALESF -PAS20171U230200001 - solicitud de reintegro   : planilla actual
	private static final String SELECT_PLANILLA_INCLUIDO_SUBTIPOS	="select num_registro, reg_lab_per ,tipo_plan_tpl , subt_plan_tpl ,ano_peri_hme,nume_peri_hme "
			+ "from  hist_metas_empleado where   "
			+ "codi_empl_per = ? "			
			+ "and TRIM(ano_peri_hme)   = ? "
			+ "and TRIM(nume_peri_hme)   = ?  "
			+ "and tipo_plan_tpl= ? " 
			+ "and subt_plan_tpl= ? ";
			 
	//PAS20171U230200001 - solicitud de reintegro   : Ultima planilla por trabajador detallada
	private static final StringBuffer SELECT_ULT_PLAN_PERIODO = new StringBuffer("select hm.tipo_plan_tpl, hm.subt_plan_tpl, hm.ano_peri_hme, hm.nume_peri_hme, st.desc_subt_stp ")
			 															 .append("from  hist_metas_empleado hm, subtplanilla st , tperiodos p ") 
			 															 .append("where hm.codi_empl_per = (select codi_empl_per from maestro_personal where numero_registro_alterno = ?) and ") 
			 															 .append("hm.ano_peri_hme ||'-' || hm.nume_peri_hme like ( select max(ano_peri_hme ||'-' || nume_peri_hme) ")
			 															 .append("                                                 from hist_metas_empleado hm where codi_empl_per = (select codi_empl_per from maestro_personal where numero_registro_alterno = ?) ) and ")
			 															 .append("st.tipo_plan_tpl = hm.tipo_plan_tpl and st.subt_plan_stp = hm.subt_plan_tpl and st.subt_plan_stp = hm.subt_plan_tpl and st.subt_plan_stp = '1' and ")
			 															 .append("p.tipo_plan_tpl = hm.tipo_plan_tpl and p.subt_plan_tpl = hm.subt_plan_tpl and p.ano_peri_tpe = hm.ano_peri_hme and p.nume_peri_tpe = hm.nume_peri_hme and ")
			 															 .append("(p.fech_cier_tpe is null or p.fech_cier_tpe >= ?)");
			 		
    /**
   * Método para validar el Datasource a usarse
   * @param Object datasource
   */
  public HistMetasEmpleaDAO(Object datasource) {
    if (datasource instanceof DataSource)
      this.datasource = (DataSource)datasource;
    else if (datasource instanceof String)
      this.datasource = getDataSource((String)datasource);
    else
      throw new DAOException(this, "Datasource no valido");
  }
  
  /**Metodo que obtiene los datos de las cabeceras, pera la boleta CTS
   * @author wrodriguezre 12-07-2016
   *@return Map de datos  
  */
  public Map listCabeceraBoletaCTS(Map params)throws DAOException{
		if(log.isDebugEnabled()) log.debug("getCabeceraBoletaCTS");
		
	  	Map datosBoleta = new HashMap();
	  	StringBuffer strSQL=null;
			  	strSQL=new StringBuffer(FIND_LIQUIDACION_TIEMPO_SERVICIO_CTS.toString());
			  	datosBoleta=executeQueryUniqueResult(datasource, strSQL.toString(),
//			  	new Object[]{params.get("subPlanilla"),params.get("anioPeriodo"),params.get("numPeriodo"),params.get("empleado"),params.get("unidadOrg")});
//			  	new Object[]{params.get("subPlanilla"),params.get("anioPeriodo"),params.get("numPeriodo"),params.get("empleado")});
			  	new Object[]{params.get("subPlanilla"),params.get("anioPeriodo"),params.get("numPeriodo"),params.get("subPlanilla"),params.get("anioPeriodo"),params.get("numPeriodo"),params.get("empleado")}); //PAS20171U230200037 agonzalesf Ajuste de rubros 
		return datosBoleta;
  }
  /**Metodo que obtiene la cantidad de planillas, del trabajador
   * @author wrodriguezre
   * @param: codigo del empleado, numero del periodo
   * @return Map planillas
   */
  public Map getPlanillasEmpleado(Map params)throws DAOException{
	  if(log.isDebugEnabled()) log.debug("getPlanillas");
	  
	  log.debug("params[]->"+params);
	  Map numPlanillas=new HashMap();
	  StringBuffer strSQL=null;
	  			 strSQL=new StringBuffer(GET_PLANILLAS_EMPLEADO_CTS.toString());
	  			 numPlanillas=executeQueryUniqueResult(datasource, strSQL.toString(),new Object[]{params.get("empleado"),params.get("numPeriodo"),params.get("annio")});
	  return numPlanillas;
  }
  
  /**
   * Obtiene los datos del Recibo por Honorarios Historicos y del Formulario de Suspensión para
   * el Reporte de Liquidacion de Pago CAS
   * @param params
   * @return Map datos del recibo CAS
   * @throws DAOException
   */
  public Map findByReciboHistorico(Map params) throws DAOException {
	if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - findByReciboHistorico()"));
    Map datosRecibo = new HashMap();    
    StringBuffer strSQL = null;
    strSQL = new StringBuffer(FIND_RECIBO_CAS_HISTORICO.toString());
    datosRecibo = executeQueryUniqueResult(datasource, strSQL.toString(),
    new Object[]{params.get("anno"), params.get("mes"), params.get("codi_empl_per")});
    if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - findByReciboHistorico() "));  
    return datosRecibo;
  }
  
  /**
   * Valida si la serie y recibo del colaborador ya está registrado en el histórico
   * @param params Map
   * @return Map datos
   * @throws DAOException
   */
  public int findByReciboRegistrado(Map params) throws DAOException {    
	if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - findByReciboRegistrado()"));
	int contador = 0;
	if (log.isDebugEnabled()) log.debug("Parametros:: " + params.get("codi_empl_per") +"|"+ params.get("txtSerieR") +"|"+ params.get("txtNumeroR"));
    Map result = executeQueryUniqueResult(datasource, FIND_COUNT_RECIBO_REGISTRADO.toString(),
    new Object[]{params.get("codi_empl_per"), params.get("txtSerieR"), params.get("txtNumeroR")});
    if (log.isDebugEnabled()) log.debug("resultado:: " + result);
    if (result != null)
    	contador = (new Integer(result.get("cont_recibo").toString())).intValue();
    if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - findByReciboRegistrado() ")); 
    return contador;
  }
  
  /**
	 * Obtiene los datos de la cabecera de la Boleta por registro de personal y periodo
	 * 
	 * @param params
	 * @return Map Cabecera
	 * @throws DAOException
	 */
	
	public Map findByCabeceraBoleta(Map params) throws DAOException {
		if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - findByCabeceraBoleta()"));
		
		if (log.isDebugEnabled()) log.debug(this.toString().concat(" params Boleta Cab - "+params));
	
		Map cabecera = new HashMap();		
		try {
		
			//agonzalesf PAS20181U230200020 agregar fecha de inicio de datos historicos - CAS
			StringBuffer strSQL = null;
			if(params.get("tipo_plan_tpl").equals("01")){
				  strSQL = new StringBuffer(FIND_CABECERA_BOLETA.toString());
		    cabecera = executeQueryUniqueResult(datasource, strSQL.toString(),
					new Object[]{ params.get("codi_empl_per"),params.get("tipo_plan_tpl"), params.get("subtipoplan"),params.get("anio"), params.get("mes")});
				 
			}
			if(params.get("tipo_plan_tpl").equals("02")){
				if (log.isDebugEnabled()) log.debug("CAS :  fec_ini_hist -> " + params.get("fec_ini_hist"));
				 strSQL = new StringBuffer(FIND_CABECERA_BOLETA_CAS.toString());
				  cabecera = executeQueryUniqueResult(datasource, strSQL.toString(),
							new Object[]{ params.get("fec_ini_hist") ,params.get("codi_empl_per"),params.get("tipo_plan_tpl"), params.get("subtipoplan"),params.get("anio"), params.get("mes")});
			}
		    
		    if (log.isDebugEnabled()) log.debug("parametros: "+params.get("codi_empl_per")+params.get("tipo_plan_tpl")+" "+params.get("subtipoplan")+" "+params.get("anio")+" "+params.get("mes"));
		    	
		} catch(Exception e) {
			StringBuffer msg = new StringBuffer()
			.append(" ERROR - findByCabeceraBoleta() : ").append(e.toString());
			if (log.isDebugEnabled()) log.debug(this.toString() + msg.toString());
			throw new DAOException(this, msg.toString());
		}
		if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - findByCabeceraBoleta() "));
		return cabecera;
	}


	  /**
	   * Actualiza el ind_consulta de boleta de la tabla historica empleado 
	   * @param params Map
	   * @return 
	   * @throws DAOException
	   */
	  public void actualizaIndConsultaByCabeceraBoleta(Map params) throws DAOException {    
		if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - actualizaIndConsultaByCabeceraBoleta() params: " + params));
		try {
		    StringBuffer strSQL = new StringBuffer(UPDATE_IND_CONSULTA_HIST_EMPL.toString());
		
		    executeUpdate(datasource, strSQL.toString(),
					new Object[]{ params.get("ind_consulta"),params.get("fec_consulta"),params.get("codi_empl_per"),params.get("tipo_plan_tpl"), params.get("subtipoplan"),params.get("anio"), params.get("mes")});
		    if (log.isDebugEnabled()) log.debug("parametros: "+params.get("codi_empl_per")+params.get("tipo_plan_tpl")+" "+params.get("subtipoplan")+" "+params.get("anio")+" "+params.get("mes"));
		    	
		} catch(Exception e) {
			StringBuffer msg = new StringBuffer()
			.append(" ERROR - actualizaIndConsultaByCabeceraBoleta() : ").append(e.toString());
			if (log.isDebugEnabled()) log.debug(this.toString() + msg.toString());
			throw new DAOException(this, msg.toString());
		}
		if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - actualizaIndConsultaByCabeceraBoleta() "));
		return ;
	  }

	  /**
	   * Actualiza el ind_consulta de boleta cts de la tabla historica empleado 
	   * @param params Map
	   * @return 
	   * @throws DAOException
	   */
	  public void actualizaIndConsultaByCabeceraBoletaCTS(Map params) throws DAOException {    
		if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - actualizaIndConsultaByCabeceraBoleta() params: " + params));
		try {
		    StringBuffer strSQL = new StringBuffer(UPDATE_IND_CONSULTA_HIST_EMPL.toString());
		
		    executeUpdate(datasource, strSQL.toString(),
					new Object[]{ params.get("ind_consulta"),params.get("fec_consulta"),params.get("codi_empl_per"),params.get("tipo_plan_tpl"), params.get("subtipoplan"),params.get("anio"), params.get("mes")});
		   
		    if (log.isDebugEnabled()) log.debug("parametros: "+params.get("codi_empl_per")+params.get("tipo_plan_tpl")+" "+params.get("subtipoplan")+" "+params.get("anio"));
		    	
		} catch(Exception e) {
			StringBuffer msg = new StringBuffer()
			.append(" ERROR - actualizaIndConsultaByCabeceraBoletaCTS() : ").append(e.toString());
			if (log.isDebugEnabled()) log.debug(this.toString() + msg.toString());
			throw new DAOException(this, msg.toString());
		}
		if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - actualizaIndConsultaByCabeceraBoletaCTS() "));
		return ;
	  }

//	    private static final StringBuffer UPDATE_IND_CONSULTA_HIST_EMPL = new StringBuffer("Update HIST_METAS_EMPLEADO set ind_consulta = ?, fec_consulta = ? ")
//		 .append(" WHERE HIST_METAS_EMPLEADO.codi_empl_per = ? ")
//		 .append(" and HIST_METAS_EMPLEADO.tipo_plan_tpl = ? ")
//		 .append(" and HIST_METAS_EMPLEADO.subt_plan_tpl = ? ")
//		 .append(" and HIST_METAS_EMPLEADO.ano_peri_hme = ? ")
//		 .append(" and HIST_METAS_EMPLEADO.nume_peri_hme =? ");
	  
	  /**
	   * Obtiene los datos del Recibo por Honorarios Historicos y del Formulario de Suspensiï¿½n para
	   * el Reporte de Liquidaciï¿½n de Pago CAS
	   * @param params
	   * @return Map datos del recibo CAS
	   * @throws DAOException
	   */
	  public Map findByIndConsulta(Map params) throws DAOException {
		if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - findByIndConsulta()"));
	    Map datosRecibo = new HashMap();    
	    StringBuffer strSQL = null;
	    strSQL = new StringBuffer(FIND_IND_CONSULTA_HIST_EMPL.toString());
	    datosRecibo = executeQueryUniqueResult(datasource, strSQL.toString(),
				new Object[]{params.get("codi_empl_per"),params.get("tipo_plan_tpl"), params.get("subtipoplan"),params.get("anio"), params.get("mes")});
	    if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - findByIndConsulta() "));  
	    return datosRecibo;
	  }
	  
	 

	//AGONZALESF -PAS20171U230200001 - solicitud de reintegro  	  
	/**
	 * Funcion para encontrar meses y años para el registro de solicitud de reintegro
	 * @param filtro
	 * @return
	 * @throws SQLException
	 */
	public List findAnioMesSolReintegro(Map filtro) throws SQLException {
		if (log.isDebugEnabled())
			log.debug(this.toString().concat(" INICIO - findAnioMesSolReintegro()"));
		List mesanio = executeQuery(datasource, SELECT_MES_ANIO.toString(), new Object[] { filtro.get("tipo_plan_tpl") });
		if (log.isDebugEnabled())
			log.debug(this.toString().concat(" FIN - findAnioMesSolReintegro() "));
		return mesanio;
	}

	//AGONZALESF -PAS20181U230200067 - solicitud de reintegro, planillas adicionales
	//AGONZALESF -PAS20191U230200011 - ajuste de query para limitar fecha de ingreso del colaborador
	/**
	 * Funcion para encontrar planillas para solicitud de reintegro
	 * @param filtro
	 * @return
	 * @throws SQLException
	 */
	public List findPlanillasSolReintegro(Map filtro, Integer limMeses) throws SQLException {
		if (log.isDebugEnabled())
			log.debug(this.toString().concat(" INICIO - findPlanillasSolReintegro()"));
		List planillas = executeQuery(datasource, SELECT_PLANILLAS_POR_COLABORADOR.toString(), new Object[] { filtro.get("codi_empl_per"),filtro.get("tipo_plan_tpl"), limMeses ,filtro.get("codi_empl_per"),filtro.get("codi_empl_per") });
		if (log.isDebugEnabled())
			log.debug(this.toString().concat(" FIN - findPlanillasSolReintegro() "));
		return planillas;
	}

	//AGONZALESF -PAS20181U230200067 - solicitud de reintegro, planillas adicionales
	/**
	 * Funcion para obtener limite de meses para planillas para solicitud de  reintegro 
	 * @param filtro
	 * @return
	 * @throws SQLException
	 */
	public Map findLimitePlanillasSolReintegro() throws SQLException {
		if (log.isDebugEnabled())
			log.debug(this.toString().concat( " INICIO - findLimitePlanillasSolReintegro()"));
		Map limite = executeQueryUniqueResult(datasource, SELECT_LIM_PLANILLAS_POR_COLABORADOR.toString());
		if (log.isDebugEnabled())
			log.debug(this.toString().concat( " FIN - findLimitePlanillasSolReintegro() "));
		return limite;
	}
		

	//AGONZALESF -PAS20171U230200001 - solicitud de reintegro  	  
	//AGONZALESF -PAS20181U230200067 - solicitud de reintegro, planillas adicionales
	/**
	 * Funcion para encontrar la planilla (CAS o Haberes) de un trabajador por mes y año 
	 * @param filtro
	 * @return
	 * @throws SQLException
	 */
	public Map findPlanillaByAnioNumero(Map filtro) throws SQLException {
		if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - findPlanillaByAnioNumero()"));
		Map planilla = executeQueryUniqueResult(datasource, SELECT_MES_ANIO_VALIDADO.toString(), new Object[] { 
				filtro.get("codi_empl_per"), 
				filtro.get("anio"),
				filtro.get("mes"),
				filtro.get("tipoPlanilla"),
				filtro.get("subtipoPlanilla")
				});
		if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - findPlanillaByAnioNumero() "));
		return planilla;
	}

	//PAS20171U230200001 - solicitud de reintegro  	
	/**
	 * Ultima planilla  de un trabajador
	 * @param filtro
	 * @return
	 * @throws SQLException 
	 */
	public Map findUltimaPlanilla(Map filtro) throws SQLException { 
		if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - findUltimaPlanilla()"));
		Map planilla = executeQueryUniqueResult(datasource, SELECT_ULTIMA_PLANILLA.toString(), new Object[] { 
				filtro.get("codi_empl_per"), 
				filtro.get("codi_empl_per")
				});
		if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - findUltimaPlanilla() "));
		return planilla; 
	}

	//AGONZALESF -PAS20171U230200001 - solicitud de reintegro  	
	/**
	 * Planilla consultada por trabajador , anio y numero 
	 * @param filtro
	 * @return
	* @throws SQLException 
	 */
	public Map findPlanilla(Map filtro) throws SQLException {
		if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - findPlanilla()"));
		Map planilla = executeQueryUniqueResult(datasource, SELECT_PLANILLA.toString(), new Object[] { 
				filtro.get("codi_empl_per"), 
				filtro.get("anio"),
				filtro.get("mes")
				});
		if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - findPlanilla() "));
		return planilla;  
	}

	//AGONZALESF -PAS20181U230200067 - solicitud de reintegro, planillas adicionales
	/**
	 * Planilla consultada por trabajador , anio y numero 
	 * @param filtro
	 * @return
	* @throws SQLException 
	 */
	public Map findPlanillaIncSubtipos(Map filtro) throws SQLException {
		if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - findPlanilla()"));
		Map planilla = executeQueryUniqueResult(datasource, SELECT_PLANILLA_INCLUIDO_SUBTIPOS.toString(), new Object[] { 
				filtro.get("codi_empl_per"), 
				filtro.get("anio"),
				filtro.get("mes"),
				filtro.get("tipoPlanilla"),
				filtro.get("subtipoPlanilla")
				});
		if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - findPlanilla() "));
		return planilla;  
	}

         //PAS20171U230200001 - solicitud de reintegro
	/**
	 * Lista ultima planilla abierta para el trabajador 
	 * @param params
	 * @return
	 * @throws DAOException
	 */
	public Map findUltPlanPeriodo(Map params) throws DAOException {
		  if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - findUltPlanPeriodo()"));
		Map res = executeQueryUniqueResult(datasource, SELECT_ULT_PLAN_PERIODO.toString(),
	      new Object[]{
	    	  params.get("codPers"), 
	    	  params.get("codPers"), 
	          params.get("fecha") });
	      if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - findUltPlanPeriodo() "));           
		return res;
	}
		  
		 
}

