/*
 * Generated by XDoclet - Do not edit!
 */
package pe.gob.sunat.rrhh.asistencia.ejb;

/**
 * Remote interface for AutorizacionFacadeEJB.
 * @xdoclet-generado el 12-04-2011
 */
public interface AutorizacionFacadeRemote
   extends javax.ejb.EJBObject
{
   /**
    * Metodo encargado de procesar las autorizacion
    * @throws FacadeException
    */
   public void actualizarAutorizaciones(  )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

   /**
    * Metodo encargado de actualizar registros de la tablas T1962
    * @throws FacadeException
    */
   public void efectuarAutorizaciones( java.util.Map datos )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

}