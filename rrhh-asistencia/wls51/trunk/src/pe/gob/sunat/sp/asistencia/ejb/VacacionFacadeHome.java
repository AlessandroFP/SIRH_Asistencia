/*
 * Generated by XDoclet - Do not edit!
 */
package pe.gob.sunat.sp.asistencia.ejb;

/**
 * Home interface for VacacionFacadeEJB.
 * @xdoclet-generado el 03-02-2020
 */
public interface VacacionFacadeHome
   extends javax.ejb.EJBHome
{
   public static final String COMP_NAME="java:comp/env/ejb/VacacionFacadeEJB";
   public static final String JNDI_NAME="ejb/rrhh/facade/sp/asistencia/VacacionFacadeEJB";

   public pe.gob.sunat.sp.asistencia.ejb.VacacionFacadeRemote create()
      throws javax.ejb.CreateException,java.rmi.RemoteException;

}
