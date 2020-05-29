package alvarezcruz.abraham.rsanalytics.model.pojo;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Usuario implements Serializable {

    public enum Sexo {
        HOMBRE,
        MUJER
    }

    private String nombre;
    private String primerApellido;
    private String segundoApellido;
    private String correo;
    private boolean esMiembro;
    private long fechaMiembro;
    private Sexo sexo;

    public Usuario(){}

    public Usuario(String nombre, String primerApellido, String correo, boolean esResponsable, String sexo) {
        this.nombre = nombre;
        this.primerApellido = primerApellido;
        this.correo = correo;
        this.esMiembro = esResponsable;
        setSexo(sexo);
    }

    public Usuario(String nombre, String primerApellido, String correo, boolean esResponsable, Sexo sexo) {
        this.nombre = nombre;
        this.primerApellido = primerApellido;
        this.correo = correo;
        this.esMiembro = esResponsable;
        this.sexo = sexo;
    }

    public static Usuario miembroFromJson(JSONObject jsonObject){

        Usuario usuario = new Usuario();

        usuario.nombre = jsonObject.optString("nombre", "");
        usuario.fechaMiembro = jsonObject.optLong("miembroDesde", -1);
        usuario.setSexo(jsonObject.optString("genero", ""));

        return usuario;
    }

    @NonNull
    @Override
    public String toString() {
        return "Usuario \'" + nombre + " " + primerApellido + "\'";
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPrimerApellido() {
        return primerApellido;
    }

    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(String sexo){

        if (sexo.equals("H")){
            this.sexo = Sexo.HOMBRE;
        }

        else if (sexo.equals("M")){
            this.sexo = Sexo.MUJER;
        }
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public String getSegundoApellido() {
        return segundoApellido;
    }

    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    public long getFechaMiembro() {
        return fechaMiembro;
    }

    public void setFechaMiembro(long fechaMiembro) {
        this.fechaMiembro = fechaMiembro;
    }
}
