package alvarezcruz.abraham.rsanalytics.utils;

public enum TipoContrato {
    COMPRA(1),
    VENTA(2);

    public final int id;
    TipoContrato(int id){
        this.id = id;
    }

    public static TipoContrato conId(int id){
        for (TipoContrato tipoContrato : TipoContrato.values()){
            if (tipoContrato.id == id) return tipoContrato;
        }
        return null;
    }
}
