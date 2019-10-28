package org.vsanyc.sandbox.couchbase.entities;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SgDocBody {

    private List<Map<String, Object>> docs = new LinkedList();
    //"new_edits": true
    private boolean newEdits;

    public List<Map<String, Object>> getDocs() {
        return docs;
    }

    public void setDocs(List<Map<String, Object>> docs) {
        this.docs = docs;
    }

    public boolean isNewEdits() {
        return newEdits;
    }

    public void setNewEdits(boolean newEdits) {
        this.newEdits = newEdits;
    }
}
