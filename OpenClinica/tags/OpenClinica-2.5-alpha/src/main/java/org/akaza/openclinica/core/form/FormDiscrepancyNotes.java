/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.core.form;

import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author jxu
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class FormDiscrepancyNotes {
    private HashMap fieldNotes;
    private HashMap numExistingFieldNotes;

    public FormDiscrepancyNotes() {
        fieldNotes = new HashMap();
        numExistingFieldNotes = new HashMap();
    }

    public void addNote(String field, DiscrepancyNoteBean note) {
        ArrayList notes;
        if (fieldNotes.containsKey(field)) {
            notes = (ArrayList) fieldNotes.get(field);
        } else {
            notes = new ArrayList();
        }

        notes.add(note);
        fieldNotes.put(field, notes);
    }

    public boolean hasNote(String field) {
        ArrayList notes;
        if (fieldNotes.containsKey(field)) {
            notes = (ArrayList) fieldNotes.get(field);
            return notes != null && notes.size() > 0;
        }
        return false;
    }

    public ArrayList getNotes(String field) {
        ArrayList notes;
        if (fieldNotes.containsKey(field)) {
            notes = (ArrayList) fieldNotes.get(field);
        } else {
            notes = new ArrayList();
        }
        return notes;
    }

    public void setNumExistingFieldNotes(String field, int num) {
        numExistingFieldNotes.put(field, new Integer(num));
    }

    public int getNumExistingFieldNotes(String field) {
        if (numExistingFieldNotes.containsKey(field)) {
            Integer numInt = (Integer) numExistingFieldNotes.get(field);
            if (numInt != null) {
                return numInt.intValue();
            }
        }
        return 0;
    }

    /**
     * @return Returns the numExistingFieldNotes.
     */
    public HashMap getNumExistingFieldNotes() {
        return numExistingFieldNotes;
    }
}