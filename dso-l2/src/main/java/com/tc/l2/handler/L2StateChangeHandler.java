/*
 *
 *  The contents of this file are subject to the Terracotta Public License Version
 *  2.0 (the "License"); You may not use this file except in compliance with the
 *  License. You may obtain a copy of the License at
 *
 *  http://terracotta.org/legal/terracotta-public-license.
 *
 *  Software distributed under the License is distributed on an "AS IS" basis,
 *  WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 *  the specific language governing rights and limitations under the License.
 *
 *  The Covered Software is Terracotta Core.
 *
 *  The Initial Developer of the Covered Software is
 *  Terracotta, Inc., a Software AG company
 *
 */
package com.tc.l2.handler;

import com.tc.async.api.AbstractEventHandler;
import com.tc.async.api.ConfigurationContext;
import com.tc.async.impl.StageController;
import com.tc.l2.context.StateChangedEvent;
import com.tc.l2.state.StateManager;
import com.tc.objectserver.core.api.ServerConfigurationContext;

public class L2StateChangeHandler extends AbstractEventHandler<StateChangedEvent> {

  private StateManager stateManager;
  private final StageController stageManager;
  private ConfigurationContext context;

  public L2StateChangeHandler(StageController stageManager) {
    this.stageManager = stageManager;
  }

  @Override
  public void handleEvent(StateChangedEvent sce) {
// execute state transition before notifying any listeners.  Listener notification 
// can happen in any order.  State transition happens in specfic order as dictated 
// by the stage controller.
    stageManager.transition(context, sce.getOldState(), sce.getCurrentState());
    stateManager.fireStateChangedEvent(sce);
  }

  @Override
  public void initialize(ConfigurationContext context) {
    super.initialize(context);
    ServerConfigurationContext oscc = (ServerConfigurationContext) context;
    this.stateManager = oscc.getL2Coordinator().getStateManager();
    this.context = context;
  }

}
