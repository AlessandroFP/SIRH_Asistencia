/*
 * Generated by XDoclet - Do not edit!
 */
package pe.gob.sunat.rrhh.asistencia.ejb;

/**
 * Home interface for AutorizacionFacadeEJB.
 * @xdoclet-generado el 12-04-2011
 */
public interface AutorizacionFacadeHome
   extends javax.ejb.EJBHome
{
   public static final String COMP_NAME="java:comp/env/ejb/AutorizacionFacadeEJB";
   public static final String JNDI_NAME="ejb/facade/rrhh/asistencia/AutorizacionFacadeEJB";

   public pe.gob.sunat.rrhh.asistencia.ejb.AutorizacionFacadeRemote create()
      throws javax.ejb.CreateException,java.rmi.RemoteException;

}
