package lco.bookmarks.batch.reader;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.json.JsonObjectReader;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

public class ChromeJsonObjectReader<T> implements JsonObjectReader<T> {

    Logger logger = LoggerFactory.getLogger(ChromeJsonObjectReader.class);

    private static final String FOLDER = "folder";

    private static final String NODE_ADDED = "Node {} added";

    ObjectMapper mapper = new ObjectMapper();
    private JsonParser jsonParser;
    private InputStream inputStream;
    private final Class<T> targetType;
    private final String targetPath;

    public ChromeJsonObjectReader(Class<T> targetType, String targetPath) {
        super();
        this.targetType = targetType;
        this.targetPath = targetPath;
    }

    public JsonParser getJsonParser() {
        return jsonParser;
    }

    public void setJsonParser(JsonParser jsonParser) {
        this.jsonParser = jsonParser;
    }


    /*
     * JsonObjectReader interface has an empty default method and must be implemented in this case to set
     * the mapper and the parser
     */
    @Override
    public void open(Resource resource) throws IOException {
        logger.info("Opening json object reader");
        this.inputStream = resource.getInputStream();
        JsonNode jsonNode = this.mapper.readTree(this.inputStream).findPath(targetPath);
        this.jsonParser = startArrayParser(jsonNode);
        logger.info("Reader open with parser reference: {}", this.jsonParser);
    }

    @Override
    public T read() throws IOException {
        try {
            if (this.jsonParser.nextToken() == JsonToken.START_OBJECT) {
                T result = this.mapper.readValue(this.jsonParser, this.targetType);
                logger.info("Object read: {}", result);
                return result;
            } else {
                logger.info("All objects read, end of file");
            }
        } catch (IOException e) {
            throw new IOException("Unable to read next JSON object", e);
        }
        return null;
    }

    /**
     * Creates a new parser from an array node
     */
    private JsonParser startArrayParser(JsonNode jsonNode) throws IOException {

        ArrayNode newArray = this.mapper.createArrayNode();

        if (jsonNode.isArray()) {

            ArrayNode arrayField = (ArrayNode) jsonNode;
            arrayField.forEach(node -> {
                if (node.get("type").textValue().equals(FOLDER)) {
                    JsonNode bookmarkFolder = node.get("name");
                    logger.info("Processing bookmark folder {}", bookmarkFolder.textValue());
                    JsonNode nodeChild = node.get("children");
                    if (!nodeChild.isMissingNode() && nodeChild.isArray()) {
                        ArrayNode arrayChild = (ArrayNode) nodeChild;
                        recurseOnChildrenNodes(arrayChild, newArray, bookmarkFolder);
                    } else {
                        logger.info("No children node");
                    }
                } else if (node.get("type").textValue().equals("url")) {
                    newArray.add(node);
                    logger.debug(NODE_ADDED, node.get("name").textValue());
                }

            });
        } else {
            logger.error("Node is not array");
        }

        ObjectMapper localMapper = new ObjectMapper();
        return localMapper.getFactory().createParser(newArray.toString());

    }

    private void recurseOnChildrenNodes(ArrayNode arrayChild, ArrayNode newArray, JsonNode bookmarkFolder) {
        arrayChild.forEach(nC -> {
            if (nC.get("type").textValue().equals(FOLDER)) {
                JsonNode bookmarkSubFolder = nC.get("name");
                logger.info("Processing bookmark folder {}", bookmarkSubFolder.textValue());
                JsonNode nodeSubChild = nC.findPath("children");
                if (!nodeSubChild.isMissingNode() && nodeSubChild.isArray()) {
                    ArrayNode arraySubChild = (ArrayNode) nodeSubChild;
                    arraySubChild.forEach(nSc -> {
                        if (nSc.get("type").textValue().equals("url")) {
                            ObjectNode newNode = (ObjectNode) nSc;
                            newNode.set(FOLDER, bookmarkSubFolder);
                            newArray.add(newNode);
                            logger.debug(NODE_ADDED, newNode.get("name").textValue());
                        }
                    });
                } else {
                    logger.info("No children node");
                }
            } else if (nC.get("type").textValue().equals("url")) {
                ObjectNode newNode = (ObjectNode) nC;
                newNode.set(FOLDER, bookmarkFolder);
                newArray.add(newNode);
                logger.debug(NODE_ADDED, newNode.get("name").textValue());
            }
        });
    }

    @Override
    public void close() throws Exception {
        this.inputStream.close();
        this.jsonParser.close();
    }
}
