/*
 * Generated by XDoclet - Do not edit!
 */
package pe.gob.sunat.sp.asistencia.ejb;

/**
 * Home interface for MantenimientoFacadeEJB.
 * @xdoclet-generado el 05-03-2020
 */
public interface MantenimientoFacadeHome
   extends javax.ejb.EJBHome
{
   public static final String COMP_NAME="java:comp/env/ejb/MantenimientoFacadeEJB";
   public static final String JNDI_NAME="ejb/rrhh/facade/sp/asistencia/MantenimientoFacadeEJB";

   public pe.gob.sunat.sp.asistencia.ejb.MantenimientoFacadeRemote create()
      throws javax.ejb.CreateException,java.rmi.RemoteException;

}
