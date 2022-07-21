package mx.bean;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import mx.conexion.DAO;
import mx.dao.PagosDao;
import mx.impl.PagosDaoImpl;
import mx.model.Pagos;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;

@Named(value = "cancelarPagosBean")
@ViewScoped
public class CancelarPagosBean extends DAO implements Serializable {

    private List<Pagos> lista;
    private Pagos pagos;

    public CancelarPagosBean() {
        this.lista = new ArrayList<>();
        this.pagos = new Pagos();
    }

    public Pagos getPagos() {
        return pagos;
    }

    public void setPagos(Pagos pagos) {
        this.pagos = pagos;
    }

    public void setLista(List<Pagos> lista) {
        this.lista = lista;
    }

    public List<Pagos> getLista() {
        PagosDao pDao = new PagosDaoImpl();
        this.lista = pDao.listaCancelarFacturas();
        return lista;
    }

    public void cancelar(Pagos pago) {

        try {

            //REGRESAR LOS SALDOS PAGADOS A LA TABLA cuenm01 DE LA BD COBRANZA
            this.Conectar();

            PreparedStatement psCuen = getCn().prepareStatement("SELECT NUM_MONED FROM cuenm01 WHERE NO_FACTURA='" + pago.getNoFactura() + "'");
            ResultSet rsCuen = psCuen.executeQuery();
            int coin = 0;

            while (rsCuen.next()) {
                coin = rsCuen.getInt("NUM_MONED");
            }
            String sqlcuen = null;
            if (coin == 2) {
                sqlcuen = "UPDATE cuenm01 "
                        + "SET SALDO=SALDO + " + pago.getImporteusd() + ", "
                        + "PAGO_MULTIPLE=NULL, PROCESADO=0, BANCO=NULL, DEPTO=NULL, AVISO=NULL, FECHA_ENVIO=NULL, FECHA_PAGO=NULL"
                        + " WHERE NO_FACTURA='" + pago.getNoFactura() + "'";
            } else {
                sqlcuen = "UPDATE cuenm01 "
                        + "SET SALDO=SALDO + " + pago.getImporte() + ", "
                        + "PAGO_MULTIPLE=NULL, PROCESADO=0, BANCO=NULL, DEPTO=NULL, AVISO=NULL, FECHA_ENVIO=NULL, FECHA_PAGO=NULL"
                        + " WHERE NO_FACTURA='" + pago.getNoFactura() + "'";
            }

            PreparedStatement pSaldos = getCn().prepareStatement(sqlcuen);
            pSaldos.executeUpdate();
            sqlcuen = null;
            this.Cerrar();

            //BORRAR EN LA TABLA pagos DE LA BD COBRANZA
            this.Conectar();
            PreparedStatement psPagos = getCn().prepareStatement("DELETE FROM pagos WHERE ID='" + pago.getId() + "'");
            psPagos.executeUpdate();
            this.Cerrar();

            //BORRAR Y/O ACTUALIZAR LA TABLA fpmultiple
            this.Conectar();

            PreparedStatement psfp = getCn().prepareStatement("UPDATE fpmultiple SET FACTURAS=REPLACE(FACTURAS,'" + pago.getNoFactura() + "', '') WHERE ID='" + pago.getPagoMultiple() + "' ");
            psfp.executeUpdate();
            this.Cerrar();

            //BORRAR REGISTROS EN SAE-TABLA CUENDET01
            this.ConectarSAE();
            String sql1 = "DELETE FROM CUEN_DET01 WHERE CTLPOL='" + pago.getId() + "'";
//                    String sql1 = "DELETE FROM CUEN_DET01 "
//                            + "WHERE REFER='" + lista.get(i).getNoFactura() + "' AND NO_PARTIDA='" + lista.get(i).getPartida() + "' "
//                            + "AND IMPORTE='" + lista.get(i).getImporte() + "' AND FECHA_APLI='" + lista.get(i).getFechaPago() + "'";
            // PreparedStatement ps = getCnSAE().prepareStatement(sql);
            PreparedStatement ps = getCnSAE().prepareStatement(sql1);
            ps.executeUpdate();
            this.CerrarSAE();

        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "SISTEMA DE COBRANZA", "CORRECCIÓN DE PAGO APLICADA"));
            RequestContext.getCurrentInstance().update("frmPrincipal:tblPagos");
            System.err.println(e.getMessage());
        }

        //RequestContext.getCurrentInstance().execute("swal({ title: 'Proceso terminado',text: '', icon: 'success', button: 'OK',});");
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "SISTEMA DE COBRANZA", "CORRECCIÓN DE PAGO APLICADA"));
        RequestContext.getCurrentInstance().update("frmPrincipal:tblPagos");

    }

    public void resetearTabla() {
        DataTable datatable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmPrincipal:tblPagos");
        datatable.reset();
    }

}
