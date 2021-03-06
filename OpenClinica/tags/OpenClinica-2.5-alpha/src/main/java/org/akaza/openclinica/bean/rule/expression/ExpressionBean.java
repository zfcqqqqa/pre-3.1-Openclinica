/* 
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.bean.rule.expression;

import org.akaza.openclinica.bean.core.AuditableEntityBean;

/**
 * @author Krikor Krumlian
 * 
 */
public class ExpressionBean extends AuditableEntityBean {

    private Context context;
    private String value;
    private String contextName;

    public ExpressionBean() {
        // TODO Auto-generated constructor stub
    }

    public ExpressionBean(Context context, String value) {
        this.context = context;
        this.value = value;
    }
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Do not use this method for getting Context name. Use of this is strictly
     * for import validation.
     * 
     * @return String
     */
    public String getContextName() {
        return this.contextName;
    }

    /**
     * Do not use this method for setting Context name. Use of this is strictly
     * for import validation.
     * 
     * @param contextName
     */
    public void setContextName(String contextName) {
        this.contextName = contextName;
        this.context = Context.getByName(contextName);
    }
}
