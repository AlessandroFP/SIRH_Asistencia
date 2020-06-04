package pe.gob.sunat.sp.asistencia.ejb.delegate;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pe.gob.sunat.framework.core.pattern.ServiceLocator;
import pe.gob.sunat.sol.BeanMensaje;
import pe.gob.sunat.sol.IncompleteConversationalState;
import pe.gob.sunat.sp.asistencia.bean.BeanCompensaOnom;
import pe.gob.sunat.sp.asistencia.bean.BeanLicencia;
import pe.gob.sunat.sp.asistencia.ejb.LicenciaFacadeHome;
import pe.gob.sunat.sp.asistencia.ejb.LicenciaFacadeRemote;

/**
 * <p>Title: AsistenciaDelegate</p>
 * <p>Description: Clase encargada de administrar las invocaciones para las funcionalidades
 * del modulo de licencias</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Sunat</p>
 * @author cgarratt
 * @version 1.0
 */
public class LicenciaDelegate {

  private LicenciaFacadeRemote licencia;

  public LicenciaDelegate() throws LicenciaException {
    try {
      LicenciaFacadeHome facadeHome = (LicenciaFacadeHome) ServiceLocator.
          getInstance().getRemoteHome(LicenciaFacadeHome.JNDI_NAME,
                                   LicenciaFacadeHome.class);
      licencia = facadeHome.create();
    }
    catch (Exception e) {
      BeanMensaje beanM = new BeanMensaje();
      beanM.setError(true);
      beanM.setMensajeerror(e.getMessage());
      beanM.setMensajesol("Por favor intente nuevamente.");
      throw new LicenciaException(beanM);
    }
  }

  /**
   * Metodo que verifica si un trabajador esta de licencia en un rango determinado de fechas
   * @param codPers Registro del trabajador
   * @param fecha1 Fecha Inicio
   * @param fecha2 Fecha Fin
   * @return boolean
   * @throws AsistenciaException
   */

  public boolean trabajadorTieneLicencia(String dbpool, String codPers,
                                         String fecha1, String fecha2) throws
      LicenciaException {

    boolean res = false;
    try {

      res = licencia.trabajadorTieneLicencia(dbpool, codPers, fecha1, fecha2);

    }
    catch (IncompleteConversationalState e) {
      throw new LicenciaException(e.getBeanMensaje());
    }
    catch (RemoteException e) {
      BeanMensaje beanM = new BeanMensaje();
      if (e.detail.getClass().getName().equals(
          "pe.gob.sunat.sol.IncompleteConversationalState")) {
        beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
      }
      else {
        beanM.setError(true);
        beanM.setMensajeerror(
            "Ha ocurrido un error al verificar si un trabajador posee licencia.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new LicenciaException(beanM);
    }

    return res;
  }

  /**
   * Metodo que devuelve la cantidad de dias de licencia en un determinado a�o
   * @param codPers Registro del trabajador
   * @param anno A�o a verificar
   * @param licencia Tipo de Licencia
   * @return float
   * @throws AsistenciaException
   */
  public float obtenerDiasAcumulados(String dbpool, String codPers, String anno,
                                     String lic) throws LicenciaException {

    float res = 0;
    try {
      res = licencia.obtenerDiasAcumulados(dbpool, codPers, anno, lic);
    }
    catch (IncompleteConversationalState e) {
      throw new LicenciaException(e.getBeanMensaje());
    }
    catch (RemoteException e) {
      BeanMensaje beanM = new BeanMensaje();
      if (e.detail.getClass().getName().equals(
          "pe.gob.sunat.sol.IncompleteConversationalState")) {
        beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
      }
      else {
        beanM.setError(true);
        beanM.setMensajeerror(
            "Ha ocurrido un error al obtener dias acumulados de licencia.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new LicenciaException(beanM);
    }

    return res;
  }

  /**
   * Metodo encargado de buscar licencias
   * @param tipo Tipo de Licencia
   * @param criterio Criterio de busqueda
   * @param valor Valor de criterio
   * @return Lista de licencias
   * @throws AsistenciaException
   */
  public List buscarLicencias(String dbpool, String tipo, String criterio,
                                   String valor, HashMap seguridad) throws
      LicenciaException {

    List lista = null;
    try {
      lista = licencia.buscarLicencias(dbpool, tipo, criterio, valor, seguridad);
    }
    catch (IncompleteConversationalState e) {
      throw new LicenciaException(e.getBeanMensaje());
    }
    catch (RemoteException e) {
      BeanMensaje beanM = new BeanMensaje();
      if (e.detail.getClass().getName().equals(
          "pe.gob.sunat.sol.IncompleteConversationalState")) {
        beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
      }
      else {
        beanM.setError(true);
        beanM.setMensajeerror(
            "Ha ocurrido un error al buscar licencias.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new LicenciaException(beanM);
    }

    return lista;
  }
  
  /**
   * Metodo encargado de buscar licencia por Solicitud: jquispecoi 03/2014
   * @param tipo Tipo de Licencia
   * @param criterio Criterio de busqueda
   * @param valor Valor de criterio
   * @return Lista de licencias
   * @throws AsistenciaException
   */
  public Map buscarLicenciaSolRef(String dbpool, String anno_ref, String numero_ref,
                                   String cod_pers, String mov) throws
      LicenciaException {

    Map lic = null;
    try {
      lic = licencia.buscarLicenciaSolRef(dbpool, anno_ref, numero_ref, cod_pers, mov);
    }
    catch (IncompleteConversationalState e) {
      throw new LicenciaException(e.getBeanMensaje());
    }
    catch (RemoteException e) {
      BeanMensaje beanM = new BeanMensaje();
      if (e.detail.getClass().getName().equals(
          "pe.gob.sunat.sol.IncompleteConversationalState")) {
        beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
      }
      else {
        beanM.setError(true);
        beanM.setMensajeerror(
            "Ha ocurrido un error al buscar licencias.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new LicenciaException(beanM);
    }

    return lic;
  }

  /**
   * Metodo encargado de agregar los datos referentes a la licencia medica
   * @param licencia Datos de la licencia
   * @throws AsistenciaException
   */
  public Map agregaDatosLicenciaMedica(String dbpool, HashMap lic) throws
      LicenciaException {

    Map licMed ;
    try {
      licMed= licencia.agregaDatosLicenciaMedica(dbpool, lic);
    }
    catch (IncompleteConversationalState e) {
      throw new LicenciaException(e.getBeanMensaje());
    }
    catch (RemoteException e) {
      BeanMensaje beanM = new BeanMensaje();
      if (e.detail.getClass().getName().equals(
          "pe.gob.sunat.sol.IncompleteConversationalState")) {
        beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
      }
      else {
        beanM.setError(true);
        beanM.setMensajeerror(
            "Ha ocurrido un error al agregar datos de licencia por enfermedad.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new LicenciaException(beanM);
    }
    return licMed;

  }

  /**
   * Metodo encargado de modificar los datos de una licencia
   * @param codPers Registro del trabajador
   * @param codUO Unidad Organizacional
   * @param tipo Tipo de Licencia
   * @param tipoEnfermedad Tipo de enfermedad
   * @param periodo Periodo
   * @param licencia Licencia
   * @param fIni Fecha Inicio
   * @param fechaIni Fecha Inicio
   * @param fechaFin Fecha Fin
   * @param ano_ref Ano referencia de la solicitud
   * @param numero_ref Numero referencia de la solicitud
   * @param area_ref Area referencia de la solicitud
   * @param obs Observacion
   * @param numero Numero de licencia
   * @param certif Certificado
   * @param cmp Codigo de Colegio Medico
   * @param fechaCita Fecha de la cita medica
   * @param usuario Usuario
   * @return String
   * @throws AsistenciaException
   */
  public String modificarLicencia(String dbpool, String codPers, String codUO,
                                  String tipo, String tipoEnfermedad,
                                  String periodo, String lic,
                                  Timestamp fIni, String fechaIni,
                                  String fechaFin, String ano_ref,
                                  String numero_ref,
                                  String area_ref, String obs, String numero,
                                  String certif, String cmp, String fechaCita,
                                  String usuario) throws LicenciaException {

    String res = "";
    try {
      res = licencia.modificarLicencia(dbpool, codPers, codUO, tipo, periodo,
                                       lic,
                                       fIni, fechaIni, fechaFin, ano_ref,
                                       numero_ref, area_ref, obs, numero,
                                       certif, cmp, fechaCita, tipoEnfermedad,
                                       usuario);
    }
    catch (IncompleteConversationalState e) {
      throw new LicenciaException(e.getBeanMensaje());
    }
    catch (RemoteException e) {
      BeanMensaje beanM = new BeanMensaje();
      if (e.detail.getClass().getName().equals(
          "pe.gob.sunat.sol.IncompleteConversationalState")) {
        beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
      }
      else {
        beanM.setError(true);
        beanM.setMensajeerror(
            "Ha ocurrido un error al modificar la licencia.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new LicenciaException(beanM);
    }

    return res;
  }

  /**
   * Metodo encargado de registrar las licencias
   * @param datos
   * @return
   * @throws LicenciaException
   */  
  public String registrarLicencia(HashMap datos, ArrayList lista) throws
      LicenciaException {

    String res = "";
    try {
      res = licencia.registrarLicencia(datos,lista);
    }
    catch (IncompleteConversationalState e) {
      throw new LicenciaException(e.getBeanMensaje());
    }
    catch (RemoteException e) {
      BeanMensaje beanM = new BeanMensaje();
      if (e.detail.getClass().getName().equals(
          "pe.gob.sunat.sol.IncompleteConversationalState")) {
        beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
      }
      else {
        beanM.setError(true);
        beanM.setMensajeerror(
            "Ha ocurrido un error al registrar la licencia.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new LicenciaException(beanM);
    }

    return res;
  }

  /**
   * Metodo encargado de eliminar licencias
   * @param params Lista de indices de licencias a eliminar
   * @param lista Lista de licencias
   * @return Lista modificada
   * @throws LicenciaException
   */
  public ArrayList eliminarLicencias(String dbpool, String[] params,
                                     ArrayList lista, ArrayList listaMensajes, String usuario) throws
      LicenciaException {

    try {
      lista = licencia.eliminarLicencias(dbpool, params, lista, listaMensajes, usuario);		//jquispecoi 05/2014
    }
    catch (IncompleteConversationalState e) {
      throw new LicenciaException(e.getBeanMensaje());
    }
    catch (RemoteException e) {
      BeanMensaje beanM = new BeanMensaje();
      if (e.detail.getClass().getName().equals(
          "pe.gob.sunat.sol.IncompleteConversationalState")) {
        beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
      }
      else {
        beanM.setError(true);
        beanM.setMensajeerror(
            "Ha ocurrido un error al eliminar las licencias.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new LicenciaException(beanM);
    }

    return lista;
  }

  /**
   * Metodo encargado de procesar el onomastico de un trabajador
   * @param codPers Registro del trabajador
   * @param compNoLaboral Indica si desea se considere los dias no laborales
   * @param usuario Usuario
   * @return Resultado del proceso
   * @throws LicenciaException
   */
  public BeanCompensaOnom procesarOnomastico(String dbpool, String codPers,
                                             boolean compNoLaboral,
                                             String usuario) throws
      LicenciaException {

    BeanCompensaOnom bCompen = null;
    try {

      bCompen = licencia.procesarOnomastico(dbpool, codPers, compNoLaboral,
                                            usuario);

    }
    catch (IncompleteConversationalState e) {
      throw new LicenciaException(e.getBeanMensaje());
    }
    catch (RemoteException e) {
      BeanMensaje beanM = new BeanMensaje();
      if (e.detail.getClass().getName().equals(
          "pe.gob.sunat.sol.IncompleteConversationalState")) {
        beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
      }
      else {
        beanM.setError(true);
        beanM.setMensajeerror(
            "Ha ocurrido un error al procesar el onomastico.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new LicenciaException(beanM);
    }

    return bCompen;
  }

  /**
   *
   * @param dbpool
   * @param codPers
   * @return
   * @throws LicenciaException
   */
  public BeanCompensaOnom prepararCompOnom(String dbpool, String codPers) throws
      LicenciaException {

    BeanCompensaOnom bCompen = null;
    try {

      bCompen = licencia.prepararCompOnom(dbpool, codPers);

    }
    catch (IncompleteConversationalState e) {
      throw new LicenciaException(e.getBeanMensaje());
    }
    catch (RemoteException e) {
      BeanMensaje beanM = new BeanMensaje();
      if (e.detail.getClass().getName().equals(
          "pe.gob.sunat.sol.IncompleteConversationalState")) {
        beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
      }
      else {
        beanM.setError(true);
        beanM.setMensajeerror(
            "Ha ocurrido un error al verificar onomastico.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new LicenciaException(beanM);
    }

    return bCompen;
  }
  

}