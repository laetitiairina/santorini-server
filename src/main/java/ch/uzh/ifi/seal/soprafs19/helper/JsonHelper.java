package ch.uzh.ifi.seal.soprafs19.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JsonHelper {

    /**
     * Transform object to JsonNode
     * @param obj
     * @return
     */
    public JsonNode objectToJsonNode(Object obj) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.valueToTree(obj);
    }

    /**
     * Get filtered object as JsonNode
     * @param obj
     * @param fields
     * @return
     * @throws JsonProcessingException
     */
    public JsonNode getFilteredObjectAsJsonNode(Object obj, List<String> fields) {

        ObjectMapper objectMapper = new ObjectMapper();

        // Transform obj to json node
        JsonNode objNode = objectMapper.valueToTree(obj);

        // Create empty json node
        JsonNode filteredObjNode = objectMapper.createObjectNode();

        // Add fields to filtered node
        for (String param : fields) {
            ((ObjectNode) filteredObjNode).set(param,objNode.get(param));
        }

        // Transform filtered node to player object and return it
        //return objectMapper.treeToValue(filteredObjNode,Player.class);

        return filteredObjNode;
    }
}
