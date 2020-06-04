package pe.gob.sunat.sp.asistencia.ejb;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;//ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA

import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.servlet.RequestDispatcher;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pe.gob.sunat.batch.dao.QueueDAO;
import pe.gob.sunat.framework.core.bean.MensajeBean;
import pe.gob.sunat.framework.core.bean.ParamBean;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.core.ejb.FacadeException;
import pe.gob.sunat.framework.core.pattern.DynaBean;
import pe.gob.sunat.framework.core.pattern.ServiceLocator;
import pe.gob.sunat.framework.util.Propiedades;
import pe.gob.sunat.framework.util.cache.dao.ParamDAO;
import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.sa.dao.T34DAO;
import pe.gob.sunat.sol.BeanFechaHora;
import pe.gob.sunat.sol.BeanMensaje;
import pe.gob.sunat.sol.IncompleteConversationalState;
import pe.gob.sunat.sol.seguridad.acceso.portal.bean.BeanUsuario;
import pe.gob.sunat.sp.asistencia.bean.BeanPeriodo;
import pe.gob.sunat.sp.asistencia.bean.BeanTipoMovimiento;
import pe.gob.sunat.sp.asistencia.bean.BeanTipoReloj;
import pe.gob.sunat.sp.asistencia.bean.BeanTurno;
import pe.gob.sunat.sp.asistencia.dao.ReporteDAO;
import pe.gob.sunat.sp.asistencia.dao.T1276DAO;
import pe.gob.sunat.sp.asistencia.dao.T1277DAO;
import pe.gob.sunat.sp.asistencia.dao.T1279DAO;
import pe.gob.sunat.sp.asistencia.dao.T1280DAO;
import pe.gob.sunat.sp.asistencia.dao.T1281DAO;
//import pe.gob.sunat.sp.asistencia.dao.T1455DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T1278DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T1282DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T1455DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T72DAO;
import pe.gob.sunat.sp.asistencia.dao.T1480DAO;
import pe.gob.sunat.sp.asistencia.dao.T1485DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T1933DAO;
import pe.gob.sunat.rrhh.dao.RegistroSirhDAO;
import pe.gob.sunat.rrhh.dao.T1595DAO;
import pe.gob.sunat.sp.asistencia.dao.T45DAO;
import pe.gob.sunat.sp.bean.BeanT12;
import pe.gob.sunat.sp.bean.BeanT99;
import pe.gob.sunat.sp.dao.T02DAO;
import pe.gob.sunat.sp.dao.T12DAO;
import pe.gob.sunat.sp.dao.T99DAO;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;
import pe.gob.sunat.utils.web.MessagesWeb;
import pe.gob.sunat.tecnologia.menu.bean.UsuarioBean;
//ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA
import pe.gob.sunat.rrhh.asistencia.dao.T4635DAO;
import pe.gob.sunat.rrhh.asistencia.dao.T4636DAO;
import pe.gob.sunat.framework.util.lang.Numero;
//FIN ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA

import pe.gob.sunat.sp.asistencia.ejb.ProcesoFacade;

/**
 * 
 * @ejb.bean name="MantenimientoFacadeEJB"
 *           description="MantenimientoFacade"
 *           jndi-name="ejb/rrhh/facade/sp/asistencia/MantenimientoFacadeEJB"
 *           type="Stateless" view-type="remote"
 * 
 * @ejb.home remote-class="pe.gob.sunat.sp.asistencia.ejb.MantenimientoFacadeHome"
 *           extends="javax.ejb.EJBHome"
 * 
 * @ejb.interface remote-class="pe.gob.sunat.sp.asistencia.ejb.MantenimientoFacadeRemote"
 *                extends="javax.ejb.EJBObject"
 * 
 * @ejb.transaction-type type="Container"
 * 
 * @ejb.resource-ref res-ref-name="jdbc/dgsp" res-type="javax.sql.DataSource" res-auth="Container"
 * @weblogic.resource-description res-ref-name="jdbc/dgsp" jndi-name="jdbc/dgsp"
 * 
 * @ejb.resource-ref res-ref-name="jdbc/dcsp" res-type="javax.sql.DataSource" res-auth="Container"
 * @weblogic.resource-description res-ref-name="jdbc/dcsp" jndi-name="jdbc/dcsp"
 * 
 * @jboss.container-configuration name="Standard Stateless SessionBean"
 * @jboss.version="3.2" 
 * @weblogic.enable-call-by-reference True
 * @weblogic.clustering home-is-clusterable="True" stateless-bean-is-clusterable="True"
 * @weblogic.transaction-descriptor trans-timeout-seconds = "300"
 * @weblogic.cache max-beans-in-cache="100"
 * @weblogic.pool max-beans-in-free-pool="100" initial-beans-in-free-pool="0"
 * 
 * @version 1.0
 */
public class MantenimientoFacade implements SessionBean {

	private final Log log = LogFactory.getLog(getClass());
	private SessionContext sessionContext;
	
	//JRR - 07/05/2010
	ServiceLocator sl = ServiceLocator.getInstance();
	
	Propiedades constantes = new Propiedades(getClass(), "/constantes.properties");

	public void ejbCreate() {		
	}

    public void ejbRemove() {
    }

    public void ejbActivate() {
    }

    public void ejbPassivate() {
    }

    public void setSessionContext(SessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public ArrayList cargarRelojes(String dbpool, String estado)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        ArrayList relojes = null;
        try {

            T1280DAO dao = new T1280DAO();
            relojes = dao.findByEstId(dbpool, estado);
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }
        return relojes;
    }

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public ArrayList buscarRelojes(String dbpool, String criterio, String valor)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        ArrayList relojes = null;
        try {

            T1280DAO dao = new T1280DAO();

            if (criterio.equals("-1")) {
                relojes = dao.findByEstId(dbpool, Constantes.ACTIVO);
            } else {
                relojes = dao.findByCritVal(dbpool, criterio, valor);
            }

        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return relojes;
    }

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
    public void eliminarRelojes(String[] params, ArrayList listaRelojes)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        try {

            T1280CMPHome cmpHome = (T1280CMPHome) sl.getLocalHome(T1280CMPHome.JNDI_NAME);

            if (params != null) {
                BeanTipoReloj reloj = null;
                T1280CMPLocal cmpLocal = null;
                for (int i = 0; i < params.length; i++) {

                    reloj = (BeanTipoReloj) listaRelojes
                            .get(Integer.parseInt(params[i]));
                    cmpLocal = cmpHome.findByPrimaryKey(new T1280CMPPK(reloj.getReloj()));
                    cmpLocal.setEstId(Constantes.INACTIVO);
                }
            }
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

    }
    
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
    public void eliminarAprobadores(String[] params, ArrayList lista)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        try {
                       
        	T1480CMPHome cmpHome = (T1480CMPHome) sl.getLocalHome(T1480CMPHome.JNDI_NAME);

            if (params != null) {
            	HashMap aprob = null;
                T1480CMPLocal cmpLocal = null;
                for (int i = 0; i < params.length; i++) {

                	aprob = (HashMap) lista.get(Integer.parseInt(params[i]));
                    cmpLocal = cmpHome.findByPrimaryKey(
                    		new T1480CMPPK(
                    			(String)aprob.get("cod_uorg"),	
								(String)aprob.get("cod_pers_ori"),
								(String)aprob.get("accion_id"),
								(String)aprob.get("mov")
                    		)
							);
                    cmpLocal.remove();
                }
            }
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

    }    

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
    public void registrarReloj(String reloj, String descripcion, String sede,
            String usuario) throws IncompleteConversationalState,
            RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        try {
            
            T1280CMPHome cmpHome = (T1280CMPHome) sl.getLocalHome(T1280CMPHome.JNDI_NAME);

            try {
                T1280CMPLocal cmpLocal = cmpHome.findByPrimaryKey(new T1280CMPPK(reloj));

                if (cmpLocal.getEstId().trim().equals(Constantes.ACTIVO)) {
                    throw new IncompleteConversationalState(
                            "El reloj con codigo: "
                                    + reloj
                                    + " ya se encuentra registrado en la base de datos");
                }

                cmpLocal.setDescrip(descripcion.trim());
                cmpLocal.setSede(sede.trim());
                cmpLocal.setEstId(Constantes.ACTIVO);
                cmpLocal.setFgraba(new java.sql.Timestamp(System
                        .currentTimeMillis()));
                cmpLocal.setCuser(usuario);
            } catch (Exception e) {
                //insertamos el registro
                cmpHome.create(reloj,
                        descripcion, sede, Constantes.ACTIVO,
                        new java.sql.Timestamp(System.currentTimeMillis()),
                        usuario);
            }

        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

    }

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public ArrayList buscarPeriodos(String dbpool, String criterio, String valor)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        ArrayList periodos = null; //Listado de periodos a devolver
        try {

            T1276DAO dao = new T1276DAO();

            if (criterio.equals("-1")) {
                periodos = dao.findByEstId(dbpool, Constantes.ACTIVO);
            } else {
                periodos = dao.findByCritVal(dbpool, criterio, valor);
            }

        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return periodos;
    }


	/**
	 * Metodo encargado de realizar la eliminacion de periodos
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 * @param params
	 * @param listaPeriodos
	 * @return
	 * @throws RemoteException
	 */    
    public void eliminarPeriodos(String[] params, ArrayList listaPeriodos)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        try {
            
            //T1276CMPHome cmpHome = (T1276CMPHome) ServiceLocator.getInstance().getLocalHome(T1276CMPHome.JNDI_NAME);
            //JRR - 23/03/2010
            pe.gob.sunat.rrhh.asistencia.dao.T1276DAO t1276dao = new pe.gob.sunat.rrhh.asistencia.dao.T1276DAO("jdbc/dgsp");

            if (params != null) {
                BeanPeriodo periodo = null;
                //T1276CMPLocal cmpLocal = null;
                for (int i = 0; i < params.length; i++) {

                    periodo = (BeanPeriodo) listaPeriodos
                            .get(Integer.parseInt(params[i]));
                    /*cmpLocal = cmpHome.findByPrimaryKey(new T1276CMPPK(periodo.getPeriodo()));
                    cmpLocal.setEstId(Constantes.INACTIVO);*/
                    t1276dao.eliminaPeriodo(periodo.getPeriodo());
                }
            }
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

    }

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public ArrayList cargarPeriodos(String dbpool)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        ArrayList periodos = null; //Listado de periodos a devolver
        try {

            T1276DAO dao = new T1276DAO();
            periodos = dao.findByEstId(dbpool, Constantes.ACTIVO);
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return periodos;
    }

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 * 
	 * @param params Map
	 * @return 
	 * @throws FacadeException
	 */
    public void registrarPeriodo(Map params)
    //public void registrarPeriodo(String dbpool, String periodo, String fechaIni, String fechaFin, String fechaCierre, String usuario)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        try {
        	String fechaIni = params.get("finicio").toString().substring(6,10) + "/" + params.get("finicio").toString().substring(3,5) + "/" + params.get("finicio").toString().substring(0,2);
            String fechaFin = params.get("ffin").toString().substring(6,10) + "/" + params.get("ffin").toString().substring(3,5) + "/" + params.get("ffin").toString().substring(0,2);
            T1276DAO daoPeriodo = new T1276DAO();
            //JRR - 12/03/2010
            pe.gob.sunat.rrhh.asistencia.dao.T1276DAO t1276dao = new pe.gob.sunat.rrhh.asistencia.dao.T1276DAO(params.get("pool_sp_g"));
            T1278DAO t1278dao = new T1278DAO(params.get("pool_sp_g"));
            
       		if (daoPeriodo.findByFecIniFecFinCodigo(params.get("pool_sp_g").toString(), fechaIni,
            			fechaFin, "")) {
            		throw new IncompleteConversationalState(
            				"El periodo que intenta registrar"
            				+ " presenta cruces de fechas con periodos ya registrados.");
            	} 
            	
            	Map m_periodo = t1276dao.findByPrimaryKey(params.get("periodo").toString());
            	
            	//Si existe el periodo
            	if (m_periodo!= null) {
            		
            		if (m_periodo.get("est_id").equals(Constantes.ACTIVO)) {
                		throw new IncompleteConversationalState(
                				"El periodo que intenta agregar ya se"
                				+ " encuentra registrado en la base de datos.");
            		} else {
            			//Modificamos si estaba de baja	(estado 0)
            			t1276dao.updatePeriodo(params);
            			
            			//Actualizamos el estado de todos los periodos por area del periodo modificado
            			daoPeriodo.UpdateT1278EstIdByPeriodo(params.get("pool_sp_g").toString(), params.get("periodo").toString(), Constantes.ACTIVO);
            			return;
            		}
            		
            	}
            	else {
            		
                	t1276dao.insertPeriodo(params);

                	T12DAO dao = new T12DAO();
                	ArrayList listaUO = dao.findByCodDesc(params.get("pool_sp_g").toString(), "", "", null);

                	if (listaUO != null) {
                		BeanT12 uo = null;
                		for (int i = 0; i < listaUO.size(); i++) {

                			uo = (BeanT12) listaUO.get(i);

                			try {
                				params.put("u_organ", uo.getT12cod_uorgan());	
                				t1278dao.insertPeriodoArea(params);

                			} catch (Exception Ex) {
                				log.debug(Ex.getMessage());
                			}
                		}
                	}            		
            		
            	}
           		
            	/*T1276CMPHome cmpHome = (T1276CMPHome) ServiceLocator.getInstance().getLocalHome(T1276CMPHome.JNDI_NAME);
            	T1278CMPHome cmpHomeDet = (T1278CMPHome) ServiceLocator.getInstance().getLocalHome(T1278CMPHome.JNDI_NAME);
            	T1276DAO daoPeriodo = new T1276DAO();

            	T1276CMPLocal cmpLocal = cmpHome.findByPrimaryKey(new T1276CMPPK(periodo));

                if (cmpLocal.getEstId().trim().equals(Constantes.ACTIVO)) {
                    throw new IncompleteConversationalState(
                            "El periodo que intenta agregar ya se"
                                    + " encuentra registrado en la base de datos.");

                }
            	
            	cmpLocal.setFinicio(fechaIni);
                cmpLocal.setFfin(fechaFin);
                cmpLocal.setFcierre(fechaCierre);
                cmpLocal.setEstId(Constantes.ACTIVO);
                cmpLocal.setFgraba(new java.sql.Timestamp(System.currentTimeMillis()));
                cmpLocal.setCuser(usuario);


            	
            } catch (Exception e) {
            	//insertamos el registro
            	cmpHome.create(periodo,
                        fechaIni, fechaFin, fechaCierre, Constantes.ACTIVO,
                        new java.sql.Timestamp(System.currentTimeMillis()),
                        usuario);

            	t1276dao.insertPeriodo(params);

            	T12DAO dao = new T12DAO();
            	ArrayList listaUO = dao.findByCodDesc(params.get("pool_sp_g").toString(), "", "", null);

            	if (listaUO != null) {
            		BeanT12 uo = null;
            		for (int i = 0; i < listaUO.size(); i++) {

            			uo = (BeanT12) listaUO.get(i);

            			try {
            				cmpHomeDet.create(periodo, uo.getT12cod_uorgan(),
                                            Constantes.ACTIVO,
                                            new Timestamp(System
                                                    .currentTimeMillis()),
                                            usuario);

            				params.put("u_organ", uo.getT12cod_uorgan());	
            				t1278dao.insertPeriodoArea(params);

            			} catch (Exception Ex) {
            				log.debug(Ex.getMessage());
            			}
            		}
            	}
            } */
            	
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

    }

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public ArrayList buscarTurnos(String dbpool, String criterio, String valor)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        ArrayList turnos = null; //Listado de turnos a devolver
        try {

            T45DAO dao = new T45DAO();

            //Buscar todos
            if (criterio.equals("-1")) {
                turnos = dao.findByEstId(dbpool, Constantes.ACTIVO);
            } else {
                turnos = dao.findByCritVal(dbpool, criterio, valor);
            }
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return turnos;
    }

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
    public void eliminarTurnos(String[] params, ArrayList listaTurnos)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        try {
            
            T45CMPHome cmpHome = (T45CMPHome) sl.getLocalHome(T45CMPHome.JNDI_NAME);

            if (params != null) {
                BeanTurno turno = null;
                T45CMPLocal cmpLocal = null;
                for (int i = 0; i < params.length; i++) {

                    turno = (BeanTurno) listaTurnos.get(Integer
                            .parseInt(params[i]));
                    cmpLocal = cmpHome.findByPrimaryKey(new T45CMPPK(turno.getCodTurno()));
                    cmpLocal.setEstId(Constantes.INACTIVO);
                }
            }
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

    }

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public ArrayList cargarTurnos(String dbpool)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        ArrayList turnos = null; //Listado de turnos a devolver
        try {

            T45DAO dao = new T45DAO();
            turnos = dao.findByEstId(dbpool, Constantes.ACTIVO);
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return turnos;
    }

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
    public void registrarTurno(String codTurno, String descripcion,
            String fechaIni, String horaIni, String fechaFin, String horaFin,
            String dias, String tolerancia, String horaLimite, String operId,
            String refrIni, String refrFin, String refrMin, String usuario,
            String control) throws IncompleteConversationalState,
            RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        try {
            
            T45CMPHome cmpHome = (T45CMPHome) sl.getLocalHome(T45CMPHome.JNDI_NAME);

            try {
                T45CMPLocal cmpLocal = cmpHome.findByPrimaryKey(new T45CMPPK(codTurno
                        .trim().toUpperCase()));

                if (cmpLocal.getEstId().trim().equals(Constantes.ACTIVO)) {
                    throw new IncompleteConversationalState(
                            "El turno con codigo: "
                                    + codTurno
                                    + " ya se encuentra registrado en la base de datos.");
                }

                cmpLocal.setDesTurno(descripcion.trim());
                cmpLocal.setFInicio(fechaIni);
                cmpLocal.setHInicio(horaIni);
                cmpLocal.setFFfin(fechaFin);
                cmpLocal.setHFin(horaFin);
                cmpLocal.setEstId(Constantes.ACTIVO);
                cmpLocal.setDiasInt(dias);
                cmpLocal.setTolera(tolerancia);
                cmpLocal.setHLimit(horaLimite);
                cmpLocal.setFGraba(new java.sql.Timestamp(System
                        .currentTimeMillis()));
                cmpLocal.setRefrIni(refrIni);
                cmpLocal.setRefrFin(refrFin);
                cmpLocal.setRefrMin(refrMin);
                cmpLocal.setCuser(usuario);
                cmpLocal.setOperId(operId);
                cmpLocal.setControlId(control);

            } catch (Exception e) {
                //insertamos el registro
                cmpHome.create(codTurno.trim()
                        .toUpperCase(), descripcion, fechaIni, horaIni,
                        fechaFin, horaFin, Constantes.ACTIVO, dias, tolerancia,
                        horaLimite, operId, new java.sql.Timestamp(System
                                .currentTimeMillis()), refrIni, refrFin,
                        refrMin, usuario, control);
            }

        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

    }

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public ArrayList buscarMovimientos(String dbpool, String criterio,
            String valor, String tipo) throws IncompleteConversationalState,
            RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        ArrayList movimientos = null; //Listado de movimientos a devolver
        try {

            T1279DAO dao = new T1279DAO();

            //Buscar todos
            if (criterio.equals("-1")) {
                movimientos = dao.joinWithT99T99ByEstadoTipo(dbpool,
                        Constantes.ACTIVO, tipo);
            } else {
                movimientos = dao.joinWithT99T99ByCritValTipo(dbpool, criterio,
                        valor, tipo);
            }
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return movimientos;
    }

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
    public void eliminarMovimientos(String[] params, ArrayList listaMovimientos)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        try {
            
            T1279CMPHome cmpHome = (T1279CMPHome) sl.getLocalHome(T1279CMPHome.JNDI_NAME);

            if (params != null) {
                BeanTipoMovimiento movimiento = null;
                T1279CMPLocal cmpLocal = null;
                for (int i = 0; i < params.length; i++) {

                    movimiento = (BeanTipoMovimiento) listaMovimientos
                            .get(Integer.parseInt(params[i]));
                    cmpLocal = cmpHome.findByPrimaryKey(new T1279CMPPK(movimiento.getMov()));
                    cmpLocal.setEstId(Constantes.INACTIVO);

                }
            }
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

    }

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public ArrayList cargarMovimientos(String dbpool, String tipo)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        ArrayList movimientos = null; //Listado de movimientos a devolver
        try {

            T1279DAO dao = new T1279DAO();
            movimientos = dao.joinWithT99T99ByEstadoTipo(dbpool,
                    Constantes.ACTIVO, tipo);
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return movimientos;
    }

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
    public void registrarMovimiento(String codMovimiento, String descripcion,
            String califica, String medida, String tipo,
            java.sql.Timestamp fechaIni, java.sql.Timestamp fechaFin,
            String soliId, String vbRrHh, String procId, String entrId,
            String refrId, String saliId, String diasAntes, String diasDespues,
            String diasAcum, String obligId, String qvalida, String ind_dias,
            String ind_proc, String usuario)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        try {
            
            T1279CMPHome cmpHome = (T1279CMPHome) sl.getLocalHome(T1279CMPHome.JNDI_NAME);

            try {
                T1279CMPLocal cmpLocal = cmpHome
                        .findByPrimaryKey(new T1279CMPPK(codMovimiento.trim().toUpperCase()));

                if (cmpLocal.getEstId().trim().equals(Constantes.ACTIVO)) {
                    throw new IncompleteConversationalState(
                            "El movimiento con codigo "
                                    + codMovimiento
                                    + " ya se encuentra registrado en la base de datos.");
                }

                cmpLocal.setDescrip(descripcion.trim());
                cmpLocal.setCalifica(califica.trim());
                cmpLocal.setMedida(medida.trim());
                cmpLocal.setEstId(Constantes.ACTIVO);
                cmpLocal.setTipoId(tipo.trim());
                cmpLocal.setFini(fechaIni);
                cmpLocal.setFfin(fechaFin);

                cmpLocal.setSoliId(soliId);
                cmpLocal.setRrhhId(vbRrHh);
                cmpLocal.setProcId(procId);
                cmpLocal.setEntrId(entrId);
                cmpLocal.setRefrId(refrId);
                cmpLocal.setSaliId(saliId);
                cmpLocal.setIndDias(ind_dias);
                cmpLocal.setIndProc(ind_proc);

                cmpLocal.setDiasAntes(new Integer(diasAntes));
                cmpLocal.setDiasDespues(new Integer(diasDespues));
                cmpLocal.setDiasAcum(new Integer(diasAcum));
                cmpLocal.setQValida(new Integer(qvalida).intValue());
                cmpLocal.setFgraba(new java.sql.Timestamp(System.currentTimeMillis()));
                cmpLocal.setCuser(usuario);
                
            } catch (Exception e) {
            	
                //insertamos el registro
                cmpHome.create(codMovimiento
                        .trim().toUpperCase(), descripcion, califica, medida,
                        Constantes.ACTIVO, tipo, fechaIni, fechaFin, soliId,
                        vbRrHh, procId, entrId, refrId, saliId, new Integer(
                                diasAntes), new Integer(diasDespues),
                        new Integer(diasAcum), obligId, new java.sql.Timestamp(
                                System.currentTimeMillis()), usuario, ind_dias);

            }

        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

    }

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public ArrayList cargarT99(String dbpool, String codTab)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        ArrayList listaT99 = new ArrayList();
        try {

            T99DAO dao = new T99DAO();
            listaT99 = dao.findByCodTab(dbpool, "", "", codTab);

        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return listaT99;
    }
    
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public ArrayList cargarT99(String dbpool, String codTab, String modulo)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        ArrayList listaT99 = new ArrayList();
        try {

            T99DAO dao = new T99DAO();
            listaT99 = dao.findByCodTab(dbpool, "", "", codTab, modulo);

        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return listaT99;
    }    

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public void desactivarDetallePeriodo(ArrayList lista, String[] checks,
            String periodo, int inicio, int fin)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        try {

            String check = "";
            BeanT12 u = null;
            for (int i = inicio; i < fin; i++) {
                u = (BeanT12) lista.get(i);
                check = checks[i];
                //desactivamos el detalle
                desactivarDetalle(periodo, u.getT12cod_uorgan(), u
                        .getT12ind_estad(), check);
            }
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }
    }

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public void desactivarDetalle(String periodo, String uOrg, String uEstado,
            String check) throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        try {
            
            T1278CMPHome cmpHome = (T1278CMPHome) sl.getLocalHome(T1278CMPHome.JNDI_NAME);

            T1278CMPLocal cmpLocal = cmpHome
                    .findByPrimaryKey(new T1278CMPPK(periodo, uOrg));

            if (check == null) {
                if (uEstado.equals(Constantes.ACTIVO)) {
                    cmpLocal.setEstId(Constantes.INACTIVO);
                }
            } else {
                if (uEstado.equals(Constantes.INACTIVO)) {
                    cmpLocal.setEstId(Constantes.ACTIVO);
                }
            }

        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

    }

    
    //JRR - 11 ABRIL 2008
    /*    public void modificarMovimiento(String codMovimiento, String descripcion,
    String califica, String medida, String tipo,
    java.sql.Timestamp fechaIni, java.sql.Timestamp fechaFin,
    String soliId, String vbRrHh, String procId, String entrId,
    String refrId, String saliId, String diasAntes, String diasDespues,
    String diasAcum, String obligId, String qvalida, String usuario, 
	String ind_dias, String ind_proc,
	String ip, String pc, String razon, String cod_usuario) */    
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
    public void modificarMovimiento(Map params, java.sql.Timestamp fechaIni, java.sql.Timestamp fechaFin)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        try {
            
            T1279CMPHome cmpHome = (T1279CMPHome) sl.getLocalHome(T1279CMPHome.JNDI_NAME);
            T1279CMPLocal cmpLocal = cmpHome.findByPrimaryKey(new T1279CMPPK(params.get("codMovimiento").toString()));
            //T1279CMPLocal cmpLocal = cmpHome.findByPrimaryKey(new T1279CMPPK(codMovimiento));

            //(Timestamp)params.get("fechaIni");
            
            //actualizamos los datos del movimiento
            cmpLocal.setDescrip(params.get("descripcion").toString());//cmpLocal.setDescrip(descripcion);
            cmpLocal.setCalifica(params.get("califica").toString());//cmpLocal.setCalifica(califica);
            cmpLocal.setMedida(params.get("medida").toString());//cmpLocal.setMedida(medida.trim());
            cmpLocal.setTipoId(params.get("tipo").toString());//cmpLocal.setTipoId(tipo.trim());
            cmpLocal.setFini(fechaIni); 
            cmpLocal.setFfin(fechaFin);
            cmpLocal.setSoliId(params.get("soliId").toString());//cmpLocal.setSoliId(soliId);
            cmpLocal.setRrhhId(params.get("vbRrHh").toString());//cmpLocal.setRrhhId(vbRrHh);
            cmpLocal.setProcId(params.get("procId").toString());//cmpLocal.setProcId(procId);
            cmpLocal.setEntrId(params.get("entrId").toString());//cmpLocal.setEntrId(entrId);
            cmpLocal.setRefrId(params.get("refrId").toString());//cmpLocal.setRefrId(refrId);
            cmpLocal.setSaliId(params.get("saliId").toString());//cmpLocal.setSaliId(saliId);
            cmpLocal.setDiasAntes(new Integer(params.get("diasAntes").toString()));//cmpLocal.setDiasAntes(new Integer(diasAntes));
            cmpLocal.setDiasDespues(new Integer(params.get("diasDespues").toString()));//cmpLocal.setDiasDespues(new Integer(diasDespues));
            cmpLocal.setDiasAcum(new Integer(params.get("diasAcum").toString()));//cmpLocal.setDiasAcum(new Integer(diasAcum));
            cmpLocal.setQValida(new Integer(params.get("qvalida").toString()).intValue());//cmpLocal.setQValida(new Integer(qvalida).intValue());
            cmpLocal.setObligId(params.get("obligId").toString());//cmpLocal.setObligId(obligId);
            cmpLocal.setIndDias(params.get("ind_dias").toString());//cmpLocal.setIndDias(ind_dias);
            cmpLocal.setIndProc(params.get("ind_proc").toString());//cmpLocal.setIndProc(ind_proc);
            cmpLocal.setCuser(params.get("usuario").toString());//cmpLocal.setCuser(usuario);
            cmpLocal.setFgraba(new Timestamp(System.currentTimeMillis()));
            
            /* PARA GRABAR EN LOG*/
            //if (codMovimiento.trim().equals(Constantes.VACACION) || codMovimiento.trim().equals(Constantes.VACACION_VENTA)) {
           	if (params.get("codMovimiento").toString().trim().equals(Constantes.VACACION) || params.get("codMovimiento").toString().trim().equals(Constantes.VACACION_VENTA)) {            
	            //ServiceLocator sl = ServiceLocator.getInstance();
	        	DataSource dgSp = sl.getDataSource("java:comp/env/jdbc/dgsp");//sp de grabacion
				RegistroSirhDAO sirhDAO = new RegistroSirhDAO(dgSp);
				FechaBean fechaBean = new FechaBean();
				HashMap mapa = new HashMap();
	
				mapa.put("codUsu",params.get("cod_usuario").toString());//mapa.put("codUsu",cod_usuario);			
				mapa.put("ip", params.get("ip").toString());//mapa.put("ip", ip);
				mapa.put("pc", params.get("pc").toString());//mapa.put("pc", pc);
				mapa.put("accion", "032");//mapa.put("accion", "032");
				mapa.put("fecha", fechaBean.getSQLDate());//mapa.put("fecha", fechaBean.getSQLDate());
				mapa.put("hora", fechaBean.getHora24()+":"+fechaBean.getMinuto()+":"+fechaBean.getSegundo());//mapa.put("hora", fechaBean.getHora24()+":"+fechaBean.getMinuto()+":"+fechaBean.getSegundo());
				mapa.put("valor", params.get("razon").toString());//mapa.put("valor", razon);
		
				sirhDAO.insertLogSirh(mapa);            
            }

        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

    }

    
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 * 
	 * @param params Map
	 * @return 
	 * @throws FacadeException
	 */
    public void modificarPeriodo(Map params)
    //public void modificarPeriodo(String dbpool, String periodo, String fechaIni, String fechaFin, String fechaCierre)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        try {

            T1276DAO daoPeriodo = new T1276DAO();
            //if (daoPeriodo.findByFecIniFecFinCodigo(dbpool, fechaIni, fechaFin, periodo)) {
           	if (daoPeriodo.findByFecIniFecFinCodigo(params.get("pool_sp_g").toString(), params.get("finicio").toString(),
           			params.get("ffin").toString(), params.get("periodo").toString())) {            	
                throw new IncompleteConversationalState(
                        "El periodo que intenta modificar"
                                + " presenta cruces de fechas con periodos registrados anteriormente.");
            }
           	
          //JVILLACORTA 16/06/2011 - AOM:MODALIDADES FORMATIVAS 
           	if (daoPeriodo.findByFecIniFecFinCodigoCAS(params.get("pool_sp_g").toString(), params.get("fec_ini_cas").toString(),
           			params.get("fec_fin_cas").toString(), params.get("periodo").toString())) {            	
                throw new IncompleteConversationalState(
                        "El periodo que intenta modificar"
                                + " presenta cruces de fechas con periodos registrados anteriormente.");
            }
           	
           	if (daoPeriodo.findByFecIniFecFinCodigoMF(params.get("pool_sp_g").toString(), params.get("fec_ini_mf").toString(),
           			params.get("fec_fin_mf").toString(), params.get("periodo").toString())) {            	
                throw new IncompleteConversationalState(
                        "El periodo que intenta modificar"
                                + " presenta cruces de fechas con periodos registrados anteriormente.");
            }
            //FIN - JVILLACORTA 16/06/2011 - AOM:MODALIDADES FORMATIVAS


/*            
            T1276CMPHome cmpHome = (T1276CMPHome) ServiceLocator.getInstance().getLocalHome(T1276CMPHome.JNDI_NAME);
            T1276CMPLocal cmpLocal = cmpHome.findByPrimaryKey(new T1276CMPPK(periodo));

            //actualizamos los datos del periodo
            fechaIni = Utiles.toYYYYMMDD(fechaIni);
			fechaFin = Utiles.toYYYYMMDD(fechaFin);
			
            cmpLocal.setFinicio(fechaIni);
            cmpLocal.setFfin(fechaFin);
            cmpLocal.setFcierre(fechaCierre);
*/
            
            pe.gob.sunat.rrhh.asistencia.dao.T1276DAO t1276dao = new pe.gob.sunat.rrhh.asistencia.dao.T1276DAO(params.get("pool_sp_g"));
            t1276dao.updatePeriodo(params); 
            //
            
            
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

    }

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
    public void modificarReloj(String reloj, String descripcion, String sede)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        try {
            
            T1280CMPHome cmpHome = (T1280CMPHome) sl.getLocalHome(T1280CMPHome.JNDI_NAME);

            T1280CMPLocal cmpLocal = cmpHome.findByPrimaryKey(new T1280CMPPK(reloj));

            //actualizamos los datos del reloj
            cmpLocal.setDescrip(descripcion);
            cmpLocal.setSede(sede);

        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

    }

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
    public void modificarTurno(String codTurno, String descripcion,
            String fechaIni, String horaIni, String fechaFin, String horaFin,
            String dias, String tolerancia, String horaLimite, String refrIni,
            String refrFin, String refrMin, String control)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        try {

            T45CMPHome cmpHome = (T45CMPHome) sl.getLocalHome(T45CMPHome.JNDI_NAME);
            T45CMPLocal cmpLocal = cmpHome.findByPrimaryKey(new T45CMPPK(codTurno));

            //actualizamos los datos del turno
            cmpLocal.setDesTurno(descripcion);
            cmpLocal.setFInicio(fechaIni);
            cmpLocal.setFFfin(fechaFin);
            cmpLocal.setHInicio(horaIni);
            cmpLocal.setHFin(horaFin);
            cmpLocal.setDiasInt(dias);
            cmpLocal.setTolera(tolerancia);
            cmpLocal.setHLimit(horaLimite);
            cmpLocal.setRefrIni(refrIni);
            cmpLocal.setRefrFin(refrFin);
            cmpLocal.setRefrMin(refrMin);
            cmpLocal.setControlId(control);

        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

    }

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public ArrayList buscarTipoMovimientos(String dbpool, String tipo,
            String estado, String fIni, String fFin)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        ArrayList lista = null;
        try {

            T1279DAO dao = new T1279DAO();
            lista = dao.findByTipoIdEstIdFecIniFecFin(dbpool, tipo, estado,
                    fIni, fFin);

        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return lista;
    }

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public ArrayList buscarPeriodosEstado(String dbpool, String estado)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        ArrayList lista = null;
        try {

            T1276DAO dao = new T1276DAO();
            lista = dao.findByEstId(dbpool, estado);

        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return lista;
    }

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public BeanPeriodo buscarPeriodoCodigo(String dbpool, String periodo)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        BeanPeriodo bean = null;
        try {

            T1276DAO dao = new T1276DAO();
            bean = dao.findByCodigo(dbpool, periodo);

        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return bean;
    }
    
    //ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
    /**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public BeanPeriodo buscarPeriodoByCodigoByEstado(String dbpool, String periodo, String estado)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        BeanPeriodo bean = null;
        try {

            T1276DAO dao = new T1276DAO();
            bean = dao.findByCodigoByEstado(dbpool, periodo, estado);

        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return bean;
    }
    //FIN ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public ArrayList buscarSedes(String dbpool, String estado)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        ArrayList sedes = null;
        try {

            T34DAO dao = new T34DAO();
            sedes = dao.findByEstId(dbpool, estado);
            
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return sedes;
    }

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public ArrayList buscarMovimientosSolicitud(String dbpool, String estado)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        ArrayList lista = null;
        try {

            T1279DAO dao = new T1279DAO();
            lista = dao.findBySoliId(dbpool, estado);
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return lista;
    }

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public boolean periodoCerradoAFecha(String dbpool, String periodo,
            String fecha) throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        boolean cerrado = false;
        try {

            T1276DAO dao = new T1276DAO();
			T72DAO t72dao = new T72DAO(sl.getDataSource("java:comp/env/jdbc/dcsp"));

            BeanPeriodo bean = dao.findByCodigo(dbpool, periodo);
            log.debug("periodo:"+periodo);
            log.debug("fecha:"+fecha);
            log.debug("getFechaCie:"+bean.getFechaCie());
            float dif = Utiles.obtenerDiasDiferencia(fecha, bean.getFechaCie());
            if (dif < 0) {
                cerrado = true;
            }
            //JRR
            else {
            	cerrado = t72dao.verificarEnvioPlanillas(periodo);
            }

        } catch (Exception e) {
        	log.error(e,e);
            //beanM.setMensajeerror("Por favor verifique la existencia del perÃ­odo"); ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
        	beanM.setMensajeerror("Por favor verifique la existencia del período"); //ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return cerrado;

    }
    
    //DTARAZONA 3ER ENTREGABLE ASISTENCIA 28/02/2018
    /**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public HashMap findMaximoVentaVacaciones(String dbpool)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        HashMap lista = null;
        try {

            T99DAO dao = new T99DAO();
            lista = dao.diasVentaVacByTipo(dbpool);
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return lista;
    }
    
    /**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public HashMap findDiasVacacionesVendidas(String dbpool, String codPers,String anno)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        HashMap lista = null;
        try {

            T1282DAO dao = new T1282DAO(dbpool);//aca
            lista = dao.diasVendidosByCodPersAnno(dbpool, codPers, anno);
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return lista;
    }
    /**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public List cargarAnnosVac(String dbpool, String codPers)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        List lista = null;
        try {

            T1282DAO dao = new T1282DAO(dbpool);
            lista = dao.cargarAnnosVac(dbpool, codPers);
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return lista;
    }
    /**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public ArrayList cargarDiasVac(String dbpool)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        ArrayList lista = null;
        try {

            T99DAO dao = new T99DAO();
            lista = dao.cargarDiasVac(dbpool);
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return lista;
    }
    //FIN DTARAZONA
    
    
    //ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII
    /**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public boolean periodoCerradoAFechaPorRegimen(String dbpool, String periodo,
            String fecha,String regimenModalidad) throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        boolean cerrado = false;
        try {

            T1276DAO dao = new T1276DAO();
			T72DAO t72dao = new T72DAO(sl.getDataSource("java:comp/env/jdbc/dcsp"));

            BeanPeriodo bean = dao.findByCodigo(dbpool, periodo);
            log.debug(periodo);
            log.debug(fecha);
            
            float dif = Utiles.obtenerDiasDiferencia(fecha,("09".equals(regimenModalidad)?bean.getFechaCieCAS():"10".equals(regimenModalidad)?bean.getFechaCieModForm():bean.getFechaCie()));
                    
            if (dif < 0) {
                cerrado = true;
            }         
            else {
            	cerrado = t72dao.verificarEnvioPlanillas(periodo);
            }

        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror("Por favor verifique la existencia del perÃ­odo");
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return cerrado;

    }
    //FIN ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII
    
    
    //JROJAS4 - 25/03/2010 
	/**
	 * Metodo encargado de verificar si un periodo CAS esta cerrado
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param mapa
	 * @return
	 * @throws RemoteException
	 */
    public boolean periodoCerradoCAS(Map mProceso) throws RemoteException {

       MensajeBean beanM = new MensajeBean();
        boolean cerrado = false;
        try {

            T1276DAO dao = new T1276DAO();
            BeanPeriodo bean = dao.findByCodigo(mProceso.get("dbpool").toString(), mProceso.get("periodo").toString());
            if (log.isDebugEnabled()) {
                log.debug("Fecha cierre CAS: "+ bean.getFechaCieCAS());
            }
            
            float dif=-1;
            if (bean.getFechaCieCAS()!=null && bean.getFechaCieCAS().trim()!="") 
            	dif = Utiles.obtenerDiasDiferencia(Utiles.obtenerFechaActual(), bean.getFechaCieCAS());
            //else throw new Exception(); //ICAPUNAY - FORMATIVAS 16/06/2011
            else cerrado = true;//ICAPUNAY - FORMATIVAS 16/06/2011
            	
            if (dif < 0) {
                cerrado = true;
            }

        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror("Por favor verifique la configuraciÃ³n del perÃ­odo para el D.L. 1057");
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new FacadeException(this, beanM);
        }

        return cerrado;

    }

    
    //JRR - 20/04/2011 
	/**
	 * Metodo encargado de verificar si un periodo de Modalidad Formativa esta cerrado
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param mapa
	 * @return
	 * @throws RemoteException
	 */
    public boolean periodoCerradoModFormativa(Map mProceso) throws RemoteException {

       MensajeBean beanM = new MensajeBean();
        boolean cerrado = false;
        try {

            T1276DAO dao = new T1276DAO();
            BeanPeriodo bean = dao.findByCodigo(mProceso.get("dbpool").toString(), mProceso.get("periodo").toString());
            if (log.isDebugEnabled()) {
                log.debug("Fecha cierre ModFormativa: "+ bean.getFechaCieModForm());
            }
            
            float dif=-1;
            if (bean.getFechaCieModForm()!=null && bean.getFechaCieModForm().trim()!="") 
            	dif = Utiles.obtenerDiasDiferencia(Utiles.obtenerFechaActual(), bean.getFechaCieModForm());
            //else throw new Exception(); //ICAPUNAY - FORMATIVAS 16/06/2011
            else cerrado = true;//ICAPUNAY - FORMATIVAS 16/06/2011
            	
            if (dif < 0) {
                cerrado = true;
            }

        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror("Por favor verifique la configuraciÃ³n del perÃ­odo para Modalidad Formativa");
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new FacadeException(this, beanM);
        }

        return cerrado;

    }
    
    //ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
    /**
	 * Metodo encargado de verificar si un periodo DL 276-728 esta cerrado
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param mapa
	 * @return
	 * @throws RemoteException
	 */
    public boolean periodoCerrado(Map mProceso) throws RemoteException {

       MensajeBean beanM = new MensajeBean();
        boolean cerrado = false;
        try {

            T1276DAO dao = new T1276DAO();
            BeanPeriodo bean = dao.findByCodigo(mProceso.get("dbpool").toString(), mProceso.get("periodo").toString());
            if (log.isDebugEnabled()) {
                log.debug("Fecha cierre regimen 276-728: "+ bean.getFechaCie());
            }
            
            float dif=-1;
            if (bean.getFechaCie()!=null && bean.getFechaCie().trim()!="") 
            	dif = Utiles.obtenerDiasDiferencia(Utiles.obtenerFechaActual(), bean.getFechaCie());           
            else cerrado = true;
            	
            if (dif < 0) {
                cerrado = true;
            }

        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror("Por favor verifique la configuracion del periodo para el D.L. 276-728");
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new FacadeException(this, beanM);
        }

        return cerrado;

    }
    //ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
    
    
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public String periodoCerradoAFecha(String dbpool, String fecha)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        String periodo = "";
        try {

            T1276DAO dao = new T1276DAO();
            periodo = dao.findByFecha(dbpool, fecha);
            if (!periodo.equals("")) {
                boolean cerrado = this.periodoCerradoAFecha(dbpool, periodo, Utiles.obtenerFechaActual());
                if (!cerrado) {
                    periodo = "";
                }
            } else {
                periodo = "NNNN-NN";
            }
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }
        return periodo;
    }
    
    
    //ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII
    /**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public String periodoCerradoAFechaPorRegimen(String dbpool, String fecha, String regimenModalidad)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        String periodo = "";
        try {

            T1276DAO dao = new T1276DAO();
            if("09".equals(regimenModalidad)){
            	periodo = dao.findByFechaCAS(dbpool, fecha);
            }else if("10".equals(regimenModalidad)){
            		periodo = dao.findByFechaModFormativas(dbpool, fecha);
            		}else{
            			 periodo = dao.findByFecha(dbpool, fecha);
            		}
           
            if (!periodo.equals("")) {
                boolean cerrado = this.periodoCerradoAFechaPorRegimen(dbpool, periodo, Utiles.obtenerFechaActual(),regimenModalidad);
                if (!cerrado) {
                    periodo = "";
                }
            } else {
                periodo = "NNNN-NN";
            }
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }
        return periodo;
    }
    //FIN ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII
    
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public boolean periodoCerradoUOFecha(String dbpool, String fechaPer, String fechaEval, String uo)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        HashMap periodo = null;
        boolean cerrado = false;
        try {

            T1276DAO dao = new T1276DAO();
			T72DAO t72dao = new T72DAO(sl.getDataSource("java:comp/env/jdbc/dcsp"));
            
            periodo = dao.findPeriodoCierre(dbpool, fechaPer, uo);
            
            if (periodo!=null){
            	float dif = Utiles.obtenerDiasDiferencia(fechaEval, (String)periodo.get("fcierre"));
                if (dif < 0) {
                	cerrado = true;
                }
                //JRR
                else {
                	cerrado = t72dao.verificarEnvioPlanillas(periodo.get("periodo").toString().trim());
                }

                if (((String)periodo.get("estado_pa")).equals(Constantes.INACTIVO)) cerrado = true;
            }            
            
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
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
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public boolean periodoCerradoUOFecha_RegimenModalidad(String dbpool, String fechaPer, String fechaEval, String uo,String regimenModalidad)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        HashMap periodo = null;
        boolean cerrado = false;
        log.debug("MantenimientoFacade.periodoCerradoUOFecha_RegimenModalidad... fechaPer: "+fechaPer);
        log.debug("MantenimientoFacade.periodoCerradoUOFecha_RegimenModalidad... fechaEval: "+fechaEval);
        log.debug("MantenimientoFacade.periodoCerradoUOFecha_RegimenModalidad... uo: "+uo);
        log.debug("MantenimientoFacade.periodoCerradoUOFecha_RegimenModalidad... regimenModalidad: "+regimenModalidad);
        try {

            T1276DAO dao = new T1276DAO();
			T72DAO t72dao = new T72DAO(sl.getDataSource("java:comp/env/jdbc/dcsp"));
            
		
			if("09".equals(regimenModalidad)){ //PARA CAS
				periodo = dao.findPeriodoCierreCAS(dbpool, fechaPer, uo);
			}else 
				if("10".equals(regimenModalidad)){ //PARA MODALIDADES FORMATIVAS
					periodo = dao.findPeriodoCierreFormativa(dbpool, fechaPer, uo);
				}else{ //CONTRATO ESPECIFICO Y PLANILLA
						periodo = dao.findPeriodoCierre(dbpool, fechaPer, uo);					
				     }       	
            
			log.debug("MantenimientoFacade.periodoCerradoUOFecha_RegimenModalidad... periodo: "+periodo);
            if (periodo!=null){
            	log.debug("MantenimientoFacade.periodoCerradoUOFecha_RegimenModalidad... (String)periodo.get(fcierre): "+(String)periodo.get("fcierre"));
            	float dif = Utiles.obtenerDiasDiferencia(fechaEval, (String)periodo.get("fcierre"));//periodo.get(fcierre) toma los valores de fcierre o fec_cierre_cas o fec_cierre_mf de acuerdo al regimen/modalidad del colaborador
                if (dif < 0) {
                	cerrado = true;
                }                
                else {
                	log.debug("MantenimientoFacade.periodoCerradoUOFecha_RegimenModalidad... periodo.get(periodo): "+periodo.get("periodo").toString().trim());
                	cerrado = t72dao.verificarEnvioPlanillas(periodo.get("periodo").toString().trim());
                	log.debug("MantenimientoFacade.periodoCerradoUOFecha_RegimenModalidad... cerrado: "+String.valueOf(cerrado));
                }
                if (((String)periodo.get("estado_pa")).equals(Constantes.INACTIVO)) cerrado = true;
            }            
            
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }
        return cerrado;
    }  
    //FIN ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII 

    
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public ArrayList buscarSaldosVacacionales(String dbpool, String registro)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        ArrayList lista = null;
        try {

            T1281DAO dao = new T1281DAO();
            lista = dao.findSaldos(dbpool, registro);

        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return lista;
    }

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 *
    public void actualizarSaldo(String dbpool, HashMap saldo)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        try {

            T99DAO codigoDAO = new T99DAO();

            T1281CMPHome cmpHome = (T1281CMPHome) sl.getLocalHome(T1281CMPHome.JNDI_NAME);

            String diasVaca = codigoDAO.findParamByCodTabCodigo(dbpool,
                    Constantes.CODTAB_PARAMETROS_ASISTENCIA,
                    Constantes.DIAS_VACACIONES);
            int numDiasVaca = diasVaca != null ? Integer.parseInt(diasVaca) : 0;

            String nuevo_saldo = (String) saldo.get("nuevo_saldo");

            if (Integer.parseInt(nuevo_saldo) > numDiasVaca) {
                throw new Exception(
                        "La cantidad de d&iacute;as no debe ser mayor a "
                                + numDiasVaca + ".");
            }

            try {
            	
                T1281CMPLocal cmpLocal = cmpHome
                        .findByPrimaryKey(new T1281CMPPK((String) saldo
                                .get("cod_pers"), (String) saldo.get("anno")));
                cmpLocal.setSaldo(new Integer(nuevo_saldo));
                cmpLocal.setCuserMod((String) saldo.get("usuario"));
                cmpLocal.setFmod(new Timestamp(System.currentTimeMillis()));

                log.info("Actualizacion Saldo Vacacional --> Registro : " +(String) saldo.get("cod_pers")
                        +" | Ano : "+(String) saldo.get("anno")
                        +" | Saldo : "+(String) saldo.get("saldo")
                        +" | Nuevo Saldo : "+(String) saldo.get("nuevo_saldo"));

            } catch (Exception e) {
            	log.error(e,e);
                throw new Exception(
                        "No se encontro el registro de saldo vacacional.");
            }

        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

    }
*/
    
/* JRR - PROGRAMACION - 18/05/2010 */    
    /**
     * Metodo encargado de validar y actualizar el saldo
     * @param params Map, String
     * @return boolean 
     * @throws FacadeException
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */	
  	public boolean validaSaldo(Map saldo)
  	throws FacadeException {
 
  		try {
  			DataSource dbpool_sp = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dcsp");
  			DataSource dbpool_sp_g = ServiceLocator.getInstance().getDataSource("java:comp/env/jdbc/dgsp");
  			pe.gob.sunat.rrhh.dao.T99DAO codigoDAO = new pe.gob.sunat.rrhh.dao.T99DAO(dbpool_sp_g);
  			T1282DAO t1282dao = new T1282DAO(dbpool_sp);
  			pe.gob.sunat.rrhh.asistencia.dao.T1281DAO t1281dao = new pe.gob.sunat.rrhh.asistencia.dao.T1281DAO(dbpool_sp_g);
  			ParamBean paramBean=codigoDAO.buscar(new String[]{
  					constantes.leePropiedad("CODTAB_PARAMETROS_ASISTENCIA")}	, dbpool_sp, constantes.leePropiedad("DIAS_VACACIONES"));
  			int numDiasVaca =0;
  			if(paramBean!=null && paramBean.getDescripcion()!=null){
  				 numDiasVaca = new Integer(paramBean.getDescripcion()).intValue();
  			}
  			String nuevo_saldo = (String) saldo.get("nuevo_saldo");
  			int sumVacEfecComp = t1282dao.findByCodPersAnnoVacEfecComp(saldo);
  			if ((sumVacEfecComp + Integer.parseInt(nuevo_saldo)) > numDiasVaca) {
  				return false;
  			}
  			Map columns= new HashMap();
  			columns.put("saldo",new Integer(nuevo_saldo));
  			columns.put("cuser_mod",(String) saldo.get("usuario"));
  			columns.put("fmod",new FechaBean().getTimestamp());
  			saldo.put("columns",columns);
  			if(!t1281dao.updateCustomColumns(saldo)){
  				return false;
  			}
  			
  			return true;
  		} catch (DAOException e) {
        log.error(e, e);
       	MensajeBean beanM = new MensajeBean();
        beanM.setMensajeerror(e.getMessage());
        beanM.setMensajesol("Por favor intente nuevamente, posible error en la BD");
        throw new FacadeException(this, beanM);
      } catch (Exception e) {
        log.error(e, e);
       	MensajeBean beanM = new MensajeBean();
        beanM.setMensajeerror(e.getMessage());
        beanM.setMensajesol("Por favor intente nuevamente.");
        throw new FacadeException(this, beanM);
      }

  	} 
/*       */    

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public ArrayList buscarResponsablesProceso(String dbpool, String proceso,
            String uorgan) throws IncompleteConversationalState,
            RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        ArrayList lista = null;
        try {

            T1485DAO dao = new T1485DAO();
            lista = dao.findByProcesoUOrgan(dbpool, proceso, uorgan);

        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return lista;
    }

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
    public void eliminarResponsablesProceso(String[] params, ArrayList lista)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        try {
            
            T1485CMPHome cmpHome = (T1485CMPHome) sl.getLocalHome(T1485CMPHome.JNDI_NAME);

            if (params != null) {
                
                HashMap resp = null;
                T1485CMPLocal cmpLocal = null;
                for (int i = 0; i < params.length; i++) {

                    resp = (HashMap) lista.get(Integer.parseInt(params[i]));
                    cmpLocal = cmpHome.findByPrimaryKey(new T1485CMPPK((String) resp
                                    .get("cod_uorg"), (String) resp
                                    .get("cod_pers"), (String) resp
                                    .get("proceso")));
                    cmpLocal.remove();
                }
            }
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

    }

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
    public void registrarResponsable(String dbpool, HashMap datos)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        try {

            T1485CMPHome cmpHome = (T1485CMPHome) sl.getLocalHome(T1485CMPHome.JNDI_NAME);
            String proceso=datos.get("operacion").toString();
            String pool=datos.get("dbpool").toString();
            if(proceso.equals("5")){
            	String mensaje="";
            	for(int j=1;j<=4;j++){
            		String procDesc="";
            		if(j==1)
						procDesc="Proceso de Asistencia";
					if(j==2)
						procDesc="Proceso de Calificación";
					if(j==3)
						procDesc="Proceso de Cierre";
					if(j==4)
						procDesc="Generación de Saldos Vacacionales";
					HashMap datosResp=new HashMap();
					datosResp.put("operacion",""+j);									
					datosResp.put("u_organ",datos.get("u_organ").toString().trim());
					datosResp.put("cod_pers",datos.get("cod_pers").toString().trim());
					datosResp.put("cuser",datos.get("cuser").toString().trim());
					log.debug("Mapa a registrar:"+datosResp);
					boolean res=registrarResponsableMasivo(pool, datosResp);
					String estado="";
					
					if(res) estado="Asignado";
					else estado="El responsable ya ha sido asignado a dicho proceso.";
					
					mensaje=mensaje+j+". "+procDesc+": "+estado+"<br>";
            	}
            	
            	 //throw new Exception(mensaje);
            }else{
            	try {
                    datos.put("fgraba", new Timestamp(System.currentTimeMillis()));
                    cmpHome.create(datos);
                } catch (Exception e) {
                    throw new Exception(
                            "El responsable ya ha sido asignado a dicho proceso.");
                }
            }
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }
    }
//dtarazona
    /**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
    public boolean registrarResponsableMasivo(String dbpool, HashMap datos)
            throws IncompleteConversationalState, RemoteException {
    	boolean result=true;
        BeanMensaje beanM = new BeanMensaje();
        try {

            T1485CMPHome cmpHome = (T1485CMPHome) sl.getLocalHome(T1485CMPHome.JNDI_NAME);
            try {
                datos.put("fgraba", new Timestamp(System.currentTimeMillis()));
                cmpHome.create(datos);
            } catch (Exception e) {
                result=false;
            }

        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }
        return result;
    }
    /**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void asignacionResponsablesMasivo(String dbpool,HashMap params)
			throws IncompleteConversationalState, RemoteException {

		BeanMensaje beanM = new BeanMensaje();
		try {

			QueueDAO qd = new QueueDAO();

			params.put("observacion", "Reporte de asignación de responsables por proceso");
			if(log.isDebugEnabled()) log.debug("EN REGISTRO DEL PROCESO:"+params);
			qd.encolaReporte(ReporteMasivoFacadeHome.JNDI_NAME,
					"asignacionResponsablesMasivo", params,params.get("cuser").toString().trim());
			
		} catch (Exception e) {
			log.error(e,e);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
	}
//fin dtarazona
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public ArrayList buscarAprobadores(HashMap datos)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        ArrayList lista = null;
        try {

            T1480DAO dao = new T1480DAO();
            lista = dao.findAprobadores(datos);

        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return lista;
    }

    
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
    public void registrarAprobador(HashMap datos)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        try {

            T1480CMPHome cmpHome = (T1480CMPHome) sl.getLocalHome(T1480CMPHome.JNDI_NAME);

            try {                
            	T1480CMPLocal cmpLocal = cmpHome
                	.findByPrimaryKey(
                			new T1480CMPPK(
                					(String) datos.get("codUO"),
                					(String) datos.get("cod_pers_ori"),
                                    (String) datos.get("accion"),
                                    (String) datos.get("mov")));

                    cmpLocal.setCodPersDes((String) datos.get("cod_pers_des"));
                    cmpLocal.setInstancia((String) datos.get("instancia"));
                    cmpLocal.setFGraba(new Timestamp(System.currentTimeMillis()));
                    cmpLocal.setCUser((String) datos.get("cuser"));
                
            } catch (Exception e) {
                datos.put("fgraba", new Timestamp(System.currentTimeMillis()));
                cmpHome.create(datos);
            }

        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }
    }

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public ArrayList buscarParametros(String dbpool, String codTab)
            throws IncompleteConversationalState, RemoteException {

        ArrayList codigos = null;
        BeanMensaje beanM = new BeanMensaje();
        try {

            T1279DAO dao = new T1279DAO();
            codigos = dao.findByTipoEstado(dbpool, codTab, Constantes.ACTIVO);

        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return codigos;
    }
    
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public HashMap buscarDelegados(HashMap datos)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        HashMap lista = null;
        try {

            T12DAO daoUO = new T12DAO();
            lista = daoUO.findDelegados(datos);

        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return lista;
    }
    
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
    public void registrarDelegados(ArrayList procesos, HashMap datos, HashMap delegOrig, HashMap delegNuevo)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        try {

        	if (log.isDebugEnabled()) {
        		log.debug("registrarDelegados - procesos: " + procesos);
        		log.debug("registrarDelegados - datos: " + datos);
        		log.debug("registrarDelegados - delegOrig: " + delegOrig);
        		log.debug("registrarDelegados - delegNuevo: " + delegNuevo);
        	}
        	
		 	Map delegKey = new HashMap();
		 	delegKey.put("cod_uorgan", datos.get("codUO"));
		 	delegKey.put("nro_registro_jefe", datos.get("codJefe")); 
        	Map dN = new HashMap();
        	
		 	//JRR - 09/02/2009
		 	T1595DAO t1595dao = new T1595DAO(sl.getDataSource("java:comp/env/jdbc/dgsp"));
		 	List lista = null;
		 	Map mapa = new HashMap();
		 	Map map_aux = null;
		 	Map delegacion = new HashMap();
		 	delegacion.put("cunidad_organ", datos.get("codUO"));
		 	delegacion.put("cod_personal_jefe", datos.get("codJefe"));
		 	delegacion.put("cuser_mod", datos.get("cuser_mod"));
		 	delegacion.put("fmod", new Timestamp(System.currentTimeMillis()));
	 		//
	 		
            if ((procesos!=null) && (procesos.size()>0)){
				for(int i=0; i< procesos.size() ;i++){
					
					BeanT99 proceso = (BeanT99)procesos.get(i);
					dN = (HashMap)delegNuevo.get(proceso.getT99codigo());
					if (log.isDebugEnabled()) {
						log.debug("i: " + i);
						log.debug("proceso.getT99codigo(): " + proceso.getT99codigo());
						log.debug("dN: " + dN);
					}
					
					delegacion.put("cod_opcion", proceso.getT99codigo());
			 		delegKey.put("opcion", proceso.getT99codigo());
			 		map_aux = t1595dao.findByKey(delegKey);
					if (log.isDebugEnabled()) log.debug("map_aux: " + map_aux);
					
					if (dN.get("cod_deleg")!=null) {
						
					 	BeanFechaHora fIni = null;
					 	BeanFechaHora fFin = null;
					 	
					 	if (!((String)dN.get("fini")).equals("")) fIni = new BeanFechaHora((String)dN.get("fini"),"dd/MM/yyyy");
					 	else fIni = new BeanFechaHora();
					 	if (!((String)dN.get("ffin")).equals("")) fFin = new BeanFechaHora((String)dN.get("ffin"),"dd/MM/yyyy");
					 	else fFin = new BeanFechaHora();
					 	
				 		if ((map_aux)!= null && !map_aux.isEmpty()) {
				 			
				 			//eliminamos el delegado
				            if (((String)dN.get("cod_deleg")).equals("")){
				            	t1595dao.eliminarDelegacion(delegacion);
				            } else {
				            	delegacion.put("cod_personal_deleg", ((String)dN.get("cod_deleg")).trim().toUpperCase());
				            	delegacion.put("finivig", fIni.getTimestamp());
				            	delegacion.put("ffinvig", fFin.getTimestamp());
				            	t1595dao.updateDelegacion(delegacion);
				            }
				 		
						} else {	 
			            	delegacion.put("cod_personal_deleg", ((String)dN.get("cod_deleg")).trim().toUpperCase());
			            	delegacion.put("finivig", fIni.getTimestamp());
			            	delegacion.put("ffinvig", fFin.getTimestamp());
			            	delegacion.put("sestado_activo", Constantes.ACTIVO);
							t1595dao.registrarDelegacion(delegacion);
						}
						 
					} else {
						
						if ((map_aux)!= null && !map_aux.isEmpty()) {
							t1595dao.eliminarDelegacion(delegacion);
							
						} else {
						 	//JRR - 06/02/2009
						 	mapa.put("cunidad_organ", datos.get("codUO"));
						 	mapa.put("cod_opcion", proceso.getT99codigo());
						 	lista = t1595dao.findByUOProceso(mapa);
						 	if (log.isDebugEnabled()) log.debug("lista: " + lista);
						 	
						 	if (lista!=null && !lista.isEmpty()) {
						 		t1595dao.eliminarDelegacionByUOProceso(mapa);
						 	}
						 	//
						}
					}
					
				}
			}

        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.toString());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }
    }   
    
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public ArrayList buscarUOsinAprob(String dbpool, String criterio,
			String valor, HashMap seguridad)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        ArrayList lista = null;
        try {

            T12DAO dao = new T12DAO();
            lista = dao.findUOsinAprob(dbpool, criterio, valor, seguridad);

        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return lista;
    }
        
    
    /**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
    public boolean cambiarAprobador(String dbpool, String tipo, String txtUO, String txtOri, String txtDes, String usuario)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        ArrayList lista = null;
        boolean actualiza = false;
        try {

            T1277DAO dao = new T1277DAO();
            T1455DAO seguiDAO = new T1455DAO(dbpool);
            T1480DAO flujoDAO = new T1480DAO();
            
            boolean seguimiento = false;
            lista = dao.findSolicitudesRecibidas(dbpool,txtOri, tipo , "-1", "0");
            txtUO = txtUO.toUpperCase();
            
            boolean flujo = flujoDAO.updateAprobadorFlujoDes(dbpool, tipo, usuario, txtDes, txtOri, txtUO);
            flujo = flujoDAO.updateAprobadorFlujoOri(dbpool, tipo, usuario, txtDes, txtOri, txtUO);
            //lista = dao.findUOsinAprob(dbpool, criterio, valor, seguridad);

            //JRR - 17/10/2008
			Map colums = new HashMap();
			colums.put("cuser_dest", txtDes);
			colums.put("fmod", new FechaBean().getTimestamp());
			colums.put("cuser_mod", usuario);

            if (flujo != false){
            	seguimiento = flujo;
               	if ((lista!=null) && (lista.size()>0)){
               		String uorgan = null;
            		
            		for(int i=0; i< lista.size() ;i++){
            			HashMap mapa = (HashMap)lista.get(i);
            			uorgan = (String) mapa.get("uorgan");
            			if ((txtUO.trim().equals(uorgan.trim())) || (txtUO.trim().equals("txtUO"))){
                			//seguimiento = seguiDAO.updateAprobadorSeg(dbpool, tipo, usuario, txtDes, txtOri, anno, Short.valueOf(numero), uorgan, cod_pers);

            				//JRR - 17/10/2008
            				mapa.put("columns", colums);
            				mapa.put("cod_pers", mapa.get("codPers"));
            				mapa.put("cuser_dest", txtOri);
            				if(log.isDebugEnabled()) log.debug("updateCustomColumns(mapa)... mapa:"+mapa);
                			seguimiento = seguiDAO.updateCustomColumns(mapa);
                			//
            			}
				
            		}
            	}
            }
            
            if (seguimiento){
            	actualiza =true;
            }
			
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return actualiza;
    }

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public ArrayList buscarSupervisores(HashMap datos)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        ArrayList lista = null;
        try {

            T1933DAO daoSup = new T1933DAO();
            lista = daoSup.findBySupervCriterios(datos);

        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return lista;
    }

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
    public void eliminarSupervisores(String dbpool, String[] params, ArrayList lista)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        try {
            T1933DAO t1933 = new T1933DAO();           
        	

            if (params != null) {
            	HashMap superv = null;
            
                for (int i = 0; i < params.length; i++) {

                	superv = (HashMap) lista.get(Integer.parseInt(params[i]));
                    t1933.borrarSupervisor(dbpool,
                    		(String)superv.get("cod_personal"),
                    		(String)superv.get("cod_mov"),
                    		(String)superv.get("cod_uorgan")
               		);
           
                }
            }
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

    }    
    
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
    public void registrarSupervisor(String dbpool, String codMov, String UO, String superv,
            String fechaInicio, String fechaFin, String usuario) throws IncompleteConversationalState,
            RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        T1933DAO t1933 = new T1933DAO();
        try{
        	try {
            
            t1933.insertRegistroSupervisor(dbpool, codMov, UO, superv, fechaInicio, fechaFin, usuario);

            } catch (Exception e) {
                //si existe actualizamos la tupla
                
            }

        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

    }
    
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
    public void modificarSupervisor(String dbpool, HashMap superv, 
            String fechaInicio, String fechaFin, String estado, String usuario) throws IncompleteConversationalState,
            RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        T1933DAO t1933 = new T1933DAO();
        try{
        	try {
            
            t1933.modificaRegistroSupervisor(dbpool, superv, fechaInicio, fechaFin, estado, usuario);

            } catch (Exception e) {
                //si existe actualizamos la tupla
                
            }

        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

    }
    
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public ArrayList buscarTipoMovimientosPerfil(String dbpool, String tipo,
            String estado, String fIni, String fFin, HashMap seguridad)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        ArrayList lista = null;
        try {

            T1279DAO dao = new T1279DAO();
            lista = dao.findByTipoIdEstIdFecIniFecFinPerfil(dbpool, tipo, estado,
                    fIni, fFin, seguridad);

        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return lista;
    }

	//JRR - 10 ABRIL 2008
	/**
	 * Realiza la busqueda de los datos de un tipo de Movimiento
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * 
	 * @param dbpool String
	 * @param tipoMov String
	 * @return Map
	 * @throws RemoteException
	 */
    public Map cargarFindByMov(String dbpool, String tipoMov)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        Map hmMov = new HashMap();
        try {

            T1279DAO dao = new T1279DAO();
            hmMov = dao.findByMov(dbpool, tipoMov);

        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return hmMov;
    }  	 
    
    /**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public Map validarSolicitud(String dbpool, String tipo) throws IncompleteConversationalState,
			RemoteException {

		Map t = null;
		BeanMensaje beanM = new BeanMensaje();
		try {			
			//if(log.isDebugEnabled()) log.debug("Llego a validar");			
			pe.gob.sunat.rrhh.dao.T99DAO dao = new pe.gob.sunat.rrhh.dao.T99DAO(dbpool);
			//obtenemos los datos del tipo de solicitud
			t = dao.findTipoSolicitudCAS(tipo);
			if(log.isDebugEnabled()) log.debug("tiposol:"+t);
			
		} catch (Exception e) {
			log.error(e,e);			
			beanM.setMensajeerror(e.toString());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		
		return t;
	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */	 
	public boolean validarLicencia(String dbpool, String tipo, String valor) throws IncompleteConversationalState,
			RemoteException {

		Map t = null;
		boolean v;
		BeanMensaje beanM = new BeanMensaje();
		
		try {								
			//pe.gob.sunat.rrhh.dao.T02DAO t02dao = new pe.gob.sunat.rrhh.dao.T02DAO(dbpool);			
			T02DAO t02dao = new T02DAO();
			HashMap regimen = t02dao.joinWithT12T99ByCodPers(dbpool, valor, null);
			if(log.isDebugEnabled()) log.debug("regimen:"+regimen);
			
			String esReg1057 = regimen.get("t02cod_rel")==null?"":(String) regimen.get("t02cod_rel");
			if(log.isDebugEnabled()) log.debug("regimen:"+esReg1057);
			
			pe.gob.sunat.rrhh.dao.T99DAO t99dao = new pe.gob.sunat.rrhh.dao.T99DAO(dbpool);
			t = t99dao.findTipoLicenciaCAS(tipo);
			if(log.isDebugEnabled()) log.debug("t:"+t);
							
			if ( esReg1057.equals("09") && t == null ) {
				v = true;
			} else{
				v = false;
			}
						
			//if(log.isDebugEnabled()) log.debug("Llego a validar");			
			//pe.gob.sunat.rrhh.dao.T99DAO dao = new pe.gob.sunat.rrhh.dao.T99DAO(dbpool);
			//obtenemos los datos del tipo de solicitud
			//t = dao.findTipoLicenciaCAS(tipo);
			//if(log.isDebugEnabled()) log.debug("tipolic:"+t);
			
			
		} catch (Exception e) {
			log.error(e,e);			
			beanM.setMensajeerror(e.toString());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}
		
		return v;
	}
	
	//ICAPUNAY - FORMATIVAS
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public ArrayList buscarTipoMovimientosFormativasPerfil(String dbpool, String tipo,
            String estado, String fIni, String fFin, HashMap seguridad)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        ArrayList lista = null;
        try {

            T1279DAO dao = new T1279DAO();
            lista = dao.findByTipoIdEstIdFecIniFecFinFormativasPerfil(dbpool, tipo, estado,
                    fIni, fFin, seguridad);

        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return lista;
    }
  //FIN ICAPUNAY - FORMATIVAS
    
    /* ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA - 22/08/2011 */
    /**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */    
    public ArrayList buscarMovimientosActivosInactivos(String dbpool) //nuevo agregado 05/12/2011 icapunay
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        ArrayList movimientos = null;      
        try {

        	T4636DAO dao = new T4636DAO(dbpool);
        	//movimientos = (ArrayList)dao.findAllMovimientosActivos();  //Listado de movimientos activos a devolver de la t1279 //nuevo agregado 05/12/2011 icapunay
        	movimientos = (ArrayList)dao.findAllMovimientos();  //Listado de movimientos activos a devolver de la t1279 //nuevo agregado 05/12/2011 icapunay
        	
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return movimientos;
    }
    
    /**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public ArrayList buscarCategorias(String dbpool)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        ArrayList categorias = null;       
        try {

        	T4635DAO dao = new T4635DAO(dbpool);
        	categorias = (ArrayList)dao.findAllCategorias(); //Listado de todas las categorias
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return categorias;
    }
    
    /**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public ArrayList buscarCategoriasOrdenadasByDescrip(String dbpool)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        ArrayList categorias = null;       
        try {

        	T4635DAO dao = new T4635DAO(dbpool);
        	categorias = (ArrayList)dao.findAllCategoriasOrderByDescrip(); //Listado de todas las categorias ordenadas por descripciÃƒÂ³n
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return categorias;
    }
    
    /**
	 * Metodo encargado de realizar la eliminacion de categorias y sus movimientos relacionados
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 * @param params
	 * @param listaCategorias	
	 * @throws RemoteException
	 */    
    public void eliminarCategorias(String[] params, ArrayList listaCategorias)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        ArrayList listaMovimientosByCategoria = null;
        boolean eliminoCate=false;
        boolean eliminoMovi=false;
        Map categoria = null;
        Map movimiento = null; 
        try {
            
            pe.gob.sunat.rrhh.asistencia.dao.T4635DAO t4635dao = new pe.gob.sunat.rrhh.asistencia.dao.T4635DAO("jdbc/dgsp");            
            pe.gob.sunat.rrhh.asistencia.dao.T4636DAO t4636dao = new pe.gob.sunat.rrhh.asistencia.dao.T4636DAO("jdbc/dgsp");
            if (params != null) {            	            	
                for (int i = 0; i < params.length; i++) {
                	categoria = (HashMap) listaCategorias.get(Integer.parseInt(params[i]));
                	listaMovimientosByCategoria=(ArrayList)t4636dao.findMovimientosByCategoria(categoria.get("cod_cate").toString());
                	if((listaMovimientosByCategoria!=null) && (listaMovimientosByCategoria.size()>0)){
                		 for (int j = 0; j < listaMovimientosByCategoria.size(); j++) {
                			 movimiento = (HashMap) listaMovimientosByCategoria.get(j);
                			 eliminoMovi=t4636dao.deleteMovimiento(categoria.get("cod_cate").toString(), movimiento.get("cod_mov").toString());
                         	 if (eliminoMovi==true){
                         		log.debug("Se elimino movimiento: "+movimiento.get("cod_mov").toString() +" de la categoria: "+categoria.get("cod_cate").toString());
                         	 }
                		 }                		
                	}else{
                		log.debug("No existen movimientos para la categoria: "+categoria.get("cod_cate").toString());
                	}                	
                	eliminoCate=t4635dao.deleteCategoriaByPK(categoria.get("cod_cate").toString());
                	if (eliminoCate==true){
                		log.debug("Se elimino Categoria: "+categoria.get("cod_cate").toString());
                	}
                }
            }
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

    }
    
    /**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public ArrayList cargarMovAsignados(String codCategoria)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        ArrayList movimientos = null;      
        try {

        	T4636DAO dao = new T4636DAO("jdbc/dcsp");
        	movimientos = (ArrayList)dao.findMovimientosByCategoria(codCategoria);  //Listado de todos los movimientos asignados a una categoria
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return movimientos;
    }
    
    /**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public ArrayList cargarMovAsignadosAllCategorias()
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        ArrayList movimientos = null;      
        try {

        	T4636DAO dao = new T4636DAO("jdbc/dcsp");
        	movimientos = (ArrayList)dao.findAllMovimientosAsignados();  //Listado de todos los movimientos asignados a todas las categorias
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return movimientos;
    }
    
    /**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public ArrayList cargarMovPorAsignar(ArrayList listaMovAsignados,ArrayList listaAllMovT1279Activos)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();               
        ArrayList listaMovPorAsignar = new ArrayList(); 
        Map movimientoA = null;
        Map movimiento = null;     
        boolean swEncontro=false;
        log.debug("listaAllMovT1279Activos.size(): "+listaAllMovT1279Activos.size());
        log.debug("listaMovAsignados.size(): "+listaMovAsignados.size());
        try {
        	        	
        	if ((listaAllMovT1279Activos!=null) && (listaAllMovT1279Activos.size()>0)){
        		for (int t = 0; t < listaAllMovT1279Activos.size(); t++) {  
        			swEncontro=false;
        			movimiento = (HashMap) listaAllMovT1279Activos.get(t);
        			if ((listaMovAsignados!=null) && (listaMovAsignados.size()>0)){        				
        				for (int a = 0; a < listaMovAsignados.size(); a++) {
            				movimientoA = (HashMap) listaMovAsignados.get(a);
            				if(movimiento.get("mov").toString().trim().equals(movimientoA.get("cod_mov").toString().trim())){
            					swEncontro=true;            						            						
            				} 						
            			}
        				if(swEncontro==false){
        					listaMovPorAsignar.add(movimiento); //Listado nueva de todos los movimientos disponibles por asignar a una categoria        					
        				}
        			}
           	 	}
        	}else{
        		log.debug("NO existen movimientos activos de la t1279");
        	}       	      	 
        	
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }
        log.debug("listaMovPorAsignar().size(): "+listaMovPorAsignar.size());
        log.debug("Nueva listaMovPorAsignar: "+listaMovPorAsignar);
        return listaMovPorAsignar;
    }
    
    /**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public String devolverNuevoCodigoCategoria(String dbpool)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        String nuevo_codCate = null;
        String cod_cate = null;
        Map ultimaCategoria = null;
        int correlativo=0;	
        try {

        	T4635DAO t4635dao = new T4635DAO(dbpool); 
        	ultimaCategoria=t4635dao.findUltimaCategoriaRegistrada();
        	if(ultimaCategoria!=null && !ultimaCategoria.isEmpty()){
        		cod_cate=(String)ultimaCategoria.get("cod_cate");
        		correlativo=Integer.parseInt(cod_cate);
        		nuevo_codCate=Numero.format(new Integer(correlativo+1), "000");
        	}else{
        		nuevo_codCate=Numero.format(new Integer(1), "000");
        	}        	
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }
        log.debug("nuevo_codCate: "+nuevo_codCate);
        return nuevo_codCate;
    }
    
    /**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public Map buscarCategoria(String dbpool, String codCategoria)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        Map categoria = null;
       
        try {

        	T4635DAO t4635dao = new T4635DAO(dbpool); 
        	categoria=t4635dao.findCategoria(codCategoria);
        		
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }
        log.debug("categoria: "+categoria);
        return categoria;
    }
    
    /**
	 * Registra y/o actualiza la categoria y sus movimientos 
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 * @param datos HashMap	
	 * @throws RemoteException
	 */
	public void registrarCategoria(String dbpool,HashMap datos)
			throws FacadeException {
		
		BeanMensaje beanM = new BeanMensaje();
		Map params = new HashMap();
		List listaMovAsignados = new ArrayList();
		boolean insertoCat=false;
		boolean insertoMov=false;
		boolean actualizoDescCat=false;
		boolean eliminoMovi=false;
		ArrayList listaMovimientosByCategoria = null;
		Map movimiento = null; 
		
		try {
							
			T4635DAO t4635dao = new T4635DAO(dbpool);            
	        T4636DAO t4636dao = new T4636DAO(dbpool);
			
	        String cod_cate = (String) datos.get("cod_cate");
	        String nuevo_codCate = (String) datos.get("nuevo_codCate");
	        String movConcatenados = (String) datos.get("movimientos");
	        String cod_mov = null;
	        
	       	params.put("descrip", (String) datos.get("descrip"));
			params.put("cod_usucreac", (String) datos.get("cod_usucreac"));
			params.put("fec_creac", (String) datos.get("fec_creac"));			

			if(movConcatenados!=null && !movConcatenados.trim().equals("")){
				StringTokenizer st = new StringTokenizer(movConcatenados, "_");			
				while (st.hasMoreTokens()) {
					listaMovAsignados.add(st.nextToken().trim());						
				}	
			}					
			
			if(listaMovAsignados!=null && !listaMovAsignados.isEmpty()){//al menos un movimiento debe tener la categoria a registrar o actualizar
				if(nuevo_codCate!=null && !nuevo_codCate.equals("")){ //registrar nueva categoria y movimientos
					params.put("cod_cate", nuevo_codCate);
					insertoCat=t4635dao.insertCategoria(params);
					if (insertoCat==true){
						log.debug("SI se inserto la nueva categoria: "+nuevo_codCate);
						for (int i = 0; i < listaMovAsignados.size(); i++) {	
							params.put("cod_mov", ((String)listaMovAsignados.get(i)).trim());
							insertoMov=t4636dao.insertMovimientoParaCategoria(params);
							if (insertoMov==true){
								log.debug("SI se inserto el movimiento: "+((String)listaMovAsignados.get(i)).trim()+" para la nueva categoria: "+nuevo_codCate);
							}else{
								log.debug("NO se inserto el movimiento: "+((String)listaMovAsignados.get(i)).trim()+" para la nueva categoria: "+nuevo_codCate);
							}
						}						
					}else{
						log.debug("NO se inserto la nueva categoria: "+nuevo_codCate);
					}
				}
				else{ //modificar categoria y movimientos
					params.put("cod_cate", cod_cate);
					actualizoDescCat=t4635dao.updateCategoriaByPK(params);					
					listaMovimientosByCategoria=(ArrayList)t4636dao.findMovimientosByCategoria(cod_cate);
                	if((listaMovimientosByCategoria!=null) && (listaMovimientosByCategoria.size()>0)){
                		 for (int j = 0; j < listaMovimientosByCategoria.size(); j++) {
                			 movimiento = (HashMap) listaMovimientosByCategoria.get(j);
                			 eliminoMovi=t4636dao.deleteMovimiento(cod_cate, movimiento.get("cod_mov").toString());
                         	 if (eliminoMovi==true){
                         		log.debug("Se elimino movimiento: "+movimiento.get("cod_mov").toString() +" de la categoria: "+cod_cate);
                         	 }
                		 }                		
                	}else{
                		log.debug("No existen movimientos registrados para categoria: "+cod_cate);
                	} 					
                	for (int k = 0; k < listaMovAsignados.size(); k++) {
                		params.put("cod_mov", ((String)listaMovAsignados.get(k)).trim());
                		insertoMov=t4636dao.insertMovimientoParaCategoria(params);
           			 	if (insertoMov==true){
           			 		log.debug("Se inserto movimiento: "+((String)listaMovAsignados.get(k)).trim()+" para categoria: "+cod_cate);
           			 	}else{
							log.debug("No se inserto movimiento: "+((String)listaMovAsignados.get(k)).trim()+" para categoria: "+cod_cate);
						}
           		 	}					
				}
			}			
			
		} catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

	}
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
    public ArrayList buscarCategoriasByMovimiento(String dbpool,String codMovimiento)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        ArrayList categorias = null;       
        try {

        	T4635DAO dao = new T4635DAO(dbpool);
        	categorias = (ArrayList)dao.findCategoriasByMovimiento(codMovimiento); //Listado de las categorias que incluyen un movimiento especifico
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return categorias;
    }
    /* FIN ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA - 22/08/2011 */

 
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @author jmaravi
	 * @since 14/03/2014
	 */
    public ArrayList findMovimientosFlujosAprobadores(String dbpool, HashMap datos) throws IncompleteConversationalState,
            RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        ArrayList movimientos = null; //Listado de movimientos a devolver
        try {

            ReporteDAO dao = new ReporteDAO();

            //Buscar todos
            movimientos = dao.findMovimientosFlujosAprobadores(dbpool,datos);
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return movimientos;
    }
    
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @author jmaravi
	 * @since 10/04/2014
	 */
    public ArrayList obtenerPendientesxAprobador(String dbpool, HashMap datos) throws IncompleteConversationalState,
            RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        ArrayList movimientos = null; //Listado de movimientos a devolver
        try {

            ReporteDAO dao = new ReporteDAO();

            //Buscar todos
            movimientos = dao.obtenerPendientesxAprobador(dbpool,datos);
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

        return movimientos;
    }    
    
    
    /**
	 * Metodo encargado de eliminar un flujo de aprobación (tupla)
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 * @param params
	 * @param listaCategorias	
	 * @throws RemoteException
	 */    
    public boolean insertAprobadorFlujo(String dbpool, HashMap datos)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        boolean result = false;
 
        try {
            
            T1480DAO t1480 = new T1480DAO();    
            result = t1480.insertAprobadorFlujo(dbpool, datos);
           
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }
        
        return result;

    }
    
    
    /**
	 * Metodo encargado de eliminar un flujo de aprobación (tupla)
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 * @param params
	 * @param listaCategorias	
	 * @throws RemoteException
	 */    
    public boolean deleteAprobadorFlujo(String dbpool, HashMap datos)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        boolean result = false;
        try {
            
            T1480DAO t1480 = new T1480DAO();    
            result = t1480.deleteAprobadorFlujo(dbpool, datos);
           
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }
        return result;
    }
 
    /**
	 * Metodo encargado de eliminar un flujo de aprobación (tupla)
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 * @param params
	 * @param listaCategorias	
	 * @throws RemoteException
	 */    
    public boolean deleteAprobadorFlujo4UniOrgYMov(String dbpool, HashMap datos)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        boolean result = false;
        try {
            
            T1480DAO t1480 = new T1480DAO();    
            result = t1480.deleteAprobadorFlujo4UniOrgYMov(dbpool, datos);
           
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }
        return result;
    }   
    
    /**
	 * Metodo encargado de eliminar un flujo de aprobación (tupla)
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 * @param params
	 * @param listaCategorias	
	 * @throws RemoteException
	 */    
    public int transferirSegPendientes(String dbpool, String uorgan, String numMov, 
			String viejoAprob, String nuevoAprob, String usuario)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        int result = 0;
        try {
            
        	pe.gob.sunat.sp.asistencia.dao.T1455DAO t1455 = new pe.gob.sunat.sp.asistencia.dao.T1455DAO();
            result = t1455.transferirSegPendientes(dbpool, uorgan, numMov, viejoAprob, nuevoAprob, usuario);
           
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }
        return result;
    }
    
    
	/**
	 * Metodo encargado del registro masivo del flujo de aprobadores 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"		
	 * @param dbean	DynaBean
	 * @throws IncompleteConversationalState
	 * @throws RemoteException
	 * @author jmaravi
	 * @since 22/04/2014
	 */
	public void registroFlujoAprobadoresMasivo(DynaBean dbean)
			throws IncompleteConversationalState, RemoteException {
		BeanMensaje beanM = new BeanMensaje();
		String usuario =  dbean.getString("usuario");
		//dbean.put("observacion", "Registro de flujo de aprobadores masivo: "+dbean.getString("archivo-filter-filename"));
		dbean.put("observacion", "Registro de flujo de aprobadores masivo");
		try {

			if (log.isDebugEnabled()) log.debug("Ingreso registroFlujoAprobadoresMasivooMantenimientojoFacade");
			if (log.isDebugEnabled()) log.debug("Va a ingresar a encolaProceso-QueueDAO");			

			QueueDAO qd = new QueueDAO();
			
			qd.encolaProceso(ProcesoFacadeHome.JNDI_NAME,"planificarRegistroFlujoAprobadoresMasivo", (HashMap)dbean, usuario);
			//PRUEBAS LOCALES
			/*ProcesoFacadeHome facade2Home = (ProcesoFacadeHome) sl.getRemoteHome(ProcesoFacadeHome.JNDI_NAME,
						ProcesoFacadeHome.class);

			ProcesoFacadeRemote remote = facade2Home.create();	
			remote.planificarRegistroFlujoAprobadoresMasivo((HashMap)dbean, usuario);*/
			//
			if (log.isDebugEnabled()) log.debug("Salió de encolaProceso-QueueDAO");

		} catch (Exception e) {
			log.error(e.getMessage());
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new IncompleteConversationalState(beanM);
		}

	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 * @author jmaravi
	 * @since 24/04/2014
	 */
    public ArrayList registrarFlujosAprobadores(String dbpool, HashMap datos)
            throws IncompleteConversationalState, RemoteException {

        BeanMensaje beanM = new BeanMensaje();
        List alMovimientos = (ArrayList)datos.get("alMovimientos");
        int iCantInstancias = 0;
        String u_organ="";
        ArrayList listStatus = new ArrayList();
        
        String sDerivarPendientes = (String)datos.get("sDerivarPendientes");
        UsuarioBean bUsuario = (UsuarioBean)datos.get("usuarioBean");
		MessagesWeb messagesWeb = (MessagesWeb)datos.get("messagesWeb");
        
        try {
			T02DAO t02dao = new T02DAO();
			
			if(alMovimientos != null){
				HashMap flujoMov;	
				HashMap hmDatosProceso;
				u_organ = ((HashMap)alMovimientos.get(0)).get("u_organ").toString().trim().toUpperCase();
				ArrayList alAprobadores = new ArrayList();
				String mov_cod="";
				
				//Recuperamos y guardamos en memoria todos los campos del formulario de aprobadores.
				for(int i = 0; i < alMovimientos.size(); i++ ){
					flujoMov = (HashMap)alMovimientos.get(i);
					mov_cod = (String)flujoMov.get("mov_cod");
					
					flujoMov.put("accion", "");//Indica la acción que se ejecutará con el flujo registrado
					flujoMov.put("aprob_ini_estado", "OK");//Indica el estado de validación de la instancia inicial
					flujoMov.put("aprob_int_estado", "OK");//Indica el estado de validación de la instancia intermedia
					flujoMov.put("aprob_fin_estado", "OK");//Indica el estado de validación de la instancia final
					
					String sAprobNew = "";
					iCantInstancias = 0;
					int iPend4Mov = Integer.parseInt((String) flujoMov.get("aprob_ini_pend"))+
							Integer.parseInt((String) flujoMov.get("aprob_int_pend"))+
							Integer.parseInt((String) flujoMov.get("aprob_fin_pend")) ;

					//por tratarse del módulo de registro de aprobadores el accion_id de la tabla es: 
					flujoMov.put("accion_id",Constantes.ACCION_APROBAR);
					
					//Determinamos la cantidad de instancias.					
					if(flujoMov.get("aprob_ini_new").equals("") && flujoMov.get("aprob_int_new").equals("") && flujoMov.get("aprob_fin_new").equals("")){
						iCantInstancias = 0;
					}else if(!flujoMov.get("aprob_ini_new").equals("") && flujoMov.get("aprob_int_new").equals("") && flujoMov.get("aprob_fin_new").equals("")){
						iCantInstancias = 1;
					}else if(!flujoMov.get("aprob_ini_new").equals("") && flujoMov.get("aprob_int_new").equals("") && !flujoMov.get("aprob_fin_new").equals("")){
						iCantInstancias = 2;
					}else if(!flujoMov.get("aprob_ini_new").equals("") && !flujoMov.get("aprob_int_new").equals("") && !flujoMov.get("aprob_fin_new").equals("")){
						iCantInstancias = 3;
					}else{
						flujoMov.put("accion", "ERROR");
						messagesWeb.setMensajeWeb(MessagesWeb.TYPE_ERROR, "Movimiento <b>"+flujoMov.get("mov_cod")+"</b>, La configuración de las instancias no es correcta. Verifique.");
					}					
					flujoMov.put("num_instancias", String.valueOf(iCantInstancias));				
					
					//Iniciamos validaciones sólo de los tipo_movs válidos.
					if(iCantInstancias>0){
						
						if(iCantInstancias==2 && flujoMov.get("aprob_ini_new").equals(flujoMov.get("aprob_fin_new"))){
							flujoMov.put("accion", "ERROR");
							messagesWeb.setMensajeWeb(MessagesWeb.TYPE_ERROR, "Movimiento <b>"+flujoMov.get("mov_cod")+"</b>, No se puede registrar el mismo aprobador en más de una instancia.");
						}
						if(iCantInstancias==3 && 
							(flujoMov.get("aprob_ini_new").equals(flujoMov.get("aprob_int_new")) ||
							 flujoMov.get("aprob_ini_new").equals(flujoMov.get("aprob_fin_new")) ||
							 flujoMov.get("aprob_int_new").equals(flujoMov.get("aprob_fin_new")) 
							)
						){
							flujoMov.put("accion", "ERROR");
							messagesWeb.setMensajeWeb(MessagesWeb.TYPE_ERROR, "Movimiento <b>"+flujoMov.get("mov_cod")+"</b>, No se puede registrar el mismo aprobador en más de una instancia.");
						}
						
						//Verificamos la existencia de los aprobadores.
						HashMap hmAprob =null;
						if(flujoMov.get("aprob_ini_new").toString().length()>0){
							hmAprob = t02dao.findByCodPers(dbpool, flujoMov.get("aprob_ini_new").toString());
							if( hmAprob != null ){
								if(hmAprob.get("t02cod_pers").equals("-")){
									flujoMov.put("accion", "ERROR");
									messagesWeb.setMensajeWeb(MessagesWeb.TYPE_ERROR, "Movimiento <b>"+flujoMov.get("mov_cod")+"</b>, el registro de aprobador <b>"+flujoMov.get("aprob_ini_new").toString()+"</b> no existe.");
								}else if(! hmAprob.get("t02cod_stat").equals("1") ){
									flujoMov.put("accion", "ERROR");
									messagesWeb.setMensajeWeb(MessagesWeb.TYPE_ERROR, "Movimiento <b>"+flujoMov.get("mov_cod")+"</b>, el registro de aprobador <b>"+flujoMov.get("aprob_ini_new").toString()+"</b> NO esta ACTIVO.");
								}
							}
						}
						hmAprob =null;
						if(flujoMov.get("aprob_int_new").toString().length()>0){
							hmAprob = t02dao.findByCodPers(dbpool, flujoMov.get("aprob_int_new").toString()) ;
							if(hmAprob != null ){
								if(hmAprob.get("t02cod_pers").equals("-")){
									flujoMov.put("accion", "ERROR");
									messagesWeb.setMensajeWeb(MessagesWeb.TYPE_ERROR, "Movimiento <b>"+flujoMov.get("mov_cod")+"</b>, el registro de aprobador <b>"+flujoMov.get("aprob_int_new").toString()+"</b> no existe.");
								}else if(! hmAprob.get("t02cod_stat").equals("1") ){
									flujoMov.put("accion", "ERROR");
									messagesWeb.setMensajeWeb(MessagesWeb.TYPE_ERROR, "Movimiento <b>"+flujoMov.get("mov_cod")+"</b>, el registro de aprobador <b>"+flujoMov.get("aprob_int_new").toString()+"</b> NO esta ACTIVO.");
								}
							}
						}
						hmAprob =null;
						if(flujoMov.get("aprob_fin_new").toString().length()>0){
							hmAprob = t02dao.findByCodPers(dbpool, flujoMov.get("aprob_fin_new").toString());
							if( hmAprob != null ){
								if(hmAprob.get("t02cod_pers").equals("-")){
									flujoMov.put("accion", "ERROR");
									messagesWeb.setMensajeWeb(MessagesWeb.TYPE_ERROR, "Movimiento <b>"+flujoMov.get("mov_cod")+"</b>, el registro de aprobador <b>"+flujoMov.get("aprob_fin_new").toString()+"</b> no existe.");
								}else if(! hmAprob.get("t02cod_stat").equals("1") ){
									flujoMov.put("accion", "ERROR");
									messagesWeb.setMensajeWeb(MessagesWeb.TYPE_ERROR, "Movimiento <b>"+flujoMov.get("mov_cod")+"</b>, el registro de aprobador <b>"+flujoMov.get("aprob_fin_new").toString()+"</b> NO esta ACTIVO.");
								}
							}
						}
						
						//Verificamos que no se intente derivar al vacio.
						if(flujoMov.get("aprob_ini_new").equals("") && !flujoMov.get("aprob_ini_pend").equals("0")){
							flujoMov.put("accion", "ERROR");  
							messagesWeb.setMensajeWeb(MessagesWeb.TYPE_ERROR, "Movimiento <b>"+flujoMov.get("mov_cod")+"</b>, No se puede eliminar una instancia cuando hay pendientes.");
						}						
						if(flujoMov.get("aprob_int_new").equals("") && !flujoMov.get("aprob_int_pend").equals("0")){
							flujoMov.put("accion", "ERROR");  
							messagesWeb.setMensajeWeb(MessagesWeb.TYPE_ERROR, "Movimiento <b>"+flujoMov.get("mov_cod")+"</b>, No se puede eliminar una instancia cuando hay pendientes.");
						}						
						if(flujoMov.get("aprob_fin_new").equals("") && !flujoMov.get("aprob_fin_pend").equals("0")){
							flujoMov.put("accion", "ERROR");  
							messagesWeb.setMensajeWeb(MessagesWeb.TYPE_ERROR, "Movimiento <b>"+flujoMov.get("mov_cod")+"</b>, No se puede eliminar una instancia cuando hay pendientes.");
						}
						
					
						//CAtegorizamos el tipo de acción a realizar con el flujo registrado
						if(!flujoMov.get("accion").equals("ERROR")){
							//Evaluamos cual es la acción que se efectuará (Nuevo o modificación).
							if((flujoMov.get("aprob_ini").equals("") || flujoMov.get("aprob_ini") == null) && ((String)flujoMov.get("aprob_ini_new")).length()==4){
								flujoMov.put("accion", "NUEVO");
							}else{
								if( flujoMov.get("aprob_ini").equals(flujoMov.get("aprob_ini_new")) &&
									flujoMov.get("aprob_int").equals(flujoMov.get("aprob_int_new")) &&
									flujoMov.get("aprob_fin").equals(flujoMov.get("aprob_fin_new")))
								{
									flujoMov.put("accion", "IGUAL");				
								}else{
									/*//Comentamos porque el F2 dice que si debemos dejar que queden solicitudes zombies
									if(sDerivarPendientes.equals("NO") && ( iPend4Mov > 0)){
										flujoMov.put("accion", "IGUAL");								
										messagesWeb.setMensajeWeb(MessagesWeb.TYPE_WARNING, "El tipo de movimiento <b>"+flujoMov.get("mov_cod")+"</b> no se modificó porque tiene pendientes.");
									}else{
										//Aqui aplicamos la derivación de pendientes.
									 */
									flujoMov.put("accion", "MODIFICA");
									//}
								}
							}
						}//FinSi son registros sin ERROR.
					}//fin tipo_mov validos
					else{
						if(!flujoMov.get("accion").equals("ERROR") && iCantInstancias == 0){
							if(iPend4Mov>0){
								flujoMov.put("accion", "ERROR");
								messagesWeb.setMensajeWeb(MessagesWeb.TYPE_ERROR, "Movimiento <b>"+flujoMov.get("mov_cod")+"</b>, no se puede eliminar el flujo cuando hay pendientes.");
							}else{
								if(!(flujoMov.get("aprob_ini").equals("") &&flujoMov.get("aprob_int").equals("")&& flujoMov.get("aprob_fin").equals("")  )){
									flujoMov.put("accion", "BORRAR");
									messagesWeb.setMensajeWeb(MessagesWeb.TYPE_ERROR, "Movimiento <b>"+flujoMov.get("mov_cod")+"</b>, el registro <b>"+flujoMov.get("aprob_int_new").toString()+"</b> no existe.");
								}
							}
						}
					}
				}//Fin For cada tipo_mov
			
			
				//-------------------------------------------------------------------------------------------------//
				//Procedemos a ejecutar las acciones correspondientes sólo en los registros que no están marcados con errores.
				for(int i = 0; i < alMovimientos.size(); i++ ){
					flujoMov = (HashMap)alMovimientos.get(i);
					mov_cod = (String)flujoMov.get("mov_cod");
					
					//Validamos 
					
					if(flujoMov.get("accion").equals("BORRAR")){
						hmDatosProceso = new HashMap();
						hmDatosProceso.put("sNumMov",flujoMov.get("mov_cod"));
						hmDatosProceso.put("cAccion",flujoMov.get("accion_id"));
						hmDatosProceso.put("sUniOrgan",flujoMov.get("u_organ"));
						deleteAprobadorFlujo4UniOrgYMov(dbpool, hmDatosProceso);
						log.info("Se eliminaron los flujos del tipo_mov="+flujoMov.get("mov_cod").toString()+" de la UniOrg="+flujoMov.get("u_organ").toString());
						messagesWeb.setMensajeWeb(messagesWeb.TYPE_SUCCESS, "Movimiento <b>"+flujoMov.get("mov_cod")+"</b>, se eliminó el flujo completo.");
					}
					
					if(flujoMov.get("accion").equals("MODIFICA")){//Primero se elimina para luego insertar.						
						
						//primero aplicamos las derivaciones (si eligio SI).
						if(sDerivarPendientes.equals("SI")){
							int iDerivados = 0;
							if(!flujoMov.get("aprob_ini").equals(flujoMov.get("aprob_ini_new")) && !flujoMov.get("aprob_ini_pend").equals("0")){
								iDerivados = transferirSegPendientes(dbpool, flujoMov.get("u_organ").toString(), flujoMov.get("mov_cod").toString(), flujoMov.get("aprob_ini").toString(), flujoMov.get("aprob_ini_new").toString(), bUsuario.getLogin());
								messagesWeb.setMensajeWeb(messagesWeb.TYPE_SUCCESS,"Se derivaron "+String.valueOf(iDerivados)+" solicitudes tipo "+flujoMov.get("mov_cod").toString()+" del registro "+flujoMov.get("aprob_ini").toString()+" al "+flujoMov.get("aprob_ini_new").toString());
							}
							if(!flujoMov.get("aprob_int").equals(flujoMov.get("aprob_int_new")) && !flujoMov.get("aprob_int_pend").equals("0")){
								iDerivados = transferirSegPendientes(dbpool, flujoMov.get("u_organ").toString(), flujoMov.get("mov_cod").toString(), flujoMov.get("aprob_int").toString(), flujoMov.get("aprob_int_new").toString(), bUsuario.getLogin());
								messagesWeb.setMensajeWeb(messagesWeb.TYPE_SUCCESS,"Se derivaron "+String.valueOf(iDerivados)+" solicitudes tipo "+flujoMov.get("mov_cod").toString()+" del registro "+flujoMov.get("aprob_int").toString()+" al "+flujoMov.get("aprob_int_new").toString());
							}
							if(!flujoMov.get("aprob_fin").equals(flujoMov.get("aprob_fin_new")) && !flujoMov.get("aprob_fin_pend").equals("0")){
								iDerivados = transferirSegPendientes(dbpool, flujoMov.get("u_organ").toString(), flujoMov.get("mov_cod").toString(), flujoMov.get("aprob_fin").toString(), flujoMov.get("aprob_fin_new").toString(), bUsuario.getLogin());
								messagesWeb.setMensajeWeb(messagesWeb.TYPE_SUCCESS,"Se derivaron "+String.valueOf(iDerivados)+" solicitudes tipo "+flujoMov.get("mov_cod").toString()+" del registro "+flujoMov.get("aprob_fin").toString()+" al "+flujoMov.get("aprob_fin_new").toString());
							}
						}
						
						//Una vez terminadas las derivaciones, se elimina el flujo.
						hmDatosProceso = new HashMap();
						hmDatosProceso.put("sNumMov",flujoMov.get("mov_cod"));
						hmDatosProceso.put("cAccion",flujoMov.get("accion_id"));
						hmDatosProceso.put("sUniOrgan",flujoMov.get("u_organ"));
						deleteAprobadorFlujo4UniOrgYMov(dbpool, hmDatosProceso);
						log.info("Se eliminaron los flujos del tipo_mov="+flujoMov.get("mov_cod").toString()+" de la UniOrg="+flujoMov.get("u_organ").toString());
					}
					//Registramos los flujos.
					if(flujoMov.get("accion").equals("MODIFICA") || flujoMov.get("accion").equals("NUEVO")){
						HashMap datosNew = new HashMap();
						datosNew.put("sNumMov",flujoMov.get("mov_cod"));
						datosNew.put("cAccion",flujoMov.get("accion_id"));
						datosNew.put("sUniOrgan",flujoMov.get("u_organ"));
						datosNew.put("sUsuario", bUsuario.getLogin());
						
						//verficamos si existe flujo por tanto se debe insertar
						if(((String)flujoMov.get("aprob_ini_new")).length() == 4){
							if(flujoMov.get("aprob_int_new").equals("") && flujoMov.get("aprob_fin_new").equals("")){//es instancia única.
								datosNew.put("sCodPersOri", flujoMov.get("aprob_ini_new") );
								datosNew.put("sCodPersDes", "" );
								datosNew.put("cInstancia", Constantes.ESTACION_UNICA);
								insertAprobadorFlujo(dbpool, datosNew);
							}else{
								//No debería haber casos (inicio e intermedio sin final), se controla en las validaciones.
								datosNew.put("sCodPersOri", flujoMov.get("aprob_ini_new") );
								
								if (flujoMov.get("aprob_int_new").equals(""))
									datosNew.put("sCodPersDes", flujoMov.get("aprob_fin_new") );
								else
									datosNew.put("sCodPersDes", flujoMov.get("aprob_int_new") );
								
								datosNew.put("cInstancia", Constantes.ESTACION_INICIAL);								
								insertAprobadorFlujo(dbpool, datosNew);
								
								if (flujoMov.get("aprob_int_new").equals("")){
									datosNew.put("sCodPersOri", flujoMov.get("aprob_fin_new") );
									datosNew.put("sCodPersDes", "" );
									datosNew.put("cInstancia", Constantes.ESTACION_FINAL);
									insertAprobadorFlujo(dbpool, datosNew);
								}else{
									datosNew.put("sCodPersOri", flujoMov.get("aprob_int_new") );
									datosNew.put("sCodPersDes", flujoMov.get("aprob_fin_new") );
									datosNew.put("cInstancia", Constantes.ESTACION_INTERMEDIA);
									insertAprobadorFlujo(dbpool, datosNew);									
									
									datosNew.put("sCodPersOri", flujoMov.get("aprob_fin_new") );
									datosNew.put("sCodPersDes", "" );
									datosNew.put("cInstancia", Constantes.ESTACION_FINAL);
									insertAprobadorFlujo(dbpool, datosNew);											
								}
							}							
						}//fin inserts						
					}//fin si son iguales (no cambia nada).					
				}//fin for para cada mov.				
				messagesWeb.setMensajeWeb(MessagesWeb.TYPE_INFO, "Se completo el proceso, ahora puede verificar los cambios realizados.");
			}else{
				//Adiciono el mensaje
				messagesWeb.setMensajeWeb(MessagesWeb.TYPE_ERROR,"No se econtraron los flujos correspondientes a la U.O. seleccionada, vuelva a intentarlo.");
				log.debug("No se econtraron los flujos correspondientes a la U.O. seleccionada, vuelva a intentarlo.");
			}            
          
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }
        return listStatus;
    }
    
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 * @author jmaravi
	 * @since 24/04/2014
	 */
    public void asignarPeriodos(Map params, ArrayList listaPeriodos)
            throws IncompleteConversationalState, RemoteException {
    	BeanMensaje beanM = new BeanMensaje();
    	try {
        	log.debug("asignarPeriodos");
        	String[] params2 =  (String[]) params.get("params2");
            T1278DAO t1278dao = new T1278DAO(params.get("pool_sp_g"));
            
            
            BeanT12 periodoUO = new BeanT12();
            if (params != null) {
            	for (int i = 0; i < params2.length; i++) {

            		periodoUO = (BeanT12) listaPeriodos.get(Integer.parseInt(params2[i]));

        			try {
        				params.put("u_organ", periodoUO.getT12cod_uorgan());	
        				t1278dao.insertPeriodoArea(params);

        			} catch (Exception Ex) {
        				log.debug(Ex.getMessage());
        			}
        		}
            }
        } catch (Exception e) {
        	log.error(e,e);
            beanM.setMensajeerror(e.getMessage());
            beanM.setMensajesol("Por favor intente nuevamente.");
            throw new IncompleteConversationalState(beanM);
        }

    }
    
    
}