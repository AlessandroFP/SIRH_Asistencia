/*
 * Generated by XDoclet - Do not edit!
 */
package pe.gob.sunat.sp.asistencia.ejb;

/**
 * Local interface for T1275CMP.
 * @xdoclet-generated at 03-02-2020
 */
public interface T1275CMPLocal
   extends javax.ejb.EJBLocalObject
{

   public void setCodPers( java.lang.String codPers ) ;

   public void setFecha( java.sql.Date fecha ) ;

   public void setHora( java.lang.String hora ) ;

   public void setReloj( java.lang.String reloj ) ;

   public void setFcreacion( java.sql.Timestamp fcreacion ) ;

   public void setCuserCrea( java.lang.String cuserCrea ) ;

   public void setFmod( java.sql.Timestamp fmod ) ;

   public void setCuserMod( java.lang.String cuserMod ) ;

   public void setCodPase( java.lang.String codPase ) ;

   public void setEstado( java.lang.String estado ) ;

   public java.lang.String getCodPers(  ) ;

   public java.sql.Date getFecha(  ) ;

   public java.lang.String getHora(  ) ;

   public java.lang.String getReloj(  ) ;

   public java.sql.Timestamp getFcreacion(  ) ;

   public java.lang.String getCuserCrea(  ) ;

   public java.sql.Timestamp getFmod(  ) ;

   public java.lang.String getCuserMod(  ) ;

   public java.lang.String getCodPase(  ) ;

   public java.lang.String getEstado(  ) ;

}
