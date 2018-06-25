package network.reborn.core.Util;

public interface Scenario {

	/**
	 * Create a Scenario instance with the specified parameters
	 * 
	 * @param name
	 *            What's the official name?
	 */
	/**
	 * Register the scenario with Core Performed at server start.
	 */
	public void register();

	/**
	 * Deregister the scenario from Core Performed at server stop.
	 */
	public void deregister();

	/**
	 * Use this to perform Bukkit.getPluginManager().registerEvents(new
	 * ClassThatImplementsListener(), Core.getPlugin());
	 */
	public void initializeListeners();

	/**
	 * Is this scenario running?
	 * 
	 * @return Boolean running
	 */
	public Boolean isRunning();

	/**
	 * Update running state
	 * 
	 * @param b
	 */
	public void setRunning(Boolean b);

	public String getName();

	public String getPrefix();

}
