/*
 * Generated by XDoclet - Do not edit!
 */
package pe.gob.sunat.sp.asistencia.ejb;

/**
 * Home interface for ArchivoFacadeEJB.
 * @xdoclet-generado el 03-02-2020
 */
public interface ArchivoFacadeHome
   extends javax.ejb.EJBHome
{
   public static final String COMP_NAME="java:comp/env/ejb/ArchivoFacadeEJB";
   public static final String JNDI_NAME="ejb/rrhh/facade/sp/asistencia/ArchivoFacadeEJB";

   public pe.gob.sunat.sp.asistencia.ejb.ArchivoFacadeRemote create()
      throws javax.ejb.CreateException,java.rmi.RemoteException;

}
