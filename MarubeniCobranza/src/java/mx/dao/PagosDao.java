package mx.dao;

import java.util.List;
import mx.model.Pagos;

public interface PagosDao {

    public List<Pagos> listaFactura();

    public int insertFactf01(Pagos paramPagos);

    public void updateFactf01(int paramInt, String paramString);

    public List<Pagos> listarFacturasEnvioCorreos();

    public List<Pagos> listarFacturasSeleccionadas();

    public List<String> listaUsuariosAvisoPago();

    public void updateEnvio(String paramString);

    //Lista para cancelar Pagos
    public List<Pagos> listaCancelarFacturas();
}
