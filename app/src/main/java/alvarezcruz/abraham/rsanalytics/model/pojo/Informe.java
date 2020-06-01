package alvarezcruz.abraham.rsanalytics.model.pojo;

import org.json.JSONObject;

public class Informe {

    private int id;
    private long fechaSolicitud;
    private boolean pendiente;
    private String municipio;
    private String nombreArchivo;

    public Informe(){}

    public Informe(int id, long fechaSolicitud, boolean pendiente, String municipio, String nombreArchivo) {
        this.id = id;
        this.fechaSolicitud = fechaSolicitud;
        this.pendiente = pendiente;
        this.municipio = municipio;
        this.nombreArchivo = nombreArchivo;
    }

    public static Informe fromJson(JSONObject jsonObject){

        Informe informe = new Informe();
        informe.id = jsonObject.optInt("id",-1);
        informe.fechaSolicitud = jsonObject.optLong("fechaSolicitudRaw", -1);
        informe.pendiente = jsonObject.optBoolean("pendiente", false);
        informe.municipio = jsonObject.optString("municipio", "");
        informe.nombreArchivo = jsonObject.optString("nombreArchivo", "");

        return informe;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(long fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public boolean isPendiente() {
        return pendiente;
    }

    public void setPendiente(boolean pendiente) {
        this.pendiente = pendiente;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }
}
