package alvarezcruz.abraham.rsanalytics.utils;

public enum  TipoInmueble {
    VIVIENDA(1);

    public final int id;
    TipoInmueble(int id){
        this.id = id;
    }

    public static TipoInmueble conId(int id){
        for (TipoInmueble tipoInmueble : TipoInmueble.values()){
            if (tipoInmueble.id == id) return tipoInmueble;
        }
        return null;
    }
}
