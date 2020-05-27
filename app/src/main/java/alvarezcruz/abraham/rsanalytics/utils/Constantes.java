package alvarezcruz.abraham.rsanalytics.utils;

public class Constantes {

    // API
    public final static String URL_SERVER = "http://192.168.1.70:34080";

    // Paths
    public final static String RUTA_USUARIO = "/usuario";
    public final static String RUTA_NOTIFICACIONES = "/notificaciones";

    // Endpoints
    // Usuario
    public final static String LOGIN_USUARIO_ENDPOINT = "/login";
    public final static String REGISTRO_USUARIO_ENDPOINT = "/registro";
    public final static String INFORMACION_GENERAL_USUARIO_ENDPOINT = "/informacionGeneral";

    // REGEX
    public final static String REGEX_CORREO = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    public final static String REGEX_NOMBRE_PERSONA = "^[A-Za-zñÑáéíóúÁÉÍÓÚ\\s]+$";
    public final static String REGEX_CONTRASENIA = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\\$%\\^&\\*])";
    public final static String REGEX_TELEFONO = "^[0-9]{9}$";

}
