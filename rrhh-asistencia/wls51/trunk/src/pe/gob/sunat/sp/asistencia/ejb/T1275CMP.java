package pe.gob.sunat.sp.asistencia.ejb;

import java.rmi.RemoteException;
import java.sql.Date;

import javax.ejb.CreateException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;

import pe.gob.sunat.ejb.EJBEstandarLocalCMP;

/**
 * 
 * 
 * @ejb.bean
 *    cmp-version="2.x"
 *    description="Entidad de la tabla T1275marcacion "
 *    local-jndi-name="ejb/rrhh/cmp/sp/asistencia/t1275cmp"
 *    name="T1275CMP"
 *    type="CMP"
 *    view-type="local"
 *    schema="T1275Schema" 
 * 
 * @ejb.home local-class="pe.gob.sunat.sp.asistencia.ejb.T1275CMPHome"
 *                local-extends="javax.ejb.EJBLocalHome"
 *
 * @ejb.interface local-class="pe.gob.sunat.sp.asistencia.ejb.T1275CMPLocal"
 *                local-extends="javax.ejb.EJBLocalObject"
 * 
 * @ejb.transaction-type type="Container"
 * 
 * @ejb.persistence table-name="t1275marcacion"
 * @ejb.pk        class="pe.gob.sunat.sp.asistencia.ejb.T1275CMPPK"
 *                extends="java.lang.Object"
 *                implements="java.io.Serializable"
 *  
 * @jboss.container-configuration name="Standard CMP 2.x EntityBean"
 * @jboss.persistence create-table="false"
 *                    remove-table="false"
 *                    table-name="t1275marcacion"
 * 
 * @jboss.version="3.2"
 * 
 * @weblogic.persistence create-table="false" remove-table="false" table-name="t1275marcacion"
 * @weblogic.cache max-beans-in-cache="300"
 * @weblogic.pool max-beans-in-free-pool="300" initial-beans-in-free-pool="0"
 * @weblogic.delay-database-insert-until ejbCreate
 *  
 * @weblogic.enable-call-by-reference True
 * @weblogic.clustering home-is-clusterable="True" stateless-bean-is-clusterable="True"
 * @weblogic.transaction-descriptor trans-timeout-seconds = "300"
 * @author CGARRATT
 *
 */
public abstract class T1275CMP  extends EJBEstandarLocalCMP
  implements EntityBean {

  EntityContext entityContext;

  /**
   * 
   * @ejb.create-method view-type="local"
   * 
   * @param codPers
   * @param fecha
   * @param hora
   * @param reloj
   * @return
   * @throws RemoteException
   * @throws CreateException
   */
  public T1275CMPPK ejbCreate(java.lang.String codPers, Date fecha,
                                       java.lang.String hora, java.lang.String reloj) throws CreateException{
    setCodPers(codPers);
    setFecha(fecha);
    setHora(hora);
    setReloj(reloj);
    return null;
  }

  public void ejbPostCreate(java.lang.String codPers, Date fecha, java.lang.String hora, java.lang.String reloj) throws CreateException {
    /**@todo Complete this method*/
  }

  /**
   * @ejb.interface-method  view-type="local"
   * 
   * @ejb.persistence column-name="cod_pers"
   *                  jdbc-type="VARCHAR"
   * @ejb.transaction type="Required"
   * 
   * @param cod_pers String
   */
  public abstract void setCodPers(java.lang.String codPers);
  /**
   * @ejb.interface-method  view-type="local"
   * 
   * @ejb.persistence column-name="fecha" jdbc-type="DATE"
   * @ejb.transaction type="Required"
   * 
   * @param fecha String
   */
  public abstract void setFecha(java.sql.Date fecha);
  /**
   * @ejb.interface-method  view-type="local"
   * 
   * @ejb.persistence column-name="hora"
   *                  jdbc-type="VARCHAR"
   * @ejb.transaction type="Required"
   * 
   * @param hora String
   */
  public abstract void setHora(java.lang.String hora);
  /**
   * @ejb.interface-method  view-type="local"
   * 
   * @ejb.persistence column-name="reloj"
   *                  jdbc-type="VARCHAR"
   * @ejb.transaction type="Required"
   * 
   * @param reloj String
   */
  public abstract void setReloj(java.lang.String reloj);
  /**
   * @ejb.interface-method  view-type="local"
   * 
   * @ejb.persistence column-name="fcreacion"
   *                  jdbc-type="TIMESTAMP"
   * @ejb.transaction type="Required"
   * 
   * @param uorgan String
   */
  public abstract void setFcreacion(java.sql.Timestamp fcreacion);
  /**
   * @ejb.interface-method  view-type="local"
   * 
   * @ejb.persistence column-name="cuser_crea"
   *                  jdbc-type="VARCHAR"
   * @ejb.transaction type="Required"
   * 
   * @param cuser_crea String
   */
  public abstract void setCuserCrea(java.lang.String cuserCrea);
  /**
   * @ejb.interface-method  view-type="local"
   * 
   * @ejb.persistence column-name="fmod"
   *                  jdbc-type="TIMESTAMP"
   * @ejb.transaction type="Required"
   * 
   * @param fmod String
   */
  public abstract void setFmod(java.sql.Timestamp fmod);
  /**
   * @ejb.interface-method  view-type="local"
   * 
   * @ejb.persistence column-name="cuser_mod"
   *                  jdbc-type="VARCHAR"
   * @ejb.transaction type="Required"
   * 
   * @param cuser_mod String
   */
  public abstract void setCuserMod(java.lang.String cuserMod);
  /**
   * @ejb.interface-method  view-type="local"
   * 
   * @ejb.persistence column-name="cod_pase"
   *                  jdbc-type="VARCHAR"
   * @ejb.transaction type="Required"
   * 
   * @param cod_pase String
   */
  public abstract void setCodPase(String codPase);
  /**
   * @ejb.interface-method  view-type="local"
   * 
   * @ejb.persistence column-name="sdel"
   *                  jdbc-type="VARCHAR"
   * @ejb.transaction type="Required"
   * 
   * @param sdel String
   */
  public abstract void setEstado(String estado);
  /**
   * @ejb.interface-method  view-type="local"
   * 
   * @ejb.persistence column-name="cod_pers"
   *                  jdbc-type="VARCHAR"
   * @ejb.pk-field
   * 
   * @ejb.transaction type="Required"
   * 
   */
  public abstract java.lang.String getCodPers();
  /**
   * @ejb.interface-method  view-type="local"
   * 
   * @ejb.persistence column-name="fecha" jdbc-type="DATE"
   * @ejb.pk-field
   * 
   * @ejb.transaction type="Required"
   * 
   */
  public abstract Date getFecha();
  /**
   * @ejb.interface-method  view-type="local"
   * 
   * @ejb.persistence column-name="hora" jdbc-type="VARCHAR"
   * @ejb.pk-field
   * 
   * @ejb.transaction type="Required"
   * 
   */
  public abstract String getHora();
  /**
   * @ejb.interface-method  view-type="local"
   * 
   * @ejb.persistence column-name="reloj"
   *                  jdbc-type="VARCHAR"
   * @ejb.pk-field
   * 
   * @ejb.transaction type="Required"
   * 
   */
  public abstract java.lang.String getReloj();
  /**
   * @ejb.interface-method  view-type="local"
   * 
   * @ejb.persistence column-name="fcreacion"
   *                  jdbc-type="TIMESTAMP"
   * 
   * @ejb.transaction type="Required"
   * 
   */
  public abstract java.sql.Timestamp getFcreacion();
  /**
   * @ejb.interface-method  view-type="local"
   * 
   * @ejb.persistence column-name="cuser_crea"
   *                  jdbc-type="VARCHAR"
   * 
   * @ejb.transaction type="Required"
   * 
   */
  public abstract java.lang.String getCuserCrea();
  /**
   * @ejb.interface-method  view-type="local"
   * 
   * @ejb.persistence column-name="fmod"
   *                  jdbc-type="TIMESTAMP"
   * 
   * @ejb.transaction type="Required"
   * 
   */
  public abstract java.sql.Timestamp getFmod();
  /**
   * @ejb.interface-method  view-type="local"
   * 
   * @ejb.persistence column-name="cuser_mod"
   *                  jdbc-type="VARCHAR"
   * 
   * @ejb.transaction type="Required"
   * 
   */
  public abstract java.lang.String getCuserMod();
  /**
   * @ejb.interface-method  view-type="local"
   * 
   * @ejb.persistence column-name="cod_pase"
   *                  jdbc-type="VARCHAR"
   * 
   * @ejb.transaction type="Required"
   * 
   */  
  public abstract String getCodPase();
  /**
   * @ejb.interface-method  view-type="local"
   * 
   * @ejb.persistence column-name="sdel"
   *                  jdbc-type="VARCHAR"
   * 
   * @ejb.transaction type="Required"
   * 
   */
  public abstract String getEstado();
  

}