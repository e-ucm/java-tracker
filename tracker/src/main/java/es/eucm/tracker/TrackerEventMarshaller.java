package es.eucm.tracker;

interface TrackerEventMarshaller {
	public static final String LINE_SEPARATOR = "\r\n";
	
	public String marshal(TrackerEvent event, TrackerAsset tracker);
}
