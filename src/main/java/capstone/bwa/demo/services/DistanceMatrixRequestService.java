package capstone.bwa.demo.services;

import capstone.bwa.demo.constants.MainConstants;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.awt.geom.Point2D;


public class DistanceMatrixRequestService {

    OkHttpClient client = new OkHttpClient();

    public String run(String url) {
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {

        }
        return null;
    }

    public String googleMatrix(double latLocation, double lngLocation, double x2, double y2) {
        DistanceMatrixRequestService request = new DistanceMatrixRequestService();
        String response = "";
        try {
            String url_request = "https://maps.googleapis.com/maps/api/distancematrix/json?origins="
                    + latLocation + "," + lngLocation
                    + "&destinations=" + x2 + "," + y2
                    + "&language=vi-VN&key=" + MainConstants.API_KEY;

            response = request.run(url_request);
//            System.out.println(response);

        } catch (Exception e) {

        }
        return response;
    }

    // distance radius circle 1 / 100 000 (*100)
    public double calculateDistanceBetweenPoints(double x1, double y1, double x2, double y2) {
        return Point2D.distance(x1, y1, x2, y2);
    }

}