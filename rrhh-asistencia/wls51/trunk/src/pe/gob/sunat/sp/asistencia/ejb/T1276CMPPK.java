/*
 * Generated by XDoclet - Do not edit!
 */
package pe.gob.sunat.sp.asistencia.ejb;

/**
 * Primary key for T1276CMP.
 * @xdoclet-generated at 03-02-2020
 */
public class T1276CMPPK
   extends java.lang.Object
   implements java.io.Serializable
{

   public java.lang.String periodo;

   public T1276CMPPK()
   {
   }

   public T1276CMPPK( java.lang.String periodo )
   {
      this.periodo = periodo;
   }

   public java.lang.String getPeriodo()
   {
      return periodo;
   }

   public void setPeriodo(java.lang.String periodo)
   {
      this.periodo = periodo;
   }

   public int hashCode()
   {
      int _hashCode = 0;
         if (this.periodo != null) _hashCode += this.periodo.hashCode();

      return _hashCode;
   }

   public boolean equals(Object obj)
   {
      if( !(obj instanceof pe.gob.sunat.sp.asistencia.ejb.T1276CMPPK) )
         return false;

      pe.gob.sunat.sp.asistencia.ejb.T1276CMPPK pk = (pe.gob.sunat.sp.asistencia.ejb.T1276CMPPK)obj;
      boolean eq = true;

      if( obj == null )
      {
         eq = false;
      }
      else
      {
         if( this.periodo != null )
         {
            eq = eq && this.periodo.equals( pk.getPeriodo() );
         }
         else  // this.periodo == null
         {
            eq = eq && ( pk.getPeriodo() == null );
         }
      }

      return eq;
   }

   /** @return String representation of this pk in the form of [.field1.field2.field3]. */
   public String toString()
   {
      StringBuffer toStringValue = new StringBuffer("[.");
         toStringValue.append(this.periodo).append('.');
      toStringValue.append(']');
      return toStringValue.toString();
   }

}
