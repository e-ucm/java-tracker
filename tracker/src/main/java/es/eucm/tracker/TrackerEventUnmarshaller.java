package es.eucm.tracker;

interface TrackerEventUnmarshaller {
	
	/**
	 * 
	 * @param event
	 * @return
	 * 
	 * @throws es.eucm.tracker.exceptions.UnmarshallingException
	 */
	public TrackerEvent unmarshal(String event);
}
