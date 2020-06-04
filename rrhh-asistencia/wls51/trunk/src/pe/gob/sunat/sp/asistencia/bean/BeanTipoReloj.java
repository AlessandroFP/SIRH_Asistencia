package pe.gob.sunat.sp.asistencia.bean;

import java.io.Serializable;
import java.sql.Date;

public class BeanTipoReloj implements Serializable {

  public String reloj = "";
  public String descrip = "";
  public String sede = "";
  public String est_id = "";
  public Date fgraba;
  public String cuser;

  public String getReloj() {
    return (reloj);
  }

  public void setReloj(String reloj) {
    this.reloj = reloj;
  }

  public String getDescrip() {
    return (descrip);
  }

  public void setDescrip(String descrip) {
    this.descrip = descrip;
  }

  public String getSede() {
    return (sede);
  }

  public void setSede(String sede) {
    this.sede = sede;
  }

  public String getEst_id() {
    return (est_id);
  }

  public void setEst_id(String est_id) {
    this.est_id = est_id;
  }

  public Date getFgraba() {
    return (fgraba);
  }

  public void setFgraba(Date fgraba) {
    this.fgraba = fgraba;
  }

  public String getCuser() {
    return (cuser);
  }

  public void setCuser(String cuser) {
    this.cuser = cuser;
  }

}