/*
 * Generated by XDoclet - Do not edit!
 */
package pe.gob.sunat.sp.asistencia.ejb;

/**
 * Primary key for T130CMP.
 * @xdoclet-generated at 03-02-2020
 */
public class T130CMPPK
   extends java.lang.Object
   implements java.io.Serializable
{

   public java.lang.String codPers;
   public java.lang.String fAutor;
   public java.lang.String hInic;

   public T130CMPPK()
   {
   }

   public T130CMPPK( java.lang.String codPers,java.lang.String FAutor,java.lang.String HInic )
   {
      this.codPers = codPers;
      this.fAutor = FAutor;
      this.hInic = HInic;
   }

   public java.lang.String getCodPers()
   {
      return codPers;
   }
   public java.lang.String getFAutor()
   {
      return fAutor;
   }
   public java.lang.String getHInic()
   {
      return hInic;
   }

   public void setCodPers(java.lang.String codPers)
   {
      this.codPers = codPers;
   }
   public void setFAutor(java.lang.String fAutor)
   {
      this.fAutor = fAutor;
   }
   public void setHInic(java.lang.String hInic)
   {
      this.hInic = hInic;
   }

   public int hashCode()
   {
      int _hashCode = 0;
         if (this.codPers != null) _hashCode += this.codPers.hashCode();
         if (this.fAutor != null) _hashCode += this.fAutor.hashCode();
         if (this.hInic != null) _hashCode += this.hInic.hashCode();

      return _hashCode;
   }

   public boolean equals(Object obj)
   {
      if( !(obj instanceof pe.gob.sunat.sp.asistencia.ejb.T130CMPPK) )
         return false;

      pe.gob.sunat.sp.asistencia.ejb.T130CMPPK pk = (pe.gob.sunat.sp.asistencia.ejb.T130CMPPK)obj;
      boolean eq = true;

      if( obj == null )
      {
         eq = false;
      }
      else
      {
         if( this.codPers != null )
         {
            eq = eq && this.codPers.equals( pk.getCodPers() );
         }
         else  // this.codPers == null
         {
            eq = eq && ( pk.getCodPers() == null );
         }
         if( this.fAutor != null )
         {
            eq = eq && this.fAutor.equals( pk.getFAutor() );
         }
         else  // this.fAutor == null
         {
            eq = eq && ( pk.getFAutor() == null );
         }
         if( this.hInic != null )
         {
            eq = eq && this.hInic.equals( pk.getHInic() );
         }
         else  // this.hInic == null
         {
            eq = eq && ( pk.getHInic() == null );
         }
      }

      return eq;
   }

   /** @return String representation of this pk in the form of [.field1.field2.field3]. */
   public String toString()
   {
      StringBuffer toStringValue = new StringBuffer("[.");
         toStringValue.append(this.codPers).append('.');
         toStringValue.append(this.fAutor).append('.');
         toStringValue.append(this.hInic).append('.');
      toStringValue.append(']');
      return toStringValue.toString();
   }

}
