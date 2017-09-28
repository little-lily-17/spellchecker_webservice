
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * Project4Task2Servlet is a web service to handle request from Android mobile
 * client It receive input from URI query and return a JSON format String
 *
 * @author nurlailifajriyah
 */
@WebServlet(name = "Project4Task1Servlet", urlPatterns = {"/Project4Task1Servlet"})
public class Project4Task2Servlet extends HttpServlet {

    //Client Data
    String os;
    String osVersion;
    String model;
    String manufacture;
    String clientRequest; //input from user

    long _id; //db ID
    String clientResponse; //result for client
    long requestTime; //receiving request time
    long responseTime; //right before sending the response to client

    //API Data
    long apiRequestTime;
    long apiResponseTime;
    String apiRequestQuery; //url query for accessing the api
    String apiResponse; //result from api

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
        HttpClient httpclient = HttpClients.createDefault();
        requestTime = System.currentTimeMillis();
        try {
            //id generation
            Random random = new Random();
            _id = System.currentTimeMillis() * 1024 + random.nextInt(10000);

            //get data from request URI
            String queryString = (request.getQueryString());
            Map<String, String> queryValue = splitURLQuery(queryString);

            //Intantiate OutputGeneratot and initialize input
            Project4Task2Model classModel = new Project4Task2Model();
            classModel.input = " "+queryValue.get("input")+ " ";

            //assign each parameter value to the class variable
            os = queryValue.get("os");
            manufacture = queryValue.get("manufacture");
            model = queryValue.get("model");
            osVersion = queryValue.get("osversion");
            clientRequest = queryValue.get("input");

            //build URI to call the Spell Check Bing API
            //https://dev.cognitive.microsoft.com/docs/services/56e73033cf5ff80c2008c679/operations/56e73036cf5ff81048ee6727
            URIBuilder builder = new URIBuilder("https://api.cognitive.microsoft.com/bing/v5.0/spellcheck/");
            builder.setParameter("text", URLEncoder.encode(clientRequest, "UTF-8")); //input text to be checked
            builder.setParameter("mode", "proof");
            builder.setParameter("preContextText", "");
            builder.setParameter("postContextText", "");
            builder.setParameter("mkt", "");
            URI uri = builder.build();

            //record query for API request
            apiRequestQuery = uri.getQuery();
            //set http request and its header
            HttpGet requestApi = new HttpGet(uri);
            requestApi.setHeader("Ocp-Apim-Subscription-Key", "f06a4e3a8905499f943e7a8d23729a14");
            //record api request time
            apiRequestTime = System.currentTimeMillis();
            //execute request to API
            HttpResponse responseApi = httpclient.execute(requestApi);
            //record api response time (with apiRequestTime to calculate API latency)
            apiResponseTime = System.currentTimeMillis();
            //get API result
            HttpEntity entity = responseApi.getEntity();
            apiResponse = EntityUtils.toString(entity);
            //OutputGenerator to transform API result to final output to be sent to the client
            //if API response is not null, process to generate output; else, return 401
            if (apiResponse != null) {
                clientResponse = classModel.generateOutput(apiResponse);
            }
            if (apiResponse == null) {
                response.setStatus(401);
                return;
            }
            //set the HTTP response code to 200 OK, with json content type
            response.setStatus(200);
            response.setContentType("application/json");
            //set request attribute to be shown in the jsp file
            request.setAttribute("result", clientResponse);
            clientResponse = "{result:\"" + clientResponse + "}"; //so it's similar with output from jsp
            //print out the response
            PrintWriter out = response.getWriter();
            out.println(clientResponse);
            //set result.jsp to be the view
            RequestDispatcher view = request.getRequestDispatcher("result.jsp");
            //forward request and response to view
            view.forward(request, response);
            //record response time
            responseTime = System.currentTimeMillis();
            //record log data to database
            MongoAccessor ma = new MongoAccessor();
            ma.mongodbConnect();
            ma.insertLog(_id, os, model, manufacture, osVersion, clientRequest, requestTime, clientResponse, responseTime, apiResponseTime - apiRequestTime);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /* Split Query to get each pair of variable and value from the URI
     * source: https://github.com/lintool/twitter-tools/blob/master/twitter-tools-core/src/main/java/cc/twittertools/corpus/data/HTMLStatusExtractor.java
     */
    public static Map<String, String> splitURLQuery(String query) throws UnsupportedEncodingException, MalformedURLException {
        Map<String, String> queryValuePairs = new LinkedHashMap<String, String>();
        String[] pairs = query.split("&"); //split each part of the query
        int pairLength = pairs.length;
        for (int i = 0; i < pairLength; i++) {
            int index = pairs[i].indexOf("="); //split variable and value, add into the Map
            queryValuePairs.put(URLDecoder.decode(pairs[i].substring(0, index), "UTF-8"), URLDecoder.decode(pairs[i].substring(index + 1), "UTF-8"));
        }
        return queryValuePairs;
    }

}
