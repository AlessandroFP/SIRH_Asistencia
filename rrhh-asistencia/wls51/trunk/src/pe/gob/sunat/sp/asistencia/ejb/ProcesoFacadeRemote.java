/*
 * Generated by XDoclet - Do not edit!
 */
package pe.gob.sunat.sp.asistencia.ejb;

/**
 * Remote interface for ProcesoFacadeEJB.
 * @xdoclet-generado el 03-02-2020
 */
public interface ProcesoFacadeRemote
   extends javax.ejb.EJBObject
{
   /**
    * Metodo que se encarga de ejecutar el proceso masivo de asistencia
    * @param params
    * @param usuario
    * @return 
    * @throws RemoteException    */
   public boolean procesarAsistencia( java.util.HashMap params,java.lang.String usuario )
      throws java.rmi.RemoteException;

   /**
    * Metodo que se encarga de ejecutar el proceso de asistencia para un trabajador
    */
   public boolean procesarAsistenciaTrabajador( java.lang.String dbpool,java.lang.String codPers,java.lang.String codUO,java.lang.String periodo,java.lang.String fechaIni,java.lang.String fechaFin,java.util.ArrayList movimientos,java.util.HashMap acumulados,java.util.HashMap acumPeriodo,java.lang.String usuario,java.lang.String indPap )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   /**
    * Metodo encargado de ejecutar el proceso de calificaciones para una determinada fecha
    */
   public boolean procesarCalificacion( java.util.HashMap mapa,java.lang.String usuario )
      throws java.rmi.RemoteException;

   public boolean procesarMarcaciones( java.util.HashMap mapa,java.lang.String usuario )
      throws java.rmi.RemoteException;

   public boolean procesarMarcacionesDBF( java.util.HashMap mapa,java.lang.String usuario )
      throws java.rmi.RemoteException;

   public boolean generarRegistroAsistencia( java.util.HashMap mapa,java.lang.String usuario )
      throws java.rmi.RemoteException;

   /**
    * Metodo encargado del registro de la asistencia de una trabajador para un determinado dia
    */
   public void generarAsistenciaTrabajador( java.util.HashMap mapa,java.util.HashMap beanPersona,java.util.HashMap fechas,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public void registrarCompensaciones( java.util.Map datos )
      throws java.rmi.RemoteException;

   public java.util.ArrayList dameMarcacionesDia( java.lang.String dbpool,java.lang.String codPers,java.lang.String fecha,pe.gob.sunat.sp.asistencia.bean.BeanTurnoTrabajo turno )
      throws java.rmi.RemoteException;

   public void eliminarMarcacionesImpares( java.util.Map params )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

   public java.lang.String preCalificarEntrada( java.lang.String fechaMarca,java.lang.String horaMarca,java.lang.String horaIni,int tolera,java.lang.String horaLim,java.lang.String medida )
      throws java.rmi.RemoteException;

   public java.lang.String preCalificarSalida( java.lang.String dbpool,java.lang.String codPers,java.lang.String fechaMarca,java.lang.String horaMarca,java.lang.String horaFin,java.lang.String medida )
      throws java.rmi.RemoteException;

   public java.lang.String preCalificarMovimiento( java.lang.String fechaMarca,java.lang.String horaIni,java.lang.String horaFin,pe.gob.sunat.sp.asistencia.bean.BeanTurnoTrabajo turno,java.lang.String medida,boolean tieneRefrigerio,boolean bOperativo )
      throws java.rmi.RemoteException;

   public boolean generarDataReloj( java.util.HashMap mapa,java.lang.String usuario )
      throws java.rmi.RemoteException;

   public java.lang.String registrarAutorizacionTrabajador( java.lang.String dbpool,java.lang.String codPers,java.lang.String codUO,java.lang.String fecha,java.lang.String horaIni,java.lang.String horaFin,java.lang.String obs,java.lang.String codJefe,java.lang.String usuario )
      throws java.rmi.RemoteException;

   public void registrarAutorizacionHE( java.util.HashMap mapa,java.lang.String usuario )
      throws java.rmi.RemoteException;

   public java.util.HashMap calcularHorasPermanencia( java.lang.String dbpool,java.lang.String codpers,pe.gob.sunat.sp.asistencia.bean.BeanHoraExtra he )
      throws java.rmi.RemoteException;

   /**
    * Acumula la labor excepcional de los trabajadores
    * @param mapa Map
    * @return 
    * @throws FacadeException    */
   public java.lang.String acumularHETrabajador( java.util.Map mapa )
      throws java.rmi.RemoteException;

   public java.lang.String acumularHE( java.util.HashMap mapa,java.lang.String usuario )
      throws java.rmi.RemoteException;

   /**
    * Metodo encargado de invocar al proceso de Asistencia para los cambios de turno de un trabajador
    * @param mapa
    * @return 
    * @throws RemoteException    */
   public void generarRegAsistenciaPorCambioTurnoTrab( java.util.Map mdatos )
      throws java.rmi.RemoteException;

   /**
    * Metodo encargado de registrar una novedad al ocurrir cambios en el turno de un trabajador
    * @param mapa
    * @return 
    * @throws RemoteException    */
   public void registrarNovedadPorCambioTurnoTrab( java.util.Map mdatos )
      throws java.rmi.RemoteException;

   public java.lang.String registrarTurnoSinCruce( java.util.Map mapa )
      throws java.rmi.RemoteException;

   public java.lang.String registrarTurnoTrabajo( java.util.HashMap mapa,java.lang.String usuario )
      throws java.rmi.RemoteException;

   public java.lang.String procesarOperativoMasivo( java.util.HashMap mapa,java.lang.String usuario )
      throws java.rmi.RemoteException;

   public void procesarOperativoTrabajador( java.util.HashMap mapa,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public boolean verificaCruceTurnosTrabajador( java.util.ArrayList turnos,java.lang.String turno,java.sql.Timestamp fIni,java.sql.Timestamp fFin,boolean modificar )
      throws java.rmi.RemoteException;

   public java.lang.String generarVacaciones( java.util.HashMap mapa,java.lang.String usuario )
      throws java.rmi.RemoteException;

   public java.lang.String registrarTurnoConCruce( java.util.Map mapa )
      throws java.rmi.RemoteException;

   /**
    * Metodo encargado de obtener la lista de procesos
    * @param dbpool
    * @param codPers
    * @return 
    * @throws RemoteException    */
   public java.util.ArrayList cargarLogProcesos( java.lang.String dbpool,java.lang.String codPers )
      throws java.rmi.RemoteException;

   /**
    * Metodo encargado de descargar el log de un proceso
    * @param dbpool
    * @param id
    * @return 
    * @throws RemoteException    */
   public java.io.InputStream descargarLogProceso( java.lang.String dbpool,java.lang.String id )
      throws java.rmi.RemoteException;

   public void registrarLicencia( java.util.HashMap mapa,java.lang.String usuario )
      throws java.rmi.RemoteException;

   /**
    * Metodo encargado de registrar la Licencia de un trabajador
    * @param datos
    * @return 
    * @throws RemoteException    */
   public java.lang.String registrarLicenciaTrabajador( java.util.HashMap datos )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   /**
    * Metodo encargado de realizar el cierre de Asistencia
    * @param params
    * @param usuario
    * @return 
    * @throws RemoteException    */
   public void cerrarAsistencia( java.util.HashMap params,java.lang.String usuario )
      throws java.rmi.RemoteException;

   /**
    * genera una cadena que contiene una tabla html con los datos
    * @param dbean DynaBean
    * @param hm HashMap
    * @return 
    * @throws FacadeException    */
   public java.lang.String generaTablaDatos( java.util.List lista )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

   /**
    * Metodo que se encarga de ejecutar el proceso de asistencia para un trabajador CAS
    * @param mapaPrin
    * @return 
    * @throws RemoteException    */
   public boolean procesarAsistenciaTrabajadorCAS( java.util.Map mapaPrin )
      throws java.rmi.RemoteException;

   /**
    * Metodo encargado del registro de la asistencia de una trabajador CAS para un determinado dia
    * @param params
    * @return 
    * @throws RemoteException    */
   public void generarAsistenciaTrabajadorCAS( java.util.Map params )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   /**
    * Metodo encargado de la Planificacion de Turnos Masivos
    * @param mapa HashMap
    * @param usuario String
    * @return String
    * @throws RemoteException    */
   public java.lang.String planificarTurnosMasivos( java.util.HashMap mapa,java.lang.String usuario )
      throws java.rmi.RemoteException;

   /**
    * Metodo encargado de la Validacion del Archivo de Turnos Masivos
    * @param mapa HashMap
    * @param usuario String
    * @return String
    * @throws RemoteException    */
   public java.lang.String validarArchivoCsvTurnos( java.util.HashMap mapa,java.lang.String usuario )
      throws java.rmi.RemoteException;

   /**
    * Metodo encargado de la Carga o Registro del Archivo de Turnos Masivos
    * @param mapa HashMap
    * @param usuario String
    * @return String
    * @throws RemoteException    */
   public java.lang.String cargarTurnosMasivos( java.util.HashMap mapa,java.lang.String usuario )
      throws java.rmi.RemoteException;

   /**
    * Metodo encargado de la Carga o Registro del Turno Individual de una Persona
    * @param mapa HashMap
    * @param usuario String
    * @return String
    * @throws RemoteException    */
   public java.lang.String registrarTurnoTrabajoMasivo( java.util.HashMap mapa,java.lang.String usuario )
      throws java.rmi.RemoteException;

   /**
    * Metodo registrarTurnoSinCruceMasivo Individual
    * @param mapa Mapa
    * @return String
    * @throws RemoteException    */
   public java.lang.String registrarTurnoSinCruceMasivo( java.util.Map mapa )
      throws java.rmi.RemoteException;

   /**
    * Metodo registrarTurnoConCruceMasivo Individual
    * @param mapa Mapa
    * @return String
    * @throws RemoteException    */
   public java.lang.String registrarTurnoConCruceMasivo( java.util.Map mapa )
      throws java.rmi.RemoteException;

   /**
    * Metodo encargado del registro de la asistencia de Modalidad Formativa para un determinado dia
    * @param params
    * @return 
    * @throws RemoteException    */
   public void generarAsistenciaTrabajadorModFormativa( java.util.Map params )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   /**
    * Metodo que se encarga de ejecutar el proceso de asistencia para Modalidades Formativas
    * @param mapaPrin
    * @return 
    * @throws RemoteException    */
   public boolean procesarAsistenciaTrabModFormativa( java.util.Map mapaPrin )
      throws java.rmi.RemoteException;

   /**
    * Metodo encargado de procesar la acumulaci?e horas de labor excepcional por grupo de colaboradores
    * @param mapa HashMap
    * @param usuario String
    * @return res boolean
    */
   public boolean procesarLaborExcepcional_interfaz( java.util.HashMap mapa,java.lang.String usuario )
      throws java.rmi.RemoteException;

   /**
    * Metodo encargado de procesar la acumulaci?e horas de labor excepcional por grupo de colaboradores
    * @param mapa HashMap
    * @param usuario String
    * @return res boolean
    */
   public boolean procesarLaborExcepcional( java.util.HashMap mapa,java.lang.String usuario )
      throws java.rmi.RemoteException;

   /**
    * Metodo encargado de procesar la acumulacion horas de labor excepcional de cada colaborador segun rango de fechas
    * @param mapa Map
    * @return res boolean
    * @throws FacadeException    */
   public boolean procesarLaborExcepcionalTrabajador( java.util.Map mapa )
      throws java.rmi.RemoteException;

   /**
    * Metodo que devuelve un Mapa de Listas con los intervalos de horas de compensacion con estado autorizado, no autorizado o rechazado
    */
   public java.util.Map calcularIntervalosHorasCompensacion( java.lang.String dbpool,java.lang.String registro,java.lang.String uuooRegistro,java.lang.String fechaEval,java.util.Map rangoExtra )
      throws java.rmi.RemoteException;

   /**
    * Metodo que devuelve una Lista con los nuevos intervalos de horas de compensacion con estado no autorizado
    */
   public java.util.List calcularIntervalosNoAutorizados( java.lang.String horIniPermisoSol,java.lang.String horFinPermisoSol,java.util.Map mapaNoAut )
      throws java.rmi.RemoteException;

   /**
    * Metodo que devuelve una Lista con el total de nuevos intervalos de horas de compensacion con estado no autorizado
    */
   public java.util.List calcularTotalIntervalosNoAutorizados( java.util.Map mapaNoAut,java.util.List listInterSolic )
      throws java.rmi.RemoteException;

   /**
    * Genera una cadena que contiene una tabla html con los intervalos de compensacion autorizados para un rango de fechas de un colaborador
    * @param lista List
    * @return String
    * @throws FacadeException    */
   public java.lang.String generaTablaLaborAutorizada( java.util.List lista )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

   /**
    * Metodo que devuelve el texto en html para el correo a Trabajadores por movimientos de asistencia
    * @param params Map
    * @param nombre String
    * @param mensaje String
    * @return String
    * @throws FacadeException    */
   public java.lang.String textoCorreoLaborExcepcional( java.util.Map params,java.lang.String nombre,java.lang.String mensaje )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

   /**
    * Metodo que devuelve los aprobadores de la solicitud autogenerada de labor excepcional
    * @param mapa Map
    * @return aprobadores Map
    */
   public java.util.Map findAprobadoresSolicitudLaborExcep( java.lang.String dbpool,java.util.Map mapa )
      throws java.rmi.RemoteException;

   /**
    * Metodo encargado de validar si cumple requisitos para la acumulacion de horas de labor excepcional de cada colaborador para una fecha
    * @param mapa Map
    * @return res boolean
    * @throws FacadeException    */
   public boolean validarLaborExcepcional( java.util.Map mapa )
      throws java.rmi.RemoteException;

   /**
    * Metodo encargado de acumular intervalos de labor excepcional autorizada, no autorizada y rechazada para una fecha
    * @param mapa Map
    * @return labor Map
    * @throws FacadeException    */
   public java.util.Map acumularLaborExcepcional( java.util.Map mapa )
      throws java.rmi.RemoteException;

   /**
    * Metodo encargado de acumular intervalos de labor excepcional (despues de hora de salida) autorizada, no autorizada y rechazada para una fecha
    * @param mapa Map
    * @return labor Map
    * @throws FacadeException    */
   public java.util.Map acumular2LaborExcepcional( java.util.Map mapa )
      throws java.rmi.RemoteException;

   /**
    * Metodo encargado de insertar labor excepcional no autorizada para una fecha
    * @param mapa Map
    * @return laborNoAut Map
    * @throws FacadeException    */
   public java.util.Map registrarLaborNoAutorizada( java.util.Map mapa )
      throws java.rmi.RemoteException;

   /**
    * Metodo encargado de insertar labor excepcional autorizada para una fecha
    * @param mapa Map
    * @return laborAut Map
    * @throws FacadeException    */
   public java.util.Map registrarLaborAutorizada( java.util.Map mapa )
      throws java.rmi.RemoteException;

   /**
    * Metodo encargado de insertar labor excepcional rechazada para una fecha
    * @param mapa Map
    * @throws FacadeException    */
   public void registrarLaborRechazada( java.util.Map mapa )
      throws java.rmi.RemoteException;

   /**
    * Metodo encargado de autogenerar solicitud de labor excepcional para una fecha
    * @param mapa Map
    * @throws FacadeException    */
   public boolean autogenerarSolicitudLabor( java.util.Map mapa )
      throws java.rmi.RemoteException;

   /**
    * Metodo encargado de enviar correo al colaborador con intervalos de labor autorizada para un rango de fechas o una fecha
    * @param mapa Map
    * @param res boolean
    * @throws FacadeException    */
   public boolean enviarCorreoLaborAutorizada( java.util.Map mapa )
      throws java.rmi.RemoteException;

   /**
    * Calcular rangos de permanencia para administtrativos y operativos con turnos de trabajo de 1 dia (hfin>hinicio) segun marcaciones
    */
   public java.util.HashMap calcularRangosPermanenciaExtra( java.lang.String dbpool,java.lang.String codpers,pe.gob.sunat.sp.asistencia.bean.BeanHoraExtra he,java.lang.String HoraIni,java.lang.String HoraFin )
      throws java.rmi.RemoteException;

   /**
    * Calcular rangos de permanencia para operativos con turnos de trabajo de 2 dias (hfin<=hinicio) segun marcaciones por dia de trabajo
    */
   public java.util.HashMap calcularRangosPermanenciaExtraOperativoEntrada( java.lang.String dbpool,java.lang.String codpers,pe.gob.sunat.sp.asistencia.bean.BeanHoraExtra he,java.lang.String HoraIni,java.lang.String HoraFin )
      throws java.rmi.RemoteException;

   /**
    * Calcular rangos de permanencia para operativos con turnos de trabajo de 2 dias (hfin<=hinicio) segun marcaciones por dia de trabajo
    */
   public java.util.HashMap calcularRangosPermanenciaExtraOperativoSalida( java.lang.String dbpool,java.lang.String codpers,pe.gob.sunat.sp.asistencia.bean.BeanHoraExtra he,java.lang.String HoraIni,java.lang.String HoraFin )
      throws java.rmi.RemoteException;

   /**
    * Calcular rangos de permanencia para operativos con turnos de trabajo de 2 dias (hfin<=hinicio) segun marcaciones por dia de trabajo
    */
   public java.util.HashMap calcularRangosPermanenciaExtraOperativoIntermedio( java.lang.String dbpool,java.lang.String codpers,pe.gob.sunat.sp.asistencia.bean.BeanHoraExtra he,java.lang.String HoraIni,java.lang.String HoraFin )
      throws java.rmi.RemoteException;

   /**
    * Metodo encargado de registrar masivamente los flujos de aprobadores.
    * @param mapa HashMap
    * @param usuario String
    * @return String
    * @throws RemoteException    */
   public java.lang.String planificarRegistroFlujoAprobadoresMasivo( java.util.HashMap mapa,java.lang.String usuario )
      throws java.rmi.RemoteException;

   /**
    * Metodo encargado de la Validacion del Archivo de Flujo de Aprobadores Masivos
    * @param mapa HashMap
    * @param usuario String
    * @return String
    * @throws RemoteException    */
   public java.lang.String registroFlujoAprobadoresMasivo( java.util.HashMap mapa,java.lang.String usuario )
      throws java.rmi.RemoteException;

   public java.lang.String preCalificarEntradaOpe2( java.lang.String fechaMarca,java.lang.String horaMarca,java.lang.String horaIni,int tolera,java.lang.String horaLim,java.lang.String medida )
      throws java.rmi.RemoteException;

   public java.lang.String preCalificarSalidaOpe1( java.lang.String dbpool,java.lang.String codPers,java.lang.String fechaMarca,java.lang.String horaMarca,java.lang.String horaFinTurno,java.lang.String medida )
      throws java.rmi.RemoteException;

   public java.lang.String preCalificarMovimientoOpe1( java.lang.String fechaMarca,java.lang.String horaIni,java.lang.String horaFin,pe.gob.sunat.sp.asistencia.bean.BeanTurnoTrabajo turno,java.lang.String medida,boolean tieneRefrigerio,boolean bOperativo )
      throws java.rmi.RemoteException;

   public java.lang.String preCalificarMovimientoOpe2( java.lang.String fechaMarca,java.lang.String horaIni,java.lang.String horaFin,pe.gob.sunat.sp.asistencia.bean.BeanTurnoTrabajo turno,java.lang.String medida,boolean tieneRefrigerio,boolean bOperativo,boolean procesaClima,int minutosClima )
      throws java.rmi.RemoteException;

   /**
    * Calificar asistencia cuando SI tiene turno (dia w:x-1) de 2 dias y SI tiene turno dia x de 2 d� (marcaciones PARES requiere para dia x)
    * @return califica Map
    * @throws FacadeException    */
   public java.util.Map preCalificarMovimientoOpe3( java.lang.String fechaMarca,java.lang.String horaIni,java.lang.String horaFin,pe.gob.sunat.sp.asistencia.bean.BeanTurnoTrabajo turnoW,pe.gob.sunat.sp.asistencia.bean.BeanTurnoTrabajo turnoX,java.lang.String medida,boolean tieneRefrigerioW,boolean tieneRefrigerioX,boolean bOperativoW,boolean bOperativoX,boolean procesaClima,int minutosClima )
      throws java.rmi.RemoteException;

   /**
    * Calificar asistencia (caso: SES) cuando SI turno (dia x-1) de 2 dias y SI turno dia x de 1 d�(marcaciones IMPARES requiere para dia x)
    * @return califica Map
    * @throws FacadeException    */
   public java.util.Map preCalificarMovimientoOpe4( java.lang.String fechaMarca,java.lang.String horaIni,java.lang.String horaFin,pe.gob.sunat.sp.asistencia.bean.BeanTurnoTrabajo turnoW,pe.gob.sunat.sp.asistencia.bean.BeanTurnoTrabajo turnoX,java.lang.String medida,boolean tieneRefrigerioW,boolean tieneRefrigerioX,boolean bOperativoW,boolean bOperativoX,boolean procesaClima,int minutosClima )
      throws java.rmi.RemoteException;

   /**
    * Metodo que devuelve el texto en html para el envio de correo de procesamiento por clima laboral
    * @param params Map
    * @param nombre String
    * @param mensaje String
    * @return String
    * @throws FacadeException    */
   public java.lang.String textoCorreoClimaLaboral( java.util.Map params,java.lang.String nombre,java.lang.String mensaje )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

}