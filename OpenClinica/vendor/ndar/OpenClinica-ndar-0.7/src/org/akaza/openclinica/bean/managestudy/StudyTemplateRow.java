/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.managestudy;

import java.util.ArrayList;

import org.akaza.openclinica.core.EntityBeanRow;

/**
 * @author jxu
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StudyTemplateRow extends EntityBeanRow {
//columns:
	public static final int COL_NAME = 0;
	public static final int COL_TYPE = 1;
	public static final int COL_SUBJECT_ASSIGNMENT = 2;
	public static final int COL_STATUS = 3;
	
	/* (non-Javadoc)
	 * @see org.akaza.openclinica.core.EntityBeanRow#compareColumn(java.lang.Object, int)
	 */
	protected int compareColumn(Object row, int sortingColumn) {
		if (!row.getClass().equals(StudyTemplateRow.class)) {
			return 0;
		}
		
		StudyTemplateBean thisStudy = (StudyTemplateBean) bean; 
		StudyTemplateBean argStudy = (StudyTemplateBean) ((StudyTemplateRow)row).bean; 

		int answer = 0;
		switch (sortingColumn) {
			case COL_NAME:
				answer = thisStudy.getName().toLowerCase().compareTo(argStudy.getName().toLowerCase());
				break;				
			case COL_TYPE:
				answer = thisStudy.getStudyTemplateTypeName().toLowerCase().compareTo(argStudy.getStudyTemplateTypeName().toLowerCase());
				break;
			case COL_SUBJECT_ASSIGNMENT:
			    answer = thisStudy.getSubjectAssignment().toLowerCase().compareTo(argStudy.getSubjectAssignment().toLowerCase());
				break;
			case COL_STATUS:
				answer = thisStudy.getStatus().compareTo(argStudy.getStatus());
				break;
		}
		
		return answer;
	}

	public String getSearchString() {
		StudyTemplateBean thisStudy = (StudyTemplateBean) bean; 
		return thisStudy.getName() + " " + thisStudy.getStudyTemplateTypeName() + " " + 
		thisStudy.getSubjectAssignment(); 
	}
	
	
	/* (non-Javadoc)
	 * @see org.akaza.openclinica.core.EntityBeanRow#generatRowsFromBeans(java.util.ArrayList)
	 */
	public ArrayList generatRowsFromBeans(ArrayList beans) {
		return StudyTemplateRow.generateRowsFromBeans(beans);
	}
	
	public static ArrayList generateRowsFromBeans(ArrayList beans) {
		ArrayList answer = new ArrayList();
		
		Class[] parameters = null;
		Object[] arguments = null;
		
		for (int i = 0; i < beans.size(); i++) {
			try {
				StudyTemplateRow row = new StudyTemplateRow();
				row.setBean((StudyTemplateBean) beans.get(i));
				answer.add(row);
			} catch (Exception e) { }
		}
		
		return answer;
	}


}
