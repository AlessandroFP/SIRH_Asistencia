/*
 * Generated by XDoclet - Do not edit!
 */
package pe.gob.sunat.sp.asistencia.ejb;

/**
 * Local home interface for T1281CMP.
 * @xdoclet-generated at 03-02-2020
 */
public interface T1281CMPHome
   extends javax.ejb.EJBLocalHome
{
   public static final String COMP_NAME="java:comp/env/ejb/T1281CMPLocal";
   public static final String JNDI_NAME="ejb/rrhh/cmp/sp/asistencia/t1281cmp";

   public pe.gob.sunat.sp.asistencia.ejb.T1281CMPLocal create(java.lang.String codPers , java.lang.String anno)
      throws javax.ejb.CreateException;

   public pe.gob.sunat.sp.asistencia.ejb.T1281CMPLocal findByPrimaryKey(pe.gob.sunat.sp.asistencia.ejb.T1281CMPPK pk)
      throws javax.ejb.FinderException;

}