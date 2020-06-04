package pe.gob.sunat.sp.asistencia.bean;

import java.io.Serializable;

/**
 * <p>Title: BeanCompensaOnom </p>
 * <p>Description: Bean que almacena la información tanto
 * de la fécha final de una compensación por onomástico como
 * los días que quedan pendientes por compensar.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class BeanCompensaOnom implements Serializable{

  private int diasXCompensar = 8;
  private String fechaIni = "";
  private String fechaFin = "";

  public int getDiasXCompensar() {
    return diasXCompensar;
  }
  public void setDiasXCompensar(int diasXCompensar) {
    this.diasXCompensar = diasXCompensar;
  }
  public String getFechaIni() {
    return fechaIni;
  }
  public void setFechaIni(String fechaIni) {
    this.fechaIni = fechaIni;
  }
  public String getFechaFin() {
    return fechaFin;
  }
  public void setFechaFin(String fechaFin) {
    this.fechaFin = fechaFin;
  }

}