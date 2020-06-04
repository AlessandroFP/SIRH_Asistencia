/*
 * Generated by XDoclet - Do not edit!
 */
package pe.gob.sunat.sp.asistencia.ejb;

/**
 * Local home interface for T130CMP.
 * @xdoclet-generated at 03-02-2020
 */
public interface T130CMPHome
   extends javax.ejb.EJBLocalHome
{
   public static final String COMP_NAME="java:comp/env/ejb/T130CMPLocal";
   public static final String JNDI_NAME="ejb/rrhh/cmp/sp/asistencia/t130cmp";

   public pe.gob.sunat.sp.asistencia.ejb.T130CMPLocal create(java.lang.String codPers , java.lang.String fAutor , java.lang.String hInic , java.lang.String uo , java.lang.String hFin , java.lang.String hSalida , java.lang.String obs , java.lang.String codJefe , java.lang.String estado , java.sql.Timestamp fCreacion , java.lang.String usuario)
      throws javax.ejb.CreateException;

   public pe.gob.sunat.sp.asistencia.ejb.T130CMPLocal create(java.lang.String codPers , java.lang.String fAutor , java.lang.String hInic)
      throws javax.ejb.CreateException;

   public pe.gob.sunat.sp.asistencia.ejb.T130CMPLocal findByPrimaryKey(pe.gob.sunat.sp.asistencia.ejb.T130CMPPK pk)
      throws javax.ejb.FinderException;

}