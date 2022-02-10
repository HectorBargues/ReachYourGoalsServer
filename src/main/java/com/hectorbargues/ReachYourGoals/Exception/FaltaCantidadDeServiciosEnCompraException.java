package com.hectorbargues.ReachYourGoals.Exception;

public class FaltaCantidadDeServiciosEnCompraException extends RuntimeException {

    public FaltaCantidadDeServiciosEnCompraException(long id_servicio) {
        super("ERROR: Falta Cantidad de Servicios " + id_servicio + " en el proceso de compra");
    }

}
