/*
 * Generated by XDoclet - Do not edit!
 */
package pe.gob.sunat.sp.asistencia.ejb;

/**
 * Remote interface for VacacionFacadeEJB.
 * @xdoclet-generado el 03-02-2020
 */
public interface VacacionFacadeRemote
   extends javax.ejb.EJBObject
{

   public java.lang.String firmarLibro( java.lang.String dbpool,java.lang.String codPers,java.lang.String[] params,java.util.ArrayList detalle,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.lang.String venderVacaciones( java.lang.String dbpool,java.lang.String codPers,java.lang.String anno,java.lang.String dias,java.lang.String usuario,java.lang.String annoRef,java.lang.String areaRef,java.lang.String numeroRef,java.lang.String obs )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   /**
    * Metodo encargado de actualizar el saldo ,actualizar el estado de la Programacion e insertar la venta
    * @param param Map
    * @return String
    * @throws FacadeException
    */
   public java.lang.String venderVacaciones( java.util.Map param )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

   public void registrarNvaVacacion( java.lang.String dbpool,java.lang.String codPers,java.lang.String licencia,java.sql.Timestamp fechaIni,java.lang.Integer dias,java.lang.String anio,java.sql.Timestamp fechaFin,java.lang.String observacion,java.lang.String estado,java.lang.String usuario,java.lang.String annoRef,java.lang.String areaRef,java.lang.String numeroRef,java.util.HashMap hmPermitidos,java.util.HashMap seguridad,boolean adelanto )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public void registrarVacacion( java.lang.String dbpool,java.lang.String codPers,java.lang.String licencia,java.sql.Timestamp fechaIni,java.lang.String anhoVac,java.lang.Integer dias,java.sql.Timestamp fechaFin,java.lang.String observacion,java.lang.String estado,java.lang.String usuario,java.lang.String annoRef,java.lang.String areaRef,java.lang.String numeroRef )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public void firmarVacacion( java.lang.String dbpool,java.lang.String numero,java.lang.String codPers,java.lang.String anho,java.lang.String periodo,java.lang.String licencia,java.sql.Timestamp fechaIni,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public void modificarVacacion( java.lang.String dbpool,java.lang.String numero,java.lang.String codPers,java.lang.String periodo,java.lang.String licencia,java.sql.Timestamp fechaIni,java.sql.Timestamp nvaFechaIni,java.lang.Integer dias,java.lang.String anio,java.sql.Timestamp nvaFechaFin,java.lang.String observacion,java.lang.String usuario,java.util.HashMap hmPermitidos,java.util.HashMap seguridad )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public void suspenderVacacion( java.lang.String dbpool,java.lang.String numero,java.lang.String codPers,java.lang.String periodo,java.lang.String licencia,java.sql.Timestamp fechaIni,java.lang.String anhoRef,java.lang.String areaRef,java.lang.String numRef,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public void registrarVacacionSuspendida( java.lang.String dbpool,java.lang.String codPers,java.lang.String licencia,java.sql.Timestamp fechaIni,java.lang.String anhoVac,java.lang.Integer dias,java.sql.Timestamp fechaFin,java.lang.String observacion,java.lang.String anhoRef,java.lang.String areaRef,java.lang.String numRef,java.lang.String usuario,java.lang.String uOrg )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public void eliminarVacacion( java.lang.String dbpool,java.lang.String numero,java.lang.String codPers,java.lang.String periodo,java.lang.String licencia,java.sql.Timestamp fechaIni,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public boolean esCantidadAceptada( java.lang.String dbpool,java.lang.String codPers,java.lang.String anho,java.util.HashMap hmReferencia,int cantAsignada,java.lang.String fechaIni )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.ArrayList buscarSaldos( java.util.HashMap mapa )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.List buscarVacacionesXLicencia( java.util.HashMap mapa )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.List buscarVacacionesEfecPend( java.util.HashMap mapa )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.List buscarVacacionesEfecGoz( java.util.HashMap mapa )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.HashMap buscarVacacionesGen( java.lang.String dbpool,java.lang.String codPers )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public void registrarVacacionesGen( java.util.HashMap mapa )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.List buscarVacacionesPorFirmar( java.lang.String dbpool,java.lang.String codPers,java.lang.String anno,java.lang.String estado )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.List buscarVacacionesPorAnnoEstadoLicencia( java.lang.String dbpool,java.lang.String codPers,java.lang.String anno,java.lang.String estado,java.lang.String tipo )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.ArrayList buscarSaldoVenta( java.lang.String dbpool,java.lang.String codPers )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.ArrayList buscarSaldoAnno( java.lang.String dbpool,java.lang.String codPers,boolean saldoFavor,java.lang.String anno,java.util.HashMap seguridad )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.List buscarDetalleVacaciones( java.lang.String dbpool,java.lang.String codPers,java.lang.String estado,java.lang.String licencia,java.lang.String anno,java.util.HashMap seguridad )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public boolean programacionValida( java.lang.String dbpool,java.lang.String codPers,java.lang.String anho,java.sql.Timestamp fechaIni,int dias )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public void modificarVacacionProgramada( java.lang.String dbpool,java.lang.String numero,java.lang.String codPers,java.lang.String periodo,java.sql.Timestamp fechaIni,java.sql.Timestamp nvaFechaIni,java.lang.Integer dias,java.sql.Timestamp nvaFechaFin,java.lang.String observacion,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.List listaVentas( java.util.HashMap mapa )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.List buscarPendientes( java.util.HashMap mapa )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public int totalDiasVendidosPorAnno( java.util.HashMap mapa )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public int totalFraccionadoPorAnio( java.util.HashMap mapa )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public int totalVentasEnSol( java.util.HashMap mapa )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public int totalReprogEnSol( java.util.HashMap mapa )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public int numDiasDisponibleAdelanto( java.util.HashMap mapa )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.ArrayList buscarSaldoGeneralPorAnno( java.lang.String dbpool,java.lang.String codPers,boolean saldoFavor,java.lang.String anhoIni,java.lang.String anhoFin )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   /**
    * Metodo que Inserta las Vacaciones Efectivas y Actualiza la Programacion de Vacaciones
    */
   public java.util.List convertirEfectiva( java.lang.String dbpool,java.lang.String numero,java.lang.String codPers,java.lang.String periodo,java.sql.Timestamp fechaIni,java.lang.String annoRef,java.lang.String areaRef,java.lang.String numeroRef,java.lang.String usuario,java.util.Map seguridad )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   /**
    * Metodo que Inserta las Vacaciones Efectivas y Actualiza la Programacion de Vacaciones
    */
   public java.util.List convertirEfectivaAdelanto( java.lang.String dbpool,java.lang.String numero,java.lang.String codPers,java.lang.String periodo,java.sql.Timestamp fechaIni,java.lang.String annoRef,java.lang.String areaRef,java.lang.String numeroRef,java.lang.String usuario,java.lang.String esAdelanto,java.util.Map seguridad )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   /**
    * Metodo que Inserta las Vacaciones Efectivas por Matrimonio Actualiza la Programacion de Vacaciones.
    */
   public java.util.List convertirEfectivaMatrimonio( java.lang.String dbpool,java.util.HashMap solicitud,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.List buscarVacacionesTipoAnho( java.util.HashMap mapa )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public void calificarVacProg( java.lang.String dbpool,java.lang.String[] params,java.util.ArrayList lista,java.lang.String tipoaut,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public void generarVacaciones( java.util.HashMap mapa,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.ArrayList reprogramarVacacion( java.util.HashMap datos )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.ArrayList postergarVacacion( java.util.HashMap datos )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   /**
    * Devuelve una lista que contiene las vacaciones programadas de un trabajador
    * @param mapa Map
    * @return Map EBV 11/03/2009 se cambia a MAPA
    * @throws FacadeException    */
   public java.util.Map buscarProgramacionTrabajador( java.util.Map mapa )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

   /**
    * Devuelve una lista que contiene las vacaciones vencidas y no gozadas de un trabajador
    * @param datos Map
    * @return resul List
    * @throws FacadeException    */
   public java.util.List validarVacacionesVencidas( java.util.Map datos )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

   /**
    * Genera La consulta de Vacaciones
    */
   public java.util.Map generarConsultaVacaciones( java.util.HashMap paramSaldos,java.util.HashMap paramVta,java.util.HashMap paramVac,java.util.HashMap paramProg )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.Map buscarVacacionPorSolRef( java.lang.String dbpool,java.lang.String anno_ref,java.lang.String numero_ref,java.lang.String cod_pers,java.lang.String mov,java.lang.String estado )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

}