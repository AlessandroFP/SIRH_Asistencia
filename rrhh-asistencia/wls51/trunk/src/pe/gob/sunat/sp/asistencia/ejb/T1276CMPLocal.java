/*
 * Generated by XDoclet - Do not edit!
 */
package pe.gob.sunat.sp.asistencia.ejb;

/**
 * Local interface for T1276CMP.
 * @xdoclet-generated at 03-02-2020
 */
public interface T1276CMPLocal
   extends javax.ejb.EJBLocalObject
{

   public void setPeriodo( java.lang.String periodo ) ;

   public void setFcierre( java.lang.String fcierre ) ;

   public void setFinicio( java.lang.String finicio ) ;

   public void setFfin( java.lang.String ffin ) ;

   public void setEstId( java.lang.String estId ) ;

   public void setFgraba( java.sql.Timestamp fgraba ) ;

   public void setCuser( java.lang.String cuser ) ;

   public java.lang.String getPeriodo(  ) ;

   public java.lang.String getFcierre(  ) ;

   public java.lang.String getFinicio(  ) ;

   public java.lang.String getFfin(  ) ;

   public java.lang.String getEstId(  ) ;

   public java.sql.Timestamp getFgraba(  ) ;

   public java.lang.String getCuser(  ) ;

}
