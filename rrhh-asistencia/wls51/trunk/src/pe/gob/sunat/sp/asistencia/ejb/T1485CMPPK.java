/*
 * Generated by XDoclet - Do not edit!
 */
package pe.gob.sunat.sp.asistencia.ejb;

/**
 * Primary key for T1485CMP.
 * @xdoclet-generated at 03-02-2020
 */
public class T1485CMPPK
   extends java.lang.Object
   implements java.io.Serializable
{

   public java.lang.String uOrgan;
   public java.lang.String codPers;
   public java.lang.String operacion;

   public T1485CMPPK()
   {
   }

   public T1485CMPPK( java.lang.String UOrgan,java.lang.String codPers,java.lang.String operacion )
   {
      this.uOrgan = UOrgan;
      this.codPers = codPers;
      this.operacion = operacion;
   }

   public java.lang.String getUOrgan()
   {
      return uOrgan;
   }
   public java.lang.String getCodPers()
   {
      return codPers;
   }
   public java.lang.String getOperacion()
   {
      return operacion;
   }

   public void setUOrgan(java.lang.String uOrgan)
   {
      this.uOrgan = uOrgan;
   }
   public void setCodPers(java.lang.String codPers)
   {
      this.codPers = codPers;
   }
   public void setOperacion(java.lang.String operacion)
   {
      this.operacion = operacion;
   }

   public int hashCode()
   {
      int _hashCode = 0;
         if (this.uOrgan != null) _hashCode += this.uOrgan.hashCode();
         if (this.codPers != null) _hashCode += this.codPers.hashCode();
         if (this.operacion != null) _hashCode += this.operacion.hashCode();

      return _hashCode;
   }

   public boolean equals(Object obj)
   {
      if( !(obj instanceof pe.gob.sunat.sp.asistencia.ejb.T1485CMPPK) )
         return false;

      pe.gob.sunat.sp.asistencia.ejb.T1485CMPPK pk = (pe.gob.sunat.sp.asistencia.ejb.T1485CMPPK)obj;
      boolean eq = true;

      if( obj == null )
      {
         eq = false;
      }
      else
      {
         if( this.uOrgan != null )
         {
            eq = eq && this.uOrgan.equals( pk.getUOrgan() );
         }
         else  // this.uOrgan == null
         {
            eq = eq && ( pk.getUOrgan() == null );
         }
         if( this.codPers != null )
         {
            eq = eq && this.codPers.equals( pk.getCodPers() );
         }
         else  // this.codPers == null
         {
            eq = eq && ( pk.getCodPers() == null );
         }
         if( this.operacion != null )
         {
            eq = eq && this.operacion.equals( pk.getOperacion() );
         }
         else  // this.operacion == null
         {
            eq = eq && ( pk.getOperacion() == null );
         }
      }

      return eq;
   }

   /** @return String representation of this pk in the form of [.field1.field2.field3]. */
   public String toString()
   {
      StringBuffer toStringValue = new StringBuffer("[.");
         toStringValue.append(this.uOrgan).append('.');
         toStringValue.append(this.codPers).append('.');
         toStringValue.append(this.operacion).append('.');
      toStringValue.append(']');
      return toStringValue.toString();
   }

}
