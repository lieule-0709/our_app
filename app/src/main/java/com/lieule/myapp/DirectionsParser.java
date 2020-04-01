package com.lieule.myapp;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DirectionsParser {
    /**
     * Returns a list of lists containing latitude and longitude from a JSONObject
     */
    public List<List<HashMap<String, String>>> parse(JSONObject jObject) {
        // routes chứa các giá trị tọa độ mà ta cần đề vẽ đường đi
        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();

        // 3 biến jRoutes, jLegs, jSteps là để lưu value của jObject
        // Vì giá trị của chúng trong JOpject là array, nên kiểu của chúng là JSONArray
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        // 1.Lấy ra value của routes,
        // 2.từ value của routes thì lấy ra value của legs,
        // 3.từ value của legs lấy ra value của steps
        try {
            // -> 1
            jRoutes = jObject.getJSONArray("routes");

            // Loop for all routes
            // Họ dùng loop để đề phòng routes có nhiều hơn 1 value
            // Trong trường hợp của mình thì chỉ có 1 value
            for (int i = 0; i < jRoutes.length(); i++) {
                // -> 2
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");

                // path là các tọa độ mình lấy được
                List path = new ArrayList<HashMap<String, String>>();

                //Loop for all legs
                // 3 ->
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                    //Loop for all steps
                    // 4. Lấy ra value của polyline
                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline = "";
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        List list = decodePolyline(polyline);

                        //Loop for all points
                        // 5. Lặp trong list, và push từng tọa độ vào path
                        for (int l = 0; l < list.size(); l++) {
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
                            hm.put("lon", Double.toString(((LatLng) list.get(l)).longitude));

                            /* hm : {
                                "lat": ....,
                                "lon": .....
                            }*/
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }
        // Trả về
        return routes;
    }


    /**
     * Method to decode polyline
     * Source : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     */
    private List decodePolyline(String encoded) {

        List poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }
}