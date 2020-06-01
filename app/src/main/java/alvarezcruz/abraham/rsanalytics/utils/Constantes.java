package alvarezcruz.abraham.rsanalytics.utils;

public class Constantes {

    // API
    public final static String URL_SERVER = "http://192.168.1.70:34080";

    // Paths
    public final static String RUTA_USUARIO = "/usuario";
    public final static String RUTA_NOTIFICACIONES = "/notificaciones";
    public final static String RUTA_INVITACION = "/invitacion";
    public final static String RUTA_GRUPO = "/grupo";
    public final static String RUTA_INFORMES = "/informe";
    public final static String RUTA_MUNICIPIOS = "/municipios";

    // Endpoints
    // Usuario
    public final static String LOGIN_USUARIO_ENDPOINT = "/login";
    public final static String REGISTRO_USUARIO_ENDPOINT = "/registro";
    public final static String INFORMACION_GENERAL_USUARIO_ENDPOINT = "/informacionGeneral";
    public final static String MARCAR_NOTIFICACIONES_LEIDAS_ENDPOINT = "/marcarNotificaciones";
    public final static String INVITAR_USUARIO_ENDPOINT= "/invitar";
    public final static String ACEPTAR_INVITACION_ENDPOINT = "/aceptar";
    public final static String RECHAZAR_INVITACION_ENDPOINT = "/rechazar";
    public final static String REGISTRO_GRUPO_ENDPOINT = "/registro";
    public final static String ABANDONAR_GRUPO_ENDPOINT = "/abandonar";
    public final static String DATOS_GENERALES_GRUPO_ENDPOINT = "/datosGenerales";
    public final static String LISTAR_INFORMES_ENDPOINT = "/listar";
    public final static String SOLICITAR_INFORME_ENDPOINT = "/solicitar";
    public final static String BUSCAR_MUNICIPIO = "/buscarParecido";

    // REGEX
    public final static String REGEX_CORREO = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    public final static String REGEX_NOMBRE_PERSONA = "^[A-Za-zñÑáéíóúÁÉÍÓÚ\\s]+$";
    public final static String REGEX_CONTRASENIA = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\\$%\\^&\\*])";
    public final static String REGEX_TELEFONO = "^[0-9]{9}$";
    public final static String REGEX_NOMBRE_GRUPO = "[`!@#$%^&*()+\\=\\[\\]{};':\"\\\\|,<>\\/?~]";

    public final static int LONGITUD_MAX_NOMBRE_GRUPO = 50;

}
