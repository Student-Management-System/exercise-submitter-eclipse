package net.ssehub.teaching.exercise_submitter.eclipse.config;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;


import net.ssehub.teaching.exercise_submitter.eclipse.Activator;

public class PrefConfig {
	
	//private ISecurePreferences secPrefs;
	
	private IEclipsePreferences prefs;
	
	
		public PrefConfig() {
			prefs = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
			
		}
		
		public String getUsername() {
			return null;
		}
		public String getPassword() {
			return null;
		}
		public void setUsername(String username) {
			
		}
		public void setPassword(String username) {
			
		}
	

}
