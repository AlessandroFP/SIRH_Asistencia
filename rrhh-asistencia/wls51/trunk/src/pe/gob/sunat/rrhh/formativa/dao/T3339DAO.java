package pe.gob.sunat.rrhh.formativa.dao;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.bean.MensajeBean;
import pe.gob.sunat.framework.core.bean.ParamBean;
import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.core.dao.interfaz.BeanMapper;
import pe.gob.sunat.framework.core.pattern.DynaBean;
import pe.gob.sunat.framework.util.cache.dao.ParamDAO;
import pe.gob.sunat.framework.util.date.FechaBean;
import java.util.HashMap;//icapunay 18/10/2011 - metodos requeridos por clase AsisForFacade
import pe.gob.sunat.utils.Utiles;//icapunay 18/10/2011 - metodos requeridos por clase AsisForFacade

/**
 * <p> Title : T3339DAO</p>
 * <p>Description : Realiza operaciones sobra la tabla T3339PERMODFOR </p>
 * <p>Copyright   : Copyright (c) 2007</p>
 * <p>Company     : COMSA </p>
 * @author PAUL ROMERO (COMSA)   
 * @version 1.0 
 */

public class T3339DAO extends DAOAbstract {
  
	private StringBuffer QUERY100_SENTENCE = new StringBuffer(" select " )
	.append("A.cod_est_per,B.cod_local,B.cod_uorga,C.t12des_uorga,C.cod_dpto,")
	.append("B.fec_ini_conv,B.fec_fin_conv,B.cod_especialidad,")
	.append("A.cod_situacion,B.cod_tip_plaza,B.cod_modalidad,A.cod_registro,")
	.append("A.num_doc,A.nom_ape_pat,A.nom_ape_mat,A.nom_per,")
	.append("A.fec_nacimiento,A.cod_sexo,A.cod_est_civil,B.num_ruc_cenform,")
	.append("B.cod_nivel_edu, B.cod_turno,B.num_horas,B.fec_ini_conv,B.fec_fin_conv,")
	.append("A.fec_nacimiento, B.cod_ent_fin, B.num_cuenta from T3339PERMODFOR A, T3340CONVENIO B, T12UORGA C ");
	
	private StringBuffer QUERY1_SENTENCE = new StringBuffer(" SELECT " )
	.append("P.COD_PER_MODFOR,P.COD_REGISTRO, P.COD_TIPDOC, P.NUM_DOC, " )
	.append("P.NOM_APE_PAT, P.NOM_APE_MAT,P.NOM_PER,P.FEC_NACIMIENTO,")
	.append("P.COD_SEXO,P.COD_NACIONALIDAD,P.COD_UBIGEO_NAC,P.COD_EST_CIVIL,")
	.append("P.IND_DISCAPACIDAD, P.DES_DISCAPACIDAD, IND_DOMICILIADO, " ) 
	.append("P.NUM_TELEFONO,NUM_CELULAR,DIR_CORREO,P.COD_TIPO_VIA,P.NOM_VIA,")
	.append("P.NUM_VIA,P.NUM_INTERIOR,P.COD_TIPO_ZONA,P.NOM_ZONA,")
	.append("P.DES_REF_DOM,P.COD_UBIGEO_DOM,OBS_PERSONAL,IND_MADRE_RESP,")
	.append("COD_SITUACION,P.COD_EST_PER,P.FEC_INI,P.FEC_FIN, P.COD_USUMODIF,")
	.append("P.FEC_MODIF,'' AS COD_UORGA,'' AS COD_MODALIDAD," ) 
	.append("T.COD_TIPDOC AS ")
	.append("COD_TIPDOC_TUTOR,T.NUM_DOC AS NUM_DOC_TUTOR," )
	.append("COD_TIP_PARENT AS COD_TIP_PARENT,")
	.append("NOM_APEPAT_TUTOR AS NOM_APEPAT_TUTOR,NOM_APEMAT_TUTOR ")
	.append("AS NOM_APEMAT_TUTOR,T.NOM_TUTOR AS NOM_TUTOR, ")
	.append("T.NUM_TELEFONO AS NUM_TELEFONO_TUTOR,") 
	.append("T.COD_TIPO_VIA AS COD_TIPO_VIA_TUTOR,T.NOM_VIA AS ") 
	.append("NOM_VIA_TUTOR,T.NUM_VIA AS NUM_VIA_TUTOR," )
	.append("T.NUM_INTERIOR AS NUM_INTERIOR_TUTOR,T.COD_TIPO_ZONA")
	.append(" AS COD_TIP_ZONA_TUT, T.NOM_ZONA AS ")
	.append("NOM_ZONA_TUTOR,T.DES_REF_DOM AS DES_REF_DOM_TUTOR,")
	.append("T.COD_UBIGEO_DOM AS COD_UBI_DOM_TUT," )
	.append("T.COD_USUMODIF AS COD_USU_MOD_TUT,T.FEC_MODIF AS ")
	.append("FEC_MODIF_TUTOR ");
	
	private StringBuffer QUERY11_SENTENCE = new StringBuffer(" SELECT " )
	.append("P.COD_PER_MODFOR,P.COD_REGISTRO,P.COD_TIPDOC,P.NUM_DOC," )
	.append("P.NOM_APE_PAT, P.NOM_APE_MAT,P.NOM_PER, P.FEC_NACIMIENTO, " )
	.append("P.COD_SEXO, P.COD_NACIONALIDAD,P.COD_UBIGEO_NAC,P.COD_EST_CIVIL,")
	.append("P.IND_DISCAPACIDAD, P.DES_DISCAPACIDAD, IND_DOMICILIADO,") 
	.append("P.NUM_TELEFONO,NUM_CELULAR,DIR_CORREO,P.COD_TIPO_VIA,P.NOM_VIA,")
	.append("P.NUM_VIA,P.NUM_INTERIOR,P.COD_TIPO_ZONA,P.NOM_ZONA,")
	.append("P.DES_REF_DOM,P.COD_UBIGEO_DOM,OBS_PERSONAL,IND_MADRE_RESP,")
	.append("COD_SITUACION,P.COD_EST_PER,P.FEC_INI,P.FEC_FIN, P.COD_USUMODIF,")
	.append("P.FEC_MODIF,C.NUM_CONVENIO,C.COD_UORGA,C.COD_MODALIDAD," ) 
	.append("C.COD_EST_CONV,T.COD_TIPDOC AS COD_TIPDOC_TUTOR," )
	.append("T.NUM_DOC AS NUM_DOC_TUTOR,COD_TIP_PARENT AS " )
	.append("COD_TIP_PARENT,NOM_APEPAT_TUTOR AS NOM_APEPAT_TUTOR,")
	.append("NOM_APEMAT_TUTOR AS NOM_APEMAT_TUTOR,T.NOM_TUTOR AS ")
	.append("NOM_TUTOR,T.NUM_TELEFONO AS NUM_TELEFONO_TUTOR,")
	.append("T.COD_TIPO_VIA AS COD_TIPO_VIA_TUTOR, T.NOM_VIA ")
	.append("AS NOM_VIA_TUTOR,T.NUM_VIA AS NUM_VIA_TUTOR, " ) 
	.append("T.NUM_INTERIOR AS NUM_INTERIOR_TUTOR, " )
	.append("T.COD_TIPO_ZONA AS COD_TIP_ZONA_TUT,")
	.append("T.NOM_ZONA AS NOM_ZONA_TUTOR, T.DES_REF_DOM  AS ")
	.append("DES_REF_DOM_TUTOR,T.COD_UBIGEO_DOM AS ")
	.append("COD_UBI_DOM_TUT,T.COD_USUMODIF AS " )
	.append("COD_USU_MOD_TUT, T.FEC_MODIF AS FEC_MODIF_TUTOR ");
	
	private StringBuffer QUERY2_SENTENCE = new StringBuffer(" INSERT " )
	.append("INTO T3339PERMODFOR (")
	.append(" cod_per_modfor    ,       " )
	.append(" cod_registro   ,     " )
	.append(" cod_tipdoc       ,     " )
	.append(" num_doc         ,     " )
	.append(" nom_ape_pat       ,     " )
	.append(" nom_ape_mat       ,     " )
	.append(" nom_per         ,     " )
	.append(" fec_Nacimiento   ,      " )
	.append(" ind_madre_resp   ,     " )
	.append(" cod_ubigeo_nac   ,     " )
	.append(" cod_nacionalidad ,     " )
	.append(" cod_sexo       ,     " )
	.append(" cod_est_civil   ,     " )
	.append(" ind_domiciliado   ,     " )
	.append(" ind_discapacidad ,     " )
	.append(" des_discapacidad ,     " )
	.append(" num_telefono   ,     " )
	.append(" num_celular       ,     " )
	.append(" dir_correo       ,     " )
	.append(" obs_personal   ,     " )
	.append(" cod_tipo_via   ,     " )
	.append(" nom_via         ,     " )
	.append(" num_via         ,     " )
	.append(" num_interior   ,     " )
	.append(" cod_tipo_zona   ,     " )
	.append(" nom_zona       ,     " )
	.append(" des_ref_dom       ,     " )
	.append(" cod_ubigeo_dom   ,     " )
	.append(" cod_usumodif   ,     " )
	.append(" fec_modif        ,     " )
	.append(" cod_est_per       ,     " )
	.append(" cod_situacion     ) VALUES (" )
	.append(" ?,?,?,?,?,?,?,?,?,?,?, " )
	.append(" ?,?,?,?,?,?,?,?,?,?, " )
	.append(" ?,?,?,?,?,?,?,?,?,?,?) " );
	
	private StringBuffer QUERY4_SENTENCE = new StringBuffer("SELECT " )
	.append("MAX(COD_PER_MODFOR) AS MAXIMO FROM T3339PERMODFOR ");
	
	
	private StringBuffer QUERY6_SENTENCE = new StringBuffer("SELECT DISTINCT ")
	.append("COD_REGISTRO FROM T3339PERMODFOR WHERE COD_REGISTRO =? " );
	
	private StringBuffer QUERY5_SENTENCE = new StringBuffer( " UPDATE " )
	.append("T3339PERMODFOR SET ") 
	.append(" cod_tipdoc             = ? ," )
	.append(" num_doc        = ? ," )
	.append(" cod_registro    = ? ," )
	.append(" nom_ape_pat         = ? ," )
	.append(" nom_ape_mat         = ? ," )
	.append(" nom_per        = ? ," )
	.append(" fec_Nacimiento     = ? ," )
	.append(  " ind_madre_resp    = ? ," )
	.append(" cod_ubigeo_nac    = ? ," )
	.append(" cod_nacionalidad   = ? ," )
	.append(" cod_sexo         = ? ," )
	.append(" cod_est_civil     = ? ," )
	.append(" ind_domiciliado    = ? ," )
	.append(" ind_discapacidad   = ? ," )
	.append(" des_discapacidad   = ? ," )
	.append(" num_telefono     = ? ," )
	.append(" num_celular         = ? ," )
	.append(" dir_correo         = ? ," )
	.append( " obs_personal     = ? ," )
	.append(" cod_tipo_via     = ? ," )
	.append(" nom_via        = ? ," )
	.append(" num_via        = ? ," )
	.append(" num_interior     = ? ," )
	.append(" cod_tipo_zona     = ? ," )
	.append(" nom_zona         = ? ," )
	.append(" des_ref_dom         = ? ," )
	.append(" cod_ubigeo_dom    = ? ," )
	.append(" cod_usumodif     = ? ," )
	.append(" fec_modif         = ? , " )
	.append(" cod_situacion     = ?   " )
	.append(" WHERE cod_per_modfor= ? " );
	
	
	private StringBuffer QUERY7_SENTENCE = new StringBuffer(" SELECT DISTINCT " )
	.append("NUM_DOC FROM T3339PERMODFOR WHERE NUM_DOC =? AND COD_REGISTRO=? ")
	.append("AND COD_SITUACION=? ");
	
	private StringBuffer QUERY10_SENTENCE = new StringBuffer(" SELECT " )
	.append("COD_REGISTRO FROM T3339PERMODFOR WHERE COD_PER_MODFOR =? " );
	
	private StringBuffer QUERY13_SENTENCE = new StringBuffer(" UPDATE " )
	.append("T3339PERMODFOR SET COD_EST_PER='E' WHERE COD_PER_MODFOR = ? ");
	
	private StringBuffer QUERY14_SENTENCE = new StringBuffer("SELECT COUNT(*)")
	.append(" AS CONTADOR FROM T3339PERMODFOR P,T3340CONVENIO C WHERE ") 
	.append("P.COD_PER_MODFOR=C.COD_PER_MODFOR AND P.NUM_DOC=? AND ")
	.append("COD_TIP_BAJA='03' ");
	
	private StringBuffer QUERY15_SENTENCE = new StringBuffer(" UPDATE " )
	.append("T3339PERMODFOR SET COD_EST_PER= ? ,COD_SITUACION= ?,FEC_FIN =? ")
	.append(" WHERE COD_PER_MODFOR = ? ");
	
	private StringBuffer QUERY16_SENTENCE = new StringBuffer("SELECT ")
	.append("COD_REGISTRO FROM T3339PERMODFOR WHERE COD_TIPDOC =? AND " )
	.append("NUM_DOC =? AND COD_EST_PER <> ? ");
	
	//icapunay 18/10/2011 - metodos requeridos por clase AsisForFacade(pe.gob.sunat.rrhh.formativa.ejb) para generacion de jar:ejbrrhh-asisforfacade.jar
	private StringBuffer QUERY17_SENTENCE = new StringBuffer("SELECT ")
	.append("P.cod_registro, P.cod_tipdoc, P.num_doc, P.nom_ape_pat, P.nom_ape_mat, P.nom_per, ")
	.append("C.cod_uorga, C.cod_modalidad, C.num_convenio, C.fec_ini_conv, ")
	.append("T.num_doc num_doc_tutor, T.cod_tip_parent, T.nom_apepat_tutor, T.nom_apemat_tutor, T.nom_tutor ")
	.append("FROM t3339permodfor P, t3340convenio C, OUTER t3473tutmodfor T ")
	.append("WHERE P.cod_per_modfor = C.cod_per_modfor ")
	.append("AND C.cod_est_conv = '1' ")
	.append("AND P.cod_per_modfor = T.cod_per_modfor ")
	.append("AND P.cod_registro <> '' ")
	.append("AND P.cod_registro IS NOT NULL ");
	
	private StringBuffer QUERY18_SENTENCE = new StringBuffer("SELECT t99codigo, t99descrip ")	
	.append("FROM t99codigos ")
	.append("WHERE t99cod_tab = '583' ");
	
	private StringBuffer QUERY19_SENTENCE = new StringBuffer("SELECT ")
	.append("M.cod_pers, M.fecha, M.hora, R.descrip ")	
	.append("FROM t1275marcacion M, t1280tipo_reloj R ")
	.append("WHERE M.cod_pers = ? ")
	.append("AND M.reloj = R.reloj ") 
	.append("AND sdel = '1' ");
	//fin icapunay 18/10/2011 - metodos requeridos por clase AsisForFacade(pe.gob.sunat.rrhh.formativa.ejb) para generacion de jar:ejbrrhh-asisforfacade.jar
	
	private StringBuffer QUERY200_SENTENCE = new StringBuffer(" UPDATE " )
	.append("T3339PERMODFOR SET COD_EST_PER =? ,COD_SITUACION= ?,FEC_INI= ?,")
	.append(" FEC_FIN = ? WHERE COD_PER_MODFOR = ? ");
	
	private StringBuffer FROM_SENTENCE = new StringBuffer(" FROM  (T3339PERMODFOR " )
	.append("P LEFT OUTER JOIN T3473TUTMODFOR T ON " )
	.append("P.COD_PER_MODFOR = T.COD_PER_MODFOR) " )
	.append(" LEFT OUTER JOIN T3340CONVENIO C ON P.COD_PER_MODFOR = " )
	.append("C.COD_PER_MODFOR WHERE  " );
	
//1. reporte movimientos por persona,Acumulados, Convenios por vencer,reporte Apertura cuenta
  private StringBuffer QUERY300_SENTENCE = new StringBuffer("SELECT ")
  .append("P.COD_REGISTRO,P.NOM_APE_PAT,P.NOM_APE_MAT,P.NOM_PER,P.NUM_DOC,")
  .append("P.FEC_NACIMIENTO,P.COD_SEXO,P.COD_EST_CIVIL,C.COD_MODALIDAD,") 
  .append("C.COD_TIP_PLAZA,C.COD_NIVEL_EDU,C.NUM_RUC_CENFORM,C.COD_CENFORM,C.COD_EST_CONV,") 
  .append("C.cod_tip_baja,C.cod_uorga,C.fec_ini_conv,C.fec_fin_conv, ") 
  .append("P.cod_tipdoc,P.cod_nacionalidad,P.nom_via,P.num_via,")
  .append("P.num_interior,P.nom_zona,P.cod_ubigeo_dom,")
  .append("P.num_telefono,P.dir_correo ")
  .append("FROM T3339PERMODFOR P,T3340CONVENIO C  " );
  
  //3. Acumulados resumen 
  private StringBuffer QUERY306_SENTENCE = new StringBuffer("SELECT ")
  .append("P.COD_REGISTRO ,count(*) AS CANTIDAD " )
  .append("FROM T3339PERMODFOR P,T3340CONVENIO C " );

  //5. Ficha
  private StringBuffer QUERY305_SENTENCE = new StringBuffer("SELECT ")
  .append("P.COD_REGISTRO,P.NOM_PER,P.NOM_APE_PAT,P.NOM_APE_MAT,")
  .append("P.FEC_NACIMIENTO,P.cod_ubigeo_NAC,P.COD_EST_CIVIL,")
  .append("P.COD_TIPDOC,P.NUM_DOC,P.COD_SEXO,P.NOM_ZONA,P.cod_ubigeo_dom,")
  .append("P.DES_REF_DOM,P.NUM_TELEFONO,T.NUM_DOC AS DOC_IDENTIDAD_T,")
  .append("T.NOM_TUTOR,T.NOM_APEPAT_TUTOR,T.NOM_APEMAT_TUTOR,")
  .append("T.COD_TIP_PARENT,C.COD_NIVEL_EDU,C.NUM_RUC_CENFORM,C.cod_cenform,C.cod_especialidad,")
  .append("C.NUM_CICLO,C.cod_uorga,P.cod_situacion,P.cod_est_per,")
  .append("C.FEC_INI_CONV,C.FEC_REAL_BAJA,C.FEC_FIN_CONV,")
  .append("C.COD_MODALIDAD,F.T02COD_PERS, F.T02FOTO,P.COD_TIPO_VIA,P.nom_via,P.num_via, " )
  .append("P.num_interior,P.cod_tipo_zona ")
  .append("FROM T3339PERMODFOR P,")
  .append("T3340CONVENIO C,OUTER T3473TUTMODFOR T,OUTER T02FOTOS F ");  
  
  //6. Media subvencion
  private StringBuffer QUERY303_SENTENCE = new StringBuffer("SELECT ")
  .append("P.COD_REGISTRO,P.NOM_APE_PAT,P.NOM_APE_MAT,P.NOM_PER,P.NUM_DOC,")
  .append("P.FEC_NACIMIENTO,P.COD_SEXO,P.COD_EST_CIVIL,C.COD_MODALIDAD,") 
  .append("C.cod_tip_plaza,C.cod_nivel_edu,C.cod_cenform,C.COD_EST_CONV,") 
  .append("C.cod_tip_baja,C.cod_uorga,C.fec_ini_conv AS fec_ini_conv,") 
  .append("C.fec_fin_conv AS fec_fin_conv,P.cod_situacion ") 
  .append("FROM T3339PERMODFOR P,T3340CONVENIO C ")
  .append("WHERE P.COD_PER_MODFOR = C.COD_PER_MODFOR ")
  .append("AND C.cod_est_conv ='1' ");    
  
  //Constancia y certificado
  private StringBuffer QUERY500_SENTENCE = new StringBuffer("SELECT " )
  .append("C.COD_MODALIDAD,P.NOM_PER,P.NOM_APE_PAT,P.NOM_APE_MAT," )
  .append("C.COD_NIVEL_EDU,C.COD_CENFORM,C.COD_ESPECIALIDAD,C.COD_UORGA,")
  .append("T.T12DES_UORGA,C.FEC_INI_CONV,C.FEC_FIN_CONV,P.COD_SEXO, " )
  .append("P.COD_TIPO_VIA,P.NOM_VIA,")
  .append("P.NUM_VIA,P.NUM_INTERIOR,P.COD_TIPO_ZONA,P.NOM_ZONA,")
  .append("C.NUM_RUC_CENFORM FROM T3340CONVENIO C,T3339PERMODFOR P,T12UORGA T ")
  .append("WHERE (C.COD_PER_MODFOR=P.COD_PER_MODFOR AND  C.COD_UORGA = ")
  .append("T.T12COD_UORGA AND C.COD_EST_CONV  <> 'E') ");
  
  //Convenio-FL JUVENIL
  private StringBuffer QUERY501_SENTENCE = new StringBuffer("SELECT ")
  .append("P.cod_registro,P.NOM_PER,P.NOM_APE_PAT,P.NOM_APE_MAT,")
  .append("P.COD_TIPDOC,P.NUM_DOC,P.COD_NACIONALIDAD,P.FEC_NACIMIENTO,")
  .append("P.COD_SEXO,P.COD_EST_CIVIL, P.COD_TIPO_VIA,P.NOM_VIA,")
  .append("P.NUM_VIA,P.NUM_INTERIOR,P.COD_TIPO_ZONA,P.NOM_ZONA,")
  .append("C.COD_NIVEL_EDU,C.COD_CENFORM,C.COD_MODALIDAD,T.COD_DPTO,")
  .append("C.DES_OTRAS_OCUP,C.FEC_INI_CONV,C.FEC_FIN_CONV,C.NUM_HORAS,")
  .append("C.IND_HOR_NOCT,C.MTO_SUBV,C.COD_UORGA,C.IND_SEGURO,")
  .append("C.COD_CIA_SEGURO,C.COD_OCUP,C.COD_TURNO,C.FEC_SURCRIP,")
  .append("C.COD_ESPECIALIDAD,P.COD_SITUACION,C.NUM_RUC_CENFORM,") 
  .append("C.COD_REPR_CENFORM, C.NOM_REPR_CENFORM,C.COD_LOCAL, C.num_ruc_local, ")
  .append("PE.t02ap_pate,PE.t02ap_mate,PE.t02nombres,PE.t02lib_elec ")  
  .append("FROM T3340CONVENIO C, T3339PERMODFOR P,T12UORGA T,")
  .append("outer t02perdp pe ")
  .append("WHERE C.COD_PER_MODFOR = P.COD_PER_MODFOR ")
  .append("AND C.COD_UORGA = T.T12COD_UORGA AND C.COD_REPR_SUNAT=")
  .append("PE.t02cod_pers AND C.COD_EST_CONV  = '1' AND  P.cod_registro = ? ");
  
  private StringBuffer QUERY600_SENTENCE = new StringBuffer("SELECT " )
  .append("P.cod_per_modfor,P.COD_TIPDOC,P.NUM_DOC,P.NOM_APE_PAT,")
  .append("P.NOM_APE_MAT,P.NOM_PER,P.IND_MADRE_RESP,P.IND_DISCAPACIDAD,")
  .append("P.FEC_NACIMIENTO,P.COD_SEXO,P.fec_nacimiento,C.FEC_MODIF," )
  .append("C.FEC_INI_CONV,C.FEC_FIN_CONV,C.IND_HOR_NOCT,C.COD_NIVEL_EDU,")
  .append("C.IND_SEGURO,C.COD_CIA_SEGURO,C.COD_ESPECIALIDAD,C.COD_OCUP," )
  .append("C.MTO_SUBV,TU.H_INICIO AS HORA_INI,TU.H_INIREFR AS HORA_INI_REF,")
  .append("TU.H_FINREF AS HORA_FIN_REF,TU.H_FIN AS HORA_FIN,C.COD_TURNO, ")
  .append("C.NUM_CONVENIO ")
  .append("FROM T3339PERMODFOR P, T3340CONVENIO C ,T12UORGA T, T45TURNO TU ") 
  .append("WHERE P.COD_PER_MODFOR = C.COD_PER_MODFOR AND C.COD_UORGA = " )
  .append("T.T12COD_UORGA AND TU.COD_TURNO=C.COD_TURNO "); 
	  
   
  private DataSource datasource;
  
  public T3339DAO() {
    super();
  }
  
  /**
   * 
   * Este constructor del DAO dicierne como crear el datasource
   * dependiendo del tipo de objeto que recibe. Esto nos ayuda a
   * mejorar la invocacion del dao.
   * 
   * @param datasource Object
   */
  public T3339DAO(Object datasource) {
    if (datasource instanceof DataSource)
      this.datasource = (DataSource)datasource;
    else if (datasource instanceof String)
      this.datasource = getDataSource((String)datasource);
    else
      throw new DAOException(this, "Datasource no valido");
  }

  /**
   * Metodo que se encarga de buscar los datos personales de un personal 
   * de un rango de fechas
   * @param Datos Map, contiene los criterios de búsqueda.
   * {cod_est_per,cod_uniNeg,cod_uorga,tipo_fecha_sel,cod_modalidad,
   * cod_registro,num_doc,nom_ape_pat,nom_ape_mat,nom_per}
   * @return List, contiene el resultado de la búsqueda
   * @throws DAOException
   * @throws Exception
   */  
  public List buscarPerModFor(Map params) throws DAOException ,Exception{
    
    String sFrom = "";
    StringBuffer sWhere = new StringBuffer("");
    StringBuffer sSQL = new StringBuffer(""); 
    String sQuery ="";
 
    if ("1".equals(((String) params.get("cod_est_conv"))) || 
       (params.get("cod_uorga") != null && !"".equals((String) params.get("cod_uorga"))) || 
       (params.get("cod_modalidad") != null && !"-1".equals((String) params.get("cod_modalidad")))){
      sFrom = new StringBuffer(" FROM  (T3339PERMODFOR P LEFT OUTER JOIN " )
      .append("T3473TUTMODFOR T ON " )
      .append("P.COD_PER_MODFOR = T.COD_PER_MODFOR) " )
      .append("LEFT OUTER JOIN T3340CONVENIO C ON P.COD_PER_MODFOR = " )
      .append("C.COD_PER_MODFOR WHERE C.COD_EST_CONV='1' " )
      .append(" AND ").toString();
    } else{
      sFrom = new StringBuffer(" FROM  (T3339PERMODFOR P LEFT OUTER JOIN " )
      .append("T3473TUTMODFOR T ON " )
      .append(  "P.COD_PER_MODFOR = T.COD_PER_MODFOR) WHERE " ).toString();        
    } 
   
    if (params.get("cod_registro") != null && !"".equals(params.get("cod_registro"))) {
      if (!"".equals(sWhere.toString())){ sWhere.append(" AND ");}
      sWhere.append("COD_REGISTRO = '").append(((String)params.get("cod_registro")).replaceAll("'", "''")).append("'");
    }
        
    if (params.get("nom_per") != null && !"".equals((String)params.get("nom_per"))) {
      if (!"".equals(sWhere.toString())){ sWhere.append(" AND ");}
      sWhere.append("NOM_PER LIKE '").append(((String)params.get("nom_per")).replaceAll("'", "''")).append("%'");
    }
        
    if (params.get("nom_ape_pat") != null && !"".equals((String)params.get("nom_ape_pat"))) {
      if (!"".equals(sWhere.toString())){ sWhere.append(" AND ");}
      sWhere.append("NOM_APE_PAT LIKE '").append(((String)params.get("nom_ape_pat")).replaceAll("'", "''")).append("%'");
    }
        
    if (params.get("nom_ape_mat") != null && !"".equals((String) params.get("nom_ape_mat"))) {
      if (!"".equals(sWhere.toString())){ sWhere.append(" AND ");}
      sWhere.append("NOM_APE_MAT LIKE '").append(((String)params.get("nom_ape_mat")).replaceAll("'", "''")).append("%'");
    }
        
    if (params.get("num_doc") != null && !"".equals((String)params.get("num_doc"))) {
      if (!"".equals(sWhere.toString())){ sWhere.append(" AND ");}
      sWhere.append("P.NUM_DOC = '").append(((String)params.get("num_doc")).replaceAll("'", "''")).append("'");
    }
        
    if (params.get("cod_est_conv") != null && !"-1".equals((String)params.get("cod_est_conv"))) {
      if (!"".equals(sWhere.toString())){ sWhere.append(" AND ");}
      sWhere.append("P.COD_EST_PER = '").append(((String)params.get("cod_est_conv")).replaceAll("'", "''")).append("'");
    }
     
    if ((params.get("perfil_usuario")!=null && !"usuario_Provincia".equals(params.get("perfil_usuario").toString())) &&
    		params.get("cod_uorga") != null && !"".equals((String)params.get("cod_uorga"))) {
      if (!"".equals(sWhere.toString())){ sWhere.append(" AND ");}
      sWhere.append("C.COD_UORGA = '").append(((String)params.get("cod_uorga")).replaceAll("'", "''")).append("'");
      sWhere.append(" AND P.COD_EST_PER = '1' ");
    }
    
    if (params.get("cod_modalidad") != null && !"-1".equals((String) params.get("cod_modalidad"))) {
      if (!"".equals(sWhere.toString())){ sWhere.append(" AND ");}
      sWhere.append("C.COD_MODALIDAD = '").append(((String) params.get("cod_modalidad")).replaceAll("'", "''")).append("'");
      sWhere.append(" AND P.COD_EST_PER = '1' ");
    }
  
    if ((params.get("perfil_usuario")!=null && !"usuario_Provincia".equals(params.get("perfil_usuario").toString())) &&
    	("1".equals(((String) params.get("cod_est_conv"))) || 
      (params.get("cod_uorga") != null && !"".equals((String)params.get("cod_uorga"))) || 
      (params.get("cod_modalidad") != null && !"-1".equals((String)params.get("cod_modalidad"))))){
      sQuery =QUERY11_SENTENCE.toString();
    } else if ((params.get("perfil_usuario")!=null && "usuario_Provincia".equals(params.get("perfil_usuario").toString())) &&
      ("1".equals(((String) params.get("cod_est_conv"))) || 
      (params.get("cod_uorga") != null && !"".equals((String)params.get("cod_uorga"))) || 
      (params.get("cod_modalidad") != null && !"-1".equals((String)params.get("cod_modalidad"))))){
    	if (!"".equals(sWhere.toString())){ sWhere.append(" AND ");}
    	sWhere.append("C.COD_UORGA LIKE '").append(params.get("uuoo_usuario").toString().substring(0,2)).append("%'");
      sWhere.append(" AND P.COD_EST_PER = '1' ");   	
      sQuery =QUERY11_SENTENCE.toString();
    } else{
      sQuery =QUERY1_SENTENCE.toString();
    }
    
    sSQL.append(sQuery).append(sFrom).append(sWhere.toString());
    
    setIsolationLevel(T3339DAO.TX_READ_UNCOMMITTED);
    List lista  = (ArrayList) executeQuery(datasource, sSQL.toString());  
    setIsolationLevel(-1);
    return lista;
  }
  
  /**
   * Metodo que se encarga de insertar los datos personales de la modalidad formativa
   * @param Datos Map, contiene los criterios de búsqueda.
   * {cod_est_per,cod_uniNeg,cod_uorga,tipo_fecha_sel,cod_modalidad,
   * cod_registro,num_doc,nom_ape_pat,nom_ape_mat,nom_per}
   * @throws DAOException
   */
  public void registraPerModFor(Map params)  throws DAOException {    
    FechaBean fecAct = new FechaBean();
    
    FechaBean bfh = new FechaBean((String) params.get("fec_nacimiento"));
    Date dateFecNac = bfh.getSQLDate();
    
    Object[] objs = new Object[] {
      (String) params.get("cod_per_modfor"),  
      (String) params.get("cod_registro"  ),  
      (String) params.get("cod_tipdoc"  ),  
      (String) params.get("num_doc"    ),  
      (String) params.get("nom_ape_pat"  ),  
      (String) params.get("nom_ape_mat"  ),  
      (String) params.get("nom_per"    ),  
      dateFecNac,  
      (String) params.get("ind_madre_resp"  ),  
      (String) params.get("cod_ubigeo_nac"  ),  
      (String) params.get("cod_nacionalidad"  ),  
      (String) params.get("cod_sexo"    ),  
      (String) params.get("cod_est_civil"  ),  
      (String) params.get("ind_domiciliado"  ),  
      (String) params.get("ind_discapacidad"  ),  
      (String) params.get("des_discapacidad"  ),  
      (String) params.get("num_telefono"  ),  
      (String) params.get("num_celular"  ),  
      (String) params.get("dir_correo"  ),  
      (String) params.get("obs_personal"  ),  
      (String) params.get("cod_tipo_via"  ),  
      (String) params.get("nom_via"    ),  
      (String) params.get("num_via"    ),  
      (String) params.get("num_interior"  ),  
      (String) params.get("cod_tipo_zona"  ),  
      (String) params.get("nom_zona"    ),  
      (String) params.get("des_ref_dom"  ),  
      (String) params.get("cod_ubigeo_dom"),  
      (String) params.get("cod_usumodif"  ),
      fecAct.getTimestamp(),
      (String) params.get("cod_est_per"),  
      (String) params.get("cod_situacion")};
    
    executeUpdate(datasource, QUERY2_SENTENCE.toString(), objs);
    
  }
  
  /**
   * Metodo que se encarga de calcular el máximo código de personal
   * @return Map, el máximo código de personal
   * @throws DAOException
   */
  public Map maximoValor() throws DAOException {
    Map mapa = executeQueryUniqueResult(datasource, QUERY4_SENTENCE.toString());
    return mapa;
  }
  
  /**
   * Metodo que se encarga de bucar el codigo  de registo de una persona (cod_per_modfor) 
   * , tomando como condicion el codigo de registro
   * @param params Map, contiene los criterios de búsqueda.
   * {cod_est_per,cod_uniNeg,cod_uorga,tipo_fecha_sel,cod_modalidad,
   * cod_registro,num_doc,nom_ape_pat,nom_ape_mat,nom_per}
   * @return Map, contiene el resultado de la búsqueda
   * @throws DAOException
   */
  public Map buscaCodRegistroPerModFor(Map params) throws DAOException {    
    Object[] objs = new Object[] {(String)params.get("cod_per_modfor")};
    Map mapa = executeQueryUniqueResult(datasource, QUERY10_SENTENCE.toString(), objs);
    return mapa;
  }
  
  /**
   * Metodo que se encarga de validar la existencia de un personal por su codigo de registro
   * @param params Map, contiene los criterios de búsqueda.
   * {cod_est_per,cod_uniNeg,cod_uorga,tipo_fecha_sel,cod_modalidad,
   * cod_registro,num_doc,nom_ape_pat,nom_ape_mat,nom_per}
   * @return Map, contiene el resultado de la búsqueda
   * @throws DAOException
   * @throws Exception
   */
  public Map validarPerModFor(Map params) throws DAOException,Exception {      
    Object[] objs = new Object[] {(String)params.get("cod_registro")};
    setIsolationLevel(T3339DAO.TX_READ_UNCOMMITTED);
    Map mapa = executeQueryUniqueResult(datasource, QUERY6_SENTENCE.toString(), objs);
    setIsolationLevel(-1);
      
    return mapa;
  }
  
  /**
   * Metodo que se encarga de validar la existencia de un personal por su numero de documento de Identidad
   * @param params Map, contiene los criterios de búsqueda.
   * {cod_est_per,cod_uniNeg,cod_uorga,tipo_fecha_sel,cod_modalidad,
   * cod_registro,num_doc,nom_ape_pat,nom_ape_mat,nom_per}
   * @return Map, contiene el resultado de la búsqueda
   * @throws DAOException
   * @throws Exception
   */
  public Map validarPerModFor_x_numdoc(Map params) throws DAOException, Exception {
    Object[] objs = new Object[] {(String)params.get("num_doc"),
      (String)params.get("cod_registro"),
      (String)params.get("cod_situacion")};
    setIsolationLevel(T3339DAO.TX_READ_UNCOMMITTED);  
    Map mapa = executeQueryUniqueResult(datasource, QUERY7_SENTENCE.toString(), objs);
    setIsolationLevel(-1);    
    return mapa;
  }

  /**
   * Metodo que se encarga de modificar los datos personales de una persona
   * @param params Map, contiene los datos a actualizar.
   * {cod_est_per,cod_uniNeg,cod_uorga,tipo_fecha_sel,cod_modalidad,
   * cod_registro,num_doc,nom_ape_pat,nom_ape_mat,nom_per}
   * @throws DAOException
   */
  public void actualizaPerModFor(Map params) throws DAOException {
    FechaBean fecAct = new FechaBean();
    FechaBean bfh = new FechaBean((String) params.get("fec_nacimiento"));
    Date dateFecNac = bfh.getSQLDate();
    
    Object[] objs = new Object[] {
      (String) params.get("cod_tipdoc"  ),  
      (String) params.get("num_doc"    ),
      (String) params.get("cod_registro"  ),
      (String) params.get("nom_ape_pat"  ),  
      (String) params.get("nom_ape_mat"  ),  
      (String) params.get("nom_per"    ),  
      dateFecNac,  
      (String) params.get("ind_madre_resp"  ),  
      (String) params.get("cod_ubigeo_nac"  ),  
      (String) params.get("cod_nacionalidad"  ),  
      (String) params.get("cod_sexo"    ),  
      (String) params.get("cod_est_civil"  ),  
      (String) params.get("ind_domiciliado"  ),  
      (String) params.get("ind_discapacidad"  ),  
      (String) params.get("des_discapacidad"  ),  
      (String) params.get("num_telefono"  ),  
      (String) params.get("num_celular"  ),  
      (String) params.get("dir_correo"  ),  
      (String) params.get("obs_personal"  ),  
      (String) params.get("cod_tipo_via"  ),  
      (String) params.get("nom_via"    ),  
      (String) params.get("num_via"    ),  
      (String) params.get("num_interior"  ),  
      (String) params.get("cod_tipo_zona"  ),  
      (String) params.get("nom_zona"    ),  
      (String) params.get("des_ref_dom"  ),  
      (String) params.get("cod_ubigeo_dom"),  
      (String) params.get("cod_usumodif"  ),
      fecAct.getTimestamp(),
      (String) params.get("cod_situacion"),  
      (String) params.get("cod_per_modfor")};
    
    executeUpdate(datasource, QUERY5_SENTENCE.toString(), objs);
  }

  /**
   * Metodo que se encarga de eliminar los datos personales de la modalidad formativa
   * @param params Map, contiene el cídigo del personal a eliminar.
   * {COD_PER_MODFOR}
   * @throws DAOException
   */
  public void eliminaPerModFor(Map params)
  throws DAOException {
    Object[] objs = new Object[] { (String) params.get("cod_per_modfor")};
    executeUpdate(datasource, QUERY13_SENTENCE.toString(), objs);
  }

  /**
   * Metodo que se encarga de buscar los datos personales de un personal
   * en modalidad formativa 
   * @param params Map, contiene los criterios de búsqueda.
   * {cod_est_per,cod_uniNeg,cod_uorga,tipo_fecha_sel,cod_modalidad,
   * cod_registro,num_doc,nom_ape_pat,nom_ape_mat,nom_per}
   * @return List, contiene el resultado de la búsqueda
   * @throws DAOException
   */
  public List consModFor(Map params) throws DAOException ,Exception{
  	String sFrom = "";
    StringBuffer sWhere = new StringBuffer("");
    StringBuffer sSQL = new StringBuffer(""); 
    String sQuery ="";
 
    if ("1".equals(((String) params.get("cod_est_conv"))) || 
    (params.get("cod_uorga") != null && !"".equals((String) params.get("cod_uorga"))) || 
    (params.get("cod_modalidad") != null && !"-1".equals((String) params.get("cod_modalidad"))) || 
    (params.get("sede") != null && !"0".equals((String)params.get("sede")))){
      sFrom = new StringBuffer(" , D.COD_DPTO FROM  (T3339PERMODFOR P LEFT OUTER JOIN " )
      .append("T3473TUTMODFOR T ON " )
      .append("P.COD_PER_MODFOR = T.COD_PER_MODFOR) " )
      .append("LEFT OUTER JOIN T3340CONVENIO C ON P.COD_PER_MODFOR = " )
      .append("C.COD_PER_MODFOR,T12UORGA D  WHERE C.COD_UORGA=D.T12COD_UORGA AND C.COD_EST_CONV='1' " )
      .append(" AND ").toString();
    } else{
      sFrom = new StringBuffer(" FROM  (T3339PERMODFOR P LEFT OUTER JOIN " )
      .append("T3473TUTMODFOR T ON " )
      .append(  "P.COD_PER_MODFOR = T.COD_PER_MODFOR) WHERE " ).toString();        
    } 
   
    if (params.get("cod_registro") != null && !"".equals(params.get("cod_registro"))) {
      if (!"".equals(sWhere.toString())){ sWhere.append(" AND ");}
      sWhere.append("COD_REGISTRO = '").append(((String)params.get("cod_registro")).replaceAll("'", "''")).append("'");
    }
        
    if (params.get("nom_per") != null && !"".equals((String)params.get("nom_per"))) {
      if (!"".equals(sWhere.toString())){ sWhere.append(" AND ");}
      sWhere.append("NOM_PER LIKE '").append(((String)params.get("nom_per")).replaceAll("'", "''")).append("%'");
    }
        
    if (params.get("nom_ape_pat") != null && !"".equals((String)params.get("nom_ape_pat"))) {
      if (!"".equals(sWhere.toString())){ sWhere.append(" AND ");}
      sWhere.append("NOM_APE_PAT LIKE '").append(((String)params.get("nom_ape_pat")).replaceAll("'", "''")).append("%'");
    }
        
    if (params.get("nom_ape_mat") != null && !"".equals((String) params.get("nom_ape_mat"))) {
      if (!"".equals(sWhere.toString())){ sWhere.append(" AND ");}
      sWhere.append("NOM_APE_MAT LIKE '").append(((String)params.get("nom_ape_mat")).replaceAll("'", "''")).append("%'");
    }
        
    if (params.get("sede") != null && !"0".equals((String)params.get("sede"))) {
      if (!"".equals(sWhere.toString())){ sWhere.append(" AND ");}
      if ( "1".equals((String) params.get("sede"))) {
        sWhere.append(" D.cod_dpto='15' ");
      } else if ( "2".equals((String) params.get("sede"))) {
        sWhere.append(" D.cod_dpto!='15' ");
      }
      sWhere.append(" AND P.COD_EST_PER = '1' ");
    }
        
    if (params.get("cod_est_conv") != null && !"-1".equals((String)params.get("cod_est_conv"))) {
      if (!"".equals(sWhere.toString())){ sWhere.append(" AND ");}
      sWhere.append("P.COD_EST_PER = '").append(((String)params.get("cod_est_conv")).replaceAll("'", "''")).append("'");
    }
        
    if (!"usuario_Provincia".equals(params.get("perfil_usuario").toString()) &&
    		params.get("cod_uorga") != null && !"".equals((String)params.get("cod_uorga"))) {
      if (!"".equals(sWhere.toString())){ sWhere.append(" AND ");}
      sWhere.append("C.COD_UORGA = '").append(((String)params.get("cod_uorga")).replaceAll("'", "''")).append("'");
      sWhere.append(" AND P.COD_EST_PER = '1' ");
    }
        
    if (params.get("cod_modalidad") != null && !"-1".equals((String) params.get("cod_modalidad"))) {
      if (!"".equals(sWhere.toString())){ sWhere.append(" AND ");}
      sWhere.append("C.COD_MODALIDAD = '").append(((String) params.get("cod_modalidad")).replaceAll("'", "''")).append("'");
      sWhere.append(" AND P.COD_EST_PER = '1' ");
    }
        
    if (!"usuario_Provincia".equals(params.get("perfil_usuario").toString()) &&
    	("1".equals(((String) params.get("cod_est_conv"))) || 
      (params.get("cod_uorga") != null && !"".equals((String)params.get("cod_uorga"))) || 
      (params.get("cod_modalidad") != null && !"-1".equals((String)params.get("cod_modalidad"))) || 
      (params.get("sede") != null && !"0".equals((String)params.get("sede"))))){
    	sQuery =QUERY11_SENTENCE.toString();
    } else if ("usuario_Provincia".equals(params.get("perfil_usuario").toString()) &&
      ("1".equals(((String) params.get("cod_est_conv"))) || 
      (params.get("cod_uorga") != null && !"".equals((String)params.get("cod_uorga"))) || 
      (params.get("cod_modalidad") != null && !"-1".equals((String)params.get("cod_modalidad"))) || 
      (params.get("sede") != null && !"0".equals((String)params.get("sede"))))){
     	if (!"".equals(sWhere.toString())){ sWhere.append(" AND ");}
     	sWhere.append("C.COD_UORGA LIKE '").append(params.get("uuoo_usuario").toString().substring(0,2)).append("%'");
      sWhere.append(" AND P.COD_EST_PER = '1' ");   	
      sQuery =QUERY11_SENTENCE.toString();
    } else{
      sQuery =QUERY1_SENTENCE.toString();
    }
    
    sSQL.append(sQuery).append(sFrom).append(sWhere.toString());
    
    setIsolationLevel(T3339DAO.TX_READ_UNCOMMITTED);
    List lista  = (ArrayList) executeQuery(datasource, sSQL.toString());  
    setIsolationLevel(-1);
    return lista;  
  }
    
  /**
   * Metodo que se encarga de generar el reporte General
    * @param datos Map, contiene los criterios de búsqueda.
    * {cod_est_per,cod_uniNeg,cod_uorga,tipo_fecha_sel,cod_modalidad,
    * cod_registro,num_doc,nom_ape_pat,nom_ape_mat,nom_per}
    * @return List, contiene el resultado de la búsqueda
    * @throws DAOException
    * @throws Exception
   */
   public List generarRepGeneral(Map datos) throws DAOException, Exception  {
     List lista = null;
     List listaParams = new ArrayList();
     StringBuffer sWhere = new StringBuffer(" WHERE A.cod_per_modfor =")
     .append("B.cod_per_modfor and B.cod_uorga = C.t12cod_uorga " );
     
     boolean hasParam = false;       
     StringBuffer sSQL = new StringBuffer(""); 
    
     if (datos.get("cod_uniNeg") != null && !"0".equals((String) datos.get("cod_uniNeg"))) {
       if (!"".equals(sWhere)) {
         sWhere.append(" AND ");
       }
       hasParam = true;
       if("1".equals((String) datos.get("cod_uniNeg"))){
         sWhere.append(" (B.cod_uorga like '1%' or B.cod_uorga like '2%') ");
       }
       else if("2".equals((String) datos.get("cod_uniNeg"))){
         sWhere.append(" (B.cod_uorga not like '1%' and B.cod_uorga not like '2%') ");
       }
     }

     if ( "1".equals((String) datos.get("cod_est_per"))) {
       if ( !"".equals(sWhere)) {
         sWhere.append(" AND ");
       }
       hasParam = true;
       sWhere.append(" B.cod_est_conv='1' AND A.COD_EST_PER = ? ");//'
       listaParams.add(datos.get("cod_est_per").toString());
     } else if ("0".equals((String) datos.get("cod_est_per"))){
       if ( !"".equals(sWhere)) {
         sWhere.append(" AND ");
       }
       hasParam = true;
       sWhere.append("A.COD_EST_PER = ? ");//'
       listaParams.add(datos.get("cod_est_per").toString());
     } else if ("2".equals((String) datos.get("cod_est_per"))){
    	 if ( !"".equals(sWhere)) {
         sWhere.append(" AND ");
       }
       hasParam = true;
       sWhere.append("A.COD_EST_PER != ? ");
       listaParams.add("E");
       if (datos.get("fechaRango_ini") != null && !"".equals(datos.get("fechaRango_ini").toString())) {
         sWhere.append("AND ( B.fec_fin_conv IS NULL or ( B.fec_fin_conv >= ? ")
         .append(" AND B.fec_ini_conv<= ? ))");
         listaParams.add(new FechaBean((String)datos.get("fechaRango_ini")).getSQLDate());
         listaParams.add(new FechaBean((String)datos.get("fechaRango_fin")).getSQLDate());
       }
       
     }

     if (datos.get("cod_local") != null && !"0".equals((String) datos.get("cod_local"))) {  
       if ( !"".equals(sWhere)) {
         sWhere.append(" AND ");
       }
       hasParam = true;
       if ( "1".equals((String) datos.get("cod_local"))) {
         sWhere.append(" C.cod_dpto='15' ");
       } else if ( "2".equals((String) datos.get("cod_local"))) {
         sWhere.append(" C.cod_dpto!='15' ");
       }
     }               
   
     if (datos.get("cod_uorga") != null && !"".equals((String) datos.get("cod_uorga"))) {

         if ( !"".equals(sWhere)) {
           sWhere.append(" AND ");
         }
         hasParam = true;
         sWhere.append("B.COD_UORGA = '")
         .append(((String) datos.get("cod_uorga")).replaceAll("'", "''"))
         .append("'");
     }
     
     if(datos.get("fecha_ini") != null && datos.get("fecha_fin") != null && 
    		 !"".equals(datos.get("fecha_ini")) && !"".equals(datos.get("fecha_fin"))){
        sWhere.append(" AND B.fec_ini_conv >= ? AND B.fec_ini_conv <=? ");
        listaParams.add(new FechaBean((String)datos.get("fecha_ini")).getSQLDate());
        listaParams.add(new FechaBean((String)datos.get("fecha_fin")).getSQLDate());
     }
     
     if(datos.get("fecha_ini2") != null && datos.get("fecha_fin2") != null &&
    		 !"".equals(datos.get("fecha_ini2")) && !"".equals(datos.get("fecha_fin2"))){
        sWhere.append(" AND B.fec_fin_conv >= ? AND B.fec_fin_conv <=? ");
        listaParams.add(new FechaBean((String)datos.get("fecha_ini2")).getSQLDate());
        listaParams.add(new FechaBean((String)datos.get("fecha_fin2")).getSQLDate());
     }

     if (datos.get("cod_especialidad") != null && !"-1".equals((String) datos.get("cod_especialidad"))) {
       if ( !"".equals(sWhere)) sWhere.append(" AND ");
       hasParam = true;
       sWhere.append("B.COD_ESPECIALIDAD = '")
       .append(((String) datos.get("cod_especialidad")).replaceAll("'", "''"))
       .append("'");
     }
   
     if (datos.get("cod_situacion") != null && !"-1".equals((String)datos.get("cod_situacion"))) {
       if ( !"".equals(sWhere)) sWhere.append(" AND ");
       hasParam = true;
       sWhere.append("A.COD_SITUACION = '")
       .append(((String) datos.get("cod_situacion")).replaceAll("'", "''"))
       .append("'");
     }
 
     if (datos.get("cod_tip_plaza") != null && !"-1".equals((String) datos.get("cod_tip_plaza"))) {
       if ( !"".equals(sWhere)) sWhere.append(" AND ");
       hasParam = true;
       sWhere.append("B.COD_TIP_PLAZA = '")
       .append(((String) datos.get("cod_tip_plaza")).replaceAll("'", "''"))
       .append("'");
     }
    
     if (datos.get("cod_modalidad") != null && !"-1".equals((String)datos.get("cod_modalidad"))) {
       if ( !"".equals(sWhere)) sWhere.append(" AND ");
       hasParam = true;
       sWhere.append("B.COD_MODALIDAD = '")
       .append(((String)datos.get("cod_modalidad")).replaceAll("'", "''"))
       .append("'");
     }
   
     if (datos.get("cod_registro") != null && !"".equals((String) datos.get("cod_registro"))) {
       if ( !"".equals(sWhere)) sWhere.append(" AND ");
       hasParam = true;
       sWhere.append("A.COD_REGISTRO = '")
       .append(((String) datos.get("cod_registro")).replaceAll("'", "''"))
       .append("'");
     }
  
     if (datos.get("num_doc") != null && !"".equals((String) datos.get("num_doc"))) {
       if ( !"".equals(sWhere)) {sWhere.append(" AND ");}
       hasParam = true;
       sWhere.append("A.NUM_DOC = '")
       .append(((String) datos.get("num_doc")).replaceAll("'", "''"))
       .append("'");
     }        
   
     if (datos.get("nom_ape_pat") != null && !"".equals((String) datos.get("nom_ape_pat"))) {
       if ( !"".equals(sWhere)) sWhere.append(" AND ");
       hasParam = true;
       sWhere.append("A.NOM_APE_PAT LIKE '")
       .append(((String) datos.get("nom_ape_pat")).replaceAll("'", "''"))
       .append("%'");
     }
    
     if (datos.get("nom_ape_mat") != null && !"".equals((String) datos.get("nom_ape_mat"))) {
       if ( !"".equals(sWhere)) { sWhere.append(" AND "); }
       hasParam = true;
       sWhere.append("A.NOM_APE_MAT LIKE '")
       .append(((String) datos.get("nom_ape_mat")).replaceAll("'", "''"))
       .append("%'");
     }

     if (datos.get("nom_per") != null && !"".equals((String)datos.get("nom_per"))) {
       if ( !"".equals(sWhere)) sWhere.append(" AND ");
       hasParam = true;
       sWhere.append("A.NOM_PER LIKE '")
       .append(((String)datos.get("nom_per")).replaceAll("'", "''"))
       .append("%'");
     }
     
     String sWhereTemp=sWhere.toString();
     if (hasParam) {
       sWhere =new StringBuffer(" ".concat(sWhereTemp));
     }
       
     sSQL.append(QUERY100_SENTENCE.toString())
     .append(sWhere.toString());
          
     setIsolationLevel(T3339DAO.TX_READ_UNCOMMITTED);
     
     BeanMapper beanMapper = new BeanMapper(){
       public Object setear(ResultSet rs, Map mapa) throws SQLException {
         try{
        	 FechaBean fecFinConvenio = new FechaBean();
        	 FechaBean fecIniConvenio = new FechaBean();
        	 FechaBean fec_nacimiento=new FechaBean();
        	 if (mapa.get("fec_fin_conv")!=null){
        		 fecFinConvenio = new FechaBean((Date)mapa.get("fec_fin_conv")); 
        	 }
        	 if (mapa.get("fec_ini_conv")!=null){
             fecIniConvenio = new FechaBean((Date)mapa.get("fec_ini_conv"));
        	 }
           int dias = (int) FechaBean.getDiferencia(fecFinConvenio.getCalendar(), 
             fecIniConvenio.getCalendar(),Calendar.DAY_OF_MONTH);
           int edad=0;
           FechaBean hoy=new FechaBean();
           if (mapa.get("fec_nacimiento")!=null){
             fec_nacimiento=new FechaBean((Date)mapa.get("fec_nacimiento"));
           }
           edad=(int)(Integer.valueOf(hoy.getAnho()).intValue()-Integer.valueOf(fec_nacimiento.getAnho()).intValue());
           GregorianCalendar gc_fec_nac = new GregorianCalendar();
             gc_fec_nac.set(Integer.valueOf(hoy.getAnho()).intValue(),
                 Integer.valueOf(fec_nacimiento.getMes()).intValue(),
                 Integer.valueOf(fec_nacimiento.getDia()).intValue());            
             GregorianCalendar gc_hoy = new GregorianCalendar();
             gc_hoy.set(Integer.valueOf(hoy.getAnho()).intValue(),
                 Integer.valueOf(hoy.getMes()).intValue(),
                 Integer.valueOf(hoy.getDia()).intValue());
             
             if(gc_hoy.before(gc_fec_nac)){
               edad--;
             }
           
           mapa.put("edad", ""+edad);
           mapa.put("dias", new Integer(dias));        
         } catch (Exception e) {
           log.error("*** SQL Error ****", e);
           throw new SQLException(e.getMessage());
         }
         return mapa;
       }
     };
    
     Object[] obj = listaParams.toArray();
     lista = (ArrayList) executeQuery(datasource, sSQL.toString(), obj, beanMapper);       
     setIsolationLevel(-1);
     return lista;
   }
  
  /**
   * Metodo que permite actualizar el estado y situacion del Personal de modalidad formativa
   * @param params Map, contiene los datos a actualizar.
   * {fec_ini_conv,fec_fin_conv,cod_est_per,cod_situacion,cod_per_modfor}
   * @throws DAOException
   */    
  public void actPerModForEstSit(Map params)throws DAOException {
    
    FechaBean bfh = new FechaBean((String) params.get("fec_ini_conv"));
    Date dateFecInicio = bfh.getSQLDate();
    
   	bfh = new FechaBean((String) params.get("fec_fin_conv"));
   	Date dateFecFin = bfh.getSQLDate();
    
    Object[] objs = new Object[] { 
  	      (String)params.get("cod_est_per"),
  	      (String)params.get("cod_situacion"),
  	      dateFecInicio,
  	      dateFecFin,
  	      (String)params.get("cod_per_modfor")
  	};
    executeUpdate(datasource,QUERY200_SENTENCE.toString(), objs);
  }

  /**
   * Metodo que se encarga de buscar los datos personales de un personal por el campo cod_per_modfor 
   * @param params Map, contiene los criterios de búsqueda.
   * {cod_est_per,cod_uniNeg,cod_uorga,tipo_fecha_sel,cod_modalidad,
   * cod_registro,num_doc,nom_ape_pat,nom_ape_mat,nom_per}
   * @return List, contiene el resultado de la búsqueda
   * @throws DAOException
   * @throws Exception 
   */
  public List buscarPerModForxCodPerModFor(Map params) throws DAOException,Exception {
    String pcod_per_modfor="";
    //StringBuffer sWhere = new StringBuffer("");
    StringBuffer sSQL = new StringBuffer(""); 

    pcod_per_modfor=(String)params.get("cod_per_modfor");
    //sWhere .append(" P.COD_PER_MODFOR= '").append(pcod_per_modfor.replaceAll("'", "''")).append("'");
    sSQL.append(QUERY1_SENTENCE.toString()).append(FROM_SENTENCE.toString()) //.append(sWhere.toString());
    .append(" P.COD_PER_MODFOR= '").append(pcod_per_modfor.replaceAll("'", "''")).append("'");
    setIsolationLevel(T3339DAO.TX_READ_UNCOMMITTED);
    List lista = (ArrayList) executeQuery(datasource, sSQL.toString());
    setIsolationLevel(-1);
    return lista;
  }
  
  /**
   * Metodo que se encarga de verificar la existencia de Convenios Rescindidos 
   * para el personal que se desea registrar
   * @param params Map, contiene los criterios de búsqueda.
   * {cod_est_per,cod_uniNeg,cod_uorga,tipo_fecha_sel,cod_modalidad,
   * cod_registro,num_doc,nom_ape_pat,nom_ape_mat,nom_per}
   * @return Map, contiene el resultado de la búsqueda
   * @throws DAOException
   * @throws Exception
   */
  public Map buscarConvRescxNumDoc(Map params) throws DAOException,Exception {
    Object[] objs = new Object[] {(String)params.get("num_doc")};
    setIsolationLevel(T3339DAO.TX_READ_UNCOMMITTED);
    Map mapa = executeQueryUniqueResult(datasource, QUERY14_SENTENCE.toString(), objs);
    setIsolationLevel(-1);  
    return mapa;
  }

  /**
   * Metodo que permite actualizar el estado, situacion y la fecha de fin de convenio del Personal de modalidad formativa
   * @param params Map, contiene los datos a actualizar.
   * {cod_est_per,cod_uniNeg,cod_uorga,tipo_fecha_sel,cod_modalidad,
   * cod_registro,num_doc,nom_ape_pat,nom_ape_mat,nom_per}
   * @throws DAOException
   */    
  public void actPerModForxExtConvenio(Map params)throws DAOException { 

    FechaBean bfh = new FechaBean((String) params.get("fec_fin_conv"));
    Date dateFecFin = bfh.getSQLDate();
    
    Object[] objs = new Object[] { 
      (String)params.get("cod_est_per"),
      (String)params.get("cod_situacion"),
      dateFecFin,
      (String)params.get("cod_per_modfor")
    };
    executeUpdate(datasource,QUERY15_SENTENCE.toString(), objs);
  }
  
  /**
   * Metodo que se encarga de validar la existencia de un personal por su codigo de registro
   * @param params Map, contiene los criterios de búsqueda.
   * {cod_est_per,cod_uniNeg,cod_uorga,tipo_fecha_sel,cod_modalidad,
   * cod_registro,num_doc,nom_ape_pat,nom_ape_mat,nom_per}
   * @return Map, contiene el resultado de la búsqueda
   * @throws DAOException
   * @throws Exception
   */
  public List validarExistePerModForm(Map params) throws DAOException,Exception {      
    
  	Object[] objs = new Object[] {
  			(String)params.get("cod_tipdoc"),
  			(String)params.get("num_doc"),
  			(String)params.get("estado")};
    setIsolationLevel(T3339DAO.TX_READ_UNCOMMITTED);
    List lista = executeQuery(datasource, QUERY16_SENTENCE.toString(), objs);
    setIsolationLevel(-1);
      
    return lista;
  }
  
  /**
   * Retorna los datos del reporte:movimientos por persona
   * @param datos DynaBean, contiene los criterios de bsqueda.
   * @return List, contiene la data para generar el reporte 
   * @throws DAOException
   */
  public List generaRepMovPersona(DynaBean datos) throws DAOException {
    List lista = null;   
    StringBuffer sWhere = new StringBuffer(" WHERE P.COD_PER_MODFOR = ")
    .append("C.COD_PER_MODFOR ");
    try{
      if ("1".equals(datos.getString("cod_est_per"))) {
        sWhere.append(" AND C.cod_est_conv <>'E' AND P.COD_EST_PER = '")
        .append(datos.getString("cod_est_per").replaceAll("'", "''")).append("'");
      } else {
        sWhere.append(" AND P.COD_EST_PER = '")
        .append(datos.getString("cod_est_per").replaceAll("'", "''")).append( "'");
      }
      if ("1".equals(datos.getString("cod_uniNeg"))) {
        sWhere.append(" AND  (C.cod_uorga like '1%' ")
        .append(" OR C.cod_uorga like '2%') ");
      } else if ("2".equals(datos.getString("cod_uniNeg"))) {
        sWhere .append(" AND  (C.cod_uorga not like '1%' ")
        .append(" AND C.cod_uorga not like '2%') ");
      }     
      if ( datos.getString("cod_uorga")!=null && !"".equals(datos.getString("cod_uorga"))) {
        sWhere.append(" AND C.COD_UORGA = '")
        .append(datos.getString("cod_uorga").replaceAll("'", "''")).append("'");
      }      
      if ( datos.getString("tipo_fecha_sel")!=null && !"".equals(datos.getString("tipo_fecha_sel"))) {
        if (datos.getDate("fecha_ini")!=null && datos.getDate("fecha_fin")!=null) {
          if ("V1".equals(datos.getString("tipo_fecha_sel"))) {
            if (datos.getDate("fecha_ini") != null) {
              sWhere.append(" AND C.fec_ini_conv BETWEEN ? AND ? ");
            }
          } else if ("V2".equals(datos.getString("tipo_fecha_sel"))) {
            if (datos.getDate("fecha_fin") != null) {
              sWhere.append(" AND C.fec_fin_conv BETWEEN ? AND ? ");
            }
          }            
        }
      }      
      if (!"-1".equals(datos.getString("cod_modalidad")) && !"".equals(datos.getString("cod_modalidad"))) {
        sWhere.append( " AND C.COD_MODALIDAD = '")
        .append(datos.getString("cod_modalidad").replaceAll("'", "''")).append("'");
      }      
      if (datos.getString("cod_registro") != null && !"".equals(datos.getString("cod_registro"))) {
        sWhere.append( " AND P.COD_REGISTRO = '" )
        .append(datos.getString("cod_registro").replaceAll("'", "''")).append("'");
      }      
      if (datos.getString("num_doc") != null && !"".equals(datos.getString("num_doc"))) {
        sWhere.append( " AND P.NUM_DOC = '")
        .append( datos.getString("num_doc").replaceAll("'", "''")).append( "'");
      } 
      if (datos.getString("nom_ape_pat") != null && !"".equals(datos.getString("nom_ape_pat"))) {
        sWhere.append( " AND P.NOM_APE_PAT LIKE '" )
        .append( datos.getString("nom_ape_pat").replaceAll("'", "''")).append( "%'");
      }      
      if (datos.getString("nom_ape_mat") != null && !"".equals(datos.getString("nom_ape_mat"))) {
        sWhere.append( " AND P.NOM_APE_MAT LIKE '" )
        .append( datos.getString("nom_ape_mat").replaceAll("'", "''")).append(  "%'");
      }            
      if (datos.getString("nom_per") != null && !"".equals(datos.getString("nom_per"))) {
        sWhere.append( " AND P.NOM_PER LIKE '" )
        .append( datos.getString("nom_per").replaceAll("'", "''")).append(  "%'");
      }      
      StringBuffer sSQL = QUERY300_SENTENCE.append(sWhere);      
      if (log.isDebugEnabled()) {
        log.debug("##############################");
        log.debug(" QUERY GENERAR RPT mov x persona:");
        log.debug("##############################");
        log.debug(sSQL);
      }        
      final BeanMapper bmMovPersona = new BeanMapper(){
        public Object setear(ResultSet rs, Map map) throws SQLException {
          StringBuffer nombre = null;
          ParamBean parametro = null;
          ParamDAO paramDAO = new ParamDAO();
          try{            
            nombre = new StringBuffer("");
            if ( map.get("nom_ape_pat") != null) {
              nombre.append((String)map.get("nom_ape_pat"));
            }
            if ( map.get("nom_ape_mat") != null) {
              nombre = nombre.append(" ");
              nombre = nombre.append((String)map.get("nom_ape_mat"));
            }
            if ( map.get("nom_per") != null) {
              nombre = nombre.append(" ");
              nombre = nombre.append((String)map.get("nom_per"));
            }
            map.put("nombre", nombre.toString());
            int edad=0;
            if ( map.get("fec_nacimiento") != null) {
            	FechaBean fec_nacimiento = new FechaBean((Date)map.get("fec_nacimiento"));
            	int anhoFecIn = Integer.valueOf(fec_nacimiento.getAnho()).intValue();
            	int mesFecIn = Integer.valueOf(fec_nacimiento.getMes()).intValue();
            	int diaFecIn = Integer.valueOf(fec_nacimiento.getDia()).intValue();
            	edad =(Integer.valueOf(new FechaBean().getAnho()).intValue()-anhoFecIn);
            	FechaBean hoy = new FechaBean();
            	int fecIni = (Integer.valueOf(hoy.getAnho()).intValue()*10000)+(mesFecIn*100)+diaFecIn;
            	int fecHoy=Integer.valueOf(hoy.getAnho().concat(hoy.getMes()).concat(hoy.getDia())).intValue();
            	if(fecHoy<fecIni){
              	edad--;
              }            	
            }
            
            map.put("edad", ""+edad);
            if ( (map.get("cod_uorga") != null && !"".equals(((String) map.get("cod_uorga")).trim()))){
              parametro = paramDAO.buscar("spprm16t12un",0, ((String) map.get("cod_uorga")).trim());
              if ( parametro!=null && parametro.getDescripcion()!=null) {
                if ("15".equals(parametro.getDescripcion().trim())) {
                  map.put("des_sede", "Lima");
                } else {
                  map.put("des_sede", "Provincias");
                }         
                if(parametro.getCodigo().length()>=1){
                  if ("1".equals(parametro.getDescripcion().substring(0,1))) {
                    map.put("uni_neg", "Tributos Internos");
                  } else {
                    map.put("uni_neg", "Aduanas");
                  }  
                }
              } else {
                map.put("des_sede", "");
                map.put("uni_neg", "");
              }
            }             
            if ( map.get("cod_modalidad") != null 
              && !"".equals((String)map.get("cod_modalidad"))) {
              parametro = paramDAO.buscar("spprm583t99Corta",0,((String)map.get("cod_modalidad")).trim());
              map.put("des_modalidad",(parametro == null ? "":parametro.getDescripcion()));
            } else {
              map.put("des_modalidad", "");
            }            
            if ( map.get("cod_tip_plaza") != null 
              && !"".equals((String)map.get("cod_tip_plaza"))) {
              parametro=paramDAO.buscar("spprmF09t99",2,((String)map.get("cod_tip_plaza")).trim());
              map.put("des_tipo_plaza",(parametro == null ? "":parametro.getDescripcion()));
            } else {
              map.put("des_tipo_plaza", "");
            }            
            if ( map.get("cod_nivel_edu") != null 
              && !"".equals((String)map.get("cod_nivel_edu"))) {
              parametro=paramDAO.buscar("spprmF01t99",2,((String)map.get("cod_nivel_edu")).trim());
              map.put("des_nivel_estudios",(parametro == null ? "":parametro.getDescripcionCorta()));
            } else {
              map.put("des_nivel_estudios", "");
            }
            if ( map.get("cod_cenform") != null 
              && !"".equals((String)map.get("cod_cenform"))) {
              parametro=paramDAO.buscar("spprm004t99",2,((String)map.get("cod_cenform")).trim());
              map.put("des_centro_estudios",(parametro == null ? "":parametro.getDescripcionCorta()));
            } else {
              map.put("des_centro_estudios", "");
            }
            if ( map.get("cod_est_conv") != null 
              && !"".equals((String)map.get("cod_est_conv"))) {
              parametro=paramDAO.buscar("spprmF07t99",2,((String)map.get("cod_est_conv")).trim());
              map.put("des_est_conv",(parametro == null ? "":parametro.getDescripcion()));
            } else {
              map.put("des_est_conv", "");
            }
            if ( map.get("cod_tip_baja") != null 
              && !"".equals((String)map.get("cod_tip_baja"))) {
              parametro=paramDAO.buscar("spprmF08t99",2,((String)map.get("cod_tip_baja")).trim());
              map.put("des_mot_baja",(parametro == null ? "":parametro.getDescripcion().trim()));
            } else {
              map.put("des_mot_baja","");
            }
            if ( map.get("cod_uorga") != null 
              && !"".equals((String)map.get("cod_uorga"))) {
              parametro=paramDAO.buscar("spprm16t12",0,((String)map.get("cod_uorga")).trim());
              map.put("des_uorga",(parametro == null ? "":parametro.getDescripcion()));
            } else {
              map.put("des_uorga", "");
            }        
          } catch (Exception e) {
            log.error("*** SQL Error ****", e);
            throw new SQLException(e.getMessage());
          }     
          return map;
        }
      };      
      setIsolationLevel(T3339DAO.TX_READ_UNCOMMITTED);
      if (datos.getString("tipo_fecha_sel")!=null && !"".equals(datos.getString("tipo_fecha_sel"))) {
        Object objects[]={datos.getDate("fecha_ini"),datos.getDate("fecha_fin")};
        lista = (ArrayList) executeQuery(datasource, sSQL.toString(),objects,bmMovPersona);
      } else {
        lista = (ArrayList) executeQuery(datasource, sSQL.toString(),bmMovPersona);
      }
      setIsolationLevel(-1);
    }catch (Exception e) {
      log.error(e,e);
      MensajeBean msg = new MensajeBean();
      msg.setMensajeerror("Ha ocurrido un error inesperado al consultar la data para el reporte: ".concat( e.getMessage() ));
      msg.setMensajesol("Por favor, intente nuevamente realizar la operacion, de continuar con el problema comuniquese con el webmaster");
      throw new DAOException (this, msg);     
    } finally {       
    }
    return lista;
  }
  
  /**
   * Retorna los datos del reporte:dia acumulados por persona
   * @param datos DynaBean, contiene los criterios de bsqueda.
   * @return List, contiene la data para generar el reporte 
   * @throws DAOException
   */   
  public List generaRepDiasAcum(DynaBean datos) throws DAOException {       
    StringBuffer sWhere = new StringBuffer(" WHERE P.COD_PER_MODFOR = ")
    .append("C.COD_PER_MODFOR ");
    List lista = null; 
    try{
      if ("1".equals(datos.getString("cod_est_per"))) {
        sWhere.append(" AND C.cod_est_conv<>'E' AND P.COD_EST_PER = '")
        .append(datos.getString("cod_est_per").replaceAll("'", "''")).append("'");
      } else {
        sWhere.append(" AND P.COD_EST_PER = '")
        .append(datos.getString("cod_est_per").replaceAll("'", "''")).append( "'");
      }           
      if ("1".equals(datos.getString("cod_uniNeg"))) {
        sWhere.append(" AND  (C.cod_uorga like '1%' ")
        .append(" OR C.cod_uorga like '2%') ");
      } else if ("2".equals(datos.getString("cod_uniNeg"))) {
        sWhere .append(" AND  (C.cod_uorga not like '1%' ")
        .append(" AND C.cod_uorga not like '2%') ");
      }
      if ( datos.getString("cod_uorga")!=null && !"".equals(datos.getString("cod_uorga"))) {
        sWhere.append(" AND C.COD_UORGA = '")
        .append(datos.getString("cod_uorga").replaceAll("'", "''")).append("'");
      }
      if ( datos.getString("tipo_fecha_sel")!=null && !"".equals(datos.getString("tipo_fecha_sel"))) {
        if (datos.getDate("fecha_ini")!=null && datos.getDate("fecha_fin")!=null) {
          if ("V1".equals(datos.getString("tipo_fecha_sel"))) {
            if (datos.getDate("fecha_ini") != null) {
              sWhere.append(" AND C.fec_ini_conv BETWEEN ? AND ? ");
            }
          } else if ("V2".equals(datos.getString("tipo_fecha_sel"))) {
            if (datos.getDate("fecha_fin") != null) {
              sWhere.append(" AND C.fec_fin_conv BETWEEN ? AND ? ");
            }
          }            
        }
      }      
      if (!"-1".equals(datos.getString("cod_especialidad"))) {
        sWhere.append(" AND C.COD_ESPECIALIDAD = '" )
        .append( datos.getString("cod_especialidad").replaceAll("'", "''")).append( "'");
      }      
      if (datos.getString("cod_registro") != null && !"".equals(datos.getString("cod_registro"))) {
        sWhere.append( " AND P.COD_REGISTRO = '" )
        .append(datos.getString("cod_registro").replaceAll("'", "''")).append("'");
      }      
      if (datos.getString("num_doc") != null && !"".equals(datos.getString("num_doc"))) {
        sWhere.append( " AND P.NUM_DOC = '")
        .append( datos.getString("num_doc").replaceAll("'", "''")).append( "'");
      } 
      if (datos.getString("nom_ape_pat") != null && !"".equals(datos.getString("nom_ape_pat"))) {
        sWhere.append( " AND P.NOM_APE_PAT LIKE '" )
        .append( datos.getString("nom_ape_pat").replaceAll("'", "''")).append( "%'");
      }      
      if (datos.getString("nom_ape_mat") != null && !"".equals(datos.getString("nom_ape_mat"))) {
        sWhere.append( " AND P.NOM_APE_MAT LIKE '" )
        .append( datos.getString("nom_ape_mat").replaceAll("'", "''")).append(  "%'");
      }            
      if (datos.getString("nom_per") != null && !"".equals(datos.getString("nom_per"))) {
        sWhere.append( " AND P.NOM_PER LIKE '" )
        .append( datos.getString("nom_per").replaceAll("'", "''")).append(  "%'");
      }      
      StringBuffer sSQL = QUERY300_SENTENCE.append(sWhere).append(" ORDER BY P.COD_REGISTRO");      
      if (log.isDebugEnabled()) {
        log.debug("#########################################");
        log.debug(" QUERY GENERAR REPORTE Dias Acumulados por persona:");
        log.debug("#########################################");
        log.debug(sSQL);
      }      
      final BeanMapper bmDiasAcum = new BeanMapper(){
        public Object setear(ResultSet rs, Map map) throws SQLException {
          StringBuffer nombre=null;
          ParamBean parametro=null;
          ParamDAO paramDAO = new ParamDAO();      
          try{
            nombre = new StringBuffer("");
            if ( map.get("nom_ape_pat") != null) {
              nombre.append((String)map.get("nom_ape_pat"));
            }
            if ( map.get("nom_ape_mat") != null) {
              nombre=nombre.append(" ");
              nombre=nombre.append((String)map.get("nom_ape_mat"));
            }
            if ( map.get("nom_per") != null) {
              nombre=nombre.append(" ");
              nombre=nombre.append((String)map.get("nom_per"));
            }
            map.put("nombre", nombre.toString());    
            
            int edad=0;
            if ( map.get("fec_nacimiento") != null) {
            	FechaBean fec_nacimiento = new FechaBean((Date)map.get("fec_nacimiento"));
            	int anhoFecIn = Integer.valueOf(fec_nacimiento.getAnho()).intValue();
            	int mesFecIn = Integer.valueOf(fec_nacimiento.getMes()).intValue();
            	int diaFecIn = Integer.valueOf(fec_nacimiento.getDia()).intValue();
            	edad =(Integer.valueOf(new FechaBean().getAnho()).intValue()-anhoFecIn);
            	FechaBean hoy = new FechaBean();
            	int fecIni = (Integer.valueOf(hoy.getAnho()).intValue()*10000)+(mesFecIn*100)+diaFecIn;
            	int fecHoy=Integer.valueOf(hoy.getAnho().concat(hoy.getMes()).concat(hoy.getDia())).intValue();
            	if(fecHoy<fecIni){
              	edad--;
              }            	
            }
            map.put("edad", ""+edad);
            if ( (map.get("cod_uorga") != null && !"".equals(((String) map.get("cod_uorga")).trim()))){
              ParamBean param = new ParamDAO().buscar("spprm16t12un",0, ((String) map.get("cod_uorga")).trim());
              if ( param!=null && param.getDescripcion()!=null) {
                if ("15".equals(param.getDescripcion().trim())) {
                  map.put("des_sede", "Lima");
                } else {
                  map.put("des_sede", "Provincias");
                }
                if(param.getCodigo().length()>=1){
                  if ("1".equals(param.getDescripcion().substring(0,1))) {
                    map.put("uni_neg", "Tributos Internos");
                  } else {
                    map.put("uni_neg", "Aduanas");
                  }  
                }
              } else {
                map.put("des_sede", "");
                map.put("uni_neg", "");
              }
            } 
            if ( map.get("cod_modalidad") != null 
              && !"".equals((String)map.get("cod_modalidad"))) {
              parametro=paramDAO.buscar("spprm583t99Corta",0,((String)map.get("cod_modalidad")).trim());
              map.put("des_modalidad",(parametro == null ? "":parametro.getDescripcion()));
            } else {
              map.put("des_modalidad", "");
            }
            if ( map.get("cod_tip_plaza") != null 
              && !"".equals((String)map.get("cod_tip_plaza"))) {
              parametro=paramDAO.buscar("spprmF09t99",2,((String)map.get("cod_tip_plaza")).trim());
              map.put("des_tipo_plaza",(parametro == null ? "":parametro.getDescripcion()));
            } else {
              map.put("des_tipo_plaza", "");
            }
            if ( map.get("cod_nivel_edu") != null 
              && !"".equals((String)map.get("cod_nivel_edu"))) {
              parametro=paramDAO.buscar("spprmF01t99",2,((String)map.get("cod_nivel_edu")).trim());
              map.put("des_nivel_estudios",(parametro == null ? "":parametro.getDescripcionCorta()));
            } else {
              map.put("des_nivel_estudios", "");
            }
            if ( map.get("cod_cenform") != null 
              && !"".equals((String)map.get("cod_cenform"))) {
              parametro=paramDAO.buscar("spprm004t99",2,((String)map.get("cod_cenform")).trim());
              map.put("des_centro_estudios",(parametro == null ? "":parametro.getDescripcionCorta()));
            } else {
              map.put("des_centro_estudios", "");
            }
            if ( map.get("cod_est_conv") != null 
              && !"".equals((String)map.get("cod_est_conv"))) {
              parametro=paramDAO.buscar("spprmF07t99",2,((String)map.get("cod_est_conv")).trim());
              map.put("des_est_conv",(parametro == null ? "":parametro.getDescripcion()));
            } else {
              map.put("des_est_conv", "");
            }
            if ( map.get("cod_tip_baja") != null 
              && !"".equals((String)map.get("cod_tip_baja"))) {
              parametro=paramDAO.buscar("spprmF08t99",2,((String)map.get("cod_tip_baja")).trim());
              map.put("des_mot_baja",(parametro == null ? "":parametro.getDescripcion().trim()));
            } else {
              map.put("des_mot_baja","");
            }
            if ( map.get("cod_uorga") != null 
              && !"".equals((String)map.get("cod_uorga"))) {
              parametro=paramDAO.buscar("spprm16t12",0,((String)map.get("cod_uorga")).trim());
              map.put("des_uorga",(parametro == null ? "":parametro.getDescripcion()));
            } else {
              map.put("des_uorga", "");
            }
          } catch (Exception e) {
            log.error("*** SQL Error ****", e);
            throw new SQLException(e.getMessage());
          }     
          return map;
        }
      };
      setIsolationLevel(T3339DAO.TX_READ_UNCOMMITTED);
      if (datos.getString("tipo_fecha_sel")!=null && !"".equals(datos.getString("tipo_fecha_sel"))) {
        Object objects[]={datos.getDate("fecha_ini"),datos.getDate("fecha_fin")};
        lista = (ArrayList) executeQuery(datasource, sSQL.toString(),objects,bmDiasAcum);
      } else {
        lista = (ArrayList) executeQuery(datasource, sSQL.toString(),bmDiasAcum);
      }
      setIsolationLevel(-1);
    }catch (Exception e) {
      log.error("*** SQL Error ****",e);
      MensajeBean msg = new MensajeBean();
      msg.setMensajeerror("Ha ocurrido un error inesperado al consultar la data para el reporte: ".concat( e.getMessage() ));
      msg.setMensajesol("Por favor, intente nuevamente realizar la operacion, de continuar con el problema comuniquese con el webmaster");
      throw new DAOException (this, msg);     
    } finally {       
    }
    return lista;
  }
  
  /**
   * Retorna un listado con la cantidad de convenios por codigo de registro
   * acumulado por persona
   * @param datos DynaBean, contiene los criterios de bsqueda.
   * @return List, contiene la data para generar el reporte 
   * @throws DAOException
   */    
  public List calculaGrupo(DynaBean datos) throws DAOException {
    List lista = null; 
    StringBuffer sWhere = new StringBuffer(" WHERE P.COD_PER_MODFOR = ")
    .append("C.COD_PER_MODFOR ");
    try{    
      if ("1".equals(datos.getString("cod_est_per"))) {
        sWhere.append(" AND C.cod_est_conv<>'E' AND P.COD_EST_PER = '")
        .append(datos.getString("cod_est_per").replaceAll("'", "''")).append("'");
      } else {
        sWhere.append(" AND P.COD_EST_PER = '")
        .append(datos.getString("cod_est_per").replaceAll("'", "''")).append( "'");
      } 
      if ("1".equals(datos.getString("cod_uniNeg"))) {
        sWhere.append(" AND  (C.cod_uorga like '1%' ")
        .append(" OR C.cod_uorga like '2%') ");
      } else if ("2".equals(datos.getString("cod_uniNeg"))) {
        sWhere .append(" AND  (C.cod_uorga not like '1%' ")
        .append(" AND C.cod_uorga not like '2%') ");
      }      
      if ( datos.getString("cod_uorga")!=null && !"".equals(datos.getString("cod_uorga"))) {
        sWhere.append(" AND C.COD_UORGA = '")
        .append(datos.getString("cod_uorga").replaceAll("'", "''")).append("'");
      }      
      if ( datos.getString("tipo_fecha_sel")!=null && !"".equals(datos.getString("tipo_fecha_sel"))) {
        if (datos.getDate("fecha_ini")!=null && datos.getDate("fecha_fin")!=null) {
          if ("V1".equals(datos.getString("tipo_fecha_sel"))) {
            if (datos.getDate("fecha_ini") != null) {
              sWhere.append(" AND C.fec_ini_conv BETWEEN ? AND ? ");
            }
          } else if ("V2".equals(datos.getString("tipo_fecha_sel"))) {
            if (datos.getDate("fecha_fin") != null) {
              sWhere.append(" AND C.fec_fin_conv BETWEEN ? AND ? ");
            }
          }            
        }
      }      
      if (!"-1".equals(datos.getString("cod_especialidad"))) {
        sWhere.append(" AND C.COD_ESPECIALIDAD = '" )
        .append( datos.getString("cod_especialidad").replaceAll("'", "''")).append( "'");
      }      
      if (datos.getString("cod_registro") != null && !"".equals(datos.getString("cod_registro"))) {
        sWhere.append( " AND P.COD_REGISTRO = '" )
        .append(datos.getString("cod_registro").replaceAll("'", "''")).append("'");
      }      
      if (datos.getString("num_doc") != null && !"".equals(datos.getString("num_doc"))) {
        sWhere.append( " AND P.NUM_DOC = '")
        .append( datos.getString("num_doc").replaceAll("'", "''")).append( "'");
      } 
      if (datos.getString("nom_ape_pat") != null && !"".equals(datos.getString("nom_ape_pat"))) {
        sWhere.append( " AND P.NOM_APE_PAT LIKE '" )
        .append( datos.getString("nom_ape_pat").replaceAll("'", "''")).append( "%'");
      }
      if (datos.getString("nom_ape_mat") != null && !"".equals(datos.getString("nom_ape_mat"))) {
        sWhere.append( " AND P.NOM_APE_MAT LIKE '" )
        .append( datos.getString("nom_ape_mat").replaceAll("'", "''")).append(  "%'");
      }     
      if (datos.getString("nom_per") != null && !"".equals(datos.getString("nom_per"))) {
        sWhere.append( " AND P.NOM_PER LIKE '" )
        .append( datos.getString("nom_per").replaceAll("'", "''")).append(  "%'");
      }      
      StringBuffer sSQL  = QUERY306_SENTENCE.append(sWhere).
      append("GROUP BY P.COD_REGISTRO ORDER BY P.COD_REGISTRO");
      if (log.isDebugEnabled()) {
        log.debug("#########################################");
        log.debug(" grupo Dias Acumulados por persona:");
        log.debug("#########################################");
        log.debug(sSQL);
      }
      setIsolationLevel(T3339DAO.TX_READ_UNCOMMITTED);  
      if (datos.getString("tipo_fecha_sel")!=null && !"".equals(datos.getString("tipo_fecha_sel"))) {
        Object objects[]={datos.getDate("fecha_ini"),datos.getDate("fecha_fin")};
        lista = (ArrayList) executeQuery(datasource, sSQL.toString(),objects);
      } else {
        lista = (ArrayList) executeQuery(datasource, sSQL.toString());
      }
      setIsolationLevel(-1);
    }catch (Exception e) {
      log.error("*** SQL Error ****",e);
      MensajeBean msg = new MensajeBean();
      msg.setMensajeerror("Ha ocurrido un error inesperado al consultar la data para el reporte: ".concat( e.getMessage() ));
      msg.setMensajesol("Por favor, intente nuevamente realizar la operacion, de continuar con el problema comuniquese con el webmaster");
      throw new DAOException (this, msg);     
    } finally {       
    }    
    return lista;
  }
  
  /**
   * Retorna los datos del reporte:relacion de convenios por vencer
   * @param datos DynaBean, contiene los criterios de bsqueda.
   * @return List, contiene la data para generar el reporte 
   * @throws DAOException
   */
  public List generaRepRelConvVencer(DynaBean datos) throws DAOException {
    List lista = null;   
    StringBuffer sWhere = new StringBuffer(" WHERE P.COD_PER_MODFOR = ")
    .append("C.COD_PER_MODFOR AND P.cod_est_per='1' ")
    .append("AND C.cod_est_conv='1' AND C.fec_fin_conv > TODAY ");
    try{    
      if ("1".equals(datos.getString("cod_est_per"))) {
        sWhere.append(" AND C.cod_est_conv<>'E' AND P.COD_EST_PER = '")
        .append(datos.getString("cod_est_per").replaceAll("'", "''")).append("'");
      } else {
        sWhere.append(" AND P.COD_EST_PER = '")
        .append(datos.getString("cod_est_per").replaceAll("'", "''")).append( "'");
      }      
      if ("1".equals(datos.getString("cod_uniNeg"))) {
        sWhere.append(" AND  (C.cod_uorga like '1%' ")
        .append(" OR C.cod_uorga like '2%') ");
      } else if ("2".equals(datos.getString("cod_uniNeg"))) {
        sWhere .append(" AND  (C.cod_uorga not like '1%' ")
        .append(" AND C.cod_uorga not like '2%') ");
      }      
      if ( datos.getString("cod_uorga")!=null && !"".equals(datos.getString("cod_uorga"))) {
        sWhere.append(" AND C.COD_UORGA = '")
        .append(datos.getString("cod_uorga").replaceAll("'", "''")).append("'");
      }      
      if ( datos.getString("tipo_fecha_sel")!=null && !"".equals(datos.getString("tipo_fecha_sel"))) {
        if (datos.getDate("fecha_ini")!=null && datos.getDate("fecha_fin")!=null) {
          if ("V1".equals(datos.getString("tipo_fecha_sel"))) {
            if (datos.getDate("fecha_ini") != null) {
              sWhere.append(" AND C.fec_ini_conv BETWEEN ? AND ? ");
            }
          } else if ("V2".equals(datos.getString("tipo_fecha_sel"))) {
            if (datos.getDate("fecha_fin") != null) {
              sWhere.append(" AND C.fec_fin_conv BETWEEN ? AND ? ");
            }
          }            
        }
      }        
      if (!"-1".equals(datos.getString("cod_modalidad"))) {
        sWhere.append( " AND C.COD_MODALIDAD = '")
        .append(datos.getString("cod_modalidad").replaceAll("'", "''")).append("'");
      }
      if (datos.getString("cod_registro") != null && !"".equals(datos.getString("cod_registro"))) {
        sWhere.append( " AND P.COD_REGISTRO = '" )
        .append(datos.getString("cod_registro").replaceAll("'", "''")).append("'");
      }
      if (datos.getString("num_doc") != null && !"".equals(datos.getString("num_doc"))) {
        sWhere.append( " AND P.NUM_DOC = '")
        .append( datos.getString("num_doc").replaceAll("'", "''")).append( "'");
      }
      if (datos.getString("nom_ape_pat") != null && !"".equals(datos.getString("nom_ape_pat"))) {
        sWhere.append( " AND P.NOM_APE_PAT LIKE '" )
        .append( datos.getString("nom_ape_pat").replaceAll("'", "''")).append( "%'");
      }      
      if (datos.getString("nom_ape_mat") != null && !"".equals(datos.getString("nom_ape_mat"))) {
        sWhere.append( " AND P.NOM_APE_MAT LIKE '" )
        .append( datos.getString("nom_ape_mat").replaceAll("'", "''")).append(  "%'");
      }            
      if (datos.getString("nom_per") != null && !"".equals(datos.getString("nom_per"))) {
        sWhere.append( " AND P.NOM_PER LIKE '" )
        .append( datos.getString("nom_per").replaceAll("'", "''")).append(  "%'");
      }
      final BeanMapper bmConvPorVencer = new BeanMapper(){
        public Object setear(ResultSet rs, Map map) throws SQLException {
          StringBuffer nombre=null;
          ParamBean parametro=null;
          ParamDAO paramDAO = new ParamDAO();          
          try{
            nombre = new StringBuffer("");
            if ( map.get("nom_ape_pat") != null) {
              nombre.append((String)map.get("nom_ape_pat"));
            }
            if ( map.get("nom_ape_mat") != null) {
              nombre=nombre.append(" ");
              nombre=nombre.append((String)map.get("nom_ape_mat"));
            }
            if ( map.get("nom_per") != null) {
              nombre=nombre.append(" ");
              nombre=nombre.append((String)map.get("nom_per"));
            }
            map.put("nombre", nombre.toString());                    
            if ( (map.get("cod_uorga") != null && !"".equals(((String) map.get("cod_uorga")).trim()))){
              ParamBean param = new ParamDAO().buscar("spprm16t12un",0, ((String) map.get("cod_uorga")).trim());
              if ( param!=null && param.getDescripcion()!=null) {                
                if ("15".equals(param.getDescripcion().trim())) {
                  map.put("des_sede", "Lima");
                } else {
                  map.put("des_sede", "Provincias");
                }
                if(param.getCodigo().length()>=1){
                  if ("1".equals(param.getDescripcion().substring(0,1))) {
                    map.put("uni_neg", "Tributos Internos");
                  } else {
                    map.put("uni_neg", "Aduanas");
                  }  
                }
              } else {
                map.put("des_sede", "");
                map.put("uni_neg", "");
              }
            }             
            if ( map.get("cod_modalidad") != null 
              && !"".equals((String)map.get("cod_modalidad"))) {
              parametro=paramDAO.buscar("spprm583t99Corta",0,((String)map.get("cod_modalidad")).trim());
              map.put("des_modalidad",(parametro == null ? "":parametro.getDescripcion()));
            } else {
              map.put("des_modalidad", "");
            }      
            if ( map.get("cod_tip_plaza") != null 
              && !"".equals((String)map.get("cod_tip_plaza"))) {
              parametro=paramDAO.buscar("spprmF09t99",2,((String)map.get("cod_tip_plaza")).trim());
              map.put("des_tipo_plaza",(parametro == null ? "":parametro.getDescripcion()));
            } else {
              map.put("des_tipo_plaza", "");
            }            
            if ( map.get("cod_nivel_edu") != null 
              && !"".equals((String)map.get("cod_nivel_edu"))) {
              parametro=paramDAO.buscar("spprmF01t99",2,((String)map.get("cod_nivel_edu")).trim());
              map.put("des_nivel_estudios",(parametro == null ? "":parametro.getDescripcionCorta()));
            } else {
              map.put("des_nivel_estudios", "");
            }            
            if ( map.get("cod_cenform") != null 
              && !"".equals((String)map.get("cod_cenform"))) {
              parametro=paramDAO.buscar("spprm004t99",2,((String)map.get("cod_cenform")).trim());
              map.put("des_centro_estudios",(parametro == null ? "":parametro.getDescripcionCorta()));
              
            } else {
              map.put("des_centro_estudios", "");
            }            
            if ( map.get("cod_uorga") != null 
              && !"".equals((String)map.get("cod_uorga"))) {
              parametro=paramDAO.buscar("spprm16t12",0,((String)map.get("cod_uorga")).trim());
              map.put("des_uorga",(parametro == null ? "":parametro.getDescripcion()));
            } else {
              map.put("des_uorga", "");
            }            
          } catch (Exception e) {
            log.error("*** SQL Error ****", e);
            throw new SQLException(e.getMessage());
          }     
          return map;
        }
      };
      StringBuffer sSQL = QUERY300_SENTENCE.append(sWhere);      
      if (log.isDebugEnabled()) {
        log.debug("###############################");
        log.debug(" QUERY GENERAR REPORTE");
        log.debug(" Relacion de Convenios por Vencer:");
        log.debug("###############################");
        log.debug(sSQL);
      }
      setIsolationLevel(T3339DAO.TX_READ_UNCOMMITTED);      
      if (datos.getString("tipo_fecha_sel")!=null && !"".equals(datos.getString("tipo_fecha_sel"))) {
        Object objects[]={datos.getDate("fecha_ini"),datos.getDate("fecha_fin")};
        lista = (ArrayList) executeQuery(datasource, sSQL.toString(),objects,bmConvPorVencer);
      } else {
        lista = (ArrayList) executeQuery(datasource, sSQL.toString(),bmConvPorVencer);
      }
      setIsolationLevel(-1);
    }catch (Exception e) {
      log.error(e,e);
      MensajeBean msg = new MensajeBean();
      msg.setMensajeerror("Ha ocurrido un error inesperado al consultar la data para el reporte: ".concat( e.getMessage() ));
      msg.setMensajesol("Por favor, intente nuevamente realizar la operacion, de continuar con el problema comuniquese con el webmaster");
      throw new DAOException (this, msg);     
    } finally {       
    }    
    return lista;
  }
  
  /**
   * Retorna los datos del reporte:ficha
   * @param datos DynaBean 
   * @return List, contiene la data para generar el reporte 
   * @throws DAOException
   */
  public List generaRepFicha(DynaBean datos) throws DAOException {
    List lista = null;   
    StringBuffer sWhere = new StringBuffer("")
    .append(" WHERE (P.COD_PER_MODFOR = C.COD_PER_MODFOR AND ")
    .append(" P.COD_PER_MODFOR = T.COD_PER_MODFOR AND ")
    .append("P.COD_REGISTRO = F.T02COD_PERS AND C.COD_EST_CONV  <> 'E') ");
    try{    
      if ("1".equals(datos.getString("cod_est_per"))) {
        sWhere.append(" AND C.cod_est_conv<>'E' AND P.COD_EST_PER = '")
        .append(datos.getString("cod_est_per").replaceAll("'", "''")).append("'");
      } else {
        sWhere.append(" AND P.COD_EST_PER = '")
        .append(datos.getString("cod_est_per").replaceAll("'", "''")).append( "'");
      }
      if ("1".equals(datos.getString("cod_uniNeg"))) {
        sWhere.append(" AND  (C.cod_uorga like '1%' ")
        .append(" OR C.cod_uorga like '2%') ");
      } else if ("2".equals(datos.getString("cod_uniNeg"))) {
        sWhere .append(" AND  (C.cod_uorga not like '1%' ")
        .append(" AND C.cod_uorga not like '2%') ");
      }      
      if ( datos.getString("cod_uorga")!=null && !"".equals(datos.getString("cod_uorga"))) {
        sWhere.append(" AND C.COD_UORGA = '")
        .append(datos.getString("cod_uorga").replaceAll("'", "''")).append("'");
      }      
      if ( datos.getString("tipo_fecha_sel")!=null && !"".equals(datos.getString("tipo_fecha_sel"))) {
        if (datos.getDate("fecha_ini")!=null && datos.getDate("fecha_fin")!=null) {
          if ("V1".equals(datos.getString("tipo_fecha_sel"))) {
            if (datos.getDate("fecha_ini") != null) {
              sWhere.append(" AND C.fec_ini_conv BETWEEN ? AND ? ");
            }
          } else if ("V2".equals(datos.getString("tipo_fecha_sel"))) {
            if (datos.getDate("fecha_fin") != null) {
              sWhere.append(" AND C.fec_fin_conv BETWEEN ? AND ? ");
            }
          }            
        }
      }        
      if (!"-1".equals(datos.getString("cod_modalidad"))) {
        sWhere.append( " AND C.COD_MODALIDAD = '")
        .append(datos.getString("cod_modalidad").replaceAll("'", "''")).append("'");
      }      
      if (datos.getString("cod_registro") != null && !"".equals(datos.getString("cod_registro"))) {
        sWhere.append( " AND P.COD_REGISTRO = '" )
        .append(datos.getString("cod_registro").replaceAll("'", "''")).append("'");
      }      
      if (datos.getString("num_doc") != null && !"".equals(datos.getString("num_doc"))) {
        sWhere.append( " AND P.NUM_DOC = '")
        .append( datos.getString("num_doc").replaceAll("'", "''")).append( "'");
      }   
      if (datos.getString("nom_ape_pat") != null && !"".equals(datos.getString("nom_ape_pat"))) {
        sWhere.append( " AND P.NOM_APE_PAT LIKE '" )
        .append( datos.getString("nom_ape_pat").replaceAll("'", "''")).append( "%'");
      }      
      if (datos.getString("nom_ape_mat") != null && !"".equals(datos.getString("nom_ape_mat"))) {
        sWhere.append( " AND P.NOM_APE_MAT LIKE '" )
        .append( datos.getString("nom_ape_mat").replaceAll("'", "''")).append(  "%'");
      }   
      if (datos.getString("nom_per") != null && !"".equals(datos.getString("nom_per"))) {
        sWhere.append( " AND P.NOM_PER LIKE '" )
        .append( datos.getString("nom_per").replaceAll("'", "''")).append(  "%'");
      }
     
      final BeanMapper bmFicha = new BeanMapper(){
        public Object setear(ResultSet rs, Map map) throws SQLException {
          StringBuffer nombre=null;          
          ParamBean parametro = null;
          ParamDAO paramDAO = new ParamDAO();
          try{
            nombre = new StringBuffer("");
            if ( map.get("nom_ape_pat") != null) {
              nombre.append((String)map.get("nom_ape_pat"));
            }
            if ( map.get("nom_ape_mat") != null) {
              nombre.append(" ");
              nombre.append((String)map.get("nom_ape_mat"));
            }
            if ( map.get("nom_per") != null) {
              nombre.append(" ");
              nombre.append((String)map.get("nom_per"));
            }
            map.put("nombres", nombre.toString()); 
            int edad=0;
           
            if ( map.get("fec_nacimiento") != null) {
            	FechaBean fec_nacimiento = new FechaBean((Date)map.get("fec_nacimiento"));
            	int anhoFecIn = Integer.valueOf(fec_nacimiento.getAnho()).intValue();
            	int mesFecIn = Integer.valueOf(fec_nacimiento.getMes()).intValue();
            	int diaFecIn = Integer.valueOf(fec_nacimiento.getDia()).intValue();
            	edad =(Integer.valueOf(new FechaBean().getAnho()).intValue()-anhoFecIn);
            	FechaBean hoy = new FechaBean();
            	int fecIni = (Integer.valueOf(hoy.getAnho()).intValue()*10000)+(mesFecIn*100)+diaFecIn;
            	int fecHoy=Integer.valueOf(hoy.getAnho().concat(hoy.getMes()).concat(hoy.getDia())).intValue();
            	if(fecHoy<fecIni){
              	edad--;
              }            	
            }
            map.put("edad", ""+edad);            
            if ( map.get("cod_est_civil") != null 
              && !"".equals((String)map.get("cod_est_civil"))) {
              parametro=paramDAO.buscar("spprm111t99",2,((String)map.get("cod_est_civil")).trim());
              map.put("estado_civil",(parametro == null ? "":parametro.getDescripcion()));
            } else {
              map.put("estado_civil", "");
            }            
            if ( map.get("cod_sexo") != null 
              && !"".equals((String)map.get("cod_sexo"))) {
              if ("F".equals((String)map.get("cod_sexo")) || "2".equals((String)map.get("cod_sexo")) ) { 
                map.put("sexo", "Femenino");
              } else if ("M".equals((String)map.get("cod_sexo")) || "1".equals((String)map.get("cod_sexo")) ){
                map.put("sexo", "Masculino");
              }
            } else {
              map.put("sexo", "");
            }
            StringBuffer direccion = new StringBuffer("");
            if ( map.get("cod_tipo_via") != null 
                && !"".equals((String)map.get("cod_tipo_via"))) {
            	 parametro=paramDAO.buscar("spprm058",4,((String)map.get("cod_tipo_via")).trim());
               
              direccion.append(parametro == null ? "":parametro.getDescripcion());
            }
            if ( map.get("nom_via") != null) {
            	direccion.append(" ");
              direccion.append((String)map.get("nom_via"));
            }
            if ( map.get("num_via") != null) {
              direccion.append(" ");
              direccion.append((String)map.get("num_via"));
            }
            if ( map.get("num_interior") != null) {
              direccion.append(" ");
              direccion.append((String)map.get("num_interior"));
            }
            
            map.put("direccion", direccion.toString());
            /*
            if ( map.get("cod_tipo_zona") != null) {
              map.put("nom_zona", (String)map.get("cod_tipo_zona"));
            }
            */
            if ( map.get("cod_tipo_zona") != null 
                && !"".equals((String)map.get("cod_tipo_zona"))) {
                parametro=paramDAO.buscar("spprm059",4,((String)map.get("cod_tipo_zona")).trim());
                map.put("nom_zona",(parametro == null ? "":parametro.getDescripcion().concat(" ")
                .concat(map.get("nom_zona")!=null?map.get("nom_zona").toString():"")));
              } else {
                map.put("nom_zona", "");
              }
            nombre = new StringBuffer("");
            if ( map.get("nom_tutor") != null) {
              nombre.append((String)map.get("nom_tutor"));
            }
            if ( map.get("nom_apepat_tutor") != null) {
              nombre.append(" ");
              nombre.append((String)map.get("nom_apepat_tutor"));
            }
            if ( map.get("nom_apemat_tutor") != null) {
              nombre.append(" ");
              nombre.append((String)map.get("nom_apemat_tutor"));
            }
            map.put("nombres_t", nombre.toString());            
            if ( map.get("fec_real_baja") != null) {
              map.put("fec_fin", map.get("fec_real_baja_desc"));
            } else if ( map.get("fec_fin_conv") != null){
              map.put("fec_fin", map.get("fec_fin_conv_desc"));
            } else {
              map.put("fec_fin","");
            }            
            if ( map.get("cod_ubigeo_nac") != null 
              && !"".equals((String)map.get("cod_ubigeo_nac"))) {
              parametro=paramDAO.buscar("spprm700t99",ParamDAO.TIPO4,((String)map.get("cod_ubigeo_nac")).trim());
              map.put("lugar_nac",(parametro == null ? "":parametro.getDescripcion()));
            } else {
              map.put("lugar_nac", "");
            }   
            
            if ( map.get("cod_ubigeo_dom") != null 
              && !"".equals((String)map.get("cod_ubigeo_dom"))) {
              parametro=paramDAO.buscar("spprm700t99",ParamDAO.TIPO4,((String)map.get("cod_ubigeo_dom")).trim());
              String distri=(parametro == null ? "":parametro.getDescripcion());
              distri = distri!=""?distri.substring(31,distri.toString().length()):"";
              map.put("distrito",distri);
            } else {
              map.put("distrito", "");
            }            
            if ( map.get("cod_tip_parent") != null 
              && !"".equals((String)map.get("cod_tip_parent"))) {
              parametro=paramDAO.buscar("spprm709t01",4,((String)map.get("cod_tip_parent")).trim());
              map.put("vinculo_t",(parametro == null ? "":parametro.getDescripcion()));
            } else {
              map.put("vinculo_t", "");
            }            
            if ( map.get("cod_cenform") != null 
              && !"".equals((String)map.get("cod_cenform"))) {
              parametro=paramDAO.buscar("spprm004t99",2,((String)map.get("cod_cenform")).trim());
              map.put("centro_estudio",(parametro == null ? "":parametro.getDescripcionCorta()));
            } else {
              map.put("centro_estudio", "");
            }            
            if ( map.get("cod_especialidad") != null 
              && !"".equals((String)map.get("cod_especialidad"))) {
              parametro=paramDAO.buscar("spprm003t99",2,((String)map.get("cod_especialidad")).trim());
              map.put("profesion",(parametro == null ? "":parametro.getDescripcion()));
            } else {
              map.put("profesion", "");
            }            
            if ( map.get("cod_uorga") != null 
              && !"".equals((String)map.get("cod_uorga"))) {
              parametro=paramDAO.buscar("spprm16t12",0,((String)map.get("cod_uorga")).trim());
              map.put("uuoo",(parametro == null ? "":parametro.getDescripcion()));
            } else {
              map.put("uuoo", "");
            }                        
            if ( map.get("cod_situacion") != null 
              && !"".equals((String)map.get("cod_situacion"))) {
              parametro=paramDAO.buscar("spprmF03t99",2,((String)map.get("cod_situacion")).trim());
              map.put("des_situacion",(parametro == null ? "":parametro.getDescripcion()));
            } else {
              map.put("des_situacion", "");
            }            
            if ( map.get("cod_est_per") != null 
              && !"".equals((String)map.get("cod_est_per"))) {
              parametro=paramDAO.buscar("spprmF07t99",2,((String)map.get("cod_est_per")).trim());
              map.put("des_estado",(parametro == null ? "":parametro.getDescripcion()));
            } else {
              map.put("des_estado", "");
            }            
            if ( map.get("cod_modalidad") != null 
              && !"".equals((String)map.get("cod_modalidad"))) {
              parametro=paramDAO.buscar("spprm583t99",0,((String)map.get("cod_modalidad")).trim());
              map.put("des_modalidad",(parametro == null ? "":parametro.getDescripcion()));
            } else {
              map.put("des_modalidad", "");
            }
          } catch (Exception e) {
            log.error("*** SQL Error ****", e);
            throw new SQLException(e.getMessage());
          }     
          return map;
        }
      };
      StringBuffer sSQL = QUERY305_SENTENCE.append(sWhere)
      .append("ORDER BY P.COD_REGISTRO,C.FEC_FIN_CONV DESC");      
      if (log.isDebugEnabled()) {
        log.debug("#########################################");
        log.debug(" QUERY GENERAR REPORTE FICHA PERSONAL:");
        log.debug("#########################################");
        log.debug(sSQL);
      }
      setIsolationLevel(T3339DAO.TX_READ_UNCOMMITTED);
      if (datos.getString("tipo_fecha_sel")!=null && !"".equals(datos.getString("tipo_fecha_sel"))) {
        Object objects[]={datos.getDate("fecha_ini"),datos.getDate("fecha_fin")};
        lista = (ArrayList) executeQuery(datasource, sSQL.toString(),objects,bmFicha);
      } else {
        lista = (ArrayList) executeQuery(datasource, sSQL.toString(),bmFicha);
      }
      setIsolationLevel(-1);
    }catch (Exception e) {
      log.error(e,e);
      MensajeBean msg = new MensajeBean();
      msg.setMensajeerror("Ha ocurrido un error inesperado al consultar la data para el reporte: ".concat( e.getMessage() ));
      msg.setMensajesol("Por favor, intente nuevamente realizar la operacion, de continuar con el problema comuniquese con el webmaster");
      throw new DAOException (this, msg);     
    } finally {       
    }    
    return lista;
  }  
  
  /**
   * Retorna los datos del reporte Relacin de Personal a pagar media Subvencin
   * @param datos DynaBean, contiene los criterios de bsqueda. 
   * @return List, contiene la data para generar el reporte 
   * @throws DAOException
   */
  public List generaRepPersMedSubv(DynaBean datos) throws DAOException {
    List lista = null;                                               
    StringBuffer sWhere = new StringBuffer("");     
    try{
    	if ("1".equals(datos.getString("cod_est_per"))) {
        sWhere.append(" AND C.cod_est_conv<>'E' AND P.COD_EST_PER = '")
        .append(datos.getString("cod_est_per").replaceAll("'", "''")).append("'");
      } else {
        sWhere.append(" AND P.COD_EST_PER = '")
        .append(datos.getString("cod_est_per").replaceAll("'", "''")).append( "'");
      }
    	if ("1".equals(datos.getString("cod_uniNeg"))) {
        sWhere.append(" AND  (C.cod_uorga like '1%' ")
        .append(" OR C.cod_uorga like '2%') ");
      } else if ("2".equals(datos.getString("cod_uniNeg"))) {
        sWhere .append(" AND  (C.cod_uorga not like '1%' ")
        .append(" AND C.cod_uorga not like '2%') ");
      }
      if ( datos.getString("cod_uorga")!=null && !"".equals(datos.getString("cod_uorga"))) {
        sWhere.append(" AND C.COD_UORGA = '")
        .append(datos.getString("cod_uorga").replaceAll("'", "''")).append("'");
      }      
      if ( datos.getString("tipo_fecha_sel")!=null && !"".equals(datos.getString("tipo_fecha_sel"))) {
        if (datos.getDate("fecha_ini")!=null && datos.getDate("fecha_fin")!=null) {
          if ("V1".equals(datos.getString("tipo_fecha_sel"))) {
            if (datos.getDate("fecha_ini") != null) {
              sWhere.append(" AND C.fec_ini_conv BETWEEN ? AND ? ");
            }
          } else if ("V2".equals(datos.getString("tipo_fecha_sel"))) {
            if (datos.getDate("fecha_fin") != null) {
              sWhere.append(" AND C.fec_fin_conv BETWEEN ? AND ? ");
            }
          }            
        }
      }        
      if (!"-1".equals(datos.getString("cod_modalidad"))) {
        sWhere.append( " AND C.COD_MODALIDAD = '")
        .append(datos.getString("cod_modalidad").replaceAll("'", "''")).append("'");
      }            
      final BeanMapper bmPagMedSuv = new BeanMapper(){
        public Object setear(ResultSet rs, Map map) throws SQLException {
        	int meses=0;
          int dies=0;
          try{
            StringBuffer nombre=null;
            ParamBean parametro=null;
            ParamDAO paramDAO = new ParamDAO();
            nombre = new StringBuffer("");
            if ( map.get("nom_ape_pat") != null) {
              nombre.append((String)map.get("nom_ape_pat"));
            }
            if ( map.get("nom_ape_mat") != null) {
              nombre=nombre.append(" ");
              nombre=nombre.append((String)map.get("nom_ape_mat"));
            }
            if ( map.get("nom_per") != null) {
              nombre=nombre.append(" ");
              nombre=nombre.append((String)map.get("nom_per"));
            }
            map.put("nombre", nombre.toString());
            //String fechaInicioConv =(String)map.get("fec_ini_conv");
            
            String fechaCumple="";
            if ( map.get("fec_ini_conv") != null) {
            	FechaBean fechaInicioConvD=new FechaBean((Date)map.get("fec_ini_conv"));
            	int anhoFecIn = Integer.valueOf(fechaInicioConvD.getAnho()).intValue();
            	int mesFecIn = Integer.valueOf(fechaInicioConvD.getMes()).intValue();
            	int diaFecIn = Integer.valueOf(fechaInicioConvD.getDia()).intValue();
            	FechaBean hoy = new FechaBean();
            	int fecIni=0;
            	int fecHoy=Integer.valueOf(hoy.getAnho().concat(hoy.getMes()).concat(hoy.getDia())).intValue();
            	
            	do{
            		mesFecIn=mesFecIn+6;
	              if(mesFecIn>12){	              	
	              	mesFecIn=mesFecIn-12;
	              	anhoFecIn++;
	              }
	              fecIni = (anhoFecIn*10000)+(mesFecIn*100)+diaFecIn;
              }while (fecHoy>fecIni);
            	fechaCumple = diaFecIn+"/"+mesFecIn+"/"+anhoFecIn;
            }
            map.put("fecha_cumple", fechaCumple);      
            if ( (map.get("cod_uorga") != null && !"".equals(((String) map.get("cod_uorga")).trim()))){
              ParamBean param = new ParamDAO().buscar("spprm16t12un",0, ((String) map.get("cod_uorga")).trim());
              if ( param!=null && param.getDescripcion()!=null) {
                if(param.getCodigo().length()>=1){
                  if ("1".equals(param.getDescripcion().substring(0,1))) {
                    map.put("uni_neg", "Tributos Internos");
                  } else {
                    map.put("uni_neg", "Aduanas");
                  }  
                }
              } else {
                map.put("des_sede", "");
                map.put("uni_neg", "");
              }
            } 
            if ( map.get("cod_modalidad") != null 
                && !"".equals((String)map.get("cod_modalidad"))) {
                parametro=paramDAO.buscar("spprm583t99Corta",0,((String)map.get("cod_modalidad")).trim());
                map.put("des_modalidad",(parametro == null ? "":parametro.getDescripcion()));
            } else {
              map.put("des_modalidad", "");
            }            
            if ( map.get("cod_tip_plaza") != null 
                && !"".equals((String)map.get("cod_tip_plaza"))) {
                parametro=paramDAO.buscar("spprmF09t99",2,((String)map.get("cod_tip_plaza")).trim());
                map.put("des_tipo_plaza",(parametro == null ? "":parametro.getDescripcion()));
              } else {
                map.put("des_tipo_plaza", "");
            }
            if ( map.get("cod_nivel_edu") != null 
              && !"".equals((String)map.get("cod_nivel_edu"))) {
              parametro=paramDAO.buscar("spprmF01t99",2,((String)map.get("cod_nivel_edu")).trim());
              map.put("des_nivel_estudios",(parametro == null ? "":parametro.getDescripcionCorta()));
            } else {
              map.put("des_nivel_estudios", "");
            }
            if ( map.get("cod_cenform") != null 
              && !"".equals((String)map.get("cod_cenform"))) {
              parametro=paramDAO.buscar("spprm004t99",2,((String)map.get("cod_cenform")).trim());
              map.put("des_centro_estudios",(parametro == null ? "":parametro.getDescripcionCorta()));
            } else {
              map.put("des_centro_estudios", "");
            }
            if ( map.get("cod_est_conv") != null 
              && !"".equals((String)map.get("cod_est_conv"))) {
              parametro=paramDAO.buscar("spprmF07t99",2,((String)map.get("cod_est_conv")).trim());
              map.put("des_est_conv",(parametro == null ? "":parametro.getDescripcion()));
            } else {
              map.put("des_est_conv", "");
            }
            if ( map.get("cod_tip_baja") != null 
              && !"".equals((String)map.get("cod_tip_baja"))) {
              parametro=paramDAO.buscar("spprmF08t99",2,((String)map.get("cod_tip_baja")).trim());
              map.put("des_mot_baja",(parametro == null ? "":parametro.getDescripcion().trim()));
            } else {
              map.put("des_mot_baja","");
            }
            if ( map.get("cod_uorga") != null 
              && !"".equals((String)map.get("cod_uorga"))) {
              parametro=paramDAO.buscar("spprm16t12",0,((String)map.get("cod_uorga")).trim());
              map.put("des_uorga",(parametro == null ? "":parametro.getDescripcion()));
            } else {
              map.put("des_uorga", "");
            }
            if ( map.get("cod_situacion") != null 
              && !"".equals((String)map.get("cod_situacion"))) {
              parametro=paramDAO.buscar("spprmF03t99",2,((String)map.get("cod_situacion")).trim());
              map.put("des_situacion",(parametro == null ? "":parametro.getDescripcion()));
            } else {
              map.put("des_situacion", "");
            }
            String fechaInicioConv =(String)map.get("fec_ini_conv_desc");                                                         
            String fechaFinConv = (String)map.get("fec_fin_conv_desc");
            int anios=0;
            int diaInicio = Integer.parseInt(fechaInicioConv.substring(0, 2));                                                  
            int mesInicio = Integer.parseInt(fechaInicioConv.substring(3, 5));                                                  
            int anioInicio = Integer.parseInt(fechaInicioConv.substring(6, 10));
            int diaFin = Integer.parseInt(fechaFinConv.substring(0, 2)) ;                                                
            int mesFin = Integer.parseInt(fechaFinConv.substring(3, 5));                                                  
            int anioFin = Integer.parseInt(fechaFinConv.substring(6, 10));
            //una variable auxiliar donde guardaremos el nmero de das 
            //que tiene el mes anterior al mes Inicio
            int b = 0;                                                                                 
            int mes = mesInicio-1;                                                                      
            if ( mes==2) {                                                                              
              if ( (anioFin%4==0 && anioFin%100!=0) || anioFin%400==0) {   
                b = 29;                                                                                  
              } else {                                                                                    
                b = 28;                                                                                  
              }                                                                                         
            } else if ( mes<=7) {                                                                         
              if ( mes==0) {                                                                              
                b = 31;                                                                                 
              } else if(mes%2==0){                                                                     
                b = 30;                                                                                
              } else {                                                                                   
                b = 31;                                                                                
              }                                                                                       
            } else if ( mes>7) {                                                                        
              if ( mes%2==0) {                                                                          
                b = 31;                                                                                
              } else {                                                                                   
                b = 30;                                                                                
              }                                                                                       
            }                                                                                       
            if ( (anioInicio>anioFin) || (anioInicio==anioFin && mesInicio>mesFin) || 
                (anioInicio==anioFin && mesInicio == mesFin && diaInicio>diaFin)) {       
                 //La fecha de inicio ha de ser anterior a la fecha Actual";                         
            } else {                                                                                  
              if ( mesInicio <= mesFin) {                                                           
                anios = anioFin - anioInicio;                                                     
                if ( diaInicio <= diaFin) {                                                           
                  meses = mesFin - mesInicio;                                                       
                  dies = diaFin - diaInicio;                                                        
                } else {                                                                                  
                  if ( mesFin == mesInicio) {                                                           
                    anios = anios - 1;                                                                    
                  }                                                                                       
                  meses = (mesFin - mesInicio - 1 + 12) % 12;                                       
                  dies = b-(diaInicio-diaFin);                                                     
                }                                                                                       
              } else {                                                                                  
                anios = anioFin - anioInicio - 1;                                                 
                if(diaInicio > diaFin){                                                            
                  meses = mesFin - mesInicio -1 +12;                                                
                  dies = b - (diaInicio-diaFin);                                                   
                } else {                                                                                  
                  meses = mesFin - mesInicio + 12;                                                  
                  dies = diaFin - diaInicio;                                                        
                }                                                                                       
              }                                                                                       
            }            
            map.put("meses",""+meses+"/"+dies);    
            map.put("mesSubv",""+meses);                                   
          } catch (Exception e) {
            log.error("*** SQL Error ****", e);
            throw new SQLException(e.getMessage());
          }
          return map;
        }
      };
      StringBuffer sSQL = QUERY303_SENTENCE.append(sWhere);
      if (log.isDebugEnabled()) {
        log.debug("###############################");
        log.debug(" QRY GENERAR RPT Rel d Personal a pagar media Subvencin:");
        log.debug("###############################");
        log.debug(sSQL);
      }      
      setIsolationLevel(T3339DAO.TX_READ_UNCOMMITTED);
      if (datos.getString("tipo_fecha_sel")!=null && !"".equals(datos.getString("tipo_fecha_sel"))) {
        Object objects[]={datos.getDate("fecha_ini"), datos.getDate("fecha_fin")};
        lista = executeQuery(datasource, sSQL.toString(),objects,bmPagMedSuv);        
      } else {
        lista = executeQuery(datasource, sSQL.toString(),bmPagMedSuv);       
      }
      setIsolationLevel(-1);
    }catch (Exception e) {
      log.error(e,e);
      MensajeBean msg = new MensajeBean();
      msg.setMensajeerror("Ha ocurrido un error inesperado al consultar la data para el reporte: ".concat( e.getMessage() ));
      msg.setMensajesol("Por favor, intente nuevamente realizar la operacion, de continuar con el problema comuniquese con el webmaster");
      throw new DAOException (this, msg);     
    } finally {       
    }    
    return lista;
  }
  
  /**
   * Retorna los datos del reporte: Plantilla apertura de cuenta
   * @param datos DynaBean, contiene los criterios de bsqueda.    
   * @return List, contiene la data para generar el reporte 
   * @throws DAOException 
   */
  public List generaRepPlanApCta(DynaBean datos) throws DAOException {
    List lista = null; 
    StringBuffer sWhere = new StringBuffer(" WHERE P.COD_PER_MODFOR = ")
    .append("C.COD_PER_MODFOR ");  
    try{    
      if ("1".equals(datos.getString("cod_est_per"))) {
        sWhere.append(" AND C.cod_est_conv<>'E' AND P.COD_EST_PER = '")
        .append(datos.getString("cod_est_per").replaceAll("'", "''")).append("'");
      } else {
        sWhere.append(" AND P.COD_EST_PER = '")
        .append(datos.getString("cod_est_per").replaceAll("'", "''")).append( "'");
      }      
      if ("1".equals(datos.getString("cod_uniNeg"))) {
        sWhere.append(" AND  (C.cod_uorga like '1%' ")
        .append(" OR C.cod_uorga like '2%') ");
      } else if ("2".equals(datos.getString("cod_uniNeg"))) {
        sWhere .append(" AND  (C.cod_uorga not like '1%' ")
        .append(" AND C.cod_uorga not like '2%') ");
      }
      if ( datos.getString("cod_uorga")!=null && !"".equals(datos.getString("cod_uorga"))) {
        sWhere.append(" AND C.COD_UORGA = '")
        .append(datos.getString("cod_uorga").replaceAll("'", "''")).append("'");
      }
      if ( datos.getString("tipo_fecha_sel")!=null && !"".equals(datos.getString("tipo_fecha_sel"))) {
        if (datos.getDate("fecha_ini")!=null && datos.getDate("fecha_fin")!=null) {
          if ("V1".equals(datos.getString("tipo_fecha_sel"))) {
            if (datos.getDate("fecha_ini") != null) {
              sWhere.append(" AND C.fec_ini_conv BETWEEN ? AND ? ");
            }
          } else if ("V2".equals(datos.getString("tipo_fecha_sel"))) {
            if (datos.getDate("fecha_fin") != null) {
              sWhere.append(" AND C.fec_fin_conv BETWEEN ? AND ? ");
            }
          }            
        }
      }
      if (datos.getString("cod_registro") != null && !"".equals(datos.getString("cod_registro"))) {
        sWhere.append( " AND P.COD_REGISTRO = '" )
        .append(datos.getString("cod_registro").replaceAll("'", "''")).append("'");
      }
      if (datos.getString("num_doc") != null && !"".equals(datos.getString("num_doc"))) {
        sWhere.append( " AND P.NUM_DOC = '")
        .append( datos.getString("num_doc").replaceAll("'", "''")).append( "'");
      }
      if (datos.getString("nom_ape_pat") != null && !"".equals(datos.getString("nom_ape_pat"))) {
        sWhere.append( " AND P.NOM_APE_PAT LIKE '" )
        .append( datos.getString("nom_ape_pat").replaceAll("'", "''")).append( "%'");
      }
      if (datos.getString("nom_ape_mat") != null && !"".equals(datos.getString("nom_ape_mat"))) {
        sWhere.append( " AND P.NOM_APE_MAT LIKE '" )
        .append( datos.getString("nom_ape_mat").replaceAll("'", "''")).append(  "%'");
      }   
      if (datos.getString("nom_per") != null && !"".equals(datos.getString("nom_per"))) {
        sWhere.append( " AND P.NOM_PER LIKE '" )
        .append( datos.getString("nom_per").replaceAll("'", "''")).append(  "%'");
      } 
      final BeanMapper bmPlanApCta = new BeanMapper(){
        public Object setear(ResultSet rs, Map map) throws SQLException {
          try{
            ParamBean parametro = null;
            ParamDAO paramDAO = new ParamDAO(); 
            StringBuffer direccion = null;
            String numDoc = "";
            if ( map.get("cod_nacionalidad") != null 
                && !"".equals((String)map.get("cod_nacionalidad"))) {
                parametro=paramDAO.buscar("spprmI00t99",2,((String)map.get("cod_nacionalidad")).trim());
                map.put("des_nacionalidad",(parametro == null ? "":parametro.getDescripcion()));
              } else {
                map.put("des_nacionalidad","");
              }  
            	if ( map.get("cod_ubigeo_dom") != null 
                && !"".equals((String)map.get("cod_ubigeo_dom"))) {
                parametro=paramDAO.buscar("spprm700t99",ParamDAO.TIPO4,((String)map.get("cod_ubigeo_dom")).trim());
                String codProvDep="";
                String codProvDepTemp="";
                if (parametro!=null && parametro.getDescripcion()!=null){
                	StringTokenizer tokenizer=new StringTokenizer(parametro.getDescripcion().toString(),"  ");
                	if (tokenizer.hasMoreTokens()){
                		codProvDepTemp = tokenizer.nextToken();
                	}
                	if (tokenizer.hasMoreTokens()){
                		codProvDep = codProvDepTemp.concat(" ").concat(tokenizer.nextToken());
                	}
                } 
                map.put("prov_dpto",codProvDep);
              } else {
                map.put("prov_dpto","");
              } 
            	if ( map.get("num_doc") != null 
                  && !"".equals((String)map.get("num_doc"))) {
            			numDoc = (String)map.get("num_doc");
            			while(numDoc.length()<8){ 
            				numDoc = "0".concat(numDoc); 
            			} 
            			 map.put("num_doc",numDoc);
                } else {
                  map.put("num_doc","00000000");
                }
            	
              direccion = new StringBuffer("");
              if ( map.get("nom_via") != null) {
                direccion.append((String)map.get("nom_via"));
              }
              if ( map.get("num_via") != null) {
                direccion.append(" ");
                direccion.append((String)map.get("num_via"));
              }
              if ( map.get("num_interior") != null) {
                direccion.append(" ");
                direccion.append((String)map.get("num_interior"));
              }
              if ( map.get("nom_zona") != null) {
                direccion.append(" ");
                direccion.append((String)map.get("nom_zona"));
              }
              map.put("direccion", direccion.toString());
          } catch (Exception e) {
            log.error("*** SQL Error ****", e);
            throw new SQLException(e.getMessage());
          }     
          return map;
        }
      };
      StringBuffer sSQL = QUERY300_SENTENCE.append(sWhere);
      if (log.isDebugEnabled()) {
        log.debug("###############################");
        log.debug(" QRY GENERAR RPT generaRepPlanApCta:");
        log.debug("###############################");
        log.debug(sSQL);
      }
      setIsolationLevel(T3339DAO.TX_READ_UNCOMMITTED);
      if (datos.getString("tipo_fecha_sel")!=null && !"".equals(datos.getString("tipo_fecha_sel"))) {
        Object objects[]={datos.getDate("fecha_ini"),datos.getDate("fecha_fin")};
        lista = executeQuery(datasource, sSQL.toString(),objects,bmPlanApCta);
      } else {
        lista = executeQuery(datasource, sSQL.toString(),bmPlanApCta);
      }
      setIsolationLevel(-1);
    }catch (Exception e) {
      log.error("*** SQL Error ****",e);
      MensajeBean msg = new MensajeBean();
      msg.setMensajeerror("Ha ocurrido un error inesperado al consultar la data para el reporte: ".concat( e.getMessage() ));
      msg.setMensajesol("Por favor, intente nuevamente realizar la operacion, de continuar con el problema comuniquese con el webmaster");
      throw new DAOException (this, msg);     
    } finally {       
    }    
    return lista;
  }
  /**
  * Retorna los datos que se usaran para generar un convenio
  * @param params DynaBean, contiene los criterios de bsqueda.  
  * @return List, contiene la data para generar el formato 
  * @throws DAOException 
  */  
  public List generaFormConv(DynaBean params) throws DAOException {
    List lista = null;
    StringBuffer sWhere = new StringBuffer("");
    StringBuffer sSQL;    
    try{
    	if ("1".equals(params.getString("cmbcod_uniNeg"))) {
        sWhere.append(" AND  (C.cod_uorga like '1%' ")
        .append(" OR C.cod_uorga like '2%') ");
      } else if ("2".equals(params.getString("cmbcod_uniNeg"))) {
        sWhere .append(" AND  (C.cod_uorga not like '1%' ")
        .append(" AND C.cod_uorga not like '2%') ");
      }    	
      if (params.getString("cod_uorga") != null && !"".equals(params.getString("cod_uorga"))) {
        sWhere.append(" AND C.COD_UORGA = '")
        .append(params.getString("cod_uorga").replaceAll("'", "''")).append("'");
      }     
      if (params.getString("cmbcod_sede") != null && !"0".equals(params.getString("cmbcod_sede"))) {    
        if ("1".equals(params.getString("cmbcod_sede"))) { 
          sWhere.append(" AND  T.COD_DPTO ='15' "); 
        } else if ("2".equals(params.getString("cmbcod_sede"))) { 
          sWhere.append(" AND  T.COD_DPTO !='15' "); 
        } 
      }   
      if (params.getString("rbtfechaIniFin") != null 
      && !"".equals(params.getString("rbtfechaIniFin"))) {
        if (params.getDate("txtdel")!=null && params.getDate("txtal")!=null){
          if ("I".equals(params.getString("rbtfechaIniFin"))) { 
            if (params.getDate("txtdel") != null) {
              sWhere.append(" AND C.FEC_INI_CONV BETWEEN ? AND ? ");
            }
          } else if ("F".equals(params.getString("rbtfechaIniFin"))) {
            if (params.getDate("txtal") != null) {
              sWhere.append(" AND C.FEC_FIN_CONV BETWEEN ? AND ? ");
            }
          }
        }
      }  
      if (params.getString("cmbcod_modalidad") != null 
      && !"-1".equals(params.getString("cmbcod_modalidad"))) { 
        sWhere.append(" AND C.COD_MODALIDAD = '")
          .append(params.getString("cmbcod_modalidad").replaceAll("'", "''")).append("'"); 
      }      
      if (params.getString("txtdni") != null && !"".equals(params.getString("txtdni"))) { 
        sWhere.append(" AND P.NUM_DOC = '")
          .append(params.getString("txtdni").replaceAll("'", "''")).append("'"); 
      }
      if (params.getString("txtape_paterno") != null && !"".equals(params.getString("txtape_paterno"))) { 
        sWhere.append(" AND P.NOM_APE_PAT LIKE '")
          .append(params.getString("txtape_paterno").replaceAll("'", "''")).append("%'"); 
      }       
      if (params.getString("txtape_materno") != null && !"".equals(params.getString("txtape_materno"))) { 
        sWhere.append(" AND P.NOM_APE_MAT LIKE '")
          .append(params.getString("txtape_materno").replaceAll("'", "''")).append("%'"); 
      }       
      if (params.getString("txtnombres") != null && !"".equals(params.getString("txtnombres"))) { 
        sWhere.append(" AND P.NOM_PER LIKE '")
          .append(params.getString("txtnombres").replaceAll("'", "''")).append("%'"); 
      }      
      final BeanMapper bmConvenio = new BeanMapper(){
        public Object setear(ResultSet rs, Map map) throws SQLException {
        	StringBuffer nombre = null;
          try{
            if ( map.get("t02ap_pate") == null) {
              map.put("t02ap_pate", "");  
            } 
            if ( map.get("t02ap_mate") == null) {
              map.put("t02ap_mate", "");  
            }
            if ( map.get("t02nombres") == null) {
              map.put("t02nombres", "");  
            }
            nombre = new StringBuffer("");
            if ( map.get("nom_tutor") != null) {
              nombre.append((String)map.get("nom_tutor"));
            }
            if ( map.get("nom_apepat_tutor") != null) {
              nombre.append(" ");
              nombre.append((String)map.get("nom_apepat_tutor"));
            }
            if ( map.get("nom_apemat_tutor") != null) {
              nombre.append(" ");
              nombre.append((String)map.get("nom_apemat_tutor"));
            }
            map.put("nombres_t", nombre.toString());            
            String fechaInicioConv =(String)map.get("fec_ini_conv_desc");                                                         
            String fechaFinConv = (String)map.get("fec_fin_conv_desc");  
            map.put("ndias",String.valueOf(FechaBean.getDiferencia(new FechaBean(fechaFinConv).getCalendar(),new FechaBean(fechaInicioConv).getCalendar(),Calendar.DAY_OF_MONTH)));
            if (log.isDebugEnabled()){
              log.debug("el NumeroDias es " + String.valueOf(FechaBean.getDiferencia(new FechaBean(fechaFinConv).getCalendar(),new FechaBean(fechaInicioConv).getCalendar(),Calendar.DAY_OF_MONTH)));
            }
            String fechaSurcripConv =map.get("fec_surcrip")!=null?(String)map.get("fec_surcrip_desc"):"";
            String fechaSurcripConvMenosUno="";
            if (!"".equals(fechaSurcripConv)){
            	FechaBean fecha = new FechaBean(fechaSurcripConv);
              fecha.getCalendar().add(Calendar.DAY_OF_YEAR,-1);
              fechaSurcripConvMenosUno=fecha.getFormatDate("dd/MM/yyyy");
            }
            int anios = 0;                                                 
            int meses = 0;                     
            int diaInicio = Integer.parseInt(fechaInicioConv.substring(0, 2));                                                  
            int mesInicio = Integer.parseInt(fechaInicioConv.substring(3, 5));                                                  
            int anioInicio = Integer.parseInt(fechaInicioConv.substring(6, 10));
            int diaFin = Integer.parseInt(fechaFinConv.substring(0, 2)) ;                                                
            int mesFin = Integer.parseInt(fechaFinConv.substring(3, 5));                                                  
            int anioFin = Integer.parseInt(fechaFinConv.substring(6, 10));  
            //una variable auxiliar donde guardaremos el nmero de das 
            //que tiene el mes anterior al mes Inicio
            int b = 0;                                                                                 
            int mes = mesInicio-1;                                                                      
            if ( mes==2) {                                                                              
              if ( (anioFin%4==0 && anioFin%100!=0) || anioFin%400==0) {   
                b = 29;                                                                                  
              } else {                                                                                    
                b = 28;                                                                                  
              }                                                                                         
            } else if ( mes<=7) {                                                                         
              if ( mes==0) {                                                                              
                b = 31;                                                                                 
              } else if(mes%2==0){                                                                     
                b = 30;                                                                                
              } else {                                                                                   
                b = 31;                                                                                
              }                                                                                       
            } else if ( mes>7) {                                                                        
              if ( mes%2==0) {                                                                          
                b = 31;                                                                                
              } else {                                                                                   
                b = 30;                                                                                
              }                                                                                       
            }
            int dies = 0;
            if ( (anioInicio>anioFin) || (anioInicio==anioFin && mesInicio>mesFin) || 
              (anioInicio==anioFin && mesInicio == mesFin && diaInicio>diaFin)) {                    
            } else {                                                                                  
              if ( mesInicio <= mesFin) {                                                           
                anios = anioFin - anioInicio;                                                     
                if ( diaInicio <= diaFin) {                                                           
                  meses = mesFin - mesInicio;                                                       
                  dies = diaFin - diaInicio;                                                        
                } else {                                                                                  
                  if ( mesFin == mesInicio) {                                                           
                    anios = anios - 1;                                                                    
                  }                                                                                       
                  meses = (mesFin - mesInicio - 1 + 12) % 12;                                       
                  dies = b-(diaInicio-diaFin);                                                     
                }                                                                                       
              } else {                                                                                  
                anios = anioFin - anioInicio - 1;                                                 
                if(diaInicio > diaFin){                                                            
                  meses = mesFin - mesInicio -1 +12;                                                
                  dies = b - (diaInicio-diaFin);                                                   
                } else {                                                                                  
                  meses = mesFin - mesInicio + 12;                                                  
                  dies = diaFin - diaInicio;                                                        
                }                                                                                       
              }                                                                                       
            }
            map.put("tot_meses",""+meses);
            map.put("tot_dias",""+dies);
            map.put("fechaSurcripConvMenosUno",fechaSurcripConvMenosUno);
          } catch (Exception e) {
            log.error("*** SQL Error ****", e);
            throw new SQLException(e.getMessage());
          }     
          return map;
        }
      }; 
      sSQL = QUERY501_SENTENCE;
      sSQL.append(sWhere);
      if (log.isDebugEnabled()) {
        log.debug("###############################");
        log.debug("QUERY GENERAR FORMATOS:CONVENIO");
        log.debug("###############################");
        log.debug(sSQL);
      }
      setIsolationLevel(T3339DAO.TX_READ_UNCOMMITTED);
      if (params.getString("rbtfechaIniFin") != null && !"".equals(params.getString("rbtfechaIniFin"))) {
        Object objects[]={params.getString("txtnum_reg"), params.getDate("txtdel"),params.getDate("txtal")};
        lista =  executeQuery(datasource, sSQL.toString(),objects,bmConvenio);
      }  else {
      	lista = executeQuery(datasource, sSQL.toString(),
        new Object[]{params.getString("txtnum_reg")},bmConvenio);
      }
      setIsolationLevel(-1);
    }catch (Exception e) {
      log.error("*** SQL Error ****",e);
      MensajeBean msg = new MensajeBean();
      msg.setMensajeerror("Ha ocurrido un error inesperado al consultar la data para el formato: ".concat( e.getMessage() ));
      msg.setMensajesol("Por favor, intente nuevamente realizar la operacion, de continuar con el problema comuniquese con el webmaster");
      throw new DAOException (this, msg);     
    } finally {       
    }    
    return lista; 
  }
   
  /**
   * Metodo que devuelve los valores para generar la Constancia y el certificado
   * @param params DynaBean, contiene los criterios de bsqueda. 
   * @return List, contiene la data para generar el formato 
   * @throws DAOException 
   */   
  public List generaConst(DynaBean params) throws DAOException {
    List lista = null;
    StringBuffer sWhere = new StringBuffer("");       
    try{    
      if ("1".equals(params.getString("cmbcod_uniNeg"))) {
        sWhere.append(" AND  (C.cod_uorga like '1%' ")
        .append(" OR C.cod_uorga like '2%') ");
      } else if ("2".equals(params.getString("cmbcod_uniNeg"))) {
        sWhere .append(" AND  C.cod_uorga not like '1%' ")
        .append(" AND C.cod_uorga not like '2%' ");
      }
      if (params.getString("cod_uorga") != null && !"".equals(params.getString("cod_uorga"))) {
        sWhere.append(" AND C.COD_UORGA = '")
        .append(params.getString("cod_uorga").replaceAll("'", "''")).append("'");
      }
      if (params.getString("cmbcod_sede") != null && !"0".equals(params.getString("cmbcod_sede"))) {    
        if ("1".equals(params.getString("cmbcod_sede"))){ 
          sWhere.append(" AND  T.COD_DPTO ='15' "); 
        } else if("2".equals(params.getString("cmbcod_sede"))){ 
          sWhere.append(" AND  T.COD_DPTO !='15' "); 
        } 
      }
      if (params.getString("rbtfechaIniFin")!=null && !"".equals(params.getString("rbtfechaIniFin"))) {
        if (params.getDate("txtdel")!=null && params.getDate("txtal")!=null) {
          if ("I".equals(params.getString("rbtfechaIniFin"))) {
            if (params.getDate("txtdel") != null) {
              sWhere.append(" AND C.fec_ini_conv BETWEEN ? AND ? ");
            }
          } else if ("F".equals(params.getString("rbtfechaIniFin"))) {
            if (params.getDate("txtal") != null) {
              sWhere.append(" AND C.fec_fin_conv BETWEEN ? AND ? ");
            }
          }            
        }
      }      
      if (params.getString("cmbcod_modalidad") != null && !"-1".equals(params.getString("cmbcod_modalidad"))) { 
        sWhere.append(" AND C.COD_MODALIDAD = '")
        .append(params.getString("cmbcod_modalidad").replaceAll("'", "''")).append("'"); 
      }       
      if (params.getString("txtnum_reg") != null && !"".equals(params.getString("txtnum_reg"))) { 
        sWhere.append(" AND P.COD_REGISTRO = '")
        .append(params.getString("txtnum_reg").replaceAll("'", "''")).append("'"); 
      }       
      if (params.getString("txtdni") != null && !"".equals(params.getString("txtdni"))) { 
        sWhere.append(" AND P.NUM_DOC = '")
        .append(params.getString("txtdni").replaceAll("'", "''")).append("'"); 
      }
      if (params.getString("txtape_paterno") != null && !"".equals(params.getString("txtape_paterno"))) { 
        sWhere.append(" AND P.NOM_APE_PAT LIKE '")
        .append(params.getString("txtape_paterno").replaceAll("'", "''")).append("%'"); 
      }
      if (params.getString("txtape_materno") != null && !"".equals(params.getString("txtape_materno"))) { 
        sWhere.append(" AND P.NOM_APE_MAT LIKE '")
        .append(params.getString("txtape_materno").replaceAll("'", "''")).append("%'"); 
      }
      if (params.getString("txtnombres") != null && !"".equals(params.getString("txtnombres"))) { 
        sWhere.append(" AND P.NOM_PER LIKE '")
        .append(params.getString("txtnombres").replaceAll("'", "''")).append("%'"); 
      }
      
      StringBuffer sSQL = new StringBuffer (QUERY500_SENTENCE.toString()).append(sWhere);            
      if (log.isDebugEnabled()) {
        log.debug("#########################################");
        log.debug("    QUERY GENERAR FORMATOS : CONST     ");
        log.debug("#########################################");
        log.debug(sSQL);
      }      
      final BeanMapper bmConstCert = new BeanMapper(){
        public Object setear(ResultSet rs, Map map) throws SQLException {
          try{
            String intendencia="";            
            if ( map.get("cod_uorga") != null 
              && !"".equals((String)map.get("cod_uorga"))) {
              if (map.get("cod_uorga").toString().length()>1) {
                intendencia=map.get("cod_uorga").toString().substring(0,2).concat("0000");
              }
            }
            map.put("intend",intendencia);
            String prefijo="";
            String prefijo1="";
            if ( map.get("cod_sexo") != null 
              && !"".equals((String)map.get("cod_uorga"))) {
              if ("M".equals((String)map.get("cod_sexo")) || "1".equals((String)map.get("cod_sexo"))) {
                prefijo="El Sr.";
                prefijo1="el Sr.";
              } else if ("F".equals((String)map.get("cod_sexo")) || "2".equals((String)map.get("cod_sexo"))) {
                prefijo="La Srta.";
                prefijo1="la Srta.";
              }
            }
            map.put("prefijo",prefijo);
            map.put("prefijo1",prefijo1);
          } catch (Exception e) {
            log.error("*** SQL Error ****", e);
            throw new SQLException(e.getMessage());
          }     
          return map;
        }
      };
      setIsolationLevel(T3339DAO.TX_READ_UNCOMMITTED);
      if (params.getString("rbtfechaIniFin")!=null && !"".equals(params.getString("rbtfechaIniFin"))) {
        Object objects[]={params.getDate("txtdel"),params.getDate("txtal")};
        lista = (ArrayList) executeQuery(datasource, sSQL.toString(),objects,bmConstCert);
      } else {
        lista = (ArrayList) executeQuery(datasource, sSQL.toString(),bmConstCert);
      }    
      setIsolationLevel(-1);
    }catch (Exception e) {
      log.error("*** SQL Error ****",e);
      MensajeBean msg = new MensajeBean();
      msg.setMensajeerror("Ha ocurrido un error inesperado al consultar la data para el formato: ".concat( e.getMessage() ));
      msg.setMensajesol("Por favor, intente nuevamente realizar la operacion, de continuar con el problema comuniquese con el webmaster");
      throw new DAOException (this, msg);     
    } finally {       
    }    
    return lista; 
  }
  
  /**
  * Retorna los datos que se usaran para generar la hoja informativa.
  * @param params DynaBean, contiene los criterios de bsqueda.  
  * @return List, contiene la data para generar el formato 
  * @throws DAOException 
  */  
  public List generaHoja(DynaBean params) throws DAOException {  
  	List lista = null;
    StringBuffer sWhere = new StringBuffer(" AND C.COD_EST_CONV!='E' ");
    try {
    	if ("1".equals(params.getString("cmbcod_uniNeg"))) {
        sWhere.append(" AND  (C.cod_uorga like '1%' ")
        .append(" OR C.cod_uorga like '2%') ");
      } else if ("2".equals(params.getString("cmbcod_uniNeg"))) {
        sWhere .append(" AND  (C.cod_uorga not like '1%' ")
        .append(" AND C.cod_uorga not like '2%') ");
      }
      if (params.getString("cmbcod_uorga") != null && !"".equals(params.getString("cmbcod_uorga"))) {
        sWhere.append(" AND C.COD_UORGA = '")
        .append(params.getString("cmbcod_uorga").replaceAll("'", "''")).append("'");
      }
      if (params.getString("cmbcod_sede") != null && !"0".equals(params.getString("cmbcod_sede"))) {    
        if ("1".equals(params.getString("cmbcod_sede"))){ 
          sWhere.append(" AND  T.COD_DPTO ='15' "); 
        } else if("2".equals(params.getString("cmbcod_sede"))){ 
          sWhere.append(" AND  T.COD_DPTO !='15' "); 
        } 
      }
      if (params.getString("rbtfechaIniFin")!=null && !"".equals(params.getString("rbtfechaIniFin"))) {
        if (params.getDate("txtdel")!=null && params.getDate("txtal")!=null) {
          if ("I".equals(params.getString("rbtfechaIniFin"))) {
            if (params.getDate("txtdel") != null) {
              sWhere.append(" AND C.fec_ini_conv BETWEEN ? AND ? ");
            }
          } else if ("F".equals(params.getString("rbtfechaIniFin"))) {
            if (params.getDate("txtal") != null) {
              sWhere.append(" AND C.fec_fin_conv BETWEEN ? AND ? ");
            }
          }            
        }
      }       
      if (params.getString("cmbcod_modalidad") != null && !"-1".equals(params.getString("cmbcod_modalidad"))) { 
        sWhere.append(" AND C.COD_MODALIDAD = '")
        .append(params.getString("cmbcod_modalidad").replaceAll("'", "''")).append("'"); 
      }        
      if (params.getString("txtnum_reg") != null && !"".equals(params.getString("txtnum_reg"))) { 
        sWhere.append(" AND P.COD_REGISTRO = '")
        .append(params.getString("txtnum_reg").replaceAll("'", "''")).append("'"); 
      }        
      if (params.getString("txtdni") != null && !"".equals(params.getString("txtdni"))) { 
        sWhere.append(" AND P.NUM_DOC = '")
        .append(params.getString("txtdni").replaceAll("'", "''")).append("'"); 
      }
      if (params.getString("txtape_paterno") != null && !"".equals(params.getString("txtape_paterno"))) { 
        sWhere.append(" AND P.NOM_APE_PAT LIKE '")
        .append(params.getString("txtape_paterno").replaceAll("'", "''")).append("%'"); 
      }
      if (params.getString("txtape_materno") != null && !"".equals(params.getString("txtape_materno"))) { 
        sWhere.append(" AND P.NOM_APE_MAT LIKE '")
        .append(params.getString("txtape_materno").replaceAll("'", "''")).append("%'"); 
      }
      if (params.getString("txtnombres") != null && !"".equals(params.getString("txtnombres"))) { 
        sWhere.append(" AND P.NOM_PER LIKE '")
        .append(params.getString("txtnombres").replaceAll("'", "''")).append("%'"); 
      }
      StringBuffer sSQL = new StringBuffer(QUERY600_SENTENCE.toString())
      .append(sWhere).append(" ORDER BY P.COD_PER_MODFOR,C.NUM_CONVENIO ");           
      if (log.isDebugEnabled()) {
        log.debug("#########################################");
        log.debug("QUERY GENERAR FORMATOS : HOJA INFORMATIVA");
        log.debug("#########################################");
        log.debug(sSQL);
      }      
      final BeanMapper bmHojInf = new BeanMapper(){
        public Object setear(ResultSet rs, Map map) throws SQLException {
          try {            
          	if (log.isDebugEnabled()){
          	  log.debug("el codigoPersona es " + map.get("cod_per_modfor").toString());
          	}
            String tipoDoc="5";
            if ( (map.get("cod_tipdoc") != null && !"".equals(((String) map.get("cod_tipdoc")).trim()))){
              if ( "01".equals(((String)map.get("cod_tipdoc")).trim())) {
                tipoDoc="1";
              } else if ("11".equals(((String)map.get("cod_tipdoc")).trim())) {
                tipoDoc="4";
              } 
            } 
            map.put("tipodoc", tipoDoc);            
            map.put("ind_madre_resp", (map.get("ind_madre_resp") != null)?(String)map.get("ind_madre_resp"):"");
            map.put("ind_discapacidad", (map.get("ind_discapacidad") != null)?(String)map.get("ind_discapacidad"):"");
            map.put("cod_sexo", (map.get("cod_sexo") != null)?(String)map.get("cod_sexo"):"");
            int edad=0;
            if ( map.get("fec_nacimiento") != null) {
            	FechaBean fec_nacimiento = new FechaBean((Date)map.get("fec_nacimiento"));
            	int mesFecIn = Integer.valueOf(fec_nacimiento.getMes()).intValue();
            	int diaFecIn = Integer.valueOf(fec_nacimiento.getDia()).intValue();
            	edad =(Integer.valueOf(new FechaBean().getAnho()).intValue()-Integer.valueOf(fec_nacimiento.getAnho()).intValue());
            	if (log.isDebugEnabled()){log.debug("edad"+edad);}
            	FechaBean hoy = new FechaBean();
            	int fecIni = (Integer.valueOf(hoy.getAnho()).intValue()*10000)+(mesFecIn*100)+diaFecIn;
            	int fecHoy=Integer.valueOf(hoy.getAnho().concat(hoy.getMes()).concat(hoy.getDia())).intValue();
            	if (log.isDebugEnabled()){
            	  log.debug("edadfecIni"+fecIni);
            	  log.debug("edadfecHoy"+fecHoy);
            	}
            	if(fecHoy<fecIni){
              	edad--;
              }            	
            }
            map.put("edad", ""+edad);            
            map.put("ind_hor_noct", (map.get("ind_hor_noct") != null)?(String)map.get("ind_hor_noct"):"");
            map.put("ind_seguro", (map.get("ind_seguro") != null)?(String)map.get("ind_seguro"):"");
            map.put("nomseguro", (map.get("cod_cia_seguro") != null)?(String)map.get("cod_cia_seguro"):"");
            if (log.isDebugEnabled()){log.debug("el mapaEspecialidad1 es-" + map.get("cod_especialidad") + "-");}
            map.put("cod_especialidad", (map.get("cod_especialidad") != null)?(String)map.get("cod_especialidad"):"");
            if (log.isDebugEnabled()){log.debug("el mapaEspecialidad2 es-" + map.get("cod_especialidad") + "-");}
            if (log.isDebugEnabled()){log.debug("el mapacod_ocup1 es-" + map.get("cod_ocup") + "-");}
            map.put("cod_ocup", (map.get("cod_ocup") != null)?(String)map.get("cod_ocup"):"");
            if (log.isDebugEnabled()){log.debug("el mapacod_ocup2 es-" + map.get("cod_ocup") + "-");}
            map.put("mto_subv", (map.get("mto_subv") != null)?map.get("mto_subv"):"");
            if (log.isDebugEnabled()){log.debug("salioHoja");}
          } catch (Exception e) {
            log.error("*** SQL Error ****", e);
            throw new SQLException(e.getMessage());
          }     
          return map;
        }
      };
      setIsolationLevel(T3339DAO.TX_READ_UNCOMMITTED);      
      if (params.getString("rbtfechaIniFin")!=null && !"".equals(params.getString("rbtfechaIniFin"))) {
        Object objects[]={params.getDate("txtdel"),params.getDate("txtal")};
        lista = (ArrayList) executeQuery(datasource, sSQL.toString(),objects,bmHojInf);
      } else {
        lista = (ArrayList) executeQuery(datasource, sSQL.toString(),bmHojInf);
      }
      setIsolationLevel(-1);
    }catch (Exception e) {
      log.error("*** SQL Error ****",e);
      MensajeBean msg = new MensajeBean();
      msg.setMensajeerror("Ha ocurrido un error inesperado al consultar la data para el formato: ".concat( e.getMessage() ));
      msg.setMensajesol("Por favor, intente nuevamente realizar la operacion, de continuar con el problema comuniquese con el webmaster");
      throw new DAOException (this, msg);     
    } finally {       
    }    
    return lista; 
  }
  
  //icapunay 18/10/2011 - metodos requeridos por clase AsisForFacade(pe.gob.sunat.rrhh.formativa.ejb) para generacion de jar:ejbrrhh-asisforfacade.jar
  /**
	 * Metodo encargado de listar a los practicantes
	 * @throws SQLException
	 */
	public ArrayList getPracticantes(Map datos)
			throws DAOException {
		
		log.debug("Mapa getPracticantes: "+ datos);
		ArrayList lista = null;
		try {
			/*String fechaIni = (String) datos.get("fechaIni");
			String fechaFin = (String) datos.get("fechaFin");*/
			String valor = (String) datos.get("valor");
			String criterio = (String) datos.get("criterio");
			//HashMap seguridad = (HashMap) datos.get("seguridad");
			HashMap seguridad = null;
			
			if (!criterio.equals("0")) {
				QUERY17_SENTENCE.append(" AND C.cod_uorga = '").append(valor).append("' ");
			}
			if (!criterio.equals("1")) {
				QUERY17_SENTENCE.append(" AND P.cod_registro = '").append(valor).append("' ");
			}
			if (!criterio.equals("3")) {
				// para Institucion
			}
			
			/*if (!fechaIni.equals("")) {
				QUERY17_SENTENCE.append(" AND fecha >= DATE('").append(Utiles.toYYYYMMDD(fechaIni)).append("') ");
			}
			if (!fechaFin.equals("")) {
				QUERY17_SENTENCE.append(" AND fecha <= DATE('").append(Utiles.toYYYYMMDD(fechaFin)).append("') ");
			}*/
			
			//	Criterios de Visibilidad
			/*if (seguridad != null) {
				HashMap roles = (HashMap) seguridad.get("roles");
				String uoSeg = (String) seguridad.get("uoSeg");				
			
				if (roles.get(Constantes.ROL_JEFE) != null) {
					QUERY17_SENTENCE.append(" AND P.cod_registro != '" ).append( codPers ).append( "' ");
					QUERY17_SENTENCE.append(" AND substr(trim(nvl(C.cod_uorga,'')),1,6) like '"
							).append( uoSeg ).append( "' ");
				} else {
					QUERY17_SENTENCE.append(" AND 1=2 ");
				}
			}*/
			log.debug("Consulta: " + QUERY16_SENTENCE);
			lista = (ArrayList) executeQuery(datasource, QUERY17_SENTENCE.toString());
		} catch (Exception e) {
	      throw new DAOException(this, "No se pudo cargar la lista de practicantes.");
	    }		
		return lista;
	}
	
	/**
	 * Obtiene las modalidades 
	 * de un trabajador.
	 * @throws DAOException
	 */	
	public List getModalidades() throws DAOException {		
		log.debug("QUERY "+QUERY18_SENTENCE);		
		List lista = executeQuery(datasource, QUERY18_SENTENCE.toString());
		return lista;
	}
	
	/**
	 * Metodo encargado de listar las marcaciones realizadas 
	 * por un practicante obetenidas de
	 * la tabla T1275Marcacion.
	 * @throws SQLException
	 */
	public ArrayList findMarcaciones(Map datos)	throws DAOException {
		log.debug("QUERY "+QUERY19_SENTENCE);	
		ArrayList lista = null;
		try {
			String fechaIni = (String) datos.get("fechaIni");
			String fechaFin = (String) datos.get("fechaFin");			
			
			if (!fechaIni.equals("")) {
				QUERY19_SENTENCE.append(" AND fecha >= DATE('").append(Utiles.toYYYYMMDD(fechaIni)).append("') ");
			}
			if (!fechaFin.equals("")) {
				QUERY19_SENTENCE.append(" AND fecha <= DATE('").append(Utiles.toYYYYMMDD(fechaFin)).append("') ");
			}
			
			//	Criterios de Visibilidad
			/*if (seguridad != null) {

				HashMap roles = (HashMap) seguridad.get("roles");
				String uoSeg = (String) seguridad.get("uoSeg");				
				
				if (roles.get(Constantes.ROL_JEFE) != null) {
					QUERY19_SENTENCE.append(" AND per.t02cod_pers != '" ).append( codPers ).append( "' ");
					QUERY19_SENTENCE.append(" AND substr(trim(nvl(per.t02cod_uorgl,'')||nvl(per.t02cod_uorg,'')),1,6) like '"
							).append( uoSeg ).append( "' ");
				} else {
					QUERY19_SENTENCE.append(" AND 1=2 ");
				}
			}*/
			
			lista = (ArrayList) executeQuery(datasource, QUERY19_SENTENCE.toString(), 
									new Object[]{datos.get("cod_registro")});		
		} catch (Exception e) {
	      throw new DAOException(this, "No se pudo cargar las marcaciones del practicante" + datos.get("codPers"));
	    }
		return lista;
	}
	//fin icapunay 18/10/2011 - metodos requeridos por clase AsisForFacade(pe.gob.sunat.rrhh.formativa.ejb) para generacion de jar:ejbrrhh-asisforfacade.jar
    
}