package mx.report;

import java.io.Serializable;
import java.sql.SQLException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

@javax.inject.Named("polizaPreviewBean")
@ViewScoped
public class polizaPreviewBean implements Serializable {

    private String nombreUsuario;
    private String folioUsuario;

    public String getNombreUsuario() {
        return this.nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getFolioUsuario() {
        return this.folioUsuario;
    }

    public void setFolioUsuario(String folioUsuario) {
        this.folioUsuario = folioUsuario;
    }

    public void polizaPreviewBean() throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        polizaPreview rCliente = new polizaPreview();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ServletContext servletContext = (ServletContext) facesContext.getExternalContext().getContext();
        String ruta = servletContext.getRealPath("/Report/polizaPreview.jasper");
        rCliente.getReporte(ruta, this.nombreUsuario = request.getParameter("frmLayoutPreview:codigo"), this.folioUsuario = request.getParameter("frmLayoutPreview:folio"));
        FacesContext.getCurrentInstance().responseComplete();
    }
 }


