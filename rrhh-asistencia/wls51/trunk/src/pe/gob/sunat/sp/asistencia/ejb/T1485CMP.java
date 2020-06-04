package pe.gob.sunat.sp.asistencia.ejb;

import java.util.Map;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;

import pe.gob.sunat.ejb.EJBEstandarLocalCMP;

/**
 * 
 * 
 * @ejb.bean
 *    cmp-version="2.x"
 *    description="Entidad de la tabla T1485seg_uorga "
 *    local-jndi-name="ejb/rrhh/cmp/sp/asistencia/t1485cmp"
 *    name="T1485CMP"
 *    type="CMP"
 *    view-type="local"
 *    schema="T1485Schema" 
 * 
 * @ejb.home local-class="pe.gob.sunat.sp.asistencia.ejb.T1485CMPHome"
 *                local-extends="javax.ejb.EJBLocalHome"
 *
 * @ejb.interface local-class="pe.gob.sunat.sp.asistencia.ejb.T1485CMPLocal"
 *                local-extends="javax.ejb.EJBLocalObject"
 * 
 * @ejb.transaction-type type="Container"
 * 
 * @ejb.persistence table-name="T1485seg_uorga"
 * @ejb.pk        class="pe.gob.sunat.sp.asistencia.ejb.T1485CMPPK"
 *                extends="java.lang.Object"
 *                implements="java.io.Serializable"
 *  
 * @jboss.container-configuration name="Standard CMP 2.x EntityBean"
 * @jboss.persistence create-table="false"
 *                    remove-table="false"
 *                    table-name="T1485seg_uorga"
 * 
 * @jboss.version="3.2"
 * 
 * @weblogic.persistence create-table="false" remove-table="false" table-name="T1485seg_uorga"
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
public abstract class T1485CMP
  extends EJBEstandarLocalCMP
  implements EntityBean {

  private EntityContext ctx;

  /**
   * @ejb.create-method view-type="local"
   * 
   * @param data Map
   * @return java.lang.Object
   * @throws CreateException
   */
  public java.lang.Object ejbCreate(Map data)
          throws CreateException {
      try {
        setUOrgan((String)data.get("u_organ"));
        setCodPers((String)data.get("cod_pers"));
        setOperacion((String)data.get("operacion"));
        setFGraba((java.sql.Timestamp)data.get("fgraba"));
        setCUser((String)data.get("cuser"));
        
        return null;
      }
      catch (Exception e) {
          throw new EJBException(e);
      }
  }

  public void ejbPostCreate(Map data)
              throws CreateException {
  }
  /**
   * @ejb.interface-method  view-type="local"
   * 
   * @ejb.persistence column-name="u_organ"
   *                  jdbc-type="VARCHAR"
   * @ejb.transaction type="Required"
   * 
   * @param uorgan String
   */
  public abstract void setUOrgan(String uorgan);
  /**
   * @ejb.interface-method  view-type="local"
   * 
   * @ejb.persistence column-name="cod_pers"
   *                  jdbc-type="VARCHAR"
   * @ejb.transaction type="Required"
   * 
   * @param cod_pers String
   */
  public abstract void setCodPers(String cod_pers);
  /**
   * @ejb.interface-method  view-type="local"
   * 
   * @ejb.persistence column-name="operacion"
   *                  jdbc-type="VARCHAR"
   * @ejb.transaction type="Required"
   * 
   * @param operacion String
   */
  public abstract void setOperacion(String operacion);
  /**
   * @ejb.interface-method  view-type="local"
   * 
   * @ejb.persistence column-name="fgraba"
   *                  jdbc-type="TIMESTAMP"
   * @ejb.transaction type="Required"
   *     
   * @param fgraba Timestamp
   */
  public abstract void setFGraba(java.sql.Timestamp fgraba);
  /**
   * @ejb.interface-method  view-type="local"
   * 
   * @ejb.persistence column-name="cuser"
   *                  jdbc-type="VARCHAR"
   * @ejb.transaction type="Required"
   * 
   * @param cuser String
   */
  public abstract void setCUser(String cuser);

  /**
   * @ejb.interface-method  view-type="local"
   * 
   * @ejb.persistence column-name="u_organ"
   *                  jdbc-type="VARCHAR"
   * @ejb.pk-field
   * 
   * @ejb.transaction type="Required"
   * 
   */
  public abstract String getUOrgan();
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
  public abstract String getCodPers();
  /**
   * @ejb.interface-method  view-type="local"
   * 
   * @ejb.persistence column-name="operacion"
   *                  jdbc-type="VARCHAR"
   * @ejb.pk-field
   * 
   * @ejb.transaction type="Required"
   * 
   */
  public abstract String getOperacion();
  /**
   * @ejb.interface-method  view-type="local"
   * 
   * @ejb.persistence column-name="fgraba"
   *                  jdbc-type="TIMESTAMP"
   * 
   * @ejb.transaction type="Required"
   * 
   */
  public abstract java.sql.Timestamp getFGraba();
  /**
   * @ejb.interface-method  view-type="local"
   * 
   * @ejb.persistence column-name="cuser"
   *                  jdbc-type="VARCHAR"
   * 
   * @ejb.transaction type="Required"
   * 
   */
  public abstract String getCUser();

}
