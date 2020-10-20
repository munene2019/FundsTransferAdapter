/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonOperations;

import java.util.Iterator;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author rmunene
 */
public class Utilities {
    public Map<String, String> parseJSON(JSONObject json, Map<String, String> dataFields) throws JSONException {
        Iterator<String> keys = json.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            String val = null;
            try {
                JSONObject value = json.getJSONObject(key);
                
                parseJSON(value, dataFields);
            } catch (Exception e) {
                if (json.isNull(key)) {
                    val = "";
                } else {
                    try {
                        val = json.getString(key);
                    } catch (Exception ex) {
                        System.out.println("Error:" + ex.getMessage());
                    }
                }
            }

            if (val != null && key != null) {
                dataFields.put(key, val);
            }
        }
        return dataFields;
    }
}
