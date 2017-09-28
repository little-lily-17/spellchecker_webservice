/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

/**
 *
 * @author nurlailifajriyah
 */
@WebServlet(name = "AppDashboard", urlPatterns = {"/AppDashboard"})
public class AppDashboard extends HttpServlet {

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //create MongoAccessor object and call mongodbConnect()method to connect to the db
        MongoAccessor ma = new MongoAccessor();
        ma.mongodbConnect();
        //get data for analytics by calling methods in MongoAccessor class
        ArrayList<JSONObject> topOsResult = ma.getTopOsVersion();
        ArrayList<JSONObject> topDeviceResult = ma.getTopDeviceModel();
        ArrayList<JSONObject> processingTime = ma.getProcessingTime();
        ArrayList<JSONObject> apiLatency = ma.getTopLatency();
        ArrayList<JSONObject> logResult = ma.getLog();
        //put results from MongoAccessor as request attribute
        request.setAttribute("doctype", "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
        request.setAttribute("topOsResult", topOsResult);
        request.setAttribute("topDeviceResult", topDeviceResult);
        request.setAttribute("processingTime", processingTime);
        request.setAttribute("apilatency", apiLatency);
        request.setAttribute("logResult", logResult);
        //set dashboard.jsp to be the view
        RequestDispatcher view = request.getRequestDispatcher("dashboard.jsp");
        //forward request and response to view
        view.forward(request, response);
    }

}
