/*
 * Generated by XDoclet - Do not edit!
 */
package pe.gob.sunat.sp.asistencia.ejb;

/**
 * Primary key for T45CMP.
 * @xdoclet-generated at 03-02-2020
 */
public class T45CMPPK
   extends java.lang.Object
   implements java.io.Serializable
{

   public java.lang.String codTurno;

   public T45CMPPK()
   {
   }

   public T45CMPPK( java.lang.String codTurno )
   {
      this.codTurno = codTurno;
   }

   public java.lang.String getCodTurno()
   {
      return codTurno;
   }

   public void setCodTurno(java.lang.String codTurno)
   {
      this.codTurno = codTurno;
   }

   public int hashCode()
   {
      int _hashCode = 0;
         if (this.codTurno != null) _hashCode += this.codTurno.hashCode();

      return _hashCode;
   }

   public boolean equals(Object obj)
   {
      if( !(obj instanceof pe.gob.sunat.sp.asistencia.ejb.T45CMPPK) )
         return false;

      pe.gob.sunat.sp.asistencia.ejb.T45CMPPK pk = (pe.gob.sunat.sp.asistencia.ejb.T45CMPPK)obj;
      boolean eq = true;

      if( obj == null )
      {
         eq = false;
      }
      else
      {
         if( this.codTurno != null )
         {
            eq = eq && this.codTurno.equals( pk.getCodTurno() );
         }
         else  // this.codTurno == null
         {
            eq = eq && ( pk.getCodTurno() == null );
         }
      }

      return eq;
   }

   /** @return String representation of this pk in the form of [.field1.field2.field3]. */
   public String toString()
   {
      StringBuffer toStringValue = new StringBuffer("[.");
         toStringValue.append(this.codTurno).append('.');
      toStringValue.append(']');
      return toStringValue.toString();
   }

}
