
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import org.json.JSONObject;

/**
 * MongoAccessor for connecting to mongodb in mlab, inserting and querying data
 * @author nurlailifajriyah
 */
public class MongoAccessor {

    DBCollection collection;
    public void mongodbConnect() {
        //change mlab password to char array
        char[] pass = "51NXO1SdmlGn".toCharArray();
        //http://mongodb.github.io/mongo-java-driver/3.0/driver/reference/connecting/authenticating/
        //create credential to connect to database instance in mlab
        MongoCredential credential = MongoCredential.createCredential("nfajriya", "project4task2", pass);
        //create client connection to mlab with the credential
        MongoClient mongoClient = new MongoClient(new ServerAddress("ds161210.mlab.com", 61210), Arrays.asList(credential));
        //get the specific database on mlab
        DB db = mongoClient.getDB("project4task2");
        //get the specific collection in that database
        collection = db.getCollection("CheckSpellingLog");
    }
    //inserLog to application log everytime when there is a request from android client
    //http://www.mkyong.com/mongodb/java-mongodb-insert-a-document/
    public void insertLog(long id, String os, String model, String manufacture, String osVersion,
            String input, long requestTime, String output, long responseTime, long apiLatency) {
        try {
            //create DB Object and put all log data into that object/document
            BasicDBObject document = new BasicDBObject();
            document.put("_id", id);
            document.put("os", os);
            document.put("model", model);
            document.put("manufacture", manufacture);
            document.put("osversion", osVersion);
            document.put("input", input);
            document.put("requesttime", requestTime);
            document.put("output", output);
            document.put("responsetime", responseTime);
            document.put("apilatency", apiLatency);
            //insert the document into collection
            collection.insert(document);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    //get log to retrieve all data from CheckSpellingLog collection
    public ArrayList<JSONObject> getLog() {
        ArrayList<JSONObject> result = new ArrayList<>();
        //find all documents in collection, assign to DBCursor
        DBCursor cursor = collection.find();
        //for all documents
        while (cursor.hasNext()) {
            //assign each document as JSONObject and add it to Arraylist
            JSONObject obj = new JSONObject(cursor.next().toString());
            result.add(obj);
        }
        return result;
    }
    //getTopOsVersion to retrieve top 5 client's OS version 
    public ArrayList<JSONObject> getTopOsVersion() {
        ArrayList<JSONObject> result = new ArrayList<>();
        //http://pingax.com/trick-convert-mongo-shell-query-equivalent-java-objects/
        //grouping based on osversion, and count each osversion
        DBObject group = new BasicDBObject("$group", new BasicDBObject("_id", "$osversion")
                .append("count", new BasicDBObject("$sum", 1)));
        //sorting based on count
        DBObject sort = new BasicDBObject("$sort", new BasicDBObject("count", -1));
        //limit to 5 top osversion
        DBObject limit = new BasicDBObject("$limit", 5);
        //create aggregation output from group, sort, and limit
        AggregationOutput output = collection.aggregate(group, sort, limit);
        //http://stackoverflow.com/questions/31110508/converting-aggregationoutput-to-jsonobject
        //for each row in output, get _id and count, put to JSONObject and add to ArrayList<JSONObject>
        for (DBObject res : output.results()) {
            JSONObject jsonobj = new JSONObject();
            jsonobj.put("_id", res.get("_id"));
            jsonobj.put("count", res.get("count"));
            result.add(jsonobj);
        }
        return result;
    }
    //getTopOsVersion to retrieve top 5 client's device manufacture and type
    public ArrayList<JSONObject> getTopDeviceModel() {
        ArrayList<JSONObject> result = new ArrayList<>();
        //http://pingax.com/trick-convert-mongo-shell-query-equivalent-java-objects/
        //grouping based on manufacture and model, and count for each
        DBObject group = new BasicDBObject("$group", new BasicDBObject("_id", new BasicDBObject("model", "$model")
                .append("manufacture", "$manufacture"))
                .append("count", new BasicDBObject("$sum", 1)));
        //sorting based on count
        DBObject sort = new BasicDBObject("$sort", new BasicDBObject("count", -1));
        //limit to 5 top osversion
        DBObject limit = new BasicDBObject("$limit", 5);
        //create aggregation output from group, sort, and limit
        AggregationOutput output = collection.aggregate(group, sort, limit);
        //http://stackoverflow.com/questions/31110508/converting-aggregationoutput-to-jsonobject
        //for each row in output, get _id, manufacture, model, and count, put to JSONObject and add to ArrayList<JSONObject>
        for (DBObject res : output.results()) {
            JSONObject jsonobj = new JSONObject();
            JSONObject jsonobj2 = new JSONObject(res.get("_id").toString());
            jsonobj.put("model", jsonobj2.isNull("model") ? "(unknown)" : jsonobj2.getString("model"));
            jsonobj.put("manufacture", jsonobj2.isNull("manufacture") ? "(unknown)" : jsonobj2.getString("manufacture"));
            jsonobj.put("count", res.get("count"));
            result.add(jsonobj);
        }
        return result;
    }
    //getProcessingTime to retrieve top 5 slowest processing time, or response minus request time
    public ArrayList<JSONObject> getProcessingTime() {
        ArrayList<JSONObject> result = new ArrayList<>();
        //find all documents in collection, assign to DBCursor
        DBCursor cursor = collection.find();
        //for all documents
        while (cursor.hasNext()) {
            DBObject doc = cursor.next();
            JSONObject jsonobj = new JSONObject();
            //get request and response time from each document
            long resTime = Long.parseLong(doc.get("responsetime").toString());
            long reqTime = Long.parseLong(doc.get("requesttime").toString());
            //calculate processing time
            long processingTime = resTime - reqTime;
            //put id and processing time to JSON object
            jsonobj.put("processingtime", processingTime + "");
            jsonobj.put("id", doc.get("_id").toString());
            //add JSON object to arraylist
            result.add(jsonobj);
        }
        //Anonymous class to sort JSONObject based on processing time
        Collections.sort(result, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                return (int) ((o2.getLong("processingtime")) - (o1.getLong("processingtime")));
            }
        });
        return result;
    }
    //getTopLatency to retrieve top 5 highest latency
    public ArrayList<JSONObject> getTopLatency() {
        ArrayList<JSONObject> result = new ArrayList<>();
        //find all documents in collection, sort by latency, limit 5, assign to DBCursor
        DBCursor cursor = collection.find().sort(new BasicDBObject("apilatency", -1)).limit(5);
        //for all documents in cursor
        while (cursor.hasNext()) {
            DBObject doc = cursor.next();
            JSONObject jsonobj = new JSONObject();
            //get _id and apilatency
            String id = doc.get("_id").toString();
            String apilatency = doc.get("apilatency").toString();
            //put id and latency to the JSON object
            jsonobj.put("id", id);
            jsonobj.put("apilatency", apilatency);
            //put JSONObject to the array list
            result.add(jsonobj);
        }
        return result;
    }
}
