package de.bwaldvogel.mongo.backend.projection;

import java.util.Map.Entry;

import de.bwaldvogel.mongo.backend.Utils;
import de.bwaldvogel.mongo.bson.Document;

public class Projection {

    public static Document projectDocument(Document document, Document fields, String idField) {

        if (document == null) {
            return null;
        }

        Document newDocument = new Document();

        // implicitly add _id if not mentioned
        // http://docs.mongodb.org/manual/tutorial/project-fields-from-query-results/#return-the-specified-fields-and-the-id-field-only
        if (!fields.containsKey(idField)) {
            newDocument.put(idField, document.get(idField));
        }

        if (onlyExclusions(fields)) {
            newDocument.putAll(document);
            for (String excludedField : fields.keySet()) {
                newDocument.remove(excludedField);
            }
        } else {
            for (Entry<String, Object> entry : fields.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (Utils.isTrue(value)) {
                    projectField(document, newDocument, key);
                }
            }
        }

        return newDocument;
    }

    private static boolean onlyExclusions(Document fields) {
        for (String key : fields.keySet()) {
            if (Utils.isTrue(fields.get(key))) {
                return false;
            }
        }
        return true;
    }

    private static void projectField(Document document, Document newDocument, String key) {
        int dotPos = key.indexOf('.');
        if (dotPos > 0) {
            String mainKey = key.substring(0, dotPos);
            String subKey = key.substring(dotPos + 1);

            Object object = document.get(mainKey);
            // do not project the subdocument if it is not of type Document
            if (object instanceof Document) {
                Document subDocument = (Document) newDocument.computeIfAbsent(mainKey, k -> new Document());
                projectField((Document) object, subDocument, subKey);
            }
        } else {
            newDocument.put(key, document.get(key));
        }
    }
}