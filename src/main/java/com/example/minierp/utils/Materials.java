package com.example.minierp.utils;

import javafx.scene.control.Alert;

public final class Materials {

    private Materials(){}

    public static String getRawType(String final_type){
        if(final_type.equals("BlueProductLid")  || final_type.equals("BlueProductBase")  ) return "BlueRawMaterial";
        if(final_type.equals("GreenProductLid") || final_type.equals("GreenProductBase") ) return "GreenRawMaterial";
        if(final_type.equals("MetalProductLid") || final_type.equals("MetalProductBase") ) return "MetalRawMaterial";
        return "";
    }


}
