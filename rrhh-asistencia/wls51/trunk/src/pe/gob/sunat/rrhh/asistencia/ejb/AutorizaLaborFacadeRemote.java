/*
 * Generated by XDoclet - Do not edit!
 */
package pe.gob.sunat.rrhh.asistencia.ejb;

/**
 * Remote interface for AutorizaLaborFacadeEJB.
 * @xdoclet-generado el 03-02-2020
 */
public interface AutorizaLaborFacadeRemote
   extends javax.ejb.EJBObject
{
   /**
    * PRAC-ASANCHEZ 26/08/2009 Inserta en la t3464 y actualiza la t130
    * @throws FacadeException    */
   public java.util.Map autorizarLE( java.util.Map datos,java.util.Map seguridad )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

   /**
    * Busca las dias con Labor Excepcional de un trabajador y si han sido autorizadas.
    * @throws DAOException
    * @throws RemoteException    */
   public java.util.Map buscarAutorizacion( java.util.Map datos,java.util.Map seguridad )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

   /**
    * FRD 01/07/2009 Busca Labor Excepcional de un trabajador y no han sido autorizadas.
    * @throws FacadeException    */
   public java.util.Map buscarLENoAutorizadas( java.util.Map datos,java.util.Map seguridad )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

   /**
    * Inserta un registro en la tabla t3464altalabor.
    * @throws DAOException
    * @throws RemoteException    */
   public java.util.Map registrarCompensacion( java.lang.String[] params,java.util.Map datos )
      throws pe.gob.sunat.framework.core.ejb.FacadeException, java.rmi.RemoteException;

}
