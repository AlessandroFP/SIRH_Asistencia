/*
 * Generated by XDoclet - Do not edit!
 */
package pe.gob.sunat.rrhh.programacion.ejb;

/**
 * Remote interface for ProgramacionFacadeEJB.
 * @xdoclet-generado el 03-02-2020
 */
public interface ProgramacionFacadeRemote
   extends javax.ejb.EJBObject
{
   /**
    * M�todo encargado de obtener los datos personales y de programacion de vacaciones del codigo de registro ingresado
    * @param datos Map
    * @return Map
    * @throws FacadeException
    */
   public java.util.Map obtenerUsuProgramacion( java.util.Map datos )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

   /**
    * Metodo encargado de grabar las programaciones de vacacion ingresadas (llama a otro que hace por periodo).
    * @param datos Map
    * @return void
    * @throws FacadeException
    */
   public java.lang.String grabarProgVacaciones( java.util.Map datos )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

   /**
    * Mtodo encargado de agregar una programacion de vacaciones
    * @param params Map
    * @return Map
    * @throws FacadeException
    */
   public java.util.Map agregarProgramacion( java.util.Map params )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

   /**
    * Mtodo encargado de eliminar las programaciones de la lista.
    * @param datos Map
    * @return void
    * @throws FacadeException
    */
   public java.util.List eliminarProgramacion( java.util.Map datos )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

   /**
    * Mtodo encargado de generar el reporte de trabajadores que faltan programar sus vacaciones
    * @param datos Map
    * @return List
    * @throws FacadeException
    */
   public java.util.List generarRepFaltantesProg( java.util.Map datos )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

   /**
    * Mtodo encargado de obtener la programacion
    * @param params Map
    * @return List
    * @throws FacadeException
    */
   public java.util.List cargarProgramacion( java.util.Map params )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

   /**
    * M�todo encargado de generar el reporte Vacaciones Programadas
    * @param datos Map
    * @return List
    * @throws FacadeException
    */
   public java.util.List generarRepVacProgramadas( java.util.Map datos )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

   /**
    * Mtodo encargado de grabar la Reprogramacion de Vacaciones
    * @param datos Map
    * @return void
    * @throws FacadeException
    */
   public void grabarReProgVacaciones( java.util.Map datos )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

   /**
    * M�todo encargado de buscar
    * @param datos Map
    * @return List
    * @throws FacadeException
    */
   public java.util.List joinWithT02T12( java.util.Map datos )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

   /**
    * metodo buscar: que se encarga de buscar parametros como es categoria y unidad organizacional
    * @param params Map, contiene los criterios de b�squeda.
    * @return List, retorna el resultado de la b�squeda
    * @throws FacadeException
    */
   public java.util.List buscarUOeIntendencia( java.util.Map params )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

   /**
    * metodo retornaSaldo: que se encarga de retornar el saldo de un anno vacacional para una persona
    * @param List listaSaldos
    * @return String, retorna el resultado de la bsqueda
    * @throws FacadeException
    */
   public java.lang.String retornaSaldo( java.util.List listaSaldos,java.lang.String codPersona,java.lang.String annovac )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

}
