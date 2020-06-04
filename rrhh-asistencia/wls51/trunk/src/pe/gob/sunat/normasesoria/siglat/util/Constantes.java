package pe.gob.sunat.normasesoria.siglat.util;

import pe.gob.sunat.framework.util.Propiedades;

public interface Constantes {
	
	/**
	 * Dentro de este archivo de propiedades se han definido las propiedades del sistema.
	 */
	static final String PROPERTIES_FILENAME = "/pe/gob/sunat/normasesoria/siglat/util/siglat.properties";
	static Propiedades propiedades = new Propiedades(
	      Constantes.class, PROPERTIES_FILENAME);
	
	//JNDI 
	public static final String POOL_SIGLAT_ESC = propiedades.leePropiedad("POOL_SIGLAT_ESC");
	public static final String POOL_SIGLAT_LEC = propiedades.leePropiedad("POOL_SIGLAT_LEC");
	public static final String POOL_SIGLAT_SEQ = propiedades.leePropiedad("POOL_SIGLAT_SEQ");
	public static final String POOL_SP_LEC = propiedades.leePropiedad("POOL_SP_LEC");
	public static final String POOL_SIGAD_LEC = propiedades.leePropiedad("POOL_SIGAD_LEC");
	
	//DATA SOURCE
	public static final String DS_DRIVER = propiedades.leePropiedad("DS_DRIVER");
	public static final String DS_URL = propiedades.leePropiedad("DS_URL");
	public static final String DS_USR = propiedades.leePropiedad("DS_USR");
	public static final String DS_PWD = propiedades.leePropiedad("DS_PWD");

	//ESTADO UUOO ACTIVO y ESTADO APLIC
	public static final String UUOO_ESTADO_ACTIVO = propiedades.leePropiedad("UUOO_ESTADO_ACTIVO");		
	public static final String UUOO_ESTADO_APLIC = propiedades.leePropiedad("UUOO_ESTADO_APLIC");

	//ESTADO DE REGISTRO
	public static final String ESTADO_ACTIVO = propiedades.leePropiedad("ESTADO_ACTIVO");
	public static final String ESTADO_INACTIVO = propiedades.leePropiedad("ESTADO_INACTIVO");
	
	//ESTADO DE RADIO
	public static final String RADIO1 = propiedades.leePropiedad("RADIO1");
	public static final String RADIO2 = propiedades.leePropiedad("RADIO2");
	public static final String RADIO3 = propiedades.leePropiedad("RADIO3");
	
	//SECUENCIAS
	public static final String SEQ_DOCUMENTO = propiedades.leePropiedad("SEQ_DOCUMENTO");
	public static final String SEQ_ASOCIACION_DOCUMENTO = propiedades.leePropiedad("SEQ_ASOCIACION_DOCUMENTO");
	public static final String SEQ_DOCUMENTO_URL = propiedades.leePropiedad("SEQ_DOCUMENTO_URL");
	public static final String SEQ_TIPO_DOCUMENTO = propiedades.leePropiedad("SEQ_TIPO_DOCUMENTO");
	public static final String SEQ_DUALIDAD = propiedades.leePropiedad("SEQ_DUALIDAD");
	public static final String SEQ_CLASIFICACION_ADUANERA = propiedades.leePropiedad("SEQ_CLASIFICACION_ADUANERA");
	public static final String SEQ_FE_DE_ERRATA = propiedades.leePropiedad("SEQ_FE_DE_ERRATA");
	public static final String SEQ_CLASIFICACION_DOCUMENTO = propiedades.leePropiedad("SEQ_CLASIFICACION_DOCUMENTO");
	public static final String SEQ_ASOCIACION_MATERIA_TEMA_SUBTEMA = propiedades.leePropiedad("SEQ_ASOCIACION_MATERIA_TEMA_SUBTEMA");
	public static final String SEQ_MATERIA_TEMA_SUBTEMA = propiedades.leePropiedad("SEQ_MATERIA_TEMA_SUBTEMA");
	public static final String SEQ_COMENTARIO = propiedades.leePropiedad("SEQ_COMENTARIO");
	public static final String SEQ_ACCESO_DOCUMENTO_TRABAJADOR = propiedades.leePropiedad("SEQ_ACCESO_DOCUMENTO_TRABAJADOR");
	public static final String SEQ_VISITA = propiedades.leePropiedad("SEQ_VISITA");
	public static final String SEQ_ARCHIVO_ADJUNTO = propiedades.leePropiedad("SEQ_ARCHIVO_ADJUNTO");
	public static final String SEQ_ACCESO_DOCUMENTO_UNIDAD_ORGANIZACIONAL = propiedades.leePropiedad("SEQ_ACCESO_DOCUMENTO_UNIDAD_ORGANIZACIONAL");
	public static final String SEQ_TERMINO_JURIDICO = propiedades.leePropiedad("SEQ_TERMINO_JURIDICO");
	public static final String SEQ_PARAM = propiedades.leePropiedad("SEQ_PARAM");
	
	//TIPO DE MATERIA TEMA SUBTEMA
	public static final String TIPO_MATERIA = propiedades.leePropiedad("TIPO_MATERIA");
	public static final String TIPO_TEMA = propiedades.leePropiedad("TIPO_TEMA");
	public static final String TIPO_SUBTEMA = propiedades.leePropiedad("TIPO_SUBTEMA");
	
	//IDENTIFICADOR EN T3920PARAM : MATERIA, TEMA y SUBTEMA
	
	public static final String PARAM_MATERIA = propiedades.leePropiedad("PARAM_MATERIA");
	public static final String PARAM_TEMA = propiedades.leePropiedad("PARAM_TEMA");
	public static final String PARAM_SUBTEMA = propiedades.leePropiedad("PARAM_SUBTEMA");
	
	//IDENTIFICADOR EN T3920 : SECTOR EMISOR,TIPO DE ASOCIACION, NIVEL DE ACCESO, TABLA
	
	public static final String IDPARAM_SECTOREMISOR = propiedades.leePropiedad("IDPARAM_SECTOREMISOR");
	public static final String IDPARAM_TIPOASOCIACION = propiedades.leePropiedad("IDPARAM_TIPOASOCIACION");
	public static final String IDPARAM_NIVELACCESO = propiedades.leePropiedad("IDPARAM_NIVELACCESO");
	public static final String IDPARAM_TABLA = propiedades.leePropiedad("IDPARAM_TABLA");
	public static final String IDPARAMMADRE_SECTOREMISOR = propiedades.leePropiedad("IDPARAMMADRE_SECTOREMISOR");
	public static final String IDPARAMMADRE_TIPOASOCIACION = propiedades.leePropiedad("IDPARAMMADRE_TIPOASOCIACION");
	public static final String IDPARAMMADRE_NIVELACCESO = propiedades.leePropiedad("IDPARAMMADRE_NIVELACCESO");
	public static final String IDPARAMMADRE_TABLA = propiedades.leePropiedad("IDPARAMMADRE_TABLA");
	public static final String DESC_SECTOREMISOR = propiedades.leePropiedad("DESC_SECTOREMISOR");
	public static final String DESC_TIPOASOCIACION = propiedades.leePropiedad("DESC_TIPOASOCIACION");
	public static final String DESC_NIVELACCESO = propiedades.leePropiedad("DESC_NIVELACCESO");
	public static final String DESC_TABLA = propiedades.leePropiedad("DESC_TABLA");
	public static final String IDPARAM_ESTADODOCUMENTO = propiedades.leePropiedad("IDPARAM_ESTADODOCUMENTO");
	public static final String IDPARAM_ESTADOREGISTRODOCUMENTO = propiedades.leePropiedad("IDPARAM_ESTADOREGISTRODOCUMENTO");
	public static final String IDPARAM_ESTADOREGISTROFEERRATAS = propiedades.leePropiedad("IDPARAM_ESTADOREGISTROFEERRATAS");
	public static final String IDPARAM_TIPODATOADUANERO = propiedades.leePropiedad("IDPARAM_TIPODATOADUANERO");
	
	//INDICADOR DE ELIMINACION
	public static String ELIMINACION_SI = propiedades.leePropiedad("ELIMINACION_SI");
	public static String ELIMINACION_NO = propiedades.leePropiedad("ELIMINACION_NO");
	
	//INDICADOR DE ACCION REGISTRO
	public static String INDICADOR_ACCION_REGISTRO_NUEVO = propiedades.leePropiedad("INDICADOR_ACCION_REGISTRO_NUEVO");
	public static String INDICADOR_ACCION_REGISTRO_ACTUAL = propiedades.leePropiedad("INDICADOR_ACCION_REGISTRO_ACTUAL");
	
	//ESTADO DE REGISTRO DEL DOCUMENTO
	public static String ESTREG_TODOS = propiedades.leePropiedad("ESTREG_TODOS");
	public static String ESTREG_INCOMPLETO = propiedades.leePropiedad("ESTREG_INCOMPLETO");
	public static String ESTREG_COMPLETO = propiedades.leePropiedad("ESTREG_COMPLETO");
	public static String ESTREG_DEVUELTO = propiedades.leePropiedad("ESTREG_DEVUELTO");
	public static String ESTREG_AUTORIZADO = propiedades.leePropiedad("ESTREG_AUTORIZADO");
	public static String ESTREG_ASOCIADO = propiedades.leePropiedad("ESTREG_ASOCIADO");
	
	//SSS 2011-02-15 inicio
	// TIPO VIGENCIA
	public static String TIPO_VIGENCIA_PARCIAL = propiedades.leePropiedad("TIPO_VIGENCIA_PARCIAL");
	public static String TIPO_VIGENCIA_TOTAL = propiedades.leePropiedad("TIPO_VIGENCIA_TOTAL");
	//SSS 2011-02-15 fin
	
	//	ESTADO DE REGISTRO DEL DOCUMENTO
	public static String ESTREGFE_COMPLETO = propiedades.leePropiedad("ESTREGFE_COMPLETO");
	public static String ESTREGFE_INCOMPLETO = propiedades.leePropiedad("ESTREGFE_INCOMPLETO");
			
	//ESTADO DEL DOCUMENTO
	public static String ESTDOC_VIGENTE = propiedades.leePropiedad("ESTDOC_VIGENTE");
	public static String ESTDOC_DEROGADO = propiedades.leePropiedad("ESTDOC_DEROGADO");
	public static String ESTDOC_SUSPENDIDO = propiedades.leePropiedad("ESTDOC_SUSPENDIDO");
	//SSS 2011-02-15 inicio
	public static String ESTDOC_NOVIGENTE = propiedades.leePropiedad("ESTDOC_NOVIGENTE");
	//SSS 2011-02-15 fin
	
	//ESTADO AUTORIZACION DE DOCUMENTO
	public static String DOCUMENTO_AUTORIZADO = propiedades.leePropiedad("DOCUMENTO_AUTORIZADO");
	public static String DOCUMENTO_NO_AUTORIZADO = propiedades.leePropiedad("DOCUMENTO_NO_AUTORIZADO");

	//ESTADO CHECK
	public static String CHECK_ACTIVO = propiedades.leePropiedad("CHECK_ACTIVO");
	public static String CHECK_INACTIVO = propiedades.leePropiedad("CHECK_INACTIVO");
	
	//TIPO DE LISTA
	public static String TIPO_LISTA_TRABAJADORES = propiedades.leePropiedad("TIPO_LISTA_TRABAJADORES");
	public static String TIPO_LISTA_UNIDADES_ORGANICAS = propiedades.leePropiedad("TIPO_LISTA_UNIDADES_ORGANICAS");

	//FORMATO DE FECHA
	public static String FORMATO_FECHA = propiedades.leePropiedad("FORMATO_FECHA");
	
	//NIVELES DE ACCESO A DOCUMENTOS
	public static String NIVEL1 = propiedades.leePropiedad("NIVEL1");
	public static String NIVEL2 = propiedades.leePropiedad("NIVEL2");
	public static String NIVEL3 = propiedades.leePropiedad("NIVEL3");
	public static String NIVEL4 = propiedades.leePropiedad("NIVEL4");
	public static String NIVEL5 = propiedades.leePropiedad("NIVEL5");
	
	//INDICADOR DE DATOS ADUANEROS
	public static String IDPARAM_DATOADUANERO = propiedades.leePropiedad("IDPARAM_DATOADUANERO");
	public static String IDPARAM_PARTIDA= propiedades.leePropiedad("IDPARAM_PARTIDA");
	public static String IDPARAM_PAISORIGEN= propiedades.leePropiedad("IDPARAM_PAISORIGEN");
	public static String IDPARAM_ADUANA= propiedades.leePropiedad("IDPARAM_ADUANA");
	public static String IDPARAM_DESCOMERCIAL= propiedades.leePropiedad("IDPARAM_DESCOMERCIAL");
	public static String IDPARAM_DESMINIMA= propiedades.leePropiedad("IDPARAM_DESMINIMA");
	public static String IDPARAM_MARCA = propiedades.leePropiedad("IDPARAM_MARCA");
	public static String IDPARAM_MODELO = propiedades.leePropiedad("IDPARAM_MODELO");
	public static String DES_PARTIDA=propiedades.leePropiedad("DES_PARTIDA");
	public static String DES_PAISORIGEN=propiedades.leePropiedad("DES_PAISORIGEN");
	public static String DES_ADUANA=propiedades.leePropiedad("DES_ADUANA");
	public static String DES_DESCOMERCIAL=propiedades.leePropiedad("DES_DESCOMERCIAL");
	public static String DES_DESMINIMA=propiedades.leePropiedad("DES_DESMINIMA");
	public static String DES_MARCA=propiedades.leePropiedad("DES_MARCA");
	public static String DES_MODELO=propiedades.leePropiedad("DES_MODELO");
	
	//GRUPOS DE TIPOS DE DOCUMENTO
	public static String VAL_GRUPO_TIPO_INFO_NORMAS_LEGALES = propiedades.leePropiedad("VAL_GRUPO_TIPO_INFO_NORMAS_LEGALES");
	public static String VAL_GRUPO_TIPO_INFO_DOCUMENTOS_INTERNOS = propiedades.leePropiedad("VAL_GRUPO_TIPO_INFO_DOCUMENTOS_INTERNOS");
	public static String VAL_GRUPO_TIPO_INFO_SENTENCIAS = propiedades.leePropiedad("VAL_GRUPO_TIPO_INFO_SENTENCIAS");
	public static String VAL_GRUPO_JURIDICO_JURISPRUDENCIA = propiedades.leePropiedad("VAL_GRUPO_JURIDICO_JURISPRUDENCIA");
	public static String VAL_GRUPO_JURIDICO_NORMA = propiedades.leePropiedad("VAL_GRUPO_JURIDICO_NORMA");
	public static String DESC_GRUPO_TIPO_INFO_NORMAS_LEGALES = propiedades.leePropiedad("DESC_GRUPO_TIPO_INFO_NORMAS_LEGALES");
	public static String DESC_GRUPO_TIPO_INFO_DOCUMENTOS_INTERNOS = propiedades.leePropiedad("DESC_GRUPO_TIPO_INFO_DOCUMENTOS_INTERNOS");
	public static String DESC_GRUPO_TIPO_INFO_SENTENCIAS = propiedades.leePropiedad("DESC_GRUPO_TIPO_INFO_SENTENCIAS");
	public static String DESC_GRUPO_JURIDICO_JURISPRUDENCIA = propiedades.leePropiedad("DESC_GRUPO_JURIDICO_JURISPRUDENCIA");
	public static String DESC_GRUPO_JURIDICO_DOCTRINA = propiedades.leePropiedad("DESC_GRUPO_JURIDICO_DOCTRINA");
	
	//TIPOS DE TEXTO DOCUMENTO
	public static String TIPO_TEXTO_DOC_PUB = propiedades.leePropiedad("TIPO_TEXTO_DOC_PUB");
	public static String TIPO_TEXTO_DOC_ACT = propiedades.leePropiedad("TIPO_TEXTO_DOC_ACT");
	public static String TIPO_TEXTO_DOC_FIN = propiedades.leePropiedad("TIPO_TEXTO_DOC_FIN");
	
	//RRHH
	public static String CODIGO_SECTOR_EMISOR_SUNAT = propiedades.leePropiedad("CODIGO_SECTOR_EMISOR_SUNAT");
	
	//ESTADO DE ACTUALIZACION DEL DOCUMENTO
	public static final String ESTACT_ACTUALIZADO = propiedades.leePropiedad("ESTACT_ACTUALIZADO");
	public static final String ESTACT_DESACTUALIZADO = propiedades.leePropiedad("ESTACT_DESACTUALIZADO");
	
	//ESTADO LEGISLACION PRINCIPAL
	public static final String ESTADO_LEGPRINCIPAL_ACTIVO = propiedades.leePropiedad("ESTADO_LEGPRINCIPAL_ACTIVO");
	public static final String ESTADO_LEGPRINCIPAL_INACTIVO = propiedades.leePropiedad("ESTADO_LEGPRINCIPAL_INACTIVO");
	
	//LEGISLACION DE USO FRECUENTE
	public static final String CANTIDAD_MESES_DEFAULT = propiedades.leePropiedad("CANTIDAD_MESES_DEFAULT");
	public static final String CANTIDAD_DE_LEGISLACIONES = propiedades.leePropiedad("CANTIDAD_DE_LEGISLACIONES");
	
	//ESTADO DE ACTUALIZACION DEL DOCUMENTO
	public static final String TIPO_ASOCIACION_AFECTADO = propiedades.leePropiedad("TIPO_ASOCIACION_AFECTADO");
	public static final String TIPO_ASOCIACION_AFECTANTE = propiedades.leePropiedad("TIPO_ASOCIACION_AFECTANTE");
	
	//TIPO DE AGRUPACION DE ASOCIACION DE DOCUMENTOS
	public static final String TIPO_GRPASOCIACION_ASO = propiedades.leePropiedad("TIPO_GRPASOCIACION_ASO");
	public static final String TIPO_GRPASOCIACION_DOC = propiedades.leePropiedad("TIPO_GRPASOCIACION_DOC");
	public static final String TIPO_GRPASOCIACION_URL = propiedades.leePropiedad("TIPO_GRPASOCIACION_URL");
	
	//BUSQUEDA DE DOCUMENTOS
	public static final String DGO_NORMA_LEGAL = VAL_GRUPO_TIPO_INFO_NORMAS_LEGALES;  
	public static final String DGO_DOCUMENTOS_INTERNOS = VAL_GRUPO_TIPO_INFO_DOCUMENTOS_INTERNOS;
	public static final String DGO_SENTENCIAS = VAL_GRUPO_TIPO_INFO_SENTENCIAS;
	
	public static final String DEO_POR_NORMA_LEGAL_DOC_INTERNO = propiedades.leePropiedad("DEO_POR_NORMA_LEGAL_DOC_INTERNO");
	public static final String DEO_POR_RANGO_FECHAS_PUBLICACION = propiedades.leePropiedad("DEO_POR_RANGO_FECHAS_PUBLICACION");
	public static final String DEO_POR_MATERIA_TEMA_SUB_TEMA_ASUNTO = propiedades.leePropiedad("DEO_POR_MATERIA_TEMA_SUB_TEMA_ASUNTO");
	public static final String DEO_POR_PALABRA_TERMINO = propiedades.leePropiedad("DEO_POR_PALABRA_TERMINO");
	public static final String DEO_POR_NORMATIVIDAD = propiedades.leePropiedad("DEO_POR_NORMATIVIDAD");
	public static final String DEO_POR_ESTADO_NORMA_DOC_INTERNO = propiedades.leePropiedad("DEO_POR_ESTADO_NORMA_DOC_INTERNO");
	public static final String DEO_POR_NRO_PROC_CALIDAD = propiedades.leePropiedad("DEO_POR_NRO_PROC_CALIDAD");
	public static final String DEO_POR_NORMA_RELACIONADA = propiedades.leePropiedad("DEO_POR_NORMA_RELACIONADA"); 
	public static final String DEO_POR_DATOS_ADUANEROS = propiedades.leePropiedad("DEO_POR_DATOS_ADUANEROS");
	
	//CONSTANTE PARA TAMAÑO DE ARCHIVO ADJUNTO EN BYTES 
	public static final String TAMANIO_ARCHIVO = propiedades.leePropiedad("TAMANIO_ARCHIVO");
	public static final String TAMANIO_TEXTO = propiedades.leePropiedad("TAMANIO_TEXTO");
	
	//CONSTANTE DE TIPO DE RELACION
	public static final String TIPO_CONCORDANCIA = propiedades.leePropiedad("TIPO_CONCORDANCIA");
	public static final String TIPO_DEROGA = propiedades.leePropiedad("TIPO_DEROGA");
	public static final String TIPO_DEROGA_PARTE = propiedades.leePropiedad("TIPO_DEROGA_PARTE");
	public static final String TIPO_MODIFICA = propiedades.leePropiedad("TIPO_MODIFICA");	
	public static final String TIPO_SUSPENDE = propiedades.leePropiedad("TIPO_SUSPENDE");
	public static final String TIPO_SUSPENDE_PARTE = propiedades.leePropiedad("TIPO_SUSPENDE_PARTE");
	public static final String TIPO_SUSTITUYE_PARTE = propiedades.leePropiedad("TIPO_SUSTITUYE_PARTE");
	public static final String TIPO_COMPLEMENTARIA = propiedades.leePropiedad("TIPO_COMPLEMENTARIA");
	public static final String TIPO_REGLAMENTARIA = propiedades.leePropiedad("TIPO_REGLAMENTARIA");

	//SSS 2011-04-18 inicio
	public static final String TIPO_DECLARA_INAPLICABLE = propiedades.leePropiedad("TIPO_DECLARA_INAPLICABLE");
	public static final String TIPO_INTEGRA = propiedades.leePropiedad("TIPO_INTEGRA");
	public static final String TIPO_ENTRADA_EN_VIGENCIA = propiedades.leePropiedad("TIPO_ENTRADA_EN_VIGENCIA");
	public static final String TIPO_PRORROGA = propiedades.leePropiedad("TIPO_PRORROGA");
	//SSS 2011-04-18 fin

	//USUARIO POR DEFECTO PARA VISITA DE DOCUMENTOS
	public static final String USUARIO_ITCONS = propiedades.leePropiedad("USUARIO_ITCONS");
	
	//DIRECTORIO DARA GUARDAR LOS INDICES DE LOS DOCUMENTOS
	public static final String INDEX_PATH = propiedades.leePropiedad("INDEX_PATH");
	
	//NUMERO MÁXIMO DE DOCUMENTOS A INDEXAR
	public static final String NUM_DOCS_INDEX = propiedades.leePropiedad("NUM_DOCS_INDEX");

	//URL SERVIDOR WEB
	//desarrollo: pc local del desarrollador
	public static final String SERVER_DESARROLLO = propiedades.leePropiedad("SERVER_DESARROLLO");
	
	//server: representa los servidores de desarrollo, calidad y producción sunat
	public static final String SERVER = propiedades.leePropiedad("SERVER");
	
	//server activo para aplicacion
	public static final String URL_SERVER_WEB = SERVER_DESARROLLO;
	
	//CONFIGURACION DE VISUALIZACION DE DUALIDADES Y COMENTARIOS DE CONSULTAS
	public static final String VISUALIZAR_DUALIDAD = propiedades.leePropiedad("VISUALIZAR_DUALIDAD");
	public static final String VISUALIZAR_COMENTARIO = propiedades.leePropiedad("VISUALIZAR_COMENTARIO");
	
	//TIEMPO DE EXPIRACION DE SESION
	public static final Integer TIEMPO_EXPIRACION = new Integer(propiedades.leePropiedad("TIEMPO_EXPIRACION"));
	
	//SSS 2011-03-17 inicio
	//CODIGO TIPO ASOCIACION DEROGA FORMA TOTAL
	public static final String CODIGO_TIPO_ASOC_SUST_EN_FORMA_TOTAL = propiedades.leePropiedad("CODIGO_TIPO_ASOC_SUST_EN_FORMA_TOTAL");
	//SSS 2011-03-17 fin
}

