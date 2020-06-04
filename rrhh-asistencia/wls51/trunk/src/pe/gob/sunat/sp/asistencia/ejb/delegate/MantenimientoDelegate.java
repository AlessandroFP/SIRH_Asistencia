package pe.gob.sunat.sp.asistencia.ejb.delegate;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import pe.gob.sunat.framework.core.ejb.DelegateException;
import pe.gob.sunat.framework.core.pattern.DynaBean;
import pe.gob.sunat.framework.core.pattern.ServiceLocator;
import pe.gob.sunat.sol.BeanMensaje;
import pe.gob.sunat.sol.IncompleteConversationalState;
import pe.gob.sunat.sp.asistencia.ServletTurnoTrabajo;
import pe.gob.sunat.sp.asistencia.bean.BeanPeriodo;
import pe.gob.sunat.sp.asistencia.ejb.MantenimientoFacadeHome;
import pe.gob.sunat.sp.asistencia.ejb.MantenimientoFacadeRemote;
import pe.gob.sunat.utils.Constantes;

/**
 * <p>
 * Title: AsistenciaDelegate
 * </p>
 * <p>
 * Description: Clase encargada de administrar las invocaciones para las
 * funcionalidades del modulo de mantenimientos
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: Sunat
 * </p>
 * 
 * @author cgarratt
 * @version 1.0
 */
public class MantenimientoDelegate {
	
	private static final Logger log = Logger.getLogger(MantenimientoDelegate.class);
	
    private MantenimientoFacadeRemote mantenimiento;

    public MantenimientoDelegate() throws MantenimientoException {
        try {
            MantenimientoFacadeHome facadeHome = (MantenimientoFacadeHome) ServiceLocator
                    .getInstance().getRemoteHome(
                    		MantenimientoFacadeHome.JNDI_NAME,
                            MantenimientoFacadeHome.class);
            mantenimiento = facadeHome.create();
        } catch (Exception e) {
            BeanMensaje beanM = new BeanMensaje();
            beanM.setError(true);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new MantenimientoException(beanM);
        }
    }

    /**
     * 
     * @param criterio
     * @param valor
     * @return @throws
     *         MantenimientoException
     */
    public ArrayList buscarTurnos(String dbpool, String criterio, String valor)
            throws MantenimientoException {

        ArrayList turnos = null;
        try {
            turnos = mantenimiento.buscarTurnos(dbpool, criterio, valor);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al buscar los turnos.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }
        return turnos;
    }
    
    /**
     * 
     * @return @throws
     *         MantenimientoException
     */
    public ArrayList cargarPeriodos(String dbpool)
            throws MantenimientoException {

        ArrayList periodos = null;
        try {
            periodos = mantenimiento.cargarPeriodos(dbpool);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al cargar los periodos.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return periodos;
    }

    /**
     * 
     * @param estado
     * @return @throws
     *         MantenimientoException
     */
    public ArrayList cargarRelojes(String dbpool, String estado)
            throws MantenimientoException {

        ArrayList relojes = null;
        try {
            relojes = mantenimiento.cargarRelojes(dbpool, estado);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al cargar los relojes.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return relojes;
    }

    /**
     * 
     * @param criterio
     * @param valor
     * @param tipo
     * @return @throws
     *         MantenimientoException
     */
    public ArrayList buscarMovimientos(String dbpool, String criterio,
            String valor, String tipo) throws MantenimientoException {

        ArrayList movimientos = null;
        try {
            movimientos = mantenimiento.buscarMovimientos(dbpool, criterio,
                    valor, tipo);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al buscar los movimientos.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return movimientos;
    }

    /**
     * 
     * @param criterio
     * @param valor
     * @return @throws
     *         MantenimientoException
     */
    public ArrayList buscarPeriodos(String dbpool, String criterio, String valor)
            throws MantenimientoException {

        ArrayList periodos = null;
        try {
            periodos = mantenimiento.buscarPeriodos(dbpool, criterio, valor);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al buscar los periodos.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return periodos;
    }

    /**
     * 
     * @param criterio
     * @param valor
     * @return @throws
     *         MantenimientoException
     */
    public ArrayList buscarRelojes(String dbpool, String criterio, String valor)
            throws MantenimientoException {

        ArrayList relojes = null;
        try {
            relojes = mantenimiento.buscarRelojes(dbpool, criterio, valor);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al buscar los relojes.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return relojes;
    }

    /**
     * 
     * @param codTabla
     * @return @throws
     *         MantenimientoException
     */
    public ArrayList cargarT99(String dbpool, String codTabla)
            throws MantenimientoException {

        ArrayList tipos = null;
        try {
            tipos = mantenimiento.cargarT99(dbpool, codTabla);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al cargar los codigos.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return tipos;
    }    
    //dtarazona
    /**
     * 
     * @param codTabla
     * @return @throws
     *         MantenimientoException
     */
    public ArrayList cargarAnnosVac(String dbpool, String codTabla)
            throws MantenimientoException {

        ArrayList tipos = null;
        try {
            tipos = (ArrayList)mantenimiento.cargarAnnosVac(dbpool, codTabla);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al cargar los codigos.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return tipos;
    }    
    /**
     * 
     * @param codTabla
     * @return @throws
     *         MantenimientoException
     */
    public ArrayList cargarDiasVac(String dbpool)
            throws MantenimientoException {

        ArrayList tipos = null;
        try {
            tipos = mantenimiento.cargarDiasVac(dbpool);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al cargar los codigos.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return tipos;
    }    
    //fin dtarazona

    /**
     * 
     * @param codTabla
     * @return @throws
     *         MantenimientoException
     */
    public ArrayList cargarT99(String dbpool, String codTabla, String modulo)
            throws MantenimientoException {

        ArrayList tipos = null;
        try {
            tipos = mantenimiento.cargarT99(dbpool, codTabla, modulo);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al cargar los codigos.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return tipos;
    }

    /**
     * 
     * @return @throws
     *         MantenimientoException
     */
    public ArrayList cargarTurnos(String dbpool) throws MantenimientoException {

        ArrayList turnos = null;
        try {
            turnos = mantenimiento.cargarTurnos(dbpool);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al cargar los turnos.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return turnos;
    }

    /**
     * 
     * @param periodo
     * @param uOrg
     * @param uEstado
     * @param check
     * @throws MantenimientoException
     */
    public void desactivarDetallePeriodo(ArrayList lista, String[] checks,
            String periodo, int inicio, int fin) throws MantenimientoException {

        try {
            mantenimiento.desactivarDetallePeriodo(lista, checks, periodo,
                    inicio, fin);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al desactivar el detalle de los periodos.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return;
    }

    /**
     * 
     * @param params
     * @param listaMovimientos
     * @throws MantenimientoException
     */
    public void eliminarMovimientos(String[] params, ArrayList listaMovimientos)
            throws MantenimientoException {

        try {
            mantenimiento.eliminarMovimientos(params, listaMovimientos);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al eliminar los movimientos.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return;
    }

    /**
     * 
     * @param params
     * @param listaMovimientos
     * @throws MantenimientoException
     */
    public void eliminarPeriodos(String[] params, ArrayList listaMovimientos)
            throws MantenimientoException {

        try {
            mantenimiento.eliminarPeriodos(params, listaMovimientos);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al eliminar los periodos.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return;
    }

    /**
     * 
     * @param params
     * @param listaMovimientos
     * @throws MantenimientoException
     */
    public void eliminarRelojes(String[] params, ArrayList listaMovimientos)
            throws MantenimientoException {

        try {
            mantenimiento.eliminarRelojes(params, listaMovimientos);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al eliminar los relojes.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return;
    }
    
    /**
     * 
     * @param params
     * @param listaMovimientos
     * @throws MantenimientoException
     */
    public void eliminarAprobadores(String[] params, ArrayList lista)
            throws MantenimientoException {

        try {
            mantenimiento.eliminarAprobadores(params, lista);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al eliminar los relojes.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return;
    }    

    /**
     * 
     * @param params
     * @param listaMovimientos
     * @throws MantenimientoException
     */
    public void eliminarTurnos(String[] params, ArrayList listaMovimientos)
            throws MantenimientoException {

        try {
            mantenimiento.eliminarTurnos(params, listaMovimientos);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al eliminar los turnos.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return;
    }

    /*
     * 
     * @param codMovimiento
     * @param descripcion
     * @param califica
     * @param medida
     * @param tipo
     * @param fechaIni
     * @param fechaFin
     * @param soliId
     * @param vbRrHh
     * @param procId
     * @param entrId
     * @param refrId
     * @param saliId
     * @throws MantenimientoException
     *
    public void modificarMovimiento(String codMovimiento, String descripcion,
            String califica, String medida, String tipo,
            java.sql.Timestamp fechaIni, java.sql.Timestamp fechaFin,
            String soliId, String vbRrHh, String procId, String entrId,
            String refrId, String saliId, String diasAntes, String diasDespues,
            String diasAcum, String obligId, String qvalida, String usuario, 
			String ind_dias, String ind_proc,
			String ip, String pc, String razon, String cod_usuario) throws MantenimientoException {

        try {
            mantenimiento.modificarMovimiento(codMovimiento, descripcion,
                    califica, medida, tipo, fechaIni, fechaFin, soliId, vbRrHh,
                    procId, entrId, refrId, saliId, diasAntes, diasDespues,
                    diasAcum, obligId, qvalida, usuario, ind_dias, ind_proc,
                    ip, pc, razon, cod_usuario);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al modificar el movimiento.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

    }
*/
    
    /**
     * 
     * @param params
     * @param fechaIni
     * @param fechaFin
     * @throws MantenimientoException
     */
    public void modificarMovimiento(Map params,
            java.sql.Timestamp fechaIni, java.sql.Timestamp fechaFin)
    throws MantenimientoException {

        try {
            mantenimiento.modificarMovimiento(params, fechaIni, fechaFin);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al modificar el movimiento.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

    }
    
    
    /**
     * 
     * @param periodo
     * @param fechaIni
     * @param fechaFin
     * @param fechaCierre
     * @throws MantenimientoException
     */
    //public void modificarPeriodo(String dbpool, String periodo, String fechaIni, String fechaFin, String fechaCierre)
    public void modificarPeriodo(Map params)
            throws MantenimientoException {

        try {
            //mantenimiento.modificarPeriodo(dbpool, periodo, fechaIni, fechaFin, fechaCierre);
            mantenimiento.modificarPeriodo(params);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al modificar el periodo.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return;
    }

    /**
     * 
     * @param reloj
     * @param descripcion
     * @param sede
     * @throws MantenimientoException
     */
    public void modificarReloj(String reloj, String descripcion, String sede)
            throws MantenimientoException {

        try {
            mantenimiento.modificarReloj(reloj, descripcion, sede);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al modificar el reloj.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return;
    }

    /**
     * 
     * @param codTurno
     * @param descripcion
     * @param fechaIni
     * @param horaIni
     * @param fechaFin
     * @param horaFin
     * @param dias
     * @param tolerancia
     * @param horaLimite
     * @throws MantenimientoException
     */
    public void modificarTurno(String codTurno, String descripcion,
            String fechaIni, String horaIni, String fechaFin, String horaFin,
            String dias, String tolerancia, String horaLimite, String refrIni,
            String refrFin, String refrMin, String control)
            throws MantenimientoException {

        try {
            mantenimiento.modificarTurno(codTurno, descripcion, fechaIni,
                    horaIni, fechaFin, horaFin, dias, tolerancia, horaLimite,
                    refrIni, refrFin, refrMin, control);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al modificar el turno.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

    }

    /**
     * 
     * @param codMovimiento
     * @param descripcion
     * @param califica
     * @param medida
     * @param tipo
     * @param fechaIni
     * @param fechaFin
     * @param soliId
     * @param vbRrHh
     * @param procId
     * @param entrId
     * @param refrId
     * @param saliId
     * @param diasAntes
     * @param diasDespues
     * @param diasAcum
     * @param obligId
     * @param usuario
     * @throws MantenimientoException
     */
    public void registrarMovimiento(String codMovimiento, String descripcion,
            String califica, String medida, String tipo,
            java.sql.Timestamp fechaIni, java.sql.Timestamp fechaFin,
            String soliId, String vbRrHh, String procId, String entrId,
            String refrId, String saliId, String diasAntes, String diasDespues,
            String diasAcum, String obligId, String qvalida, String ind_dias,
            String ind_proc, String usuario)
            throws MantenimientoException {

        try {
            mantenimiento.registrarMovimiento(codMovimiento, descripcion,
                    califica, medida.trim(), tipo.trim(), fechaIni, fechaFin,
                    soliId, vbRrHh, procId, entrId, refrId, saliId, diasAntes,
                    diasDespues, diasAcum, obligId, qvalida, ind_dias, ind_proc, usuario);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al registrar el movimiento.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return;
    }

    /**
     * 
     * @param periodo
     * @param fechaIni
     * @param fechaFin
     * @param fechaCierre
     * @param usuario
     * @throws MantenimientoException
     */
    public void registrarPeriodo(Map params)
    //public void registrarPeriodo(String dbpool, String periodo, String fechaIni, String fechaFin, String fechaCierre, String usuario)
            throws MantenimientoException {

        try {
        	mantenimiento.registrarPeriodo(params);
        	//mantenimiento.registrarPeriodo(dbpool, periodo, fechaIni, fechaFin, fechaCierre, usuario);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al registrar el periodo.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return;
    }

    /**
     * 
     * @param reloj
     * @param descripcion
     * @param sede
     * @param usuario
     * @throws MantenimientoException
     */
    public void registrarReloj(String reloj, String descripcion, String sede,
            String usuario) throws MantenimientoException {

        try {
            mantenimiento.registrarReloj(reloj, descripcion, sede, usuario);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al registrar el reloj.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return;
    }

    /**
     * 
     * @param codTurno
     * @param descripcion
     * @param fechaIni
     * @param horaIni
     * @param fechaFin
     * @param horaFin
     * @param dias
     * @param tolerancia
     * @param horaLimite
     * @param operId
     * @param usuario
     * @throws MantenimientoException
     */
    public void registrarTurno(String codTurno, String descripcion,
            String fechaIni, String horaIni, String fechaFin, String horaFin,
            String dias, String tolerancia, String horaLimite, String operId,
            String refrIni, String refrFin, String refrMin, String usuario,
            String control) throws MantenimientoException {

        try {
            mantenimiento.registrarTurno(codTurno, descripcion, fechaIni,
                    horaIni, fechaFin, horaFin, dias, tolerancia, horaLimite,
                    operId, refrIni, refrFin, refrMin, usuario, control);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al registrar el turno.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return;
    }

    /**
     * 
     * @param tipo
     * @param estado
     * @param fIni
     * @param fFin
     * @return @throws
     *         AsistenciaException
     */
    public ArrayList buscarTipoMovimientos(String dbpool, String tipo,
            String estado, String fIni, String fFin)
            throws MantenimientoException {

        ArrayList lista = null;

        try {
            lista = mantenimiento.buscarTipoMovimientos(dbpool, tipo, estado,
                    fIni, fFin);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al buscar los movimientos.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return lista;
    }

    /**
     * 
     * @param estado
     * @return @throws
     *         AsistenciaException
     */
    public ArrayList buscarPeriodosEstado(String dbpool, String estado)
            throws MantenimientoException {

        ArrayList lista = null;

        try {
            lista = mantenimiento.buscarPeriodosEstado(dbpool, estado);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al buscar los periodos.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return lista;
    }

    /**
     * 
     * @param periodo
     * @return @throws
     *         AsistenciaException
     */
    public BeanPeriodo buscarPeriodoCodigo(String dbpool, String periodo)
            throws MantenimientoException {

        BeanPeriodo bean = null;
        try {
            bean = mantenimiento.buscarPeriodoCodigo(dbpool, periodo);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al buscar los periodos.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return bean;
    }
    
    //ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
    /**
     * 
     * @param periodo
     * @return @throws
     *         AsistenciaException
     */
    public BeanPeriodo buscarPeriodoByCodigoByEstado(String dbpool, String periodo,String estado)
            throws MantenimientoException {

        BeanPeriodo bean = null;
        try {
            bean = mantenimiento.buscarPeriodoByCodigoByEstado(dbpool, periodo,estado);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al buscar el periodo.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return bean;
    }
    //FIN ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral

    /**
     * 
     * @param estado
     * @return @throws
     *         MantenimientoException
     */
    public ArrayList buscarSedes(String dbpool, String estado)
            throws MantenimientoException {

        ArrayList sedes = null;
        try {
            sedes = mantenimiento.buscarSedes(dbpool, estado);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al buscar las sedes.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return sedes;
    }

    /**
     * 
     * @param estado
     * @return @throws
     *         MantenimientoException
     */
    public ArrayList buscarMovimientosSolicitud(String dbpool, String estado)
            throws MantenimientoException {

        ArrayList lista = null;
        try {
            lista = mantenimiento.buscarMovimientosSolicitud(dbpool, estado);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al buscar los movimientos de solicitud.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return lista;
    }

    /**
     * 
     * @param estado
     * @return @throws
     *         MantenimientoException
     */
    public boolean periodoCerradoAFecha(String dbpool, String periodo,
            String fecha) throws MantenimientoException {

        boolean cerrado = false;
        try {
            cerrado = mantenimiento
                    .periodoCerradoAFecha(dbpool, periodo, fecha);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al verificar el cierre de periodo.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return cerrado;
    }

    /**
     * 
     * @param estado
     * @return @throws
     *         MantenimientoException
     */
    public String periodoCerradoAFecha(String dbpool, String fecha)
            throws MantenimientoException {

        String periodo = "";
        try {
            periodo = mantenimiento.periodoCerradoAFecha(dbpool, fecha);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al verificar el cierre de periodo.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }
        return periodo;
    }
    //DTARAZONA AJUSTE ENTREGABLE 3 28/02/2017
    /**
     * 
     * @param estado
     * @return @throws
     *         MantenimientoException
     */
    public HashMap findMaximoVentaVacaciones(String dbpool)
            throws MantenimientoException {

    	HashMap periodo = null;
        try {
            periodo = mantenimiento.findMaximoVentaVacaciones(dbpool);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al verificar el cierre de periodo.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }
        return periodo;
    }
    
    /**
     * 
     * @param estado
     * @return @throws
     *         MantenimientoException
     */
    public HashMap findDiasVacacionesVendidas(String dbpool, String codPers,String anno)
            throws MantenimientoException {

    	HashMap periodo = null;
        try {
            periodo = mantenimiento.findDiasVacacionesVendidas(dbpool, codPers,anno);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al verificar el cierre de periodo.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }
        return periodo;
    }
    
    //FIN DTARAZONA
    /**
     * 
     * @param estado
     * @return @throws
     *         MantenimientoException
     */
    public boolean periodoCerradoUOFecha(String dbpool, String fechaPer, String fechaEval, String uo)
            throws MantenimientoException {

    	boolean cerrado = false;
        try {
        	cerrado = mantenimiento.periodoCerradoUOFecha(dbpool, fechaPer, fechaEval, uo);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al verificar el cierre de periodo.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }
        return cerrado;
    } 
    
    //ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII 
    /**
     * Metodo encargado de determinar si un periodo esta cerrado para un colaborador de acuerdo su regimen/modalidad
     * @param dbpool String
     * @param fechaPer String
     * @param fechaEval String
     * @param uo String
     * @param regimenModalidad
     * @return cerrado boolean
     * @throws MantenimientoException
     */
    public boolean periodoCerradoUOFecha_RegimenModalidad(String dbpool, String fechaPer, String fechaEval, String uo, String regimenModalidad)
            throws MantenimientoException {

    	boolean cerrado = false;
        try {
        	cerrado = mantenimiento.periodoCerradoUOFecha_RegimenModalidad(dbpool, fechaPer, fechaEval, uo, regimenModalidad);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al verificar el cierre de periodo.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }
        return cerrado;
    }
  //FIN ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII 

    /**
     * 
     * @param estado
     * @return @throws
     *         MantenimientoException
     */
    public ArrayList buscarSaldosVacacionales(String dbpool, String registro)
            throws MantenimientoException {

        ArrayList lista = null;
        try {
            lista = mantenimiento.buscarSaldosVacacionales(dbpool, registro);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al buscar los movimientos de solicitud.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return lista;
    }

//REEMPLAZADO POR METODO COMSA    
	/**
	 * Metodo encargado de actualizar los saldos vacacionales
	 * @param dbpool
	 * @param saldo
	 * @throws MantenimientoException
	 *
    public void actualizarSaldo(String dbpool, HashMap saldo) throws MantenimientoException {

        try {
            mantenimiento.actualizarSaldo(dbpool, saldo);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al actualizar saldo.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return;
    }
*/    
    
/* JRR - PROGRAMACION - 18/05/2010 */
    /**
  	 * Metodo encargado de validar; el saldo ingresado + goces Efectivos + compensacions no exedan
  	 * los 30 d&iacute;as
  	 * @param dbpool
  	 * @param saldo
  	 * @throws MantenimientoException
  	 */ 
      public boolean validaSaldo(Map saldo) throws MantenimientoException {
      		boolean exito = false;
          try {
              exito = mantenimiento.validaSaldo(saldo);
          } catch (IncompleteConversationalState e) {
              throw new MantenimientoException(e.getBeanMensaje());
          } catch (RemoteException e) {
              BeanMensaje beanM = new BeanMensaje();
              if (e.detail.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")) {
                  beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
              } else {
                  beanM.setError(true);
                  beanM.setMensajeerror("Ha ocurrido un error al validar saldo.");
                  beanM.setMensajesol("Por favor intente nuevamente.");
              }
              throw new MantenimientoException(beanM);
          }

          return exito;
      }
/*    */    
    

    /**
     * 
     * @param estado
     * @return @throws
     *         MantenimientoException
     */
    public ArrayList buscarResponsablesProceso(String dbpool, String proceso, String uorgan)
            throws MantenimientoException {

        ArrayList lista = null;
        try {
            lista = mantenimiento.buscarResponsablesProceso(dbpool, proceso, uorgan);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al buscar los movimientos de solicitud.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return lista;
    }
    
    /**
     * 
     * @param params
     * @param listaMovimientos
     * @throws MantenimientoException
     */
    public void eliminarResponsablesProceso(String[] params, ArrayList lista)
            throws MantenimientoException {

        try {
            mantenimiento.eliminarResponsablesProceso(params, lista);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al eliminar los responsables del proceso.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return;
    }
    
	/**
	 * Metodo encargado de registrar al responsable de un proceso
	 * @param dbpool
	 * @param saldo
	 * @throws MantenimientoException
	 */
    public void registrarResponsable(String dbpool, HashMap datos) throws MantenimientoException {

        try {
            mantenimiento.registrarResponsable(dbpool, datos);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al registrar responsable.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return;
    }
  //dtarazona
    /**
	 * 
	 * @param params
	 * @param usuario
	 */
	public void asignacionResponsablesMasivo(String dbpool,HashMap params) {
		try {
			
			mantenimiento.asignacionResponsablesMasivo(dbpool,params);
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al asignar el responsable.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

	}
    //dtarazona
    /**
     * 
     * @param estado
     * @return @throws
     *         MantenimientoException
     */
    public ArrayList buscarAprobadores(HashMap datos)
            throws MantenimientoException {

        ArrayList lista = null;
        try {
            lista = mantenimiento.buscarAprobadores(datos);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM.setMensajeerror("Ha ocurrido un error al buscar los aprobadores de solicitud.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return lista;
    }    
    
    /**
	 * Metodo encargado de registrar los aprobadores de una solicitud
	 * @param dbpool
	 * @param saldo
	 * @throws MantenimientoException
	 */
    public void registrarAprobador(HashMap datos) throws MantenimientoException {

        try {
            mantenimiento.registrarAprobador(datos);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al registrar aprobador.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return;
    }    
    
    /**
     * Metodo encargado de buscar las asistencias de un determinado periodo
     * @param periodo String Periodo
     * @param criterio String Criterio
     * @param valor String Valor
     * @return ArrayList
     * @throws AsistenciaException
     */
    public ArrayList buscarParametros(String dbpool, String codigo) throws
        AsistenciaException {

      ArrayList lista = null;
      try {
        lista = mantenimiento.buscarParametros(dbpool, codigo);
      }
      catch (IncompleteConversationalState e) {
        throw new AsistenciaException(e.getBeanMensaje());
      }
      catch (RemoteException e) {
        BeanMensaje beanM = new BeanMensaje();
        if (e.detail.getClass().getName().equals(
            "pe.gob.sunat.sol.IncompleteConversationalState")) {
          beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
        }
        else {
          beanM.setError(true);
          beanM.setMensajeerror("Ha ocurrido un error al buscar los detalles de un codigo.");
          beanM.setMensajesol("Por favor intente nuevamente.");
        }
        throw new AsistenciaException(beanM);
      }

      return lista;
    }
    
    /**
     * 
     * @param datos
     * @return @throws
     *         MantenimientoException
     */
    public HashMap buscarDelegados(HashMap datos)
            throws MantenimientoException {

    	HashMap lista = null;
        try {
            lista = mantenimiento.buscarDelegados(datos);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM.setMensajeerror("Ha ocurrido un error al buscar los delegados.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return lista;
    }
    
  //JVV - INI
    /**
     * Metodo de encargado de validar un tipo de solicitud          
     * @param dbpool String
     * @param tipo String
     * @param seguridad HashMap 
     * @throws UtilesException
     */
    public Map validarSolicitud(String dbpool, String tipo) throws
        UtilesException {

     Map t = null;

      try {
        t = mantenimiento.validarSolicitud(dbpool, tipo);
      }
      catch (IncompleteConversationalState e) {
         throw new UtilesException(e.getBeanMensaje());
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
               "Ha ocurrido un error al buscar tipo de solicitud.");
           beanM.setMensajesol("Por favor intente nuevamente.");
         }
         throw new UtilesException(beanM);
       }

      return t;
    }
    
    /**
     * Metodo de encargado de validar un tipo 
     * de licencia
     * @param dbpool String
     * @param tipo String
     * @param seguridad HashMap 
     * @throws UtilesException
     */
    public boolean validarLicencia(String dbpool, String tipo, String valor ) throws
        UtilesException {

    	boolean t ;

      try {
        t = mantenimiento.validarLicencia(dbpool, tipo, valor);
      }
      catch (IncompleteConversationalState e) {
         throw new UtilesException(e.getBeanMensaje());
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
               "Ha ocurrido un error al buscar tipo de solicitud.");
           beanM.setMensajesol("Por favor intente nuevamente.");
         }
         throw new UtilesException(beanM);
       }

      return t;
    }
    //JVV - FIN
     
    /**
	 * Metodo encargado de registrar los delegados de una UUOO
	 * @param dbpool
	 * @param saldo
	 * @throws MantenimientoException
	 */
    public void registrarDelegados(ArrayList procesos, HashMap datos, HashMap delegOrig, HashMap delegNuevo) throws MantenimientoException {

        try {
            mantenimiento.registrarDelegados(procesos,datos,delegOrig,delegNuevo);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al registrar los delegados.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return;
    }    
    
    /**
     * 
     * @param estado
     * @return @throws
     *         MantenimientoException
     */
    public ArrayList buscarUOsinAprob(String dbpool, String criterio,
			String valor, HashMap seguridad)
            throws MantenimientoException {

        ArrayList lista = null;
        try {
            lista = mantenimiento.buscarUOsinAprob(dbpool, criterio,
        			valor, seguridad);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM.setMensajeerror("Ha ocurrido un error al buscar las UO sin aprobadores de solicitud.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return lista;
    }    
    
    /**
     * 
     * @param estado
     * @return @throws
     *         MantenimientoException
     */
    public boolean cambiarAprobador(String dbpool, String tipo, String txtUO, String txtOri, String txtDes, String usuario)
            throws MantenimientoException {

        boolean lista = false;
        try {
            lista = mantenimiento.cambiarAprobador(dbpool, tipo, txtUO,
        			txtOri, txtDes, usuario);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM.setMensajeerror("Ha ocurrido un error al cambiar el aprobador.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return lista;
    }    
    
    /**
     * 
     * @param criterio
     * @param valor
     * @return @throws
     *         MantenimientoException
     */
    public ArrayList buscarSupervisores(HashMap datos)
            throws MantenimientoException {

        ArrayList superv = null;
        try {
        	superv = mantenimiento.buscarSupervisores(datos);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al buscar los supervisores.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }
        return superv;
    }

    /**
     * 
     * @param params
     * @param listaMovimientos
     * @throws MantenimientoException
     */
    public void eliminarSupervisores(String dbpool, String[] params, ArrayList lista)
            throws MantenimientoException {

        try {
            mantenimiento.eliminarSupervisores(dbpool, params, lista);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al eliminar los supervisores.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return;
    }

    /**
     * 
     * @param params
     * @param listaMovimientos
     * @throws MantenimientoException
     */
    public void registrarSupervisor(String dbpool, String codMov, String UO, String superv,
            String fechaInicio, String fechaFin, String usuario)
            throws MantenimientoException {

        try {
            mantenimiento.registrarSupervisor(dbpool, codMov, UO, superv, fechaInicio, fechaFin, usuario);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al registrar el supervisor.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return;
    }

    /**
     * 
     * @param params
     * @param listaMovimientos
     * @throws MantenimientoException
     */
    public void modificarSupervisor(String dbpool, HashMap superv, 
            String fechaInicio, String fechaFin, String estado, String usuario)
            throws MantenimientoException {

        try {
            mantenimiento.modificarSupervisor(dbpool, superv, fechaInicio, fechaFin, estado, usuario);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al modificar el supervisor.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return;
    }

    
    /**
     * 
     * @param tipo
     * @param estado
     * @param fIni
     * @param fFin
     * @return @throws
     *         AsistenciaException
     */
    public ArrayList buscarTipoMovimientosPerfil(String dbpool, String tipo,
            String estado, String fIni, String fFin, HashMap seguridad)
            throws MantenimientoException {

        ArrayList lista = null;

        try {
            lista = mantenimiento.buscarTipoMovimientosPerfil(dbpool, tipo, estado,
                    fIni, fFin, seguridad);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al buscar los movimientos.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new DelegateException();
        }

        return lista;
    }

	//JRR - 10 ABRIL 2008
    /**
     * 
     * @param 
     * @return @throws
     *         MantenimientoException
     */
    public Map cargarFindByMov(String dbpool, String tipMov)
            throws MantenimientoException {

        Map hmMov = null;
        try {
        	hmMov = mantenimiento.cargarFindByMov(dbpool, tipMov);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al cargar los codigos.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return hmMov;
    }    
    
  //ICAPUNAY - FORMATIVAS 09/06/2011
    /**
     * @param dbpool
     * @param tipo
     * @param estado
     * @param fIni
     * @param fFin
     * @param seguridad
     * @return @throws
     *         AsistenciaException
     */
    public ArrayList buscarTipoMovimientosFormativasPerfil(String dbpool, String tipo,
            String estado, String fIni, String fFin, HashMap seguridad)
            throws MantenimientoException {

        ArrayList lista = null;

        try {
            lista = mantenimiento.buscarTipoMovimientosFormativasPerfil(dbpool, tipo, estado,fIni, fFin, seguridad);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al buscar los movimientos.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new DelegateException();
        }

        return lista;
    }
  //FIN ICAPUNAY - FORMATIVAS
    
/* ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA - 22/08/2011 */
    
    /**
     * 
     * @param params
     * @param listaCategorias
     * @throws MantenimientoException
     */
    public void eliminarCategorias(String[] params, ArrayList listaCategorias)
            throws MantenimientoException {

        try {
            mantenimiento.eliminarCategorias(params, listaCategorias);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al eliminar los periodos.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return;
    }

    /**
     * 
     * @return @throws
     *         MantenimientoException
     */    
    public ArrayList buscarMovimientosActivosInactivos(String dbpool) //nuevo agregado 05/12/2011 icapunay
            throws MantenimientoException {

        ArrayList movimientos = null;
        try {
        	//movimientos = mantenimiento.buscarMovimientosActivos(dbpool); //nuevo agregado 05/12/2011 icapunay
        	movimientos = mantenimiento.buscarMovimientosActivosInactivos(dbpool); ////nuevo agregado 05/12/2011 icapunay
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al cargar los movimientos de la t1279tipo_mov.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return movimientos;
    }
    /**
     * 
     * @return @throws
     *         MantenimientoException
     */
    public ArrayList buscarCategorias(String dbpool)
            throws MantenimientoException {

        ArrayList categorias = null;
        try {
        	categorias = mantenimiento.buscarCategorias(dbpool);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al cargar las categorias actuales.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return categorias;
    }  
    
    /**
     * 
     * @return @throws
     *         MantenimientoException
     */
    public ArrayList buscarCategoriasOrdenadasByDescrip(String dbpool)
            throws MantenimientoException {

        ArrayList categorias = null;
        try {
        	categorias = mantenimiento.buscarCategoriasOrdenadasByDescrip(dbpool);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al cargar las categorias actuales ordenadas por descripciÃƒÂ³n.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return categorias;
    }  
    
    /**
     * 
     * @return @throws
     *         MantenimientoException
     */
    public ArrayList cargarMovAsignados(String codCategoria)
            throws MantenimientoException {

        ArrayList movimientos = null;
        try {
        	movimientos = mantenimiento.cargarMovAsignados(codCategoria);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al cargar los periodos.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return movimientos;
    }
    
    /**
     * Metodo encargado de devolver todos los movimientos asignados a todas las categorias
     * @return @throws
     *         MantenimientoException
     */
    public ArrayList cargarMovAsignadosAllCategorias()
            throws MantenimientoException {

        ArrayList movimientos = null;
        try {
        	movimientos = mantenimiento.cargarMovAsignadosAllCategorias();
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al cargar los periodos.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return movimientos;
    }
    
    /**
     * 
     * @return @throws
     *         MantenimientoException
     */
    public ArrayList cargarMovPorAsignar(ArrayList listaMovAsignados, ArrayList listaAllMovT1279Activos)
            throws MantenimientoException {

        ArrayList movimientosPorAsignar = null;
        try {
        	movimientosPorAsignar = mantenimiento.cargarMovPorAsignar(listaMovAsignados,listaAllMovT1279Activos);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al cargar los periodos.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return movimientosPorAsignar;
    }
    
    /**
     * Metodo que devuelve el nuevo codigo de categoria a crear
     * 
     * @param dbpool
     * @return @throws
     *         MantenimientoException
     */
    public String devolverNuevoCodigoCategoria(String dbpool)
            throws MantenimientoException {

        String nuevo_codCate = null;
        try {
        	nuevo_codCate = mantenimiento.devolverNuevoCodigoCategoria(dbpool);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM.setMensajeerror("Ha ocurrido un error al devolver el nuevo codigo de categoria.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }
        return nuevo_codCate;
    } 
    
    /**
     * Metodo que devuelve una categoria
     * @param codCategoria String
     * @param dbpool
     * @return categoria Map
     * @throws MantenimientoException
     */
    public Map buscarCategoria(String dbpool, String codCategoria)
            throws MantenimientoException {

    	Map categoria= null;
        try {
        	categoria = mantenimiento.buscarCategoria(dbpool,codCategoria);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM.setMensajeerror("Ha ocurrido un error al devolver la categoria.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }
        return categoria;
    } 
    
    /**
     * 
     * @param params  
     * @throws MantenimientoException
     */
    public void registrarCategoria(String dbpool,HashMap params)
            throws MantenimientoException {

        try {
            mantenimiento.registrarCategoria(dbpool,params);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al eliminar los periodos.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return;
    }
    /**
     * 
     * @return @throws
     *         MantenimientoException
     */
    public ArrayList buscarCategoriasByMovimiento(String dbpool,String codMovimiento)
            throws MantenimientoException {

        ArrayList categorias = null;
        try {
        	categorias = mantenimiento.buscarCategoriasByMovimiento(dbpool,codMovimiento);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al cargar los periodos.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return categorias;
    }  
    /* FIN ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA - 22/08/2011 */ 

 
    /**
     * 
     * @param Datos necesarios para la ejecución del procedimiento.
     * @return 
     * @throws MantenimientoException
     * @author jmaravi
     * @since 13/03/2014
     */
    public ArrayList findMovimientosFlujosAprobadores(String dbpool, HashMap datos) throws MantenimientoException {

        ArrayList movimientos = null;
        try {
            movimientos = mantenimiento.findMovimientosFlujosAprobadores(dbpool, datos);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al buscar los movimientos.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return movimientos;
    }
    
    /**
     * 
     * @param criterio
     * @param valor
     * @param tipo
     * @return 
     * @throws MantenimientoException
     * @author jmaravi
     * @since 10/04/2014
     */
    public ArrayList obtenerPendientesxAprobador(String dbpool, HashMap datos) throws MantenimientoException {

        ArrayList movimientos = null;
        try {
            movimientos = mantenimiento.obtenerPendientesxAprobador(dbpool, datos);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al buscar los movimientos pendientes.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return movimientos;
    }    
    
    /**
     * 
     * @param criterio
     * @param valor
     * @param tipo
     * @return 
     * @throws MantenimientoException
     * @author jmaravi
     * @since 14/04/2014
     */
    public boolean insertAprobadorFlujo(String dbpool, HashMap datos) throws MantenimientoException {

    	boolean movimientos = false;
        try {
            movimientos = mantenimiento.insertAprobadorFlujo(dbpool, datos);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al insertar el flujo de aprobadores.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return movimientos;
    }
    
    /**
     * Permite la eliminación de un flujo de aprobación mediante su primary key     * 
     * @param datos parámetros necesarios para ejecutar la consulta.
     * @return 
     * @throws MantenimientoException
     * @author jmaravi
     * @since 14/04/2014
     */
    public boolean deleteAprobadorFlujo(String dbpool, HashMap datos) throws MantenimientoException {

        boolean movimientos = false;
        try {
            movimientos = mantenimiento.deleteAprobadorFlujo(dbpool, datos);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al eliminar el flujo de aprobadores.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return movimientos;
    }    
    
    /**
     * Permite eliminar todos los flujos asociados a una unidad organizacional y
     * un tipo de movimiento.
     * @param datos Los parámetros necesarios para realizar la consulta.
     * @return 
     * @throws MantenimientoException
     * @author jmaravi
     * @since 14/04/2014
     */
    public boolean deleteAprobadorFlujo4UniOrgYMov(String dbpool, HashMap datos) throws MantenimientoException {

        boolean movimientos = false;
        try {
            movimientos = mantenimiento.deleteAprobadorFlujo4UniOrgYMov(dbpool, datos);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al eliminar el flujo de aprobadores.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return movimientos;
    }     
    
    /**
     * Permite reasignar(transferir) todas las solicitudes del tipo X a un nuevo aprobador.
     * @param datos Los parámetros necesarios para realizar la consulta.
     * @return 
     * @throws MantenimientoException
     * @author jmaravi
     * @since 14/04/2014
     */
    public int transferirSegPendientes(String dbpool, String uorgan, String numMov, 
			String viejoAprob, String nuevoAprob, String usuario) throws MantenimientoException {

        int movimientos = 0;
        try {
            movimientos = mantenimiento.transferirSegPendientes(dbpool, uorgan, numMov, viejoAprob, nuevoAprob, usuario);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al reasignar las solicitudes pendientes.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return movimientos;
    } 
    
    
	/**
	 * Metodo encargado del registro masivo del flujo de aprobadores 
	 * @param dbean	DynaBean
	 * @return String
	 * @throws MantenimientoException
	 * @author jmaravi
	 * @since 22/04/2014
	 */
	public String registroFlujoAprobadoresMasivo(DynaBean dbean) throws MantenimientoException {

		String res = Constantes.OK;
		if (log.isDebugEnabled()) log.debug("Ingreso a registroFlujoAprobadoresMasivo-delegate");

		
		try {
			
						
			if (log.isDebugEnabled()) log.debug("Va a ingresar a registroFlujoAprobadoresMasivo-facade");
			
			if (log.isDebugEnabled()) log.debug("dbean: "+dbean);
			
			mantenimiento.registroFlujoAprobadoresMasivo(dbean);
			
			if (log.isDebugEnabled()) log.debug("Salio de registroFlujoAprobadoresMasivo-facade");
			

		} catch (IncompleteConversationalState e) {
			
			throw new MantenimientoException(e.getBeanMensaje());
			
		} catch (RemoteException e) {
			
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
				
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al realizar la carga del archivo.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new MantenimientoException(beanM);
		}

		return res;
	}
	
    /**
     * Registra el flujo de aprobadores registrados por el usuario
     * @param datos Mapa con los parámetros necesarios para ejecutar el procedimiento
     * @return 
     * @throws MantenimientoException
     * @author jmaravi
     * @since 13/03/2014
     */
    public ArrayList registrarFlujosAprobadores(String dbpool, HashMap datos) throws MantenimientoException {

        ArrayList movimientos = null;
        try {
            movimientos = mantenimiento.registrarFlujosAprobadores(dbpool, datos);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al buscar los movimientos.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return movimientos;
    }
    
    
    
    public void asignarPeriodos(Map params, ArrayList listaPeriodos)
            throws MantenimientoException {

        try {
            mantenimiento.asignarPeriodos(params, listaPeriodos);
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al asignar los periodos.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return;
    }
    
}