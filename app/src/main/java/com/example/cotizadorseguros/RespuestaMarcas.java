package com.example.cotizadorseguros;

import java.util.List;

public class RespuestaMarcas {

    private int Count;

    private String Message;

    private String SearchCriteria;

    private List<MarcaVehiculo> Results;

    public int getCount() {
        return Count;
    }

    public String getMessage() {
        return Message;
    }

    public String getSearchCriteria() {
        return SearchCriteria;
    }

    public List<MarcaVehiculo> getResults() {
        return Results;
    }
}