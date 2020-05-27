package alvarezcruz.abraham.rsanalytics.model.pojo.notificaciones;

import org.json.JSONObject;

public class Accion {

    private boolean completada;
    private int tipoAccion;
    private String nombreGrupo;

    public Accion(){}

    public Accion(boolean completada, int tipoAccion, String nombreGrupo) {
        this.completada = completada;
        this.tipoAccion = tipoAccion;
        this.nombreGrupo = nombreGrupo;
    }

    public Accion(JSONObject jsonObject){
        this.parsearJson(jsonObject);
    }

    private void parsearJson(JSONObject jsonObject){
        this.completada = jsonObject.optBoolean("completada", false);
        this.tipoAccion = jsonObject.optInt("tipo", -1);
        this.nombreGrupo = jsonObject.optString("nombreGrupo", null);
    }

    public boolean isCompletada() {
        return completada;
    }

    public void setCompletada(boolean completada) {
        this.completada = completada;
    }

    public int getTipoAccion() {
        return tipoAccion;
    }

    public void setTipoAccion(int tipoAccion) {
        this.tipoAccion = tipoAccion;
    }

    public String getNombreGrupo() {
        return nombreGrupo;
    }

    public void setNombreGrupo(String nombreGrupo) {
        this.nombreGrupo = nombreGrupo;
    }
}
