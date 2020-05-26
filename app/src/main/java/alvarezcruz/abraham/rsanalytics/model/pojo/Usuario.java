package alvarezcruz.abraham.rsanalytics.model.pojo;

import java.io.Serializable;

public class Usuario implements Serializable {

    enum Sexo {
        HOMBRE,
        MUJER
    }

    private String nombre;
    private String primerApellido;
    private String correo;
    private Sexo sexo;

    public Usuario(){}

    public Usuario(String nombre, String primerApellido, String correo, String sexo) {
        this.nombre = nombre;
        this.primerApellido = primerApellido;
        this.correo = correo;
        setSexo(sexo);
    }

    public Usuario(String nombre, String primerApellido, String correo, Sexo sexo) {
        this.nombre = nombre;
        this.primerApellido = primerApellido;
        this.correo = correo;
        this.sexo = sexo;
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
}