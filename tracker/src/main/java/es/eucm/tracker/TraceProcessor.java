package es.eucm.tracker;

/**
 * An object that can process traces
 */
public interface TraceProcessor {
	/**
	 * Adds a trace to the queue.
	 * @param trace to add
	 */
	void process(TrackerEvent trace);
	/**
	 * Allows progress to be set
	 */
	void setProgress(float progress);
}
