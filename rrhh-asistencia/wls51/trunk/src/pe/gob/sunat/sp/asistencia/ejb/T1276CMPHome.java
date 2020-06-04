/*
 * Generated by XDoclet - Do not edit!
 */
package pe.gob.sunat.sp.asistencia.ejb;

/**
 * Local home interface for T1276CMP.
 * @xdoclet-generated at 03-02-2020
 */
public interface T1276CMPHome
   extends javax.ejb.EJBLocalHome
{
   public static final String COMP_NAME="java:comp/env/ejb/T1276CMPLocal";
   public static final String JNDI_NAME="ejb/rrhh/cmp/sp/asistencia/t1276cmp";

   public pe.gob.sunat.sp.asistencia.ejb.T1276CMPLocal create(java.lang.String periodo)
      throws javax.ejb.CreateException;

   public pe.gob.sunat.sp.asistencia.ejb.T1276CMPLocal create(java.lang.String periodo , java.lang.String finicio , java.lang.String ffin , java.lang.String fcierre , java.lang.String estId , java.sql.Timestamp fgraba , java.lang.String cuser)
      throws javax.ejb.CreateException;

   public pe.gob.sunat.sp.asistencia.ejb.T1276CMPLocal findByPrimaryKey(pe.gob.sunat.sp.asistencia.ejb.T1276CMPPK pk)
      throws javax.ejb.FinderException;

}