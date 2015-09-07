package de.httc.channels.api;

import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;
 
public class ChannelController {
	private String id;
    private ChannelsDatabase channelsDatabase;
    private Comment model;
 
    // Handles /channel/{id} GET requests
    public HttpHeaders show() {
        return new DefaultHttpHeaders("show")
            //.withETag(model.getUniqueStamp())
            .lastModified(model.getLastModified());
    }
 
    // Handles /channel/{id} PUT requests
    public String update() {
    	channelsDatabase.update(model);
        return "update";
    }
    
    public void setChannelsDatabase(ChannelsDatabase channelsDatabase) {
    	this.channelsDatabase = channelsDatabase;
    }
 
    public void setId(String id) {
    	try {
			System.out.println("--- set id: " + id);
			this.id = id;
			model = channelsDatabase.findComment(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}