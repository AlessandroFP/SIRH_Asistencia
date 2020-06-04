package pe.gob.sunat.utils;

/**
 * 
 * <p>Title: Constantes</p>
 * <p>Description: Clase encargada de almacenar las constantes de las aplicaciones</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SUNAT</p>
 * @author cgarratt
 * @version 1.0
 */
public class Constantes {

  public Constantes() {
  }

  //Roles de la aplicacion
  public static String ROL_EMPLEADO = "SIRH-EMPLEADO";
  public static String ROL_JEFE = "SIRH-JEFE";
  public static String ROL_SECRETARIA = "SIRH-SECRETARIA";
  public static String ROL_ANALISTA_CENTRAL = "SIRH-ANAL.CENTRAL";
  public static String ROL_ANALISTA_OPERATIVO = "SIRH-ANAL.OPERATIVO";
  public static String ROL_ANALISTA_LEXC = "SIRH-ANAL.LEXCEPCIONAL";
  
  //Procesos
  public static String PROCESO_ASISTENCIA = "1";
  public static String PROCESO_CALIFICACION = "2";
  public static String PROCESO_CIERRE = "3";
  public static String PROCESO_VACACIONES = "4";

  //Sequence ORACLE
  public static String SEQ_SOLICITUD = "sirh_solicitud";
  public static String SEQ_LICENCIA = "sirh_licencia";

  //Constantes Genericas
  public static String OK = "OK";
  public static String ACTIVO = "1";
  public static String INACTIVO = "0";
  public static String SALIDA = "SALI";
  public static String RELOJ_MANUAL = "MMM";
  public static int HORAS_JORNADA = 8;
  public static int SABADO  = 6;
  public static int DOMINGO = 0;
  public static int DIAS_PLAZO_RECTIFICACIONES = 2;
  public static String TITULO_CORREO = "NOTIFICACI&Oacute;N DE ASISTENCIA";
  //JVV
  public static String TITULO_CORREO_DIRECTIVOS = "SIRH-Asistencia Notificaci&oacute;n a directivos";
  public static String TITULO_CORREO_TRABAJADORES = "SIRH-Asistencia Notificaci&oacute;n a trabajadores";
  public static String TITULO_CORREO_LABOR_AUTORIZA = "NOTIFICACI&Oacute;N DE AUTORIZACI&Oacute;N DE LABOR EXCEPCIONAL";//ICAPUNAY 05/10/2012 AJUSTES AOM 06A4T11 LABOR EXCEPCIONAL
  public static String TITULO_CORREO_LABOR_RECHAZO = "NOTIFICACI&Oacute;N DE RECHAZO DE LABOR EXCEPCIONAL";//ICAPUNAY 05/10/2012 AJUSTES AOM 06A4T11 LABOR EXCEPCIONAL
  public static String TITULO_CORREO_SALDO_VACACIONAL = "NOTIFICACI&Oacute;N DE GENERACI&Oacute;N DE SALDO VACACIONAL";
  public static String CODREL_REG276 = "01";
  public static String CODREL_REG728 = "02";
  public static String CODREL_REG1057 = "09";
  public static String CODREL_FORMATIVA = "10"; //ICAPUNAY 08/06/2011 AOM 36U1T11 FORMATIVAS
  //Rutas
  public static String RUTA_MARCACIONES = "/data0/sip/dat/";
  public static String RUTA_LOG_REPORTES = "/data0/sip/log/";
  public static String RUTA_LOG_PROCESOS = "/data0/sip/log/";
  
  //Tipos de Movimientos
  public static String DESCUENTO = "S";
  public static String SIN_DESCUENTO = "N";

  //Licencias
  public static int DIAS_ACUMULADOS_LICENCIAS = 20;  
  public static String LICENCIA_ENFERMEDAD = "21";
  public static String LICENCIA_JUSTIF_INASIST_DESCONT = "134"; //PAS20165E230300184 WRR - Se agrega por ajuste de solicitud 134
  public static String LICENCIA_MATRIMONIO = "23";
  //PRAC-ASANCHEZ
  public static String FALLECIMIENTO_FAMILIAR = "24";
  //
  public static String LICENCIA_COMPENSACION = "31";
  public static String FERIADO_COMPENSABLE = "38";  
  public static String LICENCIA_BOLSA = "40";
  public static String LICENCIA_ELECCIONES = "41";
  public static String LICENCIA_ONOMASTICO = "44";
  public static String FERIADO_NO_COMPENSABLE = "50";
  public static String LICENCIA_NACIMIENTO = "53";
  public static String PERMISO_CAPACITACION = "58";
  public static String LICENCIA_TITULACION = "61";
  public static String LICENCIA_PRENATAL = "14";
  public static String LICENCIA_POSTNATAL = "22";
  public static String LICENCIA_PARTO_MULTIPLE = "36";
  public static String LICENCIA_LABOR_EXCEPCIONAL = "31";
  public static String LICENCIA_GRAVIDEZ = "74";
  public static String LICENCIA_GRAVIDEZ_MULTIPLE = "75";
  
  public static String LICENCIA_SINGOCEHABER_HASTA3DIAS="06"; //WERR-PAS20155E230300132
  public static String SALDO_HORASEXTRA="540"; //WERR-PAS20155E230300132
  public static String COMISIONSERV_CITACJUDICIAL = "103"; //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
  
  //Vacaciones
  public static String VACACION = "07";
  public static String VACACION_VENTA = "46";
  public static String VACACION_SUSPENDIDA = "47";
  public static String VACACION_ESPECIAL = "48";
  public static String VACACION_PROGRAMADA = "49";
  public static String REPROGRAMACION_VACACION = "64";
  public static String VACACION_POSTERGADA = "54";
  //public static String VACACION_ADELANTADA = "63";
  public static String VACACION_INDEMNIZADA = "63";
  public static String CADENA_VACACION_ESPECIAL = "VACACION ESPECIAL";
  public static String REGIMEN_LABORAL_276 = "2";

  //Correlativos
  public static String CORREL_SOLICITUD = "01";
  public static String CORREL_LICENCIA = "03";
  public static String CORREL_VACACIONES = "02";
  public static String CORREL_ASISTENCIA = "04";
  public static String CORREL_PAPELETA = "05";

  //T01Params
  public static String T01CABECERA = "C";
  public static String T01DETALLE = "D";
  public static String T01CODTAB_FERIADO = "722";
  public static String T01CODTAB_ESTADOCIVIL = "702";

  //T99Codigos - SIRH Asistencia
  public static String T99CABECERA = "C";
  public static String T99DETALLE = "D";
  public static String CODTAB_CATEGORIA = "001";
  public static String CODTAB_CORREL = "501";
  public static String CODTAB_TIPOMOV = "502";
  public static String CODTAB_MEDIDA = "503";
  public static String CODTAB_DIAS_LICENCIA = "504";
  public static String CODTAB_DIAS_VACACIONES = "505";
  public static String CODTAB_REQUISITOS_LICENCIA = "506";
  public static String CODTAB_ESTADO_PAPELETA = "507";
  public static String CODTAB_TIPO_ENFERMEDAD = "508";
  public static String CODTAB_EST_VAC_PROGRAMADA = "509";
  public static String CODTAB_PARAMETROS_ASISTENCIA = "510";
  public static String CODTAB_DIAS_LIC_ADMINLIC = "472"; //ICAPUNAY - PAS20155E230300168 - Modificar dias de licencia prenatal, postnatal y gravidez (ley 30367)
  public static String CODTAB_ALERTA_CAMBIO_TURNO = "720"; //ICAPUNAY - PAS20165E230300096 - Alerta de Cambio de turno
  public static String CODTAB_VISUALIZAR_TURNOS_NOCONTROLADOS = "721"; //ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
  public static String CODTAB_DELEGACION = "512";
  public static String CODTAB_FECHA_LEVANTA = "513";
  public static String CODTAB_INSTANCIA = "561";    
  public static String CODTAB_TIPO_PAPELETA = "05";
  public static String CODTAB_TIPO_ASISTENCIA = "04";
  public static String CODTAB_SUPERVISOR = "585";
  public static String CODTAB_PERIODOS_BOLETA = "588";
  
  //Parametros SIRH Asistencia
  public static String DIAS_VACACIONES = "01";
  public static String DIAS_VENTA_VACACIONES = "02";
  public static String MIN_DIAS_LABORABLES = "03";
  public static String MINUTOS_SIN_DESCUENTO = "04";
  public static String DIAS_ENFERMEDAD = "05";
  public static String SALDO_MINIMO_REPROGRAMACION = "06";
  public static String MINUTOS_LABOR_EXCEPCIONAL = "06";
  public static String DIAS_HABILES_MES_VENTA_VACACIONES = "08";
  public static String FECHA_INICIO_COMPENSACION = "09";
  public static String FECHA_INICIO_SIRH_NSA = "10";
  public static String FECHA_INICIO_LABOR_EXCEPCIONAL = "30";//ICAPUNAY 11/06/2012 NOVEDADES CON INDICADOR=2 DESDE FECHA PARAMETRICA	
  public static String MINUTOS_MAXIMO_CLIMALABORAL = "35";//ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
  public static String FECHA_MININA_CLIMA_LABORAL = "36";//ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral

  //Estados papeletas
  public static String PRE_CALIFICACION_ASIS = "0";
  public static String PAPELETA_REGISTRADA = "1";
  public static String PAPELETA_ACEPTADA = "2";
  public static String PAPELETA_PROCESADA = "3";
  public static String PAPELETA_RECHAZADA = "4";
  public static String ASISTENCIA_CALIFICADA = "5";

  //Onomasticos
  public static int RANGO_DIAS_INF_ONOMASTICO = -7;
  public static int RANGO_DIAS_SUP_ONOMASTICO = 7;

  //Calificaciones por defecto
  public static String ENTRADA_NORMAL = "51";
  public static String MOVIMIENTO_NORMAL = "NN";
  public static String SALIDA_NORMAL = "52";

  //Medidas
  public static String MINUTO = "01";
  public static String HORA = "02";
  public static String DIA = "03";
  public static String MES = "04";
  public static String ANNO = "05";

  //Proceso de Asistencia
  public static String MOV_INASISTENCIA = "00";
  public static String MOV_VACACIONES = "07";
  public static String MOV_TARDANZA_CON_DESCUENTO = "01";
  public static String MOV_TARDANZA_SIN_DESCUENTO = "02";
  public static String MOV_TARDANZA_EFECTIVA = "98";
  public static String MOV_ENTRADA_ANTICIPADA = "99";
  public static String MOV_REFRIGERIO = "08";
  public static String MOV_SALIDA_NO_AUTORIZADA = "12";
  public static String MOV_TRABAJO_CAMPO = "16";
  public static String MOV_OTRO_EDIFICIO = "13";
  public static String MOV_OMISION_MARCA = "10";
  public static String MOV_ANULACION_MARCA = "60";
  public static String MOV_EXCESO_REFRIGERIO = "09";
  public static String MOV_APORTACION = "18";
  public static String MOV_COMISION_SERVICIOS = "19";
  public static String MOV_EN_OBSERVACION = "29";
  public static String MOV_PERMISO_SIN_GOCE = "03";
  public static String MOV_PAPELETA_NO_AUTORIZADA = "70";
  public static String MOV_LABOR_FUERA_OFICINA = "097";
  public static String MOV_PAPELETA_COMPENSACION = "71";
  public static String MOV_SUSPENSION = "008";
  public static String MOV_SALIDA_AUTORIZADA = "72";
  public static String MOV_RESOLUCION_067 = "76";
  public static String MOV_PERMISO_SUBVENCIONADO = "121";//ICAPUNAY 08/06/2011 AOM 36U1T11 FORMATIVAS
  public static String MOV_PERMISO_NO_SUBVENCIONADO = "122";//ICAPUNAY 08/06/2011 AOM 36U1T11 FORMATIVAS
  public static String MOV_LABOR_EXCEPCIONAL = "124"; //MTOMAYLL 12/03/2012
  public static String MOV_SOLICITUD_COMPENSACION = "125"; //EBV 03/04/2012 
  public static String MOV_PERM_PART_CON_DESCUENTO_03 = "03"; //ICR 10062015 - PAS20155E230300022 - ajustes a generacion y proceso asistencia
  public static String MOV_PERM_PART_CON_DESCUENTO_100 = "100"; //ICR 10062015 - PAS20155E230300022 - ajustes a generacion y proceso asistencia 
  public static String MOV_REFRI_CLIMALABORAL = "136"; //ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral 
  
  public static String NIVEL4 = "4"; //ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral 
  public static String NIVEL5 = "5"; //ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral 
  public static String NIVEL6 = "6"; //ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral 
  public static String MOV_REINTEGRO_POR_DESCUENTO_ASISTENCIA = "301"; //PAS20171U230200001 - solicitud de reintegro  
  public static String MOV_REINTEGRO_POR_DESCUENTO_SUBSIDIO = "302"; //PAS20171U230200001 - solicitud de reintegro  
  //Tipos de Movimiento
  public static String TIPO_MOV_VACACIONES = "02";
  public static String TIPO_MOV_LICENCIA = "03";
  public static String TIPO_MOV_PAPELETA = "05";

  //Acciones de Solicitudes
  public static String ACCION_INICIAR = "1";
  public static String ACCION_APROBAR = "2";
  public static String ACCION_RECHAZAR = "3";
  public static String ACCION_DERIVAR_RRHH = "4";

  //Estados de Solicitudes
  public static String ESTADO_SEGUIMIENTO = "1";
  public static String ESTADO_CONCLUIDA = "2";
  
  //Estaciones del flujo de solicitudes
  public static String ESTACION_INICIAL = "1";
  public static String ESTACION_INTERMEDIA = "2";
  public static String ESTACION_FINAL = "3";
  public static String ESTACION_UNICA = "4";
  
  //Estados de las Vacaciones programadas
  public static String PROG_PROGRAMADA = "0";
  public static String PROG_ACEPTADA = "1";
  public static String PROG_EFECTUADA = "2";
  public static String PROG_RECHAZADA = "3";

  //Accion Log Saldos Vacacionales
  public static String ACCION_LOG_SALDOS = "011";
  
  //Opciones con Delegacion
  public static String DELEGA_TODO = "1";
  public static String DELEGA_SOLICITUDES = "2";
  public static String DELEGA_LABOR_EXCEP = "3";
  public static String DELEGA_PAPELETAS = "4";
  public static String DELEGA_VACACIONES = "5";  
  
  //Trunos
  public static String TURNO_RS067 = "R67";
  
  //Constantes Siged
  public static String T01CODTAB_SIGED_DOC = "001";
  public static String T01CODTAB_ESTADOS = "002";
  public static String T01CODTAB_REFERENCIA_DOC = "003";
  public static String T01CODTAB_CONFIDENCIAL = "004";
  public static String T01CODTAB_ADUANAS = "005";
  public static String T01CODTAB_FLUJOS = "006";
  public static String T01USR_GENERICOS = "007";
  public static String T01EST_REVISION = "002";
  public static String T01EST_SEGUIMIENTO = "003";
  public static String T01EST_CONCLUIDO = "004";
  public static String IND_CONFIDENCIAL = "001";
  public static String LINEAS_PAGINA = "20";  
  
  //T99Codigos - Declaraciones Juradas  
  public static String CODTAB_ALCANCE = "551";
  public static String CODTAB_DDJJ_TIPOS = "552";
  public static String CODTAB_DDJJ_VIGENCIA = "553";
  public static String CODTAB_DDJJ_PARIENTES = "554";
  public static String CODTAB_DDJJ_ESTCAB = "555";
  public static String CODTAB_DDJJ_MOTIVOS = "556";
  public static String CODTAB_DDJJ_OCUPACIONES = "558";
  public static String CODTAB_DDJJ_ACCIONES = "559";
  public static String CODTAB_DDJJ_TIPDOC = "562";
  public static String CODTAB_DDJJ_ENTIDAD = "563";
  public static String CODTAB_DDJJ_DPTOS = "700";
  
  //Estados Declaraciones T1539cab_ddjj (ind_estado_id)
  public static String DDJJ_INICIAL = "0";
  public static String DDJJ_GRAB_CON_OBS = "1";
  public static String DDJJ_GRAB_SIN_OBS = "2";  
  public static String DDJJ_APROBADO = "3";
  public static String DDJJ_RECHAZADO = "4";

  //Estados Declaraciones T1540fam_ddjj (ind_estado_id) 
  public static String DDJJ_ESTADO_ACTIVO = "1";
  public static String DDJJ_ESTADO_INACTIVO = "0";
  
  //Constantes Declaraciones
  public static String DDJJ_PARIENTES = "2";
  public static String DDJJ_BIENES = "3";
  public static String DDJJ_SECCION = "1";
  public static String DDJJ_GRUPO = "2";
  public static String DDJJ_RUBRO = "3";
  public static String DDJJ_CAMPANAS = "1";
  public static String DDJJ_ACTUALIZACION_DATOS = "3";
  
  //Constantes Flujo Declaraciones
  public static String DDJJ_ACCION_DERIVAR = "1";
  public static String DDJJ_ACCION_APROBAR = "2";
  public static String DDJJ_ACCION_RECHAZAR = "3";
  
  //Constantes Seleccion Personal
  public static String SELECCION_LEDNI = "01";
  public static String SELECCION_ESTCIV = "111";
  public static String SELECCION_UBIGEO = "700";
  public static String SELECCION_VIA = "058";
  public static String SELECCION_ZONA = "059";
  public static String SELECCION_IDIOMA = "013";
  public static String SELECCION_ESPEC = "003";
  public static String SELECCION_NIVEL = "002";
  public static String SELECCION_CENTRO = "004";
  public static String SELECCION_PUESTO = "580";
  public static String SELECCION_EXPERIENCIA = "581";
  public static String SELECCION_TIPOFAM = "554";
  public static String SELECCION_PADREMADRE = "A12";
  
  //Constante de estado usado  solicitud de reintegro
  public static String SOL_REINTEGRO_ESTADO_ACTIVO = "A" ;//PAS20171U230200001 - solicitud de reintegro  
  public static String SOL_REINTEGRO_ESTADO_INACTIVO = "I" ;//PAS20171U230200001 - solicitud de reintegro  
  
  //Tipo de regimen :tregimen_laboral oracle
  public static String 	D_LEG_728_P_I = "1" ;//PAS20171U230200001 - solicitud de reintegro  
  public static String 	D_LEG_276 = "2" ;//PAS20171U230200001 - solicitud de reintegro  
  public static String 	D_LEG_728_S_M = "3" ;//PAS20171U230200001 - solicitud de reintegro  
  public static String 	D_LEG_728_T_P = "4" ;//PAS20171U230200001 - solicitud de reintegro   
  public static String 	D_LEG_728_C_E_S = "6" ;//PAS20171U230200001 - solicitud de reintegro  
  public static String 	D_LEG_728_C_N_TEMP = "7" ;//PAS20171U230200001 - solicitud de reintegro  
  public static String 	D_LEG_1057_CAS = "8" ;//PAS20171U230200001 - solicitud de reintegro  
  
  //Tipo de planillas tplanilla
  public static String 	PLANILLA_HABERES = "01" ;//PAS20171U230200001 - solicitud de reintegro  
  public static String 	PLANILLA_CAS = "02" ;//PAS20171U230200001 - solicitud de reintegro  
  
  //Estados de concepto
  public static String 	SOL_REINTEGRO_TL_LICENCIA = "L";//PAS20171U230200001 - solicitud de reintegro
  public static String 	SOL_REINTEGRO_TL_SUBSIDIO = "S";//PAS20171U230200001 - solicitud de reintegro
  public static String 	SOL_REINTEGRO_TL_PAPELETA = "P";//PAS20171U230200001 - solicitud de reintegro
  
  //Tipos de movimiento
  public static String 	TMOV_LICENCIA_ENFERMEDAD = "07";//PAS20171U230200001 - solicitud de reintegro
  public static String 	TMOV_SUBSIDIO_ENFERMEDAD = "07";//PAS20171U230200001 - solicitud de reintegro
  
  //Minimo de dias de saldo
  public static Integer MIN_SALDO_LICENCIA_ENF = new Integer(20);//PAS20171U230200001 - solicitud de reintegro
  public static Integer MIN_SALDO_SUBSIDIO_ENF = new Integer(20);//PAS20171U230200001 - solicitud de reintegro
  
  //Tipos de licencia sin goce
  public static String TMOT_SIN_GOCE = "16-09-90-86-07-42-87-88-89-17";//PAS20171U230200001 - solicitud de reintegro
  
  
  public static String TIPO_SALIDA_ASIS = "'1','4','5','7'";//PAS20171U230200001 - solicitud de reintegro
  
  public static String TIPO_SALIDA_LIC_PER = "'2','4','6','7'";//PAS20171U230200001 - solicitud de reintegro
  
  public static String TIPO_SALIDA_SUB =     "'3','5','6','7'";//PAS20171U230200020 - solicitud de reintegro //3,5,6,7
  
  
  public static String EST_ARC_TEMP_ANULADO="0";  
  public static String EST_ARC_TEMP_OK="1";  
  public static String EST_ARC_TEMP_PARA_AGREGAR="2";
  public static String EST_ARC_TEMP_PARA_BORRAR="3";
  public static String EST_ARC_TEMP_COPIAR_PERMANENTE="4";
  
  
  public static String ARCHIVO_MODO_ESCRITURA ="1";
  public static String ARCHIVO_MODO_LECTURA ="0";
  
  
  
  //PAS20181U230200023-Licencia por enfermedad
  public static String T5864_CODTAB_DIAG_CIE10= "018";
  public static String T5864_CODTAB_HABILITACIONES= "017";
  public static String T5864_CODTAB_HABILITACIONES_MENSAJE_LICENCIA= "0000000001";
  
  public static String ESTADO_ACTIVO_DEPENDENCIAS= "1";
  
  
}