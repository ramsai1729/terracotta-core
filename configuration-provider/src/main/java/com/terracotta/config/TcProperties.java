/*
 * The contents of this file are subject to the Terracotta Public License Version
 * 2.0 (the "License"); You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://terracotta.org/legal/terracotta-public-license.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Covered Software is Terracotta Configuration.
 *
 * The Initial Developer of the Covered Software is
 * Terracotta, Inc., a Software AG company
 *
 */

package com.terracotta.config;

import java.util.ArrayList;
import java.util.List;

public class TcProperties {
  private List<Property> property;

  public List<Property> getProperty() {
    if (property == null) {
      property = new ArrayList<>();
    }
    return this.property;
  }

  public void setProperty(List<Property> property) {
    this.property = property;
  }
}
