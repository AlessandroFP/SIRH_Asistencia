/*
 * Generated by XDoclet - Do not edit!
 */
package pe.gob.sunat.sp.asistencia.ejb;

/**
 * Remote interface for ReporteFacadeEJB.
 * @xdoclet-generado el 03-02-2020
 */
public interface ReporteFacadeRemote
   extends javax.ejb.EJBObject
{

   public java.util.ArrayList tipoLicencia( java.lang.String dbpool,java.lang.String tipo,java.lang.String fechaIni,java.lang.String fechaFin,java.lang.String criterio,java.lang.String valor,java.lang.String tipoQuiebre,java.util.HashMap seguridad )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.ArrayList acumuladoLicencia( java.lang.String dbpool,java.lang.String tipo,java.lang.String fechaIni,java.lang.String fechaFin,java.lang.String criterio,java.lang.String valor,java.lang.String tipoQuiebre,java.util.HashMap seguridad )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.ArrayList marcaciones( java.util.Map params )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

   public java.util.ArrayList marcacionesImpares( java.lang.String dbpool,java.lang.String fechaIni,java.lang.String fechaFin,java.lang.String criterio,java.lang.String valor,java.util.HashMap seguridad )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   /**
    * PRAC-ASANCHEZ 26/08/2009 Metodo encargado de devolver el reporte por persona de labor excepcional
    * @param params
    * @param seguridad
    * @return List
    * @throws RemoteException    */
   public java.util.List laborExcepcional( java.util.HashMap params,java.util.Map seguridad )
      throws java.rmi.RemoteException;

   /**
    * PRAC-ASANCHEZ 26/08/2009 Metodo encargado de devolver una lista de intervalos de las horas de permanencia de un trabajador en una fecha determinada y solo las autorizadas.
    * @param mapa
    * @return List
    * @throws RemoteException    */
   public java.util.List calcularListaMarcaciones( java.util.Map mapa )
      throws java.rmi.RemoteException;

   public java.util.ArrayList calificado( java.util.Map params,java.util.HashMap seguridad )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.ArrayList turnosTrabajo( java.util.HashMap params )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.ArrayList horaExtra( java.lang.String dbpool,java.lang.String fechaIni,java.lang.String fechaFin,java.lang.String criterio,java.lang.String valor,java.util.HashMap seguridad,java.lang.String indDia,java.lang.String indMin )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.ArrayList compensaciones( java.lang.String dbpool,java.lang.String fechaIni,java.lang.String fechaFin,java.lang.String criterio,java.lang.String valor,java.lang.String tQuiebre,java.util.HashMap seguridad )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   /**
    * Metodo encargado de obtener el reporte de Resumen Mensual
    * @param params
    * @return 
    * @throws RemoteException    */
   public java.util.List resumenMensual( java.util.Map params )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   /**
    * Metodo encargado de obtener el reporte de Resumen Diario
    * @param params
    * @return 
    * @throws RemoteException    */
   public java.util.List resumenDiario( java.util.Map params )
      throws java.rmi.RemoteException;

   public java.util.ArrayList vacacionesGozadas( java.lang.String dbpool,java.lang.String regimen,java.lang.String fechaIni,java.lang.String fechaFin,java.lang.String criterio,java.lang.String valor,java.lang.String mayorA,java.util.HashMap seguridad )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.ArrayList vacacionesPendientes( java.lang.String dbpool,java.lang.String regimen,java.lang.String anhoIni,java.lang.String anhoFin,java.lang.String criterio,java.lang.String valor,java.lang.String mayorA,java.util.HashMap seguridad )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.ArrayList vacacionesPendientesYProg( java.lang.String dbpool,java.lang.String anhoIni,java.lang.String anhoFin,java.lang.String criterio,java.lang.String valor,java.lang.String mayorA,java.util.HashMap seguridad )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.ArrayList vacacionesCompensadas( java.lang.String dbpool,java.lang.String fechaIni,java.lang.String fechaFin,java.lang.String criterio,java.lang.String valor,java.util.HashMap seguridad )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.ArrayList resumenVacacionesUOrg( java.lang.String dbpool,java.lang.String fechaIni,java.lang.String fechaFin,java.util.HashMap seguridad )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.ArrayList vacacionesGoceEfectivo( java.lang.String dbpool,java.lang.String fechaIni,java.lang.String fechaFin,java.lang.String criterio,java.lang.String valor,java.lang.String mayorA,java.util.HashMap seguridad )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   /**
    * Metodo encargado de listar las vacaciones de Goce Efectivo
    * @param Map parametros
    * @return List
    */
   public java.util.List vacacionesGoceEfectivo( java.util.Map parametros )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

   public java.util.ArrayList inasistencias( java.lang.String dbpool,java.lang.String regimen,java.lang.String criterio,java.lang.String valor,java.lang.String fechaIni,java.lang.String fechaFin,java.lang.String numDias,java.util.HashMap seguridad )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.ArrayList devolucionesDescuentos( java.lang.String dbpool,java.lang.String periodo,java.lang.String criterio,java.lang.String valor,java.util.HashMap seguridad )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.ArrayList estadisticoAnualVacaciones( java.lang.String dbpool,java.lang.String anhoIni,java.lang.String anhoFin,java.lang.String criterio,java.lang.String valor,java.util.HashMap seguridad )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.ArrayList personalNoGeneroSaldoVacacional( java.lang.String dbpool,java.lang.String fechaIni,java.lang.String fechaFin,java.lang.String criterio,java.lang.String valor,java.util.HashMap seguridad )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.ArrayList findDetalleSaldoVacacional( java.lang.String dbpool,java.lang.String numero )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.ArrayList buscarAnnosVacacionesPendientes( java.lang.String dbpool,java.lang.String anhoIni,java.lang.String anhoFin,java.lang.String criterio,java.lang.String valor,java.lang.String dias,java.util.HashMap seguridad )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public void masivoMarcaciones( java.lang.String dbpool,java.util.HashMap params,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public void masivoTurnos( java.lang.String dbpool,java.util.HashMap params,java.lang.String usuario,java.util.HashMap seguridad )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public void masivoInasistencias( java.lang.String dbpool,java.util.HashMap params,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public void masivoImpares( java.lang.String dbpool,java.util.HashMap params,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public void masivoPersonalSinTurno( java.lang.String dbpool,java.util.HashMap params,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public void masivoCalificado( java.lang.String dbpool,java.util.HashMap params,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public void masivoResumenMensual( java.lang.String dbpool,java.util.HashMap params,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public void masivoResumenDiario( java.lang.String dbpool,java.util.HashMap params,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public void masivoVacacionesPendientes( java.util.HashMap params,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public void masivoVacacionesProgramadas( java.util.Map params,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public void masivoVacacionesEfectivasPendientes( java.util.HashMap params,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public void masivoReportePorTipoLicencia( java.util.HashMap params )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public void masivoPapeletas( java.util.HashMap params )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public void masivoAcumuladoLicencia( java.util.HashMap params )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public void masivoVacaciones( java.util.HashMap params,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public void masivoVacacionesEfectuadas( java.util.HashMap params,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public void masivoVacacionesCompensadas( java.util.HashMap params,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.ArrayList sinTurno( java.lang.String dbpool,java.lang.String fechaIni,java.lang.String fechaFin,java.lang.String codUO,java.util.HashMap seguridad )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.ArrayList personalSinTurno( java.util.Map params,java.util.HashMap seguridad )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.ArrayList marcacionesPersonal( java.lang.String dbpool,java.lang.String fechaIni,java.lang.String fechaFin,java.lang.String criterio,java.lang.String valor,java.util.HashMap seguridad )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.ArrayList papeletas( java.util.HashMap datos,java.util.HashMap seguridad )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.ArrayList LicenciaMedica( java.lang.String dbpool,java.lang.String fechaIni,java.lang.String fechaFin,java.lang.String criterio,java.lang.String valor,java.lang.String tipoLic,java.lang.String dias,java.util.HashMap seguridad )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public void masivoLicenciaMedica( java.util.HashMap params,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public void masivoHoraExtra( java.lang.String dbpool,java.util.HashMap params,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.ArrayList detalleDiario( java.lang.String dbpool,java.lang.String fechaIni,java.lang.String fechaFin,java.lang.String criterio,java.lang.String valor,java.util.HashMap seguridad,java.lang.String mov )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public void masivoDetalleDiario( java.lang.String dbpool,java.util.HashMap params,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.List compensacionesBolsa( java.util.Map datos,java.util.Map seguridad )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   /**
    * PRAC-ASANCHEZ 28/08/2009 Metodo encargado de encolar el reporte de Labor Excepcional
    * @param dbpool
    * @param params
    * @param usuario
    * @return 
    * @throws RemoteException    */
   public void masivoLaborExcepcional( java.lang.String dbpool,java.util.HashMap params,java.lang.String usuario )
      throws java.rmi.RemoteException;

   /**
    * JVILLACORTA - 19/04/2011 - ALERTA DE SOLICITUDES
    */
   public java.util.ArrayList notificacionesDirectivos( java.util.Map params )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

   /**
    * JVILLACORTA - 20/04/2011 - ALERTA DE SOLICITUDES
    */
   public java.util.ArrayList buscaDetalleNotificaDirec( java.util.Map params )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

   /**
    * JVILLACORTA - 18/04/2011 - ALERTA DE SOLICITUDES
    */
   public java.util.ArrayList notificacionesTrabajadores( java.util.Map params )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

   /**
    * JVILLACORTA - 18/04/2011 - ALERTA DE SOLICITUDES
    */
   public void masivoNotificacionesDirectivos( java.lang.String dbpool,java.util.HashMap params,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   /**
    * JVILLACORTA - 20/04/2011 - ALERTA DE SOLICITUDES
    */
   public void masivoNotificacionesTrabajadores( java.lang.String dbpool,java.util.HashMap params,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   /**
    * Metodo que crea el archivo formato txt para reporte
    * @param nombreArchivo String
    * @param nombreReporte String
    * @param params Map
    * @param usuario String
    * @throws IncompleteConversationalState
    * @throws RemoteException    */
   public void creaReporte( java.lang.String nombreArchivo,java.lang.String nombreReporte,java.util.Map params,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   /**
    * Metodo que crea la cabecera del archivo formato txt para reporte
    * @param nombreReporte String
    * @param params Map
    * @param usuario String
    * @throws IncompleteConversationalState
    * @throws RemoteException    */
   public void cabeceraReporte( java.lang.String nombreReporte,java.util.Map params,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   /**
    * Metodo que escribe una linea en el archivo formato txt para reporte
    * @param textoImpresion String
    * @param nombreReporte String
    * @param params Map
    * @param usuario String    */
   public void escribe( java.lang.String textoImpresion,java.lang.String nombreReporte,java.util.Map params,java.lang.String usuario )
      throws java.rmi.RemoteException;

   /**
    * Metodo que finaliza o cierra el archivo formato txt para reporte
    * @param nombreArchivo String
    * @param params Map
    * @param usuario String
    * @return File
    * @throws IncompleteConversationalState
    * @throws RemoteException    */
   public java.io.File registraReporte( java.lang.String nombreArchivo,java.util.Map params,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   /**
    * Metodo que elimina los archivos temporales creados con formato .txt y .zip
    * @param nombreArchivo String
    * @throws IncompleteConversationalState
    * @throws RemoteException    */
   public void eliminaArchivosTemporales( java.lang.String nombreArchivo )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   /**
    * Metodo encargado de comprimir el archivo formato txt
    * @param ruta String
    * @param fileOrigen String
    * @param fileDestino String
    * @return boolean    */
   public boolean comprimeLog( java.lang.String ruta,java.lang.String fileOrigen,java.lang.String fileDestino )
      throws java.rmi.RemoteException;

   /**
    * Metodo encargado de la generacion del reporte de Trabajadores notificados para Goce Vacacional
    * @param dbpool String
    * @param fechaNotific String
    * @param fechaIniGoce String
    * @param criterio String
    * @param valor String
    * @param solicitud String
    * @return reporte List
    * @throws IncompleteConversationalState
    * @throws RemoteException    */
   public java.util.List notificacionesXVacacionesATrabajadores( java.lang.String dbpool,java.lang.String regimen,java.lang.String fechaNotific,java.lang.String fechaNotificFin,java.lang.String fechaIniGoce,java.lang.String fechaFinGoce,java.lang.String criterio,java.lang.String valor,java.lang.String solicitud,java.util.HashMap seguridad )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   /**
    * Metodo encargado de la generacion del reporte masivo de Trabajadores notificados para Goce Vacacional
    * @param dbpool String
    * @param params Map
    * @param usuario String
    * @throws IncompleteConversationalState
    * @throws RemoteException    */
   public void masivoNotificacionesXVacacionesATrabajadores( java.lang.String dbpool,java.util.Map params,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   /**
    * JVILLACORTA - 29/11/2011 - ALERTA DE SOLICITUDES
    */
   public java.util.ArrayList buscaDetalleNotificaTrab( java.util.Map params )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

   public java.util.ArrayList buscarPeriodosCerradosPorRegimen( java.lang.String dbpool,java.lang.String tipoRegimen,java.lang.String fechaInicio )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.ArrayList buscarPeriodosCerradosPorRegimenPorAnio( java.lang.String dbpool,java.lang.String tipoRegimen,java.lang.String anio )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public void masivoResumenMensualUUOO( java.lang.String dbpool,java.util.HashMap params,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   /**
    * Metodo encargado de obtener el reporte de gestion mensual por unidad organica
    * @param params
    * @return reporte List
    * @throws RemoteException    */
   public java.util.List resumenMensualUUOO( java.util.Map params )
      throws java.rmi.RemoteException;

   public void masivoResumenMensualColaborador( java.lang.String dbpool,java.util.HashMap params,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   /**
    * Metodo encargado de obtener el reporte de gestion mensual por colaborador
    * @param params
    * @return reporte List
    * @throws RemoteException    */
   public java.util.List resumenMensualColaborador( java.util.Map params )
      throws java.rmi.RemoteException;

   public void masivoResumenMensualMovimiento( java.lang.String dbpool,java.util.HashMap params,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   /**
    * Metodo encargado de obtener el reporte de gestion mensual por movimiento
    * @param params
    * @return reporte List
    * @throws RemoteException    */
   public java.util.List resumenMensualMovimiento( java.util.Map params )
      throws java.rmi.RemoteException;

   /**
    * Metodo encargado de obtener el reporte de gestion diario por movimiento
    * @param params
    * @return reporte List
    * @throws RemoteException    */
   public java.util.List resumenDiarioMovimiento( java.util.Map params )
      throws java.rmi.RemoteException;

   public void masivoResumenDiarioMovimiento( java.lang.String dbpool,java.util.HashMap params,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   /**
    * Metodo que redondea un numero float a un numero determinado de decimales. donde numDeci es el numero de decimales a redondear
    * @param numero float
    * @param numDeci float
    * @return numero float    */
   public float redondear( float numero,int numDeci )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   /**
    * Metodo encargado de obtener la cabecera del reporte de gestion mensual por unidad organica (opcion Buscar)
    * @param params
    * @return reporte List
    * @throws RemoteException    */
   public java.util.List resumenMensualUUOO_cabecera( java.util.Map params )
      throws java.rmi.RemoteException;

   /**
    * Metodo encargado de obtener el detalle del reporte de gestion mensual por unidad organica (opcion Buscar)
    * @param params
    * @return detalle Map
    * @throws RemoteException    */
   public java.util.Map resumenMensualUUOO_detalle( java.util.Map params )
      throws java.rmi.RemoteException;

   /**
    * Metodo encargado de obtener la cabecera del reporte de gestion mensual por movimientos (opcion Buscar)
    * @param params
    * @return reporte List
    * @throws RemoteException    */
   public java.util.List resumenMensualMovimiento_cabecera( java.util.Map params )
      throws java.rmi.RemoteException;

   /**
    * Metodo encargado de obtener el detalle del reporte de gestion mensual por movimientos (opcion Buscar)
    * @param params
    * @return detalle Map
    * @throws RemoteException    */
   public java.util.Map resumenMensualMovimiento_detalle( java.util.Map params )
      throws java.rmi.RemoteException;

   /**
    * Metodo encargado de obtener el reporte de gestion diario por movimiento
    * @param params
    * @return reporte List
    * @throws RemoteException    */
   public java.util.List resumenDiarioMovimiento_cabecera( java.util.Map params )
      throws java.rmi.RemoteException;

   /**
    * Metodo encargado de obtener el reporte de gestion diario por movimiento
    * @param params
    * @return detalle Map
    * @throws RemoteException    */
   public java.util.Map resumenDiarioMovimiento_detalle( java.util.Map params )
      throws java.rmi.RemoteException;

   /**
    * NVILLAR - 13/03/2012 - LABOR EXCEPCIONAL
    */
   public java.util.ArrayList consultarSolicitudesLE( java.util.Map params )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

   /**
    * NVILLAR - 02/05/2012 - LABOR EXCEPCIONAL
    */
   public java.util.ArrayList consultarSolicitudesLE2( java.util.Map params )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

   /**
    * NVILLAR - 19/03/2012 - LABOR EXCEPCIONAL
    */
   public java.util.ArrayList buscaDetalleConsultaSolicitudesLE( java.util.Map params )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

   /**
    * NVILLAR - 02/05/2012 - LABOR EXCEPCIONAL
    */
   public java.util.ArrayList buscaDetalleConsultaSolicitudesLE2( java.util.Map params )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

   /**
    * //INICIO - NVILLAR - 19/04/2012 - LABOR EXCEPCIONAL Metodo encargado de obtener el reporte de gestion mensual por unidad organica
    * @param params
    * @return reporte List
    * @throws RemoteException    */
   public java.util.ArrayList resumenSolLabExcepcional( java.util.Map params )
      throws java.rmi.RemoteException;

   /**
    * //INICIO - NVILLAR - 02/05/2012 - LABOR EXCEPCIONAL Metodo encargado de obtener el reporte de gestion mensual por unidad organica
    * @param params
    * @return reporte List
    * @throws RemoteException    */
   public java.util.ArrayList resumenSolLabExcepcional2( java.util.Map params )
      throws java.rmi.RemoteException;

   /**
    * INICIO - NVILLAR 23/04/2012 - LABOR EXCEPCIONAL Metodo encargado de encolar el reporte de Labor Excepcional
    * @param dbpool
    * @param params
    * @param usuario
    * @return 
    * @throws RemoteException    */
   public java.io.ByteArrayOutputStream masivoLaborExcepcional1( java.lang.String dbpool,java.util.HashMap params,java.lang.String usuario )
      throws java.rmi.RemoteException;

   /**
    * INICIO - NVILLAR 03/05/2012 - LABOR EXCEPCIONAL Metodo encargado de encolar el reporte de Labor Excepcional
    * @param dbpool
    * @param params
    * @param usuario
    * @return 
    * @throws RemoteException    */
   public void masivoLaborExcepcional2( java.lang.String dbpool,java.util.HashMap params,java.lang.String usuario )
      throws java.rmi.RemoteException;

   /**
    * NVILLAR - 22/03/2012 - LABOR EXCEPCIONAL
    */
   public java.util.ArrayList permanencias( java.util.Map params )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

   /**
    * NVILLAR - 03/05/2012 - LABOR EXCEPCIONAL
    */
   public java.util.ArrayList permanencias2( java.util.Map params )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

   /**
    * NVILLAR - 17/04/2012 - LABOR EXCEPCIONAL
    */
   public java.util.ArrayList buscaDetallePermanenciasLE( java.util.Map params )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

   /**
    * //INICIO - NVILLAR - 20/04/2012 - LABOR EXCEPCIONAL Metodo encargado de obtener el reporte de gestion mensual por unidad organica
    * @param params
    * @return reporte List
    * @throws RemoteException    */
   public java.util.ArrayList resumenPermanenciaExcepcional( java.util.Map params )
      throws java.rmi.RemoteException;

   /**
    * //INICIO - NVILLAR - 04/05/2012 - LABOR EXCEPCIONAL Metodo encargado de obtener el reporte de gestion mensual por unidad organica
    * @param params
    * @return reporte List
    * @throws RemoteException    */
   public java.util.ArrayList resumenPermanenciaExcepcional2( java.util.Map params )
      throws java.rmi.RemoteException;

   /**
    * INICIO - NVILLAR 25/04/2012 - LABOR EXCEPCIONAL Metodo encargado de encolar el reporte de Labor Excepcional
    * @param dbpool
    * @param params
    * @param usuario
    * @return 
    * @throws RemoteException    */
   public void masivoConsultaPermanencia( java.lang.String dbpool,java.util.HashMap params,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   /**
    * INICIO - NVILLAR 04/05/2012 - LABOR EXCEPCIONAL Metodo encargado de encolar el reporte de Labor Excepcional
    * @param dbpool
    * @param params
    * @param usuario
    * @return 
    * @throws RemoteException    */
   public void masivoConsultaPermanencia2( java.lang.String dbpool,java.util.HashMap params,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   /**
    * NVILLAR - 16/04/2012 - LABOR EXCEPCIONAL
    */
   public java.util.ArrayList consultaHorasAutNoAutComp( java.util.Map params )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

   /**
    * NVILLAR - 24/04/2012 - LABOR EXCEPCIONAL
    */
   public void masivoConsultaHorasAutNoAutComp( java.lang.String dbpool,java.util.HashMap params,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   /**
    * NVILLAR - LABOR EXCEPCIONAL Genera una cadena que contiene una tabla html con los datos del Trabajador Solicitante
    * @param dbean DynaBean
    * @param hm HashMap
    * @return 
    * @throws FacadeException    */
   public java.lang.String buscarNombCompleto( java.lang.String dbpool_sp,java.lang.String codigo )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

   /**
    * Metodo encargado de obtener el codigo de unidad organica restando los ceros empezando desde el final hasta el caracter que no sea cero
    * @param String unidad
    * @return String uoJefe
    * @throws FacadeException    */
   public java.lang.String findUuooJefe( java.lang.String unidad )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

   public java.util.ArrayList vacacionesGozadasMatrimonio( java.lang.String dbpool,java.lang.String regimen,java.lang.String fechaIni,java.lang.String fechaFin,java.lang.String criterio,java.lang.String valor,java.util.HashMap seguridad )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public void masivoVacacionesGozadasMatrimonio( java.util.HashMap params,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.ArrayList autorizacionesClimaLaboral( java.lang.String dbpool,java.lang.String regimen,java.lang.String fechaIni,java.lang.String fechaFin,java.lang.String criterio,java.lang.String valor,java.util.HashMap seguridad )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public void masivoAutorizacionesClimaLaboral( java.util.HashMap params,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   /**
    * Genera el reporte de directivos notificados que no programaron vacaciones de trabajadores
    * @param dbpool String
    * @param fechaNotific String
    * @param fechaNotificFin String
    * @param criterio String
    * @param valor String
    * @return reporte List
    * @throws IncompleteConversationalState
    * @throws RemoteException    */
   public java.util.ArrayList notificaDirectivosNoVacaciones( java.lang.String dbpool,java.lang.String fechaNotific,java.lang.String fechaNotificFin,java.lang.String criterio,java.lang.String valor,java.util.HashMap seguridad )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public void masivoNotificaDirectivosNoVacaciones( java.lang.String dbpool,java.util.HashMap params,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   /**
    * Genera el reporte de directivos notificados que no programaron vacaciones de trabajadores
    * @param dbpool String
    * @param regimen String
    * @param fechaCorte String
    * @param estado String
    * @param criterio String
    * @param valor String
    * @return reporte List
    * @throws IncompleteConversationalState
    * @throws RemoteException    */
   public java.util.ArrayList vacacionesTruncas( java.lang.String dbpool,java.util.HashMap datos )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public void masivoVacacionesTruncas( java.lang.String dbpool,java.util.HashMap params,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

}
