
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * OutputGenerator to transform API result to final output to be sent to the client
 * @author nurlailifajriyah
 */
public class Project4Task2Model {
    String input;
    public String generateOutput(String apiResult) {
        //assign string from api result to JSONObject
        JSONObject obj = new JSONObject(apiResult);
        
        //api result example would be like this: 
        //{ "_type": "SpellCheck", "flaggedTokens": [ { "offset": 5, "token": "Gatas", "type": "UnknownToken", "suggestions": [ { "suggestion": "Gates", "score": 1 } ] } ] }
        
        //create JSONArray based on flaggedTokens
        JSONArray arr = obj.getJSONArray("flaggedTokens");
        for (int i = 0; i < arr.length(); i++) { 
            //from flaggedTokens, get token and suggestions
            String token = ((JSONObject) arr.get(i)).getString("token");
            JSONArray suggestions = ((JSONObject) arr.get(i)).getJSONArray("suggestions");
            //from suggestions, get suggestion
            String suggestion = ((JSONObject)suggestions.get(0)).getString("suggestion");
            //add (suggestion) right after the wrong spelling words
            input = input.replace(" "+ token + " ", " "+token + "("+suggestion+") ");
        }
        //return the result
        return input.trim();
    }
}
