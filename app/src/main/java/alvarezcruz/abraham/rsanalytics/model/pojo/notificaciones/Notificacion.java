package alvarezcruz.abraham.rsanalytics.model.pojo.notificaciones;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.util.Date;

import alvarezcruz.abraham.rsanalytics.utils.Utils;

public class Notificacion {

    private int id;
    private String mensaje;
    private boolean leida;
    private String emisor;
    private Accion accion;
    private Date fechaEnvio;

    public Notificacion(){}

    public Notificacion(int id, String mensaje, boolean leida, String emisor, Accion accion, Date fechaEnvio) {
        this.id = id;
        this.mensaje = mensaje;
        this.leida = leida;
        this.emisor = emisor;
        this.accion = accion;
        this.fechaEnvio = fechaEnvio;
    }

    public Notificacion(JSONObject jsonObject){
        this.parsearJson(jsonObject);
    }

    private void parsearJson(JSONObject jsonObject){

        this.id = jsonObject.optInt("id", -1);
        this.mensaje = jsonObject.optString("mensaje", "");
        this.leida = jsonObject.optBoolean("leida", false);
        this.fechaEnvio = jsonObject.has("fecha") ? new Date((Long) Utils.obtenerDelJSON(jsonObject,"fecha")) : null;
        this.emisor = jsonObject.optString("emisor", null);
        this.accion = jsonObject.has("accion") ? new Accion((JSONObject) Utils.obtenerDelJSON(jsonObject, "accion")) : null;
    }

    @NonNull
    @Override
    public String toString() {
        return "Notificacion enviada por \'" + emisor + "\'. Tiene accion (" + (accion != null) +  "). Tiene mensaje (" + (mensaje != null) + ")" ;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean isLeida() {
        return leida;
    }

    public void setLeida(boolean leida) {
        this.leida = leida;
    }

    public String getEmisor() {
        return emisor;
    }

    public void setEmisor(String emisor) {
        this.emisor = emisor;
    }

    public Accion getAccion() {
        return accion;
    }

    public void setAccion(Accion accion) {
        this.accion = accion;
    }

    public Date getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(Date fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }
}
