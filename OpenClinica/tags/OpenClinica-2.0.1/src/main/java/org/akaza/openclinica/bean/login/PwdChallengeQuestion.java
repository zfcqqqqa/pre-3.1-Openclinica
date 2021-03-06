/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.login;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import org.akaza.openclinica.bean.core.Term;

/**
 * @author jxu
 * @version CVS: $Id: PwdChallengeQuestion.java 4554 2005-02-21 17:38:09Z jxu $
 *  
 */
public class PwdChallengeQuestion extends Term {
  public static final PwdChallengeQuestion MAIDEN_NAME = new PwdChallengeQuestion(1,
      "Mother's Maiden Name");

  public static final PwdChallengeQuestion FARORITE_PET = new PwdChallengeQuestion(2,
      "Favorite Pet");

  public static final PwdChallengeQuestion CITY_OF_BIRTH = new PwdChallengeQuestion(3,
      "City of Birth");

  private static final PwdChallengeQuestion[] members = { MAIDEN_NAME, FARORITE_PET, CITY_OF_BIRTH };

  public static final List list = Arrays.asList(members);

  private PwdChallengeQuestion(int id, String name) {
    super(id, name);
  }

  private PwdChallengeQuestion() {
  }

  public static boolean contains(int id) {
    return Term.contains(id, list);
  }

  public static PwdChallengeQuestion get(int id) {
    return (PwdChallengeQuestion) Term.get(id, list);
  }
  
  public static ArrayList toArrayList(){
    return new ArrayList(list);
  }

}