/*
 * Generated by XDoclet - Do not edit!
 */
package pe.gob.sunat.sp.asistencia.ejb;

/**
 * Local home interface for T1456CMP.
 * @xdoclet-generated at 03-02-2020
 */
public interface T1456CMPHome
   extends javax.ejb.EJBLocalHome
{
   public static final String COMP_NAME="java:comp/env/ejb/T1456CMPLocal";
   public static final String JNDI_NAME="ejb/rrhh/cmp/sp/asistencia/t1456cmp";

   public pe.gob.sunat.sp.asistencia.ejb.T1456CMPLocal create(java.lang.String codPers , java.sql.Date fecha)
      throws javax.ejb.CreateException;

   public pe.gob.sunat.sp.asistencia.ejb.T1456CMPLocal create(java.lang.String codPers , java.sql.Date fecha , java.lang.String usuario)
      throws javax.ejb.CreateException;

   public pe.gob.sunat.sp.asistencia.ejb.T1456CMPLocal findByPrimaryKey(pe.gob.sunat.sp.asistencia.ejb.T1456CMPPK pk)
      throws javax.ejb.FinderException;

}
