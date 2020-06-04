package pe.gob.sunat.rrhh.programacion.ejb;
 
import pe.gob.sunat.framework.core.ejb.StatelessAbstract;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pe.gob.sunat.framework.core.ejb.FacadeException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pe.gob.sunat.framework.core.bean.MensajeBean;
import pe.gob.sunat.framework.core.bean.ParamBean;

import javax.ejb.CreateException;
import javax.sql.DataSource;
import pe.gob.sunat.framework.core.pattern.ServiceLocator;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.Propiedades;
import pe.gob.sunat.framework.util.cache.dao.ParamDAO;
import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.framework.util.mail.Correo;
import pe.gob.sunat.framework.util.mail.CorreoException;
import javax.ejb.CreateException;
import pe.gob.sunat.framework.util.dao.SequenceDAO;
import pe.gob.sunat.rrhh.asistencia.dao.T1282DAO;
import pe.gob.sunat.rrhh.dao.CorreoDAO;
import pe.gob.sunat.rrhh.dao.T02DAO;
import pe.gob.sunat.rrhh.dao.T12DAO;
import pe.gob.sunat.rrhh.dao.T99DAO;
import pe.gob.sunat.rrhh.padron.ejb.PadronFacadeHome;
import pe.gob.sunat.rrhh.padron.ejb.PadronFacadeRemote;
import pe.gob.sunat.rrhh.asistencia.dao.T1281DAO;
import pe.gob.sunat.sp.asistencia.ejb.HoraExtraFacadeHome;
import pe.gob.sunat.sp.asistencia.ejb.HoraExtraFacadeRemote;
import pe.gob.sunat.sp.asistencia.ejb.VacacionFacadeHome;
import pe.gob.sunat.sp.asistencia.ejb.VacacionFacadeRemote;
import pe.gob.sunat.sp.asistencia.ejb.ReporteFacadeHome;//ICAPUNAY-MEMO 32-4F3100-2013
import pe.gob.sunat.sp.asistencia.ejb.ReporteFacadeRemote;//ICAPUNAY-MEMO 32-4F3100-2013
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.rrhh.asistencia.dao.T3886DAO;



/**
 * <p> Title : ProgramacionFacade</p>
 * <p>Description : EJB encargado de realizar las tareas referentes a la 
 * creaci�n y eliminaci�n de la Programacion de Vacaciones </p>
 * <p>Copyright   : Copyright (c) 2008</p>
 * <p>Company     : COMSA </p>
 * @author FRANK PICOY (COMSA)  
 * @version 1.0 
 * 
 * @ejb.bean name="ProgramacionFacadeEJB" 
 *        description="ProgramacionFacade"
 *           jndi-name="ejb/facade/rrhh/programacion/AsisProgramacionFacadeEJB"
 *           type="Stateless" 
 *           view-type="remote"
 * 
 * @ejb.home remote-class="pe.gob.sunat.rrhh.programacion.ejb.ProgramacionFacadeHome"
 *           extends="javax.ejb.EJBHome"
 * 
 * @ejb.interface remote-class="pe.gob.sunat.rrhh.programacion.ejb.ProgramacionFacadeRemote"
 *                extends="javax.ejb.EJBObject"
 * 
 * @ejb.transaction-type type="Container"
 * 
 * @ejb.resource-ref description="DS a la base Personas" res-ref-name="jdbc/dgsp" 
 *            res-type="java.sql.DataSource" res-auth="Container"
 * 
 * @weblogic.resource-description jndi-name="jdbc/dgsp" res-ref-name="jdbc/dgsp"
 * 
 * @ejb.resource-ref description="DS a la base Personas" res-ref-name="jdbc/dcsp" 
 *                   res-type="java.sql.DataSource" res-auth="Container"
 * 
 * @weblogic.resource-description jndi-name="jdbc/dcsp" res-ref-name="jdbc/dcsp"
 * @ejb.env-entry name="dcsp" type="java.lang.String" value="jdbc/dcsp"
 * 
 * @ejb.resource-ref description="DS a la base Recauda" res-ref-name="jdbc/dcrecauda"
 *                   res-type="java.sql.DataSource" res-auth="Container"
 * 
 * @weblogic.resource-description jndi-name="jdbc/dcrecauda" res-ref-name="jdbc/dcrecauda"
 *
 * @ejb.resource-ref description="DS a la base de Secuencias" res-ref-name="jdbc/dcbdseq"
 *                   res-type="java.sql.DataSource" res-auth="Container"
 * 
 * @weblogic.resource-description jndi-name="jdbc/dcbdseq" res-ref-name="jdbc/dcbdseq"

 * @weblogic.enable-call-by-reference True
 * @weblogic.clustering home-is-clusterable="True" stateless-bean-is-clusterable="True"
 * @weblogic.pool max-beans-in-free-pool="10" initial-beans-in-free-pool="5"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 */

public class ProgramacionFacade extends StatelessAbstract{
  private final Log log = LogFactory.getLog(getClass());
  private static final long serialVersionUID = 1L;
  private static final String CODIGO_REGISTRO_LOGICO = "0";
  private static final String CODIGO_REGISTRO_DEBD = "1";
  private static final String CODIGO_REGISTRO_DEBD_ELIMINADO = "2";
  private static final String CODIGO_REGISTRO_LOGICO_ELIMINADO = "3";
  private static final String SALDO_PERIODO_FUTURO = "30";
  
  ServiceLocator sl = ServiceLocator.getInstance();//ICAPUNAY-MEMO 32-4F3100-2013
  
  Propiedades constantes = new Propiedades(getClass(), "/constantes.properties");
  
  
  
  /**
   * M�todo encargado de obtener los datos personales y de programacion de vacaciones 
     * del codigo de registro ingresado
   * @param datos Map
   * @return Map 
   * @throws FacadeException
   * @ejb.interface-method view-type="remote"
   * @ejb.transaction type="NotSupported"
   */
  public Map obtenerUsuProgramacion(Map datos) throws FacadeException {
  	Map solicitante=null;
  	Map saldoxpers=new HashMap();
  	//Agregado FRD 18/06/09
  	HashMap roles = new HashMap();
  	//
    try {
    	DataSource dbpool_sp = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dcsp");
    	String dbpool = ServiceLocator.getInstance().getString("java:comp/env/dcsp");
    	T02DAO t02DAO = new T02DAO(dbpool_sp);
    	//Agregado FRD 18/06/09
    	roles = (HashMap)datos.get("roles");
    	//
    	//solicitante = t02DAO.joinWithT12T01DetaByCodPersyUUOO(datos.get("txtregistro").toString(),uuoo);
    	//EBV 17/02/2009 Debe contemplar la visibilidad
    	//solicitante = t02DAO.joinWithT12T01Deta(datos.get("txtregistro").toString());
    	T12DAO t12DAO = new T12DAO(dbpool_sp);
		String uuoousuario = datos.get("uuoo_usuario").toString().trim();
				
		Map uuooJefe = t12DAO.findByCodUorga(uuoousuario);
		String jefat=uuooJefe.get("t12cod_jefat")!=null?uuooJefe.get("t12cod_jefat").toString().trim():"";
		//jefat=uuooJefe.get("t12cod_encar")!=null?uuooJefe.get("t12cod_encar").toString().trim():jefat; //Julio Villacorta-10/11/2011-PASE PAS20112A550001284
		jefat=((uuooJefe.get("t12cod_encar")!=null) && !(uuooJefe.get("t12cod_encar").toString().trim().length()== 0))?uuooJefe.get("t12cod_encar").toString().trim():jefat; //Julio Villacorta-10/11/2011-PASE PAS20112A550001284
		if  (datos.get("usuario").toString().trim().equals(jefat.trim())) {
			solicitante = t02DAO.joinWithT12T01Deta1(datos);
	    	if(log.isDebugEnabled()) log.debug("solicitante "+solicitante);
		}
    	//EBV 26/02/2009 Si es jefe /encargado de dos unidades
		if(log.isDebugEnabled()) log.debug("perfil "+datos.get("perfil_usuario").toString());
    	if (( "usuario_AnalisOperativo".equals(datos.get("perfil_usuario").toString()))  || 
    		( "usuario_AnalisCentral".equals(datos.get("perfil_usuario").toString())) ||
    		(roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) ||
    		(roles.get(Constantes.ROL_ANALISTA_CENTRAL)!=null)	)
    	{		
			solicitante = t02DAO.joinWithT12T01Deta1(datos);
	    	if(log.isDebugEnabled()) log.debug("solicitante Analista"+solicitante);
    	}

    	//EBV 26/02/2009 Si es jefe /encargado de dos unidades
    	if ( ((solicitante== null) && ( "usuario_Jefe".equals(datos.get("perfil_usuario").toString()))) ||
   			 ((solicitante== null) && ( roles.get(Constantes.ROL_JEFE)!=null))
    	
    	   ) 
    	{
    		
    		// ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp/Prog Vacaciones
    		
    		
    		solicitante = t02DAO.joinWithT12T01Deta1(datos);
	    	if(log.isDebugEnabled()) log.debug("solicitante(perfil usuario_Jefe): "+solicitante);
	    	
    		/*Map Jefe = t02DAO.findByCodPers(datos.get("usuario").toString().trim());
    		String UUOOl = Jefe.get("t02cod_uorgl")!=null?Jefe.get("t02cod_uorgl").toString().trim():"";

    		if(log.isDebugEnabled()) log.debug("datos "+datos);
    		if (uuoousuario.equals(UUOOl)){
    			
    			uuooJefe = t12DAO.findByCodUorga(Jefe.get("t02cod_uorg").toString().trim());
    			jefat=uuooJefe.get("t12cod_jefat")!=null?uuooJefe.get("t12cod_jefat").toString().trim():"";
    			jefat=uuooJefe.get("t12cod_encar")!=null?uuooJefe.get("t12cod_encar").toString().trim():jefat;
    			//String encar=uuooJefe.get("t12cod_encar")!=null?uuooJefe.get("t12cod_encar").toString().trim():"";
    			if  (datos.get("usuario").toString().trim().equals(jefat.trim()))  {
    				if(log.isDebugEnabled()) log.debug("Es Jefe de Otra Unidad ");
    				Map datosTemp = datos;
    				int nivel = Integer.parseInt(uuooJefe.get("t12cod_nivel").toString());
        			String uuooF = nivel>0 ?Jefe.get("t02cod_uorg").toString().trim().substring(0, nivel + 1).concat("%"): "%" ;
        			datosTemp.put("uoSeg",uuooF);
        			if(log.isDebugEnabled()) log.debug("Es Jefe de Otra Unidad "+datosTemp);
        			solicitante = t02DAO.joinWithT12T01Deta1(datosTemp);
        	    	if(log.isDebugEnabled()) log.debug("solicitante UUOO  "+solicitante);
    			}
    			
    		}*/    
    		//FIN  ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp/Prog Vacaciones
    	}
    	
    	if (solicitante!=null )
    	{
    		/*EBV 17/02/2009 Debe contemplar la visibilidad
    		 * && "usuario_Jefe".equals(datos.get("perfil_usuario").toString())){
    		if (datos.get("uuoo_usuario").toString().equals(solicitante.get("t02cod_uorg").toString().trim())){
    			solicitante.put("usuarioxUUOOValido",constantes.leePropiedad("ACTIVO"));
    		} else {
    			solicitante.put("usuarioxUUOOValido",constantes.leePropiedad("INACTIVO"));
    		}
    		*/
    		solicitante.put("usuarioxUUOOValido",constantes.leePropiedad("ACTIVO"));	
    	}
    	else
    	{
    		solicitante = new HashMap();
    		solicitante.put("usuarioxUUOOValido",constantes.leePropiedad("INACTIVO"));
    	}
    	if (solicitante!=null && 
    		(solicitante.get("usuarioxUUOOValido")==null || constantes.leePropiedad("ACTIVO").equals(solicitante.get("usuarioxUUOOValido").toString()) ) &&
    		constantes.leePropiedad("ACTIVO").equals(solicitante.get("t02cod_stat").toString())){
    	VacacionFacadeHome facadeHome = (VacacionFacadeHome) ServiceLocator.
      getInstance().getRemoteHome(VacacionFacadeHome.JNDI_NAME,
                               VacacionFacadeHome.class);
    	VacacionFacadeRemote vacacion = facadeHome.create();
    	
    	String fecha_ingreso = "";
    	
    	Map hmVacGen = vacacion.buscarVacacionesGen(dbpool,datos.get("txtregistro").toString());
    	
    	if (hmVacGen!=null && hmVacGen.get("fecha")!=null && !hmVacGen.get("fecha").toString().equals("")){
    		fecha_ingreso=hmVacGen.get("fecha").toString();
    	} else {
    		fecha_ingreso=solicitante.get("t02f_ingsun_desc").toString();
    	}
    	solicitante.put("fecha_ingreso",fecha_ingreso);
    	
  		FechaBean fechabeanIngreso = new FechaBean(fecha_ingreso);
    	FechaBean fechabeanHoy = new FechaBean();
    	String fecha_gen= fechabeanIngreso.getDia().concat("/").concat(fechabeanIngreso.getMes()).concat("/").concat(fechabeanHoy.getAnho());
    	FechaBean fechabeanGen = new FechaBean(fecha_gen);
  		int periodo_Act=0;
    	if (fechabeanHoy.getCalendar().before(fechabeanGen.getCalendar())){
  			periodo_Act = Integer.parseInt(fechabeanHoy.getAnho())-1;
  		} else {
  			periodo_Act = Integer.parseInt(fechabeanHoy.getAnho());
  		}
    	String fecha_ini_vac= fechabeanIngreso.getDia().concat("/").concat(fechabeanIngreso.getMes()).concat("/").concat(String.valueOf(periodo_Act));
    	String fecha_gen_sig= fechabeanIngreso.getDia().concat("/").concat(fechabeanIngreso.getMes()).concat("/").concat(String.valueOf(periodo_Act+1));
    	String fecha_gen_sigper = fechabeanIngreso.getDia().concat("/").concat(fechabeanIngreso.getMes()).concat("/").concat(String.valueOf(periodo_Act+2));
    	
    	solicitante.put("periodo_Act_vac",String.valueOf(periodo_Act));
    	solicitante.put("periodo_Sig_vac",String.valueOf(periodo_Act+1));
    	solicitante.put("fecha_gen_sig",fecha_gen_sig);
    	solicitante.put("fecha_gen_sig_val",fecha_gen_sig);
    	solicitante.put("fecha_gen_sigper",fecha_gen_sigper);
    	solicitante.put("cod_periodo",String.valueOf(periodo_Act));
    	solicitante.put("fecha_inicio_vac",fecha_ini_vac);
    	solicitante.put("fecha_ini_vac",fecha_ini_vac);
    	solicitante.put("fecha_ini_vac_sig",fecha_gen_sig);
    	
    	T1281DAO t1281dao = new T1281DAO(dbpool_sp);
    	String saldoAct="0";
    	String saldoSig="0";
    	
    	saldoxpers.put("codPers",solicitante.get("t02cod_pers").toString());
    	saldoxpers.put("saldoFavor","true");
    	saldoxpers.put("anho",String.valueOf(periodo_Act));
    	List listaSaldos = t1281dao.findByCodPersSaldoAnno(saldoxpers);
    	if (listaSaldos.size()>0){
    		saldoAct=((HashMap)listaSaldos.get(0)).get("saldo")!=null?((HashMap)listaSaldos.get(0)).get("saldo").toString():"";
    	} 
    	solicitante.put("saldo",saldoAct);
    	solicitante.put("saldoAct",saldoAct);
    	saldoSig=SALDO_PERIODO_FUTURO;
    	solicitante.put("saldoSig",saldoSig);
    	
    	//ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta vacaciones (SC)
    	Map prms = new HashMap();
    	Map cabVacacion = null;
    	prms.put("cod_pers", datos.get("txtregistro").toString());
    	prms.put("anno", String.valueOf(periodo_Act+1));
    	if (solicitante.get("t02cod_rel")!=null){
    		if (solicitante.get("t02cod_rel").toString().trim().equals(Constantes.CODREL_REG1057)){ //solo para CAS(09) variable saldoSig no es 30
    			cabVacacion = t1281dao.findAllColumnsByKey(prms);
    			if (cabVacacion!=null){
    				solicitante.put("saldoSig",String.valueOf(30+((Integer)cabVacacion.get("saldo")).intValue()));    				
    			}
    		}
    	}
    	//ICAPUNAY 23072015 - PAS20155E230300073
    	
    	T1282DAO t1282dao = new T1282DAO(dbpool_sp);
    	datos.put("cod_pers",solicitante.get("t02cod_pers").toString());
    	datos.put("anno_vac",solicitante.get("periodo_Act_vac").toString());
    	datos.put("licencia",constantes.leePropiedad("VACACION_PROGRAMADA"));
    	List listaProgramadasAct = t1282dao.obtenerProgramadas(datos);
    	datos.put("anno_vac",solicitante.get("periodo_Sig_vac").toString());
    	List listaProgramadasSig = t1282dao.obtenerProgramadas(datos);
    	if (listaProgramadasSig.size()>0){
        	for (int i=0;i<listaProgramadasSig.size();i++) {
        		listaProgramadasAct.add((HashMap)listaProgramadasSig.get(i));
    	    }    	  	
    	}
    	solicitante.put("listaProgramadasAct",listaProgramadasAct);
    	//Comentar de Prueba para trabajar con todos los periodos en general
    	solicitante.put("listaProgramadasSig",listaProgramadasSig);
    	if (solicitante.get("t02cod_uorgl_desc")!=null)
    	{
    		solicitante.put("t02cod_uorg",solicitante.get("t02cod_uorgl").toString().trim());
    		solicitante.put("t02cod_uorg_desc",solicitante.get("t02cod_uorgl_desc").toString().trim());
    	}
    	}
    	 
    	
    } catch (DAOException e) {
      log.error(e, e);
     	MensajeBean beanM = new MensajeBean();
      beanM.setMensajeerror(e.getMessage());
      beanM.setMensajesol("Por favor intente nuevamente, posible error en la BD");
      throw new FacadeException(this, beanM);
    } catch (Exception e) {
      log.error(e, e);
     	MensajeBean beanM = new MensajeBean();
      beanM.setMensajeerror(e.getMessage());
      beanM.setMensajesol("Por favor intente nuevamente.");
      throw new FacadeException(this, beanM);
    }
    return solicitante;
  }
  
  
  
  /**
   * Metodo encargado de grabar las programaciones de vacacion ingresadas (llama a otro que hace por periodo).
   * @param datos Map
   * @return void 
   * @throws FacadeException
   * @ejb.interface-method view-type="remote"
   * @ejb.transaction type="RequiresNew"
   */
  public String grabarProgVacaciones(Map datos) throws FacadeException {
	 Map mapaProgPorEvaluar = null;
	 Map mapaProgTMP = null;
	 String periodoEvaluar = "";
	 List listaProgramacion = new ArrayList();
	 List listaTMP = new ArrayList();
	 String rpta1 = "";
	 String rpta2 = "";
	 int i=0;
	 int contConv =0;
 	 String chkconvenio = "";	 
 	 String FlagA ="";
 	 String FlagS ="";
 	 String PerNoEvaluado = "";
 	 log.debug("tttprueba:"+datos.toString());
  try {
	  FlagA = datos.get("FlagPerAct").toString();
	  FlagS = datos.get("FlagPerSig").toString();
	  	  
	  if ("".equals(FlagA) && "".equals(FlagS)){
		PerNoEvaluado = "";
	  }else{
        if ("".equals(FlagA) && !("".equals(FlagS))) {
          PerNoEvaluado = FlagS;	
        }else{
          if (!("".equals(FlagA)) && "".equals(FlagS)) {
            PerNoEvaluado = FlagA; 	
          }
	    }
	  }
	  	  
      if (!("".equals(PerNoEvaluado))){
    	listaTMP = (ArrayList)datos.get("listaProgramacion");
    	for (i=0;i<listaTMP.size();i++) {
    	  mapaProgTMP = (HashMap)listaTMP.get(i);
    	  if (PerNoEvaluado.equals(mapaProgTMP.get("anno_vac"))){
    		  if (CODIGO_REGISTRO_DEBD_ELIMINADO.equals(mapaProgTMP.get("bd"))){
    			mapaProgTMP.remove("bd");
    			mapaProgTMP.put("bd",CODIGO_REGISTRO_DEBD);
     			mapaProgTMP.remove("visible");
        		mapaProgTMP.put("visible",constantes.leePropiedad("ACTIVO"));
    		  }
    		  if (CODIGO_REGISTRO_LOGICO.equals(mapaProgTMP.get("bd"))){
      			mapaProgTMP.remove("bd");
      			mapaProgTMP.put("bd",CODIGO_REGISTRO_LOGICO_ELIMINADO);
       			mapaProgTMP.remove("visible");
          		mapaProgTMP.put("visible",constantes.leePropiedad("INACTIVO"));
      		  }
    		  
    	  }
    	}
   	  }else{
   		listaTMP = (ArrayList)datos.get("listaProgramacion");
   	  }
	  
	  //Evaluara todos los periodos para ver que no se graben Dos convenios 
	  //por periodos seguidos
	  for (i=0;i<listaTMP.size();i++) {
		mapaProgTMP = (HashMap)listaTMP.get(i);
    	if (constantes.leePropiedad("ACTIVO").equals(mapaProgTMP.get("visible"))){
	      chkconvenio = mapaProgTMP.get("ind_conv").toString();
	      if ("1".equals(chkconvenio)){
	    	 contConv = contConv + 1;  	
	      }
	    }
	  }
	  if (contConv > 1){
		  return "No se pueden grabar dos Convenios para dos Periodos consecutivos o en el mismo Periodo";
	  }

	  List listaProgXPeriodo = (ArrayList)datos.get("listaProgramacion");
	  periodoEvaluar = datos.get("periodoA").toString();
	  datos.put("cod_periodoPorEvaluar", periodoEvaluar);
	  datos.remove("fechaInicioSigPer");
	  datos.put("fechaInicioSigPer", datos.get("fechaInicioSigPerA"));
	  datos.remove("fechaInicioPer");
	  datos.put("fechaInicioPer", datos.get("fechaInicioPerA"));
	  
	  //Evaluara Periodo Actual
	  for (i=0;i<listaProgXPeriodo.size();i++) {
  		mapaProgPorEvaluar = (HashMap)listaProgXPeriodo.get(i);
  		if (periodoEvaluar.equals(mapaProgPorEvaluar.get("anno_vac").toString())){
  			listaProgramacion.add(mapaProgPorEvaluar);     	
  		}
  	  }
	  if (listaProgramacion.size()>0 && !(periodoEvaluar.equals(PerNoEvaluado)) ){
         datos.put("listaProgramacionTMP",listaProgramacion);
         log.debug("Temporal:"+listaProgramacion);
         rpta1 = grabarProgVacacionesPorPeriodo(datos);
         listaProgramacion.clear();
	  }else{
		  rpta1 = "OK";		  
	  }
	  
	  if ("OK".equals(rpta1)){
	     periodoEvaluar = datos.get("periodoS").toString();
	     datos.remove("cod_periodoPorEvaluar");
	     datos.put("cod_periodoPorEvaluar", periodoEvaluar);
	     datos.remove("fechaInicioSigPer");
	     datos.put("fechaInicioSigPer", datos.get("fechaInicioSigPerS"));
	     datos.remove("fechaInicioPer");
	     datos.put("fechaInicioPer", datos.get("fechaInicioPerS"));
	     //Evaluara Periodo Siguiente
	     for (i=0;i<listaProgXPeriodo.size();i++) {
	       mapaProgPorEvaluar = (HashMap)listaProgXPeriodo.get(i);
	       if (periodoEvaluar.equals(mapaProgPorEvaluar.get("anno_vac").toString())){
	    	 listaProgramacion.add(mapaProgPorEvaluar);     	
	       }
	     }
	     if (listaProgramacion.size()>0 && !(periodoEvaluar.equals(PerNoEvaluado)) ){
	    	datos.remove("listaProgramacionTMP");
	    	datos.put("listaProgramacionTMP",listaProgramacion);
	    	log.debug("Temporal2:"+listaProgramacion);
	        rpta2 = grabarProgVacacionesPorPeriodo(datos);
	        listaProgramacion.clear();
	     }else{
            rpta2="OK";
	     }
	  }
	  if("OK".equals(rpta1) && "OK".equals(rpta2)){
		  return "OK";	  
	  }else{
		  if ("OK".equals(rpta1)) rpta1 = "";
		  if ("OK".equals(rpta2)) rpta2 = "";
		  return rpta1 + " " + rpta2;
		  
	  }
 	
  } catch (DAOException e) {
    log.error(e, e);
   	MensajeBean beanM = new MensajeBean();
    beanM.setMensajeerror(e.getMessage());
    beanM.setMensajesol("Por favor intente nuevamente, posible error en la BD");
    throw new FacadeException(this, beanM);
  } catch (Exception e) {
    log.error(e, e);
   	MensajeBean beanM = new MensajeBean();
    beanM.setMensajeerror(e.getMessage());
    beanM.setMensajesol("Por favor intente nuevamente.");
    throw new FacadeException(this, beanM);
  }
  
}  
  
  /**
   * Mtodo encargado de grabar las programaciones de vacacion ingresadas.
   * @param datos Map
   * @return String 
   * @throws FacadeException
   * @throws CreateException
   * @ejb.transaction type="Required"
   */
  
  public String grabarProgVacacionesPorPeriodo(Map datos) throws FacadeException,CreateException {
    try {
    	DataSource dbpool_sp = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dgsp");
    	T1282DAO t1282DAO = new T1282DAO(dbpool_sp);
    	T3886DAO t3886DAO = new T3886DAO(dbpool_sp);
    	List listaProgramacion = (ArrayList)datos.get("listaProgramacionTMP");
    	Map mapaProg=null;
    	Map mapaAnt=null;
    	FechaBean fechaHoy = new FechaBean();
    	String periodo = fechaHoy.getAnho().concat(fechaHoy.getMes());
    	String periodoInicial = null;
    	boolean existenNuevasProg = false;
    	Map columns =null;
    	Map convenio = new HashMap();
    	Map MapresCon = null;
    	String chkconvenio = "";
    	String tieneConvenio = "";
    	int Secuencia = 0;
    	    	
    	//PREGUNTAR SI TIENE CONVENIO t3886
    	convenio.put("cod_pers", datos.get("registro"));
    	convenio.put("per_convenio", datos.get("cod_periodoPorEvaluar").toString());
		
		MapresCon = t3886DAO.findConvenioVigPeriodo(convenio);
    	if (MapresCon==null){
    		tieneConvenio="NO";	
    	}else{
    		tieneConvenio="SI";
    	}
    	
    	//Flag en false de Convenio modificado.
    	boolean bconvmod = false;
    	boolean grabeConv =false;
   	    ServiceLocator sl= ServiceLocator.getInstance();
   	    //
    	for (int i=0;i<listaProgramacion.size();i++) {
    		mapaProg = (HashMap)listaProgramacion.get(i);
	    	if (CODIGO_REGISTRO_DEBD_ELIMINADO.equals(mapaProg.get("bd")) && 
	    		constantes.leePropiedad("INACTIVO").equals(mapaProg.get("visible"))){
	    		chkconvenio = mapaProg.get("ind_conv").toString();
	    		if ("1".equals(chkconvenio)){
	    		  bconvmod = true;  	
	    		}
	    	}
	    	
	    }
    	
   	if ( "SI".equals(tieneConvenio) && bconvmod==true){
   	  return "No se Puede Eliminar un Convenio Registrado Anteriormente";
   	}
   	mapaProg=null;

    	for (int i=0;i<listaProgramacion.size();i++) {
    		mapaProg = (HashMap)listaProgramacion.get(i);
	    	mapaProg.put("cod_pers",datos.get("registro"));
	    	mapaProg.put("licencia",constantes.leePropiedad("VACACION_PROGRAMADA"));
	    	mapaProg.put("anno_vac",datos.get("cod_periodoPorEvaluar").toString());
	    	mapaProg.put("u_organ",datos.get("uorga").toString());
	    	mapaProg.put("cuser_crea",datos.get("cuser_crea").toString());
	    	periodoInicial=(String)mapaProg.get("periodo");
	    	mapaProg.put("periodo",periodo);
	    	mapaProg.put("fcreacion",new FechaBean().getTimestamp());
	    	//String sustento = mapaProg.get("sustento")!=null?mapaProg.get("sustento").toString():"";
	    	//mapaProg.put("sustento", sustento);
	    	log.debug("prueba2222:"+mapaProg.toString());
	    	
	    	if (CODIGO_REGISTRO_LOGICO.equals(mapaProg.get("bd")) && 
	    		constantes.leePropiedad("ACTIVO").equals(mapaProg.get("visible"))){
	    		mapaProg.put("est_id",constantes.leePropiedad("ACTIVO"));
	    		mapaProg.put("ffinicio",new FechaBean((String) mapaProg.get("ffinicio_desc")).getTimestamp());
		    	mapaProg.put("ffin",new FechaBean((String) mapaProg.get("ffin_desc")).getTimestamp());
		    	mapaAnt = t1282DAO.findAllColumnsByKey(mapaProg);
		    	if (mapaAnt!=null){
		    	  columns =new HashMap();
		    	  columns.put("dias",mapaProg.get("dias").toString());
		    	  columns.put("ffin",new FechaBean((String) mapaProg.get("ffin_desc")).getTimestamp());
		    	  columns.put("est_id",constantes.leePropiedad("ACTIVO"));
		    	  mapaProg.put("columns",columns);
		    	  t1282DAO.updateCustomColumns(mapaProg);
		    	} else {
	    		  t1282DAO.insertarVacacionesDetalle(mapaProg); 
	    		  //INSERTAR EN LA T3886
	    		  //Si grabe convenio no lo vuelvo a grabar.
	    		  if ( (!grabeConv) && ("1".equals(mapaProg.get("ind_conv"))) &&  ( "NO".equals(tieneConvenio) ) ){
		    		  convenio.put("ind_estado", "1"); 		  
		    		  convenio.put("ann_convenio",fechaHoy.getAnho());//
		    		  DataSource dsSecuence= sl.getDataSource("java:/comp/env/jdbc/dcbdseq");
	                  Secuencia = new SequenceDAO().getSequence(dsSecuence,"set3886");
		    		  convenio.put("num_convenio", String.valueOf(Secuencia));
		    		  
		    		  convenio.put("fec_finconvenio", 
		    		  new FechaBean((String) datos.get("fechaInicioSigPer")).getSQLDate());//
		    		  
		    		  //convenio.put("fec_finconvenio", datos.get("fechaInicioSigPer"));//
		    		  convenio.put("cod_usureg", datos.get("cuser_crea").toString());
		    		  convenio.put("fec_registro", new FechaBean().getTimestamp());
		    		  convenio.put("cod_usumodif", null);
		    		  convenio.put("fec_modif", null);
		    		  t3886DAO.insertarConvenio(convenio);
	    			  grabeConv = true;
	    		  }
	    		  //
		    	}
	    		mapaProg.put("bd",CODIGO_REGISTRO_DEBD);
	    		existenNuevasProg = true;
	    		
	    	} else if (CODIGO_REGISTRO_DEBD_ELIMINADO.equals(mapaProg.get("bd")) && constantes.leePropiedad("INACTIVO").equals(mapaProg.get("visible"))){
	    		columns =new HashMap();
	    		columns.put("est_id",constantes.leePropiedad("INACTIVO"));
	    		columns.put("cuser_mod",mapaProg.get("cuser_crea").toString());
	    		columns.put("fmod",new FechaBean().getTimestamp());
	    		mapaProg.put("columns",columns);
	    		mapaProg.put("periodo",periodoInicial);

	    		t1282DAO.updateCustomColumns(mapaProg);
	    	} else if (CODIGO_REGISTRO_LOGICO_ELIMINADO.equals(mapaProg.get("bd"))){
	    	}
    	}
    	
    	if (existenNuevasProg) {
    		CorreoDAO correoDao = new CorreoDAO(dbpool_sp);
    		String correosmtp = correoDao.findCorreoByRegistro(datos.get("registro").toString());
    		
    		if (correosmtp!=null && !"".equals(correosmtp)){
    			
    			try{//ICAPUNAY 19/07/2012 CONSIDERAR PARA ENVIAR ALERTA Q TIEMPO DE VACACION PROGRAMADA SEA<=FECHA CESE
    				
			  		//String msg = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2//EN\"><html><body><p><strong>Se registr� su Programaci�n de Vacaciones</strong>"; //ICAPUNAY 15/12/2011 Arreglo de acentos
	    			String msg = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2//EN\"><html><body><p><strong>Se registr&oacute; su Programaci&oacute;n de Vacaciones</strong>"; //ICAPUNAY 15/12/2011 Arreglo de acentos
			  	 	msg = msg + "</body></html>";	    	 
			  		Correo correo = new Correo(correosmtp,msg);
			  		correo.enviarHtml();
			  	
			  	//ICAPUNAY 19/07/2012 CONSIDERAR PARA ENVIAR ALERTA Q TIEMPO DE VACACION PROGRAMADA SEA<=FECHA CESE	
    			} catch (CorreoException ce) {
			 		if (log.isDebugEnabled()) log.debug("Error en CorreoException: "+ce.toString());									 	
				 	if (log.isDebugEnabled()) log.debug("No se pudo enviar correo al trabajador con registro: " + datos.get("registro").toString());									 										 	
    			}	
    			//FIN ICAPUNAY 19/07/2012 CONSIDERAR PARA ENVIAR ALERTA Q TIEMPO DE VACACION PROGRAMADA SEA<=FECHA CESE
    		}
    	}
    	return "OK";
    } catch (DAOException e) {
      log.error(e, e);
      MensajeBean beanM = new MensajeBean();
      beanM.setMensajeerror(e.getMessage());
      beanM.setMensajesol("Por favor intente nuevamente, posible error en la BD");
      //throw new FacadeException(this, beanM);
      throw new FacadeException(this, e.toString());
    } catch (Exception e) {
      log.error(e, e);
      MensajeBean beanM = new MensajeBean();
      beanM.setMensajeerror(e.getMessage());
      beanM.setMensajesol("Por favor intente nuevamente.");
      //throw new FacadeException(this, beanM);
      throw new FacadeException(this, e.toString());
    }
    
  }

  /**
   * Mtodo encargado de agregar una programacion de vacaciones
   * @param params Map
   * @return Map 
   * @throws FacadeException
   * @ejb.interface-method view-type="remote"
   * @ejb.transaction type="NotSupported"
   */
  public Map agregarProgramacion(Map params) throws FacadeException {
   	Map mapaResult = new HashMap();
  	boolean existeCruce=false;
   	List listaProgramacion=null;
   	int numdiasTotal=0;
   	String chkconvenio = "";
    try {
    	listaProgramacion = (ArrayList)params.get("listaProgramacion");
    	FechaBean fechaInicio;
    	FechaBean fechaFin;
    	FechaBean fechaParam = new FechaBean(params.get("txtfecha").toString());
    	FechaBean fechaParamFin = new FechaBean(params.get("txtfecha").toString());
    	fechaParamFin.getCalendar().add(Calendar.DAY_OF_YEAR,Integer.parseInt(params.get("num_dias").toString())-1);
    	Map mapaProg=new HashMap();
    	String flag="";
    	for (int i=0;i<listaProgramacion.size();i++) {
    		fechaInicio = new FechaBean(((HashMap)listaProgramacion.get(i)).get("ffinicio_desc").toString());
    		fechaFin = new FechaBean(((HashMap)listaProgramacion.get(i)).get("ffin_desc").toString());
		    if (constantes.leePropiedad("ACTIVO").equals(((HashMap)listaProgramacion.get(i)).get("visible").toString())) {		
    		    if ("agregarReProgramacion".equals(params.get("accion").toString()) && params.get("chkEliminar")!=null){
    		    	flag="0";
    		    	for (int j=0;j<((String[])params.get("chkEliminar")).length;j++){
            		  if (i==Integer.parseInt(((String[])params.get("chkEliminar"))[j])){
            			flag="1";
            			break;
            		  }
            	    }
    		    	if("1".equals(flag)){
    		    		continue;
    		    	}
    		    }
    		    if ("4".equals(((HashMap)listaProgramacion.get(i)).get("est_id").toString())){
    		    }else{
		    	  if ((fechaParam.getCalendar().after(fechaInicio.getCalendar()) &&
		    				fechaParam.getCalendar().before(fechaFin.getCalendar())) || 
		    				fechaParam.getCalendar().equals(fechaInicio.getCalendar()) ||
		    				fechaParam.getCalendar().equals(fechaFin.getCalendar())) {
		            existeCruce = true; 
		            break;
		    		} else if ((fechaParamFin.getCalendar().after(fechaInicio.getCalendar()) &&
    		  		 fechaParamFin.getCalendar().before(fechaFin.getCalendar())) || 
    		  		 fechaParamFin.getCalendar().equals(fechaInicio.getCalendar()) ||
    		  		 fechaParamFin.getCalendar().equals(fechaFin.getCalendar())) {
		           existeCruce = true; 
		           break;
		    		}
    		    }  
		    	
		    }
    	 }
    	
		
    	if (existeCruce) {
    		mapaResult.put("existeFecha", "1");	
    	} else {
    		if ("agregarReProgramacion".equals(params.get("accion").toString())){
    		  mapaProg.put("anno_vac",params.get("periodo").toString());
    		}else{
    		  mapaProg.put("anno_vac",params.get("cod_periodo").toString()); 	  
    		}
    		  mapaProg.put("ffinicio_desc",params.get("txtfecha").toString());
    		  mapaProg.put("ffin_desc",fechaParamFin.getDia().concat("/").concat(fechaParamFin.getMes()).concat("/").concat(fechaParamFin.getAnho()));
    		  mapaProg.put("dias",params.get("num_dias"));
    		  mapaProg.put("uorga",params.get("uorga").toString());
    		  mapaProg.put("bd",constantes.leePropiedad("INACTIVO"));
    		  mapaProg.put("est_id",constantes.leePropiedad("ACTIVO"));
    		  mapaProg.put("visible",constantes.leePropiedad("ACTIVO"));
    		  chkconvenio = params.get("txtchkconv")!=null?params.get("txtchkconv").toString():"0";
    		  if ("1".equals(chkconvenio)){
    			mapaProg.put("ind_conv","1");
    		  }else{
    			mapaProg.put("ind_conv","0");
    		  }
    		  
    		  mapaProg.put("sustento",params.get("txtSustento")!=null?params.get("txtSustento").toString():"");
    		  numdiasTotal = params.get("numdiasTotal")!=null?Integer.parseInt(params.get("numdiasTotal").toString()):0;
    		  listaProgramacion.add(mapaProg);
    		  mapaResult.put("existeFecha", "0");
    		  mapaResult.put("numdiasTotal",String.valueOf(numdiasTotal + Integer.parseInt(params.get("num_dias").toString())));
    	}
    	mapaResult.put("listaProgramacion", listaProgramacion);
    	
      
    } catch (DAOException e) {
      log.error(e, e);
    	MensajeBean beanM = new MensajeBean();
      beanM.setMensajeerror(e.getMessage());
      beanM.setMensajesol("Por favor intente nuevamente, posible error en la BD");
      throw new FacadeException(this, beanM);
    } catch (Exception e) {
      log.error(e, e);
    	MensajeBean beanM = new MensajeBean();
      beanM.setMensajeerror(e.getMessage());
      beanM.setMensajesol("Por favor intente nuevamente.");
      throw new FacadeException(this, beanM);
    }
    return mapaResult;
  }
  
  
  /**
   * Mtodo encargado de eliminar las programaciones de la lista.
   * @param datos Map
   * @return void 
   * @throws FacadeException
   * @ejb.interface-method view-type="remote"
   * @ejb.transaction type="NotSupported"
   */
  public List eliminarProgramacion(Map datos) throws FacadeException {
  	List listaProgramacion=null;
    try {
    	listaProgramacion = (ArrayList)datos.get("listaProgramacion");
    	String[] listaEliminadas = null;
      if(datos.get("chkEliminar")!=null){
        if (datos.get("chkEliminar").getClass() != String.class){
          listaEliminadas = (String[])datos.get("chkEliminar");
        } else{
          listaEliminadas = new String[1];
          listaEliminadas[0] = (String)datos.get("chkEliminar");
        }      
        Map mapaEliminado=null;  
        
        for (int i=0;i<listaEliminadas.length;i++) {
        	mapaEliminado = ((HashMap)listaProgramacion.get(Integer.parseInt(listaEliminadas[i])));
        	if (CODIGO_REGISTRO_DEBD.equals(mapaEliminado.get("bd"))){
        		mapaEliminado.put("bd",CODIGO_REGISTRO_DEBD_ELIMINADO);
        	} else if (CODIGO_REGISTRO_LOGICO.equals(mapaEliminado.get("bd"))){
        		mapaEliminado.put("bd",CODIGO_REGISTRO_LOGICO_ELIMINADO);
        	}
        	mapaEliminado.put("visible",constantes.leePropiedad("INACTIVO"));
        }
      }
    } catch (DAOException e) {
      log.error(e, e);
     	MensajeBean beanM = new MensajeBean();
      beanM.setMensajeerror(e.getMessage());
      beanM.setMensajesol("Por favor intente nuevamente, posible error en la BD");
      throw new FacadeException(this, beanM);
    } catch (Exception e) {
      log.error(e, e);
     	MensajeBean beanM = new MensajeBean();
      beanM.setMensajeerror(e.getMessage());
      beanM.setMensajesol("Por favor intente nuevamente.");
      throw new FacadeException(this, beanM);
    }
    return listaProgramacion;
    
  }

  //ICAPUNAY - PAS20181U230300016 - Mejoras vacaciones2
  /**
   * Mtodo encargado de generar el reporte de trabajadores que faltan programar sus vacaciones
   * @param datos Map
   * @return List 
   * @throws FacadeException
   * @ejb.interface-method view-type="remote"
   * @ejb.transaction type="NotSupported"
   */
  public List generarRepFaltantesProg(Map datos) throws FacadeException {
  	List lista = null;
  	List listaSaldos = null;
  	List listaResult = new ArrayList();
  	Map params= new HashMap();
  	List colabSinProgramacion = new ArrayList();
  	
    try {
    	
    	String dbpool = "jdbc/dcsp";
    	String fechaIngreso =	(datos.get("fechaIngreso") != null) ? ((String)datos.get("fechaIngreso")).trim(): "";	
    	if (log.isDebugEnabled()) log.debug("fechaIngreso: " + fechaIngreso);
    	datos.put("fechaIngreso",fechaIngreso);//fecha de ingreso maxima	??????	
		datos.put("estado",Constantes.ACTIVO);//1
		datos.put("noregimen",Constantes.CODREL_FORMATIVA);
		
    	DataSource dbpool_sp = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dcsp");
    	T02DAO t02DAO = new T02DAO(dbpool_sp);
    	T1281DAO t1281dao = new T1281DAO(dbpool_sp);
    	T1282DAO t1282dao = new T1282DAO(dbpool_sp);
    	pe.gob.sunat.sp.asistencia.dao.T1456DAO t1456dao = new pe.gob.sunat.sp.asistencia.dao.T1456DAO();
    	
    	lista = t02DAO.listarFaltantesProgVac(datos);
    	params.put("anno",new Integer(2007));
    	params.put("saldominimo",new Integer(0));
    	listaSaldos = t1281dao.findSaldos(params);
    	
    	String fecha_ingreso="";
    	String fecha_gen="";
    	String saldoAct="0";
    	int periodo_Act = 0;
    	FechaBean fechabeanHoy = new FechaBean();
    	FechaBean fechabeanGen;

    	String periodo_Act_vac="";
    	String periodo_Sig_vac="";
    	    	
    	Map mregistro = null;
    	String codPersona="";
    	FechaBean fechabeanIngreso;
    	
    	if (lista!=null && !lista.isEmpty()){	
	    	for (int i=0;i<lista.size();i++) {
	    		    		
	    		mregistro = (HashMap)lista.get(i);
				
				codPersona = mregistro.get("t02cod_pers")!=null?mregistro.get("t02cod_pers").toString().trim():"";
				
				Map hmVacGen = t1456dao.joinWithT02ByCodPersEstId(dbpool, codPersona);
				if (log.isDebugEnabled()) log.debug("hmVacGen (t1456dao): " + hmVacGen);
				if (hmVacGen!=null && hmVacGen.get("fecha")!=null && !hmVacGen.get("fecha").toString().equals("")){
		    		fecha_ingreso=hmVacGen.get("fecha").toString();
		    	} else {
		    		fecha_ingreso=mregistro.get("t02f_ingsun")!=null?mregistro.get("t02f_ingsun_desc").toString().trim():"";
		    	}
				mregistro.put("t02f_ingsun_desc",fecha_ingreso);
				if (log.isDebugEnabled()) log.debug("fecha_ingreso (final): " + fecha_ingreso);
				if (!fecha_ingreso.equals("")){
					fecha_gen= new FechaBean(fecha_ingreso).getDia().concat("/").concat(new FechaBean(fecha_ingreso).getMes()).concat("/").concat(fechabeanHoy.getAnho());
		        	fechabeanGen = new FechaBean(fecha_gen);
		        	fechabeanIngreso = new FechaBean(fecha_ingreso);
		        	if (fechabeanHoy.getCalendar().before(fechabeanGen.getCalendar())){//fechabeanHoy=05/01/2018 {22/12/2017} - fechabeanGen=01/12/2018 {01/12/2017}
		        		periodo_Act = Integer.parseInt(fechabeanHoy.getAnho())-1;//2017 {}
		        		if (Integer.parseInt(fechabeanIngreso.getAnho())==periodo_Act){
		        			periodo_Act = periodo_Act +1;
		        		}
		      		} else {//fechabeanHoy=10/12/2018 {22/12/2017}   fechabeanGen=01/12/2018 {01/12/2017}
		      			periodo_Act = Integer.parseInt(fechabeanHoy.getAnho());//2018  {2017}
		      			if (Integer.parseInt(fechabeanIngreso.getAnho())==periodo_Act){
		        			periodo_Act = periodo_Act +1;
		        		}							      			
		      		}
		        	periodo_Act_vac = String.valueOf(periodo_Act);
		        	periodo_Sig_vac = String.valueOf(periodo_Act+1);
		        	if (log.isDebugEnabled()) log.debug("periodo_Act_vac: " + periodo_Act_vac);
		        	if (log.isDebugEnabled()) log.debug("periodo_Sig_vac: " + periodo_Sig_vac);
				}			
				List vacaciones = t1282dao.findVacacionesByCodPersAnio(codPersona,periodo_Act_vac);
				if (log.isDebugEnabled()) log.debug("vacaciones (T1282DAO): " + vacaciones);
				
				List vacacionesSgte = t1282dao.findVacacionesByCodPersAnio(codPersona,periodo_Sig_vac);
				if (log.isDebugEnabled()) log.debug("vacacionesSgte (T1282DAO): " + vacacionesSgte);
				
				if (vacaciones==null || vacaciones.isEmpty()){//sin programacion periodo actual
					if (log.isDebugEnabled()) log.debug("vacaciones==null o vacio - registro: " + codPersona + " - anio: "+periodo_Act_vac);
					mregistro.put("periodo_Act_vac",periodo_Act_vac);
					if (listaSaldos!=null){						      				
	      		        saldoAct = retornaSaldo(listaSaldos,codPersona,periodo_Act_vac);
	      			}
	      			mregistro.put("saldoAct",saldoAct);
	      			
	      			if (vacacionesSgte==null || vacacionesSgte.isEmpty()){//sin programacion periodo sgte
						if (log.isDebugEnabled()) log.debug("vacacionesSgte==null o vacio - registro: " + codPersona + " - anio: "+periodo_Sig_vac);
						mregistro.put("periodo_Sig_vac",periodo_Sig_vac);									
					}      			
	      			else{
	      				mregistro.put("periodo_Sig_vac","PROGRAMADO");
					}      			
					colabSinProgramacion.add(mregistro);				
														
				}else{
					mregistro.put("periodo_Act_vac","PROGRAMADO");
					if (vacacionesSgte==null || vacacionesSgte.isEmpty()){//sin programacion periodo sgte
						if (log.isDebugEnabled()) log.debug("vacacionesSgte==null o vacio - registro: " + codPersona + " - anio: "+periodo_Sig_vac);
						mregistro.put("periodo_Sig_vac",periodo_Sig_vac);
						colabSinProgramacion.add(mregistro);					
					}									
				}
	    	}
    	}	
    } catch (DAOException e) {
      log.error(e, e);
     	MensajeBean beanM = new MensajeBean();
      beanM.setMensajeerror(e.getMessage());
      beanM.setMensajesol("Por favor intente nuevamente, posible error en la BD");
      throw new FacadeException(this, beanM);
    } catch (Exception e) {
      log.error(e, e);
     	MensajeBean beanM = new MensajeBean();
      beanM.setMensajeerror(e.getMessage());
      beanM.setMensajesol("Por favor intente nuevamente.");
      throw new FacadeException(this, beanM);
    }
   
    return colabSinProgramacion;
  }
  //FIN
  
  /**
   * Mtodo encargado de obtener la programacion
   * @param params Map
   * @return List 
   * @throws FacadeException
   * @ejb.interface-method view-type="remote"
   * @ejb.transaction type="NotSupported"
   */
  public List cargarProgramacion(Map params) throws FacadeException {
   	MensajeBean beanM = new MensajeBean();
   	List lista=null;
    try {
      DataSource dbpool_sp = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dcsp");
      T1282DAO t1282dao = new T1282DAO(dbpool_sp);
      params.put("licencia",constantes.leePropiedad("VACACION_PROGRAMADA"));
      params.put("est_id",constantes.leePropiedad("PROGRAMACION_ACEPTADA"));
      lista=t1282dao.findByCodPersAnnoVacEstIdLicencia(params);      
      
    } catch (DAOException e) {
      log.error(e, e);
      beanM.setMensajeerror(e.getMessage());
      beanM.setMensajesol("Por favor intente nuevamente, posible error en la BD");
      throw new FacadeException(this, beanM);
    } catch (Exception e) {
      log.error(e, e);
      beanM.setMensajeerror(e.getMessage());
      beanM.setMensajesol("Por favor intente nuevamente.");
      throw new FacadeException(this, beanM);
    }
    return lista;
  }




  
  /**
   * M�todo encargado de generar el reporte Vacaciones Programadas
   * @param datos Map
   * @return List 
   * @throws FacadeException
   * @ejb.interface-method view-type="remote"
   * @ejb.transaction type="NotSupported"
   */
  public List generarRepVacProgramadas(Map datos) throws FacadeException {
	  if(log.isDebugEnabled())log.debug("ingresando a dao generarRepVacProgramadas");
    try {
    	DataSource dbpool_sp = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dcsp");
    	ParamDAO paramDAO = new ParamDAO();
    	T02DAO t02DAO = new T02DAO(dbpool_sp);
    	List lista = new ArrayList();
    	    	   	
    	List listaDetalle = t02DAO.listarVacProgramadas(datos);
    	if(log.isDebugEnabled())log.debug("listaDetalle.size()-->" + listaDetalle.size());
    	Map mapa = null;
    	String t02cod_pers = "";
    	String t02ap_pate = "";
    	String t02ap_mate= "";
    	String t02nombres = "";
    	String t02f_ingsun = "";
    	String t99descrip = "";
    	String t12des_corta = "";
    	String estado = "";
    	String desde = "";
        String hasta = "";
        String dias = "";
        String t12des_uorga = "";
        String t02cod_cate = "";
        String est_id = "";
        StringBuffer texto = new StringBuffer("");
    	for (int i = 0; i < listaDetalle.size(); i++) {
    		mapa = (HashMap) listaDetalle.get(i);
    		if(log.isDebugEnabled())log.debug("en el beanmaper");
    		if(log.isDebugEnabled())log.debug("mapa-->" + mapa);
      	t02cod_pers = mapa.get("t02cod_pers")!=null? (String)mapa.get("t02cod_pers"):"";
      	t02ap_pate = mapa.get("t02ap_pate")!=null? (String)mapa.get("t02ap_pate"):"";
      	t02ap_mate= mapa.get("t02ap_mate")!=null?(String)mapa.get("t02ap_mate"):"";
      	t02nombres = mapa.get("t02nombres")!=null?(String)mapa.get("t02nombres"):"";
      	t02f_ingsun = mapa.get("t02f_ingsun")!=null?(String)mapa.get("t02f_ingsun_desc"):"";
      	t02cod_cate = mapa.get("t02cod_cate")!=null?(String)mapa.get("t02cod_cate"):"";
      	
      	ParamBean pb = paramDAO.buscar("spprm001t99",ParamDAO.TIPO2,(t02cod_cate).trim()); //diego
      	//t99descrip=paramDAO.buscar("spprm001t99",ParamDAO.TIPO2,(t02cod_cate).trim()).getDescripcion().trim();
        t99descrip=pb!=null?pb.getDescripcion().trim():"";
      	t12des_corta = mapa.get("t12des_corta")!=null?(String)mapa.get("t12des_corta"):"";
      	 
      	texto =  new StringBuffer(t02cod_pers.trim()).append( " - ").append(t02ap_pate.trim()).append( " ")
				.append(t02ap_mate.trim()).append( ", ").append( t02nombres.trim())
				.append(" (").append( t02f_ingsun).append( ")")
				.append("  -  " ).append(t99descrip.trim() ).append( " / ").append(t12des_corta.trim());
				
				if(log.isDebugEnabled())log.debug("texto-->" + texto);
				
      	mapa.put("trabajador", texto.toString().trim());
      	est_id = mapa.get("est_id")!=null?(String)mapa.get("est_id"):"";
      	
    	//ParamBean pb2 = paramDAO.buscar("spprmV01t99",ParamDAO.TIPO2,est_id.trim()); //diego      	
      	String dbpool = "jdbc/dcsp";
      	T99DAO t99dao = new T99DAO(dbpool);
      	DataSource ds = sl.getDataSource(dbpool);	
      	ParamBean pb2 = t99dao.buscar(new String []{"V01"}, ds, est_id.trim());//parametro de usuario quien ejecuta asistencia
      	
        estado=pb2!=null?pb2.getDescripcion().trim():"";
      	mapa.put("estado", estado);
        
        desde= mapa.get("desde")!=null?mapa.get("desde_desc").toString().trim().substring(0,10):"";
        mapa.put("desde", desde);
        hasta= mapa.get("hasta")!=null?mapa.get("hasta_desc").toString().trim().substring(0,10):"";
        mapa.put("hasta", hasta);
        
        dias= mapa.get("dias")!=null?mapa.get("dias").toString().trim():"";
        mapa.put("dias", dias);        
        
        t12des_uorga= mapa.get("t12des_uorga")!=null?mapa.get("t12des_uorga").toString().trim():"";
        mapa.put("t12desuorga", t12des_uorga);
        
        lista.add(mapa);
    	}
    	//ASZ
    	if(log.isDebugEnabled())log.debug("lista: " + lista);
    	//    	
    	return lista;
    	
    } catch (DAOException e) {
      log.error(e, e);
    	MensajeBean beanM = new MensajeBean();
      beanM.setMensajeerror(e.getMessage());
      beanM.setMensajesol("Por favor intente nuevamente, posible error en la BD");
      throw new FacadeException(this, beanM);
    } catch (Exception e) {
      log.error(e, e);
    	MensajeBean beanM = new MensajeBean();
      beanM.setMensajeerror(e.getMessage());
      beanM.setMensajesol("Por favor intente nuevamente.");
      throw new FacadeException(this, beanM);
    }finally{
    	log.debug("fin de generarRepVacProgramadas");
    }
    
  }
  
  /**
   * Mtodo encargado de grabar la Reprogramacion de Vacaciones
   * @param datos Map
   * @return void 
   * @throws FacadeException
   * @ejb.interface-method view-type="remote"
   * @ejb.transaction type="RequiresNew"
   */
  public void grabarReProgVacaciones(Map datos) throws FacadeException {
   	MensajeBean beanM = new MensajeBean();
    try {
    	DataSource dbpool_sp = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dgsp");
    	T1282DAO t1282DAO = new T1282DAO(dbpool_sp);
    	T1281DAO t1281dao = new T1281DAO(dbpool_sp);
    	List listaProgramacion = (ArrayList)datos.get("listaProgramacion");
    	String[] listaEliminadas = null;
    	Map mapaEliminado=null;
    	if(datos.get("chkEliminar")!=null){
    		listaEliminadas = (String[])datos.get("chkEliminar");
        for (int i=0;i<listaEliminadas.length;i++) {
        	mapaEliminado = ((HashMap)listaProgramacion.get(Integer.parseInt(listaEliminadas[i])));
        	if (CODIGO_REGISTRO_DEBD.equals(mapaEliminado.get("bd"))){
        		mapaEliminado.put("bd",CODIGO_REGISTRO_DEBD_ELIMINADO);
        	} else if (CODIGO_REGISTRO_LOGICO.equals(mapaEliminado.get("bd"))){
        		mapaEliminado.put("bd",CODIGO_REGISTRO_LOGICO_ELIMINADO);
        	}
        	mapaEliminado.put("visible",constantes.leePropiedad("INACTIVO"));
        }
    	}
    	Map mapaProg=null;
    	Map mapaAnt= null;
    	FechaBean fechaHoy = new FechaBean();
    	String periodo = fechaHoy.getAnho().concat(fechaHoy.getMes());
    	Map columns = null;
    	for (int i=0;i<listaProgramacion.size();i++) {
    		mapaProg = (HashMap)listaProgramacion.get(i);
	    	mapaProg.put("cod_pers",datos.get("registro"));
	    	mapaProg.put("licencia",constantes.leePropiedad("VACACION_PROGRAMADA"));
	    	mapaProg.put("anno_vac",datos.get("periodo").toString());
	    	mapaProg.put("u_organ",datos.get("uorga").toString());
	    	mapaProg.put("periodo",periodo);
	    	mapaProg.put("fcreacion",new FechaBean().getTimestamp());
	    	//String sustento = datos.get("sustento")!=null?datos.get("sustento").toString():"";
	    	//mapaProg.put("sustento", sustento);
	    	
	    	log.debug("prueba111:"+mapaProg.toString());
	    	if (CODIGO_REGISTRO_LOGICO.equals(mapaProg.get("bd"))){
	    		mapaProg.put("ffinicio",new FechaBean((String) mapaProg.get("ffinicio_desc")).getTimestamp());
		    	mapaProg.put("ffin",new FechaBean((String) mapaProg.get("ffin_desc")).getTimestamp());
		    	mapaProg.put("est_id",constantes.leePropiedad("ACTIVO"));
		    	mapaAnt = t1282DAO.findAllColumnsByKey(mapaProg);
		    	if (mapaAnt!=null){
		    		columns =new HashMap();
		    		columns.put("dias",mapaProg.get("dias").toString());
		    		columns.put("ffin",new FechaBean((String) mapaProg.get("ffin_desc")).getTimestamp());
		    		columns.put("est_id",constantes.leePropiedad("ACTIVO"));
		    		columns.put("est_id",constantes.leePropiedad("ACTIVO"));
		    		columns.put("cuser_mod",datos.get("registro"));
		    		columns.put("fmod",new FechaBean().getTimestamp());
		    		mapaProg.put("columns",columns);
		    		//EBV 16/02/2009 se cambia ya que no debe actualizar el periodo actual
		    		t1282DAO.updateCustomColumnsSinPeriodo(mapaProg);
		    	} else {
		    		mapaProg.put("ind_conv","0");
		    		t1282DAO.insertarVacacionesDetalle(mapaProg);	
		    	}
	    	} else if (CODIGO_REGISTRO_DEBD_ELIMINADO.equals(mapaProg.get("bd"))){
	    		mapaProg.put("est_id",constantes.leePropiedad("INACTIVO"));
	    		columns =new HashMap();
	    		columns.put("est_id",constantes.leePropiedad("INACTIVO"));
	    		columns.put("cuser_mod",datos.get("cod_usumodif").toString());
	    		columns.put("fmod",new FechaBean().getTimestamp());
	    		mapaProg.put("columns",columns);
	    		//EBV 16/02/2009 se cambia ya que no debe actualizar el periodo actual
	    		t1282DAO.updateCustomColumnsSinPeriodo(mapaProg);
	    		mapaProg.put("licencia",constantes.leePropiedad("VACACION"));
	    		t1282DAO.deleteByPrimaryKeySinPeriodo(mapaProg);
	    		//t1282DAO.updateCustomColumns(mapaProg);
	    	} 
    	} 
  			datos.put("cod_pers",datos.get("registro"));
  			datos.put("anno",datos.get("periodo"));
  			Map saldo=t1281dao.findAllColumnsByKey(datos);
  			columns= new HashMap();
  			columns.put("saldo",new Integer(((Integer)saldo.get("saldo")).intValue()+(new Integer(datos.get("diasAnulados").toString())).intValue()));  
  			datos.put("columns",columns);
  			t1281dao.updateCustomColumns(datos);
    } catch (DAOException e) {
      log.error(e, e);
      beanM.setMensajeerror(e.getMessage());
      beanM.setMensajesol("Por favor intente nuevamente, posible error en la BD");
      throw new FacadeException(this, beanM);
    } catch (Exception e) {
      log.error(e, e);
      beanM.setMensajeerror(e.getMessage());
      beanM.setMensajesol("Por favor intente nuevamente.");
      throw new FacadeException(this, beanM);
    }
    
  }
  
  /**
   * M�todo encargado de buscar  
   * @param datos Map
   * @return List
   * @throws FacadeException
   * @ejb.interface-method view-type="remote"
   * @ejb.transaction type="NotSupported"
   */
  public List joinWithT02T12(Map datos) throws FacadeException {
    List lista = null;
    try {
      PadronFacadeHome facadeHome = (PadronFacadeHome)ServiceLocator.
      getInstance().getRemoteHome( PadronFacadeHome.JNDI_NAME,PadronFacadeHome.class);
      PadronFacadeRemote pf = facadeHome.create();
      lista = pf.joinWithT02T12(datos);
    } catch (DAOException e) {
      log.error(e, e);
      MensajeBean beanM = new MensajeBean();
      beanM.setMensajeerror(e.getMessage());
      beanM.setMensajesol("Por favor intente nuevamente, posible error en la BD");
      throw new FacadeException(this, beanM);
    } catch (Exception e) {
      log.error(e, e);
      MensajeBean beanM = new MensajeBean();
      beanM.setMensajeerror(e.getMessage());
      beanM.setMensajesol("Por favor intente nuevamente.");
      throw new FacadeException(this, beanM);
    }
    return lista;
  }
  
  /**
   * metodo buscar: que se encarga de buscar parametros como es categoria y unidad organizacional
   * @param params Map, contiene los criterios de b�squeda. 
   * @return List, retorna el resultado de la b�squeda
   * @throws FacadeException
   * 
   * @ejb.interface-method view-type="remote"
   * @ejb.transaction type="NotSupported"
   */
  public List buscarUOeIntendencia(Map params) throws FacadeException {
    List listados = null;
    try {
      String opcion = ((String)params.get("opcion")).trim();
      DataSource dbpool_sp = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dcsp");
      T12DAO daospt12 = new T12DAO(dbpool_sp);
      if ("uor".equals(opcion)) {
        listados = daospt12.findByBusquedaGen(params);
      } else if ("int".equals(opcion)) {
        listados = daospt12.findByBusquedaInt(params);
      }
    } catch (DAOException e) {
      log.error(e, e);
      MensajeBean beanM = new MensajeBean();
      beanM.setMensajeerror(e.getMessage());
      beanM.setMensajesol("Por favor intente nuevamente, posible error en la BD");
    } catch(Exception e) {
      log.error(e);
      MensajeBean beanM = new MensajeBean();
      beanM.setError(true);
      beanM.setMensajeerror(e.getMessage());
      beanM.setMensajesol("Por favor intente nuevamente.");
      throw new FacadeException(this,beanM);      
    } finally {
    }
    return listados;
  } 
  
  /**
   * metodo retornaSaldo: que se encarga de retornar el saldo de un anno vacacional para una persona
   * @param List listaSaldos 
   * @return String, retorna el resultado de la bsqueda
   * @throws FacadeException
   * 
   * @ejb.interface-method view-type="remote"
   * @ejb.transaction type="NotSupported"
   */
  public String retornaSaldo(List listaSaldos,String codPersona,String annovac) throws FacadeException {
    String saldo="0";
    try {
      for (int i=0;i<listaSaldos.size();i++) {
      	if (codPersona.equals(((HashMap)listaSaldos.get(i)).get("cod_pers").toString()) &&
      			annovac.equals(((HashMap)listaSaldos.get(i)).get("anno").toString())) {
      		saldo = ((HashMap)listaSaldos.get(i)).get("saldo").toString();
      		break;
      	}
      }
    	
    } catch (DAOException e) {
      log.error(e, e);
      MensajeBean beanM = new MensajeBean();
      beanM.setMensajeerror(e.getMessage());
      beanM.setMensajesol("Por favor intente nuevamente, posible error en la BD");
    } catch(Exception e) {
      log.error(e);
      MensajeBean beanM = new MensajeBean();
      beanM.setError(true);
      beanM.setMensajeerror(e.getMessage());
      beanM.setMensajesol("Por favor intente nuevamente.");
      throw new FacadeException(this,beanM);      
    } finally {
    }
    return saldo;
  }  
  
}
