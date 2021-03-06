/*
 * Generated by XDoclet - Do not edit!
 */
package pe.gob.sunat.sp.asistencia.ejb;

/**
 * Remote interface for LicenciaFacadeEJB.
 * @xdoclet-generado el 03-02-2020
 */
public interface LicenciaFacadeRemote
   extends javax.ejb.EJBObject
{

   public java.lang.String registrarLicencia( java.util.HashMap datos,java.util.ArrayList trabajadores )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.lang.String modificarLicencia( java.lang.String dbpool,java.lang.String codPers,java.lang.String codUO,java.lang.String tipo,java.lang.String periodo,java.lang.String licencia,java.sql.Timestamp fIni,java.lang.String fechaIni,java.lang.String fechaFin,java.lang.String ano_ref,java.lang.String numero_ref,java.lang.String area_ref,java.lang.String obs,java.lang.String numero,java.lang.String certif,java.lang.String cmp,java.lang.String fechaCita,java.lang.String tipoEnfermedad,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.List buscarLicencias( java.lang.String dbpool,java.lang.String tipo,java.lang.String criterio,java.lang.String valor,java.util.HashMap seguridad )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.Map buscarLicenciaSolRef( java.lang.String dbpool,java.lang.String anno_ref,java.lang.String numero_ref,java.lang.String cod_pers,java.lang.String licencia )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.ArrayList eliminarLicencias( java.lang.String dbpool,java.lang.String[] params,java.util.ArrayList lista,java.util.ArrayList listaMensajes,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public pe.gob.sunat.sp.asistencia.bean.BeanCompensaOnom procesarOnomastico( java.lang.String dbpool,java.lang.String codPers,boolean compNoLaboral,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public void registrarLicenciaOnomastico( java.lang.String dbpool,java.lang.String codPers,java.lang.String fecha,java.lang.String usuario )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public boolean trabajadorTieneLicencia( java.lang.String dbpool,java.lang.String codPers,java.lang.String fecha1,java.lang.String fecha2 )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public float obtenerDiasAcumulados( java.lang.String dbpool,java.lang.String codPers,java.lang.String anno,java.lang.String licencia )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.Map agregaDatosLicenciaMedica( java.lang.String dbpool,java.util.HashMap licencia )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public pe.gob.sunat.sp.asistencia.bean.BeanCompensaOnom prepararCompOnom( java.lang.String dbpool,java.lang.String codPers )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

   public java.util.ArrayList validaLicenciaModif( java.util.HashMap mapa,java.util.HashMap tipoMov,boolean validaRango )
      throws pe.gob.sunat.sol.IncompleteConversationalState, java.rmi.RemoteException;

}
