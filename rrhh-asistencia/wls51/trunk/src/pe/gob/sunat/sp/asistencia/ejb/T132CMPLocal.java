/*
 * Generated by XDoclet - Do not edit!
 */
package pe.gob.sunat.sp.asistencia.ejb;

/**
 * Local interface for T132CMP.
 * @xdoclet-generated at 03-02-2020
 */
public interface T132CMPLocal
   extends javax.ejb.EJBLocalObject
{

   public void setCodPers( java.lang.String codPers ) ;

   public void setTAcum( java.math.BigDecimal tAcum ) ;

   public void setEstId( java.lang.String estId ) ;

   public void setFcreacion( java.sql.Timestamp fcreacion ) ;

   public void setCodUser( java.lang.String codUser ) ;

   public void setFmod( java.sql.Timestamp fmod ) ;

   public void setCuserMod( java.lang.String cuserMod ) ;

   public java.lang.String getCodPers(  ) ;

   public java.math.BigDecimal getTAcum(  ) ;

   public java.lang.String getEstId(  ) ;

   public java.sql.Timestamp getFcreacion(  ) ;

   public java.lang.String getCodUser(  ) ;

   public java.sql.Timestamp getFmod(  ) ;

   public java.lang.String getCuserMod(  ) ;

}
