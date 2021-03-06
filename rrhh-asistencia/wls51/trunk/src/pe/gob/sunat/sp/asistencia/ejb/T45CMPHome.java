/*
 * Generated by XDoclet - Do not edit!
 */
package pe.gob.sunat.sp.asistencia.ejb;

/**
 * Local home interface for T45CMP.
 * @xdoclet-generated at 03-02-2020
 */
public interface T45CMPHome
   extends javax.ejb.EJBLocalHome
{
   public static final String COMP_NAME="java:comp/env/ejb/T45CMPLocal";
   public static final String JNDI_NAME="ejb/rrhh/cmp/sp/asistencia/t45cmp";

   public pe.gob.sunat.sp.asistencia.ejb.T45CMPLocal create(java.lang.String codTurno)
      throws javax.ejb.CreateException;

   public pe.gob.sunat.sp.asistencia.ejb.T45CMPLocal create(java.lang.String codTurno , java.lang.String descripcion , java.lang.String fechaIni , java.lang.String horaIni , java.lang.String fechaFin , java.lang.String horaFin , java.lang.String estId , java.lang.String dias , java.lang.String tolerancia , java.lang.String horaLimite , java.lang.String operId , java.sql.Timestamp fgraba , java.lang.String refrIni , java.lang.String refrFin , java.lang.String refrMin , java.lang.String cuser , java.lang.String controlID)
      throws javax.ejb.CreateException;

   public pe.gob.sunat.sp.asistencia.ejb.T45CMPLocal findByPrimaryKey(pe.gob.sunat.sp.asistencia.ejb.T45CMPPK pk)
      throws javax.ejb.FinderException;

}
