package es.eucm.tracker;

interface TrackerEventMarshaller {
	public String marshal(TrackerEvent event, TrackerAsset tracker);
}
