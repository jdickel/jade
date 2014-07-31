package com.sos.jadevaadincockpit.view.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.sos.JSHelper.Options.SOSOptionElement;
import com.sos.jadevaadincockpit.JadevaadincockpitUI;
import com.sos.jadevaadincockpit.adapters.JadeVaadinAdapter;
import com.sos.jadevaadincockpit.globals.Globals;
import com.sos.jadevaadincockpit.view.AddProfileWindow;
import com.sos.jadevaadincockpit.view.ProfileTree;
import com.sos.jadevaadincockpit.viewmodel.ProfileContainer;
import com.vaadin.data.Item;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

/**
 * 
 * @author JS
 *
 */
public class ProfileTreeActionHandler implements Handler {
	private static final long serialVersionUID = -1010544471098644643L;

	private static final Action SAVE_FILE = new Action("Save");
	private static final Action CLOSE_FILE = new Action("Close");
	private static final Action ADD_PROFILE = new Action("Add new profile");
	private static final Action RENAME_FILE = new Action("Rename file");
	
    private static final Action DELETE_PROFILE = new Action("Delete profile");
    private static final Action RENAME_PROFILE = new Action("Rename profile");
    private static final Action EXECUTE_PROFILE = new Action("Execute profile");
    
    private static final Action DEBUG_PRINT_OPTIONS = new Action("DEBUG: print dirty options");
    private static final Action DEBUG_PRINT_MISSING_OPTIONS = new Action("DEBUG: print missig options");
    
    private static final Action[] settingsFileActions = new Action[] { SAVE_FILE, CLOSE_FILE, ADD_PROFILE, RENAME_FILE };
    private static final Action[] profileActions = new Action[] { EXECUTE_PROFILE, RENAME_PROFILE, DELETE_PROFILE };
    
    private static final Action[] debugActions = new Action[] { DEBUG_PRINT_OPTIONS, DEBUG_PRINT_MISSING_OPTIONS };

	@Override
	public Action[] getActions(Object target, Object sender) {
		
		List<Action> actionsList = new ArrayList<Action>();
		
		if (target != null) {
			
			ProfileTree profileTree = JadevaadincockpitUI.getCurrent().getJadeMainUi().getProfileTree();
			
			ProfileContainer profileContainer = profileTree.getProfileContainer();
			
			if (profileContainer.isRoot(target)) { // item which received the right click is a settings file
				
				for (Action a : settingsFileActions) {
					
					actionsList.add(a);
				}
			} else { // item which received the right click is a profile
				
				for (Action a : profileActions) {
					
					actionsList.add(a);
				}
			}
		}
		
		for (Action a : debugActions) {
			
			actionsList.add(a);
		}
		
		Action[] actions = actionsList.toArray(new Action[actionsList.size()]);
		
		return actions;
	}

	@Override
	public void handleAction(Action action, Object sender, Object target) {
		
		ProfileTree profileTree = JadevaadincockpitUI.getCurrent().getJadeMainUi().getProfileTree();
		ProfileContainer profileContainer = profileTree.getProfileContainer();
		
		if (profileContainer.isRoot(target)) { // item which received the right click is a settings file; check settingsFileActions
			
			if (action == SAVE_FILE) {
				
				Globals.getJadeDataProvider().saveSettingsFile(target);
				
			} else if (action == CLOSE_FILE) {
				
				Globals.getJadeDataProvider().closeSettingsFile(target);
				
			} else if (action == ADD_PROFILE) {
				
				AddProfileWindow window = new AddProfileWindow();
				UI.getCurrent().addWindow(window);

			} else if (action == RENAME_FILE) {
				
				Notification.show("Sorry, not available yet.");
			}
		} else { // item which received the right click is a profile; check profileActions
			
			if (action == EXECUTE_PROFILE) {
				
				// TODO profile has to be saved before execution as checkMandatory() will read from the file (not from the options-Object)
				Globals.getJadeDataProvider().saveSettingsFile(target);
				
				JadeVaadinAdapter jadeAdapter = new JadeVaadinAdapter();
				jadeAdapter.execute(profileTree.getItem(target));

			} else if (action == RENAME_PROFILE) {
				
				Notification.show("Sorry, not available yet.");
				
			} else if (action == DELETE_PROFILE) {
				
				profileContainer.removeItemRecursively(target);
				
			}
		}
		
		if (action == DEBUG_PRINT_OPTIONS) { // TODO
			
			System.out.println("DIRTY OPTIONS");
			Item targetItem = profileTree.getItem(target);
			@SuppressWarnings("unchecked")
			HashMap<String, SOSOptionElement> options = (HashMap<String, SOSOptionElement>) targetItem.getItemProperty(ProfileContainer.PROPERTY.OPTIONS).getValue();
			if (options != null) {
				Iterator<String> optionsIterator = options.keySet().iterator();
				while (optionsIterator.hasNext()) {
					String optionName = optionsIterator.next();
					SOSOptionElement option = options.get(optionName);
					String optionValue = option.Value();
					if (option.isDirty()) {
						System.out.println(optionName + " = " + optionValue);					
					}
				}
			} else {
				System.out.println("options is null");
			}
			
		} else if (action == DEBUG_PRINT_MISSING_OPTIONS) {
			for (String s : Globals.missingOptions.keySet()) {
				System.out.println("MISSING OPTION:          " + s + " = " + Globals.missingOptions.get(s));
			}
		}
	}

}